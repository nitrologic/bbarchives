; ID: 2185
; Author: Jesse
; Date: 2008-01-14 17:17:04
; Title: vectors part 3
; Description: ball to vector collition

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
		v.updateVector(True)
	End Method
      Method animate ()
         ' start To calculate movement
         
         ' add air resistance
         MyOb.vx :* MyOb.airf;
         MyOb.vy :* MyOb.airf;
         ' dont let it go over Max speed
         If (MyOb.vx>game.maxV)
            MyOb.vx = game.maxV;
         ElseIf (MyOb.vx<-game.maxV)
            MyOb.vx = -game.maxV;
         EndIf
         If (MyOb.vy>game.maxV) 
            MyOb.vy = game.maxV;
         Else If (MyOb.vy<-game.maxV)
            MyOb.vy = -game.maxV;
         EndIf
         ' update the vector parameters
         MyOb.updateObject();
		 ' check the walls For collisions
         For Local w:vector2d = EachIn game.walllist
            Local v:vector2d = MyOb.findIntersection(w);
 
			v = v.updateVector(False);
            
			Local pen# = MyOb.r-v.Length;
            ' If we have hit the wall
            If (pen>=0)
               ' move Object away from the wall
               MyOb.p1.x :+ v.dx*pen;
               MyOb.p1.y :+ v.dy*pen;
               ' change movement, bounce off from the normal of v
               Local vbounce:vector2d = New vector2d
			   vbounce.dx = v.lx
			   vbounce.dy = v.ly
			   vbounce.lx = v.dx
			   vbounce.ly = v.dy
			   vbounce.b = 1
			   vbounce.f = 1
               Local vb:vector2d = MyOb.bounced(vbounce);
               MyOb.vx = vb.vx;
               MyOb.vy = vb.vy;
            EndIf
         Next
         ' reset Object To other side If gone out of stage
         If (MyOb.p1.x>game.stageW+MyOb.r)
            MyOb.p1.x = -MyOb.r;
         Else If (MyOb.p1.x<-MyOb.r)
            MyOb.p1.x = game.stageW+MyOb.r
         EndIf
         If (MyOb.p1.y>game.stageH+MyOb.r)
            MyOb.p1.y = -MyOb.r;
         Else If (MyOb.p1.y<-MyOb.r)
            MyOb.p1.y = game.stageH+MyOb.r;
         EndIf
         ' draw it
         'drawAll();
         ' make End point equal To starting point For Next cycle
         MyOb.p0 = MyOb.p1;
         ' save the movement without time
         MyOb.vx :/ MyOb.timeFrame;
         MyOb.vy :/ MyOb.timeFrame;
      EndMethod
	  Method drawall()
		Local i%
		For Local v:vector2d = EachIn game.walllist
			DrawLine v.p0.x,v.p0.y,v.p1.x,v.p1.y
		Next
		
		DrawOval myOb.p0.x - myOb.r, myOb.p0.y - myOb.r,..
		         MyOb.r * 2,myOb.r * 2
	  End Method      ' main Function
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
    Method updateVector:vector2d (frompoints:Int)
         ' x And y components
         If (frompoints)
            vx = p1.x-p0.x;
            vy = p1.y-p0.y;
         Else
			p0 = New point
			p1 = New point	
            p1.x = p0.x+vx;
            p1.y = p0.y+vy;
         EndIf
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
      Method updateObject ()
         ' find time passed from last update
         Local thisTime# = MilliSecs();
         Local time# = (thisTime-lastTime)/100;
         ' we use time, Not frames To move so multiply movement vector with time passed
         vx :* time;
         vy :* time;
         ' add gravity, also based on time
         vy = vy+time*game.gravity;
         p1 = New point
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
      Method findIntersection:vector2d (v2:vector2d)
         ' vector between center of ball And starting point of wall
         Local v3:vector2d = New vector2d
         Local v:vector2d
		 v3.vx = p1.x-v2.p0.x;
         v3.vy = p1.y-v2.p0.y;
         ' check If we have hit starting point
         Local dp# = v3.vx*v2.dx+v3.vy*v2.dy;
         If (dp<0)
            ' hits starting point
            v = v3;
         Else 
            Local v4:vector2d = New vector2d
            v4.vx = p1.x-v2.p1.x;
            v4.vy = p1.y-v2.p1.y;
            ' check If we have hit side Or endpoint
            dp = v4.vx*v2.dx+v4.vy*v2.dy;
            If (dp>0) 
               ' hits ending point
               v = v4;
            Else
               ' it hits the wall
               ' project this vector on the normal of the wall
               v = v3.projectVector(v2.lx, v2.ly);
            EndIf
         EndIf
         Return v;
      EndMethod
      Method bounced:vector2d (v2:vector2d)
         ' projection of v1 on v2
         Local proj1:vector2d = projectVector(v2.dx, v2.dy);
         ' projection of v1 on v2 normal
         Local proj2:vector2d = projectVector(v2.lx, v2.ly);
         Local proj:vector2d = New vector2d
         ' reverse projection on v2 normal
         proj2.Length = Sqr(proj2.vx*proj2.vx+proj2.vy*proj2.vy);
         proj2.vx = v2.lx*proj2.Length;
         proj2.vy = v2.ly*proj2.Length;
         ' add the projections
         proj.vx = f*v2.f*proj1.vx+b*v2.b*proj2.vx;
         proj.vy = f*v2.f*proj1.vy+b*v2.b*proj2.vy;
         Return proj;
      End Method
      ' project vector v1 on unit-sized vector dx/dy
      Method projectVector:vector2d (dx#, dy#)
         ' find dot product
         Local dp# = vx*dx+vy*dy;
         Local proj:vector2d = New vector2d
         ' projection components
         proj.vx = dp*dx;
         proj.vy = dp*dy;
         Return proj;
      End Method

End Type

Global game:tgame = New tgame
game.Create(640,480,10,0.5)
game.myOb.vx = 5;
game.myOb.vy = -5;
game.myOb.airf = .99
game.myOb.b = 1
game.myOb.f = 1
game.myOb.r = 20
game.MyOb.lastTime = MilliSecs()
game.myOb.p0 = New point
game.myOb.p0.x = 200
game.myOb.p0.y = 30
' vectors x/y components
game.myOb.vx = 5
game.myOb.vy = -5
Local v:vector2d = New vector2d
game.createWall(250,110,50,100,1,1)
game.createwall(250,150,250,110,1,1)
game.createWall(50,150,250,150,1,1)
game.createWall(50,100,50,150, 1,1)
game.createWall(250,300,400,280,1,1)
game.createWall(250,300,250,350,1,1)
game.createWall(400,280,400,330,1,1)
game.createWall(250,350,400,330,1,1)

Graphics game.stageW,game.stageH
Repeat
	Cls
	game.animate()
	game.drawall()
	Flip()
Until KeyDown(key_escape)
