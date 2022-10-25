; ID: 2209
; Author: Otus
; Date: 2008-02-03 11:32:44
; Title: BlitzPing/1.0
; Description: Uses the pingback protocol to ping all links on a web page.

'BlitzPing/1.0

'GETs a document, and pings the external links found.
'Uses the pingback protocol http://www.hixie.ch/specs/pingback/pingback-1.0

SuperStrict

Framework BRL.Blitz
Import BRL.Socketstream
Import BRL.StandardIO

Type TPingSource
	Field URI:String
	Field host:String
	Field doc:String
	Field data:String
	
	Const agent:String = "BlitzPing/1.0"
	
	Function Open:TPingSource(URI:String)
	'Opens a socket to the URI specified, and reads the content to data.
	'Closes the socket and returns the created object.
		Local s:TPingSource = New TPingSource
		s.URI=URI
		s.host = URI[URI.Find("//")+2..URI.Find("/",7)]
		s.doc = URI[URI.Find("/",7)+1..]
		Local sock:TSocket = CreateTCPSocket()
		If Not ConnectSocket(sock,HostIp(s.host),80) Return Null
		Local stream:TStream = CreateSocketStream(sock)
		WriteLine(stream,..
			"GET /"+s.doc+" HTTP/1.0~n"+..
			"Host: "+s.host+"~n"+..
			"User-Agent: "+agent+"~n~n")
		While Not Eof(stream)
			s.data:+ReadLine(stream)+"~n"
		Wend
		CloseStream(stream)
		If sock CloseSocket(sock)
		Return s
	End Function
	
	Method Process:Int()
	'Reads the data. Creates a ping target from any anchors it finds.
	'Pings valid targets, and returns the number of successful pings.
		Local pos:Int = 0, tag:String, target:String, t:TPingTarget, pings:Int
		While data.Find("<a ",pos)>-1
			pos = data.Find("<a ",pos)+1
			tag = data[pos..data.Find(">",pos)]
			If tag.Find("href=")>-1
				target = tag[tag.Find("href=")+6..]
				If target.Find(" ")>-1
					target = target[..target.Find(" ")-1]
				Else
					target = target[..target.length-1]
				End If
				Print target
				t = TPingTarget.Open(target,host)
				If t 
					'Print "Found: "+t.URI
					If t.Process()
						'Print "Pinging: "+t.server
						pings:+t.Ping(URI)
					End If
				Else
					'Print "Skipping: "+pos+"/"+data.length
				End If
			End If
		Wend
		Return pings
	End Method
End Type

Type TPingTarget
	Field URI:String
	Field host:String
	Field doc:String
	Field data:String
	Field server:String
	
	Const agent:String = "BlitzPing/1.0"
	
	Function Open:TPingTarget(URI:String,host:String)
	'Opens a socket to the URI specified, and reads the content to data.
	'Closes the socket and returns the created object.
		Local t:TPingTarget = New TPingTarget
		t.URI=URI
		t.host = URI[URI.Find("//")+2..URI.Find("/",7)]
		If t.host=host Return Null	'Don't ping the same host
		t.doc = URI[URI.Find("/",7)+1..]
		Local sock:TSocket = CreateTCPSocket()
		If Not ConnectSocket(sock,HostIp(t.host),80) Return Null
		Local stream:TStream = CreateSocketStream(sock)
		WriteLine(stream,..
			"GET /"+t.doc+" HTTP/1.0~n"+..
			"Host: "+t.host+"~n"+..
			"User-Agent: "+agent+"~n~n")
		While Not Eof(stream)
			t.data:+ReadLine(stream)+"~n"
		Wend
		CloseStream(stream)
		If sock CloseSocket(sock)
		Return t
	End Function
	
	Method Process:Int()
	'Finds the ping server from data, and finds out host and doc. 
	'Returns True is ping server found, False if not.
		Local pos:Int = data.Find("X-Pingback:")
		If pos=-1
			'No pingback HTTP header - search for head link
			pos=0
			While data.ToLower().Find("<link",pos) > -1
				pos=data.ToLower().Find("<link",pos)
				Local area:String = data[pos..data.Find(">",pos)]
				If area.Find("pingback")>-1
					server = area[area.Find("href=")+6..area.Find(" ",area.Find("href="))-1]
				End If
			Wend
		Else
			server = data[pos+11..data.Find("~n",pos)].Trim()
		End If
		If Not server Return False	'Server not found - not pingable
		host = server[server.Find("//")+2..server.Find("/",7)]
		doc = server[server.Find("/",7)+1..]
		Return True
	End Method
	
	Method Ping:Int(source:String)
	'Sends an XML/RPC ping to the ping server with source as source and URI as target.
	'Returns True on a successful ping, False on a fail.
		Local sock:TSocket = CreateTCPSocket()
		If Not ConnectSocket(sock,HostIp(host),80) Return Null
		Local stream:TStream = CreateSocketStream(sock)
		If Not stream Return Null
		Local request:String
		request = ..
			"<?xml version=~q1.0~q?>~n"+..
			"<methodCall>~n"+..
			"<methodName>pingback.ping</methodName>~n"+..
			"<params>~n"+..
			"<param><value><string>"+source+"</string></value></param>~n"+..
			"<param><value><string>"+URI+"</string></value></param>~n"+..
			"</params>~n"+..
			"</methodCall>"
		request = "POST /"+doc+" HTTP/1.0~n"+..
			"Host: "+host+"~n"+..
			"User-Agent: "+agent+"~n"+..
			"Content-Type: text/xml~n"+..
			"Content-Length: "+request.length+"~n~n"+..
			request
		'Print "Sending request..."
		'Print request
		WriteLine(stream, request)
		'Print "Receiving response..."
		Rem
		While Not Eof(stream)
			Print ReadLine(stream)
		Wend
		End Rem
		Return True
	End Method
End Type


Local source:String = Input("URL for ping source:")
'Open the ping source
Local s:TPingSource = TPingSource.Open(source)
If s
	Print "Open Successful"
	'Process the source and ping the links
	Print s.Process()+" links pinged"
Else
	Print "Open failed!"
End If
