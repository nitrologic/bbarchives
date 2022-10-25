; ID: 2690
; Author: GIB3D
; Date: 2010-04-03 00:39:56
; Title: Image/Texture To Model
; Description: Creates 3D Pixels using the pixel colors of an image/texture.

Graphics3D 800,600,0,2
	SetBuffer BackBuffer()
	SeedRnd MilliSecs()

Const VertexLimit = 16379
Global Screen = CreateMesh():EntityFX Screen,1+2+16+32
Global ScreenSurface = CreateSurface(Screen)
	
Global Light = CreateLight()

Global PivotYaw = CreatePivot():PositionEntity PivotYaw,0,20,0:RotateEntity PivotYaw,0,180,0
Global PivotPitch = CreatePivot(PivotYaw):PositionEntity PivotPitch,0,0,0:RotateEntity PivotPitch,90,0,0
Global Camera = CreateCamera(PivotPitch)
CameraZoom Camera,.8


;WARNING: If you load an image/texture that is too large, it may take a very long time for it to finish loading.
;It may (Note the "may") be safer to just use images at/under 1024x768 and textures at/under 1024x1024.
;It depends on the computer uberness...

;Start here by entering an image/texture 
;The different pixel_type numbers are specified in the CreatePixel function

;ImageToModel("Image.png","ImageAlpha.png")
TextureToModel("Texture.png",1+256,3)

Print "Hold left mouse button to use Mouse Look"
Print "WASD keys to move"
Print "Press any key to start"
WaitKey

;PositionEntity CreatePlane(),0,-1,0

MoveMouse GraphicsWidth()*.5,GraphicsHeight()*.5
MouseXSpeed():MouseYSpeed()
While Not KeyDown(1)
	
	If MouseHit(2)
		MoveMouse GraphicsWidth()*.5,GraphicsHeight()*.5
		MouseXSpeed():MouseYSpeed()
	EndIf

	If MouseDown(2)
		FUNC_GTATurnCamera(PivotPitch,PivotYaw)
	EndIf
	
	FUNC_MoveEntity(PivotYaw,10)
	
	UpdateWorld
	RenderWorld
	
	
	
	Flip 0
	Delay(10)
	
Wend
FreeEntity Screen
End

