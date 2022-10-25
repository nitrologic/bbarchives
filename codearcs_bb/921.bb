; ID: 921
; Author: Afrohorse
; Date: 2004-02-06 07:26:54
; Title: DreamFilter
; Description: A Dreamy fullscreen filter effect

;===============================================================================
; Dream Filter - Ian Lindsey (Afrohorse)
; ------------
; 03 Feb 2004
;
; The scene is rendered to a smaller sized viewport and copied into a texture.
; The texture is placed onto a sprite that overlays the entire screen, using 
; an additive blend - this sprite also is scaled slightly larger than the screen.
; (The filtering will blur the scene - like depth of field)
;
; The sprite is then fed back into the next frame (motion blur), so that the 
; additive blend burns the brighter parts of the scene into the texture. The 
; slight scale up of the sprite will add a bloom effect around these bright parts
; (High dynamic range).
;
; Example Usage:
; --------------
;
; <Program Start>
; Create the camera (use Global g_Camera)
; NBDreamFilter_Create()
;
; <Main Loop>
; Program Update code..
; NBDreamFilter_Update()
;
; RenderWorld
; Flip
; <End Of Main Loop>
; 
; <Program End>
; NBDreamFilter_Destroy()
; 
; =====================================================================
; NOTE: The scenes camera should be placed in a global variable called
;       g_Camera
; =====================================================================
;
; 
;===============================================================================

; Includes /////////////////////////////////////////////////////////////////////

; Constants ////////////////////////////////////////////////////////////////////

; Tweakable Values 
Const k_HDRTextureSize 	= 256		; Blur texture size, must be power of 2
Const k_HDRMotionZoom#	= 1.022		; The amount the blur sprite scales
Const k_HDRBlurFeedBack = 220		; 0 - 255 The amount of previous frame to feedback
Const k_HDRBlurFilter	= 100		; 0 - 255 The amount of the filter to overlay (additive) to the scene

Const k_HDRZSpritePos#	= 1.1
Const k_HDRSpriteOffs#	= 0.001

; Globals //////////////////////////////////////////////////////////////////////
Global g_BlurTex
Global g_BlurSprite

; Functions ////////////////////////////////////////////////////////////////////

;///////////////////////////////////////////////////////////////////////////////
; Creates the Dream filter
;///////////////////////////////////////////////////////////////////////////////
Function NBDreamFilter_Create()
	
	; Create a fullscreen sprite
	spr 		= CreateMesh(g_Camera)
	sf 		= CreateSurface(spr)
	
	; Make a quad
	AddVertex   sf, -1, 1, 0, 0, 0
	AddVertex   sf,  1, 1, 0, 1, 0
	AddVertex   sf, -1,-1, 0, 0, 1
	AddVertex   sf,  1,-1, 0, 1, 1
		
	AddTriangle sf, 0, 1, 2
	AddTriangle sf, 3, 2, 1
	
	; Set up the sprites position, scale etc...
	zpos#		= k_HDRZSpritePos#
	range#		= k_HDRMotionZoom*zpos#
	PositionEntity  spr,  -k_HDRSpriteOffs#, k_HDRSpriteOffs#, zpos#
	ScaleEntity   	spr,  range#, range#, 1.0
	EntityOrder	spr,  -10000
	
	; Set full colour & additive blend
	EntityFX 		spr, 1
	EntityBlend     	spr, 3
	g_BlurSprite  = 	spr
	
	; Create blur texture
	g_BlurTex = CreateTexture(k_HDRTextureSize, k_HDRTextureSize)
	EntityTexture spr, g_BlurTex
	
End Function

;///////////////////////////////////////////////////////////////////////////////
; Destroys the Dream filter
;///////////////////////////////////////////////////////////////////////////////
Function NBDreamFilter_Destroy()

	FreeTexture g_BlurTex
	FreeEntity  g_BlurSprite
 	
End Function


;///////////////////////////////////////////////////////////////////////////////
; Updates the Dream filter
;///////////////////////////////////////////////////////////////////////////////
Function NBDreamFilter_Update()

	; Set the viewport to the same size as the blur texture 
	CameraViewport g_Camera, 0, 0, k_HDRTextureSize, k_HDRTextureSize
	
	; Set the blur feedback value (Additive)
	col = k_HDRBlurFeedBack
	EntityColor g_BlurSprite, col,col,col

	; Render the scene (including the blur sprite and copy it into the texture)
	RenderWorld 
	CopyRect  0,0,k_HDRTextureSize,k_HDRTextureSize, 0, 0, BackBuffer(), TextureBuffer(g_BlurTex)
	
	; Reset the viewport to fullscreen
	CameraViewport g_Camera,0,0,GraphicsWidth(),GraphicsHeight()
	
	; Set the blur filter value (Additive)
	col = k_HDRBlurFilter
	EntityColor g_BlurSprite, col,col,col
	
End Function
