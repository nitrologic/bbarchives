; ID: 17
; Author: Rob Hutchinson
; Date: 2001-08-18 00:46:32
; Title: Dev. Position/Rotate entity w/mouse
; Description: Simple function to help you position and rotate an entity with the mouse.

; DEVPosRotEntityWM(entityobject,cameraobject,[speed#])
; -----------------------------------------------------
; AUTHOR: Rob Hutchinson (loki.sd@cableinet.co.uk)
; BB3DV:  v1.60 Beta.
; DESC:   Simple function to help you position and rotate an entity with the mouse.
; PARAMS: entityobject - the entity you want to fiddle with.
;         speed#       - the speed to move with. Recommend around 0.1 (default)
; NOTES:  Will lock the mouse in the center of the screen in order to achieve more
;         smooth movement with MXS() and MYS(). You should comment out any mouse
;         command stuff your program is doing before using this function.
; USAGE:  Call this function straight after RenderWorld(), cos it needs to output text.
;
;           CONTROL:     NORMAL MODE  + ALT MODE     + ROT MODE    + ROT+ALT MODE
; CONTROLS: Up/Down    = +/- Z axis   | +/- Y axis   | +/- Roll    | +/- Roll    
;           Left/Right = +/- X axis   | +/- X axis   | +/- Pitch   | +/- Yaw
;           ---------------------------------------------------------------------------           
;           SWITCHES:
;           LMB        = Hold down to switch to alt mode.
;           RMB        = Hold down to switch to rotation mode.
;           MMB        = Hold down to switch to camera mode. (this will allow you to
;                        simply see whats going on)
;           F1         = Reset all positions
;
;           HAHA! its a bitch to start with, but it gets easier :) (unless your MMB is
;           in some ungodly place on the mouse :)).
;           
Global entitX#,entitY#,entitZ#
Global entitPi#,entitYa#,entitRo#
Global cammyX#,cammyY#,cammyZ#
Global cammyPi#,cammyYa#,cammyRo#
Function DEVPosRotEntityWM(ent,cammy,spd# = .1)
	msX# = MouseXSpeed() * spd#
	msY# = MouseYSpeed() * spd#

	If MouseDown(3) = False
		; We are working with the entity..
		If MouseDown(2) = False
			entitX# = entitX# + msX#
			If MouseDown(1) = False
				entitZ# = entitZ# - msY#
			Else
				entitY# = entitY# - msY#
			EndIf
		Else
			entitPi# = entitPi# - msY#
			If MouseDown(1) = False
				entitRo# = entitRo# - msX#
			Else
				entitYa# = entitYa# + msX#
			EndIf
		EndIf
	Else
		; We are working with the camera..
		If MouseDown(2) = False
			cammyX# = cammyX# + msX#
			If MouseDown(1) = False
				cammyZ# = cammyZ# - msY#
			Else
				cammyY# = cammyY# - msY#
			EndIf
		Else
			cammyPi# = cammyPi# - msY#
			If MouseDown(1) = False
				cammyRo# = cammyRo# - msX#
			Else
				cammyYa# = cammyYa# + msX#
			EndIf
		EndIf
	EndIf
	
	If KeyHit(59)
		; Reset positions....
		entitX# = 0 : entitY# = 0 : entitZ# = 0
		cammyX# = 0 : cammyY# = 0 : cammyZ# = 0
		entitPi# = 0 : entitYa# = 0 : entitRo# = 0
		cammyPi# = 0 : cammyYa# = 0 : cammyRo# = 0
	EndIf

	PositionEntity ent,entitX#,entitY#,entitZ#
	RotateEntity ent,entitPi#,entitYa#,entitRo#
	PositionEntity cammy,cammyX#,cammyY#,cammyZ#
	RotateEntity cammy,cammyPi#,cammyYa#,cammyRo#
	
	Text 0,FontHeight()*0,"PRWM() - Entity X# = " + entitX#
	Text 0,FontHeight()*1,"PRWM() - Entity Y# = " + entitY#
	Text 0,FontHeight()*2,"PRWM() - Entity Z# = " + entitZ#
	Text 0,FontHeight()*3,"PRWM() - Entity Pitch# = " + entitPi#
	Text 0,FontHeight()*4,"PRWM() - Entity Yaw# = " + entitYa#
	Text 0,FontHeight()*5,"PRWM() - Entity Roll# = " + entitRo#

	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
End Function
