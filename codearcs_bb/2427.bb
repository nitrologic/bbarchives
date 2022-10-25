; ID: 2427
; Author: Luke111
; Date: 2009-03-05 20:58:38
; Title: File Viewer-Modification Checker
; Description: Checks Files for Modifications In Bytes and Loads Files For Viewing

;Check http://nanotechindustries.spaces.live.com/ for New Products, and Product Updates
;NT Check 1.0.0
Print "___________________________"
Print "|NT Check 1.0.0 UP 0.0.1  |"
Print "|Nanotech Industries, 2009|"
Print "---------------------------"
AppTitle "NT Check 1.0.0"
.start
option1$=Input$("View | Check - ")
If option1$="View" Or option1$="view" Then
	Goto loadbyte
EndIf
If option1$="Check" Or option1$="check" Then
	Goto cbyte
EndIf
.loadbyte
filename$=Input$("Please Enter The Path, Filename, & Extension For The Viewing - ")
time$=Input$("Please Enter The Time To View The File In Seconds - ")
open=OpenFile(filename$)
Goto nxtload
.nxtload
Print ReadLine$(open)
If Eof(open)<>1 Then
	Goto nxtload
EndIf
If Eof(open)=1 Then
	Goto nxtload2
EndIf
.nxtload2
CloseFile(open)
Goto nxtload3
.nxtload3
Delay time$+"000" 
End

.cbyte
filename2$=Input$("Please Enter The Path, Filename, & Extension For The File - ")
time2$=Input$("Please Enter The Time Between Checks For Modification In Seconds - ")
If FileType(filename2$)=0 Then
	RuntimeError "Error: File Not Found"
EndIf
If FileType(filename2$)=1 Then
	Print "File Found"
EndIf
If FileType(filename2$)=2 Then
	Proceed("File Is A Directory. Proceed?",False)
EndIf
.nxtcbyte
orginsize$=FileSize(filename2$)
Print FileSize(filename2$)+" Bytes"
.nxtcbyte2
If FileSize(filename2$)<>orginsize$ Then
	Print "File Has Been Modified"
	Proceed("File Has Been Modified!",True)
	Goto nxtcbyte
EndIf
If FileSize(filename2$)=orginsize$ Then
	Print "File Not Modified"
	Delay time2$+"000"
	Goto nxtcbyte2
EndIf
