; ID: 2432
; Author: DareDevil
; Date: 2009-03-11 03:19:39
; Title: Shadow Map
; Description: Shadow Map Real time

[codebox]
;===>O=========================o<=================================================================
;===>O=========================o<=================================================================
;===>	Name file: 
;===>
;===>	Programmatore:
;===>	  Caldarulo Vincenzo (Vision&Design Software)
;===>	Descrizione:
;===>
;===>O=========================o<=================================================================
;===>O=========================o<=================================================================
; 

;=================================================================================================================================
;----------------------------------------------------------------------------------------------------------------------------
;=====================================================
;===>

Type SHWS_Shadow 
	Field Caster
	Field ObjOrig
	Field ObjShw
End Type 
;=====================================================
;===>
Const SHWS_LevelTexture% = 1
Const SHWS_TexSize# = 512
Const SHWS_ConstSize# = 1.0 / Float(SHWS_TexSize)
Const SHWS_Far# = 300
Const SHWS_ConstColor# = 1.0/256.0
Const SHWS_FactorZ# = SHWS_Far*SHWS_ConstColor

Dim SHWS_Pix(SHWS_TexSize*SHWS_TexSize)
;=====================================================
;===>
Global SHWS_Camera%
Global SHWS_ShadowCam%
Global SHWS_ENABLE%			= True
Global SHWS_ENABLE_ONE%	= True
Global SHWS_TexLight%
Global SHWS_TexCam%
Global SHWS_TexBase%
Global SHWS_Tollerance% = (8 Shl 16) + (8 Shl 16) + 8
;Global SHWS_Tollerance% = 8


Global txdraw%

	; ===>
	; Create a fullscreen sprite
Global SHWS_ImgSHW_Obj

;=====================================================
;--------------------------------------------------
;===>
;
;===>
;
Function SHW_Init.SHWS_Shadow(a_Camera)
	; ===>
	SHWS_Camera		 = a_Camera
	; ===>
	If(GraphicsDepth()<=16) Then 
		;SHWS_Tollerance = 8 
		SHWS_Tollerance% = (8 Shl 16) + (8 Shl 16) + 8
		
	Else 
		;SHWS_Tollerance = 4
		SHWS_Tollerance% = (4 Shl 16) + (4 Shl 16) + 4
		
	EndIf
	; ===>
	SHWS_ShadowCam = CreateCamera() 
	CameraClsColor SHWS_ShadowCam, 255, 255, 255
	CameraViewport SHWS_ShadowCam,0,0,SHWS_TexSize,SHWS_TexSize 	;the viewport the shadows will be rendered through.
	CameraProjMode SHWS_ShadowCam, 1;2														;Orthographic view, sorry, I've only figured out very simple directional shadows.
	HideEntity SHWS_ShadowCam
	; ===>
	SHWS_TexLight = CreateTexture(SHWS_TexSize,SHWS_TexSize,1);+16+32+256)
	TextureBlend SHWS_TexLight,2
	SHWS_TexCam = CreateTexture(SHWS_TexSize,SHWS_TexSize,1);+16+32+256)
	TextureBlend SHWS_TexCam,2
	; ===>
	SHWS_TexBase = CreateTexture(16,16,1+16+32+256)
	TextureBlend SHWS_TexBase,2
	SetBuffer TextureBuffer(SHWS_TexBase) ; 
	ClsColor 255, 255, 255
	Cls 
	ClsColor 0, 0, 0
	SetBuffer BackBuffer()
	; ===>
	Local Dof# = 1;.1
	SHWS_ImgSHW_Obj 	= CreateSprite(a_Camera)
	; ===>
	; Set up the sprites position, scale etc...
	PositionEntity  SHWS_ImgSHW_Obj, -0.0018, 0.0, 1.002
	;EntityBlend SHWS_ImgSHW_Obj,3
	;EntityFX SHWS_ImgSHW_Obj,32
	;EntityAlpha SHWS_ImgSHW_Obj,.1
	
	; ===>
	; Set full colour & additive blend
	EntityFX 		SHWS_ImgSHW_Obj, 1
	EntityBlend SHWS_ImgSHW_Obj, 2
	EntityAlpha SHWS_ImgSHW_Obj,0.5
	; ===>
	; Create blur texture
	EntityTexture SHWS_ImgSHW_Obj, SHWS_TexLight
	HideEntity SHWS_ImgSHW_Obj
	; ===>
	;Local l_Fact# = 200
	;BlurTextureInit(SHWS_TexSize, SHWS_TexSize/l_Fact, SHWS_TexSize/(l_Fact*.75));
	;BlurTextureInit(SHWS_TexSize, 3, 4);
	; ===>
