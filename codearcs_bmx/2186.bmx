; ID: 2186
; Author: Chroma
; Date: 2008-01-14 23:30:38
; Title: D&amp;D Dice Roller
; Description: Just a little function I wrote to accept a die roll string and return a roll + bonus.

SeedRnd MilliSecs()

Local dice$ = "1d20+3"

For i = 1 To 20
	Print "Rolling "+dice+": "++RollDice(dice) 
Next

End

Function RollDice:Int(die$)
	If Left$(die,1) = "d" Then die = "1" + die
	Local roll$[] = die.split("d")
	Local ppos = Instr(roll[1],"+")
	Local mpos = Instr(roll[1],"-")
	Local bonus%, total%
	If ppos > 0
		bonus = Int(Mid$(roll[1], ppos+1, roll[1].length-ppos))
		roll[1] = Left(roll[1], ppos-1)
	ElseIf mpos > 0
		bonus = Int(Mid$(roll[1], mpos, roll[1].length-mpos+1))
		roll[1] = Left(roll[1], mpos-1)
	EndIf
	For Local i% = 1 To Int(roll[0])
		total:+ Rand(Int(roll[1]))
	Next
	Return total + bonus
End Function
