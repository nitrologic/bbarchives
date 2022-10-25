; ID: 1135
; Author: gman
; Date: 2004-08-19 05:49:46
; Title: advapi32.dll decls for registry access
; Description: DLL declarations for use with registry functions

.lib "Advapi32.dll"

RegOpenKey%(hKeyParent%,SubKey$,phkResult*):"RegOpenKeyA"
RegCloseKey%(hKey%):"RegCloseKey"
RegFlushKey%(hKey%):"RegFlushKey"
RegCreateKey%(hKeyParent%,SubKey$,phkResult*):"RegCreateKeyA"
RegDeleteKey%(hKeyParent%,SubKey$):"RegDeleteKeyA"
RegSetValueEx%(hKey%,ValueName$,Reserved%,nType%,Bytes*,size%):"RegSetValueExA"
RegDeleteValue%(hKey%,ValueName$):"RegDeleteValueA"
RegEnumKey%(hKey%,idx%,Key*,size%):"RegEnumKeyA"
RegEnumValue%(hKey%,idx%,ValueName*,NameSize*,Reserved%,nType*,ValueBytes*,ValueSize*):"RegEnumValueA"
RegQueryValueEx%(hKey%,ValueName$,Reserved%,nType*,ValueBytes*,ValueSize*):"RegQueryValueExA"
