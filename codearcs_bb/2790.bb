; ID: 2790
; Author: Mr Snidesmin
; Date: 2010-12-02 19:48:33
; Title: 3d Puppet Experiment
; Description: Simple puppet you can control with mouse and keys

Graphics3D 800, 600, 0, 2

Type puppet
	Field body
	Field head
	Field armL
	Field armR
	Field farmL
	Field farmR
	
	Field bodyYaw#
	Field bodyDeltaYaw#
	Field headYaw#
	Field headPitch#
	Field headDeltaPitch#
	
	Field armLpitch#
	Field armRpitch#
	
	Field armLyaw#
	Field armRyaw#
	
	Field armRoll#
	Field armRollTarget#
	
	Field armYaw#
	Field armYawTarget#	
End Type


l = CreateLight(1)
RotateEntity l, 45,0,0

Global pup.puppet = quickpuppet()

Global mainCamera = CreateCamera()
PositionEntity mainCamera, 5, 2.5, -5
PointEntity mainCamera, pup\body

;CameraClsColor mainCamera, 180, 170, 150
CameraClsColor mainCamera, 50, 70, 120

Global lookAtCamera = False

Global livemode = True

HidePointer()	
While Not KeyHit(1)
	If KeyHit(28) Then	
		livemode = Not livemode 
		If livemode Then
			MoveMouse GraphicsWidth()/2, GraphicsHeight()/2
			FlushMouse()
			pup\headYaw = 0
			pup\headPitch = 0
			pup\bodyYaw = 0
			HidePointer()			
		End If
	End If

	mx# = MouseXSpeed()
	my# = MouseYSpeed()
	mz# = MouseZSpeed()
	
	If livemode Then
		MoveMouse GraphicsWidth()/2, GraphicsHeight()/2
	Else
		ShowPointer()
	End If
		
	If KeyHit(59) Then pup\armRollTarget = 0.0
	If KeyHit(60) Then pup\armRollTarget = 0.5
	If KeyHit(61) Then pup\armRollTarget = 1.0
	If KeyHit(62) Then pup\armRollTarget = 2.0
	
	If KeyHit(63) Then pup\armYawTarget = 0.0
	If KeyHit(64) Then pup\armYawTarget = 0.5
	If KeyHit(65) Then pup\armYawTarget = 1.0
	If KeyHit(66) Then pup\armYawTarget = 2.0
	
	If (KeyHit(208)) Then
		lookAtCamera = Not lookAtCamera 
	End If
	
	If (KeyDown(200)) Then
		pup\headYaw = pup\headYaw + mx
		pup\headDeltaPitch = my
	Else
		If lookAtCamera Then
			;pup\headYaw = pup\headYaw * 0.9
			;pup\headPitch = pup\headPitch * 0.9
			dx# = EntityX(mainCamera, True) - EntityX(pup\head, True)
			dy# = EntityY(mainCamera, True) - EntityY(pup\head, True)
			dz# = EntityZ(mainCamera, True) - EntityZ(pup\head, True)
			
			AlignToVector pup\head, dx, dy, dz, 1, 0.2
			pup\headYaw = EntityYaw(pup\head)
			pup\headPitch = EntityRoll(pup\head)
		End If
		
		pup\headDeltaPitch = pup\headDeltaPitch - mz * 4
		pup\headDeltaPitch = pup\headDeltaPitch * 0.8
		
		If Not MouseDown(1) Then
			pup\armLpitch = pup\armLpitch + my
			pup\armLyaw = pup\armLyaw + mx 
		End If
		If Not MouseDown(2) Then
			pup\armRpitch = pup\armRpitch - my
			pup\armRyaw = pup\armRyaw - mx
		End If
	End If
	
	pup\armRoll = pup\armRoll + 0.1 * (pup\armRollTarget - pup\armRoll)
	pup\armYaw = pup\armYaw + 0.1 * (pup\armYawTarget - pup\armYaw)

	
	If MouseDown(1) Then
		pup\armLyaw = pup\armLyaw * 0.9
		pup\armLpitch = pup\armLpitch * 0.9
	End If
	If MouseDown(2) Then
		pup\armRyaw = pup\armRyaw * 0.9
		pup\armRpitch = pup\armRpitch * 0.9	
	End If


	pup\bodyDeltaYaw = pup\bodyDeltaYaw * 0.9
	pup\bodyDeltaYaw = pup\bodyDeltaYaw + 1.0 * (KeyDown(205) - KeyDown(203))
	pup\bodyYaw = pup\bodyYaw + pup\bodyDeltaYaw
	pup\headPitch = pup\headPitch - pup\headDeltaPitch
	
	minArmPitch# = 25
	maxArmPitch# = 120
	
	minArmYaw# = 0
	maxArmYaw# = 100
	
	;Limits
	If (pup\armLPitch < -maxArmPitch) pup\armLPitch = -maxArmPitch
	If (pup\armRPitch > maxArmPitch) pup\armRPitch = maxArmPitch
	If (pup\armLPitch > minArmPitch) pup\armLPitch = minArmPitch
	If (pup\armRPitch < -minArmPitch) pup\armRPitch = -minArmPitch
		
	If (pup\armLYaw < -maxArmYaw) pup\armLYaw = -maxArmYaw
	If (pup\armRYaw > maxArmYaw) pup\armRYaw = maxArmYaw
	If (pup\armLYaw > minArmYaw) pup\armLYaw = minArmYaw
	If (pup\armRYaw < -minArmYaw) pup\armRYaw = -minArmYaw
	
	;limit to make sure arms do not cross
	;EntityColor pup\farmL, 255, 255, 255
	;EntityColor pup\farmR, 255, 255, 255
	
	critArmYaw# = 85
	If (pup\armRYaw > critArmYaw And pup\armLYaw < -critArmYaw) Then
		dPitch# = pup\armRPitch + pup\armLPitch
		If Abs(dPitch) < 20 Then
			;EntityColor pup\farmL, 255, 50, 50
			;EntityColor pup\farmR, 255, 50, 50
			
			If (dPitch < 0) Then
				pup\armRPitch = pup\armRPitch - 0.5
				pup\armLPitch = pup\armLPitch - 0.5
				
				pup\armRYaw = pup\armRYaw - 0.5
				pup\armLYaw = pup\armLYaw - 0.5
			Else
				pup\armRPitch = pup\armRPitch + 0.5
				pup\armLPitch = pup\armLPitch + 0.5
				
				pup\armRYaw = pup\armRYaw + 0.5
				pup\armLYaw = pup\armLYaw + 0.5
			End If
		End If 
	End If
	
	maxHeadYaw# = 80
	maxHeadPitch# = 50
	minHeadPitch# = 30
		
	If (pup\headYaw < -maxHeadYaw) pup\headYaw = -maxHeadYaw
	If (pup\headYaw > maxHeadYaw) pup\headYaw = maxHeadYaw
	
	If (pup\headPitch < -minHeadPitch) pup\headPitch = -minHeadPitch
	If (pup\headPitch > maxHeadPitch) pup\headPitch = maxHeadPitch
	
	
	RotateEntity pup\body, 0, pup\bodyYaw, 0
	RotateEntity pup\head, 0, pup\headYaw, pup\headPitch
	RotateEntity pup\armL, pup\armLPitch, pup\armLyaw+40, -pup\armLyaw * pup\armRoll
	RotateEntity pup\armR, pup\armRPitch, pup\armRyaw-40, pup\armRyaw * pup\armRoll
	RotateEntity pup\farmL, pup\armLPitch, pup\armLyaw*pup\armYaw+20, 0
	RotateEntity pup\farmR, pup\armRPitch, pup\armRyaw*pup\armYaw-20, 0
	
	SetBuffer BackBuffer()
	RenderWorld()
	Color 255,100,0
	y = GraphicsHeight() - 160
	x = 10
	dy = 16
	Text x, y+0*dy, "F1-F4: Set arm roll extent"
	Text x, y+1*dy, "F5-F8: Set arm yaw extent"
	If (KeyDown(200)) Then
		Text x, y+2*dy, "Mouse X/Y: Look around" 
	Else
		Text x, y+2*dy, "Mouse X/Y: Move Arms"
	End If
	Text x, y+3*dy, "Mouse Wheel: Look up/down"
	Text x, y+4*dy, "Mouse LB: Relax Left Arm"
	Text x, y+5*dy, "Mouse RB: Relax Right Arm"	
	Text x, y+6*dy, "Left+Right Arrows: Rotate"	
	Text x, y+7*dy, "Down Arrow: Look at camera"	
	Text x, y+8*dy, "Up Arrow: Switch to look mode"	
	
	Flip
