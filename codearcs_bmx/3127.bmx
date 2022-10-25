; ID: 3127
; Author: zoqfotpik
; Date: 2014-06-04 00:24:35
; Title: Cellular Automaton Explorer
; Description: Cellular Automaton Explorer

Global map:Int[2000,2000]
Global newmap:Int[2000,2000]
Global lastdied:Int[2000,2000]
Graphics 1000,1000
Global size = 200
Global cellsize = 1000/size
Global low = 25
Global high = 35
Global colorscheme:Int ' 0 raw 1 age 2 greyscaleage
Global nextstate:Int[52,2500]
Global r[50]
Global b[50]
Global g[50]
Global currentwheel:Int
Global stack:coordstack = New coordstack
Global alivelist:TList = New TList
Global x%
Global y%
fps_milli=MilliSecs()
fps_counter=0
update_frequency=10
' for each cell, add up the surrounding 8 cells into neighbors
' next state = nextstate[currentcelltype,neighbors]

Function init()
	Print "entering initmap"
	initmap()
	
	initrules()
	Print "initmap done"
	initcolors()
End Function

Function initmap()

For i = 1 To size
	For j = 1 To size
	map[i,j]=Rand(50)
	c:coord = New coord
	c.x = i
	c.y = j
	alivelist.addlast(c)
	Next
	Next
End Function

Function initrules()
For i = 0 To 50
	For j = 0 To 2499
	nextstate[i,j]=Rand(50)
	Next
	Next
End Function
Function initcolors()
For i = 1 To 49
	r[i]=Rand(255)
	g[i]=Rand(255)
	b[i]=Rand(255)
	Next
End Function

init()

Function processlist()
Print alivelist.count()
cell:coord = New coord
Local newstate:Int
	'Print alivelist.count()
	For cell = EachIn alivelist
		neighbors = 0
		'Print cell.x+","+ cell.y
		For x = cell.x-1 To cell.x+1
		For y = cell.y-1 To cell.y+1
		'Print ".."+x+","+y
			If x <> cell.x And y <> cell.y neighbors = neighbors + map[x,y]
			'Print "break1"
		Next
		Next
		'Print "break2"
		'Print getmap(cell.x,cell.y)
		'Print neighbors
		newstate=nextstate[getmap(cell.x,cell.y),neighbors]
		'Print "break3"
		newmap[cell.x,cell.y]=newstate
		'Print "break4"
		If newstate = 0 Or newstate < low Or newstate > high alivelist.remove(cell)
	
	Next
End Function

Function getmap(mx:Int,my:Int)
	If mx>0 And mx<2000 And my > 0 And my<2000 
		Return map[mx,my]
	Else
		Return 0
	EndIf
End Function

Function processmap()
For i = 1 To size-1
	For j = 1 To size-1
		neighbors = 0
		aliveneighbors = 0
		For x% = i-1 To i+1
		For y% = j-1 To j+1
			If x <> i And y <> j neighbors = neighbors + map[x,y]
		Next
		Next
		newmap[i,j]=nextstate[map[i,j],neighbors]
	Next
	Next
End Function

Function copyandplot()
For i = 1 To size-1
	For j = 1 To size-1
	nc = newmap[i,j]
	If nc < low  Or nc > high 
		nc = 0
		lastdied[i,j]=ticks
	EndIf
	
	map[i,j]=nc
	
	 
		setcolorbycellnum(ticks-lastdied[i,j]-1) 
		'DrawRect i*cellsize,j*cellsize,cellsize-1,cellsize-1
		Plot i,j
		
	Next
	Next
End Function

While Not KeyDown(KEY_ESCAPE)
	Cls
	' fps counter
	fps_counter=fps_counter+1
	If fps_counter=update_frequency
     fps=1000/Float(((MilliSecs()-fps_milli))/update_frequency)
     fps_milli=MilliSecs()
     fps_counter=0
' Print fps
	Print "FPS:"+fps
     EndIf



	ticks = ticks + 1
	processmap()
	For i = 1 To size-1
	For j = 1 To size-1
	nc = newmap[i,j]
	If nc < low  Or nc > high 
		nc = 0
		lastdied[i,j]=ticks
	EndIf
	
	map[i,j]=nc
	
	 
		setcolorbycellnum(ticks-lastdied[i,j]-1) 
		DrawRect i*cellsize,j*cellsize,cellsize-1,cellsize-1
		'Plot i,j
		
	Next
	Next
	If MouseDown(1)
		low = MouseX()/20
		high = MouseY()/20
	EndIf
	
	If KeyHit(KEY_q) 
		low = low + 1 
		Printparams()
	EndIf 
	If KeyHit(KEY_a) 
		low = low - 1
		Printparams()
	EndIf
	If KeyHit(key_w) 
		high = high + 1
		Printparams()
	EndIf
	If KeyHit(KEY_s) 
		high = high - 1
		Printparams()
	EndIf
	If KeyHit(KEY_E)
		size = size+1
		cellsize=1000/size
	EndIf
	If KeyHit(KEY_D)
		size = size -1
		cellsize = 1000/size
	EndIf
	If high > 49 high = 49
	If high < low high = low
	If low > high low = high
	If low < 1 low = 1 
	If KeyDown(KEY_SPACE) initrules()
	If KeyDown(KEY_M) initmap()
	If KeyDown(KEY_C) initcolors()
	If KeyDown(KEY_R) printruleset()
	If MouseZ() < currentwheel
		size = size - 1
		'currentwheel = currentwheel - 1
		'low = low - 1
		'high = high -1
		'printparams()
	EndIf
	
	If MouseZ() > currentwheel
		size = size + 1
		'currentwheel = currentwheel + 1
		'low = low + 1
		'high = high + 1
		'printparams()
	EndIf
	
	 If KeyHit(KEY_LEFT)
	low = low - 1
		high = high -1
		printparams()
	EndIf
	
	If KeyHit(KEY_RIGHT)
	low = low + 1
		high = high +1
		printparams()
	EndIf
		
	
Flip
'Print "low:" + low
'Print "high:" + high
Wend
End

Function printruleset()
	For i = 0 To 50
	For j = 0 To 2500
	Print nextstate[i,j]
	Next
	Next
End Function

Function printparams()
Print "----------------"
Print "Low:"+low
Print "High:"+high
End Function

Function setcolorbycellnum(cellnum:Int)
'If cellnum > 25 SetColor 255,255,255
'If cellnum < 25 SetColor 0,0,0
'Return

'SetColor 255-cellnum*5,255-cellnum*5,255-cellnum*5
'Return
If cellnum < 0 cellnum = 0
If cellnum > 49 cellnum = 49
SetColor r[cellnum],g[cellnum],b[cellnum]
Return
End Function

Type coord
Field x:Int
Field y:Int
End Type

Type coordstack
Field data:coord[4000000]
Field head:Int = 0
Method push(mycoord:coord)
	head = head + 1
	data[head]=mycoord
End Method 
Method pop:coord()
	If head < 0 Print "STACK UNDERFLOW"
	a:coord = data[head]
	head = head - 1
	Return a
End Method 
End Type
