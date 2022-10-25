; ID: 918
; Author: Matty
; Date: 2004-02-05 22:29:31
; Title: Mouse Click Functions
; Description: A simple function which determines whether a click,double click or if the mouse is held down.

;
;
;
;Mouse Usage Algorithm
;
;
;Call MouseInput(M,N) with M being the minimum time in millisecs in which to count a click as separate
;from holding the mouse down, and N being the maximum time in millisecs for a click to count as a 
;doubleclick
;
;
;The return value is the sum of the parameters in the const statement
;below, leftbutton, Right button etc.  
;
;
;Some sample values for the two parameters for the MouseInput() function would be 60 and 450.
;
;
;



Const LeftButton=1,Rightbutton=2,MiddleButton=4,DoubleClick=8,HeldDown=16,SingleClick=32

Type MouseObj

Field ButtonClicked
Field TimeClicked

End Type

Function MouseInput(MinClickTime,DoubleClickSpeed)
;Click Mode = 1 is a Click, = 2 is a Held Down button
;

Time=MilliSecs()
For Button.MouseObj=Each MouseObj
	If Time>Button\TimeClicked+DoubleClickSpeed Then Delete button
Next



If MouseDown(LeftButton) And MouseDown(RightButton)=0 Then MyMouseInput=CheckButton(LeftButton,Time,MinClickTime,DoubleClickSpeed)
If MouseDown(Rightbutton) And MouseDown(LeftButton)=0 Then MyMouseInput=CheckButton(RightButton,Time,MinClickTime,DoubleClickSpeed)
If MouseDown(MiddleButton) Then MyMouseInput=CheckButton(MiddleButton,Time,MinClickTime,DoubleClickSpeed)
If MouseDown(LeftButton) And MouseDown(RightButton) Then MyMouseInput=CheckButton(LeftButton+RightButton,Time,MinClickTime,DoubleClickSpeed)

Return MyMouseInput


End Function

Function CheckButton(WhichButton,Time,MinClickTime,DoubleClickSpeed)
ClickMode=SingleClick
	For Button.MouseObj=Each MouseObj
		If Time>Button\TimeClicked+MinClickTime And Time<Button\TimeClicked+DoubleClickSpeed Then 
			;Ie we have a click, not a mouse button held down...
			ClickMode=DoubleClick
		Else
			ClickMode=HeldDown
			Button\TimeClicked=Time
		EndIf 
	Next
Select ClickMode

Case SingleClick

Button.MouseObj=New MouseObj
Button\ButtonClicked=WhichButton
Button\Timeclicked=Time
Return SingleClick+Whichbutton


Case DoubleClick

For Button.Mouseobj=Each MouseObj
Delete Button
Next
Return DoubleClick+Whichbutton


Case HeldDown

Return HeldDown+WhichButton


End Select



End Function
