; ID: 1095
; Author: Stevie G
; Date: 2004-06-23 15:04:56
; Title: A Wee Tank Game  - Now with Mouse option
; Description: Details Below

Graphics3D 1280,960,16,1
SetFont LoadFont("Tahoma",32,1)
SeedRnd MilliSecs()

Global JOYSTICK
Global GunTarget
Global KUP = 200
Global KDOWN = 208
Global KLEFT = 203
Global KRIGHT = 205
Global KSELECT = 28
Global HGW = GraphicsWidth()*.5
Global HGH = GraphicsHeight()*.5

Const Detail# = 15
Const FPS = 30
Const FadeSpeed# = .02
Const Gravity# = 0.05
Const tanks = 4
Const bullets = 100
Const particles = 750
Const drums = 8
Const menus = 2
Const FireTurn# = .25
Const MoveTurn# = .1
Const BulletLife# = 30
Const T_TANK = 1
Const T_ARENA = 2
Const T_DRUM = 3
Const T_BULLET = 4
Const AI_Attack = 0
Const AI_Retreat = 1
Const AI_GoForBonus = 2
Const AI_Flee = 3
Const AI_Straffe = 4
Global SOUNDon = False
Global SOUNDexplode
Global SOUNDclang
Global SOUNDhit
Global SOUNDlaser
Global SOUNDmenu
Global SOUNDbonus
Global camera=CreateCamera()
Global menu_cam = CreateCamera()
Global Pivot = CreatePivot()
Global Light = CreateLight()
Global AIPivot = CreatePivot()
Global FrameTimer = CreateTimer(FPS)
Global BulletMesh
Global ParticleMesh
Global DrumMesh
Global BubbleMesh
Global NextBullet
Global NextParticle
Global ParticleTemplate
Global Fade
Global FadeStatus#
Global NX#
Global NZ#
Global MINEactivate, FIREactivate
Global zoom#=6.75
Global GAMEOVER
Global QUIT
Global PLAYERS
Global KILLS
Global DIFFICULTY

Collisions T_TANK,T_TANK,1,2
Collisions T_TANK,T_ARENA,2,2
Collisions T_TANK,T_DRUM,1,2
Collisions T_BULLET,T_ARENA,2,1	
Collisions T_BULLET,T_TANK,1,1
Collisions T_BULLET,T_DRUM,1,1

Type tank_type
	Field ID
	Field Model
	Field Turret
	Field MaxSpeed#
	Field BulletReload
	Field BulletSpeed#
	Field BulletSpread
	Field BulletHeat#
	Field BulletTimer
	Field Shield#
	Field Damage#
	Field Score
	Field ScoreTexture
	Field Respawn
	Field BubbleShield
	Field Life#
	Field LifeBar
	Field LifeRecharge#
	Field Power#
	Field PowerBar
	Field Cooler#
	Field CoolerBar
	Field MineCounter
	Field Mine
	Field Control
	Field r#,g#,b#
	;ai stuff
	Field MoveCounter
	Field FireCounter
	Field MX#, MZ#
	Field FX# , FZ#
	Field Target
End Type

Type bullet_type
	Field ID
	Field Model
	Field Life
	Field Speed#
	Field Damage#
End Type

Type drum_type
	Field ID
	Field Model
	Field Life#
End Type

Type particle_type
	Field Vertex
	Field x#,y#,z#
	Field vx#,vy#,vz#
	Field r,g,b
	Field life#
	Field fade#
	Field size#
	Field weight#
End Type

Type bonus_type
	Field ID
	Field Model
	Field timer
End Type

Type arena_type
	Field Model
	Field Hud
	Field Radar
End Type	

Type menu_type
	Field Model
	Field ID
	Field texture
	Field options
	Field title$
	Field initial
	Field current
	Field sub_options[5]
	Field sub_string$[5]
	Field sub_current[5]
End Type

Dim tank.tank_type(tanks-1)
Dim bullet.bullet_type( bullets -1 )
Dim drum.drum_type ( drums - 1)
Dim particle.particle_type ( particles - 1)
Dim menu.menu_type( menus -1 )
Global BONUS.bonus_type = New bonus_type
Global ARENA.arena_type = New arena_type

GAMEinit()

Repeat
	GAMEstart()
	Repeat
		WaitTimer(FrameTimer)
		BULLETSupdate()
		PARTICLESupdate()
		BONUSupdate()
		TANKSupdate()
		UpdateWorld()
		RenderWorld()
		Flip
	Until QUIT Or GAMEOVER  
	If GAMEOVER GAMEend()
Until (1=2)
		
End

;==========================================================
;==========================================================
;==========================================================

Function TANKSupdate()

	;radar
	TurnEntity ARENA\radar,0,5,0

	For t.tank_type = Each tank_type
	
		t\BulletTimer = t\BulletTimer - ( t\BulletTimer > 0 )
								
		If t\Life > 0 
	
			FIREactivate = True
			
			;control
			If t\Control < 2
				If JOYSTICK
					;analogue
					t\MX = JoyX(t\ID)
					t\MZ = -JoyY(t\ID)
					t\FX = JoyRoll(t\ID) / 180.0
					t\FZ = -JoyZ(t\ID)
					MINEactivate = JoyDown( 12,t\ID )
				Else
					;mouse / keyboard
					t\MX = KeyDown( KRIGHT )-KeyDown( KLEFT )
					t\MZ = KeyDown( KUP )-KeyDown( KDOWN )
					FIREactivate = MouseDown(1)
					MINEactivate = MouseDown(2)
				EndIf
			Else
				AI ( t )
			EndIf
				
			;moving
			speed#  = NORMALxz( t\MX , t\MZ ) 
			If speed > 0.3
				PositionEntity Pivot,EntityX(t\Model) + NX * 5 , 0 , EntityZ(t\Model) + NZ * 5
				RotateEntity t\Model,0,EntityYaw(t\Model)+DeltaYaw(t\Model,Pivot) * MoveTurn ,0
				speed = speed * t\MaxSpeed
				TranslateEntity t\Model , NX * speed , (3-EntityY(t\Model)) , NZ * speed 
				PARTICLEnew( t\Model, 0,-1.5,-3, -.1,.1, -.1,.1, -.2,-.1 , 64,80,64,80,64,80, .25,.5, .25 , .1, 1)
			EndIf
			
			;firing - joystick / ai control 
			speed# = NORMALxz( t\FX , t\FZ )
			If speed > 0.5 
				PositionEntity Pivot,EntityX(t\Model) + NX * 5 , 0 , EntityZ(t\Model) + NZ * 5
				yaw# = DeltaYaw(t\Turret,Pivot)
				TurnEntity t\Turret,0, yaw *FireTurn ,0
				If  t\BulletTimer = 0  And  t\Cooler > t\BulletHeat And ( Abs(yaw)< 15 )  
					If t\respawn = 0 And FIREactivate BULLETnew(  t )
				EndIf 
			EndIf
			PositionEntity t\Turret,EntityX(t\Model),3,EntityZ(t\Model),1	
			
			;firing - mouse
			If t\Control < 2 And ( Not JOYSTICK )
				PositionEntity GunTarget,( MouseX() - HGW ) *.145 , 3 , ( HGH - MouseY() ) *.17
				PointEntity t\Turret , GunTarget
				RotateEntity t\Turret,0,EntityYaw( t\Turret ),0
				If t\BulletTimer = 0 And Abs(yaw)< 15  And t\Cooler > t\BulletHeat
					If t\respawn = 0 And FIREactivate BULLETnew(  t )
				EndIf
			End If
			
			;recharge gun & life		
			t\Life = LIMIT( t\Life + t\LifeRecharge, 0, t\Power )
			t\Cooler = LIMIT( t\Cooler + .0075, 0, 1)
	
			;lay / trigger mine
			If t\MineCounter = 1 And MINEactivate MINEnew( t, 0 )
			If t\MineCounter > 1 t\MineCounter = t\mineCounter + 1
			If t\MineCounter > 30 And MINEactivate MINEnew( t,1 )
															
		EndIf
		
		;bonus pickup
		If BONUS\timer > 0 And EntityDistance( BONUS\Model, t\Model ) < 3
			BONUS\timer = 0
			TANKbonus (t , BONUS\ID )
			PARTICLEnew ( BONUS\Model, 0,0,0, 0,0,.5,.5,0,0, 255,255,255,255,0,0, .25,.5, 1 , .01 , 8, 10 )
			SOUNDplay( SOUNDbonus )
		EndIf
			
		;respawn	
		If t\Respawn > 0 TANKrespawn( t )
		
		;update hud
		ScaleEntity t\LifeBar,t\Life,1,1
		ScaleEntity t\PowerBar,t\Power,1,1
		ScaleEntity t\CoolerBar,t\Cooler,1,1

	Next
	
	;quit
	QUIT = KeyDown(1)
	
	;pause
	If KeyDown(25) MENUshow ( MENU(1) )
		
