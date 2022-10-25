; ID: 1821
; Author: Subirenihil
; Date: 2006-09-21 10:13:08
; Title: TCP/IP Network
; Description: Tired of always typing in IP addresses? try this!

Type host
	Field ip$,gn$,cp%,mp%,tim%
End Type

Type player
	Field id%,name$
End Type

Type chat
	Field msg$,pos%
End Type

Graphics 640,480,0,2
SetBuffer FrontBuffer()
Cls
Locate 0,0
name$=Input$("What is your name? ")
SetBuffer BackBuffer()

KNL_Initialize("{24538F2F-748F-4779-8E20-36D948406E34}",8,134)

KNL_EnumerateHosts(8080)

k$=""
xit$=0
Repeat
	While KNL_CheckMessages()=-1
		id=KNL_GetMessageID()
		If id=31
			hip$=KNL_FieldGetString$("HostIP")
			newh=True
			For h.host=Each host
				If h\ip$=hip$
					newh=False
					h\gn$=KNL_FieldGetString$("HostName")
					h\cp%=KNL_FieldGetInteger("CurrentPlayers")
					h\mp=KNL_FieldGetInteger("MaxPlayers")
					h\tim%=MilliSecs()
				EndIf
			Next
			If newh=True
				h.host=New host
				h\ip$=hip$
				h\gn$=KNL_FieldGetString$("HostName")
				h\cp%=KNL_FieldGetInteger("CurrentPlayers")
				h\mp=KNL_FieldGetInteger("MaxPlayers")
				h\tim%=MilliSecs()
			EndIf
		EndIf
		KNL_ClearMessage
	Wend
	Repeat
		k1=GetKey()
		k2=GetKey()
		kx=True
		Select k1
			Case 0:
			Case 8:
				If Len(k$)>1 Then k$=Left$(k$,Len(k$)-1)
				If Len(k$)<=1 Then k$=""
				kx=False
			Case 9:
				kx=False
				k$=k$+"    "
			Case 13:
				c.chat=New chat
				c\msg$=k$
				c\pos%=1
				k$=""
				kx=False
			Default:
				If k1>=32 And k1<=126
					k$=k$+Chr$(k1)
					kx=False
				EndIf
		End Select
		Select k2
			Case 0:
			Case 8:
				If Len(k$)>1 Then k$=Left$(k$,Len(k$)-1)
				If Len(k$)<=1 Then k$=""
				kx=False
			Case 9:
				kx=False
				k$=k$+"    "
			Case 13:
				c.chat=New chat
				c\msg$=k$
				c\pos%=1
				k$=""
				kx=False
			Default:
				If k2>=32 And k2<=126
					k$=k$+Chr$(k2)
					kx=False
				EndIf
		End Select
	Until kx=True
	pos=1
	For h.host=Each host
		If h\cp<h\mp
			If KeyHit(58+pos) Then xit=h\ip$
			pos=pos+1
			c.chat=New chat
			c\msg$=h\gn$+" ("+Str$(h\cp)+"/"+Str$(h\mp)+")"
			c\pos%=2
		EndIf
		If MilliSecs()-h\tim>3000 Then Delete h
	Next
	pos=1
	
	Cls
	Text 0,0,"Open Games (press the corrosponding function to join a game):",0,0
	For c.chat=Each chat
		If c\pos=1
			xit=1
		ElseIf c\pos=2
			If pos<=11 Then Text 20,20*pos,c\msg,0,0
			pos=pos+1
			Delete c
		EndIf
	Next
	If MilliSecs() Mod 500 > 250 Then
		Text 0,240,"Create Game: "+k$+"|"
	Else
		Text 0,240,"Create Game: "+k$
	EndIf
	Flip
Until xit<>0
KNL_StopHostsEnumeration

Cls
Flip

If xit=1
	For c.chat=Each chat
		If c\pos=1 Then Exit
	Next
	Text 320,240,"Starting net game "+Chr$(34)+c\msg$+Chr$(34)+" ...",1,1
	Flip
	
	If KNL_CreateHost(8080,c\msg$,"","",8)=0
		Cls
		Text 320,240,"Failed to create host. Press a key.",1,1
		FlushKeys
		Flip
		WaitKey
		KNL_Shutdown
		End
	EndIf
	KNL_SetPlayerInfo name$,""
