; ID: 126
; Author: Oldefoxx
; Date: 2001-11-04 21:41:25
; Title: Pattern$() Function
; Description: Makes it easy to identify groups of letters/numbers

Graphics 640,480
ClsColor 0,0,128
Cls
Print "If you had a social security number, it would be in the form of"
Print pattern$("123-45-6789")+" or "+pattern$("A123-45-6789")
Print
Print "If you had a date, such as Jan 12, 2001, it would be in the form"
Write " of "+pattern$("Jan 12, 2001")+", "+pattern$("01/12/2001")+", "
Print pattern$("01-12-2001")+", or something similar."
Print
Print "If you had a phone number replaced by "+pattern$("(123) 456-7890")+","
Print "it would also be very easy to recognize, right?"
Print
Print "And you can probably guess what this pattern:  "+pattern$("12:30 am")
Print "would most likely represent."
Print
Print "But wait!  This one could be hard:  "+pattern$("Calhoun, John C.")
WaitKey
End


Function pattern$(sstring$)
Local a,temp$=""
For a=1 To Len(sstring$)
  Select Mid(sstring$,a,1)
  Case "0","1","2","3","4","5","6","7","8"
    temp$=temp$+"9"  
  Case "B","C","D","E","F","G","H","I","J","K","L","M","N"
    temp$=temp$+"A"
  Case "O","P","Q","R","S","T","U","V","W","X","Y","Z"
    temp$=temp$+"A"
  Case "b","c","d","e","f","g","h","i","j","k","l","m","n"
     temp$=temp$+"a"
  Case "o","p","q","r","s","t","u","v","w","x","y","z"
     temp$=temp$+"a"
  Default
     temp$=temp$+Mid(sstring$,a,1)
  End Select
Next
Return temp$
End Function

