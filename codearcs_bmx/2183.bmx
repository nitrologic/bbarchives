; ID: 2183
; Author: Jesse
; Date: 2008-01-13 23:20:59
; Title: vector point to line deflection
; Description: vector line and point intersection & deflection

SuperStrict
Type point
	Field x#
	Field y#
End Type
Type tgame
	Field stageW%
	Field stageH%
	Field maxV#
	Field gravity#
	Field bounce:vector2d
	Field t#
	Field MyOb:vector2d
	Field v1:vector2d[5]
End Type
Type vector2d
	Field p0:point
	Field p1:point
	Field vx#,vy#
	Field dx#,dy#
	Field rx#,ry#
	Field lx#,ly#
	Field Length#
	Field timeFrame#
	Field lastTime#
	Global airf#
	Global b#
	Global f#
End Type 


Global game:tgame = New tgame
      
game.stageW = 320
game.stageH = 240
game.maxV = 10
game.gravity = 0.0;

' Create Object
' point p0 is its starting point in the coordinates x/y
game.myob = New vector2d
game.myOb.airf = 1.0
game.myOb.b = 1.0
game.myOb.f = 1.0
game.myOb.p0 = New point
game.myOb.p0.x = 150
game.myob.p0.y = 100
' vectors x/y components
game.myOb.vx = 1.0;
game.myOb.vy = 0.0;
game.myOb.lastTime = MilliSecs()
' Create first vector
' point p0 is its starting point in the coordinates x/y
' point p1 is its End point in the coordinates x/y
game.v1[1] = New vector2d
game.v1[2] = New vector2d
game.v1[3] = New vector2d
game.v1[4] = New vector2d
game.v1[1].p0 = New point
game.v1[1].p1 = New point
game.v1[1].p0.x = 5
game.v1[1].p0.y = 10
game.v1[1].p1.x = 250
game.v1[1].p1.y = 50
game.v1[1].b = 1.0
game.v1[1].f = 1.0
game.v1[2].p0 = New point
game.v1[2].p1 = New point 
game.v1[2].p0.x = 250
game.v1[2].p0.y = 50
game.v1[2].p1.x = 280
game.v1[2].p1.y = 130
game.v1[2].b = 1
game.v1[2].f = 1
game.v1[3].p0 = New point
game.v1[3].p1 = New point
game.v1[3].p0.x = 280
game.v1[3].p0.y = 130
game.v1[3].p1.x = 50
game.v1[3].p1.y = 160
game.v1[3].b = 1
game.v1[3].f = 1
game.v1[4].p0 = New point
game.v1[4].p1 = New point
game.v1[4].p0.x = 50
game.v1[4].p0.y = 160
game.v1[4].p1.x = 5
game.v1[4].p1.y = 10
game.v1[4].b = 1
game.v1[4].f = 1

Graphics game.stageW,game.stageH,32

Repeat

	Cls 
	runme()
	Flip()

Until KeyDown(key_escape)



Function runMe ()
	Local i%
	    
	For i = 1 To 4
	    updateVector(game.v1[i]);
    Next

	Local ob:vector2d = game.myOb;
    ' add air resistance
    ob.vx :* ob.airf
    ob.vy :* ob.airf
    ' dont let it go over Max speed
    If (ob.vx>game.maxV)
    	ob.vx = game.maxV
    ElseIf (ob.vx<-game.maxV)
        ob.vx = -game.maxV
    EndIf
    If (ob.vy>game.maxV)
    	ob.vy = game.maxV
    Else If (ob.vy<-game.maxV)
        ob.vy = -game.maxV
    EndIf
    ' update the vector parameters
    updateObject(ob)
    ' time To collide something big
    game.t = 1000000
    ' no collision yet
    game.bounce = Null
    ' find collisions with walls
    For Local i% = 1 To 4
    	Local t% = findIntersection(ob, game.v1[i]);
    	' If this has collision, save it
        If (t<game.t)
		    ' which wall To collide with
        	game.bounce = game.v1[i];
               ' save time
            game.t = t;
        EndIf
	Next
    ' we have collision
    If game.bounce 
    	' set End point To intersection point
    	ob.p1.x = ob.p0.x+ob.vx*game.t;
    	ob.p1.y = ob.p0.y+ob.vy*game.t;
    	' bounce
    	Local newv:vector2d = bounce(ob, game.bounce);
    	' change movement vector
    	ob.vx = newv.vx;
    	ob.vy = newv.vy;
    	' add New direction To End point
    	ob.p1.x :+ ob.vx*(1-game.t);
    	ob.p1.y :+ ob.vy*(1-game.t);
    	' save the time
   		game.t = 1-game.t;
    EndIf
    ' reset Object To other side If gone out of stage
    If (ob.p1.x>game.stageW)
    	ob.p1.x :- game.stageW;
    ElseIf (ob.p1.x<0)
    	ob.p1.x :+ game.stageW;
    EndIf
    If (ob.p1.y>game.stageH)
    	ob.p1.y :- game.stageH;
    ElseIf (ob.p1.y<0)
    	ob.p1.y :+ game.stageH;
    EndIf
	' draw it
	drawAll(ob);
	' make End point equal To starting point For Next cycle
	ob.p0 = ob.p1;
	' save the movement without time
	ob.vx :/ ob.timeFrame;
	ob.vy :/ ob.timeFrame;
