; ID: 335
; Author: David Bird(Birdie)
; Date: 2002-06-02 17:29:51
; Title: Truespace .cob / .scn loader
; Description: Import truespace objects (.cob) or scenes (.scn) into Blitz

;
;
;
;
;	TrueSpace binary cob importer
;	(c)2002 David Bird
;	enquire@davebird.fsnet.co.uk
;
;
;
;

Global EU_X#,EU_Y#,EU_Z#
Dim TS_Mat#(4,4)
	TS_Mat(4,1)=0
	TS_Mat(4,2)=0
	TS_Mat(4,3)=0
	TS_Mat(4,4)=1


Type ChunkHeader
	Field szChunkType$				; Identifies The Chunk Type 			4 byte string 
	Field sMajorVersion				; Version								2 bytes
	Field sMinorVersion				; Version								2 bytes
	Field lChunkID 					; Identifier: Each Chunk Is Unique 		4 bytes (int)
	Field lParentID 				; Parent, Some Chunks Own Each Other 	4 bytes (int)
	Field lDataBytes 				; Number Of Bytes In Data				4 bytes (int)
End Type

Type ChunkName
	Field sNameDupecount		; Dupecount (short)
	Field sStringLen			; Length Of String(short)
	Field name$					; Name
End Type 


Global ts_v.vertex[65535]
Global uts_v.texture[65535]
Global ts_f.face[200000]

Type vertex
	Field ps#[3]	;positional
End Type

Type texture
	Field ps#[2]
End Type

Type face
	Field hand
	Field count
	Field i[20]
	Field ti[20]
	Field matindex
	Field ts_index
	Field sf_index
End Type

Type brush
	Field id
	Field bru
	Field bRed
	Field bGrn
	Field bBlu
	Field bAlp#
	Field bShn#
	Field tex
	Field tFilename$
	Field tu#
	Field tv#
	Field tx#
	Field ty#
End Type

Type TS_entity
	Field ent
	Field id$
	Field par$
	Field IsLight
	Field IsMesh
End Type

Function ImportTrueSpace(filename$,opto=False)
	
	Delete Each TS_Entity
	fstrip$=GetLocalDirectoryName(filename$)
	If Len(fstrip)<Len(filename) Then
		WorkDirectory$=Left$(filename$,Len(filename$)-Len(fstrip))
	Else
		workDirectory=""
	EndIf

	file=ReadFile(WorkDirectory+fstrip)
	
	If file=0 Then RuntimeError "File Doesn't Exist."
	
	;
	;	Header Chunk
	;
	szIdentifier$	=TS_ReadBytes_L$(file,9)		; Always "Caligari "
	szVersion$		=TS_ReadBytes_L$(file,6)		; V00.01 Or Whatever Version The File Comes From
	chMode$			=TS_ReadBytes_L$(file,1)		; A: ASCII, B:BINARY. We Will Only Read Binary Files
	szBitMode$		=TS_ReadBytes_L$(file,2)		; LH: Little Endian, HL: Big Endian (Irrelevant) 
	szBlank$		=TS_ReadBytes_L$(file,13)		; 13 Blank Spaces
	chNewLine		=TS_ReadBytes_L$(file,1)		; CR chr$(13)+0
	CN.ChunkName=Null
	
	If chMode$<>"A" Then
		CHeader.ChunkHeader=ReadHeader(file)			;Read in the chunk header
		Local brush.brush[65535]
	
		Repeat
			stack=FilePos(file)
			TS_CHUNKEND=stack+CHeader\lDataBytes
			Select CHeader\szChunkType
				
				Case "PolH"	;**** THIS IS A MESH
					;Ensure we have a clean list of brushes at the beginning of an entity
					For bru.brush=Each brush
						If bru\bru<>0 Then FreeBrush bru\bru
						If bru\tex<>0 Then FreeTexture bru\tex
						Delete bru
					Next
					BrushCount=0
					;Cleanup faces vertices and texture coords
					Delete Each texture
					Delete Each vertex
					Delete Each face
	
					CN=ReadChunkName(file)
					;axis matrix
					For a=0 To 11
						TS_Axis[a]=ReadFloat(file)
					Next
					;position matrix
					For a=0 To 11
						TS_MATRIX[a]=ReadFloat(file)
					Next
					mesh=TS_ReadEntity(file);Read in mesh vertices /faces texture coords and colors
					NameEntity mesh,CN\name
					; Add it to the list correcting any parenting
					TS_TransformMesh mesh	;perform scaling etc and correct rotation

					thisEntity.ts_entity=PushEntity(mesh,CHeader)
					;Normals not supported I think.
