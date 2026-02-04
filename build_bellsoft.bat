@echo off
setlocal

echo ============================================
echo      BellSoft Liberica Native Image Build
echo ============================================

REM 1. Set BellSoft Path (Auto-configured from libs)
set "GRAALVM_HOME=%CD%\libs\bellsoft-nik\bellsoft-liberica-vm-full-openjdk17-23.0.11"

set JAVA_HOME=%GRAALVM_HOME%
set PATH=%GRAALVM_HOME%\bin;%PATH%

echo.
echo [1.5/3] Initializing Visual Studio Environment...
call "C:\Program Files\Microsoft Visual Studio\18\Insiders\VC\Auxiliary\Build\vcvars64.bat"
if %ERRORLEVEL% NEQ 0 (
    echo [WARNING] Failed to initialize Visual Studio environment. Build might fail.
)

echo.
echo [1.6/3] Compiling Resources (Icon)...
rc /fo terminal.res terminal.rc

echo.
echo [1/3] Environment Setup
echo GRAALVM_HOME: %GRAALVM_HOME%
echo JAVA_HOME: %JAVA_HOME%
echo.

echo [2/3] Cleaning target...
call mvnw clean

echo.
echo [3/3] Building Native Image with BellSoft...
echo Command: mvnw -Pnative package
call mvnw -Pnative package

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [FAILURE] Build failed. Check the output above.
    exit /b %ERRORLEVEL%
)

echo.
echo [SUCCESS] Build completed!
echo Executable should be in target\
