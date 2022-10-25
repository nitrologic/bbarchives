; ID: 2789
; Author: Blitzplotter
; Date: 2010-12-02 12:15:03
; Title: Snakes Client and Server
; Description: Run up to ten snake clients, will work on a LAN or even further if your more adventurous. Not very optimised - apologies for the indentation but I'm moving on from this now.

[codebox]; WORMCLIENT.bb - you can run up to 10 clients - dependant 
; on CPU grunt.......Although even on my laptop I could not
; get my CPU above 57 percent
; a little SNAKES client by Blitzplotter, note you will need 
; to compile the associated code WORMSERVER, make
; the executable WORMSERVER & the client will automatically
; try and start a server if one is not running. If you close the
; client(s) before closing the WORMSERVER, the WORMSERVER will
; mop up the 123456789 files that appear whilst the clients & 
; server are running. 


Global print_once=0

Global title_once=0

max_bank = 512
sent_count=0

Dim srv_player_data(512);  place to store what the server reckons state of play is

test_x_move=5;   amount of packets to have sent before start adjusting x co-ord of this player
test_y_move=5;    amount of packets to have sent before start adjusting y co-ord of this player
x_move=0
y_move=0

Global box_size=500

Global player_score=0
Global player_mult_score=0

Global worm_walled=0
Global worm_hit_player=0

Global x_check_move=0
Global y_check_move=0

maxplayers=35
params=3     	; 0=playernum
XX#=1			; 1=x
YY#=2			; 2=y; 
ALIVE#=3		; 3=alive set to (1) or (0) for dead

Dim collArray(35,5)

Global this_player=99

Global this_player_num = 0
Global change_player_num_once=0


Dim proxArray(5)    ; 5 levels of proximity

	

;basically got eight directions to move the worm in. 2 ways to do this, send


Dim move_logic_x(8)
Dim move_logic_y(8)

Global move_count=1  ;note this goes up to 8 & cycles back to 1

move_logic_x(0)=0 : move_logic_y(0)=0  ;dead, therefore do not move

move_logic_x(1)=1 : move_logic_y(1)=0  ;right    NOTE 1=+1; 2=-1 on server
move_logic_x(2)=1 : move_logic_y(2)=1  ;up right
move_logic_x(3)=0 : move_logic_y(3)=1  ;up
move_logic_x(4)=2 : move_logic_y(4)=1  ;up Left
move_logic_x(5)=2 : move_logic_y(5)=0  ;left
move_logic_x(6)=2 : move_logic_y(6)=2  ;down left
move_logic_x(7)=0 : move_logic_y(7)=2  ;down
move_logic_x(8)=1 : move_logic_y(8)=2  ;down right


tcp_sendbank=CreateBank(max_bank)
tcp_recbank=CreateBank(max_bank)


;use this string to run numerous clients on ONE laptop
Global USE_LOOPBACK$="127.0.0.1"

;If you want to access a server on your network, open a 
;command prompt and enter ipconfig on the PC/laptop you
;intend to run the server on:-

Global USE_NETWORK$="192.168.1.4" ; modify this IP for a remote
                                  ; computer on your LAN you wish
								  ; to attach to the Server

link_string$=USE_LOOPBACK$     ;will detect the WORMSERVER if running on the client computer
;link_string$=USE_NETWORK$


;Graphics3D 700,500,16,2            ; B3D
Graphics 800,600,16,2               ; 




;populate the collision Array params:
For count=1 To 30
	collArray(count,XX)=5
	collArray(count,YY)=5
	collArray(count,ALIVE)=1
Next

;link=OpenTCPStream("127.0.0.1",7056)			;NOTE:  might need to replace with your IP info
;link=OpenTCPStream("192.168.0.2",7056)         ;

link=OpenTCPStream(link_string,7056)

