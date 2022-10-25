; ID: 456
; Author: Jim Brown
; Date: 2002-10-10 16:59:53
; Title: *** Sprite Control *** UPDATED 07 March 04
; Description: Controls 3D sprites just like 2D images. 26 commands.

;;; Example Usage


Include "Sprite Control.bb"

SpriteGraphics3D width,height             ; set up your 3D graphics display
myimage=LoadImage3D("example.bmp")        ; load/create 3d image and attach to the sprite camera

Repeat
 .... do 3d stuff
 DrawImage3D myimage,MouseX(),MouseY()    ; positions the 3d sprite at mouse coordinates
 RenderWorld
 .... draw 2d stuff here
 Flip
Until KeyHit(1)
End
