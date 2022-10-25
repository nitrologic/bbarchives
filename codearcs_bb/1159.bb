; ID: 1159
; Author: JPD
; Date: 2004-09-07 13:08:09
; Title: Filecheck
; Description: If a file doesn't exist, exit application.

;Number of files to check for program
files_count = 4
Dim files$(files_count)

;Filenames
files$(1) = "FILE_01.DAT"
files$(2) = "FILE_02.DAT"
files$(3) = "FILE_03.DAT"
files$(4) = "FILE_04.DAT"

;Check files
For d = 1 To files_count

checkfile = FileType(files$(d))
If checkfile <> 1 RuntimeError "File " + Chr$(34) + files$(d)+Chr$(34) + " doesn't exist!"
;If checkfile <> 1 Notify "File " + Chr$(34) + files$(d)+Chr$(34) + " doesn't exist!"
CloseFile checkfile

Next
