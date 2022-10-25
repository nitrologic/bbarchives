; ID: 3234
; Author: Bobysait
; Date: 2015-12-29 07:29:00
; Title: Minimalist Date+Time type
; Description: Manage user defined time and dates

SuperStrict

Type TTimeDate
	
	Global DAY_COUNT:Int[][]	=	[[0,31,28,31,30,31,30,31,31,30,31,30,31],[0,31,29,31,30,31,30,31,31,30,31,30,31]];
	
	Global TABLE_DAYS:Int[][];
	Global TABLE_MONTHES:Int[][];
	
	Global TABLE_MAP_DAYS:Int[][][];
	
	Global TOSTR_DateSeparator:String="/";
	Global TOSTR_TimeSeparator:String=":";
	Global TOSTR_Separator:String = " - "
	
	Const MS_TO_SC:Int		=	1000;
	Const SC_TO_MN:Int		=	60;
	Const MN_TO_HR:Int		=	60;
	Const HR_TO_DA:Int		=	24;
	
	Const MS_TO_MN:Int		=	MS_TO_SC*SC_TO_MN;
	Const MS_TO_HR:Int		=	MS_TO_MN*MN_TO_HR;
	Const MS_TO_DA:Int		=	MS_TO_HR*HR_TO_DA;
	
	Function init_LookupTables()
		
		If TABLE_DAYS=Null
			TABLE_DAYS			=	[New Int[365],New Int[366]];
			TABLE_MONTHES		=	[New Int[365],New Int[366]];
			TABLE_MAP_DAYS		=	[New Int[][13],New Int[][13]];
			
			Local d_offset_0:Int	=	0;
			Local d_offset_1:Int	=	0;
			
			For Local m:Int = 1 To 12
				TABLE_MAP_DAYS[0][m] = New Int[32];
				TABLE_MAP_DAYS[1][m] = New Int[32];
			Next
			
			For Local m:Int = 1 To 12
				For Local d:Int = 0 Until DAY_COUNT[0][m]
					TABLE_DAYS[0][d_offset_0+d]		=	d+1;
					TABLE_MONTHES[0][d_offset_0+d]	=	m;
					TABLE_MAP_DAYS[0][m][d+1]=	d_offset_0+d;
				Next;
				d_offset_0 :+ DAY_COUNT[0][m];
				
				For Local d:Int = 0 Until DAY_COUNT[1][m]
					TABLE_DAYS[1][d_offset_1+d]		=	d+1;
					TABLE_MONTHES[1][d_offset_1+d]	=	m;
					TABLE_MAP_DAYS[1][m][d+1]=	d_offset_1+d;
				Next;
				d_offset_1 :+ DAY_COUNT[1][m];
			Next;
		EndIf;
		
	End Function
	
	Function DayId:Int(year:Int, month:Int, day:Int)
		Return TABLE_MAP_DAYS[isBissextile(year)][month][day];
	End Function
	
	Field y:Int;	' year
	Field d:Short;	' day of the year
	Field m:Int;	' millisecs from the day
	
	' Create a time instance
	Function Create:TTimeDate()
		TTimeDate.init_LookupTables();
		
		Local t:TTimeDate = New TTimeDate;
		t.y = 0; t.d = 0; t.m = 0;
		Return t;
	End Function
	
	' set the current date
	Method setDate(year:Int, month:Int, day:Int)
		Self.y = year;
		Self.d = TABLE_MAP_DAYS[bissextile()][month][day];
	End Method
	
	' set the current time
	Method setTime(hour:Int,minute:Int,second:Int,milliseconds:Int)
		Self.m = milliseconds + second*MS_TO_SC + minute * MS_TO_MN + hour * MS_TO_HR;
	End Method
	
	' set both date and time
	Method Set(year:Int, month:Int, day:Int, hour:Int,minute:Int,second:Int,milliseconds:Int)
		Self.y = year;
		Self.d = TABLE_MAP_DAYS[bissextile()][month][day];
		Self.m = milliseconds + second*MS_TO_SC + minute * MS_TO_MN + hour * MS_TO_HR;
	End Method
	
	' get the year
	Method getYr:Int() ; Return Self.y; End Method
	' get the global day id in the year ( in the range [0,day_per_year[ )
	Method getDayId:Int(); Return Self.d; End Method
	' get the global millisecond on the day (in the range [0,MS_TO_DAY[ )
	Method getMilliseconds:Int(); Return Self.m; End Method
	
	' get extracted month
	Method getMt:Int() ; Return TABLE_MONTHES[bissextile()][Self.d]; End Method
	' get extracted day of the month
	Method getDa:Int() ; Return TABLE_DAYS[bissextile()][Self.d]; End Method
	' get extracted hour of the day
	Method getHr:Int() ; Return Int ((Float(Self.m) / MS_TO_HR) Mod(HR_TO_DA)); End Method
	' get extracted minute ...
	Method getMn:Int() ; Return Int ((Float(Self.m) / MS_TO_MN) Mod(MN_TO_HR)); End Method
	' get extracted second
	Method getSc:Int() ; Return Int (Float(Self.m) / MS_TO_SC) Mod(SC_TO_MN); End Method
	' get extracted remaining milliseconds before the next second
	Method getMS:Int() ; Return Self.m Mod(MS_TO_SC); End Method
	
	' convert date+Time to a string
	Method ToString:String()
		Return	fYear	(getYr())+ TOSTR_DateSeparator	+..
				fMonth	(getMt())+ TOSTR_DateSeparator	+..
				fDay	(getDa())+ ..
				TOSTR_Separator +..
				fHour	(getHr()) + TOSTR_TimeSeparator +..
				fMinute	(getMn()) + TOSTR_TimeSeparator +..
				fSecond	(getSc()) + TOSTR_TimeSeparator +..
				fMillis	(getMs());
	End Method
	
	' increase day ID
	' @inc can be lower or higher than 0
	' if inc<0 then it decrease the day ID (day and year will be updated automatically if day<0 or day>'day_per_year')
	' bissextile years are considered.
	Method IncDa(inc:Int)
		
		Local yb:Int = 365+Self.bissextile();
		
		If inc>0
			
			While inc>0
				
				If inc+Self.d>=yb
					inc :- (yb-Self.d);
					Self.d = 0;
					Self.y :+ 1;
					yb = 365+Self.bissextile();
				Else
					Self.d :+ inc;
					inc = 0;
				EndIf;
				
			Wend;
			
		Else
			
			While inc<0
				
				If inc+Self.d<0
					Self.y :- 1;
					yb = 365+Self.bissextile();
					inc :+ Self.d+1;
					Self.d = yb-1;
				Else
					Self.d :+ inc;
					inc = 0;
				EndIf;
				
			Wend;
			
		EndIf;
		
	End Method
	
	' increase milliseconds
	' @inc can be lower or higher than 0
	Method IncMs(inc:Int)
		If inc>0
			
			While inc>0
				If Self.m+inc>=MS_TO_DA
					inc :- (MS_TO_DA - Self.m);
					Self.m = 0;
					Self.IncDa(1);
				Else
					Self.m :+ inc;
					inc = 0;
				EndIf;
			Wend;
			
		Else
			
			While inc<0
				If Self.m+inc<0
					Self.IncDa(-1);
					inc :+ Self.m+1;
					Self.m = MS_TO_DA-1;
				Else
					Self.m :+ inc;
					inc = 0;
				EndIf;
			Wend;
			
		EndIf;
		
	End Method
	
	' increase hour, minute, second
	Method IncHr(inc:Int) ; Self.IncMs(inc*MS_TO_HR); End Method
	Method IncMn(inc:Int) ; Self.IncMs(inc*MS_TO_MN); End Method
	Method IncSc(inc:Int) ; Self.IncMs(inc*MS_TO_SC); End Method
	
	' returns true if the specified @year is bissextile
	Function isBissextile:Byte(year:Int)
		Return (year Mod(4))=0
	End Function
	
	' returns True if the TTimeDate instance' year is bissextile
	Method bissextile:Byte()
		Return (Self.y Mod(4))=0
	End Method
	
	'#region - Internal stuff -
	' formated string for date and time members
	Function fYear:String(yr:Int)
		If yr>=0
			Return "+"+Replace(RSet(yr,4)," ","0");
		Else
			Return "-"+Replace(RSet(Abs(yr),4)," ","0");
		EndIf
	End Function
	Function fMonth		:String(mt:Byte) ; Return Replace(RSet(mt,2)," ","0"); End Function
	Function fDay		:String(da:Byte) ; Return Replace(RSet(da,2)," ","0"); End Function
	Function fHour		:String(hr:Byte) ; Return Replace(RSet(hr,2)," ","0"); End Function
	Function fMinute	:String(mn:Byte) ; Return Replace(RSet(mn,2)," ","0"); End Function
	Function fSecond	:String(sc:Byte) ; Return Replace(RSet(sc,2)," ","0"); End Function
	Function fMillis	:String(ms:Short); Return Replace(RSet(ms,3)," ","0"); End Function
	'#end region
	
End Type


Local t:TTimeDate = TTimeDate.Create()
	't.setDate(2015,12,29);
	't.setTime(15,32,10,850);
	
' set current date and time to 2015/12/29 at 15h32 10.85 sec (10.85 sec =10 sec + 850 ms)
t.Set(2015,12,29, 15,32,10,850);
Print t.ToString();

' remove 2016 years -> reach the year -1 ad
t.incDa( - ((365*3+366) * 504) );		' 3 times 365 + 1 time 366 = 4 years (*504 = 2016)
				Print t.ToString();
t.IncHr(8);		Print t.ToString();		' add 8 hours to the current time
t.IncMn(27);	Print t.ToString();		' add 27 minutes to the current time
t.IncSc(49);	Print t.ToString();		' etc ...
t.IncMs(150);	Print t.ToString();		' etc ...
