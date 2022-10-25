; ID: 33
; Author: DJWoodgate
; Date: 2001-09-01 12:06:46
; Title: Function Roman$
; Description: Convert number to Roman Numerals

; Returns a string with the Roman numerals for value v
; This will Not be accurate for numbers > 3999 as the number
; 5000 should be represented as an M with a line above it etc, however as we
; do not have such a character I have used N, O, P.. etc for larger numbers, but see printroman...
Function roman$(v%)
	r$="IVXLCDMNPQRSTUWYZ"
	n$=v : i=Len(n$)*2-1
	For x=1 To Len(n$)
		d=Mid$(n$,x,1)
		Select d
		Case 1,2,3	: rom$=rom$+String$(Mid$(r$,i,1),d)
		Case 4		: rom$=Rom$+Mid$(r$,i,1)+Mid$(r$,i+1,1)
		Case 5		: rom$=rom$+Mid$(r$,i+1,1)
		Case 6,7,8	: rom$=rom$+Mid$(r$,i+1,1)+String$(Mid$(r$,i,1),d-5)
		Case 9		: rom$=rom$+Mid$(r$,i,1)+Mid$(R$,i+2,1)
		End Select
		i=i-2
	Next
	Return rom$
End Function

; Print Roman characters in graphics mode to x,y
; Uses character mapping from roman$() to produce
; M with bars for large numbers.  It all gets a bit
; silly if numbers are realy large however.
; Works best with larger font sizes
Function Printroman(r$,x,y)
h=StringHeight("M"):w=StringWidth("M")
Locate x,y
For s=1 To Len(r$)
	t$=Mid$(r$,s,1)
	p=Instr("NPQRSTUWYZ",t$,1)
	If p>0
		x=x+w
		Write "M"
		For l=1 To p
			Locate x-w+w/5,y-(l-1)*h/10
			Write "¯"
		Next
		Locate x,y
	Else
		x=x+StringWidth(t$)
		Write t$
	End If
Next
End Function

