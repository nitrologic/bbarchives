; ID: 331
; Author: markrosten
; Date: 2002-08-13 12:57:59
; Title: DigitText
; Description: Displaying text on a HUD using a custom mesh

;digit sprites for HUD
;by Mark Rosten
;markbasic@planetflibble.com


Graphics3D 640,480				;for example only. not needed



;=================================== declares

;-- hud digit text declares
Global digitbrush, digitbrushblank
Global digitlasttxt$

;-- load texture font frames
Global fonttexwidth = 16
Global fonttexheight = 16
Global fonttexture = LoadAnimTexture( "digifont.png", 4+16+32, fonttexwidth, fonttexheight, 0, 43 ) 
If fonttexture = 0 Then RuntimeError( "File 'digifont.png' not found!" )



;=================================== functions


;------------- create digit entity (for hud display text)
Function CreateDigitEntity( parent, numdigits, x, y, CamZoom#=1.0 )

	;-- create work brushes?
	If Not digitbrush
	
		; create work brushes
		digitbrush = CreateBrush()
		digitbrushblank = CreateBrush()

		; clear blank digit brush texture
		; (reset all pixels in texture to alpha 0, colour 0,0,0 )
		tex = CreateTexture(1,1,4)
		SetBuffer TextureBuffer(tex)
		For ty=0 To TextureHeight(tex)-1
			For tx=0 To TextureWidth(tex)-1
				WritePixel tx,ty,0
			Next
		Next
		SetBuffer BackBuffer()

		; assign blank alpha'ed texture to blank work brush
		BrushTexture digitbrushblank, tex

		; release texture
		FreeTexture tex
	EndIf

	;-- create digit mesh
	ent = CreateDigitMesh( numdigits, parent )

	;-- adjust start position y by font height
	y = y + fonttexheight

	;-- set entity properties
	EntityFX ent, 1+4+8 ;full bright, flatshaded, disable fog
	EntityOrder ent, -1 ;bring to front

	;-- position entity relative to camera
	;   (if you have multiple cameras then you will need to hide the digit entity before
	;    rendering the cameras that you don't want the digit entity visible in)
	PositionEntity ent,x-(GraphicsWidth()/2),(GraphicsHeight()/2)-y,(GraphicsWidth()/2) * CamZoom#

	;un-comment this to ignore the positioning in 2d space and view as normal mesh
	;PositionEntity ent, -50, 0, 100 

	;-- return newly created entity
	Return ent

End Function


;------------- create digit mesh (quads joined together. one surf per quad)
Function CreateDigitMesh( numdigits=1, parent )

	;-- create mesh
	mesh = CreateMesh( parent )

	;-- determine x and y sizes for single digit segment
	xsiz = fonttexwidth-1
	ysiz = fonttexheight-1

	;-- create seperate quad per digit segment
	For digit = 1 To numdigits

		; create a surface for this quad
		surf = CreateSurface( mesh )

		; create corner vertices for quad
		v0 = AddVertex( surf, x, ysiz, 0, 0, 0 )		;top left
		v1 = AddVertex( surf, x+xsiz, ysiz, 0, 1, 0 )	;top right
		v2 = AddVertex( surf, x+xsiz, 0, 0, 1, 1 )		;bottom right
		v3 = AddVertex( surf, x, 0, 0, 0, 1 )			;bottom left

		; create 2 triangles to create quad
		AddTriangle surf, v0, v1, v2
		AddTriangle surf, v0, v2, v3

		; increment x start position for next quad
		x = x + xsiz

	Next

	;-- update normals (not sure if needed)
	UpdateNormals mesh

	;-- return newly created mesh
	Return mesh

End Function


;------------- update surface textures for digit mesh to show text
Function UpdateDigitText( ent, txt$, newx=-999, newy=-999, camzoom#=1.0 )

	;-- change in position?
	If newx <> -999
		If newx <> digitlastx Or newy <> digitlasty
			PositionEntity ent,newx-(GraphicsWidth()/2),(GraphicsHeight()/2)-newy,(GraphicsWidth()/2) * CamZoom#
		EndIf
	EndIf

	;-- if no change in text since last call, then don't need to do anything
	If txt$ = digitlasttxt$ Then Return

	;-- store this text
	digitlasttxt$ = txt$

	;-- convert to uppercase
	txt$ = Upper$(txt$)

	;-- texture digits 1 to length of passed text
	For i = 1 To Len(txt$)

		; only texture available surfaces
		If i <= CountSurfaces( ent )

			; get this digit's surface handle
			surf = GetSurface( ent, i )

			; determine texture frame
			; (this is based on layout of font image loaded)
			f = 0
			Select Mid$(txt$,i,1)
				Case ","
					f = 0
				Case "-"
					f = 1
				Case "."
					f = 2
				Case "/"
					f = 3
				Case ":"
					f = 4
				Case "="
					f = 5
				Case "?"
					f = 6
				Case "0","1","2","3","4","5","6","7","8","9"
					f = Int(Mid$(txt$,i,1))+7
				Default
					If Mid$(txt$,i,1) >= "A" And Mid$(txt$,i,1) <= "Z"
						f = (Asc(Mid$(txt$,i,1)) - 64) + 16
					EndIf
			End Select

			; set brush texture and paint surface with it?
			If f > 0
				BrushTexture digitbrush, fonttexture, f
				PaintSurface surf, digitbrush
			Else
				; no available font character for this digit. so use blank digit
				PaintSurface surf, digitbrushblank
			EndIf
		Else
			; text exceeds number of surfaces in digit mesh, so exit loop
			Exit
		EndIf
	Next

	;-- clear remaining digits?
	If Len(txt$) < CountSurfaces( ent )

		; go through remaining digit surfaces..
		For i = Len(txt$)+1 To CountSurfaces( ent )
			; get this digit's surface handle
			surf = GetSurface( ent, i )

			; use blank digit for this surface
			PaintSurface surf, digitbrushblank
		Next

	EndIf

End Function


;=================================== example usage
;                                    (remove this section when copying in to your program)

;create light
light = CreateLight()			;for test only. not needed

;initialise camera
camera = CreateCamera()
camzoom# = 1.0
CameraZoom camera,camzoom#
CameraRange camera,1,((GraphicsWidth()/2) * camzoom#)+1000

;setup digit mesh (8 digits in this example)
numdigits = 14
mydigit_entity = CreateDigitEntity( camera, numdigits, 64, 64, camzoom# )

;create cube to rotate in front of camera
cube = CreateCube()
EntityColor cube, 128,0,0
PositionEntity cube, 0, 0, 5

;render loop
While Not KeyHit(1)
	test$ = Str$(cnt) : cnt = cnt + 1
	If Len(test$) < numdigits Then test$ = String$("0",8-Len(test$)) + test$
	test$ = "Test "+test$

	;if mouse button then pass new x/y 2d coordinates to fuction
	;otherwise just update digit text
	If MouseDown(1) And detach=False
		UpdateDigitText( mydigit_entity, test$, MouseX(), MouseY(), camzoom# )
	Else
		UpdateDigitText( mydigit_entity, test$ )
	EndIf

	;turn dummy cube
	TurnEntity cube, .5, 1, .5

	;toggle wireframe on W key
	If KeyHit(17)
		showwire = Not showwire
		WireFrame showwire
	EndIf

	;toggle digit text detach from camera
	If KeyHit(57)
		detach = Not detach
		If detach
			EntityParent mydigit_entity, cube
			PositionEntity mydigit_entity, 0, 0, 10
		Else
			EntityParent mydigit_entity, camera
			RotateEntity mydigit_entity, 0, 0, 0
			UpdateDigitText( mydigit_entity, test$, 64, 64, camzoom# )
		EndIf
	EndIf

	;update and render world
	UpdateWorld
	RenderWorld

	;instructions text
	Text 0,0,"Hold mouse button to move digit text in 2d space"
	Text 0,12,"Press W to toggle wireframe mode"
	Text 0,24,"Press SPACE to toggle digit text attachment to camera or cube"

	Flip
Wend
End
