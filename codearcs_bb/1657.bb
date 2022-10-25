; ID: 1657
; Author: Nilium
; Date: 2006-04-01 11:54:42
; Title: Lotus Particle System R2 - Part 2
; Description: Part 2 - The now-public domain Lotus Particle System R2

Function DrawLotusParticle( P.LotusParticle, TPivot )
	If P <> Null Then
		
		If P\Cull > 0 Then
			CameraProject gLotusCamera, P\PositionX, P\PositionY, P\PositionZ
			If ProjectedZ() <= 0 Then Return
			PX = ProjectedX()
			If PX < -75 Or PX > gWidth+75 Then Return
			PY = ProjectedY()
			If PY < 0 Or PY > gHeight+75 Then Return
		EndIf
		
		LFrom# = P\Life/P\LifeSpan
		LTo# = 1.0 - LFrom#
		
		PositionEntity gLotusParticlePivot,P\PositionX,P\PositionY,P\PositionZ
		RotateEntity gLotusParticlePivot,P\AngleX,P\AngleY,0
		
		Gr.LotusGraph = P\ColorGraph
		If Gr <> Null Then
			If Gr\Keys-1 <= 0 Then Gr = Null
		EndIf
		
		If Gr = Null Then
			If P\Range <> 0 Then
				A# = Max(P\AlphaFrom*LFrom + P\AlphaTo*LTo,1.0-(EntityDistance(gLotusCamera,gLotusParticlePivot)/P\Range))
			Else
				A# = P\AlphaFrom*LFrom + P\AlphaTo*LTo
			EndIf
			R = P\RedFrom*LFrom + P\RedTo*LTo
			G = P\GreenFrom*LFrom + P\GreenTo*LTo
			B = P\BlueFrom*LFrom + P\BlueTo*LTo
		Else
			AccumulateGraphArray( Gr )
			Pivot# = Gr\Width * LTo
			Front = -1
			Behind = -1
			For N = 0 To Gr\Keys - 1
				If gGraphArray( N, 0 ) >= Pivot Then
					Behind = N-1
					Front = N
					Exit
				EndIf
			Next
			
			If Front = - 1 Then
				Front = Gr\Keys-1
				Behind = 0
			EndIf
			
			Behind = Min( Behind, 0 )
			Front = Max( Front, Gr\Keys - 1 )
			
			VFrom# = 1.0-ReturnedY#( Pivot#, gGraphArray( Behind, 0 ), gGraphArray( Behind, 1 )*10, gGraphArray( Front, 0 ), gGraphArray( Front, 1 )*10 )/10
			VTo# = 1.0 - VFrom
			
			If P\Range > 0 Then
				A# = Max(P\AlphaFrom*VFrom + P\AlphaTo*VTo,1.0-(EntityDistance(gLotusCamera,gLotusParticlePivot)/P\Range))
			Else
				A# = P\AlphaFrom*VFrom + P\AlphaTo*VTo
			EndIf
			
			R = P\RedFrom*VFrom + P\RedTo*VTo
			G = P\GreenFrom*VFrom + P\GreenTo*VTo
			B = P\BlueFrom*VFrom + P\BlueTo*VTo
		EndIf
		
		Gr.LotusGraph = P\SizeGraph
		If Gr <> Null Then
			If Gr\Keys-1 <= 0 Then Gr = Null
		EndIf
		
		If Gr = Null Then
			SizeX# = P\SizeFromX*LFrom+P\SizeToX*LTo
			SizeY# = P\SizeFromY*LFrom+P\SizeToY*LTo
			SizeZ# = P\SizeFromZ*LFrom+P\SizeToZ*LTo
		Else
			AccumulateGraphArray( Gr )
			Pivot# = Gr\Width * LTo
			Front = -1
			Behind = -1
			For N = 0 To Gr\Keys - 1
				If gGraphArray( N, 0 ) >= Pivot Then
					Behind = N-1
					Front = N
					Exit
				EndIf
			Next
			
			If Front = - 1 Then
				Front = Gr\Keys-1
				Behind = 0
			EndIf
			
			Behind = Min( Behind, 0 )
			Front = Max( Front, Gr\Keys - 1 )
			
			VFrom# = 1.0-ReturnedY#( Pivot#, gGraphArray( Behind, 0 ), gGraphArray( Behind, 1 )*10, gGraphArray( Front, 0 ), gGraphArray( Front, 1 )*10 )/10
			VTo# = 1.0 - VFrom
			
			SizeX# = P\SizeFromX*VFrom+P\SizeToX*VTo
			SizeY# = P\SizeFromY*VFrom+P\SizeToY*VTo
			SizeZ# = P\SizeFromZ*VFrom+P\SizeToZ*VTo
		EndIf
		
		If P\Hidden = False And A > 0 And SizeX > 0 And SizeY > 0 And SizeZ > 0 Then
			T.LotusTexture = P\Texture
			
			If T <> Null Then
				Select P\BlendMode
					Case 1
						Surface = T\Alpha
					Case 2
						Surface = T\Multiply
					Case 3
						Surface = T\Add
				End Select
				If Surface = 0 Then Surface = gLotusNullTexture\Alpha
			Else
				Surface = T\Alpha
			EndIf
			
			If Surface <> 0 Then
				If P\WaveRadiusX <> 0 Or P\WaveRadiusY <> 0 Or P\WaveRadiusZ <> 0 Then
					MoveEntity gLotusParticlePivot,(gSine(Int((P\LifeBegan+(P\LifeSpan-P\Life))*P\WaveSpeedX) Mod 359)*P\WaveRadiusX)*LFrom,(gCosine(Int((P\LifeBegan+(P\LifeSpan-P\Life))*P\WaveSpeedY) Mod 359)*P\WaveRadiusY)*LFrom,(gCosine(Int((P\LifeBegan+(P\LifeSpan-P\Life))*P\WaveSpeedZ) Mod 359)*P\WaveRadiusZ)*LFrom
				EndIf
				
				Select P\ViewMode
					Case 1		;; Always Facing
						PointEntity gLotusParticlePivot,gLotusCamera,P\AngleZ
					Case 2
						TurnEntity gLotusParticlePivot,0,0,P\AngleZ
					Case 3		;; X-facing
						PositionEntity TPivot,P\PositionX,P\PositionY,P\PositionZ
						RotateEntity TPivot,P\AngleX,P\AngleY,0
						EntityParent gLotusParticlePivot,TPivot,1
						TurnEntity TPivot,0,DeltaYaw(gLotusParticlePivot,gLotusCamera),0
						EntityParent gLotusParticlePivot,0,1
						TurnEntity gLotusParticlePivot,0,0,P\AngleZ
					Case 4		;; Y-facing
						PositionEntity TPivot,P\PositionX,P\PositionY,P\PositionZ
						RotateEntity TPivot,0,P\AngleY,0
						EntityParent gLotusParticlePivot,TPivot,1
						TurnEntity TPivot,DeltaPitch(gLotusParticlePivot,gLotusCamera),0,0
						EntityParent gLotusParticlePivot,0,1
						TurnEntity gLotusParticlePivot,0,0,P\AngleZ
				End Select
				
				If P\RollMode => 1 And P\RollMode <= 3 Then
					AlignToVector gLotusParticlePivot,P\LastPositionX-P\PositionX,P\LastPositionY-P\PositionY,P\LastPositionZ-P\PositionZ,P\RollMode,1
				EndIf
				
				If P\Animated >= True Then
					P\Frame = (T\Rows*T\Columns)*LTo
				EndIf
				Rows = 0
				Columns = 0
				Frame = P\Frame
				While Frame >= T\Columns
					Rows = Rows + 1
					Frame = Frame - T\Columns
				Wend
				Columns = P\Frame Mod T\Columns
				
				eU# = Columns*T\UStep
				eV# = Rows*T\VStep

				Select P\ParticleMesh
					Case cP_QUAD
						TFormPoint -SizeX,SizeY,0,gLotusParticlePivot,gLotusParticleMesh
						V = AddVertex(Surface,TFormedX(),TFormedY(),TFormedZ(),eU+T\UStep,eV)
						P\Vertex = V
						
						TFormPoint SizeX,SizeY,0,gLotusParticlePivot,gLotusParticleMesh
						AddVertex(Surface,TFormedX(),TFormedY(),TFormedZ(),eU,eV)
						
						TFormPoint SizeX,-SizeY,0,gLotusParticlePivot,gLotusParticleMesh
						AddVertex(Surface,TFormedX(),TFormedY(),TFormedZ(),eU,eV+T\VStep)
						
						TFormPoint -SizeX,-SizeY,0,gLotusParticlePivot,gLotusParticleMesh
						AddVertex(Surface,TFormedX(),TFormedY(),TFormedZ(),eU+T\UStep,eV+T\VStep)
						
						AddTriangle Surface,V,V+1,V+2
						AddTriangle Surface,V+2,V+3,V
						
						For N = 0 To 3
							VertexColor Surface,V+N,R,G,B,A
						Next
						
					Case cP_TRIANGLE
						TFormPoint -SizeX,SizeY,0,gLotusParticlePivot,gLotusParticleMesh
						V = AddVertex(Surface,TFormedX(),TFormedY(),TFormedZ(),eU+T\UStep,eV)
						P\Vertex = V
						
						TFormPoint SizeX*3,SizeY,0,gLotusParticlePivot,gLotusParticleMesh
						AddVertex(Surface,TFormedX(),TFormedY(),TFormedZ(),eU+T\UStep-T\UStep*2,eV)
						
						TFormPoint -SizeX,-SizeY*3,0,gLotusParticlePivot,gLotusParticleMesh
						AddVertex(Surface,TFormedX(),TFormedY(),TFormedZ(),eU+T\UStep,eV+T\VStep*2)
						
						AddTriangle Surface,V,V+1,V+2
						
						For N = 0 To 2
							VertexColor Surface,V+N,R,G,B,A
						Next
					
					Case cP_TRAIL,cP_TRAILVERTICAL
						If P\ParticleMesh = 2 Then
							TFormPoint -SizeX,0,0,gLotusParticlePivot,gLotusParticleMesh
							V = AddVertex(Surface,TFormedX(),TFormedY(),TFormedZ(),eU,eV+LTo*T\VStep)
							P\Vertex = V
							TFormPoint SizeX,0,0,gLotusParticlePivot,gLotusParticleMesh
							AddVertex(Surface,TFormedX(),TFormedY(),TFormedZ(),eU+T\UStep,eV+LTo*T\VStep)
						Else
							TFormPoint 0,SizeY,0,gLotusParticlePivot,gLotusParticleMesh
							V = AddVertex(Surface,TFormedX(),TFormedY(),TFormedZ(),eU,eV+LTo*T\VStep)
							P\Vertex = V
							TFormPoint 0,-SizeY,0,gLotusParticlePivot,gLotusParticleMesh
							AddVertex(Surface,TFormedX(),TFormedY(),TFormedZ(),eU+T\UStep,eV+LTo*T\VStep)
						EndIf
						
						If P\Previous <> Null Then
							If P\Texture = P\Previous\Texture And P\Previous\ParticleMesh = P\ParticleMesh And P\Previous\Vertex > -1 And P\Previous\BlendMode = P\BlendMode Then
								AddTriangle Surface,V,V+1,P\Previous\Vertex+1
								AddTriangle Surface,P\Previous\Vertex+1,P\Previous\Vertex,V
							EndIf
						EndIf
						
						For N = 0 To 1
							VertexColor Surface,V+N,R,G,B,A
						Next
					
					Case cP_DENT
						TFormPoint -SizeX,SizeY,0,gLotusParticlePivot,gLotusParticleMesh
						V = AddVertex(Surface,TFormedX(),TFormedY(),TFormedZ(),eU+T\UStep,eV)
						
						TFormPoint SizeX,SizeY,0,gLotusParticlePivot,gLotusParticleMesh
						AddVertex(Surface,TFormedX(),TFormedY(),TFormedZ(),eU,eV)
						
						TFormPoint SizeX,-SizeY,0,gLotusParticlePivot,gLotusParticleMesh
						AddVertex(Surface,TFormedX(),TFormedY(),TFormedZ(),eU,eV+T\VStep)
						
						TFormPoint -SizeX,-SizeY,0,gLotusParticlePivot,gLotusParticleMesh
						AddVertex(Surface,TFormedX(),TFormedY(),TFormedZ(),eU+T\UStep,eV+T\VStep)
						
						TFormPoint 0,0,-SizeZ,gLotusParticlePivot,gLotusParticleMesh
						AddVertex(Surface,TFormedX(),TFormedY(),TFormedZ(),eU+T\UStep*.5,eV+t\VStep*.5)
						
						AddTriangle Surface,V,V+1,V+4
						AddTriangle Surface,V+1,V+2,V+4
						AddTriangle Surface,V+2,V+3,V+4
						AddTriangle Surface,V+3,V,V+4
						
						For N = 0 To 4
							VertexColor Surface,V+N,R,G,B,A
						Next
					
					Default
						P\Vertex = CountVertices(Surface)
						For N = 1 To CountSurfaces(P\ParticleMesh)
							V = CountVertices(Surface)
							S = GetSurface(P\ParticleMesh,N)
							For eV = 0 To CountVertices(S)-1
								TFormPoint VertexX(S,eV)*SizeX,VertexY(S,eV)*SizeY,VertexZ(S,eV)*SizeZ,gLotusParticlePivot,gLotusParticleMesh
								nV = AddVertex(Surface,TFormedX(),TFormedY(),TFormedZ(),VertexU(S,eV),VertexV(S,eV))
								VertexColor Surface,nV,R,G,B,A
							Next
							
							For eT = 0 To CountTriangles(S)-1
								eV0 = TriangleVertex(S,eT,0)
								eV1 = TriangleVertex(S,eT,1)
								eV2 = TriangleVertex(S,eT,2)
								AddTriangle Surface,V+eV0,V+eV1,V+eV2
							Next
						Next
						
						S = 0
				End Select
				gLotusParticlesDrawn = gLotusParticlesDrawn + 1
			Else
				P\Vertex = -1
			EndIf
		Else
			P\Vertex = -1
		EndIf
	Else
		Return False
	EndIf
	Return True
