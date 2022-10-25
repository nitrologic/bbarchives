; ID: 1034
; Author: Banshee
; Date: 2004-05-22 21:55:26
; Title: Overhead Spaceship Game
; Description: The source code for Space Corps Armageddon

Global music
Global musicChannel

; Variable Declerations
Const shipsQ=100
Const bulletsQ=800
Const explosionsQ=5000
Const ringsQ=5

;Ship Graphic Size
Dim shipSize#(91) ;collision radius of ship type

;Ship Variables
Dim gfx(shipsQ) ;entity no.
Dim x#(shipsQ) ;position
Dim y#(shipsQ) ;position
Dim f#(shipsQ) ;facing
Dim a#(shipsQ) ;acceleration
Dim s#(shipsQ) ;speed
Dim t#(shipsQ) ;top speed
Dim h#(shipsQ) ;handling
Dim w(shipsQ,1) ;weapon Type
Dim wAmmo(shipsQ,1) ;weaponAmmo
Dim wDelay(shipsQ,1) ;weapon fire speed counter
Dim wMaxDelay(shipsQ,1) ;weapon fire speed constant
Dim mHp(shipsQ) ;original hit points
Dim hp(shipsQ) ;hit points
Dim armour(shipsQ) ;armour
Dim size#(shipsQ) ;size of ship
Dim ai(shipsQ) ;-1 = player, >0 = skill
Dim aiState(shipsQ) ;0=combat, 1=gunnery
Dim targ(shipsQ) ;target of AI
Dim damagePuff(shipsQ) ;time since last damage dirty puff
Dim aggression#(shipsQ,shipsQ) ;hate list
Dim side(shipsQ) ;team of ship
Dim radarImage(shipsQ) ;radarDot

;Gameplay Variables
Global camera ;camera entity no.
Global camX#
Global camZ#
Global backdrop ;backdrop entity no.
Dim pieX#(360) ; precalculated sin
Dim pieZ#(360) ; precalculated cos
Global lastTime# ;last frame timer() index
Global gameSpeed# ;framerate based movement index

;Weapon Variables
Global weaponCntr ;weapon entity last used
Dim weapGfx(bulletsQ) ;entity no.
Dim weapLife#(bulletsQ) ;weapon lifespan
Dim weapX#(bulletsQ) ;position
Dim weapY#(bulletsQ) ;position
Dim weapF#(bulletsQ) ;rotation
Dim weapID(bulletsQ) ;owner
Dim weapPay(bulletsQ) ;payload
Dim weapEmmitter#(bulletsQ) ;flame emmitter Frequency
Dim weapEmmitTime#(bulletsQ) ;flame emmitter Timer
Dim weapSpd#(bulletsQ) ;weapon speed
Dim weapAcc#(bulletsQ) ;weapon acceleration
Dim weapTrack(bulletsQ) ;weapon Tracking
Dim weapHoming#(bulletsQ) ;weapon Homing
Global redLaser ;brush no. of weapon
Global greenLaser ;brush no. of weapon
Global blueLaser ;brush no. of weapon
Global missile ;brush no. of weapon
Global torpedo ;brush no. of weapon
Global bomb ;brush no. of weapon
Global massDriver ;brush no. of weapon
Global xeonLaser ;brush no. of weapon

;Explosion Variables
Global expCntr ;explosion entity last used
Dim expBrush(25) ;brush no. of explosion animation
Dim expGfx(explosionsQ) ;entity no.
Dim expFrame(explosionsQ) ;frame displayed
Dim expFrameDelay#(explosionsQ) ;frame timer

;Ring Burst Variables
Global ringCntr ;ring entity last used
Dim ringGfx(ringsQ) ;entity no.
Dim ringLife#(ringsQ) ;ring duration counter

;Sound Variables
Global laserSnd
Global missileSnd
Global massSnd
Global xeonSnd
Global bangSnd
Dim impactSnd(5)

;menu
Dim logoG(25)
Global menuActive
Global menu
Global titleScreen
Global radar
Global hud
Global score
Global defeat
Global bronze
Global silver
Global gold
Global playerSpawn
Global redTime
Global blueTime
Global blueQty
Global redQty
Global powerTimer
Global redShips
Global blueShips
Global redSpawnSpeed
Global blueSpawnSpeed

;powerUps
Dim powerX(10)
Dim powerY(10)
Dim powerT(10)
Dim powerGfx(10)
Dim powerBrush(3)
Global powerCntr
Global playerShipHull
; Program Initialisation
init() ;setup display
initHud() ;setup HUD interface

readShipSizeFile(); setup ship size data array
initFacing() ;calculate pie

menuActive=10000

; Main Loop
gameTimer()
Repeat
	gameTimer()

	If KeyDown(1) Then End

	respawn()

	For sh=shipsQ To 2 Step-1
		If hp(sh)>0
			camTrack=sh
			If side(sh)=1
				blueQty=blueQty+1
			Else
				redQty=redQty+1
			EndIf
			aiCombat(sh)
			move(sh)
		EndIf
	Next
	If hp(1)>0
		camTrack=1
		blueQty=blueQty+1
		control(1)
		move(1)
		pickups()
		playerSpawn=10000
	Else
		playerSpawn=playerSpawn -gameSpeed
		If playerSpawn<0 Then blueTime=99999999
	EndIf

	bullets()
	explosions()
	rings()
	
	camera(camTrack)
Forever

