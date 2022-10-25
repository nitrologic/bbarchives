; ID: 2753
; Author: Krischan
; Date: 2010-08-17 04:30:27
; Title: Milkyway Panorama
; Description: Creates a stunning realistic lowpoly milkyway background object

Const StarSingleTexture$="star.png"		; single star Texture
Const StarFieldsTexture$="stars.png"	; background starfield texture
Const FogTexture$="nebula.jpg"			; 360° split panorama nebula texture

Const StarTexScale#=1.5					; texture scaling of background stars
Const PanoSegments%=32					; number of panorama segments (should be at least 8)

; -----------------------------------------------------------------------------
; Initialize
; -----------------------------------------------------------------------------
Function InitMilkyway(Stars%,Dist#,MinAngle#,MaxAngle#,Scale,R1%=0,G1%=0,B1%=0,A1#=0.0,R2%=0,G2%=0,B2%=0,A2#=0.0,R3%=0,G3%=0,B3%=0,A3#=0.0)
	
	Local MilkyWay%,StarBox%,FogPanorama%,StarSphere%
	Local StarTex%,StarsTex%,FogTex%
	
	MilkyWay=CreatePivot()
	
	; load textures
	StarTex=LoadTexture(StarSingleTexture,2)
	StarsTex=LoadTexture(StarFieldsTexture,2)
	FogTex=LoadTexture(FogTexture,16+32)
	ScaleTexture StarsTex,StarTexScale,StarTexScale
	
	; create stars
	StarBox=InitStarBox(Scale,StarsTex,1,0)
	FogPanorama=InitPanorama(FogTex,Scale,Scale*0.8,PanoSegments,1+2+32,R1,G1,B1,A1,R2,G2,B2,A2,R3,G3,B3,A3)
	StarSphere=InitStarSphere(StarTex,Stars,0.5*(Scale/100.0),0.75*(Scale/50.0),0.5,Dist,MinAngle,MaxAngle,Scale,1)
	
	EntityParent StarBox,MilkyWay
	EntityParent FogPanorama,MilkyWay
	EntityParent StarSphere,MilkyWay
	
	; reorder
	EntityOrder StarBox,3
	EntityOrder FogPanorama,2
	EntityOrder StarSphere,1
	
	; blending
	TextureBlend FogTex,5
	EntityBlend FogPanorama,3
	EntityBlend StarSphere,3
	;EntityBlend StarBox,3
	
	Return MilkyWay
	
End Function


; -----------------------------------------------------------------------------
; create milkyway nebula panorama
; -----------------------------------------------------------------------------
Function InitPanorama(texture%,radius#,h#,segmente,fx%=0,r1%=255,g1%=255,b1%=255,a1#=1.0,r2%=255,g2%=255,b2%=255,a2#=1.0,r3%=255,g3%=255,b3%=255,a3#=1.0)
	
	Local ang1#,ang2#,inc#,x1#,z1#,x2#,z2#
	Local uu1#,uu2#,uv1#,uv2#,uv3#
	Local v0%,v1%,v2%,v3%,v4%,v5%
	
	Local mesh=CreateMesh()
	Local surf=CreateSurface(mesh)
	
	inc=360.0/segmente
	
	ang1=0
	
	While ang1<360
		
		ang2=(ang1+inc) Mod 360
		
		x1=radius*Cos(ang1)
		z1=radius*Sin(ang1)
		x2=radius*Cos(ang2)
		z2=radius*Sin(ang2)
		
		If ang1<180 Then
			
			; use upper texture UV coordinates
			uu1=1.0-(ang1/180.0)
			uu2=1.0-(ang1+inc)/180.0
			uv1=0.50
			uv2=0.25
			uv3=0.00
			
		Else
			
			; use lower texture UV coordinates
			uu1=1-(((ang1/180.0))-1)
			uu2=1-(((ang1+inc)/180.0)-1)
			uv1=1.0
			uv2=0.75
			uv3=0.5
			
		EndIf
		
		v0=AddVertex(surf,x1,-h,z1,uu1,uv1) : VertexColor surf,v0,r1,g1,b1,a1
		v1=AddVertex(surf,x2,-h,z2,uu2,uv1) : VertexColor surf,v1,r1,g1,b1,a1
		v2=AddVertex(surf,x1, 0,z1,uu1,uv2) : VertexColor surf,v2,r2,g2,b2,a2
		v3=AddVertex(surf,x2, 0,z2,uu2,uv2) : VertexColor surf,v3,r2,g2,b2,a2
		v4=AddVertex(surf,x1, h,z1,uu1,uv3) : VertexColor surf,v4,r3,g3,b3,a3
		v5=AddVertex(surf,x2, h,z2,uu2,uv3) : VertexColor surf,v5,r3,g3,b3,a3
		
		AddTriangle surf,v0,v1,v3
		AddTriangle surf,v3,v2,v0
		AddTriangle surf,v2,v5,v4
		AddTriangle surf,v5,v2,v3
		
		ang1=ang1+inc
		
	Wend
	
	EntityFX mesh,fx
	
	EntityTexture mesh,texture
	
	Return mesh
	
End Function


; -----------------------------------------------------------------------------
; create starsphere
; -----------------------------------------------------------------------------
Function InitStarSphere(texture%,stars%,min#,max#,fix#,centered#,centermin#,centermax#,range#,scale#=1.0)
	
	Local star%,mesh%,surf%
	Local i%,size#,a#
	Local r%,g%,b%
	Local col#,maxy#
	
	star=CreateStarQuad()
	
	mesh=CreateMesh()
	surf=CreateSurface(mesh)
	EntityFX mesh,1+2
	
	For i=1 To stars
		
		; create new surface if there are too many stars
		If CountVertices(surf)>=65532 Then surf=CreateSurface(mesh)
		
		; size
		size=min*scale
		If Rnd(1)>fix Then size=Rnd(min,max)*scale
		ScaleEntity star,size,size,size
		
		; reset quad
		PositionEntity star,0,0,0
		
		; more stars centered or random?
		If Rnd(1)>centered Then maxy=centermin Else maxy=centermax
		
		; turn and move starquad
		TurnEntity star,Rnd(-Rnd(0,maxy),Rnd(0,maxy)),Rnd(0,Rnd(0,360)),0
		MoveEntity star,0,0,range*scale
		PointEntity star,mesh
		
		; color
		col=Rnd(1)
		If col>0.75 And col<=1.00 Then r=255 : g=224 : b=192	; 25% orange stars
		If col>0.50 And col<=0.75 Then r=255 : g=255 : b=192	; 25% yellow stars
		If col>0.25 And col<=0.50 Then r=192 : g=224 : b=255	; 25% blue stars
		If col>0.00 And col<=0.25 Then r=255 : g=255 : b=255	; 25% white stars
		
		; alpha
		If size>min*scale Then a#=1.0 Else a#=Rnd(0.5,1)
		
		; add to single surface mesh
		AddToSurface(star,surf,mesh,r,g,b,a)
		
	Next
	
	FreeEntity star
	
	EntityTexture mesh,texture
	
	FlipMesh mesh
	
	Return mesh
	
End Function


; -----------------------------------------------------------------------------
; simple cube starbox
; -----------------------------------------------------------------------------
Function InitStarBox(scale#,texture%,fx%,blend%)
	
	Local starbox%
	
	starbox=CreateCube()
	EntityTexture starbox,texture
	FlipMesh starbox
	ScaleEntity starbox,scale,scale,scale
	
	EntityFX starbox,fx
	EntityBlend starbox,blend
	
	Return starbox
	
End Function


; -----------------------------------------------------------------------------
; add a mesh to another mesh
; -----------------------------------------------------------------------------
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


; -----------------------------------------------------------------------------
; create star quad
; -----------------------------------------------------------------------------
Function CreateStarQuad(r%=255,g%=255,b%=255,a#=1.0,fx%=0)
	
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
	
	EntityFX mesh,fx
	
	Return mesh
	
End Function
