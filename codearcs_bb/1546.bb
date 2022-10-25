; ID: 1546
; Author: Techlord
; Date: 2005-11-29 16:27:23
; Title: c e l l u l a r
; Description: c e l l u l a r - 3D Game

;c e l l u l a r
;by frankie 'techlord' taylor
Const LEVEL_COLLISIONTYPE_NONE=0
Const LEVEL_COLLISIONTYPE_LEVEL=1
Const LEVEL_COLLISIONTYPE_PLAYER=2
Const LEVEL_COLLISIONTYPE_BLAST=3
Const LEVEL_COLLISIONTYPE_POW=4
Const LEVEL_COLLISIONTYPE_CELL=5
Const LEVEL_COLLISIONTYPE_VIRUS=6
Const LEVEL_COLLISIONMETHOD_ELLIPSOID=1
Const LEVEL_COLLISIONMETHOD_POLYGON=2
Const LEVEL_COLLISIONMETHOD_BOX=3
Const LEVEL_COLLISIONRESPONSE_STOP=1
Const LEVEL_COLLISIONRESPONSE_SLIDE=2
Const LEVEL_COLLISIONRESPONSE_SLIDENOSLOPE=3
Const BLAST_STATE_ALIVE=1
Const BLAST_STATE_DETONATE=2 
Const CELL_STATE_DEAD=0
Const CELL_STATE_HEALTHY=1
Const CELL_STATE_REPLICATE=2
Const CELL_STATE_REPLICATING=3
Const CELL_STATE_INFECTED=4
Const CELL_STATE_INFECTION=5
Const CELL_STATE_HEALING=6
Const CELL_STATE_DYING=7	
Const VIRUS_STATE_ALIVE=1
Const VIRUS_STATE_TARGETING=2
Const VIRUS_STATE_PENETRATE=3
Const VIRUS_STATE_REPLICATE=4
Const VIRUS_STATE_REPLICATING=5
Const VIRUS_STATE_DYING=6
Const VIRUS_STATE_DEAD=7
Const PARTICLE_STATE_DEAD=0
Const PARTICLE_STATE_ALIVE=1
Const POW_STATE_SPAWNED=1
Const POW_STATE_PICKEDUP=2

Global player.player
Global player_WobbleViewScreen
Global underw_a
Global blaster_ID.blaster[4]
Global cellular_ID.cellular[4]
Global viral_ID.viral[4]
Global powerup_ID.powerup[4]
Global reactor_ID.reactor[4]
Global levelcells

Dim blasterentity(16)
Dim cellularentity(16)
Dim viralentity(16)

Type player
	Field id
	Field typeid
	Field entity%
	Field ship[3]	
	Field blaster1
	Field blaster2
	Field r,g,b
	Field x#,y#,z#
	Field xv#,yv#,zv#
	Field xa#,ya#,za#	
	Field pitch#
	Field yaw#
	Field speed#	; Current
	Field destx#
	Field destz#
	Field destpitch#
	Field destyaw#	; Destination
	Field keymap%[10]
	Field health
	Field booster
	Field score
	Field xradius#
	Field yradius#
	Field state
End Type

Type blaster
	Field id
	Field entity
	Field range#
	Field speed#
	Field xradius#
	Field yradius#	
	Field power	
	Field count
End Type

Type blast
	Field blasterid
	Field entity
	Field range#
	Field speed#
	Field power
	Field state
End Type

Type reactor
	Field id 
	Field entity%
	Field r,g,b
	Field life#
	Field fade#
	Field size#
	Field weight#
End Type
	
Type particle
	Field reactorid
	Field entity%
	Field x#,y#,z#
	Field vx#,vy#,vz#
	Field r,g,b,a#
	Field life#
	Field fade#
	Field size#
	Field weight#
	Field state
End Type

Type powerup
	Field id
	Field entity
	Field effect
	Field xradius#
	Field yradius#
	Field r,g,b,a#
	Field life
End Type

Type pow
	Field powerupid
	Field entity
	Field effect
	Field r,g,b,a#
	Field life
	Field state
End Type

Type cellular
	Field id
	Field entity
	Field health
	Field point
	Field r,g,b
	Field replicatespeed%
	Field replicacount%
	Field replicaincubation%
	Field replicatebehavior
	Field healspeed#
	Field xradius#
	Field yradius#	
End Type

Type cell
	Field cellularid
	Field entity
	Field health
	Field r,g,b,a#
	Field replicatespeed%
	Field replicacount%
	Field replicaincubation%
	Field replicatebehavior
	Field healspeed#
	Field state			
End Type 

Type viral
	Field id
	Field entity
	Field point
	Field health
	Field speed#
	Field vmax#
	Field amax#
	Field xradius#
	Field yradius#		
	Field replicatespeed%
	Field replicacount%
	Field replicaincubation%
	Field replicatebehavior	
	Field replicatehealth%	
	Field r,g,b
End Type 

