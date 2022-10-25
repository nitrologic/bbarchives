; ID: 1551
; Author: deps
; Date: 2005-12-01 20:02:11
; Title: The little ant that could
; Description: Simple rules, interesting pattern

Const sw:Int = 300
Const sh:Int = 300

Local map:Int[sw,sh]
Local x,y,dir, generation, update

Graphics 640,480,0

glMatrixMode(GL_PROJECTION)
glLoadIdentity()
glortho( 0,sw, sh,0, 0,1 )
glMatrixMode(GL_MODELVIEW)
glLoadIdentity()

x = sw/2
y = sh/2
update = MilliSecs()

While Not KeyHit( KEY_ESCAPE )

	If Not map[x,y] Then
		map[x,y] = 1
		dir:+1
		If dir > 3 Then dir = 0
	Else
		map[x,y] = 0
		dir:-1
		If dir < 0 Then dir = 3
	EndIf
	
	Select dir
		Case 0
			y:-1
		Case 1
			x:+1
		Case 2
			y:+1
		Case 3
			x:-1
	EndSelect
	
	If x < 0 Then x = sw-1
	If x >= sw Then x = 0
	If y < 0 Then y = sh-1
	If y >= sh Then y = 0

	If MilliSecs() > update
		Cls
			For Local u:Int = 0 Until sw
			For Local v:Int = 0 Until sh
				If map[u,v] Then plot2 u,v
			Next
			Next
		
			SetColor 255,0,0
			DrawText generation ,0,0
			SetColor 255,255,255
		
		Flip 0
		update = MilliSecs()+2
	EndIf
	
	generation:+1
Wend
End

Function plot2( x:Int, y:Int )
	DrawRect x,y, 1,1
EndFunction
