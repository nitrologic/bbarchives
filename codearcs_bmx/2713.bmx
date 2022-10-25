; ID: 2713
; Author: Oddball
; Date: 2010-05-11 12:05:51
; Title: Basic ASCII text module
; Description: Module for drawing text using a fixed width, ASCII ordered image font.

'Ascii bitmap font rendering
SuperStrict

Rem
bbdoc: Gaphics/AsciiFont
End Rem
Module ODD.AsciiFont

ModuleInfo "Version: 1.3"
ModuleInfo "Author: David Williamson"
ModuleInfo "License: Public Domain"

ModuleInfo "History: 1.3"
ModuleInfo "       : Draw now recognises the newline escape character and Null strings."
ModuleInfo "History: 1.2"
ModuleInfo "       : Added block text and line-spacing."
ModuleInfo "History: 1.1"
ModuleInfo "       : Added letter-spacing."
ModuleInfo "History: 1.0"
ModuleInfo "       : Initial release."

Import BRL.Max2D

Const ASCII_PADDED:Int=16
Const ASCII_EXTENDED:Int=32

Const ALIGN_LEFT:Int=-1
Const ALIGN_RIGHT:Int=1
Const ALIGN_TOP:Int=-1
Const ALIGN_BOTTOM:Int=1
Const ALIGN_CENTER:Int=0

