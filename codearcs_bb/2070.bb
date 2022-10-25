; ID: 2070
; Author: Kalisme
; Date: 2007-07-17 01:30:16
; Title: basic directional shadows
; Description: casts a directional shadow onto a surface

;Attempt at explaining directional shadows
;-Kevin Laherty								(kalisme@hotmail.com)

;This is mearly a re-release of a pseudo-tutorial I released ages ago.
;I refur to this as a pseudo-tutorial because I'm actually quite
;concerned that this will seem like total utter jibberish
;since I'm horrible at explaining things...
;This is based off code that someone posted in a forum
;a few years ago (great learning material).
;I don't know his name, but if you know who I'm talking about
;please inform me, he deserves alot of credit.

;Anywho, this code casts a directional shadow from a caster entity
;onto a receiver entity. It may not be the most stunning style
;of casting shadows, but it's quite fast (when coded right)
;and seems to blend quite well into a blitz3d game engine.
;I hope I help at least one person and someone uses it to
;fancy-up a game n_n

;This was also the basis of my ShaKAL library I was working on
;a long time ago. (If anyone remembers it)

Graphics3D 800,600
SetBuffer BackBuffer()
AmbientLight 0,0,0

;Ssize:
;the size of the shadows texture.
;(remember To use base 2... eg: 2 4 8 16 32 64 128 256 512)
;make sure it's smaller than the games resolution...
;eg: you can't render a 512x512 map on a 320x240 screen...
Global ssize=64
Global shadow_tex=CreateTexture(ssize,ssize,16+32) ;create the shadow texture
;Xscale: 
;lets say the default zoom the shadow camera is 1.(it's actually .06, but 1 sounds easyier :P )
;so if Xscale = 1, the zoom = 1/Xscale# = 1
;but if Xscale is smaller than 1 it will make the camera zoom in more.
;It's hard for me to explain... play with it, you'll get it in time.
Global Xscale#=1*(.8/4)

;creating and setting up the shadow rendering camera.
shadowcam=CreateCamera() 
CameraViewport shadowcam,0,0,ssize,ssize 	;the viewport the shadows will be rendered through.
CameraClsColor shadowcam,255,255,255 		;Yup, white background for shadow textures.
CameraProjMode shadowcam,2					;Orthographic view, sorry, I've only figured out very simple directional shadows.
CameraZoom shadowcam,.06/xscale# 			;though the function "shadow_test" does this every time it's used, here it is for you to see anyway. It's not needed here.
CameraFogMode shadowcam,1					;The fog is put on so it seems like everythings white... to make a caster visable to the camera, disable fogging to that object.
CameraFogRange shadowcam,1,1
CameraFogColor shadowcam,255,255,255

HideEntity shadowcam ;we don't want it onscreen now.

;just setting up the scene========
camera=CreateCamera()					;<-the game camera
	PositionEntity camera,0,5,-15
scene=CreateSphere()						;<-later used as the receiver.
	ScaleEntity scene,20,1,20
caster=CreateCone()						;<-the object that shall cast a shadow.
UpdateNormals caster
EntityShininess caster, .2
	EntityColor caster,200,0,0
	RotateEntity caster,40,90,60
	PositionEntity caster, 0,2,0
	ScaleEntity caster, 1,2,1.5
light=CreateLight(2)					;<-the light source
	vis_light = CreateSphere(12,light)	;<-sphere used to make the light source visable.
		EntityColor vis_light,0,200,0
		EntityFX vis_light,1
	PositionEntity light,-20,8,-1
;=================================

;placing the shadow texture onto the receiver...
;but If you make a shadow lib For a game,
;You'll probably want this in the actual function... not here.
EntityTexture scene,shadow_tex

;Main loop============================================
While quit=0
Cls
MoveEntity light,0,0,1:TurnEntity light,0,-5,0 ;A rotating light source. Much more intresting.
TurnEntity caster,0,0,.5
RenderWorld
UpdateWorld
If KeyHit(1) Then quit=1
shadow_test(shadowcam,camera,light,caster,scene) ;<- our function.

Text 500,10,"Xscale#: "+ Xscale#
Text 500,20,"zoom: "+ .06/Xscale#
Text 300,30,"hit ''<-'' & ''->'' to change the Xscale#."
Flip

If KeyHit(203) Then xscale#=Xscale#*.8
If KeyHit(205) Then xscale#=Xscale#*1.2
Wend
End
;=====================================================



;shadow_test function:
;I made this to help explain the how the real magic is done.
;used like so:
;shadow_test(s_camera,g_camera,s_light,s_caster,s_receiver)
;
;>s_camera
;			The shadow camera used to render the shadows.
;
;>g_camera
;			The in game camera used to render the game.
;
;>s_light
;			The light source.
;
;>s_caster
;			The object that shadows are casted from.
;
;>s_receiver
;			Where the shadows land onto.
Function shadow_test(s_camera,g_camera,s_light,s_caster,s_receiver)

CameraZoom s_camera,.06/xscale#		;<-setting up the zoom for the shadow rendering camera.

cleartexture(shadow_tex)	;<- I added this because alot of graphics cards seem to render fog differant to mine.. Hopefully this fixes the blurring problem.
HideEntity g_camera			;The in game camera must be hidden,
ShowEntity s_camera			;and the shadow camera must become visable.

EntityFX s_caster,1+8		;<- setting up the caster:
EntityColor s_caster,0,0,0	;It needs to be black and unnefexted by fog and lights.

;The shadow rendering camera must be placed at
;the light source Then pointed at the caster.
PositionEntity s_camera,EntityX(s_light,1),EntityY(s_light,1),EntityZ(s_light,1)
PointEntity s_camera,s_caster

;Here we render the camera and convert it to a texture.
RenderWorld()
Color 255,255,255: Rect 0,0,ssize,ssize,0 ;<-this rectangle is used to cull anything that tries to poke outside of the textures boundry box.
CopyRect 0,0,ssize,ssize,0,0,BackBuffer(),TextureBuffer(shadow_tex)

;The surface and verticy loop.
;This loops through every surface of the receiver mesh,
;Then every surfaces vertecies.
;Our main focus should be the
;"Tformpoint" And "vertextexcoords" commands.
;These commands actually paint the texture onto the
;receiver correctly.
For s=1 To CountSurfaces(s_receiver) 
	surf=GetSurface(s_receiver,s) 
	For v=0 To CountVertices(surf)-1 
		TFormPoint VertexX(surf,v),VertexY(surf,v),VertexZ(surf,v),s_receiver,s_camera
		VertexTexCoords surf,v,(TFormedX()/Xscale#)/32+0.5,1-((TFormedY()/Xscale#)/32+.5) 
	Next 
Next 


;Here we just switch the attributes of the caster back to normal.
;In an actual game engine you might have to make this more complex.
;I suggest keeping the old attributes in tempory memory then
;calling them back at about this point.
EntityFX s_caster,0
EntityColor s_caster,200,0,0

;here we hide the shadow camera and bring back the game camera,
;ready to go back to the main game loop like nothing ever happend.
ShowEntity g_camera
HideEntity s_camera

End Function


;This last little function holds no real importance, it just clears a texture and makes it white.
;I added this because when I first released this code I found alot of graphics cards don't make
;the fog as thick as mine (I'm guessing it's because my card it cheap), and the shadow texture
;Grabbed the old shadow texture off the ground and blurred with the new one...
;very annoying and unpretty.... Oh well... Hope this works much better.
Function cleartexture(tex)
SetBuffer TextureBuffer( tex ) 
Color 255,255,255
Rect 0,0,TextureWidth(tex)+1,TextureHeight(tex)+1
SetBuffer BackBuffer()
End Function
