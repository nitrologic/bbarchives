; ID: 1300
; Author: Malice
; Date: 2005-02-21 10:00:25
; Title: Convert Integers to Words
; Description: Functions to convert Integer values into String words.

;Convert Integer To String
;Submitted by Malice

;Updated October 2009

Function IntToString$(nInt)
	If (Not(nInt)) Then Return "None"
	
	Local sReturn$
	If (nInt<0)
		nInt=Abs(nInt)
		sReturn$="Minus "			
		If nInt>(999999999)	Then 	Return "Too Many Less Than Zero"
	End If
	
	If nInt>(999999999)	Then 	Return "Too Many"
			
	Local HTU	=	(nInt Mod 1000)
	Local Thou	=	((nInt Mod 1000000)			-	HTU)	*	0.001
	Local Mill	=	((nInt Mod 1000000000)		-	((Thou	* 1000 )+	HTU))	*	0.000001

	If (Mill) 
		sReturn$	=	sReturn+HundredTen$(Mill)+" Million"
	End If
	
	If (Thou)
		If (Mill)						Then	sReturn$	=	sReturn+", "
		sReturn$	=	sReturn+HundredTen$(Thou,(Mill))+" Thousand"
	End If
	
	If (HTU)
		If ((Mill)	Or	(Thou))	Then	sReturn$	=	sReturn+", "
		sReturn$=sReturn+HundredTen$(HTU,(Mill+Thou))
	End If

	Return CleanString$(sReturn$)
End Function
			
Function HundredTen$(nInt,nBefore=False)
	
	Local sReturn$
	
	Local	Tens		=	(Int(Floor(Float(nInt * 0.1)) Mod 10))
	Local	Units		=	(Int(nInt Mod 10))
	If (Tens<2)
		Units			=	(nInt Mod 100)
		Tens			=	0
	End If
		
	Local	Hund	=	((nInt-((Tens*10)+Units))	*0.01)
		
	If (Hund)
		sReturn$		=	sReturn$+Unit$(Hund)+"-Hundred"
	End If
		
	If (Not(Tens+Units))
		Return sReturn$
	Else
		If ((Hund)+(nBefore))
			sReturn$=sReturn$+" And "
		End If
	End If					
	
	If (Tens)	
		Select (Tens)
			Case 2	:	sReturn$=sReturn$+"Twenty"
						If (Units) Then sReturn=sReturn$+"-"
			Case 3	:	sReturn$=sReturn$+"Thirty"
						If (Units) Then sReturn=sReturn$+"-"
			Case 4	:	sReturn$=sReturn$+"Forty"
						If (Units) Then sReturn=sReturn$+"-"
			Case 5	:	sReturn$=sReturn$+"Fifty"
						If (Units) Then sReturn=sReturn$+"-"
			Default	:	sReturn$=Replace(sReturn$+Unit(Tens)+"ty","tty","ty")
						If (Units) Then sReturn=sReturn$+"-"
		End Select
	End If
	
	If (Units) Then sReturn$=sReturn$+Unit(Units)
	
	Return sReturn$
	
End Function	

Function Unit$(nInt)
	Select nInt
		Case 0	:	Return ""
		Case 1	:	Return "One"
		Case 2	:	Return "Two"
		Case 3	:	Return "Three"
		Case 4	:	Return "Four"
		Case 5	:	Return "Five"
		Case 6	:	Return "Six"
		Case 7	:	Return "Seven"
		Case 8	:	Return "Eight"
		Case 9	:	Return "Nine"
		Case 10	:	Return "Ten"
		Case 11	:	Return "Eleven"
		Case 12	:	Return "Twelve"
		Case 13	:	Return "Thirteen"
		Case 15	:	Return "Fifteen"
		Default	:	Return Replace(Unit$(nInt Mod 10)+"teen","tteen","teen")
	End Select
End Function

Function CleanString$(sReturn$)
	sReturn$=Trim$(sReturn$)
	sReturn$=Replace (sReturn$,"  "," ")
	sReturn$=Replace (sReturn$,", And"," And")
	If(Left$(sReturn$,3)="And")	Then	sReturn$=Right$(sReturn$,(Len(sReturn$)-4))
	If(Right$(sReturn$,3)="And")	Then	sReturn$=Left$(	sReturn$,(Len(sReturn$)-4))

	Return sReturn$+"."
End Function
