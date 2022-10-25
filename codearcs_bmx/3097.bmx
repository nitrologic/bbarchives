; ID: 3097
; Author: Pineapple
; Date: 2014-01-06 10:15:13
; Title: Line Graph
; Description: Interactive line graph, good for UIs

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--


SuperStrict

'Framework brl.glmax2d ' only needed for example program
Import brl.max2d
Import brl.polledinput
Import "vect2d.bmx"		' Code archive: http://blitzbasic.com/codearcs/codearcs.php?code=2960
Import "interpolate.bmx"	' Code archive: http://blitzbasic.com/codearcs/codearcs.php?code=2955
Import "mouse.bmx"		' Code archive: http://blitzbasic.com/codearcs/codearcs.php?code=3072


' Example Program

Rem

' Set up the window
AppTitle="Graph thing"
Const gw%=512,gh%=256
Graphics gw,gh
SetClsColor 32,32,32

' Create the graph object
Local g:graph=New graph

' Main loop
Repeat
	Cls
	
	' Draw the stuff
	Const margin%=32
	SetColor 0,0,0
	DrawRect margin,margin,gw-margin-margin,gh-margin-margin
	SetColor 255,255,255
	g.render margin,margin,gw-margin-margin,gh-margin-margin
	
	' Information about the selected vector
	Local o:vect2d=g.mouseovervect()
	Local vt$="Vector: ( Mouse over for info )"
	If o Then vt="Vector: ( "+o.x+" , "+o.y+" )"
	DrawText "Value at mouse X: "+g.getvalue(Double(mx-margin)/(gw-margin-margin)),2,gh-30
	DrawText vt,2,gh-16
	
	' Update the stuff
	g.update margin,margin,gw-margin-margin,gh-margin-margin
	updatemouse
	
	Flip
	If AppTerminate() End
Forever

EndRem



' Has a list of points and an interpolation function.
Type func
	' Interpolation function
	Field interpolate!(a!,b!,x!)=interpolate_linear
	' List of vect2d objects
	Field vects:TList=CreateList()
	' Get the Y value for some X
	' Returns 0 if there are no vectors in the list. Returns the value of the nearest vector if the X is outside the func's range.
	Method getvalue!(x!)
		If vects.isempty() Return 0
		Local pvx!=-1,pvy!=0
		For Local v:vect2d=EachIn vects
			If v.x>x Then
				If pvx=-1 Then
					Return v.y
				Else
					Return interpolate(pvy,v.y,(x-pvx)/(v.x-pvx))
				EndIf
			EndIf
			pvx=v.x;pvy=v.y
		Next
		Return pvy
	End Method
End Type

