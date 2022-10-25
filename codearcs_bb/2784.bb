; ID: 2784
; Author: _PJ_
; Date: 2010-11-01 20:27:10
; Title: Bubbles Screensaver Example
; Description: A Copy of the Windows 7  (But in 3D!)

AppTitle "EKD - 3D Bubbles Screensaver"


;	NOTES_____________________________________________________________________________________________________________

; 3D Bubbles Screensaver
; Based on Microsoft "Bubbles" Screensaver
; Written by PJ using Blitz3D
; Fully 3D


; Utilises Windows API Calls:

;	.lib "user32.dll"

;	user32_GetSystemMetrics% (nIndex%) : "GetSystemMetrics"
;	user32_GetDC% (hwnd%) : "GetDC"
;	user32_ReleaseDC% (hwnd%, hdc%) : "ReleaseDC"
;	user32_ShowWindow% (hwnd%, nCmdShow%) : "ShowWindow"
;	user32_GetFocus%() : "GetFocus"

;	.lib "gdi32.dll"
;	gdi32_GetPixel% (hdc%, x%, y%) : "GetPixel"

;	STATICS & CONSTANT DEFINITION AND DECLARATION;____________________________________________________________________

Const MAX_BUBBLES%=16
Const BUBBLE_RADIUS#=1.0
Const BUBBLE_SPEED#=0.025

Const BOUNDARY_X_MIN#=-8
Const BOUNDARY_X_MAX#=8
Const BOUNDARY_Y_MIN#=-6
Const BOUNDARY_Y_MAX#=6
Const BUBBLE_LOC#=7.5

Global GW%,GH%
Global OverlaySprite
Global Sun%
Global Cam
Global Quad%

Global hWnd%=SystemProperty("AppHWND")

Global ExitCondition=False

Global MX=MouseX()
Global MY=MouseY()
Global MZ=MouseZ()

Type Bubble
	Field Entity
	Field CR,CG,CB
	Field TR,TG,TB
	Field DX#
	Field DY#
	Field Light
End Type

;RUNTIME;____________________________________________________________________________________________________________

main

;METHODS_____________________________________________________________________________________________________________

;	SYSTEM
	
Function main()
	;Initialise
	Initialise
	
	; Reset Key state
	
	FlushMouse()
	FlushKeys()
	
	MX=MouseX()
	MY=MouseY()
	MZ=MouseZ()
	;Required to clear Mouse_*_Speed() values
	MoveMouse MouseX(),MouseY();GW*0.5,GH*0.5
	
	; Invoke Main Loop
	While Not (ExitCondition)
		Loop
	Wend
	
	
	; Closedown
	; Free Resources
	Local ALLBUBBLES.Bubble
	For ALLBUBBLES.Bubble=Each Bubble
		FreeEntity ALLBUBBLES\Entity
		Delete ALLBUBBLES
	Next
	
	ClearWorld True,True,True
	EndGraphics
	End
End Function

Function Initialise()
	GW%=user32_GetSystemMetrics(16)
	GH%=user32_GetSystemMetrics(17)
	
	Graphics3D GW,GH,32,2
	SetBuffer BackBuffer()
	
	SeedRnd	MilliSecs()
	
	AmbientLight 128,128,128
	
	Cam=CreateCamera()
	CameraRange Cam,0.1,10.0
	MoveEntity Cam,0,0,-0.5
	Quad=CreateDesktopQuad(1,Float((Float(GH)/Float(GW))),Cam)
	;Sun%=CreateLight(1)
	;LightColor Sun,192,192,192
	CreateBubbles()
End Function

Function Loop()
		
	UpdateBubbles()
	
	UpdateWorld
	RenderWorld
	
	ExitCondition =(((GetKey()) Or (KeyDown(1)) Or (GetMouse())) Or (MouseXSpeed()+MouseYSpeed()+MouseZSpeed()) Or (user32_GetFocus()<>hWnd) Or (MouseX()<>MX)Or (MouseY()<>MY)Or (MouseZ()<>MZ))
	
	CollisionCheck()

	Delay 10
	Flip False
	
End Function	

;	MAINTENANCE

Function UpdateBubbles()
	Local UB.Bubble
	For UB.Bubble =Each Bubble
		UpdateColour(UB.Bubble)
		TranslateEntity UB\Entity,UB\DX*BUBBLE_SPEED,UB\DY*BUBBLE_SPEED,0
	Next
End Function

;	GENERATION