Type virus
	Field viralid
	Field entity
	Field health
	Field speed#
	Field x#,y#,z#
	Field vx#,vy#,vz#
	Field ax#,ay#,az#
	Field vmax#
	Field amax#	
	Field replicatespeed%
	Field replicacount%
	Field replicaincubation%
	Field replicatebehavior		
	Field replicatehealth%		
	Field target
	Field r,g,b
	Field state
End Type

Type wave
	Field id
	Field title$
	Field virustype
	Field viruses
	Field celltype			
End Type	

Function levelStart()
	levellight=CreateLight()
	RotateEntity(levellight,90,0,0)
	Collisions(LEVEL_COLLISIONTYPE_PLAYER,LEVEL_COLLISIONTYPE_VIRUS,LEVEL_COLLISIONMETHOD_ELLIPSOID,LEVEL_COLLISIONRESPONSE_STOP)
	Collisions(LEVEL_COLLISIONTYPE_CELL,LEVEL_COLLISIONTYPE_VIRUS,LEVEL_COLLISIONMETHOD_ELLIPSOID,LEVEL_COLLISIONRESPONSE_STOP)
	Collisions(LEVEL_COLLISIONTYPE_PLAYER,LEVEL_COLLISIONTYPE_POW,LEVEL_COLLISIONMETHOD_ELLIPSOID,LEVEL_COLLISIONRESPONSE_STOP)
	Collisions(LEVEL_COLLISIONTYPE_BLAST,LEVEL_COLLISIONTYPE_CELL,LEVEL_COLLISIONMETHOD_ELLIPSOID,LEVEL_COLLISIONRESPONSE_STOP)
	Collisions(LEVEL_COLLISIONTYPE_BLAST,LEVEL_COLLISIONTYPE_VIRUS,LEVEL_COLLISIONMETHOD_ELLIPSOID,LEVEL_COLLISIONRESPONSE_STOP)
	Collisions(LEVEL_COLLISIONTYPE_VIRUS,LEVEL_COLLISIONTYPE_BLAST,LEVEL_COLLISIONMETHOD_ELLIPSOID,LEVEL_COLLISIONRESPONSE_STOP)
	Collisions(LEVEL_COLLISIONTYPE_VIRUS,LEVEL_COLLISIONTYPE_CELL,LEVEL_COLLISIONMETHOD_ELLIPSOID,LEVEL_COLLISIONRESPONSE_STOP)
End Function

Function waveStart()
	;wave 1
	this.wave=New wave
	this\title$="Protect the Red Blood Cells"
	this\virustype=1
	this\viruses=100
	this\celltype=1
End Function

Function playerStart()
	HidePointer()
	this.player=New player
	this\r=130
	this\speed#=1	
	this\health=100
	this\blaster1=1
	this\blaster2=3
	this\booster=50		
	this\entity=CreateCamera()
	EntityType(this\entity,LEVEL_COLLISIONTYPE_PLAYER)
	EntityRadius(this\entity,1)	
	CreateListener(this\entity%)
	playerkeymap(this,6)
	playertargetpivot=CreatePivot(this\entity)
	TranslateEntity(playertargetpivot,0,0,50)
	this\ship[1]=CreateCone(4,False,this\entity)
	ScaleMesh(this\ship[1],.50,1,.75)
	RotateMesh(this\ship[1],90,0,0)
	PositionEntity(this\ship[1],-1,-1,.5)
	PointEntity(this\ship[1],playertargetpivot)	
	this\ship[2]=CreateCone(4,False,this\entity)
	ScaleMesh(this\ship[2],.50,1,.75)
	RotateMesh(this\ship[2],90,0,0)
	PositionEntity(this\ship[2],1,-1,.5)
	PointEntity(this\ship[2],playertargetpivot)	
	PositionEntity(this\entity%,0,0,-35)
	player=this 
End Function

Function playerUpdate()
	For this.player = Each player
		playercontrol(this)
		powentity=EntityCollided(this\entity,LEVEL_COLLISIONTYPE_POW)
		For pow.pow = Each pow
			If pow\entity=powentity 
				pow\state=POW_STATE_PICKEDUP
				this\r=pow\r
				this\g=pow\g
				this\b=pow\b
				this\score=this\score+100
			EndIf	
		Next
	Next 
End Function

