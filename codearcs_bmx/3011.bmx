; ID: 3011
; Author: TAS
; Date: 2012-12-13 22:50:17
; Title: Catmull perimeter
; Description: Uses Catmull splines to draw a closed shape

'Catmull perimeter
'By Thomas Stevenson
'war-game-programming.com
'Adapted from code by ImaginaryHuman which was
'Adapted from code by Warpy and Matt McFarland

Type Point
	Field x:Int
	Field y:Int
End Type

Const Accuracy:Double=0.05		'Lower has more line segments
	
SeedRnd(MilliSecs())	'Different each time
SetGraphicsDriver GLMax2DDriver()
Graphics 800,600,0
SetBlend LIGHTBLEND
Local ControlPoint:Int
Local Counter:Int	
glEnable(GL_LINE_SMOOTH)	'Quick antaliasing hack
glHint(GL_LINE_SMOOTH_HINT,GL_NICEST)
glLineWidth(3.0)

Local NumPoints=Rand(4,30)	
Local Points:Point[]=Catmull_Create(NumPoints)

Repeat
	Cls
	If MouseDown(1)
		'check for point drag
		ControlPoint=Catmull_Find(Points)
		If ControlPoint
			'Catmull_Find() returns NumPoints for point 0
			ControlPoint=ControlPoint Mod NumPoints
			'if non 0 then mouse is near (+/-7) of a control point
			Points[ControlPoint].x=MouseX()
			Points[ControlPoint].y=MouseY()
		EndIf
	EndIf

	'Draw segments
	Counter=Catmull_Draw(Points,$00FFFFFF)
	'Info text
	DrawText "Control Points: "+String(NumPoints),10,5
	DrawText "Segiments (cps/0.05): "+String(Counter),10,20
	DrawText "Curves (=cps): "+String(NumPoints),10,35
	
	DrawText "Spacebar: New random shape",550,10
	DrawText "Escape:  Exit",550,30
	DrawText "Left Mouse: Drag points",550,50
	Catmull_Info(Points,60)
	Flip
	
	If KeyHit(KEY_SPACE) 
		NumPoints=Rand(7,30)
		Points=Catmull_Create(NumPoints)	'returns array of point types
	EndIf	
	If KeyHit(KEY_ESCAPE) Then Exit
	If AppTerminate() Then Exit
Forever 
End

Function Catmull_Draw(p:Point[],clr)
		Local PrevX:Double,PrevY:Double
		Local bytes:Byte Ptr = Varptr clr
		SetColor bytes[2],bytes[1],bytes[0]
		Local CM_Counter=0
		'Accuracy = constant set by Main
		a:Double=0.5
		For i=1 To p.length
			'calc indexs of four control points for curve from i to i+1
			'wrap index if Sj>=p.length
			s0=(i-1); s1=(i+0) Mod p.length;	s2=(i+1) Mod p.length;	s3=(i+2) Mod p.length
			For T:Double=0 To 1 Step Accuracy
				x:Double=a*( (2*p[S1].x)+(p[S2].x-p[S0].x)*T..
				+(2*p[S0].x-5*p[S1].x+4*p[S2].x-p[S3].x)*T*T..
				+(3*p[S1].x-p[S0].x-3*p[S2].x+p[S3].x) *T*T*T)
				Y:Double=a*( (2*p[S1].y)+(p[S2].y-p[S0].y)*T +(2*p[S0].y-5*p[S1].y+4*p[S2].y-p[S3].y)*T*T +(3*p[S1].y-p[S0].y-3*p[S2].y+p[S3].y) *T*T*T)
				If PrevX=0 And PrevY=0
					PrevX=X; PrevY=Y
				EndIf
				DrawLine PrevX,PrevY,X,Y,False
				PrevX=X; PrevY=Y
				CM_Counter:+1
			Next
		Next
		Return CM_Counter
End Function

Function Catmull_Create:Point[](np,r1=80,r2=230)
	Local Pts:Point[]=New Point[np]
	For p:Int=0 To np-1
		Local deg:Int=P*(360/np) Mod 360
		Pts[p]=New Point
		Local h=Rand(r1,r2)
		Pts[p].x=400+h*Cos(deg)
		Pts[p].y=300+h*Sin(deg)
	Next
	Return Pts
End Function

Function Catmull_Info(p:Point[],y)
	'Draw controls excluding duplicates
	For i=0 To p.length-1
		SetColor $88,$88,$88	'mark the point
		DrawLine p[i].x-7,p[i].y-7,p[i].x+7,p[i].y+7
		DrawLine p[i].x-7,p[i].y+7,p[i].x+7,p[i].y-7
		SetColor 255,0,0	'point number
		DrawText i,p[i].x+5,p[i].y+5
		SetColor 255,255,255	'xy info
		DrawText RSet(i,3)+RSet(P[i].x,6)+RSet(P[i].y,6),10,y+i*15
	Next
End Function

Function Catmull_Find(p:Point[])
	'Point 0= Point n 
	If InsideRect(MouseX(),MouseY(),p[0].x-7,p[0].y-7,14,14) Then Return p.length
	For i=1 To p.length-1
		If InsideRect(MouseX(),MouseY(),p[i].x-7,p[i].y-7,14,14) Then Return i
	Next
	'Not found
	Return 0
End Function

Function InsideRect(x,y,x2,y2,w,h)
	If x<x2 Then Return False	
	If x>(x2+w) Then Return False	
	If y<y2 Then Return False	
	If y>(y2+h) Then Return False	
	Return True
End Function
