; ID: 552
; Author: starfox
; Date: 2003-01-17 05:32:48
; Title: CSG Functions
; Description: Use Boolean Union/Subtraction/Intersection

;Csg Engine
;By David Dawkins(Starfox)
;Started 1-10-03
;Finished(without optimizing)1-13-03
;Optimized(1-15-03)
;Licensing: None! Except to give me credit in anything you use it in:)
;Features: Union, Subtraction, Intersect, Fast enough, Removes bad triangles
;Call the csg(mesha,meshb,booltype) to do the actual functions
;Limits: Not to fast with many tris, no uvmapping
;Use PositionEntity, RotateMesh, ScaleMesh to modify pos/orient of mesh
;What csg() returns is the result mesh, not the actual meshes you send to it
;so you might want to delete those.

;Here are the globals
Global csgscale#=1,csgpiv ;The world scale (1- Normal, 2-2x Normal)
Global csgmesh[2] ;Different surface holders for different opereations

Type triangle
Field x#,y#,z#,vz#[2]
Field nx#,ny#,nz#
Field surf,index
Field ver[2],mesh
Field vx#[2],vy#[2]
Field id,tarid,del
End Type

Function FindNorm#(t.triangle,axis)
;Thanks to Simon Harris
x1# = t\vx[0]
y1# = t\vy[0]
z1# = t\vz[0]
x2# = t\vx[1]
y2# = t\vy[1]
z2# = t\vz[1]
x3# = t\vx[2]
y3# = t\vy[2]
z3# = t\vz[2]
ax#=x2-x1
ay#=y2-y1
az#=z2-z1
	
bx#=x3-x2
by#=y3-y2
bz#=z3-z2
	
