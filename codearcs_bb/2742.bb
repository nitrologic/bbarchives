; ID: 2742
; Author: MusicianKool
; Date: 2010-07-18 21:37:13
; Title: 3D coordinate systems
; Description: Cartesian, Spherical, and 3D to 2D projection

; Here is the Source code in Blitz3D.
; It's slower but does the same thing as the lib.

;#Region    ;Spherical Vector

Global SphericalBank = CreateBank(4*5)

Function Spherical(x#,y#,z#,Vector#)
	RD# = Vector#
	zen# = ATan2(Sqr(x#*x#+y#*y#),z#)
	azi# = ATan2(y#,x#)
	PokeFloat (SphericalBank,0,Sin(Zen#))
	PokeFloat (SphericalBank,4,Cos(Zen#))
	PokeFloat (SphericalBank,8,Sin(Azi#))
	PokeFloat (SphericalBank,12,Cos(Azi#))
	PokeFloat (SphericalBank,16,RD#)
End Function

Function Spherical_X#()
	Return (PeekFloat(SphericalBank,16)*PeekFloat(SphericalBank,0)*PeekFloat(SphericalBank,12))
End Function

Function Spherical_Y#()
	Return (PeekFloat(SphericalBank,16)*PeekFloat(SphericalBank,0)*PeekFloat(SphericalBank,8))
End Function

Function Spherical_Z#()
	Return (PeekFloat(SphericalBank,16)*PeekFloat(SphericalBank,4))
End Function

;#End Region

;#Region	;Cartesian Vector
Global CartesianBank = CreateBank(3*4)
Function Cartesian#(Pitch#,Yaw#,Roll#,X_vector#,Y_vector#,Z_vector#)
	sx# = Sin(Pitch#)
	cx# = Cos(Pitch#)
	sy# = Sin(Yaw#)
	cy# = Cos(Yaw#)
	sz# = Sin(Roll#)
	cz# = Cos(Roll#)

	;// rotation around x
	xy# = cx#*Y_vector# - sx#*Z_vector#
	xz# = sx#*Y_vector# + cx#*Z_vector#
	;// rotation around y
	yz# = cy#*xz# - sy#*X_vector#
	yx# = sy#*xz# + cy#*X_vector#
	;// rotation around z
	zx# = cz#*yx# - sz#*xy#
	zy# = sz#*yx# + cz#*xy#

	PokeFloat (CartesianBank,0,zx#)
	PokeFloat (CartesianBank,4,zy#)
	PokeFloat (CartesianBank,8,yz#)
End Function
Function Cartesian_X#():		Return PeekFloat(CartesianBank,0):	End Function
Function Cartesian_Y#():		Return PeekFloat(CartesianBank,4):	End Function
Function Cartesian_Z#():		Return PeekFloat(CartesianBank,8):	End Function
;#End Region

Global ProjectionBank = CreateBank(3*4)
Function Project_3dto2d(gx,gy,gz,x,y,z,dst = 1000,dst2 = 100)
	PokeFloat(ProjectionBank, gx + dst * x / (z + dst2) , 0)
	PokeFloat(ProjectionBank, gy + dst * y / (z + dst2) , 4)
	PokeFloat(ProjectionBank, gz + dst * z / (x + dst2) , 8)
End Function
Function Projected_X#()
	Return PeekFloat(ProjectionBank,0)
End Function
Function Projected_Y#()
	Return PeekFloat(ProjectionBank,4)
End Function
Function Projected_Z#()
	Return PeekFloat(ProjectionBank,8)
End Function
