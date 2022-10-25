; ID: 2580
; Author: JoshK
; Date: 2009-09-10 00:20:58
; Title: Netwerks
; Description: Enet-based networking module

Rem
bbdoc: Leadwerks.Netwerks
EndRem
Module leadwerks.netwerks

SuperStrict

Import pub.enet
Import brl.map
Import brl.eventqueue
Import brl.socket
Import brl.bankstream
Import brl.linkedlist
Import "server.bmx"

Const EVENT_CONNECT:Int=ENET_EVENT_TYPE_CONNECT
Const EVENT_DISCONNECT:Int=ENET_EVENT_TYPE_DISCONNECT
Const EVENT_PACKETRECEIVE:Int=ENET_EVENT_TYPE_RECEIVE

Const PACKET_RELIABLE:Int=ENET_PACKET_FLAG_RELIABLE
Const PACKET_SEQUENCED:Int=2

Private
Const ENET_PACKET_FLAG_UNSEQUENCED:Int=2
Public

Rem
bbdoc:A host is a local network connection.
EndRem
Type THost
	
	Field ip:Int
	Field port:Int
	Field enethost:Byte Ptr
	Field peers:TList=New TList
	Field server:TServer
	Field serverupdatefrequency:Int=4*1000*60' 4 minutes (server removes after five)
	Field serverupdatetime:Int
	
	Method Delete()
		enet_host_destroy(enethost)
		peers.clear()
	EndMethod
	
	Rem
	bbdoc:Connects to a peer
	EndRem
	Method Connect:TPeer(ip:Int,port:Int)
		Const channels:Int=32
		Local addr:Byte Ptr
		Local peer:TPeer
		peer=New TPeer
		peer.ip=ip'HostIp(ip)
		peer.port=port
		addr=enet_address_create(peer.ip,port)
		peer.enetpeer=enet_host_connect(enethost,addr,channels)
		enet_address_destroy addr
		If peer.enetpeer=Null Return Null
		peer.link=peers.addlast(peer)
		Return peer
	EndMethod

	Method FindPeerByEnetPeer:TPeer(enetpeer:Byte Ptr)
		Local peer:TPeer
		For peer=EachIn peers
			If peer.enetpeer=enetpeer Return peer
		Next
		RuntimeError "Can't find peer."
	EndMethod
	
	Method FindPeerByIPAndPort:TPeer(ip:Int,port:Int,enetpeer:Byte Ptr)
		Local peer:TPeer
		For peer=EachIn peers
			If peer.ip=ip And peer.port=port
				peer.enetpeer=enetpeer
				Return peer
			EndIf
		Next
		peer=New TPeer
		peer.link=peers.addlast(peer)
		peer.ip=ip
		peer.port=port
		peer.enetpeer=enetpeer
		Return peer
	EndMethod

	Rem
	bbdoc:Disconnects from a peer
	EndRem
	Method Disconnect(peer:TPeer,force:Int=False)
		If force
			enet_peer_reset(peer.enetpeer)
			peer.link.remove()
		Else
			enet_peer_disconnect(peer.enetpeer)
		EndIf
		peer.connected=False
	EndMethod

	Rem
	bbdoc:Sends a packet to all connected peers.
	EndRem
	Method BroadcastPacket(packet:TPacket,channel:Int=0,flags:Int=PACKET_SEQUENCED)
		Local enetpacket:Byte Ptr
		Local enetflags:Int=0
		If (PACKET_RELIABLE & flags) enetflags:|ENET_PACKET_FLAG_RELIABLE
		If Not (PACKET_SEQUENCED & flags) enetflags:|ENET_PACKET_FLAG_UNSEQUENCED
		enetpacket=enet_packet_create(packet._bank.buf(),packet._bank.size(),flags)
		enet_host_broadcast(enethost,channel,enetpacket)
	EndMethod
	
	Rem
	bbdoc:Sends a packet to a peer.  If the send was successful, True is returned.
	EndRem
	Method SendPacket:Int(peer:TPeer,packet:TPacket,channel:Int=0,flags:Int=PACKET_SEQUENCED)
		Local enetpacket:Byte Ptr
		Local enetflags:Int=0
		If (PACKET_RELIABLE & flags) enetflags:|ENET_PACKET_FLAG_RELIABLE
		If Not (PACKET_SEQUENCED & flags) enetflags:|ENET_PACKET_FLAG_UNSEQUENCED
		enetpacket=enet_packet_create(packet._bank.buf(),packet._bank.size(),flags)
		Return enet_peer_send(peer.enetpeer,channel,enetpacket)=0
	EndMethod
	
	Rem
	bbdoc:Waits for a network event.
	EndRem
	Method WaitEvent:TEvent(timeout:Int=1000)
		If server
			If MilliSecs()-serverupdatetime>serverupdatefrequency
				server.refresh()
			EndIf
		EndIf
		Local ev:ENetEvent=New ENetEvent
		If enet_host_service(enethost,ev,timeout)
			Select ev.event
				
				Case ENET_EVENT_TYPE_CONNECT
					Local peer:TPeer
					Local ip:Int,port:Int
					ip=enet_peer_ip(ev.peer)
					port=enet_peer_port(ev.peer)
					peer=FindPeerByIPAndPort(ip,port,ev.peer)
					Return CreateEvent(EVENT_CONNECT,peer)
					
				Case ENET_EVENT_TYPE_DISCONNECT
					Local ip:Int,port:Int
					Local peer:TPeer
					peer=FindPeerByEnetPeer(ev.peer)
					peer.link.remove()
					Return CreateEvent(EVENT_DISCONNECT,peer)
				
				Case ENET_EVENT_TYPE_RECEIVE
					Local peer:TPeer
					Local packet:TPacket=New TPacket
					peer=FindPeerByEnetPeer(ev.peer)
					packet.writebytes(enet_packet_data(ev.packet),enet_packet_size(ev.packet))
					packet.seek(0)
					enet_packet_destroy(ev.packet)
					Return CreateEvent(EVENT_PACKETRECEIVE,peer,ev.channel,0,0,0,packet)
				
			EndSelect
		EndIf
	EndMethod
	
	Method Publish:Int(url:String,name:String="",game:String="")
		server=TServer.Create(url,name,game)
		serverupdatetime=MilliSecs()
		If server Return True Else Return False
	EndMethod
	
	Rem
	bbdoc:Creates a new host.
	EndRem
	Function Create:THost(ip:Int=0,port:Int=7777,players:Int=32,flags:Int=0)
		Local host:THost
		Local addr:Byte Ptr
		host=New THost
		If ip
			host.ip=ip'HostIp(ip)
		Else
			host.ip=ENET_HOST_ANY
		EndIf
		host.port=port
		If port<>0 Or host.ip<>ENET_HOST_ANY addr=enet_address_create(host.ip,port)
		host.enethost=enet_host_create(addr,players,0,0)
		If port enet_address_destroy addr
		If Not host.enethost Return Null		
		Return host
	EndFunction
	
