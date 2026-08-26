@echo off
title PlantPal - Stopping Application
cls

echo =========================================================
echo    PLANTPAL - SHUTTING DOWN SERVER
echo =========================================================
echo.

echo [*] Searching for processes running on port 8080...
set "FOUND=0"

for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080" ^| findstr "LISTENING"') do (
    set "FOUND=1"
    echo [*] Found process with PID: %%a. Terminating...
    taskkill /F /PID %%a >nul 2>&1
)

if "%FOUND%"=="1" (
    echo.
    echo [SUCCESS] PlantPal server (Port 8080) has been stopped successfully!
) else (
    echo.
    echo [INFO] No running PlantPal process found on Port 8080.
)

echo.
echo =========================================================
pause