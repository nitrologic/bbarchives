; ID: 1666
; Author: Grey Alien
; Date: 2006-04-14 08:21:39
; Title: Icon in Top Left corner of Window
; Description: Icon in Top Left

Strict
Extern "win32"
	Function FindWindowA:Int(nullstring%,WindowText$z)
	Function ExtractIconA:Int(hWnd%,File$z,Index%)
	Function SetClassLongA:Int(hWnd%,nIndex%,Value%)
End Extern

AppTitle = "test"
Graphics 640,480,0
'Graphics 640,480,32

Local handle:Int = FindWindowA(0,"test")
ccSetIcon(StripExt(ccAppFileNonDebug())+".ico",handle)

Repeat
	Cls
	Flip
Until KeyDown(Key_Escape)

Function ccSetIcon(iconname$, TheWindow%)	
	Local icon=ExtractIconA(TheWindow,iconname,0)
	SetClassLongA(TheWindow,-14,icon)
End Function

Function ccAppFileNonDebug$()
	'Returns AppFile but without the .debug on the end when running in debug mode.
	If Mid(AppFile,Len(AppFile)-8, 5) = "debug" Then
		Return Mid(AppFile,1,Len(AppFile)-10)+".exe" 'chops off .debug.exe (10 chars)
	Else
		Return AppFile
	EndIf
End Function
