; ID: 947
; Author: elias_t
; Date: 2004-02-27 11:18:29
; Title: Ray-&gt;Sphere Intersection
; Description: Returns intersection points

;infinite ray to sphere intersection example.
;The intersection points are returned.

Dim picked1#(2);1st intersection point
Dim picked2#(2);2nd intersection point [optional]


; x1,y1,z1    : 1st point of segment
; x2,y2,z2    : 2nd point of segment
; x3,y3,z3, r : coordinates And radius of sphere)

Function sphere_line_intersection (x1#,y1#,z1#, x2#,y2#,z2#, x3#,y3#,z3#,r#)

Local mu#

Local dx1#=(x2-x1)
Local dy1#=(y2-y1)
Local dz1#=(z2-z1)

Local dx2#=(x1-x3)
Local dy2#=(y1-y3)
Local dz2#=(z1-z3)


Local a# = dx1*dx1 + dy1*dy1 + dz1*dz1
Local b# = 2*( dx1*dx2 + dy1*dy2 + dz1*dz2 )
Local c# = (x3*x3)+(y3*y3)+(z3*z3)+(x1*x1)+(y1*y1)+(z1*z1)-2*(x3*x1+y3*y1+z3*z1)-(r*r)


Local bm#=(b * b)
Local da#=2*a
Local fac#=(4 * a * c)

Local i#=bm - fac

Local sqi#=Sqr(i)



;if segment crosses the sphere
If i>=0

  ;incoming intersection
  mu = (-b - sqi) / da
  picked1(0) = x1 + mu*dx1
  picked1(1) = y1 + mu*dy1
  picked1(2) = z1 + mu*dz1

  ;
  ;outgoing intersection
  ;remove this if you only need the first intersection point
  mu = (-b + sqi) / da
  picked2(0) = x1 + mu*dx1
  picked2(1) = y1 + mu*dy1
  picked2(2) = z1 + mu*dz1
  ;

Return 1
EndIf



End Function







;************************************************************************
;EXAMPLE




Graphics3D 640,480,0,2

cam=CreateCamera()
MoveEntity cam,0,3,-5
light=CreateLight()


;------------------------------
;create a "ray like triangle" near the camera's position 0,0,-5
ray=CreateMesh()
rs=CreateSurface(ray)
v0=AddVertex(rs,-.03,0,-5)
v1=AddVertex(rs,0,0,+10)
v2=AddVertex(rs,.03,0,-5)
AddTriangle(rs,v0,v1,v2)
EntityColor ray,255,255,0
UpdateNormals ray
EntityFX ray,16
;------------------------------


;------------------------------
;Create a sphere [radius=1]
ob1=CreateSphere()
EntityColor ob1,255,0,0
EntityAlpha ob1,.75
EntityFX ob1,16

;create dummy objects
d1=CreateCube()
ScaleEntity d1,.05,.05,.05
EntityColor d1,0,0,255
d2=CreateCube()
ScaleEntity d2,.05,.05,.05
EntityColor d2,0,255,0
;------------------------------


;variables to control the ray
rx#=0
ry#=0
rz#=10


PointEntity cam,ob1

	
While Not KeyHit(1)
RenderWorld()

;move the ray
If KeyDown(200) ry=ry+.1 VertexCoords rs,v1,rx,ry,rz
If KeyDown(208) ry=ry-.1 VertexCoords rs,v1,rx,ry,rz
If KeyDown(203) rx=rx-.1 VertexCoords rs,v1,rx,ry,rz
If KeyDown(205) rx=rx+.1 VertexCoords rs,v1,rx,ry,rz

;x,y,z=1st point ,rx,ry,rz=2nd point ,sx,sy,sz,r=sphere pos and radius r
If sphere_line_intersection (0,0,-5, rx,ry,rz, 0,0,0,1)
PositionEntity d1,picked1(0),picked1(1),picked1(2)
PositionEntity d2,picked2(0),picked2(1),picked2(2)
EndIf

Text 200,0,"[ Cursor keys change ray direction]"
Flip
Wend
ClearWorld()
End
