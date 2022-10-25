; ID: 63
; Author: Warpy
; Date: 2001-09-30 05:33:46
; Title: Flags
; Description: How to store the values of many flags in one number

While 1
	;show instructions
	Print "flag 1 - man"
	Print "flag 2 - mouse"
	Print "flag 4 - blue"
	Print "flag 8 - red"
	;user input a number, which is the values of the flags selected added together, eg 
	num=Input("number: ")
	
	;check if flags are set
	If num And 1 Then Print "man!"
	If num And 2 Then Print "mouse!"
	If num And 4 Then Print "blue!"
	If num And 8 Then Print "red!"
	;..repeat for as many powers of two as you want
	
	;this works because of the way computers store numbers.
	;Numbers are stored as a string of 0s and 1s, with each power of two being represented by one of the bits in the string
	;eg 1 is shown as 0001 and 2 is shown as 0010
	;to make numbers other than powers of two, you just add them together
	;eg 3 is 0011 (2+1) and 7 is 0111 (4+2+1)

	;the AND operation returns a number which is made by checking each bit in one number with the corresponding bit in another number.
	;If they're both 1, Then the corresponding bit in the number returned is set To 1. Otherwise, it's set To 0
	;This allows you to see whether any power of 2 is used to make a number
	;and so store the values of lots of flags (True/False values) in one number
	
	;Here, the code checks for flags 1,2,4 and 8
	;So, if you entered the number 9 (1001), 9 AND 1 works like this:
	;1001
	;1000
	;returns
	;1000
	;Similarly, 9 AND 1 does this:
	;1001
	;1000
	;returns
	;0001
	
	;Very clever, no? :)
Wend
