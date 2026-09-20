@echo off
setlocal
set "APP_HOME=%~dp0"
set "WRAPPER_JAR=%APP_HOME%gradle\wrapper\gradle-wrapper.jar"
set "WRAPPER_SHA256=cb0da6751c2b753a16ac168bb354870ebb1e162e9083f116729cec9c781156b8"

if not exist "%WRAPPER_JAR%" (
  echo Oxygen Weather: missing checked-in Gradle Wrapper JAR: %WRAPPER_JAR%
  exit /b 1
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
