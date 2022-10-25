; ID: 3208
; Author: Dan
; Date: 2015-05-27 05:51:12
; Title: Allow only Single instance, blitz+ blitz3d
; Description: Run only 1 instance of a Program, using Mutex - B+,B3D

;kernel32.decls:

;.lib "kernel32.dll"
; CreateMutex%(lpMutexAttributes%,bInitialOwner%,lpName$):"CreateMutexA"
; api_GetLastError% () : "GetLastError"
; api_ReleaseMutex%(Handle%):"ReleaseMutex"
;
; Note: My system allready has api_CreateMutex% (lpMutexAttributes*, bInitialOwner%, lpName$) : "CreateMutexA" in Kernel32.decls but it crashes the app. The above Function works.

	Const ERROR_ALREADY_EXISTS=183
	hMutex=CreateMutex(0,1,"Change this for different programs") ; Mutex accessible to any program, change it for other programs 
	If api_GetLastError() = ERROR_ALREADY_EXISTS Then
	
	    Print "Sorry allready running!" ; Remove this line, it is only for this demo
	    Delay 5000                      ; Remove this line, it is only for this demo
	
		End ; End the app
	EndIf
	
	Print " Working ! "
	Print "Start me again to check if this demo is working correctly"
	Print "Remember to''Change this for different programs'' text "
	Print " so your apps have an unique mutex assigned"
	Repeat 
	Until KeyDown(1)
;At the end of your program release the Mutex with: 	
	api_ReleaseMutex(hMutex)
	
	End

;**********************************************************

;Edit 15.6.2016:    Mutex as function, simplifies the usage.

Function Mutex(MutexName$="")
;To set a mutex, enter a name
;call it again without parameters to end the duplicate start!

;Copy following lines at the beginning of your program and uncomment
;Const ERROR_ALREADY_EXISTS=183			;For mutex
;Global hMutex

;example run:
;If Mutex("My Saver C")=1 Then Mutex() ;Prevents multiple instances 
    
	If Len(MutexName$)>0
		hMutex=CreateMutex(0,1,MutexName$) ; Mutex accessible to any program, change it for other programs 
		If api_GetLastError() = ERROR_ALREADY_EXISTS Then Return 1
	Else
		api_ReleaseMutex(hMutex)
		End 						;To end or not to end ?!?
	EndIf
End Function
