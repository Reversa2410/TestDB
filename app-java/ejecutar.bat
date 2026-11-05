@echo off
REM Ejecuta la aplicacion con Maven. Con --prueba hace la
REM comprobacion de conexion por consola, sin abrir la ventana.
setlocal
cd /d "%~dp0"

call mvn -q compile
if errorlevel 1 (
    echo No esta compilado. Revise los errores de Maven.
    exit /b 1
)

call mvn -q exec:java -Dexec.args="%*"