End Function

' Function To find all parameters For the vector
Function updateVector (v:vector2d)
	' x And y components
	' End point coordinate - start point coordinate
	v.vx = v.p1.x-v.p0.x;
	v.vy = v.p1.y-v.p0.y;
	' length of vector
	v.Length = Sqr(v.vx*v.vx+v.vy*v.vy);
	' normalized unti-sized components
	If (v.Length>0) 
		v.dx = v.vx/v.Length;
		v.dy = v.vy/v.Length;
	Else
		v.dx = 0;
		v.dy = 0;
	EndIf
	' Right hand normal
	v.rx = -v.vy;
	v.ry = v.vx;
	' Left hand normal
	v.lx = v.vy;
	v.ly = -v.vx;
End Function

Function updateObject (v:vector2d)
	' find time passed from last update
    Local thisTime# = MilliSecs()
    Local time# = (thisTime-v.lastTime)/6.0
    ' we use time, Not frames To move so multiply movement vector with time passed
    v.vx :* time;
    v.vy :* time;
    ' add gravity, also based on time
    v.vy = v.vy+time*game.gravity;
    'v.p1 = {};
	v.p1 = New point
    ' find End point coordinates
    v.p1.x = v.p0.x+v.vx;
    v.p1.y = v.p0.y+v.vy;
    ' length of vector
    v.Length = Sqr(v.vx*v.vx+v.vy*v.vy);
    ' normalized unti-sized components
    v.dx = v.vx/v.Length;
    v.dy = v.vy/v.Length;
    ' Right hand normal
    v.rx = -v.vy;
    v.ry = v.vx;
    ' Left hand normal
    v.lx = v.vy;
    v.ly = -v.vx;
    ' save the current time
    v.lastTime = thisTime;
    ' save time passed
    v.timeFrame = time;
End Function

' find intersection point of 2 vectors

Function findIntersection:Int (v1:vector2d, v2:vector2d)
' vector between starting points
	Local t1#,t2#
	Local v3a:vector2d = New vector2d
	Local v3b:vector2d = New Vector2d
 	v3a.vx = v2.p0.x - v1.p0.x
	v3a.vy = v2.p0.y - v1.p0.y
    v3b.vx = v1.p0.x - v2.p0.x
	v3b.vy = v1.p0.y - v2.p0.y
    ' If they are parallel vectors, Return big number
    If ((v1.dx = v2.dx And v1.dy = v2.dy) Or (v1.dx = -v2.dx And v1.dy = -v2.dy))
    	Return 1000000;
	Else
    	t1 = perP(v3a, v2)/perP(v1, v2)
        t2 = perP(v3b, v1)/perP(v2, v1)
    EndIf
    If (t1>0 And t1<=1 And t2>0 And t2<=1)
    	Return t1;
	Else
    	Return 1000000;
    EndIf
End Function

' calculate perp product of 2 vectors
Function perP# (va:vector2d, vb:vector2d)

	Return va.vx*vb.vy-va.vy*vb.vx;
         
End Function
      
' find New vector bouncing from v2
Function bounce:vector2d (v1:vector2d, v2:vector2d)
	' projection of v1 on v2
    Local proj1:vector2d = projectVector(v1, v2.dx, v2.dy);
    ' projection of v1 on v2 normal
    Local proj2:vector2d = projectVector(v1, v2.lx/v2.Length, v2.ly/v2.Length);
    Local proj:vector2d = New vector2d
    ' reverse projection on v2 normal
    proj2.vx :* -1;
    proj2.vy :* -1;
    ' add the projections
    proj.vx = v1.f*v2.f*proj1.vx+v1.b*v2.b*proj2.vx;
    proj.vy = v1.f*v2.f*proj1.vy+v1.b*v2.b*proj2.vy;
    Return proj;
End Function

Function projectVector:vector2d (v1:vector2d, dx#, dy#)
' find dot product
	Local dp# = v1.vx*dx+v1.vy*dy;
    Local proj:vector2d = New vector2d
    ' projection components
    proj.vx = dp*dx;
    proj.vy = dp*dy;
    Return proj;
End Function

Function drawall(v:vector2d)
	Local vn:vector2d
	For Local i:Int = 1 To 4
		vn = game.v1[i]
		DrawLine vn.p0.x,vn.p0.y,vn.p1.x,vn.p1.y
	Next	
	DrawOval v.p0.x-2,v.p0.y-2,4,4
End Function
