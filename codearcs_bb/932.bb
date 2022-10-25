; ID: 932
; Author: Rogue Vector
; Date: 2004-02-11 14:07:31
; Title: Compass Class
; Description: A pseudo object oriented module

.CLASS_Compass


.INFO_Compass
;Open Source Code
;By Rogue Vector 2004
;Requires Graphics Mode and Backbuffer to be initialised
;Requires a pointer to a valid Camera object
;Requires two image files: compass.jpg , needle.jpg
;Images must have dimensions of 256x256 pixels
;Black:RGB(0,0,0) will be 100% transparent 


.CONSTANTS_Compass
Const FAILURE=0
Const SUCCESS=1


.PUBLIC_Compass
;<NONE>

.PROTECTED_Compass
Type TCompassVector

	Field mXpos#
	Field mYpos#
	Field mZpos#

End Type


Type TCompass

	Field Protected_mNorthPole.TCompassVector
	Field Protected_mNorthPoleEntity
	Field Protected_mDummyObj
	Field Protected_mCompassSprite
	Field Protected_mNeedleSprite
	Field Protected_mHUD
	Field Protected_mHUDAspect#
	Field Protected_mHUDScale#
	Field Protected_mHUDCompass
	Field Protected_mHUDNeedle
	Field Protected_mAngle#
	Field Protected_mVisible%
	
End Type


