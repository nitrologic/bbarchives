; ID: 3167
; Author: BlitzSupport
; Date: 2014-12-13 11:21:05
; Title: Save settings to correct location on Windows
; Description: Avoid problems saving to application's local folder on Windows Vista, 7/8 upwards...

' ----------------------------------------------------------
' Reads and writes save-files in correct location on Windows!
' ----------------------------------------------------------

Rem

	Usage:
	
	1)	Modify the ApplicationName and PrefsFilename globals to suit
	your application. Can be left as defaults for demo.
	
	2)	Use only the three functions below:

	LoadSettings ()	-	loads your settings file, if it exists

	SaveSettings ()	-	creates folder in correct location (if needed);
						creates prefs file in folder (if needed);
						saves prefs!

	GetPrefsFile ()	-	returns path of prefs file, based on
						ApplicationName and PrefsFilename.

						Modify these two globals below to suit yourself!

						Note on application name/prefs filename:

						To avoid conflict with other applications,
						probably best to use your developer/studio
						name for ApplicationName (this is the folder
						name) and the name of your game for
						PrefsFilename (eg. "Rocket Raiders.cfg")

	3)	On running the demo, navigate to the folder name returned by GetPrefsFile ()
		to see the folder/prefs file. Delete the folder to clean up after playing
		around! Try re-running several times, and try deleting prefs file/folder
		and re-running...
		
End Rem

' ----------------------------------------------------------
' N O T E S . . .
' ----------------------------------------------------------

' Just call SaveSettings to set up your save folder/file for the first time.

' You'll have to amend LoadSettings to accommodate your game's variables and/or structure.

' Amend WriteSettingsFile to suit your game. (SaveSettings is just a wrapper around this.)

SuperStrict

?Win32 ' Windows only!

Global ApplicationName:String = "AAA Example Game"
Global PrefsFilename:String = "prefs.txt"

' Some example variables to be saved/loaded:

Global Example_Lives:Int = 0
Global Example_Level:String = ""

' Uses an example save file of the form:

Rem

	LIVES:Integer
	LEVEL:String
	
	... eg.
	
	LIVES:5
	LEVEL:Space

End Rem

' Windows special path constants:

Const CSIDL_PERSONAL:Int = $5 ' Use this instead of CSIDL_MYDOCUMENTS. I don't know why! Ask Microsoft...
Const CSIDL_APPDATA:Int = $1A

' From http://www.blitzbasic.com/codearcs/codearcs.php?code=2815
' See URL for more special folder locations!

Function GetSpecialFolder:String (folder:Int)

	' Shell32 functions...
	
	Global SHGetSpecialFolderLocation_	(hwndOwner:Byte Ptr, nFolder:Int, pidl:Byte Ptr) "win32"
	Global SHGetPathFromIDList_			(pidl:Byte Ptr, bytearray:Byte Ptr) "win32"
	
	' OLE32 functions...
	
	Global CoTaskMemFree_ (pv:Byte Ptr)
	
	' Assign function pointers...
	
	Local shell32:Int	= LoadLibraryA ("shell32.dll")
	Local ole32:Int		= LoadLibraryA ("ole32.dll")

	Local result:Int = False
	
	If shell32

		SHGetSpecialFolderLocation_	= GetProcAddress (shell32, "SHGetSpecialFolderLocation")
		SHGetPathFromIDList_		= GetProcAddress (shell32, "SHGetPathFromIDList")

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

	Function GetSpecialFolder_Sub:String(folder_id:Int) ' JoshK's code brutally hacked-in!

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

End Function

' Helper function...

Function Quoted:String (in:String)
	Return "~q" + in + "~q"
End Function

' Retrieves location of prefs file based on ApplicationName and PrefsFilename,
' whether or not they've been created yet...

Function GetPrefsFile:String ()

	Local folder:String = GetSpecialFolder (CSIDL_APPDATA)

	If folder = ""

		folder = GetSpecialFolder (CSIDL_PERSONAL)

		If folder = ""
			folder = "C:" ' Gah! What's up with your system?!
		EndIf

	EndIf
	
	If Right (folder, 1) <> "\" And Right (folder, 1) <> "/"
		folder = folder + "\"
	EndIf
	
	Return folder + ApplicationName + "\" + PrefsFilename
	
End Function

' ----------------------------------------------------------
' Deletes settings file...
' ----------------------------------------------------------

Function DeleteSettings ()
	DeleteFile (GetPrefsFile ())
End Function

' ----------------------------------------------------------
' Loads settings, if settings file exists...
' ----------------------------------------------------------

' Use SaveSettings to set up prefs folder/file!

