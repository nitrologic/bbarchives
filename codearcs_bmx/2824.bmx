; ID: 2824
; Author: Warpy
; Date: 2011-02-15 10:36:19
; Title: Gradient of several colours
; Description: Calculate colours on a gradient which cycles through several arbitrary colours

Graphics 600,600,0

Local colors[][] = [[0,255,0],[255,0,0],[127,127,127],[255,255,255]]
Local grades#[] = [0.0, 1.0/3, 2.0/3, 1.0]

While Not (KeyHit(KEY_ESCAPE) Or AppTerminate())

	'draw entire gradient
	For x=0 To 600
		t#=x/600.0
		calcColor t,colors,grades
		DrawRect x,400,1,50
		calcColor2 t
		DrawRect x,450,1,50
	Next

	'get colour of gradient at mouse position
	t#=MouseX()/600.0
	calcColor t,colors,grades
	
	DrawRect 250,150,100,100
	
	DrawLine 0,400,600,400
	DrawRect MouseX()-5,390,10,20
	
	Flip
	Cls

Wend

'calculate colour corresponding to a position on a multi-part gradient
'colors is an array of [r,g,b] arrays
'grades is an array of points in the interval [0..1] to match the colours to
Function calcColor(t#,colors[][],grades#[])
	i=1
	While t>grades[i]
		i:+1
	Wend
	
	t=(t-grades[i-1])/(grades[i]-grades[i-1])
	
	red# = (1-t)*colors[i-1][0]+t*colors[i][0]
	green# = (1-t)*colors[i-1][1]+t*colors[i][1]
	blue# = (1-t)*colors[i-1][2]+t*colors[i][2]
	
	SetColor red,green,blue
EndFunction

'alternative method, with no arrays
'but harder to change
Function calcColor2(t#)
	red = (t<1.0/3)*(t*3)*255 + (t>=1.0/3 And t<2.0/3)*(255*(1-(t-1.0/3)*3)+127*(t-1.0/3)*3) + (t>=2.0/3)*(127*(1-(t-2.0/3)*3)+255*(t-2.0/3)*3)
	green = (t<1.0/3)*(1-t*3)*255 + (t>=1.0/3 And t<2.0/3)*(127*(t-1.0/3)*3) + (t>=2.0/3)*(127*(1-(t-2.0/3)*3)+255*(t-2.0/3)*3)
	blue = (t>=1.0/3 And t<2.0/3)*(127*(t-1.0/3)*3) + (t>=2.0/3)*(127*(1-(t-2.0/3)*3)+255*(t-2.0/3)*3)
	SetColor red,green,blue
End Function
