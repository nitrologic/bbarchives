; ID: 2368
; Author: TWH
; Date: 2008-12-04 22:05:21
; Title: 2D Metaballs
; Description: using Hannu Kankaanpaas method

[code]
SuperStrict
Framework BRL.GlMax2D
Import brl.Random

'src idea:
'http://www.niksula.hut.fi/~hkankaan/Homepages/metaballs.html
AppTitle = "fast metaballs. "
'HideMouse()
Global resX:Int=800, resY:Int=600
SetGraphicsDriver GLMax2DDriver()
Graphics resX,resY,0,0

SeedRnd(0)'MilliSecs() 

Local numBalls:Int = 10
Local balls:tball[numBalls]
For Local i:Int=0 Until numBalls
	balls[i] = TBall.Create( TPoint.Create( Rand(50,resX-50), Rand(50,resY-50) ), Rand(1,4) )
Next

Local threshold:Float = 0.001'0.003 '0.0004 'higher value -> smaller metaballs
Local mbs:tmetaballsystem = TMetaballsystem.Create(balls, 2, threshold) 'balls, gooeyness, and potential field threshold

Local iterations:Int = 0
Local fps:Int = 0
Local lastSec:Long = MilliSecs() + 1000
Local frames:Int = 0

Global bench1:Int = 0
Local closestBall:TBall = Null

While(Not KeyHit(KEY_ESCAPE) And Not AppTerminate()	)
	Cls

	If(MouseDown(1) And closestBall <> Null)
		
		closestBall.pos.x = MouseX()
		closestBall.pos.y = MouseY() 
		
	Else
		'Find ball closest to mouse
		Local minDist# = 1e32
		For Local b:TBall = EachIn balls
			Local distSq# = (MouseX() - b.pos.x)^2 + (MouseY() - b.pos.y)^2
			If(distSq < minDist)
				closestBall = b
				minDist = distSq
			EndIf
		Next
	EndIf
	
	Local t# = MilliSecs() / 1000.0
	
	If MouseDown(2)
		balls[1].pos.x = resX/2.0 + 60 * Cos(2*t * 180/Pi)
		balls[1].pos.y = resY/2.0 + 60 * Sin(2*t * 180/Pi)
	EndIf
	If(closestBall <> Null) Then DrawLine(MouseX(), MouseY(), closestBall.pos.x, closestBall.pos.y )
	
	mbs.drawBalls(20) 'sim_step size, dt in pixels to move 
	
	SetColor 255,255,0
	DrawText("iterations: "+iterations+"    fps: "+fps, 0,0)
	'DrawText("bench1: "+(bench1/1000.0),0,15)
	bench1 = 0
	
	If(lastSec < MilliSecs() )
		lastSec = MilliSecs() + 1000
		fps = frames
		frames = 0
	EndIf
	
	iterations:+1
	frames :+ 1
	Flip
	
Wend
End

Type TPoint
	Field x:Double, y:Double
	
	Function Create:TPoint(x:Double, y:Double)
		Local tmp:TPoint = New TPoint
		tmp.x = x
		tmp.y = y
		Return tmp
	End Function
End Type

Type TBall
	
	Field pos:TPoint
	Field pos0:TPoint
	Field edgePos:tpoint
	Field size:Double
	Field tracking:Int 'Bool
		
	Function Create:TBall(pos:tpoint, size:Double)
		Local tmp:TBall = New TBall
		tmp.pos = pos
		tmp.pos0 = pos
		tmp.edgePos = pos
		tmp.size = size
		tmp.tracking = False
		Return tmp
	End Function
End Type