Wend
End


Function quickpuppet.puppet()
	p.puppet = New puppet
	p\body = CreateCube()
	p\head = CreateSphere(8, p\body)
	
	ScaleMesh p\body, 0.5, 1.4, 1
	ScaleMesh p\head, 0.8, 1, 0.7
	PositionMesh p\head, 0, 0.3, 0
	
	eye1 = CreateSphere(4, p\head)
	ScaleEntity eye1, 0.3, 0.3, 0.3
	PositionEntity eye1, 0.55,0.3,-0.3
	eye2 = CreateSphere(4, p\head)
	ScaleEntity eye2, 0.3, 0.3, 0.3
	PositionEntity eye2, 0.55,0.3,0.3
	PositionEntity p\head, 0,2,0
	
	p\armL = CreateCylinder(6, True, p\body)
	RotateMesh p\armL, 90,0,0
	FitMesh p\armL, -0.3, -0.3, 0, 0.6, 0.6, 2.0
	RotateMesh p\armL, 45,0,0
	PositionEntity p\armL, 0, 1, 1
		
	p\armR = CopyMesh(p\armL, p\body)
	RotateMesh p\armR, 0, 180, 0
	PositionEntity p\armR, 0, 1, -1
		
	p\farmL = CopyMesh(p\armL, p\armL)
	PositionEntity p\farmL, 0, -1.5, 1.5
	RotateMesh p\farmL, 20, 0, 30
	
	p\farmR = CopyMesh(p\armR, p\armR)
	PositionEntity p\farmR, 0, -1.5, -1.5
	RotateMesh p\farmR, -20, 10, 30
	
	p\armRollTarget = 0.5
	p\armYawTarget = 1.0
	
	Return p
End Function
