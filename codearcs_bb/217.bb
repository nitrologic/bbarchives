; ID: 217
; Author: EdzUp[GD]
; Date: 2002-02-02 11:50:32
; Title: Useful functions
; Description: Some useful functions for many types of games

[code]

Function OnScreen(Enta)
	;Like DB's OnScreen function
	CameraProject camera,EntityX#(Enta),EntityY#(Enta),EntityZ#(Enta)
	If ProjectedZ#()<>0
		If ProjectedX#()>-1 And ProjectedX#()<GraphicsWidth() And ProjectedY#()>-1 And ProjectedY#()<GraphicsHeight()
			Return 1
		EndIf
	EndIf
	Return 0
End Function

Function ScreenX()
	Return ProjectedX#()
End Function

Function ScreenY()
	Return ProjectedY#()
End Function

Function FindAngle#( x1#,y1#,x2#,y2# )
 a#=ATan2(x2-x1,y2-y1)
 Return 180-a#
End Function

Function Distance#(x#,y#,z#,x2#,y2#,z2#)
 ;distance function a very useful function indeed.
 value#=Sqr((x#-x2#)*(x#-x2#)+(y#-y2#)*(y#-y2#)+(z#-z2#)*(z#-z2#))
 Return value#
End Function

Function TurnDirection(facing#,pivot#)
	diff = facing#-pivot
	While diff>180 : diff=diff-360 : Wend
	While diff<-180 : diff=diff+360 : Wend
	If diff<0 Then Return -1 Else Return 1
	Return 0
End Function

[/code]
