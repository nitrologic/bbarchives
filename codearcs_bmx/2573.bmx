; ID: 2573
; Author: Otus
; Date: 2009-08-28 03:52:33
; Title: Simple connections
; Description: Simple socket stream connectivity

SuperStrict

Import BRL.SocketStream

Function AcceptSocketStream:TSocketStream(port:Int, timeout:Int)
	Local conn:TSocket = CreateTCPSocket()
	If Not conn.Bind(port)
		conn.Close
		Return Null
	End If
	conn.Listen(0)
	Local s:TSocket = conn.Accept(timeout)
	conn.Close
	If Not s Return Null
	Return CreateSocketStream(s, True)
End Function

Function RequestSocketStream:TSocketStream(ip:Int, port:Int)
	Local s:TSocket = CreateTCPSocket()
	If Not s.Connect(ip, port)
		s.Close
		Return Null
	End If
	Return CreateSocketStream(s, True)
End Function
