; ID: 851
; Author: Rob
; Date: 2003-12-09 09:00:15
; Title: RippleMesh (  )
; Description: Ripple a mesh to parameters

;set up
Graphics3D 640,480,16,2
camera=CreateCamera()
PositionEntity camera,0,0,-3
light=CreateLight()
RotateEntity light,45,45,0

;any mesh with enough verts
mymesh = CreateSphere(12)
EntityShininess mymesh,1

While Not KeyHit(1)
	RippleMesh(mymesh,1,20,0.01)
	UpdateWorld
	RenderWorld
	Flip
Wend
End

;mesh is your mesh
;speed is how fast it ripples
;density is how fine the effect is (experiment to see how much of the mesh you affect)
;depth is how much it ripples

Function ripplemesh(mesh,speed#,density#,depth#)
	count=MilliSecs()*speed
	For scount=1 To CountSurfaces(mesh)
		surface = GetSurface(mesh,scount)
		numverts=CountVertices(surface)-1
		For i=0 To numverts
			a#=Cos(count+(i*density))*speed
			b#=Sin(count+(i*density))*speed
			c#=-b;Sin(count+(i*density))*speed
			nx#=VertexNX(surface,i)*depth
			ny#=VertexNY(surface,i)*depth
			nz#=VertexNZ(surface,i)*depth			
			x#=VertexX(surface,i)
			y#=VertexY(surface,i)
			z#=VertexZ(surface,i)		
			VertexCoords surface,i,x#+(a*nx),y#+(b*ny),z#+(c*nz)
		Next
	Next
End Function
