; ID: 2980
; Author: Blitzplotter
; Date: 2012-10-08 19:48:04
; Title: Pointy Rockets
; Description: Plots 4 rockets and rotates them to point to the next one (needs the rocket.3ds)

; Movement & Rotation ***
; By Paul Gerfen (www.gamecoding.co.uk)

; added to slightly by Blitzplotter



Global quadrant=9


Graphics3D 800,600

SetBuffer BackBuffer()

camera=CreateCamera()
CameraViewport camera,0,0,800,600

light=CreateLight()

rocket=LoadMesh( "rocket.3ds" )
PositionEntity rocket,0,0,37


r1_quad=return_angle(1,1,3,3)
r2_quad=return_angle(0,0,3,-3)
r3_quad=return_angle(0,0,-3,-3)
r4_quad=return_angle(0,0,-3,3)


Print r1_quad

Print r2_quad

Print r3_quad

Print r4_quad


;give rockets positions E,S,W & N (ish) co-ordinates respectively

x1=-28
 y1=18

x2=8
 y2=8

x3=18
 y3=-8

x4=-18
 y4=-18



rocket1=LoadMesh( "rocket.3ds" )
PositionEntity rocket1,x1,y1,47

rocket2=LoadMesh( "rocket.3ds" )
PositionEntity rocket2,x2,y2,47

rocket3=LoadMesh( "rocket.3ds" )
PositionEntity rocket3,x3,y3,47

rocket4=LoadMesh( "rocket.3ds" )
PositionEntity rocket4,x4,y4,47


r1_angle=return_angle(x1,y1,x2,y2)

r2_angle=return_angle(x2,y2,x3,y3)

r3_angle=return_angle(x3,y3,x4,y4)

r4_angle=return_angle(x4,y4,x1,y1)

Print " "
Print "Thats a slice of debug... Press Any Key"

WaitKey



RotateEntity rocket1,0,0,-r1_angle

RotateEntity rocket2,0,0,-r2_angle

RotateEntity rocket3,0,0,-r3_angle

RotateEntity rocket4,0,0,-r4_angle






While Not KeyHit(1)

	
	If KeyDown(200)	Then
		MoveEntity rocket,0,0.05,0
	EndIf
	
	If KeyDown(203)	Then
		TurnEntity rocket,0,0,1.0
	EndIf
	
	If KeyDown(205)	Then
		TurnEntity rocket,0,0,-1.0
	EndIf
	
	UpdateWorld
	RenderWorld
	
	Text 320,500,"One rocket - Movement & Rotation"
	
	Text 270,522,"With four other rockets that'll point to each other"
	
	Flip

Wend
End



Function return_angle(x1,y1,x2,y2)

quadrant=5

;first establish which quadrant 2nd co-ords are in with respect to first co-ords

If x1<x2

	If y1<y2
		quadrant=1
	Else
		quadrant=2
	EndIf
	
Else

	If y1>y2
		quadrant=3
	Else
		quadrant=4
	EndIf

EndIf

Select(quadrant)

Case 1
	Print "1-1"
	Print" x1:"+x1+" y1:"+y1+" x2: "+x2+" y2: "+y2
	angle#= Float ATan2(y2-y1,x2-x1)
	;1st quadrant fixer:
	angle2#= 0+(90-angle)
	
	
Case 2
	Print "2-2"
	Print" x1:"+x1+" y1:"+y1+" x2: "+x2+" y2: "+y2
	angle#= (Float (ATan2(y2-y1,x2-x1)))+180
	;2nd quadrant fixer:
	angle2# = 90+(180-angle)
	
	
Case 3
	Print "3-3"
	Print" x1:"+x1+" y1:"+y1+" x2: "+x2+" y2: "+y2
	angle#= (Float (ATan2(y2-y1,x2-x1)))+360
	;3rd quadrantfixer:
	angle2#=180+(270-angle)
	
	
	
Case 4
	Print "4-4"
	Print" x1:"+x1+" y1:"+y1+" x2: "+x2+" y2: "+y2
	angle#= (Float (ATan2(y2-y1,x2-x1)))+180
	;4th quadrant fixer
	angle2#=270+(360-angle)
	
	
End Select


Return(angle2)



End Function
