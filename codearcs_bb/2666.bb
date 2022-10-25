; ID: 2666
; Author: Streaksy
; Date: 2010-03-18 09:15:53
; Title: Weighted Random
; Description: Tidy weighted random number generation.

;WEIGHTED RANDOM ARRAYS
Global weightedcases,maxweightedcases=10000
Dim weightedcaseweight(maxweightedcases)
Dim weightedcaselabel$(maxweightedcases)






;DEMO
AppTitle "Weighted Random Demo"
Dim demodim(10)
AddWeightedCase 10
AddWeightedCase 5
AddWeightedCase 2
AddWeightedCase 1
AddWeightedCase 1
AddWeightedCase 1
AddWeightedCase 1
AddWeightedCase 1
AddWeightedCase 1
AddWeightedCase 1
SetBuffer BackBuffer()
SetFont LoadFont("verdana",17)
Repeat
Cls
		w=WeightedRandom()
		demodim(w)=demodim(w)+1
	For t=1 To 10
	Color 100,50,50:Rect 0,(t-1)*20,demodim(t),18
	Color 100,150,255:Text 20,(t-1)*20,"weight: "+weightedcaseweight(t)
	Next
		sum=0
		For t=1 To 10
		sum=sum+demodim(t)
		Next
	For t=1 To 10
	perc=(demodim(t)*100)/sum
	Color 100,250,155:Text 220,(t-1)*20,"occurance: "+perc+"%"
	Next
Flip
Until KeyHit(1)
End







;WEIGHTED RANDOM FUNCTIONS
Function WeightedRandom()
For t=1 To weightedcases:maxweight=maxweight+weightedcaseweight(t):Next
v=Rand(1,maxweight)
	For t=1 To weightedcases
	w=weightedcaseweight(t)
	If maxweight-w<v Then Return t
	maxweight=maxweight-w
	Next
End Function

Function AddWeightedCase(weight,label$="")
weightedcases=weightedcases+1
weightedcaseweight(weightedcases)=weight
weightedcaselabel(weightedcases)=label
End Function

Function ClearWeightedCases()
weightedcases=0
End Function
