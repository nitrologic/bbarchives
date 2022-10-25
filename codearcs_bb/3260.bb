; ID: 3260
; Author: Dan
; Date: 2016-02-18 04:56:27
; Title: commandline compile (b3d/Bplus)
; Description: batch files for windows commandline (cmd)

RuntimeError "This code is for Commandline, it is not intended to be run from BlitzBasic IDE." 

B3dRun.bat

@Echo Off
%~d1
cd %~dp1
set blitzpath=C:\BB3D\
set path=%blitzpath%bin\
"%blitzpath%bin\blitzcc.exe" "%1"
pause


B3dDebug.bat

@Echo Off
%~d1
cd %~dp1
set blitzpath=C:\BB3D\
set path=%blitzpath%bin\
"%blitzpath%bin\blitzcc.exe" -d "%1"
pause

B3dcompile.bat

@Echo Off
%~d1
cd %~dp1
set blitzpath=D:\BB3D\
set path=%blitzpath%bin\
"%blitzpath%bin\blitzcc.exe" -o "%~d1%~p1%~n1".exe "%1"
pause
