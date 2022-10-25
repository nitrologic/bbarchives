; ID: 3221
; Author: _PJ_
; Date: 2015-09-01 06:11:01
; Title: Simple Smooth Noise
; Description: Smooth Random Noise

;Really basic example:

Graphics 1600,900,32
Global NoiseSeed
Const NoiseCells%=64

Dim NoiseMap#(0,0)
Dim TangentMap#(0,0)

NoiseSeed=InitialiseNoise()
GenerateContourMap()

w=NoiseCells-1
h=NoiseCells-1
For y= 0 To h
	For x=0 To w
		c#=NoiseMap(x,y)
		c=c*255
		Color c,c,c
		Rect x*4,y*4,4,4,True 
	Next
Next




;_________________________________________________________


;Fast Smooth Noise by PJ Chowdhury 2015

Function InitialiseNoise(Seed=0)
	If (Not(Seed))
		Seed=MilliSecs()
	End If
	SeedRnd Seed
	
	Return Seed
End Function

Function GenerateContourMap()
	Local Y#
	Local X#
	Local Z#
	
	Local W=NoiseCells-1
	Local H=NoiseCells-1
	
	RandomiseNoiseMap
	SmootheMap
End Function

Function SmootheMap()
        CalculateTangentMap
	
	Local W=NoiseCells-1
	Local H=NoiseCells-1
	
	Local X
	Local Y
	Local Z#
	
	For Y = 0 To H
		For X = 0 To W
			
			;Contour Map Point
			Z=(NoiseMap(X,Y)+(TangentMap(X,Y)))
			
			;Modify point by tangential amount
			NoiseMap(X,Y)=Z
			
		Next
	Next
	
	;We have finished with Tangent Map now, so DeAllocate memory space
	Dim TangentMap#(0,0)
End Function

Function CalculateTangentMap()
	Local X
	Local Y
	
	Local XX
	Local YY
	
	Local XXX
	Local YYY
	
	Local W=NoiseCells-1
	Local H=NoiseCells-1
	
	Dim TangentMap#(W,H)
	
	Local TestPoint#
	Local Current#
	Local Difference#
	Local Mean#
	
	;First Pass to Populate Base Tangents and obtain Maxima/Minima
	For Y=0 To H
		For X= 0 To W
			
			Current=NoiseMap(X,Y)
			Mean=0.0
			
			;Determine height difference by contributions  from all surrounding points (including wraparound boundary for tiling)
			For YY=Y-1 To Y+1
				For XX=X-1 To X+1
					
					;This allows for wraparound
					XXX=((XX+NoiseCells) Mod NoiseCells)
					YYY=((YY+NoiseCells) Mod NoiseCells)
					
					TestPoint#=NoiseMap#(XXX,YYY)
					Difference=(TestPoint-Current)
					Mean=Mean+Difference
					
				Next
			Next
			
			;Average weighting contributions for this cell
			Mean#=Mean# * 0.125
			
			;Store the mean tangent value
			TangentMap#(X,Y) = Mean#
			
		Next
	Next
	
End Function

Function RandomiseNoiseMap()
	Local Y#
	Local X#
	Local Z#
	
	Local W%=NoiseCells-1
	Local H%=NoiseCells-1
	
	Dim NoiseMap#(W,H)
	
	;Randomise all points
	For Y=0 To H
		For X=0 To W
			;Put value into Z# to ensure it's float
			Z#=Rnd(0.0,1.0)
			NoiseMap#(X,Y)=Z#
		Next
	Next
End Function
