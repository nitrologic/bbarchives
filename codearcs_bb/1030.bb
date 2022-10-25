; ID: 1030
; Author: fredborg
; Date: 2004-05-14 18:19:02
; Title: Depth of Field
; Description: Simple depth of field effect!

;
; Depth of field
;
; Created by Mikkel Fredborg
; Use as you please!
;
Graphics3D 800,600,0,2
SetBuffer BackBuffer()

HidePointer

;
; Create a camera...
camera = CreateCamera()
CameraRange camera,0.1,1000.0
CameraFogMode camera,True
CameraFogRange camera,100,1000

;
; create some cubes
For i = 0 To 100
	cube = CreateCube()
	PositionEntity cube,Rnd(-100,100),Rnd(-100,20),Rnd(-100,100)
	RotateEntity cube,Rnd(-180,180),Rnd(-180,180),Rnd(-180,180)
	ScaleEntity cube,Rnd(1,10),Rnd(1,10),Rnd(1,10)
Next
			
;
; Light
light = CreateLight()
RotateEntity light,90,0,0

; Depth of Field setup
Type DepthOfField
	Field layers
	Field layer[99]
	Field texture
	Field tsize
	Field tbuffer
	Field near#,far#
	Field camera
End Type

dof.DepthOfField = DOF_Create(camera,3,2.0)

Repeat
	RotateEntity camera,MouseY(),-MouseX(),0
	MoveEntity camera,KeyDown(205)-KeyDown(203),0,KeyDown(200)-KeyDown(208)

	DOF_Update(dof)
	
	RenderWorld
	
	Flip False

Until KeyHit(1)

End

Function DOF_Update(dof.depthoffield)

	HideEntity dof\layer[0]

	CameraRange dof\camera,dof\near*0.95,1000
	CameraViewport dof\camera,0,0,dof\tsize,dof\tsize
	RenderWorld
	CopyRect 0,0,dof\tsize,dof\tsize,0,0,BackBuffer(),dof\tbuffer
	
	ShowEntity dof\layer[0]

	CameraRange dof\camera,0.1,1000	
	CameraViewport dof\camera,0,0,GraphicsWidth(),GraphicsHeight()
	
End Function

Function DOF_Create.DepthOfField(camera,layers,spread#=0.0)

	dof.depthoffield = New depthoffield

	dof\camera = camera

	dof\layers = layers

	dof\tsize	 = 512
	dof\near	 = 100.0
	dof\far		 = 300.0
	
	ClearTextureFilters
	dof\texture = CreateTexture(dof\tsize,dof\tsize,1+256+16+32)
	dof\tbuffer = TextureBuffer(dof\texture)
	
	ang# = 360.0/Float(dof\layers)
	For i = 0 To dof\layers-1
		dof\layer[i] = CreateFace(1)
			
		EntityAlpha dof\layer[i],1.0/Float(dof\layers)
		EntityFX	dof\layer[i],1+8
	
		ps# = dof\near+(i*((dof\far-dof\near)/Float(dof\layers)))

		px# = Sin(i*ang)*(i/Float(dof\layers))*spread
		py# = Cos(i*ang)*(i/Float(dof\layers))*spread
		
		PositionEntity dof\layer[i],px,py,ps
		ScaleEntity dof\layer[i],ps,ps,1.0		
	
		EntityTexture dof\layer[i],dof\texture
		
		If i = 0
			EntityParent dof\layer[i],dof\camera,True
		Else
			EntityParent dof\layer[i],dof\layer[i-1],True
		End If
	Next

	Return dof

End Function

Function CreateFace(segs=1,parent=0)

	mesh=CreateMesh( parent )
	surf=CreateSurface( mesh )
	stx#=-1.0
	sty#=stx
	stp#=Float(2)/Float(segs)
	y#=sty
	For a=0 To segs
		x#=stx
		v#=a/Float(segs)
		For b=0 To segs
			u#=b/Float(segs)
			AddVertex(surf,x,-y,0,u,v) ; swap these for a different start orientation
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
	
	FlipMesh mesh
	UpdateNormals mesh

	Return mesh
	
End Function
