; ID: 2645
; Author: Zeke
; Date: 2010-01-22 13:10:37
; Title: Detect file/dir changes
; Description: Monitor file/dir changes

'Source Code created on 21 Jan 2010 09:21:21 with Logic Gui Version 5.1 Build 422
SuperStrict

Import MaxGui.Drivers

Extern "win32"
	Function ReadDirectoryChangesW:Int(hDirectory:Int, lpBuffer:Byte Ptr, nBufferLength:Int, bWatchSubtree:Int, dwNotifyFilter:Int, lpBytesReturned:Int, lpOverlapped:Byte Ptr, lpCompletionRoutine:Byte Ptr)
	Function CreateFileA:Int(lpFileName$z, dwDesiredAccess:Int, dwShareMode:Int, lpSecurityAttributes:Byte Ptr, dwCreationDisposition:Int, dwFlagsAndAttributes:Int, hTemplateFile:Int)
	Function CloseHandle:Int(hObject:Int)
	Function WaitForSingleObject:Int(hHandle:Int, dwMilliseconds:Int)
	Function CreateEventA:Int(lbEventAttributes:Int, bManualReset:Int, bInitialState:Int, lpName$z)
	Function ResetEvent:Int(hEvent:Int)
End Extern

'Types
Type OVERLAPPED
	Field Internal:Int
	Field InternalHigh:Int
	Field offset:Int
	Field OffsetHight:Int
	Field hEvent:Int
End Type

Type FILE_NOTIFY_INFORMATION
	Field NextEntryOffset:Int '4
	Field Action:Int
	Field FileNameLength:Int '4
	Field fileName:Int, filename_:Int, Filename1:Double, FileName2:Double, FileName3:Double
	Field Filename4:Double, FileName5:Double, FileName6:Double, FileName7:Double
	Field Filename8:Double, FileName9:Double, FileName10:Double, FileName11:Double
	Field Filename12:Double, FileName13:Double, FileName14:Double, FileName15:Double
	Field Filename16:Double, FileName17:Double, FileName18:Double, FileName19:Double
	Field Filename20:Double, FileName21:Double, FileName22:Double, FileName23:Double
	Field Filename24:Double, FileName25:Double, FileName26:Double, FileName27:Double
	Field Filename28:Double, FileName29:Double, FileName30:Double, FileName31:Double
	Field Filename32:Double, FileName33:Double, FileName34:Double, FileName35:Double
	Field Filename36:Double, FileName37:Double, FileName38:Double, FileName39:Double
	Field Filename40:Double, FileName41:Double, FileName42:Double, FileName43:Double
	Field Filename44:Double, FileName45:Double, FileName46:Double, FileName47:Double
	Field Filename48:Double, FileName49:Double, FileName50:Double, FileName51:Double
	Field Filename52:Double, FileName53:Double, FileName54:Double, FileName55:Double
	Field Filename56:Double, FileName57:Double, FileName58:Double, FileName59:Double
	Field Filename60:Double , FileName61:Double , FileName62:Double , FileName63:Double
	'^^ filename 512 bytes
End Type

'Consts
Const TIME_OUT:Int = $102

Const FILE_SHARE_DELETE:Int = $4
Const FILE_SHARE_READ:Int = $1
Const FILE_SHARE_WRITE:Int = $2
Const FILE_ALL_ACCESS:Int = $1FF
Const FILE_LIST_DIRECTORY:Int = $1
Const OPEN_EXISTING:Int = $3
Const FILE_FLAG_BACKUP_SEMANTICS:Int = $2000000
Const FILE_FLAG_OVERLAPPED:Int = $40000000
Const FILE_ATTRIBUTE_NORMAL:Int = $80

'FILE_NOTIFY_CHANGE
Const FILE_NOTIFY_CHANGE_FILE_NAME:Int = $1
Const FILE_NOTIFY_CHANGE_DIR_NAME:Int = $2
Const FILE_NOTIFY_CHANGE_ATTRIBUTES:Int = $4
Const FILE_NOTIFY_CHANGE_SIZE:Int = $8
Const FILE_NOTIFY_CHANGE_LAST_WRITE:Int = $10
Const FILE_NOTIFY_CHANGE_LAST_ACCESS:Int = $20
Const FILE_NOTIFY_CHANGE_CREATION:Int = $40
Const FILE_NOTIFY_CHANGE_SECURITY:Int = $100

