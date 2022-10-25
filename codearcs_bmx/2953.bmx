; ID: 2953
; Author: Pineapple
; Date: 2012-06-22 14:32:39
; Title: Color space conversion
; Description: Convert colors to and from RGB, CMY, CMYK, HSV, HSL, HCL, YIQ, YUV, CIE XYZ, and CIE L*ab

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--


' Usage:
'  1:
'   Use FloatRGB:Float[] ( red:Int , green:Int, blue:Int ) to convert your
'   color to a form that the conversion functions can best work with.
'  2:
'   Call your function rgbto___:Float[] ( ___:Float[] ) using that returned
'   value to get it in your desired color space. (Where "___" is the color
'   space you want to convert to)
'  3:
'   Modify the values in that returned array however you like
'  4:
'   Use SetColorRGB ( rgb:Float[] ) to set it as a color, or 
'   ___torgb:Float[] to conver it back to the rgb colorspace and then
'   IntRGB:Int[] ( rgb:Float[] ) or ByteRGB:Byte[] ( rgb:Float[] ) or
'   GetR:Int( rgb:Float[] ) and GetG:Int( rgb:Float[] ) and
'   GetB:Int( rgb:Float[] ) to get the new red,green,blue values
'  Ranges:
'   RGB: [0,1]
'   CMY: [0,1]
'   CMYK: [0,1]
'   HSV: [0,1]
'   HSL: [0,1]
'   HCL: [0,1]
'   YIQ: [-1,1]
'   YUV: [-1,1]
'   XYZ: [0,1]
'   L*ab: [0,100] for L*, a and b are theoretically unbounded (I think, 
'                 I could very well be interpreting this stuff wrong)


SuperStrict

' example program
Rem 
AppTitle="Color spaces"
Graphics 320,240
Local val#[]=[.5,.5,.5],s#=0.005
Repeat
	setcolorrgb hsvtorgb(val)
	DrawRect 0,0,64,240
	setcolorrgb hsltorgb(val)
	DrawRect 64,0,64,240
	setcolorrgb hcltorgb(val)
	DrawRect 128,0,64,240
	setcolorrgb xyztorgb(val)
	DrawRect 192,0,64,240
	setcolorrgb cmytorgb(val)
	DrawRect 256,0,64,240
	
	DrawText_ "hsv",2,220
	DrawText_ "hsl",66,220
	DrawText_ "hcl",130,220
	DrawText_ "xyz",194,220
	DrawText_ "cmy",258,220
	
	DrawText_ "value: "+Left(val[0],5)+","+Left(val[1],5)+","+Left(val[2],5),2,0
	DrawText_ "a,z,s,x,d,c keys to alter the values",2,16
	
	If KeyDown(key_a) Then val[0]:+s
	If KeyDown(key_z) Then val[0]:-s
	If KeyDown(key_s) Then val[1]:+s
	If KeyDown(key_x) Then val[1]:-s
	If KeyDown(key_d) Then val[2]:+s
	If KeyDown(key_c) Then val[2]:-s
	For Local x%=0 To 2
		If val[x]>1 val[x]=1
		If val[x]<0 val[x]=0
	Next
	Flip
Until KeyDown(27) Or AppTerminate()

Function DrawText_(str$,x%,y%)
	SetColor 0,0,0
	DrawText str,x,y+1
	DrawText str,x+1,y
	DrawText str,x,y-1
	DrawText str,x-1,y
	DrawText str,x+1,y+1
	DrawText str,x-1,y-1
	DrawText str,x+1,y-1
	DrawText str,x-1,y+1
	SetColor 255,255,255
	DrawText str,x,y
End Function
End Rem