;					UpdateNormals mesh				
					CreateNormal mesh
				Case "VCol" ; vertex colouring info
					VpaintCount=TS_Readlong(file)	;list count
					For a=0 To VpaintCount-1
						Findex=TS_ReadLong(file)	;the face to alter
						VCount=TS_Readlong(file)	;how many vertices in this face
						fc.face=ts_f[Findex]
						surf=GetSurface(mesh,fc\MatIndex+1)
						If surf=0 Then RuntimeError "Surface error."
						b=0
							vRed=ReadByte(file)
							vGrn=ReadByte(file)
							vBlu=ReadByte(file)
							vAlp=ReadByte(file)
							VertexColor surf,TriangleVertex(surf,fc\sf_index,b),vRed,vGrn,vBlu;,vAlp
						b=2
							vRed=ReadByte(file)
							vGrn=ReadByte(file)
							vBlu=ReadByte(file)
							vAlp=ReadByte(file)
							VertexColor surf,TriangleVertex(surf,fc\sf_index,b),vRed,vGrn,vBlu;,vAlp
						b=1
							vRed=ReadByte(file)
							vGrn=ReadByte(file)
							vBlu=ReadByte(file)
							vAlp=ReadByte(file)
							VertexColor surf,TriangleVertex(surf,fc\sf_index,b),vRed,vGrn,vBlu;,vAlp
					Next
					EntityFX mesh,2
				Case "Scen"
					mesh=CreatePivot()
					PushEntity mesh,CHeader
				Case "PhAS"
					mesh=CreatePivot()
					PushEntity mesh,CHeader
				Case "Grou"		;This is a pivot entity
					CN=ReadChunkName(file)
					;axis matrix
					For a=0 To 11
						TS_Axis[a]=ReadFloat(file)
					Next
					;position matrix
					For a=0 To 11
						TS_MATRIX[a]=ReadFloat(file)
					Next
					;Correct orientation of grouphandle
					mesh=CreatePivot()
					TS_TransformMesh mesh	;perform scaling etc and correct rotation
					thisEntity=PushEntity(mesh,CHeader)
				Case "Lght"		;This is a pivot entity
				Case "Mat1"
					tbrush.brush=ReadMaterial(file)
					ss=GetSurface(mesh,tbrush\id+1)
					PaintSurface ss,tbrush\bru
				Default
			End Select
	
			;Shift to next chunk.
			stack=stack+CHeader\lDataBytes
			SeekFile file,stack
			;
			
			CHeader.ChunkHeader=ReadHeader(file)			;Read in the chunk header
		Until CHeader\szChunkType="END "

	Else ; Does not support Ascii format
		RuntimeError "Can not import ASCII cob files."
	End If
	
	;close file if its open
	If file=0 Then CloseFile file

	For bru.brush=Each brush
		If bru\bru<>0 Then FreeBrush bru\bru
		If bru\tex<>0 Then FreeTexture bru\tex
		Delete bru
	Next
	Delete Each ChunkHeader
	Delete Each ChunkName
	Delete Each texture
	Delete Each vertex
	Delete Each face

	entity.TS_Entity=First TS_Entity
	If entity<>Null Then 
		ent=entity\ent
		Delete Each TS_entity
	EndIf
	RotateEntity ent,-90,180,180
;	TurnEntity ent,0,0,-180
	Return ent
End Function

Function PushEntity.TS_Entity(mesh,ch.ChunkHeader)
	If ch\lChunkID=0 And ch\lParentID=0 Then 
		e.TS_Entity=New TS_Entity
		e\ent	=mesh
		e\id	=ch\lChunkID
		e\par	=ch\lParentID
		;
		EntityParent e\ent,0,False
		;
		Return
	Else
		For fe.TS_entity=Each TS_entity
			If fe\id=ch\lParentID Then
				e.TS_Entity=New TS_Entity
				e\ent	=mesh
				e\id	=ch\lChunkID
				e\par	=ch\lParentID
				;
				EntityParent e\ent,fe\ent,False
				;
				Return
			EndIf
		Next
		e.TS_Entity=New TS_Entity
		e\ent	=mesh
		e\id	=ch\lChunkID
		e\par	=0
		;
		EntityParent e\ent,0,False
	End If
	Return e
End Function

Function TS_ReadBytes_L$(file,Ln)
	b$=""
	For a=0 To ln-1
		b$=b$+Chr$(ReadByte(file))
	Next
	Return b$
End Function

Function TS_ReadShort(file)
	lw=ReadByte(file)
	hg=ReadByte(file) Shl 8
	Return lw Or hg
End Function

Function TS_ReadInt(file)
	lw=ReadByte(file)
	hg=(ReadByte(file)) Shl 8
	Return lw Xor hg
