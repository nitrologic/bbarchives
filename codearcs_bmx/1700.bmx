; ID: 1700
; Author: Nilium
; Date: 2006-05-06 01:38:27
; Title: Signed Byte &amp; Short Functions
; Description: Functions to convert integers to and from signed shorts & bytes

' Signed Short -> Int
Function ShortInt%( s@@ )
    Return (s Shl 16) Sar 16
End Function

' Int -> Signed Short
Function IntShort@@( i% )
    Return ((i&$80000000) Shr 16)|(i&$7FFFFFFF)
End Function

' Signed Byte -> Int
Function ByteInt%( s@ )
    Return (s Shl 24) Sar 24
End Function

' Int -> Signed Byte
Function IntByte@@( i% )
    Return ((i&$80000000) Shr 24)|(i&$7FFFFFFF)
End Function
