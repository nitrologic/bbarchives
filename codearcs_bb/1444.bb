; ID: 1444
; Author: turtle1776
; Date: 2005-08-16 08:55:42
; Title: FTP Using WinInet Userlib Functions
; Description: A program that uses the Windows Internet (WinInet) userlib to access FTP sites and transfer files.

;=====================
;WinInet FTP Functions
;=====================
;By Patrick Lester (turtle1776)

;This program uses the Windows Internet (WinInet) userlib to access FTP sites and transfer files. 
;WinInet functions seem to be faster and more reliable than the TCP/IP alternatives using the 
;native Blitz OpenTCPStream() command.

;The list of functions can be found below in both the decls list and below that, where it says
;"Working WinInet Functions."  These functions are used in the following, very simple FTP
;program. Obviously, a nicer Windows program, complete with bells and whistles, could be
;created with the basic elements included here.

;Reference
;-----------
;http://msdn.microsoft.com/library/default.asp?url=/library/en-us/wininet/wininet/ftp_sessions.asp
;http://msdn.microsoft.com/library/default.asp?url=/library/en-us/wininet/wininet/wininet_reference.asp
;http://msdn.microsoft.com/library/default.asp?url=/library/en-us/debug/base/system_error_codes.asp
;http://msdn.microsoft.com/library/default.asp?url=/library/en-us/wininet/wininet/wininet_errors.asp

;Add to decls file
;------------------
;.lib "wininet.dll"
;FtpCreateDirectory%(hFTPSession%,lpszDirectory$):"FtpCreateDirectoryA"
;FtpDeleteFile%(hFTPSession%,lpszFileName$) : "FtpDeleteFileA"
;FtpFindFirstFile%(hFTPSession%,lpszFileName$,lpFindFileData*,dwFlags%,dwContext%) : "FtpFindFirstFileA"
;FtpGetCurrentDirectory%(hFTPSession%,lpszCurrentDirectory*,neededLength*):"FtpGetCurrentDirectoryA"
;FtpGetFile%(hFTPSession%,RemoteFile$,LocalFile$,fFailIfExists%,dwFlagsAndAttributes%,dwFlags%,dwContext%):"FtpGetFileA"
;FtpPutFile%(hFTPSession%,LocalFile$,NewRemoteFile$,dwFlags%,dwContext%):"FtpPutFileA"
;FtpRemoveDirectory%(hFTPSession%,lpszDirectory$) : "FtpRemoveDirectoryA"
;FtpRenameFile%(hFTPSession%,lpszExisting$,lpszNew$) : "FtpRenameFileA"
;FtpSetCurrentDirectory%(hFTPSession%,lpszDirectory$) : "FtpSetCurrentDirectoryA"
;InternetCloseHandle%(hInternet%):"InternetCloseHandle"
;InternetConnect%(hInternet%, ServerName$, ServerPort%, Username$, Password$, Service%, Flags%,  dwContext%):"InternetConnectA"
;InternetFindNextFile%(hInternet%,lpvFindData*) : "InternetFindNextFileA"
;InternetOpen%(Agent$, AccessType%, ProxyName%, ProxyBypass%, Flags%):"InternetOpenA"
;InternetGetLastResponseInfo%(lpdwError*,lpszBuffer*,lpdwBufferLength*) : "InternetGetLastResponseInfoA"

;.lib "kernel32.dll"
;GetLastError%() : "GetLastError"

;========================================
;Constants, globals, and types
Const succeeded = 1, failed = -1
Const FTP_TRANSFER_TYPE_ASCII = 1 ;Used by FtpPutFile and FtpGetFile functions.
Const FTP_TRANSFER_TYPE_BINARY = 2 ;Used by FtpPutFile and FtpGetFile functions.
Const ERROR_NO_MORE_FILES = 18 ;Used by FtpFindFirstFile
Const FILE_ATTRIBUTE_NORMAL = $80; = 128 -- Used by FtpGetFile function.
Const INTERNET_FLAG_RELOAD = $80000000 ;Used by FtpFindFirstFile
Global ghFTPSession, ghInternet, gCurrentDirectory$
Type FTPFile 
	Field directory$
	Field fileName$
	Field typeOfFile	
	Field sizeofFile ;in bytes
End Type


;Full Program: Open, run and close FTP Session
;----------------------------------------------------
;RunFTPSession("ftp.hq.nasa.gov","anonymous","anon@yahoo.com")
RunFTPSession("ftp.gnu.org","anonymous","anon@yahoo.com")


