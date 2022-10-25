; ID: 2505
; Author: degac
; Date: 2009-06-13 05:29:07
; Title: CalendarGadget
; Description: This creates a calendar gadget with appointment

Rem
		Calendar gadget
		
		degac + sebholl gadget redraw improvements
		
		07 jun 2009		first release, some basic fixes
		08 jun 2009		the Calendar Gadget returns (every time you click/change the date) the new date
					in the EventExtra field
					you can have multiple appointments in the same date (dd-mm)
					you can have 'fixed' appointment for a specific year (dd-mm-yyyy)
					
		13 jun 2009		SetGadgetColor support
					Some bug fix with calendar length & panel
					CreateCalendar has 2 styles
					- CAL_WEEKEND (1) : the SUNDAY and SATURDAY label are both 'coloured'
					- CAL_HIDDEN  (2) : the selected day is not showed on the calendar	
					Changed GetCalendarAppointment & RemoveAppointments to get / remove a single appointment
					Appointments are 'numbered' and displayed automatically


		02 ago 2010
		
					Added:
					
						+ SetCalendarLocale(calendar:tcalendar,weeks,months)
						
						To localize days name and months
						usage:
						
						SetCalendarLocale(cal,"Lu,Ma,Me,Gi,Ve,Sa,Do","Gen,Feb,Mar,Apr,Mag,Giu,Lug,Ago,Set,Ott,Nov,Dic")
						
						Day week and months *MUST* be separated by commas
						

						+ SetCalendarHoliday(calendar:tcalendar,date,descriptions)
						
						To add an holidays calendar
						
						Usage:
						
						SetCalendarHoliday("01-01,25-12","New Year,Christmas")
						
						dates and descriptions *MUST* be separated by commas
						
						dates are written as DAY-MONTH
						
		14 AGO 2010
		
				Fixed:	- Replaced const with CAL_WEEKEND,CAL_HIDDEN,CAL_TODAY,CAL_PICKER
				- fixed WEEKEND behaviour
		Added:	- 2 new constant:	
				CAL_TODAY 	: to come back to the current date
				CAL_PICKER	: to allow the user to choose a date and close the gadget
	
End Rem	

SuperStrict
Import MaxGUI.Drivers

Local window:TGadget = CreateWindow("Calendar v. 1.3",100,100,210,300,,window_titlebar|window_center|WINDOW_RESIZABLE)
Local data:TCalendar = CreateCalendar(10,40,180,,window,CAL_WEEKEND|CAL_TODAY|CAL_PICKER)
'Local data2:TCalendar = CreateCalendar(210,40,180,,window,CAL_WEEKEND|CAL_HIDDEN)
SetGadgetLayout data, EDGE_RELATIVE,EDGE_RELATIVE,EDGE_RELATIVE,EDGE_RELATIVE
Local label:TGadget = CreateLabel(" ",10,5,200,25,window)

SetGadgetColor data,200,200,200
'SetGadgetLayout data2, EDGE_RELATIVE,EDGE_RELATIVE,EDGE_RELATIVE,EDGE_RELATIVE
'Local label2:TGadget = CreateLabel("Label",210,5,200,25,window)

'this method to set an Appointment-reminder on the calendar

SetCalendarAppointment(data,"10-06","Test application!")
SetCalendarAppointment(data,"10-06","Second appointment...~nRemember to conquer the world!")
SetCalendarAppointment(data,"10-06","Buy the milk")
SetCalendarAppointment(data,"12-06","World launch!")
SetCalendarAppointment(data,"12-08-2010","---appointment---1")
SetCalendarAppointment(data,"12-08-2010","---appointment---2")
SetCalendarAppointment(data,"09-06-2009","This is a fixed date")
RemoveAppointments(data,"10-06",2)
'RemoveAppointments(data,"12-06")
'SetCalendarHoliday(data,"15-08,16-08","Ferragosto")
'SetCalendarLocale(data,"Lu,Ma,Me,Gi,Ve,Sa,Do","Gen,Feb,Mar,Apr,Mag,Giu,Lug,Ago,Set,Ott,Nov,Dic")
'SetCalendarLocale(data,"Lu,Ma,Me,Gi,Ve,Sa,Do","Gennaio,Febbraio,Marzo,Aprile,Maggio,Giugno,Luglio,Agosto,Settembre,Ottobre,Novembre,Dicembre")
RedrawGadget data 'you need to 'redraw' the gadget to see the appointments set for the current month
'RedrawGadget data2

