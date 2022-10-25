; ID: 2634
; Author: Otus
; Date: 2009-12-27 07:12:41
; Title: OOP File System Interface
; Description: Object oriented wrapper for BRL.FileSystem

SuperStrict

Rem
bbdoc: File system interface
End Rem
Module Otus.FS

ModuleInfo "Version: 1.00"
ModuleInfo "Author: Jan Varho"
ModuleInfo "License: Public domain (none)"

Import BRL.FileSystem

Rem
bbdoc: A file in the file system
about:
Open files using #Open. TFile objects can be used with OpenStream.
End Rem
Type TFile
	
	Field _path:String
	
	Rem
	bbdoc: Read the file
	returns: A file stream
	End Rem
	Method Read:TStream()
		Return ReadFile(_path)
	End Method
	
	Rem
	bbdoc: Write the file
	returns: A file stream
	End Rem
	Method Write:TStream()
		Return WriteFile(_path)
	End Method
	
	Rem
	bbdoc: Open the file
	returns: A file stream
	End Rem
	Method OpenStream:TStream(readable% = True, writeable% = True)
		Return OpenFile(_path, readable, writeable)
	End Method
	
	Rem
	bbdoc: Remove the file
	returns: True if successful
	End Rem
	Method Remove:Int()
		If DeleteFile(_path)
			_path = ""
			Return True
		End If
		Return False
	End Method
	
	Rem
	bbdoc: Rename the file
	returns: True if successful
	End Rem
	Method Rename:Int(name:String)
		If RenameFile(_path, name)
			_path = RealPath(name)
			Return True
		End If
		Return False
	End Method
	
	Rem
	bbdoc: Copy the file
	returns: A TFile object for the copy if successful
	End Rem
	Method Copy:TFile(dest:String)
		If CopyFile(_path, dest)
			Return Open(dest)
		End If
		Return Null
	End Method
	
	Rem
	bbdoc: Get the parent directory of the file
	returns: A #TDir object
	End Rem
	Method GetDir:TDir()
		Return TDir.Open( ExtractDir(_path) )
	End Method
	
	Rem
	bbdoc: Get file extension
	returns: A string representing the file extension
	End Rem
	Method GetExt:String()
		Return ExtractExt(_path)
	End Method
	
	Rem
	bbdoc: Get the filename
	returns: A string representing the name of the file
	End Rem
	Method GetName:String()
		Return StripDir(_path)
	End Method
	
	Rem
	bbdoc: Get filemode
	returns: The file's mode flags
	End Rem
	Method GetMode:Int()
		Return FileMode(_path)
	End Method
	
	Rem
	bbdoc: Set filemode
	returns: True if successful
	End Rem
	Method SetMode:Int(mode:Int)
		SetFileMode _path, mode
		Return FileMode(_path)=mode
	End Method
	
	Rem
	bbdoc: Get path
	returns: The full path of the file
	End Rem
	Method GetPath:String()
		Return _path
	End Method
	
	Rem 
	bbdoc: Get filesize
	returns: The size of the file in bytes
	End Rem
	Method GetSize:Int()
		Return FileSize(_path)
	End Method
	
	Rem
	bbdoc: Last modified
	returns: The timestamp of the last modification to the file
	End Rem
	Method GetTime:Int()
		Return FileTime(_path)
	End Method
	
	Rem
	bbdoc: Creates a new file
	returns: A TFile object if successful
	End Rem
	Function Create:TFile(path:String)
		If Not CreateFile(path) Then Return Null
		Return Open(path)
	End Function
	
	Rem
	bbdoc: Opens a file
	returns: A TFile object if successful
	about:
	You can test if it's a directory by casting to #TDir
	End Rem
	Function Open:TFile(path:String)
		Local f:TFile
		Select FileType(path)
		Case FILETYPE_FILE
			f = New TFile
		Case FILETYPE_DIR
			f = New TDir
		Default
			Return Null
		End Select
		f._path = RealPath(path)
		Return f
	End Function
	
