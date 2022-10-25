; ID: 533
; Author: GrahamK
; Date: 2002-12-21 06:25:26
; Title: Ray intersect
; Description: Calculates the intersection between a triangle and a ray(vector) in 3d space

; a vector type
Type vector3D
	Field dx#,dy#,dz#
End Type

; create a new vector object
Function vector3D_Create.vector3D(x#,y#,z#)
	v.vector3d = New vector3d
	vector3d_set(v,x,y,z)
	Return v
End Function

; set a vector object to a set of values
Function vector3D_Set(v1.vector3d,x#,y#,z#)
	v1\dx# = x#
	v1\dy# = y#
	v1\dz# = z#
End Function


Const SAME_CLOCKNESS = 1
Const DIFF_CLOCKNESS = 0 
Function vector3D_CheckSameClockDir(pt1.vector3d, pt2.vector3d,pt3.vector3d,norm.vector3d)
   ; calculate normal
   test.vector3d = vector3d_trinormal(pt1,pt2,pt3)
   dotprod# = test\dx*norm\dx + test\dy*norm\dy + test\dz*norm\dz
   Delete test  
   
   If(dotprod < 0) Then 
	Return DIFF_CLOCKNESS;
   Else 
	Return SAME_CLOCKNESS;
  End If
End Function


; calculate the normal of a triangle
Function Vector3D_TriNormal.Vector3d(v1.Vector3d,v2.Vector3d,v3.Vector3d)
	; calculate differences
  	ux#= v3\dx#- v2\dx#
   	uy#= v3\dy#- v2\dy#
  	uz#= v3\dz#- v2\dz#
    vx#= v1\dx#- v2\dx#
    vy#= v1\dy#- v2\dy#
   	vz#= v1\dz#- v2\dz#

	; calculate vector
	n.Vector3D = vector3d_create((uy#*vz#)-(vy#*uz#),(uz#*vx#)-(vz#*ux#),(ux#*vy#)-(vx#*uy#)); New Vector3D
	
    mag# = Sqr#(n\dx#^2+n\dy#^2+n\dz#^2)

	; create normal by scaling by 1/magnitude
	If mag#<>0 Then 
		vector3d_set(n,n\dx#/mag#,n\dy#/mag#,n\dz#/mag#) 
	Else 
		vector3d_set(n,0,0,1) ; to remove and div 0 errors
	End If
	
	Return n
End Function


; check if a ray from a given point intersects a triangle.
; Only considers an intersect if the triangle is 'facing' the point, ie. only intersects with the front of the triangle
Function vector3d_LineTriIntersect(pt1.vector3d, pt2.vector3d, pt3.vector3d, linept.vector3d, vect.vector3d, pt_int.vector3d)

   ; vector form triangle pt1 To pt2
   V1x# = pt2\dx - pt1\dx
   V1y# = pt2\dy - pt1\dy
   V1z# = pt2\dz - pt1\dz

   ; vector form triangle pt2 To pt3
   V2x# = pt3\dx - pt2\dx
   V2y# = pt3\dy - pt2\dy
   V2z# = pt3\dz - pt2\dz

   ; vector normal of triangle
   norm.vector3d = vector3d_trinormal(pt1,pt2,pt3) ;vector3d_create(V1y*V2z-V1z*V2y,V1z*V2x-V1x*V2z,V1x*V2y-V1y*V2x)

   ; dot product of normal And Line's vector If zero Line is parallel To triangle
   dotprod# = norm\dx*vect\dx + norm\dy*vect\dy + norm\dz*vect\dz;

   If(dotprod < 0)
      ;Find point of intersect To triangle plane.
      ;find t To intersect point
      t1# = -(norm\dx*(linept\dx-pt1\dx)+norm\dy*(linept\dy-pt1\dy)+norm\dz*(linept\dz-pt1\dz))
	  t2# =  (norm\dx*vect\dx+norm\dy*vect\dy+norm\dz*vect\dz)

	  t#=t1/t2
      ; If ds is neg Line started past triangle so can't hit triangle.
      If(t < 0) Then Delete norm:Return 0

	  vector3d_set(pt_int,linept\dx + vect\dx*t,linept\dy + vect\dy*t,linept\dz + vect\dz*t)
	
      If(vector3D_CheckSameClockDir(pt1, pt2, pt_int, norm) = SAME_CLOCKNESS) Then
         If(vector3D_CheckSameClockDir(pt2, pt3, pt_int, norm) = SAME_CLOCKNESS) Then
            If(vector3D_CheckSameClockDir(pt3, pt1, pt_int, norm) = SAME_CLOCKNESS) Then
			   Delete norm
               Return 1;
            End If
         End If
      End If
   End If
   Delete norm
   Return 0
End Function



; example code below... function has not been heavily tested as yet

p1.vector3d = vector3d_create(-1,1,-1)
p2.vector3d = vector3d_create( 1,1,-1)
p3.vector3d = vector3d_create( 0,1, 1)

linept.vector3d = vector3d_create(0,0,0)
vect.vector3d = vector3d_create(0,10,1)

pt_int.vector3d = vector3d_create(0,0,0)


ci = vector3d_LineTriIntersect(p1,p2,p3,linept,vect,pt_int)
If ci Then 
	DebugLog "Intersected at x:"+pt_int\dx+" y:"+pt_int\dy+" z:"+pt_int\dz
Else
	DebugLog "Does not intersect"
End If

DebugLog "---------------------------------------------"


vector3d_set(vect,0,10,20)
ci = vector3d_LineTriIntersect(p1,p2,p3,linept,vect,pt_int)
If ci Then 
	DebugLog "Intersected at x:"+pt_int\dx+" y:"+pt_int\dy+" z:"+pt_int\dz
Else
	DebugLog "Does not intersect"
End If



While Not KeyDown(1)
Wend

Delete Each vector3d

End
