; ID: 2272
; Author: JoshK
; Date: 2008-06-15 19:22:53
; Title: Difference Clouds heightmap
; Description: Generates a heightmap using a diamond-square fractal

Strict

Type THeightField
	
	Field size
	Field height:Short[,]
	
	Function Create:THeightField(size)
		Local h:THeightfield=New THeightField
		Local height:Short[size,size]
		h.height=height
		h.size=size
		Return h
	EndFunction
	
	Method Copy:THeightField()
		Local h:THeightfield=New THeightField
		Local height:Short[size,size]
		Local x,y
		h.height=height
		h.size=size
		MemCopy h.height,height,size*size*2
		Return h
	EndMethod
	
	Method Blend(h:THeightfield,b#=0.5)
		Local x,y
		For x=0 To size-1
			For y=0 To size-1
				height[x,y]=height[x,y]*b+h.height[x,y]*(1.0-b)
			Next
		Next
	EndMethod
	
	Method Noise()
		Local x,y
		For x=0 To size-1
			For y=0 To size-1
				height[x,y]=Rand(65536)
			Next
		Next
	EndMethod
	
	Method Flatten(h#=0)
		Local x,y
		For x=0 To size-1
			For y=0 To size-1
				height[x,y]=h*65536
			Next
		Next
	EndMethod
	
	Method DiamondSquareFractal(blend#=0.5,featuresize#=64)
		Local x,y,res
		height[0,0]=Rand(0,65536)
		height[size-1,0]=Rand(0,65536)
		height[0,size-1]=Rand(0,65536)
		height[size-1,size-1]=Rand(0,65536)
		res=size
		Local iteration
		Local passes=1
		Local gridsize=size
		Repeat
			iteration:+1
			
			'Diamond
			For x=0 To passes-1
				For y=0 To passes-1
					DiamondFractal(x*(gridsize-1),y*(gridsize-1),res,blend,featuresize)
				Next
			Next
			
			'Square
			For x=0 To passes-1
				For y=0 To passes-1
					SquareFractal(x*(gridsize-1)+(res-1)/2,y*(gridsize-1),res,blend,featuresize)
					SquareFractal(x*(gridsize-1),y*(gridsize-1)+(res-1)/2,res,blend,featuresize)
					SquareFractal(x*(gridsize-1)+(res-1),y*(gridsize-1)+(res-1)/2,res,blend,featuresize)
					SquareFractal(x*(gridsize-1)+(res-1)/2,y*(gridsize-1)+(res-1),res,blend,featuresize)
				Next
			Next
			
			res=(res+1)/2
			passes:*2
			If res=2 Exit
			gridsize=(gridsize+1)/2
		Forever
	EndMethod

	Method DiamondFractal(x0,y0,d,blend#,featuresize#)
		Local hd,x1,y1
		x1=x0+d-1
		y1=y0+d-1
		hd=(d+1)/2
		Local i,avg#
		'Print x0+", "+y0+", "+x1+", "+y1
		Local distblend#=Min(Float(d)/featuresize,1.0)
		blend:*distblend
		
		height[x0+hd-1,y0+hd-1] = ( height[x0,y0] + height[x0,y1] + height[x1,y0] + height[x1,y1] ) / 4
	EndMethod
	
	Method SquareFractal(x0,y0,d,blend#,featuresize#)
		Local hd,x1,y1
		x1=x0+d-1
		y1=y0+d-1
		hd=(d-1)/2
		'Print x0+", "+y0+", "+x1+", "+y1
		Local i=0
		Local avg#=0.0
		
		If x0-hd=>0
			i:+height[x0-hd,y0]
			avg:+1
		EndIf

		If x0+hd<=size-1
			i:+height[x0+hd,y0]
			avg:+1
		EndIf

		If y0-hd=>0
			i:+height[x0,y0-hd]
			avg:+1
		EndIf

		If y0+hd<=size-1
			i:+height[x0,y0+hd]
			avg:+1
		EndIf
		
		Local distblend#=Min(Float(d)/featuresize,1.0)
		blend:*distblend
		height[x0,y0]=i/avg * (1.0 - blend) + Rand(65535) * blend
	EndMethod
	
	Method Multiply(m#)
		Local x,y
		For x=0 To size-1
			For y=0 To size-1
				height[x,y]:*m
			Next
		Next
	EndMethod
	
	Method ToPixmap:TPixmap()
		Local x,y,r
		Local p:TPixmap=CreatePixmap(size,size,PF_I8)
		For x=0 To size-1
			For y=0 To size-1
				r=height[x,y]/65536.0*255.0
				p.WritePixel x,y,r+(r Shl 8)+(r Shl 16)
			Next
		Next
		Return p
	EndMethod
	
	Method FromPixmap(pixmap:TPixmap)
		Local x,y,px,py
		For x=0 To size-1
			For y=0 To size-1
				px=Min(x,pixmap.width-1)
				py=Min(y,pixmap.height-1)			
				height[x,y]=((pixmap.ReadPixel(px,py) & $00FF0000) Shr 16)/255.0*65536
			Next
		Next
	EndMethod
	
EndType

Local h:THeightfield
h=THeightfield.Create(1025)
SeedRnd MilliSecs()
h.DiamondSquareFractal(1.0,256)

SavePixmapPNG h.topixmap(),"test.png"
OpenURL "test.png"
