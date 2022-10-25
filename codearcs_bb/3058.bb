; ID: 3058
; Author: Rob the Great
; Date: 2013-06-08 19:23:53
; Title: Smooth Movement for Entities
; Description: Smoothly move an entity from point A to point B (with rotation)

;====================================================================
; Project: Smooth Movement Function
; Version: 1.0
; Author: Rob the Great
; Email: robmerrick181@gmail.com
; Copyright: No Copyright, free for public use (commercial and non-commericial)
; Description:    This is a single function that will apply smooth 3D movement
;				  on an entity from point A to point B. The function can loop
;				  on its own until the movement is complete, or it can be 
;				  iterated within an external loop. The function returns 0 if
;				  the movement is still going, or 1 if the movement is complete.
;====================================================================

;Everything in this block needs to be copied in order to use it in your project.
;BEGIN BLOCK
;===============================================================================
;I chose to use a type for ease and flow. This also allows for more than one entity to move at the same time, but looping must be disabled for that to work.

Type smoothmovement

	Field entity
	Field movevalue#
	Field amplitude#
	Field pitchamplitude#
	Field yawamplitude#
	Field rollamplitude#
	Field snapped

End Type

;This function will move an entity from point A to point B in a smooth manner.
;It will begin with a velocity of 0 and will accelerate until it is half-way
;to the end point, and then decelerate until it reaches the end point at a 
;velocity of 0. This is just like PositionEntity, only without the snapping.
;This function will also rotate that same entity from rotation A to rotation
;B in a smooth manner.
;
;This function requires the creation of two pivots, one for point A and the
;other for point B. I used pivots to cut back on the number of parameters 
;needed to make this function work. Simply create the two pivots and position
;and rotate them to represent the starting and ending points for the entity
;to be moved. Don't forget to free the pivots once you are done, because the
;function won't do this for you (unless you add it in yourself above the 
;Return 1 line in the SmoothMove() function.)
;
;The function has two modes: internal loop or external loop. The internal loop
;is more for my purposes, but I went ahead and included it in this demo. It will
;loop on its own and automatically end when it reaches its destination. This is
;especially useful if you wish to move the camera, but not so much for other 
;entities. The other mode is the external loop, which just means that you have
;to continously call this function in a loop until it returns 1. The second
;option is more likely what you want out of this function, and is thus the default.
;
;Parameters: entity
;		   			-the handle of the entity to be moved from point A to point B.
;			 startpiv
;					-the handle of a pivot created to store the starting point of
;					-the entity to be moved. Make sure it is positioned at the 
;					-starting coordinates and that it is rotated to the starting values.
;					-Note that the entity will automatically snap to the startpiv's 
;					-beginning position and rotation to ensure the math is right.
;			 endpiv
;					-the handle of a pivot created to store the ending point of the
;					-entity to be moved. Make sure that it is positioned at the
;					-ending coordinates and that it is rotated to the ending values.
;			 framecount# = 120.0
;					-the number of iterations to make until the entity has moved from
;					-point A to point B. The default is 120 frames (a floating point
;					-number is used for math reasons, but this is untested with values
;					-other than integers for arguments). If needed, set this to 0 or 1 to
;					-instantly snap the entity to the new position and rotation. This is 
;					-the same thing as calling PoisitionEntity() and RotateEntity()
;					-except that the math will be a little more intense on the 
;					-processor using this method. It's your call.
;			 loop = 0
;					-set this parameter to 0 to return after each iteration, or set it
;					-to 1 to have the function loop on its own until it is finished. The
;					-default is set to 0
;
;Returns 0 if the entity is not done moving (and loop is off), or 1 if the movement is
;complete.
;
;NOTES: This function will rotate an entity as well as position it. The rotation works for
;all Blitz rotation values, but keep in mind that if it passes 180 degrees, it will rotate
;more than once before reaching the final destination. This function will snap the entity
;to the starting rotation of the startpiv to ensure the math works as it should. Because
;of this, the entity can accidently snap into it's starting position if this is different
;from its current position. Also note that this function is only designed to handle Global
;position and rotation values, so keep than in mind when you set up your start and end pivots.

