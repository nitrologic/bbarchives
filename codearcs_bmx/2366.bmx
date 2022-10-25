; ID: 2366
; Author: Warpy
; Date: 2008-12-02 14:08:45
; Title: Knuth shuffle
; Description: Randomly rearrange a set of numbers

'takes as input the number of elements you want to shuffle, and returns an array saying which
'elements to swap with which
'i.e. when this is done you should swap element i with element k[i]
Function KnuthShuffle[](n)
	Local k[n]
	For i=0 To n-1
		k[i]=i
	Next
	
	For i=0 To n-2
		j=Rand(i,n-1)
		b=k[i]
		k[i]=k[j]
		k[j]=b
	Next
	
	Return k
End Function

'an example - shuffle up the letters in a string
SeedRnd MilliSecs()
s$="abcdefg"
o$=""
For i=EachIn knuthshuffle(Len(s))
	o:+Chr(s[i])
Next
Print o
