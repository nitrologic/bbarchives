; ID: 3089
; Author: zoqfotpik
; Date: 2013-11-16 09:33:22
; Title: Color Cycling and Procedural Critters
; Description: Demonstrates color effects, includes randomly generated space invaders critters

Global GFXHEIGHT=480
Global GFXWIDTH=640
Global maxstars=1000
Graphics GFXWIDTH, GFXHEIGHT
SetMaskColor 255,255,0
Global critterlist:TList=New TList
Global critterlist2:TList = New TList

SetScale 2,2
SetColor 0,0,0
DrawRect 100,100,100,100
SetColor 255,255,0
DrawText "WILDLY INTERESTING COPPER EFFECTS",000,130
myimage:TImage = CreateImage(1000,75,1)
Global ticks

GrabImage myimage, 000,100

SetScale 1,1
SetBlend solidblend
For i = 1 To 10
Local s:proceduralpic = New proceduralpic
s.randcolors()
s.initgrid()
s.x = 68*i
s.y = 120
ListAddLast critterlist,s
Next

For i = 1 To 10
s:proceduralpic = New proceduralpic
s.randcolors()
s.initgrid()
s.x = 68*i-64
s.y = 240
ListAddLast critterlist2,s
Next

Type Tstar
Field x,y,vx,vy,size:Int
Field alpha:Float
Method update()
	x=x+vx
	y = y + vy
	If x < 0 x = GFXWIDTH
End Method
Method draw()
	SetColor 255,255,255
	SetAlpha alpha
	Plot x,y
	'DrawRect x,y, size,size
End Method
End Type

Type Tstarfield
Global star_list:TList = New TList
Function init(num_stars)
For Local i = 1 To num_stars
	s:Tstar = New Tstar
	s.x = Rand(GFXWIDTH)
	s.y = Rand(GFXHEIGHT)
	s.vx = (Rand(5)+Rnd())*-1
	s.vy = 0
	s.size=Rand(3)
	s.alpha = Rnd()+.5
	ListAddLast(star_list,s)
Next
End Function
Method update()
	For s:tstar=EachIn star_list
	s.update()
	SetBlend alphablend
	s.draw()
	Next
End Method
End Type

Type Tcolor
	Field r#, g#, b#
End Type

	
Global mystarfield:TStarfield = New TStarfield
myStarfield.init(MAXSTARS)

While KeyDown(key_escape)=0

For Local thiscritter:proceduralpic = EachIn critterlist
If thiscritter.x < -32
thiscritter.x = 640
thiscritter.randcolors()
thiscritter.initgrid()
EndIf
Next

For thiscritter:proceduralpic = EachIn critterlist2
If thiscritter.x > 640
thiscritter.x = -32
thiscritter.randcolors()
thiscritter.initgrid()
EndIf
Next


Cls
ticks = ticks + 1
SetScale 1,1

SetBlend solidblend


copperblock (170,230,ticks)


SetColor 255,255,255

SetBlend maskblend
SetScale 2,2
DrawImage myimage,0-Sin(ticks)*210-210,115
SetScale 1,1

mystarfield.update()
copperblock (300,400,ticks+20)
copperblock 25,65,ticks-20

SetBlend solidblend
For thiscritter:proceduralpic = EachIn critterlist
thiscritter.x = thiscritter.x - 5
thiscritter.y = 120+Sin(thiscritter.x*10)*3
thiscritter.draw()
Next

For thiscritter:proceduralpic = EachIn critterlist2
thiscritter.x = thiscritter.x + 5
thiscritter.y = 240+Sin(thiscritter.x*10)*3
thiscritter.draw()
Next

Flip
Wend


Function waveline(x1, x2, y, frequency#, amplitude#)
For Local i = x1 To x2 Step 2
myy=y+Sin(i*frequency)*amplitude
DrawRect i, myy, 2,2
Next
End Function

Function copperblock(starty, endy,timestep)
For i = starty To endy Step 2
SetColor Abs(Sin(i*20+timestep*20)*255), Abs(Tan(i*5+timestep*2)*50),Abs(Cos(i*5+timestep*20)*255)
waveline 0,640,i, Sin(timestep)*5, Cos(timestep)*20
Next
End Function

'---------------------------------
Type color
'---------------------------------
Field r:Int, g:Int, b:Int

Method SetmyColor(myr,myg,myb)
	r=myr
	g=myg
	b=myb
End Method

Method randcolor()
	r=Rand(100,255)	
	g=Rand(100,255)
	b=Rand(100,255)
End Method

Method thiscolor()
	SetColor r,g,b
End Method
End Type


'---------------------------------
Type proccritter
'---------------------------------
Field x, y, vx, vy, angle

Field primarycolor:color=New color
Field secondarycolor:color=New color

Method update()
End Method
Method draw()
End Method
End Type

'--------------------------------
Type proceduralpic Extends proccritter
'--------------------------------
Field grid[8,8]
Field myimage = CreateImage(32,32)

Method randcolors()
	primarycolor.randcolor()
	secondarycolor.randcolor()
End Method

Method initgrid()

	For myx = 0 To 7
	For myy = 0 To 7
	grid[myx,myy]=0
	Next
	Next
	
	
	For i = 1 To 25
		xrand = Rand(0,3)
		yrand = Rand(0,7)
	
		celltype = Rand(2)
		grid[xrand,yrand]=celltype
		grid[7-xrand,yrand]=celltype
		
	Next
	Cls
	
	For myy = 0 To 7
		For myx = 0 To 7
						
			SetColor(0,0,0)
			If grid[myx, myy]=1
				primarycolor.thiscolor()
			Else If grid [myx, myy]=2
				secondarycolor.thiscolor()
			EndIf
			
			DrawRect (100+myx*4), (100+myy*4), 3, 3
		Next
	Next
	SetColor 255,255,255
	GrabImage myimage,100,100
	
End Method

Method draw()
	SetColor 150,150,150
	DrawImage myimage, x+2,y+2
	SetColor 255,255,255
	DrawImage myimage, x, y
	
End Method

Method update()
End Method

End Type
