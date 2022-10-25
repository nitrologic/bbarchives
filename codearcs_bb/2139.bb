; ID: 2139
; Author: boomboom
; Date: 2007-11-04 06:12:18
; Title: Different Matrix 'Rain'
; Description: This is another matrix effect

Graphics 480,600,0,2 ;Sets Graphics Mode

For i = 0 To GraphicsWidth() Step 15
	cMatrixRain(1,i,Rnd#(1,12)) ;Create MatrixRain
Next

;MAIN LOOP
Repeat
	uMatrixRain() ;Update MatrixRain
	VWait ;Basic frame limiting
Until KeyHit(1)

; Matrix Rain Functions ----------------------------------
Type MatrixRain
	Field Pointer%
	Field PosX#
	Field PosY#
	Field Speed#
	Field DelayCounter%
End Type
Function cMatrixRain(Pointer%, PosX#, Speed#)
	TMatrixRain.MatrixRain = New MatrixRain
	TMatrixRain\PosX# = PosX#
	TMatrixRain\Speed = Speed
	TMatrixRain\DelayCounter% = TMatrixRain\Speed
End Function
Function uMatrixRain()
	For TMatrixRain.MatrixRain = Each MatrixRain
		If TMatrixRain\DelayCounter% = 0 Then
			Color 0,Rnd(50,255),0
			Text TMatrixRain\PosX#,TMatrixRain\PosY#,Chr$(Rnd(33,126))
			TMatrixRain\PosY# = TMatrixRain\PosY# + 10
			TMatrixRain\DelayCounter% = TMatrixRain\Speed
		Else
			TMatrixRain\DelayCounter% = TMatrixRain\DelayCounter% - 1
		End If
		
		If TMatrixRain\PosY# > GraphicsHeight() Then
			Color 0,0,0
			Rect TMatrixRain\PosX#,0,10,GraphicsHeight()
			TMatrixRain\PosY# = 0
		End If
	Next
End Function
;=========================================================
