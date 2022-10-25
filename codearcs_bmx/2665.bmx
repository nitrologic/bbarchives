; ID: 2665
; Author: Azathoth
; Date: 2010-03-13 22:40:36
; Title: 2GB+ filestream
; Description: Handles large files (64bit sizes) - Windows only

Strict

Import Pub.Win32

Const INVALID_HANDLE_VALUE = -1 

Const GENERIC_READ = $80000000
Const GENERIC_WRITE = $40000000

Const OPEN_EXISTING = 3

Extern "win32"
	Function CreateFileW(lpFileName:Byte Ptr,dwDesiredAccess,dwShareMode,lpSecurityAttributes:Byte Ptr,dwCreationDisposition,dwFlagsAndAttributes,hTemplateFile)
	Function GetFileSizeEx(hFile, lpFileSize:Byte Ptr)
	Function SetFilePointerEx(hFile, liDistanceToMove:Long, lpNewFilePointer:Byte Ptr, dwMoveMethod)
	Function ReadFile(hFile, lpBuffer:Byte Ptr, nNumberOfBytesToRead, lpNumberOfBytesRead:Byte Ptr, lpOverlapped:Byte Ptr)
	Function WriteFile(hFile, lpBuffer:Byte Ptr, nNumberOfBytesToWrite, lpNumberOfBytesWritten:Byte Ptr, lpOverlapped:Byte Ptr)
	Function FlushFileBuffers(hFile)

	Function CloseHandle(hObject)
	Function GetLastError()
	
EndExtern

Function FileSizeEx:Long(path:String)
	Local size:Long, handle:Int

	handle=CreateFileW(path.ToWString(),GENERIC_READ,0,Null,OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL,0)
	If handle<>INVALID_HANDLE_VALUE
		If GetFileSizeEx(handle, Varptr size)=0 Then size=-1 
		CloseHandle(handle)
	EndIf
	Return size
EndFunction

Type TFileStream Extends TStream
	Field _pos:Long, _size:Long, _handle=INVALID_HANDLE_VALUE, _mode
	
	Method PosEx:Long()
		Return _pos
	EndMethod
	
	Method SizeEx:Long()
		Return _size
	EndMethod
	
	Method Eof()
		Return (PosEx()=SizeEx())
	EndMethod
	
	Method SeekEx:Long(pos:Long)
		Assert _handle<>INVALID_HANDLE_VALUE Else "Attempt to seek closed stream"
		SetFilePointerEx(_handle,pos,Varptr _pos,0)
		Return _pos
	EndMethod
	
	Method Read(buf:Byte Ptr, count)
		Assert _handle<>INVALID_HANDLE_VALUE Else "Attempt to read from closed stream"
		Assert _mode & GENERIC_READ Else "Attempt to read from write-only stream"
		If ReadFile(_handle,buf,count, Varptr count, Null)=0 Then Return 0
		_pos:+count
		Return count
	EndMethod
	
	Method Write( buf:Byte Ptr,count )
		Assert _handle<>INVALID_HANDLE_VALUE Else "Attempt to write to closed stream"
		Assert _mode & GENERIC_WRITE Else "Attempt to write to read-only stream"
		If WriteFile(_handle,buf,count,Varptr count, Null )=0 Then Return 0
		_pos:+count
		If _pos>_size _size=_pos
		Return count
	End Method
	
	Method Pos()
		Return Int(_pos)
	EndMethod
	
	Method Size()
		Return Int(_size)
	EndMethod
	
	Method Seek(pos)
		Return Int(SeekEx(pos))
	EndMethod
	
	Method Flush()
		If _handle<>INVALID_HANDLE_VALUE Then FlushFileBuffers(_handle)
	EndMethod	
	
	Method Close()
		If _handle=INVALID_HANDLE_VALUE Then Return
		Flush()
		CloseHandle(_handle)
		_handle=INVALID_HANDLE_VALUE
		_pos=0
		_size=0
	EndMethod
	
	Function OpenFile:TFileStream(path:String,readable,writeable )
		Local m, handle, wstr:Byte Ptr
		
		If readable m:|GENERIC_READ
		If writeable m:|GENERIC_WRITE
		
		wstr=path.ToWString()
		
		handle=CreateFileW(wstr,m,0,Null,OPEN_EXISTING,FILE_ATTRIBUTE_NORMAL,0)
		
		MemFree(wstr)
		If handle<>INVALID_HANDLE_VALUE
			Local stream:TFileStream=New TFileStream
			
			stream._handle=handle
			SetFilePointerEx(handle,0,Varptr stream._pos,1)
			SetFilePointerEx(handle,0,Varptr stream._size,2)
			SetFilePointerEx(handle,stream._pos,Null,0)
			stream._mode=m
			Return stream
		EndIf
	EndFunction
		
EndType