Type TAsciiFont
	Field img:TImage, _width:Int, _height:Int, _letterSpacing:Int, _lineSpacing:Int
	Field frontPad:Int, extended:Int
	
	Rem
	bbdoc: Loads an ascii font from an image file
	returns: An ascii font object
	about: Loads a fixed width ascii ordered font for rendering.
	
	@flags are the standard #LoadImage flags with two ascii font specific additions
	[ @ASCII_PADDED | The image contains the first 32 unprintable characters
	* @ASCII_EXTENDED | The image contains the extended ascii characters(128-255)
	]
	End Rem
	Function Load:TAsciiFont( url:Object, width:Int, height:Int, flags:Int=0 )
		Local af:TAsciiFont=New TAsciiFont
		af.frontPad=flags&ASCII_PADDED
		af.extended=flags&ASCII_EXTENDED
		Local fCount:Int=96
		If af.frontPad Then fCount:+32
		If af.extended Then fCount:+128
		af.img=LoadAnimImage(url,width,height,0,fCount,flags)
		af._width=width;af._height=height;af._letterSpacing=0;af._lineSpacing=0
		If af.img Then Return af Else Return Null
	End Function
	
	Rem
	bbdoc: Sets the font's letter-spacing and line-spacing
	about: Sets the letter-spacing and line-spacing of the font. A negative spacing will cause the letters to overlap.
	EndRem
	Method SetSpacing( letterSpacing:Int, lineSpacing:Int=0 )
		_letterSpacing=letterSpacing
		_lineSpacing=lineSpacing
	End Method
	
	Rem
	bbdoc: Draws text on the screen
	about: Draws text at position @x, @y. @horAlign and @vertAlign discribes how the text is aligned. The values are @ALIGN_LEFT, @ALIGN_RIGHT, @ALIGN_TOP, @ALIGN_BOTTOM, @ALIGN_CENTER.
	
	If a width, @w, is specified then the text exeeding that value will be displayed over multiple lines, otherwise text is displayed on a single line.
	
	Draw ignores the current Max2D rotation.
	EndRem
	Method Draw( text:String, x:Float, y:Float, horAlign:Int=ALIGN_LEFT, vertAlign:Int=ALIGN_TOP, w:Int=0 )
		If text="" Then Return
		If text.Contains("~n")
			Local blocks:String[]=text.Split("~n")
			Local xScale:Float, yScale:Float
			GetScale xScale, yScale
			Local h:Int[]=New Int[blocks.length]
			Local align:Float
			For Local count:Int=0 Until blocks.length
				h[count]=height(blocks[count],w)
				align:+(h[count]+_lineSpacing)*yScale
			Next
			align:-_lineSpacing*yScale
			align:*.5
			y:-(align+align*vertAlign)
			For Local count:Int=0 Until blocks.length
				Draw blocks[count],x,y,horAlign,vertAlign,w
				y:+h[count]*yScale
			Next
		ElseIf w>0
			Local lineWidth:Int
			lineWidth=Floor(w/Float(_width+_letterSpacing))
			If lineWidth<2 Then Return
			Local words:String[]=text.Split(" ")
			Local lines:String[]=New String[text.length]
			Local lIndex:Int=0
			Local wIndex:Int=0
			While wIndex<words.length
				lines[lIndex]=words[wIndex]
				While lines[lIndex].length>lineWidth
					lines[lIndex+1]=lines[lIndex][lineWidth-1..]
					lines[lIndex]=lines[lIndex][..lineWidth-1]+"-"
					lIndex:+1
				Wend
				wIndex:+1
				While wIndex<words.length And lines[lIndex].length+words[wIndex].length+1<lineWidth
					lines[lIndex]:+" "+words[wIndex]
					wIndex:+1
				Wend
				lIndex:+1
			Wend
			
			Local xScale:Float, yScale:Float
			GetScale xScale, yScale
			Local align:Float=(((_height+_lineSpacing)*lIndex)-_lineSpacing)*.5*yScale
			y:-(align+align*vertAlign)
			For Local count:Int=0 To lIndex
				Draw lines[count],x,y+(count*(_height+_lineSpacing)*yScale),horAlign
			Next
		Else
			Local rot:Float=GetRotation()
			Local xScale:Float, yScale:Float
			GetScale xScale, yScale
			Local fOffset:Int=0
			If Not frontPad Then fOffset=-32
			Local a:Int
			Local align:Float=Width(text)*.5*xScale
			x:-(align+align*horAlign)
			align=_height*.5*yScale
			y:-(align+align*vertAlign)
			For Local count:Int=0 Until text.length
				a=text[count]
				If Not(extended) And a>=128 Then Continue
				DrawImage img,x+(count*(_width+_letterSpacing)*xScale),y,a+fOffset
			Next
			SetRotation rot
		EndIf
	End Method
	
	Rem
	bbdoc: Gets the total width of text
	returns: The pixel width of text
	about: If @text is blank then #Width returns the width of a single character.
	EndRem
	Method Width:Int( text:String="" )
		If text Then Return ((_width+_letterSpacing)*text.length)-_letterSpacing Else Return _width
	End Method
	
	Rem
	bbdoc: Gets the total height of text
	returns: The pixel height of text
	about: If @text is blank or @w=0 then #Height returns the height of a single character.
	EndRem
	Method Height:Int( text:String="", w:Int=0 )
		If text="" Or w=0 Then Return _height
		
		If text.Contains("~n")
			Local blocks:String[]=text.Split("~n")
			Local h:Int
			For Local count:Int=0 Until blocks.length
				height(blocks[count],w)
				h:+height(blocks[count],w)+_lineSpacing
			Next
			Return h-_lineSpacing
		Else
			Local lineWidth:Int
			lineWidth=Floor(w/Float(_width+_letterSpacing))
			If lineWidth<2 Then Return 0
			Local words:String[]=text.Split(" ")
			Local lines:String[]=New String[text.length]
			Local lIndex:Int=0
			Local wIndex:Int=0
			While wIndex<words.length
				lines[lIndex]=words[wIndex]
				While lines[lIndex].length>lineWidth
					lines[lIndex+1]=lines[lIndex][lineWidth-1..]
					lines[lIndex]=lines[lIndex][..lineWidth-1]+"-"
					lIndex:+1
				Wend
				wIndex:+1
				While wIndex<words.length And lines[lIndex].length+words[wIndex].length+1<lineWidth
					lines[lIndex]:+" "+words[wIndex]
					wIndex:+1
				Wend
				lIndex:+1
			Wend
			Return ((_height+_lineSpacing)*lIndex)-_lineSpacing
		EndIf
	End Method
End Type

Rem
bbdoc: Loads an ascii font from an image file
returns: An ascii font object
about: Loads a fixed width ascii ordered font for rendering.

@flags are the standard #LoadImage flags with two ascii font specific additions
[ @ASCII_PADDED | The image contains the first 32 unprintable characters
* @ASCII_EXTENDED | The image contains the extended ascii characters(128-255)
]
End Rem
Function LoadAsciiFont:TAsciiFont( url:Object, width:Int, height:Int, flags:Int=0 )
	Return TAsciiFont.Load(url,width,height,flags)
End Function
