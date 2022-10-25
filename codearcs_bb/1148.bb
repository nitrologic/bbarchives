; ID: 1148
; Author: AntMan - Banned in the line of duty.
; Date: 2004-08-28 19:36:27
; Title: Omni Light/Normal Mapper
; Description: Light map(With shadows) or normal map a scene.

Global imap
Global ptri.pickedtri = New pickedtri

;Omni V0.9 Beta 5 - 3D Lighting and normal mapping Engine.
;
;Public Domain.
;
;Feel free to upload improved versions.
;
;Originally wrote by Antony Wells 2003
;-----

;-{Engine Globals. There are functions to change these, use 'em]
Global lMapWidth#=2048,lMapHeight#=2048
Global chunkWidth#=32,chunkHeight#=256
Const shadeWidth =128,shadeHeight=128
Global lMap.lightMap 



;---Test code]
Graphics3D 640,480,32,2
SetBuffer BackBuffer()

room=CreateCube()
FlipMesh room
ScaleMesh room,30,30,30

vcam=CreateCamera()

;-

Global vest=LoadTexture("units\thorn\tex5.bmp")

rock=LoadTexture("units\thorn\tex2.jpg")
tock=LoadTexture("units\thorn\tex2.jpg")
TextureBlend rock,1
TextureBlend tock,5

;EntityTexture room,rock,0,1
;ScaleTexture rock,0.2,0.2

;EntityTexture room,rock,0,1



AmbientLight 128,128,128


;-

;-

;AddReciever(room,16,16,1)


lv=CreateSphere()
cube=CreateCube();LoadMesh("units\hover\main.3ds")

If Not cube End
EntityColor room,128,0,0
;EntityTexture cube,tock,0,1




;FitMesh cube,-2,-2,-2,4,4,4
ScaleMesh cube,4,4,4
RotateMesh cube,90,0,0

;EntityTexture cube,rock,0,1

EntityTexture room,rock
Local cd[100]
cd[0]=cube


addreciever(cd[0],256,256,1)
	renderMap(vcam)



Repeat
If KeyDown(57) TextureBlend imap,1 Else TextureBlend imap,4
	TurnEntity vcam,MouseYSpeed(),-MouseXSpeed(),0
	PositionEntity lv,Cos(aaa#)*35,0,Sin(aaa)*35
	aaa#=aaa+1
	If MouseDown(2)
		PositionEntity lv,EntityX(vcam),EntityY(vcam),EntityZ(vcam)
	EndIf
	
	
	If KeyDown(17) MoveEntity vcam,0,0,0.1
	If KeyDown(31) MoveEntity vcam,0,0,-0.1
	If KeyDown(30) MoveEntity vcam,-0.1,0,0
	If KeyDown(32) MoveEntity vcam,0.1,0,0
	;-
	UpdateWorld
	RenderWorld
	;-
	Text 1,1,MouseZ()*0.1
	Flip
		;VectorLight(room,lv,MouseZ()*0.1,0)
		entityLight(cd[0],lv,MouseZ()*0.1,0)
		For j=0 To 0
	;		VectorLight(cd[j],lv,MouseZ()*0.1,0)
		Next
		
Until KeyDown(1)


Global lMapBlend=3
;-End of test]

;-[Types]
Type recv ;reciever meshes(Are lit)
	Field id
	Field cW,cH
	Field hull
End Type

Type cast ;caster meshes (Cast shadows)
	Field id
End Type

Type light ;3d lights
	Field x#,y#,z#
	Field cpiv,r,g,b
	Field fallOff#
	Field typ
	Field shade.shademap[100],sc
End Type

;-Chunks are 'reciever' tris, processed for rendering.
Type chunk 
	Field x#[3],y#[3],z#[3]
	Field u#[3],v#[3] ;u,v coords for the lightmap
	Field vi[3],hull
	Field nx#,ny#,nz# ;x,y,z normal of the triangle.
	Field pTex ;which texture plane to map to. (1-3)
	Field lTex ;lightMap texture
	Field srf,mesh,tri,ent
	Field eux#,evx#
	Field euy#,evy#
	Field euz#,evz#,leaf.leaf
	Field ox#,oy#,oz#
	Field tBank
	Field lit,shade
	Field cw,ch
	Field mx#,my#,mz#
	;real time fx 
	Field shim#[3]
	Field cx#,cy#,cz#,entX#,entY#,entZ#
End Type

Type shade
	Field x#[2],y#[2],z#[2]
	Field sx#[2],sy#[2]
	Field msh,light.light
End Type

Type yl
	Field y[shadeHeight]
End Type
Type shadeMap
	Field img,buf
	Field x.yl[shadeWidth]
	Field light.light,cast.cast
End Type


Type leaf
	Field leaf.leaf[2]
	Field x#,y#,w#,h#
	Field on
End Type

Type lightMap
	Field texMap,texBuf
	Field sW,sH
	Field tree.leaf,scam
End Type

;shadow maps

