; ID: 1096
; Author: Nilium
; Date: 2004-06-24 22:33:06
; Title: Various Sorting Algorithms
; Description: Shell, Bubble, Insertion, Selection, and Quick sort functions

;; All sorting functions have been ported from C/C++ code on the website http://linux.wku.edu/~lamonml/
;; All credit for writing these functions goes to Michael Lamont

Graphics 800,600,32,2

F = LoadFont( "Arial", "16" )
SetFont F

;; The array
Const ARRAYSIZE = 3000
Dim SortArray( ARRAYSIZE )


;; Fill it with pseudo-random integers
For N = 0 To ARRAYSIZE
	SortArray( N ) = Rand(256*256)
Next

;; Pick a mode
Print "1.  Shell Sort"
Print "2.  Quick Sort"
Print "3.  Bubble Sort (Warning: Can be very slow)"
Print "4.  Selection Sort"
Print "5.  Insertion Sort"
SortMethod = Input$( "Pick a method: " )

Select SortMethod
	Case 1
		Delay 250
		TT = MilliSecs()
		ShellSort( ARRAYSIZE )
	Case 2
		PivotMode = Input$( "Use a random pivot position with the quick sort function? (0 or 1)    " )
		Delay 250
		TT = MilliSecs()	;; Get the starting time
		QuickSort( 0, ARRAYSIZE, PivotMode )
	Case 3
		Delay 250
		TT = MilliSecs()
		BubbleSort( ARRAYSIZE )
	Case 4
		Delay 250
		TT = MilliSecs()
		SelectionSort( ARRAYSIZE )
	Case 5
		Delay 250
		TT = MilliSecs()
		InsertionSort( ARRAYSIZE )
End Select
TT = MilliSecs() - TT	;; Get the difference, resulting in the time it took to sort the array

For N = 64 To 128		;; Print 64 of the array's elements
	Print SortArray( N )
Next

Print "Time Taken " + TT	;; And tell you how long it took

WaitKey



;; FUNCTIONS

Function ShellSort( Size% )
	Local i, j, increment, temp
	
	increment = 3
	While increment > 0
			For i = 0 To Size
				j = i
				temp = SortArray( i )
				While j >= increment And SortArray( j-increment) > temp
						SortArray(j) = SortArray(j-increment)
						j = j - increment
				Wend
				SortArray(j) = temp
			Next
			If increment/2 <> 0 Then
				increment = increment / 2
			ElseIf increment = 1
				increment = 0
			Else
				increment = 1
			EndIf
	Wend
End Function

Function SelectionSort( Size% )
	Local i, j, min, temp
	
	For i = 0 To Size
		min = i
		For j = i+1 To Size
			If SortArray( j ) < SortArray( min ) Then min = j
		Next
		temp = SortArray( i )
		SortArray( i ) = SortArray( min )
		SortArray( min ) = temp
	Next
End Function

Function BubbleSort( Size% )
	Local i, j, temp
	
	For i = Size To 0 Step -1
		For j = 1 To i
			If SortArray( j - 1 ) > SortArray( j ) Then
				temp = SortArray( j - 1 )
				SortArray( j - 1 ) = SortArray( j )
				SortArray( j ) = temp
			EndIf
		Next
	Next
End Function

Function QuickSort( L, R, RandomPivot = True )
	Local A, B, SwapA#, SwapB#, Middle#
	A = L
	B = R
	
	If RandomPivot Then
		Middle = SortArray( Rand(L, R) )
	Else
		Middle = SortArray( (L+R)/2 )
	EndIf
	
	While True
		
		While SortArray( A ) < Middle
			A = A + 1
			If A > R Then Exit
		Wend
		
		While  Middle < SortArray( B )
			B = B - 1
			If B < 0 Then Exit
		Wend
		
		If A > B Then Exit
		
		SwapA = SortArray( A )
		SwapB = SortArray( A )
		SortArray( A ) = SortArray( B )
		SortArray( A ) = SortArray( B )
		SortArray( B ) = SwapA
		SortArray( B ) = SwapB
		
		A = A + 1
		B = B - 1
		
		If B < 0 Then Exit
		
	Wend
	
	If L < B Then QuickSort( L, B )
	If A < R Then QuickSort( A, R )
End Function

Function InsertionSort( Size% )
	Local i, j, index
	
	For i = 1 To Size - 1
		index = SortArray( i )
		j = i
		While j > 0 And SortArray( j-1 ) > index
			SortArray( j ) = SortArray( j - 1 )
			j = j - 1
		Wend
		SortArray( j ) = index
	Next
End Function
