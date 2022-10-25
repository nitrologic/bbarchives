; ID: 2457
; Author: Yasha
; Date: 2009-04-10 12:11:44
; Title: Vertex animation
; Description: MD2-like animation using VertexCoords

Const MAXFRAMES=100
Const MAXVERTS=1000
Const MAXSEQS=10

Global appheight=768
Global appwidth=1024
Global appdepth=32

AppTitle "Vertex morph animation";,"Are you sure you want to quit?"

Type MDX
	Field mesh,surf,verts			;Mesh, surface and texture handles; Total no. vertices
	Field tex,texname$				;Texture handle, and filename. Add more slots if using more than one texture
	Field cframe#,frames			;Current frame, total no. frames
	Field fr.TFrame[MAXFRAMES]		;All frames
	Field cseq,seqs					;Current anim sequence, total no. sequences
	Field seq.sequence[MAXSEQS]		;Animation sequences
	Field speed#,mode,dir			;Animation speed, mode (0=stop,1=loop,2=ping-pong,3=one-shot), direction (mode 2)
	Field tlen,tt					;Transition length and time (for tweening between sequences)
End Type

Type TFrame
	Field vx#[MAXVERTS],vy#[MAXVERTS],vz#[MAXVERTS]		;Position
	Field nx#[MAXVERTS],ny#[MAXVERTS],nz#[MAXVERTS]		;Normal
End Type

Type sequence
	Field start		;First frame
	Field finish	;Last frame
	Field speed#	;Default play speed
End Type


SC_FPS=60	;Desired framerate
rtime=Floor(1000.0/SC_FPS)
limited=True

Graphics3D appwidth,appheight,appdepth,6
SetBuffer BackBuffer()

centrecam=CreatePivot()
PositionEntity centrecam,0,15,0
camera=CreateCamera(centrecam)
PositionEntity camera,0,20,-50,1

sun=CreateLight()
PositionEntity sun,-100,400,0
PointEntity sun,centrecam

ground=CreateMesh()
parquet=CreateSurface(ground)
v1=AddVertex(parquet,-125,0,150):v2=AddVertex(parquet,125,0,150):v3=AddVertex(parquet,125,0,-100)
AddTriangle(parquet,v1,v2,v3):v2=AddVertex(parquet,-125,0,-100):AddTriangle(parquet,v1,v3,v2)
EntityColor ground,0,0,255
block=CreateCube():ScaleMesh block,20,5,20


dragon.MDX=LoadMDX("dragon.mdx"):d=dragon\mesh:PositionEntity d,0,17,0:ScaleMDXMesh(dragon,0.5,0.5,0.5)
dragon\texname="dragon.bmp":dragon\tex=LoadTexture(dragon\texname):EntityTexture d,dragon\tex


While Not KeyDown(1)
	ctime=MilliSecs()

	MoveEntity camera,0,KeyDown(200)-KeyDown(208),KeyDown(30)-KeyDown(44)
	TurnEntity centrecam,0,KeyDown(203)-KeyDown(205),0
	MoveEntity centrecam,0,(KeyDown(31)-KeyDown(45))*0.2,0
	PointEntity camera,centrecam
	
	If KeyHit(57) Then AnimateMDX(dragon.MDX,1,0.1,0,0)
	If KeyHit(28) Then AnimateMDX(dragon.MDX,1,0.1,0,20)	;Tween!
	If KeyHit(38) Then limited=Not limited					;Turn off frame limit
	
	If MilliSecs()-render_time=>1000 Then fps=frames:frames=0:render_time=MilliSecs():Else frames=frames+1
	
	UpdateMDX
	UpdateWorld
	RenderWorld
	
	Text 0,30,"FPS: "+fps
	Text 0,60,"Current frame: "+dragon\cframe
	
	n=rtime-(MilliSecs()-ctime)		;Free spare CPU time
	Delay n-(limited+1)
	
	Flip limited
Wend

End


