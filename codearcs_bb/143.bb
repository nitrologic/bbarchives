; ID: 143
; Author: Neo Genesis10
; Date: 2001-11-19 03:19:31
; Title: Scripting engine
; Description: A simple script engine for in game scenes

; before calling cutscene function, I read the script commands into an array...

Global scriptcount
Dim scom$( 49 )			; holds command string
Dim sact( 49 )			; holds action
Dim param$( 49 )			; for storing multiple parameters

Restore scriptcommands
Read scriptcount
For i = 1 To scriptcount
	Read scom$( i-1 )	; command line
	Read sact( i-1 )	; actions for script command
Next

Function Cutscene( val )

	scene = ReadFile("cutscenes/scene"+val+".scn")
	If scene = 0 Return False

	While Not Eof( scene )
		ClearArrays()
		ReadText = False
		temp$ = ReadLine( scene )	; grab a line of information
	
		; cycle through line, picking out bits we want

		curparam = 0
		For i = 1 To Len( temp$ )
			section$ = Mid$( temp$, i, 1 )
			If section$ = ";" Exit	; jump out of commented sections
			If section$ = ","
				curparam = curparam + 1
				If curparam > 49 RuntimeError("Too many parameters!")
			EndIf	
			
			For x = 48 To 57
				If section$ = Chr$( x )
					param$(curparam) = param$(curparam) + section$
					ntmp$ = ""
					While ntmp$ <> "," And ntmp$ <> ""
						param$( curparam ) = param$( curparam ) + ntmp$
						ntmp$ = Mid$( temp$, i, 1 )
						i = i + 1
					Wend
					Exit
				EndIf
			Next

			For x = 0 To (scriptcount - 1)
				If Mid$( temp$, i, Len( scom$( x ) ) ) = scom$( x )
					takeaction = sact( x )
					i = i + Len( scom$( x ))
				EndIf
			Next
			
			If section$ = Chr$( 34 )
				i = i + 1
				ntmp$ = ""
				Repeat
					param$( curparam ) = param$( curparam ) + ntmp$
					ntmp$ = Mid$( temp$, i, 1 )
					If ntmp$ = Chr$( 34 ) Exit	; stop if quote symbol (") found
					i = i + 1
					If i > Len( temp$ ) Exit	; stop of end of line reached
				Forever
			EndIf
			
		Next
		
		Select takeaction
			Case 1
				; your commands here
				; use param( parameter number ) for numbers
				; and param$( parameter ) for text

				; Example: Chat 50, 50, "Hello world"
				; Text param(0), param(1), param$( 0 )
			Case 2

			Case 3
		End Select
		
		; update movements/collisions and draw world here

		Flip
		
	Wend
End Function

Function ClearArrays()

	For i = 0 To 5
		param$(i) = ""
	Next

End Function

.scriptcommands

; format: "CommandName", action
; Action refers to the takeaction command above

Data 6			; number of commands
Data "CreateChar", 1
Data "CreateNPC", 2
Data "BGMusic", 3
Data "FGMusic", 4
Data "FadeMuse", 5
Data "Animate", 6