End Function


;; PARTICLE
Function CreateParticle(Emitter)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	ORoll# = EntityRoll( Emitter )
	OPitch# = EntityPitch( Emitter )
	OYaw# = EntityYaw( Emitter )
	RotateEntity Emitter, OPitch, OYaw, 0
	For Emission = 1 To E\EmissionRate
;		If E\BlendMode = 3 Then Stop
		gLotusParticleCount = gLotusParticleCount + 1
		P.LotusParticle = New LotusParticle
		
		P\Parent = E
		P\Trail = E\Trail
		P\Child = E\Child
		
;		P\LastPosition = New LotusVector
;		P\Position = New LotusVector
		
		RndRad# = Rnd(0,E\CylinderX)
		RndAng% = Rand(359)
		ET# = MilliSecs()
		gTempLotusVector\X = gSine(RndAng)*RndRad + (gSine(ET*E\CircleSpeedX Mod 359)*E\CircleRadiusX)+Rnd(-E\CubeX,E\CubeX)
		gTempLotusVector\Z = Rnd(E\CylinderZ,E\CylinderY) + gSine(ET*E\CircleSpeedY Mod 359)*E\CircleRadiusY+Rnd(-E\CubeY,E\CubeY)
		gTempLotusVector\Y = gCosine(RndAng)*RndRad + (gCosine(ET*E\CircleSpeedZ Mod 359)*E\CircleRadiusZ)+Rnd(-E\CubeZ,E\CubeZ)
		LotusVec_TFormPoint gTempLotusVector,E\Entity,0
