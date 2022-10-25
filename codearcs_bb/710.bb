; ID: 710
; Author: SSS
; Date: 2003-06-01 15:47:56
; Title: Vector Graphics Functions
; Description: Vector Graphics Functions for... Vector Graphics

here is the function set 


Type Vector2
Field x,y
End Type 
Type Vector3
Field x,y,z
End Type 

Type VectorGeomObject
Field start.VectorGeomSet
Field size
Field position.Vector2
End Type 

Type VectorGeomSet
Field movement.Vector2
Field StartColor.Vector3
Field EndColor.Vector3
Field NextSet.VectorGeomSet
End Type


Function Vec2.Vector2(x,y)
	v.Vector2 = New Vector2
	v\x = x
	v\y = y
	Return v
End Function

Function Vec3.Vector3(x,y,z)
	v.Vector3 = New Vector3
	v\x = x
	v\y = y
	v\z = z
	Return v
End Function

Function CreateVector(x = 0, y = 0)
	v.VectorGeomObject = New VectorGeomObject
	v\size = 0
	v\position.Vector2 = New Vector2
	v\position\x = x
	v\position\y = y
	Return Handle(v)
End Function

Function PositionVector(hVector,x,y)
	v.VectorGeomObject = Object.VectorGeomObject(hVector)
	v\position\x = x
	v\position\y = y
End Function 

Function AppendVector(hVector,vector.Vector2,startcolor.Vector3,endcolor.Vector3)
	v.VectorGeomObject = Object.VectorGeomObject(hVector)
	If v\size = 0
		v\start.VectorGeomSet = New VectorGeomSet
		vecset.VectorGeomSet = v\start
		vecset\movement.Vector2 = vector
		vecset\StartColor = startcolor
		vecset\EndColor = endcolor
		vecset\NextSet.VectorGeomSet = New VectorGeomSet
		v\size = v\size +1
	Else
		vecset.VectorGeomSet = v\start
		For i = 1 To v\size-1
			vecset = vecset\NextSet
		Next
		vecset\NextSet.VectorGeomSet = New VectorGeomSet
		vecset = vecset\NextSet
		vecset\movement.Vector2 = vector
		vecset\StartColor = startcolor
		vecset\EndColor = endcolor
		v\size = v\size+1
	EndIf
End Function

Function GetNumVectors(hVector)
	v.VectorGeomObject = Object.VectorGeomObject(hVector)
	Return v\size
End Function

Function EditVector(hVector,Index,vector.Vector2,startcolor.Vector3,endcolor.Vector3)
	v.VectorGeomObject = Object.VectorGeomObject(hVector)
	If Index > v\size Then Return
	vecset.VectorGeomSet = v\start
	For i = 1 To Index-1
		vecset = vecset\NextSet
	Next
	vecset\movement = vector
	vecset\StartColor = startcolor
	vecset\EndColor = endcolor
End Function 

Function VectorStartColor(hVector,Index,startcolor.Vector3)
	v.VectorGeomObject = Object.VectorGeomObject(hVector)
	If Index > v\size Then Return
	vecset.VectorGeomSet = v\start
	For i = 1 To Index-1
		vecset = vecset\NextSet
	Next
	vecset\StartColor = startcolor
End Function 

Function VectorMovement(hVector,Index,vector.Vector2)
	v.VectorGeomObject = Object.VectorGeomObject(hVector)
	If Index > v\size Then Return
	vecset.VectorGeomSet = v\start
	For i = 1 To Index-1
		vecset = vecset\NextSet
	Next
	vecset\movement = vector
End Function 

Function DrawVectors()
	Local x = 0
	Local y = 0
	For v.VectorGeomObject = Each VectorGeomObject
		vecset.VectorGeomSet = v\start
		x = v\position\x
		y = v\position\y
		For i = 1 To v\size
			Color vecset\StartColor\x,vecset\StartColor\y,vecset\StartColor\z
			Line x,y,vecset\movement\x+x,vecset\movement\y+y
			x = x + vecset\movement\x
			y = y + vecset\movement\y
			vecset = vecset\NextSet
		Next
	Next
End Function 

and here is an example 

Graphics 640,480,32,2
SetBuffer BackBuffer()
xz = CreateVector(320,240)


While Not KeyDown(1)
Cls
;x = x + 3
;If x > 630 Then x = -10
Rect 310,230,20,20
If MouseDown(1) 
AppendVector(xz,Vec2(MouseX()-320,MouseY()-240),Vec3(Rand(255),Rand(255),Rand(255)),Vec3(0,0,0))
Delay 100
EndIf
DrawVectors()
Flip
Wend
