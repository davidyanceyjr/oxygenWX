@echo off
setlocal
set "APP_HOME=%~dp0"
set "WRAPPER_JAR=%APP_HOME%gradle\wrapper\gradle-wrapper.jar"
set "WRAPPER_URL=https://services.gradle.org/distributions/gradle-9.6.0-wrapper.jar"
set "WRAPPER_SHA256=497c8c2a7e5031f6aa847f88104aa80a93532ec32ee17bdb8d1d2f67a194a9c7"

if not exist "%WRAPPER_JAR%" (
  echo Oxygen Weather: bootstrapping Gradle Wrapper 9.6.0...
  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "$ErrorActionPreference='Stop';" ^
    "New-Item -ItemType Directory -Force -Path (Split-Path '%WRAPPER_JAR%') | Out-Null;" ^
    "Invoke-WebRequest -UseBasicParsing -Uri '%WRAPPER_URL%' -OutFile '%WRAPPER_JAR%.tmp';" ^
    "$hash=(Get-FileHash -Algorithm SHA256 '%WRAPPER_JAR%.tmp').Hash.ToLowerInvariant();" ^
    "if ($hash -ne '%WRAPPER_SHA256%') { Remove-Item -Force '%WRAPPER_JAR%.tmp'; throw 'Gradle Wrapper JAR checksum mismatch: ' + $hash };" ^
    "Move-Item -Force '%WRAPPER_JAR%.tmp' '%WRAPPER_JAR%'"
  if errorlevel 1 exit /b 1
)

for /f %%H in ('powershell -NoProfile -Command "(Get-FileHash -Algorithm SHA256 '%WRAPPER_JAR%').Hash.ToLowerInvariant()"') do set "ACTUAL_SHA=%%H"
if /I not "%ACTUAL_SHA%"=="%WRAPPER_SHA256%" (
  echo Oxygen Weather: existing Gradle Wrapper JAR failed checksum verification.
  exit /b 1
)

if defined JAVA_HOME (
  set "JAVACMD=%JAVA_HOME%\bin\java.exe"
) else (
  set "JAVACMD=java.exe"
)

"%JAVACMD%" -Dorg.gradle.appname=gradlew -classpath "%WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
exit /b %ERRORLEVEL%
