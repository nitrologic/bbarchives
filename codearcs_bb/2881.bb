; ID: 2881
; Author: Charrua
; Date: 2011-08-17 06:43:00
; Title: LevenshteinDistance
; Description: Calculates the minimun amount of basic operations required to transmorm String1 into String2. Three basic operations are considered: Character Delete, Insert, Substitution

Function LevenshteinDistance(String1$, String2$)
	
	;calculates the minimun amount of basic operations required to transmorm String1 into String2
	;3 basic operations are considered: Character Delet, Insert, Substitution
	
	Local Len1=Len(String1)
	Local Len2=Len(String2)
	Dim Ld(Len1, Len2)

	Local i, j, Cost, s1$, s2$

	If Len1 = 0 Then Return Len2
	If Len2 = 0 Then Return Len1
	
	For i=0 To Len1
		Ld(i,0)=i
	Next
	For j=0 To Len2
		Ld(0,j)=j
	Next

	For i=1 To Len1
	
		For j=1 To Len2
		
			s1$ = Mid(String1,i,1)
			s2$ = Mid(String2,j,1)
			If s1 = s2 Then
				Cost=0
			Else
				Cost=1
			End If
			
			Ld(i,j) = Min( Min( Ld(i-1,j)+1 , Ld(i,j-1)+1 ), Ld(i-1,j-1)+Cost)	;deletion, insertion, substitution
			
		Next
	Next
	
	Return Ld(Len1, Len2)

End Function