End Function

Function TS_ReadLong(file)
	a=ReadByte(file)
	b=ReadByte(file) Shl 8
	c=ReadByte(file) Shl 16
	d=ReadByte(file) Shl 24
	long = a Or b
	long = long Or c
	long = long Or d
	Return long
End Function

Function TS_ReadFace.face(file)
	f.face=New face
	f\hand=ReadByte(file)					;flags ???
	f\count=TS_ReadShort(file)

	If f\count>3 Then RuntimeError "This importer only supports triangluated entities."

	If (f\hand And F_HOLE)=0 Then ;bit 4 ok
		f\matindex=TS_ReadShort(file)
		For a=0 To f\count-1
			f\i[a]=TS_ReadLong(file)
			f\ti[a]=TS_ReadLong(file)
		Next
	End If
	Return f
End Function

Function TS_ReadString$(file)
	Repeat
		byte=ReadByte(file)
		If byte=0 Then Return f$
		f$=f$+Chr$(byte)
	Until byte=0
	Return f$
End Function





;Read In Chunk Header
Function ReadHeader.ChunkHeader(file)
	H.ChunkHeader=New ChunkHeader
	H\szChunkType=TS_ReadBytes_L(file,4)

	H\sMajorVersion=TS_ReadShort(file)			;
	H\sMinorVersion=TS_ReadShort(file)			;
	H\lChunkID=TS_ReadLong(file) 				; Identifier: Each Chunk Is Unique 		
	H\lParentID=TS_ReadLong(file) 				; Parent, Some Chunks Own Each Other 	
	H\lDataBytes=TS_ReadLong(file)				; Number Of Bytes In Data				
	Return H
End Function

Function ReadChunkName.ChunkName(file)
	CN.ChunkName=New ChunkName
	CN\sNameDupecount=TS_ReadShort(file)		; Dupecount (short)
	CN\sStringLen=TS_ReadShort(file)			; Length Of String(short)
	CN\name=TS_ReadBytes_L(file,CN\sStringLen)	; Name
	Return CN
End Function


;requires TS_MATRIX[] and TS_AXIS[] to be loaded with 
;the transform matrix and axis info before calling.
Function TS_TransformMesh(mesh)
;	PositionEntity mesh,-TS_AXIS[0],-TS_AXIS[1],-TS_AXIS[2],True
End Function