End Function
	
;==========================================================
;==========================================================
;==========================================================

Function BULLETSupdate()

	For b.bullet_type = Each bullet_type
		
		MoveEntity b\Model,0,0,b\speed
		b\Life = b\Life - (b\Life > 0)
		
		;do collisions
		If CountCollisions(b\Model) > 0
			For ID = 1 To 3
				entity = EntityCollided (b\Model,ID)
				If entity <> 0
					Select ID
						Case T_TANK
							t.tank_type = Object.tank_type( EntityName( entity ) )
							damage# = b\Damage * (1.0 - t\Shield)
							t\Power = LIMIT( t\Power - damage*.25,0.0,1.0)
							t\Life = LIMIT( t\Life - damage,0.0,1.0)
							If t\Life = 0 And t\Respawn = 0
								;destroy tank
								EXPLOSION ( t\Turret, 20 , b\ID, t\r,t\g,t\b, True )
							Else
								;hit tank
								h.tank_type = tank( b\ID )
								HIT ( b\Model, h\r,h\g,h\b, False , SOUNDhit,  True )
							EndIf
						Case T_ARENA
							;hit arena
							HIT ( b\Model, 142,100,142, True, SOUNDclang, False ) 
						Case T_DRUM
							d.drum_type = Object.drum_type( EntityName( entity ) )
							d\Life = LIMIT( d\Life - b\Damage,0.0,2.0)
							If d\Life = 0 
								;destroy drum
								EXPLOSION ( d\Model, 20 , b\ID, 168,116,100, False )
							Else
								;hit drum
								HIT ( b\Model, 168,116,100, True, SOUNDclang, False )
							EndIf	
					End Select
				EndIf
			Next
			b\Life =0 
		EndIf
						
		If b\Life = 0 HideEntity b\Model:ResetEntity b\Model
		
	Next
	
End Function

;==========================================================
;==========================================================
;==========================================================

Function PARTICLESupdate()

	s = GetSurface( ParticleMesh , 1 )
	t = GetSurface( ParticleTemplate , 1 )
	cv = CountVertices(t)-1
		
	For p.particle_type = Each particle_type
	
		p\Life = LIMIT( p\Life - p\fade ,0 , 1.0 ) 
		
		p\vy = p\vy - Gravity * p\weight
		p\x = p\x + p\vx
		p\y = p\y + p\vy
		p\z = p\z + p\vz
		
		If p\y < p\size 
			p\y = p\size
			p\vx = p\vx * .75
			p\vy = -p\vy*.5
			p\vz = p\vz * .75
		EndIf
		
		For v=0 To cv
			px# = p\x + VertexX(t,v) * p\size
			py# = p\y + VertexY(t,v) * p\size
			pz# = p\z + VertexZ(t,v) * p\size
			VertexCoords s , p\Vertex + v , px, py, pz 
			VertexColor s, p\Vertex + v , p\r , p\g , p\b , .75 * p\Life
		Next

	Next

End Function

;==========================================================
;==========================================================
;==========================================================

Function BONUSupdate()
	
	If BONUS\timer = 0 
		HideEntity BONUS\Model
		If Rand(60)=1
			BONUS\ID = Rand(1,8)
			BONUS\timer = 150
			bx = ( Rand(0,1)*2-1 ) * ( 5 + Rand(0,3) * 10 )
			bz = ( Rand(0,1)*2-1 ) * ( 5 + Rand(0,3) * 10 )
			PositionEntity BONUS\Model,bx,3,bz 
			ShowEntity BONUS\Model
		EndIf
	EndIf
	
	If BONUS\timer > 0
		BONUS\timer = BONUS\timer - 1
		alpha# = 1.0
		If BONUS\timer <= 30 alpha# = Float( BONUS\timer)*.033
		If BONUS\timer > 120 alpha# = Float(150.0 - BONUS\timer ) * .033
		EntityAlpha BONUS\Model,alpha
		TurnEntity BONUS\Model,0,10,0
	EndIf

End Function

;==========================================================
;==========================================================
;==========================================================

