; ID: 316
; Author: David Bird(Birdie)
; Date: 2002-05-07 18:36:24
; Title: Area Collision Lib
; Description: As the title says..

;
;	Area Collision Library
;	David Bird
;	enquire@davebird.fsnet.co.uk
;
;syntax

;SetSphereRegion(posx,posy,posz,radius)
;SetCubeRegion(posx,posy,posz,size)
;SetRectRegion(posx,posy,posz,width,height,depth)
;PointInRegion(regType,checkx,checky,checkz)
;SphereInRegion(regType,checkx,checky,checkz,radius to check)
;EntityInRegion(regType,Entity,Radius) Problem No way of obtaining entity radius
;FreeAllRegions()
;FreeRegion(r.reg)
;GetEntityRegion.reg(ent,x#,y#,z#,rd#=1)
;GetSphereRegion.reg(x#,y#,z#,rad#)
;GetPointRegion.reg(x#,y#,z#)

Global AC_DEBUG=False ;set this this to true to show regions
Type reg
	Field typ	;0-sphere 1-cube 2-rect
	Field piv
	Field rad#
	Field sx#
	Field sy#
	Field sz#
	Field lpiv
	Field tx#
	Field ty#
	Field tz#
	Field debugent
End Type
Function SetAC_DEBUG(d=False)
	AC_DEBUG=d
End Function

Function MoveRegion(r.reg,x#,y#,z#)
	MoveEntity r\piv,x,y,z
End Function

Function TurnRegion(r.reg,x#,y#,z#,Glob=0)
	TurnEntity r\piv,x,y,z,glob
End Function

Function PositionRegion(r.reg,x#,y#,z#,Glob=0)
	PositionEntity r\piv,x,y,z,Glob
End Function

Function TranslateRegion(r.reg,x#,y#,z#,Glob=0)
	TranslateEntity r\piv,x,y,z,Glob
End Function

Function SetSphereRegion.reg(x#,y#,z#,rad#,parent=0)
	r.reg=New reg
	r\piv=CreatePivot(parent)
	r\lpiv=CreatePivot()
	PositionEntity r\piv,x,y,z
	r\rad=rad
	r\typ=0			;only spheres unto now
	If AC_DEBUG=True Then
		t=CreateSphere(10,r\piv)
		EntityAlpha t,.25
		EntityFX t,16
		ScaleEntity t,rad,rad,rad
		r\debugent=t
	End If
	Return r
End Function

Function SetCubeRegion.reg(x#,y#,z#,size#,parent=0)
	r.reg=New reg
	r\piv=CreatePivot(parent)
	r\lpiv=CreatePivot()
	PositionEntity r\piv,x,y,z
	r\sx=size
	r\sy=size
	r\sz=size
	r\typ=1		;only spheres unto now
	If AC_DEBUG=True Then
		t=CreateCube(r\piv)
		EntityAlpha t,.25
		EntityFX t,16
		ScaleEntity t,size,size,size
		r\debugent=t
	End If

	Return r
End Function

Function SetRectRegion.reg(x#,y#,z#,w#,h#,d#,parent=0)
	r.reg=New reg
	r\piv=CreatePivot(parent)
	r\lpiv=CreatePivot()
	PositionEntity r\piv,x,y,z
	r\sx=w
	r\sy=h
	r\sz=d
	r\typ=1
	If AC_DEBUG=True Then
		t=CreateCube(r\piv)
		EntityAlpha t,.25
		EntityFX t,16
		ScaleEntity t,w,h,d
		r\debugent=t
	End If
	Return r
End Function

Function PointInRegion(r.reg,x#,y#,z#)
	typ=r\typ
	If typ=2 Then t=1
	Select typ
		Case 0								;-point in sphere
			dx#=EntityX(r\piv,True)-x
			dy#=EntityY(r\piv,True)-y
			dz#=EntityZ(r\piv,True)-z
			rad#=Sqr(dx^2+dy^2+dz^2)
			If rad<r\rad Then Return True
		Case 1								;- point in cube or rectangle
			If x<EntityX(r\piv,True)+r\sx And x>EntityX(r\piv,True)-r\sx
				If y<EntityY(r\piv,True)+r\sy And y>EntityY(r\piv,True)-r\sy
					If z<EntityZ(r\piv,True)+r\sz And z>EntityZ(r\piv,True)-r\sz
						Return True
					End If
				End If
			End If			
	End Select
	Return False
End Function

Function EntityInRegion(ent,r.reg,rd#=1)
	Return SphereInRegion(r,EntityX(ent,True),EntityY(ent,True),EntityZ(ent,True),rd);??? radius
End Function

Function SphereInRegion(r.reg,x#,y#,z#,rd#)
	typ=r\typ
	If typ=2 Then t=1
	Select typ
		Case 0								;-Sphere in sphere
			dx#=EntityX(r\piv,True)-x
			dy#=EntityY(r\piv,True)-y
			dz#=EntityZ(r\piv,True)-z
			rad#=Sqr(dx^2+dy^2+dz^2)-rd
			If rad<r\rad Then Return True
		Case 1								;-Sphere in cube
			dx#=EntityX(r\piv,True)-x
			dy#=EntityY(r\piv,True)-y
			dz#=EntityZ(r\piv,True)-z
			rad#=Sqr(dx^2+dy^2+dz^2)
			dx=dx/rad
			dy=dy/rad
			dz=dz/rad
			dx=EntityX(r\piv,True)-(dx*(rad-rd))
			dy=EntityY(r\piv,True)-(dy*(rad-rd))
			dz=EntityZ(r\piv,True)-(dz*(rad-rd))
			If dx<EntityX(r\piv,True)+r\sx And dx>EntityX(r\piv,True)-r\sx
				If dy<EntityY(r\piv,True)+r\sy And dy>EntityY(r\piv,True)-r\sy
					If dz<EntityZ(r\piv,True)+r\sz And dz>EntityZ(r\piv,True)-r\sz
						Return True
					End If
				End If
			End If			
	End Select
	Return False
End Function

Function GetPointRegion.reg(x#,y#,z#)
	;returns the first region that a point is within
	For r.reg=Each reg
		If PointInRegion(r,x,y,z)=True Then Return r
	Next
	Return Null
End Function

Function GetSphereRegion.reg(x#,y#,z#,rad#)
	;returns the first region that a point is within
	For r.reg=Each reg
		If SphereInRegion(r,x,y,z,rad)=True Then Return r
	Next
	Return Null
End Function

Function GetEntityRegion.reg(ent,rd#=1)
	;returns the first region that a point is within
	For r.reg=Each reg
		If EntityInRegion(ent,r,rd)=True Then Return r
	Next
	Return Null
End Function

Function FreeAllRegions()
	For r.reg=Each reg
		If r\piv<>0 Then FreeEntity r\piv
		If r\lpiv<>0 Then FreeEntity r\lpiv
		Delete r
	Next
End Function

Function FreeRegion(r.reg)
		If r\piv<>0 Then FreeEntity r\piv
		If r\lpiv<>0 Then FreeEntity r\lpiv
		Delete r
End Function

Function UpdateRegions()
	For r.reg=Each reg
		r\tx=EntityX(r\piv,True)-EntityX(r\lpiv,True)
		r\ty=EntityY(r\piv,True)-EntityY(r\lpiv,True)
		r\tz=EntityZ(r\piv,True)-EntityZ(r\lpiv,True)
		PositionEntity r\lpiv,EntityX(r\piv,True),EntityY(r\piv,True),EntityZ(r\piv,True)
	Next
End Function

;only used in debug
Function HideRegions()
	For r.reg=Each reg
		HideEntity r\debugent
	Next
End Function

Function ShowRegions()
	For r.reg=Each reg
		ShowEntity r\debugent
	Next
End Function
