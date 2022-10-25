; ID: 324
; Author: David Bird(Birdie)
; Date: 2002-05-19 18:19:54
; Title: Truespace 2 importer.
; Description: Import truespace 2 cob models into Blitz. (ASCII only)

;
;	Cob File Loader
;	ASCII only
;	(c)2002 David Bird
;	enquire@davebird.fsnet.co.uk
;
;Rules for use.
;truespace2 objects must be rotated to 0,0,0 in the editer before saving
;No children support. but multiple surfaces can be applied
;textures must be in the root dir of exe
;You get better results if you triangulate the mesh in ts
Graphics3D 640,480,0,2

cam=CreateCamera()
CameraRange cam,.1,300
PositionEntity cam,0,0,-8


mesh=LoadCobASCII("test.cob")	;Can be run without triangulation but not very well.

lit=CreateLight()
While Not KeyDown(1)
	If KeyHit(17) Then w=1-w
	If KeyDown(30) Then MoveEntity cam,0,0,.1
	If KeyDown(44) Then MoveEntity cam,0,0,-.1
	If KeyDown(200) Then TurnEntity cam,1,0,0
	If KeyDown(208) Then TurnEntity cam,-1,0,0
	If KeyDown(203) Then TurnEntity cam,0,1,0
	If KeyDown(205) Then TurnEntity cam,0,-1,0
	WireFrame w
	UpdateWorld
	RenderWorld
	Text 0,0,vecx
	Text 0,15,vecy
	Text 0,30,vecz
	Flip
Wend


ClearWorld
EndGraphics
End

;
;	Cob File Loader
;	ASCII only
;	(c)2002 David Bird
;
Type vrt
	Field ind
	Field x#,y#,z#
End Type

Type tex
	Field ind
	Field u#,v#
End Type
	
Type face
	Field count
	Field i[20]
	Field matid
	Field t[20]
End Type

Function GetVert.vrt(index)
	For v.vrt=Each vrt
		If v\ind=index Then Return v
	Next
End Function

Function Gettex.tex(index)
	For t.tex=Each tex
		If t\ind=index Then Return t
	Next
End Function

