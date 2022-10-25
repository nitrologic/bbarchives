; ID: 421
; Author: JoshK
; Date: 2002-09-08 20:32:33
; Title: 2D BSP
; Description: Demonstrates 2-dimensional bsp trees.

;2-Dimensional BSP Tree
;by Joshua Klint

AppTitle "2-Dimensional BSP Tree"
Graphics 700,320,16,2
SetBuffer BackBuffer()

Type node
Field x1,y1,x2,y2,nx#,ny#
Field child.node[1]
Field index,flags
Field red,green,blue
End Type

Global intersectedx#
Global intersectedy#
Global countnodes
Global master.node

ClsColor 255,255,255
logstring$="Click and drag to create a node."

Repeat

For n.node=Each node
	Color 0,0,0
	x#=(n\x2-n\x1)/2+n\x1
	y#=(n\y2-n\y1)/2+n\y1
	Line x,y,x+n\nx*6,y+n\ny*6
	Color n\red,n\green,n\blue
	Line n\x1,n\y1,n\x2,n\y2
	Color 0,0,0
	Text n\x1,n\y1,n\index
	Next

If pointwithinrect(MouseX(),MouseY(),0,0,399,300)
	If MouseHit(1)
		If Not drawmode
			logstring$=""
			drawmode=True
			x1=MouseX()
			y1=MouseY()
			x2=x1
			y2=y1
			EndIf
		EndIf
	If MouseDown(1)
		x2=MouseX()
		y2=MouseY()
		Color 255,0,0
		Line x1,y1,x2,y2
		Color 200,200,200
		For n.node=Each node
			If intersectlines(x1,y1,x2,y2,n\x1,n\y1,n\x2,n\y2)
				Line intersectedx,0,intersectedx,300
				Line 0,intersectedy,400,intersectedy
				EndIf
			Next
		Else
		If drawmode
			drawmode=False
			logstring$="Created new node."
			For n.node=Each node
				If intersectlines(x1,y1,x2,y2,n\x1,n\y1,n\x2,n\y2)
					logstring="Created multiple nodes."
					Exit
					EndIf
				Next
			n.node=New node
			n\red=Rnd(255)
			n\green=Rnd(255)
			n\blue=Rnd(255)
			n\x1=x1
			n\y1=y1
			n\x2=x2
			n\y2=y2
			nx#=y2-y1
			ny#=x2-x1
			l#=Sqr(nx^2+ny^2)
			n\nx=nx/l
			n\ny=-ny/l
			n\index=countnodes
			countnodes=countnodes+1
			If master.node=Null
				master=n
				Else
				placenode n,master
				EndIf
			EndIf
		EndIf
	EndIf

If master<>Null
	Color 0,0,0
	displaybsptree master,550,20,60
	EndIf
	
Color 208,207,192
Rect 0,300,400,2
Rect 400,0,2,GraphicsHeight()
Color 0,0,0
Text 5,304,logstring
Flip
Cls
Forever

Function pointwithinrect(x#,y#,boxx#,boxy#,width#,height#)
If x>boxx
	If x<boxx+width
		If y>boxy
			If y<boxy+height Return True
			EndIf
		EndIf
	EndIf
End Function

Function intersectlines(x1#,y1#,x2#,y2#,x3#,y3#,x4#,y4#,segment=True)
den#=(y4-y3)*(x2-x1)-(x4-x3)*(y2-y1)
If den=0.0 Return False
ua#=((x4-x3)*(y1-y3)-(y4-y3)*(x1-x3))/den
ub#=((x2-x1)*(y1-y3)-(y2-y1)*(x1-x3))/den
If segment
	If ua>1.0 Or ua<0.0 Or ub>1.0 Or ub<0.0 Return False
	EndIf
intersectedx#=x1+ua*(x2-x1)
intersectedy#=y1+ua*(y2-y1)
Return True
End Function

;If result=0 point is on line.
;If result=1 point is behind line.
;if result=-1 point is in front of line.
Function pointonline(x#,y#,x1#,y1#,x2#,y2#)
m#=(y2-y1)/(x2-x1)
b#=-m*x1+y1
yvalue#=m*x+b
If yvalue=y Return 0
If yvalue>y Return -1*Sgn(x2-x1)
If yvalue<y Return 1*Sgn(x2-x1)
End Function

Function placenode(n.node,m.node)
result=pointonline(n\x1,n\y1,m\x1,m\y1,m\x2,m\y2)
result2=pointonline(n\x2,n\y2,m\x1,m\y1,m\x2,m\y2)
If result=-1 result=0
If result2=-1 result2=0
If result<>result2
	intersectlines(n\x1,n\y1,n\x2,n\y2,m\x1,m\y1,m\x2,m\y2,False)
	n2.node=New node
	n2\x1=intersectedx
	n2\y1=intersectedy
	n2\x2=n\x2
	n2\y2=n\y2
	n\x2=intersectedx
	n\y2=intersectedy
	n2\index=countnodes
	n2\nx=n\nx
	n2\ny=n\ny
	n\red=Rnd(255)
	n\green=Rnd(255)
	n\blue=Rnd(255)
	countnodes=countnodes+1
	If m\child[result2]=Null
		m\child[result2]=n2
		Else
		placenode n2,m\child[result2]
		EndIf
	EndIf
If m\child[result]=Null
	m\child[result]=n
	Else
	placenode n,m\child[result]
	EndIf
End Function

Function displaybsptree(n.node,x,y,width,layer=0)
Text x,y,n\index,1,1
offset=width/2^layer
If n\child[0]<>Null
	Line x,y,x-offset,y+40
	displaybsptree n\child[0],x-offset,y+40,width,layer+1
	EndIf
If n\child[1]<>Null
	Line x,y,x+offset,y+40
	displaybsptree n\child[1],x+offset,y+40,width,layer+1
	EndIf
End Function
