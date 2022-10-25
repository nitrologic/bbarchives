; ID: 2404
; Author: JoshK
; Date: 2009-02-01 23:10:23
; Title: Disable aeroglass theme in Windows Vista
; Description: Disable aeroglass theme in Windows Vista

SuperStrict

Import pub.win32

Private

Extern "win32"
	Function FreeLibrary:Int(hlib:Int)
EndExtern

Global DWMlibrary:Int

AeroSetEnable(False)
Delay 10000
If DWMlibrary FreeLibrary(DWMlibrary)

Public

Function AeroSetEnable(enable:Int) 
	Const DWM_EC_DISABLECOMPOSITION:Int=0
	Const DWM_EC_ENABLECOMPOSITION:Int=1
	Local DwmEnableComposition:Int(mode:Int)
	
	If Not DWMlibrary DWMlibrary=LoadLibraryA("DWMAPI.dll") 
	If DWMlibrary
		DwmEnableComposition=getprocaddress(DWMlibrary,"DwmEnableComposition")
		If DwmEnableComposition
			If enable
				DwmEnableComposition(DWM_EC_ENABLECOMPOSITION) 
			Else
				DwmEnableComposition(DWM_EC_DISABLECOMPOSITION) 
			EndIf
		EndIf
	EndIf
EndFunction
