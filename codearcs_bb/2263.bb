; ID: 2263
; Author: Xyle
; Date: 2008-06-07 19:22:59
; Title: Basic TCP Chat
; Description: A basic TCP chat program

Graphics3D 500,400,16,2
SetBuffer BackBuffer()

AppTitle("Basic TCP Example")

Global Status$
Global PortIn% = 25000
Global fntArialB = LoadFont("Arial",18,True) 
Global RecMsg$
Global SndMsg$
Global PType%
Global Client%
Global ClientStrm
Global Server%
Global Join_IP$
Global Send_IP%
Global Serv_IP%
Global OpenChat%

Cam = CreateCamera()
PositionEntity Cam,0,200,-20


Choice$ = Input("Would you like to host a chat server or join one? [h or j]")

If Choice = "h" Or Choice = "H" Then
	Server = CreateTCPServer(PortIn) ;Server listens on 25000
	
	;Check if Server was created
	If Server <> 0 Then
		Status = "Server started..."
	Else
		Status = "Error starting server."
	EndIf
	
	PType = 1 ;Is Player a host?
Else
	Join_IP$ = Input("Enter the ip address of host: [192.168.0.231]")
	If Len(Join_IP) = 0 Then Join_IP = "192.168.0.231" ;if no ip addy is given, this is the default
	
	Client = OpenTCPStream(Join_IP,PortIn) ;Client Listens on 25000
	
	;Check if client connected to server
	If Client <> 0 Then
		Status = "Client connected to "+Join_IP
	Else
		Status = "Error starting client."
	EndIf
	
	PType = 2 ;Is Player a Client?
EndIf


Color 0,255,0
SetFont fntArialB

While Not KeyHit(1)
	Check_Keys()
	
	If PType = 1 Then ;Player is a server
		;Check for new stream
		strStrm = AcceptTCPStream(Server)
		;if there is a new stream, capture the stream
		If strStrm Then
			ClientStrm = strStrm
		Else
			;if there is a captured stream check if there is a message in it
			If ClientStrm <> 0
				If ReadAvail(ClientStrm) Then
					Send_IP = TCPStreamIP(ClientStrm) ;Get IP Address of Message Sender
					RecMsg = ReadLine$(ClientStrm) ;Get the message from the stream
				EndIf
			EndIf
		EndIf
			
	EndIf
	If PType = 2 Then ;Player is a client
		;check if there is a message in the stream
		If ReadAvail(Client) Then
			Send_IP = TCPStreamIP(Client) ;Get IP Address of Message Sender
			RecMsg = ReadLine$(Client) ;Get the message from the stream
		End If
	EndIf
	
	UpdateWorld()
	RenderWorld()
	
	Text 10,20,"Status: "+Status
	Text 10,40,"[Press any keys to type a message, return to send it]"
	Text 10,60,"[Press enter to send a message, press esc to exit]"
	Text 10,80,"[Client must send first message so server can capture Client Stream]"
	Text 10,100,"strStrm: "+strStrm
	Text 10,120,"ClienStrm: "+ClientStrm
	Text 10,140,"Port: "+PortIn
	Text 10,160,"PType: "+PType
	Color 0,0,255
	If RecMsg <> "" Then Text 10,340,Send_IP+": "+RecMsg
	Color 0,255,0
	Text 10,370,">: "+SndMsg
	Flip
	
Wend
If PType = 1 Then 
	If ClientStrm <> 0 Then
		;Close Server stream and server
		CloseTCPStream(ClientStrm)
		CloseTCPServer(Server) 
	EndIf
EndIf
If PType = 2 Then CloseTCPStream(Client) ;Close client TCP Stream
ClearWorld()
End()

Function Check_Keys()
	;Grab any keys that get pressed
	TKey% = GetKey()
	
	If TKey <> 0 Then 
		If Tkey <> 8 Then ;8 = backspace key
			;convert pressed key to the actual character
			TChr$ = Chr$(TKey)
			;append string with last key pressed
			SndMsg = SndMsg + TChr
		Else
			;if backspace key is hit, erase last letter of message
			If Len(SndMsg) > 0 Then SndMsg = Left(SndMsg,Len(SndMsg)-1)
		EndIf
	EndIf
	
	;hitting enter key will send contents of sndmsg to client/server
	If KeyHit(28) Then ;Enter key
		If PType = 1 And ClientStrm <> 0 And SndMsg <> "" Then ;Player is a host, send message to client
			;send message to Client
			WriteLine(ClientStrm,SndMsg)
		EndIf
		
		If PType = 2 Then ;And SndMsg <> "" Then ;Player is a client, send message to server
			;Send message to server
			WriteLine(Client,SndMsg)
		EndIf
		SndMsg = "" ;message was sent, clear message to make room for a new message
		OpenChat = 0
	EndIf
End Function