Function respawn()
	If redShips>0 And redQty<50
		redTime=redTime +gameSpeed
		If redTime>redSpawnSpeed
			redTime=0
			id=findFreeShip(2)
			If id>0
				redShips=redShips-1
				initShip(id,Rnd(1,91),2)

				x(id)=Rnd(40)-Rnd(40)
				y(id)=50
				f(id)=Rnd(90,270)
			EndIf
		EndIf
	EndIf
	
	If blueShips>0 And blueQty<50
		blueTime=blueTime +gameSpeed
		If blueTime>blueSpawnSpeed
			blueTime=0

			If playerSpawn<0
				id=findFreeShip(1)
			Else
				id=findFreeShip(2)
			EndIf

			If id>1
				initShip(id,Rnd(1,91),1)
				ai(id)=-1
			Else
				If id>0
					playerShipHull=playerShipHull-1
					If playerShipHull<1 Then playerShipHull=1
					initShip(id,playerShipHull,1)
				EndIf
			EndIf
			If id>0
				blueShips=blueShips-1
				x(id)=Rnd(40)-Rnd(40)
				y(id)=-50
				f(id)=Rnd(90,270)
			EndIf
		EndIf
	EndIf
	
	powerTimer=powerTimer +gameSpeed
	If powerTimer>30000
		powerTimer=0
		n=Rnd(2,shipsQ)
		If hp(n)>0 Then placePowerUp(n)
	EndIf
	
	redQty=0
	blueQty=0
End Function
Function findFreeShip(strt)
	ret=0
	For id=strt To shipsQ
		If hp(id)<1
			Ret=id
			id=shipsQ+1
		EndIf
	Next
	Return ret
End Function
Function camera(camTrack)
	camX=x(camTrack)
	If camX<-32.0
		camX=-32.0
	Else
		If camX>32.0
			camX=32.0
		EndIf
	EndIf

	camZ=y(camTrack)
	If camZ<-40.0
		camZ=-40.0
	Else
		If camZ>40.0
			camZ=40.0
		EndIf
	EndIf

	PositionEntity camera,camX,camZ,0	
	RenderWorld
	
	If menuActive=>10000
		If redQty=0 Or blueQty=0 Then menuActive=0
		If menuActive<30000
			For combat=2 To 6
				initShip(combat,Rnd(1,91),2)
				initShip(combat+5,Rnd(1,91),1)
			Next
			menuActive=30000
		EndIf
		DrawImage menu,432,282
		If GetKey()>0
			menuActive=0
			begin()
		EndIf
	Else
		If redQty+redShips<1
			If menuActive=0 Then musicChannel=PlaySound(music)
			menuActive=menuActive +gameSpeed
			FlushKeys
			If blueQty+blueShips<10
				DrawImage bronze,361,299
			Else
				If blueQty+blueShips<20
					DrawImage silver,361,299
				Else
					DrawImage gold,361,299
				EndIf
			EndIf
			Text 506,447,"Score "+score
		Else
			If blueQty+blueShips<1
				If menuActive=0 Then musicChannel=PlaySound(music)
				FlushKeys
				menuActive=menuActive +gameSpeed
				DrawImage defeat,361,299
				Text 506,447,"Score "+score
			Else
				If hp(1)>0 Or blueShips<1
					DrawImage hud,10,10

					Color 255,255,0
					Text 23,76,"Missiles "+wAmmo(camTrack,1),0,1

					Text 158,55,"Hull",1
					Text 158,75,hp(camTrack)+"/"+mHp(camTrack),1

					Color 0,0,255
					Text 76,88,"Blue "+blueQty
					Text 128,124,blueShips,1,1
					Color 255,0,0
					r$=("Red "+redQty)
					Text 241-StringWidth(r$),88,r$
					Text 190,124,redShips,1,1

					Color 255,255,255
					r$="Score "+score
					Text 293-StringWidth(r$),76,r$,0,1
				Else
					DrawImage titleScreen,512-160,384-128
					Text 512,384+98,"Respawn in "+(playerSpawn/1000)+"s",1
				EndIf
			EndIf
		EndIf
	EndIf
	
	Text 10,748,"http://www.bansheestudios.com"
	fps$=Int(1000/gameSpeed)+" fps"
	Text 1014-StringWidth(fps$),748,fps$

	radar(camTrack)

	Flip 0
End Function
Function radar(camTrack)
	DrawImage radar,820,5
	For sh=1 To shipsQ
		If hp(sh)>0
			If sh<>camTrack
				rng#=range2D(x(camTrack),y(camTrack),x(sh),y(sh))
				If rng#<=44
					bearing=f(camTrack)-ATan2(x(camTrack)-x(sh),y(camTrack)-y(sh))
					While bearing<0
						bearing=bearing+360
					Wend
					While bearing>360
						bearing=bearing-360
					Wend
					xPos=pieX(bearing)*rng#
					yPos=pieZ(bearing)*rng#
					DrawImage radarImage(sh),917+xPos,97+yPos
				EndIf
			EndIf
		EndIf
	Next
End Function

