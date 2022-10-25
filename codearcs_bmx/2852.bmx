; ID: 2852
; Author: Bladum
; Date: 2011-05-13 16:09:09
; Title: Perlin Noise Type
; Description: 2D Perlin Noise for making random maps

'==========================================
'           CLASS                          
'   main perlin noise class               
'==========================================
Type CPerlinNoise

	Field sizeX = 640
	Field sizeY = 480

	'first random noise table	
	Field noiseArr:Float[,] = New Float[sizeX, sizeY]
	'output table
	Field terrainArr:Float[,] = New Float[sizeX, sizeY]
	'output bitmap
	Field bitmapOut:TPixmap = TPixmap.Create(sizeX, sizeY, PF_RGBA8888)

	'frequency, the lower the larger terrains of same level	
	Field frequency:Float = 1.0
	'starting amplitude
	Field amplitude:Float = 1.0
	'change of amplitude of next octave
	Field persistance:Float = 0.6
	'number of octaves
	Field octaves = 8

	'min and max colors
	Field colMin:CColor = CColor.Create(0, 0, 0)
	Field colMax:CColor = CColor.Create(0, 0, 0)
	
	'==========================================
	'    to init perlin noise values
	'==========================================
	Method ChangeParams(fre:Float, amp:Float, pers:Float, oct)
		
		frequency = fre
		amplitude = amp
		persistance = pers
		octaves = oct
	
	End Method
	
	'==========================================
	'       single field noise generation
	'=========================================
	Method GetRandomNoise:Float(x:Float, y:Float)
		
		Local fre:Float = frequency
		Local amp:Float = amplitude
		Local finalValue:Float = 0.0
		
		For Local i = 0 To octaves
			finalValue = finalValue + LinearFIlterNoise(x * fre, y * fre) * amp
			fre = fre * 2.0
			amp = amp * persistance
		Next
		
		If(finalValue < - 1.0) finalValue = -1.0
		If(finalValue > 1.0) finalValue = 1.0
		
		finalValue = finalValue * 0.5 + 0.5
		
		Return finalValue
	
	End Method
	
	'==========================================
	'      create output terrain array
	'==========================================
	Method MakeTerrainMap()
		
		For Local x = 0 To sizeX - 1
			For Local y = 0 To sizeY - 1
				terrainArr[x, y] = GetRandomNoise(x, y)
			Next
		Next
	
	End Method
	
	'==========================================
	'   process bitmap to file
	'==========================================
	Method MakeBitmap()
		
		bitmapOut.ClearPixels($FFFFFFFF)
			
		For Local x = 0 To sizeX - 1
			For Local y = 0 To sizeY - 1
			
				Local val:Float = terrainArr[x, y]
				Local R = colMax.R * val + colMin.R * (1 - val)
				Local G = colMax.G * val + colMin.G * (1 - val)
				Local B = colMax.B * val + colMin.B * (1 - val)
								
				SetColor(R, G, B)
				bitmapOut.WritePixel(x, y, $FF000000 + (R * $10000 + G * $100 + B))
				
			Next
		Next
	
	End Method
	
	'============================================
	'    perlin noise with linear interpolation
	'===========================================
	Method LinearFilterNoise:Float(x:Float, y:Float)
		
		Local fractionX:Float = X - Int(X)
		Local fractionY:Float = Y - Int(Y)
		
		Local x1 = (Int(x) + sizeX) Mod sizeX
		Local y1 = (Int(y) + sizeY) Mod sizeY
		Local x2 = (Int(x) + sizeX - 1) Mod sizeX
		Local y2 = (Int(y) + sizeY - 1) Mod sizeY
		
		If(x1 < 0) x1 = x1 + sizeX
		If(x2 < 0) x2 = x2 + sizeX
		If(y1 < 0) y1 = y1 + sizeY
		If(y2 < 0) y2 = y2 + sizeY
		
		Local finVal:Float = 0
		
		finVal = finVal + fractionX * fractionY * noiseArr[x1, y1]
		finVal = finVal + fractionX * (1 - fractionY) * noiseArr[x1, y2]
		finVal = finVal + (1 - fractionX) * fractionY * noiseArr[x2, y1]
		finVal = finVal + (1 - fractionX) * (1 - fractionY) * noiseArr[x2, y2]
		
		Return finVal
	
	End Method
	
	'===========================================
	'     to fill noise array with white noise
	'===========================================
	Method InitNoise()
		
		noiseArr = New Float[sizeX, sizeY]
		For Local x = 0 To sizeX - 1
			For Local y = 0 To sizeY - 1
				noiseArr[x, y] = (RndFloat() - 0.5) * 2.0
			Next
		Next
			
	End Method
	
	'===========================================
	Method terrainSinus(p:Float)
		
		For Local x = 0 To sizeX - 1
			For Local y = 0 To sizeY - 1
		
				Local md:Float = Sin(y * 180 / sizeY) * 2 - 1
				terrainArr[x, y] = md * p + terrainArr[x, y] * (1.0 - p)
				
			Next
		Next
	
	End Method
	
	'============================================
	'      start process
	'===========================================
	Function Calculate()
		
		'create altitude map
		Local highMap:CPerlinNoise = New CPerlinNoise
		highMap.ChangeParams(0.02, 0.95, 0.6, 6)
		highMap.colMin = CColor.Create(0, 120, 0)
		highMap.colMax = CColor.Create(100, 220, 100)
		highMap.InitNoise()
		highMap.MakeTerrainMap()
		highMap.MakeBitmap()
		SavePixmapPNG(highMap.bitmapOut, "high1.png")
		
		'creat humitidy map
		Local humMap:CPerlinNoise = New CPerlinNoise
		humMap.ChangeParams(0.04, 0.99, 0.6, 6)
		humMap.colMin = CColor.Create(0, 0, 20)
		humMap.colMax = CColor.Create(0, 50, 120)
		humMap.InitNoise()
		humMap.MakeTerrainMap()
		humMap.MakeBitmap()
		SavePixmapPNG(humMap.bitmapOut, "high2.png")
		
		'create temperature map
		Local tempMap:CPerlinNoise = New CPerlinNoise
		tempMap.ChangeParams(0.04, 0.99, 0.6, 6)
		tempMap.colMin = CColor.Create(60, 0, 0)
		tempMap.colMax = CColor.Create(240, 0, 0)
		tempMap.InitNoise()
		tempMap.MakeTerrainMap()
		tempMap.terrainSinus(0.6)
		tempMap.MakeBitmap()
		SavePixmapPNG(tempMap.bitmapOut, "high3.png")
		
		'generate additional world map
		GenerateWorldMap(highMap, humMap, tempMap)

	End Function
	
	'=============================================
	'      generate simple world map
	'=============================================
	Function GenerateWorldMap(highMap:CPerlinNoise,humMap:CPerlinNoise,tempMap:CPerlinNoise)
	
		Local pixies:TPixmap = TPixmap.Create(tempMap.sizeX, tempMap.sizeY, PF_RGBA8888)
		
		For Local x = 0 To tempMap.sizeX - 1
			For Local y = 0 To tempMap.sizeY - 1
		
				Local T:Float = tempMap.terrainArr[x, y] * 2 - 1.0
				Local H:Float = humMap.terrainArr[x, y] * 2 - 1.0
				Local A:Float = highMap.terrainArr[x, y] * 2 - 1.0
			
				Local R, G, B
				
				'water
				If(A < 0)
					R = 0
					G = 60 + A * 60
					B = 120 + A * 100
				'land
				Else
				
					'altitude
					R = 60 + A * 180
					G = 60 + A * 180
					B = 60 + A * 180
					
					'temperature
					If(T >= 0)
						G = G * (1.0 - T * 0.3)
						B = B * (1.0 - T * 0.3)
					Else
						R = R * (1.0 + T * 0.3)
					End If
					
					'high humidity
					If(H >= 0)
						R = R * (1.0 - H * 0.3)
						B = B * (1.0 - H * 0.3)
					Else
						G = G * (1.0 + H * 0.3)
					End If
				
				End If

				'some final quantizations
								
				R = (R / 15) * 15
				G = (G / 15) * 15
				B = (B / 15) * 15
				
				
				pixies.WritePixel(x, y, $FF000000 + (R * $10000 + G * $100 + B))
			
			Next
		Next
		SavePixmapPNG(pixies, "worldMap.png")
		
	End Function
	
End Type



'==================================
'			CLASS                  
'just simple class to help colors
'==================================
Type CColor

	Field R = 0
	Field G = 0
	Field B = 0
	
	'==================================
	Function Create:CColor(r, g, b)
		Local aa:CColor = New CColor
		
		aa.R = r
		aa.g = g
		aa.b = b
		
		Return aa
	End Function
	
	'==================================
	Function Process:CColor(f:Long)
		Local aa:CColor = New CColor
		
		aa.R = f & $00FF0000
		aa.R = aa.R / $10000
		aa.G = f & $0000FF00
		aa.G = aa.G / $100
		aa.B = f & $000000FF		
		
		Return aa
	End Function
	
End Type


'===========================================================
'===========================================================
'===========================================================
'===========================================================
'===========================================================
'===========================================================

'some simple setup
SetGraphicsDriver(GLMax2DDriver ())
Graphics(640, 480, 0, 60)
	
'run perlin noise
CPerlinNoise.Calculate()

Global img:TImage = LoadImage("worldMap.png")
	
While Not KeyHit(KEY_ESCAPE) And Not AppTerminate()
	Cls()
	
	DrawImage(img,0,0)

	Flip(1)
Wend