;========================================
;WORKING WININET FUNCTIONS: These functions are actually used elsewhere in the program.
;They are listed here so you can see what they look like all in one place.

;Functions that open and close FTP session (used by OpenFTPSession and CloseFTPSession)
hInternet = InternetOpen("...", 0, 0, 0, 0)
ghFTPSession = InternetConnect(hInternet ,"policyalmanac.org",21,login$,password$,1,$08000000,0)
result = InternetCloseHandle(ghFTPSession) 
result = InternetCloseHandle(hInternet) 

;File functions
result = FtpDeleteFile(ghFTPSession,file$)
result = FtpGetFile (ghFTPSession,remoteFile$,localFile$,0,FILE_ATTRIBUTE_NORMAL,FTP_TRANSFER_TYPE_BINARY,0)
result = FTPGetFileList(directory$)
result = FtpPutFile (ghFTPSession,localFile$,remoteFile$,FTP_TRANSFER_TYPE_BINARY,0)
result = FtpRenameFile(ghFTPSession,existingFile$,newFileName$) ;also renames directories

;Directory functions
result = FtpCreateDirectory%(ghFTPSession%,directory$)
directory$ = FTPGetCurrDirectory$()
result = FtpRemoveDirectory(ghFTPSession,directory$)
result = FtpSetCurrentDirectory(ghFTPSession,directory$)

error = GetLastError()


;========================================
;BLITZ FUNCTIONS

;This function runs the FTP session until the user exits by typing 'x'
Function RunFTPSession(host$,user$,password$)

	Print "Connecting to FTP server at "+ host$ + " ... (please wait)"
	If OpenFTPSession(host$,user$,password$) = failed
		error = GetLastError()
		Print "Error opening FTP session. Error = " + GetError(error)
		Print "Finished. Sorry."
		WaitKey()
		End			
	End If
	
	gCurrentDirectory$ = FTPGetCurrDirectory$()
	If FTPGetFileList() = succeeded Then PrintRemoteFileList()

	Repeat
		myinput$ = Input$("Next Action (type h for help, x to exit program): ")
		Print " "	
		
		;Process user input
		If myInput$ = "x" Or myInput$ = "X" Then Exit
		ProcessUserInput(myInput$)	
		
		;Get and print current directory and current file list.
		gCurrentDirectory$ = FTPGetCurrDirectory$() 
		If FTPGetFileList() = succeeded
			PrintRemoteFileList()
		Else
			error = GetLastError()
			Print "Error listing files. Error = " + GetError(error)			
		End If
		
		If myinput$ = "h" Then PrintHelp()	
	Forever
	
	Print "Closing FTP session ... (please wait)"
	CloseFTPSession()
End Function