; Realtime - Ship Functions
Function control(id) ;accept player control
	If KeyDown(200) ;speed control
		s(id)=s(id)+ (a(id) * gameSpeed#)
		If s(id)>t(id) Then s(id)=t(id)
	Else
		If KeyDown(208)
			s(id)=s(id)- (a(id) * gameSpeed)
			If s(id)<0 Then s(id)=0
		EndIf
	EndIf
	
	If KeyDown(203) ;bearing control
		f(id)=f(id)- (h(id) * gameSpeed)
		If f(id)<0 Then f(id)=f(id)+360
	Else
		If KeyDown(205)
			f(id)=f(id)+ (h(id) * gameSpeed)
			If f(id)>360 Then f(id)=f(id)-360
		EndIf
	EndIf
	
	If wDelay(id,0)<0 ;main weapon delay
		If KeyDown(57)
			If wAmmo(id,0)<>0
				wAmmo(id,0)=wAmmo(id,0)-1
				wDelay(id,0)=wMaxDelay(id,0)
				fireWeapon(id,0)
			EndIf
		EndIf
	Else
		wDelay(id,0)=wDelay(id,0) -gameSpeed
	EndIf
	
	If wDelay(id,1)<0 ;secondary weapon delay
		If KeyDown(29)
			If wAmmo(id,1)<>0
				wAmmo(id,1)=wAmmo(id,1)-1
				wDelay(id,1)=wMaxDelay(id,1)
				fireWeapon(id,1)
			EndIf
		EndIf
	Else
		wDelay(id,1)=wDelay(id,1) -gameSpeed
	EndIf
End Function
Function aiCombat(id) ;artificial intelligence
	If hp(targ(id))<1 ;target acquisition
		targ(id)=0
		selectTarget(id)
	EndIf

	If targ(id)=0
		SelectTarget(id)
	Else
		If aiState(id)<2 ;fighting AI
			facing=Int(f(targ(id)))
			aheadX#=x(targ(id))+ (pieX(facing) * s(targ(id)) * 500)
			aheadZ#=y(targ(id))+ (pieZ(facing) * s(targ(id)) * 500)
		Else
			If aiState(id)=2 ;evasion AI
				facing=f(targ(id))+90
				If facing>360 Then facing=facing-360
			Else
				If aiState(id)=3
					facing=f(targ(id))-90
					If facing<0 Then facing=facing+360
				EndIf
			EndIf
			aheadX#=x(targ(id))+ (pieX(facing) * 1500)
			aheadZ#=y(targ(id))+ (pieZ(facing) * 1500)
		EndIf
		
		bearing=ATan2( aheadX-x(id) , aheadZ-y(id) )-f(id) ;AI aiming parameters
		While bearing>360
			bearing=bearing-360
		Wend
		While bearing<0
			bearing=bearing+360
		Wend
		
		rng#=range2D( x(id),y(id) , aheadX,aheadZ )
		
		If rng<10 ;range control
			top#=(t(id)*(rng/10))-.0015
		Else
			top#=t(id)-.0015
		EndIf
		If top<=0
			aiState(id)=2+Rnd(0,1)
			top=0
		EndIf
		If s(id)>top
			s(id)=s(id)- (a(id) * gameSpeed)
			If s(id)<0 Then s(id)=0
		Else
			If aiState(id)=1 ;speed Control
				s(id)=s(id)- (a(id) * gameSpeed)
				If s(id)<0 Then s(id)=0
			Else
				If aiState(id)=2 Or aiState(id)=3
					s(id)=s(id)+ (a(id) * gameSpeed)
					If s(id)>t(id) Then s(id)=t(id)
					If rng<2 Then aiState(id)=0
				Else
					If bearing>250 Or bearing<120
						s(id)=s(id)+ (a(id) * gameSpeed#)
						If s(id)>t(id) Then s(id)=t(id)
					Else
						If bearing>135 And bearing<225
							s(id)=s(id)- (a(id) * gameSpeed)
							If s(id)<0 Then s(id)=0
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
		
		If bearing>180 ;bearing control
			If bearing<355
				f(id)=f(id)- (h(id) * gameSpeed)
				If f(id)<0 Then f(id)=f(id)+360
			EndIf
		Else
			If bearing>5
				f(id)=f(id)+ (h(id) * gameSpeed)
				If f(id)>360 Then f(id)=f(id)-360
			EndIf
		EndIf

		If wDelay(id,0)<0 ;main Weapon
			If bearing>340 Or bearing<20
				aiState(id)=0
				If rng<8
					If wAmmo(id,0)<>0
						wAmmo(id,0)=wAmmo(id,0)-1
						wDelay(id,0)=wMaxDelay(id,0)
						fireWeapon(id,0)
					EndIf
				EndIf
			EndIf
		EndIf
		
		wDelay(id,0)=wDelay(id,0) -gameSpeed ;primary weapon timer also serves fighting AI state
		If wDelay(id,0)<-5000
			wDelay(id,0)=0
			aiState(id)=1
			targ(id)=0
		EndIf
		
		If wDelay(id,1)<0 ;secondary Weapon
			If rng<5
				If bearing>355 Or bearing<5
					If wAmmo(id,1)<>0
						wAmmo(id,1)=wAmmo(id,1)-1
						wDelay(id,1)=wMaxDelay(id,1)
						fireWeapon(id,1)
					EndIf
				EndIf
			EndIf
		Else
			wDelay(id,1)=wDelay(id,1) -gameSpeed
		EndIf
			
	EndIf
End Function
Function selectTarget(id) ;target selection
	facing=Int(f(id))
	aimX#=x(id)+ (pieX(facing) * 2)
	aimZ#=y(id)+ (pieZ(facing) * 2)
	
	targetRange#=9999
	For sh=1 To shipsQ
		If hp(sh)>0
			If side(sh)<>side(id)
				If sh<>id
					rng#=range2D( aimX,aimZ , x(sh),y(sh) ) - aggression(id,sh)
					If rng<targetRange
						targetRange=rng
						targ(id)=sh
					EndIf
				EndIf
			EndIf
		EndIf
	Next
End Function
Function move(id) ;process movements of each ship

	If damagePuff(id)<0 ;damage trail
		damagePuff(id)=damagePuff(id)+50
		chnc=Rnd(0,mHp(id))
		If chnc>hp(id)
			factor#=1-Sin( (hp(id)*90) /mHp(id))
			makeExplosion( x(id),y(id), factor# ,1)
		EndIf
	Else
		damagePuff(id)=damagePuff(id) -gameSpeed
	EndIf

	facing=Int(f(id)) ;movement
	x(id)=x(id)+ (pieX(facing) * s(id) * gameSpeed)
	y(id)=y(id)+ (pieZ(facing) * s(id) * gameSpeed)
	
	If x(id)<-40.0
		x(id)=-40.0
	Else
		If x(id)>40.0
			x(id)=40.0
		EndIf
	EndIf
	If y(id)<-46.0
		y(id)=-46.0
	Else
		If y(id)>46.0
			y(id)=46.0
		EndIf
	EndIf
	
	PositionEntity gfx(id),x(id),y(id),10
	RotateSprite gfx(id),-f(id)
End Function
Function placePowerUp(id)
	powerCntr=powerCntr+1
	If powerCntr=11 Then powerCntr=1

	pType=Rnd(1,3)
	If pType=>1 And pType<=3
		powerX(powerCntr)=x(id)
		powerY(powerCntr)=y(id)
		powerT(powerCntr)=pType
		PaintEntity powerGfx(powerCntr),powerBrush(pType)
		PositionEntity powerGfx(powerCntr),powerX(powerCntr),powerY(powerCntr),10.35
		ShowEntity powerGfx(powerCntr)
	EndIf
End Function
Function pickups()
	For pick=1 To 10
		If powerT(pick)>0
			If range2D(x(1),y(1),powerX(pick),powerY(pick))<size(1)/2
				Select powerT(pick)
					Case 1
					hp(1)=mHp(1)
					Case 2
					playerShipHull=playerShipHull+5
					If playerShipHull>91 Then playerShipHull=91
					initShip(1,playerShipHull,1)
					Case 3
					wAmmo(1,1)=wAmmo(1,1)+10
				End Select
				HideEntity powerGfx(pick)
				powerT(pick)=0
			EndIf
		EndIf
	Next
End Function
Function fireWeapon(id,weapon) ;called when a weapon is fired to choose which weapon to launch
	bulletCounter()
	Select w(id,weapon)
		Case 1
		fireRedLaser(id)
		Case 2
		fireGreenLaser(id)
		Case 3
		fireBlueLaser(id)
		Case 4
		fireMissile(id)
		Case 5
		fireTorpedo(id)
		Case 6
		fireBomb(id)
		Case 7
		fireMassDriver(id)
		Case 8
		fireXeonLaser(id)
	End Select
End Function
Function bulletCounter()
	safety=0
	Repeat
		weaponCntr=weaponCntr+1
		If weaponCntr>bulletsQ Then weaponCntr=1
		safety=safety+1
		If safety>bulletsQ Then weapLife(weaponCntr)=-1
	Until weapLife(weaponCntr)<=0
End Function
Function fireRedLaser(id) ;Create a red laser bullet
	weapX#(weaponCntr)=x(id)
	weapY#(weaponCntr)=y(id)
	weapF(weaponCntr)=f(id)
	weapID(weaponCntr)=id
	weapLife#(weaponCntr)=2000
	weapPay(weaponCntr)=Rnd(1,4)
	weapEmmitter(weaponCntr)=0
	weapSpd(weaponCntr)=0.005
	weapAcc(weaponCntr)=0
	weapTrack(weaponCntr)=0

	PaintEntity weapGfx(weaponCntr),redLaser
	RotateSprite weapGfx(weaponCntr),-f(id)
	ShowEntity weapGfx(weaponCntr)

	If range2D(x(id),y(id),camX,camZ)<5 Then PlaySound laserSnd	
End Function
Function fireGreenLaser(id) ;Create a green laser bullet
	weapX#(weaponCntr)=x(id)
	weapY#(weaponCntr)=y(id)
	weapF(weaponCntr)=f(id)
	weapID(weaponCntr)=id
	weapLife#(weaponCntr)=4000
	weapPay(weaponCntr)=Rnd(1,6)
	weapEmmitter(weaponCntr)=0
	weapSpd(weaponCntr)=0.005
	weapAcc(weaponCntr)=0
	weapTrack(weaponCntr)=0

	PaintEntity weapGfx(weaponCntr),greenLaser
	RotateSprite weapGfx(weaponCntr),-f(id)
	ShowEntity weapGfx(weaponCntr)

	If range2D(x(id),y(id),camX,camZ)<5 Then PlaySound laserSnd	
End Function
Function fireBlueLaser(id) ;Create a blue laser bullet
	weapX#(weaponCntr)=x(id)
	weapY#(weaponCntr)=y(id)
	weapF(weaponCntr)=f(id)
	weapID(weaponCntr)=id
	weapLife#(weaponCntr)=6000
	weapPay(weaponCntr)=Rnd(1,8)
	weapEmmitter(weaponCntr)=0
	weapSpd(weaponCntr)=0.005
	weapAcc(weaponCntr)=0
	weapTrack(weaponCntr)=0

	PaintEntity weapGfx(weaponCntr),blueLaser
	RotateSprite weapGfx(weaponCntr),-f(id)
	ShowEntity weapGfx(weaponCntr)
	
	If range2D(x(id),y(id),camX,camZ)<5 Then PlaySound laserSnd	
End Function
Function fireMissile(id) ;Create a missile
	weapX#(weaponCntr)=x(id)
	weapY#(weaponCntr)=y(id)
	weapF(weaponCntr)=f(id)
	weapID(weaponCntr)=id
	weapLife#(weaponCntr)=6000
	weapPay(weaponCntr)=Rnd(3,8)
	weapEmmitter(weaponCntr)=15
	weapSpd(weaponCntr)=s(id)*.5
	weapAcc(weaponCntr)=0.00003
	weapTrack(weaponCntr)=0

	PaintEntity weapGfx(weaponCntr),missile
	RotateSprite weapGfx(weaponCntr),-f(id)
	ShowEntity weapGfx(weaponCntr)
	
	If range2D(x(id),y(id),camX,camZ)<5 Then PlaySound missileSnd	
End Function
Function fireTorpedo(id) ;Create a torpedo
	weapX#(weaponCntr)=x(id)
	weapY#(weaponCntr)=y(id)
	weapF(weaponCntr)=f(id)
	weapID(weaponCntr)=id
	weapLife#(weaponCntr)=20000
	weapPay(weaponCntr)=Rnd(5,12)
	weapEmmitter(weaponCntr)=10
	weapSpd(weaponCntr)=s(id)*.33
	weapAcc(weaponCntr)=0.00001
	weapHoming(weaponCntr)=0.04

	PaintEntity weapGfx(weaponCntr),missile
	RotateSprite weapGfx(weaponCntr),-f(id)
	ShowEntity weapGfx(weaponCntr)
	
	facing=Int(f(id))
	aimX#=x(id)+ (pieX(facing) * 2)
	aimZ#=y(id)+ (pieZ(facing) * 2)
	
	targetRange#=9999
	For sh=1 To shipsQ
		If hp(sh)>0
			If side(sh)<>side(id)
				If sh<>id
					rng#=range2D( aimX,aimZ , x(sh),y(sh) )
					If rng<targetRange
						targetRange=rng
						weapTrack(weaponCntr)=sh
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	
	If range2D(x(id),y(id),camX,camZ)<5 Then PlaySound missileSnd	
End Function
Function fireBomb(id) ;Create a bomb
	weapX#(weaponCntr)=x(id)
	weapY#(weaponCntr)=y(id)
	weapF(weaponCntr)=f(id)
	weapID(weaponCntr)=id
	weapLife#(weaponCntr)=30000
	weapPay(weaponCntr)=Rnd(9,16)
	weapEmmitter(weaponCntr)=5
	weapSpd(weaponCntr)=s(id)*.25
	weapAcc(weaponCntr)=0.000005
	weapHoming(weaponCntr)=0.06

	PaintEntity weapGfx(weaponCntr),bomb
	RotateSprite weapGfx(weaponCntr),-f(id)
	ShowEntity weapGfx(weaponCntr)
	
	facing=Int(f(id))
	aimX#=x(id)+ (pieX(facing) * 2)
	aimZ#=y(id)+ (pieZ(facing) * 2)
	
	targetRange#=9999
	For sh=1 To shipsQ
		If hp(sh)>0
			If side(sh)<>side(id)
				If sh<>id
					rng#=range2D( aimX,aimZ , x(sh),y(sh) )
					If rng<targetRange
						targetRange=rng
						weapTrack(weaponCntr)=sh
					EndIf
				EndIf
			EndIf
		EndIf
	Next

	If range2D(x(id),y(id),camX,camZ)<5 Then PlaySound missileSnd	
End Function
Function fireMassDriver(id) ;Create a mass driver bullet
	weapX#(weaponCntr)=x(id)
	weapY#(weaponCntr)=y(id)
	weapF(weaponCntr)=f(id)+Rnd(5)-Rnd(5)
	If weapF(weaponCntr)>360.0 Then weapF(weaponCntr)=weapF(weaponCntr)-360.0
	If weapF(weaponCntr)<0.0 Then weapF(weaponCntr)=weapF(weaponCntr)+360.0
	weapID(weaponCntr)=id
	weapLife#(weaponCntr)=1000
	weapPay(weaponCntr)=Rnd(1,8)
	weapEmmitter(weaponCntr)=0
	weapSpd(weaponCntr)=0.008
	weapAcc(weaponCntr)=-0.000025
	weapTrack(weaponCntr)=0

	PaintEntity weapGfx(weaponCntr),massDriver
	RotateSprite weapGfx(weaponCntr),-weapF(weaponCntr)
	ShowEntity weapGfx(weaponCntr)
	
	If range2D(x(id),y(id),camX,camZ)<5 Then PlaySound massSnd	
End Function
Function fireXeonLaser(id) ;Create a xeon laser bullet
	weapX#(weaponCntr)=x(id)
	weapY#(weaponCntr)=y(id)
	weapF(weaponCntr)=f(id)
	weapID(weaponCntr)=id
	weapLife#(weaponCntr)=6000
	weapPay(weaponCntr)=Rnd(1,20)
	weapEmmitter(weaponCntr)=0
	weapSpd(weaponCntr)=0.005
	weapAcc(weaponCntr)=0
	weapTrack(weaponCntr)=0

	PaintEntity weapGfx(weaponCntr),xeonLaser
	RotateSprite weapGfx(weaponCntr),-f(id)
	ShowEntity weapGfx(weaponCntr)
	
	If range2D(x(id),y(id),camX,camZ)<5 Then PlaySound xeonSnd	
End Function



;Realtime - Bullet & Particle Functions
Function bullets() ;move Bullets
	For b=1 To bulletsQ
		If weapLife(b)>0
			weapLife(b)=weapLife(b)-gameSpeed#
			
			If weapTrack(b)>0
				bearing=ATan2( x(weapTrack(b))-weapX(b) , y(weapTrack(b))-weapY(b) ) - weapF(b)
				While bearing>360
					bearing=bearing-360
				Wend
				While bearing<0
					bearing=bearing+360
				Wend
				If bearing>180
					If bearing<355
						weapF(b)=weapF(b) -(gameSpeed*weapHoming(b))
						If weapF(b)<0.0 Then weapF(b)=weapF(b)+360.0
					EndIf
				Else
					If bearing>5
						weapF(b)=weapF(b) +(gameSpeed*weapHoming(b))
						If weapF(b)>360.0 Then weapF(b)=weapF(b)-360.0
					EndIf
				EndIf
				RotateSprite weapGfx(b),-weapF(b)
			EndIf

			
			For spd=1 To 2
				weapSpd(b)=weapSpd(b)+weapAcc(b)
				facing=Int(weapF(b))
				weapX(b)=weapX(b) +(pieX(facing) * weapSpd(b) * gameSpeed)
				weapY(b)=weapY(b) +(pieZ(facing) * weapSpd(b) * gameSpeed)
		
				For sh=1 To shipsQ
					If hp(sh)>0
						If weapID(b)<>sh
							If range2D(weapX(b),weapY(b),x(sh),y(sh))<size(sh)
								If side(weapID(b))=side(sh)
									targ(weapID(b))=0
									aiState(weapID(b))=2+Rnd(1)
								Else
									weapLife(b)=0.0

									bearing=Int(ATan2(weapX(b)-x(sh),weapY(b)-y(sh)))
									While bearing>360
										bearing=bearing-360
									Wend
									While bearing<0
										bearing=bearing+360
									Wend
										
									If bearing=>315 Or bearing<45
										damage=weapPay(b)-(armour(sh)+2)
									Else
										If bearing=>225 And bearing<315
											damage=weapPay(b)-armour(sh)
										Else
											If bearing=>45 And bearing<135
												damage=weapPay(b)-armour(sh)
											Else
												damage=(weapPay(b)+3)-armour(sh)
											EndIf
										EndIf
									EndIf	
									
									If damage<0 Then damage=0
									hp(sh)=hp(sh)-damage
									If damage>2 Then aiState(sh)=2+Rnd(0,1)
									aggression(sh,weapID(b))=aggression(sh,weapID(b)) +(damage*.1)
									selectTarget(sh)
									explosion#=(damage+1.0)/10
	
									expX#=x(sh)+ ( pieX(bearing) * size(sh) )
									expZ#=y(sh)+ ( pieZ(bearing) * size(sh) )
									If hp(sh)<1
										makeRing(sh)
										makeExplosion(expX#,expZ#,1.0,0)
										makeExplosion(x(sh),y(sh),size(sh)*5,0)
										HideEntity gfx(sh)
										If range2D(x(sh),y(sh),camX,camZ)<15 Then PlaySound bangSnd
										If side(sh)=2
											If weapID(b)=1 Or aggression(sh,1)>mHp(sh)*.3
												score=score+mHp(sh)
												placePowerUp(sh)
												playerShipHull=playerShipHull+1
											EndIf
											redSpawnSpeed=redSpawnSpeed-500
											If redSpawnSpeed<1000 Then redSpawnSpeed=1000
											blueSpawnSpeed=blueSpawnSpeed+400
											If blueSpawnSpeed>10000 Then blueSpawnSpeed=10000
										Else
											blueSpawnSpeed=blueSpawnSpeed-500
											If blueSpawnSpeed<1000 Then blueSpawnSpeed=1000
											redSpawnSpeed=redSpawnSpeed+400
											If redSpawnSpeed>10000 Then redSpawnSpeed=10000
										EndIf
									Else
										If damage>0
											If weapID(b)=1 And side(sh)=2 Then score=score+damage
											makeExplosion(expX#,expZ#,explosion,0)
											If range2D(expX,expZ,camX,camZ)<7 Then PlaySound impactSnd(Rnd(1,5))
										EndIf
									EndIf
						
									spd=3
									sh=101
								EndIf
							EndIf
						EndIf
					EndIf
				Next
			Next

			If weapLife(b)<=0
				HideEntity weapGfx(b)
			Else
				PositionEntity weapGfx(b),weapX(b),weapY(b),10.00000001
				If weapEmmitter(b)>0
					weapEmmitTime(b)=weapEmmitTime(b) -gameSpeed
					If weapEmmitTime(b)<0
						weapEmmitTime(b)= weapEmmitter(b)
						makeExplosion(weapX(b),weapY(b),0.3,1)
					EndIf
				EndIf
			EndIf

		EndIf
	Next
End Function
Function makeExplosion(x#,y#,sz#,layer) ;create new explosion entity
	expCntr=expCntr+1
	If expCntr>explosionsQ Then expCntr=1
	
	expFrame(expCntr)=1
	expFrameDelay(expCntr)=50
	
	If layer=0
		PositionEntity expGfx(expCntr),x#,y#,9.85
	Else
		PositionEntity expGfx(expCntr),x#,y#,10.15
	EndIf
	ScaleSprite expGfx(expCntr),sz#,sz#
	PaintEntity expGfx(expCntr),expBrush(1)
	ShowEntity expGfx(expCntr)
End Function
Function explosions() ;animate explosion entities
	For ex=1 To explosionsQ
		If ExpFrame(ex)>0
			expFrameDelay(ex)=expFrameDelay(ex)-gameSpeed
			If expFrameDelay(ex)<0
				expFrame(ex)=expFrame(ex)+1
				If expFrame(ex)<=25
					PaintEntity expGfx(ex),expBrush(expFrame(ex))
					expFrameDelay(ex)=50
				Else
					expFrame(ex)=0
					HideEntity expGfx(ex)
				EndIf
			EndIf
		EndIf
	Next
End Function
Function makeRing(id) ;make a ring burst
	ringCntr=ringCntr+1
	If ringCntr>ringsQ Then ringCntr=1

	PositionEntity ringGfx(ringCntr),x(id),y(id),10
	RotateEntity ringGfx(ringCntr),Rnd(-67,67),Rnd(-67,67),Rnd(-67,67)
	ShowEntity ringGfx(ringCntr)
	
	ringLife(ringCntr)=1
End Function
Function rings()
	For ring=1 To ringsQ
		If ringLife(ring)>0
			ringLife(ring)=ringLife(ring) +gameSpeed
			If ringLife(ring)<1000
				scale#=ringLife(ring)*.05
				ScaleEntity ringGfx(ring),scale,scale,scale
			Else
				ringLife(ring)=0
				HideEntity ringGfx(ring)
			EndIf
		EndIf
	Next
End Function

;UDF's
Function gameTimer() ;calculate time interlude between frames
	time=Float(MilliSecs())
	gameSpeed=time-lastTime
	lastTime=time
End Function
Function range2D(ax#,az#,bx#,bz#)
	dx#=Abs(ax#-bx#)
	dz#=Abs(az#-bz#)
	Return Sqr((dx#*dx#)+(dz#*dz#))
End Function

; Program Setup
Function init() ;setup graphics for game
	;setup display
	Graphics3D 1024,768
	SetBuffer BackBuffer()

	camera=CreateCamera()

	intro()
	
	;make a background
	backdrop=LoadSprite("Backdrop/backdrop1.jpg")
	PositionEntity backdrop,0,0,35
	ScaleSprite backdrop,67,67
	
	;setup weapons
	redLaser=LoadBrush("Weapons/redLaser.png",2)
	greenLaser=LoadBrush("Weapons/greenLaser.png",2)
	blueLaser=LoadBrush("Weapons/blueLaser.png",2)
	missile=LoadBrush("Weapons/missile.png",2)
	torpedo=LoadBrush("Weapons/bomb2.png",2)
	bomb=LoadBrush("Weapons/bomb1.png",2)
	massDriver=LoadBrush("Weapons/massDriver.png",2)
	xeonLaser=LoadBrush("Weapons/xeonLaser.png",2)
	For weapon=1 To bulletsQ
		weapGfx(weapon)=CreateSprite()
		HideEntity weapGfx(weapon)
		HandleSprite weapGfx(weapon),0,0
		ScaleSprite weapGfx(weapon),0.34,0.34
	Next
	
	;setup explosion brushes
	For ex=1 To 25
		expBrush(ex)=LoadBrush("Explosion/dth"+Str(ex)+".bmp",2)
	Next
	;setup explosion entities
	For ex=1 To explosionsQ
		expGfx(ex)=CreateSprite()
		RotateSprite expGfx(ex),Rnd(1.0,360.0)
		HideEntity expGfx(ex)
		EntityAlpha expGfx(ex),0.35
		HandleSprite expGfx(ex),0,0
	Next
	
	;setup ring burst
	For ring=1 To ringsQ
		ringGfx(ring)=LoadSprite("Explosion/ringBurst.png",1)
		SpriteViewMode ringGfx(ring),4
		HideEntity ringGfx(ring)
		EntityAlpha ringGfx(ring),0.4
	Next
	
	;powerUps
	For power=1 To 10
		powerGfx(power)=CreateSprite()
		HideEntity powerGfx(power)
	Next

	laserSnd=LoadSound("SFX\laser.wav")
	missileSnd=LoadSound("SFX\missileLaunch.wav")
	massSnd=LoadSound("SFX\massDriver.wav")
	xeonSnd=LoadSound("SFX\xeonLaser.wav")
	bangSnd=LoadSound("SFX\bang1.wav")
	impactSnd(1)=LoadSound("SFX\impact1.wav")
	impactSnd(2)=LoadSound("SFX\impact2.wav")
	impactSnd(3)=LoadSound("SFX\impact3.wav")
	impactSnd(4)=LoadSound("SFX\impact4.wav")
	impactSnd(5)=LoadSound("SFX\impact5.wav")
End Function
Function initFacing() ;calculate pie
	For facing=0 To 360
		pieX(facing)=Sin(facing)
		pieZ(facing)=Cos(facing)
	Next
End Function
Function initShip(id,class,team) ;create a ship
	If gfx(id)>0 Then FreeEntity gfx(id)
	If team=1
		gfx(id)=LoadSprite("Ships\"+Str(class)+".png",2)
		radarImage(id)=LoadImage("UI\blue.png")
	Else
		gfx(id)=LoadSprite("Ships\r"+Str(class)+".png",2)
		radarImage(id)=LoadImage("UI\red.png")
	EndIf
	
	For clear=1 To shipsQ
		aggression(id,clear)=0
	Next
	
	side(id)=team
		
	a(id)=0.000006-(shipSize(class)*0.000003)
	t(id)=0.012-shipSize(class)*0.006
	h(id)=(0.15-(shipSize(class)*0.1))*1.3
	
	size(id)=shipSize(class)
	armour(id)=Int(shipSize(class)*3.5)
	hp(id)=(shipSize(class)*100)+class
	
	If id<>1 Then armour(id)=armour(id)-1
	If armour(id)<0 Then armour(id)=0
	
	f(id)=Rnd(1,360)
	ai(id)=1
	mhp(id)=hp(id)
	
	If class<30
		w(id,0)=1
		wMaxDelay(id,0)=250-class

		w(id,1)=4
		wMaxDelay(id,1)=750-(class*3)
		wAmmo(id,1)=size(id)*20
	Else
		If class<60
			n=Rnd(1)
			If n=0
				w(id,0)=2
				wMaxDelay(id,0)=250-class
			Else
				w(id,0)=8
				wMaxDelay(id,0)=1000-(class*4)
			EndIf

			w(id,1)=5
			wMaxDelay(id,1)=750-(class*3)
			wAmmo(id,1)=size(id)*15
		Else
			n=Rnd(1)
			If n=0
				w(id,0)=3
				wMaxDelay(id,0)=250-class
			Else
				w(id,0)=7
				wMaxDelay(id,0)=150-(class*.67)
			EndIf

			w(id,1)=6
			wMaxDelay(id,1)=1050-(class*4)
			wAmmo(id,1)=size(id)*10
		EndIf
	EndIf
	wAmmo(id,0)=-1
	
	
End Function
Function readShipSizeFile()
	fileHandle=ReadFile("ShipSize.dat")
	For sh=1 To 91
		sizeX=ReadByte(fileHandle)
		sizeZ=ReadByte(fileHandle)
		shipSize(sh)=(sizeX+sizeZ)
		shipSize(sh)=shipSize(sh)*0.01388888
	Next
	CloseFile fileHandle
End Function
Function initHud()
	titleScreen=LoadImage("UI\SpaceCorpsTitle.png")
	radar=LoadImage("UI\Radar.png")
	hud=LoadImage("UI\hud.png")
	
	bronze=LoadImage("UI\bronze.png")
	silver=LoadImage("UI\silver.png")
	gold=LoadImage("UI\gold.png")
	defeat=LoadImage("UI\defeat.png")
	menu=LoadImage("UI\menu.png")
	
	powerBrush(1)=LoadBrush("UI\repair.png",2)
	powerBrush(2)=LoadBrush("UI\upgrade.png",2)
	powerBrush(3)=LoadBrush("UI\missile.jpg",2)
End Function
Function intro()
	music=LoadSound("SFX\theme.wav")
	LoopSound music
	musicChannel=PlaySound(music)
	logo=LoadBrush("UI\logo.png",2)
	gameTimer()

	For lo=1 To 25
		logoG(lo)=CreateSprite()
		ScaleSprite logoG(lo),64,25
		EntityAlpha logoG(lo),.0004
		PaintEntity logoG(lo),logo
	Next

	tim=0
	shade#=0
	gameTimer()
	Repeat
		gameTimer()
		tim=tim +gameSpeed

		shuddertim=tim
		If Shuddertim>3000 Then shuddertim=3000
		factor#=3000-shuddertim

		For lo=1 To 25
			xPos#=(Rnd(30.0)-Rnd(20.0) * factor) /3000
			yPos#=(Rnd(30.0)-Rnd(20.0) * factor) /3000
			PositionEntity logoG(lo),xPos,yPos,100
		Next
		
		RenderWorld

		If tim>3000
			shade=shade +(gameSpeed*.1)
			If shade>255 Then shade=255
			Color shade,shade,shade
			Text 512,600,"Space Corps: Armageddon",1,1
		EndIf
		Flip 0
	Until tim>4500

	For lo=1 To 25
		FreeEntity logoG(lo)
	Next
	FreeBrush logo
	Cls
	FlushKeys
End Function
Function begin()
	StopChannel musicChannel
	For id=1 To shipsQ
		If gfx(id)>0
			FreeEntity gfx(id)
			gfx(id)=0
		EndIf
		hp(id)=-1
	Next
	
	For id=1 To weaponsQ
		weapLife(id)=0
		HideEntity weapGfx(id)
	Next
	
	SeedRnd MilliSecs()
	playerShipHull=25
	redSpawnSpeed=5000
	blueSpawnSpeed=5000
	redShips=99
	blueShips=99
	initShip(1,playerShipHull,1) ;make a ship
	initShip(2,playerShipHull-5,2) ;make an enemy
	ai(1)=-1
	x(1)=Rnd(40)-Rnd(40)
	y(1)=-65
	f(1)=Rnd(90,270)
	x(2)=Rnd(40)-Rnd(40)
	y(2)=65
End Function
