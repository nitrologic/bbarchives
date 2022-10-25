; ID: 955
; Author: big10p
; Date: 2004-02-29 18:36:05
; Title: Spinning, exploding titles demo
; Description: Game title spins and explodes before reforming.

;
; Spinning, exploding, reconstructing titles demo
;
; by big10p (A.K.A. Chris Chadwick) 2003.
;
; Written with Blitz3D v1.83
;

	Graphics3D 800,600,16

	SetBuffer BackBuffer()

	SeedRnd MilliSecs()
	
	Type grid_pix
		Field ent
		Field x#,y#,z#
		Field rot#
	End Type

	Type part
		Field ent
		Field x#,y#,z#
	End Type

	Const TOTAL_STARS = 400	; Number of stars in star field.
	Const EXPLODE% = 400	; Number of frames to explode before imploding.
			
	Global frame_count%
	Global fps%
	Global fps_timeout%
	Global frame_time%
	Global slowest_frame%
	Global frame_start%

	Global title_state% = 3
	Global explode_count%
		
	Global cam = CreateCamera()
	PositionEntity cam,0,0,-14
	
	Global light = CreateLight()

	Global spiv = CreatePivot()
	Global piv = CreatePivot()

	; Master entity the title grid is constructed from.
	Global title_ent = CreateCube()
	EntityShininess title_ent,1
	HideEntity title_ent
	
	; Blur sprite.
	sprite = CreateSprite(cam)
	PositionEntity sprite,0,0,1.1
	ScaleSprite sprite,1.5,1
	SpriteViewMode sprite,1
	EntityAlpha sprite,.124
	EntityColor sprite,0,0,0
	EntityBlend sprite,1
	EntityFX sprite,1
	EntityOrder sprite,1
	Dither False
	CameraClsMode cam,0,1
	
	make_titles()	
	make_stars()

	; Main loop.
	While Not KeyHit(1)
		frame_start = MilliSecs()

		update_titles()
		update_stars()
		
		UpdateWorld
		CopyRect 0,0,800,600,0,0,FrontBuffer(),BackBuffer()
		RenderWorld

		frame_time = MilliSecs() - frame_start	
		;show_info()		

		Flip(1)
	Wend
	
	kill_titles()
	kill_stars()
	
	ClearWorld
		
	End


;
; Update title animation.
;
Function update_titles()

	Local y#
	
	Select title_state
		Case 0	; Prepare title pixels for exploding...

			title_state = 1
			explode_count = EXPLODE
			
			For this.grid_pix = Each grid_pix
				this\x = Rnd(-1,1)
				this\y = Rnd(-1,1)
				this\z = Rnd(-1,1)
				this\rot = Rnd(-1,1)
			Next		

		Case 1	; Explode title pixels...

			For this.grid_pix = Each grid_pix
				TranslateEntity this\ent,this\x,this\y,this\z
				
				; Pitch, yaw and roll rotations must be done separately so that
				; we can reverse the process correctly when reconstructing!
				TurnEntity this\ent,this\rot,0,0
				TurnEntity this\ent,0,this\rot,0
				TurnEntity this\ent,0,0,this\rot
			Next
			
			explode_count = explode_count - 1

			If explode_count = 0
				title_state = 2
				explode_count = EXPLODE
			EndIf		

		Case 2	; Implode/reconstruct exploded title...

			For this.grid_pix = Each grid_pix
				TranslateEntity this\ent,-this\x,-this\y,-this\z
				
				; Do pitch, yaw and roll rotations separately and in REVERSE order
				; from explosion so pixels reconstruct correctly!
				TurnEntity this\ent,0,0,-this\rot
				TurnEntity this\ent,0,-this\rot,0
				TurnEntity this\ent,-this\rot,0,0
			Next
			
			TurnEntity piv,0,-1,0
			
			explode_count = explode_count - 1
			If explode_count = 0 Then title_state = 3				

		Case 3	; Spin the title...

			; Spin faster when showing back and slower when showing front.
			y = Abs(EntityYaw(piv))/2.0
			TurnEntity piv,0,-(.3 + (Sin(y) * (y/20))),0
			
			; Randomly explode title.
			If Rand(1,400) = 100 Then title_state = 0
	End Select
	
End Function


