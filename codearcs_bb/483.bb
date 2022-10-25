; ID: 483
; Author: Difference
; Date: 2002-11-10 02:57:18
; Title: Is In Triangle 2D
; Description: Check if point is inside triangle

; by Charles H. Giffen from
; http://groups.google.com/groups?selm=3784B03B.F1CCF05D%40virginia.edu&oe=UTF-8&output=gplain
; Blitz Port by Peter Scheutz

Function IsInTriangle ( px#,py#, ax#,ay#,bx#,by#,cx#,cy# ) 

	Local bc#,ca#,ab#,ap#,bp#,cp#,abc#

	bc# = bx*cy - by*cx 
	ca# = cx*ay - cy*ax 
	ab# = ax*by - ay*bx
	ap# = ax*py - ay*px
	bp# = bx*py - by*px
	cp# = cx*py - cy*px
	abc# = Sgn(bc + ca + ab)

	If (abc*(bc-bp+cp)>0) And (abc*(ca-cp+ap)>0) And (abc*(ab-ap+bp)>0) Then Return True
End Function
