; ID: 266
; Author: Kalle
; Date: 2002-03-12 14:58:47
; Title: Windows Screen Saver
; Description: Has nothing to do with writing games, but is very nice...

;*******************************************************************************
;*                                                                             *
;* Windows Screensaver                                                         *
;* (Tested with Win98, but should also work with any other 32 Bit Windows)     *
;*                                                                             *
;* Code by Kalle (Pascal Gwosdek) (2002)                                       *
;*                                                                             *
;* Just insert your code instead of the comments below.                        *
;* When finished, create an executable and change the filename into <Name>.scr *
;* Now, you can handle it as a "real" screensaver...                           *
;* The preview in the installation screen is not supported. Another bug is     * 
;* that the Blitz Text window appears at every use, ignore it.                 *
;*                                                                             *
;* Have fun :-)                                                                *
;*                                                                             *
;*******************************************************************************

;Declarations

AppTitle "ScreenSaver"
ChangeDir SystemProperty$("appdir") ;This line is important when running as a Screensaver in Windows.
;Disable it while testing in Blitz (because the "appdir" of Blitz is "\bin"...) 
If CommandLine$() = "" Or Left$(CommandLine$(),2) = "/C" Or Left$(CommandLine$(),2) = "/c" Then ConfigScreensaver
If CommandLine$() = "/S" Or CommandLine$() = "/s" Then ExecuteScreensaver
End

;---------------------------------------

Function ConfigScreensaver()
;Code for config screen... If you want to, launch an external application (written in Delphi or 
;Visual Basic). You can save your data in an additional file. 

End
End Function

;---------------------------------------

Function ExecuteScreensaver()
;Graphics mode (fullscreen), double buffering command(s) and loading of images, meshes, sounds... 
FlushKeys
FlushMouse
MoveMouse 0,0
Repeat
	;Code for running screensaver...
	
Until MouseX() <> 0 Or MouseY() <> 0 Or GetKey() <> 0 Or GetMouse() <> 0
End
End Function 
