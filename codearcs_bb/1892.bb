; ID: 1892
; Author: Subirenihil
; Date: 2006-12-27 14:47:46
; Title: Self-Destructing .exe
; Description: A program that removes itself when its done running

;Compile in its own folder as "Self-Destruct.exe"
;Run the compiled version and it will delete itself
;
;!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
;!! DO NOT RUN FROM WITHIN BLITZ !!
;!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

;The following lines must be in shell32.decls
;   .lib "shell32.dll"
;   ShellExecute%(hwnd%,Operation$,File$,Parameters$,Directory$,ShowCmd%):"ShellExecuteA"

Graphics 400,300,0,2
SetBuffer FrontBuffer()

SelfDestruct
Text 200,150,"Press any key to self destruct...",1,1
WaitKey
End

Function SelfDestruct()
	dir$=SystemProperty("appdir")
	If Right$(dir$,1)<>"\" Then dir$=dir$+"\"

	tempdir$=SystemProperty("tempdir")
	If Right$(tempdir$,1)<>"\" Then tempdir$=tempdir$+"\"
	
	file$=dir$+"Self-Destruct.exe"
	temp$=tempdir$+"_uninstSelf-Destruct.bat"
	
	bat=WriteFile(temp$)
	
	WriteLine bat,":Repeat"
	WriteLine bat,"del "+Chr$(34)+file$+Chr$(34)
	WriteLine bat,"if exist "+Chr$(34)+file$+Chr$(34)+" goto Repeat"
	WriteLine bat,"rmdir "+Chr$(34)+Left$(dir$,Len(dir$)-1)+Chr$(34)
	WriteLine bat,"del "+Chr$(34)+temp$+Chr$(34)
	
	CloseFile bat
	ShellExecute 0,"open",Chr$(34)+temp$+Chr$(34),"","",0
End Function
