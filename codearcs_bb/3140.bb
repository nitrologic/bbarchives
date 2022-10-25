; ID: 3140
; Author: Wings
; Date: 2014-08-09 05:04:40
; Title: TCPIP 3D game example
; Description: Framework for a TCPIP connection blitz3d game. ready to be expanded.

; Example blitz3d tcpip game.
; Written by wings on blitz forum wings@tiberion.eu
;

Global server
Global player
Global player_guid ; player clident global indentifyer.
Global player_guid_counter ; Here we create new ids

Global client_debug_network ; A millisecond counter for debuging 
Global link ; This is player link. accaly i store session in this. i prefare name link.


;Data base for players
Type player_type
	Field name$
	Field player_guid
	Field x#,y#,z#,rot#,status
	Field mesh
	Field link
End Type




;Try to Start the server. if port 7001 is not available game goes into client mode.
Server = CreateTCPServer (7001) ; if this fails game will go on as a client



Global rec_bank=CreateBank(512) ; A common datablock. used to store data from client
Global send_bank=CreateBank(512) ; A common datablock. used to store data that is sent to clients.





;Setup Graphics mode & camera envornoment
Graphics3D 800,600
SetBuffer BackBuffer()
camera=CreateCamera()
CameraViewport camera,0,0,800,600
CameraRange camera,1,100
CameraFogRange camera,2,100
CameraFogMode camera,True
CameraFogColor camera,150,200,255
CameraClsColor camera,150,200,255
PositionEntity camera,0,10,-10
TurnEntity camera,30,0,0
light=CreateLight()


;Create a grass texture 64*64 with 1 flag and that is color.  one can also use loadtexture
grass=CreateTexture(64,64,1)
SetBuffer TextureBuffer(grass)
ClsColor 22,100,22
Cls
For i=0 To 256
	Color 0+Rnd(10),50+Rnd(155),0+Rnd(10)
	Plot Rnd(64),Rnd(64)
Next
SetBuffer BackBuffer()
ScaleTexture grass,3,3  
Color 255,255,255
ClsColor 0,0,0


;Create the grass entity as a endless plane
plane=CreatePlane()
EntityTexture plane,grass


;Create player mesh
player=CreateCube()
PositionEntity player,5,1,5






;The big main. mixed server & client in same code.
While Not KeyHit(1)

	server_function()

	player_controlls()
	update_player_positions()

	UpdateWorld()
	RenderWorld()

	draw_status_texts()
	client_networking()
	
	Flip
	
Wend





;Draw varius status text on screen. and some help.
Function draw_status_texts()

	Text 0,0,"Blitz3d TCPIP Example"

	If server
		Text 0,10,"player_guid_counter="+player_guid_counter
	Else
		If link=0 Then Text 0,10,"Press F1 for connect to server"
		Text 0,30,"client_debug_network="+client_debug_network
	End If

	If link=0
		Text 0,20,"Not Connected"
	Else
		Text 0,20,"Connected"
		
	End If
	
End Function







; Here we place all server handling functions. like accept new streams and send recive data from clients.
Function server_function()
	;Only run this if its the server. 
	If server 

		;TCP/IP stream check for new one.
		server_check() ;
		
		;TCP/IP Handling of streams. like sending reciving.
		server_handle_player()

		;update client position of server object. this makes the server also move at clients no neet to send our self to server. ;)
		p.player_type = First player_type
		p\x#=EntityX#(player)
		p\y#=EntityY#(player)
		p\z#=EntityZ#(player)
		p\rot#=EntityYaw(player)

	End If
	
End Function




;Here we handle all player controlls. left/right forward/backward even commands for connect to server.
Function player_controlls()

	If KeyHit(59) And server=False Then connect_to_server() ; Connect to server F1

	If KeyDown(17) ; Mover forward  "w"
		MoveEntity player,0,0,0.05
	End If
	
	If KeyDown(31) ; Move backward "s"
		MoveEntity player,0,0,-0.05
	End If
	
	If KeyDown(30) ; Turn Left "a"
		TurnEntity player,0,1,0
	End If
	
	If KeyDown(32) ; Turn right "d"
		TurnEntity player,0,-1,0
	End If

End Function







;Updates the meshes from data structure. that been loaded from network last. this makes players move on each other screens. and rotates
Function update_player_positions()

	;Update player positions
	For p.player_type=Each player_type
		If p\mesh<>player
	
			PositionEntity p\mesh,p\x#,p\y#,p\z#
			RotateEntity p\mesh,EntityPitch(p\mesh),p\rot#,EntityRoll(p\mesh)
			
		End If
	Next
	
End Function







;Server needs to check for new players connection setup. it alls hapends here.
Function server_check()

	;This code counts the registred players
	i=0
	For p.player_type = Each player_type
		i=i+1
	Next
	
	;If there is no one register even the server is not register. then this code creates the first player with the Guid 0  The first one :)
	If i=0	
		p.player_type= New player_type
		p\x#=EntityX#(player)
		p\y#=EntityY#(player)
		p\z#=EntityZ#(player)
		p\player_guid=0
		p\mesh=player
		
	End If
	
	
	;Check for new streams connected. and if detect a new stream create a new player give it a guid. 
	stream=AcceptTCPStream(Server)
	If stream
		Cls
		Print "A new stream connected !"
		Delay 2000
		
		;Create a new player for stream
		p.player_type= New player_type
		p\link = stream
		p\mesh=CreateCube()
		
		player_guid_counter=player_guid_counter+1
		p\player_guid = player_guid_counter ; The stream has now a global indentifyer. now that is nice.
		
	End If


	;Security code
	If player_guid_counter>10000 Then RuntimeError ("Some fool is hacking our little game client or you been playing far to long.") ; Be sure to put in this kind of bugs. lamo lama


	

