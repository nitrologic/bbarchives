; ID: 2984
; Author: Radman
; Date: 2012-10-12 23:45:34
; Title: Text-to-Speech (PC)
; Description: Turns text to speech

;The Text-to-Speech Program for the PC
;          Jason Hendricks            

;Types
;This type allows you to have more than one text-to-speech at once! (The program will crash without this...)
Global a = 0 ;This is the accumulator variable that is required for the text-to-speech function
Type Voice
Field file ;The file handle
End Type

;Ask "What do you want me to say?"
MyText$ = Input$("What do you want to say? ")
Say(MyText$) ;This command turns the text "Voice$" into speech.
Print "You are now saying:"
Print MyText$
Delay 10000 ;Wait for a while. (If you remove this, it still talks!)

;Functions
;Speak Function (If you paste this function and the type "Say" to your program then you can use the command Say(text$) to turn your text to speech!)
Function Say(words$)
v.Voice = New Voice ;Make a new file handle...
a = a + 1 ;Write to the next vbs file...
	
	;Write a .vbs file to execute speech
	v\file = WriteFile("C:\temp\" + Str$(a) + ".vbs")
		WriteLine(v\file,"Set objVoice = CreateObject(" + Chr$(34) + "SAPI.SpVoice" + Chr$(34) + ")") ;Activate the text-to-speech function
		WriteLine(v\file,"ObjVoice.speak " +Chr$(34) + words$ + Chr$(34)) ;"words$" is the text that is turned to speech
		CloseFile(v\file) ;Save the file
	ExecFile("C:\temp\" + Str$(a) + ".vbs") ;Turn the text into speech!
End Function

;If you want to use this in your program, copy the type and function into your program.
;Jason Hendricks
