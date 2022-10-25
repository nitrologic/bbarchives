; ID: 1099
; Author: AntMan - Banned in the line of duty.
; Date: 2004-06-28 10:06:01
; Title: Collision2.0 - Motion based, very accurate.
; Description: Small little replacement 2d collision system.

;-quick example

initcol(64) ;init system with 64pixel motion map

col1=createCol(myImage) create a col image.call this once for each unique image.

moveCol(col1,20,20) ;move it. call this once per frame.

and finally,

checkCol(col1,col2,rate) to check for a collision.
rate= skip rate, see above.


;-[=-]
Type cSys
	Field mRes,iRes
	Field xs#,ys#
	Field gw#,gh#
End Type
Type col
	Field id,mMap ;low res version and motion map.
	Field lx#,ly#,x#,y#
End Type


Global cSys.cSys
Function initCols(mRes=256)
	cSys=New cSys
	csys\mRes=mRes
	cSys\iRes=iRes
	cSys\gw=GraphicsWidth()
	cSys\gh=GraphicsHeight()
	TFormFilter False
End Function
Function createCol(image)
	col.col=New col
	col\id=CopyImage(image)
	col\mMap=CreateImage(csys\mRes,csys\mres)
	wd#=Float(ImageWidth(image))/Float(GraphicsWidth())
	hd#=Float(ImageHeight(image))/Float(GraphicsHeight())
	nw#=csys\mres*wd 
	nh#=csys\mres*hd
	ResizeImage col\id,nw,nh
	Return Handle(col)
End Function

Function moveCol(col,x#,y#) ;move it to the same x,y as the image
	in.col=Object.col(col)
	in\lx=in\x
	in\ly=in\y
	in\x=x
	in\y=y
End Function

Function checkCol(col1,col2,skipRate=5) ;higher skip rate = faster but less accurate.Play with it, diff shapes will fair differantly
	c1.col=Object.col(col1)
	If c1=Null Return
	c2.col=Object.col(col2)
	If c2=Null Return 
	;-
	;-[ Generate motion maps]
	genMotionMap(c1,skipRate)
	genMotionMap(c2,skipRate)
	Return ImagesCollide(c1\mmap,0,0,0,c2\mmap,0,0,0)
End Function

Function genMotionMap(in.col,skip=1) ;INTERNAL FUNCTION - 
	ax#=in\x
	ay#=in\y
	ex#=in\lx
	ey#=in\ly 
	ax=csys\mres*(ax/csys\gw)
	ay=csys\mres*(ay/csys\gh)
	ex=csys\mres*(ex/csys\gw)
	ey=csys\mres*(ey/csys\gh) ;scale coords into the res of the motion map
	
	xd#=ex-ax
	yd#=ey-ay
	If Abs(xd)>Abs(yd) steps#=Abs(xd) Else steps#=Abs(yd)
	xi#=xd/Steps
	yi#=yd/Steps
	SetBuffer ImageBuffer(in\mMap)
	sImg=in\id
	xi=xi*Float(skip)
	yi=yi*Float(skip)
	Cls
	If steps<1 steps=1
	Repeat
			;-
			DrawImage sImg,ax,ay
			ax=ax+xi
			ay=ay+yi
		;-
		steps=steps-skip
	Until steps<1
End Function
