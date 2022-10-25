; ID: 3213
; Author: Dan
; Date: 2015-07-06 16:45:21
; Title: 3 functions Timer_Sec Timer_mSec and Screen
; Description: Timer Countdown/Up/Reset in m/Seconds, Screen = Graphic

;====================================================================
; Project: MiliSeconds + Seconds Timer, Screen
; Version: 0.0
; Author: Dan 
; Email: -.-
; Copyright: PD
; Description: Timer function returns seconds passed since last function reset
;              parameter to count up needs to be passed as - eg. -1
;              Can be used as countdown if parameter is higer than 0
;              it continues below 0 !
;
;              Screen = Graphic call function  
;              Opens a window in specified resolution and maximizes
;              it to the desktop resolution, usefull for small progs
;              or testing functions
;              uncomment below code for demo 
;        (Decls needed for the Screen are at the end of the function)
;====================================================================

;Screen(320,250)
;
;Global Timer_s=MilliSecs() ;<---Put at the start of your program---
;Global Timer_m=MilliSecs() ;<---Put at the start of your program---
;
;Repeat
;
;Cls
;
;blink=Time_mSec(-1) Mod 80
;
;If blink>0 And blink <40
;   Color $0,$25,$ff
;   Rect 0,100,160,15,1
;Else
;  Color $ff,$0,$ff
;  Rect 0,114,160,15,1
;EndIf
;
;Color $ff,$ff,$ff
;Text 1,1,"Left Mousebutton resets Timer_Sec"
;Text 1,140,"Right Mousebutton resets Timer_mSec"
;Text 1,28,Time_Sec(25)+" = Time_Sec(25)"
;Text 1,44,Time_Sec(-1)+" = Time_Sec(-1)"
;Text 1,70,"This Program ends in 0:"+Time_Sec(50)
;Text 1,100,Time_mSec(800)+" = Time_mSec(800)"
;Text 1,114,Time_mSec(-1)+  " = Time_mSec(-1)"
;If Time_Sec(50)=<0 Then End
;
;If MouseDown(1)
;Time_Sec(0)
;EndIf
;
;If MouseDown(2)
;Time_mSec(0)
;EndIf
;
;Delay 1
;Until KeyDown (1)
 
Function Screen(x,y)
    DeskX=api_GetSystemMetrics(0)
	DeskY=api_GetSystemMetrics(1)
	If x>DeskX Then x=DeskX
    If x<64 Then x=64
	If y>DeskY Then y=DeskY
    If y<64 Then y=64
    bits=api_GetDeviceCaps(api_GetDC( api_GetDesktopWindow()),12)
	Graphics x,y,bits,2
	Graphics x,y,bits,3
	api_MoveWindow(api_GetActiveWindow(),0,0,DeskX,DeskY,True)

; User32.decls	
;;.lib "user32.dll"
;api_GetSystemMetrics% (nIndex%) : "GetSystemMetrics"
;api_GetActiveWindow%():"GetActiveWindow"
;api_GetDC% (hwnd%) : "GetDC"
;api_GetDesktopWindow% () : "GetDesktopWindow"
;api_MoveWindow% (hwnd%, x%, y%, nWidth%, nHeight%, bRepaint%) : "MoveWindow"
;
; GDI32.decls
;.lib "gdi32.dll"
;api_GetDeviceCaps% (hdc%, nIndex%) : "GetDeviceCaps"

End Function

Function Time_Sec(x)
;Global Timer_s=MilliSecs() ;<---Put at the start of your program---
; x can be -1<,0 or >0 
;- numbers returns seconds passed since last function call with 0 
;0 resets the timer
;above 0 sets a countdown timer in seconds
	If x>0
		Return x-Int((MilliSecs()-Timer_s)*0.001)
	ElseIf x=0
		Timer_s=MilliSecs()
    Else
		Return Int((MilliSecs()-Timer_s)*0.001)
	EndIf
End Function

Function Time_mSec(x)
;Global Timer_m=MilliSecs() ;<---Put at the start of your program---
; x can be -1<,0 or >0 
;- numbers returns miliseconds passed since last function call with 0
;0 resets the timer
;above 0 sets a countdown timer in seconds
	
	If x>0
	    y1=MilliSecs()-Timer_m
		If Len(y1)>2
			y2=Left$(y1,Len(y1)-2)
			Return x-Int(y2)
		Else
			Return x
		EndIf
	ElseIf x=0
		Timer_m=MilliSecs()
    Else
	    y1=Left$(MilliSecs(),7)
		y2=Left$(Timer_m,7)
		Return Int(y1)-Int(y2)
	EndIf
End Function
