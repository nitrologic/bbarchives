; ID: 1583
; Author: Perturbatio
; Date: 2005-12-29 05:45:07
; Title: OpenConsole
; Description: A simple reconfigurable console for use in your BMax apps

Rem
Openconsole Version 0.1.0
created by Kris Kelly (Perturbatio) (C) 2005

TODO:
	Add text wrapping
	Add output scrolling
	Use TAB to autocomplete a command
	Perhaps turn it into a module?
EndRem
SuperStrict

Import pert.appfuncs 'if you don't have this module remember to comment this line
					  'and the SetAppTitle command below

Function SplitString:TList(inString:String, Delim:String)
	Local tempList : TList = New TList
	Local currentChar : String = ""
	Local count : Int = 0
	Local TokenStart : Int = 0
	
	If Len(Delim)<>1 Then Return Null
	
	inString = Trim(inString)
	
	For count = 0 Until Len(inString)
		If inString[count..count+1] = delim Then
			tempList.AddLast(inString[TokenStart..Count])
			TokenStart = count + 1
		End If
'		
	Next
	tempList.AddLast(inString[TokenStart..Count])	
	Return tempList
End Function


Function GetKey:Int()
	Const RepeatRate : Int = 100 'ms
	Global LastTime:Long = MilliSecs()
	Global LastKey:Int = 0
	Local key:Int = 0
		
	While  Key <= 226
		If KeyDown(key) Then Exit
		Key:+ 1
	Wend
	
	
	
	If Key = 227 Then Return 0
	
	If Key = LastKey Then
		If MilliSecs()-LastTime < RepeatRate Then Return 0
	EndIf
	
	LastTime = MilliSecs()
	LastKey = Key
	If Key < 227 Then Return Key Else Return 0
	
End Function


Type TRGB
	Field Red		: Int 'could use byte but int is faster
	Field Green		: Int
	Field Blue		: Int
	Field Alpha		: Int
	
	Method AsInt:Int()
		Return ( Red| (Green Shl 8) | (Blue Shl 16))
	End Method
	
	Method FromInt(col:Int)
		Red = col Shr 16 & $FF
		Green = col Shr 8  & $FF
		Blue = col & $FF
	End Method
	
	Method SetCol()
		SetColor Red, Green, Blue
	End Method
End Type


