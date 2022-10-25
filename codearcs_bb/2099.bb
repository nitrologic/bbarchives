; ID: 2099
; Author: grable
; Date: 2007-09-03 20:19:16
; Title: NTFS Alternate Data Streams
; Description: Allows to handle/enumerate NTFS Alternate Data Streams

Rem
	Enumerate/Handle NTFS Alternate Data Streams attached to a file/dir

	What is NTFS Alternate Data Streams?
	
		They are streams attached to files/directories, but not part of the original content.
		
		General syntax for streams are "FILENAME:STREAMNAME" 
		or the longer "FILENAME:STREAMNAME:$DATA".
		
		Most file apis handle the FILE:STREAM syntax, 
		including BlitzMax's TStream.
		
		Note that there is no indication that a file has any streams 
		attached to it in explorer, not even the size is incremented.
		
		Handling the file outside explorer/cli
		(eg sending over network, archiving, etc) 
		makes the file loose its streams.		
		
	Requirements:
		Windows 2000 and above
		NTFS Volumes
		
	author: grable
	email : grable0@gmail.com
EndRem
SuperStrict

Import BRL.Stream

?Win32

Private
Extern "Win32"
	Const INVALID_HANDLE_VALUE:Int = -1

	Const FILE_SHARE_READ:Int = $00000001	
	Const OPEN_EXISTING:Int = 3	
	Const FILE_FLAG_BACKUP_SEMANTICS:Int = 33554432
	
	Const GENERIC_READ:Int = $80000000

	Function BackupRead:Int( hfile:Int, buff:Byte Ptr, count:Int, bytesread:Int Var, abort:Int, processsecurity:Int, context:Byte Ptr Var)
	Function BackupSeek:Int( hfile:Int, lowbytestoseek:Int, highbytestoseek:Int, lowbytesseeked:Int Var, highbytesseeked:Int Var, context:Byte Ptr Var)
	Function CreateFileA:Int( fname$z, access:Int, sharedmode:Int, securityattribs:Byte Ptr, disp:Int, flags:Int, templatefile:Int)
	Function CloseHandle:Int( hfile:Int)
	Function DeleteFileA:Int( fn$z)
EndExtern
Public


'
' Stream Types
'
Const STREAM_DATA:Int = 1			' original file stream
Const STREAM_EXTERNAL_DATA:Int = 2
Const STREAM_SECURITY_DATA:Int = 3
Const STREAM_ALTERNATE_DATA:Int = 4	' standard attached streams
Const STREAM_LINK:Int = 5
Const STREAM_PROPERTY_DATA:Int = 6
Const STREAM_OBJECT_ID:Int = 7
Const STREAM_REPARSE_DATA:Int = 8
Const STREAM_SPARSE_DOCK:Int = 9

Type TWin32StreamID Abstract
	Field Typ:Int
	Field Attribs:Int
	Field Size:Long
	Field NameSize:Int
EndType

'
' enumerate streams attatched to file/dir
'
Type TFindStream Extends TWin32StreamID
	Field Name:String
	Field StripTags:Int
' private
	Field Handle:Int
	Field Context:Byte Ptr
	
	Function Create:TFindStream( filename:String, striptags:Int = True)
		Local fs:TFindStream = New TFindStream
		fs.StripTags = striptags
		If Not fs.FindFirst( filename) Then Return Null
		Return fs
	EndFunction
	
	Method FindFirst:Int( filename:String)
		Handle = CreateFileA( filename, GENERIC_READ, FILE_SHARE_READ, Null, OPEN_EXISTING, FILE_FLAG_BACKUP_SEMANTICS, Null)
		If Handle = INVALID_HANDLE_VALUE Then Return Null
		' skip the real file stream
		If Not FindNext() Then
			Close()
			Return False
		EndIf
		' get next stream
		If Not FindNext() Then
			Close()
			Return False
		EndIf
		Return True
	EndMethod
	
	Method FindNext:Int()
		If Handle = INVALID_HANDLE_VALUE Then Return False	
		Local read:Int
		' read header	
		If Not BackupRead( Handle, Self, SizeOf(TWin32StreamID), read, False, False, Context) Then Return False
		If read <> SizeOf(TWin32StreamID) Then Return False
		' read name
		Local streamname:Short[NameSize + 1]
		If Not BackupRead( Handle, streamname, NameSize, read, False, False, Context) Then Return False
		If read <> NameSize Then Return False	
		If StripTags Then
			Name = String.FromWString( streamname)[1..]
			Name = Name[..Name.Find( ":")]
		Else
			Name = String.FromWString( streamname)			
		EndIf
		' skip data
		Local t1:Int, t2:Int
		BackupSeek( Handle, Int(Size & $FFFFFFFF), Int((Size Shr 32) & $FFFFFFFF), t1, t2, Context)
		Return True
	EndMethod
	
	Method Close()
		If Handle = INVALID_HANDLE_VALUE Then Return
		CloseHandle( Handle)
		Handle = INVALID_HANDLE_VALUE
	EndMethod
EndType


'
' alternate stream enumeration functions
'
Function FindFirstStream:TFindStream( filename:String, striptags:Int = True)
	Return TFindStream.Create( filename, striptags)
EndFunction

Function FindNextStream:Int( fs:TFindStream)
	If fs Then Return fs.FindNext()
	Return False
EndFunction

Function CloseFindStream( fs:TFindStream)
	If fs Then fs.Close()
EndFunction


'
' alternate stream utility functions
'
Function DeleteFileStream:Int( fn:String)
	If fn And fn.Find(":") Then Return DeleteFileA( fn)
	Return False
EndFunction

Function CopyFileStream:Int( oldfn:String, newfn:String)
	If oldfn And newfn And oldfn.Find(":") And newfn.Find(":") Then
		Local in:TStream = ReadStream( oldfn), ok:Int = True
		If in Then
			Local out:TStream = WriteStream( newfn)
			If out Then
				Try
					CopyStream in, out
				Catch ex:TStreamWriteException
					ok = False
				EndTry
				out.Close()
			EndIf			
			in.Close()
		EndIf		
		Return ok
	EndIf
	Return False
EndFunction

Function RenameFileStream:Int( oldfn:String, newfn:String)
	If CopyFileStream( oldfn, newfn) Then
		DeleteFileStream( oldfn)
		Return True
	EndIf
	Return False		
EndFunction


?
