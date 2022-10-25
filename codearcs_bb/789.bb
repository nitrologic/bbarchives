; ID: 789
; Author: dan_upright
; Date: 2003-09-04 21:26:11
; Title: simple ini file commands (broken)
; Description: read/write ini files using the win api

kernel32.decls:
----
.lib "kernel32.dll"

ReadIni%(AppName$, KeyName$, Default$, String$, size%, FileName$):"GetPrivateProfileStringA"
WriteIni%(AppName$, KeyName$, Value$, FileName$):"WritePrivateProfileStringA"
