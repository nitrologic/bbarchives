; ID: 798
; Author: PsychicParrot
; Date: 2003-09-25 14:26:29
; Title: Supercam! Another 3rd person cam.
; Description: Alternative to smoothcam, I guess :)

; SuperCam!
;
; by PsychicParrot 2003
;
; 
; Usage : SuperCam(cam,ent,cspeed#,dist#,hite#,xrot#,tilt#)
; 
;

Graphics3D 640,480,16,3
SetBuffer=BackBuffer()

Global campivot=CreateCube() ; create pivot for camera
Global camera=CreateCamera()  ; create camera (!!!)

; ---------------------- THIS IS ALL JUST TO POPULATE THE WORLD WITH SOME RUBBISH ---------------

Global light=CreateLight()
Global player=CreateCube()    ; create simple player
Global plane=CreatePlane()   ; create simple floor
MoveEntity player,0,1,0
; Create texture of size 256x256
tex=CreateTexture(256,256)

; Set buffer - texture buffer
SetBuffer TextureBuffer(tex)

; Clear texture buffer with background white color
For i=1 To 10
Color Rnd(0,255),Rnd(0,255),Rnd(0,255)
Rect Rnd(0,256),Rnd(0,256),Rnd(0,256),Rnd(0,256)
Next

; Texture cube with texture
EntityTexture plane,tex
EntityTexture player,tex

; Set buffer - backbuffer
SetBuffer BackBuffer()

; ----------------------------------------------------------------------------------------------

While Not KeyHit(1)

If KeyDown(200) Then 
	MoveEntity player,0,0,.2
End If

If KeyDown(203) TurnEntity player,0,1,0
If KeyDown(205) TurnEntity player,0,-1,0

SuperCam(camera,player,.02,8,3,0,2)

RenderWorld
Flip

Wend

End

Function SuperCam(cam,ent,cspeed#,dist#,hite#,xrot#,tilt#)

TFormPoint 0,hite#,-dist#,ent,0

cx#=(TFormedX()-EntityX(cam))*cspeed#
cy#=(TFormedY()-EntityY(cam))*cspeed#
cz#=(TFormedZ()-EntityZ(cam))*cspeed#

TranslateEntity cam,cx,cy,cz
PointEntity cam,ent
RotateEntity cam,xrot#,EntityYaw(cam),tilt#

End Function
