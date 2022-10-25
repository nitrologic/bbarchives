; ID: 2555
; Author: SytzeZ
; Date: 2009-08-05 09:52:09
; Title: Game of Life
; Description: Blitz3D version of Conway's Game of Life

;My version of the Game of Life
;Keys:
;  Spacebar : Play/Pause
;  Left MB : Edit (while pausing)
;  Numpad +/- : Change Speed
;  S : Save
;  O : Open
;You can change the window size


Graphics3D 1200,800,16,2
AppTitle "Game Of Life"
SetBuffer BackBuffer()

Const Size=256
Dim Grid(Size-1,Size-1)

Type change
	Field x,y
End Type

Global Play=0
Global Spd=50

Cls
w=GraphicsWidth()/Size
h=GraphicsHeight()/Size
Color 127,127,127
For x=0 To Size-1
	Line 0,x*h,Size*w,x*h
	Line x*w,0,x*w,Size*h
	;For y=0 To Size-1
	;	Rect x*w,y*h,w+1,h+1,0
	;Next
Next

Repeat
	For x=1 To Size-2
		For y=1 To Size-2
			o=Grid(x,y)
			n=-o
			For xx=-1 To 1
				For yy=-1 To 1
					n=n+Grid(x+xx,y+yy)
				Next
			Next
			If Play Then
				If o=0 And n=3 Then
					o=1
				ElseIf o=1 And (n>3 Or n<2)
					o=0
				End If
			Else
				If RectsOverlap(MouseX(),MouseY(),1,1,x*w,y*h,w,h) Then
					If MouseHit(1) Then o=1-o
				End If
			EndIf
			If o<>Grid(x,y) Then
				Color 0,0,0
				Rect x*w,y*h,w+1,h+1,1
				Color 127+o*127,127+o*127,127+o*127
				Rect x*w,y*h,w+1,h+1,o
				ch.change=New change
				ch\x=x
				ch\y=y
			EndIf
		Next
	Next
	For ch.change=Each change
		Grid(ch\x,ch\y)=1-Grid(ch\x,ch\y)
		Delete ch
	Next
	Flip
	If Play Then
		Delay 1000/Spd
	EndIf
	Spd=Spd+KeyHit(78)-KeyHit(74)
	Spd=Spd+(Spd=0)
	If KeyHit(57) Then Play=1-Play
	If KeyHit(31) Then
		f=WriteFile("save.dat")
		For x=0 To Size-1
			For y=0 To Size-1
				If Grid(x,y) Then
					WriteByte f,x
					WriteByte f,y
				End If
			Next
		Next
		CloseFile f
	End If
	If KeyHit(24) Then
		f=ReadFile("save.dat")
		Repeat
			x=ReadByte(f)
			y=ReadByte(f)
			Grid(x,y)=1
			Color 255,255,255
			Rect x*w,y*h,w,h,1
		Until Eof(f)
		CloseFile f
	End If
Forever
