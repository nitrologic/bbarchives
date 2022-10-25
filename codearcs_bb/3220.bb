; ID: 3220
; Author: Yue
; Date: 2015-08-14 21:14:43
; Title: Free third-person camera .
; Description: For a game in the third person .

; Code Free Camera.
; ====================
Graphics3D ( 800, 600, 32, 2)
SetBuffer ( BackBuffer())


; Variables Free Camera.
Local  distCamera       = 200
Local  mouseVel#       	= 0.5 
Local  suaveCamara# 	= 4.5

Local mxs#
Local mys#
Local camxa# 
Local camya#

; Escene.
Local Luz%    = CreateLight()
Local Camera% = CreateCamera() ; Camera.
Local Cubo%   = CreateCube()   ; Player.
ScaleMesh Cubo%, 25, 25, 25

HidePointer()
; Bucle.
While Not KeyHit(1)
	
	
        ; camera look
	If MouseDown(2) Then
		distCamera% = distCamera% + (MouseYSpeed() * mouseVel#)
		If distCamera% < 200 Then distCamera% = 200
		If distCamera% > 500 Then distCamera% = 500
		MoveMouse GraphicsWidth() / 2, GraphicsHeight() / 2
	Else
		mxs# = CurveValue(MouseXSpeed() * mouseVel#, mxs, suaveCamara#)
		mys# = CurveValue(MouseYSpeed() * mouseVel#, mys, suaveCamara#)
		camxa# = camxa - mxs Mod 360
		camya# = camya + mys
		If camya < -45  Then camya = -45
		If camya > 45 Then camya = 45
		distCamera% = distCamera% + (MouseZSpeed() * 3)
		MoveMouse GraphicsWidth() / 2, GraphicsHeight() / 2
		RotateEntity Camera%, camya, camxa, 0.0
		If distCamera% < 200 Then distCamera% = 200
		If distCamera% > 500 Then distCamera% = 500
	EndIf
	PositionEntity Camera%, EntityX(Cubo%),  EntityY(Cubo%), EntityZ(Cubo%)
	MoveEntity Camera%, 0, 0, -distCamera%
	
	RenderWorld
	Flip
	
Wend
End 



; Camara libre.
; ===============
Function CurveValue#(newvalue#, oldvalue#, increments)
	If increments >  1 Then oldvalue# = oldvalue# - (oldvalue# - newvalue#) / increments 
	If increments <= 1 Then oldvalue# = newvalue# 
	Return oldvalue# 
End Function