Function CreateBubbles()
	;OverlaySprite=CreateOverlay()
	
	Local IterBubbles%
	
	Local Bub.Bubble
	Local Sprite
	
	For IterBubbles=0 To MAX_BUBBLES-1
		
		Bub.Bubble=New Bubble
		
		Bub\Entity=CreateSphere(25)
		
		;Sprite=CopyEntity(OverlaySprite)
		
		Bub\CR=(Rand(0,224)+32)
		Bub\CG=(Rand(0,224)+32)
		Bub\CB=(Rand(0,224)+32)
		Bub\TR=Rand(224)+32
		Bub\TG=Rand(224)+32
		Bub\TB=Rand(224)+32
		Bub\DX=Rnd(0.0-1.0,1.0)
		Bub\DY=Rnd(0.0-1.0,1.0)
		EntityType Bub\Entity,1
		
		ScaleMesh Bub\Entity,BUBBLE_RADIUS,BUBBLE_RADIUS,BUBBLE_RADIUS
		EntityRadius Bub\Entity,BUBBLE_RADIUS
		Bub\Light=CreateLight(2)
		PositionEntity Bub\Light,-25,25,0,True
		LightConeAngles Bub\Light,0,5
		LightColor Bub\Light,255,255,255
		LightRange Bub\Light,5
		PositionEntity Bub\Entity,Rand(0-IterBubbles,IterBubbles),Rand(0-(IterBubbles Shr 1),IterBubbles Shr 1),BUBBLE_LOC,True
		PointEntity Bub\Light,Bub\Entity
		EntityParent Bub\Light,Bub\Entity
		EntityAlpha Bub\Entity,0.5
		
		;PositionEntity Sprite,EntityX(Bub\Entity,True),EntityY(Bub\Entity,True),EntityZ(Bub\Entity,True)-1,True
		
		;EntityParent Sprite,Bub\Entity
		
		EntityShininess Bub\Entity,0.75
		EntityColor Bub\Entity,Bub\CR,Bub\CG,Bub\CB
		
	Next
	
	UpdateWorld
	Collisions 1,1,1,1
End Function	

