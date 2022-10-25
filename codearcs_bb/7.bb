; ID: 7
; Author: David Bird(Birdie)
; Date: 2001-08-16 18:42:22
; Title: Vector Products
; Description: A couple of helper functions Dot and cross product.

Type vector
	Field x#
	Field y#
	Field z#
End Type

Global CProd.vector=New vector
Global DProd#
;
;Cross and DotProduct functions
Function CrossProduct(x1#,y1#,z1#,x2#,y2#,z2#)
	CProd\x=(y1*z2)-(z1*y2)
	CProd\y=(z1*x2)-(x1*z2)
	CProd\z=(x1*y2)-(y1*x2)
End Function
Function DotProduct#(x1#,y1#,z1#,x2#,y2#,z2#)
	DProd=((x1*x2)+(y1*y2)+(z1*z2))
	Return DProd
End Function
;Return Cross product answers
Function CproductX#()
	Return CProd\x#
End Function
Function CproductY#()
	Return CProd\y#
End Function
Function CproductZ#()
	Return CProd\z#
End Function
;Return Dot product answers
Function DProduct#()
	Return DProd#
End Function