If Not link
	Print "failed to create link - No server detected - looking.....4" : ExecFile("WORMSERVER.exe"): Delay 1500
	
	link=OpenTCPStream(link_string,7056)
	
	If Not link
		
		wait$=Input ("Waited 15 seconds to detect Server - want to wait longer? (Y or N):")
		If wait$="y" Or wait$="Y"
			Print "okay, hanging on for a 30 second now.... last chance": Delay 30000
			link=OpenTCPStream(link_string,7056)
			
			If Not link
				Print "Ending...": Delay 1500: 
				End
			Else
				Print "                                         Server located, finally."
				;perimeter
				
				
				
				Line box_size,0,box_size,box_size
				Line 0,box_size,box_size,box_size
			EndIf
		Else
			Print "Terminating Client.": Delay 1500: End
				
			
		End If
	Else
		Print"                                         Server started by moi.": Delay 1500: Cls
	EndIf
	
Else
	Text 520,12,("Server detected")
EndIf

Text 12,580,(" Connected.. This player is Alive in the world....")

status=12; we emulate a logged in client

Text 520,30,("left & right cursors to turn")

; Draw perimeter

Line box_size,0,box_size,box_size
Line 0,box_size,box_size,box_size

While Not KeyHit(1)

	;If Eof(link) Then Print "Server has gone, 5 secs until this is gone too.... " : Delay 5000 : End 
	If Eof(link)
		Print "Server has gone, you had a score of:"+((player_mult_score*255)+player_score)
		
		wait$=Input ("Want to see if Server will restart? (Y or N):")
		If wait$="y" Or wait$="Y"
			Print "okay, hanging on for a 8 second wait before re-try.... last chance": Delay 8000
			link=OpenTCPStream(link_string,7056)
			
			If Not link
				Print "Ending...": Delay 1500: 
				End
			Else
				Print "Server located."
				status=14; attempt at pushing x & y to server
			EndIf
			Delay 5000
			Cls
			
			;perimeter
			Line 512,0,512,512
			Line 0,512,512,512
			
		Else
			Print "Terminating Client. This was player"+this_player_num: Delay 1500: End
			
			
		End If
		
	EndIf
		
		
	

	Select status
	
		Case 10
			
			If player_score>=255
				player_score=1
				player_mult_score=player_mult_score+1
			EndIf
			
	
		If ReadAvail(link)>=max_bank
		
			;time to read some ;)
			r=ReadBytes(tcp_recbank,link,0,max_bank)
			If pon=1
			Print "have readin "+r+" bytes from server."
			Print "-----------------------------------------------------"
		EndIf
		
			;my attempt at accessing sent count
			xth=PeekByte(tcp_recbank,0)        ;the amount of clients server has detected all
			                                   ;together: Note if a client is registered
			                                   ;by the server - the logic will dictate that
			                                   ;once that client dies it stays dead until
											   ; a new 'game' is ininitiated & the server
											   ;opens up all players for joining again. The
												;problem is that the server decrements the 
												; client count at the moment when a player 
												; stroke client is detected as 'left'.
				serv_count=PeekByte(tcp_recbank,1)
				
				
		    ;attempt at copying in data received from server now
		    For copy_ct=1 To 511
		    srv_player_data(copy_ct)=PeekByte(tcp_recbank,copy_ct)
		    Next
			
			;current players
			mem_loc=((20*1)-10)
			
			For loop = 1 To xth  ;cycling through the number of clients server states
								;is connected:  
				
				;Note - the xth is simply a loop index of how many clients are currently attached
	            ;work out where in 512 bytes this players x and y co-ords are:		
			    mem_loc=((20*loop)-10)
				If pon=1
					
			    Print"mem_loc is:"+mem_loc
			    
				Print "player: "+loop+" x co-ord:"+srv_player_data(mem_loc+1)
				Print "player: "+loop+" y co-ord:"+srv_player_data(mem_loc+2)
				
			EndIf
			
			If skiponce<3              ;needs to pass 3 times before it settles down
				skiponce=skiponce+1
			Else
				Text 20,550,"This is Player :"+this_player_num
			EndIf
			
			
			Flip
			
			Select loop
					
				Case 1
					Plot srv_player_data(mem_loc+1)*2,srv_player_data(mem_loc+2)*2
					
					
					;Text 610,(112)+loop*11,"player:"+loop+" score:"+player_score
					
					;Text 610,(132)+loop*11,"player:"+loop+" score:"+test_score
					
					
					collArray(1,XX)=srv_player_data(mem_loc+1)*2
					collArray(1,YY)=srv_player_data(mem_loc+2)*2
		Case 2
			Plot srv_player_data(mem_loc+1)*2,srv_player_data(mem_loc+2)*2
			;is alive";+srv_player_data(mem_loc+1);+" Y:"+srv_player_data(mem_loc+2)
			
			collArray(2,XX)=srv_player_data(mem_loc+1)*2
			collArray(2,YY)=srv_player_data(mem_loc+2)*2
			
			
			
		Case 3
			Plot srv_player_data(mem_loc+1)*2,srv_player_data(mem_loc+2)*2
			;" is alive";+srv_player_data(mem_loc+1);+" Y:"+srv_player_data(mem_loc+2)
			
			collArray(3,XX)=srv_player_data(mem_loc+1)*2
			collArray(3,YY)=srv_player_data(mem_loc+2)*2
			
		Case 4
			Plot srv_player_data(mem_loc+1)*2,srv_player_data(mem_loc+2)*2
			;" is alive";+srv_player_data(mem_loc+1);+" Y:"+srv_player_data(mem_loc+2)
			
			
			collArray(4,XX)=srv_player_data(mem_loc+1)*2
			collArray(4,YY)=srv_player_data(mem_loc+2)*2
			
		Case 5
			Plot srv_player_data(mem_loc+1)*2,srv_player_data(mem_loc+2)*2
			;" is alive";+srv_player_data(mem_loc+1);+" Y:"+srv_player_data(mem_loc+2)
			
			collArray(5,XX)=srv_player_data(mem_loc+1)*2
			collArray(5,YY)=srv_player_data(mem_loc+2)*2
			
		Case 6
			Plot srv_player_data(mem_loc+1)*2,srv_player_data(mem_loc+2)*2
			;" is alive";+srv_player_data(mem_loc+1);+" Y:"+srv_player_data(mem_loc+2)
			
			
			collArray(6,XX)=srv_player_data(mem_loc+1)*2
			collArray(6,YY)=srv_player_data(mem_loc+2)*2
			
		Case 7
			Plot srv_player_data(mem_loc+1)*2,srv_player_data(mem_loc+2)*2
			;" is alive";+srv_player_data(mem_loc+1);+" Y:"+srv_player_data(mem_loc+2)
			
			
			collArray(7,XX)=srv_player_data(mem_loc+1)*2
			collArray(7,YY)=srv_player_data(mem_loc+2)*2
			
		Case 8
			Plot srv_player_data(mem_loc+1)*2,srv_player_data(mem_loc+2)*2
			;" is alive";+srv_player_data(mem_loc+1);+" Y:"+srv_player_data(mem_loc+2)
			
			
			collArray(8,XX)=srv_player_data(mem_loc+1)*2
			collArray(8,YY)=srv_player_data(mem_loc+2)*2
			
		Case 9
			Plot srv_player_data(mem_loc+1)*2,srv_player_data(mem_loc+2)*2
			;" is alive";+srv_player_data(mem_loc+1);+" Y:"+srv_player_data(mem_loc+2)
			
			
			collArray(9,XX)=srv_player_data(mem_loc+1)*2
			collArray(9,YY)=srv_player_data(mem_loc+2)*2
			
		Case 10
			Plot srv_player_data(mem_loc+1)*2,srv_player_data(mem_loc+2)*2
			;" is alive";+srv_player_data(mem_loc+1);+" Y:"+srv_player_data(mem_loc+2)
			
			
			collArray(10,XX)=srv_player_data(mem_loc+1)*2
			collArray(10,YY)=srv_player_data(mem_loc+2)*2
			
			
	End Select
	