;		LotusVec_TFormedB P\Position
		P\PositionX = TFormedX()
		P\PositionY = TFormedY()
		P\PositionZ = TFormedZ()
		
;		P\Translation = LotusVec_Copy(E\Translation)
		gTempLotusVector\X = Rnd(-E\TranslationJitterX*E\TranslationJitterDown,E\TranslationJitterX*E\TranslationJitterUp)
		gTempLotusVector\Y = Rnd(-E\TranslationJitterY*E\TranslationJitterDown,E\TranslationJitterY*E\TranslationJitterUp)
		gTempLotusVector\Z = Rnd(-E\TranslationJitterZ*E\TranslationJitterDown,E\TranslationJitterZ*E\TranslationJitterUp)
		P\TranslationX = E\TranslationX + gTempLotusVector\X
		P\TranslationY = E\TranslationY + gTempLotusVector\Y
		P\TranslationZ = E\TranslationZ + gTempLotusVector\Z
;		LotusVec_Add(P\Translation,gTempLotusVector)
		
;		P\Angle = New LotusVector
		
		P\VelocityDecay = E\VelocityDecay
		
		gTempLotusVector\X = Rnd(-E\RandomRotationX,E\RandomRotationX)
		gTempLotusVector\Y = Rnd(-E\RandomRotationY,E\RandomRotationY)
		gTempLotusVector\Z = Rnd(-E\RandomRotationZ,E\RandomRotationZ)
		P\AngleX = OPitch + gTempLotusVector\X
		P\AngleY = OYaw + gTempLotusVector\Y
		P\AngleZ = ORoll + gTempLotusVector\Z
		
