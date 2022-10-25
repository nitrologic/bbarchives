; ID: 3134
; Author: zoqfotpik
; Date: 2014-06-21 19:11:07
; Title: Volumetric Model to Isometric Sprite
; Description: This is a quick and dirty method of making isometric sprites.

Type TColor
	'color by RGB value
	Field r:Int, g:Int, b:Int
	Method Setrgb(myr:Int,myg:Int, myb:Int)
		r=myr
		g = myg
		b = myb
	End Method
	
	Method getr()
		Return myr
	End Method
	
	Method getg()
		Return g
	End Method
	
	Method getb()
		Return b
	End Method
	
	Method printrgb()
	' For debug.
		Print r + " " + g + " " + b
	End Method
	
	Method setcolorfromtcolor()
	' Set the global draw color according to tcolor rgb.
		SetColor r, g, b
	End Method
	
	Method addrgb(nr:Int, ng:Int, nb:Int)
	' Add given rgb value to this rgb.
		r = r + nr
		g = g + ng
		b = b + nb
	End Method
	
End Type

Type imagegrid
	'imagegrid - array of RGB values.  Z is positive upward.
	
	Field colorarray : TColor [32,32,32] ' Define the voxel array
	Field imagearray : TImage [32]
	
	Method New()
	' constructor initializes entire array-- we have to do this because
	' Max doesn't ordinarily initialize an array of objects and it produces
	' bizarre errors if you don't.
		For Local i:Int = 0 To 31
			For Local j:Int = 0 To 31
				For Local k:Int = 0 To 31
					colorarray[i,j,k]=New tcolor
				Next
			Next
		Next
		
		For i:Int = 0 To 31
			imagearray[i]=CreateImage(45,69, 1, DYNAMICIMAGE|MASKEDIMAGE)
		Next
	End Method
	
	Method setvoxel(x:Int, y:Int, z: Int, r:Int, g:Int, b:Int)
		Assert invoxel(x,y,z) Else "Voxel out of bounds."
		colorarray[x,y,z].Setrgb(r, g, b)
	End Method
	
	Method drawslice(x:Int, y:Int, z:Int) ' draws a square slice z at screencoords x and y
		Assert testconstraint(x,0,31) Else "Slice out of bounds in drawslice()"
		For Local i:Int = 0 To 31
			For Local j:Int = 0 To 31
				colorarray[i,j,z].setcolorfromtcolor()
				Plot x+j, y+i
			Next
		Next
	End Method
	
	Method fill(r:Int, g:Int, b:Int)
		For Local i:Int = 0 To 31
			For Local j:Int = 0 To 31
				For Local k:Int = 0 To 31
					setpoint(i,j,k, r,g,b)
				Next
			Next
		Next 
	End Method
	
	Method fillrect(ux:Int, uy:Int, lx:Int, ly:Int, z:Int, r:Int, g:Int, b:Int)
		Assert invoxel(ux, uy, z) And invoxel(lx, ly, z) Else "Rect out of bounds in fillrect()"
		For Local i:Int = ux To lx
			For Local j:Int = uy To ly
				setpoint(i,j,z, r,g,b)
			Next
		Next
	End Method
	
	
	Method fillbox(ux:Int, uy:Int, lx:Int, ly:Int, lz:Int, uz:Int, r:Int, g:Int, b:Int) ' coordinates of box, then bottom to top extents
		Assert invoxel(ux, uy, uz) And invoxel(lx, ly, lz) Else "Box out of bounds in fillbox"
		For Local k:Int = lz To uz
			fillrect(ux, uy, lx, ly, k, r, g, b)
		Next
	End Method
	
	Method setpoint(i:Int, j:Int, k:Int, r:Int, g:Int, b:Int)
		Assert invoxel(i,j,k) Else "Voxel out of bounds in setpoint()"
		colorarray[i,j,k].setrgb(r,g,b)
	End Method
	
	Method isoccupied(x:Int, y:Int, z:Int)
		Assert invoxel(x,y,z) Else "Voxel out of bounds in isoccupied()"
		Local tempcolor:tcolor = colorarray[x,y,z]
		If tempcolor.r + tempcolor.g + tempcolor.b > 0
			Return True
		Else
			Return False
		EndIf
	End Method
	
	Method toplight(lightr:Int, lightg:Int, lightb:Int) ' toplights entire boxel
		For Local i:Int = 0 To 31
			For Local j:Int = 0 To 31
				Local k:Int = 31
				' descend from top of boxel to first occupied voxel
				While k > 0 And isoccupied(i,j,k)=False
					k = k - 1
				Wend
				If k > -1 And isoccupied(i,j,k)=True
					colorarray[i,j,k].addrgb(lightr:Int, lightg:Int, lightb:Int)
				EndIf
			Next
		Next
	End Method
	
	Method leftlight(lightr:Int, lightg:Int, lightb:Int)
		For Local j:Int = 0 To 31
			For Local k:Int = 0 To 31
				Local i:Int = 31
				'enter from the left and move toward first occupied voxel
				While i>0 And isoccupied(i,j,j)=False
					i=i-1
				Wend
				If i>-1 And isoccupied(i,j,k)=True
					colorarray[i,j,k].addrgb(lightr:Int, lightg:Int, lightb:Int)
				EndIf
			Next
		Next
	End Method
	
	Method grabsliceimages()
	Local tempimage:TImage = CreateImage(45,69, 1, DYNAMICIMAGE|MASKEDIMAGE)
		
		For Local i:Int = 0 To 31
			Cls
			'SetRotation 45
			'SetScale 1,.5
			SetColor 255,255,255
			drawslice (100,100,i)
			GrabImage tempimage, 100,100, 0
			Cls 
			SetRotation 45
			DrawImage tempimage, 100,100
			GrabImage imagearray[i],77,77,0
		Next
		SetRotation 0
	End Method
	
	Method drawimagestack(x, y)
	Local tempimage:TImage = CreateImage(45,69, 1, DYNAMICIMAGE|MASKEDIMAGE)
		SetScale 1, .5
		For Local i:Int = 0 To 31
		tempimage = imagearray[i]
			DrawImage tempimage,x,y-i
		Next
	End Method
	
