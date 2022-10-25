; ID: 2084
; Author: Yahfree
; Date: 2007-07-31 19:22:05
; Title: Current GMT Date/Time Grabbers
; Description: 2 functions to grab the Current date/Time in GMT

;--------------------------------------------
;      CurrentGMTTime()
;--------------------------------------------

Function CurrentGMTTime$()
date$ = CurrentDate$()
time$ = CurrentTime$()


	;split date
	day = Mid$(date$, 1, 2)
	mon = (Instr("JanFebMarAprMayJunJulAugSepOctNovDec", Mid$(date$, 4, 3)) + 2) / 3	

	bank = CreateBank(255)
	;retreive timezone info	
	api_GetTimeZoneInformation(bank)
	;offset in minutes for the timezone
	bias = -PeekInt(bank, 0)
	;date when 'normal' time starts	
	month1 = PeekShort(bank, 70);month
	day1 = PeekShort(bank, 74);day
	hour1 = PeekShort(bank, 76);hour
	;date when 'normal' time starts	
	month2 = PeekShort(bank, 154);month
	day2 = PeekShort(bank, 158);day
	hour2 = PeekShort(bank, 160);hour
	;amount of minutes offset in summer	
	daylightbias = PeekInt(bank, 168)
	FreeBank bank
	
	;test for daylightsaving
	test = 0
	If (mon >= month2) And (mon <= month1) Then
		If (mon <> month1) And (mon <> month2) Then test = 1
		If (day >= day2) And (mon = month2) Then test = 1
		If (day <= day1) And (mon = month1) Then test = 1
	End If
	If test Then bias = bias - daylightbias
	
	hour = Int(Mid$(time$, 1, 2)) - (bias / 60.0)
	
	min = Int(Mid$(time$, 4, 2)) - (bias Mod 60)
	If min < 1 Then min = min + 60 hour=hour-1
	If min > 59 Then min = min - 60 hour=hour+1
	
	If hour < 0 Then hour = hour + 24
	If hour > 23 Then hour = hour - 24
	
	sec$ = Right$(time$, 2)
	
	If hour>=10 shour$ = hour
	If hour<10 shour$ = "0"+hour
	If min>=10 smin$ = min
	If min<10 smin$ = "0"+min
	
	time$ = shour + ":" + smin + ":" + sec
	
	Return time$
		
End Function


;--------------------------------------------
;      CurrentGMTDate$()
;--------------------------------------------
Function CurrentGMTDate$()
date$ = CurrentDate$()
time$ = CurrentTime$()

f_mon$=""
	day = Mid$(date$, 1, 2)
	mon = (Instr("JanFebMarAprMayJunJulAugSepOctNovDec", Mid$(date$, 4, 3)) + 2) / 3
	year = Mid(date$,8,4)
    maxdays = Int(Mid$("312831303130313130313031", mon * 2 - 1, 2)) + ((mon = 2) * (year Mod 4 = 0))
	hour = Int(Mid$(time$, 1, 2))
	min = Int(Mid$(time$, 4, 2)) 
	
	bank = CreateBank(255)
	;retreive timezone info	
	api_GetTimeZoneInformation(bank)
	;offset in minutes for the timezone
	bias = -PeekInt(bank, 0)
	;date when 'normal' time starts	
	month1 = PeekShort(bank, 70);month
	day1 = PeekShort(bank, 74);day
	hour1 = PeekShort(bank, 76);hour
	;date when 'normal' time starts	
	month2 = PeekShort(bank, 154);month
	day2 = PeekShort(bank, 158);day
	hour2 = PeekShort(bank, 160);hour
	;amount of minutes offset in summer	
	daylightbias = PeekInt(bank, 168)
	FreeBank bank
	
	test = 0
	If (mon >= month2) And (mon <= month1) Then
		If (mon <> month1) And (mon <> month2) Then test = 1
		If (day >= day2) And (mon = month2) Then test = 1
		If (day <= day1) And (mon = month1) Then test = 1
	End If
	If test Then bias = bias - daylightbias
	
	;If the hour is greater then 23 (24+) then day would be 1 more in GMT
	If hour+bias/60 > 23 day=day+1
	
	;If the hour is lesser then 0 (-1 and below) then the day would be 1 less in GMT
	If hour+bias/60 < 0 day=day-1
	
	;If the day exceeds the max amount of days in the month, then the month would be 1 more in GMT
	If day > maxdays mon=mon+1
	
	;if the day is less then 1 the month would be 1 less in GMT, reset maxdays so it reads the new month..
	; And set the day to the max days in the month (1 less from the next month)
	If day < 1
	mon=mon-1 
	maxdays = Int(Mid$("312831303130313130313031", mon * 2 - 1, 2)) + ((mon = 2) * (year Mod 4 = 0))
	day=maxdays
	End If
	
	;If the month exceeds 12 (months in a year) set months to 1
	If mon > 12 year=year+1 mon = 1
	
	;If the month is less then 1, set months to 12, drop year by 1
	If mon < 1 year=year-1 mon = 12
	
	
	Select mon
	Case 12: f_mon="Dec"
	Case 11: f_mon="Nov"
	Case 10: f_mon="Oct"
	Case 9: f_mon="Sep"
	Case 8: f_mon="Aug"
	Case 7: f_mon="Jul"
	Case 6: f_mon="Jun"
	Case 5: f_mon="May"
	Case 4: f_mon="Apr"
	Case 3: f_mon="Mar"
	Case 2: f_mon="Feb"
	Case 1: f_mon="Jan"
	End Select
	
	If day>=10 sday$ = day
	If day<10 sday$ = "0"+day
	
Return sday + " " + f_mon + " " + year

End Function
