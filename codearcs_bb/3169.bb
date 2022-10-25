; ID: 3169
; Author: ThePaiva
; Date: 2014-12-24 01:33:06
; Title: Mumble Positional Audio
; Description: DLL for positional audio in Mumble

InitMumble() ;Links your game to Mumble. You only need to use it once in your game

UpdateMumble(x#,y#,z#,rotation#) ;It updates your position in Mumble to all the other players.
;Just send the global entity position of your character or camera. Oh, and the rotation needs to be in 360 degrees. That's all.
