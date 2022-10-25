; ID: 1327
; Author: Malice
; Date: 2005-03-15 05:27:27
; Title: Basic Multiple Orthographic Camera Views
; Description: Sets 3 camera views top, side, front and a render

Graphics3D 800,600
SetBuffer BackBuffer() ; Set Scene

cone=CreateCone(6)    ;Set example entities
cube=CreateCube()     ;Set example Entities

EntityColor cube,255,128,128 ;add effects to entities
EntityColor cone,128,128,64  ;add effects to entities

effectlight=CreateLight(1)   ; add lighting
PositionEntity effectlight,40,30,40  ; set lighting
PointEntity effectlight,cube ; etc. etc.

;TOP VIEW CAMERA
topcam=CreateCamera() ;create camera
CameraViewport topcam,0,(GraphicsHeight()/2),GraphicsWidth()/2,(GraphicsHeight()/2); set viewport
CameraProjMode topcam,2 ; make view orthogonal

;SIDE VIEW CAMERA
sidecam=CreateCamera()
CameraViewport sidecam,0,0,GraphicsWidth()/2,GraphicsHeight()/2
CameraProjMode sidecam,2

;FRONT VIEW CAMERA
frontcam=CreateCamera()
CameraViewport frontcam,GraphicsWidth()/2,0,width,GraphicsHeight()/2
CameraProjMode frontcam,2

rendercam=CreateCamera()
CameraViewport rendercam,GraphicsWidth()/2,GraphicsHeight()/2,GraphicsWidth()/2,GraphicsHeight()/2


;Set object positions
PositionEntity cone,50,0,50

PositionEntity cube,52,0,50

PositionEntity topcam, 51,10,50
PositionEntity sidecam, 61,0,50
PositionEntity frontcam, 51,0,60
PositionEntity rendercam, 56,5,55

;point cameras
PointEntity topcam,cone
TurnEntity topcam,0,0,180
PointEntity sidecam,cone
PointEntity frontcam,cone
PointEntity rendercam,cone

EntityParent topcam,cone
EntityParent sidecam,cone
EntityParent frontcam,cone
EntityParent rendercam,cone


;MAIN LOOP
While Not KeyDown(1)

;movement by cursor keys
TranslateEntity cone,(KeyDown(208)-KeyDown(200)),0,0
TranslateEntity cone,0,0,(KeyDown(203)-KeyDown(205))

;Rendering
WireFrame True


HideEntity rendercam
ShowEntity sidecam
ShowEntity topcam
ShowEntity frontcam

RenderWorld
Flip

WireFrame False

ShowEntity rendercam
HideEntity sidecam
HideEntity topcam
HideEntity frontcam

RenderWorld
Flip

Wend

;END OF PROGRAM

;EndGraphics
End
