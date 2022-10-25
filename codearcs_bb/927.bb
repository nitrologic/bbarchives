; ID: 927
; Author: CodeD
; Date: 2004-02-09 02:13:00
; Title: Rotating/Animated Quake2(md2) model
; Description: some simple code to animate and rotate 360 a quake 2 md2 model

;Richard Colletta (CodeD) Feb. 9, 2004
;This is my first try with BB3D to see if I can actually figure this crap out...wow, I can!! It's not hard...
;LESSON 1 (Let's see if I can load an md2!!)
;Yay, my first fricoculous program, "shhhh...it's only an example"
;I think I'll use this as a base to start learning with, maybe making a basic pointless game with
;LESSON 2 (Let's see if I can rotate it around)
;Alright!! starting with the basic example code to load an md2 I figured out purely by guessing how to rotate the darn thing!
;I was tripping on my feet till I figured i had to renderworld and flip for every rotation, I remember all that old basic stuff so, x = x + 1 was easy!
;LESSON 3 (let's see if I can animate this thing?!)
;Yes, we did it finally, just had to figure out the right place to put those loop codes and those anim sequences!
;If only this were QuickBasic I'd be getting accolades for loading, displaying, and animating a q2 model!

;beginning of a passage of voodoo incantations of some sort
Graphics3D 800, 600 ; sets the graphics mode
SetBuffer BackBuffer() ;creates a buffer for animations

camera=CreateCamera() ;creates a friggin camera
ground=CreatePlane(2) ;this makes some ground!
EntityAlpha ground, 0.5 ;i just stole this ground stuff from examples
EntityColor ground, 50, 20, 10
PositionEntity camera, 0, 120, -60 ;position the camera in x, y, z i.e. leftright, updown, 3rd dimension
spotlight = CreateLight() ;lets make a light
PositionEntity spotlight, 0, 80, -60 ;put it somewhere
light=CreateLight() ;another one whee! drunk with power
RotateEntity light, 90, 0, 0

playermod = LoadMD2("tris.md2") ;load a md2 file, replace this with an md2 you have in directory, I used Johnny the Homicidal Maniac for this example
playermodtexture = LoadTexture ("jthm.pcx") ;loads the md2's texture which should be in the same directory as the model, both unpacked!
EntityTexture playermod, playermodtexture ;it puts the texture on the model

PositionEntity playermod, 0, 122, 0 ;put our dude somewhere

x = 0 ;make a variable called x and give it a value


AnimateMD2 playermod, 1, 0.1, 0, 40 ;play the idle (standing) animation for quake2 model (i.e. frames 0-40)
While Not KeyDown(1) ;while a key hasn't been pressed do the following:
x = x + 1 ;increase x's value by 1, which will make it cycle through 360 degrees
RotateEntity playermod, 0, x, 0 ;inserting the value x to make it dynamic, it rotates!
RenderWorld ;render screen
UpdateWorld ;update it, i.e. animations etc.
Flip ;flip buffer pages
Wend

End ;End
