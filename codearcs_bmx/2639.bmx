; ID: 2639
; Author: JoshK
; Date: 2010-01-11 15:15:07
; Title: Networking Library
; Description: An advanced client/server system

SuperStrict

Import pub.enet

Private

Const ENET_PACKET_FLAG_UNSEQUENCED:Int=2
Const SERVERUPDATEFREQUENCY:Int=4*1000*60

Function enet_host_port:Int( peer:Byte Ptr  )
	Local ip:Int=(Int Ptr peer)[1]
	Local port:Int=(Short Ptr peer)[4]
	?LittleEndian
	ip=(ip Shr 24) | (ip Shr 8 & $ff00) | (ip Shl 8 & $ff0000) | (ip Shl 24)
	?
	Return port
EndFunction

Function enet_host_ip:Int( peer:Byte Ptr  )
	Local ip:Int=(Int Ptr peer)[1]
	Local port:Int=(Short Ptr peer)[4]
	?LittleEndian
	ip=(ip Shr 24) | (ip Shr 8 & $ff00) | (ip Shl 8 & $ff0000) | (ip Shl 24)
	?
	Return ip
EndFunction

Function enet_peer_port:Int( peer:Byte Ptr  )
	Local ip:Int=(Int Ptr peer)[3]
	Local port:Int=(Short Ptr peer)[8]
	?LittleEndian
	ip=(ip Shr 24) | (ip Shr 8 & $ff00) | (ip Shl 8 & $ff0000) | (ip Shl 24)
	?
	Return port
EndFunction

Function enet_peer_ip:Int( peer:Byte Ptr  )
	Local ip:Int=(Int Ptr peer)[3]
	Local port:Int=(Short Ptr peer)[8]
	?LittleEndian
	ip=(ip Shr 24) | (ip Shr 8 & $ff00) | (ip Shl 8 & $ff0000) | (ip Shl 24)
	?
	Return ip
EndFunction

Public

Type TNetworkNode
	
	Field port:Int
	Field ip:Int
	Field enethost:Byte Ptr
	Field enetpeer:Byte Ptr
	
	Method Delete()
		Free()
	EndMethod
	
	Method Free()
		If enethost
			enet_host_destroy(enethost)
			enethost=Null
		EndIf
	EndMethod
	
	Method Update()
		Local ip:Int,port:Int,client:TClient,ev:ENetEvent=New ENetEvent,id:Byte,packet:TPacket
		
		If Not Self.enethost RuntimeError "Can't update a remote server."
		Repeat
			If enet_host_service(Self.enethost,ev,0)
				
				Select ev.event
				
				Case ENET_EVENT_TYPE_CONNECT
					id=NETWORK_CONNECT
				
				Case ENET_EVENT_TYPE_DISCONNECT
					id=NETWORK_DISCONNECT
				
				Case ENET_EVENT_TYPE_RECEIVE
					Local size:Int=enet_packet_size(ev.packet)
					Local data:Byte[size]
					MemCopy(Varptr id,enet_packet_data(ev.packet),1)
					If size>1
						packet=New TPacket
						packet._bank.resize(size-1)
						MemCopy(packet._bank.buf(),enet_packet_data(ev.packet)+1,size-1)
					EndIf
				
				Default
					Continue
					
				EndSelect
				
				EvaluateEvent(id,packet,ev.peer)
				
			Else
				Exit
			EndIf
			
			If enethost=Null Exit
			
		Forever

	EndMethod
	
	Method EvaluateEvent(id:Int,packet:TPacket,enetpeer:Byte Ptr) Abstract
	
EndType

'Public


