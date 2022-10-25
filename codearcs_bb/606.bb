; ID: 606
; Author: Jim Brown
; Date: 2003-03-01 12:52:16
; Title: StartupPlus  - UPDATED for BlitzPlus v1.34
; Description: Simple 'Display Options' startup window

EXAMPLE USAGE
=============



; StartupPlus - Example
; Syntax Error

Include "StartupPlus.bb"

If SetDisplay("SuperDemo 2003",512,384,16,False) = 0 End
sw=GraphicsWidth() : sh=GraphicsHeight() : sd=GraphicsDepth()

x=40 : y=90 : dx=4 : dy=3
size=sw*0.15
timer=CreateTimer(60)

While Not KeyHit(1) Or MouseHit(1)
	WaitTimer timer
	Cls
	x=x+dx : y=y+dy
	If x<=0 Or x>=sw-size Then dx=-dx
	If y<=0 Or y>=sh-size Then dy=-dy
	Rect x,y,size,size
	Text 5,5,"Driver: "+GfxDriverName$(GFXDriver)
	Text 5,25,"Width="+sw+" Height="+sh+" Depth="+sd
	Flip
Wend

End





THE INCLUDE (StartupPlus.bb)
============================



; StartupPlus by Syntax Error
; Updated for BlitzPlus v1.34

; include file 'StartupPlus.bb"

Global GFXDriver=1

; set program display graphics
Function SetDisplay(title$="BlitzPlus Example",w=640,h=480,d=16,full=0,driver=1)
	Local xo=160
	If FileType("logo.jpg")=0 Then xo=0
	win = CreateWindow(title$,307,322,xo+164,182,Desktop(),1)
	; Graphics Mode
	Label1 = CreateLabel("Graphics Mode",xo+36,8,74,14,win,0)
	Combo1 = CreateComboBox(xo+8,26,138,100,win,0)
	AddGadgetItem Combo1,"Choose Resolution..."
	For g=1 To CountGfxModes()
		mode$=Str$(GfxModeWidth(g))+" x "+Str$(GfxModeHeight(g))+"  "+Str$(GfxModeDepth(g))+"Bit"
		AddGadgetItem Combo1,mode$
		If w=GfxModeWidth(g) And h=GfxModeHeight(g) And d=GfxModeDepth(g)
			SelectGadgetItem Combo1,g
		EndIf
	Next
	If SelectedGadgetItem(Combo1)=-1 Then SelectGadgetItem Combo1,0
	; Graphics Driver
	Label2 = CreateLabel("Graphics Driver",xo+36,52,74,14,win,0)
	Combo2 = CreateComboBox(xo+8,70,138,100,win,0)
	For g=1 To CountGfxDrivers()
		AddGadgetItem Combo2,GfxDriverName$(g)
	Next
	If driver<1 Then driver=1
	If driver>CountGfxDrivers() Then driver=CountGfxDrivers()
	SelectGadgetItem Combo2,driver-1
	; FullScreen / Windowed
	RadioButton1 = CreateButton("FullScreen",xo+4,98,76,20,win,3)
	RadioButton2 = CreateButton("Windowed",xo+80,98,70,20,win,3)
	SetButtonState RadioButton1,full=True
	SetButtonState RadioButton2,full=False
	; Start Button
	Button = CreateButton("Start",xo+4,120,140,26,win,0)
	; Graphic Image
	If xo>0
		panel = CreatePanel(16,14,128,128,win)
		SetPanelImage panel,"logo.jpg"
	EndIf
	
	; await user response
	Repeat
	Select WaitEvent()
		Case $103 ; key stroke
			If EventData()=27 Exit
			If EventData()=13
				If SelectedGadgetItem(Combo1)=0
					Notify "Please choose a Graphics mode"
				Else
					startflag=True : Exit
				EndIf
			EndIf
		Case $803 ; [x] close window
			Exit
		Case $401 ; gadget event
			If EventSource()=Button
				If SelectedGadgetItem(Combo1)=0
					Notify "Please choose a graphics mode"
				Else
					startflag=True : Exit
				EndIf
			EndIf
	End Select
	Forever

	; read combobox selections
	g=SelectedGadgetItem(Combo1)
	GFXDriver=SelectedGadgetItem(Combo2)+1
	If g>0	
		w=GfxModeWidth(g) : h=GfxModeHeight(g) : d=GfxModeDepth(g)
		full=ButtonState(radiobutton1)
	EndIf
	
	; close GUI and created graphics display
	FreeGadget win
	If full=0 Then full=2 ; windowed
	If startflag=True
		AppTitle title$
		SetGfxDriver GFXDriver
		Graphics w,h,d,full
		SetBuffer BackBuffer()
	EndIf
	Return startflag
End Function
