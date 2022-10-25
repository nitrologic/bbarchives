; ID: 2747
; Author: JoshK
; Date: 2010-08-01 00:23:07
; Title: File System Watcher
; Description: Monitors changes to a directory

SuperStrict

Import pub.win32
Import brl.eventqueue
Import brl.standardio
Import brl.threads
Import brl.random

Private

'Externs
Extern "win32"
	Function GetOverlappedResult:Int(hFile:Int,lpOverlapped:OVERLAPPED,lpNumberOfBytesTransferred:Byte Ptr,bWait:Int)
	Function ReadDirectoryChangesW(hDirectory:Int,lpBuffer:Byte Ptr,nBufferLength:Int,bWatchSubtree:Int,dwNotifyFilter:Int,lpBytesReturned:Byte Ptr,lpOverlapped:OVERLAPPED,lpCompletionRoutine:Byte Ptr)
	Function CreateFileA(lpFileName$z,dwDesiredAccess:Int,dwShareMode:Int,lpSecurityAttributes:Byte Ptr,dwCreationDisposition:Int,dwFlagsAndAttributes:Int,hTemplateFile:Int)
	Function CloseHandle:Int(hObject:Int)
	Function CreateEventA:Int(lpEventAttributes:Int,bManualReset:Int,bInitialState:Int,lpName$z)
	'Function GetLastError:Int()
EndExtern


'Constants
Const FILE_FLAG_BACKUP_SEMANTICS:Int=$02000000
Const FILE_FLAG_OVERLAPPED:Int=$40000000

Const GENERIC_READ:Int=$80000000 
Const FILE_SHARE_READ:Int=$00000001
Const OPEN_EXISTING:Int=3

Const INVALID_HANDLE_VALUE:Int=-1
Const FILE_SHARE_WRITE:Int=$00000002

Const WAIT_FAILED% = $FFFFFFFF 
Const WAIT_OBJECT_0%  = $0 
Const WAIT_ABANDONED% = $80 
Const WAIT_TIMEOUT% = $102

Const FILE_NOTIFY_CHANGE_FILE_NAME:Int=$00000001
Const FILE_NOTIFY_CHANGE_DIR_NAME:Int=$00000002
Const FILE_NOTIFY_CHANGE_ATTRIBUTES:Int=$00000004
Const FILE_NOTIFY_CHANGE_SIZE:Int=$00000008
Const FILE_NOTIFY_CHANGE_LAST_WRITE:Int=$00000010
Const FILE_NOTIFY_CHANGE_LAST_ACCESS:Int=$00000020
Const FILE_NOTIFY_CHANGE_CREATION:Int=$00000040
Const FILE_NOTIFY_CHANGE_SECURITY:Int=$00000100

'Types
Type OVERLAPPED
	Field Internal:Int
	Field InternalHigh:Int
	Field Offset:Int
	Field OffsetHigh:Int
	Field hEvent:Int=CreateEventA(0,0,False,Null)
	
	Method Delete()
		If hEvent CloseHandle(hEvent)
	EndMethod
	
EndType

Const FILE_ACTION_ADDED:Int=$00000001
Const FILE_ACTION_REMOVED:Int=$00000002
Const FILE_ACTION_MODIFIED:Int=$00000003
Const FILE_ACTION_RENAMED_OLD_NAME:Int=$00000004
Const FILE_ACTION_RENAMED_NEW_NAME:Int=$00000005

Public

'Event constants
Const EVENT_FILECREATED:Int=6870001
Const EVENT_FILEDELETED:Int=6870002
Const EVENT_FILERENAMED:Int=6870003
Const EVENT_FILEMODIFIED:Int=6870004