Function AI ( t.tank_type )

	t\MoveCounter = t\MoveCounter - (t\MoveCounter > 0 )
	t\FireCounter = t\FireCounter - (t\FireCounter > 0 )
	FireRange# =  ( BulletLife * t\BulletSpeed )
	MINEactivate = ( t\MineCounter = 1 And Rand(60)=1 ) 
	
	If t\Target > -1 
		TargetDistance# = EntityDistance( t\Model, tank(t\target)\Model )
		If tank(t\target)\respawn > 0 t\Target = -1
	Else
		TargetDistance# = 1000.0
		t\FireCounter = 0
	EndIf
	
	;find a new target
	If t\FireCounter = 0	
		FleeX# = 0
		FleeZ# = 0	
		For c.tank_type = Each tank_type
			If t<>c 
				FleeX = FleeX + EntityX(c\Model) * .33
				FleeZ = FleeZ + EntityZ(c\Model) * .33
				CheckDistance# = EntityDistance( t\Model, c\Model ) 
				If c\Respawn = 0 And CheckDistance < TargetDistance 
					t\Target = c\ID
					TargetDistance = CheckDistance 
				EndIf
				
				;Detonate the mine?
				If c\Respawn = 0 And t\MineCounter > 1 
					If EntityDistance( t\mine, c\Model ) < Rand(5, 20) And EntityDistance( t\mine, t\model) > 20
						MINEactivate = True
					EndIf
				EndIf
				
			EndIf
		Next
		t\FireCounter = 20
	EndIf
			
	;shoot at target if within range
	If t\Target > -1 And (TargetDistance < FireRange+10 ) And ( t\Cooler > ( Rand(2,8) *  t\BulletHeat ) )
		c.tank_type = tank(t\Target)
		time# = TargetDistance / t\BulletSpeed
		ox# = c\MaxSpeed * time
		oz# = c\MaxSpeed * time
		PositionEntity Pivot,EntityX(c\Model) + Rnd(-ox,ox) , 0, EntityZ(c\Model) + Rnd(-oz,oz)
		Distance# = NORMALxz ( EntityX( Pivot) - EntityX(t\Model) , EntityZ(Pivot) - EntityZ( t\Model) )
		t\FX = NX
		t\FZ = NZ
	Else
		t\FX = 0
		t\FZ = 0
	EndIf			
	
	;move	
	If t\MoveCounter = 0
	
		MoveRange# = t\MaxSpeed * 10.0 
		BonusRange# = MoveRange * 10 * (BONUS\timer > 0 )
		BonusDistance# = EntityDistance(t\Model,BONUS\Model) 	
									
		;decide on action
		If t\Target > -1 
			Action = AI_Straffe
			PositionEntity AIPivot, EntityX( tank(t\Target)\Model ), 0, EntityZ( tank(t\Target)\Model )
			If TargetDistance >  ( FireRange + 10.0) Action = AI_Attack
			If TargetDistance < ( FireRange - 10.0 ) Action = AI_Retreat
		Else
			Action = AI_Flee
			PositionEntity AIPivot, FleeX, 0, FleeZ
		EndIf
		If BonusDistance <  BonusRange
			Action = AI_GoForBonus
			PositionEntity AIPivot, EntityX( BONUS\Model ), 0, EntityZ( BONUS\Model )
		EndIf
		Distance# = NORMALxz( EntityX( AIPivot ) - EntityX( t\Model), EntityZ( AIPivot ) - EntityZ( t\Model ) ) 
		If Action = AI_Attack Or Action = AI_GoForBonus Distance# = 1000.00
		If Action = AI_Flee Or Action = AI_Retreat Distance# =0.0
		
		;check possible moves			
		choice = -1
		angle = Rand( 0,7 )
		For l = 0 To 7
			angle = (angle + 1 ) Mod 8
			vx# = NX * Cos( angle*45) - NZ * Sin( angle*45)
			vz# = NZ * Cos( angle*45) + NX * Sin(angle*45)
			PositionEntity pivot, EntityX(t\Model)	+ vx * MoveRange , 0, EntityZ( t\Model) + vz * MoveRange
			unused = LinePick ( EntityX(t\Model) , 3 , EntityZ(t\Model) , vx * MoveRange, 0 , vz * MoveRange , 2 )
						
			;If Nothing blocking route
			If PickedEntity() = 0
			
				;Is there a live mine near New point?
				NoGo = False		
				For c.tank_type = Each tank_type
					If c <> t And c\MineCounter > 1 
						If EntityDistance( pivot, c\Mine ) < Rand(5,10) NoGo = True
					EndIf
				Next

				If NoGo = False
					CheckDistance# = EntityDistance( pivot, AIPivot )
					Select Action
					Case AI_Attack, AI_GoForBonus
						If CheckDistance < Distance
							Distance = CheckDistance
							choice = angle
						EndIf
					Case AI_Flee, AI_Retreat
						If CheckDistance > Distance
							Distance = CheckDistance
							choice = angle
						EndIf
					Case AI_straffe
						If angle = 2 Or angle = 6 choice = angle 
					End Select
				EndIf
						
			EndIf
	
		Next

		t\MoveCounter = 10

		If choice > -1
			;found a move
			t\MX = NX * Cos( choice * 45) - NZ * Sin( choice*45)
			t\MZ = NZ * Cos( choice * 45 ) + NX * Sin( choice*45)
		Else
			;no move
			choice = Rand(3,5)
			t\MX = NX * Cos( choice * 45) - NZ * Sin( choice*45)
			t\MZ = NZ * Cos( choice * 45 ) + NX * Sin( choice*45)
		EndIf
			
	EndIf
		
		
End Function

;==========================================================
;==========================================================
;==========================================================

