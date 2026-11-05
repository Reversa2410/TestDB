@echo off
REM Compila el proyecto con Maven.
setlocal
cd /d "%~dp0"

call mvn -q compile
if errorlevel 1 (
    echo.
    echo FALLO LA COMPILACION.
    exit /b 1
)

echo Compilacion correcta. Ahora ejecute ejecutar.bat
