; ID: 1994
; Author: ninjarat
; Date: 2007-04-20 16:02:28
; Title: StarGen
; Description: Generate a high quality HDR starfield image.

Function MakeStarfield:TPixmap(sizex=800,sizey=600)
	Local sf:TPixmap=TPixmap.Create(1600,1200,PF_RGB888,1)
	sf.ClearPixels $ff000000
	'make starfield
	For x=0 To 1599
		For y=0 To 1199
			If Rand(60)>59 Then
				red=Rand(128,255)
				grn=Rand(128,255)
				blu=Rand(128,255)
				br#=Rand(-920,18)
				sf.WritePixel x,y,255 Shl 24|red Shl 16|grn Shl 8|blu
				If br>1 Then
					For x2=x-br*2-2 To x+br*2+2
						For y2=y-br*2-2 To y+br*2+2
							If (x2<>x Or y2<>y) And ..
							 x2>=0 And x2<1600 And ..
							 y2>=0 And y2<1200 Then
								distX#=Float(x)-Float(x2)
								distY#=Float(y)-Float(y2)
								distM#=Abs(Sqr(distX*distX+distY*distY))
								I#=(1/(distM^3))*(br/127#)
								ncI=I*255
								prI=$ff&(sf.ReadPixel(x2,y2)Shr 16)
								pgI=$ff&(sf.ReadPixel(x2,y2)Shr 8)
								pbI=$ff&sf.ReadPixel(x2,y2)
								rd=((ncI*red)+prI) ; If rd>255 Then rd=255
								gn=((ncI*grn)+pgI) ; If gn>255 Then gn=255
								bl=((ncI*blu)+pbI) ; If bl>255 Then bl=255
								sf.WritePixel x2,y2,255 Shl 24|rd Shl 16|gn Shl 8|bl
							End If
						Next
					Next
				End If
			End If
		Next
	Next
	'make big star in center
	For x=0 To 1599
		For y=0 To 1199
			distX#=800-Float(x)
			distY#=600-Float(y)
			distM#=Abs(Sqr(distX*distX+distY*distY))
			I#=(1/(distM*distM))*750
			If I>.05 And Abs(Sqr(distX*distX+distY*distY))<200 Then
				ncI=I*255
				pcI=(($ff&sf.ReadPixel(x,y))+..
				 ($ff&(sf.ReadPixel(x,y)Shr 8))+..
				 ($ff&(sf.ReadPixel(x,y)Shr 16))/3)
				rd=(ncI+pcI)/4 ; If rd>255 Then rd=255
				gn=(ncI+pcI)/3 ; If gn>255 Then gn=255
				bl=(ncI+pcI)/2 ; If bl>255 Then bl=255
				sf.WritePixel x,y,255 Shl 24|rd Shl 16|gn Shl 8|bl
			End If
		Next
	Next
	sf=ResizePixmap(sf,sizex,sizey).Convert(PF_RGB888)
	Return sf
End Function
