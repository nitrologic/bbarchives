; ID: 2124
; Author: EdzUp[GD]
; Date: 2007-10-20 13:42:18
; Title: CalculateDice
; Description: For all the RPG fans here a dice function

;Attack dice system
SeedRnd MilliSecs()
Global AttackDice$ = "4D10*2D3";"50+D6+D20+D100-D20+1000"	;minimum is -17 (1+1+1-1), Maximum is 125 (6+20+100-20)

Global cy=0
Global Min=1000
Global Max=0

For a=0 To 65535
	C= CalculateDice( AttackDice$ )
	If C<Min Then Min=C
	If C>Max Then Max=C
Next

Repeat
	cy=0
	Cls
	Text 10, cy, CalculateDice( AttackDice$ )
	Text 50, cy, "Min:"+Min
	Text 150, cy, "Max:"+Max
	WaitKey
Until KeyDown( 1 )=1
End

Function CalculateDice( DiceString$ )
	Local Modifier$ = "+"
	Local Dice$ = ""
	Local DiceModValue=1						;example 2D6 this var would be 2
	Local DiceValue=0							;example 2D6 this var would be rand( 1, 6 )
	Local IsDice=False							;true if this is a dice calculation
	Local Char$=""
	Local Value=0
	Local TempValue=0
	
	If Len( DiceString$ )<1 Then Return			;empty strings just exit the function
	
	For DicePos=1 To Len( DiceString$ )
		Char$ = Upper$( Mid$( DiceString$, DicePos, 1 ) )
		Dice$ = Dice$ + Char$
		If Char$="+" Or Char$ = "-" Or Char$="*" Or Char$="/" Or DicePos=Len( DiceString$ )
			DiceModValue=1
			;we hit a calculation
			DPos = Instr( Dice$, "D" )
			If DPos=0
				;its just a standard value
				TempValue = Dice$
				
				Select Modifier$
				Case "+":	Value = Value +TempValue
				Case "-":	Value = Value -TempValue
				Case "*":	Value = Value *TempValue
				Case "/":	Value = Value /TempValue
				End Select		
			Else
				;its a dice calculation
				IsDice=True
				If DPos>1 Then DiceModValue = Left$( Dice$, DPos-1 )
				DiceValue = Right$( Dice$, Len( Dice$ )-DPos )
				
				;add the dice so 50D80 would run for 50 cycles
				For DK=1 To DiceModValue
					;use a temporary value as multiplication would multiply the whole value
					;rather than just was it needed to be multiplied by
					TempValue = TempValue + Rand( 1, DiceValue )
				Next
				
				Select Modifier$
				Case "+":	Value = Value +TempValue
				Case "-":	Value = Value -TempValue
				Case "*":	Value = Value *TempValue
				Case "/":	Value = Value /TempValue
				End Select
			EndIf
	
			Dice$=""
			TempValue=0
			;adjust modifier here as it has to affect the next calculation
			If DicePos<Len( DiceString$ ) Then Modifier$ = Char$
		EndIf
	Next
	
	Return Value
End Function
