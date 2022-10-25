; ID: 3128
; Author: zoqfotpik
; Date: 2014-06-04 00:34:19
; Title: 1D Cellular Automaton Explorer
; Description: Generates 1D cellular automaton images

' 2d ca

Global states[100000]
Global array[1000]
Global nextarray[1000]
Global neighborradius%=1
Global r%[50]
Global g%[50]
Global b%[50]
Global low%
Global high%
Global cellsize = 2
Global celltypes = 10
Global screenarray[1000,1000]
Global drawstripheight%=10
Global scrollwindow% = 1000
Function initcolors()
For i = 1 To 49
	r[i]=Rand(255)
	g[i]=Rand(255)
	b[i]=Rand(255)
	Next
End Function

Function initarray()
	'For i = 0 To 999
	'array[i]=Rand(49)
	'Next
	
	'For i = 450 To 550
		'array[i]=Rand(49)
	'Next
	
	For i = 0 To 999
		array[i]=Rand(celltypes)
	Next
	
	'array[500]=Rand(49)
End Function

Function initstates()
	For i = 0 To 99999
	states[i]=Rand(celltypes)-1
	Next
	states[0]=0
	low = Rand(neighborradius*50)
	high = low + Rand(neighborradius*50)
End Function

Function doarray()
	For j = 0 To 1000/cellsize
		For i = 0 To 999/cellsize
			Local neighbors = 0
			For x = i-neighborradius To i+neighborradius
				absx = x
				If absx<0 absx = absx+1000/cellsize
				If absx>999/cellsize absx = absx-1000/cellsize
				neighbors = neighbors + array[absx]
			Next
			nextcell = states[neighbors]
			If neighbors < low Or neighbors > high nextcell = 0
			nextarray[i]=states[neighbors]
			'SetColor r[states[neighbors]],g[states[neighbors]],b[states[neighbors]]
			'DrawRect i*cellsize,j*cellsize,cellsize,cellsize

		Next
		For i = 0 To 999/cellsize
			array[i]=nextarray[i]
			screenarray[i,j]=nextarray[i]
		Next
		'If j Mod 40 = 1 Flip
		'states[Rand(celltypes)]=Rand(celltypes)
	Next
	'drawscreen()

End Function

Graphics 1000,1000
Cls
initcolors()
initarray()
initstates()
doarray()
Flip
While Not KeyDown(KEY_ESCAPE)
	If KeyHit(KEY_SPACE)
		Cls
		initarray()
		doarray()
		Flip
	EndIf
	
	If KeyHit(KEY_R)
		initstates()
		initarray()
		doarray()
		Flip
	EndIf
	
	If KeyHit(KEY_C)
		initcolors()
		drawscreen()
		Flip
	EndIf
	
	drawscreen
	Flip
	scrollwindow:+1
	If scrollwindow > 999 scrollwindow = 0
Wend

Function drawscrolledscreen()
	Local y = 1000/cellsize
	For j = scrollwindow To 0 Step -1
	drawcellline(j,y*cellsize)
	y=y-1
	Flip
	Next
End Function


Function drawscreen()
	For j = 1 To 999
	'For i = 0 To 999/cellsize
	'	SetColor r[screenarray[i,j]],g[screenarray[i,j]],b[screenarray[i,j]]
	'	DrawRect i*cellsize,(j*cellsize)-cellsize,cellsize,cellsize
	'Next
	DrawcellLine(j,j*cellsize)
	Next
End Function
	


Function DrawcellLine(j,screeny)
	For i = 0 To 999/cellsize
		SetColor r[screenarray[i,j]],g[screenarray[i,j]],b[screenarray[i,j]]
		DrawRect i*cellsize,screeny,cellsize,cellsize
	Next
End Function
