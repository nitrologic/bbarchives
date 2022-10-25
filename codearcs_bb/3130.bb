; ID: 3130
; Author: Prym
; Date: 2014-06-10 07:42:19
; Title: demo pivot orbi
; Description: planet attraction

;;;; 
;;;; demo pivot orbital 
;;;; 

Global mouse_move_speed# = 0.25
Global GW% = 800 , GH% = 600 , type_ellipsoide% = 1 
Global spaceplanet% = 0 , x% = -2 , y% = -2 , z% = -2 
Global parentplayer0% = 0 , player0% = 0 , camera% = 0 
Global pivotplanete4% = 0 , pivotorbiplanete4% = 0 
Global planete4% = 0 , textureplanete4% = 0 , rndscale% = 0 
Global mvx% = 0 , mvz% = 0 , vitesse% = 10 
Global deplacy# = 0.0 , deplacx# = 0.0 
Global campitch# = 0.0 , camyaw# = 0.0 , camroll# = 0.0 
Global oldpitch# = 0.0 , oldyaw# = 0.0 , oldroll# = 0.0 

Graphics3D GW , GH , 32 , 2 
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
parentplayer0 = CreatePivot ( ) 
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
spaceplanet = CreateSphere ( ) 
ScaleEntity spaceplanet , 10 * rndscale , 10 * rndscale , 10 * rndscale 
EntityColor spaceplanet , Rand ( 100 , 150 ) , Rand ( 50 , 200 ) , Rand ( 50 , 250 ) 
PositionEntity spaceplanet , 100 * x * Rand ( 5 , 20 ) , 100 * y * Rand ( 5 , 20 ) , 100 * z * Rand ( 5 , 20 ) 
Next : Next : Next 
;planete4 
pivotplanete4 = CreatePivot ( ) 
PositionEntity pivotplanete4 , 0 , 0 , 0 
pivotorbiplanete4 = CreatePivot ( pivotplanete4 ) 
planete4 = CreateSphere ( 32 , pivotplanete4 ) 
EntityRadius planete4 , 1100 
EntityType planete4 , type_ellipsoide 
ScaleEntity planete4 , 1000 , 1000 , 1000 
EntityTexture planete4 , textureplanete4 
Collisions type_ellipsoide , type_ellipsoide , 1 , 3 

HidePointer
; api_ShowWindow(SystemProperty$("AppHWND"), 1) ; with user32.dll 

While Not KeyHit ( 1 ) 

		;pivot ref change 
		If parentplayer0 = 0 Then 
			If EntityDistance ( player0 , pivotplanete4 ) < 3000 Then 
				oldpitch# = EntityPitch# ( player0 , 1 ) 
				oldyaw# = EntityYaw# ( player0 , 1 ) 
				oldroll# = EntityRoll# ( player0 , 1 ) 
				EntityParent player0 , pivotplanete4 , 1 
				AlignToVector pivotorbiplanete4 , EntityX ( player0 ) , EntityY ( player0 ) , EntityZ ( player0 ) , 2 
				EntityParent player0 , pivotorbiplanete4 , 1 
				RotateEntity player0 , oldpitch# , oldyaw# , oldroll# , 1 
				;memo local rotations 
				campitch# = EntityPitch# ( player0 , 0 ) 
				camyaw# = EntityYaw# ( player0 , 0 ) 
				camroll# = 0 ; EntityRoll# ( player0 , 0 ) 
			EndIf 
		ElseIf parentplayer0 = pivotorbiplanete4 Then 
			If EntityDistance ( player0 , pivotplanete4 ) > 3200 Then 
				oldpitch# = EntityPitch# ( player0 , 1 ) 
				oldyaw# = EntityYaw# ( player0 , 1 ) 
				oldroll# = EntityRoll# ( player0 , 1 ) 
				EntityParent player0 , 0 , 1 
				RotateEntity player0 , oldpitch# , oldyaw# , oldroll# , 1 
				;memo local rotations 
				campitch# = EntityPitch# ( player0 , 0 ) 
				camyaw# = EntityYaw# ( player0 , 0 ) 
				camroll# = 0 ; EntityRoll# ( player0 , 0 ) 
			ElseIf EntityDistance ( player0 , pivotplanete4 ) < 3100 Then 
				EntityParent player0 , pivotplanete4 , 1 
				AlignToVector pivotorbiplanete4 , EntityX ( player0 ) , EntityY ( player0 ) , EntityZ ( player0 ) , 2 
				EntityParent player0 , pivotorbiplanete4 , 1 
			EndIf 
		EndIf 
		
		RotateEntity player0 , campitch# , camyaw# , camroll# 
		MoveEntity player0 , mvx , 0 , mvz 
		parentplayer0 = GetParent ( player0 ) 
		Collisions type_ellipsoide , type_ellipsoide , 1 , 3 
		
		UpdateWorld 
		RenderWorld 
		If parentplayer0 = 0 Then Text GW/4 , 100 , "Free cosmos" , 1 Else 
		If parentplayer0 = pivotorbiplanete4 Then Text 3*GW/4 , 100 , "Planet orbit" , 1 
		deplacy# = MouseYSpeed ( ) * mouse_move_speed#
		deplacx# = MouseXSpeed ( ) * mouse_move_speed#
		MoveMouse GW / 2 , GH / 2 
		
		Flip 0 
		Delay 1 
		
		mvx = 0 
		mvz = 0 
		If KeyDown ( 200 ) Or KeyDown ( 17 ) Then mvz =   vitesse 
	   	If KeyDown ( 208 ) Or KeyDown ( 31 ) Then mvz = - vitesse 
		If KeyDown ( 205 ) Or KeyDown ( 32 ) Then mvx =   vitesse 
		If KeyDown ( 203 ) Or KeyDown ( 30 ) Then mvx = - vitesse 

		campitch# = ( campitch# + deplacy# ) Mod 360 
		camyaw#   = ( camyaw#   - deplacx# ) Mod 360 
		If campitch# > 90.0 Then campitch# = 90.0
		If campitch# < -90.0 Then campitch# = -90.0
		
Wend 

End 

;;;; 
;;;; parapry 2009-2014
;;;;
