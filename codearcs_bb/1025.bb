; ID: 1025
; Author: skidracer
; Date: 2004-05-11 14:16:55
; Title: BlitzLobby example program
; Description: Register your multiplayer games and launch from MSN Messenger sessions.

; mylobby
; blitzplus example code for using blitzlobby userlib
; by simon@nitrologic.net
; download lib from http://www.nitrologic.net/simon/blitzlobby.zip

; modify following for your application
; create .exe and run from windows 
; on first run your program will be registerred 
; warning - IDE launch will fail due to bad SystemProperty("appdir")
; once registerred launch your app from a DXLobby host such as MSN Messenger
; on launch from lobby LobbyInit will return either host or server status

; warning primitive chat program follows (won't see incoming message until you send a message)

appname$="MyLobby"
appexe$="mylobby.exe"
guid$="0123456789ABCDEF"

port=2323

status=LobbyInit()

If status=0
	appdir$=SystemProperty("appdir")
	res=LobbyRegister(guid,appname,"version of card game",appexe,appdir)
	Notify("Regisetered "+appname+Chr$(10)+appdir+appexe+Chr$(10)+"Now available for launch from MSN Messenger")
	End
EndIf

Notify "Lobby Launch Detected using port "+port

Print "lobbyplayer="+LobbyPlayer$()
Print "lobbyhost="+LobbyHost$()
Print "lobbycount="+LobbyCount()
For i=1 To LobbyCount()
	Print "lobbyip["+i+"]="+LobbyIP(i-1)
Next

If status=1
	Print "You are server!"
	server=CreateTCPServer(port) 
	If (server=0) Notify("CreateServer Failed"):End
	While True
		stream=AcceptTCPStream(server) 
		If (stream<>0) Exit
		Delay 10
	Wend	
	Print "Got Connection!"
	WriteLine stream,"Hello From Server"
	chat(stream)
Else
	Print "You are client!"
	While (True)
		stream=OpenTCPStream(LobbyHost$(),port) 
		If stream<>0 Exit
		Print "."
		Delay 50
	Wend
;		If stream=0 Notify"OpenTCPStream Failed":End
	WriteLine stream,"Hello From Client"
	chat(stream)
EndIf

Print "Program Finished, Hit return to continue"
Input
End

Function chat(stream)
	While True
		a$=Input$(">")
		If a$="quit" Return
		WriteLine stream,a$
		If ReadAvail(stream) 
			m$=ReadLine(stream)
			Print m$
		EndIf
	Wend
End Function
