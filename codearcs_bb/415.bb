; ID: 415
; Author: Phish
; Date: 2002-09-03 11:43:20
; Title: Billiards Style Collision Physics
; Description: 2D circle collision response which can be used for almost anything where 2 objects collide in 2 dimensions

;------------------------------------------
;2D COLLISION DEMO CODE
;------------------------------------------
; By Joseph 'Phish' Humfrey
; This type of collision response isn't
; just useful for pool and billiard style
; games, and in fact, I didn't write it for
; that reason at all. I wrote it because I
; needed to have collision for the space
; ships in my game 'Unity'. When their
; shields are up, they use this relatively
; simple method. It can be used for almost
; anything - player character bouncing off
; enemies in a platform game, to space
; ship collision, to a game which does
; actually involve balls. The basic
; algorithm which I used would work for
; both 2d and 3d, so if you would like to
; see it, email me at phish@aelius.com
;------------------------------------------
; The actual code which works out what
; happens after a collision is in the
; UpdateCirclePhysics() function.
;------------------------------------------
; Enjoy!
;------------------------------------------
;------------------------------------------

Graphics 800, 600, 16, 2
SetBuffer BackBuffer()
SeedRnd MilliSecs()



;------------------------------------------
; MAIN DATA TYPE
;------------------------------------------
; This exact type isn't supposed to be used
; Instead, you should use some of the fields
; in your own type, or just use this one
; for reference, to see what each field does
Type circle
	Field x#, y#		;position
	Field dx#, dy#		;x and y speeds
	
	Field radius#		;radius of circle
	Field mass#			;mass of circle
End Type
;------------------------------------------
;------------------------------------------






;------------------------------------------
; SET UP BALLS INTO A POOL STYLE ARRANGEMENT
; FOR DEMO
;------------------------------------------
.Setup
ballTriangleSize=5
For xloop = ballTriangleSize To 1 Step -1
	For yloop = 1 To xloop
		c.circle = New circle
		c\x = (5-xloop)*27 + 200
		c\y = yloop*31-(xloop*31)/2.0 + 300
		c\dx=0
		c\dy=0
		c\radius = 15
		c\mass = 50
	Next
Next

;Cue ball (smaller so you know which it is :)
cue.circle = New circle
cue\x = 800
cue\y = 300 +20
cue\dx = -20
cue\dy = Rnd(4)-2
cue\radius = 14
cue\mass = 50
;------------------------------------------
;------------------------------------------







;------------------------------------------
;MAIN LOOP
;------------------------------------------
; This is the main While..Wend game loop
While Not KeyDown(1)

	Cls
	
	UpdateCirclePhysics()
	RenderCircles()
	
	;------------
	; Reset button
	Text 10, 10, "Press a mouse button to reset."
	Text 10, 25, "Press Esc to exit."
	If GetMouse() Then
		For c.circle = Each circle
			Delete c
		Next
		Goto setup
	End If
	;------------
	
	Flip
	
Wend
;------------------------------------------
;------------------------------------------
End









;------------------------------------------
Function UpdateCirclePhysics()
;------------------------------------------
; This is the main physics function for the
; circles. It contains the very basic
; movement physics as well as the collision
; response code.
;------------------------------------------

	For c.circle = Each circle
	
		;update positions
		c\x=c\x+c\dx
		c\y=c\y+c\dy
		
		;gradually slow down
		c\dx=c\dx*0.99
		c\dy=c\dy*0.99
		
		;------------------------------------------
		;COLLISION CHECKING
		;------------------------------------------
		; Check each circle in the loop against
		; every other (c against c2)
		For c2.circle = Each circle
		
			collisionDistance# = c\radius+c2\radius
			actualDistance# = Sqr((c2\x-c\x)^2+(c2\y-c\y)^2)
			
			;Collided or not?
			If actualDistance<collisionDistance Then
				
				collNormalAngle#=ATan2(c2\y-c\y, c2\x-c\x)

				;Position exactly touching, no intersection
				moveDist1#=(collisionDistance-actualDistance)*(c2\mass/Float((c\mass+c2\mass)))
				moveDist2#=(collisionDistance-actualDistance)*(c\mass/Float((c\mass+c2\mass)))
				c\x=c\x + moveDist1*Cos(collNormalAngle+180)
				c\y=c\y + moveDist1*Sin(collNormalAngle+180)
				c2\x=c2\x + moveDist2*Cos(collNormalAngle)
				c2\y=c2\y + moveDist2*Sin(collNormalAngle)
				
				
				;------------------------------------------
				;COLLISION RESPONSE
				;------------------------------------------
				;n = vector connecting the centers of the circles.
				;we are finding the components of the normalised vector n
				nX#=Cos(collNormalAngle)
				nY#=Sin(collNormalAngle)
				
				;now find the length of the components of each movement vectors
				;along n, by using dot product.
				a1# = c\dx*nX  +  c\dy*nY
				a2# = c2\dx*nX +  c2\dy*nY
				
				;optimisedP = 2(a1 - a2)
				;             ----------
				;              m1 + m2
				optimisedP# = (2.0 * (a1-a2)) / (c\mass + c2\mass)
				
				;now find out the resultant vectors
				;r1 = c1\v - optimisedP * mass2 * n
				c\dx = c\dx - (optimisedP*c2\mass*nX)
				c\dy = c\dy - (optimisedP*c2\mass*nY)
				
				;r2 = c2\v - optimisedP * mass1 * n
				c2\dx = c2\dx + (optimisedP*c\mass*nX)
				c2\dy = c2\dy + (optimisedP*c\mass*nY)

			End If

		Next
		;------------------------------------------
		;------------------------------------------
		
		
		;Simple Bouncing off walls.
		If c\x<c\radius Then
			c\x=c\radius
			c\dx=c\dx*-0.9
		End If
		If c\x>GraphicsWidth()-c\radius Then
			c\x=GraphicsWidth()-c\radius
			c\dx=c\dx*-0.9
		End If
		If c\y<c\radius Then
			c\y=c\radius
			c\dy=c\dy*-0.9
		End If
		If c\y>GraphicsHeight()-c\radius Then
			c\y=GraphicsHeight()-c\radius
			c\dy=c\dy*-0.9
		End If
		
	Next

End Function





;------------------------------------------
Function RenderCircles()
;------------------------------------------
; Simple function draws all the circles
; on the screen.
;------------------------------------------

	For c.circle = Each circle
		If c\radius=15 Then Color 200, 50, 50 Else Color 255, 255, 255
		Oval c\x-c\radius, c\y-c\radius, c\radius*2, c\radius*2
	Next
	
End Function
;------------------------------------------
;------------------------------------------
