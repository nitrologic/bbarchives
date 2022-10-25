; ID: 2719
; Author: Spencer
; Date: 2010-05-19 22:32:46
; Title: Extended Mouse Click Events
; Description: MouseClick, MouseUp, MouseDown, MouseDoubleClick

;-------------------------------------------------------------------------------------------------
; Mouse Events 
;To use, save this code in your project's folder as "cMouseEvents.bb" 
;Then type Include "cMouseEvents.bb" at the top of your code
;-------------------------------------------------------------------------------------------------
cMouseEvents_Initalize()

Global cMouseEvent.cMouseEvents
Const  cMouseEvents_DefaultDoubleClickTimeWindow = 200
Global cMouseEvents_DoubleClickTimeWindowInMillisecs = cMouseEvents_DefaultDoubleClickTimeWindow

Type cMouseEvents
    Field LeftDown
    Field LeftUp
    Field LeftClick
    Field LeftDoubleClick
    Field LastTimeUserLeftClicked
    Field RightDown
    Field RightUp
    Field RightClick
    Field RightDoubleClick
    Field LastTimeUserRightClicked
    Field MiddleDown
    Field MiddleUp
    Field MiddleClick
    Field MiddleDoubleClick
    Field LastTimeUserMiddleClicked
End Type

Function cMouseEvents_Initalize()
	If cMouseEvent = Null Then
		cMouseEvent.cMouseEvents = New cMouseEvents
		cMouseEvent\LeftDown = 0
		cMouseEvent\LeftUp = 0
		cMouseEvent\LeftClick = 0
		cMouseEvent\LeftDoubleClick = 0
		cMouseEvent\LastTimeUserLeftClicked = 0
		cMouseEvent\RightDown = 0
		cMouseEvent\RightUp = 0
		cMouseEvent\RightClick = 0
		cMouseEvent\RightDoubleClick = 0
		cMouseEvent\LastTimeUserRightClicked = 0
		cMouseEvent\MiddleDown = 0
		cMouseEvent\MiddleUp = 0
		cMouseEvent\MiddleClick = 0
		cMouseEvent\MiddleDoubleClick = 0
		cMouseEvent\LastTimeUserMiddleClicked = 0
	EndIf
End Function

Function cMouseEvents_Dispose()
    Delete cMouseEvent
End Function

Function cMouseEvents_SetDoubleClickTimeWindow(MilliSeconds)
	If MilliSeconds > 0 Then
		cMouseEvents_DoubleClickTimeWindowInMillisecs = MilliSeconds
	Else
		cMouseEvents_DoubleClickTimeWindowInMillisecs = cMouseEvents_DefaultDoubleClickTimeWindow
	EndIf
End Function

Function cMouseEvents_GetDoubleClickTimeWindow()
	Return cMouseEvents_DoubleClickTimeWindowInMillisecs
End Function