;
; Create the game title grid.
;
Function make_titles()

	Local title_rows% = 0
	Local title_cols% = 0
	Local grid_row$, datum$
	Local pix.grid_pix
	Local grid_step#
	Local tl_x#, tl_y#
	Local title_width# = 18.0
	Local title_height#
	Local pix_size#
			
	; Find width and height of title grid from DATA
	Restore TITLE_DATA
	Read grid_row$
	title_cols = Len(grid_row$)
	Repeat
		title_rows = title_rows + 1
		Read grid_row$
	Until grid_row$ = "."
	
	title_height = title_rows * (title_width/title_cols)

	grid_step = title_width/(title_cols-1)
	tl_x = -title_width/2.0
	tl_y = title_height/2.0

	pix_size# = grid_step/2.0 - 0.04
	ScaleEntity piv,pix_size,pix_size,pix_size
	
	Restore TITLE_DATA
	 
	For r = 1 To title_rows
		Read grid_row$

		For c = 1 To title_cols
			datum$ = Mid$(grid_row$,c,1)

			If datum$ <> " "
				pix = New grid_pix
				pix\ent = CopyEntity(title_ent,piv)
				
				PositionEntity pix\ent,tl_x + ((c-1)*grid_step),tl_y - ((r-1)*grid_step),0,True

				Select datum$
					Case "1"
						EntityColor pix\ent,150,50,0
						ScaleEntity pix\ent,1,1,4
					Case "2"
						EntityColor pix\ent,0,150,0
						ScaleEntity pix\ent,1,1,6
					Case "3"
						EntityColor pix\ent,150,150,0
						ScaleEntity pix\ent,1,1,2
					Case "4"
						EntityColor pix\ent,150,0,0
						ScaleEntity pix\ent,1,1,4
					Default
						EntityColor pix\ent,100,50,0
				End Select
			EndIf
		Next
	Next
		
End Function


;
; Free all mem used by title.
;
Function kill_titles()

	For this.grid_pix = Each grid_pix
		FreeEntity this\ent
		Delete this
	Next

End Function


;
; Show debug info
;
Function show_info()
	
	frame_count = frame_count + 1

	If MilliSecs() > fps_timeout Then
		fps_timeout = MilliSecs() + 1000 
		fps = frame_count 
		frame_count = 0 
	EndIf 
	
	If frame_time > slowest_frame Then slowest_frame = frame_time
	
	Color 0,0,100
	Rect 0,0,200,80,1
	Color 255,255,255
	
	Text 10,10," Triangles: " + TrisRendered()
	Text 10,25," Millisecs: " + frame_time
	Text 10,40,"   slowest: " + slowest_frame
	Text 10,55,"       FPS: " + fps

End Function


;
; Create and initialize stars.
;
Function make_stars()

	For n = 1 To TOTAL_STARS
		this.part = New part
		
		this\ent = CreateSprite()
		EntityColor this\ent,255,255,255
		EntityAutoFade this\ent,200,500
	
		init_star(this)
		update_stars()
	Next

End Function


;
; Free all mem used by stars.
;
Function kill_stars()

	For this.part = Each part
		FreeEntity this\ent
		Delete this
	Next

End Function


;
; Update star field.
;
Function update_stars()

	For this.part = Each part

		If Not EntityInView(this\ent,cam)
			init_star(this)
		Else
			TranslateEntity this\ent,this\x,this\y,this\z
		EndIf
	Next

End Function


;
; Spawn/re-spawn star.
;
Function init_star(star.part)

	r#=Rnd(0.0,360.0)
	star\x = Cos(r)/2.0
	star\y = Sin(r)/2.0
	star\z = -Rnd(.01,.5)

	PositionEntity star\ent,star\x*50,star\y*50,500

End Function


.TITLE_DATA
Data " ttttttttttttttttttttttttttttttttttttttttt "
Data "t                                         t"
Data "t 111 111 111 111 111     111 1 1   1 111 t"
Data "t 1   1 1 1 1 1   1        1  1 11 11 1   t"
Data "t 111 111 111 1   11  111  1  1 1 1 1 11  t"
Data "t   1 1   1 1 1   1        1  1 1   1 1   t"
Data "t 111 1   1 1 111 111      1  1 1   1 111 t"
Data "t                 2222                    t"
Data "t 1 1  1 1   1  22222222  11  111 111 111 t"
Data "t 1 11 1 1   1 2244224422 1 1 1   1 1 1   t"
Data "t 1 1 11  1 1  2222222222 1 1 11  11  111 t"
Data "t 1 1  1  1 1    22  22   1 1 1   1 1   1 t"
Data "t 1 1  1   1    22 22 22  111 111 1 1 111 t"
Data "t              2        2                 t"
Data " ttttttttttttt      3     tttttttttttttttt "
Data "                   3                       "
Data "                    3                      "
Data "                   3                       "
Data "."
