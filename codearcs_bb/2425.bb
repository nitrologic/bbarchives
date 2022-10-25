; ID: 2425
; Author: Malice
; Date: 2009-03-01 14:18:41
; Title: Mimic Desktop Resolution (Requires "user32.dll")
; Description: Sets the Graphics (or Graphics3D) rresolution to the same as windows Desktop

.lib "user32.dll"
GetSystemMetrics% (nIndex%) : "GetSystemMetrics"

_________________________________________________________

Function SetResolution3D(nBit_Depth=32,nWindow_Mode=0)
Graphics3D GetSystemMetrics(16),GetSystemMetrics(1),nBit_Depth,nWindow_Mode
End Function

Function SetResolution(nBit_Depth=32,nWindow_Mode=0)
Graphics GetSystemMetrics(16),GetSystemMetrics(1),nBit_Depth,nWindow_Mode
End Function
