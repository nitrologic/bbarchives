; ID: 290
; Author: skn3[ac]
; Date: 2002-04-10 00:11:06
; Title: gettok$(word$,token,seperator)
; Description: A handy function to return a token from a string...

Function gettok$(from$,which,space$=" ")
	Local foundword=False
	Local mode=False
	Local current=0
	Local maketok$=""
	Local getchar$=""
	For i=1 To Len(from$)
		getchar$=Mid$(from$,i,1)
		If foundword=False Then
			If mode=False Then
				If getchar$<>space$ Then
					mode=True
					current=current+1
				End If
				If current=which Then
					foundword=True
					maketok$=maketok$+getchar$
				End If
			Else
				If getchar$=space$ Then
					mode=False
				End If
			End If
		Else
			If getchar$=space$ Then
				Exit
			Else
				maketok$=maketok$+getchar$
			End If
		End If
	Next
	Return maketok$
End Function

;some test examples
Print gettok$("hello, this is just a test",2,",")
Print gettok$("Testing 1 2 3",2)
Print ( Int(gettok$("/calc 50 + 65",2)) + Int(gettok$("/calc 50 + 65",4)) )
