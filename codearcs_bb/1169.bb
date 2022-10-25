; ID: 1169
; Author: big10p
; Date: 2004-10-02 10:56:22
; Title: 2D particle effect
; Description: Pointless but pretty :)

Graphics 800,600
	SetBuffer BackBuffer()

	SeedRnd MilliSecs()
	
	Type particle
		Field x#
		Field y#
		Field speedx#
		Field speedy#
		Field decelx#
		Field decely#
		Field brightness#
		Field fader#
		Field life%
	End Type

	Type sub_particle
		Field x#
		Field y#
		Field speedx#
		Field speedy#
		Field r%,g%,b%
		Field life%
	End Type
	
	Const NUM_PARTICLES=500
	Const PARTICLE_LIFE=130
	Const MAX_SPEED#=10.0
		
	init_particles()

	While Not KeyHit(1)
		Cls

		draw_particles()

		Flip

		;Are there any particles still alive to update?
		If (First particle=Null And First sub_particle=Null) Then
			init_particles()
		Else
			update_particles()
		EndIf

	Wend
	
	End

	
Function init_particles()

	degDir# = 0
	degStep# = 360.0/NUM_PARTICLES
	startx% = GraphicsWidth() Shr 1
	starty% = GraphicsHeight() Shr 1

	Repeat
		this.particle = New particle
		this\x# = startx%
		this\y# = starty%

		randSpeed# = Rnd(.2,10.0)
		this\speedx# = Cos(degDir) * randSpeed#
		this\speedy# = Sin(degDir) * randSpeed#

		decel# = Rnd(50.0,100.0)
		this\decelx# = this\speedx/(randSpeed#*10)
		this\decely# = this\speedy/(randSpeed#*10)

		;Pre-calculate the life of this particle
		;(a particle dies when it comes to a stop)
		sx# = this\speedx#
		dx# = this\decelx#
		sy# = this\speedy#
		dy# = this\decely#
		While (Sgn(sx#-dx#)=Sgn(sx#) And Sgn(sy#-dy#)=Sgn(sy#))
			sx# = sx# - dx#
			sy# = sy# - dy#
			this\life% = this\life% + 1
		Wend

		this\brightness# = 255
		this\fader# = 255.0/this\life%
		
		degDir# = degDir# + degStep#
	Until degDir# >= 360
	
End Function


Function update_particles()

	For that.sub_particle = Each sub_particle
		that\x# = that\x# + that\speedx#
		that\y# = that\y# + that\speedy#

		If (that\life%) Then
			that\life% = that\life% - 1
		Else
			Delete that
		EndIf
	Next

	For this.particle = Each particle
		this\x# = this\x# + this\speedx#
		this\y# = this\y# + this\speedy#

		If (this\life%) Then
			this\speedx# = this\speedx#-this\decelx#
			this\speedy# = this\speedy#-this\decely#
			this\brightness# = this\brightness# - this\fader#
			this\life% = this\life% - 1
		Else
			degDir# = 0.0
			degStep# = 360.0/32.0
			r% = Rand(50,255)		
			g% = 0
			b% = Rand(50,255)		

			Repeat
				spawn.sub_particle = New sub_particle
				spawn\x# = Int(this\x#)
				spawn\y# = Int(this\y#)
		
				spawn\speedx# = Cos(degDir) * Rnd#(2.0,4.0)
				spawn\speedy# = Sin(degDir) * Rnd#(2.0,4.0)

				spawn\life% = 20
				spawn\r% = r%		
				spawn\g% = g%		
				spawn\b% = b%		
				
				degDir# = degDir# + degStep#
			Until degDir# >= 360
			
			Delete this
		EndIf
	Next

End Function


Function draw_particles()

	For this.particle = Each particle
		clr% = this\brightness#
		Color clr%,clr%,clr%
		Rect this\x#,this\y#,3,3
	Next

	For that.sub_particle = Each sub_particle
		Color that\r%,that\g%,that\b%
		Rect that\x#,that\y#,2,2
	Next
	
End Function
