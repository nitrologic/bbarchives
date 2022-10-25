; ID: 3168
; Author: BlitzSupport
; Date: 2014-12-17 17:38:49
; Title: IsPow2 - Power of two test
; Description: Tests if a number is a power of two

' http://yarchive.net/comp/power_of_two.html

Function IsPow2:Int (value:Int)
	Return Not (value & (value - 1))
End Function

' For > Int values! Both versions can be passed Ints or Longs
' in the demo, though what happens when you exceed the supported
' range is... "unsupported".

Function IsPow2Long:Long (value:Long)
	Return Not (value & (value - 1))
End Function

Print ""
Print "Ints..."
Print ""

For Local loop:Int = 0 To 128
	Print loop + " : " + IsPow2 (loop)
Next

Print ""
Print "Longs..."
Print ""

For Local longloop:Long = 0 To 128
	Print longloop + " : " + IsPow2Long (longloop)
Next
