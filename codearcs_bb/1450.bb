; ID: 1450
; Author: CS_TBL
; Date: 2005-08-22 08:42:11
; Title: TheCatWalksOnTheKeyboard-proof String-2-Float
; Description: convert strings to float while ignoring messy input

Function String2Float#(a$)
	;
	;	String2Float, by CS_TBL
	;
	;       allowed in the string are: dot, minus, space, 0123456789
	;	other chars before the 'value' return 0.0, and other things after the 'value' are ignored
	;
	;	examples:
	;
	;	"0.1.2.3" returns "12.3"
	;	"-----5" returns "-5.0"
	;	"1.024-.-5" returns "1024.5"
	;	"1.000.000.00" returns "1000000.0"
	;	"-1- 2 3 4 . 5 - 6" returns "-1234.56"
	;

	; wipe spaces
	a$=Replace$(a$," ","")

	If a$="" Return 0

	l=Len(a$)

	; do we have an odd amount of '-' ?
	az$=Replace$(a$,"-","")
	m=l-Len(az$)

	; yes? it's a negative number!
	If m Mod 2 negative=True

	; scan the value (without - ) for dots
	For t=Len(az$)-1 To 0 Step -1
		If Not found
			If Mid$(az$,t+1,1)="."
				found=True
				foundpos=(Len(az$)-1)-t
			EndIf
		EndIf
	Next
	; so we found the most-right dot, if any..


	; get rid of all the dots then
	az$=Replace$(az$,".","")

	l=Len(az$)

	If found ; place 1 dot back
		az$=Left$(az$,l-foundpos)+"."+Right$(az$,foundpos)
	EndIf

	If negative
		az$="-"+az$
	EndIf

	Return Float(az$)
	
End Function
