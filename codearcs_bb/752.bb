; ID: 752
; Author: Litobyte
; Date: 2003-07-23 09:50:56
; Title: b3d light trails FX
; Description: Same Light Trail function of below, with the new vertexalpha[and vertex color]command used

; b3d Light trails and simple dynamic spherical environment mapping
; Started from the simple demo wrote by Ben Hust
Graphics3D 640,480,16,1
SetBuffer BackBuffer()
SeedRnd 21354
;--------------------------------------------------------------------------
;I normally keep this as an include but it makes it easier if I add it here
;--------------------------------------------------------------------------

Const trail_update=1				; trail update frequency (lower number updates more often)
Const maxVerts=20					; maximum number of verts per trail object (polygons * 2)

Type trail
	Field id,brush,surface
	Field tdx#[maxVerts], tdy#[maxVerts], tdz#[maxVerts]
	Field update%,alpha#
	Field point1, point2
End Type

;---------------------
;standard camera stoof
;---------------------
camera=CreateCamera()
CameraClsColor camera,0,150,200

;light=CreateLight()
;RotateEntity light,30,0,15
AmbientLight 147,147,147

;get a texture
Gosub gridtex

;---------------------------
;create an object to look at
;---------------------------
ship=CreateSphere(8)
exhaust1=CreateCylinder(8,True,ship)
exhaust2=CreateCylinder(8,True,ship)
ScaleMesh ship,1,1,.5
RotateMesh ship,60,0,180
am=CreateCone(8)
ScaleMesh am,1.05,2.5,.5
RotateMesh am,95,0,0
AddMesh am,ship
FreeEntity am

ScaleEntity exhaust1,0.4,1,0.4		: ScaleEntity exhaust2,0.4,1,0.4
RotateEntity exhaust1,90,0,0		: RotateEntity exhaust2,90,0,0
MoveEntity exhaust1,-1,-1.2,0		: MoveEntity exhaust2,1,-1.2,0


;entitytexture exhaust1,mytexture
;entitytexture exhaust2,mytexture

; give simple first layer texturing to the ship (download texture from:)
EntityTexture ship,grid,0,0
; or comment and use this below:
s=GetSurface(ship,1)
For i=1 To CountVertices(s)
	VertexColor s,i,Rand(1,255),Rand(1,255),Rand(1,255)
Next
;EntityFX ship,2
UpdateNormals ship
EntityShininess ship,.85

; front lights dxlight
clight=CreateLight(2,ship)
MoveEntity clight,0,0,15
RotateEntity clight,15,0,0
LightColor clight,100,150,250
LightConeAngles clight,5,10
LightRange clight,250

; rear trail-dxlight
;tlight=CreateLight(2,ship)
;MoveEntity tlight,0,0,-5:LightColor tlight,150,50,0:LightRange tlight,50


; the envmap itself
envmap=CreateTexture(128,128,256+64+1) ; create an empty texture
TextureBlend envmap,2
; with env spherical flag, And FAST MEM OP for later use
EntityTexture ship,envmap,0,1 ; and assign it to the ship mesh

EntityFX exhaust1,2:s=GetSurface(exhaust1,1)
For i=1 To CountVertices(s)
	c=Rand(150,250)
	VertexColor s,i,c,c,c
Next
EntityFX exhaust2,2:s=GetSurface(exhaust2,1)
For i=1 To CountVertices(s)
	c=Rand(150,250)
	VertexColor s,i,c,c,c
Next

; dynamic env map camera
Global demcam=CreateCamera(ship)
CameraClsColor demcam,0,100,155
CameraViewport demcam,0,0,128,128
CameraZoom demcam,.25

;CameraProjMode demcam,0 ;fasthide the special cam
demcamp=CreateCube(demcam)
EntityColor demcamp,255,200,200
TurnEntity demcam,0,180,0
MoveEntity demcam,0,10,-5 ; put it backward or forward by setting a minus Z or a plus Z

;create a floor
Floor=CreateFace(100,True)
ScaleEntity Floor,500,500,500
EntityTexture Floor,grid
MoveEntity Floor,0,-.5,0

Gosub createobjects

CreateTrail(exhaust1,exhaust2,8)

speed#=1
showcam=False
timer=CreateTimer(50)

Repeat

	WaitTimer(timer)

	If KeyDown(200) move#=move#+.25			;Forwards
	If KeyDown(208) move#=move#-.25			;Backward
	If KeyDown(205) turn#=turn#-4			;Left
	If KeyDown(203) turn#=turn#+4			;Right
	If KeyHit(17) wire=Not(wire):WireFrame wire
	If KeyHit(2) showcam=Not(showcam)
	move=move*.9
	
	RotateEntity ship,0,turn,0
	MoveEntity ship,0,0,move
	
	;hide
	CameraProjMode camera,0
	CameraProjMode demcam,1

	EntityAlpha ship,0
	EntityAlpha exhaust1,0
	EntityAlpha exhaust2,0
	
	;render
	PointEntity demcam,ship
	
	RenderWorld	
	CopyRect 0,0,128,128,0,0,BackBuffer(),TextureBuffer(envmap)
		
	EntityAlpha ship,1
	EntityAlpha exhaust1,1
	EntityAlpha exhaust2,1
	
	CameraProjMode camera,1
	If Not(SHOWCAM) CameraProjMode demcam,0
	
	
	update_camera(ship,camera,5,3)	
	updatetrails
	
	UpdateWorld()
	RenderWorld()
	
	Flip
	

