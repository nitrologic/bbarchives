; ID: 101
; Author: Klapster
; Date: 2001-10-14 15:22:24
; Title: Hex-2-Dec
; Description: Converts a Hexidecimal color code into RGB decimal values

;+++++++++++++++++++++++++++++++++++++
;+----====::::Hex-2-Dec:::::====-----+
;+++++++++++++++++++++++++++++++++++++

;----By Tom Klapiscak
;-------Afro Productions 2001
;Feel free to use/alter this function but some credit would be nice
;E-mail comments suggestions to klapster22@hotmail.com

;Hope you find it useful!

;The three variables the numbers are assigned to are Rtot,Gtot and Btot
;These are the three RGB variables extracted from the hex number
;Ie, #10011A would be convert to 16(Rtot), 1(Gtot), 26(Btot)




;-----Example code using Hextodec function below
;You must declare the following variables as global for this code to work
Global Rtot,Gtot,Btot

Value_Hex$="#10011A";Declare the Hex Value
HextoDec(Value_Hex$);Put this Hex value into the function

;Output the results
Print "The Hex Value is "+Value_Hex$

Print " "

Print "In RGB the values are:"
Print " R = "+Rtot
Print " G = "+Gtot
Print " B = "+Btot





;----Converts a hex number to 3 decimal RGB numbers------
;Usage : HextoDec(Hex value string)
Function hextoDec(hexval$)

	;Split hex value up into r,g and b parts 1(1s) and 2(16s)
	r2$=Mid(hexval$,2,1)
	g2$=Mid(hexval$,4,1)	
	b2$=Mid(hexval$,6,1)

	r1$=Mid(hexval$,3,1)
	g1$=Mid(hexval$,5,1)
	b1$=Mid(hexval$,7,1)
	
	
	;Work out values
	r1no=r1$
	If r1$="A" Then r1no=10
	If r1$="B" Then r1no=11
	If r1$="C" Then r1no=12
	If r1$="D" Then r1no=13
	If r1$="E" Then r1no=14
	If r1$="F" Then r1no=15

	r2no=r2$
	If r2$="A" Then r2no=10
	If r2$="B" Then r2no=11
	If r2$="C" Then r2no=12
	If r2$="D" Then r2no=13
	If r2$="E" Then r2no=14
	If r2$="F" Then r2no=15
	
	r2no=r2no*16
	Rtot=r2no+r1no



	g1no=g1$
	If g1$="A" Then g1no=10
	If g1$="B" Then g1no=11
	If g1$="C" Then g1no=12
	If g1$="D" Then g1no=13
	If g1$="E" Then g1no=14
	If g1$="F" Then g1no=15
	
	g2no=g2$
	If g2$="A" Then g2no=10
	If g2$="B" Then g2no=11
	If g2$="C" Then g2no=12
	If g2$="D" Then g2no=13
	If g2$="E" Then g2no=14
	If g2$="F" Then g2no=15
	
	g2no=g2no*16
	gtot=g2no+g1no


	b1no=b1$
	If b1$="A" Then b1no=10
	If b1$="B" Then b1no=11
	If b1$="C" Then b1no=12
	If b1$="D" Then b1no=13
	If b1$="E" Then b1no=14
	If b1$="F" Then b1no=15
	
	b2no=b2$
	If b2$="A" Then b2no=10
	If b2$="B" Then b2no=11
	If b2$="C" Then b2no=12
	If b2$="D" Then b2no=13
	If b2$="E" Then b2no=14
	If b2$="F" Then b2no=15
	
	b2no=b2no*16
	btot=b2no+b1no

	
End Function
