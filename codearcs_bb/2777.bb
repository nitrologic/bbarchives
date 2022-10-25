; ID: 2777
; Author: Krischan
; Date: 2010-10-12 09:40:57
; Title: 3D Single Surface Spiral Galaxy
; Description: Creates a 3D Spiral Galaxy using a Single Surface algorithm

Graphics3D 800,600,32,2

SetBuffer BackBuffer()

Const TurnSpeed#    = 4.000        ; cam turn speed
Const RollSpeed#    = 0.500        ; cam roll speed
Const CameraSpeed#  = 0.005        ; cam move speed
Const Stars%        = 24000        ; number of stars
Const Spiralarms%   = 4            ; number of spiral arms
Const Spread#       = 20.0         ; star spread
Const Rotation#     = 4.0          ; how many spiral rotations
Const Range#        = 100.0        ; milkyway radius

Global WIDTH%=GraphicsWidth()
Global HEIGHT%=GraphicsHeight()
Global TIMER%=CreateTimer(60)
Global Scale#=WIDTH/3.0            ; star scale

Global total%,vis%

Local cam%,galaxy%,tex%

Type quad
	
	Field surf%
	Field x#,y#,z#
	Field v%
	Field scale#
	Field r%,g%,b%
	
End Type

; init galaxy
galaxy=InitGalaxy(Stars,Spiralarms,Spread,Rotation,Range)
EntityFX galaxy,1+2
EntityBlend galaxy,3
tex=CreateSunTexture(256,128,128,128)
TextureBlend tex,3
EntityTexture galaxy,tex

; init camera
cam=CreateCamera()
CameraRange cam,0.1,1000
PositionEntity cam,0,90,150
PointEntity cam,galaxy

MoveMouse WIDTH/2,HEIGHT/2

; main loop
While Not KeyHit(1)
	
	Local ms%,me%
	
	Movement(cam)
	
	ms=MilliSecs()
	UpdateGalaxy(cam,Scale)
	me=MilliSecs()-ms
	
	RenderWorld
	
	WaitTimer TIMER
	
	AppTitle vis+" Stars visible ["+total+" Stars total] "+me+"ms"
	
	Flip 0
	
Wend

End

