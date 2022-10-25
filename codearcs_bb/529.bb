; ID: 529
; Author: tobiasm767
; Date: 2002-12-16 12:20:56
; Title: Texture-HUD
; Description: a texture based HUD

; #################################
; ###        TEXTURE-HUD        ###
; ###            V1.0           ###
; #################################
;
;	(c) 2002 Tobias Müller
;	lanpage@freenet.de / www.LAN-Byte.de.gs
;
;
;
;	hud_create( parent, CamZoom#=1.0 ) - create the hud. you MUST set camera as parent
;	hud_Text( X, Y, Text, red=255, green=255, blue=255, delete_Text_after_render=0, x_center=0 )
;	hud_clear()	- delete all hud-text and render the hud
;	hud_del_text() - delete only the hud-text
;	hud_delete() - delete the hud
;	hud_render() - render the hud


;set the hud texture size (bigger texture -> smaller text)
Const tex_size = 256


Global hud_sprite
Global hud_texture
Global hud_redraw=0


Type hud_Text
	Field X%
	Field Y%
	Field Msg$
	Field r
	Field g
	Field b
	Field del
	Field x_center
End Type




;run example
example()




Function hud_create( parent , CamZoom#=1.0 )


	hud_sprite = CreateSprite( parent )
	SpriteViewMode( hud_sprite,2)

	hud_texture = CreateTexture(tex_size,tex_size,1)


	EntityTexture( hud_sprite, hud_texture)

	

	;3:4 = 0.75 
	ScaleSprite( hud_sprite, 1, 0.75)
	MoveEntity( hud_sprite,0,0, 1 * CamZoom )

	EntityOrder( hud_sprite, -1000 )
	EntityBlend(hud_sprite,3)
	

End Function




Function hud_Text( X, Y, msg$, r=255, g=255, b=255, del=0,x_center=0 )

	a.Hud_Text = New Hud_Text 
	a\X = X
	a\Y = Y
	a\msg = msg
	a\r = r
	a\g = g
	a\b = b
	a\del = del
	a\x_center = x_center

	hud_redraw=1

End Function



Function hud_clear()

		For a.Hud_text = Each Hud_Text
			Delete a
		Next

	SetBuffer TextureBuffer( hud_texture )
		;black background
		Color 0,0,0
		Rect 0,0,tex_size, tex_size,1

		Flip(False)
	SetBuffer(BackBuffer())


End Function





Function hud_del_text()

	For a.Hud_text = Each Hud_Text
		Delete a
	Next


End Function

Function hud_delete()

	hud_del_text()

	FreeTexture(hud_texture)
	FreeEntity(hud_sprite)

End Function






Function hud_render()

	If hud_redraw=1 Then

		SetBuffer TextureBuffer( hud_texture )	
			;black background
			Color 0,0,0
			Rect 0,0,tex_size, tex_size,1
	
			;write text
			For a.Hud_Text = Each Hud_text
				Color( a\r, a\g, a\b )
				Text( a\X, a\Y, a\msg, a\x_center )
		
				If a\del=1 Then temp=1:Delete a		
			Next

			Flip(False)
	
		SetBuffer BackBuffer()
	
		If temp = 1 Then
			;a hud text were deleted -> render the hud again
			hud_redraw=1
		Else
			hud_redraw=0
		EndIf
	EndIf

End Function











Function example()

	Graphics3D(800,600,0,2)
	SetBuffer BackBuffer()
	
	camera=CreateCamera()
	CameraClsColor(camera,34,85,136)
	
	hud_create(camera)
	hud_text(tex_size/2,tex_size/2,"HUD-TEST",255,0,0,False,True)

	;something for the background
	cube=CreateCube()
	EntityColor(cube,64,64,64)
	MoveEntity(cube,0,0,4)

	light=CreateLight()
	MoveEntity(light,10,3,1)

	While(Not KeyHit(1))
		hud_render()
		
		hud_text(0,0,CurrentTime(),255,255,255,True,False)
		
		TurnEntity(cube,1,1,1)
	
	UpdateWorld()
	RenderWorld()
	Flip
	Wend

End Function
