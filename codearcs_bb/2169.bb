; ID: 2169
; Author: Xzider
; Date: 2007-12-11 14:51:11
; Title: Firework Effect
; Description: Messing around with particles

Graphics3D 800,600,32,2

Global Temp_ParticleID%
Global Dir%
Global Camera% = CreateCamera()

Type Particle

  Field Entity%
  Field TexName$
  Field ID%
  Field ParticleType%
  Field X#
  Field Y#
  Field Z#
  Field Scale0%
  Field Scale1%
  Field Alpha#
  Field Counter0%
  Field Counter1%
  Field AlphaCounter0%
  Field AlphaCounter1%
  Field Color0%
  Field Color1%
  Field Color2%
  Field Dir#
  Field Dir1#
  Field Dir2#
  Field Dir3#
  Field Gravity#

End Type

While Not KeyHit(1)

  If Rand(20) = 1

		X# = Rand(-100,100)
		Y# = Rand(-100,100)
		Z# = Rand(100,250)
		
  For i=1 To 10

		Create_Particle("Spark1.bmp",X#,Y#,Z#,1,1,1,1,255,255,255,1000)
		Create_Particle("Spark2.bmp",X#,Y#,Z#,1,1,1,1,255,255,255,1000)

  Next

  End If


  Cls

  UpdateWorld
  RenderWorld

		Update_Particles()
		
  Flip

Wend


Function Create_Particle(TempName$,TempX#,TempY#,TempZ#,TempAlpha#,TempType%,TempScale0%,TempScale1%,TempColor0%,TempColor1%,TempColor2%,TempCounter%)

		Pa.Particle = New Particle
		Pa\TexName$ = TempName$
		Pa\X# = TempX#
		Pa\Y# = TempY#
		Pa\z# = TempZ#
		Pa\Alpha# = TempAlpha#
		Pa\ParticleType% = TempType%
		Pa\Scale0% = TempScale0%
		Pa\Scale1% = TempScale1%
		Pa\Counter0% = MilliSecs()
		Pa\Counter1% = TempCounter%
		Pa\Alphacounter0% = MilliSecs()
		Pa\AlphaCounter1% = 50
		Pa\Color0% = TempColor0%
		Pa\Color1% = TempColor1%
		Pa\Color2% = TempColor2%
		SeedRnd MilliSecs()
		Pa\Dir1# = Rand(-1,1)
		Pa\Dir2# = Rand(-1,1)
		Pa\Dir3# = Rand(-1,1)
		
  If Dir% = 1

		Dir% = 2
		
  Else

		Dir% = 1
		
  End If

		Pa\Dir# = Dir%

		Pa\Entity% = LoadSprite(Pa\TexName$)
		ScaleSprite Pa\Entity%,Pa\Scale0%,Pa\Scale1%
		EntityAlpha Pa\Entity%,Pa\Alpha#
		PositionEntity Pa\Entity%,Pa\X#,Pa\Y#,Pa\Z#
		
  If Pa\Color0% = 0 And Pa\Color1% = 0 And Pa\Color2% = 0

  Else

		EntityColor Pa\Entity%,Pa\Color0%,Pa\Color1%,Pa\Color2%

  End If

  If Not Pa\Entity%

		RuntimeError "Could not load sprite " + Pa\TexName$ + "!"
		
  End If

End Function

Function Update_Particles()

  For Pa.Particle = Each Particle

Select Pa\ParticleType%

  Case 1

		Pa\Alpha# = Pa\Alpha# - 0.01
		
		EntityAlpha Pa\Entity%,Pa\Alpha#

		MoveEntity Pa\Entity%,Pa\Dir1#,Pa\Dir2#,Pa\Dir3#
		MoveEntity Pa\Entity%,0,Pa\Gravity#,0
		
		Pa\Gravity# = Pa\Gravity# - 0.05

  Default

End Select

  If MilliSecs() > Pa\AlphaCounter0% + Pa\AlphaCounter1%	

		Pa\AlphaCounter0% = MilliSecs()	
		
  End If

  If MilliSecs() > Pa\Counter0% + Pa\Counter1% Or Pa\Alpha# <= 0

		FreeEntity Pa\Entity%
		Delete Pa.Particle

  End If

  Next

End Function

Function DeleteParticles()

  For Pa.Particle = Each Particle

		FreeEntity Pa\Entity%
		Delete Pa.Particle
		
  Next

End Function

Function New_ParticleID()

		Temp_ParticleID% = Temp_ParticleID% + 1
		
End Function
