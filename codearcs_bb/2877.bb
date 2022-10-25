; ID: 2877
; Author: Subirenihil
; Date: 2011-08-03 17:50:24
; Title: Shortcut
; Description: Create a shortcut file (*.lnk)

;The following lines must be in shell32.decls
;   .lib "shell32.dll"
;   ShellExecute%(hwnd%,Operation$,File$,Parameters$,Directory$,ShowCmd%):"ShellExecuteA"

Const DATALINES = 9 ;+10 ;use this if you use the readability modification below

;Load a template .inf file from data
Dim InfLines$(DATALINES)

Restore template
For a=0 To DATALINES-1
	Read InfLines$(a)
	InfLines$(a) = Replace$(InfLines$(a),"'",Chr$(34)) ;Convert single quotes into double quotes
Next

;Create a shortcut to notepad on the user's desktop
CreateShortcut "%16384%", "Testing", "%10%\NOTEPAD.EXE", "%10%\NOTEPAD.EXE", 0, "", "%16430%", "Open Notepad"
End

;Creates a shortcut called <Title> at <Link Location> that opens <Target>,
;  has icon <Icon Index> from <Icon File>, starts in <Start Folder> and has <Description>
;  <Target> also needs to contain any additional parameters.
Function CreateShortcut(LinkLocation$, Title$, Target$, IconFile$, IconIndex%, UserName$="", StartIn$="", Description$="")
	Local infstr$[DATALINES]
	
	tempdir$=SystemProperty("tempdir")
	If Right$(tempdir$,1)<>"\" Then tempdir$=tempdir$+"\"
	
	Repeat
		temp$=tempdir$+"Create Shortcut"+Rand(10000)+".inf"
	Until FileType(temp$)=0
	
	inf%=WriteFile(temp$)
	
	For a = 0 To DATALINES-1
		infstr[a]=InfLines$(a)
		;For easier to read *.inf files, change the %'s to < and >
		If Instr(InfLines$(a),"%LinkLocation%"): infstr[a]=Replace$(infstr[a],"%LinkLocation%", LinkLocation$)	:EndIf; <Link Location>
		If Instr(InfLines$(a),"%LinkTitle%"   ): infstr[a]=Replace$(infstr[a],"%LinkTitle%"   , Title$)			:EndIf; <Title>
		If Instr(InfLines$(a),"%Target%"      ): infstr[a]=Replace$(infstr[a],"%Target%"      , Target$)		:EndIf; <Target>
		If Instr(InfLines$(a),"%IconFile%"    ): infstr[a]=Replace$(infstr[a],"%IconFile%"    , IconFile$)		:EndIf; <Icon File>
		If Instr(InfLines$(a),"%IconIndex%"   ): infstr[a]=Replace$(infstr[a],"%IconIndex%"   , IconIndex%)		:EndIf; <Icon Index>
		If Instr(InfLines$(a),"%UserName%"    ): infstr[a]=Replace$(infstr[a],"%UserName%"    , UserName$)		:EndIf; <User Name>
		If Instr(InfLines$(a),"%StartFolder%" ): infstr[a]=Replace$(infstr[a],"%StartFolder%" , StartIn$)		:EndIf; <Start Folder>
		If Instr(InfLines$(a),"%Description%" ): infstr[a]=Replace$(infstr[a],"%Description%" , Description$)	:EndIf; <Description>
		WriteLine inf, infstr[a]
	Next
	
	CloseFile inf%
	ShellExecute 0,"install",Chr$(34)+temp$+Chr$(34),"","",0
End Function

.template
Data "[Version]"
Data "signature='$CHICAGO$'"
Data ""
Data "[DefaultInstall]"
Data "UpdateInis=Addlink"
Data ""
Data "[Addlink]"
Data "setup.ini, progman.groups,, 'group1=''%LinkLocation%'''"
Data "setup.ini, group1,, '''%LinkTitle%'',''%Target%'',''%IconFile%'',%IconIndex%, ''%UserName%'', ''%StartFolder%'', Comment='%Description%''"

