; ID: 2942
; Author: Jur
; Date: 2012-04-17 09:36:16
; Title: Akima spline
; Description: Make splines with akima interpolation method

SuperStrict

Graphics 800,600
SeedRnd(MilliSecs())	'Different each time
SetLineWidth(2)

Global SelectedPoint:Point
Global Points:Point[10]
MakePoints()

Function MakePoints()
	For Local p:Int=0 Until Points.length
		Points[p]=New Point
		Points[p].x=Rand(50,750)
		Points[p].y=Rand(100,550)
	Next
EndFunction

While Not KeyHit(KEY_ESCAPE)
	Cls
	
	For Local p:Int=0 Until Points.length
		If Abs(MouseX()-Points[p].x)<4 And Abs(MouseY()-Points[p].y)<4 Then
			If MouseDown(1) Then
				SelectedPoint = Points[p]
			EndIf
		EndIf	
	Next
	
	If SelectedPoint Then
		SelectedPoint.x=MouseX()
		SelectedPoint.y=MouseY()
		If MouseDown(1) = False Then 
			SelectedPoint = Null
			FlushMouse()
		EndIf
	EndIf
	
	If KeyHit(KEY_SPACE) Then MakePoints()

	Local xAprev:Float, yAprev:Float, xCprev:Float, yCprev:Float
	For Local i:Int=2 Until Points.length-3
		For Local mu:Float=0 To 1 Step 0.05
			'DebugStop()

			Local xC:Float = CubicInterpolate(Points[i-1].x, Points[i].x, Points[i+1].x, Points[i+2].x, mu)
			Local yC:Float = CubicInterpolate(Points[i-1].y, Points[i].y, Points[i+1].y, Points[i+2].y, mu)
			
			Local yA:Float = AkimaInterpolate(Points[i-2].y, Points[i-1].y, Points[i].y, Points[i+1].y, Points[i+2].y, Points[i+3].y, mu)
			Local xA:Float = AkimaInterpolate(Points[i-2].x, Points[i-1].x, Points[i].x, Points[i+1].x, Points[i+2].x, Points[i+3].x, mu)

			If xAprev=0 And yAprev=0 Then
				xAprev = xA
				yAprev = yA
			EndIf
			If xCprev=0 And yCprev=0 Then
				xCprev = xC
				yCprev = yC
			EndIf
			
			SetColor 220,220,200
			DrawLine xCprev,yCprev, xC,yC
			
			SetColor 255,200,0
			DrawLine xAprev,yAprev, xA,yA

			xAprev = xA
			yAprev = yA
			xCprev = xC
			yCprev = yC

		Next
	Next
	
	
	'points
	SetColor 255,255,255
	For Local i:Int=2 Until Points.length-3
		DrawOval(Points[i].x-2, Points[i].y-2, 4,4)
		DrawText i,Points[i].x+7,Points[i].y+7
	Next
	
	SetColor 220,220,200
	DrawText "cubic spline",10,10
	SetColor 255,200,0
	DrawText "akima spline",10,30
	SetColor 200,200,200
	DrawText "move points with mouse, make new points with <space> ",10,50
	
	Flip
Wend


' akima interpolation requires 6 points 
' interpolate between y3 And y4 at position mu (0..1)
'----------------------------------------------------------------------------
Function AkimaInterpolate:Float(y1:Float, y2:Float, y3:Float, y4:Float, y5:Float, y6:Float, mu:Float)

	Local num1:Float = Abs((y5-y4)- (y4-y3))*(y3-y2) + Abs((y3-y2) - (y2-y1))*(y4-y3)
	Local den1:Float = Abs((y5-y4) - (y4-y3)) + Abs((y3-y2) - (y2-y1))
	
	Local num2:Float = Abs((y6-y5)- (y5-y4))*(y4-y3) + Abs((y4-y3) - (y3-y2))*(y5-y4)
	Local den2:Float = Abs((y6-y5) - (y5-y4)) + Abs((y4-y3) - (y3-y2))
	
	Local t1:Float
	If den1>0.00001 Then   '0
		t1=num1/den1
	Else
		t1=0.0
	EndIf
	
	Local t2:Float
	If den2>0.00001 Then   '0
		t2=num2/den2
	Else
		t2=0.0
	EndIf
	
	Local C:Float = (3*(y4-y3) - 2*t1 - t2)
	Local D:Float = (t1 + t2 - 2*(y4-y3))
	
	Return  y3 + (t1 + (C + D*mu)*mu)*mu

EndFunction




'interpolate between y1 and y2
Function CubicInterpolate:Float(y0:Float, y1:Float, y2:Float, y3:Float, mu:Float)
   Local a0#,a1#,a2#,a3#,mu2#

   mu2 = mu*mu
   a0 = y3-y2-y0+y1
   a1 = y0-y1-a0
   a2 = y2-y0
   a3 = y1
   
   Return a0*mu*mu2+a1*mu2+a2*mu+a3
End Function

Type Point
	Field x:Int
	Field y:Int
	
	Function Create:point(x#,y#)
		Local p:point=New point
		p.x=x
		p.y=y
		Return p
	End Function
End Type
