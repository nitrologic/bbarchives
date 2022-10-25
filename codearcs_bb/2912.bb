; ID: 2912
; Author: Austin Wicker
; Date: 2012-01-20 13:46:17
; Title: Creating a Diamond.
; Description: Teach how to create a diamond in raw code.

Graphics3D 800,600,32,2 ;set graphics mode here ie:800,600 or 640,480, whatever fits your needs
SetBuffer BackBuffer() ;manually set our buffer to backbuffer. (this really isn't needed when you set your graphics to 3d as shown above ^_^)

camera=CreateCamera() ;create a camera to see everything!!!
light=CreateLight(2) ;make light to brighten up our scene

sphere=CreateSphere(2) ;limit the sphere to 2 segments to make it look like a diamond
PositionEntity sphere,0,0,5 ;posiion our new diamond so that we can see it :D

EntityShininess sphere,99 ;give the diamond a little shine! :)

EntityColor sphere,179,231,255 ;color the diamond to look like a diamond ie:Red,Green,Blue
PointEntity light,sphere ;point the light directly onto the diamond. (not really needed)
Repeat ;start or loop
	
	TurnEntity sphere,0,5,0 ;constantly turn our diamond to the right
	
	
	UpdateWorld ;update objects
	RenderWorld ;draw objects to the screen. (without this the diamond will not move just appear on the screen)
	Flip ;flips the BackBuffer into our view
Forever ;loop the program forever. at least until the exit butten is clicked on. :P
