; ID: 2598
; Author: Jesse
; Date: 2009-10-25 12:17:57
; Title: ARC
; Description: ARC, PIE, Circle Piece

SuperStrict
Function arc(x:Float, y:Float, startAngle:Float, endAngle:Float, radius:Float,closed:Int = False,pie:Int = False)
	Local fx:Float,fy:Float 'first x,y
	Local lx:Float,ly:Float 'last x,y
	Const RATE:Float = Pi/180.0
	If startAngle = endAngle Then Return
	If startAngle > endAngle
		Local ta:Float = endAngle
		endAngle = startAngle
		startAngle = ta
	EndIf
	Local angle:Float = endAngle - StartAngle
	If angle > 360.0 angle = 360.0
	
	Local Stp:Float = 1/(RATE * radius)
	Local AccumAngle:Float = StartAngle
	If closed = True
		fx = Cos(accumAngle) * radius
		fy = Sin(accumAngle) * radius
	EndIf
	While accumAngle < (StartAngle+Angle)
					
			lx:Float = Cos((accumAngle)) * radius
			ly:Float = Sin((accumAngle)) * radius
			Plot x + lx, y + ly
			AccumAngle :+ stp
	Wend

	If closed = True
		If pie = True
			DrawLine x, y, x + fx, y + fy,False
			DrawLine x, y, x + lx, y + ly,False
		Else
			DrawLine x+fx,y+fy,x+lx,y+ly,False
		EndIf
	EndIf
End Function 


Graphics 800,600

Repeat
	Cls
	arc(400,300,0,90,200,True,True)
	arc(380,280,90,360,200,True,True)
	Flip
Until KeyDown(key_escape)