Function CreatePixel(x#,y#,z#,red,green,blue,alpha#=1,pixel_type=1,inset#=.01)
	;1 - Triangle Pixel
	;2 - Square Pixel
	;3 - LCD Pixel
	
	If CountVertices(ScreenSurface) > VertexLimit
		ScreenSurface = CreateSurface(Screen)
	EndIf
	
	Local Surface = ScreenSurface
	Local v1,v2,v3,v4
	
	Select pixel_type
		Case 1
			v1 = AddVertex(Surface,x,y,z);1 Left
			v2 = AddVertex(Surface,x+.5,y,z+1);0 Top
			v3 = AddVertex(Surface,x+1,y,z);2 Right
			AddTriangle(Surface,v1,v2,v3)
			VertexColor(Surface,v1,red,0,0,alpha)
			VertexColor(Surface,v2,0,green,0,alpha)
			VertexColor(Surface,v3,0,0,blue,alpha)
		Case 2
			v1 = AddVertex(Surface,x,y,z);Bottom Left
			v2 = AddVertex(Surface,x,y,z+1);Top Left
			v3 = AddVertex(Surface,x+1,y,z+1);Top Right
			v4 = AddVertex(Surface,x+1,y,z);Bottom Right
			AddTriangle(Surface,v1,v2,v3)
			AddTriangle(Surface,v4,v1,v3)
			VertexColor(Surface,v1,red,0,0,alpha)
			VertexColor(Surface,v2,0,green,0,alpha)
			VertexColor(Surface,v3,0,0,blue,alpha)
			VertexColor(Surface,v4,red,green,blue,alpha)
		Case 3
			Local N# = 1.0/3.0
			
			;Red
			v1 = AddVertex(Surface,x+inset,y,z+inset);Bottom Left
			v2 = AddVertex(Surface,x+inset,y,z+1-inset);Top Left
			v3 = AddVertex(Surface,x+N,y,z+1-inset);Top Right
			v4 = AddVertex(Surface,x+N,y,z+inset);Bottom Right
			AddTriangle(Surface,v1,v2,v3)
			AddTriangle(Surface,v4,v1,v3)
			VertexColor(Surface,v1,red,0,0,alpha)
			VertexColor(Surface,v2,red,0,0,alpha)
			VertexColor(Surface,v3,red,0,0,alpha)
			VertexColor(Surface,v4,red,0,0,alpha)
			
			;Green
			v1 = AddVertex(Surface,x+N,y,z+inset);Bottom Left
			v2 = AddVertex(Surface,x+N,y,z+1-inset);Top Left
			v3 = AddVertex(Surface,x+(N*2),y,z+1-inset);Top Right
			v4 = AddVertex(Surface,x+(N*2),y,z+inset);Bottom Right
			AddTriangle(Surface,v1,v2,v3)
			AddTriangle(Surface,v4,v1,v3)
			VertexColor(Surface,v1,0,green,0,alpha)
			VertexColor(Surface,v2,0,green,0,alpha)
			VertexColor(Surface,v3,0,green,0,alpha)
			VertexColor(Surface,v4,0,green,0,alpha)
			
			;Blue
			v1 = AddVertex(Surface,x+(N*2),y,z+inset);Bottom Left
			v2 = AddVertex(Surface,x+(N*2),y,z+1-inset);Top Left
			v3 = AddVertex(Surface,x+1-inset,y,z+1-inset);Top Right
			v4 = AddVertex(Surface,x+1-inset,y,z+inset);Bottom Right
			AddTriangle(Surface,v1,v2,v3)
			AddTriangle(Surface,v4,v1,v3)
			VertexColor(Surface,v1,0,0,blue,alpha)
			VertexColor(Surface,v2,0,0,blue,alpha)
			VertexColor(Surface,v3,0,0,blue,alpha)
			VertexColor(Surface,v4,0,0,blue,alpha)
			
	End Select
End Function

Function FUNC_MoveEntity(entity,speed_multiplier#=1)
	TFormVector (KeyDown(32)-KeyDown(30))*speed_multiplier,0,(KeyDown(17)-KeyDown(31))*speed_multiplier,Camera,0
	
	TranslateEntity entity,TFormedX(),TFormedY(),TFormedZ()
End Function

Function FUNC_GTATurnCamera(pitch_pivot,yaw_pivot,turn_multiplier#=1)
	TurnEntity yaw_pivot,0,-MouseXSpeed()*turn_multiplier,0
	TurnEntity pitch_pivot,MouseYSpeed()*turn_multiplier,0,0
	
	RotateEntity pitch_pivot,EntityPitch(pitch_pivot),0,0
	
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
End Function


Function ImageToModel(file$,alphafile$="",pixel_type=1,inset#=.01)
	Local Image = LoadImage(file)
	Local ImageAlpha = LoadImage(alphafile)
	
	Local Width,Height
	Local RGB,R,G,B,A#,Pixels,Dupe
	
	If Image
		DebugLog "ImageToModel("+file+")"
		
		Width = ImageWidth(Image)
		Height = ImageHeight(Image)
		
		If ImageAlpha
			DebugLog "ImageAlpha exists"
			LockBuffer ImageBuffer(ImageAlpha)
		EndIf
		LockBuffer ImageBuffer(Image)
			For x = 0 To Width-1
				For y = 0 To Height-1
					A = 1
					If ImageAlpha
						RGB = ReadPixelFast(x,y,ImageBuffer(ImageAlpha))
						R = GetRed(RGB)
						G = GetGreen(RGB)
						B = GetBlue(RGB)
						A = ((R+G+B)/3.0)/255.0
					EndIf
					
					RGB = ReadPixelFast(x,y,ImageBuffer(Image))
					R = GetRed(RGB)
					G = GetGreen(RGB)
					B = GetBlue(RGB)
					
					If R Or G Or B
						CreatePixel((Width-x)-(Width*.5),0,y-(Height*.5),R,G,B,A,pixel_type,inset)
					EndIf
					
					Pixels=Pixels+1
				Next
			Next
		UnlockBuffer ImageBuffer(Image)
		If ImageAlpha
			UnlockBuffer ImageBuffer(ImageAlpha)
			FreeImage ImageAlpha
		EndIf
		
		DebugLog "Done : "+Pixels+"Pixels"
		FreeImage Image
		Return True
	EndIf
	
	DebugLog "File ("+file+") Not found"
End Function

Function TextureToModel(file$,flags=1+2,pixel_type=1,inset#=.01)
	Local Texture = LoadTexture(file,flags)
	
	Local Width,Height
	Local RGB,R,G,B,A#,Pixels,Dupe
	
	If Texture
		DebugLog "TextureToModel("+file+")"
		
		Width = TextureWidth(Texture)
		Height = TextureHeight(Texture)
		
		LockBuffer TextureBuffer(Texture)
			For x = 0 To Width-1
				For y = 0 To Height-1
					RGB = ReadPixelFast(x,y,TextureBuffer(Texture))
					R = GetRed(RGB)
					G = GetGreen(RGB)
					B = GetBlue(RGB)
					A = GetAlpha(RGB)/255.0
					
					CreatePixel((Width-x)-(Width*.5),0,y-(Height*.5),R,G,B,A,pixel_type,inset)
					
					Pixels=Pixels+1
				Next
			Next
		UnlockBuffer TextureBuffer(Texture)
		
		DebugLog "Done : "+Pixels+"Pixels"
		FreeTexture Texture
		Return True
	EndIf
	
	DebugLog "File ("+file+") Not found"
End Function

Function GetRed(rgb)
	Return (rgb And $FF0000) Shr 16
End Function

Function GetGreen(rgb)
	Return (rgb And $FF00) Shr 8
End Function

Function GetBlue(rgb)
	Return rgb And $FF
End Function

Function GetAlpha(rgb)
	Return (rgb And $FF000000) Shr 24
End Function