Local app$[]=GetCalendarAppointment(data,"10-06",1)

For Local ss$=EachIn app
	Print "Appointment: {"+ss+"}"
Next


While True
	WaitEvent
		Select EventID()
		Case EVENT_GADGETCLOSE
			If EventSource()=DATA
				Print "Data finale : "+String(EventExtra())
				Print "Oppure : "+EventX()+" "+EventY()+" "+EventData()
			End If
		Case event_gadgetaction
			
			If EventSource() = data
				SetGadgetText label,String(EventExtra())
			End If
			
			'If EventSource() = data2
		'			SetGadgetText label2,String(EventExtra())
		'	End If
			
						
		Case event_windowclose
			If EventSource() = window End
		End Select
Wend

End
Rem

		Calendar gadget
		v.1.3 - 14 aug 2010
		
		Fixed:	- Replaced const with CAL_WEEKEND,CAL_HIDDEN,CAL_TODAY,CAL_PICKER
				- fixed WEEKEND behaviour
		Added:	- 2 new constant:	CAL_TODAY : to come back to the current date
							CAL_PICKER: to allow the user to choose a date and close the gadget
			
		
		

End Rem


Rem
bbdoc: Create a basic calendar.
about: Displays a simple calendar for year/month reporting the (eventual) appointments or festivity<br>
it returns the new date in the @{TEvent.Extra} field.

@x, @y are the initial position, while @w and @h (if specified) are the width and the height of the 
calendar.

Remember that there are @minimum size dimensions for wide and height.

[ @Style | @Description 
* WEEKEND | Saturday and Sunday are coloured
* HIDDEN | The day selected is not shown 
]
<br>
See also: #SetCalendarAppointment, #GetCalendarAppointment, #RemoveAppointments
End Rem
Function CreateCalendar:TCalendar(x:Int,y:Int,w:Int = 152,h:Int = 191,group:TGadget = Null,_style:Int = 0)
	If group = Null Return Null	
	Local min_w:Int = 152,min_h:Int = 191
	If w<min_w w = min_w
	If h<min_h Or h>min_h h = min_h
	If _style&CAL_TODAY Or _style&CAL_PICKER h:+25
	
	Return TCalendar.Create(x,y,w,h,group,_style)
End Function
Rem
bbdoc: Set an appointment for the date (DD-MM) or (DD-MM-YYYY)
about: You can specify a simple date (Day-Month) and the appointment will be repeated every year.<br>
Or you can specify a complete date (day-month-year) in the form of DD-MM-YYYY, and in this case the appointment will be displayed only in the year YYYY.<br>
For the same date you can specify more appointments, they will be queued.<br>

See also: #CreateCalendar, #GetCalendarAppointment, #RemoveAppointments
End Rem	
Function SetCalendarAppointment(calendar:TCalendar,date$ = "",app$ = "")
	If calendar = Null Return
	If date = "" Return
	calendar.SetAppointment(date,app)
End Function

Rem
bbdoc: Set Holidays
about: It creates the national/religious holiday

@week and @description MUST be string with value separated by commas.
The date format must be in the format 01-02

Example: "01-01,25-12","New year,Christmas"
End Rem
Function SetCalendarHoliday(calendar:tcalendar,week$,description$)
	If calendar=Null Return
	If week="" Or description="" Return
	calendar.SetHoliday(week,description)
End Function

Rem
bbdoc: Set Calendar Locale
about: It uses the local name for months and days
@week and @month must contain value separated by commas
Day week starts with MONDAY.
Example:

SetCalendarLocale(data,"Lu,Ma,Me,Gi,Ve,Sa,Do","Gen,Feb,Mar,Apr,Mag,Giu,Lug,Ago,Set,Ott,Nov,Dic")

End Rem
Function SetCalendarLocale(calendar:tcalendar,week$,month$)
	If calendar=Null Return
	If week="" Or month="" Return
	calendar.SetLocale(week,month)
