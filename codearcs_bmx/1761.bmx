; ID: 1761
; Author: ImaginaryHuman
; Date: 2006-07-23 16:55:45
; Title: Catmull Rom Splines
; Description: Adaptation of Warpy's Catmull Rom Splines

'Catmull rom splines
'Adapted from code by Warpy and Matt McFarland

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
Const Accuracy:Double=0.05		'Lower has more line segments
	
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
	Local NumPoints:Int=Rand(4,10)	'Whatever you like >3
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
		For Local S:Int=0 To NumPoints-4
			For Local T:Double=0 To 1 Step Accuracy
				Local X:Double=.5:Double*((2*Points[S+1].x)+(Points[S+2].x-Points[S].x)*T+(2*Points[S].x-5*Points[S+1].x+4*Points[S+2].x-Points[S+3].x)*T*T+(3*Points[S+1].x-Points[S].x-3*Points[S+2].x+Points[S+3].x)*T*T*T)
				Local Y:Double=.5:Double*((2*Points[S+1].y)+(Points[S+2].y-Points[S].y)*T+(2*Points[S].y-5*Points[S+1].y+4*Points[S+2].y-Points[S+3].y)*T*T+(3*Points[S+1].y-Points[S].y-3*Points[S+2].y+Points[S+3].y)*T*T*T)
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
		DrawLine PrevX,PrevY,Points[NumPoints-2].x,Points[NumPoints-2].y
		DrawText "Edges: "+String(Counter),0,10
		DrawText "Controls: "+String(NumPoints),0,20
		DrawText "Curves: "+String(NumPoints-3),0,30
		Flip
		If KeyHit(KEY_SPACE) Then DoAnother=True
		If KeyHit(KEY_LEFT) Then ControlPoint=Max(0,ControlPoint-1)
		If KeyHit(KEY_RIGHT) Then ControlPoint=Min(NumPoints-1,ControlPoint+1)
	Until KeyHit(KEY_ESCAPE) Or DoAnother=True
Until DoAnother=False
