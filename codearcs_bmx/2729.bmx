; ID: 2729
; Author: Madk
; Date: 2010-06-12 13:36:02
; Title: Faster array access
; Description: Faster writing to an array

Import "Array.c"

Extern
	Function Poke(array@@[],index:Long,value@@)
End Extern
