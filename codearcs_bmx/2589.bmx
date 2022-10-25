; ID: 2589
; Author: Matt McFarland
; Date: 2009-09-23 15:16:29
; Title: Draw a Circle
; Description: One point at a time!

'Simple Circles with Max2D
SuperStrict

'Turn the graphics on
Graphics 640,480

'Declare Variables
Local x:Float
Local y:Float
Local originx:Float = 640/2
Local originy:Float = 480/2
Local degree:Float 
Local radius:Float = 64
Local toggle:Byte = True

'Main loop
Repeat
	Cls
	DrawText("originx:"+String(originx),0,0)
	DrawText("originy:"+String(originy),0,12)
	DrawText("radius:"+String(radius),0,24)
	DrawText("Use arrow keys and +/- to modify",0,36)	
	DrawText("Use [SPACE] to toggle",0,48)
	
	'Draw the whole circle
	If Toggle=True
		For degree = 0 To 360 Step .5	
			x = originx +Cos(degree) * radius
			Y = originy +Sin(degree) * radius
			Plot x,y
		Next
	'Show the plot of the circle
	Else
		x = originx +Cos(degree) * radius
		Y = originy +Sin(degree) * radius
		Plot x,y
		degree:+5
	End If

	'User Interface
	If KeyDown(key_left) originx:-1
	If KeyDown(key_right) originx:+1
	If KeyDown(key_up) originy:-1
	If KeyDown(key_down) originy:+1
	If KeyDown(key_numadd) radius :+.5
	If KeyDown(key_numsubtract) radius:-.5
	If KeyHit(Key_space) toggle:+1
	If toggle > 1 toggle = 0
	Flip
Until KeyHit(key_escape)
