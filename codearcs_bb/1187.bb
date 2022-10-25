; ID: 1187
; Author: Neochrome
; Date: 2004-11-05 12:13:30
; Title: RelativePath
; Description: To get a relativepath returned

Dim Root$(64)
Dim ssFile$(64)


Function GetRelativePath$(strRoot$, strFile$)
    Local i%, NewTreeStart%, sRel$
	Local RootCount%, FileCount%

    If Left(strRoot, 3) <> Left(strFile, 3) Then
        GetRelativePath = strFile
        Return ""
    End If

	buffa$ = ""
	For i=1 To Len(strRoot$)
		
		
		If Mid(strRoot$,i,1)="\" Then
			RootCount% = RootCount% + 1
		Else
			buffa$ = Mid(strRoot$,i,1)
			Root$(RootCount%) = Root$(RootCount%) + buffa$
		End If
	Next

	buffa$ = ""
	For i=1 To Len(strFile$)
		
		
		If Mid(strFile$,i,1)="\" Then
			FileCount% = FileCount% + 1
		Else
			buffa$ = Mid(strFile$,i,1)
			ssfile$(FileCount%) = ssfile$(FileCount%) + buffa$
		End If
	Next

	i=0
    
    While Root(i) = ssFile(i)
        i = i + 1
    Wend
    
    If i = RootCount% Then
        While i <= FileCount%
            sRel = sRel + ssFile(i) + "\"
            i = i + 1
        Wend
        GetRelativePath = Left(sRel, Len(sRel) - 1)
       	Return ""
    End If
    
    NewTreeStart = i

    While i < RootCount
        sRel = sRel + "..\"
        i = i + 1
    Wend

    While NewTreeStart <= FileCount
        sRel = sRel + ssFile(NewTreeStart) + "\"
        NewTreeStart = NewTreeStart + 1
    Wend
    
	Return Left(sRel, Len(sRel) - 1)
End Function
