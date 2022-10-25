; ID: 2781
; Author: EdzUp[GD]
; Date: 2010-10-22 14:35:11
; Title: EdzUp Network system
; Description: EdzUp's complete networking system

'
'	EdzUpNetworkSystem.bmx - Copyright ©EdzUp
'	Programmed by Ed 'EdzUp' Upton
'

SuperStrict
Import BRL.LinkedList
Import BRL.System
Import BRL.Socket
Import BRL.SocketStream
Import BRL.Retro
Import MaxGUI.MaxGUI
Import BRL.EventQueue
Import BRL.PNGLoader

' modules which may be required:
' Import BRL.BMPLoader
' Import BRL.TGALoader
' Import BRL.JPGLoader

'network variables
Global LocalSocket:TSocket = Null				'local socket for data transfer
Global LocalStream:TSocketStream = Null			'local stream for data transfer
Global LocalBuffer:String = ""				'the local streams buffer
Global EdzUpNetwork_Connected:Byte = False			'true if networking is up and running
Global EdzUpNetwork_Hosting:Byte = False			'True if this is the server
Global EdzUpNetwork_LocalID:Long = 0				'the ID that the server has sent you
Global EdzUpNetwork_MessageCount:Long = 0			'this is the message count
Global EdzUpNetwork_PacketStart:String = "<EPB>"		'this is the packet beginning
Global EdzUpNetwork_PacketEnd:String = "<EPE>"		'this is the packet end
Global EdzUpNetwork_ServerName:String = ""			'this is the server name for advertising purposes
Global EdzUpNetwork_ServerIP:String = ""			'this is the servers IP address
Global EdzUpNetwork_ServerPassword:String = ""		'the server password, change from "" if the server requires a password
Global EdzUpNetwork_NetworkError:Long = 0			'0 if there is no error
Global EdzUpNetwork_PingTimeout:Long = 120			'after 120 seconds your kicked
Global EdzUpNetwork_LocalPort:Long = 12233			'port for the networking system

Global EdzUpNetwork_PlayerCount:Long = 0			'number of players currently in the game
Global EdzUpNetwork_PlayersMaximum:Long = 64		'number of connections accepted by the system
Global EdzUpNetwork_MaximumID:Long = 0			'this is the Maximum ID that has been allocated

'INTERNAL GLOBALS
Global EdzUpNetwork_Internal_UsingID:Byte = False	'True if a client is using the ID they should be using (not 0)
Global EdzUpNetwork_Internal_ServerPasswordOk:Byte = False	'True if the server password has been accepted
Global EdzUpNetwork_Internal_UserAccepted:Byte = False	'True if the user has been accepted
Global EdzUpNetwork_Internal_Authenticate:Byte = False

'timing system for networking
Global EdzUpNetwork_NetworkTime:String = CurrentTime()	'this is the network time (for ping check etc)
Global EdzUpNetwork_SecondsPassed:Long = 0			'see how many seconds have passed

Global EdzUpNetwork_LocalUsername:String = ""		'the username for the local machine
Global EdzUpNetwork_LocalPassword:String = ""		'the local password for this machine
Global EdzUpNetwork_LocalServerPassword:String = ""	'The password for the local machine to be send to authenticate with the server

Global EdzUpNetwork_LocalNoNetworkGame:Byte = False	'true if this is a local game WITHOUT networking

Global CompressBank:TBank = CreateBank( 4 )		'used in conversion routines

Global DebugHandle:TStream = Null
Global DebugFileName:String = ""

' -----------------------------------------------------------------------------
' These globals MUST be included in your code!
'James L Boyd code
' -----------------------------------------------------------------------------
Global GT_Start:Long	' Start of game time
Global GT_Last:Int		' Last INTEGER value of MilliSecs ()
Global GT_Current:Long	' LONG value updated by MilliSecs ()

Type UserType
	Field Name:String						'the username of this client
	Field Password:String					'the password relating to this user
End Type
Global User:UserType = Null
Global UserList:TList = CreateList()

Type BannedType
	Field IP:String						'the ip address that is banned
	Field Timer:Long						'how long it has left to be banned (-1 permanent)
	Field Count:Long						'how many times the ip has been banned
	Field Reason:String					'the reason they have been banned
End Type
Global Banned:BannedType = Null
Global BannedList:TList = CreateList()

Type DisplayType
	Field Data:String
End Type
Global Display:DisplayType = Null
Global DisplayList:TList = CreateList()

Type PacketType
	Field PacketFrom:Long							'who the packet is from
	Field PacketTo:Long							'the id of who the packet is for
	Field Id:Long								'the message ID for receipt checking, so a receipt for message 16853 relates to packet 16853 so it can be deleted
	Field Data:String								'the message's data packet
	Field Class:Long								'the class of the message
	Field Important:Byte							'True if a receipt is required for this message
	Field Stream:TSocketStream						'this is who sent it
	
	Function Create:PacketType()
		Return New PacketType						'function to create a new packet type
	End Function
End Type
Global PacketList:TList = CreateList()
Global Packet:PacketType = Null

Type ReceiptType
	Field ReceiptFrom:Long							'who the receipt is from
	Field ReceiptTo:Long							'who it is to
	Field ID:Long									'the ID of the receipt
	Field Data:String								'what the receipt contains (if it needs resending)
	Field Class:Long								'the class of the message held as a receipt
	
	Field Timer:Long								'timer to see how long the receipt has got till resending
	Field ReceiptCurrentTime:String					'used for timer checking
	
	Function Create:ReceiptType()
		Return New ReceiptType
	End Function
End Type
Global ReceiptList:TList = CreateList()
Global Receipt:ReceiptType = Null
Global NetworkReceiptTimer:Long = Floor( ( EdzUpNetwork_PingTimeout +2 )/10 )'this is the time that the system will wait before resending the data
If NetworkReceiptTimer<5 Then NetworkReceiptTimer =5 'stop any mad sending problems (minimum 5 seconds)

Global EdzUpNetwork_ReceiptCount:Long = 0

Rem
EdzUpNetwork_ServerStartup:Byte( Port:Int )	.	.	.	this will startup the server [WORKING]
EdzUpNetwork_Connect:Byte( IP:Int, Port:Int )	.	.	.	this will connect the client to the server [WORKING]
EdzUpNetwork_AlreadyExist:Byte( Username:String ).	.	.	returns true if this username is already in use on the server

NETWORK ERRORS
0) No errors
1) Ping Timeout
2) Server Password failed
3) Username or password failed
4) Server error, closed connection
EndRem

'System Constants
Const NETWORK_AUTHORISATION:Int = $FFFF				'if someone is authorised
Const NETWORK_SERVERPASSWORD:Int = $FFFE			'Clients server password
Const NETWORK_PASSWORD:Int = $FFFD					'someone is changing thier password
Const NETWORK_PINGTIMEOUT:Int = $FFFC				'someone has pinged out
Const NETWORK_PING:Int = $FFFB					'someone is pinging something
Const NETWORK_DISCONNECT:Int = $FFFA				'a client disconnected from the server
Const NETWORK_SERVERMESSAGE:Int = $FFF9				'a message from the server
Const NETWORK_CLIENTINFORMATION:Int = $FFF8			'this will update all clients with the relevant information
Const NETWORK_CLIENTMESSAGE:Int = $FFF7				'this is a message from the client
Const NETWORK_MESSAGE:Int = $FFF6					'this is a universal network message
Const NETWORK_RECEIPT:Int = $FFF5					'this is a network receipt for a message
Const NETWORK_CHAT:Int = $FFF4					'this is a chat packet
Const NETWORK_CONNECT:Int = $FFF3					'when someone connects
Const NETWORK_FILETRANSFER:Int = $FFF1				'a file is being sent between systems
Const NETWORK_CLIENTID:Int = $FFF0					'for quick permiation of ID's

