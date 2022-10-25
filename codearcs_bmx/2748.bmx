; ID: 2748
; Author: Warpy
; Date: 2010-08-01 11:32:20
; Title: Enumerable bezier curve
; Description: A bezier curve whose points can be enumerated by EachIn

Type bezier
	Field x1#,y1#,x2#,y2#,x3#,y3#,x4#,y4#
	Field inc#
	
	Function Create:bezier(x1#,y1#,x2#,y2#,x3#,y3#,x4#,y4#,inc#=-1)
		b:bezier=New bezier
		b.x1=x1
		b.y1=y1
		b.x2=x2
		b.y2=y2
		b.x3=x3
		b.y3=y3
		b.x4=x4
		b.y4=y4
		
		If inc=-1
			dx#=x4-x1
			dy#=y4-y1
			d#=Sqr(dx*dx+dy*dy)
			inc=1/d
		EndIf
		b.inc=inc
		
		Return b
	End Function
	
	Method objectenumerator:bezierenumerator()
		Return bezierenumerator.Create(Self)
	End Method
	
	Method pos(t#,x# Var, y# Var)
		nt#=1-t
		x=nt*nt*nt*x1 + 3*nt*nt*t*x2 + 3*nt*t*t*x3 + t*t*t*x4
		y=nt*nt*nt*y1 + 3*nt*nt*t*y2 + 3*nt*t*t*y3 + t*t*t*y4
	End Method
End Type

Type bezierenumerator
	Field b:bezier
	Field t#

	Function Create:bezierenumerator(b:bezier)
		be:bezierenumerator=New bezierenumerator
		be.b=b
		Return be
	End Function
	
	Method HasNext()
		If t<=1 Return 1
	End Method

	Method NextObject:Object()
		Local point#[2]
		b.pos t,point[0],point[1]
		If t=1
			t:+1
			point=[b.x4,b.y4]
			Return point
		EndIf
		t:+b.inc
		If t>1 t=1
		Return point
	End Method
End Type

'usage
b:bezier = bezier.create( 0,0, 100,100, 200,200, 300,0 )

'you can use EachIn to get all the points along the curve
Local point#[2]

For point=EachIn b
	DrawRect point[0],point[1],1,1
Next

'or you can do it directly
Local x#,y#
Local t#=0

For t=0 to 1 step 0.01
	b.pos t,x,y
	DrawRect x,y,1,1
Next
