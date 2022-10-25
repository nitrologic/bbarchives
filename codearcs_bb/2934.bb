; ID: 2934
; Author: Danny
; Date: 2012-03-15 11:35:54
; Title: Check Free Disk Space
; Description: Test a device or path for enough disk space left to write your data

; Add to Kernal32.decls:
;
; .lib "Kernel32.dll"
; API_GetDiskFreeSpaceEx%(lpDirectoryName$, lpFreeBytesAvailable*, lpTotalNumberOfBytes*, lpTotalNumberOfFreeBytes*) : "GetDiskFreeSpaceExA"


Type LongInt
	Field a%, b%
End Type

Function HasFreeSpace%(path$, minFreeBytes%)
	
	;# Returns TRUE (1), FALSE(0) or -1 when path not found/accessible/mounted
	;
	;# Max bytes that can be tested is 2,147,483,647 bytes (=2.1Gb = 2048 Mb)
	;# Thus always returns TRUE if more than 2.1Gb is free!
	
	If minFreeBytes < 0 Then minFreeBytes = ((2048 *1024) *1024) -1
	
	GFS_out_FABytes.LongInt = New LongInt
	GFS_out_TotBytes.LongInt = New LongInt
	GFS_out_TFBytes.LongInt = New LongInt
	
	Local ret% = False
	
	If API_GetDiskFreeSpaceEx(path, GFS_out_FABytes, GFS_out_TotBytes, GFS_out_TFBytes)
		
		If GFS_out_FABytes\b <> 0 
			; when B <> 0 then MORE than 4.2Gb free
			ret = True
		ElseIf (GFS_out_FABytes\b = 0) And (GFS_out_FABytes\a < 0)
			; when B = 0, A < 0, MORE than 2.1Gb free
			ret = True
		ElseIf GFS_out_FABytes\a > minFreeBytes Then
			; A contains remaining bytes free
			ret = True
		Else
			; NOT ENOUGH SPACE FREE!
			ret = False
		EndIf
		
	Else
		; Path not found, drive doesn't exist or media not mounted
		ret = -1
		
	EndIf
	
	; purge
	Delete GFS_out_FABytes
	Delete GFS_out_TotBytes
	Delete GFS_out_TFBytes
	; return
	Return ret
	
End Function


; EXAMPLE USE

Local drive$ = "C:"

Local megabyte% = 100
Local bytes% = ((megabyte *1024) *1024)

Select HasFreeSpace(drive, bytes)
	Case True
		Print " "+drive+" has more than "+megabyte+" Mb free ("+bytes+" bytes)"
	Case False
		Print " "+drive+" DOES NOT have "+megabyte+" Mb free ("+bytes+" bytes) !"
	Case -1
		Print " "+drive+" not accessible, found or mounted!"
End Select

Print "" : Print " <any key>"
WaitKey
End
