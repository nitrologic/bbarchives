; ID: 2797
; Author: zambani
; Date: 2010-12-12 15:39:51
; Title: Text Shaper
; Description: Displays text to conform to the shape on an image.

SuperStrict
Graphics(800, 600, 0, 30)


'SetBlend(ALPHABLEND)
Local imagePath:String = "shape1.png"	'path to shaper image. Text will conform to location where the alpha > 0
Local txt:String						'Text to shape
txt = "We've got a few SIRIUS struggling to survive in one tank. Unfortunately for then, in the other tank is a thriving  colony of IRIDESA. We need to eradicate the IRIDESA so that the SIRIUS colony."
'txt = LoadString("Sample.txt")

Global st:objTextShaper = objTextShaper.createNew(txt, LoadImage(imagePath))

While Not KeyHit(KEY_ESCAPE) And Not AppTerminate()
	Cls()
	st.render()		'Draw textshaper
	Flip(-1)
Wend

'========================================= TEXT SHAPER ============================================

Type objTextShaper		'renders a text  base on the alpha shape of  an image
	Field _line:Int[]
	Field _text:String
	Field _font:TImageFont
	Field _lineHeight:Int
	Field _worldsArray:String[]
	Field _linesArray:String[]
	Field _startIndex:Int
	Field _lineIndex:Int = 1
	Field _yStart:Int
	Field _skip:Int
	Field xOffset:Int
	Field yOffset:Int
	
	
	Function createNew:objTextShaper(t:String, img:TImage, font:TImageFont = Null)
		'position of text is specified in the render method
		Local ts:objTextShaper = New objTextShaper
		ts._text = t
		ts._font = font
		Local tt:TImageFont
		If ts._font <> Null
			tt:TImageFont = GetImageFont() 'save current font
			SetImageFont(ts._font)
		End If
		ts._lineHeight = TextHeight(ts._text)
		ts._worldsArray = ts._text.Split(" ")
		ts._line = ts._getLinesLimit(img)
	
		ts.SetText(ts._text)
		If ts._font <> Null Then SetImageFont(tt)		'restore current font	
		Return ts
	End Function
	
	Method SetText(t:String)
		_text = t.Replace("~n", " ~n ")	'create space between lines
		Local ss:String
		_worldsArray = _text.Split(" ")
		While _startIndex < _worldsArray.Length
			_linesArray = _linesArray[.._lineIndex]
			ss = _getFullLine()
			_linesArray[_lineIndex - 2] = ss
			
			'Skip additional lines if required
			_lineIndex:+_skip
			If _skip > 0 Then _linesArray = _linesArray[.._lineIndex]
			
			If _lineIndex >= (_line.Length / 2) Then Exit		'end of image
		Wend		
	End Method
	
	Method _getLinesLimit:Int[] (i:TImage)		'returns  an array of line limis based on image i
		Local pix:TPixmap = LockImage(i)
		Local x:Int, y:Int, ar:Int[], p1:Int = 1, p2:Int = 1
		Local st:Int = False
		
		For Local cy:Int = 0 Until pix.Height
			If st = 1		'Force shut any line that started but not completed
				ar = ar[..(ar.length + 1)]
				ar[ar.length - 1] = pix.width
				st = 0
			End If
			For Local cx:Int = 0 Until pix.width
				p1 = pix.ReadPixel(cx, cy) Shr 24
				If p1 > 0 Then p1 = 1
				If p1 <> p2
					If st = 0
						ar = ar[..(ar.length + 1)]
						ar[ar.length - 1] = cx
						st = 1
						If _yStart = 0 Then _yStart = cy-_lineHeight
					Else
						ar = ar[..(ar.length + 1)]
						ar[ar.length - 1] = cx
						st = 0
						Exit
					End If
					p2 = p1
				End If
			Next
			p1 = 1
			p2 = 1
			cy:+(_lineHeight - 1)
		Next
		Return ar
	End Method
	
	Method _getFullLine:String()
		Local l:String
		Local l2:String
		Local a:Int
		Local cr:Int, cro:Int, ex:Int = False
		_skip = -1
		For a:Int = _startIndex Until _worldsArray.Length
			l:+_worldsArray[a] + " "
			If TextWidth(l) > (_line[_lineIndex * 2 + 1] - _line[_lineIndex * 2]) Then Exit
			cr = _worldsArray[a].find("~n", cro)
			While cr <> - 1
				ex = True
				cro = cr + 1
				_skip:+1
				cr = _worldsArray[a].find("~n", cro)
			Wend			
			If ex
				a:+1
				Exit
			End If
			l2 = l
		Next
		_skip = Max(0, _skip)
		_startIndex = a
		_lineIndex:+1
		Return l2
		End
	End Method
	
	Method render(xo:Float = 0, yo:Float = 0)
		If _font <> Null Then SetImageFont(_font)
		For Local c:Int = 0 Until _linesArray.Length
			DrawText(_linesArray[c], xo + _line[c * 2]+xOffset, (_lineHeight * c) + yo+_yStart+yOffset)
		Next
	End Method
End Type
