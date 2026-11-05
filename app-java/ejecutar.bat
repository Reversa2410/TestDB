@echo off
REM Ejecuta la aplicacion. Con el parametro --prueba hace la
REM comprobacion de conexion por consola, sin abrir la ventana.
setlocal
cd /d "%~dp0"

REM --- Localizar el JDK -------------------------------------------
set "JDKBIN="
for %%I in (javac.exe) do if not "%%~$PATH:I"=="" set "JDKBIN=%%~dp$PATH:I"
if not defined JDKBIN if defined JAVA_HOME if exist "%JAVA_HOME%\bin\javac.exe" set "JDKBIN=%JAVA_HOME%\bin\"
if not defined JDKBIN for /d %%D in ("C:\Program Files\Java\jdk*") do if exist "%%D\bin\javac.exe" set "JDKBIN=%%D\bin\"
if not defined JDKBIN for /d %%D in ("C:\Program Files\Eclipse Adoptium\jdk*") do if exist "%%D\bin\javac.exe" set "JDKBIN=%%D\bin\"
if not defined JDKBIN for /d %%D in ("C:\Program Files\Microsoft\jdk*") do if exist "%%D\bin\javac.exe" set "JDKBIN=%%D\bin\"
if not defined JDKBIN (
    echo No se encontro un JDK. Instale uno o defina la variable JAVA_HOME.
    exit /b 1
)

if not exist "bin\gestionclientes\Main.class" (
    echo No esta compilado. Ejecute primero compilar.bat
    exit /b 1
)

"%JDKBIN%java" -cp "bin;lib\*" -Djava.library.path="lib" gestionclientes.Main %*
