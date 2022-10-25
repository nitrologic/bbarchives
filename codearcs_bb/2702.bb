; ID: 2702
; Author: Malice
; Date: 2010-04-19 10:20:23
; Title: Bit Fiddling (Blitz3D / B+ version)
; Description: Manipulate/Identify bit values

; Thansk Shortwind for the original. These are useful functions and so I thought it handy to provide the BB version just in case.

;x=the Int with the value, bit is the bit to set, ie. 0-31
; Note: bits start at Bit 0 = 1, Bit 1 =2, Bit 3=4 etc...

Function SetBit%(x%,Bit%)
 	Return x% Or (1 Shl Bit)
End Function

Function ClearBit%(x%,Bit%)
 	Return x% And(x Xor (1 Shl Bit))
End Function

Function ToggleBit%(x%,Bit%)
	Return x%Xor(1 Shl Bit%)
End Function

Function ReadBit(x%,Bit%)
	Return (x% And (1 Shl Bit%))
End Function
