; ID: 2995
; Author: Zethrax
; Date: 2012-10-31 02:47:31
; Title: GetUnixTimestamp - Function to calculate a Unix timestamp
; Description: A Unix timestamp is the number of seconds since the start of 1970, in UTC time.

Function GetUnixTimestamp()
	; Returns a Unix timestamp.

	; A Unix timestamp is the number of seconds since the start of the year 1970.
	; Unix timestamps operate on UTC time (Coordinated Universal Time)
	; which is the same as GMT time (Greenwich Mean Time).
	; Due to the use of UTC, local daylight saving and timezones are not a factor.
	
	; Note that Unix time seems to ignore leap seconds.

	; Requires a 'kernel32.decls' file in the Blitz3D 'userlibs' folder with the contents:-
	; .lib "kernel32.dll"
	; api_GetSystemTime (lpSystemTime*) : "GetSystemTime"

	; The 'kernel32.decls' file can be obtained from:-
	; http://www.blitzbasic.com/codearcs/codearcs.php?code=1180

	; Reference Links:-
	; http://en.wikipedia.org/wiki/Unix_time
	; http://en.wikipedia.org/wiki/Leap_year
	; http://en.wikipedia.org/wiki/Leap_second
	; http://en.wikipedia.org/wiki/Coordinated_universal_time
	; http://msdn.microsoft.com/en-us/library/windows/desktop/ms724390(v=vs.85).aspx
	; http://msdn.microsoft.com/en-us/library/windows/desktop/ms724950(v=vs.85).aspx

	; To test the results use: http://www.unixtimestamp.com/

	; Get the UTC/GMT time and date.
	Local timebank = CreateBank( 16 )
	api_GetSystemTime( timebank )
	Local year = PeekShort( timebank, 0 ) ; The year. Exact value.
	Local month = PeekShort( timebank, 2 ) ; The month. Struct values: January = 1 - December = 12
	Local day = PeekShort( timebank, 6 ) - 1 ; The day of the month. The valid values for this struct member are 1 through 31.
	Local hour = PeekShort( timebank, 8 ) ; The hour. The valid values for this struct member are 0 through 23.
	Local minute = PeekShort( timebank, 10 ) ; The minute. The valid values for this struct member are 0 through 59.
	Local second = PeekShort( timebank, 12 ) ; The second. The valid values for this struct member are 0 through 59.
	FreeBank timebank

	; Do we include the current year in the leap year test?
	Local end_year
	If month > 2
		end_year = year
	Else 
		end_year = year - 1
	EndIf
	
	; Calculate if a specific year is a leap year
	Local y
	For y = 1970 To end_year
		If ( y Mod 100 ) = 0
			If ( y Mod 400 ) = 0
				day = day + 1 ; leap year = True
			EndIf
		Else
			If ( y Mod 4 ) = 0
				day = day + 1 ; leap year = True
			EndIf 
		EndIf 
	Next
	
	day = day + ( year - 1970 ) * 365

	If month > 1 Then day = day + 31
	If month > 2 Then day = day + 28
	If month > 3 Then day = day + 31
	If month > 4 Then day = day + 30
	If month > 5 Then day = day + 31
	If month > 6 Then day = day + 30
	If month > 7 Then day = day + 31
	If month > 8 Then day = day + 31
	If month > 9 Then day = day + 30
	If month > 10 Then day = day + 31
	If month > 11 Then day = day + 30
	If month > 12 Then day = day + 31

	Return day * 86400 + hour * 3600 + minute * 60 + second
End Function


;*** DEMO ***


Print "Unix Timestamp: " + GetUnixTimestamp()

; If you want to do a precise test and have XAMMP (or something similar) installed
; then create a file in the XAMMP htdocs folder named 'time.php' with the contents
; <?php echo time() ?>
; and uncomment the line below.
;ExecFile "http://localhost/time.php"

WaitKey
End
