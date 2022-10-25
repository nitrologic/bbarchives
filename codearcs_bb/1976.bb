; ID: 1976
; Author: Diego
; Date: 2007-03-30 20:33:34
; Title: Min Max
; Description: Get the minor or major of two numbers | Die kleinere oder größere zweier Zahlen erhalten

Function Min(Zahl1%, Zahl2%)
If Zahl1% < Zahl2% Then Return Zahl1% Else Return Zahl2%
End Function

Function MinFloat#(Zahl1#, Zahl2#)
If Zahl1# < Zahl2# Then Return Zahl1# Else Return Zahl2#
End Function

Function MinString$(String1$, String2$)
If String1$ < String2$ Then Return String1$ Else Return String2$
End Function

Function Max(Zahl1%, Zahl2%)
If Zahl1% > Zahl2% Then Return Zahl1% Else Return Zahl2%
End Function

Function MaxFloat#(Zahl1#, Zahl2#)
If Zahl1# > Zahl2# Then Return Zahl1# Else Return Zahl2#
End Function

Function MaxString$(String1$, String2$)
If String1$ > String2$ Then Return String1$ Else Return String2$
End Function

Function InRange(Min%, Value%, Max%)
If Value% < Min% Then Return Min%
If Value% > Max% Then Return Max%
Return Value%
End Function

Function InRangeFloat#(Min#, Value#, Max#)
If Value# < Min# Then Return Min#
If Value# > Max# Then Return Max#
Return Value#
End Function

Function InRangeString$(Min$, Value$, Max$)
If Value$ < Min$ Then Return Min$
If Value$ > Max$ Then Return Max$
Return Value$
End Function
