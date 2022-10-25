; ID: 2943
; Author: ThePict
; Date: 2012-05-06 07:03:10
; Title: AddLineToFile
; Description: Sticks a line of text or data to the end of a file

Function AddLineToFile(textline$,file$)
mf3=WriteFile("tmp.txt")
If FileType(file$)=0 Then Goto writelastline
mf2=ReadFile(file$)
Repeat
w$=ReadLine(mf2)
WriteLine(mf3,w$)
Until Eof(mf2)
CloseFile(mf2)
.writelastline
WriteLine(mf3,textline$)
CloseFile(mf3)
CopyFile "tmp.txt",file$
End Function
