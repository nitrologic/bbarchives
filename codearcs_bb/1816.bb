; ID: 1816
; Author: Andres
; Date: 2006-09-18 11:05:21
; Title: Remote File Engine
; Description: Easy way to download and read remote files via HTTP/FTP

Global UserAgent$ = "Transporter"	; What ever you'd like to name your application
Global ResponseDelay% = 2000		; How many millisecs to wait for HTTP response
Global NewLine$ = Chr(13) + Chr(10)	; Line break may differ
Const RFDebugMode% = False				; Debug mode

Type transporter
	Field id%
	Field code%
	Field filename$
	Field protocol$
End Type

Function OpenRemoteFile%(path$, port% = 80, variables$ = "", httpheader$ = "")
	Local protocol$ = Lower(Sector$(path$, ":", 0))
	Local host$ = Sector$(path$, "/", 2)
	Local ip$ = "www." + Sector$(host$, ".", Sectors%(host$, ".") - 1) + "." + Sector$(host$, ".", Sectors%(host$, "."))
	path$ = "/" + Sector$(path$, "/", 3, True)
	Local header%, bank%, tim%, occ%
	
	
	Local Stream% = OpenTCPStream(host$, port%)
	If Not Stream% Then Stream% = OpenTCPStream(ip$, port%)
	
	If Stream
		Select protocol$
			Case "http", "https" ; ---------------------------------------------------------------------------------------------------------------------------------------
				; Send request
				
				If Not Len(variables$) Then
					WriteLine(Stream, "GET " + path$ + " HTTP/1.1")
				Else
					WriteLine(Stream, "POST " + path$ + " HTTP/1.1")
				EndIf
				WriteLine(Stream, "Host: " + host$)
				WriteLine(Stream, "User-Agent: " + UserAgent$)
				If Len(variables$) Then
					WriteLine(Stream, "Content-Type: application/x-www-form-urlencoded")
					WriteLine(Stream, "Content-Length: " + Len(variables$))
				EndIf
				If Len(httpheader$) Then WriteLine(Stream, httpheader$)
				WriteLine(Stream, "Connection: Close")
				WriteLine(Stream, "")
				If Len(variables$) Then WriteLine(Stream, variables$)
				
				; Debug mode
				If RFDebugMode% Then
					If Not Len(variables$) Then DebugLog ">>> GET " + path$ + " HTTP/1.1" Else DebugLog ">>> POST " + path$ + " HTTP/1.1"
					DebugLog ">>> Host: " + host$
					DebugLog ">>> User-Agent: " + UserAgent$
					If Len(variables$) Then
						DebugLog ">>> Content-Type: application/x-www-form-urlencoded"
						DebugLog ">>> Content-Length: " + Len(variables$)
					EndIf
					If Len(httpheader$) Then DebugLog ">>> " + httpheader$
					DebugLog ">>> Connection: Close"
					DebugLog ">>> "
					If Len(variables$) Then DebugLog ">>> " + variables$
				EndIf
				
				; Wait for response
				tim = MilliSecs()
				Repeat
				Until (MilliSecs() - tim) => ResponseDelay% Or ReadAvail(Stream)
				
				txt$ = ReadLine$(Stream%)
				If RFDebugMode Then DebugLog "<<< " + txt$
				If Sector$(txt$, " ", 0) = "HTTP/1.1" Or Sector$(txt$, " ", 0) = "HTTP/1.0"
					code% = Int Sector$(txt$, " ", 1)
					If (code% => 300) Then
						CloseTCPStream Stream%
						Return False
					EndIf
				EndIf
				
				occ% = 0
				header% = CreateBank(0)
				bank% = CreateBank(12); Bank Int, Stream Int, Size Int
				
				PokeInt(bank%, 0, header%)
				PokeInt(bank%, 4, Stream%)
				PokeInt(bank%, 8, -1)
				
				this.transporter = New transporter
					this\id% = bank%
					this\code% = code%
					this\filename$ = Sector$(path$, "/", Sectors%(path$, "/"))
					this\protocol$ = "http"
				
				; Received HTTP Header
				Repeat
					txt$ = ReadRemoteLine$(bank%)
					value$ = Mid$(Sector$(txt$, ":", 1, True), 2)
					
					If RFDebugMode Then DebugLog "<<< " + txt$
					
					Select Sector$(txt$, ":", 0)
						Case ""
							Return bank%
						Case "Content-Disposition"
							For i = 0 To Sectors(value$, ";")
								subtxt$ = Trim$(Sector$(value$, ";", i))
								subvalue$ = Sector$(subtxt$, "=", 1)
								
								Select Sector$(subtxt$, "=", 0)
									Case "filename"
										this\filename$ = subvalue$
										DebugLog "[" + this\filename$ + "]"
								End Select
							Next
						Case "Content-Length"
							PokeInt bank%, 8, Int(value$)
					End Select
				Forever
			Case "ftp" ; ---------------------------------------------------------------------------------------------------------------------------------------
				header% = CreateBank(1)
				bank% = CreateBank(16); Bank Int, Stream Int, Size Int
				
				PokeInt(bank%, 0, header%)
				PokeInt(bank%, 4, Stream%)
				PokeInt(bank%, 8, -1)
				PokeInt(bank%, 12, stream%)
				
				this.transporter = New transporter
					this\id% = bank%
					this\code% = "0"
					this\filename$ = Sector$(path$, "/", Sectors(path$, "/"))
					this\protocol$ = "ftp"
				
				; Wait for response
				tim = MilliSecs()
				Repeat
				Until (MilliSecs() - tim) => ResponseDelay% Or ReadAvail(Stream)
				
				Repeat
					If ReadAvail(stream%)
						txt$ = ReadLine(stream%)
						code% = Int Sector$(txt$, " ", 0)
						value$ = Mid$(Sector$(txt$, " ", 1, True), 1)
						If RFDebugMode Then DebugLog "<<< " + txt$
					Else
						txt$ = "":code% = 0:value$ = "":cmd$ = ""
					EndIf
					
					If Not ReadAvail(stream%)
						Select code%
							Case 220
								cmd$ = "USER " + variables$
							Case 331
								cmd$ = "PASS " + httpheader$
							Case 230
								cmd$ = "SIZE " + path$
							Case 213
								PokeInt(bank%, 8, Int value$)
								cmd$ = "PASV"
							Case 227
								; Connect to PASV mode
								txt$ = Sector(txt$, "(", 1)
								host$ = Sector(txt$, ",", 0) + "." + Sector(txt$, ",", 1) + "." + Sector(txt$, ",", 2) + "." + Sector(txt$, ",", 3)
								port% = (Int Sector(txt$, ",", 4)) * 256 + (Int Left$(Sector(txt$, ",", 5), Len(Sector(txt$, ",", 5)) - 1))
								pasv_stream% = OpenTCPStream(host$, port%)
								
								; Update stream
								PokeInt(bank%, 4, pasv_stream%)
								
								cmd$ = "RETR " + path$
							Case 150
								Return bank%
						End Select
						
						; ERROR
						If code% => 400
							If stream% Then CloseTCPStream stream%
							If pasv_stream% Then CloseTCPStream pasv_stream%
							If header% Then FreeBank header%
							If bank% Then FreeBank bank%
							Return 0
						EndIf
					EndIf
					
					If Len(cmd$) Then
						If RFDebugMode% Then DebugLog ">>> " + cmd$
						WriteLine stream%, cmd$
					EndIf
				Forever
				
				Return bank%
		End Select
	EndIf
