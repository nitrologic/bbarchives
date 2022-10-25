; ID: 1537
; Author: Shagwana
; Date: 2005-11-18 14:02:06
; Title: MaxGui Program Flow via sub hooking
; Description: Example of using one master hook to send events to multipul functions

'
' Example of using one master hook, to send sorted events to
' registered gadgets.
'
' Also an example of how to control program flow in a more efficent manner.
'

SuperStrict


Type TMasterEventHook

  'Master sub hook list of intrested gadgets
  Global tSubHookIntrestedList:TList=Null    'Starts off empty

  Method New()
    If tSubHookIntrestedList:TList=Null
      Print "Sub hook list created"
      tSubHookIntrestedList:TList=CreateList()
      EndIf
    End Method

  'Add the master hooking method in
  Function InstallMasterHook()
    Print "Hook installed"
    AddHook EmitEventHook,TMasterEventHook.ProcessEvents
    End Function


  'All events pass to here
  Function ProcessEvents:Object(iId:Int,tData:Object,tContext:Object)

    If tData=Null Then Return Null     'Nothing here to work on
    Local tEv:TEvent=TEvent(tData)     'Convert into an event
    
    'Scan the list, see if we have a source
    For Local tSH:TSubEventHook = EachIn tSubHookIntrestedList:TList
      If tSH.pWidget = tEv.Source 
        tSH.OnEvent(tEv)    'Send the event to the proper place
        Return Null
        EndIf
      Next
'    Print tEv.Source.tostring()+" -> "+tEv.tostring()

    Return Null     'Presume we want to capture events!
    End Function


  End Type



Type TSubEventHook Extends TMasterEventHook

  Method New()
    Print "Sub hook in"
    ListAddLast TMasterEventHook.tSubHookIntrestedList,Self
    End Method

  Field pWidget:TGadget=Null             'Events of this gadget needed

  Method OnEvent(tEv:TEvent) Abstract    '----> coded someplace else

  End Type




'_/ code \____________________________________________________________________________________________________



Type TWin1Code Extends TSubEventHook  

  'Register a gadget to this code lump ....
  Function RegisterGadget:TWin1Code(tG:TGadget)
    Local tSH:TWin1Code = New TWin1Code
    tSH.pWidget = tG
    Return tSH:TWin1Code
    End Function

  'Event processing for this gadget
  Method OnEvent(tEv:TEvent)
    Print "TWin 1 Code -> "+tEv.tostring()

    'Event handle...
    Select tEv.Id
      Case EVENT_WINDOWCLOSE
      End    'Quit this program on window close
      End Select

    End Method

  End Type





Type TBut1Code Extends TSubEventHook  

  'Register a gadget to this code lump ....
  Function RegisterGadget:TBut1Code(tG:TGadget)
    Local tSH:TBut1Code = New TBut1Code
    tSH.pWidget = tG
    Return tSH:TBut1Code
    End Function

  'Event processing for this gadget
  Method OnEvent(tEv:TEvent)
    Print "TBut 1 Code -> "+tEv.tostring()
    End Method

  End Type






Type TBut2Code Extends TSubEventHook  

  'Register a gadget to this code lump ....
  Function RegisterGadget:TBut2Code(tG:TGadget)
    Local tSH:TBut2Code = New TBut2Code
    tSH.pWidget = tG
    Return tSH:TBut2Code
    End Function


  'Event processing for this gadget
  Method OnEvent(tEv:TEvent)
    Print "TBut 2 Code -> "+tEv.tostring()
    End Method

  End Type





Type TBut3Code Extends TSubEventHook  

  'Register a gadget to this code lump ....
  Function RegisterGadget:TBut3Code(tG:TGadget)
    Local tSH:TBut3Code = New TBut3Code
    tSH.pWidget = tG
    Return tSH:TBut3Code
    End Function

  'Event processing for this gadget
  Method OnEvent(tEv:TEvent)
    Print "TBut 3 Code -> "+tEv.tostring()
    End Method

  End Type






Type TBut4Code Extends TSubEventHook  

  'Register a gadget to this code lump ....
  Function RegisterGadget:TBut4Code(tG:TGadget)
    Local tSH:TBut4Code = New TBut4Code
    tSH.pWidget = tG
    Return tSH:TBut4Code
    End Function

  'Event processing for this gadget
  Method OnEvent(tEv:TEvent)
    Print "TBut 4 Code -> "+tEv.tostring()
    End Method

  End Type




Type TBut5Code Extends TSubEventHook  

  'Register a gadget to this code lump ....
  Function RegisterGadget:TBut5Code(tG:TGadget)
    Local tSH:TBut5Code = New TBut5Code
    tSH.pWidget = tG
    Return tSH:TBut5Code
    End Function

  'Event processing for this gadget
  Method OnEvent(tEv:TEvent)
    Print "TBut 5 Code -> "+tEv.tostring()
    End Method

  End Type





'Gui building ...
Global pWin1:TGadget=CreateWindow("Supersize me!",500,100,400,400,Null,WINDOW_TITLEBAR|WINDOW_RESIZABLE|WINDOW_CLIENTCOORDS)
Global pBut1:TGadget=CreateButton("But1",40,40,80,26,pWin1)
Global pBut2:TGadget=CreateButton("But2",40,120,80,26,pWin1)
Global pPan1:TGadget=CreatePanel(180,50,200,200,pWin1,PANEL_BORDER)
Global pBut3:TGadget=CreateButton("But3",40,10,80,26,pPan1)
Global pPan2:TGadget=CreatePanel(20,300,240,80,pWin1,PANEL_BORDER)
Global pBut4:TGadget=CreateButton("But4",40,20,80,26,pPan2)
Global pBut5:TGadget=CreateButton("But5",140,40,80,26,pPan2)
SetMinWindowSize pWin1,200,200


'Install sub event hooks for given gadgets ....
Local tWin1Logic:TWin1Code = TWin1Code.RegisterGadget( pWin1 )
Local tBut1Logic:TBut1Code = TBut1Code.RegisterGadget( pBut1 )
Local tBut2Logic:TBut2Code = TBut2Code.RegisterGadget( pBut2 )
Local tBut3Logic:TBut3Code = TBut3Code.RegisterGadget( pBut3 )
Local tBut4Logic:TBut4Code = TBut4Code.RegisterGadget( pBut4 )
Local tBut5Logic:TBut5Code = TBut5Code.RegisterGadget( pBut5 )


'Master hook routine install
TMasterEventHook.InstallMasterHook()

'Main loopage
Repeat
  WaitEvent
  Forever
End
