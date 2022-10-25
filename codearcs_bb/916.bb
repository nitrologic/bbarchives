; ID: 916
; Author: MPZ
; Date: 2004-02-05 03:08:51
; Title: Blitz3D Filerequester (2.Update)
; Description: Use a Filerequester in Blitz 3D

; This Procedure is for free MPZ (@) from Berlin
; Version 0.2 1/2004
; 
; in the USERLIBS must be the file kernel32.decls
;.lib "kernel32.dll"
;api_RtlMoveMemory(Destination*,Source,Length) : "RtlMoveMemory"

; in the USERLIBS must be the file comdlg32.decls
;.lib "comdlg32.dll"
;api_GetOpenFileName% (pOpenfilename*) : "GetOpenFileNameA"
;api_GetSaveFileName% (pOpenfilename*) : "GetSaveFileNameA"


Graphics 800,600,0,2

; GetOpen/saveFileName consts Flags (useful ones only!)...
Const OFN_CREATEPROMPT         = $2000    ; Prompts the user as to whether they want to create a file that doesnt exist.
Const OFN_FILEMUSTEXIST        = $1000    ; File must exist for it to be returned.
Const OFN_HIDEREADONLY         = 4        ; Hides the read only button in the dialog...
Const OFN_NOCHANGEDIR          = 8        ; Stops the user from changing the initial directory.
Const OFN_NONETWORKBUTTON      = $20000   ; Hides and disables the network button.
Const OFN_NOREADONLYRETURN     = $8000    ; Stops the requester returning readonly files..
Const OFN_NOVALIDATE           = 256      ; If selected, no check will be done for invalid characters.
Const OFN_OVERWRITEPROMPT      = 2        ; Prompt for overwrite file...
Const OFN_PATHMUSTEXIST        = $800     ; Specifies that the path MUST exist for it to be able to be selected.
Const OFN_READONLY             = 1        ; Makes the read only checkbox in the dialog box to be checked immediately.

; getopenfile $(Title_of_Requester$, SearchPath$,Files_with_ending$, Flags); 	
; getsavefile $(Title_of_Requester$, Save_File_name$,Files_with_ending$, Flags); 	
;
; Title_of_Requester$= "Name of the Requester / Name des Dateifragefensters
; SearchPath$ = "C:\" ; Path for File searching / Pfad wo nach der Datei gesuchet werden soll 
; Files_with_ending$ = "All Files (*.*)" + Chr$(0) + "*.*" + Chr$(0)
;					 = "Blitzbasic" + Chr$(0) + "*.bb" + Chr$(0) + "Text" + Chr$(0) + "*.txt" + Chr$(0)
; Flags = See Flag lists
; Save_File_name$ = "C:\test.bb" ; Name of the Savefile with Path / Name der Datei mit Pfad zum speichern


