; ID: 2047
; Author: b32
; Date: 2007-06-26 13:02:21
; Title: find mydocuments
; Description: get path for 'my documents' folder

;Save these lines as a textfile in the directory c:\program files\blitz3d\userlibs
;and call the file 'shell32.decls'
;-------------------------------------
;.lib "shell32.dll"

;api_GetFolderPath%(hwnd, p1, p2, p3, out*) : "SHGetFolderPathA"
;-------------------------------------
;it will enable the api_getfolderpath command


	Print GetMyDocumentsPath$()
	
	WaitKey()	
	End

;-------------------------------------------------------------------------------------------------------
;											GetMyDocumentsPath()
;-------------------------------------------------------------------------------------------------------
;uses SHGetFolderPathA to get the mydocuments folder
Function GetMyDocumentsPath$()

	bank = CreateBank(256)
	
	api_GetFolderPath(0, $5, 0, 0, bank)
	
	s$ = ""
	For i = 0 To 255
		b = PeekByte(bank, i)
		If b = 0 Then Exit
		s$ = s$ + Chr$(b)
	Next
	
	FreeBank bank
	
	If Right$(s$, 1) <> "\" Then s$ = s$ + "\"
	
	Return s$
	
End Function
