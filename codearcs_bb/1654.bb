; ID: 1654
; Author: Nilium
; Date: 2006-03-31 20:57:09
; Title: Lotus Particle System R2 - Part 1
; Description: Part 1 - The now-public domain Lotus Particle System R2

Dim gSine#(0)
Dim gCosine#(0)
Dim gGraphArray#( 0, 1 )
Dim gAlphaPartArray#( 0, 0 )
Dim gLotusUpdateTimes%( 5 )

Function Min#(A#,B#)
	If A < B Then Return B
	Return A
End Function

Function Max#(A#,B#)
	If A > B Then Return B
	Return A
End Function

Function lSort( L, R )
	
	If R <= L Then Return False
	
	Local A, B, SwapA#, SwapB#, Middle#
	A = L
	B = R
	
	Middle# = gAlphaPartArray( (L+R)/2, 0 )
	
	Repeat
		
		While gAlphaPartArray( A, 0 ) < Middle
			A = A + 1
			If A > R Then Exit
		Wend
		
		While  Middle < gAlphaPartArray( B, 0 )
			B = B - 1
			If B < 0 Then Exit
		Wend
		
		If A > B Then Exit
		
		SwapA = gAlphaPartArray( A, 0 )
		SwapB = gAlphaPartArray( A, 1 )
		gAlphaPartArray( A, 0 ) = gAlphaPartArray( B, 0 )
		gAlphaPartArray( A, 1 ) = gAlphaPartArray( B, 1 )
		gAlphaPartArray( B, 0 ) = SwapA
		gAlphaPartArray( B, 1 ) = SwapB
		
		A = A + 1
		B = B - 1
		
		If B < 0 Then Exit
		
	Forever
	
	If L < B Then lSort( L, B )
	If A < R Then lSort( A, R )
End Function

Function InsertionSort( Size% )
	Local i, j, index
	
	For i = 1 To Size - 1
		index = gGraphArray( i, 0 )
		j = i
		While j > 0 And gGraphArray( j-1, 0 ) > index
			gGraphArray( j, 0 ) = gGraphArray( j - 1, 0 )
			j = j - 1
		Wend
		gGraphArray( j, 0 ) = index
	Next
End Function