'FILE_ACTION
Const FILE_ACTION_ADDED:Int = $1
Const FILE_ACTION_REMOVED:Int = $2
Const FILE_ACTION_MODIFIED:Int = $3
Const FILE_ACTION_RENAMED_OLD_NAME:Int = $4
Const FILE_ACTION_RENAMED_NEW_NAME:Int = $5


'Globals
Global running:Int = 0
Global flags:Int = 0
Global subtree:Int = 0
Global folder:String = ""
Global nUsed:Int = 0
Global hDir:Int = 0
Global hEvent:Int = 0
Global oLap:OVERLAPPED = New OVERLAPPED
Global buffer:Byte Ptr = MemAlloc(65535)
Global dirBuf:FILE_NOTIFY_INFORMATION = New FILE_NOTIFY_INFORMATION


Global Window:TGadget = CreateWindow("Detect file/directory changes",352,239,516,316,Null,WINDOW_TITLEBAR|WINDOW_RESIZABLE |WINDOW_STATUS |WINDOW_CLIENTCOORDS )
	Global Group1:TGadget = CreatePanel(8,61,108,249,Window,PANEL_GROUP,"Filter")
	SetGadgetLayout(Group1, 1, 0, 1, 0)
		Global chkFileName:TGadget = CreateButton("File Name",3,3,100,20,Group1,BUTTON_CHECKBOX)
			SetButtonState( chkFileName,0 )
			SetGadgetToolTip( chkFileName, "Any file name change in the watched directory or subtree causes a change notification wait operation to return. Changes include renaming, creating, or deleting a file." )
		Global chkDirName:TGadget = CreateButton("Dir Name",3,28,100,20,Group1,BUTTON_CHECKBOX)
			SetButtonState( chkDirName,0 )
			SetGadgetToolTip( chkDirName, "Any directory-name change in the watched directory or subtree causes a change notification wait operation to return. Changes include creating or deleting a directory." )
		Global chkAttributes:TGadget = CreateButton("Attributes",3,53,100,20,Group1,BUTTON_CHECKBOX)
			SetButtonState( chkAttributes,0 )
			SetGadgetToolTip( chkAttributes, "Any attribute change in the watched directory or subtree causes a change notification wait operation to return." )
		Global chkSize:TGadget = CreateButton("Size",3,78,100,20,Group1,BUTTON_CHECKBOX)
			SetButtonState( chkSize,0 )
			SetGadgetToolTip( chkSize, "Any file-size change in the watched directory or subtree causes a change notification wait operation to return. The operating system detects a change in file size only when the file is written to the disk. For operating systems that use extensive caching, detection occurs only when the cache is sufficiently flushed." )
		Global chkLastWrite:TGadget = CreateButton("Last Write",3,103,100,20,Group1,BUTTON_CHECKBOX)
			SetButtonState( chkLastWrite,0 )
			SetGadgetToolTip( chkLastWrite, "Any change to the last write-time of files in the watched directory or subtree causes a change notification wait operation to return. The operating system detects a change to the last write-time only when the file is written to the disk. For operating systems that use extensive caching, detection occurs only when the cache is sufficiently flushed." )
		Global chkLastAccess:TGadget = CreateButton("Last Access",3,128,100,20,Group1,BUTTON_CHECKBOX)
			SetButtonState( chkLastAccess,0 )
			SetGadgetToolTip( chkLastAccess, "Any change to the last access time of files in the watched directory or subtree causes a change notification wait operation to return." )
		Global chkCreation:TGadget = CreateButton("Creation",3,153,100,20,Group1,BUTTON_CHECKBOX)
			SetButtonState( chkCreation,0 )
			SetGadgetToolTip( chkCreation, "Any change to the creation time of files in the watched directory or subtree causes a change notification wait operation to return." )
		Global chkSecurity:TGadget = CreateButton("Security",3,178,100,20,Group1,BUTTON_CHECKBOX)
			SetButtonState( chkSecurity,0 )
			SetGadgetToolTip( chkSecurity, "Any security-descriptor change in the watched directory or subtree causes a change notification wait operation to return." )
		Global chkWatchSubDirs:TGadget = CreateButton("Watch Sub Dirs",3,208,100,20,Group1,BUTTON_CHECKBOX)
			SetButtonState( chkWatchSubDirs,0 )
			SetGadgetToolTip(chkWatchSubDirs, "If CHECKED, the application monitors the directory tree rooted at the specified directory. If UNCHECKED, the application monitors only the selected directory.")
	Local Label2:TGadget = CreateLabel("Log Window:", 131, 61, 142, 20, Window, Null)
	SetGadgetLayout Label2, 1, 0, 1, 0
	Global btnWatch:TGadget = CreateButton("Start",448,13,63,20,Window,BUTTON_PUSH)
		SetGadgetToolTip(btnWatch, "Start monitoring selected directory.")
		SetGadgetLayout btnWatch, 0, 1, 1, 0
	Global btnExit:TGadget = CreateButton("Exit", 448, 50, 63, 20, Window, BUTTON_PUSH)
	SetGadgetLayout btnExit, 0, 1, 1, 0
	Global txtLog:TGadget = CreateTextArea(131, 81, 380, 229, Window, Null)
	SetGadgetLayout txtLog, 1, 1, 1, 1
		SetTextAreaText( txtLog , "" )
	Local Label1:TGadget = CreateLabel("Selected Directory:", 8, 13, 203, 17, Window, Null)
	SetGadgetLayout Label1, 1, 0, 1, 0
	Global txtDir:TGadget = CreateTextField(8, 31, 387, 20, Window, Null)
	SetGadgetLayout txtDir, 1, 1, 1, 0
		SetGadgetText( txtDir,"")
	Global btnSelectDir:TGadget = CreateButton("...",398,30,24,20,Window,BUTTON_PUSH)
		SetGadgetToolTip( btnSelectDir, "Select directory to be monitored." )
	SetGadgetLayout btnSelectDir, 0, 1, 1, 0

