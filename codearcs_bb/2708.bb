; ID: 2708
; Author: Krischan
; Date: 2010-05-06 10:52:03
; Title: Discworld
; Description: A procedural snow globe

AppTitle "Discworld"

Graphics3D 800,600,32,2

Global camx#,camy#,camz#,camp#,camw#,camr#,keepcam%=False,tx%

SeedRnd 6

Type snow
	
	Field entity%
	Field size#
	Field distance#
	Field pivot%
	Field speed_p#
	Field speed_y#
	Field speed_r#
	
End Type


; Constants
Const HEIGHTMAP_SIZE%=256
Const MAXCOLS%=2^16
Const DIV3#=1.0/3
Const PATCHSIZE%=64
Const DETAIL%=64
Const SNOWFLAKES%=1000

; Globals
Global Minh#=2^16
Global Maxh#=0


; Blitzarrays
Global RT%[MAXCOLS],GT%[MAXCOLS],BT%[MAXCOLS]
Global RW%[MAXCOLS],GW%[MAXCOLS],BW%[MAXCOLS]


; Arrays
Dim Red%(0),Green%(0),Blue%(0),Percent#(0)

Dim VertexBuffer(128,128)


; Frametween stuff
Global GameSpeed%=60
Global Screenwidth%=GraphicsWidth()
Global Screenheight%=GraphicsHeight()
Global FramePeriod%=1000/GameSpeed
Global FrameTime%=MilliSecs()-FramePeriod
Global DeltaTimeOld%

; Scene Objects
Global pivot%,dummy%,patch%,cam%,cyl%,light%,glow%,ground%,ring%,ring2%


Restore Temperate : CreateGradient(11,MAXCOLS,True,RT,GT,BT)
Restore Wood : CreateGradient(32,MAXCOLS,True,RW,GW,BW)
InitScene()


Dim HeightMap#(HEIGHTMAP_SIZE,HEIGHTMAP_SIZE)
Dim NoiseMap#(HEIGHTMAP_SIZE+1,HEIGHTMAP_SIZE+1)

Generate_Heightmap(1.5,3,True,False)

tx1=CreateTexture(HEIGHTMAP_SIZE,HEIGHTMAP_SIZE)
tx2=CreateTexture(HEIGHTMAP_SIZE,HEIGHTMAP_SIZE)
buffer1=TextureBuffer(tx1)
buffer2=TextureBuffer(tx2)
LockBuffer buffer1
LockBuffer buffer2
For x=0 To HEIGHTMAP_SIZE-1
	For y=0 To HEIGHTMAP_SIZE-1
		
		h=Int(Norm(HeightMap(x,HEIGHTMAP_SIZE-y),Minh,Maxh,0,MAXCOLS))
		WritePixelFast x,y,RW[h]*$10000+GW[h]*$100+BW[h],buffer1
		WritePixelFast x,y,RW[h]*$10000+GW[h]*$100+BW[h],buffer2
		
	Next
Next
UnlockBuffer buffer2
UnlockBuffer buffer1

EntityTexture cyl,tx1,0,2
EntityTexture ground,tx2,0,2
EntityTexture ring,tx2,0,2
EntityTexture ring2,tx2,0,2
ScaleTexture tx1,1,4
ScaleTexture tx2,1,1

TextureBlend tx1,2
TextureBlend tx2,2

ntex1=CreateNormalTexture(0,128,64,True)
EntityTexture cyl,ntex1,0,1
TextureBlend ntex1,4
ScaleTexture ntex1,1,4

ntex2=CreateNormalTexture(0,128,64,True)
EntityTexture ground,ntex2,0,1
EntityTexture ring,ntex2,0,1
EntityTexture ring2,ntex2,0,1
TextureBlend ntex2,4
ScaleTexture ntex2,1,1

mx=195 : my=198 : mz=190
EntityColor cyl,mx,my,mz
EntityColor ground,mx,my,mz
EntityColor ring,mx,my,mz
EntityColor ring2,mx,my,mz


mesh=CreateMesh()
EntityFX mesh,1+2+16+32

stex=CreateSnowTexture()
EntityTexture mesh,stex
TextureBlend stex,5

