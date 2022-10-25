; ID: 1726
; Author: Smurftra
; Date: 2006-06-02 12:59:09
; Title: TDateTime
; Description: Date Time Type

'	--	--	--	--	--	--	--	--	--	--	--	--	--	--	--  --  --  --	--  --  --  --	--  --  --  --  --
'###############################################################################################################
'	This Code (C) Francois Albert
'	
'	Edit: 2006/06/01
'
'	Quick: Date and Time Type
'	
'###############################################################################################################
'	--	--	--	--	--	--	--	--	--	--	--	--	--	--	--  --  --  --	--  --  --  --	--  --  --  --  --
'###############################################################################################################
'	THIS CODE HOLDS
'	
'	Objects:
'		TDateTime
'
'	Test Code: Yes
'###############################################################################################################
'	--	--	--	--	--	--	--	--	--	--	--	--	--	--	--  --  --  --	--  --  --  --	--  --  --  --  --
'###############################################################################################################
'	NOTEPAD
'
'	Add a format function

'###############################################################################################################
'	--	--	--	--	--	--	--	--	--	--	--	--	--	--	--  --  --  --	--  --  --  --	--  --  --  --  --
'###############################################################################################################

Strict
	
'###############################################################################################################
'Test Code
'	Uncomment code to run tests of datetime routines
'###############################################################################################################
rem	
	Local dtTest:TDateTime
	Local dtTest2:TDateTime
	
	TDateTime.Language = TDateTime.lngEnglish 
	
	dtTest = New TDateTime
	dtTest2 = New TDateTime
	
	dtTest.Set_DateTimeCurrent
	dtTest2.Set_DateTimeSerial(2012,7,19,15,43,12)
	
	Print  TDateTime.Get_String(dtTest,"Now : DD/MM/YYYY HH:NN:SS")
	Print  TDateTime.Get_String(dtTest,"Test2 : DD/MM/YYYY HH:NN:SS")
	
	Print "Diff Year : " + TDateTime.Diff("Y",dttest,dttest2)
	Print "Diff Month : " + TDateTime.Diff("M",dttest,dttest2)
	Print "Diff Day : " + TDateTime.Diff("D",dttest,dttest2)
	Print "Diff Hour : " + TDateTime.Diff("H",dttest,dttest2)
	Print "Diff Minute : " + TDateTime.Diff("N",dttest,dttest2)
	Print "Diff Second : " + TDateTime.Diff("S",dttest,dttest2)

	Print TDateTime.Get_String(TDateTime.Add("Y",25,dtTest),"now + 25 years : DD/MM/YYYY HH:NN:SS")
	Print TDateTime.Get_String(TDateTime.Add("M",25,dtTest),"now + 25 months : DD/MM/YYYY HH:NN:SS")
	Print TDateTime.Get_String(TDateTime.Add("D",25,dtTest),"now + 25 days : DD/MM/YYYY HH:NN:SS")
	Print TDateTime.Get_String(TDateTime.Add("H",25,dtTest),"now + 25 hours : DD/MM/YYYY HH:NN:SS")
	Print TDateTime.Get_String(TDateTime.Add("N",25,dtTest),"now + 25 minutes : DD/MM/YYYY HH:NN:SS")
	Print TDateTime.Get_String(TDateTime.Add("S",25,dtTest),"now + 25 seconds : DD/MM/YYYY HH:NN:SS")
	
	Print TDateTime.Get_String(dtTest,"Today is DDDD, D of MMMM of the year YYYY")
end rem
'###############################################################################################################
'End of Test Code
'###############################################################################################################



