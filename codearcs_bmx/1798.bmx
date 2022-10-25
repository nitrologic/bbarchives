; ID: 1798
; Author: Kurator
; Date: 2006-08-29 16:27:34
; Title: Vector Class
; Description: A Simple Vector Class to do some Maths on 3d Vectors

SuperStrict

Type Vector

	Field X:Double
	Field Y:Double
	Field Z:Double
	
	Function Create:Vector(vx:Double, vy:Double, vz:Double)
	
		Local TempVector:Vector = New Vector
		
		TempVector.X = vx
		TempVector.Y = vy
		TempVector.Z = vz

		Return TempVector
	
	EndFunction
	
	Method SetXYZ:Vector(vx:Double, vy:Double, vz:Double)
	
		X = vx
		Y = vy
		Z = vz
		
		Return Self
	
	EndMethod
	
	Method Set:Vector(v:Vector)
	
		X = v.X
		Y = v.Y
		Z = v.Z
		
		Return Self
	
	EndMethod
	
	Method Add:Vector(v:Vector)
	
		If v
		
			X = X + v.X
			Y = Y + v.Y
			Z = Z + v.Z
		
		EndIf
		
		Return Self
	
	EndMethod
	
	Method Sub:Vector(v:Vector)
	
		If v
		
			X = X - v.X
			Y = Y - v.Y
			Z = Z - v.Z
		
		EndIf
		
		Return Self
	
	EndMethod	
	
	Method SubXYZ:Vector(vx:Double, vy:Double, vz:Double)
	
		X = X - vx
		Y = Y - vy
		Z = Z - vz
		
		Return Self
	
	EndMethod	
	
	Method GetDotPV:Double(v:Vector)
	
		If v
		
			Return X * v.X + Y * v.Y + Z * v.Z
				
		EndIf
		
		Return 0
	
	EndMethod
	
	Method CrossPV:Vector(v:Vector)
	
		Local tx:Double, ty:Double, tz:Double

		If v 
		
			tx = X 
			ty = Y 
			tz = Z 
			X = ty * v.Z - tz * v.Y 
			Y = tz * v.X - tx * v.Z 
			Z = tx * v.Y - ty * v.X 
		
		EndIf 	
	
		Return Self
	
	EndMethod
	
	Method Mul:Vector(factor:Double)
	
		X :* factor
		Y :* factor
		Z :* factor
		
		Return Self
	
	EndMethod
	
	Method Div:Vector(divisor:Double)
	
		Local factor:Double
		
		factor = 1 / divisor
		
		X :* factor
		Y :* factor
		Z :* factor
		
		Return Self		
	
	EndMethod
	
	Method Normalize:Vector()

		Local factor:Double
		
		factor = 1.0 / self.GetLength()
		
		X :* factor
		Y :* factor
		Z :* factor
		
		Return Self				
	
	EndMethod
	
	Method RotateAroundX:Vector(angle:Double)
	
		Local tx:Double, ty:Double, tz:Double
		
		tx = X 
		ty = Y 
		tz = Z 
		' X = tx 
		Y = Cos(angle) * ty - Sin(angle) * tz 
		Z = Sin(angle) * ty + Cos(angle) * tz  		
		
		Return Self
	
	EndMethod
	
	Method RotateAroundY:Vector(angle:Double)
	
		Local tx:Double, ty:Double, tz:Double
		
		tx = X 
		ty = Y 
		tz = Z 
		X = Cos(angle) * tx + Sin(angle) * tz
		' Y = ty   
		Z = -Sin(angle) * tx + Cos(angle) * tz		
		
		Return Self
	
	EndMethod
	
	Method RotateAroundZ:Vector(angle:Double)
	
		Local tx:Double, ty:Double, tz:Double
		
		tx = X 
		ty = Y 
		tz = Z 
		X = Cos(angle) * tx - Sin(angle) * ty
		Y = Sin(angle) * tx + Cos(angle) * ty   
		' Z = tz		
		
		Return Self
	
	EndMethod
	
	Method RotateAroundV:Vector(v:Vector, angle:Double)
	
		Local tx:Double, ty:Double, tz:Double, cosa:Double, sina:Double, ecosa:Double

		cosa = Cos(angle) 
		sina = Sin(angle) 
		ecosa = 1.0 - cosa

		tx = X 
		ty = Y 
		tz = Z 
		
		X = tx * (cosa + v.X * v.X * ecosa) + ty * (v.X * v.Y * ecosa - v.Z * sina) + tz * (v.X * v.Z * ecosa + v.Y * sina) 
		Y = tx * (v.Y * v.X * ecosa + v.Z * sina) + ty * (cosa + v.Y * v.Y * ecosa) + tz * (v.Y * v.Z * ecosa - v.X * sina)
		Z = tx * (v.Z * v.X * ecosa - v.Y * sina) + ty * (v.Z * v.Y * ecosa + v.X * sina) + tz * (cosa + v.Z * v.Z * ecosa)
		
		Return Self				
	
	EndMethod	
	
	Method Copy2Vec:Vector()
	
		Local v:Vector = New Vector
	
		If v
		
			v.X = X
			v.Y = Y
			v.Z = Z
		
		EndIf
		
		Return v
		
	EndMethod
		
	Method GetX:Double()
	
		Return X:Double

	EndMethod
	
	Method GetY:Double()
	
		Return Y:Double

	EndMethod

	Method GetZ:Double()
	
		Return Z:Double

	EndMethod
	
	Method GetLength:Double()
	
		Return Sqr(X * X + Y * Y + Z * Z) 
	
	EndMethod
	
	Method GetLengthSqr:Double()
	
		Return X * X + Y * Y + Z * Z
	
	EndMethod	
	
	Method SetX:Vector(newX:Double)
	
		X = newX

	EndMethod
	
	Method SetY:Vector(newY:Double)
	
		Y = newY

	EndMethod

	Method SetZ:Vector(newZ:Double)
	
		Z = newZ

	EndMethod	

