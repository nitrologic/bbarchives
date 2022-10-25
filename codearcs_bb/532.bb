; ID: 532
; Author: fredborg
; Date: 2002-12-18 10:34:41
; Title: LineOfSight3D
; Description: Useful for testing whether an enemy can see the player!

;
; LineOfSight3D - Example usage
;
; Created by Mikkel Fredborg
;

Graphics3D 640,480 
SetBuffer BackBuffer() 

;setup light
AmbientLight 128,128,128
light = CreateLight()

;create a camera
camera=CreateCamera()
PositionEntity camera,0,10,0
RotateEntity camera,90,0,0
CameraProjMode camera,2
CameraZoom camera,0.05

;create a wall
wall = CreateCube()
EntityColor wall,128,128,128
ScaleEntity wall,5,1,1
PositionEntity wall,0,0,-4
EntityPickMode wall,2

;create two targets
c2=CreateSphere() 
EntityColor c2,128,50,50
PositionEntity c2,2,0,0
EntityPickMode c2,2

c4=CreateSphere() 
EntityColor c4,128,50,50
PositionEntity c4,2,0,-6
EntityPickMode c4,2

; create observer
c3=CreateCone() 
RotateMesh c3,90,0,0
EntityColor c3,50,128,90

; viewcone dummy
vd# = 10.0 
va# = 90.0

Global viewcone = CreateDisc(va/10,c3,1,-va/2.0,va/2.0)
EntityColor viewcone,0,50,64
EntityFX viewcone,1

lastva# = va#
lastvd# = vd#

While Not KeyDown( 1 ) 

	; controls
	TurnEntity c3,0,(KeyDown(203)-KeyDown(205)),0 		; Left/Right arrow rotates
	MoveEntity c3,0,0,(KeyDown(200)-KeyDown(208))*0.1	; Up/Down arrow moves forward and backward
	
	vd# = vd#+(KeyDown(201)-KeyDown(209))*0.1			; Page Up/Down Alters view range
	If vd<1.0 Then vd = 1.0

	va# = va#+(KeyDown(210)-KeyDown(211))				; Insert/Delete alters view angle
	If va<  1.0 Then va =   1.0
	If va>360.0 Then va = 360.0

	; update viewcone mesh, if changes have been made
	If va<>lastva
		FreeEntity viewcone
		viewcone = CreateDisc(va/10,c3,1.0,-va/2.0,va/2.0)
		EntityColor viewcone,0,50,64
		EntityFX viewcone,1
		
		lastva = va
		lastvd = vd
	End If

	ScaleEntity viewcone,vd,vd,vd

	UpdateWorld
	RenderWorld 

	Text 320,10,"Arrows - move | Page Up/Down - view range | Insert\Delete - view angle",True

	visible1 = LineOfSight3D(c3,c2,vd,va)
	visible2 = LineOfSight3D(c3,c4,vd,va)
	
	If visible1
		Text 320,40,"Observer can see target 1",True
		CameraProject camera,EntityX(c3,True),EntityY(c3,True),EntityZ(c3,True)
		x0 = ProjectedX()
		y0 = ProjectedY()
			
		CameraProject camera,EntityX(c2,True),EntityY(c2,True),EntityZ(c2,True)
		x1 = ProjectedX()
		y1 = ProjectedY()

		Line(x0,y0,x1,y1)				
	Else
		Text 320,40,"Observer can NOT see target 1",True
		CameraProject camera,EntityX(c3,True),EntityY(c3,True),EntityZ(c3,True)
		x0 = ProjectedX()
		y0 = ProjectedY()
			
		CameraProject camera,EntityX(c2,True),EntityY(c2,True),EntityZ(c2,True)
		x1 = ProjectedX()
		y1 = ProjectedY()

		DottedLine(x0,y0,x1,y1)
	End If
	
	If visible2
		Text 320,50,"Observer can see target 2",True
			
		CameraProject camera,EntityX(c3,True),EntityY(c3,True),EntityZ(c3,True)
		x0 = ProjectedX()
		y0 = ProjectedY()
			
		CameraProject camera,EntityX(c4,True),EntityY(c4,True),EntityZ(c4,True)
		x1 = ProjectedX()
		y1 = ProjectedY()

		Line(x0,y0,x1,y1)
	Else
		Text 320,50,"Observer can NOT see target 2",True
		CameraProject camera,EntityX(c3,True),EntityY(c3,True),EntityZ(c3,True)
		x0 = ProjectedX()
		y0 = ProjectedY()
			
		CameraProject camera,EntityX(c4,True),EntityY(c4,True),EntityZ(c4,True)
		x1 = ProjectedX()
		y1 = ProjectedY()

		DottedLine(x0,y0,x1,y1)
	End If

	Text 320,440,"View Range - "+vd+"| View Angle - "+va,True

	Flip 

