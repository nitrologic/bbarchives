; ID: 1749
; Author: fredborg
; Date: 2006-07-11 09:59:12
; Title: Color Module
; Description: Easy conversion between rgb and hsv

SuperStrict

Module Pub.Color

ModuleInfo "Version: 1.00"
ModuleInfo "Authors: Mikkel Fredborg"
ModuleInfo "License: Public Domain"

Import BRL.Math

Type TColor
	
	Method toARGB:Int() Abstract
		
End Type

Type TColorHSV Extends TColor
	
	Field h:Float,s:Float,v:Float,a:Float=1.0
	
	Method toRGB:TColorRGB()

		Local temph:Float = self.h
		Local temps:Float = self.s
		Local tempv:Float = self.v
	
		Local rgb:TColorRGB = New TColorRGB
	
		If temph=>360.0 Or temph<0.0 Then temph = 0.0
	
		If temps = 0 Then
			rgb.r = v
			rgb.g = v
			rgb.b = v
		Else
			temph = temph / 60.0
			
			Local i:Int   = Floor(temph)
			Local f:Float = temph - i
			Local p:Float = tempv * (1 - temps)
			Local q:Float = tempv * (1 - temps * f)
			Local t:Float = tempv * (1 - temps * (1 - f))

			Select i
				Case 0
					rgb.r = v
					rgb.g = t
					rgb.b = p
				Case 1
					rgb.r = q
					rgb.g = v
					rgb.b = p
				Case 2
					rgb.r = p
					rgb.g = v
					rgb.b = t
				Case 3
					rgb.r = p
					rgb.g = q
					rgb.b = v
				Case 4
					rgb.r = t
					rgb.g = p
					rgb.b = v
				Default
					rgb.r = v
					rgb.g = p
					rgb.b = q
			End Select		
		EndIf

		rgb.a = a

		Return rgb
	
	EndMethod
	
	Function fromARGB:TColorHSV(argb:Int)
		
		Return TColorRGB.fromARGB(argb).toHSV()
		
	EndFunction
	
	Method toARGB:Int()
		
		Return self.toRGB().toARGB()

	EndMethod
	
EndType

Type TColorRGB Extends TColor

	Field r:Float,g:Float,b:Float,a:Float=1.0
	
	Method toHSV:TColorHSV()
		
		Local tempr:Float = Min(1.0,Max(0.0,self.r))
		Local tempg:Float = Min(1.0,Max(0.0,self.g))
		Local tempb:Float = Min(1.0,Max(0.0,self.b))

		Local minVal:Float = Min(Min(tempr,tempg),tempb)
		Local maxVal:Float = Max(Max(tempr,tempg),tempb)
		
		Local diff:Float = maxVal - minVal
	
		Local hsv:TColorHSV = New TColorHSV
		hsv.v = maxVal
	
		If maxVal = 0.0 Then
			hsv.s = 0.0
			hsv.h = 0.0
		Else
			hsv.s = diff / maxVal
	
			If tempr = maxVal
				hsv.h = (tempg - tempb) / diff
			ElseIf tempg = maxVal
				hsv.h = 2.0 + (tempb - tempr) / diff
			Else
				hsv.h = 4.0 + (tempr - tempg) / diff
			EndIf
	
			hsv.h = hsv.h * 60.0
			If hsv.h < 0 Then hsv.h = hsv.h + 360.0
		EndIf

		If hsv.h<  0.0 Then hsv.h = 0.0
		If hsv.h>360.0 Then hsv.h = 0.0
		
		hsv.a = a
		
		Return hsv
		
	EndMethod

	Function fromARGB:TColorRGB(argb:Int,alpha:Int=True)
	
		Local rgb:TColorRGB = New TColorRGB
	
		If alpha	
			rgb.a = ((argb Shr 24) & $FF)/255.0
		EndIf
		
		rgb.r = ((argb Shr 16) & $FF)/255.0
		rgb.g = ((argb Shr 8) & $FF)/255.0
		rgb.b = (argb & $FF)/255.0
	
		Return rgb
		
	EndFunction

	Function fromBGR:TColorRGB(argb:Int)
	
		Local rgb:TColorRGB = New TColorRGB
	
		rgb.r = (argb & $000000FF)/255.0
		rgb.g = ((argb Shr 8) & $000000FF)/255.0
		rgb.b = ((argb Shr 16) & $000000FF)/255.0
	
		Return rgb
		
	EndFunction

	Method toARGB:Int()
		
		Local tempr:Int = Min(255,Max(0,Int(self.r*255)))
		Local tempg:Int = Min(255,Max(0,Int(self.g*255)))
		Local tempb:Int = Min(255,Max(0,Int(self.b*255)))
		Local tempa:Int = Min(255,Max(0,Int(self.a*255)))
						
		Return (tempa Shl 24) | (tempr Shl 16) | (tempg Shl 8) | tempb

	EndMethod
	
EndType
