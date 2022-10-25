; ID: 397
; Author: Miracle
; Date: 2002-08-19 12:16:59
; Title: Linked Type Lists (Updated)
; Description: Search through large numbers of types with linked lists

Type ship
	Field x
	Field y
	Field pv.ship			; This will point to the previous ship in the linked list
	Field nx.ship			; And this points to the next one
End Type

Type destroyer
	Field lastship.ship		; Points to the last ship in the string ...
	Field firstship.ship	; ... and the first one.
End Type

Type fighter
	Field lastship.ship
	Field firstship.ship
End Type

Global destroyer.destroyer = New destroyer
Global fighter.fighter = New fighter

; Let's make some destroyers
For x = 1 To 100
	d.ship = New ship
	d\pv = destroyer\lastship
	If destroyer\lastship <> Null Then destroyer\lastship\nx = d Else destroyer\firstship = d
	destroyer\lastship = d
Next

; Now we need fighters
For x = 1 To 250
	f.ship = New ship
	f\pv = fighter\lastship
	If fighter\lastship <> Null Then fighter\lastship\nx = f Else fighter\firstship = f
	fighter\lastship = f
Next

; Move all the fighters one pixel to the left, in reverse order
scratch.ship = fighter\lastship
Repeat
	scratch\x = scratch\x - 1
	scratch = scratch\pv
Until scratch = Null

; Draw all the destroyers, in forward order
scratch.ship = destroyer\firstship
Repeat
	DrawImage destroyerimage,scratch\x,scratch\y
	scratch = scratch\nx
Until scratch = Null

; Insert a fighter after the first one in the list
f.ship = New ship
f\nx = fighter\firstship\nx
f\pv = fighter\firstship
fighter\firstship\nx = f

; Add a new destroyer to the end of the list
d.ship = New ship
d\pv = destroyer\lastship
destroyer\lastship\nx = d
destroyer\lastship = d

; Delete the 15th destroyer
scratch.ship = destroyer\firstship
For x = 1 To 14
	scratch = scratch\nx
Next
scratch\nx\pv = scratch\pv
scratch\pv\nx = scratch\nx
Delete scratch

; Move all ships three pixels down
For scratch.ship = Each ship
	scratch\y = scratch\y + 3
Next
