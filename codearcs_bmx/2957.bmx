; ID: 2957
; Author: Pineapple
; Date: 2012-07-02 15:17:14
; Title: Pixel/Color blend modes
; Description: 144 different ways to draw one pixel on top of another

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--


' While this code is public domain, I really did work quite hard on it and would appreciate if you didn't claim undue credit
' Standard Photoshop algorithms were used whereever available.
' You can view a complete summary of blending modes at https://dl.dropbox.com/u/10116881/program/blendexamples/blitzpage/summary.txt


Rem

View graphic representations: 
https://dl.dropbox.com/u/10116881/program/blendexamples/blitzpage/index.htm

View time comparisons: 
https://dl.dropbox.com/u/10116881/program/blendexamples/blitzpage/times.txt

View descriptions of each blend mode:
https://dl.dropbox.com/u/10116881/program/blendexamples/blitzpage/summary.txt

The following code archive entries are imported:
colorspace.bmx
http://blitzbasic.com/codearcs/codearcs.php?code=2953 (alt. https://dl.dropbox.com/u/10116881/blitz/code/colorspace.bmx)

Blend modes are handled in such a way that adding your own is very trivial. A blend function is in the format: 
PixelBlend_MyBlendFunc ( top_red:Int , top_green:Int , top_blue:Int , bottom_red:Int , bottom_green:Int , bottom_blue:Int , resulting_red:Int Var , resulting_green:Int Var , resulting_blue:Int Var )

Then simply use it via SetPixelBlend ( PixelBlend_MyBlendFunc )

PixelBlend_Alpha is the default blend mode.

You can also isolate the effects of a blend mode to any combination of the RGB channels and prevent the alpha channel from being altered. Unaffected channels will use the bottom value. Here's an example:

SetPixelBlend ( PixelBlend_Alpha , PBCHANNELS_R | PBCHANNELS_G )

Which would include the A, R, and G channels.

PBCHANNELS_ONLYR | PBCHANNELS_ONLYG would include R and G without A.

All the available channels are PBCHANNELS_NONE, PBCHANNELS_R , PBCHANNELS_G , PBCHANNELS_B , PBCHANNELS_ONLYR , PBCHANNELS_ONLYG , PBCHANNELS_ONLYB , PBCHANNELS_A , PBCHANNELS_RGB , PBCHANNELS_ALL. The default is PBCHANNELS_ALL. It is not recommended that you exclude the alpha channel.

When calling WritePixelAlpha or BlendPixels, you can use the mask argument as an extra alpha argument. The default value, 255, is exactly as opaque as the source pixel. You can increase or decrease the value to scale it up or down.

EndRem


SuperStrict
Import brl.pixmap
Import "colorspace.bmx" 	' http://blitzbasic.com/codearcs/codearcs.php?code=2953


' example program
Rem

SetPixelBlend PixelBlend_Multiply ' try experimenting with different blend modes by placing them here!

' first, let's load an example image to use
Local base:TPixmap=LoadPixmap(LoadBank("http::dl.dropbox.com/u/10116881/program/blendexamples/giftbox.png"))
Assert base,"Failed to load example image! You might not be connected to the internet, or the dropbox server might be down."
' and now let's make the pixmap we want to overlay on top of it
Local gradient:TPixmap=CreatePixmap(base.width,base.height,PF_RGBA8888)
For Local x%=0 Until gradient.width
For Local y%=0 Until gradient.height
	gradient.WritePixel x,y,$ff00ff00|((Int(x*255/Float(gradient.width))) Shl 16)|(255-Int(y*255/Float(gradient.width)))
Next
Next
' and blend the first onto the bottom using the blend mode specified just a bit ago
Local blended:TPixmap=CopyPixmap(base)
For Local x%=0 Until gradient.width
For Local y%=0 Until gradient.height
	WritePixelAlpha blended,x,y,gradient.ReadPixel(x,y)
Next
Next

' make the graphics window
AppTitle="Pixel blend example"
Graphics base.width*2,base.height*2
Repeat
	Cls
	DrawPixmap blended,base.width/2,0
	DrawPixmap base,0,base.height
	DrawPixmap gradient,base.width,base.height
	Flip
	If KeyDown(key_escape) Or AppTerminate() Then End
Forever

EndRem


Global pixblendfunc%(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)=PixelBlend_Alpha

Function SetPixelBlend(blendfunc%(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var),channels%=PBCHANNELS_ALL)
	pixblendfunc=blendfunc
	PBCHANNELS=channels
End Function

