; ID: 1977
; Author: Diego
; Date: 2007-03-30 23:09:04
; Title: Trinary / Trinit�t
; Description: The trinary operator as function fpr blitz/ Der Trinit�tsoperator als Funktion f�r Blitz

Function Trinary(Condition%, Valid%, Invalid%)
If Condition% Then Return Valid% Else Return Invalid%
End Function

Function TrinaryFloat#(Condition%, Valid#, Invalid#)
If Condition% Then Return Valid# Else Return Invalid#
End Function

Function TrinaryString$(Condition%, Valid$, Invalid$)
If Condition% Then Return Valid$ Else Return Invalid$
End Function
