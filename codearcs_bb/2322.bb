; ID: 2322
; Author: Nate the Great
; Date: 2008-09-26 03:55:10
; Title: Movemouse
; Description: Makes mouse bonce around

Graphics 400,400,0,2
AppTitle "Mousefun  :)"

Global mx# = 200
Global my# = 200
Global mvx# = 0
Global mvy# = 0
Global red = 255
Global green = 255
Global blue = 0


Type sparks
	Field x#,y#,dx#,dy#,lif,r,g,b
End Type


SetBuffer BackBuffer()

While Not KeyDown(1)
Cls

mvx# = mvx# + (MouseXSpeed()/7)
mvy# = mvy# + (MouseYSpeed()/7)

mvy# = mvy# + .1


If my# > 400 Then
	mvy# = -mvy#*.7
	my# = 400
EndIf

If my# < 0 Then
	mvy# = -mvy#*.9
	my# = 0
EndIf
;
If mx# > 400 Then
	mvx# = -mvx#*.9
	mx# = 400
EndIf

If my# > 397 Then
	mvx# = mvx#*.7
EndIf

;
If mx# < 0 Then
	mvx# = -mvx#*.9
	mx# = 0
EndIf
;
;If mvy# < 2 Then mvy# = 0
;
;
mx# = mx# + mvx#
my# = my# + mvy#

MoveMouse mx,my
updatesparks()


Flip

Wend

End



Function updatesparks()

For a = 1 To 4
	s.sparks = New sparks
	s\x# = MouseX()
	s\y# = MouseY()
	s\dx# = Rnd#(-.3,.3)
	s\dy# = Rnd#(-.3,.3)
	s\r = red
	s\g = green
	s\b = blue
	s\lif = 255
Next

For s.sparks = Each sparks
	Color s\r*(s\lif/255.0),s\g*(s\lif/255.0),s\b*(s\lif/255.0)
	Plot s\x#,s\y#
	s\x# = s\x# + s\dx#
	s\y# = s\y# + s\dy#
	
	s\lif = s\lif - 2
	If s\lif <= -1 Then Delete s.sparks
Next

End Function