End Type

Type voxgrid
	 ' A grid of 32 x 32 x 32 points, and associated isometric sprite images
	Field image:TImage = CreateImage(45,69, 1, DYNAMICIMAGE|MASKEDIMAGE)
	Field imagestack:TImage = CreateImage(45,69,32,DYNAMICIMAGE|MASKEDIMAGE)
	
	Method setimage(myimage:TImage)
		image = myimage
	End Method
	
	Method setimageslice(i:Int, myimage:TImage)
		Assert testconstraint(i,0,31) Else "Slice out of bounds in setimageslice"
	End Method
	
	Method drawgrid(x:Int, y:Int)
		For Local i:Float = 1 To 31
			DrawImage image, x,y-i,0
		Next
	End Method	
	
End Type

Function testconstraint(x:Int, lowerbound:Int, upperbound:Int)
' Test whether an input integer x is between lowerbound and upperbound.
	If x < lowerbound Or x > upperbound 
		Return False
	Else 
		Return True
	EndIf
End Function

Function invoxel(x:Int, y:Int, z:Int)
' Test whether an input point x, y, z is within a 32 by 32 by 32 voxel.
	If testconstraint(x, 0, 31) And testconstraint(y, 0, 31) And testconstraint (z, 0, 31)
		Return True
	Else
		Return False
	EndIf
End Function

Function boxinvoxel(ux:Int,uy:Int, lx:Int, ly:Int, lz:Int, uz:Int)
	If invoxel(ux, uy, uz) And invoxel (lx, ly, lz)
		Return True
	Else
		Return False
	EndIf
End Function

Global testcolor:tcolor = New tcolor
Global testgrid:imagegrid = New imagegrid
Global testgrid2:imagegrid = New imagegrid

testcolor.Setrgb(100,101,102)
testcolor.printrgb()

Graphics 640, 480

For i = 1 To 1000
testgrid.setvoxel (jRand(32),jRand(32),jRand(32), jRand(255),Rand(255),jRand(255))
Next

testgrid2.fill(75,75,100)
'testgrid2.fillrect(10, 10, 20, 20, 5, 0, 0, 0)	
testgrid2.toplight(45,45,45)
testgrid2.leftlight(100,100,100)

Cls
testgrid2.grabsliceimages()


While Not KeyDown (KEY_ESCAPE)

Cls
testgrid.drawslice(100,100,10)
testgrid2.drawslice(140,100,10)
testgrid2.drawslice(180,100,5)
testgrid2.drawslice(240,100,31)
testgrid2.drawimagestack(300,100)
testgrid2.drawimagestack(344,100)
testgrid2.drawimagestack(322,120)
Flip

Wend

Function jrand(x:Int)
	Local retval:Int = Rand(x)-1
	Return retval
End Function