' A UI thing for the func object. Its func object will always keep its vectors within 0-1 inclusive on both axes.
Type graph
	' The func object
	Field f:func=New func
	' Size of squares to draw a vector and the size of the outline for if it's been selected
	Field vectsize%=4,selvectsize%=10
	' A bunch of random crap for handling selection of one or more vectors
	Field sel:TLink,seldist%=14
	Field closest:vect2d=Null,cd!=0,clink:TLink
	Field sellist:TList,shiftmode%=shiftnull,shiftstart:vect2d=New vect2d,shiftend:vect2d=New vect2d,selectinglist%=0
	Const shiftnull%=0,shiftselect%=1,shiftdelete%=2
	Field unselatmouseup%=0
	' Renders the graph to the specified rectangle
	' Setting interp to true uses the func object's interpolation function, setting to false uses linear. (Assuming linear is a lot faster)
	Method render(x%,y%,w%,h%,interp%=False)
		Local pvx!=-1,pvy!=-1
		For Local v:vect2d=EachIn f.vects
			If pvx>=0 Then renderline pvx,pvy,v.x,v.y,x,y,w,h,interp
			Local vx!=v.x*w+x,vy!=v.y*h+y
			DrawRect vx-vectsize/2,vy-vectsize/2,vectsize,vectsize
			pvx=v.x;pvy=v.y
		Next
		If sellist
			For Local link:TLink=EachIn sellist
				Local v:vect2d=vect2d(link._value)
				Local vx!=v.x*w+x,vy!=v.y*h+y
				drawrectoutline vx-selvectsize/2,vy-selvectsize/2,selvectsize,selvectsize
			Next
		ElseIf sel Or (closest And cd<seldist) Then
			Local v:vect2d=closest
			If sel Then v=vect2d(sel._value)
			Local vx!=v.x*w+x,vy!=v.y*h+y
			drawrectoutline vx-selvectsize/2,vy-selvectsize/2,selvectsize,selvectsize
		EndIf
		If shiftmode<>shiftnull Then
			Local vx1!=shiftstart.x*w+x,vy1!=shiftstart.y*h+y
			Local vx2!=shiftend.x*w+x,vy2!=shiftend.y*h+y
			drawrectoutline Min(vx1,vx2),Min(vy1,vy2),Abs(vx1-vx2),Abs(vy1-vy2)
		EndIf
	End Method
	' Draws a line for the graph. Setting interp to true uses the func object's interpolation function, setting to false uses linear. (Assuming linear is a lot faster)
	' (You shouldn't need to do anything with this)
	Method renderline(x1!,y1!,x2!,y2!,x%,y%,w%,h%,interp%=False)
		Local px1!=x1*w+x,py1!=y1*h+y
		Local px2!=x2*w+x,py2!=y2*h+y
		If Not interp
			DrawLine px1,py1,px2,py2
		Else
			Local py%=0
			If px1=px2 Then
				DrawLine px1,py1,px2,py2
			Else
				For Local lx%=px1 Until px2
					Local ix!=Double(lx-x)/w
					Local iy!=f.getvalue(ix)
					Local ny%=iy*h+y
					If lx>px1 Then DrawLine lx-1,py,lx,ny
					py=ny
				Next
			EndIf
		EndIf
	End Method
	' Returns the vector the mouse is over, if any
	Method mouseovervect:vect2d()
		If sel Then Return vect2d(sel._value)
		If closest And cd<seldist Then
			If sellist
				For Local link:TLink=EachIn sellist
					If vect2d(link._value)=closest Then Return closest
				Next
				Return Null
			Else
				Return closest
			EndIf
		EndIf
		Return Null
	End Method
	' Get the value at a specified x (0-1 inclusive)
	Method getvalue!(x!)
		Return f.getvalue(x)
	End Method
	' Updates the graph object, allowing for mouse input
	Method update(x%,y%,w%,h%)
		Local px!=Double(mx-x)/w,py!=Double(my-y)/h
		Local mi%=mousein(x,y,w,h)
		Local shift%=shiftdown()
		' Clickkity
		If unselatmouseup And (Not lmbd) Then sellist=Null;sel=Null;unselatmouseup=0
		If mi Then
			closest=Null;cd=0;clink=Null
			Local link:TLink=f.vects._head._succ
			While link<>f.vects._head
				Local v:vect2d=vect2d(link._value)
				Local vx!=v.x*w+x,vy!=v.y*h+y
				Local d!=Max(Abs(mx-vx),Abs(my-vy))
				If closest=Null Or d<=cd Then
					closest=v;cd=d;clink=link
				EndIf
				link=link._succ
			Wend
			If shift
				If lmb Or rmb Then
					sellist=Null
					If rmb Then shiftmode=shiftdelete Else shiftmode=shiftselect
					shiftstart.x=px;shiftstart.y=py
					shiftend.x=px;shiftend.y=py
				EndIf
			Else
				If closest And cd<seldist Then
					If lmb Then
						sel=clink
						If sellist And sellist.contains(clink) Then
							selectinglist=1
						Else
							sellist=Null
						EndIf
					ElseIf rmb And clink._pred<>f.vects._head And clink._succ<>f.vects._head
						clink.remove
						sellist=Null
						sel=Null
					EndIf
				ElseIf lmb
					If Not sellist Then
						mouseaddvect(x,y,w,h)
					Else
						sellist=Null
					EndIf
				ElseIf rmb
					sellist=Null
				EndIf
			EndIf
		Else
			closest=Null
		EndIf
		' Shiffity
		If shiftmode<>shiftnull Then
			If Not shift Then
				shiftmode=shiftnull
			Else
				shiftend.x=Min(1,Max(0,px));shiftend.y=Min(1,Max(0,py))
				If (shiftmode=shiftselect And Not lmbd) Or (shiftmode=shiftdelete And Not rmbd) Then
					Local list:TList=selectbox(Min(shiftstart.x,shiftend.x),Min(shiftstart.y,shiftend.y),Max(shiftstart.x,shiftend.x),Max(shiftstart.y,shiftend.y))
					If shiftmode=shiftselect
						sellist=list
					Else
						For Local link:TLink=EachIn list
							If link._pred<>f.vects._head And link._succ<>f.vects._head Then link.remove
						Next
					EndIf
					shiftmode=shiftnull
				EndIf
			EndIf
		EndIf
		' Drag
		If lmbd And sel Then
			If sellist
				Local selv:vect2d=vect2d(sel._value)
				Local dx!=px-selv.x,dy!=py-selv.y
				For Local link:TLink=EachIn sellist
					Local v:vect2d=vect2d(link._value)
					If link._pred=f.vects._head Or link._succ=f.vects._head Then
						dx=0
					Else
						If dx<0
							If Not sellist.contains(link._pred)
								Local pv:vect2d=vect2d(link._pred._value)
								dx=-Min(-dx,v.x-pv.x)
							EndIf
						ElseIf dx>0
							If Not sellist.contains(link._succ)
								Local sv:vect2d=vect2d(link._succ._value)
								dx=Min(dx,sv.x-v.x)
							EndIf
						EndIf
					EndIf
					If dy<0
						dy=-Min(-dy,v.y)
					ElseIf dy>0
						dy=Min(dy,1-v.y)
					EndIf
				Next
				For Local link:TLink=EachIn sellist
					Local v:vect2d=vect2d(link._value)
					v.x:+dx;v.y:+dy
				Next
			Else
				Local v:vect2d=vect2d(sel._value)
				v.y=py
				If sel._pred<>f.vects._head And sel._succ<>f.vects._head Then
					Local pv:vect2d=vect2d(sel._pred._value)
					Local sv:vect2d=vect2d(sel._succ._value)
					v.x=Min(sv.x,Max(pv.x,px))
				EndIf
				v.x=Min(1,Max(0,v.x))
				v.y=Min(1,Max(0,v.y))
			EndIf
		Else
			sel=Null
		EndIf
	End Method
	' Gets all the vectors within a space
	Method selectbox:TList(minx!,miny!,maxx!,maxy!)
		Local list:TList=CreateList()
		Local link:TLink=f.vects._head._succ
		While link<>f.vects._head
			Local v:vect2d=vect2d(link._value)
			If v.x>=minx And v.y>=miny And v.x<=maxx And v.y<=maxy Then list.addlast link
			link=link._succ
		Wend
		Return list
	End Method
	' Add a new vector at the mouse location
	Method mouseaddvect(x%,y%,w%,h%)
		Local px!=Double(mx-x)/w,py!=Double(my-y)/h
		px=Min(1,Max(0,px))
		py=Min(1,Max(0,py))
		If f.vects.isempty() Then
			Local v0:TLink=f.vects.addlast(vect2d.Create(0,py))
			Local v1:TLink=f.vects.addlast(vect2d.Create(1,py))
			sellist=CreateList()
			sellist.addlast v0
			sellist.addlast v1
			sel=v0
			unselatmouseup=1
		ElseIf f.vects.count()<2 Then
			' this really shouldn't happen but ok
			Local v:vect2d=vect2d(f.vects.first())
			Local n:vect2d=vect2d.Create(0,py)
			If v.x>px Then
				v.x=1
				n.x=0
				f.vects.addfirst n
			Else
				v.x=0
				n.x=1
				f.vects.addlast n
			EndIf
		Else
			Local link:TLink=f.vects._head._succ
			While link<>f.vects._head
				Local v:vect2d=vect2d(link._value)
				If v.x>px Then
					Local n:vect2d=vect2d.Create(px,py)
					Local nlink:TLink=New TLink
					nlink._value=n
					nlink._succ=link
					nlink._pred=link._pred
					link._pred._succ=nlink
					link._pred=nlink
					sel=nlink
					Exit
				EndIf
				link=link._succ
			Wend
		EndIf
	End Method
	' Draws the outline of a rectangle
	Function DrawRectOutline(x%,y%,w%,h%,skipn%=0,skips%=0,skipe%=0,skipw%=0)
		If Not skipn DrawLine x,y,x+w-1,y
		If Not skipw DrawLine x+w-1,y,x+w-1,y+h-1
		If Not skips DrawLine x+w-1,y+h-1,x,y+h-1
		If Not skipe DrawLine x,y+h-1,x,y
	End Function
	' Checks for shift modifier
	Function shiftdown%()
		Return KeyDown(key_lshift) | KeyDown(key_rshift)
	End Function
End Type
