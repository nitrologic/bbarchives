; ID: 1918
; Author: Vic 3 Babes
; Date: 2007-02-07 21:24:58
; Title: Multiple Options
; Description: A simple way to handle loads of boolean options in a program

One way of handling lots of boolean options that your program may have.  A lot better than typing loads of CreateButton and SetButtonState lines anyway.

[codebox]
Const PICSON=$1, SOUNDON=$2, MUSICON=$4, VISAIDON=$8, AUTOON=$10, BLOCKON=$20
Const BIGWINON=$40, CLEARON=$80, NINTHON=$100, TENTHON=$200
Const BITSUSED=$3F, NUMPREFS=10
Const DEFAULTPREFS=SOUNDON Or MUSICON Or BIGWINON

Const CANCELLED=0, CHANGED=1

Const ESCAPE=1

Const KEY_PRESSED=$101
Const GADGET_ACTION=$401
Const CLOSE_WINDOW=$803, ACTIVATE_WINDOW=$804

Const CLICK_BUTTON=1, CHECK_BOX=2, RADIO_BUTTON=3

Global preferences, newprefs, changedprefs, centrex, centrey, prefsloop, testbit, gadloop

Dim gadgets(9)

;we don't really need these next four lines - they just make the main loop
;a bit shorter in reporting actions - see the commented lines in the
;change_prefs function where the checkbox gadgets are created - if you uncomment
;those lines - then remove or comment these ones out.
Dim gadtext$(9)
Restore gadnames
For gadloop=0 To 9
	Read gadtext$(gadloop)
Next
;-----------------------------------------
centrex=(ClientWidth(Desktop()) Shr 1)
centrey=(ClientHeight(Desktop()) Shr 1)
main_window=CreateWindow( "Main Program",centrex-125,centrey-150,250,300,0,35)
SetMinWindowSize main_window
prefsbutton=CreateButton("Prefs",10,10,60,20,main_window)
textbox=CreateTextArea(10,40,230,250,main_window)
SetTextAreaColor textbox,$FF,$FF,$80,True

preferences=DEFAULTPREFS
Repeat
	WaitEvent()
	event=EventID()
	Select event
		Case GADGET_ACTION
			evsource=EventSource()
			If evsource=prefsbutton
				If change_prefs()
					;make changes to program
					change$=""
					changedprefs=newprefs Xor preferences
					For prefsloop=0 To NUMPREFS-1
						testbit=1 Shl prefsloop
						If changedprefs And testbit
							;this preference has changed
							change$=gadtext$(prefsloop)
							If newprefs And testbit Then change$=change$+" ON" Else change$=change$+" OFF"
							;alternative example
							;Select testbit
							;	Case PICSON			If newprefs and testbit then turn pics on else turn pics off
							;	Case MUSICON		If newprefs and testbit then turn music on else turn it off
							;	etc...
							;End Select
						EndIf
						If change$<>"" Then AddTextAreaText textbox,change$+Chr$(13)+Chr$(10): change$=""
					Next
					preferences=newprefs
				Else
					AddTextAreaText textbox,"Cancelled"+Chr$(13)+Chr$(10)
				EndIf
				AddTextAreaText textbox,"============="+Chr$(13)+Chr$(10)
			EndIf
		Case CLOSE_WINDOW
			finished=True
		Case KEY_PRESSED
			If EventData()=ESCAPE Then finished=True
	End Select
Until finished
End

;#########################################

Function change_prefs()
Local numgads, enabled;,gadtext$

Window=CreateWindow( "Change Prefs",centrex-100,centrey-150,200,300,main_window,49)
SetMinWindowSize window
;see comment at top for these next three commented lines, and commented local string above
;Restore gadnames
For gadnum=0 To NUMPREFS-1
;	Read gadtext$
;	gadgets(gadnum)=CreateButton(gadtext$,10,5+(gadnum * 25),150,20,window,CHECKBOX)
;remove or comment next line if uncommenting above
	gadgets(gadnum)=CreateButton(gadtext$(gadnum),10,5+(gadnum * 25),150,20,window,CHECK_BOX)
	SetButtonState gadgets(gadnum),1 And (preferences Shr gadnum)
Next

ok=CreateButton("OK",10,270,60,20,window)
cancel=CreateButton("Cancel",130,270,60,20,window)
DisableGadget ok
enabled=False
action=-1
Repeat
	WaitEvent()
	event=EventID()
	Select event
		Case CLOSE_WINDOW
			If EventSource()=window
				action=CANCELLED
			Else
				End
			EndIf
		Case GADGET_ACTION
			Select EventSource()
				Case ok		action=CHANGED
				Case cancel	action=CANCELLED
				Default
					newprefs=0
					For gadnum=0 To NUMPREFS-1
						newprefs=newprefs Or (ButtonState(gadgets(gadnum)) Shl gadnum)
					Next
					If (newprefs Xor preferences)
						If Not(enabled) Then EnableGadget ok: enabled=True
					Else
						If enabled Then DisableGadget ok: enabled=False
					EndIf
			End Select
		Case ACTIVATE_WINDOW
			If EventSource()<>window
				ActivateWindow window
			EndIf
		Case KEY_PRESSED	;doesn't work if checkbox clicked - but $105 event reported
			If EventData()=ESCAPE Then action=CANCELLED
	End Select
Until action > -1

FreeGadget window

Return action
End Function
;#########################################

.gadnames
Data "Pictures","Sound","Music","Visual-aid","Auto-change number","Block illegal moves"
Data "Big window","Clear selected number","Ninth","Tenth"
[/codebox]

N.B. The action performed by this code so far as the Activate_Window and Close_Window actions are concerned in the change_prefs() function depend upon whether the program is running in debug mode or not.  I haven't quite figured out why the Activate_Window takes precedence over Close_Window when debugging is switched off.

Try clicking the Main Window's close button when the prefs window is open - in both debug and non-debug modes to see what I mean.  And no - it's not Blitz that ends the program in debug mode - you can check that by change the "End" command to "ActivateWindow window" in change_prefs.

-----

One thing of note - if you click on a check-box so that there is a dotted rectangle around it, and then press F10 - have a look at what event is reported in the debug window.  The EventSource() and EventData() are both 0 though.  This happens when any gadget has a dotted rectangle around it - even ordinary buttons.

Nothing serious - but thought I'd mention it since I noticed it.  As far as I know, $2004 is when you grab something with the mouse - like the title-bar or a slider bar, and $2005 is when you release the mouse in the same circumstances.
