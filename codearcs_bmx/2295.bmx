; ID: 2295
; Author: Warpy
; Date: 2008-08-11 17:26:01
; Title: Draw outlines of overlapped circles
; Description: Draw only the border of shapes made of multiple intersecting circles

Type circle
	Field x#,y#,r#
	Field nonarcs:TList
	Field vx#,vy#

	Method New()
		nonarcs=New TList
	End Method

	Function Create:circle(x#,y#,r#)
		c:circle=New circle
		c.x=x
		c.y=y
		c.r=r
		an#=Rnd(360)
		v#=Rnd(.5,2)
		c.vx=v*Cos(an)
		c.vy=v*Sin(an)
		Return c
	End Function
	
	Method update()
		x:+vx
		If x<0 Or x>800
			vx=-vx
			x:+vx
		EndIf
		y:+vy
		If y<0 Or y>800
			vy=-vy
			y:+vy
		EndIf
	End Method
	
	Method intersect(c2:circle)
		debugo "<<<<<"
		dx#=c2.x-x
		dy#=c2.y-y
		d=Sqr(dx*dx+dy*dy)
		If d>=r+c2.r Then Return
		
		fullcircle:nonarc=nonarc.Create(0,360)
		If r>c2.r
			If d+c2.r<=r
				c2.nonarcs=New TList
				c2.nonarcs.addlast fullcircle
				debugo "removed second circle!"
				Return
			EndIf
		Else
			If d+r<=c2.r
				nonarcs=New TList
				nonarcs.addlast fullcircle
				debugo "removed first circle"
				Return
			EndIf
		EndIf
		
		dx:/d
		dy:/d
		
		b#=(r*r-c2.r*c2.r+d*d)/(2*d)
		theta#=ACos(b/r)
		phi#=ATan2(dy,dx)
		addnonarc(phi-theta,phi+theta)
		
		Rem
		If anbetween(0,phi-theta,phi+theta)
			SetColor 255,0,0
		Else
			SetColor 0,0,255
		EndIf
		
		x1#=x+Cos(phi-theta)*r
		y1#=y+Sin(phi-theta)*r
		x2#=x+Cos(phi+theta)*r
		y2#=y+Sin(phi+theta)*r
		DrawText "1",x1,y1
		DrawText "2",x2,y2
		
		SetColor 255,255,255
		EndRem
		
		theta2#=ACos((d-b)/c2.r)
		phi2#=phi+180
		c2.addnonarc(phi2-theta2,phi2+theta2)

		Rem
		x3#=c2.x+Cos(phi2-theta2)*c2.r
		y3#=c2.y+Sin(phi2-theta2)*c2.r
		x4#=c2.x+Cos(phi2+theta2)*c2.r
		y4#=c2.y+Sin(phi2+theta2)*c2.r
		DrawText "3",x3+5,y3
		DrawText "4",x4+5,y4
		EndRem
	End Method
	
	Method addnonarc(b1#,b2#)
		newna:nonarc=nonarc.Create(b1,b2)
		For na:nonarc=EachIn nonarcs
			debugo "compare "+newna.repr()+" with "+na.repr()
			If na.contains(b1)
				If na.contains(b2)
					If newna.contains(na.an1) And newna.contains(na.an2)
						debugo("both arcs contained in each other - full circle")
						nonarcs=New TList
						nonarcs.addlast nonarc.Create(0,360)
						Return
					EndIf
					debugo("new arc wholly contained in old arc")
					Return
				Else
					debugo("a1->b2")
					nonarcs.remove na
					addnonarc(na.an1,b2)
					Return
				EndIf
			Else
				If na.contains(b2)
					debugo("b1->a2")
					nonarcs.remove na
					addnonarc(b1,na.an2)
					Return
				Else
					If newna.contains(na.an1)
						debugo("old arc wholly contained in new arc")
						nonarcs.remove na
					Else
						debugo("arcs don't intersect")
					EndIf
				EndIf
			EndIf
		Next
		nonarcs.addlast newna
	End Method
	
	Method draw()
		DrawText nonarcs.count(),x+8,y
		Select nonarcs.count()
		Case 0 'this circle doesn't intersect any other, so just draw all of it
			drawarc(0,360)
		Case 1 'this circle intersects one other, so only need to look at one nonarc
			na:nonarc=nonarc(nonarcs.first())
			'If na.an1=0 And na.an2=360 Then Return
			drawarc(na.an2,na.an1)
		Default 'this circle intersects several others, so work through list of nonarcs, drawing 
				'from end of previous one to start of next one
			SortList nonarcs,True,nonarc.comparestarts
			ona:nonarc=nonarc(nonarcs.last())
			For na:nonarc=EachIn nonarcs
				drawarc(ona.an2,na.an1)
				ona=na
			Next
		End Select

		If drawnonarcs
			SetColor 255,0,0
			For na:nonarc=EachIn nonarcs
				drawarc(na.an1,na.an2)
			Next
			SetColor 255,255,255
		EndIf
	End Method
		
	Method drawarc(a#,b#)
		If b<a Then b:+360	
		steps=2*Pi*r/10
		s#=360.0/steps
		theta#=a
		ox#=x+r*Cos(theta)
		oy#=y+r*Sin(theta)
		While theta<b
			dx#=x+r*Cos(theta)
			dy#=y+r*Sin(theta)
			DrawLine ox,oy,dx,dy
			ox=dx
			oy=dy
			theta:+s
		Wend
		DrawLine ox,oy,x+r*Cos(b),y+r*Sin(b)
	End Method
End Type
Type nonarc
	Field an1#,an2#
	
	Function Create:nonarc(an1#,an2#)
		na:nonarc=New nonarc
		an1=an1 Mod 360
		If an1<0 Then an1:+360
		If an2<>360 Then an2=an2 Mod 360
		If an2<0 Then an2:+360
		na.an1=an1
		na.an2=an2
		Return na
	End Function
	
	Method contains(an#)
		d1#=andiff(an2,an1)
		If d1<0 Then d1:+360
		d2#=andiff(an,an1)
		If d2=0 Then Return 1
		If d2<0 Then d2:+360
		debugo "     "+d1
		debugo "     "+d2
		If d2<d1 Then Return 1 Else Return 0
	End Method
	
	Function comparestarts(o1:Object,o2:Object)
		na1:nonarc=nonarc(o1)
		na2:nonarc=nonarc(o2)
		If nicean(na1.an1)<nicean(na2.an1) Then Return -1 Else Return 1
	End Function
	
	Method repr$()
		Return "("+String(Int(an1))+","+String(Int(an2))+")"
	End Method
End Type

Function nicean#(an#)
	an=an Mod 360
	If an<=-180 Then an:+360
	Return an
End Function

Function andiff#(an1#,an2#)
	'debugo String(an1)+" , "+String(an2)
	If an2=0 And an1=360 Then Return 360
	dan#=(an1-an2) Mod 360
	If dan>180 dan:-360
	If dan<-180 dan:+360
	Return dan
End Function

Function anbetween(an#,an1#,an2#)
End Function

Function intersectcircles(circles:TList)
	For c:circle=EachIn circles
		c.nonarcs=New TList
	Next
	
	
	l:TList=circles.copy()
	While l.count()>1
		debugo "?????"
		c1:circle=circle(l.removefirst())
		For c2:circle=EachIn l
			c1.intersect(c2)
		Next
	Wend
End Function


''''TEST EXAMPLE


Global debugging=0

Function debugo(txt$)
	If debugging
		Print txt
	EndIf
End Function

Global drawnonarcs=0

circles:TList=New TList
SeedRnd MilliSecs()
For n=0 To 20
	circles.addlast circle.Create(Rand(300,500),Rand(300,500),Rand(30,100))
Next
c2:circle=circle.Create(400,400,80)
circles.addlast c2

Graphics 800,800,0

While Not KeyHit(KEY_ESCAPE) Or AppTerminate()
	c2.x=MouseX()
	c2.y=MouseY()
	
	If MouseHit(2)
		debugging=1
		debugo "------------------"
	Else
		debugging=0
	EndIf
	
	If MouseHit(1)
		drawnonarcs=1-drawnonarcs
	EndIf

	For c:circle=EachIn circles
		c.update()
	Next	
	
	intersectcircles(circles)
	
	For c:circle=EachIn circles
		c.draw()
	Next	
	
	DrawText "click mouse to see non-arcs",0,0

	Flip
	Cls
Wend
