; ID: 1973
; Author: skn3[ac]
; Date: 2007-03-22 19:50:54
; Title: Win32 DriveInfo GetHardDrives() GetCdDrives() GetNetworkDrives() GetRemoveableDrives() GetRamDrives()
; Description: Get Hard drive / cd drive / flash drive / etc capacity / available bytes

Strict

'Import brl.standardio
Import brl.linkedlist

Const DRIVE_UNKNOWN:Int = 0     'The drive Type cannot be determined.
Const DRIVE_NO_ROOT_DIR:Int = 1 'The root path is invalid; For example, there is no volume is mounted at the path.
Const DRIVE_REMOVABLE:Int = 2   'The drive has removable media; For example, a floppy drive, thumb drive, Or flash card reader.
Const DRIVE_FIXED:Int = 3       'The drive has fixed media; For example, a hard drive Or flash drive.
Const DRIVE_REMOTE:Int = 4      'The drive is a remote (network) drive.
Const DRIVE_CDROM:Int = 5       'The drive is a CD-ROM drive.
Const DRIVE_RAMDISK:Int = 6     'The drive is a RAM disk.

Extern "win32"
	Function _GetDiskFreeSpaceEx(lpdirectoryname:Byte Ptr,lpfreebytesavailable:Byte Ptr,lptotalnumberofbytes:Byte Ptr,lptotalnumberoffreebytes:Byte Ptr) = "GetDiskFreeSpaceExA@16"
	Function _GetLogicalDriveStrings(nbufferlength:Int,lpbuffer:Byte Ptr) = "GetLogicalDriveStringsA@8"
	Function _GetDriveType(lprootpathname:Byte Ptr) = "GetDriveTypeA@4"
End Extern

Type tdriveinfo
	Const buffersize:Int = 256
	
	Global buffer:Byte[tdriveinfo.buffersize]
	
	Field drivetype:Int
	Field availablebytes:Long
	Field totalbytes:Long
	Field freebytes:Long
	Field label:String
	
	Function _setcstring:Byte Ptr(nstring:String)
		Local temp_i:Int
		'write string
		For temp_i = 0 Until nstring.length
			tdriveinfo.buffer[temp_i] = nstring[temp_i]
		Next
		'write null
		tdriveinfo.buffer[nstring.length] = 0
		
		Return tdriveinfo.buffer
	End Function
	
	Function _getfromstring:String()
		Local temp_i:Int
		Local temp_string:String
		For temp_i = 0 Until tdriveinfo.buffersize
			If temp_i = tdriveinfo.buffersize-1 Or tdriveinfo.buffer[temp_i] = 0
			Else
				temp_string = temp_string + Chr(tdriveinfo.buffer[temp_i])
			End If
		Next
		Return temp_string
	End Function
	
	Function _bytestospacestring:String(nspace:Long)
		If nspace < 1024 '1 k
			Return tdriveinfo._trimfloat(nspace)+" b"
		ElseIf nspace < 1048576 '1 meg
			Return tdriveinfo._trimfloat(nspace / 1024.0)+" k"
		ElseIf nspace < 1073741824 '1 gig
			Return tdriveinfo._trimfloat(nspace / 1048576.0)+" meg"
		Else
			Return tdriveinfo._trimfloat(nspace / 1073741824.0)+" gig"
		End If
	End Function
	
	Function _trimfloat:String(nfloat:Float,nlength:Int = 2)
		Local temp_pos:Int
		Local temp_string:String = String(nFloat)
		temp_pos = temp_string.find(".")
		If temp_pos > -1 Return temp_string[0..temp_pos+1+nlength]
		Return temp_string
	End Function
	
	Method PercentFree:Float()
		Return (100.0 / totalbytes) * (totalbytes - freebytes)
	End Method
	
	Method PercentUsed:Float()
		Return (100.0 / totalbytes) * availablebytes
	End Method
	
	Method CapacityString:String()
		Return tdriveinfo._bytestospacestring(totalbytes)
	End Method
	
	Method AvailableString:String()
		Return tdriveinfo._bytestospacestring(availablebytes)
	End Method
	
	Method UsedString:String()
		Return tdriveinfo._bytestospacestring(totalbytes - freebytes)
	End Method
	
	Method Capacity:Long()
		Return totalbytes
	End Method
	
	Method Available:Long()
		Return availablebytes
	End Method
	
	Method Used:Long()
		Return totalbytes - freebytes
	End Method
End Type

Function GetAllDrives:TList(nfilter:Int=-1)
	Local temp_list:TList = CreateList()
	Local temp_drives:String
	Local temp_label:String
	Local temp_i:Int
	Local temp_type:Int
	
	'get list of drives
	_GetLogicalDriveStrings(tdriveinfo.buffersize,tdriveinfo.buffer)
	temp_drives = tdriveinfo._getfromstring()
	'read in list of drives
	For temp_i = 0 Until temp_drives.length Step 3
		temp_label = temp_drives[temp_i..temp_i+3]
		
		'create tdriveinfo type
		temp_type = _GetDriveType(tdriveinfo._setcstring(temp_label))
		If nfilter = -1 Or temp_type = nfilter
			'get drive space details
			Local temp_disk:tdriveinfo = New tdriveinfo
			'get space details
			_GetDiskFreeSpaceEx(tdriveinfo._setcstring(temp_label),Varptr(temp_disk.availablebytes),Varptr(temp_disk.totalbytes),Varptr(temp_disk.freebytes))
			'set other details
			temp_disk.label = temp_label.tolower()
			temp_disk.drivetype = temp_type
			
			temp_list.addlast(temp_disk)
		End If
	Next
	
	Return temp_list
End Function

Function GetCDDrives:TList()
	Return GetAllDrives(DRIVE_CDROM)
End Function

Function GetHardDrives:TList()
	Return GetAllDrives(DRIVE_FIXED)
End Function

Function GetRamDrives:TList()
	Return GetAllDrives(DRIVE_RAMDISK)
End Function

Function GetRemoveableDrives:TList()
	Return GetAllDrives(DRIVE_REMOVABLE)
End Function

Function GetNetworkDrives:TList()
	Return GetAllDrives(DRIVE_REMOTE)
End Function
