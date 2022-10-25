; ID: 2818
; Author: Oddball
; Date: 2011-01-26 19:09:45
; Title: GNetServerList
; Description: Use the GNet server listing service with BlitzMax

'GNet Server Listing Module
SuperStrict

Rem
bbdoc: Networking/GNetServerList
End Rem
Module ODD.GNetServerList

ModuleInfo "Version: 1.0"
ModuleInfo "Author: David Williamson, Mark Sibly"
ModuleInfo "License: Public Domain"

ModuleInfo "History: 1.0"
ModuleInfo "       : Initial release."

Import BRL.SocketStream
Import BRL.LinkedList

Rem
bbdoc: GNet server data
End Rem
Type TGNetServer
	Rem
	bbdoc:  The game's name.
	End Rem
	Field game:String
	Rem
	bbdoc:  The server's name/descriptor
	End Rem
	Field server:String
	Rem
	bbdoc:  The server's global IP address
	End Rem
	Field ip:String
End Type

Rem
bbdoc: Initializes GNetServerList
about: If GNetServerList is not initialized then the online server list at http://www.blitzbasic.com/gnet/gnet.php is used.
End Rem
Function InitGNetServerList( host:String, get:String, port:Int=80 )
	_host=host
	_get=get
	_port=port
End Function

Rem
bbdoc: Pings the online server list
returns: The global IP of the computer
End Rem
Function GNetPing:String()
	Local stream:TSocketStream=Open("ping")
	If Not stream Then Return False
	
	Local ip:String=ReadLine(stream)
	
	CloseStream stream
	Return ip
End Function

Rem
bbdoc: Adds a server to the online list
returns: True if successful
about: @Game is a string describing your game; eg. "Pong", "CallOfDuty", etc.

@Server is an optional string which can be used to describe the server; eg. "UK_Server", "3v3Deathmatch", etc.

@Game and @server should be kept short, and use only alphanumeric characters.
End Rem
Function AddGNetServer:Int( game:String, server:String="" )
	Return Exec("add",game,server)
End Function

Rem
bbdoc: Refreshes the server
returns: True if successful
about: Servers are automatically removed after 5 minutes of inactivity.
Use @RefreshGNetServer to keep your server 'alive'.
If @server is defined then the lsiting is updated with the new server name.
End Rem
Function RefreshGNetServer:Int( game:String, server:String="" )
	Return Exec("ref",game,server)
End Function

Rem
bbdoc: Removes the server from the online list
returns: True if successful
about: You should always remove servers at game exit, or before starting a new server session.
End Rem
Function RemoveGNetServer:Int( game:String )
	Return Exec("rem",game,"")
End Function

Rem
bbdoc: Gets the online server list
returns: A TList containing the TGNetServer objects
about: Searches for servers for the specified game, or all games if @game is omitted.

The results of the search are returned as a list of TGNetServer objects.

Use @For .. @EachIn to examine the server list.
End Rem
Function ListGNetServers:TList( game:String="" )
	Local list:TList=New TList
	
	Local stream:TSocketStream=Open("list")
	If Not stream Then Return Null
	
	Local t_game:String
	Local t_server:String
	Local t_ip:String
	
	Repeat
		t_game=ReadLine(stream)
		If t_game[..2]<>"<b" And t_game<>""
			t_server=ReadLine(stream)
			t_ip=ReadLine(stream)
			
			If game="" Or Esc(game)=t_game
				Local p:TGNetServer=New TGNetServer
				
				p.game=t_game
				p.server=t_server
				p.ip=t_ip
				
				list.AddFirst p
			EndIf
		EndIf
	Until Eof(stream)
	
	CloseStream stream
	Return list
End Function

Private

Global _host:String="www.blitzbasic.com"
Global _get:String="/gnet/gnet.php"
Global _port:Int=80

Function Esc:String( t:String )
	t=t.Replace("&","")
	t=t.Replace("%","")
	t=t.Replace("'","")
	t=t.Replace(Chr(34),"")
	t=t.Replace(" ","_")
	Return t
End Function

Function Open:TSocketStream( opt:String )
	Local socket:TSocket=CreateTCPSocket()
	ConnectSocket socket,HostIp(_host),_port
	Local stream:TSocketStream=CreateSocketStream(socket,True)
	
	WriteLine stream,"GET "+_get+"?opt="+opt+" HTTP/1.0"
	WriteLine stream,"HOST: "+_host
	WriteLine stream,""
	FlushStream stream
	
	While ReadLine(stream)<>""
	Wend
	
	Return stream
End Function

Function Exec:Int( opt:String, game:String, server:String )
	opt=opt+"&game="+Esc(game)
	
	If server<>"" Then opt=opt+"&server="+Esc(server)
	
	Local stream:TSocketStream=Open(opt)
	If Not stream Then Return False
	
	Local ok:Int=False
	If ReadLine(stream)="OK" Then ok=True
	
	CloseStream stream
	Return ok
End Function