Type ClientType
	Field ID:Long						'the ID of the client, given by the server
	Field Username:String					'thier username
	Field Authenticated:Byte				'True of this user has authenticated thier password
	Field Syncronising:Long					'what it is current syncronising (-1 none, >0 that ID or -2 complete)
	Field PacketID:Long					'packet ID that is sent on important packets
	Field Ping:Long						'ping time for this computer
	Field Cheating:Byte						'True if this player is cheating
	Field TimesCheated:Long					'how many times they have cheated
	
	Field Socket:TSocket					'thier socket (reqiured for networking)
	Field Stream:TSocketStream				'required for networking
	Field Buffer:String					'this is the data buffer
End Type
Global Client:ClientType = Null
Global ClientList:TList = CreateList()

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_CloseClient()
	CloseStream LocalStream
	CloseSocket LocalSocket
	Delay(1000)
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_KillClientConnections( ID:Long, KillAll:Byte = False )
	For Client:ClientType = EachIn ClientList
		If Client<>Null
			If Client.ID = ID Or KillAll=True
				CloseStream( Client.Stream )
				CloseSocket( Client.Socket )
			EndIf
		EndIf
	Next
End Function

Function EdzUpNetwork_ShutdownNetwork()
	'kill every connection
	EdzUpNetwork_KillClientConnections( 0, True )
	Delay( 1000 )
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_ResetNetworkVariables()
	LocalSocket = Null				'local socket for data transfer
	LocalStream = Null			'local stream for data transfer
	LocalBuffer = ""				'the local streams buffer
	EdzUpNetwork_Connected = False			'true if networking is up and running
	EdzUpNetwork_Hosting = False			'True if this is the server
	EdzUpNetwork_LocalID = 0				'the ID that the server has sent you
	EdzUpNetwork_MessageCount = 0			'this is the message count
	EdzUpNetwork_ServerName = ""			'this is the server name for advertising purposes
	EdzUpNetwork_ServerIP = ""			'this is the servers IP address
	EdzUpNetwork_ServerPassword = ""		'the server password, change from "" if the server requires a password
	EdzUpNetwork_NetworkError = 0			'0 if there is no error
	EdzUpNetwork_LocalPort = 12233			'port for the networking system

	EdzUpNetwork_Internal_UsingID = False	'True if a client is using the ID they should be using (not 0)
	EdzUpNetwork_Internal_ServerPasswordOk = False	'True if the server password has been accepted
	EdzUpNetwork_Internal_UserAccepted = False	'True if the user has been accepted
	EdzUpNetwork_Internal_Authenticate = False

	EdzUpNetwork_LocalServerPassword = ""	'The password for the local machine to be send to authenticate with the server

	EdzUpNetwork_LocalNoNetworkGame = False	'true if this is a local game WITHOUT networking
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_UpdateClient:Byte( ID:Long, Operator:Long, Value:String )
	'this will update the client with the relevant information
	For Client:ClientType = EachIn ClientList
		If Client<>Null
			If Client.ID = ID
				Select Operator
				Case 1	'Authenticated status change
					Client.Authenticated = True
				Default	'update the username
					Client.Username = Value
				End Select
			EndIf
		EndIf
	Next
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_Authenticate:Byte( ServerPassword:String, Username:String, UserPassword:String )
	Local Timer:String = CurrentTime()
	Local SecsPass:Long = 0
	Local Received:String = ""
	Local Available:Long = 0
	Local CE:Long = 0

	'Get a local ID for the connection
	EdzUpNetwork_Debug( "Getting ID" )
	DebugLog "Getting ID"+Chr(10)
	Repeat
		If CurrentTime()<>Timer
			SecsPass :+ 1
			Timer = CurrentTime()
			
			If EdzUpNetwork_LocalID=0
				'ask server for ID
				EdzUpNetwork_Message( EdzUpNetwork_LocalID, 0, NETWORK_CLIENTID, "ID?", False, LocalStream )
			EndIf
		EndIf

		CE= EdzUpNetwork_UpdateNetwork()				'this will update the whole network then adjust the ID
		If CE<>0
			'there is an error of sorts
			EdzUpNetwork_Debug( "ID:Network Error set to "+CE )
			Return False
		EndIf
		
		Delay( 1 )
	Until EdzUpNetwork_LocalID<>0 Or SecsPass>60
	
	'failed to authenticate
	If SecsPass>60 Then Return False
	
	EdzUpNetwork_Debug( "Authenticating server password..." )
	SecsPass = 0
	Repeat
		If CurrentTime()<>Timer
			SecsPass :+ 1
			Timer = CurrentTime()
			
			If EdzUpNetwork_Internal_ServerPasswordOk = False
				'Send the server password that the client has
				EdzUpNetwork_Message( EdzUpNetwork_LocalID, 0, NETWORK_SERVERPASSWORD, ServerPassword, False, LocalStream )
			EndIf
			
			CE = EdzUpNetwork_UpdateNetwork()
			If CE<>0
				EdzUpNetwork_Debug( "SP:Network error set to "+CE )
				Return False
			EndIf
		EndIf
		Delay( 1 )
	Until EdzUpNetwork_Internal_ServerPasswordOk = True Or SecsPass>60
	If SecsPass>60 Then Return False

	EdzUpNetwork_Debug( "Authenticating user details..." )
	SecsPass = 0
	Repeat
		If CurrentTime()<>Timer
			SecsPass :+ 1
			Timer = CurrentTime()
			
			If EdzUpNetwork_Internal_UserAccepted = False
				'Send the server password that the client has		
				EdzUpNetwork_Message( EdzUpNetwork_LocalID, 0, NETWORK_AUTHORISATION, Username+"¬"+MD5( UserPassword ), False, LocalStream )
			EndIf
			
			CE = EdzUpNetwork_UpdateNetwork()
			If CE<>0
				EdzUpNetwork_Debug( "UD:Network error set to "+CE )
				Return False
			EndIf
		EndIf
		Delay( 1 )
	Until EdzUpNetwork_Internal_UserAccepted = True Or SecsPass>60
	If SecsPass>60 Then Return False
	Return True
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_UpdateNetwork:Long( )
	Local TempClientWaiting:TSocket = Null
	Local AvailableBytes:Long = 0
	Local CurrentByte:Byte = 0
	Local TempReceive:String = ""
	Local CB:Long = 0
	
	EdzUpNetwork_NetworkError = 0				'reset the error log
	If EdzUpNetwork_Hosting = True				'This computer is hosting
		'this application is the server
		
		'
		'	CHECK FOR NEW CONNECTIONS
		'
		TempClientWaiting = SocketAccept( LocalSocket, 1 )
		If TempClientWaiting
			Client:ClientType = New ClientType
			Client.UserName = DottedIP( SocketRemoteIP( TempClientWaiting ) ) +"["+String( GameTime() )+"]"
			Client.ID = EdzUpNetwork_NextAvailID()
			If Client.ID>EdzUpNetwork_MaximumID Then EdzUpNetwork_MaximumID = Client.ID

			Client.Authenticated = False
			Client.Syncronising = -1
			Client.PacketID = 0
			Client.Socket = TempClientWaiting
			Client.Stream = CreateSocketStream( Client.Socket )
			ListAddLast( ClientList, Client )
			EdzUpNetwork_PlayerCount :+ 1
			EdzUpNetwork_AddDisplay( Client.Username+" connected" )
			EdzUpNetwork_Debug( "Added client:"+Client.Username+" ID:"+Client.ID )
			EdzUpNetwork_Message( -1, EdzUpNetwork_LocalID, NETWORK_CLIENTID, IntToStr( Client.ID ), False, Client.Stream )
		EndIf
		
		'****************
		'*  PING CHECK  *
		'****************
		If CurrentTime()<>EdzUpNetwork_NetworkTime
			EdzUpNetwork_NetworkTime = CurrentTime()
			EdzUpNetwork_SecondsPassed :+ 1
			
			If EdzUpNetwork_SecondsPassed>60
				'Ping check every sixty seconds
				For Client:ClientType = EachIn ClientList
					If Client<>Null
						If Client.Authenticated = True
							EdzUpNetwork_Debug( "Pinging to "+Client.ID+" waiting for a response" )
							EdzUpNetwork_Message( EdzUpNetwork_LocalID, Client.ID, NETWORK_PING, "", False )
						EndIf
					EndIf
				Next
				EdzUpNetwork_SecondsPassed = 0
			EndIf
		EndIf
		
		'
		'	NOW CHECK ALL THE CLIENTS FOR PACKETS BEING SENT
		'
		For Client:ClientType = EachIn ClientList
			If Client<>Null
				EdzUpNetwork_Internal_Authenticate = False						'when set to true set the authenticated flag
				
				If EdzUpNetwork_ConnectionOk( Client.Socket )=True
					'perform client stuff here
					AvailableBytes = SocketReadAvail( Client.Socket )			'get available bytes on client socket
					If AvailableBytes>0
						For CB = 0 To AvailableBytes -1					'read in bytes and add them to clients buffer
							Client.Buffer = Client.Buffer + Chr( ReadByte( Client.Stream ) )
						Next
					EndIf
					'process a packet from the buffer
					Client.Buffer = EdzUpNetwork_ProcessBuffer( Client.Buffer )
							
					If EdzUpNetwork_Internal_Authenticate = True
						'they have passed the checks
						Client.Authenticated = True
					EndIf
				Else
					'weve lost the client, either they closed the application or the connection was dropped
					EdzUpNetwork_PlayerCount :- 1
					EdzUpNetwork_Debug( Client.Username+ "disconnected..." )
					EdzUpNetwork_KillReceipts( Client.ID )						'kill thier receipts
					EdzUpNetwork_AddDisplay( Client.Username+" left the game" )		'add a message and they broadcast the termination
					EdzUpNetwork_Message( EdzUpNetwork_LocalID, -1, NETWORK_DISCONNECT, IntToStr( Client.ID )+Client.Username, True )
					ListRemove( ClientList, Client )						'remove them from the list
				EndIf
			EndIf
		Next
		
		'END OF SERVER UPDATE SYSTEM
	Else
		'this application is a client
		If EdzUpNetwork_ConnectionOk( LocalSocket ) = True
			'READ IN NEW PACKETS AND ADD THEM TO THE BUFFER (LocalBuffer)
			AvailableBytes = SocketReadAvail( LocalSocket )
			If AvailableBytes>0
				For CB = 0 To AvailableBytes -1
					LocalBuffer = LocalBuffer + Chr( ReadByte( LocalStream ) )
				Next
			EndIf
			
			'process a packet from the buffer
			LocalBuffer = EdzUpNetwork_ProcessBuffer( LocalBuffer )
		Else
			'if the connection has been dropped then set it to Network Error
			EdzUpNetwork_NetworkError = 4
			
			Return 4
		EndIf
	EndIf
	
	'UPDATE ALL PACKETS AND RECEIPTS
	EdzUpNetwork_UpdatePackets()
	EdzUpNetwork_UpdateReceipts()
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_StreamInformation:String( Stream:TSocketStream, Operator:Long = 0 )
	For Client:ClientType = EachIn ClientList
		If Client<>Null
			If Client.Stream = Stream
				Select Operator
				Default	'Return ID
					Return Client.ID
				End Select
			EndIf
		EndIf
	Next
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_ProcessBuffer:String( DataBuffer:String )
	Local Buffer:String = DataBuffer
	Local ExtractedPacket:String = ""
	Local TempBuffer:String = ""

	Local TempFrom:Long = 0
	Local TempTo:Long = 0
	Local TempCount:Long = 0
	Local TempClass:Long = 0
	Local TempData:String = ""
	Local TempImportant:String = "F"

	EdzUpNetwork_Internal_UsingID = True

	If Instr( Buffer, EdzUpNetwork_PacketStart )>0
		If Instr( Buffer, EdzUpNetwork_PacketEnd )>0
			'trim the buffer to get rid of background noise in the buffer
			ExtractedPacket = Right( Buffer, Len( Buffer )-( Instr( Buffer, EdzUpNetwork_PacketStart ) -1 ) )
			Buffer = ExtractedPacket
			ExtractedPacket = ""
			
			'extract the packet from the buffer, Packetstart...PacketEnd and then process it
			TempBuffer = Left( Buffer, Instr( Buffer, EdzUpNetwork_PacketEnd ) +( Len( EdzUpNetwork_PacketEnd ) -1 ) )
			
			'remove the packet from the network buffer
			Buffer = Replace( Buffer, TempBuffer, "" )
			
			'remove the packet beginning and packet ending from the temporary buffer
			TempBuffer = Replace( TempBuffer, EdzUpNetwork_PacketStart, "" )
			TempBuffer = Replace( TempBuffer, EdzUpNetwork_PacketEnd, "" )
			
			'process the temporary buffer and store information
			TempFrom = StrToInt( Mid( TempBuffer, 1, 4 ) )				'Message From
			TempTo = StrToInt( Mid( TempBuffer, 5, 4 ) )					'Message To
			TempClass = StrToInt( Mid( TempBuffer, 9, 4 ) )				'Message Class
			TempCount = StrToInt( Mid( TempBuffer, 13, 4 ) )				'Message ID
			TempData = Mid( TempBuffer, 17, Len( TempBuffer ) -17 )		'Message Data
			TempImportant = Right( TempBuffer, 1 )						'Message Important
				
			If EdzUpNetwork_Hosting = True
				If TempFrom = 0
					'they dont have the ID they should be using
					EdzUpNetwork_Internal_UsingID = False
					
					'erase the clients buffer, ALL data is ignored
					Return ""
				EndIf
			EndIf
			
			'now we need to add the packet to the list for 'processing'
			EdzUpNetwork_AddPacket( TempFrom, TempTo, TempClass, TempCount, TempData, TempImportant )
			
			'return the buffer, with the packet removed
			Return Buffer
		Else
			'a beginning but no ending so erase the buffer
			Return ""
		EndIf
	Else
		'corrupt data in packet
		Return ""
	EndIf
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_Message( MessageFrom:Long, MessageTo:Long, MessageClass:Long, MessageData:String, Important:Int=False, Stream:TSocketStream = Null )
	'broadcast a message to everyone who is in the same galaxy & system as the client
	Local SendPacket:String = ""
	Local SendIt:Byte = False
	Local DontSendTo:Long = 0

	If MessageClass=NETWORK_DISCONNECT
		'There is no need to send a disconnect message to the person who disconnected
		DontSendTo = StrToInt( Left( MessageData, 4 ) )
	EndIf

	'create the packet
	SendPacket = "<"
	SendPacket :+ EdzUpNetwork_PacketStart								'build the packet data
	SendPacket :+ IntToStr( MessageFrom )					'add the Message from
	SendPacket :+ IntToStr( MessageTo )					'add the message to
	SendPacket :+ IntToStr( MessageClass )					'add the class
	SendPacket :+ IntToStr( EdzUpNetwork_MessageCount )			'add the Message ID
	SendPacket :+ MessageData
	If Important = True Then SendPacket :+ "T" Else SendPacket :+ "F"
	SendPacket :+ EdzUpNetwork_PacketEnd

	If Stream<>Null
		'send a specific message to someone (normally from server to client)
		WriteLine Stream, SendPacket

		'if important keep a receipt
		If Important = True Then EdzUpNetwork_AddReceipt( MessageFrom, MessageTo, MessageClass, EdzUpNetwork_MessageCount, MessageData )
		
		EdzUpNetwork_MessageCount :+ 1		'increment the message count
		Return	
	EndIf
	
	If EdzUpNetwork_Hosting = True
		'locate the client and send them the packet (-1 if the packet has to be broadcast)
		For Client:ClientType = EachIn ClientList
			If Client<>Null
				'REACHED
				If Client.Authenticated = True
					SendIt = False
					If MessageTo = -1 Then SendIt = True
					If Client.ID = MessageTo Then SendIt = True
					If DontSendTo<>0 And Client.ID<>DontSendTo Then SendIt = True		'dont send to sender
					If SendIt = True
						If Client.Authenticated = True
							WriteLine Client.Stream, SendPacket

							If Important = True
								'store a receipt per client for broadcasts
								EdzUpNetwork_AddReceipt( MessageFrom, Client.ID, MessageClass, EdzUpNetwork_MessageCount, MessageData )								
							EndIf
						Else
							EdzUpNetwork_Debug( Client.ID+" is not authenticated so cannot receive "+MessageData )
						EndIf
					EndIf
				Else
					'only authentication packets are sent
					If MessageClass = NETWORK_SERVERPASSWORD Or MessageClass = NETWORK_AUTHORISATION Or MessageClass = NETWORK_CLIENTID
						WriteLine Client.Stream, SendPacket
					EndIf
				EndIf
			EndIf
		Next
	Else
		'this will send the packet to the server
		WriteLine LocalStream, SendPacket
	EndIf
	
	If Important = True And MessageTo<>-1
		'if this is important then we need to keep a receipt, and its not a broadcast
		EdzUpNetwork_AddReceipt( MessageFrom, MessageTo, MessageClass, EdzUpNetwork_MessageCount, MessageData )
	EndIf
	EdzUpNetwork_MessageCount :+ 1		'increment the message count
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_StartupServer:Byte( Port:Int )
	'this will bind a socket and begin listening
	LocalSocket = CreateTCPSocket()
	If Not BindSocket( LocalSocket, Port ) Or Not SocketListen( LocalSocket )
		CloseSocket LocalSocket
		
		Return False
	EndIf
	
	EdzUpNetwork_Hosting = True
	EdzUpNetwork_LocalID = 0
	EdzUpNetwork_LocalPort = Port
	EdzUpNetwork_AccessLoginData()
	
	Return True
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_Connect:Byte( IP:Int, Port:Int )
	LocalSocket = CreateTCPSocket()
	If ConnectSocket( LocalSocket, IP, Port ) = False Then Return False
	If LocalSocket<>Null
		LocalStream = CreateSocketStream( LocalSocket )
		
		EdzUpNetwork_Connected = True
	Else
		CloseSocket( LocalSocket )			'no point leaving it open if it failed to connect
		Return False
	EndIf
	
	EdzUpNetwork_LocalPort = Port
	EdzUpNetwork_Hosting = False
	Return True
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_AlreadyExist:Byte( Username:String )
	For Client:ClientType = EachIn ClientList
		If Client<>Null
			If Upper( Client.Username ) = Upper( Username ) Then Return True
		EndIf
	Next
	
	Return False
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_NextAvailID:Long( )
	Local TempID:Long = 1
	Local IDInUse:Byte = False
	
	Repeat
		IDInUse = False
		For Client:ClientType = EachIn ClientList
			If Client<>Null
				If Client.ID = TempID
					TempID = TempID +1
					IDInUse = True
				EndIf
			EndIf
		Next
	Until IDInUse = False
	
	Return TempID
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_ConnectionOk:Byte( Socket:TSocket )
	'test socket to see if exception if thrown, if so then return false
	Local B:Byte = 5
	Local A:Byte Ptr = Varptr( B )

	Try
		Socket.Send( A, 1 )
		Socket.Recv( A, 1 )
	Catch o:Object
		Return False
	EndTry
	
	'try blitzmax's socket connected
	If SocketConnected( Socket ) = False Then Return False
	
	Return True
