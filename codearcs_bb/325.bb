; ID: 325
; Author: nadia
; Date: 2002-05-20 05:35:54
; Title: CreateShadowmap()
; Description: function to make terrain shadow map

;Please use however you like.
;If you improve functionality or speed please poste improvements!

;Make sure all meshes you like to cast shadows are set to
;EntityPickMode(), obscure true

;Use a paint program to blur the resulting shadow map

;For finer shadow details render shadow map in high resolution,
;up to 1024x1024 (beware, quite long render time!) then scale it 
;down in a paint program

;points for improvement:
;better speed
;added edge blur functionality

;Idea and implementation by nadia lunanova
;nadia_lunanova@yahoo.com

;++ How to use ++++++++++++++++++++++++++++++

;	mapName$		File name of new shadow map.
;	map_Width		Width of new shadow map in pixels.
;	map_Height		Height of new shadow map in pixels. (Width and height are usually the same.)
;	light			Handel to the light element.
;	terrain			Handel to the terrain.
;	terrain_Left#	Left position of terrain.
;	terrain_Width#	Width of terrain after scaling.
;	terrain_Top#	Top position of terrain.
;	terrain_Depth#	Depth or Length of terrain after scaling.
;	light_Color		Color on shadow map where there is no shadow. Optional, defaults to white
;	shadow_Color	Color useed to paint shadow. Optional, defaults to grey 50,50,50
;
;	Default trigger key is 'M'

Function CreateShadowMap(mapName$,map_Width, map_Height,light, terrain,terrain_Left#,terrain_Width#,terrain_Top#,terrain_Depth#,light_Color=255,shadow_Color=90)
	
	If (Not KeyHit(Key_B)) Then Return 	;<<---------------- Only run if key 'M' is hit
	FlushKeys()
	
	time#=MilliSecs()	;to measure render time
	
	sMsg$="Creating Shadow Map, please wait...!"
	font=LoadFont("Arial",35,True)
	SetFont font
	Color 250,250,150
	Text GraphicsWidth()/2,(GraphicsHeight()/2)-(60*scr_Scale),sMsg$,True,True
	
	;set up progress bar
	progW=400
	progX=(GraphicsWidth()/2)-(progW/2)
	progY=(GraphicsHeight()/2)+50
	progH=20
	Color 0,0,200
	Rect progX-4,progY-4,progW+8,progH+8
	Flip
 	
	;get light coordinates
	lX# = EntityX(light)
	lY# = EntityY(light)
	lZ# = EntityZ(light)
	;this asumes that the light is set 
	;relative To the centre of the terrain
	lnX#=lX+(terrain_Width/2)
	lnZ#=lZ-(terrain_Depth/2)
	lposX#=lnX
	lposZ#=lnZ
	PositionEntity light,lposX,ly,lposZ

	
	;calculate step increments		
	stepX#=terrain_Width#/map_Width
	stepZ#=terrain_Depth#/map_Height
	
	;create shadow map
	img=CreateImage(map_Width,map_Height)
	
	;create  ant
	ant= CreatePivot()
	posX#=terrain_Left
	posY#=terrain_Top

	;place ant on first grid spot
	PositionEntity ant,posX,TerrainY(terrain,posX,0,posY)+0.2,posY
		
	;set shadow color
	colGrey= GetRGB(shadow_Color,shadow_Color,shadow_Color)	;set shadow color
	colLight= GetRGB(light_Color,light_Color,light_Color)
	;walk the walk...
	For x= 0 To map_Width-1
		For y= 0 To map_Height-1
			LockBuffer ImageBuffer(img)

			If Not EntityVisible (ant, light) Then
				pCol=colGrey
			Else
				pCol=colLight
			End If
			;paint he spot
			WritePixelFast x,y,pCol,ImageBuffer(img)
			
			;calculate next position
			posY=posY-stepZ
			PositionEntity ant,posX,TerrainY(terrain,posX,0,posY),posY
			;for light
			lposY=lposY-stepZ
			PositionEntity light,lposX,lY,lposX

			If KeyHit(1) Then End
		Next
		posY=terrain_Left
		posX=posX+stepX
		
		;show progress
		UnlockBuffer ImageBuffer(img)
		SetBuffer BackBuffer()

		Color 0,0,200
		Rect progX-4,progY-4,progW+8,progH+8

		Color 255,0,0
		Rect progX,progY,progW/Float(map_Width)*Float(x+1),progH
		
		Color 250,250,150
		Text GraphicsWidth()/2,(GraphicsHeight()/2)-(60*scr_Scale),sMsg$,True,True

		Flip
		;back to shadow paint mode
		SetBuffer ImageBuffer(img)
		Color shadow_color,shadow_color,shadow_color
	Next 
	
	;save shadow map, clean up
	SaveImage img,mapName$
	SetBuffer BackBuffer()
	FreeImage img
	FreeEntity ant
	;move light back to original position
	PositionEntity light,lx,ly,lz

	RenderWorld()
	
	;display elapsed time
	time= (MilliSecs() - time)/600
	sTime$=" Min"
	If time > 60 Then 
		min=time/60
		sec=time Mod 60
	Else
		sec=Int(time)
	End If
	Text GraphicsWidth()/2,(GraphicsHeight()/2)-60,"Shadow Map done!",True,True
	FreeFont font
	font = LoadFont("Arial",30,True)
	SetFont font
	Color 200,0,0
	
	Text GraphicsWidth()/2,(GraphicsHeight()/2)-30,"Time elapsed: " + min + ":" + sec + sTime$,True,True
	Text GraphicsWidth()/2,(GraphicsHeight()/2),"Hit any key to continue...",True,True
	Flip
	FreeFont font
	WaitKey()
	
End Function

Function GetRGB(R,G,B)
	Return   (B Or (G Shl 8) Or (R Shl 16) Or (255 Shl 24))
End Function

	
