package com.oxygen.weather.ui

import android.app.UiAutomation
import android.app.Instrumentation
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ContentValues
import android.content.Intent
import android.hardware.display.DisplayManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.Rect
import android.os.SystemClock
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.InputDevice
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityWindowInfo
import android.provider.MediaStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.SharedPreferencesUnitPresetStore
import com.oxygen.weather.application.UnitPresetSelection
import com.oxygen.weather.data.DemoWeatherRepository
import com.oxygen.weather.derived.HistoricalSynthesis
import com.oxygen.weather.presentation.HomePresentationMapper
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/** Unscored device-side feasibility probe for the SQ.10 semantic interaction endpoint. */
@RunWith(AndroidJUnit4::class)
class StaticInteractionObserverFeasibilityTest {
    private val instrumentation: Instrumentation = InstrumentationRegistry.getInstrumentation()
    private val automation: UiAutomation = instrumentation.uiAutomation
    private val targetPackage = instrumentation.targetContext.packageName
    private val timeoutMs = 2_000L
    private val display = Point()
    private val rawEvents = mutableListOf<JSONObject>()
    private val actionResults = mutableListOf<JSONObject>()
    private val diagnostics = mutableListOf<JSONObject>()
    private val diagnosticTrials = JSONArray()
    private var lastAppearances = JSONArray()
    private lateinit var outputFile: File
    private var outputUri: android.net.Uri? = null
    @Volatile private var pending: Pending? = null
    @Volatile private var activeDiagnostic: DiagnosticCapture? = null

    private data class VisibleNode(
        val windowId: Int,
        val layer: Int?,
        val text: String?,
        val description: String?,
        val bounds: Rect,
        val clickable: Boolean,
        val enabled: Boolean,
        val className: String?,
        val scrollableAncestors: List<Rect> = emptyList(),
    )

    private data class WindowSnapshot(
        val id: Int,
        val layer: Int,
        val packageName: String?,
        val active: Boolean,
        val focused: Boolean,
        val text: List<VisibleNode>,
        val fromFallback: Boolean = false,
        val bounds: Rect? = null,
    )

    private data class SafePoint(
        val window: WindowSnapshot,
        val target: VisibleNode,
        val point: Point,
        val targetBounds: Rect,
        val containerBounds: Rect,
        val appBounds: Rect,
        val displayBounds: Rect,
        val reason: String? = null,
    )

    private class Pending(
        val id: String,
        val expected: (List<WindowSnapshot>) -> VisibleNode?,
    ) {
        val done = CountDownLatch(1)
        @Volatile var startNanos: Long = 0
        @Volatile var inputDownUptimeMs: Long = 0
        @Volatile var confirmation: JSONObject? = null
        @Volatile var timedOutAtNanos: Long? = null
        val inputTraces = JSONArray()
    }

    private class DiagnosticCapture(val trial: Int) {
        val events = JSONArray()
        val windowSamples = JSONArray()
        val screenshots = JSONArray()
    }

    private data class EventFacts(
        val packageName: String?, val type: Int, val eventUptimeMs: Long,
        val callbackNanos: Long, val queryNanos: Long, val windowId: Int,
    )

    private fun safePoint(
        windows: List<WindowSnapshot>, packageName: String, label: String,
        matches: (VisibleNode) -> Boolean, requireScrollable: Boolean, size: Point,
    ): SafePoint {
        val active = windows.filter { it.packageName == packageName && it.active && it.focused && !it.fromFallback }
        if (active.size != 1) return SafePoint(active.firstOrNull() ?: WindowSnapshot(-1, -1, null, false, false, emptyList()),
            VisibleNode(-1, null, null, null, Rect(), false, false, null), Point(), Rect(), Rect(), Rect(), Rect(), "NO_ACTIVE_FOCUSED_APP_WINDOW")
        val window = active.single()
        if (window.bounds == null || window.bounds.isEmpty) return SafePoint(window,
            VisibleNode(window.id, window.layer, null, null, Rect(), false, false, null), Point(), Rect(), Rect(), Rect(), Rect(), "MISSING_APP_WINDOW_BOUNDS")
        val targets = window.text.filter(matches)
        if (targets.isEmpty()) return SafePoint(window,
            VisibleNode(window.id, window.layer, null, null, Rect(), false, false, null), Point(), Rect(), Rect(), Rect(), Rect(), "NO_TARGET")
        if (targets.size != 1) return SafePoint(window, targets.first(), Point(), targets.first().bounds,
            Rect(), window.bounds, Rect(0, 0, size.x, size.y), "AMBIGUOUS_TARGET")
        val target = targets.single()
        if (!target.enabled) return SafePoint(window, target, Point(), target.bounds, Rect(), window.bounds,
            Rect(0, 0, size.x, size.y), "TARGET_DISABLED")
        val container = target.scrollableAncestors.lastOrNull()
        if (requireScrollable && container == null) return SafePoint(window, target, Point(), target.bounds, Rect(), window.bounds,
            Rect(0, 0, size.x, size.y), "MISSING_SCROLL_CONTAINER")
        val actualContainer = container ?: window.bounds
        val displayBounds = Rect(0, 0, size.x, size.y)
        val intersection = Rect(target.bounds)
        if (!intersection.intersect(actualContainer) || !intersection.intersect(window.bounds) || !intersection.intersect(displayBounds))
            return SafePoint(window, target, Point(), target.bounds, actualContainer, window.bounds, displayBounds, "EMPTY_INTERSECTION")
        val point = Point(intersection.centerX(), intersection.centerY())
        val higherLayers = windows.filter { it.id != window.id && it.layer > window.layer }
        if (higherLayers.any { it.bounds == null }) return SafePoint(window, target, point, target.bounds,
            actualContainer, window.bounds, displayBounds, "MISSING_HIGHER_LAYER_BOUNDS")
        val occluded = higherLayers.any { other -> other.bounds?.contains(point.x, point.y) == true ||
            other.text.any { it.bounds.contains(point.x, point.y) } }
        return SafePoint(window, target, point, target.bounds, actualContainer, window.bounds, displayBounds,
            if (occluded) "OCCLUDED" else null)
    }

    private fun classifyOutcome(injectionAttempted: Boolean, injectionAccepted: Boolean,
                                state: String?, eventConfirmed: Boolean): String = when {
        !injectionAttempted && state == "UNSAFE" -> "NO_INPUT_UNSAFE"
        !injectionAttempted -> "NO_INPUT_PRECONDITION"
        !injectionAccepted -> "INJECTION_INCOMPLETE"
        state == "STATE_UNREADABLE" -> "INJECTION_ACCEPTED + STATE_UNREADABLE"
        state == "WRONG_STATE" -> "INJECTION_ACCEPTED + WRONG_STATE"
        state == "NO_STATE_CHANGE" -> "INJECTION_ACCEPTED + NO_STATE_CHANGE"
        state == "VISIBLE_LATE" -> "INJECTION_ACCEPTED + VISIBLE_LATE"
        state == "EXPECTED" && eventConfirmed -> "INJECTION_ACCEPTED + EXPECTED_STATE_CONFIRMED"
        state == "EXPECTED" -> "INJECTION_ACCEPTED + VISIBLE_NO_ELIGIBLE_EVENT"
        else -> "INJECTION_ACCEPTED + STATE_UNREADABLE"
    }

    private fun classifyRenderedState(expected: Boolean, partial: Boolean, readable: Boolean, wrong: Boolean): String = when {
        expected -> "EXPECTED_WINDOW_VISIBLE"
        partial -> "PARTIAL_VISIBLE"
        wrong -> "WRONG_STATE"
        readable -> "WRONG_STATE"
        else -> "STATE_UNREADABLE"
    }

    private fun classifyEventState(confirmed: Boolean, late: Boolean, wrongDestination: Boolean): String = when {
        confirmed -> "ELIGIBLE_WITHIN_TIMEOUT"
        wrongDestination -> "WRONG_DESTINATION"
        late -> "LATE"
        else -> "NO_ELIGIBLE_EVENT"
    }

    private fun settingsActionState(action: String, windows: List<WindowSnapshot>, packageName: String): Boolean {
        val app = windows.singleOrNull { it.packageName == packageName && it.active && it.focused } ?: return false
        return when (action) {
            "menu" -> app.text.count { it.description == "Settings" && it.enabled } == 1
            "settings" -> app.text.count { it.text == "Settings" } == 1
            "home" -> app.text.count { it.description == "Choose Home page, current: Now" } == 1
            else -> false
        }
    }

    private fun eligibility(
        event: EventFacts, inputStartNanos: Long, inputDownUptimeMs: Long,
        windows: List<WindowSnapshot>, matched: VisibleNode?,
    ): Pair<String, VisibleNode?> {
        if (inputStartNanos <= 0 || inputDownUptimeMs <= 0) return "UNKNOWN_INPUT_TIME" to null
        if (event.eventUptimeMs < inputDownUptimeMs || event.callbackNanos < inputStartNanos)
            return "STALE_EVENT" to null
        if (event.queryNanos > inputStartNanos + TimeUnit.MILLISECONDS.toNanos(timeoutMs))
            return "AFTER_DEADLINE" to null
        if (event.windowId < 0) return "INVALID_EVENT_WINDOW" to null
        if (event.packageName != targetPackage && event.packageName != null) return "OTHER_EVENT_PACKAGE" to null
        if (event.packageName == null && event.type != AccessibilityEvent.TYPE_WINDOWS_CHANGED)
            return "NULL_PACKAGE_WRONG_TYPE" to null
        val window = windows.firstOrNull { it.id == event.windowId } ?: return "ABSENT_EVENT_WINDOW" to null
        if (window.packageName != targetPackage) return "OTHER_WINDOW_PACKAGE" to null
        if (event.packageName == null && window.fromFallback) return "FALLBACK_WINDOW" to null
        if (!window.active || !window.focused) return "INACTIVE_OR_UNFOCUSED_WINDOW" to null
        val node = matched ?: return "EXPECTED_NODE_MISSING" to null
        if (node.windowId != window.id || !intersectsDisplay(node.bounds)) return "NODE_OUTSIDE_EVENT_WINDOW_OR_DISPLAY" to null
        return "ELIGIBLE" to node
    }

    @Test
    fun matcherChecksSyntheticEventAndWindowFacts() {
        display.set(1080, 1920)
        val start = 1_000_000_000L
        val down = 10_000L
        val node = VisibleNode(531, 1, null, "Choose Home page, current: Hourly",
            Rect(10, 10, 100, 100), true, true, "Button")
        val app = WindowSnapshot(531, 1, targetPackage, true, true, listOf(node))
        val popup = WindowSnapshot(532, 2, targetPackage, false, false, emptyList())
        val event = EventFacts(targetPackage, AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED,
            down, start, start + TimeUnit.MILLISECONDS.toNanos(timeoutMs), 531)
        fun check(reason: String, facts: EventFacts = event, windows: List<WindowSnapshot> = listOf(app),
                  found: VisibleNode? = node, inputStart: Long = start, inputDown: Long = down) {
            assertEquals(reason, eligibility(facts, inputStart, inputDown, windows, found).first)
        }
        check("ELIGIBLE")
        check("ELIGIBLE", event.copy(packageName = null, type = AccessibilityEvent.TYPE_WINDOWS_CHANGED))
        check("OTHER_WINDOW_PACKAGE", event.copy(packageName = null, type = AccessibilityEvent.TYPE_WINDOWS_CHANGED,
            windowId = 532), listOf(app, popup.copy(packageName = "android")))
        check("STALE_EVENT", event.copy(eventUptimeMs = down - 1))
        check("STALE_EVENT", event.copy(callbackNanos = start - 1))
        check("UNKNOWN_INPUT_TIME", inputDown = 0)
        check("UNKNOWN_INPUT_TIME", inputStart = 0)
        check("NULL_PACKAGE_WRONG_TYPE", event.copy(packageName = null))
        check("OTHER_EVENT_PACKAGE", event.copy(packageName = "android"))
        check("INVALID_EVENT_WINDOW", event.copy(windowId = -1))
        check("ABSENT_EVENT_WINDOW", event.copy(windowId = 999))
        check("INACTIVE_OR_UNFOCUSED_WINDOW", windows = listOf(app.copy(active = false)))
        check("INACTIVE_OR_UNFOCUSED_WINDOW", windows = listOf(app.copy(focused = false)))
        check("FALLBACK_WINDOW", event.copy(packageName = null,
            type = AccessibilityEvent.TYPE_WINDOWS_CHANGED), listOf(app.copy(fromFallback = true)))
        check("EXPECTED_NODE_MISSING", found = null)
        check("NODE_OUTSIDE_EVENT_WINDOW_OR_DISPLAY", found = node.copy(bounds = Rect(1200, 10, 1300, 100)))
        check("NODE_OUTSIDE_EVENT_WINDOW_OR_DISPLAY", found = node.copy(windowId = 532))
        check("AFTER_DEADLINE", event.copy(queryNanos = event.queryNanos + 1))
    }

    @Test
    fun safePointSelectionChecksSyntheticGeometry() {
        val node = VisibleNode(531, 1, null, "Later", Rect(700, 1600, 900, 1750), false, true,
            "Button", listOf(Rect(0, 400, 1080, 1800)))
        val app = WindowSnapshot(531, 1, targetPackage, true, true, listOf(node), bounds = Rect(0, 24, 1080, 1870))
        fun select(windows: List<WindowSnapshot> = listOf(app), requireScrollable: Boolean = true) =
            safePoint(windows, targetPackage, "Later", { it.description == "Later" }, requireScrollable,
                Point(1080, 1920))
        val good = select()
        assertEquals(null, good.reason)
        assertTrue(good.target.bounds.contains(good.point.x, good.point.y))
        assertEquals(Point(800, 1675), good.point)
        val movedNode = node.copy(bounds = Rect(720, 1500, 920, 1650))
        val moved = safePoint(listOf(app.copy(text = listOf(movedNode))), targetPackage, "Later",
            { it.description == "Later" }, true, Point(1080, 1920))
        assertEquals(Point(820, 1575), moved.point)
        assertEquals("NO_TARGET", select(listOf(app.copy(text = emptyList()))).reason)
        assertEquals("AMBIGUOUS_TARGET", select(listOf(app.copy(text = listOf(node, node.copy(bounds = Rect(600, 1600, 800, 1750)))))).reason)
        assertEquals("TARGET_DISABLED", select(listOf(app.copy(text = listOf(node.copy(enabled = false))))).reason)
        assertEquals("MISSING_SCROLL_CONTAINER", select(listOf(app.copy(text = listOf(node.copy(scrollableAncestors = emptyList()))))).reason)
        assertEquals("MISSING_APP_WINDOW_BOUNDS", select(listOf(app.copy(bounds = null))).reason)
        assertEquals("EMPTY_INTERSECTION", select(listOf(app.copy(bounds = Rect(0, 0, 500, 500)))).reason)
        assertEquals("NO_ACTIVE_FOCUSED_APP_WINDOW", select(listOf(app.copy(focused = false))).reason)
        val occluder = WindowSnapshot(532, 2, "android", false, false, emptyList(), bounds = Rect(790, 1660, 820, 1690))
        assertEquals("OCCLUDED", select(listOf(app, occluder)).reason)
        assertEquals("MISSING_APP_WINDOW_BOUNDS", safePoint(
            listOf(app.copy(bounds = null)), targetPackage, "Later", { it.description == "Later" }, false,
            Point(1080, 1920)).reason)
    }

