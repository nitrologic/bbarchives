; ID: 1760
; Author: ImaginaryHuman
; Date: 2006-07-23 16:51:09
; Title: Cubic Bezier Curves
; Description: Lets you play with Bezier Curves

'Cubic Bezier splines
'Adapted from code Wedoe

'Press Space for a totally new random spline
'Press left arrow to control the previous control point
'Press right arrow to control the next control point
'Press Escape to exit
'Move  mouse to see it adapt

Strict

Type Point
	Field x:Int
	Field y:Int
End Type
Const Accuracy:Double=0.03		'Lower has more line segments
	
SeedRnd(MilliSecs())	'Different each time
SetGraphicsDriver GLMax2DDriver()
Graphics 640,480,0
SetBlend LIGHTBLEND
Local DoAnother:Int
Local ControlPoint:Int
Local Counter:Int
glEnable(GL_LINE_SMOOTH)	'Quick antaliasing hack
glHint(GL_LINE_SMOOTH_HINT,GL_NICEST)
glLineWidth(3.0)
Repeat
	DoAnother=False
	Local NumPoints:Int=Rand(4,16)	'Whatever >3
	NumPoints=NumPoints-(NumPoints Mod 3)+1	'3 points per bezier plus 1, 4th point of each bez is shared
	Local Points:Point[NumPoints]
	For Local p:Int=0 To NumPoints-1
		Points[p]=New Point
		Points[p].x=Rand(20,620)
		Points[p].y=Rand(20,460)
	Next
	ControlPoint=NumPoints/2
	Repeat
		Cls
		Points[ControlPoint].x=MouseX()
		Points[ControlPoint].y=MouseY()
		'Draw controls
		For Local p:Int=0 To NumPoints-1
			SetColor $88,$88,$88
			DrawLine Points[p].x-7,Points[p].y-7,Points[p].x+7,Points[p].y+7
			DrawLine Points[p].x-7,Points[p].y+7,Points[p].x+7,Points[p].y-7
			SetColor $00,$44,$88
			DrawLine Points[p].x,Points[p].y,Points[Min(NumPoints-1,p+1)].x,Points[Min(NumPoints-1,p+1)].y
			DrawText p,Points[p].x+5,Points[p].y+5
		Next
		'Draw segments
		Local PrevX:Double,PrevY:Double
		SetColor $FF,$FF,$FF
		Counter=0
		For Local S:Int=0 To NumPoints-4 Step 3
			For Local T:Double=0 To 1 Step Accuracy
				Local X:Double=Points[s].x*(1-T)^3 + 3*Points[s+1].x*(1-T)^2*T + 3*Points[s+2].x*(1-T)*T^2 + Points[s+3].x*T^3
				Local Y:Double=Points[s].y*(1-T)^3 + 3*Points[s+1].y*(1-T)^2*T + 3*Points[s+2].y*(1-T)*T^2 + Points[s+3].y*T^3
				If PrevX=0 And PrevY=0
					PrevX=X
					PrevY=Y
				EndIf
				DrawLine PrevX,PrevY,X,Y,False
				PrevX=X
				PrevY=Y
				Counter:+1
			Next
		Next
		DrawLine PrevX,PrevY,Points[NumPoints-1].x,Points[NumPoints-1].y
		DrawText "Edges: "+String(Counter),0,10
		DrawText "Controls: "+String(NumPoints),0,20
		DrawText "Curves: "+String(NumPoints/3),0,30
		Flip
		If KeyHit(KEY_SPACE) Then DoAnother=True
		If KeyHit(KEY_LEFT) Then ControlPoint=Max(0,ControlPoint-1)
		If KeyHit(KEY_RIGHT) Then ControlPoint=Min(NumPoints-1,ControlPoint+1)
	Until KeyHit(KEY_ESCAPE) Or DoAnother=True
Until DoAnother=False
