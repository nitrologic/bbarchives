; ID: 2758
; Author: Krischan
; Date: 2010-08-29 11:59:59
; Title: Infinite Starfield / Infinite Asteroid Belt
; Description: Creates an infinite starfield or asteroid belt using a single surface entity

AppTitle "Infinite Starfield"

Graphics3D 800,600,32,2

Global SCALEX#		= 2.0				; starfield scale X
Global SCALEY#		= 2.0				; starfield scale Y
Global SCALEZ#		= 2.0				; starfield scale Z

Global TurnSpeed#	= 4.0				; cam turn speed
Global RollSpeed#	= 0.5				; cam roll speed
Global CameraSpeed#	= 0.01				; cam move speed

Global WIDTH%		= GraphicsWidth()	; grab screen width
Global HEIGHT%		= GraphicsHeight()	; grab screen height
Global TIMER%		= CreateTimer(60)	; timer

Type star
	
	Field col%
	Field x#,y#,z#
	
End Type

; camera
Global cam=CreateCamera()
CameraRange cam,0.01,10
PositionEntity cam,0,0,0

; starfield mesh
Global starfield=CreateMesh()
Global surf=CreateSurface(starfield)
Global star=CreateQuad()
EntityTexture starfield,CreateStarTexture()
EntityFX starfield,1+2+32
EntityBlend starfield,3

; add stars to starfield
AddStars(20000,0.001,0.005)

MoveMouse WIDTH/2,HEIGHT/2

; main loop
While Not KeyHit(1)
	
	; camera movement
	Movement(cam)
	
	; update stars
	UpdateStarfield(cam,2,1)

	RenderWorld()
	
	WaitTimer TIMER
	
	Flip 0
	
Wend

End

; create a simple star texture
Function CreateStarTexture(size%=256,flags%=3)
	
	Local tex%=CreateTexture(size,size,flags)
	Local tb%=TextureBuffer(tex)
	
	Local i#,j%,col%,rgb%
	
	SetBuffer tb
	LockBuffer tb
	
	For j=0 To 255
		
		col=255-j
		If col>255 Then col=255
		rgb=col*$1000000+col*$10000+col*$100+col
		
		For i=0 To 360 Step 0.1
			
			WritePixelFast (size/2)+(Sin(i)*(j*size/512)),(size/2)+(Cos(i)*(j*size/512)),rgb,tb
			
		Next
		
	Next
	
	UnlockBuffer tb
	SetBuffer BackBuffer()
	
	Return tex
	
End Function

