; ID: 1359
; Author: D4NM4N
; Date: 2005-04-25 11:17:30
; Title: positionsprite()
; Description: display 3d sprites using 2d screen coordinates

;--------------------------------------------------------------------------------
;A couple of functions to display 3d sprites using 2d coordinates!!
;By dan@d-grafix   
;--------------------------------------------------------------------------------
;firstly ive kept it as simple as possible.
;im not going in to the calculations involved to work out the cam dist and scale factors, but this is a 
;simple utility to convert 2d coords back in to a virtual 2d screen for using sprites or other flat entities.

;Positionsprite() - a simple Virtual 2d screen of 640x480 coordinate system for sprites. (works in any screen mode with these numbers)
;useprojectedx/y#() - a routne to convert position returned from projectedx/y() for higher screen resolutions

;use:
;	positionsprite (entity,	x(as 2d), y(as 2d), scale(1 keeps original size (256x256) for actual screen coords))
;	useprojectedx/y# (coords# (coordinate for conversion))

;possible uses:
; 	HUD
;	lens flares
;	displaying 'lives' etc

;rules:
;	sprites must be parented to the primary camera (with normal zoom?(not tested for this))
;	screen modes must be equally proporsonate to 640x480... ie 320x240 1024x768 etc otherwise the you may loose some Y!
;	its not 100% accurate and (may) produce funny results when tiled sprite are used although this may be fixable with some fiddling
;	the scale factor of 1.0   is calculated from sprites loaded at 256x256. for smaller ones adjust it as appropriate







;--------------------------------------------------------------------------------
;gfx setup
;--------------------------------------------------------------------------------
;try it with various screen modes
Graphics3D 1024,768,16,1

Global gwidth			:gwidth  =GraphicsWidth()
Global gheight			:gheight =GraphicsHeight()


cam=CreateCamera()
sprite1=LoadSprite("    your sprite.png      ",1,cam)
sprite2=CopyEntity(sprite1,cam)
sprite3=CopyEntity(sprite1,cam)



;--------------------------------------------------------------------------------
;demo code
;--------------------------------------------------------------------------------
	scale#=1
	
	;position some sprites using virtual 640x480 coords (works in all relative screen modes) 
	positionsprite (sprite1,0,0,scale)		;prints in top corner
	positionsprite (sprite2,639,479,scale) 	; prints in bottom corner (-1 to show gren cross)
	
	;position sprite using true screen coords (useful for use with projectedx/y() in different res modes ie. for lens flares)
	px#=useprojectedx#(512)
	py#=useprojectedy#(384)
	positionsprite (sprite3,px#,py#,scale) ; prints at actual screen coords for grafx mode!

	RenderWorld()

	;draw some 2d text guides to see whats happening 
	Text 50,10,"Virtual coord = 0,0"
	Text gwidth-200,gheight-20,"Virtual coord = 640,480"
	Text 512,384,"real screen coord = 512,384";
	
	Flip

        waitkey()


;--------------------------------------------------------------------------------
;projection coord converters
;--------------------------------------------------------------------------------
Function useprojectedx#(coord#)
	div#=640/Float(gwidth)
	Return coord*div
End Function

Function useprojectedy#(coord#)
	div#=480/Float(gheight)
	Return coord*div
End Function

;--------------------------------------------------------------------------------
;Position sprite on ' virtual 2d '  3d screen
;--------------------------------------------------------------------------------
Function positionsprite(sprite,x#,y#,sps#=1.0,zdi#=3.2);zdi is precalculated and should not be changed (5.1 for upgrade to 1024)
	sps = sps*1.28 	; 1.29 is precalculated too (best leave)
	cx#=(x-(640*.5))*.01
	cy#=(y-(480*.5))*.01
	ScaleSprite sprite,sps,sps
	PositionEntity sprite,cx,-cy,zdi
End Function