End Function

'------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_CheckUser:Byte( Username:String, Password:String )
	For User:UserType = EachIn UserList
		If User<>Null
			If Upper( User.Name ) = Upper( Username )
				If User.Password = Password
					Return 1
				Else
					Return -1
				EndIf
			EndIf
		EndIf
	Next

	User:UserType = New UserType
	User.Name = Username
	User.Password = Password
	ListAddLast( UserList, User )
	EdzUpNetwork_AccessLoginData( True )		'Save login stuffs with new user added
	
	Return 2
End Function

'------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_CheckIP:Byte( IP:String )
	'this will check for a specific ip and see if its in the ban list
	For Banned:BannedType = EachIn BannedList
		If Banned<>Null
			If Banned.IP = IP
				Return True
			EndIf
		EndIf
	Next

	Return False
End Function

'------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_AccessLoginData( Save:Byte = False )
	Local Handle:TStream = Null
	
	Select Save
	Case True			'save
		Handle = WriteFile( "UserData.txt" )
		If Handle<>Null
			For User:UserType = EachIn UserList
				If User<>Null
					WriteLine( Handle, User.Name )
					WriteLine( Handle, User.Password )
				EndIf
			Next
			CloseFile( Handle )
		EndIf
	Default			'Load
		Handle = ReadFile( "UserData.txt" )
		If Handle<>Null
			Repeat
				User:UserType = New UserType
				User.Name = ReadLine( Handle )
				User.Password = ReadLine( Handle )
				ListAddLast( UserList, User )
			Until Eof( Handle )
			CloseFile( Handle )
		EndIf
	End Select
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_AddPacket( MesFrom:Long, MesTo:Long, MesClass:Long, MesID:Long, MesData:String, MesImportant:String, Stream:TSocketStream =Null )
	'this function will add a packet to the list for processing, once it is processed it will be deleted
	Packet:PacketType = PacketType.Create()
	Packet.PacketFrom = MesFrom
	Packet.PacketTo = MesTo
	Packet.Class = MesClass
	Packet.ID = MesID
	Packet.Data = MesData
	If MesImportant = "T" Then Packet.Important = True Else	Packet.Important = False
	Packet.Stream = Stream
	
	ListAddLast( PacketList, Packet )
	EdzUpNetwork_AddDisplay( "Packet from "+String( MesFrom )+" To "+String( MesTo )+" containing:"+MesData )
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_UpdatePackets()
	'this function runs through the packets and sorts the ones that need to be sorted by the EdzUpNetwork System
	Local RemovePacket:Byte = False
	Local TemporaryData:String = ""
	Local TemporaryValue:Long = 0
	Local TemporaryUsername:String = ""
	Local TemporaryPassword:String = ""
	Local AlreadyAuthenticated:Byte = False

	Local PacketFrom:Long = 0
	Local PacketTo:Long = 0
	Local PacketID:Long = 0
	Local PacketData:String = 0
	Local PacketClass:Long = 0
	Local PacketImportant:Byte = 0
	Local PacketStream:TSocketStream = Null
	
	For Packet:PacketType = EachIn PacketList
		If Packet <> Null
			'store packet information
			PacketFrom = Packet.PacketFrom
			PacketTo = Packet.PacketTo
			PacketClass = Packet.Class
			PacketID = Packet.ID
			PacketData = Packet.Data
			PacketImportant = Packet.Important
			PacketStream = Packet.Stream
			
			Select Packet.Class
			Case NETWORK_AUTHORISATION		'AUTHORISATION PACKETS, Username+Password
				'remove packet as its already retrieved
				ListRemove( PacketList, Packet )
				'needs to get username, password
				If EdzUpNetwork_Hosting = True
					'the server needs to check password against username, and the server password if any required
					EdzUpNetwork_Debug( "Authentication Packet..." )
					'remove all receipts relating to clientID
					EdzUpNetwork_KillReceiptsClass( NETWORK_CLIENTID, PacketFrom )
					If Instr( PacketData, "¬" )<>0
						TemporaryUsername = Left( PacketData, Instr( PacketData, "¬", 1 ) -1 )
						TemporaryPassword = Right( PacketData, Len( PacketData ) - ( Instr( PacketData, "¬", 1 ) ) )
						
						EdzUpNetwork_Debug( "Authentication data:["+TemporaryUsername+"]["+TemporaryPassword+"]" )
						
						'check to see if they are already in
						For Client:ClientType = EachIn ClientList
							If Client<>Null
								If Client.Username = TemporaryUsername
									'tell them the Id that is already set
									EdzUpNetwork_Debug( "Entry located, already in" )
									EdzUpNetwork_Message( -1, EdzUpNetwork_LocalID, NETWORK_CLIENTID, IntToStr( Client.ID ), False, Client.Stream )
									AlreadyAuthenticated = True
								EndIf
							EndIf
						Next
						
						If AlreadyAuthenticated = False
							EdzUpNetwork_Debug( "New Authentication located, creating entry" )
							'see if thier details are stored on the host server
							If EdzUpNetwork_CheckUser( TemporaryUsername, TemporaryPassword ) >0
								EdzUpNetwork_Message( EdzUpNetwork_LocalID, PacketFrom, NETWORK_AUTHORISATION, "OK", False )
								EdzUpNetwork_KillReceipts( PacketFrom )	'set to true as we dont want to keep sending receipts
								EdzUpNetwork_Internal_Authenticate = True		'make it so system authenticates them
								EdzUpNetwork_AddDisplay( TemporaryUserName+" authenticated ok." )
								EdzUpNetwork_UpdateClient( PacketFrom, 0, TemporaryUsername )
								EdzUpNetwork_UpdateClient( PacketFrom, 1, "" )					'Authenticate them
								
								'broadcast that this player has joined the game
								EdzUpNetwork_Message( -1, EdzUpNetwork_LocalID, NETWORK_CLIENTINFORMATION, IntToStr( PacketFrom )+TemporaryUsername, True )
							Else
								EdzUpNetwork_Message( EdzUpNetwork_LocalID, PacketFrom, NETWORK_AUTHORISATION, "FAIL", False )
							EndIf
						EndIf
					EndIf
				Else
					'a authorisation message from the server needs to be processed
					If PacketData = "OK"
						EdzUpNetwork_Internal_UserAccepted = True
					Else
						EdzUpNetwork_Internal_UserAccepted = False
					EndIf
				EndIf
				
			Case NETWORK_SERVERPASSWORD		'see if they got the password correct
				ListRemove( PacketList, Packet )
				If EdzUpNetwork_Hosting = True
					'as this application is the server check the password against the server password
					If Packet.Data = EdzUpNetwork_ServerPassword
						'the server passwords match, Send OK to client so they can continue authentication
						EdzUpNetwork_Debug( "Server password:"+PacketData+" is the same as :"+EdzUpNetwork_ServerPassword )
						EdzUpNetwork_Message( EdzUpNetwork_LocalID, PacketFrom, NETWORK_SERVERPASSWORD, "OK", False )
					Else
						'it will send FAIL if the passwords dont match
						EdzUpNetwork_Debug( "Server password:"+PacketData+" isnt the same as :"+EdzUpNetwork_ServerPassword )
						EdzUpNetwork_Message( EdzUpNetwork_LocalID, PacketFrom, NETWORK_SERVERPASSWORD, "FAIL", False )
					EndIf
				Else
					If PacketData = "OK"
						EdzUpNetwork_Internal_ServerPasswordOk = True
					Else
						EdzUpNetwork_Internal_ServerPasswordOk = False
					EndIf
				EndIf
			
			Case NETWORK_PASSWORD			'someone is changing thier user password, update list
				ListRemove( PacketList, Packet )
				For Client:ClientType = EachIn ClientList
					If Client<>Null
						If Client.ID = PacketFrom
							'we got em
							TemporaryData = Client.Username
						EndIf
					EndIf
				Next
				
			Case NETWORK_PINGTIMEOUT		'Server message to client telling them they are out
				ListRemove( PacketList, Packet )
				If EdzUpNetwork_Hosting = False
					EdzUpNetwork_NetworkError = 1			'Set error system to Ping timeout
				EndIf
				
			Case NETWORK_PING				'Ping request from server to clients
				ListRemove( PacketList, Packet )
				If EdzUpNetwork_Hosting = True
					'server will need to update clients ping time
					For Client:ClientType = EachIn ClientList
						If Client<>Null
							If Client.ID = PacketFrom
								'we got em now reset the ping time
								DebugLog "Ping From "+Client.Username+", ping time reset"
								Client.Ping = 0
							EndIf
						EndIf
					Next
				Else
					'a ping request from the server
					EdzUpNetwork_Message( EdzUpNetwork_LocalID, 0, NETWORK_PING, "PONG", False )
				EndIf

				
			Case NETWORK_DISCONNECT		'Someone has quit, Server>>Clients
				If EdzUpNetwork_Hosting = False
					'remove the local client bits
					TemporaryValue = StrToInt( Left( PacketData, 4 ) )
					TemporaryUsername = Right( PacketData, Len( PacketData )-4 )
					EdzUpNetwork_KillReceipts( TemporaryValue )
					For Client:ClientType = EachIn ClientList
						If Client.ID = TemporaryValue
							EdzUpNetwork_PlayerCount :- 1
							ListRemove( ClientList, Client )
						EndIf
					Next
				EndIf
				ListRemove( PacketList, Packet )
				
			Case NETWORK_SERVERMESSAGE		'a server message to the client
				ListRemove( PacketList, Packet )
				EdzUpNetwork_AddDisplay( PacketData )
				If PacketFrom<>0 And PacketData = "CHEAT DETECTED"
					'Someone is cheating
					For Client:ClientType = EachIn ClientList
						If Client<>Null
							If Client.ID = PacketFrom
								Client.Cheating = True
								Client.TimesCheated :+ 1
								
								If EdzUpNetwork_Hosting=True
									EdzUpNetwork_AddDisplay( "***CHEAT DETECTED*** User:"+Client.Username+" Cheated "+Client.TimesCheated )
								EndIf
							EndIf
						EndIf
					Next
				EndIf
			
			Case NETWORK_MESSAGE			'a message from client to server
				ListRemove( PacketList, Packet )
				EdzUpNetwork_AddDisplay( PacketData )
								
			Case NETWORK_RECEIPT			'a receipt message telling someone that the message has been received
				'we will need to remove the receipt that ID is the same as Packet.Data as its just been acknowledged
				ListRemove( PacketList, Packet )
				EdzUpNetwork_RemoveReceipt( StrToInt( PacketData ) )
			
			Case NETWORK_CHAT				'A chat packet, To = -1 to broadcast
				ListRemove( PacketList, Packet )
				If EdzUpNetwork_Hosting = True
					'dont send it as important as its just chat, PacketTo=-1 then broadcast
					EdzUpNetwork_Debug( ">CHAT : "+PacketTo+" : "+PacketData )
					EdzUpNetwork_Message( EdzUpNetwork_LocalID, PacketTo, NETWORK_CHAT, PacketData, False )
				EndIf
				EdzUpNetwork_AddDisplay( PacketData )		'servers send the data, clients just add to display list
				
			Case NETWORK_CONNECT			'someone has connected, Server>>Clients, Data = UserID[4],Name Length[4], Username
				ListRemove( PacketList, Packet )
				Client:ClientType = New ClientType
				Client.ID = StrToInt( Mid( Client.Buffer, 1, 4 ) )			'convert the Client's ID
				TemporaryValue = StrToInt( Mid( Client.Buffer, 5, 4 ) )		'Get length of the Username
				Client.Username = Mid( Client.Buffer, 9, 9 +TemporaryValue )	'get the username
				
			Case NETWORK_CLIENTINFORMATION	'someone is requesting information on an ID
				ListRemove( PacketList, Packet )
				If EdzUpNetwork_Hosting = True
					TemporaryValue = StrToInt( PacketData )
					For Client:ClientType = EachIn ClientList
						If Client<>Null
							If Client.ID = TemporaryValue
								EdzUpNetwork_Message( 0, PacketFrom, NETWORK_CLIENTINFORMATION, IntToStr( Client.ID )+Client.Username, True )
							EndIf
						EndIf
					Next
				Else
					'update the client name with the data specified
					TemporaryValue = StrToInt( Left( Packet.Data, 4 ) )
					For Client:ClientType = EachIn ClientList
						If Client<>Null
							If Client.ID = TemporaryValue
								Client.Username = Right( PacketData, Len( PacketData ) -4 )
								DebugLog "Client updated: "+Client.ID+" changed name to "+Client.Username
							EndIf
						EndIf
					Next
				EndIf
				
			Case NETWORK_CLIENTID			'Server has assigned you client id ID
				ListRemove( PacketList, Packet )
				If EdzUpNetwork_Hosting = False
					If EdzUpNetwork_LocalID = 0
						EdzUpNetwork_LocalID = StrToInt( PacketData )
						DebugLog "LocalID set to "+EdzUpNetwork_LocalID
					EndIf
				Else
					'if server then send them the information they want
					EdzUpNetwork_Debug( "Client is requesting ID" )
					EdzUpNetwork_Message( -1, EdzUpNetwork_LocalID, NETWORK_CLIENTID, IntToStr( Long( EdzUpNetwork_StreamInformation( PacketStream ) ) ), False, PacketStream )
				EndIf
				
			Case NETWORK_FILETRANSFER		'A file transfer has been requested, -1 is start, -2 end, >-1 segment number
				ListRemove( PacketList, Packet )
				
			Default						'This is a packet that the EdzUpNetwork system needs to pass on
				If EdzUpNetwork_Hosting = True	'server needs to broadcast the packet to all clients
					ListRemove( PacketList, Packet )
					For Client:ClientType = EachIn ClientList
						If Client<>Null
							If Client.ID = PacketTo Or PacketTo <1		'send to a specific person or if its a broadcast
								'send the data packet to the client as required (-1 or 0 it gets broadcast to all if
								'its an unknown packet
								EdzUpNetwork_Message( 0, Client.ID, PacketClass, PacketData, PacketImportant )
							EndIf
						EndIf
					Next
				EndIf
			End Select
			
			If PacketImportant = True
				'respond to server for important packets
				DebugLog "Sending receipt packet marked important"
				EdzUpNetwork_Message( EdzUpNetwork_LocalID, PacketFrom, NETWORK_RECEIPT, IntToStr( PacketID ), False )
			EndIf
		EndIf
	Next
