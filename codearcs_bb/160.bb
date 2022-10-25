; ID: 160
; Author: duncki
; Date: 2001-12-08 03:42:44
; Title: Image wrapping
; Description: a little Image wrapping routine

; a little wrap routine feel free to make it better
; modified by Andreas Duncker Germany
;sw = screenwidth
;sh = screenheight
;mx = the X Coordinates of the Image
;mw = the width of the  Image
;speed = the speed im Pixel for the Image
;this Wrap routine is from the Rocket Example but now a little bit smaller
;with only then Code that you must have.
;the only think that you must do is draw a little Image , name it "sprite.bmp"
;and put it in then same Directory where you put this code
;good luck! 
sw=640
sh=480
Graphics sw,sh,16,1
sprite=LoadImage("Sprite.bmp")
mw=ImageWidth(sprite)
mx=640 ; in this Moment teh Startpositon of the image
speed=-8;Speed in Pixel
SetBuffer BackBuffer()
While Not KeyDown(1)
Cls
mx=mx+speed;then current Movemen only in X
If mx+mw>sw Then DrawImage sprite,mx-sw,100;now the wrap routine
If mx>sw Then mx=speed
If mx<0 Then DrawImage sprite,sw+mx,100
If mx+mw<0 Then mx=sw-mw+speed
DrawImage sprite,mx,100
Flip
Wend
End
