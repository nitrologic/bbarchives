; ID: 2850
; Author: Jimmy
; Date: 2011-05-13 02:56:57
; Title: Accessing of Wacom Pen (Legacy serial)
; Description: Direct Access of Wacom pen using their RS232 protocol

; Access a Wacom pen, on legacy models that uses serial cable
; Reads x position, y position, and pressure
; And testing it with some simple tiles or lines.
; Uses the portio library dll (pasted in used bits directly in source instead of including)

Dim i(7)
Global penx,peny,penpress,hovering
Global RegsIn	= CreateBank(255)
Global RegsOut	= CreateBank(255)
Function DlPortWritePortUchar%( port%, value% )
DebugLog "DlPortWritePortUchar " + port%
DebugLog "DlPortWritePortUchar " + value%
PokeInt( RegsIn, 0, port% )
PokeInt( RegsIn, 4, value% )
Return CallDLL("DIO","_DlPortWritePortUchar", RegsIn, RegsOut )
End Function
Function DlPortReadPortUchar%( port% )
DebugLog "DlPortReadPortUchar" + port%
PokeInt( RegsIn, 0, port% )
Return CallDLL("DIO","_DlPortReadPortUchar", RegsIn, RegsOut )
End Function
Global	COM 	    = 1016
Global	THR			= 0
Global	RBR			= 0
Global	DLL			= 0
Global	DLH			= 1
Global	IER			= 1
Global	FCR			= 2
Global	LCR			= 3
Global	MCR			= 4
Global	LSR			= 5
Global	MSR			= 6
Global	SCR			= 7
; DlPortWritePortUchar( COM + 1, 0)
DlPortWritePortUchar( COM + 3, 128); DLAB
DlPortWritePortUchar( COM + 0, 12) ; 9600 baud
DlPortWritePortUchar( COM + 1, 0)  ; baud
DlPortWritePortUchar( COM + 3, 3)  ; bits
DlPortWritePortUchar( COM + 2, $c7); FIFO, 07 / 0
Dlportwriteportuchar( COM + 4 ,11) ; , Turn on DTR, RTS, And OUT
Graphics 800,600,16
SetFont LoadFont("blitz")
layer=CreateImage(800,600)
LoadBuffer (BackBuffer(),"Any16x16tiles.png")
SetBuffer BackBuffer()
Global gfx=CreateImage(16,16,64)
For a=0 To 63
GrabImage(gfx,a Shl 4,0,a)
Next
Dim map(50,38)
createlevel()
tool=2
; Main
Repeat
Repeat:e=DlPortReadPortUchar( COM + LSR ):Until e And 32
DlPortWritePortUchar( COM + 0,Asc("@"))
Repeat:e=DlPortReadPortUchar( COM + LSR ):Until e And 1
i(0)=DlPortReadPortUchar( COM + RBR )
Repeat:e=DlPortReadPortUchar( COM + LSR ):Until e And 1
i(1)=DlPortReadPortUchar( COM + RBR )
Repeat:e=DlPortReadPortUchar( COM + LSR ):Until e And 1
i(2)=DlPortReadPortUchar( COM + RBR )
Repeat:e=DlPortReadPortUchar( COM + LSR ):Until e And 1
i(3)=DlPortReadPortUchar( COM + RBR )
Repeat:e=DlPortReadPortUchar( COM + LSR ):Until e And 1
i(4)=DlPortReadPortUchar( COM + RBR )
Repeat:e=DlPortReadPortUchar( COM + LSR ):Until e And 1
i(5)=DlPortReadPortUchar( COM + RBR )
Repeat:e=DlPortReadPortUchar( COM + LSR ):Until e And 1
i(6)=DlPortReadPortUchar( COM + RBR )
penx=(i(2)+(i(1) Shl 7))/6
peny=(i(5)+i(4) Shl 7)/6
penpress=i(6) And 127 Xor %01000000 
hovering=i(0) And 64
If Not hovering Then penpress=0
; test
For h=0 To 37
For w=0 To 49
DrawBlock gfx,w Shl 4,h Shl 4,map(w,h)
Next
Next

Color 255,penpress,255
tempxmo=penx Shr 4:tempymo=peny Shr 4
tile=map(tempxmo,tempymo)
; Sense tools
If mode=0 And press=0 And penpress>0 And tile=0 Then tool=1:press=1
If mode=0 And press=0 And penpress>0 And tile=2 Then tool=2:press=1
If mode=0 And press=0 And penpress>0 And tile=9 Then Color 255,0,0:press=1
If mode=0 And press=0 And penpress>0 And tile=10 Then Color 0,255,0:press=1
If mode=0 And press<>0 And penpress=0 Then press=0
If mode=0 And press<>0 Then Goto notdraw

If penpress>0 And mode=0 Then mode=1:x1=penx:y1=peny
If penpress=0 And mode=1 Then mode=2:x3=penx:y3=peny
If penpress>0 And mode=2 Then mode=3:y2=penx:y2=peny
If penpress=0 And mode=-1 Then mode=0
If (tool=1 Or tool=2) And mode=1 Then Line x1,y1,penx,peny
If tool=2 And mode=2 Then x2=penx:y2=peny:Line x1,y1,x2,y2:Line x2,y2,x3,y3 ; drawing two lines in a triangle like fashion

SetBuffer ImageBuffer(layer)
If tool=2 And mode=3 Then bezierspline(x1,y1,x2,y2,x3,y3):mode=-1
If tool=1 And mode=2 Then Line (x1,y1,x3,y3):mode=-1

.notdraw:

SetBuffer BackBuffer()
DrawImage layer,0,0
DrawImage gfx,penx,peny,14
Plot penx,peny
Flip
; If KeyHit(1) Then SaveBuffer(FrontBuffer(),"screenshot.bmp"): End
Until MouseDown(2)

Function createlevel()
For a=0 To 37
For b=0 To 49
map(b,a)=15
Next
Next
map(0,0)=0
map(1,0)=2
map(2,0)=9
map(3,0)=10
End Function
