; ID: 1999
; Author: Matt Merkulov
; Date: 2007-04-21 05:58:08
; Title: Game of Brutal Koloboks
; Description: Game reminding Crimsonland a bit, powered by smart 2D engine

'Game of Brutal Koloboks by Matt Merkulov

'Controls:
'WASD - move protagonist
'Mouse - move target
'Left mouse button - fire
'Right mouse button - Force
'Mouse wheel - zoom
'Space - teleport

Framework brl.glmax2d ' Base module - an engine based on OpenGL
Import brl.random ' Generator of random numbers
Import BRL.Basic ' From this module command Incbin is used
Import BRL.PNGLoader ' Loading of PNG images

Incbin "new_images.png" ' It is for keeping image in an exe

Const sxsize = 800, sysize = 600, color_depth = 32 ' Resolution of the screen and depth of color

Const tilesize = 64 ' the size of the tile / sprite

' Auxiliary constants
Const tilesize2 = tilesize / 2, tilesize4 = tilesize / 4, tilesize8 = tilesize / 8
Const tilesize16 = tilesize / 16, tilesize32 = tilesize / 32

Const sxsize2 = sxsize / 2, sysize2 = sysize / 2
Const sxsize4 = sxsize / 4, sxsize34 = sxsize * 3 / 4
Const sysize34 = sysize * 3 / 4, sxsize24 = sxsize / 2 -4

Const fxsize = 160, fysize = 120 ' Size of a field in tiles
Const fblurq = 5 ' Blur passes quantity for temporarily generated auxiliary heightmap of a field
Const sand_threshold# = 0.4, grass_threshold# = 0.5 ' Thresholds of height for sand and grass
Global fdx#, fdy# ' Shift of a displayed part of a field

Const kolobokq = 500 ' Wild koloboks
Global speedpersec# = 1.0 ' Modifier of speed (tiles / se?)
Global angpersec# = 90.0 ' Modifier of angular speed (degrees / se?)

Global sc# = 1.0, tilesc# ' Magnification in pixels and tiles
Global dtim# ' Time of processing of the previous cycle
Global timspeed# ' Modifier for moving depending on dtim#
Global timang# ' the Modifier for turn depending on dtim#
Const minms = 100 ' Maximum seconds for cycle
Const cam_speed# = 2.0 ' Relative speed of reaction of the camera on mouse movements
Const magn_speed# = 2.0 ' Relative speed of reaction of scale on mouse wheel rotation 
Global camx#, camy# ' Current coordinates of the camera

Global layer_order:TList = CreateList()' List of displayed layers in order of appearance
Global actingobj:TList = CreateList()' List for active objects

Const showcollisions = False ' Display of collisions(now switched off)
Global ccnt, objcnt, chcnt ' Counters of collisions, objects, checks of collisions per second

Const force_reload_time = 7000, force_power# = 3.0 ' Time of Force "reloading", its power
Global force_time = 1000, force_radius# = 5.0 ' Time of action of Force, radius of action
Global force_reload, force_effect ' Time of "reload"'s end and effect of Force

Const fireable_percent = 25 ' Percent of fireable ground koloboks
Const min_fire_distance# = 7.0 ' Minimal distance of firing
Const min_enemy_distance = 20 ' Minimal distance up to the enemy in the beginning of game

Const constant_bonustypeq = 7, temporary_bonustypeq = 5 ' Quantity of constant and temporary bonuses
Const constant_bonus_crateq = 10 ' Quantity of crates with constant bonuses (for every)
Const temporary_bonus_crateq = 100 ' Quantity of crates with temporary bonuses
Const empty_crates_percent = 30 ' Percent of empty crates
Const crate_bits_packq = 4 ' Quantity of variants of pieces of a box
Const bonustypeq = constant_bonustypeq + temporary_bonustypeq

' Constant bonuses
Const BONUS_BULLET_DAMAGE = 0 ' Increase damage of bullets
Const BONUS_BULLET_SPEED = 1 ' Increase the speed of bullets
Const BONUS_BULLET_LIFETIME = 2 ' Increase in time of a life of a bullet
Const BONUS_RELOAD_TIME = 3 ' Reduction of intervals between shots
Const BONUS_MAX_HEALTH = 4 ' Increase maximal quantity of health
Const BONUS_SPEED = 5 ' Increase player's speed
Const BONUS_ESOURCE = 6 ' Energy sources (it is necessary to collect all for finishing of game)
Global esource_collected, light

' Time(Temporary)bonuses
Const bonus_threshold = constant_bonustypeq
Const BONUS_HEALTH = bonus_threshold + 0 ' Health
Const BONUS_TEMPORARY_FIREPOWER = bonus_threshold + 1 ' Temporary increase fire power
Const BONUS_BOMB = bonus_threshold + 2 ' Bomb!
Const BONUS_TEMPORARY_SPEED = bonus_threshold + 3 ' Temporary acceleration
Const BONUS_TEMPORARY_INVULNERABILITY = bonus_threshold + 4 ' Temporary invulnerability

Global temporary_firepower, temporary_speed, temporary_invulnerability ' Time of the termination(ending)of action of bonuses

Const fading_time = 1000, damage_time = 400 ' Time of "fading out", "reddening" from damages
Const NOT_YET = 1000000000, INDESTRUCTIBLE = 1000000000 ' Constants "has not died yet", "indestructible"

Const TM_IDLE = 0 ' Playng as usually
Const TM_READY = 1 ' Preparing for teleportation (waiting)
Const TM_DECREASING = 2 ' Decreasing
Const TM_ENLARGING = 3 ' Growing on a new place
Global teleport = NOT_YET, teleport_mode = TM_IDLE ' Time of the ending of a cycle of teleportation mode
Const teleport_ready_time = 5000 ' Time of preparation for teleportation
Const max_teleport_radius = 50 ' Maximal distance in tiles for teleportation

Type layer_obj Abstract
	Field collision_with:TList = CreateList()' List of layers, with which this layer collides

	Method collides_with(layer:layer_obj)
		If tile_layer_obj(layer) Then RuntimeError "Tile layers cannot collide - use tile collision layer"
		collision_with.addlast layer
	End Method

	Method draw()
	End Method
End Type

