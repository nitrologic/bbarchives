; ID: 2399
; Author: Otus
; Date: 2009-01-24 12:21:06
; Title: Blitz Prune
; Description: Cleans up source directories

SuperStrict

' Prunes a Blitz source directory tree.
' Options to remove .bak files, .bmx 
' directories and (.debug).exe files.

Framework MaxGUI.Drivers

Import BRL.FileSystem

Import "autoform.bmx"


' Main
Local f:TForm = New TForm
If AutoForm( "Options", f ) Prune f.Base_directory, f
' End Main


Type TForm
	
	Field Base_directory:String		= CurrentDir()	{ directory }
	
	Field Recursive:Int				= True		{ bool }
	
	Field Remove_BAK_files:Int		= True		{ bool }
	
	Field Remove_BMX_directories:Int	= True		{ bool }
	
	Field Remove_debug_executables:Int	= False		{ bool }
	
	Field Remove_other_executables:Int	= False		{ bool }
	
End Type

Function Prune(dirname:String, f:TForm)
	dirname = StripSlash(dirname)
	Local dir:Int = ReadDir(dirname)
	dirname :+ "/"
	
	Local file:String = NextFile(dir)
	While file
		Select FileType(dirname+file)
		Case FILETYPE_DIR
			If file="." Or file=".."
				'Skip
			Else If file=".bmx"
				If f.Remove_BMX_directories
					Print dirname+file
					DeleteDir dirname+file, True
				End If
			Else
				If f.Recursive
					Prune dirname+file, f
				End If
			End If
			
		Case FILETYPE_FILE
			Local ext:String = ExtractExt(file).ToLower()
			If ext="bak"
				If f.Remove_BAK_files
					Print dirname+file
					DeleteFile dirname+file
				End If
			ElseIf ext="exe"
				If file.ToLower().EndsWith(".debug.exe")
					If f.Remove_debug_executables
						Print dirname+file
						DeleteFile dirname+file
					End If
				Else
					If f.Remove_other_executables
						Print dirname+file
						DeleteFile dirname+file
					End If
				End If
			End If
		End Select
		
		file = NextFile(dir)
	Wend
	
End Function
