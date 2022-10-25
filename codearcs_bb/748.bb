; ID: 748
; Author: Binary_Moon
; Date: 2003-07-19 04:47:39
; Title: Trails - As seen in Rocket Boards
; Description: Flexible trail mesh that follows two entities

Graphics3D 640,480,16,2
SetBuffer BackBuffer()

;---------------------
;standard camera stoof
;---------------------
camera=CreateCamera()
light=CreateLight(camera)

;get a texture
Gosub gridtex

;---------------------------
;create an object to look at
;---------------------------
ship=CreateCube()
exhaust1=CreateCylinder(8,True,ship)
exhaust2=CreateCylinder(8,True,ship)

EntityColor ship,50,50,100
ScaleMesh ship,1,.5,2

EntityColor exhaust1,128,128,200	: EntityColor exhaust2,128,128,200
ScaleEntity exhaust1,0.4,1,0.4		: ScaleEntity exhaust2,0.4,1,0.4
RotateEntity exhaust1,90,0,0		: RotateEntity exhaust2,90,0,0
MoveEntity exhaust1,-1,-1.2,0		: MoveEntity exhaust2,1,-1.2,0

;create a floor
Floor=CreatePlane()
EntityTexture Floor,grid
MoveEntity Floor,0,-.5,0

;WireFrame True

CreateTrail(exhaust1,exhaust2,8)

speed#=1

timer=CreateTimer(50)

Repeat

	WaitTimer(timer)

	If KeyDown(200) move#=move#+.4			;Forwards
	If KeyDown(208) move#=move#-.4			;Backward
	If KeyDown(205) turn#=turn#-4			;Left
	If KeyDown(203) turn#=turn#+4			;Right
	
	move=move*.9
	
	RotateEntity ship,0,turn,0
	MoveEntity ship,0,0,move
	
	update_camera(ship,camera,5,2)
	
	updatetrails
	
	UpdateWorld
	RenderWorld
	
	Flip
	

Until KeyHit(1)

End

.gridtex

	grid=CreateTexture(64,64,1+8)
	
	SetBuffer TextureBuffer(grid)
	Color 255,255,255 	: Rect 0,0,64,64
	Color 255,0,0 		: Rect 0,0,64,64,False
	ScaleTexture grid,8,8
	
	SetBuffer BackBuffer()

Return


;--------------------------------------------------------------------------
;I normally keep this as an include but it makes it easier if I add it here
;--------------------------------------------------------------------------

Const trail_update=1				; trail update frequency (lower number updates more often)
Const maxVerts=20					; maximum number of verts per trail object (polygons * 2)

Type trail
	Field id
	Field tdx#[maxVerts], tdy#[maxVerts], tdz#[maxVerts]
	Field update
	Field surface
	Field point1, point2
End Type

;-----------------
;update all trails
;-----------------
Function UpdateTrails()
	
	Local x#,y#,z#

	;loop through all trails
	For t.trail = Each trail

		; Move the trail pieces along.
		t\update=t\update+1 : If t\update=trail_update Then t\update=0

		;update				
		If t\update=0
			For i=2 To CountVertices(t\surface)-1
				t\tdx[i] = (VertexX(t\surface,i-2) - VertexX(t\surface,i))/trail_update
				t\tdy[i] = (VertexY(t\surface,i-2) - VertexY(t\surface,i))/trail_update
				t\tdz[i] = (VertexZ(t\surface,i-2) - VertexZ(t\surface,i))/trail_update
			Next
		End If

		For i=2 To CountVertices(t\surface)-1
			VertexCoords(t\surface,i,VertexX(t\surface,i)+t\tdx[i],VertexY(t\surface,i)+t\tdy[i],VertexZ(t\surface,i)+t\tdz[i])
		Next
		
		;position the first two verts at the back of the ship
		VertexCoords(t\surface,0,EntityX(t\point1,1),EntityY(t\point1,1),EntityZ(t\point1,1))
		VertexCoords(t\surface,1,EntityX(t\point2,1),EntityY(t\point2,1),EntityZ(t\point2,1))
		
	Next
			
End Function

;---------------------
;create a trail object
;---------------------
Function CreateTrail(point1,point2,polys=20,alpha#=1.0)

	Local x#,y#,z#
	
	t.trail=New trail

	;create mesh and set properties
	mesh = CreateMesh()
	t\surface = CreateSurface(mesh)
	t\id = mesh

	;check there are two trail strat points
	If point1=False Then RuntimeError "You must specify 'point1' for one side of the trail" Else t\point1=point1
	If point2=False Then RuntimeError "You must specify 'point2' for one side of the trail" Else t\point2=point2
	
	;mid pount between two trial objects
	x=(EntityX(t\point1,1)+EntityX(t\point2,1))/2
	y=(EntityY(t\point1,1)+EntityY(t\point2,1))/2
	z=(EntityZ(t\point1,1)+EntityZ(t\point2,1))/2

	;create polygons
	For i=0 To polys
		AddVertex t\surface,x,y,z,Float(i)/Float(polys),1,0
		AddVertex t\surface,x,y,z,Float(i)/Float(polys),0,0
		If i>0
			AddTriangle t\surface,i*2,i*2-1,i*2-2
			AddTriangle t\surface,i*2,i*2+1,i*2-1
		End If
	Next
	
	;set trail properties
	EntityFX mesh,17
	EntityAlpha mesh,alpha
	
	Return mesh
	
End Function

;--------------------------------
;free trail and delete trail type
;--------------------------------
Function free_trail(id)

	For t.trail=Each trail
		If t\id=id
			FreeEntity t\id
			Delete t
		EndIf
	Next
	
	Return True
	
End Function

;----------------------------------------------------------------------
;These are just handy functions that have nothing to do with the trails
;----------------------------------------------------------------------

;---------------------------------------
;a chase cam for use in 3rd person games
;---------------------------------------
Function update_camera(character,camera,distance#,height#,angle=180)

	;get player values
	px#=EntityX(character)
	py#=EntityY(character)
	pz#=EntityZ(character)
	
	pa#=EntityYaw(character)+angle
	
	;get camera values
	cx#=EntityX(camera)
	cy#=EntityY(camera)
	cz#=EntityZ(camera)
	
	;If EntityDistance(camera,character)>Float(distance*1.5) EntityType camera,c_none
	
	;work out new values
	cx=curvevalue(cx,newxvalue(px,pa,distance),6)
	cy=curvevalue(cy,py+height,7)
	cz=curvevalue(cz,newzvalue(pz,pa,distance),6)

	;update camera position

	PositionEntity camera,cx,cy,cz
	PointEntity camera,character
	
	;EntityType camera,c_camera
	
End Function

;--------------------
;curve value function
;--------------------
Function CurveValue#(current#,destination#,amount)

	current=current+((destination-current)/amount)
	If current<0.01 And current>-0.01 Then current=0
	Return current

End Function

;---------
;newxvalue
;---------
Function NewXValue#(current#,ang#,dist#)

	Return current-Sin(ang)*dist

End Function

;---------
;newyvalue
;---------
Function NewYValue#(current#,ang#,dist#)

	Return current+Sin(ang)*dist

End Function

;---------
;newzvalue
;---------
Function NewZValue#(current#,ang#,dist#)

	Return current+Cos(ang)*dist

End Function
