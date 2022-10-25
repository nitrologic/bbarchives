; ID: 2437
; Author: Warpy
; Date: 2009-03-20 10:23:47
; Title: Dendrogram
; Description: Create a dendrogram from a set of data

Type dendrogram
	Field d1:dendrogram,d2:dendrogram
	Field o:Object
	Field dist#
	
	Function Create:dendrogram(objects:TList,dist#(o1:Object,o2:Object))
		Local l:TList=New TList
		For o:Object=EachIn objects
			d:dendrogram=New dendrogram
			d.o=o
			l.addlast d
		Next
		While l.count()>1
			nl:TList=l.copy()
			mindist#=-1
			Local md1:dendrogram,md2:dendrogram
			While nl.count()
				d1:dendrogram=dendrogram(nl.removefirst())
				For d2:dendrogram=EachIn nl
					td#=0
					n=0
					For o1:Object=EachIn d1
					For o2:Object=EachIn d2
						td:+dist(o1,o2)
						n:+1
					Next
					Next
					td:/n
					If td<mindist Or mindist=-1
						mindist=td
						md1=d1
						md2=d2
					EndIf
				Next
			Wend
			
			l.remove md1
			l.remove md2
			l.addlast dendrogram.link(md1,md2,mindist)
			
		Wend
		Return dendrogram(l.first())
	End Function
	
	Function link:dendrogram(d1:dendrogram,d2:dendrogram,dist#)
		d:dendrogram=New dendrogram
		d.d1=d1
		d.d2=d2
		d.dist=dist
		Return d
	End Function
	
	Method ObjectEnumerator:dendroEnum()
		Return dendroEnum.Create(Self)
	End Method
	
	
	'draw the actual dendrogram
	Method draw(minx#,maxx#)
		mx#=(minx+maxx)/2
		If o
			SetColor 255,0,0
			SetAlpha 1
			DrawRect mx-2,590,5,10
		Else
			SetColor 255,255,0
			SetAlpha 1
			d1.draw minx,mx
			d2.draw mx,maxx
			x1#=(minx+mx)/2
			x2#=(mx+maxx)/2
			y1#=600-d1.dist
			y2#=600-d2.dist
			y3#=600-dist
			DrawLine x1,y1,x1,y3
			DrawLine x2,y2,x2,y3
			DrawLine x1,y3,x2,y3
		EndIf
	End Method
End Type

Type dendroEnum
	Field d:dendrogram
	Field de:dendroEnum
	Field state
	
	Function Create:dendroEnum(d:dendrogram)
		de:dendroEnum=New dendroEnum
		de.d=d
		If d.o
			de.state=0
		Else
			de.de=d.d1.objectenumerator()
			de.state=1
		EndIf
		Return de
	End Function
	
	Method hasnext()
		Select state
		Case -1
			Return False
		Case 0
			Return True
		Case 1
			If de.hasnext()
				Return True
			Else
				de=d.d2.objectenumerator()
				state=2
				Return de.hasnext()
			EndIf
		Case 2
			Return de.hasnext()
		End Select
	End Method
	
	Method nextobject:Object()
		Select state
		Case 0
			state=-1
			Return d.o
		Case 1,2
			Return de.nextobject()
		End Select
	End Method
End Type

'example - cluster 2d points based on geometric distance
Type point
	Field x#,y#
	
	Function Create:point(x#,y#)
		p:point=New point
		p.x=x
		p.y=y
		Return p
	End Function
	
	Function dist#(o1:Object,o2:Object)
		p1:point=point(o1)
		p2:point=point(o2)
		dx#=p2.x-p1.x
		dy#=p2.y-p1.y
		Return Sqr(dx*dx+dy*dy)
	End Function
	
	Method draw()
		DrawOval x-2,y-2,4,4
	End Method
End Type

'draw points in 2d-space, and show dendrogram links
Function drawdendro(d:dendrogram, mx# Var, my# Var)
	If d.o
		p:point=point(d.o)
		SetAlpha 1
		p.draw
		mx=p.x
		my=p.y
	Else
		SetColor 255,255,255
		Local x1#,y1#,x2#,y2#
		drawdendro d.d1,x1,y1
		drawdendro d.d2,x2,y2
		mx=(x1+x2)/2
		my=(y1+y2)/2
		SetAlpha .2
		DrawLine x1,y1,x2,y2
		DrawText d.dist,mx,my
	EndIf
End Function



AppTitle="dendrogramania"
Graphics 600,600,0
SetBlend ALPHABLEND

points:TList=New TList
Local d:dendrogram

While Not (KeyHit(KEY_ESCAPE) Or AppTerminate())

	If MouseHit(1)
		points.addlast point.Create(MouseX(),MouseY())
	EndIf
	
	If MouseHit(2)
		points=New TList
		d=Null
	EndIf
	
	If points.count()
		d=dendrogram.Create(points,point.dist)
		Local x#,y#
		depth=0
		drawdendro d,x,y
		d.draw 0,600
	EndIf

	Flip
	Cls

Wend
