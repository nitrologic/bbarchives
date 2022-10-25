; ID: 2512
; Author: Matty
; Date: 2009-06-23 18:28:21
; Title: Sort Algorithm for positive integers
; Description: Fast yet simple method for sorting a list of positive integers (can be extended to negative easily too)

;Program to demonstrate a sorting method (hopefully) for sorting
;a list of numbers between 0 and 16777215 (24 bit) which are all 4-byte integers
;
;
Graphics 800,600,0,2	;Initialise Graphics
SeedRnd 1;MilliSecs()	;Seed Random Number Generator

Const Listcount=1000000	;Set maximum number of elements in list to be sorted -> this can be any number 
Dim List(Listcount) 	;Array to hold our list of unsorted numbers

						
For i=0 To Listcount	;Set the values for our array to a sequence of random numbers
	List(i)=Rand(0,16777215)
Next


Delay 1000 				;Give the system time to breathe before beginning..probably not needed


starttime=MilliSecs()	;Initial starting time.

bank=SortList()			;Call our sorting function here and return the sorted list as a bank

totaltime=MilliSecs()-starttime ; Calculate our total time taken here.



Text 0,0,"Time(in milliseconds):"+totaltime	;Display time taken
Flip						;Refresh the screen
WaitKey						;Wait for keypress
If bank<>0 Then FreeBank bank	;Free bank 
End	



Function SortList()
;Sort Function 
;
;
;
;
;
;


bank=CreateBank(4+listcount*4)	;Bank to hold our sorted list // ML19JAN2015 - EDIT - ADDED 4 Bytes...oops! my error - took someone 5 years to find it!

tempbank=CreateBank(4*16777216)	;Temporary bank to hold the count of each list value -> One 4-byte integer element for 16777216 different values.

For i=0 To listcount	;Generate temporary bank data
	PokeInt(tempbank,list(i)*4,PeekInt(tempbank,list(i)*4)+1)
Next

				
offset=0			
For i=0 To 16777215		;Populate sorted list based on count of each value stored in 'tempbank'.	
	numcount=PeekInt(tempbank,i*4)
	For j=1 To numcount
		PokeInt bank,offset,i		
		offset=offset+4
	Next
Next
FreeBank tempbank		;Free the temporary bank
Return bank				;Return the bank to the main program.
End Function
