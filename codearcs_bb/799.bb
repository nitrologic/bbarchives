; ID: 799
; Author: bradford6
; Date: 2003-09-26 12:39:55
; Title: Particle Emitter
; Description: uses Type within a Type

; bradford6 2003
; 
;LMB = create emitter
;RMB = Delete emitter
Graphics3D 800,600,32

; place your particle image file HERE 
Global particle_image$ = "particle.png"

; Create Global Variables
Global MAX_EMITTERS = 64
Const MAX_PARTICLES = 12
Global emit_create_timer,emit_create_interval=20
Global midh=GraphicsHeight()/2
Global midw=GraphicsWidth()/2
Global pictentity
Global px#,py#,pz#

; create a simple world (camera, light and a Plane)
Global camera = CreateCamera()
	MoveEntity camera,0,5,-10
	TurnEntity camera,25,0,0
Global plane = CreateCube()
	EntityPickMode plane,2
	EntityColor plane,25,25,150
	ScaleEntity plane,20,.1,20

Global light = CreateLight()

; create a cube or cone and a particle
Global cube = CreateCone():HideEntity cube
Global part = LoadSprite(particle_image$,8):HideEntity part

EntityColor cube,255,0,0
EntityColor part,255,255,0


; Type declarations
; we are creating a particle type to handle the individual particles and an emitter that spews the ; 

particles


Type particle
	Field entity
	Field speed#
	Field life#
	Field x#,y#,z#
	Field image
	Field is_alive

End Type

Type emitter
	Field entity
	Field x#,y#,z#
	Field rate
	Field is_alive
	Field p.particle[MAX_PARTICLES]

End Type


; create a total of MAX_EMITTERS emitters
; and leave them dormant until we need them

For x = 1 To MAX_EMITTERS
	e.emitter = New emitter
	e\is_alive=0	; is NOT alive until we enable it
Next

; # # # MAIN LOOP

Repeat

update_emitters.emitter()

emit_create_timer = emit_create_timer - 1
If emit_create_timer <0 Then emit_create_timer=-1
	
	If MouseDown(1) = 1
		If emit_create_timer=-1
			emit_create_timer=emit_create_interval
		;Repeat
		;Until MouseDown(1)=0
			campick()					; pick
			create_emitter(px#,py#,pz#)
		EndIf
	EndIf


If MouseDown(2) = 1
	campick()
	If pictentity<>0
		kill_emitter()
	EndIf
EndIf

	UpdateWorld()
	RenderWorld()
	Flip

Until KeyHit(1)=1

;======================================================


Function create_emitter.emitter(ex#,ey#,ez#)
; place an emiiter where we click
; count through until we find an empty one--then stop
	
	For e.emitter = Each emitter
		
		If added=0
			If e\is_alive=0
				added=1
				e\is_alive = 1
				e\entity = CopyEntity(cube)
				EntityPickMode e\entity,2 ; set to ply pick
				e\x# = ex#
				e\y# = ey#
				e\z# = ez#
				PositionEntity e\entity,e\x#,e\y#,e\z#
				
				; here for our newly created
				For n = 1 To MAX_PARTICLES
					e\p.particle[n] = New particle
				Next
			EndIf
		EndIf	
		
	Next

Return e.emitter
End Function
;======================================================

Function kill_emitter.emitter()
; place an emiiter where we click
; count through until we find an empty one--then stop
	For e.emitter = Each emitter
		
		If pictentity=e\entity
			For n = 1 To MAX_PARTICLES
				If e\p.particle[n]\is_alive
					FreeEntity e\p.particle[n]\entity
				EndIf
			Next

			FreeEntity e\entity
			Delete e
			e.emitter = New emitter
			; free particles too	
				; or delete entity and then immed e.entity = new entity then 	
			
		EndIf	
		
	Next

Return e.emitter

Return
End Function
;======================================================


Function update_emitters.emitter()
For e.emitter = Each emitter
	If e\is_alive = 1
		For n = 1 To MAX_PARTICLES
			If e\p.particle[n]\is_alive = 0
				e\p.particle[n]\is_alive=1
				e\p.particle[n]\entity = CopyEntity(part)
				e\p.particle[n]\life = Rnd(20,60)
				rnx# = EntityX(e\entity)
				rny# = EntityY(e\entity)
				rnz# = EntityZ(e\entity)
				rnx# = Rnd(rnx#-0.5,rnx#+0.5)
				rny# = Rnd(rny#-0.5,rny#+0.5)
				rnz# = Rnd(rnz#-0.5,rnz#+0.5)


				PositionEntity e\p.particle[n]\entity,rnx#,rny#,rnz#
			Else
				MoveEntity e\p.particle[n]\entity,0,.05,0
				e\p.particle[n]\life = e\p.particle[n]\life -1
				If e\p.particle[n]\life<0
					e\p.particle[n]\is_alive=0
					FreeEntity e\p.particle[n]\entity
				EndIf
			EndIf
			
		Next
	;	e\p.particle[n]\entity = CreateSphere(6)
	;	MoveEntity e\p.particle[n]\entity,0,3,Rnd(0,5)
	
	EndIf
Next
Return e.emitter
End Function
;======================================================


Function pause_emitter()

Return
End Function
;======================================================

Function resume_emitter()

Return
End Function


Function campick()
	
	pictentity=0
	CameraPick(camera,MouseX(),MouseY())
	
	If PickedEntity()<>0
	
			
		;If EntityName(PickedEntity())="map"
			pictentity=PickedEntity()
			px#=PickedX()
			py#=PickedY()
			pz#=PickedZ()
				
			;	PositionEntity pick_pivot,pictx,picty,pictz 
			
			;edistance# = EntityDistance(camera,pick_pivot)
			
			;pictname$=EntityName$(pictentity)
			
			
	EndIf

Return 
End Function