End Function

Rem
	bbdoc: Get the appointment(s) for a determinated date (DD-MM) or (DD-MM-YYYY)
	about: It returns an array of string of the appointment(s) of that @date.<br>
	If @index is specified ONLY the appointment number Index will be returned. Index is between 1 and number of appointments.<br>
	
	See also: #CreateCalendar, #SetCalendarAppointment, #RemoveAppointments
End Rem
Function GetCalendarAppointment:String[](Calendar:TCalendar,date$ = "",index:Int=-1)
If date = "" Or calendar = Null Return [""]
	Return calendar.GetAppointment(date,index-1)
End Function

Rem
bbdoc: Remove all appointments
about: This function will remove all the appointments from a calendar<br>
If a @date is specified only the appointment(s) on that @date will be removed<br>
If @index is specified ONLY the appointment number Index will be deleted. Index is between 1 and number of appointments.<br>

See also: #CreateCalendar, #SetCalendarAppointment, #GetCalendarAppointment
End Rem
Function RemoveAppointments(calendar:TCalendar,date$ = "",index:Int=-1)
If date = ""
	ClearMap(calendar.map_appointment)
Else
	If index=-1
		MapRemove calendar.map_appointment,date
	Else
		'remove the INDEX appointments (0..n)
		index:-1
		Local _array_des$[],_new_array$[]
		_array_des = String[](MapValueForKey(calendar.map_appointment,date))
		If index>=0 And index<_Array_des.length
		_array_des[index]=""
			Local coco:Int
			For Local co$=EachIn _Array_Des
				If co<>""
				_new_array=_new_array[..coco+1]
				_new_array[coco]=co
				coco:+1
				End If
			Next
			MapRemove calendar.map_appointment,date
			MapInsert calendar.map_appointment,date,_new_Array
		
		End If		
	End If
End If
End Function

	Const CAL_WEEKEND:Int=	1
	Const CAL_HIDDEN:Int=		2
	Const CAL_TODAY:Int=		4
	Const CAL_PICKER:Int=		8


