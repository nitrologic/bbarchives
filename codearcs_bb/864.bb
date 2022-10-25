; ID: 864
; Author: Pepsi
; Date: 2003-12-22 23:01:54
; Title: CreateMyCone [Updated:2004-2-18]
; Description: a CreateCone blitz-like function. The cone looks better then blitz CreateCone's generated cone now! ie: no smoothed edges on the end face.

; CreateMyCone Example
; ----------------------
; By: Todd Riggins 12-22-2003
;
; a CreateCone blitz-like function. Does the Same thing. Just wanted to do
; this to see how to actaully create a sphere with the addvertex/addtriangle
; commands. Just thought I would share.
;

; -------
; 2004-2-18 21:00:00
; Fixed normal vectors problem: Needed to have cone end as seperate surface.
; The blitz CreateCone function smooth shades the edge from the sides and 
; end face making it look less like a cone. My version clearly shows the
; end face as it should be in reality, like a cone.

; Left sphere is created by the CreateMyCone function.
; Right sphere is created by blitz's CreateCone command.
;
; Controls:
; - Use mouse to rotate the cones
; - wireframe toggle 
; - Esc key to escape


Graphics3D 640,480
SetBuffer BackBuffer()

camera=CreateCamera()

light=CreateLight()
RotateEntity light,90,0,0

;tex=LoadTexture("grid.bmp",9)

; enter how many segments the sphere has
segs=32

; Create Blitz Cone
cone=CreateCone(segs)
PositionEntity cone,1,0,4
;EntityTexture cone,tex

; Create Cone manually
mycone=CreateMyCone(segs)
PositionEntity mycone,-1,0,4
;EntityTexture mycone,tex

; key helper
wkey=0

MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

While Not KeyDown( 1 )

	mspx#=MouseXSpeed()
	mspy#=MouseYSpeed()

	If KeyDown(17) And wkey=0
		wkey=1
	EndIf

	If KeyDown(17)=False And wkey=1
		wkey=0
		If wframe=0
			wframe=1
		Else
			wframe=0
		EndIf
		If wframe=0 WireFrame False
		If wframe=1 WireFrame True
	EndIf


	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
	TurnEntity cone,0,0,mspx#
	TurnEntity cone,mspy#,0,0

	TurnEntity mycone,0,0,mspx#
	TurnEntity mycone,mspy#,0,0

	RenderWorld
	Flip
Wend

End

; ---------------------------------------------------------
Function CreateMyCone(segments,solid=True,parent=0)
	Local top,br,bl; 		side of cone
	Local bs0,bs1,newbs; 	bottom side vertices
	
	If segments<3 Or segments>100 Then Return 0
	
	thiscone=CreateMesh(parent)
	thissurf=CreateSurface(thiscone)
	If solid=True
		thissidesurf=CreateSurface(thiscone)
	EndIf
	div#=Float(360.0/(segments))

	height#=1.0
	upos#=1.0
	udiv#=Float(1.0/(segments))
	RotAngle#=90	

	; first side
	XPos#=-Cos(RotAngle#)
	ZPos#=Sin(RotAngle#)

	top=AddVertex(thissurf,0.0,height,0.0,upos#-(udiv#/2.0),0)
	br=AddVertex(thissurf,XPos#,-height,ZPos#,upos#,1)

	If solid=True Then bs0=AddVertex(thissidesurf,XPos#,-height,ZPos#,XPos#/2.0+0.5,ZPos#/2.0+0.5)

	RotAngle#=RotAngle#+div#

	XPos#=-Cos(RotAngle#)
	ZPos#=Sin(RotAngle#)
				
	bl=AddVertex(thissurf,XPos#,-height,ZPos#,upos#-udiv#,1)	

	If solid=True Then bs1=AddVertex(thissidesurf,XPos#,-height,ZPos#,XPos#/2.0+0.5,ZPos#/2.0+0.5)
	
	AddTriangle(thissurf,bl,top,br)

	; rest of sides
	For i=1 To (segments-1)
		br=bl
		upos#=upos#-udiv#
		top=AddVertex(thissurf,0.0,height,0.0,upos#-(udiv#/2.0),0)	
	
		RotAngle#=RotAngle#+div#

		XPos#=-Cos(RotAngle#)
		ZPos#=Sin(RotAngle#)
		
		bl=AddVertex(thissurf,XPos#,-height,ZPos#,upos#-udiv#,1)	

		If solid=True Then newbs=AddVertex(thissidesurf,XPos#,-height,ZPos#,XPos#/2.0+0.5,ZPos#/2.0+0.5)
	
		AddTriangle(thissurf,bl,top,br)
		
		If solid=True
			AddTriangle(thissidesurf,newbs,bs1,bs0)
		
			If i<(segments-1)
				bs1=newbs
			EndIf
		EndIf
	Next
	
	UpdateNormals thiscone 
	Return thiscone 
End Function
