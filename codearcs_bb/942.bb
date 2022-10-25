; ID: 942
; Author: elias_t
; Date: 2004-02-23 23:24:49
; Title: ray-&gt;plane ray-&gt;triangle intersection
; Description: The intersection point is calculated

;FIRST FILE.

;-------------------------------------------------------------------------
;A function that finds out if a line segment and a plane intersect
;and calculates the intersection point in the array picked(2)
;where picked(0)=pickedx,..1=y,..2=z
;
;After a tutorial by Paul Bourke.
;
;by elias_t
;
;-------------------------------------------------------------------------

;stores the picked location
Dim picked#(2)



Function ray_plane(p1x#,p1y#,p1z#, p2x#,p2y#,p2z#, pax#,pay#,paz#, pbx#,pby#,pbz#, pcx#,pcy#,pcz#)

Local d#
Local total#,denom#,mu#
Local nx#,ny#,nz#

				
Local dx1#=(pcx - pax)
Local dy1#=(pcy - pay)
Local dz1#=(pcz - paz)

Local dx2#=(pbx - pax)
Local dy2#=(pby - pay)
Local dz2#=(pbz - paz)

nx#=(dy1*dz2)-(dz1*dy2)
ny#=(dz1*dx2)-(dx1*dz2)
nz#=(dx1*dy2)-(dy1*dx2)

;------------------------------------



   d = - nx * pax - ny * pay - nz * paz

   ;Calculate the position on the Line that intersects the plane
   denom = nx * (p2x - p1x) + ny * (p2y - p1y) + nz * (p2z - p1z);

   If (Abs(denom) < 0.0001) Return 0;Line And plane don't intersect
      
   mu = - (d + nx * p1x + ny * p1y + nz * p1z) / denom
   picked(0) = p1x + mu * (p2x - p1x)
   picked(1) = p1y + mu * (p2y - p1y)
   picked(2) = p1z + mu * (p2z - p1z)
	
	;comment this out if you want an infinite ray
   If (mu < 0 Or mu > 1) Return 0;Intersection Not along Line segment
      
   Return 1

End Function





;-------------------------------------------------------------------------
;EXAMPLE
;quick and messy but works ok.

Graphics3D 640,480,32,2

cam=CreateCamera()
CameraRange cam,1,20000
PositionEntity cam,0,5,-10
li=CreateLight()

;start and end point of line segment
p1x#=0:p1y#=10:p1z#=-10
p2x#=-120:p2y#=-500:p2z#=1000



;visualize the triangle that defines a plane
m=CreateMesh()
s=CreateSurface(m)
v0=AddVertex(s,1,0,0)
v1=AddVertex(s,0,0,1)
v2=AddVertex(s,-1,0,0)
v3=AddVertex(s,0,0,-1);extra vert to make a plane
AddTriangle (s,v0,v2,v1)

EntityColor m,0,255,0
UpdateNormals m




RotateMesh m,20,0,30
ScaleMesh m,400,400,400

;3 vertexes of the triangle that define a plane
v0x#=VertexX(s,v0):v0y#=VertexY(s,v0):v0z#=VertexZ(s,v0)
v1x#=VertexX(s,v1):v1y#=VertexY(s,v1):v1z#=VertexZ(s,v1)
v2x#=VertexX(s,v2):v2y#=VertexY(s,v2):v2z#=VertexZ(s,v2)




;create the visual ray
test=CopyMesh(m)
EntityColor test,255,255,0
EntityFX test,16

s1=GetSurface(test,1)

;the +-.2 is for visualisation purposes
VertexCoords(s1,0,p1x+.2,p1y,p1z)
VertexCoords(s1,1,p2x,p2y,p2z)
VertexCoords(s1,2,p1x-.2,p1y,p1z)


PositionEntity cam,0,15,-10
PointEntity cam ,m


;intersction pointer
cc=CreateCube()
EntityColor cc,255,0,0
ScaleEntity cc,.2,.2,.2

;add the extra tri to make a plane
AddTriangle (s,v0,v3,v2)
UpdateNormals m

While Not KeyHit(1)

RenderWorld()

If KeyDown(200) Then VertexCoords(s1,1,VertexX(s1,1),VertexY(s1,1)+1,VertexZ(s1,1))
If KeyDown(208) Then VertexCoords(s1,1,VertexX(s1,1),VertexY(s1,1)-1,VertexZ(s1,1))

If KeyDown(203) Then VertexCoords(s1,1,VertexX(s1,1)-1,VertexY(s1,1),VertexZ(s1,1))
If KeyDown(205) Then VertexCoords(s1,1,VertexX(s1,1)+1,VertexY(s1,1),VertexZ(s1,1))

If KeyDown(201) Then VertexCoords(s1,1,VertexX(s1,1),VertexY(s1,1),VertexZ(s1,1)+1)
If KeyDown(209) Then VertexCoords(s1,1,VertexX(s1,1),VertexY(s1,1),VertexZ(s1,1)-1)


dx#=VertexX(s1,1)
dy#=VertexY(s1,1)
dz#=VertexZ(s1,1)

res=ray_plane(p1x,p1y,p1z, dx,dy,dz  ,v0x,v0y,v0z,v1x,v1y,v1z,v2x,v2y,v2z)
Text 10,10,res


If res
PositionEntity cc,picked(0),picked(1),picked(2),1
Text 10,100,picked(0)
Text 10,120,picked(1)
Text 10,140,picked(2)
EndIf



Flip

Wend


End





;SECOND FILE--------------------------------------------------------


;-------------------------------------------------------------------------
;A function that finds out if a line segment and a triangle intersect
;and calculates the intersection point in the array picked(2)
;where picked(0)=pickedx,..1=y,..2=z
;
;This actually the function posted by sswift and originally made by Tomas Moller.
;Only here we get also the intersection point !
;
;mod by elias_t
;
;-------------------------------------------------------------------------

;stores the picked location
Dim picked#(2)


Function rit(Px#,Py#,Pz#, Dx#,Dy#,Dz#, V0x#,V0y#,V0z#, V1x#,V1y#,V1z#, V2x#,V2y#,V2z#, Extend_To_Infinity=1, Cull_Backfaces=0)
	
	;-
	E1x# = V2x# - V0x#
	E1y# = V2y# - V0y#
	E1z# = V2z# - V0z#

	;-
	E2x# = V1x# - V0x#
	E2y# = V1y# - V0y#
	E2z# = V1z# - V0z#

	; Hxyz = Crossproduct(Dxyz, E2xyz)
	Hx# = (Dy# * E2z#) - (E2y# * Dz#)
	Hy# = (Dz# * E2x#) - (E2z# * Dx#)
	Hz# = (Dx# * E2y#) - (E2x# * Dy#)

	; Calculate the dot product of the above vector and the vector between point 0 and point 2.
	A# = (E1x# * Hx#) + (E1y# * Hy#) + (E1z# * Hz#)

	;cull
	If (Cull_Backfaces = 1) And (A# >= 0) Then Return 0
		
	;parralel
	If (A# > -0.00001) And (A# < 0.00001) Then Return 0
	
	;Inverse Determinant
	F# = 1.0 / A#

	;-
	Sx# = Px# - V0x#
	Sy# = Py# - V0y#
	Sz# = Pz# - V0z#
	
	; U# = F# * (DotProduct(Sxyz, Hxyz))
	U# = F# * ((Sx# * Hx#) + (Sy# * Hy#) + (Sz# * Hz#))
	
	;check u
	If (U# < 0.0) Or (U# > 1.0) Return 0

	; Qxyz = CrossProduct(Sxyz, E1xyz)
	Qx# = (Sy# * E1z#) - (E1y# * Sz#)
	Qy# = (Sz# * E1x#) - (E1z# * Sx#)
	Qz# = (Sx# * E1y#) - (E1x# * Sy#)
	
	; V# = F# * DotProduct(Dxyz, Qxyz)
	V# = F# * ((Dx# * Qx#) + (Dy# * Qy#) + (Dz# * Qz#))

	;check v
	If (V# < 0.0) Or ((U# + V#) > 1.0) Return 0


	T# = F#*((E2x#*Qx#)+(E2y#*Qy#)+(E2z#*Qz#)) 
	
	If T#<0 Return 0
	

	If Extend_To_Infinity=0 And T#>1 Return 0




;-------------------------------------------------
;Calculate intersection point
nx#=(E1y*E2z)-(E1z*E2y)
ny#=(E1z*E2x)-(E1x*E2z)
nz#=(E1x*E2y)-(E1y*E2x)
d# = -  nx*V0x - ny*V0y - nz*V0z 

denom# = nx*Dx + ny*Dy + nz*Dz
mu# = - (d + nx*Px + ny*Py + nz*Pz) / denom

picked(0) = Px + mu * DX
picked(1) = Py + mu * Dy
picked(2) = Pz + mu * Dz
;-------------------------------------------------



	;intersects		
	Return 1

End Function








;Example
Graphics3D 640,480,32,2

cam=CreateCamera()
PositionEntity cam,0,5,-10
li=CreateLight()

;start and end point of line segment
p1x#=0:p1y#=0:p1z#=-10
p2x#=0:p2y#=5:p2z#=10

;3 vertexes of the triangle
v0x#=8:v0y#=0:v0z#=-1
v1x#=0:v1y#=5:v1z#=0
v2x#=-5:v2y#=0:v2z#=1


dx#=p2x-p1x
dy#=p2y-p1y
dz#=p2z-p1z

st=MilliSecs()
For i=1 To 1000000
res=rit(p1x,p1y,p1z,dx,dy,dz,v0x,v0y,v0z,v1x,v1y,v1z,v2x,v2y,v2z)
Next
et=MilliSecs()
dt=(et-st)


m=CreateMesh()
s=CreateSurface(m)
v0=AddVertex(s,v0x,v0y,v0z)
v1=AddVertex(s,v1x,v1y,v1z)
v2=AddVertex(s,v2x,v2y,v2z)
AddTriangle (s,v0,v2,v1)
EntityColor m,0,255,0
UpdateNormals m

test=CopyMesh(m)
EntityColor test,255,255,0
EntityFX test,16

s1=GetSurface(test,1)

;the +-.2 is for visualisation purposes
VertexCoords(s1,0,p1x+.2,p1y,p1z)
VertexCoords(s1,1,p2x,p2y,p2z)
VertexCoords(s1,2,p1x-.2,p1y,p1z)


PositionEntity cam,0,15,-10
PointEntity cam ,m


;intersction pointer
cc=CreateCube()
EntityColor cc,255,0,0
ScaleEntity cc,.2,.2,.2





While Not KeyHit(1)

RenderWorld()

If KeyDown(200) Then VertexCoords(s1,1,VertexX(s1,1),VertexY(s1,1)+.1,VertexZ(s1,1))
If KeyDown(208) Then VertexCoords(s1,1,VertexX(s1,1),VertexY(s1,1)-.1,VertexZ(s1,1))

If KeyDown(203) Then VertexCoords(s1,1,VertexX(s1,1)-.1,VertexY(s1,1),VertexZ(s1,1))
If KeyDown(205) Then VertexCoords(s1,1,VertexX(s1,1)+.1,VertexY(s1,1),VertexZ(s1,1))

If KeyDown(201) Then VertexCoords(s1,1,VertexX(s1,1),VertexY(s1,1),VertexZ(s1,1)+.1)
If KeyDown(209) Then VertexCoords(s1,1,VertexX(s1,1),VertexY(s1,1),VertexZ(s1,1)-.1)


dx#=VertexX(s1,1)-p1x
dy#=VertexY(s1,1)-p1y
dz#=VertexZ(s1,1)-p1z

res=rit(p1x,p1y,p1z,dx,dy,dz,v0x,v0y,v0z,v1x,v1y,v1z,v2x,v2y,v2z,0)

Text 10,10,res

If res=1
PositionEntity cc,picked(0),picked(1),picked(2),1
Text 10,100,picked(0)
Text 10,120,picked(1)
Text 10,140,picked(2)
EndIf

Text 10,40,dt

Flip

Wend


End
