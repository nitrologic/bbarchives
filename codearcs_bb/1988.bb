; ID: 1988
; Author: Jeremy Alessi
; Date: 2007-04-15 22:39:28
; Title: getSubnetMask() &amp; getBroadcastAddress()
; Description: Useful for LAN searches ...

Function getBroadcastAddress$( ip$ )

	subnetMask$ = getSubnetMask()

	bank = CreateBank( 4 )
	byte$ = ""
	j = 1
	For i = 0 To 3
		byte = ""
		While Mid( subnetMask$, j, 1 ) <> "." And j < Len( subnetMask$ )
			byte$ = byte$ + Mid( subnetMask$, j, 1 )
			j = j + 1
		Wend
		j = j + 1
		PokeByte( bank, i, byte$ )
	Next
	
	onesComplement( bank, 0, 4 )
		
	bank2 = CreateBank( 4 )
	byte$ = ""
	j = 1
	For i = 0 To 3
		byte$ = ""
		While Mid( ip$, j, 1 ) <> "." And j < Len( ip$ )
			byte$ = byte$ + Mid( ip$, j, 1 )
			j = j + 1
		Wend
		j = j + 1
		PokeByte( bank2, i, byte$ )
	Next
	
	bank3 = CreateBank( 4 )
	For i = 0 To 3
		PokeByte( bank3, i, ( PeekByte( bank, i ) Or PeekByte( bank2, i ) ) )
	Next
	
	broadcastAddress$ = ( PeekByte( bank3, 0 ) + "." + PeekByte( bank3, 1 ) + "." + PeekByte( bank3, 2) + "." + PeekByte( bank3, 3 ) )

	FreeBank( bank )
	FreeBank( bank2 )
	FreeBank( bank3 )
	
	Return broadcastAddress$


End Function
