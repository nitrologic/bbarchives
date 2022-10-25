; ID: 954
; Author: Beaker
; Date: 2004-02-29 12:23:30
; Title: Line function
; Description: Fairly fast Line function

Function FastLine(x,y, x2,y2, rgb=$FFFFFF)
	Local yl = False
	Local incval, finval

	Local short = y2-y
	Local long = x2-x
	If (Abs(short)>Abs(long))
		temp=short
		short=long
		long=temp
		yl = True
	EndIf
	
	finval = long
	If (long<0)
		incval = -1
		long = -long
	Else
		incval = 1
	EndIf

	Local dec#
	If (long=0)
		dec = Float(short)
	Else
		dec = (Float(short)/Float(long))
	EndIf
	
	Local j# = 0.0
	If (yl)
		i=0
		While i <> finval
			If x+j >= 0
				If x+j < GraphicsWidth()
					If y+i >= 0
						If y+i < GraphicsHeight()
							WritePixel x+j, y+i,rgb	
						EndIf
					EndIf
				EndIf
			EndIf
			j = j + dec
			i = i + incval
		Wend
	Else
		i=0
		While i <> finval
			If x+i >= 0
				If x+i < GraphicsWidth()
					If y+j >= 0
						If y+j < GraphicsHeight()
							WritePixel x+i, y+j,rgb
						EndIf
					EndIf
				EndIf
			EndIf
			j = j + dec
			i = i + incval
		Wend
	EndIf
End Function
