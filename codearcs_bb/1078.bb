; ID: 1078
; Author: aCiD2
; Date: 2004-06-07 16:22:46
; Title: Human Interface Lib
; Description: VERY Simple 'Keybinding' System

; -----------------------------------------------------------------------------------------------------
;
; The NuClear Fusion Game Engine
;		Human Interface Library
;	
; -----------------------------------------------------------------------------------------------------
;
; This library is the support for input. It allows better input of devices than blitz supports because
; the hit mouse buttons, joystick buttons or keyboard kets are stored until the next HINT_Update is
; called.
;
; Usage
;	Usage of this library is simple. You first define which keys do what using the HIO_DefineAction
;	function (allows for keyboard, joystick and mouse support) and then at the end, or start of every
;	loop call 'HINT_Update.' Now, you can use HINT_CheckAction to check
;	an action.
;
; Thanks to..
;	Andreas|Blixt - Nice simple XML loading, taken from a verlet 2D demo you made.
;
; -----------------------------------------------------------------------------------------------------

Global HINT_Version$ = "1.0.00"

Const HINT_INPUTTYPE_MOUSE = 1
Const HINT_INPUTTYPE_KEYBOARD = 2
Const HINT_INPUTTYPE_JOYSTICK = 3

Const HINT_MODE_UP = 0
Const HINT_MODE_HIT = 1
Const HINT_MODE_DOWN = 2
Const HINT_MODE_RELEASE = 3

Dim HINT_Keys(237)

Type HINT_Action
	Field Name$
	Field Key
	Field DownMode
	Field ActionType
End Type

; -----------------------------------------------------------------------------------------------------
; These functions are used for the human interface library
; -----------------------------------------------------------------------------------------------------
Function HINT_CheckAction(Name$)

	Local Action.HINT_Action = HINT_FindAction_Name(Name)
	Return Action\DownMode
	
End Function

Function HINT_DefineAction(Name$, ActionCode, ActionType = HINT_INPUTTYPE_KEYBOARD)
	
	Local Action.HINT_Action = New HINT_Action
	Action\Name = Name
	Action\Key = ActionCode
	Action\ActionType = ActionType
	
End Function

Function HINT_FindAction_Name.HINT_Action(Name$)
	
	Local Action.HINT_Action
	
	For Action = Each HINT_Action
		If Action\Name = Name Then Return Action
	Next
	
End Function

Function HINT_Update()

	Local Action.HINT_Action

	For Action = Each HINT_Action
		Select Action\ActionType
			Case HINT_INPUTTYPE_MOUSE
				Select Action\DownMode
					Case HINT_MODE_UP	If MouseDown(Action\Key) Then Action\DownMode = HINT_MODE_HIT
					Case HINT_MODE_HIT	If MouseDown(Action\Key) Then Action\DownMode = HINT_MODE_DOWN Else Action\DownMode = HINT_MODE_UP
					Case HINT_MODE_DOWN	If Not MouseDown(Action\Key) Then Action\DownMode = HINT_MODE_RELEASE
					Case HINT_MODE_RELEASE Action\DownMode = HINT_MODE_UP
				End Select
		End Select
	Next

End Function

; -----------------------------------------------------------------------------------------------------
; These functions are used for loading human interface data from config files (in xml format)
; -----------------------------------------------------------------------------------------------------
Function HINT_XML_GetAttribute$(xmlline$,attr$)
	Local pos1%,pos2%,temp$,name$,value$

	If Left(xmlline,1) <> "<" Or Right(xmlline,1) <> ">" Then Return ""
	xmlline = Mid(xmlline,2,Len(xmlline) - 2)

	attr = Lower(attr)

	pos2 = Instr(xmlline," ")
	Repeat
		pos1 = pos2
		pos2 = Instr(xmlline," ",pos1 + 1)

		If pos1 > 0 And pos2 = 0
			pos2 = Len(xmlline)
			While Mid(xmlline,pos2,1) <> Chr(34)
				If pos2 <= 1 Then Exit
				pos2 = pos2 - 1
			Wend
			pos2 = pos2 + 1
		ElseIf pos1 = 0 And pos2 = 0
			Exit
		EndIf

		temp = Mid(xmlline,pos1 + 1,pos2 - pos1 - 2)
		If Instr(temp,"=")
			name = Left(temp,Instr(temp,"=") - 1)
			value = Mid(temp,Instr(temp,"=") + 2)
			If Lower(name) = attr Then Return value
		EndIf
	Forever

	Return ""