End Function

'------------------------------------------------------------------------------------------------------------------------
Function DottedIPToInt:Long( sIPAddress:String )'pass IP as a String e.g. "127.0.0.1"
	'; Author: Perturbatio (converted to BlitzMax(SuperStrict) by EdzUp)
	';VARS
	Local iIP:Long
	Local iDotPos:Int = 0
	Local iOldDotPos:Int = 0
	Local strTemp:String
	Local Counter:Int = 3

	'MAIN
	While Counter > 0 
		iOldDotPos = iDotPos
		iDotPos = Instr(sIPAddress, ".", iOldDotPos+1)
		strTemp = Mid$(sIPAddress,iOldDotPos + 1, (iDotPos - iOldDotPos)-1)
		iIP = iIP + (Int( strTemp ) Shl (Counter * 8))
		Counter = Counter - 1
	Wend

	strTemp = Right(sIPAddress, (Len(sIPAddress) - iDotPos) )
	iIP = iIP + (Int( strTemp ) Shl 0)	

	Return iIP 
End Function

'------------------------------------------------------------------------------------------------------------------------
Function IntToStr:String( Num:Int )
	Local Temp:String = ""
	Local BP:Int = 0

	PokeInt CompressBank, 0, Num 
	
	For BP = 0 To 3
		Temp = Temp + Chr$( PeekByte( CompressBank, BP ) )
	Next
	
	Return Temp
