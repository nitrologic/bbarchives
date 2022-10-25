; ID: 1839
; Author: mindstorms
; Date: 2006-10-13 21:07:02
; Title: sqr function
; Description: returns in radical form

Graphics 200,13
AppTitle("sqr function")
Global left_output,right_output
Repeat
out.output = findSquareRoot(Input("number: "))
Cls
Text 0,0,out\oleft+"*sqr("+out\oright+")"
Flip
WaitKey()
Cls
Until KeyDown(1)

Type output		;type used to store all outputs of various funcitons I use...easier than using globals
	Field oleft,oright
End Type

Function findSquareRoot.output(num%)
	Local c_num = num
	Local c_prime = 1
	Local place = 0
	Local answers[50]
	Local left_answer = 1
	Local right_answer = 1
	
	Repeat
		c_prime = NextPrime(c_prime)
		If c_num = c_prime Then ;all done
			answers[place] = c_prime
			Exit
		ElseIf c_num Mod c_prime = 0 Then 
			answers[place] = c_prime
			place = place + 1
			c_num = c_num/c_prime
			c_prime = 1
		EndIf
	Forever
	
	For i = 0 To place
		For j = i+1 To place
			If answers[i] = -1 Or answers[j] = -1 Then 		;make sure we don't choose something already taken
			ElseIf answers[i] = answers[j] Then 
				left_answer = left_answer*answers[j]
		
				answers[j] = -1
				answers[i] = -1
				Exit
			ElseIf j = place Then 
				right_answer = right_answer*answers[i]
				answers[i] = -1
			EndIf
			Next
			If i = place Then 
				If answers[i] <> -1 Then right_answer = right_answer*answers[i]
			EndIf
	Next
	out.output = New output
	out\oleft = left_answer
	out\oright = right_answer
	Return out
End Function

;Prime number search function
Function NextPrime(current)
  Local i
  Local found=0

  While Not found
    current = current + 1 : found = True
    For i = 2 To current/2
      If (current Mod i) = 0 Then
        Found = False
        Exit
      EndIf
    Next
  Wend

  Return current
End Function
