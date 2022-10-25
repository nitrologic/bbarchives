; ID: 455
; Author: skn3[ac]
; Date: 2002-10-10 03:33:09
; Title: Any Zoom Level Line
; Description: Draw a line at any zoom level..

Graphics 640,480,32,2
Global angle#=0

	While KeyDown(1)=False
		SetBuffer BackBuffer()
		Cls
		Color 255,255,255
		drawline(200,200,MouseX(),MouseY(),33)
		Flip
	Wend

Function DrawLine(StartX,StartY,EndX,EndY,Scale)
	StartX=StartX/Scale*Scale
	StartY=StartY/Scale*Scale
	EndX=EndX/Scale*Scale
	EndY=EndY/Scale*Scale
	If StartX<EndX Then
		Xmode=True
	Else
		Xmode=False
	End If
	If StartY<EndY Then
		Ymode=True
	Else
		Ymode=False
	End If
	width#=Abs(startX-EndX)
	height#=Abs(StartY-EndY)
		
	If width#>height# Then
		loops=width#
		If XMode=False Then
			xstep#=-1
		Else
			xstep#=1
		End If
		If Ymode=False Then
			ystep#=-(height#/width#)
		Else
			ystep#=(height#/width#)
		End If
	Else
		loops=height#
		If XMode=False Then
			xstep#=-(width#/height#)
		Else
			xstep#=(width#/height#)
		End If
		If Ymode=False Then
			ystep#=-1
		Else
			ystep#=1
		End If
	End If
		
	drawX#=startX
	drawY#=startY
	
	loops=Ceil(loops/scale)+1
	
	For i=1 To loops
		getX#=drawX#/scale
		getY#=drawY#/scale
		intX=Int(getX#)*scale
		intY=Int(GetY#)*scale
		
		Rect IntX,IntY,scale,scale
		drawX#=drawX#+(xstep#*scale)
		drawY#=drawY#+(ystep#*scale)
	Next
End Function
