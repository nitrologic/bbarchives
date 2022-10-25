; ID: 2475
; Author: GIB3D
; Date: 2009-05-08 17:06:13
; Title: Simple Motion Blur
; Description: Simple Motion Blur

InitGraphics(1024,1024)

Global Light = CreateLight()
	
Global Camera = CreateCamera()
	PositionEntity Camera,0,1,-5
	CameraRange Camera,.001,100
	

CreatePlane()

Local Cube = CreateCube()
PositionEntity Cube,0,1,0
RotateEntity Cube,30,40,0

Local Overlay = CreateQuad(1,1,-1,Camera)
PositionEntity Overlay,0,0,.5
EntityAlpha Overlay,.8

Local Texture = CreateTexture(1024,1024,1+16+32)
EntityTexture Overlay,Texture

LockBuffer TextureBuffer(Texture)
	For y = 0 To TextureHeight(Texture)-1
		For x = 0 To TextureWidth(Texture)-1
			WritePixelFast x,y,0,TextureBuffer(Texture)
		Next
	Next
UnlockBuffer TextureBuffer(Texture)
	
RenderTexture(Texture)

While Not KeyDown(1)
	
	TurnEntity Cube,.5,1,.5
	
	If KeyDown(200) TranslateEntity Camera,0,0,.1
	If KeyDown(208) TranslateEntity Camera,0,0,-.1
	If KeyDown(203) TranslateEntity Camera,-.1,0,0
	If KeyDown(205) TranslateEntity Camera,.1,0,0
	
	UpdateWorld
	
	RenderTexture(Texture)
	
	Flip
Wend
End

Function RenderTexture(texture)
	RenderWorld
	CopyRect 0,0,1024,1024,0,0,FrontBuffer(),TextureBuffer(texture)
End Function

Function InitGraphics(w = 1024, h = 768,title$="Blitz3D Program",exit_message$="")
	Graphics3D w, h, 32, 2
	SetBuffer BackBuffer()
	SeedRnd MilliSecs()
	
	If exit_message <> ""
		AppTitle title,exit_message
			Else
				AppTitle title
	EndIf
End Function

Function CreateQuad(width#,height#,order%=-1,parent%=False)
	Local v0,v1,v2,v3
	Local Point,Surface
	
	If Not parent = False
		Point = CreateMesh(parent)
			Else
				Point = CreateMesh()
	EndIf
	
	If order <> 0
		EntityOrder Point,order
	EndIf
	
	EntityFX Point,1
			
	Surface = CreateSurface(Point)
	
	v0=AddVertex(Surface,-(width*.5),(height*.5),0 ,0,0)
	v1=AddVertex(Surface,(width*.5),(height*.5),0 ,1,0)
	v2=AddVertex(Surface,-(width*.5),-(height*.5),0 ,0,1)
	v3=AddVertex(Surface,(width*.5),-(height*.5),0 ,1,1)
	
	AddTriangle(Surface,v0,v1,v2)
	AddTriangle(Surface,v1,v3,v2)
	
	Return Point
End Function
