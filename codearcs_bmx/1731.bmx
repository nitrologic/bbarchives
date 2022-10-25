; ID: 1731
; Author: kronholm
; Date: 2006-06-11 18:57:25
; Title: Mouse controlled "spaceship" firing rockets which explode after a defined time
; Description: Nothing fancy, just a fully commented code snippet written to help you understand basic movement, gravity, classes (types) etc.

'fire multiple rockets that blow up at a certain time
Graphics 800,600,0						'init gfx window

Global rocketlist:TList = CreateList()				'make a list for active rockets
Global gravity# = -0.07						'gravity
SeedRnd(MilliSecs())						'make a quick "random" seed
HideMouse

Repeat								'mainloop, runs until escape pressed
	If MouseHit(1) Then trocket.fire(5)			'fire 5 rockets on LMB click, change to mousedown for autofire
	DrawRect MouseX(),580,10,40
	For Local rocket:trocket = EachIn rocketlist	'run thru rocketlist
		rocket.drawandmove()				'draw a rocket
	Next							'move to next object in rocketlist
	GCCollect()							'collect garbage
	Flip ; Cls						'flip backbuffer and clear it
Until KeyHit(key_escape)

Type trocket										
	Field x#,y#						'position of the rocket
	Field xvel#=Rand(-2,2),yvel#=Rand(5,8)			'velocity of the rocket
	Field timetolive = 0, timetodie = 100			'a crude timer deciding when rocket dies
	Function fire(number)					'the "fire x number of rockets function"
		For Local i=1 To number				'simply creates x number of trockets and adds them to list
			Local newrocket:trocket = New trocket
			newrocket.x = MouseX()+5 ; newrocket.y = 580	'set coordinates to mouse
			rocketlist.addlast(newrocket)
		Next
	EndFunction
	Method drawandmove()				'move and draw a rocket
		x:+xvel ; y:-yvel					'add the veloceties
		yvel:+gravity								'add the gravity to the y velocity
		DrawOval x,y,1,1							'draw the rocket itself
		timetolive:+1								'add to the timetolive counter
		If timetolive >= timetodie Then die()		'counter check
	EndMethod
	Method die()									'death method
		DrawText "BOOM",x-20,y						' :)
		Local rocket:trocket = Self					
		rocketlist.remove(rocket)					'remove rocket from list
		rocket = Null								'die.
	EndMethod
EndType
