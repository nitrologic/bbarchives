; ID: 2102
; Author: Arem
; Date: 2007-09-10 19:37:16
; Title: Base Converter
; Description: Converts a number from one base to another.

Type character
	Field symbol$,value
End Type

seed$="0123456789abcdefghijklmnopqrstuvwxyz"

For a=0 To 35
	c.character=New character
	c\symbol$=Left$(seed$,1)
	c\value=a
	
	seed$=Right$(seed$,Len(seed$)-1)
Next

start$=Input$("Enter a number in any base: ")
base=Input$("Enter the base: ")
newbase=Input$("Enter the base to convert to: ")

savedstart$=start$

If Not base<37 And newbase<37
	Print "Base must be less than 37!"
	Delay(5000)
	End
End If

total=0
power=0

While Not start$=""
	currentsymbol$=Right$(start$,1)
	
	For c.character=Each character
		If c\symbol$=Lower$(currentsymbol$)
			currentnumber=c\value
		End If
	Next
	
	If currentnumber>base-1
		invalid=1
	End If
	
	total=total+currentnumber*base^power
	
	start$=Left$(start$,Len(start$)-1)
	power=power+1
Wend

While Not total=0
	currentnumber=total Mod newbase
	
	total=total-currentnumber
	total=total/newbase
	
	For c.character=Each character
		If c\value=currentnumber
			currentsymbol$=c\symbol$
		End If
	Next
	
	output$=currentsymbol$+output$
Wend

If invalid
	Print savedstart$+" is not a valid base "+base+" number!"
Else
	Print output$
End If

Delay(5000)
