; ID: 450
; Author: sswift
; Date: 2002-10-06 18:27:37
; Title: Line Normal
; Description: This function will calculate the normal of a line.

Global Nx#
Global Ny#
Global Nd#

Function Get_Line_Normal(X1#, Y1#, X2#, Y2#)

	; Calculate the normal of the line, (A = Nx, B = Ny)
	; and the distance of the line (D) from the origin (0,0,0) at it's closest point to the origin.
	Nx# = Y2# - Y1# 
	Ny# = X1# - X2#
	Nd# = X2#*Y1# - X1#*Y2#


	; Normalize the normal.
	; (Remove any scaling which will skew our results.  Make the normal's length 1.)
	Length# = Sqr(Nx#*Nx# + Ny#*Ny#)
	Nx# = Nx# / Length#
	Ny# = Ny# / Length#
	Nd# = Nd# / Length#

End Function
