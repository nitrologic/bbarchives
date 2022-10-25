; ID: 1063
; Author: Pedro
; Date: 2004-05-31 16:06:29
; Title: rotate the camera with the mouse smoothly
; Description: riate camera with mouse

;----------------------------------------------------------------------------------------------
; 	Title 			: Function to move smoothly the camera with the mouse
;	Authors			: Philippe C 
;	Version 		: V0.1
;
;	This library provides functionalities to move the camera in a smooth way with the mouse.
;	Whatever the speed of the mouse the rotation of the camera are always smooth.
;	The effect in a game seems better.
;
;----------------------------------------------------------------------------------------------
;----------------------------------------------------------------------------------------------
;Version 0.1
; 	intial version
;----------------------------------------------------------------------------------------------

; type created in order to manage the camera mouvement in a smooth way
Type objet3dCamera
	Field current_value_x#
	Field current_value_y#
End Type
objet3dCamera.objet3dCamera = New objet3dCamera


Function objet3dCameraWalk(camera,clickL%,clickR%,XSpeed%,YSpeed%,Zspeed%,move=1,speed#=0.3,smooth#=6.0)
;--------------------------------------------------------------------------------------------------------------------
; The rotation of the camera are smooth
;--------------------------------------------------------------------------------------------------------------------
	
	If speed <= 0 Then RuntimeError "objet3dCameraWalk, error program , the speed should be greater than 0 => " + speed
	If smooth < 1 Then RuntimeError "objet3dCameraWalk, error program , the smooth should be greater than 1 => " + smooth
	
	If camera <= 0 Then RuntimeError "objet3dCameraWalk, error program , the camera doesn