If axis = 1 Then Return (ay#*bz#)-(az#*by#)
If axis = 2 Then Return (az#*bx#)-(ax#*bz#)
If axis = 3 Then Return (ax#*by#)-(ay#*bx#)
End Function
	
Function Trisintersect(t.triangle,tr.triangle)
Local x#[2],y#[2],z#[2]
surf = GetSurface(csgmesh[0],1)

x[0] = t\vx[0]
y[0] = t\vy[0]
z[0] = t\vz[0]
x[1] = t\vx[1]
y[1] = t\vy[1]
z[1] = t\vz[1]
x[2] = t\vx[2]
y[2] = t\vy[2]
z[2] = t\vz[2]

VertexCoords(surf,0,x[0],y[0],z[0])
VertexCoords(surf,1,x[1],y[1],z[1])
VertexCoords(surf,2,x[2],y[2],z[2])

surf1 = GetSurface(csgmesh[1],1)

x[0] = tr\vx[0]
y[0] = tr\vy[0]
z[0] = tr\vz[0]
x[1] = tr\vx[1]
y[1] = tr\vy[1]
z[1] = tr\vz[1]
x[2] = tr\vx[2]
y[2] = tr\vy[2]
z[2] = tr\vz[2]

VertexCoords(surf1,0,x[0],y[0],z[0])
VertexCoords(surf1,1,x[1],y[1],z[1])
VertexCoords(surf1,2,x[2],y[2],z[2])

inter = MeshesIntersect(csgmesh[0],csgmesh[1])

Return inter
End Function

Function RayIntersect(t.triangle,tr.triangle)
Local x#[2],y#[2],z#[2]
x[0] = t\vx[0]
y[0] = t\vy[0]
z[0] = t\vz[0]
x[1] = t\vx[1]
y[1] = t\vy[1]
z[1] = t\vz[1]
x[2] = t\vx[2]
y[2] = t\vy[2]
z[2] = t\vz[2]
surf = GetSurface(csgmesh[0],1)

VertexCoords(surf,0,x[0],y[0],z[0])
VertexCoords(surf,1,x[1],y[1],z[1])
VertexCoords(surf,2,x[2],y[2],z[2])

piv = csgpiv
PositionEntity(piv,tr\x,tr\y,tr\z)
AlignToVector(piv,tr\nx,tr\ny,tr\nz,3)
MoveEntity piv,0,0,100000*csgscale

EntityPickMode csgmesh[0],2

distx# = EntityX(piv)-tr\x
disty# = EntityY(piv)-tr\y
distz# = EntityZ(piv)-tr\z
picked = LinePick(tr\x,tr\y,tr\z,distx,disty,distz)

If picked > 0
Return 1
Else
Return 0
EndIf
End Function

Function Split(t.triangle,tr.triangle)
If t = Null Then Return
If tr = Null Then Return
mesh = tr\mesh
Local newvx#[2],newvy#[2],newvz#[2]
Local edge1,edge2,edge3,count=0
Local cx#[2],cy#[2],cz#[2]
Local epsilon#=.00001
cube = csgmesh[2]
EntityPickMode cube,2;Position the plane
PositionEntity cube,t\x,t\y,t\z
RotateEntity cube,0,0,0
AlignToVector(cube,t\nx,t\ny,t\nz,2)
;Edge1
x# = tr\vx[0]
y# = tr\vy[0]
z# = tr\vz[0]
x1# = tr\vx[1]
y1# = tr\vy[1]
z1# = tr\vz[1]
distx# = x-x1:disty#=y-y1:distz#=z-z1
picked = LinePick(x1,y1,z1,distx,disty,distz)
If picked = cube
edge1 = True
newvx[count] = PickedX():newvy[count]=PickedY():newvz[count]=PickedZ()

distx# = newvx[count] - x1
disty# = newvy[count] - y1
distz# = newvz[count] - z1
cdist# = Sqr(distx*distx + disty*disty + distz*distz)
If cdist <= epsilon
edge1 = False
Else
count = count + 1
EndIf

EndIf
;Edge2
x# = tr\vx[1]
y# = tr\vy[1]
z# = tr\vz[1]
x1# = tr\vx[2]
y1# = tr\vy[2]
z1# = tr\vz[2]
distx# = x-x1:disty#=y-y1:distz#=z-z1
picked = LinePick(x1,y1,z1,distx,disty,distz)
If picked = cube
edge2 = True
newvx[count] = PickedX():newvy[count]=PickedY():newvz[count]=PickedZ()

distx# = newvx[count] - x1
disty# = newvy[count] - y1
distz# = newvz[count] - z1
cdist# = Sqr(distx*distx + disty*disty + distz*distz)
If cdist <= epsilon
edge2 = False
Else
count = count + 1
EndIf

EndIf
;Edge3
x# = tr\vx[2]
y# = tr\vy[2]
z# = tr\vz[2]
x1# = tr\vx[0]
y1# = tr\vy[0]
z1# = tr\vz[0]
distx# = x-x1:disty#=y-y1:distz#=z-z1
picked = LinePick(x1,y1,z1,distx,disty,distz)
If picked = cube
edge3 = True
newvx[count] = PickedX():newvy[count]=PickedY():newvz[count]=PickedZ()

distx# = newvx[count] - x1
disty# = newvy[count] - y1
distz# = newvz[count] - z1
cdist# = Sqr(distx*distx + disty*disty + distz*distz)
If cdist <= epsilon
edge3 = False
Else
count = count + 1
EndIf

EndIf
If count = 0 Then Return 0
If edge1 And edge2
cx[0] = tr\vx[0]
cy[0] = tr\vy[0]
cz[0] = tr\vz[0]
cx[1] = tr\vx[1]
cy[1] = tr\vy[1]
cz[1] = tr\vz[1]
cx[2] = tr\vx[2]
cy[2] = tr\vy[2]
cz[2] = tr\vz[2]
surf = tr\surf
mesh = tr\mesh
tr\del = 1
;deletetri(tr)
v1 = AddVertex(surf,cx[0],cy[0],cz[0])
v2 = AddVertex(surf,newvx[0],newvy[0],newvz[0])
v3 = AddVertex(surf,cx[1],cy[1],cz[1])
v4 = AddVertex(surf,newvx[1],newvy[1],newvz[1])
v5 = AddVertex(surf,cx[2],cy[2],cz[2])

index = AddTriangle(surf,v1,v2,v5)
tm.triangle = createtri(surf,index,mesh)
tm\tarid = t\id
index = AddTriangle(surf,v2,v4,v5)
tm.triangle = createtri(surf,index,mesh)
tm\tarid = t\id
index = AddTriangle(surf,v2,v3,v4)
tm.triangle = createtri(surf,index,mesh)
tm\tarid = t\id

ElseIf edge1 And edge3
cx[0] = tr\vx[0]
cy[0] = tr\vy[0]
cz[0] = tr\vz[0]
cx[1] = tr\vx[1]
cy[1] = tr\vy[1]
cz[1] = tr\vz[1]
cx[2] = tr\vx[2]
cy[2] = tr\vy[2]
cz[2] = tr\vz[2]
surf = tr\surf
mesh = tr\mesh
tr\del = 1
;deletetri(tr)
v1 = AddVertex(surf,cx[0],cy[0],cz[0])
v2 = AddVertex(surf,newvx[0],newvy[0],newvz[0])
v3 = AddVertex(surf,cx[1],cy[1],cz[1])
v4 = AddVertex(surf,cx[2],cy[2],cz[2])
v5 = AddVertex(surf,newvx[1],newvy[1],newvz[1])

index = AddTriangle(surf,v1,v2,v5)
tm.triangle = createtri(surf,index,mesh)
tm\tarid = t\id
index = AddTriangle(surf,v2,v4,v5)
tm.triangle = createtri(surf,index,mesh)
tm\tarid = t\id
index = AddTriangle(surf,v2,v3,v4)
tm.triangle = createtri(surf,index,mesh)
tm\tarid = t\id
ElseIf edge2 And edge3
cx[0] = tr\vx[0]
cy[0] = tr\vy[0]
cz[0] = tr\vz[0]
cx[1] = tr\vx[1]
cy[1] = tr\vy[1]
cz[1] = tr\vz[1]
cx[2] = tr\vx[2]
cy[2] = tr\vy[2]
cz[2] = tr\vz[2]
surf = tr\surf
mesh = tr\mesh
tr\del = 1
;deletetri(tr)
v1 = AddVertex(surf,cx[0],cy[0],cz[0])
v2 = AddVertex(surf,cx[1],cy[1],cz[1])
v3 = AddVertex(surf,newvx[0],newvy[0],newvz[0])
v4 = AddVertex(surf,cx[2],cy[2],cz[2])
v5 = AddVertex(surf,newvx[1],newvy[1],newvz[1])

index = AddTriangle(surf,v1,v2,v5)
tm.triangle = createtri(surf,index,mesh)
tm\tarid = t\id
index = AddTriangle(surf,v2,v3,v5)
tm.triangle = createtri(surf,index,mesh)
tm\tarid = t\id
index = AddTriangle(surf,v3,v4,v5)
tm.triangle = createtri(surf,index,mesh)
tm\tarid = t\id
ElseIf edge2
cx[0] = tr\vx[0]
cy[0] = tr\vy[0]
cz[0] = tr\vz[0]
cx[1] = tr\vx[1]
cy[1] = tr\vy[1]
cz[1] = tr\vz[1]
cx[2] = tr\vx[2]
cy[2] = tr\vy[2]
cz[2] = tr\vz[2]
surf = tr\surf
mesh = tr\mesh
tr\del = 1
;deletetri(tr)
v1 = AddVertex(surf,cx[0],cy[0],cz[0])
v2 = AddVertex(surf,cx[1],cy[1],cz[1])
v3 = AddVertex(surf,newvx[0],newvy[0],newvz[0])
v4 = AddVertex(surf,cx[2],cy[2],cz[2])
index = AddTriangle(surf,v1,v2,v3)
tm.triangle = createtri(surf,index,mesh)
tm\tarid = t\id
index = AddTriangle(surf,v1,v3,v4)
tm.triangle = createtri(surf,index,mesh)
tm\tarid = t\id
ElseIf edge1
cx[0] = tr\vx[0]
cy[0] = tr\vy[0]
cz[0] = tr\vz[0]
cx[1] = tr\vx[1]
cy[1] = tr\vy[1]
cz[1] = tr\vz[1]
cx[2] = tr\vx[2]
cy[2] = tr\vy[2]
cz[2] = tr\vz[2]
surf = tr\surf
mesh = tr\mesh
tr\del = 1
;deletetri(tr)
v1 = AddVertex(surf,cx[0],cy[0],cz[0])
v2 = AddVertex(surf,newvx[0],newvy[0],newvz[0])
v3 = AddVertex(surf,cx[1],cy[1],cz[1])
v4 = AddVertex(surf,cx[2],cy[2],cz[2])
index = AddTriangle(surf,v1,v2,v4)
tm.triangle = createtri(surf,index,mesh)
tm\tarid = t\id
index = AddTriangle(surf,v2,v3,v4)
tm.triangle = createtri(surf,index,mesh)
tm\tarid = t\id
ElseIf edge3
cx[0] = tr\vx[0]
cy[0] = tr\vy[0]
cz[0] = tr\vz[0]
cx[1] = tr\vx[1]
cy[1] = tr\vy[1]
cz[1] = tr\vz[1]
cx[2] = tr\vx[2]
cy[2] = tr\vy[2]
cz[2] = tr\vz[2]
surf = tr\surf
mesh = tr\mesh
tr\del = 1
;deletetri(tr)

v1 = AddVertex(surf,cx[0],cy[0],cz[0])
v2 = AddVertex(surf,cx[1],cy[1],cz[1])
v3 = AddVertex(surf,cx[2],cy[2],cz[2])
v4 = AddVertex(surf,newvx[0],newvy[0],newvz[0])

index = AddTriangle(surf,v1,v2,v4)
tm.triangle = createtri(surf,index,mesh)
tm\tarid = t\id
index = AddTriangle(surf,v2,v3,v4)
tm.triangle = createtri(surf,index,mesh)
tm\tarid = t\id
EndIf

;UpdateNormals mesh
End Function

Function CreateTri.triangle(surf,index,mesh)
Local x1#,y1#,z1#
Local x2#,y2#,z2#
Local x3#,y3#,z3#
tr.triangle = Last triangle
newid = tr\id + 1
t.triangle = New triangle
t\surf = surf : t\index = index : t\mesh = mesh
t\ver[0] = TriangleVertex(surf,t\index,0)
x1=VertexX(surf,t\ver[0]):y1=VertexY(surf,t\ver[0]):z1=VertexZ(surf,t\ver[0])
TFormPoint(x1,y1,z1,t\mesh,0)
x1 = TFormedX():y1 = TFormedY():z1 = TFormedZ()
t\vx[0] = x1:t\vy[0]=y1:t\vz[0]=z1
t\ver[1] = TriangleVertex(surf,t\index,1)
x2=VertexX(surf,t\ver[1]):y2=VertexY(surf,t\ver[1]):z2=VertexZ(surf,t\ver[1])
TFormPoint(x2,y2,z2,t\mesh,0)
x2 = TFormedX():y2 = TFormedY():z2 = TFormedZ()
t\vx[1] = x2:t\vy[1]=y2:t\vz[1]=z2
t\ver[2] = TriangleVertex(surf,t\index,2)
x3=VertexX(surf,t\ver[2]):y3=VertexY(surf,t\ver[2]):z3=VertexZ(surf,t\ver[2])
TFormPoint(x3,y3,z3,t\mesh,0)
x3 = TFormedX():y3 = TFormedY():z3 = TFormedZ()
t\vx[2] = x3:t\vy[2]=y3:t\vz[2]=z3
t\nx = findnorm(t,1)
t\ny = findnorm(t,2)
t\nz = findnorm(t,3)
normlen# = Sqr#((t\nx*t\nx)+(t\ny*t\ny)+(t\nz*t\nz))
If normlen > 0
t\nx = t\nx/normlen : t\ny = t\ny/normlen : t\nz = t\nz/normlen
Else
t\del = 1
EndIf
t\x = (x1 + x2 + x3)/3.0:t\y = (y1 + y2 + y3)/3.0:t\z = (z1 + z2 + z3)/3.0
t\id = newid
Return t
End Function

Function DeleteTris()
;Deletes all the triangles that should be deleted
Local surface[1000]
Local counter=0
For t.triangle = Each triangle
If t\del = 1

For doit = 1 To counter
If t\surf = surface[doit]
counter = doit:Exit
EndIf
Next

If t\surf <> surface[counter]
counter = counter + 1
surface[counter] = t\surf
EndIf

EndIf
Next

For doit = 1 To counter
surf = surface[doit]

	count = CountTriangles(surf)
	bank = CreateBank((15*count)*4)
	For tricount = 0 To count-1
	off = (tricount*15)*4
	in = TriangleVertex(surf,tricount,0)
	x# = VertexX(surf,in):y#=VertexY(surf,in):z#=VertexZ(surf,in)
	u# = VertexU(surf,in):v#=VertexV(surf,in)
	TFormPoint(x,y,z,mesh,0)
	x = TFormedX():y=TFormedY():z=TFormedZ()
	PokeFloat(bank,off,x)
	PokeFloat(bank,off+4,y)
	PokeFloat(bank,off+8,z)
	PokeFloat(bank,off+12,u)
	PokeFloat(bank,off+16,v)

	in = TriangleVertex(surf,tricount,1)
	x# = VertexX(surf,in):y#=VertexY(surf,in):z#=VertexZ(surf,in)
	u# = VertexU(surf,in):v#=VertexV(surf,in)
	TFormPoint(x,y,z,mesh,0)
	x = TFormedX():y=TFormedY():z=TFormedZ()
	PokeFloat(bank,off+20,x)
	PokeFloat(bank,off+24,y)
	PokeFloat(bank,off+28,z)
	PokeFloat(bank,off+32,u)
	PokeFloat(bank,off+36,v)

	in = TriangleVertex(surf,tricount,2)
	x# = VertexX(surf,in):y#=VertexY(surf,in):z#=VertexZ(surf,in)
	u# = VertexU(surf,in):v#=VertexV(surf,in)
	TFormPoint(x,y,z,mesh,0)
	x = TFormedX():y=TFormedY():z=TFormedZ()
	PokeFloat(bank,off+40,x)
	PokeFloat(bank,off+44,y)
	PokeFloat(bank,off+48,z)
	PokeFloat(bank,off+52,u)
	PokeFloat(bank,off+56,v)

	Next
	
	ClearSurface(surf,True,True)
	
	For tricount = 0 To count-1
	skipthis = 0
	For t.triangle = Each triangle
	If t\del = 1
		If t\index = tricount And t\surf = surf
		skipthis = 1:Exit
		EndIf
	EndIf
	Next
	;If (t\index <> tricount Or t\surf <> surf)
	If skipthis = 0
	off = (tricount*15)*4
	x# = PeekFloat(bank,off)
	y# = PeekFloat(bank,off+4)
	z# = PeekFloat(bank,off+8)
	u# = PeekFloat(bank,off+12)
	v# = PeekFloat(bank,off+16)
	TFormPoint(x,y,z,0,mesh)
	x = TFormedX():y=TFormedY():z=TFormedZ()
	a = AddVertex(surf,x,y,z,u,v)
	x# = PeekFloat(bank,off+20)
	y# = PeekFloat(bank,off+24)
	z# = PeekFloat(bank,off+28)
	u# = PeekFloat(bank,off+32)
	v# = PeekFloat(bank,off+36)
	TFormPoint(x,y,z,0,mesh)
	x = TFormedX():y=TFormedY():z=TFormedZ()
	b = AddVertex(surf,x,y,z,u,v)
	x# = PeekFloat(bank,off+40)
	y# = PeekFloat(bank,off+44)
	z# = PeekFloat(bank,off+48)
	u# = PeekFloat(bank,off+52)
	v# = PeekFloat(bank,off+56)
	TFormPoint(x,y,z,0,mesh)
	x = TFormedX():y=TFormedY():z=TFormedZ()
	c = AddVertex(surf,x,y,z,u,v)
	AddTriangle(surf,a,b,c)
	EndIf
	Next
	FreeBank bank
	
	
Next

For t.triangle = Each triangle
If t\del = 1
	For tom.triangle = Each triangle
	If tom\index > t\index And tom\surf = t\surf
	tom\index = tom\index - 1
	EndIf
	Next
Delete t
EndIf
Next
	
;index = t\index
;surf = t\surf
;id = t\id
;tar = t\tarid
;Delete t
;UpdateNormals mesh
;For tom.triangle = Each triangle
;If tom\index > index And tom\surf = surf
;tom\index = tom\index - 1
;EndIf
;Next
;EndIf
;Next

;Return mesh
End Function

Function CSG(mesha,meshb,mode=2)
;mode 1 - Union, mode 2 - Subtraction, mode 3 - Intersection
Local x1#,y1#,z1#
Local x2#,y2#,z2#
Local x3#,y3#,z3#
Local idstate=0

;Do prestuff
xpos# = EntityX(mesha,1):ypos# = EntityY(mesha,1):zpos#=EntityZ(mesha,1)
xpos1# = EntityX(meshb,1):ypos1# = EntityY(meshb,1):zpos1#=EntityZ(meshb,1)
mesha = CopyMesh(mesha) : meshb = CopyMesh(meshb)
meshc = CopyMesh(mesha) : meshd = CopyMesh(meshb)
PositionMesh mesha,xpos,ypos,zpos
PositionMesh meshb,xpos1,ypos1,zpos1
PositionMesh meshc,xpos,ypos,zpos
PositionMesh meshd,xpos1,ypos1,zpos1

For cat = 1 To 4
If cat = 1 Then curmesh = mesha
If cat = 2 Then curmesh = meshb
If cat = 3 Then curmesh = meshc
If cat = 4 Then curmesh = meshd
For surfcount = 1 To CountSurfaces(curmesh)
	surf = GetSurface(curmesh,surfcount)
	For tricount = 0 To CountTriangles(surf)-1
	t.triangle = New triangle
	t\surf = surf : t\index = tricount : t\mesh = curmesh
	t\ver[0] = TriangleVertex(surf,t\index,0)
	x1=VertexX(surf,t\ver[0]):y1=VertexY(surf,t\ver[0]):z1=VertexZ(surf,t\ver[0])
	TFormPoint(x1,y1,z1,t\mesh,0)
	x1 = TFormedX():y1 = TFormedY():z1 = TFormedZ()
	t\vx[0] = x1:t\vy[0]=y1:t\vz[0]=z1
	t\ver[1] = TriangleVertex(surf,t\index,1)
	x2=VertexX(surf,t\ver[1]):y2=VertexY(surf,t\ver[1]):z2=VertexZ(surf,t\ver[1])
	TFormPoint(x2,y2,z2,t\mesh,0)
	x2 = TFormedX():y2 = TFormedY():z2 = TFormedZ()
	t\vx[1] = x2:t\vy[1]=y2:t\vz[1]=z2
	t\ver[2] = TriangleVertex(surf,t\index,2)
	x3=VertexX(surf,t\ver[2]):y3=VertexY(surf,t\ver[2]):z3=VertexZ(surf,t\ver[2])
	TFormPoint(x3,y3,z3,t\mesh,0)
	x3 = TFormedX():y3 = TFormedY():z3 = TFormedZ()
	t\vx[2] = x3:t\vy[2]=y3:t\vz[2]=z3
	t\nx = findnorm(t,1)
	t\ny = findnorm(t,2)
	t\nz = findnorm(t,3)
	normlen# = Sqr#((t\nx*t\nx)+(t\ny*t\ny)+(t\nz*t\nz))
	If normlen > 0
	t\nx = t\nx/normlen : t\ny = t\ny/normlen : t\nz = t\nz/normlen
	Else
	t\del = 1
	EndIf
	t\x = (x1 + x2 + x3)/3.0:t\y = (y1 + y2 + y3)/3.0:t\z = (z1 + z2 + z3)/3.0
	t\id = idstate:t\tarid = -1
	idstate = idstate + 1
	Next
Next
Next

;Create the reuse objects
csgmesh[0] = CreateMesh()
surf = CreateSurface(csgmesh[0])
v1 = AddVertex(surf,-1,0,1)
v2 = AddVertex(surf,1,0,1)
v3 = AddVertex(surf,1,0,-1)
AddTriangle(surf,v1,v2,v3)

csgmesh[1] = CreateMesh()
surf = CreateSurface(csgmesh[1])
v1 = AddVertex(surf,-1,0,1)
v2 = AddVertex(surf,1,0,1)
v3 = AddVertex(surf,1,0,-1)
AddTriangle(surf,v1,v2,v3)

csgmesh[2] = CreateCube()
ScaleMesh csgmesh[2],10000*csgscale,0,10000*csgscale

;Time to split the a polys
For t.triangle = Each triangle
If t\mesh = meshb
hit = 0
	For tr.triangle = Each triangle
	If tr\mesh = mesha And tr\tarid <> t\id
	If tr\del = 0
	inter = 0
	inter = trisintersect(t,tr)
	If inter = 1
		split(t,tr)
	EndIf
	EndIf
	EndIf
	Next
EndIf
Next

;Now split the b poly's
For t.triangle = Each triangle
If t\mesh = meshc
	For tr.triangle = Each triangle
	If tr\mesh = meshb And tr\tarid <> t\id
	If tr\del = 0
	inter = 0
	inter = trisintersect(t,tr)
	If inter = 1
		split(t,tr)
	EndIf
	EndIf
	EndIf
	Next
EndIf
Next

;Step 2 of reuse
surf = GetSurface(csgmesh[0],1)
AddTriangle(surf,2,1,0)
FreeEntity csgmesh[1]
FreeEntity csgmesh[2]
csgpiv = CreatePivot()

;Delete all the triangles without normals
For t.triangle = Each triangle
If t\nx = 0 And t\ny = 0 And t\nz = 0 And t\del = 0
t\del = 1
EndIf
Next

Local epsilon#=.00001

;MeshA
For t.triangle = Each triangle
If t\mesh = mesha And t\del = 0
cosangle# = -1
inter = 0
intermode = 0;1 - inside, 2 - shared, 3 - Not inside
quickdist# = 100000
	For tr.triangle = Each triangle
	If tr\mesh = meshd
	res = rayintersect(tr,t)
	If res = 1
	intx# = PickedX()
	inty# = PickedY()
	intz# = PickedZ()
	dist# = Sqr((intx-t\x)*(intx-t\x)+(inty-t\y)*(inty-t\y)+(intz-t\z)*(intz-t\z))
	If dist < quickdist
	quickdist = dist
	inter = 1
	cosangle# = (t\nx*tr\nx)+(t\ny*tr\ny)+(t\nz*tr\nz)
	EndIf
	EndIf
	EndIf
	Next
	
	If inter = 1
	If cosangle > 0 Then intermode = 1
	If cosangle < 0 Then intermode = 3
	If quickdist < epsilon ;If the triangle is shared
	intermode = 2
	EndIf
	Else
	intermode = 3
	EndIf
	
If mode = 2
	If intermode = 1 Or intermode = 2
	t\del = 1
	;deletetri(t)
	EndIf
ElseIf mode = 1
	If intermode = 1 Or intermode = 2
	t\del = 1
	;deletetri(t)
	EndIf
ElseIf mode = 3
	If intermode = 3 Or intermode = 2
	t\del = 1
	;deletetri(t)
	EndIf
EndIf
EndIf
Next

;MeshB
For t.triangle = Each triangle
If t\mesh = meshb And t\del = 0
cosangle = -1
inter = 0
intermode = 0;1 - inside, 2 - shared, 3 - not inside
quickdist# = 100000
	For tr.triangle = Each triangle
	If tr\mesh = meshc
	res = rayintersect(tr,t)
	If res = 1
	intx# = PickedX()
	inty# = PickedY()
	intz# = PickedZ()
	dist# = Sqr((intx-t\x)*(intx-t\x)+(inty-t\y)*(inty-t\y)+(intz-t\z)*(intz-t\z))
	If dist < quickdist
	quickdist = dist
	inter = 1
	cosangle# = (t\nx*tr\nx)+(t\ny*tr\ny)+(t\nz*tr\nz)
	EndIf
	EndIf
	EndIf
	Next
	
	If inter = 1
	If cosangle > 0 Then intermode = 1
	If cosangle < 0 Then intermode = 3
	If quickdist < epsilon
	intermode = 2
	EndIf
	Else
	intermode = 3
	EndIf
	
If mode = 2
	If intermode = 3 Or intermode = 2
	t\del = 1
	;deletetri(t)
	EndIf
ElseIf mode = 1
	If intermode = 1
	t\del = 1
	;deletetri(t)
	EndIf
ElseIf mode = 3
	If intermode = 3
	t\del = 1
	;deletetri(t)
	EndIf
EndIf
EndIf
Next

;Now delete the polys that don't belong(loose polys)

clearltris(mesha)
clearltris(meshb)

deletetris()

If mode = 2
FlipMesh(meshb)
EndIf

FreeEntity csgpiv
FreeEntity csgmesh[0]
FreeEntity meshd
FreeEntity meshc
AddMesh(meshb,mesha)
FreeEntity meshb

Delete Each triangle
UpdateNormals mesha

Return mesha
End Function

Function ClearLTris(mesh)
;Clears the triangles that have no partners
Local epsilon#=.00001,cdist#
Local count=0, dist#[2]
For t.triangle = Each triangle
If t\mesh = mesh And t\del = 0
count=0
For full = 0 To 2
	For tom.triangle = Each triangle
	If tom\del = 0 And tom\mesh = mesh
	cdist = 0
		For car = 0 To 2
		dist[0] = tom\vx[car] - t\vx[full]
		dist[1] = tom\vy[car] - t\vy[full]
		dist[2] = tom\vz[car] - t\vz[full] 
		cdist# = Sqr(dist[0]*dist[0] + dist[1]*dist[1] + dist[2]*dist[2])
		If cdist <= epsilon Then Exit
		Next
	If cdist >= 0 Then count = count + 1:Exit
	EndIf
	Next
Next

If count < 3
t\del = 1
EndIf
EndIf
Next
End Function

example()
Function example()
Local wire=0
AppTitle "Starfox's CSG (David Dawkins)"
Graphics3D 640,480
cube = CreateCube()
ScaleMesh cube,10,2,10
cube2 = CreateCube()
ScaleMesh cube2,5,5,5
cam = CreateCamera()
MoveEntity cam,0,10,-20
PointEntity cam,cube2
TurnEntity CreateLight(),45,45,0
UpdateNormals cube:UpdateNormals cube2
EntityAlpha cube2,.5

While Not KeyHit(1)
If KeyDown(205) Then MoveEntity cube2,.1,0,0
If KeyDown(203) Then MoveEntity cube2,-.1,0,0
If KeyDown(30) Then MoveEntity cube2,0,0,.1
If KeyDown(44) Then MoveEntity cube2,0,0,-.1
If KeyDown(200) Then MoveEntity cube2,0,.1,0
If KeyDown(208) Then MoveEntity cube2,0,-.1,0
If KeyHit(17)
wire = 1 - wire
WireFrame wire
EndIf
If KeyHit(57)
e = MilliSecs()
man = csg(cube,cube2)
e = MilliSecs() -e
FreeEntity cube
cube = man
EndIf

UpdateWorld
RenderWorld
Text 0,0,"Time: "+e+" m"
Text 0,15,"Arrows to Move Left/Right/Up/Down"
Text 0,30,"A/Z to move Forward and Back"
Text 0,45,"Tris: "+TrisRendered()/2
Flip
Wend
End
End Function
