; ID: 3203
; Author: Silver_Knee
; Date: 2015-04-30 05:08:58
; Title: Pie Chart
; Description: Draw a pie chart filled with different colors

Function DrawPieChart(x:Float, y:Float, pieces:TPieChartPiece[], radius:Float)
	Local lastAngle:Float=0
	For Local p:TPieChartPiece=EachIn pieces
		SetColor p.r,p.g,p.b
		For Local dy:Float= -radius To +radius Step 1
			'right half - cut to right half with Max here
			Local circleAngle:Float=ACos(dy/radius) 'min 0 / max 180

			If circleAngle<90
				'bottom
				If p.angle>circleAngle And circleAngle>lastAngle
					DrawLine x+Sin(circleAngle)*radius,y+dy,x+Tan(lastAngle)*dy,y+dy
				ElseIf p.angle<=circleAngle
					DrawLine x+Tan(p.angle)*dy,y+dy,x+Tan(lastAngle)*dy,y+dy			
				EndIf
			Else
				'top
				Local dx:Int
				If p.angle<=180
					dx=Tan(p.angle)*dy
				Else
					dx=0
				EndIf
				
				If p.angle>circleAngle And circleAngle>lastAngle
					DrawLine x+dx,y+dy,x+Sin(circleAngle)*radius,y+dy
				ElseIf p.angle>=circleAngle And lastAngle<=180
					DrawLine x+dx,y+dy,x+Tan(lastAngle)*dy,y+dy
				EndIf
			EndIf
			
			'left half - cut to left half with Min here
			circleAngle=360-circleAngle

			If circleAngle>270
				'bottom
				If p.angle>circleAngle And circleAngle>lastAngle
					DrawLine Min(x,x-Sin(360-circleAngle)*radius),y+dy,Min(x,x-Tan(360-p.angle)*dy),y+dy
				ElseIf p.angle>=circleAngle
					DrawLine Min(x,x-Tan(360-p.angle)*dy),y+dy,Min(x,x-Tan(360-lastAngle)*dy),y+dy
				EndIf
			Else
				'top 
				Local dx:Int
				If lastAngle>=180
					dx=-Tan(360-lastAngle)*dy
				Else
					dx=0
				EndIf
				
				If p.angle>circleAngle And circleAngle>lastAngle
					DrawLine x-Sin(360-circleAngle)*radius,y+dy,x+dx,y+dy
				ElseIf p.angle<=circleAngle And p.angle>180
					DrawLine x+Tan(p.angle)*dy,y+dy,x+dx,y+dy
				EndIf			
			EndIf
		Next 
		DrawLine x,y,x+Sin(p.angle)*radius,y+Cos(p.angle)*radius
		DrawLine x,y,x+Sin(lastAngle)*radius,y+Cos(lastAngle)*radius
		lastAngle=p.angle
	Next
End Function 

Type TPieChartPiece
	Function Create:TPieChartPiece(angle:Float,r:Int,g:Int,b:Int)
		Local this:TPieChartPiece=New TPieChartPiece
		this.angle=angle
		this.r=r
		this.g=g
		this.b=b
		Return this
	End Function
	
	Field angle:Float
	Field r:Int,g:Int,b:Int
End Type
