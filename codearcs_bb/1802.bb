; ID: 1802
; Author: Ryudin
; Date: 2006-08-31 20:04:33
; Title: Chat
; Description: A chatroom I created, but don't have anyone to chat with.

AppTitle "Login.","End chat?"
Graphics 800,600
Delay 15
Global font = LoadFont("Blitz",16)
SetFont font
SeedRnd MilliSecs() * 345452567478
.start
Cls
Print"<System Computer "+Rnd(-100,100)+" activated. Processing -_-_+*+* Activating Auto Start System>"
Delay 5000
Print "<CCSV<<<<<Center Code Source Variable Activation Sequence.>>>>>[Initialize] [Execute]>"
Print "<<<[Program Running]>>[Ask]> <System Messages Off<System<<<SMS>>>out.>>"
Delay 9000
Print "Press '1' to join a room or '2' to host one."
Repeat
	If KeyHit(2) join = True Exit
	If KeyHit(3) host = True Exit
Forever
FlushKeys()
If join = True Then
	nam$ = Input("Which chat room do you want to join? ")
	IP$ = Input$("IP Address of the server? ")
	JoinNetGame("Chat Server " + nam$ + " V. C. 1.0",IP$)
EndIf
If host = True Then
	Print "Press '1' for a dock, '2' for a base, of '3' for a planet."
	Repeat
		If KeyHit(2) dock = True Exit
		If KeyHit(3) base = True Exit
		If KeyHit(4) plan = True Exit
	Forever
	FlushKeys()
	If dock = True plc$ = "dock"
	If base = True plc$ = "base"
	If plan = True plc$ = "planet"
	If plc$ = "" plc$ = "dock"
	nam$ = Input$("What is the name of your new " + plc$ + "? ")
	If nam$ = "Spain" And plc$ = "planet" Goto sl1
	Select plc$
		Case "dock" plc$ = "Dock"
		Case "base" plc$ = "Base"
		Case "planet" plc$ = "Planet"
		Default plc$ = "Dock"
	End Select
	;There are really (in this chat program) three types of what I classify as servers. First, the
	;docks. They are the most important and are for anybody who wants to come in. Secondly, the bases,
	;which are just private stations where you create a very random name. Third, the planet. This is
	;the major gathering point for many to just sit and talk all day. Use all three as you play.
	HostNetGame("Chat Server " + nam$ + dock + base + plan + " V. C. 1.0")
EndIf
.st1
FlushKeys()
Name$ = Input$("What is your username? ")
p = CreateNetPlayer(Name$)
;message types
;1 - login/out message
;2 - public message
;3 - private message
Cls
SendNetMsg(1,"<" + Name$ + " has just logged in.>",p,0)
Locate 0,0
srvr$ = "<[Server] Chat Server " + nam$ + " " + dock + base + plan + " " + plc$ + " V. C. 1.0 [Log]>"
Print "<" + Name$ + " has just logged in to " + srvr$ + ".>"
Print "<" + CurrentTime$() + ", " + CurrentDate$() + ".>"
Print "<Initializing System Message Stopper.<System<<<SMS>>>out.>>"
Print"<System interface closed. Docking with chat system "+Rnd(-100,100)+":_-+[}Frequency locked on.>"
Print "<System message: Frequency found and locked in. Chat enabled. Station out. (_+)+{][}[]>"
msg$ = ""
acc$="abcdefghijklmnopqrstuvwxyz 1234567890-=_+*/\][{}|`~';:.,?!@#$%^&*()ABCDEFGHIJKLMNOPQRSTUVWXYZ<>"
AppTitle srvr$ + " - Press 'Esc' to exit Or 'F1' to find a new room.","End chat?" 

