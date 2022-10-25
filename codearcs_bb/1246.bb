; ID: 1246
; Author: Drey
; Date: 2004-12-26 18:16:09
; Title: Unique Hardware-Processed Lighting Environments per entity
; Description: Uses DX hardware lightings management to get around the global 8 light limit. 100 objects and 42 lights

Graphics3D 800, 600,32, 2
SetBuffer BackBuffer()
HidePointer

Global Cam = CreateCamera()
Global ShowActiveLights

SeedRnd MilliSecs()

MoveMouse GraphicsWidth()/2, GraphicsHeight()/2
FlushMouse()


Type MavObject

	Field Pivot
	Field Mesh ; What's important to hidden
	Field X#
	Field Y#
	Field Z#

	Field  ObjectType
	
	
	Field MaxLights;  How many lights can act on it

	Field AmbR; What just a quick option for custom ambient light
	Field AmbG
	Field AmbB

	
	

End Type 

Type MavLight

	Field Pivot ; Something i might end up using later
	Field Mesh ; Something I might end up using later
	Field X#,Y#,Z#; Placement
	

	Field R, G, B ;  Color Values
	Field LightRange# ;  Sets the Range for the light
	Field TriggerRange# ; Sets the Trigger Range, Used to see if it's even worth goin through the check. this is to help take up some processes 

	Field LightType;  for later use, used for what type of light it is.
	Field AngleIn# ; Used for SpotLights, later use
	Field AngleOut#; Used for SpotLights, later use

	Field Static;
	Field Testing

End Type 


Dim LC(8) ; Light Checkers( MavLight Handles) .  basicly, the lights that will end up being used for the lighting system
Dim Dis#(8) ; Parallel with LC, it saves the distance of the light

Dim DyLC(8)
Dim DyDis#(8)

Dim TempCube(8);  if light markers are on




Repeat

	Locate 0,0
	Cls
	Flip 0
	NumOfLights = Input("NumberOfLights(0 to 8) activiated: " )
	
Until NumOfLights > -1 And NumOfLights < 9


ST = CreateTexture(16,16,  4)
SetBuffer TextureBuffer(ST)
	Color 0,0,0
	Rect 0,0,16,16
	Color 255,255,255

	Oval 0, 0,16,16
SetBuffer BackBuffer()


Gray = 32



Mesh = CreateSphere(8)
ScaleEntity Mesh, 2.5, 2.5, 2.5




HideEntity Mesh

Ship.MavObject = New MavObject

Ship\Pivot = CreatePivot()
Ship\Mesh = Mesh

SHip\ObjectType = 1
	ship\MaxLights = NumOfLights 


Ship\AmbR = Gray
ship\AmbG = Gray
Ship\AmbB = Gray

;Not important for the techinque
;{
CamPointPivot = CreatePivot()


EntityParent Ship\Mesh, Ship\Pivot

EntityParent CamPointPivot, Ship\Pivot

EntityParent Cam, CamPOintPivot



MoveEntity Cam, 0, 0, -50


PositionEntity Ship\Pivot, 0,0, 0

CameraRange Cam, 0.01, 5000
;}






; This creates the lights, then hiddens them.  I figure it'll be alil after than creating and destroying lights all the time.
Dim Light(8)

For I = 0 To 8

	Light(I) = CreateLight(2)

	HideEntity Light(I)

Next

ShowActiveLights = 1

Sign = 1

For X = -5 To 4
	For Z = 0 To 9
	
		ObjectCount = ObjectCount + 1
		NO.MavObject = New mavobject ; New Object

		NO\Pivot = CreatePivot()
		NO\Mesh = CopyEntity(Mesh)
		HideEntity No\Mesh 
		NO\MaxLights = NumOfLights

		

		NO\AmbR = Gray 
		NO\AmbG =Gray
		NO\AmbB = Gray

		PositionEntity NO\Mesh, X * 45, 0, Z * 45
		

		No\X = EntityX(No\Mesh)
		No\Y = EntityY(no\mesh)
		No\Z = EntityZ(no\mesh)

	Next
Next 

; Light Creation and Placement

RotationPivot = CreatePivot()
PositionEntity RotationPivot, 0, 0, 3 * 75

LightMesh = CreateSprite()
HideEntity LightMesh
EntityTexture LightMesh, ST



	GLightRange = 45



	GTriggerRange = 150


