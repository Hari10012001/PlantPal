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

:: 3. Free Port 8080 if already in use
echo.
echo [2/4] Ensuring Port 8080 is available...
for /f "tokens=5" %%p in ('netstat -aon ^| findstr /R ":8080 .*LISTENING"') do (
    echo       Freeing Port 8080 (PID %%p)...
    taskkill /F /PID %%p >nul 2>&1
)
echo       Port 8080 ready.

:: 4. Start Spring Boot in a dedicated server window
echo.
echo [3/4] Starting PlantPal Spring Boot Server on port 8080...
echo       (Server logs will run in a dedicated window)
cd /d "%~dp0"
start "PlantPal Backend Server" cmd /k "title PlantPal Server Logs && set DB_PASSWORD=Hari2025@&& set ADMIN_PASSWORD=LiveAdminPassword@2026&& mvn spring-boot:run"

:: 5. Wait for server and automatically open browser
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
    timeout /t 4 >nul
    exit /b 0
)

set /a COUNT+=1
if %COUNT% GEQ %MAX_WAIT% (
    echo.
    echo [NOTICE] Server is taking slightly longer. Opening browser now...
    start http://localhost:8080/pages/login.html
    exit /b 0
)

echo       Waiting for server startup... (%COUNT%/%MAX_WAIT%s)
timeout /t 2 >nul
goto WAIT_LOOP
