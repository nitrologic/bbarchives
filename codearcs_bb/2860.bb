; ID: 2860
; Author: Moore
; Date: 2011-06-17 01:26:01
; Title: Pixies 2
; Description: Pixel perfect sprites with masking

; Pixies 2 Created by NRMStudios
; NRMStudios@gmail.com

; Based on "pixies.bb"
; by skidracer

; Improvements
; Animation Support
; Masking Support

; Graphics3D 800,600 ; for demo

Global pixies_mask_texture = CreateTexture(1024,1024,4)
Global pixies_mask_size = 1024
mask_texture(pixies_mask_texture)

;DEMO =========================================================================================================

;	camera = CreateCamera()
;	CameraClsColor camera, 0,100,0 ; makes background green instead of black
;	
;	
;	; Create sprite
;	sprite = CreatePixie(camera, 128, 128, 5, 2)
;	brush = GetEntityBrush(sprite)
;	t = GetBrushTexture(brush) ; get the texture
;	image = CreateImage(128,128,2)
;	
;	; add a red circle to the first frame
;	SetBuffer ImageBuffer(image, 0)
;	;SetBuffer TextureBuffer(t, 0)
;	Color 255,0,0
;	Oval 10, 10, 100, 100, True
;	Color 1,0,0
;	Oval 10, 10, 100, 100, False
;	Color 0,255,0
;	Text 50,50, "HELLO!", True, True
;	; add a blue circle to the second frame
;	SetBuffer ImageBuffer(image, 1)
;	;SetBuffer TextureBuffer(t, 1)
;	Color 0,0,255
;	Oval 10, 10, 100, 100, True
;	Color 1,0,0
;	Oval 10, 10, 100, 100, False
;	Color 255,255,255
;	Text 50,50, "HELLO!", True, True
;	
;	CopyRect 0,0,128,128,0,0,ImageBuffer(image,0),TextureBuffer(t,0) 
;	CopyRect 0,0,128,128,0,0,ImageBuffer(image,1),TextureBuffer(t,1)
;	mask_texture(t,128,128,2) 
;	FreeImage image
;	
;	; position entity on the screen
;	PositionEntity sprite, 400,200, 0
;
;	; load an animated sprite, image is 100x50 of two triangles each being 50x50 with a black masking
;	sprite2 = LoadAnimPixie(camera,"triangle.png", 50, 50, 5)
;	PositionEntity sprite2, 50, 50, 0
;	
;	sprite3 = LoadPixie(camera,"triangle.png", 5)
;	PositionEntity sprite3, 50, 150, 0
;	
;	
;	time = MilliSecs()
;	While Not KeyHit(1)
;		; Timer to animate the image
;		If MilliSecs() - time > 1000 Then
;			time = MilliSecs()
;			If frame = 1 Then frame = 0 Else frame = 1 ; advance the frame
;			EntityTexture sprite, t, frame ; update the entity texture
;		EndIf
;		
;		SetBuffer BackBuffer()
;		RenderWorld()
;		Flip
;	Wend
;	
;	End
;	
; End of demo =================================================================================================================================

Function mask_texture(t, frames = 1, mask_color = 0); ...................................................................... mask_texture
; Drops the alpha of all pixels that match the masking color to 0
	w = TextureWidth(t)
	h = TextureHeight(t)
	For fi = 0 To frames - 1
		buffer = TextureBuffer(t, fi)
		LockBuffer buffer
		For iw = 0 To w - 1
			For ih = 0 To h - 1
				c = ReadPixelFast(iw, ih, buffer) And $FFFFFF 		; Read pixel from texture
				If c = mask_color 									; If Color = mask_color
					WritePixelFast iw, ih, 0, buffer 				; Write transparent black pixel
				Else 												; If color != mask_color
					WritePixelFast iw, ih, c Or $FF000000, buffer 	; Write color and 100% alpha to texture
				End If
			Next
		Next
		UnlockBuffer buffer
	Next 
End Function

Function SaveAnimImage(file$, img, frames = 1, states = 1) ; ..................................................................... SaveAnimImage
; This function is NOT needed for pixies to work.
	w = ImageWidth(img)
	h = ImageHeight(img)
	image = CreateImage(w*frames,h*states)
	For ii = 0 To states - 1
		For i = 0 To frames - 1
			CopyRect 0, 0, w, h, i * w, ii * h, ImageBuffer(img,i), ImageBuffer(image)
		Next
	Next
	chk = SaveImage(image, file)
	FreeImage image
	Return chk
