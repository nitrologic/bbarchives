; ID: 1555
; Author: CoderLaureate
; Date: 2005-12-08 10:07:44
; Title: TCP Socket Server
; Description: An event driven TCP Socket Server

Rem

	Title:   TCPSocket Server
	
	Author:  Jim Pishlo  (CoderLaureate)
	            (J9T@Jimmy9Toes.com)
	
End Rem

Strict

Type TCPSocketServer
	
	'Properties
	Field MyPort:Int = 3849
	Field MySocket:TSocket
	Field MyStream:TSocketStream
	Field Connections:TList = New TList
	Field StopSeq:String = "~r~n"
	
	'Callback handles
	Field NewConnectionCallback(Conn:TCPSocketConnection)
	Field MsgRcvdCallback(Conn:TCPSocketConnection)
	Field LostConnectionCallback(Conn:TCPSocketConnection)
	
	'Constructor (for lack of a better word)
	Function Create:TCPSocketServer(_Port:Int = 3849)
		Local s:TCPSocketServer = New TCPSocketServer
		s.MyPort = _Port
		s.MySocket = CreateTCPSocket()
		s.MyStream = CreateSocketStream(s.MySocket)
		s.MySocket.Bind(_Port)
		s.MySocket.Listen(0)
		Return s
	End Function
	
	'The main method.
	'Monitors connections and triggers events.
	Method Listen()
		Local newConn:TSocket = MySocket.Accept(0)
		If newConn <> Null Then
			Local conn:TCPSocketConnection = TCPSocketConnection.Create(Self,newConn)
			'Add the new connection to the collection
			Connections.AddLast(conn)
			'If a callback has been assigned call it.
			If NewConnectionCallback <> Null Then
				NewConnectionCallback(conn)	'Pass a refernce to the new connection
			End If
		End If
		
		'Clean up closed connections
		For Local c:TCPSocketConnection = EachIn Connections
			If Not c.MySocket.Connected() Then
				If LostConnectionCallback <> Null Then
					LostConnectionCallback(c)
				End If
				Connections.Remove(c)	'Remove connection from collection
			End If
		Next
		
		'Receive Data from connections
		'Trigger callback if neccesary
		For Local c:TCPSocketConnection = EachIn Connections
			If c.MySocket.Connected() Then
				Local t:String = c.Receive()
				If t <> "" Then
					If MsgRcvdCallback <> Null Then
						MsgRcvdCallback(c)
						c.Buffer = ""
					End If
				End If
			End If
		Next 
	End Method
	
	Method Broadcast(Message:String, Conn:TCPSocketConnection = Null)
		If Conn <> Null Then
			Conn.Send(Message)
		Else
			Local c:TCPSocketConnection
			For c = EachIn Connections
				If c.MySocket.Connected() Then
					c.Send(Message)
				End If
			Next
		End If	
	End Method
	
End Type

Type TCPSocketConnection
	Field MyID:String
	Field MyServer:TCPSocketServer
	Field MySocket:TSocket
	Field MyStream:TSocketStream
	Field Buffer:String = ""
	Field StopSeq:String
	
	Function Create:TCPSocketConnection(s:TCPSocketServer, NewSocket:TSocket)
		Local c:TCPSocketConnection = New TCPSocketConnection
		c.MyServer = s
		c.MySocket = NewSocket
		c.MyStream = CreateSocketStream(c.MySocket)
		c.StopSeq = c.MyServer.StopSeq
		c.MyID = DottedIP(c.MySocket.RemoteIP())
		Return c
	End Function

	Method Send(Text:String)
		MySTream.WriteString(Text)
	End Method

	Method Receive:String()
		Local nBytes:Int = MySocket.ReadAvail()
		Local s:String = StopSeq
		If nBytes Then
			Local in:String = ReadString(MyStream,nBytes)
			Buffer:+ in
			If Buffer.Length >= s.Length And Right$(Buffer,s.Length) = s Then
				Local t:String = Buffer.Replace(s,"") 'Strip out the stop sequence
				Return t
			End If
		End If
	End Method
	
End Type


Function CreateTCPSocketServer:TCPSocketServer(_Port:Int = 3849)
	Return TCPSocketServer.Create(_Port)
End Function





'Test Code:  This is a simple Telnet Chat Server.  Run this program, then
'            open up a command prompt and type:
'  
'				Telnet localhost 3849
'
'			 You can telnet into this server from anywhere in the world
'            and do *very basic* text chatting.  To log in from another
'            computer, type:
'
'				Telnet {host computer ip} 3849
'
'			 This is just a sample program to show you what you can do
'			 with the TCPSocketServer object.
'----------------------------------------------------------------------

'Create callback functions for interaction with the TCPSocketServer Object.

'Function to handle data recieved by server
'------------------------------------------
Function TextHandler(C:TCPSocketConnection)
	C.MyServer.BroadCast(C.MyID + ": " + C.Buffer)
End Function

'Greet new users and assign an ID
'--------------------------------
Function Greet(C:TCPSocketConnection)
	C.MyID = "User [" + MilliSecs() + "]"
	C.MyServer.BroadCast("Welcome!~r~n",C)
	C.MyServer.BroadCast(C.MyID + " has entered the room.~r~n")	
End Function

'Alert other users when a user leaves
'------------------------------------
Function LostConnection(C:TCPSocketConnection)
	C.MyServer.BroadCast("~r~n" + C.MyID + " has left the room.~r~n~r~n")
End Function

'Create an Instance of the TCPSocketServer Class
'-------------------------------------------------
Global Server:TCPSocketServer = CreateTCPSocketServer()

'Assign Function Pointers to Server's callback handles
'-----------------------------------------------------
Server.MsgRcvdCallback = TextHandler
Server.NewConnectionCallback = Greet
Server.LostConnectionCallback = LostConnection


'The Main Loop
'--------------
While Not KeyHit(KEY_ESCAPE)
	Server.Listen()		'That's it!
Wend
