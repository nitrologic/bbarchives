; ID: 2466
; Author: JoshK
; Date: 2009-04-27 15:43:25
; Title: HTTPStream factory+
; Description: HTTPStream factory with optional proxy server and authentication

SuperStrict

Import brl.FileSystem
Import brl.socketstream
Import bah.base64
Import brl.httpstream

Private

New THTTPStreamFactoryEx

Type THTTPStreamFactoryEx Extends TStreamFactory
	
	Global proxyserver:String
	Global port:Int=80
	Global username:String
	Global password:String   
	
	Method CreateStream:TStream( url:Object,proto$,path$,readable:Int,writeable:Int )
		If proto="http"
			Local stream:TStream
			Local i:Int=path.Find( "/",0 ),server$,file$
			If i<>-1
				server=path[..i]
				file=path[i..]
			Else
				server=path
				file=""
			EndIf
			
			If proxyserver
				Local sock:TSocket=CreateTCPSocket()
				ConnectSocket(sock,HostIp(proxyserver),port)
				stream=CreateSocketStream(sock,True)
			Else
				stream=TSocketStream.CreateClient(server,port)
			EndIf
			
			If Not stream Return Null
			
			stream.WriteLine "GET http://"+path+" HTTP/1.0"
			stream.WriteLine "Host: "+server

			If username<>"" And password<>""
				Local auth:String = username+":"+password
				Local c:Byte Ptr=auth.ToCString()
				auth=TBase64.Encode(c,auth.length)
				MemFree(c)
				stream.WriteLine "Authorization: Basic "+auth
			EndIf
			
			If proxyserver
				WriteLine stream, "User-Agent: MultiProx"
			EndIf
			WriteLine stream, "Connection: Close"
			WriteLine stream, ""
			
			While Not Eof( stream )
				If Not stream.ReadLine() Exit
			Wend
			
			Return stream
		EndIf
	EndMethod
	
EndType


Public

Rem
bbdoc:Sets the proxy server for http streams.
EndRem
Function SetHTTPProxy(server:String)
   THTTPStreamFactoryEx.proxyserver=server
EndFunction

Rem
bbdoc:Sets the port for http streams.
EndRem
Function SetHTTPPort(port:Int)
   THTTPStreamFactoryEx.port=port
EndFunction

Rem
bbdoc:Sets the authentication info.
EndRem
Function SetHTTPAuthentication(username:String,password:String)
   THTTPStreamFactoryEx.username=username
   THTTPStreamFactoryEx.password=password
EndFunction
