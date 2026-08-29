@echo off
TITLE PlantPal - Local Database Inspector
COLOR 0B
CLS

echo =========================================================================
echo               PLANTPAL - LOCAL DATABASE INSPECTOR (MySQL)
echo =========================================================================
echo.

:: 1. Database Configuration
set "DB_NAME=plantpal_db"
set "DB_USER=root"
if "%DB_PASSWORD%"=="" (
    echo [NOTICE] DB_PASSWORD environment variable is not set.
    set /p "DB_PASS=Enter MySQL root password (or set DB_PASSWORD env var): "
) else (
    set "DB_PASS=%DB_PASSWORD%"
)

:: 2. Locate MySQL CLI
set "MYSQL_CMD=mysql"
where mysql >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" (
        set "MYSQL_CMD=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
    ) else if exist "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" (
        set "MYSQL_CMD=C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe"
    ) else if exist "C:\xampp\mysql\bin\mysql.exe" (
        set "MYSQL_CMD=C:\xampp\mysql\bin\mysql.exe"
    ) else (
        echo [ERROR] MySQL CLI client not found in PATH or standard installation paths.
        echo         Please ensure MySQL is installed or use a MySQL GUI.
        echo.
        pause
        exit /b 1
    )
)

echo [1/3] Testing database connection to '%DB_NAME%'...
"%MYSQL_CMD%" -u %DB_USER% -p%DB_PASS% -e "SELECT 1;" >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Could not connect to MySQL database '%DB_NAME%'.
    echo         Please ensure MySQL Server is running and credentials are valid.
    echo.
    pause
    exit /b 1
)
echo       Database connection successful!
echo.

:: 3. Process Arguments
if "%1"=="" goto SHOW_SUMMARY
if /I "%1"=="status" goto SHOW_SUMMARY
if /I "%1"=="tables" goto SHOW_TABLES
if /I "%1"=="data" goto SHOW_DATA

:SHOW_SUMMARY
echo [2/3] Database Overview and Record Counts:
echo -------------------------------------------------------------------------
"%MYSQL_CMD%" -u %DB_USER% -p%DB_PASS% %DB_NAME% -e "SELECT 'users' AS Table_Name, COUNT(*) AS Total_Records FROM users UNION ALL SELECT 'plant_categories', COUNT(*) FROM plant_categories UNION ALL SELECT 'plants', COUNT(*) FROM plants UNION ALL SELECT 'care_schedules', COUNT(*) FROM care_schedules UNION ALL SELECT 'watering_records', COUNT(*) FROM watering_records UNION ALL SELECT 'growth_records', COUNT(*) FROM growth_records;"
echo.
echo [3/3] Recent Registered Users:
echo -------------------------------------------------------------------------
"%MYSQL_CMD%" -u %DB_USER% -p%DB_PASS% %DB_NAME% -e "SELECT id, full_name, email, role, created_at FROM users ORDER BY id DESC LIMIT 5;"
echo.
echo =========================================================================
echo Usage Options:
echo   DB.bat          - Shows summary record counts and users
echo   DB.bat tables   - Lists all table schemas
echo   DB.bat data     - Shows recent plant care records
echo =========================================================================
echo.
exit /b 0

:SHOW_TABLES
echo [TABLE SCHEMAS] Showing tables in '%DB_NAME%':
echo -------------------------------------------------------------------------
"%MYSQL_CMD%" -u %DB_USER% -p%DB_PASS% %DB_NAME% -e "SHOW TABLES;"
echo.
exit /b 0

:SHOW_DATA
echo [PLANTS AND CARE SCHEDULES]
echo -------------------------------------------------------------------------
"%MYSQL_CMD%" -u %DB_USER% -p%DB_PASS% %DB_NAME% -e "SELECT p.id, p.name, c.name AS category, p.plant_status, cs.watering_interval_days AS interval_days, cs.last_watered_date, DATE_ADD(cs.last_watered_date, INTERVAL cs.watering_interval_days DAY) AS next_water_due FROM plants p LEFT JOIN plant_categories c ON p.category_id = c.id LEFT JOIN care_schedules cs ON cs.plant_id = p.id LIMIT 10;"
echo.
exit /b 0