For X = -3 To 3 Step 1
	For  Z =0 To 5
	count = count + 1

	
	
	L.MavLight = New MavLight

	


	

	
		L\R = Rand(255 * Sign)
		L\B = Rand(255 * Sign)
		L\G = Rand(255 * Sign)

		L\LightRange = GLightRange

		L\TriggerRange = GTriggerRange
	
		
		
		L\X = X * (75) 
		L\Y =  7 ;Rand(5, 10)
		L\Z = Z * (75)

		markCube = CopyEntity(LightMesh)
		HideEntity MarkCube
		PositionEntity MarkCube, L\X, L\Y, L\Z
		ScaleEntity MarkCube, .5, .5, .5
		
			EntityColor MarkCube, Abs(L\R), Abs(L\G),Abs( L\B)

		EntityFX MarkCube, 1
		HideEntity markcube
		L\Mesh = MarkCube
	
		L\Pivot = L\Mesh 
		;PositionEntity L\Pivot, L\X, L\Y, L\Z
		EntityParent L\Pivot, RotationPivot
		

		


	Next
Next


LightStatic = 1

MoSpd# = 1.5
MeshSpd# = 1
Show_FPS_Counter=1
While KeyHit(1) < 1 

		
	
	;MUST OCCUR
	;Camera Refreshes it's Z buffer( color buffer is optional )
	CameraClsMode Cam, 1,1
	RenderWorld(); This renders NOTHING, but puts things in the Z buffer, important for when u render More than one Object.
	
	;Must Occur, make sure that the color and z buffer doesn't refresh one bit
	CameraClsMode Cam, 0, 0
	If CreateMarkers
		RenderLightPlaceHolders()
	EndIf 
	LightObjects(); The lighting rountie
	
	;Mouse wheel allows for a up close look at the action
	MouseZSpeed = MouseZSpeed()
	MoveEntity Cam, 0, 0, MouseZSpeed * 5

	MoveEntity Ship\Pivot, (KeyDown(32) - KeyDown(30))*MeshSpd, (KeyDown(57) - KeyDown(29)) * MeshSpd, (KeyDown(17) - KeyDown(31)) * MeshSpd

	If MouseDown(2) ;Mouse Button 2 allows for All angle camera movement
		
		TurnEntity CamPointPivot, -MouseYSpeed() * MoSpd,  -MouseXSpeed() * MoSpd, 0
		RotateEntity CamPointPivot, EntityPitch(CamPointPivot), EntityYaw(CamPointPivot), 0

	Else

		TurnEntity Ship\Pivot,0, -MouseXSpeed() * MoSpd, 0
EndIf

	If MouseDown(3) ; Somewhat recenters the camera

		RotateEntity CamPointPivot, 25, 0,0
		
	EndIf 

	TurnEntity RotationPivot, 0, rotationspeed/100.0, 0

	If KeyDown(76)

		RotationSpeed = 0
		LightStatic = 1
		static(LightStatic)

	EndIf 

	RotationTest = (KeyDown(75) - KeyDown(77))

	If RotationTest <> 0
	
		Rotationspeed = rotationspeed + RotationTest
		
		If LightStatic = 1 Then
			LightStatic = 0
			Static(0)
		EndIf
	EndIf 
	
	;recenters mouse
	MoveMouse GraphicsWidth()/2, GraphicsHeight()/2




	NewLightRange# = (KeyDown(205) - KeyDown(203))

	If newLightRange <> 0 Then
	
		GLightRange  = GLightRange + NewLightRange
		AddLightRangeAll(NewLightRange)

	EndIf
	
	NewTriggerRange# =  (KeyDown(200) - KeyDown(208))


	If NewTriggerRange <> 0 Then

		GTriggerRange = GTriggerRange + NewTriggerRange
		AddTriggerRangeAll(NewTriggerRange)

	EndIf 


	If KeyHit(50) Then
		CreateMarkers = Not createMarkers
	EndIf

	If KeyHit(49) Then
		ShowActiveLights = Not ShowActiveLights
	EndIf 

	;Cheap instaneous FPS rate
    If Show_FPS_Counter = True Then
       
        EndingFPS = MilliSecs()
        MilliDif% = EndingFPS - StartingFPS
       
        If MilliDif < 1 Then
            MilliDif = 1
       EndIf

       FPS_Count = 1000/MilliDif
       
       Text 0,0, "FPS: " + FPS_Count
      
    EndIf

StartingFPS = MilliSecs()
If (MilliSecs() - MilliLast) > 1000
	AverFPS = FPSCount
	MilliLast = MilliSecs()
	FPSCount = 0
Else
	FPSCount = FPSCount + 1
EndIf

	Text 0, 20, "Object Count: " + ObjectCount
	Text 0, 10, "Lights: " +  count 
	
	Text 0, 40, "General Light Range: " +  GLightRange
	Text 0, 50, "General Trigger Range: " + GTriggerRange
	Text 0, 60, "Rotation Speed: " + RotationSpeed
	Text 0, 70, "Aver FPS: " + AverFPS

	For I = 0 To ( NumOfLights -1  ) 

		Text 0, I * 10 + 80, "Light " + DyLC(I)  + " Dis: " + DyDis(I)

	Next 
	
	