;--- [ Engine control ]
Function lightMapSize( width#,height#)
	lMapWidth =width
	lMapHeight =height
End Function

Function chunkSize( width#,height#)
	chunkWidth =width
	chunkHeight =height
End Function
Function shadeMapSize( width,height)
	lMap\sw =width
	lMap\sh =height
End Function


Function ligtMapBlend( mode=2)
	lMapBlend =mode
End Function

;--- [ Special Fx ] ;you must retain chunks for fx to work.
Global aa=0
Function shadowShimmer() ;disabled for now.

End Function


;--- [ Scene Building ]
Function addReciever(mesh,cw=128,ch=128,hull=False)
	recv.recv =New recv
	recv\id =mesh
	recv\cw=cw
	recv\ch=ch
	recv\hull=hull
	;EntityFX mesh,2
End Function

Function addCaster(mesh)
	cast.cast =New cast
	cast\id =mesh
End Function


Const cOmni=1,cDirectional=2
Function addOmniLight.light(x#,y#,z#,r#=128,g#=128,b#=128,fallOff#=10)
	light.light =New light
	light\x =x
	light\y =y
	light\z =z
	light\r =r
	light\g =g
	light\b =b
	light\fallOff=fallOff
	Return light
End Function

Function renderMap(cam=0,mode=1)
Local x#[5],y#[5],z#[5],v#[5]
	For recv.recv =Each recv
		EntityPickMode recv\id,2,True
		HideEntity recv\id
	Next
	For cast.cast =Each cast
		HideEntity cast\id
	Next
	CameraViewport cam,0,0,shadeWidth,shadeHeight
	CameraZoom cam,1.8
	mapWidth =GraphicsWidth()
	mapHeight =GraphicsHeight()

	For light.light =Each light
		PositionEntity cam,light\x,light\y,light\z
		For cast.cast =Each cast
			light\shade[light\sc] =New shadeMap
			shademap.shademap =light\shade[light\sc]
			light\sc=light\sc+1
			shadeMap\cast =cast
			PointEntity cam,cast\id
			ShowEntity cast\id
			Cls
			RenderWorld
			HideEntity cast\id
			LockBuffer
			For px=0 To shadeWidth-1
				For py=0 To shadeHeight-1
					;pix =ReadPixelFast(px,py) 
					If ((ReadPixelFast(px,py) Shr 16) And 255)
						If shadeMap\x[px]=Null shadeMap\x[px] =New yl
						shadeMap\x[px]\y[py] =True
					EndIf
				Next
			Next
			UnlockBuffer
		Next
		
	Next
	
	
	
	For cast.cast =Each cast
		ShowEntity cast\id
	Next
	
	
	Goto skipnew
	For light.light =Each light ;create shadow segs'
	PositionEntity cam,light\x,light\y,light\z
	For cast.cast =Each cast
		PointEntity cam,cast\id
		For sc =1 To CountSurfaces( cast\id)
			srf =GetSurface( cast\id,sc)
			For t=1 To CountTriangles( srf)
				shade.shade =New shade
				shade\msh =cast\id
				shade\light =light
				sa=sa+1
				For vt=0 To 2
					v1 =TriangleVertex(srf,t,vt)
					If v1=<CountVertices(srf)
						CameraProject cam,VertexX(srf,v1),VertexY(srf,v1),VertexZ(srf,v1)
						shade\sx[vt] =ProjectedX()
						shade\sy[vt] =ProjectedY() 
					Else
						Delete shade
						Goto skipS
					EndIf
					
					Next
				Next
			.skipS
			Next
		
		Next
		
	Next
	.skipnew
	
	createLightMap()
	If cam setLightCam(cam)
	For recv.recv =Each recv
		cx#=EntityX(recv\id)
		cy#=EntityY(recv\id)
		cz#=EntityZ(recv\id)
		;recv\cx=cx
	;	recv\cy=cy
	;	recv\cz=cz
		
		unweld(recv\id)
		HideEntity recv\id
		EntityTexture recv\id,lMap\texMap,0,0
	;	EntityFX recv\id,1
		sC =CountSurfaces( recv\id)
		;UpdateNormals recv\id
		If sc
			For s=1 To sc
				srf =GetSurface( recv\id,s)
				tris =CountTriangles( srf)
			
				For tri=0 To tris
					chunk.chunk =New chunk
					chunk\cx=cx
					chunk\cy=cy
					chunk\cz=cz
					chunk\tri=tri
					chunk\ent=recv\id
					chunk\srf=srf
					chunk\entX=EntityY(recv\id)
					chunk\entY=EntityX(recv\id)
					chunk\entZ=EntityZ(recv\id)
					chunk\hull=recv\hull
					
					chunk\cw =recv\cw
					chunk\mesh =recv\id
					chunk\ch =recv\ch
					chunk\srf =srf
					chunk\lit =True	
					For vt=0 To 2
						vi =TriangleVertex(srf,tri,vt)
						If vi<CountVertices(srf)
							x[vt] =VertexX( srf,vi)
							y[vt] =VertexY( srf,vi)
							z[vt] =VertexZ( srf,vi)
							chunk\x[vt] =x[vt]
							chunk\y[vt] =y[vt]
							chunk\z[vt] =z[vt]
							chunk\vi[vt] =vi
						Else
							Delete chunk
							Goto skipChunk
						EndIf
					Next
					triNorm( chunk\x[0],chunk\y[0],chunk\z[0],chunk\x[1],chunk\y[1],chunk\z[1],chunk\x[2],chunk\y[2],chunk\z[2])
					nx#=tnormX()
					ny#=tnormY()
					nz#=tnormZ()
					
				
					
								
					If Abs(nx)>Abs(ny) And Abs(nx)>Abs(nz)
						chunk\pTex =1
						;map onto yz-plane
					Else
						If Abs(ny)>Abs(nx) And Abs(ny)>Abs(nz)
							chunk\pTex =2
						Else
							chunk\pTex =3
						EndIf
					EndIf
					.skipChunk								
				Next
			Next
		EndIf
		
	Next
	If mode=1 tr =normalChunks()
	If mode=2 tr =lightChunks()
	For recv.recv =Each recv
		;UpdateNormals recv\id
		ShowEntity recv\id
	Next
	CameraViewport cam,0,0,GraphicsWidth(),GraphicsHeight()
	CameraZoom cam,1
	For cast.cast =Each cast
		ShowEntity cast\id
	Next
	SetBuffer BackBuffer()
	Return sa
End Function

Function big#(v0#,v1#,v2#)
	If v0>v1 
		If v0>v2 
			Return v0
		Else 
			Return v2
		EndIf
	Else 
		If v1>v2
			Return v1
		Else	
			Return v2
		EndIf
	EndIf
End Function

Function small#(v0#,v1#,v2#)
	If v0<v1 
		If v0<v2 
			Return v0
		Else 
			Return v2
		EndIf
	Else 
		If v1<v2
			Return v1
		Else	
			Return v2
		EndIf
	EndIf
End Function


Function vectorLight(entity,light,inten#=0.5,hull)
	ox#=EntityX(light)
	oy#=EntityY(light)
	
	oz#=EntityZ(light)
	;If Not hull inten=-inten
	tp=CreatePivot()
	PositionEntity tp,EntityX(light),EntityY(light),EntityZ(light)
	TFormPoint ox,oy,oz,tp,entity
	;FreeEntity tp
	light=tp
	
	PositionEntity light,TFormedX(),TFormedY(),TFormedZ() ;Transpose light into 
	srf=CountSurfaces(Entity)
	lx#=EntityX(light)
	ly#=EntityY(light)
	lz#=EntityZ(light)

	cs=CountSurfaces(entity)
	For s=1 To cs
		srf=GetSurface(entity,s)
		For v=0 To CountVertices(srf)-1
			vx#=VertexX(srf,v)
			vy#=VertexY(Srf,v)
			vz#=VertexZ(srf,v)
			dx# =(lx-vx)
			dy# =(ly-vy)
			dz# =(lz-vz)
			tdx#=vx-lx
			tdy#=vy-ly
			tdz#=vz-lz
		;	tl#=Sqr(tdx*tdx+tdy*tdy*+tdz*tdz)
		;	dx=(dx/tl)*inten
		;	dy=(dy/tl)*inten
			;dz=(dz/tl)*inten
			nl#=Sqr(tdx*tdx+tdy*tdy+tdz*tdz)
			nx#=dx/nl
			ny#=dy/nl
			nz#=dz/nl
			nl#=(nl*10.-255.)/255.
	
			nl=1-nl
	
	If nl>1 nl=1
	If nl<0 nl=0
	nl=in

	nx=nx*inten
	ny=ny*inten
	nz=nz*inten

			
			VertexColor srf,v,128+(128*nx),128+(128*nz),128+(128*ny)
		Next

	Next
	FreeEntity tp
End Function


Function old()
For v=0 To CountVertices(srf)-1
			vx#=VertexX(srf,v)
			vy#=VertexY(Srf,v)
			vz#=VertexZ(srf,v)
			dx# =(lx-vx)
			dy# =(ly-vy)
			dz# =(lz-vz)
			tdx#=vx-lx
			tdy#=vy-ly
			tdz#=vz-lz
		;	tl#=Sqr(tdx*tdx+tdy*tdy*+tdz*tdz)
		;	dx=(dx/tl)*inten
		;	dy=(dy/tl)*inten
			;dz=(dz/tl)*inten
			nl#=Sqr(tdx*tdx+tdy*tdy+tdz*tdz)
			nx#=dx/nl
			ny#=dy/nl
			nz#=dz/nl
			nl#=(nl*10.-255.)/255.
	
			nl=1-nl
	
	If nl>1 nl=1
	If nl<0 nl=0
	nl=in

	nx=nx*inten
	ny=ny*inten
	nz=nz*inten

			
			VertexColor srf,v,128+(128*nx),128+(128*nz),128+(128*ny)
		Next
End Function


Function entityLight(entity,light,inten#=0.5,hull=False)
	ox#=EntityX(light)
	oy#=EntityY(light)
	oz#=EntityZ(light)
	inten =0.5
;	If Not hull inten=1.-inten
	tp=CreatePivot()
	PositionEntity tp,EntityX(light),EntityY(light),EntityZ(light)
	TFormPoint ox,oy,oz,tp,entity
	;FreeEntity tp
	light=tp
	
	PositionEntity light,TFormedX(),TFormedY(),TFormedZ() ;Transpose light into 
	dx# =(EntityX(light)-EntityX( entity));*inten
	dy# =(EntityY(light)-EntityY( entity));*inten
	dz# =(EntityZ(light)-EntityZ( entity));*inten
	
	nl#=Sqr(dx*dx+dy*dy+dz*dz)
	nx#=dx/nl
	ny#=dy/nl
	nz#=dz/nl
	nl#=(nl*10.-255.)/255.
	
	nl=1.-nl
	
	If nl>1. nl=1
	If nl<0. nl=0
	nl=in

	nx=nx;*inten
	ny=ny;*inten
	nz=nz;*inten
	
	;nz=(Sgn(nz))+nz
	;nx=(Sgn(nx))+nx
	;ny=(Sgn(ny))+ny
	
;	If nx<0 nx
		
		
	FreeEntity tp
	EntityColor entity,128.+(128.*nx),128.+(128.*nz),128.+(128.*ny)
	

	;EntityColor entity,256+(256*nx),256.0+(256*ny),256.0+(256*nt)
	
;	PositionEntity light,ox,oy,oz ;return light.
End Function
 

Function createLightMap()
	lMap.lightMap =New lightMap
	lmap\texMap =CreateTexture(lMapWidth,lMapHeight,8)
	lmap\texBuf =TextureBuffer(lMap\texMap)
	TextureCoords lmap\texMap,1 ;set to the second u,v set.
	 imap=lmap\texmap
	
	TextureBlend lmap\texMap,3
End Function
Function setLightCam(cam)
	lmap\scam =cam
	CameraClsMode lmap\scam,False,True
End Function

;-- [ Leaf Engine] ;packs multiple textures into 1 'larger' texture
Function newLeaf.leaf(texture) ;returns leaf object that holds the tex.
	width =TextureWidth(texture)
	height =TextureHeight(texture)
	If width<1 Or height<1 Return
	If lMap\tree =Null ;first image
		lMap\tree =New leaf
		lMap\tree\w =lMapWidth
		lMap\tree\h =lMapHeight
	EndIf
	For leaf.leaf =Each leaf
		out.leaf =insertLeaf( leaf,texture)
		If out<>Null Return out
	Next
	;Return addLeaf( lMap\tree,texture)
End Function


Function insertLeaf.leaf( leaf.leaf,texture)
width =TextureWidth(texture)
height =TextureHeight(texture)	
	
If leaf\on Return 
	
	If width<=leaf\w And height<=leaf\h ;fits
		leaf\on =True

		leaf\leaf[0] =New leaf
		leaf\leaf[1] =New leaf
		leaf\leaf[0]\x =leaf\x+width
		leaf\leaf[0]\y =leaf\y
		leaf\leaf[0]\w =leaf\w-width-1
		leaf\leaf[0]\h =height
				
		leaf\leaf[1]\x =leaf\x
		leaf\leaf[1]\y =leaf\y+height
		leaf\leaf[1]\w =leaf\w
		leaf\leaf[1]\h =leaf\h-height
		
		leaf\w =width
		leaf\h =height
		CopyRect 0,0,width,height,leaf\x,leaf\y,TextureBuffer(texture),lMap\texBuf
		Return leaf
	EndIf
End Function


Function addLeaf.leaf( leaf.leaf,texture) ;internal function
Local nleaf.leaf
	width =TextureWidth(texture)
	height =TextureHeight(texture)

If leaf\on

    nleaf =insertLeaf( leaf\leaf[0],texture)
	If nleaf<>Null Return nleaf
	nleaf =insertLeaf( leaf\leaf[1],texture)
	If nleaf<>Null Return nleaf
Else
	nleaf =insertLeaf( leaf,texture)
	If nleaf<>Null Return nleaf
EndIf
	
End Function

Function leafU#(leaf.leaf,u#) ;converts a normal u coord into a lightmap u coord
	Return ((leaf\x+1)+((leaf\w-2)*u))/lMapWidth 
End Function

Function leafV#(leaf.leaf,v#)
	Return ((leaf\y+1)+((leaf\h-2)*v))/lMapHeight
End Function

Function textureNorm(tex)
	SetBuffer TextureBuffer(Tex)
	tw#=TextureWidth(Tex)
	th#=TextureHeight(tex)
	LockBuffer
	For x=0 To tw-1
	For y=0 To th-1
		cv=ReadPixelFast(x,y)
		r=(cv Shr 16) And 255
		g=(cv Shr 8) And 255
		b=(cv And 255)
		cv=(r+g+b)/3.
		nx#=128
		ny#=128
		nz#=cv
		rgb = nz Or (ny Shl 8) Or (nx Shl 16)
		WritePixelFast x,y,rgb
	Next
	Next
	UnlockBuffer
	SetBuffer BackBuffer()
End Function


Function NormalChunks() ;final chunk 'prep' before rendering
Local miU#,miV#
Local maU#,maV#,rU#,rV#
Local u#[3],v#[3]
Local spiv =CreatePivot(),lpiv1=CreatePivot()
Local lpiv2 =CreatePivot(),lpiv3 =CreatePivot()

For chunk.chunk =Each chunk
If chunk\lit
		;-fx
		
		;----
		For vt=0 To 2
			chunk\shim[vt] =Rnd(360)
			Select chunk\pTex
				Case 1 ;yz
					u[vt] =chunk\y[vt]
					v[vt] =chunk\z[vt]
				Case 2 ;xz
					u[vt] =chunk\x[vt]
					v[vt] =chunk\z[vt]
				Case 3 ;xy
					u[vt] =chunk\x[vt]
					v[vt] =chunk\y[vt]
				Default
					RuntimeError "Illegal projection plane"
			End Select
		Next
		triNorm( chunk\x[0],chunk\y[0],chunk\z[0],chunk\x[1],chunk\y[1],chunk\z[1],chunk\x[2],chunk\y[2],chunk\z[2])
		
		;TriNorm( x[0],y[0],z[0],x[1],y[1],z[1],x[2],y[2],z[2])
		chunk\nX# =tNormX()
		chunk\ny =tNormY()
		chunk\nz =tNormZ()
					
				
		
					
		
		;tnx#=chunk\nx
		;tny#=chunk\ny
		;tnz#=chunk\nz
		
		
		;map u,v into valid 0,1 range.
		miU = 9999
		miV = 9999
		maU = -9999
		maV = -9999
		For i=0 To 2
			If u[i]<miU
				miU =u[i]
			EndIf
			If u[i]>maU
				maU =u[i]
			EndIf
			If v[i]<miV
				miV =v[i]
			EndIf
			If v[i]>maV
				maV =v[i]
			EndIf
		Next
		
	
		rU =maU -miU
		rV =maV -miV
		
	
		mapWidth =chunk\cw
		mapHeight =chunk\ch
	
		tempMap =CreateTexture( mapWidth,mapHeight,256)
		If Not tempMap Return
		For vt=0 To 2
			chunk\u[vt] =(u[vt]-miU) /rU
			chunk\v[vt] =(v[vt]-miV) /rV
		Next
		
	
    	dist# = -(chunk\nx * chunk\x[0]+chunk\ny*chunk\y[1]+chunk\nz*chunk\z[2])
		Select chunk\pTex
			Case 3
			
				Z# = -(chunk\nx*miU + chunk\ny * miV + Dist) / chunk\nz
				uvx# = miu : UVY# = miV : UVZ# = Z
				Z# = -(chunk\NX * maU + chunk\NY * miV + Dist) / chunk\NZ
				V1X# = maU : V1Y# = miV : V1Z# = Z
				Z# = -(chunk\NX * miu + chunk\NY * maV + Dist) / chunk\NZ
				V2X# = miu : V2Y# = maV : V2Z# = Z
			Case 2
				Y# = -(chunk\NX * miu + chunk\NZ * miV + Dist) / chunk\NY
				UVX# = miu : UVY# = Y : UVZ# = miV
				Y# = -(chunk\NX * maU + chunk\NZ * miV + Dist) / chunk\NY
				V1X# = maU : V1Y# = Y : V1Z# = miV
				Y# = -(chunk\NX * miu + chunk\NZ * maV + Dist) / chunk\NY
				V2X# = miu : V2Y# = Y : V2Z# = maV
			Case 1		
				X# = -(chunk\NY * miu + chunk\NZ * miV + Dist) / chunk\NX
				UVX# = X : UVY# = miu : UVZ# = miV
				X# = -(chunk\NY * maU + chunk\NZ * miV + Dist) / chunk\NX	
				V1X# = X : V1Y# = maU : V1Z# = miV	
				X# = -(chunk\NY * miu + chunk\NZ * maV + Dist) / chunk\NX
				V2X# = X : V2Y# = miu : V2Z# = maV
		End Select
		chunk\eux = V1X - UVX : chunk\euy = V1Y - UVY : chunk\euz = V1Z - UVZ
		chunk\evx = V2X - UVX : chunk\evy = V2Y - UVY : chunk\evz = V2Z - UVZ
		chunk\ox = UVX# : chunk\oy = UVY# : chunk\oz = UVZ#

		;dx# =chunk\ox
		
		ox#=md( chunk\x[0],chunk\x[1],chunk\x[2])
		oy#=md( chunk\y[0],chunk\y[1],chunk\y[2])
		oz#=md( chunk\z[0],chunk\z[1],chunk\z[2])
	;	ox=ox+chunk\cx
		
		;DebugLog chunk\cx+" Oy>"+chunk\cy+" oz>"+chunk\cz
		If chunk\hull
	ox=-ox
			oy=-oy
			oz=-oz
		;	dx#=ox-chunk\cx
		;	dy#=oy-chunk\cy
		;	dz#=oz-chunk\cz	
		Else
		;	dx =chunk\cx-ox
		;	dy =chunk\cy-oy
		;	dz =chunk\cz-oz
			
		EndIf	
	;	ox=ox-chunk\cx
	;	oy=oy-chunk\cy
	;	oz=oz-chunk\cz
		
		;nl# =Sqr(ox*ox+oy*oy+oz*oz)
	;	tnx#=(ox)/nl
	;	tny#=(oy)/nl
	;	tnz#=(oz)/nl
	;	ox=chunk\cx+ox
	;	oy=chunk\cy+oy
	;	oz=chunk\cz+oz
		
	;	cc=CreateCube()
	;	FitMesh cc, -0.2,-0.2,-0.2,0.4,0.4,0.4
	;	PositionEntity cc,ox,oy,oz
		
	;	vp=CreateCube()
	;	FitMesh vp,-0.2,-0.2,-.2,0.4,0.4,0.4
	;	PositionEntity vp,chunk\cx,chunk\cy,chunk\cz
	
		If mapWidth<>laW Or mapHeight<>laH
			laW =mapWidth
			laH =mapHeight
			If tmpImg FreeImage tmpImg
			tmpImg =CreateImage(mapWidth,mapHeight)
			tmpBuf =ImageBuffer(tmpImg)
			SetBuffer tmpBuf
		EndIf
		LockBuffer tmpBuf
		LockBuffer TextureBuffer(vest)
		rv#=128.+(128.*tnx)
		bv#=128.+(128.*tny)
		gv#=128.+(128.*tnz)
		;rv=Rnd(255)
		;EntityColor cc,255,255,255
		vb=TextureBuffer(vest)
		entX#=chunk\entX
		entY#=chunk\entY
		entZ#=chunk\entZ
		If chunk\hull dir#=1 Else dir=-1
		PositionEntity spiv,chunk\cx,chunk\cy,chunk\cz
		tw#=TextureWidth(vest)
		th#=TextureHeight(vest)
		
		For x#=0 To mapWidth-1 
			au# = x/mapwidth
			N_UEdgeX# = chunk\EuX * au#  :  N_UEdgeY# = chunk\euY * au#  :  N_UEdgeZ# = chunk\euZ * au#
		For y#=0 To mapHeight-1 
			av# = y/mapHeight		
			N_VEdgeX# = chunk\evX * av#  :  N_VEdgeY# = chunk\evY * av#  :  N_VEdgeZ# = chunk\evZ * av#
			lx# = (chunk\ox + N_UEdgeX + N_VEdgeX)
			ly# = (chunk\oy + N_UEdgeY + N_VEdgeY)
			lz# = (chunk\oz + N_UEdgeZ + N_VEdgeZ)
		
			
			td#=Sqr(lx*lx+ly*ly+lz*lz)
			nox#=lx/td ;*Rnd(0.75,1.25)
			noz#=ly/td ;'*Rnd(0.75,1.25)
			noy#=lz/td
			
			
			
		;	shaded =False
		;	lcast.cast=Null
		;	r1=0
			;g1=0
		;	b1=0
	
		;	For light.light =Each light
		;		PositionEntity lmap\scam,light\x,light\y,light\z
		;		shaded=False
		;		If light\sc
		;		For j=0 To light\sc-1
		;			shade.shadeMap =light\shade[j]
		;			PointEntity lmap\scam,shade\cast\id
		;			If shade\cast\id =chunk\mesh Goto skips 
		;			CameraProject lmap\scam,lx,ly,lz
		;			px =ProjectedX()
		;			If px>0 And px<shadeWidth
		;			If shade\x[px]<>Null
		;				py =ProjectedY()
		;				If py>0 And py<shadeHeight
		;					shaded =shade\x[px]\y[py]
		;					If shaded Exit
		;				EndIf
		;			EndIf
		;			EndIf
		;			.skips
		;		Next
		;		EndIf
		;		If Not shaded
				
		;			PositionEntity spiv,light\x,light\y,light\z
		;			ed# =(EntityDistance(spiv,lpiv1)*light\fallOff)
		;			
		;			r1=(light\r-ed)
		;			g1=(light\g-ed)
		;			b1=(light\b-ed)
		;			If r1<0 r1=0
		;			If g1<0 g1=0
		;			If b1<0 b1=0
		;			rv=rv+r1
		;			gv=gv+g1
		;			bv=bv+b1
		;		Else
				
		;		EndIf
		;	Next
		;	If rv>255 rv=255
		;	If gv>255 gv=255
		;	If bv>255 bv=255
			;If r1<0 rv=0
			;If g1<0 gv=0
			;If b1<0 bv=0	
				
		;	xd#=(lx-chunk\cx)
		;	yd#=(ly-chunk\cy)
		;	zd#=(lz-chunk\cz)
			;I;f chunk\hull=False
			;	xd=0
			;	yd=0
			;	zd=0			
			;EndIf
			
			
		;	ld#=Sqr(xd*Xd+Yd*Yd+zd*zd)
		;	PositionEntity lpiv1,lx,ly,lz
		;	ld#=EntityDistance(lpiv1,spiv)
			;ld=-dist
			
		;	anx#=xd/ld
		;	any#=yd/ld
		;Function PickedUVW(ent,surf,tri,x#,y#,z#,nx#,ny#,nz#)
		pickedUVW(chunk\ent,chunk\srf,chunk\tri,lx,ly,lz,chunk\nx,chunk\ny,chunk\nz)
		ssu#=pickedU()
		ssv#=pickedV()
		ax#=tw*ssu
		ay#=th*ssv
		;If ax>tw-1 ax=tw-1
		;If ay>th-1 ay=th-1
		;If ax<0 ax=0
		;If ay<0 ay=0
			ab=ReadPixelFast(ax,ay,vb)
			ar=(ab Shr 16) And 255
			ag=(ab Shr 8) And 255
			ab=(ab And 255)
		;	anz#=zd/ld  ;+((ag/255)-1)
		;	any=any+ag
			
			;anx=ar
			;any=ag
			
			;anz=ab
		;	anx#=tnx#+Rnd(-0.001,0.001)
		;	any#=tny#+Rnd(-0.001,0.001)
		;	anz#=tnz#+Rnd(-0.001,0.001)
		;	anx=tnx+Cos(x)
		;	any=tny+Sin(y)
		;	anz=tnz
		
		vx#=((ar)/255.0)
		vz#=((ag)/255.0)
		vy#=((ab)/255.0)
		vx#=-1.+vx*2.
		vy#=-1.+vy*2. ;bring vector into valid -1,1 range
		vz#=-1.+vz*2.
		
	;	If vx>1 End
		;PointEntity lpiv1,spiv
		;PointEntity spiv,lpiv1
		
	
		;AlignToVector spiv,nox,noy,noz,0
		
		;AlignToVector spiv,nox,noy,noz,2
		
		
	
		TFormVector2(vx,vy,vz,nox,noy,noz)
		ar=128.0+(128.0*TFormedX())
		ag=128.0+(128.0*TFormedY())
		ab=128.0+(128.0*TFormedZ())
		
		;ar=128
		;ag=128
		;ab=128
		
		
		
		;ar=vx*anx
		
	;;	ag=vy*any
	;	ab=vz*anz
		;ar=128.+(128.*vx)
		;ag=128.+(128.*vy)
		;ab=128.+(128.*vz)	
	;	anx=anx*vx
	;	any=any*vy
	;	anz=anz*vz
	;	rv#=128.+(128.*anx)
	;	bv#=128.+(128.*any)
	;	gv#=128.+(128.*anz)
	;	ar=(ar-128)*2
	;	ag=(ag-128)*2
	;	ab=(ab-128)*2
	;	rv=rv+ar
	;	gv=gv+ag
	;	bv=bv+ab
		
		
	;	rv#=(0.5+(nox/2.0))*255.
	;	gv#=(0.5+(noy/2.0))*255.
	;	bv#=(0.5+(noz/2.0))*255.
			
		rv=ar
		gv=ag
		bv=ab	
		If rv<0 rv=0
		If gv<0 gv=0
		If bv<0 bv=0
		If rv>255 rv=255
		If gv>255 gv=255
		If bv>255 bv=255
		;	bv=ar
		;	rv=ag
			
		;	gv=ab;+Rnd(-20,20)
			arg = bv Or (gv Shl 8) Or (rv Shl 16)	
			WritePixelFast x,y,arg
		Next
		Next
		UnlockBuffer tmpBuf
		UnlockBuffer TextureBuffer(vest)
		SetBuffer TextureBuffer(tempMap)
		DrawBlock tmpImg,0,0
		SetBuffer tmpBuf 	
	;CopyRect 0,0,mapWidth,mapHeight,0,0,tmpBuf,TextureBuffer(tempMap)
		

	
		

		
		;-
		leaf.leaf =newLeaf(tempMap) 
		chunk\leaf =leaf
		FreeTexture tempMap
		For vt=0 To 2 ;map lightmap onto mesh
			chunk\u[vt] =leafU(leaf,chunk\u[vt])
			chunk\v[vt] =leafV(leaf,chunk\v[vt])
			VertexTexCoords chunk\srf,chunk\vi[vt],chunk\u[vt],chunk\v[vt],0,1
		Next
EndIf
	Next

	For recv.recv =Each recv
		ShowEntity recv\id
	Next
	CameraZoom lmap\scam,1
	SetBuffer BackBuffer()
	CameraViewport lmap\scam,0,0,GraphicsWidth(),GraphicsHeight()
	FreeImage tmpImg
End Function
Function md#(v1#,v2#,v3#)
	v1=v1+2000
	v2=v2+2000
	v3=v3+2000
	s#=sm(v1,v2,v3)
	b#=bg(v1,v2,v3)
	v#=s+((b-s)/2.)
	v=v-2000
	Return v
End Function

Function bg#(v1#,v2#,v3#)
	If v1=v2
		If v3<v2 Return v2
	EndIf
	If v1=v3
		If v2<v1 Return v1
	EndIf
	If v2=v3
		If v1<v2 Return v2
	EndIf
		
	
	If v1>v2 And v1>v3 Return v1
	If v2>v1 And v2>v3 Return v2
	Return v3
End Function

Function sm#(v1#,v2#,v3#)
	If v1=v2
		If v3>v2 Return v1
	EndIf
	If v1=v3
		If v2>v3 Return v1
	EndIf
	If v2=v3 
		If v1>v2 Return v2
	EndIf
	
	
	If v1<v2 And v1<v3 Return v1
	If v2<v1 And v2<v3 Return v2
	Return v3
End Function


Function LightChunks() ;final chunk 'prep' before rendering
Local miU#,miV#
Local maU#,maV#,rU#,rV#
Local u#[3],v#[3]
Local spiv =CreatePivot(),lpiv1=CreatePivot()
Local lpiv2 =CreatePivot(),lpiv3 =CreatePivot()

For chunk.chunk =Each chunk
If chunk\lit
		;-fx
		
		;----
		For vt=0 To 2
			chunk\shim[vt] =Rnd(360)
			Select chunk\pTex
				Case 1 ;yz
					u[vt] =chunk\y[vt]
					v[vt] =chunk\z[vt]
				Case 2 ;xz
					u[vt] =chunk\x[vt]
					v[vt] =chunk\z[vt]
				Case 3 ;xy
					u[vt] =chunk\x[vt]
					v[vt] =chunk\y[vt]
				Default
					RuntimeError "Illegal projection plane"
			End Select
		Next
		triNorm( chunk\x[0],chunk\y[0],chunk\z[0],chunk\x[1],chunk\y[1],chunk\z[1],chunk\x[2],chunk\y[2],chunk\z[2])
		
		tnx#=tNormX()
		tny#=tnormY()
		tnz#=tnormz()
		
		;map u,v into valid 0,1 range.
		miU = 9999
		miV = 9999
		maU = -9999
		maV = -9999
		For i=0 To 2
			If u[i]<miU
				miU =u[i]
			EndIf
			If u[i]>maU
				maU =u[i]
			EndIf
			If v[i]<miV
				miV =v[i]
			EndIf
			If v[i]>maV
				maV =v[i]
			EndIf
		Next
		
	
		rU =maU -miU
		rV =maV -miV
		
	
		mapWidth =chunk\cw
		mapHeight =chunk\ch
	
		tempMap =CreateTexture( mapWidth,mapHeight,256)
		If Not tempMap Return
		For vt=0 To 2
			chunk\u[vt] =(u[vt]-miU) /rU
			chunk\v[vt] =(v[vt]-miV) /rV
		Next
		
	
    	dist# = -(chunk\nx * chunk\x[0]+chunk\ny*chunk\y[1]+chunk\nz*chunk\z[2])
		Select chunk\pTex
			Case 3
			
				Z# = -(chunk\nx*miU + chunk\ny * miV + Dist) / chunk\nz
				uvx# = miu : UVY# = miV : UVZ# = Z
				Z# = -(chunk\NX * maU + chunk\NY * miV + Dist) / chunk\NZ
				V1X# = maU : V1Y# = miV : V1Z# = Z
				Z# = -(chunk\NX * miu + chunk\NY * maV + Dist) / chunk\NZ
				V2X# = miu : V2Y# = maV : V2Z# = Z
			Case 2
				Y# = -(chunk\NX * miu + chunk\NZ * miV + Dist) / chunk\NY
				UVX# = miu : UVY# = Y : UVZ# = miV
				Y# = -(chunk\NX * maU + chunk\NZ * miV + Dist) / chunk\NY
				V1X# = maU : V1Y# = Y : V1Z# = miV
				Y# = -(chunk\NX * miu + chunk\NZ * maV + Dist) / chunk\NY
				V2X# = miu : V2Y# = Y : V2Z# = maV
			Case 1		
				X# = -(chunk\NY * miu + chunk\NZ * miV + Dist) / chunk\NX
				UVX# = X : UVY# = miu : UVZ# = miV
				X# = -(chunk\NY * maU + chunk\NZ * miV + Dist) / chunk\NX	
				V1X# = X : V1Y# = maU : V1Z# = miV	
				X# = -(chunk\NY * miu + chunk\NZ * maV + Dist) / chunk\NX
				V2X# = X : V2Y# = miu : V2Z# = maV
		End Select
		chunk\eux = V1X - UVX : chunk\euy = V1Y - UVY : chunk\euz = V1Z - UVZ
		chunk\evx = V2X - UVX : chunk\evy = V2Y - UVY : chunk\evz = V2Z - UVZ
		chunk\ox = UVX# : chunk\oy = UVY# : chunk\oz = UVZ#

	
			
	
	
		If mapWidth<>laW Or mapHeight<>laH
			laW =mapWidth
			laH =mapHeight
			If tmpImg FreeImage tmpImg
			tmpImg =CreateImage(mapWidth,mapHeight)
			tmpBuf =ImageBuffer(tmpImg)
			SetBuffer tmpBuf
		EndIf
		LockBuffer
		LockBuffer TextureBuffer(vest)
		vb=TextureBuffer(vest)
		For x#=0 To mapWidth-1 
			au# = x/mapwidth
			N_UEdgeX# = chunk\EuX * au#  :  N_UEdgeY# = chunk\euY * au#  :  N_UEdgeZ# = chunk\euZ * au#
		For y#=0 To mapHeight-1 
			av# = y/mapHeight		
			N_VEdgeX# = chunk\evX * av#  :  N_VEdgeY# = chunk\evY * av#  :  N_VEdgeZ# = chunk\evZ * av#
			lx# = (chunk\ox + N_UEdgeX + N_VEdgeX)
			ly# = (chunk\oy + N_UEdgeY + N_VEdgeY)
			lz# = (chunk\oz + N_UEdgeZ + N_VEdgeZ)
			
			shaded =False
			lcast.cast=Null
			r1=0
			g1=0
			b1=0
	
			For light.light =Each light
				PositionEntity lmap\scam,light\x,light\y,light\z
				shaded=False
				If light\sc
				For j=0 To light\sc-1
					shade.shadeMap =light\shade[j]
					PointEntity lmap\scam,shade\cast\id
					If shade\cast\id =chunk\mesh Goto skips 
					CameraProject lmap\scam,lx,ly,lz
					px =ProjectedX()
					If px>0 And px<shadeWidth
					If shade\x[px]<>Null
						py =ProjectedY()
						If py>0 And py<shadeHeight
							shaded =shade\x[px]\y[py]
							If shaded Exit
						EndIf
					EndIf
					EndIf
					.skips
				Next
				EndIf
				If Not shaded
					PositionEntity lpiv1,lx,ly,lz
					PositionEntity spiv,light\x,light\y,light\z
					ed# =(EntityDistance(spiv,lpiv1)*light\fallOff)
					
					r1=(light\r-ed)
					g1=(light\g-ed)
					b1=(light\b-ed)
					If r1<0 r1=0
					If g1<0 g1=0
					If b1<0 b1=0
					rv=rv+r1
					gv=gv+g1
					bv=bv+b1
				Else
				
				EndIf
			Next
			If rv>255 rv=255
			If gv>255 gv=255
			If bv>255 bv=255
			;If r1<0 rv=0
			;If g1<0 gv=0
			;If b1<0 bv=0		
			
			rv=128+(128*tnx)
			gv=128+(128*tny)
			bv=128+(128*tnz)
			
			WritePixelFast x,y,bv Or (gv Shl 8) Or (rv Shl 16)
			rv=0
			gv=0
			bv=0
		Next
		Next
		UnlockBuffer TextureBuffer(vest)
		UnlockBuffer
		SetBuffer TextureBuffer(tempMap)
		DrawBlock tmpImg,0,0
		SetBuffer tmpBuf 	
	;CopyRect 0,0,mapWidth,mapHeight,0,0,tmpBuf,TextureBuffer(tempMap)
		

	
		

		
		;-
		leaf.leaf =newLeaf(tempMap) 
		chunk\leaf =leaf
		FreeTexture tempMap
		For vt=0 To 2 ;map lightmap onto mesh
			chunk\u[vt] =leafU(leaf,chunk\u[vt])
			chunk\v[vt] =leafV(leaf,chunk\v[vt])
			VertexTexCoords chunk\srf,chunk\vi[vt],chunk\u[vt],chunk\v[vt],0,1
		Next
EndIf
	Next

	For recv.recv =Each recv
		ShowEntity recv\id
	Next
	CameraZoom lmap\scam,1
	SetBuffer BackBuffer()
	CameraViewport lmap\scam,0,0,GraphicsWidth(),GraphicsHeight()
	FreeImage tmpImg
End Function

Function TFormVector2(Ax#,Ay#,Az#,Bx#,By#,Bz#)
temp=CreatePivot()
AlignToVector temp,bx,0,0,1
AlignToVector temp,0,by,0,2
AlignToVector temp,0,0,bz,3
TFormVector ax,ay,az,0,temp
FreeEntity temp
End Function

Function max3#(a#,b#,c#)
	If a>b 
		If a>c Return a
		Return c
	EndIf
	If b>c Return b
	Return c
End Function
Function min3#(a#,b#,c#)
	If a<b 
		If a<c Return a
		Return c
	EndIf
	If b<c Return b
	Return c
End Function


Function texToImage(texture) ;converts a texture to an image
	out =CreateImage(TextureWidth(texture),TextureHeight(texture))
	CopyRect 0,0,TextureWidth(texture),TextureHeight(texture),0,0,TextureBuffer(texture),ImageBuffer(out)
	Return out
End Function


;-- 3rd party functions. 

Function dot(x0#,y0#,x1#,y1#,x2#,y2#)

Return (x1#-x0#)*(y2#-y1#)-(x2#-x1#)*(y1#-y0#)

End Function

Function inTri(px#,py#,x0#,y0#,x1#,y1#,x2#,y2#)

If dot(x0,y0,x1,y1,px,py)>=0

If dot(x1,y1,x2,y2,px,py)>=0

If dot(x2,y2,x0,y0,px,py)>=0

Return True

EndIf

EndIf

EndIf

End Function


Global g_TriNormalX#, g_TriNormalY#, g_TriNormalZ#

Function TriNorm(x1#, y1#, z1#, x2#, y2#, z2#, x3#, y3#, z3#)
    ux# = x1# - x2#
    uy# = y1# - y2#
    uz# = z1# - z2#
    vx# = x3# - x2#
    vy# = y3# - y2#
    vz# = z3# - z2#	
	nx# = (uy# * vz#) - (vy# * uz#)
    ny# = (uz# * vx#) - (vz# * ux#)  
    nz# = (ux# * vy#) - (vx# * uy#)
; Normalize it
    NormLen# = Sqr((nx*nx) + (ny*ny) + (nz*nz))  
    If NormLen > 0
		nx = nx/NormLen : ny = ny/NormLen: nz = nz/NormLen
	Else
		nx = 0 : ny = 0 : nz = 1
	EndIf
	g_TriNormalX = nx
	g_TriNormalY = ny
	g_TriNormalZ = nz
End Function
Dim txv#(3) 
Type TRIS
Field x0#
Field y0#
Field z0#
Field u0#
Field v0#
Field U20#
Field V20#

Field x1#
Field y1#
Field z1#
Field u1#
Field v1#
Field U21#
Field V21#

Field x2#
Field y2#
Field z2#
Field u2#
Field v2#
Field U22#
Field V22#

Field surface
End Type

Function Weld(mish)
Dim txv(3)


For nsurf = 1 To CountSurfaces(mish)
su=GetSurface(mish,nsurf)
For tq = 0 To CountTriangles(su)-1
txv(0) = TriangleVertex(su,tq,0)
txv(1) = TriangleVertex(su,tq,1)
txv(2) = TriangleVertex(su,tq,2)
vq.TRIS = New TRIS
 
vq\x0# = VertexX(su,txv(0))
vq\y0# = VertexY(su,txv(0))
vq\z0# = VertexZ(su,txv(0))
vq\u0# = VertexU(su,txv(0),0)
vq\v0# = VertexV(su,txv(0),0)
vq\u20# = VertexU(su,txv(0),1)
vq\v20# = VertexV(su,txv(0),1)

vq\x1# = VertexX(su,txv(1))
vq\y1# = VertexY(su,txv(1))
vq\z1# = VertexZ(su,txv(1))
vq\u1# = VertexU(su,txv(1),0)
vq\v1# = VertexV(su,txv(1),0)
vq\u21# = VertexU(su,txv(1),1)
vq\v21# = VertexV(su,txv(1),1)

vq\x2# = VertexX(su,txv(2))
vq\y2# = VertexY(su,txv(2))
vq\z2# = VertexZ(su,txv(2))
vq\u2# = VertexU(su,txv(2),0)
vq\v2# = VertexV(su,txv(2),0)
vq\u22# = VertexU(su,txv(2),1)
vq\v22# = VertexV(su,txv(2),1)
Next

ClearSurface su

For vq.tris = Each tris

vt1=findvert(su,vq\x0#,vq\y0#,vq\z0#,vq\u0#,vq\v0#,vq\u20#,vq\v20#)

If vt1=-1 Then
vt1=AddVertex(su,vq\x0#,vq\y0#,vq\z0#,vq\u0#,vq\v0#)
VertexTexCoords su,mycount,vq\u20#,vq\v20#,0,1
vt1 = mycount
mycount = mycount +1
EndIf

vt2=findvert(su,vq\x1#,vq\y1#,vq\z1#,vq\u1#,vq\v1#,vq\u21#,vq\v21#)
If Vt2=-1 Then
vt2=AddVertex( su,vq\x1#,vq\y1#,vq\z1#,vq\u1#,vq\v1#)
VertexTexCoords su,mycount,vq\u21#,vq\v21#,0,1
vt2 = mycount
mycount = mycount +1
EndIf

vt3=findvert(su,vq\x2#,vq\y2#,vq\z2#,vq\u2#,vq\v2#,vq\u22#,vq\v22#)

If vt3=-1 Then 
vt3=AddVertex(su,vq\x2#,vq\y2#,vq\z2#,vq\u2#,vq\v2#)
VertexTexCoords su,mycount,vq\u22#,vq\v22#,0,1
vt3 = mycount
mycount = mycount +1
EndIf

AddTriangle su,vt1,vt2,vt3

Next

Delete Each tris
mycount=0
Next
End Function

Function findvert(su,x2#,y2#,z2#,u2#,v2#,u22#,v22#)
Local thresh# =0.001

For t=0 To CountVertices(su)-1
If Abs(VertexX(su,t)-x2#)<thresh# Then 
If Abs(VertexY(su,t)-y2#)<thresh# Then 
If Abs(VertexZ(su,t)-z2#)<thresh# Then 
If Abs(VertexU(su,t,0)-u2#)<thresh# Then 
If Abs(VertexV(su,t,0)-v2#)<thresh# Then 
If Abs(VertexU(su,t,1)-u22#)<thresh# Then 
If Abs(VertexV(su,t,1)-v22#)<thresh# Then
Return t
EndIf
EndIf
EndIf
EndIf
EndIf
EndIf
EndIf
Next
Return -1
End Function

Function debugMeshUV( mesh)
	srf =GetSurface(mesh,1)

		For v=0 To CountVertices(srf)-2
			DebugLog "U1>"+VertexU(srf,v,0)
			DebugLog "U2>"+VertexU(srf,v,0)
			DebugLog "V1>"+VertexV(srf,v,0)
			DebugLog "V2>"+VertexV(srf,v,0)
			VertexTexCoords srf,v,Rnd(0.3),Rnd(0.3)
		Next


	
End Function


Function debugLeafs(doLog=True)
If doLog=4
	For leaf.leaf =Each leaf
		DebugLog "=-=-=-=-=-=-=-=-=-=-"
		If leaf\on
			DebugLog "Active leaf"
		EndIf
		DebugLog "X:"+leaf\x+" Y:"+leaf\y+" W:"+leaf\w+" H:"+leaf\h
	Next
EndIf
	;Return
	If Not MouseDown(1) Return
	For leaf.leaf =Each leaf
		Color 128,128,128
		Rect leaf\x,leaf\y,leaf\w,leaf\h
		Color 255,255,255
		Rect leaf\x,leaf\y,leaf\w,leaf\h,0
	Next
End Function


Function Unweld(mesh)
;Unweld a mesh, retaining all of its textures coords and textures
For surfcount = 1 To CountSurfaces(mesh)
surf = GetSurface(mesh,surfcount)

count = CountTriangles(surf)
bank = CreateBank((15*count)*4)
For tricount = 0 To count-1
off = (tricount*15)*4
in = TriangleVertex(surf,tricount,0)
x# = VertexX(surf,in):y#=VertexY(surf,in):z#=VertexZ(surf,in)
u# = VertexU(surf,in):v#=VertexV(surf,in)
PokeFloat(bank,off,x)
PokeFloat(bank,off+4,y)
PokeFloat(bank,off+8,z)
PokeFloat(bank,off+12,u)
PokeFloat(bank,off+16,v)

in = TriangleVertex(surf,tricount,1)
x# = VertexX(surf,in):y#=VertexY(surf,in):z#=VertexZ(surf,in)
u# = VertexU(surf,in):v#=VertexV(surf,in)
PokeFloat(bank,off+20,x)
PokeFloat(bank,off+24,y)
PokeFloat(bank,off+28,z)
PokeFloat(bank,off+32,u)
PokeFloat(bank,off+36,v)

in = TriangleVertex(surf,tricount,2)
x# = VertexX(surf,in):y#=VertexY(surf,in):z#=VertexZ(surf,in)
u# = VertexU(surf,in):v#=VertexV(surf,in)
PokeFloat(bank,off+40,x)
PokeFloat(bank,off+44,y)
PokeFloat(bank,off+48,z)
PokeFloat(bank,off+52,u)
PokeFloat(bank,off+56,v)
Next

ClearSurface(surf,True,True)

For tricount = 0 To count-1
off = (tricount*15)*4
x# = PeekFloat(bank,off)
y# = PeekFloat(bank,off+4)
z# = PeekFloat(bank,off+8)
u# = PeekFloat(bank,off+12)
v# = PeekFloat(bank,off+16)
a = AddVertex(surf,x,y,z,u,v)
x# = PeekFloat(bank,off+20)
y# = PeekFloat(bank,off+24)
z# = PeekFloat(bank,off+28)
u# = PeekFloat(bank,off+32)
v# = PeekFloat(bank,off+36)
b = AddVertex(surf,x,y,z,u,v)
x# = PeekFloat(bank,off+40)
y# = PeekFloat(bank,off+44)
z# = PeekFloat(bank,off+48)
u# = PeekFloat(bank,off+52)
v# = PeekFloat(bank,off+56)
c = AddVertex(surf,x,y,z,u,v)
AddTriangle(surf,a,b,c)
Next
FreeBank bank

Next
;UpdateNormals mesh


Return mesh
End Function

Function TNormX#()
	Return g_TriNormalX
End Function
Function TNormY#()
	Return g_TriNormalY
End Function
Function TNormZ#()
	Return g_TriNormalZ
End Function

; Load in animation sequences
;Idle=ExtractAnimSeq(gsg9,100,159)
;Run=ExtractAnimSeq(gsg9,2,37)
;Jump=ExtractAnimSeq(gsg9,38,99)
;Crouch=ExtractAnimSeq(gsg9,161,190)
;CrouchWalk=ExtractAnimSeq(gsg9,192,220)




; PickedU(), PickedV(), PickedW() commands 
;
; Created by Mikkel Fredborg
; 
; Use as you please, but please include a thank you :)
;

;
; PickedTri type
; Necessary for the PickedU(), PickedV(), and PickedW() commands
Type PickedTri
Field ent,surf,tri;picked entity, surface and triangle
Field px#,py#,pz#    ;picked xyz
Field pu#[1],  pv#[1]  ,pw#[1]  ;picked uvw x 2

Field vx#[2],  vy#[2]  ,vz#[2]  ;vertex xyz
Field vnx#[2], vny#[2] ,vnz#[2] ;vertex normals
Field vu#[5],  vv#[5]  ,vw#[5]  ;vertex uvw x 2
End Type



;
; Returns the Texture U coordinate of the last successful pick command
; coordset may be set to either 0 or 1
Function PickedU#(coordset = 0)

; if something new has been picked then calculate the new uvw coordinates


Return ptri\pu[coordset]

End Function

;
; Returns the Texture U coordinate of the last successful pick command
; coordset may be set to either 0 or 1
Function PickedV#(coordset = 0)

; if something new has been picked then calculate the new uvw coordinates

Return ptri\pv[coordset]

End Function

;
; Returns the Texture U coordinate of the last successful pick command
; coordset may be set to either 0 or 1
Function PickedW#(coordset = 0)

; if something new has been picked then calculate the new uvw coordinates


Return ptri\pw[coordset]

End Function

;
; Calculates the UVW coordinates of a pick
; Do not call this by yourself, as PickedU(), PickedV(), and PickedW()
; takes care of calling it when nescessary
Function PickedUVW(ent,surf,tri,x#,y#,z#,nx#,ny#,nz#)

If surf
ptri\ent  = ent
ptri\surf = surf
ptri\tri  = tri


ptri\px = x
ptri\py = y
ptri\pz = z

For i = 0 To 2
TFormPoint VertexX(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i)),VertexY(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i)),VertexZ(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i)),ptri\ent,0

ptri\vx[i] = TFormedX()
ptri\vy[i] = TFormedY()
ptri\vz[i] = TFormedZ()

ptri\vnx[i] = VertexNX(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i))
ptri\vny[i] = VertexNY(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i))
ptri\vnz[i] = VertexNZ(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i))

ptri\vu[i+0] = VertexU(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i),0)
ptri\vv[i+0] = VertexV(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i),0)
ptri\vw[i+0] = VertexW(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i),0)

ptri\vu[i+3] = VertexU(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i),1)
ptri\vv[i+3] = VertexV(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i),1)
ptri\vw[i+3] = VertexW(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i),1)
Next

; Select which component of xyz coordinates to ignore
Local coords = 3

If Abs(NX) > Abs(NY)
If Abs(NX)>Abs(NZ) Then coords = 1
Else
If Abs(NY)>Abs(NZ) Then coords = 2
EndIf

Local a0#,a1#,b0#,b1#,c0#,c1#

; xy components
If (coords = 3)
; edge 0
a0# = ptri\vx[1] - ptri\vx[0]
a1# = ptri\vy[1] - ptri\vy[0]

; edge 1
b0# = ptri\vx[2] - ptri\vx[0]
b1# = ptri\vy[2] - ptri\vy[0]

; picked offset from triangle vertex 0
c0# =ptri\px - ptri\vx[0]
c1# = ptri\py - ptri\vy[0]
Else
; xz components
If (coords = 2)
; edge 0
a0# = ptri\vx[1] - ptri\vx[0]
a1# = ptri\vz[1] - ptri\vz[0]

; edge 1
b0# = ptri\vx[2] - ptri\vx[0]
b1# = ptri\vz[2] - ptri\vz[0]

; picked offset from triangle vertex 0
c0# = ptri\px - ptri\vx[0]
c1# = ptri\pz - ptri\vz[0]
Else
; yz components

; edge 0
a0# = ptri\vy[1] - ptri\vy[0]
a1# = ptri\vz[1] - ptri\vz[0]

; edge 1
b0# = ptri\vy[2] - ptri\vy[0]
b1# = ptri\vz[2] - ptri\vz[0]

; picked offset from triangle vertex 0
c0# = ptri\py - ptri\vy[0]
c1# = ptri\pz - ptri\vz[0]
End If
End If

;
; u and v are offsets from vertex 0 along edge 0 and edge 1
; using these it is possible to calculate the Texture UVW coordinates
; of the picked XYZ location
;
; a0*u + b0*v = c0
; a1*u + b1*v = c1
;
; solve equation (standard equation with 2 unknown quantities)
; check a math book to see why the following is true
;
Local u# = (c0*b1 - b0*c1) / (a0*b1 - b0*a1)
Local v# = (a0*c1 - c0*a1) / (a0*b1 - b0*a1)

; If either u or v is out of range then the
; picked entity was not a mesh, and therefore
; the uvw coordinates cannot be calculated
If (u<0.0 Or u>1.0) Or (v<0.0 Or v>1.0)
Return 
End If

; Calculate picked uvw's for coordset 0 (and modulate them to be in the range of 0-1 nescessary)
ptri\pu[0] = (ptri\vu[0] + ((ptri\vu[1] - ptri\vu[0]) * u) + ((ptri\vu[2] - ptri\vu[0]) * v)) Mod 1
ptri\pv[0] = (ptri\vv[0] + ((ptri\vv[1] - ptri\vv[0]) * u) + ((ptri\vv[2] - ptri\vv[0]) * v)) Mod 1
ptri\pw[0] = (ptri\vw[0] + ((ptri\vw[1] - ptri\vw[0]) * u) + ((ptri\vw[2] - ptri\vw[0]) * v)) Mod 1

; If any of the coords are negative
If ptri\pu[0]<0.0 Then ptri\pu[0] = 1.0 + ptri\pu[0]
If ptri\pv[0]<0.0 Then ptri\pv[0] = 1.0 + ptri\pv[0]
If ptri\pw[0]<0.0 Then ptri\pw[0] = 1.0 + ptri\pw[0]

; Calculate picked uvw's for coordset 1 (and modulate them to be in the range of 0-1 nescessary)
ptri\pu[1] = (ptri\vu[3] + ((ptri\vu[4] - ptri\vu[3]) * u) + ((ptri\vu[5] - ptri\vu[3]) * v)) Mod 1
ptri\pv[1] = (ptri\vv[3] + ((ptri\vv[4] - ptri\vv[3]) * u) + ((ptri\vv[5] - ptri\vv[3]) * v)) Mod 1
ptri\pw[1] = (ptri\vw[3] + ((ptri\vw[4] - ptri\vw[3]) * u) + ((ptri\vw[5] - ptri\vw[3]) * v)) Mod 1

; If any of the coords are negative
If ptri\pu[1]<0.0 Then ptri\pu[1] = 1.0 + ptri\pu[1]
If ptri\pv[1]<0.0 Then ptri\pv[1] = 1.0 + ptri\pv[1]
If ptri\pw[1]<0.0 Then ptri\pw[1] = 1.0 + ptri\pw[1]
End If

End Function




;
