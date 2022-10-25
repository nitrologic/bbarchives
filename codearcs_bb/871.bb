; ID: 871
; Author: Jeppe Nielsen
; Date: 2004-01-02 10:53:15
; Title: Obscurer example - With viewangle
; Description: An example showing how to calculate if entities can see each other

;Obscurer example by Jeppe Nielsen 2004
;nielsen_jeppe@hotmail.com

Graphics3D 800,600,16,2

viewangle#=45

CreateLight(2)

cam=CreateCamera()
PositionEntity cam,0,40,0
RotateEntity cam,90,0,0
CameraZoom cam,2

obj1=CreateCone()
RotateMesh obj1,-90,0,0

view=CreateCone(16,1,obj1)

RotateMesh view,-90,0,0
PositionMesh view,0,0,1
ScaleMesh view,0.5,0.5,0.5
EntityColor view,128,128,0
EntityAlpha view,0.8
UpdateViewCone(view,10,viewangle#)

obj2=CreateCube()

cube1=CreateCube()
EntityColor cube1,255,255,0
EntityPickMode cube1,3

cube2=CreateCube()
EntityColor cube2,255,255,0
EntityPickMode cube2,3

cube3=CreateCube()
EntityColor cube3,255,255,0
EntityPickMode cube3,3


Repeat

If KeyDown(203)

	TurnEntity obj1,0,2,0

EndIf

If KeyDown(205)

	TurnEntity obj1,0,-2,0

EndIf

If KeyDown(78)

	viewangle#=viewangle#+1
	If viewangle#>179
		viewangle#=179
	EndIf
	UpdateViewCone(view,10,viewangle#)	

EndIf

If KeyDown(74)

	viewangle#=viewangle#-1
	If viewangle#<15
		viewangle#=15
	EndIf
	UpdateViewCone(view,10,viewangle#)	

EndIf

an#=MilliSecs()/20
PositionEntity obj2,Sin(an#)*10,0,Cos(an#)*10

an#=MilliSecs()/50
PositionEntity cube1,Sin(an#)*6,0,Cos(an#)*6

PositionEntity cube2,Sin(an#+120)*6,0,Cos(an#+120)*6
PositionEntity cube3,Sin(an#+240)*6,0,Cos(an#+240)*6

RenderWorld

If CanSeeObject(obj1,obj2,viewangle)

	Color 255,255,255
	Text 400,200,"I see it :)",1
	CameraProject cam,EntityX(obj1),EntityY(obj1),EntityZ(obj1)
	x1=ProjectedX()
	y1=ProjectedY()
	CameraProject cam,EntityX(obj2),EntityY(obj2),EntityZ(obj2)
	x2=ProjectedX()
	y2=ProjectedY()	
	Color Rnd(255),Rnd(255),Rnd(255)
	Line x1,y1,x2,y2
	

EndIf

Color 255,255,255
Text 400,10,"Left/right to rotate observer",1
Text 400,30,"+ / - to change view angle",1
Text 400,50,"Yellow boxes obscurers the view",1
Text 400,70,"View angle : "+viewangle,1

Flip

Until KeyDown(1)
End

Function UpdateViewCone(cone,depth#,angle#)
	
	sc#=Tan(angle/2)*depth*2
	
	ScaleEntity cone,sc,sc,depth#
	
End Function

Function CanSeeObject(obj1,obj2,angle#=90)

dist#=EntityDistance(obj1,obj2)
dx#=(EntityX(obj2,1)-EntityX(obj1,1)) / dist
dy#=(EntityY(obj2,1)-EntityY(obj1,1)) / dist
dz#=(EntityZ(obj2,1)-EntityZ(obj1,1)) / dist

TFormVector 0,0,1,obj1,0

;dot product:
dot#=dx*TFormedX()+dy*TFormedY()+dz*TFormedZ()

If ACos(dot#)<angle/2
	
	Return EntityVisible(obj1,obj2)
	
EndIf

End Function
