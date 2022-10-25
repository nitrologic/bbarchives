; ID: 684
; Author: Prof
; Date: 2003-05-12 13:10:08
; Title: Get File Date
; Description: Function to get the creation date of a file.

;'Get Creation Date' by The Prof for Blitzers everywhere!
;
; Workaround Only. Launches 'cmd.exe' to get the info.
;
Graphics 640,200,32,2

File$="c:\program files\BlitzPlus\Samples\Mak\BlitzPaint.bb"

CreationDate$=GetCreationDate$(File$)

Text 10,10,File$
Text 10,30,CreationDate$
Flip
WaitKey():End

; ********************************************************

Function GetCreationDate$(Filename$)
  ; This function returns the creation dates of filename$.
  ; The filename must include the complete path.
  ; Tested on XP Pro only. Your OS may differ.

  If FileType(Filename$)=1
     TempFile$=SystemProperty("tempdir")+"/cdate.txt"
     c$="cmd.exe /c dir "+Chr$(34)+filename$+Chr$(34)+" > "+Chr$(34)+TempFile$+Chr$(34)
     ExecFile(c$)

     ; We need a slight delay otherwise it runs too fast (On my
     ; machine anyway), and can cause an error when trying
     ; to open the output file. Increase if necessary. 
     Delay 50

     FileIn=ReadFile(TempFile$)
     ; Ignore the first 5 lines - just drive info etc...
     For N=1 To 5:l$=ReadLine(FileIn):Next
     l$=ReadLine(FileIn):CloseFile(FileIn)
     CreationDate$=Left$(l$,10)
  Else
     CreationDate$="The filename given doesn't appear to exist"
  EndIf
  Return CreationDate$
End Function
