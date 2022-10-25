; ID: 2321
; Author: Warpy
; Date: 2008-09-22 23:44:54
; Title: Number from a poisson distribution
; Description: Returns a random number from a Poisson distribution with given mean

Function poisson(lambda!)
	If lambda>500 Return poisson(lambda/2)+poisson(lambda/2)
	k=0
	u!=Rnd(0,1)
	fact=1
	p!=Exp(-lambda)
	u:-p
	While u>0
		k:+1
		fact:*k
		p:*lambda/k
		u:-p
	Wend
	Return k
End Function


'  example - draws a graph of the proability density function
Local counts[20]
For i=0 To 1000
	p=poisson(5)
	If p<20
		counts[p]:+1
	EndIf
Next

Graphics 800,800,0
For i=0 To 19
	DrawRect i*40,0,40,counts[i]
	Print counts[i]
Next
Flip
WaitKey()
