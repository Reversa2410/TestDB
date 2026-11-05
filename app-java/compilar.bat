@echo off
REM Compila el proyecto sin necesidad de Maven ni IDE.
setlocal enabledelayedexpansion
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

if not exist "bin" mkdir "bin"

set "FUENTES="
for /r "src" %%F in (*.java) do set FUENTES=!FUENTES! "%%F"
if not defined FUENTES (
    echo No se encontraron archivos .java dentro de src.
    exit /b 1
)

echo Compilando con "%JDKBIN%javac"
"%JDKBIN%javac" -encoding UTF-8 -d bin -cp "lib\*"!FUENTES!
if errorlevel 1 (
    echo.
    echo FALLO LA COMPILACION.
    exit /b 1
)

echo Compilacion correcta. Ahora ejecute ejecutar.bat