Const NETWORK_CONNECT:Int=1
Const NETWORK_DISCONNECT:Int=2
Const NETWORK_PINGREQUEST:Int=3
Const NETWORK_PINGRESPONSE:Int=4
Const NETWORK_JOINREQUEST:Int=5
Const NETWORK_JOINRESPONSE:Int=6
Const NETWORK_CHAT:Int=7
Const NETWORK_LEAVEGAME:Int=8
Const NETWORK_CHANGENAMEREQUEST:Int=9
Const NETWORK_CHANGENAMERESPONSE:Int=10
Const NETWORK_PLAYERJOINED:Int=11
Const NETWORK_PLAYERLEFT:Int=12
Const NETWORK_LEAVEGAMEREQUEST:Int=13
Const NETWORK_LEAVEGAMERESPONSE:Int=14
Const NETWORK_PLAYERCHANGEDNAME:Int=15

Const SEND_RELIABLE:Int=ENET_PACKET_FLAG_RELIABLE
Const SEND_UNSEQUENCED:Int=ENET_PACKET_FLAG_UNSEQUENCED

Type TServer Extends TNetworkNode
	
	Field clients:TList=New TList
	Field callback(server:TServer,client:TClient,id:Int,packet:TPacket)
	Field bannedips:Int[]
	Field clientmap:TMap=New TMap
	Field url:String
	Field lastrefreshtime:Int
	
	Method Free()
		If url
			Remove()
			url=""
		EndIf
		Super.Free()
	EndMethod
	
	Function EncodeURL:String(t:String)
		Local newString:String
		Local i:Int,c:String,asciival:Int,hexstr:String,newhexstr:String
		For i = 0 To Len(t)
			c:String = t[i..i+1]
			asciival = Asc(c)
			If asciival > 32 And asciival < 123
				' handle replacing the special set of chars
				c = Replace(c,"'","%27")
				c = Replace(c,"%","%25")
				c = Replace(c,"<","%3C")
				c = Replace(c,">","%3E")
				c = Replace(c,"\","%5C")
				c = Replace(c,"^","%5E")
				c = Replace(c,"[","%5B")
				c = Replace(c,"]","%5D")
				c = Replace(c,"+","%2B")
				c = Replace(c,"$","%24")
				c = Replace(c,",","%2C")
				c = Replace(c,"@","%40")
				c = Replace(c,":","%3A")
				c = Replace(c,";","%3B")
				c = Replace(c,"/","%2F")
				c = Replace(c,"!","%21")
				c = Replace(c,"#","%23")
				c = Replace(c,"?","%3F")
				c = Replace(c,"=","%3D")
				c = Replace(c,"&","%26")
				newString:+c
			Else
				hexstr$ = Hex(asciival)
				newhexstr$ = "%" + hexstr[Len(hexstr)-2..Len(hexstr)]
				newstring:+newhexstr
			EndIf
		Next
		
		newstring = newstring[..Len(newstring)-3]
		Return newstring
	EndFunction
	
	Function Create:TServer(port:Int=0,portrange:Int=40)
		Local addr:Byte Ptr,server:TServer=New TServer

		server.ip=ENET_HOST_ANY
		If portrange<=1
			addr=enet_address_create(server.ip,server.port)
			server.enethost=enet_host_create(addr,32,0,0)
			enet_address_destroy(addr)
		Else
			If port=0 port=7777
			For Local n:Int=port To port+portrange-1
				addr=enet_address_create(ENET_HOST_ANY,n)
				server.enethost=enet_host_create(addr,64,0,0)
				enet_address_destroy(addr)
				If server.enethost
					server.port=n
					Exit
				EndIf
			Next
		EndIf
		If Not server.enethost Return Null		
		Return server
	EndFunction
	
	Method Publish:Int(servername:String="",gamename:String="",url:String="http::www.leadwerks.com/gameservers/gameservers.php")
		Local stream:TStream
		gamename=EncodeURL(gamename)
		servername=EncodeURL(servername)
		Self.url=url
		stream=OpenStream(url+"?action=addserver&gamename="+gamename+"&servername="+port+"|"+servername)
		If Not stream Return False
		Self.url=url
		While Not stream.Eof()
			Local s:String=stream.ReadLine()
			If s.Trim()
				stream.close()
				Return False
			EndIf
		Wend
		stream.close()
		lastrefreshtime=MilliSecs()
		Return True
	EndMethod
	
	Method Remove:Int()
		Local stream:TStream
		stream=OpenStream(url+"?action=removeserver")
		If stream
			stream.close()
			Return True
		Else
			Return False
		EndIf
	EndMethod
	
	Function Query:String[](gamename:String="",url:String="")
		Local stream:TStream,s:String,sarr:String[],n:Int
		
		If url="" url="http::www.leadwerks.com/gameservers/gameservers.php"
		stream=OpenStream(url+"?action=listservers&gamename="+gamename)
		If Not stream Return Null
		While Not stream.Eof()
			s=ReadLine(stream)
			If s.Trim().length>0
				sarr=sarr[..sarr.length+1]
				sarr[sarr.length-1]=s.Trim()
				Local sa:String[]=sarr[sarr.length-1].split("|")
				Local name:String=sa[0]
				sarr[sarr.length-1]=HostIp(name)+"|"+sarr[sarr.length-1]
			EndIf
		Wend
		stream.close()
		Return sarr
	EndFunction
	
	Method Refresh:Int()
		Local stream:TStream
		stream=OpenStream(url+"?action=refreshserver")
		If Not stream Return False
		stream.close()
		Return True	
	EndMethod
	
	Method FindClientByName:TClient(name:String)
		Return TClient(clientmap.valueforkey(name))
	EndMethod
	
	Method FindClientByPeer:TClient(peer:Byte Ptr)
		Local client:TClient
		For client=EachIn clients
			If client.enetpeer=peer Return client
		Next
	EndMethod
	
	Method FindClient:TClient(ip:Int,port:Int)
		Local client:TClient
		For client=EachIn clients
			If client.ip=ip And client.port=port Return client
		Next
	EndMethod
	
	Method Update()
		If url
			Local time:Int=MilliSecs()
			If time-lastrefreshtime>serverupdatefrequency
				lastrefreshtime=time
				Refresh()
			EndIf
		EndIf
		Super.Update()		
	EndMethod
	
	Method EvaluateEvent(id:Int,packet:TPacket,enetpeer:Byte Ptr)
		Local client:TClient=FindClient(enet_peer_ip(enetpeer),enet_peer_port(enetpeer))
		Select id
		
		Case NETWORK_PINGRESPONSE
			client.latency=MilliSecs()-packet.ReadInt()
		
		Case NETWORK_LEAVEGAMEREQUEST
			If client
				Send(client,NETWORK_LEAVEGAMERESPONSE,Null,SEND_RELIABLE)
				Local relay:TPacket=New TPacket
				relay.WriteLine(client.name)
				Broadcast(NETWORK_PLAYERLEFT,relay,SEND_RELIABLE)
				clients.remove(client)
				If clientmap.valueforkey(client.name)=client clientmap.remove(client.name)
			EndIf
			
		Case NETWORK_JOINREQUEST
			If client
				Disconnect(client,1)
				Local relay:TPacket=New TPacket
				relay.WriteLine(client.name)
				Broadcast(NETWORK_PLAYERLEFT,relay,SEND_RELIABLE)
			EndIf
			client=TClient.Find(enet_peer_ip(enetpeer),enet_peer_port(enetpeer))
			client.enetpeer=enetpeer
			
			If IPBanned(client.ip)
				Disconnect(client,0)
				Return
			Else
				client.name=packet.ReadLine()
				If Not FindClientByName(client.name)
					clientmap.insert(client.name,client)
					clients.AddLast(client)
					Local responsepacket:TPacket=New TPacket
					responsepacket.WriteByte(1)
					responsepacket.WriteByte(clients.count()-1)
					Local peer:TClient
					For peer=EachIn clients
						If peer<>client responsepacket.WriteLine(peer.name)
					Next
					Send(client,NETWORK_JOINRESPONSE,responsepacket,SEND_RELIABLE)
					id=NETWORK_PLAYERJOINED
					
					'Tell everyone he joined
					responsepacket=New TPacket
					responsepacket.WriteLine(client.name)
					Broadcast(NETWORK_PLAYERJOINED,responsepacket,SEND_RELIABLE)
					
				Else
					packet=New TPacket
					packet.WriteByte(0)'no, you can't joint
					packet.WriteByte(1)'reason: name already taken
					Send(client,NETWORK_JOINRESPONSE,packet,SEND_RELIABLE)
					'Disconnect(client,0)
					Return
				EndIf
			EndIf
		
		Case NETWORK_CHANGENAMEREQUEST
			Local name:String=packet.ReadLine()
			packet=New TPacket
			If client
				Local oldname:String=client.name
				If FindClientByName(name)<>Null And client.name<>name
					packet.WriteByte(0)
					send(client,NETWORK_CHANGENAMERESPONSE,packet,SEND_RELIABLE)
					Return
				Else
					packet.WriteByte(1)
					packet.WriteLine(name)
					send(client,NETWORK_CHANGENAMERESPONSE,packet,SEND_RELIABLE)
					clientmap.remove(client.name)
					client.name=name
					clientmap.insert(name,client)
					If oldname<>name
						packet=New TPacket
						packet.WriteLine(oldname)
						packet.WriteLine(name)
						Broadcast(NETWORK_PLAYERCHANGEDNAME,packet,SEND_RELIABLE)
					EndIf
				EndIf
			Else
				If FindClientByName(name)=Null
					packet.WriteByte(1)
					packet.WriteLine(name)
					send(client,NETWORK_CHANGENAMERESPONSE,packet,SEND_RELIABLE)
					Return
				EndIf
			EndIf
			Return
								
		Case NETWORK_CONNECT
			Return
		
		Case NETWORK_DISCONNECT
			If client
				clients.remove(client)
				If clientmap.valueforkey(client.name)=client clientmap.remove(client.name)
				If Not client.enethost client.free()
			EndIf
			
		Case NETWORK_PINGREQUEST
			If Not client
				client=New TClient
				client.enetpeer=enetpeer
			EndIf
			Send(client,NETWORK_PINGRESPONSE,packet)
			Return
		
		Case NETWORK_CHAT
			Local relay:TPacket=New TPacket
			Local count:Int=packet.ReadByte()
			relay.WriteLine(client.name)
			relay.WriteLine(packet.ReadLine())
			If count=0
				Broadcast(NETWORK_CHAT,relay,SEND_RELIABLE)
			Else
				For Local n:Int=1 To count
					client=FindClientByName(packet.ReadLine())
					If client
						Send(client,NETWORK_CHAT,relay,SEND_RELIABLE)
					EndIf
				Next
			EndIf
			Return
		
		EndSelect
		
		If callback
			If packet packet.seek(0)
			callback(Self,client,id,packet)
		EndIf
	EndMethod
	
	Method Send:Int(client:TClient,id:Int,packet:TPacket=Null,flags:Int=0,channel:Int=0)
		Local enetpacket:Byte Ptr
		Local result:Int
		
		If Not client.enetpeer RuntimeError "Can't send to local client."
		
		Local data:Byte[]
		If packet
			If packet._bank.size()=0 packet=Null
		EndIf
		If packet
			data=New Byte[packet._bank.size()+1]
			MemCopy(Varptr data[1],packet._bank.buf(),packet._bank.size())
		Else
			data=New Byte[1]
		EndIf
		data[0]=id
		enetpacket=enet_packet_create(data,data.length,flags)
		result=(enet_peer_send(client.enetpeer,channel,enetpacket)=0)		
		Return result
	EndMethod
	
	Method Broadcast:Int(id:Int,packet:TPacket=Null,flags:Int=0,channel:Int=0)
		Local result:Int=1
		For Local client:TClient=EachIn clients
			If Not Send(client,id,packet,flags,channel) result=0
		Next
		Return result
		Rem
		Local enetpacket:Byte Ptr
		Local result:Int
		
		Local data:Byte[]
		If packet
			If packet._bank.size()=0 packet=Null
		EndIf
		If packet
			data=New Byte[packet._bank.size()+1]
			MemCopy(Varptr data[1],packet._bank.buf(),packet._bank.size())
		Else
			data=New Byte[1]
		EndIf
		data[0]=id
		enetpacket=enet_packet_create(data,data.length,flags)
		enet_host_broadcast(Self.enethost,channel,enetpacket)
		EndRem
	EndMethod
	
	Method Disconnect(client:TClient,force:Int=False)
		If client.enetpeer
			If force
				enet_peer_reset(client.enetpeer)
			Else
				enet_peer_disconnect(client.enetpeer)
			EndIf
			clients.remove(client)
			If Not client.enethost
				client.link.remove()
			EndIf
			If clientmap.valueforkey(client.name)=client
				clientmap.remove(client.name)
			EndIf
			client.enetpeer=Null
		EndIf
	EndMethod
	
	Method BanIP(ip:Int)
		bannedips=bannedips[..bannedips.length+1]
		bannedips[bannedips.length-1]=ip
	EndMethod
	
	Method IPBanned:Int(ip:Int)
		For Local n:Int=0 To bannedips.length-1
			If ip=bannedips[n] Return True
		Next
		Return False
	EndMethod
	
	Method Kick(client:TClient)
		BanIP(client.ip)
		Disconnect(client)
	EndMethod
	
EndType

Type TClient Extends TNetworkNode
	
	Const maxplayers:Int=64
	
	Global list:TList=New TList
	
	Field name:String
	Field link:TLink
	Field server:TServer
	Field connected:Int
	Field joined:Int=0
	Field callback(client:TClient,id:Int,packet:TPacket)
	Field userdata:Object
	Field channels:Int=16
	Field latency:Int
	
	Method Free()
		If link
			link.remove()
			link=Null
		EndIf
		Super.Free()
	EndMethod
	
	Function Find:TClient(ip:Int,port:Int)
		Local client:TClient
		For client=EachIn list
			If client.ip=ip And client.port=port Return client
		Next
		client=New TClient
		client.ip=ip
		client.port=port
		client.link=list.addlast(client)
		Return client
	EndFunction
	
	Function Create:TClient(port:Int=0,portrange:Int=40)
		Const maxpeers:Int=32
		Local client:TClient=New TClient
		Local addr:Byte Ptr
		
		If portrange<=1
			client.ip=ENET_HOST_ANY
			client.port=port
			addr=enet_address_create(client.ip,client.port)
			client.enethost=enet_host_create(addr,maxpeers,0,0)
			enet_address_destroy(addr)
			If Not client.enethost Return Null
			client.link=list.addlast(client)
		Else
			If port=0 port=7776
			For Local n:Int=port To port+portrange-1
				addr=enet_address_create(ENET_HOST_ANY,n)
				client.enethost=enet_host_create(addr,maxpeers,0,0)
				enet_address_destroy(addr)
				If client.enethost
					client.port=port
					Exit
				EndIf
			Next
		EndIf
		
		Return client
	EndFunction
	
	Method Disconnect(force:Int=False)
		Const disconnecttimeout:Int=10000
		
		If Not Self.enethost RuntimeError "Can't update a remote server."
		If server
			If force
				enet_peer_reset(server.enetpeer)
			Else
				Send(NETWORK_LEAVEGAMEREQUEST,Null,SEND_RELIABLE)
				Local ev:ENetEvent=New ENetEvent
				Local start:Int=MilliSecs()
				Repeat
					If MilliSecs()-start>disconnecttimeout Exit
					If enet_host_service(Self.enethost,ev,100)
						Select ev.event
						Case ENET_EVENT_TYPE_RECEIVE
							If ev.packet
								Local id:Int,packet:TPacket
								Local size:Int=enet_packet_size(ev.packet)
								Local data:Byte[size]
								MemCopy(Varptr id,enet_packet_data(ev.packet),1)
								If size>1
									packet=New TPacket
									packet._bank.resize(size-1)
									MemCopy(packet._bank.buf(),enet_packet_data(ev.packet)+1,size-1)
								EndIf
								Select id
								Case NETWORK_LEAVEGAMERESPONSE
									Exit
								EndSelect
							EndIf
						EndSelect
					Else
						Exit
					EndIf
				Forever
				enet_peer_disconnect(server.enetpeer)
			EndIf
			server=Null
		EndIf
		joined=False
	EndMethod
	
	Method SetName(name:String)
		If name.length>16 name=name[..16]
		If server
			Local packet:TPacket=New TPacket
			packet.WriteLine(name)
			Send(NETWORK_CHANGENAMEREQUEST,packet,SEND_RELIABLE)
		Else
			Self.name=name
		EndIf
	EndMethod
	
	Method Connect:Int(ip:Int,port:Int)
		Local addr:Byte Ptr
				
		If ip=0 ip=2130706433
		If Not Self.enethost RuntimeError "Remote client cannot connect to server."
		If server Disconnect()		
		server=New TServer
		server.ip=ip
		server.port=port
		addr=enet_address_create(server.ip,server.port)
		server.enetpeer=enet_host_connect(enethost,addr,channels)
		enet_address_destroy(addr)
		If server.enetpeer=Null
			server=Null
			Return 0
		EndIf
		
		Rem
		Local _callback:Byte Ptr=Null
		_callback=callback		
		Local start:Int=MilliSecs()
		Repeat
			Update()
			If MilliSecs()-start>10000 Or joined=1 Exit
		Forever
		callback=_callback
		EndRem
		
		Return 1
	EndMethod
	
	Method Send:Int(id:Int,packet:TPacket=Null,flags:Int=0,channel:Int=0)
		Local enetpacket:Byte Ptr
		Local result:Int
		
		If Not connected Return 0
		
		If Not server RuntimeError "Client is not connected."
		Local data:Byte[]
		If packet
			If packet._bank.size()=0 packet=Null
		EndIf
		If packet
			data=New Byte[packet._bank.size()+1]
			MemCopy(Varptr data[1],packet._bank.buf(),packet._bank.size())
		Else
			data=New Byte[1]
		EndIf
		data[0]=id
		enetpacket=enet_packet_create(data,data.length,flags)
		result=(enet_peer_send(server.enetpeer,channel,enetpacket)=0)		
		Return result
	EndMethod
	
	Method EvaluateEvent(id:Int,packet:TPacket,enetpeer:Byte Ptr)
		Select id
		Case NETWORK_CONNECT
			Self.connected=True
			packet=New TPacket
			packet.WriteLine(name)
			Send(NETWORK_JOINREQUEST,packet)			
		Case NETWORK_JOINRESPONSE
			joined=packet.ReadByte()
			If joined=0 Disconnect()
		Case NETWORK_CHANGENAMERESPONSE
			If packet.ReadByte()=1
				name=packet.ReadLine()
			EndIf
		Case NETWORK_PINGREQUEST
			Send(NETWORK_PINGRESPONSE,packet)
			Return
		Case NETWORK_PINGRESPONSE
			latency=MilliSecs()-packet.ReadInt()
			packet.WriteInt(enet_peer_ip(enetpeer))
			packet.WriteInt(enet_peer_port(enetpeer))
			If enetpeer=pingpeer
				enet_peer_disconnect(pingpeer)
				pingpeer=Null
			EndIf
		EndSelect
		If callback
			If packet packet.seek(0)
			callback(Self,id,packet)
		EndIf
	EndMethod
	
	Method Join()
		If Not joined
			Local packet:TPacket=New TPacket
			packet.WriteLine(name)
			Send(NETWORK_JOINREQUEST,packet)
		EndIf
	EndMethod
	
	Function ConvertEvent:Int(ev:EnetEvent,packet:TPacket)
		Select ev.event
		Case ENET_EVENT_TYPE_CONNECT
			Return NETWORK_CONNECT
		Case ENET_EVENT_TYPE_DISCONNECT
			Return NETWORK_DISCONNECT
		Case ENET_EVENT_TYPE_RECEIVE
			If ev.packet
				Local id:Int
				Local size:Int=enet_packet_size(ev.packet)
				Local data:Byte[size]
				MemCopy(Varptr id,enet_packet_data(ev.packet),1)
				If size>1
					packet._bank.resize(size-1)
					MemCopy(packet._bank.buf(),enet_packet_data(ev.packet)+1,size-1)
				EndIf
				enet_packet_destroy(ev.packet)
				Return id
			EndIf
		EndSelect
	EndFunction
	
	Field pingpeer:Byte Ptr
	
	Method Ping:Int(ip:Int=0,port:Int=0)
		Const disconnecttimeout:Int=1000
		Local packet:TPacket=New TPacket,addr:Byte Ptr,enetpacket:Byte Ptr,ev:EnetEvent,result:Int,id:Int,start:Int
		
		If server
			If server.ip=ip And server.port=port ip=0
		EndIf
		
		If Not enethost RuntimeError("Can't ping remote client.")
		If ip
			If pingpeer
				enet_peer_disconnect(pingpeer)
				pingpeer=Null
			EndIf
			
			ev=New EnetEvent
			addr=enet_address_create(ip,port)
			pingpeer=enet_host_connect(enethost,addr,1)
			enet_address_destroy(addr)
			If pingpeer
				start=MilliSecs()
				Repeat
					If MilliSecs()-start>disconnecttimeout
						enet_peer_disconnect(pingpeer)
						pingpeer=Null
						'Print "Connect timeout"
						Return 0
					EndIf
					If enet_host_service(enethost,ev,0)
						id=ConvertEvent(ev,packet)
						Select id
						Case NETWORK_CONNECT
							Exit
						EndSelect
					EndIf
				Forever
				Local data:Byte[5]
				data[0]=NETWORK_PINGREQUEST
				start=MilliSecs()
				MemCopy(Varptr data[1],Varptr start,4)
				enetpacket=enet_packet_create(data,data.length,0)
				If enet_peer_send(pingpeer,0,enetpacket)=0
					Return 1
				Else
					enet_peer_disconnect(pingpeer)
					pingpeer=Null
					'Print "can't send packet"
					Return 0
				EndIf				
			Else
				'Print "Cant connect to peer at "+ip+", "+port
				Return 0
			EndIf
		Else
			packet:TPacket=New TPacket
			packet.WriteInt(MilliSecs())
			Return Send(NETWORK_PINGREQUEST,packet)
		EndIf
	EndMethod
	
	Method Say:Int(text:String,recipients:String[]=Null)
		Local packet:TPacket=New TPacket
		If recipients
			packet.WriteByte(recipients.length)
			packet.WriteLine(text)
			For Local n:Int=0 To recipients.length-1
				packet.WriteLine(recipients[n])
			Next
		Else
			packet.WriteByte(0)
			packet.WriteLine(text)
		EndIf
		Return Send(NETWORK_CHAT,packet)		
	EndMethod
	
EndType


Type TPacket Extends TBankStream
	
	Method New()
		_bank=New TBank
	EndMethod
	
EndType


Function CreatePacket:TPacket()
	Local packet:TPacket=New TPacket
	Return packet
EndFunction
