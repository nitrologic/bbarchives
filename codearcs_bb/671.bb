; ID: 671
; Author: Synchronist
; Date: 2003-05-04 12:19:26
; Title: Blitz Haiku
; Description: A pseudo-Haiku generator in Blitz

;;; Blitz Haiku
; This little app was adapted from a very old C64
; program called Hanshan from around 1985. Enjoy!


;;;GLOBALS
Dim W$(35):Dim S$(35)

;;; MAIN LOOP
Initialize()
Repeat
	SeedRnd(MilliSecs())
	r=Rand(1,3)
	Select r
		Case 1
			Pattern1()
		Case 2
			Pattern2()
		Case 3
			Pattern3()
	End Select
	
	Print
	ans$=Input("Would you like another? Y/N: ")
	If ans$="Y" Or ans$="y"
		Print
	Else Print: Print("Many blessings on you!")
		Delay 1000
		End
	EndIf
Until KeyHit(1)



;;; FUNCTIONS
Function Initialize()
	Restore worddata
	For j = 1 To 35
		Read word$: W$(j)=word$: S$(j)=word$
	Next
	Return
End Function

Function Pattern1()
	SeedRnd(MilliSecs())
	Print W$(Rand(1,35))+"..."+W$(Rand(1,35))
	Print "   "+W$(Rand(1,35))
	Print "      "+W$(Rand(1,35))
	Return
End Function

Function Pattern2()
	SeedRnd(MilliSecs())
	Print S$(Rand(1,35))
	Print "   "+S$(Rand(1,35))+"..."
	Print S$(Rand(1,35))
	Return
End Function

Function Pattern3()
	SeedRnd(MilliSecs())
	Print "   "+W$(Rand(1,35))
	Print S$(Rand(1,35))
	Print "   "+W$(Rand(1,35))+", "+S$(Rand(1,35))
	Return
End Function




;;; DATA
.worddata
Data "hammered","hanging","winding","clearest","weary"
Data "optimize","deadline","coding","computer","calculating"
Data "terminal","keyboard","late","now","thinking"
Data "in the cool morning"
Data "nodding in slumbering repose"
Data "waves of coolness"
Data "out from the deepest"
Data "joyful, joyful"
Data "in the black darkness"
Data "I take your words"
Data "I put out the light"
Data "the time runs out"
Data "those that are left"
Data "learning"
Data "action"
Data "I hurry forward"
Data "why should you waste"
Data "when shall we finish"
Data "little sleeping"
Data "much typing"
Data "those few steps"
Data "now at dusk"
