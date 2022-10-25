; ID: 2774
; Author: Filax
; Date: 2010-10-07 03:42:31
; Title: 3D letters using blitz sprites
; Description: 3D letters with FastText extension

;
; 3D letters by filax, use as you want :) it's free.
;
Include "include\FastText_Unicode.bb"

Type t3dText
	
	Field X#
	Field Y#
	Field Z#
	
	Field R%
	Field G%
	Field B%
	
	Field TurnX#
	Field TurnY#
	Field TurnZ#
	
	Field Entity%
	Field Texture%
	
End Type

Global tTextureWidth%=1024
Global tTextureHeight%=256
Global tFontId%
Global tFontSize%=100

InitGraphics(1024,768)

Init3DText()

For i=1 To 1
	MyText.t3dText=Create3DText("New energy",Rnd(-50,50),Rnd(-50,50),Rnd(-50,50),Rnd(1,10),Rnd(100,255),Rnd(100,255),Rnd(100,255))
Next

Global Light = CreateLight(2)
Global Camera = CreateCamera()
PositionEntity Camera,0,0,-5
CameraRange Camera,0.001,500

MoveMouse (GraphicsWidth()/2,GraphicsHeight()/2)

While Not KeyDown(1)
	;Cls
	
	If MouseDown(2) Then
		Change3DText(MyText,"Mc2>"+MilliSecs())	
	EndIf

	FreeLook(Camera)
	Refresh3DText()
	
	RenderWorld
	
	
	
	Flip False
	
Wend
End

Function Init3DText()
	tFontId=LoadFont ("tahoma",tFontSize, 0,0,0,0, FT_ANTIALIASED) : SetFont tFontId	
End Function

Function Change3DText(id.t3dText,caption$)
	
	SetBuffer TextureBuffer(id\Texture%)
	
	Color 0,0,0 : Rect 0,0,tTextureWidth%, tTextureHeight%,1
	Color id\R%,id\G%,id\B% : Text tTextureWidth%/2,tTextureHeight%/2,caption$,1,1
	
	SetBuffer BackBuffer()
	
End Function

Function Create3DText.t3dText(caption$,X#,Y#,Z#,Scale#=1,R%=255,G%=255,B%=255)
	
	Local S.t3dText=New t3dText
	
	S\X#=X#
	S\Y#=Y#
	S\Z#=Z#
	
	S\R%=R%
	S\G%=G%
	S\B%=B%
	
	S\TurnX#=0;Rnd(0.1,0.2)
	S\TurnY#=Rnd(-0.1,0.1)
	S\TurnZ#=0;Rnd(0.1,0.2)
	
	S\Entity%=CreateSprite()
	SpriteViewMode S\Entity%,2
	EntityFX S\Entity%,1+16
	EntityBlend S\Entity%,3
	EntityAlpha S\Entity%,1.0
	
	ScaleSprite(S\Entity%,4*Scale#, 1*Scale#)
	PositionEntity S\Entity%,S\X#,S\Y#,S\Z#
	
	S\Texture% = CreateTexture(tTextureWidth%, tTextureHeight%, 8+16+32)
	EntityTexture S\Entity%, S\Texture%
	SetBuffer TextureBuffer(S\Texture%)
	
	;Color 50,50,50
	;Rect 0,0,width%, height%,1
	
	Color R%,G%,B% : Text tTextureWidth%/2,tTextureHeight%/2,caption$,1,1
	SetBuffer BackBuffer()
	
	Return S
	
End Function


Function Refresh3DText(p%=0)
	
	For I.t3dText=Each t3dText
		
		TurnEntity I\Entity%,I\TurnX#,I\TurnY#,I\TurnZ#
		
	Next
	
End Function


Function InitGraphics(w = 1440, h =900,title$="Blitz3D Program",exit_message$="")
	Graphics3D w, h, 32, 2
	SetBuffer BackBuffer()
	SeedRnd MilliSecs()
	
	If exit_message <> ""
		AppTitle title,exit_message
	Else
		AppTitle title
	EndIf
End Function

Global FL_Pitch#,FL_Yaw#,FL_Roll#,FL_XSpeed#,FL_YSpeed#,FL_ZSpeed#

Function FreeLook(FL_Cam)
	
	FL_Pitch#=FL_Pitch#+(MouseYSpeed()*0.02) : FL_Pitch#=FL_Pitch#/1.2
	FL_Yaw#=FL_Yaw#+-(MouseXSpeed()*0.02) : FL_Yaw#=FL_Yaw#/1.2
	
	MoveMouse (GraphicsWidth()/2,GraphicsHeight()/2)
	
	FL_ZSpeed#=FL_ZSpeed#+Float(KeyDown(17)-KeyDown(31))*0.01: FL_ZSpeed#=FL_ZSpeed#/1.14;  [w] & [s]
	FL_XSpeed#=FL_XSpeed#+Float(KeyDown(32)-KeyDown(30))*0.01 : FL_XSpeed#=FL_XSpeed#/1.14 ; [a] & [d]
	FL_YSpeed#=FL_YSpeed#+Float(KeyDown(19)-KeyDown(33))*0.01 : FL_YSpeed#=FL_YSpeed#/1.14 ; [r] & [f]
	
	MoveEntity FL_Cam,FL_XSpeed#,FL_YSpeed#+Abs(FL_Roll#*FL_XSpeed#)/50,FL_ZSpeed#
	
	Local cp#=EntityPitch(FL_Cam,True)+FL_Pitch#
	If Abs(cp)>89 Then cp=Sgn(cp)*89
	
	RotateEntity FL_Cam,cp,EntityYaw(FL_Cam)+FL_Yaw#,0
	
End Function
