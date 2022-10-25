; ID: 2301
; Author: Warpy
; Date: 2008-08-21 17:01:33
; Title: Random number with weighting
; Description: Given an array of weights, pick an index so probability is proportional to the corresponding weight.

Function picksomething(probabilities#[])
   p#=rnd(0,1)
   t#=0
   i=0
   while t<=p
      t:+probabilities[i]
      i:+1
   wend
   return i - 1 
End Function


Function picksomethingbinary(probabilities#[],sumprobabilities#[])
	l=Len(probabilities)

	p#=Rnd(0,1)
	i=l/2
	move=i
	While 1
		move:/2
		If move=0 Then move=1
		s#=sumprobabilities[i]
		If s<p
			i:+move
		ElseIf s-probabilities[i]>p
			i:-move
		Else
			Return i
		EndIf
	Wend
	Return i - 1 
End Function


'EXAMPLE 

Local probabilities#[5]
probabilities=[.1,.1,.1,.2,.5]

Local sumprobabilities#[5]
t#=0
For i=0 To 4
	t:+probabilities[i]
	sumprobabilities[i]=t
Next

ms=MilliSecs()
For i=1 To 100000
	number=picksomethingbinary(probabilities,sumprobabilities)
Next
diff=MilliSecs()-ms
Print "binary search method took "+String(diff)+"ms"

ms=MilliSecs()
For i=1 To 100000
	number=picksomething(probabilities)
Next
diff=MilliSecs()-ms
Print "lazy method took "+String(diff)+"ms"