'//MainLoop
Repeat
	Delay 10
	If PollEvent()	
		Select EventID()
			Case EVENT_WINDOWCLOSE
				Select EventSource()
					Case Window	Window_WC( Window )
				End Select
	
			Case EVENT_GADGETACTION
				Select EventSource()
					Case chkFileName chkFileName_GA(chkFileName, EventData())
					Case chkDirName chkDirName_GA(chkDirName, EventData())
					Case chkAttributes chkAttributes_GA(chkAttributes, EventData())
					Case chkSize chkSize_GA(chkSize, EventData())
					Case chkLastWrite chkLastWrite_GA(chkLastWrite, EventData())
					Case chkLastAccess chkLastAccess_GA(chkLastAccess, EventData())
					Case chkCreation chkCreation_GA(chkCreation, EventData())
					Case chkSecurity chkSecurity_GA(chkSecurity, EventData())
					Case chkWatchSubDirs chkWatchSubDirs_GA(chkWatchSubDirs, EventData())
					Case btnWatch	btnWatch_GA( btnWatch )
					Case btnExit	btnExit_GA( btnExit )
					Case txtLog	txtLog_GA( txtLog )
					Case txtDir	txtDir_GA( txtDir )
					Case btnSelectDir	btnSelectDir_GA( btnSelectDir )
				End Select
		End Select
	EndIf
	If Not Running Then Continue
	Local Pos:Int = 0
	Local ret:Int = waitforsingleobject(hEvent, 50) '50ms delay
	
	If ret <> TIME_OUT
		MemCopy(dirBuf, buffer, SizeOf(dirBuf))
		
		Select dirBuf.Action
			Case FILE_ACTION_MODIFIED
				AddLog "File Modified"
			Case FILE_ACTION_ADDED
				AddLog "File Added"
			Case FILE_ACTION_REMOVED
				AddLog "File Deleted"
			Case FILE_ACTION_RENAMED_NEW_NAME
				AddLog "File Renamed New filename"
			Case FILE_ACTION_RENAMED_OLD_NAME
				AddLog "File Renamed Old filename"
			Default
				AddLog "Unkown File Action= " + dirBuf.Action
		End Select
		
		Local file:String = String.FromShorts(Short Ptr(Byte Ptr(Varptr(dirBuf.fileName))), dirBuf.FileNameLength / 2)
		
		AddLog "~tFileName=" + file

		While dirBuf.NextEntryOffset <> 0 'process all actions
			Pos:+dirBuf.NextEntryOffset
			MemCopy dirBuf, buffer + Pos, SizeOf(dirBuf)
			
			Select dirBuf.Action
				Case FILE_ACTION_MODIFIED
					AddLog "File/Dir Modified"
				Case FILE_ACTION_ADDED
					AddLog "File Added"
				Case FILE_ACTION_REMOVED
					AddLog "File Deleted"
				Case FILE_ACTION_RENAMED_NEW_NAME
					AddLog "File Renamed New name"
				Case FILE_ACTION_RENAMED_OLD_NAME
					AddLog "File Renamed Old name"
				Default
					AddLog "Unkown File Action= " + dirBuf.Action
			End Select
			Local file:String = String.FromShorts(Short Ptr(Byte Ptr(Varptr(dirBuf.fileName))), dirBuf.FileNameLength / 2)
			
			AddLog "~tFileName=" + file
		Wend
		
		'Reset event
		ResetEvent hEvent
		ReadDirectoryChangesW(hDir, buffer, 65535, subtree, flags, nUsed, oLap, Null)
	End If
