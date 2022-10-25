; ID: 2874
; Author: BlitzSupport
; Date: 2011-07-19 21:13:37
; Title: Determining size of data type
; Description: Shows how many bits a variable is made up of. Possibly useless.

variable:Long = 1

Repeat

	variable = variable Shl 1
	
	count = count + 1
	
Until variable = 0

Print count