Function FADE ( target# )

	direction# = Sgn(target - FadeStatus )

	Repeat
		FadeStatus = LIMIT( FadeStatus + direction * FadeSpeed , 0.0,1.0)
		EntityAlpha fade, (1.0 - FadeStatus)
		RenderWorld()
		Flip
	Until ( FadeStatus >= target And direction = 1) Or (FadeStatus <= target And direction= -1) Or (direction=0)
	
End Function

;==========================================================
;==========================================================
;==========================================================

Function NORMALxz#( jx# , jz# )

	speed# = Sqr ( jx * jx + jz * jz )
	NX = jx / speed
	NZ = jz / speed

	Return speed

End Function

;==========================================================
;==========================================================
;==========================================================

Function HIT ( entity, r, g, b, Mark, Sound, IsTANK )

	SOUNDplay( Sound )
	If IsTANK 
		PARTICLEnew ( entity, 0,0,0, 0,0,.5,.5,0,0, r,r,g,g,b,b, .4,.6, 1 , .01 , 2, 33 )
		PARTICLEnew( entity, 0,0,0, -.25,.25, -.5,.5, -.5,0 , 255,255,128,255,0,0, .25,.5, .5, .02, 3)
	Else
		PARTICLEnew( entity, 0,0,0, -.25,.25, -.5,.5, -.5,0 , 128,192,128,192,128,192, .2,.3, .5, .01, 3)	
	EndIf
		
	If Mark PARTICLEnew( entity, 0,0,-1, 0,0, 0,0, 0,0 ,16,48,16,48,16,48 ,.2,.3, 0, .05, 1)

End Function

;==========================================================
;==========================================================
;==========================================================

Function EXPLOSION( entity, range#, ID ,r,g,b, IsTank )

	;do explosion
	HideEntity entity
	SOUNDplay( SOUNDexplode )
	PARTICLEnew( entity, 0,0,0, -.25,.25, .5,1.5, -.25,.25 , r,r,g,g,b,b, .5,.75,1, .005,10)
	PARTICLEnew( entity, 0,0,0, -.25,.25, .5,1.5, -.25,.25 , 255,255,128,255,0,0, .25,.5, 1, .01,10)
	PARTICLEnew ( entity, 0,0,0, 0,0,.25,.5,0,0, 255,255,255,255,255,255, .75,1, .5 , 0 , 16, range )
	
	;if tank destroyed start respawn & updatescore for the tank that was responsible
	
	If IsTank
		t.tank_type = Object.tank_type( EntityName( entity ) )
		t\Shield = 1
		t\Respawn = 1
		If t\MineCounter > 1 
			EXPLOSION ( t\Mine, 20, t\ID, t\r,t\g,t\b, False )
		Else
			HideEntity t\Mine
		EndIf
			
		TANKbonus ( t )
		c.tank_type = tank( ID )
		If c<>t 
			PARTICLEnew( entity, 0,3,0, 0,0, .25,.25,0,0 , c\r,c\r,c\g,c\g,c\b,c\b, 1.5,1.5, 0, .01,1)
			SCOREupdate( c , 1 )
		Else
			SCOREupdate( c, -1 )
		EndIf
	EndIf
	
	;check tanks
	For t.tank_type = Each tank_type
		If t\Life > 0 
			distance# = EntityDistance( t\Turret, entity )
			If distance < range
				damage# = ( ( range - distance ) / range ) * (1.0-t\Shield)
				t\Power = LIMIT( t\Power - damage*.25,0.0,1.0)
				t\Life = Limit (t\Life - damage, 0 , 1 )
				If t\Life = 0 EXPLOSION( t\Turret, 20, ID, t\r,t\g,t\b, True )
			EndIf
		EndIf
	Next
	
	;check drums
	For d.drum_type = Each drum_type
		If d\Life > 0
			distance# = EntityDistance( d\Model, entity )
			If distance  < range
				damage# = ( ( range - distance ) / range ) 
				d\Life = Limit (d\Life - damage, 0 , 2 )
				If d\Life = 0 EXPLOSION ( d\Model, 20 , ID, 168,116,100, False )
			EndIf
		EndIf
	Next
	
End Function

;==========================================================
;==========================================================
;==========================================================

Function TANKbonus ( t.tank_type , bonus=0 )

	Select bonus
		Case 0
			t\BulletReload = 9
			t\BulletSpeed = 1.25 
			t\MaxSpeed = .4 
			t\Shield = 0.0
			EntityAlpha t\BubbleShield, 0
			t\Damage = .1 
			t\LifeRecharge = .002
			t\BulletSpread=0
			t\MineCounter = 0
		Case 1
			t\BulletReload =LIMIT(  t\BulletReload - 3, 3,9 ) 
		Case 2
			t\BulletSpeed = LIMIT( t\BulletSpeed + .25,  1.25, 1.75 )
		Case 3
			t\MaxSpeed = Limit ( t\MaxSpeed + .1, .4, .6)
		Case 4
			t\Shield = Limit ( t\Shield + .25, 0, .75 )
			EntityAlpha t\BubbleShield, t\Shield*.5
		Case 5
			t\Damage = Limit ( t\Damage + .05, .1,.2 )
		Case 6
			t\LifeRecharge = LIMIT( t\LifeRecharge + .002, .002, .01 )
		Case 7
			t\BulletSpread = LIMIT( t\BulletSpread+1,0,2 )
		Case 8
			If t\MineCounter = 0 
				t\MineCounter = 1
				ShowEntity t\Mine
				EntityParent t\Mine, t\Model
				PositionEntity t\Mine, 0,0,-2.5
			EndIf
	End Select
	
	t\BulletHeat# = .025 * ( (  t\BulletSpread + 1.0 ) + ( t\Damage * 10.0 ) + ( t\BulletSpeed / 1.25 ) )
	
End Function

;==========================================================
;==========================================================
;==========================================================

Function TANKrespawn( t.tank_type )

	t\Respawn = t\Respawn + 1
	
	;stage 0 - smoking
	If t\Respawn < 91
		PARTICLEnew( t\Model, 0,1,0, -.1,.1, .2,.3, -.1,.1 , 64,80,64,80,64,80, .25,.5, 0,.02,1)
	EndIf
	;stage 1 - show BubbleShield									
	If t\Respawn > 90 And t\Respawn <= 120 
		scale# = Float(t\Respawn - 91) / 29.0
		EntityAlpha t\BubbleShield, scale*.5
	EndIf
	;stage 2 - scale Turret, fill energy
	If t\Respawn > 120 And t\Respawn <= 150
		ShowEntity t\Turret
		scale# = Float(t\Respawn - 121) / 29.0
		t\Life = scale
		t\Power = Limit (t\Power + .04, 0.0,1.0) 	
	EndIf
	;stage 3 - hide BubbleShield
	If t\Respawn > 150 And t\Respawn <= 180
		scale# = Float(t\Respawn - 151) / 29.0
		EntityAlpha t\BubbleShield, .5 - scale*.5 
	EndIf
	;stage 4 - back in play
	If t\Respawn = 180 
		t\Respawn = 0
		If t\Shield = 1 t\Shield = 0
	EndIf
	
End Function

;==========================================================
;==========================================================
;==========================================================

Function MINEnew( t.tank_type , action )

	Select action
	Case 0	;drop mine
		EntityParent t\Mine,0 
		t\MineCounter = 2
	Case 1	;detonate mine 
		EXPLOSION ( t\Mine, 20 , t\ID , t\r,t\g,t\b , False )
		t\MineCounter = 0
	End Select
	
End Function

;==========================================================
;==========================================================
;==========================================================

Function BULLETnew( t.tank_type )

	angle# = t\BulletSpread * 5
	SOUNDplay( SOUNDlaser ) 
	t\Cooler = Limit ( t\Cooler - t\BulletHeat , 0, 1.0)
	PARTICLEnew( t\Turret, 0,0,3 , -.25,.25, -.25,.25, 0.05,0.1,  255,255,255,255,255,255, .25,.25, 0, .04,t\BulletSpread+1) 
	
	For fired = -Sgn( t\BulletSpread ) To Sgn( t\BulletSpread ) 
		If t\BulletSpread <>1 Or fired <> 0 
			b.bullet_type = bullet( NextBullet)
			EntityParent b\Model,t\Turret
			EntityColor b\Model,t\r,t\g,t\b
			PositionEntity b\Model,0,0,3
			RotateEntity b\Model,0,fired*angle,0
			scale# = t\Damage*10.0
			ScaleEntity b\Model,1,1,scale
			EntityParent b\Model,0
			ShowEntity b\Model
			b\Life = BulletLife
			b\ID = t\ID
			b\speed = t\BulletSpeed
			b\Damage = t\Damage
			t\BulletTimer = t\BulletReload
			NextBullet = ( NextBullet + 1 ) Mod bullets
		EndIf
	Next
		
End Function

;==========================================================
;==========================================================
;==========================================================

Function PARTICLEnew( mesh, x#,y#,z#, vx1#,vx2#, vy1#,vy2#, vz1#,vz2#, r1,r2, g1,g2, b1,b2 , s1#,s2# , w#, f#, number, range#=0)

	random# = Rand( -180,180 )

	For l = 1 To number
		p.particle_type = particle( NextParticle)
		If range = 0 
			TFormVector x,y,z, mesh, 0 
		Else
			angle# = 360 * l / number + random
			TFormVector Cos(angle),y,Sin(angle), mesh,0
			f# = 1.0 / ( range * 3.0)
		EndIf	
		p\x = EntityX(mesh,1)+TFormedX()
		p\y = EntityY(mesh,1)+TFormedY()
		p\z = EntityZ(mesh,1)+TFormedZ()
		If range = 0 
			TFormVector Rnd(vx1,vx2),Rnd(vy1,vy2),Rnd(vz1,vz2), mesh, 0
		Else
			TFormVector Cos(angle)*.5,Rnd(vy1,vy2),Sin(angle)*.5,mesh,0
		EndIf
		p\vx = TFormedX() 
		p\vy = TFormedY() 
		p\vz = TFormedZ() 
		p\r = Rand(r1,r2) 
		p\g = Rand(g1,g2) 
		p\b = Rand(b1,b2)
		p\size = Rnd(s1,s2)
		p\weight = w
		p\fade = f
		p\Life = 1.0
		NextParticle = ( NextParticle + 1 ) Mod particles
	Next

End Function 

;==========================================================
;==========================================================
;==========================================================

Function SCOREupdate( t.tank_type , add#=0 )

	t\Score = t\Score + add
	If t\Score = KILLS And ( Not GAMEOVER) GAMEOVER = t\ID+1
	SetBuffer TextureBuffer (t\ScoreTexture)
	Color 0,0,0:Rect 0,0,32,32
	Color t\r,t\g,t\b:Text 16,16,t\Score,1,1
	SetBuffer BackBuffer()
	
End Function 

;==========================================================
;==========================================================
;==========================================================

Function GAMEend()

	Winner = tank(GAMEOVER-1)\Model
	CAMERApoint( EntityX(Winner),EntityY(Winner),EntityZ(Winner), 36.75 , 150 , .1 )
	CAMERApoint( 0,0,0, 6.75 , 60 , .1 )
	
End Function

;==========================================================
;==========================================================
;==========================================================

Function GAMEstart()

	MENUshow( MENU(0) )
	FADE( 0 )
	GAMEreset ()
	FADE( 1 )
	
End Function

;==========================================================
;==========================================================
;==========================================================

Function CAMERApoint( x#,y#,z#, zoomtarget#, iterations, smooth#, show =True)

	PositionEntity Pivot,x,y,z
		
	For l = 1 To iterations
		TurnEntity camera, DeltaPitch(camera, pivot)*smooth, DeltaYaw( camera, pivot)*smooth, 0
		zoom = zoom + Sgn( zoomtarget - zoom ) 
		CameraZoom camera,  zoom 
		If show
			WaitTimer (FrameTimer)
			PARTICLESupdate()
			RenderWorld()
			Flip
		EndIf
	Next

End Function

;==========================================================
;==========================================================
;==========================================================

Function GAMEreset( )

	;tanks
	For t.tank_type = Each tank_type
		t\Score = 0
		t\BulletTimer=0
		t\Life = 1.0
		t\Power = 1.0
		t\Cooler = 1.0
		t\Respawn = 0
		t\Control = ( t\ID < PLAYERS ) + 2 * ( t\ID >=PLAYERS )
		x# = ( (t\ID=0 Or t\ID=2) - (t\ID=1 Or t\ID=3) )*30
		z# = ( (t\ID=1 Or t\ID=2) - (t\ID=0 Or t\ID=3) )*30
		PositionEntity t\Model,x,3,z
		PointEntity t\Model,ARENA\Model:TurnEntity t\Model,0,180,0
		RotateEntity t\Turret,0,EntityYaw(t\Model),0
		ResetEntity t\Model
		PositionEntity t\Turret,x,3,z
		ShowEntity t\Turret:ScaleEntity t\Turret,1,1,1
		ShowEntity t\Model
		SCOREupdate (t)
		TANKbonus (t)
		ScaleEntity t\LifeBar,t\Life,1,1
		ScaleEntity t\PowerBar,t\Power,1,1
		HideEntity t\Mine
		t\FX = 0:t\FZ = 0
		t\MX = 0:t\MZ = 0
		t\MoveCounter = t\ID * 2 
		t\FireCounter = t\ID * 2
		t\Target = -1
	Next
	
	;bullets
	For w.bullet_type = Each bullet_type
		HideEntity w\Model
		ResetEntity w\Model
	Next
		
	;drums
	For b.drum_type = Each drum_type
		x# = Cos( -22.5 + b\ID *45.0) * 27
		z# = Sin(-22.5 + b\ID * 45.0) * 27
		b\Life = 2.0
		PositionEntity b\Model,x,2,z
		ResetEntity b\Model
		ShowEntity b\Model
	Next
		
	;particles
	For p.particle_type = Each particle_type
		p\Life = 0
	Next
	PARTICLESupdate()
		
	;bonus
	HideEntity BONUS\Model
	BONUS\timer = 0
		
	;misc	
	zoom# = 6.75
	GAMEOVER = False
	
	;Gun Target Reset
	If ( Not JOYSTICK ) And PLAYERS > 0
		ShowEntity GunTarget
		MoveMouse HGW, HGH
	Else
		HideEntity GunTarget
	EndIf
	
	
End Function

;==========================================================
;==========================================================
;==========================================================

Function LIMIT#( q# , low# , hi# )

	If q < low q = low
	If q > hi q = hi
	Return q
	
End Function

;==========================================================
;==========================================================
;==========================================================

Function SOUNDplay ( sound )
	If SOUNDon 
		new_channel = PlaySound( sound )
	EndIf
End Function

;==========================================================
;==========================================================
;==========================================================

Function MENUshow ( m.menu_type )
	
	;reset
	m\current = m\initial
	MENUupdate( m )
	
	;fade in	
	ShowEntity m\model
	For l=  0 To 30
		RotateEntity m\model, 0,0,l*12
		PositionEntity m\model,0,0,2+(30-l)
		RenderWorld()
		Flip
	Next		
	
	Repeat
	
		;user input
		jx = KeyDown( KRIGHT ) - KeyDown( KLEFT )
		jy = KeyDown( KDOWN) - KeyDown( KUP )
		jb = KeyDown( KSELECT)
		If KeyDown(1) End
				
		;change current option
		If jy <> 0 
			old = m\current
			m\current = Limit (m\current + jy, 0, m\options-1 )
			jx=0:jy = (old <> m\current)
		EndIf
		
		;change current sub option
		If jx <> 0  
			old = m\sub_current[m\current]
			m\sub_current[m\current] = Limit ( m\sub_current[m\current] + jx , 0, m\sub_options[m\current]-1 )
			jx = ( old <> m\sub_current[m\current] )
		EndIf
		
		;update menu
		If (jx+jy) <> 0 
			SOUNDplay( SOUNDmenu)
			If m\sub_current[2] = 0 m\sub_current[0] = LIMIT ( m\sub_current[0], 0, 1 )
			MENUupdate ( m )
		EndIf
		
		RenderWorld()
		Flip

	Until m\sub_options[m\current] = 1 And jb
	
	SOUNDplay ( SOUNDmenu )

	;fade out
	For l=  0 To 30
		RotateEntity m\model, 0,0,l*12
		PositionEntity m\model,0,0,2+l
		RenderWorld()
		Flip
	Next	
	HideEntity m\model

	;get details
	Select m\ID
	Case 0
		PLAYERS = m\sub_current[0]
		KILLS = ( m\sub_current[1] + 1 ) * 10 
		JOYSTICK = ( m\sub_current[2] = 1 )
	Case 1
		QUIT = ( m\current = 1)
	End Select

End Function

;==========================================================
;==========================================================
;==========================================================

Function MENUupdate(m.menu_type, update_title=False )

	SetBuffer TextureBuffer(m\texture)
	
	If update_title
		Color 64,64,128:Rect 0,0,256,128,1 
		MyText( 128,12,m\title, 1,1, 255,255,0 )
	Else
		Color 64,64,128:Rect 0,28,256,88,1
	EndIf
		
	y = 22 + ( m\options = 2 ) * 48
		
	For option = 0 To m\options - 1
	
		r = 255
		g = 155+100*(m\current = option)
		b = 155+100*(m\current = option)
		x = 8 + 120 * ( m\sub_options[option]=1 )
		
		MyText ( x, y + option * 24, Left$( m\sub_string[option], 12), m\sub_options[option]=1,0, r,g,b )
		
		If m\sub_options[option] > 0
			MyText ( 136, y + option * 24 , Mid$(m\sub_string[option], ( m\sub_current[option] + 1)*12,12 ), 0,0, r,g,b )
		EndIf
	Next
	
	SetBuffer BackBuffer()
		
	Delay 100
	
End Function

;==========================================================
;==========================================================
;==========================================================

Function MyText ( x, y, t$, cx, cy, r, g, b )

	Color 32,23,64 
	For l = 0 To 4
		px = ( ( l = 0) - (l = 2) ) * 1
		py = ( ( l = 1) - (l = 3) ) * 1
		If l= 4 Color r,g,b
		Text x+ px , y+ py , t$ , cx , cy
	Next

End Function

;==========================================================
;==========================================================
;==========================================================
;==========================================================
; THESE FUNCTIONS ARE ONLY USED ONCE
;==========================================================
;==========================================================
;==========================================================
;==========================================================

Function MENUinit()

	Restore MENUdata
	
	For l = 0 To menus-1
		m.menu_type = New menu_type
		menu( l ) = m
		m\ID = l
		w# = .5
		h# = .25
		m\model = QUADnew(menu_cam,-90, w,h,1 )
		frame = CYLINDERnew( 3, 128,128,128, 0,0,90, .01,w,.01, 0,h+.01,0 )
		CYLINDERnew( 3, 128,128,128, 0,0,90, .01,w,.01, 0,-(h+.01),0, frame )
		CYLINDERnew( 3, 128,128,128, 0,0,0, .01,h,.01, -(w+.01),0,0, frame )
		CYLINDERnew( 3, 128,128,128, 0,0,0, .01,h,.01, w+.01,0,0, frame )
		CYLINDERnew( 3, 142,100,142, 0,0,-45, .04,.02,.04, w,h,0, frame )
		CYLINDERnew( 3, 142,100,142, 0,0,-45, .04,.02,.04, -w,-h,0, frame )
		CYLINDERnew( 3, 142,100,142, 0,0,45, .04,.02,.04, w,-h,0, frame )
		CYLINDERnew( 3, 142,100,142, 0,0,45, .04,.02,.04, -w,h,0, frame )
		EntityParent frame, m\model
		PositionEntity frame, 0,0,0 
		PositionEntity m\model,0,0,2
		HideEntity m\model
		m\texture = CreateTexture (256,128, 16+32+1)
		EntityTexture m\model, m\texture
		Read m\title
		Read m\options
		Read m\initial
		For option = 0 To m\options-1
			Read m\sub_options[option]
			Read m\sub_string[option]
			m\sub_current[option] = ( m\sub_options[option] > 1 )
		Next
		MENUupdate( m, True )
	Next	
	
End Function

;==========================================================
;==========================================================
;==========================================================

Function GAMEinit()

	;light
	RotateEntity light,30,-60,0 
			
	;camera	
	PositionEntity camera,0,250,-250 
	CameraZoom camera, zoom
	CAMERApoint( 0,0,0, 6.75, 1 , 1 , False )
	fade = QUADnew( camera , -90 )
	EntityColor fade,0,0,0
	PositionEntity fade,0,0,6.75
	FADE(0)
	
	;menu camera
	PositionEntity menu_cam,-25000,0,0
	CameraClsMode menu_cam,False,True
	PositionEntity menu_cam,65000,65000,65000
	EntityOrder menu_cam,-999
		
	;bonus
	BONUS\Model = CYLINDERnew(3,255,255,0, -90,0,0, 1,.5,1, 0,0,0, 0,  1,1, 1,1,0 )
						
	;BubbleShield
	BubbleMesh = CYLINDERnew( 3,128,128,128, 0,0,0, 3,2,3, 0,-1,0 ,0, 1,1, 1,1, 0)
	HideEntity BubbleMesh
	EntityShininess BubbleMesh,1
	
	;bullets
	BulletMesh = CYLINDERnew( 6, 128,128,128, -90,0,0, .25,.375,.25, 0,0,0 ,0, 1,2, 1,1, 0, 1)
	HideEntity BulletMesh
	EntityType BulletMesh,T_BULLET
	EntityRadius BulletMesh,.5
	For b=0 To bullets-1
		bullet(b) = New bullet_type
		bullet(b)\Model = CopyEntity ( BulletMesh )
		bullet(b)\Life = 0
		HideEntity bullet(b)\Model
	Next
	
	;tanks
	For l = 0 To tanks-1
		tank(l) = New tank_type
		tank(l)\ID = l
		tank(l)\r = 100+84*(l=1)+42*(l=3)
		tank(l)\g = 100+84*(l=2)+42*(l=3)
		tank(l)\b = 100+84*(l=0)
		TANKinit ( tank(l) )
	Next
	
	;drums
	DrumMesh = CYLINDERnew( 3, 168,116,100, 0,0,0, 1,2,1, 0,0,0 )
	HideEntity DrumMesh
	For l=0 To drums-1
		drum(l) = New drum_type
		drum(l)\Model = CopyEntity ( DrumMesh )
		drum(l)\ID = l
		EntityType drum(l)\Model,T_DRUM
		EntityRadius drum(l)\Model,1.5,2  
		NameEntity drum(l)\Model,Handle(drum(l)) 
		EntityPickMode drum(l)\Model, 1
	Next

	;sounds
	If SOUNDon
		SOUNDexplode = LoadSound("Explode.wav")
		SOUNDclang = LoadSound("Clang.wav")
		SOUNDhit = LoadSound("Hit.wav")
		SOUNDlaser = LoadSound("Laser.wav")
		SOUNDmenu = LoadSound("Menu.wav")
		SOUNDbonus = LoadSound("Bonus.wav")
	EndIf
	
	;GunTarget
	GunTarget = CreateSphere()
	EntityColor GunTarget,50,20,20
	EntityBlend GunTarget,3
	ScaleEntity GunTarget,2,2,2
	HideEntity GunTarget
	
	ARENAinit()
	PARTICLEinit()	
	MENUinit()
	GAMEreset ()
			
End Function

;==========================================================
;==========================================================
;==========================================================

Function PARTICLEinit()

	ParticleTemplate = CYLINDERnew(6, 0,0,0, 0,30,0, 1,.75,1, 0,0,0	, 0, 2,2, 1,1, 2, 1)
	ParticleMesh = CreateMesh()
	EntityFX ParticleMesh,32+2
	EntityShininess ParticleMesh,1
	s= CreateSurface( ParticleMesh )
	gs = GetSurface(ParticleTemplate,1)
	cv = CountVertices ( gs )
	ct = CountTriangles ( gs )
	For l= 0 To particles-1
		vs= l * cv
		particle(l) = New particle_type
		particle(l)\Vertex = vs
		For v=0 To cv-1
			v0 = AddVertex(s,0,0,0)
			VertexNormal s,v0,VertexNX(gs,v),VertexNY(gs,v),VertexNZ(gs,v) 
		Next
		For t=0 To ct - 1
			AddTriangle s, TriangleVertex( gs,t,0)+vs,TriangleVertex( gs,t,1)+vs,TriangleVertex( gs,t,2)+vs
		Next 
	Next
		
End Function

;==========================================================
;==========================================================
;==========================================================

Function ARENAinit()

	Ground=QUADnew( 0, 0, 45,1,45)
	EntityOrder Ground,1
	EntityColor Ground,0,0,0
	EntityBlend Ground,3 
	;EntityAlpha Ground,.75
	
	Plane = CreatePlane()
	EntityColor Plane,0,0,0 
	PositionEntity Plane,0,-.5,0
	EntityAlpha Plane,.5 ;75
	EntityOrder Plane,1
	Mirror = CreateMirror()
	PositionEntity Mirror,0,-.1,0
	Texture=CreateTexture(64,64)
	SetBuffer TextureBuffer(Texture)
	For y=0 To 1:For x=0 To 1
		If ( x + y ) Mod 2 = 0 Color 128,32,128 Else Color 64,16,64 
		Rect x*32,y*32,32,32,1
	Next:Next
			
	SetBuffer BackBuffer()
	ScaleTexture Texture,1.0/9.0,1.0/9.0
	TextureBlend Texture,3	
	EntityTexture Ground,Texture
	FreeTexture Texture
				
	part = CYLINDERnew( 3, 142,100,142, 0,0,0, 2,3,2, -45,3,45 )
	CYLINDERnew( 3, 142,100,142, 90,0,0, 2,3,2, -45,3,0 ,part)
	CYLINDERnew( 3, 128,128,128, 90,0,0, .5,20.5,.5  ,-45,4,-22.5  ,part)
	CYLINDERnew( 3, 128,128,128, 0,0,90, .5,20.5,.5  ,-22.5,4,-45  ,part)
	CYLINDERnew( 3, 128,128,128, 90,0,0, .5,20.5,.5  ,-45,2,-22.5  ,part)
	CYLINDERnew( 3, 128,128,128, 0,0,90, .5,20.5,.5  ,-22.5,2,-45  ,part)
	CYLINDERnew(3, 128,128,128, 90,0,0, .5,4,.5, -20,4.5,25, part)
	CYLINDERnew(3, 128,128,128, 90,0,0, .5,4,.5, -20,1.5,25, part)
	CYLINDERnew(3, 128,128,128, 0,0,90, .5,4,.5, -25,4.5,20, part)
	CYLINDERnew(3, 128,128,128, 0,0,90, .5,4,.5, -25,1.5,20, part)
	CYLINDERnew(3, 142,100,142, 0,0,0, 1,3,1, -20,3,20, part)
	CYLINDERnew(3, 142,100,142, 0,0,0, 1,3,1, -30,3,20, part)
	CYLINDERnew(3, 142,100,142, 0,0,0, 1,3,1, -20,3,30, part)
	ARENA\model = CYLINDERnew( 3,142,100,142, 0,0,0 ,3,2,3, 0,2,0 ,0, 1,1, 1,.5 )
	ARENA\radar = CYLINDERnew( 3,128,128,128,0,0,0,.5,1,1,0,5,0, 0,1,1 )
	CYLINDERnew( 3,142,100,142,90,0,0,2,2,2,0,8,0, ARENA\radar,2,2, 1, 1 )
	
	EntityType ARENA\Model,T_ARENA
	EntityPickMode ARENA\Model,2
	For l=0 To 3
		temp = CopyMesh(part):RotateMesh temp,0,l*90,0
		AddMesh temp,ARENA\Model:FreeEntity temp
		px = 39*( ( l=3 ) - ( l = 0) ) + 13 * ( (l = 2) - ( l = 1) )
		ARENA\hud = CYLINDERnew( 3, 96,96,96, 0,0,90, .25,8,.25, px,13,45 )
		temp = CYLINDERnew( 3, 96,96,96, 0,0,90, 2,.5,1, px - 8.5,12,45, ARENA\hud)
		temp = CYLINDERnew( 3, 96,96,96, 0,0,90, 2,.5,1, px + 8.5,12,45, ARENA\hud )
		temp = CYLINDERnew( 3, 96,96,96, 0,0,90, .2,8,.2, px ,10.5,45, ARENA\hud )
	Next
	FreeEntity part
		
End Function

;==========================================================
;==========================================================
;==========================================================

Function TANKinit( t.tank_type )
	
	;Turret
	t\Turret = CYLINDERnew( 6 , 128,128,128 , 0,0,0 , 1,.5,1 , 0,0,0 )
	;ariel
	CYLINDERnew( 4 , t\r,t\g,t\b , 0,0,0 , .1,1,.1 , .5,1,-.5 , t\Turret)
	;hatch
	CYLINDERnew( 4 , t\r,t\g,t\b , 0,0,0 , .3,.25,.3 , -.25,.5,.25,  t\Turret)
	;barrel
	CYLINDERnew( 4 , 128,128,128 , 90,0,0 , .25,.5,.25 ,0,0,1.5, t\Turret )
	;nosel
	CYLINDERnew( 6 , t\r,t\g,t\b , 90,0,0 , .5,.5,.5 , 0,0,2.5 , t\Turret ,0,1 , .75,1)
	;Turret base
	 CYLINDERnew( 6, t\r,t\g,t\b , 0,0,0 , 1.5,.25,1.5 , 0,-.75,0 , t\Turret )
	;Base
	t\Model = CYLINDERnew( 6, 128,128,128 , 0,0,0 , .75,.5,1.75 , 0,1.5,-.5 )
	;Exhaust
	CYLINDERnew( 6, t\r,t\g,t\b , 90,0,0 , .25,.25,.25 , 0,1.5,-2.5 , t\Model ,1,0 )
	EntityRadius t\Model,2.75
	EntityPickMode t\Model, 1
	EntityType t\Model,T_TANK
	;tracks
	For x# = -1.25 To 1.25 Step 2.5
		CYLINDERnew( 6, 64,64,64 , 0,0,90 , 1,.5,2 , x ,1,0 , t\Model ,0,0 )
		CYLINDERnew( 3, t\r,t\g,t\b, 0,0,-90 , .5,.5,.5 , x,1,0 , t\Model ,1,1)
		CYLINDERnew( 3, t\r,t\g,t\b, 0,0,-90 , .4,.4,.4 , x,1,-1 , t\Model ,1,1)
		CYLINDERnew( 3, t\r,t\g,t\b, 0,0,-90 , .4,.4,.4 , x,1,1 , t\Model ,1,1)
	Next
	PositionMesh t\Model,0,-3,0
	NameEntity t\Model,Handle(t)
	NameEntity t\Turret,Handle(t) 
	
	;mine model
	t\Mine = CYLINDERnew( 6 , 128,128,128 , 0,0,0 , 1,.1,1 , 0,-2.8,0 )
	CYLINDERnew( 6 , t\r,t\g,t\b , 0,0,0 , .5,.1,.5 , 0,-2.6,0, t\Mine )
		
	;bubble shield for respawn
	t\BubbleShield = CopyEntity (BubbleMesh, t\Model)
	EntityColor t\BubbleShield,t\r,t\g,t\b
			
	;hud
	px = 39*( ( t\ID=3 ) - ( t\ID = 0) ) +13 * ( (t\ID = 2) - ( t\ID = 1) )
	t\LifeBar = CYLINDERnew( 3, t\r,t\g,t\b, 0,0,90, 1,8,1, 0,0,0 )
	PositionEntity t\LifeBar,px,13,45
	t\PowerBar = CYLINDERnew( 3, t\r*.5,t\g*.5,t\b*.5, 0,0,90, .5,8,.5, 0,0,0 )
	PositionEntity t\PowerBar,px,13,45
	t\CoolerBar = CYLINDERnew( 3, t\r*.75,t\g*.75,t\b*.75, 0,0,90,.5,8,.5,0,0,0 )
	PositionEntity t\CoolerBar,px,10.5,45
	ScoreMesh = QUADnew(Plane,-90,2.5,2.5,1)
	PositionEntity ScoreMesh,px,7.5,45
	t\ScoreTexture = CreateTexture(32,32)
	EntityTexture ScoreMesh, t\ScoreTexture
				
End Function

;==========================================================
;==========================================================
;==========================================================

Function CYLINDERnew( segs=12 ,r,g,b, rx,ry,rz, sx#,sy#,sz# , px#,py#,pz# ,parent = 0, b_in#=1.0, t_in#=1.0 , b_wi#=1.0, t_wi#=1.0, fx#=2, de#=Detail )

	segs = segs * de
	
	mesh = CreateMesh():s = CreateSurface(mesh)
	For y# = -1 To 1 Step 2
	
		w# = b_wi * (y=-1) + t_wi * (y=1)
	
		For v = 0 To segs-1
			a# = Float(v) * ( 360.0 / Float(segs) )
			v0 = AddVertex( s,w*Cos(a),y,w*Sin(a) )
			VertexColor s,v0,r,g,b
		Next
	Next
	
	vb = AddVertex(s,0,b_in,0):VertexColor s,vb,r*b_in,g*b_in,b*b_in 
	vt = AddVertex(s,0,-t_in,0):VertexColor s,vt,r*t_in,g*t_in,b*t_in
	
	For v1 = 0 To segs-1
		v2 = ( v1 + 1) Mod segs
		v3 = ( v2 + segs )
		v4 = ( v1 + segs )
		AddTriangle s,vt,v1,v2
		AddTriangle s,vb,v3,v4 
		AddTriangle s,v3,v2,v1
		AddTriangle s,v1,v4,v3
	Next
	
	ScaleMesh mesh,sx,sy,sz	
	RotateMesh mesh,rx,ry,rz
	PositionMesh mesh ,px,py,pz
	UpdateNormals mesh
	EntityFX mesh,fx
	EntityShininess mesh,1.0
	If fx=0 EntityColor mesh,r,g,b
	If parent > 0 
		AddMesh mesh,parent:FreeEntity mesh
	Else
		Return mesh
	EndIf

End Function

;==========================================================
;==========================================================
;==========================================================

Function QUADnew( parent=0, rx=0 ,sx#=1, sy#=1, sz#=1 )

	mesh=CreateMesh(parent)
	surface=CreateSurface(mesh)
	AddVertex surface,-1,0,1,0,0
	AddVertex surface,1,0,1,1,0
	AddVertex surface,1,0,-1,1,1
	AddVertex surface,-1,0,-1,0,1
	AddTriangle surface,0,1,2
	AddTriangle surface,2,3,0
	RotateMesh mesh,rx,0,0	
	ScaleMesh mesh,sx,sy,sz
	EntityFX mesh,1
		
	Return mesh
End Function	

;==========================================================
;==========================================================
;==========================================================

.MENUdata

Data "TANX  by Stevie G"
Data 4, 0 	
Data 3,"Players     Demo        One         Two         "
Data 5,"Kills       10          20          30          40          50          "
Data 2,"Control     Mouse       JoyPad      "
Data 1,"Start"

Data "Paused"
Data 2,0
Data 1,"Continue"
Data 1,"Quit"