Type TCalendar Extends Tproxygadget

	Global day_name$[] = ["Mo","Tu","We","Th","Fr","Sa","Su"]'["Lu","Ma","Me","Gi","Ve","Sa","Do"]
	Global month_name$[] = ["January","February","March","April","May","June","July","August","September","October","November","December"]
	Global month_days:Int[] = [31,28,31,30,31,30,31,31,30,31,30,31]

	Field map_dayfest:tmap = CreateMap()
	Field map_appointment:tmap = CreateMap()
	Field _fest_data$[] = ["01-01","06-01","25-04","01-05","02-06","15-08","01-11","08-12","25-12","26-12"]
	Field _fest_Desc$[] = ["New year","Epifany","Liberation day","Worker day","Republic Day","Ferragosto","All saints","Immaculate conception","Nativity","S.Steven"]
	Field _app_data$[]
	Field _app_desc$[]
	Field lbl_day:tgadget[]
	
	Field _keephidden:Int
	Field panel:TGadget
	Field style:Int
	Field year_dec:TGadget,year_inc:TGadget,year:TGadget
	Field month_dec:TGadget,month_inc:TGadget,month:TGadget
	Field day_panel:TGadget
	Field days:TGadget[49],label:TGadget[7]
	Field current_year:Int,current_month:Int,current_day:Int
	Field day_wide:Int = 21,day_height:Int = 21
	Field max_wide:Int = 146,max_heigth:Int = 191
	Field data$
	Field pickday:tgadget,today:tgadget
	
	Method SetLocale(_week$="",_month$="")
		If _week="" Or _month="" Return
		
		day_name=_week.split(",")
		month_name=_month.split(",")
		
		If Len(day_name)<7 day_name=day_name[..7]
		If Len(month_name)<12 month_name=month_name[..12]
		
		Local con:Int
		For Local kd:tgadget=EachIn label
			SetGadgetText kd,day_name[con]
			con:+1
		Next
	
	End Method
	
	Method SetHoliday(_day$="",_desc$="")
		If _day="" Or _desc="" Return
		
		Local _dday:String[]=_day.split(",")
		Local _ddesc:String[]=_desc.split(",")
		
		If Len(_dday)<Len(_ddesc) _dday=_dday[..Len(_ddesc)]
		If Len(_dday)>Len(_ddesc) _ddesc=_ddesc[..Len(_dday)]
		
		
		ClearMap(map_dayfest)
		_fest_data=_dday
		_fest_desc=_ddesc
		For Local s:Int = 0 Until _Fest_data.length
				MapInsert(map_dayfest,_fest_data[s],_fest_desc[s])
		Next

	End Method
	

	Method SetEnabled(sta:Int = True)

		Local k:TGadget
		
		If sta = False
			DisableGadget panel
			DisableGadget year_dec
			DisableGadget year_inc
			DisableGadget year
			DisableGadget month_dec
			DisableGadget month_inc
			DisableGadget month
			
			For k = EachIn days
				DisableGadget k
			Next
			
			For k = EachIn label
				DisableGadget k
			Next
		Else
			EnableGadget panel
			EnableGadget year_dec
			EnableGadget year_inc
			EnableGadget year
			EnableGadget month_dec
			EnableGadget month_inc
			EnableGadget month
			
			For k = EachIn days
				EnableGadget k
			Next
			For k = EachIn label
				EnableGadget k
			Next


		End If
		
	End Method
	
	Method Close()
		EmitEvent CreateEvent(EVENT_GADGETCLOSE,Self,current_year,0,current_day,current_month,data)
		end Method
	
	Method Free()
		RemoveHook EmitEventHook,EventHandler,Self
		If panel FreeGadget panel
	
		If year_dec FreeGadget year_dec
		If year_inc FreeGadget year_inc
		If year	FreeGadget year
		If month_dec FreeGadget month_dec
		If month_inc FreeGadget month_inc
		If month FreeGadget month
		
		Local gg:TGadget
		
		For Local kd:tgadget=EachIn lbl_Day
			FreeGadget kd
		Next
		lbl_day=Null
		
		If days
			For gg = EachIn days
				FreeGadget gg
				gg = Null
			Next
			days = Null
		End If
		
		If label
			For gg = EachIn label
				FreeGadget gg
				gg = Null
			Next
			label = Null
		End If
		
		_app_desc=Null
		_app_data=Null
		_fest_desc=Null
		_fest_Data=Null
		ClearMap map_dayfest
		ClearMap map_appointment
		map_dayfest=Null
		map_appointment=Null
	
		panel = Null
		Super.Free
	End Method

	Method SetColor(red:Int,green:Int,blue:Int)
		SetPanelColor panel,red,green,blue
	End Method

	
	Function Create:TCalendar(x:Int,y:Int,w:Int,h:Int,group:TGadget,style:Int = 0)
		Local sp:TCalendar = New TCalendar
		sp.panel = 		CreatePanel(x,y,w,h+(style&CAL_TODAY=True)*25,group,PANEL_BORDER)
		
		w =  sp.panel.ClientWidth()
		h =  sp.panel.ClientHeight()'-(style&TODAY=True)*25
		
		HideGadget sp.panel
		SetPanelColor sp.panel, 255, 255, 255
		
		sp.year_dec = 	CreateButton("<",0,0,20,20,sp.panel )
		SetGadgetLayout sp.year_dec, EDGE_ALIGNED, EDGE_CENTERED, EDGE_ALIGNED, EDGE_CENTERED
		
		sp.year_inc = 	CreateButton(">",w-20,0,20,20,sp.panel )
		SetGadgetLayout sp.year_inc, EDGE_CENTERED, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_CENTERED
		
		sp.year	 = 	CreateLabel ("year",20,0,W-40,20,sp.panel,LABEL_CENTER )
		SetGadgetLayout sp.year, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_CENTERED
		
		sp.month_dec = 	CreateButton("<",0,20,20,20,sp.panel )
		SetGadgetLayout sp.month_dec, EDGE_ALIGNED, EDGE_CENTERED, EDGE_ALIGNED, EDGE_CENTERED
		
		sp.month_inc = 	CreateButton(">",w-20,20,20,20,sp.panel )
		SetGadgetLayout sp.month_inc, EDGE_CENTERED, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_CENTERED
		
		sp.month = 		CreateLabel ("month",20,20,W-40,20,sp.panel,LABEL_CENTER )
		SetGadgetLayout sp.month, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_CENTERED
	
		SetGadgetLayout CreateLabel("",0,40,W,2,sp.panel,LABEL_SEPARATOR), EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_CENTERED
	
		Local XX:Int,YY:Int = 62
		Local nam$,k:Int,lbl:TGadget,counter:Int
		
		sp._keepHidden = style&CAL_HIDDEN 'Dont' show the selected day...
		
		If style&CAL_TODAY And Not (style&CAL_PICKER)
			sp.today=CreateButton("Today",0,h-22,w-1,22,sp.panel)
		ElseIf (style&CAL_TODAY) And (style&CAL_PICKER)
			sp.today=CreateButton("Today",0,h-22,w/2-1,22,sp.panel)
			sp.pickday=CreateButton("Pick date",w/2+1,h-22,w/2-1,22,sp.panel)

		End If	
		sp.style=Style
		
		sp.day_wide = w/7
		sp.max_wide = w-sp.day_wide
		
		Local dyear:Int,dmonth:Int,dday:Int,ddate:String
		
		
		Local dinfo:String[]
		dinfo=GetCurrentDate(CurrentDate())
		'with my CurrentDate() hack everything will be faster...
		'so at the moment I need to convert the months in an index...
	
		sp.current_day = 		Int(dinfo[0])
		sp.current_month = 	Int(dinfo[1])
		sp.current_year = 	Int(dinfo[2])
		
		SetGadgetText sp.year,dinfo[2]
		SetGadgetText sp.month,month_name[Int(dinfo[1])-1]		
		Local tmpOffset:Int =  (w-(sp.day_wide*7))/2		
		sp.day_panel =  CreatePanel( tmpOffset, yy-20, w-(tmpOffset*2), sp.day_height*7, sp.panel )
		SetGadgetLayout sp.day_panel, EDGE_ALIGNED, EDGE_RELATIVE, EDGE_ALIGNED, EDGE_ALIGNED
		
		For counter = 0 Until 7
			lbl = CreateLabel(day_name[counter],xx,0,sp.day_wide,18,sp.day_panel,LABEL_CENTER)
			SetGadgetLayout lbl,EDGE_RELATIVE,EDGE_RELATIVE,EDGE_ALIGNED,EDGE_CENTERED
			xx:+sp.day_wide
			If counter = 6 SetGadgetColor lbl,128,128,128
			If STYLE&CAL_WEEKEND And counter=5 SetGadgetColor lbl,168,168,168
			sp.label[counter] = lbl
	
		Next
		
		Local julian_time:Int = Conv_Date2Julian(ddate)
		Local day_week:Int = DayOfTheWeek(julian_time) ' 0 : Sunday, 1 : Monday...
		
		For Local i:Int =  0 Until 49
			sp.days[i] =  CreateButton("",(i Mod 7) * sp.day_wide,20+(i/7)*sp.day_height,sp.day_wide,sp.day_height,sp.day_panel)
			SetGadgetLayout sp.days[i],EDGE_RELATIVE,EDGE_RELATIVE,EDGE_RELATIVE,EDGE_RELATIVE
		Next
		
		sp.DrawCalendar(-1,dinfo) ' it reports the CURRENT DATA
		AddHook EmitEventHook,EventHandler,sp
		sp.data = ddate
		sp.SetProxy sp.panel
		
		'add festivity
		'change the global array for your purposes
		
		For Local s:Int = 0 Until sp._Fest_data.length
				MapInsert(sp.map_dayfest,sp._fest_data[s],sp._fest_desc[s])
		Next
		
		ShowGadget sp.panel
