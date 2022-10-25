; ID: 2177
; Author: Snarkbait
; Date: 2008-01-02 21:41:04
; Title: File Date/Time/Attr functions for b+
; Description: File Date/Time Functions

; filetime.bb include file
; File Date & Time, Attributes functions 
; by snarkbait
; snarkbait66@gmail.com
;
; typical Microsoft - it takes no less than FIVE api function calls to get the proper date!
;
; add the following to kernel32.decls
; .lib "kernel32.dll"
;
;api_GetFileTime%(hFile%,lpCreationTime*,lpLastAccessTime*,lpLastWriteTime*):"GetFileTime"
;api_CreateFile%(FileName$, dwDesiredAccess%, dwShareMode%, lpSecurity%, dwCreationDsp%, dwFlagsAndAttr%, hTemplate%):"CreateFileA"
;api_FileTimeToSystemTime%(lpFileTime*, lpSystemTime*):"FileTimeToSystemTime"
;api_CloseHandle%( hObject% ):"CloseHandle"
;api_FileTimeToLocalFileTime%(lpFileTime*, lpLocalFileTime*):"FileTimeToLocalFileTime" 
;
;; also, to get file attributes, add
; api_GetFileAttributes%( lpFilename$ ):"GetFileAttributesA"
;


Type SYSTEMTIME
	Field wYear%
	Field wMonth%
	Field wDayOfWeek%
	Field wDay%
	Field wHour%
	Field wMinute%
	Field wSecond%
	Field wMilliseconds%
End Type

; API constants

; CreateFile:Desired Access
Const GENERIC_WRITE = $40000000
Const GENERIC_READ = $80000000
; CreateFile:FlagsAndAttributes
Const FILE_ATTRIBUTE_NORMAL = $80
; CreateFile:CreationDisposition
Const OPEN_EXISTING = 3
; CreateFile:ShareMode
Const FILE_SHARE_READ = 1
Const FILE_SHARE_WRITE = 2

; other constants
Const FILETIMESIZE = 8
Const SYSTEMTIMESIZE = 16
Const GET_CREATION = 0
Const GET_LASTACCESS = 1
Const GET_LASTMODIFIED = 2
Const ATTR_STRING$ = "RHSVDALC"

;==========================================================================
; FUNCTION: FileDateTime()
; PARAMS:
;			filename$ : valid filename/path string
;			[returnType] : default GET_LASTMODIFIED, or GET_CREATION, GET_LASTACCESS
;						Specifies if you want the file creation, last modified, or last accessed date/time
;						Last Modified is the generally used 'file date'
; CALLS:
;			FileExists()
;			api_CreateFile() : kernel32.dll function - returns handle for subsequent i/o ops
;			api_GetFileTime() : kernel32.dll function - fills passed banks with 64-bit UTC date/time value
;			api_FileTimeToLocalTime : kernel32.dll function - converts from 64bit UTC to systemtime struct
;			api_FileTimeToSystemTime : kernel32.dll function - converts sytemtime struct to same with localized date/time values
;			api_CloseHandle : kernel32.dll - Closes file handle opened with api_CreateFile()
;			GetFileDateFromBank() : constructor method for type SYSTEMTIME, creates instance and
;									fills it with values from the passed bank
;
; RETURNS:
;	Instance of type SYSTEMTIME 
; OTHER: If you do not specify dwShareMode for CreateFile, and filename is already opened, WILL CAUSE CRASH.
;		Keep sharemode to 'FILE_SHARE_READ Or FILE_SHARE_WRITE'
Function FileDateTime.SYSTEMTIME( filename$, returnType = GET_LASTMODIFIED )
	Local hFile%
	Local success%
	Local retValue%
	Local bankCreationFileTime = CreateBank(FILETIMESIZE)
	Local bankLastAccessFileTime = CreateBank(FILETIMESIZE)
	Local bankLastModifiedFileTime = CreateBank(FILETIMESIZE)
	Local bankLocalFileTime = CreateBank(FILETIMESIZE)
	Local bankSystemTime = CreateBank(SYSTEMTIMESIZE)
	
	; check file
	If Not FileExists( filename$ )
		Return Null
	EndIf
	
	; get api file handle
	hFile = api_CreateFile( filename$, GENERIC_WRITE Or GENERIC_READ, FILE_SHARE_READ Or FILE_SHARE_WRITE, 0, OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, 0)
	If hFile > 0
		; get 64-bit UDT, for all three options
		success = api_GetFileTime(hFile, bankCreationFileTime, bankLastAccessFileTime, bankLastModifiedFileTime)
		If success
			; convert selected return type to local time
			Select returnType
				Case GET_CREATION
					success = api_FileTimeToLocalFileTime( bankCreationFileTime, bankLocalFileTime )
				Case GET_LASTACCESS
					success = api_FileTimeToLocalFileTime( bankLastAccessFileTime, bankLocalFileTime )
				Case GET_LASTMODIFIED
					success = api_FileTimeToLocalFileTime( bankLastModifiedFileTime, bankLocalFileTime )
				Default
					success = False
			End Select
			If success
				; convert to structured time info ( & free unused banks)
				FreeBank bankCreationFileTime
				FreeBank bankLastModifiedFileTime
				FreeBank bankLastAccessFileTime
				success = api_FileTimeToSystemTime(bankLocalFileTime, bankSystemTime)
				If success
					; create type object and fill with bank values
					FreeBank bankLocalFileTime
					this.SYSTEMTIME = GetFileDateFromBank( bankSystemTime )
					FreeBank bankSystemTime
					; didn't create type properly
					If this.SYSTEMTIME = Null
						retValue = False
					Else
						; everything OK
						retValue = True
					EndIf
				Else
					; error, but needs close file
					retValue = False
				EndIf
			Else
				; error, but needs close file
				retValue = False
			EndIf
		Else
			; error, but needs close file
			retValue = False
		EndIf
	Else
		; error
		Return Null
	EndIf
	
	If retValue
		; everything OK, close file and return Object
		api_CloseHandle( hFile)
		Return this
	Else
		; SNAFU, close file and return null
		api_CloseHandle( hFile)
		Return Null
	EndIf
