; ID: 2732
; Author: Plash
; Date: 2010-06-21 21:24:43
; Title: Message-based networking
; Description: Simple example for message-based networking

' common.bmx

Const MSGID_PING:Int = 10
Const MSGID_PONG:Int = 11

Type TBaseClient Extends TStream Abstract
	
	Field m_socket:TSocket, m_sip:Int
	
	Method Init(socket:TSocket)
		m_socket = socket
		If m_socket
			m_sip = m_socket.RemoteIP()
		End If
	End Method
	
	Method Read:Int(buf:Byte Ptr, count:Int)
		Return m_socket.Recv(buf, count)
	End Method
	
	Method Write:Int(buf:Byte Ptr, count:Int)
		Return m_socket.Send(buf, count)
	End Method
	
	Method ReadAvail:Int()
		Return m_socket.ReadAvail()
	End Method
	
	Method Eof:Int()
		If m_socket
			If m_socket.Connected() = True
				Return False
			End If
		End If
		Close()
		Return True
	End Method
	
	Method Close()
		If m_socket
			m_socket.Close()
			m_socket = Null
		End If
	End Method
	
	Method Connect:Int(remoteip:Int, remoteport:Int)
		m_sip = remoteip
		Return m_socket.Connect(remoteip, remoteport)
	End Method
	
	Method Connected:Int()
		If m_socket
			Return m_socket.Connected()
		End If
		Return False
	End Method
	
	Method Update()
		If Not Eof()
			If ReadAvail() > 0
				HandleMessage(ReadInt())
			End If
		End If
	End Method
	
	Method GetIPAddressAsInt:Int()
		Return m_sip
	End Method
	
	Method GetIPAddressAsString:String(separator:String = ".")
		Return (m_sip Shr 24) + separator + (m_sip Shr 16 & 255) + separator + (m_sip Shr 8 & 255) + separator + (m_sip & 255)
	End Method
	
	Method HandleMessage(id:Int) Abstract
	
End Type
