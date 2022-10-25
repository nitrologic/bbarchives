; ID: 598
; Author: Neochrome
; Date: 2003-02-24 05:56:24
; Title: QLimit
; Description: Limit values

Function QLimit(Quick,Low,High)
   If Quick>High then Quick=High
   If Quick<Low then Quick=Low
   Return Quick
End function