;		P\Velocity = LotusVec_Copy(E\Velocity)
		gTempLotusVector\X = Rnd(-E\VelocityJitterX*E\VelocityJitterDown,E\VelocityJitterX*E\VelocityJitterUp)
		gTempLotusVector\Y = Rnd(-E\VelocityJitterY*E\VelocityJitterDown,E\VelocityJitterY*E\VelocityJitterUp)
		gTempLotusVector\Z = Rnd(-E\VelocityJitterZ*E\VelocityJitterDown,E\VelocityJitterZ*E\VelocityJitterUp)
		P\OVelocityX = E\VelocityX + gTempLotusVector\X
		P\OVelocityY = E\VelocityY + gTempLotusVector\Y
		P\OVelocityZ = E\VelocityZ + gTempLotusVector\Z
		RotateEntity gLotusParticlePivot,P\AngleX,P\AngleY,0,1
		TFormVector P\OVelocityX,P\OVelocityY,P\OVelocityZ,gLotusParticlePivot,0
		P\VelocityX = TFormedX()
		P\VelocityY = TFormedY()
		P\VelocityZ = TFormedZ()
;		LotusVec_Add(P\Velocity,gTempLotusVector)
;		P\AngleVelocity = LotusVec_Copy(E\AngleVelocity)
;		P\Acceleration = LotusVec_Copy(E\Acceleration)
;		P\AngleAcceleration = LotusVec_Copy(E\AngleAcceleration)
		P\AngleVelocityX = E\AngleVelocityX
		P\AngleVelocityY = E\AngleVelocityY
		P\AngleVelocityZ = E\AngleVelocityZ
		P\AccelerationX = E\AccelerationX
		P\AccelerationY = E\AccelerationY
		P\AccelerationZ = E\AccelerationZ
		gTempLotusVector\X = Rnd(-E\SizeJitterX*E\SizeJitterDown,E\SizeJitterX*E\SizeJitterUp)
		gTempLotusVector\Y = Rnd(-E\SizeJitterY*E\SizeJitterDown,E\SizeJitterY*E\SizeJitterUp)
		gTempLotusVector\Z = Rnd(-E\SizeJitterZ*E\SizeJitterDown,E\SizeJitterZ*E\SizeJitterUp)
		If E\SizeJitterUniform Then
			S# = (gTempLotusVector\X+gTempLotusVector\Y+gTempLotusVector\Z)/3
			gTempLotusVector\X = S
			gTempLotusVector\Y = S
			gTempLotusVector\Z = S
		EndIf
;		P\SizeFrom = LotusVec_Copy(E\SizeFrom)
		P\SizeFromX = E\SizeFromX + gTempLotusVector\X
		P\SizeFromY = E\SizeFromY + gTempLotusVector\Y
		P\SizeFromZ = E\SizeFromZ + gTempLotusVector\Z
		
;		P\SizeTo = LotusVec_Copy(E\SizeTo)
		P\SizeToX = E\SizeToX + gTempLotusVector\X
		P\SizeToY = E\SizeToY + gTempLotusVector\Y
		P\SizeToZ = E\SizeToZ + gTempLotusVector\Z
		
;		P\WaveRadius = LotusVec_Copy(E\WaveRadius)
;		P\WaveSpeed = LotusVec_Copy(E\WaveSpeed)
		P\WaveRadiusX = E\WaveRadiusX
		P\WaveRadiusY = E\WaveRadiusY
		P\WaveRadiusZ = E\WaveRadiusZ
		P\WaveSpeedX = E\WaveSpeedX
		P\WaveSpeedY = E\WaveSpeedY
		P\WaveSpeedZ = E\WaveSpeedZ
		
		If Float(E\ColorJitterRed + E\ColorJitterBlue + E\ColorJitterGreen) + E\ColorJitterAlpha <> 0 Then
			If E\ColorJitterUniform = False Then
				RR = Rand(-E\ColorJitterRed*E\ColorJitterDown,E\ColorJitterRed*E\ColorJitterUp)
				RG = Rand(-E\ColorJitterGreen*E\ColorJitterDown,E\ColorJitterGreen*E\ColorJitterUp)
				RB = Rand(-E\ColorJitterBlue*E\ColorJitterDown,E\ColorJitterBlue*E\ColorJitterUp)
			Else
				C = (E\ColorJitterRed+E\ColorJitterGreen+E\ColorJitterBlue)/3
				C = Rand(-C*E\ColorJitterDown,C*E\ColorJitterDown)
				RR = C
				RG = C
				RB = C
			EndIf
			RA# = Rnd(-E\ColorJitterAlpha*E\ColorJitterDown,E\ColorJitterAlpha*E\ColorJitterUp)
		EndIf
		
		P\RedFrom = E\RedFrom + RR
		P\GreenFrom = E\GreenFrom + RG
		P\BlueFrom = E\BlueFrom + RB
		P\AlphaFrom = E\AlphaFrom + RA
		
		P\RedTo = E\RedTo + RR
		P\GreenTo = E\GreenTo + RG
		P\BlueTo = E\BlueTo + RB
		P\AlphaTo = E\AlphaTo + RA
		
		P\LifeSpan = E\LifeSpan + Rand(-E\LifeSpanJitter*E\LifeSpanJitterDown,E\LifeSpanJitter*E\LifeSpanJitterUp)
		P\Life = P\LifeSpan
		P\LifeBegan = MilliSecs()
		P\Texture = E\Texture
		P\BlendMode = E\BlendMode
		P\ParticleMesh = E\ParticleMesh
		P\Previous = E\Latest
		E\Latest = P
		P\Vertex = -1
		P\ViewMode = E\ViewMode
		P\LifeMode = E\LifeMode
		P\ChildMode = E\ChildMode
		P\Draw = 1
		P\Frozen = E\Frozen
		P\Hidden = E\Hidden
		P\Frame = E\Frame
		P\Animated = E\Animated
		P\Gravity = E\Gravity + Rnd(-E\GravityJitter*E\GravityJitterDown,E\GravityJitter*E\GravityJitterUp)
		P\GravityEnabled = E\GravityEnabled
		P\Weight = E\Weight
		P\RollMode = E\RollMode	
		P\Range = E\Range
		P\Cull = E\Cull
		P\Sorting = E\Sorting
		P\SizeGraph = E\SizeGraph
		P\ColorGraph = E\ColorGraph
		P\Bounce = E\Bounce
		P\BounceDecay = E\BounceDecay
		P\BounceMax = E\BounceMax
		P\MinY = E\MinY
		P\BounceSound = E\BounceSound
		P\SplineMesh = E\SplineMesh
		P\BounceSoundRange = E\BounceSoundRange
		P\DeflectorsAffect = E\DeflectorsAffect
		
		If P\BlendMode = 1 And P\Sorting => 1 Then
			gLotusAlphaParticleCount = gLotusAlphaParticleCount + 1
		EndIf
		
