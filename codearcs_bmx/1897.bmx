; ID: 1897
; Author: ninjarat
; Date: 2007-01-07 03:55:12
; Title: TRadialVector, TLinearVector, TPos
; Description: Some OO classes to help you do vector maths

'base class
Type TPos 'would have extended it but it works better the way I did it
	Field x!,y!
	
	Method setTo:TPos(ex!,wy!)
		x=ex
		y=wy
	End Method
	
	Function createPos:TPos(ex!,wy!)
		Local temp:TPos
		temp.x=ex
		temp.y=wy
		Return temp
	End Function
End Type

'children (sort of)
Type TLinearVector 'less accurate but faster
	Field cur:TPos,nxt:TPos
	
	Method setTo(cr:TPos,nx:TPos)
		nxt=nx
		cur=cr
	End Method
	
	Method setToZero()
		nxt=cur
	End Method
	
	Method increment(step!=1)
		xdif!=nxt.x-cur.x
		ydif!=nxt.y-cur.y
		cur=nxt
		nxt.x:+xdif*step
		nxt.y:+ydif*step
	End Method
	
	Method toRadialVector:TRadialVector()
		Local temp:TRadialVector
		temp.pos=cur
		xdif!=nxt.x-cur.x
		ydif!=nxt.y-cur.y
		temp.vel=Sqr(xdif*xdif+ydif*ydif)
		If xdif>0 Then
			temp.ang=ATan(yvel/xvel)
		Else
			temp.ang=ATan(yvel/xvel)+180
		End If
	End Method
	
	Function createVector:TLinearVector(cr:TPos,nx:TPos)
		Local temp:TLinearVector
		temp.nxt=nx
		temp.cur=cr
		Return temp
	End Function
	
	Function createVectorFromCoords:TLinearVector(cx!,cy!,nx!,ny!)
		Local temp:TLinearVector
		temp.nxt=TPos.createPos(nx,ny)
		temp.cur=TPos.createPos(cx,cy)
		Return temp
	End Function
End Type

Type TRadialVector 'more accurate but slower
	Field pos:TPos,ang!,vel!
	
	Method setTo(p:TPos,a!,v!)
		pos=p
		ang=a
		vel=v
	End Method
	
	Method setToZero()
		vel=0
	End Method
	
	Method increment(step!=1)
		xvel!=Cos(ang)*vel*step
		yvel!=Sin(ang)*vel*step
		pos.x:+xvel
		pos.y:+yvel
	End Method
	
	Method toLinearVector:TLinearVector()
		Local temp:TLinearVector
		temp.cur=pos
		temp.nxt.x=pos.x+Cos(ang)*vel
		temp.nxt.y=pos.y+Sin(ang)*vel
		Return temp
	End Method
	
	Function createVector:TRadialVector(p:TPos,a!,v!)
		Local temp:TRadialVector
		temp.pos=p
		temp.ang=a
		temp.vel=v
		Return temp
	End Function
	
	Function createVectorFromCoords:TRadialVector(px!,py!,a!,v!)
		Local temp:TRadialVector
		temp.pos=TPos.createPos(px,py)
		temp.ang=a
		temp.vel=v
		Return temp
	End Function
End Type