End Function 

'------------------------------------------------------------------------------------------------------------------------
Function FloatToStr:String( num:Float )
	'-=-=-=Convert a floating point number To a 4 Byte String
	Local st:String = ""
	Local i:Int = 0
	
	PokeFloat CompressBank, 0, num

	For i = 0 To 3 
		st$ = st$ + Chr$( PeekByte( CompressBank, i ) )
	Next 
	
	Return st$
End Function

'------------------------------------------------------------------------------------------------------------------------
Function StrToInt:Int( st:String )
	'-=-=-=Take a 4 Byte String And turn it back into a floating point #.
	Local i:Int =0 
	
	For i = 0 To 3
		PokeByte CompressBank, i, Asc( Mid$( st, i+1, 1 ) )
	Next
	
	Return PeekInt( CompressBank, 0 )
End Function

'------------------------------------------------------------------------------------------------------------------------
Function StrToFloat:Float( st:String )
	Local I:Int =0
	
	For i=0 To 3
		PokeByte CompressBank, i, Asc( Mid$( st, i+1, 1 ) )
	Next

	Return PeekFloat( CompressBank, 0 )
End Function

'------------------------------------------------------------------------------------------------------------------------
Function Encrypt:String( StringToCode:String )
	Local Value:Byte = 0
	Local Pos:Int = 0
	Local NewString:String = ""
	
	For Pos=1 To Len( StringToCode )
		Value = Asc( Mid$( StringToCode, Pos, 1 ) ) +Pos
		NewString = NewString + Chr$( Value )
	Next
	
	Return NewString
