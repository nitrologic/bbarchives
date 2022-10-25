; ID: 3163
; Author: zoqfotpik
; Date: 2014-11-25 22:15:50
; Title: Simple Path Following
; Description: Given a list of path points, follow them one by one

' Simple Pathfinding
Strict
Graphics 640,480

' Target X, Target Y
Global tx:Float = 500
Global ty:Float = 200

' Current Position X, Y
Global px:Float = 320
Global py:Float = 240

' Variables related to slope between points
Global slope:Float
Global rise:Float
Global run:Float

' X and Y portions of movement increment
Global xstep:Float
Global ystep:Float

' This is the slope in degrees, converted by ATan() or Arctangent
Global deg:Float

Type coord
Field x:Int,y:Int
End Type

' List of path points
Global path:TList = New TList

While Not KeyDown(KEY_ESCAPE)

' Change position of target seeker
If MouseHit(1)
	px = MouseX()
	py = MouseY()
EndIf

' Add a New Path Point
If MouseHit(2)
	Local newcoord:coord = New coord
	newcoord.x = MouseX()
	newcoord.y = MouseY()
	path.addlast(newcoord)
EndIf

Local target:coord = New coord

' Get an object off the path point list and cast it to Coord
target = Coord(path.first())

If target <> Null
	tx = target.x
	ty = target.y
EndIf

SetColor 255,255,255
Local thispathpoint:coord = New coord
For thispathpoint = EachIn path
	Plot thispathpoint.x,thispathpoint.y
Next

' Basic right triangle algebra
run = tx - px
rise = ty - py
slope = rise / run

' Get the degree value of the slope
deg = ATan(slope)
If px > tx deg = deg + 180

' Get trigonometric slope value
xstep = Cos(deg)*2
ystep = Sin(deg)*2
' There is a simpler way of doing all that stuff which doesn't involve trig or the angle
' but it doesn't give you the desired degree facing and you probably will need that anyway


' Update the seeker point's position
px = px + xstep
py = py + ystep

' Are we close enough to the target path point?  If so, remove it
If distance(px,py,tx,ty) < 5
	path.remove(target)
EndIf

Print deg
SetColor 255,255,0
Plot px, py
SetColor 255,0,0
Plot tx,ty
Flip
Wend

Function distance:Float(x1:Int, y1:Int, x2:Int, y2:Int) 
Local d:Float=Sqr((x1 - x2)^2 + (y1 - y2)^2)
Return d
End Function