For i=1 To SNOWFLAKES
	
	s.snow = New snow
	s\entity=CreatePivot()
	s\size=Rnd(0.005,0.01)
	
	If Rnd(1)<0.25 Then s\distance=Rnd(Rnd(0.1,0.8),Rnd(0.8,1.0)) Else s\distance=Rnd(0.8,0.99)
	s\speed_p=Rnd(-0.01,0.01)
	s\speed_y=Rnd(-0.01,0.01)
	s\speed_r=Rnd(-0.01,0.01)
	
	RotateEntity s\entity,Rnd(-Rnd(0,90),Rnd(0,90)),Rnd(0,360),Rnd(0,360)
	
Next

.start

Minh#=2^16
Maxh#=0

Generate_Heightmap(1.5,Rnd(2.5,3),False,True)
InitDiscWorld()

MoveMouse Screenwidth/2,Screenheight/2

quad=CreateQuad()
HideEntity quad

Collisions 1,2,2,3

While Not KeyHit(1)
	
	; Frametween calculation
	Local FrameElapsed%,FrameTicks%,FrameTween#,t%
	Repeat FrameElapsed=MilliSecs()-FrameTime Until FrameElapsed
	FrameTicks=FrameElapsed/FramePeriod
	FrameTween=Float(FrameElapsed Mod FramePeriod)/Float(FramePeriod)
	
	; Frametween loop
	For t=1 To FrameTicks
		
		; Frametween Captureworld
		FrameTime=FrameTime+FramePeriod : If t=FrameTicks Then CaptureWorld
		
		; SPACE = Wireframe / ENTER = New Discworld
		If KeyHit(57) Then wf=1-wf : WireFrame wf
		
		
		If KeyHit(28)  Then
			keepcam=True
			camx=EntityX(cam)
			camy=EntityY(cam)
			camz=EntityZ(cam)
			camp=EntityPitch(cam)
			camw=EntityYaw(cam)
			camr=EntityRoll(cam)
			FreeEntity patch
			Goto start
		EndIf
		
		;Movement()
		FreeCam(cam,85,0.01)
		
		FreeEntity mesh
		mesh=CreateMesh()
		EntityFX mesh,1+2+16+32
		EntityTexture mesh,stex
		EntityBlend mesh,3
		
		For s.snow = Each snow
			
			s\speed_p=s\speed_p+Rnd(-0.01,0.01)
			s\speed_y=s\speed_y+Rnd(-0.01,0.01)
			s\speed_r=s\speed_r+Rnd(-0.01,0.01)
			
			PositionEntity s\entity,0,0,0
			
			TurnEntity s\entity,s\speed_p,s\speed_y,s\speed_r
			MoveEntity s\entity,0,0,s\distance
			
			PositionEntity quad,EntityX(s\entity),EntityY(s\entity),EntityZ(s\entity)
			ScaleEntity quad,s\size,s\size,s\size
			PointEntity quad,cam
			
			If EntityInView(quad,cam) Then AddToMesh(quad,mesh)
			
		Next
		
		UpdateWorld
		
	Next
	
	RenderWorld FrameTween
	
	AppTitle "Discworld | Tris: "+TrisRendered()
	
	Flip 0
	
Wend

End

Function CreateSnowTexture()
	
	Local tex%=CreateTexture(512,512)
	Local tb%=TextureBuffer(tex)
	
	Local i#,j%,col%,rgb%
	
	SetBuffer tb
	
	LockBuffer tb
	
	; Intensity steps
	For j=0 To 255
		
		col=255-(1.0/Exp(j*0.00001)*j)
		If col>255 Then col=255
		
		rgb=col*$10000+col*$100+col
		
		; Draw circles
		For i=0 To 360 Step 0.1
			WritePixelFast 256+(Sin(i)*j),256+(Cos(i)*j),rgb,tb
		Next
		
	Next
	
	UnlockBuffer tb
	SetBuffer BackBuffer()
	
	Return tex
	
End Function

