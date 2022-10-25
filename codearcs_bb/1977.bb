; ID: 1977
; Author: Diego
; Date: 2007-03-30 23:09:04
; Title: Trinary / Trinität
; Description: The trinary operator as function fpr blitz/ Der Trinitätsoperator als Funktion für Blitz

Function Trinary(Condition%, Valid%, Invalid%)
If Condition% Then Return Valid% Else Return Invalid%
End Function

Function TrinaryFloat#(Condition%, Valid#, Invalid#)
If Condition% Then Return Valid# Else Return Invalid#
End Function

Function TrinaryString$(Condition%, Valid$, Invalid$)
If Condition% Then Return Valid$ Else Return Invalid$
End Function