    @Test
    fun outcomeClassificationChecksSyntheticEventsAndSnapshots() {
        matcherChecksSyntheticEventAndWindowFacts()
        assertEquals("NO_INPUT_UNSAFE", classifyOutcome(false, false, "UNSAFE", false))
        assertEquals("NO_INPUT_PRECONDITION", classifyOutcome(false, false, "NO_TARGET", false))
        assertEquals("INJECTION_INCOMPLETE", classifyOutcome(true, false, null, false))
        assertEquals("INJECTION_ACCEPTED + EXPECTED_STATE_CONFIRMED", classifyOutcome(true, true, "EXPECTED", true))
        assertEquals("INJECTION_ACCEPTED + VISIBLE_NO_ELIGIBLE_EVENT", classifyOutcome(true, true, "EXPECTED", false))
        assertEquals("INJECTION_ACCEPTED + WRONG_STATE", classifyOutcome(true, true, "WRONG_STATE", false))
        assertEquals("INJECTION_ACCEPTED + STATE_UNREADABLE", classifyOutcome(true, true, "STATE_UNREADABLE", false))
        assertEquals("EXPECTED_WINDOW_VISIBLE", classifyRenderedState(true, false, true, false))
        assertEquals("PARTIAL_VISIBLE", classifyRenderedState(false, true, true, false))
        assertEquals("STATE_UNREADABLE", classifyRenderedState(false, false, false, false))
        assertEquals("WRONG_STATE", classifyRenderedState(false, false, true, true))
        assertEquals("ELIGIBLE_WITHIN_TIMEOUT", classifyEventState(true, false, false))
        assertEquals("NO_ELIGIBLE_EVENT", classifyEventState(false, false, false))
        assertEquals("LATE", classifyEventState(false, true, false))
        assertEquals("WRONG_DESTINATION", classifyEventState(false, false, true))
        val now = VisibleNode(531, 1, null, "Choose Home page, current: Now", Rect(1, 1, 2, 2), true, true, "Button")
        val menu = VisibleNode(531, 1, null, "Settings", Rect(1, 1, 2, 2), true, true, "View")
        val settings = VisibleNode(531, 1, "Settings", null, Rect(1, 1, 2, 2), true, true, "Text")
        val back = VisibleNode(531, 1, null, "Return to Home", Rect(1, 1, 2, 2), true, true, "Button")
        assertTrue(settingsActionState("menu", listOf(WindowSnapshot(531, 1, targetPackage, true, true, listOf(menu))), targetPackage))
        assertTrue(settingsActionState("settings", listOf(WindowSnapshot(531, 1, targetPackage, true, true, listOf(settings, back))), targetPackage))
        assertTrue(settingsActionState("home", listOf(WindowSnapshot(531, 1, targetPackage, true, true, listOf(now))), targetPackage))
    }

    @Test
    fun validatesTestPathsOnPixel3XL() = runSq10PathsOnPixel3XL()