End Function

;=====================================================
;--------------------------------------------------
;===>
;
;===>
;
Function SHW_AddObject.SHWS_Shadow( Obj, Caster=True, TexAlpha=0)
	; ===>
	Local l_CurModel.SHWS_Shadow = New SHWS_Shadow
	; ===>
	l_CurModel\ObjOrig	= Obj
	l_CurModel\Caster		= Caster
	; ===>
	If (l_CurModel\Caster)
		; ===>
		l_CurModel\ObjShw = CopyMesh(l_CurModel\ObjOrig)
		EntityColor_(	l_CurModel\ObjShw	, 255, 255, 255)
		EntityFX 			l_CurModel\ObjShw	, 2+1;+4+8
		EntityTexture l_CurModel\ObjShw, SHWS_TexBase, 0, 7
		; ===>
		HideEntity l_CurModel\ObjShw
		; ===>
	EndIf
	; ===>
	Return l_CurModel
	; ===>
End Function
; =====================================================
; ===>
;
; ===>
;
Function SHW_Update( s_light)
	; ===>
	If (Not SHWS_ENABLE) Then Return
	; ===>
	Local YSize# = (Float(SHWS_TexSize)*(Float(GraphicsHeight())/Float(GraphicsWidth())))
	Local YUp# = (Float(SHWS_TexSize)-YSize)*.5
	Local YDw# = SHWS_TexSize-YUp
	; ===>
	Local Id_SHWS_TexCam%		= TextureBuffer(SHWS_TexCam)
	Local Id_SHWS_TexLight%	= TextureBuffer(SHWS_TexLight)
	; ===>
	EntityParent SHWS_ImgSHW_Obj,SHWS_Camera
	HideEntity SHWS_ImgSHW_Obj
	AmbientLight	255,255,255
	;===>
	HideEntity SHWS_Camera																				; The in game camera must be hidden,
	ShowEntity SHWS_ShadowCam																			; and the shadow camera must become visable.
	;===>
	SHW_ObjSHW_Show()
	SHW_ObjOrig_Hide()
	;===>
	PositionEntity SHWS_ShadowCam, EntityX(s_light,True), EntityY(s_light,True), EntityZ(s_light,True)
	PointEntity SHWS_ShadowCam, CenterWord
	;===>
	Local l_CurModel.SHWS_Shadow
	Local ObjRecieved, ObjShw
	Local l_ColorZ#, l_TexU#, l_TexV#
	Local l_Vx#, l_Vy#, l_Vz# 
	;===>
	For l_CurModel = Each SHWS_Shadow
		;========================================================================
		;===>
		If (l_CurModel\Caster And SHWS_ENABLE) Then
			;========================================================================
			; ===>
			ObjRecieved = l_CurModel\ObjOrig
			ObjShw 			= l_CurModel\ObjShw;
			; ===>
			EntityTexture ObjShw, SHWS_TexBase, 0, 7
			EntityFX 			ObjShw, 2+1
			; ===>
			PositionEntity	ObjShw, EntityX     (ObjRecieved), EntityY     (ObjRecieved), EntityZ     (ObjRecieved)
			RotateEntity 		ObjShw, EntityPitch (ObjRecieved), EntityYaw   (ObjRecieved), EntityRoll  (ObjRecieved)
			ScaleEntity			ObjShw, EntityScaleX(ObjRecieved), EntityScaleY(ObjRecieved), EntityScaleZ(ObjRecieved)
			; ===>
			; =========================================================
			; Generate texture coordinate
			;	===>
			Local IdSurf%, IdVert%, CurSurf%
			For IdSurf=1 To CountSurfaces(ObjShw) 
				; =========================================================
				; ===>
				CurSurf=GetSurface(ObjShw,IdSurf) 
				For IdVert=0 To CountVertices(CurSurf)-1 
					; =========================================================
					; ===>
					l_Vx = VertexX(CurSurf,IdVert) : l_Vy = VertexY(CurSurf,IdVert) : l_Vz = VertexZ(CurSurf,IdVert)
					; ===>
					TFormPoint l_Vx, l_Vy, l_Vz, ObjRecieved, SHWS_ShadowCam
					l_ColorZ = TFormedZ()*SHWS_FactorZ
					VertexColor CurSurf,IdVert, l_ColorZ, l_ColorZ, l_ColorZ
					; ===>
					TFormPoint l_Vx, l_Vy, l_Vz, ObjRecieved, 0
					CameraProject SHWS_ShadowCam, TFormedX(), TFormedY(), TFormedZ()
					l_TexU# = ProjectedX()*SHWS_ConstSize;/Float(SHWS_TexSize)
					l_TexV# = ProjectedY()*SHWS_ConstSize;/Float(SHWS_TexSize)
					VertexTexCoords CurSurf,IdVert, l_TexU, l_TexV
					; ===>
					; =========================================================
				Next 
				; ===>
				; =========================================================
			Next 
			; ===>
			; =========================================================
		EndIf	
		;===>
		;=========================================================
	Next
	; =========================================================
	; ===>
	;	Costruito il finto zbuffer renderiziamo dal punto di vista della luce
	RenderWorld()
	
