; ID: 1642
; Author: Naughty Alien
; Date: 2006-03-13 10:38:40
; Title: Bubble Sort
; Description: Sorting an array

Dim MyArray(90) 
Global Number 
Global Count
Cell_Number=41
Graphics 1280,1024,32,1
;*** Make an array of random numbers ***
Cls
Print
Print "Initial random array :-"
Print

                                             
For Count = 1 To Cell_Number
        Number = Rnd (0,100)
        MyArray(Count) = Number
        Print MyArray(Count)
Next 
Delay 8000
;Number = UBOUND(MyArray)

Bubblesort (Cell_Number)
Print
Print "Sorted array :-"
Print

For Count = 1 To Cell_Number
        Print MyArray(Count)
Next 
Delay 8000
End


Function Bubblesort(num%)

For Count = 1 To num%
        For Counter = 1 To num%
                If MyArray(Counter) > MyArray(Count) 
                   SWAP=MyArray(Count)
                   MyArray(Count)=MyArray(Counter)
                   MyArray(Counter)=SWAP
                End If
        Next 
Next 
End Function
