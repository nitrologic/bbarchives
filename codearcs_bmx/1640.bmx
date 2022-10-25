; ID: 1640
; Author: WendellM
; Date: 2006-03-11 14:40:28
; Title: GNet (1.16) sample
; Description: Example of GNet in BlitzMax 1.16 (uses MaxGUI)

'by Wendell Martin - Mar. 11, 2006

Strict

'initialize GNet (both are "hosts" but somebody's gotta "dial up," and that's the "client")
Local hosting = Proceed( "Do you want to be host? (No = be client, Cancel = end)" )
If hosting = -1 Then End
Local host:TGNetHost = CreateGNetHost()
Local port = 1234 ' users would normally choose one allowed through their firewall
Local address$ = "127.0.0.1"' loopback IP address for testing host & client on same machine
Local timeout_ms = 10000 ' client has 10 seconds to connect to host

' initialize host or client
Local success
If hosting Then
	success = GNetListen( host, port )
	If Not success Then RuntimeError "GNetListen failed"
Else
	success = GNetConnect( host, address$, port, timeout_ms )
	If Not success Then RuntimeError "GNetConnect failed"
EndIf

' create local and remote GNet objects
Local localObj:TGNetObject = CreateGNetObject:TGNetObject( host )
Local remoteObj:TGNetObject
Local objList:TList = New TList ' list of received, remote objects

' Host GUI goes at upper-left, Client one to its right.
Local name$, x, y
If hosting Then
	name$ = "Host"
	x = 10
	y = 10
Else
	name$ = "Client"
	x = 500
	y = 10
EndIf
Local win:TGadget = CreateWindow( name$, x,y, 400,300 )
CreateLabel( "LOCAL:", 10,10, 50,20, win )
CreateLabel( "int:", 10,30, 30,20, win )
Local localTxt0:TGadget = CreateTextField( 40,30, 50,20, win )
CreateLabel( "txt:", 10,60, 30,20, win )
Local localTxt1:TGadget = CreateTextField( 40,60, 100,20, win )
CreateLabel( "REMOTE:", 10,90, 50,20, win )
CreateLabel( "int:", 10,110, 30,20, win )
Local remoteTxt0:TGadget = CreateTextField( 40,110, 50,20, win )
CreateLabel( "txt:", 10,140, 30,20, win )
Local remoteTxt1:TGadget = CreateTextField( 40,140, 100,20, win )


Repeat

	Delay 10 ' allow other apps some cycles to minimize CPU use
	PollEvent

	'update local GNet object with any local  changes 
	SetGNetInt localObj, 0, Int( TextFieldText(localTxt0) )
	SetGNetString localObj, 1, TextFieldText(localTxt1)
	
	GNetSync host ' send to other instance & get updates
	
	' Only a single object will be returned in this example, but more-advanced checking
	' would be needed to separate out messages and deal with multiple GNet objects in a
	' more-advanced app
	objList = GNetObjects( host, GNET_MODIFIED )
	For remoteObj = EachIn objList
		SetGadgetText remoteTxt0, GetGNetInt( remoteObj, 0 )
		SetGadgetText remoteTxt1, GetGNetString( remoteObj, 1 )
	Next
	
Until EventID() = EVENT_WINDOWCLOSE