End Function

'------------------------------------------------------------------------------------------------------------------------
Function Decrypt:String( StringToUnencode:String )
	Local Value:Byte = 0
	Local Pos:Int = 0
	Local NewString:String = ""
	
	For Pos=1 To Len( StringToUnencode )
		Value = Asc( Mid$( StringToUnencode, Pos, 1 ) ) -Pos
		NewString = NewString + Chr$( Value )
	Next
	
	Return NewString
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_AddReceipt( MesFrom:Long, MesTo:Long, MesClass:Int, MesID:Long, MesData:String )
	'setup the receipt for book keeping
	Receipt:ReceiptType = ReceiptType.Create()
	Receipt.ReceiptFrom = MesFrom
	Receipt.ReceiptTo = MesTo
	Receipt.Class = MesClass
	Receipt.ID = MesID
	Receipt.Data = MesData
	
	Receipt.Timer = NetworkReceiptTimer
	Receipt.ReceiptCurrentTime = CurrentTime()
	
	ListAddLast( ReceiptList, Receipt )
	EdzUpNetwork_ReceiptCount :+ 1
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_KillReceipts( KillID:Long )
	'remove receipts for disconnecting people
	For Receipt:ReceiptType = EachIn ReceiptList
		If Receipt<>Null
			If Receipt.ReceiptTo = KillID Or Receipt.ReceiptFrom = KillID
				'kill all receipts to and from this ID as acknowledging them will slow network
				EdzUpNetwork_ReceiptCount :- 1
				ListRemove( ReceiptList, Receipt )
			EndIf
		EndIf
	Next
	
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_KillReceiptsClass( Class:Int, ID:Long )
	'this function will remove specific receipts from the list with the class and ID
	For Receipt:ReceiptType = EachIn ReceiptList
		If Receipt<>Null
			If Receipt.ReceiptFrom = ID And Receipt.Class = Class
				ListRemove( ReceiptList, Receipt )
			EndIf
		EndIf
	Next
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_RemoveReceipt( ReceiptID:Long )
	For Receipt:ReceiptType = EachIn ReceiptList
		If Receipt<>Null
			If Receipt.ID = ReceiptID
				ListRemove( ReceiptList, Receipt )
			EndIf
		EndIf
	Next
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_UpdateReceipts()
	Local ReceiptFrom:Long = 0
	Local ReceiptTo:Long = 0
	Local ReceiptClass:Long = 0
	Local ReceiptID:Long =0 
	Local ReceiptData:String = ""
	
	For Receipt:ReceiptType = EachIn ReceiptList
		If Receipt<>Null
			If CurrentTime()<>Receipt.ReceiptCurrentTime
				Receipt.Timer :- 1
				Receipt.ReceiptCurrentTime = CurrentTime()		'reset the timer check
				
				If Receipt.Timer <1
					ReceiptFrom = Receipt.ReceiptFrom
					ReceiptTo = Receipt.ReceiptTo
					ReceiptClass = Receipt.Class
					ReceiptID = Receipt.ID
					ReceiptData = Receipt.Data
					ListRemove( ReceiptList, Receipt )			'must be removed as the send message will create new receipt
							
					EdzUpNetwork_ReceiptCount :- 1
					EdzUpNetwork_Message( ReceiptFrom ,ReceiptTo, ReceiptClass, ReceiptData, True )
				EndIf
			EndIf
		EndIf
	Next
