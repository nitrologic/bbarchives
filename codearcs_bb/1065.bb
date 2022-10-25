; ID: 1065
; Author: big10p
; Date: 2004-06-01 18:06:02
; Title: shifted grid collision demo
; Description: How to do FAST 2D col-det with large amount of sprites

;
; 2D 'Shifted Grid' collision detection demo by big10p (A.K.A. Chris Chadwick) 2004.
;
; Written with Blitz3D v1.86
;
; Based on an original C/C++ article by Tom Moertel.
;
;
; The 'shifted grid' method is great for doing collision detection when huge numbers
; of sprites are involved! Basically, it involves dividing the play area into a grid of
; sectors and then only checking for collisions between sprites that occupy the same sector.
; Because of the mechanics of this method, it's also necessary to 'shift' this grid right,
; down and down-right, re-checking for collisions each time. This may sound wasteful/slow but
; it's really not - it's MUCH faster than checking every sprite against every other sprite!
;
; This simple demo confines all sprites to a single screen but the shifted grid method
; also works well with large scrolling playfields, when you still need to check for collisions
; between objects that are currently off-screen.
;
; The key to getting the best performance out of this method is in setting the grid sectors
; to the optimal size (see SECTOR_SIZE, below). What is optimal? That depends on the type of
; situation it's being used in, for example:
;  - If the objects in your game tend to bunch together into large clusters, use a small
;    sector size.
;  - If your game has a large, scrolling playfield and the objects tend to be fairly well
;    spaced apart, use a larger sector size.
;
; Basically: set SECTOR_SIZE so that it's unlikely that a large amount of sprites will ever
; occupy a single sector of this size! ;)
;

	Const SCREEN_WIDTH%  = 800
	Const SCREEN_HEIGHT% = 600
	
	Graphics SCREEN_WIDTH,SCREEN_HEIGHT,32
	SetBuffer BackBuffer()

	SeedRnd MilliSecs()

	Global frame_count%
	Global fps%
	Global slowest_fps%
	Global fps_timeout%
	Global frame_time%
	Global slowest_frame%
	Global frame_start%
	fps_timer = CreateTimer(60)
	slowmo% = False
	
	; Pseudo sprite object, just for demo purposes.
	Type spriteT
		Field x#, y#
		Field vx#, vy#
		Field r%, g%, b%
		Field width%, height%

		Field sector_link.spriteT	; Links all sprites within a given sector.
		Field hit%					; Flag to indicate if sprite has collided.
	End Type

	; Collision detection grid control consts/vars.
	; Note: to work properly, SECTOR_SIZE must conform to the following:
	; 1) Must be a power of 2 in order to enable the fast method of sorting sprite's
	;    into sectors, using a bit-shift operation with find_sector (see update_sectors()).
	; 2) Must be big enough to catch collisions of sprites with the largest bounding box length.
	;    That is: biggest bounding box length allowed = (SECTOR_SIZE/2)+1
	;    E.g., A sector size of 64 will work for sprite's upto 33x33 in size.

	Const SECTOR_SIZE%   = 32						; Length of sector side (in pixels).
	Const SECTOR_SHIFT%  = SECTOR_SIZE/2					; Sector offset for 4-shift operation.
	Const NUM_X_SECTORS% = (SCREEN_WIDTH+SECTOR_SIZE-1)/SECTOR_SIZE		; Horizontal sectors in grid.
	Const NUM_Y_SECTORS% = (SCREEN_HEIGHT+SECTOR_SIZE-1)/SECTOR_SIZE	; Vertical sectors in grid.
	Global find_sector%  = Log(SECTOR_SIZE)/Log(2)				; Right bit-shift to get sector from coord.

	; Linked list heads - one for each sector in the collision detection grid.	
	Dim sector_head.spriteT(NUM_X_SECTORS,NUM_Y_SECTORS)
	
	Const NUM_SPRITES% = 300
	create_sprites(NUM_SPRITES)	

	
	; --- Main Loop ---
	
	While Not KeyHit(1)
		frame_start = MilliSecs()

		Cls

		If KeyHit(57) Then slowmo = Not slowmo

		act_on_collisions()		

		; Draw all sprites and update their position.
		For spr.spriteT = Each spriteT
			Color spr\r,spr\g,spr\b
			Rect spr\x,spr\y,spr\width,spr\height,1

			new_x# = spr\x + spr\vx
			If new_x < 0 Or new_x > SCREEN_WIDTH-1 Then spr\vx = -spr\vx
			spr\x = spr\x + spr\vx
			
			new_y# = spr\y + spr\vy
			If new_y < 0 Or new_y > SCREEN_HEIGHT-1 Then spr\vy = -spr\vy
			spr\y = spr\y + spr\vy
		Next
	
		frame_time = MilliSecs() - frame_start	
		show_info()

		WaitTimer(fps_timer)
		VWait 
		Flip(0)

		If slowmo Then Delay 200
	Wend

	FreeTimer fps_timer
	End