;	;=========================================================
;	HideEntity SHWS_ShadowCam	: ShowEntity SHWS_Camera
;	SHW_ObjSHW_Hide(): SHW_ObjOrig_Show()
;	Return
	
	; =========================================================
	; ===>
	; Mi copio l'immagine nell Muffer
	CopyRect 0,0, SHWS_TexSize, SHWS_TexSize, 0, 0, BackBuffer(), TextureBuffer(SHWS_TexLight)
	
	;SmoothTexture(SHWS_TexLight)
	
	;Return
	; ===>
	; ora rimetto la telecamera giusta
	PositionEntity SHWS_ShadowCam, EntityX(SHWS_Camera,True)    , EntityY(SHWS_Camera,True)  , EntityZ(SHWS_Camera,True)
	RotateEntity	 SHWS_ShadowCam, EntityPitch(SHWS_Camera,True), EntityYaw(SHWS_Camera,True), EntityRoll(SHWS_Camera,True)
	; =========================================================
	; ===>
	;	Mi metto dal punto di vista normale e vedo lo ZBuffer
	RenderWorld()
	; =========================================================
	; ===>
	; Mi copio l'immagine nell Buffer
	CopyRect 0,0, SHWS_TexSize, SHWS_TexSize, 0, 0, BackBuffer(), TextureBuffer(SHWS_TexCam)
	; =========================================================
	;===> 
	
	For l_CurModel = Each SHWS_Shadow
		;===>
		If (l_CurModel\Caster And SHWS_ENABLE) Then 
			EntityTexture l_CurModel\ObjShw, SHWS_TexLight, 0, 7
			EntityFX 			l_CurModel\ObjShw, 1
		EndIf
		;===>
	Next
	; =========================================================
	; ===>
	;	Costruito il finto zbuffer renderiziamo
	RenderWorld()
	; =========================================================
	; ===>
	; Mi copio l'immagine nell Muffer
	CopyRect 0,0, SHWS_TexSize, SHWS_TexSize, 0, 0, BackBuffer(), TextureBuffer(SHWS_TexLight)
	; =========================================================
	; ===>
	SHW_ShaderShadow(SHWS_TexLight,SHWS_TexCam)
	; ===>
	; =========================================================
	;===>
	ClsColor 0, 0, 0
	SetBuffer BackBuffer()
	;===>
	HideEntity SHWS_ShadowCam	 : SHW_ObjSHW_Hide()
	
	;SmoothTexture(SHWS_TexLight,6)
	;BlurTexture(SHWS_TexLight)
	;BlurTexturetest(SHWS_TexLight,3,4)
	
	ShowEntity SHWS_Camera : SHW_ObjOrig_Show()
	;===>
	AmbientLight	 AMBIENTLIGHTCOLOR_R, AMBIENTLIGHTCOLOR_G, AMBIENTLIGHTCOLOR_B
	;===>
	If (SHWS_ENABLE) Then SHWS_ENABLE_ONE = False Else SHWS_ENABLE_ONE = True
	;===>
	ShowEntity SHWS_ImgSHW_Obj
	;===>
End Function


