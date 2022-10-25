; ID: 3002
; Author: TAS
; Date: 2012-11-16 17:37:59
; Title: Extract Text
; Description: Another Splitter!

'extract.bmx
'extracts data out of delimited string
'and puts it into am array

'Thomas A Stevenson
'war-game-programming.com
'11-16-2012

'example of use
Local s$[3]
Extract("Dog,Cat,Deer,Pig,horse",s)
For i=0 To s.length-1
	Print s[i]
Next

Function Extract(s$,stringArray$[] Var,seperator$=",",i=0)
	'leading or terminal seperator returns an empty string
	'i = array index to store first segiment
	k=1	'start position
	Repeat
		'find location of seperater
		j=Instr(s$,seperator,k)
		If j=0
			'terminal segiment
			'Print String(j)+"  "+Mid(s$,k)
			stringArray[i]=Mid(s$,k)
			Exit
		EndIf
		
		'Print String(j)+"  "+Mid(s$,k,j-k)
		stringArray[i]=Mid(s$,k,j-k)
		'increament and check array index
		i=i+1
		If i=stringArray.length Then 
			'redim array
			stringArray=stringArray[..i+1]
		EndIf
		
		k=j+1	'inc start position
	Forever
End Function
