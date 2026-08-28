@echo off
TITLE PlantPal - 1-Click Server Launcher
COLOR 0A
CLS

echo =========================================================================
echo               PLANTPAL - PERSONAL PLANT CARE PLATFORM
echo                           1-Click Server Launcher
echo =========================================================================
echo.

:: 1. Set Database & Admin Environment Variables
set "DB_PASSWORD=Hari2025@"
set "ADMIN_PASSWORD=LiveAdminPassword@2026"

:: 2. Check Java
echo [1/4] Checking Java environment...
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java is not installed or not in PATH! Please install Java 21.
    pause
    exit /b 1
)
echo       Java runtime detected.

:: 3. Check Maven
echo.
echo [2/4] Checking Maven build tool...
call mvn -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Maven is not installed or not in PATH!
    pause
    exit /b 1
)
echo       Maven build tool detected.

:: 4. Start Spring Boot in dedicated window
echo.
echo [3/4] Starting PlantPal Spring Boot Server on port 8080...
echo       (Logs will run in a separate window. Press Ctrl+C there to stop)
cd /d "%~dp0"
start "PlantPal Backend Server" cmd /k "title PlantPal Server Logs && set DB_PASSWORD=Hari2025@& set ADMIN_PASSWORD=LiveAdminPassword@2026& call mvn spring-boot:run"

:: 5. Wait for server and automatically launch default browser
echo.
echo [4/4] Waiting for backend server to become ready on http://localhost:8080...
set MAX_WAIT=35
set COUNT=0

:WAIT_LOOP
powershell -NoProfile -Command "$r = Invoke-WebRequest -Uri 'http://localhost:8080/api/health' -UseBasicParsing -ErrorAction SilentlyContinue; if ($r.StatusCode -eq 200) { exit 0 } else { exit 1 }" >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    echo.
    echo =========================================================================
    echo [SUCCESS] PlantPal Server is LIVE on http://localhost:8080!
    echo [ACTION] Launching default web browser automatically...
    echo =========================================================================
    start http://localhost:8080/pages/login.html
    echo.
    echo To stop the application anytime, double-click stop.bat
    echo.
    ping 127.0.0.1 -n 4 >nul
    exit /b 0
)

set /a COUNT+=1
if %COUNT% GEQ %MAX_WAIT% (
    echo.
    echo [NOTICE] Server is taking a bit longer. Opening browser now...
    start http://localhost:8080/pages/login.html
    exit /b 0
)

echo       Waiting for port 8080... (%COUNT%/%MAX_WAIT%s)
ping 127.0.0.1 -n 3 >nul
goto WAIT_LOOP
