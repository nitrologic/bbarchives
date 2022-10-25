; ID: 814
; Author: Jeremy Alessi
; Date: 2003-10-23 17:02:32
; Title: Full Screen Motion Blur
; Description: Blurs the whole screen.

;====== Full Screen Motion Blur =================================================================
;====== PROGRAMMED BY JEREMY ALESSI =============================================================
;====== Requires Rob's Sprite Functions =========================================================

;====== SPRITE FUNCTIONS IF YOU NEED THEM =======================================================
;====== IF NOT JUST COMMENT THEM OUT ============================================================
;fov is the same as your CameraZoom.
Function Sprite2D(sprite,x#,y#,fov#)
	PositionEntity sprite,2*(x#-320),-2*(y#-240),fov#*640
End Function

;scale sprite in screen pixels relative to a 640x480 res when used with Sprite2D

Function ScaleSprite2(sprite,x,y)
	ScaleEntity sprite,x,y,1
End Function
;please pass camera to this function Or 0 for a billboard Type with mesh.

Function CreateSprite2(parent)
	If parent<>0
		m=CreateMesh(parent)
	Else
		m=CreateMesh()
	EndIf
	s=CreateSurface(m)
	AddVertex s,-1,+1,-1,0,0:AddVertex s,+1,+1,-1,1,0
	AddVertex s,+1,-1,-1,1,1:AddVertex s,-1,-1,-1,0,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	ScaleEntity m,100,100,1
	Return m
End Function
;================================================================================================

;====== GLOBAL VARIABLES ========================================================================
Global blur, blurHud
;================================================================================================

;====== CALL THIS TO SET UP BLUR CAMERA, CHILDREN, AND TEXTURES =================================
;====== CALL IT AFTER SETTING UP THE MAIN CAMERA AND THEN PASS THE CAMERA =======================
;====== BLUR_SEVERITY BECOMES WORSE WITH HIGHER NUMERICAL ALPHA LEVELS ==========================
;====== SET THE SEVERITY AND THE FALLOFF VALUES SO THAT ALL 3 BLUR HUDS TOGETHER ARE STILL ======
;====== TRANSPARENT =============================================================================
Function SetupBlurCamera(blurCamera, blurSev# = 0.5)
	
	blur = CreateTexture( Float(GraphicsWidth()), Float(GraphicsWidth()), 256)
	
	widthRatio# = TextureWidth(blur) / Float(GraphicsWidth())
	heightRatio# = TextureHeight(blur) / Float(GraphicsHeight())
	
	ScaleTexture(blur,widthRatio#,heightRatio#)
	blurHud=CreateSprite2(blurCamera)
	NameEntity(blurHud,"blurHud")
	EntityOrder(blurHud,-1000)
	EntityFX(blurHud,1 + 8 + 16)
	ScaleSprite2(blurHud,640,480)
	Sprite2D(blurHud,320,240,1)
	EntityAlpha(blurHud,blurSev#)
	EntityTexture(blurHud, blur)
	
End Function
;================================================================================================

;====== BLUR FUNCTION, CALL BEFORE RENDERWORLD ==================================================
;====== PASS THE CAMERA TO IT, HOW MUCH DURATION BETWEEN LATENT IMAGES OR JUST HOW MUCH BLUR ====
;====== AND WHETHER YOU WANT TO COPY FROM THE FRONT OR BACK BUFFER 1=FRONT 2=BACK ===============
;====== BE SURE TO CALL ShowBlur() BEFORE USING THIS OR THE BLUR WILL NOT APPEAR ================
;====== YOU CAN ALSO USE HideBlur() SO THE EFFECT CAN BE CONTROLLED FOR A CERTAIN MODE ==========
Function BlurScreen(blurCamera,blurSev# = 0.5,whichBuffer = 1)
	EntityAlpha(blurHud, blurSev#)
	Select whichBuffer
		Case 1
			CopyRect( 0, 0, GraphicsWidth(), GraphicsHeight(), 0, 0, FrontBuffer(), TextureBuffer(blur) )
		
		Case 2
			CopyRect( 0 , 0, GraphicsWidth(), GraphicsHeight(), 0, 0, BackBuffer(), TextureBuffer(blur) )
					
	End Select

End Function
;================================================================================================

;====== HIDE THE BLUR EFFECTS ===================================================================
Function HideBlur()
	HideEntity(blurHud)
End Function
;================================================================================================

;====== SHOW THE BLUR EFFECTS ===================================================================
Function ShowBlur()	
	ShowEntity(blurHud)
End Function
;================================================================================================
