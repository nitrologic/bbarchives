; ID: 2813
; Author: JoshK
; Date: 2011-01-19 13:41:46
; Title: File attributes
; Description: Control file attributes on Windows

SuperStrict

Import pub.win32

Private

Const FILE_ATTRIBUTE_HIDDEN:Int=$2

Extern "win32"
	Function SetFileAttributesA(lpFileName$z,dwFileAttributes:Int)
	Function GetFileAttributesA:Int(lpFileName$z)
EndExtern

Public

Function HideFile(path:String)
	Local attrib:Int
	attrib=GetFileAttributesA(path)
	If Not (FILE_ATTRIBUTE_HIDDEN & attrib)
		SetFileAttributesA(path,attrib|FILE_ATTRIBUTE_HIDDEN)
	EndIf
EndFunction

Function ShowFile(path:String)
	Local attrib:Int
	attrib=GetFileAttributesA(path)
	If (FILE_ATTRIBUTE_HIDDEN & attrib)
		SetFileAttributesA(path,attrib~FILE_ATTRIBUTE_HIDDEN)	
	EndIf
EndFunction

Function FileHidden:Int(path:String)
	Local attrib:Int
	attrib=GetFileAttributesA(path)
	If (FILE_ATTRIBUTE_HIDDEN & attrib)
		Return True
	EndIf
EndFunction
