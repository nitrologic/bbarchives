; ID: 3211
; Author: Dan
; Date: 2015-06-24 07:50:31
; Title: Commandline Splitting
; Description: Splits the commandline, usefull for the file handling

; Version: 1.0
; Author: Dan (at www.blitzbasic.com forum) 
; Email: -
; Copyright: PD
; Description: Returns space separated Commandline parameters
;              Parameter in Quotes ".." is passed as is
;              Read the description from the CommandLine_Init function
;====================================================================

;Put type declaration at the start:

Type CMDLine
     Field arg$
End Type

;Demo: uncomment to test, set the commandline in the IDE's options
;BB ide Menu: Program/Program Command Line ...
;c:\bb3d\test.bmp /hlp "c:\bb 3d\file name.txt" c:\windows\win.com /PNG /type bla bla .jpg c:\dd\1.jpg/ lala

;Graphics 1650,1050,32,2
;
;A=Commandline_Init()   ;Must be called first, to split the commandline into parts, it returns a number of Commands.
;;
;Print "Commandline Captured: "+a
;For x=1 To a
;	Print x+" "+Commandline_Read$(x)
;	Delay 100  ; needed only for the demo display slowdown 
;Next
;WaitKey
;/Demo

Function Commandline_Read$(nr)
; Returns the commandline at index nr
	If nr>0
		this.CMDLine = First CMDLine
		For this.CMDLine = Each CMDLine
			x=x+1
			If nr=x Then Return this\arg$
		Next
	End If
	Return False
End Function

Function Commandline_Init()
;This function returns the number of commandline, added to the CMDline type
;To use this function in your program you have to declare the rules for passing the commandline to this app
;
;This function works as :
; Every commandline parameter will be split at the 1st space position
; example:/hlp c:\files\01.bmp 
; will return 2 as parameter value and 
;/hlp
;c:\files\01.bmp
;
;Space characters are stripped from each parameter, but not if they were enclosed in the Double Quote "
; singlequote ' and Doublesinglequote '' are ignored
; if the filename has space chars in it eg: My Birthdayphoto.jpg  the file passed to this program
; from the commandline shall be like this: "My Birthdaysphoto.jpg" (enclosed in quotes)
; This is automaticaly done in windows (8.1) when dropping files onto the .exe
; If the file/pathname contains a space char " " in it, the whole drive:path/filename.ext shall be enclosed in quotes 
; eg. "c:\my files\my file.jpe g"
; This function does not check if the file exists, because the parameter can be nonfile, so it is on your part to do this
; This function returns an value of maximum parameters added, so you can check if anything were added aswell
; and access the single items within the limits of this value 
; Call this function like: A=Commandline_Init()

	qa$=CommandLine()
	CMD_start=1
	CMD_Quote=0
	CMD_index=0
	x=0
	ca$=""
	a$=""
	If Len(qa$)=0 Goto CMDreadEnd
		
		.CMDread
		x=x+1  
		ca$=ca$+Mid$(qa$,x,1)
		
;    Print LSet ("x="+x,6)+":"+Right$(Hex$(Asc(ca$)),2)+" = "+ca$+" --"+CMD_Start+" ->"+CMD_Quote    ; Uncomment for debug		
		a$=Trim(Mid$(qa$,CMD_Start,X-CMD_Start))
		If ca$=" " And CMD_Quote=0
			If CMD_Start=1 
				If a$>""
					CMD_index=CMD_AddType(CMD_Index,a$)
				EndIf
				
				CMD_start=x
			Else
			    A$=Trim(Mid$(qa$,CMD_Start+1,X-CMD_Start))
				If A$>""
					CMD_index=CMD_AddType(CMD_Index,a$)
				EndIf
				
				CMD_Start=x
			EndIf
		EndIf
		
		If ca$=Chr$(34)
			If x-CMD_Start>0 And CMD_Quote=0
				If a$>""
					CMD_index=CMD_AddType(CMD_Index,a$)
				EndIf
				CMD_Start=x
				
			EndIf
			If CMD_Quote=0 
				CMD_Start=x+1
				CMD_Quote=1
			Else
			    If a$>""
					CMD_index=CMD_AddType(CMD_Index,a$)
				EndIf	
				CMD_start=x+1
				CMD_Quote=0
				
			EndIf
		EndIf
		
		If x>Len(qa$)
			If A$>"" 
		  		CMD_index=CMD_AddType(CMD_Index,a$)
			EndIf
			Goto CMDreadEnd
		EndIf
		
		ca$=""
		
		Goto CMDread
		
		.CMDreadEnd
    ;Print CMD_Index																					;Uncomment for debug
		Return CMD_index
		
End Function

Function CMD_AddType(index,A$)
;Used to shorten the code from the Commandline_Init() function
					CMD_index=index+1
					This.Cmdline = New CMDLine
					this\arg$=A$
					Return CMD_index
End Function