Const TILE_DONT_DRAW = -1
Type tile_layer_obj Extends layer_obj
	Field image:TImage ' Images for tiles
	Field frame[fxsize, fysize]' Number of tile for each cell

	Method collides_with(layer:layer_obj)
		RuntimeError "Tile layers cannot collide -use tile collision layer"
	End Method

	Function add:tile_layer_obj(tile_image:TImage, clearing = True) ' Adding tile layer
		l:tile_layer_obj = New tile_layer_obj
		l.image = tile_image
		If clearing Then
			For y = 0 Until fysize ' Installation "do not draw tile" for all cells
				For x = 0 Until fxsize
					l.frame[x, y] = TILE_DONT_DRAW
				Next
			Next
		End If
		layer_order.addlast l ' Adding layer in the list of displayed ones
		Return l
	End Function

	Method draw()' Drawing layer
		SetScale tilesc#, tilesc# 
		scr2field 0, 0, x1#, y1# 
		scr2field sxsize - 1, sysize - 1, x2#, y2# 

		xx1 = Max(0, Floor(x1#)) ' Determining what part of field is currently visible on screen
		xx2 = Min(Ceil(x2#), fxsize - 1)
		yy1 = Max(0, Floor(y1#))
		yy2 = Min(Ceil(y2#), fysize - 1)

		For y = yy1 To yy2
			For x = xx1 To xx2
				If frame[x, y] >= TILE_DRAW Then ' Check, whether it is necessary to draw tile
					field2scr x, y, sx#, sy# 
					DrawImage image, sx#, sy#, frame[x, y]
				End If
			Next
		Next
	End Method
End Type

Type tile_collision_layer_obj Extends layer_obj
	Field collision[fxsize, fysize] ' Collision with tile ("solid" / "hologram")

	Function add:tile_collision_layer_obj()
		Return New tile_collision_layer_obj
	End Function
End Type

Type object_layer_obj Extends layer_obj
	Field objects:TList[fxsize, fysize]' the List of objects for each cell, being on it

	Function add:object_layer_obj()
		l:object_layer_obj = New object_layer_obj
		For y = 0 Until fysize ' Initialization of lists
			For x = 0 Until fxsize
				l.objects[x, y] = CreateList()
			Next
		Next
		layer_order.addlast l
		Return l
	End Function

	Method draw()
		scr2field 0, 0, x1#, y1# 
		scr2field sxsize - 1, sysize - 1, x2#, y2# 

		xx1 = Max(0, Floor(x1# -0.5))
		xx2 = Min(Floor(x2# + 0.5), fxsize - 1)
		yy1 = Max(0, Floor(y1# -0.5))
		yy2 = Min(Floor(y2# + 0.5), fysize - 1)

		For y = yy1 To yy2
			For x = xx1 To xx2
				For o:base_obj = EachIn objects[x, y]
					o.draw
				Next
			Next
		Next
		reset_transformations
	End Method
End Type

Const CT_IMMATERIAL = 0 ' Type of collision model - non - material
Const CT_CIRCULAR = 1 ' Type of collision model - a circle
Const CT_SQUARE = 2 ' Type of collision model - a square
' Base type for objects
Type base_obj
	Field x#, y#, size# = 1, angle# ' Coordinates, size(in tiles), an angle of object sprite's turn
	Field speed# ' Speed of object (tiles / sec)
	Field moving_angle# ' Current angle of movement
	Field r = 255, g = 255, b = 255 ' Color of object (by default white)
	Field image:TImage, frame ' Image for object
	Field tilex, tiley ' Coordinates of tile on which this object being on 
	Field act_link:TLink, tile_link:TLink ' References to this object from lists of active objects and objects of a tile
	Field layer:object_layer_obj ' Layer of object
	Field coll_type = CT_CIRCULAR, radius# = 0.5 ' Type of collision model and its radius
	Field health# ' Health of object
	Field death = NOT_YET, damage_end ' Time of death (it is not defined yet), time of the ending of "reddening"

	Const ONLY_ON_GROUND = True ' Constant for accommodation of object only on a land
	Method place_find(onlyonground = False)' Search of a place for accommodation of object
		Repeat
			x = Rnd(1.0, fxsize - 1.01)
			y = Rnd(1.0, fysize - 1.01)
			tilex = Floor(x)
			tiley = Floor(y)
			' Definition of kolobok's groundness / waterness
			If layer_sand.collision(tilex, tiley) Then layer = layer_ground_koloboks Else layer = layer_water_koloboks
			' Check of a finding on a land (for accommodation only on a land) and on absence of collisions
			If layer = layer_ground_koloboks Or onlyonground = False Then If Not collision(x#, y#) Then Exit
		Forever
	End Method

	Method random_color()' Definnig random (but not so dark) color for object
		Repeat
			r = Rand(0, 255)
			g = Rand(0, 255)
			b = Rand(0, 255)
		Until r + g + b >= 255
	End Method

	Const ACTIVE = True, INACTIVE = False
	Method register(acting = ACTIVE)' Registration of object in lists
		tilex = Floor(x#)
		tiley = Floor(y#)
		tile_link = layer.objects(tilex, tiley).addlast(Self)' Registration in the list of objects of a cell (tile)
		If acting Then act_link = actingobj.addlast(Self)' Registration in the list of active objects
		objcnt:+1
	End Method

	Method draw()' Drawing of object
		field2scr x#, y#, sx#, sy# 
		SetScale size# * tilesc#, size# * tilesc# 
		SetRotation angle# 

		dmg = damage_end - MilliSecs() ' "Reddening" from damages
		If dmg > 0 Then
			k1# = 1.0 * dmg / damage_time; k2# = 1.0 - k1# 
			SetColor k1# * 255 + k2# * r, k2# * g, k2# * b
		Else
			SetColor r, g, b ' Setting natural color
		End If

		If death = NOT_YET Then
			SetAlpha 1 ' If yet has not started to disappear, opaque
		Else
			SetAlpha limit(.001* (death - MilliSecs()), 0, 1)' Else disappear...
			If death < MilliSecs() Then destroy ' And in the end it is destroyed absolutely
		End If

		If Self = player Then
			If temporary_firepower > MilliSecs() Then
				col = 191 + 64 * Sin(MilliSecs()) ' Flickering yellow color of the player with fire power
				SetColor col, col, 0
			End If
			If temporary_invulnerability > MilliSecs() Then SetAlpha 0.5 ' half-transparency of invulnerable
			Select teleport_mode
				Case TM_READY; SetAlpha 0.75 + 0.25 * Sin(MilliSecs())' Cyclic change of a transparency during preparation for teleportation
				Case TM_DECREASING; s# = sc# * size# / tilesize * Max(0.0, 1.0* (teleport - MilliSecs()) / fading_time); SetScale s#, s# ' Reduction
				Case TM_ENLARGING; s# = sc# * size# / tilesize * Min(1.0, 1.0 - 1.0* (teleport - MilliSecs()) / fading_time); SetScale s#, s# ' Occurrence in a new place
			End Select
		End If

		DrawImage image, sx#, sy#, frame
	End Method

	Method move(newx#, newy#)' Correct moving
		newtilex = Floor(newx#)
		newtiley = Floor(newy#)
		If tilex <> newtilex Or tiley <> newtiley Then ' If the object has moved to other cell, 
			RemoveLink tile_link ' Remove it from the list of an old cell
			tilex = newtilex
			tiley = newtiley
			tile_link = layer.objects[tilex, tiley].addlast(Self) ' Register in the list of new cell
		End If
		x# = newx#
		y# = newy#
	End Method

	Method try_move(newx#, newy#)
		If Not collision(newx#, newy#) Then move newx#, newy#; Return True
	End Method

	Method try_move_ang(ang#, spd#, ma_change = False)
		If try_move(x# + timspeed# * Cos(ang#) * spd#, y# + timspeed# * Sin(ang#) * spd#) Then
			If ma_change Then moving_angle# = ang# 
			Return True
		End If
	End Method

	Method collision2(o:base_obj, newx#, newy#)' Check of object on collision with another
		Select True
			Case coll_type = CT_CIRCULAR ' If model of the given object - a circle
				Select True
					Case o.coll_type = CT_CIRCULAR ' And model of the second object - a circle too (a circle with circle)
						dx# = newx# -o.x# 
						dy# = newy# -o.y# 
						' Checking, whether distance between objects, than the sum of their radiuses there is less
						If Sqr(dx# * dx# + dy# * dy#) < o.radius# + radius# Then ccnt:+1; Return True
					Case o.coll_type = CT_SQUARE ' And if model of the second object - a square (a circle with a square)
						If(o.x# - o.radius# <= newx# And newx# <= o.x# + o.radius#) Or (o.y# - o.radius# <= newy# And newy# <= o.y# + o.radius#) Then
							dx# = Abs(newx# -o.x#)
							dy# = Abs(newy# -o.y#)
							sumr# = o.radius# + radius# 
							If dx# < sumr# And dy# < sumr# Then ccnt:+1; Return True
						Else
							dx# = Min(Abs(newx# -o.x# -o.radius#), Abs(newx# -o.x# + o.radius#))
							dy# = Min(Abs(newy# -o.y# -o.radius#), Abs(newy# -o.y# + o.radius#))
							If Sqr(dx# * dx# + dy# * dy#) < radius# Then ccnt:+1; Return True
						End If
					Default ' But here if the second object is non - material - collision is not present
						Return False
				End Select
			Case coll_type = CT_SQUARE ' If model of the given object - a square
				If o.coll_type = CT_SQUARE Then ' And model of the second object - a square too
					dx# = Abs(newx# -o.x#)
					dy# = Abs(newy# -o.y#)
					sumr# = o.radius# + radius# 
					' Checking, whether according coordinate difference is less than the sum of radiuses
					If dx# < sumr# And dy# < sumr# Then ccnt:+1; Return True
				Else ' Else we check collision of the second object with given (interchange the position)
					Return o.collision2(Self, newx#, newy#)
				End If
			Default ' Non - material object do not collide
				Return False
		End Select
	End Method

	Method collision(newx#, newy#) ' Check of the given object on collision with something
		' Collision with borders of a field (it will complicate other checks, therefore we leave)
		If newx# < 1.0 Or newy# < 1.0 Or newx# >= fxsize - 1.0 Or newy# >= fysize - 1.0 Then
			boundaries_collision_act
			Return True
		End If
		For l:layer_obj = EachIn layer.collision_with ' Cycle on all layers of a collision
			tl:tile_collision_layer_obj = tile_collision_layer_obj(l)
			If tl Then ' If it's a tile collison layer, 
				For yy = Floor(newy# -radius#)To Floor(newy# + radius#)
					For xx = Floor(newx# -radius#)To Floor(newx# + radius#)
						If tl.collision(xx, yy) Then
							tile_object.x# = xx + 0.5
							tile_object.y# = yy + 0.5
							If collision2(tile_object, newx#, newy#) Then collided = True; tile_collision_act xx, yy
						End If
					Next
				Next
			Else ' Else it's object layer
				ol:object_layer_obj = object_layer_obj(l)
				x2 = Floor(newx#)
				y2 = Floor(newy#)
				For yy = y2 - 1 To y2 + 1
					For xx = x2 - 1 To x2 + 1
						For o:base_obj = EachIn ol.objects[xx, yy]
							If Self <> o Then
								chcnt:+1
								If showcollisions Then ' Displaying checks of collisions by lines
									field2scr o.x#, o.y#, sx1#, sy1# 
									field2scr newx#, newy#, sx2#, sy2# 
									DrawLine sx1#, sy1#, sx2#, sy2# 
								End If
								If collision2(o, newx#, newy#) Then collided = True; object_collision_act o
							End If
						Next
					Next
				Next
			End If
		Next
		Return collided
	End Method

	Method act()' Actions of objects
	End Method

	Method object_collision_act(o:base_obj)' Actions at collision with objects
	End Method

	Method tile_collision_act(xx, yy)' Actions at collision with "solid" tiles
	End Method

	Method boundaries_collision_act()' Actions at collision with borders of a map
	End Method

	Method damage(amount#)' Taking damage
		If death < NOT_YET Then Return ' If it's already disappearing then leaving
		If health# = INDESTRUCTIBLE Then Return ' If basically it is indestructive, then leaving too
		If Self = player And temporary_invulnerability > MilliSecs() Then Return ' If it is temporarily indestructive - leaving
		health# = health# -amount# ' we Reducing health
		damage_end = damage_time + MilliSecs()' Settng "reddening"
		If health <= 0 Then ' If health on zero, 
			death = fading_time + MilliSecs()' Object starts to disappear
			' the Crate disappears at once, the others become non - material
			If crate_obj(Self) = Null Then coll_type = CT_IMMATERIAL Else death = 0
		End If
	End Method

	Method destroy()' Correct destruction of object
		If act_link <> Null Then RemoveLink act_link ' Removing object from the list of active ones
		RemoveLink tile_link ' Removing object from the list of cell's objects
		objcnt:-1
	End Method
End Type

Global tile_object:base_obj = New base_obj
tile_object.radius# = 0.5
tile_object.coll_type = CT_SQUARE

' Base type for koloboks
Type kolobok_obj Extends base_obj
	Field bullet_reload, bullet_reload_time ' Time of the reload's ending , reload time
	Field bullet_speed#, bullet_lifetime = 2000 ' Speed and time of a life of a bullet of this kolobok
	Field bullet_damage# ' Damage of a bullet
	Field max_health# = 1 ' Maximal health
	Field bite_damage#, bite_reload ' Damage from a bite and time of an opportunity of a following bite
	Field bite_reload_time, bite ' Interval between bites, an auxiliary flag

	Function create:kolobok_obj()' Creation of wild kolobok
		o:kolobok_obj = New kolobok_obj
		o.random_color
		o.image = kolobok
		o.moving_angle# = Rnd(0, 360)
		If Rand(1, 100) > fireable_percent And o.frame = 1 Then ' Parameters for not able to shoot
			o.bullet_reload = 1000000000
			o.bullet_reload_time = 1000
			o.bullet_lifetime = 0
			o.bullet_damage# = 0
			o.bullet_speed# = 0
		Else ' Parameters for able to shoot
			o.bullet_reload_time = Rand(300, 1000)
			o.bullet_lifetime = Rand(1000, 4000)
			o.bullet_damage# = Rnd(1, 5)
			o.bullet_speed# = Rnd(0.5, 1.5)
		End If
		o.max_health = Rand(50, 200)
		o.health = o.max_health
		o.bite_damage# = Rnd(4, 12)
		o.bite_reload_time = Rand(200, 500)
		' Calculation of the size and speed on set of parameters
		o.size# = (o.max_health - 50) / 150.0 + o_bullet_speed# / 1.5 + o.bullet_lifetime / 4000.0
		o.size# :+o.bullet_damage# / 5.0 + (o.bite_damage# -4.0) / 8.0 + (500 - o.bite_reload_time) / 300.0
		o.size# :+(1000.0 - o.bullet_reload_time) / 1000.0
		o.size# = limit(o.size / 7.0 * 1.0 + 0.25, 0, 1.0)
		o.speed# = (1.25 - o.size#) * 2
		o.radius# = 0.4 * o.size# 
		o.place_find
		o.frame = (o.layer = layer_ground_koloboks)' For water koloboks - 0, for ground - 1
		o.register
		Return o
	End Function

	Method draw()' Drawing kolobok
		super.draw
		bar_draw
	End Method

	Method bar_draw()' Drawing of a strip of health
		field2scr x#, y#, sx#, sy# 
		barsize = 1.0 * size# * sc# ' Setting length (depending on the kolobok's size in pixels)
		If barsize > 4 And max_health <> health Then
			barsize2 = barsize / 2
			barheight = limit(Floor(max_health / 50) + 1, 1, 6)' Setting height depending on a maximum of health
			SetRotation 0
			SetScale 1, 1
			SetGrayColor 255
			k# = 1.0 * health / max_health
			DrawEmptyRect sx# -barsize2, sy# -barsize2 - 6, barsize - 1, barheight + 2
			SetColor 255* (1.0 - k#), 255 * k#, 0 ' Setting color: closer to a maximum - green, closer to 0 - red
			DrawRect sx# -barsize2 + 1, sy# -barsize2 - 5, k# * (barsize - 2), barheight
		End If
	End Method

	Method act()' Kolobok's actions
	If death < NOT_YET Then Return ' If kolobok disappearing, he will be idle

	angle# = ATan2(player.y - y#, player.x - x#)' the angle of "prompting" on the player

	If force_effect > MilliSecs() Then ' Calculating distance up to the player if Force works
		rad# = Sqr((player.x# -x#) * (player.x# -x#) + (player.y# -y#) * (player.y# -y#))
	Else
		rad# = 10000
	End If
	If rad# <= force_radius# Then ' If the distance up to the player is less than radius of action of Force, 
		' Trying to move away from the player
		try_move_ang angle# + 180.0, force_power# * Sin(90.0* (force_radius# -rad#) / force_radius#)
	Else
		' Else calculating, in what side to rotate
		dang# = calc_dangle(moving_angle#, angle# + 180* (temporary_firepower > MilliSecs()))
		' Also trying to move after turning
		If Not try_move_ang(moving_angle# + timang# * (1 - 2 * (dang# < 0)), speed#, True) Then
			' If move was not possible, 
			If bite Then ' If it is possible to bite, we'll stand and bite...
				moving_angle# = angle# 
				bite = False
			Else ' If it is impossible, we'll try make a sidestep
				If Not try_move_ang(moving_angle# + 90.0* (1 - 2 * Rand(0, 1)), speed#, True) Then
					' If it is impossible, we'll try to step in another side
					If Not try_move_ang(moving_angle# + 180.0, speed#, True) Then moving_angle# = Rnd(0.0, 360.0)
						' If we have absolutely clamped, next time we shall try a random angle
					End If
				End If
			End If
		End If

		If bullet_reload < MilliSecs() Then ' If time has come to shoot
			' And distance up to the player is less than maximal
			If Sqr((player.x# -x#) * (player.x# -x#) + (player.y# -y#) * (player.y# -y#)) <= min_fire_distance# Then
				' Creating the list and insert there all of nearby water koloboks
				near:TList = nearly_objects(CreateList(), tilex, tiley, 2, layer_water_koloboks)
				' And also ground ones
				near = nearly_objects(near, tilex, tiley, 2, layer_ground_koloboks)
				' But delete current kolobok and the player
				near.remove player
				near.remove Self
				' Because we shall check, whether there is another kolobok on a way of the bullet which have been released in the player
				For o:base_obj = EachIn near
					If kolobok_obj(o) Then
						' Calculating angle between a vector of a shot and a vector from center of shooting to center of checked kolobok
						dang# = Abs(calc_dangle(ATan2(y# -o.y#, x# -o.x#), ATan2(y# -player.y#, x# -player.x#)))
						' Checking is radius of kolobok is not less than length of an arch
						If Pi * Sqr((x# -o.x#) * (x# -o.x#) + (y# -o.y#) * (y# -o.y#)) * dang# / 180.0 < o.radius Then Return
					End If
				Next
				' If there are no koloboks on a way of a shot - firing safely 
				fire
			End If
		End If
	End Method

	Method object_collision_act(o:base_obj)
		If o = player Then ' Checking, if current kolobok have collided with the player
			If bite_reload < MilliSecs() Then ' If we are ready to bite
				player.damage(bite_damage)' Then bite
				bite_reload = MilliSecs() + bite_reload_time
			End If
			bite = True ' This flag shows, that we have seized the player and then we can stand at current place
		End If
	End Method

	Method fire()
		' The amendment for the speed at temporary acceleration
		If Self = player And temporary_speed > MilliSecs() Then spd# = 6.0 Else spd# = speed# 
		If Self = player And temporary_firepower > MilliSecs() Then ' Shooting with firepower
			bullet_obj.create x#, y#, 0.75, angle#, 4.0 + spd#, 2000, 25, Self, 0.5 * 0.3, r, g, b
			bullet_reload = MilliSecs() + 40
		Else ' Shooting in usual mode
			bullet_obj.create x#, y#, 0.5 * size#, angle#, bullet_speed# + spd#, bullet_lifetime, bullet_damage, Self, size# * 0.3, r, g, b
			bullet_reload = MilliSecs() + bullet_reload_time
		End If
	End Method

End Type

' the Player
Type player_obj Extends kolobok_obj
	Function create:player_obj()
		o:player_obj = New player_obj
		o.x# = 0.5 * fxsize ' Placing the player in the center of the field
		o.y# = 0.5 * fysize
		o.size# = 0.75
		o.radius# = 0.4 * o.size
		o.image = kolobok
		o.frame = 2
		o.speed# = 2.0
		o.bullet_reload_time = 450
		o.bullet_speed# = 1.0
		o.bullet_damage# = 2.5
		o.max_health# = 300
		o.health# = o.max_health# 
		o.layer = layer_ground_koloboks
		Repeat ' Moving the player to the right until he will not stand completely on a land and uncollided
			o.x:+0.5
		Until Not o.collision(o.x#, o.y#)
		o.register
		Return o
	End Function

	Method act()' Actions of the player
		If death < NOT_YET Then Return ' If we have already defeated, we're idle
		If teleport_mode = TM_IDLE Then ' If currently we're not teleportating 
			If KeyHit(KEY_SPACE) Then ' If the space key is pressed
				If Sqr(targetx# * targetx# + targety# * targety#) <= max_teleport_radius Then ' And the distance is not more than maximum
					If Not collision(player.x# + targetx#, player.y# + targety#) Then ' And also in a place of occurrence there are no collisions
						teleport_mode = TM_READY ' That we prepare for teleportation
						teleport = MilliSecs() + teleport_ready_time ' Setting time of next stage
					End If
				End If
			End If
		Else
			If teleport <= MilliSecs() Then ' If the cycle has come to the end, 
			teleport_mode = teleport_mode + 1 ' Passing to the following
			teleport = MilliSecs() + fading_time ' Setting time of a next cycle
			If teleport_mode = TM_ENLARGING And Not collision(player.x# + targetx#, player.y# + targety#) Then
				move player.x# + targetx#, player.y# + targety# ' It is moved to a point of teleportation after reduction
				fdx2# = fdx2# -targetx# 
				fdy2# = fdy2# -targety# 
				targetx# = 0
				targety# = 0
			ElseIf teleport_mode > TM_ENLARGING Then ' If the cycle of increasing is completed
				teleport_mode = TM_IDLE ' that we reset teleportation mode
			End If
		End If
		Return ' At teleportation it is necessary to stand quietly, therefore we leave a method
	End If

	If bullet_reload < MilliSecs()And MouseDown(1) Then fire ' If time to shoot have approached and fire key is pressed then fire
	
	If MouseDown(2)And force_reload <= MilliSecs() Then ' Using Force if the button is pressed and kolobok is ready
		force_reload = force_reload_time + MilliSecs()
		force_effect = force_time + MilliSecs()
	End If
	If force_effect > MilliSecs() Then size# = 0.75 + 0.5* (force_effect - MilliSecs()) / force_time Else size# = 0.75 ' "Swelling" from Force

	mov = False ' Calculating angle of a vector of movement, based on the pressed keys
	If KeyDown(KEY_S) Then ang2# = 90.0; mov = True
	If KeyDown(KEY_W) Then ang2# = -90.0; mov = True
	' If one of the previous keys is pressed - we'll modify an angle depending on previous value
	If KeyDown(KEY_A) Then ang2# = 180.0 - 0.5 * ang2#; mov = True
	If KeyDown(KEY_D) Then ang2# = 0.5 * ang2#; mov = True
	
	If Not mov Then Return ' If we are standing, there is nothing more to do
	
	' Modifier of speed for temporary acceleration
	If temporary_speed > MilliSecs() Then spd# = 6.0 Else spd# = speed# 
		' If there are no collisions, move
		try_move_ang ang2#, spd# 
	End Method

	Method destroy()
		' They kicked us well, so we shout
		Notify"AAAAAAAAAAAAAA!!! Whyyyyy???!!!"
		End
	End Method

	Method object_collision_act(o:base_obj)
		' If we have collided with a bonus and it is not taken yet - we'll take it
		bo:bonus_obj = bonus_obj(o)
		If bo Then If bo.death = NOT_YET Then bo.get
	End Method
End Type

' the Bullet
Type bullet_obj Extends base_obj
	Field parent:base_obj, damage# ' the Index on shooting and factor of damage

	' Creating a bullet
	Function create:bullet_obj(bx#, by#, bsize#, bangle#, bspeed#, blifetime, bdamage#, bparent:base_obj = Null, d# = 0, br = 255, bg = 255, bb = 255)
		bul:bullet_obj = New bullet_obj
		bul.layer = layer_bullets
		bul.x# = bx# + Cos(bangle#) * d# ' Displacement otn.The given coordinates
		bul.y# = by# + Sin(bangle#) * d# 
		bul.r = br
		bul.g = bg
		bul.b = bb
		bul.image = bullet
		bul.parent = bparent
		bul.angle# = bangle# 
		bul.size# = bsize# 
		bul.speed# = bspeed# 
		bul.radius = bsize# * 0.25
		bul.death = MilliSecs() + blifetime
		bul.damage# = bdamage# 
		bul.register
	End Function

	Method act()' Works simply - bullet flies forward before collision
		move x# + timspeed# * Cos(angle#) * speed#, y# + timspeed# * Sin(angle#) * speed# 
		collision x#, y# 
		If MilliSecs() > death Then destroy ' Time of a life is limited from occurrence
	End Method

	Method object_collision_act(o:base_obj)' Damage of the met object (except shooter)
		If o <> parent Then
			ccnt:+1
			o.damage(damage)
			destroy
		End If
	End Method
	
	Method boundaries_collision_act()' It is destroyed at collision with borders
		destroy
	End Method
End Type

Type crate_obj Extends base_obj
	Field bonus_type ' Type of a bonus inside

	Function create:crate_obj(b_type)
		o:crate_obj = New crate_obj
		o.image = crate
		o.place_find ONLY_ON_GROUND
		o.bonus_type = b_type
		o.coll_type = CT_SQUARE
		o.health = 10
		If o.speed >= bonustypeq Then o.speed = -1
		o.register INACTIVE
	End Function

	Method destroy()' Explosion of a crate
		If bonus_type >= 0 Then bonus_obj.create x#, y#, bonus_type ' Creation of a bonus on its place

		offset = Rand(0, crate_bits_packq - 1) * 16 ' the random choice of a package of slices
		For yy = 0 To 3 ' Creation of 16 scattering slices
			For xx = 0 To 3
				o:crate_bits_obj = New crate_bits_obj
				o.dx# = Rnd(-1.0, 1.0) + xx - 1.5
				o.dy# = Rnd(-1.0, 1.0) + yy - 1.5
				o.x# = x# + 0.125* (xx * 2 - 3)
				o.y# = y# + 0.125* (yy * 2 - 3)
				o.image = crate_bits
				o.frame = xx + yy * 4 + offset
				o.layer = layer_top_scenery
				o.death = 2000 + MilliSecs()
				o.register
			Next
		Next
	
		super.destroy ' Destruction of crate object - calling procedure from base_obj
	End Method

End Type

Type crate_bits_obj Extends base_obj
	Field dx#, dy# ' Increments for movement

	Method act()' Bits simply flying for 2 seconds
		x# = x# + dx# * timspeed# 
		y# = y# + dy# * timspeed# 
	End Method
End Type

Type bonus_obj Extends base_obj
	Field dangle#, rotation_period!, pulsing_period! ' Variables for pulsing / tilting

	Function create:bonus_obj(x#, y#, b_type)
		o:bonus_obj = New bonus_obj
		o.x = x# 
		o.y = y# 
		o.image = bonus
		o.frame = b_type
		o.health = INDESTRUCTIBLE ' Bonus can not be destroyed
		o.dangle# = Rnd(5, 30)
		o.rotation_period! = Rnd(0.5, 0.1)
		o.pulsing_period! = Rnd(0.5, 0.1)
		o.layer = layer_ground_koloboks
		o.register
	End Function

	Method draw()
		angle# = dangle# * Sin(rotation_period! * MilliSecs())' Angle variations
		size# = 0.8 + 0.2 * Sin(pulsing_period! * MilliSecs())' Size variations
		super.draw
	End Method

	Method get()' We taking bonus
		' Constant bonuses change characteristics to the value depending
		' on quantity of such bonuses on a map (if to collect all bonuses characteristics will change
		' from initial up to the fixed value, they are specified in comments)
		Select frame
			Case BONUS_BULLET_DAMAGE; player.bullet_damage:+10.0 / constant_bonus_crateq ' 2.5 - 12.5
			Case BONUS_BULLET_SPEED; player.bullet_speed:+3.0 / constant_bonus_crateq ' 1.0 - 4.0 tiles / se?
			Case BONUS_BULLET_LIFETIME; player.bullet_lifetime:+3000 / constant_bonus_crateq ' 2 - 5 se?
			Case BONUS_RELOAD_TIME; player.bullet_reload_time:-400 / constant_bonus_crateq ' 0.5 - 0.1 se?
			Case BONUS_MAX_HEALTH; player.max_health:+500.0 / constant_bonus_crateq; player.health = player.max_health ' 300 - 800
			Case BONUS_SPEED; player.speed:+2.0 / constant_bonus_crateq ' 2.0 - 4.0 tiles / se?
			Case BONUS_HEALTH
				If player.health = player.max_health Then Return ' If we have full health - we do not take a bonus
				player.health = limit(player.health + 0.15 * player.max_health, 0, player.max_health) ' + 15% from a maximum
			Case BONUS_TEMPORARY_FIREPOWER; temporary_firepower = MilliSecs() + 10000 ' 10 seconds of firepower
			Case BONUS_TEMPORARY_SPEED; temporary_speed = MilliSecs() + 15000 ' 15 seconds of acceleration
			Case BONUS_TEMPORARY_INVULNERABILITY; temporary_invulnerability = MilliSecs() + 20000 ' 20 seconds of invulnerability
			Case BONUS_BOMB
				For n1 = 2 To 4 ' Generation of splinters of a bomb
					n2 = 0
					While n2 < 360
						bullet_obj.create x#, y#, 1, n2, n1, (5 - n1) * 800, 35, player, player.size# * 0.4
						n2 = n2 + 10* (n1 - 1)
					Wend
				Next
			Case BONUS_ESOURCE
				esource_collected = esource_collected + 1
				If esource_collected = constant_bonus_crateq Then light = MilliSecs() + fading_time ' Yes there will be light if all mana will be collected
		End Select
		death = fading_time + MilliSecs()' Disappearance of a bonus
		coll_type = CT_IMMATERIAL ' The bonus becomes non - material
	End Method
End Type

SeedRnd MilliSecs()' That is for receive new sequence of random numbers each new program launch

SetGraphicsDriver GLMax2DDriver()' Setting the OpenGL graphics driver 
Graphics sxsize, sysize ', color_depth
AutoImageFlags FILTEREDIMAGE | MIPMAPPEDIMAGE | DYNAMICIMAGE
SetBlend ALPHABLEND
reset_transformations

' Loading images with an alpha-channel from an exe-file
Global images:TPixmap = LoadPixmapPNG("incbin::new_images.png")

' Creating images for tiles
tex_water:TImage = tiles_grab(0, 1, False)
tex_sand:TImage = tiles_grab(1, 1, False)
tex_grass:TImage = tiles_grab(2, 1, False)

' Cutting out images
Global kolobok:TImage = tiles_grab(3, 3)
Global bullet:TImage = tiles_grab(6)
Global bonus:TImage = tiles_grab(7, 12)
Global crate:TImage = tiles_grab(19)
Global crate_bits:TImage = CreateImage(tilesize4, tilesize4, crate_bits_packq * 16)
For n = 0 To 3
	For yy = 0 To 3
		For xx = 0 To 3
			new_grab crate_bits, n * tilesize + xx * tilesize4, yy * tilesize4 + tilesize * 5, n * 16 + yy * 4 + xx
		Next
	Next
Next
Global target:TImage = tiles_grab(24), targetx#, targety# 

' Creating water texture in a package of tiles
tile_tex:TImage = CreateImage(tilesize, tilesize, 513)
pixmap:TPixmap = LockImage(tile_tex, 0)
pixmap.paste(LockImage(tex_water)), 0, 0
UnlockImage tile_tex, 0
UnlockImage tex_water
' Adding two libraries - transition from water to sand and from sand to a grass
tile_lib_create tex_water, tex_sand, 4.0 / tilesize, 360.0, tile_tex, 1
tile_lib_create tex_sand, tex_grass, 4.0 / tilesize, 720.0, tile_tex, 257

' Making "slice pie" of layers
Global layer_tiles:tile_layer_obj = tile_layer_obj.add(tile_tex)' all over again - tiley
Global layer_bullets:object_layer_obj = object_layer_obj.add()' Then bullets and splinters of bombs
Global layer_water_koloboks:object_layer_obj = object_layer_obj.add()' After - water koloboks
Global layer_ground_koloboks:object_layer_obj = object_layer_obj.add()' Then - ground koloboks, crates and bonuses
Global layer_top_scenery:object_layer_obj = object_layer_obj.add()' From above - splinters of crates

' Creating layers of tile collisions
Global layer_water:tile_collision_layer_obj = tile_collision_layer_obj.add()' the Layer of"firm"water
Global layer_sand:tile_collision_layer_obj = tile_collision_layer_obj.add()' the Layer of"firm"sand

' Defining what collides with what
layer_water_koloboks.collides_with layer_water_koloboks ' Water koloboks - among themselves
layer_water_koloboks.collides_with layer_sand ' Water koloboks - with a tile collision layer of sand
layer_ground_koloboks.collides_with layer_ground_koloboks ' Ground koloboks - among themselves
layer_ground_koloboks.collides_with layer_water ' Ground koloboks - with a tile collision layer of water
layer_bullets.collides_with layer_water_koloboks ' Bullets - with ground koloboks
layer_bullets.collides_with layer_ground_koloboks ' Bullets - with water koloboks

field_generate ' Generating a field
Global player:player_obj = player_obj.create()' Creating the player
objects_generate ' Creating koloboks and crates

HideMouse

sc# = 64.0
fdx# = player.x + sxsize2 / sc# 
fdy# = player.y + sysize2 / sc# 
Repeat

	tim = MilliSecs()' Storing current moment of time
	
	MoveMouse sxsize2, sysize2 ' Setting the cursor of the mouse in the center of the screen

	' Smooth change of coordinates of the camera (while teleportation the camera is fixed on the player, differently - on an average point between the player and a target)
	camera_change 0.5 * targetx# * (teleport_mode = TM_IDLE), 0.5 * targety# * (teleport_mode = TM_IDLE), 1.1 ^ MouseZ() * 64.0
	
	player.angle = ATan2(targety#, targetx#)' Targetting player's sprite on a target

	timspeed# = speedpersec# * dtim# ' Definition of multiplier for the speed based of last cycle time
	timang# = angpersec# * dtim# ' Same for angular speed

	' Displaying layers
	For l:layer_obj = EachIn layer_order
		l.draw
	Next

	' Actions of active objects
	For o:base_obj = EachIn actingobj
		o.act
	Next

	' Displaying counters
	DrawText"Frames / sec:" + fps + ", objects:" + objcnt + ", collision checks / frame:" + chcnt + ", collisions / frame:" + ccnt, 0, 0
	ccnt = 0
	chcnt = 0

	' Displaying target
	field2scr targetx# + player.x, targety# + player.y, sx#, sy# 
	DrawImage target, sx#, sy# 

	' Clarification of the screen after gathering all energy sources
	If light > MilliSecs() Then
		' Setting transparency
		SetAlpha 1.0 - 1.0 * (light - MilliSecs()) / fading_time
		' Drawing also the white rectangular all-screen-wide
		DrawRect 0, 0, sxsize, sysize
		reset_transformations
	ElseIf light <> 0 Then
		' Congratulating player with a victory
		Notify"Congratulations!!!"
		End
	End If

	Flip False

	' Updating counters of the frames per second
	If fpstim <= MilliSecs() Then
		fpstim = MilliSecs() + 1000
		fps = cnt
		cnt = 0
	Else
		cnt:+1
	End If

	If teleport_mode = TM_IDLE Then
		targetx# :+(MouseX() -sxsize2) / sc# ' Changing coordinates of a target
		targety# :+(MouseY() -sysize2) / sc# 
		' Restrictions on moving target far from player 
		targetx# = limit(targetx#, Max(-sxsize / sc# * 0.75, -player.x), Min(sxsize / sc# * 0.75, fxsize - player.x))
		targety# = limit(targety#, Max(-sysize / sc# * 0.75, -player.y), Min(sysize / sc# * 0.75, fysize - player.y))
	End If

	' Calculation of time spent for a coil of a cycle(in seconds) for calculation of multipliers of speeds
	dtim# = 0.001* (Min(MilliSecs() -tim, minms))
	' Time is limited for unallowing too big multipliers, negatively
	' affecting collisions

Until KeyHit(KEY_ESCAPE)

' Generation of a field
Function field_generate()
	Const tile_water = 0
	Const tile_sand = 256
	Const tile_grass = 512
	Local ff#[fxsize, fysize, 2]' Auxiliary buffer - heightmaps for tiles
	Local pos2bit[] = [0, 6, 1, 4, 5, 2, 7, 3]
	fmin# = 1.0; fmax# = 0 ' Variables of a minimum and a maximum of values of heights
	For n = 0 To fblurq + 3
		loadingbar"Generating field...", n, fblurq + 4 ' the Indicator of completeness of process
		maxd# = 0
		For y = 0 Until fysize ' the Cycle on all tiles
			For x = 0 Until fxsize
				Select n
					Case 0 ' firstly we'll fill heighmap with random values
						ff#[x, y, 1] = Rnd(0, 1)
					Case fblurq + 1 ' After stages of smoothing - tile layers formation stage
						d# = (ff#[x, y, k] -fmin#) / (fmax# -fmin#)' Correcting value of height that the minimum corresponded to value 0.0, and maximum to 1.0
						If d# < sand_threshold# Then ' Up to a threshold of sand
							layer_tiles.frame[x, y] = tile_water ' Displaying clean tile waters
							layer_water.collision[x, y] = True ' Setting a collision with this tile in a water layer
						ElseIf d# < grass_threshold# Then ' From a threshold of sand up to a threshold of a grass
							layer_tiles.frame[x, y] = tile_sand ' Displaying clean sand tile
							layer_sand.collision[x, y] = True ' Setting a collision with this tile in a layer of sand
						Else ' After a threshold of a grass
							layer_tiles.frame[x, y] = tile_grass ' Displaying clean grass tile
							layer_sand.collision[x, y] = True
						End If
					Case fblurq + 2 ' Stage of elimination of the grass adjoining water
						If layer_tiles.frame[x, y] = tile_grass Then ' If tile is a grass, 
							For yy = -1 To 1 ' the Cycle on all next tilem
								For xx = -1 To 1
									x2 = (x + xx + fxsize)Mod fxsize ' Calculation of coordinates of next tile
									y2 = (y + yy + fysize)Mod fysize '(a field zatsikleno)
									If layer_tiles.frame[x2, y2] = tile_water Then ' If one of tiles is water
										layer_tiles.frame[x, y] = tile_sand ' That changes grass tile on sand tile
									End If
								Next
							Next
						End If
					Case fblurq + 3 ' Stage of smoothing tiles (a choice of the frame from library)
						If layer_tiles.frame[x, y] > tile_water Then ' If pure(clean)water this is passed(missed)tile
							bitpos = 0; mask = 0
							For yy = -1 To 1 ' the Cycle on all next tilem
								For xx = -1 To 1
									If xx <> 0 Or yy <> 0 Then
										x2 = (x + xx + fxsize) Mod fxsize
										y2 = (y + yy + fysize) Mod fysize
										If layer_tiles.frame[x, y] > tile_sand Then ' If ?urrent tile - a grass, 
											' If neighbour tile - a grass too then certain bit (of this neighbour) will be on in the current tile frame number
											If layer_tiles.frame[x2, y2] > tile_sand Then setbit mask, pos2bit[bitpos]
										Else ' Else it's sand tile
											' If neighbour tile too then certain bit (of this neighbour) will be on in the current tile frame number
											If layer_tiles.frame[x2, y2] > tile_water Then setbit mask, pos2bit[bitpos]
										End If
										bitpos:+1 ' Increasing bit counter
									End If
								Next
							Next
							layer_tiles.frame[x, y] = 1 + 256* (layer_tiles.frame[x, y] = tile_grass) + mask
						End If
					Default ' Stages of smoothing of a heightmap
						sum# = 0
						For yy = -1 To 1 ' Summarizing values of heights of next tiles and height of current tile * 8
							For xx = -1 To 1
								sum# = sum# + ff#[(x + xx + fxsize) Mod fxsize, (y + yy + fysize) Mod fysize, k] * (1.0 + 7.0* (xx = 0 And yy = 0))
							Next
						Next
						sum# = sum# / 16.0 ' Calculating average value (central tile has the same weight, as all 8 next in the sum)
						If n = fblurq Then setminmax sum#, fmin#, fmax# ' Correcting values of a maximum and a minimum of height
						ff#[x, y, 1 - k] = sum# ' Setting value of height in the buffer
				End Select
			Next
		Next
		k = 1 - k ' Swapping the buffer and a current map
		If n = fblurq + 1 Then ' Fringing tilemap with water after a stage of formation of layers
			For x = 0 Until fxsize
				waterize x, 0
				waterize x, fysize - 1
			Next
			For y = 0 Until fysize
				waterize 0, y
				waterize fxsize - 1, y
			Next
		End If
	Next
End Function

' Fill tile with water
Function waterize(x, y)
	layer_tiles.frame[x, y] = 0 ' Displaying clean water tile
	layer_water.collision[x, y] = True ' Collision for water tile collision layer
	layer_sand.collision[x, y] = False ' No collision for sand tile collision layer
End Function

' Creation of library tiles transition between structures
Function tile_lib_create(bottom_tile:TImage, top_tile:TImage, rowd#, period#, tile_lib:TImage, offset = 0)
	Local dt#[tilesize2] ' Filling array of fluctuations of border
	For dn = 0 Until tilesize2
		dt#[dn] = (Sin(90 + dn * period# / tilesize2) - 1) * tilesize32
	Next

	bottom_pixmap:TPixmap = LockImage(bottom_tile)
	top_pixmap:TPixmap = LockImage(top_tile)

	For n = 0 To 255 ' Eight cells around tile can be same or different (2 variants), 
		' therefore all - 2 ^ 8 = 256 variants
		loadingbar "Generating transition tiles...", n, 256
		lib_pixmap:TPixmap = LockImage(tile_lib, n + offset)
		For n1 = 0 To 1
			For n2 = 0 To 1
				v = biton(n, n1 + n2 * 2)
				vx = biton(n, n1 + 4)
				vy = biton(n, n2 + 6)
				For yy = 0 Until tilesize2
					For xx = 0 Until tilesize2
						If vx Then
							If vy Then
								If v Then
									k1# = 1
								Else
									k1# = rowd# * (Sqr(xx * xx + yy * yy))
								End If
							Else
								k1# = (yy + dt#[xx]) * rowd# 
							End If
						Else
							If vy Then
								k1# = (xx + dt#[yy]) * rowd# 
							Else
								k1# = 2.0 - rowd# * (Sqr((tilesize2 - xx) * (tilesize2 - xx) + (tilesize2 - yy) * (tilesize2 - yy)) + Rand(-1, 1))
							End If
						End If
						If k1# > 1 Then k1# = 1 ' we Limit factor within the limits of an interval[0, 1]
						If k1# < 0 Then k1# = 0
						k2# = 1.0 - k1# ' Coefficient of transparency for pixels of another tile
						If n1 Then x = tilesize - 1 - xx Else x = xx ' Mirroring (if it is necessary)
						If n2 Then y = tilesize - 1 - yy Else y = yy
						fromrgba ReadPixel(top_pixmap, x, y), r1, g1, b1, dummy ' Receiving color components of tiles' pixels 
						fromrgba ReadPixel(bottom_pixmap, x, y), r2, g2, b2, dummy
						' Mixing colors with the set factors, then write pixel with resulting components
						WritePixel lib_pixmap, x, y, torgba(k1# * r1 + k2# * r2, k1# * g1 + k2# * g2, k1# * b1 + k2# * b2, 255)
					Next
				Next	
			Next
		Next
	Next

	UnlockImage bottom_tile
	UnlockImage top_tile
End Function

' Generation of crates and koloboks
Function objects_generate()
	' Koloboks
	For n = 1 To kolobokq
		If(n Mod 100) = 0 Then loadingbar"Generating objects...", n, kolobokq * 3
		Repeat
			o:kolobok_obj = kolobok_obj.create()
			' Distance up to the player should be not less than minimum
			If Sqr((o.x - player.x) * (o.x - player.x) + (o.y - player.y) * (o.y - player.y)) >= min_enemy_distance Then Exit
			o.destroy
		Forever
	Next
	
	' crates with constant bonuses
	For n1 = 1 To constant_bonustypeq ' Cycle on all types of bonuses
		If(n Mod 100) = 0 Then loadingbar"Generating objects...", n1 + constant_bonustypeq, constant_bonustypeq * 3
		For n2 = 1 To constant_bonus_crateq ' Creating certain quantity of crates of each type
			crate_obj.create(n1 - 1)
		Next
	Next

	' crates with temporary bonuses
	For n = 1 To temporary_bonus_crateq
		loadingbar"Generating objects...", n + temporary_bonus_crateq * 2, temporary_bonus_crateq * 3
		If Rand(1, 100) > empty_crates_percent Then
			crate_obj.create Rand(0, temporary_bonustypeq - 1) + bonus_threshold
		Else
			crate_obj.create - 1 ' the Part of crates are empty
		End If
	Next
End Function

' Adding nearly objects to the list
Function nearly_objects:TList(lst:TList, x, y, radius, layer:object_layer_obj)
	For yy = Max(y - radius, 0)To Min(y + radius, fysize - 1)
		For xx = Max(x - radius, 0)To Min(x + radius, fxsize - 1)
			For o:base_obj = EachIn(layer.objects[xx, yy])
				lst.addlast o
			Next
		Next
	Next
	Return lst
End Function

' Function of a grabbing of the image from other image
Function new_grab:TImage(image:TImage, x, y, frame)
	pixmap:TPixmap = LockImage(image, frame)
	w:TPixmap = images.window(x, y, ImageWidth(image), ImageHeight(image))
	pixmap.paste w, 0, 0
	UnlockImage image
	Return image
End Function

' Function of a grabbing tile or series of tiles from the image
Function tiles_grab:TImage(num, frameq = 1, midhn = True)
	image:TImage = CreateImage(tilesize, tilesize, frameq)
	If midhn Then MidHandleImage image ' the flag midhn means, that the image is necessary ottsentrovat
	For n = 0 To frameq - 1
		pos = num + n
		new_grab image, (pos Mod 4) * tilesize, Floor(pos / 4) * tilesize, n ' By default tiley settle down on the image in 4 columns
	Next
	Return image
End Function

Function reset_transformations()
	SetGrayColor 255
	SetRotation 0
	SetAlpha 1
	SetScale 1.0, 1.0
End Function

Function camera_change(x#, y#, scale#)
	' Changing camera scaling and position 
	sc# = sc# + magn_speed# * (scale# -sc#) * dtim# 
	camx# = camx# + cam_speed# * (x# -camx#) * dtim# 
	camy# = camy# + cam_speed# * (y# -camy#) * dtim# 

	sc# = limit(sc#, Max(1.0 * sxsize / fxsize, 1.0 * sysize / fysize), 256.0)' Restriction of increasing scaling
	tilesc# = sc# / tilesize ' Calculation of factor of scaling for tiles
	
	xsize# = sxsize / sc# ' Sizes of a displayed rectangular piece of a field
	ysize# = sysize / sc# 
	
	fdx# = limit(player.x + camx# -xsize# * 0.5, 0, fxsize - xsize#)' Restrictions of displacement of a screen field (within borders)
	fdy# = limit(player.y + camy# -ysize# * 0.5, 0, fysize - ysize#)
End Function

' Setting color - a shade of grey
Function SetGrayColor(col)
	SetColor col, col, col
End Function

' Strip displaying completeness of process
Function loadingbar(txt$, pos, maximum)
	Cls
	SetColor 128, 128, 255
	DrawText txt$, (sxsize - TextWidth(txt$)) / 2, sysize34
	col = 255 * pos / maximum
	SetGrayColor 255
	DrawEmptyRect sxsize4, sysize34 + 20, sxsize2, 30
	SetColor 255 - col, col, 0
	DrawRect sxsize4 + 2, sysize34 + 22, sxsize24 * pos / maximum, 26
	Flip False
	SetGrayColor 255
End Function

' Function for drawing an empty rectangle
Function DrawEmptyRect(x#, y#, xsize#, ysize#)
	xsize# = xsize# -1
	ysize# = ysize# -1
	DrawLine x#, y#, x# + xsize#, y# 
	DrawLine x# + xsize#, y#, x# + xsize#, y# + ysize# 
	DrawLine x# + xsize#, y# + ysize#, x#, y# + ysize# 
	DrawLine x#, y# + ysize#, x#, y# 
End Function

' Function for translation Write / ReadPixel - value to color components' values and an alpha of the channel
Function fromRGBa(from, r Var, g Var, b Var, a Var)
	b = from & $FF
	g = (from Shr 8) & $FF
	r = (from Shr 16) & $FF
	a = (from Shr 24) & $FF
	Return
End Function

' Function for translation values of color components and an alpha - channel to Write / ReadPixel - value
Function toRGBa(r, g, b, a = 255)
	Return b | (g Shl 8) | (r Shl 16) | (a Shl 24)
End Function

' Swapping values of two variables
Function swap(v1 Var, v2 Var)
	z = v2
	v2 = v1
	v1 = z
End Function

' Change of a minimum and a maximum on the basis of a variable
Function setminmax(v#, vmin# Var, vmax# Var)
	If v# < vmin# Then vmin# = v# 
	If v# > vmax# Then vmax# = v# 
End Function

' Translation from screen coordinates to field coordinates in tiles 
Function scr2field(sx#, sy#, tx# Var, ty# Var)
	tx# = sx# / sc# + fdx# 
	ty# = sy# / sc# + fdy# 
End Function

' Translation from field coordinates to screen
Function field2scr(tx#, ty#, sx# Var, sy# Var)
	sx# = (tx# - fdx#) * sc#
	sy# = (ty# - fdy#) * sc#
End Function

' Making variable stay in limits of minumum and maximum values
Function limit# (v#, vmin#, vmax#)
	If v# < vmin# Then v = vmin# ElseIf v# > vmax# Then v# = vmax# 
	Return v#
End Function

' Function returns state of a bit in value at number bitnum
Function biton(v, bitnum)
	If v & (1 Shl bitnum) Then Return True Else Return False
End Function

' Setting on a bit at number bitnum in value of a variable
Function setbit(v Var, bitnum)
	v = v | (1 Shl bitnum)
End Function

' Calculation of the minimal difference of angles
Function calc_dangle# (ang1#, ang2#)
	dang# = ang2# - ang1# 
	Return dang# - Floor(dang# / 360 + 0.5) * 360
End Function
