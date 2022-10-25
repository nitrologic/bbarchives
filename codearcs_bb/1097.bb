; ID: 1097
; Author: Kev
; Date: 2004-06-25 23:04:28
; Title: Multi Function Returns
; Description: Multi Function variable passing/modifying

;
; decls
;

.lib "user32.dll"
apiCallWindowProc%(lpPrevWndFunc*,hWnd%,Msg%,wParam$,lParam*):"CallWindowProcA"
apiCallWindowProcSTR$(lpPrevWndFunc*,hWnd%,Msg%,wParam$,lParam*):"CallWindowProcA"

;
; Multi Function Returns.
; 	Code By Kevin Poole.

; create bank for modifying in a function.
PASSbank = CreateBank(256)

; poke address of strings into bank.
PokeInt PASSbank,0,GRAB_StringAddr("String 1")
PokeInt PASSbank,8,GRAB_StringAddr("String 2")

; call test function.
ReturnTest(PASSbank)

; show results.
Print ""
Print "Modifyed from within function"
Print ""

newstring1$ = GRAB_StringFromAddr(PeekInt(PASSbank,0))
newstring2$ = GRAB_StringFromAddr(PeekInt(PASSbank,8))
Print newstring1$
Print newstring2$

MouseWait
End

Function ReturnTest(bank)

	; display string from bank, then modify and store it.
	modifyString1$ = GRAB_StringFromAddr(PeekInt(bank,0))
	Print modifyString1$
	PokeInt bank,0,GRAB_StringAddr(modifyString1$+" : MODIFYED ")

	; display string from bank, then modify and store it.
	modifyString2$ = GRAB_StringFromAddr(PeekInt(bank,8))
	Print modifyString2$
	PokeInt bank,8,GRAB_StringAddr(modifyString2$+" : MODIFYED ")
		
	Return bank
	
End Function

; returns address of string.
Function GRAB_StringAddr(grabstring$)

	Local grab_string_bank
	
	;
	If grab_string_bank = 0 Then
		;
		grab_string_bank = CreateBank(256)	
		Restore CUSTOM_GET_STRING_ADDRESS
		Repeat
			Read e_data
			If e_data = 999 Then Exit
			PokeByte grab_string_bank,next_entry_data,e_data
			next_entry_data = next_entry_data + 1	
		Forever
	
	EndIf
	
	;	
	string_Addr = apiCallWindowProc(grab_string_bank,0,0,grabstring$,CreateBank(0))

	Return string_Addr
	
End Function

; returns address of string.
Function GRAB_StringFromAddr$(address)

	Local grab_stringfrom_bank
	
	;
	If grab_string_bank = 0 Then
		;
		grab_string_bank = CreateBank(256)	
		Restore CUSTOM_GET_STRING_FROM_ADDRESS
		Repeat
			Read e_data
			If e_data = 999 Then Exit
			PokeByte grab_string_bank,next_entry_data,e_data
			next_entry_data = next_entry_data + 1	
		Forever
	
	EndIf
	
	;	
	string_Addr$ = apiCallWindowProcSTR(grab_string_bank,0,address,"",CreateBank(0))

	Return string_Addr$
	
End Function


.CUSTOM_GET_STRING_ADDRESS
Data $3E,$8B,$44,$24,$0C 				; MOV EAX,DWORD PTR DS:[ESP+0C]
Data $C3 				 				; RET
Data 999

.CUSTOM_GET_STRING_FROM_ADDRESS
Data $3E,$8B,$44,$24,$08 				; MOV EAX,DWORD PTR DS:[ESP+0C]
Data $C3 				 				; RET
Data 999
