; ID: 2829
; Author: tesuji
; Date: 2011-02-27 12:37:54
; Title: TSimpleTextFX
; Description: Various Text FX

SuperStrict

' -----------------------------------------------------------------------------------------------------------------------
' TSimpleTextFx - Focus, Drop Shadow, Glow, Emboss, Reflection & 3D Effects
' Simple enhancement for Blitzmax inbuilt drawtext commands to allow for soft drop shadows, outlines and focal effects
' Pre renders text to images for improved display flexibility and pixel manipulation
' Caveats - Current technique is limited to short text phrases due to use of loadanimimage/grabpixmap (screen size only)
'           could be fixed by pre-rendering individual glyphs aka http://blitzbasic.com/codearcs/codearcs.php?code=2736
'           or reducing the number of blur iterations
'         - Pre-rendering can take some initial time at start up. Would prefer realtime blur using GPU ;-)
' Credits - Uses impixi's pixmap blur code http://blitzbasic.com/codearcs/codearcs.php?code=1840
' Hint - try using other fonts such as http://www.2-free.net/free-fonts/marker-felt-wide-plain-regular/35853.html
'           Bold fonts work best.
' Tesuji 2011
' -----------------------------------------------------------------------------------------------------------------------
'

Type TBlurImage

	Field img:TImage
	Field frameCount:Int
	Field marginBottom:Int
	
	Function generateBlurTextImage:TBlurImage(text:String, frames:Int=10, marginLeft:Int=2, marginRight:Int=8, marginTop:Int=0, marginBottom:Int=8)
	
		Local bi:TBlurImage = New TBlurImage
	
		'SetClsColor 255,0,0
		Cls
		SetColor 255,255,255
		'SetClsColor 0,0,0
		SetBlend ALPHABLEND
		SetAlpha 1.0
		DrawText text,marginLeft,marginTop
		Local pixmap:TPixmap = GrabPixmap(0,0, TextWidth(text)+(marginLeft+marginRight), TextHeight(text)+(marginTop+marginBottom))
		bi.img = generateBlurImageStrip(pixmap,frames)
		bi.frameCount = frames
		bi.marginBottom = marginBottom
		Return bi
	
	End Function

	Function generateBlurImageStrip:TImage(pm:TPixmap, frames:Int=10)
	
		SetBlend ALPHABLEND
		Local blur:Float = 1.0
		Local blurInc:Float = 1/Float(frames)
		Local x:Int = 0
		Local y:Int = 0
		Local w:Int = PixmapWidth(pm)
		Local h:Int = PixmapHeight(pm)
		Local maxw:Int = w
		Local maxh:Int = h
		Cls
		Local blurpm:TPixmap
		For Local i:Int = 1 To frames
			blurpm = CopyPixmap(pm)
			blurPixmap(blurpm,blur)
			DrawPixmap blurpm, x,y
			x :+ w
			If x+w > GraphicsWidth() Then y :+ h ; maxw = x ; maxh = y + h ; x=0
			blur :- blurInc 
		Next
	
		Return LoadAnimImage(GrabPixmap(0,0,maxw,maxh), w, h, 0,frames)
	
	End Function

	'pm - the pixmap to blur. Format must be PF_RGBA8888
	'k - blurring amount. Value between 0.0 and 1.0
	'	 0.1 = Extreme, 0.9 = Minimal 

	Function blurPixmap(pm:TPixmap, k:Float = 0.5)
	
	
		For Local x:Int = 1 To (pm.Width - 1)
	    	For Local y:Int = 0 To (pm.Height - 1)
				WritePixel(pm, x, y, blurPixel(ReadPixel(pm, x, y), ReadPixel(pm, x - 1, y), k))
	    	Next
	    Next
	
	    For Local x:Int = (pm.Width - 3) To 0 Step -1
	    	For Local y:Int = 0 To (pm.Height - 1)
				WritePixel(pm, x, y, blurPixel(ReadPixel(pm, x, y), ReadPixel(pm, x + 1, y), k))
	    	Next
	    Next
	
	    For Local x:Int = 0 To (pm.Width - 1)
	    	For Local y:Int = 1 To (pm.Height - 1)
				WritePixel(pm, x, y, blurPixel(ReadPixel(pm, x, y), ReadPixel(pm, x, y - 1), k))
	    	Next
	    Next
	
	    For Local x:Int = 0 To (pm.Width - 1)
	    	For Local y:Int = (pm.Height - 3) To 0 Step -1
				WritePixel(pm, x, y, blurPixel(ReadPixel(pm, x, y), ReadPixel(pm, x, y + 1), k))
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
					
		Local px2a:Byte = px2 Shr 24			
		Local px2b:Byte = px2 Shr 16
		Local px2g:Byte = px2 Shr 8
		Local px2r:Byte = px2
					
		'pxa = (px2a * (1 - k)) + (pxa * k)
		pxb = (px2b * (1 - k)) + (pxb * k)
		pxg = (px2g * (1 - k)) + (pxg * k)
		pxr = (px2r * (1 - k)) + (pxr * k)
		pxa = Min(255,(pxr+pxg+pxb))
			
		Return Int(pxa Shl 24 | pxb Shl 16 | pxg Shl 8 | pxr)
	
	EndFunction

