; ID: 1379
; Author: Tibit
; Date: 2005-05-21 08:42:55
; Title: Input in Graphics Mode
; Description: Use the normal Input in graphics mode without stopping your app

'Strict

Rem 
bbdoc: Input in GraphicsMode
End Rem 

' Uncomment the four lines below to use this as a module (you have to build it then)
'Module Pub.input

'ModuleInfo "Version: 1"
'ModuleInfo "Author: Wave~"
'ModuleInfo "License: Blitz Shared Source Code and Public Domain"

Import BRL.Max2D
Import BRL.Retro

Rem 
bbdoc: InputText works just as a normal input but in graphicsmode. It waits for you to press enter then returns a string.
endrem 
Function InputText$(Text$,X,Y)
Local Inp$
	Repeat
		Inp = TInput.Text(Text$,X,Y)
		Flip;Cls
	Until Inp <> ""

Return Inp
EndFunction

Rem 
bbdoc: InputText works just as a normal Textinput but it does NOT stop the program! Returns "" until ENTER is pressed then the message you have written is returned as a string.
endrem 
Function DynamicInput$(Text$,X,Y)
	Return TInput.Text(Text$,X,Y)
EndFunction

Private
Type TInput

	Global tempText$

	Function Text$(Text$,X,Y)
		
			Local aKeytoGet = GetChar()
			If aKeytoGet'Anykey was pressed
			
				If aKeytoGet = 13 'ENTER
					Text$ = tempText$
					If Text$ = "" Then Text = " "
					tempText$ = ""
					FlushKeys
					Return Text$
				Else If aKeytoGet = 8 Or aKeytoGet = 4 'AscII For Backspace And Delete
					If Len( tempText$ ) > 0 Then tempText$ = Left$( tempText$, Len(tempText$) -1 )	
				Else' If aKeytoGet>=32 And aKeytoGet<=122 And Len(Message)<52
					tempText$:+ Chr$(aKeytoGet)
				EndIf
	
			EndIf
			
			DrawText Text$ + tempText,X,Y
			Return ""

	EndFunction

EndType




'Rem 
'Shows the use of Both input methods!
'--------------------------------------------------------
	Graphics 300,70,0 'Graphicsmode is a MUST
	
	Local Name$ = InputText("Enter Your Name: ",10,10)	
	DrawText "Your Name was: "+Name$,30,30 ;Flip

	WaitKey()	

	Local X, Code$
	While Not KeyDown(Key_Escape)

		Code = DynamicInput$( Name+" enter code : ",10,10)
		If Upper(Code) = Upper("code")	Then DrawText "-- Correct Code! --",10,30;Flip;WaitKey() Else DrawText "Enter ~qCode~q ok?",10,30
		DrawRect X,50,40,5 ; X:+1 ; If X > GraphicsWidth() X = 0
		
	Flip;Cls
	Wend
'--------------------------------------------------------
'Good to have function
'EndRem
