; ID: 2285
; Author: Perturbatio
; Date: 2008-07-12 11:33:06
; Title: MouseXSpeed, MouseYSpeed and MouseZSpeed
; Description: For help with B3D users converting to BMax

Function MouseXSpeed:Int()
	Global lastX:Int =0
	Local result:Int = MouseX()-lastX
	lastX = MouseX()	
	Return result
End Function


Function MouseYSpeed:Int()
	Global lastY:Int =0
	Local result:Int = MouseY()-lastY
	lastY = MouseY()	
	Return result
End Function


Function MouseZSpeed:Int()
	Global lastZ:Int =0
	Local result:Int = MouseZ()-lastZ
	lastZ = MouseZ()	
	Return result
End Function
