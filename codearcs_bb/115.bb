; ID: 115
; Author: Skully
; Date: 2001-10-27 23:40:46
; Title: 2 and 4 byte compressor
; Description: 2 byte and 4 byte compressor/decompressor

Function crunch2$(num)
	byte2=num Shr 8 And %11111111 
	byte1=num And %11111111
	Return Chr$(byte2)+Chr$(byte1)	
End Function

Function uncrunch2(bytes$)
	byte2=Asc(Left$(bytes,1)) Shl 8
	byte1=Asc(Right$(bytes,1))
	Return byte2 Or byte1
End Function

Function crunch4$(num)
	byte4=num Shr 24 And %11111111 
	byte3=num Shr 16 And %11111111 
	byte2=num Shr 8 And %11111111 
	byte1=num And %11111111 
	Return Chr$(byte4)+Chr$(byte3)+Chr$(byte2)+Chr$(byte1)	
End Function

Function uncrunch4(bytes$)
	byte4=Asc(Left$(bytes,1)) Shl 24
	byte3=Asc(Mid$(bytes,2,1)) Shl 16
	byte2=Asc(Mid$(bytes,3,1)) Shl 8
	byte1=Asc(Right$(bytes,1))
	Return byte4 Or byte3 Or byte2 Or byte1
End Function

