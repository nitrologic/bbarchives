; ID: 948
; Author: Ross C
; Date: 2004-02-27 12:01:34
; Title: Fireworks
; Description: Functions and working code to generate fireworks

Graphics 800,600
SetBuffer BackBuffer()


Type shot
	Field x#,y#
	Field speed#
	Field distance
	Field travelled#
	Field cr,cg,cb
End Type

Type particle
	Field x#,y#
	Field angle#
	Field speed#
	Field time
	Field timer
End Type


While Not KeyHit(1)
	Cls
								;     x             y           speed      distance
	If KeyHit(2) Then fire_shot(Rnd(10,700) , Rnd(200,600) , Rnd(0.5,3) , Rnd(50,180))
	
	
	update_shots()
	update_particles()
	Flip
Wend
End


Function fire_shot(x#,y#,speed#,distance#)
	s.shot=New shot
	s\x			= x
	s\y			= y
	s\speed		= speed
	s\distance	= s\y-distance
	s\cr		= Rand(100,255)
	s\cg		= Rand(100,155)
	s\cb		= Rand(10,100)

End Function

Function update_shots()
	For s.shot=Each shot
		s\y=s\y-s\speed ; move shot
		Color s\cr,s\cg,s\cb ; set color
		Rect s\x,s\y,2,2; draw particle
		
		If s\y<s\distance Then
			trnd=Rand(1,6); number of Rings of particles the shot will produce (min,max)
			For mloop=1 To trnd; loop for the number of rings of particles the shot will produce (min,max)
				speed#=Rnd(0.2,2); rnd speed for each ring of particles
				incr#=Rnd(10,70); angle/spacing between each particle
				For loop=0 To incr; loop thru each particle in the loop, and create
					create_new_particle(s\x,s\y,Rnd(speed*0.8,speed*1.2),loop*(360/incr))
				Next
			Next
			Delete s.shot
		End If
		
	Next
End Function

Function create_new_particle(x#,y#,speed#,angle)
	p.particle=New particle
	p\x=x
	p\y=y
	p\speed=speed
	p\angle=angle
	p\timer=MilliSecs()
	p\time=Rand(500,2000)
End Function

Function update_particles()
	For p.particle=Each particle
		p\x=p\x+Cos(p\angle)*p\speed ; move particle
		p\y=p\y+Sin(p\angle)*p\speed ; move particle
		temp=Rand(100,255) ; random red color
		temp1=Rand(10,temp) ; random green colour, based on the result of the random red colour
		Color temp,temp1,Rand(10,190) ; set colour
		Rect p\x,p\y,Rand(1,4),Rand(1,4) ; set colour
		If MilliSecs()>p\timer+p\time Then
			Delete p.particle
		End If
	Next
End Function
