; ID: 3034
; Author: Henri
; Date: 2013-03-01 14:25:33
; Title: Search MinGW files
; Description: Search and copy MinGW files

Strict

OnEnd Clean

'Folders
'-------
Local src_dir:String = "C:\Blitzmax" 'Replace(RequestDir("Select Blitzmax folder"),"/","\")
Local min_dir:String = "C:\MinGW" 'Replace(RequestDir("Select MinGW folder"),"/","\")
Local dst_dir:String = "C:\Temp\MinGW_files" 'Replace(RequestDir("Select destination folder"),"/","\")

Const COPY_FILES:Int = True	'Set to false to only display results

'Get library files list
'----------------------
If Not src_dir End 
FileList(src_dir+"\lib",True)

'Add ar.exe and ld.exe to list
'-----------------------------
If Tfile.list
	Local myfile:Tfile
	myfile = New Tfile
	myfile.file = "ar.exe"
	myfile = New Tfile
	myfile.file = "ld.exe"
Else
	End
EndIf

'Search MinGW directory
'----------------------
If Not min_dir End
FileList(min_dir,True,True)

'Select destination directory
'----------------------------
If COPY_FILES	
	If Not dst_dir End
	If FileSize(dst_dir)=-1
		Notify "Destination directory does not exist. Creating folder "+dst_dir
		If Not CreateDir(dst_dir,True) Notify "Could not create directory. Ending program";End
	EndIf
EndIf

'Show results
'------------
If Not Tfile.list End
If Tfile.list.count() > 0
	For Local myfile:Tfile = EachIn Tfile.list
		Print "Found="+myfile.found+" |"+myfile.path+myfile.file+"|"+myfile.size
		If myfile.found And COPY_FILES
			If Not CopyFile(myfile.path+myfile.file,dst_dir+"\"+myfile.file)
				Notify "Error: Could not copy file "+myfile.file,True
			EndIf
		EndIf
	Next
EndIf

End

Function FileList(folder:String,recurse:Int,copy:Int=False )
	Local thisFolder:Int = ReadDir(folder)	
	If thisFolder	
		Repeat	
			Local file:String = NextFile(thisFolder)	
			If Len(file)=0 Exit	
			Select FileType(folder + "\" + file)	
			Case 1	
				If copy
					For Local myfile:Tfile = EachIn Tfile.list
						If myfile.found Continue
						If file.ToUpper()=myfile.file.ToUpper()
							myfile.path = folder+"\"
							myfile.size = FileSize(folder+"\"+file)
							myfile.found = True
							Exit
						EndIf
					Next
				Else
					Local myfile:Tfile = New Tfile
					myfile.file = file
				EndIf
			Case 2	
				Select file							
				Case ".",".."
				Default
					If recurse FileList(folder + "\" + file,recurse,copy)
				End Select		
			End Select			
					
		Forever	
		CloseDir thisFolder	
	End If
End Function

Function Clean()
	If Tfile.list Tfile.list.clear() ; Tfile.list = Null
EndFunction

Type Tfile
	Field path:String
	Field file:String
	Field found:Int = 0
	Field size:Int
	Global list:TList
	
	Method New()
		If list=Null list=New TList
		list.addlast(Self)
	EndMethod
EndType