Next	
			
			If change_player_num_once=0
				change_player_num_once=1
				this_player_num=xth
				
				;this should ensure that this player num is only set once
				
			EndIf
			
			
			status=12 ; Yeppp time to respond... 
		End If
			
	
	Case 12
	
		;THIS IS THE SENDER
	
	    ;          player count:  1 byte	
		;          player: x, y co -ords, 2 bytes
		;          shot anything:  yes/no 1 byte
		;          projectile alive: 1 byte
		;          projectile x,y: 2 bytes
		;          player world count:  1 byte
		;          leaving 2 bytes for whatever       
	
	
	    ;byte 1: 1, add 1 to x,2 subtract 1 from x
	    ;byte 2: 1, add 1 to y,2 subtract 1 from y
		
		;byte seven: chaning to 12 from the 13 the server sets it to
	
		;haha client time to spam server first we make a cool tcp packet with random nunbers.
		For i=0 To (max_bank-1)
			PokeByte tcp_sendbank,i,0
			If sentcount<255
			PokeByte tcp_sendbank,0,sent_count
			Else sentcount=255
			EndIf
		Next
		
		PokeByte tcp_sendbank,3,this_player_num; poke the player_num byte the changed once number
		
		
	;encapsulate the move stuff within if alive logic
		
		If worm_walled=0
		
		
		If x_move>test_x_move Then
		    ;Print"set x________ to move"
			PokeByte tcp_sendbank,1,move_logic_x(move_count);1 here means server needs To move x For this
			                                ;player/client by +1
			
			;x_check_move=2*(move_logic_x(move_count)) this is only 1 to 8 doh!
			
		;Note the client is Not self aware therefore how to instigate direction changes 
		;as a result of input.....
			
			;easy - need to detect left cursor & right cursor first - this will be the 
			;controlling influence for the worm.....

		End If
		If y_move>test_y_move Then
		    ;Print"set y________ to move"
			PokeByte tcp_sendbank,2,move_logic_y(move_count)       ;1 here means server needs to move y for this
			                                ;player/client by +1
			
			;y_check_move=2*(move_logic_y(move_count))
			
		End If
		
	Else
		
		PokeByte tcp_sendbank,1,move_logic_x(0);1 here means server needs To move x For this by nothing
		PokeByte tcp_sendbank,2,move_logic_y(0);1 here means server needs to move y for this by nothing
		
		
	EndIf; endif for worm_walled code - basically this worm WILL MOVE NO MORE!!!!!!
	
	this_dead_mem_loc=((20*(loop-1)-10))
	
	
	
	
	
	
	
	If collArray(this_player_num,XX)>498 Or collArray(this_player_num,YY)>498 Then
				
			worm_walled=1
			
			PokeByte tcp_sendbank,(this_player_num*20)-13,12    ;13 here means this client is unlucky and dead!!!!
			; NOTE: if going to 
			
			;Text 250,250,"WORM SPLATTERED INTO A WALL..xmove: "+x_move+" &y:"+y_move
			
			this_dead_mem_loc=((20*(loop-1)-10))
			
			Text 40,420,"player: "+this_player_num+" dead at x co-ord:"+collArray(this_player_num,XX)
			Text 40,440,"player: "+this_player_num+" dead at y co-ord:"+collArray(this_player_num,YY)
			Text 40,460,"player: "+this_player_num+" Score:"++((player_mult_score*255)+player_score)
			
			PokeByte tcp_sendbank,1,move_logic_x(0);1 here means server needs To move x For this by nothing
			PokeByte tcp_sendbank,2,move_logic_y(0);1 here means server needs to move y for this by nothing
			
		EndIf
		
		If worm_walled=0
			
			PokeByte tcp_sendbank,7,12       ;12 here means this client is alive
			
											;this'll hopefully be changing the server from
											;13 to 12........................
			                                ;player/client by +1
			
			Text 500,380,("player not hit wall")
			
			
			;player alive so increment score
			player_score=player_score+1  
			
		Else
		;need to send the number here to server to inform player walled (& hit other player)
			Text 500,400,("player hit wall")
			
			PokeByte tcp_sendbank,7,13 
			
		EndIf
		
		
		PokeByte tcp_sendbank,5,player_score  
		PokeByte tcp_sendbank,6,player_mult_score 
		
		s=WriteBytes (tcp_sendbank,link,0,max_bank)
		;Print "Sending "+s+" bytes to server."+sent_count+" times"
		sent_count=sent_count+1
		
		status=10  ; Well we go out and wait for packet		
		
	Case 14
		
		;Client to contact server first we make a cool tcp packet
		For i=0 To (max_bank-1)
			PokeByte tcp_sendbank,i,0
			If sentcount<255
				PokeByte tcp_sendbank,0,sent_count
				Else sentcount=255
			EndIf
		Next
		
		If x_move>test_x_move Then
			PokeByte tcp_sendbank,1,srv_player_data(mem_loc+1)		;1 here means server needs to move x for this
			                                ;player/client by +1
			
		End If
		If y_move>test_y_move Then
			PokeByte tcp_sendbank,2,srv_player_data(mem_loc+2)      ;1 here means server needs to move y for this
			                                ;player/client by +1
			
		End If
		
		PokeByte tcp_sendbank,1,srv_player_data(mem_loc+1)
		PokeByte tcp_sendbank,2,srv_player_data(mem_loc+2)
		
		
		s=WriteBytes (tcp_sendbank,link,0,max_bank)
		sent_count=sent_count+1
		
		status=10
		
	Case 16  
		
		If ReadAvail(link)>=max_bank
			
			;time to read some ;)
			r=ReadBytes(tcp_recbank,link,0,max_bank)
			If pon=1
				
			Print "have readin "+r+" bytes from server."
			Print "-----------------------------------------------------"
		EndIf
		
			;accessing sent count
			xth=PeekByte(tcp_recbank,0)
			serv_count=PeekByte(tcp_recbank,1)
			
		    ;copying in data received from server now
		    For copy_ct=1 To 511
				srv_player_data(copy_ct)=PeekByte(tcp_recbank,copy_ct)
		    Next
			
			;current players
			mem_loc=((20*1)-10)
			For loop = 1 To xth
	            ;work out where in 512 bytes this players x and y co-ords are:		
			    mem_loc=((20*loop)-10)
				If pon=1
					
			    Print"mem_loc is:"+mem_loc
			    
				Print "player: "+loop+" x co-ord:"+srv_player_data(mem_loc+1)
				Print "player: "+loop+" y co-ord:"+srv_player_data(mem_loc+2)
			EndIf
			
				
			Next	
			
			status=12 ; Yeppp time to respond... 
		End If
		
		
		
	
