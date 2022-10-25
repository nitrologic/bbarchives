; ID: 2371
; Author: Plash
; Date: 2008-12-13 02:51:36
; Title: mcc (module cleaner)
; Description: Delete '.bmx', 'win32', 'linux' and 'macos' module compilations

Rem
	
	MCC is public-domain
	
	CHANGES (v0.79)
		Changed file checking routine and added support for threaded compilations ('.mt')
		
	CHANGES (v0.78)
		Inital, public release
	
	Copyright Tim Howard 2008 (aka Plash)
	
End Rem

SuperStrict


Framework brl.standardio

Import brl.system

'Take away the directory
Global appargslen:Int = AppArgs.Length - 1

Global VERSION:String = "0.79"
Global prop_dir:String = "", prop_recursive:Int = True

Global plats:String[] = ["win32", "linux", "macos"]

Print("MCC -- version " + VERSION)

'Internally, blitzmax sends its directory to itself, we need to skip over this line
If appargslen > 0 'AppArgs.Length > 1
	
	For Local i:Int = 1 To appargslen
	  Local a:String = AppArgs[i].ToLower()
		
		Select(a)
			Case "-dir", "/dir"
				If (appargslen - (i - 1)) >= 2
				  Local b:String = AppArgs[i + 1]
					
					Select(b.ToLower())
						Case "/?"
							Print("USAGE:")
								Print("~t-dir ~q/directory/path~q")
								Print()
								
							Print("DESCRIPTION:")
								Print("~tSet the path to remove files from (will recursively search all")
								Print("~tchild directories if 'recursive' is turned on)")
							
						Default
							
							If (appargslen - (i - 1)) >= 2
							  Local tmp:String
								
								tmp = AppArgs[i + 1]
								
								If tmp[0] = 34 And tmp[tmp.Length - 1] = 34
									
									prop_dir = tmp[1..tmp.Length - 1]
									
								Else
									
									prop_dir = tmp
									
								End If
								
								prop_dir = prop_dir.Replace("\", "/")
								If prop_dir[prop_dir.Length - 1] = 47 Then prop_dir = prop_dir[0..prop_dir.Length - 1]
								
								i:+1
							Else
								
								InvalidNOps(a + " " + b)
								
							End If
							
					End Select
					
				Else
					
					InvalidNOps(a)
					
				End If
				
			Case "-recursive", "-r", "/recursive", "/r"
				If (appargslen - (i - 1)) >= 2
				  Local b:String = AppArgs[i + 1]
					
					Select(b.ToLower())
						Case "/?"
							Print("USAGE:")
								Print("~t-recursive 1, true, on/0, false, off")
								Print()
								
							Print("DESCRIPTION:")
								Print("~tIf set to true, the directory ('-help dir') will be recursively searched")
							
						Default
							
							If (appargslen - (i - 1)) >= 2
							  Local tmp:String
								
								tmp = AppArgs[i + 1].ToLower()
								
								Select(tmp)
									Case "1", "true", "on"
										prop_recursive = True
										
									Case "0", "false", "off"
										prop_recursive = False
										
								End Select
								
								i:+1
							Else
								
								InvalidNOps(a + " " + b)
								
							End If
							
					End Select
					
				Else
					
					InvalidNOps(a)
					
				End If
				
			Case "-help", "/help"
				If (appargslen - (i - 1)) >= 2
				  Local b:String = AppArgs[i + 1]
					
					Select(b.ToLower())
						Case "dir"
							Print("USAGE:")
								Print("~t-dir ~q/directory/path~q")
								Print()
								
							Print("DESCRIPTION:")
								Print("~tSet the path to remove files from (will recursively search all")
								Print("~tchild directories if 'recursive' is turned on)")
							
						Case "recursive", "r"
							Print("USAGE:")
								Print("~t-recursive 1, true, on/0, false, off")
								Print()
								
							Print("DESCRIPTION:")
								Print("~tIf set to true, the directory ('-help dir') will be recursively searched")
							
						Case "help"
							Print("DESCRIPTION:")
								Print("~tGives informa-- wait a second.. haha! funny, very funny.")
							
						Case "/?"
							Print("USAGE:")
								Print("~t-help $COMMAND$ ~t~t(for help on a command)")
								Print("~t-help ~t~t(for a list of commands)")
								Print()
								
							Print("DESCRIPTION:")
								Print("~tGives information on commands")
							
						Default
							Print("Unknown command, type '-help' for a list of commands")
							
					End Select
					
				Else
					
					Print("LIST OF PROPERTIES/COMMANDS:")
						Print("~t-dir ~t~t(type '-dir /?', or '-help dir' for more information)")
						Print("~t-recursive ~t(type '-recursive /?', or '-help recursive' for more information)")
						Print()
						Print("~t-help ~t~t(type '-help' for this list, or '-help $COMMAND$' for command information)")
						
				End If
				
				End
				
			Default
				Print("Unknown command: " + a + " ~t~t(type '-help' for a list of commands)")
				End
				
		End Select
		
	Next
	
Else
	
	RQDir()
	
End If

If prop_dir = Null
	
	RQDir()
	
End If

If FileType(prop_dir) = FILETYPE_DIR
	
	Print("Removing 'win32', 'macos', 'linux' module compilations, and '.bmx' folders from ~q" + prop_dir + "/~q")
	DoModRemove()
	
Else
	
	Print("The path, ~q" + prop_dir + "~q, does not exist (or it is a file, and not a directory)")
	End
	
End If


''
Function DoModRemove()
	
	prop_dir:+"/"
	
	DoFolder(prop_dir, prop_recursive)
	
End Function

Function DoFolder(path:String, orr_recursive:Int)
  Local dir:Int, file:String
	
	dir = ReadDir(path)
	
	Repeat
		
		'No NextDir function :(
		file = NextFile(dir)
		
		If file = "" Then Exit
		If file <> "." And file <> ".." Then
			
			Select(FileType(path + file))
				Case FILETYPE_FILE
					
					If file[file.Length - 1] = 97 Or file[file.Length - 1] = 105
						If IsFileBad(file, 1)
							
							'Print(".." + MinimizePath(path) + file)
							RemoveFile(path + file)
							
						Else
							
							If IsFileBad(file, 2)
								
								'Print(".." + MinimizePath(path) + file)
								RemoveFile(path + file)
								
							End If
							
						End If
					End If
					
				Case FILETYPE_DIR
					
					'Print("dd: ~q" + file + "~q")
					If file.ToLower() = ".bmx"
						
						'Print(".." + MinimizePath(path) + file)
						DeleteDir(path + file, True)
						
					Else If orr_recursive = True And file[file.Length - 4..file.Length].ToLower() = ".mod"
						
						DoFolder(path + file + "/", True)
						
					End If
					
			End Select
			
		EndIf
		
	Forever
	
	CloseDir(dir)
	
End Function

Function IsFileBad:Int(file:String, _type:Int)
  Local fend:String, fendt:String
	
	If _type = 1
		
		fend = file[file.Length - 20..file.Length].ToLower()
		fendt = file[file.Length - 23..file.Length].ToLower()
		
		For Local i:Int = 0 To 2
			If fend = ".release." + plats[i] + ".x86.a" Or fend = ".release." + plats[i] + ".x86.i"
				
				Return(True)
				
			Else If fendt = ".release.mt." + plats[i] + ".x86.a" Or fendt = ".release.mt." + plats[i] + ".x86.i"
				
				Return(True)
				
			End If
		Next
		
	Else If _type = 2
		
		fend = file[file.Length - 18..file.Length].ToLower()
		fendt = file[file.Length - 21..file.Length].ToLower()
		
		For Local i:Int = 0 To 2
			
			If fend = ".debug." + plats[i] + ".x86.a" Or fend = ".debug." + plats[i] + ".x86.i"
				
				Return(True)
				
			Else If fendt = ".debug.mt." + plats[i] + ".x86.a" Or fendt = ".debug.mt." + plats[i] + ".x86.i"
				
				Return(True)
				
			End If
				
		Next
		
	End If
	
	Return(False)
	
End Function

Function RemoveFile(path:String)
	
	Print(".." + MinimizePath(path))
	DeleteFile(path)
	
End Function

Function MinimizePath:String(path:String)
	
  Local ind:Int, lind:Int = -1
	
	Repeat
		ind = path.Find("/", ind + 1)
		
		If ind > -1
			
			If path.Find("/", ind + 1)= -1 Then Exit
			
		Else
			Exit
		End If
		
		lind = ind
	Forever
	
	Return(path[lind..path.Length])
	
End Function


Function RQDir()
	
	prop_dir = RequestDir("Select path for mcc", CurrentDir())
	
	If prop_dir = Null
		
		Print("No directory selected (cancelled)")
		End
		
	Else
		
		prop_dir = prop_dir.Replace("\", "/")
		If prop_dir[prop_dir.Length - 1] = 47 Then prop_dir = prop_dir[0..prop_dir.Length - 1]
		
	End If
	
End Function

Function InvalidNOps(cmd:String)
	
	Print("ERROR: Invalid number of arguments, see '" + cmd + " /?'")
	End
	
End Function
