; ID: 2145
; Author: JoshK
; Date: 2007-11-06 20:51:38
; Title: MathEx Module
; Description: Some much-needed math functions including Curve(), RGBA(), and others

Strict

Rem
bbdoc:
End Rem
Module leadwerks.mathex

ModuleInfo "Version: 1.0.0"
ModuleInfo "Author: Joshua Klint"
ModuleInfo "Copyright: Leadwerks Corporation"
ModuleInfo "www.leadwerks.com"

Import brl.math

Rem
bbdoc:
End Rem
Function Curve:Float(newvalue:Float,oldvalue:Float,increments#)
	Local sign,slip#
	sign=Sgn(oldvalue-newvalue)
	slip#=(oldvalue-newvalue)/increments
	If Abs(slip)<m slip=m*Sgn(slip)
	oldvalue=oldvalue-slip
	If sign<>Sgn(oldvalue-newvalue) Return newvalue
	Return oldvalue
EndFunction

Rem
bbdoc: 
EndRem
Function Radians:Float(Degrees:Float)
	Return Degrees*Pi/180.0
EndFunction

Rem
bbdoc: 
EndRem
Function Degrees:Float(Radians:Float)
	Return Radians/Pi*180.0
EndFunction

Rem
bbdoc:
End Rem
Function CurveAngle#(newangle#,oldangle#,increments#)
	If increments>1
		oldangle=oldangle Mod 360.0
		newangle=newangle Mod 360.0
		If (oldangle+360)-newangle<newangle-oldangle oldangle=360+oldangle
		If (newangle+360)-oldangle<oldangle-newangle newangle=360+newangle
		oldangle=oldangle-(oldangle-newangle)/increments
	EndIf
	If increments<=1 oldangle=newangle
	Return oldangle
EndFunction

Rem
bbdoc:
End Rem
Function Dbl!(val!)
	Return val!
EndFunction

Rem
bbdoc:
End Rem
Function Log2#(num#)
	Return Log(num)/0.693147
End Function

Rem
bbdoc:
EndRem
Function Pow2:Int(val,mode=0)
	Return Round(2^Round(Log2(val)))
EndFunction

Rem
bbdoc:
End Rem
Function Round(val#)
	Local dec#
	dec#=val-Floor(val)
	If dec<0.5 Return Floor(val) Else Return Ceil(val)
EndFunction

Rem
bbdoc:
End Rem
Function Red:Int(hue)
	Return (hue & $000000FF)
EndFunction

Rem
bbdoc:
End Rem
Function Green:Int(hue)
	Return (hue & $0000FF00) Shr 8
EndFunction

Rem
bbdoc:
End Rem
Function Blue:Int(hue)
	Return (hue & $00FF0000) Shr 16
EndFunction

Rem
bbdoc:
End Rem
Function Alpha:Int(hue)
	Return (hue & $FF000000) Shr 24
EndFunction

Rem
bbdoc:
End Rem
Function RGB:Int(r,g,b)
	Return r+(g Shl 8)+(b Shl 16)+(255 Shl 24)
End Function

Rem
bbdoc:
End Rem
Function RGBA:Int(r,g,b,a)
	Return r+(g Shl 8)+(b Shl 16)+(a Shl 24)
End Function

Rem
bbdoc:
End Rem
Function Clamp:Float(value:Float,minvalue:Float=0.0,maxvalue:Float=1.0)
	value=Max(value,minvalue)
	value=Min(value,maxvalue)
	Return value
EndFunction
