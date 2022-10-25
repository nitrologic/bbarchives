; ID: 1177
; Author: aab
; Date: 2004-10-20 16:25:50
; Title: DistantSound()
; Description: Play a sound loaded with loadsound, in a 3d way

Function distantSound(soundHandle,cam,entity,range#=500,volume#=1,inview#=0.7)
	If range#<1 range#=1
	;If Not EntityInView(entity,cam) ChannelVolume soundHandle,0.5
	
;	dis#=Sqr(Abs(EntityX(entity)-EntityX(cam))*Abs(EntityX(entity)-EntityX(cam))+Abs(EntityY(entity)-EntityY(cam))*Abs(EntityY(entity)-EntityY(cam))+Abs(EntityZ(entity)-EntityZ(cam))*Abs(EntityZ(entity)-EntityZ(cam)))
dis#=entitydistance(cam,entity)	
	If 1-dis#/range#>0 And 1-dis#/range#<1
		SoundVolume soundHandle,volume#*(1-dis#/range#)*((1-inview)+(Float#(EntityInView(entity,cam))*inview))
		PlaySound soundHandle

	EndIf
	
End Function






;This one Checks for Co-ordinates rather than entities
Function distantSoundCoOrd(soundHandle,x1,y1,z1,x2,y2,z2,range#=500,volume#=1,inview#=0.7)
	If range#<1 range#=1
	;If Not EntityInView(entity,cam) ChannelVolume soundHandle,0.5
	
	dis#=Sqr(Abs(x2-x1)*Abs(x2-x1))+Abs(y2-y1)*Abs(y2-y1)+Abs(z2-z1)*Abs(z2-z1)

	If 1-dis#/range#>0 And 1-dis#/range#<1
		SoundVolume soundHandle,volume#*(1-dis#/range#)*((1-inview)+(Float#(EntityInView(entity,cam))*inview))
		PlaySound soundHandle

	EndIf
	
End Function
