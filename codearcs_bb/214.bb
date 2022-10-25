; ID: 214
; Author: Rob
; Date: 2002-02-01 17:24:14
; Title: FLEX - proper shadows, lights and shaped shadows
; Description: FLEX will accurately deform any shape to a level or a terrain.

; Flexible Towel Shadows By Rob Cummings (rob@redflame.net),
; CreateFace function & assistance by David Bird
;
; Rough code here. Use for cloth sims, 
; lights And shadows. Have fun! Load own image for fun or 
; even use a character mesh as the Flex...

AppTitle "Flexible Towel Shadows in Blitz3D"
HidePointer
Global camera,light,world,Flex,mx#,my#,ball,w
;setup graphics and scene
Graphics3D 640,480,16,2
SetBuffer BackBuffer()
camera=CreateCamera()
CameraRange camera,1,1600
MoveEntity camera,500,800,500
light=CreateLight(2)
MoveEntity light,0,400,400
LightRange light,350
AmbientLight 50,50,50

;make test world
world=createworld() 

;make shadow
Flex=CreateFlex()

;make shadow texture
t=CreateTexture(32,32,48+2)
SetBuffer TextureBuffer(t)
Color 255,255,255:Rect 0,0,32,32,1

For i=0 To 31
	col=(255-(i*8))
	pos=(i/2)
	wid=31-i
	Color col,col,col
	Oval pos,pos,wid,wid,1
Next
Color 255,255,255
EntityTexture Flex,t
EntityFX Flex,1
EntityBlend Flex,2

;hack
whitetexture=CreateTexture(32,32)


;make ball
ball=CreateSphere()
EntityColor ball,255,0,0
EntityShininess ball,1
ScaleEntity ball,50,50,50
MoveEntity ball,0,300,0
TurnEntity ball,0,-45,0

;misc
PointEntity camera,world
SetBuffer BackBuffer()

;mainloop
While Not KeyHit(1)
	If KeyHit(17)
		 w=1-w
		WireFrame w
		;visibility hack for you to test
		If w=1
			EntityBlend Flex,1
		Else
			EntityBlend Flex,2
		EndIf
	EndIf
	
	mx#=MouseXSpeed()*0.5
	my#=MouseYSpeed()*0.5
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
	MoveEntity ball,-mx,0,my
	
	;UpdateFlex(target deformable mesh (try a model!!),mesh that casts shadow)
	UpdateFlex(Flex,ball)
	UpdateWorld
	RenderWorld
	Text 0,0,"Use mouse to move ball, watch the shadow!"
	Text 0,16,"press 'w' to toggle wireframe mode, ESC to quit."
	Flip
Wend
End

;load your own level? but make it pickable.
Function CreateWorld()
	p=CreatePivot()
	a=CreateCube(p)
	FitMesh a,-500,0,-500,1000,10,1000
	a=CreateCube(p)
	FitMesh a,-200,10,-200,400,40,400
	a=CreateSphere(8,p)
	ScaleEntity a,100,100,100
	MoveEntity a,-200,0,-200
	a=CreateSphere(8,p)
	ScaleEntity a,100,100,100
	MoveEntity a,200,0,-200
	a=CreateSphere(8,p)
	ScaleEntity a,100,100,100
	MoveEntity a,200,0,200
	a=CreateSphere(8,p)
	ScaleEntity a,100,100,100
	MoveEntity a,-200,0,200
	
	For i=1 To CountChildren(p)
		EntityPickMode GetChild(p,i),2
	Next
	
	Return p
End Function

Function CreateFlex()
	m=CreateFace(8) ;segs
	ScaleEntity m,250,250,250
	Return m
End Function

Function UpdateFlex(flexmesh,source)
	x#=EntityX(source):y#=EntityY(source):z#=EntityZ(source)
	PositionEntity flexmesh,x,y,z

	;you can optimise by precalculating x and z
	s=GetSurface(flexmesh,1)
	For i=0 To CountVertices(s)-1

		vx#=VertexX(s,i)
		vy#=0
		vz#=VertexZ(s,i)

		TFormPoint vx,vy,vz,flexmesh,0
		vx=TFormedX() : vy=TFormedY() : vz=TFormedZ()
		
		LinePick vx,vy,vz,vx,vy-9000,vz
		vy=PickedY()+50
		
		TFormPoint vx,vy,vz,0,flexmesh
		vx=TFormedX() : vy=TFormedY() : vz=TFormedZ()
		
		VertexCoords s,i,vx,vy,vz
		
	Next
	
End Function


;Creates a single sided face
;segmented
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
