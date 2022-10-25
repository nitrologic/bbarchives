; ID: 3262
; Author: RemiD
; Date: 2016-03-26 12:24:31
; Title: say something or ask something to the user
; Description: to say something to the user, to ask something to the user

Graphics3D(1000,625,32,2)

setbuffer(backbuffer())
Say("Hello, welcome to this simple say/ask example program")
TAnswer$ = Ask("What is your name ?")
Say("Have a nice day, "+TAnswer+".")

End()

Function Say(Message$,ClearScreen%=True)

 If(ClearScreen = True)
  ClsColor(000,000,000)
  Cls()
  Locate(0,0)
  Color(125,000,250)
 EndIf

 Print(Message)
 Print("Press any key to continue")

 FlushKeys()
 WaitKey()

End Function

Function Ask$(Message$,ClearScreen%=True)

 If(ClearScreen = True)
  ClsColor(000,000,000)
  Cls()
  Locate(0,0)
  Color(125,000,250)
 EndIf

 FlushKeys()
 TAnswer$ = Input$(Message)
 ;DebugLog("TAnswer = "+TAnswer)
 Return TAnswer

End Function
