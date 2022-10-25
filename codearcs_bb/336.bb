; ID: 336
; Author: skn3[ac]
; Date: 2002-06-04 12:52:15
; Title: [UPDATED 3/2/2003] UDP client / Server
; Description: working example of a UDP host/client model.

Graphics 640,300,32,2
AppTitle "Udp functions"

;Protocol
;Msg Id
;1 = Connect [C -> S]
;2 = Accept  [? -> ?] + username
;3 = Pong    [? -> ?]
;4 = Error   [? -> ?]
;5 = Notice  [S -> C]

;Ping Byte
;0 = NoPing
;1 = Ping

;Welcome Msg
Global UdpGameName$="TestName"

;Error Codes
Dim Error$(5)
Error$(1) = "Game already started"
Error$(2) = "Server is busy try again soon!
Error$(3) = "Ilegal protocol"
Error$(4) = "Check that TCP and UDP are not blocked, or in use, on the specified port"
Error$(5) = "No response from server"

Global UdpServer=True
Global UdpStream,TcpStream
Global UdpServerName$="Mr host",UdpClientName$="Guest"
Global Udp=False
Global UdpBusy=False,UdpIp=0,UdpPort=0
Global UdpSaveIp=0,UdpSavePort=0
Global UdpPing=False,UdpPingStart=MilliSecs(),UdpPingResult=0
Global UdpTime=False,UdpTimeStart=MilliSecs(),UdpTimeOut=5000,UdpTimeRetry=1

Print "-----------------------"
Print "Welcome to the udp test"
Print "-----------------------"
Print ""
Print "Type a user name"
GetName$=Input$("> ")

Print ">"
Print "Choose a mode :"
Print " 0 = client"
Print " 1 = server"
GetMode=Input("> ")

If GetMode=0 Then
	Print ">"
	Print "Type the local port you want to use"
	GetLocalPort=Input("> ")
	Print ">"
	Print "Type server address :"
	Print "( This can be xxx.xxx.xxx.xxx or www.address.com )"
	GetAd$=Input$("> ")
	Print ">"
	Print "Type server port :"
	GetPort=Input("> ")
	GetResult=JoinGame(GetName$,GetAd$,GetLocalPort,GetPort)
	If GetResult > 0 Then 
		Print Error$(GetResult)
		Goto EndOfApp
	End If
Else
	Print ">"
	Print "Type a name for the server"
	GetGameName$=Input$("> ")
	Print ">"
	Print "Type server port :"
	GetPort=Input("> ")
	GetResult=HostGame(GetGameName$,GetName$,GetPort)
	If GetResult > 0 Then 
		Print Error$(GetResult)
		Goto EndOfApp
	End If
End If

While KeyDown(1)=False
	Select UdpRuntime()
		Case 5
			Print Error$(5)
			Exit
	End Select
Wend

If UdpStream>0 Then CloseUDPStream(UdpStream)
If TcpStream>0 Then CloseTCPServer(TcpStream)
.EndOfApp
Print ""
Print "Application end"
Print "Press any key..."
WaitKey()

