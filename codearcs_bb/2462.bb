; ID: 2462
; Author: em22
; Date: 2009-04-16 08:04:30
; Title: Change Window Icon
; Description: Change Window Icon

; Example to change window icon in Blitz+
;
; by em22
;
; make sure you add the user decs to blitz at the end.

EditWindow=CreateWindow("Test Window",50, 50,392,186,0,1)

test=GetWinOSHandle(EditWindow)
SetWindowIcon(test,"c:\windows\explorer.exe")     ; this can be an ico file or an exe with an icon resource.

Repeat  ; test loop

	id=WaitEvent()

	Select id
	
		Case $803
		
		End
	
	End Select 
	
Forever

Function GetWinOSHandle(win)
	Return(QueryObject(win,1))	
End Function

Function SetWindowIcon(hWnd,ICOfile$)
	icon=ExtractIconA(hWnd,ICOfile$,0)
	SetClassLongA(hWnd,-14,icon)
End Function

;
; user decs - create a file called user.decls in C:\Program Files\BlitzPlus\userlibs, and copy the following, remove the ;
; .lib "shell32.dll"
; ShellExecuteA(hwnd%,op$,file$,params$,dir$,showcmd%)
; ExtractIconA%(hWnd%,File$,Index%):"ExtractIconA"
;
;
; .lib "user32.dll"
; SetClassLongA%(hWnd%,nIndex%,Value%):"SetClassLongA"
;
;