;; Line Helper functions, written by Jeremy Alessi (they are public domain)
Function ReturnedY#(x#, X1#, Y1#, X2#, Y2#)
	Return (  Slope( X1#, Y1#, X2#, Y2# ) * x# + YIntercept( X1#, Y1#, X2#, Y2# ) )
End Function

Function Slope#(X1#, Y1#, X2#, Y2#)
	m# = ( ( Y2# - Y1# ) / ( X2# - X1# ) )

	If m#=0
		Return .01 ;avoid infinity
	EndIf
	
	Return m#
End Function

Function YIntercept(X1#, Y1#, X2#, Y2#)
	Return (Y1# - Slope#( X1#, Y1#, X2#, Y2# ) * X1#)
End Function

Type LotusParticle
	Field Texture.LotusTexture
	Field Frame%
	Field FrameStart%
	Field FrameLength%
	Field Animated%
	
	Field Parent.LotusEmitter
	Field Child.LotusEmitter
	Field Trail.LotusEmitter
	
	Field LastPositionX#
	Field LastPositionY#
	Field LastPositionZ#
	
	Field PositionX#
	Field PositionY#
	Field PositionZ#
	
	Field TranslationX#
	Field TranslationY#
	Field TranslationZ#
	
	Field VelocityX#
	Field VelocityY#
	Field VelocityZ#
	
	Field OVelocityX#
	Field OVelocityY#
	Field OVelocityZ#
	
	Field AccelerationX#
	Field AccelerationY#
	Field AccelerationZ#
	
	Field VelocityDecay#
	
	Field AngleX#
	Field AngleY#
	Field AngleZ#
	
	Field AngleVelocityX#
	Field AngleVelocityY#
	Field AngleVelocityZ#
	
	Field AngleAccelerationX#
	Field AngleAccelerationY#
	Field AngleAccelerationZ#
	
	Field DeflectorSpeedX#
	Field DeflectorSpeedY#
	Field DeflectorSpeedZ#
	
	Field RedFrom%
	Field GreenFrom%
	Field BlueFrom%
	Field AlphaFrom#
	
	Field RedTo%
	Field GreenTo%
	Field BlueTo%
	Field AlphaTo#
	
	Field ColorGraph.LotusGraph
	
	Field SizeFromX#
	Field SizeFromY#
	Field SizeFromZ#
	
	Field SizeToX#
	Field SizeToY#
	Field SizeToZ#
	
	Field SizeGraph.LotusGraph
	
	Field WaveRadiusX#
	Field WaveRadiusY#
	Field WaveRadiusZ#
	
	Field WaveSpeedX#
	Field WaveSpeedY#
	Field WaveSpeedZ#
	
	Field BlendMode%
	
	Field Vertex%
	
	Field RollMode%
	
	Field LifeSpan#
	Field LifeBegan#
	Field Life#
	Field ParticleMesh%
	Field Previous.LotusParticle
	
	Field GravityEnabled%
	Field Gravity#
	Field Weight#
	
	Field Range#
	
	Field ViewMode%
	Field LifeMode%
	
	Field ChildMode%
	Field Frozen%
	Field Hidden%
	Field Draw%
	Field Cull%
	Field Sorting%
	
	Field Bounce%
	Field BounceDecay#
	Field BounceMax%
	Field Bounces%
	
	Field MinY#
	
	Field BounceSound%
	Field BounceSoundChannel%
	Field BounceSoundRange#
	
	Field SplineMesh%
	
	Field DeflectorsAffect%
End Type

Type LotusEmitter
	Field Name$

	Field Entity%
	Field Texture.LotusTexture
	Field Frame%
	Field FrameStart%
	Field FrameLength%
	Field Animated%

	Field Parent.LotusEmitter
	Field Child.LotusEmitter
	Field Trail.LotusEmitter
	
	Field TranslationJitterUp%
	Field TranslationJitterDown%
	
	Field PositionX#
	Field PositionY#
	Field PositionZ#
	
	Field AngleX#
	Field AngleY#
	Field AngleZ#
	
	Field TranslationJitterX#
	Field TranslationJitterY#
	Field TranslationJitterZ#
	
	Field VelocityJitterUp
	Field VelocityJitterDown
	
	Field VelocityJitterX#
	Field VelocityJitterY#
	Field VelocityJitterZ#
	
	Field VelocityDecay#
	
	Field TranslationX#
	Field TranslationY#
	Field TranslationZ#
	
	Field VelocityX#
	Field VelocityY#
	Field VelocityZ#
	
	Field AccelerationX#
	Field AccelerationY#
	Field AccelerationZ#
	
	Field AngleVelocityX#
	Field AngleVelocityY#
	Field AngleVelocityZ#
	
	Field AngleAccelerationX#
	Field AngleAccelerationY#
	Field AngleAccelerationZ#
	
	Field RandomRotationX#
	Field RandomRotationY#
	Field RandomRotationZ#
	
	Field CircleRadiusX#
	Field CircleSpeedX#
	Field CircleRadiusY#
	
	Field CircleSpeedY#
	Field CircleRadiusZ#
	Field CircleSpeedZ#
	
	Field RedFrom%
	Field GreenFrom%
	Field BlueFrom%
	Field AlphaFrom#
	
	Field RedTo%
	Field GreenTo%
	Field BlueTo%
	Field AlphaTo#
	
	Field ColorJitterRed%
	Field ColorJitterGreen%
	Field ColorJitterBlue%
	Field ColorJitterAlpha#
	Field ColorJitterUniform%
	Field ColorJitterUp%
	Field ColorJitterDown%
	
	Field ColorGraph.LotusGraph
	
	Field SizeJitterX#
	Field SizeJitterY#
	Field SizeJitterZ#
	Field SizeJitterUniform%
	Field SizeJitterUp%
	Field SizeJitterDown%
	
	Field SizeFromX#
	Field SizeFromY#
	Field SizeFromZ#
	
	Field SizeToX#
	Field SizeToY#
	Field SizeToZ#
	
	Field SizeGraph.LotusGraph
	
	Field CylinderX#
	Field CylinderY#
	Field CylinderZ#
	
	Field CubeX#
	Field CubeY#
	Field CubeZ#
	
	Field WaveRadiusX#
	Field WaveRadiusY#
	Field WaveRadiusZ#
	
	Field WaveSpeedX#
	Field WaveSpeedY#
	Field WaveSpeedZ#
	
	Field BlendMode%
	
	Field LifeSpan%
	Field LifeSpanJitter%
	Field LifeSpanJitterUp%
	Field LifeSpanJitterDown%
	
	Field Active%
	Field ActiveSpan%
	Field FreeOnEndActive%
	
	Field GravityEnabled%
	Field Gravity#
	Field GravityJitter#
	Field GravityJitterUp%
	Field GravityJitterDown%
	Field Weight#
	
	Field RollMode%
	
	Field Emit%
	Field EmissionRate%
	
	Field InnerRadius#
	Field Radius#
	Field YRadius#
	
	Field ParticleMesh%
	Field Latest.LotusParticle
	Field ViewMode%
	Field LifeMode%
	Field WaitA#		;; Auto-emission wait
	Field WaitB#		;; Child-emission wait
	Field WaitC#		;; Trail-emission wait
	Field WaitSpan%
	Field ChildMode%
	Field Range#
	
	Field Frozen%
	Field Hidden%
	Field Cull%
	Field Sorting%
	Field DWaitSpan%
	Field WaitSpanJitter%
	
	Field Bounce%
	Field BounceDecay#
	Field BounceMax%
	
	Field MinY#
	
	Field EmitSound%
	Field BounceSound%
	Field LoopEmitSound%
	Field EmitSoundChannel%
	Field EmitSoundRange#
	Field BounceSoundRange#
	
	Field SplineMesh%
	
	Field DeflectorsAffect%
End Type

Type LotusDeflector
	Field PositionX#
	Field PositionY#
	Field PositionZ#
	Field Entity%
	Field Radius#
	Field Strength#
	Field Active%
End Type

Type LotusTexture
	Field Bitmap%
	Field Flags%
	Field Path$
	Field Add%
	Field Multiply%
	Field Alpha%
	Field UStep#
	Field VStep#
	Field Rows%
	Field Columns%
End Type

Type LotusGraph
	Field Bank%
	Field Keys%
	Field Width#
End Type

Dim LoadedEmitters%(255)						;; The emitters loaded by LoadASCIIEmitters
Global gLoadedEmittersCount%					;; The amount of emitters loaded through LoadASCIIEmitters

Dim LoadedDeflectors%(255)					;; The deflectors loaded by LoadASCIIEmitters
Global gLoadedDeflectorsCount%				;; The amount of deflectors loaded through LoadASCIIEmitters

Dim LoadedTextures%(255)						;; The textures loaded by LoadASCIIEmitters
Global gLoadedTexturesCount%					;; The amount of textures loaded through LoadASCIIEmitters

Global gLotusParticlePivot%					;; Pivot used to determine various properties and the position and rotation of particles
Global gLotusTPivot%						;; Extra pivot used for view mode calculations
Global gLotusParticleMesh%					;; The container-mesh for all Lotus particles
Global gLotusCamera%						;; The camera particles will face (when told to)

Global gLotusNullTexture.LotusTexture			;; The 'Null Texture' object- used in the case that no texture is available
Global gLotusEmitterTexture%					;; The texture assigned to emitter cones.
Global gNullTexture%						;; The handle of the 'Null Texture'

Global gLotusParticleCount%					;; The amount of particles in existence
Global gLotusAlphaParticleCount%				;; The amount of alpha-blended particles to be sorted
Global gLotusParticlesDrawn%					;; The amount of particles drawn during the last call to UpdateLotusParticles

Const cP_QUAD% = 0							;; SetEmitterParticleMesh enumerator; sets a particle's mesh to a quad (two triangles)
Const cP_TRIANGLE% = 1						;; SetEmitterParticleMesh enumerator; sets a particle's mesh to a triangle
Const cP_TRAIL% = 2							;; SetEmitterParticleMesh enumerator; sets a particle's mesh to the trail style (all particles are 'connected' by quads)
Const cP_TRAILVERTICAL% = 3					;; SetEmitterParticleMesh enumerator; sets a particle's mesh to the vertical trail style (all particles are 'connected' by quads)
Const cP_DENT% = 4							;; SetEmitterParticleMesh enumerator; sets a particle's mesh to a quad with a dent in the center (four triangles)

Const updTimeSort% = 0						;; Indices for update times
Const updTimeEmit% = 1
Const updTimePart% = 2
Const updTimeDrawAlpha% = 3
Const updTimeTexture% = 4
Const updTimeAll% = 5

Global gWidth%									;; The screen buffer width and height
Global gHeight%

Const cUSE_EMITTERCONES = 0						;; Whether or not to use emitter cones (little graphical thingy); recommended to leave off

Global gFacingX#,gFacingY#,gFacingZ#				;; Facing vector

Type LotusVector
	Field X#
	Field Y#
	Field Z#
End Type

Global gTempLotusVector.LotusVector	;can be used for temporary LotusVector operations.
Global gNullLotusVector.LotusVector	;zero-LotusVector.

Type LotusStringPiece
	Field Text$
End Type

Function DivideString(Text$,Sep$)
	Text$ = Trim(Text$)
	Local Pieces = 0
	OT$ = Text$
	Text$ = ""
	For E = 1 To Len(OT)
		C = Asc(Mid(OT,E,1))
		S = Instr(Sep,Chr(C))

		If (Not StringOpen) And C = 39 Then Exit

		If C = 34 Then StringOpen = Not StringOpen

		If (S > 0 Or C = 32) And (Not StringOpen) Then
			Text = Text + Chr(4)
		Else
			Text = Text + Chr(C)
		EndIf
	Next

	While Text$ <> ""
		If Asc(Left(Text$,1)) = 34 Then
			Text$ = Right(Text$,Len(Text$)-1)
			Closest = Instr(Text$,Chr(34))
		Else
			Closest = Instr(Text$,Chr(4))
		EndIf

		If Closest Then
			NText$ = Trim(Left(Text$,Closest-1))
			Text$ = Trim(Right(Text$,Len(Text$)-Closest))
		Else
			NText$ = Text$
			Text$ = ""
		EndIf

		P.LotusStringPiece = New LotusStringPiece
		P\Text$ = Replace(NText$,"\"+Chr(34),Chr(34))
		Pieces = Pieces + 1
	Wend

	Return Pieces
End Function

;creates a LotusVector and returns it
Function LotusVec.LotusVector(X#=0,Y#=0,Z#=0)
	V.LotusVector = New LotusVector
	V\X = X
	V\Y = Y
	V\Z = Z
	Return V
End Function

;adds LotusVector B to LotusVector A
Function LotusVec_Add(A.LotusVector, B.LotusVector)
	If Not A <> Null Or B <> Null Then Return False
	A\X = A\X + B\X
	A\Y = A\Y + B\Y
	A\Z = A\Z + B\Z
	Return True
End Function

;subtracts LotusVector B from LotusVector A
Function LotusVec_Subtract(A.LotusVector, B.LotusVector)
	If Not A <> Null Or B <> Null Then Return False
	A\X = A\X - B\X
	A\Y = A\Y - B\Y
	A\Z = A\Z - B\Z
	Return True
End Function

;returns the difference of LotusVector A and LotusVector B
Function LotusVec_Difference.LotusVector(A.LotusVector, B.LotusVector)
	If Not A <> Null Or B <> Null Then Return Null
	Return LotusVec(A\X-B\X,A\Y-B\Y,A\Z-B\Z)
End Function

;returns the sum of LotusVector A and LotusVector B
Function LotusVec_Sum.LotusVector(A.LotusVector, B.LotusVector)
	If Not A <> Null Or B <> Null Then Return Null
	Return LotusVec(A\X+B\X,A\Y+B\Y,A\Z+B\Z)
End Function

;multiplies LotusVector A by LotusVector B
Function LotusVec_Multiply(A.LotusVector, B.LotusVector)
	If Not A <> Null Or B <> Null Then Return False
	A\X = A\X * B\X
	A\Y = A\Y * B\Y
	A\Z = A\Z * B\Z
	Return True
End Function

;divides LotusVector A by LotusVector B
Function LotusVec_Divide(A.LotusVector, B.LotusVector)
	If Not A <> Null Or B <> Null Then Return False
	A\X = A\X / B\X
	A\Y = A\Y / B\Y
	A\Z = A\Z / B\Z
	Return True
End Function

;scales LotusVector A by Scalar B
Function LotusVec_Scale(A.LotusVector, B#)
	If Not A <> Null Then Return False
	A\X = A\X * B
	A\Y = A\Y * B
	A\Z = A\Z * B
	Return True
End Function

;divides LotusVector A by Scalar B
Function LotusVec_SDivide(A.LotusVector,B#)
	If Not A <> Null Or B = 0 Then Return False
	A\X = A\X / B
	A\Y = A\Y / B
	A\Z = A\Z / B
	Return True
End Function

;makes LotusVector A the cross product of LotusVectors A and B
Function LotusVec_CrossProduct(A.LotusVector, B.LotusVector)
	If Not A <> Null Or B <> Null Then Return False
	X# = A\Y*B\Z - A\Z*B\Y
	Y# = A\Z*B\X - A\X*B\Z
	Z# = A\X*B\Y - A\Y*B\X
	A\X = X
	A\Y = Y
	A\Z = Z
	Return True
End Function

;returns the dot product of LotusVectors A and B
Function LotusVec_DotProduct#(A.LotusVector, B.LotusVector)
	If Not A <> Null Or B <> Null Then Return False
	Return A\X * B\X + A\Y * B\Y + A\Z * B\Z;
End Function

;copies the contents of LotusVector A to a new LotusVector and returns it
Function LotusVec_Copy.LotusVector(A.LotusVector)
	If Not A <> Null Then Return Null
	B.LotusVector = New LotusVector
	B\X = A\X
	B\Y = A\Y
	B\Z = A\Z
	Return B
End Function

;copies the contents of LotusVector B to LotusVector A
Function LotusVec_CopyTo(A.LotusVector,B.LotusVector)
	If Not A <> Null Or B <> Null Then Return False
	A\X = B\X
	A\Y = B\Y
	A\Z = B\Z
End Function

;normalizes LotusVector A
Function LotusVec_Normalize(A.LotusVector)
	If Not A <> Null Then Return False
	M# = LotusVec_Magnitude(A)
	A\X = A\X / M
	A\Y = A\Y / M
	A\Z = A\Z / M
	Return True
End Function

;returns the magnitude of LotusVector A
Function LotusVec_Magnitude#(A.LotusVector)
	If Not A <> Null Then Return False
	Return Sqr(A\X*A\X+A\Y*A\Y+A\Z*A\Z)
End Function

;returns the distance between LotusVector A and LotusVector B
Function LotusVec_Distance#(A.LotusVector,B.LotusVector)
	If Not A <> Null Or B <> Null Then Return False
	DX# = A\X - B\X
	DY# = A\Y - B\Y
	DZ# = A\Z - B\Z
	Return Sqr(DX*DX+DY*DY+DZ*DZ)
End Function

;inverts LotusVector A
Function LotusVec_Invert(A.LotusVector)
	If Not A <> Null Then Return False
	A\X = -A\X
	A\Y = -A\Y
	A\Z = -A\Z
	Return True
End Function

;returns an inverted LotusVector A
Function LotusVec_Inverse.LotusVector(A.LotusVector)
	If Not A <> Null Then Return Null
	B.LotusVector = LotusVec_Copy(A)
	LotusVec_Invert B
	Return B
End Function

;returns the position of an entity in a LotusVector.  G = Global
Function LotusVec_Entity_Position.LotusVector(Entity,G = False)
	If Entity = 0 Then Return Null
	V.LotusVector = New LotusVector
	V\X = EntityX(Entity,G)
	V\Y = EntityY(Entity,G)
	V\Z = EntityZ(Entity,G)
	Return V
End Function

;returns the angle of an entity in a LotusVector.  G = Global
Function LotusVec_Entity_Angle.LotusVector(Entity,G = False)
	If Entity = 0 Then Return Null
	V.LotusVector = New LotusVector
	V\X = EntityPitch(Entity,G)
	V\Y = EntityYaw(Entity,G)
	V\Z = EntityRoll(Entity,G)
	Return V
End Function

;positions an entity at LotusVector A
Function LotusVec_PositionEntity(Entity,A.LotusVector,Glb=False)
	If Entity = 0 Or A = Null Then Return False
	PositionEntity Entity,A\X,A\Y,A\Z,Glb
End Function

;rotates an entity to LotusVector A
Function LotusVec_RotateEntity(Entity,A.LotusVector,Glb=False)
	If Entity = 0 Or A = Null Then Return False
	RotateEntity Entity,A\X,A\Y,A\Z,Glb
End Function

;turns an entity by LotusVector A
Function LotusVec_TurnEntity(Entity,A.LotusVector)
	If Entity = 0 Or A = Null Then Return False
	TurnEntity Entity,A\X,A\Y,A\Z
End Function

;aligns an entity to LotusVector A
Function LotusVec_AlignToLotusVector(Entity,A.LotusVector,Axes=3,Trans#=1)
	If Entity = 0 Or A = Null Then Return False
	AlignToVector Entity,A\X,A\Y,A\Z,Axes,Trans
End Function

;moves an entity by LotusVector A
Function LotusVec_MoveEntity(Entity,A.LotusVector)
	If Entity = 0 Or A = Null Then Return False
	MoveEntity Entity,A\X,A\Y,A\Z
End Function

;translates an entity by LotusVector A
Function LotusVec_TranslateEntity(Entity,A.LotusVector)
	If Entity = 0 Or A = Null Then Return False
	TranslateEntity Entity,A\X,A\Y,A\Z
End Function

;returns the normals of a vertex
Function LotusVec_VertexNormal.LotusVector(Surface,Index)
	If Surface = 0 Or Index < 0 Or Index > CountVertices(Surface)-1 Then Return Null
	Return LotusVec(VertexNX(Surface,Index),VertexNY(Surface,Index),VertexNZ(Surface,Index))
End Function

;returns the local position of a vertex
Function LotusVec_VertexPosition.LotusVector(Surface,Index)
	If Surface = 0 Or Index < 0 Or Index > CountVertices(Surface)-1 Then Return Null
	Return LotusVec(VertexX(Surface,Index),VertexY(Surface,Index),VertexZ(Surface,Index))
End Function

;sets the position of a vertex to LotusVector A
Function LotusVec_VertexCoords(Surface,Index,A.LotusVector)
	If Surface = 0 Or Index < 0 Or Index > CountVertices(Surface)-1 Or A = Null Then Return False
	VertexCoords Surface,Index,A\X,A\Y,A\Z
End Function

;returns the normal of a triangle
Function LotusVec_TriangleNormal.LotusVector(Surface,Index)
	If Surface = 0 Or Index < 0 Or Index > CountTriangles(Surface)-1 Then Return Null
	Local VNormals.LotusVector[2]
	For N = 0 To 2
			VNormals[N] = LotusVec_VertexNormal(Surface,TriangleVertex(Surface,Index,N))
	Next
	LotusVec_Subtract VNormals[0],VNormals[1]
	LotusVec_Subtract VNormals[1],VNormals[2]
	LotusVec_CrossProduct(VNormals[0],VNormals[1])
	Delete VNormals[1]
	Delete VNormals[2]
	Return VNormals[0]
End Function

;returns the local position of a triangle
Function LotusVec_TrianglePosition.LotusVector(Surface,Index)
	If Surface = 0 Or Index < 0 Or Index > CountTriangles(Surface)-1 Then Return Null
	Local VNormals.LotusVector[2]
	For N = 0 To 2
			VNormals[N] = LotusVec_VertexPosition(Surface,TriangleVertex(Surface,Index,N))
	Next
	LotusVec_Add(VNormals[0],VNormals[1])
	LotusVec_Add(VNormals[0],VNormals[2])
	LotusVec_SDivide(VNormals[0],3)
	Delete VNormals[1]
	Delete VNormals[2]
	Return VNormals[0]
End Function

;transforms LotusVector A from one coordinate system to another
Function LotusVec_TFormPoint(A.LotusVector,EntityA,EntityB)
	If Not A <> Null Then Return False
	TFormPoint A\X,A\Y,A\Z,EntityA,EntityB
End Function

;returns the TFormed vector
Function LotusVec_TFormedA.LotusVector()
	Return LotusVec(TFormedX(),TFormedY(),TFormedZ())
End Function

;puts the values of the TFormed vector into LotusVector A
Function LotusVec_TFormedB(A.LotusVector)
	If A = Null Then Return False
	A\X = TFormedX()
	A\Y = TFormedY()
	A\Z = TFormedZ()
End Function

Global ParticleTiles=0
Global BulletHoleEmitter=0
Global BulletSparkEmitter=0
Global BulletSmokeEmitter=0

Function FXP_SetupTilesets()
	If ParticleTiles = 0 Then
		Tiles = LoadLotusTileset("media/particles.png",59,2,4,1,0,1)
		ParticleTiles = Tiles
	Else
		Tiles = ParticleTiles
	EndIf
	
	Return Tiles
End Function

Function CreateBulletSpark(X#,Y#,Z#,NX#,NY#,NZ#)
	Tiles = FXP_SetupTilesets()
	
	If BulletSparkEmitter = 0 Then
		e = CreateEmitter()
		ApplyTexture e,Tiles,3
		
		SetEmitterVelocity e,0,.2,0
		SetEmitterSizeJitter e,1,0,0,0,1,1
		SetEmitterLifespan e,8
		SetEmitterSorting e,1
		SetEmitterBlend e,3
		SetEmitterColorFrom e,246,167,76,.6
		SetEmitterColorTo e,235,155,0,0
		SetEmitterSizeTo e,1,.5
		SetEmitterSizeFrom e,.5,.25
		SetEmitterRandomRotation e,25,25,0
		SetEmitterRollMode e,1
		SetEmitterEmissionRate e,32
		BulletSparkEmitter = e
	Else
		e = BulletSparkEmitter
	EndIf
	
	If BulletSmokeEmitter = 0 Then
		s = CreateEmitter()
		SetEmitterLifespan s,120
		SetEmitterVelocity s,0,.005,0
		SetEmitterVelocityDecay s,1
		SetEmitterEmissionRate s,4
		SetEmitterColorFrom s,88,86,84,.7
		SetEmitterColorTo s,77,76,75,0
		SetEmitterColorJitter s,15,15,15,0,1
		ApplyTexture s,Tiles,4
		SetEmitterSizeFrom s,0,0
		SetEmitterRandomRotation s,0,0,180
		SetEmitterSizeTo s,1,2
		SetEmitterSizeJitter s,.4,.4,.4,1,1,0
		SetEmitterTranslation s,0,.01,0
		SetEmitterTranslationJitter s,0,.025,0,1,0
		BulletSmokeEmitter = s
	Else
		s = BulletSmokeEmitter
	EndIf
	
	If BulletHoleEmitter = 0 Then
		b = CreateEmitter()
		SetEmitterLifespan b,3000
		SetEmitterViewmode b,2
		SetEmitterColorFrom b,0,0,0,8
		SetEmitterColorTo b,0,0,0,0
		SetEmitterSize b,1,1,1
		SetEmitterRandomRotation b,0,0,180
		SetEmitterSizeJitter b,.4,.4,.4,1,1,1
		ApplyTexture b,Tiles,1
	Else
		b = BulletHoleEmitter
	EndIf
	
	PositionEntity e,X,Y,Z
	PositionEntity s,X,Y,Z
	AlignToVector e,NX,NY,NZ,2,1
	AlignToVector s,NX,NY,NZ,3,1
	MoveEntity s,0,0,.7
	MoveEntity e,0,0,.2
	PositionEntity b,X,Y,Z
	AlignToVector b,NX,NY,NZ,3,1
	MoveEntity b,0,0,.025
	
	CreateParticle(s)
	CreateParticle(e)
	CreateParticle(b)
End Function

Function CreateTorch()
	Tiles = FXP_SetupTilesets()
	
	E = CreateEmitter()
	ApplyTexture E,Tiles,0
	SetEmitterTranslation E,0,.045,0
	SetEmitterLifespan E,600,400
	SetEmitterBlend E,3
	SetEmitterSizeFrom E,.45,.45
	SetEmitterColor E,209,151,78,1
	SetEmitterColorTo E,150,80,0,0
	SetEmitterColorJitter E,42,42,42,0,1
	SetEmitterSizeTo E,1.25,1.25
	SetEmitterRandomRotation E,0,0,50
	SetEmitterViewMode E,1
	SetEmitterSizeJitter E,.25,.25,.25,1,1,0
	
	N = CreateEmitter()
	ApplyTexture N,Tiles,7
	SetEmitterParent N,E
	SetEmitterChildMode E,2
	SetEmitterTranslation N,0,.045,0
	SetEmitterVelocity N,0,0,.0025
	SetEmitterRandomRotation N,20,360,180
	SetEmitterLifeSpan N,3500
	SetEmitterColorFrom N,120,120,120,.7
	SetEmitterColorTo N,100,100,100,0
	SetEmitterBlend N,1
	SetEmitterWaitSpan E,2
	SetEmitterWaitSpan N,5
	SetEmitterSizeJitter N,.5,.5,.5,1,1,0
	SetEmitterLifeMode N,2
	SetEmitterSizeFrom N,.05,.05
	SetEmitterSizeTo N,1.5,1.5
	SetEmitterEmissionRate E,1
	SetEmitterLifeMode E,2
	
	SetEmitterCulling N,0
	
	Return E
End Function

;#Region LotusXML.bb
	;#Region DESCRIPTION
		; XML load / parse / save functions
		; Written by Blitztastic, butchered by Noel Cower
	;#End Region
	
	;#Region CLASSES
		Type XMLnodelist
			Field node.XMLnode
			Field nextnode.XMLnodelist
			Field prevnode.XMLnodelist
		End Type
		
		; for internal use, do not use in code outside of this file
		Type XMLworklist
			Field node.XMLnode
		End Type
		
		
		Type XMLnode
			Field tag$,value$,path$
			Field firstattr.XMLattr
			Field lastattr.XMLattr	
			Field attrcount,fileid
			Field endtag$
			
			; linkage functionality
			Field firstchild.XMLnode
			Field lastchild.XMLnode
			Field childcount
			Field nextnode.XMLnode
			Field prevnode.XMLnode
			Field parent.XMLnode
		End Type
		
		Type XMLattr
			Field name$,value$
			Field sibattr.XMLattr
			Field parent.XMLnode
		End Type
		
		Global XMLFILEID
	;#End Region
	
	;#Region PROCEDURES
		Function ReadXML.XMLnode(filename$)
			infile = ReadFile(filename$)
			XMLFILEID=MilliSecs()
			x.XMLnode = XMLReadNode(infile,Null)
			CloseFile infile
			Return x
		End Function
		
		Function WriteXML(filename$,node.XMLnode,writeroot=False)
			outfile = WriteFile(filename$)
			WriteLine outfile,"<?xml version="+Chr$(34)+"1.0"+Chr$(34)+" ?>"
			XMLwriteNode(outfile,node)
			CloseFile outfile
		End Function
		
		Function XMLOpenNode.XMLnode(parent.XMLnode,tag$="")
			x.XMLnode = New XMLnode
			x\tag$=tag$
			x\fileid = XMLFILEID; global indicator to group type entries (allows multiple XML files to be used)
			XMLaddNode(parent,x)
			Return x
		End Function
		
		Function XMLCloseNode.XMLnode(node.XMLnode)
			Return node\parent
		End Function
		
		; adds node to end of list (need separate function for insert, or mod this on)
		Function XMLAddNode(parent.XMLnode,node.XMLnode)
			If parent <> Null
				If parent\childcount = 0 Then
					parent\firstchild = node
				Else
					parent\lastchild\nextnode = node
				End If
				node\prevnode = parent\lastchild
				parent\lastchild = node
				parent\childcount = parent\childcount +1
				node\path$ = parent\path$+parent\tag$
			End If
			node\parent = parent
			node\path$=node\path$+"/"
		End Function
		
		Function XMLDeleteNode(node.XMLnode)
			n.XMLnode = node\firstchild
			; delete any children recursively
			While n <> Null
				nn.XMLnode= n\nextnode
				XMLdeletenode(n)
				n = nn
			Wend
		
			; delete attributes for this node
			a.XMLattr = node\firstattr
			While a <> Null
				na.XMLattr = a\sibattr
				Delete a
				a = na
			Wend
		
			; dec parents child count
			If node\parent <> Null
				node\parent\childcount = node\parent\childcount -1
				
				; heal linkages
				If node\prevnode <> Null Then node\prevnode\nextnode = node\nextnode
				If node\nextnode <> Null Then node\nextnode\prevnode = node\prevnode
				If node\parent\firstchild = node Then node\parent\firstchild = node\nextnode
				If node\parent\lastchild = node Then node\parent\lastchild = node\prevnode
			End If
			; delete this node		
			Delete node
		
		End Function
		
		; node functions
		Function XMLfindNode.XMLnode(node.XMLnode,path$)
		
			ret.XMLnode = Null
			p=Instr(path$,"/")
			If p > 0 Then 
				tag$=Left$(path$,p-1)
				a.XMLnode = node
				While ret=Null And a<>Null 
					If Lower(tag$)=Lower(a\tag$) Then
						If p=Len(path$) Then
								ret = a
						Else
							If a\firstchild <> Null Then
								ret = XMLfindnode(a\firstchild,Mid$(path$,p+1))
							End If
						End If
					End If
					a = a\nextnode
				Wend
			End If
			Return ret
		End Function
		
		Function XMLDeleteList(nl.XMLnodelist)
			While nl <> Null
				na.XMLnodelist = nl\nextnode
				Delete nl
				nl = na
			Wend
		End Function
		
		
		Function XMLSelectNodes.XMLnodelist(node.XMLnode,path$,recurse=True)
			root.XMLnodelist=Null
			XMLselectnodesi(node,path$,recurse)
			prev.XMLnodelist=Null
			c = 0
			For wl.XMLworklist = Each XMLworklist
				c = c + 1
				nl.XMLnodelist = New XMLnodelist
				nl\node = wl\node
				If prev = Null Then 
					root = nl
					prev = nl
				Else
					prev\nextnode = nl
					nl\prevnode = prev
				End If
				prev = nl
				Delete wl
			Next
			;gak debuglog "XML: "+c+" nodes selected"
			Return root
		End Function
		
		; internal selection function, do not use outside this file
		Function XMLSelectNodesI(node.XMLnode,path$,recurse=True)
			wl.XMLworklist=Null
			If node = Null Then
			End If
			ret.XMLnode = Null
			p=Instr(path$,"/")
			If p > 0 Then 
				tag$=Left$(path$,p-1)
				a.XMLnode = node
				While a<>Null 
					If Lower(path$)=Lower(Right$(a\path$+a\tag$+"/",Len(path$))) Then
							wl = New XMLworklist
							wl\node = a
					End If
					If a\firstchild <> Null And (recurse) Then
						XMLSelectNodesI(a\firstchild,path$)
					End If
					a = a\nextnode
				Wend
			End If
		
		End Function
		
		Function XMLNextNode.XMLnode(node.XMLnode)
			Return node\nextnode
		End Function
		
		Function XMLPrevNode.XMLnode(node.XMLnode)
			Return node\prevnode
		End Function
		
		Function XMLAddAttr(node.XMLnode,name$,value$)
			;gak debuglog "XML:adding attribute "+name$+"="+value$+" ("+Len(value$)+")"
			a.XMLattr = New XMLattr
			a\name$ = name$
			a\value$ = value$
			If node\attrcount = 0 Then
				node\firstattr = a
			Else
				node\lastattr\sibattr = a
			End If
			node\lastattr=a
			node\attrcount = node\attrcount + 1
			a\parent = node
			If Upper(a\value)="TRUE" a\value=1
			If Upper(a\Value)="FALSE" a\value=0
			If Upper(a\Value)="GRAPHICSWIDTH" a\value=GraphicsWidth()
			If Upper(a\Value)="GRAPHICSHEIGHT" a\value=GraphicsHeight()
		End Function
		
		Function XMLReadNode.XMLnode(infile,parent.XMLnode,pushed=False)
			mode = 0
			root.XMLnode = Null
			cnode.XMLnode = Null
			x.XMLnode = Null
			ispushed = False
			done = False
			While (Not done) And (Not Eof(infile))
				c = ReadByte(infile)
				If c<32 Then c=32
				ch$=Chr$(c)
		;		;gak debuglog "{"+ch$+"} "+c+" mode="+mode
				Select mode
				  Case 0 ; looking for the start of a tag, ignore everything else
					If ch$ = "<" Then 
						mode = 1; start collecting the tag
					End If
				  Case 1 ; check first byte of tag, ? special tag
					If ch$ = "?" Or ch$ = "!" Then
				 		mode = 0; class special nodes as garbage & consume
					Else
						If ch$ = "/" Then 
							mode = 2 ; move to collecting end tag
							x\endtag$=ch$
							;gak debuglog "** found end tag"
						Else
							cnode=x
							x.XMLnode = XMLOpennode(cnode)
							If cnode=Null Then root=x
							x\tag$=ch$
							mode = 3 ; move to collecting start tag
						End If
					End If
				  Case 2 ; collect the tag name (close tag)
					If ch$=">" Then 
						mode = 0 ; end of the close tag so jump out of loop
						;done = True
						x = XMLclosenode(x)
					Else 
						x\endtag$ = x\endtag$ + ch$
					End If
				  Case 3 ; collect the tag name 
					If ch$=" " Then 
						;gak debuglog "TAG:"+x\tag$
						mode = 4 ; tag name collected, move to collecting attributes
					Else 
						If ch$="/" Then 
							;gak debuglog "TAG:"+x\tag$
							x\endtag$=x\tag$
							mode = 2; start/end tag combined, move to close
						Else
							If ch$=">" Then
								;gak debuglog "TAG:"+x\tag$
								mode = 20; tag closed, move to collecting value
							Else
								x\tag$ = x\tag$ + ch$
							End If
						End If
					End If
				  Case 4 ; start to collect attributes
					If Lower(ch$)>="a" And Lower(ch$)<="z" Then 
						aname$=ch$;
						mode = 5; move to collect attribute name
					Else
						If ch$=">" Then
							x\value$=""
							mode = 20; tag closed, move to collecting value
						Else
							If ch$="/" Then 
								mode = 2 ; move to collecting end tag
								x\endtag$=ch$
								;gak debuglog "** found end tag"
							End If
						End If
					End If
				  Case 5 ; collect attribute name
					If ch$="=" Then
					  ;gak debuglog "ATT:"+aname$
					  aval$=""
					  mode = 6; move to collect attribute value
					Else
					  aname$=aname$+ch$
					End If
				  Case 6 ; collect attribute value
					If c=34 Then
						mode = 7; move to collect string value
					Else
						If c <= 32 Then 
							;gak debuglog "ATV:"+aname$+"="+aval$
							XMLAddAttr(x,aname$,aval$)
							mode = 4; start collecting a new attribute
						Else
					   		aval$=aval$+ch$
						End If
					End If
				  Case 7 ; collect string value
					If c=34 Then
						;gak debuglog "ATV:"+aname$+"="+aval$
						XMLADDattr(x,aname$,aval$)
						mode = 4; go and collect next attribute
					Else
						aval$=aval$+ch$
					End If
				  Case 20 ; COLLECT THE VALUE PORTION
					If ch$="<" Then 
						;gak debuglog "VAL:"+x\tag$+"="+x\value$
						mode=1; go to tag checking
					Else
						x\value$=x\value$+ch$
					End If
				End Select
				
				If Eof(infile) Then done=True
			
			Wend
		
			Return root
		
		End Function
		
		; write out an XML node (and children)
		Function XMLWriteNode(outfile,node.XMLnode,tab$="")
		;	;gak debuglog "Writing...."+node\tag$+".."
			s$="<"+node\tag$
			a.XMLattr = node\firstattr
			While a<>Null
				s$ = s$+" "+Lower(a\name$)+"="+Chr$(34)+a\value$+Chr$(34)
				a = a\sibattr
			Wend
			
			If node\value$="" And node\childcount = 0 Then
				s$=s$+"/>"
				et$=""
			Else
				s$=s$+">"+node\value$
				et$="</"+node\tag$+">"
			End If
			
			WriteLine outfile,XMLcleanStr$(tab$+s$)
			n.XMLnode = node\firstchild
			While n <> Null
				XMLwriteNode(outfile,n,tab$+"  ")
				n = n\nextnode
			Wend
			
			If et$<> "" Then WriteLine outfile,XMLCleanStr$(tab$+et$)
		
		End Function
		
		; remove non-visible chars from the output stream
		Function XMLCleanStr$(s$)
			a$=""
			For i = 1 To Len(s$)
				If Asc(Mid$(s$,i,1))>=32 Then a$ = a$ +Mid$(s$,i,1)
			Next
			Return a$
		
		End Function
		
		; attribute functions
		; return an attribute of a given name
		Function XMLFindAttr.XMLattr(node.XMLnode,name$)
			ret.XMLattr = Null
			If node <> Null Then 
				a.XMLattr = node\firstattr
				done = False
				While ret=Null And a<>Null 
					If Lower(name$)=Lower(a\name$) Then
						ret = a
					End If
					a = a\sibattr
				Wend
			End If
			Return ret
		End Function
		
		; return an attribute value as a string
		Function XMLAttrValueStr$(node.XMLnode,name$,dflt$="")
			ret$=dflt$
			a.XMLattr = XMLfindattr(node,name$)
			If a <> Null Then ret$=a\value$
			Return ret$
		End Function
		
		; return an attribute value as an integer
		Function XMLAttrValueInt(node.XMLnode,name$,dflt=0)
			ret=dflt
			a.XMLattr = XMLfindattr(node,name$)
			If a <> Null Then ret=a\value
			Return ret
		End Function
		
		; return an attribute value as a float
		Function XMLAttrValueFloat#(node.XMLnode,name$,dflt#=0)
			ret#=dflt#
			a.XMLattr = XMLfindattr(node,name$)
			If a <> Null Then ret#=a\value
			Return ret
		End Function
		
		Function XMLHasChildren(node.XMLnode)
			Return node\firstchild <> Null
		End Function
		
		Function XMLHasAttributes(node.XMLnode)
			Return node\firstattr <> Null
		End Function
		
		Function XMLGetChild.XMLNode(node.XMLNode,index=0)
			child.XMLNode = node\FirstChild
			For i = 0 To index-1
				child.XMLNode = child\nextnode
			Next
			Return child
		End Function
		
		Function XMLGetFirstAttribute.XMLAttr(node.XMLNode)
			Return node\firstattr
		End Function
		
		Function XMLGetNextAttribute.XMLAttr(attr.XMLAttr)
			Return attr\sibattr
		End Function
		
		Function XMLHasAttribute(n.XMLNode,attr$)
			If XMLHasAttributes(n) = 0 Then Return 0
			
			a.XMLAttr = XMLGetFirstAttribute(n)
			While (a <> Null)
				If Lower(a\name) = Lower(attr) Return 1
				a = XMLGetNextAttribute(a)
			Wend
			Return 0
		End Function
		
		Function XMLGetParent.XMLNode(node.XMLNode)
			Return node\parent
		End Function
		
		Function PrintXMLNode(i.XMLNode,start$="")
			If i = Null Then Return
			Write start+"<"+i\tag
			a.XMLAttr = XMLGetFirstAttribute(i)
			While a <> Null
				Write " "+a\name+"="+Chr(34)+a\value+Chr(34)
				a = XMLGetNextAttribute(a)
			Wend
			Write ">"
			Print ""
			
			f.XMLNode = XMLGetChild(i,0)
			While f.XMLNode <> Null
				PrintXMLNode(f,start+"    ")
				f = XMLNextNode(f)
			Wend
			Print start+"</"+i\tag+">"
		End Function
	;#End Region
;#End Region

;; DE/INITIALIZATION
Function InitLotus()
	Dim gSine#(359)
	Dim gCosine#(359)
	
	For A = 0 To 359
		gSine#(A) = Sin(A)
		gCosine#(A) = Cos(A)
	Next
	
	gLotusParticlePivot = CreatePivot()
	gLotusTPivot = CreatePivot()
	gLotusParticleMesh = CreateMesh()
	EntityFX gLotusParticleMesh,1+2+8+16+32
	
	gLotusNullTexture.LotusTexture = New LotusTexture
	gNullTexture = Handle(gLotusNullTexture)
	gLotusNullTexture\Rows = 1
	gLotusNullTexture\Columns = 1
	gLotusNullTexture\UStep = 1
	gLotusNullTexture\VStep = 1
	
	Brush = CreateBrush()
	BrushFX Brush,1+2+16+32
	
	BrushBlend Brush,1
	gLotusNullTexture\Alpha = CreateSurface(gLotusParticleMesh)
	PaintSurface gLotusNullTexture\Alpha,Brush
	
	FreeBrush Brush
	
	gNullLotusVector = New LotusVector
	gTempLotusVector = New LotusVector
	
	If cUSE_EMITTERCONES Then
		gLotusEmitterTexture = CreateTexture(64,64,1+16+32)
		Buffer = GraphicsBuffer()
		T = gLotusEmitterTexture
		SetBuffer TextureBuffer(T)
		R = ColorRed()
		G = ColorGreen()
		B = ColorBlue()
		Color 255,255,255
		Rect 0,0,64,64,True
		Color 255,120,40
		Rect 0,60,64,4
		Color R,G,B
		SetBuffer Buffer
	EndIf
	
	gLotusParticleCount = 0
	
	gWidth = GraphicsWidth()
	gHeight = GraphicsHeight()
End Function

Function KillLotus()
	Dim gSine#(0)
	Dim gCosine#(0)
	FreeEntity gLotusParticlePivot
	FreeEntity gLotusTPivot
	FreeEntity gLotusParticleMesh
	If cUSE_EMITTERCONES Then FreeTexture gLotusEmitterTexture
	gLotusParticlePivot = 0
	gLotusParticleMesh = 0
	gLotusEmitterTexture = 0
	
	For T.LotusTexture = Each LotusTexture
		FreeTexture T\Bitmap
	Next
	
	For E.LotusEmitter = Each LotusEmitter
		FreeEntity E\Entity
	Next
	
	For G.LotusGraph = Each LotusGraph
		FreeBank G\Bank
	Next
	
	For D.LotusDeflector = Each LotusDeflector
		FreeEntity D\Entity
	Next
	
	Delete Each LotusParticle
	Delete Each LotusEmitter
	Delete Each LotusGraph
	Delete Each LotusDeflector
	Delete Each LotusVector
	Delete Each LotusTexture
	Delete Each LotusStringPiece
	gLotusParticleCount = 0
End Function


;; TEXTURE
Function LoadLotusTexture(Path$,Flags%=59,Alpha=0,Multiply=0,Add=0)
	For T.LotusTexture = Each LotusTexture
		If Lower(T\Path) = Lower(Path) Then Return Handle(T)
	Next
	Bitmap = LoadTexture(Path$,Flags)
	If Bitmap = 0 Then Return False
	T.LotusTexture = New LotusTexture
	T\Bitmap = Bitmap
	T\Flags = Flags
	T\Rows = 1
	T\Columns = 1
	T\UStep = 1
	T\VStep = 1
	T\Path = Path
	SetTextureBlendModes Handle(T),Alpha,Multiply,Add
	Return Handle(T)
End Function

Function LoadLotusAnimTexture(Path$,Flags%=59,Rows%=1,Columns%=1,Alpha=0,Multiply=0,Add=0)
	For T.LotusTexture = Each LotusTexture
		If Lower(T\Path) = Lower(Path) Then Return Handle(T)
	Next
	Texture = LoadLotusTexture(Path$,Flags,Alpha,Multiply,Add)
	T.LotusTexture = Object.LotusTexture(Texture)
	If T = Null Or T = gLotusNullTexture Then Return False
	Rows = Min(Rows,1):Columns = Min(Columns,1)
	T\Rows = Rows
	T\Columns = Columns
	FrameWidth# = TextureWidth(T\Bitmap)/Columns
	FrameHeight# = TextureHeight(T\Bitmap)/Rows
	T\UStep = FrameWidth/TextureWidth(T\Bitmap)
	T\VStep = FrameHeight/TextureHeight(T\Bitmap)
	Return Texture
End Function

Function LoadLotusTileSet(Path$,Flags%=59,Rows%=3,Columns%=3,Alpha=0,Multiply=0,Add=0)
	For T.LotusTexture = Each LotusTexture
		If Lower(T\Path) = Lower(Path) Then Return Handle(T)
	Next
	Texture = LoadLotusTexture(Path$,Flags,Alpha,Multiply,Add)
	T.LotusTexture = Object.LotusTexture(Texture)
	If T = Null Or T = gLotusNullTexture Then Return False
	Rows = Min(Rows,1):Columns = Min(Columns,1)
	T\Rows = Rows
	T\Columns = Columns
	FrameWidth# = TextureWidth(T\Bitmap)/Columns
	FrameHeight# = TextureHeight(T\Bitmap)/Rows
	T\UStep = FrameWidth/TextureWidth(T\Bitmap)
	T\VStep = FrameHeight/TextureHeight(T\Bitmap)
	Return Texture
End Function

Function ApplyTexture(Emitter,Texture,Frame=-1,Animated%=-1,FrameLength%=-1)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\Texture = Object.LotusTexture(Texture)
	If Frame > -1 Then E\Frame = Frame E\FrameStart = Frame
	If Animated > -1 Then E\Animated = Animated
	If FrameLength > -1 Then E\FrameLength = FrameLength
End Function

Function SetTextureTiles(Texture,Rows,Columns)
	T.LotusTexture = Object.LotusTexture(Texture)
	If T = Null Or T = gLotusNullTexture Then Return False
	Rows = Min(Rows,1):Columns = Min(Columns,1)
	T\Rows = Rows
	T\Columns = Columns
	FrameWidth# = TextureWidth(T\Bitmap)/Columns
	FrameHeight# = TextureHeight(T\Bitmap)/Rows
	T\UStep = FrameWidth/TextureWidth(T\Bitmap)
	T\VStep = FrameHeight/TextureHeight(T\Bitmap)
	Return True
End Function

Function SetTextureBlendModes(Texture,Alpha=True,Multiply=False,Add=True,RebuildSurfaces = 0)
	T.LotusTexture = Object.LotusTexture(Texture)
	If T = Null Then Return False
	Brush = CreateBrush()
	BrushTexture Brush,T\Bitmap
	BrushFX Brush,1+2+16+32
	
	If Add > 0 And (T\Add = 0 Or RebuildSurfaces > 0)
		T\Add = CreateSurface(gLotusParticleMesh)
		BrushBlend Brush,3
		PaintSurface T\Add,Brush
	ElseIf Add = 0 And T\Add <> 0 Then
		T\Add = 0
		Rebuild = True
	EndIf
	
	If Alpha > 0 And (T\Alpha = 0 Or RebuildSurfaces > 0)
		T\Alpha = CreateSurface(gLotusParticleMesh)
		BrushBlend Brush,1
		PaintSurface T\Alpha,Brush
	ElseIf Alpha = 0 And T\Alpha <> 0 Then
		T\Alpha = 0
		Rebuild = True
	EndIf
	
	If Multiply > 0 And (T\Multiply = 0 Or RebuildSurfaces > 0)
		T\Multiply = CreateSurface(gLotusParticleMesh)
		BrushBlend Brush,2
		PaintSurface T\Multiply,Brush
	ElseIf Multiply = 0 And T\Multiply <> 0 Then
		T\Multiply = 0
		Rebuild = True
	EndIf
	
	If Rebuild Then RebuildLotusTextures()
	
	FreeBrush Brush
End Function

Function FreeLotusTexture(Texture)
	T.LotusTexture = Object.LotusTexture(Texture)
	If T = Null Then Return False
	FreeTexture T\Bitmap
	Delete T
	RebuildLotusTextures()
End Function

Function ClearLotusTextures()
	For T.LotusTexture = Each LotusTexture
		If T <> gLotusNullTexture Then
			FreeTexture T\Bitmap
			Delete T
		EndIf
	Next
	
	RebuildLotusTextures()
End Function

Function RebuildLotusTextures()
	FreeEntity gLotusParticleMesh
	gLotusParticleMesh = CreateMesh()
	EntityFX gLotusParticleMesh,1+2+16+32
	
	Brush = CreateBrush()
	BrushFX Brush,1+2+16+32
	
	BrushBlend Brush,1
	gLotusNullTexture\Alpha = CreateSurface(gLotusParticleMesh)
	PaintSurface gLotusNullTexture\Alpha,Brush
	
	FreeBrush Brush
	
	For T.LotusTexture = Each LotusTexture
		If T <> gLotusNullTexture Then SetTextureBlendModes Handle(T),Abs(T\Alpha),Abs(T\Multiply),Abs(T\Add),1
	Next
End Function

Function TextureUsesAdd(Texture)
	T.LotusTexture = Object.LotusTexture(Texture)
	If T = Null Then Return False
	If T = gLotusNullTexture Then Return True
	If T\Add <> 0 Then Return True
End Function

Function TextureUsesMultiply(Texture)
	T.LotusTexture = Object.LotusTexture(Texture)
	If T = Null Then Return False
	If T = gLotusNullTexture Then Return True
	If T\Alpha <> 0 Then Return True
End Function

Function TextureUsesAlpha(Texture)
	T.LotusTexture = Object.LotusTexture(Texture)
	If T = Null Then Return False
	If T = gLotusNullTexture Then Return True
	If T\Alpha <> 0 Then Return True
End Function
	
;; DEFLECTOR
Function DeflectorExists(Deflector)
	For D.LotusDeflector = Each LotusDeflector
		If D\Entity = Deflector Then
			Return D\Entity
		EndIf
	Next
	Return 0
End Function

Function CreateDeflector(Parent=0)
	D.LotusDeflector = New LotusDeflector
	D\Radius = 0
	D\Strength = .5
	D\Active = 0
	D\Entity = CreatePivot()
	NameEntity D\Entity,Handle(D)
	Return D\Entity
End Function

Function SetDeflectorStrength(Deflector, Strength#=.5)
	If DeflectorExists(Deflector) = 0 Then Return False
	D.LotusDeflector = Object.LotusDeflector(EntityName(Deflector))
	If D = Null Then Return False
	D\Strength = Strength
End Function

Function SetDeflectorActive(Deflector,Active = 1)
	If DeflectorExists(Deflector) = 0 Then Return False
	D.LotusDeflector = Object.LotusDeflector(EntityName(Deflector))
	If D = Null Then Return False
	D\Active = Active
End Function

Function SetDeflectorRadius(Deflector, Radius#=0)
	If DeflectorExists(Deflector) = 0 Then Return False
	D.LotusDeflector = Object.LotusDeflector(EntityName(Deflector))
	If D = Null Then Return False
	D\Radius = Radius
End Function

Function ActivateDeflector(Deflector)
	If DeflectorExists(Deflector) = 0 Then Return False
	D.LotusDeflector = Object.LotusDeflector(EntityName(Deflector))
	If D = Null Then Return False
	D\Active = True
End Function

Function DeActivateDeflector(Deflector)
	If DeflectorExists(Deflector) = 0 Then Return False
	D.LotusDeflector = Object.LotusDeflector(EntityName(Deflector))
	If D = Null Then Return False
	D\Active = False
End Function
	


;; EMITTER
Function NameEmitter(Emitter,Name$)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\Name = Name$
End Function

Function GetEmitterName$(Emitter)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	Return E\Name
End Function

Function CreateEmitter%(Texture = 0)
	If Texture = 0 Then Texture = Handle(gLotusNullTexture)
	If cUSE_EMITTERCONES Then 
		P = CreateCone(4)
		For S = 1 To CountSurfaces(P)
			Surface = GetSurface(P,S)
			For V = 0 To CountVertices(Surface)-1
				eV# = 1.0-VertexY(Surface,V)/2
				eU# = VertexX(Surface,V)/2
				If eV > 1 Then eV = eV - .5
				If eV < 0 Then eV = eV + .5
				If eV > 1 Then Stop
				VertexTexCoords Surface,V,eU,eV,0,0
			Next
		Next
		Surface = 0
		EntityTexture P,gLotusEmitterTexture
		EntityFX P,1
		RotateMesh P,-90,0,0
		PositionMesh P,0,0,-2
	Else
		P = CreatePivot()
	EndIf
	
	HideEntity P
	E.LotusEmitter = New LotusEmitter
	NameEntity P,Handle(E)
	
	E\Weight = 1
	E\Texture = Object.LotusTexture(Texture)
	E\Entity = P
	E\WaitSpan = 1
	E\DWaitSpan = 1
	E\CircleSpeedX = 1
	E\CircleSpeedY = 1
	E\CircleSpeedZ = 1
	E\SizeFromX = 1
	E\SizeFromY = 1
	E\SizeFromZ = 1
	E\SizeToX = 1
	E\SizeToY = 1
	E\SizeToZ = 1
	E\WaveSpeedX = 1
	E\WaveSpeedY = 1
	E\WaveSpeedZ = 1
	E\RedFrom = 255
	E\BlueFrom = 255
	E\GreenFrom = 255
	E\RedTo = 255
	E\BlueTo = 255
	E\GreenTo = 255
	E\AlphaFrom = 1.0
	E\BlendMode = 1
	E\LifeSpan = 80
	E\EmissionRate = 1
	E\Cull = 1
	E\Sorting = 1
	E\ViewMode = 1
	E\LifeMode = 1
	E\ChildMode = 1
	E\ActiveSpan = -1
	E\Active = -1
	Return P
End Function

Function LoadEmitter%(Path$,Flags%=59,Alpha=1,Multiply=0,Add=0)
	For T.LotusTexture = Each LotusTexture
		If Lower(T\Path$) = Lower(Path$) Then Exit
	Next
	If T <> Null Then
		Return CreateEmitter(Handle(T))
	EndIf
	
	Texture = LoadLotusTexture(Path$,Flags,Alpha,Multiply,Add)
	Return CreateEmitter(Texture)
End Function

Function KillLotusEmitter(Emitter)	;; Recommendation: When calling this or FreeEmitter(E), call it like so: YourEmitter = FreeEmitter(YourEmitter) so that the integer, YourEmitter, gets set to zero
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
		
	If E\EmitSoundChannel Then StopChannel( E\EmitSoundChannel )
	Delete E
	FreeEntity Emitter
	Return 0
End Function

Function FreeEmitter(Emitter)
	Return KillLotusEmitter( Emitter )
End Function

Function ClearEmitters()		;; Reset all references to emitters to 0 after calling this
	For E.LotusEmitter = Each LotusEmitter
		KillLotusEmitter E\Entity
	Next
End Function

Function EmitterExists(Emitter)
	For E.LotusEmitter = Each LotusEmitter
		If E\Entity = Emitter Then Return E\Entity
	Next
	Return 0
End Function

Function CopyEmitter(Emitter)
	If EmitterExists( Emitter ) = 0 Then Return False
	EOld.LotusEmitter = Object.LotusEmitteR(Int(EntityName$(Emitter)))
	If EOld = Null Then Return False
	If cUSE_EMITTERCONES Then
		P = CreateCone(4)
		For S = 1 To CountSurfaces(P)
			Surface = GetSurface(P,S)
			For V = 0 To CountVertices(Surface)-1
				eV# = 1.0-VertexY(Surface,V)/2
				eU# = VertexX(Surface,V)/2
				If eV > 1 Then eV = eV - .5
				If eV < 0 Then eV = eV + .5
				If eV > 1 Then Stop
				VertexTexCoords Surface,V,eU,eV,0,0
			Next
		Next
		Surface = 0
		EntityTexture P,gLotusEmitterTexture
		EntityFX P,1
		RotateMesh P,-90,0,0
		PositionMesh P,0,0,-2
	Else
		P = CreatePivot()
	EndIf
	HideEntity P
	E.LotusEmitter = New LotusEmitter
	NameEntity P,Handle(E)
	PositionEntity P,EntityX(Emitter,True),EntityY(Emitter,True),EntityZ(Emitter,True)
	RotateEntity P,EntityPitch(Emitter,True),EntityYaw(Emitter,True),EntityRoll(Emitter,True)
	E\Texture = EOld\Texture
	E\Frame = EOld\Frame
	E\Animated = EOld\Animated
	E\Entity = P
	E\TranslationJitterX = EOld\TranslationJitterX
	E\TranslationJitterY = EOld\TranslationJitterY
	E\TranslationJitterZ = EOld\TranslationJitterZ
	E\TranslationX = EOld\TranslationX
	E\TranslationY = EOld\TranslationY
	E\TranslationZ = EOld\TranslationZ
	E\VelocityX = EOld\VelocityX
	E\VelocityY = EOld\VelocityY
	E\VelocityZ = EOld\VelocityZ
	E\AngleVelocityX = EOld\AngleVelocityX
	E\AngleVelocityY = EOld\AngleVelocityY
	E\AngleVelocityZ = EOld\AngleVelocityZ
	E\AccelerationX = EOld\AccelerationX
	E\AccelerationY = EOld\AccelerationY
	E\AccelerationZ = EOld\AccelerationZ
	E\AngleAccelerationX = EOld\AngleAccelerationX
	E\AngleAccelerationY = EOld\AngleAccelerationY
	E\AngleAccelerationZ = EOld\AngleAccelerationZ
	E\SizeFromX = EOld\SizeFromX
	E\SizeFromY = EOld\SizeFromY
	E\SizeFromZ = EOld\SizeFromZ
	E\SizeToX = EOld\SizeToX
	E\SizeToY = EOld\SizeToY
	E\SizeToZ = EOld\SizeToZ
	E\SizeJitterX = EOld\SizeJitterX
	E\SizeJitterY = EOld\SizeJitterY
	E\SizeJitterZ = EOld\SizeJitterZ
	E\VelocityJitterX = EOld\VelocityJitterX
	E\VelocityJitterY = EOld\VelocityJitterY
	E\VelocityJitterZ = EOld\VelocityJitterZ
	E\CubeX = EOld\CubeX
	E\CubeY = EOld\CubeY
	E\CubeZ = EOld\CubeZ
	E\CylinderX = EOld\CylinderX
	E\CylinderY = EOld\CylinderY
	E\CylinderZ = EOld\CylinderZ
	E\WaveRadiusX = EOld\WaveRadiusX
	E\WaveRadiusY = EOld\WaveRadiusY
	E\WaveRadiusZ = EOld\WaveRadiusZ
	E\WaveSpeedX = EOld\WaveSpeedX
	E\WaveSpeedY = EOld\WaveSpeedY
	E\WaveSpeedZ = EOld\WaveSpeedZ
	E\CircleSpeedX = EOld\CircleSpeedX
	E\CircleSpeedY = EOld\CircleSpeedY
	E\CircleSpeedZ = EOld\CircleSpeedZ
	E\CircleRadiusX = EOld\CircleRadiusX
	E\CircleRadiusY = EOld\CircleRadiusY
	E\CircleRadiusZ = EOld\CircleRadiusZ
	E\RandomRotationX = EOld\RandomRotationX
	E\RandomRotationY = EOld\RandomRotationY
	E\RandomRotationZ = EOld\RandomRotationZ
	E\TranslationJitterUp = EOld\TranslationJitterUp
	E\TranslationJitterDown = EOld\TranslationJitterDown
	E\VelocityJitterUp = EOld\VelocityJitterUp
	E\VelocityJitterDown = EOld\VelocityJitterDown
	E\RedFrom = EOld\RedFrom
	E\GreenFrom = EOld\GreenFrom
	E\BlueFrom = EOld\BlueFrom
	E\AlphaFrom = EOld\AlphaFrom
	E\RedTo = EOld\RedTo
	E\GreenTo = EOld\GreenTo
	E\BlueTo = EOld\BlueTo
	E\AlphaTo = EOld\AlphaTo
	E\BlendMode = EOld\BlendMode
	E\LifeSpan = EOld\LifeSpan
	E\WaitSpan = EOld\WaitSpan
	E\DWaitSpan = EOld\DWaitSpan
	E\WaitSpanJitter = EOld\WaitSpanJitter
	E\ParticleMesh = EOld\ParticleMesh
	E\Emit = EOld\Emit
	E\SizeJitterUniform = EOld\SizeJitterUniform
	E\ColorJitterRed = EOld\ColorJitterRed
	E\ColorJitterGreen = EOld\ColorJitterGreen
	E\ColorJitterBlue = EOld\ColorJitterBlue
	E\ColorJitterAlpha = EOld\ColorJitterAlpha
	E\ColorJitterUniform = EOld\ColorJitterUniform
	E\ColorJitterDown = EOld\ColorJitterDown
	E\ColorJitterUp = EOld\ColorJitterUp
	E\Frozen = EOld\Frozen
	E\Hidden = EOld\Hidden
	E\EmissionRate = EOld\EmissionRate
	E\ViewMode = EOld\ViewMode
	E\LifeMode = EOld\LifeMode
	E\ChildMode = EOld\ChildMode
	E\Gravity = EOld\Gravity
	E\GravityEnabled = EOld\GravityEnabled
	E\Weight = EOld\Weight
	E\SizeJitterUp = EOld\SizeJitterUp
	E\SizeJitterDown = EOld\SizeJitterDown
	E\Cull = EOld\Cull
	E\Sorting = EOld\Sorting
	E\RollMode = EOld\RollMode
	E\Range = EOld\Range
	E\LifeSpanJitter = EOld\LifeSpanJitter
	E\LifeSpanJitterUp = EOld\LifeSpanJitterUp
	E\LifeSpanJitterDown = EOld\LifeSpanJitterDown
	E\ColorGraph = EOld\ColorGraph
	E\SizeGraph = EOld\SizeGraph
	E\EmitSound = EOld\EmitSound
	E\BounceSound = EOld\BounceSound
	E\LoopEmitSound = EOld\LoopEmitSound
	E\VelocityDecay = EOld\VelocityDecay
	E\SplineMesh = EOld\SplineMesh
	E\ActiveSpan = EOld\ActiveSpan
	E\Active = EOld\Active
	E\FreeOnEndActive = EOld\FreeOnEndActive
	E\DeflectorsAffect = EOld\DeflectorsAffect
	E\Parent = EOld\Parent
	E\Child = EOld\Child
	E\Trail = EOld\Trail
	EntityParent E\Entity,GetParent(EOld\Entity)
	Return P
End Function

Function SortParticles()
	Dim gAlphaPartArray#( Min(gLotusAlphaParticleCount-1,0), 1 )
	For P.LotusParticle = Each LotusParticle
		If P\BlendMode = 1 And P\Sorting >= 1 Then
			PositionEntity gLotusParticlePivot, P\PositionX, P\PositionY, P\PositionZ, 1
			gAlphaPartArray( ACount, 0 ) = -EntityDistance( gLotusCamera, gLotusParticlePivot )
			gAlphaPartArray( ACount, 1 ) = Handle( P )
			ACount = ACount + 1
		EndIf
		If ACount >= gLotusAlphaParticleCount Then Exit
	Next
	
	If gLotusAlphaParticleCount > 1 Then
		lSort( 0, gLotusAlphaParticleCount-1 )
	EndIf
End Function

;; UPDATE
Function UpdateLotusParticles(DeltaTime#=1,Camera=0)

	gLotusParticlesDrawn = 0
	Local ET% = MilliSecs()
;	gLotusUpdateTimes( uptTimeAll ) = ET
	
;	gLotusUpdateTimes( updTimeTexture ) = MilliSecs()
	For T.LotusTexture = Each LotusTexture
		If T\Add <> 0 Then ClearSurface T\Add,1,1
		If T\Multiply <> 0 Then ClearSurface T\Multiply,1,1
		If T\Alpha <> 0 Then ClearSurface T\Alpha,1,1
	Next
;	gLotusUpdateTimes( updTimeTexture ) = MilliSecs() - gLotusUpdateTimes( updTimeTexture )
	
	If Camera = 0 Then
		Camera = gLotusCamera
		If Camera = 0 Then Return False
	Else
		gLotusCamera = Camera
	EndIf
	
	If DeltaTime > 0 Then
	;	gLotusUpdateTimes( updTimeEmit ) = MilliSecs()
		For E.LotusEmitter = Each LotusEmitter
			KillIt = False
			
			E\PositionX = EntityX(E\Entity,1)
			E\PositionY = EntityY(E\Entity,1)
			E\PositionZ = EntityZ(E\Entity,1)
			
			E\AngleX = EntityPitch(E\Entity,1)
			E\AngleY = EntityYaw(E\Entity,1)
			E\AngleZ = EntityRoll(E\Entity,1)
			
			If E\Frozen = False
				If E\Emit And E\WaitA = 0 Then
					CreateParticle E\Entity
				EndIf
				E\WaitA = E\WaitA + DeltaTime
				If E\WaitA >= E\WaitSpan Then
					E\WaitA = 0
					E\WaitSpan = E\DWaitSpan+Rand(-E\WaitSpanJitter,E\WaitSpanJitter)
				EndIf
				If E\Active > -1 Then
					E\Active = E\Active - 1
					If E\Active = 0 Then
						E\Active = -1
						StopEmitter( E\Entity )
						If E\FreeOnEndActive >= 1 Then KillIt = True
					EndIf
				EndIf
				If E\EmitSound <> 0 And E\EmitSoundChannel <> 0 And E\LoopEmitSound > 0 And E\Emit = 1 Then
					If ChannelPlaying( E\EmitSoundChannel ) <= 0 Then E\EmitSoundChannel = PlaySound( E\EmitSound )
				ElseIf E\EmitSound <> 0 And E\EmitsoundChannel = 0 And E\Emit = 1 Then
					E\EmitSoundChannel = PlaySound( E\EmitSound )
				ElseIf E\EmitSoundChannel <> 0 And E\Emit = 0 And E\LoopEmitSound = 1 Then
					StopChannel( E\EmitSoundChannel )
				EndIf
				
				If E\EmitSoundChannel <> 0 And E\EmitSoundRange > 0 Then
					Vol# = EntityDistance( E\Entity, gLotusCamera ) / E\EmitSoundRange
					If Vol# > 1 Then Vol = 1
					If Vol# < 0 Then Vol = 0
					Vol# = 1.0 - Vol
					ChannelVolume( E\EmitSoundChannel, Vol# )
				EndIf
			EndIf
			
			E\WaitB = E\WaitB + DeltaTime
			E\WaitC = E\WaitC + DeltaTime
			If E\WaitB > E\WaitSpan Then E\WaitB = 0
			If E\WaitC > E\WaitSpan Then E\WaitC = 0
				
			If KillIt = True Then KillLotusEmitter( E\Entity )
		Next
	;	gLotusUpdateTimes( updTimeEmit ) = MilliSecs() - gLotusUpdateTimes( updTimeEmit )
		
		For LD.LotusDeflector = Each LotusDeflector
			If LD\Active Then
				LD\PositionX = EntityX(LD\Entity,True)
				LD\PositionY = EntityY(LD\Entity,True)
				LD\PositionZ = EntityZ(LD\Entity,True)
			EndIf
		Next
		
	;	gLotusUpdateTimes( updTimePart ) = MilliSecs()
		For P.LotusParticle = Each LotusParticle
			If P\Life >= 0 Then
				
				N = 0
				Decay# = 1.0
				While N < DeltaTime*(Not P\Frozen)
					If P\Trail <> Null Then
						If P\Trail\WaitC <= 0 Then
							PositionEntity P\Trail\Entity,P\PositionX,P\PositionY,P\PositionZ,1
							RotateEntity P\Trail\Entity,P\AngleX,P\AngleY,0,1
							CreateParticle(P\Trail\Entity)
							RotateEntity P\Trail\Entity,P\Trail\AngleX,P\Trail\AngleY,P\Trail\AngleZ,1
							PositionEntity P\Trail\Entity,P\Trail\PositionX,P\Trail\PositionY,P\Trail\PositionZ,1
						EndIf
					EndIf
					D# = Max(DeltaTime-N,1)
					LFrom# = P\Life/P\LifeSpan
					LTo# = 1.0 - LFrom#
					
					P\LastPositionX = P\PositionX
					P\LastPositionY = P\PositionY
					P\LastPositionZ = P\PositionZ
					
					P\PositionX = P\PositionX + P\TranslationX*D
					P\PositionY = P\PositionY + P\TranslationY*D
					P\PositionZ = P\PositionZ + P\TranslationZ*D
					
					P\Gravity = (P\Gravity - (.1*P\Weight)*D)*P\GravityEnabled
					P\PositionY = P\PositionY + P\Gravity*D
					
					OPitch# = P\AngleX
					OYaw# = P\AngleY
					
					If P\SplineMesh = 0 Then
						P\AngleX = P\AngleX + (P\AngleVelocityX + P\AngleAccelerationX*LTo)*D
						P\AngleY = P\AngleY + (P\AngleVelocityY + P\AngleAccelerationY*LTo)*D
					Else
						Surface = GetSurface( P\SplineMesh, 1 )
						Vertices = CountVertices( Surface ) - 1
						Vertex = Int( Vertices * LTo )
						If Vertex <= 0 Then Vertex = Vertex + 1
						NX# = VertexX( Surface, Vertex - 1 ) - VertexX( Surface, Vertex )
						NY# = VertexY( Surface, Vertex - 1 ) - VertexY( Surface, Vertex )
						NZ# = VertexZ( Surface, Vertex - 1 ) - VertexZ( Surface, Vertex )
						RotateEntity P\SplineMesh,OPitch,OYaw,0
						TFormNormal -NX,-NY,-NZ,P\SplineMesh,0
						AlignToVector gLotusParticlePivot,TFormedX(),TFormedY(),TFormedZ(),3,1
						P\AngleX = EntityPitch( gLotusParticlePivot, 1 )
						P\AngleY = EntityYaw( gLotusParticlePivot, 1 )
					EndIf
					
					P\AngleZ = P\AngleZ + (P\AngleVelocityZ + P\AngleAccelerationZ*LTo)*D
					
					If P\AngleX <> OPitch Or P\AngleY <> OYaw Then
						RotateEntity gLotusParticlePivot,P\AngleX,P\AngleY,0
						TFormVector P\OVelocityX,P\OVelocityY,P\OVelocityZ,gLotusParticlePivot,0
						P\VelocityX = TFormedX()
						P\VelocityY = TFormedY()
						P\VelocityZ = TFormedZ()
					EndIf
					
					If P\SplineMesh <> 0 Then
						P\AngleX = OPitch
						P\AngleY = OYaw
					EndIf
					
					If P\VelocityDecay# <> 0 Then
						Decay# = (P\VelocityDecay*LFrom)
					EndIf
					
	;				MoveEntity gLotusParticlePivot,((P\VelocityX+(P\AccelerationX*LTo))*Decay)*D,((P\VelocityY+(P\AccelerationY*LTo))*Decay)*D,((P\VelocityZ+(P\AccelerationZ*LTo))*Decay)*D
					
					If P\DeflectorsAffect Then
						P\DeflectorSpeedX = P\DeflectorSpeedX * .98
						P\DeflectorSpeedY = P\DeflectorSpeedY * .98
						P\DeflectorSpeedZ = P\DeflectorSpeedZ * .98
						
						PositionEntity gLotusParticlePivot,P\PositionX,P\PositionY,P\PositionZ
						
						For LD.LotusDeflector = Each LotusDeflector
							If LD\Active = True And LD\Strength <> 0 And LD\Radius > 0 Then
								Distance# = EntityDistance(LD\Entity,gLotusParticlePivot)
								
								If Distance# < LD\Radius Then
									Magnitude# = (1.0 - (Distance# / LD\Radius))*LD\Strength
									If P\Weight <> 0 Then Magnitude# = Magnitude# * P\Weight
										
									DX# = P\PositionX - LD\PositionX
									DY# = P\PositionY - LD\PositionY
									DZ# = P\PositionZ - LD\PositionZ
									
									P\DeflectorSpeedX = P\DeflectorSpeedX + DX * Magnitude
									P\DeflectorSpeedY = P\DeflectorSpeedY + DY * Magnitude
									P\DeflectorSpeedZ = P\DeflectorSpeedZ + DZ * Magnitude
								EndIf
							EndIf
						Next
					EndIf
					
					P\PositionX = P\PositionX + ((P\VelocityX+(P\AccelerationX*LTo))*Decay)*D + P\DeflectorSpeedX*D
					P\PositionY = P\PositionY + ((P\VelocityY+(P\AccelerationY*LTo))*Decay)*D + P\DeflectorSpeedY*D
					P\PositionZ = P\PositionZ + ((P\VelocityZ+(P\AccelerationZ*LTo))*Decay)*D + P\DeflectorSpeedZ*D
					
					If P\MinY <> 0 Then
						If P\PositionY <= P\MinY And P\Bounce > 0 Then
							P\Bounces = P\Bounces + 1
							
							If P\BounceMax > 0 And P\Bounces > P\BounceMax Then
								KillIt = True
								Exit
							EndIf
							
							P\PositionY = P\MinY + .01
							
							P\VelocityX = P\VelocityX * P\BounceDecay
							P\VelocityY = P\VelocityY * P\BounceDecay
							P\VelocityZ = P\VelocityZ * P\BounceDecay
							
							P\Gravity = -P\Gravity*(P\BounceDecay*1.3)
							
							If P\BounceSound <> 0 Then P\BounceSoundChannel = PlaySound( P\BounceSound )
						ElseIf P\PositionY <= P\MinY And P\Bounce = 0 Then
							KillIt = True
							Exit
						EndIf
					EndIf
					
					Select P\LifeMode
						Case 1
							P\Life = P\Life - D#
						Case 2
							P\Life = P\LifeSpan - (ET - P\LifeBegan)
					End Select
					
					N = N + 1
				Wend
				
				If KillIt = True Then
					KillParticle P
					KillIt = False
				ElseIf P\BlendMode <> 1 Or P\Sorting = False
					DrawLotusParticle P,gLotusTPivot
				EndIf
			Else
				KillParticle P
			EndIf
		Next
	;	gLotusUpdateTimes( updTimePart ) = MilliSecs() - gLotusUpdateTimes( updTimePart )
	EndIf
	
;	gLotusUpdateTimes( updTimeSort ) = MilliSecs()
	SortParticles()
;	gLotusUpdateTimes( updTimeSort ) = MilliSecs() - gLotusUpdateTimes( updTimeSort )
	
;	gLotusUpdateTimes( updTimeDrawAlpha ) = MilliSecs()
	For HJ = 0 To gLotusAlphaParticleCount - 1
		DrawLotusParticle Object.LotusParticle( gAlphaPartArray( HJ, 1 ) ), gLotusTPivot
	Next
;	gLotusUpdateTimes( updTimeDrawAlpha ) = MilliSecs() - gLotusUpdateTimes( updTimeDrawAlpha )
	
;	gLotusUpdateTimes( uptTimeAll ) = MilliSecs() - gLotusUpdateTimes( uptTimeAll )
End Function