Function LoadMDX.MDX(fname$)	;Load an MDX file
	meshbank=CreateBank(FileSize(fname))
	filein=ReadFile(fname)
	ReadBytes(meshbank,filein,0,BankSize(meshbank))
	CloseFile filein
	
	scale#=PeekFloat(meshbank,0)
	sfac#=PeekFloat(meshbank,4)
	
	ent.MDX=New MDX
	ent\mesh=CreateMesh():s=CreateSurface(ent\mesh):ent\surf=s
	
	ent\frames=PeekShort(meshbank,8)	;Number of frames
	fsize=PeekInt(meshbank,10)			;Size of a frame in bytes
	ent\verts=PeekShort(meshbank,14)	;No. verts
	ent\seqs=PeekShort(meshbank,28)		;No. anim sequences
	
	For v=0 To ent\verts-1		;Store the UVs, don't bother with other data yet
		vu#=PeekShort(meshbank,34+v*4)/65535.0
		vv#=PeekShort(meshbank,36+v*4)/65535.0
		AddVertex(s,0,0,0,vu,vv)
	Next
	
	np=PeekInt(meshbank,16)
	ts=PeekShort(meshbank,np)		;No. tris
	For t=0 To ts-1		;Triangle info
		v0=PeekShort(meshbank,np+2+t*6)
		v1=PeekShort(meshbank,np+4+t*6)
		v2=PeekShort(meshbank,np+6+t*6)
		AddTriangle(s,v0,v1,v2)
	Next
	
	np=PeekInt(meshbank,20):nlen=0		;Texture data offset
	texnum=PeekByte(meshbank,np)		;Number of textures
	For t=0 To texnum-1
		pos=PeekInt(meshbank,np+1+t*4)
		namelen=PeekShort(meshbank,pos)
		ent\texname=""					;Add more texture slots if intending to use more than one texture
		For v=0 To namelen-1
			ent\texname=ent\texname+Chr(PeekByte(meshbank,pos+2+v))
		Next
		If FileType(ent\texname)=1 Then ent\tex=LoadTexture(ent\texname):EntityTexture ent\mesh,ent\tex
	Next
	
	np=PeekInt(meshbank,24)
	For f=0 To ent\frames-1		;Frame data
		ent\fr[f]=New TFrame
		For v=0 To ent\verts-1
			pos=np+(f*ent\verts+v)*9
			ent\fr[f]\vx[v]=PeekSShort(meshbank,pos)/sfac
			ent\fr[f]\vy[v]=PeekSShort(meshbank,pos+2)/sfac
			ent\fr[f]\vz[v]=PeekSShort(meshbank,pos+4)/sfac
			ent\fr[f]\nx[v]=PeekSByte(meshbank,pos+6)/127.0
			ent\fr[f]\ny[v]=PeekSByte(meshbank,pos+7)/127.0
			ent\fr[f]\nz[v]=PeekSByte(meshbank,pos+8)/127.0
		Next
	Next
	ent\fr[MAXFRAMES]=New TFrame		;Temporary frame (for transition tweening)
	
	np=PeekInt(meshbank,30)
	For sq=0 To ent\seqs-1
		ent\seq[sq]=New sequence
		ent\seq[sq]\start=PeekShort(meshbank,np+sq*8)
		ent\seq[sq]\finish=PeekShort(meshbank,np+sq*8+2)
		ent\seq[sq]\speed=PeekFloat(meshbank,np+sq*8+4)
	Next
	
	For v=0 To ent\verts-1		;Set up the mesh at frame 0 (entirely optional)
		VertexCoords s,v,ent\fr[0]\vx[v],ent\fr[0]\vy[v],ent\fr[0]\vz[v]
		VertexNormal s,v,ent\fr[0]\nx[v],ent\fr[0]\ny[v],ent\fr[0]\nz[v]
	Next
	
	FreeBank meshbank
	Return ent
End Function