Until KeyHit(1)

End

.gridtex

	grid=CreateTexture(64,64,1+8)
	
	SetBuffer TextureBuffer(grid)
	Color 255,255,255 	: Rect 0,0,64,64
	Color 255,0,0 		: Rect 0,0,64,64,False
	Color 155,155,155	: Rect 32,32,64,64,False
	ScaleTexture grid,.1,.1
	
	SetBuffer BackBuffer()

Return

.createobjects

;create some (20x20=400) objects to variate the surrounding environ
For i=1 To 20
	For l=1 To 20
		ot%=Rand(1,20) ;randomize object type
		Select ot%
			Case 1
				o=CreateCube()
				ScaleEntity o,1,5,1
				EntityColor o,0,50,100
			Case 2
				o=CreateSphere(5)
				ScaleEntity o,3,5,3
				EntityColor o,50,220,150
				EntityAlpha o,.5
				EntityFX o,4
			Case 3
				o=CreateCone(6)
				ScaleEntity o,2,5,2
				EntityColor o,50,150,20
			Case 4
				o=CreateCylinder(6,1)
				ScaleEntity o,1,5,1
				EntityColor o,150,100,0
			Case 5
				o=CreateSphere(8)
				ScaleEntity o,1,5,1
				EntityColor o,250,100,0
				EntityShininess o,.75
			Default
				o=CreatePivot()			
		End Select
		PositionEntity o,(i-10)*25,2.5,(l-10)*25
	Next
Next

Return

;Creates a single sided face segmented
Function CreateFace(segs=1,double=False,parent=0)
    mesh=CreateMesh( parent )
    surf=CreateSurface( mesh )
    stx#=-.5
    sty#=stx
    stp#=Float(1)/Float(segs)
    y#=sty
    For a=0 To segs
        x#=stx
        v#=a/Float(segs)
        For b=0 To segs
            u#=b/Float(segs)
            AddVertex(surf,x,0,y,u,v) ; swap these for a different start orientation
            x=x+stp
        Next
        y=y+stp
    Next
    For a=0 To segs-1
        For b=0 To segs-1
            v0=a*(segs+1)+b:v1=v0+1
            v2=(a+1)*(segs+1)+b+1:v3=v2-1
            AddTriangle( surf,v0,v2,v1 )
            AddTriangle( surf,v0,v3,v2 )
        Next
    Next
    UpdateNormals mesh
    If double=True Then EntityFX mesh,16
    Return mesh
End Function

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
			t\alpha=1.0-Float(i)/10
			If t\alpha<0 Then t\alpha=0
			VertexColor t\surface,i,255-i*10,(20-i)*5,i*15,t\alpha
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
Function CreateTrail(point1,point2,polys=20,startalpha#=1.0)

	Local x#,y#,z#
	
	t.trail=New trail

	;create mesh and set properties
	t\id = CreateMesh()
	t\brush = CreateBrush()
	BrushBlend t\brush,3
	BrushFX t\brush,2+16
	t\surface = CreateSurface(t\id,t\brush)
	t\alpha=1.0
	
	;check there are two trail strat points
	If point1=False Then RuntimeError "must specify 'point1' for one side of the trail" Else t\point1=point1
	If point2=False Then RuntimeError "must specify 'point2' for one side of the trail" Else t\point2=point2
	
	;mid pount between two trial objects
	x=(EntityX(t\point1,1)+EntityX(t\point2,1))/2
	y=(EntityY(t\point1,1)+EntityY(t\point2,1))/2
	z=(EntityZ(t\point1,1)+EntityZ(t\point2,1))/2

	;create polygons
	For i=0 To polys
		AddVertex t\surface,x,y,z,Float(i)/Float(polys),1,0
		AddVertex t\surface,x,y,z,Float(i)/Float(polys),0,0
		VertexNormal t\surface,i,0,-1,0
		VertexNormal t\surface,i+1,0,-1,0
		
		If i>0
			AddTriangle t\surface,i*2,i*2-1,i*2-2
			AddTriangle t\surface,i*2,i*2+1,i*2-1
		End If
	Next
	
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
	
	;work out new values
	cx=curvevalue(cx,newxvalue(px,pa,distance),6)
	cy=curvevalue(cy,py+height,7)
	cz=curvevalue(cz,newzvalue(pz,pa,distance),6)

	;update camera position


	If MouseDown(1)
		PositionEntity camera,px,py+7,pz+5
		MoveEntity camera,MouseXSpeed()/10,0,MouseYSpeed()/10
		PointEntity camera,character
	Else
		PositionEntity camera,cx,cy,cz
		PointEntity camera,character
	EndIf
	
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