Type TOpenConsole
	Field _Visible			: Int
	Field _X				: Float
	Field _Y				: Float
	Field _Width			: Float
	Field _Height			: Float
	Field _FontCol			: TRGB
	Field _BGCol			: TRGB
	Field CmdProcessor		: TCommandProcessor
	Field _BorderWidth		: Int = 4
	Field HotKey			: Int = KEY_TAB
	Field HotCtrlKey		: Int = KEY_LCONTROL
	Field Alpha				: Float = 0.75
	Field TextAlpha			: Float = 1.0
	Field History			: TList
	Field HistoryPos		: Int = -1 'represents the position in the History List (-1 means no position)
	Field InputBuffer		: String
	Field CursorPos			: Int = 0
	Field OutList			: TList
	Field OutListPos		: Int = 0
	
	
	'Get Field Methods
	Method Visible:Int()
		Return _Visible
	End Method
	
	Method X:Float()
		Return _X
	End Method
	
	Method Y:Float()
		Return _Y
	End Method
	
	Method Width:Float()
		Return _Width
	End Method
	
	Method Height:Float()
		Return _Height
	End Method
	
	
	'Set Field Methods
	Method SetVisible(val:Int)
		_Visible = val
	End Method
	
	Method SetX(val:Float)
		_X = val
	End Method
	
	Method SetY(val:Float)
		_Y = val
	End Method
	
	Method SetWidth(val:Float)
		_Width = val
	End Method
	
	Method SetHeight(val:Float)
		_Height = val
	End Method
	

	Method Draw()
		Local LineHeight:Int = (TextHeight(InputBuffer)+2)
		'Draw BG
		SetBlend(AlphaBlend)
		SetAlpha(Alpha)
		_BGCol.SetCol()
		DrawRect(X(), Y(), Width(), Height())
		
		'Draw Text
		SetAlpha(TextAlpha)
		_FontCol.SetCol()
		SetViewport(X() + _BorderWidth, Y() + _BorderWidth, Width() - (_BorderWidth Shl 1), Height() - (_BorderWidth Shl 1))
		SetOrigin(X() + _BorderWidth, Y() + _BorderWidth)
		
		DrawText(InputBuffer, 0, 0)
		
		'Draw Cursor
		DrawText("_", TextWidth(InputBuffer[..CursorPos]), 0)
		DrawText("_", TextWidth(InputBuffer[..CursorPos]), 1)
		DrawText("_", TextWidth(InputBuffer[..CursorPos]), 2)
		
		'Draw Command Output
		If OutList.Count() > 0 Then
			For Local count:Int = OutListPos To OutList.Count()-1
				DrawText(String(OutList.ValueAtIndex(count)), 0, LineHeight+(LineHeight * count))
				
			Next
		EndIf
		'restore viewport and origin
		SetViewport(0,0,GraphicsWidth(), GraphicsHeight())
		SetOrigin(0,0)
		SetAlpha(1.0)
		
	End Method
	
	
	Method Update()
		If Visible() Then Draw()
		CheckInput()
	End Method
	
	
	Method CheckInput()
		Local ch:Int
		
		'visibility of console
		If Not Visible() Then 
		
			If KeyHit(HotKey) And KeyDown(HotCtrlKey) Then 
				SetVisible(True)
				FlushKeys()
			EndIf
			
			Return
		EndIf
		
		If KeyHit(HotKey) And KeyDown(HotCtrlKey) Then
			SetVisible(False)
		EndIf
		
		'Main input section
		
		ch = GetChar()
		
		If (ch > 31) And (ch < 126) Then 
			InputBuffer = InputBuffer[..CursorPos]+Chr(ch)+InputBuffer[CursorPos..]':+Chr(ch)
			CursorPos:+1
		EndIf
		
		Select ch
			Case 8 'Backspace
				If CursorPos > 0 Then
					InputBuffer = InputBuffer[..CursorPos-1]+InputBuffer[CursorPos..] 'backspace
					CursorPos:-1
				EndIf
			
			Case 13 'Return
				If Len(InputBuffer)>0 Then
					History.AddFirst(InputBuffer)
					CmdProcessor.Execute(InputBuffer)
				EndIf
				InputBuffer = "" 'return
				CursorPos = 0
				HistoryPos = -1
							
			Case 27 'Escape
				InputBuffer = "" 'escape
				CursorPos = 0
				HistoryPos = -1
				
		End Select
		
		Local SpecialKey:Int = GetKey()
			
		Select SpecialKey
			Case KEY_LEFT 
				If CursorPos>0 Then CursorPos:- 1
				
			Case KEY_RIGHT
				If CursorPos < Len(InputBuffer) Then CursorPos:+ 1
				
			Case KEY_UP
				If History.Count() > 0 Then
					If HistoryPos < History.Count()-1 Then HistoryPos:+ 1
					InputBuffer = String(History.ValueAtIndex(HistoryPos))
					CursorPos = Len(InputBuffer)
				EndIf
				
			Case KEY_DOWN
				If History.Count() > 0 Then
					If HistoryPos > -1 Then HistoryPos:- 1
					If HistoryPos < 0 Then 
						InputBuffer = ""
					Else
						InputBuffer = String(History.ValueAtIndex(HistoryPos))
					EndIf
					CursorPos = Len(InputBuffer)
				EndIf
				
			Case KEY_DELETE
				If CursorPos < Len(InputBuffer) Then
					InputBuffer = InputBuffer[..CursorPos]+InputBuffer[CursorPos+1..]
				EndIf
				
			Case KEY_END
				CursorPos = Len(InputBuffer)
				
			Case KEY_HOME
				CursorPos = 0
				
			Case KEY_TAB
				'find next command that begins with the current InputBuffer text (i.e. if the user has typed "Cle" then show Clear)
				'NEED TO IMPLEMENT LATER
		End Select
			
	End Method
	
	
	Method Out(outString:String)
		OutList.AddFirst(outString)
	End Method
	
	
	Function Create:TOpenConsole(Visible:Int, X:Float, Y:Float, Width:Float, Height:Float, FontCol:Int = $FFFFFF, BGCol:Int = $000000)
		Local tempConsole:TOpenConsole = New TOpenConsole
			tempConsole.CmdProcessor = TCommandProcessor.Create()
				tempConsole.CmdProcessor.Parent = tempConsole
			tempConsole.SetVisible(Visible)
			tempConsole.SetX(X)
			tempConsole.SetY(Y)
			tempConsole.SetWidth(Width)
			tempConsole.SetHeight(Height)
			
			tempConsole._FontCol:TRGB = New TRGB
				tempConsole._FontCol.FromInt(FontCol)
						
			tempConsole._BGCol:TRGB = New TRGB
				tempConsole._BGCol.FromInt(BGCol)
				
			tempConsole.History:TList = New TList
			
			tempConsole.OutList:TList = New TList
		
		Return tempConsole
	End Function
