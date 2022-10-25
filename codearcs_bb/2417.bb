; ID: 2417
; Author: KillerX
; Date: 2009-02-22 04:46:41
; Title: WayPointer
; Description: A pathfinding program

Graphics 800,600,0,1
SetBuffer BackBuffer()
Global PathTaken$ = 5
Dim LevelData(25,15)
Dim WayPoints(25,15)
Dim WayPointDirection$(25,15)
For Loop1 = 1 To 25
For Loop2 = 1 To 15
WayPoints(Loop1,Loop2) = -1
Next
Next


Dim AngleToStart(25,15)
Global StartX = 8
Global StartY = 5
Global FinishX = 21
Global FinishY = 2
Global MouseYChange = 0
Global MouseXChange = 0


Restore Level1Pointer;reference code
For Loop1 = 1 To 15
For Loop2 = 1 To 25
Read LevelData(Loop2,Loop1)

Next
Next






Main()



End



Function Main()
Repeat
Pathfinder()
ChangePoints()
MovePlayer()

DrawLevel()
Color 0,0,255
Text 0,0,PathTaken$
Flip
Until KeyHit(1)
End Function

Function MovePlayer()
If KeyDown(57)
If StartX = FinishX And StartY = FinishY Return
If WayPointDirection(FinishX,FinishY) = "L" FinishX = FinishX - 1:Return
If WayPointDirection(FinishX,FinishY) = "R" FinishX = FinishX + 1:Return
If WayPointDirection(FinishX,FinishY) = "U" FinishY = FinishY - 1:Return
If WayPointDirection(FinishX,FinishY) = "D" FinishY = FinishY + 1:Return
EndIf
End Function



Function DrawLevel()
For Loop1 = 1 To 25
For Loop2 = 1 To 15
	If LevelData(Loop1,Loop2)
	Color 128,128,128
	Rect -32+Loop1*32,-32+Loop2*32,32,32
	Else
	Color 64,64,64
	Rect -32+Loop1*32,-32+Loop2*32,32,32
	EndIf
	Color 0,0,255

	Text -16+Loop1*32,-16+Loop2*32,WayPointDirection$(Loop1,Loop2)+WayPoints(Loop1,Loop2),1,1
	
	
Next
Next
;draw start and finish points
Color 255,0,0
Rect -32+StartX*32,-32+StartY*32,32,32,0
Color 0,255,0
Rect -32+FinishX*32,-32+FinishY*32,32,32,0



Color 0,0,0
Rect 0,480,800,120
End Function




Function Pathfinder()
For Loop1 = 2 To 24
For Loop2 = 2 To 14
WayPoints(Loop1,Loop2) = -1
Next
Next
WayPointer()
End Function






Function WayPointer()
WayPoints(StartX,StartY) = 0
For Loop1 = 2 To 24
For Loop2 = 2 To 14
Highest_Value = 768
If LevelData(Loop1,Loop2) = 0
If WayPoints(Loop1,Loop2) = -1
If WayPoints(Loop1-1,Loop2) >= 0 And WayPoints(Loop1-1,Loop2) < Highest_Value;Loop1+1;Loop1-1
WayPoints(Loop1,Loop2) = WayPoints(Loop1-1,Loop2) + 1
WayPointDirection(Loop1,Loop2) = "L"
Highest_Value = WayPoints(Loop1-1,Loop2)
EndIf
If WayPoints(Loop1+1,Loop2) >= 0 And WayPoints(Loop1+1,Loop2) < Highest_Value;Loop1+1
WayPoints(Loop1,Loop2) = WayPoints(Loop1+1,Loop2) + 1
WayPointDirection(Loop1,Loop2) = "R"
Highest_Value = WayPoints(Loop1-1,Loop2)
EndIf
If WayPoints(Loop1,Loop2-1) >= 0 And WayPoints(Loop1,Loop2-1) < Highest_Value;Loop2-1
WayPoints(Loop1,Loop2) = WayPoints(Loop1,Loop2-1) + 1
WayPointDirection(Loop1,Loop2) = "U"
Highest_Value = WayPoints(Loop1-1,Loop2)
EndIf
If WayPoints(Loop1,Loop2+1) >= 0 And WayPoints(Loop1,Loop2+1) < Highest_Value;Loop2+1
WayPoints(Loop1,Loop2) = WayPoints(Loop1,Loop2+1) + 1
WayPointDirection(Loop1,Loop2) = "D"
Highest_Value = WayPoints(Loop1-1,Loop2)
EndIf
EndIf
EndIf
Next
Next

For Loop1 = 2 To 24
For Loop2 = 2 To 14
If LevelData(Loop1,Loop2) = 0
If WayPoints(Loop1,Loop2) = -1 WayPointer()
EndIf
Next
Next
End Function






Function CalculateAngleToFinish(ATFX,ATFY)
;CalculatedAngleToFinish is returned
;CalculateAngleToFinish is function name
;DisplacementToFinishDeltaX and DisplacementToFinishDeltaY is the difference between points
;ReturnedAngleToFinish is to avoid multiple calls to the function


CalculatedAngleToFinish = 4
DisplacementToFinishDeltaX = ATFX - FinishX
DisplacementToFinishDeltaY = ATFY - FinishY

If Abs(DisplacementToFinishDeltaX) > Abs(DisplacementToFinishDeltaY)
		If DisplacementToFinishDeltaX > 0 CalculatedAngleToFinish = 3
		If DisplacementToFinishDeltaX < 0 CalculatedAngleToFinish = 1
Else
		If DisplacementToFinishDeltaY > 0 CalculatedAngleToFinish = 0
		If DisplacementToFinishDeltaY < 0 CalculatedAngleToFinish = 2
EndIf


Return CalculatedAngleToFinish
End Function






Function ChangePoints()
;start point
MouseYChange = MouseYChange + MouseYSpeed()
MouseXChange = MouseXChange + MouseXSpeed()
If MouseYChange<-32 And LevelData(StartX,StartY-1) = 0 StartY = StartY - 1:MouseYChange = 0
If MouseXChange>32 And LevelData(StartX+1,StartY) = 0 StartX = StartX + 1:MouseXChange = 0
If MouseXChange<-32 And LevelData(StartX-1,StartY) = 0 StartX = StartX - 1:MouseXChange = 0
If MouseYChange>32 And LevelData(StartX,StartY+1) = 0 StartY = StartY + 1:MouseYChange = 0
MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
;finish point

End Function







.Level1Pointer
Data 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
Data 1,0,1,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1
Data 1,0,1,0,0,1,1,0,0,0,0,0,0,0,1,1,0,1,0,0,0,0,0,0,1
Data 1,0,1,0,0,1,1,0,0,0,0,0,1,0,1,0,0,0,0,1,0,0,0,0,1
Data 1,0,1,0,0,1,1,0,0,0,0,0,0,0,1,1,1,0,0,1,0,0,0,0,1
Data 1,0,1,0,0,1,1,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,1
Data 1,0,1,0,0,0,0,0,0,1,1,1,1,1,1,1,0,1,0,1,0,0,0,0,1
Data 1,0,1,0,0,1,1,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,1
Data 1,0,1,0,0,1,1,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,1
Data 1,0,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 1,0,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 1,0,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 1,0,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 1,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