End Function





;Player networking handling. here player clients get there data from server and sending it curent position.
Function client_networking()

	;Only run this code if a datalink has been establish
	If link
		ms=MilliSecs()
	
		;Send client data to server. evert frame this is done ;)
		PokeInt send_bank,0,1000 ; CLient cord package.
		PokeFloat send_bank,4,EntityX#(player)
		PokeFloat send_bank,4+8,EntityY#(player)
		PokeFloat send_bank,4+16,EntityZ#(player)
		PokeFloat send_bank,4+24,EntityYaw#(player)
		WriteBytes send_bank,link,0,512 ; Write it all to stream.
		
		
		
		Repeat 
			again=False
			If ReadAvail(link)>=512 ; Ya we got somthing to read.
				again=True
				bytes=ReadBytes(rec_bank,link,0,512)
				If bytes<512 Then RuntimeError "The client stream broke sir.." ; Lamo was here
		
		
				Select PeekInt(rec_bank,0)
				Case 1001 ; Found a client cordinate package.
				
					guid=PeekInt(rec_bank,4) ; global id
					newplayer=True
					
					For p2.player_type = Each player_type
						If guid=p2\player_guid
							newplayer = False ; Found the player.. now store data
							
							p2\x#=PeekFloat#(rec_bank,8)
							p2\y#=PeekFloat#(rec_bank,16)
							p2\z#=PeekFloat#(rec_bank,24)
							p2\rot#=PeekFloat#(rec_bank,32)
								
						End If
					Next
				
					If newplayer=True
						p2.player_type = New player_type
						p2\mesh=CreateCube()
						EntityColor p2\mesh,Rnd(255),Rnd(255),Rnd(255)
						p2\player_guid=guid
						p2\x#=PeekFloat#(rec_bank,8)
						p2\y#=PeekFloat#(rec_bank,16)
						p2\z#=PeekFloat#(rec_bank,24)
						p2\rot#=PeekFloat#(rec_bank,32)
	
					
					End If
				
				End Select 
		
			End If
			
		Until ReadAvail(link)<512
		
		
		client_debug_network=MilliSecs()-ms

	End If ;End of whole network block 
	
End Function





;This functions takes care of all player network data recive and send.
Function server_handle_player()

	For p.player_type= Each player_type
	
		If p\player_guid<>0
		
			If Eof(p\link) Then RuntimeError "A client quitet.. server crash !  Best regards Lamo coder" ;Realy put in some client kill & cleanup code here ;D
		
			Repeat
			If ReadAvail(p\link)>=512 ; Yes only 512 is a magic thing. 
			
				bytes=ReadBytes(rec_bank,p\link,0,512)
				
				If bytes<>512 Then RuntimeError ("Awsome client dint send 512.. wat to to Regards Lamo coder"); Yes we shold kick and ban this hacker or poor internet connection for a user.
				
					Select PeekInt(rec_bank,0) ; Reads the first byte and we go from there..
	
					Case 1000 ; Defaul x,y,z, data player cordinate packet recived.
						
						
						;Read the data from packet into the database. X,Y,Z and Rotation  (yaw)
						p\x#=PeekFloat(rec_bank,4)
						p\y#=PeekFloat(rec_bank,4+8)
						p\z#=PeekFloat(rec_bank,4+16)
						p\rot#=PeekFloat(rec_bank,4+24)
						
						;Well we shold make a payback to client as thanks for player cords ;)
						For p2.player_type= Each player_type
						
							If p2.player_type<> p.player_type;Every one except it self.
								
								PokeInt rec_bank,0,1001 ; A client cordinate package. Note i call this one 1001.. a reply from server clients cords. its verry important
								PokeInt rec_bank,4,p2\player_guid
								PokeFloat rec_bank,8,p2\x#	;X position
								PokeFloat rec_bank,16,p2\y# ;Y position
								PokeFloat rec_bank,24,p2\z# ;Z Position
								PokeFloat rec_bank,32,p2\rot# ; Rotation
							
								WriteBytes rec_bank,p\link,0,512 ; Send the data block
							
							End If		
							
						Next ; End of all other clients cords to the client
									
					End Select
						
				End If
				
			Until ReadAvail(p\link)<512 ; we need to read all packets before continue to clear tcp buffer. 
			
		End If
	Next

End Function






;Simple try to connect to server.
Function connect_to_server()

	Cls
	Print "Trying to connect to server"
	Print "Open stream"
	
	link=OpenTCPStream("localhost",7001)
	Print link
	If Not link Then RuntimeError "Failed to connect to server."

	Print "Connection success"
	Flip
	
	Delay 2000

End Function