Function SaveMDX(ent.MDX,fname$)	;Save an MDX to file after changing it
	meshbank=CreateBank(34)
	numframes=ent\frames
	
	SetMDXFrame(ent,0)
	scale#=MaxRadius(ent\mesh):sfac#=32765.0/scale	;Prevent rounding errors that might result in changing sign
	PokeFloat meshbank,0,scale:PokeFloat meshbank,4,sfac
	
	s=ent\surf							;Only one surface for now
	vs=ent\verts
	PokeShort meshbank,8,numframes		;Number of frames
	PokeInt meshbank,10,vs*9			;Size of a frame in bytes
	PokeShort meshbank,28,ent\seqs		;Number of anim sequences (only one, no way to get sequence data from an b3d. Change it later if necessary)
	
	PokeShort meshbank,14,vs:ResizeBank meshbank,34+(vs*4)+2
	
	For v=0 To vs-1		;Store the UVs, don't bother with other data yet
		PokeShort meshbank,34+v*4,VertexU(s,v)*65535
		PokeShort meshbank,36+v*4,VertexV(s,v)*65535
	Next
	
	ts=CountTriangles(s):np=BankSize(meshbank)
	PokeShort meshbank,np-2,ts:ResizeBank meshbank,np+ts*6
	PokeInt meshbank,16,np-2	;Offset for triangle data
	For t=0 To ts-1		;Triangle data
		PokeShort meshbank,np+t*6,TriangleVertex(s,t,0)
		PokeShort meshbank,np+2+t*6,TriangleVertex(s,t,1)
		PokeShort meshbank,np+4+t*6,TriangleVertex(s,t,2)
	Next
	FreeEntity mesh
	
	pos=BankSize(meshbank):PokeInt meshbank,20,pos	;Offset for texture name
	texnum=1	;Need to make a couple of changes to the system to manage more textures
	ResizeBank meshbank,pos+1+texnum*4
	PokeByte(meshbank,pos,texnum)
	For t=0 To texnum-1
		np=BankSize(meshbank):PokeInt meshbank,pos+1+t*4,np
		ResizeBank meshbank,np+2+Len(ent\texname):PokeShort(meshbank,np,Len(ent\texname))
		For v=1 To Len(ent\texname)
			PokeByte(meshbank,np+1+v,Asc(Mid(ent\texname,v,1)))
		Next
	Next
	
	np=BankSize(meshbank):PokeInt meshbank,24,np	;Offset for frame data
	ResizeBank meshbank,np+ent\frames*vs*9
	
	For f=0 To numframes-1		;Frame data
		For v=0 To vs-1
			pos=np+(f*vs+v)*9
			PokeSShort meshbank,pos,ent\fr[f]\vx[v]*sfac
			PokeSShort meshbank,pos+2,ent\fr[f]\vy[v]*sfac
			PokeSShort meshbank,pos+4,ent\fr[f]\vz[v]*sfac
			PokeSByte meshbank,pos+6,ent\fr[f]\nx[v]*127
			PokeSByte meshbank,pos+7,ent\fr[f]\ny[v]*127
			PokeSByte meshbank,pos+8,ent\fr[f]\nz[v]*127
		Next
	Next
	
	np=BankSize(meshbank):PokeInt meshbank,30,np	;Offset for sequence data
	ResizeBank meshbank,np+8*ent\seqs
	For s=0 To ent\seqs
		PokeShort meshbank,np+8*ent\seqs,ent\seq[s]\start:PokeShort meshbank,np+2+8*ent\seqs,ent\seq[s]\finish
		PokeFloat meshbank,np+4+8*ent\seqs,ent\seq[s]\speed		;Call it one long animseq because I don't know if/how B3D stores those
	Next
	
	fileout=WriteFile(mdxfile)
	WriteBytes(meshbank,fileout,0,BankSize(meshbank))
	CloseFile fileout
	FreeBank meshbank
End Function