End Function

Function CloseRemoteFile(bank%)
	If PeekInt(bank%, 0) Then FreeBank PeekInt(bank%, 0)
	If PeekInt(bank%, 4) Then CloseTCPStream PeekInt(bank%, 4)
	If BankSize(bank%) = 18 Then If PeekInt(bank%, 12)
		WriteLine PeekInt(bank%, 12), "BYE"
		If RFDebugMode% Then DebugLog ">>> BYE"
		CloseTCPStream PeekInt(bank%, 12)
	EndIf
	FreeBank bank%
	
	For this.transporter = Each transporter
		If this\id% = bank%
			Delete this
			Exit
		EndIf
	Next
End Function

Function EORF(bank%)
	If Not PeekInt(bank%, 4)
		If BankSize(PeekInt(bank%, 0)) = 0 Then Return True
	Else
		If Eof(PeekInt(bank%, 4)) Then Return True
	EndIf
End Function

Function RemoteFileSize%(bank%)
	Return PeekInt(bank%, 8)
End Function

Function RemoteFileName$(bank%)
	For this.transporter = Each transporter
		If this\id% = bank% Return this\filename$
	Next
End Function

Function RemoteFileCode%(bank%)
	For this.transporter = Each transporter
		If this\id% = bank% Return this\code%
	Next
End Function

Function RemoteFileProtocol$(bank%)
	For this.transporter = Each transporter
		If this\id% = bank% Return this\protocol$
	Next
End Function

