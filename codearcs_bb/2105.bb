; ID: 2105
; Author: Stevie G
; Date: 2007-09-12 19:36:18
; Title: Verlet Ragdoll
; Description: As above

Graphics3D 640,480,16,1

Global CAMERA, LIGHT , LIMB , JOINT, PLANE , CURSOR, ARROW
Global PICKED.PointMass
Global TEST.body
Const ITERATIONS = 5
Const TIMESTEP# = .0025
Const PUSH_PULL = 1
Const PUSH = 2
Const PULL = 3
Const C_PointMass = 1
Const C_Ground = 2
Collisions C_PointMass, C_Ground, 2, 3

Type Vector
	Field x#
	Field y#
End Type

Type PointMass
	Field Pivot
	Field Old.Vector
	Field Acceleration.Vector
	Field Offset.Vector
	Field Mass#
	Field InvMass#
	Field Pick
End Type

Type Constraint
	Field Flag
	Field Entity
	Field Radius#
	Field p1.PointMass
	Field p2.PointMass
	Field MaxLength#
	Field MinLength#
End Type

Type Body
	Field PointMasses
	Field p.Pointmass[ 50 ]
End Type

Type Pose
	Field Position.vector[15]
End Type

Type Animation
	Field Frames
	Field Keyframe.Pose[ 10 ]
End Type

GAMEinit()

While Not KeyDown(1)

	GAMEupdate()
	UpdateWorld()
	RenderWorld()
	Flip
	
Wend

;=====================================================
;=====================================================
;=====================================================

Function GAMEinit()

	LIMB = CreateSphere()
	PositionMesh LIMB,0,0,1
	ScaleMesh LIMB , .5, .5, .5
	EntityColor LIMB,200,100,100
	UpdateNormals LIMB
	EntityFX LIMB, 4
	EntityAlpha LIMB,.25
	HideEntity LIMB
	
	P = CreateCube() 
	ScaleMesh P, 70,70,70
	EntityColor P,50,75,150
	FlipMesh P
	UpdateNormals P
	EntityType P, C_Ground
		
	JOINT = CreateSphere()
	EntityColor JOINT, 150,100,50
	HideEntity JOINT
	
	LIGHT = CreateLight()
	
	CAMERA = CreateCamera()
	PositionEntity CAMERA , 0 , 0, -100
	CameraFogMode CAMERA, 1
	CameraFogRange CAMERA, 80,120
	CameraFogColor CAMERA,0,0,0

	TEST = BODYcreate()
	BODYreset( TEST , 0,20)

	CURSORinit()
	
End Function

;=====================================================
;=====================================================
;=====================================================

Function GAMEupdate()

	CURSORupdate()
	POINTMASSupdate()
	CONSTRAINTupdate()
	BODYupdate()

End Function

;=====================================================
;=====================================================
;=====================================================

Function CURSORinit()

	PLANE = CreatePlane()
	RotateEntity PLANE , -90,0,0
	EntityAlpha PLANE, 0
	EntityPickMode PLANE, 2
	
	CURSOR = CreateSphere()
	EntityColor CURSOR,0,255,255
	
	ARROW = CopyEntity( LIMB , CURSOR )
	EntityColor ARROW , 0,255,255

End Function

;=====================================================
;=====================================================
;=====================================================

Function CURSORupdate()

	ML = MouseDown(1)

	CameraPick ( CAMERA , MouseX(), MouseY() )
	PositionEntity CURSOR , PickedX(), PickedY(), PickedZ() 
	
	Smallest# = 10000
	PICKED = Null
	For p.pointmass = Each pointmass
		If p\Pick
			EntityColor p\Pivot , 50,100,50
			Distance# = EntityDistance( p\Pivot , CURSOR )
			If Distance# < Smallest
				Smallest = Distance
				PICKED = p
			EndIf
		EndIf
	Next
	
	If PICKED <> Null
		EntityColor PICKED\Pivot , 0,255,255
		PointEntity ARROW, PICKED\pivot
		ScaleEntity ARROW , 1 , 1, EntityDistance( ARROW , PICKED\pivot )
		If ML
			dx# = ( EntityX( CURSOR ) - EntityX( PICKED\pivot ) ) *.1
			dy# = ( EntityY( CURSOR ) - EntityY( PICKED\pivot ) ) *.1
			TranslateEntity PICKED\pivot , dx, dy , 0
		EndIf	
	EndIf

