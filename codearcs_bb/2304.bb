; ID: 2304
; Author: cipherdude9
; Date: 2008-08-24 04:10:44
; Title: databomb
; Description: use to erase folders

.start
; Define what folder to start with ... 
folder$ = Input$("file to delete:")

; Open up the directory, and assign the handle to myDir 
myDir=ReadDir(folder$) 

; Let's loop forever until we run out of files/folders to delete! 
Repeat 
; Assign the next entry in the folder to file$ 
file$=NextFile$(myDir) 

; If there isn't another one, let's exit this loop 
If file$="" Then Exit 

; Use FileType to determine if it is a folder (value 1) or a file and print results 
If FileType(folder$+"\"+file$) = 1 Then 
Print "File: " + file$ + " deleted"
DeleteFile folder$+"\"+file$
If FileType(folder$+"\"+file$) = 0 Then Print "deletion of file: " + file$ + " confirmed"
End If 

Forever 

; Properly delete the open folder 
DeleteDir folder$ 

;done! 
Print "Done deleteing files"
Goto start
