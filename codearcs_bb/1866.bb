; ID: 1866
; Author: JoshK
; Date: 2006-11-18 15:46:31
; Title: Load a menu from a file
; Description: Loads a menu (*.mnu) file and creates a window menu

Const MaxMenuItems=99

Global menuitem:TGadget[MaxMenuItems]

Const MENU_ABOUT=1
Const MENU_EXIT=2
Const MENU_CONSOLE=3
Const MENU_CONSOLE_COPY=23
Const MENU_CONSOLE_SELECTALL=24
Const MENU_CONSOLE_STARTLOG=27
Const MENU_CONSOLE_STOPLOG=28
Const MENU_CONSOLE_SAVE=19
Const MENU_CONSOLE_CLOSE=20
Const MENU_FULLSCREEN=21

Function LoadMenu:Int(file$,window:TGadget)

	root:TGadget=WindowMenu(window)
	If Not root Return

	f:TStream=ReadFile(file)
	If Not f Return
	
	For menu:TGadget=EachIn root.kids
		FreeMenu menu
	Next

	If Not root Return
	For n=0 To MaxMenuItems-1
		MenuItem[n]=Null
	Next
	
	While Not f.eof()
		s$=f.readline()
		s=Trim(s)
		If Left(s,1)="[" And Right(s,1)="]"
			title$=Mid(s,2,Len(s)-2)
			item:TGadget=Null
			menu:TGadget=CreateMenu(title,0,root)
			parent:TGadget=menu
		Else
			p=Instr(s," ")
			If p
				command$=Left(s,p-1)
			Else
				command=s
			EndIf
			command=Trim(command)
			command=Lower(command)
			If p
				params$=Right(s,Len(s)-p)
			Else
				params=""
			EndIf
			Select command
				Case "<>"
					If parent CreateMenu("",0,parent)
				Case "{"
					If item:TGadget<>Null parent=item
				Case "}"
					parent=menu
				Case "menu"
					If parent
						text$=Trim((piece(params,1,",")))
						If Not Instr(text,Chr(34)) text=""
						text=Replace(text,Chr(34),"")
						tagname$=piece(params,2,",")
						tag=GetMenuAction(tagname)
						hotkeyname$=Trim((piece(params,3,",")))
						hotkey=KeyCode(hotkeyname)
						modifier=Int(piece(params,4,","))
						item:TGadget=CreateMenu(text,tag,parent,hotkey,modifier)
						menuitem[tag]=item
						extrahotkey=0
						Select tag
							Case KEY_0 extrahotkey=KEY_NUM0
							Case KEY_1 extrahotkey=KEY_NUM1
							Case KEY_2 extrahotkey=KEY_NUM2
							Case KEY_3 extrahotkey=KEY_NUM3
							Case KEY_4 extrahotkey=KEY_NUM4
							Case KEY_5 extrahotkey=KEY_NUM5
							Case KEY_7 extrahotkey=KEY_NUM7
							Case KEY_8 extrahotkey=KEY_NUM8
							Case KEY_9 extrahotkey=KEY_NUM9
							
							Case KEY_SLASH extrahotkey=KEY_NUMDIVIDE
							
							Case KEY_PERIOD extrahotkey=KEY_NUMDECIMAL
							Case KEY_EQUALS extrahotkey=KEY_NUMADD
							Case KEY_MINUS extrahotkey=KEY_NUMSUBTRACT					
						EndSelect
						If extrahotkey SetHotKeyEvent(extrahotkey ,modifier,CreateEvent(EVENT_MENUACTION,Null,tag))
					EndIf
			EndSelect
		EndIf
	Wend
	
	f.close()
	UpdateWindowMenu window
	Return True
EndFunction

Function GetMenuAction:Int(actionname$)
	actionname=Upper(actionname)
	actionname=Trim(actionname)
	Select actionname
		Case "ABOUT" Return MENU_ABOUT
		Case "FULLSCREENMODE" Return MENU_FULLSCREEN
		Case "EXIT" Return MENU_EXIT
	EndSelect
EndFunction

