; ID: 1156
; Author: Curtastic
; Date: 2004-09-04 01:02:12
; Title: Mouse Include
; Description: Handles the mouse. mouse\doubleclick, mouse\button\released...

;do not use Mouse_Speed(). Use mouse\button\speed
;do not use MouseHit(). Use mouse\button\hit
;if mouse\crontrolled then
	;do not use MouseX() Or MouseY(). Use mouse\x and mouse\y
	;do not use MoveMouse(). use mouse\x=


Type Mouse
	;button left
	Field bl.mousebutton
	;button right
	Field br.mousebutton
	;button middle
	Field bm.mousebutton
	;the position of the mouse
	Field x,y
	;stores mousexspeed(),mouseyspeed()
	Field speedx,speedy,speedz
	;this tells if the mouse pointer is hidden and kept at the center and you draw your own pointer
	Field controlled
	;how many millisecs are allowed for a double click
	Field hitdoublespeed
End Type
Global mouse.mouse=New mouse

mouse\hitdoublespeed=300


mouse\bl=New mousebutton
mouse\bl\index=1
mouse\br=New mousebutton
mouse\br\index=2
mouse\bm=New mousebutton
mouse\bm\index=3


Type MouseButton
	;what number blitz uses for this button
	Field index
	
	;where the mouse was when the click started
	Field startx,starty
	;if the button whas hit this loop
	Field hit
	;tells if the button was let go this loop
	Field released
	;tells if the button is down this loop
	Field down
	;tells if the button was doubleclicked this loop
	Field hitdouble
	;when it was let go
	Field releasedtime
	;internal: makes sure no clicks were missed
	Field hitcheck
End Type










;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
Function MouseUpdate()
	Local b.mousebutton
	

	mouse\speedz=MouseZSpeed()
	mouse\speedx=MouseXSpeed()
	mouse\speedy=MouseYSpeed()
	If mouse\controlled Then
		mouse\x=mouse\x+mouse\speedx
		mouse\y=mouse\y+mouse\speedy
		MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
	
		;done after movemouse to fix mouse problems on some computers
		MouseXSpeed()
		MouseYSpeed()
	
		mouse\x=keepin(mouse\x,0,GraphicsWidth()-1)
		mouse\y=keepin(mouse\y,0,GraphicsHeight()-1)

	Else
		mouse\x=MouseX()
		mouse\y=MouseY()
	EndIf


	For b=Each mousebutton
		b\released=0
		b\hitdouble=0

		b\hit=MouseHit(b\index)>0
		b\down=MouseDown(b\index)

		If b\hitcheck=0 Then b\hitcheck=b\hit
		If b\hitcheck=1 And b\down=0 Then
			b\released=1
			b\hitcheck=0
		EndIf
	
		If b\hit=1 Then
			b\startx=mouse\x
			b\starty=mouse\y

			If MilliSecs()-b\releasedtime<mouse\hitdoublespeed Then
				b\hitdouble=1
			EndIf
			b\releasedtime=MilliSecs()
		EndIf
	Next

End Function


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;pass Null if you want to flush all buttons
Function MouseFlush(mb.mousebutton,speeds=1)
	Local mb2.mousebutton


	For mb2=Each mousebutton
		If mb2=mb Or mb=Null Then

			mb2\hit=0
			mb2\startx=0
			mb2\starty=0
			MouseHit(mb2\index)
			mb2\hitcheck=0

		EndIf
	Next

	If speeds=1 Then
		MouseZSpeed()
		MouseXSpeed()
		MouseYSpeed()
		mouse\speedz=0
		mouse\speedx=0
		mouse\speedy=0
	EndIf
End Function



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;keeps num in bounds
;returns the same number if its already in bounds.
Function KeepIn#(num#,min#,max#)
	If num<min Then 
		Return min
	ElseIf num>max
		Return max
	EndIf
	Return num
End Function
