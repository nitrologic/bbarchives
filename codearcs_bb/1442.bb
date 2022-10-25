; ID: 1442
; Author: turtle1776
; Date: 2005-08-13 16:07:29
; Title: FileDate$() and GetFileTime$()
; Description: Get the time and date that the given file was created, last accessed, or last written.

;===========
;GetFileTime
;===========
;By Patrick Lester (turtle1776)

;This program contains 2 main functions, FileDate$() and FileTime$(), which 
;get the time and date that the given file was created, last accessed, or last written. 
;It also contains a number of lower-level functions that can get specific time
;and date information (month, year, day of week, etc.).

;These functions use Windows API calls, so you need to first add the following
;to your .decls text file (mine is called user32.decls) in your Blitz userlibs folder.

;.lib "kernel32.dll"
;api_OpenFile% (lpFileName$, lpReOpenBuff*, wStyle%) : "OpenFile"
;api_GetFileTime% (hFile%, lpCreationTime*, lpLastAccessTime*, lpLastWriteTime*) : "GetFileTime"
;api_FileTimeToLocalFileTime% (lpFileTime*, lpLocalFileTime*) : "FileTimeToLocalFileTime"
;api_FileTimeToSystemTime% (lpFileTime*, lpSystemTime*) : "FileTimeToSystemTime"
;api_CloseHandle% (hObject%) : "CloseHandle"

;For more information, see:
;http://msdn.microsoft.com/library/default.asp?url=/library/en-us/sysinfo/base/time_reference.asp
;http://msdn.microsoft.com/library/default.asp?url=/library/en-us/fileio/fs/openfile.asp
;http://msdn.microsoft.com/library/default.asp?url=/library/en-us/sysinfo/base/closehandle.asp

;=====================================================


Print FileDate$("C:\Program Files\BlitzPlus\BlitzPlus.exe",2)
Print FileTime$("C:\Program Files\BlitzPlus\BlitzPlus.exe",2)
WaitKey()
End



;====================================
;MAIN FUNCTIONS

;This function uses the functions below to create a string containing
;the file date in the following format: MM-DD-YYYY.
;- Mode 0 = original file creation time
;- Mode 1 = last access time
;- Mode 2 = last write time
Function FileDate$(file$,mode=2)
	bank=GetFileTime(file$,mode)	
	month = FileMonth(bank) : myMonth$ = month
	If month < 10 Then myMonth$ = "0"+ month		
	day = FileDay(bank) : myDay$ = day
	If day < 10 Then myDay$ = "0"+ day
	year = FileYear(bank)
	FreeBank bank
	Return myMonth$ + "-" + myDay$ + "-" + year 
End Function

;This function uses the functions below to create a string containing
;the file time in hours, minutes and seconds as follows: HH:MM:SS
;Hours are returned in 0-23. You will need to convert to AM or PM if
;that is needed.
;- Mode 0 = original file creation time
;- Mode 1 = last access time
;- Mode 2 = last write time
Function FileTime$(file$,mode=2)
	bank=GetFileTime(file$,mode)		
	hour = FileHour(bank) : myHour$ = hour
	If hour < 10 Then myHour$ = "0"+ hour
	minute = FileMinute(bank) : myMinute$ = minute
	If minute < 10 Then myMinute$ = "0"+ minute
	second = FileSecond(bank) : mySecond$ = second
	If second < 10 Then mySecond$ = "0"+ second
	FreeBank bank	
	Return myHour$ + ":" + myMinute$ + ":" + mySecond	 
End Function


;====================================
;LOWER LEVEL UTILITY FUNCTIONS

;This function gets the file times for a given file and stores them in a
;bank, which is returned from the function. The time and date info
;can then be read from the bank using the other functions below. Don't
;forget to free the bank after you are done with it, as is done in the
;main 2 functions above. 
;- Mode 0 = original file creation time
;- Mode 1 = last access time
;- Mode 2 = last write time
Function GetFileTime(file$,mode=2)
	If mode < 0 Or mode > 2 Then Return ;error
	If file$ = "" Then Return ; error
	lpReOpenBuff = CreateBank (150)
	lpCreationTime = CreateBank (8)
	lpLastAccessTime = CreateBank (8)
	lpLastWriteTime = CreateBank (8)
	lpLocalFileTime = CreateBank (8)
	lpSystemTime = CreateBank (16)	
	
	hFile = api_OpenFile% (file$, lpReOpenBuff, 0) 
	If hFile <> -1 ;if hFile = -1 then error, can't open file
		result = api_GetFileTime (hFile, lpCreationTime, lpLastAccessTime, lpLastWriteTime)
		If result = 1 ;if result <> 1 then error, can't get file time			
			If mode = 0 Then result = api_FileTimeToLocalFileTime(lpCreationTime,lpLocalFileTime)
			If mode = 1 Then result = api_FileTimeToLocalFileTime(lpLastAccessTime,lpLocalFileTime)
			If mode = 2 Then result = api_FileTimeToLocalFileTime(lpLastWriteTime,lpLocalFileTime)		
			If result = 1 ;if result <> 1 then error, can't convert to local time
				result = api_FileTimeToSystemTime (lpLocalFileTime,lpSystemTime)
				;note: if result <> 1 then function failed
			End If
		End If
		api_CloseHandle(hFile)				
	End If	

	;Clean up and end function	
	FreeBank lpReOpenBuff : FreeBank lpCreationTime : FreeBank lpLastAccessTime
	FreeBank lpLastWriteTime : FreeBank lpLocalFileTime
	Return lpSystemTime
End Function

Function FileYear(bank)
	If bank <= 0 Then Return ;invalid bank address
	Return PeekShort(bank,0)
End Function

Function FileMonth(bank)
	If bank <= 0 Then Return ;invalid bank address
	Return PeekShort(bank,2) 
End Function

Function FileDayOfWeek(bank)
	If bank <= 0 Then Return ;invalid bank address
	Return PeekShort(bank,4);0 = Sunday, 1= Monday, etc. ... 6 = Saturday
End Function

Function FileDay(bank)
	If bank <= 0 Then Return ;invalid bank address
	Return PeekShort(bank,6);0-31
End Function

Function FileHour(bank)
	If bank <= 0 Then Return ;invalid bank address
	Return PeekShort(bank,8) ;0-23 (you will need to convert to AM/PM as needed)
End Function

Function FileMinute(bank)
	If bank <= 0 Then Return ;invalid bank address
	Return PeekShort(bank,10) ;0-59
End Function

Function FileSecond(bank)
	If bank <= 0 Then Return ;invalid bank address
	Return PeekShort(bank,12) ;0-59
End Function

Function FileMillisec(bank)	
	If bank <= 0 Then Return ;invalid bank address
	Return PeekShort(bank,14) ;0-999
End Function
