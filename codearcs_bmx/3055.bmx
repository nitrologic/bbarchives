; ID: 3055
; Author: Luke111
; Date: 2013-05-30 23:57:25
; Title: Simple Progress Bars
; Description: Simple Progress Bars in Max2D

Rem
	This is a really simple progress bar I made for my WIP game.
	I created and tested it in about 30 minutes.
	I hope someone finds it useful!
End Rem

Type SimpleProgressBar
	Field minValue:Short, maxValue:Short, currentValue:Short 'these are shorts to make it a bit easier since they don't support negatives...
	Field colorR:Byte,colorG:Byte,colorB:Byte 'background color for the progress bar.
	Field x:Float,y:Float,w:Float,h:Float '(x,y) coordinates for the bar, followed by width and height of it.
	Field text:String = "" 'the text to place in the middle of the filled bar.
	
	Method Create:SimpleProgressBar(_x:Float,_y:Float,_w:Float,_h:Float,_minValue:Short,_maxValue:Short,_text:String="",_colorR:Byte=0,_colorG:Byte=255,_colorB:Byte=0)
		x = _x
		y = _y
		w = _w
		h = _h
		text = _text
		minValue = _minValue
		currentValue = minValue 'the current value defaults to the minimum value...
		maxValue = _maxValue
		colorR = _colorR
		colorG = _colorG
		colorB = _colorB
		Return Self
	End Method
	
	'method to set the values of the progress and the text as well.
	Method SetValue(value:Short,_text:String = Null)
		'only set the new value if it is in range.
		If value >= minValue And value <= maxValue Then
			currentValue = value
		EndIf
		'set the text if specified.
		If _text <> Null Then
			text = _text
		EndIf
	End Method
	
	'call this during the rendering part of the main loop.
	Method Render()
		'get current color
		Local tempR:Int,tempG:Int,tempB:Int
		GetColor(tempR,tempG,tempB)
		
		'get opposite color (used for non-filled background as well as overlayed text)
		Local oppositeColorR:Byte,oppositeColorG:Byte,oppositeColorB:Byte
		If colorR > 64 Then
			oppositeColorR = 0
		Else
			oppositeColorR = 255
		EndIf
		If colorG > 64 Then
			oppositeColorG = 0
		Else
			oppositeColorG = 255
		EndIf
		If colorB > 64 Then
			oppositeColorB = 0
		Else
			oppositeColorB = 255
		EndIf
		
		'non-filled background...
		SetColor oppositeColorR,oppositeColorG,oppositeColorB
		DrawRect x,y,w,h
		
		'filled background...
		SetColor colorR,colorG,colorB
		'finds and rounds the progress percent
		Local filledPercent:Int = (currentValue - minValue) * 100 / (maxValue - minValue)
		'finds and rounds the width of the filled part.
		Local filledWidth:Int = filledPercent * w / 100
		DrawRect x,y,filledWidth,h 'draw the filled part!
		
		'overlayed text...
		If text <> "" And TextWidth(text) < filledWidth Then 'only draw it if it can fit in the filled part.
			SetColor oppositeColorR,oppositeColorG,oppositeColorB
			DrawText text,x + ((filledWidth / 2) - (TextWidth(text) / 2)),y 'draw the text in the middle of the filled part (horizontally)
		EndIf
		
		SetColor tempR,tempG,tempB
	End Method
End Type

'****DEMO****
Graphics 800,600,0 'windowed

'create two progress bars, one on the top, and one on the bottom.
Local top:SimpleProgressBar = New SimpleProgressBar.Create(200,25,400,50,0,100,"0") 'this one is the default green, and goes from 0 to 100.
Local bottom:SimpleProgressBar = New SimpleProgressBar.Create(200,100,400,50,0,500,"0",255,0,0) 'this one is red, and goes from 0 to 500.

Local startTime:Int = MilliSecs() 'used to help with the timing of the progress in the progress bars...

While Not KeyHit(KEY_ESCAPE)
	If MilliSecs() - startTime > 50 Then 'update every 50 milliseconds..
		startTime = MilliSecs()
		If top.currentValue >= top.maxValue Then
			top.SetValue(top.minValue,String(top.minValue))
		Else
			top.SetValue(top.currentValue + 1,String(top.currentValue + 1))
		EndIf
		If bottom.currentValue >= bottom.maxValue Then
			bottom.SetValue(bottom.minValue,String(bottom.minValue))
		Else
			bottom.SetValue(bottom.currentValue + 1,String(bottom.currentValue + 1))
		EndIf
	EndIf
	Cls()
	'render the bars...
	top.Render()
	bottom.Render()
	Flip()
Wend