;This function conducts various actions based on what the user types in.
;This function illustrates how to access most of the WinInet functions.
Function ProcessUserInput(myinput$)
	If myinput$ = ".." ;go up one level	
		If gCurrentDirectory$ = "/" Then Return		
		Repeat
			SecondToLastSlash = lastSlash 
			lastSlash = x
			x = Instr (gCurrentDirectory$, "/",lastSlash+1)
		Until x = 0
		newDirectory$ = Left (gCurrentDirectory$, SecondToLastSlash) 
		If newDirectory$ = "" Then newDirectory$ = "/"
		result = FtpSetCurrentDirectory(ghFTPSession,newDirectory$)	
		If result = 1 Return succeeded
		If result <> 1 
			error = GetLastError()
			Print "Failed. Error = " + GetError(error)
		End If		
	
	;Get (download) a file	
	Else If myinput$ = "g"	
		remoteFile$ = Input$("Type a file in current directory listed above or x to cancel: ")
		If remoteFile$ = "" Or remoteFile$ = "x" Then Return
		transfer$ = Input$("Transfer in binary mode? If not sure, answer yes. If you answer no, it will be an ASCII transfer. (y/n): ")		
		If Left (transfer$, 1) = "n" Or Left (transfer$, 1) = "N" 
			transferType = FTP_TRANSFER_TYPE_ASCII 
			Print "ASCII transfer"		
		Else
			transferType = FTP_TRANSFER_TYPE_BINARY
			Print "Binary transfer."
		End If	
		downloadToC$ = Input$("Download to C:\ drive? (y/n): ")
		If downloadToC$ = "" Or downloadToC$ = "y" Or downloadToC$ = "yes"
			localFile$ = "C:\"+remoteFile$
			remoteFile$ = gCurrentDirectory$+remoteFile$ 
			result = FtpGetFile (ghFTPSession,remoteFile$,localFile$,0,FILE_ATTRIBUTE_NORMAL,transferType,0)
			If result = 1 Then Print "Succeeded"
			If result <> 1 
				error = GetLastError()
				Print "Failed. Error = " + GetError(error)
			End If	
		Else
			localDirectory$ = Input$("Please type the full path to the local directory: ")
			If Right$(localDirectory$,1) <> "\" And Right$(localDirectory$,1) <> "/"
				localDirectory$ = localDirectory$+"\"
			End If	
			localFile$ = localDirectory$+remoteFile$
			remoteFile$ = gCurrentDirectory$+remoteFile$ 
			result = FtpGetFile (ghFTPSession,remoteFile$,localFile$,0,FILE_ATTRIBUTE_NORMAL,transferType,0)			
			If result = 1 Then Print "Succeeded"
			If result <> 1 
				error = GetLastError()
				Print "Failed. Error = " + GetError(error)
			End If	
		End If	
		
	;Put (upload) a file	
	Else If myInput$ = "p"	
		localFile$ = Input$("Type a local file starting with C:\ to upload it to this directory (or press x to cancel): ")
		If localFile$ = "" Or localFile$ = "x" Then Return
		remoteFile$ = gCurrentDirectory$+GetFileName$(localFile$)
		transfer$ = Input$("Transfer in binary mode? If not sure, answer yes. If you answer no, it will be an ASCII transfer. (y/n): ")		
		If Left (transfer$, 1) = "n" Or Left (transfer$, 1) = "N" 
			transferType = FTP_TRANSFER_TYPE_ASCII 
			Print "ASCII transfer"		
		Else
			transferType = FTP_TRANSFER_TYPE_BINARY
			Print "Binary transfer."
		End If	
		result = FtpPutFile (ghFTPSession,localFile$,remoteFile$,transferType,0)
		If result = 1 Then Print "Succeeded"
		If result <> 1 
			error = GetLastError()
			Print "Failed. Error = " + GetError(error)
		End If	
		
	;Delete a file
	Else If myInput$ = "d"		
		remoteFile$ = Input$("Type a file in current directory listed above or x to cancel: ")
		If remoteFile$ = "" Or remoteFile$ = "x" Then Return
		remoteFile$ = gCurrentDirectory$+remoteFile$
		result = FtpDeleteFile(ghFTPSession,remoteFile$)
		If result = 1 Then Print "Succeeded"
		If result <> 1 
			error = GetLastError()
			Print "Failed. Error = " + GetError(error)
		End If	

	;Rename a file or directory
	Else If myInput$ = "r"	
		existingFile$ = Input$("Type a file in current directory listed above or x to cancel: ")
		If existingFile$ = "" Or existingFile$ = "x" Then Return
		existingFile$ = gCurrentDirectory$+existingFile$		
		newFileName$ = Input$("Type a new name or x to cancel: ")
		If newFileName$ = "" Or newFileName$ = "x" Then Return
		newFileName$ = gCurrentDirectory$+newFileName$			
		result = FtpRenameFile(ghFTPSession,existingFile$,newFileName$)
		If result = 1 Then Print "Succeeded"
		If result <> 1 
			error = GetLastError()
			Print "Failed. Error = " + GetError(error)
		End If		

	;Create a directory
	Else If myInput$ = "cd"	
		newDirectory$ = Input$("Type a subdirectory name or x to cancel: ")
		If newDirectory$ = "" Or newDirectory$ = "x" Then Return
		newDirectory$ = gCurrentDirectory$+newDirectory+"/"
		result = FtpCreateDirectory%(ghFTPSession%,newDirectory$)
		If result = 1 Then Print "Succeeded"
		If result <> 1 
			error = GetLastError()
			Print "Failed. Error = " + GetError(error)
		End If		
		
	;Delete a directory
	Else If myInput$ = "dd"	
		directory$ = Input$("Type a subdirectory to delete or x to cancel: ")
		If directory$ = "" Or directory$ = "x" Then Return
		directory$ = gCurrentDirectory$+directory+"/"
		result = FtpRemoveDirectory(ghFTPSession,directory$)
		If result = 1 Then Print "Succeeded"
		If result <> 1 
			error = GetLastError()
			Print "Failed. Error = " + GetError(error)
		End If	

	;Stop in debug mode
	Else If myInput$ = "s"
		Stop
		
	;Printing help.
	Else If myInput$ = "h"
		;do nothing, handled in RunFTPSession() function
				
	;Check to see if the user typed in a subdirectory name. If so, open it.						
	Else 
		For ftpfile.ftpfile = Each ftpfile
			If ftpfile\fileName$ = myinput$
				If ftpfile\typeOfFile = 2
					fullFile$ = gCurrentDirectory$ + myinput$
					result = FtpSetCurrentDirectory(ghFTPSession,fullFile$)
				End If
				Return succeeded
			End If
		Next	
		Print "No such subdirectory. Press h for help."
		Return failed 
	End If	