End Select

	
	play_ct=0

    ;update x and y co-ords for this client to send to server:-	
	
	x_move = x_move+1
	y_move = y_move+1

	;Delay 10 ; yep we have to delay some here to emulate many clients.. or cpu dies.
	
	Delay 50
	
	VWait 1; let the CPU rest
	
	
	If KeyDown(203)
		;Text 400,400,("left detected")
		If move_count>1
			move_count=move_count-1
		Else
			move_count=8
		EndIf
	;Else
		;Text 400,400,("             nowt detected")
	EndIf

	If KeyDown(205)
		;Text 400,400,("right detected")
		If move_count<8
			move_count=move_count+1
		Else
			move_count=1
		EndIf
	EndIf

	If title_once<=6
		
		AppTitle "Worm Client Number "+this_player_num
		title_once=title_once+1
		
		Text 20,500,"vvvvvv Proximity meter: 10 to 50 units respectively vvvvvv"
		
	EndIf
	
	;call the proximity detector function
	If xth>1
		proximity(this_player_num,xth)
	EndIf
	
	
	
Wend


Function proximity(thisplayer,maxplayers)
	
	
	For checkOthers=1 To maxplayers
		
		Local XX=1
		Local YY=2
		
		If checkOthers=thisplayer
			;do nothing
		Else
			If collArray(checkOthers,XX)>collArray(thisplayer,XX)
				xdiff=collArray(checkOthers,XX)-collArray(thisplayer,XX)
			Else
				xdiff=collArray(thisplayer,XX)-collArray(checkOthers,XX)
			EndIf
			
			If collArray(checkOthers,YY)>collArray(thisplayer,YY)
				ydiff=collArray(checkOthers,YY)-collArray(thisplayer,YY)
			Else
				ydiff=collArray(thisplayer,YY)-collArray(checkOthers,YY)
			EndIf
			
			
			For closeness=50 To 10 Step -10
				
				If xdiff<closeness
					If xdiff>(closeness-10)
						proxArray(closeness/10)=proxArray(closeness/10)+1
					EndIf
				EndIf
				
			Next
			
		EndIf
		
	Next
	
	;draw the five proximity lines:
	For drawProx=1 To 5
		Line 1,510+(drawProx*6),(proxArray(drawProx)),510+(drawProx*6)
	Next
	