End Type


Type TSimpleTextFx

	Const LEFT_JUSTIFY:Int=0
	Const RIGHT_JUSTIFY:Int=1
	Const CENTER_JUSTIFY:Int=2

	Field image:TBlurImage 
	Field x:Float,y:Float
	Field scale:Float=1.0 
	Field color:TColor = New TColor ' main color
	Field alpha:Float=1.0 ' opacity
	Field phase:Float=1.0 ' animation phase 0.0 to 1.0
	
	Method Create:TSimpleTextFx(image:TBlurImage, x:Int=0, y:Int=0)
		Self.image = image
		Self.x = x
		Self.y = y
		Return Self
	End Method
	
	Method position(textX:Int=0,textY:Int=0, transitionRate:Float=1.0, justify:Int=LEFT_JUSTIFY)

		Local tx:Int
		Local ty:Int = textY
		
		Select justify
			Case LEFT_JUSTIFY tx = textX
			Case RIGHT_JUSTIFY tx = textX+(ImageWidth(image.img)*scale)
			Case CENTER_JUSTIFY tx = textX-(ImageWidth(image.img)*scale*.5)
		End Select
				
		x :+ (tx-x)*transitionRate
		y :+ (ty-y)*transitionRate
	End Method
	
	Method positionCenter(transitionRate:Float=1.0)
		Local tx:Int = (GraphicsWidth()*.5)-(ImageWidth(image.img)*scale*.5)
		Local ty:Int = (GraphicsHeight()*.5)-(ImageHeight(image.img)*scale*.5)
		x :+ (tx-x)*transitionRate
		y :+ (ty-y)*transitionRate
	End Method

	Method scaleTo(tscale:Float,transitionRate:Float=1.0)
		Local w:Float = ImageWidth(image.img)
		Local h:Float = ImageHeight(image.img)
		Local cx:Float = x+((w*.5)*scale)
		Local cy:Float = y+((h*.5)*scale)		
		scale :+ (tscale-scale)*transitionRate
		x = cx-((w*.5)*scale)	
		y = cy-((h*.5)*scale)	
	End Method
	
	Method fitWidth(pixels:Int,transitionRate:Float=1.0)
		Local tscale:Float = pixels/Float(ImageWidth(image.img))
		scale :+ (tscale-scale)*transitionRate		
	End Method
	
	Method render() Abstract ' all sub-types must implement this method themselves
	
End Type

Type TGlowText Extends TSimpleTextFx

	Field glowColor:TColor = New TColor.Create(1,1,1)

	Method Create:TGlowText(image:TBlurImage,x:Int=0,y:Int=0)
		Super.Create(image,x,y)
		Return Self
	End Method
	
	Method update(xinc:Float=0, yinc:Float=0)

		x :+ xinc
		x = TUtils.wrapAround(x,-ImageWidth(image.img)*scale, GraphicsWidth())

		y :+ yinc
		y = TUtils.wrapAround(y,-ImageHeight(image.img)*scale,GraphicsHeight())
		
	End Method

	Method render()
	
		Local opacity:Float = TUtils.limit(phase,0.0,1.0)
		Local superScale:Float = 1.0

		SetBlend LIGHTBLEND
		SetAlpha alpha*.75*opacity
		SetScale scale*superscale,scale*superscale

		Local count:Int = 10
						
		Local frame:Float = 0
		Local frameInc:Float = image.frameCount/Float(count)
		Local offset:Float = 1*scale
		
		glowColor.use()
		For Local i:Int = 1 To count
		
			DrawImage image.img,x,y,frame
			DrawImage image.img,x,y-offset,frame		
			DrawImage image.img,x,y+offset,frame		
		
			offset :* 1.08
			frame :+ frameInc		
		Next
		
		SetBlend ALPHABLEND
		SetAlpha alpha*opacity
		color.use()
		DrawImage image.img,x,y,image.frameCount*.5
		SetBlend LIGHTBLEND
		DrawImage image.img,x,y,image.frameCount*.5
						
		SetScale 1,1
	
	End Method