Function WritePixelAlpha(target:TPixmap,x%,y%,argb%,mask%=$ff)
	Local on%=target.ReadPixel(x,y)
	target.WritePixel x,y,BlendPixels(argb,on,mask)
End Function

Const PBCHANNELS_NONE%=	%0000
Const PBCHANNELS_R%=	%1100
Const PBCHANNELS_G%=	%1010
Const PBCHANNELS_B%=	%1001
Const PBCHANNELS_ONLYR%=	%0100
Const PBCHANNELS_ONLYG%=	%0010
Const PBCHANNELS_ONLYB%=	%0001
Const PBCHANNELS_A%=	%1000
Const PBCHANNELS_RGB%=	%0111
Const PBCHANNELS_ALL%=	%1111
Global PBCHANNELS%=PBCHANNELS_ALL

Function BlendPixels%(argb1%,argb2%,mask%=$ff,channels%=-1)
	If channels=-1 Then channels=PBCHANNELS
	Local a1%=(argb1 Shr 24)
	If mask=0 
		Return argb2
	ElseIf mask<$ff
		a1=mask*a1/255
	ElseIf mask>$ff
		a1=mask*a1/255
		If a1>$ff Then a1=$ff
	EndIf
	If a1=0 Or channels=PBCHANNELS_NONE Then Return argb2
	Local alpha%,a2%=(argb2 Shr 24)
	If channels&PBCHANNELS_A Then
		alpha=$ff-((($ff-a1)*($ff-a2))/255)
	Else
		alpha=a2
	EndIf
	Local ret_r%,ret_g%,ret_b%
	Local on_r%=(argb2 Shr 16)&$ff
	Local on_g%=(argb2 Shr 8)&$ff
	Local on_b%=argb2&$ff
	If channels&PBCHANNELS_RGB Then
		pixblendfunc(	(argb1 Shr 16)&$ff,(argb1 Shr 8)&$ff,argb1&$ff, ..
				on_r,on_g,on_b, ..
				ret_r,ret_g,ret_b )
		Local as%=$ff-a1
		If channels&PBCHANNELS_ONLYR Then
			ret_r=(ret_r*a1+on_r*as)/255 
		Else 
			ret_r=on_r
		EndIf
		If channels&PBCHANNELS_ONLYG Then
			ret_g=(ret_g*a1+on_g*as)/255
		Else
			ret_g=on_g
		EndIf
		If channels&PBCHANNELS_ONLYB Then
			ret_b=(ret_b*a1+on_b*as)/255
		Else
			ret_b=on_b
		EndIf
	Else
		ret_r=on_r
		ret_g=on_g
		ret_b=on_b
	EndIf
	Return (alpha Shl 24)|(ret_r Shl 16)|(ret_g Shl 8)|ret_b
End Function

' Function PixelBlend_Template(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)

