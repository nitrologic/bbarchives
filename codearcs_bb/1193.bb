; ID: 1193
; Author: Vertex
; Date: 2004-11-13 10:15:37
; Title: 3D Lib
; Description: 4x4 matrix + 3 dim. vector

; ----------------------------------------------------------------------- 
Type matrix_t 
	Field aa#, ab#, ac#, ad# 
	Field ba#, bb#, bc#, bd# 
	Field ca#, cb#, cc#, cd# 
	Field da#, db#, dc#, dd# 
End Type 
	
Type vector_t 
	Field x#, y#, z#
End Type 
; ----------------------------------------------------------------------- 

; ----------------------------------------------------------------------- 
Function CreateMatrix.matrix_t() 
	Local m.matrix_t = New matrix_t 
	 
	MatrixIdentity(m) 
	 
	Return m 
End Function 

Function FreeMatrix(m.matrix_t) 
	Delete m 
	m = Null 
End Function 

Function MatrixIdentity(m.matrix_t) 

	; | 1 0 0 0 | 
	; | 0 1 0 0 | 
	; | 0 0 1 0 | 
	; | 0 0 0 1 | 
	 
	m\aa# = 1.0 : m\ab# = 0.0 : m\ac# = 0.0 : m\ad# = 0.0 
	m\ba# = 0.0 : m\bb# = 1.0 : m\bc# = 0.0 : m\bd# = 0.0 
	m\ca# = 0.0 : m\cb# = 0.0 : m\cc# = 1.0 : m\cd# = 0.0 
	m\da# = 0.0 : m\db# = 0.0 : m\dc# = 0.0 : m\dd# = 1.0 
End Function 