; rebuild starfield mesh
Function UpdateStarfield(parent%,maxdist#=2.0,fade%=False)
	
	Local s.star,px#,py#,pz#,d#,a#
	
	ClearSurface(surf)
	
	For s.star = Each star
		
		; calc star position
		px=EntityX(parent)-s\x
		py=EntityY(parent)-s\y
		pz=EntityZ(parent)-s\z
		
		; check if star must be moved
		If px<-SCALEX Then s\x=s\x-(SCALEX*2)
		If px>+SCALEX Then s\x=s\x+(SCALEX*2)
		If py<-SCALEY Then s\y=s\y-(SCALEY*2)
		If py>+SCALEY Then s\y=s\y+(SCALEY*2)
		If pz<-SCALEZ Then s\z=s\z-(SCALEZ*2)
		If pz>+SCALEZ Then s\z=s\z+(SCALEZ*2)
		
		; reposition star
		PositionEntity star,s\x,s\y,s\z
		
		; star is visible?
		If EntityInView(star,cam) Then
			
			; get distance
			d=EntityDistance(star,cam)
			
			; check if not to far away
			If d<maxdist Then
				
				; align star to cam
				PointEntity star,cam
				
				; add alpha
				a=1.0 : If fade Then a=Normalize(d,maxdist*0.5,maxdist,1,0)
				
				; add star to starfield again
				AddToSurface(star,surf,starfield,s\col,s\col,s\col,a)
				
			EndIf
			
		EndIf
		
	Next
	
End Function

; add stars to starfield mesh
Function AddStars(amount%=1,min#=0.01,max#=0.02)
	
	Local i%,s.star,size#
	
	For i=1 To amount
		
		s.star = New star
		
		size#=Rnd(min,max)
		
		s\col=Rand(64,255)
		s\x=Rnd(-SCALEX,SCALEX)
		s\y=Rnd(-SCALEY,SCALEY)
		s\z=Rnd(-SCALEZ,SCALEZ)
		
		PositionEntity(star,s\x,s\y,s\z,1)
		ScaleEntity star,size,size,size
		AddToSurface(star,surf,starfield,255,255,255,1)
		
	Next
	
End Function

; simple spaceship freeflight
Function Movement(cam%,sensitivity#=1.0)
	
	Local roll#,cx#,cz#,tx#,ty#
	
	cx=(KeyDown(205)-KeyDown(203))*CameraSpeed
	cz=(KeyDown(200)-KeyDown(208))*CameraSpeed
	roll=(MouseDown(2)-MouseDown(1))*RollSpeed
	
	tx=Normalize(MouseX(),0,WIDTH , 1,-1)
	ty=Normalize(MouseY(),0,HEIGHT,-1, 1)
	
	If ty<0 Then ty=(Abs(ty)^sensitivity)*-1 Else ty=ty^sensitivity
	If tx<0 Then tx=(Abs(tx)^sensitivity)*-1 Else tx=tx^sensitivity
	
	TurnEntity cam,ty*TurnSpeed,tx*TurnSpeed,roll*TurnSpeed
	MoveEntity cam,cx,0,cz
	
End Function

; normalize a value
Function Normalize#(value#=128.0,value_min#=0.0,value_max#=255.0,norm_min#=0.0,norm_max#=1.0)
	
	Return ((value-value_min)/(value_max-value_min))*(norm_max-norm_min)+norm_min
	
End Function

; add a mesh to another mesh's surface
Function AddToSurface(mesh,surf,singlesurfaceentity,r%,g%,b%,a#) 
	
	Local vert%[2],vr%[2],vg%[2],vb%[2],va#[2]
	Local surface%,oldvert%,i%,i2%
	
	surface = GetSurface(mesh,1) 
	
	For i = 0 To CountTriangles(surface)-1
		
		For i2 = 0 To 2 
			
			oldvert = TriangleVertex(surface,i,i2)
			
			vr[i2]=r
			vg[i2]=g
			vb[i2]=b
			va[i2]=a
			
			TFormPoint VertexX(surface,oldvert),VertexY(surface,oldvert),VertexZ(surface,oldvert), mesh,singlesurfaceentity 
			vert[i2] = AddVertex(surf,TFormedX(),TFormedY(),TFormedZ(),VertexU(surface,oldvert),VertexV(surface,oldvert)) 
			VertexColor surf,vert[i2],r,g,b,a
			
		Next 
		
		AddTriangle(surf,vert[0],vert[1],vert[2])
		
	Next 
	
End Function

; create a quad
Function CreateQuad(r%=255,g%=255,b%=255,a#=1.0)
	
	Local mesh%,surf%,v1%,v2%,v3%,v4%
	
	mesh=CreateMesh()
	surf=CreateSurface(mesh)
	
	v1=AddVertex(surf,-1,1,0,1,0)
	v2=AddVertex(surf,1,1,0,0,0)
	v3=AddVertex(surf,-1,-1,0,1,1)
	v4=AddVertex(surf,1,-1,0,0,1)
	
	VertexColor surf,v1,r,g,b,a
	VertexColor surf,v3,r,g,b,a
	VertexColor surf,v2,r,g,b,a
	VertexColor surf,v4,r,g,b,a
	
	AddTriangle(surf,0,1,2)
	AddTriangle(surf,3,2,1)
	
	FlipMesh mesh
	
	Return mesh
	
End Function
