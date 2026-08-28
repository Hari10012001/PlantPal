@echo off
TITLE PlantPal - Stopping Application
COLOR 0C
CLS

echo =========================================================================
echo               PLANTPAL - SHUTTING DOWN SERVER
echo =========================================================================
echo.

echo [*] Searching for processes running on port 8080...
for /f "tokens=5" %%p in ('netstat -aon ^| findstr /R ":8080 .*LISTENING"') do (
    echo [*] Found process with PID: %%p. Terminating...
    taskkill /F /PID %%p >nul 2>&1
)

:: Extra fallback via PowerShell to ensure clean shutdown
powershell -NoProfile -Command "Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique | ForEach-Object { Stop-Process -Id $_ -Force -ErrorAction SilentlyContinue }" >nul 2>&1

echo.
echo =========================================================================
echo [SUCCESS] PlantPal server (Port 8080) has been stopped successfully!
echo =========================================================================
echo.
timeout /t 3 >nul