;=====================================================
;--------------------------------------------------
;===>
;
;===>
;
Function SHW_ShaderShadow( a_ImageLight, a_ImageCamera )
	
	; =========================================================
	; ===>
	Local BuffImageLight = TextureBuffer(a_ImageLight)
	Local BuffImageCamera = TextureBuffer(a_ImageCamera)
	; ===>
	Local l_IdX%, l_IdY%,l_Pc% ,l_Pl%
	
	
	Local l_ColAmb%	= (AMBIENTLIGHTCOLOR_R Shl 16)+(AMBIENTLIGHTCOLOR_G Shl 8)+ AMBIENTLIGHTCOLOR_B
	
	Local l_ColAmb_r%	= AMBIENTLIGHTCOLOR_R
	Local l_ColAmb_g%	= AMBIENTLIGHTCOLOR_G
	Local l_ColAmb_b%	= AMBIENTLIGHTCOLOR_B
	
	l_ColAmb_r%	= ( l_ColAmb Shr 16)	And $ff
	l_ColAmb_g%	= ( l_ColAmb Shr 8 )	And $ff
	l_ColAmb_b%	= ( l_ColAmb )				And $ff
	
	Local l_ColNorm%= (255 Shl 24)+(255 Shl 16)+(255 Shl 8)+ 255	
	; ===>
	Local YSize#	= (Float(SHWS_TexSize)*(Float(GraphicsHeight())/Float(GraphicsWidth())))
	Local YUp#		= (Float(SHWS_TexSize)-YSize)*.5
	Local YDw#		= SHWS_TexSize-YUp-1
	Local XDw#		= SHWS_TexSize-1
	
	; ===>
	LockBuffer BuffImageCamera
	LockBuffer BuffImageLight
	SetBuffer BuffImageLight
	; ===>
	For l_IdY = YUp To YDw
		For l_IdX = 0 To XDw
			; ===>
			l_Pc% = ReadPixelFast( l_IdX, l_IdY, BuffImageCamera)
			l_Pl% = ReadPixelFast( l_IdX, l_IdY ) + SHWS_Tollerance
			;===>
			If (l_Pl<l_Pc) Then 
				WritePixelFast l_IdX, l_IdY, l_ColAmb
			Else
				WritePixelFast l_IdX, l_IdY, l_ColNorm
			EndIf
			; ===>
		Next
	Next
	; ===>
	UnlockBuffer BuffImageCamera
	UnlockBuffer BuffImageLight
	SetBuffer BackBuffer()
	; ===>
End Function


;=====================================================
;--------------------------------------------------
;===>
;
;===>
;
Function SmoothTexture_Fast( a_Image, a_Smooth=2)
	; ===>
	Local l_x0%, l_y0%, l_Idy0%
	Local l_x1%, l_y1%, l_Idy1%
	Local l_x1_a%, l_x1_b%
	Local l_Color#, l_NColor#
	Local gfxbuffer=TextureBuffer(a_Image)
	; ===>
	LockBuffer gfxbuffer
	SetBuffer gfxbuffer
	For l_y0=0 To SHWS_TexSize-1
		l_Idy0 = l_y0*SHWS_TexSize
		For l_x0=0 To SHWS_TexSize-1
			; ===>
			SHWS_Pix(l_Idy0+l_x0) = ReadPixelFast( l_x0,l_y0,gfxbuffer) And $FF
			; ===>
		Next
	Next
	; ===>
	
	For l_y0=0 To SHWS_TexSize-1
		l_Idy0 = l_y0*SHWS_TexSize
		For l_x0=0 To SHWS_TexSize-1
			; ===>
			l_Color = 0;
			l_NColor = 0;
			; ===>
			For l_y1=l_y0-a_Smooth To l_y0+a_Smooth
				If (l_y1>=0 And l_y1<=SHWS_TexSize) Then 
					l_Idy1 = l_y1*SHWS_TexSize
					; ===>
					l_x1_a = l_x0-a_Smooth	: If (l_x1<0)						Then l_x1_a = 0
					l_x1_b = l_x0+a_Smooth	: If (l_x1>SHWS_TexSize)Then l_x1_a = SHWS_TexSize-1
					; ===>
					For l_x1=l_x1_a To l_x1_b
						; ===>
						l_NColor = l_NColor+1
						l_Color = l_Color+SHWS_Pix(l_Idy1+l_x1)
						; ===>
					Next
				EndIf
			Next
			; ===>
			l_Color = l_Color / l_NColor
			WritePixelFast l_x0,l_y0,((l_Color Shl 16)+(l_Color Shl 8)+l_Color)
			; ===>
		Next
	Next
	; ===>
	UnlockBuffer gfxbuffer
	SetBuffer BackBuffer()
	; ===>