' For your convenience, SetColor for an array containing 3 floats indicating rgb
Function SetColorRGB(rgb#[])
	SetColorRGBv rgb[0],rgb[1],rgb[2]
End Function

' SetColor for 3 floats indicating rgb
Function SetColorRGBv(br#,bg#,bb#)
	Local r%=br*255,g%=bg*255,b%=bb*255
	If r<0 r=0
	If g<0 g=0
	If b<0 b=0
	If r>255 r=255
	If g>255 g=255
	If b>255 b=255
	SetColor r,g,b
End Function

' Turn a float array indicating RGB into a byte array
Function ByteRGB@[](rgb#[])
	Return [Byte(rgb[0]*255),Byte(rgb[1]*255),Byte(rgb[2]*255)]
End Function

' Turn a float array indicating RGB into an int array
Function IntRGB%[](rgb#[])
	Return [Int(rgb[0]*255),Int(rgb[1]*255),Int(rgb[2]*255)]
End Function

' Turn rgb into a float array
Function FloatRGB#[](red%,green%,blue%)
	Return [red/255.0,green/255.0,blue/255.0]
End Function

Function FloatARGB#[](argb%)
	Return [((argb Shr 16)&$ff)/255.0,((argb Shr 8)&$ff)/255.0,(argb&$ff)/255.0]
End Function

' Get individual channels from float array
Function GetR%(rgb#[])
	Return rgb[0]*255
End Function
Function GetG%(rgb#[])
	Return rgb[0]*255
End Function
Function GetB%(rgb#[])
	Return rgb[0]*255
End Function

' the following code was adapted from the examples found at:
' http://easyrgb.com/index.php?X=MATH&H=02#text2

' observer = 2 degrees, illuminant = D65
Const refx#=95.047
Const refy#=100.0
Const refz#=108.883

' assumes rgb in the range [0,1]
' returns xyz in the range [0,1]
Function rgbtoxyz#[](rgb#[])
	Local r#=rgb[0],g#=rgb[1],b#=rgb[2]
	If(r>0.04045) Then 
		r=((r+0.055)/1.055)^2.4
	Else
		r:/12.92
	EndIf
	If(g>0.04045) Then 
		g=((g+0.055)/1.055)^2.4
	Else
		g:/12.92
	EndIf
	If(b>0.04045) Then 
		b=((b+0.055)/1.055)^2.4
	Else
		b:/12.92
	EndIf
	r:*100
	g:*100
	b:*100
	Return [(r*0.4124+g*0.3576+b*0.1805)/refx,(r*0.2126+g*0.7152+b*0.0722)/refy,(r*0.0193+g*0.1192+b*0.9505)/refz]
End Function
' assumes xyz in the range [0,1]
' returns rgb in the range [0,1]
Function xyztorgb#[](xyz#[])
	Local x#=xyz[0]*refx/100.0,y#=xyz[1]*refy/100.0,z#=xyz[2]*refz/100.0
	Local r#=x* 3.2406+y*-1.5372+z*-0.4986
	Local g#=x*-0.9689+y* 1.8758+z* 0.0415
	Local b#=x* 0.0557+y*-0.2040+z* 1.0570
	If r>0.0031308 Then
		r=1.055*(r^(0.416666667))-.055
	Else
		r:*12.92
	EndIf
	If g>0.0031308 Then
		g=1.055*(g^(0.416666667))-.055
	Else
		g:*12.92
	EndIf
	If b>0.0031308 Then
		b=1.055*(b^(0.416666667))-.055
	Else
		b:*12.92
	EndIf
	Return [r,g,b]
End Function
' assumes xyz in the range [0,1]
' returns l*ab in a very varying range
Function xyztolab#[](xyz#[])
	Local x#=xyz[0],y#=xyz[1],z#=xyz[2]
	If(x>0.008856) Then
		x=x^0.333333333
	Else
		x=(x*7.787)+0.137931034
	EndIf
	If(y>0.008856) Then
		y=y^0.333333333
	Else
		y=(y*7.787)+0.137931034
	EndIf
	If(z>0.008856) Then
		z=z^0.333333333
	Else
		z=(z*7.787)+0.137931034
	EndIf
	Return [116*y-16,500*(x-y),200*(y-z)]
End Function
' assumes l*ab in a very varying range
' assumes xyz in the range [0,1]
Function labtoxyz#[](lab#[])
	Local y#=(lab[0]+16)/116.0
	Local x#=lab[1]/500.0+y
	Local z#=y-lab[2]/200.0
	If x^3>.008856 Then
		x=x^3
	Else
		x=(x-0.137931034)/7.787
	EndIf
	If y^3>.008856 Then
		y=y^3
	Else
		y=(y-0.137931034)/7.787
	EndIf
	If z^3>.008856 Then
		z=z^3
	Else
		z=(z-0.137931034)/7.787
	EndIf
	Return [x,y,z]
End Function
' assumes rgb in the range [0,1]
' returns l*ab in a very varying range
Function rgbtolab#[](rgb#[])
	Return xyztolab(rgbtoxyz(rgb))
End Function
' assumes l*ab in a very varying range
' returns rgb in the range [0,1]
Function labtorgb#[](lab#[])
	Return xyztorgb(labtoxyz(lab))
End Function
' assumes rgb in the range [0,1]
' returns cmy in the range [0,1]
Function rgbtocmy#[](rgb#[])
	Return [1-rgb[0],1-rgb[1],1-rgb[2]]
End Function
' assumes cmy in the range [0,1]
' returns rgb in the range [0,1]
Function cmytorgb#[](cmy#[])
	Return [1-cmy[0],1-cmy[1],1-cmy[2]]
End Function
' assumes cmy in the range [0,1]
' returns cmyk in the range [0,1]
Function cmytocmyk#[](cmy#[])
	Local c#=cmy[0],m#=cmy[1],y#=cmy[2]
	Local k#=1
	If c<k Then k=c
	If m<k Then k=m
	If y<k Then k=y
	If k=1 Then Return [0.0,0.0,0.0,k]
	Local k1#=1-k
	Return [(c-k)/k1,(m-k)/k1,(y-k)/k1,k]
End Function
' assumes cmyk in the range [0,1]
' returns cmy in the range [0,1]
Function cmyktocmy#[](cmyk#[])
	Local k1#=1-cmyk[3]
	Return [cmyk[0]*k1+cmyk[3],cmyk[1]*k1+cmyk[3],cmyk[2]*k1+cmyk[3]]
End Function
' assumes rgb in the range [0,1]
' returns cmyk in the range [0,1]
Function rgbtocmyk#[](rgb#[])
	Return cmytocmyk(rgbtocmy(rgb))
End Function
' assumes cmyk in the range [0,1]
' returns rgb in the range [0,1]
Function cmyktorgb#[](cmyk#[])
	Return cmytorgb(cmyktocmy(cmyk))
End Function

' the following code was adapted from the examples found at:
' http://mjijackson.com/2008/02/rgb-to-hsl-and-rgb-to-hsv-color-model-conversion-algorithms-in-javascript

' assumes rgb in the range [0,1]
' returns hsl in the range [0,1]
Function rgbtohsl#[](rgb#[])
	Local r#=rgb[0],g#=rgb[1],b#=rgb[2]
	Local hsl#[]=New Float[3]
	Local ax#=Max(Max(r,g),b),in#=Min(Min(r,g),b)
	hsl[2]=(ax+in)/2.0
	If(ax=in) Then Return hsl
	Local d#=ax-in
	If hsl[2]>.5 Then hsl[1]=d/(2.0-ax-in) Else hsl[1]=d/(ax+in)
	Select ax
		Case r
			Local gv%=0
			If g<b Then gv=6
			hsl[0]=(g-b)/d+gv
		Case g
			hsl[0]=(b-r)/d+2
		Case b
			hsl[0]=(r-g)/d+4
	End Select
	hsl[0]:/6.0
	Return hsl
End Function
' assumes hsl in the range [0,1]
' returns rgb in the range [0,1]
Function hsltorgb#[](hsl#[])
	Local h#=hsl[0],s#=hsl[1],l#=hsl[2]
	If s=0 Then Return [l,l,l]
	Local q#
	If l<.5 Then q=l*(1+s) Else q=(l+s-l*s)
	Local p#=2*l-q
	Return [_huetorgb(p,q,h+0.333333333),_huetorgb(p,q,h),_huetorgb(p,q,h-0.333333333)]
End Function
Function _huetorgb#(p#,q#,t#)
	If t<0 t:+1
	If t>1 t:-1
	If t<0.166666667 Return p+(q-p)*6*t
	If t<0.5 Return q
	If t<0.666666667 Return p+(q-p)*(0.666666667-t)*6
	Return p
End Function
' assumes rgb in the range [0,1]
' returns hsv in the range [0,1]
Function rgbtohsv#[](rgb#[])
	Local r#=rgb[0],g#=rgb[1],b#=rgb[2]
	Local hsv#[]=New Float[3]
	Local ax#=Max(Max(r,g),b),in#=Min(Min(r,g),b)
	hsv[2]=ax
	Local d#=ax-in
	hsv[1]=0
	If ax<>0 Then hsv[1]=d/ax
	If(ax=in) Then hsv[0]=0;Return hsv
	Select ax
		Case r
			Local gv%=0
			If g<b Then gv=6
			hsv[0]=(g-b)/d+gv
		Case g
			hsv[0]=(b-r)/d+2
		Case b
			hsv[0]=(r-g)/d+4
	End Select
	hsv[0]:/6.0
	Return hsv
End Function
' assumes hsv in the range [0,1]
' returns rgb in the range [0,1]
Function hsvtorgb#[](hsv#[])
	Local h#=hsv[0],s#=hsv[1],v#=hsv[2]
	Local h6#=h*6
	Local i%=h6
	Local f#=h6-i
	Local p#=v*(1-s)
	Local q#=v*(1-f*s)
	Local t#=v*(1-(1-f)*s)
	Select i Mod 6
		Case 0 Return [v,t,p]
		Case 1 Return [q,v,p]
		Case 2 Return [p,v,t]
		Case 3 Return [p,q,v]
		Case 4 Return [t,p,v]
		Case 5 Return [v,p,q]
	End Select
	Return [0.0,0.0,0.0]
End Function

' the following code was derived from the information found at:
' http://en.wikipedia.org/wiki/HSL_and_HSV#From_luma.2Fchroma.2Fhue

Const _b_r#=.3,_b_g#=.59,_b_b#=.11

' assumes hcl in the range [0,1]
' returns rgb in the range [0,1]
Function hcltorgb#[](hcl#[])
	Local h#=hcl[0]*6,c#=hcl[1]
	Local x#=c*(1-Abs((h Mod 2)-1))
	Local rgb#[]
	If 0<=h And h<1
		rgb=[c,x,0.0]
	ElseIf 1<=h And h<2
		rgb=[x,c,0.0]
	ElseIf 2<=h And h<3
		rgb=[0.0,c,x]
	ElseIf 3<=h And h<4
		rgb=[0.0,x,c]
	ElseIf 4<=h And h<5
		rgb=[x,0.0,c]
	ElseIf 5<=h And h<=6
		rgb=[c,0.0,x]
	Else
		rgb=[0.0,0.0,0.0]
	EndIf
	Local m#=hcl[2]-(_b_r*rgb[0]+_b_g*rgb[1]+_b_b*rgb[2])
	rgb[0]:+m
	rgb[1]:+m
	rgb[2]:+m
	Return rgb
End Function
' assumes rgb in the range [0,1]
' returns hcl in the range [0,1]
Function rgbtohcl#[](rgb#[])
	Local r#=rgb[0],g#=rgb[1],b#=rgb[2]
	Local hcl#[]=New Float[3]
	Local m#=Max(Max(r,g),b)
	hcl[1]=m-Min(Min(r,g),b)
	If hcl[1]=0 Then
		hcl[0]=0
	ElseIf m=r
		hcl[0]=((g-b)/hcl[1]) Mod 6
	ElseIf m=g
		hcl[0]=((b-r)/hcl[1])+2
	ElseIf m=b
		hcl[0]=((r-g)/hcl[1])+4
	EndIf
	hcl[0]:/6.0
	hcl[2]=_b_r*r+_b_g*g+_b_b*b
	Return hcl
End Function

' assumes rgb in the range [0,1]
' returns yiq in the range [-1,1]
Function rgbtoyiq#[](rgb#[])
	Local r#=rgb[0],g#=rgb[1],b#=rgb[2]
	Local yiq#[]=New Float[3]
	yiq[0]=0.299000*r + 0.587000*g + 0.114000*b
	yiq[1]=0.595716*r - 0.274453*g - 0.321264*b
	yiq[2]=0.211456*r - 0.522591*g + 0.311350*b
	Return yiq
End Function

' assumes yiq in the range [-1,1]
' returns rgb in the range [0,1]
Function yiqtorgb#[](yiq#[])
	Local y#=yiq[0],i#=yiq[1],q#=yiq[2]
	Local rgb#[]=New Float[3]
	rgb[0]=y + 0.9563*i + 0.6210*q
	rgb[1]=y - 0.2721*i - 0.6474*q
	rgb[2]=y - 1.1070*i + 1.7046*q
	Return rgb
End Function

' assumes rgb in the range [0,1]
' returns yuv in the range [-1,1]
Const yuv_uconst#=1.0/0.436
Const yuv_vconst#=1.0/0.615
Function rgbtoyuv#[](rgb#[])
	Local r#=rgb[0],g#=rgb[1],b#=rgb[2]
	Local yuv#[]=New Float[3]
	yuv[0]=(0.299*r + 0.587*g + 0.114*b)
	yuv[1]=(0.492*(b-yuv[0]))*yuv_uconst
	yuv[2]=(0.877*(r-yuv[0]))*yuv_vconst
	yuv[0]=yuv[0]*2-1
	Return yuv
End Function

' assumes yuv in the range [-1,1]
' returns rgb in the range [0,1]
Function yuvtorgb#[](yuv#[])
	Local y#=(yuv[0]+1)/2.0,u#=yuv[1]/yuv_uconst,v#=yuv[2]/yuv_vconst
	Local rgb#[]=New Float[3]
	rgb[0]=y + 1.140*v
	rgb[1]=y - 0.395*u - 0.581*v
	rgb[2]=y + 2.032*u
	Return rgb
End Function