End Function

;END CLIENT CODE
;=========================================================================	
[\codebox]

[codebox]


;WORMSERVER.bb - create an executable called WORMSERVER.exe
; in the same directory as the WORMCLIENT.exe

;removed need for a separate folder to hide files in

;VARS------------------------------------------------------------

Dim player_data(512)  ;512 bytes to copy into each clients

;Populate dummy player data
For count = 10 To 490 Step 20
	player_data(count+1)=player_number      ;x    (-9)      
	player_data(count+2)=count				;y    (-8)
	player_data(count+3)=count				;THIS IS JUST A COUNT OF CLIENTS THAT ARE RUNNING    
	player_data(count+4)=0					;(-7) 0 bullet dead, 1 alive  - now player num 
											;(-6)player score - not doing bullets for a while anyway.make this score count 1(1-255)
	player_data(count+5)=count+10			;(-5)2nd part score make this score count 2(255*255)   i.e. 10+(2*255)
	player_data(count+6)=count+10			;(-4)

player_data(count+7)=13			        	;alive signified by 13

Next



max_bank=512
global_client_count=0
Dim file_name$(255); 255 possible clients - who'd need more?
Global file_count=0


Type session

	Field link
	Field tcp_sendbank
	Field tcp_recbank
	Field status
	Field player_number