Function UdpRuntime()
	RecvUDPMsg(UdpStream)
	MsgLength=ReadAvail(UdpStream)
	If MsgLength > 0 Then
		;Read header bytes
		GetMode=ReadByte(UdpStream)
		GetPing=ReadByte(UdpStream)
		
		;Read Msg Properties
		UdpIp=UDPMsgIP(UdpStream)
		UdpPort=UDPMsgPort(UdpStream)
		
		;Process ping/pong
		If GetPing=True Then
			;Send Pong Msg
			WriteUdpByte(UdpStream,3)
			WriteUdpByte(UdpStream,0)
			SendUDPMsg(UdpStream,UdpIp,UdpPort)
		End If
		
		Select GetMode
			Case 1 ;[SERVER]
				If UdpServer=True
					;Is a Server
					If Udp=False Then
						If UdpBusy=False Then
							;Connection Accepted. Save IP and Port
							UdpBusy=True
							UdpSaveIp=UdpIp
							UdpSavePort=UdpPort
							;Send Accept echo
							WriteUdpByte(UdpStream,2) ; Msg ID
							WriteUdpByte(UdpStream,0) ; Ping Status
							WriteUdpString(UdpStream,UdpServerName$)
							SendUDPMsg(UdpStream,UdpIp,UdpPort)
							Print "User Connecting ..."
						Else
							;Server Is Busy send error
							WriteUdpByte(UdpStream,4) ; Msg ID
							WriteUdpByte(UdpStream,0) ; Ping Status
							WriteUdpByte(UdpStream,2) ; Msg Data
							SendUDPMsg(UdpStream,UdpIp,UdpPort)
						End If
					Else
						;Server is already in progress.
						;Send Error Msg
						WriteUdpByte(UdpStream,4) ; Msg ID
						WriteUdpByte(UdpStream,0) ; Ping Status
						WriteUdpByte(UdpStream,1) ; Msg Data
						SendUDPMsg(UdpStream,UdpIp,UdpPort)
					End If
				Else
					;Is a Client
					;Send Protocol error msg
					WriteUdpByte(UdpStream,4) ; Msg ID
					WriteUdpByte(UdpStream,0) ; Ping Status
					WriteUdpByte(UdpStream,3) ; Msg Data
					SendUDPMsg(UdpStream,UdpIp,UdpPort)						
				End If
				
			Case 2 ;[CLIENT]
				If UdpServer=True
					;Is a Server
					If Udp=False Then
						If UdpBusy=True Then
							;Grab client username
							UdpClientName$=ReadUdpString$(UdpStream)							
							Print "Client is called '" + UdpClientName$ + "'"
							;Send Notice plus ping
							WriteUdpByte(UdpStream,5)
							WriteUdpByte(UdpStream,1)
							WriteUdpString(UdpStream,UdpGameName$)
							SendUDPMsg(UdpStream,UdpSaveIp,UdpSavePort)
							;Save Ping Data
							UdpPing=True
							UdpPingStart=MilliSecs()
						End If
					End If
				Else
					;Is a Client
					If Udp=False Then
						If UdpBusy=True Then
							Print "Connection Accepted!"
							;Connection allowed. Save IP and Port
							UdpSaveIp=UdpIp
							UdpSavePort=UdpPort
							;Get Server username from msg
							UdpServerName$=ReadUdpString$(UdpStream)
							Print "Server is called '" + UdpServerName$ + "'"
							;Send Client Accept+User+Ping
							WriteUdpByte(UdpStream,2) ; Msg ID
							WriteUdpByte(UdpStream,1) ; Ping Status
							WriteUdpString(UdpStream,UdpClientName$)
							SendUDPMsg(UdpStream,UdpIp,UdpPort)
							;Save Ping Data
							UdpPing=True
							UdpPingStart=MilliSecs()
						End If
					End If
				End If
			Case 3 ; [CLIENT / SERVER]
				;Recieved a pong (ping is complete).
				If UdpPing=True Then
					If UdpBusy=True Then
						If Udp=False Then
							;Connection complete
							Udp=True
							UdpBusy=False
							;Close Tcp Name Lookup server
							If UdpServer=True Then CloseTCPServer(TcpStream)
							
							Print ""
							Color 255,0,0
							Print "Link Completed"
							Color 255,255,255
						End If
					End If
					UdpPing=False
					UdpPingResult=MilliSecs()-UdpPingStart
					Print "Ping " + UdpPingResult + "ms"
				End If
			Case 4 ; [CLIENT / SERVER]
				Print "ERROR : " +Error$(ReadByte(UdpStream))
			Case 5 ; [CLIENT]
				If UdpServer=False Then
					If Udp=True Then
						Color 75,75,255
							Print ReadUdpString$(UdpStream)
						Color 255,255,255
					End If
				End If
		End Select
	End If
	;Update Timer
	If UdpTime=True Then
		If MilliSecs()-UdpTimeStart > UdpTimeOut Then
			UdpTimeStart=MilliSecs()
			If UdpTimeRetry > 0 Then
				UdpTimeRetry=UdpTimeRetry-1
				;Attempts events
				If UdpServer=True Then
					;Server
				Else
					;Client
					If Udp=True Then
						;Connected
					Else
						;Not Connected
						If UdpBusy=True Then
							;Attempting Connect
							;Send another attempt at connect
							WriteByte(UdpStream,1)
							WriteByte(Udpstream,0)
							SendUDPMsg(UdpStream,UdpSaveIp,UdpSavePort)
							Print "Retrying ..."
						End If
					End If
				End If
			Else
				;TIMEOUT!
				If UdpServer=True Then
					;Server
				Else
					;Client
					If Udp=True Then
						;Connected
					Else
						;Not Connected
						If UdpBusy=True Then
							;No Resposne when attempting to connect
							Return 5
						End If
					End If
				End If
			End If
		End If
	End If
	;No errors
	Return 0
