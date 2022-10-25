; ID: 1710
; Author: Fisken
; Date: 2006-05-12 23:05:10
; Title: Call dll at runtime (no not CallDLL).
; Description: Calls DLL functions and com methods during runtime

.lib "d3d9.dll"
Direct3DCreate9_%(SDK%):"Direct3DCreate9"
