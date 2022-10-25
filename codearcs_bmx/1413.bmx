; ID: 1413
; Author: boomboommax
; Date: 2005-06-29 08:46:15
; Title: gnet conversion
; Description: gnet listing service for max

Type networkgnet
	Const host:String = "www.blitzbasic.com"
	Const hostget:String = "/gnet/gnet.php"
	
	Const port:Int = 80
	
	Global socket:tsocket
	Global stream:tsocketstream
	
	Function gnet_esc:String(t:String)
		
		t = Replace(t,"&","")
		t = Replace(t,"%","")
		t = Replace(t,"'","")
		t = Replace(t,Chr(34),"")
		t = Replace(t," ","_")
		
		Return t
		
	End Function
	
	Function gnet_open:tsocketstream(opt:String)
		
		networkgnet.socket = CreateTCPSocket()
		
		ConnectSocket(networkgnet.socket,HostIp(networkgnet.host),networkgnet.port)
		networkgnet.stream = CreateSocketStream(networkgnet.socket,True)
		
		WriteLine networkgnet.stream,"GET "+networkgnet.hostget+"?opt="+opt+" HTTP/1.0"
		WriteLine networkgnet.stream,"HOST: "+networkgnet.host
		WriteLine networkgnet.stream,""
		
		FlushStream(networkgnet.stream)
		
		While ReadLine(networkgnet.stream) <> ""
		Wend
		
		Return networkgnet.stream
		
	End Function
	
	Function gnet_exec(opt:String,game:String,server:String)
		
		opt = opt+"&game="+networkgnet.gnet_esc(game)
		
		If server <> "" opt = opt+"&server="+networkgnet.gnet_esc(server)
		
		Local t:tsocketstream = networkgnet.gnet_open(opt)
		If Not t Then Return False
		
		Local ok = False
		If ReadLine(t) = "OK" Then ok = True
		
		CloseSocket(networkgnet.socket)
		Return ok
		
	End Function
	
	Function gnet_ping:String()
		
		Local t:tsocketstream = networkgnet.gnet_open("ping")
		If Not t Then Return False
		
		Local ip:String = ReadLine(t)
		
		CloseSocket(networkgnet.socket)
		Return ip
		
	End Function
	
	Function gnet_addserver(game:String,server:String="")
		
		Return networkgnet.gnet_exec("add",game,server)
		
	End Function
	
	Function gnet_refreshserver(game:String,server:String="")
		
		Return networkgnet.gnet_exec("ref",game,server)
		
	End Function
	
	Function gnet_removeserver(game:String)
		
		Return networkgnet.gnet_exec("rem",game,"")
		
	End Function
	
	Function gnet_listservers(game:String="")
		
		For Local n:networkgnet_server = EachIn networkserverlist
			networkserverlist.remove(n)
			n = Null
		Next
		
		FlushMem
		
		Local t:tsocketstream = networkgnet.gnet_open("list")
		If Not t Then Return False
		
		Local t_game:String
		Local t_server:String
		Local t_ip:String
		
		Repeat
			t_game = ReadLine(t)
			If t_game = "" Then Exit
			
			t_server = ReadLine(t)
			t_ip = ReadLine(t)
			
			If game = "" Or game = t_game
				Local p:networkgnet_server = New networkgnet_server
				
				p.game = t_game
				p.server = t_server
				p.ip = t_ip
				
				networkserverlist.addfirst(p)
			EndIf
		Forever
		
		CloseSocket(networkgnet.socket)
		Return 1
		
	End Function
End Type


Global networkserverlist:TList = New TList

Type networkgnet_server
	Field game:String
	Field server:String
	Field ip:String
End Type
