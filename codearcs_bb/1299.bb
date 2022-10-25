; ID: 1299
; Author: sswift
; Date: 2005-02-21 02:12:01
; Title: "Marching Ants" Selection Line
; Description: This function draws an automatically animated line of dots that move along a line from the first point to the second.

; -----------------------------------------------------------------------------------------------------------------------------------
; This function draws a line of "marching ants" to the current buffer.
; This is the same kind of animated line drawn around a selection in Photoshop.
;
; There is no need to do anything to make the ants animate, other than continuously redrawing the line on the screen.
; -----------------------------------------------------------------------------------------------------------------------------------
Function Line_MarchingAnts(X1#, Y1#, X2#, Y2#)
	
	Local X#, Y#
	Local XDist#, YDist#
	Local Mv#
	Local StepX#, StepY#
	Local AntSpeed, AntSize, AntCounter
	Local RGB
	
	; Setup

		XDist# = X1# - X2#
		YDist# = Y1# - Y2#
	
		If XDist# < 0 Then XDist# = -XDist#
		If YDist# < 0 Then YDist# = -YDist#
		
		Select (YDist# > XDist#)
			Case True  Mv# = YDist# 
			Case False Mv# = XDist#
		End Select
		
		StepX# = XDist# / Mv#
		If (X1# > X2#) Then StepX# = -StepX#
	
		StepY# = YDist# / Mv#
		If (Y1# > Y2#) Then StepY# = -StepY#

		X# = X1#
		Y# = Y1#
	
	; This is the clever bit that makes the ants move. 
		
		; The starting value is determined by the current time.
		; AntSpeed is the amount of time in milliseconds it takes for an ant to move one ant length.
		; AntSize is the size of each ant, plus the space between them.
		
		AntSpeed = 50
		AntSize  = 8
		AntCounter = Abs((MilliSecs()/AntSpeed) Mod AntSize)
	
	LockBuffer GraphicsBuffer()
	For nc = 0 To Floor(mv#)

		Select (Abs(AntCounter) Mod AntSize) < (AntSize/2) 
			Case True  WritePixelFast X#, Y#, $00000000
			Case False WritePixelFast X#, Y#, $FFFFFFFF
		End Select
	
		X# = X# + StepX#
		Y# = Y# + StepY#

		AntCounter = AntCounter - 1

	Next
	UnlockBuffer GraphicsBuffer()

End Function
