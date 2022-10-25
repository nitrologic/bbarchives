; ID: 2534
; Author: Chroma
; Date: 2009-07-15 21:52:55
; Title: GNet in BlitzMax
; Description: Simple and concise way to use GNet with your Multiplayer BlitzMax game.

'GNet in BlitzMax

'Original Code by Mark Sibly

'Converted and condensed by Noel Cole (Chroma)

Global GNET_HOST$ = "www.blitzbasic.com"
Global GNET_HOSTIP% = HostIp(GNET_HOST)
Global GNET_PORT=80
Global GNET_GET$="/gnet/gnet.php"

'Example
Local MyGame$ = "This is My Game."
Local MyServer$ = "This is My Server."

GNET_AddServer(MyGame, MyServer)
'GNET_RefreshServer(mygame,"Refreshed")
'GNET_RemoveServer(mygame)

End

'=========================================
'-------------GNET Routines---------------
'=========================================
Function GNET_AddServer(game$, server$="")
	GNET_Exec("add", game, server)
End Function

Function GNET_RefreshServer(game$, server$="")
	GNET_Exec("ref", game, server)
End Function

Function GNET_RemoveServer(game$)
	GNET_Exec("rem", game, "")
End Function

Function GNET_Exec(opt$, game$, server$)
	Local conn:TSockStream = TSockStream.Create()
	ConnectSocket(conn.socket,GNET_HOSTIP,GNET_PORT)
	opt$=opt$+"&game="+Format(game$)
	If server$<>"" opt$=opt$+"&server="+Format(server$)
	WriteLine conn.stream, "GET "+GNET_GET$+"?opt="+opt$+" HTTP/1.0"
	WriteLine conn.stream, "HOST: " + GNET_HOST$
	WriteLine conn.stream, ""
	CloseSocket(conn.socket)
End Function

Type TSockStream
	Field socket:TSocket
	Field stream:TSocketStream
	Function Create:TSockStream(_port%=9050)
		Local ss:TSockStream = New TSockStream
		ss.socket = CreateTCPSocket()
		ss.stream = CreateSocketStream(ss.socket)
		BindSocket(ss.socket, _port)
		Return ss
	End Function
End Type

Function Format$( t$ )
	t$=Replace$( t$,"&","" )
	t$=Replace$( t$,"%","" )
	t$=Replace$( t$,"'","" )
	t$=Replace$( t$,Chr$(34),"" )
	t$=Replace$( t$," ","_" )
	Return t$
End Function
'-----------------------------------------
'=========================================