End Function

Function HINT_XML_GetTag$(ParseString$)
	
	Local Pos
	
	ParseString = Trim(ParseString)
	
	; Check its valid
	If Left(ParseString, 1) <> "<" Or Right(ParseString, 1) <> ">" Then Return ""
	If Len(ParseString) < 3 Then Return ""
	
	
	; Get where the end of the tag is.
	Pos = Instr(ParseString, " ")
	If Pos = 0 Or Pos > Instr(ParseString, ">") Then Pos = Instr(ParseString, ">")
	If Pos = 0 Then Return ""
	
	; Return the tag name
	Return Lower$(Mid$(ParseString, 2, Pos - 2))
	
End Function

Function HINT_XML_LoadConfig(File$)
	
	Local FilePointer, CurrentLine$, Action.HINT_Action

	FilePointer = OpenFile(File)
	If FilePointer = 0 Then DebugLog "[ Human Interface Error!!! XML Loading ] The requested file cannot be accessed, check its existance. '" + File + "'": Return False
	
	While Not Eof(FilePointer)
	
		CurrentLine = ReadLine(FilePointer)
		DebugLog HINT_XML_GetTag(CurrentLine)
		Select HINT_XML_GetTag(CurrentLine)
			Case "keyboardkey"
				Action = New HINT_Action
				Action\Name = HINT_XML_GetAttribute(CurrentLine, "Name")
				Action\Key = HINT_XML_GetAttribute(CurrentLine, "Keycode")
				Action\ActionType = HINT_INPUTTYPE_KEYBOARD
				
			Case "mousebutton"
				Action = New HINT_Action
				Action\Name = HINT_XML_GetAttribute(CurrentLine, "Name")
				Action\Key = HINT_XML_GetAttribute(CurrentLine, "Mousebutton")
				Action\ActionType = HINT_INPUTTYPE_MOUSE
				
			Case "joystick"
				Action = New HINT_Action
				Action\Name = HINT_XML_GetAttribute(CurrentLine, "Name")
				Action\Key = HINT_XML_GetAttribute(CurrentLine, "Joybutton")
				Action\ActionType = HINT_INPUTTYPE_JOYSTICK
		End Select
	
	Wend
	
End Function

Function HINT_XML_SaveConfig(File$)

	Local FilePointer, CurrentLine$, Action.HINT_Action

	FilePointer = WriteFile(File)
	If FilePointer = 0 Then DebugLog "[ Human Interface Error!!! XML Loading ] The requested file cannot be accessed, check its existance and write propeties. '" + File + "'": Return False
	
	For Action = Each HINT_Action
		
		Select Action\ActionType
			Case HINT_INPUTTYPE_MOUSE WriteLine FilePointer, "<MouseButton Name=" + Chr(34) + Action\Name + Chr(34) + " MouseButton=" + Chr(34) + Action\Key + Chr(34) + ">"
			Case HINT_INPUTTYPE_KEYBOARD WriteLine FilePointer, "<Keyboard Name=" + Chr(34) + Action\Name + Chr(34) + " Keycode" + Chr(34) + Action\Key + Chr(34) + ">"
			Case HINT_INPUTTYPE_KEYBOARD WriteLine FilePointer, "<Joystick Name=" + Chr(34) + Action\Name + Chr(34) + " Joybutton" + Chr(34) + Action\Key + Chr(34) + ">"
		End Select
		
	Next

End Function

; -----------------------------------------------------------------------------------------------------
; -----------------------------------------------------------------------------------------------------
; -----------------------------------------------------------------------------------------------------
; Example

SetBuffer BackBuffer()
Stop
HINT_XML_LoadConfig("Test.Config")

While Not KeyHit(1)
	
	Cls
	
	For a.hint_action = Each hint_action
		Text 0, y, a\name
	Next
	
	;Stop
	HINT_Update
	
	;If HINT_CheckAction("Click") = 3 Then RuntimeError ""
	
	Flip
	
Wend