RedrawGadget sp

		Return sp
	End Function
	
	Method SetToday()
		Local dinfo:String[]
		dinfo=GetCurrentDate(CurrentDate())
		current_day = 	Int(dinfo[0])
		current_month = Int(dinfo[1])
		current_year = 	Int(dinfo[2])
		SetGadgetText year,dinfo[2]
		SetGadgetText month,month_name[Int(dinfo[1])-1]
		RedrawGadget Self
	End Method
	
	Function GetCurrentDate:String[](ddate:String)
		Local dinfo:String[3]
		ddate = ddate.ToLower()
		dinfo = ddate.split(" ")
		Local mcounter:Int = 0
		For Local ch$ = EachIn month_name$
			mcounter:+1
			If dinfo[1][..3] = ch.ToLower()[..3] Then dinfo[1] = String(mcounter)'for linux compatibility
		Next
			
		ddate = dinfo[0]+" "+dinfo[1]+" "+dinfo[2]
		Return dinfo
	End Function
	
	Method DrawCalendar(day_offset:Int = 0,info:String[])
		Local yy:Int = 62,nam$,k:Int,counter:Int
		Local max_days:Int = month_days[Int(info[1])-1]
		If AnnoBisesto(Int(info[2])) = 1 And Int(info[1]) = 2 Then max_days = 29
		
		Local festday$[],_array_des$[],_tool$
				
		For Local k:Int =  0 Until days.length
			If k>=day_offset And k<(max_days+day_offset) Then
					
				nam = String((k-day_offset)+1)
				SetGadgetText days[k], nam
				
				_tool = ""
			
			
				
				SetGadgetColor days[k],200,200,200
				SetGadgetColor days[k],0,0,0,False
			
				If counter = 6 SetGadgetColor days[k],128,128,128
				If (STYLE&CAL_WEEKEND)=CAL_WEEKEND And counter =5 SetGadgetColor days[k],168,168,168				
				If Int(GadgetText(days[k]))  = Int(info[0]) And _keephidden = 0 
					SetGadgetColor days[k],125,125,230
					SetGadgetColor days[k],255,255,255,False
				End If
					'check for fest day
								
				For Local fd:String = EachIn MapKeys(map_dayfest)
					festday = fd.split("-")
					
					If Int(GadgetText(days[k])) = Int(festday[0]) And Int(info[1]) = Int(festday[1]) 	
							SetGadgetColor days[k],128,128,128
							SetGadgetToolTip days[k],String(MapValueForKey(map_dayfest,fd))
					End If
					
				Next
		
					
				'check for appointment day 
				
				For Local fd:String = EachIn MapKeys(map_appointment)
					festday = fd.split("-")
				
					If festday.length = 2
	
						If Int(GadgetText(days[k])) = Int(festday[0]) And Int(info[1]) = Int(festday[1])
							SetGadgetColor days[k],0,200,20
							_array_des = String[](MapValueForKey(map_appointment,fd))
							Local coco:Int=0
							For Local sas$ = EachIn _array_des
								If sas<>"" 
									If _array_des.length>1 'more than 1 appointment
										_tool = _tool+"("+String(coco+1)+") "+sas+"~n";coco:+1
									Else
										_tool = _tool+sas+"~n"
									End If
								End If
							Next
							_tool = _tool[..Len(_tool)-1]
							SetGadgetToolTip days[k],_tool
						End If
					ElseIf festday.length = 3
						'Print "This is a FIXED appointment DD-MM-YYYY"
						If Int(GadgetText(days[k])) = Int(festday[0]) And  Int(info[1]) = Int(festday[1]) And Int(info[2]) = Int(festday[2])
							SetGadgetColor days[k],0,200,20
							_array_des = String[](MapValueForKey(map_appointment,fd))
							Local coco:Int=0
							For Local sas$ = EachIn _array_des

							If sas<>"" 
								If _Array_Des.length>1
									_tool =_tool+"("+String(coco+1)+") "+sas+"~n";coco:+1
								Else
									_tool = _tool+sas+"~n"

								End If
							End If
							Next
							_tool = _tool[..Len(_tool)-1]
							SetGadgetToolTip days[k],_tool
						End If
	
	
					End If
				
						
				Next
				
				If GadgetHidden(days[k]) Then ShowGadget(days[k])
				
			Else
				If Not GadgetHidden(days[k]) Then HideGadget(days[k])
			EndIf
			counter=(counter+1) Mod 7

		Next
	End Method
	
	Method GetAppointment$[](_Dat$ = "",index:Int=-1)
		If _dat = "" Return [""]
		
		If index=-1
			Return String[](MapValueForKey(map_appointment,_dat))
		Else
			Local _array_des$[]=String[](MapValueForKey(map_appointment,_dat))
			If index>=0 And index<_array_des.length
				Return [_array_des[index]]
			End If
		End If
	End Method
	
	Method SetAppointment(_dat$ = "",_des$ = "")
		If _dat = "" Or _des = "" Return
		'check if any appointments is already present and add it to a string array
		Local _array_des$[]
		_array_des = String[](MapValueForKey(map_appointment,_dat))
		_array_des = _array_des[.._array_des.length+1]
		_array_des[_array_des.length-1] = _des

		MapInsert(map_appointment,_dat,_array_des)
	End Method
	
	Method Activate(command:Int)
		SetDate()
	End Method

	Method SetDate()
		If current_month>12 current_month = 1
		If current_month<1 current_month = 12
		Local newdate$ = Right("00"+String(current_day),2)+" "+Right("00"+String(current_month),2)+" "+String(current_year)
		SetGadgetText year,String(current_year)
		SetGadgetText month,month_name[current_month-1]
		
		Local xx:Int,dinfo:String[] = [String(current_Day),String(current_month),String(current_year)]
	
		Local julian_time:Int = Conv_Date2Julian("01 "+dinfo[1]+" "+dinfo[2])'the first day of the month
		Local day_week:Int = DayOfTheWeek(julian_time)
		
		DrawCalendar(day_week,dinfo)
		data = newdate
	End Method
	
	
	Function eventHandler:Object( pID%, pData:Object, pContext:Object )
		Local pEvent:TEvent =  TEvent(pData)
		Local obj:TCalendar = TCalendar(pContext)
		
		If pEvent
		
				
			Select pEvent.Source
				Case obj.year_Inc
				'	Print "Anno +"
					obj.current_year:+1
					obj.SetDate()
					pevent.extra = obj.data
					pevent.source = obj
					Return pevent

						
				Case obj.year_dec
				'	Print "Anno -"
					obj.current_year:-1
					obj.SetDate()
					pevent.extra = obj.data
					pevent.source = obj
					Return pevent

				Case obj.month_Inc
				'	Print "Mese +"
					obj.current_month:+1
					obj.SetDate()
					pevent.extra = obj.data
					pevent.source = obj					
					Return pevent

				Case obj.month_dec
					'Print "Mese -"
					obj.current_month:-1
					obj.SetDate()
					pevent.extra = obj.data
					pevent.source = obj
					Return pevent
			
				Case obj.today
					obj.SetToday()					
					Return pevent
				Case obj.pickday
					pevent.extra=obj.data
					obj.Close()
					Return pevent

			End Select			

			For Local dgad:TGadget = EachIn obj.days
				If pevent.Source = dgad
					'Print "Pressed DAY: "+GadgetText(dgad)
					obj.current_day = Int(GadgetText(dgad))
					obj.SetDate()
					pevent.extra = obj.data
					pevent.source = obj
					Return pevent
				End If
			Next
			
			
		End If
		
		Return pData
	EndFunction

	
End Type

Function AnnoBisesto:Int(anno:Int = 0)
If anno = 0 Return 0
Local bisesto:Int = 0
	If (anno Mod 4) = 0 bisesto = 1
	If (anno Mod 100) = 0
		If (anno Mod 400) = 0
			bisesto = 1
		Else
			bisesto = 0
		End If
	End If
	Return bisesto
End Function

Function Conv_Date2Julian:Int(_data$ = "")
	If _data$ = "" Return 0

	Local year:Int,month:Int,day:Int
	Local _din$[] = _Data.split(" ")
	year = Int(_din[2])
	month = Int(_din[1])
	day = Int(_din[0])
	
	Local JulianDate:Int
	' conversion taken from a website (lost link)
	JulianDate =  367 * Year - ((7 * (Year + 5001 + ((Month - 9) / 7))) / 4) + ((275 * Month) / 9) + Day + 1729777
	
	Return JulianDate
End Function

Function DayOfTheWeek:Int(pjulian:Int)
	Local Jt:Float = Float(pjulian)+1.5
	Return (jt Mod 7)
End Function
