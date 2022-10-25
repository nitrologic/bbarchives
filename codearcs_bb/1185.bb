; ID: 1185
; Author: Eikon
; Date: 2004-11-04 12:58:33
; Title: Change Desktop Wallpaper
; Description: userlib

; Required Userlib
; .lib "user32.dll"
; SystemParametersInfo%(uAction%, uParam%, lpvParam$, fuWinIni%):"SystemParametersInfoA"

AppTitle "Change Desktop Wallpaper by Eikon"
Graphics 640, 320, 16, 2

Const SET_WALLPAPER% = 20
Const UPDATE_INI_FILE% = 1

file$ = "Cash Out.bmp" ; Change to path of the wallpaper

ret% = SystemParametersInfo(SET_WALLPAPER%, 0, file$, UPDATE_INI_FILE%)
If Not ret% Then RuntimeError "Set Wallpaper failed!"
