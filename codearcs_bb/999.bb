; ID: 999
; Author: Adren Software
; Date: 2004-04-15 15:56:28
; Title: delete Me
; Description: Writes .log files for review

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;LogFile.bb
;Contains Functions to write .log files
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

Function CreateLog(St$)

file = WriteFile(St$)

Return file

End Function

Function WriteLog(LG$,St$)

SeekFile(LG$,FileSize(LG$))

WriteLine(LG$,St$)

End Function

Function EraseLog(LG$)

SeekFile(LG$,0)

WriteLine(LG$,"")

End Function

Function CloseLog(LG$)

CloseFile(LG$)

LG$ = 0

End Function