Function LoadCobASCII(filename$)
	file=ReadFile(filename$)
	If file=0 Then Return 0
	
	mesh=CreateMesh()
	dum$=ReadLine(file)	;strip 2 info lines
	dum=ReadLine(file)
	NameEntity mesh,Trim$(Mid$(ReadLine$(file),5))	;Read in entities name
	dum=ReadLine(file)
	vecx$=Mid$(ReadLine(file),8)
	vecy$=Mid$(ReadLine(file),8)
	vecz$=Mid$(ReadLine(file),8)
	dum=ReadLine(file)
	tmat1$=Mid$(ReadLine(file),1)
	tmat2$=Mid$(ReadLine(file),1)
	tmat3$=Mid$(ReadLine(file),1)
	tmat4$=Mid$(ReadLine(file),1)
	ratfish$=Trim$(Mid$(ReadLine(file),15))
	vertcount=ratfish
	indexc=0
	For a=1 To vertcount
		v.vrt=New vrt
		v\ind=indexc
		indexc=indexc+1
		st$=Trim$(ReadLine$(file))
		v\x=Mid$(st,1,Instr(st," ")-1)
		v\x=v\x
		st=Mid$(st,Instr(st," ")+1)
		v\y=Mid$(st,1,Instr(st," ")-1)
		v\y=v\y
		st=Mid$(st,Instr(st," ")+1)
		v\z=st
	Next
	texcount=Trim$(Mid$(ReadLine(file),17))
	indext=0
	For a=1 To texcount
		t.Tex=New tex
		t\ind=indext
		indext=indext+1
		st=Trim$(ReadLine$(file))
		t\u=Mid$(st,1,Instr(st," "))
		t\v=Mid$(st,Instr(st," "))
	Next
	facecount=Trim$(Mid$(ReadLine(file),6))
	surfcount=0
	For a=1 To facecount
		f.face=New face
		tmp$=Trim$(ReadLine$(file))		;surface id last indent
		f\count=Mid$(tmp,11,2)
		If Lower$(Left$(tmp,4))="face" Then 
			Repeat
				tmp$=Mid$(tmp$,Instr(tmp$," ")+1)
			Until Instr(tmp$," ")=0
			f\matid=tmp
			f\matid=f\matid+1
		Else
			f\matid=1	;;todo Hole face describes how to cut out the next face
		End If
		If f\matid>surfcount Then surfcount=f\matid
		tmp$=Trim$(ReadLine$(file))
		For b=0 To f\count-1
			tp$=Mid$(tmp$,Instr(tmp$,"<")+1,Instr(tmp$,">")-2)	;<vertindex , texture index>
			If Right$(tp$,1)=">" Then tp=Left$(tp,Len(tp)-1)
			tmp$=Mid$(tmp$,Instr(tmp$,">")+1)
			f\i[b]=Left$(tp,Instr(tp,","))
			f\t[b]=Mid$(tp,Instr(tp,",")+1)
		Next
	Next
	dum=ReadLine(file)
	;Once loaded all vert/face and tex coord info setup surfaces
	dum=ReadLine(file)
	For a=1 To surfcount
		Repeat
			t4$=ReadLine$(file)
			If Lower$(Left$(t4,3))="end" Then Goto misstexture
		Until Lower$(Left$(t4,3))="rgb"
		;rgb
		t4=Mid$(t4,4)
		red#=Left$(t4,Instr(t4,",")-1)
		t4=Mid$(t4,Instr(t4,",")+1)
		green#=Left$(t4,Instr(t4,",")-1)
		blue#=Mid$(t4,Instr(t4,",")+1)
		;alpha info
		t5$=ReadLine$(file)
		alpha$=Trim$(Mid$(t5,6))
		alpha=Mid$(alpha,1,Instr(alpha," ")-1)
		;shininess
		t5=Trim$(Mid$(t5,Instr(t5,"ks")-12))
		shiny#=Mid$(t5,1,Instr(t5," ")-1)
		;texture info
		;filename cull must be in main directory sorry
		BFile$=ReadLine$(file)
		Repeat
			bfile=Mid$(bfile,1+Instr(bfile,"\"))
		Until Instr(bfile,"\")=0
		brush=LoadBrush(bfile)
		If brush=0 Then 
			brush=CreateBrush(red*255,green*255,blue*255)
			BrushColor brush,red*255,green*255,blue*255
			texxx=LoadTexture(bfile)
			If texxx<>0 Then
				BrushTexture brush,texxx
			End If
		Else
			BrushColor brush,red*255,green*255,blue*255
		End If
		BrushAlpha brush,alpha
		BrushShininess brush,shiny
		surf=CreateSurface(mesh,brush)
		dum=ReadLine(file)
	Next
	.misstexture
	Local vv.vrt[20]
	Local tt.tex[20]
	
	CloseFile file
	;Now add the mesh information
	For f=Each face
		If f\matid>=0 Then
			surf=GetSurface(mesh,f\matid)
			For b=1 To f\count
				vv[b-1]=getvert(f\i[b-1])
				
				tt[b-1]=gettex(f\t[b-1])
			Next
	
			vrtst=AddVertex(surf,vv[0]\x,vv[0]\y,vv[0]\z,tt[0]\u,tt[0]\v)
			For b=1 To f\count-1
				AddVertex surf,vv[b]\x,vv[b]\y,vv[b]\z,tt[b]\u,tt[b]\v
			Next
			
			AddTriangle surf,vrtst,vrtst+1,vrtst+2
			For b=3 To f\count-1
				AddTriangle surf,vrtst,vrtst+b-1,vrtst+b
			Next
		Else

		End If
	Next
	Delete Each vrt
	Delete Each tex
	Delete Each face
	UpdateNormals mesh
	m11#=Mid$(tmat1,1,Instr(tmat1," ")-1)
	tmat1=Mid$(tmat1,Instr(tmat1," ")+1)
	m12#=Mid$(tmat1,1,Instr(tmat1," ")-1)
	tmat1=Mid$(tmat1,Instr(tmat1," ")+1)
	m13#=Mid$(tmat1,1,Instr(tmat1," ")-1)
	tmat1=Mid$(tmat1,Instr(tmat1," ")+1)
	m14#=tmat1
	m21#=Mid$(tmat2,1,Instr(tmat2," ")-1)
	tmat2=Mid$(tmat2,Instr(tmat2," ")+1)
	m22#=Mid$(tmat2,1,Instr(tmat2," ")-1)
	tmat2=Mid$(tmat2,Instr(tmat2," ")+1)
	m23#=Mid$(tmat2,1,Instr(tmat2," ")-1)
	tmat2=Mid$(tmat2,Instr(tmat2," ")+1)
	m24#=tmat2
	m31#=Mid$(tmat3,1,Instr(tmat3," ")-1)
	tmat3=Mid$(tmat3,Instr(tmat3," ")+1)
	m32#=Mid$(tmat3,1,Instr(tmat3," ")-1)
	tmat3=Mid$(tmat3,Instr(tmat3," ")+1)
	m33#=Mid$(tmat3,1,Instr(tmat3," ")-1)
	tmat3=Mid$(tmat3,Instr(tmat3," ")+1)
	m34#=tmat3

	For ss=1 To CountSurfaces(mesh)
		surf=GetSurface(mesh,ss)
		For vt=0 To CountVertices(surf)-1
			x#=VertexX(surf,vt)
			y#=VertexY(surf,vt)
			z#=VertexZ(surf,vt)
			ny#=(m11*x)+(m12*y)+(m13*z)+m14
			nx#=(m21*x)+(m22*y)+(m23*z)+m24
			nz#=(m31*x)+(m32*y)+(m33*z)+m34
			VertexCoords surf,vt,nx,ny,nz
		Next
	Next
	RotateMesh mesh,-90,0,0

;	vx1#=Mid$(vecx,1,Instr(vecx," "))
;	vecx=Mid$(vecx,Instr(vecx," ")+1)
;	vx2#=Mid$(vecx,1,Instr(vecx," "))
;	vecx=Mid$(vecx,Instr(vecx," ")+1)
;	vx3#=vecx

;	vz1#=Mid$(vecy,1,Instr(vecy," "))
;	vecy=Mid$(vecy,Instr(vecy," ")+1)
;	vz2#=Mid$(vecy,1,Instr(vecy," "))
;	vecy=Mid$(vecy,Instr(vecy," ")+1)
;	vz3#=vecy

;	vy1#=Mid$(vecz,1,Instr(vecz," "))
;	vecz=Mid$(vecz,Instr(vecz," ")+1)
;	vy2#=Mid$(vecz,1,Instr(vecz," "))
;	vecz=Mid$(vecz,Instr(vecz," ")+1)
;	vy3#=vecz
;	AlignToVector mesh,vx1,vx2,vx3,2
;	AlignToVector mesh,vy1,vy2,vy3,1
;	AlignToVector mesh,vz1,vz2,vz3,3
;	Stop
	Delete Each face
	Delete Each vrt
	Delete Each tex
	Return mesh
End Function