'FileSystemWatcher type
Type TFileSystemWatcher
	
	Const flags:Int=FILE_NOTIFY_CHANGE_FILE_NAME|FILE_NOTIFY_CHANGE_DIR_NAME|FILE_NOTIFY_CHANGE_LAST_WRITE
	
	Field path:String
	Field recursive:Int
	Field buffer:Byte[1024]
	Field hfile:Int
	Field overlap:OVERLAPPED=New OVERLAPPED
	Field mode:Int
	Field renamedfilepreviousname:String
	
	Method Delete()
		If hfile CloseHandle(hfile)
	EndMethod
	
	Function Create:TFileSystemWatcher(path:String="",recursive:Int=True)
		Local filesystemwatcher:TFileSystemWatcher
		
		filesystemwatcher=New TFileSystemWatcher
		filesystemwatcher.path=RealPath(path)
		filesystemwatcher.recursive=recursive
		filesystemwatcher.hfile=CreateFileA(filesystemwatcher.path,GENERIC_READ,FILE_SHARE_READ|FILE_SHARE_WRITE,Null,OPEN_EXISTING,FILE_FLAG_BACKUP_SEMANTICS|FILE_FLAG_OVERLAPPED,0)
		If filesystemwatcher.hfile=0 Return Null
		Return filesystemwatcher
	EndFunction
	
	Method Update()
		Local bytesreturned:Int
		Local bytestransferred:Int
		Local event:TEvent		
		Local bank:TBank
		Local stream:TStream
		Local NextEntryOffset:Int
		Local Action:Int
		Local FileNameLength:Int
		Global FileName:String
		Local pos:Int
		Local b:Int
		
		If mode=0
			ReadDirectoryChangesW(Self.hfile,buffer,buffer.length,Self.recursive,Self.flags,Null,Self.overlap,Null)
			mode=1
		EndIf
		
		If GetOverlappedResult(Self.hfile,overlap,Varptr bytesreturned,False)
			mode=0
			If bytesreturned
				bank=CreateStaticBank(buffer,bytesreturned)
				stream=CreateBankStream(bank)
				
				While Not stream.Eof()
					pos=stream.pos()
					NextEntryOffset=stream.ReadInt()
				'	Print "NextEntryOffset: "+NextEntryOffset
					Action=stream.ReadInt()
				'	Print "Action: "+action
					FileNameLength=stream.ReadInt()
					If FileNameLength=0 Notify "DAMN"
				'	Print "FileNameLength: "+FileNameLength
					'FileName=stream.ReadString(FileNameLength)
					filename=Self.path+"/"
					'Print "STARTING: "+filenamelength
					For Local n:Int=0 To filenamelength/2-1
						b=stream.ReadByte()
						filename:+Chr(b)
						stream.ReadByte()
						'Print b+", "+Chr(b)
					Next
					'Print ""
					filename=filename.Replace("\","/")
					event=New TEvent
					event.source=filename
					Select action
						Case FILE_ACTION_ADDED
							event.id=EVENT_FILECREATED
							'Print Int(Byte Ptr(event.extra))
							'Print "File ~q"+filename+"~q created."
						Case FILE_ACTION_REMOVED
							event.id=EVENT_FILEDELETED
							'Print "File ~q"+filename+"~q deleted."
						Case FILE_ACTION_MODIFIED
							event.id=EVENT_FILEMODIFIED
							'Print "File ~q"+filename+"~q modified."							
						Case FILE_ACTION_RENAMED_OLD_NAME
							renamedfilepreviousname=filename
							event=Null
						Case FILE_ACTION_RENAMED_NEW_NAME
							event.id=EVENT_FILERENAMED
							event.extra=renamedfilepreviousname
							'Print renamedfilepreviousname[1]
							'Print renamedfilepreviousname	
							renamedfilepreviousname=""
							'Print "File renamed from ~q"+String(event.extra)+"~q to ~q"+String(event.source)+"~q."
					EndSelect
					If event EmitEvent(event)
					If NextEntryOffset stream.seek(pos+NextEntryOffset) Else Exit
				Wend
				'Print "DONE"
			EndIf
		EndIf
	EndMethod
	
EndType

Function CreateFileSystemWatcher:TFileSystemWatcher(path:String,recursive:Int=True)
	Return TFileSystemWatcher.Create(path,recursive)
EndFunction