Function LoadSettings ()

	Local loadprefs:TStream = ReadFile (GetPrefsFile ())
	
	If loadprefs
	
		While Not Eof (loadprefs)
			
			Local info:String = ReadLine (loadprefs)

			' Example line:
			
			' LEVEL:Space
			
			Local splitter:Int = Instr (info, ":")
			
			Local entry:String = Trim (Left (info, splitter - 1))
			Local value:String = Trim (Mid (info, splitter + 1))
			
			' Amend the Case entries and Example_Variables to suit your game!
			
			Select entry

				Case "LIVES"
				
					' Read an Int...
					
					Example_Lives = Int (value)

				Case "LEVEL"
				
					' Read a String...
					
					Example_Level = value

				Default

			EndSelect
			
		Wend

		CloseFile loadprefs

	Else
		Print "Couldn't load preferences file! Does " + Quoted (GetPrefsFile ()) + " exist?"
	EndIf

End Function

' ----------------------------------------------------------
' Save settings. Modify the highlighted section to suit!
' ----------------------------------------------------------

Function WriteSettingsFile (folder:String)

	If Right (folder, 1) = "\" Or Right (folder, 1) = "/"
		folder = folder [..Len (folder) - 1] ' FileType doesn't like end slashes on folders!
	EndIf
	
	Local savefile:String = folder + "\" + PrefsFilename

	If FileType (folder) = FILETYPE_DIR
	
		Local created:Int = CreateFile (savefile)
		
		If created
		
			Local saveprefs:TStream = WriteFile (savefile)
			
			If saveprefs

				' ----------------------------------------------------------
				' *** MODIFY ME! ***
				' ----------------------------------------------------------
		
				' ----------------------------------------------------------
				' The settings save section - amend to suit your game!
				' ----------------------------------------------------------

				WriteLine saveprefs, "LIVES:" + Example_Lives
				WriteLine saveprefs, "LEVEL:" + Example_Level

				' ----------------------------------------------------------
				' *** END OF MODIFY ME! ***
				' ----------------------------------------------------------

				CloseFile saveprefs

			Else

				Print "Unable to create preferences file " + Quoted (savefile)
			EndIf

		EndIf
	
	Else
		Print "Unable to locate preferences folder " + Quoted (folder)
		Print "Folder is " + FileType (folder)
		Print "Folder is " + FileType ("C:\Users\James\AppData\Roaming\AAA Example Game\")
		Print "Folder is " + FileType ("C:\Users\James\AppData\Roaming\AAA Example Game")
	EndIf

End Function

' ----------------------------------------------------------
' Wrapper for WriteSettingsFile...
' ----------------------------------------------------------

Function SaveSettings ()
	
	Local folder:String = ExtractDir (GetPrefsFile ())
	
	folder = Replace (folder, "/", "\") ' WTF, ExtractDir?!

	CreateDir folder$

	Select FileType (folder$)
	
		Case 0
		
			Print "Unable to create preferences folder " + Quoted (folder)
			
		Case FILETYPE_DIR
		
			WriteSettingsFile folder
			
		Case FILETYPE_FILE ' Unlikely!
		
			Print "Unable to create preferences folder + " + Quoted (folder) + "; file with same name already exists!"
			
	EndSelect
	
End Function

? ' End of ?Win32 section!

' ----------------------------------------------------------
' D E M O . . .
' ----------------------------------------------------------

' Some random level names for saving...

Local Level_Name:String [10]

Level_Name [0] = "Snow World"
Level_Name [1] = "Eiffel Tower"
Level_Name [2] = "Egypt"
Level_Name [3] = "Space"
Level_Name [4] = "Moon"
Level_Name [5] = "Hell"
Level_Name [6] = "Desert"
Level_Name [7] = "Circus"
Level_Name [8] = "Inca"
Level_Name [9] = "Alien Planet"

SeedRnd MilliSecs ()

Print ""
Print "Using " + GetPrefsFile () ' Just for info

' ----------------------------------------------------------
' Load settings... expected to fail if not already saved!
' ----------------------------------------------------------

Print ""
Print "Attempting to load settings..."
Print ""

LoadSettings

' ----------------------------------------------------------
' Show loaded settings... will be default null on first run
' ----------------------------------------------------------

Print ""
Print "Loaded settings:"
Print ""
Print "~t~tLives: " + Example_Lives
Print "~t~tLevel: " + Example_Level
Print ""

' ----------------------------------------------------------
' Randomly change settings...
' ----------------------------------------------------------

Print ""
Print "Randomly changing settings..."
Print ""

Example_Lives = Rand (1, 10)
Example_Level = Level_Name [Rand (0, 9)]

' ----------------------------------------------------------
' Save settings...
' ----------------------------------------------------------

Print ""
Print "Saving new settings..."
Print ""

SaveSettings

' ----------------------------------------------------------
' Reload saved settings...
' ----------------------------------------------------------

Print ""
Print "Attempting to reload saved settings..."
Print ""

LoadSettings

' ----------------------------------------------------------
' Show loaded settings...
' ----------------------------------------------------------

Print ""
Print "Loaded settings:"
Print ""
Print "~t~tLives: " + Example_Lives
Print "~t~tLevel: " + Example_Level
Print ""