End Function

'--------------------------------------------------------------------------------------------------
Function EdzUpNetwork_AddDisplay( DataToAdd:String )
	Display:DisplayType = New DisplayType
	Display.Data = DataToAdd+Chr( 10 )
	ListAddLast( DisplayList, Display )
End Function

'--------------------------------------------------------------------------------------------------
Function EdzUpNetwork_DisplayNext:String( )
	Local TextToReturn:String = ""
	For Display:DisplayType = EachIn DisplayList
		If Display<>Null
			If TextToReturn = ""
				TextToReturn = Display.Data
				ListRemove( DisplayList, Display )
			EndIf
		EndIf
	Next
	
	Return TextToReturn
End Function

'--------------------------------------------------------------------------------------------------
Function EdzUpNetwork_DisplayWaiting:Long()
	'returns true if text is waiting for display
	For Display:DisplayType = EachIn DisplayList
		If Display<>Null
			Return True
		EndIf
	Next
	
	Return False
End Function

Function MD5:String( in:String )
	Local h0:Long = $67452301
	Local h1:Long = $EFCDAB89
	Local h2:Long = $98BADCFE
	Local h3:Long = $10325476
    
	Local r:Int[] = [7, 12, 17, 22,  7, 12, 17, 22,  7, 12, 17, 22,  7, 12, 17, 22,..
                5,  9, 14, 20,  5,  9, 14, 20,  5,  9, 14, 20,  5,  9, 14, 20,..
                4, 11, 16, 23,  4, 11, 16, 23,  4, 11, 16, 23,  4, 11, 16, 23,..
                6, 10, 15, 21,  6, 10, 15, 21,  6, 10, 15, 21,  6, 10, 15, 21]
                
	Local k:Int[] = [$D76AA478, $E8C7B756, $242070DB, $C1BDCEEE, $F57C0FAF, $4787C62A,..
                $A8304613, $FD469501, $698098D8, $8B44F7AF, $FFFF5BB1, $895CD7BE,..
                $6B901122, $FD987193, $A679438E, $49B40821, $F61E2562, $C040B340,..
                $265E5A51, $E9B6C7AA, $D62F105D, $02441453, $D8A1E681, $E7D3FBC8,..
                $21E1CDE6, $C33707D6, $F4D50D87, $455A14ED, $A9E3E905, $FCEFA3F8,..
                $676F02D9, $8D2A4C8A, $FFFA3942, $8771F681, $6D9D6122, $FDE5380C,..
                $A4BEEA44, $4BDECFA9, $F6BB4B60, $BEBFBC70, $289B7EC6, $EAA127FA,..
                $D4EF3085, $04881D05, $D9D4D039, $E6DB99E5, $1FA27CF8, $C4AC5665,..
                $F4292244, $432AFF97, $AB9423A7, $FC93A039, $655B59C3, $8F0CCC92,..
                $FFEFF47D, $85845DD1, $6FA87E4F, $FE2CE6E0, $A3014314, $4E0811A1,..
                $F7537E82, $BD3AF235, $2AD7D2BB, $EB86D391]
                
  Local intCount:Long = (((in$.length + 8) Shr 6) + 1) Shl 4
  Local data:Long[intCount]
  
  For Local ac:Long=0 Until in$.length
    data[ac Shr 2] = data[ac Shr 2] | ((in$[ac] & $FF) Shl ((ac & 3) Shl 3))
  Next
  data[in$.length Shr 2] = data[in$.length Shr 2] | ($80 Shl ((in$.length & 3) Shl 3)) 
  data[data.length - 2] = (Long(in$.length) * 8) & $FFFFFFFF
  data[data.length - 1] = (Long(in$.length) * 8) Shr 32
  
  For Local chunkStart:Long=0 Until intCount Step 16
    Local a:Long = h0
	Local b:Long = h1
	Local c:Long = h2
	Local d:Long = h3
	Local i:Long = 0
	Local f:Long = 0
	Local t:Long = 0
	        
    For i=0 To 15
      f = d ~ (b & (c ~ d))
      t = d
      
      d = c ; c = b
      b = Rol((a + f + k[i] + data[chunkStart + i]), r[i]) + b
      a = t
    Next
    
    For i=16 To 31
      f = c ~ (d & (b ~ c))
      t = d

      d = c ; c = b
      b = Rol((a + f + k[i] + data[chunkStart + (((5 * i) + 1) & 15)]), r[i]) + b
      a = t
    Next
    
    For i=32 To 47
      f = b ~ c ~ d
      t = d
      
      d = c ; c = b
      b = Rol((a + f + k[i] + data[chunkStart + (((3 * i) + 5) & 15)]), r[i]) + b
      a = t
    Next
    
    For i=48 To 63
      f = c ~ (b | ~d)
      t = d
      
      d = c ; c = b
      b = Rol((a + f + k[i] + data[chunkStart + ((7 * i) & 15)]), r[i]) + b
      a = t
    Next
    
    h0 :+ a ; h1 :+ b
    h2 :+ c ; h3 :+ d
  Next
  
  Return (LEHex(h0) + LEHex(h1) + LEHex(h2) + LEHex(h3)).ToLower()  
End Function

Function SHA1$(in$)
  Local h0:Long = $67452301
	Local h1:Long = $EFCDAB89
	Local h2:Long = $98BADCFE
	Local h3:Long = $10325476
	Local h4:Long = $C3D2E1F0
	Local a:Long = 0
	Local b:Long = 0
	Local c:Long = 0
	Local d:Long = 0
	Local e:Long = 0
	Local i:Long = 0
	Local f:Long = 0
	Local t:Long = 0
	  
  Local intCount:Long = (((in$.length + 8) Shr 6) + 1) Shl 4
  Local data:Long[intCount]
  
  For c=0 Until in$.length
    data[c Shr 2] = (data[c Shr 2] Shl 8) | (in$[c] & $FF)
  Next
  data[in$.length Shr 2] = ((data[in$.length Shr 2] Shl 8) | $80) Shl ((3 - (in$.length & 3)) Shl 3) 
  data[data.length - 2] = (Long(in$.length) * 8) Shr 32
  data[data.length - 1] = (Long(in$.length) * 8) & $FFFFFFFF
  
  For Local chunkStart:Long=0 Until intCount Step 16
    a = h0
	b = h1
	c = h2
	d = h3
	e = h4

    Local w:Long[] = data[chunkStart..chunkStart + 16]
    w = w[..80]
    
    For i=16 To 79
      w[i] = Rol(w[i - 3] ~ w[i - 8] ~ w[i - 14] ~ w[i - 16], 1)
    Next
    
    For i=0 To 19
      t = Rol(a, 5) + (d ~ (b & (c ~ d))) + e + $5A827999 + w[i]
      
      e = d ; d = c
      c = Rol(b, 30)
      b = a ; a = t
    Next
    
    For i=20 To 39
      t = Rol(a, 5) + (b ~ c ~ d) + e + $6ED9EBA1 + w[i]
      
      e = d ; d = c
      c = Rol(b, 30)
      b = a ; a = t
    Next
    
    For i = 40 To 59
      t = Rol(a, 5) + ((b & c) | (d & (b | c))) + e + $8F1BBCDC + w[i]
      
      e = d ; d = c
      c = Rol(b, 30)
      b = a ; a = t
    Next

    For i = 60 To 79
      t = Rol(a, 5) + (b ~ c ~ d) + e + $CA62C1D6 + w[i]
      
      e = d ; d = c
      c = Rol(b, 30)
      b = a ; a = t
    Next
    
    h0 :+ a ; h1 :+ b ; h2 :+ c
    h3 :+ d ; h4 :+ e
  Next
  
  Return (Hex(h0) + Hex(h1) + Hex(h2) + Hex(h3) + Hex(h4)).ToLower()  
