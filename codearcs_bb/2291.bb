; ID: 2291
; Author: _33
; Date: 2008-07-28 23:04:18
; Title: PokeByteAdd / PokeByteSubtract
; Description: Instead of poking a value, Poke an operation!

Function PokeByteAdd(address%, disp%, value%, bind = True)
	Local byte% = PeekByte(address, disp) + value
	If bind = True Then
		If byte > 255 Then byte = 255
	EndIf
	PokeByte (address, disp, byte)
End Function

Function PokeByteSubtract(address%, disp%, value%, bind = True)
	Local byte% = PeekByte(address, disp) - value
	If bind = True Then
		If byte < 0 Then byte = 0
	EndIf
	PokeByte (address, disp, byte)
End Function
