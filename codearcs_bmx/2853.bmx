; ID: 2853
; Author: tesuji
; Date: 2011-05-16 16:49:35
; Title: HSB Color Picker
; Description: A simple max2D HSB color picker

SuperStrict

' Tesuji 2011
' A simple color picker

Type TColorPicker

	Field hue:Float ' 0..360
	Field saturation:Float = 1.0 ' 0..1.0
	Field brightness:Float = 1.0 ' 0..1.0	
	Field red:Int,green:Int,blue:Int ' 0..255
	Field buttonClicked:Int = False ' true if ok button has just been clicked
	
	Field xPos:Int,yPos:Int

	Field gradientImage:TImage
	Field hueImage:TImage
	Field sbZone:TZone = New TZone.Create(1,1,256,256, 0)
	Field hZone:TZone = New TZone.Create(1+256+24,1,24,256, 1)
	Field dragZone:TZone
	Field buttonZone:TZone = New TZone.Create(1+256+24+24+24,1+208,48,48)

	Method Create:TColorPicker()
		gradientImage = drawGradientImage()
		hueImage = drawHueImage()
		Return Self
	End Method

	Function drawGradientImage:TImage()
		Local image:TImage = CreateImage(256,1)
		Local pixmap:TPixmap = LockImage(image)
		Local alpha:Int
		For Local i:Int = 0 To 255
			alpha = i Shl 24
			WritePixel(pixmap,i,0,$00ffffff | alpha)
		Next
		UnlockImage(image)
		Return image
	End Function

	Function drawHueImage:TImage()
		Local image:TImage = CreateImage(256,1)
		Local pixmap:TPixmap = LockImage(image)
		Local r:Float,g:Float,b:Float
		Local hi:Float = 360.0/256.0
		Local h:Float = 0
		Local red:Int,green:Int,blue:Int
		For Local i:Int = 0 To 255
			HSBtoRGB(h,1,1,r,g,b)
			red   = r*255
			green = g*255
			blue  = b*255
			WritePixel(pixmap,i,0,$FF000000 | (red Shl 16 | green Shl 8 | blue))
			h :+ hi
		Next
		UnlockImage(image)
		Return image
	End Function

	Method update(x:Int,y:Int)
		xPos = x
		yPos = y
		Local r:Float,g:Float,b:Float
		HSBtoRGB(hue,saturation,brightness,r,g,b)
		red = r*255
		green = g * 255
		blue = b * 255
		
		sbZone.update(MouseX()-x,MouseY()-y)
		hZone.update(MouseX()-x,MouseY()-y)
		buttonZone.update(MouseX()-x,MouseY()-y)
		buttonClicked = False
		
		If MouseDown(1)
			If sbZone.over Or (dragZone <> Null And dragZone.index = sbZone.index)
				dragZone = sbZone
				saturation = clamp((MouseX()-x-sbZone.x)/255.0,0.0,1.0)
				brightness = clamp(1.0-((MouseY()-y-sbZone.y)/255.0),0.0,1.0)
			End If
			
			If hZone.over Or (dragZone <> Null And dragZone.index = hZone.index)
				dragZone = hZone
				hue = clamp(((MouseY()-y-hZone.y)/255.0)*359,0,359)
			End If
			
		Else
			dragZone = Null
		End If

		If (MouseHit(1) And buttonZone.over) Or KeyHit(KEY_ENTER)
			buttonClicked = True
		End If


	End Method
	
	Function clamp:Float(value:Float,minimum:Float,maximum:Float)
		If value > maximum Then value = maximum
		If value < minimum Then value = minimum
		Return value
	End Function	
		
	Method render()
		renderBackground()
		renderColorBox(xPos,yPos)
		renderHueBox(xPos+1+256+24,yPos)
		renderSelectedColor(xPos+1+256+24+24+24,yPos)
		renderValues(xPos+1+256+24+24+24,yPos+1+48+16+8)
		renderButton()
		renderSBMarker()
		renderHMarker()
	End Method

	Method renderBackground()
		SetAlpha .75
		SetBlend ALPHABLEND
		SetColor 188,188,188
		DrawRect xPos-8,yPos-8, 256+96+48,256+16
	    SetBlend LIGHTBLEND
		SetColor 128,128,128
		DrawLine xPos-8,yPos-8, xPos-8+256+96+46, yPos-8
		DrawLine xPos-8,yPos-8, xPos-8, yPos-8+256+14
		SetBlend SHADEBLEND 
		DrawLine xPos-8,yPos-8+256+15, xPos-8+256+96+47, yPos-8+256+15
		DrawLine xPos-8+256+96+47,yPos-8, xPos-8+256+96+47, yPos-8+256+15
	End Method
	
	Method renderHMarker()
	
		Local yo:Int = (hue/360.0)*hZone.h
		Local x:Float = xPos+hZone.x+1
		Local y:Float = yPos+hZone.y+yo
		
		SetColor 0,0,0
		SetAlpha .5		
	    DrawPoly  ([x-1,y, x-8,y-7, x-8,y+7])
	    x = xPos+hZone.x+hZone.w+2
	    DrawPoly  ([x+1,y, x+7,y-7, x+7,y+7])
	
	End Method
	
	Method renderSBMarker()
	
		Local x:Int = xPos+sbZone.x+(saturation*sbZone.w)
		Local y:Int = yPos+sbZone.y+((1.0-brightness)*sbZone.h)
		SetAlpha 1.0
		Local xpoints:Int[] = [-1,1,1,-1,-2,2,0,0]
		Local ypoints:Int[] = [-1,1,-1,1,0,0,-2,2]
		SetColor 0,0,0		
		For Local i:Int = 0 To xpoints.length-1
			Plot x+xpoints[i],y+ypoints[i]
		Next
		xpoints = [-2,2,2,-2,-3,-3,3,3,-1,1,1,-1]
		ypoints = [-2,2,-2,2,-1,1,1,-1,-3,-3,3,3]
		SetColor 255,255,255
		For Local i:Int = 0 To xpoints.length-1
			Plot x+xpoints[i],y+ypoints[i]
		Next		
	
	End Method
	
	Method renderColorBox(x:Int,y:Int)
	    SetBlend SOLIDBLEND
		SetColor 0,0,0
		DrawRect x,y,256+2,256+2
		SetAlpha 1.0

		SetBlend ALPHABLEND
		SetColor 255,255,255
		DrawImageRect gradientImage,x+1+256,y+1,-256,256
		
		Local r:Float,g:Float,b:Float
		HSBtoRGB(hue,1,1,r,g,b)
		SetColor r*255,g*255,b*255
		SetBlend ALPHABLEND
		DrawImageRect gradientImage,x+1,y+1,256,256
		
		SetColor 0,0,0
		SetRotation 270
		SetAlpha 1.0
		DrawImageRect gradientImage,x+1+256,y+1,-256,-256
		
		SetRotation 0

	End Method
	
	Method renderHueBox(x:Int,y:Int)		
		SetBlend SOLIDBLEND
		SetColor 0,0,0
		DrawRect x,y,24+2,256+2		
		SetRotation 90
		SetColor 255,255,255
		DrawImageRect hueImage, x+1+24,y+1,256,24
		SetRotation 0
	End Method
	
	Method renderSelectedColor(x:Int,y:Int,w:Int=48,h:Int=48)
		SetBlend SOLIDBLEND
		SetColor 0,0,0
		DrawRect x,y,w+2,h+2
		SetColor red,green,blue
		DrawRect x+1,y+1,w,h			
	End Method
	
	Method renderValues(x:Int,y:Int)
		SetBlend ALPHABLEND
		Local height:Int = 20
		SetColor 0,0,0
		SetAlpha .75
		DrawText "H",x,y
		DrawText "S",x,y+height
		DrawText "B",x,y+height*2
		DrawText "R",x,y+height*3
		DrawText "G",x,y+height*4
		DrawText "B",x,y+height*5
		
		renderValue(x+16,y,          49-16, height-4, hue)
		renderValue(x+16,y+height,   49-16, height-4, saturation*100)
		renderValue(x+16,y+height*2, 49-16, height-4, brightness*100)
		renderValue(x+16,y+height*3, 49-16, height-4, red, 255,0,0)
		renderValue(x+16,y+height*4, 49-16, height-4, green, 0,255,0)
		renderValue(x+16,y+height*5, 49-16, height-4, blue, 0,0,255)

	End Method
	
	Method renderButton()
		SetBlend ALPHABLEND
		SetColor 0,0,0
		DrawRect xPos+buttonZone.x-1,yPos+buttonZone.y-1,buttonZone.w+2,buttonZone.h+2
		SetColor 255,255,255
		SetAlpha 0.5
		SetRotation 90
		DrawImageRect gradientImage,xPos+buttonZone.x+buttonZone.w,yPos+buttonZone.y,buttonZone.w,buttonZone.h

		SetRotation 0
		SetAlpha .5
		SetColor 255,255,255
		DrawText "OK", xPos+buttonZone.x+16,yPos+buttonZone.y+buttonZone.h-32
	End Method
	
	Method renderValue(x:Int,y:Int, w:Int,h:Int, value:Int, r:Int=255,g:Int=255,b:Int=255)
		SetBlend LIGHTBLEND
		SetColor r,g,b
		SetAlpha .15
		DrawRect x,y-2,w,h
		SetAlpha .5
		DrawImageRect gradientImage,x+1+(w-2),y+1-2,-(w-2),h-2
		SetBlend ALPHABLEND
		SetAlpha .5
		SetColor 0,0,0
		DrawText value, x+2,y
	End Method
	
	Method setFromRGB(r:Int,g:Int,b:Int)
		
		Local h:Float,s:Float,v:Float
		RGBtoHSB(r,g,b,h,s,v)
		hue = h
		saturation = s
		brightness = v/255.0		
		update(xPos,yPos)		
		
	End Method
	
	Method RGBtoHSB(r:Float, g:Float, b:Float, h:Float Var, s:Float Var, v:Float Var )
		Local mn!, mx!, dif!, ad!, dv!, md!
		If ( r < g And r < b )
			mn = r
		Else If ( g < b )
			mn = g
		Else
			mn = b
		EndIf

		If ( r > g And r > b )
			mx = r
			dif = g-b
			ad = 0!
		Else If ( g > b )
			dif = b-r
			mx = g
			ad = 120!
		Else
			dif = r-g
			ad = 240!
			mx = b
		EndIf
		
		md = mx-mn

		h = (60!*(dif / md))+ad
		s = md/mx
		v = mx
	End Method

	
	Function HSBtoRGB(h:Float, s:Float, v:Float, r:Float Var, g:Float Var, b:Float Var) 
 
	    Local i:Int 
	    Local f:Float, p:Float, q:Float, t:Float,hTemp:Float 
	  
	    If s = 0.0 Or h = -1.0   ' s==0? Totally unsaturated = grey so R,G And B all equal value 
	      	b = v
		 	g = v
		 	r = v 
	    	Return 
	    EndIf
	 
	    hTemp = h/60.0 
	    i = Floor(hTemp)                ' which sector 
	    f = hTemp - i                   ' how far through sector 
	    p = v * ( 1 - s ) 
	    q = v * ( 1 - s * f ) 
	    t = v * ( 1 - s * ( 1 - f ) ) 
	  
	    Select i  
	    	Case 0 r = v ; g = t ; b = p 
	    	Case 1 r = q ; g = v ; b = p 
	    	Case 2 r = p ; g = v ; b = t 
	    	Case 3 r = p ; g = q ; b = v
	    	Case 4 r = t ; g = p ; b = v 
	    	Case 5 r = v ; g = p ;b = q 
	    End Select 
	End Function 

