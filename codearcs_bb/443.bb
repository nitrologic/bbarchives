; ID: 443
; Author: Spacechimp
; Date: 2002-09-29 06:20:26
; Title: 3D Lens Flare
; Description: 1 light, 3 lens reflections

Type Make_Light
	Field Light_Sprite
	Field Flare_Sprite1
	Field Flare_Sprite2
	Field Flare_Sprite3
	Field LF_pivot1
	Field LF_pivot2
		Field Screen_Wash_Alpha1#
			Field light_bright# 
				Field Light_Bright_size#
					Field Light1_Distance#
					Field Flare1_Distance# 
					Field Flare2_Distance# 
					Field Flare3_Distance# 
						Field FLareX_Ang#
						Field FLareY_Ang#
					End Type 
;###################
MAIN LOOP
;###################


;************************************************
Places then updates coordinates for placing 
;************************************************
If Light_Create = False And Place_light = 3 Then 
Make_Lens_Flare = True
Light_Create = True
EndIf

If Light_Create = False And Place_light = 2 Then 
LightX_Pos# = 100
LightY_Pos# = 100
LightZ_Pos# = 100
Place_light =  3
EndIf 

If Light_Create = False And Place_light = 1 Then 
LightX_Pos# = 200
LightY_Pos# = -100
LightZ_Pos# = 100
Place_light =  2
EndIf 

If Light_Create = False And Place_light = 0 Then 
LightX_Pos# = 0
LightY_Pos# = 0
LightZ_Pos# = 0
Place_light =  1
EndIf 




If Make_Lens_Flare = False Then 
New_Light.Make_Light = New Make_Light

	New_Light\Light_Sprite = LoadSprite("sprites/LightA.png")
			PositionEntity 

New_Light\Light_Sprite,LightX_Pos#,LightY_Pos#,LightZ_Pos#
			
		New_Light\Flare_Sprite1 = 

LoadSprite("sprites/FlareA.png")
			EntityParent 

New_Light\Flare_Sprite1,New_Light\Light_Sprite
			
			New_Light\Flare_Sprite2 = 

LoadSprite("sprites/FlareA.png")
				EntityParent 

New_Light\Flare_Sprite2,New_Light\Light_Sprite
			
				New_Light\Flare_Sprite3 = 

LoadSprite("sprites/FlareA.png")
					EntityParent 

New_Light\Flare_Sprite3,New_Light\Light_Sprite
				
	New_Light\LF_pivot1 = CreatePivot()
		EntityParent New_Light\LF_pivot1,camera
		New_Light\LF_pivot2 = CreatePivot()
			EntityParent 

New_Light\LF_pivot2,New_Light\Light_Sprite

	
New_Light\light_bright# = 

EntityDistance(New_Light\LF_pivot2,New_Light\Light_Sprite) 

New_Light\Screen_Wash_Alpha1# = (-light_bright# + 30) / 60
EndIf

	
	For New_Light.Make_Light = Each Make_Light



PointEntity New_Light\LF_pivot1,New_Light\Light_Sprite


New_Light\Light_Bright_size# = (-light_bright# + 30) / 6
	If New_Light\Light_Bright_size# < 1 Then 
	New_Light\Light_Bright_size# = 1
	EndIf

New_Light\Light1_Distance# = 

EntityDistance(New_Light\Light_Sprite,camera) / 5 
New_Light\Flare1_Distance# = 

EntityDistance(New_Light\Light_Sprite,camera) / 6
	New_Light\Flare2_Distance# = 

EntityDistance(New_Light\Light_Sprite,camera) / 15
		New_Light\Flare3_Distance# = 

EntityDistance(New_Light\Light_Sprite,camera) / 30

New_Light\FLareX_Ang# = (EntityPitch(New_Light\LF_pivot1) - 

EntityPitch(camera)) * 

EntityDistance(New_Light\Light_Sprite,New_Light\LF_pivot1) / 139
	New_Light\FLareY_Ang# = (EntityYaw(New_Light\LF_pivot1) - 

EntityYaw(camera)) * 

EntityDistance(New_Light\Light_Sprite,New_Light\LF_pivot1) / 139


ScaleSprite New_Light\Light_Sprite,New_Light\Light1_Distance#  * 

New_Light\Light_Bright_size# ,New_Light\Light1_Distance# * 

New_Light\Light_Bright_size# 
ScaleSprite New_Light\Flare_Sprite1,New_Light\Flare1_Distance# / 

3,New_Light\Flare1_Distance# / 3
	ScaleSprite 

New_Light\Flare_Sprite2,New_Light\Flare2_Distance# / 

3,New_Light\Flare2_Distance# / 3
		ScaleSprite 

New_Light\Flare_Sprite3,New_Light\Flare3_Distance# / 

2,New_Light\Flare3_Distance# / 2

		
PositionEntity New_Light\Flare_Sprite3,New_Light\FLareY_Ang# / 

2,New_Light\FLareX_Ang# / 2,1
	PositionEntity New_Light\Flare_Sprite2,New_Light\FLareY_Ang# 

/ 3,New_Light\FLareX_Ang# / 3,2
		PositionEntity 

New_Light\Flare_Sprite1,New_Light\FLareY_Ang# / 

6,New_Light\FLareX_Ang# / 6,3
		
PositionEntity 

New_Light\LF_pivot2,EntityYaw(New_Light\LF_pivot1),EntityPitch(New_L

ight\LF_pivot1),0
	
Next