End Function

;This function opens an FTP session
Function OpenFTPSession(domain$,login$,password$)
	ghInternet = InternetOpen("...", 0, 0, 0, 0)
	If Not ghInternet Then Return failed
	ghFTPSession = InternetConnect(ghInternet,domain$,21,login$,password$,1,$08000000,0)	
	If Not ghFTPSession Then Return failed
	Return succeeded
End Function 

;This function closes the current FTP session
Function CloseFTPSession()
	InternetCloseHandle(ghFTPSession) 
	InternetCloseHandle(ghInternet) 
End Function 

;This function returns the current directory with a trailing slash /
Function FTPGetCurrDirectory$()	
	lpNeededLength = CreateBank(4)
	PokeInt lpNeededLength,0,200	
	lpCurrentDirectory = CreateBank(256) ;needs to be big enough to handle the string
	If FtpGetCurrentDirectory%(ghFTPSession,lpCurrentDirectory,lpNeededLength) = succeeded	
		currentDirectory$=ReadAPIString$(lpCurrentDirectory)
		If Right$(currentDirectory$,1) <> "/" Then currentDirectory$ = currentDirectory$+"/"
	End If	
	FreeBank lpNeededLength : FreeBank lpCurrentDirectory
	Return currentDirectory$
End Function

;This function iterates through the file in a given FTP directory
;and stores the file information in a type called ftpfile, which 
;contains the file's directory, filename, type (1 = directory, 2 = file),
;and size in bytes. If no directory is specified, the function will use
;the current directory.
Function FTPGetFileList(remoteDirectory$="")
	Delete Each ftpfile	
	If remoteDirectory$ = "" Then remoteDirectory$ = gCurrentDirectory$
	lpFindFileData = CreateBank (320) 
	hInternet = FtpFindFirstFile(ghFTPSession,remoteDirectory$,lpFindFileData,INTERNET_FLAG_RELOAD,0)	
	If hInternet = 0 
		FreeBank lpFindFileData
		If GetLastError() = ERROR_NO_MORE_FILES Then Return succeeded ;no files or subdirectories
		Return failed	
	EndIf	

	;Iterate through the files in the directory and store each in a type.
	Repeat
		ftpfile.ftpfile = New ftpfile	
		ftpfile\directory$ = remoteDirectory$
		ftpfile\fileName$ = ReadAPIString$(lpFindFileData,44)
		If PeekInt(lpFindFileData,0) = 16 Then ftpfile\typeOfFile = 2 ;directory (FILE_ATTRIBUTE_DIRECTORY)
		If PeekInt(lpFindFileData,0) = 128 Then ftpfile\typeOfFile = 1 ;file (FILE_ATTRIBUTE_NORMAL)
		ftpfile\sizeofFile = PeekInt(lpFindFileData,32)	 ;nFileSizeLow is enough, accurate for files < 2.1 gigs (that's huge)	
	Until InternetFindNextFile(hInternet,lpFindFileData) = 0

	result = InternetCloseHandle(hInternet) 
	FreeBank lpFindFileData	
	Return succeeded
End Function
;Technical Note: File information returned in the lpFindFileData bank is in the 
;form of a WIN32_FIND_DATA structure. Data in this structure is in the following
;positions in the bank. See
;http://msdn.microsoft.com/library/default.asp?url=/library/en-us/fileio/fs/win32_find_data_str.asp
	;0-3 = dwFileAttributes
	;4-11 = ftCreationTime;
	;8-19 = ftLastAccessTime;
	;20-27 = ftLastWriteTime;;
	;28-31 = nFileSizeHigh
	;32-35 = nFileSizeLow
	;36-39 = dwReserved0
	;40-43 = dwReserved1
	;44+ = null terminated file name

;This function extracts the file name from an url or local file.
Function GetFileName$(file$)
	Repeat
		x = Instr (file$, "\")
		file$ = Right (file$, Len (file$) - x) 
	Until x = 0
	Repeat
		x = Instr (file$, "/")
		file$ = Right (file$, Len (file$) - x) 
	Until x = 0	
	Return file$
End Function

;This function retrieves and translates common error codes. A complete
;list can be found here: 
;http://msdn.microsoft.com/library/default.asp?url=/library/en-us/debug/base/system_error_codes.asp
;http://msdn.microsoft.com/library/default.asp?url=/library/en-us/wininet/wininet/wininet_errors.asp
Function GetError$(error)
	If error = 2 Then Return "ERROR_FILE_NOT_FOUND"
	If error = 3 Then Return "ERROR_PATH_NOT_FOUND"
	If error = 5 Then Return "ERROR_ACCESS_DENIED"
	If error = 18 Then Return "ERROR_NO_MORE_FILES"
	If error = 19 Then Return "ERROR_WRITE_PROTECT"
	If error = 87 Then Return "ERROR_INVALID_PARAMETER"
	If error = 12003 ; "ERROR_INTERNET_EXTENDED_ERROR"
		lpdwError = CreateBank (4)
		lpszBuffer = CreateBank (1000)
		lpdwBufferLength = CreateBank (4)
		PokeInt lpdwBufferLength,0,1001
		result = InternetGetLastResponseInfo(lpdwError,lpszBuffer,lpdwBufferLength) 
		If result = 1 Then errorString$="Extended error. " + ReadAPIString$(lpszBuffer) +" ("+PeekInt(lpdwError,0) + ")"
		If result = 0 Then errorString$="Extended error not returned. Error " + GetLastError()
		FreeBank lpdwError : FreeBank lpszBuffer : FreeBank lpdwBufferLength
		Return errorString$	
	End If
	If error = 12007 Then Return "ERROR_INTERNET_NAME_NOT_RESOLVED "
	If error = 12013 Then Return "ERROR_INTERNET_INCORRECT_USER_NAME"
	If error = 12014 Then Return "ERROR_INTERNET_INCORRECT_PASSWORD"
	If error = 12015 Then Return "ERROR_INTERNET_LOGIN_FAILURE"	
	If error = 12030 Then Return "Internet connection has been terminated." ;ERROR_INTERNET_CONNECTION_ABORTED
	If error = 12031 Then Return "ERROR_INTERNET_CONNECTION_RESET"
	If error = 12110 Then Return "ERROR_FTP_TRANSFER_IN_PROGRESS"	
	If error = 12111 Then Return "ERROR_FTP_DROPPED"
	If error = 12112 Then Return "ERROR_FTP_NO_PASSIVE_MODE"	
	Return "Undefined error " + error ;if none of the above is true, return the number
End Function

;This function reads a null-terminated string returned from API function
;to a given bank.  The offset parameter is used if the string is part of a
;larger data structure, as is the case when this function is called from 
;FTPGetFileList().  In that particular case, the string is stored in a larger
;data structure called WIN32_FIND_DATA.
Function ReadAPIString$(bank,offset=0)
	size = BankSize(bank)
	For x = offset To (size-1)
		If PeekByte(bank,x) = 0 Then Exit ;null terminator found
		myString$ = myString$ + Chr$(PeekByte(bank,x))
	Next	
	Return myString$
End Function

;This function prints the file list obtained through FTPGetFileList.
Function PrintRemoteFileList()
	Print ""
	Print "================================"
	Print "CURRENT DIRECTORY = " + gCurrentDirectory$
	Print ""
	Print "Subdirectories"; 
	Print "-----------------"
	For ftpfile.ftpfile = Each ftpfile
		If ftpfile\typeOfFile = 2
			count = count + 1
			Print ftpfile\fileName$ + " (" + ftpfile\sizeofFile + " bytes)"
		End If	
	Next
	If count = 0 Then Print "None"	
	Print ""
	Print "Files in directory"
	Print "------------------"
	count = 0
	For ftpfile.ftpfile = Each ftpfile
		If ftpfile\typeOfFile = 1
			count = count + 1
			Print ftpfile\fileName$ + " (" + ftpfile\sizeofFile + " bytes)"
		End If
	Next
	If count = 0 Then Print "None"
	Print ""
End Function

;This function prints out the commands.
Function PrintHelp()
	Print ""
	Print "Commands"
	Print "-------------"
	Print "x = Exit program"
	Print "h = Help"
	Print "subdirectory name = Open subdirectory in current directory"
	Print ".. = Go up one directory level"
	Print "g = Get (download) a file."
	Print "p = Put (upload) a file"
	Print "d = Delete a file"
	Print "r = Rename a file or directory."
	Print "cd = Create a directory."
	Print "dd = Delete a directory (must be empty)."		
	Print ""
End Function
