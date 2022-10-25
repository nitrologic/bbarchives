; ID: 2524
; Author: Ked
; Date: 2009-07-09 23:16:12
; Title: FileLastModified()
; Description: A function I could never find, so I just posted a modified version here.

Function FileLastModified:String[](path:String)
	Local retval:String[2]
	
	Local time:Int=FileTime(path)
	Local o:Int Ptr=Int Ptr(localtime_(Varptr(time)))
	
	Local second:Int=o[0]
	Local minute:Int=o[1]
	Local hour:Int=o[2]
	Local day:Int=o[3]
	Local month:Int=(o[4]+1)
	Local year:Int=(o[5]+1900)
	
	Local pm:String
	If hour>12
		hour=(hour-12)
		pm="PM"
	Else
		pm="AM"
	EndIf
	If hour=12
		pm="PM"
	EndIf
	If hour=0
		hour=12
		pm="AM"
	EndIf
	
	Local mi:String
	If minute<10
		mi="0"+String(minute)
	Else
		mi=String(minute)
	EndIf
	
	Local se:String
	If second<10
		se="0"+String(second)
	Else
		se=String(second)
	EndIf
	
	retval:String[0]=String(hour)+":"+mi+":"+se+" "+pm
	
	Local mon:String
	Select month
		Case 1 ; mon="January"
		Case 2 ; mon="February"
		Case 3 ; mon="March"
		Case 4 ; mon="April"
		Case 5 ; mon="May"
		Case 6 ; mon="June"
		Case 7 ; mon="July"
		Case 8 ; mon="August"
		Case 9 ; mon="September"
		Case 10; mon="October"
		Case 11; mon="November"
		Case 12; mon="December"
	EndSelect
	
	retval:String[1]=mon+" "+day+", "+year
	
	Return retval
EndFunction
