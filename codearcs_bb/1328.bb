; ID: 1328
; Author: jfk EO-11110
; Date: 2005-03-15 23:20:51
; Title: Hiscore
; Description: Simple Hiscore Example

Graphics 800,600,32,2


;; a 2-dimansional string-array, containing scores and names

Const ARRAYSIZE = 30
Dim SortArray$( ARRAYSIZE ,1)

;; Fill it with pseudo-random values
For N = 0 To ARRAYSIZE
    ; this may be the score
	SortArray( N ,0) = Rand(100,1000)
    ; this may be the player name (creating some random names here)
	SortArray( N ,1) = Chr$(Rand(65,90))+Chr$(Rand(65,90))+Chr$(Rand(65,90))+Chr$(Rand(65,90))
Next

;Sort the list
BubbleSort( ARRAYSIZE )

;this will save the hiscore list
save_score(ARRAYSIZE)
;and if it's once saved, you can also load it:
load_score(ARRAYSIZE)


Print "-=Hall of Fame=-"
For i=0 To ARRAYSIZE
 Print SortArray( i ,0) +" Points by: " + SortArray( i ,1)
Next


WaitKey()
End

Function save_score(size)
 wr=WriteFile("myhiscore.txt")
 For i=0 To size
  WriteLine wr,SortArray(i,0)
  WriteLine wr,SortArray(i,1)
 Next
 CloseFile wr
End Function

Function load_score(size)
 re=ReadFile("myhiscore.txt")
 For i=0 To size
  SortArray(i,0)=ReadLine(re)
  SortArray(i,1)=ReadLine(re)
 Next
 CloseFile re
End Function


Function BubbleSort( Size% )
	Local i, j, temp0$,temp1$
	
	For i = Size To 0 Step -1
		For j = 1 To i
			If SortArray$( j - 1,0 ) < SortArray$( j ,0) Then
				temp0$ = SortArray$( j - 1 ,0)
				temp1$ = SortArray$( j - 1 ,1)
				SortArray$( j - 1 ,0) = SortArray$( j ,0)
				SortArray$( j - 1 ,1) = SortArray$( j ,1)
				SortArray$( j ,0) = temp0$
				SortArray$( j ,1) = temp1$
			EndIf
		Next
	Next
End Function
