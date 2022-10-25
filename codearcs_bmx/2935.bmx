; ID: 2935
; Author: Kittomer
; Date: 2012-03-17 03:58:05
; Title: 2D and 3D simplex noise
; Description: Very fast simplex noise

SuperStrict

Const _NoiseSize:Int = 256

Graphics (_NoiseSize, _NoiseSize)

Global _Noise:SimplexNoise = New SimplexNoise

Global _Z:Float

Global _Scale:Float = 0.01

Global _Image:TImage = CreateImage(_NoiseSize, _NoiseSize, 1, DYNAMICIMAGE)

Global _Pixmap:TPixmap

Global _temp:Float

Global _fpsMS:Int
Global _MSperFrame:Int
Global _lasttime:Int = MilliSecs()
Global _FPSCount:Int
Global _FPS:Int

While Not AppTerminate()

	If KeyDown (KEY_ESCAPE)
	
		End
	
	End If

	If KeyDown (KEY_R)
	
		_Noise.Randomize()
	
	End If
	
	'Increase or decrease scale
	If KeyDown (KEY_LEFT)
	
		_Scale:+0.001
	
	End If
	
	If KeyDown (KEY_RIGHT)
	
		If _Scale > 0 _Scale:-0.001
	
	End If
	
	'Increase or decrease Z-coordinate (animates noise)
	
	If KeyDown (KEY_UP)
	
		_Z:+0.01
	
	End If
	
	If KeyDown (KEY_DOWN)
	
		If _Z > 0 _Z:-0.01
	
	End If
	
	'Create Noise	
	_Pixmap = LockImage(_Image)
	
	For Local _x:Int = 0 To _NoiseSize - 1
		
		For Local _y:Int = 0 To _NoiseSize - 1
		
			_temp = _Noise.Noise_3D(_x * _Scale, _y * _Scale, _Z)
			
			_temp = (_temp + 1.0) / 2 'Normalize output range from -1.0/+1.0 to 0.0/1.0
			
			_Pixmap.WritePixel(_x, _y, argb(_temp * 255, _temp * 255, _temp * 255, 255))
				
		Next
			
	Next
		
	UnlockImage (_Image)
	
	Cls
	
	If _Image <> Null DrawImage (_Image, 0, 0)
		
	SetColor (0, 255, 0)
	
	DrawText ("Noise Scale: " + _Scale, 10, 10)
	DrawText ("Z Coordinate: " + _Z, 10, 25)
	DrawText ("FPS: " + _FPS, 10, 40)
	
	SetColor (255, 255, 255)
	
	Flip
	
	FPS()

Wend

