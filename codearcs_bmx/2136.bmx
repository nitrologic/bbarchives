; ID: 2136
; Author: Leon Drake
; Date: 2007-11-02 22:57:56
; Title: Boxes Collide
; Description: Math only Box to Box Collision Detection

Function boxescollide(x#,y#,z#,w#,h#,d#,x2#,y2#,z2#,w2#,h2#,d2#,coordtype=True)


If coordtype=True Then

w# = x#+w#
h# = y#+h#
z# = z#+d#

w2# = x2#+w2#
h2# = y2#+h2#
z2# = z2#+d2#


EndIf
	
	
	If x# <= w2# And w# >= x2# And y# <= h2# And h# >= y2# And z# <= d2# And d# >= z2# Then
	Return True
	Else
	Return False
	EndIf 
		
	
End Function
