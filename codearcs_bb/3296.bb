; ID: 3296
; Author: RemiD
; Date: 2016-11-16 04:07:32
; Title: 4 key states (Up/Idle, ToDown/Pressed, Down/Held, ToUp/Released)
; Description: This demonstrates how to get 4 different states for each key instead of the default 3 (up, hit, down)

;4 key states (Up/Idle, ToDown/Pressed, Down/Held, ToUp/Released)
;This demonstrates how to get 4 different states for each key instead of the default 3 (up, hit, down)
;i have coded my own keys/input system using this, but this is just a simple example so that you can understand the concept and add it to your own keys/input system...
Graphics3D(640,480,32,2)

SeedRnd(MilliSecs())

;for each key you need to have these globals in order to store the state of the key, or you can create arrays and have a constant for each key which corresponds to its index in the array...
Global KeySpaceState%
Global KeySpacePrevState%
Global KeySpaceDownCount%

Const CUp% = 1 ;Idle
Const CToDown% = 2 ;Pressed
Const CDown% = 3 ;Held
Const CToUp% = 4 ;Released

Global Timer = CreateTimer(10)

While(KeyDown(1)<>1)

 GetInput()

 ;SetBuffer(BackBuffer())
 ;ClsColor(000,000,000)
 ;Cls()
 ;Locate(0,0)

 If( KeySpaceState = CUp )
  Print("Up/Idle")
 ElseIf( KeySpaceState = CToDown )
  Print("ToDown/Pressed")
 ElseIf( KeySpaceState = CDown )
  Print("Down/Held")
 ElseIf( KeySpaceState = CToUp )
  Print("ToUp/Released")
 EndIf

 WaitTimer(Timer)
 VWait():Flip()

Wend

End()

Function GetInput()
 KeySpaceState = CUp
 If( KeyDown(57)=1 )
  KeySpaceDownCount = KeySpaceDownCount + 1
  If( KeySpaceDownCount = 1 )
   KeySpaceState = CToDown
   KeySpacePrevState = CToDown
  ElseIf( KeySpaceDownCount > 1 )
   KeySpaceState = CDown
   KeySpacePrevState = CDown
  EndIf
 ElseIf( KeyDown(57)=0 )
  KeySpaceDownCount = 0
  If( KeySpacePrevState = CToDown Or KeySpacePrevState = CDown )
   KeySpaceState = CToUp
   KeySpacePrevState = CToUp
  EndIf
 EndIf
 ;DebugLog("KeySpaceState = "+KeySpaceState)
End Function
