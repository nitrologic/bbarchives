; ID: 2986
; Author: Captain Wicker Soft
; Date: 2012-10-13 22:40:10
; Title: Starfield Stress Test (Blitz3D)
; Description: Starfield stress test in blitz3d

Graphics3D 800,600,32,2
SeedRnd MilliSecs()
SetBuffer BackBuffer()

Local light=CreateLight()

Global camera=CreateCamera()


Dim spheres(1000)


Repeat

	For t=1 To 1000
		spheres(t)=CreateSphere(1.5)
		ScaleEntity spheres(t),.01,.01,.01
		PositionEntity spheres(t),Rand(-t,t),Rand(-t,t),Rand(-t,t)
	Next


	MoveCamera(camera)

	UpdateWorld()
	RenderWorld()
	ShowFPS(10,10)
	Flip(True)
Until KeyHit(1)


Function MoveCamera(camera_name%)
	If KeyDown(203)
		MoveEntity(camera_name%,-1,0,0)
	ElseIf KeyDown(205)
		MoveEntity(camera_name%,1,0,0)
	EndIf
	If KeyDown(200)
		MoveEntity(camera_name%,0,0,1)
	ElseIf KeyDown(208)
		MoveEntity(camera_name%,0,0,-1)
	EndIf
End Function

Function ShowFPS(x#,y#)
	timenow=MilliSecs()
	If timenow>telltime Then
		telltime=timenow+1000
		frames=0
	Else
		frames=frames+1
	EndIf
		Text x#,y#,frames
End Function