Function TS_PosNSet(surf,x#,y#,z#)
	nx#=(TS_MATRIX[00]*x)+(TS_MATRIX[01]*y)+(TS_MATRIX[02]*z)+ts_Matrix[03]
	ny#=(TS_MATRIX[04]*x)+(TS_MATRIX[05]*y)+(TS_MATRIX[06]*z)+ts_Matrix[07]
	nz#=(TS_MATRIX[08]*x)+(TS_MATRIX[09]*y)+(TS_MATRIX[10]*z)+ts_Matrix[11]
	thisV=AddVertex(surf,-nx,-ny,nz)
	Return thisV
End Function

;Strip path from a string and return only the filename
Function GetLocalDirectoryName$(a$)
	b$=a$
	If Instr(a$,"\")=0 Then 
		Return a$
	Else
		Repeat
			a$=Mid$(a$,Instr(a$,"\")+1)
		Until Instr(a$,"\")=0
		Return a$
	EndIf
End Function

Function TS_ReadEntity(file)
	;Because truespace uses texture coords by face so the vertex count
	;WILL increase so just store the vertices for laster ref..

	vertcount=TS_Readlong(file) ; vertices Count
	For a=0 To vertcount-1
		If ts_v[a]<>Null Then Delete ts_v[a]
		ts_v[a]=New vertex
		For b=0 To 2
			ts_v[a]\ps[b]=ReadFloat(file)
		Next
	Next
	;texture coords now
	UVcount=TS_Readlong(file)
	For a=0 To UVcount-1
		If uts_v[a]<>Null Then Delete uts_v[a]
		uts_v[a]=New texture
		For b=0 To 1
			poo#=ReadFloat(file)
			uts_v[a]\ps[b]=poo
			If b=1 Then uts_v[a]\ps[b]=-uts_v[a]\ps[b]	;**ADDED
		Next
	Next

	;count the number of faces in this chunk. this will be used
	;at the end of the chunk to check if loaded correctly
	facecount=TS_Readlong(file)
	mesh=CreateMesh()
	For a=0 To facecount-1
		ts_f[a]=TS_ReadFace(file)
		ts_f[a]\TS_INDEX=a				;asc this with truespace face index
		cs=CountSurfaces(mesh)
		If (ts_f[a]\matindex+1)>CountSurfaces(mesh) Then
			For aa=CountSurfaces(mesh) To ts_f[a]\matindex
				CreateSurface mesh
			Next
		End If
	Next
	
	;Now we have the faces,mesh and new surfaces add it all together
	For b=0 To facecount-1
		surf=GetSurface(mesh,ts_f[b]\matindex+1)
		vc=CountVertices(surf)
		For a=0 To 2
			i=ts_f[b]\i[a]
			ti=ts_f[b]\ti[a]
			;-x???
			If CountVertices(surf)>65534 Then RuntimeError "Too many vertices for surface"
			thisV=TS_PosNSet(Surf,ts_v[i]\ps[0],ts_v[i]\ps[1],ts_v[i]\ps[2])
			VertexTexCoords surf,thisV,uts_v[ti]\ps[0],uts_v[ti]\ps[1]
		Next
		AddTriangle surf,vc,vc+2,vc+1 ;1,2
		ts_f[b]\sf_index=CountTriangles(surf)-1	;store the triangles position in this surface
	Next
	Countentity=Countentity+1
	Return mesh
End Function
Function ReadMaterial.brush(file)
	b.brush=New brush	;TODO
	b\id=TS_Readshort(file)
	ReadByte(file)
	ReadByte(file)
	ReadByte(file)
	b\bRed=(255*ReadFloat(file))
	b\bGrn=(255*ReadFloat(file))
	b\bBlu=(255*ReadFloat(file))
	b\bru=CreateBrush(b\bRed,b\bGrn,b\bBlu)
	b\bAlp=ReadFloat(file)
	BrushAlpha b\bru,b\bAlp
	ReadFloat(file);	DebugLog "Ambient Glow="+ReadFloat(file)+" NOT SUPPORTED"
	b\bShn=ReadFloat(file)
	BrushShininess b\bru,b\bShn
	ReadFloat(file);	DebugLog "exp         ="+ReadFloat(file)+" NOT SUPPORTED"	
	ReadFloat(file);	DebugLog "ior         ="+ReadFloat(file)+" NOT SUPPORTED"	
	
	If TS_CHUNKEND>FilePos(file) Then	;if not at chunk end then
		dum$=ReadByte(file)
		dum$=ReadByte(file)
		dum$=ReadByte(file)
	
		length=TS_Readshort(file); get the length of filename
		filename$=TS_Readbytes_L(file,length)
		If filename$<>"" Then
			ff$=GetLocalDirectoryName(filename$)
			b\tFilename$=ff
			test$=WorkDirectory+ff
			b\tex=LoadTexture(WorkDirectory+ff)
			;throw an error if texture not there.
			If b\tex=0 Then RuntimeError "Can't find "+ff
			b\tx#=ReadFloat(file)
			b\ty#=ReadFloat(file)
			PositionTexture b\tex,b\tx,b\ty
			;texture repeats
			ur#=ReadFloat(file)
			vr#=ReadFloat(file)
			b\tu#=Float(1)/ur
			b\tv#=Float(1)/vr
			ScaleTexture b\tex,b\tu,b\tv
			BrushTexture b\bru,b\tex
		End If
	EndIf
	Return b
End Function

Function CreateNormal(mesh)
	For s=1 To CountSurfaces(mesh)
		surf=GetSurface(mesh,s)
		For t=0 To CountTriangles(surf)-1
			i0=TriangleVertex(surf,t,0)
			i1=TriangleVertex(surf,t,1)
			i2=TriangleVertex(surf,t,2)
			x0#=VertexX(surf,i0)
			y0#=VertexY(surf,i0)
			z0#=VertexZ(surf,i0)
			x1#=VertexX(surf,i1)
			y1#=VertexY(surf,i1)
			z1#=VertexZ(surf,i1)
			x2#=VertexX(surf,i2)
			y2#=VertexY(surf,i2)
			z2#=VertexZ(surf,i2)
			dx0#=x1-x0
			dx1#=x2-x0
			dy0#=y1-y0
			dy1#=y2-y0
			dz0#=z1-z0
			dz1#=z2-z0
			nx#=(dy0*dz1)-(dz0*dy1)
			ny#=(dz0*dx1)-(dx0*dz1)
			nz#=(dx0*dy1)-(dy0*dx1)
			ln#=Sqr(nx^2+ny^2+nz^2)
			nx=nx/ln
			ny=ny/ln
			nz=nz/ln
			VertexNormal surf,i0,nx,ny,nz
			VertexNormal surf,i1,nx,ny,nz
			VertexNormal surf,i2,nx,ny,nz
		Next
	Next
End Function
