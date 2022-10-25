; ID: 2231
; Author: Leon Drake
; Date: 2008-03-13 23:42:49
; Title: drawpixmaptext
; Description: Utilize fonts in pixmaps for panels

Function drawpixmaptext(text$,pix:TPixmap,x,y,font:TImageFont,red=255,green=255,blue=255)
Local newx
Local theight			
			
		
		
		theight = font.Height()	
		Print theight	
		For i = 0 To text.length-1

			tempText$ = Mid(text, i+1, 1)
			asciiVal = Asc(tempText)
			n=font.CharToGlyph( asciiVal )
			If n<0 Continue
			
			glyph:TImageGlyph = font.LoadGlyph(n)
			image:TImage=glyph._image
			Print "Draw "+Chr(asciiVal)
			If image <> Null
			tempix:TPixmap = LockImage(image)
			UnlockImage(glyph._image)
			
			If tempix

				
			
					
					For yy = 0 To tempix.height-1
						For xx = 0 To tempix.width-1
							If newx+x+xx < pix.width And y+yy < pix.height Then
							
							alpha# = RGBA_alpha(ReadPixel(tempix,xx,yy))
							alphan# = Float(alpha#/255.0)
							anti# = 1.0-alphan#
							ured = RGBA_Red(ReadPixel(tempix,xx,yy))
							ugrn = RGBA_Green(ReadPixel(tempix,xx,yy))
							ublu = RGBA_Blue(ReadPixel(tempix,xx,yy))
							
							newred = ((RGBA_Red(ReadPixel(pix,newx+x+xx,y+yy))*anti#)+(ured*alphan#))
							newgrn = ((RGBA_Green(ReadPixel(pix,newx+x+xx,y+yy))*anti#)+(ugrn*alphan#))
							newblu = ((RGBA_Blue(ReadPixel(pix,newx+x+xx,y+yy))*anti#)+(ublu*alphan#))
		

							If newred > 255 Then newred = 255
							If newgrn > 255 Then newgrn = 255
							If newblu > 255 Then newblu = 255
														
							WritePixel(pix,newx+x+xx,(theight-tempix.height)+y+yy,ToRGBA(newred,newgrn,newblu,255))

							EndIf
						Next
					Next
					
			EndIf
			EndIf
			newx = 	newx + tempix.width	

			
		
		Next


End Function

	Function RGBA_Red%(rgba%)
		Return (rgba Shr 16) & $FF	
	End Function
              
	Function RGBA_Green%(rgba%)
		Return (rgba Shr 8) & $FF 
	End Function
	
	Function RGBA_Blue%(rgba%)
		Return rgba & $FF 
	End Function
	
	Function RGBA_alpha%(rgba%)
		Return (rgba% Shr 24) & $FF
	End Function
	
	Function ToRGBA%(r%,g%,b%,a%)
		'return (a << 24) | (r << 16) | (g << 8) | (b);
		Return ((A Shl 24) | (R Shl 16) | (G Shl 8) | B)
	End Function
