; ID: 1658
; Author: Andres
; Date: 2006-04-05 00:39:43
; Title: Nemesis Chat
; Description: A graphical chat

; Client source only

Type map
	Field tile
	Field x
	Field y
	Field attribute
	Field toptile
End Type

Type Connection
	Field ip$
	Field name$
	Field character$
	Field x#
	Field y#
	Field tx#
	Field ty#
	Field frame#
	Field minframe#
	Field footprinttime
	Field messagetime
	Field message$
End Type

Type server
	Field address$
	Field name$
	Field map$
	Field clients$
End Type

Type footprint
	Field x
	Field y
	Field frame
	Field angle
	Field time
End Type

Global ChatBufferSize = 32
Global TopChat = ChatBufferSize - 5
Dim chat$(ChatBufferSize, 4) ; red, green, blue, text$

Global serverliststream = OpenTCPStream("www.kylm.com", 80)
	If serverliststream
		WriteLine serverliststream, "GET /servers.php HTTP/1.1"
		WriteLine serverliststream, "Host: nemesis.kylm.com"
		WriteLine serverliststream, "User-Agent: NemesisChat"
		WriteLine serverliststream, "Connection: Keep-Alive"
		WriteLine serverliststream, ""
		
		While Not ReadAvail(serverliststream)
		Wend
		
		Repeat
			txt$ = ReadLine$(serverliststream)
		Until txt$ = ""
	EndIf

Global MainWindow = CreateWindow("Chat options", ClientWidth(Desktop()) / 2 - 350 / 2, ClientHeight(Desktop()) / 2 - 210 / 2, 350, 210, Desktop(), 1)
AppTitle("Nemesis chat")
AppIcon("data\icon.ico")
SeedRnd MilliSecs()

CreateLabel("Name:", 5, 8, 50, 15, MainWindow)
Global namefield = CreateTextField(55, 5, 150, 20, MainWindow)
CreateLabel("Character:", 5, 33, 50, 15, MainWindow)
Global characterlist = CreateComboBox(55, 30, 150, 20, MainWindow)
Global screenmodebutton = CreateButton("Fullscreen", 215, 7, 120, 14, MainWindow, 2)
Global footprintbutton = CreateButton("Footprints", 215, 21, 120, 14, MainWindow, 2)
Global bubblebutton = CreateButton("Message bubbles", 215, 35, 120, 14, MainWindow, 2)

CreateLabel("Host:", 5, 75, 50, 15, MainWindow)
Global hostlist = CreateListBox(18, 90, 140, 60, MainWindow)
Global hostfield = CreateTextField(18, ClientHeight(MainWindow) - 25, 140, 20, MainWindow)
Global autobutton = CreateButton("", 2, 90, 15, 15, MainWindow, 3)
Global manualbutton = CreateButton("", 2, ClientHeight(MainWindow) - 23, 15, 15, MainWindow, 3)

CreateLabel("Info:", 160, 75, 50, 15, MainWindow)
CreateLabel("Name: ", 160, 90, 40, 15, MainWindow)
Global namelabel = CreateLabel("", 210, 90, 130, 15, MaiNWindow)
CreateLabel("Address: ", 160, 105, 40, 15, MainWindow)
Global addresslabel = CreateLabel("", 210, 105, 130, 15, MaiNWindow)
CreateLabel("Map: ", 160, 120, 40, 15, MainWindow)
Global maplabel = CreateLabel("", 210, 120, 130, 15, MaiNWindow)
CreateLabel("Users: ", 160, 135, 40, 15, MainWindow)
Global userslabel = CreateLabel("", 210, 135, 130, 15, MaiNWindow)

Global creditsbutton = CreateButton("Credits", ClientWidth(MainWindow) - 145, ClientHeight(MainWindow) - 25, 50, 20, MainWindow)
Global webbutton = CreateButton("Web", ClientWidth(MainWindow) - 90, ClientHeight(MainWindow) - 25, 40, 20, MainWindow)
Global chatbutton = CreateButton("Chat", ClientWidth(MainWindow) - 45, ClientHeight(MainWindow) - 25, 40, 20, MainWindow)

SetButtonState autobutton, True
DisableGadget hostfield

;Characters
strip = LoadIconStrip("data\characters\characters.bmp")
SetGadgetIconStrip characterlist, strip
	AddGadgetItem characterlist, "Toshi", 1, 0
	AddGadgetItem characterlist, "Kaj", 0, 1
	AddGadgetItem characterlist, "Shiki", 0, 2
FreeIconStrip strip

;Variables
Global message$
Global mainversion$ = "1.0"
Global protocolversion$ = "1.0"
Global localhost$ = ""
Global infotimer = CreateTimer(8)
Global LoadingWindow
Global stream

Global name$
Global character$
Global host$
Global servername$
Global screenmode%
Global footprint
Global bubbles
Global footprinttime
Global bubbletime
Global bubbletext$

