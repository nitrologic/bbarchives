; ID: 1485
; Author: Fernhout
; Date: 2005-10-11 15:21:29
; Title: Rounded text
; Description: To create text serounded by another color.

;-----------------------------------------------------------
; Function RoundText Give a text a differend back color 
; Pass X and Y position and the texts  you want to print.
; After that i used color names instead of RGB color notation
; See the function SetColor for mor detail
;
; Remark this wil not flip screens and draw on the active viewport
; setup by the user.
;-----------------------------------------------------------
Function RoundText (XPos,YPos,Label$,RoundColor$,TextColor$)
	SetColor (RoundColor$) ; New function see function for detail
	For x = -1 To 1
		For y = -1 To 1
			Text Xpos+x,Ypos+y,Label$
		Next 
	Next 
	SetColor (TextColor$)
	Text XPos,YPos,Label$
End Function