; update quads in single surface mesh
Function UpdateGalaxy(cam%,scale#=256.0)
	
	Local cx#=EntityX(cam)
	Local cy#=EntityY(cam)
	Local cz#=EntityZ(cam)
	
	Local q.quad,d#,s#
	
	total=0
	vis=0
	
	For q.quad = Each quad
		
		; check if star is visible
		TFormPoint q\x,q\y,q\z,0,cam
		
		; in front of cam?
		If TFormedZ()>0 Then
		
			; adaptive size
			d#=Distance3D(q\x,q\y,q\z,cx,cy,cz)
			s#=(q\scale/d)+(d/scale)
			If s<d/scale Then s=d/scale
		
			; align single surface quads to cam
			UpdateQuad(q.quad,s,cam)
			
			vis=vis+1
		
		EndIf
		
		total=total+1
		
	Next
	
End Function

; create galaxy
Function InitGalaxy%(stars%=10000,arms%=4,spread#=40.0,rot#=2.0,range#=100.0)
	
	Local mesh%=CreateMesh()
	Local surf%=CreateSurface(mesh)
	Local q.quad
	Local angle#,dist#,turb#
	Local i%,col#,multi#,lum#,bulge%,counter%
	
	For i=1 To stars
		
		q.quad = New quad
		
		; color
		col=Rnd(1)
		If col>0.90 And col<=1.00 Then q\r=255 : q\g=  0 : q\b=  0
		If col>0.70 And col<=0.90 Then q\r=255 : q\g=255 : q\b=  0
		If col>0.50 And col<=0.70 Then q\r=  0 : q\g=  0 : q\b=255
		If col>0.00 And col<=0.50 Then q\r=255 : q\g=255 : q\b=255
		
		; angle
		angle=Int(Floor(i*1.0/(stars/arms)))*(360.0/arms)
		
		; center / arm relation
		If Rnd(1)>0.5 Then multi=Rnd(0.1,1) Else multi=Rnd(1,2)
		
		; distance and turbulence
		dist=Rnd(0,range)*Rnd(1,Rnd(Rnd(multi)))
		turb=Rnd(0,Rnd(spread)) : If Rnd(1)>0.5 Then turb=-turb
		
		; more red/yellow stars in bulge
		lum=Rnd(1)
		If dist<range/2*lum Then
			
			If lum>0.75 Then
				
				q\r=255 : q\g=0 : q\b=0		; red stars
				
			Else If lum>0.5 Then
				
				q\r=255 : q\g=255 : q\b=0	; yellow stars
				
			EndIf
			
		Else
			
			If lum>0.75 Then
				
				q\r=0 : q\g=0 : q\b=255		; blue stars
				
			Else If lum>0.5 Then
				
				q\r=255 : q\g=255 : q\b=255	; white stars
				
			EndIf
			
		EndIf
		
		; star position x/z
		q\x=dist*Cos(angle+(dist*rot))+Rnd(Rnd(Rnd(-spread)),Rnd(Rnd(spread)))
		q\z=dist*Sin(angle+(dist*rot))+Rnd(Rnd(Rnd(-spread)),Rnd(Rnd(spread)))
		
		; star position y
		bulge=Normalize(Distance2D(q\x,q\z,0,0),0,range/2.0,0,180)/2.0
		If bulge>90 Then bulge=90
		q\y=(Cos(bulge)*Rnd(Rnd(-spread),Rnd(spread))/2.0)+(turb/10.0)
		
		; scale
		q\scale=Rnd(0.01,Rnd(0.02,Rnd(0.04,0.08)))
		
		; create new surface if too many vertices
		If counter+4>32000 Then surf=CreateSurface(mesh) : counter=0
		q\surf=surf
		
		; add vertices
		q\v=AddVertex(q\surf,0,0,0,0,0)
		AddVertex(q\surf,0,0,0,1,0)
		AddVertex(q\surf,0,0,0,1,1)
		AddVertex(q\surf,0,0,0,0,1)
		
		; color vertices
		VertexColor q\surf,q\v,q\r,q\g,q\b
		VertexColor q\surf,q\v+1,q\r,q\g,q\b
		VertexColor q\surf,q\v+2,q\r,q\g,q\b
		VertexColor q\surf,q\v+3,q\r,q\g,q\b
		
		; add triangles
		AddTriangle(q\surf,q\v,q\v+1,q\v+2)
		AddTriangle(q\surf,q\v,q\v+2,q\v+3)
		
		; vertex counter
		counter=counter+4
		
	Next
	
	Return mesh
	
End Function

; align single surface quad to cam
Function UpdateQuad(q.quad,s#,target%)
	
	Local x1#,y1#,z1#,x2#,y2#,z2#
	
	TFormVector -s,0,0,target,0
	x1=TFormedX()
	y1=TFormedY()
	z1=TFormedZ()
	
	TFormVector 0,-s,0,target,0
	x2=TFormedX()
	y2=TFormedY()
	z2=TFormedZ()
	
	VertexCoords q\surf,q\v+0,q\x-x1-x2,q\y-y1-y2,q\z-z1-z2
	VertexCoords q\surf,q\v+1,q\x-x1+x2,q\y-y1+y2,q\z-z1+z2
	VertexCoords q\surf,q\v+2,q\x+x1+x2,q\y+y1+y2,q\z+z1+z2
	VertexCoords q\surf,q\v+3,q\x+x1-x2,q\y+y1-y2,q\z+z1-z2
	
End Function

; calculate 2D Distance
Function Distance2D#(x1#,y1#,x2#,y2#)
	
	Local x#=x1-x2
	Local y#=y1-y2
	
	Return Sqr((x*x)+(y*y))
	
End Function

; calucate 3D Distance
Function Distance3D#(x1#,y1#,z1#,x2#,y2#,z2#)
	
	Local x#=x1-x2
	Local y#=y1-y2
	Local z#=z1-z2
	
	Return Sqr((x*x)+(y*y)+(z*z))
	
End Function

; normalize a value
Function Normalize#(value#=128.0,vmin#=0.0,vmax#=255.0,nmin#=0.0,nmax#=1.0)
    
    Return ((value#-vmin#)/(vmax#-vmin#))*(nmax#-nmin#)+nmin#
    
End Function

; camera movement
Function Movement(cam%,sensitivity#=1.0)
    
    Local roll#,cz#,tx#,ty#,multi%=1
    
    ; arrows = move / LMB = Turbo / RMB = Lightspeed / LMB+RMB = incredible speed
    cz=(KeyDown(200)-KeyDown(208))*CameraSpeed
	roll=(KeyDown(203)-KeyDown(205))*RollSpeed
    If MouseDown(1) Then multi=10
	If MouseDown(2) Then multi=multi*10
    
    tx=Normalize(MouseX(),0,WIDTH , 1,-1)
    ty=Normalize(MouseY(),0,HEIGHT,-1, 1)
    
    If ty<0 Then ty=(Abs(ty)^sensitivity)*-1 Else ty=ty^sensitivity
    If tx<0 Then tx=(Abs(tx)^sensitivity)*-1 Else tx=tx^sensitivity
    
    TurnEntity cam,ty*TurnSpeed,tx*TurnSpeed,roll*TurnSpeed
    MoveEntity cam,0,0,cz*multi
	
End Function

; create a stunning sun texture
Function CreateSunTexture(size%=512,r%=255,g%=255,b%=255)
	
	Local tex%=CreateTexture(size,size,3)
	Local tb%=TextureBuffer(tex)
	
	Local i#,j%,col%,rgb%
	Local x%,y%,xx%,yy%
	Local a%
	
	LockBuffer tb
	
	For j=0 To (size/2)-1
		
		col=255-Normalize(j,0,(size/2.0)-1,0,255)
		If col>255 Then col=255
		rgb=col*$1000000+col*$10000+col*$100+col
		
		For i=0 To 360 Step 0.05
			
			WritePixelFast (size/2)+(Sin(i)*j),(size/2)+(Cos(i)*j),rgb,tb
			
		Next
		
	Next
	
	UnlockBuffer tb
	
	; temp camera
	Local tempcam%=CreateCamera()
	CameraRange tempcam,1,WIDTH*2
	
	; temp pivot
	Local tempsun%=CreatePivot()
	
	; create 4 layers
	CreateQuad(tempsun,size/4.0,tex,3,1+8+16,r*1.00,g*1.00,b*1.00,1.00)
	CreateQuad(tempsun,size/1.5,tex,3,1+8+16,r*1.00,g*1.00,b*1.00,1.00)
	CreateQuad(tempsun,size/1.2,tex,3,1+8+16,r*0.75,g*0.75,b*0.50,0.75)
	CreateQuad(tempsun,size/1.0,tex,3,1+8+16,r*0.50,g*0.50,b*0.50,0.50)
	
	PositionEntity tempsun,0,0,WIDTH
    
	; render it
    RenderWorld
	
	; delete pivot
	FreeEntity tempsun
	
	LockBuffer BackBuffer()
	LockBuffer tb
	
	; grab image
	For x=0 To size-1
		
		For y=0 To size-1
			
			xx=(WIDTH/2)-(size/2)+x
			yy=(HEIGHT/2)-(size/2)+y
			
			rgb=ReadPixelFast(xx,yy,BackBuffer())
			
			r=(rgb And $ff0000)/$10000
			g=(rgb And $ff00)/$100
			b=(rgb And $ff)
			a=255
			
			; alpha
			If (r+g+b)/3 < 32 Then a=Normalize((r+g+b)/3,0,32,0,255)
			
			; rgb
			rgb=(r+g+b)/3*$1000000+r*$10000+g*$100+b
			
			WritePixelFast x,y,rgb,tb
			
		Next
		
	Next
	
	UnlockBuffer tb
	UnlockBuffer BackBuffer()
	
	RenderWorld
	
	; delete temp cam
	FreeEntity tempcam
	
	Return tex
	
End Function

; advanced quad creation
Function CreateQuad(parent%=False,scale#=1.0,tex%=False,blend%=False,fx%=False,r%=255,g%=255,b%=255,a#=1.0)
	
	Local mesh%=CreateMesh()
	Local surf%=CreateSurface(mesh)
	
	Local v0%=AddVertex(surf, 1, 1,0,0,0)
	Local v1%=AddVertex(surf,-1, 1,0,1,0)
	Local v2%=AddVertex(surf,-1,-1,0,1,1)
	Local v3%=AddVertex(surf, 1,-1,0,0,1)
	
	AddTriangle surf,v0,v1,v2
	AddTriangle surf,v0,v2,v3
	
	If parent Then EntityParent mesh,parent
	If fx Then EntityFX mesh,fx
	If tex Then EntityTexture mesh,tex
	If blend Then EntityBlend mesh,blend
	
	EntityColor mesh,r,g,b
	EntityAlpha mesh,a
	
	VertexColor surf,v0,r,g,b,a
	VertexColor surf,v1,r,g,b,a
	VertexColor surf,v2,r,g,b,a
	VertexColor surf,v3,r,g,b,a
	
	ScaleEntity mesh,scale,scale,scale
	
	Return mesh
	
End Function
