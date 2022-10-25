; ID: 3063
; Author: Henri
; Date: 2013-07-01 15:45:08
; Title: DateTimePicker
; Description: DateTime picker control for Win32

Strict

Import maxgui.drivers

Extern "win32"
	Function GetModuleHandle:Int (lpmodulehandlename:Byte Ptr)="GetModuleHandleW@4"
	Function CreateWindowEx:Int (dwExstyle:Int,lpclassname:Byte Ptr,lpwindowname:Byte Ptr,dwStyle:Int,x:Int,y:Int,..
								w:Int,h:Int,hwndparent:Int,hmenu:Int,hinstance:Int,lpparam:Int)="CreateWindowExA@48"
	Function GetDateFormatA:Int(lpLocaleName:Int, dwFlags:Int,lpDate:Int,lpFormat:Byte Ptr,..
								lpDateStr:Byte Ptr,cchDate:Int)
End Extern

Const DATETIMEPICK_CLASSW:String	= "SysDateTimePick32"
Const MONTHCAL_CLASSW:String		= "SysMonthCal32"
Const DTM_GETSYSTEMTIME:Int			= $1001
Const DTM_SETSYSTEMTIME:Int			= $1002
Const DTM_SETFORMAT:Int				= $1005


'EXAMPLE
'------------------------------------------------------------------------------------
Local win:tgadget = CreateWindow("test",0,0,300,250,Null,WINDOW_TITLEBAR|WINDOW_CENTER)
Local dtp:Tdtpicker = Tdtpicker.Create(20,20,90,23,win,"dd MM yyyy") 'Format is optional
Local button:tgadget = CreateButton("Get date",20,80,70,30,win)
Repeat
	WaitEvent()
	Select EventID()
	Case EVENT_WINDOWCLOSE		End
	Case EVENT_GADGETACTION		Notify(dtp.Getdate())
	EndSelect
Forever
'------------------------------------------------------------------------------------


Type Tdtpicker
	Field hwnd:Int
	Field edit_hwnd:Int
	Field p:tgadget
	Field format:String=""
	
	Function Create:Tdtpicker(x:Int, y:Int, width:Int, height:Int, group:tgadget, format:String="")
		Local d:Tdtpicker = New Tdtpicker
		d.p = CreatePanel(x,y,width,height,group)
		d.hwnd = QueryGadget(d.p,QUERY_HWND)
		Local wstyle:Int = WS_CHILD|WS_VISIBLE|WS_BORDER|WS_TABSTOP
		d.edit_hwnd = CreateWindowEx(0,DATETIMEPICK_CLASSW,"Date",wstyle,..
							0,0,width,height,d.hwnd,Null,GetModuleHandle(Null),Null)
		If format<>""
			d.format = format
			Local hformat:Byte Ptr = format.ToCString()
			SendMessageW(d.edit_hwnd, DTM_SETFORMAT, 0, Int(hformat))
			MemFree hformat
		EndIf
		
		Return d
	End Function
	
	Method activate()
		SetFocus(edit_hwnd)
	EndMethod
	
	Method GetDate:String()
		Local buffer:Byte[20]
		Local st:_SYSTEMTIME = New _SYSTEMTIME
		SendMessageW(edit_hwnd, DTM_GETSYSTEMTIME, 0, Int(Byte Ptr st))
		If format<>""
			Local hformat:Byte Ptr = format.ToCString()
			GetDateFormatA(0,Null,Int(Byte Ptr st),hformat,buffer,20)
			MemFree hformat
		Else
			GetDateFormatA(0,Null,Int(Byte Ptr st),Null,buffer,20)
		EndIf
		Local s:String
		For Local bb:Byte = EachIn buffer
			If bb>0 Then s:+ Chr(bb)
		Next
		Return s
	EndMethod
EndType

Type  _SYSTEMTIME
    Field wYear:Int
    Field wMonth:Int
    Field wDayOfWeek:Int
    Field wDay:Int
    Field wHour:Int
    Field wMinute:Int
    Field wSecond:Int
    Field wMilliseconds:Int
EndType
