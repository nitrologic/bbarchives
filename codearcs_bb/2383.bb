; ID: 2383
; Author: Underwood
; Date: 2008-12-25 16:44:08
; Title: 2D Ball Collisions
; Description: Smaller program for ball collisions...

'-------------------------------------------------------------------
' Initialization
'-------------------------------------------------------------------





' Initialize


	Graphics(1024,768,32)
	SeedRnd MilliSecs()
	AutoMidHandle(1)
	SetBlend(3)
	


' Types

	Type ball
		Field x#
		Field y#
		Field vx#
		Field vy#
		Field friction#
		Field mass#
		Field radius#
		Field kind%
	End Type

	Global balllist:TList = CreateList()




	Global GRAVITY# = .098






	For Local loop = 0 To 50
		CreateBall(0,Rand(0,1024),Rand(0,768),Rnd(-5,5),Rnd(-5,5),1,0.001,10)
	Next





	While Not KeyHit(key_escape)
	Cls

		UpdateBalls()

	Flip
	Wend
	End












' Functions

	Function CreateBall(kind%,x#,y#,vx#,vy#,friction#,mass#,radius#)

		Local b:ball = New ball
			b.kind     = kind
			b.x        = x
			b.y        = y
			b.vx       = vx
			b.vy       = vy
			b.friction = friction
			b.mass     = mass
			b.radius   = radius
		ListAddLast(balllist,b)

	End Function



	Function UpdateBalls()

		For Local b:ball = EachIn balllist

				b.vx = (b.vx * b.friction)							' Update velocities (with gravity and friction)
				b.vy = ((b.vy + GRAVITY) * b.friction)

					If b.kind <> 0
						b.vx = 0
						b.vy = 0
					EndIf

				b.x = (b.x + b.vx)								' Update positions
				b.y = (b.y + b.vy)

				If b.x + b.radius < 0
					b.vx = -(b.vx)
				EndIf

				If b.x > GraphicsWidth() - b.radius
					b.vx = -(b.vx)
					b.x  = GraphicsWidth() - b.radius
				EndIf

				If b.y + b.radius < 0
					b.vy = -(b.vy)
				EndIf

				If b.y > GraphicsHeight() - b.radius
					b.vy = -(b.vy)
					b.y  = GraphicsHeight() - b.radius
				EndIf

					For Local b2:ball = EachIn balllist

						Local collisiondistance# = (b.radius + b2.radius)
						Local actualdistance#    = GetDistance(b.x,b.y,b2.x,b2.y)

							If actualdistance < collisiondistance

								Local normal#    = ATan2((b2.y - b.y),(b2.x - b.x))
								Local movedist1# = ((collisiondistance - actualdistance) * (b2.mass / Float((b.mass + b2.mass))))
								Local movedist2# = ((collisiondistance - actualdistance) * (b.mass / Float((b.mass + b2.mass))))

								b.x  = (b.x + (movedist1 * Cos(normal + 180)))			' position them to 'not touching'
								b.y  = (b.y + (movedist1 * Sin(normal + 180)))
								b2.x = (b2.x + (movedist2 * Cos(normal)))
								b2.y = (b2.y + (movedist2 * Sin(normal)))

								Local nx# = Cos(normal)							' find components of normalized vector
								Local ny# = Sin(normal)

								Local a1# = ((b.vx * nx) + (b.vy * ny))				' find length using dot product
								Local a2# = ((b2.vx * nx) + (b2.vy * ny))

								Local opt# = ((2.0 * (a1 - a2)) / (b.mass + b2.mass))

									b.vx  = (b.vx - (opt * b2.mass * nx))
									b.vy  = (b.vy - (opt * b2.mass * ny))
									b2.vx = (b2.vx + (opt * b.mass * nx))
									b2.vy = (b2.vy + (opt * b.mass * ny))
							EndIf

					Next

			DrawOval(b.x - b.radius / 2,b.y - b.radius / 2,b.radius * 2,b.radius * 2)

		Next

	End Function



	Function GetDistance:Float(x1#,y1#,x2#,y2#)

		Return(Sqr(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1))))

	End Function
