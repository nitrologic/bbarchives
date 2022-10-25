; ID: 153
; Author: bradford6
; Date: 2002-02-27 16:55:57
; Title: Simple 3D template
; Description: very basic 3d "getting started" template

; Blitz3d template

AppTitle "3D madness","GOODBYE!"
; check to see if a graphics mode exist then
; if so set it, if not display error
If GfxMode3DExists (800,600,16)
    Graphics3D 800,600,16
Else
  RuntimeError "ACHTUNG! UPGRADE YOUR VIDEO CARD!"
EndIf

;
;  Create a camera . we see all 3d
; through this camera. you can have multiple 
; cameras
;
cam=CreateCamera() ; create a world camera

MoveEntity cam,0,0,-5 ; move the camera "back" 5 units 

lite=CreateLight() ; create a light for our world

blob=CreateCube() ; create a cube and call it blob
EntityColor blob,0,0,255 ; color our blob, red, green , blue 

PositionEntity blob,0,0,3 ;  place the blob at world coordinate 0,0,3

Repeat  ; * * * * beginning of loop

TurnEntity blob,1,1,1 ; turnentity entity,x,y,z
	
	RenderWorld ; render the 3d scene
	Flip ; flip the buffer

Until KeyDown(1)=1 ; * * * * end of loop
; check to see if key 1 (escape key) is pressed

RuntimeError "adios amigos"
End