End Function

;=====================================================
;--------------------------------------------------
;===>
;
;===>
;
Function SmoothTexture( a_Image, a_Smooth=2)
	; ===>
	Local l_x0%, l_y0%, l_Idy0%
	Local l_x1%, l_y1%, l_Idy1%
	Local l_Color#, l_NColor#
	Local gfxbuffer=TextureBuffer(a_Image)
	; ===>
	LockBuffer gfxbuffer
	SetBuffer gfxbuffer
	For l_y0=0 To SHWS_TexSize-1
		l_Idy0 = l_y0*SHWS_TexSize
		For l_x0=0 To SHWS_TexSize-1
			; ===>
			SHWS_Pix(l_Idy0+l_x0) = ReadPixelFast( l_x0,l_y0,gfxbuffer) And $FF
			; ===>
		Next
	Next
	; ===>
	
	For l_y0=0 To SHWS_TexSize-1
		l_Idy0 = l_y0*SHWS_TexSize
		For l_x0=0 To SHWS_TexSize-1
			; ===>
			l_Color = 0;
			l_NColor = 0;
			; ===>
			For l_y1=l_y0-a_Smooth To l_y0+a_Smooth
				If (l_y1>=0 And l_y1<=SHWS_TexSize) Then 
					l_Idy1 = l_y1*SHWS_TexSize
					; ===>
					For l_x1=l_x0-a_Smooth To l_x0+a_Smooth
						; ===>
						If (l_x1>=0 And l_x1<=SHWS_TexSize) Then 
							l_NColor = l_NColor+1
							l_Color = l_Color+SHWS_Pix(l_Idy1+l_x1)
						EndIf
						; ===>
					Next
				EndIf
			Next
			; ===>
			l_Color = l_Color / l_NColor
			WritePixelFast l_x0,l_y0,((l_Color Shl 16)+(l_Color Shl 8)+l_Color)
			; ===>
		Next
	Next
	; ===>
	UnlockBuffer gfxbuffer
	SetBuffer BackBuffer()
	; ===>
End Function



;=====================================================
;--------------------------------------------------
;===>
;
;===>
;
Function SHW_ShadowEnable()
	;===>
	SHWS_ENABLE = True
	;===>
End Function
;=====================================================
;--------------------------------------------------
;===>
;
;===>
;
Function SHW_ShadowDisable()
	;===>
	SHWS_ENABLE = False
	;===>
End Function

;=====================================================
;--------------------------------------------------
;===>
;
;===>
;
Function SHW_ObjSHW_Hide()
	;===>
	Local l_CurModel.SHWS_Shadow
	;===>
	For l_CurModel = Each SHWS_Shadow 
		If (l_CurModel\Caster) Then HideEntity l_CurModel\ObjShw 
	Next
	;===>
End Function
;=====================================================
;--------------------------------------------------
;===>
;
;===>
;
Function SHW_ObjSHW_Show()
	;===>
	Local l_CurModel.SHWS_Shadow
	;===>
	For l_CurModel = Each SHWS_Shadow 
		If (l_CurModel\Caster) Then ShowEntity l_CurModel\ObjShw 
	Next
	;===>
End Function
;=====================================================
;--------------------------------------------------
;===>
;
;===>
;
Function SHW_ObjOrig_Hide()
	;===>
	Local l_CurModel.SHWS_Shadow
	;===>
	For l_CurModel = Each SHWS_Shadow 
		HideEntity l_CurModel\ObjOrig 
	Next
	;===>
End Function
;=====================================================
;--------------------------------------------------
;===>
;
;===>
;
Function SHW_ObjOrig_Show()
	;===>
	Local l_CurModel.SHWS_Shadow
	;===>
	For l_CurModel = Each SHWS_Shadow 
		ShowEntity l_CurModel\ObjOrig 
	Next
	;===>
End Function

;=====================================================
;--------------------------------------------------
;===>
;
;===>
;
Function SHW_SmootShadowTexture()
	;===>
	If (SHWS_ENABLE=False) Then Return
	
;	;===>
;	Local l_SHW_Recived.SHWS_Shadow
;	;===>
;	For l_SHW_Recived = Each SHWS_Shadow
;		; ===>
;		If (l_SHW_Recived\Caster) Then BlurTexture( l_SHW_Recived\Tex)
;		; ===>
;	Next
;	;===>
End Function
[/codebox]
