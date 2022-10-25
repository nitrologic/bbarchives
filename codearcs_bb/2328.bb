; ID: 2328
; Author: Xzider
; Date: 2008-10-03 22:15:19
; Title: HideWindow DLL
; Description: Hides a designated window

;Can change Title$ to whatever you want to hide.

AppTitle "HideWindow Example"

		Title$ = "HideWindow Example"
		
		Result% = HideWindow(Title$)

  If Result%

		Print Title$ + " hidden!"
		
  Else

		Print "Failed to hide " + Title$
		
  End If


		Delay 2000
		
		Print
		
		Result% = ShowWindow(Title$)

  If Result%

		Print Title$ + " shown!"
		
  Else

		Print "Failed to show " + Title$
		
  End If


		Print
		Print "Press any key to quit."
		Print
		
		WaitKey
		End
