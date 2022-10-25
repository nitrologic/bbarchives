; ID: 2065
; Author: JA2
; Date: 2007-07-13 09:40:59
; Title: Taxi Game
; Description: The basics for a Taxi game...

Graphics3D 640,480,16,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()

;-------------------;
; Types and Globals ;-----------------------------------------------------
;-------------------;

Type Person

	Field Start,Dest,X,Y,Pos
	
End Type

Type Location

	Field Num,X,Y,Ppl,MaxPpl,Name$,Pos[99]
	
End Type

Type Player

	Field X,Y,Ppl,MaxPpl,Dest[99],Score
	
End Type

;--------;
; Create ;----------------------------------------------------------------
;--------;

Function CreateLocation (Num,X,Y,MaxPpl,Name$)

Local LN.Location

LN.Location = New Location
LN\Num		= Num
LN\X		= X
LN\Y		= Y
LN\MaxPpl	= MaxPpl
LN\Name		= Name

End Function

Function CreatePerson (Location)

Local PN.Person,LN.Location,A

PN.Person 	= New Person
PN\Start	= Location

Repeat

	PN\Dest = Rand (1,5)
	
Until PN\Dest <> PN\Start

For LN.Location = Each Location

	If PN\Start = LN\Num
	
		LN\Ppl 	= LN\Ppl + 1
		PN\X	= LN\X
		PN\Y	= LN\Y + 10
		
		For A = 0 To LN\MaxPpl
		
			If LN\Pos[A] = 0
			
				LN\Pos[A]	= 1
				PN\Pos		= A
				
				Exit				
			
			EndIf
		
		Next
	
	EndIf

Next

End Function

Function CreatePlayer (X,Y,Max)

Local PR.Player

PR.Player 	= New Player
PR\X		= X
PR\Y		= Y
PR\MaxPpl	= Max

End Function

;--------;
; Update ;----------------------------------------------------------------
;--------;

Function UpdateGame()

UpdatePlayers()
UpdatePeople()
UpdateLocations()

End Function

Function UpdatePlayers()

Local PR.Player,PN.Person,LN.Location,A

For PR.Player = Each Player

	PR\X = PR\X + KeyDown(205)-KeyDown(203)
	PR\Y = PR\Y + KeyDown(208)-KeyDown(200)

	Color 250,200,0
	Oval PR\X-3,PR\Y-3,6,6,True
	Color 255,255,0
	
	For A = 0 To PR\MaxPpl-1
	
		Text 550,A*15+40,PR\Dest[A],True,True
	
	Next
	
	Text 550,20,"Score: "+PR\Score,True,True
	
	For PN.Person = Each Person
	
		If RectsOverlap (PN\X-2+PN\Pos*8,PN\Y-2,4,4,PR\X-3,PR\Y-3,6,6)
		
			If PR\Ppl < PR\MaxPpl
			
				PR\Ppl = PR\Ppl + 1
				
				For A = 0 To PR\MaxPpl
				
					If PR\Dest[A] = 0
					
						PR\Dest[A] = PN\Dest
						
						For LN.Location = Each Location
						
							If LN\Num = PN\Start
							
								LN\Ppl			= LN\Ppl - 1
								LN\Pos[PN\Pos]	= 0
							
							EndIf
						
						Next
						
						Delete PN
						
						Exit
					
					EndIf
				
				Next
			
			EndIf
		
		EndIf
	
	Next

	For LN.Location = Each Location
	
		If RectsOverlap (LN\X-5,LN\Y-5,10,10,PR\X-3,PR\Y-3,6,6)
		
			For A = 0 To PR\MaxPpl
			
				If PR\Dest[A] = LN\Num
				
					PR\Score	= PR\Score + 100
					PR\Ppl		= PR\Ppl - 1
					PR\Dest[A] 	= 0
				
				EndIf
			
			Next
		
		EndIf
	
	Next
	
Next

End Function

Function UpdateLocations()

Local LN.Location

For LN.Location = Each Location

	Color 255,0,0
	Rect LN\X-5,LN\Y-5,10,10,True
	Color 0,255,0
	Text LN\X,LN\Y-30,LN\Name+"("+LN\Num+")",True,True
	Text LN\X,LN\Y-15,LN\Ppl+"/"+LN\MaxPpl,True,True

Next

End Function

Function UpdatePeople()

Local PN.Person

For PN.Person = Each Person

	Color 0,0,255
	Rect PN\X-2+PN\Pos*8,PN\Y-2,4,4,True
	Color 0,255,255
	Text PN\X+PN\Pos*8,PN\Y+10,PN\Dest,True,False
	
Next

End Function

;-----------;
; Game Loop ;-------------------------------------------------------------
;-----------;

Setup()

Function Setup()

CreateLocation (01,090,050,02,"Church")
CreateLocation (02,320,100,12,"School")
CreateLocation (03,400,400,05,"Beach")
CreateLocation (04,600,420,03,"Bridge")
CreateLocation (05,120,380,08,"Grocery")

CreatePerson (01)
CreatePerson (02)
CreatePerson (02)
CreatePerson (02)
CreatePerson (03)
CreatePerson (03)
CreatePerson (05)
CreatePerson (05)
CreatePerson (05)

CreatePlayer (320,240,9)

Game()

End Function

Function Game()

Local LN.Location,PN.Person,PR.Player,A,NewLocation

Repeat
Cls

UpdateGame()

If KeyHit (57)

	NewLocation = Rand (1,5)

	For LN.Location = Each Location
	
		If LN\Num = NewLocation
		
			If LN\Ppl < LN\MaxPpl
			
				CreatePerson (LN\Num)
			
			EndIf
		
		EndIf
	
	Next

EndIf

Flip
Until KeyDown (1)
End

End Function
