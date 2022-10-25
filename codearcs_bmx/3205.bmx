; ID: 3205
; Author: AdamStrange
; Date: 2015-05-16 06:34:41
; Title: Dynamic Arrays
; Description: how to manage automatic resizing arrays

local myArraySize:int = 100
local myArray:myType[myArraySize]

'now to check and resize the array. the array is increased in steps of 50
local newsize:int = 250
While newsize => myArraySize
	myArraySize :+ 50

	myArray = myArray[..myArraySize]
Wend
