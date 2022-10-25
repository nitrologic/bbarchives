; ID: 416
; Author: Filax
; Date: 2002-09-03 12:34:21
; Title: 3D Template for beginner
; Description: 3D Template for beginner with loadanim mesh fx function

Global Objet

Graphics3D 1024,768,32,0
;SetBuffer BackBuffer()

; Creation de la caméra
; ---------------------
camera=CreateCamera()
;CameraRange camera,0.01,20000

; -------------------------------
; Chargement de l'objet
; -------------------------------
Objet=LoadAnimMesh( "TIEI2.3ds" )
RotateEntity Objet,0,0,0
PositionEntity Objet,0,0,60

; -------------------------------------------------------------------------------------------------------------------------
; On applique un shininess totale et ensuite on s'occupe du cockpit qui se nomme 'Glass'
; -------------------------------------------------------------------------------------------------------------------------
ShininessAlpha_LoadAnimMesh(Objet,1,0.5)
ShininessAlpha_Child(Objet,"Glass",0.5,0.5)

; Texturage normal
; ----------------
;texture=LoadTexture("Panel-1.jpg")
;EntityTexture Objet,texture
;EntityFX Objet,2

; Texturage Chrome
; ----------------
;fx=LoadTexture( "Panel-1.jpg",64 +1)
;EntityTexture Objet,fx

;EntityFX Objet,2


; Enable/disable antialiasing
; ---------------------------
;AntiAlias True

; Definition des lumiere ambiante
; --------------------------------------------
AmbientLight 0,0,0

; Creation des spots
; --------------------------
Range=200
Light1=CreateLight(2)
LightColor light1,100,150,200
PositionEntity light1,100,100,-160
LightRange light1,Range

Light2=CreateLight(2)
LightColor light2,100,150,200
PositionEntity light2,-100,-100,-160
LightRange light2,Range

; Rotation
; --------
accelerator#=0.2
decelerator#=3


While Not KeyHit(1)
	; Deplacement Haut / Bas
	; ----------------------------------
	If KeyDown(200)	Then
		Velocity_Y#=Velocity_Y#+accelerator#
	EndIf


	If KeyDown(208)	Then
		Velocity_Y#=Velocity_Y#-accelerator#
	EndIf


	If Velocity_Y#>0 Then 
		Velocity_Y#=Velocity_Y#-accelerator#/decelerator#
		If Velocity_Y#<0 Then Velocity_Y#=0
	EndIf

	If Velocity_Y#<0 Then 
		Velocity_Y#=Velocity_Y#+accelerator#/decelerator#
		If Velocity_Y#>0 Then Velocity_Y#=0
	EndIf



	; Deplacement Gauche / Droite
	; -----------------------------------------
	If KeyDown(203)	Then
		Velocity_X#=Velocity_X#+accelerator#
	EndIf


	If KeyDown(205)	Then
		Velocity_X#=Velocity_X#-accelerator#
	EndIf

	If Velocity_X#>0 Then 
		Velocity_X#=Velocity_X#-accelerator#/decelerator#
		If Velocity_X#<0 Then Velocity_X#=0
	EndIf

	If Velocity_X#<0 Then 
		Velocity_X#=Velocity_X#+accelerator#/decelerator#
		If Velocity_X#>0 Then Velocity_X#=0
	EndIf

	
	TurnEntity Objet,Velocity_Y#,Velocity_X#,0


	
	If KeyDown(210)	Then
		; Fonction de rotation des objects UP
		; --------------------------------
		MoveEntity Camera,0,0,1
		Text 335,500,"Camera Movement"
	EndIf
	
	If KeyDown(211)	Then
		; Fonction de rotation des objects UP
		; --------------------------------
		MoveEntity Camera,0,0,-1 
		Text 335,500,"Camera Movement"
	EndIf

	UpdateWorld
	RenderWorld
	
	Flip
Wend
End

; ----------------------------------------------------------------------------------------------------
; Fonction pour trouver un objet nommé et lui attribuer un alpha / shininess
; ----------------------------------------------------------------------------------------------------
Function ShininessAlpha_Child(Entity,Name$,Alpha#,Shininess#)
	If EntityName$(Entity)=name$ Then 
		Return Entity
	Else
		For a=1 To CountChildren(Entity)
			Child=ShininessAlpha_Child(GetChild(Entity,a),name$,Alpha#,Shininess#)
			
			If Child<>0
				If EntityName$(Child)=name$ Then 
					EntityAlpha Child,Alpha#
					EntityShininess Child,Shininess#
				EndIf
			End If
		Next
	EndIf
	
	Return 0
End Function

; -------------------------------------------------------------------------------------------------------------
; Fonction pour appliquer un shininess / alpha a tout les enfants d'un anim mesh
; -------------------------------------------------------------------------------------------------------------
Function ShininessAlpha_LoadAnimMesh(Entity,Alpha#,Shininess#)
	For a=1 To CountChildren(Entity)
		Child=GetChild(Entity,a)
		EntityAlpha Child,Alpha#
		EntityShininess Child,Shininess#
	Next
End Function
