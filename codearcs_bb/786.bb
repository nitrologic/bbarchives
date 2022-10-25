; ID: 786
; Author: LostCargo
; Date: 2003-08-31 15:48:02
; Title: Round Floor Celing  towards Zero
; Description: Rounds towards zero. Combined Floor Ceiling

;==========================================================
;FUNCTION ::  MATH FLOOR CEILING
;
;NOTE:  This function trims to the integer. 
;	Simply rounds To the nearest integer in the 
;       direction of zero
;	excellent function for simply trimming th e
;==========================================================
Function MATH_FC#(TARGET_VALUE#)

If  TARGET_VALUE# >0 Then
	Return Floor(TARGET_VALUE#)
End If

If target_value <0 Then
	Return Ceil(TARGET_VALUE#)
End If
	
	

End Function
