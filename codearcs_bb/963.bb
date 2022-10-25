; ID: 963
; Author: sswift
; Date: 2004-03-11 17:32:21
; Title: Weighted Random Numbers
; Description: A simple code example to show how to generate weigthed random numbers where one number appears more often than another,

Function Weighted_Random()

	Local Weight[5]
		
	; Weight each number, 0 thru 4.
		Weight[0] = 10
		Weight[1] = 10
		Weight[2] = 10
		Weight[3] = 10
		Weight[4] = 1
		
	; Add up the weights.
		TotalWeight = 0
		For Loop = 0 To 4
			TotalWeight = TotalWeight + Weight[Loop]
		Next
		
	; Choose a random number between 1 and the total weight.
		RandNum = Rand(1, TotalWeight)			
		
	; Figure out which value this number corresponds to, using the weights.
		For Loop = 0 To 4
			If Weight[Loop] > RandNum Then Return Loop
			RandNum = RandNum - Weight[Loop]
		Next  

End Function