End Type

Type TShadowText Extends TSimpleTextFx

	Field shadowHeight:Float = 4.7
	Field shadowAlpha:Float = 0.5

	Method Create:TShadowText(image:TBlurImage,x:Int=0,y:Int=0)
		Super.Create(image,x,y)
		Return Self
	End Method

	Method update(xinc:Float=0, yinc:Float=0)

		x :+ xinc
		x = TUtils.wrapAround(x,-ImageWidth(image.img)*scale, GraphicsWidth())

		y :+ yinc
		y = TUtils.wrapAround(y,-ImageHeight(image.img)*scale,GraphicsHeight())
		
	End Method
	
	Method render()

			Local opacity:Float = TUtils.limit(phase,0.0,1.0)
			Local h:Float = shadowHeight*opacity
	
			SetScale scale,scale

			SetColor 0,0,0
			SetBlend ALPHABLEND
			SetAlpha alpha*shadowAlpha*opacity
			Local frame:Int = Min(h*(image.frameCount/6), image.frameCount-1)
			DrawImage image.img, x+(h*scale), y+(h*scale), frame

			color.use()
			SetAlpha alpha*opacity			
			SetBlend ALPHABLEND
			DrawImage image.img,x,y,image.frameCount/3

			SetColor 255,255,255
			SetAlpha alpha*.5*opacity			
			SetBlend LIGHTBLEND
			DrawImage image.img,x,y,image.frameCount/6

			SetScale 1,1
	
	End Method

End Type

Type TBlurText Extends TSimpleTextFx

	Method Create:TBlurText(image:TBlurImage,x:Int=0,y:Int=0)
		Super.Create(image,x,y)
		Return Self
	End Method
	
	Method update(xdir:Float=0,ydir:Float=0, blur:Float=0)
	
		Local speed:Float = phase
	
		y :+ speed*ydir
		y = TUtils.wrapAround(y,-ImageHeight(image.img)*scale,GraphicsHeight())
		
		x :+ speed*xdir 
		x = TUtils.wrapAround(x,-ImageWidth(image.img)*scale, GraphicsWidth())
				
	End Method
		
	Method render()
		SetScale scale,scale
		'SetBlend LIGHTBLEND
		SetAlpha alpha
		color.use()
		Local frame:Int = TUtils.limit((1.0-phase)*(image.frameCount-1),0, image.frameCount-1)
		DrawImage image.img,x,y, frame
		SetScale 1,1	
	End Method
	
	Function zSort:Int(o1:Object,o2:Object)	
		Return (TBlurText(o2).phase*1024)-(TBlurText(o1).phase*1024)
	End Function

End Type

Type TReflectionText Extends TSimpleTextFx

	Method Create:TReflectionText(image:TBlurImage,x:Int=0,y:Int=0)
		Super.Create(image,x,y)
		Return Self
	End Method
	
	Method render()
	
		SetScale scale,scale
		SetBlend ALPHABLEND
		SetAlpha alpha
		color.use()
		
		Local anim:Float = TUtils.limit(phase,0.0, 1.0)
		If anim > 0.0

		Local w:Float = ImageWidth(image.img)
		Local h:Float = ImageHeight(image.img)-(image.marginBottom*1.5)
		Local frame:Float = image.frameCount/6
		Local blend:Float = 1.0
		Local blendInc:Float = 1/h
		Local frameInc:Float = blendInc*(image.frameCount-(image.frameCount/6))

		Local margin:Int = (image.marginBottom)*scale
		margin = 0

		SetBlend ALPHABLEND
		Local yoff:Float = ((h)*(1.0-anim))		
		DrawSubImageRect image.img, x, y+(yoff*scale),w,h-(yoff), 0,0,w,h-yoff, 0,0,    frame
		
		For Local yy:Int = 0 To h-1
			SetAlpha alpha*.75*blend
			DrawSubImageRect image.img, x,y-margin+(yy*scale)+((h*scale)-margin),w,1, 0,h-yy-yoff,w,-1, 0,0, frame
			blend :- (blendInc*1.25)
			frame :+ frameInc
		Next
		
		End If
				
		SetScale 1,1
		
	End Method

End Type