End Function

;==========================================================================
; FUNCTION: GetFileDateFromBank()
; PARAMS:
;			inBank : 16-byte bank in the SYSTEMTIME format
; CALLS:
; RETURNS:
;	Instance of type SYSTEMTIME, filled with values from bank
; OTHER:
;

Function GetFileDateFromBank.SYSTEMTIME( inBank )

	; check bank is the right size
	If BankSize( inBank ) < SYSTEMTIMESIZE Return Null
	
	; create instance
	Local this.SYSTEMTIME = New SYSTEMTIME
	
	; peek 2-byte values from bank
	this\wYear = PeekShort( inBank, 0 )
	this\wMonth = PeekShort( inBank, 2 )
	this\wDayOfWeek = PeekShort( inBank, 4 )
	this\wDay = PeekShort( inBank, 6 ) 
	this\wHour = PeekShort( inBank, 8 ) 
	this\wMinute = PeekShort( inBank, 10 )
	this\wSecond = PeekShort( inBank, 12 )
	this\wMilliseconds = PeekShort( inBank, 14 )
	
	; return Object
	Return this
End Function

;==========================================================================
; FUNCTION: GetFileDateStringFromType()
; PARAMS:
;			this.SYSTEMTIME : object of type SYSTEMTIME
; CALLS:
; RETURNS:
;	String in American date format, i.e. "MM/DD/YYYY HH:MM:SS {AM/PM}"
; OTHER:		
;

Function GetFileDateStringFromType$( this.SYSTEMTIME )
	Local retStr$
	Local AMPM$
	Local hrs
	Local strMin$
	Local strSec$
	
	; meridiem
	If this\wHour > 11
		AMPM$ = "PM"
	Else
		AMPM$ = "AM"
	EndIf
	
	; convert from military time
	hrs = this\wHour Mod 12
	If hrs = 0 hrs = 12
	
	; fix minute/second strings to 2 places
	If this\wMinute < 10
		strMin = "0" + this\wMinute
	Else
		strMin = Str(this\wMinute)
	EndIf
	If this\wSecond < 10
		strSec = "0" + this\wSecond
	Else
		strSec = Str(this\wSecond)
	EndIf
	
	;concatenate
	retStr = Str(this\wMonth) + "/" + Str(this\wDay) + "/" + Str(this\wYear) + " " + Str(hrs) + ":" + strMin + ":" + strSec + " " + AMPM$
	
	Return retStr
	
End Function

;==========================================================================
; FUNCTION: GetFileAttributesStr()
; PARAMS:
;			filename$ : valid filename/path string
; CALLS:
;			FileExists()
;			api_GetFileAttributes() : kernel32.dll function - returns Long value with file attribute bit flags
; RETURNS:
;	String with accumulated file attributes, i.e. "A" for Archive, "D" for folder, "HSA" for Hidden, System and Archive
;
; OTHER:	N = normal, R = read only, H = hidden S = system V = volume
;           	D = directory, A = archive, L = alias, C = compressed
;	If you want just the integer value, just use the api function api_GetFileAttributes( filename$ ) by itself

Function GetFileAttributesStr$( filename$ )
	If Not FileExists( filename$ )
		Return ""
	EndIf
	
	Local fAttr
	Local retStr$
	Local a
	
	fAttr = api_GetFileAttributes( filename$ )
	
	; mask to 8-bits
	fAttr = fAttr And $FF
	
	; attribute normal
	If fAttr = 0 Then
      	  	retStr$ = "N"
    	Else
      ; loop thru byte to see which bits are set
        	For a = 1 To 8
            		If fAttr And 1 Shl (a - 1) Then
				; bit is set, add proper code from string
                		retStr = retStr + Mid$(ATTR_STRING, a, 1)
            		End If
        	Next
    	End If

	Return retStr
End Function

;==========================================================================
; FUNCTION: FileExists()
; PARAMS:
;			filename$ : valid filename/path string
; CALLS:
; RETURNS:
;	True if file exists, False if not
; OTHER:		
;

Function FileExists( filename$ )
	Local retValue
	If FileType( filename$ ) = 1
		retValue = True
	Else
		retValue = False
	EndIf
	Return retValue
End Function
