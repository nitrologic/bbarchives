; ID: 1334
; Author: BlackJumper
; Date: 2005-03-20 00:21:36
; Title: Block Intrusive keys - StickyKey_dll
; Description: Disable Windows StickyKey popup and toggle keyboard beeps ... now trap ALt+TAB and WIN key, etc.

; New Version 2 code
; Locks down your game with a single function call
; July 2005
Graphics 640, 480

StopIntrusions = True
vernum$ = GetStickyKeysVersionNumber()

KeyboardGameLock(True)

Repeat
Text 20,0,  "'T' to toggle Keyboard intrusion features on/off ......."
Text 20,60, "Keypressed " + GetKey()
Text 20,100, vernum
Text 20,140, "Keyboard intrusion state is " + StopIntrusions 

Text 20, 300, "try pressing SHIFT 5+ times (... can launch StickyKeys dialog);"
Text 20, 320, "hold down RightSHIFT For 8+ seconds (can launch FilterKeys dialog);"
Text 20, 340, "hold down NUMLOCK for 5+ seconds (can launch ToggleKeys dialog);"
Text 20, 360, "use ALT+TAB, ALT+Esc, CTRL+Esc Or the Win key ... can cause task switching"


If KeyHit(20) Then                 ;  'T' to toggle StopIntrusions 
   StopIntrusions = 1 - StopIntrusions 
   KeyboardGameLock( StopIntrusions )
EndIf	
		  

Flip False
Delay 30
Cls
Until KeyHit(1)
   
KeyboardGameLock(False) ; ESSENTIAL - Unhook GLOBAL (all apps) filter

End
