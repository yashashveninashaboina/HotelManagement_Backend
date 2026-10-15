@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM ----------------------------------------------------------------------------

@echo off
setlocal

where mvn >nul 2>&1
if errorlevel 1 (
    echo Maven was not found on PATH.
    echo Install Maven or use the full Maven executable path.
    exit /b 1
)

mvn %*
exit /b %ERRORLEVEL%