lines = 5
FlushKeys()
DebugLog "<System<Message Server<[Send All]>Reaction>Message>"
DebugLog "<IF Name$ Inside(Guard)<Create NPC{Sergeant}<Msg1$>[Execute]>ENDIF [COMMANDLINECHANGE]>"
DebugLog "<[Extra{Rate 100}][Create SQUAD1{Bloody Moon Squad}]<System<<SMS>>Out>>"
DebugLog "<[SKIPLINE]|SMS means 'System Message Stopper'. It stops the AI from going on and on.|[SS]>"
DebugLog "<System<[SKIPLINE]|SS means 'Stop Skip' and stops the comment line.|[SS]>Ending>"
DebugLog "<System<[Exit{Messenger}]>Ending[Application{Messenger}]>"
Repeat
	g = GetKey()
	c$ = Chr$(g)
	If Instr(acc$,c$,1) <> 0 Then
		msg$ = msg$ + c$
	EndIf
	If KeyHit(14) Then 
		If Len(msg$) > 0 Then
			msg$ = Left$(msg$,Len(msg$) - 1)
		EndIf
	EndIf
	Text 2,600 - (FontHeight() + 5),msg$
	If KeyHit(28) Then
		SendNetMsg(2,"<" + Name$ + ">: " + msg$,p,0)
		Color 0,0,255
		Print "<" + Name$ + ">: " + msg$
		If msg$ = "Reporting for duty." Then 
			If Instr(Name$,"Guard",1) <> 0 Then
				lines = lines + 2
				msg$ = ""
				Delay 2000
				Print "<Sergeant>: Work with the Bloody Moon Squad, " + Name$ + "."
				Delay 3000
				Print "<Sergeant>: They need it."
			EndIf
		EndIf
		msg$ = ""
		Color 255,255,255
		lines = lines + 1
	EndIf
	If RecvNetMsg() Then
		tp = NetMsgType()
		Color 0,255,0
		If tp = 2 Print NetMsgData$()
		Color 255,255,255
	EndIf
	If KeyHit(59) newroomstarter = True Exit
	If RecvNetMsg() Then
		mess$ = NetMsgData$()
		messt = NetMsgType()
		If messt > 0 And messt < 100 Print mess$ lines = lines + 1
	EndIf 
	If lines > 44 Then
		Cls
		Flip
		Locate 0,0
		lines = 2
		Print "<AUTO:<Automated message: Flipped screen because of too many lines of chat.>"
		Print"<Initiatitng chat process -_-_-_-_ +*+*+*+* Chat enabled. <System<<SMS>>out.>>OFF;>"
	EndIf
Until KeyHit(1)
SendNetMsg(1,"<" + Name$ + " has just logged out.>",p,0)
Print "<" + Name$ + " has just logged out.>"
If newroomstarter = True Then
	newroomstarter = False 
	DeleteNetPlayer(Name$)
	StopNetGame()
	Goto start
EndIf
RuntimeError"<System Code<<[Delete] ``P."+Name$+"'' [End Game]>[Deactivate server{"+srvr$+"}]>Ending>"
.sl1
AppTitle "<System<SECRET LOCATION 1 [Execute]>Planet>"
Print "You have landed on planet Spain. There is a secret castle base here, called Tudor. There is"
Print "also one called Fevermain. Go to those, or just connect to the rest of the world via"
Print "satellite."
Print "1 - Base"
Print "2 - Connect Via Satellite"
Repeat
	If KeyHit(2) Goto slc
	If KeyHit(3) Then
		newroomstarter = False 
		DeleteNetPlayer(Name$)
		StopNetGame()
		Goto start
	EndIf
Forever
.slc
Print "1 - Tudor    2 - Fevermain"
Repeat
	If KeyHit(2) Then
		nam$ = "Tudor, Spain"
		plc$ = "Secret"
		base = 1 
		HostNetGame("Secret Base 1 - " + nam$ + " " + plc$ + dock + base + plan + "V. C. 1.0")
		Goto st1
	EndIf
	If KeyHit(3) Then
		nam$ = "Fevermain, Spain"
		plc$ = "Secret"
		base = 1 
		HostNetGame("Secret Base 2 - " + nam$ + " " + plc$ + dock + base + plan + "V. C. 1.0")
		Goto st1
	EndIf
Forever