;
; Creates requested number of sprites.
;
; Params:
; num - Number of sprites to create.
;
Function create_sprites(num%)

	For n = 1 To num
		spr.spriteT = New spriteT

		spr\r = 50 + Rand(205)
		spr\g = 50 + Rand(205)
		spr\b = 50 + Rand(205)

		spr\x = Rand(0,SCREEN_WIDTH-1)
		spr\y = Rand(0,SCREEN_HEIGHT-1)

		vel# = Rnd(.1,3.0)
		vx# = vel-Rnd(0,vel)
		vy# = vel-vx
		If Rand(0,1) Then vx = -vx
		If Rand(0,1) Then vy = -vy
		spr\vx = vx
		spr\vy = vy

		s = Rand(4,8)
		spr\width = s
		spr\height = s
	Next

End Function

	
;
; Calculate and act on all collisions.
;
Function act_on_collisions()

	; Reset all sprites' collision hit flag.
	For spr.spriteT = Each spriteT
		spr\hit = False
	Next

	; Check all sectors, shifting the grid as we go in order
	; to catch collisions that occur across a sector boundary.
	; Note: due to this mechanism, it's possible some collisions
	; will be detected more than once. The actual collision
	; detection/action code must take this into account.
				
	update_sectors(0, 0)
	check_all_sectors()

	update_sectors(SECTOR_SHIFT, 0)
	check_all_sectors()

	update_sectors(0, SECTOR_SHIFT)
	check_all_sectors()

	update_sectors(SECTOR_SHIFT, SECTOR_SHIFT)
	check_all_sectors()
	
End Function


;
; Put all sprites into correct sector linked list.
;
; Params:
; shift_x_off - Horizontal offset to shift sector grid by.
; shift_y_off - Vertical offset to shift sector grid by.
;
Function update_sectors(shift_x_off%, shift_y_off%)

	; Reset sector linked list heads.
	For sx% = 0 To NUM_X_SECTORS
		For sy% = 0 To NUM_Y_SECTORS
			sector_head(sx,sy) = Null
		Next
	Next

	; Put each sprite into it's sector's linked list.
	For spr.spriteT = Each spriteT
		sx = Int(spr\x + shift_x_off) Shr find_sector
		sy = Int(spr\y + shift_y_off) Shr find_sector
		spr\sector_link = sector_head(sx,sy)
		sector_head(sx,sy) = spr
	Next
	
End Function


;
; Check all sectors for collisions.
;
Function check_all_sectors()

	For sx% = 0 To NUM_X_SECTORS
		For sy% = 0 To NUM_Y_SECTORS
			check_sector_collisions(sector_head(sx,sy))
		Next
	Next

End Function


;
; Checks for collisions between all sprites in the given sector linked list.
;
; Params:
; sprite_list - Linked list of sprites to check collisions between.
;
Function check_sector_collisions(sprite_list.spriteT)

	; Ignore sectors with 0 or only 1 sprite in them.
	If sprite_list = Null
		Return
	ElseIf sprite_list\sector_link = Null
		Return
	EndIf

	; Check for collisions between all sprites in this sector.
	spr1.spriteT = sprite_list
	While spr1\sector_link <> Null
	
		spr2.spriteT = spr1\sector_link
		While spr2 <> Null

			; Don't bother checking if spr1 & spr2 have already been involved in collisions.
			If spr1\hit = False Or spr2\hit = False
	
				; Time to actually see if these 2 sprites have collided!
				; For demo purposes, we'll simply see if their rects are intersecting.
				If RectsOverlap(spr1\x,spr1\y,spr1\width,spr1\height, spr2\x,spr2\y,spr2\width,spr2\height)
					; OK, spr1 & spr2 have collided! Let's simply swap their velocity vectors
					; in an 'attempt' to make them bounce off each other. :P
					temp_x# = spr1\vx
					temp_y# = spr1\vy
					spr1\vx = spr2\vx
					spr1\vy = spr2\vy
					spr2\vx = temp_x
					spr2\vy = temp_y
					
					; Indicate these 2 sprites were involved in a collision.
					spr1\hit = True
					spr2\hit = True
				EndIf

			EndIf

			spr2 = spr2\sector_link
		Wend
		
		spr1 = spr1\sector_link
	Wend
	
End Function


;
; Display debug info
;
Function show_info()
	
	If fps_timeout
		frame_count = frame_count + 1

		If MilliSecs() > fps_timeout Then
			fps_timeout = MilliSecs() + 1000 
			fps = frame_count 
			frame_count = 0 
		
			If fps < slowest_fps Or slowest_fps = 0 Then slowest_fps = fps
		EndIf 
		
		If frame_time > slowest_frame Then slowest_frame = frame_time
		
		If 1
		Color 255,255,255
		Text 10,10,"(Press SPACEBAR to toggle slow motion)"
		
		Text 10,40,"   Sprites: " + NUM_SPRITES
		Text 10,55," Millisecs: " + frame_time
		Text 10,70,"   Slowest: " + slowest_frame
		Text 10,85,"       FPS: " + fps
		Text 10,100,"     Worst: " + slowest_fps
		
		Text 10,130,"Sector Size: " + SECTOR_SIZE
		Text 10,145,"  X Sectors: " + NUM_X_SECTORS
		Text 10,160,"  Y Sectors: " + NUM_Y_SECTORS
		Text 10,175,"      Shift: " + find_sector
		EndIf
	Else
		; First call initialization.
		fps_timeout = MilliSecs() + 1000 
	EndIf
	
End Function
