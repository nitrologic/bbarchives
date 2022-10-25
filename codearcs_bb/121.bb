; ID: 121
; Author: Oldefoxx
; Date: 2001-11-03 22:48:55
; Title: AllFiles on AllDrives
; Description: Displays all files as found on all drives from C: to Z:

AppTitle "List all Files to Screen"
;  By Donald R. Darden, 2001
;  This program will print to the screen all the
;  file names found on drive partitions from C: to
;  Z:  With some slight changes it can be modified
;  for use in a Function to locate any directory or
;  file on any drive upon request.

Graphics 640,480
For drive=Asc("C") To Asc("Z") 
  folderlist$= Chr(drive)+":\"
  While folderlist$>""
    a=Instr(folderList$,Chr$(0),1) 
    If a Then
      folder$=Left(folderList$,a-1)
      folderList$=Mid$(folderList$,a+1)
    Else
      folder$=folderList$
      folderList$=""
    End If
    dirhdl=ReadDir(folder$)  
    If dirhdl=0 Then Exit
    Repeat
      file$=NextFile$(dirhdl)
      If file$="" Then Exit
      If FileType(folder$+file$)=2 Then
        comment$="SDir"
        If file$="." Or Right$(file$,2)=".." Then  
          Goto valid
        EndIf
        folderlist$=folder$+file$+"\"+Chr$(0)+folderlist$
      Else
        comment$="File"
      End If
      Print LSet(folder$+file$,75)+" "+comment$
      file$=Upper(file$) 
      For a=Asc("A") To Asc("Z")
        If Instr(file$,Chr$(a),1) Then Goto valid
      Next
      For a=Asc("0") To Asc("9")
        If Instr(file$,Chr$(a),1) Then Goto valid
      Next
      Print "["+file$+"]"
      WaitKey
.valid
    Forever
    CloseDir(dirhdl)
  Wend
Next
WaitKey
End
