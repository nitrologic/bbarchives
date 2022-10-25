; ID: 487
; Author: ckob
; Date: 2002-11-14 07:15:59
; Title: saving files on a remote system
; Description: saves files to a server

;---------------------------SERVER--------------------------
;small example on how to write to files over the net
;BY:Ckob

mainsvr=CreateTCPServer(8080) ;create the server
If mainsvr<>0 Then 
Print "Server started successfully."
Else
Print "Server failed to start."
End
End If
While Not KeyHit(1)
strStream=AcceptTCPStream(mainsvr)
If strStream Then 
msg$ =ReadString$(strStream)
Print msg$
Delay 2000
fileout = WriteFile("example.dat")
WriteLine( fileout, msg$ )
CloseFile(fileout)

Else 
Print "No message has been recieved yet yet"
Delay 1000
End If 
Wend
End
;-----------------------------------------------------------




;---------------------------Client--------------------------
;small example on how to write to files over the net
;BY:Ckob
mainsvr=OpenTCPStream("127.0.0.1",8080)
If mainsvr<>0 Then 
Print "Client Connected successfully."
Else
Print "Server failed to connect."
WaitKey 
End
End If
name$ = Input("Name: ")
FlushKeys
; write stream to server
WriteString mainsvr,name$
;-----------------------------------------------------------
