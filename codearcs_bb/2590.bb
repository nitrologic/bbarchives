; ID: 2590
; Author: Yasha
; Date: 2009-09-26 15:32:08
; Title: 3D line function
; Description: Draw a 2D line between 3D points

Global appheight=768
Global appwidth=1024
Global appdepth=32

AppTitle "";,"Are you sure you want to quit?"

Local SC_FPS=60	;Desired framerate
Local rtime=Floor(1000.0/SC_FPS)
Local ctime,limited=True
Local FPScount,FPStime,FPSframes

Graphics3D appwidth,appheight,appdepth,6
SetBuffer BackBuffer()

;Create an empty scene
Local centrecam=CreatePivot()
PositionEntity centrecam,0,25,0
Local camera=CreateCamera(centrecam)
PositionEntity camera,0,20,-50,1

Local sun=CreateLight()
PositionEntity sun,-100,400,0
PointEntity sun,centrecam

Local ground=CreateMesh(),tiles=CreateSurface(ground)
Local v1=AddVertex(tiles,-125,0,150),v2=AddVertex(tiles,125,0,150),v3=AddVertex(tiles,125,0,-100)
AddTriangle(tiles,v1,v2,v3):v2=AddVertex(tiles,-125,0,-100):AddTriangle(tiles,v1,v3,v2)
EntityColor ground,0,0,255
Local block=CreateCube():ScaleMesh block,20,5,20

Local linemesh=CreateMesh(camera),linesurf=CreateSurface(linemesh)	;The surface to use to draw the lines - single surface is always faster
;Adjust the surface's colour here, or add vertex colours to the drawing function as necessary

Local marker1=CreateSphere():PositionEntity marker1,-5,15,-5:EntityAlpha marker1,0.3	;Mark the ends of the line in 3D space
Local marker2=CreateSphere():PositionEntity marker2,5,25,5:EntityAlpha marker2,0.3

While Not KeyDown(1)
	ctime=MilliSecs()
	
	MoveEntity camera,0,KeyDown(200)-KeyDown(208),KeyDown(30)-KeyDown(44)
	TurnEntity centrecam,0,KeyDown(203)-KeyDown(205),0
	PointEntity camera,centrecam
	RenderWorld
	
	ClearSurface linesurf	;If you redraw each frame as with standard drawing commands
	Draw3DLine(camera,linesurf,-5,15,-5,5,25,5,4)
	
	If MilliSecs()-FPStime=>1000 Then FPScount=FPSframes:FPSframes=0:FPStime=MilliSecs():Else FPSframes=FPSframes+1
	Text 0,30,"FPS: "+FPScount
	Text 0,60,"Arrow keys to turn camera, A and Z to zoom"
	
	Delay (rtime-(MilliSecs()-ctime))-(limited+1)		;Free spare CPU time
	Flip limited
Wend

End


Function Draw3DLine(camera,surf,x1#,y1#,z1#,x2#,y2#,z2#,thickness#=1,entity=0)	;If entity is not 0, points refer to an entity's local space
	TFormPoint x1,y1,z1,entity,camera
	x1=TFormedX()
	y1=TFormedY()
	z1=TFormedZ()
	Local d1#=Sqr(x1*x1+y1*y1+z1*z1)/GraphicsWidth()
	TFormPoint x2,y2,z2,entity,camera
	x2=TFormedX()
	y2=TFormedY()
	z2=TFormedZ()
	Local d2#=Sqr(x2*x2+y2*y2+z2*z2)/GraphicsWidth()
	
	Local theta#=ATan2(y2*(d1/d2)-y1,x2*(d1/d2)-x1)
	Local xTForm#=Cos(theta)*thickness
	Local yTForm#=Sin(theta)*thickness
	
	Local V0=AddVertex(surf,x1+yTForm*d1,y1-xTForm*d1,z1)
	Local V1=AddVertex(surf,x1-yTForm*d1,y1+xTForm*d1,z1)
	Local V2=AddVertex(surf,x2-yTForm*d2,y2+xTForm*d2,z2)
	Local V3=AddVertex(surf,x2+yTForm*d2,y2-xTForm*d2,z2)
	
	AddTriangle(surf,V0,V1,V2)
	AddTriangle(surf,V2,V3,V0)
End Function