Function MatrixRotateX(m.matrix_t, a#) 

	; | 1	  0      0     0 | 
	; | 0  cos(a) sin(a) 0 | 
	; | 0 -sin(a) cos(a) 0 | 
	; | 0   0      0     1 | 
	 
	m\aa# = 1.0 : m\ab# =  0.0      : m\ac# = 0.0      : m\ad# = 0.0 
	m\ba# = 0.0 : m\bb# =  Cos#(a#) : m\bc# = Sin#(a#) : m\bd# = 0.0 
	m\ca# = 0.0 : m\cb# = -Sin#(a#) : m\cc# = Cos#(a#) : m\cd# = 0.0 
	m\da# = 0.0 : m\db# =  0.0      : m\dc# = 0.0      : m\dd# = 1.0 
End Function 

Function MatrixRotateY(m.matrix_t, a#) 

	; | cos(a)  0     -sin(a) 0     | 
	; |  0      1       0     0     | 
	; |  0     sin(a)   0    cos(a) | 
	; |  0      0       0     1     | 
	 
	m\aa# = Cos#(a#) : m\ab# = 0.0 : m\ac# = -Sin#(a#) : m\ad# = 0.0 
	m\ba# = 0.0      : m\bb# = 1.0 : m\bc# =  0.0      : m\bd# = 0.0 
	m\ca# = Sin#(a#) : m\cb# = 0.0 : m\cc# =  Cos#(a#) : m\cd# = 0.0 
	m\da# = 0.0      : m\db# = 0.0 : m\dc# =  0.0      : m\dd# = 1.0 
End Function 

Function MatrixRotateZ(m.matrix_t, a#) 

	; |  cos(a) sin(a) 0 0 | 
	; | -sin(a) cos(a) 0 0 | 
	; |   0      0     1 0 | 
	; |   0      0     0 1 | 
	 
	m\aa# =  Cos#(a#) : m\ab# = Sin#(a#) : m\ac# = 0.0 : m\ad# = 0.0 
	m\ba# = -Sin#(a#) : m\bb# = Cos#(a#) : m\bc# = 0.0 : m\bd# = 0.0 
	m\ca# =  0.0      : m\cb# = 0.0      : m\cc# = 1.0 : m\cd# = 0.0 
	m\da# =  0.0      : m\db# = 0.0      : m\dc# = 0.0 : m\dd# = 1.0 
End Function 

Function MatrixScale(m.matrix_t, sx#, sy#, sz#) 

	; | sx 0  0  0 | 
	; | 0  sy 0  0 | 
	; | 0  0  sz 0 | 
	; | 0  0  0  1 | 
	 
	m\aa# = sx# : m\ab# = 0.0 : m\ac# = 0.0 : m\ad# = 0.0 
	m\ba# = 0.0 : m\bb# = sy# : m\bc# = 0.0 : m\bd# = 0.0 
	m\ca# = 0.0 : m\cb# = 0.0 : m\cc# = sz# : m\cd# = 0.0 
	m\da# = 0.0 : m\db# = 0.0 : m\dc# = 0.0 : m\dd# = 1.0 
End Function 

Function MatrixTranslate(m.matrix_t, tx#, ty#, tz#) 
	 
	; | 1  0  0  0 | 
	; | 0  1  0  0 | 
	; | 0  0  1  0 | 
	; | tx ty tz 1 | 
	 
	m\aa# = 1.0 : m\ab# = 0.0 : m\ac# = 0.0 : m\ad# = 0.0 
	m\ba# = 0.0 : m\bb# = 1.0 : m\bc# = 0.0 : m\bd# = 0.0 
	m\ca# = 0.0 : m\cb# = 0.0 : m\cc# = 1.0 : m\cd# = 0.0 
	m\da# = tx# : m\db# = ty# : m\dc# = tz# : m\dd# = 1.0 
End Function 

Function MatrixTranspose(m.matrix_t) 
	Local t.matrix_t = New matrix_t 
	 
	; | aa ab ac ad | -> | aa ba ca da 
	; | ba bb bc bd |    | ab bb cb db 
	; | ca cb cc cd |    | ac bc cc dc 
	; | da db dc dd |    | ad bd cd dd 
	 
	t\aa# = m\aa# : t\ab# = m\ab# : t\ac# = m\ac# : t\ad# = m\ad# 
	t\ba# = m\ba# : t\bb# = m\bb# : t\bc# = m\bc# : t\bd# = m\bd# 
	t\ca# = m\ca# : t\cb# = m\cb# : t\cc# = m\cc# : t\cd# = m\cd# 
	t\da# = m\da# : t\db# = m\db# : t\dc# = m\dc# : t\dd# = m\dd# 
	 
	m\aa# = t\aa# : m\ab# = t\ba# : m\ac# = t\ca# : m\ad# = t\da# 
	m\ba# = t\ab# : m\bb# = t\bb# : m\bc# = t\cb# : m\bd# = t\db# 
	m\ca# = t\ac# : m\cb# = t\bc# : m\cc# = t\cc# : m\cd# = t\dc# 
	m\da# = t\ad# : m\db# = t\bd# : m\dc# = t\cd# : m\dd# = t\dd# 
	 
	Delete t 
End Function 

Function MatrixMultiplyMatrix(a.matrix_t, b.matrix_t, c.matrix_t) 

	; c = a*b 
	 
	; aa ab ac ad 
	c\aa# = a\aa#*b\aa# + a\ab#*b\ba# + a\ac#*b\ca# + a\ad#*b\da#
	c\ab# = a\aa#*b\ab# + a\ab#*b\bb# + a\ac#*b\cb# + a\ad#*b\db#
	c\ac# = a\aa#*b\ac# + a\ab#*b\bc# + a\ac#*b\cc# + a\ad#*b\dc#
	c\ad# = a\aa#*b\ad# + a\ab#*b\bd# + a\ac#*b\cd# + a\ad#*b\dd#
	
	; ba bb bc bd
	c\ba# = a\ba#*b\aa# + a\bb#*b\ba# + a\bc#*b\ca# + a\bd#*b\da#
	c\bb# = a\ba#*b\ab# + a\bb#*b\bb# + a\bc#*b\cb# + a\bd#*b\db#
	c\bc# = a\ba#*b\ac# + a\bb#*b\bc# + a\bc#*b\cc# + a\bd#*b\dc#
	c\bd# = a\ba#*b\ad# + a\bb#*b\bd# + a\bc#*b\cd# + a\bd#*b\dd#
	
	; ca cb cc cd
	c\ca# = a\ca#*b\aa# + a\cb#*b\ba# + a\cc#*b\ca# + a\cd#*b\da#
	c\cb# = a\ca#*b\ab# + a\cb#*b\bb# + a\cc#*b\cb# + a\cd#*b\db#
	c\cc# = a\ca#*b\ac# + a\cb#*b\bc# + a\cc#*b\cc# + a\cd#*b\dc#
	c\cd# = a\ca#*b\ad# + a\cb#*b\bd# + a\cc#*b\cd# + a\cd#*b\dd#
	
	; da db dc dd
	c\da# = a\da#*b\aa# + a\db#*b\ba# + a\dc#*b\ca# + a\dd#*b\da#
	c\db# = a\da#*b\ab# + a\db#*b\bb# + a\dc#*b\cb# + a\dd#*b\db#
	c\dc# = a\da#*b\ac# + a\db#*b\bc# + a\dc#*b\cc# + a\dd#*b\dc#
	c\dd# = a\da#*b\ad# + a\db#*b\bd# + a\dc#*b\cd# + a\dd#*b\dd#
End Function 
; ----------------------------------------------------------------------- 

; ----------------------------------------------------------------------- 
Function CreateVector.vector_t() 
	Local v.vector_t = New vector_t 
	Return v 
End Function 

Function FreeVector(v.vector_t) 
	Delete v 
	v = Null 
End Function 

Function VectorDotProduct#(a.vector_t, b.vector_t) 
	Return a\x#*b\x# + a\y#*b\y# + a\z#*b\z# 
End Function 

Function VectorLength#(v.vector_t) 
	Return Sqr#(VectorDotProduct#(v, v)) 
End Function 

Function VectorScale(v.vector_t, s#) 
	 
	; v = v * s# 

	v\x# = v\x# * s# 
	v\y# = v\y# * s# 
	v\z# = v\z# * s# 
End Function 

Function VectorNormalize(v.vector_t) 
	VectorScale(v, 1.0 / VectorLength(v)) 
End Function 

Function VectorCrossProduct(a.vector_t, b.vector_t, c.vector_t) 
	c\x# = a\y#*b\z# - a\z#*b\y#
	c\y# = a\z#*b\x# - a\x#*b\z#
	c\z# = a\x#*b\y# - a\y#*b\x#
End Function 

Function VectorAddition(a.vector_t, b.vector_t, c.vector_t) 
	 
	; c = a+b 

	c\x# = a\x# + b\x# 
	c\y# = a\y# + b\y# 
	c\z# = a\z# + b\z# 
End Function 

Function VectorSubtraction(a.vector_t, b.vector_t, c.vector_t) 

	; c = a-b 
	 
	c\x# = a\x# - b\x# 
	c\y# = a\y# - b\y# 
	c\z# = a\z# - b\z# 
End Function 
; ----------------------------------------------------------------------- 

; ----------------------------------------------------------------------- 
Function VectorMultiplyMatrix(v.vector_t, m.matrix_t) 
	Local t.vector_t = New vector_t 
	 
	; v = v*m 
	 
	t\x# = v\x# 
	t\y# = v\y# 
	t\z# = v\z# 
	 
	v\x# = m\aa#*t\x# + m\ba#*t\y# + m\ca#*t\z# + m\da#
	v\y# = m\ab#*t\x# + m\bb#*t\y# + m\cb#*t\z# + m\db#
	v\z# = m\ac#*t\x# + m\bc#*t\y# + m\cc#*t\z# + m\dc#
	 
	Delete t 
End Function 

Function MatrixCrossProduct(v.vector_t, m.matrix_t) 

	; v*m = VectorCrossProduct(v) 
	 
	m\ab# =  v\z# 
	m\ac# = -v\y# 
	m\ba# = -v\z# 
	m\bc# =  v\x# 
	m\ca# =  v\y# 
	m\cb# =  v\x# 
	m\dd# =  1.0 
End Function 
; -----------------------------------------------------------------------