End Function

Function SHA256$(in$)
  Local h0:Long = $6A09E667, h1:Long = $BB67AE85, h2:Long = $3C6EF372, h3:Long = $A54FF53A
  Local h4:Long = $510E527F, h5:Long = $9B05688C, h6:Long = $1F83D9AB, h7:Long = $5BE0CD19
  
  Local k:Int[] = [$428A2F98, $71374491, $B5C0FBCF, $E9B5DBA5, $3956C25B, $59F111F1,..
                $923F82A4, $AB1C5ED5, $D807AA98, $12835B01, $243185BE, $550C7DC3,..
                $72BE5D74, $80DEB1FE, $9BDC06A7, $C19BF174, $E49B69C1, $EFBE4786,..
                $0FC19DC6, $240CA1CC, $2DE92C6F, $4A7484AA, $5CB0A9DC, $76F988DA,..
                $983E5152, $A831C66D, $B00327C8, $BF597FC7, $C6E00BF3, $D5A79147,..
                $06CA6351, $14292967, $27B70A85, $2E1B2138, $4D2C6DFC, $53380D13,..
                $650A7354, $766A0ABB, $81C2C92E, $92722C85, $A2BFE8A1, $A81A664B,..
                $C24B8B70, $C76C51A3, $D192E819, $D6990624, $F40E3585, $106AA070,..
                $19A4C116, $1E376C08, $2748774C, $34B0BCB5, $391C0CB3, $4ED8AA4A,..
                $5B9CCA4F, $682E6FF3, $748F82EE, $78A5636F, $84C87814, $8CC70208,..
                $90BEFFFA, $A4506CEB, $BEF9A3F7, $C67178F2]

  Local intCount:Long = (((in$.length + 8) Shr 6) + 1) Shl 4
  Local data:Long[intCount]
  
	Local a:Long = 0
	Local b:Long = 0
	Local c:Long = 0
	Local d:Long = 0
	Local e:Long = 0
	Local f:Long = 0
	Local i:Long = 0
	Local t:Long = 0
	Local g:Long = 0
	Local h:Long = 0
	Local t0:Long = 0
	Local t1:Long=0
	
  For c=0 Until in$.length
    data[c Shr 2] = (data[c Shr 2] Shl 8) | (in$[c] & $FF)
  Next
  data[in$.length Shr 2] = ((data[in$.length Shr 2] Shl 8) | $80) Shl ((3 - (in$.length & 3)) Shl 3) 
  data[data.length - 2] = (Long(in$.length) * 8) Shr 32
  data[data.length - 1] = (Long(in$.length) * 8) & $FFFFFFFF
  
  For Local chunkStart:Long=0 Until intCount Step 16
	a = h0
	b = h1
	c = h2
	d = h3
	e = h4
	f = h5
	g = h6
	h = h7

    Local w:Long[] = data[chunkStart..chunkStart + 16]
    w = w[..64]
    
    For i=16 To 63
      w[i] = w[i - 16] + (Ror(w[i - 15], 7) ~ Ror(w[i - 15], 18) ~ (w[i - 15] Shr 3))..
            + w[i - 7] + (Ror(w[i - 2], 17) ~ Ror(w[i - 2], 19) ~ (w[i - 2] Shr 10))
    Next
    
    For i=0 To 63
      t0 = (Ror(a, 2) ~ Ror(a, 13) ~ Ror(a, 22)) + ((a & b) | (b & c) | (c & a))
      t1 = h + (Ror(e, 6) ~ Ror(e, 11) ~ Ror(e, 25)) + ((e & f) | (~e & g)) + k[i] + w[i]
      
      h = g ; g = f ; f = e ; e = d + t1
      d = c ; c = b ; b = a ;  a = t0 + t1  
    Next
    
    h0 :+ a ; h1 :+ b ; h2 :+ c ; h3 :+ d
    h4 :+ e ; h5 :+ f ; h6 :+ g ; h7 :+ h
  Next
  
  Return (Hex(h0) + Hex(h1) + Hex(h2) + Hex(h3) + Hex(h4) + Hex(h5) + Hex(h6) + Hex(h7)).ToLower()  
End Function

Function Rol:Long(val:Long, shift:Long)
  Return (val Shl shift) | (val Shr (32 - shift))
End Function

Function Ror:Long( val:Long, shift:Long )
  Return (val Shr shift) | (val Shl (32 - shift))
End Function

Function LEHex:String( val:Long )
  Local out:String = Hex( val )
  
  Return out[6..8] + out[4..6] + out[2..4] + out[0..2]
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_Debug( Message:String )
	DebugHandle = OpenFile( DebugFileName )
	If DebugHandle<>Null
		SeekStream( DebugHandle, FileSize( DebugFileName ) )
		WriteLine DebugHandle, Message
		CloseFile( DebugHandle )
	EndIf
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_StartDebug( FileName:String )
	Local Date:String = CurrentDate$()
	
	Date = Replace$( Date, " ", "" )
	Date = Replace$( Date, "Jan", "01" )
	Date = Replace$( Date, "Feb", "02" )
	Date = Replace$( Date, "Mar", "03" )
	Date = Replace$( Date, "Apr", "04" )
	Date = Replace$( Date, "May", "05" )
	Date = Replace$( Date, "Jun", "06" )
	Date = Replace$( Date, "Jul", "07" )
	Date = Replace$( Date, "Aug", "08" )
	Date = Replace$( Date, "Sep", "09" )
	Date = Replace$( Date, "Oct", "10" )
	Date = Replace$( Date, "Nov", "11" )
	Date = Replace$( Date, "Dec", "12" )
	
	Date = Date + "-"
	Date = Date + CurrentTime$()
	Date = Replace$( Date, ":", "" )
	
	DebugLog FileName+Date+Chr$(10)
	
	DebugHandle = WriteFile( Filename+Date+".txt" )
	DebugFileName = Filename+Date+".txt"
	If DebugHandle<>Null
		WriteLine( DebugHandle, "Network debug log" )
		WriteLine( DebugHandle, "Started : "+CurrentDate$() )
		WriteLine( DebugHandle, "          "+CurrentTime$() )
		WriteLine( DebugHandle, "---------------------------------------------------------------" )
		CloseFile( DebugHandle )
	EndIf
End Function

'------------------------------------------------------------------------------------------------------------------------
Function EdzUpNetwork_StopDebug()
	EdzUpNetwork_Debug( "---------------------------------------------------------------" )
End Function

' -----------------------------------------------------------------------------
' ResetGameTime MUST be called at the start of your game, or at least
' before you first try to use the GameTime function...
' -----------------------------------------------------------------------------

' You can also call this to reset GameTime on reaching a new level, starting
' a new game after the player dies, etc...
Function ResetGameTime:Int ()
	GT_Current	= 0
	GT_Start		= Long (MilliSecs ())
	GT_Last		= GT_Start
End Function

' -----------------------------------------------------------------------------
' Returns milliseconds from when ResetGameTime was called...
' -----------------------------------------------------------------------------
Function GameTime:Long ()
	Local msi:Int = MilliSecs ()

	GT_Current = GT_Current + (msi - GT_Last)
	GT_Last = msi

	Return GT_Current
End Function