End Function

;=====================================================
;=====================================================
;=====================================================

Function BODYupdate()

	;if wounded decrease the head/neck between 5-20
	
	For b.body = Each body
		;head/ neck
		VECTORset( b\p[1]\Acceleration , 0, 20 )
		VECTORset( b\p[2]\Acceleration , 0, 20 )
		;wrists
		VECTORset( b\p[4]\Acceleration , -5, -5 )
		VECTORset( b\p[7]\Acceleration , 5, -5 )
		;heels
		VECTORset( b\p[10]\Acceleration , -15, -30 )
		VECTORset( b\p[13]\Acceleration , 15, -30 )
		;hands
		VECTORset( b\p[5]\Acceleration , 0, -1 )
		VECTORset( b\p[8]\Acceleration , 0, -1 )
		;elbows
		VECTORset( b\p[3]\Acceleration , -1, 0 )
		VECTORset( b\p[6]\Acceleration , 1, 0 )
		;knees
		VECTORset( b\p[9]\Acceleration , -1, 0 )
		VECTORset( b\p[12]\Acceleration , 1, 0 )
		;feet
		VECTORset( b\p[11]\Acceleration , -1, -1 )
		VECTORset( b\p[14]\Acceleration , 1, -1 )


;		POINTMASSapplyforce( b\p[1] , 0,20 )
;		POINTMASSapplyforce( b\p[2] , 0,20 )
;		;wrists
;		POINTMASSapplyforce( b\p[4] , -5,-10 )
;		POINTMASSapplyforce( b\p[7] , 5,-10 )
;		;neels
;		POINTMASSapplyforce( b\p[10] , -15,-30 )
;		POINTMASSapplyforce( b\p[13] , 15,-30 )
;		;hands	
;		POINTMASSapplyforce( b\p[5] , 0,-1 )
;		POINTMASSapplyforce( b\p[8] , 0,-1 )
;		;elbows
;		POINTMASSapplyforce( b\p[3] , -1,0 )
;		POINTMASSapplyforce( b\p[6] , 1,0 )
;		;knees
;		POINTMASSapplyforce( b\p[9] , -1,0 )
;		POINTMASSapplyforce( b\p[12] , 1,0 )
;		;feet
;		POINTMASSapplyforce( b\p[11] , -1,-1 )
;		POINTMASSapplyforce( b\p[14] , 1,-1 )
	Next

End Function

;=====================================================
;=====================================================
;=====================================================

