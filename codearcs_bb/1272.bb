; ID: 1272
; Author: nawi
; Date: 2005-01-29 15:32:40
; Title: Line Overlap Circle
; Description: Check if line is overlapping circle

Function LineOverlapCircle(x1#,y1#,x2#,y2#,cx#,cy#,Radius#)
	;'Calc Closest Point To circle center
	Local dx31#=cx#-x1#
	Local dx21#=x2#-x1#
	Local dy31#=cy#-y1#
	Local dy21#=y2#-y1#
	Local d#=((dx21#*dx21#)+(dy21#*dy21#))
	If d#<>0 Then d#=((dx31#*dx21#)+(dy31#*dy21#))/d#
	;'Clip To the line segments legal bounds
	If d#<0.0 Then d#=0
	If d#>1.0 Then d#=1
	Local dx#=cx#-(x1#+(dx21#*d#))
	Local dy#=cy#-(y1#+(dy21#*d#))
	If Radius# => Sqr((dx#*dx#)+(dy#*dy#))
		;'Line intersects circle
		Return 1 
	EndIf
	Return 0
End Function