;		If P\BlendMode = 1 Then
;			Insert P Before First LotusParticle
;		ElseIf P\BlendMode = 3 Then
;			Insert P After Last LotusParticle
;		EndIf
	Next
	
	If P <> Null Then
		If P\Child <> Null Then
			If P\Child\WaitB <= 0 And P\ChildMode <> 1 Then
				ax# = EntityPitch(P\Child\Entity,1)
				ay# = EntityYaw(P\Child\Entity,1)
				az# = EntityRoll(P\Child\Entity,1)
				RotateEntity P\Child\Entity,P\AngleX,P\AngleY,P\AngleZ,1
				PositionEntity P\Child\Entity,P\PositionX,P\PositionY,P\PositionZ,1
				CreateParticle(P\Child\Entity)
				RotateEntity P\Child\Entity,ax,ay,az,1
				PositionEntity P\Child\Entity,P\Child\PositionX,P\Child\PositionY,P\Child\PositionZ,1
			EndIf
		EndIf
	EndIf
	
	RotateEntity Emitter, OPitch, OYaw, ORoll
End Function

Function KillParticle(P.LotusParticle)
	If P = Null Then Return False
	gLotusParticleCount = gLotusParticleCount - 1
	If P\BlendMode = 1 Then gLotusAlphaParticleCount = gLotusAlphaParticleCount - 1
	
	If P\Child <> Null Then
			If P\Child\WaitB <= 0 And P\ChildMode <> 1 Then
				RotateEntity P\Child\Entity,P\AngleX,P\AngleY,P\AngleZ,1
				PositionEntity P\Child\Entity,P\PositionX,P\PositionY,P\PositionZ,1
				CreateParticle(P\Child\Entity)
				RotateEntity P\Child\Entity,P\Child\AngleX,P\Child\AngleY,P\Child\AngleZ,1
				PositionEntity P\Child\Entity,P\Child\PositionX,P\Child\PositionY,P\Child\PositionZ,1
			EndIf
		EndIf
	
	Delete P
End Function

Function ClearParticles()
	Delete Each LotusParticle
End Function


;; GRAPH

Function CreateGraph()
	G.LotusGraph = New LotusGraph
	G\Bank = CreateBank( 0 )
	G\Keys = 0
	Return Handle(G)
End Function

