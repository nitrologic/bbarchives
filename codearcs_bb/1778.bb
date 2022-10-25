; ID: 1778
; Author: Ked
; Date: 2006-08-05 22:25:44
; Title: Wrong Client Stopper
; Description: Stops unregistered clients from logging into the server.

;SERVER PART;
Graphics 400,200,32,2
SetBuffer BackBuffer()
AppTitle "Server"

Global msg$,server,t,file,c_name$

server = CreateTCPServer(8080)     ;CREATE SERVER
If server=0 Then End

While Not KeyHit(1)     ;LOOP
Cls

t = AcceptTCPStream(server)

If t     ;CLIENT ASKING SERVER
     msg$=ReadLine(t)
     If msg$="Login to server"
          WriteLine t,"Client name?"
          msg$=ReadLine(t)
               file=ReadFile("Clients.txt")     ;OPEN FILE
               
               Repeat      ;LOOP 2
               c_name$=ReadLine(file)
               If Eof(file) then CloseFile(file):Exit
               Until c_name$=msg$
               
               If c_name$=msg$
                    WriteLine t,"Access Granted"
               Else
                    WriteLine t,"Access Denied"
               EndIf
          EndIf
     EndIf

Flip
Wend
End

;-THE .TXT FILE-;
;Create a simple .txt file. On the first line type "tneilc"
;without the quotes. Save the file with the name ;"Clients.txt", without the quotes.

;CLIENT PART;
Graphics 640,480,32,2
SetBuffer BackBuffer()
AppTitle "Client"

Global c,msg$,clientname$="tneilc"

c=OpenTCPStream("127.0.0.1",8080)
If c=0 Then End

Print "Connected"
Delay 50
Print "Asking to log in"
Delay 100

WriteLine c,"Login to server"
msg$=ReadLine(c)
Print msg$
WriteLine c,clientname$
msg$=ReadLine(c)
Print msg$

WaitKey()
End
