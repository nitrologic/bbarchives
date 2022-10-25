; ID: 2545
; Author: SebHoll
; Date: 2009-07-24 20:04:50
; Title: MSN Messenger Music/Game Hint API
; Description: Set the user's MSN 'Show what I'm listening to/doing' status to your own track info/game name.

SuperStrict

'Save this as a BMX file and import it into your source code.
'e.g. SetMessengerInfo("My Song Name - My Artist")

Public

Const MSNICON_MUSIC:Int = 0		'Text will become a song link.
Const MSNICON_OFFICE:Int = 1
Const MSNICON_GAMES:Int = 2

Function SetMessengerInfo:Int( pText$, pType:Int = MSNICON_MUSIC )
	
	?Win32
	
	Local tmpTypeString:String = "Music"
	Local tmpEnable:String = "1"
	
	If Not pText Then tmpEnable = "0"
	
	Select pType
		Case MSNICON_MUSIC';tmpTypeString = "Music"
		Case MSNICON_OFFICE;tmpTypeString = "Office"
		Case MSNICON_GAMES;tmpTypeString = "Games"
	EndSelect
	
	Local tmpString$ = "\0" + tmpTypeString + "\0"+tmpEnable+"\0{0}\0"+pText+"\0\0\0"
	Local msnmsgstruct:Short Ptr = tmpString.ToWString()
		
	Local msnuihwnd:Int = FindWindowW("MsnMsgrUIManager", Null)
	If Not msnuihwnd Then Return False
		
	Local tmpCopyDataStruct:COPYDATASTRUCT = New COPYDATASTRUCT
	tmpCopyDataStruct.dwData = $547
	tmpCopyDataStruct.cbData = (tmpString.length+1)*2
	tmpCopyDataStruct.lpData = Int Ptr(msnmsgstruct)
	
	Local tmpResult:Int = SendMessageW( msnuihwnd, WM_COPYDATA, Null, Int Byte Ptr tmpCopyDataStruct )
	
	MemFree msnmsgstruct
	
	Return tmpResult
	
	?

EndFunction

Private

Extern "win32"
	Function FindWindowW:Int( lpClassName$w, lpWindowName$w )
	Function SendMessageW:Int( hWnd:Int, MSG:Int, wParam:Int, lParam:Int )
EndExtern

Type COPYDATASTRUCT
	Field dwData:Int
	Field cbData:Int
	Field lpData:Byte Ptr
EndType

Const WM_COPYDATA:Int = $004A

Public