Type TGradientText Extends TSimpleTextFx

	Field bottomColor:TColor = New Tcolor.Create(0,0,0)

	Method Create:TGradientText(image:TBlurImage,x:Int=0,y:Int=0)
		Super.Create(image,x,y)
		Return Self
	End Method
	
	Method render()
	
		Local anim:Float = TUtils.limit(phase,0.0,1.0)

		SetScale scale,scale
		SetBlend ALPHABLEND
		SetAlpha alpha
		color.use()
		
		Local w:Float = ImageWidth(image.img)
		Local h:Float = ImageHeight(image.img)-image.marginBottom
		Local frame:Int = image.frameCount/4
		Local blend:Float = 0
		Local blendInc:Float = (1/h)*anim

		For Local yy:Int = 0 To h-1
			color.useBlend(bottomColor,blend)	
			SetBlend ALPHABLEND
			SetAlpha alpha*anim
			DrawSubImageRect(image.img, x, y+(yy*scale), w, 1, 0, yy, w, 1, 0,0, frame)
			SetBlend LIGHTBLEND
			SetAlpha alpha*anim*2
			DrawSubImageRect(image.img, x, y+(yy*scale), w, 1, 0, yy, w, 1, 0,0, frame)

			blend :+ blendInc 
		Next
				
		SetScale 1,1
	
	End Method

End Type

Type TEmbossText Extends TSimpleTextFx

	Field highlightColor:TColor = New TColor.Create(1,1,1)

	Method Create:TEmbossText(image:TBlurImage,x:Int=0,y:Int=0)
		Super.Create(image,x,y)
		Self.color.setRgb(.5,.5,.5)
		Return Self
	End Method
	
	Method render()
	
		Local anim:Float = TUtils.limit(phase,0.0,1.0)
	
		SetScale scale,scale
		SetBlend ALPHABLEND
		SetAlpha alpha*anim
		color.use()
		Local frame:Int = (image.frameCount-1)*(anim)
		DrawImage image.img,x+(scale*1),y+(scale*1),frame/2
		SetBlend LIGHTBLEND	
		SetAlpha alpha*.75*anim	
		DrawImage image.img,x,y,frame/4
		highlightColor.use()
		DrawImage image.img,x-(scale*1),y-(scale*1),frame/6

		SetScale 1,1
	
	End Method

End Type

Type T3DText Extends TSimpleTextFx

	Field originX:Float = GraphicsWidth()/2
	Field originY:Float = GraphicsHeight()/2 ' vanishing point

	Method Create:T3DText(image:TBlurImage,x:Int=0,y:Int=0)
		Super.Create(image,x,y)
		Return Self
	End Method
	
	Method update(xinc:Float=0, yinc:Float=0)

		x :+ xinc
		x = TUtils.wrapAround(x,-ImageWidth(image.img)*scale, GraphicsWidth())

		y :+ yinc
		y = TUtils.wrapAround(y,-ImageHeight(image.img)*scale,GraphicsHeight())
		
	End Method
	
	Method render()
	
		Local anim:Float = TUtils.limit(phase,0.0,64.0)
	
		SetScale scale,scale
		SetBlend ALPHABLEND
		SetAlpha alpha * anim
		color.use()
		
		Local w:Float = ImageWidth(image.img)
		Local h:Float = ImageHeight(image.img)-image.marginBottom
		Local frame:Int = image.frameCount/3

		Local yoff:Float = 0
		Local x1:Float = x
		Local x2:Float = x + (w*scale)
		Local y1:Float = y

		Local lines:Float = (scale*(h-1))*anim

		Local x1inc:Float = (originX-x1)/(originY-y1)
		Local x2inc:Float = (originX-x2)/(originY-y1)
		Local yinc:Float = (h/lines+.0000001)

		x1 :+ ((originY-y)*(1.0-anim))*x1inc
		x2 :+ ((originY-y)*(1.0-anim))*x2inc
		y1 :+ ((originY-y)*(1.0-anim))

		SetScale 1,1
		
		For Local yy:Int = 0 To lines
			SetBlend ALPHABLEND
			DrawSubImageRect(image.img, x1,y1+yy,x2-x1,1, 0,yy*yinc,w,1, 0,0, frame)
			x1 :+ x1inc
			x2 :+ x2inc
		Next
				
	End Method

End Type

