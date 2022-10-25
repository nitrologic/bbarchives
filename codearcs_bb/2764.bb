; ID: 2764
; Author: _PJ_
; Date: 2010-09-08 10:38:50
; Title: 3D Entity Highlighting
; Description: Captures a 2D Image of the rendered entity

;-----------------------------------------------------------------------------------------------------------------------------------
;Example
;-----------------------------------------------------------------------------------------------------------------------------------

Graphics3D 800,600,32,6
SetBuffer BackBuffer()

Cube=CreateSphere()
EntityColor Cube,Rand(32,224),Rand(32,224),Rand(32,224)
EntityPickMode Cube,2,1

Cam=CreateCamera()
MoveEntity Cam,0,0,-5

Local MyImage%=0

PointEntity cam,cube

While Not(KeyDown(1))
	;Control Camera Movement
	MoveEntity Cam,(KeyDown(205)-KeyDown(203))*0.1,0,(KeyDown(200)-KeyDown(208))*0.1
	TurnEntity Cam,0,(MouseXSpeed())*0.25,0,True
	
	If KeyHit(57)
		FlushKeys()
		MyImage=EntityHighlightImage(Cube,Cam)
		If (MyImage) Then DrawImage MyImage,0,0
		Flip
		WaitKey()
		End
	Else
		UpdateWorld
		RenderWorld
	End If
	Flip
Wend	





















;-----------------------------------------------------------------------------------------------------------------------------------
;Functions
;-----------------------------------------------------------------------------------------------------------------------------------



Function EntityHighlightImage%(Entity%,Camera%,RGBa%=-1)
	If (Not(Entity)) Then Return 0
	If (Not (Camera)) Then Return 0
	If (Not(((EntityClass(Entity)="Mesh") Or (EntityClass(Entity)="Terrain") Or (EntityClass(Entity)="Plane") Or (EntityClass(Entity)="Mirror") Or (EntityClass(Entity)="MD2") Or (EntityClass(Entity)="BSP")))) Then Return 0
	If (Not(EntityVisible(Entity,Camera))) Then Return 0
	If (Not(EntityInView(Entity,Camera))) Then Return 0
	
	; Make a copy so as not to 'harm' the original entity
	Local WorkingCopy%=CopyEntity(Entity)
	
	;GraphicsWidth() and GraphicsHeight() should match the camera viewport
	Local W%=GraphicsWidth()
	Local H%=GraphicsHeight()
	
	Local X%,Y%
	Local RGB
	Local IterSurface%,Surface%,IterVertex%
	
	;OPTIONAL	
	; These are really only if you wish to make use of the CropImage() function to restrict output to the highlight only.
	;Else, the image dimensions will match the W & H Viewport given above.
;	{
	Local L%=W-1,R%=0,T%=H-1,B%=0
;	}
	
	; Create an Image to draw the highlight separately.
	
	Local ReturnImage=CreateImage(W,H)
	UpdateWorld
	RenderWorld
	LockBuffer ImageBuffer(ReturnImage)
	For IterSurface=1 To CountSurfaces(Entity)
		Surface=GetSurface(Entity,IterSurface)
		For IterVertex=0 To CountVertices(Surface)-1
			TFormPoint(VertexNX(Surface,IterVertex),VertexNY(Surface,IterVertex),VertexNZ(Surface,IterVertex),Entity,0)
			
			CameraProject Camera,TFormedX(),TFormedY(),TFormedZ()
			
			X=ProjectedX()
			Y=ProjectedY()
			
			RGB=RGBa((X-(GraphicsWidth() Shr True)/GraphicsWidth())*255,0,(Y-(GraphicsHeight() Shr True)/GraphicsHeight())*255)
			
			WritePixelFast X,Y,RGB,ImageBuffer(ReturnImage)		
			
			;OPTIONAL	
			; These are really only if you wish to make use of the CropImage() function to restrict output to the highlight only.
			;Else, the image dimensions will match the W & H Viewport given above.
;			{
			If (X<L) Then L=X
			If (X>R) Then R=X
			If (Y<T) Then T=Y
			If (Y>B) Then B=Y
;			}
			
		Next
	Next
	UnlockBuffer ImageBuffer(ReturnImage)
	
	
	;Free up duplicates
	FreeEntity WorkingCopy
	
	
			;OPTIONAL	
		; These are really only if you wish to make use of the CropImage() function to restrict output to the highlight only.
		;Else, the image dimensions will match the W & H Viewport given above.
;		{
	
	Local CroppedImage
	If (L*T*R*B)
		If (((B-T)<1) Or ((R-L)<1))
			L=0
			T=0
		End If
		
		CroppedImage=CropImage(ReturnImage,L,T,R,B)
		
			;Free up duplicates
		FreeImage ReturnImage
		ReturnImage=CroppedImage		
	End If
;		}
	
	Return ReturnImage
	
End Function

Function CropImage%(Image%,X1%,Y1%,X2%,Y2%)
	If (Not(Image)) Then Return 0
	Local W%=X2-X1
	Local H%=Y2-Y1
	Local CroppedImage%=CreateImage(W,H)
	Local XRW%,YRW%
	Local RGB%
	LockBuffer ImageBuffer(Image)
	LockBuffer ImageBuffer(CroppedImage)
	For XRW=0 To W-1
		For YRW=0 To H-1
			RGB=ReadPixelFast(XRW+X1,YRW+Y1,ImageBuffer(Image))
			WritePixelFast XRW,YRW,RGB,ImageBuffer(CroppedImage)
		Next
	Next
	UnlockBuffer ImageBuffer(Image)
	UnlockBuffer ImageBuffer(CroppedImage)
	Return CroppedImage
End Function













Function RGBa%(R%,G%,B%,a%=0)
	;	Returns aRGB Value from components.
	
	Return ((a% Shl 24) Or (R% Shl 16) Or (G% Shl 8) Or B%)
End Function

;_______________________________________________________________________________________________________________________
;_______________________________________________________________________________________________________________________

Function Red(RGBa_Value%)
	;	Returns Red component.
	
	Return ((RGBa_Value% Shr 16) And 255)
End Function

;_______________________________________________________________________________________________________________________
;_______________________________________________________________________________________________________________________

Function Green(RGBa_Value%)
	;	Returns Green component.
	
	Return ((RGBa_Value% Shr 8) And 255)
End Function

;_______________________________________________________________________________________________________________________
;_______________________________________________________________________________________________________________________

Function Blue(RGBa_Value%)
	;	Returns Blue component.
	
	Return (RGBa_Value% And 255)
End Function

;_______________________________________________________________________________________________________________________
;_______________________________________________________________________________________________________________________

Function Alpha%(RGBa_Value%)
	;	Returns Alpha component.
	
	Return (RGBa_Value% Shr 24 And 255)
End Function

;_____________
