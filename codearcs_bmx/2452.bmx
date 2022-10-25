; ID: 2452
; Author: SebHoll
; Date: 2009-04-05 10:17:28
; Title: XPMFromPixmap:String[]()
; Description: Converts a BlitzMax pixmap into an XPM compliant string array.

Function XPMFromPixmap:String[](pPixmap:TPixmap)
	
	Const charRange:Int = 16
	
	Local x:Int, y:Int, i:Int, j:Int, tmpColor:Int, chrctsPerPixel:Int, tmpString$
	Local width:Int = PixmapWidth(pPixmap), height:Int = PixmapHeight(pPixmap)
	Local colormap:Int[][] = New Int[][height], colors:Int[], colorstrings:String[]
	
	For y = 0 Until height
		colormap[y] = New Int[width]
		For x = 0 Until width
			'Read color from pixel
			tmpColor = ReadPixel(pPixmap,x,y)
			'If less than 50% alpha, set a standard transparent color.
			If (tmpColor Shr 24) < $80 Then tmpColor = $00000000
			'Find the color if it has been used before.
			For i = 0 Until colors.length
				If colors[i] = tmpColor Then Exit
			Next
			'If it hasn't been found, add it to the end of the array.
			If i = colors.length Then colors:+[tmpColor]
			'And finally, update the colormap with the color index.
			colormap[y][x] = i
		Next
	Next
	
	chrctsPerPixel = (colors.length / charRange) + 1
	colorstrings = New String[colors.length]
	
	Local tmpResult:String[] = [width + " " + height + " " + colors.length + " " + chrctsPerPixel]
	
	For i = 0 Until colors.length
		Local tmpI:Int = i
		For j = 0 Until chrctsPerPixel
			colorstrings[i]:+Chr$("a"[0]+(tmpI Mod charRange))
			tmpI:/charRange
		Next
		tmpString = colorstrings[i] + "~tc "
		If (colors[i] Shr 24) <> $FF Then tmpString:+"None" Else tmpString:+"#"+_RGBHex(colors[i]&$FFFFFF)
		tmpResult:+[tmpString]
	Next
	
	For y = 0 Until height
		tmpString = ""
		For x = 0 Until width
			tmpString:+colorstrings[colormap[y][x]]
		Next
		tmpResult:+[tmpString]
	Next
	
	Return tmpResult
	
EndFunction

Function _RGBHex$( rgb:Int )
	Local buf:Short[6]
	For Local k:Int=5 To 0 Step -1
		Local n:Int=(rgb&15)+Asc("0")
		If n>Asc("9") n=n+(Asc("A")-Asc("9")-1)
		buf[k]=n
		rgb:Shr 4
	Next
	Return String.FromShorts( buf,buf.length )
End Function
