; ID: 2511
; Author: Modded
; Date: 2009-06-18 13:45:33
; Title: Wipeout vapour style trails
; Description: Fast 3D mesh for trails

Graphics3D 1280,768,32,2
SetBuffer(BackBuffer())

camera= CreateCamera()
MoveEntity camera,4,4,4
ball_piv = CreatePivot()
ball = CreateSphere(5,ball_piv)
ball_tar = CreatePivot(ball_piv)
testp1 = CreatePivot(ball)
testp2 = CreatePivot(ball)
testp3 = CreatePivot(ball)
PositionEntity testp1,-.6,0,0
PositionEntity testp2,  0,1,0
PositionEntity testp3, .6,0,0

testp4 = CreatePivot(ball)
testp5 = CreatePivot(ball)
testp6 = CreatePivot(ball)
PositionEntity testp4,-.6,1,0
PositionEntity testp5,  0,2,0
PositionEntity testp6, .6,1,0

testp7 = CreatePivot(ball)
testp8 = CreatePivot(ball)
testp9 = CreatePivot(ball)
PositionEntity testp7,.4,0,0
PositionEntity testp8,  1,1,0
PositionEntity testp9, 1.6,0,0

TurnEntity ball,0,0,0
ScaleEntity ball,.1,.1,.1
PointEntity camera,ball
light = CreateLight()
RotateEntity light,0,45,0

a# = 0.0
trail = create_trail(50,testp1,testp2,testp3,255,170,170)
trail2= create_trail(500,testp4,testp5,testp6,170,255,170)
trail3= create_trail(5000,testp7,testp8,testp9,170,170,255)

Function upate_trail(trail,testp1,testp2,testp3)
	Local tsurf  = GetSurface(trail,1)
	For a = CountVertices(tsurf)-1 To 3 Step -1
		VertexCoords(tsurf,a,VertexX#(tsurf,a-3),VertexY#(tsurf,a-3),VertexZ#(tsurf,a-3))
	Next
	VertexCoords(tsurf,0,EntityX#(testp1,True),EntityY#(testp1,True),EntityZ#(testp1,True))
	VertexCoords(tsurf,1,EntityX#(testp2,True),EntityY#(testp2,True),EntityZ#(testp2,True))
	VertexCoords(tsurf,2,EntityX#(testp3,True),EntityY#(testp3,True),EntityZ#(testp3,True))
End Function

Function move_trail(trail,testp1,testp2,testp3)
	Local tsurf = GetSurface(trail,1)
	For a = CountVertices(tsurf)-1 To 0 Step -3
		VertexCoords(tsurf,a,EntityX#(testp1,True),EntityY#(testp1,True),EntityZ#(testp1,True))
		VertexCoords(tsurf,a-1,EntityX#(testp2,True),EntityY#(testp2,True),EntityZ#(testp2,True))
		VertexCoords(tsurf,a-2,EntityX#(testp3,True),EntityY#(testp3,True),EntityZ#(testp3,True))
	Next
End Function

Function create_trail(sections,testp1,testp2,testp3,colr=75,colg=75,colb=255)
	Local trail = CreateMesh()
	Local tsurf = CreateSurface(trail)
	Local trail_length = sections
	AddVertex(tsurf,EntityX#(testp1,True),EntityY#(testp1,True),EntityZ#(testp1,True))
	AddVertex(tsurf,EntityX#(testp2,True),EntityY#(testp2,True),EntityZ#(testp2,True))
	AddVertex(tsurf,EntityX#(testp3,True),EntityY#(testp3,True),EntityZ#(testp3,True))
	For a = 0 To trail_length-1
		AddVertex(tsurf,EntityX#(testp1,True),EntityY#(testp1,True),EntityZ#(testp1,True))
		AddVertex(tsurf,EntityX#(testp2,True),EntityY#(testp2,True),EntityZ#(testp2,True))
		AddVertex(tsurf,EntityX#(testp3,True),EntityY#(testp3,True),EntityZ#(testp3,True))
		AddTriangle(tsurf,0+(a*3),1+(a*3),3+(a*3))
		AddTriangle(tsurf,1+(a*3),4+(a*3),3+(a*3))
		AddTriangle(tsurf,1+(a*3),2+(a*3),4+(a*3))
		AddTriangle(tsurf,2+(a*3),5+(a*3),4+(a*3))
		AddTriangle(tsurf,0+(a*3),3+(a*3),5+(a*3))
		AddTriangle(tsurf,0+(a*3),5+(a*3),2+(a*3))
	Next
	b = CountVertices(tsurf)
	For a = 0 To b
		If a < 6
			If a < 3
				VertexColor(tsurf,a,colr,colg,colb,.1)
			Else
				VertexColor(tsurf,a,colr,colg,colb,.5)
			EndIf
		Else
			VertexColor(tsurf,a,colr,colg,colb,1-Float(a)/Float(b))
		EndIf
	Next
	EntityFX trail,1+2+4+8+32
	Return trail
End Function

wframe = False

a = -1
While Not(KeyHit(1))
	PositionEntity ball,Cos(a)*3,Cos(a*2)*3,Sin(a*2)*1.5
	PositionEntity ball_tar,0.01+Cos(a+10)*3,Sin(a+10*2)*3,0.01+Sin((a+10)*2)*1.5
	
	TurnEntity ball_piv,0,.5,.2
	PointEntity ball,ball_tar
	RotateEntity ball,EntityPitch#(ball,True),EntityYaw#(ball,True),0
;	PointEntity camera,ball
	If a = -1 Then move_trail(trail,testp1,testp2,testp3)
	If a = -1 Then move_trail(trail2,testp4,testp5,testp6)
	If a = -1 Then move_trail(trail3,testp7,testp8,testp9)
	upate_trail(trail,testp1,testp2,testp3)
	upate_trail(trail2,testp4,testp5,testp6)
	upate_trail(trail3,testp7,testp8,testp9)

	PositionEntity camera,0,0,0
	TurnEntity camera,0,1,0
	MoveEntity camera,0,0,-7

	If a# > 360 Then a#=a#-360
	a# = a# + 1

	If KeyHit(17) Then wframe = Not wframe
	Wireframe wframe

	UpdateWorld
	RenderWorld
	Color 255,255,255
	Text 10,10,TrisRendered()
	Flip; False
Wend
ClearWorld
EndGraphics
End
