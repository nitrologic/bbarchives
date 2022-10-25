; ID: 745
; Author: -=Darkheart=-
; Date: 2003-07-15 08:04:51
; Title: Rename File Deluxe
; Description: Renames a file.

Function Renamefile(orgfile$,newfile$)
If FileType (orgfile$)<>1 Then RuntimeError "File " + orgfile$ + " Does Not Exist!"
thispath$=bbgetDir$(orgfile$)
thisfile$=bbgetfile$(newfile$)
mynewfile$=thispath$+thisfile$
If orgfile$=mynewfile$ Then RuntimeError "New Filename Cannot Match Old Filename!"
CopyFile orgfile$,mynewfile$
DeleteFile orgfile$
End Function

Function bbGetDir$(path$)
	For a = Len(path$) To 1 Step -1
		byte$ = Mid(path$,a,1)
		If byte$ = "\"
			Return Left(path$,a)
		EndIf
	Next
	Return ""
End Function


Function bbGetFile$(path$)
	For a = Len(path$) To 1 Step -1
		byte$ = Mid(path$,a,1)
		If byte$ = "\"
			Return Right(path$,Len(path$)-a)
		EndIf
	Next
	Return path$
End Function

;Example Renamefile ("c:\bum.bb","scum.bb")
