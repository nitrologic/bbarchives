; ID: 2106
; Author: Arem
; Date: 2007-09-16 22:07:16
; Title: Music Sync
; Description: Syncs Music Over Network

;SERVER

AppTitle("Music Sync Server")

Type client
	Field connection,timer
End Type

server=CreateTCPServer(550)

If Not server
	Notify "Couldn't Initialize Server!",1
	End
Else
	window=CreateWindow("Music Sync Server",ClientWidth(Desktop())/2-200,ClientHeight(Desktop())/2-100,400,200,Desktop(),3)
	open=CreateButton("New Music",50,50,100,20,window)
	label=CreateLabel("Status: ",50,100,300,100,window)
	SetGadgetText(label,"Status: server is running.")
End If

pingtimer=CreateTimer(1)

While Not endvariable=1
	WaitEvent(0)
	
	connect=AcceptTCPStream(server)
	
	If connect
		c.client=New client
		c\timer=CreateTimer(1)
		c\connection=connect
		For a=1 To 3
			WriteLine(c\connection,"sync")
			
			While Not ReadAvail(c\connection)
			Wend
			
			WriteLine(c\connection,"pong")
		Next
		
		Delay(250)
		WriteLine(c\connection,MilliSecs())
	End If
	
	If EventID()=$803
		endvariable=1
	End If
	
	If EventID()=$401
		If EventSource()=open
			thesound$=RequestFile("Open a song","mp3")
			
			starttime=MilliSecs()+15000
			
			If thesound$<>""
				SetGadgetText(label,"Status: Playing "+readtoslash$(thesound$))
			
				For c.client=Each client
					WriteLine(c\connection,"play"+readtoslash$(thesound$))
					WriteLine(c\connection,"start"+starttime)
				Next
			End If
		End If
	End If
	
	If TimerTicks(pingtimer)>5
		For c.client=Each client
			WriteLine(c\connection,"ping")
		Next
		
		ResetTimer(pingtimer)
	End If
	
	For c.client=Each client
		If ReadAvail(c\connection)
			If ReadLine(c\connection)="ping"
				ResetTimer(c\timer)
			End If
		End If
	
		If TimerTicks(c\timer)>20
			CloseTCPStream(c\connection)
			Delete c
		End If
	Next
	
	Delay(1)
Wend

End

Function readtoslash$(incoming$)
	temp$=""
	
	While Right$(incoming$,1)<>"\" And incoming$<>""
		temp$=Right$(incoming$,1)+temp$
		incoming$=Left$(incoming$,Len(incoming$)-1)
	Wend

	Return temp$
End Function















;CLIENT

AppTitle("Music Sync Client")

server$="dayne"
connection=OpenTCPStream(server$,550)

If connection
	Print("Succesfully connected to server.  Music will begin playing on next song.")
Else
	Notify("No server!")
	End
End If

servertimer=CreateTimer(1)
clienttimer=CreateTimer(1)

For a=1 To 3
	While Not ReadAvail(connection)
		Delay(1)
	Wend
	
	temp$=ReadLine(connection)
	
	oldtime=MilliSecs()
	WriteLine(connection,"ping")
	
	While Not ReadAvail(connection)
	Wend
	
	temp$=ReadLine(connection)
	
	lag=lag+(MilliSecs()-oldtime)
Next

While Not ReadAvail(connection)
Wend

offset=MilliSecs()-Int(ReadLine(connection))-lag/6

While Not endvariable=1
	If ReadAvail(connection)
		incoming$=ReadLine(connection)
		
		If Left$(incoming$,4)="play"
			incoming$=Right$(incoming$,Len(incoming$)-4)
			nextsong$=incoming$
			newsong=LoadSound("\\"+server$+"\music\"+incoming$)
		
			While Not ReadAvail(connection)
				Delay(1)
			Wend
			
			incoming$=ReadLine(connection)
		
			If Left$(incoming$,5)="start"
				incoming$=Right$(incoming$,Len(incoming$)-5)
				
				starttime=Int(incoming$)+offset
			End If
		End If
		
		If incoming$="ping"
			ResetTimer(servertimer)
		End If
	End If
	
	If starttime
		If Not MilliSecs()<starttime-500
			Print("Playing: "+nextsong$)
			While MilliSecs()<starttime-5
				Delay(1)
			Wend
			
			While MilliSecs()<starttime
			Wend
			
			StopChannel(channel)
			channel=PlaySound(newsong)
			starttime=0
		End If
	End If
	
	If TimerTicks(servertimer)>20
		Notify("Connection to server lost!",1)
		End
	End If
	
	If TimerTicks(clienttimer)>5
		WriteLine(connection,"ping")
		ResetTimer(clienttimer)
	End If
	
	Delay(1)
Wend

End
