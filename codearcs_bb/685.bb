; ID: 685
; Author: Difference
; Date: 2003-05-12 14:43:17
; Title: ReadFileAsString$()
; Description: Read file into string

Function ReadFileAsString$(file$)

	Local tmp$=SystemProperty ("tempdir") + MilliSecs() + "bbrsrf.tmp"
	Local ret$
	Local fbank=CreateBank(	FileSize(file$))

	f=OpenFile (file$)
		If Not f FreeBank fbank : Return ""
	
		ReadBytes fbank,f,0,BankSize(fbank)  	
	CloseFile f
	
	f=WriteFile (tmp$)
		If Not f FreeBank fbank : Return ""
		WriteInt f,BankSize(fbank) 
		WriteBytes fbank,f,0,BankSize(fbank)
	CloseFile f

	f=OpenFile(tmp$)
		If Not f FreeBank fbank : Return ""
		ret$=ReadString(f)
	CloseFile f

	FreeBank fbank

	DeleteFile tmp$

	Return ret$

End Function