'Simplex noise in 2D and 3D
Type SimplexNoise
	
	'Gradient table
	Field grad3:grad[] = [New Grad.Create(1, 1, 0), New Grad.Create(-1, 1, 0), New Grad.Create(1, -1, 0), New Grad.Create(-1, -1, 0),  ..
						 New Grad.Create(1, 0, 1), New Grad.Create(-1, 0, 1), New Grad.Create(1, 0, -1), New Grad.Create(-1, 0, -1),  ..
						 New Grad.Create(0, 1, 1), New Grad.Create(0, -1, 1), New Grad.Create(0, 1, -1), New Grad.Create(0, -1, -1)]
	
	'Permutation table.
	Field perm:Int[] = New Int[512]
	
	'Permutation table containing precomputed, mod12'd perm table.
	Field permMod12:Int[] = New Int[512]
	
	'Precomputed skew factors.
	Field F2:Float = 0.5 * (Sqr(3.0) - 1.0)
	Field G2:Float = (3.0 - Sqr(3.0)) / 6.0
	Field F3:Float = 1.0 / 3.0
	Field G3:Float = 1.0 / 6.0
  	
	Method New()
	
		'Randomize the permutation tables.
		For Local I:Int = 0 To 511
			perm[I] = Rand (0, 255)
			permMod12[I] = perm[I] Mod 12
		Next
		
	End Method
	
	'Re-Randomize the permutation tables.
	Method Randomize()
	
		For Local I:Int = 0 To 511
			perm[I] = Rand (0, 255)
			permMod12[I] = perm[I] Mod 12
		Next
		
	End Method
	
	'Should be faster than Floor()
	Method FastFloor:Int(x:Float)
	
		Local y:Int
	
		If x > 0
			y = Int (x)
		Else
			y = Int (x - 1)
		End If
		
		Return y
		
	End Method
	
	'Dot product for 3D Noise
	Method Dot3D:Float (g:Grad, x:Float, y:Float, z:Float)
	
	Return g.x * x + g.y * y + g.z * z
	
	EndMethod
	
	'Dot product for 2D Noise
	Method Dot2D:Float (g:Grad, x:Float, y:Float)
	
	Return g.x * x + g.y * y
	
	EndMethod
	
  	' 2D simplex noise
  	Method Noise_2D:Float (xin:Float, yin:Float)
		
		' Noise contributions from the three corners
	    Local n0:Float, n1:Float, n2:Float
		
	    ' Skew the input space to determine which simplex cell we're in
		Local s:Float = (xin + yin) * F2 ' Hairy factor for 2D
		
	    Local I:Int = FastFloor(xin + s)
	    Local j:Int = FastFloor(yin + s)
		
	    Local t:Float = (I + j) * G2
		
		' Unskew the cell origin back to (x,y) space
	    Local X0:Float = I - t
	    Local Y0:Float = j - t
		
		' The x,y distances from the cell origin
	    X0 = xin - X0
	    Y0 = yin - Y0
		
	    'For the 2D case, the simplex shape is an equilateral triangle.
	    'Determine which simplex we are in.
		
		'Offsets for second (middle) corner of simplex in (i,j) coords
	    Local i1:Int, j1:Int
		
	    If X0 > Y0 Then
			' lower triangle, XY order: (0,0)->(1,0)->(1,1)
	    	i1 = 1
			j1 = 0
	    Else
			' upper triangle, YX order: (0,0)->(0,1)->(1,1)
			i1 = 0
			j1 = 1
		EndIf
		
	    ' A step of (1,0) in (i,j) means a step of (1-c,-c) in (x,y), and
	    ' a step of (0,1) in (i,j) means a step of (-c,1-c) in (x,y), where
	    ' c = (3-sqrt(3))/6
		
		' Offsets for middle corner in (x,y) unskewed coords
	    Local x1:Float = x0 - i1 + G2
	    Local y1:Float = y0 - j1 + G2
		
		' Offsets for last corner in (x,y) unskewed coords
	    Local x2:Float = x0 - 1.0 + 2.0 * G2
	    Local y2:Float = y0 - 1.0 + 2.0 * G2
		
	    ' Work out the hashed gradient indices of the three simplex corners
	    Local ii:Int = I & 255
	    Local jj:Int = j & 255
	    Local gi0:Int = permMod12[ii + perm[jj]]
	    Local gi1:Int = permMod12[ii + i1 + perm[jj + j1]]
	    Local gi2:Int = permMod12[ii + 1 + perm[jj + 1]]
		
	    ' Calculate the contribution from the three corners
	    Local t0:Float = 0.5 - X0 * X0 - Y0 * Y0
		
	    If t0 < 0 Then
			n0 = 0.0
	    Else
	    	t0 = t0 * t0
	    	n0 = t0 * t0 * Dot2D(grad3[gi0], X0, Y0) ' (x,y) of grad3 used for 2D gradient
	    EndIf
		
	    Local t1:Float = 0.5 - x1 * x1 - y1 * y1
		
	    If t1 < 0 Then
			n1 = 0.0
	    Else
			t1 = t1 * t1
	    	n1 = t1 * t1 * Dot2D(grad3[gi1], x1, y1)
	    EndIf
		
	    Local t2:Float = 0.5 - x2 * x2 - y2 * y2
		
	    If t2 < 0 Then
			n2 = 0.0
	    Else
	    	t2 = t2 * t2
	    	n2 = t2 * t2 * Dot2D(grad3[gi2], x2, y2)
	    EndIf
		
	    ' Add contributions from each corner to get the final noise value.
	    ' The result is scaled to return values in the interval [-1,1].
	    Return 70.0 * (n0 + n1 + n2)
		
  	EndMethod
	
	' 3D simplex noise
	Method Noise_3D:Float(xin:Float, yin:Float, zin:Float)
	    
		' Noise contributions from the four corners
		Local n0:Float, n1:Float, n2:Float, n3:Float
	    
		' Skew the input space to determine which simplex cell we're in
	    Local s:Float = (xin + yin + zin) * F3 ' Very nice And simple skew factor For 3D
	    Local I:Int = fastfloor(xin + s)
	    Local j:Int = fastfloor(yin + s)
	    Local k:Int = fastfloor(zin + s)
		
	    Local t:Float = (I + j + k) * G3
		
		' Unskew the cell origin back to (x,y,z) space
	    Local X0:Float = I - t
	    Local Y0:Float = j - t
	    Local Z0:Float = k - t
		
		' The x,y,z distances from the cell origin
	    X0 = xin - X0
	    Y0 = yin - Y0
	    Z0 = zin - Z0
		
	    ' For the 3D case, the simplex shape is a slightly irregular tetrahedron.
	    ' Determine which simplex we are in.
		
		' Offsets for second corner of simplex in (i,j,k) coords
	    Local i1:Int, j1:Int, k1:Int
		
		' Offsets for third corner of simplex in (i,j,k) coords
	    Local i2:Int, j2:Int, k2:Int
		
	    If X0 >= Y0 Then
	    	If Y0 >= Z0 Then
	        	i1 = 1; j1 = 0; k1 = 0; i2 = 1; j2 = 1; k2 = 0 ' X Y Z order
	        Else If X0 >= Z0
				i1 = 1; j1 = 0; k1 = 0; i2 = 1; j2 = 0; k2 = 1 ' X Z Y order
	        Else
				i1 = 0; j1 = 0; k1 = 1; i2 = 1; j2 = 0; k2 = 1 ' Z X Y order
	    	EndIf
	    Else ' x0<y0
	    	If Y0 < Z0
		  		i1 = 0; j1 = 0; k1 = 1; i2 = 0; j2 = 1; k2 = 1 ' Z Y X order
		  	Else If X0 < Z0
				i1 = 0; j1 = 1; k1 = 0; i2 = 0; j2 = 1; k2 = 1 ' Y Z X order
	      	Else
				i1 = 0; j1 = 1; k1 = 0; i2 = 1; j2 = 1; k2 = 0 ' Y X Z order
			EndIf
	    EndIf
		
	    ' A step of (1,0,0) in (i,j,k) means a step of (1-c,-c,-c) in (x,y,z),
	    ' a step of (0,1,0) in (i,j,k) means a step of (-c,1-c,-c) in (x,y,z), and
	    ' a step of (0,0,1) in (i,j,k) means a step of (-c,-c,1-c) in (x,y,z), where
	    ' c = 1/6.
		
		' Offsets for second corner in (x,y,z) coords
	    Local x1:Float = X0 - i1 + G3
	    Local y1:Float = Y0 - j1 + G3
	    Local z1:Float = Z0 - k1 + G3
		
		' Offsets for third corner in (x,y,z) coords
	    Local x2:Float = X0 - i2 + 2.0 * G3
	    Local y2:Float = Y0 - j2 + 2.0 * G3
	    Local z2:Float = Z0 - k2 + 2.0 * G3
		
		' Offsets for last corner in (x,y,z) coords
	    Local x3:Float = X0 - 1.0 + 3.0 * G3
	    Local y3:Float = Y0 - 1.0 + 3.0 * G3
	    Local z3:Float = Z0 - 1.0 + 3.0 * G3
		
	    ' Work out the hashed gradient indices of the four simplex corners
	    Local ii:Int = I & 255
	    Local jj:Int = j & 255
	    Local kk:Int = k & 255
	    Local gi0:Int = permMod12[ii + perm[jj + perm[kk]] ]
	    Local gi1:Int = permMod12[ii + i1 + perm[jj + j1 + perm[kk + k1]] ]
	    Local gi2:Int = permMod12[ii + i2 + perm[jj + j2 + perm[kk + k2]] ]
	    Local gi3:Int = permMod12[ii + 1 + perm[jj + 1 + perm[kk + 1]] ]
		
	    ' Calculate the contribution from the four corners
		
	    Local t0:Float = 0.6 - x0 * x0 - y0 * y0 - z0 * z0
		
	    If t0 < 0
			n0 = 0.0
		Else
	      t0 = t0 * t0
	      n0 = t0 * t0 * Dot3D (grad3[gi0], X0, Y0, Z0)
	    EndIf
		
	    Local t1:Float = 0.6 - x1 * x1 - y1 * y1 - z1 * z1
		
	    If t1 < 0
			n1 = 0.0
	    Else
	      t1 = t1 * t1
	      n1 = t1 * t1 * Dot3D(grad3[gi1], x1, y1, z1)
	    EndIf
		
	    Local t2:Float = 0.6 - x2 * x2 - y2 * y2 - z2 * z2
		
	    If t2 < 0
			n2 = 0.0
	    Else
	      t2 = t2 * t2
	      n2 = t2 * t2 * Dot3D(grad3[gi2], x2, y2, z2)
	    EndIf
		
	    Local t3:Float = 0.6 - x3 * x3 - y3 * y3 - z3 * z3
		
	    If t3 < 0
			n3 = 0.0
	    Else
	      t3 = t3 * t3
	      n3 = t3 * t3 * Dot3D(grad3[gi3], x3, y3, z3)
	    EndIf
		
	    ' Add contributions from each corner to get the final noise value.
	    ' The result is scaled to stay just inside [-1,1]
	    Return 32.0 * (n0 + n1 + n2 + n3)
		
	EndMethod

EndType

'Custom data type for simplex noise gradient definition.
Type Grad

	Field x:Float, y:Float, z:Float
	
	Method Create:grad(x:Float, y:Float, z:Float = 0.0)
	
		Self.x = x
		Self.y = y
		Self.z = z
		
		Return Self
	
	End Method

EndType

Function argb:Int(_red:Int, _green:Int, _blue:Int, _alpha:Int)
	
	Local _argb:Int = 256 * 256 * 256 * _alpha + 256 * 256 * _blue + 256 * _green + _red
	
	Return _argb
		
End Function

Function FPS()
	
	_fpsMS:+MilliSecs() - _lasttime
	_MSperFrame = (MilliSecs() - _lasttime) '* 0.01
	_lasttime = MilliSecs()
	_FPSCount:+1
			
	If (_fpsMS >= 1000)
		_FPS = _FPSCount
		_FPSCount = 0
		_fpsMS:-1000
	End If

End Function