.CONSTRUCTORS_Compass
Function Compass_Create.TCompass(v_cam, v_path$, v_scrwidth#, v_scrheight#, v_xpos#, v_ypos#, v_uniscale#=0.125)

	Local this.TCompass = New TCompass
	
	this\Protected_mHUD = CreatePivot(v_cam) 				
	this\Protected_mHUDAspect = Float(v_scrheight) / v_scrwidth
	PositionEntity this\Protected_mHUD, -1, this\Protected_mHUDAspect, 1
	this\Protected_mHUDScale = 2.0 / v_scrwidth
	ScaleEntity this\Protected_mHUD, this\Protected_mHUDScale, -this\Protected_mHUDScale, this\Protected_mHUDScale
	
	this\Protected_mCompassSprite = LoadSprite(v_path + "compass.jpg" )
	If (this\Protected_mCompassSprite = 0) Then RuntimeError "file: " + v_path + "compass.jpg ...does not exist"
	HideEntity this\Protected_mCompassSprite	
	
	this\Protected_mNeedleSprite = LoadSprite(v_path + "needle.jpg" )
	If (this\Protected_mNeedleSprite = 0)  Then RuntimeError "file: " + v_path + "needle.jpg ...does not exist"

	HideEntity this\Protected_mNeedleSprite	
		
	this\Protected_mHUDCompass = CopyEntity(this\Protected_mCompassSprite, this\Protected_mHUD) 
			
	ScaleSprite this\Protected_mHUDCompass, v_uniscale, v_uniscale	
	EntityAlpha this\Protected_mHUDCompass, 0.5		
	
	EntityOrder this\Protected_mHUDCompass, -2			
	ShowEntity 	this\Protected_mHUDCompass
	
	this\Protected_mHUDNeedle = CopyEntity(this\Protected_mNeedleSprite, this\Protected_mHUD) 
	ScaleSprite this\Protected_mHUDNeedle, v_uniscale, v_uniscale	
	EntityAlpha this\Protected_mHUDNeedle, 0.5	
	
	EntityOrder this\Protected_mHUDNeedle, -2			
	ShowEntity 	this\Protected_mHUDNeedle			
	
	FreeEntity this\Protected_mCompassSprite
	FreeEntity this\Protected_mNeedleSprite
	
	this\Protected_mAngle = 0.0
		
	PositionEntity this\Protected_mHUDCompass, v_xpos, v_ypos,    1
	PositionEntity this\Protected_mHUDNeedle,  v_xpos, v_ypos,    1
	
	this\Protected_mDummyObj = CreatePivot()
	
	this\Protected_mVisible = True
	
	Return this

End Function


.DESTRUCTORS_Compass
Function Compass_Destroy(v_object.TCompass)
	
	If (Handle v_object)
	
		If (v_object\Protected_mHUDCompass<>0) FreeEntity v_object\Protected_mHUDCompass
		If (v_object\Protected_mHUDNeedle<>0)  FreeEntity v_object\Protected_mHUDNeedle
		If (v_object\Protected_mNorthPoleEntity<>0) FreeEntity v_object\Protected_mNorthPoleEntity
		If (v_object\Protected_mDummyObj<>0) FreeEntity v_object\Protected_mDummyObj
		
		Delete v_object
	
		Return SUCCESS
			
	EndIf
	
	Return FAILURE

End Function


.METHODS_Compass
Function Compass_SetNorthPole(v_object.TCompass, v_cam, v_posX#, v_posY#, v_posZ#)

	If (Handle v_object)
	
		v_object\Protected_mNorthPole = New TCompassVector
		v_object\Protected_mNorthPole\mXpos = v_posX
		v_object\Protected_mNorthPole\mYpos = v_posY
		v_object\Protected_mNorthPole\mZpos = v_posZ
	
		v_object\Protected_mNorthPoleEntity = CreatePivot()
		PositionEntity v_object\Protected_mNorthPoleEntity, v_object\Protected_mNorthPole\mXpos, v_object\Protected_mNorthPole\mYpos, v_object\Protected_mNorthPole\mZpos
		PointEntity v_cam, v_object\Protected_mNorthPoleEntity
		PointEntity v_object\Protected_mDummyObj, v_object\Protected_mNorthPoleEntity

		Return SUCCESS
			
	EndIf
	
	Return FAILURE

End Function


Function Compass_GetNorthPole.TCompassVector(v_object.TCompass)

	If (Handle v_object)
	
		Return v_object\Protected_mNorthPole
	
	Else
	
		Return Null
		
	EndIf

End Function


Function Compass_SetAlphaBlend(v_object.TCompass, v_cmpalpha#=1.0, v_ndlalpha#=1.0, v_cmpblend%=1, v_ndlblend%=0)

	If (Handle v_object)

		EntityAlpha v_object\Protected_mHUDCompass, v_cmpalpha
		EntityBlend v_object\Protected_mHUDCompass, v_cmpblend
		EntityAlpha v_object\Protected_mHUDNeedle,  v_ndlalpha
		
		If (v_ndlblend) Then EntityBlend v_object\Protected_mHUDNeedle,  v_ndlblend
		
		Return SUCCESS
	
	End If

	Return FAILURE

End Function


Function Compass_Update(v_object.TCompass, v_cam)

	PositionEntity v_object\Protected_mDummyObj, EntityX(v_cam), EntityY(v_cam), EntityZ(v_cam)
	PointEntity v_object\Protected_mDummyObj, v_object\Protected_mNorthPoleEntity
	
	RotateSprite v_object\Protected_mHUDNeedle, v_object\Protected_mAngle
	v_object\Protected_mAngle = EntityYaw(v_cam) - EntityYaw(v_object\Protected_mDummyObj)
		
	If (v_object\Protected_mAngle < 0) Then v_object\Protected_mAngle = 360.0 + v_object\Protected_mAngle
		
	If (v_object\Protected_mVisible)
	
		ShowEntity v_object\Protected_mHUDCompass
		ShowEntity v_object\Protected_mHUDNeedle
		
	Else
	
		HideEntity v_object\Protected_mHUDCompass
		HideEntity v_object\Protected_mHUDNeedle
	
	EndIf

End Function


Function Compass_Show()

	Protected_mVisible = True

End Function


Function Compass_Hide()

	Protected_mVisible = False

End Function



.ENDCLASS_Compass

;-------------------------------------------
;Compass Class Test Program
;By Rogue Vector 2004


Type TFlakes
 Field x#
 Field y#
 Field c
End Type


.CONSTANTS_testprog
Const TYPE_OBJECT=1
Const TYPE_WORLD =2
Const ELLIPSOID_TO_ELLIPSOID=1
Const ELLIPSOID_TO_POLYGON=2
Const ELLIPSOID_TO_BOX=3
Const COLLISION_STOP=1
Const COLLISION_FULL_SLIDE=2
Const COLLISION_NO_SLIDE=3
Const TOTALFLAKES=800
Const FPS=60


.INITIALISATION_testprog
AppTitle "Compass Class Test Program"
Graphics3D 800,600,16
SetBuffer BackBuffer()
Include "Compass.bb"


.GLOBALS_testprog
Global g_framePeriod#  = 1000 / FPS
Global g_frameTime#    = MilliSecs () - g_framePeriod
Global g_animspeed#   = 0.05
Global g_scrwidth#    = GraphicsWidth()
Global g_scrheight#   = GraphicsHeight()
Global g_graphmidX#  = GraphicsWidth()/2
Global g_graphmidY#  = GraphicsHeight()/2
Global g_cam = InitCamera()
Global g_cameraX#=0.0
Global g_cameraY#=0.0
Global g_cameraZ#=0.0
Global g_terrain = CreateTerrainscape()
Global g_plane   = 0
Global g_clouds  = CreateCloudPlane()
Global g_compass.TCompass = Null
Global g_mouseXspeed#=0.0
Global g_mouseYspeed#=0.0
Global g_mouseRoll#=0.0
Global g_mousePitch#=0.0
Global g_fpsMilli#=MilliSecs()
Global g_fpsCounter%=0
Global g_updateFrequency%=10
Global g_fps%=0


;set compass
Global g_compassX# = Float(g_scrwidth)  / 14
Global g_compassY# = g_scrheight - Float(g_scrheight) / 11
g_compass = Compass_Create(g_cam, "", g_scrwidth, g_scrheight, g_compassX, g_compassY)
Compass_SetAlphaBlend(g_compass, 0.5, 0.7, 1)
Compass_SetNorthPole(g_compass, g_cam, 1744.42, 40, 4601.18)


;set environment
Global g_polemodel = LoadMesh("northpole.3ds")
PositionEntity g_polemodel, 1744.42, 40, 4601.18
InitSnowFlakes()
AmbientLight 200,200,200	
ClsColor 200,200,200
HidePointer


.MAINLOOP_testprog
Repeat
		
	Repeat
		l_frameElapsed = MilliSecs () - g_frameTime
	Until l_frameElapsed
	
	Cls
	
	l_frameTicks = l_frameElapsed / g_framePeriod
	
	l_frameTween = Float (l_frameElapsed Mod g_framePeriod) / Float (g_framePeriod)
		
	For l_frameLimit = 1 To l_frameTicks
	
		If l_frameLimit = l_frameTicks Then CaptureWorld
		g_frameTime = g_frameTime + g_framePeriod
			
		UpdateGame ()
				
		UpdateFrameRate()
		
		UpdateWorld
	
	Next
		
	If KeyHit (17): w = 1 - w: WireFrame w: EndIf ; Press 'W'
		
	RenderWorld l_frameTween
	
	Compass_Update(g_compass, g_cam)		
	
	UpdateSnowFlakes()
		
	Color 0,0,255
	Text 5, 5, "Compass Class Test Program"
	Text 5, 20, "By Rogue Vector"
	Text 5, 35, "Frame Rate = " + g_fps
	Text 6, 50, "Compass needle always points north."
	Text 5, 65, "Head North to reach the pole..."
		
	Flip 

Until KeyHit (1)


.SHUTDOWN_testprog
FreeEntity g_polemodel
FreeEntity g_terrain
FreeEntity g_clouds
FreeEntity g_plane
DestroySnowFlakes()
Compass_Destroy(g_compass)
ClearWorld()
EndGraphics


.END_testprog
End


.FUNCTIONS_testprog
Function InitCamera()

	Local l_cam = CreateCamera()
	CameraViewport l_cam, 0, 0, GraphicsWidth(), GraphicsHeight()
	CameraZoom  l_cam,1
	CameraRange l_cam,1, 6000
	EntityType l_cam, TYPE_OBJECT
	EntityRadius l_cam, 1.4
	CameraFogMode  l_cam,1
	CameraFogColor l_cam,200,200,200
	CameraClsMode  l_cam, False, True  	
	CameraFogRange l_cam,0, 3000
	
	PositionEntity l_cam, 1673.23,129.002,570.286
	
	Collisions TYPE_OBJECT, TYPE_WORLD, ELLIPSOID_TO_POLYGON, COLLISION_NO_SLIDE  
	ResetEntity l_cam
	
	Return l_cam
	
End Function


Function CreateTerrainscape()
	
	Local l_terrain=LoadTerrain("heightmap_256.bmp")
	ScaleEntity l_terrain,20,600,20
	TerrainDetail l_terrain,800,1
	EntityPickMode l_terrain, 2, True
	Local l_map=LoadTexture("icefield.jpg",9)
	ScaleTexture l_map,20,20
	TextureBlend l_map,2
	EntityTexture l_terrain,l_map,0,1
	
	g_plane = CreatePlane(1, l_terrain)
	EntityTexture g_plane, l_map,0,1
	PositionEntity  g_plane, 0, -0.1, 0
	FreeTexture l_map
	Return l_terrain

End Function


Function CreateCloudPlane()

	Local l_map=LoadTexture("cloud.bmp",1)
	ScaleTexture l_map,1000,1000
	Local l_cloudplane =CreatePlane()
	EntityTexture l_cloudplane,l_map
	RotateEntity l_cloudplane,0,0,180
	EntityAlpha l_cloudplane,0.8
	PositionEntity l_cloudplane,0,800,0
	FreeTexture l_map
	
	Return l_cloudplane

End Function


Function UpdateGame ()

	;Process keyboard input
	If KeyDown(200)=True Then MoveEntity g_cam,0,0,5			                           	; Up
	If KeyDown(208)=True Then MoveEntity g_cam,0,0,-5                          				; Down		
	If KeyDown(205)=True Then MoveEntity g_cam,5,0,0                        				; Right (Sidestep)
	If KeyDown(203)=True Then MoveEntity g_cam,-5,0,0                 		         		; Left (Sidestep)
	If KeyDown(76)=True Then TurnEntity  g_cam,-EntityPitch#(g_cam),0,-EntityRoll#(g_cam) 	; center look

	g_cameraX#=EntityX#(g_cam)
	g_cameraY#=EntityY#(g_cam)
	g_cameraZ#=EntityZ#(g_cam)
	
	l_terrainY#=TerrainY#(g_terrain, g_cameraX, g_cameraY, g_cameraZ)+40
	
	PositionEntity g_cam, g_cameraX, l_terrainY, g_cameraZ

	
	;Process mouse movement for in-game action
	g_mouseXspeed = g_mouseXspeed * 0.9 + MouseXSpeed()
	g_mouseYspeed = g_mouseYspeed * 0.9 + MouseYSpeed()
		
	 
	MoveMouse g_graphmidX, g_graphmidY
	
		
	TurnEntity g_cam, +(g_mouseYspeed * 2) * g_animspeed, -(g_mouseXspeed * 2) * g_animspeed, 0
	
	g_mouseRoll=EntityRoll#(g_cam)
	
	If (g_mouseRoll<>0) Then TurnEntity g_cam,0,0,-g_mouseRoll
	 
	; Restriction looking up
	g_mousePitch=EntityPitch#(g_cam)
		If g_mousePitch > 50
		g_mousePitch = g_mousePitch - 50
		TurnEntity g_cam,-g_mousePitch * g_animspeed, 0, 0
	EndIf 
	 
	; Restriction looking down
	If g_mousePitch < -75
		g_mousePitch = g_mousePitch + 75
		TurnEntity g_cam,-g_mousePitch * g_animspeed, 0, 0
	EndIf
	
	MoveEntity g_clouds, 48*g_animspeed, 0, 48*g_animspeed
	
	FlushMouse
	
End Function


Function InitSnowFlakes()

	SeedRnd MilliSecs()
	
	For x = 1 To TOTALFLAKES

	   flake.TFlakes = New TFlakes
	   flake\x#=Rnd(g_scrwidth,-70)
	   flake\y#=Rnd(g_scrheight,0)
	   flake\c=Rnd(4,0)

	Next
	
End Function


Function UpdateSnowFlakes()
	
  For flake.TFlakes = Each TFlakes

   If flake\y#>g_scrheight 
 
	flake\x#=Rnd(g_scrwidth,-70)
    flake\y#=0
	flake\c=Rnd(4,0)

   End If

     Select flake\c    
      Case 1
       Color 255,255,255
       dir=Rnd(-.5,1)
       flake\x#=flake\x#+dir+.1
       flake\y#=flake\y#+.8
       Oval flake\x#,flake\y#,1,1,1
      Case 2
       Color 250,250,250
       dir=Rnd(-1,1.5)
       flake\x#=flake\x#+dir+.1
       flake\y#=flake\y#+1
       Oval flake\x#,flake\y#,2,2,1
      Case 3
       Color 245,245,245
       dir=Rnd(-1,2)
       flake\x#=flake\x#+dir+.1
       flake\y#=flake\y#+1.5
       Oval flake\x#,flake\y#,3,3,1
      Case 4
       Color 255,255,255
       dir=Rnd(-2,2.6)
       flake\x#=flake\x#+dir+.1
       flake\y#=flake\y#+2
       Oval flake\x#,flake\y#,4.5,4.5,1
     End Select   
 
Next

End Function


Function DestroySnowFlakes()

	For flake.TFlakes = Each TFlakes
	
		Delete flake
	
	Next
	
End Function

	
Function UpdateFrameRate()

	g_fpsCounter = g_fpsCounter + 1
	
	If (g_fpsCounter = g_updateFrequency)
		
		g_fps = 1000 / Float(((MilliSecs() - g_fpsMilli)) / g_updateFrequency)
		g_fpsMilli = MilliSecs()
		g_fpsCounter = 0	
	
	EndIf

End Function
