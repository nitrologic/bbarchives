; ID: 2184
; Author: Jesse
; Date: 2008-01-14 05:18:40
; Title: vectors part 2
; Description: Ball to line collition and deflection

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
	Global walllist:TList
	Method Create(W%,H%,V#,G#)
    	walllist = CreateList()
		stageW = W
    	stageH = H
    	maxV = V
    	gravity = G
		myOb = New vector2d
	End Method
	Method createWall(x1#,y1#,x2#,y2#,b#,f#)
      ' Create vector
      ' point p0 is its starting point in the coordinates x/y
      ' point p1 is its End point in the coordinates x/y
		Local v:vector2d = New vector2d
		walllist.addlast(v)
		v.p0 = New point
		v.p0.x = x1
		v.p0.y = y1
		v.p1 =New point
		v.p1.x = x2
		v.p1.y = y2
		v.b = b
		v.f = f
		v.updateVector()
	End Method
      Method animate() 
         Local i%,w:vector2d,vb:vector2d,pen# 
		 Local MyOb:vector2d = game.myOb;
         ' add air resistance
         MyOb.vx :* MyOb.airf;
         MyOb.vy :* MyOb.airf;
         ' dont let it go over Max speed
         If (Myob.vx>game.maxV)
            MyOb.vx = game.maxV;
         Else If (MyOb.vx<-game.maxV)
            MyOb.vx = -game.maxV;
         EndIf
         If (MyOb.vy>game.maxV)
            MyOb.vy = game.maxV;
         Else If (MyOb.vy<-game.maxV)
            MyOb.vy = -game.maxV;
         EndIf
         ' update the vector parameters
         myob.updateObject();
         ' check the walls For collisions
         For w = EachIn game.Walllist
            ' If we have hit the wall
            pen# = MyOb.r-myOb.findIntersection(w);
            If pen>=0 
               ' move MyObject away from the wall
               MyOb.p1.x :+ w.lx*pen;
               MyOb.p1.y :+ w.ly*pen;
               ' change movement
               vb = myob.bounce(w);
               MyOb.vx = vb.vx;
               MyOb.vy = vb.vy;
            EndIf
         Next
         ' reset MyObject To other side If gone out of stage
         If (MyOb.p1.x>game.stageW+MyOb.r)
            MyOb.p1.x = -Myob.r;
         ElseIf (Myob.p1.x<-Myob.r)
            MyOb.p1.x = game.stageW+Myob.r;
         EndIf
         If (Myob.p1.y>game.stageH+Myob.r)
            MyOb.p1.y = -Myob.r;
         ElseIf (Myob.p1.y<-Myob.r)
            MyOb.p1.y = game.stageH+Myob.r;
         EndIf
         ' draw it
         drawAll(Myob);
         ' make End point equal To starting point For Next cycle
         MyOb.p0 = MyOb.p1;
         ' save the movement without time
         MyOb.vx :/ MyOb.timeFrame;
         MyOb.vy :/ MyOb.timeFrame;
      End Method
			
	  Method drawall(v1:vector2d)
		For Local v:vector2d = EachIn game.Walllist
			DrawLine v.p0.x,v.p0.y,v.p1.x,v.p1.y
		Next
		DrawOval v1.p0.x-v1.r,v1.p0.y-v1.r,..
		         v1.r*2,v1.r*2
	  End Method
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
	Field r#
	Global airf#
	Global b#
	Global f#
    
	Method updateObject()
         Local ThisTime#,time#
		 ' find time passed from last update
         thisTime = MilliSecs();
         time = (thisTime - lastTime) /100
         ' we use time, Not frames To move so multiply movement vector with time passed
         vx :* time;
         vy :* time;
         ' add gravity, also based on time
         vy = vy+time*game.gravity;
         p1 = New point;
         ' find End point coordinates
         p1.x = p0.x+vx;
         p1.y = p0.y+vy;
         ' length of vector
         Length = Sqr(vx*vx+vy*vy);
         ' normalized unti-sized components
         dx = vx/Length;
         dy = vy/Length;
         ' Right hand normal
         rx = -vy;
         ry = vx;
         ' Left hand normal
         lx = vy;
         ly = -vx;
         ' save the current time
         lastTime = thisTime;
         ' save time passed
         timeFrame = time;
      End Method
	
      Method updateVector:vector2d()
         ' x And y components
         ' End point coordinate - start point coordinate
         vx = p1.x-p0.x
         vy = p1.y-p0.y
         ' length of vector
         Length = Sqr(vx*vx+vy*vy);
         ' normalized unti-sized components
         If (Length>0)
            dx = vx/Length;
            dy = vy/Length;
         Else
            dx = 0;
            dy = 0;
         EndIf
         ' Right hand normal
         rx = -dy;
         ry = dx;
         ' Left hand normal
         lx = dy;
         ly = -dx;
         Return Self
      End Method
      ' find New vector bouncing from v2
      Method bounce:vector2d(v2:vector2d)
         ' projection of v1 on v2
         Local proj1:vector2d = projectVector(Self, v2.dx, v2.dy);
         ' projection of v1 on v2 normal
         Local proj2:vector2d = projectVector(Self, v2.lx, v2.ly);
         Local proj:vector2d = New vector2d
         ' reverse projection on v2 normal
         proj2.Length = Sqr(proj2.vx*proj2.vx+proj2.vy*proj2.vy);
         proj2.vx = v2.lx*proj2.Length
         proj2.vy = v2.ly*proj2.Length
         ' add the projections
         proj.vx = f*v2.f*proj1.vx+b*v2.b*proj2.vx
         proj.vy = f*v2.f*proj1.vy+b*v2.b*proj2.vy
         Return proj
      End Method
      ' project vector v1 on unit-sized vector dx/dy
      Method projectVector:vector2d (v1:vector2d, dx#, dy#)
         ' find dot product
         Local dp# = v1.vx*dx+v1.vy*dy;
         Local proj:vector2d = New vector2d
         ' projection components
         proj.vx = dp*dx;
         proj.vy = dp*dy;
         Return proj;
      End Method
      ' find intersection point of 2 vectors
      Method findIntersection# (v2:vector2d)
         ' vector between center of ball And starting point of wall
         Local v3:vector2d = New vector2d
		 
         v3.vx = p1.x-v2.p0.x;
         v3.vy = p1.y-v2.p0.y;
         ' project this vector on the normal of the wall
         Local v:vector2d = projectVector(v3, v2.lx, v2.ly);
         ' find length of projection
         v.Length = Sqr(v.vx*v.vx+v.vy*v.vy);
         Return v.Length;
      End Method
End Type

Global game:tgame = New tgame
' Create gameMyObject
' CreateMyObject
' point p0 is its starting point in the coordinates x/y
game.Create(640,480,20,0.6)
      game.myOb.airf = 1.0
	  game.myOb.b = 0.5
	  game.myOb.f = 0.9
	  game.myOb.r = 20
	  game.MyOb.lastTime = MilliSecs()
      game.myOb.p0 = New point
	  game.myOb.p0.x = 350
	  game.myOb.p0.y = 120
      ' vectors x/y components
      game.myOb.vx = 0;
      game.myOb.vy = -25;
      Local v:vector2d = New vector2d
	  game.createWall(600,50,50,50,1,1)
	  game.createWall(250,450,600,50,1,1)
	  game.createWall(50,450,250,450,1,1)
	  game.createWall(50,50,50,450,1,1)


Graphics game.stageW,game.stageH
Repeat
	Cls
	game.animate()
	game.drawall(game.myob)
	Flip()
Until KeyDown(key_escape)
