; ID: 2958
; Author: Pineapple
; Date: 2012-07-05 11:17:51
; Title: Very fast sin/cos
; Description: Grab sin/cos values from a lookup table

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--


' Examples of usage. Use the highest example for each function whenever possible for maximum speed

Import "costable.bmx" ' Generate this file using the function provided below

' stp_ must be equal to whatever you used for precision when you generated the table file
Const stp_%=8
Const stm_%=360*stp_,sta_%=stm_-90*stp_

Local angle%=90

' cosine
	
	' when your value will always be in the range [0,360)
	Print costable[Int( angle *stp_)]
	
	' when your value will always be in the range [0,infinity)
	Print costable[Int( angle *stp_) Mod stm_]
	
	' works for all values
	Print costable[((Int( angle *stp_) Mod stm_)+stm_) Mod stm_]

' sine
	
	' when your value will always be in the range [0,infinity)
	Print costable[(Int( angle *stp_)+sta_) Mod stm_]
	
	' works for all values
	Print costable[(((Int( angle *stp_)+sta_) Mod stm_)+stm_) Mod stm_]



' Use this to generate a BlitzMax source file containing a table which you'll import
' Higher numbers for precision result in more memory usage but less error. 8 should be adequate for most purposes.
Function MakeTableFile(path$,precision%)
	Local f:TStream=WriteFile(path)
	WriteLine f,"' Cosine lookup table courtesy of code written by Sophie Kirschner. (meapineapple@gmail.com)"
	Local s$="["
	For Local x%=0 Until 360*precision
		s:+Cos(x/Double(precision))+"!"
		If x<360*precision-1 Then 
			s:+"," 
			If x Mod 4=3 Then s:+" .. ~n"
		Else 
			s:+" ]"
		EndIf
	Next
	WriteLine f,"Global costable![] = "+s
	CloseFile f
End Function