End Type

Type TZone

	Field x:Int,y:Int,w:Int,h:Int
	Field index:Int = -1
	Field over:Int = False

	Function Create:TZone(x:Int=0,y:Int=0,w:Int=0,h:Int=0,index:Int=-1)
		Local tz:TZone = New TZone
		tz.x = x
		tz.y = y
		tz.w = w
		tz.h = h
		tz.index = index
		Return tz
	End Function
	
	Method update(mx:Int, my:Int)
		isOver(mx,my)
	End Method
	
	Method isOver:Int(mx:Int, my:Int)
		over = (mx >= x And mx < x+w And my >= y And my < y+h)
		Return over
	End Method

End Type

' -----------------------------------------------------------------
' Example usage
' -----------------------------------------------------------------
'Rem

Graphics 800,600

Local picker:TColorPicker = New TColorPicker.Create()
Local x:Int=200,y:Int=200
Local r:Int = 55, g:Int = 66, b:Int = 89
picker.setFromRGB(r,g,b)

While Not KeyHit(KEY_ESCAPE)
	Cls
	SetBlend SOLIDBLEND

	SetColor r,g,b
	DrawRect 0,0,GraphicsWidth(),GraphicsHeight()
	picker.update(x,y)
	picker.render()
	x :+ (KeyDown(KEY_RIGHT)-KeyDown(KEY_LEFT))
	y :+ (KeyDown(KEY_DOWN)-KeyDown(KEY_UP))
	If picker.buttonClicked 
		r = picker.red
		g = picker.green
		b = picker.blue
	End If
	SetBlend ALPHABLEND
	SetAlpha 1.0
	SetColor r~255,g~255,b~255
	DrawText "Hue Saturation Brightness RGB Colour Picker Example",0,0
	
	Flip
Wend

End

'End Rem
