; ID: 1567
; Author: Picklesworth
; Date: 2005-12-17 15:36:23
; Title: FixPath
; Description: Creates every missing folder in a file path

Function FixPath(path$)
;Creates every missing folder in a path. (Fills in gaps in a filepath)
;Handy for file saving.
;Written by Dylan McCall (Mr. Picklesworth)
	path$=extractfilepath(path$)
	Local c=1,pathTo$
	Repeat
		slash=Instr(path$,"\",c)
		If slash=0
			If c>=Len(path$)+1
				Exit
			Else
				slash=Len(path$)+1
			EndIf
		EndIf
		
		folder$=Mid(path$,c,slash-c)
		If FileType(pathTo$+folder)=0 Then CreateDir(pathTo$+folder)
		c=slash+1
		pathTo$=pathTo$+folder+"\"
	Forever
	Return 1
End Function

;FUNCTION ExtractFilePath
;Accepts a filepath with filename and returns only the path
;i.e. pass c:\temp\test.txt the return value will be c:\temp\
Function ExtractFilePath$(sFilePath$) ;Written by Perturbatio
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
