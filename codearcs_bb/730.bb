; ID: 730
; Author: PRJ
; Date: 2003-06-30 06:14:35
; Title: FastLine
; Description: Fast line drawing in plain blitz

Function FLine(x1,y1,x2,y2,rgb)
	xd=(x2-x1)
	yd=(y2-y1)
	If Abs(xd)>Abs(yd)
		If x1>x2
			temp=x1
			x1=x2
			x2=temp
			temp=y1
			y1=y2
			y2=temp
			xd=(x2-x1)
			yd=(y2-y1)
		EndIf
		grad#=Float yd/Float xd
		If grad#=0.0 Then grad#=0.0001
		yf#=Float y1+grad#
		For x=x1 To x2 Step 1
			WritePixelFast x,Int(yf#),rgb
			yf#=yf#+(grad#*1.0)
		Next
	Else
		If y1>y2
			temp=x1
			x1=x2
			x2=temp
			temp=y1
			y1=y2
			y2=temp
			xd=(x2-x1)
			yd=(y2-y1)
		EndIf
		grad#=Float xd/Float yd
		If grad#=0.0 Then grad#=0.0001
		xf#=Float x1+grad#
		For y=y1 To y2 Step 1
			WritePixelFast Int(xf#),y,rgb
			xf#=xf#+(grad#*1.0)
		Next
	EndIf
End Function
