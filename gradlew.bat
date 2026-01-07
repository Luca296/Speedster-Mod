@echo off
setlocal

set DIR=%~dp0

if exist "%DIR%\gradle\wrapper\gradle-wrapper.jar" (
  goto run
)

echo Missing gradle\wrapper\gradle-wrapper.jar
echo This repo should include the Gradle Wrapper JAR.
echo.
echo You can fix by downloading the wrapper jar or re-generating wrapper.
echo.
exit /b 1

:run
set JAVA_EXE=java
if not "%JAVA_HOME%"=="" set JAVA_EXE=%JAVA_HOME%\bin\java.exe

"%JAVA_EXE%" -classpath "%DIR%\gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*

endlocal