EndType

Rem
bbdoc:A peer is a remote network connection.
EndRem
Type TPeer
	
	Field link:TLink
	Field ip:Int
	Field port:Int
	Field enetpeer:Byte Ptr
	Field userdata:Object
	Field connected:Int=True
	
	Rem
	Method Compare:Int(o:Object)
		Local peer:TPeer=TPeer(o)
		If peer.ip>ip Return 1
		If peer.ip<ip Return -1
		If peer.port>port Return 1
		If peer.port<port Return -1
		Return 0
	EndMethod
	EndRem
	
EndType


'Error in enet module
Private

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

Rem
bbdoc:Packets are chunks of data that hosts can be sent to and received from peers.  The TPacket class is an extension of the bankstream class, so packets can be written to and read from.
EndRem
Type TPacket Extends TBankStream
	
	Method New()
		_bank=New TBank
	EndMethod
	
EndType

Rem
bbdoc:Creates a new host.
EndRem
Function CreateHost:THost(ip:Int=0,port:Int=7777,players:Int=32)
	Return THost.Create(ip,port,players)
EndFunction

Rem
bbdoc:Connects a host to a peer.
EndRem
Function ConnectHost:TPeer(host:THost,ip:Int,port:Int)
	Return host.connect(ip,port)
EndFunction

Rem
bbdoc:Returns the ip of a host.
EndRem
Function GetHostIP:Int(host:THost)
	Return host.ip
EndFunction

Rem
bbdoc:Returns the port of a host.
EndRem
Function GetHostPort:Int(host:THost)
	Return host.port
EndFunction

Rem
bbdoc:Returns the ip address of a peer.
EndRem
Function GetPeerIP:Int(peer:TPeer)
	Return peer.ip
EndFunction

Rem
bbdoc:Returns the port of a peer.
EndRem
Function GetPeerPort:Int(peer:TPeer)
	Return peer.port
EndFunction

Rem
bbdoc:Disconnects from a peer.
EndRem
Function DisconnectHost(host:THost,peer:TPeer,force:Int=False)
	host.disconnect(peer,force)
EndFunction

Rem
bbdoc:Sends a packet to a peer.  If the send was successful, True is returned.
EndRem
Function SendPacket:Int(host:THost,peer:TPeer,packet:TPacket,channel:Int=0,flags:Int=PACKET_SEQUENCED)
	Return host.SendPacket(peer,packet,channel,flags)
EndFunction

Rem
bbdoc:Sends a packet to a peer.  If the send was successful, True is returned.
EndRem
Function BroadcastPacket(host:THost,packet:TPacket,channel:Int=0,flags:Int=PACKET_SEQUENCED)
	host.BroadcastPacket(packet,channel,flags)
EndFunction

Rem
bbdoc:Waits for an event.
EndRem
Function WaitNetwork:TEvent(host:THost,timeout:Int=1000)
	Return host.WaitEvent(timeout)
EndFunction

Rem
bbdoc:Creates a new packet.
EndRem
Function CreatePacket:TPacket()
	Return New TPacket
EndFunction

Rem
bbdoc:Publishes a host online
EndRem
Function PublishHost:Int(host:THost,url:String,name:String,game:String="")
	Return host.publish(url,name,game)
EndFunction
