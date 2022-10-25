; ID: 990
; Author: Pepsi
; Date: 2004-04-07 22:13:24
; Title: OpenGL-like Point Primitives
; Description: (Updates! [April  9,2004]: Optimized Speed by: DJWoodgate ) --- Little system to create opengl-like point primitives for use with 3D editors

; OpenGL-Like Point Primitives - Free for all! No $$$ required!
; History:
; Version 1
; - Original code by: Todd Riggins
; Version 2
; - Optimized for MORE speed and some code cleanup  By: DJWoodgate
; Version 3
; - Removed if statments with comparing projx/y to the point's size in which
;   eliminated unwanted graphic glitches.
; ------------------------------------------------------------------------
; Directions and notes
; ------------------------------------------------------------------------
; Use arrows to move around
; right mouse click to rotate camera
; - Notice how the square-ish points remain the same size no matter how close they
;   are to the camera! 
; - Unfortunitly this system is not fast enough for big "group vertice selections" like
;   you would want in moddeling editors. It's definitely fast enough for level editors like Maplet!
; - This system is all in 3D. Didn't want to use 2D graphics here. Doing this in 3D help'd me
;   mimic OpenGl point primitives pretty much To the T.
; !!!! If anybody has any ideas to speed this up, please let me know. Moving the vertices around all
; the time is what slows this system down the more point primitives you have.

