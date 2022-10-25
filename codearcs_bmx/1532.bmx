; ID: 1532
; Author: Shagwana
; Date: 2005-11-14 11:27:12
; Title: MaxGui Resize &amp; Redraw Canvas Example
; Description: Example of dynamic resizing of a windows display.

'
'  Simple example of dynamic resizing of the canvas and contence
'  Coded by Stephen Greener (aka Shagwana) www.sublimegames.com
'
'

Global pWin:TGadget=CreateWindow("Supersize me!",100,100,400,400,Null,WINDOW_TITLEBAR|WINDOW_RESIZABLE|WINDOW_CLIENTCOORDS)
Global pCan:TGadget=CreateCanvas(0,0,400,400,pWin)

SetMinWindowSize pWin,200,200    'Ensure the window dont go too small
SetGadgetLayout pCan,EDGE_ALIGNED,EDGE_ALIGNED,EDGE_ALIGNED,EDGE_ALIGNED   'Lock the size     

'Draw the display (in the window)
Function DrawDisplay()
  'Set destination for drawing
  SetGraphics CanvasGraphics(pCan)
  SetViewport 0,0,GadgetWidth(pCan),GadgetHeight(pCan)

  'Draw something
  Cls
  SetLineWidth 4
  DrawLine 0,0,GadgetWidth(pCan),GadgetHeight(pCan)
  DrawLine 0,GadgetHeight(pCan),GadgetWidth(pCan),0

  'Swap buffers
  Flip
  End Function


Function EventHook:Object(iId,tData:Object,tContext:Object)
    Local Event:TEvent=TEvent(tData)
    Select Event.Source
      Case pCan
      'Canvas event
      Select Event.id
        Case EVENT_GADGETPAINT
        'Needs redrawing
        DrawDisplay()    'Go redraw the display
        Return Null      'Dont pass the event back as its been delt with
        End Select
      End Select
    'Passback...
    Return Event         'Event pass through (nothing thats been captured)
	End Function

  
Local bQuit:Int=False             'Not to quit yet!

AddHook EmitEventHook,EventHook   'Add in an event hook


'Main event loop...
Repeat

  WaitEvent()

  Select EventSource()

    Case pCan
    Select EventID()
      Case EVENT_GADGETPAINT
      DrawDisplay()
      End Select

    Case pWin
    Select EventID()

      Case EVENT_WINDOWCLOSE
      'Quit the program
      bQuit=True
      End Select

    End Select

  Until bQuit=True
End
