@echo off
:: saves current directory
cd > temp
set /p curLoc=<temp
del temp
:: switches current directory to the root directory
cd /
:: searches for the file
dir /b/s "Program Files\Java\jdk1.8*" > temp
set /p var=<temp
del temp
:: back to original directory
cd %curLoc%
:: sets the classpath
setx CLASSPATH "%CLASSPATH%;%cd%\CS15Support.jar" > nul
:: sets JAVA_HOME
setx JAVA_HOME "%var%" /M > nul
:: sets the system path to include JAVA_HOME\bin
powershell "[Environment]::SetEnvironmentVariable('Path', [Environment]::GetEnvironmentVariable('Path', [EnvironmentVariableTarget]::Machine) + ';%%JAVA_HOME%%\bin', 'Machine')"
:: if no error, deletes the settings and script files
if %errorlevel% neq 0 (
echo Something ain't right here. Better call a TA.
) else (
echo All set!
(goto) 2>nul & del settings.zip macSetup.sh "%~f0"
)