; ID: 600
; Author: Rob
; Date: 2003-02-24 12:17:34
; Title: Simplest bullet code in the world
; Description: fire bullets and keep track of them in the most painless manner possible. Ideal for the beginner.

;very simple bullet shooter code (rob@redflame.net)

Graphics 640,480,16,2
SetBuffer BackBuffer()
HidePointer()

; for the bullets
Type bullet
	Field x,y
End Type

playerx=320
playery=240

While Not KeyHit(1)
	Cls

	playerx=MouseX()
	playery=MouseY()
	
	If MouseHit(1) Then fire_bullet(playerx,playery) ; needs an x and y pos to start from
	
	Oval playerx,playery,8,8 ; draw the player
	
	update_all_bullets() ; process bullets that have been created with fire_bullet

	Flip
Wend
End

Function fire_bullet(x,y)
	b.bullet=New bullet
	b\x=x
	b\y=y
End Function

Function update_all_bullets()
	For b.bullet=Each bullet
		If b\x>640
			Delete b
		Else
			b\x=b\x+4
			Plot b\x,b\y
		EndIf
	Next
End Function
