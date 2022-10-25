; ID: 2332
; Author: Kev
; Date: 2008-10-10 18:33:13
; Title: Delete files to Recycle Bin
; Description: delete file to recycle bin

decls needs to be added to userlibs.

.lib "shell32.dll"
shell32_SHFileOperation%(lpFileOp*):"SHFileOperationA" 

.lib "user32.dll"
user32_CallWindowProc%(lpPrevWndFunc*,hwnd%,msg%,wParam%,lParam$):"CallWindowProcA"

;----------------------------------------------------------------------------------------

; and a simple example

Const FO_DELETE = $3
Const FOF_ALLOWUNDO = $40
Const FOF_NOCONFIRMATION = $10
Const FOF_SILENT = $4

; send example file to bin
If send_to_bin("e:\tmp.txt") = 0 Then
	Print "sent to bin"
Else
	Print "problem sending file to bin"
EndIf

MouseWait
End

Function send_to_bin(filename$)

	; build quick asm call to obtain string address
	asm = CreateBank(5)
	Restore asm_data
	For add_byte = 0 To 4
		Read byte
		PokeByte asm,add_byte,byte
	Next
	str_addr = user32_CallWindowProc(asm,0,0,0,filename$)

	; build struct
	bank = CreateBank(24)
	PokeInt bank,4,FO_DELETE 
	PokeInt bank,8,str_addr
	PokeInt bank,16,FOF_ALLOWUNDO Or FOF_NOCONFIRMATION Or FOF_SILENT

	; delete file to bin
	value =  shell32_SHFileOperation(bank)
	
	FreeBank bank
	FreeBank asm
	
	Return value

End Function

.asm_data
Data $8b,$44,$24,$10,$c3
