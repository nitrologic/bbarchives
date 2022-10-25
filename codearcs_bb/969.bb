; ID: 969
; Author: AntMan - Banned in the line of duty.
; Date: 2004-03-17 04:32:05
; Title: 3D Motion Blur Starfield
; Description: A 3D motion blurred starfield done in 2D.

Graphics 640,480,32,1
SetBuffer BackBuffer()

;--
Function Line(x#,y#,x1#,y1#,cv#)
	xd=x1-x
	yd=y1-y
	If Abs(xd)>Abs(yd) steps=Abs(xd) Else steps=Abs(yd)
	If steps<2 steps=2
	xi#=xd/Float(steps)
	yi#=yd/Float(steps)
	ci#=cv/Float(steps)
	av#=0
	For steps=steps To 1 Step -1
		WritePixelFast x,y,av Or (av Shl 8) Or (av Shl 16)
		av=av+ci
		x=x+xi
		y=y+yi		
	Next
	
End Function


;--Star

Dim mt#(4,4,8) ;Eight matrix buffers

Global sCount

Type vector

Field x#,y#,z#

End Type

Type star
    Field ox,oy,oz
    Field loc.vector,oT,reset
End Type

;-Cam

Global cX#,cY#,cZ#

Global cP#,cT#,cR# ;cT = CYaw.


initStars(5000)

Repeat:Cls
	cR=cR+1
	cP = cP+ MouseXSpeed()*0.2
	cT = cT+ MouseYSpeed()*0.2
	cZ=cZ+MouseDown(1)*6
	cZ=cZ-MouseDown(2)*6

	MoveMouse 320,240
	FlushMouse 
	starCycle()
Flip:Until KeyDown(1)

EndGraphics
End

;-Star Functions


Function initStars(starCount = 1000)
   Local star.star
   For j = 1 To starCount
      star = New star
      star\loc = vector( Rnd(-3000,3000),Rnd(-3000,3000),          Rnd(-4000,4000) )
   Next
End Function

Const s1# = 255*256

Function starCycle()

Local star.star,dp.vector,oz

dp = New vector

gen_x_mat(1,cP)

gen_y_mat(2,cT)

multi_mat(1,2,3)

gen_z_mat(2,cR)

gen_t_mat(5, cX,cY,cZ)

multi_mat(3,2,1)

multi_mat(1,5,2)

i=2

LockBuffer 

For star = Each star



oz = ((mt(1,3,i) * star\loc\x) + (mt(2,3,i) * star\loc\y) + (mt(3,3,i) * star\loc\z) + mt(4,3,i) ) + 256

cV = ((s1) / ( oz)) 



nX=320+ ( ((mt(1,1,i) * star\loc\x) + (mt(2,1,i) * star\loc\y) + (mt(3,1,i) * star\loc\z) + mt(4,1,i))   *256) / (oz)



nY=200+ ((  (mt(1,2,i) * star\loc\x) + (mt(2,2,i) * star\loc\y) + (mt(3,2,i) * star\loc\z) + mt(4,2,i))  *256) / (oz) 

If cv<0 cv=0
If cv>255 cv=255

If ny>0 And  ny<480 And nx>0 And nx<640

	If star\reset=True
		star\ox=nx
		star\oy=ny
		star\reset=False
	EndIf

	Line star\ox,star\oy,nx,ny,cv
Else
	star\reset=True
EndIf

star\ox=star\ox+(nx-star\ox)*0.25
star\oy=star\oy+(ny-star\oy)*0.25


Next

UnlockBuffer

Delete dp

Return sRen

End Function



;--Music


Function playTrack(num=1)

PlayMusic("t"+num+".mid")

End Function


Function gen_x_mat(i,a#)

	mt(1,1,i)=1:mt(2,1,i)=0:mt(3,1,i)=0:mt(4,1,i)=0

	mt(1,2,i)=0:mt(2,2,i)=Cos(a):mt(3,2,i)=Sin(a):mt(4,2,i)=0

	mt(1,3,i)=0:mt(2,3,i)=-Sin(a):mt(3,3,i)=Cos(a):mt(4,3,i)=0

	mt(1,4,i)=0:mt(2,4,i)=0:mt(3,4,i)=0:mt(4,4,i)=1

End Function

Function gen_y_mat(i,a#)

	mt(1,1,i)=Cos(a):mt(2,1,i)=0:mt(3,1,i)=-Sin(a):mt(4,1,i)=0

	mt(1,2,i)=0:mt(2,2,i)=1:mt(3,2,i)=0:mt(4,2,i)=0
	
	mt(1,3,i)=Sin(a):mt(2,3,i)=0:mt(3,3,i)=Cos(a):mt(4,3,i)=0

	mt(1,4,i)=0:mt(2,4,i)=0:mt(3,4,i)=0:mt(4,4,i)=0

End Function

Function gen_z_mat(i,a#)

	mt(1,1,i)=Cos(a):mt(2,1,i)=Sin(a):mt(3,1,i)=0:mt(4,1,i)=0

	mt(1,2,i)=-Sin(a):mt(2,2,i)=Cos(a):mt(3,2,i)=0:mt(4,2,i)=0

	mt(1,3,i)=0:mt(2,3,i)=0:mt(3,3,i)=1:mt(4,3,i)=0

	mt(1,4,i)=0:mt(2,4,i)=0:mt(3,4,i)=0:mt(4,4,i)=0

End Function


Function gen_t_mat(i,x#,y#,z#)

	mt(1,1,i)=1:mt(2,1,i)=0:mt(3,1,i)=0:mt(4,1,i)=x

	mt(1,2,i)=0:mt(2,2,i)=1:mt(3,2,i)=0:mt(4,2,i)=y

	mt(1,3,i)=0:mt(2,3,i)=0:mt(3,3,i)=1:mt(4,3,i)=z

	mt(1,4,i)=0:mt(2,4,i)=0:mt(3,4,i)=0:mt(4,4,i)=1

End Function

Function multi_mat(i1,i2,i3) ;Takes matrices i1 and i2 and combines them, resulting in i3

	For m=1 To 4
	For m1=1 To 4
		mt(m,m1,i3)=0
	For m2=1 To 4
		mt(m,m1,i3)=mt(m,m1,i3)+mt(m,m2,i2)*mt(m2,m1,i1)
	Next
	Next
	Next

End Function


Function vector.vector(x#=0,y#=0,z#=0) 
	v.vector=New vector:v\x=x:v\y=y:v\z=z
	Return v
End Function


;------------------
