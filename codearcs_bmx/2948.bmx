; ID: 2948
; Author: Jur
; Date: 2012-06-04 02:21:53
; Title: Pixmap text panel
; Description: A proxy gadget panel with text on pixmap

SuperStrict
Import MaxGui.Drivers
Import MaxGUI.ProxyGadgets


'rem
'DEMO

Local window:TGadget = CreateWindow( "Window",100, 100, 250, 250, Null, WINDOW_TITLEBAR|WINDOW_CLIENTCOORDS|WINDOW_RESIZABLE )
Local group:TGadget = CreatePanel(10,10,200,200,window,PANEL_GROUP,"Group Label")


Local Font:TImageFont = TImageFont.CreateDefault()
Local fontHeight:Int = ImageFontHeight(Font)   'because "Height" method doesnt give correct height
Local PixmapTextPanel:TPixmapTextPanel = New TPixmapTextPanel.Make("Test", 10,30,70,fontHeight,group,PANEL_ACTIVE,Font)
SetGadgetColor(PixmapTextPanel, 255,255,0)


Repeat
	WaitEvent()
	Print CurrentEvent.ToString()
	Select EventID()
		Case EVENT_WINDOWCLOSE, EVENT_APPTERMINATE
			End
		Case EVENT_MOUSEDOWN
			If EventSource()=PixmapTextPanel Then 
				Print "PixmapTextPanel clicked"
			EndIf
			
	End Select
Forever

'endrem



Type TPixmapTextPanel Extends TProxyGadget
	Field Panel:TGadget
	Field text:String
	Field font:TImageFont
	Field R:Int,G:Int,B:Int   'text color
	
	
	Method Make:TPixmapTextPanel(_text:String, x:Int, y:Int, w:Int, h:Int, Group:TGadget, style:Int, _font:TImageFont)
		
		Panel = CreatePanel(x,y,w,h,Group,style)
		SetProxy(Panel)
		font = _font
		SetText(_text)

		Return Self
	EndMethod
	
	
	Method SetTextColor(_R:Int,_G:Int,_B:Int)
		R=_R ; G=_G ; B=_B
		SetGadgetPixmap(Panel, MakeTextPixmap())
	EndMethod
	
	
	Method SetText(_text:String)
		text = _text
		If ImageFontTextWidth(text,font)>GadgetWidth(Panel) Then
			text = ImageFontCutTextToWidth(text, GadgetWidth(Panel), font)
			text:+".."
		EndIf
		SetGadgetPixmap(Panel, MakeTextPixmap())
	EndMethod
	
	
	Method MakeTextPixmap:TPixmap()
	
		Local Pixmap:TPixmap = CreatePixmap(GadgetWidth(Panel), GadgetHeight(Panel), PF_RGBA8888)
		Pixmap.ClearPixels(0)
		
		Local xAdvance:Int
		For Local i:Int=0 Until text.Length
			Local n:Int = font.CharToGlyph(text[i])
			If n<0 Continue
			
			Local Glyph:TImageGlyph = font.LoadGlyph(n)
			If Glyph._image Then
			 	Local GlyphPixmap:TPixmap = Glyph._image.Pixmaps[0]
				If GlyphPixmap Then
					Local tx:Float = glyph._x
					Local ty:Float = glyph._y
					
					For Local iy:Int = 0 Until GlyphPixmap.height
						For Local ix:Int = 0 Until GlyphPixmap.width
							If xAdvance + ix + tx < Pixmap.width And iy + ty < Pixmap.Height Then
								Local BGRA:Int = ReadPixel(GlyphPixmap, ix, iy)
								Local A:Int = (BGRA Shr 24) & $FF
								BGRA = B | (G Shl 8) | (R Shl 16) | (A Shl 24)
								WritePixel(Pixmap, xAdvance + ix + tx, iy + ty, BGRA)
							EndIf
						Next
					Next
				EndIf
			EndIf
			xAdvance:+Glyph.Advance()
		Next
		
		Return Pixmap
	EndMethod
	
EndType



'some useful functions...

Function ImageFontHeight:Int(font:TImageFont)
	Local h:Int
	For Local ascii:Int=0 Until font._glyphs.length
		Local Glyph:TImageGlyph = font.LoadGlyph(ascii)
		If Glyph Then
			h=Max(h, Glyph._y+Glyph._h)
		EndIf
	Next
	Return h
EndFunction

Function ImageFontTextWidth:Int(text:String, Font:TImageFont)
	Local width:Int = 0
	For Local n:Int = 0 Until text.Length
		Local I:Int = font.CharToGlyph(Text[n])
		If I < 0 Continue
		width:+font.LoadGlyph(I).Advance()
	Next
	Return width
End Function

Function ImageFontCutTextToWidth:String(text:String, widthMax:Int, Font:TImageFont)
	Local width:Int = 0
	For Local n:Int = 0 Until text.Length
		Local i:Int = font.CharToGlyph(Text[n])
		If i < 0 Continue
		width:+font.LoadGlyph(I).Advance()
		If width>widthMax Then
			Return text[..n-1]
		EndIf
	Next
	Return text
End Function
