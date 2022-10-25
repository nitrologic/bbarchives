; ID: 2811
; Author: Baystep Productions
; Date: 2011-01-18 07:45:10
; Title: Draw Shadowed Text
; Description: Draws text with a shadow

Function DrawShadowText(text$,x#,y#,depth%=1,opacity#=0.5)
	Local _r%,_g%,_b%,_a%
	GetColor(_r%,_g%,_b%)	'Get current drawing color and save it.
	_a% = GetAlpha()
	SetColor 0,0,0
	SetAlpha _a%-opacity#		'Requires ALPHABLEND
	DrawText(text$,x#+depth%,y#+depth%)
	SetColor _r%,_g%,_b%
	SetAlpha _a%
	DrawText(text$,x#,y#)
EndFunction