Function KeyCode:Int(keyname$)
	keyname=Upper(keyname)
	keyname=Trim(keyname)
	Select keyname
	
		Case "A" Return KEY_A
		Case "B" Return KEY_B
		Case "C" Return KEY_C
		Case "D" Return KEY_D
		Case "E" Return KEY_E
		Case "F" Return KEY_F
		Case "G" Return KEY_G
		Case "H" Return KEY_H
		Case "I" Return KEY_I
		Case "J" Return KEY_J
		Case "K" Return KEY_K
		Case "L" Return KEY_L
		Case "M" Return KEY_M
		Case "N" Return KEY_N
		Case "O" Return KEY_O
		Case "P" Return KEY_P
		Case "Q" Return KEY_Q
		Case "R" Return KEY_R
		Case "S" Return KEY_S
		Case "T" Return KEY_T
		Case "U" Return KEY_U
		Case "V" Return KEY_V
		Case "W" Return KEY_W
		Case "X" Return KEY_X
		Case "Y" Return KEY_Y
		Case "Z" Return KEY_Z
		
		Case "0" Return KEY_0
		Case "1" Return KEY_1
		Case "2" Return KEY_2
		Case "3" Return KEY_3
		Case "4" Return KEY_4
		Case "5" Return KEY_5
		Case "6" Return KEY_6
		Case "7" Return KEY_7
		Case "8" Return KEY_8
		Case "9" Return KEY_9

		Case "F1" Return KEY_F1
		Case "F2" Return KEY_F2
		Case "F3" Return KEY_F3
		Case "F4" Return KEY_F4
		Case "F5" Return KEY_F5
		Case "F6" Return KEY_F6
		Case "F7" Return KEY_F7
		Case "F8" Return KEY_F8
		Case "F9" Return KEY_F9
		Case "F10" Return KEY_F10
		Case "F11" Return KEY_F11
		Case "F12" Return KEY_F12

		Case "SPACE" Return KEY_SPACE
		Case "ESCAPE" Return KEY_ESCAPE
		Case "BACKSPACE" Return KEY_BACKSPACE
		Case "CLEAR" Return KEY_CLEAR
		Case "RETURN" Return KEY_RETURN
		Case "ENTER" Return KEY_ENTER
		Case "PAUSE" Return KEY_PAUSE

		Case "CAPSLOCK" Return KEY_CAPSLOCK
		Case "PAGEUP" Return KEY_PAGEUP
		Case "PAGEDOWN" Return KEY_PAGEDOWN
		Case "END" Return KEY_END
		Case "HOME" Return KEY_HOME
		Case "LEFT" Return KEY_LEFT
		Case "RIGHT" Return KEY_RIGHT
		Case "UP" Return KEY_UP
		Case "DOWN" Return KEY_DOWN
		Case "SELECT" Return KEY_SELECT
		Case "PRINT" Return KEY_PRINT
		Case "EXECUTE" Return KEY_EXECUTE
		Case "SCREEN" Return KEY_SCREEN
		Case "INSERT" Return KEY_INSERT
		Case "DELETE" Return KEY_DELETE
		Case "HELP" Return KEY_HELP
		Case "NUMLOCK" Return KEY_NUMLOCK
		Case "SCROLL" Return KEY_SCROLL
		Case "-","_" Return KEY_MINUS
		Case "="."+" Return KEY_EQUALS
		Case "[","{" Return KEY_OPENBRACKET
		Case "]","}" Return KEY_CLOSEBRACKET
		Case "\","|" Return KEY_BACKSLASH
		Case ";",":" Return KEY_SEMICOLON
		Case "'",Chr(34) Return KEY_QUOTES
		Case ",","<" Return KEY_COMMA
		Case ".".">" Return KEY_PERIOD
		Case "/","?" Return KEY_SLASH

	EndSelect
EndFunction

Function Piece$(s$,entry,char$=" ")
	Local n
	Local p
	Local a$
	While Instr(s,char+char)
		s=Replace(s,char+char,char)
	Wend
	For n=1 To entry-1
		p=Instr(s,char)
		s=Right(s,Len(s)-p)
		If Not p
			If entry=n Exit Else Return
		EndIf
	Next
	p=Instr(s,char)
	If p<1
		a$=s
	Else
		a=Left(s,p-1)
	EndIf
	Return a
End Function
