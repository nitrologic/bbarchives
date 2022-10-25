; ID: 2147
; Author: JoshK
; Date: 2007-11-06 23:13:47
; Title: OpenGL Accelerator
; Description: These two replacement functions will offer slightly faster performance, depending on the application.

Private

Const GL_TEXTURE_1D_SLOT=1
Const GL_TEXTURE_2D_SLOT=2
Const GL_TEXTURE_3D_SLOT=3
Const GL_TEXTURE_CUBE_MAP_SLOT=4
Const GL_TEXTURE_RECTANGLE_ARB_SLOT=5

Global BoundTexture[64,5]
Global CurrentTextureUnit

Global FastTextureBind=True' toggle this variable to see the difference

Function glBindTexture_(target,index)
	Local slot
	Select target
		Case GL_TEXTURE_1D
			slot=GL_TEXTURE_1D_SLOT
		Case GL_TEXTURE_2D
			slot=GL_TEXTURE_2D_SLOT
		Case GL_TEXTURE_3D	
			slot=GL_TEXTURE_3D_SLOT
		Case GL_TEXTURE_CUBE_MAP
			slot=GL_TEXTURE_CUBE_MAP_SLOT
		Case GL_TEXTURE_RECTANGLE_ARB
			slot=GL_TEXTURE_RECTANGLE_ARB_SLOT
		Default
			RunTimeError "Unknown texture target."
	EndSelect
	If FastTextureBind
		If BoundTexture[CurrentTextureUnit,slot]=index Return
	EndIf
	glBindTexture target,index
	BoundTexture[CurrentTextureUnit,slot]=index
EndFunction

Function glActiveTextureARB_(texunit)
	If FastTextureBind
		If CurrentTextureUnit=texunit-GL_TEXTURE0 Return
	EndIf
	glActiveTextureARB texunit
	CurrentTextureUnit=texunit-GL_TEXTURE0
EndFunction

Public

Rem
'Uncomment for testing:
Function glbindtexture()
EndFunction

Function glActiveTextureARB()
EndFunction
EndRem
