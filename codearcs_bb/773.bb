; ID: 773
; Author: skidracer
; Date: 2003-08-20 05:35:53
; Title: Pixies
; Description: sprites with sharp pixel perfect features

; pixies.bb
; by skidracer

; pixies are pixel perfect sprite overlays

; LoadPixie(camera,imagefile$)

; returns a sprite parented to a camera 
; with following features 
;  1:1 pixel-texel scale for zero filtered sharp overlays
;  position pixies in screen coordinates

; 20.8.2003 untested with odd sized sprites
; 21.8.2003 modified to handle odd textures

displaywidth=1024
displayheight=768

Graphics3D displaywidth,displayheight  

cam=CreateCamera() 
CameraRange cam,.1,1000 

pixie=LoadPixie(cam,"simon.bmp")

While Not KeyHit(1) 
	PositionEntity pixie,MouseX(),MouseY(),0
	RenderWorld 
	UpdateWorld 
	TurnEntity cam,1,2,0	;test texel drift
	Flip 
	Wend 
End 

Function LoadPixie(camera,file$)
; load squared texture
	texture=LoadTexture(file)
	width=TextureWidth(texture)
	height=TextureHeight(texture)
	image=LoadImage(file)
	iwidth=ImageWidth(image)
	iheight=ImageHeight(image)
	If iwidth<>width Or iheight<>height
		buffer=TextureBuffer(texture)
		ibuffer=ImageBuffer(image)
		For y=0 To height-1
			For x=0 To width-1
				WritePixel x,y,ReadPixel(x,y,ibuffer),buffer
			Next
		Next
		ScaleTexture texture,Float(width)/iwidth,Float(height)/iheight ; will blitzmax need float()?
		width=iwidth
		height=iheight
	EndIf
	FreeImage image
; change these for viewports
	viewwidth=GraphicsWidth()
	viewheight=GraphicsHeight()
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
		aspect#=Float(viewheight)/viewwidth
		PositionEntity magic,-1,aspect,1 
		scale#=2.0/viewwidth 
		ScaleEntity magic,scale,-scale,-scale 
		magic=CreatePivot(magic)
		PositionEntity magic,-.5,-.5,0
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