End Type


'Parameter Types
Const ptByte		: Int = 0
Const ptInteger		: Int = 1
Const ptFloat		: Int = 2
Const ptString		: Int = 3



Type TCommand
	Field Command	: String
	Field Action(Owner:TOpenConsole Var, params:TList Var)
	Field HelpText : String
	
	
	Function Create:TCommand(cmd:String, Action(Owner:TOpenConsole Var, params:TList Var), HelpTxt:String)
		If Len(cmd)=0 Then Return Null
		
		
		
		Local tempCommand:TCommand = New TCommand
			tempCommand.Command = cmd
			tempCommand.Action = Action
			tempCommand.HelpText = HelpTxt
		Return tempCommand
		
	End Function
	
End Type


Type TCommandProcessor
	Field CommandList	: TList
	Field Parent:TOpenConsole
	
	Method Execute(cmd:String)
		?Debug
			Print "Command String: " + cmd
			Print "Command List Count: " + CommandList.Count()
		?
		Local cmdList:TList = SplitString(cmd, " ")
		
		?Debug
			Print "cmdList val 0: " + String(cmdList.ValueAtIndex(0))
			Print "Command List Val 0 String:" + String(TCommand(CommandList.ValueAtIndex(0)).Command).ToUpper()
		?
		Local command:TCommand = GetCommand(String(cmdList.ValueAtIndex(0)))
		
		If command <> Null Then
			?Debug
				Print "Command: "+command.command
				Print "Help: " + command.HelpText
			?
			command.Action(Parent, cmdList)
		Else
			Parent.Out("Command not found: " + String(cmdList.ValueAtIndex(0)))
		EndIf
	End Method
	
	Method RegisterCommand(cmd:TCommand)
		If Not GetCommand(cmd.command) Then
			
			ListAddLast(CommandList, cmd)
			
		EndIf
	End Method
	
	
	Method GetCommand:TCommand(cmd:String)	
		?Debug
			Print "Entered GetCommand with " + cmd
		?
		'iterate through command list and check each TCommand entry for cmd$
		If CommandList.Count() < 1 Then Return Null
		
		?Debug
			Print "commandList count  greater than 0"
		?
		
		For Local tempCommand:TCommand = EachIn CommandList
			?Debug
				Print "Iterating through CommandList"
				Print tempCommand.Command.ToUpper() + " ------ " + cmd.ToUpper()
			?
			If tempCommand.Command.ToUpper() = cmd.ToUpper() Then 
				Return tempCommand
			EndIf
			
		Next
		Return Null
	End Method
	
	
	Function Create:TCommandProcessor()
		Local tempCommandProcessor:TCommandProcessor = New TCommandProcessor
			tempCommandProcessor.CommandList:TList = New TList
		Return tempCommandProcessor
	End Function
End Type


''''''''''''''''''''
''''''''''''''''''''
''''''' TEST '''''''
''''''''''''''''''''
''''''''''''''''''''


Graphics 640,480,0,0

Global con:TOpenConsole = TOpenConsole.Create(False, 0,0,640,200, $99FF99, $006600)
	con.OutList.AddLast("Press CTRL+TAB to show/hide console")

'COMMAND SetConsoleAlpha
Global cmdSetConsoleAlpha:TCommand = TCommand.Create("SetConsoleAlpha", cmdSetConsoleAlpha_Action, "Set the alpha value of the console")
	
	Function cmdSetConsoleAlpha_Action(Owner:TOpenConsole Var, params:TList Var)
		Local val:Float
		
		If params.Count() >1 Then
			val = String(params.ValueAtIndex(1)).ToFloat()
			
			If val => 0.1 And val <= 1.0 Then
				owner.Alpha = val
			Else
				Owner.Out("Error: alpha value range from 0.1 to 1.0")
			EndIf
		Else
			Owner.Out("Error: alpha value is not optional")
		EndIf
	End Function

	con.cmdProcessor.RegisterCommand(cmdSetConsoleAlpha)



