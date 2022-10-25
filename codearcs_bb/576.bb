; ID: 576
; Author: Insane Games
; Date: 2003-02-06 13:21:43
; Title: Simple way to separete/cut a string
; Description: Get just the usefull part of a string

Cuts the string and gets only the info you want
 Ex: If CString$ is "abc/def/" and CNumber if 2, then the function will return "def".
* DO NOT FORGET TO INCLUDE A "/" IN THE END OF THE STRING *

 This little function was made by Insane Games (david@infomaniacos.org). If you know how to make it goes faster or how to avoid the use of the last "/", please, send me an email. Thanx.

Function CutString$(CString$, CNumber)
  stemp$ = ""
  numtemp = 0
  For temp=1 To Len(CString$)
    If Mid(CString$, temp, 1)<>"/"
      stemp$ = stemp$ + Mid(CString$, temp, 1)
    Else
      numtemp = numtemp + 1
      If numtemp = CNumber
        Return stemp$
      Else
        stemp$ = ""
      EndIf
    EndIf
  Next

  Return ""
End Function


;Print CutString$("abc/def/", 2) ;- just for testing
