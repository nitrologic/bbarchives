; ID: 3239
; Author: Dan
; Date: 2016-01-05 14:47:35
; Title: Displaying leading zero's
; Description: add zero's for the e.g score display

;====================================================================
; Project: AddZero 
; Version: 1.0
; Author: Dan
; Email: ~.~
; Copyright:	PD
; Description:    Adds leading 0 before numbers so that the number
;                   12 will be displayed as 0012 or 00012 depending 
;                   on how many places the display shall be shown
;                   optional a displaying flag can be set
;                   (to show the - if the numbers are below)
;                   or to display a set of customizeabled flags which can be shown
;                   if the length of the number is higher than it should be displayed
;====================================================================

Function addzero$(var,showcount,displayflag=0)
;add leading 0 to the variable, returns a string (obviously)!
;if the flag is set, it will display the flag
;it displays the -numbers as showcount-1 eg showcount=3, var=3 returns as "-03"
;if showcount is 1 it ignores the - sign and displayflag 
	If showcount<=0 
		showcount=1
	EndIf
	displayflag=displayflag Mod 5
	If var=>0
		Select displayflag
			Case 0
				fl$=""
			Case 1
				fl$="+"
			Case 2
				fl$="<"
			Case 3
				fl$="±"
			Case 4
				fl$="~"
		End Select
	Else
		fl$="-"				;Set the flag as -
		var=-var			;Make the variable as positive number
	EndIf
	
	If Len(var)>showcount
		If displayflag>0
			Txt$=fl$+Right$(var,showcount-1)
		Else
			Txt$=Right$(var,showcount)
		EndIf
	EndIf
	
	If Len(var)<showcount And showcount>1
	    If fl$="-" And displayflag>0
			Txt$=fl$+String("0",showcount-Len(var)-1)+var
	    Else
			Txt$=String("0",showcount-Len(var))+var
		EndIf
	ElseIf Len(var)=showcount And showcount>1
		If fl$="-" And displayflag>0
			Txt$=fl$+String("0",showcount-Len(var)-2)+Right$(var,showcount-1)
		Else
			Txt$=String("0",showcount-Len(var))+Right$(var,showcount)
		EndIf
	ElseIf showcount=1
	    Txt$=Right$(var,1)
	EndIf

Return Txt$
End Function
