; ID: 491
; Author: Klaas
; Date: 2002-11-17 11:48:06
; Title: xForm
; Description: Resets all scale transformations

Function xForm(entity,sxp#=1,syp#=1,szp#=1)
	Vx# = GetMatElement(Entity, 0, 0)
	Vy# = GetMatElement(Entity, 0, 1)
	Vz# = GetMatElement(Entity, 0, 2)	

	sx#=Sqr(Vx#*Vx# + Vy#*Vy# + Vz#*Vz#)
	sy#=Sqr(Vx#*Vx# + Vy#*Vy# + Vz#*Vz#)
	sz#=Sqr(Vx#*Vx# + Vy#*Vy# + Vz#*Vz#)
	
	px#=EntityX(entity) * sxp
	py#=EntityY(entity) * syp
	pz#=EntityZ(entity) * szp

	For i=1 To CountChildren(entity)
		child=GetChild(entity,i)
		xForm(child,sx,sy,sz)
	Next

	ScaleEntity entity,1,1,1
	ScaleMesh entity,sx,sy,sz
	PositionEntity entity,px,py,pz
End Function