;You can also uncomment this data section (and add 10 to the DATALINES constant),
; as well as slightly modify the CreateShortcut function to make the *.inf easier to read.
; However, if you are not using the complete file paths (i.e. *.inf directory listings, etc.) then this won't work.
	;Data ""
	;Data "[Strings]"
	;Data "LinkTitle    = '<Title>'"
	;Data "LinkLocation = '<LinkLocation>'"
	;Data "Target       = '<Target>'"
	;Data "IconFile     = '<IconFile>'"
	;Data "IconIndex    = '<IconIndex>'"
	;Data "UserName     = '<UserName>'"
	;Data "StartFolder  = '<StartFolder>'"
	;Data "Description  = '<Description>'"

; Common DIRID Listing, as used by *.inf files
; For example: if <Link Location> = "%16384%" then the shortcut would be created on the user's desktop.
;
; Destination Directories
;
; 01 - SourceDrive:\pathname         (the directory from which the INF file was installed) 
; 10 - Windows directory             (%SystemRoot%) 
; 11 - System directory              (%SystemRoot%\system32 -NT-, %SystemRoot%\system -Win9x/Me-) 
; 12 - Drivers directory             (%SystemRoot%\system32\drivers -NT-, %SystemRoot%\system\IoSubsys -Win9x/Me-)
; 17 - INF file directory            (%SystemRoot%\inf)
; 18 - Help directory                (%SystemRoot%\Help)
; 20 - Fonts directory               (%SystemRoot%\Fonts)
; 24 - Root directory of system disk (%SystemDrive%)
; 25 - Shared directory              (%ALLUSERSPROFILE%\Shared Documents)
; 53 - User profile directory        (%USERPROFILE%)
;
; Shell Special Folders
;
; 16419 %ALLUSERSPROFILE%\Application Data
; 16409 %ALLUSERSPROFILE%\Desktop
; 16430 %ALLUSERSPROFILE%\Documents
; 16437 %ALLUSERSPROFILE%\Documents\My Music
; 16438 %ALLUSERSPROFILE%\Documents\My Pictures
; 16439 %ALLUSERSPROFILE%\Documents\My Videos
; 16415 %ALLUSERSPROFILE%\Favorites
; 16406 %ALLUSERSPROFILE%\Start Menu
; 16407 %ALLUSERSPROFILE%\Start Menu\Programs
; 16431 %ALLUSERSPROFILE%\Start Menu\Programs\Administrative Tools
; 16408 %ALLUSERSPROFILE%\Start Menu\Programs\Startup
; 16429 %ALLUSERSPROFILE%\Templates
; 16410 %USERPROFILE%\Application Data
; 16417 %USERPROFILE%\Cookies
; 16384 %USERPROFILE%\Desktop
; 16400 %USERPROFILE%\Desktop
; 16390 %USERPROFILE%\Favorites
; 16412 %USERPROFILE%\Local Settings\Application Data
; 16443 %USERPROFILE%\Local Settings\Application Data\Microsoft\CD Burning
; 16418 %USERPROFILE%\Local Settings\History
; 16416 %USERPROFILE%\Local Settings\Temporary Internet Files
; 16389 %USERPROFILE%\My Documents
; 16397 %USERPROFILE%\My Documents\My Music
; 16423 %USERPROFILE%\My Documents\My Pictures
; 16398 %USERPROFILE%\My Documents\My Videos
; 16403 %USERPROFILE%\NetHood
; 16411 %USERPROFILE%\PrintHood
; 16392 %USERPROFILE%\Recent
; 16393 %USERPROFILE%\SendTo
; 16395 %USERPROFILE%\Start Menu
; 16386 %USERPROFILE%\Start Menu\Programs
; 16432 %USERPROFILE%\Start Menu\Programs\Administrative Tools
; 16391 %USERPROFILE%\Start Menu\Programs\Startup
; 16405 %USERPROFILE%\Templates
; 16422 %ProgramFiles%
; 16427 %ProgramFiles%\Common Files
; 16440 %SystemRoot%\Resources
; 16441 %SystemRoot%\Resources\0409