    private fun runSq10PathsOnPixel3XL() {
        check(shell("getprop ro.product.device").trim() == "crosshatch")
        check(shell("getprop ro.build.version.sdk").trim() == "31")
        val context = instrumentation.targetContext
        val prefs = context.getSharedPreferences("effects_preference_v1", 0)
        val originalEffects = prefs.getString("effects_id", "full") ?: "full"
        val themePrefs = context.getSharedPreferences("theme_preference_v1", 0)
        val contrastPrefs = context.getSharedPreferences("contrast_preference_v1", 0)
        val originalTheme = themePrefs.getString("theme_id", "glass") ?: "glass"
        val originalContrast = contrastPrefs.getString("contrast_id", "standard") ?: "standard"
        val originalSize = shell("wm size").trim()
        val originalDensity = shell("wm density").trim()
        val originalTimeout = shell("settings get system screen_off_timeout").trim()
        val originalFontScale = shell("settings get system font_scale").trim()
        val originalForceRtl = shell("settings get global force_rtl").trim()
        val originalStayAwake = shell("settings get global stay_on_while_plugged_in").trim()
        val runId = SystemClock.elapsedRealtime()
        outputFile = File(context.getExternalFilesDir(null), "213-resolve-screenshot-backed-sq-10-path-visibility/run-$runId.json")
        outputFile.parentFile?.mkdirs()
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "213-sq10-test-path-resolution-$runId.json")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/OxygenWX/213-resolve-screenshot-backed-sq-10-path-visibility")
        }
        outputUri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: error("Unable to create cycle 212 result output")
        val allTrials = JSONArray()
        diagnostics += JSONObject().put("cycle", "213-resolve-screenshot-backed-sq-10-path-visibility")
            .put("candidateApkSha256", "a397dbd70467518fbf79e37b0b5068ab103210689be7f2d547e3561d8872161a")
            .put("deviceSerial", shell("getprop ro.serialno").trim()).put("device", "Pixel 3 XL crosshatch Android 12/API 31")
            .put("preflight", JSONObject().put("size", originalSize).put("density", originalDensity)
                .put("screenTimeoutMs", originalTimeout).put("fontScale", originalFontScale)
                .put("forceRtl", originalForceRtl).put("stayAwake", originalStayAwake)
                .put("appearanceRestoredFromCycle211", JSONObject().put("theme", originalTheme)
                    .put("contrast", originalContrast).put("effects", originalEffects)))
        check(originalStayAwake == "7") { "Developer Options Stay Awake is not selected" }
        automation.setOnAccessibilityEventListener(::onAccessibilityEvent)
        automation.serviceInfo = automation.serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            flags = flags or AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        }
        automation.setRotation(UiAutomation.ROTATION_FREEZE_0)
        try {
            check(themePrefs.edit().putString("theme_id", "glass").commit())
            check(contrastPrefs.edit().putString("contrast_id", "standard").commit())
            check(prefs.edit().putString("effects_id", "full").commit())
            shell("settings put system screen_off_timeout 0")
            shell("wm size 1080x1920")
            shell("wm density 480")
            shell("settings put system font_scale 1.0")
            shell("settings put global force_rtl 0")
            display.set(1080, 1920)
            check(shell("settings get system screen_off_timeout").trim() == "0")
            check(shell("settings get global stay_on_while_plugged_in").trim() == "7")
            check(shell("wm size").contains("1080x1920") && shell("wm density").contains("480"))
            val paths = listOf("daily-none", "daily-full", "settings-none", "settings-full")
            for (path in paths) {
                val effects = if (path.endsWith("none")) "off" else "full"
                check(prefs.edit().putString("effects_id", effects).commit())
                val pathRecord = JSONObject().put("path", path).put("effects", effects).put("trials", JSONArray())
                allTrials.put(pathRecord)
                var consecutiveWrong = 0
                var stopReason: String? = null
                for (trialIndex in 1..5) {
                    val trial = JSONObject().put("trial", trialIndex).put("reset", "PENDING")
                    pathRecord.getJSONArray("trials").put(trial)
                    try {
                        launchHome()
                        trial.put("initialWindows", jsonWindows(snapshotWindows()))
                            .put("initialScreenshot", captureCycle213Screenshot("$path-trial-$trialIndex-initial"))
                        if (path.startsWith("daily")) {
                            ensurePage("Daily")
                            trial.put("dailySetupWindows", jsonWindows(snapshotWindows()))
                                .put("dailySetupScreenshot", captureCycle213Screenshot("$path-trial-$trialIndex-daily-setup"))
                            val dailyWindows = fixturePresentation().dailyWindows
                            check(dailyWindows.size == 2 && dailyWindows.all { it.entries.size == 5 })
                            val firstRows = dailyWindows[0].entries.map { it.spokenSummary }
                            val secondRows = dailyWindows[1].entries.map { it.spokenSummary }
                            val resetActions = JSONArray()
                            var resetCount = 0
                            var position = dailyWindowPosition(snapshotWindows(), firstRows, secondRows)
                            while (position == "SECOND" && resetCount < 2) {
                                var earlier = safePoint(snapshotWindows(), targetPackage, "Earlier",
                                    { it.description == "Earlier" }, true, display)
                                if (earlier.reason in setOf("NO_TARGET", "EMPTY_INTERSECTION")) {
                                    val scroll = screenshotCheckedScroll("$path-trial-$trialIndex-reset-$resetCount", "Daily")
                                    resetActions.put(scroll)
                                    if (!scroll.optBoolean("statePreserved")) break
                                    earlier = safePoint(snapshotWindows(), targetPackage, "Earlier",
                                        { it.description == "Earlier" }, true, display)
                                }
                                if (earlier.reason != null) break
                                resetActions.put(safeResetTap("$path-trial-$trialIndex-reset-earlier-$resetCount",
                                    { it.description == "Earlier" }, true, { ws -> dailyWindowPosition(ws, firstRows, secondRows) == "FIRST" }))
                                resetCount++
                                position = dailyWindowPosition(snapshotWindows(), firstRows, secondRows)
                            }
                            if (position != "FIRST") {
                                trial.put("reset", "FAILED").put("resetActions", resetActions)
                                    .put("reason", "First Daily window is not established by visible fixture rows; position=$position")
                                stopReason = "Daily first-window reset could not be verified"
                                error(stopReason!!)
                            }
                            val upSwipes = JSONArray()
                            val targetSnapshots = JSONArray()
                            var selectionWindows = snapshotWindows()
                            var selected = safePoint(selectionWindows, targetPackage, "Later",
                                { it.description == "Later" }, true, display)
                            targetSnapshots.put(jsonWindows(selectionWindows))
                            var swipeCount = 0
                            while (selected.reason in setOf("NO_TARGET", "EMPTY_INTERSECTION") && swipeCount < 6) {
                                val scroll = screenshotCheckedScroll("$path-trial-$trialIndex-reveal-later-$swipeCount", "Daily")
                                upSwipes.put(scroll); swipeCount++
                                if (!scroll.optBoolean("statePreserved")) {
                                    stopReason = "Daily page state was not attributable after setup swipe"
                                    break
                                }
                                selectionWindows = snapshotWindows()
                                targetSnapshots.put(jsonWindows(selectionWindows))
                                selected = safePoint(selectionWindows, targetPackage, "Later",
                                    { it.description == "Later" }, true, display)
                            }
                            trial.put("reset", "FIRST_DAILY_WINDOW").put("setupSwipes", upSwipes)
                                .put("resetActions", resetActions)
                                .put("targetSelectionSnapshots", targetSnapshots)
                                .put("targetSelection", safePointSummary(selected))
                                .put("firstRange", findRange(snapshotWindows(), "Daily")?.text ?: JSONObject.NULL)
                            if (stopReason != null) {
                                trial.put("disposition", "NO_INPUT_PRECONDITION").put("reason", stopReason)
                            } else if (selected.reason != null) {
                                trial.put("disposition", "NO_INPUT_PRECONDITION").put("reason", selected.reason)
                                stopReason = "Daily Later target unavailable after bounded setup"
                            } else {
                                val expectedRange = dailyWindows[1].rangeLabel
                                val nextRows = dailyWindows[1].entries.map { it.spokenSummary }
                                val expectedDates = dailyWindows[1].entries.map { it.day }
                                val firstRows = dailyWindows.first().entries.map { it.spokenSummary }
                                val beforeRows = activeWindow()?.text.orEmpty().mapNotNull { it.description }
                                    .filter { it in firstRows }
                                val matched = beforeRows.firstOrNull()
                                    ?: error("No first-window Daily row is visible to anchor the same-slot comparison")
                                val slot = firstRows.indexOf(matched)
                                val expectedRow = nextRows[slot]
                                trial.put("firstVisibleSlot", slot).put("expectedRange", expectedRange)
                                    .put("expectedDisplayedDates", JSONArray(expectedDates)).put("expectedSameSlotRow", expectedRow)
                                val result = runTrackedAction("$path-trial-$trialIndex-daily-later",
                                    target = { it.description == "Later" }, requireScrollable = true,
                                expectedState = { ws -> dailyRowState(ws, expectedRow) },
                                eventNode = { ws -> if (dailyRowState(ws, expectedRow)) activeAppNode(ws) { it.description == expectedRow } else null })
                                trial.put("action", result)
                                if (result.optString("rendered_state") == "PARTIAL_VISIBLE") {
                                    val review = captureDailyWindowReview("$path-trial-$trialIndex", expectedRange, expectedDates, nextRows)
                                    result.put("windowReview", review)
                                        .put("rendered_state", review.optString("rendered_state", "PARTIAL_VISIBLE"))
                                }
                                consecutiveWrong = updateWrongStreak(result, consecutiveWrong)
                                if (result.optString("disposition").contains("WRONG_STATE") && consecutiveWrong >= 2)
                                    stopReason = "Second independently reset visible-state failure"
                            }
                        } else if (path.startsWith("hourly")) {
                            ensurePage("Hourly")
                            trial.put("reset", "FIRST_HOURLY_WINDOW")
                            val (expectedRange, rows) = expectedHourlyRanges()
                            val result = runTrackedAction("$path-trial-$trialIndex-hourly-later",
                                target = { it.description == "Later" }, requireScrollable = false,
                                expectedState = { ws -> hourlyState(ws, expectedRange, rows) },
                                eventNode = { ws -> if (hourlyState(ws, expectedRange, rows)) findRange(ws, "Hourly") else null })
                            trial.put("expectedRange", expectedRange).put("expectedRows", JSONArray(rows)).put("action", result)
                            consecutiveWrong = updateWrongStreak(result, consecutiveWrong)
                            if (result.optString("disposition").contains("WRONG_STATE") && consecutiveWrong >= 2)
                                stopReason = "Second independently reset visible-state failure"
                        } else {
                            trial.put("reset", "NOW_HOME_MENU_CLOSED")
                            val menu = runTrackedAction("$path-trial-$trialIndex-home-actions",
                                target = { it.description == "Home actions" }, requireScrollable = false,
                                expectedState = { ws -> settingsActionState("menu", ws, targetPackage) },
                                eventNode = { ws -> if (settingsActionState("menu", ws, targetPackage))
                                    activeAppNode(ws) { it.description == "Settings" } else null })
                            trial.put("homeActions", menu)
                            consecutiveWrong = updateWrongStreak(menu, consecutiveWrong)
                            if (consecutiveWrong >= 2) stopReason = "Second independently reset visible-state failure"
                            if (stopReason == null && menu.optBoolean("expectedVisible")) {
                                val settings = runTrackedAction("$path-trial-$trialIndex-settings",
                                    target = { it.description == "Settings" }, requireScrollable = false,
                                expectedState = { ws -> settingsActionState("settings", ws, targetPackage) },
                                eventNode = { ws -> if (settingsActionState("settings", ws, targetPackage)) activeAppNode(ws) { it.text == "Settings" } else null })
                                trial.put("settings", settings)
                                consecutiveWrong = updateWrongStreak(settings, consecutiveWrong)
                                var returnTarget = safePoint(snapshotWindows(), targetPackage, "Return to Home",
                                    { it.description == "Return to Home" }, true, display)
                                if (settings.optBoolean("expectedVisible")) {
                                    val scrolls = JSONArray()
                                    var scrollCount = 0
                                    while (returnTarget.reason in setOf("NO_TARGET", "EMPTY_INTERSECTION") && scrollCount < 6) {
                                        val scroll = screenshotCheckedScroll("$path-trial-$trialIndex-settings-return-$scrollCount", "Settings")
                                        scrolls.put(scroll)
                                        scrollCount++
                                        if (!scroll.optBoolean("statePreserved")) {
                                            stopReason = "Settings page state was not attributable after setup swipe"
                                            break
                                        }
                                        returnTarget = safePoint(snapshotWindows(), targetPackage, "Return to Home",
                                            { it.description == "Return to Home" }, true, display)
                                    }
                                    settings.put("returnControlScrolls", scrolls).put("returnTargetSelection", safePointSummary(returnTarget))
                                    if (stopReason == null && scrollCount >= 6 && returnTarget.reason != null) {
                                        stopReason = "No safe Return to Home target after six screenshot-checked setup scrolls: ${returnTarget.reason}"
                                    }
                                }
                                if (consecutiveWrong >= 2) stopReason = "Second independently reset visible-state failure"
                                if (stopReason == null && settings.optBoolean("expectedVisible") && returnTarget.reason == null) {
                                    val home = runTrackedAction("$path-trial-$trialIndex-return-home",
                                        target = { it.description == "Return to Home" }, requireScrollable = true,
                                        expectedState = { ws -> settingsActionState("home", ws, targetPackage) },
                                        eventNode = { ws -> if (settingsActionState("home", ws, targetPackage)) activeAppNode(ws) { it.description == "Choose Home page, current: Now" } else null })
                                    trial.put("returnHome", home)
                                    consecutiveWrong = updateWrongStreak(home, consecutiveWrong)
                                    if (consecutiveWrong >= 2) stopReason = "Second independently reset visible-state failure"
                                } else trial.put("returnHome", JSONObject().put("disposition", "SKIPPED")
                                    .put("reason", stopReason ?: if (settings.optBoolean("expectedVisible"))
                                        "Return target is not a safe visible target: ${returnTarget.reason}" else "Settings destination not visible"))
                            } else {
                                trial.put("settings", JSONObject().put("disposition", "SKIPPED").put("reason", "Home actions menu not visible"))
                                trial.put("returnHome", JSONObject().put("disposition", "SKIPPED").put("reason", "Home actions menu not visible"))
                            }
                        }
                    } catch (error: Throwable) {
                        trial.put("disposition", "SKIPPED").put("reason", error.message ?: error.javaClass.name)
                        stopReason = stopReason ?: "Trial setup/attribution failure: ${error.message ?: error.javaClass.name}"
                    }
                    persistResults(allTrials)
                    if (stopReason != null) {
                        for (skipped in (trialIndex + 1)..5) {
                            pathRecord.getJSONArray("trials").put(JSONObject().put("trial", skipped)
                                .put("disposition", "SKIPPED").put("reason", stopReason))
                        }
                        break
                    }
                }
                if (stopReason != null) pathRecord.put("stopReason", stopReason)
                persistResults(allTrials)
            }
        } finally {
            pending = null
            automation.setOnAccessibilityEventListener(null)
            themePrefs.edit().putString("theme_id", originalTheme).commit()
            contrastPrefs.edit().putString("contrast_id", originalContrast).commit()
            prefs.edit().putString("effects_id", originalEffects).commit()
            restore("system", "screen_off_timeout", originalTimeout)
            restoreDisplay("size", originalSize)
            restoreDisplay("density", originalDensity)
            restore("system", "font_scale", originalFontScale)
            restore("global", "force_rtl", originalForceRtl)
            restore("global", "stay_on_while_plugged_in", originalStayAwake)
            automation.setRotation(UiAutomation.ROTATION_UNFREEZE)
            launchHome()
            diagnostics += JSONObject().put("restoration", JSONObject()
                .put("size", shell("wm size").trim()).put("density", shell("wm density").trim())
                .put("screenTimeoutMs", shell("settings get system screen_off_timeout").trim())
                .put("fontScale", shell("settings get system font_scale").trim())
                .put("forceRtl", shell("settings get global force_rtl").trim())
                .put("stayAwake", shell("settings get global stay_on_while_plugged_in").trim())
                .put("theme", themePrefs.getString("theme_id", "glass"))
                .put("contrast", contrastPrefs.getString("contrast_id", "standard"))
                .put("effects", prefs.getString("effects_id", "full")))
            persistResults(allTrials)
        }
    }

    private fun activeWindow(): WindowSnapshot? = snapshotWindows().singleOrNull {
        it.packageName == targetPackage && it.active && it.focused && !it.fromFallback
    }

    private fun activeAppNode(windows: List<WindowSnapshot>, predicate: (VisibleNode) -> Boolean): VisibleNode? {
        val app = windows.singleOrNull { it.packageName == targetPackage && it.active && it.focused } ?: return null
        return app.text.singleOrNull(predicate)
    }

    private fun selectSafeTarget(label: String, predicate: (VisibleNode) -> Boolean, requireScrollable: Boolean): SafePoint {
        val windows = snapshotWindows()
        return safePoint(windows, targetPackage, label, predicate, requireScrollable, display)
    }

    private fun safePointSummary(selected: SafePoint): JSONObject = JSONObject()
        .put("target", jsonNode(selected.target)).put("pointPx", pointJson(selected.point))
        .put("targetBounds", rectJson(selected.targetBounds))
        .put("containerBounds", rectJson(selected.containerBounds))
        .put("appWindowBounds", rectJson(selected.appBounds))
        .put("displayBounds", rectJson(selected.displayBounds))
        .put("reason", selected.reason ?: JSONObject.NULL)

    private fun dailyWindowPosition(windows: List<WindowSnapshot>, firstRows: List<String>, secondRows: List<String>): String {
        val app = windows.singleOrNull { it.packageName == targetPackage && it.active && it.focused } ?: return "UNKNOWN"
        if (findDescription(listOf(app), "Choose Home page, current: Daily") == null) return "UNKNOWN"
        val visible = app.text.mapNotNull { it.description }.toSet()
        val first = visible.any(firstRows::contains)
        val second = visible.any(secondRows::contains)
        return when {
            first && !second -> "FIRST"
            second && !first -> "SECOND"
            else -> "UNKNOWN"
        }
    }

    private fun safeResetTap(
        id: String, target: (VisibleNode) -> Boolean, requireScrollable: Boolean,
        expectedResetState: (List<WindowSnapshot>) -> Boolean,
    ): JSONObject {
        val before = snapshotWindows()
        val action = Pending(id) { null }
        synchronized(rawEvents) { rawEvents.clear() }
        pending = action
        var error: Throwable? = null
        try {
            safeTapWithRevalidation(id, target, requireScrollable)
            waitFor { expectedResetState(snapshotWindows()) }
        } catch (failure: Throwable) {
            error = failure
        }
        pending = null
        val after = snapshotWindows()
        val trace = action.inputTraces.optJSONObject(0)
        return JSONObject().put("id", id).put("preActionWindows", jsonWindows(before))
            .put("preActionScreenshot", captureCycle213Screenshot("$id-before"))
            .put("inputTrace", trace ?: JSONObject.NULL)
            .put("events", JSONArray(synchronized(rawEvents) { rawEvents.toList() }))
            .put("postActionWindows", jsonWindows(after))
            .put("postActionScreenshot", captureCycle213Screenshot("$id-after"))
            .put("expectedResetState", expectedResetState(after))
            .put("disposition", when {
                error != null && trace?.has("pointPx") != true -> "NO_INPUT_PRECONDITION"
                error != null -> "INJECTION_INCOMPLETE"
                trace?.optBoolean("downAccepted") == true && trace.optBoolean("upAccepted") -> "RESET_STATE_CONFIRMED"
                else -> "RESET_STATE_UNVERIFIED"
            })
            .also { if (error != null) it.put("reason", error.message ?: error.javaClass.name) }
    }

    private fun runTrackedAction(
        id: String, target: (VisibleNode) -> Boolean, requireScrollable: Boolean,
        expectedState: (List<WindowSnapshot>) -> Boolean,
        eventNode: (List<WindowSnapshot>) -> VisibleNode?,
    ): JSONObject {
        val before = snapshotWindows()
        val actionData = JSONObject().put("id", id).put("preActionWindows", jsonWindows(before))
            .put("preActionScreenshot", captureCycle213Screenshot("$id-before"))
        val expected = { windows: List<WindowSnapshot> -> if (expectedState(windows)) eventNode(windows) else null }
        var observerResult: JSONObject? = null
        try {
            observerResult = observe(id, expected) { safeTapWithRevalidation(id, target, requireScrollable) }
        } catch (_: Throwable) {
            observerResult = actionResults.lastOrNull { it.optString("id") == id }
        }
        val after = snapshotWindows()
        val visible = runCatching { expectedState(after) }.getOrDefault(false)
        val hasReadableApp = after.any { it.packageName == targetPackage && it.text.isNotEmpty() }
        val dailyPartial = id.contains("daily-later") && activeAppNode(after) { it.description?.startsWith("Choose Home page, current: Daily") == true } != null
        val oldVisible = runCatching { expectedState(before) }.getOrDefault(false)
        val inputTrace = observerResult?.optJSONArray("inputEvents") ?: JSONArray()
        val trace = if (inputTrace.length() > 0) inputTrace.optJSONObject(0) else null
        val attempted = trace?.has("pointPx") == true
        val injection = inputTrace.optJSONObject(0) ?: JSONObject()
        val downAccepted = injection.optBoolean("downAccepted")
        val upAccepted = injection.optBoolean("upAccepted")
        val injectionAccepted = attempted && downAccepted && upAccepted
        val failure = trace?.optString("preconditionFailure")?.takeIf { it.isNotBlank() }
        val moved = trace?.optBoolean("targetMoved") == true
        val timeoutAt = observerResult?.optLong("timeoutEndElapsedRealtimeNanos", 0L) ?: 0L
        val lateVisible = !visible && hasReadableApp && run {
            SystemClock.sleep(1_000)
            val late = snapshotWindows()
            expectedState(late)
        }
        val state = when {
            !attempted && failure != null -> if (trace?.optBoolean("unsafe") == true ||
                failure.contains("OCCLUDED") || failure.contains("unsafe", true)) "UNSAFE" else failure
            attempted && !injectionAccepted -> "INJECTION_INCOMPLETE"
            visible -> "EXPECTED"
            lateVisible -> "VISIBLE_LATE"
            !hasReadableApp -> "STATE_UNREADABLE"
            windowSummary(before).toString() == windowSummary(after).toString() || oldVisible -> "NO_STATE_CHANGE"
            else -> "WRONG_STATE"
        }
        val disposition = if (!attempted && failure == null) "NO_INPUT_PRECONDITION" else
            classifyOutcome(attempted, injectionAccepted, state, observerResult?.optString("status") == "CONFIRMED")
        val postScreenshot = captureCycle213Screenshot("$id-after")
        val rendered = classifyRenderedState(visible && !id.contains("daily-later"), dailyPartial && visible,
            hasReadableApp, state == "WRONG_STATE")
        val event = classifyEventState(observerResult?.optString("status") == "CONFIRMED", lateVisible,
            state == "WRONG_STATE")
        actionData.put("observer", observerResult ?: JSONObject.NULL)
            .put("postActionWindows", jsonWindows(after))
            .put("postActionScreenshot", postScreenshot)
            .put("rendered_state", rendered)
            .put("event_state", event)
            .put("expectedVisible", visible || lateVisible)
            .put("lateVisible", lateVisible)
            .put("disposition", disposition)
            .put("inputDisposition", when {
                !attempted && state == "UNSAFE" -> "NO_INPUT_UNSAFE"
                !attempted -> "NO_INPUT_PRECONDITION"
                !injectionAccepted -> "INJECTION_INCOMPLETE"
                else -> "INJECTION_ACCEPTED"
            })
            .put("stateDisposition", state)
            .put("stateReason", when {
                state == "STATE_UNREADABLE" && id.contains("daily-later") -> "Expected Daily row is absent from the fresh hierarchy"
                else -> JSONObject.NULL
            })
            .put("observerDisposition", if (observerResult?.optString("status") == "CONFIRMED") "CONFIRMED" else "NO_ELIGIBLE_EVENT")
            .put("timedOutAtElapsedRealtimeNanos", timeoutAt)
            .put("inputTrace", trace ?: JSONObject.NULL)
            .put("inputEvents", inputTrace)
        actionResults.removeAll { it.optString("id") == id }
        actionResults += actionData
        persistResults(JSONArray())
        return actionData
    }

    private fun safeTapWithRevalidation(id: String, predicate: (VisibleNode) -> Boolean, requireScrollable: Boolean) {
        val firstWindows = snapshotWindows()
        var first = safePoint(firstWindows, targetPackage, id, predicate, requireScrollable, display)
        val traces = pending?.inputTraces ?: error("No active action record")
        val trace = JSONObject().put("target", id).put("clickable", first.target.clickable)
            .put("firstSnapshot", jsonWindows(firstWindows)).put("targetBounds", rectJson(first.targetBounds))
            .put("containerBounds", rectJson(first.containerBounds)).put("appWindowBounds", rectJson(first.appBounds))
            .put("displayBounds", rectJson(first.displayBounds)).put("firstPointPx", pointJson(first.point))
        traces.put(trace)
        if (first.reason != null) {
            trace.put("preconditionFailure", first.reason)
            error("NO_INPUT_PRECONDITION: ${first.reason}")
        }
        val secondWindows = snapshotWindows()
        val second = safePoint(secondWindows, targetPackage, id, predicate, requireScrollable, display)
        trace.put("immediatePreInputSnapshot", jsonWindows(secondWindows))
            .put("revalidatedTargetBounds", rectJson(second.targetBounds))
            .put("revalidatedContainerBounds", rectJson(second.containerBounds))
            .put("revalidatedAppWindowBounds", rectJson(second.appBounds))
            .put("revalidatedDisplayBounds", rectJson(second.displayBounds))
            .put("revalidatedPointPx", pointJson(second.point))
        if (second.reason != null || second.window.id != first.window.id) {
            trace.put("preconditionFailure", second.reason ?: "TARGET_WINDOW_CHANGED")
            if (second.reason == "OCCLUDED") trace.put("unsafe", true)
            error("NO_INPUT_PRECONDITION: ${second.reason ?: "TARGET_WINDOW_CHANGED"}")
        }
        var chosenPoint = first.point
        val moved = first.targetBounds != second.targetBounds || first.containerBounds != second.containerBounds ||
            first.appBounds != second.appBounds
        if (moved || !Rect(second.targetBounds).contains(chosenPoint.x, chosenPoint.y) ||
            !Rect(second.containerBounds).contains(chosenPoint.x, chosenPoint.y) ||
            !Rect(second.appBounds).contains(chosenPoint.x, chosenPoint.y) ||
            !Rect(second.displayBounds).contains(chosenPoint.x, chosenPoint.y)) {
            trace.put("targetMoved", true)
            chosenPoint = second.point
            val thirdWindows = snapshotWindows()
            val third = safePoint(thirdWindows, targetPackage, id, predicate, requireScrollable, display)
            trace.put("movedTargetRecheckSnapshot", jsonWindows(thirdWindows))
                .put("movedTargetRecheckBounds", rectJson(third.targetBounds))
                .put("movedTargetRecheckPointPx", pointJson(third.point))
            if (third.reason != null || third.window.id != first.window.id ||
                second.targetBounds != third.targetBounds || second.containerBounds != third.containerBounds ||
                second.appBounds != third.appBounds ||
                !Rect(third.targetBounds).contains(chosenPoint.x, chosenPoint.y) ||
                !Rect(third.containerBounds).contains(chosenPoint.x, chosenPoint.y) ||
                !Rect(third.appBounds).contains(chosenPoint.x, chosenPoint.y) ||
                !Rect(third.displayBounds).contains(chosenPoint.x, chosenPoint.y)) {
                trace.put("preconditionFailure", third.reason ?: "TARGET_MOVED_TWICE_OR_POINT_UNSAFE")
                trace.put("unsafe", true)
                error("NO_INPUT_UNSAFE: target moved or became unsafe during revalidation")
            }
            first = third
        } else chosenPoint = first.point
        trace.put("pointPx", pointJson(chosenPoint)).put("source", InputDevice.SOURCE_TOUCHSCREEN)
            .put("safeIntersection", rectJson(Rect(first.targetBounds).apply {
                intersect(first.containerBounds); intersect(first.appBounds); intersect(first.displayBounds)
            }))
            .put("finalTarget", jsonNode(first.target))
        injectTap(chosenPoint.x.toFloat(), chosenPoint.y.toFloat(), trace)
    }

    private fun expectedDailyRows(): List<String> = fixturePresentation().dailyWindows.flatMap { it.entries }.map { it.spokenSummary }

    private fun expectedDailyFirstRange(): String = fixturePresentation().dailyWindows.firstOrNull()?.rangeLabel
        ?: error("Fixture has no Daily window")

    private fun expectedDailyRanges(): Pair<String, List<String>> {
        val daily = fixturePresentation().dailyWindows
        check(daily.size == 2 && daily.all { it.entries.size == 5 })
        return daily[1].rangeLabel to daily[1].entries.map { it.spokenSummary }
    }

    private fun expectedHourlyRanges(): Pair<String, List<String>> {
        val hourly = fixturePresentation().hourlyWindows
        check(hourly.size >= 2 && hourly[0].entries.size == 6 && hourly[1].entries.size == 6)
        return hourly[1].rangeLabel to hourly[1].entries.map { it.spokenSummary }
    }

    private fun fixturePresentation() = HomePresentationMapper.map(
        DemoWeatherRepository.load(LocalDateTime.now()),
        HistoricalSynthesis.derive(DemoWeatherRepository.load(LocalDateTime.now())),
        UnitPresetSelection(SharedPreferencesUnitPresetStore(instrumentation.targetContext)).effectivePreset,
    )

    private fun dailyState(windows: List<WindowSnapshot>, range: String, expectedRow: String): Boolean {
        val page = windows.singleOrNull { it.packageName == targetPackage && it.active && it.focused &&
            findDescription(listOf(it), "Choose Home page, current: Daily") != null } ?: return false
        if (findRange(listOf(page), "Daily")?.text != range) return false
        val expected = expectedDailyRows().toSet()
        val visibleRows = page.text.filter { it.description in expected }.mapNotNull { it.description }
        return visibleRows.isNotEmpty() && visibleRows.all { it in expected } && expectedRow in visibleRows
    }

    private fun dailyRowState(windows: List<WindowSnapshot>, expectedRow: String): Boolean {
        val page = windows.singleOrNull { it.packageName == targetPackage && it.active && it.focused &&
            findDescription(listOf(it), "Choose Home page, current: Daily") != null } ?: return false
        return page.text.any { it.description == expectedRow }
    }

    private fun screenshotCheckedScroll(
        id: String, expectedPage: String, towardStart: Boolean = false,
        towardEnd: Boolean = false, distanceFraction: Float = 0.6f,
    ): JSONObject {
        val before = snapshotWindows()
        val app = before.singleOrNull { it.packageName == targetPackage && it.active && it.focused && !it.fromFallback }
            ?: error("SCROLL_PRECONDITION: no unique active/focused app window")
        check((expectedPage == "Settings" && settingsScreenRecognizable(app)) ||
            findDescription(listOf(app), "Choose Home page, current: $expectedPage") != null) {
            "SCROLL_PRECONDITION: expected $expectedPage page/state is not active"
        }
        val containers = app.text.flatMap { it.scrollableAncestors }.distinctBy { listOf(it.left, it.top, it.right, it.bottom) }
            .mapNotNull { candidate ->
                val safe = Rect(candidate)
                if (!safe.intersect(app.bounds ?: return@mapNotNull null) ||
                    !safe.intersect(Rect(0, 0, display.x, display.y))) null else safe
            }
        check(containers.size == 1) { "SCROLL_PRECONDITION: expected one visible scroll container, found ${containers.size}" }
        val bounds = containers.single()
        check(bounds.width() >= 120 && bounds.height() >= 180) { "SCROLL_PRECONDITION: scroll bounds too small $bounds" }
        val preScreenshot = captureCycle213Screenshot("$id-before-swipe")
        check(!preScreenshot.has("error")) { "SCROLL_SCREENSHOT_FAILED: ${preScreenshot.optString("error")}" }
        val x = bounds.centerX().toFloat()
        val inset = bounds.height() / 5
        val startY: Float
        val endY: Float
        if (towardEnd) {
            val available = bounds.height() - 2 * inset
            val distance = (available * distanceFraction.coerceIn(0.1f, 0.8f)).toInt()
            startY = (bounds.bottom - inset - (available - distance) / 2).toFloat()
            endY = startY - distance
        } else {
            startY = (if (towardStart) bounds.top + inset else bounds.bottom - inset).toFloat()
            endY = (if (towardStart) bounds.bottom - inset else bounds.top + inset).toFloat()
        }
        val trace = injectSetupSwipe(x, startY, endY)
        val after = snapshotWindows()
        val screenshot = captureCycle213Screenshot("$id-after-swipe")
        check(!screenshot.has("error")) { "SCROLL_SCREENSHOT_FAILED: ${screenshot.optString("error")}" }
        val postApp = after.singleOrNull { it.packageName == targetPackage && it.active && it.focused }
        val statePreserved = postApp != null && (findDescription(listOf(postApp), "Choose Home page, current: $expectedPage") != null ||
            (expectedPage == "Settings" && settingsScreenRecognizable(postApp)))
        return JSONObject().put("id", id).put("scrollContainerBounds", rectJson(bounds))
            .put("source", InputDevice.SOURCE_TOUCHSCREEN).put("startPointPx", JSONArray(listOf(x, startY)))
            .put("endPointPx", JSONArray(listOf(x, endY))).put("gesture", trace)
            .put("preSwipeWindows", jsonWindows(before)).put("preSwipeScreenshot", preScreenshot)
            .put("postSwipeWindows", jsonWindows(after)).put("postSwipeScreenshot", screenshot)
            .put("statePreserved", statePreserved)
            .put("stateDisposition", if (statePreserved) "EXPECTED_PAGE_RETAINED" else "STATE_UNATTRIBUTABLE")
    }

    private fun settingsScreenRecognizable(window: WindowSnapshot): Boolean = window.text.any {
        it.text == "Settings" || it.description == "Return to Home" ||
            it.text in setOf("Appearance", "Units", "Locations", "Data Sources", "Privacy", "Open Source Licenses", "About")
    }

    private fun captureDailyWindowReview(id: String, expectedRange: String, expectedDates: List<String>, expectedRows: List<String>): JSONObject {
        val initialWindows = snapshotWindows()
        val active = initialWindows.singleOrNull { it.packageName == targetPackage && it.active && it.focused }
            ?: error("DAILY_REVIEW: active Daily page unavailable")
        val controlState = active.text.filter { it.description == "Earlier" || it.description == "Later" ||
            it.description == "Earlier unavailable" || it.description == "Later unavailable" }
            .map { JSONObject().put("description", it.description).put("enabled", it.enabled).put("bounds", rectJson(it.bounds)) }
        val review = JSONObject().put("expectedRange", expectedRange).put("expectedRows", JSONArray(expectedRows))
            .put("expectedDisplayedDates", JSONArray(expectedDates))
            .put("postActionControls", JSONArray(controlState))
            .put("captures", JSONArray())
        val seen = linkedSetOf<String>()
        repeat(3) { index ->
            val windows = snapshotWindows()
            val app = windows.singleOrNull { it.packageName == targetPackage && it.active && it.focused }
                ?: error("DAILY_REVIEW: active Daily page unavailable")
            check(findDescription(listOf(app), "Choose Home page, current: Daily") != null) {
                "DAILY_REVIEW: Daily page identity lost"
            }
            val scrollBounds = app.text.flatMap { it.scrollableAncestors }.lastOrNull() ?: Rect(0, 0, display.x, display.y)
            val rows = app.text.filter { node ->
                val description = node.description ?: return@filter false
                if (description !in expectedRows) return@filter false
                val visible = Rect(node.bounds)
                visible.intersect(scrollBounds) && visible.intersect(Rect(0, 0, display.x, display.y)) &&
                    visible.height() >= (node.bounds.height() * 0.65f).toInt()
            }.mapNotNull { it.description }
            seen += rows
            val screenshot = captureCycle213Screenshot("$id-window-review-$index")
            check(!screenshot.has("error")) { "DAILY_REVIEW_SCREENSHOT_FAILED: ${screenshot.optString("error")}" }
            review.getJSONArray("captures").put(JSONObject().put("index", index)
                .put("windows", jsonWindows(windows)).put("visibleExpectedRows", JSONArray(rows))
                .put("screenshot", screenshot))
            if (seen.containsAll(expectedRows)) return review.put("visibleExpectedRowsAcrossCaptures", JSONArray(seen))
                .put("rendered_state", if (controlState.any { it.optString("description") == "Earlier" && it.optBoolean("enabled") } &&
                    controlState.any { it.optString("description") == "Later unavailable" }) "EXPECTED_WINDOW_VISIBLE" else "PARTIAL_VISIBLE")
            if (index == 0 || index == 1) {
                val scroll = if (index == 0) screenshotCheckedScroll("$id-window-review-scroll-$index", "Daily", towardStart = true)
                    else screenshotCheckedScroll("$id-window-review-scroll-$index", "Daily", towardEnd = true, distanceFraction = 0.55f)
                review.getJSONArray("captures").put(scroll)
                if (!scroll.optBoolean("statePreserved")) return review
                    .put("visibleExpectedRowsAcrossCaptures", JSONArray(seen))
                    .put("rendered_state", "STATE_UNREADABLE")
            }
        }
        return review.put("visibleExpectedRowsAcrossCaptures", JSONArray(seen)).put("rendered_state", "PARTIAL_VISIBLE")
    }

    private fun hourlyState(windows: List<WindowSnapshot>, range: String, rows: List<String>): Boolean {
        val page = windows.singleOrNull { it.packageName == targetPackage && it.active && it.focused &&
            findDescription(listOf(it), "Choose Home page, current: Hourly") != null } ?: return false
        if (findRange(listOf(page), "Hourly")?.text != range) return false
        val visibleRows = page.text.mapNotNull { it.description?.takeIf(rows::contains) }
        return visibleRows == rows
    }

    private fun updateWrongStreak(action: JSONObject, previous: Int): Int =
        if (action.optString("stateDisposition") in setOf("WRONG_STATE", "NO_STATE_CHANGE")) previous + 1 else 0

    private fun rectJson(rect: Rect): JSONArray = JSONArray(listOf(rect.left, rect.top, rect.right, rect.bottom))
    private fun pointJson(point: Point): JSONArray = JSONArray(listOf(point.x, point.y))

    private fun captureCycle213Screenshot(label: String): JSONObject {
        val at = SystemClock.elapsedRealtimeNanos()
        val bitmap = automation.takeScreenshot() ?: return JSONObject().put("error", "screenshot unavailable")
        return try {
            val clean = label.replace(Regex("[^A-Za-z0-9_-]"), "-")
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "213-$clean.png")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/OxygenWX/213-resolve-screenshot-backed-sq-10-path-visibility")
            }
            val resolver = instrumentation.targetContext.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: error("cannot create screenshot")
            resolver.openOutputStream(uri, "w")?.use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }
                ?: error("cannot write screenshot")
            JSONObject().put("uri", uri.toString()).put("elapsedRealtimeNanos", at)
                .put("width", bitmap.width).put("height", bitmap.height)
        } catch (error: Throwable) {
            JSONObject().put("elapsedRealtimeNanos", at).put("error", error.message ?: error.javaClass.name)
        } finally { bitmap.recycle() }
    }

    @Test
    fun dailyLaterReachabilityDiagnostic() = runObserver(fixedSet = false)

    @Test
    fun revalidatesFixedActions() = runObserver(fixedSet = true)

    @Test
    fun continueUnscoredPairAfterIsolatedDailyObserverFailure() = runRemainingPairActions()

    @Test
    fun finishUnscoredPairFullAppearance() = runFullAppearanceContinuation()

    @Test
    fun finishFullAppearanceAfterHourlyObserverFailure() = runFullRemainingActions()

    private fun runFullRemainingActions() {
        val context = instrumentation.targetContext
        val prefs = context.getSharedPreferences("effects_preference_v1", 0)
        val originalEffects = prefs.getString("effects_id", "full") ?: "full"
        val originalSize = shell("wm size").trim()
        val originalDensity = shell("wm density").trim()
        val originalTimeout = shell("settings get system screen_off_timeout").trim()
        val originalFontScale = shell("settings get system font_scale").trim()
        val originalForceRtl = shell("settings get global force_rtl").trim()
        val originalStayAwake = shell("settings get global stay_on_while_plugged_in").trim()
        check(readPreference("theme_preference_v1", "theme_id") == "glass")
        check(readPreference("contrast_preference_v1", "contrast_id") == "standard")
        check(originalEffects == "full" && originalStayAwake == "7")
        val runId = SystemClock.elapsedRealtime()
        outputFile = File(context.getExternalFilesDir(null), "211-sq10-dry-run/full-remaining-$runId.json")
        outputFile.parentFile?.mkdirs()
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "211-sq10-full-remaining-$runId.json")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/OxygenWX/211-sq10-static-full-dry-run-method-validation")
        }
        outputUri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: error("Unable to create Full remainder output")
        diagnostics += JSONObject().put("continuationOf", "the same unscored pair; no action is repeated")
            .put("appearance", "full").put("reason", "Full Hourly Later semantic endpoint was unverified")
            .put("startElapsedRealtimeNanos", SystemClock.elapsedRealtimeNanos())
        automation.setOnAccessibilityEventListener(::onAccessibilityEvent)
        automation.serviceInfo = automation.serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            flags = flags or AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        }
        automation.setRotation(UiAutomation.ROTATION_FREEZE_0)
        try {
            shell("settings put system screen_off_timeout 0")
            shell("wm size 1080x1920")
            shell("wm density 480")
            shell("settings put system font_scale 1.0")
            shell("settings put global force_rtl 0")
            display.set(1080, 1920)
            check(shell("settings get system screen_off_timeout").trim() == "0")
            check(shell("wm size").contains("1080x1920") && shell("wm density").contains("480"))
            check(shell("settings get global stay_on_while_plugged_in").trim() == "7")
            check(prefs.edit().putString("effects_id", "full").commit())
            launchHome()
            ensurePage("Daily")
            val daily = try {
                dailyLater("full-daily-later")
            } catch (error: Throwable) {
                val raw = actionResults.lastOrNull { it.optString("id") == "full-daily-later" }
                JSONObject().put("id", "full-daily-later").put("status", "UNVERIFIED")
                    .put("reason", error.message ?: error.javaClass.name)
                    .put("rawObserver", raw ?: JSONObject.NULL)
            }
            val actions = JSONArray().put(record(daily))
            ensurePage("Now")
            actions.put(record(safeObserve("full-home-menu-open", { windows -> findText(windows, "Settings") }) {
                tapDescription("Home actions")
            }))
            actions.put(record(safeObserve("full-home-menu-close", { windows ->
                val home = findDescription(windows, "Choose Home page, current: Now")
                if (home != null && findText(windows, "Settings") == null) home else null
            }) { pressBack() }))
            actions.put(record(safeObserve("full-settings-open", { windows ->
                val returnControl = findText(windows, "Return to Home") ?: return@safeObserve null
                findText(windows, "Settings")?.takeIf { it.windowId == returnControl.windowId }
            }) {
                tapDescription("Home actions")
                tapText("Settings")
            }))
            val returnControl = findText(snapshotWindows(), "Return to Home")
            val returnResult = if (returnControl == null) {
                JSONObject().put("id", "full-settings-return").put("status", "UNVERIFIED")
                    .put("reason", "Return to Home control was not present after Settings open")
            } else safeObserve("full-settings-return", { windows ->
                findDescription(windows, "Choose Home page, current: Now")
            }) { tapText("Return to Home") }
            actions.put(record(returnResult))
            persistResults(JSONArray().put(JSONObject().put("effects", "full")
                .put("continuation", true).put("actions", actions)))
            SystemClock.sleep(1_000)
            diagnostics += JSONObject().put("continuationCompletedElapsedRealtimeNanos", SystemClock.elapsedRealtimeNanos())
        } finally {
            pending = null
            automation.setOnAccessibilityEventListener(null)
            prefs.edit().putString("effects_id", originalEffects).commit()
            restore("system", "screen_off_timeout", originalTimeout)
            restoreDisplay("size", originalSize)
            restoreDisplay("density", originalDensity)
            restore("system", "font_scale", originalFontScale)
            restore("global", "force_rtl", originalForceRtl)
            restore("global", "stay_on_while_plugged_in", originalStayAwake)
            automation.setRotation(UiAutomation.ROTATION_UNFREEZE)
            launchHome()
        }
    }

    private fun runFullAppearanceContinuation() {
        val context = instrumentation.targetContext
        val prefs = context.getSharedPreferences("effects_preference_v1", 0)
        val originalEffects = prefs.getString("effects_id", "full") ?: "full"
        val originalSize = shell("wm size").trim()
        val originalDensity = shell("wm density").trim()
        val originalTimeout = shell("settings get system screen_off_timeout").trim()
        val originalFontScale = shell("settings get system font_scale").trim()
        val originalForceRtl = shell("settings get global force_rtl").trim()
        val originalStayAwake = shell("settings get global stay_on_while_plugged_in").trim()
        check(readPreference("theme_preference_v1", "theme_id") == "glass")
        check(readPreference("contrast_preference_v1", "contrast_id") == "standard")
        check(originalEffects == "full" && originalStayAwake == "7")
        val runId = SystemClock.elapsedRealtime()
        outputFile = File(context.getExternalFilesDir(null), "211-sq10-dry-run/full-continuation-$runId.json")
        outputFile.parentFile?.mkdirs()
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "211-sq10-full-continuation-$runId.json")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/OxygenWX/211-sq10-static-full-dry-run-method-validation")
        }
        outputUri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: error("Unable to create Full continuation output")
        diagnostics += JSONObject().put("continuationOf", "the same unscored None/Full pair")
            .put("appearance", "full").put("startElapsedRealtimeNanos", SystemClock.elapsedRealtimeNanos())
        automation.setOnAccessibilityEventListener(::onAccessibilityEvent)
        automation.serviceInfo = automation.serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            flags = flags or AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        }
        automation.setRotation(UiAutomation.ROTATION_FREEZE_0)
        try {
            shell("settings put system screen_off_timeout 0")
            shell("wm size 1080x1920")
            shell("wm density 480")
            shell("settings put system font_scale 1.0")
            shell("settings put global force_rtl 0")
            display.set(1080, 1920)
            check(shell("settings get system screen_off_timeout").trim() == "0")
            check(shell("wm size").contains("1080x1920") && shell("wm density").contains("480"))
            check(shell("settings get global stay_on_while_plugged_in").trim() == "7")
            check(prefs.edit().putString("effects_id", "full").commit())
            launchHome()
            ensurePage("Now")
            val full = runAppearance("full").put("continuation", true)
            persistResults(JSONArray().put(full))
            SystemClock.sleep(1_000)
            diagnostics += JSONObject().put("continuationCompletedElapsedRealtimeNanos", SystemClock.elapsedRealtimeNanos())
        } finally {
            pending = null
            automation.setOnAccessibilityEventListener(null)
            prefs.edit().putString("effects_id", originalEffects).commit()
            restore("system", "screen_off_timeout", originalTimeout)
            restoreDisplay("size", originalSize)
            restoreDisplay("density", originalDensity)
            restore("system", "font_scale", originalFontScale)
            restore("global", "force_rtl", originalForceRtl)
            restore("global", "stay_on_while_plugged_in", originalStayAwake)
            automation.setRotation(UiAutomation.ROTATION_UNFREEZE)
            launchHome()
        }
    }

    private fun runRemainingPairActions() {
        val context = instrumentation.targetContext
        val prefs = context.getSharedPreferences("effects_preference_v1", 0)
        val originalEffects = prefs.getString("effects_id", "full") ?: "full"
        val originalSize = shell("wm size").trim()
        val originalDensity = shell("wm density").trim()
        val originalTimeout = shell("settings get system screen_off_timeout").trim()
        val originalFontScale = shell("settings get system font_scale").trim()
        val originalForceRtl = shell("settings get global force_rtl").trim()
        val originalStayAwake = shell("settings get global stay_on_while_plugged_in").trim()
        check(readPreference("theme_preference_v1", "theme_id") == "glass")
        check(readPreference("contrast_preference_v1", "contrast_id") == "standard")
        check(originalEffects == "full" && originalStayAwake == "7")
        val runId = SystemClock.elapsedRealtime()
        outputFile = File(context.getExternalFilesDir(null), "211-sq10-dry-run/continuation-$runId.json")
        outputFile.parentFile?.mkdirs()
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "211-sq10-continuation-$runId.json")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/OxygenWX/211-sq10-static-full-dry-run-method-validation")
        }
        outputUri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: error("Unable to create continuation output")
        diagnostics += JSONObject().put("continuationOf", "the existing unscored pair; no action is repeated")
            .put("reason", "Daily Later node moved below the reviewed tap point; first invocation incorrectly aborted on UNVERIFIED record")
            .put("startElapsedRealtimeNanos", SystemClock.elapsedRealtimeNanos())
        automation.setOnAccessibilityEventListener(::onAccessibilityEvent)
        automation.serviceInfo = automation.serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            flags = flags or AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        }
        automation.setRotation(UiAutomation.ROTATION_FREEZE_0)
        try {
            shell("settings put system screen_off_timeout 0")
            shell("wm size 1080x1920")
            shell("wm density 480")
            shell("settings put system font_scale 1.0")
            shell("settings put global force_rtl 0")
            display.set(1080, 1920)
            check(shell("settings get system screen_off_timeout").trim() == "0")
            check(shell("wm size").contains("1080x1920") && shell("wm density").contains("480"))
            check(shell("settings get global stay_on_while_plugged_in").trim() == "7")

            val noneActions = JSONArray()
            check(prefs.edit().putString("effects_id", "off").commit())
            launchHome()
            ensurePage("Now")
            noneActions.put(record(observe("off-home-menu-open", { windows -> findText(windows, "Settings") }) {
                tapDescription("Home actions")
            }))
            noneActions.put(record(observe("off-home-menu-close", { windows ->
                val home = findDescription(windows, "Choose Home page, current: Now")
                if (home != null && findText(windows, "Settings") == null) home else null
            }) { pressBack() }))
            noneActions.put(record(observe("off-settings-open", { windows ->
                val returnControl = findText(windows, "Return to Home") ?: return@observe null
                findText(windows, "Settings")?.takeIf { it.windowId == returnControl.windowId }
            }) {
                tapDescription("Home actions")
                tapText("Settings")
            }))
            noneActions.put(record(observe("off-settings-return", { windows ->
                findDescription(windows, "Choose Home page, current: Now")
            }) { tapText("Return to Home") }))

            check(prefs.edit().putString("effects_id", "full").commit())
            launchHome()
            ensurePage("Now")
            val appearances = JSONArray()
                .put(JSONObject().put("effects", "off").put("continuation", true).put("actions", noneActions))
                .put(runAppearance("full").put("continuation", true))
            persistResults(appearances)
            SystemClock.sleep(1_000)
            diagnostics += JSONObject().put("continuationCompletedElapsedRealtimeNanos", SystemClock.elapsedRealtimeNanos())
        } finally {
            pending = null
            automation.setOnAccessibilityEventListener(null)
            prefs.edit().putString("effects_id", originalEffects).commit()
            restore("system", "screen_off_timeout", originalTimeout)
            restoreDisplay("size", originalSize)
            restoreDisplay("density", originalDensity)
            restore("system", "font_scale", originalFontScale)
            restore("global", "force_rtl", originalForceRtl)
            restore("global", "stay_on_while_plugged_in", originalStayAwake)
            automation.setRotation(UiAutomation.ROTATION_UNFREEZE)
            launchHome()
        }
    }

    private fun runObserver(fixedSet: Boolean) {
        val context = instrumentation.targetContext
        val prefs = context.getSharedPreferences("effects_preference_v1", 0)
        val themePrefs = context.getSharedPreferences("theme_preference_v1", 0)
        val contrastPrefs = context.getSharedPreferences("contrast_preference_v1", 0)
        val seededTheme = !themePrefs.contains("theme_id")
        val seededEffects = !prefs.contains("effects_id")
        val seededContrast = !contrastPrefs.contains("contrast_id")
        if (seededTheme) check(themePrefs.edit().putString("theme_id", "glass").commit())
        if (seededEffects) check(prefs.edit().putString("effects_id", "full").commit())
        if (seededContrast) check(contrastPrefs.edit().putString("contrast_id", "standard").commit())
        val originalEffects = prefs.getString("effects_id", "full") ?: "full"
        val originalTheme = readPreference("theme_preference_v1", "theme_id")
        val originalContrast = readPreference("contrast_preference_v1", "contrast_id")
        val originalTimeout = shell("settings get system screen_off_timeout").trim()
        val originalSize = shell("wm size").trim()
        val originalDensity = shell("wm density").trim()
        val originalFontScale = shell("settings get system font_scale").trim()
        val originalForceRtl = shell("settings get global force_rtl").trim()
        val originalAnimator = shell("settings get global animator_duration_scale").trim()
        val originalTransition = shell("settings get global transition_animation_scale").trim()
        val originalWindow = shell("settings get global window_animation_scale").trim()
        val originalStayAwake = shell("settings get global stay_on_while_plugged_in").trim()
        val runId = SystemClock.elapsedRealtime()
        diagnostics += JSONObject().put("appearancePreflight", JSONObject()
            .put("theme", originalTheme).put("effects", originalEffects).put("contrast", originalContrast)
            .put("seededForFreshInstall", JSONObject().put("theme", seededTheme)
                .put("effects", seededEffects).put("contrast", seededContrast)))
        outputFile = File(context.getExternalFilesDir(null), "211-sq10-dry-run/observer-events-$runId.json")
        outputFile.parentFile?.mkdirs()
        val outputValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "211-sq10-observer-events-$runId.json")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/OxygenWX/211-sq10-static-full-dry-run-method-validation")
        }
        outputUri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, outputValues)
            ?: error("Unable to create persistent observer output")
        automation.setOnAccessibilityEventListener(::onAccessibilityEvent)
        automation.serviceInfo = automation.serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            flags = flags or AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        }
        automation.setRotation(UiAutomation.ROTATION_FREEZE_0)
        try {
            shell("settings put system screen_off_timeout 0")
            shell("wm size 1080x1920")
            shell("wm density 480")
            shell("settings put system font_scale 1.0")
            shell("settings put global force_rtl 0")
            shell("settings put global animator_duration_scale 1")
            shell("settings put global transition_animation_scale 1")
            shell("settings put global window_animation_scale 1")
            context.getSystemService(DisplayManager::class.java)
                .getDisplay(android.view.Display.DEFAULT_DISPLAY).getRealSize(display)
            check(shell("settings get system screen_off_timeout").trim() == "0") { "Screen timeout was not disabled" }
            check(shell("wm size").contains("1080x1920")) { "Measurement viewport size readback failed" }
            check(shell("wm density").contains("480")) { "Measurement density readback failed" }
            check(shell("settings get global stay_on_while_plugged_in").trim() == "7") {
                "Stay Awake is not selected on AC/USB power"
            }
            check(originalEffects == "full") { "The pre-probe app appearance was not Full: $originalEffects" }
            check(originalTheme == "glass") { "The pre-probe theme was not Glass: $originalTheme" }
            check(originalContrast == "standard") { "The pre-probe contrast was not Standard: $originalContrast" }
            check(originalStayAwake == "7") { "Stay Awake changed before probe: $originalStayAwake" }

            if (fixedSet) {
                val warmupStart = SystemClock.elapsedRealtimeNanos()
                launchHome()
                waitFor { findDescription(snapshotWindows(), "Choose Home page, current: Now") != null }
                SystemClock.sleep(1_000)
                diagnostics += JSONObject().put("warmup", JSONObject()
                    .put("disposition", "UNMEASURED")
                    .put("startElapsedRealtimeNanos", warmupStart)
                    .put("endElapsedRealtimeNanos", SystemClock.elapsedRealtimeNanos())
                    .put("state", windowSummary(snapshotWindows())))
            }

            check(prefs.edit().putString("effects_id", "off").commit())
            launchHome()
            check(readPreference("effects_preference_v1", "effects_id") == "off") {
                "Effects preference readback failed for off"
            }
            if (fixedSet) {
                val results = JSONArray()
                listOf("off", "full").forEach { effects ->
                    check(prefs.edit().putString("effects_id", effects).commit())
                    launchHome()
                    check(readPreference("effects_preference_v1", "effects_id") == effects) {
                        "Effects preference readback failed for $effects"
                    }
                    ensurePage("Now")
                    results.put(runAppearance(effects))
                }
                persistResults(results)
                SystemClock.sleep(1_000)
            } else {
                ensurePage("Daily")
                record(dailyLater("off-daily-later-diagnostic"))
                persistResults(JSONArray())
            }

            assertTrue("Observer output must be retained", outputFile.isFile && outputFile.length() > 0)
        } finally {
            persistResults(JSONArray())
            pending = null
            activeDiagnostic = null
            automation.setOnAccessibilityEventListener(null)
            prefs.edit().putString("effects_id", originalEffects).commit()
            restore("system", "screen_off_timeout", originalTimeout)
            restoreDisplay("size", originalSize)
            restoreDisplay("density", originalDensity)
            restore("system", "font_scale", originalFontScale)
            restore("global", "force_rtl", originalForceRtl)
            restore("global", "animator_duration_scale", originalAnimator)
            restore("global", "transition_animation_scale", originalTransition)
            restore("global", "window_animation_scale", originalWindow)
            restore("global", "stay_on_while_plugged_in", originalStayAwake)
            automation.setRotation(UiAutomation.ROTATION_UNFREEZE)
            launchHome()
        }
    }

    private fun diagnoseChooserTrial(trial: Int): JSONObject {
        val capture = DiagnosticCapture(trial)
        activeDiagnostic = capture
        synchronized(rawEvents) { rawEvents.clear() }
        val beforeWindows = snapshotWindows()
        val chooser = findDescription(beforeWindows, "Choose Home page, current: Now")
        if (chooser == null) {
            activeDiagnostic = null
            return JSONObject()
                .put("trial", trial)
                .put("status", "NO_INITIAL_NOW_IDENTITY")
                .put("beforeWindowTree", windowSummary(beforeWindows))
                .put("failureScreenshot", captureScreenshot(capture, "initial-now-identity-missing"))
        }
        captureWindowSample(capture, "beforeChooserTap", beforeWindows)

        val action = Pending("off-diagnostic-now-hourly-$trial") {
            windows -> findDescription(windows, "Choose Home page, current: Hourly")
        }
        pending = action
        val chooserTouch = diagnosticTap(chooser.bounds.centerX().toFloat(), chooser.bounds.centerY().toFloat(), action)
            .put("selector", "Choose Home page, current: Now")
            .put("node", jsonNode(chooser))
        val postTapWindows = snapshotWindows()
        captureWindowSample(capture, "immediateAfterChooserTap", postTapWindows)
        val firstScreenshot = captureScreenshot(capture, "first-post-tap")

        val menuItemDescription = "Hourly page, 2 of 4, not selected"
        var menuItem = findDescription(snapshotWindows(), menuItemDescription)
        val chooserDeadlineNanos = action.startNanos + TimeUnit.MILLISECONDS.toNanos(timeoutMs)
        while (menuItem == null && SystemClock.elapsedRealtimeNanos() < chooserDeadlineNanos) {
            val sampledWindows = snapshotWindows()
            captureWindowSample(capture, "waitingForChooserItem", sampledWindows)
            menuItem = findDescription(sampledWindows, menuItemDescription)
            if (menuItem == null) SystemClock.sleep(25)
        }

        val result = JSONObject()
            .put("trial", trial)
            .put("effects", "off")
            .put("selector", menuItemDescription)
            .put("inputStartElapsedRealtimeNanos", action.startNanos)
            .put("timeoutMs", timeoutMs)
            .put("chooserTap", chooserTouch)
            .put("firstPostTapScreenshot", firstScreenshot)
            .put("windowSamples", capture.windowSamples)
            .put("accessibilityEvents", capture.events)
        if (menuItem == null) {
            action.timedOutAtNanos = SystemClock.elapsedRealtimeNanos()
            result.put("status", "CHOOSER_ITEM_MISSING")
                .put("classification", "awaiting-screenshot-and-window-tree-review")
                .put("failureElapsedRealtimeNanos", action.timedOutAtNanos)
                .put("failureWindowTree", windowSummary(snapshotWindows()))
                .put("failureScreenshot", captureScreenshot(capture, "chooser-item-timeout"))
            pending = null
            activeDiagnostic = null
            persistResults(JSONArray())
            return result
        }

        captureWindowSample(capture, "chooserItemVisible", snapshotWindows())
        result.put("chooserItemVisibleElapsedRealtimeNanos", SystemClock.elapsedRealtimeNanos())
            .put("chooserItemNode", jsonNode(menuItem))
        val selectionTouch = diagnosticTap(menuItem.bounds.centerX().toFloat(), menuItem.bounds.centerY().toFloat(), action)
            .put("selector", menuItemDescription)
            .put("node", jsonNode(menuItem))
        result.put("selectionTap", selectionTouch)
        val remainingNanos = (chooserDeadlineNanos - SystemClock.elapsedRealtimeNanos()).coerceAtLeast(0)
        action.done.await(remainingNanos, TimeUnit.NANOSECONDS)
        if (action.confirmation == null) action.timedOutAtNanos = SystemClock.elapsedRealtimeNanos()
        result.put("status", if (action.confirmation != null) "CONFIRMED" else "DESTINATION_NOT_CONFIRMED")
            .put("events", JSONArray(synchronized(rawEvents) { rawEvents.toList() }))
            .put("confirmation", action.confirmation ?: JSONObject.NULL)
        action.confirmation?.optLong("confirmationElapsedRealtimeNanos")?.takeIf { it > 0 }?.let {
            result.put("confirmationElapsedRealtimeNanos", it)
                .put("latencyMs", (it - action.startNanos) / 1_000_000.0)
        }
        action.timedOutAtNanos?.let { result.put("timeoutEndElapsedRealtimeNanos", it) }
        if (action.confirmation == null) {
            result.put("failureWindowTree", windowSummary(snapshotWindows()))
                .put("failureScreenshot", captureScreenshot(capture, "destination-timeout"))
        }
        pending = null
        activeDiagnostic = null
        persistResults(JSONArray())
        return result
    }

    private fun diagnosticTap(x: Float, y: Float, action: Pending): JSONObject {
        val down = SystemClock.uptimeMillis()
        val record = JSONObject()
            .put("x", x)
            .put("y", y)
            .put("downUptimeMillis", down)
        val downEvent = MotionEvent.obtain(down, down, MotionEvent.ACTION_DOWN, x, y, 0)
        downEvent.source = InputDevice.SOURCE_TOUCHSCREEN
        try {
            if (action.startNanos == 0L) action.startNanos = SystemClock.elapsedRealtimeNanos()
            if (action.inputDownUptimeMs == 0L) action.inputDownUptimeMs = down
            record.put("actionStartElapsedRealtimeNanos", action.startNanos)
                .put("injectionAttemptElapsedRealtimeNanos", SystemClock.elapsedRealtimeNanos())
                .put("motionEventSource", downEvent.source)
            val downInjected = automation.injectInputEvent(downEvent, true)
            record.put("downInjected", downInjected)
            SystemClock.sleep(32)
            val upTime = SystemClock.uptimeMillis()
            val upEvent = MotionEvent.obtain(down, upTime, MotionEvent.ACTION_UP, x, y, 0)
            upEvent.source = InputDevice.SOURCE_TOUCHSCREEN
            try {
                val upInjected = automation.injectInputEvent(upEvent, true)
                record.put("upInjected", upInjected)
                    .put("upUptimeMillis", upTime)
                    .put("upMotionEventSource", upEvent.source)
            } finally {
                upEvent.recycle()
            }
        } catch (error: Throwable) {
            record.put("injectionError", error.message ?: error.javaClass.name)
        } finally {
            downEvent.recycle()
        }
        return record
    }

    private fun captureWindowSample(capture: DiagnosticCapture, phase: String, windows: List<WindowSnapshot>) {
        val sample = JSONObject()
            .put("phase", phase)
            .put("elapsedRealtimeNanos", SystemClock.elapsedRealtimeNanos())
            .put("uptimeMillis", SystemClock.uptimeMillis())
            .put("windows", windowSummary(windows))
        synchronized(capture) { capture.windowSamples.put(sample) }
    }

    private fun captureScreenshot(capture: DiagnosticCapture, label: String): JSONObject {
        val capturedAt = SystemClock.elapsedRealtimeNanos()
        val bitmap = automation.takeScreenshot()
            ?: return JSONObject().put("label", label).put("error", "UiAutomation.takeScreenshot returned null")
        return try {
            val context = instrumentation.targetContext
            val name = "211-sq10-chooser-trial-${capture.trial}-$label.png"
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, name)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/OxygenWX/211-sq10-static-full-dry-run-method-validation/input-probes")
            }
            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: error("Unable to create screenshot media record")
            context.contentResolver.openOutputStream(uri, "w")?.use { stream ->
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)) { "Screenshot PNG compression failed" }
            } ?: error("Unable to open screenshot output stream")
            val verification = context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
                ?: error("Screenshot was not readable after capture")
            check(verification.width == bitmap.width && verification.height == bitmap.height) {
                "Screenshot dimensions changed after capture"
            }
            verification.recycle()
            JSONObject()
                .put("label", label)
                .put("uri", uri.toString())
                .put("elapsedRealtimeNanos", capturedAt)
                .put("width", bitmap.width)
                .put("height", bitmap.height)
        } catch (error: Throwable) {
            JSONObject()
                .put("label", label)
                .put("elapsedRealtimeNanos", capturedAt)
                .put("error", error.message ?: error.javaClass.name)
        } finally {
            bitmap.recycle()
        }
    }

    private fun runAppearance(effects: String): JSONObject {
        val actions = JSONArray()
        actions.put(record(pageChange("$effects-now-hourly", "Now", "Hourly")))
        actions.put(record(pageChange("$effects-hourly-daily", "Hourly", "Daily")))
        actions.put(record(pageChange("$effects-daily-details", "Daily", "Details")))
        actions.put(record(pageChange("$effects-details-now", "Details", "Now")))

        ensurePage("Now")
        ensurePage("Hourly")
        actions.put(record(changedRange("$effects-hourly-later", "Hourly")))
        ensurePage("Daily")
        val dailyResult = try {
            dailyLater("$effects-daily-later")
        } catch (error: Throwable) {
            val raw = actionResults.lastOrNull { it.optString("id") == "$effects-daily-later" }
            JSONObject().put("id", "$effects-daily-later").put("status", "UNVERIFIED")
                .put("reason", error.message ?: error.javaClass.name)
                .put("rawObserver", raw ?: JSONObject.NULL)
        }
        actions.put(record(dailyResult))

        ensurePage("Now")
        actions.put(record(safeObserve("$effects-home-menu-open", { windows -> findText(windows, "Settings") }) {
            tapDescription("Home actions")
        }))
        actions.put(record(safeObserve("$effects-home-menu-close", { windows ->
            val home = findDescription(windows, "Choose Home page, current: Now")
            if (home != null && findText(windows, "Settings") == null) home else null
        }) { pressBack() }))
        actions.put(record(safeObserve("$effects-settings-open", { windows ->
            val returnControl = findText(windows, "Return to Home") ?: return@safeObserve null
            findText(windows, "Settings")?.takeIf { it.windowId == returnControl.windowId }
        }) {
            tapDescription("Home actions")
            tapText("Settings")
        }))
        val returnControl = findText(snapshotWindows(), "Return to Home")
        val returnResult = if (returnControl == null) {
            JSONObject().put("id", "$effects-settings-return").put("status", "UNVERIFIED")
                .put("reason", "Return to Home control was not present after Settings open")
        } else safeObserve("$effects-settings-return", { windows ->
            findDescription(windows, "Choose Home page, current: Now")
        }) { tapText("Return to Home") }
        actions.put(record(returnResult))
        return JSONObject().put("effects", effects).put("actions", actions)
    }

    private fun safeObserve(
        id: String,
        expected: (List<WindowSnapshot>) -> VisibleNode?,
        input: () -> Unit,
    ): JSONObject = try {
        observe(id, expected, input)
    } catch (error: Throwable) {
        val raw = actionResults.lastOrNull { it.optString("id") == id }
        JSONObject().put("id", id).put("status", "UNVERIFIED")
            .put("reason", error.message ?: error.javaClass.name)
            .put("rawObserver", raw ?: JSONObject.NULL)
    }

    private fun pageChange(id: String, from: String, to: String): JSONObject {
        ensurePage(from)
        return observe(id, { windows -> findDescription(windows, "Choose Home page, current: $to") }) {
            tapDescription("Choose Home page, current: $from")
            tapDescription("$to page, ${pageIndex(to)} of 4, not selected")
        }
    }

    private fun changedRange(id: String, page: String): JSONObject {
        ensurePage(page)
        val beforeWindows = snapshotWindows()
        val before = findRange(beforeWindows, page) ?: run {
            diagnostics += JSONObject()
                .put("phase", "rangeBefore")
                .put("page", page)
                .put("windows", windowSummary(beforeWindows))
            persistResults(JSONArray())
            error("No visible $page range label before input")
        }
        return observe(id, { windows ->
            findRange(windows, page)?.takeIf { it.text != before.text }
        }) {
            tapDescription("Later")
        }.put("rangeBefore", before.text)
    }

    private fun dailyLater(id: String): JSONObject {
        val setup = JSONObject().put("id", id).put("gate", "INPUT_REACHABILITY_ERROR")
        diagnostics += setup
        val initial = snapshotWindows()
        val app = initial.singleOrNull { it.packageName == targetPackage && it.active && it.focused && !it.fromFallback }
            ?: error("INPUT_REACHABILITY_ERROR: active Daily app window unavailable")
        check(findDescription(listOf(app), "Choose Home page, current: Daily") != null) {
            "INPUT_REACHABILITY_ERROR: named Daily unavailable"
        }
        val scroll = app.text.firstOrNull { it.className?.contains("ScrollView") == true &&
            it.bounds.contains(540, 750) && it.bounds.contains(540, 1500) }
            ?: error("INPUT_REACHABILITY_ERROR: verified Daily scroll bounds unavailable")
        check(display.x == 1080 && display.y == 1920 && scroll.bounds == Rect(0, 411, 1080, 1864)) {
            "INPUT_REACHABILITY_ERROR: viewport or Daily scroll bounds changed: ${scroll.bounds}"
        }
        val firstRange = findRange(listOf(app), "Daily")?.text
            ?: error("ENDPOINT_SETUP_ERROR: first Daily range unavailable")
        val unit = UnitPresetSelection(SharedPreferencesUnitPresetStore(instrumentation.targetContext)).effectivePreset
        val fixture = DemoWeatherRepository.load(LocalDateTime.now())
        val mapped = HomePresentationMapper.map(fixture, HistoricalSynthesis.derive(fixture), unit).dailyWindows
        check(mapped.size == 2 && mapped.all { it.entries.size == 5 }) { "ENDPOINT_SETUP_ERROR: expected two five-row Daily windows" }
        val first = mapped[0].entries.map { it.spokenSummary }
        val second = mapped[1].entries.map { it.spokenSummary }
        check(firstRange == "TODAY–SUN" && firstRange == mapped[0].rangeLabel) {
            "ENDPOINT_SETUP_ERROR: installed first range differs from frozen compact fixture: $firstRange"
        }
        val initialRows = app.text.filter { it.isVisibleDailySummary(scroll.bounds) }
        check(initialRows.isNotEmpty() && initialRows.all { it.description in first && first.count { summary -> summary == it.description } == 1 }) {
            "ENDPOINT_SETUP_ERROR: visible first-window rows differ from mapped fixture"
        }
        val observedFirstRows = initialRows.mapNotNull { it.description }.toMutableSet()
        setup.put("firstRange", firstRange).put("secondRange", mapped[1].rangeLabel)
            .put("firstWindowRows", JSONArray(first)).put("secondWindowRows", JSONArray(second))
            .put("initialWindows", windowSummary(initial)).put("scrollBounds", JSONArray(listOf(scroll.bounds.left, scroll.bounds.top, scroll.bounds.right, scroll.bounds.bottom)))
            .put("initialScreenshot", dailyScreenshot("$id-before-swipe"))
        var ready: WindowSnapshot? = null
        var later: VisibleNode? = null
        for (attempt in 0..2) {
            val windows = snapshotWindows()
            val active = windows.singleOrNull { it.id == app.id && it.packageName == targetPackage && it.active && it.focused }
                ?: error("INPUT_REACHABILITY_ERROR: Daily window lost during setup")
            check(findDescription(listOf(active), "Choose Home page, current: Daily") != null) {
                "INPUT_REACHABILITY_ERROR: named Daily changed during setup"
            }
            val visibleRows = active.text.filter { it.isVisibleDailySummary(scroll.bounds) }
            check(visibleRows.isNotEmpty() && visibleRows.all { it.description in first && first.count { summary -> summary == it.description } == 1 }) {
                "ENDPOINT_SETUP_ERROR: first-window row identity changed before Later tap"
            }
            observedFirstRows += visibleRows.mapNotNull { it.description }
            findRange(listOf(active), "Daily")?.text?.let { check(it == firstRange) { "INPUT_REACHABILITY_ERROR: Daily range changed during setup" } }
            // Cycle 210-D observed this visible enabled node as clickable=false. The reviewed
            // cycle 211 protocol therefore uses only the predeclared point, after checking the
            // point is inside the live node, Daily content, and the current active app window.
            later = active.text.firstOrNull { it.description == "Later" && it.enabled &&
                it.bounds.contains(794, 1721) && scroll.bounds.contains(794, 1721) &&
                intersectsDisplay(it.bounds) }
            setup.put("snapshot$attempt", windowSummary(windows))
                .put("screenshot$attempt", dailyScreenshot("$id-after-$attempt-swipes"))
            if (later != null) { ready = active; break }
            if (attempt == 2) break
            val swipe = injectSetupSwipe(1500f, 750f)
            if (attempt == 0) setup.put("setupStartElapsedRealtimeNanos", swipe.getLong("startElapsedRealtimeNanos"))
            setup.put("swipe${attempt + 1}", swipe)
            persistResults(JSONArray())
        }
        val target = later ?: error("INPUT_REACHABILITY_ERROR: Later absent after two bounded swipes")
        val finalWindow = ready ?: error("INPUT_REACHABILITY_ERROR: final Daily window unavailable")
        check(first.toSet() == observedFirstRows) {
            "ENDPOINT_SETUP_ERROR: installed first-window summaries do not equal all five frozen fixture rows"
        }
        val finalRange = findRange(listOf(finalWindow), "Daily")?.text
        check(finalRange == mapped[0].rangeLabel) { "INPUT_REACHABILITY_ERROR: first range changed before tap: $finalRange" }
        val obstructions = snapshotWindows().filter { it.id != finalWindow.id && it.layer > finalWindow.layer }
            .flatMap { window -> window.text.filter { it.bounds.contains(794, 1721) }.map { window.packageName to it } }
        check(obstructions.isEmpty()) { "INPUT_REACHABILITY_ERROR: planned tap point is covered by another window" }
        val finalRows = finalWindow.text.filter { it.isVisibleDailySummary(scroll.bounds) }
        val chosen = finalRows.maxWithOrNull(compareBy<VisibleNode> { it.bounds.bottom }.thenBy { it.bounds.top })
            ?: error("ENDPOINT_SETUP_ERROR: no on-display first-window row at Later readiness")
        val slot = first.indexOf(chosen.description)
        val predicted = second[slot]
        check(slot >= 0 && first.count { it == chosen.description } == 1 && predicted !in first &&
            (first + second).count { it == predicted } == 1) {
            "ENDPOINT_SETUP_ERROR: same-slot next-window summary is not unique"
        }
        setup.put("selectedSlot", slot).put("selectedFirstRow", jsonNode(chosen))
            .put("predictedNextRow", predicted).put("predictedCountAcrossTen", 1)
            .put("laterTarget", jsonNode(target)).put("plannedTapPointPx", JSONArray(listOf(794, 1721)))
            .put("gate", "REACHABLE")
        persistResults(JSONArray())
        val result = try {
            observe(id, { windows -> findDescription(windows, predicted) }) {
            val tapWindows = snapshotWindows()
            val tapWindow = tapWindows.singleOrNull { it.id == finalWindow.id && it.packageName == targetPackage &&
                it.active && it.focused && !it.fromFallback }
                ?: error("INPUT_REACHABILITY_ERROR: active Daily window changed before tap")
            check(findDescription(listOf(tapWindow), "Choose Home page, current: Daily") != null &&
                findRange(listOf(tapWindow), "Daily")?.text == mapped[0].rangeLabel) {
                "INPUT_REACHABILITY_ERROR: Daily identity or exact first range changed before tap"
            }
            val liveTarget = tapWindow.text.singleOrNull { it.description == "Later" && it.enabled &&
                it.bounds.contains(794, 1721) && scroll.bounds.contains(794, 1721) && intersectsDisplay(it.bounds) }
                ?: error("INPUT_REACHABILITY_ERROR: planned point no longer hits one enabled visible Later target")
            check(tapWindows.filter { it.id != tapWindow.id && it.layer > tapWindow.layer }
                .none { window -> window.text.any { it.bounds.contains(794, 1721) } }) {
                "INPUT_REACHABILITY_ERROR: planned tap point became obscured before tap"
            }
            setup.put("immediatePreTapTarget", jsonNode(liveTarget))
                .put("preTapReadyElapsedRealtimeNanos", SystemClock.elapsedRealtimeNanos())
            injectTap(794f, 1721f)
            }
        } catch (error: Throwable) {
            val raw = actionResults.lastOrNull { it.optString("id") == id }
            setup.put("rowEndpoint", "UNVERIFIED")
                .put("rowEndpointReason", error.message ?: error.javaClass.name)
            JSONObject().put("id", id).put("status", "UNVERIFIED")
                .put("reason", error.message ?: error.javaClass.name)
                .put("rawObserver", raw ?: JSONObject.NULL)
        }
        if (result.optString("status") == "CONFIRMED") {
            setup.put("rowEndpoint", "CONFIRMED").put("tapStartElapsedRealtimeNanos", result.optLong("startElapsedRealtimeNanos"))
                .put("confirmation", result.optJSONObject("confirmation") ?: JSONObject.NULL)
        }
        var range = findRange(snapshotWindows(), "Daily")?.text
        if (range == null) {
            setup.put("rangeInspectionSwipe", injectSetupSwipe(750f, 1500f))
            range = findRange(snapshotWindows(), "Daily")?.text
        }
        val postTapWindows = snapshotWindows()
        val postTapDaily = postTapWindows.singleOrNull { it.packageName == targetPackage && it.active && it.focused &&
            findDescription(listOf(it), "Choose Home page, current: Daily") != null }
        val postTapRows = postTapDaily?.text?.filter { it.isVisibleDailySummary(scroll.bounds) }?.mapNotNull { it.description }.orEmpty()
        val secondIndexes = postTapRows.map { second.indexOf(it) }
        val rangeMatches = range == mapped[1].rangeLabel
        val rowsMatch = postTapRows.isNotEmpty() && secondIndexes.all { it >= 0 } &&
            secondIndexes.zipWithNext().all { (before, after) -> before < after }
        setup.put("independentRange", range ?: JSONObject.NULL)
            .put("postTapVisibleRows", JSONArray(postTapRows))
            .put("rangeGate", if (rangeMatches && rowsMatch) "CONFIRMED" else "UNVERIFIED")
            .put("rangeGateReason", if (!rangeMatches) "expected ${mapped[1].rangeLabel}, observed $range" else if (!rowsMatch) "next-window visible row order unavailable or mismatched" else JSONObject.NULL)
        persistResults(JSONArray())
        return result.put("rangeBefore", firstRange).put("rangeAfter", range ?: JSONObject.NULL)
    }

    private fun Rect.intersectCopy(other: Rect): Boolean = Rect(this).intersect(other)

    private fun VisibleNode.isVisibleDailySummary(scrollBounds: Rect): Boolean =
        description?.contains(", low ") == true && description.contains(", high ") &&
            description.contains(", precipitation ") && bounds.intersectCopy(scrollBounds)

    private fun dailyScreenshot(label: String): JSONObject {
        val result = JSONObject().put("label", label).put("elapsedRealtimeNanos", SystemClock.elapsedRealtimeNanos())
        val bitmap = automation.takeScreenshot() ?: return result.put("error", "Screenshot unavailable")
        return try {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$label.png")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/OxygenWX/211-sq10-static-full-dry-run-method-validation/daily-reachability")
            }
            val uri = instrumentation.targetContext.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: error("Screenshot record unavailable")
            instrumentation.targetContext.contentResolver.openOutputStream(uri, "w")?.use {
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it))
            } ?: error("Screenshot stream unavailable")
            val verification = instrumentation.targetContext.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it)
            } ?: error("Screenshot was not readable after capture")
            check(verification.width == bitmap.width && verification.height == bitmap.height) {
                "Screenshot dimensions changed after capture"
            }
            verification.recycle()
            result.put("uri", uri.toString()).put("width", bitmap.width).put("height", bitmap.height)
        } catch (error: Throwable) {
            result.put("error", error.message ?: error.javaClass.name)
        } finally { bitmap.recycle() }
    }

    private fun captureRunScreenshot(label: String): JSONObject {
        val capturedAt = SystemClock.elapsedRealtimeNanos()
        val bitmap = automation.takeScreenshot()
            ?: return JSONObject().put("label", label).put("error", "UiAutomation.takeScreenshot returned null")
        return try {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "211-${label.replace(Regex("[^A-Za-z0-9_-]"), "-")}.png")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/OxygenWX/211-sq10-static-full-dry-run-method-validation/actions")
            }
            val resolver = instrumentation.targetContext.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: error("Unable to create action screenshot")
            resolver.openOutputStream(uri, "w")?.use {
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) { "Action screenshot compression failed" }
            } ?: error("Unable to open action screenshot")
            val decoded = resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
                ?: error("Action screenshot was not readable after capture")
            check(decoded.width == bitmap.width && decoded.height == bitmap.height) { "Action screenshot dimensions changed" }
            decoded.recycle()
            JSONObject().put("uri", uri.toString()).put("elapsedRealtimeNanos", capturedAt)
                .put("width", bitmap.width).put("height", bitmap.height)
        } catch (error: Throwable) {
            JSONObject().put("label", label).put("elapsedRealtimeNanos", capturedAt)
                .put("error", error.message ?: error.javaClass.name)
        } finally { bitmap.recycle() }
    }

    private fun injectSetupSwipe(startY: Float, endY: Float): JSONObject =
        injectSetupSwipe(540f, startY, endY)

    private fun injectSetupSwipe(x: Float, startY: Float, endY: Float): JSONObject {
        check(pending == null) { "Setup swipe must precede measured observation" }
        val start = SystemClock.uptimeMillis()
        val trace = JSONObject().put("source", InputDevice.SOURCE_TOUCHSCREEN)
            .put("x", x).put("startY", startY).put("endY", endY)
            .put("downUptimeMs", start).put("startElapsedRealtimeNanos", SystemClock.elapsedRealtimeNanos())
        for (step in 0..10) {
            val uptime = if (step == 0) start else SystemClock.uptimeMillis()
            val action = when (step) { 0 -> MotionEvent.ACTION_DOWN; 10 -> MotionEvent.ACTION_UP; else -> MotionEvent.ACTION_MOVE }
            val y = startY + (endY - startY) * step / 10f
            val event = MotionEvent.obtain(start, uptime, action, x, y, 0)
            event.source = InputDevice.SOURCE_TOUCHSCREEN
            try {
                check(automation.injectInputEvent(event, true)) { "INPUT_REACHABILITY_ERROR: swipe event injection failed at step $step" }
            } finally { event.recycle() }
            trace.put("event$step", JSONObject().put("action", action).put("uptimeMs", uptime).put("y", y))
            if (step < 10) SystemClock.sleep(24)
        }
        SystemClock.sleep(120)
        return trace.put("upUptimeMs", SystemClock.uptimeMillis()).put("completeElapsedRealtimeNanos", SystemClock.elapsedRealtimeNanos())
    }

    private fun observe(
        id: String,
        expected: (List<WindowSnapshot>) -> VisibleNode?,
        input: () -> Unit,
    ): JSONObject {
        val action = Pending(id, expected)
        synchronized(rawEvents) { rawEvents.clear() }
        pending = action
        var inputError: Throwable? = null
        try {
            input()
        } catch (error: Throwable) {
            inputError = error
        }
        if (action.startNanos > 0) {
            val remainingNanos = (action.startNanos + TimeUnit.MILLISECONDS.toNanos(timeoutMs) - SystemClock.elapsedRealtimeNanos()).coerceAtLeast(0)
            if (inputError == null) action.done.await(remainingNanos, TimeUnit.NANOSECONDS)
        }
        val completed = action.confirmation != null
        if (!completed) action.timedOutAtNanos = SystemClock.elapsedRealtimeNanos()
        pending = null
        val result = JSONObject()
            .put("id", id)
            .put("startElapsedRealtimeNanos", action.startNanos)
            .put("inputEvents", action.inputTraces)
            .put("timeoutMs", timeoutMs)
            .put("status", when {
                completed -> "CONFIRMED"
                inputError != null -> "INPUT_ERROR"
                action.startNanos == 0L -> "NO_INPUT"
                else -> "TIMEOUT"
            })
            .put("events", JSONArray(synchronized(rawEvents) { rawEvents.toList() }))
        inputError?.let { result.put("reason", it.message ?: it.javaClass.name) }
        action.confirmation?.let { result.put("confirmation", it) }
        action.timedOutAtNanos?.let { result.put("timeoutEndElapsedRealtimeNanos", it) }
        action.confirmation?.optLong("confirmationElapsedRealtimeNanos")?.takeIf { it > 0 }?.let {
            val observedElapsedMs = (it - action.startNanos) / 1_000_000.0
            if (observedElapsedMs <= 500.0) {
                result.put("latencyMs", observedElapsedMs)
                    .put("semanticTimingDisposition", "VALID_UNSCORED")
            } else {
                result.put("rawDiagnosticElapsedMs", observedElapsedMs)
                    .put("semanticTimingDisposition", "UNVERIFIED")
                    .put("semanticTimingReason", "eligible event query completed after the unchanged 500 ms threshold")
            }
        }
        actionResults += result
        persistResults(JSONArray())
        if (!completed) error("Observer action $id failed: ${result.optString("status")} ${result.optString("reason")}")
        return result
    }

    private fun record(result: JSONObject): JSONObject {
        if (actionResults.lastOrNull() !== result) actionResults += result
        if (!result.has("postActionWindows")) result.put("postActionWindows", windowSummary(snapshotWindows()))
        if (!result.has("postActionScreenshot")) result.put("postActionScreenshot", captureRunScreenshot(result.optString("id")))
        persistResults(JSONArray())
        if (result.optString("status") !in setOf("CONFIRMED", "UNVERIFIED")) {
            error("Observer action ${result.optString("id")} failed")
        }
        return result
    }

    private fun persistResults(appearances: JSONArray) {
        if (!::outputFile.isInitialized) return
        runCatching {
            if (appearances.length() > 0) lastAppearances = appearances
            outputFile.parentFile?.mkdirs()
            val output = JSONObject()
                    .put("protocol", "SQ.10 test-path observability; unscored")
                    .put("diagnosticMode", InstrumentationRegistry.getArguments().getString("diagnosticMode") ?: "daily-reachability-or-fixed-set")
                    .put("timeoutMs", timeoutMs)
                    .put("displayPx", JSONArray(listOf(display.x, display.y)))
                    .put("appearances", lastAppearances)
                    .put("trialPaths", lastAppearances)
                    .put("diagnosticTrials", diagnosticTrials)
                    .put("actions", JSONArray(actionResults))
                    .put("diagnostics", JSONArray(diagnostics))
                    .toString(2)
            outputFile.writeText(output)
            outputUri?.let { uri ->
                outputFile.inputStream().use { input ->
                    instrumentation.targetContext.contentResolver.openOutputStream(uri, "wt")?.use { outputStream ->
                        input.copyTo(outputStream)
                    } ?: error("Unable to write persistent observer output")
                }
            }
        }
    }

    private fun onAccessibilityEvent(event: AccessibilityEvent) {
        val eventCallbackNanos = SystemClock.elapsedRealtimeNanos()
        val eventCallbackUptime = SystemClock.uptimeMillis()
        val eventPackage = event.packageName?.toString()
        val diagnostic = activeDiagnostic
        val diagnosticWindows = if (diagnostic != null) snapshotWindows() else null
        if (diagnostic != null) {
            val source = runCatching { event.source }.getOrNull()
            val sourceBounds = source?.let { Rect().also(it::getBoundsInScreen) }
            val diagnosticEvent = JSONObject()
                .put("eventType", event.eventType)
                .put("eventTimeUptimeMs", event.eventTime)
                .put("eventPackage", eventPackage ?: JSONObject.NULL)
                .put("eventWindowId", event.windowId)
                .put("eventClass", event.className?.toString() ?: JSONObject.NULL)
                .put("eventText", JSONArray(event.text.map { it.toString() }))
                .put("eventContentDescription", event.contentDescription?.toString() ?: JSONObject.NULL)
                .put("callbackElapsedRealtimeNanos", eventCallbackNanos)
                .put("callbackUptimeMillis", eventCallbackUptime)
                .put("eventSource", if (source == null) JSONObject.NULL else JSONObject()
                    .put("packageName", source.packageName?.toString() ?: JSONObject.NULL)
                    .put("windowId", source.windowId)
                    .put("text", source.text?.toString() ?: JSONObject.NULL)
                    .put("contentDescription", source.contentDescription?.toString() ?: JSONObject.NULL)
                    .put("visibleToUser", source.isVisibleToUser)
                    .put("boundsInScreen", sourceBounds?.let {
                        JSONArray(listOf(it.left, it.top, it.right, it.bottom))
                    } ?: JSONObject.NULL))
                .put("windows", windowSummary(diagnosticWindows.orEmpty()))
            synchronized(diagnostic) { diagnostic.events.put(diagnosticEvent) }
            runCatching { source?.recycle() }
        }
        val current = pending
        if (current == null) return
        val windows = diagnosticWindows ?: snapshotWindows()
        val eventWindow = windows.firstOrNull { it.id == event.windowId }
        val candidate = eventWindow?.let { current.expected(listOf(it)) }
        val queryDoneNanos = SystemClock.elapsedRealtimeNanos()
        val (reason, match) = eligibility(
            EventFacts(eventPackage, event.eventType, event.eventTime, eventCallbackNanos, queryDoneNanos, event.windowId),
            current.startNanos, current.inputDownUptimeMs, windows, candidate,
        )
        val window = windows.firstOrNull { it.id == event.windowId }
        val record = JSONObject()
            .put("eventType", event.eventType)
            .put("eventTimeUptimeMs", event.eventTime)
            .put("eventPackage", eventPackage ?: JSONObject.NULL)
            .put("eventWindowId", event.windowId)
            .put("eventClass", event.className?.toString())
            .put("eventText", JSONArray(event.text.map { it.toString() }))
            .put("eventContentDescription", event.contentDescription?.toString())
            .put("callbackElapsedRealtimeNanos", eventCallbackNanos)
            .put("eventDeliveryDelayUptimeMs", (eventCallbackUptime - event.eventTime).coerceAtLeast(0))
            .put("queryCompleteElapsedRealtimeNanos", queryDoneNanos)
            .put("queryDelayMs", (queryDoneNanos - eventCallbackNanos) / 1_000_000.0)
            .put("eventWindowLayer", window?.layer ?: JSONObject.NULL)
            .put("eventWindowPackage", window?.packageName ?: JSONObject.NULL)
            .put("eventWindowActive", window?.active ?: JSONObject.NULL)
            .put("eventWindowFocused", window?.focused ?: JSONObject.NULL)
            .put("inputDownUptimeMs", current.inputDownUptimeMs)
            .put("eligibilityReason", reason)
            .put("windows", jsonWindows(windows))
            .put("matchedNode", match?.let(::jsonNode) ?: JSONObject.NULL)
        synchronized(rawEvents) { rawEvents += record }
        if (reason == "ELIGIBLE" && match != null && current.confirmation == null) {
            current.confirmation = JSONObject()
                .put("confirmationElapsedRealtimeNanos", queryDoneNanos)
                .put("eventCallbackElapsedRealtimeNanos", eventCallbackNanos)
                .put("eventType", event.eventType)
                .put("eventTimeUptimeMs", event.eventTime)
                .put("eventPackage", eventPackage ?: JSONObject.NULL)
                .put("eventWindowId", event.windowId)
                .put("windowLayer", window?.layer ?: JSONObject.NULL)
                .put("node", jsonNode(match))
                .put("queryDelayMs", (queryDoneNanos - eventCallbackNanos) / 1_000_000.0)
                .put("eventDeliveryDelayUptimeMs", (eventCallbackUptime - event.eventTime).coerceAtLeast(0))
            current.done.countDown()
        }
    }

    private fun snapshotWindows(): List<WindowSnapshot> {
        val windows = runCatching { automation.windows }.getOrDefault(emptyList())
        val snapshots = windows.mapNotNull { window ->
            val root = runCatching { window.root }.getOrNull() ?: return@mapNotNull null
            val nodes = mutableListOf<VisibleNode>()
            val windowBounds = Rect().also { runCatching { window.getBoundsInScreen(it) } }
            collectNodes(root, window.id, window.layer, nodes, 0, emptyList())
            WindowSnapshot(
                id = window.id,
                layer = window.layer,
                packageName = root.packageName?.toString(),
                active = window.isActive,
                focused = window.isFocused,
                text = nodes,
                bounds = windowBounds.takeUnless { it.isEmpty },
            )
        }
        if (snapshots.isNotEmpty()) return snapshots
        val root = runCatching { automation.rootInActiveWindow }.getOrNull() ?: return emptyList()
        val nodes = mutableListOf<VisibleNode>()
        collectNodes(root, root.windowId, -1, nodes, 0, emptyList())
        return listOf(
            WindowSnapshot(root.windowId, -1, root.packageName?.toString(), true, true, nodes, fromFallback = true,
                bounds = Rect(0, 0, display.x, display.y).takeUnless { it.isEmpty }),
        )
    }

    private fun collectNodes(
        node: AccessibilityNodeInfo,
        windowId: Int,
        layer: Int,
        output: MutableList<VisibleNode>,
        depth: Int,
        scrollableAncestors: List<Rect>,
    ) {
        if (depth > 40) return
        if (node.isVisibleToUser) {
            val bounds = Rect().also(node::getBoundsInScreen)
            if (bounds.width() > 0 && bounds.height() > 0) {
                output += VisibleNode(
                    windowId = windowId,
                    layer = layer,
                    text = node.text?.toString(),
                    description = node.contentDescription?.toString(),
                    bounds = bounds,
                    clickable = node.isClickable,
                    enabled = node.isEnabled,
                    className = node.className?.toString(),
                    scrollableAncestors = scrollableAncestors.map(::Rect),
                )
            }
        }
        val nextAncestors = if (node.isScrollable && node.isVisibleToUser) {
            val bounds = Rect().also(node::getBoundsInScreen)
            if (bounds.isEmpty) scrollableAncestors else scrollableAncestors + bounds
        } else scrollableAncestors
        for (index in 0 until node.childCount) {
            runCatching { node.getChild(index) }.getOrNull()?.let {
                collectNodes(it, windowId, layer, output, depth + 1, nextAncestors)
            }
        }
    }

    private fun findDescription(windows: List<WindowSnapshot>, expected: String): VisibleNode? =
        windows.asSequence().filter { it.packageName == targetPackage }.flatMap { it.text.asSequence() }
            .firstOrNull { it.description == expected && intersectsDisplay(it.bounds) }

    private fun findText(windows: List<WindowSnapshot>, expected: String): VisibleNode? =
        windows.asSequence().filter { it.packageName == targetPackage }.flatMap { it.text.asSequence() }
            .firstOrNull { it.text == expected && intersectsDisplay(it.bounds) }

    private fun findRange(windows: List<WindowSnapshot>, page: String): VisibleNode? {
        val pageIdentity = "Choose Home page, current: $page"
        val identity = findDescription(windows, pageIdentity) ?: return null
        val ranges = windows.asSequence().filter { it.id == identity.windowId }.flatMap { it.text.asSequence() }
            .filter { intersectsDisplay(it.bounds) }
            .filter { node ->
                val value = node.text.orEmpty()
                when (page) {
                    "Hourly" -> value.matches(Regex(".*\\d{1,2} [AP]M–.*"))
                    "Daily" -> value.matches(Regex("(?i)(TODAY|[A-Z]{3,}).*–.*"))
                    else -> false
                }
            }
            .sortedBy { it.bounds.top }
        return ranges.firstOrNull()
    }

    private fun ensurePage(page: String) {
        val current = currentPage() ?: error("Home page identity is unavailable before setting up $page")
        if (current == page) return
        tapDescription("Choose Home page, current: $current")
        tapDescription("$page page, ${pageIndex(page)} of 4, not selected")
        waitFor { findDescription(snapshotWindows(), "Choose Home page, current: $page") != null }
    }

    private fun currentPage(): String? = listOf("Now", "Hourly", "Daily", "Details")
        .firstOrNull { findDescription(snapshotWindows(), "Choose Home page, current: $it") != null }

    private fun tapDescription(description: String) = tapNode("contentDescription=$description") { it.description == description }
    private fun tapText(text: String) = tapNode("visibleText=$text") { it.text == text || it.description == text }

    private fun tapNode(selector: String, predicate: (VisibleNode) -> Boolean) {
        fun lookup(): VisibleNode? = snapshotWindows().asSequence().filter { it.packageName == targetPackage }
            .flatMap { it.text.asSequence() }.firstOrNull { predicate(it) && intersectsDisplay(it.bounds) }
        var node = lookup()
        val deadline = SystemClock.elapsedRealtime() + timeoutMs
        while (node == null && SystemClock.elapsedRealtime() < deadline) {
            SystemClock.sleep(20)
            node = lookup()
        }
        if (node == null) {
            val summary = windowSummary(snapshotWindows())
            diagnostics += JSONObject().put("phase", "inputSelector").put("selector", selector).put("windows", summary)
            persistResults(JSONArray())
            error("No visible node matches $selector; windows=$summary")
        }
        injectTap(node.bounds.centerX().toFloat(), node.bounds.centerY().toFloat())
    }

    private fun injectTap(x: Float, y: Float, existingTrace: JSONObject? = null) {
        val down = SystemClock.uptimeMillis()
        val trace = existingTrace ?: JSONObject().put("source", InputDevice.SOURCE_TOUCHSCREEN)
            .put("pointPx", JSONArray(listOf(x, y))).put("downUptimeMs", down)
            .put("downElapsedRealtimeNanos", SystemClock.elapsedRealtimeNanos())
        if (existingTrace == null) pending?.inputTraces?.put(trace)
        val downEvent = MotionEvent.obtain(down, down, MotionEvent.ACTION_DOWN, x, y, 0)
        downEvent.source = InputDevice.SOURCE_TOUCHSCREEN
        try {
            pending?.let {
                if (it.startNanos == 0L) it.startNanos = SystemClock.elapsedRealtimeNanos()
                if (it.inputDownUptimeMs == 0L) it.inputDownUptimeMs = down
            }
            val downAccepted = automation.injectInputEvent(downEvent, true)
            trace.put("downAccepted", downAccepted)
            check(downAccepted) { "Touch down injection failed" }
            SystemClock.sleep(32)
            val up = SystemClock.uptimeMillis()
            trace.put("upUptimeMs", up).put("upElapsedRealtimeNanos", SystemClock.elapsedRealtimeNanos())
            val upEvent = MotionEvent.obtain(down, up, MotionEvent.ACTION_UP, x, y, 0)
            upEvent.source = InputDevice.SOURCE_TOUCHSCREEN
            try {
                val upAccepted = automation.injectInputEvent(upEvent, true)
                trace.put("upAccepted", upAccepted)
                check(upAccepted) { "Touch up injection failed" }
            } finally {
                upEvent.recycle()
            }
        } finally {
            downEvent.recycle()
        }
    }

    private fun pressBack() {
        val down = SystemClock.uptimeMillis()
        val trace = JSONObject().put("source", "KEYCODE_BACK").put("downUptimeMs", down)
            .put("downElapsedRealtimeNanos", SystemClock.elapsedRealtimeNanos())
        pending?.let {
            if (it.startNanos == 0L) it.startNanos = SystemClock.elapsedRealtimeNanos()
            if (it.inputDownUptimeMs == 0L) it.inputDownUptimeMs = down
            it.inputTraces.put(trace)
        }
        val downAccepted = automation.injectInputEvent(KeyEvent(down, down, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK, 0), true)
        trace.put("downAccepted", downAccepted)
        SystemClock.sleep(32)
        val up = SystemClock.uptimeMillis()
        trace.put("upUptimeMs", up).put("upElapsedRealtimeNanos", SystemClock.elapsedRealtimeNanos())
        trace.put("upAccepted", automation.injectInputEvent(KeyEvent(down, up, KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK, 0), true))
    }

    private fun waitFor(predicate: () -> Boolean) {
        val deadline = SystemClock.elapsedRealtime() + timeoutMs
        while (!predicate() && SystemClock.elapsedRealtime() < deadline) SystemClock.sleep(25)
    }

    private fun launchHome() {
        val context = instrumentation.targetContext
        val intent = Intent().setClassName(context, "com.oxygen.weather.MainActivity")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        waitFor { findDescription(snapshotWindows(), "Choose Home page, current: Now") != null }
        val windows = snapshotWindows()
        if (findDescription(windows, "Choose Home page, current: Now") == null) {
            diagnostics += JSONObject()
                .put("phase", "launchHome")
                .put("visibleNowIdentity", false)
                .put("windowSummary", windowSummary(windows))
            persistResults(JSONArray())
            error("Home page identity unavailable after launch; windows=${windowSummary(windows)}")
        }
    }

    private fun windowSummary(windows: List<WindowSnapshot>): JSONArray = JSONArray().apply {
        windows.forEach { window ->
            put(JSONObject()
                .put("id", window.id)
                .put("layer", window.layer)
                .put("packageName", window.packageName ?: JSONObject.NULL)
                .put("active", window.active)
                .put("focused", window.focused)
                .put("fromFallback", window.fromFallback)
                .put("nodes", JSONArray(window.text.map { jsonNode(it) })))
        }
    }

    private fun shell(command: String): String =
        instrumentation.uiAutomation.executeShellCommand(command).use { descriptor ->
            android.os.ParcelFileDescriptor.AutoCloseInputStream(descriptor).bufferedReader().readText()
        }

    private fun readPreference(file: String, key: String): String? =
        instrumentation.targetContext.getSharedPreferences(file, 0).getString(key, null)

    private fun restore(namespace: String, key: String, value: String) {
        if (value == "null" || value.isBlank()) return
        shell("settings put $namespace $key $value")
    }

    private fun restoreDisplay(kind: String, value: String) {
        when {
            value.startsWith("Physical size:") -> shell("wm size reset")
            value.startsWith("Override size:") -> shell("wm size ${value.substringAfter(':').trim()}")
            value.startsWith("Physical density:") -> shell("wm density reset")
            value.startsWith("Override density:") -> shell("wm density ${value.substringAfter(':').trim()}")
            else -> shell("wm $kind reset")
        }
    }

    private fun intersectsDisplay(bounds: Rect): Boolean =
        Rect(bounds).intersect(0, 0, display.x, display.y)

    private fun pageIndex(page: String): Int = when (page) {
        "Now" -> 1
        "Hourly" -> 2
        "Daily" -> 3
        "Details" -> 4
        else -> error("Unknown page $page")
    }

    private fun jsonNode(node: VisibleNode): JSONObject = JSONObject()
        .put("windowId", node.windowId)
        .put("layer", node.layer)
        .put("className", node.className ?: JSONObject.NULL)
        .put("text", node.text ?: JSONObject.NULL)
        .put("contentDescription", node.description ?: JSONObject.NULL)
        .put("clickable", node.clickable)
        .put("enabled", node.enabled)
        .put("boundsInScreen", rectJson(node.bounds))
        .put("scrollableAncestorBoundsInScreen", JSONArray(node.scrollableAncestors.map(::rectJson)))

    private fun jsonWindows(windows: List<WindowSnapshot>): JSONArray = JSONArray().apply {
        windows.forEach { window ->
            put(JSONObject()
                .put("id", window.id)
                .put("layer", window.layer)
                .put("packageName", window.packageName ?: JSONObject.NULL)
                .put("active", window.active)
                .put("focused", window.focused)
                .put("fromFallback", window.fromFallback)
                .put("boundsInScreen", window.bounds?.let(::rectJson) ?: JSONObject.NULL)
                .put("visibleNodeCount", window.text.size)
                .put("nodes", JSONArray(window.text.map(::jsonNode))))
        }
    }
}