End Type

Rem
bbdoc: A directory in the filesystem
about:
You can open a directory using #Open and cycle the files in it using EachIn.
Note that directories are also files.
End Rem
Type TDir Extends TFile
	
	Rem
	bbdoc: Changes the current directory
	End Rem
	Method ChangeTo:Int()
		Return ChangeDir(_path)
	End Method
	
	Method Read:TStream()
		Return Null
	End Method
	
	Method Write:TStream()
		Return Null
	End Method
	
	Method OpenStream:TStream(readable% = True, writeable% = True)
		Return Null
	End Method
	
	Rem
	bbdoc: Removes the directory
	returns: True on success
	about:
	Removing directories is recursive!
	End Rem
	Method Remove:Int()
		If DeleteDir(_path, True)
			_path = ""
			Return True
		End If
		Return False
	End Method
	
	Method Copy:TDir(dest:String)
		If CopyDir(_path, dest)
			Return Open(dest)
		End If
		Return Null
	End Method
	
	Method GetExt:String()
		Return Null
	End Method
	
	Rem
	bbdoc: Open a file in the directory
	returns: A #TFile object for the file
	End Rem
	Method OpenFile:TFile(name:String)
		Return TFile.Open(GetPath()+"/"+name)
	End Method
	
	Rem
	bbdoc: Open a sub directory
	returns: A #TDir object for the directory
	End Rem
	Method OpenDir:TDir(name:String)
		Return TDir.Open(GetPath()+"/"+name)
	End Method
	
	Rem
	bbdoc: Create a file in the directory
	returns: A #TFile object for the new file
	End Rem
	Method CreateFile:TFile(name:String)
		Return TFile.Create(GetPath()+"/"+name)
	End Method
	
	Rem
	bbdoc: Create a sub directory
	returns: A #TDir object for the new directory
	End Rem
	Method CreateDir:TDir(name:String)
		Return TDir.Create(GetPath()+"/"+name)
	End Method
	
	Method ObjectEnumerator:TDirEnum()
		Return TDirEnum.Create(Self)
	End Method
	
	Rem
	bbdoc: Creates a directory
	returns: A TDir object if successful
	End Rem
	Function Create:TDir(path:String)
		If FileType(path)<>FILETYPE_DIR And Not CreateDir(path, True) Then Return Null
		Return Open(path)
	End Function
	
	Rem
	bbdoc: Current directory
	returns: A TDir object for the current directory
	End Rem
	Function Current:TDir()
		Return Open(CurrentDir())
	End Function
	
	Rem
	bbdoc: Opens a directory
	returns: A TDir object if successful
	End Rem
	Function Open:TDir(path:String)
		If FileType(path)<>FILETYPE_DIR Then Return Null
		Local d:TDir = New TDir
		d._path = RealPath(path)
		Return d
	End Function
	
End Type

Type TDirEnum
	
	Field _dirname:String
	
	Field _handle:Int, _next:String
	
	Method Delete()
		If _handle CloseDir _handle
	End Method
	
	Method HasNext:Int()
		If _next Then Return True
		If _handle CloseDir _handle
		_handle = 0
		Return False
	End Method
	
	Method NextObject:Object()
		Local f:TFile = TFile.Open(_dirname+_next)
		_next = NextFile(_handle)
		While _next="." Or _next=".."
			_next = NextFile(_handle)
		Wend
		Return f
	End Method
	
	Function Create:TDirEnum(d:TDir)
		Local e:TDirEnum = New TDirEnum
		e._dirname = d.GetPath() + "/"
		e._handle = ReadDir(d._path)
		e.NextObject
		Return e
	End Function
	
End Type

New TFileStreamFactory

Type TFileStreamFactory Extends TStreamFactory
	
	Method CreateStream:TStream( url:Object, proto$, path$, readable%, writeable% )
		Local f:TFile = TFile(url)
		If Not f Return Null
		Return f.OpenStream(readable, writeable)
	End Method
	
End Type
