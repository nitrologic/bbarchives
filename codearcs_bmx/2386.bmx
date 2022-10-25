; ID: 2386
; Author: Underwood
; Date: 2008-12-25 17:26:38
; Title: PlaySound (better)
; Description: A PlaySound() function that uses its position to make some 'effects'.

Function PlaySoundBetter(sound:TSound,soundx:Int,soundy:Int,originx:Int,originy:Int,volspread:Float)

	Local channel:TChannel
	Local volume:Float
	Local panvalue:Float

	channel:TChannel = AllocChannel()				        ' allocate a channel

	volume#   = (1 - ((GetDistance(originx,originy,soundx,soundy) * (1 - volspread#)) / 100))
	panvalue# = (2 * ((soundx / GraphicsWidth())) - 1)

		If panvalue# < -1 panvalue# = -1
		If panvalue# > 1 panvalue# = 1

	SetChannelRate(channel:TChannel,Rnd(.8,1.2))				' make each sound original
	SetChannelVolume(channel:TChannel,volume#)
	SetChannelPan(channel:TChannel,panvalue#)

		If volume# > 0 PlaySound(sound:TSound,channel:TChannel)        ' play sound

End Function


Function GetDistance:Float(x1#,y1#,x2#,y2#)

	Return(Sqr(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1))))

End Function