'COMMAND Quit
Global cmdQuit:TCommand = TCommand.Create("Quit", cmdQuit_Action, "Quit the program")

	Function cmdQuit_Action(Owner:TOpenConsole Var, params:TList Var)
		End
	End Function

	con.cmdProcessor.RegisterCommand(cmdQuit)



'COMMAND ListCommands
Global cmdListCommands:TCommand = TCommand.Create("ListCommands", cmdListCommands_Action, "List all the commands")

	Function cmdListCommands_Action(Owner:TOpenConsole Var, params:TList Var)
		For Local tempCommand:TCommand = EachIn Owner.cmdProcessor.CommandList
			Owner.Out(tempCommand.Command + " - " + tempCommand.HelpText)
			
		Next
	End Function

	con.cmdProcessor.RegisterCommand(cmdListCommands)



'COMMAND Help
Global cmdHelp:TCommand = TCommand.Create("Help", cmdHelp_Action, "Provides help on the specified command (USAGE: Help <command>)")
	
	Function cmdHelp_Action(Owner:TOpenConsole Var, params:TList Var)
		'if too many parameters passed
		If params.Count() > 2 Then 
			Owner.Out(cmdHelp.HelpText)
			Owner.Out("Maximum of one parameter allowed")
		EndIf
		
		'if one parameter passed then look it up
		If Params.Count()>1 Then
			Local paramString:String = String(Params.ValueAtIndex(1))
			Local tempCommand:TCommand = Owner.cmdProcessor.GetCommand(ParamString)
		
			If tempCommand <> Null Then		
				If Len(tempCommand.HelpText)>0 Then
					owner.out(tempCommand.HelpText)
				Else
					owner.out("No help found for " + ParamString)
				EndIf
			Else
				owner.out("Command not found: " + ParamString)
			EndIf
		Else 'if no parameters passed, return the Help for cmdHelp
			Owner.Out("Type ListCommands for a full list of all commands")
			Owner.Out(cmdHelp.HelpText)
		EndIf
	End Function
	
	con.cmdProcessor.RegisterCommand(cmdHelp)



'COMMAND ClearHistory
Global cmdClearHistoryList:TCommand = TCommand.Create("ClearHistory", cmdClearHistoryList_Action, "Clears the command history.")

	Function cmdClearHistoryList_Action(Owner:TOpenConsole Var, params:TList Var)
		If Params.Count()>1 Then 
			If String(Params.ValueAtIndex(1)) = "/?" Then
				 Owner.Out(cmdClearHistoryList.HelpText)
				Return
			EndIf
		EndIf
		Owner.History.Clear()
		Owner.HistoryPos = -1
		Owner.Out("History cleared.")
	End Function

	con.cmdProcessor.RegisterCommand(cmdClearHistoryList)



'COMMAND Cls
Global cmdClearOutList:TCommand = TCommand.Create("Cls", cmdClearOutList_Action, "Clears the output buffer.")

	Function cmdClearOutList_Action(Owner:TOpenConsole Var, params:TList Var)
		Owner.OutList.Clear()
		Owner.OutListPos = 0
	End Function

	con.cmdProcessor.RegisterCommand(cmdClearOutList)


	con.SetVisible(True)


Global CurrentMem:Int
Global LastMem : Int


While Not (KeyDown(KEY_ESCAPE) And (KeyDown(KEY_LSHIFT) Or KeyDown(KEY_RSHIFT)))
	CurrentMem = GCMemAlloced()
	If (CurrentMem < (LastMem - 18000)) Or (CurrentMem > (LastMem + 18000)) Then
		'SetAppTitle is in AppFuncs.mod ( http://www.blitzbasic.com/Community/posts.php?topic=43341 )
		'just comment this part out If you don't have it
		SetAppTitle("Open Console test - Press Shift+Escape to Quit " + CurrentMem)
		LastMem = CurrentMem
	EndIf
	Cls
		'DRAW A BLOCK TO SHOW THE ALPHA
		SetColor(255,0,0)
		DrawRect(10,10,600,250)	
		
		con.Update()
		'DrawText(CurrentMem, 0,GraphicsHeight()-20)
	Flip

Wend
End
