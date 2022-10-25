; ID: 3223
; Author: Flanker
; Date: 2015-09-17 13:09:04
; Title: Perlin noise
; Description: perlin noise function with parameters and loop ability

Dim octave(x,y,octaves)
Dim perlin#(x,y)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
; example ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
Graphics 800,600,32,2
SetBuffer BackBuffer()

SeedRnd MilliSecs()

perlin_size = 256

While Not KeyHit(1)

	Cls

	time = Perlin_Generate(perlin_size,8,4,0.5,False,0)
	
	LockBuffer()
	
	For x = 0 To perlin_size-1
		For y = 0 To perlin_size-1
		
			rgb = perlin(x,y) Or (perlin(x,y) Shl 8) Or (perlin(x,y) Shl 16)
		
			WritePixelFast x,y,rgb
		
		Next
	Next
	
	UnlockBuffer()
	
	Text 650,50,time + "ms"
	
	Flip
	
	WaitKey()
	
Wend

End
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
Function Perlin_Generate(size#=128,octaves#=8,frequency#=8,persistence#=0.5,loop=False,seed=0)

	; time measure
	time = MilliSecs()

	; used to store maximum value
	maximum# = 0
	minimum# = 2147483647


	; resize arrays
	Dim octave(size+1,size+1,octaves)
	Dim perlin(size+1,size+1)

	; calculate the size of initial cells
	inc# = size / frequency
	
	; fill randomly octave 0
	If seed <> 0 Then SeedRnd seed
	For x = 0 To size
		For y = 0 To size
		
			octave(x,y,0) = Rand(255)
			
			; to loop the noise : fill cells from a side with value from opposite side
			If loop = True
				If x = size Then octave(x,y,0) = octave(0,y,0)
				If y = size Then octave(x,y,0) = octave(x,0,0)
			EndIf
			
		Next
	Next
	
	; interpolate points from octaves, based on first octave
	For layer = 1 To octaves
		
		; initialize new x for each layer
		x1# = 0
		x2# = inc
		
		For x = 0 To size
		
			; advance in x if necessary
			If x = x2
				x1 = x2
				x2 = x2+inc
				If x2 > size+1 Then x2 = size+1
			EndIf
		
			; initialize new x for each layer
			y1# = 0
			y2# = inc
		
			For y = 0 To size
			
				; advance in x if necessary
				If y = y2
					y1 = y2
					y2 = y2+inc
					If y2 > size+1 Then y2 = size+1
				EndIf			

				; special function from Ken Perlin, similar to a sinusoid but faster than Sin/Cos
				position# = (y-y1) / (y2-y1)
				position = position * position * position * (position * (position * 6 - 15) + 10)
				
				v1# = octave(x1,y1,0) + position * (octave(x1,y2,0) - octave(x1,y1,0))
				v2# = octave(x2,y1,0) + position * (octave(x2,y2,0) - octave(x2,y1,0))
				
				position = (x-x1) / (x2-x1)
				position = position * position * position * (position * (position * 6 - 15) + 10)
				
				octave(x,y,layer) = v1 + position * (v2 - v1)
								
				; add the new point to perlin array (sum)
				perlin(x,y) = perlin(x,y) + octave(x,y,layer) * persistence^layer
				
				; update minimum and maximum values
				If perlin(x,y) > maximum Then maximum = perlin(x,y)
				If perlin(x,y) < minimum Then minimum = perlin(x,y)

				
			Next
		Next
		
		frequency = frequency * 2 ; from each octave to the next, multiply the frequency by 2
		inc = size / frequency ; update the cells size (or step)

	Next
	
	; interpolate noise from 0 to 255
	For x = 0 To size
		For y = 0 To size
		
			perlin(x,y) = (perlin(x,y)-minimum) / (maximum-minimum) * 255
		
		Next
	Next
	
	; return the time taken to proceed
	Return MilliSecs()-time
	
End Function
