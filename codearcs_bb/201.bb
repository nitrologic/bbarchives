; ID: 201
; Author: Jim Teeuwen
; Date: 2002-01-24 20:53:25
; Title: More versatile VB Split() Command
; Description: Splits a string into a 1 dimensional Array.and..

[code]
;// Usage: bbSplit(Expression$ [, Delimiter$][, Count%])
;// Expression is needed (doh!).

;// Delimiter(optional) is the character used to split the string.
;//  A blank Space is default.

;// Count(optional) represents the number of items you want to have returned.
;//  Defaults to -1, wich means it will return all possible results.

;// ### EXAMPLES #################################

;// example 1.
;// Specify string
mystring$="abcd,1234,beer,babes"
;// Split it and set ',' as the delimiter
bbSplit(mystring$,",")
;// print the results
print split(0)
print split(1)
print split(2)
print split(3)

;// output will be:
abcd
1234
beer
babes

;// example 2.
;// Specify string
mystring$="123.456.789.0"
;// Split it and set '.' as the delimiter
;// this time we only want 2 results
bbSplit(mystring$,".",2)
;// print the results
print split(0)
print split(1)

;// output will be:
123
456

;// ### THE GOODS ################################

Dim split(0)
Type splitt
	Field txt$
End Type

Function bbsplit(txt$,devider$=" ",count=-1)
	n=0
	If Instr(txt$,devider$)=0 Then Goto e
	Select count
	Case -1
		While txt$<>""
			spl.splitt=New splitt
			If Instr(txt$,devider$)<>0 Then
				spl\txt$=Left$(txt$,Instr(txt$,devider$)-1)
				txt$=Mid$(txt$,Instr(txt$,devider$)+Len(devider$))
			Else
				spl\txt$=txt$
				txt$=""
			End If
		Wend
	Default
		While txt$<>"" And n<count
			spl.splitt=New splitt
			If Instr(txt$,devider$)<>0 Then
				spl\txt$=Left$(txt$,Instr(txt$,devider$)-1)
				txt$=Mid$(txt$,Instr(txt$,devider$)+Len(devider$))
			Else
				spl\txt$=txt$
			txt$=""
			End If
			n=n+1
		Wend
	End Select
	split_makeArray()
	split_cleaner()
	.e
End Function

Function split_cleaner()
Repeat
	For spl.splitt=Each splitt
		Delete spl
	Next
Until spl=Null
End Function

Function split_makeArray()
j=0
Repeat
	For spl.splitt=Each splitt
		j=j+1
	Next
Until spl=Null
Dim split(j)
j=0
Repeat
	For spl.splitt=Each splitt
		split(j)=spl\txt$
		j=j+1
	Next
Until spl=Null
End Function
[/code]
