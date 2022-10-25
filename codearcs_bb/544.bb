; ID: 544
; Author: Snarty
; Date: 2003-01-10 00:00:38
; Title: Gradient Rect
; Description: As the command says...

; Written By Paul Snart
; Copyright Pacific Software
; Open Source (Credit where credit is due)

Function GradientBar(x,y,w#,h,r1#,g1#,b1#,r2#,g2#,b2#)

	NumSteps#=w-1
	
	If NumSteps>0
		StRd#=(r2-r1)/NumSteps
		NRd#=r1
		StGr#=(g2-g1)/NumSteps
		NGr#=g1
		StBl#=(b2-b1)/NumSteps
		NBl#=b1
		For dw=1 To w-1
			Color NRd,NGr,NBl
			Line x+dw,y+1,x+dw,y+h-1
			NRd=NRd+StRd
			NGr=NGr+StGr
			NBl=NBl+StBl
		Next
	EndIf

End Function
