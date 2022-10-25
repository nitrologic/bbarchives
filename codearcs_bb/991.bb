; ID: 991
; Author: Danny
; Date: 2004-04-08 06:26:39
; Title: Dynamic Water
; Description: want footsteps creating ripples in a water surface?

; fxWater Module by Danny van der Ark - dendanny@xs4all.nl
;
; (Heavily) inpired by samples by Reda Borchardt and Rob Cummings.
; use as you like - send me a sample would be cool.
;
; april '04
;

;
; Usage:
;
; 1. Create a water surface using: fxWater_Create( name$, xsize, zsize, dampening#, parent )
;    This function returns the 'type-handle' to the water surface wich you will need later.
;
; 2. Obtain the entity handle (to apply color, texture, alpha, etc) using the function
;	 fxWater_get_entity( typehandle) -> typehandle is the value returned by fxWater_Create()
;
; 3. Then simply call 'fxWater_Update()' every frame during your main loop.
;
;
; 4. To create a splah in the water use the fxWater_Dimple() function. The function needs the
;	 type handle that was returned by 'fxWater_Create', the coordinates of the splash, the
;	 force/strength of the splash and the 'range' or size of the splash.
;
;	 fxWater_Dimple( hand, x,z, force#=1.0, range#=1.0)
;
; 5. Freeing stuff not done yet because I want to alter it to use memory banks in stead of
;	 a large static array to store the vertex data - wich should give a more efficient use
;	 of memory...
;
; You can create as many surfaces of any size as you wish. If you wish surfaces larger than 100x100
; then adjust the fxwater_max_depth/width globals. If you want more than 5 surfaces at the same time
; adjust the fxwater_max accordingly. 
; 
; It is necesary To 'UpdateNormals' every frame to ensure the highlights are re-calculated every
; frame. Without this the ripples are there, but just not visible. This slows things down
; dramatically ofcourse. Any other ideas welcome....
;
; He ho, enjoy!
;
; ooo
; (_) Danny v.d. Ark



Global fxWATER_MAX = 5				;max number of active surfaces/effects at one time
Global fxWATER_NUM = 0				;current number of active water effects.

Global FXWATER_MAX_WIDTH = 100		;max sub-division for water meshes
Global FXWATER_MAX_DEPTH = 100		;max sub-division for water meshes

Global FXWATER_MAX_BANKS = fxWATER_MAX * 2
Global FXWATER_NUM_BANKS = 0


Global DEMO_WATER_WIDTH	 = 40 	;--> Adjust these for different plane sizes / mesh resolution
Global DEMO_WATER_DEPTH  = 40


;reserve memory banks for vertice altitudes
Dim fxWaterBank#(FXWATER_MAX_BANKS, FXWATER_MAX_WIDTH, FXWATER_MAX_DEPTH )


Type fxWaterWave

	Field id
	Field name$
	
	Field active		;if not ripples then fx is not actiave
	
	Field parent		;parent entity (if any)
	Field entity		;water mesh
	Field surface		;water surface

	Field bank1			;pointers to array for mem storage
	Field bank2

	Field width			;width of plane / surface
	Field depth			;depth of plane / surface
	Field dampening#	;water dynamics

End Type

;Demo options
Global OPT_WIREFRAME = 0
Global OPT_REFRSH 	 = 0
Global OPT_BALLFREEZE= 0
Global OPT_CEILING	 = 1

;--------------------------------------------------------------------------------------------------

;Graphics3D 640,480,32,1
Graphics3D 640,480,0,2

; Camera + Light
Global campivot = CreatePivot()
Global camera = CreateCamera(campivot)

PositionEntity camera, 0, 0, -35
PointEntity camera, campivot

light = CreateLight(2)
PositionEntity light,-60,0,100
LightColor light,240,240,210
PointEntity light, campivot

light = CreateLight(2)
PositionEntity light,50,0,-50
LightColor light,150,150,180
PointEntity light, campivot

AmbientLight 40,40,40

;load texture
;tex = LoadTexture ("water.tga",1+64)
tex = create_noise_map()

;create water planes
Global w1 = fxWater_Create( "floor",  DEMO_WATER_WIDTH, DEMO_WATER_DEPTH, 0.025 )
water = fxWater_get_entity( w1 )
PositionEntity water, 0, -10, 0
EntityColor water, 30,50,200
EntityShininess water, 0.1
EntityTexture water,tex

Global w2 = fxWater_Create( "ceiling",  DEMO_WATER_WIDTH, DEMO_WATER_DEPTH, 0.025 )
water = fxWater_get_entity( w2 )
FlipMesh water ; flip it because the camera is underneath it
PositionEntity water, 0, 10, 0
EntityColor water, 250,100,100
EntityColor water, 140,130,20
EntityShininess water, 0.1
EntityTexture water,tex

;force refresh
fxWater_dimple( w1, 1,1, 0.001, 0.001)
fxWater_dimple( w2, 1,1, 0.001, 0.001)

;set random ball direction/speed
Global bx#=  0.2
Global by#= -1.75
Global bz#= -0.25

Global bmaxx# =  DEMO_WATER_WIDTH * 0.5	;half the width of the water plane
Global bmaxy# = 10.0
Global bmaxz# =  DEMO_WATER_DEPTH * 0.5	;half the depth of the water plane

;create ball
ball = CreateSphere(6)
EntityColor ball, 0,0,0
EntityShininess ball, 1.0
EntityTexture ball, tex

SeedRnd MilliSecs()

; Main Loop
tstart = MilliSecs() + 1000
frame = 0

Global lastx# = 0
Global lasty# = 0
Global lastz# = 0

tim = CreateTimer(50)

l$ = "WavyWaterFx by Danny van der Ark"

While Not KeyHit(1)

	;update bouncing ball
	update_ball( ball )
	
	;update fxWater
	fxWater_update()
	
	;render
    UpdateWorld
    RenderWorld

	;debug
	Color 000,000,000
	Text 2,1, l$
	Color 255,255,255
	Text 2,0, l$
	Color 100,100,100
	Text 2,12, "based on samples from Reda Borchardt And Rob Cummings."
	Color 20,140,255
	Text 2,454, "[ESC] to quit - [D] for fast but dirty refresh(" + OPT_REFRESH + ") - [W] To toggle WireFrame (" + OPT_WIREFRAME + ")"
	Text 2,466, "[P] To pause  - [B] to pause/reset ball - [C] to toggle ceiling"
	Color 250,250,220
	Text 10,220,"FPS  " + fps
	Text 10,232,"TRIS " + TrisRendered()

	;orbit camera
	;TurnEntity campivot, 0, 0.25, 0

	;check fps
	frame = frame + 1
	If MilliSecs() >= tstart Then
		fps = frame
		frame = 0
		tstart = MilliSecs() + 1000
	EndIf

	;toggle ceiling [C]
	If KeyHit(46) Then
		OPT_CEILING = 1 - OPT_CEILING
		If OPT_CEILING Then
			water = fxWater_Get_Entity(w2)
			ShowEntity water
		Else
			water = fxWater_Get_Entity(w2)
			HideEntity water
		EndIf
	EndIf
	
	;pause all [P]
	If KeyHit(25) Then
		FlushKeys
		While Not KeyHit(25) Wend
	EndIf

	;pause ball [B]
	If KeyHit(48) Then
		OPT_BALLFREEZE = 1 - OPT_BALLFREEZE
		If OPT_BALLFREEZE Then
			PositionEntity ball, 0,0,0
			bx# = 0
			by# = 0
			bz# = 0
		Else
			bx#=Rnd#(-1,1)
			by#=Rnd#(-2,2)
			bz#=Rnd#(-1,1)
		EndIf
	EndIf

	;wireframe [W]
	If KeyHit(17) Then
		OPT_WIREFRAME = 1 - OPT_WIREFRAME
		WireFrame OPT_WIREFRAME
	EndIf
	
	;Dirty refresh [D]
	If KeyHit(32) Then
		OPT_REFRESH = 1 - OPT_REFRESH
	EndIf	

	;check refresh mode
	If OPT_REFRESH Then
		;fast and dirty (pot noodle style)
	    Flip False
	Else
		;slow but clean
		Flip True
		WaitTimer(tim)
	EndIf

	If MouseHit(1) Then
		x = Rnd(1,DEMO_WATER_WIDTH)
		z = Rnd(1,DEMO_WATER_DEPTH)
		fxWater_Dimple( w1, x, z, 1, 5)
	EndIf
Wend

End

;--------------------------------------------------------------------------------------------------

Function update_ball( ent )

	;move the ball
	TranslateEntity ent, bx,by,bz

	DoDimple = False
	
	;check left/right walls
	If EntityX#(ent) >  bmaxx-2 Then bx# = bx# * -1
	If EntityX#(ent) < -bmaxx+2 Then bx# = bx# * -1

	;check ceiling bounce
	If EntityY#(ent) >  bmaxy-2 Then
		;reverse vertical direction
		by# = by# * -1.0
		;create dimple
		fxWater_Dimple(w2, EntityX(ent)+bmaxx,EntityZ(ent)+bmaxz, -1.0, 5.0)
		;slightly alter direction
		bx# = bx# + Rnd#(-0.25, 0.25)
		bz# = bz# + Rnd#(-0.25, 0.25)
	EndIf

	;check floor bounce
	If EntityY#(ent) < -bmaxy+2 Then
		;reverse vertical direction
		by# = by# * -1.0
		;create dimple
		fxWater_Dimple(w1, EntityX(ent)+bmaxx,EntityZ(ent)+bmaxz, 1.0, 5.0)
		;slightly alter direction
		bx# = bx# + Rnd#(-0.25, 0.25)
		bz# = bz# + Rnd#(-0.25, 0.25)
	EndIf

	;check front/back walls
	If EntityZ#(ent) >  bmaxz-2 Then bz# = bz# * -1
	If EntityZ#(ent) < -bmaxz+2 Then bz# = bz# * -1

End Function

;--------------------------------------------------------------------------------------------------

Function fxWater_Create( name$="", width=1, depth=1, damp#=0.01, parent=0 )


	;create new Wavy water effect plane
	w.fxWaterWave = New fxWaterWave

	w\id		= 1
	w\name$		= name$
	
	w\active	= True
	
	;create rectangular grid mesh
	w\width		= width
	w\depth		= depth
	w\dampening#= 1 - damp#
	
	w\parent	= parent
	w\entity	= create_mesh_plane( w\width, w\depth, False, parent )
	w\surface	= GetSurface(w\entity,1)
	
	;store handle for quick retrieval during collision
	NameEntity w\entity, Handle(w)
	
	;reserve memory banks to hold vertex energy
	w\bank1		= fxWater_Create_Buffer()
	w\bank2		= fxWater_Create_Buffer()
	
	;return mesh handle
	Return Handle(w)
	
End Function

;--------------------------------------------------------------------------------------------------

Function fxWater_update()

	For w.fxWaterWave = Each fxWaterWave

		;if the surface is perfectly flat then this value remains 0
		dyna# = 0
		
		;process water
	    For x = 1 To w\width-1
	        For z = 1 To w\depth-1
				fxWaterBank#(w\bank2,x,z) = (fxWaterBank#(w\bank1,x-1,z) + fxWaterBank#(w\bank1,x+1,z) + fxWaterBank#(w\bank1,x,z+1) + fxWaterBank#(w\bank1,x,z-1)) / 2.1-fxWaterBank#(w\bank2,x,z) 
				fxWaterBank#(w\bank2,x,z) = fxWaterBank#(w\bank2,x,z) * w\dampening#
				dyna# = dyna# + fxWaterBank#(w\bank2,x,z)
	        Next
	    Next

		;Only deform patch if necesary
		If dyna# <> 0 Then
		    ;PatchTransform
			k=0
			For i = 0 To w\depth
		        For j = 0 To w\width
					VertexCoords(w\surface,k,VertexX(w\surface,k),fxWaterBank#(w\bank2,j,i),VertexZ(w\surface,k))
		            k=k+1
		        Next
		    Next
		EndIf

		;should be optional - depending on type of texture (slows down seriously!)
		UpdateNormals w\entity
		
	    ;SwapWaterBuffer
		tmp = w\bank1
		w\bank1 = w\bank2
		w\bank2 = tmp

	Next

End Function

;--------------------------------------------------------------------------------------------------

Function fxWater_Dimple( hand, x,z, force#=1.0, range#=1.0)
	
	w.fxWaterWave = Object.fxWaterWave(hand)

	For xg = x - range# * 0.5 To x+range# * 0.5
		For zg = z - range# * 0.5 To z+range# * 0.5
			If xg> 0 And xg < w\width And zg>0 And zg<w\depth Then
				fxWaterBank#(w\bank2, xg,zg) = force#
			EndIf
		Next
	Next

End Function

;--------------------------------------------------------------------------------------------------

Function fxWater_get_surface( hand )

	w.fxWaterWave = Object.fxWaterWave( hand )
	Return w\surface

End Function

;--------------------------------------------------------------------------------------------------

Function fxWater_get_entity( hand )

	w.fxWaterWave = Object.fxWaterWave( hand )
	Return w\entity

End Function

;--------------------------------------------------------------------------------------------------

Function fxWater_Create_Buffer() ; xsize=1, zsize=1 )

	If FXWATER_NUM_BANKS >= FXWATER_MAX_BANKS Then 
		RuntimeError "[fxWater::Create_buffer] Max amount of fxWater memory banks reached!"
	Else
		;create a new memory bank and resize it to fit
		FXWATER_NUM_BANKS = FXWATER_NUM_BANKS + 1
		;NOTE: convert array into memory bank
	EndIf

	Return FXWATER_NUM_BANKS
	
End Function

;--------------------------------------------------------------------------------------------------

Function fxWater_Free_Buffers( )

;| Frees all buffers. Call as a part of when scene/level is removed from memory.


	;NOTE: resize memory banks to 0 (once implemented).

	Return 0
	
End Function

;--------------------------------------------------------------------------------------------------

;Creates a flat grid mesh
Function create_mesh_plane(width=1,depth=1,doublesided=False,parent=0)

	tot = width + (depth*width)
	mix#= (width+depth) * 0.5
	
	mesh=CreateMesh( parent )
	surf=CreateSurface( mesh )
	
	stx#=-.5
	sty#=stx
	stp#=Float(1)/Float(mix#)
	y#=sty#
	
	For a=0 To depth
		x#=stx
		v#=a/Float(depth)
		
		For b=0 To width
			u#=b/Float(width)
			AddVertex(surf,x,0,y,u,v)
			x=x+stp
		Next
		y=y+stp
	Next
	
	For a=0 To depth-1
		For b=0 To width-1
			v0=a*(width+1)+b:v1=v0+1
			v2=(a+1)*(width+1)+b+1:v3=v2-1
			AddTriangle( surf,v0,v2,v1 )
			AddTriangle( surf,v0,v3,v2 )
		Next
	Next
	
	UpdateNormals mesh

	If doublesided=True Then EntityFX mesh,16
	
	FitMesh mesh, -width*0.5, 0, -depth*0.5, width, 1, depth
	
	Return mesh

	
End Function

;--------------------------------------------------------------------------------------------------

Function create_noise_map()

; creates a noise map to be used as a generic reflection map

	sq = 128

	tex = CreateTexture(sq,sq,65,1)
	tbuf = TextureBuffer(tex)
	SetBuffer(tbuf)
	
	For x = 0 To sq-1
		For y = 0 To sq-1
			r = Rnd(100,120)
			g = Rnd(100,130)
			b = Rnd(190,240)
			Color r,g,b
			Rect x,y,1,1
		Next
	Next	
	
	SetBuffer(BackBuffer())
	
	Return tex

End Function


;--------------------------------------------------------------------------------------------------
;[end of stuff]
