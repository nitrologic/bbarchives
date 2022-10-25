; ID: 2283
; Author: Warpy
; Date: 2008-07-04 10:21:12
; Title: Equations of Motion - Bouncing off a Wall
; Description: Finds the new position and velocity of an object that hits a wall.

'object moving from (ox,oy) to (x,y) colliding with wall from (x1,y1) to (x2,y2)
'walls's normal is (nx,ny), direction along wall is (dx,dy)
'for the purposes of position-based dynamics, we need to change (ox,oy) so that it lies on
'the object's new line of travel.

lambda#=linesintersect(ox,oy,x,y,x1,y1,x2,y2)
If lambda#>=0
	vx#=newx#-ox
	vy#=newy#-oy
	newx#=ox+lambda*vx
	newy#=oy+lambda*vy
	side=Sgn(nx*(ox-x1)+ny*(oy-y1))
	ndp#=(nx*vx+ny*vy)*side
	fdp#=(dx*vx+dy*vy)*friction
	vx=(vx-2*nx*ndp-dx*fdp)*bounce
	vy=(vy-2*ny*ndp-dy*fdp)*bounce
	x=newx+vx*(1-lambda)
	y=newy+vy*(1-lambda)
	ox=x-vx
	oy=y-vy
EndIf



'given lines (ax,ay)->(bx,by) and (cx,cy)->(dx,dy)
'returns distance along first line where lines intersect
Function linesintersect#(ax#,ay#,bx#,by#,cx#,cy#,dx#,dy#,fit=0)
	'fit, bitmask, set:
	' 1: doesn't need to be on first segment
	' 2: doesn't need to be on second segment
	bx:-ax
	by:-ay
	dx:-cx
	dy:-cy
	
	If dx<>0
		lambda#=(cy-ay+(ax-cx)*dy/dx)/(by-bx*dy/dx)
	Else
		lambda#=(cx-ax+(ay-cy)*dx/dy)/(bx-by*dx/dy)
	EndIf
	If bx<>0
		mu#=(ay-cy+(cx-ax)*by/bx)/(dy-dx*by/bx)
	Else
		mu#=(ax-cx+(cy-ay)*bx/by)/(dx-dy*bx/by)
	EndIf
	
	Rem
	Print String(ax)+"  ,  "+String(ay)
	Print String(bx)+"  ,  "+String(by)
	Print String(cx)+"  ,  "+String(cy)
	Print String(dx)+"  ,  "+String(dy)
	Print lambda
	Print mu
	WaitKey
	EndRem
	If (lambda#>=0 And lambda<=1) Or (fit & 1)
	 If (mu#>=0 And mu<=1) Or (fit & 2) Return lambda
	EndIf
	Return -1
End Function
