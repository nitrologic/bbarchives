; ID: 279
; Author: dirkduck
; Date: 2002-03-22 15:16:34
; Title: Binary Search
; Description: A simple binary search, used to find a value in a large array

Graphics 640,480,16,2 


high=0 ;high element of search
low=0 ;low element of search
middle=0 ;middle value of search
oldmiddle=0 ;used to check if the value couldn't be found
value=0 ;the value to search for
asize=0 ;the size of the array
done=0 ;flag to see if we have finished the search


;gather info
asize=Input("How many elements would you like in the array?: ")
low=0
high=asize-1

value=Input("What value would you like to search for in the array?: ")


Dim array(asize) ;dim the array to be searched

For i=0 To asize-1
	array(i)=i*(2+i)   ;fill each element with a strange number, change it to whatever you want 
	Print "value of array("+Str$(i)+") is: "+Str$(array(i)) ;list all the values
Next

Print "Searching for "+Str$(value)

Repeat
	middle=(high - low)/2 + low ;find the middle value
	Print "middle is "+Str$(array(middle))  ;print the value of middle for 'debug' purposes 
	
 	If(oldmiddle=middle) ;If oldmiddle has equaled middle For more Then 1 loop Then we know that the element couldn't be found
		Print "The value is not stored in any element in this array"
		done=1  ;tell the loop that we are done and to leave
	EndIf

	oldmiddle=middle ;store a value for oldmiddle, used to see if we couldn't find the value
	
	If(array(middle)=value) Print "found it in element "+Str$(middle) ;found the value 
	If(value<array(middle)) Then high=middle ;if the middle is too high Then reset high to middle 
	If(value>array(middle)) Then low=middle ;or if the middle is too low then reset low to middle 


Until ((array(middle)=value)Or(done=1)) ;loop until the middle value is equal to the value we specified or done is true 

WaitKey()