Wend 
End 

Function DottedLine(stx#,sty#,enx#,eny#,dotlength=5)

	mvx#=Stx-enx:mvy#=sty-eny
	If mvx<0 mvx=-mvx
	If mvy<0 mvy=-mvy
	If mvy>mvx mv#=mvy Else mv#=mvx
	stpx#=(mvx/mv):If Stx>enx stpx=-stpx
	stpy#=(mvy/mv):If Sty>eny stpy=-stpy

	; Calculate the color of the line
	c = (ColorRed()*256*256)+(ColorGreen()*256)+ColorBlue()
	LockBuffer GraphicsBuffer()
	For nc=0 To Floor(mv)
		If stx>0 And stx<GraphicsWidth()-1
			If sty>0 And sty<GraphicsHeight()-1
				If (Int(nc/dotlength) Mod 2) = 0
					WritePixelFast stx,sty,c
				End If
			End If
		End If
			
		stx=stx+stpx
		sty=sty+stpy
	Next
	UnlockBuffer GraphicsBuffer()

End Function

Function CreateDisc(segments=8,parent=0,radius#=1.0,anglemin#=0.0,anglemax#=360.0)
	
	If segments<1 Then segments = 1
	
	disc = CreateMesh(parent)
	surf = CreateSurface(disc)
	
	v0 = AddVertex(surf,0,0,0)
	v1 = AddVertex(surf,Sin(anglemin)*radius,0,Cos(anglemin)*radius)
	
	anglestep# = (anglemax-anglemin)/Float(segments)
	
	For segment = 1 To segments
		angle# = anglemin+(anglestep*segment)
		v2 = AddVertex(surf,Sin(angle)*radius,0,Cos(angle)*radius)
		AddTriangle(surf,v0,v1,v2)
		v1 = v2
	Next

	Return disc

End Function

;
; LineOfSight3D()
;
; Usage:
;	observer	= Entity that is looking
;	target		= Entity that the observer is looking for
;	viewrange	= How far can the observer see (in units)
;	viewangle	= How wide is the view of the observer (in degrees)
;
; Created by Mikkel Fredborg - Use as you please
;
Function LineOfSight3D(observer,target,viewrange#=10.0,viewangle# = 90.0)

	;distance between observer and target
	Local dist# = EntityDistance(observer,target)

	;check if the target is within viewrange 
	If dist<=viewrange
		
		;observer vector
		TFormVector 0,0,1,observer,0
		Local ox# = TFormedX()
		Local oy# = TFormedY()
		Local oz# = TFormedZ()
	
		;pick vector
		Local dx# = (EntityX(target,True)-EntityX(observer,True))/dist#
		Local dy# = (EntityY(target,True)-EntityY(observer,True))/dist#
		Local dz# = (EntityZ(target,True)-EntityZ(observer,True))/dist#

		;dot product
		Local dot# = ox*dx + oy*dy + oz*dz

		;check if the target is within the viewangle
		If dot => Cos(viewangle/2.0)
			; check if something is blocking the view
			If LinePick(EntityX(observer,True),EntityY(observer,True),EntityZ(observer,True),dx*viewrange,dy*viewrange,dz*viewrange,0.01)=target
				; observer can see target
				Return True
			End If
		End If
		
	End If

	; observer cannot see target	
	Return False

End Function
