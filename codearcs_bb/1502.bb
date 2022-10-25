; ID: 1502
; Author: Jan_
; Date: 2005-10-25 03:21:59
; Title: Web Browser
; Description: A webbrowser, without a HTML-View

;init the Window:
Global Proxy_Adress$,Proxy_port%

Proxy_Adress$="192.168.2.30"
Proxy_port%=3128
main_window=CreateWindow("Netz durchstöberer",0,0,GadgetWidth(Desktop()),GadgetHeight(Desktop()),0,9)
seite=CreateTextField(0,1,230,20,main_window)
canvas = CreateCanvas(2,25,GadgetWidth(Desktop())-12,GadgetHeight(Desktop())-75,main_window)
btn_go=CreateButton("Los!",250,1,100,20,main_window,1)

SetBuffer CanvasBuffer(canvas)
ClsColor (255,255,255)
Cls
Color 0,0,0
Get_Site("http://www.inarie.de/nav.php?where=main&show=home")
FlipCanvas canvas
Repeat
	;If GetKey()=27 Then End
	Local event=WaitEvent()
	Select event
		Case 1025 ;Taste Gedrückt
			;Stop
			Select EventSource()
				Case btn_go
					ClsColor (255,255,255)
					Cls
					Color 0,0,0
					Get_Site(TextFieldText(seite))
					FlipCanvas canvas
			End Select
		Case $803 : End
	End Select
Forever




Function Get_Site(Adresse$,SX=0,SY=0,port=80)
	;Http herrausschneiden
	If Left(Upper(Adresse$),7)="HTTP://" Then
		Adresse$ = Mid(adresse$,8,-1)
	EndIf
	
	Local x%,y%
	Local Server$, Seite$
	
	;Unterseite auf dem Server finden:
	x=Instr(Adresse$,"/")
	If x =0 Then
		Server$=Adresse$
		Seite$="/"
	Else
		Server$=Left(Adresse$,x-1)
		Seite$=Right(Adresse$,Len(Adresse$)-x+1)
	EndIf
	
	Local Htmlzeichen$,HTML_Keyword$,l
	Local txt$,m,g,i,tcp,Zeile$,Buchstabe$
	Local in_Body%
	x=0
	y=0
	
	;verbingung Öffnen
	tcp = HttpGet(Server$,Seite$,port,Proxy_Adress$,Proxy_port%) ; the last 3 points, are for the Proxy and Ports.
	If tcp = False RuntimeError "unable to connect to address"
	
	;Seite auslesen
	While Eof(tcp) = False
		zeile$=ReadLine$(tcp) ; Zeile zum bearbeiten
		;txt$=""
		DebugLog zeile$
		For i =1 To Len(zeile$)
		
			; HTML Quelltext bearbeiten
			If Mid(zeile$,i,1)="<" Then m=m+1:g=i
			If Mid(zeile$,i,1)=">" Then
				m=m-1
				
				Htmlzeichen$ = Lower(Mid(zeile$,g+1,i-g-1))
				l=Instr(Htmlzeichen$," ")
				If l Then
					Html_keyword$=Left(Htmlzeichen$,l-1)
				Else
					Html_keyword$=Htmlzeichen$
				EndIf
				Select Html_keyword$
					Case "body"
						in_body=1
					Case "br"
						Text x-sx,y-sy,txt$
						txt$=""
						y=y+15
					Case "/body"
						Text x-sx,y-sy,txt$
						txt$=""
						in_body=0
				End Select
				;If Upper(Mid(zeile$,g+1,i-g-1)) = "BR"
				;	txt$=txt$+Chr(13)+Chr(10)
				;EndIf
			 
			EndIf
			If m<0 Then m=0
			buchstabe$=Mid(zeile$,i,1)
			If buchstabe$=">" Then Buchstabe$ = ""
			; Text aufnehmen
			If m=0 And in_body Then txt$=txt$+Buchstabe$
		Next
		
		;Leerzeichen, am ende, der Zeile
		If Not(Trim(txt$) = "")
			txt$ = txt$+" "
		EndIf

	Wend
	CloseTCPStream(tcp)
End Function
;function
Function HttpGet(server$,path$,port=80,proxy$="",proxyport=0)
	Local www
	If Len(proxy$) = 0 proxy$ = server$
	If proxyport = 0 proxyport = port
	www = OpenTCPStream(proxy$,proxyport)
	If www = False Return False
	WriteLine www,"GET http://" + server$ + ":" + port + path$ + " HTTP/1.1" + Chr$(13)+Chr$(10) + "Host: " + server$ + Chr$(13)+Chr$(10) + "User-Agent: blitzbasic" + Chr$(13)+Chr$(10) + "Accept: */*" + Chr$(13)+Chr$(10)
	Return www
End Function
