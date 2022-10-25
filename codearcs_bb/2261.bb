; ID: 2261
; Author: Xyle
; Date: 2008-06-01 15:14:37
; Title: Basic UDP Chat
; Description: A simple client/ server chat program

Graphics3D 500,400,16,2
SetBuffer BackBuffer()

AppTitle("Basic UDP Example")

Global Status$
Global PortIn% = 25000
Global fntArialB = LoadFont("Arial",18,True) 
Global RecMsg$
Global SndMsg$
Global PType%
Global Client%
Global Server%
Global Join_IP$
Global Send_IP%
Global Serv_IP%

Cam = CreateCamera()
PositionEntity Cam,0,200,-20


Choice$ = Input("Would you like to host a chat server or join one? [h or j]")

If Choice = "h" Or Choice = "H" Then
	Server = CreateUDPStream(PortIn) ;Server listens on 25000
	If Server <> 0 Then
		Status = "Server started..."
	Else
		Status = "Error starting server."
	EndIf
	PType = 1 ;Is Player a host?
Else
	Join_IP$ = Input("Enter the ip address of host: [192.168.0.1]")
	If Len(Join_IP) = 0 Then Join_IP = "192.168.0.1"
	Serv_IP = SL_DotToInt(Join_IP) ;need to change entered ip address to ip format so blitz can read
	Client = CreateUDPStream(PortIn) ;Client Listens on 25000
	If Client <> 0 Then
		Status = "Client connected to "+Serv_IP
	Else
		Status = "Error starting client."
	EndIf
	PType = 2 ;Is Player a Client?
EndIf


Color 0,255,0
SetFont fntArialB

While Not KeyHit(1)
	Check_Keys()
	
	If PType = 1 Then Temp% = RecvUDPMsg(Server)
	If PType = 2 Then Temp% = RecvUDPMsg(Client)
	If Temp <> 0 Then DebugLog Temp
	If Temp <> 0 Then
		If PType = 1 Then
			Send_IP = UDPMsgIP(Server) ;Get IP Address of Message Sender
			RecMsg$ = ReadLine$(Server)
		Else
			Send_IP = UDPMsgIP(Client) ;Get IP Address of Message Sender
			RecMsg$ = ReadLine$(Client)
		EndIf
	EndIf
	
	UpdateWorld()
	RenderWorld()
	
	Text 10,20,"Status: "+Status
	Text 10,40,"[Press 1 to send a message, press esc to exit]"
	Text 10,60,"[Client must send first message so server can capture IP address]"
	Color 0,0,255
	If RecMsg <> "" Then Text 10,340,Send_IP+": "+RecMsg
	Color 0,255,0
	Text 10,370,">: "+SndMsg
	Flip
	
Wend
ClearWorld()
End()

Function Check_Keys()
	TKey% = GetKey()
	If TKey <> 0 Then 
		If Tkey <> 8 Then ;8 = backspace key
			TChr$ = Chr$(TKey)
			SndMsg = SndMsg + TChr
		Else
			SndMsg = Left(SndMsg,Len(SndMsg)-1)
		EndIf
	EndIf
	If KeyHit(28) Then ;Enter key
		If PType = 1 And Send_IP <> 0 And SndMsg <> "" Then ;Player is a host, send message to client
			;SndMsg = "Hi there mr client!"
			WriteLine(Server,SndMsg)
			SendUDPMsg(Server,Send_IP,PortIn)
		EndIf
		
		If PType = 2 And SndMsg <> "" Then ;Player is a client, send message to server
			;SndMsg = "Hello mr server!"
			WriteLine(Client,SndMsg)
			SendUDPMsg(Client,Serv_IP,PortIn)
		EndIf
		SndMsg = ""
	EndIf
End Function

Function SL_DotToInt%(ip$)
	;From Blitzbasic Forums 'Chroma'
	;IP address must be converted to an integer
	off1=Instr(ip$,".")	  :ip1=Left$(ip$,off1-1)
	off2=Instr(ip$,".",off1+1):ip2=Mid$(ip$,off1+1,off2-off1-1)
	off3=Instr(ip$,".",off2+1):ip3=Mid$(ip$,off2+1,off3-off2-1)
	off4=Instr(ip$," ",off3+1):ip4=Mid$(ip$,off3+1,off4-off3-1)
	Return ip1 Shl 24 + ip2 Shl 16 + ip3 Shl 8 + ip4
End Function
