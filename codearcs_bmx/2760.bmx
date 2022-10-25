; ID: 2760
; Author: TomToad
; Date: 2010-09-01 12:29:07
; Title: Worm Screen Saver
; Description: A bunch of worms moving on the screen controled by vectors

SuperStrict
SeedRnd(MilliSecs())'randomize the seed

Global Width:Int = DesktopWidth() 'get the desktop resolution
Global Height:Int = DesktopHeight()
Global Depth:Int = 32
While Not GraphicsModeExists(Width,Height,Depth) 'find the highest bit depth we can use
	Depth :- 8
	If Depth = 0 Then RuntimeError("Cannot find depth for "+Width+"x"+Height)
Wend

Type TVector 'Just a vector :)
	Field x:Double
	Field y:Double
	
	Function Zero:TVector() 'creates a 0 vector
		Local Vector:TVector = New TVector
		Vector.x = 0
		Vector.y = 0
		Return Vector
	End Function
End Type

' A TSingularity is just an entity in space.  The worms direction is affected by the "pull" of each
' singularity it is close to.
Type TSingularity
	Global List:TList = CreateList() 'This holds a list of all the singularities in the world
	
	Field x:Double 'The singularity's position
	Field y:Double
	
	'This function takes a location and returns a vector depending on the direction and the nearness
	'relative to the singularity.  the closer to the singularity the point is, the greater the magnitude
	'Any point which is more than 100 pixels away and the method returns a zero vector
	Method CreateVector:TVector(x:Int,y:Int)
		Local dx:Double = Self.x - x 'calculate the offset from the point to the singularity
		Local dy:Double = Self.y - y
		Local Distance:Double = Sqr(dx*dx+Dy*dy) 'calculate the distance
		If Distance > 100 Then Return TVector.Zero() 'if the distance is more than 100, return 0 vector
		
		Local Magnitude:Double = (100.0-Distance)*.006 'magnitude is inversely proportional to the distance
		If Magnitude < 0 Then Return TVector.Zero() 'shouldn't happen, but might due to rounding if distance is very near 100
		Local Direction:Double = ATan2(dy,dx) 'Get the direction to the singularity
		
		Local Vector:TVector = New TVector
		Vector.x = Cos(direction)*Magnitude 'create a vector at the proper direction and distance
		vector.y = Sin(Direction)*Magnitude
		Return Vector 'return the vector
	End Method
End Type

For Local i:Int = 1 To 100 'We will create 100 singularities
	Local Singularity:TSingularity = New TSingularity
	Singularity.X = Rnd(0,Width) 'Place the singularity randomly within our space
	Singularity.y = Rnd(0,Height)
	
	TSingularity.List.AddLast(Singularity)
Next

'Each particle represents the "head" of our worm.  The tail is created by the trail.  
Type TParticle
	Field Trail:TList = CreateList() 'A list used for the "Tail"
	Field TrailCount:Int = 0
	Field x:Double 'the position of the head particle
	Field y:Double
	Field momentum:TVector 'the movement vector last frame
	
	Method New() 'Creates the Momentum vector and sets it to zero
		Momentum = TVector.Zero()
	End Method
	
	'this updates the position of the worm from the combined vectors of all the singularites and the momentum
	'of the worm.  It also saves this movement for use in the next frame
	Method Update(Vector:TVector)
		'if the particle is moving very, very, very slowly, then it might be too far from any singularities.
		'We need to give it a little nudge.
		
		If Abs(Vector.x) < .00001 And Abs(Vector.y) < .00001
			Vector.x = Sgn(Width/2-x)*.06
			Vector.y = Sgn(Height/2-y)*.06
		End If
		Local TrailVector:TVector = New TVector 'We need to save the current position in the trail to create it's "tail"
		TrailVector.X = x
		TrailVector.Y = y
		Trail.AddLast(TrailVector)
		If TrailCount >= 20 'The tail will only be 20 units long
			Trail.RemoveFirst()
		Else
			TrailCount :+ 1
		End If
		Momentum.x = Vector.x 'Save the current vector for momentum
		Momentum.y = Vector.y
		x :+ Vector.x 'update the head's position
		y :+ Vector.y
		'if our worm leaves the world, we will reposition it randomly
		If x < -100 Or x > Width + 100 Or y < -100 Or y > Height + 100
			x = Rand(0,Width)
			y = Rand(0,Height)
			Momentum.X = 0
			Momentum.Y = 0
			Trail.Clear()
			TrailCount = 0
		End If
	End Method
End Type

Local List:TList = CreateList() 'this holds a list of all the particles in the world
For Local i:Int = 1 To 30 '30 particles
	Local Particle:TParticle = New TParticle
	Particle.x = Rnd(0,Width) 'Pick a random position
	Particle.y = Rnd(0,Height)
	List.AddLast(Particle)
Next

Graphics Width,Height,Depth 'Set the screen rez
Local PVector:TVector = New TVector 'this will hold the combined vectors for our particle
Local NextTime:Int = MilliSecs() + 16 'Implementing a fixed logic scheme.  this is roughly 60 frames a second
Local SingularityFlag:Int = False 'true to drawsingularities

While Not KeyHit(KEY_ESCAPE)
	Local time:Int = MilliSecs() 'current time
	'The logic loop.
	While Time >= NextTime
		For Local Particle:TParticle = EachIn List 'go through all the particles
			PVector.x = 0 'Set the PVector to 0.  I could just create a new 0 Vector, but I think that
			PVector.y = 0 'just setting this to 0 is quicker
			
			For Local Singularity:TSingularity = EachIn TSingularity.List 'Now we need to go through each singularity
				Local Vector:Tvector = Singularity.CreateVector(Particle.x,Particle.y) 'Get the vector of this singularity
				PVector.x :+ Vector.X 'Add the returned vector to the current particle vector
				PVector.Y :+ Vector.Y
			Next
			PVector.X :+ particle.Momentum.X 'Now we add the momentum of the particle
			PVector.y :+ Particle.Momentum.Y
			Particle.Update(PVector) 'update the particle
		Next
		NextTime :+ 16 'Next logic frame
	Wend
	If KeyHit(KEY_S) Then SingularityFlag = Not SingularityFlag 'toggle whether singularities are drawn or not
	Cls
	If SingularityFlag 'If true, we draw the singularities
		SetColor 255,255,0
		For Local Singularity:TSingularity = EachIn TSingularity.List
			DrawOval Singularity.x-2,Singularity.y-2,4,4
		Next
	
	
		SetColor 255,255,255
	End If
	For Local Particle:TParticle = EachIn List 'go through each particle and draw it's "head" and "Tail"
		Local ox:Double
		Local oy:Double
		Local Define:Int = False
		For Local Trail:TVector = EachIn Particle.Trail
			If Not Define
				Define = True
				ox = Trail.x
				oy = trail.y
				Continue
			End If
			DrawLine ox,oy,Trail.x,Trail.y
			ox = Trail.x
			oy = Trail.y
		Next
		DrawOval Particle.x-2,Particle.y-2,4,4
	Next
	Flip
Wend