Type TMetaballsystem
	Const borderStepSize:Double = 0.01'0.01 'number of pixel to move in search of border offset from center
	Field balls:tball[]
	Field goo:Double
	Field threshold:Double
	Field minSize:Double
	Field oneOverGoo:Double
	Field minOverThresPowGoo:Double
	
	Function Create:TMetaballsystem(balls:TBall[], goo:Double, threshold:Double)
		Local tmp:TMetaballsystem = New TMetaballsystem
		
		tmp.balls = balls
		tmp.goo = goo
		tmp.threshold = threshold
		tmp.minSize = 1e32 'smallest ball
		tmp.oneOverGoo = 1.0/goo
		For Local b:TBall = EachIn balls
			If b.size < tmp.minSize Then tmp.minSize = b.size
		Next
		tmp.minOverThresPowGoo = (tmp.minSize/threshold)^(tmp.oneOverGoo)
		
		Return tmp
	End Function 
	
	Method calcForce:Double(pos:tpoint) 'called by stepOnceTowardsBorder
		'return the metaball fields force at point "pos"
		Local forceAtPoint:Double = 0.0
		
		For Local ball:tball = EachIn balls
			'### Formula (1)
			Local tmp:TPoint = TPoint.Create(ball.pos.x-pos.x,ball.pos.y-pos.y)
			Local div:Double= Sqr(tmp.x*tmp.x + tmp.y*tmp.y)^goo
			'Local div:Double = tmp.x^2 + tmp.y^2
			If( div<>0 ) 'prevent div by zero
				forceAtPoint :+ ball.size / div	
			Else
				forceAtPoint :+ 1e32'"big number"	
			EndIf
		Next
		Return forceAtPoint
	End Method
	
	Method calcNormal:tpoint(pos:tpoint) 'return a normalized (magnitude==1) tangent at points "pos"
		Local np:TPoint=TPoint.Create(0,0)
		
		For Local ball:tball = EachIn balls
			' ### Formula (3)
			Local fromPointToBall:TPoint = TPoint.Create(ball.pos.x-pos.x, ball.pos.y-pos.y)
			Local centerDist:Double= Sqr(fromPointToBall.x*fromPointToBall.x + fromPointToBall.y*fromPointToBall.y)
			Local rDiv:Double = 1.0 / centerDist^(2.0 + goo)
			np.x :+ -goo * ball.size * fromPointToBall.x * rDiv
			np.y :+ -goo * ball.size * fromPointToBall.y * rDiv
		Next
		Local rLen:Double = 1.0 / Sqr(np.x*np.x + np.y*np.y)
		
		Local retval:TPoint = TPoint.Create(np.x*rLen, np.y*rLen)
		'?debug
		'SetColor 0,255,0
		'DrawLine(pos.x, pos.y, pos.x+retval.x*10, pos.y+retval.y*10) 'draw normal
		'?
		
		Return retval
	End Method
	
	Method calcTangent:tpoint(pos:tpoint)
		'return a normalized (magnitude==1) tangent at points "pos"
		Local np:tpoint=Self.calcNormal(pos)
		'###Formula(7)
		Return TPoint.Create( -np.y, np.x )
	End Method
	
	Method stepOnceTowardsBorder:tpoint(pos:tpoint, forceAtPoint:Double)
		'Local timeStart:Int = MilliSecs()
		
		'step once towards the border of the metaballs field, return
		'new coordinates and force at old coordinates
		
		Local np:TPoint = calcNormal(pos)
		'### Formula(5)
		Local stepsize:Double = (minSize/threshold)^(oneOverGoo) - (minSize / forceAtPoint)^(oneOverGoo) + borderStepSize
		'Local stepsize:Double =  minOverThresPowGoo - (minSize / forceAtPoint)^(oneOverGoo) + borderStepSize
		'bench1 = bench1 + (MilliSecs() - timeStart)
		
		Return TPoint.Create( pos.x + np.x*stepsize , pos.y + np.y*stepsize )
	End Method
	
	Method trackTheBorder:tpoint(pos:tpoint)
		'track the border of the metaball field and return new coords
		Local force:Double = 1e7 '9999999.0
		'loop until force is weaker than the desired threshold
		
		'TODO: may loop forever
		Local iters:Int = 0;
		While(force > threshold)
			force = calcForce(pos)
			pos = stepOnceTowardsBorder(pos, force)
			
			?debug
			SetColor 255,255,0
			Plot pos.x, pos.y 'show a little debug output i.e. yellow pixels
			?
			
			iters :+1
			If(iters > 10) 'if it takes to long to find the border/threshold point, break
				Exit
				'Local s# = 5.0
				'DrawOval(pos.x-s,pos.y-s, s, s)
				'Flip
				'DebugStop()
			EndIf
		Wend
		Return pos
	End Method
	
	Method euler:TPoint(pos:TPoint, h:Double)
		Local t1:TPoint = calcTangent(pos)
		Return TPoint.Create( pos.x + h*t1.x, pos.y + h*t1.y )
	End Method
	
	Method rungeKutta2:TPoint(pos:TPoint, h:Double)
	 ' PYTHON: pos + h * Self.calcTangent(pos + Self.calcTangent(pos) * h / 2)
	Local t1:TPoint = calcTangent(pos)
	Local t2:TPoint = calcTangent( TPoint.Create(pos.x + t1.x * h/2, pos.y + t1.y * h/2 ) )
	Return TPoint.Create( pos.x + h*t2.x, pos.y + h*t2.y )
	End Method
	
	Method rungeKutta4:TPoint(pos:TPoint, h:Double)
    't1 = func(pos)
    't2 = func(pos + t1 * h / 2)
    't3 = func(pos + t2 * h / 2)
    't4 = func(pos + t3 * h)
    'Return pos + (h / 6) * (t1 + 2*t2 + 2*t3 + t4)
	Local t1:TPoint = calcTangent(pos)
	Local t2:TPoint = calcTangent( TPoint.Create(pos.x + t1.x * h/2, pos.y + t1.y * h/2 ) )
	Local t3:TPoint = calcTangent( TPoint.Create(pos.x + t2.x * h/2, pos.y + t2.y * h/2 ) )
	Local t4:TPoint = calcTangent( TPoint.Create(pos.x + t3.x * h, pos.y + t3.y * h ) )
	Return TPoint.Create( pos.x + (h/6)*(t1.x + 2*t2.x + 2*t3.x + t4.x), pos.y + (h/6)*(t1.y + 2*t2.y + 2*t3.y + t4.y ) )
	End Method
	
	Method drawBalls(stepping:Double)
		'First track the border for all balls and store
		'it to pos0 and edgePos. The latter will move along the border,
		'pos0 stays at the initial coordinates
		For Local b:TBall = EachIn balls
			Local borderPlusOne:TPoint = TPoint.Create(b.pos.x, b.pos.y-1) '(b.pos.x, b.pos.y-1 ) 'b.pos.x+Rnd(-.25,.25), b.pos.y+Rnd(-.25,.25) 
			b.pos0 = trackTheBorder(borderPlusOne)
			b.edgePos = b.pos0
			b.tracking = True
		Next
		
		'count how many times we have tracked a single ball. 
		'no need To do it too many times...
		Local loopIndex:Int = 0 
		
		For Local ball:tball = EachIn balls
			Local siz# = 6
			DrawOval(ball.pos.x-siz, ball.pos.y-siz,siz,siz)
		Next
			
		While(loopIndex < 200)
			loopIndex :+ 1
			
			
			For Local ball:tball = EachIn balls
				
				If(Not ball.tracking) Then Continue  'skip if tracking
				
				'store the old coordinates for drawing
				Local old_pos:TPoint = ball.edgePos
				
				'walk along the tangent, using chosen differential method
				'ball.edgePos = euler(ball.edgePos, stepping )
				ball.edgePos = rungeKutta2(ball.edgePos, stepping )
				'ball.edgePos = rungeKutta4(ball.edgePos, stepping )
				
				
				'correction step towards the border
				Local forceOnEdge:Double = calcForce(ball.edgePos)
				ball.edgePos = stepOnceTowardsBorder(ball.edgePos, forceOnEdge)
				
				'glVertex2f(old_pos.x, old_pos.y)
				'glVertex2f(ball.edgePos.x, ball.edgePos.y)
				SetColor 255,255,255
				'SetLineWidth 2
				DrawLine(old_pos.x, old_pos.y, ball.edgePos.x, ball.edgePos.y)
				'SetLineWidth 1
				'Plot (old_pos.x, old_pos.y)
				
				'check if we've gone a full circle or hit some other edge tracker
				For Local ob:TBall = EachIn balls
					Local delta:TPoint = TPoint.Create(ob.pos0.x - ball.edgePos.x, ob.pos0.y - ball.edgePos.y)
					Local distanceSq:Double = (delta.x*delta.x) + (delta.y*delta.y)
					
					If( (ob <> ball Or loopIndex > 3)  And distanceSq < (stepping*stepping) )
						ball.tracking = False
					EndIf
					
				Next 'eof check circle loop
				
				
			Next 'eof For ball loop
			
			Local tracking:Int = 0 'count how many balls are being tracked.
			For Local ball:TBall = EachIn balls
				If(ball.tracking) Then tracking :+ 1		
			Next
			
			If tracking = 0 Then Exit
			
						
		Wend 'eof while loopIndex

		
	End Method
End Type

[/code]
Edit: 15.08-2010
-Fixed a few silly mistakes.
-Added a counter that prevents trackTheBorder() for looping infinitely if the border/threshold cant be found.
