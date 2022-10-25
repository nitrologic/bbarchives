; ID: 2444
; Author: JoshK
; Date: 2009-03-26 11:35:42
; Title: Proxy http stream factory
; Description: HTTP Stream Factory with proxy server

SuperStrict

Import brl.socketstream

Module leadwerks.proxystream

Private

New TProxyStreamFactory

Type TProxyStreamFactory Extends TStreamFactory

	Global ProxyServer:String
	Global Port:Int
	
	Method CreateStream:TStream( url:Object,proto$,path$,readable:Int,writeable:Int )
		If proto="http"
			If ProxyServer
				Local stream:TStream
				Local sock:TSocket=CreateTCPSocket()
				ConnectSocket(sock,HostIp(ProxyServer),Port)
				stream=CreateSocketStream(sock,True)
				WriteLine stream, "GET http://"+path+" HTTP/1.1"
				WriteLine stream, "User-Agent: MultiProx"
				WriteLine stream, "Connection: Close"
				WriteLine stream, ""
				While Not Eof( stream )
					If Not stream.ReadLine() Exit
				Wend
				Return stream
			EndIf
		EndIf
	EndMethod

EndType

Public

Rem
bbdoc:Sets the proxy server and port for http streams.
EndRem
Function SetProxyServer(server:String,port:Int)
	TProxyStreamFactory.ProxyServer=server
	TProxyStreamFactory.Port=port
EndFunction
