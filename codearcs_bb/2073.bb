; ID: 2073
; Author: Diego
; Date: 2007-07-22 12:25:15
; Title: FileName, FileExtention, FilePath, AddFileExtention
; Description: Get file name, file extention or file path or add a file extention | Bekomme den Dateinamen, die Dateiendung oder den Pfad oder fürge eine Dateiendung hinzu

; Example | Beispiel
File$ = "C:\My.Files\Docs/File.txt" ; Difficult Filename | Schwieriger Dateiname
Print FileName$(File$)
Print FileExtention$(File$)
Print FilePath$(File$)
WaitKey
End

Function FileName$(File$)
For I% = Len(File$) To 1 Step -1
	If Mid(File$, I%, 1) = "\" Or Mid(File$, I%, 1) = "/" Then Return Mid(File$, I% + 1)
	Next
Return File$
End Function

Function FileExtention$(File$)
For I% = Len(File$) To 1 Step -1
	If Mid(File$, I%, 1) = "." Return Mid(File$, I% + 1)
	If Mid(File$, I%, 1) = "\" Or Mid(File$, I%, 1) = "/" Then Return ""
	Next
End Function

Function FilePath$(File$)
For I% = Len(File$) To 1 Step -1
	If Mid(File$, I%, 1) = "\" Or Mid(File$, I%, 1) = "/" Then Return Left(File$, I% - 1)
	Next
End Function

; Adds a file extention, if not exists. | Fügt eine Dateiendung hinzu falls noch keine da ist.
Function AddFileExtention$(File$, Extention$)
If FileExtention(File$) = "" Then Return File$ + "." + Extention$ Else Return File$
End Function