; ------------------------------------------------------------------------
;  Superior Documentation...
; ------------------------------------------------------------------------
; thisPoint.POINTPRIMITIVE=AddPoint.POINTPRIMITIVE( x#, y#, z#)
;  		AddPoint returns a type handle to a newly created point primitive.
;        x#, y#, z# parameters = initial 3d location for point primitive.
; ------------------------------------------------------------------------
; PositionPoint(thisPoint.POINTPRIMITIVE, x#, y#, z#)
;		Position's an already created point primitive in a new 3d location.
; ------------------------------------------------------------------------
; PointSize(thisPoint.POINTPRIMITIVE,size)
; 		Make the point primitive smaller or bigger. Default = 2
; ------------------------------------------------------------------------
; PointBias(thisPoint.POINTPRIMITIVE,bias#)
;       Move the point closer to or further from the camera. Helps in 
;       z-buffering fighting issues. Default = 0.25
; ------------------------------------------------------------------------
; ColorPoint(thisPoint.POINTPRIMITIVE,red,green,blue)
; 		Be artistic! Color your point your favorite color! :P
; ------------------------------------------------------------------------
; DeletePoint(thisPoint.POINTPRIMITIVE)
;		Delete Point.
; ------------------------------------------------------------------------
; UpdatePoints(mycamera)
; 		Need this in the main loop! Pass your camera's handle in the parameter!
; ------------------------------------------------------------------------


; NOTE: xCameraControls is there just for example sakes...

Graphics3D 640, 480, 0, 2
SetBuffer BackBuffer(  )
Global grwidth = GraphicsWidth(), grheight=GraphicsHeight()
Global grhalfwidth = grwidth/2, grhalfheight = grheight/2

;camera
camera=CreateCamera()
CameraRange camera,0.1,1000
CameraClsColor camera,40,100,60	
PositionEntity camera, 0, 10, -25 

;light
Global light
light=CreateLight()
LightColor light,32,32,32
TurnEntity light,45,45,0

; various camera control variable helpers
Global dest_cam_yaw#
Global dest_cam_pitch#
Global mfb=0

; the point primitive structure 
Type POINTPRIMITIVE
	Field Px#
	Field Py#
	Field Pz#
	Field size
	Field bias#
	Field red
	Field green
	Field blue
	Field vi0
	Field vi1
	Field vi2
	Field vi3
	Field Deleted
End Type

; Single Surface Partical System For Point Primitives
; - All points use the same surface of the mesh, but they
; - own their own vertices each...

Global PointQuads=CreateMesh()
Global PQsSurf=CreateSurface(PointQuads)
EntityFX PointQuads,3 ; Points are vertex colored and fullbright

; !!!!!!!!!!!!!!!!!
; change 'number' to make more or less points
; !!!!!!!!!!!!!!!!!
number=15
For i=-number To number
	For j=-number To number
		thisPoint.POINTPRIMITIVE=AddPoint(i,0,j)
		ColorPoint(thisPoint,Rnd(127)+128,Rnd(127)+128,Rnd(127)+128)
	Next
Next

; ------------
; MAIN Example
; ------------
Repeat
	xCameraControls(camera)
	UpdatePoints(camera)
	RenderWorld
	Flip
Until KeyHit(1) = True

Function AddPoint.POINTPRIMITIVE(px#,py#,pz#)
	For newpoint.POINTPRIMITIVE = Each POINTPRIMITIVE
		If newpoint\deleted=True Then newpoint\deleted=False : Exit
	Next	
	If newpoint = Null Then
		newpoint.POINTPRIMITIVE = New POINTPRIMITIVE
		newpoint\vi0=AddVertex(PQsSurf, 0, 0,0);,0,0 ; 0 left top
		newpoint\vi1=AddVertex(PQsSurf, 0, 0,0);,1,0 ; 1 right top
		newpoint\vi2=AddVertex(PQsSurf, 0, 0,0);,0,1 ; 2 left bottom
		newpoint\vi3=AddVertex(PQsSurf, 0, 0,0);,1,1 ; 3 right bottom
		AddTriangle PQsSurf,newpoint\vi2,newpoint\vi0,newpoint\vi1 ; and 2 triangles...
		AddTriangle PQsSurf,newpoint\vi2,newpoint\vi1,newpoint\vi3
	EndIf
	PositionPoint(newpoint,px,py,pz)
	ColorPoint(newpoint,255,0,0)
	Pointsize(newpoint,2)
	PointBias(newpoint,0.25)
	Return newpoint	
End Function


Function PositionPoint(thisPoint.POINTPRIMITIVE,px#,py#,pz#)
	thisPoint\px = px : thispoint\py = py : thispoint\pz = pz
End Function

Function PointSize(thisPoint.POINTPRIMITIVE,size)
	thisPoint\size=size
End Function

Function PointBias(thisPoint.POINTPRIMITIVE,bias#)
	thisPoint\bias#=bias#
End Function

Function ColorPoint(thisPoint.POINTPRIMITIVE,red,green,blue)
	thisPoint\red=red
	thisPoint\green=green
	thisPoint\blue=blue
	VertexColor PQsSurf,thisPoint\vi0,thisPoint\red,thisPoint\green,thisPoint\blue
	VertexColor PQsSurf,thisPoint\vi1,thisPoint\red,thisPoint\green,thisPoint\blue
	VertexColor PQsSurf,thisPoint\vi2,thisPoint\red,thisPoint\green,thisPoint\blue
	VertexColor PQsSurf,thisPoint\vi3,thisPoint\red,thisPoint\green,thisPoint\blue
End Function

Function DeletePoint(thisPoint.POINTPRIMITIVE)
	VertexCoords PQsSurf,thisPoint\vi0,0,0,0
	VertexCoords PQsSurf,thisPoint\vi1,0,0,0
	VertexCoords PQsSurf,thisPoint\vi2,0,0,0
	VertexCoords PQsSurf,thisPoint\vi3,0,0,0
	thisPoint\deleted=True
End Function

Function UpdatePoints(mycamera)
	Local TlX#, TLY#, TLZ#, TRX#, TRY#, TRZ#, BLX#, BLY#, BLZ#, BRX#, BRY#, BRZ#
	Local Zdist#, ProjX#, ProjY#

	; loop through and update points...
	For thisPoint.POINTPRIMITIVE = Each POINTPRIMITIVE
		If Not thispoint\deleted 	
			; Project point position to screen
			CameraProject mycamera,thispoint\px,thispoint\py,thispoint\pz
			projx = ProjectedX() : projy = ProjectedY()
			; if on screen then...
			If ProjectedZ()>0 
				; Get distance of point from the camera viewplane
				TFormPoint thispoint\px,thispoint\py,thispoint\pz, 0,mycamera
				zdist = TFormedZ()-thispoint\bias
				; project our box back into worldspace
				Reverseproject(mycamera,projx-thispoint\size,projy-thispoint\size,Zdist)
					TLX#=TFormedX#()
					TLY#=TFormedY#()
					TLZ#=TFormedZ#()
				Reverseproject(mycamera,projx+thisPoint\size,projy-thisPoint\size,Zdist)
					TRX#=TFormedX#()
					TRY#=TFormedY#()
					TRZ#=TFormedZ#()
				Reverseproject(mycamera,projx-thisPoint\size,projy+thisPoint\size,Zdist)
					BLX#=TFormedX#()
					BLY#=TFormedY#()
					BLZ#=TFormedZ#()
				Reverseproject(mycamera,projx+thisPoint\size,projy+thisPoint\size,Zdist)
					BRX#=TFormedX#()
					BRY#=TFormedY#()
					BRZ#=TFormedZ#()
				; Update the point in worldspace
				VertexCoords PQsSurf,thisPoint\vi0,TLX#,TLY#,TLZ#
				VertexCoords PQsSurf,thisPoint\vi1,TRX#,TRY#,TRZ#
				VertexCoords PQsSurf,thisPoint\vi2,BLX#,BLY#,BLZ#
				VertexCoords PQsSurf,thisPoint\vi3,BRX#,BRY#,BRZ#
			EndIf
		EndIf
	Next							
End Function

; Reverse project a point. Need to specify camzoom and Z
; will not deal with scaled cameras so don't!
Function Reverseproject(cam,sx#,sy#,z#,zoom#=1,dest=0)
	Local f#,x#,y#
	f# = Zoom * grhalfwidth
	x = ((sx-grhalfwidth)/f)  * z
	y = ((grhalfheight-sy)/f) * z
	TFormPoint x,y,z,cam,dest ; camera to dest (0 for world)
End Function


Function xCameraControls(mycamera)

	Local thisspeed#=0.25
	Local thisUnitSqr#=1.0

	If MouseDown(1)=0 And MouseDown(2)=0 Then mfb=0

		;zoom
	   	If KeyDown(208) Then MoveEntity mycamera,0,0,-thisspeed#
	   	If KeyDown(200) Then MoveEntity mycamera,0,0,thisspeed#
	
		;straff left/right
		If KeyDown(203) Then MoveEntity mycamera,-thisspeed#,0,0
		If KeyDown(205) Then MoveEntity mycamera,thisspeed#,0,0
   
		;elevate up/down
		If KeyDown(157) Then MoveEntity mycamera,0,-thisspeed#,0
		If KeyDown(54) Then MoveEntity mycamera,0,thisspeed#,0

		If MouseDown(2)=True And mfb=0
			mfb=1
			MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
		EndIf

		If MouseDown(2)=False And mfb=1
			mfb=0
		EndIf	
	
		If mfb>0
			mxs#=MouseXSpeed()
			mys#=MouseYSpeed()
	
			dest_cam_yaw#=dest_cam_yaw#-mxs#
			dest_cam_pitch#=dest_cam_pitch#+mys#
			RotateEntity mycamera,dest_cam_pitch#,dest_cam_yaw#,0
			MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
		EndIf
	
End Function
