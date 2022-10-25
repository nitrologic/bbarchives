; ID: 1576
; Author: klepto2
; Date: 2005-12-24 17:17:53
; Title: Easy Mouse System for MaxGui
; Description: Mouse handling (BMX)

The TMouse Type:
[codebox]
Type TMouse
	Field x:Int  = 0
	Field y:Int  = 0
	Field z:Int  = 0
	Field Button:Byte[3] 
	
	Function Init:TMouse()
		Return New TMouse
	End Function
	
	Method Update()
		Local CUR_Event:TEvent = CurrentEvent
		Select Cur_Event.id
			Case EVENT_MOUSEMOVE  
				x = Cur_Event.x
				y = Cur_Event.y   
			Case EVENT_MOUSEWHEEL
				z:+Cur_Event.data
			Case EVENT_MOUSEDOWN
				Button[cur_event.data-1] = True
			Case EVENT_MOUSEUP
				Button[cur_event.data-1] = False
		End Select
	End Method  
	
	Method getX:Int()
		Return x
	End Method  
	
	Method getY:Int()
		Return y
	End Method
	Method getZ:Int()
		Return z
	End Method   
	
	Method GetButton:Byte(_Button:Int=0)
		If _Button >= 0 And _Button <= 2 Then 
			Return Button[_Button]
		EndIf
	End Method
	
End Type
[/codebox]

And a sample:

[codebox]
' createcanvas.bmx

Strict

Global GAME_WIDTH=800
Global GAME_HEIGHT=600

' create a centered window with client size GAME_WIDTH,GAME_HEIGHT

Local wx=(GadgetWidth(Desktop())-GAME_WIDTH)/2
Local wy=(GadgetHeight(Desktop())-GAME_HEIGHT)/2

Local window:TGadget=CreateWindow("My Canvas",wx,wy,GAME_WIDTH,GAME_HEIGHT,Null,WINDOW_TITLEBAR|WINDOW_CLIENTCOORDS)

' create a canvas for our game

Local canvas:TGadget=CreateCanvas(0,0,800,600,window)
 

Global Mouse:TMouse = TMouse.Init()
 
' create an update timer

CreateTimer 60

While WaitEvent() 

	Mouse.Update()     'Important the Update Method have to be directly after the WaitEvent() Command 
	
	Select EventID()
		Case EVENT_TIMERTICK
			RedrawGadget canvas
			
		Case EVENT_GADGETPAINT
			SetGraphics CanvasGraphics(canvas)
			Cls   
			SetOrigin -15-Mouse.getZ()/2,-15-Mouse.getZ()/2
			If Mouse.GetButton(0) = True Then
				DrawOval Mouse.getX(),Mouse.getY(),30+Mouse.getZ(),30+Mouse.getZ()	
			End If   
			SetOrigin 0,0
		    DrawText "X : "+Mouse.x,20,40
		    DrawText "Y : "+Mouse.y,20,60   
		    DrawText "Z : "+Mouse.z,20,80
		    DrawText Mouse.Button[0] + " : " + Mouse.Button[1] + " : " + Mouse.Button[2],20,100
		    
			Flip
    
			
		Case EVENT_WINDOWCLOSE
		   	FreeGadget canvas
			End

		Case EVENT_APPTERMINATE
			End
	End Select    
Wend    

Type TMouse
	Field x:Int  = 0
	Field y:Int  = 0
	Field z:Int  = 0
	Field Button:Byte[3] 
	
	Function Init:TMouse()
		Return New TMouse
	End Function
	
	Method Update()
		Local CUR_Event:TEvent = CurrentEvent
		Select Cur_Event.id
			Case EVENT_MOUSEMOVE  
				x = Cur_Event.x
				y = Cur_Event.y   
			Case EVENT_MOUSEWHEEL
				z:+Cur_Event.data
			Case EVENT_MOUSEDOWN
				Button[cur_event.data-1] = True
			Case EVENT_MOUSEUP
				Button[cur_event.data-1] = False
		End Select
	End Method  
	
	Method getX:Int()
		Return x
	End Method  
	
	Method getY:Int()
		Return y
	End Method
	Method getZ:Int()
		Return z
	End Method   
	
	Method GetButton:Byte(_Button:Int=0)
		If _Button >= 0 And _Button <= 2 Then 
			Return Button[_Button]
		EndIf
	End Method
	
End Type
[/codebox]