Type TScanlineText Extends TSimpleTextFx

	Field bottomColor:TColor = New Tcolor.Create(0,0,0)

	Method Create:TScanlineText(image:TBlurImage,x:Int=0,y:Int=0)
		Super.Create(image,x,y)
		Return Self
	End Method
	
	Method render()
	
		Local anim:Float = TUtils.limit(phase,0.0,1.0)

		SetScale scale,scale
		SetBlend ALPHABLEND
		SetAlpha alpha*anim
		color.use()
		
		Local w:Float = ImageWidth(image.img)
		Local h:Float = ImageHeight(image.img)-image.marginBottom
		Local spread:Float =  Max(1.0,1.0+((1.0-Abs(anim))*4))
		Local frame:Int = TUtils.limit(Min(image.frameCount,((image.frameCount/4)*spread)),0,image.frameCount-1)
		Local blend:Float = 0
		Local blendInc:Float = 1/h

		Local yoff:Float = 0
		
		SetScale scale,scale*.5
		
		For Local yy:Int = 0 To h-1
			color.useBlend(bottomColor,blend)
			yoff = (yy-(h/2.0))*(1/h)*(spread-1.0)*(50*scale)
			If yy Mod 2 = 0
				SetBlend ALPHABLEND
				SetAlpha alpha*anim
				bottomColor.useBlend(color,blend)
				DrawSubImageRect(image.img, x, y+(yy*scale)+yoff, w, 1, 0, yy, w, 1, 0,0, frame)
			Else
				SetBlend ALPHABLEND
				SetAlpha alpha*anim					
				DrawSubImageRect(image.img, x, y+(yy*scale)+yoff, w, 1, 0, yy, w, 1, 0,0, frame)
				SetBlend LIGHTBLEND
				DrawSubImageRect(image.img, x, y+(yy*scale)+yoff, w, 1, 0, yy, w, 1, 0,0, frame)

			End If
			blend :+ blendInc 
		Next
		SetScale 1,1
	
	End Method


End Type

' ---------------

Type TColor

	Field red:Float=1.0, green:Float=1.0, blue:Float=1.0
	
	Method Create:TColor(r:Float,g:Float,b:Float)
		Self.setRgb(r,g,b)	
		Return Self
	End Method

	Method setRgb(r:Float,g:Float,b:Float)
		red = r ; green = g ; blue = b
	End Method
	
	Method use()
		SetColor red*255,green*255,blue*255
	End Method
	
	Method useBlend(blendColor:TColor, blendAmount:Float=.5)	
		Local r:Float = red-((red-blendColor.red)*blendAmount)
		Local g:Float = green-((green-blendColor.green)*blendAmount)
		Local b:Float = blue-((blue-blendColor.blue)*blendAmount)
		SetColor r*255,g*255,b*255
	End Method
	
End Type

Type TUtils

	Function limit:Float(value:Float, minValue:Float, maxValue:Float)
		Return Max(Min(value, maxValue), minValue)		
	End Function
	
	Function wrapAround:Float(value:Float, minValue:Float, maxValue:Float)
		If value > maxValue 
			value = minValue
		Else If value < minValue
			value = maxValue
		End If
		Return value
	End Function

End Type

' -------------------------------------------------------------------------------------
' All code below only needed for example demo
' -------------------------------------------------------------------------------------

Type TMouse

	Field x:Int=GraphicsWidth()/2,y:Int=GraphicsHeight()/2 ' current x,y location
	Field downX:Int, downY:Int ' position of mouse when button was pressed
	Field speedx:Int, speedy:Int ' mouse speed
	Field leftButtonPressed:Int = False ' Button has just been pressed
	Field leftButtonDown:Int = False ' Button is currently down
	Field leftButtonReleased:Int = False ' Button has just been released
	
	Method update()
		speedx = x-MouseX()
		speedy = y-MouseY()
		x = MouseX()
		y = MouseY()

		leftButtonPressed = MouseHit(1)
		leftButtonReleased = False
		If MouseDown(1) 
			leftButtonDown = True
		Else
			If leftButtonDown Then leftButtonReleased = True
			leftButtonDown = False
		End If
		If leftButtonPressed Then downX = x ; downY = y				
		
	End Method

End Type


' ---------------------------------------
' comment this line to use as an include
New Demo.run()
' ---------------------------------------

