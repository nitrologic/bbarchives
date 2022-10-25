; ID: 150
; Author: John Pickford
; Date: 2001-11-29 04:51:00
; Title: B2D--&gt;B3D
; Description: Convert 2D code to 3D accellerated 2D

;*******************************************************************
;
; 2Din3D	 By J.Pickford - Freeware!
;..........................................
;
; A set of 2D sprite functions implemented in B3D
;
; Functions are intended to be VERY close to B2D commands to make 
; conversion of B2D games easy
;
;
;
; This code is a work-in-progress. I hope to emulate more of the B2D
; functionality in future versions.
;
; Future Enhancements: Sprite Scaling, Rotation, Variable Alpha
;
;********************************************************************

Const maxframes=1000		;max number of animation frames allowed in a single texture

Type texturebank

	Field t_texture
	Field t_brush
	Field t_sx#[maxframes]
	Field t_sy#[maxframes]
	Field t_width#
    Field t_height#
    Field t_maxframe
	Field t_totalwidth#
	Field t_totalheight#

End Type

Const width#=640			;Screensize
Const height#=480


	Graphics3D width,height
	SetBuffer BackBuffer()
	
Global camera=CreateCamera()
Global pivot=CreateCamera()
Global displaylist=CreateMesh(camera)
Global lasttexture.texturebank



Global alien.texturebank=loadanimimageb3d ("alien.bmp",64,64)  ;Just an example



	clock#=0
	
	While Not MouseHit(2)
	
	
	
	ClearDisplayList() ;This resets the display list - ESSENTIAL!
	

	;"Draw" some stuff-------------------------------Actual drawing takes place at Renderworld()
	
		For f#=1 To 360 Step 5
			
			drawimageb3d (alien,width/2+width/2*Sin(clock/4)*Cos(clock+f),height/2+Cos(clock/5)*height/2*Sin(clock+f),(f/5) Mod 64)
	
		Next
	
	;should be at opposite corners of the screen 
		
		drawimageb3d (alien,0,0,(clock/5)Mod 64)
		drawimageb3d (alien,width-64,height-64,(clock/7)Mod 64)
	
	;-------------------------------------------------
		
		RenderWorld() ;Actual drawing takes place
		Color 255,255,255
		Text 100,10,"Click Right Mouse Button to Quit"
		Flip
	
		clock=clock+1
	
	Wend


	End

;--------------------------------------
; Call this at the start of each frame


Function ClearDisplayList()

	FreeEntity displaylist
	displaylist=CreateMesh(camera)
	PositionEntity displaylist,-width/2,-height/2,width/2
	lasttexture=Null
	
End Function

;------------------------------------------------------------------------
; LoadAnimImageB3D
;...................
;
;  Intended to be compatible with B2D command "LoadAnimImage"
;
;  Known problems: If B3D resizes texture because it's too big then
;                  the UV's will be wrong.  Looking for a neat fix...
;
;				   The transparent colour must be 0,0,0	
;-------------------------------------------------------------------------


Function LoadAnimImageB3D.texturebank (file$,swidth#,sheight#)

	this.texturebank=New texturebank
	this\t_texture=LoadTexture (file$,1+4)		;color + masked
	this\t_brush=CreateBrush()
	BrushTexture this\t_brush,this\t_texture
    BrushFX this\t_brush,1						;Full bright - no lighting needed

	this\t_totalwidth=TextureWidth (this\t_texture)
	this\t_totalheight=TextureHeight (this\t_texture)
	this\t_width=swidth
	this\t_height=sheight
	
	frame=0
	sy#=0
	
;Create the UV coords for each animation frame (just for speed later on)
	
	While sy<this\t_totalheight
	
	 	sx#=0
	 
		While sx<this\t_totalwidth
	
			this\t_sx[frame]=sx/this\t_totalwidth
			this\t_sy[frame]=sy/this\t_totalheight
			frame=frame+1
			sx=sx+swidth
		
		Wend
		
		sy=sy+sheight
	
	Wend
	
	
	this\t_maxframe=frame-1

	Return this

End Function


;--------------------------------------------------------------------------------
; DrawImageB3D (Simple sprite with no fancy functions)
;......................................................
;
;
; Intended to be compatible with the B2D equivelant (DrawImage)
; The only enhancement being x,y are floats allowing sub-pixel positioning
;
;--------------------------------------------------------------------------------

Function DrawImageB3D (texture.texturebank,x#,y#,frame)

	y=height-y	;in B3D y is up - in B2D y is down
		
;use existing surface except when switching source textures
	
	If texture=lasttexture Then surface=FindSurface (displaylist,texture\t_brush)
	If Not surface Then surface=CreateSurface (displaylist,texture\t_brush)
	lasttexture=texture
	
	w#=texture\t_width-1			;size of image (pixels)
	h#=texture\t_height-1
	tw#=w/texture\t_totalwidth		;size of image (texture coords)
	th#=h/texture\t_totalheight
	sx#=texture\t_sx[frame]			;position of image within texture (texture coords)
	sy#=texture\t_sy[frame]

	;								four vertices represent four corners of sprite

	v1=AddVertex (surface,x,y,0,sx,sy)
	v2=AddVertex (surface,x+w,y,0,sx+tw,sy)
	v3=AddVertex (surface,x,y-h,0,sx,sy+th)
	v4=AddVertex (surface,x+w,y-h,0,sx+tw,sy+th)
		 
	AddTriangle (surface,v1,v2,v3)	;two triangle make rectangular up image
	AddTriangle (surface,v2,v4,v3)

End Function