Function cMouseEvents_Update()
    Local LeftClick             = MouseHit(1)
    Local RightClick            = MouseHit(2)
    Local MiddleClick           = MouseHit(3)
    Local LeftDown              = MouseDown(1)
    Local RightDown             = MouseDown(2)
    Local MiddleDown            = MouseDown(3)
    Local LeftUp                = ( (cMouseEvent\LeftDown=1  ) And (LeftDown=0  ) )
    Local RightUp               = ( (cMouseEvent\RightDown=1 ) And (RightDown=0 ) )
    Local MiddleUp              = ( (cMouseEvent\MiddleDown=1) And (MiddleDown=0) )
    Local TimeUserLeftClicked   = LeftClick   * MilliSecs()
    Local TimeUserRightClicked  = RightClick  * MilliSecs()
    Local TimeUserMiddleClicked = MiddleClick * MilliSecs()
    Local LeftClickDuration     = TimeUserLeftClicked   - cMouseEvent\LastTimeUserLeftClicked
    Local RightClickDuration    = TimeUserRightClicked  - cMouseEvent\LastTimeUserRightClicked
    Local MiddleClickDuration   = TimeUserMiddleClicked - cMouseEvent\LastTimeUserMiddleClicked
    Local LeftDoubleClick       = (LeftClickDuration   > 0) And (LeftClickDuration   < cMouseEvents_DoubleClickTimeWindowInMillisecs)
    Local RightDoubleClick      = (RightClickDuration  > 0) And (RightClickDuration  < cMouseEvents_DoubleClickTimeWindowInMillisecs)
    Local MiddleDoubleClick     = (MiddleClickDuration > 0) And (MiddleClickDuration < cMouseEvents_DoubleClickTimeWindowInMillisecs)
	
    cMouseEvent\LeftClick         = LeftClick
    cMouseEvent\RightClick        = RightClick
    cMouseEvent\MiddleClick       = MiddleClick
    cMouseEvent\LeftDown          = LeftDown
    cMouseEvent\RightDown         = RightDown
    cMouseEvent\MiddleDown        = MiddleDown
    cMouseEvent\LeftUp            = LeftUp
    cMouseEvent\RightUp           = RightUp
    cMouseEvent\MiddleUp          = MiddleUp
    cMouseEvent\LeftDoubleClick   = LeftDoubleClick
    cMouseEvent\RightDoubleClick  = RightDoubleClick
    cMouseEvent\MiddleDoubleClick = MiddleDoubleClick
	
    If LeftDoubleClick Then
        cMouseEvent\LastTimeUserLeftClicked   = 0
    ElseIf TimeUserLeftClicked > 0 Then
        cMouseEvent\LastTimeUserLeftClicked = TimeUserLeftClicked
    EndIf
	
    If RightDoubleClick Then
        cMouseEvent\LastTimeUserRightClicked   = 0
    ElseIf TimeUserRightClicked > 0 Then
        cMouseEvent\LastTimeUserRightClicked = TimeUserRightClicked
    EndIf
	
    If MiddleDoubleClick Then
        cMouseEvent\LastTimeUserMiddleClicked  = 0
    ElseIf TimeUserMiddleClicked > 0 Then
        cMouseEvent\LastTimeUserMiddleClicked = TimeUserMiddleClicked
    EndIf
	
End Function

Function cMouseEvents_ButtonClick(ButtonNumber)
	If cMouseEvent <> Null Then
        Select ButtonNumber
            Case 1 : Return cMouseEvent\LeftClick
            Case 2 : Return cMouseEvent\RightClick
            Case 3 : Return cMouseEvent\MiddleClick
            Default: Return 0
        End Select
    Else
        Return -1
    EndIf
End Function

Function cMouseEvents_ButtonDown(ButtonNumber)
	If cMouseEvent <> Null Then
        Select ButtonNumber
            Case 1 : Return cMouseEvent\LeftDown
            Case 2 : Return cMouseEvent\RightDown
            Case 3 : Return cMouseEvent\MiddleDown
            Default: Return 0
        End Select
    Else
        Return -1
    EndIf
End Function

Function cMouseEvents_ButtonUp(ButtonNumber)
    If cMouseEvent <> Null Then
        Select ButtonNumber
            Case 1 : Return cMouseEvent\LeftUp
            Case 2 : Return cMouseEvent\RightUp
            Case 3 : Return cMouseEvent\MiddleUp
            Default: Return 0
        End Select
    Else
        Return -1
    EndIf
End Function

Function cMouseEvents_ButtonDoubleClick(ButtonNumber)
    If cMouseEvent <> Null Then
        Select ButtonNumber
            Case 1 : Return cMouseEvent\LeftDoubleClick
            Case 2 : Return cMouseEvent\RightDoubleClick
            Case 3 : Return cMouseEvent\MiddleDoubleClick
            Default: Return 0
        End Select
    Else
        Return -1
    EndIf
End Function
;-------------------------------------------------------------------------------------------------
