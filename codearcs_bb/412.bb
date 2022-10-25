; ID: 412
; Author: sswift
; Date: 2002-08-28 17:41:13
; Title: EntityScaleX(), EntityScaleY(), EntityScaleZ()
; Description: These three functions return the current scale of an entity on each axis.

; -------------------------------------------------------------------------------------------------------------------
; This function returns the X axis scale of an entity, as set by ScaleEntity().
; -------------------------------------------------------------------------------------------------------------------
Function EntityScaleX#(Entity)

        Local Vx#, Vy#, Vz#, Scale#

	Vx# = GetMatElement(Entity, 0, 0)
	Vy# = GetMatElement(Entity, 0, 1)
	Vz# = GetMatElement(Entity, 0, 2)	
	
	Scale# = Sqr(Vx#*Vx# + Vy#*Vy# + Vz#*Vz#)
	
	Return Scale#

End Function


; -------------------------------------------------------------------------------------------------------------------
; This function returns the Y axis scale of an entity, as set by ScaleEntity().
; -------------------------------------------------------------------------------------------------------------------
Function EntityScaleY#(Entity)

        Local Vx#, Vy#, Vz#, Scale#

	Vx# = GetMatElement(Entity, 1, 0)
	Vy# = GetMatElement(Entity, 1, 1)
	Vz# = GetMatElement(Entity, 1, 2)	
	
	Scale# = Sqr(Vx#*Vx# + Vy#*Vy# + Vz#*Vz#)
	
	Return Scale#

End Function


; -------------------------------------------------------------------------------------------------------------------
; This function returns the Z axis scale of an entity, as set by ScaleEntity().
; -------------------------------------------------------------------------------------------------------------------
Function EntityScaleZ#(Entity)

        Local Vx#, Vy#, Vz#, Scale#

	Vx# = GetMatElement(Entity, 2, 0)
	Vy# = GetMatElement(Entity, 2, 1)
	Vz# = GetMatElement(Entity, 2, 2)	
	
	Scale# = Sqr(Vx#*Vx# + Vy#*Vy# + Vz#*Vz#)
	
	Return Scale#

End Function