End Type


Graphics 400,600,16,2            ; B3D

AppTitle "Serving worms with killer logic"

server=CreateTCPServer(7056)
;print "server is running.."

msmin=9999999 ; minimums millisecs :D

While Not KeyHit(1)   

	stream = AcceptTCPStream(server)
	If stream
		global_client_count=global_client_count+1
		;print "New stream detected. memory allocated."
		ses.session = New session
		ses\link=stream
		ses\tcp_sendbank = CreateBank(max_bank)	
		ses\tcp_recbank = CreateBank(max_bank)
		ses\status =10 ; Simulate login.
		;ses\client_count=global_client_count
		
		ses\player_number=global_client_count  
		Print "global_client_count is:"+ses\player_number
		
		fn_create_file(Int ses\link)
	
	End If


	oldms=MilliSecs()
	;Validate streams.. 
	For ses.session = Each session
	
		If Eof(ses\link) 
			;print "A session called "+ses\link+" has stop working."
			;print "Frees memory banks for session "+ses\link
			FreeBank ses\tcp_sendbank 
			FreeBank ses\tcp_recbank
			;print "deletes sessions "+ses\link
			
			numberin=ses\link
			fn_delete_file(Int ses\link)
			
			Delete ses
			;global_client_count=global_client_count-1 ;removing This could have BIG ramifications:-
			
			;instead of doing the aboveset the dead byte in the client:-
			
			
		End If
		
	Next
	
	For ses.session = Each session; this is looking at the first few bytes sent in from each session/client
	
	
		Select ses\status
		Case 10
		
			If ReadAvail(ses\link)>=max_bank
			
				;so we got a new packet :D and its all 100% secure... so we can go on live our lifes
				r=ReadBytes (ses\tcp_recbank,ses\link,0,max_bank)
				;print "for session "+ses\link+" who has a tcp recbank of:"+ses\tcp_recbank
				;print "read "+r+" bytes from stream"
				
				;my attempt at accessing sent count
				rx_count=PeekByte(ses\tcp_recbank,0)

				If PeekByte(ses\tcp_recbank,3)>0
					client_has_player_num=PeekByte(ses\tcp_recbank,3)
					
					
					;print"***********************************************************"
					Print"   Found player number in data:  "+client_has_player_num+"......Press any key"
					
					;so try setting the player_number for sending back to 41
					ses\player_number=client_has_player_num  ;IS NOW WORKING
					

				Else
					
					Print "Setting playernum to :"+global_client_count+" Press any key"
					;WaitKey
					client_has_player_num = global_client_count
					playerNumLoc=(20*ses\player_number)-7				;
					player_data(playerNumLoc)=client_has_player_num

					
				EndIf
				
				
				
				
				; end add player_number to a player if he already does not have one
				;############################################################################

				
				;##########################################
				; player alive or dead status
				
				If PeekByte(ses\tcp_recbank,7)=12
					alive_data=PeekByte(ses\tcp_recbank,2)
					
					
					;print"***********************************************************"
					Print"   Found alive data (set to 12):  "+alive_data
					;print"***********************************************************"
					;----------------------------------------------------------------------
					;okay moving the logic to move the worm here coz its alive:-
					
					
						;############################################################################
				; now access the data wrt the player/client 
					
									;###################################
				;player score discovery
					
					If PeekByte(ses\tcp_recbank,5)>0
						client_has_score=PeekByte(ses\tcp_recbank,5)
						client_big_score=PeekByte(ses\tcp_recbank,6)
						
					;print"***********************************************************"
						Print client_has_player_num+"   Found player number score in data:  "+client_has_score
						Print client_has_player_num+"   Found player multiplier score in data:  "+client_big_score
						
					;print"***********************************************************"
						
					Else
						client_has_score = 69
						playerNumLoc=(20*ses\player_number)-6
						player_data(playerNumLoc)=client_has_score
						
					;print"***********************************************************"
						Print"   Assigned a player score to a new client"
					;print"***********************************************************"
						
					;2nd bit of score
						playerNumLoc=(20*ses\player_number)-5
						player_data(playerNumLoc)=client_has_score
						
						
					EndIf
					
			
					
					If PeekByte(ses\tcp_recbank,1)>0 
						xmove=PeekByte(ses\tcp_recbank,1)
						
					;print"XMOVE detected as :"+xmove
						
						If xmove=1;add one
							xloc=(20*ses\player_number)-9
							If player_data(xloc)<=250 
								player_data(xloc)=player_data(xloc)+1
						;print"adding ++++++++++++++++++++++++X"
						;print"adding ++++++++++++++++++++++++X"
								
							EndIf
						EndIf
						If xmove=2
						;subtract one  
							xloc=(20*ses\player_number)-9
							If player_data(xloc)>=1 

								player_data(xloc)=player_data(xloc)-1

							EndIf
						EndIf
						
						If xmove>2   
							xloc=(20*ses\player_number)-9
		
							player_data(xloc)=xmove
						EndIf
			
					EndIf
					
					
					If PeekByte(ses\tcp_recbank,2)>0
						ymove=PeekByte(ses\tcp_recbank,2)
						
						
					;;print "Detected Move request for y of client:"+ses\tcp_recbank
						yloc=(20*ses\player_number)-8
						ymove=PeekByte(ses\tcp_recbank,2)

						If ymove=1;add one
							yloc=(20*ses\player_number)-8
							If player_data(yloc)<=250 
								player_data(yloc)=player_data(yloc)+1
							EndIf
						EndIf
						If ymove=2;subtract one  
							yloc=(20*ses\player_number)-8
							If player_data(yloc)>=1 
								player_data(yloc)=player_data(yloc)-1
							EndIf
						EndIf
						
						
						If ymove>2;refresh from client
							
							yloc=(20*ses\player_number)-8
				   		;If player_data(yloc)>=1 
							player_data(yloc)=ymove
						;EndIf
						EndIf
	
					EndIf

					;---------------------------------------------------------------------
				Else

					client_has_player_num = global_client_count
					playerNumLoc=(20*ses\player_number)-3   ;might be right?
					player_data(playerNumLoc)=client_has_player_num
					
				EndIf
				
				If PeekByte(ses\tcp_recbank,7)=13
					
					Cls
					
					;Text 20,100,
					Print "Wall hit for player:"+player_data(playerNumLoc)
					
					;Text 20,120,
					Print "last xmove was:"+xmove+"last ymove was:"+ymove
					
				EndIf

			
				ses\status=12 ; well time to reply this packet next round :D
			
			EndIf
		
		Case 12 ; Well time to reply a packet.
		
			;First we fill it upp with 0
			For i=0 To (max_bank-1)
				;PokeByte ses\tcp_sendbank,i,Rnd(255)
				PokeByte ses\tcp_sendbank,i,0
			Next
			
			

			rx_count=PeekByte(ses\tcp_recbank,0)
			
			;client_count=PeekByte(ses\tcp_recbank,0)
			PokeByte ses\tcp_sendbank,0,global_client_count   
			PokeByte ses\tcp_sendbank,1,rx_count
	
			For count = 10 To 490
				PokeByte ses\tcp_sendbank,count,player_data(count); copying in data
			Next
			
			
			; SETTING THE PLAYER NUMBER TO WHAT THE CLIENT RECKONS
			Print"==================================="
			Print "Client Number set to: "+ses\player_number
			PokeByte ses\tcp_sendbank,3,ses\player_number 


			;Secondly we reply it kindly.
			s=WriteBytes (ses\tcp_sendbank,ses\link,0,max_bank)
			;;print "Succesfull sending "+s+" bytes of data"
			 packetssent= packetssent+1
	
			ses\status=10 ; Well we cant wait for next coll packet to arive :D"
			
		End Select
	
	
	Next	
	
	ms=MilliSecs()-oldms
	If ms>msmax Then msmax=ms
	If ms<msmin Then msmin=ms
	count=count+1
	cmsmed=cmsmed+ms
	msmed=cmsmed/count   ; medium millisecs
	
	
	tick=tick+1
	If tick>=500 Then Print "Ticks: "+count+" MS="+ms+" MIN="+msmin+" MED="+msmed+" MAX="+msmax+ "Packets sent "+ packetssent : tick=0

	
		If KeyHit(19) Then MSMIN=99999 : msmed=0 : count=0 : msmax=0 : packetssent=0 :cmsmed=0: ;print "Counter resets."

		Delay 8 ; this gives cpu time to rest of OS in windows. as server is not so cpu intensive.

Wend
;---------------------------------------------------------------------
;  functions
;---------------------------------------------------------------------



Function fn_delete_file(numberin)
	;print "Deleting File: "+numberin
	;del_file_name$="file_dir/"+numberin+".ini"
	del_file_name$="./"+numberin+".ini"

	DeleteFile (del_file_name$)
	;print "File Deleted"	
End Function


Function fn_create_file(numberin)

	BestName$="Mark"

	file_name$(file_count)="./"+numberin+".ini"

	;print file_name$(file_count)
	writethis$=file_name$(file_count)
	file_count=file_count+1
	
	; Open a file to write to 
	fileout = WriteFile(writethis$)

	
	WriteString( fileout, "new string:1" ) 

	; Close the file 
	CloseFile( fileout ) 
	
End Function


;END WORMSERVER.bb
;=================================================================

[/codebox]
