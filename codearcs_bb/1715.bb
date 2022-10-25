; ID: 1715
; Author: Andres
; Date: 2006-05-16 04:52:51
; Title: Simple particle engine
; Description: Supports frames, duration and velocity

Type particle
	Field image%, x#, y#, xs#, ys#, w%, h%, time%, duration%, frames%
End Type

Global Inerts# = .9

Function CreateParticle(image%, x#, y#, xs#, ys#, duration%, frames% = 1)
	this.particle = New particle
		this\image% = CopyImage(image%)
		this\x# = x#
		this\y# = y#
		this\xs# = xs#
		this\ys# = ys#
		this\w% = ImageWidth(this\image%)
		this\h% = ImageHeight(this\image%)
		this\time% = MilliSecs()
		this\duration% = duration%
		this\frames% = frames%
End Function

Function DrawParticles()
	For par.particle = Each particle
		If par\frames% = 1
			DrawImage par\image%, par\x% - par\w% / 2, par\y% - par\h% / 2
		Else
			frame% = (par\frames% - 1) * (Float (MilliSecs() - par\time%) / par\duration%)
			If frame% > par\frames% Then frame% = par\frames%
			DrawImage par\image%, par\x% - par\w% / 2, par\y% - par\h% / 2, frame%
		EndIf
	Next
End Function

Function UpdateParticles()
	For par.particle = Each particle
		If MilliSecs() - par\time% < par\duration% Then
			par\x# = par\x# + par\xs#
			par\y# = par\y# + par\ys#
			
			par\xs# = par\xs# * Inerts#
			par\ys# = par\ys# * Inerts#
		Else
			FreeImage par\image%
			Delete par
		EndIf
	Next
End Function
