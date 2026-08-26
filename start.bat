@echo off
title PlantPal - Starting Application
cls

echo =========================================================
echo    PL Serp PLANTPAL - PERSONAL PLANT CARE PLATFORM
echo =========================================================
echo.

:: 1. Set Database & Admin Environment Variables
set "DB_PASSWORD=Hari2025@"
set "ADMIN_PASSWORD=LiveAdminPassword@2026"

:: 2. Check if Port 8080 is currently in use and clean it up
echo [1/3] Checking Port 8080 availability...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080" ^| findstr "LISTENING"') do (
    echo [*] Port 8080 is busy by PID %%a. Freeing port...
    taskkill /F /PID %%a >nul 2>&1
)

echo [2/3] Environment configured (DB_PASSWORD & ADMIN_PASSWORD set).
echo [3/3] Launching Spring Boot server on http://localhost:8080/ ...
echo.
echo =========================================================
echo    PlantPal is starting! 
echo    Once started, open: http://localhost:8080/
echo    To stop the application, run stop.bat or press Ctrl+C
echo =========================================================
echo.

:: 3. Run Maven Spring Boot
call mvn spring-boot:run

pause