; ID: 2439
; Author: Warpy
; Date: 2009-03-20 15:59:21
; Title: Levenshtein distance
; Description: Find the minimum number of edit operations needed to transform one string into another

'long-winded version, makes a Len(s)xLen(t) array
Function levenshtein(s$,t$)
	Local d[Len(s)+1,Len(t)+1]
	For i=0 To Len(s)
		d[i,0]=i
	Next
	For j=0 To Len(t)
		d[0,j]=j
	Next
	For i=1 To Len(s)
		dbg$=""
		For j=1 To Len(t)
			If s[i-1]=t[j-1] Then cost=0 Else cost=1
			d[i,j]=Min(Min(d[i-1,j]+1,d[i,j-1]+1),d[i-1,j-1]+cost)
			If dbg dbg:+" "
			dbg:+d[i,j]
		Next
		Print dbg
	Next
	Return d[Len(s),Len(t)]
End Function

'small version, just remembers the current and previous rows of the distance matrix
Function levenshtein2(s$,t$)
	Local row1[],row2[]
	row1=New Int[Len(t)+1]
	For j=0 To Len(t)
		row1[j]=j
	Next
	For i=1 To Len(s)
		row2=New Int[Len(t)+1]
		row2[0]=i
		dbg$=""
		For j=1 To Len(t)
			If s[i-1]=t[j-1] Then cost=0 Else cost=1
			row2[j]=Min(Min(row1[j]+1,row2[j-1]+1),row1[j-1]+cost)
			If dbg dbg:+" "
			dbg:+row2[j]
		Next
		Print dbg
		row1=row2
	Next
	Return row2[Len(t)]
End Function


While 1
	s$=Input(">")
	t$=Input(">")
	Print levenshtein(s,t)
	Print "--"
	Print levenshtein2(s,t)
Wend
