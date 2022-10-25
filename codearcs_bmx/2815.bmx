; ID: 2815
; Author: BlitzSupport
; Date: 2011-01-20 18:40:17
; Title: Get Windows 'special folder' paths
; Description: Retrieves the real paths of Windows' special folders

Const CSIDL_INTERNET:Int = $1
Const CSIDL_PROGRAMS:Int = $2
Const CSIDL_CONTROLS:Int = $3
Const CSIDL_PRINTERS:Int = $4
Const CSIDL_PERSONAL:Int = $5 ' Use this instead of CSIDL_MYDOCUMENTS. I don't know why! Ask Microsoft...
Const CSIDL_FAVORITES:Int = $6
Const CSIDL_STARTUP:Int = $7
Const CSIDL_RECENT:Int = $8
Const CSIDL_SENDTO:Int = $9
Const CSIDL_BITBUCKET:Int = $A
Const CSIDL_STARTMENU:Int = $B
Const CSIDL_MYDOCUMENTS:Int = $C
Const CSIDL_MYMUSIC:Int = $D
Const CSIDL_MYVIDEO:Int = $E
Const CSIDL_DESKTOPDIRECTORY:Int = $10
Const CSIDL_DRIVES:Int = $11
Const CSIDL_NETWORK:Int = $12
Const CSIDL_NETHOOD:Int = $13
Const CSIDL_FONTS:Int = $14
Const CSIDL_TEMPLATES:Int = $15
Const CSIDL_COMMON_STARTMENU:Int = $16
Const CSIDL_COMMON_PROGRAMS:Int = $17
Const CSIDL_COMMON_STARTUP:Int = $18
Const CSIDL_COMMON_DESKTOPDIRECTORY:Int = $19
Const CSIDL_APPDATA:Int = $1A
Const CSIDL_PRINTHOOD:Int = $1B
Const CSIDL_LOCAL_APPDATA:Int = $1C
Const CSIDL_ALTSTARTUP:Int = $1D
Const CSIDL_COMMON_ALTSTARTUP:Int = $1E
Const CSIDL_COMMON_FAVORITES:Int = $1F
Const CSIDL_INTERNET_CACHE:Int = $20
Const CSIDL_COOKIES:Int = $21
Const CSIDL_HISTORY:Int = $22
Const CSIDL_COMMON_APPDATA:Int = $23
Const CSIDL_WINDOWS:Int = $24
Const CSIDL_SYSTEM:Int = $25
Const CSIDL_PROGRAM_FILES:Int = $26
Const CSIDL_MYPICTURES:Int = $27
Const CSIDL_PROFILE:Int = $28
Const CSIDL_SYSTEMX86:Int = $29
Const CSIDL_PROGRAM_FILESX86:Int = $2A
Const CSIDL_PROGRAM_FILES_COMMON:Int = $2B
Const CSIDL_PROGRAM_FILES_COMMONX86:Int = $2C
Const CSIDL_COMMON_TEMPLATES:Int = $2D
Const CSIDL_COMMON_DOCUMENTS:Int = $2E
Const CSIDL_COMMON_ADMINTOOLS:Int = $2F
Const CSIDL_ADMINTOOLS:Int = $30
Const CSIDL_CONNECTIONS:Int = $31
Const CSIDL_COMMON_MUSIC:Int = $35
Const CSIDL_COMMON_PICTURES:Int = $36
Const CSIDL_COMMON_VIDEO:Int = $37
Const CSIDL_RESOURCES:Int = $38
Const CSIDL_RESOURCES_LOCALIZED:Int = $39
Const CSIDL_COMMON_OEM_LINKS:Int = $3A
Const CSIDL_CDBURN_AREA:Int = $3B
Const CSIDL_COMPUTERSNEARME:Int = $3D
Const CSIDL_FLAG_PER_USER_INIT:Int = $800
Const CSIDL_FLAG_NO_ALIAS:Int = $1000
Const CSIDL_FLAG_DONT_VERIFY:Int = $4000
Const CSIDL_FLAG_CREATE:Int = $8000
Const CSIDL_FLAG_MASK:Int = $FF00

Function GetSpecialFolder:String (folder:Int)

	?Win32 ' Windows only!

	' Shell32 functions...
	
	Global SHGetSpecialFolderLocation_ (hwndOwner:Byte Ptr, nFolder:Int, pidl:Byte Ptr) "win32"
	Global SHGetPathFromIDList_ (pidl:Byte Ptr, bytearray:Byte Ptr) "win32"
	
	' OLE32 functions...
	
	Global CoTaskMemFree_ (pv:Byte Ptr)
	
	' Assign function pointers...
	
	Local shell32:Int = LoadLibraryA ("shell32.dll")
	Local ole32:Int = LoadLibraryA ("ole32.dll")

	Local result:Int = False
	
	If shell32

		SHGetSpecialFolderLocation_ = GetProcAddress (shell32, "SHGetSpecialFolderLocation")
		SHGetPathFromIDList_ = GetProcAddress (shell32, "SHGetPathFromIDList")

		If (Not SHGetSpecialFolderLocation_) Or (Not SHGetPathFromIDList_)
			DebugLog "Failed to assign shell32 function pointer!"
			Return ""
		EndIf

	Else

		DebugLog "Failed to open shell32.dll!"
		Return ""

	EndIf

	If ole32

		CoTaskMemFree_ = GetProcAddress (ole32, "CoTaskMemFree")

		If Not CoTaskMemFree_
			DebugLog "Failed to assign ole32 function pointer!"
			Return ""
		EndIf

	Else

		DebugLog "Failed to open ole32.dll!"
		Return ""

	EndIf

	Function GetSpecialFolder_Sub:String(folder_id:Int) 

		Local idl:TBank = CreateBank (8) 
		Local pathbank:TBank = CreateBank (260) 
		Local n%
		Local sp$
		Local b:Int

		If SHGetSpecialFolderLocation_ (Null, folder_id, BankBuf (idl)) = 0		

			SHGetPathFromIDList_ Byte Ptr PeekInt (idl, 0), BankBuf (pathbank)

			For n = 0 To 259
				b = PeekByte (pathbank, n)
				If b = 0
					CoTaskMemFree_ (Byte Ptr PeekInt (idl, 0))
					Return sp
				EndIf
				sp$ = sp$ + Chr (b)
			Next
		Else
			Return ""
		EndIf
		
		CoTaskMemFree_ (Byte Ptr PeekInt (idl, 0))
		
		Return sp.Trim ()
		
	End Function

	If SHGetSpecialFolderLocation_ And SHGetPathFromIDList_ And CoTaskMemFree_
		Return GetSpecialFolder_Sub (folder)
	EndIf

	?

End Function

' For Local loop:Int = 1 To 10000000 ' Mem leak test!

	Print GetSpecialFolder (CSIDL_PROGRAM_FILESX86)
	Print GetSpecialFolder (CSIDL_DESKTOPDIRECTORY)
	Print GetSpecialFolder (CSIDL_PERSONAL)
	Print GetSpecialFolder (CSIDL_WINDOWS)
	Print GetSpecialFolder (CSIDL_SYSTEM)
	
	' Handy one: create a folder here in which to store your app settings correctly on Windows Vista upwards...
	
	Print GetSpecialFolder (CSIDL_APPDATA)

'	Delay 10
	
' Next
