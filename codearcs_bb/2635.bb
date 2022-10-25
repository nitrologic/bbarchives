; ID: 2635
; Author: mark1110
; Date: 2009-12-31 01:59:34
; Title: chatroom
; Description: a program

AppTitle "deh-chatroom"
Graphics 1000,700,16,2
Graphics 100,70,16,2
StartNetGame ()
Graphics 1000,700,16,2
n$ = Input$ ("name : ")
nu = Input ("1-10")
CreateNetPlayer (n$)
Repeat
	If RecvNetMsg () And NetMsgType < 0 And NetMsgType > 10 Then Print NetMsgData$ () + NetMsgFrom()
	If KeyHit (199) Then send = True And se$ = Input$ ("message :  ")
	If send = True Then SendNetMsg nu,se$,n$,0,0 And send = False
Until KeyHit (1)
DeleteNetPlayer n$
End
