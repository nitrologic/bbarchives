; ID: 3030
; Author: TAS
; Date: 2013-02-08 20:35:57
; Title: Rotate a hollow box
; Description: Rotation

Graphics 800,600
Cls 
SetBlend SOLIDBLEND
SetColor 200,200,200
SetScale 2,2
DrawRect 200,200,100,50
SetColor 255,0,0
SetHandle 0,0
SetLineWidth 2
Rot_Box(200,200,100,50,45)
Rot_Box(200,200,100,50,90)
Rot_Box(200,200,100,50,180)

Flip
WaitKey()


Function RotX#(x#,y#,deg#)
	Return x*Cos(deg)-y*Sin(deg)
End Function

Function RotY#(x#,y#,deg#)
	Return x*Sin(deg)+y*Cos(deg)
End Function

Function Rot_Box(bx#,by#,w#,h#,deg#)
	'bx,by == upper right corner
	'draw a box rotated about its center
	Local s1#,s2#
	GetScale(s1,s2)
	SetScale 1,1
	w=w/2*s1
	h=h/2*s2
	'calc center of rectangle
	x=bx+w
	y=by+h
	'calc corner points after rotating about center
	x1=x+RotX#(-w,-h,deg)
	y1=y+RotY#(-w,-h,deg)
	x2=x+RotX#( w,-h,deg)
	y2=y+RotY#( w,-h,deg)
	x3=x+RotX#(-w, h,deg)
	y3=y+RotY#(-w, h,deg)
	x4=x+RotX#( w, h,deg)
	y4=y+RotY#( w, h,deg)

	'draw the box
	DrawLine x1,y1,x2,y2
	DrawLine x1,y1,x3,y3
	DrawLine x2,y2,x4,y4
	DrawLine x3,y3,x4,y4
	'restore scale
	SetScale s1,s2
End Function
