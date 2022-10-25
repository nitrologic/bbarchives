; ID: 1922
; Author: Andy_A
; Date: 2007-02-12 13:05:07
; Title: Handy string parsing function
; Description: Extract words, characters, or symbols using delimiters

;Parse string function demo
;  By:Andy Amaya
;Date:2007.02.11

;Purpose for parse$() function:
;	To allow user to extract words or groups of symbols using
;	the delimiter best suited to the task at hand.

crLf$ = Chr$(13)+Chr$(10)
separator$ = crLf$+"==========================================================="+crLf$

;===================================================
;Parse string using the "comma" character
;	Possible Use: read data in comma separated value file
;===================================================
Print parse$("Now,is,the,time,for,all,good,men,to,come,to,the,aid,of,their,country.",7,",")
Print separator$

;===================================================
;Parse string using default delimiter of "space" character
;	Possible Use: count number of words in a text file
;===================================================
Print parse$("The quick brown fox jumped over the lazy dog.",9)
Print separator$

;===================================================
;Parse string using "period" character
;	Possible Use: break out IP address
;===================================================
For x = 1 To 4
	Print parse$("127.0.0.1",x,".")
Next
Print separator$

;===================================================
;Parse string using "backslash" character
;	Possible Use: break out path & filename info
;===================================================
For x = 1 To 200
	result$ = parse$("C:\Program Files\BlitzPlus\Parse string function demo.bb",x,"\")
	If result$ <> "" Then Print result$	Else Exit
Next
Print separator$

a$ = Input("Press [ENTER] to Exit.")
End

Function parse$(string2Chk$, n, delimiter$=" ")
	;initialize local variables
	Local count% = 0
	Local findDelimiter% = 0
	Local position% = 1
	Local current$ = ""
	;'n' must be greater than zero
	;otherwise exit function and return null string
	If n > 0 Then
		;strip leading and trailing spaces
		string2Chk$  = Trim(string2Chk$)
		;find the word(s)
		Repeat
			;first check if the delimiter occurs in string2Chk$
			findDelimiter% = Instr(string2Chk$,delimiter$,position)
			If findDelimiter <> 0 Then
				;extract current word in string2Chk$
				current$ = Mid$(string2Chk$,position,findDelimiter-position)
				;word extracted; increment counter
				count = count + 1
				;update the start position of the next pass
				position = findDelimiter + 1
				;if counter is same as n then exit loop
				If count = n Then findDelimiter = 0
			End If
		Until findDelimiter = 0
		;Special Case: only one word and no delimiter(s) or last word in string2Chk$
		If (count < n) And (position <= Len(string2Chk$)) Then
			current$ = Mid$(string2Chk$,position, Len(string2Chk$) - position+1)
			count = count + 1
			;looking for word that is beyond length of string2Chk$
			If count < n Then current$ = ""
		End If
	End If
	Return current$
End Function
