; ID: 2241
; Author: markcw
; Date: 2008-04-11 21:28:48
; Title: FloatToDouble Module for B3D/B+
; Description: Convert floats to doubles and back for DLL functions

;FloatToDouble Module for B3D/B+
;Author: markcw, edited 29 Nov 08

;DLL functions with doubles for parameters can be declared
;as 2 integers instead. Then you can use FloatToDouble to convert a
;float into 2 integers which are the binary equivalent of a double
;but with the same value as the float. Finally you can pass
;the 2 integers to the 2 parameters in the DLL function.
;For example, glDepthRange takes 2 doubles so the decls would be:
;glDepthRange(zNearLo,zNearHi, zFarLo,zFarHi):"glDepthRange"
;Then a 'wrapper' function can be written to take 2 floats:
;bbglDepthRange(zNear#,zFar#)

Function FloatToDouble(value#, dpart = 0)
 ;Converts a float into a double as 2 integers
 ;dpart -> Double flag indicating which part to return, 0=dlo, 1=dhi
 ;Returns a low or high double integer - decimal equivalent of the float
 ;Site: techsupt.winbatch.com/TS/T000001034F21.html

 Local integer, sign, exponent, fraction, dexp, dlo, dhi
 integer = FloatToInt(value#)
 sign = integer And $80000000 ;Sign bit
 exponent = integer And $7F800000 ;8-bit exponent
 fraction = integer And $007FFFFF ;23-bit mantissa
 dexp = ((exponent Shr 23) - 127 + 1023) Shl 20 ;Double exponent
 dlo = (fraction And 7) Shl 29
 dhi = sign Or dexp Or (fraction Shr 3)
 If dpart = 0 Then Return dlo
 Return dhi

End Function

Function DoubleToFloat#(dlo, dhi)
 ;Converts a double as 2 integers into a float
 ;dlo -> Low double integer, dhi -> High double integer
 ;Returns a float - decimal equivalent of the double as 2 integers
 ;Site: techsupt.winbatch.com/TS/T000001034F21.html

 Local dsgn, sign, dexp, exponent, fraction
 dsgn = Abs(dhi Shr 31) ;Double sign
 sign = dsgn Shl 31 ;Sign bit
 dexp = Abs((dhi Shr 20) - (dsgn Shl 11)) ;Double exponent
 exponent = (dexp + 127 - 1023) Shl 23 ;8-bit exponent
 fraction = ((dhi And $000FFFFF) Shl 3) + (dlo Shr 29) ;23-bit mantissa
 Return IntToFloat(sign Or exponent Or fraction)

End Function

Function FloatToInt(value#)
 ;Converts a float into a float as an integer
 ;Returns an integer that is the binary equivalent of the float
 ;Site: wiki.tcl.tk/756

 Local sign, exponent, fraction#
 Local f1f#, f2f#, f3f#, se1, e2f1, f1, f2, f3
 If value# > 0 Then sign = 0 Else sign = 1
 value# = Abs(value#)
 exponent = Int(Floor(Log(value#) / 0.69314718055994529)) + 127
 fraction# = (value# / (2 ^ (exponent - 127))) - 1
 If exponent < 0 Then exponent = 0 : fraction# = 0.0 ;Round off to zero
 If exponent > 255 Then exponent = 255 ;Outside legal range for a float
 fraction# = fraction# * 128.0
 f1f# = Floor(fraction#)
 fraction# = (fraction# - f1f#) * 256.0
 f2f# = Floor(fraction#)
 fraction# = (fraction# - f2f#) * 256.0
 f3f# = Floor(fraction#)
 f1 = Int(f1f#) : f2 = Int(f2f#) : f3 = Int(f3f#)
 se1 = (sign Shl 7) Or (exponent Shr 1) ;Sign and Exponent1
 e2f1 = ((exponent And 1) Shl 7) Or f1 ;Exponent2 and Fraction1
 Return (se1 Shl 24) Or (e2f1 Shl 16) Or (f2 Shl 8) Or f3

End Function

Function IntToFloat#(value)
 ;Converts a float as an integer into a float
 ;Returns a float that is the binary equivalent of the integer
 ;Site: www.cs.princeton.edu/introcs/91float/

 Local sign, exponent, fraction
 sign = (value And $80000000) Shr 31
 exponent = (value And $7F800000) Shr 23
 fraction = value And $007FFFFF
 Return (-1 ^ sign) * (2 ^ (exponent - 127)) * (1 + (fraction / (2 ^ 23)))

End Function
