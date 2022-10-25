; ID: 3263
; Author: dw817
; Date: 2016-04-03 19:25:36
; Title: Fix Subtitle SRT Uppercase
; Description: Converts any text from SRT to proper case denoted by punctuation marks

'     ________________________________________
'    //                                     //
'   // "Fix Subtitle SRT Uppercase"        //
'  // Version: "Shift Key"                //
' // Written by David W (dw817) 04/03/16 //
'//_____________________________________//
'
' Being quite familiar with Subtitle Edit, one feature I
' noticed that was absent in it was the ability to take
' beginning sentences and ensure they are uppercase.
'
' This is needed if you take CLOSE CAPTIONING and convert it.
'
' While Subtitle Edit has a very nice library to determine
' what proper names need to begin Uppercase, it does not do
' the same for normal sentences ending in punctuation marks.
'
' Very surprising.
'
' So I made this quick program to rectify that situation.
' BEWARE ! Saves changes back to original file. Backup
' first If you just want to experiment.

Strict
Local c$,f$,o$,t$,u$,i,br,uc=1,fp:TStream

f$=RequestFile("* LOAD SRT FILE *") ' load select file
If f$="" Or KeyDown(27) Then End ' exit if blank or ESCAPE key

fp=ReadStream(f$) ' get file handle for SRT file
t$=LoadText(fp) ' load it ALL in one swoop into T$
CloseStream fp ' close file

For i=0 Until Len(t$)
  c$=t$[i..i+1] ' read each character one at a time.
  If c$="<" Then br=1 ' ensure we are not inside a bracket command
  If c$=">" And br=1 Then br=0 ' if we are, ignore uppercase requests
  If br=0
    u$=Upper$(c$)
    If uc=1 And u$>="A" And u$<="Z" Then c$=u$ ; uc=0 ' must be a letter to uppercase and flag completion
    If c$="." Or c$="!" Or c$="?" Then uc=1 ' only flag for uppercase with these characters
  EndIf
  o$:+c$ ' add to output text
Next

Print o$ ' show output in debug screen
fp=WriteStream(f$) ' write to file we just read from
WriteString fp,o$ ' enter contents of OUTPUT text
CloseStream fp ' close file