Function ReadRemoteLine$(bank%)
	Local avail%, rbank% = PeekInt(bank%, 0), stream% = PeekInt(bank%, 4)
	
	; Update bank
	UpdateRemoteFile(bank%)
	
	; Read line
	txt$ = ""
	For i = 0 To BankSize(rbank%) - 1
		char% = PeekByte(rbank%, i)
		txt$ = txt$ + Chr(char%)
		If Right$(txt$, Len(NewLine$)) = NewLine$ Then Exit
	Next
	
	If Right$(txt$, 2) = NewLine$ Then
		txt$ = Mid$(txt$, 1, Len(txt$) - Len(NewLine$))
	ElseIf Right$(txt$, 1) = Right$(NewLine$, 1) Then
		txt$ = Mid$(txt$, 1, Len(txt$) - (Len(NewLine$) - 1))
	EndIf
	
	; Resize bank
	size% = BankSize(rbank%) - (Len(txt$) + Len(NewLine$))
	If size% < 0 Then size% = 0
	ResizeRemoteBank(bank%, size%)
	
	Return txt$
End Function

Function ReadRemoteString$(bank%)
	Local rbank% = PeekInt(bank%, 0)
	UpdateRemoteFile(bank%)
	
	a% = PeekInt(rbank%, 0)
	
	txt$ = ""
	For i = 0 To a% - 1
		txt$ = txt$ + Chr(PeekByte(rbank%, 4 + i))
	Next
	
	ResizeRemoteBank(bank%, BankSize(rbank%) - (4 + a%))
	Return txt$
End Function

Function ReadRemoteInt%(bank%)
	Local rbank% = PeekInt(bank%, 0)
	UpdateRemoteFile(bank%)
	
	a% = PeekInt(rbank%, 0)
	ResizeRemoteBank(bank%, BankSize(rbank%) - 4)
	Return a%
End Function

Function ReadRemoteShort%(bank%)
	Local rbank% = PeekInt(bank%, 0)
	UpdateRemoteFile(bank%)
	
	a% = PeekShort(rbank%, 0)
	ResizeRemoteBank(bank%, BankSize(rbank%) - 2)
	Return a%
End Function

Function ReadRemoteByte%(bank%)
	Local rbank% = PeekInt(bank%, 0)
	UpdateRemoteFile(bank%)
	
	a% = PeekByte(rbank%, 0)
	ResizeRemoteBank(bank%, BankSize(rbank%) - 1)
	Return a%
End Function

Function WriteRemoteBytes(bank%, file%, offset%, count%)
	Local rbank% = PeekInt(bank%, 0)
	UpdateRemoteFile(bank%)
	
	Local N% = WriteBytes(rbank%, file%, offset%, count%)
	ResizeRemoteBank(bank%, BankSize(rbank%) - N%)
	
	Return N%
End Function

Function RemoteReadAvail(bank%)
	UpdateRemoteFile(bank%)
	received% = BankSize(PeekInt(bank%, 0))
	If PeekInt(bank%, 4) Then waiting% = ReadAvail(PeekInt(bank%, 4)) Else waiting% = 0
	Return received% + waiting%
End Function

Function UpdateRemoteFile(bank%)
	Local rbank% = PeekInt(bank%, 0), stream% = PeekInt(bank%, 4)
	
	If stream% And rbank%
		avail% = ReadAvail(stream%)
		offset% = BankSize(rbank%)
		
		ResizeBank(rbank%, offset% + avail%)
		ReadBytes(rbank%, stream%, offset%, avail%)
		
		If Eof(stream%) Then
			CloseTCPStream stream%
			PokeInt bank%, 4, 0
		EndIf
	EndIf
End Function

Function ResizeRemoteBank(bank%, size%)
	Local rbank% = PeekInt(bank%, 0), start% = BankSize(rbank%) - size%
	
	If BankSize(rbank%)
		CopyBank(rbank%, start%, rbank%, 0, size%)
		ResizeBank(rbank%, size%)
	EndIf
End Function

Function Sector$(txt$, separator$, sector%, toend% = False)
	Local result$ = "", occ
	For i = 1 To Len(txt$)
		If Mid$(txt$, i, 1) = separator$
			occ = occ + 1
			If toend% And occ% > sector% Then result$ = result$ + Mid$(txt$, i, 1)
		Else
			If occ => sector Then result$ = result$ + Mid$(txt$, i, 1)
		EndIf
		If Not toend% Then If occ > sector Then Exit
	Next
	Return result$
End Function

Function Sectors%(txt$, needle$)
	occ% = 0
	For i = 1 To Len(txt$) Step 1
		If Instr(txt$, needle$, i)
			occ% = occ% + 1
			i = Instr(txt$, needle$, i)
		Else
			Exit
		EndIf
	Next
	Return occ%
End Function