Function AddGraphPoint( Graph, X#, Y# )
	G.LotusGraph = Object.LotusGraph( Graph )
	If G = Null Then Return False
	Dim gGraphArray( G\Keys, 1 )
	For N = 0 To G\Keys-1
		gGraphArray( N, 0 ) = PeekFloat( G\Bank, N*8-1 )
		gGraphArray( N, 1 ) = PeekFloat( G\Bank, N*8+4-1 )
	Next
	gGraphArray( G\Keys, 0 ) = X
	gGraphArray( G\Keys, 1 ) = Y
	DebugLog "Adding "+gGraphArray( G\Keys, 0 )+","+gGraphArray( G\Keys, 1 )+" to graph "+Graph
	InsertionSort( G\Keys )
	If gLotusDebugInfo Then
		DebugLog ""
		DebugLog "----GRAPH REPORT-----"
		For N = 0 To G\Keys
			DebugLog gGraphArray( N, 0 ) + "  ,    "+gGraphArray( N, 1 )
		Next
		DebugLog "---------------------------"
		DebugLog ""
	EndIf
	G\Keys = G\Keys + 1
	ResizeBank G\Bank, G\Keys*8
	For N = 0 To G\Keys - 1
		PokeFloat( G\Bank, N*8-1, gGraphArray( N, 0 ) )
		PokeFloat( G\Bank, N*8+4-1, gGraphArray( N, 1 ) )
	Next
	G\Width = gGraphArray( G\Keys-1, 0 )
	DebugLog "Resized graph to "+G\Width
End Function

Function RemoveGraphPoint( Graph, X#, Range# = 0 )
	G.LotusGraph = Object.LotusGraph( Graph )
	If G = Null Then Return False
	Dim gGraphArray( G\Keys-2, 1 )
	For N = 0 To G\Keys-1
		eX# = PeekFloat( G\Bank, N*8-1 )
		If (eX > X+Range And eX < X-Range) Or Found = True Then
			gGraphArray( C, 0 ) = eX
			gGraphArray( C, 1 ) = PeekFloat( G\Bank, N*8+4-1 )
			C = C + 1
		Else
			Found = True
		EndIf
	Next
	InsertionSort( G\Keys-2 )
	G\Keys = G\Keys - 1
	ResizeBank G\Bank, G\Keys*8
	For N = 0 To G\Keys - 1
		PokeFloat( G\Bank, N*8-1, gGraphArray( N, 0 ) )
		PokeFloat( G\Bank, N*8+4-1, gGraphArray( N, 1 ) )
	Next
	G\Width = gGraphArray( G\Keys-1, 0 )
	DebugLog "Resized graph to "+G\Width
End Function

Function AccumulateGraphArray( G.LotusGraph )
	If G = Null Then Return False
	Dim gGraphArray( G\Keys-1, 1 )
	For N = 0 To G\Keys-1
		gGraphArray( N, 0 ) = PeekFloat( G\Bank, N*8-1 )
		gGraphArray( N, 1 ) = PeekFloat( G\Bank, N*8+4-1 )
	Next
End Function

Function FreeGraph( Graph )
	G.LotusGraph = Object.LotusGraph( Graph )
	If G = Null Then Return False
	FreeBank G\Bank
	Delete G
End Function


Function SetLotusCamera(Camera)
	gLotusCamera = Camera
End Function

Function StartEmitter( Emitter, ActiveSpan = -2, FreeEmitterOnEnd=0 )
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	If ActiveSpan > -2 Then
		SetEmitterActiveSpan Emitter,ActiveSpan
	EndIf
	E\Active = E\ActiveSpan
	E\Emit = 1
	
	If E\EmitSound <> 0 Then E\EmitSoundChannel = PlaySound( E\EmitSound )
	
	E\FreeOnEndActive = FreeEmitterOnEnd
End Function

Function StopEmitter( Emitter )
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	If E\EmitSoundChannel <> 0 And E\LoopEmitSound >= 1 Then StopChannel( E\EmitSoundChannel )
	E\Emit = 0
	E\Active = -1
End Function

Function SetEmitterLifespan(Emitter,Lifespan,Jitter%=0,Up%=1,Down%=1)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\Lifespan = Lifespan
	E\LifeSpanJitter = Jitter%
	E\LifeSpanJitterUp% = Up%
	E\LifeSpanJitterDown% = Down%
End Function

Function SetEmitterActiveSpan( Emitter, Active=-1 )
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\ActiveSpan = Active
End Function

Function SetEmitterPosition(Emitter,X#,Y#,Z#)	;; Backwards compatibility
	If EmitterExists( Emitter ) = 0 Then Return False
	PositionEntity Emitter,X,Y,Z
End Function

Function SetEmitterAngle(Emitter,Pitch#,Yaw#,Roll#)	;; Backwards compatibility
	If EmitterExists( Emitter ) = 0 Then Return False
	RotateEntity Emitter,Pitch,Yaw,Roll
End Function

Function SetEmitterTranslation(Emitter,X#,Y#,Z#)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\TranslationX = X
	E\TranslationY = Y
	E\TranslationZ = Z
	Return True
End Function

Function SetEmitterVelocity(Emitter,X#,Y#,Z#)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\VelocityX = X
	E\VelocityY = Y
	E\VelocityZ = Z
	Return True
End Function

Function SetEmitterVelocityDecay(Emitter,Decay#=0)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\VelocityDecay = Decay
End Function

Function SetEmitterAcceleration(Emitter,X#,Y#,Z#)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\AccelerationX = X
	E\AccelerationY = Y
	E\AccelerationZ = Z
End Function

Function SetEmitterAngleVelocity(Emitter,Pitch#,Yaw#,Roll#)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\AngleVelocityX = Pitch
	E\AngleVelocityY = Yaw
	E\AngleVelocityZ = Roll
End Function

Function SetEmitterAngleAcceleration(Emitter,Pitch#,Yaw#,Roll#)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\AngleAccelerationX = Pitch
	E\AngleAccelerationY = Yaw
	E\AngleAccelerationZ = Roll
End Function

Function SetEmitterTranslationJitter(Emitter,X#,Y#,Z#,Up%=1,Down%=1)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\TranslationJitterX = X
	E\TranslationJitterY = Y
	E\TranslationJitterZ = Z
	E\TranslationJitterUp = Up
	E\TranslationJitterDown = Down
End Function

Function SetEmitterVelocityJitter(Emitter,X#,Y#,Z#,Up%=1,Down%=1)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\VelocityJitterX = X
	E\VelocityJitterY = Y
	E\VelocityJitterZ = Z
	E\VelocityJitterUp = Up
	E\VelocityJitterDown = Down
End Function

Function SetEmitterColorFrom(Emitter,Red=255,Green=255,Blue=255,Alpha#=1)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName$(Emitter)))
	If E <> Null Then
		E\RedFrom = Red
		E\GreenFrom = Green
		E\BlueFrom = Blue
		E\AlphaFrom = Alpha
	Else
		Return False
	EndIf
	Return True
End Function

Function SetEmitterColorTo(Emitter,Red=255,Green=255,Blue=255,Alpha#=1)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName$(Emitter)))
	If E <> Null Then
		E\RedTo = Red
		E\GreenTo = Green
		E\BlueTo = Blue
		E\AlphaTo = Alpha
	Else
		Return False
	EndIf
	Return True
End Function

Function SetEmitterColorJitter(Emitter,Red%=0,Green%=0,Blue%=0,Alpha#=0,Uniform%=1,Up%=1,Down%=1)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\ColorJitterRed = Red
	E\ColorJitterGreen = Green
	E\ColorJitterBlue = Blue
	E\ColorJitterAlpha = Alpha
	E\ColorJitterUniform = Uniform
	E\ColorJitterUp = Up
	E\ColorJitterDown = Down
End Function

Function SetEmitterColor(Emitter,Red=255,Green=255,Blue=255,Alpha#=1)
	If EmitterExists( Emitter ) = 0 Then Return False
	SetEmitterColorFrom Emitter,Red,Green,Blue,Alpha
	SetEmitterColorTo Emitter,Red,Green,Blue,Alpha
End Function

Function SetEmitterSizeFrom(Emitter,X#=1,Y#=1,Z#=1)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\SizeFromX = X
	E\SizeFromY = Y
	E\SizeFromZ = Z
End Function

Function SetEmitterSizeTo(Emitter,X#=1,Y#=1,Z#=1)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\SizeToX = X
	E\SizeToY = Y
	E\SizeToZ = Z
End Function

Function SetEmitterSizeJitter(Emitter,X#=0,Y#=0,Z#=0,Uniform%=1,Up%=1,Down%=1)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\SizeJitterX = X
	E\SizeJitterY = Y
	E\SizeJitterZ = Z
	E\SizeJitterUniform = Uniform
	E\SizeJitterUp = Up
	E\SizeJitterDown = Down
End Function

Function SetEmitterSize(Emitter,X#=1,Y#=1,Z#=1)
	If EmitterExists( Emitter ) = 0 Then Return False
	SetEmitterSizeFrom Emitter,X,Y,Z
	SetEmitterSizeTo Emitter,X,Y,Z
End Function

Function SetEmitterBlend(Emitter,Blend=1)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\BlendMode = Blend
End Function

Function SetEmitterRandomRotation(Emitter,PitchRange#=0,YawRange#=0,RollRange#=0)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\RandomRotationX = PitchRange
	E\RandomRotationY = YawRange
	E\RandomRotationZ = RollRange
End Function

Function SetEmitterParticleMesh(Emitter,Mesh=0)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\ParticleMesh = Mesh
End Function

Function SetEmitterRoll(Emitter,Roll#=0)
	If EmitterExists( Emitter ) = 0 Then Return False
	RotateEntity Emitter,EntityPitch(Emitter,False),EntityYaw(Emitter,False),Roll
End Function

Function SetEmitterAutoEmit(Emitter,Emit=1)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\Emit = Emit
	If Emit Then E\Active = -1
End Function

Function SetEmitterTrail(Emitter,TrailEmitter=0)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	If TrailEmitter <> 0 Then
		Trail.LotusEmitter = Object.LotusEmitter(Int(EntityName(TrailEmitter)))
	Else
		Trail.LotusEmitter = Null
	EndIf
	
	E\Trail = Trail
End Function

Function SetEmitterChild(Emitter,ChildEmitter=0)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	If ChildEmitter <> 0 Then
		Child.LotusEmitter = Object.LotusEmitter(Int(EntityName(ChildEmitter)))
	Else
		Child.LotusEmitter = Null
	EndIf
	
	E\Child = Child
	If Child <> Null Then Child\Parent = E
End Function

Function SetEmitterParent(Emitter,ParentEmitter=0)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	If ParentEmitter <> 0 Then
		Parent.LotusEmitter = Object.LotusEmitter(Int(EntityName(ParentEmitter)))
	Else
		Parent.LotusEmitter = Null
	EndIf
	
	E\Parent = Parent
	If Parent <> Null Then Parent\Child = E
End Function

Function SetEmitterBox(Emitter,X#=0,Y#=0,Z#=0)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\CubeX = X
	E\CubeY = Y
	E\CubeZ = Z
End Function

Function SetEmitterCylinder(Emitter,Radius#=0,Top#=0,Bottom#=0)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\CylinderX = Radius
	E\CylinderY = Top
	E\CylinderZ = Bottom
End Function

Function SetEmitterRadius(Emitter,Radius#=0,InnerRadius# = 0,YRadius# = 0)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\Radius = Radius
	E\InnerRadius = InnerRadius
	If YRadius# = 0 Then YRadius# = Radius#
	EYRadius# = YRadius
End Function

Function SetEmitterWaveSpeed(Emitter,X#=1,Y#=1,Z#=1)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\WaveSpeedX = Abs X
	E\WaveSpeedY = Abs Y
	E\WaveSpeedZ = Abs Z
End Function

Function SetEmitterWaveRadius(Emitter,X#=0,Y#=0,Z#=0)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\WaveRadiusX = X
	E\WaveRadiusY = Y
	E\WaveRadiusZ = Z
End Function

Function SetEmitterEmissionRate(Emitter,Amount%=1)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\EmissionRate = Amount
End Function

Function SetEmitterViewMode(Emitter,Mode%=1)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\ViewMode = Mode
End Function

Function SetEmitterLifeMode(Emitter,Mode%=1)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\LifeMode = Max(Min(Mode,1),3)
End Function

Function SetEmitterChildMode(Emitter,Mode%=1)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\ChildMode = Mode
End Function

Function SetEmitterWaitSpan(Emitter,Span%=1,Jitter%=0)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\WaitSpan = Min(Span,1)
	E\DWaitSpan = Min(Span,1)
	E\WaitSpanJitter = Jitter
End Function

Function SetEmitterCircleSpeed(Emitter,X#,Y#,Z#)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\CircleSpeedX = Abs X
	E\CircleSpeedY = Abs Y
	E\CircleSpeedZ = Abs Z
End Function

Function SetEmitterCircleRadius(Emitter,X#,Y#,Z#)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\CircleRadiusX = X
	E\CircleRadiusY = Y
	E\CircleRadiusZ = Z
End Function

Function SetEmitterRadii(Emitter,X#,Y#,Z#)	;; Backwards compatibility
	Return SetEmitterCircleRadius(Emitter,X,Y,Z)
End Function

Function SetEmitterRadiiSpeed(Emitter,X#,Y#,Z#)	;; Backwards compatibility
	Return SetEmitterCircleSpeed(Emitter,X,Y,Z)
End Function

Function FreezeEmitter(Emitter,Recursive% = True)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	While E <> Null
		E\Frozen = True
		T.LotusEmitter = E\Trail
		If T <> Null And Recursive = True Then T\Frozen = True
		For P.LotusParticle = Each LotusParticle
			If P\Parent = E Or (P\Parent = T And Recursive) Then
				P\Frozen = True
			EndIf
		Next
		If E\EmitSoundChannel <> 0 Then PauseChannel( E\EmitSoundChannel )
		If Recursive Then
			E = E\Child
			If E <> Null Then T = E\Trail
		Else
			E = Null
		EndIf
	Wend
End Function

Function UnFreezeEmitter(Emitter,Recursive% = True)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	While E <> Null
		E\Frozen = False
		T.LotusEmitter = E\Trail
		If T <> Null And Recursive = True Then T\Frozen = False
		For P.LotusParticle = Each LotusParticle
			If P\Parent = E Or (P\Parent = T And Recursive) Then
				If P\LifeMode = 2 And P\Frozen = True Then
					P\LifeBegan = MilliSecs()-(P\LifeSpan-P\Life)
				EndIf
				P\Frozen = False
			EndIf
		Next
		If E\EmitSoundChannel <> 0 Then ResumeChannel( E\EmitSoundChannel )
		If Recursive Then
			E = E\Child
			If E <> Null Then T = E\Trail
		Else
			E = Null
		EndIf
	Wend
End Function

Function HideEmitter(Emitter,Recursive% = True)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))	
	While E <> Null
		T.LotusEmitter = E\Trail
		E\Hidden = True
		If T <> Null And Recursive = True Then T\Hidden = True
		For P.LotusParticle = Each LotusParticle
			If P\Parent = E Or (P\Parent = T And Recursive = True) Then
				P\Hidden = True
			EndIf
		Next
		If Recursive Then
			E = E\Child
			If E <> Null Then T = E\Trail
		Else
			E = Null
		EndIf
	Wend
End Function

Function ShowEmitter(Emitter,Recursive% = True)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))	
	While E <> Null
		T.LotusEmitter = E\Trail
		E\Hidden = False
		If T <> Null And Recursive = True Then T\Hidden = False
		For P.LotusParticle = Each LotusParticle
			If P\Parent = E Or (P\Parent = T And Recursive = True) Then
				P\Hidden = False
			EndIf
		Next
		If Recursive Then
			E = E\Child
			If E <> Null Then T = E\Trail
		Else
			E = Null
		EndIf
	Wend
End Function

Function SetEmitterGravity(Emitter,Gravity#=0,Enabled=1)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\Gravity = Gravity
	E\GravityEnabled = Min(Max(Enabled,1),0)
End Function

Function SetEmitterGravityJitter(Emitter,GravityJitter#=0,Up=1,Down=1)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\GravityJitter# = GravityJitter
	E\GravityJitterUp = Up
	E\GravityJitterDown = Down
End Function

Function SetEmitterWeight(Emitter,Weight#=0)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\Weight = Weight
End Function

Function SetEmitterRollMode(Emitter,Mode%=0)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\RollMode = Mode
End Function

Function SetEmitterViewRange(Emitter,Range#=0)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\Range# = Range#
End Function

Function SetEmitterCulling(Emitter,Enabled=0)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\Cull = Enabled
End Function

Function SetEmitterSorting(Emitter,Enabled=0)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\Sorting = Enabled
End Function

Function SetEmitterColorGraph(Emitter, Graph)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\ColorGraph = Object.LotusGraph( Graph )
End Function

Function SetEmitterSizeGraph(Emitter, Graph)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\SizeGraph = Object.LotusGraph( Graph )
End Function

Function SetEmitterBounce(Emitter, MinY#, Bounce%=-1, BounceDecay#=.25, MaximumBounces%=-1)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\MinY = MinY
	If Bounce > -1 Then E\Bounce = Bounce
	E\BounceDecay = BounceDecay
	If MaximumBounces > -1 Then E\BounceMax = MaximumBounces
End Function

Function SetEmitterEmissionSound(Emitter, Sound, Loop = 0, Range# = 100)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	If E\EmitSound <> Sound Then
		If E\EmitSoundChannel <> 0 Then
			StopChannel E\EmitSoundChannel
			E\EmitSoundChannel = 0
		EndIf
	EndIf
	E\EmitSound = Sound
	E\LoopEmitSound = Loop
	E\EmitSoundRange = 100
End Function

Function SetEmitterBounceSound(Emitter, Sound, Range# = 100)
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\BounceSound = Sound
	E\BounceSoundRange = Range
End Function

Function SetEmitterSplineMesh( Emitter, Mesh=0 )
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\SplineMesh = Mesh
End Function

Function SetEmitterDeflectorsAffect( Emitter, Enabled=1 )
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\DeflectorsAffect = Enabled
End Function

Function EnableDeflectors( Emitter )
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\DeflectorsAffect = True
End Function

Function DisableDeflectors( Emitter )
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	E\DeflectorsAffect = False
End Function

Function EmitterActive( Emitter )
	If EmitterExists( Emitter ) = 0 Then Return False
	E.LotusEmitter = Object.LotusEmitter(Int(EntityName(Emitter)))
	If E = Null Then Return False
	Return E\Emit
End Function

Function LoadASCIIEmitters( Path$ )
	i.XMLNode = ReadXML(Path)
		f.XMLNode = i
		
		While f <> Null
			ParseLPXML(f)
			f = XMLNextNode(f)
		Wend
		
		XMLDeleteNode(i)
		
		Return 1
End Function

Function ParseLPXML(i.xmlNode)
End Function
