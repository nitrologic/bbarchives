; ID: 2525
; Author: Ked
; Date: 2009-07-10 00:20:23
; Title: RecycleBinPath()
; Description: Returns the physical path of the Recycle Bin (Windows XP and earlier)

Function RecycleBinPath:String()
	Local drive:String=GetEnv_("SYSTEMDRIVE")
	Local os:String=GetEnv_("OS")
	
	If Lower(os)="windows_nt"
		drive:+"\RECYCLER\"
	Else
		drive:+"\RECYCLED\"
	EndIf
	
	Local dir:String[]=LoadDir(drive)
	Local path:String=Null
	If dir
		path=drive+dir[0]
	EndIf
	
	Return path
EndFunction