Global game_speed = 32
Global last_milli
Global mouseaction
Global fpstime
Global fps#

Global TileSize = 32
Global MapWidth
Global MapHeight
Global MapName$

Global frame#
Global minframe# = 0
Global x# = -1
Global y# = -1
Global camerax
Global cameray
Global xspeed# = 2
Global yspeed# = 1.5

Global header
Global bar
Global scroll
Global tiles
Global toshi
Global kaj
Global shiki
Global lightemoticons
Global darkemoticons
Dim FootPrintImage(35)

Global infoleft
Global infomid
Global inforight
Global leftbubble
Global midbubble
Global rightbubble

LoadFields()

Repeat
	If MainWindow
		Events()
		ReceiveServers()
	Else
		If (MilliSecs() - last_milli) => 1000 / game_speed
			
			If MouseDown(1) And mouseaction = 2
				yy = (MouseY() - ImageHeight(scroll) / 2 - (GraphicsHeight() - ImageHeight(bar) + 6))
				NewTop = ChatBufferSize * (Float yy / 67)
				TopChat = TopChat + Sgn(NewTop - TopChat)
				
				If TopChat > ChatBufferSize - 5 Then TopChat = ChatBufferSize - 5
				If TopChat < 0 Then TopChat = 0
			EndIf
			
			MoveCharacters()
			ReceiveData()
			
			last_milli = MilliSecs()
		Else
			camerax = x - GraphicsWidth() / 2
			cameray = y - GraphicsHeight() / 2 + ImageHeight(bar) * .75
			If camerax > MapWidth * TileSize - GraphicsWidth() + TileSize Then camerax = MapWidth * TileSize - GraphicsWidth() + TileSize
			If cameray > MapHeight * TileSize - GraphicsHeight() + ImageHeight(bar) + TileSize Then cameray = MapHeight * TileSize - GraphicsHeight() + ImageHeight(bar) + TileSize
			If camerax < 0 Then camerax = 0
			If cameray < -ImageHeight(header) Then cameray = -ImageHeight(header)
			
			Inputs()
			
			DrawGround()
			DrawFootPrints()
			DrawCharacter()
			DrawTop()
			MouseOver()
			DrawBubbles()
			DrawBar()
			DrawChat()
			
			Flip
			Cls
			CountFPS()
		EndIf
	EndIf
Forever

Function Events()
	Select WaitEvent()
		Case $401
			Select EventSource()
				Case autobutton, manualbutton
					If ButtonState(autobutton)
						EnableGadget hostlist
						DisableGadget hostfield
					Else
						EnableGadget hostfield
						DisableGadget hostlist
					EndIf
				Case hostlist
					For this.server = Each server
						If i = SelectedGadgetItem(hostlist)
							SetGadgetText namelabel, this\name
							SetGadgetText addresslabel, this\address
							SetGadgetText maplabel, this\map
							SetGadgetText userslabel, this\clients
						EndIf
						i = i + 1
					Next
				Case creditsbutton
					ExecFile("notepad credits.txt")
				Case webbutton
					ExecFile("http://nemesis.kylm.com")
				Case chatbutton
					SaveFields()
					If TextFieldText(namefield) = "":Notify "You need to type in your name!":Return:EndIf
					
					If ButtonState(autobutton)
						If SelectedGadgetItem(hostlist) < 0:Notify "You need to select the host!":Return:EndIf
						
						host$ = GadgetItemText(hostlist, SelectedGadgetItem(hostlist))
						For this.server = Each server
							If i = SelectedGadgetItem(hostlist)
								host$ = this\address$
								servername$ = this\name$
							EndIf
							i = i + 1
						Next
					Else
						If TextFieldText(hostfield) = "":Notify "You need to enter the host's IP!":Return:EndIf
						host$ = TextFieldText(hostfield)
						servername$ = host$
					EndIf
					
					name$ = TextFieldText(namefield)
					character$ = GadgetItemText(characterlist, SelectedGadgetItem(characterlist))
					screenmode = ButtonState(screenmodebutton)
					footprint = ButtonState(footprintbutton)
					bubbles = ButtonState(bubblebutton)
					BeginChat()
			End Select
		Case $803
			FreeGadget MainWindow
			End
	End Select
	
	If MainWindow
		If Len(TextFieldText(namefield)) > 24 Then SetGadgetText namefield, Left$(TextFieldText(namefield), 24)
	EndIf
	
End Function