Type Demo

	Field background:TImage

	Method run()
	
		Graphics 1024,768,32
				
		'SetImageFont LoadImageFont("TAHOMA.TTF",24,SMOOTHFONT)
		'SetImageFont LoadImageFont("MarkerFeltWide.ttf",24,SMOOTHFONT) 		
		'SetImageFont LoadImageFont("UNIVRS6.TTF",20,SMOOTHFONT)
		'SetImageFont LoadImageFont("Admisi.ttf",20,SMOOTHFONT)
		SetImageFont LoadImageFont("Continuum.ttf",24,SMOOTHFONT)

		createBackground()

		Local time:Int = MilliSecs()
		Const BLUR_FRAMES:Int = 64

		Local txt:String = "How now brown cow"
		If AppArgs.length > 1 
			txt = AppArgs[1]
		End If

		' pre-generate text image blur frames		
						
		Local demoTexts:TList = New TList
		demoTexts.addLast(New TBlurText.Create(TBlurImage.generateBlurTextImage("Focus Text",BLUR_FRAMES)))
		demoTexts.addLast(New TShadowText.Create(TBlurImage.generateBlurTextImage("Drop Shadow Text",BLUR_FRAMES)))
		demoTexts.addLast(New TGlowText.Create(TBlurImage.generateBlurTextImage("Glow Text",BLUR_FRAMES)))
		demoTexts.addLast(New TEmbossText.Create(TBlurImage.generateBlurTextImage("Embossed Text",BLUR_FRAMES)))
		demoTexts.addLast(New TGradientText.Create(TBlurImage.generateBlurTextImage("Gradient Text",BLUR_FRAMES)))
		demoTexts.addLast(New TReflectionText.Create(TBlurImage.generateBlurTextImage("Reflection Text",BLUR_FRAMES)))
		demoTexts.addLast(New T3DText.Create(TBlurImage.generateBlurTextImage("3D Text",BLUR_FRAMES)))
		demoTexts.addLast(New TScanlineText.Create(TBlurImage.generateBlurTextImage("Scanline Text",BLUR_FRAMES)))

		Local blurimage:TBlurImage = TBlurImage.generateBlurTextImage(txt,BLUR_FRAMES)

		Print "Generated in "+(MilliSecs()-time) + " ms"
				
		' setup SimpleTextFX examples
		Local shadowText:TShadowText = New TShadowText.Create(blurimage,700,200)
		shadowText.color.setRgb(1,1,0) 
		shadowText.scale = 2.0
		
		Local glowText:TGlowText = New TGlowText.Create(blurimage,200,200)
		glowText.scale = 4.0
		glowText.color.setRgb(0,0,0)
		glowText.glowcolor.setRgb(0,1,0)
		
		Local embossText:TEmbossText = New TEmbossText.Create(blurimage)
		embossText.scale = 2.0
		embossText.color.setRgb(1,.5,0)
		embossText.highlightColor.setRgb(1,1,1)
		
		Local gradientText:TGradientText = New TGradientText.Create(blurimage)
		gradientText.fitWidth(GraphicsWidth())
		gradientText.color.setRgb(0,1,1)
		gradientText.bottomColor.setRgb(1,.6,0)
	
		Local reflectionText:TReflectionText = New TReflectionText.Create(blurimage)
		reflectionText.scale = 2.0
		reflectionText.color.setRgb(.5,1,1)
		
		Local threedTexts:TList = New TList
		Local tphase:Float = .2
		For Local i:Int = 1 To 3
			Local threedText:T3DText = New T3DText.Create(blurimage)
			threedText.positionCenter()
			threedText.y = 100
			threedText.scale = 2.0
			threedText.color.setRgb(1,1,0)
			threedText.phase = tphase
			threedTexts.addLast(threedText)
			tphase :*.75
		Next
		
		Local scanlineText:TScanlineText = New TScanlineText.Create(blurimage)
		scanlineText.scale = 2
		scanlineText.color.setRgb(1,1,0)
		scanlineText.bottomColor.setRgb(1,.3,0)
				
		Local frame:Float = 0
		Local frameInc:Float = .25
		
		Local textSwarm:TList = New TList
		For Local i:Int = 0 To 100
			Local b:TBlurText = New TBlurText.Create(blurimage, Rand(0,GraphicsWidth()), Rand(0,GraphicsHeight()) )
			b.phase = Rnd(0.0,2.0)-2.3
			b.color.setRgb(.5,1,.5)
			b.scale = 2
			textSwarm.addLast(b)
		Next
		
		Local xdir:Float = .1
		Local ydir:Float = 0
		Local xaxis:Float = 0
		Local yaxis:Float = 0
		Local size:Float = 2.0
		
		Const MODE_COMBO:Int = 0
		Const MODE_DOF:Int = 1
		Const MODE_SHADOW:Int = 2
		Const MODE_GLOW:Int = 3
		Const MODE_EMBOSS:Int = 4
		Const MODE_GRADIENT:Int = 5
		Const MODE_REFLECTION:Int = 6
		Const MODE_3D:Int = 7
		Const MODE_SCANLINE:Int = 8
		Const MAX_MODES:Int = 9
		
		Local mode:Int = MODE_COMBO
		Local alpha:Float = 1.0
		Local alphaInc:Float = .01
		
		Local phase:Float = 0.0 ' animation phase

		Local mouse:TMouse = New TMouse
		MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
		HideMouse()
				
		Flip
		Delay(5000)
		
		#mainloop 		
		While Not KeyHit(KEY_ESCAPE)
			
			Cls
		
			Select mode
			
				Case MODE_COMBO
				
					drawBackground(10-phase,1-(phase*.25), 1.5-(phase*.05))
				
					Local animPhase:Float = 0.0
					Local y:Int = 60	
					For Local demoText:TSimpleTextFx = EachIn demoTexts
					
						SetBlend LIGHTBLEND
						demoText.scale = 2
						demoText.positionCenter()
						demoText.y = y
						demoText.phase = TUtils.limit(phase-animPhase,0.0,1.0)
						demoText.render()
												
						animPhase :+ 1.0
						y :+ 80

					Next
								
				Case MODE_DOF
				
					If phase < 1.0 Then
						Local title:TSimpleTextFx = TSimpleTextFx(demoTexts.valueAtIndex(0))
						title.phase = 1.0-phase
						title.scaleTo(3,.05)
						title.render()
					End If
				
					If phase < 5.0 Then xaxis = 1 Else xaxis = -1 

					SetBlend ALPHABLEND
					SortList (textSwarm,False,TBlurText.zSort)
					For Local b:TBlurText = EachIn textSwarm
						b.update(xdir,ydir,xaxis)
						b.scale :+ (yaxis*.025) ; b.scale = TUtils.limit(b.scale,.5,200)
						b.phase :+ (xaxis*.005) 
						b.render()
					Next
					
					phase :+ .01
					If phase > 10.0 Then phase = 0.0 ; mode :+ 1 
										
				Case MODE_SHADOW
		
					drawBackground(phase, phase*.25, phase*.25)		

					If phase < 2.0 Then
						Local title:TSimpleTextFx = TSimpleTextFx(demoTexts.valueAtIndex(1))
						title.phase = 1.0-(phase)
						title.scaleTo(5,.025)
						title.render()
					End If
					
					If phase > 5.0 Then xaxis = -1
					If phase > 6.0 Then xaxis = 1
					If phase > 7.0 Then xaxis = 0
					
					shadowText.update(xdir,ydir)
					shadowText.shadowHeight :+ xaxis*.025 ; shadowText.shadowHeight = TUtils.limit(shadowText.shadowHeight,0,200) 
					shadowText.scale :+ (yaxis*.025) ; shadowText.scale = TUtils.limit(shadowText.scale,.5,200)
					shadowText.render()
					
				Case MODE_GLOW
				
					drawBackground(.2)
					
					If phase < 2.0 Then
						Local title:TSimpleTextFx = TSimpleTextFx(demoTexts.valueAtIndex(2))
						title.phase = 1.0-(phase*.5)
						title.scaleTo(.5,.025)
						title.positionCenter(.025)
						title.render()
					End If
					
					xaxis = (Int(phase Mod 2)*2)-1
			
					glowText.update(xdir,ydir)
					glowText.alpha = alpha
					glowText.phase = phase * .25
					glowText.color.setRgb(0,phase*.125,0)
					glowText.render()
					glowText.scale :+ (yaxis*.25) ; glowText.scale = TUtils.limit(glowText.scale,1.0,200)
					
					alpha :+ (xaxis*.01) ; alpha = TUtils.limit(alpha,0.0,1.0)
					
				Case MODE_EMBOSS
				
					If phase < 1.0 Then
						Local title:TSimpleTextFx = TSimpleTextFx(demoTexts.valueAtIndex(3))
						title.phase = 1.0-phase
						title.scaleTo(1,.05)						
						title.render()
					End If

					If phase Mod 2.0 > 0 And phase Mod 2.0 < .1 Then yaxis = 1
					If phase Mod 2.0 > .9 And phase Mod 2.0 < 1.0 Then yaxis = -1
		
					drawBackground(.5, 1.0-phase,1.0-(phase*.25))
					embossText.phase = phase * .25
					embossText.scale :+ (yaxis*.25) ; embossText.scale = TUtils.limit(embossText.scale,1.0,200)
					embossText.positionCenter(.01)		
					embossText.render()
										
				Case MODE_GRADIENT

					If phase < 1.0 Then
						Local title:TSimpleTextFx = TSimpleTextFx(demoTexts.valueAtIndex(4))
						title.phase = 1.0-phase
						title.scaleTo(1,.05)
						title.position(GraphicsWidth()/2,100,.05,TSimpleTextFx.LEFT_JUSTIFY)
						title.render()
					End If
		
					drawBackground(.5)
					gradientText.phase = phase ; gradientText.phase = TUtils.limit(gradientText.phase,0.0,1.0)
					gradientText.scale :+ (yaxis*.25) ; gradientText.scale = TUtils.limit(gradientText.scale,1.0,200)
					gradientText.positionCenter()		
					gradientText.render()
					
				Case MODE_REFLECTION
		
					reflectionText.phase = phase*.5 ; reflectionText.phase = TUtils.limit(reflectionText.phase,0.0,1.0)
					reflectionText.scale :+ (yaxis*.25) ; reflectionText.scale = TUtils.limit(reflectionText.scale,1.0,200)
					reflectionText.positionCenter()		
					reflectionText.render()
					
				Case MODE_3D

					If phase < 1.0 Then
						Local title:TSimpleTextFx = TSimpleTextFx(demoTexts.valueAtIndex(6))
						title.phase = 1.0-phase
						title.scaleTo(10,.05)
						title.render()
					End If

					xaxis = 1
					If phase > 5.0 Then xaxis = -1
				
					For Local threedText:T3Dtext = EachIn threedTexts
						threedText.update(xdir,ydir)
						If phase > 0.0 And phase < 1.0 Then threedText.position(GraphicsWidth()*.5, 200, .1, TSimpleTextFx.CENTER_JUSTIFY)
						If phase > 5.0 And phase < 6.0 Then threedText.position(GraphicsWidth()*.5, GraphicsHeight()-200, 1, TSimpleTextFx.CENTER_JUSTIFY)
						threedText.scale :+ (yaxis*.025) ; threedText.scale = TUtils.limit(threedText.scale,.5,200)
						threedText.phase :*  1.0+(xaxis*.005) ; threedText.phase = TUtils.limit(threedText.phase,0.0,16.0)
						threedText.render()					
					Next
				
					xdir :* .9
					ydir :* .9	
										
				Case MODE_SCANLINE
				
					If phase < 1.0 Then
						Local title:TSimpleTextFx = TSimpleTextFx(demoTexts.valueAtIndex(7))
						title.phase = 1.0-phase
						title.scaleTo(5,.05)
						title.render()
					End If

					scanlineText.phase = phase*.5 ; 'scanlineText.phase = TUtils.limit(scanlineText.phase,0.0,1.0)
					scanlineText.scale :+ (yaxis*.25) ; scanlineText.scale = TUtils.limit(scanlineText.scale,1.0,200)
					scanlineText.positionCenter()		
					scanlineText.render()
					
			End Select
				
			mouse.update()

			phase :+ .0075
			If phase > 10.0 Then phase = 0.0 ; mode :+ 1 ; xdir = -5 			
			If KeyHit(KEY_SPACE) Then mode :+ 1 ; phase = 0 ; xdir = -5
			If KeyHit(KEY_RIGHT) Then mode :+ 1 ; phase = 0 ; xdir = -5
			If KeyHit(KEY_LEFT) Then mode :- 1 ; phase = 0 ; xdir = -5			
			mode = TUtils.wrapAround(mode,0,MAX_MODES-1)
					
			xdir :- mouse.speedx*.25
			ydir :- mouse.speedy*.25
			xdir :* .999
			ydir :* .999
			
			xaxis = 0 ; yaxis = 0
			If KeyDown(KEY_UP) yaxis = 1
			If KeyDown(KEY_DOWN)  yaxis = -1
						
			Flip
						
		Wend
		End	
	End Method
	
	Method createBackground(size:Int=32, frames:Int=128)
		Cls
		SetColor 255,255,255
		SetBlend LIGHTBLEND
		SetAlpha .25
		DrawRect 1,1,size-1,size-1
		DrawRect 0,0,size,size

		For Local xx:Int = 0 To 3
			For Local yy:Int = 0 To 3
				SetAlpha (xx*yy)*.1
					DrawRect (xx*size*.25),(yy*size*.25),size*.25,size*.25
			Next
		Next
		Local pixmap:TPixmap = GrabPixmap(0,0, size, size)		
		background = TBlurImage.generateBlurImageStrip(pixmap,frames)
		
	End Method
	
	Method drawBackground(alpha:Float=1.0, phase:Float = 0.0, scale:Float=1.0)
	
		Local zoom:Float = TUtils.limit(scale,.025,128)
		Local frame:Int = TUtils.limit(phase*96,1,96)
		Local xScale:Float = (GraphicsWidth()/ImageWidth(background))*zoom
		Local yScale:Float = (GraphicsHeight()/ImageHeight(background))*zoom
		SetScale xScale,yScale 
		SetBlend LIGHTBLEND
		SetAlpha alpha
		SetColor 255,255,255
		Local x:Float = 0
		Local y:Float = 0
		While (y<GraphicsHeight())
			DrawImage background,x,y,frame-1
			x :+ ImageWidth(background)*xScale
			If x >= GraphicsWidth() Then x = 0 ; y :+ ImageHeight(background)*yScale
		Wend
		SetScale 1,1
	
	End Method
	
End Type
