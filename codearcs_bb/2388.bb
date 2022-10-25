; ID: 2388
; Author: H. T. U.
; Date: 2008-12-25 17:41:33
; Title: Simple particles
; Description: A basic particle system for beginners

Global totalparticles;Total number of particles ever created



Type emitter
	
	Field pt
	Field mx#,my#,mz#
	Field rmx#,rmy#,rmz#
	Field pitch#,yaw#,roll#
	Field rpitch#,ryaw#,rroll#
	Field fade
	
End Type

Type particle
	
	Field sprite
	Field emitter
	Field mx#,my#,mz#
	Field pitch#,yaw#,roll#
	Field alpha#
	Field fade
	Field life
	Field ctime
	Field id
	
End Type





Function createemitter(mx#,my#,mz#,pitch#,yaw#,roll#,rm#=0,rr#=0,fade=True);Create an emitter

	
	e.emitter=New emitter
	e\pt=CreatePivot();emitter entity
	;particle motion and rotation(including random amounts)
	e\mx=mx
	e\my=my
	e\mz=mz
	e\rmx=rm
	e\rmy=rm
	e\rmz=rm
	e\pitch=pitch
	e\yaw=yaw
	e\roll=roll
	e\rpitch=rr
	e\ryaw=rr
	e\rroll=rr
	e\fade=fade;particle fading


	
	Return e\pt
	
End Function

Function createparticle(emitter,life#);Create a particle
	;get the emitter
	For e.emitter=Each emitter
		If e\pt=emitter
			a=Handle(e.emitter)
		EndIf
	Next
	emtr.emitter=Object.emitter(a)
	
	If emtr.emitter=Null RuntimeError "Emitter doesn't exist";stop the program if the emitter doesn't exist
			
			totalparticles=totalparticles+1;update the particle count
			
			TFormVector emtr\mx,emtr\my,emtr\mz,emtr\pt,0;keep the particle on track no matter what the emitter's rotation
						
			p.particle=New particle
			p\sprite=CreateSprite()
			p\emitter=emitter
			;particle movement and rotation values	
			p\mx=TFormedX()+Rnd(-(emtr\rmx),emtr\rmx)
			p\my=TFormedY()+Rnd(-(emtr\rmy),emtr\rmy)
			p\mz=TFormedZ()+Rnd(-(emtr\rmz),emtr\rmz)
			p\pitch=emtr\pitch+Rnd(-(emtr\rpitch),emtr\rpitch)
			p\yaw=emtr\yaw+Rnd(-(emtr\ryaw),emtr\ryaw)
			p\roll=emtr\roll+Rnd(-(emtr\rroll),emtr\rroll)
			p\alpha=1
			;particle fading and life values
			p\fade=emtr\fade
			p\life=life
			p\ctime=MilliSecs()
			p\id=totalparticles;used for getparticle

			PositionEntity p\sprite,EntityX(emtr\pt),EntityY(emtr\pt),EntityZ(emtr\pt)
		
			Return p\sprite

End Function

Function particlealpha(particle,alpha#);changes the particles base alpha (required for proper fading)

	For p.particle=Each particle
		If p\sprite=particle p\alpha=alpha;set the particle's base alpha to alpha
	Next
	
End Function

Function updateparticles();update every particle's movement, rotation, and fading

	For p.particle=Each particle
		If p.particle<>Null
		
			TranslateEntity p\sprite,p\mx,p\my,p\mz
			TurnEntity p\sprite,p\pitch,p\yaw,p\roll
		
			If p\fade=True;if the particle is supposed to fade out of existence
			
				alpha#=p\alpha-(p\alpha*(MilliSecs()-p\ctime)/p\life)

			EndIf
			EntityAlpha p\sprite,alpha
			
			If alpha<=0;if the particle's alpha is zero or less
				freeparticle(p\sprite)
			EndIf
					
		EndIf
	Next
	
End Function

Function countparticles(emitter);returns the number of particles produced by an emitter currently in existence
	
	For p.particle=Each particle
		If p\emitter=emitter;if emitter is the particle's emitter
			cnt=cnt+1
		EndIf			
	Next
	
	Return cnt
				
End Function

Function getparticle(id);

	For p.particle=Each particle
		If p.particle<>Null And p\id=id;if the particle exists and it's id equals id			
			Return p\sprite
		EndIf
	Next			

End Function

Function partmovex#(particle);returns a particle's x movement
		
	For p.particle=Each particle
		If p.particle<>Null And p\sprite=particle;if the particle exists and it's sprite is the supplied particle
			Return p\mx
		EndIf
	Next
	
End Function

Function partmovey#(particle);returns a particle's y movement
		
	For p.particle=Each particle
		If p.particle<>Null And p\sprite=particle;if the particle exists and it's sprite is the supplied particle
			Return p\my
		EndIf
	Next
	
End Function

Function partmovez#(particle);returns a particle's z movement
		
	For p.particle=Each particle
		If p.particle<>Null And p\sprite=particle;if the particle exists and it's sprite is the supplied particle
			Return p\mz
		EndIf
	Next
	
End Function

Function partpitch#(particle);returns a particle's pitch rotation
		
	For p.particle=Each particle
		If p.particle<>Null And p\sprite=particle;if the particle exists and it's sprite is the supplied particle
			Return p\pitch
		EndIf
	Next
	
End Function

Function partyaw#(particle);returns a particle's yaw rotation
		
	For p.particle=Each particle
		If p.particle<>Null And p\sprite=particle;if the particle exists and it's sprite is the supplied particle
			Return p\yaw
		EndIf
	Next
	
End Function

Function partroll#(particle);returns a particle's roll rotation
		
	For p.particle=Each particle
		If p.particle<>Null And p\sprite=particle;if the particle exists and it's sprite is the supplied particle
			Return p\roll
		EndIf
	Next
	
End Function

Function modifyparticle(particle,mx#,my#,mz#,pitch#=0,yaw#=0,roll#=0,life=1000,fade=True,rel=False);changes a particle after it's created

	For p.particle=Each particle
		If p.particle<>Null And p\sprite=particle;if the particle exists and it's sprite is the supplied particle
			If rel=False;if the change isn't relative
				p\mx=mx
				p\my=my
				p\mz=mz
				p\pitch=pitch
				p\yaw=yaw
				p\roll=roll
				p\life=life
				p\fade=fade
			Else;if it is
				p\mx=p\mx+mx
				p\my=p\my+my
				p\mz=p\mz+mz
				p\pitch=p\pitch+pitch
				p\yaw=p\yaw+yaw
				p\roll=p\roll+roll
				p\life=p\life+life
				If fade=False p\fade=Not p\fade
			EndIf
		EndIf
	Next
	
End Function

Function freeparticle(particle);removes a particle

	For p.particle=Each particle
		If p\sprite=particle
			FreeEntity p\sprite
			Delete p
		EndIf
	Next

End Function

Function modifyemitter(emitter,mx#,my#,mz#,pitch#=0,yaw#=0,roll#=0,rm#=0,rr#=0,fade=True,rel=False);changes an emitter after it's created
	;get the emitter
	For e.emitter=Each emitter		
		If e\pt=emitter
			a=Handle(e.emitter)
		EndIf
	Next
	emtr.emitter=Object.emitter(a)

	If emtr.emitter=Null RuntimeError "Emitter doesn't exist";stop the program if the emitter doesn't exist

	If rel=False;if the change isn't relative
		emtr\mx=mx
		emtr\my=my
		emtr\mz=mz
		emtr\rmx=rm
		emtr\rmy=rm
		emtr\rmz=rm
		emtr\pitch=pitch
		emtr\yaw=yaw
		emtr\roll=roll
		emtr\rpitch=rr
		emtr\ryaw=rr
		emtr\rroll=rr
		emtr\fade=fade
	Else;if it is
		emtr\mx=emtr\mx+mx
		emtr\my=emtr\my+my
		emtr\mz=emtr\mz+mz
		emtr\rmx=emtr\rmx+rm
		emtr\rmy=emtr\rmy+rm
		emtr\rmz=emtr\rmz+rm
		emtr\pitch=emtr\pitch+pitch
		emtr\yaw=emtr\yaw+yaw
		emtr\roll=emtr\roll+roll
		emtr\rpitch=emtr\rpitch+rr
		emtr\ryaw=emtr\ryaw+rr
		emtr\rroll=emtr\rroll+rr
		If fade=True emtr\fade=Not emtr\fade
	EndIf	
End Function

Function modifyemitterrnd(emitter,rmx#,rmy#,rmz#,rpitch#,ryaw#,rroll#);change an emitter's random values in depth
	;get the emitter
	For e.emitter=Each emitter		
		If e\pt=emitter
			a=Handle(e.emitter)
		EndIf
	Next
	emtr.emitter=Object.emitter(a)
	
	emtr\rmx=rmx
	emtr\rmy=rmy
	emtr\rmz=rmz
	emtr\rpitch=rpitch
	emtr\ryaw=ryaw
	emtr\rroll=rroll
	
End Function

Function freeemitter(emitter);removes an emitter
	
	For e.emitter=Each emitter
		If e\pt=emitter
			FreeEntity e\pt
			Delete e
		EndIf
	Next
	
End Function

Function clearparticlesystem();removes the entire particle system

	For e.emitter=Each emitter
		If e<>Null
			If e\pt=True FreeEntity e\pt
			Delete e
		EndIf
	Next
	
	For p.particle=Each particle
		If p<>Null
			If p\sprite=True FreeEntity p\sprite
			Delete p
		EndIf
	Next

End Function
