; ID: 808
; Author: Perturbatio
; Date: 2003-10-12 20:13:17
; Title: ExtractFileName$, ExtractFileExt$, ExtractFilePath$
; Description: Allows extraction of Filename, FileExtension and FilePath from a string

;FUNCTION ExtractFileName
;Accepts a filepath and returns the filename

Function ExtractFileName$(sFilePath$)
;LOCAL VARS
Local iStartPos% = 0
Local iSearchPos% = 0
Local iFilePathLength = 0
Local sFileName$ = ""
	
;BEGIN FUNCTION CODE
iFilePathLength = Len(sFilePath$)
iSearchPos% = iFilePathLength
 
While (iStartPos% < 1) And (iSearchPos% > 1)

	iStartPos% = Instr(sFilePath$, "\", iSearchPos%)
	iSearchPos% = iSearchPos% - 1
	
Wend

If iStartPos = 0 Then ;if the filepath contains no backslashes
	sFileName$ = sFilePath$
Else
	sFileName$ = Right$(sFilePath$, iFilePathLength% - iStartPos%)
EndIf


Return sFileName$

End Function



;FUNCTION ExtractFileExt
;Accepts a filepath and returns the extension for the file

Function ExtractFileExt$(sFilePath$)
;LOCAL VARS
Local iStartPos% = 0
Local iSearchPos% = 0
Local iFilePathLength = 0
Local sFileExt$ = ""
	
;BEGIN FUNCTION CODE
iFilePathLength = Len(sFilePath$)
iSearchPos% = iFilePathLength
 
While (iStartPos% < 1) And (iSearchPos% > 1)

	iStartPos% = Instr(sFilePath$, ".", iSearchPos%)
	iSearchPos% = iSearchPos% - 1
	
Wend

If iStartPos = 0 Then ;if the filepath contains no .
	sFileExt$ = sFilePath$
Else
	sFileExt$ = Right$(sFilePath$, iFilePathLength% - iStartPos%)
EndIf


Return sFileExt$

End Function

;FUNCTION ExtractFilePath
;Accepts a filepath with filename and returns only the path
;i.e. pass c:\temp\test.txt the return value will be c:\temp\

Function ExtractFilePath$(sFilePath$)
;LOCAL VARS
Local iStartPos% = 0
Local iSearchPos% = 0
Local iFilePathLength = 0
Local sFileExt$ = ""
	
;BEGIN FUNCTION CODE
iFilePathLength = Len(sFilePath$)
iSearchPos% = iFilePathLength
 
While (iStartPos% < 1) And (iSearchPos% > 1)

	iStartPos% = Instr(sFilePath$, "\", iSearchPos%)
	iSearchPos% = iSearchPos% - 1
	
Wend

If iStartPos = 0 Then ;if the filepath contains no backslashes
	sFileExt$ = sFilePath$
Else
	sFileExt$ = Left$(sFilePath$, iStartPos%)
EndIf


Return sFileExt$

End Function
