; ID: 2658
; Author: AdamRedwoods
; Date: 2010-03-03 12:49:46
; Title: Ultra Fast Draw Pixmap on Pixmap
; Description: The fastest method I could find to draw Pixmap onto another Pixmap with alpha

Function UltraDrawPixmapOnPixmap(spix:TPixmap,  pixmap:TPixmap, x:Int, y:Int, opacity:Int = $ff) NoDebug
        	'' if your pixmaps are rgba8888 then it will work faster
		'' ARGB is byte order
		'' non-multiplied alpha
	Local sourcepixel:Int Ptr, osp:Int Ptr
        Local destpixel:Int Ptr, dsp:Int Ptr
        Local sAlpha:Int
        
        If (Not spix Or Not pixmap) Then Return
        If spix.format<>PF_RGBA8888 spix = ConvertPixmap(spix, PF_RGBA8888 )
        If pixmap.format<>PF_RGBA8888 pixmap = ConvertPixmap(pixmap, PF_RGBA8888 )

        Local sWidth:Int=spix.Width
        Local sHeight:Int=spix.Height
        Local dWidth:Int=pixmap.Width
        Local dHeight:Int=pixmap.Height
        sourcepixel=Int Ptr(PixmapPixelPtr(spix,0,0))
        destpixel=Int Ptr(PixmapPixelPtr(pixmap,0,0))
        Local sRowInts:Int= spix.pitch Shr 2 '' 4 bytes to int 
        Local dRowInts:Int= pixmap.pitch Shr 2

		''crop the image
		If ( (x+sWidth)>=dWidth) Then sWidth=dWidth-x '-2 'if you get wraparound issues
        	If ( (y+sHeight)>=dHeight) Then sHeight=dHeight-y
		
		If(y<0) 
			y = Abs(y)
			sourcepixel :+(y*sRowInts)
			sHeight :-y
			y=0
		EndIf
		
		If(x<0) 
			sourcepixel :+(-x)
			sWidth :+x
			x=0
		EndIf
		
		destpixel = destpixel + x +(y*dRowInts)
                dsp = destpixel
                osp = sourcepixel
		Local Mask:Int=$000000FF

		For Local j:Int=0 To (sHeight)
			For Local i:Int =0 To (sWidth) Step 2 'dRowInts
					Local sPixel:Int 
					sPixel = sourcepixel[i]
					sAlpha=(sPixel Shr 24) & $000000ff & opacity
				
                      	                Local dPixel:Int = destpixel[i] 
					
					''''straight alpha (unmultiplied)
					'Local dAlpha:Int = dPixel Shr 24 & Mask ''untested: alpha onto alpha
					'Local fa:Int = sAlpha  + ((256-sAlpha)*dAlpha) Shr 8 ''untested: alpha onto alpha
					Local srb:Int = sPixel & $00ff00ff
					Local sg:Int = sPixel & $0000ff00
					Local drb:Int = dPixel & $00ff00ff
					Local dg:Int = dPixel & $0000ff00
					
					Local orb:Int = (drb + (((srb - drb) * sAlpha + $00800080) Shr 8)) & $00ff00ff
					Local og:Int = (dg + (((sg - dg ) * sAlpha + $00008000) Shr 8)) & $0000ff00

					destPixel[i] = orb | og | $ff000000 ''no need for alpha blending if its on static bg
					
					''lets do it again
                                        If ( i >= dWidth And (sWidth & 1))
						''odd length, dont draw
					Else
					sPixel = sourcepixel[i+1]
					sAlpha=(sPixel Shr 24) & $000000ff & opacity
				
                      	                dPixel:Int = destpixel[i+1] 
					srb:Int = sPixel & $00ff00ff
					sg:Int = sPixel & $0000ff00
					drb:Int = dPixel & $00ff00ff
					dg:Int = dPixel & $0000ff00
					
					orb:Int = (drb + (((srb - drb) * sAlpha + $00800080) Shr 8)) & $00ff00ff
					og:Int = (dg + (((sg - dg ) * sAlpha + $00008000) Shr 8)) & $0000ff00

					destPixel[i+1] = orb | og | $ff000000
		                        EndIf				
                Next
                sourcepixel= osp + sRowInts*j ''need these to truncate off any extra bytes
                destpixel = dsp + dRowInts*j         
        Next        
End Function
