; ID: 2238
; Author: Xzider
; Date: 2008-04-05 20:54:39
; Title: Online Game Code
; Description: For beginners-Online Game Code-

/////////////////////
///////Server///////
///////////////////

AppTitle "Server"

Global Server%
Global Stream%
Global Port% = 5000
Global PID% ;Player ID to use with NewPID() Function
Global P.Player
Global ServerUpdate% = MilliSecs()
Global ServerUpdateDelay% = 200

Type Player

  Field Stream%
  Field ID%
  Field Name$
  Field X#
  Field Y#
  Field Z#
  Field Linkdead%

End Type

		Server% = CreateServer(Port%)

While Not KeyHit(1)

		Stream% = AcceptTCPStream(Server%)

  If Stream% ;If somone connected

		;Create Player
		
		P.Player = New Player
		P\Stream% = Stream% ;This is now the players stream, this is how you would send him messages
		P\ID% = NewPID()
		P\Name$ = ReadLine(P\Stream%) ;Expecting client to send name as soon as he connects
		Print P\Name$ + " connected!"
		
		UpdatePlayerToAll(0,P\ID%)
		UpdatePlayerToAll(1,P\ID%)
		
  End If

		;Scan players
		
  For P.Player = Each Player

  If ReadAvail(P\Stream%)

		;Check messages sent from client to server
		Msg% = ReadByte(P\Stream%)
		CheckMsg(Msg%)
		
  End If

  If Eof(P\Stream%)

		P\Linkdead% = 1
		
  End If

  If P\LinkDead% = 1

		TempID% = P\ID%
		
  For Ld.Player = Each Player

  If Ld\ID% <> TempID%

		WriteByte(Ld\Stream%,3)
		WriteByte(Ld\Stream%,TempID%)

  End If
	
  Next

		Print P\Name$ + " disconnected!"
		Delete P.Player
		
  End If

  Next

  If MilliSecs() >= ServerUpdate% + ServerUpdateDelay%

		ServerUpdate% = MilliSecs()

  For P.Player = Each Player

		TempID% = P\ID%
		TempX# = P\X#
		TempY# = P\Y#
		TempZ# = P\Z#
		
  For Coord.Player = Each Player

  If Coord\ID% <> TempID% ; Dont send to the person we are updating, he already knows his own coords!:)

		WriteByte(Coord\Stream%,2)
		WriteByte(Coord\Stream%,TempID%)
		WriteFloat(Coord\Stream%,TempX#)
		WriteFloat(Coord\Stream%,TempY#)
		WriteFloat(Coord\Stream%,TempZ#)
		
  End If

  Next

  Next

  End If

		Delay 10
Wend

Function CreateServer(TempPort%)

		TempServer% = CreateTCPServer(TempPort%)
		
  If TempServer%

		Print "Server started on port " + TempPort% + "!"
		
  Else

		Print "Server could not be started!"
		Print "Press any key to quit."
		WaitKey
		End
		
  End If


  Return TempServer%

End Function

Function CheckMsg(TempMsg%)

Select TempMsg%

  Case 1

		;Could be used to update coords here.
		TempX# = ReadFloat(P\Stream%)
		TempY# = ReadFloat(P\Stream%)
		TempZ# = ReadFloat(P\Stream%)
		
		P\X# = TempX#
		P\Y# = TempY#
		P\Z# = TempZ#
		
		Print "<"+P\ID%+"> " + P\Name$ + " - X:" + P\X# + " - Y:" + P\Y# + " - Z:" + P\Z# ;DELETE THIS CODE, LAGS SERVER,FOR DEBUG ONLY
		
Default

		Print "Opcode not recognized " + TempMsg%
		
End Select

End Function

Function NewPID()

		PID% = PID% + 1
		
  Return PID%

End Function

;;;;;

Function UpdatePlayerToAll(TempAll%,TempID%)
		
  If TempAll% = 0
			
  For Find.Player = Each Player
				
  If Find\ID% = TempID%
					
		TempName$ = Find\Name$
					
  End If
				
  Next
			
  For Send.Player = Each Player
				
  If Send\ID% <> TempID%
					
		WriteByte(Send\Stream%,1)
		WriteByte(Send\Stream%,TempID%)
		WriteLine(Send\Stream%,TempName$)
		
  End If

  Next
			
  ElseIf TempAll% = 1
			
  For Find.Player = Each Player
				
  If Find\ID% = TempID%
					
		TempID% = Find\ID%
		TempStream% = Find\Stream%
					
  End If
				
  Next
			
  For Send.Player = Each Player
						
  If Send\ID% <> TempID%

		WriteByte(TempStream%,1)
		WriteByte(TempStream%,Send\ID%)
		WriteLine(TempStream%,Send\Name$)			
					
  End If
				
  Next
			
  End If
		
		TempSucess% = 1
		
  Return TempSucess%

End Function

////////////////////
//Client////////////
////////////////////

Graphics3D 800,600,32,2
AppTitle "Client"

Global Client%
Global IP$ = "127.0.0.1"
Global Port% = 5000
Global Name$
Global Camera% = CreateCamera()
Global UpdateCounter% = MilliSecs()
Global UpdateDelay% = 500
Global X#
Global Y#
Global Z#

Type Player

  Field ID%
  Field Entity%
  Field Name$
  Field X#
  Field Y#
  Field Z#
  Field DeltaX#
  Field DeltaY#
  Field DeltaZ#

End Type

		Name$ = Input("Your name: ")
		
		Client% = Connect(IP$,Port%)
		
		WriteLine(Client%,Name$)
		
		ConstructWorld()
		
While Not KeyHit(1)

  Cls

		UpdateWorld
		RenderWorld
		
		Handle_Coords()
		UpdatePlayers()
		Update()
		
  If ReadAvail(Client%) > 0

		Msg% = ReadByte(Client%)
		CheckMsg(Msg%)
		
  End If

  If Eof(Client%)

		RuntimeError "Disconnected from server!"
		
  End If

  Flip

Wend

Function Connect(TempIP$,TempPort%)

		TempClient% = OpenTCPStream(TempIP$,TempPort%)
		
  If TempClient%

		Print "Connected to " + TempIP$ + " on port " + TempPort%  + "!"
		
  Else

		Print "Could not connect!"
		Print "Press any key to quit."
		WaitKey
		End
		
  End If

  Return TempClient%

End Function

Function CheckMsg(TempMsg%)

Select TempMsg%

  Case 1

		;Add new player
		
		P.Player = New Player
		
		P\ID% = ReadByte(Client%)
		P\Name$ = ReadLine(Client%)
		P\Entity% = CreateSphere()
		EntityColor P\Entity%,0,0,200
		
  Case 2


		TempID% = ReadByte(Client%)
		
  For P.Player = Each Player

  If P\ID% = TempID%

		P\X# = ReadFloat(Client%)
		P\Y# = ReadFloat(Client%)
		P\Z# = ReadFloat(Client%)
		
	    P\DeltaX# = P\X# - EntityX(P\Entity%)
        P\DeltaY# = P\Y# - EntityY(P\Entity%)
        P\DeltaZ# = P\Z# - EntityZ(P\Entity%)

		;Delta the coordinates so he doesnt warp.
        P\DeltaX# = P\DeltaX#/100
        P\DeltaY# = P\DeltaY#/100
        P\DeltaZ# = P\DeltaZ#/100
 		Exit

  End If

  Next

  Case 3

		TempID% = ReadByte(Client%)
		
  For P.Player = Each Player

  If P\ID% = TempID%

		FreeEntity P\Entity%
		Delete P.Player
		
  End If

  Next

Default

		RuntimeError "Opcode not recognized " + TempMsg%

		WaitKey 
		End
		
End Select

End Function

Function ConstructWorld()

		PositionEntity Camera%,0,5,0
		Terrain% = CreatePlane()
		EntityColor Terrain%,0,150,0
		CreateCube()
		
End Function

Function Update()

  If KeyDown(200)

		MoveEntity Camera%,0,0,0.2
		
  End If


  If KeyDown(208)

		MoveEntity Camera%,0,0,-0.2
		
  End If


  If KeyDown(203)

		TurnEntity Camera%,0,0.5,0
		
  End If


  If KeyDown(205)

		TurnEntity Camera%,0,-0.5,0
		
  End If

		X# = EntityX(Camera%)
		Y# = EntityY(Camera%)
		Z# = EntityZ(Camera%)
		
		PositionEntity Camera%,X#,Y#,Z#

End Function

Function Handle_Coords()

  If MilliSecs() >= UpdateCounter% + UpdateDelay

		UpdateCounter% = MilliSecs()
		
		WriteByte(Client%,1)
		WriteFloat(Client%,X#)
		WriteFloat(Client%,Y#)
		WriteFloat(Client%,Z#)

  End If

End Function

Function UpdatePlayers()

  For P.Player = Each Player

  Color 0,0,255


  If EntityInView(P\Entity%,Camera%)

		CameraProject Camera%,EntityX(P\Entity%),EntityY(P\Entity%),EntityZ(P\Entity%)
		Text ProjectedX(),ProjectedY(),">" + P\Name$ + "<"
		
  End If

		TranslateEntity P\Entity%,P\DeltaX#,P\DeltaY#,P\DeltaZ#
		
  Next

End Function
