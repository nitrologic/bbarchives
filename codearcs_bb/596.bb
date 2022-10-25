; ID: 596
; Author: skidracer
; Date: 2003-02-21 02:54:05
; Title: PanelWindow
; Description: demonstrates a backdrop panel for changing background color of a window

Function PanelWindow(window)
	panel=CreatePanel(0,0,ClientWidth(window),ClientHeight(window),window)
	SetGadgetLayout panel,True,True,True,True
	Return panel
End Function

window = CreateWindow("Test Master",20,20,200,200) 
panel=PanelWindow(window)
SetPanelColor panel,20,40,200

i=CreateTextField(4,4,100,20,panel)

Repeat
	Select WaitEvent()
		Case $803 End	
	End Select
Forever
