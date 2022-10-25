; ID: 863
; Author: Pepsi
; Date: 2003-12-22 22:02:46
; Title: CreateMyCylinder [updated:2004-2-19]
; Description: a CreateCylinder blitz-like function. Does the Same thing with added ring segment parameter.

; CreateMyCylinder Example
; ----------------------
; By: Todd Riggins 12-22-2003
;
; a CreateCylinder blitz-like function. Does the Same thing. Just wanted to do
; this to see how to actaully create a cylinder with the addvertex/addtriangle
; commands. Just thought I would share.

; -----------------
; 2004-2-19 1:10:00
; - Added ring segments. Ring Segment param of zero acts like the
;    blitz-like Function. IE: no ring segments.
; - The added ring segments adds vertices to help bend cylinder tunnels or angle
;    pipe meshs. Ofcoarse, you will need to add your own code to manipulate the
;    cylinder's vertices.
; 2004-2-18 21:00:00
; - Fixed normal vectors problem: Needed to have cylinder ends as seperate surface
;
; Left sphere is created by the CreateMyCylinder function.
; Right sphere is created by blitz's CreateCylinder command.
;
; Controls:
; - Use mouse to rotate the cylinders
; - wireframe toggle 
; - Esc key to escape

; rediminsionable arrays
Dim tRing(0)
Dim bRing(0)

Graphics3D 640,480
SetBuffer BackBuffer()

camera=CreateCamera()

light=CreateLight()
RotateEntity light,90,0,0

;columngrid=LoadTexture("grid.bmp",9)

; enter how many segments the sphere has
vsegs=32
rsegs=4

; Create Blitz Cylinder
Cylinder=CreateCylinder(vsegs,True,0)
PositionEntity cylinder,1,0,4
;EntityTexture cylinder,columngrid

; Create Cylinder manually
mycylinder=CreateMyCylinder(vsegs,rsegs,True,0)
PositionEntity mycylinder,-1,0,4
;EntityTexture mycylinder,columngrid

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
	TurnEntity cylinder,0,0,mspx#
	TurnEntity cylinder,mspy#,0,0

	TurnEntity mycylinder,0,0,mspx#
	TurnEntity mycylinder,mspy#,0,0

	RenderWorld
	Flip
Wend

End

; ---------------------------------------------------------
Function CreateMyCylinder(verticalsegments,ringsegments=0,solid=True,parent=0)
	Local tr,tl,br,bl; 		side of cylinder
	Local ts0,ts1,newts; 	top side vertexs
	Local bs0,bs1,newbs; 	bottom side vertexs
	If verticalsegments<3 Or verticalsegments>100 Then Return 0
	If ringsegments<0 Or ringsegments>100 Then Return 0
	
	thiscylinder=CreateMesh(parent)
	thissurf=CreateSurface(thiscylinder)
	If solid=True
		thissidesurf=CreateSurface(thiscylinder)
	EndIf
	div#=Float(360.0/(verticalsegments))

	height#=1.0
	ringSegmentHeight#=(height#*2.0)/(ringsegments+1)
	upos#=1.0
	udiv#=Float(1.0/(verticalsegments))
	vpos#=1.0
	vdiv#=Float(1.0/(ringsegments+1))

	SideRotAngle#=90

	; re-diminsion arrays to hold needed memory.
	; this is used just for helping to build the ring segments...
	Dim tRing(verticalsegments)
	Dim bRing(verticalsegments)
	
	;render end caps if solid
	If solid=True
		XPos#=-Cos(SideRotAngle#)
		ZPos#=Sin(SideRotAngle#)

		ts0=AddVertex(thissidesurf,XPos#,height,ZPos#,XPos#/2.0+0.5,ZPos#/2.0+0.5)
		bs0=AddVertex(thissidesurf,XPos#,-height,ZPos#,XPos#/2.0+0.5,ZPos#/2.0+0.5)

		SideRotAngle#=SideRotAngle#+div#

		XPos#=-Cos(SideRotAngle#)
		ZPos#=Sin(SideRotAngle#)
		
		ts1=AddVertex(thissidesurf,XPos#,height,ZPos#,XPos#/2.0+0.5,ZPos#/2.0+0.5)
		bs1=AddVertex(thissidesurf,XPos#,-height,ZPos#,XPos#/2.0+0.5,ZPos#/2.0+0.5)
		
		For i=1 To (verticalsegments-2)
			SideRotAngle#=SideRotAngle#+div#

			XPos#=-Cos(SideRotAngle#)
			ZPos#=Sin(SideRotAngle#)
			
			newts=AddVertex(thissidesurf,XPos#,height,ZPos#,XPos#/2.0+0.5,ZPos#/2.0+0.5)
			newbs=AddVertex(thissidesurf,XPos#,-height,ZPos#,XPos#/2.0+0.5,ZPos#/2.0+0.5)
			
			AddTriangle(thissidesurf,ts0,ts1,newts)
			AddTriangle(thissidesurf,newbs,bs1,bs0)
		
			If i<(verticalsegments-2)
				ts1=newts
				bs1=newbs
			EndIf
		Next
	EndIf
	
	; -----------------------
	; middle part of cylinder
	thisHeight#=height#
	
	; top ring first		
	SideRotAngle#=90
	XPos#=-Cos(SideRotAngle#)
	ZPos#=Sin(SideRotAngle#)
	thisUPos#=upos#
	thisVPos#=0
	tRing(0)=AddVertex(thissurf,XPos#,thisHeight,ZPos#,thisUPos#,thisVPos#)
	For i=0 To (verticalsegments-1)
		SideRotAngle#=SideRotAngle#+div#
		XPos#=-Cos(SideRotAngle#)
		ZPos#=Sin(SideRotAngle#)
		thisUPos#=thisUPos#-udiv#
		tRing(i+1)=AddVertex(thissurf,XPos#,thisHeight,ZPos#,thisUPos#,thisVPos#)
	Next	
	
	For ring=0 To (ringsegments)

		; decrement vertical segment
		thisHeight=thisHeight-ringSegmentHeight#
		
		; now bottom ring
		SideRotAngle#=90
		XPos#=-Cos(SideRotAngle#)
		ZPos#=Sin(SideRotAngle#)
		thisUPos#=upos#
		thisVPos#=thisVPos#+vdiv#
		bRing(0)=AddVertex(thissurf,XPos#,thisHeight,ZPos#,thisUPos#,thisVPos#)
		For i=0 To (verticalsegments-1)
			SideRotAngle#=SideRotAngle#+div#
			XPos#=-Cos(SideRotAngle#)
			ZPos#=Sin(SideRotAngle#)
			thisUPos#=thisUPos#-udiv#
			bRing(i+1)=AddVertex(thissurf,XPos#,thisHeight,ZPos#,thisUPos#,thisVPos#)
		Next
		
		; Fill in ring segment sides with triangles
		For v=1 To (verticalsegments)
			tl=tRing(v)
			tr=tRing(v-1)
			bl=bRing(v)
			br=bRing(v-1)
			
			AddTriangle(thissurf,tl,tr,br)
			AddTriangle(thissurf,bl,tl,br)
		Next
		
		; make bottom ring segmentthe top ring segment for the next loop.
		For v=0 To (verticalsegments)
			tRing(v)=bRing(v)
		Next		
	Next
			
	UpdateNormals thiscylinder 
	Return thiscylinder 
End Function