End Function

Function flush_texture(t, frames = 1)
; Fast way to set the alpha of all pixels in a texture to 0 using copyrect

	w = TextureWidth(t)
	h = TextureHeight(t)
	size = w
	If h > size Then size = h
	
	; adjust size of blank mask as needed
	If pixies_mask_size < size Then
		While pixies_mask_size < size
			pixies_mask_size = pixies_mask_size * 2
		Wend
		FreeTexture pixies_mask_texture
		pixies_mask_texture = CreateTexture(pixies_mask_size, pixies_mask_size, 5)
		mask_texture(pixies_mask_texture, pixies_mask_size, pixies_mask_size)
	EndIf
	
	For i = 0 To frames - 1
		CopyRect 0,0,w,h,0,0,TextureBuffer(pixies_mask_texture), TextureBuffer(t,i)
	Next
End Function

Function CreatePixie(camera, w#, h#, flags=1, frames=1, viewwidth# = 0, viewheight# = 0) ; CreatePixie
	; load squared texture
	
	texture = CreateTexture(w,h,flags,frames)
	width = TextureWidth(texture)
	height = TextureHeight(texture)
	
	If w<>width Or h<>height
		For i = 0 To frames - 1
			If (flags And (1 Shl 2)) <> 0 Then ; if image is masked
				flush_texture(texture, frames) ; flush the alpha value from the texture 
			Else ; image is not masked
				SetBuffer TextureBuffer(texture, i) ; provide a plain black background with full alpha
				Color 255,255,255
				Rect 0,0,iwidth,iheight,True
			EndIf
		Next
	
		ScaleTexture texture,Float(width)/w,Float(height)/h ; will blitzmax need float()?
		width=w
		height=h
	ElseIf (flags And (1 Shl 2)) <> 0 Then
		flush_texture(texture, frames)
	EndIf
	
	; change these for viewports
	If viewwidth = 0 Then viewwidth = GraphicsWidth()
	If viewheight = 0 Then viewheight = GraphicsHeight()
	
	; find existing pixiespace parented to camera
	magic=0
	n=CountChildren(camera)
	For i=1 To n
		If EntityName(GetChild(camera,i))="pixiespace" 
			magic=GetChild(GetChild(camera,i),1)
		EndIf
	Next
	If magic=0
		magic=CreatePivot(camera) 
		NameEntity(magic,"pixiespace")
		aspect# = (viewheight/viewwidth) 			;aspect# = viewheight/viewwidth
		PositionEntity magic,-1,aspect,1			;PositionEntity magic,-1,aspect,1
		scale#=(2.0/viewwidth) 						;scale# = 2.0/viewwidth
		ScaleEntity magic,scale,-scale,-scale 
		magic=CreatePivot(magic)
		PositionEntity magic,-.5,-.5,0 				;PositionEntity magic,-.5,-.5,0
	EndIf
	
	; create sprite from texture as child of magic overlay	
	sprite=CreateSprite()
	EntityParent sprite,magic		;cludge for blitz bug in createsprite(parent)
	brush=CreateBrush()
	BrushFX brush,1
	BrushTexture brush,texture
	PaintEntity sprite,brush
	FreeBrush brush
	SpriteViewMode sprite,2 
	scale#=1.0/viewwidth 
	ScaleSprite sprite,width*scale,height*scale
	Return sprite
End Function

Function LoadAnimPixie(camera,file$, w#, h#, flgs = 1, count = 0, frst=0, viewwidth# = 0, viewheight# = 0)
; load squared texture
	image=LoadImage(file)
	iwidth#=ImageWidth(image)
	iheight#=ImageHeight(image)
	If count = 0 Then 
		count = (iwidth/w)*(iheight/h)
	EndIf
	FreeImage image
	image = LoadAnimImage(file, w, h, frst, count)
	iwidth = ImageWidth(image)
	iheight = ImageHeight(image)
	texture=LoadAnimTexture(file, flgs, w, h, frst, count)
	width#=TextureWidth(texture)
	height#=TextureHeight(texture)
	
	For i = 0 To count - 1
		If iwidth<>width Or iheight<>height
			CopyRect 0,0,iwidth,iheight,0,0,ImageBuffer(image,i), TextureBuffer(texture,i) 
			
			ScaleTexture texture, width / (w), height / (h)
			width=iwidth
			height=iheight
		EndIf
	Next

	If (flgs And (1 Shl 2)) <> 0 Then mask_texture(texture, count)
	
	FreeImage image

	; change these for viewports
	If viewwidth = 0 Then viewwidth = GraphicsWidth()
	If viewheight = 0 Then viewheight = GraphicsHeight()
	
; find existing pixiespace parented to camera
	magic=0
	n=CountChildren(camera)
	For i=1 To n
		If EntityName(GetChild(camera,i))="pixiespace" 
			magic=GetChild(GetChild(camera,i),1)
		EndIf
	Next
	If magic=0
		magic=CreatePivot(camera) 
		NameEntity(magic,"pixiespace")
		aspect# = (viewheight/viewwidth) 			;aspect# = viewheight/viewwidth
		PositionEntity magic,-1,aspect,1			;PositionEntity magic,-1,aspect,1
		scale#=(2.0/viewwidth) 						;scale# = 2.0/viewwidth
		ScaleEntity magic,scale,-scale,-scale 
		magic=CreatePivot(magic)
		PositionEntity magic,-.5,-.5,0 				;PositionEntity magic,-.5,-.5,0
	EndIf
	
; create sprite from texture as child of magic overlay	
	sprite=CreateSprite()
	EntityParent sprite,magic		;cludge for blitz bug in createsprite(parent)
	brush=CreateBrush()
	BrushFX brush,1
	BrushTexture brush,texture
	PaintEntity sprite,brush
	FreeBrush brush
	SpriteViewMode sprite,2 
	scale#=1.0/viewwidth 
	ScaleSprite sprite,width*scale,height*scale
	Return sprite
End Function

Function LoadPixie(camera,file$, flags)
; load squared texture
	texture=LoadTexture(file, flags)
	width#=TextureWidth(texture)
	height#=TextureHeight(texture)
	image=LoadImage(file)
	iwidth#=ImageWidth(image)
	iheight#=ImageHeight(image)

	If iwidth<>width Or iheight<>height
		SetBuffer TextureBuffer(texture)
		
		CopyRect 0,0,iwidth,iheight,0,0,ImageBuffer(image), TextureBuffer(texture)
	
		ScaleTexture texture,Float(width)/iwidth,Float(height)/iheight ; will blitzmax need float()?
		width=iwidth
		height=iheight
	EndIf

	FreeImage image
	
	If (flags And (1 Shl 2)) <> 0 Then mask_texture(Texture, width, height)
	
; change these for viewports
	viewwidth#=GraphicsWidth()
	viewheight#=GraphicsHeight()
	
; find existing pixiespace parented to camera
	magic=0
	n=CountChildren(camera)
	For i=1 To n
		If EntityName(GetChild(camera,i))="pixiespace" 
			magic=GetChild(GetChild(camera,i),1)
		EndIf
	Next
	If magic=0
		magic=CreatePivot(camera) 
		NameEntity(magic,"pixiespace")
		aspect# = (viewheight/viewwidth) 			;aspect# = viewheight/viewwidth
		PositionEntity magic,-1,aspect,1			;PositionEntity magic,-1,aspect,1
		scale#=(2.0/viewwidth) 						;scale# = 2.0/viewwidth
		ScaleEntity magic,scale,-scale,-scale 
		magic=CreatePivot(magic)
		PositionEntity magic,-.5,-.5,0 				;PositionEntity magic,-.5,-.5,0
	EndIf
	
; create sprite from texture as child of magic overlay	
	sprite=CreateSprite()
	EntityParent sprite,magic		;cludge for blitz bug in createsprite(parent)
	brush=CreateBrush()
	BrushFX brush,1
	BrushTexture brush,texture
	PaintEntity sprite,brush
	FreeBrush brush
	SpriteViewMode sprite,2 
	scale#=1.0/viewwidth 
	ScaleSprite sprite,width*scale,height*scale
	Return sprite
End Function
