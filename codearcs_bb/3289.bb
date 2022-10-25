; ID: 3289
; Author: Prym
; Date: 2016-10-07 11:53:23
; Title: demopivorbi4bis
; Description: planet attraction 2

;;;; 
;;;; demopivorbi4bis
;;;; demo pivot orbital bis 
;;;; 
;;;; pivorbiplayer0 now keeps attached to player0 
;;;; 

Global GW% = 800 , GH% = 600 , type_ellipsoide% = 1 
Global spaceplanets% = 0 , x% = -2 , y% = -2 , z% = -2 
Global parentplayer0% = 0 , player0% = 0 , camera% = 0 
Global pivotplanete4% = 0 , pivorbiplayer0% = 0 
Global planete4% = 0 , textureplanete4% = 0 
Global rndscale% = 0 , cubeplanete4% = 0  
Global mvx% = 0 , mvz% = 0 , vitesse% = 100 
Global deplacy# = 0.0 , deplacx# = 0.0 
Global campitch# = 0.0 , camyaw# = 0.0 , camroll# = 0.0 
Global oldpitch# = 0.0 , oldyaw# = 0.0 , oldroll# = 0.0 
Global pitchorbi% = 0 , yaworbi% = 0 , rollorbi% = 0 
Global Yplayer0 = 0 
Global frameTimer% = CreateTimer ( 15 ) 

Graphics3D GW , GH , 32 , 2 

;textureplanete4 
textureplanete4 = CreateTexture ( 32 , 32 ) 
SetBuffer TextureBuffer ( textureplanete4 ) 
	ClsColor 100 , 110 , 130 
	Color 200 , 190 , 180 
	Cls 
	Text 0 ,  0 , "++++" 
	Text 0 ,  8 , "++++" 
	Text 0 , 16 , "++++" 
	Text 0 , 24 , "++++" 
SetBuffer BackBuffer ( ) 
;player 
pivorbiplayer0 = CreatePivot ( ) 
player0 = CreateSphere ( ) 
EntityRadius player0 , 100 
EntityType player0 , type_ellipsoide 
ScaleEntity player0 , 10 , 10 , 10 
camera = CreateCamera ( player0 ) 
CameraViewport camera , 0 , 0 , GW , GH 
PositionEntity player0 , 0 , 0 , -5000 
;scene 
For x = -2 To 2 Step 4 
For y = -2 To 2 Step 4 
For z = -2 To 2 Step 4 
rndscale = Rand ( 5 , 30 ) 
spaceplanets = CreateSphere ( ) 
ScaleEntity spaceplanets , 10 * rndscale , 10 * rndscale , 10 * rndscale 
EntityColor spaceplanets , Rand ( 100 , 150 ) , Rand ( 100 , 200 ) , Rand ( 100 , 250 ) 
PositionEntity spaceplanets , 100 * x * Rand ( 5 , 20 ) , 100 * y * Rand ( 5 , 20 ) , 100 * z * Rand ( 5 , 20 ) 
Next : Next : Next 
;planete4 
pivotplanete4 = CreatePivot ( ) 
PositionEntity pivotplanete4 , 0 , 0 , 0 
planete4 = CreateSphere ( 32 , pivotplanete4 ) 
EntityRadius planete4 , 1200 
EntityType planete4 , type_ellipsoide 
ScaleEntity planete4 , 1000 , 1000 , 1000 
EntityTexture planete4 , textureplanete4 
cubeplanete4 = CreateCube ( pivotplanete4 ) 
ScaleEntity cubeplanete4 , 100 , 100 , 100 
EntityColor cubeplanete4 , Rand ( 100 , 150 ) , Rand ( 50 , 200 ) , Rand ( 50 , 250 ) 
PositionEntity cubeplanete4 , 1000 , 0 , 0 
cubeplanete4 = CreateCube ( pivotplanete4 ) 
ScaleEntity cubeplanete4 , 100 , 100 , 100 
EntityColor cubeplanete4 , Rand ( 100 , 150 ) , Rand ( 50 , 200 ) , Rand ( 50 , 250 ) 
PositionEntity cubeplanete4 , 0 , 1000 , 0 
cubeplanete4 = CreateCube ( pivotplanete4 ) 
ScaleEntity cubeplanete4 , 100 , 100 , 100 
EntityColor cubeplanete4 , Rand ( 100 , 150 ) , Rand ( 50 , 200 ) , Rand ( 50 , 250 ) 
PositionEntity cubeplanete4 , 0 , 0 , 1000 

HidePointer
Collisions type_ellipsoide , type_ellipsoide , 1 , 3 
; api_ShowWindow(SystemProperty$("AppHWND"), 1) ; avec GDI32.decls 

