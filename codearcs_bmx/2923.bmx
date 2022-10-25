; ID: 2923
; Author: EdzUp[GD]
; Date: 2012-02-20 03:31:40
; Title: Graveyard Dogs GNet module
; Description: Module that performs like Blitz3d's GNet module

Rem
bbdoc:Graveyard Dogs GNet module
EndRem
Module gd.gnet
ModuleInfo "Version: 1.0"
ModuleInfo "Author: Ed Upton, Mark Sibly (Blitz basic code)"
ModuleInfo "License: Public Domain"
ModuleInfo ""
ModuleInfo "History: 1.0"
ModuleInfo "       : Initial release."

SuperStrict 

Import BRL.SocketStream
Import BRL.LinkedList
Import BRL.Retro

Rem
bbdoc: GNet server data
about:this holds the data from list server it will allow you to get information from GNet.
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
Global GNetServerList:TList = CreateList()
Global GNetServer:TGNetServer = Null					'here for SuperStrict declaration
Rem
bbdoc:GDGNet system
about:Main type
EndRem
Type GDGNetType
	Field Host:String = "www.blitzbasic.com"
	Field HostAddress:Int = HostIp( Host )
	Field Port:Int = 80
	Field Get:String = "/gnet/gnet.php"
	
	Field GameName:String = ""
	Field ServerName:String = ""
	
Rem
bbdoc: AddServer( game name$, server name$ )
about:Game name is the games name ie "Pong", "Space invaders" etc.
Server name is the name of your particular server, ie "CTF game", "2v2 deathmatch".
Both should be kept as short as possible.
returns: nothing
EndRem
	Function AddServer( asgame:String, asserver:String = "" )
		Exec( "add", asgame, asserver )
	End Function
	
Rem
bbdoc: RefreshServer( game name$, server name$ )
about:This must be called to keep your server alive, after five minutes of inactivity the servers are removed.
returns: nothing
EndRem
	Function RefreshServer( rsgame:String, rsserver:String ="" )
		Exec( "ref", rsgame, rsserver )
	End Function
	
Rem
bbdoc: RemoveServer( game name$ )
about:This removes the server from GNet, this can be done when you dont want it on there any more or if your game is closing down.
returns: nothing
EndRem
	Function RemoveServer( rsgame:String )
		Exec( "rem", rsgame, "" )
	End Function

Rem
bbdoc: Gets the online server list
about:Searches for servers for the specified game, or all games if @game is omitted.
The results of the search are returned as a list of TGNetServer objects.
Use @For .. @EachIn to examine the server list.
A TList containing the TGNetServer objects.
End Rem
	Function ListServers( game:String="" )
		Local Socket:TSocket
		Local Stream:TSocketStream
		
		Socket = CreateTCPSocket()
		Stream = CreateSocketStream( Socket )
		
		If stream=Null Then End
		BindSocket( Socket, 9050 )
		
		ConnectSocket( Socket, GDGNet.HostAddress, GDGNet.Port )		
		WriteLine( Stream, "GET "+GDGNet.Get+"?opt=list HTTP/1.0" )
		WriteLine( Stream, "HOST: "+GDGNet.Host )
		WriteLine( Stream, "" )

		Local t_game:String
		Local t_server:String
		Local t_ip:String
		
		Local Ignore:Int = 0
		For Ignore =0 To 7
			t_game = ReadLine( Stream )
		Next
		
		Repeat
			t_game = ReadLine( Stream )
			If t_game[..2]<>"<b" And t_game<>""
				t_server = ReadLine( Stream )
				t_ip = ReadLine( Stream )
				
				If game="" Or Format( game ) =t_game
					GNetServer:TGNetServer = New TGNetServer
					GNetServer.Game = t_game
					GNetServer.Server = t_server
					GNetServer.IP = t_ip
					ListAddLast( GNetServerList, GNetServer )
				EndIf
			EndIf
		Until Eof( Stream )

		CloseStream( Stream )
		CloseSocket( Socket )
	End Function

	Function Exec( opt:String, game:String, server:String )
		Local Socket:TSocket
		Local Stream:TSocketStream
		
		Socket = CreateTCPSocket()
		Stream = CreateSocketStream( Socket )
		
		If stream=Null Then End
		BindSocket( Socket, 9050 )
		
		ConnectSocket( Socket, GDGNet.HostAddress, GDGNet.Port )
		opt :+ "&game="+Format( Game )
		If server<>"" opt :+ "&server="+Format( server )
		
		WriteLine( Stream, "GET "+GDGNet.Get+"?opt="+opt+" HTTP/1.0" )
		WriteLine( Stream, "HOST: "+GDGNet.Host )
		WriteLine( Stream, "" )
		CloseStream( Stream )
		CloseSocket( Socket )
	End Function
		
	Function Format:String( t:String )
		t$=Replace$( t$,"&","" )
		t$=Replace$( t$,"%","" )
		t$=Replace$( t$,"'","" )
		t$=Replace$( t$,Chr$(34),"" )
		t$=Replace$( t$," ","_" )
		Return t$
	End Function
End Type
Global GDGNet:GDGNetType = New GDGNetType