EndType

Rem 

	DebugLog "Small Test Code"
	
	Local a:Vector
	Local b:Vector
	
	Local t:Double
	
	a = vector.create(1, 2, 3)
	b = vector.create(2, 3, 0)
	
	
	DebugLog "Vector a"
	DebugLog " a.x = " + String(a.GetX())
	DebugLog " a.Y = " + String(a.GetY())
	DebugLog " a.Z = " + String(a.GetZ())
	
	DebugLog "Vector b"
	DebugLog " b.x = " + String(b.GetX())
	DebugLog " b.Y = " + String(b.GetY())
	DebugLog " b.Z = " + String(b.GetZ())
	
	
	DebugLog "Adding Vector b to Vector a"
	a.Add(b)
	DebugLog "Vector a"
	DebugLog " a.x = " + String(a.GetX())
	DebugLog " a.Y = " + String(a.GetY())
	DebugLog " a.Z = " + String(a.GetZ())
	
	DebugLog "Subtracting Vector b to Vector a"
	a.Sub(b)
	DebugLog "Vector a"
	DebugLog " a.x = " + String(a.GetX())
	DebugLog " a.Y = " + String(a.GetY())
	DebugLog " a.Z = " + String(a.GetZ())
	DebugLog "Length of Vector a = " + String(a.GetLength())
	DebugLog "Length of Vector b = " + String(b.GetLength())
	DebugLog "Normalize Vector b "
	b.Normalize()
	DebugLog "Vector b"
	DebugLog " b.x = " + String(b.GetX())
	DebugLog " b.Y = " + String(b.GetY())
	DebugLog " b.Z = " + String(b.GetZ())
	DebugLog "Length of Vector b = " + String(b.GetLength())
	DebugLog "Rotate Vector a by 90 Degree around X Axis"
	a.RotateAroundX(90)
	DebugLog "Vector a"
	DebugLog " a.x = " + String(a.GetX())
	DebugLog " a.Y = " + String(a.GetY())
	DebugLog " a.Z = " + String(a.GetZ())
	DebugLog "Rotate Vector a by -90 Degree around X Axis"
	a.RotateAroundX(-90)
	DebugLog "Vector a"
	DebugLog " a.x = " + String(a.GetX())
	DebugLog " a.Y = " + String(a.GetY())
	DebugLog " a.Z = " + String(a.GetZ())
	DebugLog "Rotate Vector by 90 Degree around Vector b"
	a.RotateAroundV(b,90)
	DebugLog "Vector a"
	DebugLog " a.x = " + String(a.GetX())
	DebugLog " a.Y = " + String(a.GetY())
	DebugLog " a.Z = " + String(a.GetZ())
	DebugLog "Rotate Vector by -90 Degree around Vector b"
	a.RotateAroundV(b,-90)
	DebugLog "Vector a"
	DebugLog " a.x = " + String(a.GetX())
	DebugLog " a.Y = " + String(a.GetY())
	DebugLog " a.Z = " + String(a.GetZ())
	
EndRem
