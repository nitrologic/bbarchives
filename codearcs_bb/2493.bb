; ID: 2493
; Author: BlitzSupport
; Date: 2009-05-31 15:45:10
; Title: Follow player's position with delay
; Description: Entity follows player's position, delayed by a given time

; Use cursors... 'enemy' will trail by 2 seconds...

Type Snapshot
	Field x#
	Field y#
	Field ticks
End Type

; The key variables -- play with these!

trail = 1		; Updates applied every 'trail' milliseconds...
latency = 2000	; Trailing entity delayed by this much (in milliseconds)...

Graphics 1024, 768, 0, 2
SetBuffer BackBuffer ()

ticks = MilliSecs ()

x# = 0
y# = 0

xs# = 0
ys# = 0

move# = 0.025

Repeat

	; Move player...
		
	If KeyDown (203) Then xs = xs - move
	If KeyDown (205) Then xs = xs + move

	If KeyDown (200) Then ys = ys - move
	If KeyDown (208) Then ys = ys + move

	x = x + xs
	y = y + ys

	; Record current time...
		
	ms = MilliSecs ()

	If ms - trail => ticks
	
		; Create a snapshot in time with player's current position...
	
		ticks = ms
		snap.Snapshot = New Snapshot
		snap\x = x
		snap\y = y
		snap\ticks = ticks

	EndIf
		
	Cls

	; Draw player's position...
	
	Color 127, 64, 64	
	Rect x - 4, y - 4, 8, 8

	; Draw player's position 
	Color 127, 127, 255

	For snap.Snapshot = Each Snapshot
		If ms - latency => snap\ticks
			trailx# = snap\x
			traily# = snap\y
			Delete snap
		EndIf
	Next
	
	Rect trailx - 2, traily - 2, 4, 4
		
	Flip
	
Until KeyHit (1)

End
