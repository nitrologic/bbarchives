; ID: 2736
; Author: Jesse
; Date: 2010-06-26 16:53:57
; Title: fixedmap font
; Description: display bmax fonts as bitmap font

Type TBitMap
	Field image		:TImage
	Field gx			:Int
	Field gy			:Int
	Field advance		:Int
	Function Create:TBitMap(image:TImage,x:Float,y:Float,adv:Int,width:Int,height:Int)
		
		If width  = 0 Return Null 
		If height = 0  Return Null
		Local b:TbitMap = New TBitMap
		If image = Null
			b.gx = x
			b.gy = y
			b.advance = adv
			Return b	
		EndIf
		
		
		Local from:TPixmap = image.lock(0,True,False)
		
		b.image = CreateImage(width,height)
		b.gx = x
		b.gy = y
		b.advance = adv
		
		Local inTo:TPixmap = b.image.lock(0,True,True)
		into.ClearPixels(0)
		Local w:Int
		Local h:Int
		If into.width  >= from.width+x Then  w = from.width  Else w = inTo.width - x
		If into.height >= from.height+x Then h = from.height Else h = inTo.height- y

		For Local d:Int = 0 Until h
			For Local a:Int = 0  Until w
					Local n:Int = from.ReadPixel(a,d)
					inTo.WritePixel(a+x+2,d+y+2,n)
			Next
		Next

		Return b
		
	End Function
	
	
	Method Draw(x:Float,y:Float)
		If image DrawImage image,x,y
	End Method
	
End Type		

Type TBitMapFont
	
	Field bitMap		:Tbitmap[96]
	Field gfx			:TMax2dGraphics
	
	Function Create:TBitmapFont(url:String,size:Int)
		Local oldFont:TImageFont = GetImageFont()
		Local imgFont:TImageFont=LoadImageFont(url,size)
		Local b:TbitMapFont = New TbitMapFont
		If imgfont=Null Return Null
		SetImageFont(imgfont)
		
		b.gfx = tmax2dgraphics.Current()
		
		For Local i:Int = 0 Until 96
			Local n:Int = imgFont.CharToGlyph(i+32)
			If n < 0 Continue
			Local glyph:TImageGlyph = imgFont.LoadGlyph(n)
			If glyph
				b.bitMap[i] = TBitMap.Create(glyph._image,glyph._x,glyph._y,glyph._advance,size*2,size*2)			
			Else
			 	b.bitmap[i] = New TBitMap
			EndIf
		Next
		SetImageFont(oldFont)
		Return b
	End Function
	
	Method draw(text:String,x:Float,y:Float)
		For Local i:Int = 0 Until text.length
			Local a:Int = text[i]-32
			Local bm:TBitMap = bitmap[a]
			bm.Draw(x,y)
			x :+ bm.advance * gfx.tform_ix
			y :+ bm.advance * gfx.tform_jx
		Next
	End Method
	
	Method drawFixed(text:String,x:Float,y:Float)
		For Local i:Int = 0 Until text.length
			Local a:Int = text[i]-32
			bitmap[a].draw(x,y)
			x:+bitmap[a].advance
		Next
			
	End Method
End Type
