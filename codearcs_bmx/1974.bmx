; ID: 1974
; Author: rich41x
; Date: 2007-03-23 10:20:34
; Title: Simple Network Module
; Description: A simple network module similar to that of BlitzBasic

SuperStrict

Rem
bbdoc: Net
EndRem
Module rds.net

Import brl.stream
Import brl.socket
Import brl.socketstream

Rem
bbdoc: Server type
EndRem
Type TServer
	Field socket:TSocket

	Function Create:TServer(port:Int)
		Local server:TServer = New TServer
		server.socket = CreateTCPSocket()
		If Not BindSocket(server.socket, port) Or Not SocketListen(server.socket)
			CloseSocket(server.socket)
			Return Null
		EndIf
		Return server
	EndFunction
	
	Method Close()
		CloseSocket(socket)
	EndMethod

	Method Accept:TClient()
		Local accepted_socket:TSocket = SocketAccept(socket)
		If Not accepted_socket Return Null
		Local client:TClient = New TClient
		client.socket = accepted_socket
		Return client
	EndMethod
EndType

Rem
bbdoc: Client type
EndRem
Type TClient Extends TStream
	Field socket:TSocket
	
	Function Create:TClient(address:String, port:Int)
		Local client:TClient = New TClient
		client.socket = CreateTCPSocket()
		If Not ConnectSocket(client.socket, HostIp(address), port)
			CloseSocket(client.socket)
			Return Null
		EndIf
		Return client
	EndFunction
	
	Method Close()
		CloseSocket(socket)
	EndMethod
	
	Method Read:Int(buffer:Byte Ptr, count:Int)
		Return socket.Recv(buffer, count)
	EndMethod
	
	Method Write:Int(buffer:Byte Ptr, count:Int)
		Return socket.Send(buffer, count)
	EndMethod
	
	Method ReadAvail:Int()
		Return SocketReadAvail(socket)
	EndMethod
EndType

Rem
bbdoc: Create a new server
EndRem
Function CreateServer:TServer(port:Int)
	Return TServer.Create(port)
EndFunction

Rem
bbdoc: Close a server
EndRem
Function CloseServer(server:TServer)
	server.Close()
EndFunction

Rem
bbdoc: Accept a new client if available
EndRem
Function AcceptClient:TClient(server:TServer)
	Return server.Accept()
EndFunction

Rem
bbdoc: Create a new client
EndRem
Function CreateClient:TClient(address:String, port:Int)
	Return TClient.Create(address, port)
EndFunction

Rem
bbdoc: Close a client
EndRem
Function CloseClient(client:TClient)
	client.Close()
EndFunction

Rem
bbdoc: Check the number of bytes available to be read
EndRem
Function ReadAvail:Int(client:TClient)
	Return client.ReadAvail()
EndFunction
