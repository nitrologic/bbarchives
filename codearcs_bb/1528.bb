; ID: 1528
; Author: Jesse B Andersen
; Date: 2005-11-10 14:55:17
; Title: Line of sight mesh management
; Description: Makes mesh object, that block line of sight, transparent!!

;xmlspy
;Nov. 10, 2005
;http://www.alldevs.com
;jesse_andersengt@yahoo.com


Graphics3D 640, 480

camera = CreateCamera()
AmbientLight 255, 255, 255


player = CreateCube()
MoveEntity player, 0, 0,10
EntityType player, 1



cube2 = CreateCube()
MoveEntity cube2, 0, 0, 5
EntityPickMode cube2, 1
EntityColor cube2, 255, 0, 0
ScaleEntity cube2, 2,1,1

cube2 = CreateCube()
MoveEntity cube2, 5, 0, 5
EntityPickMode cube2, 1
EntityColor cube2, 255, 0, 0

cube2 = CreateCube()
MoveEntity cube2, -5, 0, 5
EntityPickMode cube2, 1
EntityColor cube2, 255, 0, 0




plane = CreatePlane()
EntityColor plane, 0, 0, 255
MoveEntity plane, 0, -1, 0

Repeat

PointEntity camera,player
move_cam(camera)
	a = CameraPick(camera,GraphicsWidth()/2,GraphicsHeight()/2)
	If a <> b Then
		b = a
		If b > 0 Then EntityAlpha b, .5 : c = b
	Else
		b = 0
		If c > 0 And c <> a Then EntityAlpha c, 1 : c = 0
	EndIf
	
UpdateWorld
RenderWorld
Flip

Until KeyHit(1)


Function Move_Cam( CAM )
		If KeyDown(29) = 0 And KeyDown(157 ) = 0 Then
			;Camera Movement
			
			;Left Right
			If KeyDown(203) Then MoveEntity Cam, -1, 0, 0
			If KeyDown(205) Then MoveEntity Cam, 1, 0, 0
			;Up Down
			If KeyDown(200) Then MoveEntity Cam, 0, 0, 1
			If KeyDown(208) Then MoveEntity Cam, 0, 0, -1
			;AZ
			If KeyDown(30) Then MoveEntity Cam, 0, 1, 0
			If KeyDown(44) Then MoveEntity Cam, 0, -1, 0
				

		ElseIf KeyDown(29) Or KeyDown(157) Then
			;Left Right
			If KeyDown(203) Then TurnEntity Cam, 0, 1, 0 : Return True
			If KeyDown(205) Then TurnEntity Cam, 0, -1, 0 : Return True
			
			;Up Down
			If KeyDown(200) Then TurnEntity Cam, 1, 0, 0 : Return True
			If KeyDown(208) Then TurnEntity Cam, -1, 0, 0 : Return True
				
			;AZ
			If KeyDown(30) Then TurnEntity Cam, 0, 0, 1 : Return True
			If KeyDown(44) Then TurnEntity Cam, 0, 0, -1 : Return True
			
			
		EndIf
End Function

;Pardon da French
