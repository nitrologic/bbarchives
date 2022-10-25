; ID: 288
; Author: Rob
; Date: 2002-04-07 20:05:48
; Title: 2D and 3D water
; Description: the underlying algo to create water ripples

; 2D water - simple version
; use height/col result to deform terrains or meshes for water ripples.
; By Rob Cummings

Const  MAXX = 64	; Width And height of WaveMap 
Const  MAXY = 64
Const  DAMP# = 0.1;16
Global CT=0
Global NW=1
Dim WaveMap#(1,MAXX,MAXY)

Graphics 320,240,16,2
SetBuffer BackBuffer()

While Not KeyHit(1)
;	Cls
	If time>10
		rndx=Rnd(MAXX-2)+1
		rndy=Rnd(MAXY-2)+1
		WaveMap(CT,Rndx,Rndy)=256
		time=0
	EndIf
	time=time+1
	
	UpdateWaveMap()
	Flip
Wend
End

Function UpdateWaveMap()
	;Skip the edges To allow area sampling
	For y = 1 To MAXY-1
		For x = 1 To MAXX-1
			n# = ( WaveMap(CT,x-1,y)+WaveMap(CT,x+1,y)+WaveMap(CT,x,y-1)+WaveMap(CT,x,y+1) ) / 2-WaveMap(NW,x,y)
			n = n - (n * DAMP)
			WaveMap(NW,x,y) = n
		Next
	Next
	
	;Render
	For y = 1 To MAXY-1
		For x = 1 To MAXX-1
			col=(WaveMap(CT,x,y)-WaveMap(NW,x,y))+127
			Color col,col,col
			Plot x,y
		Next
	Next

	;Swap frames
	Temporary_Value = CT
	CT = NW
	NW = Temporary_Value
	
End Function
