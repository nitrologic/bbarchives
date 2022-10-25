; ID: 328
; Author: Ice9
; Date: 2002-05-24 14:38:14
; Title: Curve Angle
; Description: smooth rotation.

Function curveangle#( currentangle#,desiredangle#,curve# )
	

	Adist#=	Sqr((DesiredAngle#-CurrentAngle#)*(DesiredAngle#-CurrentAngle#))
    If DesiredAngle>CurrentAngle 
		If Adist#>180 
			CurrentAngle=CurrentAngle-(Adist#/curve)
			If CurrentAngle<-180 Then CurrentAngle=CurrentAngle+360
		Else
			Adist#=360-Adist#
			CurrentAngle=CurrentAngle+(Adist#/curve)
			If CurrentAngle>180 Then CurrentAngle=CurrentAngle-360
		EndIf
		
    Else
		If ADist#>180 
			CurrentAngle=CurrentAngle+(Adist#/curve)
			If CurrentAngle>180 Then CurrentAngle=CurrentAngle-360
		Else
			Adist#=360-Adist#
			CurrentAngle=CurrentAngle-(Adist#/curve)
			If CurrentAngle<-180 Then CurrentAngle=CurrentAngle+360
		EndIf
    EndIf
			
	Return CurrentAngle
	
End Function