While Not KeyHit ( 1 ) 

		WaitTimer(frameTimer) ; Pause 1/15 sec

		TurnEntity pivotplanete4 , 0.0 , 1.0 , 0.0 ; pivorbi4bis.exe : pivotplanete4 can keep turning , now .

		;pivot ref change 
		If parentplayer0 = 0 Then 
			If EntityDistance ( player0 , pivotplanete4 ) < 3000 Then 
				oldpitch# = EntityPitch# ( player0 , 1 ) 
				oldyaw# = EntityYaw# ( player0 , 1 ) 
				oldroll# = EntityRoll# ( player0 , 1 ) 
					EntityParent pivorbiplayer0 , pivotplanete4 , 0 
					EntityParent player0 , pivotplanete4 , 1 
					AlignToVector pivorbiplayer0 , EntityX# ( player0 ) , EntityY# ( player0 ) , EntityZ# ( player0 ) , 2 
					Yplayer0 = EntityY ( player0 ) 
					EntityParent player0 , pivorbiplayer0 , 1 
					PositionEntity player0 , 0 , Yplayer0 , 0 
					RotateEntity player0 , oldpitch# , oldyaw# , oldroll# , 1 
					RotateEntity camera , 30.0 , 0.0 , 0.0 
				;memo local rotations 
				campitch# = EntityPitch# ( player0 , 0 ) 
				camyaw# = EntityYaw# ( player0 , 0 ) 
				camroll# = 0 ; EntityRoll# ( player0 , 0 ) 
				parentplayer0 = 4 
			EndIf 
			Delay 20 
		ElseIf parentplayer0 = 4 Then 
			If EntityDistance ( player0 , pivotplanete4 ) > 3200 Then 
				oldpitch# = EntityPitch# ( player0 , 1 ) 
				oldyaw# = EntityYaw# ( player0 , 1 ) 
				oldroll# = EntityRoll# ( player0 , 1 ) 
					Yplayer0 = 0 
					EntityParent player0 , 0 
					EntityParent pivorbiplayer0 , 0 
					RotateEntity player0 , oldpitch# , oldyaw# , oldroll# , 1 
					RotateEntity camera , 0.0 , 0.0 , 0.0 
				;memo local rotations 
				campitch# = EntityPitch# ( player0 , 0 ) 
				camyaw# = EntityYaw# ( player0 , 0 ) 
				camroll# = 0 ; EntityRoll# ( player0 , 0 ) 
				parentplayer0 = 0 
			EndIf 
			Delay 20 
		EndIf 
		
		mvx = 0 
		mvz = 0 
		If KeyDown ( 200 ) Or KeyDown ( 17 ) Then mvz =   vitesse 
	   	If KeyDown ( 208 ) Or KeyDown ( 31 ) Then mvz = - vitesse 
		If KeyDown ( 205 ) Or KeyDown ( 32 ) Then mvx =   vitesse 
		If KeyDown ( 203 ) Or KeyDown ( 30 ) Then mvx = - vitesse 
		campitch# = ( campitch# + deplacy# ) ; Mod 360 
		camyaw#   = ( camyaw#   - deplacx# ) ; Mod 360 
		deplacy# = MouseYSpeed ( ) * 0.1 
		deplacx# = MouseXSpeed ( ) * 0.1 

		;mouvements player 
		Collisions type_ellipsoide , type_ellipsoide , 1 , 3 
		Yplayer0 = EntityY ( player0 ) 
		If parentplayer0 = 0 Then 
			RotateEntity player0 , campitch , camyaw , 0 ; camroll# ; 
			MoveEntity player0 , mvx , 0 , mvz 
		ElseIf parentplayer0 = 4 Then 
			PositionEntity player0 , 0.0 , Yplayer0 -0.8*mvz*Sin(campitch) , 0.0 
			RotateEntity player0 , campitch , 0.0 , 0.0 ; , camroll# ; 
			TurnEntity pivorbiplayer0 , 0.0 , -deplacx , 0.0 
			TurnEntity pivorbiplayer0 , mvz*0.08 , 0.0 , -mvx*0.08 
		EndIf 

		UpdateWorld 
		RenderWorld 

		If parentplayer0 = 0 Then 
			Text GW/4 , 100 , "Free cosmos" , 1 
		ElseIf parentplayer0 = 4 Then 
			Text 3*GW/4 , 100 , "Planet orbit" , 1 
		EndIf 
		MoveMouse GW / 2 , GH / 2 

		Flip 
		
Wend 

End 

;;;; 
;;;; parapry 2009-2016
;;;;
