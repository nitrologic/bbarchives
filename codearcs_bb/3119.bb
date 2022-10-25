; ID: 3119
; Author: Matty
; Date: 2014-04-13 00:36:00
; Title: Lightning Effect (2d)
; Description: Lightning effect from point A to near point B

;Matt Lloyd Lightning Implementation.
;
;Free to use as you wish. Play around with the parameters.
;
;
;
;
;
Function DrawLightning(sx#,sy#,fx#,fy#,depth=0)
;;;;
;	
;	Draw 2d Lightning.
;	Pass the starting and ending points.  Depth parameters refers to recursion.  Don't want to blow the stack!
;
;	
;
;

Local dx#,dy#,dist#,ox#,oy#,x#,y#,udx#,udy#
Local i

;;;;
;;;;
;;play around with these numbers to see the effects
Local shakiness#=-1.0 ;
Local branchchance=5;
Local wr1 = 16
Local wr2 = 16;
Local hr1 = 4
Local hr2 = 24


If(depth>3) Then Return ;;as said - don't want to go too deep in the stack...

dx#=fx-sx
dy#=fy-sy
dist#=Sqr(dx*dx+dy*dy)
If(dist=0) Then Return ;no need to do any lightning if our start and ending points are on top of each other...and also avoid nasty division by zeroes...

udx#=dx/dist	;unit vectors in direction of lightning...
udy#=dy/dist


x#=sx 
y#=sy
ox#=x
oy#=y

For i=1 To dist 

x=x+udx+Rnd(-shakiness,shakiness)
y=y+udy+Rnd(-shakiness,shakiness)

ColorCentre()
Line x,y,ox,oy

ColorInnerEdge()
Line x-udy,y+udx, ox-udy,oy+udx
Line x+udy,y-udx,ox+udy,oy-udx

ColorOuterEdge()
Line x-udy*2.0,y+udx*2.0, ox-udy*2.0,oy+udx*2.0
Line x+udy*2.0,y-udx*2.0,ox+udy*2.0,oy-udx*2.0

ox#=x#
oy#=y#

If(Rand(1,100)<branchchance) Then 
	drawlightning(ox,oy,ox+Rand(-wr1,wr2),oy+Rand(-hr1,hr2),depth+1)
EndIf 

Next 


End Function 

Function ColorCentre()
;Color of centre of lightning strike
;
;;

Color 255,255,255

End Function

Function ColorInnerEdge()
;;
;
;Main color theme of lightning strike
;
;


Color 0,128,255

End Function

Function ColorOuterEdge()
;
;Faint outer colour of lightning strike...
;
;


Color 0,32,128

End Function 



;;EXAMPLE
Graphics 800,600,0,2
SetBuffer BackBuffer()

Repeat

	Cls
	DrawLightning(400,300,MouseX(),MouseY())
	Text 0,0,"Move the mouse to move the lightning target position"
	Flip
Until KeyDown(1)
End
