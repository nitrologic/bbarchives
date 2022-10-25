; ID: 1840
; Author: impixi
; Date: 2006-10-14 20:37:34
; Title: Pixmap Blur
; Description: Blur a pixmap

SuperStrict

Graphics 800, 600
SetBlend SOLIDBLEND

Local pic:TPixmap = createTestPixmap()
'Local pic:TPixmap = LoadPixmap("your.png")

Local pm:TPixmap = ConvertPixmap(pic, PF_RGBA8888)

blurPixmap(pm, 0.5)

While Not KeyHit(KEY_ESCAPE)

	Cls

	DrawPixmap pic, 0, 0
	DrawPixmap pm, pic.width + 10, 0

	Flip
	
	Delay 1

Wend

End

Function createTestPixmap:TPixmap()

	SeedRnd MilliSecs()

	Local testpm:TPixmap = CreatePixmap(256, 256, PF_RGB888)
	
	For Local x:Int = 0 To testpm.width - 1
		For Local z:Int = 0 To testpm.height - 1
			WritePixel(testpm, x, z, (Rand(0, 255) Shl 16) | (Rand(0, 255) Shl 8) | Rand(0, 255))
		Next
	Next

	Return testpm

EndFunction


'*************** BLURRING FUNCTIONS ****************************

Function blurPixmap(pm:TPixmap, k:Float = 0.5)

	'pm - the pixmap to blur. Format must be PF_RGBA8888
	'k - blurring amount. Value between 0.0 and 1.0
	'	 0.1 = Extreme, 0.9 = Minimal 

	For Local x:Int = 1 To (pm.Width - 1)
    	For Local z:Int = 0 To (pm.Height - 1)
			WritePixel(pm, x, z, blurPixel(ReadPixel(pm, x, z), ReadPixel(pm, x - 1, z), k))
    	Next
    Next

    For Local x:Int = (pm.Width - 3) To 0 Step -1
    	For Local z:Int = 0 To (pm.Height - 1)
			WritePixel(pm, x, z, blurPixel(ReadPixel(pm, x, z), ReadPixel(pm, x + 1, z), k))
    	Next
    Next

    For Local x:Int = 0 To (pm.Width - 1)
    	For Local z:Int = 1 To (pm.Height - 1)
			WritePixel(pm, x, z, blurPixel(ReadPixel(pm, x, z), ReadPixel(pm, x, z - 1), k))
    	Next
    Next

    For Local x:Int = 0 To (pm.Width - 1)
    	For Local z:Int = (pm.Height - 3) To 0 Step -1
			WritePixel(pm, x, z, blurPixel(ReadPixel(pm, x, z), ReadPixel(pm, x, z + 1), k))
    	Next
    Next
	
End Function

Function blurPixel:Int(px:Int, px2:Int, k:Float)
		
	'Utility function used by blurPixmap.
	'Uncomment the commented lines to enable alpha component 
	'processing (usually not required).
			
	Local pxa:Byte = px Shr 24
	Local pxb:Byte = px Shr 16
	Local pxg:Byte = px Shr 8
	Local pxr:Byte = px
				
	'Local px2a:Byte = px2 Shr 24			
	Local px2b:Byte = px2 Shr 16
	Local px2g:Byte = px2 Shr 8
	Local px2r:Byte = px2
				
	'pxa = (px2a * (1 - k)) + (pxa * k)
	pxb = (px2b * (1 - k)) + (pxb * k)
	pxg = (px2g * (1 - k)) + (pxg * k)
	pxr = (px2r * (1 - k)) + (pxr * k)
				
	Return Int(pxa Shl 24 | pxb Shl 16 | pxg Shl 8 | pxr)

EndFunction

'**************************************************************
