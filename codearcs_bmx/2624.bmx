; ID: 2624
; Author: matibee
; Date: 2009-12-04 06:22:27
; Title: Particles!
; Description: Simple 2d particles

Const MAX_PARTICLES_PER_EMITTER%	= 600


Type Particle
	Field m_fAge:Float
	Field m_fScale:Float
	Field m_fRotation:Float
	Field m_fVelocityX:Float
	Field m_fVelocityY:Float
	Field m_fRed:Float
	Field m_fBlue:Float
	Field m_fGreen:Float
	Field m_fAlpha:Float
	Field m_fX:Float
	Field m_fY:Float
End Type


Type ParticleEmitter

	Field m_freeParticles:TList
	Field m_liveParticles:TList 
	
	Field m_baseParticle:Particle
	
	Field m_fPointWeight:Float = 1.0
	Field m_fScaleFactor:Float = -0.2
	Field m_fRotationFactor:Float = 720.0
	Field m_fVelocityMin:Float = 9.0
	Field m_fVelocityMax:Float = 100.0
	Field m_fRedFactor:Float = 0.0
	Field m_fBlueFactor:Float = 0.0
	Field m_fGreenFactor:Float = 0.0
	Field m_fAlphaFactor:Float = -1.0
	
	Field m_iParticlesPerBurst:Int = 20
	
	Field m_Image:TImage
	Field m_iXHandle:Int, m_iYHandle:Int
	
	Field m_lastBurstTime:Int
	
	Function Create:ParticleEmitter( strUrl:String = "", IgnoreImage:Int = False )
		Local emitter:ParticleEmitter = New ParticleEmitter
		emitter.m_freeParticles = New TList 
		emitter.m_liveParticles = New TList 
		emitter.m_baseParticle = New Particle
		For Local t:Int = 0 To MAX_PARTICLES_PER_EMITTER - 1
			emitter.m_freeParticles.AddLast( New Particle )
		Next
		If ( Len (strUrl) )
			Local inFile:TStream = ReadFile( strUrl )
			While ( inFile And Not Eof( inFile ) )
				'Debugstop
				Local strLine:String = ReadLine( inFile )
				If ( Len(strLine) > 0 And Left( strline, 1 ) <> "'" )
					Local key:String = Left( strLine, Instr( strLine, "=" ) - 1 )
					Local value:String = Right( strLine, Len( strLine ) - Instr( strLine, "=" ) )
					If ( key = "point_weight" )
						emitter.m_fPointWeight = value.ToFloat()
					Else If ( key = "point_scalefactor" )
						emitter.m_fScaleFactor = value.ToFloat()
					Else If ( key = "point_scale" )
						emitter.m_baseParticle.m_fScale = value.ToFloat()
					Else If ( key = "point_rotationfactor" )
						emitter.m_fRotationFactor = value.ToFloat()
					Else If ( key = "point_velocity_min" )
						emitter.m_fVelocityMin = value.ToFloat()
					Else If ( key = "point_velocity_max" )
						emitter.m_fVelocityMax = value.ToFloat()
					Else If ( key = "point_redfactor" )
						emitter.m_fRedFactor = value.ToFloat()
					Else If ( key = "point_red" )
						emitter.m_baseParticle.m_fRed = value.ToFloat()
					Else If ( key = "point_greenfactor" )
						emitter.m_fGreenFactor = value.ToFloat()
					Else If ( key = "point_green" )
						emitter.m_baseParticle.m_fGreen = value.ToFloat()
					Else If ( key = "point_bluefactor" )
						emitter.m_fBlueFactor = value.ToFloat()
					Else If ( key = "point_blue" )
						emitter.m_baseParticle.m_fBlue = value.ToFloat()
					Else If ( key = "point_alphafactor" )
						emitter.m_fAlphaFactor = value.ToFloat()
					Else If ( key = "point_alpha" )
						emitter.m_baseParticle.m_fAlpha = value.ToFloat()
					Else If ( key = "emitter_particles_per_burst" )
						emitter.m_iParticlesPerBurst = value.ToInt()
					Else If ( key = "image" And Not IgnoreImage )
						emitter.m_Image = LoadImage( value, FILTEREDIMAGE )
					Else If ( key = "xhandle" )
						emitter.m_iXHandle = value.ToInt()
					Else If ( key = "yhandle" )
						emitter.m_iYHandle = value.ToInt()
					End If				
				End If
			End While
			CloseFile ( inFile )
			If emitter.m_Image SetImageHandle( emitter.m_Image, emitter.m_iXHandle, emitter.m_iYHandle )
		End If		
		Return emitter
	End Function
	
	Method Draw()
		If ( Not m_Image ) Return 
		For Local p:Particle = EachIn m_liveParticles
			SetScale p.m_fScale, p.m_fScale
			SetRotation p.m_fRotation
			SetAlpha p.m_fAlpha
			SetColor( p.m_fRed * 255.0, p.m_fGreen * 255.0, p.m_fBlue * 255.0 )
			DrawImage( m_Image, p.m_fX, p.m_fY )
		Next 
	End Method
	
	Method Update( fTime:Float )
		For Local p:Particle = EachIn m_liveParticles
			p.m_fAlpha :+ m_fAlphaFactor * fTime
			p.m_fScale :+ m_fScaleFactor * fTime
			If ( p.m_fAlpha <= 0 Or p.m_fScale <= 0 )
				m_freeParticles.AddLast( p )
				m_liveParticles.Remove( p )
			Else
				p.m_fAge :+ fTime
				p.m_fRotation :+ m_fRotationFactor * fTime
				p.m_fX :+ p.m_fVelocityX * fTime
				p.m_fY :+ p.m_fVelocityY * fTime
				p.m_fVelocityX :- m_fPointWeight * fTime
				p.m_fVelocityY :- m_fPointWeight * fTime
				p.m_fY :+ m_fPointWeight * (p.m_fAge * p.m_fAge)
				p.m_fRed :+ m_fRedFactor * fTime
				p.m_fBlue :+ m_fBlueFactor * fTime
				p.m_fGreen :+ m_fGreenFactor * fTime
			End If 
		Next 
		Assert ( m_freeParticles.Count() + m_liveParticles.Count() = MAX_PARTICLES_PER_EMITTER ) 'ensure particles don't exist in both lists!!
	End Method
	
	Method DoBurst( iX:Int, iY:Int, fpsSync:Int = True )
		If ( fpsSync )
			Local timeNow:Int = MilliSecs()
			If ( timeNow - m_lastBurstTime ) >= 20
				m_lastBurstTime = timeNow
			Else 
				Return 
			End If 
		EndIf 
		
		Local count:Int = m_iParticlesPerBurst
		For Local p:Particle = EachIn m_freeParticles
			MemCopy( p, m_baseParticle, SizeOf( Particle ) )
			p.m_fVelocityX = m_fVelocityMin + ( RndFloat() * ( m_fVelocityMax - m_fVelocityMin ) )
			p.m_fVelocityY = m_fVelocityMin + ( RndFloat() * ( m_fVelocityMax - m_fVelocityMin ) )
			p.m_fX = iX
			p.m_fY = iY
			m_liveParticles.AddLast( p )
			m_freeParticles.Remove( p )
			count :- 1
			If ( count <= 0 )
				Return
			End If
		Next 
	End Method

End Type