Print getopenfile$("File open / Datei öffnen","d:\","All Files (*.*)" + Chr$(0) + "*.*" + Chr$(0)); flags optional

Print getsavefile$("File Save / Datei sichern","c:\Hello.bb","Blitzbasic" + Chr$(0) + "*.bb" + Chr$(0) + "Text" + Chr$(0) + "*.txt" + Chr$(0));; flags optional

While MouseHit(1) <> 1
Wend

End

;--------------------------You can use it as BlitzLIB

Function getOpenFile$(lpstrTitle$,lpstrInitialDir$,lpstrFilter$,flags=$1000)

	nextOffset%=0 
	theBank=CreateBank(76)
	lStructSize=76
	PokeInt theBank,nextOffset%,lStructSize
	nextOffset%=nextOffset%+4 
		
	hwndOwner=0
	PokeInt theBank,nextOffset%,hwndOwner
	nextOffset%=nextOffset%+4 
		
	hInstance=0
	PokeInt theBank,nextOffset%,hInstance
	nextOffset%=nextOffset%+4 

	If lpstrFilter$ = "" Then
		lpstrFilter$ = "All Files (*.*)" + Chr$(0) + "*.*" + Chr$(0)+ Chr$(0)
	Else	
		lpstrFilter$ = lpstrFilter$ + Chr$(0)		
	End If
	lpstrFilter_ = CreateBank(Len(lpstrFilter$)) 
	string_in_bank(lpstrFilter$,lpstrFilter_)
	PokeInt theBank,nextOffset%,AddressOf(lpstrFilter_)
	nextOffset%=nextOffset%+4
		
	lpstrCustomFilter=0
	PokeInt theBank,nextOffset%,lpstrCustomFilter
	nextOffset%=nextOffset%+4 
	
	nMaxCustFilter=0
	PokeInt theBank,nextOffset%,nMaxCustFilter
	nextOffset%=nextOffset%+4 
	
	nFilterIndex=0
	PokeInt theBank,nextOffset%,nFilterIndex
	nextOffset%=nextOffset%+4 

	lpstrFile$= String$ (" ", 254)
	lpstrFile_ = CreateBank(Len(lpstrFile$)) 
	string_in_bank(lpstrFile$,lpstrFile_)
	PokeInt theBank,nextOffset%,AddressOf(lpstrFile_)
	nextOffset%=nextOffset%+4 
	
	nMaxFile=255
	PokeInt theBank,nextOffset%,nMaxFile
	nextOffset%=nextOffset%+4 
	
	lpstrFileTitle$=String$ (" ", 254)
	lpstrFileTitle_ = CreateBank(Len(lpstrFileTitle$)) 
	string_in_bank(lpstrFileTitle$,lpstrFileTitle_)
	PokeInt theBank,nextOffset%,AddressOf(lpstrFileTitle_)
	nextOffset%=nextOffset%+4  
	
	nMaxFileTitle=255
	PokeInt theBank,nextOffset%,nMaxFileTitle
	nextOffset%=nextOffset%+4 
	
	If lpstrInitialDir$="" Then
		lpstrInitialDir$="c:\"+Chr$(0)
	Else
		lpstrInitialDir$=lpstrInitialDir$+Chr$(0)
	End If
	lpstrInitialDir_ = CreateBank(Len(lpstrInitialDir$)) 
	string_in_bank(lpstrInitialDir$,lpstrInitialDir_)
	PokeInt theBank,nextOffset%,AddressOf(lpstrInitialDir_)
	nextOffset%=nextOffset%+4 
	
	If lpstrTitle$="" Then
		lpstrTitle$="Open"+Chr$(0) 
	Else
		lpstrTitle$ = lpstrTitle$ + Chr$(0)
	End If	
	lpstrTitle_ = CreateBank(Len(lpstrTitle$)) 
	string_in_bank(lpstrTitle$,lpstrTitle_)	
	PokeInt theBank,nextOffset%,AddressOf(lpstrTitle_)
	nextOffset%=nextOffset%+4 

	PokeInt theBank,nextOffset%,flags
	nextOffset%=nextOffset%+4 
	
	nFileOffset=0
	PokeShort theBank,nextOffset%,nFileOffset
	nextOffset%=nextOffset%+2
	
	nFileExtension=0
	PokeShort theBank,nextOffset%,nFileExtension
	nextOffset%=nextOffset%+2
	
	lpstrDefExt=0
	PokeInt theBank,nextOffset%,lpstrDefExt
	nextOffset%=nextOffset%+4 
	
	lCustData=0
	PokeInt theBank,nextOffset%,lCustData
	nextOffset%=nextOffset%+4 
	
	lpfnHook=0
	PokeInt theBank,nextOffset%,lpfnHook
	nextOffset%=nextOffset%+4 

	lpTemplateName$=""+Chr$(0)
	lpTemplateName_ = CreateBank(Len(lpTemplateName$)) 
	string_in_bank(lpTemplateName$,lpTemplateName_)
	PokeInt theBank,nextOffset%,AddressOf(lpTemplateName_)
	nextOffset%=nextOffset%+4 
	If api_GetOpenFileName (theBank) Then
		lpstrFile$ = bank_in_string$(lpstrFile_)
	Else
		lpstrFile$ =""
	End If
	FreeBank theBank
	FreeBank lpstrFilter_
	FreeBank lpstrFile_
	FreeBank lpstrFileTitle_
	FreeBank lpstrInitialDir_
	FreeBank lpstrTitle_
	FreeBank lpTemplateName_
	Return lpstrFile$
End Function

Function getsaveFile$(lpstrTitle$,lpstrFile$,lpstrFilter$,flags=2) ; Get a SAVEFILENAME

	nextOffset%=0 
	theBank=CreateBank(76)
	lStructSize=76
	PokeInt theBank,nextOffset%,lStructSize
	nextOffset%=nextOffset%+4 
		
	hwndOwner=0
	PokeInt theBank,nextOffset%,hwndOwner
	nextOffset%=nextOffset%+4 
		
	hInstance=0
	PokeInt theBank,nextOffset%,hInstance
	nextOffset%=nextOffset%+4 

	If lpstrFilter$ = "" Then
		lpstrFilter$ = "All Files (*.*)" + Chr$(0) + "*.*" + Chr$(0)+ Chr$(0)
	Else	
		lpstrFilter$ = lpstrFilter$ + Chr$(0)		
	End If
	lpstrFilter_ = CreateBank(Len(lpstrFilter$)) 
	string_in_bank(lpstrFilter$,lpstrFilter_)
	PokeInt theBank,nextOffset%,AddressOf(lpstrFilter_)
	nextOffset%=nextOffset%+4
		
	lpstrCustomFilter=0
	PokeInt theBank,nextOffset%,lpstrCustomFilter
	nextOffset%=nextOffset%+4 
	
	nMaxCustFilter=0
	PokeInt theBank,nextOffset%,nMaxCustFilter
	nextOffset%=nextOffset%+4 
	
	nFilterIndex=0
	PokeInt theBank,nextOffset%,nFilterIndex
	nextOffset%=nextOffset%+4 

	lpstrFile_ = CreateBank(255) 
	string_in_bank(lpstrFile$+Chr$(0),lpstrFile_)
	PokeInt theBank,nextOffset%,AddressOf(lpstrFile_)
	nextOffset%=nextOffset%+4
	
	nMaxFile=255
	PokeInt theBank,nextOffset%,nMaxFile
	nextOffset%=nextOffset%+4 
	
	lpstrFileTitle$=String$ (" ", 254)
	lpstrFileTitle_ = CreateBank(Len(lpstrFileTitle$)) 
	string_in_bank(lpstrFileTitle$,lpstrFileTitle_)
	PokeInt theBank,nextOffset%,AddressOf(lpstrFileTitle_)
	nextOffset%=nextOffset%+4  
	
	nMaxFileTitle=255
	PokeInt theBank,nextOffset%,nMaxFileTitle
	nextOffset%=nextOffset%+4 
	
	lpstrInitialDir$=""+Chr$(0)
	lpstrInitialDir_ = CreateBank(Len(lpstrInitialDir$)) 
	string_in_bank(lpstrInitialDir$,lpstrInitialDir_)
	PokeInt theBank,nextOffset%,AddressOf(lpstrInitialDir_)
	nextOffset%=nextOffset%+4 
	
	If lpstrTitle$="" Then
		lpstrTitle$="Save"+Chr$(0) 
	Else
		lpstrTitle$ = lpstrTitle$ + Chr$(0)
	End If	
	lpstrTitle_ = CreateBank(Len(lpstrTitle$)) 
	string_in_bank(lpstrTitle$,lpstrTitle_)	
	PokeInt theBank,nextOffset%,AddressOf(lpstrTitle_)
	nextOffset%=nextOffset%+4 

	PokeInt theBank,nextOffset%,flags
	nextOffset%=nextOffset%+4 
	
	nFileOffset=0
	PokeShort theBank,nextOffset%,nFileOffset
	nextOffset%=nextOffset%+2
	
	nFileExtension=0
	PokeShort theBank,nextOffset%,nFileExtension
	nextOffset%=nextOffset%+2
	
	lpstrDefExt=0
	PokeInt theBank,nextOffset%,lpstrDefExt
	nextOffset%=nextOffset%+4 
	
	lCustData=0
	PokeInt theBank,nextOffset%,lCustData
	nextOffset%=nextOffset%+4 
	
	lpfnHook=0
	PokeInt theBank,nextOffset%,lpfnHook
	nextOffset%=nextOffset%+4 

	lpTemplateName$=""+Chr$(0)
	lpTemplateName_ = CreateBank(Len(lpTemplateName$)) 
	string_in_bank(lpTemplateName$,lpTemplateName_)
	PokeInt theBank,nextOffset%,AddressOf(lpTemplateName_)

	If api_GetSaveFileName (theBank) Then
		lpstrFile$ = bank_in_string$(lpstrFile_)
	Else
		lpstrFile$ =""
	End If
	FreeBank theBank
	FreeBank lpstrFilter_
	FreeBank lpstrFile_
	FreeBank lpstrFileTitle_
	FreeBank lpstrInitialDir_
	FreeBank lpstrTitle_
	FreeBank lpTemplateName_
	Return lpstrFile$
End Function

Function AddressOf(Bank) ; Find the correct Adress of a Bank (for C *Pointer)
	Local Address = CreateBank(4) 
	api_RtlMoveMemory(Address,Bank+4,4) 
	Return PeekInt(Address,0) 
End Function

Function string_in_bank(s$,bankhandle) ; Put a String in a Bank
	Local pos=1
	Local pos2=0
	Repeat
		PokeByte(bankhandle,pos2,Asc(Mid(s$,pos,Len(s$))))
		pos=pos+1
		pos2=pos2+1
	Until pos=Len(s$)+1
End Function

Function bank_in_string$(bankhandle) ; Get a String from a Bank
	Local s$=""
	Local pos=0
	Repeat
		s$=s$+Chr(PeekByte(bankhandle,pos))
		pos=pos+1
	Until pos=BankSize(bankhandle)
	s$=Replace$(s$,Chr(0)," ")
	Return s$
End Function
