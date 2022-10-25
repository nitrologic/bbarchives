; ID: 1847
; Author: Devils Child
; Date: 2006-10-24 23:09:23
; Title: Infinite far clipplane
; Description: Stops clipping entities!

Type DX7_Matrix
	Field m1#, m2#, m3#, m4#
	Field m5#, m6#, m7#, m8#
	Field m9#, m10#, m11#, m12#
	Field m13#, m14#, m15#, m16#
End Type

Function InitDX7()
If DX7_SetSystemProperties(SystemProperty("Direct3D7"), SystemProperty("Direct3DDevice7"), SystemProperty("DirectDraw7"), SystemProperty("AppHWND"), SystemProperty("AppHINSTANCE")) Then RuntimeError "Error initializing dx7."
If DX7_GetStencilBitDepth() < 8 Then
	DX7_CreateStencilBuffer()
	If DX7_GetStencilBitDepth() < 8 Then RuntimeError "Graphic card does not support stencil buffers."
End If
End Function

Function FreeDX7()
DX7_RemoveSystemProperties()
End Function

Function DX7_InfiniteFarClipPlane()
m.DX7_Matrix = New DX7_Matrix
DX7_GetTransform 3, m
m\m11# = 1
m\m15# = -.1
DX7_SetTransform 3, m
Delete m
End Function
