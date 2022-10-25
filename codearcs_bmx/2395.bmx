; ID: 2395
; Author: Otus
; Date: 2009-01-18 17:04:38
; Title: Improved HTTP Streams
; Description: Support for GET, HEAD and POST methods with authentication

SuperStrict

Rem
bbdoc: HTTP Streams
End Rem
Module Otus.HttpStream

ModuleInfo "Version: 1.00"
ModuleInfo "Author: Jan Varho"
ModuleInfo "License: Public domain"

Import BRL.SocketStream

Import BaH.Base64

Rem
bbdoc: Wrapper stream for HTTP requests
about:
The following HTTP methods are supported: GET, HEAD and POST.

An example:
<pre>Local s:TStream = THttpStream.Get("www.google.com")

While Not s.Eof()
	Print s.ReadLine()
Wend</pre>
End Rem
Type THttpStream Extends TStreamWrapper
	
Rem
bbdoc: The HTTP User-Agent
about:
Only sent if you provide a non-empty value.
End Rem
	Global user_agent:String = ""
	
Rem
bbdoc: HTTP headers sent by the server
about:
An example:
<pre>Local s:THttpStream = THttpStream.Head("www.google.com")

For h:string = EachIn s.headers
	Print h
Next</pre>
End Rem
	Field headers:String[]
	
	Method _ReceiveHeaders()
		Local l:String = _stream.ReadLine()
		headers = [l]
		l = _stream.ReadLine()
		While l<>""
			headers = headers + [l]
			l = _stream.ReadLine()
		Wend
	End Method
	
	Method _SendHeaders()
		If user_agent _stream.WriteLine "User-Agent: "+user_agent
		If headers
			For Local h:String = EachIn headers
				_stream.WriteLine h
			Next
		End If
		_stream.WriteLine ""
	End Method
	
Rem
bbdoc: Requests an URL using GET
about:
A convieniency function that allows you to easily GET an URL.
The http-prefix is optional and you can also supply authentication details:
<pre>Local s:TStream = THttpStream.GetURL("http://user:password@www.example.com/file.html")</pre>
End Rem
	Function GetURL:THttpStream(url:String)
		If url.StartsWith("http://") url = url[7..]
		
		Local at:Int = url.Find("@"), auth:String
		If at <> -1
			auth = url[..at]
			Local c:Byte Ptr = auth.ToCString()
			auth = TBase64.Encode(c, auth.length)
			MemFree c
			auth = "Authorization: Basic "+auth
			url = url[at+1..]
		End If
		
		at = url.Find("/")
		Local server:String, file:String
		If at = -1
			server = url
			file = "/"
		Else
			server = url[..at]
			file = url[at..]
		End If
		
		If auth Return Get(server, 80, file, [auth])
		
		Return Get(server, 80, file)
	End Function
	
Rem
bbdoc: HTTP GET method
about:
Requests a file on a server with GET method. You can also supply your own additional headers.
End Rem
	Function Get:THttpStream(server:String, port:Int=80, file:String="/", headers:String[]=Null)
		Local stream:TStream=TSocketStream.CreateClient( server, port )
		If Not stream Return Null
		
		stream.WriteLine "GET "+file+" HTTP/1.0"
		stream.WriteLine "Host: "+server
		
		Local h:THttpStream = New THttpStream
		h._stream = stream
		h.headers = headers
		
		h._SendHeaders
		h._ReceiveHeaders
		
		Return h
	End Function
	
Rem
bbdoc: GET with authentication
about:
Requests a file on a server with GET method.
You can supply authentication details and also additional headers.
End Rem
	Function GetAuth:THttpStream(server:String, user:String, pass:String, port:Int=80,..
			file:String="/", headers:String[]=Null)
		Local auth:String = user+":"+pass
		Local c:Byte Ptr = auth.ToCString()
		auth = TBase64.Encode(c, auth.length)
		MemFree c
		
		If headers
			headers = headers + ["Authorization: Basic "+auth]
		Else
			headers = ["Authorization: Basic "+auth]
		End If
		
		Return Get(server, port, file, headers)
	End Function
	
Rem
bbdoc: HTTP HEAD method
about:
Requests the headers for a file on a server. Use #headers to read them.
End Rem
	Function Head:THttpStream(server:String, port:Int=80, file:String="/", headers:String[]=Null)
		Local stream:TStream=TSocketStream.CreateClient( server, port )
		If Not stream Return Null
		
		stream.WriteLine "HEAD "+file+" HTTP/1.0"
		stream.WriteLine "Host: "+server
		
		Local h:THttpStream = New THttpStream
		h._stream = stream
		h.headers = headers
		
		h._SendHeaders
		h._ReceiveHeaders
		
		Return h
	End Function
	
Rem
bbdoc: HEAD with authentication
about:
Requests headers for a file on a server with HEAD method.
You can supply authentication details and also additional headers.
End Rem
	Function HeadAuth:THttpStream(server:String, user:String, pass:String, port:Int=80,..
			file:String="/", headers:String[]=Null)
		Local auth:String = user+":"+pass
		Local c:Byte Ptr = auth.ToCString()
		auth = TBase64.Encode(c, auth.length)
		MemFree c
		
		If headers
			headers = headers + ["Authorization: Basic "+auth]
		Else
			headers = ["Authorization: Basic "+auth]
		End If
		
		Return Head(server, port, file, headers)
	End Function
	
Rem
bbdoc: HTTP POST method
about:
POST sends data to the server. You must specify the length any content of data.
Example:
<pre>Local s:String = "userid=admin&password=admin"
Local data:Byte Ptr = s.ToCString()
Local s:TStream = THttpStream.Post("www.example.com", data, s.length,..
	"application/x-www-form-urlencoded", 80, "/auth.php")</pre>
End Rem
	Function Post:THttpStream(server:String, content:Byte Ptr, clength:Int, ctype:String,..
			port:Int=80, file:String="/", headers:String[]=Null)
		Local stream:TStream=TSocketStream.CreateClient( server, port )
		If Not stream Return Null
		
		stream.WriteLine "POST "+file+" HTTP/1.0"
		stream.WriteLine "Host: "+server
		stream.WriteLine "Content-Length: "+clength
		stream.WriteLine "Content-Type: "+ctype
		
		Local h:THttpStream = New THttpStream
		h._stream = stream
		h.headers = headers
		
		h._SendHeaders
		
		stream.Write content, clength
		stream.WriteLine ""
		
		h._ReceiveHeaders
		
		Return h
	End Function
	
Rem
bbdoc: POST with authentication
about:
An authenticated version of #Post.
End Rem
	Function PostAuth:THttpStream(server:String, content:Byte Ptr, clength:Int, ctype:String,..
			user:String, pass:String, port:Int=80, file:String="/", headers:String[]=Null)
		Local auth:String = user+":"+pass
		Local c:Byte Ptr = auth.ToCString()
		auth = TBase64.Encode(c, auth.length)
		MemFree c
		
		If headers
			headers = headers + ["Authorization: Basic "+auth]
		Else
			headers = ["Authorization: Basic "+auth]
		End If
		
		Return Post(server, content, clength, ctype, port, file, headers)
	End Function
	
End Type
