; ID: 2527
; Author: ImaginaryHuman
; Date: 2009-07-11 19:10:33
; Title: Quadratic and Cubic Bezier Curves
; Description: Calculate and draw beziers using 3 and 4 control points

Strict

Local NumberOfSegments:Int=50

SetGraphicsDriver GLMax2DDriver()
Graphics 800,600
glEnable(GL_LINE_SMOOTH)
glHint(GL_LINE_SMOOTH_HINT,GL_NICEST)
glLineWidth(2.0)
SetBlend LIGHTBLEND
'SetClsColor $FF,$FF,$FF

Local PX:Float[5]
Local PY:Float[5]
PX[0]=50
PY[0]=550
PX[1]=225
PY[1]=50
PX[2]=370
PY[2]=300
PX[3]=525
PY[3]=575
PX[4]=750
PY[4]=50

Local Position:Float
Local X:Float
Local Y:Float
Local X2:Float
Local Y2:Float
Local Controlling:Int=False
Local ControlPoint:Int=0
Local MX:Int
Local MY:Int
Local Point:Int
Local SegmentSize:Float=1.0/NumberOfSegments
Repeat
	Cls
	
	'Control it
	MX=MouseX()
	MY=MouseY()
	If Controlling=False
		If MouseDown(1)
			For Point=0 To 4
				If MX>=PX[Point]-15 And MY>=PY[Point]-15 And MX<=PX[Point]+15 And MY<=PY[Point]+15 And Point<>2
					'Control the point so long as it's not the middle shared point
					Controlling=True
					ControlPoint=Point
				EndIf
			Next
		Else
			Controlling=False
		EndIf
	EndIf	
	If Controlling=True
		SetColor $0,$0,$0
		DrawText MX+","+MY,0,32
		PX[ControlPoint]=MX
		PY[ControlPoint]=MY
		If Not MouseDown(1) Then Controlling=False
	EndIf

	'Force continuity - calculate the middle shared point automatically
	PX[2]=PX[1]+((PX[3]-PX[1])*0.5)
	PY[2]=PY[1]+((PY[3]-PY[1])*0.5)

	'Draw stuff
	SetColor $88,$88,$88
	DrawLine PX[0],PY[0],PX[1],PY[1]	'Draw lines
	DrawLine PX[1],PY[1],PX[2],PY[2]
	DrawLine PX[2],PY[2],PX[3],PY[3]
	DrawLine PX[3],PY[3],PX[4],PY[4]
	SetColor $FF,$0,$0
	DrawOval PX[0]-5,PY[0]-5,10,10	'Draw control points
	DrawOval PX[1]-5,PY[1]-5,10,10
	'DrawOval PX[2]-5,PY[2]-5,10,10	'Don't draw middle shared point
	DrawOval PX[3]-5,PY[3]-5,10,10
	DrawOval PX[4]-5,PY[4]-5,10,10
	SetColor 121,195,3
	
	'Draw curve 1 quadratic
	X=PX[0]
	Y=PY[0]
	Position=SegmentSize
	While Position<=1.00001	'Go to 1.00001 to make sure final line is drawn
		Curve3(PX[0],PY[0],PX[1],PY[1],PX[2],PY[2],Position,X2,Y2)
		DrawLine X,Y,X2,Y2
		X=X2
		Y=Y2
		Position:+SegmentSize
	Wend
	
	'Draw curve 2 quadratic
	X=PX[2]
	Y=PY[2]
	Position=SegmentSize
	While Position<=1.00001	'Go to 1.00001 to make sure final line is drawn
		Curve3(PX[2],PY[2],PX[3],PY[3],PX[4],PY[4],Position,X2,Y2)
		DrawLine X,Y,X2,Y2
		X=X2
		Y=Y2
		Position:+SegmentSize
	Wend

	'Draw total curve as cubic bezier
	SetColor $FF,$00,$00
	X=PX[0]
	Y=PY[0]
	Position=SegmentSize
	While Position<=1.00001	'Go to 1.00001 to make sure final line is drawn
		Curve4(PX[0],PY[0],PX[1],PY[1],PX[3],PY[3],PX[4],PY[4],Position,X2,Y2)
		DrawLine X,Y,X2,Y2
		X=X2
		Y=Y2
		Position:+SegmentSize
	Wend

	Flip 1
Until KeyHit(KEY_ESCAPE) Or AppTerminate()

Function Curve3(P1X:Float,P1Y:Float,P2X:Float,P2Y:Float,P3X:Float,P3Y:Float,Position:Float,PointX:Float Var,PointY:Float Var)
	'Calculate a point on a bezier curve with 3 control points (quadratic) using floating point math
	'Requires three control points with X and Y coordinates and a current position on the curve in the range 0..1
	'Coordinates are returned in PointX and PointY variables

	'Calculate position along each line between pairs of control points P1-to-P2 and P2-to-P3
	P1X:+((P2X-P1X)*Position)					'P1 to P2 X, scaled, absolute
	P1Y:+((P2Y-P1Y)*Position)					'P1 to P2 Y, scaled, absolute
	P2X:+((P3X-P2X)*Position)					'P2 to P3 X, scaled, absolute
	P2Y:+((P3Y-P2Y)*Position)					'P2 to P3 Y, scaled absolute

	'Calculate position along final line between P12 and P23
	PointX=((P2X-P1X)*Position)+P1X			'P12 to P23 X, scaled, absolute
	PointY=((P2Y-P1Y)*Position)+P1Y			'P12 to P23 Y, scaled, absolute
End Function

Function Curve4(P1X:Float,P1Y:Float,P2X:Float,P2Y:Float,P3X:Float,P3Y:Float,P4X:Float,P4Y:Float,Position:Float,PointX:Float Var,PointY:Float Var)
	'Calculate a point on a bezier curve with 4 control points (cubic) using floating point math
	'Requires four control points with X and Y coordinates and a current position on the curve in the range 0..1
	'Coordinates are returned in PointX and PointY variables

	'Calculate position along each line between pairs of control points P1-to-P2, P2-to-P3, P3-to-P4
	P1X:+((P2X-P1X)*Position)					'P1 to P2 X, scaled, absolute
	P1Y:+((P2Y-P1Y)*Position)					'P1 to P2 Y, scaled, absolute
	P2X:+((P3X-P2X)*Position)					'P2 to P3 X, scaled, absolute
	P2Y:+((P3Y-P2Y)*Position)					'P2 to P3 Y, scaled, absolute
	P3X:+((P4X-P3X)*Position)					'P3 to P4 X, scaled, absolute
	P3Y:+((P4Y-P3Y)*Position)					'P3 to P4 Y, scaled, absolute

	'Calculate position along intermediary lines between P12-to-P23 and P23-to-P34
	P1X:+((P2X-P1X)*Position)					'P12 to P23 X, scaled, absolute
	P1Y:+((P2Y-P1Y)*Position)					'P12 to P23 Y, scaled, absolute
	P2X:+((P3X-P2X)*Position)					'P23 to P34 X, scaled, absolute
	P2Y:+((P3Y-P2Y)*Position)					'P23 to P34 Y, scaled, absolute

	'Calculate position along final lines between P123 and P234
	PointX=((P2X-P1X)*Position)+P1X			'P123 to P234 X, scaled, absolute
	PointY=((P2Y-P1Y)*Position)+P1Y			'P123 to P234 Y, scaled, absolute
End Function
