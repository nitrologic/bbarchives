; ID: 1297
; Author: Prof
; Date: 2005-02-20 15:30:15
; Title: Permutations Function
; Description: Returns the number of Permutations for 'N' values

; 
;   Non-Recursive Permutations Function By Prof
;
;                    Examples
;
;                3 will return 6
;                4 will return 24
;                5 will return 120 etc...
;
;
Graphics 640,480,32,2
SetBuffer BackBuffer()

N_Values=5                      ; Number of values
Perms=Permutations(N_Values)    ; Get the Number of permutations

Text 10,10,Str(Perms)+" possible arrangements (Permutations) of "+Str(N_Values)+" Values."
Flip
WaitKey()
End

; ***************************************************************

Function Permutations(N)
  ; Returns the number of Permutations of N Values
  ; N is an Int and must be greater than Zero otherwise Zero is returned
  ;
  If N>1 
     Result=N*(N-1)
     For X=N-2 To 1 Step-1
         Result=Result*X
     Next
  ElseIf N=1
     Result=1
  Else
     Result=0
  EndIf
  Return Result
End Function

; ***************************************************************
