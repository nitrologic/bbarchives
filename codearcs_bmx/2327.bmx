; ID: 2327
; Author: JoshK
; Date: 2008-10-03 07:52:47
; Title: GamePool
; Description: Creates a list of public servers for a game

SuperStrict

Import brl.stream
Import brl.socket
Import brl.httpstream
Import brl.linkedlist

Type TGamePoolServer
	
	Const LOCALIP:Int=2130706433
	
	Field ip:Int=LOCALIP
	Field url$
	Field game$
	Field name$

	Method Delete()
		Local stream:TStream
		If url
			stream=OpenStream(url+"?action=removeserver")
			If stream stream.close()
		EndIf
	EndMethod
	
	Method Refresh:Int()
		Local stream:TStream
		stream=OpenStream(url+"?action=refreshserver")
		If Not stream Return False
		stream.close()
		Return True	
	EndMethod
	
	Function Create:TGamePoolServer(url$,name$="",game$="")
		Local server:TGamePoolServer=New TGamePoolServer
		Local stream:TStream
		server.url=url
		server.name=name
		server.game=game
		server.url=url
		stream=OpenStream(url+"?action=addserver&gamename="+game+"&servername="+name)
		If Not stream Return Null
		While Not stream.Eof()
			If ReadLine(stream).Trim()
				stream.close()
				Return Null
			EndIf
		Wend
		If stream stream.close()
		Return server
	EndFunction
	
	Function Get:TGamePoolServer[](url$,game$="")
		Local descarray:TGamePoolServer[]
		Local list:TList
		Local stream:TStream
		Local s$,sarr$[],n:Int
		Local server:TGamePoolServer
		stream=OpenStream(url+"?action=listservers&gamename="+game)
		If Not stream Return Null
		list=New TList
		While Not stream.Eof()
			s=ReadLine(stream)
			sarr=s.split("|")
			If sarr.length=2
				list.addfirst(s)
			EndIf
		Wend
		stream.close()
		If list.IsEmpty() Return Null
		descarray=New TGamePoolServer[list.count()]
		For s=EachIn list
			sarr=s.split("|")
			server=New TGamePoolServer
			server.game=game
			server.ip=HostIp(sarr[0])
			server.name=sarr[1]
			descarray[n]=server
			n:+1
		Next
		Return descarray
	EndFunction
	
EndType

Function CreateGamePoolServer:TGamePoolServer(url$,name$="",game$="")
	Return TGamePoolServer.Create(url$,name$,game)
EndFunction

Function RefreshGamePoolServer(server:TGamePoolServer)
	server.Refresh()
EndFunction

Function GetGamePoolServers:TGamePoolServer[](url$,game$="")
	Return TGamePoolServer.Get(url,game)
EndFunction

Function GetGamePoolServerIP:Int(server:TGamePoolServer)
	Return server.ip
EndFunction

Function GetGamePoolServerName:String(server:TGamePoolServer)
	Return server.name
EndFunction

Function GetGamePoolServerGame:String(server:TGamePoolServer)
	Return server.game
EndFunction
