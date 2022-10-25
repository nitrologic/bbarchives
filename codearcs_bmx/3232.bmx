; ID: 3232
; Author: Kryzon
; Date: 2015-11-20 15:19:01
; Title: Simple work break timer
; Description: A lightweight timer utility for taking breaks from work, includes intervals like 30 minutes, 1 hour etc.

'WORK BREAK TIMER UTILITY.
'Nov. 2015.

SuperStrict

Framework maxgui.win32maxguiex
Import brl.eventqueue


GCSetMode( 2 ) 'MANUAL GC MODE.


'Import some Win API functions.

Extern 

	Function SetTimer:Int( hWND:Int, nIDEvent:Int, elapse:Int, timerProc( hWND:Int, uMSG:Int, idEvent:Int, dwTime:Int ) ) "win32"
	Function KillTimer:Int( hWND:Int, uIDEvent:Int ) "win32"
	
	Function FlashWindowEx:Int( flashInfoStructure:Byte Ptr ) "win32"
	
	Function GetModuleFileNameA:Int( hModule:Int, name:Byte Ptr, nSize:Int ) "win32"

End Extern


'Windows taskbar flash FLASHINFO structure.

Type FLASHINFO

	Rem  
	  UINT  cbSize;
	  HWND  hwnd;
	  DWORD dwFlags;
	  UINT  uCount;
	  DWORD dwTimeout;
	EndRem

	Field cbSize:Int
	Field hWND:Int
	Field dwFlags:Int
	Field uCount:Int
	Field dwTimeout:Int

End Type



Global minutesToWait:Int = 0
Global timerID:Int = 0


'Main window.

Const width:Int = 270
Global mainWindow:TGadget = CreateWindow( "(OFF) Break Timer", 0, 0, width, 160, Null, ..
WINDOW_TITLEBAR | WINDOW_RESIZABLE | WINDOW_STATUS | WINDOW_CENTER )

	Global minutesCombo:TGadget = CreateComboBox( 20, 10, 150, 32, mainWindow )
	AddGadgetItem( minutesCombo, "20 minutes", 0, -1, "", "20" )
	AddGadgetItem( minutesCombo, "30 minutes", 0, -1, "", "30" )
	AddGadgetItem( minutesCombo, "40 minutes", GADGETITEM_DEFAULT, -1, "", "40" )
	AddGadgetItem( minutesCombo, "1 hour", 0, -1, "", "60" )
	AddGadgetItem( minutesCombo, "1 hour 30 minutes", 0, -1, "", "90" )
	AddGadgetItem( minutesCombo, "2 hours", 0, -1, "", "120" )
	
	Global startButton:TGadget = CreateButton( "Start", 20, 60, 80, 32, mainWindow )
	Local exitButton:TGadget = CreateButton( "Exit", 120, 60, 80, 32, mainWindow )


'Taskbar flash settings.

Global fInfo:FLASHINFO = New FLASHINFO
fInfo.cbSize = 4 * 5
fInfo.hWND = QueryGadget( mainWindow, QUERY_HWND )
fInfo.dwFlags = $2 | $C 'FLASHW_TRAY | FLASHW_TIMERNOFG
fInfo.uCount = 0 'Flashes forever, until the window is clicked to foreground.
fInfo.dwTimeout = 0 'Default flashing rate (milliseconds).


'See if there's any default user-minutes in the executable name.

parseCommandLine()
GCCollect()


Repeat
	WaitEvent()
	
	Select EventID()
		
		Case EVENT_APPTERMINATE
			quit()
			
		Case EVENT_WINDOWCLOSE
			If EventSource() = mainWindow Then quit()
									
		Case EVENT_GADGETACTION
		
			Select EventSource()
			
				Case exitButton
					quit()
					
				Case startButton
					toggleWinTimer()
		
			End Select
								
	End Select

Forever


Function quit()
	
	_stopWinTimer()
	GCCollect() '_stopWinTimer() already does this, but anyway.
	
	End

End Function 


Function parseCommandLine()

	'The first argument is always the path to the executable file.
	'If there's more than one argument, test if the second argument is a number.

	If AppArgs.length > 1 Then
		
		Local tempMinutes:Int = Int( AppArgs[1] )
		If Chr( AppArgs[1][0] ) = "-" Then tempMinutes = Int( AppArgs[1][ 1 .. ] ) 'Also allow values preceded by "-".
		
		Const MAX_MINUTES:Int = 999999
		If tempMinutes > 0 And tempMinutes <= MAX_MINUTES Then
			AddGadgetItem( minutesCombo, String( tempMinutes ) + " minutes (default)", GADGETITEM_DEFAULT, -1, "", String( tempMinutes ) )
			SelectGadgetItem( minutesCombo, CountGadgetItems( minutesCombo ) - 1 )		
		EndIf
	EndIf
		
End Function


Function getMinutesToWait()
	
	minutesToWait = Int( String( GadgetItemExtra( minutesCombo, SelectedGadgetItem( minutesCombo ) ) ) )
	
End Function


Function toggleWinTimer()

	If timerID Then
		_stopWinTimer()
	Else
		_startWinTimer()
	EndIf

End Function


Function _startWinTimer()

	_stopWinTimer() 'Make sure it's off.

	getMinutesToWait() 'Get the user choice.
	
	SetGadgetText( mainWindow, "(ON) Break Timer" )
	Local statusText:String	= "Start: " + CurrentTime()
	SetStatusText( mainWindow, statusText )
	MinimizeWindow( mainWindow )
	
	SetGadgetText( startButton, "Stop" )

	timerID = SetTimer( 0, 0, minutesToWait * 60 * 1000, _processWinTimer )

End Function


Function _processWinTimer( hWND:Int, uMSG:Int, idEvent:Int, dwTime:Int )

	'The timer has ticked.
	'Windows timers created with 'SetTimer' are periodic, so we destroy it in this first tick.

	FlashWindowEx( fInfo )
	
	_stopWinTimer()

End Function


Function _stopWinTimer()
	
	SetGadgetText( mainWindow, "(OFF) Break Timer" )

	If timerID <> 0 Then
		Local statusText:String	= WindowStatusText( mainWindow )
		SetStatusText( mainWindow, statusText[ .. 15 ] + "~t End: " + CurrentTime() )

		SetGadgetText( startButton, "Restart" )

		KillTimer( 0, timerID )
		timerID = 0
	EndIf

	GCCollect()

End Function
