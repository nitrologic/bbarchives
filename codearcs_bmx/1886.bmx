; ID: 1886
; Author: Fabian.
; Date: 2006-12-24 01:18:48
; Title: fmc.Tweaks
; Description: Overrides some brl functions correcting some bugs

Strict
Module fmc.Tweak

ModuleInfo "Version: 0.07"
ModuleInfo "Modserver: Fabian"

Import fmc.Development
Import brl.win32maxgui
Import brl.stream
Import brl.socket

Private

Global __SetHotKeyEvent:TFunctionHook = TFunctionHook.Create ( SetHotKeyEvent , _SetHotKeyEvent )
Global __bbSetPointer:TFunctionHook = TFunctionHook.Create ( bbSetPointer , _bbSetPointer )
Global __CasedFileName:TFunctionHook = TFunctionHook.Create ( CasedFileName , _CasedFileName )
Global __OpenStream:TFunctionHook '= TFunctionHook.Create ( OpenStream , _OpenStream )
Global __bbObjectNew:TFunctionHook '= TFunctionHook.Create ( bbObjectNew , _bbObjectNew )
Global __ActivateWindow:TFunctionHook = TFunctionHook.Create ( ActivateWindow , _ActivateWindow )
Global __UpdateLocalName:TFunctionHook = TFunctionHook.Create ( UpdateLocalName , _UpdateLocalName )
Global __UpdateRemoteName:TFunctionHook = TFunctionHook.Create ( UpdateRemoteName , _UpdateRemoteName )
Global __HostIp:TFunctionHook = TFunctionHook.Create ( HostIp , _HostIp )
Global __OpenURL:TFunctionHook = TFunctionHook.Create ( OpenURL , _OpenURL )

Function _SetHotKeyEvent:THotKey( key,mods,event:TEvent=Null,owner=0 )
	If Not event event=CreateEvent( EVENT_HOTKEYHIT,Null,key,mods )
	Local t:THotKey=hotKeys
	While t
		If t.key=key And t.mods=mods And t.owner=owner Exit
		t=t.succ
	Wend
	If Not t
		t=New THotKey
		t.key=key
		t.mods=mods
		t.owner=owner
		t.succ=hotKeys
		hotKeys=t
	EndIf
	t.event=event
	Return t
EndFunction

Function _bbSetPointer ( h ) NoDebug
  If h<1 Or h>14
    bbSetCursor 0
    Local Point:Long
    GetCursorPos Varptr Point
    SetCursorPos ( ( Int Ptr Varptr Point ) [ 0 ] , ( Int Ptr Varptr Point ) [ 1 ] )
  Else
    __bbSetPointer.Disable
    bbSetPointer h
    __bbSetPointer.Enable
  EndIf
EndFunction

Function _CasedFileName$ ( path$ )
	Local drive$
	If Len path >= 2 And path [ 1 ] = Asc ":"
		drive = path [.. 2 ].ToUpper ( )
		path = path [ 2 ..]
	EndIf
	Local folder$ = "." , length = Len path , start
	For Local i = 0 To length
		If i = length Or path [ i ] = Asc "/" Or path [ i ] = Asc "\"
			If i = start
				folder = "/"
			Else
				Local dir = opendir_ ( drive + folder )
				If Not dir
					Return
				EndIf
				Local name$ = path [ start .. i ] , file$
				Repeat
					file = readdir_ ( dir )
					If Not file
						closedir_ dir
						Return
					EndIf
					If file.ToLower ( ) = name.ToLower ( )
						Exit
					EndIf
				Forever
				closedir_ dir
				folder = path [.. start ] + file
				path = folder + path [ i ..]
			EndIf
			start = i + 1
		EndIf
	Next
	Return drive + path
EndFunction

Global stream_factories:TStreamFactory

Function _OpenStream:TStream( url:Object,readable=True,writeable=True )

	Local stream:TStream=TStream( url )
	If stream
		Return TStreamStream.Create( stream )
	EndIf

	Local str$=String( url ),proto$,path$
	If str
		Local i=str.Find( "::",0 )
		If i=-1
			Local r:TStream=TCStream.OpenFile( str,readable,writeable )
			If r Return r
		Else
			proto$=str[..i].ToLower()
			path$=str[i+2..]
		EndIf
	EndIf

	Local factory:TStreamFactory=stream_factories
	
	While factory
		Local stream:TStream=factory.CreateStream( url,proto,path,readable,writeable )
		If stream Return stream
		factory=factory._succ
	Wend
EndFunction

Function _bbObjectNew:Object ( t ) NoDebug
  __bbObjectNew.Disable
  Local obj:Object = bbObjectNew ( t )
  __bbObjectNew.Enable
  Local f:TStreamFactory = TStreamFactory ( obj )
  If f
    stream_factories = f
  EndIf
  Return obj
EndFunction

Function _ActivateWindow ( window:TGadget )
  SetForegroundWindow QueryGadget ( window , QUERY_HWND )
EndFunction

Function _UpdateLocalName ( socket:TSocket )
	Local addr:Byte Ptr=MemAlloc(16),size=16
	If getsockname_( socket._socket,addr,size )<0
		socket._localIp=0
		socket._localPort=0
		MemFree addr
	EndIf
	socket._localIp=addr[4] Shl 24 | addr[5] Shl 16 | addr[6] Shl 8 | addr[7]
	socket._localPort=addr[2] Shl 8 | addr[3]
	MemFree addr
EndFunction

Function _UpdateRemoteName ( socket:TSocket )
	Local addr:Byte Ptr=MemAlloc(16),size=16
	If getpeername_( socket._socket,addr,size )<0
		socket._remoteIp=0
		socket._remotePort=0
		MemFree addr
		Return
	EndIf
	socket._remoteIp=addr[4] Shl 24 | addr[5] Shl 16 | addr[6] Shl 8 | addr[7]
	socket._remotePort=addr[2] Shl 8 | addr[3]
	MemFree addr
EndFunction

Function _HostIp ( HostName$ , index = 0 )
	If index<0 Return
	Local addr_type,addr_len
	Local addrs:Byte Ptr Ptr=gethostbyname_( HostName,addr_type,addr_len )
	If addrs=Null Or addr_type<>AF_INET_ Or addr_len<>4 Return
	Local n
	While addrs[n]
		n:+1
	Wend
	Local ips Ptr=Int Ptr MemAlloc(n*4)
	For Local i=0 Until n
		Local p:Byte Ptr=addrs[i]
		ips[i]=p[0] Shl 24 | p[1] Shl 16 | p[2] Shl 8 | p[3]
	Next
	Local addr
	If index<n addr=ips[index]
	MemFree ips
	Return addr
EndFunction

Function _OpenURL( url$ )
	Return Driver.OpenURL( url )
EndFunction

Extern
  Function bbSetPointer ( h )
  Function bbSetCursor ( cursor ) = "_Z11bbSetCursorP7HICON__"
  Function bbObjectNew:Object ( t )
  Function UpdateLocalName ( socket:TSocket ) = "_brl_socket_TSocket_UpdateLocalName"
  Function UpdateRemoteName ( socket:TSocket ) = "_brl_socket_TSocket_UpdateRemoteName"
EndExtern

Extern "Win32"
  Function SetForegroundWindow ( Win )
  Function GetCursorPos ( Point:Byte Ptr )
  Function SetCursorPos ( X , Y )
EndExtern
