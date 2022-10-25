; ID: 998
; Author: Jeppe Nielsen
; Date: 2004-04-15 12:19:59
; Title: 2D Collision Example
; Description: Create circles and lines that collide with each other.

;2D Collision Example By Jeppe Nielsen 2004
;nielsen_jeppe@hotmail.com
;

;Added June 28th 2004:
;global variables to get the intersection or collision point between a line and a circle.

;use these globals to get the collision point between a circle and a line. 
Global LineCollisionX#
Global LineCollisionY#

Graphics 800,600,16,2
SetBuffer BackBuffer()

;Create lines at screen edges
LineNew(0,0,800,0)
LineNew(800,0,800,600)
LineNew(0,600,800,600)
LineNew(0,0,0,600)

;Create two Circles
CircleNew(100,100,20,0.4,0.2)
CircleNew(200,100,50,-0.2,0.3)

event=0
cline.cline=Null
Repeat
	Cls
		
	If MouseHit(2)
		
		CircleNew(Rnd(200,600),Rnd(100,500),Rnd(10,40),Rnd(-2,2),Rnd(-2,2))
	
	EndIf
	
	Select event
	
		Case 0
			If MouseHit(1)
				x1#=MouseX()
				y1#=MouseY()
				event=1
				cline=LineNew(x1#,y1#,x1+10,y1)
			EndIf
		Case 1
					
			LineRecalc(cline,x1,y1,MouseX(),MouseY())
			If MouseHit(1)
				event=0
			EndIf
		
	
	End Select
	
	;Update and draw stuff
	CircleUpdate()
	PoofUpdate()

	CircleDraw()
	LineDraw()
	PoofDraw()
			
	Text 400,10,"2D Collision Example by Jeppe Nielsen 2004",1,0
	Text 400,30,"Left mouse button to create lines",1,0
	Text 400,50,"Right mouse button to create more circles",1,0
	
	Flip
	
Until KeyDown(1)
End

Type cline

	Field x1#,y1#,x2#,y2#,nx#,ny#,ux#,uy#

End Type

Function LineNew.cline(x1#,y1#,x2#,y2#)

	l.cline=New cline
	
	LineRecalc(l,x1#,y1#,x2#,y2#)
	
	Return l

End Function

Function LineRecalc(l.cline,x1#,y1#,x2#,y2#)

	l\x1=x1
	l\y1=y1
	l\x2=x2
	l\y2=y2

	dx#=l\x2-l\x1
	dy#=l\y2-l\y1
	
	d#=Sqr(dx*dx+dy*dy)
	If d#<0.0001
		d#=0.0001
	EndIf
	
	l\ux=dx/d
	l\uy=dy/d
	
	l\nx#=l\uy
	l\ny#=-l\ux

End Function

Function LineDraw()

For l.cline=Each cline
	
	Color 255,255,255
	Line l\x1,l\y1,l\x2,l\y2
	
	Color 255,255,0
	;Draw normal
	xm#=(l\x1+l\x2)/2.0
	ym#=(l\y1+l\y2)/2.0
	Line xm,ym,xm+l\nx*10,ym+l\ny*10
	
Next

End Function

;Global LineCollisionX#
;Global LineCollisionY#

;Returns the shortest distance from a point to a line
;Use LineCollisionX and LineCollisionY to get the collision point.
Function LineDistance#(l.cline,x#,y#)

	dx#=l\x2-l\x1
	dy#=l\y2-l\y1
	
	d#=Sqr(dx*dx+dy*dy)
	
	px#=l\x1-x#
	py#=l\y1-y#
	
	dist#=(dx*py-px*dy) / d
	
	LineCollisionX=x#-l\nx*dist#
	LineCollisionY=y#-l\ny*dist#
		
	Return Abs(dist#)
	
End Function

;Returns true if a point collides with a line within range r
Function LineCollide(l.cline,x#,y#,r#)
	
	dx1#=x-(l\x1-l\ux*r)
	dy1#=y-(l\y1-l\uy*r)
	
	d#=Sqr(dx1*dx1+dy1*dy1)
	
	dx1=dx1/d
	dy1=dy1/d
	
	dx2#=x-(l\x2+l\ux*r)
	dy2#=y-(l\y2+l\uy*r)
	
	d#=Sqr(dx2*dx2+dy2*dy2)
	
	dx2=dx2/d
	dy2=dy2/d
	
	dot1#=dx1*l\ux+dy1*l\uy
	dot2#=dx2*l\ux+dy2*l\uy
	
	Return ((dot1#>=0 And dot2#<=0) Or (dot1#<=0 And dot2#>=0)) And (LineDistance(l,x,y)<=r)
	
End Function

Type circle

	Field x#,y#,vx#,vy#
	
	Field vel#

	Field r#

End Type

Function CircleNew.circle(x#,y#,r#=50,vx#=0,vy#=0)
	
	c.circle=New circle
	c\x=x
	c\y=y
	c\r=r
	c\vx=vx
	c\vy=vy
	
	CirclePlace(c)
	
	Return c
End Function

Function CirclePlace(c.circle,w#=800,h#=600)
	
	num=0
	While CirclePlaceTest(c,c\x,c\y)=False And num<1000
	
		c\x=Rnd(w)
		c\y=Rnd(h)
	
		num=num+1
	
	Wend
	
End Function

;Returns true if a circle can be placed, it doesn´t collide with any other circles or lines
Function CirclePlaceTest(c.circle,x#,y#)

	For cc.circle=Each circle
		If cc<>c
			dx#=cc\x-c\x
			dy#=cc\y-c\y
			d#=Sqr(dx*dx+dy*dy)
			If d<(c\r+cc\r)
		
				Return False
				
			EndIf
		EndIf
	Next

	For l.cline=Each cline

		If LineCollide(l,c\x,c\y,c\r)
				
			Return False
					
		EndIf

	Next

Return True

End Function

;draw circles
Function CircleDraw()

Color 0,0,255
For c.circle=Each circle
	
	rh#=c\r*2

	Oval c\x-c\r,c\y-c\r,rh,rh

Next


End Function

Function CircleUpdate()

For c.circle=Each circle

	;Calculate total velocity
	c\vel#=Sqr(c\vx*c\vx+c\vy*c\vy)
	
	;collision against other circles
	For cc.circle=Each circle
		;do not test against itself
		If cc<>c
			;vector from one circle to another
			dx#=cc\x-c\x
			dy#=cc\y-c\y
			d#=Sqr(dx*dx+dy*dy)
			;check of distance is smaller than the two circle´s radii together
			If d<(c\r+cc\r)
		
				;make the vector a unit vector (length=1), multiply it with the circle´s
				;total velocity, to get the new motion vector
				c\vx=(-dx#/d) * c\vel
				c\vy=(-dy#/d) * c\vel
			
			EndIf
		EndIf
	Next
	
	;collision agains lines
	For l.cline=Each cline

		;Check if circle collides with a line
		If LineCollide(l,c\x,c\y,c\r)
		
			;create a mark, where the circle has colliede with the line
			PoofNew(LineCollisionX,LineCollisionY)
				
			;Get the dot product between the circles motion vector and the line´s normal vector
			dot#=c\vx*l\nx+c\vy*l\ny	
			
			;Calculate the circle´s new motion vector
			c\vx=c\vx-2.0*l\nx*dot
			c\vy=c\vy-2.0*l\ny*dot
					
		EndIf

	Next
	
	;add velocity to position
	c\x=c\x+c\vx
	c\y=c\y+c\vy
	
	;Wrap to screen boundaries
	If c\x>GraphicsWidth()
		c\x=0
	EndIf
	
	If c\y>GraphicsHeight()
		c\y=0
	EndIf

	If c\x<0
		c\x=GraphicsWidth()
	EndIf	
	
	If c\y<0
		c\y=GraphicsHeight()
	EndIf
Next

End Function

Type poof

	Field x,y
	
	Field age#
	
	Field maxage
	
End Type

Function PoofNew.poof(x,y,age#=20)
	
	p.poof=New poof
	
	p\x=x
	p\y=y
	p\maxage=age
	
	Return p
End Function

Function PoofUpdate()

For p.poof=Each poof
	p\age=p\age+1

	If p\age>=p\maxage
	
		Delete p
		
	EndIf

Next

End Function

Function PoofDraw()

For p.poof=Each poof
			
	pah=p\age*0.5
		
	Oval p\x-pah,p\y-pah,p\age,p\age,0
		
Next

End Function