Else
	For h.host=Each host
		If h\ip$=xit$ Then Exit
	Next
	Text 320,240,"Connecting to net game "+Chr$(34)+h\gn$+Chr$(34)+" ...",1,1
	Flip
	
	KNL_Shutdown
	KNL_Initialize("{24538F2F-748F-4779-8E20-36D948406E34}",8,138)
	If KNL_Connect(name$,"",xit$,8080,"",1)=0
		Cls
		Text 320,240,"Failed to connect to host. Press a key.",1,1
		FlushKeys
		Flip
		WaitKey
		KNL_Shutdown
		End
	EndIf
EndIf

For c.chat=Each chat
	Delete c
Next

Cls

k$=""
Repeat
	While KNL_CheckMessages()
		Select KNL_GetMessageID()
			Case 28:
				pid%=KNL_FieldGetInteger("LocalID")
				p.player=New player
				p\id%=pid%
				p\name$=name$
			Case 3
				pid%=KNL_FieldGetInteger("LocalID")
				p.player=New player
				p\id%=pid%
				p\name$=name$
			Case 4:
				pid%=KNL_FieldGetInteger("PlayerID")
				p.player=New player
				p\id%=pid%
				p\name$=KNL_GetPlayerName$(pid)
				c.chat=New chat
				c\pos=2
				c\msg$=p\name$+" has joined the game."
			Case 5:
				pid%=KNL_FieldGetInteger("PlayerID")
				For p.player=Each player
					If p\id%=pid%
						c.chat=New chat
						c\pos=2
						c\msg$=p\name$+" has left the game."
					EndIf
					Delete p
				Next
			Case 7:
				Select KNL_GetInteger(0)
					Case 1:
						pid%=KNL_FieldGetInteger("SenderID")
						For p.player=Each player
							If p\id%=pid%
								c.chat=New chat
								c\pos=2
								c\msg$=p\name$+": "+KNL_GetString$(1)
							EndIf
						Next
				End Select
		End Select
		KNL_ClearMessage()
	Wend

	Repeat
		k1=GetKey()
		k2=GetKey()
		kx=True
		Select k1
			Case 0:
			Case 8:
				If Len(k$)>1 Then k$=Left$(k$,Len(k$)-1)
				If Len(k$)<=1 Then k$=""
				kx=False
			Case 9:
				kx=False
				k$=k$+"    "
			Case 13:
				c.chat=New chat
				c\msg$=k$
				c\pos%=1
				k$=""
				kx=False
			Default:
				If k1>=32 And k1<=126
					k$=k$+Chr$(k1)
					kx=False
				EndIf
		End Select
		Select k2
			Case 0:
			Case 8:
				If Len(k$)>1 Then k$=Left$(k$,Len(k$)-1)
				If Len(k$)<=1 Then k$=""
				kx=False
			Case 9:
				kx=False
				k$=k$+"    "
			Case 13:
				c.chat=New chat
				c\msg$=k$
				c\pos%=1
				k$=""
				kx=False
			Default:
				If k2>=32 And k2<=126
					k$=k$+Chr$(k2)
					kx=False
				EndIf
		End Select
	Until kx=True

	pos=0
	For c.chat=Each chat
		If c\pos=1
			d.chat=New chat
			d\msg$=name$+": "+c\msg$
			d\pos=2
			KNL_SendInteger 1
			KNL_SendString c\msg$
			KNL_UpdateNetwork(0,0,1000,2)
			Delete c
		Else
			pos=pos+1
		EndIf
	Next
	If pos>20
		c.chat=First chat
		For a=1 To pos-20
			d.chat=After c
			Delete c
			c=d
		Next
	EndIf
	pos=0
	Cls
	For c.chat=Each chat
		Text 0,pos*20,c\msg$,0,0
		pos=pos+1
	Next
	If MilliSecs() Mod 500 > 250 Then
		Text 0,420,"Chat: "+k$+"|"
	Else
		Text 0,420,"Chat: "+k$
	EndIf
	Flip
Until KeyHit(1)

KNL_Shutdown
End