Forever

Function Window_WC( Window:TGadget )
	EndApp
End Function

Function chkFileName_GA(Button:TGadget, State:Int)
	flags:+$1 * (State = 1) - $1 * (State = 0)
End Function

Function chkDirName_GA( Button:TGadget, State:Int )
	flags:+$2 * (State = 1) - $2 * (State = 0)
End Function

Function chkAttributes_GA( Button:TGadget, State:Int )
	flags:+$4 * (State = 1) - $4 * (State = 0)
End Function

Function chkSize_GA(Button:TGadget, State:Int)
	flags:+$8 * (State = 1) - $8 * (State = 0)
End Function

Function chkLastWrite_GA( Button:TGadget, State:Int )
	flags:+$10 * (State = 1) - $10 * (State = 0)
End Function

Function chkLastAccess_GA( Button:TGadget, State:Int )
	flags:+$20 * (State = 1) - $20 * (State = 0)
End Function

Function chkCreation_GA( Button:TGadget, State:Int )
	flags:+$40 * (State = 1) - $40 * (State = 0)
End Function

Function chkSecurity_GA( Button:TGadget, State:Int )
	flags:+$100 * (State = 1) - $100 * (State = 0)
End Function

Function chkWatchSubDirs_GA( Button:TGadget, State:Int )
	subtree = State
End Function

Function btnWatch_GA( Button:TGadget )
	If GadgetText(Button) = "Start" Then
		StartMonitor
	Else
		StopMonitor
	EndIf
End Function

Function btnExit_GA( Button:TGadget )
	EndApp
End Function

Function txtLog_GA( TextArea:TGadget )
End Function

Function txtDir_GA( TextField:TGadget )
	folder = TextField.getText()
End Function

Function btnSelectDir_GA:Int( Button:TGadget )
	Local dir:String = RequestDir("Select directory to be monitored", "")
	If dir = "" Then Return 0
	If Right(dir, 1) <> "\" Then dir:+"\"
	SetGadgetText(txtDir , dir)
	folder=dir
End Function

'// Functions

Function EndApp()
	MemFree buffer
	If hEvent CloseHandle hEvent
	If hDir CloseHandle hDir
	End
End Function

Function StartMonitor:Int()
	If folder = "" Then
		AddLog "Select directory first"
		Return 0
	End If
	If FileType(folder) <> 2 Then
		AddLog "Invalid directory"
		Return 0
	End If
	If Right(folder,1)<>"\" Then folder:+"\"
	SetGadgetText(btnWatch, "Stop")
	Running = 1
	DisableGadget(Group1)
	SetTextAreaText(txtLog , "")
	
	If hDir Then CloseHandle hDir
	hDir = CreateFileA(folder, FILE_LIST_DIRECTORY, FILE_SHARE_READ | FILE_SHARE_WRITE, Null, OPEN_EXISTING, FILE_FLAG_BACKUP_SEMANTICS | FILE_FLAG_OVERLAPPED, Null)
	If Not hDir
		AddLog "Error~n"
		Return 0
	End If
	If hEvent Then CloseHandle hEvent
	hEvent = CreateEventA(0, True, True, Null)
	oLap.hEvent = hEvent
	ReadDirectoryChangesW(hDir, buffer, 65535, subtree, flags, nUsed, oLap, Null)
	AddLog "Monitoring started...~n"
End Function

Function StopMonitor()
	SetGadgetText(btnWatch, "Start")
	Running = 0
	EnableGadget(Group1)
	If hEvent CloseHandle hEvent
	If hDir CloseHandle hDir
	AddLog "Monitoring stopped...~n"
End Function

Function AddLog(text:String)
	AddTextAreaText(txtLog, text + "~n")
	PollSystem
End Function
