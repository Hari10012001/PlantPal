@echo off
TITLE PlantPal - Stopping Application
COLOR 0C
CLS

echo =========================================================================
echo               PLANTPAL - SHUTTING DOWN SERVER
echo =========================================================================
echo.

echo [*] Stopping PlantPal processes on port 8080...
powershell -NoProfile -Command "Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique | ForEach-Object { if ($_ -gt 0) { Stop-Process -Id $_ -Force -ErrorAction SilentlyContinue } }"

echo.
echo =========================================================================
echo [SUCCESS] PlantPal server (Port 8080) has been stopped successfully!
echo =========================================================================
echo.
ping 127.0.0.1 -n 3 >nul