Function AnimateMDX(ent.MDX,mode=1,speed#=1,seq=0,tlen=0)	;Animate an MDX
	ent\mode=mode
	ent\speed=speed
	ent\cseq=seq
	ent\dir=Sgn(ent\speed)
	If ent\dir=1
		ent\cframe=ent\seq[seq]\start
	Else
		ent\cframe=ent\seq[seq]\finish
	EndIf
	ent\tlen=tlen
	If tlen
		For v=0 To ent\verts-1
			ent\fr[MAXFRAMES]\vx[v]=VertexX(ent\surf,v):ent\fr[MAXFRAMES]\vy[v]=VertexY(ent\surf,v):ent\fr[MAXFRAMES]\vz[v]=VertexZ(ent\surf,v)
			ent\fr[MAXFRAMES]\nx[v]=VertexNX(ent\surf,v):ent\fr[MAXFRAMES]\ny[v]=VertexNY(ent\surf,v):ent\fr[MAXFRAMES]\nz[v]=VertexNZ(ent\surf,v)
		Next
		ent\tt=0
	EndIf
End Function

Function MDXAnimTime#(ent.MDX)		;Return the current point of the animation as a float, like MD2AnimTime
	If ent\tlen Then Return -1
	Return ent\cframe
End Function

Function MDXAnimLength(ent.MDX,seq=-1)		;Total number of frames held in the MDX, or in the specified sequence
	If seq<0 Or seq>ent\seqs-1
		Return ent\frames
	Else
		Return ent\seq[seq]\finish-ent\seq[seq]\start
	EndIf
End Function

Function MDXAnimating(ent.MDX)		;Return true if MDX is currently animating
	If ent\mode>0 Then Return True:Else Return False
End Function

Function SetMDXFrame(ent.MDX,frame#)	;Manually set animation to a specific point (stops animation)
	ent\mode=0
	ent\cframe=frame
	For v=0 To ent\verts
		VertexCoords ent\surf,v,ent\fr[frame]\vx[v],ent\fr[frame]\vy[v],ent\fr[frame]\vz[v]
		VertexNormal ent\surf,v,ent\fr[frame]\nx[v],ent\fr[frame]\ny[v],ent\fr[frame]\nz[v]
	Next
End Function

Function AddMDXSeq(ent.MDX,fframe,lframe,speed#=1)	;Define an MDX sequence by first and last frames
	ent\seq[ent\seqs]\start=fframe
	ent\seq[ent\seqs]\finish=lframe
	ent\seq[ent\seqs]\speed=speed
	ent\seqs=ent\seqs+1
End Function

Function GetMDXSeq(ent.MDX)		;Return which sequence the MDX is currently playing
	Return ent\cseq
End Function

Function UpdateMDX(updatespeed#=1.0)	;Use this instead of/in addition to UpdateWorld, as that obviously doesn't control MDX animation
	For ent.MDX=Each MDX
		animspeed#=ent\seq[ent\cseq]\speed*ent\speed*updatespeed
		If ent\mode
			If Not ent\tlen
				If ent\mode=2	;Ping-pong
					If animspeed<0 Then animspeed=-animspeed:ent\dir=-ent\dir	;Just reverse the direction, simpler
					ent\cframe=ent\cframe+animspeed*ent\dir
					If ent\cframe>=ent\seq[ent\cseq]\finish-1 Then ent\dir=-1:ent\cframe=ent\seq[ent\cseq]\finish-1
					If ent\cframe<=ent\seq[ent\cseq]\start Then ent\dir=1:ent\cframe=ent\seq[ent\cseq]\start
					
					If ent\dir=1
						frame1=Floor(ent\cframe):frame2=frame1+1:frametween#=ent\cframe-frame1
					Else
						frame1=Ceil(ent\cframe):frame2=frame1-1:frametween#=frame1-ent\cframe
					EndIf
				Else	;Linear
					ent\cframe=ent\cframe+animspeed
					If ent\dir=1	;Going forwards
						If ent\cframe>=ent\seq[ent\cseq]\finish Then ent\cframe=ent\seq[ent\cseq]\start:If ent\mode=3 Then ent\mode=0
						frame1=Floor(ent\cframe)
						If frame1<ent\seq[ent\cseq]\finish-1 Then frame2=frame1+1:Else frame2=ent\seq[ent\cseq]\start
						frametween#=ent\cframe-frame1
					Else	;Going backwards
						If ent\cframe<ent\seq[ent\cseq]\start
							ent\cframe=ent\seq[ent\cseq]\finish-(ent\seq[ent\cseq]\start-ent\cframe)
							If ent\mode=3 Then ent\mode=0
						EndIf
						frame2=Floor(ent\cframe)
						If frame2<ent\seq[ent\cseq]\finish-1 Then frame1=frame2+1:Else frame1=ent\seq[ent\cseq]\start
						frametween#=1-(ent\cframe-frame2)
					EndIf
				EndIf
			Else	;Tween to next sequence
				frame1=MAXFRAMES
				frame2=ent\seq[ent\cseq]\start
				ent\tt=ent\tt+1
				frametween#=Float(ent\tt)/ent\tlen
				If ent\tt>=ent\tlen Then ent\tlen=0:ent\tt=0
			EndIf
			
			f1.TFrame=ent\fr[frame1]:f2.TFrame=ent\fr[frame2]	;When repeatedly accessing type fields, keep the path simple - faster
			For v=0 To ent\verts-1
				vx#=(f1\vx[v]*(1-frametween))+(f2\vx[v]*frametween)
				vy#=(f1\vy[v]*(1-frametween))+(f2\vy[v]*frametween)
				vz#=(f1\vz[v]*(1-frametween))+(f2\vz[v]*frametween)
				nx#=(f1\nx[v]*(1-frametween))+(f2\nx[v]*frametween)
				ny#=(f1\ny[v]*(1-frametween))+(f2\ny[v]*frametween)
				nz#=(f1\nz[v]*(1-frametween))+(f2\nz[v]*frametween)
				VertexCoords ent\surf,v,vx,vy,vz
				VertexNormal ent\surf,v,nx,ny,nz
			Next
		EndIf
	Next
End Function

Function ScaleMDXMesh(ent.MDX,xs#,ys#,zs#)	;ScaleMesh won't work, because the VertexCoords change every frame. Slow!
	For f=0 To ent\frames-1
		For v=0 To ent\verts-1
			ent\fr[f]\vx[v]=ent\fr[f]\vx[v]*xs
			ent\fr[f]\vy[v]=ent\fr[f]\vy[v]*ys
			ent\fr[f]\vz[v]=ent\fr[f]\vz[v]*zs
		Next
	Next
	frame=Floor(ent\cframe)
	For v=0 To ent\verts
		VertexCoords ent\surf,v,ent\fr[frame]\vx[v],ent\fr[frame]\vy[v],ent\fr[frame]\vz[v]
		VertexNormal ent\surf,v,ent\fr[frame]\nx[v],ent\fr[frame]\ny[v],ent\fr[frame]\nz[v]
	Next
End Function

Function CopyMDX.MDX(ent.MDX,newtex=False)
	ent2.MDX=New MDX
	ent2\mesh=CopyMesh(ent\mesh)	;Have to use CopyMesh as CopyEntity would use old mesh data
	ent2\surf=GetSurface(ent2\mesh,1):ent2\verts=CountVertices(ent2\surf)	;Change this if multisurface or multimesh MDX desired
	ent2\texname=ent\texname
	If newtex=True Then ent2\tex=LoadTexture(ent2\texname):Else ent2\tex=ent\tex	;Can share the texture. Remember not to free it in that case!
	EntityTexture ent2\mesh,ent2\tex
	ent2\frames=ent\frames
	For f=0 To ent2\frames-1
		ent2\fr[f]=New TFrame
		For v=0 To ent2\verts-1
			ent2\fr[f]\vx[v]=ent\fr[f]\vx[v]
			ent2\fr[f]\vy[v]=ent\fr[f]\vy[v]
			ent2\fr[f]\vz[v]=ent\fr[f]\vz[v]
		Next
	Next
	ent2\cseq=ent\cseq:ent2\seqs=ent\seqs
	For s=0 To ent2\seqs-1
		ent2\seq[s]=New sequence
		ent2\seq[s]\start=ent\seq[s]\start
		ent2\seq[s]\finish=ent\seq[s]\finish
		ent2\seq[s]\speed=ent\seq[s]\speed
	Next
	ent2\cframe=ent2\seq[ent2\cseq]\start
	Return ent2.MDX
End Function

Function FreeMDX(ent.MDX,freetex=True)
	If freetex And ent\tex<>0 Then FreeTexture ent\tex
	For s=0 To ent\seqs-1
		Delete ent\seq[s]
	Next
	For f=0 To ent\frames-1
		Delete ent\fr[f]
	Next
	FreeEntity ent\mesh
	Delete ent
End Function

Function PeekSShort(bank,offset)	;Return a signed short, range -32767,32767
	s=PeekShort(bank,offset)
	If s>32767 Then Return s-65535:Else Return s
End Function

Function PeekSByte(bank,offset)		;Return a signed byte, range -127,127
	b=PeekByte(bank,offset)
	If b>127 Then Return b-255:Else Return b
End Function

Function PokeSShort(bank,offset,value)	;Store a signed Short
	If value<0 Then value=value+65535
	PokeShort bank,offset,value
End Function

Function PokeSByte(bank,offset,value)	;Store a signed Byte
	If value<0 Then value=value+255
	PokeByte bank,offset,value
End Function

Function MaxRadius#(body)		;Get the largest radius of a mesh (ie. distance of furthest vertex)
	Local r#,cs%,s%,ver%		;Obviously only works on single meshes
	
	For cs=1 To CountSurfaces(body)
		s=GetSurface(body,cs)
		For ver=0 To CountVertices(s)-1
			dx#=VertexX(s,ver)
			dy#=VertexY(s,ver)
			dz#=VertexZ(s,ver)
			dd#=Sqr(dx*dx+dy*dy+dz*dz)
			If r<dd Then r=dd
		Next
	Next
	
	Return r#
End Function