Function BODYcreate.body()

	b.body = New body
		
	b\p[ 0 ] = POINTMASScreate( b,0,0 )					;hip
	b\p[ 1 ] = POINTMASScreate( b,0,7.5 )				;neck
	b\p[ 2 ] = POINTMASScreate( b,0,12.0 , True )			;head
	b\p[ 3 ] = POINTMASScreate( b,-3,5 )				;l elbow
	b\p[ 4 ] = POINTMASScreate( b,-4,0 )				;l wrist
	b\p[ 5 ] = POINTMASScreate( b,-4 ,-2.5, True )			;l hand
	b\p[ 6 ] = POINTMASScreate( b,3,5 )					;r elbow
	b\p[ 7 ] = POINTMASScreate( b,4,0 )					;r wrist
	b\p[ 8 ] = POINTMASScreate( b,4 ,-2.5 , True )			;r hand
	b\p[ 9 ] = POINTMASScreate( b,-3,-6 )				;l knee
	b\p[ 10 ] = POINTMASScreate( b,-4,-12 , True)		;l ankle
	b\p[ 11 ] = POINTMASScreate( b,-6,-12 )				;l foot
	b\p[ 12 ] = POINTMASScreate( b,3,-6 )				;r knee
	b\p[ 13 ] = POINTMASScreate( b,4,-12, True )			;r ankle
	b\p[ 14 ] = POINTMASScreate( b,6,-12 )				;r foot
	
	BODYreset( b )
		
	CONSTRAINTcreate( b\p[ 0 ] , b\p[ 1 ] , 2 )			;torsoe
	CONSTRAINTcreate( b\p[ 1 ] , b\p[ 2 ] , 2)				;head
	CONSTRAINTcreate( b\p[ 1 ] , b\p[ 3 ] , 2)				;l upper arm
	CONSTRAINTcreate( b\p[ 3 ] , b\p[ 4 ] , 1.5)			;l forearm
	CONSTRAINTcreate( b\p[ 4 ] , b\p[ 5 ] , 2)				;l hand
	CONSTRAINTcreate( b\p[ 1 ] , b\p[ 6 ] , 2)				;r upper arm
	CONSTRAINTcreate( b\p[ 6 ] , b\p[ 7 ] , 1.5)			;r forearm
	CONSTRAINTcreate( b\p[ 7 ] , b\p[ 8 ] , 2)				;r hand
	CONSTRAINTcreate( b\p[ 0 ] , b\p[ 9 ] , 2)				;l thigh
	CONSTRAINTcreate( b\p[ 9 ] , b\p[ 10 ] , 2.5)			;l calf
	CONSTRAINTcreate( b\p[ 10 ] , b\p[ 11 ] , 2)			;l foot
	CONSTRAINTcreate( b\p[ 0 ] , b\p[ 12 ], 2 )			;r thigh
	CONSTRAINTcreate( b\p[ 12 ] , b\p[ 13 ] , 2.5 )		;r calf
	CONSTRAINTcreate( b\p[ 13 ] , b\p[ 14 ] , 2)			;r foot
		
	Return b
	
End Function
	
;=====================================================
;=====================================================
;=====================================================