Function CreateQuad(r%=255,g%=255,b%=255,alpha#=1.0,fx%=0,centered%=False)
	
	Local mesh%,surface%,v1%,v2%,v3%,v4%,s#
	
	If centered Then s#=0.5 Else s#=1.0
	
	mesh=CreateMesh()
	surface=CreateSurface(mesh)
	
	v1=AddVertex (surface,-s, s,0,1,0)
	v2=AddVertex (surface, s, s,0,0,0)
	v3=AddVertex (surface,-s,-s,0,1,1)
	v4=AddVertex (surface, s,-s,0,0,1)
	
	VertexColor surface,v1,r,g,b,alpha
	VertexColor surface,v3,r,g,b,alpha
	VertexColor surface,v2,r,g,b,alpha
	VertexColor surface,v4,r,g,b,alpha
	
	AddTriangle(surface,0,1,2)
	AddTriangle(surface,3,2,1)
	
	EntityFX mesh,fx
	
	FlipMesh mesh
	
	Return mesh
	
End Function

Function AddToMesh(source%,target%)
	
	Local vert%[2]
	Local oldvert%,i1%,i2%,v1%,v2%
	Local surf%,surf1%=GetSurface(source,1),surf2%
	Local r%,g%,b%,a#
	
	v1=CountVertices(surf1)
	For v2=1 To CountSurfaces(target)
		
		surf=GetSurface(target,v2)
		If CountVertices(surf)+v1<64000 Then surf2=surf : Goto skip
		
	Next
	
	surf2=CreateSurface(target)
	
	.skip
	
	For i1=0 To CountTriangles(surf1)-1
		
		For i2=0 To 2
			
			oldvert = TriangleVertex(surf1,i1,i2)
			
			r=VertexRed(surf1,oldvert)
			g=VertexGreen(surf1,oldvert)
			b=VertexBlue(surf1,oldvert)
			a=VertexAlpha(surf1,oldvert)
			
			TFormPoint VertexX(surf1,oldvert),VertexY(surf1,oldvert),VertexZ(surf1,oldvert),source,target
			vert[i2]=AddVertex(surf2,TFormedX(),TFormedY(),TFormedZ(),VertexU(surf1,oldvert),VertexV(surf1,oldvert))
			VertexColor surf2,vert[i2],r,g,b,a
			VertexNormal surf2,vert[i2],VertexNX(surf1,oldvert),VertexNY(surf1,oldvert),VertexNZ(surf1,oldvert)
			
		Next
		
		AddTriangle(surf2,vert[0],vert[1],vert[2])
		
	Next
	
End Function

Function CreateNormalTexture(flag%=0,height#=128.0,factor#=256.0,inverse%=False,wrap%=False)
	
	Local x%,y%
	Local xm1%,xp1%,ym1%,yp1%
	Local tl%,tm%,tr%,ml%,mm%,mr%,bl%,bm%,br%
	Local vx#,vy#,vz#
	Local isq2#,sum#
	Local al#,ar#,at#,ab#
	Local m#,r%,g%,b%
	
	Local w%=HEIGHTMAP_SIZE
	Local h%=HEIGHTMAP_SIZE
	
	Local texture%=CreateTexture(w,h,flag)
	Local buffer%=TextureBuffer(texture)
	
	SetBuffer buffer
	LockBuffer buffer
	
	For y = 0 To h-1
		
		; wrap vertical
		If wrap Then
			
			ym1=y-1 : If ym1<0 Then ym1=h-1
			yp1=y+1 : If yp1>h-1 Then yp1=0
			
		Else
			
			ym1=y-1 : If ym1<0 Then ym1=0
			yp1=y+1 : If yp1>h-1 Then yp1=h-1
			
		EndIf
		
		For x = 0 To w-1
			
			; wrap horizontal
			If wrap Then
				
				xm1=x-1 : If xm1<0 Then xm1=w-1
				xp1=x+1 : If xp1>w-1 Then xp1=0
				
			Else
				
				xm1=x-1 : If xm1<0 Then xm1=0
				xp1=x+1 : If xp1>w-1 Then xp1=w-1
				
			EndIf
			
			; get central and surrounding pixels
			tl=HeightMap(xm1,ym1)
			tm=HeightMap(x  ,ym1)
			tr=HeightMap(xp1,ym1)
			ml=HeightMap(xm1,y  )
			mm=HeightMap(x  ,y  )
			mr=HeightMap(xp1,y  )
			bl=HeightMap(xm1,yp1)
			bm=HeightMap(x  ,yp1)
			br=HeightMap(xp1,yp1)
			
			isq2=1.0/Sqr(2.0)
			sum=1.0+isq2+isq2
			
			al=(tl*isq2+ml+bl*isq2)/sum
			ar=(tr*isq2+mr+br*isq2)/sum
			at=(tl*isq2+tm+tr*isq2)/sum
			ab=(bl*isq2+bm+br*isq2)/sum			
			
			; inverse normalmap
			If inverse Then
				vx=(al-ar)/((255.0*factor))
				vy=(at-ab)/((255.0*factor))
			Else
				vx=(ar-al)/((255.0*factor))
				vy=(ab-at)/((255.0*factor))
			EndIf
			
			m=Max(0,vx*vx+vy*vy)
			m=Min(m,1.0)
			
			vz=Sqr(1.0-m) 
			
			If height<>0.0
				
				vz=vz/height
				m#=Sqr(vx*vx+vy*vy+vz*vz)
				vx=vx/m
				vy=vy/m
				vz=vz/m
				
			EndIf
			
			; calculate colors
			r=Int(Floor(vx*127.5+127.5+0.5))
			g=Int(Floor(vy*127.5+127.5+0.5))
			b=Int(Floor(vz*127.5+127.5+0.5))
			
			; write map
			WritePixelFast(x,y,(r Shl 16)+(g Shl 8)+b)
			
		Next
		
	Next
	
	UnlockBuffer buffer
	SetBuffer BackBuffer()
	
	Return texture
	
End Function

Function Min#(v1#,v2#)
	
	If v1<v2 Then Return v1 Else Return v2
	
End Function


; returns the max value of two values
Function Max#(v1#,v2#)
	
	If v1>v2 Then Return v1 Else Return v2
	
End Function

Function FreeCam(camera%,maxpitch#=85.0,movespeed#,rotspeed#=16.666,rotfloat#=8.0)
	
	Local movex#,movez#,dx#,dy#,dk#,dt%,t%
	Local pitch#
	
	; Arrows = Move
	movex=KeyDown(205)-KeyDown(203)
	movez=KeyDown(200)-KeyDown(208)
	
	; smooth movement
	t=MilliSecs() : dt=t-DeltaTimeOld : DeltaTimeOld=t : dk=Float(dt)/rotspeed
	dx=(Screenwidth/2-MouseX())*0.01*dk : dy=(Screenheight/2-MouseY())*0.01*dk
	TurnEntity camera,-dy,dx*0.1*dk*rotfloat,0
	
	; limit pitch
	pitch=EntityPitch(camera,1) : If pitch>maxpitch Then pitch=maxpitch Else If pitch<-maxpitch Then pitch=-maxpitch
	
	; rotate and move
	RotateEntity camera,pitch,EntityYaw(camera,1),0,1	
	MoveEntity camera,movex*movespeed,0,movez*movespeed
	
End Function

Function InitScene()
	
	; Pivots
	pivot=CreatePivot() : MoveEntity pivot,0,-0.15,0
	dummy=CreatePivot()
	
	glow=InitGlow(0.25,0.95)
	EntityParent glow,dummy
	
	cyl=CreateCylinder(DETAIL,0,dummy)
	EntityFX cyl,2+16
	ScaleEntity cyl,0.9,0.25,0.9
	PositionEntity cyl,0,-0.69,0
	EntityType cyl,2
	
	ring=CreateTorus(0.875,0.025,DETAIL,16)
	RotateMesh ring,90,0,0
	PositionEntity ring,0,-0.45,0
	
	ring2=CreateTorus(0.9,0.025,DETAIL,16)
	RotateMesh ring2,90,0,0
	PositionEntity ring2,0,-0.95,0
	ScaleMesh ring2,1,2,1
	
	ground=CreateCylinder(DETAIL,1,dummy)
	ScaleEntity ground,0.92,0.01,0.92
	PositionEntity ground,0,-0.95,0
	EntityFX ground,2+16
	EntityType ground,2
	
	light=CreateLight(2,glow)
	PositionEntity light,-100,150,50
	LightRange light,200
	AmbientLight 64,64,64
	
	cam=CreateCamera()
	CameraRange cam,0.01,1000
	CameraClsColor cam,75,100,128
	EntityType cam,1
	EntityRadius cam,0.05
	
	If keepcam Then
		PositionEntity cam,camx,camy,camz
		RotateEntity cam,camp,camw,camr
	Else
		MoveEntity cam,0,0.5,2
		PointEntity cam,pivot
	EndIf
	
	
	
End Function

Function InitDiscWorld()
	
	patch=CreatePatch(PATCHSIZE-1,1.0/((PATCHSIZE-1)/2),0,0,0,128,128,128,1,1)
	RotateEntity patch,90,0,0
	EntityParent patch,dummy
	
	surf=GetSurface(patch,1)
	
	tx=CreateTexture(HEIGHTMAP_SIZE,HEIGHTMAP_SIZE)
	buffer=TextureBuffer(tx)
	LockBuffer buffer
	For x=0 To HEIGHTMAP_SIZE-1
		For y=0 To HEIGHTMAP_SIZE-1
			
			h=Int(Norm(HeightMap(x,HEIGHTMAP_SIZE-y),Minh,Maxh,0,MAXCOLS))
			WritePixelFast x,y,RT[h]*$10000+GT[h]*$100+BT[h],buffer
			
		Next
	Next
	UnlockBuffer buffer
	
	EntityTexture patch,tx,0,2
	TextureBlend tx,5
	
	ntex=CreateNormalTexture(0,128,64)
	EntityTexture patch,ntex,0,1
	TextureBlend ntex,4
	
	mx=195 : my=198 : mz=190
	EntityColor patch,mx,my,mz
	
	For v=0 To CountVertices(surf)-1
		
		y=Int(Floor(v*1.0/PATCHSIZE))
		x=v-(y*PATCHSIZE)
		
		vx#=VertexX(surf,v)
		vy#=VertexY(surf,v)
		vz#=Norm(HeightMap(x*(HEIGHTMAP_SIZE/PATCHSIZE),y*(HEIGHTMAP_SIZE/PATCHSIZE)),Minh,Maxh,0,0.5)
		If vz<0.25 Then vz=0.25
		
		c=Int(Norm(HeightMap(x*(HEIGHTMAP_SIZE/PATCHSIZE),y*(HEIGHTMAP_SIZE/PATCHSIZE)),Minh,Maxh,128,255))
		
		VertexColor surf,v,c,c,c
		
		VertexCoords surf,v,Cube2SphereX(vx,vy,vz),Cube2SphereY(vx,vy,vz),(Cube2SphereZ(vx,vy,vz)*-1)+0.7
		
	Next
	
	ScaleMesh patch,0.88,0.88,0.88
	
	UpdateNormals patch
	
End Function

Function CreateTorus(torrad#,torwidth#,segments,sides,parent=0)
	
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


Function CreateGlowTexture(size%=128)
	
	Local tex%=CreateTexture(size,size,64)
	Local tb%=TextureBuffer(tex)
	
	Local i#,j%,col%,rgb%,px%,py%
	
	SetBuffer tb
	
	Color 255,255,255
	Rect 0,0,size,size,1
	
	LockBuffer tb
	
	; Intensity steps
	For j=0 To (size/2)-1
		
		col=(1.5-(1.5/Exp(j*1.0/(size/2-1))))*j*(512.0/size)
		If col>255 Then col=255
		If col<0 Then col=0
		rgb=col*$1000000+col*$10000+col*$100+col
		
		; Draw circles
		For i=0 To 359.95 Step 0.05
			px=(size/2.0)-1+(Sin(i)*(j+0.5))+0.5
			py=(size/2.0)-1+(Cos(i)*(j+0.5))+0.5
			
			WritePixelFast px,py,rgb,tb
		Next
		
	Next
	
	UnlockBuffer tb
	SetBuffer BackBuffer()
	
	Return tex
	
End Function

Function InitGlow(shininess#,glowalpha#)
	
	Local mesh%=CreateSphere(DETAIL)
	
	tex=CreateGlowTexture()
	
	EntityBlend mesh,3
	
	EntityTexture mesh,tex,0,1
	
	EntityFX mesh,2+32
	EntityShininess mesh,shininess
	EntityType mesh,2
	
	UpdateMesh(mesh,100,150,255,glowalpha)
	ScaleMesh mesh,0.999,0.999,0.999
	
	FreeTexture tex
	
	Return mesh
	
End Function

Function UpdateMesh(mesh%,r%=255,g%=255,b%=255,a#=1.0)
	
	Local v%,a1#
	Local surf%=GetSurface(mesh,1)
	
	For v=0 To CountVertices(surf)-1
		
		If VertexY(surf,v)<=-0.5 Then a1=0 Else a1=a
		
		VertexColor surf,v,r,g,b,a1
	Next
	
End Function

Function Generate_Heightmap(Scale#,Multiplier#,wrap%=False,island%=False)
	
	Local Max_Height#,NoiseMapSize%,ScaleDifference#,StepSize#
	Local N1#,N2#,N3#,N4#,HX#,HY#,IX#,IY#,ICX#,ICY#,NA#,NB#,NC#,ND#
	Local i%,x%,y%,xx%,yy%
	Local v#
	
	Max_Height=Scale
	
	For y=0 To HEIGHTMAP_SIZE Step 1
		
		For x=0 To HEIGHTMAP_SIZE Step 1
			
			HeightMap(x,y)=0
			
		Next
		
	Next
	
	NoiseMapSize=HEIGHTMAP_SIZE/2
	Max_Height=Max_Height*Multiplier
	
	Repeat
		
		For y=0 To NoiseMapSize
			
			For x=0 To NoiseMapSize
				
				NoiseMap(x,y)=Rnd(0,Max_Height#)
				
				If island Then If x=0 Or x=NoiseMapSize Or y=0 Or y=NoiseMapSize Then NoiseMap(x,y)=0
				
			Next
			
		Next
		
		If wrap Then
			
			For i=0 To NoiseMapSize : NoiseMap(i,0)=NoiseMap(i,NoiseMapSize) : Next
			For i=0 To NoiseMapSize : NoiseMap(0,i)=NoiseMap(NoiseMapSize,i) : Next
			
		EndIf
		
		ScaleDifference=HEIGHTMAP_SIZE*1.0/NoiseMapSize
		StepSize=1.0/Float(ScaleDifference)
		
		For y=0 To NoiseMapSize-1
			
			For x=0 To NoiseMapSize-1
				
				N1=NoiseMap(x,  y  )
				N2=NoiseMap(x+1,y  )
				N3=NoiseMap(x,  y+1)
				N4=NoiseMap(x+1,y+1)
				
				HX=x*ScaleDifference
				HY=y*ScaleDifference
				
				IY=0
				
				For yy=0 To ScaleDifference-1
					
					ICY=1.0-((Cos(IY*180.0)+1.0)/2.0)
					
					IX=0	
					
					For xx=0 To ScaleDifference-1
						
						ICX=1.0-((Cos(IX*180.0)+1.0)/2.0)
						
						NA=N1*(1.0-ICX)
						NB=N2*ICX
						NC=N3*(1.0-ICX)
						ND=N4*ICX
						
						v=HeightMap(HX+xx,HY+yy)+(NA+NB)*(1.0-ICY)+(NC+ND)*ICY
						
						If v>Maxh Then Maxh=v
						If v<Minh Then Minh=v
						
						HeightMap(HX+xx,HY+yy)=v
						
						IX=IX+StepSize
						
					Next
					
					IY=IY+StepSize	
					
				Next
				
			Next
			
		Next
		
		NoiseMapSize=NoiseMapSize/2
		
		Max_Height=Max_Height*Multiplier
		
	Until NoiseMapSize<=2
	
End Function



Function Cube2SphereX#(x#,y#,z#)
	
	Return x*Sqr(1.0-y*y*0.5-z*z*0.5+y*y*z*z*DIV3)
	
End Function

Function Cube2SphereY#(x#,y#,z#)

	Return y*Sqr(1.0-z*z*0.5-x*x*0.5+z*z*x*x*DIV3)

End Function

Function Cube2SphereZ#(x#,y#,z#)
	
	Return z*Sqr(1.0-x*x*0.5-y*y*0.5+x*x*y*y*DIV3)
	
End Function

Function CreatePatch(size%,scale#,px#,py#,pz#,r%,g%,b%,a#,fx%)
	
	Local x%,z%,v#,u#,v0%,v1%,v2%,v3%
	
	; create mesh and surface
	Local mesh%=CreateMesh()
	Local surf%=CreateSurface(mesh)
	
	For z=0 To size
		
		For x=0 To size
			
			; calculate uv coordinates that the texture fits to the tile
			u=x*1.0/size
			v=z*1.0/size*-1
			
			; set vertexposition
			VertexBuffer(x,z)=AddVertex (surf,-((size)/2.0)+x,-((size)/2.0)+z,size/2,u,v)
			VertexColor surf,VertexBuffer(x,z),r,g,b,a#
			
		Next
		
	Next
	
	; set triangles
	For z=0 To size-1
		
		For x=0 To size-1
			
			v0=VertexBuffer(x,z)
			v1=VertexBuffer(x+1,z)
			v2=VertexBuffer(x+1,z+1)
			v3=VertexBuffer(x,z+1)
			
			AddTriangle (surf,v0,v2,v1)
			AddTriangle (surf,v0,v3,v2)
			
		Next
		
	Next
	
	; position, scale and fx
	PositionEntity mesh,px,py,pz
	ScaleMesh mesh,scale,scale,scale
	EntityFX mesh,fx
	
	Return mesh
	
End Function

Function CreateGradient(colors%,steps%,inverse=False,R%[MAXCOLS],G%[MAXCOLS],B%[MAXCOLS])
	
	Dim Percent#(colors),Red%(colors),Green%(colors),Blue%(colors)
	
	Local i%,pos1%,pos2%,pdiff%
	Local rdiff%,gdiff%,bdiff%
	Local rstep#,gstep#,bstep#
	Local counter%=0
	
	If inverse Then
		
		For i=colors To 1 Step -1
			
			Read Percent(i),Red(i),Green(i),Blue(i)
			Percent(i)=100.0-Percent(i)
			
		Next
		
	Else
		
		For i=0 To colors-1 : Read Percent(i),Red(i),Green(i),Blue(i) : Next
		
	EndIf
	
    While counter<colors
		
        pos1=Percent(counter)*steps*1.0/100
		pos2=Percent(counter+1)*steps*1.0/100
		
        pdiff=pos2-pos1
		
        rdiff%=Red(counter)-Red(counter+1)
		gdiff%=Green(counter)-Green(counter+1)
		bdiff%=Blue(counter)-Blue(counter+1)
		
        rstep#=rdiff*1.0/pdiff
		gstep#=gdiff*1.0/pdiff
		bstep#=bdiff*1.0/pdiff
		
		For i=0 To pdiff
			
			R[pos1+i]=Int(Red(counter)-(rstep*i))
			G[pos1+i]=Int(Green(counter)-(gstep*i))
			B[pos1+i]=Int(Blue(counter)-(bstep*i))
			
		Next
		
        counter=counter+1
		
	Wend
	
End Function

Function Norm#(v#=128.0,vmin#=0.0,vmax#=255.0,nmin#=0.0,nmax#=1.0)
	
	Return ((v-vmin)/(vmax-vmin))*(nmax-nmin)+nmin
	
End Function


.Temperate
Data   0.0,255,255,255	; icy mountains
Data   5.0,179,179,179	; transition
Data  10.0,153,143, 92	; tundra
Data  25.0,115,128, 77	; high grasslands
Data  45.0, 42,102, 41	; low grasslands
Data  48.0, 42,102, 41	; low grasslands
Data  50.0,200,200,118	; coast / should be a 0 height
Data  53.0, 17, 82,112	; shallow ocean
Data  65.0, 17, 82,112	; shallow ocean
Data  75.0,  9, 62, 92	; ocean
Data 100.0,  9, 62, 92	; deep ocean

.Wood
Data 0.0,127,79,39
Data 3.22581,129,77,37
Data 6.45161,134,86,46
Data 9.67742,155,105,64
Data 12.9032,126,77,34
Data 16.129,145,95,55
Data 19.3548,110,65,29
Data 22.5806,135,83,43
Data 25.8065,117,69,29
Data 29.0323,128,84,49
Data 32.2581,121,71,29
Data 35.4839,145,93,50
Data 38.7097,164,112,66
Data 41.9355,97,57,23
Data 45.1613,130,76,30
Data 48.3871,129,81,44
Data 51.6129,135,81,37
Data 54.8387,131,79,37
Data 58.0645,140,93,51
Data 61.2903,155,105,64
Data 64.5161,129,75,34
Data 67.7419,145,95,55
Data 70.9677,107,63,28
Data 74.1936,134,86,46
Data 77.4194,135,83,43
Data 80.6452,126,80,46
Data 83.871,107,57,15
Data 87.0968,133,81,41
Data 90.3226,114,66,30
Data 93.5484,114,66,26
Data 96.7742,128,77,27
Data 100.0,126,78,42
