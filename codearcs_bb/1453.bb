; ID: 1453
; Author: Grey Alien
; Date: 2005-08-25 19:44:34
; Title: Loading Bar
; Description: Blitz Plus Loading Bar

; -----------------------------------------------------------------------------
; Loading Bar
; -----------------------------------------------------------------------------
Function ccLoadingBar(Percent#) ;Percent must be float from 0.0 to 1.0
	SetBuffer BackBuffer() ;precaution
	BarWidth = 400
	BarHeight# = 40 ;float so colour calc below returns float
	BarStartX = (ScreenWidth-BarWidth)/2 
	BarStartY = (ScreenHeight-BarHeight)/2 
	ccRectGradient(4, BarStartX-1, BarStartY-1, BarWidth+2, BarHeight+2)
	Color 0,0,0 ;draw a black one to cover up old percent text
	Rect BarStartX, BarStartY, BarWidth, BarHeight, 1 ;only need this line if no CLS was performed since last draw	
	Color 0,0,255 ;now a blue bar
	Local Max = 100
	For i = 0 To BarHeight-1
		;not full brightness
		If i > BarHeight/2 Then
			Local Col = (((BarHeight-i)+1)/(BarHeight/2)) * Max
		Else
			Col = ((i+1)/(BarHeight/2)) * Max
		EndIf
		Col = 50 + (Col * 1.5) ;avoid totally black bits
		Color 0,0,Col
		Rect BarStartX, BarStartY+i, BarWidth*Percent, 1, 1	
	Next
	Color 255,255,255
	;this assumes that the default font is loaded not another one!
	Text(ScreenWidth/2, (ScreenHeight/2)-(FontHeight()/2), ccPercentToString(Percent) + "%", True)
	Flip
End Function

Function ccRectGradient(Depth#, x, y ,w, h) ;depth in pixels
	For i = 0 To Depth-1
		Local Col = 255 - (((i+1)/Depth) * 255) ;depth is float so that i/depth returns a float 
		Color Col, Col, Col
		Rect x-i, y-i, w+i*2, h+i*2, 0
	Next
End Function

; -----------------------------------------------------------------------------
; Percent To String
; -----------------------------------------------------------------------------
Function ccPercentToString(Per#)
	;simply times by 100 then look for decimal place and chop it and everything past it
	;warning no range checking is performed
	Local PerString$ = Per * 100
	Return Mid$(PerString, 1, Instr(PerString, ".", 1)-1)
End Function
