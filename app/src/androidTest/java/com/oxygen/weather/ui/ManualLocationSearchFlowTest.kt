package com.oxygen.weather.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.unit.LayoutDirection
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oxygen.weather.LocationSearchTestHooks
import com.oxygen.weather.MainActivity
import com.oxygen.weather.application.LocationSearchState
import com.oxygen.weather.data.locationsearch.LocationCandidate
import com.oxygen.weather.data.locationsearch.LocationSearch
import com.oxygen.weather.data.locationsearch.LocationSearchRequest
import com.oxygen.weather.data.locationsearch.LocationSearchResult
import com.oxygen.weather.data.provider.ForecastRequest
import com.oxygen.weather.ui.EffectsLevel
import java.io.File
import java.io.FileOutputStream
import java.time.ZoneId
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ManualLocationSearchFlowTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()

    private val calls = AtomicInteger()
    private val handoff = AtomicReference<ForecastRequest?>()
    private val handoffCalls = AtomicInteger()
    private val enteredSearch = CountDownLatch(1)
    private val releaseSearch = CountDownLatch(1)
    private val resultCandidate = LocationCandidate(
        providerId = 31,
        displayName = "Springfield",
        latitude = 39.7990175,
        longitude = -89.6439575,
        timeZone = ZoneId.of("America/Chicago"),
        admin1 = "Illinois",
        country = "United States",
        countryCode = "US",
    )
    private val otherCandidate = LocationCandidate(
        providerId = 32,
        displayName = "Springfield",
        latitude = 42.1014831,
        longitude = -72.589811,
        timeZone = ZoneId.of("America/New_York"),
        admin1 = "Massachusetts",
        country = "United States",
        countryCode = "US",
    )

    @Before
    fun launchRealActivityWithFakeSearch() {
        calls.set(0)
        handoffCalls.set(0)
        handoff.set(null)
        LocationSearchTestHooks.searchFactory = {
            LocationSearch { request ->
                calls.incrementAndGet()
                when (request.query) {
                    "empty" -> LocationSearchResult.NoResults
                    "failed" -> LocationSearchResult.Failure(LocationSearchResult.Category.HTTP_OR_PROVIDER)
                    "slow" -> {
                        enteredSearch.countDown()
                        releaseSearch.await(5, TimeUnit.SECONDS)
                        LocationSearchResult.Success(listOf(resultCandidate, otherCandidate))
                    }
                    "new" -> LocationSearchResult.Success(listOf(otherCandidate))
                    else -> LocationSearchResult.Success(listOf(resultCandidate, otherCandidate))
                }
            }
        }
        LocationSearchTestHooks.onSelectedRequest = { handoff.set(it); handoffCalls.incrementAndGet() }
        LocationSearchTestHooks.effectsOverrideForTests = EffectsLevel.OFF
        LocationSearchTestHooks.layoutDirectionOverrideForTests = LayoutDirection.Rtl
        compose.activityRule.scenario.recreate()
    }

    @After
    fun clearHooks() {
        releaseSearch.countDown()
        LocationSearchTestHooks.searchFactory = null
        LocationSearchTestHooks.onSelectedRequest = null
        LocationSearchTestHooks.effectsOverrideForTests = null
        LocationSearchTestHooks.layoutDirectionOverrideForTests = null
    }

    @Test
    fun actualActivityRouteShowsLoadingOrderedDisambiguatedResultsAndSelectsOnlyExplicitly() {
        selectHomePage("Daily")
        compose.onNodeWithContentDescription("Search for a place").assertIsDisplayed()
        assertTarget("location-search-entry")
        compose.onNodeWithContentDescription("Search for a place").performClick()
        compose.onNodeWithText("Search places").assertIsDisplayed()
        assertEquals(0, calls.get())

        compose.onNodeWithTag("location-search-query").performClick()
        compose.onNodeWithTag("location-search-query").performTextInput("slow")
        assertTarget("location-search-submit")
        compose.onNodeWithTag("location-search-submit").performClick()
        assertTrue("search fake was not entered", enteredSearch.await(5, TimeUnit.SECONDS))
        compose.onNodeWithTag("location-search-loading").assertIsDisplayed()
        assertEquals(1, calls.get())
        androidx.test.espresso.Espresso.pressBack()
        compose.waitForIdle()
        capture("search-loading-effects-off")

        releaseSearch.countDown()
        compose.waitUntil(10_000) {
            compose.onNodeWithText("Results for slow", substring = false).fetchSemanticsNodeOrNull() != null
        }
        compose.onAllNodesWithText("Springfield", substring = false).assertCountEquals(2)
        compose.onNodeWithText("Illinois · United States · US", substring = false).assertIsDisplayed()
        compose.onNodeWithContentDescription("Select Springfield, Illinois, United States").assertIsDisplayed()
        compose.onNodeWithContentDescription("Select Springfield, Massachusetts, United States").assertIsDisplayed()
        val firstResultTop = compose.onNodeWithContentDescription("Select Springfield, Illinois, United States").fetchSemanticsNode().boundsInRoot.top
        val secondResultTop = compose.onNodeWithContentDescription("Select Springfield, Massachusetts, United States").fetchSemanticsNode().boundsInRoot.top
        assertTrue("RTL must preserve provider result order", firstResultTop < secondResultTop)
        compose.onNodeWithText("Home · Daily", substring = false).assertIsDisplayed()
        assertEquals(null, handoff.get())
        assertTarget("location-search-cancel")
        compose.onNodeWithTag("location-search-result-0").performScrollTo()
        compose.onNodeWithTag("location-search-result-0").assertIsDisplayed()
        assertTarget("location-search-result-0")
        compose.onNodeWithTag("location-search-result-1").performScrollTo()
        compose.onNodeWithTag("location-search-result-1").assertIsDisplayed()
        assertTarget("location-search-result-1")
        capture("search-results-bottom-effects-off")
        compose.onNodeWithTag("location-search-result-0").performScrollTo()
        capture("search-results-effects-off")
        compose.onNodeWithTag("location-search-result-0").performClick()
        compose.waitForIdle()

        val request = handoff.get() ?: error("selection callback did not receive a request")
        assertEquals(resultCandidate.latitude, request.coordinates.latitude, 0.0)
        assertEquals(resultCandidate.longitude, request.coordinates.longitude, 0.0)
        assertEquals("Springfield", request.location.displayName)
        assertEquals(resultCandidate.timeZone, request.location.timeZone)
        assertTrue(request.location.id.value != resultCandidate.providerId.toString())
        assertEquals(72, request.coverage.hourlyHours)
        assertEquals(10, request.coverage.dailyDays)
        assertEquals(1, handoffCalls.get())
        assertEquals(1, calls.get())
        compose.onNodeWithContentDescription("Choose Home page, current: Daily").assertIsDisplayed()
        compose.onNodeWithText("Demo Station", substring = false).assertIsDisplayed()
        compose.onNodeWithText("Search places").assertDoesNotExist()
    }

    @Test
    fun emptyFailureSystemBackAndBlankSubmissionAreVisibleAndSafe() {
        compose.onNodeWithContentDescription("Search for a place").performClick()
        compose.onNodeWithTag("location-search-submit").assertIsDisplayed()
        assertEquals(0, calls.get())
        compose.onNodeWithTag("location-search-query").performClick()
        compose.onNodeWithTag("location-search-query").performTextInput("empty")
        compose.onNodeWithTag("location-search-submit").performClick()
        compose.waitUntil(5_000) { compose.onNodeWithTag("location-search-empty").fetchSemanticsNodeOrNull() != null }
        compose.onNodeWithText("No places found for empty. Try another spelling or a nearby city.").assertIsDisplayed()
        androidx.test.espresso.Espresso.pressBack()
        compose.waitForIdle()
        capture("search-empty-effects-off")

        compose.onNodeWithTag("location-search-query").performClick()
        compose.onNodeWithTag("location-search-query").performTextClearance()
        compose.onNodeWithTag("location-search-query").performTextInput("failed")
        compose.onNodeWithTag("location-search-submit").performClick()
        compose.waitUntil(5_000) { compose.onNodeWithTag("location-search-failure").fetchSemanticsNodeOrNull() != null }
        compose.onNodeWithText("Place search is temporarily unavailable. Try again shortly.").assertIsDisplayed()
        compose.onNodeWithText("endpoint", substring = true).assertDoesNotExist()
        assertEquals(2, calls.get())
        androidx.test.espresso.Espresso.pressBack()
        compose.waitForIdle()
        capture("search-failure-effects-off")

        compose.onNodeWithContentDescription("Cancel place search").performClick()
        compose.onNodeWithContentDescription("Choose Home page, current: Now").assertIsDisplayed()
        compose.onNodeWithContentDescription("Search for a place").performClick()
        androidx.test.espresso.Espresso.pressBack()
        compose.waitForIdle()
        compose.onNodeWithText("Search places").assertDoesNotExist()
        compose.onNodeWithContentDescription("Choose Home page, current: Now").assertIsDisplayed()
    }

    private fun selectHomePage(label: String) {
        compose.onNodeWithContentDescription("Choose Home page, current: Now").performClick()
        val index = listOf("Now", "Hourly", "Daily", "Details").indexOf(label)
        compose.onNodeWithContentDescription("$label page, ${index + 1} of 4, not selected").performClick()
        compose.waitForIdle()
    }

    private fun assertTarget(tag: String) {
        val bounds = compose.onNodeWithTag(tag).fetchSemanticsNode().boundsInRoot
        val density = compose.activity.resources.displayMetrics.density
        assertTrue("$tag width below 48dp: ${bounds.width / density}", bounds.width / density >= 48f)
        assertTrue("$tag height below 48dp: ${bounds.height / density}", bounds.height / density >= 48f)
    }

    private fun capture(name: String) {
        val bitmap = compose.onRoot().captureToImage().asAndroidBitmap()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val configuration = compose.activity.resources.configuration
        val direction = if ((LocationSearchTestHooks.layoutDirectionOverrideForTests
                ?: if (configuration.layoutDirection == android.view.View.LAYOUT_DIRECTION_RTL) LayoutDirection.Rtl else LayoutDirection.Ltr
            ) == LayoutDirection.Rtl) "rtl" else "ltr"
        val fontScale = "%.1f".format(java.util.Locale.ROOT, configuration.fontScale)
        val directory = File(context.getExternalFilesDir(null), "cycle116")
        check(directory.mkdirs() || directory.isDirectory)
        FileOutputStream(File(directory, "$name-$direction-font-$fontScale.png")).use { output ->
            check(bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, output))
        }
    }
}

private fun androidx.compose.ui.test.SemanticsNodeInteraction.fetchSemanticsNodeOrNull() =
    runCatching { fetchSemanticsNode() }.getOrNull()
