; ID: 3038
; Author: BlitzSupport
; Date: 2013-03-15 14:31:47
; Title: Get list of valid drives (Windows-only)
; Description: Retrieve list of drives on a Windows system

SuperStrict

Extern "win32"
	Function GetLogicalDriveStringsA (buffer_size:Int, buffer:Byte Ptr)
	Function GetDriveTypeA (name:Byte Ptr)
End Extern

Function GetDriveList:TList ()

	Local drivelist:TList = CreateList ()
	
	' Stupid, stupid Windows...
	
	Local mem:TBank = CreateBank (255)
	Local result:Int = GetLogicalDriveStringsA (255, BankBuf (mem))

	If result > 255
		ResizeBank (mem, result)
		result = GetLogicalDriveStringsA (result, BankBuf (mem))
	EndIf
	
	If result
	
		Local drive:String = ""
		
		For Local a:Int = 0 To result - 1
	
			Local char:Byte = PeekByte (mem, a)
	
			If char
				drive = drive + Chr (char)
			Else
				ListAddLast drivelist, drive
				drive$ = ""
			EndIf
			
		Next
	
		mem = Null
		Return drivelist
	
	Else
	
		mem = Null
		Return Null
	
	EndIf

End Function

Function GetDriveType:String (drive:String)

	' You can pass "A:" or "A:\"...
	
	If Right (drive, 1) <> "\" Then drive = drive + "\"
	
	Const DRIVE_UNKNOWN:Int = 0
	Const DRIVE_NO_ROOT_DIR:Int = 1
	Const DRIVE_REMOVABLE:Int = 2
	Const DRIVE_FIXED:Int = 3
	Const DRIVE_REMOTE:Int = 4
	Const DRIVE_CDROM:Int = 5
	Const DRIVE_RAMDISK:Int = 6

	Select GetDriveTypeA (drive.ToCString ())
		Case DRIVE_UNKNOWN
			Return "Unknown drive type"
		Case DRIVE_NO_ROOT_DIR
			Return "Drive doesn't exist"
		Case DRIVE_REMOVABLE
			Return "Removable drive"
		Case DRIVE_FIXED
			Return "Fixed drive"
		Case DRIVE_REMOTE
			Return "Network drive"
		Case DRIVE_CDROM
			Return "CD/DVD drive"
		Case DRIVE_RAMDISK
			Return "RAM-based drive"
	End Select

End Function

Local drives:TList = GetDriveList ()

If drives

	For Local letter:String = EachIn drives
		' Print letter					' Drive name only...
		Print letter + "~t~t" + GetDriveType (letter)	' Drive and type...
	Next
	
	' Print "$:\~t~t" + GetDriveType ("$:\") ' Non-existent drive test!
	
EndIf