'###############################################################################################################
'Object
'	TDateTime
'###############################################################################################################
'Description
'	Handles Date and Time functionalities and variables
'###############################################################################################################
Type TDateTime
	'******************************************************************************************************
	'** Constants        **********************************************************************************
	'******************************************************************************************************
	Const MonthList$="JANFEBMARAPRMAYJUNJULAUGSEPOCTNOVDEC"
	Const lngEnglish = 0
	Const lngFrench = 1
	
	'I list this in constants because even tho its a global, i'd rather have it a constant but blitzmax cant
	Global txtMonth_Abbrv:String[][]= 	..
		[..
		["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"],..
		["Janv","Févr","Mars","Avr","Mai","Juin","Juil","Août","Sept","Oct","Nov","Déc"]..
		]

	Global txtMonth_Full:String[][]= 	..
		[..
		["January","February","March","April","May","June","July","August","September","October","November","December"],..
		["Janvier","Février","Mars","Avril","Mai","Juin","Juillet","Août","Septembre","Octobre","Novembre","Décembre"]..
		]

	Global txtDay_Abbrv:String[][]= 	..
		[..
		["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],..
		["Dim","Lun","Mar","Mer","Jeu","Ven","Sam"]..
		]
		
	Global txtDay_Full:String[][]= 	..
		[..
		["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"],..
		["Dimanche","Lundi","Mardi","Mercredi","Jeudi","Vendredi","Samedi"]..
		]
		
	
	'******************************************************************************************************
	'** Globals          **********************************************************************************
	'******************************************************************************************************	
	Global Language = 0
	
	'******************************************************************************************************
	'** Variables        **********************************************************************************
	'******************************************************************************************************
	Field Year:Int
	Field Month:Int
	Field Day:Int
	Field Hour:Int
	Field Minute:Int
	Field Second:Int
	
	'******************************************************************************************************
	'** Functions        **********************************************************************************
	'******************************************************************************************************

	'== Create ===============================================================================
	'	Instanciates and returns a new TDateTime object
	Function Create:TDateTime()
		Return New TDateTime
	End Function
	
	'== Add ===============================================================================
	'	Adds a value to a date depending on the selected interval
	'	Interval:
	'		Y = Year, M = Month, D = Day, H = Hour, N = Minute, S = Seconds
	'	Returns the new TDateTime
	Function Add:TDateTime(pInterval$, pVal:Int, pDate1:TDateTime)
		Local i:Int
		Local pDate:TDateTime
		
		pDate1.Copy(pDate)
		
		
		Select pInterval$
			Case "Y"	'Year
				pDate.Year :+ pVal
			Case "M"	'Month
				'Checks if passed value is more or equal to a Year
				i = pVal / 12
				If i <> 0 Then
					' If more or equal to a Year then use the Year add 
					pDate = TDateTime.Add("Y", i, pDate)
					pDate.Month :+ pVal mod 12
				  Else
				  	pDate.Month :+ pVal
				End If
				
				'Checks if adding remaining Months would rollover the Year
				i = pDate.Month / 12
				If i > 0 Then
					pDate.Month = pDate.Month - (i * 12)
					pDate = TDateTime.Add("Y", i, pDate)
				End If
			Case "D"	'Day
				'Converts in julian to add by day and then back to dates
				i = TDateTime.Conv_Date2Julian(pDate)
				i :+ pVal
				TDateTime.Conv_Julian2Date i, pDate
			Case "H"	'Hour
				'Checks if passed value is more or equal to a Day
				i = pVal / 24
				If i <> 0 Then
					' If more or equal to a Day then use the Day add 
					pDate = TDateTime.Add("D", i, pDate)
					pDate.Hour :+ pVal mod 24
				  Else
				  	pDate.Hour :+ pVal
				End If
				
				'Checks if adding remaining Hours would rollover the Day
				i = pDate.Hour/ 24
				If i > 0 Then
					pDate.Hour = pDate.Hour - (i * 24)
					pDate = TDateTime.Add("D", i, pDate)
				End If					
			Case "N"	'Minute
				'Checks if passed value is more or equal to an Hour
				i = pVal / 60
				If i <> 0 Then
					' If more or equal to a Hour then use the Hour add 
					pDate = TDateTime.Add("H", i, pDate)
					pDate.Minute :+ pVal mod 60
				  Else
				  	pDate.Minute :+ pVal
				End If

				'Checks if adding remaining Minutes would rollover the Hour
				i = pDate.Minute/ 60
				If i > 0 Then
					pDate.Minute = pDate.Minute - (i * 60)
					pDate = TDateTime.Add("H", i, pDate)
				End If			
			Case "S"	'Seconds
				'Checks if passed value is more or equal to a Minute
				i = pVal / 60
				If i <> 0 Then
					' If more or equal to a Minute then use the Minute add 
					pDate = TDateTime.Add("N", i, pDate)
					pDate.Second :+ pVal mod 60
				  Else
				  	pDate.Second :+ pVal
				End If
				
				'Checks if adding remaining Seconds would rollover the Minute
				i = pDate.Second / 60
				If i > 0 Then
					pDate.Second = pDate.Second - (i * 60)
					pDate = TDateTime.Add("N", i, pDate)
				End If
		End Select
		
		Return pDate
	End Function
	
	'== Diff ===============================================================================
	'	Returns the difference between two dates depending on specified interval
	'	Interval:
	'		Y = Year, M = Month, D = Day, H = Hour, N = Minute, S = Seconds
	'	If pDate2 < pDate1 the result will be negative
	Function Diff(pInterval$, pDate1:TDateTime, pDate2:TDateTime)
		Local i:Int
		Local j:Int
		
		Select pInterval$
			Case "Y"	'Year
				i = pDate2.Year - pDate1.Year
			Case "M"	'Month
				i = (pDate2.Year - pDate1.Year) * 12 + (pDate2.Month - pDate1.Month)
			Case "D"	'Day
				'Converts in julian to substract days
				i = TDateTime.Conv_Date2Julian(pDate2) - TDateTime.Conv_Date2Julian(pDate1)
			Case "H"	'Hour
				i = (TDateTime.Conv_Date2Julian(pDate2) - TDateTime.Conv_Date2Julian(pDate1)) * 24
				i :+ pDate2.Hour - pDate1.Hour
			Case "N"	'Minute
				i = (TDateTime.Conv_Date2Julian(pDate2) - TDateTime.Conv_Date2Julian(pDate1)) * 1440
				i :+ (pDate2.Hour * 60 + pDate2.Minute) - (pDate1.Hour * 60 + pDate1.Minute)
			Case "S"	'Seconds
				i = (TDateTime.Conv_Date2Julian(pDate2) - TDateTime.Conv_Date2Julian(pDate1)) * 86400
				i :+ (pDate2.Hour * 3600 + pDate2.Minute * 60 + pDate2.Second) - (pDate1.Hour * 3600 + pDate1.Minute * 60 + pDate1.Second)

		End Select
		
		Return i
	End Function
	
	'== Get_String ===============================================================================
	'	Returns the Date / Time formated to the user's needs
	'	pFormat:
	'		YYYY	= Year as 4-digit number
	'		YY		= Year as 2-digit number
	'		MMMM	= Month in full description (January, February...) -- Not Implemented
	'		MMM		= Month as an abbreviation (JAN, FEB...)
	'		MM		= Month as 2-digit number (leading zero)
	'		M 		= Month without leading zeros
	'		DDDD	= Day in full description (Monday, Tuesday...) -- Not Implemented
	'		DDD		= Day as an abbreviation (MON, TUE...) -- Not Implemented
	'		DD		= Day as a 2-digit number (leading zero)
	'		D 		= Day without leading zeros
	'		HH		= Hour as a 2-digit number (leading zero)
	'		H 		= Hour without leading zeros
	'		NN		= Minute as a 2-digit number (leading zero)
	'		N 		= Minute without leading zeros
	'		SS		= Second as a 2-digit number (leading zero)
	'		S 		= Second without leading zeros
	'
	'		Note : 	Any other string found will be kept the same. ex: "The D of MMMM" would give
	'				The 2 of January
	Function Get_String:String(pDate:TDateTime, pFormat:String)
		Local tStr:String
		Local i, j, l
		Local tLast:String
		Local tTest:String
		
		l = pFormat.length
		
		tLast = ""
		tStr = ""
	
		For i = 1 To l
			tTest = Mid$(pFormat, i, 1)
			If tLast <> tTest  Then
			  	If tLast <> "" Then
					tStr = tStr + Get_String_Parser(pFormat, tLast, i, j, pDate)
			  	End If
			  	tLast = tTest
			  	J = 1
			  Else
				J :+ 1
			End If
		Next
		
		tStr = tStr + Get_String_Parser(pFormat, tLast, i, j, pDate)
		
		Return tStr
	End Function
	
	'== Get_String_Parser ===============================================================================
	'	Parses a single element of the string
	Function Get_String_Parser:String(pStr:String, pLast:String, S:Int, L:Int, pDate:TDateTime)
		Local tStr:String
		
		Select pLast
			
  			Case "Y"
  				Select L
  					Case 2
  						tStr = Get_String_Parser_Formater(pDate.Year,2)
  					Case 4
  						tstr = Get_String_Parser_Formater(pDate.Year,4)
  				End Select
  			Case "M"
  				Select L
  					Case 1
  						tStr = String.FromInt(pDate.Month)
  					Case 2
  						tStr = Get_String_Parser_Formater(pDate.Month,2)
  					Case 3
  						tStr = txtMonth_Abbrv[Language][pDate.Month-1]
  					Case 4
  						tStr = txtMonth_Full[Language][pDate.Month-1]
  				End Select
  			Case "D"
  				Select L
  					Case 1
  						tStr = String.FromInt(pDate.Day)
  					Case 2
  						tStr = Get_String_Parser_Formater(pDate.Day,2)
  					Case 3
  						tStr = txtDay_Abbrv[Language][TDateTime.WeekDay(pDate)]
  					Case 4
  						tStr = txtDay_Full[Language][TDateTime.WeekDay(pDate)]
  				End Select  			
  			Case "H"
  				Select L
  					Case 1
  						tStr = String.FromInt(pDate.Hour)
  					Case 2
  						tStr = Get_String_Parser_Formater(pDate.Hour,2)
  				End Select 
   			Case "N"
   				Select L
  					Case 1
  						tStr = String.FromInt(pDate.Minute)
  					Case 2
  						tStr = Get_String_Parser_Formater(pDate.Minute,2)
  				End Select 
  			Case "S"
  				Select L
  					Case 1
  						tStr = String.FromInt(pDate.Second)
  					Case 2
  						tStr = Get_String_Parser_Formater(pDate.Second,2)
  				End Select 
  			Default
  				tStr = Mid$(pStr, S-L, L)
  		End Select
  		
  		Return tStr
	End Function

	'== Get_String_Parser_Formater ===============================================================================
	'	Formats with leading zeros if necessary
	Function Get_String_Parser_Formater:String(Val, Nb)
		Local tStr:String
		Local tStr2:String
		Local i

		tStr2 = ""
		tStr = String.FromInt(Val)
		
		If tStr.length < Nb Then
			For i = 1 To Nb - tStr.length 
				tStr2 = tStr2 + "0"
			Next
			tStr2 = tStr2 + tStr
		  Else
		  	tStr2 = Right$(tStr,Nb)
		End If
		
		Return tStr2
	End Function
	
	'== Conv_Date2Julian ===============================================================================
	'	Converts a TDateTime into a Julian Number.
	'	Returns the Julian Number as an Int
	Function Conv_Date2Julian(pDate:TDateTime)
		Local JulianDate:Int
		
		' conversion taken from a website (lost link)
		JulianDate = 367 * pDate.Year - ((7 * (pDate.Year + 5001 + ((pDate.Month - 9) / 7))) / 4) + ((275 * pDate.Month) / 9) + pDate.Day + 1729777
	
		Return JulianDate
	End Function
	
	'== Conv_Julian2Date ===============================================================================
	'	Converts a Julian Numbert into a TDateTime
	Function Conv_Julian2Date(pJulian:Int, pDate:TDateTime var)
	    Local l:Int
	    Local k:Int
	    Local n:Int
	    Local i:Int
	    Local j:Int
	
	    ' conversion taken from a website (lost link)
	    j = pJulian + 1402
	    k = ((j - 1) / 1461)
	    l = j - 1461 * k
	    n = ((l - 1) / 365) - (l / 1461)
	    i = l - 365 * n + 30
	    j = ((80 * i) / 2447)
	    pDate.Day = i - ((2447 * j) / 80)
	    i = (j / 11)
	    pDate.Month = j + 2 - 12 * i
	    pDate.Year = 4 * k + n + i - 4716
	End Function
	
	'== WeekDay ===============================================================================
	'	Returns the day of the week (0 = sunday, 6 = saturday)
	Function WeekDay(pDate:TDateTime)
		Local y
		Local M
		Local W
		
 		If (pDate.Month < 3) Then
			M = pDate.Month+12
        	y = pDate.Year-1
          Else
          	M = pDate.Month
          	y = pDate.Year
        End If

        W = (pDate.Day + Int((13 * M - 27) / 5) + y + Int(y / 4) - Int(y / 100) + Int(y / 400)) mod 7
                      
		Return W
	End Function	
	
	'******************************************************************************************************
	'** Methods          **********************************************************************************
	'******************************************************************************************************
	
	'== New ===============================================================================
	'	Initialises the new object to zero 
	Method New()
		Year = 0
		Month = 0
		Day = 0
		Hour = 0
		Minute = 0
		Second = 0
	End Method
	
	'== Copy ===============================================================================
	'	Copies the date/time of the current object over the parameter object
	Method Copy(pDate:TDateTime var)
		If pDate = Null Then
			pDate = TDateTime.Create()
		End If
		
		pDate.Set_DateTimeSerial Year, Month, Day, Hour, Minute, Second
		
	End Method
	
	'== compare ===============================================================================
	'	Override of compare method
    Method compare:Int(pDate:Object)
		Local i:Int
		Local j:Int
		Local r:Int
		
		'Compares the two dates in julian (Days)
		r = TDateTime.Conv_Date2Julian(Self) - TDateTime.Conv_Date2Julian(TDateTime(pDate))
		
		If r = 0 Then 'If the dates are equal, compares the time in seconds
			i = Self.Hour * 3600 + Self.Minute * 60 + Self.Second
			j = TDateTime(pDate).Hour * 3600 + TDateTime(pDate).Minute * 60 + TDateTime(pDate).Second
			r= i - j
		End If
		
		Return r
    End Method	

	'== Set_DateSerial ===============================================================================
	'	Sets the Date by specifying each parameter
	Method Set_DateSerial(pYear:Int, pMonth:Int, pDay:Int)
		Year = pYear
		Month = pMonth
		Day = pDay
	End Method
	
	'== Set_TimeSerial ===============================================================================
	'	Sets the Time by specifying each parameter
	Method Set_TimeSerial(pHour:Int, pMinute:Int, pSecond:Int)
		Hour = pHour
		Minute = pMinute
		Second = pSecond
	End Method	
	
	'== Set_DateTimeSerial ===============================================================================
	'	Sets the Date and Time by specifying each parameter
	Method Set_DateTimeSerial(pYear:Int, pMonth:Int, pDay:Int, pHour:Int, pMinute:Int, pSecond:Int)
		Set_TimeSerial(pHour, pMinute, pSecond)
		Set_DateSerial(pYear, pMonth, pDay)
	End Method
	
	'== Set_DateCurrent ===============================================================================
	'	Sets the Date to the current system date
	Method Set_DateCurrent()
		Local tDate$
		
		tDate$ = CurrentDate$()
		
		Day = Int(tDate$[..2])
		Month = (Instr(MonthList$, tDate$[3..6].ToUpper(), 1) / 3) + 1
		Year = Int(tDate$[tDate$.length - 4..])
	End Method
	
	'== Set_TimeCurrent ===============================================================================
	'	Sets the Time to the current system date
	Method Set_TimeCurrent()
		Local tTime$
		
		tTime$ = CurrentTime$()
		
		Hour = Int(tTime$[..2])
		Minute = Int(tTime$[3..6])
		Second = Int(tTime$[7..])
	End Method
	
	'== Set_DateTimeCurrent ===============================================================================
	'	Sets the Date and Time to the current system date
	Method Set_DateTimeCurrent()
		Set_DateCurrent
		Set_TimeCurrent
	End Method		
	
	'== Debug_PrintDate ===============================================================================
	'	Prints the Date and Time in the debug console
	Method Debug_PrintDate()
		Print Year + "/" + Month + "/" + Day + "  " + Hour + ":" + Minute + ":" + Second
	End Method
End Type