Function PixelBlend_Alpha(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=r1
	g=g1
	b=b1
End Function

Function PixelBlend_Lighten(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Max(r1,r2)
	g=Max(g1,g2)
	b=Max(b1,b2)
End Function

Function PixelBlend_Darken(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Min(r1,r2)
	g=Min(g1,g2)
	b=Min(b1,b2)
End Function

Function PixelBlend_Exclusion(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=r1+r2-r1*r2/127.5
	g=g1+g2-g1*g2/127.5
	b=b1+b2-b1*b2/127.5
End Function

Function PixelBlend_Difference(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Abs(r1-r2)
	g=Abs(g1-g2)
	b=Abs(b1-b2)
End Function

Function PixelBlend_Contrast(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Max(0,Min(255,r2+r1-127))
	g=Max(0,Min(255,g2+g1-127))
	b=Max(0,Min(255,b2+b1-127))
End Function

Function PixelBlend_HardContrast(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	If r2>127
		r=Max(0,Min(255,r2+r1-127))
	Else
		r=Max(0,Min(255,r2-r1+127))
	EndIf
	If g2>127
		g=Max(0,Min(255,g2+g1-127))
	Else
		g=Max(0,Min(255,g2-g1+127))
	EndIf
	If b2>127
		b=Max(0,Min(255,b2+b1-127))
	Else
		b=Max(0,Min(255,b2-b1+127))
	EndIf
End Function

Function PixelBlend_Multiply(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=r1*r2/255
	g=g1*g2/255
	b=b1*b2/255
End Function

Function PixelBlend_Screen(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=r1+r2-r1*r2/255
	g=g1+g2-g1*g2/255
	b=b1+b2-b1*b2/255
End Function

Function PixelBlend_Overlay(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	If r2<128
		r=2*r1*r2/255
	Else
		r=(255-2*(255-r1)*(255-r2)/255)
	EndIf
	If g2<128
		g=2*g1*g2/255
	Else
		g=(255-2*(255-g1)*(255-g2)/255)
	EndIf
	If b2<128
		b=2*b1*b2/255
	Else
		b=(255-2*(255-b1)*(255-b2)/255)
	EndIf
End Function

Function PixelBlend_Softlight(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	If r1>127.5
		r=r2+(255-r2)*((r1-127.5)/127.5)*(0.5-Abs(r1-127.5)/255.0)
	Else
		r=r2-r2*((127.5-r1)/127.5)*(0.5-Abs(r2-127.5)/255.0)
	EndIf
	If g1>127.5
		g=g2+(255-g2)*((g1-127.5)/127.5)*(0.5-Abs(g1-127.5)/255.0)
	Else
		g=g2-g2*((127.5-g1)/127.5)*(0.5-Abs(g2-127.5)/255.0)
	EndIf
	If b1>127.5
		b=b2+(255-b2)*((b1-127.5)/127.5)*(0.5-Abs(b1-127.5)/255.0)
	Else
		b=b2-b2*((127.5-b1)/127.5)*(0.5-Abs(b2-127.5)/255.0)
	EndIf
End Function

Function PixelBlend_HardLight(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	If r1<128
		r=2*r1*r2/255
	Else
		r=(255-2*(255-r1)*(255-r2)/255)
	EndIf
	If g1<128
		g=2*g1*g2/255
	Else
		g=(255-2*(255-g1)*(255-g2)/255)
	EndIf
	If b1<128
		b=2*b1*b2/255
	Else
		b=(255-2*(255-b1)*(255-b2)/255)
	EndIf
End Function

Function PixelBlend_Dodge(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	If r1=255 r=255 Else r=Min(255,((r2 Shl 8)/(255-r1)))
	If g1=255 g=255 Else g=Min(255,((g2 Shl 8)/(255-g1)))
	If b1=255 b=255 Else b=Min(255,((b2 Shl 8)/(255-b1)))
End Function

Function PixelBlend_Burn(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	If r1=0 r=0 Else r=Max(0,(255-((255-r2) Shl 8)/r1))
	If g1=0 g=0 Else g=Max(0,(255-((255-g2) Shl 8)/g1))
	If b1=0 b=0 Else b=Max(0,(255-((255-b2) Shl 8)/b1))
End Function

Function PixelBlend_ChemicalDodge(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Min(255,r2+Max(0,r2-255+r1))
	g=Min(255,g2+Max(0,g2-255+g1))
	b=Min(255,b2+Max(0,b2-255+b1))
End Function

Function PixelBlend_ChemicalBurn(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Max(0,r2-Max(0,r2-r1))
	g=Max(0,g2-Max(0,g2-g1))
	b=Max(0,b2-Max(0,b2-b1))
End Function

Function PixelBlend_Raise(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Min(255,r2+Min(r1,r2))
	g=Min(255,g2+Min(g1,g2))
	b=Min(255,b2+Min(b1,b2))
End Function

Function PixelBlend_Lower(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Max(0,r2-Min(255-r1,r2))
	g=Max(0,g2-Min(255-g1,g2))
	b=Max(0,b2-Min(255-b1,b2))
End Function

Function PixelBlend_Over(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=((r1*r1)+(r1*r2))/510
	g=((g1*g1)+(g1*g2))/510
	b=((b1*b1)+(b1*b2))/510
End Function

Function PixelBlend_Under(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=((r2*r2)+(r1*r2))/510
	g=((g2*g2)+(g1*g2))/510
	b=((b2*b2)+(b1*b2))/510
End Function

Function PixelBlend_Reflect(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	If r2=255 r=255 Else r=Min(255,r1*r1/(255-r2))
	If g2=255 g=255 Else g=Min(255,g1*g1/(255-g2))
	If b2=255 b=255 Else b=Min(255,b1*b1/(255-b2))
End Function

Function PixelBlend_Reflex(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	If r1=255 r=255 Else r=Min(255,(r2*r2/(255-r1)))
	If g1=255 g=255 Else g=Min(255,(g2*g2/(255-g1)))
	If b1=255 b=255 Else b=Min(255,(b2*b2/(255-b1)))
End Function

Function PixelBlend_Freeze(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=255-r1;g=255-g1;b=255-b1
	If r2=0 r=0 Else r=Max(0,255-r*r/r2)
	If g2=0 g=0 Else g=Max(0,255-g*g/g2)
	If b2=0 b=0 Else b=Max(0,255-b*b/b2)
End Function

Function PixelBlend_Heat(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=255-r2;g=255-g2;b=255-b2
	If r1=0 r=0 Else r=Max(0,255-r*r/r1)
	If g1=0 g=0 Else g=Max(0,255-g*g/g1)
	If b1=0 b=0 Else b=Max(0,255-b*b/b1)
End Function

Function PixelBlend_Singe(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=r2*Abs(255-r1-r2)/255
	g=g2*Abs(255-g1-g2)/255
	b=b2*Abs(255-b1-b2)/255
End Function

Function PixelBlend_PinLight(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	If r1<128
		r=Min(2*r1,r2)
	Else
		r=Max(2*(r1-128),r2)
	EndIf
	If g1<128
		g=Min(2*g1,g2)
	Else
		g=Max(2*(g1-128),g2)
	EndIf
	If b1<128
		b=Min(2*b1,b2)
	Else
		b=Max(2*(b1-128),b2)
	EndIf
End Function

Function PixelBlend_VividLight(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	If r1=0
		r=0
	ElseIf r1=255
		r=255
	ElseIf r1<128
		r=Max(0,(255-((255-r2) Shl 8)/r1))
	Else
		r=Min(255,((r2 Shl 8)/(255-r1)))
	EndIf
	If g1=0
		g=0
	ElseIf g1=255
		g=255
	ElseIf g1<128
		g=Max(0,(255-((255-g2) Shl 8)/g1))
	Else
		g=Min(255,((g2 Shl 8)/(255-g1)))
	EndIf
	If b1=0
		b=0
	ElseIf b1=255
		b=255
	ElseIf b1<128
		b=Max(0,(255-((255-b2) Shl 8)/b1))
	Else
		b=Min(255,((b2 Shl 8)/(255-b1)))
	EndIf
End Function

Function PixelBlend_HardMix(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	PixelBlend_VividLight r1,g1,b1,r2,g2,b2,r,g,b
	If r<128 r=0 Else r=255
	If g<128 g=0 Else g=255
	If b<128 b=0 Else b=255
End Function

Const pbt553%=255*255*255
Function PixelBlend_SoftMix(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=r2*r1*r1*r1/pbt553
	g=g2*g1*r1*g1/pbt553
	b=b2*b1*r1*b1/pbt553
End Function

Function PixelBlend_Additive(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Min(255,r2+r1)
	g=Min(255,g2+g1)
	b=Min(255,b2+b1)
End Function

Function PixelBlend_Subtractive(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Max(0,r2-r1)
	g=Max(0,g2-g1)
	b=Max(0,b2-b1)
End Function

Function PixelBlend_AdditiveInverted(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Min(255,r2+($ff-r1))
	g=Min(255,g2+($ff-g1))
	b=Min(255,b2+($ff-b1))
End Function

Function PixelBlend_SubtractiveInverted(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Max(0,r2-($ff-r1))
	g=Max(0,g2-($ff-g1))
	b=Max(0,b2-($ff-b1))
End Function

Function PixelBlend_Stamp(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Max(0,Min(255,r2+r1+r1-255))
	g=Max(0,Min(255,g2+g1+g1-255))
	b=Max(0,Min(255,b2+b1+b1-255))
End Function

Function PixelBlend_Brush(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local r3%,g3%,b3%
	PixelBlend_BrushUp r1,g1,b1,r2,g2,b2,r3,g3,b3
	PixelBlend_BrushDown r1,g1,b1,r2,g2,b2,r,g,b
	r=(r+r3)/2
	g=(g+g3)/2
	b=(b+b3)/2
End Function

Function PixelBlend_BrushUp(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local l1%=r1*3+g1*6+b1
	Local l2%=r2*3+g2*6+b2
	r=(r1*l2+r2*l1)/5100
	g=(g1*l2+g2*l1)/5100
	b=(b1*l2+b2*l1)/5100
End Function

Function PixelBlend_BrushDown(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local l1%=r1*3+g1*6+b1
	Local l2%=r2*3+g2*6+b2
	r=(r1*l1+r2*l2)/5100
	g=(g1*l1+g2*l2)/5100
	b=(b1*l1+b2*l2)/5100
End Function

Function PixelBlend_Exponentiate(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=r2^(r1/255.0)
	g=g2^(g1/255.0)
	b=b2^(b1/255.0)
End Function

Const log255#=5.5412635451584258
Function PixelBlend_Logarithmic(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Max(0,r2*Log(r1)/log255)
	g=Max(0,g2*Log(g1)/log255)
	b=Max(0,b2*Log(b1)/log255)
End Function

Function PixelBlend_Quench(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=(r2*($ff-r1)+r1*r1)/255
	g=(g2*($ff-g1)+g1*g1)/255
	b=(b2*($ff-b1)+b1*b1)/255
End Function

Function PixelBlend_Fence(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=r2*(r1+r2)/510
	g=g2*(g1+g2)/510
	b=b2*(b1+b2)/510
End Function

Function PixelBlend_Fade(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local s2%=r2+g2+b2,s1%=r1+g1+b1
	r=(r2+s2)*(r1+s1)/4080
	g=(g2+s2)*(g1+s1)/4080
	b=(b2+s2)*(b1+s1)/4080
End Function

Function PixelBlend_Bake(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local s1%=r1+g1+b1
	r=((r2*s1)+(r1*s1))/2040
	g=((g2*s1)+(g1*s1))/2040
	b=((b2*s1)+(b1*s1))/2040
End Function

Function PixelBlend_Broil(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local s2%=r2+g2+b2
	r=((r2*s2)+(r1*s2))/2040
	g=((g2*s2)+(g1*s2))/2040
	b=((b2*s2)+(b1*s2))/2040
End Function

Function PixelBlend_Dress(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Max(0,r2-Abs(r2-r1))
	g=Max(0,g2-Abs(g2-g1))
	b=Max(0,b2-Abs(b2-b1))
End Function

Function PixelBlend_Strip(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Min(255,(r2*Abs(r1-127)) Shr 7)
	g=Min(255,(g2*Abs(g1-127)) Shr 7)
	b=Min(255,(b2*Abs(b1-127)) Shr 7)
End Function

Function PixelBlend_Bless(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Min(255,r2+(r1*r1*r2/65025))
	g=Min(255,g2+(g1*g1*g2/65025))
	b=Min(255,b2+(b1*b1*b2/65025))
End Function

Function PixelBlend_Curse(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r1=255-r1;g1=255-g1;b1=255-b1
	r=Max(0,r2-(r1*r1*(255-r2)/65025))
	g=Max(0,g2-(g1*g1*(255-g2)/65025))
	b=Max(0,b2-(b1*b1*(255-b2)/65025))
End Function

Function PixelBlend_Shine(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Min(255,r2*(r1/127.5))
	g=Min(255,g2*(g1/127.5))
	b=Min(255,b2*(b1/127.5))
End Function

Function PixelBlend_Inhale(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=255-r1;g=255-g1;b=255-b1
	r=Max(0,Min(255,r2-(r)*(r/127.5-1)))
	g=Max(0,Min(255,g2-(g)*(g/127.5-1)))
	b=Max(0,Min(255,b2-(b)*(b/127.5-1)))
End Function

Function PixelBlend_Exhale(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Max(0,Min(255,r2+r1*(r1/127.5-1)))
	g=Max(0,Min(255,g2+g1*(g1/127.5-1)))
	b=Max(0,Min(255,b2+b1*(b1/127.5-1)))
End Function

Function PixelBlend_Breathe(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	PixelBlend_Inhale r1,g1,b1,r2,g2,b2,r1,g1,b1
	PixelBlend_Exhale r1,g1,b1,r2,g2,b2,r,g,b
End Function

Function PixelBlend_Vivify(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Max(0,Min(255,r2+r1+r1-g1-b1))
	g=Max(0,Min(255,g2+g1+g1-r1-b1))
	b=Max(0,Min(255,b2+b1+b1-g1-r1))
End Function

Function PixelBlend_MeanVivify(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Max(0,Min(255,r2+r1-(g1+b1)/2))
	g=Max(0,Min(255,g2+g1-(r1+b1)/2))
	b=Max(0,Min(255,b2+b1-(g1+r1)/2))
End Function

Function PixelBlend_Polish(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	PixelBlend_Overlay r1,g1,b1,r2,g2,b2,r2,g2,b2
	PixelBlend_BrushUp r1,g1,b1,r2,g2,b2,r,g,b
End Function

Function PixelBlend_Wash(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local s%=r1+g1+b1
	r=Min(255,r2+((s+r1) Shr 2))
	g=Min(255,g2+((s+g1) Shr 2))
	b=Min(255,b2+((s+b1) Shr 2))
End Function

Function PixelBlend_Glow(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local r3%,g3%,b3%
	PixelBlend_Shine r1,g1,b1,r2,g2,b2,r3,g3,b3
	PixelBlend_Hardlight r1,g1,b1,r2,g2,b2,r,g,b
	r=Max(r,r3)
	g=Max(g,g3)
	b=Max(b,b3)
End Function

Function PixelBlend_SoftGlow(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	PixelBlend_Softlight r1,g1,b1,r2,g2,b2,r2,g2,b2
	PixelBlend_Screen r1,g1,b1,r2,g2,b2,r,g,b
End Function

Function PixelBlend_HardGlow(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	PixelBlend_Multiply r1,g1,b1,r2,g2,b2,r2,g2,b2
	PixelBlend_Additive r1,g1,b1,r2,g2,b2,r,g,b
End Function

Function PixelBlend_Soften(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	PixelBlend_Overlay r1,g1,b1,r2,g2,b2,r,g,b
	PixelBlend_Softlight r,g,b,r2,g2,b2,r,g,b
End Function

Function PixelBlend_Mean(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=(r1+r2) Shr 1
	g=(g1+g2) Shr 1
	b=(b1+b2) Shr 1
End Function

Function PixelBlend_Modulo(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=r2 Mod (r1+1)
	g=g2 Mod (g1+1)
	b=b2 Mod (b1+1)
End Function

Function PixelBlend_Multiplicative(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Min(255,r1*r2)
	g=Min(255,g1*g2)
	b=Min(255,b1*b2)
End Function

Const PBAng#=180#/256#

Function PixelBlend_Sine(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Abs(Sin(r1*PBAng))*r2
	g=Abs(Sin(g1*PBAng))*g2
	b=Abs(Sin(b1*PBAng))*b2
End Function

Function PixelBlend_Cosine(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Abs(Cos(r1*PBAng))*r2
	g=Abs(Cos(g1*PBAng))*g2
	b=Abs(Cos(b1*PBAng))*b2
End Function

Function PixelBlend_Tangent(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Min(255,Max(0,r2+Tan(r1*PBAng-90)*32))
	g=Min(255,Max(0,g2+Tan(g1*PBAng-90)*32))
	b=Min(255,Max(0,b2+Tan(b1*PBAng-90)*32))
End Function

Function PixelBlend_SoftTangent(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Min(255,Max(0,r2+Tan(r1*PBAng-90)*8))
	g=Min(255,Max(0,g2+Tan(g1*PBAng-90)*8))
	b=Min(255,Max(0,b2+Tan(b1*PBAng-90)*8))
End Function

Function PixelBlend_HardTangent(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Min(255,Max(0,r2+Tan(r1*PBAng-90)*128))
	g=Min(255,Max(0,g2+Tan(g1*PBAng-90)*128))
	b=Min(255,Max(0,b2+Tan(b1*PBAng-90)*128))
End Function

Function PixelBlend_Intensify(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Min(255,r2+((r1*r2) Shr 8))
	g=Min(255,g2+((g1*g2) Shr 8))
	b=Min(255,b2+((b1*b2) Shr 8))
End Function

Function PixelBlend_Detensify(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Max(0,r2-(255-((r1*r2) Shr 8)))
	g=Max(0,g2-(255-((g1*g2) Shr 8)))
	b=Max(0,b2-(255-((b1*b2) Shr 8)))
End Function

Function PixelBlend_DetensifyInverted(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=Max(0,r2-((r1*r2) Shr 8))
	g=Max(0,g2-((g1*g2) Shr 8))
	b=Max(0,b2-((b1*b2) Shr 8))
End Function

Function PixelBlend_Hue(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohsl([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohsl([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hsltorgb([val1[0],val2[1],val2[2]])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_Saturation(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohsl([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohsl([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hsltorgb([val2[0],val1[1],val2[2]])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_Saturate(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohsl([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohsl([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hsltorgb([val2[0],Min(1,val2[1]+val1[1]),val2[2]])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_Desaturate(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohsl([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohsl([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hsltorgb([val2[0],Max(0,val2[1]-(1-val1[1])),val2[2]])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_DesaturateInverted(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohsl([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohsl([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hsltorgb([val2[0],Max(0,val2[1]-val1[1]),val2[2]])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_MixSaturation(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohsl([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohsl([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hsltorgb([val2[0],(val2[1]+val1[1])/2.0,val2[2]])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_Lightness(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohsl([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohsl([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hsltorgb([val2[0],val2[1],val1[2]])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_MixLightness(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohsl([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohsl([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hsltorgb([val2[0],val2[1],(val2[2]+val1[2])/2.0])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_Value(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohsv([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohsv([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hsvtorgb([val2[0],val2[1],val1[2]])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_MinValue(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohsv([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohsv([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hsvtorgb([val2[0],val2[1],Min(val2[2],val1[2])])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_MaxValue(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohsv([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohsv([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hsvtorgb([val2[0],val2[1],Max(val2[2],val1[2])])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_MixValue(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohsv([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohsv([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hsvtorgb([val2[0],val2[1],(val2[2]+val1[2])/2.0])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_Chroma(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohcl([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohcl([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hcltorgb([val2[0],val1[1],val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_Color(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohsl([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohsl([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hsltorgb([val1[0],val1[1],val2[2]])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_ChromaColor(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohcl([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohcl([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hcltorgb([val1[0],val1[1],val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_Grayness(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohsl([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohsl([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hsltorgb([val2[0],val1[1],val1[2]])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_ChromaGrayness(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohcl([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohcl([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hcltorgb([val2[0],val1[1],val1[2]])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_Shade(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohsl([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohsl([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hsltorgb([val1[0],val2[1],val1[2]])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_ChromaShade(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohcl([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohcl([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hcltorgb([val1[0],val2[1],val1[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_Illuminative(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohsl([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohsl([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hsltorgb([val2[0],val2[1],Min(1,val2[2]+val1[2])])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_Deluminative(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohsl([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohsl([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hsltorgb([val2[0],val2[1],Max(0,val2[2]-(1-val1[2]))])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_DeluminativeInverted(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtohsl([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtohsl([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=hsltorgb([val2[0],val2[1],Max(0,val2[2]-val1[2])])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_Cyan(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtocmyk([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtocmyk([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=cmyktorgb([val2[0],val1[1],val1[2],val2[3]])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_Magenta(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtocmyk([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtocmyk([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=cmyktorgb([val1[0],val2[1],val1[2],val2[3]])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_Yellow(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtocmyk([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtocmyk([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=cmyktorgb([val1[0],val1[1],val2[2],val2[3]])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_Black(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtocmyk([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtocmyk([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=cmyktorgb([val2[0],val2[1],val2[2],val1[3]])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_BlackAndCyan(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtocmyk([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtocmyk([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=cmyktorgb([val2[0],val1[1],val1[2],val1[3]])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_BlackAndMagenta(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtocmyk([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtocmyk([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=cmyktorgb([val1[0],val2[1],val1[2],val1[3]])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_BlackAndYellow(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtocmyk([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtocmyk([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=cmyktorgb([val1[0],val1[1],val2[2],val1[3]])
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_CIEX(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoxyz([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoxyz([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=xyztorgb([val1[0],val2[1],val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_CIEY(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoxyz([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoxyz([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=xyztorgb([val2[0],val1[1],val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_CIEZ(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoxyz([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoxyz([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=xyztorgb([val2[0],val2[1],val1[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_CIEXY(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoxyz([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoxyz([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=xyztorgb([val1[0],val1[1],val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_CIEYZ(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoxyz([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoxyz([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=xyztorgb([val2[0],val1[1],val1[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_CIEXZ(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoxyz([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoxyz([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=xyztorgb([val1[0],val2[1],val1[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_XYZMultiply(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoxyz([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoxyz([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=xyztorgb([val1[0]*val2[0],val1[1]*val2[1],val1[2]*val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_XYZAdditive(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoxyz([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoxyz([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=xyztorgb([val1[0]+val2[0],val1[1]+val2[1],val1[2]+val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_XYZSubtractiveInverted(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoxyz([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoxyz([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=xyztorgb([val2[0]-(1-val1[0]),val2[1]-(1-val1[1]),val2[2]-(1-val1[2])])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_LabL(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtolab([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtolab([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=labtorgb([val1[0],val2[1],val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_LabA(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtolab([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtolab([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=labtorgb([val2[0],val1[1],val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_LabB(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtolab([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtolab([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=labtorgb([val2[0],val2[1],val1[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_LabLA(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtolab([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtolab([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=labtorgb([val1[0],val1[1],val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_LabAB(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtolab([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtolab([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=labtorgb([val2[0],val1[1],val1[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_LabLB(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtolab([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtolab([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=labtorgb([val1[0],val2[1],val1[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_LabColorAdditive(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtolab([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtolab([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=labtorgb([(val1[0]+val2[0])/2.0,val1[1]+val2[1],val1[2]+val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_Luma(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoyiq([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoyiq([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=yiqtorgb([val1[0],val2[1],val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_LumaAdditive(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoyiq([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoyiq([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=yiqtorgb([val2[0]+val1[0],val2[1],val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_LumaSubtractive(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoyiq([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoyiq([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=yiqtorgb([val2[0]-val1[0],val2[1],val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_Crave(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoyiq([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoyiq([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=yiqtorgb([val1[0]*val2[0],(val2[1]+val1[1])/2.0,(val2[2]+val1[2])/2.0])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_InPhase(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoyiq([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoyiq([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=yiqtorgb([val2[0],val1[1],val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_Quadrature(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoyiq([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoyiq([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=yiqtorgb([val2[0],val2[1],val1[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_Liven(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	PixelBlend_Quadrature r1,g1,b1,r2,g2,b2,r2,g2,b2
	PixelBlend_Multiply r1,g1,b1,r2,g2,b2,r,g,b
End Function

Function PixelBlend_InPhaseAdditive(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoyiq([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoyiq([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=yiqtorgb([val2[0],val1[1]+val2[1],val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_QuadratureAdditive(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoyiq([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoyiq([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=yiqtorgb([val2[0],val2[1],val1[2]+val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_LumaAndInPhase(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoyiq([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoyiq([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=yiqtorgb([val1[0],val1[1],val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_LumaAndQuadrature(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoyiq([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoyiq([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=yiqtorgb([val1[0],val2[1],val1[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_YIQColor(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoyiq([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoyiq([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=yiqtorgb([val2[0],val1[1],val1[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_UChrominance(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoyuv([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoyuv([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=yuvtorgb([val2[0],val1[1],val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_VChrominance(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoyuv([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoyuv([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=yuvtorgb([val2[0],val2[1],val1[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_UChrominanceAdditive(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoyuv([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoyuv([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=yuvtorgb([val2[0],val1[1]+val2[1],val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_VChrominanceAdditive(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoyuv([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoyuv([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=yuvtorgb([val2[0],val2[1],val1[2]+val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_LumaAndUChrominance(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoyuv([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoyuv([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=yuvtorgb([val1[0],val1[1],val2[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_LumaAndVChrominance(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoyuv([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoyuv([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=yuvtorgb([val1[0],val2[1],val1[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_YUVColor(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoyuv([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoyuv([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=yuvtorgb([val2[0],val1[1],val1[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_YIQChannelSwap(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoyiq([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoyiq([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=yiqtorgb([val2[0],val2[2]+val1[1],val2[1]+val1[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Function PixelBlend_YUVChannelSwap(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local val1#[]=rgbtoyuv([r1/255.0,g1/255.0,b1/255.0])
	Local val2#[]=rgbtoyuv([r2/255.0,g2/255.0,b2/255.0])
	Local ret#[]=yuvtorgb([val2[0],val2[2]+val1[1],val2[1]+val1[2]])
	PBLimRGB ret
	r=ret[0]*255;g=ret[1]*255;b=ret[2]*255
End Function

Const pbdiv7255#=0.0274509806
Function PixelBlend_Reduce(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	Local rv%=Int(r1*pbdiv7255)
	Local gv%=Int(g1*pbdiv7255)
	Local bv%=Int(b1*pbdiv7255)
	r=(r2 Shr rv) Shl rv
	g=(g2 Shr gv) Shl gv
	b=(b2 Shr bv) Shl bv
End Function

Function PixelBlend_And(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=r1 & r2
	g=g1 & g2
	b=b1 & b2
End Function

Function PixelBlend_Or(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=r1 | r2
	g=g1 | g2
	b=b1 | b2
End Function

Function PixelBlend_Xor(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=r1 ~ r2
	g=g1 ~ g2
	b=b1 ~ b2
End Function

Function PixelBlend_Nand(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=$ff~(r1 & r2)
	g=$ff~(g1 & g2)
	b=$ff~(b1 & b2)
End Function

Function PixelBlend_Nor(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=$ff~(r1 | r2)
	g=$ff~(g1 | g2)
	b=$ff~(b1 | b2)
End Function

Function PixelBlend_Xnor(r1%,g1%,b1%,r2%,g2%,b2%,r% Var,g% Var,b% Var)
	r=$ff~(r1 ~ r2)
	g=$ff~(g1 ~ g2)
	b=$ff~(b1 ~ b2)
End Function

Function PBLimRGB(rgb#[])
	rgb[0]=Min(1,Max(0,rgb[0]))
	rgb[1]=Min(1,Max(0,rgb[1]))
	rgb[2]=Min(1,Max(0,rgb[2]))
End Function