End Function

Function HostGame(GameName$,UserName$,Port)
	UdpServer=True
	UdpBusy=False
	Udp=False
	UdpServerName$=UserName$
	UdpGameName$=GameName$
	;Make sure Game Name is longer than 0
	If Len(UdpGameName$)=0 Then UdpGameName$="Unnamed Game"
	;Create Streams
	UdpStream=CreateUDPStream(Port)
	TcpStream=CreateTCPServer(Port)
	If UdpStream=0 Or TcpStream=0 Then
		;TCP or UDP is in use on the given port
		;Return Error number
		If UdpStream>0 Then CloseUDPStream(UdpStream)
		If TcpStream>0 Then CloseTCPStream(TcpStream)
		Return 4		
	End If
	;Server was started correctly
	Print "Server Started!"
	Return 0
End Function

Function JoinGame(UserName$,Address$,LocalPort,ServerPort)
	UdpServer=False
	UdpBusy=True
	Udp=False
	UdpClientName$=UserName$
	;Create Streams
	UdpStream=CreateUDPStream(LocalPort)
	If UdpStream=0 Then Return 4
	;Setup timer for retry attempt
	UdpTime=True
	UdpTimeStart=MilliSecs()
	UdpTimeOut=5000
	UdpTimeRetry=3
	;Write Connect string
	UdpSaveIp=GetIpInt(Address$,ServerPort)
	UdpSavePort=ServerPort
	WriteByte(UdpStream,1)
	WriteByte(Udpstream,0)
	SendUDPMsg(UdpStream,UdpSaveIp,UdpSavePort)
	Print "Connecting to "+DottedIP$(UdpSaveIp)
	;Join was done correctly
	Return 0
End Function

Function ReadUdpString$(Stream)
	MakeString$=""
	While Eof(Stream)=False
		MakeString$=MakeString$+Chr$(ReadByte(Stream))
	Wend
	Return MakeString$
End Function

Function WriteUdpByte(Stream,TheByte)
	WriteByte(Stream,TheByte)
End Function

Function WriteUdpString(Stream,TheString$)
	For I=1 To Len(TheString$)
		WriteUdpByte(Stream,Asc(Mid$(TheString,I,1)))
	Next
End Function

Function GetIpInt(Address$,Port=0)
	;Check for character type
	Found=False
	For I=1 To Len(Address$)
		GetAsc=Asc(Mid$(Address$,I,1))
		If GetAsc < 48 Or GetAsc > 57 
			If GetAsc <> 46 Then
				Found=True
				Exit
			End If
		End If
	Next
	If Found=False Then
		Return GetIpFromDotted(Address$)
	Else
		Return GetIpFromName(Address$,Port)
	End If
End Function

Function GetIpFromDotted(inputip$)
	break1 = Instr(inputIP$,".") : break2 = Instr(inputIP$,".",break1+1) : break3 = Instr(inputIP$,".",break2+1)
	add1 = Mid(inputIP$,1,break1-1):add2 = Mid(inputIP$,break1+1,break2-1):add3 = Mid(inputIP$,break2+1,break3-1):add4 = Mid(inputIP$,break3+1)
	ipreturn=(add1 Shl 24) + (add2 Shl 16) + (add3 Shl 8) + add4
	Return ipreturn	
End Function

Function GetIpFromName(Name$,Port)
	Print "Looking up address " + Name$ + "..."
	Delay 100
	OpenStream=OpenTCPStream(Name$,Port)
	If OpenStream=0 Then Return 0
	RealAddress=TCPStreamIP(OpenStream)
	CloseTCPStream(OpenStream)
	Return RealAddress
End Function