Function SmoothMove(entity,startpiv,endpiv,framecount# = 120.0,loop = 0)


	If entity = 0 Or startpiv = 0 Or endpiv = 0 ;check for valid handles
		RuntimeError "Please specificy a valid handle for entity, startpiv, and endpiv."
	EndIf
	If EntityDistance(entity,endpiv) <= 0.01 ;bail if entity is already at the end point (useful for multiple entity movements)
		If Abs(EntityPitch(entity,True) - EntityPitch(endpiv,True)) <= 0.01
			If Abs(EntityYaw(entity,True) - EntityYaw(endpiv,True)) <= 0.01
				If Abs(EntityRoll(entity,True) - EntityRoll(endpiv,True)) <= 0.01
					Return 1
				EndIf
			EndIf
		EndIf
	EndIf
	;make variables to store the entity's current rotation
	Local temppitch#
	Local tempyaw#
	Local temproll#
	
	;define a name for the smoothmovement type
	Local s.smoothmovement
	
	;make a variable to know whether or not the type has already been created (only applies if looping is enabled)
	Local typemade = 0
	
	;even though this is a loop, the function will return if looping is disabled
	Repeat
		
		;here's a panic button. remove if needed.
		If KeyDown(1) Then End
		
		If (typemade) Or (Not loop) ;if the type has already been made or looping is disabled
			For s.smoothmovement = Each smoothmovement
				If s\entity = entity ;find the type with the entity to be moved. Note that this function can handle more than one movement if looping is disabled.
					temppitch# = EntityPitch#(s\entity,True) ;store the rotation values
					tempyaw# = EntityYaw#(s\entity,True) ;store the rotation values
					temproll# = EntityRoll#(s\entity,True) ;store the rotation values
					PointEntity s\entity,endpiv ;aim the entity at the end point
					Local newvalue#
					newvalue = Sin#(s\movevalue#/framecount#) ;This only needs to be calculated once per iteration, but is used multiple times.
					;the next line is a very simplified form of a very complex equation. See the comments at the end for complete details.
					;Local radianvalue# ;OLD VALUE! See below for more info.
					;radianvalue# = (Pi/framecount#)*(s\movevalue#) ;OLD VALUE! See below for more info.
					;MoveEntity s\entity,0,0,(Sin#(RADIAN_TO_DEGREE#*(radianvalue#)))*(s\amplitude#) ;OLD VALUE! See below for more info.
					MoveEntity s\entity,0,0,newvalue#*(s\amplitude#) ;SIMPLIFIED VALUE! See below for more info.
					RotateEntity s\entity,temppitch#+newvalue#*s\pitchamplitude#,tempyaw#+newvalue#*s\yawamplitude,temproll#+newvalue#*s\rollamplitude#,True
					;turn the entity back to how it was facing and then apply a gradual turn to slowly orient toward the final rotation.
					s\movevalue# = s\movevalue# + 180.0 ;this increases by 1*180 each iteration. Think of it as counting the number of frames that have passed
														;the *180 comes from simplifying the equation above.
					If s\movevalue# >= (framecount#*180) ;If we go above the frame count (*180 from the simplified equation above), we are done!
						PositionEntity s\entity,EntityX#(endpiv,True),EntityY#(endpiv,True),EntityZ#(endpiv,True),True ;should be there, but let's be smart
						RotateEntity s\entity,EntityPitch#(endpiv,True),EntityYaw(endpiv,True),EntityRoll#(endpiv,True),True ;Same as above
						Delete s ;get ride of the smoothmovement type
						Return 1 ;and we're done!
					EndIf
					If Not loop ;if we're not looping
						Return 0 ;we're not done
					Else ;if we are looping
						Exit ;get out of the For...Next loop (no need to cycle through everything once we have our type modified)
					EndIf
				EndIf
			Next
		EndIf
		If Not typemade ;if this is the first iteration...
						;note that typemade changes to 1 after this block and will stay that way if looping is enabled. If looping is disabled,
						;the function won't make it to this point anyway because it returns before it hits this point if the type has already been made.
			PositionEntity entity,EntityX(startpiv,True),EntityY(startpiv,True),EntityZ(startpiv,True),True ;Make sure we are starting at the right spot
			RotateEntity entity,EntityPitch(startpiv,True),EntityYaw(startpiv,True),EntityRoll(startpiv,True),True ;same as above
			s.smoothmovement = New smoothmovement ;make a new smoothmovement type
			s\entity = entity ;inheret the entity
			s\movevalue# = 0.0 ;initialize the movement value (think of this as a counter for the number of iterations. It increases by 1*180 each iteration).
			;integraldistance = ((2*framecount)/Pi)*(amplitude of sin wave), yay calculus! See below for more info on how I got this equation.
			s\amplitude# = ((EntityDistance#(startpiv,endpiv))*Pi)/(2.0*framecount#) ;amplitude is solved from the integral, using entitydistance for the distance
			s\pitchamplitude# = (EntityPitch#(endpiv,True) - EntityPitch#(startpiv,True))*Pi/(2.0*framecount#) ;This tells us how fast to rotate on the x-axis
			s\yawamplitude# = (EntityYaw#(endpiv,True) - EntityYaw#(startpiv,True))*Pi/(2.0*framecount#) ;This tells us how fast to rotate on the y-axis
			s\rollamplitude# = (EntityRoll#(endpiv,True) - EntityRoll#(startpiv,True))*Pi/(2.0*framecount#) ;This tells us how fast to rotate on the z-axis
			typemade = 1 ;let the program know we have made the smoothmovement type so we don't make it again (only applies if looping is enabled)
			If Not loop ;if we are not looping
				Return 0 ;we're not done at this point
			EndIf
		EndIf
		
		UpdateWorld ;on the loop, we need these lines, feel free to modify as you wish
		RenderWorld
		Flip
		
	Forever ;Don't freak out about this line. There is a panic button (ESC) to get out if needed.


End Function
;END BLOCK
;============================================================================================================================================

;3D Example - This shows how the process works. I won't comment this part as strongly.
;Essentially, I am making two spheres and a start and end point for each sphere.
Graphics3D 1024,768,0,2

SetBuffer BackBuffer()

Global camera = CreateCamera()
MoveEntity camera,0,20,-15
RotateEntity camera,60,0,0

Local light = CreateLight(2)
PositionEntity light,80,30,-5

Local sphere = CreateSphere()
MoveEntity sphere,-15,0,0
Local front = CreateCube()
PositionEntity front,-15,0,1.5
ScaleEntity front,0.75,0.75,0.75
EntityParent front,sphere
Local pointA
Local pointB
pointA = CreateCube()
pointB = CreateCube()
PositionEntity pointA,EntityX(sphere),EntityY(sphere),EntityZ(sphere)
PositionEntity pointB,EntityX(sphere) + 30,EntityY(sphere),EntityZ(sphere)
ScaleEntity pointA,0.1,0.1,0.1
ScaleEntity pointB,0.1,0.1,0.1
RotateEntity pointA,0,0,0
RotateEntity pointB,0,90,0
EntityColor pointA,255,0,0
EntityColor pointB,0,0,255.0

Local sphere2 = CreateSphere()
MoveEntity sphere2,0,0,-15
Local front2 = CreateCube()
PositionEntity front2,0,0,1.5-15
ScaleEntity front2,0.75,0.75,0.75
EntityParent front2,sphere2
Local pointC
Local pointD
pointC = CreateCube()
pointD = CreateCube()
PositionEntity pointC,EntityX(sphere2),EntityY(sphere2),EntityZ(sphere2)
PositionEntity pointD,EntityX(sphere2),EntityY(sphere2),EntityZ(sphere2) + 30
ScaleEntity pointC,0.1,0.1,0.1
ScaleEntity pointD,0.1,0.1,0.1
RotateEntity pointC,90,180,180
RotateEntity pointD,-90,-180,-90
EntityColor pointC,0,255.0,0
EntityColor pointD,255.0,255.0,0

Local atpoint$ = "A"
Local framevalue = 120
Local option = 0
Local moved = 0
Local moved2 = 0

While Not KeyDown(1)

	If KeyHit(57) ;Spacebar
		;Option 0 (EXTERNAL LOOP, DEFAULT)
		If Not option
			
			While (moved = 0) Or (moved2 = 0)
				
				If KeyDown(1) Then End
				
				If atpoint$ = "A"
					moved = SmoothMove(sphere,pointA,pointB,framevalue,option) ;Here is the SmoothMove() Function for the first sphere, and the second is below.
					moved2 = SmoothMove(sphere2,pointC,pointD,120,option) ;I left the framevalue constant so you can see different rates of movement.
				Else
					moved = SmoothMove(sphere,pointB,pointA,framevalue,option)
					moved2 = SmoothMove(sphere2,pointD,pointC,120,option) ;same as above
				EndIf
				
				UpdateWorld
				RenderWorld
				Text 0,0,"MOVING! Note that this text will not appear if option is set to 1."
				Flip
				
			Wend
			
		;Option 1 (INTERNAL LOOP)
		Else
			If atpoint$ = "A"
				SmoothMove(sphere,pointA,pointB,framevalue,option)
				SmoothMove(sphere2,pointC,pointD,120,option)
			Else
				SmoothMove(sphere,pointB,pointA,framevalue,option)
				SmoothMove(sphere2,pointD,pointC,120,option)
			EndIf
		EndIf
		
		;Reset from above, doesn't demonstrate the concepts of this function.
		FlushKeys()
		If atpoint$ = "A"
			atpoint$ = "B"
		Else
			atpoint$ = "A"
		EndIf
		moved = 0
		moved2 = 0
	EndIf
	
	If KeyHit(28) ;Enter
		option = Not option
	EndIf
	
	If KeyDown(200) ;Up arrow
		framevalue = framevalue + 1
	EndIf
	
	If KeyDown(208) ;Down arrow
		framevalue = framevalue - 1
	EndIf
	
	UpdateWorld
	RenderWorld
	Text 0,0,"Hit the space bar to move the spheres."
	Text 0,15,"The first sphere is currently at point " + atpoint$ + "."
	Text 0,30,"It will take " + framevalue + " frames to move from one point to the next. More frames makes the effect more pronounced."
	Text 0,45,"Push the up and down arrows to increase and decrease the number of frames required for movement. More frames takes longer."
	Text 0,60,"Push Enter to change the method of the function. Currently, it is set to option " + option + "."
	Text 0,75,"Option 0 (default) will not loop on its own, while option 1 will loop on its own."
	Text 0,90,"Option 0 supports multiple entity movement at the same time, but option 1 does not."
	Flip
	
Wend

End
;The essentials to this equation:
;
;If you're wondering how I got the equation MoveEntity s\entity,0,0,(Sin#(s\movevalue#/framecount#))*(s\amplitude#), read this.
;
;I used a Sin function because it's periodic and because the values gradually go from 0 to 1 and back to 0.
;A typical Sin(x) function has a period of 2pi, but I didn't want it to go backwards, so I cut the period
;in half to make it pi. This means that I needed to limit my domain to be from 0 to pi*time. I ended up with
;a very basic function of Sin(pi*elapsedframes) for values to move the entity "forward". I needed this curve to
;stretch based on the total number of frames for the whole function, and the math makes this turn into a reciprocal.
;My function became Sin((pi/framecount)*(elapsedframe)). During testing, I saw that these values weren't giving
;me the right distances, so I needed to modify the amplitude of the sin wave in order to increase or decrease
;total distance travelled. The problem was that I had no idea what to set the amplitude to in order to get the
;exact distance. Looking at a Sin graph, I could then see that I had to use Calculus to get this answer.
;So, I let f(x) = Sin((pi/framecount)*(elapsedframe))*amplitude. EntityDistance would have to give me the
;same thing as the integral of this equation, so I went ahead and set up an integral.
;
;Let x = elapsedframes, c = framecount, amplitude = a, d = Total Distance Travelled
;Then the integral from 0 to c of ( (sin(pi*x/c)*a dx ) = d
;Let u = -cos(pi*x/c)
;du = sin(pi*x/c)*pi/c dx
;Producing (c/pi) times the integral from -1 to 1 of ( a*du ) = d
;Evaluating to 2*a*c/pi = d.
;
;From here, I solved for a (amplitude) to get
;a = d*pi/(2*c)
;Meaning
;amplitude = EntityDistance(startpiv,endpiv)*pi/(2*framecount)
;
;From here, I needed to convert from Radians to Degrees because the Blitz Sin function only handles degrees.
;This is what ended up happening:
;Local radianvalue#
;radianvalue# = (Pi/framecount#)*(s\movevalue#) ;s\movevalue# would increase by 1 each iteration, representing the passage of 1 frame
;RADIAN_TO_DEGREE# = 180/pi ;a constant to convert from radians to degrees
;MoveEntity s\entity,0,0,(Sin#(RADIAN_TO_DEGREE#*(radianvalue#)))*(s\amplitude#)
;
;This equation was so nasty that I went to clean it up a little bit. Notice that the pis cancel, making the
;equation of MoveEntity s\entity,0,0,(Sin#(180*s\movevalue#/framecount#))*(s\amplitude#).
;
;Rather than mutliply by 180 each iteration, I felt that it would be slightly better to add 180 to s\movevalue instead.
;I could then disregard the radian to degree conversion, making the final equation
;MoveEntity s\entity,0,0,(Sin#(s\movevalue#/framecount#))*(s\amplitude#)
;
;I applied the same technique to the rotation values.
;
;I hope that was more helpful that confusing. If none if it made sense, just trust that it works.
