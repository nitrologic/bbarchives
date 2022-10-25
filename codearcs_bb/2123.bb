; ID: 2123
; Author: UUICEO
; Date: 2007-10-20 11:59:25
; Title: String splitting function
; Description: Will split an undetermined # of items from a string

;	By Robert Quackenbush
;

Dim SplitData$(1); This is the array that will contain the returned information

;	This is sample code to demonstrate how the function works

Junk$ = "This will be line #1,This is the next line,Line #3 here,And so on"

; Here is the call to the function, the variable 'totalSplits' will contain the # of items being returned in the array 'SplitData$()

totalSplits = Split( Junk$ )

;	This will display the returned information 

For i = 1 To totalSplits
	Print SplitData$(i)
Next
WaitKey()

;	Here is the function that does all the work.

Function Split(DataToSplit$)

;Declare our variables

Local splitcounter = 0, pntr = -1, pntr2 = 0, counter = 0

;	Continue to check for splits until we reach the end of the string
	
	While pntr <> 0
		pntr = Instr(DataToSplit$, ",", pntr + 1)

;	If we found a split then add 1 to the counter

		If pntr > 0 Then splitcounter = splitcounter + 1
	Wend

;	Now we have the amount of items (-1) in the string so we can dimension our array

	Dim SplitData$(splitcounter + 1)

;	the first item in the string is handled differently than the rest since there is no comma preceding it

	pntr = 0:pntr2 = Instr(DataToSplit$, ",", 0)
	SplitData$(1) = Left$(DataToSplit$, pntr2 - 1)

;	After splitting off the first item we move on to the rest of the string.

	For counter = 2 To splitcounter

;	These 2 pointers keep track of the beginning and ending of the item we are looking for in the string.

		pntr = pntr2 + 1
		pntr2 = Instr(DataToSplit$, ",", pntr)

;	Now that we have the items location we can pull it from the main string and insert it in to our return array

		SplitData$(counter) = Mid$(DataToSplit$, pntr, (pntr2 - pntr))
	Next

;	Once we have done every instance of the comma delimiters we are left with the very end of the string

;	This also is handled differently than the rest of the string since there is no comma after it.

	SplitData$(counter) = Right$(DataToSplit$, Len(DataToSplit$) - pntr2)

;	Now we can return the # of items located in the string back to the calling program.

	Return (splitcounter + 1)
End Function