Function BODYreset( b.body , x#=0 , y#=0 )

	For l = 0 To b\PointMasses-1
		p.pointmass = b\p[l]
		PositionEntity p\Pivot , x+p\Offset\x , y+p\Offset\y , 0
		VECTORset( p\Old , EntityX( p\Pivot ) , EntityY( p\Pivot ) )
		ResetEntity p\Pivot
	Next

End Function

;=====================================================
;=====================================================
;=====================================================

Function POINTMASSapplyforce( p.Pointmass, Ax#, Ay# )

	VECTORset( p\Acceleration , Ax, Ay )

End Function

;=====================================================
;=====================================================
;=====================================================

Function POINTMASScreate.pointmass( b.body , x# , y# , Pick = False , Mass#=1.0 , InvMass#=1.0 )

	p.pointmass = New pointmass
	b\PointMasses = b\PointMasses + 1
	p\Pivot = CopyEntity ( JOINT )
	p\Pick = Pick 
	p\Offset = VECTORcreate(x,y)
	p\Old = VECTORcreate()
	p\Acceleration = VECTORcreate()
	p\Mass = Mass
	p\InvMass = InvMass
	EntityType p\Pivot, C_PointMass
	Return p
	
End Function

;=====================================================
;=====================================================
;=====================================================

Function POINTMASSupdate()

	DRAG# = .01
	FRICTION# = .9 ;.5

	For p.pointmass = Each pointmass

		VelocityX# = ( EntityX( p\Pivot ) - p\Old\x )
		VelocityY# = ( EntityY( p\Pivot ) - p\Old\y )
		VECTORset( p\Old , EntityX( p\Pivot ) , EntityY( p\Pivot ) )
		Speed# = Sqr( VelocityX * VelocityX + VelocityY * VelocityY )
		
		;drag
		DragX# = VelocityX * Speed * DRAG
		DragY# = VelocityY * Speed * DRAG
		
		;friction
		COLLIDED = False
		For c = 1 To CountCollisions( p\Pivot )
			If CollisionNY( p\Pivot, c ) > 0
				COLLIDED = True
			EndIf
		Next
		If COLLIDED
			FrictionX# = VelocityX * FRICTION
			FrictionY# = VelocityY * FRICTION
		Else
			FrictionX# = 0
			FrictionY# = 0
		EndIf
		
		;movement
		MoveX# = ( VelocityX - DragX - FrictionX ) + p\Acceleration\x * TIMESTEP
		MoveY# = ( VelocityY - DragY - FrictionY ) + p\Acceleration\y * TIMESTEP
		TranslateEntity p\Pivot , MoveX , MoveY, 0
		p\Acceleration\x = 0
		p\Acceleration\y = 0
	Next

End Function

;=====================================================
;=====================================================
;=====================================================
		
Function CONSTRAINTcreate( p1.PointMass , p2.PointMass , Radius# , MinLength#=0 )

	c.constraint = New constraint
	c\p1 = p1
	c\p2 = p2
	c\Radius = Radius
	c\MaxLength = EntityDistance( p1\pivot , p2\pivot )
	c\Entity = CopyEntity( LIMB )
	
	If MinLength > 0 
		c\MinLength = MinLength 
		c\Flag = PUSH
		EntityColor c\Entity,255,0,0
	Else 
		c\MinLength = c\MaxLength
		c\Flag = PUSH_PULL
		EntityColor c\Entity,0,0,255
	EndIf

End Function

;=====================================================
;=====================================================
;=====================================================

Function CONSTRAINTupdate()

	For i = 1 To iterations
		For c.constraint = Each constraint
			p1.pointmass = c\p1
			p2.pointmass = c\p2
			length# = EntityDistance( p2\Pivot, p1\Pivot )+.0000001 
			If length > 0
				Select c\Flag 
					Case PUSH_PULL
						Diff# = ( Length - c\MaxLength ) / ( Length * ( p1\InvMass+p2\InvMass) )
						dx# = ( EntityX( p2\Pivot ) - EntityX( p1\Pivot ) ) * Diff
						dy# = ( EntityY( p2\Pivot ) - EntityY( p1\Pivot ) ) * Diff 
						TranslateEntity p1\Pivot , dx*p1\InvMass , dy*p1\InvMass , 0 
						TranslateEntity p2\Pivot , -dx*p2\InvMass , -dy*p2\InvMass , 0 
					Case PUSH
						If length < c\MinLength
							Diff# = ( Length - c\MinLength ) / ( Length * ( p1\InvMass+p2\InvMass) )
							dx# = ( EntityX( p2\Pivot ) - EntityX( p1\Pivot ) ) * Diff
							dy# = ( EntityY( p2\Pivot ) - EntityY( p1\Pivot ) ) * Diff 
							TranslateEntity p1\Pivot , dx*p1\InvMass , dy*p1\InvMass , 0 
							TranslateEntity p2\Pivot , -dx*p2\InvMass , -dy*p2\InvMass , 0 
						EndIf
					Case PULL
						If length > c\MaxLength
							Diff# = ( Length - c\MaxLength ) / ( Length * ( p1\InvMass+p2\InvMass) )
							dx# = ( EntityX( p2\Pivot ) - EntityX( p1\Pivot ) ) * Diff
							dy# = ( EntityY( p2\Pivot ) - EntityY( p1\Pivot ) ) * Diff 
							TranslateEntity p1\Pivot , dx*p1\InvMass , dy*p1\InvMass , 0 
							TranslateEntity p2\Pivot , -dx*p2\InvMass , -dy*p2\InvMass , 0 
						EndIf
				End Select
			EndIf
		Next
	Next
	
	For c.constraint = Each constraint
		PositionEntity c\Entity , EntityX( c\p1\Pivot ) , EntityY( c\p1\Pivot ) , EntityZ( c\p1\Pivot )
		PointEntity c\Entity , c\p2\pivot
		If c\Flag = PUSH_PULL
			ScaleEntity c\Entity, c\Radius , c\Radius , EntityDistance( c\p1\Pivot , c\p2\Pivot )
		EndIf
		If c\Flag = PUSH
			ScaleEntity c\Entity, c\Radius , c\Radius , c\MinLength 
		EndIf			
	Next

End Function

;=====================================================
;=====================================================
;=====================================================
		
Function VECTORcreate.vector( x#=0 , y#=0 )

	v.vector = New vector
	v\x  = x
	v\y = y
	Return v
	
End Function

;=====================================================
;=====================================================
;=====================================================

Function VECTORset( v.vector , x#=0, y#=0 )

	v\x = x
	v\y = y
	
End Function