Flip False
 




Wend
ShowPointer

End





Function LightObjects()



	;For every mesh u put in MavObjects
	For Obj.MavObject = Each MavObject 

		;Of course, this is important
		ShowEntity Obj\Mesh 

		;for now, i just slapped this on the mesh
		AmbientLight Obj\AmbR, obj\AmbG , obj\AmbB
		
		;Dims all 
		Dim LC(obj\maxlights)
		;A cheap lil way to make sure it grabs some near by lights.  
				
		For L.MavLight = Each MavLight


		ActiveShown = 0
				
			NewDis = EntityDistance(Obj\Mesh, L\Pivot)
				
				
				
				
			If NewDis < (L\TriggerRange)

				If Not L\Static
					TFormPoint 0,0,0, L\Pivot, 0
					
					L\X = TFormedX()
					L\Y = TFormedY()
					L\Z = TFormedZ()
				EndIf
				
					;This system here see how the NewDistance Compares with the current once.
					;The system is built on a stack idea.
					;Example:
					;ArrayNumber - Handle - Distance
					;   0-1-30
					;   1-2-40
					;   2-26-50
					;If NewDis = 35 and MavLight Handle is 5, this basicly happens
					; 0-1-30
					; 1-5-35
					; 2-2-40
					;The 26-50 gets pushed upward.  The idea is the closest light is 0 and the farthest is Obj\MaxLights-1
				

					
					
					For X = 0 To Obj\MaxLights - 1
				
						

							If LC(X) = 0 Then

								DIS(X) = NewDis
								LC(X) = Handle(L)

        Exit

							Else 

								If Dis(X) >= NewDis And LC(X) <> Handle(L)
								
									
									;The stack push system
									
									For II = Obj\maxLights - 2 To X Step -1

										LC(II + 1 ) = LC(II )
										Dis(II + 1) = Dis(II)
									Next
									Dis(X) = NewDis
									LC(X) = Handle(L)
									
									
									Exit

								EndIf
								
							EndIf
							
						Next
						
				EndIf
				
		Next
		
		For I = 0 To Obj\MaxLights -1
		
			CurMavLight.MavLight = Object.MavLight( LC( I ) )
			
			If CurMavLight <> Null
				; If U want the current lights to have temp makers

				LightCounter = LightCounter + 1
				If ShowActiveLights 

					If Obj\ObjectType Then

						

						ActiveShown = 1
						TempCube(i) = CreateCube()
						EntityColor TempCube(i), CurMavLight\R, CurMavLight\G, CurMavLight\B
						PositionEntity TempCube(i), CurMavLight\X, CurMavLight\Y, CurMavLight\Z
				
						EntityFX TempCube(i), 1
						;ScaleEntity TempCube(i), 1.5, 1.5, 1.5
					EndIf 
				EndIf

				;Where all the light placement happens
				PositionEntity Light(I), CurMavLight\X, CurMavLight\Y, CurMavLight\Z
				LightColor Light(I), CurMavLight\R, CurMavLight\G, CurMavLight\B
				LightRange Light(I), CurMavLight\LightRange
				ShowEntity Light(I)
				
			EndIf
			
		Next
		
		
		RenderWorld()


		;resets everything
		If ActiveShown
		
			For I = 0 To LightCounter -1
				FreeEntity TempCube(i)
			Next 

		EndIf 


		If  Obj\ObjectType = 1

			For I = 0 To Obj\MaxLights - 1

				DyLC(I) = LC(I)
				DyDis(I) = Dis(I)

			Next

		EndIf 
		
		For i = 0 To Obj\MaxLights

				HideEntity Light(I)
			
		Next


		;Hides the Entity
		HideEntity Obj\Mesh 

		
		
	Next
	

End Function

Function RenderLightPlaceHolders()

	For CurLight.MavLight = Each MavLight

		ShowEntity CurLight\Mesh 

		RenderWorld()

		HideEntity CurLight\Mesh

	Next
	
End Function

Function AddLightRangeALL(AddThis#)

	For L.MavLight = Each MavLight

		L\LightRange = L\LightRange + AddThis

	Next 

End Function

Function AddTriggerRangeALL(AddThis#)

	For L.MavLight = Each MavLight

		L\TriggerRange = L\TriggerRange + AddThis

	Next 

End Function

Function Static(value)

	
	For L.MavLight = Each MavLight

		L\Static = value

		If  Value = 0
		
			L\X = EntityX(L\Pivot)
			L\Y = EntityY(L\Pivot)
			L\Z = EntityZ(L\Pivot)
			
		EndIf 

	Next 


End Function
