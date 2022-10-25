; ID: 2735
; Author: Blitzplotter
; Date: 2010-06-26 04:41:56
; Title: 3D line function the sequel
; Description: Incorporated a little array into Yasha's excellent 3D line function

;Global appheight=768
;Global appwidth=1024
;Global appdepth=32

Global appheight=800
Global appwidth=800
Global appdepth=32

AppTitle "Blitzplotter, plotting Balls using an array with a little help from Yasha";,"Are you sure you want to quit?"

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

;my marker

Local marker3=CreateSphere():PositionEntity marker3,5,35,15:EntityAlpha marker3,0.3


;#####      3d point creation array     #####
;#####                                  #####

MAXBALLS#=100

;array to hold a few 3d points
Dim ay(100,2)

;how many balls do you want plotted
Global extrapoints=80

If extrapoints>MAXBALLS
	Print"The array is only 100 big... please reduce your balls"
	Print"Press any key"
	WaitKey
	End
EndIf


;populate First 10 co-ords of array
xGap=2   ;distance in the xplane between points
yGap=3	 ;distance in the yplane between points
zGap=2   ;distance in the zplane between points

slope_in_zed=1  ;1 adjusts co-ords in the z plane, 0 doesn't

;populate array with 3d points for a zig zag effect
For x = 1 To extrapoints
	ay(x,0) = x*xGap;  x co-ords
	
	
	If x Mod 2 = 1; y co-ords up and down
		ay(x,1) = x * -yGap
	Else
		ay(x,1) = x * yGap
	EndIf
	
	If slope_in_zed=1
		If x Mod 2 = 1; z co-ords up and down
			ay(x,2) = x * -zGap
		Else
			ay(x,2) = x * zGap
		EndIf
	Else
		ay(x,2) = x*zGap; constant z co-ords
	EndIf

	;print out the co-ordinates
;	Print "x: "+ay(x,0)+" y: "+ay(x,1)+" z: "+ay(x,2)
;	
;	Print" "
	
Next

;Print" Press any key...."
Print " "
Print " "
Print " "
;WaitKey

;#####                             #####
;#####  end point creation         #####
;#####                             #####



Dim marker(4)

;this plots the spheres from the points within the array

For cz = 1 To extrapoints
	
	Local marker5=CreateSphere():PositionEntity marker5,5+ay(cz,0),35+ay(cz,1),15+ay(cz,2):EntityAlpha marker5,0.3
	
Next



While Not KeyDown(1)
	ctime=MilliSecs()
	
	MoveEntity camera,0,KeyDown(200)-KeyDown(208),KeyDown(30)-KeyDown(44)
	TurnEntity centrecam,0,KeyDown(203)-KeyDown(205),0
	PointEntity camera,centrecam
	RenderWorld
	
	ClearSurface linesurf	;If you redraw each frame as with standard drawing commands
	Draw3DLine(camera,linesurf,-5,15,-5,5,25,5,4)
	
	;introducing the third point
	Draw3DLine(camera,linesurf,5,25,5,5,35,15,4)
	
	linethick=2
	
	;;iterating through the array of co-ordinates:-
	For ct = 1 To extrapoints
		
		;prototype new Draw 3D Linw which will march through a pre-defined set of points within an array
		Draw3DLine(camera,linesurf,5+ay(ct,0),35+ay(ct,1),15+ay(ct,2),5+ay(ct+1,0),35+ay(ct+1,1),15+ay(ct+1,2),linethick)
		
	Next
	
	
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