Function playerControl(this.player)
	;Enhanced Player control by jfk EO-11110
	this\r#=this\r+(Sgn(130-this\r)*5)
	this\g#=this\g+(Sgn(0-this\g)*5)
	this\b#=this\b+(Sgn(0-this\b)*5)
	CameraClsColor(this\entity%,this\r,this\g,this\b)
	
	; Mouse x and y speed
	mxs#=-MouseXSpeed()/4.0
	mys#=MouseYSpeed()/4.0
	; Mouse shake (total mouse movement)
	mouse_shake=Abs(((mxs#+mys#)/2)/1000.0)
	; Destination camera angle x and y values
	this\destyaw#=this\destyaw#-mxs#
	this\destpitch#=this\destpitch#+mys#
	; Current camera angle x and y values
	this\yaw#=this\yaw#+((this\destyaw#-this\yaw#)/2.5)
	this\pitch#=this\pitch#+((this\destpitch#-this\pitch#)/2.5)
	RotateEntity this\entity%,this\pitch#,this\yaw#,1
	; Rest mouse position to centre of screen
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
	;z#=.3
	If KeyDown(this\keymap[1]) x#=this\speed#*-1.
	If KeyDown(this\keymap[2]) x#=this\speed#
	If KeyDown(this\keymap[3]) z#=this\speed#*-1.
	If KeyDown(this\keymap[4]) z#=this\speed#
	If KeyDown(this\keymap[5]) TurnEntity(this\entity%,0,0,-1.)
	If KeyDown(this\keymap[6]) TurnEntity(this\entity%,0,0,1.)
	; Move camera using movement values
	MoveEntity this\entity%,x#,y#,z#

	If MouseHit(1) blastSpawn(this\blaster1)
	If MouseHit(2) 
		If this\blaster2
			playersmartbomb(this);super weapon
			this\r=255
			this\g=130
			this\b=60
			this\blaster2=this\blaster2-1
		EndIf
	EndIf
End Function

Function playersmartbomb(this.player)
	For virus.virus = Each virus
		If virus\state=VIRUS_STATE_ACTIVE
			For virusloop = 1 To 16
				particleSpawn(1,virus\entity)
			Next
			virus\state=VIRUS_STATE_DEAD
		EndIf			
	Next
End Function

Function playerkeymap(this.player,keys%)
	Restore playerkeymapdata
	For loop = 1 To keys%
		Read key%
		this\keymap[loop]=key%
	Next	
End Function

Function playerCursor()
	;Rect(GraphicsWidth()/2-4,GraphicsHeight()/2-4,8,8,0)
	width=GraphicsWidth()/2
	height=GraphicsHeight()/2
	;Line(width-4,height-4,width+4,height+4)
	;Line(width+4,height-4,width-4,height+4)
Oval(width-8,height-8,16,16,False)
	
End Function

Function playerWobbleView()
	;by jfk
	gw#=GraphicsWidth()
	gh#=GraphicsHeight()
	CopyRect(0,0,gw,gh,0,0,BackBuffer(),ImageBuffer(player_WobbleViewScreen))
	underw_a=(underw_a+4)
	steph#=gh/32
	mu8#=gh/60
	If underw_a>359 Then underw_a=0
	For iif#=0 To gh-4  Step .001
		wsin#=(Sin((underw_a+iif)Mod 360.0)*mu8#)
		CopyRect(0,iif,gw,steph+4, 0,iif+wsin#, ImageBuffer(player_WobbleViewScreen),BackBuffer())
		iif=iif+steph
	Next
End Function

Function blastStart()
	;standard burst
	blaster.blaster = New blaster
	blaster\id=1
	blaster_ID[blaster\id]=blaster
	blaster\speed=2
	blaster\range=25
	blaster\entity=CreateCone(4,True)
	blaster\power=1
	blaster\xradius#=.5
	blaster\yradius#=1
	ScaleMesh(blaster\entity,.1,.75,.1)
	RotateMesh(blaster\entity,270,0,0)
	EntityFX(blaster\entity,13)
	EntityBlend(blaster\entity,3)
	HideEntity(blaster\entity)
End Function

Function blastSpawn.blast(blasterid)
	;blaster ship 1
	this.blast=New blast
	this\blasterid=blasterid
	blaster.blaster=blaster_ID[this\blasterid]
	this\entity=CopyEntity(blaster\entity)
	this\speed=blaster\speed
	this\range=blaster\range
	this\power=blaster\power
	this\state=BLAST_STATE_ALIVE
	EntityType(this\entity,LEVEL_COLLISIONTYPE_BLAST)
	EntityRadius(this\entity,blaster\xradius#,blaster\yradius#)		
	PositionEntity(this\entity,EntityX(player\ship[1],True),EntityY(player\ship[1],True),EntityZ(player\ship[1],True))
	RotateEntity(this\entity,EntityPitch(player\ship[1],True),EntityYaw(player\ship[1],True),EntityRoll(player\ship[1],True))
	;blaster ship 2
	this.blast=New blast
	this\blasterid=blasterid
	blaster.blaster=blaster_ID[this\blasterid]
	this\entity=CopyEntity(blaster\entity)
	this\speed=blaster\speed
	this\range=blaster\range
	this\power=blaster\power
	this\state=BLAST_STATE_ALIVE
	EntityType(this\entity,LEVEL_COLLISIONTYPE_BLAST)
	EntityRadius(this\entity,blaster\xradius#,blaster\yradius#)		
	PositionEntity(this\entity,EntityX(player\ship[2],True),EntityY(player\ship[2],True),EntityZ(player\ship[2],True))
	RotateEntity(this\entity,EntityPitch(player\ship[2],True),EntityYaw(player\ship[2],True),EntityRoll(player\ship[2],True))
	
	;aligntovector
	Return this
End Function

Function blastUpdate()
	For this.blast = Each blast
		Select this\state
			Case BLAST_STATE_ALIVE
				MoveEntity(this\entity,0,0,this\speed#)
				TurnEntity(this\entity,0,0,2)
				If EntityCollided(this\entity,LEVEL_COLLISIONTYPE_VIRUS) this\state=BLAST_STATE_DETONATE
				this\range=this\range-1
				If this\range<0 this\state=BLAST_STATE_DETONATE 
			Case BLAST_STATE_DETONATE
				FreeEntity(this\entity)
				Delete this
		End Select
	Next
End Function

Function particleStart()
	;particle bits
	reactor.reactor=New reactor
	reactor\id=1
	reactor\r=255
	reactor\g=255
	reactor\b=0
	reactor\life=32
	reactor\fade#=1
	reactor\size#=1	
	reactor_ID[reactor\id]=reactor
	reactor\entity=CreateCone(3,True);
	ScaleMesh(reactor\entity,.1,.2,.1)
	EntityBlend(reactor\entity,3)
	EntityColor(reactor\entity,0,255,0)
	HideEntity(reactor\entity)	
End Function

Function particleSpawn.particle(reactorid,particleparent)
	this.particle=New particle
	this\reactorid=reactorid
	reactor.reactor=reactor_ID[this\reactorid]
	this\entity=CopyEntity(reactor\entity)
	this\life=reactor\life
	this\a#=1
	this\state=PARTICLE_STATE_ALIVE
	PositionEntity(this\entity,EntityX(particleparent,True),EntityY(particleparent,True),EntityZ(particleparent,True))
	RotateEntity(this\entity,Rand(360),Rand(360),Rand(360))
	Return this
End Function

Function particleUpdate()
	For this.particle = Each particle
		Select this\state
			Case PARTICLE_STATE_ALIVE
				this\life=this\life-1
				If Not this\life this\state=PARTICLE_STATE_DEAD
				MoveEntity(this\entity,0,0,.3)
				this\a#=this\a#-(1/this\life)
				EntityAlpha(this\entity,this\a#)
				;TurnEntity(this\entity,5,0,0)
			Case PARTICLE_STATE_DEAD
				FreeEntity(this\entity)
				Delete this
		End Select
	Next
End Function

Function powStart()
	;blaster upgrade 1
	powerup.powerup=New powerup
	powerup\id=1
	powerup_ID[powerup\id]=powerup
	powerup\effect=1
	powerup\life=500
	powerup\g=255
	powerup\b=255
	powerup\a#=1	
	powerup\entity=CreateCube()
	ScaleEntity(powerup\entity,.5,.5,.5)
	EntityColor(powerup\entity,powerup\r,powerup\g,powerup\b)
	EntityBlend(powerup\entity,3)	
	powerup\xradius#=.5
	powerup\yradius#=.5
	HideEntity(powerup\entity) 
	
	;blaster upgrade 2
	powerup.powerup=New powerup
	powerup\id=2
	powerup_ID[powerup\id]=powerup
	powerup\effect=2
	powerup\life=500	
	powerup\r=255
	powerup\g=255
	powerup\a#=1	
	powerup\entity=CreateCube()
	ScaleEntity(powerup\entity,.5,.5,.5)
	EntityColor(powerup\entity,powerup\r,powerup\g,powerup\b)
	EntityBlend(powerup\entity,3)
	powerup\xradius#=.5
	powerup\yradius#=.5
	HideEntity(powerup\entity) 	

	;blaster upgrade 3
	powerup.powerup=New powerup
	powerup\id=3
	powerup_ID[powerup\id]=powerup
	powerup\effect=3
	powerup\life=500	
	powerup\b=255
	powerup\a#=1	
	powerup\entity=CreateCube()
	ScaleEntity(powerup\entity,.5,.5,.5)
	EntityColor(powerup\entity,powerup\r,powerup\g,powerup\b)
	EntityBlend(powerup\entity,3)	
	powerup\xradius#=.5
	powerup\yradius#=.5
	HideEntity(powerup\entity) 
		
	;blaster upgrade 4
	powerup.powerup=New powerup
	powerup\id=4
	powerup_ID[powerup\id]=powerup
	powerup\effect=4
	powerup\life=500	
	powerup\r=255
	powerup\g=0
	powerup\b=255
	powerup\a#=1	
	powerup\entity=CreateCube()
	ScaleEntity(powerup\entity,.5,.5,.5)
	EntityColor(powerup\entity,powerup\r,powerup\g,powerup\b)
	EntityBlend(powerup\entity,3)	
	powerup\xradius#=.5
	powerup\yradius#=.5
	HideEntity(powerup\entity) 	
				
End Function

Function powUpdate()
	If Rand(1000)=True powSpawn(Rand(4))
	For this.pow = Each pow
		powerup.powerup=powerup_ID[this\powerupid]
		Select this\state
			Case POW_STATE_SPAWNED
				TurnEntity(this\entity,4,8,16)
				this\life=this\life-1
				If Not this\life this\state=POW_STATE_DEAD	
			Case POW_STATE_PICKEDUP
				EntityType(this\entity,LEVEL_COLLISIONTYPE_NONE)
				Select this\effect
					Case 1 playerSmartBomb(player)
					Case 2 player\blaster2=player\blaster2+1
					Case 3 player\score=player\score+5000
					Case 4 For powloop = 1 To 3 cellSpawn(1) Next
				End Select
				this\state=POW_STATE_DEAD
			Case POW_STATE_DEAD
				FreeEntity(this\entity)
				Delete this
		End Select
	Next
End Function

Function powSpawn.pow(powerupid,powerupparent=0)
	this.pow = New pow
	this\powerupid=powerupid
	powerup.powerup=powerup_ID[powerupid]
	this\effect=powerup\effect
	this\entity=CopyEntity(powerup\entity)
	EntityType(this\entity,LEVEL_COLLISIONTYPE_POW)
	EntityRadius(this\entity,powerup\xradius,powerup\yradius)	
	this\r=powerup\r
	this\g=powerup\g
	this\b=powerup\b
	this\a=powerup\a
	this\life=powerup\life	
	EntityColor(this\entity,this\r,this\g,this\b)
	EntityShininess(this\entity,.9)
	this\state=POW_STATE_SPAWNED
	If Not powerupparent
		PositionEntity(this\entity,Rand(-25,25),Rand(-25,25),Rand(-25,25))	
		RotateEntity(this\entity,Rand(359),Rand(359),Rand(359))
	Else
		PositionEntity(this\entity,EntityX(powerupparent),EntityY(powerupparent),EntityZ(powerupparent))	
		RotateEntity(this\entity,EntityX(powerupparent),EntityY(powerupparent),EntityZ(powerupparent))		
	EndIf
	Return this	
End Function

Function cellStart()
	;blood cell
	cellular.cellular=New cellular
	cellular\id=1
	cellular_ID[cellular\id]=cellular
	cellular\health=400
	cellular\point=100
	cellular\replicatespeed#=2000
	cellular\replicacount%=1
	cellular\replicaincubation#=0
	cellular\replicatebehavior=1	
	cellular\entity=cellTorusCreate(1,1,32,32,0);blood cell
	ScaleMesh(cellular\entity,1,1,.5)
	cellular\xradius=1
	cellular\yradius=.25
	cellular\r=127
	EntityColor(cellular\entity,cellular\r,cellular\g,cellular\b)
	HideEntity(cellular\entity)

	;white cell
	cellular.cellular=New cellular
	cellular\id=2
	cellular_ID[cellular\id]=cellular
	cellular\entity=cellTorusCreate(1,1,32,32,0);blood cell
	ScaleMesh(cellular\entity,1,1,.5)
	cellular\xradius=1
	cellular\yradius=.25	
	cellular\r=234
	cellular\g=255
	cellular\b=154	
	EntityColor(cellular\entity,cellular\r,cellular\g,cellular\b)
	HideEntity(cellular\entity)

	;brain cell	
End Function

Function cellSpawn.cell(cellularid,cellularparent=0)
	this.cell = New cell
	this\cellularid=cellularid
	cellular.cellular=cellular_ID[cellularid]
	this\health=cellular\health
	this\replicatespeed=cellular\replicatespeed#
	this\replicaincubation=cellular\replicaincubation#
	this\replicatebehavior=cellular\replicatebehavior	
	this\entity=CopyEntity(cellular\entity)
	this\r=cellular\r
	this\g=cellular\g
	this\b=cellular\b
	this\a#=1	
	EntityColor(this\entity,this\r,this\g,this\b)
	EntityShininess(this\entity,.9)
	EntityType(this\entity,LEVEL_COLLISIONTYPE_CELL)
	EntityRadius(this\entity,cellular\xradius,cellular\yradius)	
	this\state=CELL_STATE_HEALTHY
	If Not cellularparent
		PositionEntity(this\entity,Rand(-25,25),Rand(-25,25),Rand(-25,25))	
		RotateEntity(this\entity,Rand(359),Rand(359),Rand(359))
	Else
		PositionEntity(this\entity,EntityX(cellularparent),EntityY(cellularparent),EntityZ(cellularparent))	
		RotateEntity(this\entity,EntityX(cellularparent),EntityY(cellularparent),EntityZ(cellularparent))		
	EndIf
	Return this	
End Function

Function cellUpdate()
	levelcells=0
	For this.cell = Each cell
		cellular.cellular=cellular_ID[this\cellularid]
		Select this\state
			Case CELL_STATE_HEALTHY
				TurnEntity(this\entity,1,2,3)
				MoveEntity(this\entity,.01,.01,.01)
				If EntityCollided(this\entity,LEVEL_COLLISIONTYPE_VIRUS) 
					this\state=CELL_STATE_INFECTED
					EntityShininess(this\entity,0)
				EndIf	
				this\replicatespeed=this\replicatespeed-1
				If Not this\replicatespeed this\state=CELL_STATE_REPLICATE
				levelcells=levelcells+1
			Case CELL_STATE_HEALING
				levelcells=levelcells+1	
			Case CELL_STATE_REPLICATE
				this\state=CELL_STATE_REPLICATING	
				levelcells=levelcells+1
			Case CELL_STATE_REPLICATING
				cellSpawn(this\cellularid)
				player\score=player\score+cellular\point
				this\replicatespeed=cellular\replicatespeed
				this\state=CELL_STATE_HEALTHY	
				player\r=255			
				levelcells=levelcells+1	
			Case CELL_STATE_INFECTED
				EntityType(this\entity,LEVEL_COLLISIONTYPE_NONE) 
				this\r#=this\r+(Sgn(0-this\r)*7)
				this\g#=this\g+(Sgn(255-this\g)*7)
				this\b#=this\b+(Sgn(0-this\b)*7)
				EntityColor(this\entity,this\r,this\g,this\b)
				this\health=this\health-1
				If Not this\health this\state=CELL_STATE_INFECTION
			Case CELL_STATE_INFECTION		
				this\health=this\health-1
				If this\health=-10 this\state=CELL_STATE_DYING
				this\a#=this\a#-.1
				EntityAlpha(this\entity,this\a#)
			Case CELL_STATE_DYING
				EntityType(this\entity,LEVEL_COLLISIONTYPE_NONE) 
				this\r#=this\r+(Sgn(0-this\r)*15)
				this\g#=this\g+(Sgn(0-this\g)*15)
				this\b#=this\b+(Sgn(0-this\b)*15)
				EntityColor(this\entity,this\r,this\g,this\b)
				If this\r+this\g+this\b<10 this\state=CELL_STATE_DEAD				
			Case CELL_STATE_DEAD
				FreeEntity(this\entity)
				Delete this				
		End Select
	Next
End Function

Function cellTorusCreate(torrad#,torwidth#,segments,sides,parent=0)
	;by Philip Merwarth
	torusmesh=CreateMesh(parent)
	surf=CreateSurface(torusmesh)
	FATSTEP#=360.0/sides
	DEGSTEP#=360.0/segments
	radius#=0
	x#=0
	y#=0
	z#=0
	fat#=0 
	Repeat
		radius = torrad + (torwidth)*Sin(fat)
		deg#=0
		z=torwidth*Cos(fat)
		Repeat
			x=radius*Cos(deg)
			y=radius*Sin(deg)
			AddVertex surf,x,y,z,x,y,z			
			deg=deg+DEGSTEP	
		Until deg>=360
		fat=fat+FATSTEP
	Until fat>=360
	For vert=0 To segments*sides-1
		v0=vert
		v1=vert+segments
		v2=vert+1
		v3=vert+1+segments
		
		If v1>=(segments*sides) Then v1=v1-(segments*sides)
		If v2>=(segments*sides) Then v2=v2-(segments*sides)
		If v3>=(segments*sides) Then v3=v3-(segments*sides)
		
		AddTriangle surf,v0,v1,v2
		AddTriangle surf,v1,v3,v2	
	Next
	UpdateNormals torusmesh
	Return torusmesh
End Function

Function virusStart()
	;andromeda minor
	viral.viral=New viral
	viral\id=1
	viral_ID[viral\id]=viral
	viral\g=255
	viral\health=1
	viral\point=50
	viral\vmax=.2
	viral\amax=.8		
	viral\replicatespeed%=16
	viral\replicacount%=3
	viral\replicaincubation%=256
	viral\replicatebehavior=1
	viralentityres=2
	viralentityxscale#=.25
	viralentityyscale#=1
	viralentityzscale#=.25		
	viral\entity=CreateSphere(viralentityres)
	ScaleMesh(viral\entity,viralentityxscale,viralentityyscale,viralentityzscale)
	viruscopy1=CreateSphere(viralentityres)
	ScaleMesh(viruscopy1,viralentityxscale,viralentityyscale,viralentityzscale)
	RotateMesh(viruscopy1,90,0,0)
	viruscopy2=CreateSphere(viralentityres)
	ScaleMesh(viruscopy2,viralentityxscale,viralentityyscale,viralentityzscale)
	RotateMesh(viruscopy2,0,0,90)	
	AddMesh(viruscopy1,viral\entity)
	AddMesh(viruscopy2,viral\entity)
	viral\xradius=.75
	EntityColor(viral\entity,viral\r,viral\g,viral\b)
	HideEntity(viral\entity)
	FreeEntity(viruscopy1)
	FreeEntity(viruscopy2)

	;andromeda major	
		
	;nemisis
	
	;t-strain
	
End Function

Function virusSpawn(viralid,virusparententity=0)
	this.virus=New virus
	this\viralid=viralid
	viral.viral=viral_ID[this\viralid]
	this\entity=CopyEntity(viral\entity)
	EntityType(this\entity,LEVEL_COLLISIONTYPE_VIRUS)
	EntityRadius(this\entity,viral\xradius)	
	this\health=viral\health
	this\vmax=viral\vmax
	this\amax=viral\amax
	this\replicatespeed=viral\replicatespeed%
	this\replicaincubation=viral\replicaincubation%
	this\replicatebehavior=viral\replicatebehavior	
	this\state=VIRUS_STATE_TARGETING
	If Not virusparententity
		this\x=Rand(40,50)* (Rand(-1,1)Or 1)  
		this\y=Rand(40,50)* (Rand(-1,1)Or 1)
		this\z=Rand(40,50)* (Rand(-1,1)Or 1)
	Else
		this\x=EntityX(virusparententity)
		this\y=EntityY(virusparententity)
		this\z=EntityZ(virusparententity)	
	EndIf	
	PositionEntity(this\entity,this\x,this\y,this\z)
End Function

Function virusUpdate()

	If Rand(200)=True virusSpawn(1)
	
	For this.virus = Each virus
		viral.viral=viral_ID[this\viralid]
		Select this\state
			Case VIRUS_STATE_TARGETING 
				For cell.cell =  Each cell
					If Rand(10)=True And cell\state=CELL_STATE_HEALTHY 
						this\target=cell\entity
						this\state=VIRUS_STATE_ACTIVE
					EndIf
				Next
				
				;vibrate
				virusvibe#=.05
				PositionEntity(this\entity,this\x+Rnd(-virusvibe,virusvibe),this\y+Rnd(-virusvibe,virusvibe),this\z+Rnd(-virusvibe,virusvibe))						
			
			Case VIRUS_STATE_ACTIVE
				;reacquire target based on ai skill
				For cell.cell =  Each cell
					If this\target=cell\entity And cell\state<>CELL_STATE_HEALTHY  
						this\state=VIRUS_STATE_TARGETING
						Exit
					EndIf
				Next
							
				;Homing example, by Jeppe Nielsen 2003 
				dx#=(EntityX(this\target)-this\x)
				dy#=(EntityY(this\target)-this\y)
				dz#=(EntityZ(this\target)-this\z)
					
				l#=Sqr(dx#^2+dy#^2+dz#^2)
				
				dx#=(dx#/l#)*this\amax#
				dy#=(dy#/l#)*this\amax#
				dz#=(dz#/l#)*this\amax#
				
				;if close enough escape target
				If l#<=1;distance#
					dx#=-dx#
					dy#=-dy#
					dz#=-dz#
				EndIf
				
				;check against all other enemies, to avoid them
				;For virus.virus = Each virus
				;	If virus<>this
				;		
				;		dex#=(this\x-virus\x)
				;		dey#=(this\y-virus\y)
				;		dez#=(this\z-virus\z)
				;		
				;		l#=Sqr(dex#^2+dey#^2+dez#^2)
				;		
				;		dxx#=dxx#+(dex#/l#)*this\amax#
				;		dyy#=dyy#+(dey#/l#)*this\amax#
				;		dzz#=dzz#+(dez#/l#)*this\amax#
				;		
				;		co=co+1
				;	EndIf
				;Next
				
				If co
				
				dxx#=dxx#/Float(co)
				dyy#=dyy#/Float(co)
				dyz#=dyz#/Float(co)
				
				EndIf
				
				dx#=(dx#+dxx#)/2
				dy#=(dy#+dyy#)/2
				dz#=(dz#+dzz#)/2
				
				this\ax#=this\ax#+dx#
				this\ay#=this\ay#+dy#
				this\az#=this\az#+dz#
				
				acc#=Sqr(this\ax#^2+this\ay#^2+this\az#^2)
				
				;Check if current acceleration is more than allowed
				If acc#>this\amax#
					this\ax#=(this\ax#/acc#)*this\amax
					this\ay#=(this\ay#/acc#)*this\amax
					this\az#=(this\az#/acc#)*this\amax
				EndIf
				
				this\vx#=this\vx#+this\ax#
				this\vy#=this\vy#+this\ay#
				this\vz#=this\vz#+this\az#
				
				vel#=Sqr(this\vx#^2+this\vy#^2+this\vz#^2)
				
				;Check if current velocity is more than allowed
				If vel#>this\vmax#
					this\vx#=(this\vx#/vel#)*this\vmax
					this\vy#=(this\vy#/vel#)*this\vmax
					this\vz#=(this\vz#/vel#)*this\vmax
				EndIf
				
				; add velocity to position
				this\x#=this\x#+this\vx#
				this\y#=this\y#+this\vy#			
				this\z#=this\z#+this\vz#
				
				PositionEntity(this\entity,this\x,this\y,this\z) 
				TurnEntity(this\entity,4,8,16) 
				
				blastentity=EntityCollided(this\entity,LEVEL_COLLISIONTYPE_BLAST)
				If blastentity this\health=this\health-1
				
				If Not this\health
					For virusloop = 1 To 16
						particleSpawn(1,this\entity)
					Next
					this\state=VIRUS_STATE_DEAD
				EndIf
				
				cellentity=EntityCollided(this\entity,LEVEL_COLLISIONTYPE_CELL)
				If cellentity
					EntityType(this\entity,LEVEL_COLLISIONTYPE_NONE) 
					this\target=cellentity
					this\state=VIRUS_STATE_PENETRATE
				EndIf	
	
			Case VIRUS_STATE_PENETRATE
				this\x#=EntityX(this\target);this\x+(Sgn(EntityX(this\target)-this\x)*.01);
				this\y#=EntityY(this\target);this\y+(Sgn(EntityY(this\target)-this\y)*.01);
				this\z#=EntityZ(this\target);this\z+(Sgn(EntityZ(this\target)-this\z)*.01);
				PositionEntity(this\entity,this\x,this\y,this\z) 
				;If this\x+this\y#+this\z#<.10 
					this\state=VIRUS_STATE_REPLICATE
				;EndIf
			Case VIRUS_STATE_REPLICATE
				;vibrate
				virusvibe#=.05
				PositionEntity(this\entity,this\x+Rnd(-virusvibe,virusvibe),this\y+Rnd(-virusvibe,virusvibe),this\z+Rnd(-virusvibe,virusvibe))						
				this\replicaincubation=this\replicaincubation-1
				If Not this\replicaincubation
					this\replicaincubation=viral\replicaincubation
					;EntityColor(this\entity,255,0,0)
					this\state=VIRUS_STATE_REPLICATING
				EndIf	
				
			Case VIRUS_STATE_REPLICATING
				Select this\replicatebehavior
					Case 1; replicate once
						this\replicatespeed=this\replicatespeed-1
						If Not this\replicatespeed
							this\replicacount=this\replicacount+1
							virusspawn(this\viralid,this\entity)
							If this\replicacount=viral\replicacount
								this\replicacount=0
								this\state=VIRUS_STATE_DYING
							EndIf	
							this\replicatespeed=viral\replicatespeed;reset speed
						EndIf

				End Select
				 
			Case VIRUS_STATE_DYING
				this\r#=this\r+(Sgn(0-this\r)*1)
				this\g#=this\g+(Sgn(0-this\g)*1)
				this\b#=this\b+(Sgn(0-this\b)*1)
				EntityColor(this\entity,this\r,this\g,this\b)
				If this\r+this\g+this\b<1 this\state=VIRUS_STATE_DEAD
								
			Case VIRUS_STATE_DEAD
				player\score=player\score+viral\point
				FreeEntity(this\entity)
				Delete this
							
		End Select
	Next 
End Function

.GAME_START
	AppTitle("c e l l u l a r")
	Graphics3D(800,600,32,2)
	SetBuffer(BackBuffer())
	
	Repeat
		Text(GraphicsWidth()/2,0,"c e l l u l a r",1)
		Text(GraphicsWidth()/2,24,"protect living cells",1)
		Text(GraphicsWidth()/2,36,"destroy attacking viruses",1)
		Text(GraphicsWidth()/2,48,"collect protein blocks",1)
		Text(GraphicsWidth()/2,60,"W,A,S,D,Mouse,LMB,RMB",1)
		Text(GraphicsWidth()/2,GraphicsHeight()/2,"press <space> to start",1)
		Flip()
	Until KeyDown(57)

	player_WobbleViewScreen=CreateImage(GraphicsWidth(),GraphicsHeight())
	
	SeedRnd (MilliSecs()) 

	
	levelStart()
	leveltimer=CreateTimer(30)
	playerStart()
	blastStart()
	powStart()
	particleStart()
	cellStart()
	virusStart()
	
	;initial start
	For cellloop = 1 To 12 cellSpawn(1)	Next	
	virusSpawn(1)
	
	audioCD=PlayCDTrack(1,3) 

	FlushKeys()
	FlushMouse()

.GAME_UPDATE
	While Not KeyDown(1)
		WaitTimer(leveltimer)
			
			UpdateWorld()
			RenderWorld()
			playerUpdate()
			blastUpdate()
			powUpdate()
			particleUpdate()
			cellUpdate()
			virusUpdate()

			playerWobbleView()

			If KeyHit(63) Delay(2000) ;debug
			If KeyHit(67) SaveBuffer(FrontBuffer(),"screenshot.bmp")

			playerCursor()
			
			Color(255,255,255)
			Text(GraphicsWidth()/2,0,"score:"+player\score,1)
			Text(GraphicsWidth()/2,12,"cells:"+levelcells,1)
			Text(GraphicsWidth()/2,24,"bombs:"+player\blaster2,1)
			If Not levelcells Text(GraphicsWidth()/2,GraphicsHeight()/2-64,"GAME OVER",1)

	 		VWait			
		Flip()
		If Not levelcells Exit
	Wend

.GAME_STOP
	FlushKeys()
	WaitKey()

	FreeTimer(leveltimer)
	Delete Each player
	Delete Each blaster
	Delete Each blast
	Delete Each powerup
	Delete Each pow
	Delete Each reactor
	Delete Each particle
	Delete Each cellular
	Delete Each cell
	Delete Each viral
	Delete Each virus
	ClearWorld()
	
	Goto GAME_START		
	End


.playerkeymapdata
;    lft rgt rwd fwd lr rr        
;    a   d   s   w  q  e
Data 30,32,31,17,44,18
