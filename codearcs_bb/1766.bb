; ID: 1766
; Author: big10p
; Date: 2006-07-28 11:20:44
; Title: GUI object management
; Description: Type demo for managing overlapping GUI objects

Graphics 500,500,0,2
	SetBuffer BackBuffer()
	SeedRnd MilliSecs()
	
	Type thingyT
		Field x%, y%
		Field width%, height%
		Field r%, g%, b%
	End Type
	
	; create some random 'thingies'.
	For n = 1 To 50
		this.thingyT = New thingyT
		this\width = Rand(30,100)
		this\height = Rand(30,100)
		this\x = Rand(0,500-this\width)
		this\y = Rand(0,500-this\height)
		this\r = Rand(20,255)
		this\g = Rand(20,255)
		this\b = Rand(20,255)
	Next
	
	While Not KeyHit(1)
		Cls
		If MouseHit(1) Then update_thingies()
		draw_thingies()
		Flip
	Wend
	
	End	

Function update_thingies()

	mx = MouseX()
	my = MouseY()
	
	this.thingyT = Last thingyT

	; We need to check through the thingy list backwards so that
	; a thingy overlapping another one is checked first.
	While this <> Null
		If (mx >= this\x) And (mx <= (this\x+this\width-1))	
			If (my >= this\y) And (my <= (this\y+this\height-1))	
				; This thingy has been clicked on so make it top thingy.
				Insert this After Last thingyT
				Return
			EndIf
		EndIf
		
		this = Before this
	Wend	

End Function

Function draw_thingies()

	For this.thingyT = Each thingyT
		Color this\r,this\g,this\b
		Rect this\x,this\y,this\width,this\height,True
		Color 255,255,255
		Rect this\x,this\y,this\width,this\height,False
	Next
	
End Function
