; ID: 1138
; Author: gman
; Date: 2004-08-20 07:15:28
; Title: kernel32.dll decls for INI access
; Description: DLL declarations for use with INI functions

.lib "kernel32.dll"

IniRead%(AppName$, KeyName$, Default$, String*, size%, FileName$):"GetPrivateProfileStringA"
IniEnumValues%(AppName$, KeyNull%, Default$, String*, size%, FileName$):"GetPrivateProfileStringA"
IniEnumSections%(KeyNull1%, KeyNull2%, Default$, String*, size%, FileName$):"GetPrivateProfileStringA"
IniWrite%(AppName$, KeyName$, Value$, FileName$):"WritePrivateProfileStringA"
