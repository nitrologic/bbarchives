; ID: 3054
; Author: starfox
; Date: 2013-05-23 11:17:32
; Title: Read a Double from File
; Description: Read an Intel style Double from a File

Function ReadDouble#(file) ;Converts double to float
Local byte[7]
For count = 0 To 7 ;Read the 8 byte /64 bit double
byte[count] = 0
byte[count] = ReadByte(file)
Next
Local floatbank = CreateBank(4)
Local expd=0,expf=0
Local fb[3]
expd = ((byte[7] And 127) Shl 4) + ((byte[6] And 240) Shr 4)
If expd
	expf = expd - 1024 + 128
Else
	expf = 0
EndIf
fb[3] = (byte[7] And 128) + (expf Shr 1)
fb[2] = ((expf And 1) Shl 7) + ((byte[6] And 15) Shl 3) + ((byte[5] And %11100000) Shr 5)
fb[1] = ((byte[5] And %00011111) Shl 3) + ((byte[4] And %11100000) Shr 5)
fb[0] = ((byte[4] And %00011111) Shl 3) + ((byte[3] And %11100000) Shr 5)
PokeByte(floatbank,0,fb[0]):PokeByte(floatbank,1,fb[1]):PokeByte(floatbank,2,fb[2]):PokeByte(floatbank,3,fb[3])
Local flt# = PeekFloat(floatbank,0)
FreeBank floatbank
Return flt
End Function