Function CreateDesktopQuad(W#=1,H#=1,Parent%=0)
	
	;Initialise
	
	Local HWnd%=SystemProperty("AppHWND")
	Local X%
	Local Y%
	Local bByte%
	user32_ShowWindow(HWnd,0)
	
	; Build Quad Mesh
	Local Mesh%=CreateQuad(W,H,Parent)
	
	; Read Desktop Image
	
	Local Desktop_DC%=user32_GetDC(0)
	Local Image%=CreateImage(GW,GH)
	Local Buffer%=ImageBuffer(Image)
	
	LockBuffer Buffer
	
	For Y=0 To GH-1
		For X=0 To GW-1
			bByte=gdi32_GetPixel(Desktop_DC,X,Y)
			bByte=ConvertaBRGToaRGB(bByte)
			WritePixelFast X,Y,bByte,Buffer
		Next
	Next
	
	user32_ReleaseDC(0,Desktop_DC)
	
	UnlockBuffer Buffer
	
	; Apply Scaled Texture From Image
	Local Texture%=CreateTexture(GW,GH)
	Local TW=TextureWidth(Texture)
	Local TH=TextureHeight(Texture)
	Local TxBuffer%=TextureBuffer(Texture)
	ResizeImage Image,TW,TH
	Buffer=ImageBuffer(Image)
	
	LockBuffer Buffer
	LockBuffer TxBuffer
	For X=0 To TW-1
		For Y=0 To TH-1	
			CopyPixelFast TW-(X+1),Y,Buffer,X,Y,TxBuffer
		Next
	Next
	
	EntityTexture Mesh,Texture
	
	UnlockBuffer Buffer
	UnlockBuffer TxBuffer
	
	;Fix Memory Leaks
	FreeImage Image
	
	FreeTexture Texture
	
	;Display Window
	user32_ShowWindow(HWnd,5)
	
	; Return Final Mesh
	EntityFX Mesh,1
	EntityOrder Mesh,MAX_BUBBLES+1
	Return Mesh
End Function

Function CreateQuad(Width#,Height#,Parent%=0)
	Local Mesh%=CreateMesh()
	Local Surface%=CreateSurface(Mesh)
	
	Local v0%=AddVertex(Surface,-1,1,0,1,0)
	Local v1%=AddVertex(Surface,1,1,0,0,0)
	Local v2%=AddVertex(Surface,-1,-1,0,1,1)
	Local v3%=AddVertex(Surface,1,-1,0,0,1)
	
	VertexColor Surface,v0,255,255,255
	VertexColor Surface,v1,255,255,255
	VertexColor Surface,v2,255,255,255
	VertexColor Surface,v3,255,255,255
	
	AddTriangle(Surface,0,1,2)
	AddTriangle(Surface,3,2,1)
	
	ScaleMesh Mesh,0.5*Width,0.5*Height,0.1
	If (Parent) Then EntityParent Mesh,Parent
	Return Mesh
End Function

Function CreateOverlay()	; DEPRECATED
;	Local X,Y,Byte
;	Restore Overlay
;	Local Sprite=CreateQuad(BUBBLE_RADIUS Shr 1,BUBBLE_RADIUS Shr 1)
;	Local Texture=CreateTexture(64,64,771)
;	Local Buffer=TextureBuffer(Texture)
;	LockBuffer Buffer
;	For X= 0 To 63
;		For Y=0 To 63
;			Read Byte
;			Byte=ConvertToRGB(Byte,Byte,Byte)
;			WritePixelFast X,Y,Byte,Buffer
;		Next
;	Next
;	UnlockBuffer Buffer
;	EntityTexture Sprite,Texture
;	FreeTexture Texture
;	Return Sprite
End Function


;	COLLISION DETECTION


Function CollisionCheck()
	; Simple 2D collision trig
	Local CollBub.Bubble
	Local BounceBubble
	For CollBub.Bubble=Each Bubble
		BounceBubble=EntityCollided(CollBub\Entity,1)
		If (BounceBubble)
			CollBub\DX=CollBub\DX-DifferenceX(CollBub\Entity,BounceBubble)
			CollBub\DY=CollBub\DY-DifferenceY(CollBub\Entity,BounceBubble)
		Else
			If CheckBoundsX(CollBub\Entity,BOUNDARY_X_MIN,BOUNDARY_X_MAX) Then CollBub\DX=0-CollBub\DX
			If CheckBoundsY(CollBub\Entity,BOUNDARY_Y_MIN,BOUNDARY_Y_MAX) Then CollBub\DY=0-CollBub\DY
		End If
	Next		
End Function

Function CheckBoundsX(Entity,Min,Max)
	Return ((EntityX(Entity,True)<Min)Or (EntityX(Entity,True)>Max))
End Function

Function CheckBoundsY(Entity,Min,Max)
	Return ((EntityY(Entity,True)<Min) Or (EntityY(Entity,True)>Max))
End Function

Function DifferenceX#(Source,Target)
	Return (EntityX(Target,True)-EntityX(Source,True))
End Function

Function DifferenceY#(Source,Target)
	Return (EntityY(Target,True)-EntityY(Source,True))
End Function

;	BUBBLE COLOURS

Function UpdateColour(BC.Bubble)
	Local R1=BC\CR
	Local G1=BC\CG
	Local B1=BC\CB
	Local R2=BC\TR
	Local G2=BC\TG
	Local B2=BC\TB
	Local DR=Sgn(R2-R1)
	Local DG=Sgn(G2-G1)
	Local DB=Sgn(B2-B1)
	
	BC\CR=R1+DR
	BC\CG=G1+DG
	BC\CB=B1+DB
	
	If (Not(DR*DG*DB))
		BC\TR=Rand(0,224)+32
		BC\TG=Rand(0,224)+32
		BC\TB=Rand(0,224)+32
	End If
	
	EntityColor BC\Entity,BC\CR,BC\CG,BC\CB
End Function

Function Red(RGB)		
	Return RGB And 255
End Function

Function Green(RGB)
	Return (RGB Shr 8) And 255
End Function

Function Blue(RGB)
	Return (RGB Shr 16) And 255
End Function

Function ConvertaBRGToaRGB(aRGB)
	Local R=(aRGB Shr 16)And 255
	Local G=(aRGB Shr 8)And 255
	Local B=(aRGB Shr 0)And 255
	Local a=(aRGB Shr 24)And 255
	Return R+(G Shl 8)+(B Shl 16)+a Shl 24
End Function

Function ConvertToRGB(R,G,B)
	Return R+(G Shl 8)+(B Shl 16)
End Function

;	DATA (DEPRECATED);_____________________________________________________________________________________________________________

.Overlay
;Data 0,0,0,0,0,1,1,1
;Data 1,1,1,1,2,2,2,2
;Data 2,2,2,2,3,3,3,3
;Data 39,87,86,85,145,229,229,229
;Data 229,229,230,145,85,86,86,39
;Data 3,3,3,3,3,2,2,2
;Data 2,2,2,2,2,1,1,1
;Data 1,1,1,1,0,0,0,0
;Data 0,0,0,0,1,1,1,1
;Data 1,1,1,2,2,2,2,2
;Data 2,2,3,3,21,23,94,152
;Data 184,209,202,197,202,211,214,213
;Data 213,214,211,202,197,201,206,181
;Data 151,94,23,21,3,3,3,2
;Data 2,2,2,2,2,2,1,1
;Data 1,1,1,1,1,0,0,0
;Data 0,0,0,1,1,1,1,1
;Data 1,2,2,2,2,2,2,2
;Data 3,3,45,115,201,189,197,200
;Data 202,206,206,204,203,203,202,202
;Data 202,202,202,202,203,204,205,200
;Data 197,196,185,197,115,45,3,3
;Data 3,2,2,2,2,2,2,2
;Data 1,1,1,1,1,1,0,0
;Data 0,0,1,1,1,1,1,1
;Data 2,2,2,2,2,2,3,3
;Data 7,59,192,209,206,203,203,202
;Data 200,198,196,195,193,192,190,190
;Data 190,190,191,192,194,196,198,199
;Data 201,201,201,203,205,188,59,7
;Data 3,3,3,2,2,2,2,2
;Data 2,1,1,1,1,1,1,0
;Data 0,1,1,1,1,1,1,2
;Data 2,2,2,2,2,3,5,62
;Data 150,186,199,206,203,199,196,192
;Data 188,183,179,175,171,168,165,163
;Data 162,164,167,170,174,177,182,187
;Data 192,195,198,201,203,197,181,145
;Data 62,5,3,3,2,2,2,2
;Data 2,2,1,1,1,1,1,1
;Data 1,1,1,1,1,1,2,2
;Data 2,2,2,3,3,61,145,200
;Data 198,200,201,198,193,186,179,171
;Data 160,151,143,137,132,129,125,123
;Data 122,124,127,130,135,139,147,156
;Data 167,176,184,192,197,199,197,195
;Data 197,145,61,3,3,3,2,2
;Data 2,2,2,1,1,1,1,1
;Data 1,1,1,1,1,2,2,2
;Data 2,2,3,3,61,192,203,203
;Data 201,197,192,185,174,160,146,132
;Data 119,108,99,92,87,84,81,79
;Data 78,79,82,85,88,93,102,112
;Data 124,138,153,169,181,190,195,199
;Data 199,198,187,61,3,3,3,2
;Data 2,2,2,2,1,1,1,1
;Data 1,1,1,1,2,2,2,2
;Data 2,3,3,60,183,199,203,199
;Data 193,186,175,159,142,122,104,89
;Data 75,65,57,51,48,46,44,43
;Data 42,43,45,47,49,53,60,68
;Data 80,93,111,130,149,168,182,191
;Data 196,199,196,176,60,3,3,3
;Data 2,2,2,2,2,1,1,1
;Data 1,1,1,2,2,2,2,2
;Data 3,18,154,186,196,201,196,189
;Data 179,164,145,124,103,82,64,51
;Data 41,34,29,25,23,22,21,21
;Data 20,21,22,23,24,27,30,36
;Data 44,55,69,88,110,133,154,173
;Data 186,194,197,191,181,153,18,3
;Data 3,2,2,2,2,2,1,1
;Data 1,1,2,2,2,2,2,3
;Data 26,165,204,201,199,193,185,172
;Data 154,133,110,87,66,48,35,26
;Data 20,16,14,12,11,11,11,11
;Data 10,11,11,11,12,13,14,18
;Data 22,29,39,52,71,93,117,141
;Data 164,180,191,196,196,201,162,26
;Data 3,3,2,2,2,2,2,1
;Data 1,1,2,2,2,2,3,5
;Data 154,205,204,199,192,182,166,147
;Data 123,100,77,57,39,27,19,13
;Data 11,9,8,8,8,8,8,8
;Data 8,8,8,8,8,8,8,10
;Data 11,14,20,28,41,58,79,103
;Data 129,154,175,189,195,199,200,150
;Data 5,3,3,2,2,2,2,1
;Data 1,2,2,2,2,3,3,56
;Data 187,202,200,192,180,162,140,116
;Data 90,68,48,33,23,16,12,9
;Data 8,7,7,7,7,7,7,7
;Data 7,7,7,7,7,7,7,7
;Data 8,9,11,15,22,32,47,67
;Data 92,119,146,171,186,195,196,181
;Data 57,3,3,3,2,2,2,2
;Data 2,2,2,2,2,3,62,185
;Data 197,200,192,181,162,136,108,82
;Data 59,44,40,38,30,19,11,9
;Data 8,7,7,7,7,7,7,7
;Data 7,7,7,7,7,7,7,7
;Data 7,7,8,9,12,17,26,39
;Data 59,84,113,143,170,187,195,190
;Data 177,62,3,3,2,2,2,2
;Data 2,2,2,2,3,57,188,200
;Data 200,195,183,165,138,107,77,53
;Data 46,68,109,129,118,75,31,11
;Data 9,8,7,8,8,8,8,8
;Data 8,8,8,8,8,8,7,7
;Data 7,7,7,7,8,10,14,21
;Data 35,54,81,113,146,173,189,197
;Data 194,180,58,3,3,2,2,2
;Data 2,2,2,3,11,152,202,204
;Data 195,186,169,143,112,78,51,38
;Data 81,175,239,253,251,206,107,31
;Data 11,10,9,8,8,8,8,8
;Data 8,8,8,8,8,8,8,8
;Data 8,7,7,7,7,8,9,12
;Data 20,32,54,84,119,151,177,192
;Data 198,196,147,12,3,3,2,2
;Data 2,2,2,3,56,196,204,199
;Data 189,174,149,118,84,55,33,48
;Data 166,242,255,255,255,255,206,75
;Data 18,10,10,9,8,8,8,8
;Data 8,8,8,8,8,8,8,8
;Data 8,8,8,7,7,7,7,9
;Data 12,20,35,59,91,125,158,182
;Data 194,196,188,56,3,3,2,2
;Data 2,2,3,24,153,198,202,193
;Data 179,156,126,91,61,37,23,73
;Data 216,253,255,255,255,255,251,117
;Data 27,10,10,10,9,9,9,9
;Data 9,9,9,9,9,9,8,8
;Data 8,8,8,8,7,7,7,7
;Data 9,13,22,39,65,98,133,166
;Data 187,196,192,146,24,3,3,2
;Data 2,2,3,126,202,204,197,185
;Data 167,137,101,68,41,23,17,79
;Data 221,254,255,255,255,255,253,127
;Data 30,11,11,11,10,9,9,9
;Data 9,9,9,9,9,9,9,9
;Data 9,8,8,8,8,7,7,7
;Data 7,9,14,24,43,72,108,145
;Data 175,192,198,197,123,3,3,2
;Data 2,3,41,192,205,203,192,176
;Data 150,116,78,48,28,16,11,58
;Data 201,250,255,255,255,255,239,105
;Data 23,11,11,11,10,9,9,9
;Data 9,9,9,9,9,9,9,9
;Data 9,9,8,8,8,8,7,7
;Data 7,8,10,15,28,51,85,124
;Data 160,185,198,199,184,41,3,3
;Data 2,7,121,207,206,198,184,164
;Data 133,95,59,34,19,13,9,27
;Data 132,222,250,254,253,243,173,58
;Data 15,11,11,12,11,10,10,10
;Data 10,10,10,10,10,10,9,9
;Data 9,9,9,8,8,8,8,7
;Data 7,7,8,11,19,36,65,103
;Data 142,174,193,201,201,119,8,3
;Data 3,22,200,207,204,192,175,150
;Data 115,77,45,24,14,10,9,12
;Data 44,131,201,220,217,164,72,22
;Data 11,13,13,13,11,10,10,10
;Data 10,10,10,10,10,10,10,10
;Data 9,9,9,9,9,8,8,8
;Data 7,7,7,8,13,25,49,84
;Data 124,160,186,199,200,197,22,3
;Data 3,14,187,205,200,186,164,134
;Data 97,61,33,19,11,9,9,9
;Data 12,28,60,79,70,40,18,11
;Data 13,13,13,13,11,10,10,10
;Data 10,10,10,10,10,10,10,10
;Data 10,9,9,9,9,8,8,8
;Data 8,7,7,7,10,18,36,66
;Data 105,144,176,194,198,178,14,3
;Data 3,95,191,204,194,178,151,116
;Data 80,47,26,14,10,9,9,10
;Data 10,10,11,14,13,11,11,13
;Data 13,13,13,12,12,11,11,11
;Data 11,11,11,11,11,10,10,10
;Data 10,10,9,9,9,9,8,8
;Data 8,8,7,7,8,13,26,50
;Data 87,127,164,188,196,180,92,3
;Data 3,155,199,203,190,170,138,100
;Data 65,37,20,11,9,9,9,10
;Data 10,10,11,11,11,11,13,13
;Data 13,13,14,13,12,11,11,11
;Data 11,11,11,11,11,11,11,10
;Data 10,10,10,9,9,9,9,8
;Data 8,8,7,7,8,11,19,38
;Data 71,112,152,182,197,191,145,3
;Data 46,180,203,201,185,160,124,85
;Data 52,29,15,10,9,9,10,10
;Data 10,11,11,11,11,13,13,13
;Data 13,14,14,13,12,11,11,11
;Data 11,11,11,11,11,11,11,11
;Data 10,10,10,10,9,9,9,9
;Data 8,8,8,7,7,9,15,30
;Data 58,97,140,174,194,196,172,46
;Data 92,208,207,197,179,151,111,71
;Data 40,22,13,9,9,9,10,10
;Data 10,11,11,11,13,13,13,13
;Data 14,14,14,12,11,11,12,12
;Data 12,12,12,11,11,11,11,11
;Data 11,10,10,10,10,9,9,9
;Data 8,8,8,7,7,8,12,23
;Data 47,84,127,164,190,201,202,93
;Data 86,203,206,194,174,142,100,59
;Data 31,18,11,9,9,9,10,10
;Data 10,11,11,11,13,13,13,14
;Data 14,14,13,12,12,12,12,12
;Data 12,12,12,12,12,11,11,11
;Data 11,11,10,10,10,9,9,9
;Data 8,8,8,7,7,7,10,19
;Data 39,73,116,156,185,200,197,85
;Data 74,198,205,192,170,136,92,52
;Data 26,13,10,9,9,10,10,10
;Data 11,11,11,13,13,13,13,14
;Data 14,13,12,12,12,12,12,12
;Data 12,12,12,12,12,12,11,11
;Data 11,11,10,10,10,10,9,9
;Data 9,8,8,8,7,7,9,16
;Data 33,64,106,149,182,198,191,72
;Data 145,202,204,190,167,132,87,48
;Data 23,11,9,9,9,10,10,10
;Data 11,11,11,13,13,13,14,14
;Data 13,12,12,12,12,12,12,12
;Data 12,12,12,12,12,12,12,11
;Data 11,11,11,10,10,10,9,9
;Data 9,8,8,8,7,7,8,14
;Data 29,58,100,145,179,197,196,142
;Data 229,213,204,189,166,129,84,46
;Data 22,11,8,8,8,10,10,10
;Data 11,11,11,13,13,13,14,13
;Data 12,11,12,12,12,12,12,12
;Data 12,12,12,12,12,12,12,11
;Data 11,11,11,10,10,10,9,9
;Data 9,8,8,8,7,7,8,13
;Data 28,56,97,142,177,198,206,226
;Data 229,217,204,189,164,126,82,44
;Data 21,11,8,7,7,9,9,10
;Data 11,11,11,12,12,12,12,11
;Data 11,12,12,12,12,12,13,13
;Data 13,13,13,12,12,12,12,12
;Data 11,11,11,10,10,10,9,9
;Data 9,8,8,8,7,7,8,13
;Data 27,55,96,141,176,197,210,226
;Data 229,216,202,187,162,124,80,43
;Data 21,11,8,7,7,8,8,8
;Data 10,10,10,11,11,10,11,11
;Data 11,12,12,12,12,12,13,13
;Data 13,13,13,12,12,12,12,12
;Data 11,11,11,10,10,10,9,9
;Data 9,8,8,8,7,7,8,13
;Data 27,55,96,141,176,196,210,226
;Data 229,216,202,187,161,123,79,43
;Data 20,10,8,7,7,8,8,8
;Data 9,9,9,10,10,10,11,11
;Data 11,12,12,12,12,12,13,13
;Data 13,13,13,12,12,12,12,12
;Data 11,11,11,10,10,10,9,9
;Data 9,8,8,8,7,7,8,13
;Data 27,55,96,141,175,196,210,226
;Data 229,216,203,187,162,124,80,43
;Data 21,11,8,7,7,8,8,8
;Data 9,9,9,10,10,10,11,11
;Data 11,12,12,12,12,12,13,13
;Data 13,13,13,12,12,12,12,12
;Data 11,11,11,10,10,10,9,9
;Data 9,8,8,8,7,7,8,13
;Data 27,55,96,141,176,196,210,226
;Data 229,213,203,189,165,127,82,44
;Data 21,11,8,7,7,8,8,8
;Data 9,9,9,10,10,10,11,11
;Data 11,12,12,12,12,12,13,13
;Data 13,13,13,12,12,12,12,12
;Data 11,11,11,10,10,10,9,9
;Data 9,8,8,8,7,7,8,13
;Data 27,55,96,141,176,197,206,226
;Data 145,204,203,189,166,129,85,46
;Data 22,11,8,7,7,8,8,8
;Data 9,9,9,10,10,10,11,11
;Data 11,11,12,12,12,12,12,12
;Data 12,12,12,12,12,12,12,11
;Data 11,11,11,10,10,10,9,9
;Data 9,8,8,8,7,7,8,13
;Data 28,56,98,142,177,197,198,139
;Data 75,202,204,190,168,132,88,49
;Data 24,11,8,7,7,8,8,8
;Data 9,9,9,10,10,10,11,11
;Data 11,11,12,12,12,12,12,12
;Data 12,12,12,12,12,12,12,11
;Data 11,11,11,10,10,10,9,9
;Data 9,8,8,8,7,7,8,14
;Data 30,59,101,145,179,198,194,72
;Data 86,209,206,192,171,137,94,54
;Data 27,13,8,7,7,8,8,8
;Data 9,9,9,10,10,10,10,11
;Data 11,11,11,12,12,12,12,12
;Data 12,12,12,12,12,12,11,11
;Data 11,11,10,10,10,10,9,9
;Data 9,8,8,8,7,7,9,17
;Data 34,66,108,150,182,200,201,82
;Data 92,216,207,195,175,144,103,62
;Data 32,15,9,7,7,7,8,8
;Data 8,9,9,9,10,10,10,11
;Data 11,11,11,11,12,12,12,12
;Data 12,12,12,12,12,11,11,11
;Data 11,11,10,10,10,9,9,9
;Data 8,8,8,7,7,8,10,20
;Data 41,76,119,158,186,200,209,93
;Data 46,185,204,198,180,153,115,73
;Data 40,20,10,7,7,7,8,8
;Data 8,9,9,9,10,10,10,10
;Data 11,11,11,11,11,11,12,12
;Data 12,12,12,11,11,11,11,11
;Data 11,10,10,10,10,9,9,9
;Data 8,8,8,7,7,8,12,25
;Data 50,88,131,166,190,197,172,46
;Data 3,155,203,201,187,163,128,87
;Data 50,26,13,8,7,7,8,8
;Data 8,9,9,9,9,10,10,10
;Data 10,11,11,11,11,11,11,11
;Data 11,11,11,11,11,11,11,11
;Data 10,10,10,10,9,9,9,9
;Data 8,8,8,7,7,9,16,32
;Data 61,102,143,175,194,196,146,3
;Data 3,95,198,203,192,172,142,102
;Data 63,34,17,10,7,7,7,8
;Data 8,8,9,9,9,9,10,10
;Data 10,10,11,11,11,11,11,11
;Data 11,11,11,11,11,11,11,10
;Data 10,10,10,9,9,9,9,8
;Data 8,8,7,7,8,11,21,41
;Data 75,116,154,183,195,186,92,3
;Data 3,15,194,205,196,180,155,119
;Data 79,45,23,12,8,7,7,8
;Data 8,8,8,9,9,9,9,10
;Data 10,10,10,10,11,11,11,11
;Data 11,11,11,11,11,10,10,10
;Data 10,10,9,9,9,9,8,8
;Data 8,8,7,7,9,15,29,55
;Data 92,132,166,189,198,182,14,3
;Data 3,22,204,211,202,188,167,136
;Data 97,59,32,16,10,7,7,7
;Data 8,8,8,8,9,9,9,9
;Data 10,10,10,10,10,10,10,10
;Data 10,10,10,10,10,10,10,10
;Data 10,9,9,9,9,8,8,8
;Data 8,7,7,8,11,20,39,71
;Data 110,149,179,196,202,197,22,3
;Data 3,8,120,217,206,194,178,152
;Data 116,77,44,23,12,8,7,7
;Data 7,8,8,8,9,9,9,9
;Data 9,10,10,10,10,10,10,10
;Data 10,10,10,10,10,10,10,10
;Data 9,9,9,9,9,8,8,8
;Data 7,7,7,9,15,28,53,89
;Data 129,164,188,200,209,115,8,3
;Data 2,3,41,197,208,200,187,166
;Data 135,97,60,33,17,10,8,7
;Data 7,7,8,8,8,8,9,9
;Data 9,9,9,10,10,10,10,10
;Data 10,10,10,10,10,10,9,9
;Data 9,9,9,8,8,8,8,7
;Data 7,7,8,12,21,40,71,109
;Data 147,177,195,201,190,41,3,3
;Data 2,3,3,127,216,203,194,177
;Data 152,118,80,48,26,15,9,8
;Data 7,7,7,8,8,8,8,9
;Data 9,9,9,9,9,9,9,9
;Data 9,9,9,9,9,9,9,9
;Data 9,9,8,8,8,8,7,7
;Data 7,8,11,17,32,57,91,129
;Data 163,187,197,207,123,3,3,3
;Data 2,2,3,24,156,206,201,187
;Data 167,139,104,69,41,23,13,9
;Data 7,7,7,7,8,8,8,8
;Data 9,9,9,9,9,9,9,9
;Data 9,9,9,9,9,9,9,9
;Data 9,8,8,8,8,7,7,7
;Data 8,10,15,28,49,79,115,149
;Data 177,193,199,148,24,3,3,2
;Data 2,2,3,3,56,214,205,195
;Data 180,159,129,95,62,37,20,12
;Data 9,7,7,7,7,8,8,8
;Data 8,8,8,9,9,9,9,9
;Data 9,9,9,9,9,9,8,8
;Data 8,8,8,8,7,7,7,8
;Data 9,14,25,44,72,105,139,168
;Data 188,197,199,56,3,3,3,2
;Data 2,2,2,3,12,152,212,201
;Data 191,175,152,122,88,56,33,19
;Data 12,9,7,7,7,7,8,8
;Data 8,8,8,8,8,8,8,8
;Data 8,8,8,8,8,8,8,8
;Data 8,8,8,7,7,7,8,10
;Data 14,23,40,66,99,132,162,183
;Data 196,204,144,13,3,3,2,2
;Data 2,2,2,3,3,57,196,207
;Data 198,187,171,146,115,81,52,32
;Data 20,13,9,8,7,7,7,7
;Data 8,8,8,8,8,8,8,8
;Data 8,8,8,8,8,8,8,8
;Data 8,7,7,7,7,8,10,15
;Data 23,38,62,93,126,156,179,192
;Data 200,188,58,3,3,3,2,2
;Data 2,2,2,2,3,3,62,196
;Data 201,195,184,167,141,110,80,55
;Data 36,23,15,10,9,7,7,7
;Data 7,7,7,8,8,8,8,8
;Data 8,8,8,8,8,8,7,7
;Data 7,7,7,8,9,11,16,26
;Data 41,62,90,121,152,176,190,195
;Data 187,62,3,3,3,2,2,2
;Data 2,2,2,2,2,3,3,57
;Data 198,203,195,183,164,140,113,86
;Data 62,42,27,18,13,9,8,7
;Data 7,7,7,7,7,7,7,7
;Data 7,7,7,7,7,7,7,7
;Data 7,7,9,10,14,21,31,47
;Data 68,94,122,150,174,190,197,187
;Data 57,3,3,3,2,2,2,2
;Data 1,2,2,2,2,3,3,5
;Data 153,219,203,195,183,166,145,122
;Data 96,71,50,34,23,16,11,9
;Data 8,8,7,7,7,7,7,7
;Data 7,7,7,7,7,7,7,8
;Data 8,10,13,18,26,39,56,78
;Data 103,129,153,175,190,198,212,147
;Data 5,3,3,3,2,2,2,2
;Data 1,1,2,2,2,2,3,3
;Data 26,165,219,203,195,185,171,154
;Data 131,107,83,62,44,31,21,16
;Data 13,11,9,9,8,8,8,8
;Data 8,8,8,8,8,9,10,11
;Data 13,18,24,34,49,69,91,115
;Data 139,161,179,191,199,214,158,26
;Data 3,3,3,2,2,2,2,1
;Data 1,1,2,2,2,2,2,3
;Data 3,18,153,196,202,197,189,178
;Data 162,142,120,98,76,57,43,33
;Data 26,21,17,15,14,13,13,13
;Data 13,13,13,13,14,17,19,23
;Data 28,36,48,63,83,106,129,151
;Data 169,184,193,197,187,147,19,3
;Data 3,3,2,2,2,2,2,1
;Data 1,1,1,2,2,2,2,2
;Data 3,3,3,60,191,207,201,194
;Data 184,171,154,135,114,94,77,63
;Data 52,44,37,32,29,27,27,27
;Data 27,27,27,28,30,34,40,46
;Data 55,67,82,101,122,143,163,179
;Data 190,196,203,183,61,3,3,3
;Data 3,2,2,2,2,2,1,1
;Data 1,1,1,1,2,2,2,2
;Data 2,3,3,3,61,195,217,203
;Data 199,191,180,167,151,134,118,103
;Data 91,80,70,63,58,55,55,55
;Data 55,55,55,56,59,66,74,84
;Data 96,109,124,141,159,175,187,196
;Data 201,211,191,62,3,3,3,3
;Data 2,2,2,2,2,1,1,1
;Data 1,1,1,1,1,2,2,2
;Data 2,2,3,3,3,61,144,216
;Data 208,201,197,190,180,169,157,145
;Data 134,123,113,105,100,97,96,96
;Data 96,96,96,98,101,108,118,128
;Data 139,151,163,176,186,194,197,206
;Data 211,141,61,3,3,3,3,2
;Data 2,2,2,2,1,1,1,1
;Data 1,1,1,1,1,1,2,2
;Data 2,2,2,3,3,3,5,62
;Data 150,195,208,204,199,193,186,178
;Data 170,162,154,148,145,143,142,141
;Data 141,142,142,143,146,151,158,167
;Data 175,183,190,198,200,206,188,143
;Data 62,5,3,3,3,3,2,2
;Data 2,2,2,1,1,1,1,1
;Data 0,1,1,1,1,1,1,2
;Data 2,2,2,2,2,3,3,3
;Data 7,59,192,220,213,205,202,200
;Data 194,190,186,182,180,178,177,177
;Data 177,177,178,179,181,184,188,193
;Data 197,199,203,211,218,189,60,7
;Data 3,3,3,3,2,2,2,2
;Data 2,2,1,1,1,1,1,1
;Data 0,0,1,1,1,1,1,1
;Data 2,2,2,2,2,2,3,3
;Data 3,3,45,114,198,193,210,212
;Data 205,204,204,201,200,198,198,198
;Data 198,198,198,199,200,201,202,205
;Data 210,206,185,195,115,45,3,3
;Data 3,3,3,2,2,2,2,2
;Data 2,1,1,1,1,1,1,0
;Data 0,0,0,1,1,1,1,1
;Data 1,2,2,2,2,2,2,2
;Data 3,3,3,3,21,23,95,150
;Data 185,222,217,215,212,210,209,209
;Data 209,209,210,211,213,217,219,183
;Data 146,95,23,21,3,3,3,3
;Data 3,2,2,2,2,2,2,2
;Data 1,1,1,1,1,1,0,0
;Data 0,0,0,0,1,1,1,1
;Data 1,1,1,2,2,2,2,2
;Data 2,2,3,3,3,3,3,3
;Data 40,86,85,85,142,231,227,227
;Data 227,227,227,142,85,85,86,40
;Data 3,3,3,3,3,3,3,2
;Data 2,2,2,2,2,2,1,1
;Data 1,1,1,1,1,0,0,0
;Data -1
