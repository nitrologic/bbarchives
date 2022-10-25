; ID: 1465
; Author: ImaginaryHuman
; Date: 2005-09-17 17:47:58
; Title: Besenham's LineDraw routine (integer math only)
; Description: Draw lines pixel-by-pixel with integer math

'Bresenham linedraw in BlitzMax, adapted from C code

Strict
Graphics 640,480,0
Repeat
	Cls
	Line(320,240,MouseX(),MouseY())
	Flip
Until KeyHit(KEY_ESCAPE)
End

Function Line(X1:Int,Y1:Int,X2:Int,Y2:Int)
	'Draws a line of individual pixels from X1,Y1 to X2,Y2 at any angle
 	Local Steep:Int=Abs(Y2-Y1) > Abs(X2-X1)			'Boolean
	If Steep
		Local Temp:Int=X1; X1=Y1; Y1=Temp		'Swap X1,Y1
		Temp=X2; X2=Y2; Y2=Temp		'Swap X2,Y2
	EndIf
	Local DeltaX:Int=Abs(X2-X1)		'X Difference
	Local DeltaY:Int=Abs(Y2-Y1)		'Y Difference
	Local Error:Int=0		'Overflow counter
	Local DeltaError:Int=DeltaY		'Counter adder
	Local X:Int=X1		'Start at X1,Y1
	Local Y:Int=Y1		
	Local XStep:Int
	Local YStep:Int
	If X1<X2 Then XStep=1 Else XStep=-1	'Direction
	If Y1<Y2 Then YStep=1 Else YStep=-1	'Direction
	If Steep Then Plot(Y,X) Else Plot(X,Y)		'Draw
	While X<>X2
		X:+XStep		'Move in X
		Error:+DeltaError		'Add to counter
		If (Error Shl 1)>DeltaX		'Would it overflow?
			Y:+YStep		'Move in Y
			Error=Error-DeltaX		'Overflow/wrap the counter
		EndIf
		If Steep Then Plot(Y,X) Else Plot(X,Y)		'Draw
	Wend
End Function
