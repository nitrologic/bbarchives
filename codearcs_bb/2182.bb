; ID: 2182
; Author: Wings
; Date: 2008-01-13 12:11:54
; Title: TCP engine ULTRA FAST
; Description: TCP engine 2 show prof that BLitz TCP can work flawless.

;CP engine ULTRA FAST
;
; Written by Daniel Eriksson  (wings@tiberion.eu)
;Public Domain free do use as wish including selling it.


Type session

	Field link
	Field tcp_sendbank
	Field tcp_recbank
	Field status

End Type

window=CreateWindow("Key press me window",100,100,640,200,0,1)

server=CreateTCPServer(7056)
Print "server is running.."

msmin=9999999 ; minimums millisecs :D

While Not KeyHit(1)   ;Hehe this command dosent work in BlitzPluss with the console window.

	stream = AcceptTCPStream(server)
	If stream
		
		Print "New stream detected. memmory allocated."
		ses.session = New session
		ses\link=stream
		ses\tcp_sendbank = CreateBank(512)	
		ses\tcp_recbank = CreateBank(512)
		ses\status =10 ; Simulate login.
	
	End If


	oldms=MilliSecs()
	;Validate streams.. 
	For ses.session = Each session
	
		If Eof(ses\link) 
			Print "A session caled "+ses\link+" has stop working."
			Print "Frees memmory banks for session "+ses\link
			FreeBank ses\tcp_sendbank 
			FreeBank ses\tcp_recbank
			Print "deletes sessions "+ses\link
			Delete ses
		End If
		
	Next
	
	
	For ses.session = Each session
	
	
		Select ses\status
		Case 10
		
			If ReadAvail(ses\link)>=512
			
				;so we got a new packet :D and its all 100% secure... so we can go on live our lifes
				r=ReadBytes (ses\tcp_recbank,ses\link,0,512)
				;Print "read "+r+" bytes from stream"
			
				ses\status=12 ; well time to reply this packet next round :D
			
			End If
		
		Case 12 ; Well time to reply a packet.
		
			;First we fill it upp with randomnezz
			For i=0 To 511
				PokeByte ses\tcp_sendbank,i,Rnd(255)
			Next
			
			;Secondly we reply it kindly.
			s=WriteBytes (ses\tcp_sendbank,ses\link,0,512)
			;Print "Succesfull sending "+s+" bytes of data"
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

	
	If KeyHit(19) Then MSMIN=99999 : msmed=0 : count=0 : msmax=0 : packetssent=0 :cmsmed=0: Print "Counter resets."

	Delay 10 ; this gives cpu time to rest of OS in windows. as server is not so cpu intensive.

Wend
;--------------------------------------------------------------------




;------------ Client part ------------------------------
; this is a dumb client.. it only spams server :D


tcp_sendbank=CreateBank(512)
tcp_recbank=CreateBank(512)

link=OpenTCPStream("www.tiberion.com",7056)

If Not link Then Print "failed to create link." : Delay 3000 : End

Print "Connected.. let the spam flood."

status=12; we emulate a loged in client

While Not KeyHit(1)

	If Eof(link) Then Print "What server died ??? why   WHYYY !!?? " : Delay 5000 : End 


	Select status
	
	Case 10
	
		If ReadAvail(link)>=512
		
			;time to read some ;)
			r=ReadBytes(tcp_recbank,link,0,512)
			;Print "have readin "+r+" bytes from server."
			status=12 ; Yeppp time to respond... 
		End If
			
	
	Case 12
	
		;haha client time to spam server first we make a cool tcp packet with random nunbers.
		For i=0 To 511
			PokeByte tcp_sendbank,i,Rnd(255)
		Next
		
		;well we got our important randomnes.. time ti send the junk spam to server.
		s=WriteBytes (tcp_sendbank,link,0,512)
		;Print "Sending "+s+" bytes to server."
		
		status=10  ; Well we go out and wait for packet :D
		
	
	End Select
	
		tick=tick+1
	If tick>=500 Then Print CurrentTime()+"...tick..." : tick=0



	Delay 10 ; yep we have to delay some here to emulate many clients.. or cpu dies.

Wend
