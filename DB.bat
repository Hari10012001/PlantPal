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
if /I "%1"=="users" goto SHOW_USERS
if /I "%1"=="plants" goto SHOW_PLANTS
if /I "%1"=="categories" goto SHOW_CATEGORIES
if /I "%1"=="watering" goto SHOW_WATERING
if /I "%1"=="growth" goto SHOW_GROWTH

echo [ERROR] Unknown option '%1'.
echo.
goto SHOW_USAGE

:SHOW_SUMMARY
echo [2/3] Database Overview and Record Counts:
echo -------------------------------------------------------------------------
"%MYSQL_CMD%" -u %DB_USER% -p%DB_PASS% %DB_NAME% -e "SELECT 'users' AS Table_Name, COUNT(*) AS Total_Records FROM users UNION ALL SELECT 'plant_categories', COUNT(*) FROM plant_categories UNION ALL SELECT 'plants', COUNT(*) FROM plants UNION ALL SELECT 'care_schedules', COUNT(*) FROM care_schedules UNION ALL SELECT 'watering_records', COUNT(*) FROM watering_records UNION ALL SELECT 'growth_records', COUNT(*) FROM growth_records;"
echo.
echo [3/3] Recent Registered Users:
echo -------------------------------------------------------------------------
"%MYSQL_CMD%" -u %DB_USER% -p%DB_PASS% %DB_NAME% -e "SELECT id, full_name, email, role, created_at FROM users ORDER BY id DESC LIMIT 5;"
echo.
goto SHOW_USAGE

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

:SHOW_USERS
echo [USERS DIRECTORY]
echo -------------------------------------------------------------------------
"%MYSQL_CMD%" -u %DB_USER% -p%DB_PASS% %DB_NAME% -e "SELECT id, full_name, email, role, created_at FROM users ORDER BY id ASC;"
echo.
exit /b 0

:SHOW_PLANTS
echo [ALL PLANTS]
echo -------------------------------------------------------------------------
"%MYSQL_CMD%" -u %DB_USER% -p%DB_PASS% %DB_NAME% -e "SELECT p.id, p.name, p.species, c.name AS category, p.location, p.plant_status, u.email AS owner FROM plants p LEFT JOIN plant_categories c ON p.category_id = c.id LEFT JOIN users u ON p.user_id = u.id ORDER BY p.id ASC;"
echo.
exit /b 0

:SHOW_CATEGORIES
echo [PLANT CATEGORIES]
echo -------------------------------------------------------------------------
"%MYSQL_CMD%" -u %DB_USER% -p%DB_PASS% %DB_NAME% -e "SELECT id, name, description, created_at FROM plant_categories ORDER BY id ASC;"
echo.
exit /b 0

:SHOW_WATERING
echo [RECENT WATERING RECORDS]
echo -------------------------------------------------------------------------
"%MYSQL_CMD%" -u %DB_USER% -p%DB_PASS% %DB_NAME% -e "SELECT w.id, p.name AS plant_name, w.watered_date, w.notes, w.created_at FROM watering_records w LEFT JOIN plants p ON w.plant_id = p.id ORDER BY w.id DESC LIMIT 15;"
echo.
exit /b 0

:SHOW_GROWTH
echo [RECENT GROWTH OBSERVATIONS]
echo -------------------------------------------------------------------------
"%MYSQL_CMD%" -u %DB_USER% -p%DB_PASS% %DB_NAME% -e "SELECT g.id, p.name AS plant_name, g.record_date, g.height_cm, g.leaf_count, g.notes FROM growth_records g LEFT JOIN plants p ON g.plant_id = p.id ORDER BY g.id DESC LIMIT 15;"
echo.
exit /b 0

:SHOW_USAGE
echo =========================================================================
echo Usage Options:
echo   DB.bat              - Shows summary record counts and recent users
echo   DB.bat tables       - Lists all database tables
echo   DB.bat data         - Shows plant care schedules and next watering due
echo   DB.bat users        - Lists all registered users and roles
echo   DB.bat plants       - Lists all registered plants and owners
echo   DB.bat categories   - Lists all plant categories
echo   DB.bat watering     - Lists recent watering event logs
echo   DB.bat growth       - Lists recent plant growth observation logs
echo =========================================================================
echo.
exit /b 0
