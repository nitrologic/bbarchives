; ID: 2325
; Author: JoshK
; Date: 2008-10-02 19:20:20
; Title: UDP Networking Class
; Description: Easy OO UDP Networking

SuperStrict

Import brl.socket
Import brl.map

Type TNetNode
	
	Const LOCALIP:Int=2130706433
	
	Global map:TMap=New TMap
	
	Field socket:TSocket
	Field ip:Int
	Field port:Int
	
	Method SendNetMessage:Int(recipient:TNetNode,data:Byte Ptr=Null,size:Int=0)
		If ip<>LOCALIP Return False
		Return sendto_(socket._socket,data,size,0,recipient.ip,recipient.port)
	EndMethod
 	
	Method GetNetMessage:TNetMessage()
		If ip<>LOCALIP Return Null
		Local message:TNetMessage,size:Int,ip:Int,port:Int,buf:Byte[]
		size=socket.ReadAvail()
		If size
			message=New TNetMessage
			message.data=New Byte[size]
			recvfrom_(socket._socket,message.data,size,0,ip,port)
			message.sender=Create(ip,port)
			message.size=message.data.length
			Return message
		EndIf
	EndMethod
 	
	Method WaitNetMessage:TNetMessage(timeout:Int=0)
		Local time:Int
		Local message:TNetMessage
		time=MilliSecs()
		Repeat
			message=GetNetMessage()
			If message Return message
			If timeout
				If MilliSecs()-time>timeout Return Null
			EndIf
			Delay 1
		Forever
	EndMethod
	
	Function Create:TNetNode(ip:Int,port:Int=41000)
		Local netnode:TNetNode
		netnode=Find(ip,port)
		If netnode Return netnode
		netnode=New TNetNode
		netnode.ip=ip
		netnode.port=port
		If ip=LOCALIP
			netnode.socket=CreateUDPSocket()
			If Not BindSocket(netnode.socket,port) Return Null
		EndIf
		map.insert netnode,netnode
		Return netnode
	EndFunction
	
	Function Find:TNetNode(ip:Int,port:Int=41000)
		Local netnode:TNetNode
		netnode=New TNetNode
		netnode.ip=ip
		netnode.port=port
		netnode=TNetNode(map.valueforkey(netnode))
		Return netnode
	EndFunction
	
	Method Compare:Int(o:Object)
		Local netnode:TNetNode
		netnode=TNetNode(o)
		If netnode.ip>ip Return 1
		If netnode.ip<ip Return -1
		If netnode.port>port Return 1
		If netnode.port<port Return -1
		Return 0
	EndMethod
	
EndType

Type TNetMessage
	
	Field sender:TNetNode
	Field data:Byte[]
	Field size:Int
	
	Method ToString:String()
		Return String.fromCString(data)
	EndMethod
	
EndType

Function CreateNetNode:TNetNode(ip:Int=TNetNode.LOCALIP,port:Int=41000)
	Return TNetNode.Create(ip,port)
EndFunction

Function SendNetMessage:Int(sender:TNetNode,recipient:TNetNode,data:Byte Ptr,size:Int)
	Return sender.SendNetMessage(recipient,data,size)
EndFunction

Function GetNetMessage:TNetMessage(recipient:TNetNode)
	Return recipient.GetNetMessage()
EndFunction

Function WaitNetMessage:TNetMessage(recipient:TNetNode,timeout:Int=0)
	Return recipient.WaitNetMessage(timeout)
EndFunction
