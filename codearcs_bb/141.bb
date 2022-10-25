; ID: 141
; Author: Rob
; Date: 2001-11-18 19:58:55
; Title: 2D bullet code for the beginner
; Description: 2D bullet code for the beginner uses types

;simple 2D missile/bullet code
;use cursor keys and spacebar

;our missile type
Type missiletype
	Field x,y
End Type
Global missile.missiletype ; missile type 


Graphics 640,480,16,2
SetBuffer BackBuffer()

While Not KeyHit(1)
	Cls
	; move player ship
	If KeyDown(203) ; left
		x=x-2
	ElseIf KeyDown(205) ; right
		x=x+2
	EndIf
	
	If KeyDown(200) ; up
		y=y-2
	ElseIf KeyDown(208) ; down
		y=y+2
	EndIf

	If KeyHit(57) SpawnMissile(x,y)  ; missile spawn (spacebar)
	
	;draw player
	Oval x,y,16,16
	
	;update and draw missiles
	UpdateMissiles()
	
	
	Flip
Wend
End


Function SpawnMissile(missx,missy)
	missile.missiletype=New missiletype
	missile\x=missx
	missile\y=missy
End Function

Function UpdateMissiles()
	For missile.missiletype=Each missiletype
		;whatever suits you here!
		;ie...
		If missile\x>640    ; or a collision...! you decide
			Delete missile
		Else
			missile\x=missile\x+10
			Rect missile\x,missile\y,2,2
		EndIf
	Next
End Function