Function ReceiveServers()
	If serverliststream
		If Eof(serverliststream)
			CloseTCPStream serverliststream
			serverliststream = 0
		Else
			If ReadAvail(serverliststream)
				txt$ = ReadLine$(serverliststream)
				
				While Instr(txt$, "\'")
					txt$ = Replace(txt$, "\'", "'")
				Wend
				While Instr(txt$, "\" + Chr(34))
					txt$ = Replace(txt$, "\" + Chr(34),  Chr(34))
				Wend
				
				If Len(txt$) > 3
					address$ = Left$(txt$, Instr(txt$, Chr(0)) - 1)
					txt$ = Right$(txt$, Len(txt$) - Len(address$) - 1)
					name$ = Left$(txt$, Instr(txt$, Chr(0)) - 1)
					txt$ = Right$(txt$, Len(txt$) - Len(name$) - 1)
					mapname$ = Left$(txt$, Instr(txt$, Chr(0)) - 1)
					txt$ = Right$(txt$, Len(txt$) - Len(mapname$) - 1)
					clients$ = Left$(txt$, Instr(txt$, Chr(0)) - 1)
					
					this.server = New server
						this\address = address$
						this\name = name$
						this\map = mapname$
						this\clients = clients$
					
					AddGadgetItem hostlist, this\name
				EndIf
			EndIf
		EndIf
	EndIf
End Function

Function LoadFields()
	rf = ReadFile("data.dat")
	If rf
		SetGadgetText namefield, ReadLine$(rf)
		SelectGadgetItem characterlist, ReadLine$(rf)
		SetButtonState screenmodebutton, ReadLine$(rf)
		SetButtonState footprintbutton, ReadLine$(rf)
		SetButtonState bubblebutton, ReadLine$(rf)
		CloseFile rf
	EndIf
End Function

Function SaveFields()
	wf = WriteFile("data.dat")
	If wf
		WriteLine wf, TextFieldText(namefield)
		WriteLine wf, SelectedGadgetItem(characterlist)
		WriteLine wf, ButtonState(screenmodebutton)
		WriteLine wf, ButtonState(footprintbutton)
		WriteLine wf, ButtonState(bubblebutton)
		CloseFile wf
	EndIf
End Function

Function CountFPS()
	If Not Rand(0, 50) Then fps = ((MilliSecs() - fpstime) + 1000.0) / (MilliSecs() - fpstime)
	fpstime = MilliSecs()
End Function

Function AppIcon(iconfile$)
	hwnd = User32_GetActiveWindow()
	icon = LoadIcon(hwnd, iconfile$, 0)
	User32_SetClassLong hwnd, -14, icon
End Function

Function BeginChat()
	AppTitle("Nemesis chat " + mainversion$)
	LoadingWindow = CreateWindow("Loading...", ClientWidth(Desktop()) / 2 - 250 / 2, ClientHeight(Desktop()) / 2 - 80 / 2, 250, 80, Desktop(), 1)
	label = CreateLabel("", 5, ClientHeight(LoadingWindow) / 2 - 7, ClientWidth(LoadingWindow) - 10, 18, LoadingWindow)
	api_SetWindowPos(QueryObject(LoadingWindow, 1), -1, 0, 0, 0, 0, $0003)
	
	;Connect
	ip$ = Left$(host$, Instr(host$, ":") - 1)
	port = Right$(host$, Len(host$) - Instr(host$, ":"))
	SetGadgetText label, "Connecting to " + host$ + " ..."
	stream = OpenTCPStream(ip$, port)
	If Not stream
		Error("Unable to connect to the server!")
		Return False
	EndIf
	
	;Error check
	error$ = ReadString$(stream)
	If Not error$ = ""
		Error(error$)
		Return False
	EndIf
	
	;Send version
	SetGadgetText label, "Comparing versions..."
	WriteString stream, protocolversion$
	tim = MilliSecs()
	While Not ReadAvail(stream)
		If Eof(stream)
			Error("Versions don't match!")
			Return False
		EndIf
		If MilliSecs() < tim Then tim = tim - (24 * 60 * 60 * 1000)
		If (MilliSecs() - tim) > 5000
			Error("No response from the server!")
			Return False
		EndIf
	Wend
	
	mapsize = ReadInt(stream)
	MapName$ = ReadString$(stream)
	M = Len(MapName$) + 4
	Repeat
		SetGadgetText label, "Receiving map '" + MapName$ + "' (" + Int((Float M / mapsize) * 100) + "%)..."
		this.map = New map
			this\tile = ReadShort(stream)
			this\x = ReadByte(stream)
			this\y = ReadByte(stream)
			this\attribute = ReadByte(stream)
			this\toptile = ReadShort(stream)
			
		If this\x > MapWidth Then MapWidth = this\x
		If this\y > MapHeight Then MapHeight = this\y
		
		M = M + 7
	Until M = mapsize
	
	;System
	FreeGadget MainWindow
	MainWindow = False
	FreeGadget LoadingWindow
	LoadingWindow = False
	Graphics 640, 480, 32, 2 - screenmode
	SetBuffer BackBuffer()
	SetFont LoadFont("Courier New", 18, True)
	ShowPointer
	MoveMouse GraphicsWidth() / 2, GraphicsHeight() / 2
	TFormFilter False
	
	;Spawnpoint
	Repeat
		x = Rand(5, MapWidth - 5) * TileSize
		y = Rand(5, MapHeight - 5) * TileSize
		If Not Blocked(x / TileSize, y / TileSize) Exit
	Forever
	
	header = LoadImage("data\header.bmp")
	bar = LoadImage("data\bar.bmp")
	scroll = LoadImage("data\scroll.bmp")
	lightemoticons = LoadAnimImage("data\lightemoticons.bmp", 16, 16, 0, 6)
	darkemoticons = LoadAnimImage("data\darkemoticons.bmp", 16, 16, 0, 6)
	
	infoleft = LoadImage("data\infoleft.bmp")
	infomid = LoadImage("data\infomid.bmp")
	inforight = LoadImage("data\inforight.bmp")
	leftbubble = LoadImage("data\leftbubble.bmp")
	midbubble = LoadImage("data\midbubble.bmp")
	rightbubble = LoadImage("data\rightbubble.bmp")
	
	temptiles = LoadImage("data\tiles.bmp")
	tilecount = ImageWidth(temptiles) / TileSize
	FreeImage temptiles
	
	tiles = LoadAnimImage("data\tiles.bmp", 32, 32, 0, tilecount)
	toshi = LoadAnimImage("data\characters\toshi.bmp", 32, 32, 0, 16)
	kaj = LoadAnimImage("data\characters\kaj.bmp", 32, 32, 0, 16)
	shiki = LoadAnimImage("data\characters\shiki.bmp", 32, 32, 0, 16)
	
	footprintimage(0) = LoadImage("data\footprint.bmp")
	MaskImage footprintimage(0), 255, 0, 255
	MidHandle footprintimage(0)
	For i = 1 To 35
		footprintimage(i) = CopyImage(footprintimage(0))
		RotateImage footprintimage(i), i * 10
	Next
	
	MaskImage lightemoticons, 255, 0, 255
	MaskImage darkemoticons, 255, 0, 255
	MaskImage infoleft, 255, 0, 255
	MaskImage inforight, 255, 0, 255
	MaskImage leftbubble, 255, 0, 255
	MaskImage midbubble, 255, 0, 255
	MaskImage rightbubble, 255, 0, 255
	
	MaskImage tiles, 255, 0, 255
	MaskImage toshi, 255, 0, 255
	MaskImage kaj, 255, 0, 255
	MaskImage shiki, 255, 0, 255
	
	ResizeImage scroll, ImageWidth(scroll), 67 * (Float 5 / ChatBufferSize)
	
End Function

Function MoveCharacters()
	;Local character
	truetime# = Float (1000.0 / game_speed) / (MilliSecs() - last_milli)
	If MouseDown(1) And mouseaction = 1
		angle = GetAngle(x - camerax, y - cameray, MouseX() - TileSize / 2, MouseY() - TileSize / 2)
		If GetLenght(x - camerax, y - cameray, MouseX() - TileSize / 2, MouseY() - TileSize / 2) < (xspeed + yspeed) / 2
			x = camerax + MouseX() - TileSize / 2
			y = cameray + MouseY() - TileSize / 2
		Else
			If (Not Blocked(Floor(x + Cos(angle) * xspeed / truetime#) / TileSize + .45, Floor(y) / TileSize + .5)) And (Not Blocked(Floor(x + Cos(angle) * xspeed / truetime#) / TileSize - .45, Floor(y) / TileSize + .5)) x = x + Cos(angle) * xspeed / truetime#
			If (Not Blocked(Floor(x) / TileSize + .45, Floor(y + Sin(angle) * yspeed / truetime#) / TileSize + .5)) And (Not Blocked(Floor(x) / TileSize - .45, Floor(y + Sin(angle) * yspeed / truetime#) / TileSize + .5)) y = y + Sin(angle) * yspeed / truetime#
			frame = frame + .3
		EndIf
		
		minframe = (angle - 45) / 90 * 4
		If angle < 45 minframe = 12
		If frame < minframe Or frame > minframe + 3 frame = minframe
		
		If (MilliSecs() - footprinttime) > 350 And footprint
			that.footprint = New footprint
				that\angle = Floor(angle / 10) Mod 36
				that\x = x + TileSize / 2
				that\y = y + TileSize * .85
				that\time = MilliSecs()
				footprinttime = MilliSecs()
		EndIf
			
		UploadInfo()
	Else
		frame = minframe
	EndIf
	
	;Remote characters
	For this.connection = Each connection
		truetime# = Float (1000.0 / game_speed) / (MilliSecs() - last_milli)
		
		angle = GetAngle(this\x, this\y, this\tx, this\ty)
		lenght = GetLenght(this\x, this\y, this\tx, this\ty)
		
		If this\x =< 0 And this\y =< 0
			this\x = this\tx
			this\y = this\ty
		EndIf
		
		If lenght <= (xspeed + yspeed) / 2
			this\x = this\tx
			this\y = this\ty
		Else
			this\x = this\x + Cos(angle) * xspeed / truetime#
			this\y = this\y + Sin(angle) * yspeed / truetime#
			this\frame = this\frame + .3
			
			If (MilliSecs() - this\footprinttime) > 350 And footprint
				that.footprint = New footprint
					that\angle = Floor(angle / 10) Mod 36
					that\x = this\x + TileSize / 2
					that\y = this\y + TileSize * .85
					that\time = MilliSecs()
					this\footprinttime = MilliSecs()
			EndIf
		EndIf
		
		If this\frame < this\minframe Or this\frame > this\minframe + 3 this\frame = this\minframe
		
	Next
End Function

Function DrawGround()
	For this.map = Each map
		xx = this\x * TileSize - camerax
		yy = this\y * TileSize - cameray
		DrawBlock tiles, xx, yy, this\tile
	Next
End Function

Function DrawFootPrints()
	For this.footprint = Each footprint
		DrawImage footprintimage(this\angle), this\x - camerax, this\y - cameray, this\frame
		If MilliSecs() < this\time Then this\time = this\time - 24 * 60 * 60 * 1000
		If (MilliSecs() - this\time) > 60000 Delete this
	Next
End Function

Function DrawCharacter()
	For this.connection = Each connection
		If this\y < y
			Select Lower(this\character)
				Case "toshi"
					DrawImage toshi, this\x - camerax, this\y - cameray, this\frame
				Case "kaj"
					DrawImage kaj, this\x - camerax, this\y - cameray, this\frame
				Case "shiki"
					DrawImage shiki, this\x - camerax, this\y - cameray, this\frame
				Default
					Text this\x - camerax + TileSize / 2, this\y - cameray + TileSize / 2, "[" + this\name + "]", 1, 1
			End Select
		EndIf
	Next

	Select Lower(character)
		Case "toshi"
			DrawImage toshi, x - camerax, y - cameray, frame
		Case "kaj"
			DrawImage kaj, x - camerax, y - cameray, frame
		Case "shiki"
			DrawImage shiki, x - camerax, y - cameray, frame
	End Select
	
	For this.connection = Each connection
		If this\y => y
			Select Lower(this\character)
				Case "toshi"
					DrawImage toshi, this\x - camerax, this\y - cameray, this\frame
				Case "kaj"
					DrawImage kaj, this\x - camerax, this\y - cameray, this\frame
				Case "shiki"
					DrawImage shiki, this\x - camerax, this\y - cameray, this\frame
				Default
					Text this\x - camerax + TileSize / 2, this\y - cameray + TileSize / 2, "[" + this\name + "]", 1, 1
			End Select
		EndIf
	Next
End Function

Function DrawTop()
	For this.map = Each map
		If this\attribute = 2
			xx = this\x * TileSize - camerax
			yy = this\y * TileSize - cameray
			DrawImage tiles, xx, yy, this\toptile
		EndIf
	Next
End Function

Function DrawBubbles()
	If bubbles
		For this.connection = Each connection
			If (MilliSecs() - this\messagetime) < 1000 + Len(this\message) * 100
				xx = this\x - camerax + TileSize / 2
				yy = this\y - cameray - ImageHeight(leftbubble) - 2
				
				cur$ = this\message
				lenght = GraphicsWidth() - xx - 20
				If lenght > 400 Then lenght = 400
				If StringWidth(cur$ + "...") > lenght
					While StringWidth(cur$ + "...") > lenght
						cur$ = Left$(cur$, Len(cur$) - 1)
						If Not Len(cur$) Exit
					Wend
					cur$ = cur$ + "..."
				EndIf
				
				w = StringWidth(cur$) + 17
				DrawImage leftbubble, xx, yy
				For i = 0 To (w - ImageWidth(rightbubble)) / ImageWidth(midbubble)
					DrawBlockRect midbubble, xx + ImageWidth(leftbubble) + i * ImageWidth(midbubble), yy, 0, 0, w - i * ImageWidth(midbubble) - ImageWidth(leftbubble) - ImageWidth(rightbubble), ImageHeight(midbubble)
				Next
				DrawImage rightbubble, xx + w - ImageWidth(rightbubble), yy
				
				For j = 0 To 5
					If j = 0 Then smile$ = ":)"
					If j = 1 Then smile$ = ":("
					If j = 2 Then smile$ = ":D"
					If j = 3 Then smile$ = ":P"
					If j = 4 Then smile$ = ":O"
					If j = 5 Then smile$ = ":S"
					
					a = 0
					occurs = 0
					While Instr(cur$, smile$, a + 1)
						a = Instr(cur$, smile$, a + 1)
						DrawImage lightemoticons, xx + 2 + StringWidth(Left$(cur$, a) + String$("  ", occurs)) - (StringWidth(smile$) * occurs), yy + 2, j
						occurs = occurs + 1
					Wend
					cur$ = Replace(cur$, smile$, "  ")
				Next
				
				Color 0, 0, 0
				Text xx + 10, yy + 2, cur$
			EndIf
		Next
		
		If (MilliSecs() - bubbletime) < 1000 + Len(bubbletext$) * 100
			xx = x - camerax + TileSize / 2;GraphicsWidth() / 2
			yy = y - cameray - ImageHeight(leftbubble) - 2;GraphicsHeight() / 2 - TileSize / 2 - ImageHeight(leftbubble) - 2
			
			Text 10, 10, "(" + bubbletext$ + ")"
			
			cur$ = bubbletext$
			lenght = GraphicsWidth() - xx - 20
			If lenght > 400 Then lenght = 400
			
			If StringWidth(cur$ + "...") > lenght
				While StringWidth(cur$ + "...") > lenght
					cur$ = Left$(cur$, Len(cur$) - 1)
					If Not Len(cur$) Exit
				Wend
				cur$ = cur$ + "..."
			EndIf
			
			w = StringWidth(cur$) + 17
			DrawImage leftbubble, xx, yy
			For i = 0 To (w - ImageWidth(rightbubble)) / ImageWidth(midbubble)
				DrawBlockRect midbubble, xx + ImageWidth(leftbubble) + i * ImageWidth(midbubble), yy, 0, 0, w - i * ImageWidth(midbubble) - ImageWidth(leftbubble) - ImageWidth(rightbubble), ImageHeight(midbubble)
			Next
			DrawImage rightbubble, xx + w - ImageWidth(rightbubble), yy
			
			For j = 0 To 5
				If j = 0 Then smile$ = ":)"
				If j = 1 Then smile$ = ":("
				If j = 2 Then smile$ = ":D"
				If j = 3 Then smile$ = ":P"
				If j = 4 Then smile$ = ":O"
				If j = 5 Then smile$ = ":S"
				
				a = 0
				occurs = 0
				While Instr(cur$, smile$, a + 1)
					a = Instr(cur$, smile$, a + 1)
					DrawImage lightemoticons, xx + 2 + StringWidth(Left$(cur$, a) + String$("  ", occurs)) - (StringWidth(smile$) * occurs), yy + 2, j
					occurs = occurs + 1
				Wend
				cur$ = Replace(cur$, smile$, "  ")
			Next
			
			Color 0, 0, 0
			Text xx + 10, yy + 2, cur$
		EndIf
	EndIf
End Function

Function DrawBar()
	Color 220, 220, 220
	DrawBlock header, 0, 0
	DrawBlock bar, 0, GraphicsHeight() - ImageHeight(bar)
	
	yy = 66 * (Float TopChat / ChatBufferSize)
	DrawBlock scroll, 576 - ImageWidth(scroll), (GraphicsHeight() - ImageHeight(bar) + 7) + yy
	
	connections = 1
	For this.connection = Each connection
		connections = connections + 1
	Next
	
	Text 25, 3, MapName$
	Text GraphicsWidth() / 2 + 25, 3, servername$
	Text GraphicsWidth() - 35, GraphicsHeight() - 90, connections
	Text GraphicsWidth() - 35, GraphicsHeight() - 68, Int(fps)
End Function

Function DrawChat()
	start = TopChat + 1
	For i = start To start + 4
		txt$ = chat(i, 4)
		yy = GraphicsHeight() - 45 - 12 * (start + 4 - i)
		
		For j = 0 To 5
			If j = 0 Then smile$ = ":)"
			If j = 1 Then smile$ = ":("
			If j = 2 Then smile$ = ":D"
			If j = 3 Then smile$ = ":P"
			If j = 4 Then smile$ = ":O"
			If j = 5 Then smile$ = ":S"
			
			a = 0
			occurs = 0
			While Instr(txt$, smile$, a + 1)
				a = Instr(txt$, smile$, a + 1)
				DrawImage darkemoticons, 2 + StringWidth(Left$(txt$, a) + String$("  ", occurs)) - (StringWidth(smile$) * occurs), yy, j
				occurs = occurs + 1
			Wend
			txt$ = Replace(txt$, smile$, "  ")
		Next
		
		Color chat(i, 1), chat(i, 2), chat(i, 3)
		Text 10, yy, txt$
	Next
	
	Color 200, 200, 200
	msg$ = message$
	
	While StringWidth(msg$ + "|") > 570
		msg$ = Right$(msg$, Len(msg$) - 1)
	Wend
	
	If MilliSecs() Mod 1000 < 500 msg$ = msg$ + "|"
	
	Text 10, GraphicsHeight() - 22, msg$
End Function

Function AddText(r, g, b, txt$)
	Repeat
		cur$ = Wraptext(txt$)
		txt$ = Mid$(txt$, Len(cur$) + 1)
		
		For i = 1 To ChatBufferSize - 1
			For j = 1 To 4
				chat(i, j) = chat(i + 1, j)
			Next
		Next
		
		chat(ChatBufferSize, 1) = r
		chat(ChatBufferSize, 2) = g
		chat(ChatBufferSize, 3) = b
		chat(ChatBufferSize, 4) = cur$
	Until txt$ = ""
End Function

Function WrapText$(txt$)
	Repeat
		cur$ = cur$ + Mid$(txt, 1, 1)
		txt$ = Mid$(txt, 2)
		
		If Right$(cur$, 1) = " " result$ = cur$
		If txt$ = "" 
			result$ = cur$
			Exit
		EndIf
	Until StringWidth(cur$) => 550
	
	If result$ = ""
		result$ = Left$(cur$, Len(cur$) - 1)
	EndIf
	
	Return result$
End Function

Function MouseOver()
	For this.connection = Each connection
		If RectsOverlap(this\x - camerax, this\y - cameray, TileSize, TileSize, MouseX(), MouseY(), 1, 1)
			
			w = 200
			If StringWidth("Name: " + this\name) + 20> w Then w = StringWidth("Name: " + this\name) + 20
			
			DrawInfobox(this\x - camerax + TileSize - 5, this\y - cameray - 5, w)
			Color 250, 250, 250
			Text this\x - camerax + TileSize + 5, this\y - cameray + 0 * 12, "Name: " + this\name
			Text this\x - camerax + TileSize + 5, this\y - cameray + 1 * 12, "Char: " + this\character
			Text this\x - camerax + TileSize + 5, this\y - cameray + 2 * 12, "IP: " + this\ip
			Exit
		EndIf
	Next
End Function

Function Inputs()
		key = GetKey()
		
		If key = 13
			If Not message$ = ""
				Message(message$)
				message$ = ""
			EndIf
		ElseIf key = 8
			If Len(message$) message$ = Left$(message$, Len(message$) - 1)
		Else
			If key => 32 And Len(message$) < 160
				message$ = message$ + Chr(key)
			EndIf
		EndIf
		
		If KeyDown(56) And KeyHit(28)
			w = GraphicsWidth()
			h = GraphicsHeight()
			d = GraphicsDepth()
			screenmode = 1 - screenmode
			Graphics w, h, d, 2 - screenmode
			SetBuffer BackBuffer()
			SetFont LoadFont("Courier New", 18, True)
			ShowPointer
			MoveMouse GraphicsWidth() / 2, GraphicsHeight() / 2
		EndIf
		
		If MouseHit(1)
			If MouseY() < GraphicsHeight() - ImageHeight(bar)
				mouseaction = 1
			ElseIf RectsOverlap(576 - ImageWidth(scroll), (GraphicsHeight() - ImageHeight(bar) + 7), ImageWidth(scroll), 66, MouseX(), MouseY(), 1, 1)
				mouseaction = 2
			Else
				mouseaction = 0
			EndIf
		EndIf
		If Not MouseDown(1) mouseaction = 0
		
		If KeyHit(1) TheEnd()
End Function

Function Message$(msg$)
	If Left$(msg$, 1) = "/"
		command$ = Mid$(msg$, 2, Instr(msg$, " ") - 2)
		parameter$ = Right$(msg$, Len(msg$) - Len(command$) - 2)
		Select command$
			Case "exit"
				CloseTCPStream stream
				End
			Case "name"
				If Not Len(parameter$) > 24
					If (Not name$ = parameter$) And Len(parameter$) > 0
						WriteLine stream, localhost$ + " Name:" + parameter$
					Else
						If Len(parameter$)
							AddText(200, 0, 0, "You already have that name!")
						Else
							AddText(200, 0, 0, "Too short name!")
						EndIf
					EndIf
				Else
					AddText(200, 0, 0, "Name can't be longer than 24 characters! You tried " + Len(parameter$) + " characters!")
				EndIf
			Default
				AddText(200, 0, 0, "Unknown command!")
		End Select
	Else
		WriteLine stream, localhost$ + " Say:" + msg$
	EndIf
End Function

Function UploadInfo()
	If TimerTicks(infotimer)
		WriteLine stream, localhost$ + " Info:" + x + ";" + y + ";" + minframe
		ResetTimer infotimer
	EndIf
End Function

Function ReceiveData()
	While ReadAvail(stream) > 8
		txt$ = ReadLine$(stream)
		CheckMessage(txt$)
	Wend
	
	If Eof(stream)
		Error("Connection to server has been lost!")
	EndIf
End Function

Function CheckMessage(msg$)
	from$ = Left$(msg$, Instr(msg$, " ") - 1)
	action$ = Mid$(msg$, Len(from$) + 2, Instr(msg$, ":", Len(from$)) - Len(from$) - 2)
	parameter$ = Right$(msg$, Len(msg$) - Len(from$) - 2 - Len(action$))
	
	If from$ = "Server"
		;Server commands
		Select Lower(action$)
			Case "kick"
				If parameter$ = localhost$ Or Lower(parameter$) = Lower(name$)
					EndGraphics
					If stream CloseTCPStream stream
					Error("You have been kicked from the server!")
				EndIf
				For this.connection = Each connection
					If this\ip = parameter$ Or this\name = parameter$
						AddText(200, 0, 0, "Server is kicking " + parameter$ + "!")
					EndIf
				Next
			Case "disconnect"
				For this.connection = Each connection
					If this\ip = parameter
						AddText(200, 0, 0, this\name + " has left the chat!")
						Delete this
					EndIf
				Next
			Case "ip"
				localhost$ = parameter$
				WriteLine stream, localhost$ + " Name:" + name$
				WriteLine stream, localhost$ + " Character:" + character$
				UploadInfo()
			Case "connected"
				AddText(0, 200, 0, parameter$ + " has joined!")
			Case "say"
				AddText(0, 0, 255, "Server: " + parameter$)
		End Select
		Return
	Else
		For this.connection = Each connection
			If this\ip = from$
				avail = True
				Exit
			EndIf
		Next
		If Not avail
			If Not from$ = localhost$ Or localhost$ = ""
				this.connection = New connection
					this\ip = from$
					this\tx = -TileSize * 5
					this\ty = -TileSize * 5
			EndIf
		EndIf
	EndIf
	
	;User transfers
	Select Lower(action$)
		Case "name"
			If from$ = localhost$ And (Not localhost$ = "") And (Not name$ = parameter$)Then
				AddText(0, 200, 0, name$ + " changed name to " + parameter$ + "!")
				name$ = parameter$
			EndIf
			For this.connection = Each connection
				If this\ip = from$ And (Not localhost$ = from$)
					If Not this\name$ = "" AddText(0, 200, 0, this\name + " changed name to " + parameter$ + "!")
					this\name = parameter$
				EndIf
			Next
		Case "character"
			For this.connection = Each connection
				If this\ip = from$
					this\character = parameter$
				EndIf
			Next
		Case "info"
			xx# = Left$(parameter$, Instr(parameter$, ";") - 1)
			yy# = Mid$(parameter$, Len(xx) + 2, Instr(parameter$, ";", Len(xx)))
			frm# = Right$(parameter$, Len(parameter$) - Len(xx) - Len(yy) - 2)
			
			For this.connection = Each connection
				If this\ip = from$
					If this\tx < 0 And this\ty < 0
						this\x = xx
						this\y = yy
					EndIf
					this\tx = xx
					this\ty = yy
					this\minframe = frm
				EndIf
			Next
		Case "say"
			If from$ = localhost$
				AddText(200, 200, 200, name$ + ": " + parameter$)
				bubbletime = MilliSecs()
				bubbletext$ = parameter$
			EndIf
			For this.connection = Each connection
				If this\ip = from$
					If this\x > camerax And this\y > cameray And this\x < camerax + GraphicsWidth() - TileSize And this\y < cameray + GraphicsHeight() - TileSize
						this\messagetime = MilliSecs()
						this\message = parameter$
						AddText(200, 200, 200, this\name + ": " + parameter$)
					EndIf
				EndIf
			Next
		Default
			AddText(200, 0, 0, "Unknown message: " + msg$)
	End Select

End Function

Function GetAngle(x, y, targetx, targety)
	If targety<y Then
		If targetx<x Then
			Return Abs(ATan2(targetx-x,targety-y))+90
		Else
			Return 270+(180-Abs(ATan2(targetx-x,targety-y)))
		EndIf
	Else If targety=>y Then
		Return Abs(ATan2(targetx-x,targety-y)-90)
	EndIf
End Function

Function GetLenght(x, y, xx, yy)
	Return ((x - xx)^2 + (y - yy)^2)
End Function

Function DrawInfobox(x, y, w)
	DrawImage infoleft, x, y
	For i = 0 To (w - ImageWidth(inforight)) / ImageWidth(infomid)
		DrawBlockRect infomid, x + ImageWidth(infoleft) + i * ImageWidth(infomid), y, 0, 0, w - i * ImageWidth(infomid) - ImageWidth(infoleft) - ImageWidth(inforight), ImageHeight(infomid)
	Next
	DrawImage inforight, x + w - ImageWidth(inforight), y
End Function

Function Blocked(x, y)
	For this.map = Each map
		If this\x = Int(x) And this\y = Int(y)
			If this\attribute = 1
				Return True
			Else
				Return False
			EndIf
		EndIf
	Next
End Function

Function Error(txt$)
	Notify txt$, 1
	If Not MainWindow End
	If LoadingWindow
		FreeGadget LoadingWindow
		LoadingWindow = False
	EndIf
End Function

Function TheEnd()
	If stream CloseTCPStream stream
	End
End Function
