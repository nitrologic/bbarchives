; ID: 1919
; Author: grable
; Date: 2007-02-08 10:10:22
; Title: MAXGUI: Multiple file requester
; Description: [Win32] Allows for selecting multiple files in a file requester.

SuperStrict

?Win32
Import PUB.Win32

Private

Const MAX_BUFFER_SIZE:Int = 8192

Const OFN_ALLOWMULTISELECT:Int = 512
Const OFN_CREATEPROMPT:Int = $2000
Const OFN_ENABLEHOOK:Int = 32
Const OFN_ENABLESIZING:Int = $800000
Const OFN_ENABLETEMPLATE:Int = 64
Const OFN_ENABLETEMPLATEHANDLE:Int = 128
Const OFN_EXPLORER:Int = $80000
Const OFN_EXTENSIONDIFFERENT:Int = $400
Const OFN_FILEMUSTEXIST:Int = $1000
Const OFN_HIDEREADONLY:Int = 4
Const OFN_LONGNAMES:Int = $200000
Const OFN_NOCHANGEDIR:Int = 8
Const OFN_NODEREFERENCELINKS:Int = $100000
Const OFN_NOLONGNAMES:Int = $40000
Const OFN_NONETWORKBUTTON:Int = $20000
Const OFN_NOREADONLYRETURN:Int = $8000
Const OFN_NOTESTFILECREATE:Int = $10000
Const OFN_NOVALIDATE:Int = 256
Const OFN_OVERWRITEPROMPT:Int = 2
Const OFN_PATHMUSTEXIST:Int = $800
Const OFN_READONLY:Int = 1
Const OFN_SHAREAWARE:Int = $4000
Const OFN_SHOWHELP:Int = 16
Const OFN_SHAREFALLTHROUGH:Int = 2
Const OFN_SHARENOWARN:Int = 1
Const OFN_SHAREWARN:Int = 0

Type TOpenFileNameA
	Field lStructSize:Int
	Field hwndOwner:Int
	Field hInstance:Int
	Field lpstrFilter:Byte Ptr
	Field lpstrCustomFilter:Int
	Field nMaxCustFilter:Int
	Field nFilterIndex:Int
	Field lpstrFile:Byte Ptr
	Field nMaxFile:Int
	Field lpstrFileTitle:Byte Ptr
	Field nMaxFileTitle:Int
	Field lpstrInitialDir:Byte Ptr
	Field lpstrTitle:Byte Ptr
	Field Flags:Int
	Field nFileOffset:Short
	Field nFileExtension:Short
	Field lpstrDefExt:Byte Ptr
	Field lCustData:Int
	Field lpfnHook:Byte Ptr
	Field lpTemplateName:Byte Ptr
EndType

Extern "Win32"
	Function GetOpenFileName:Int( of:Byte Ptr) = "GetOpenFileNameA@4"	
EndExtern

Extern "C"
	Function memcpy( dst:Byte Ptr, src$z, sz:Int)
EndExtern

Public
?

Function RequestMultiFile:String[]( text:String, exts:String = Null, path:String = Null)
	?MacOS | Linux	
	Local res:String = RequestFile( test, exts, False, path)
	If res.Length <= 0 Then Return Null
	Return [res]
	?
	?Win32	
	Global hwndFocus:Int
	
	' prepare filename / path (ripped from BRL's RequestFile())
	Local file:String, dir:String
	path = path.Replace( "/","\" )	
	Local i:Int = path.FindLast( "\" )
	If i <> -1 Then
		dir = path[..i]
		file = path[i+1..]
	Else
		file = path
	EndIf
	' calculate default index of extension in extension list from path name
	Local ext:String, defext:Int,p:Int,q:Int
	p = path.Find(".")
	If (p>-1) Then
		ext = "," + path[p+1..].toLower() + ","
		Local exs:String = exts.toLower()
		exs = exs.Replace(":",":,")
		exs = exs.Replace(";",",;")
		p = exs.find(ext)
		If p >-1 Then
			Local q:Int = -1
			defext = 1
			While True
				q = exs.find(";",q+1)
				If q > p Then Exit
				If q = -1 Then 
					defext = 0
					Exit
				EndIf
				defext :+ 1
			Wend
		EndIf
	EndIf
	If exts Then
		If exts.Find(":") = -1 Then
			exts = "Files~0*." + exts
		Else
			exts = exts.Replace(":","~0*.")
		EndIf
		exts = exts.Replace(";","~0")
		exts = exts.Replace(",",";*.") + "~0"
	EndIf
	
	' allocate cstrings
	Local textp:Byte Ptr = text.ToCString()
	Local extsp:Byte Ptr = exts.ToCString()
	Local dirp:Byte Ptr
	If dir.Length > 0 Then dirp = dir.ToCString()
	
	' prepare file buffer
	Local buf:Byte[MAX_BUFFER_SIZE]
	memcpy( buf, file, file.Length)
	
	' initialize dialog options
	Local of:TOpenFileNameA = New TOpenFileNameA
	of.lStructSize = SizeOf(TOpenFileNameA)
	of.hwndOwner = GetActiveWindow()
	of.lpstrTitle = textp
	of.lpstrFilter = extsp
	of.nFilterIndex = defext
	of.lpstrFile = buf
	of.lpstrInitialDir = dirp
	of.nMaxFile = buf.Length
	of.Flags = OFN_HIDEREADONLY | OFN_NOCHANGEDIR | OFN_PATHMUSTEXIST | OFN_FILEMUSTEXIST | OFN_ALLOWMULTISELECT | OFN_EXPLORER ' | OFN_LONGNAMES
	
	' display dialog
	hwndFocus = GetFocus()
	Local n:Int = GetOpenFileName( of)
	SetFocus( hwndFocus)
	
	' free cstrings
	MemFree textp
	MemFree extsp
	If dirp Then MemFree dirp

	' failure ?
	If n <= 0 Then Return Null
	
	' count the number of files
	Local s:Byte Ptr = buf
	Local count:Int = 0
	While s[0] <> 0
		If s[1] = 0 Then
			count :+ 1
			s :+ 2
			If s[0] = 0 Then Exit
		EndIf
		s :+ 1
	Wend
	If count <= 0 Then Return Null
	
	' extract filenames into String array
	If count = 1 Then 
		'MARK: im following RequestFile() convention here, and returing "\" path seperators #1
		'Return [ String.FromCString( buf).Replace( "\", "/") ]
		Return [ String.FromCString( buf) ]
	Else
		Local result:String[] = New String[count]	
		s = buf
		For Local i:Int = 0 Until count
			result[i] = String.FromCString( s)
			s :+ result[i].Length + 1
		Next	
		'MARK: im following RequestFile() convention here, and returing "\" path seperators #2
		'result[0] = result[0].Replace( "\", "/") + "/"
		result[0] :+ "\"
		Return result
	EndIf
EndFunction
