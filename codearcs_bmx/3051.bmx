; ID: 3051
; Author: BlitzSupport
; Date: 2013-04-19 19:22:34
; Title: Get size of type in bytes
; Description: A way to return the size of a variable/object type

Print SizeOf (Byte (0))
Print SizeOf (Short (0))
Print SizeOf (Int (0))
Print SizeOf (Long (0))
Print SizeOf (Float (0))
Print SizeOf (Double (0))

' Bonus ball! Size of type...

' Example type/class...

Type Test
	Field temp1:Int		' 4 bytes
	Field temp2:Short	' 2 bytes
End Type

Print SizeOf (Test (Null))
