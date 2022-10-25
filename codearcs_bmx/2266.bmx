; ID: 2266
; Author: JoshK
; Date: 2008-06-11 09:46:27
; Title: Time and Date
; Description: Convert a time stamp to a date structure, and get online file dates

Strict

Extern "win32"
	Function gmtime:Byte Ptr(time:Int Ptr)
EndExtern

Print FileDate("http::blitzmax.com/Home/_index_.php").ToString()
Print FileDate("test.exe",0).ToString()
Print FileDate("test.exe",1).ToString()

Function FileDate:TDate(path$,GMT=0)
	Select ExtractProtocol(path).tolower()
		Case "http"
			path=StripProtocol(path)
			
			Local i=path.Find( "/",0 ),server$,file$
			If i<>-1
				server=path[..i]
				file=path[i..]
			Else
				server=path
				file="/"
			EndIf
			
			Local stream:TStream=TSocketStream.CreateClient( server,80 )
			If Not stream Return
			
			stream.WriteLine "HEAD " + file + " HTTP/1.1"
			stream.WriteLine "Host: " + server
			stream.WriteLine "Connection: close"
			stream.WriteLine ""
			
			Local date:TDate,key$
			
			Local s$,sarr$[]
			While Not Eof(stream)
				s=stream.ReadLine()
				'Print s
				sarr=s.split(":")
				If sarr.length>1
					key=sarr[0].Trim()
					If key="Last-Modified" Or key="Date"
						'Print s
						For Local n=2 To sarr.length-1
							sarr[1]:+":"+sarr[n]
						Next
						sarr=sarr[1].split(",")
						If sarr.length>1
							date=New TDate
							sarr=sarr[1].split("")
							If sarr.length>0 date.day=Int(sarr[0])
							If sarr.length>1
								Select sarr[1].tolower()
									Case "jan" date.month=1
									Case "feb" date.month=2
									Case "mar" date.month=3
									Case "apr" date.month=4									
									Case "may" date.month=5
									Case "jun" date.month=6
									Case "jul" date.month=7
									Case "aug" date.month=8
									Case "sep" date.month=9
									Case "oct" date.month=10
									Case "nov" date.month=11
									Case "dec" date.month=12
									Default
										stream.Close()
										Return
								EndSelect
							EndIf
							If sarr.length>2 date.year=Int(sarr[2])
							If sarr.length>3
								sarr=sarr[3].split(":")
								If sarr.length>0 date.hour=Int(sarr[0])
								If sarr.length>1 date.minute=Int(sarr[1])
								If sarr.length>2 date.second=Int(sarr[2])
							EndIf
							stream.Close()
							If key="Last-Modified" Return date
						EndIf
					EndIf
				EndIf
			Wend
			stream.Close()
			Return date
			
		Default
			Local time
			time=FileTime(path)
			If time=-1 Return
			Return TDate.FromTime(time,GMT)
	EndSelect
	
	Function ExtractProtocol$(path$)
		Local sarr$[]=path.split("::")
		If sarr.length>1 Return sarr[0]
	EndFunction
	
	Function StripProtocol$(path$)
		Local sarr$[]=path.split("::")
		If sarr.length>1 Return sarr[1] Else Return path
	EndFunction
	
EndFunction

Type TDate
	
	Field second
	Field minute
	Field hour
	Field day
	Field month
	Field year
	
	Method Compare:Int(o:Object)
		Local date:TDate=TDate(o)
		If date.year>year Return 1
		If date.year<year Return -1
		If date.month>month Return 1
		If date.month<month Return -1
		If date.day>day Return 1
		If date.day<day Return -1
		If date.hour>hour Return 1
		If date.hour<hour Return -1
		If date.minute>minute Return 1
		If date.minute<minute Return -1
		If date.second>second Return 1
		If date.second<second Return -1
		Return 0
	EndMethod
	
	Function Create:TDate(second,minute,hour,day,month,year)
		Local date:TDate
		date.second=second
		date.minute=minute
		date.hour=hour
		date.day=day
		date.month=month
		date.year=year
		Return date
	EndFunction
	
	Function FromTime:TDate(time,GMT=0)
		Local tm:Int Ptr
		If GMT
			tm = Int Ptr(gmtime(Varptr(time)))
		Else
			tm = Int Ptr(localtime_(Varptr(time)))
		EndIf
		Local date:TDate=New TDate
		date.second=tm[0]
		date.minute=tm[1]
		date.hour=tm[2]
		date.day=tm[3]
		date.month=(tm[4]+1)
		date.year=(tm[5]+1900)
		Return date
	EndFunction
	
	Method ToString$()
		Local s$
		s="Second: "+second+"~n"
		s:+"Minute: "+minute+"~n"
		s:+"Hour: "+hour+"~n"
		s:+"Day: "+day+"~n"
		s:+"Month: "+month+"~n"
		s:+"Year: "+year+"~n"
		Return s
	EndMethod
	
	'I don't know how to convert this to a time stamp
	Rem
	Method ToTime:Int()
		Local time
		Local tm:Int Ptr = Int Ptr(localtime_(Varptr(time)))
		Local date:TDate=New TDate
		date.hour=tm[2]
		date.minute=tm[1]
		date.second=tm[0]
		date.day=tm[3]
		date.month=(tm[4]+1)
		date.year=(tm[5]+1900)
		Return time
	EndMethod
	EndRem
	
EndType
