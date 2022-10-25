; ID: 2907
; Author: JoshK
; Date: 2012-01-07 19:30:34
; Title: WindowEx
; Description: Easily handles Windows, Menus, and Toolbars

SuperStrict

Import maxgui.proxygadgets
Import maxgui.drivers

Private

Type TIntWrapper
	
	Field i:Int
	
	Method Compare:Int(o:Object)
		Local intwrapper:TIntWrapper=TIntWrapper(o)
		If i>intwrapper.i Return 1
		If i<intwrapper.i Return -1
		Return 0
	EndMethod
	
	Function Create:TIntWrapper(i:Int)
		Local intwrapper:TIntWrapper=New TIntWrapper
		intwrapper.i=i
		Return intwrapper
	EndFunction
	
EndType

Function CopyRect(x0:Int,y0:Int,w:Int,h:Int,x1:Int,y1:Int,src:TPixmap,dst:TPixmap)
	Local x:Int,y:Int,c:Int'=RGBA(0,0,0,0)
	For x=0 To w-1
		For y=0 To h-1
			If src c=ReadPixelSafe(src,x0+x,y0+y)
			WritePixelSafe(dst,x1+x,y1+y,c)
		Next
	Next
EndFunction

Function ReadPixelSafe:Int(pixmap:TPixmap,x:Int,y:Int)
	If PixelIsValid(pixmap,x,y) Return pixmap.ReadPixel(x,y)
EndFunction

Function WritePixelSafe:Int(pixmap:TPixmap,x:Int,y:Int,color:Int)
	If PixelIsValid(pixmap,x,y) pixmap.WritePixel(x,y,color)
EndFunction

Function PixelIsValid:Int(pixmap:TPixmap,x:Int,y:Int)
	If x<0 Return False
	If x>pixmap.width-1 Return False
	If y<0 Return False
	If y>pixmap.height-1 Return False
	Return True
EndFunction

Local x:Int,y:Int
Local red:Int
MemCopy Varptr red,[0:Byte,0:Byte,255:Byte,255:Byte],4

For x=0 To TWindowEx.MissingIcon.width-1
	For y=0 To TWindowEx.MissingIcon.height-1
		TWindowEx.MissingIcon.WritePixel x,y,red
		If x=0 And y=0 TWindowEx.MissingIcon.WritePixel x,y,0
	Next
Next

For x=0 To TWindowEx.BlankIcon.width-1
	For y=0 To TWindowEx.BlankIcon.height-1
		TWindowEx.BlankIcon.WritePixel x,y,0
	Next
Next

Public

Type TWindowEx Extends TProxyGadget
	
	Const LoadPixmapPath:String=""
	Const ToolbarIconSize:Int=16
	
	Global menuiditerator:Int=0
	Global menuidmap:TMap=New TMap
	Global actionicon:TPixmap[]
	Global actionmenuicon:TPixmap[]
	Global MissingIcon:TPixmap=CreatePixmap(ToolbarIconSize,ToolbarIconSize,PF_RGBA8888)
	Global BlankIcon:TPixmap=CreatePixmap(ToolbarIconSize,ToolbarIconSize,PF_RGBA8888)
	
	Field window:TGadget
	Field toolbar:TGadget
	Field menuroot:TGadget
	Field currentmenu:TGadget
	Field toolbaritemid:Int[]
	Field menumap:TMap=New TMap
	Field toolbaritemmap:TMap=New TMap
	Field toolbartips:String[]
	Field existingtips:String[]
	Field existingtoolbarenablestate:Int[]
	
	Method Cleanup()
		RemoveHook EmitEventHook,EventHook,Self
		FreeGadget window
	EndMethod
	
	Method ToggleItem(action:Int)
		If ItemChecked(action)
			UncheckItem action
		Else
			CheckItem action
		EndIf
	EndMethod
	
	Method EnableItem(action:Int)
		Local intwrapper:TIntWrapper=TIntWrapper.Create(action)
		Local menu:TGadget
		menu=TGadget(menumap.ValueForKey(intwrapper))
		If menu EnableGadget menu
		intwrapper = TIntWrapper(toolbaritemmap.valueforkey(intwrapper))
		If intwrapper
			EnableGadgetItem toolbar,intwrapper.i
			existingtoolbarenablestate[intwrapper.i]=True
		EndIf
	EndMethod
	
	Method DisableItem(action:Int)
		Local intwrapper:TIntWrapper=TIntWrapper.Create(action)
		Local menu:TGadget
		menu=TGadget(menumap.ValueForKey(intwrapper))
		If menu DisableGadget menu
		intwrapper = TIntWrapper(toolbaritemmap.valueforkey(intwrapper))	
		If intwrapper
			DisableGadgetItem toolbar,intwrapper.i
			existingtoolbarenablestate[intwrapper.i]=False
		EndIf
	EndMethod
	
	Method CheckItem(action:Int)
		Local intwrapper:TIntWrapper=TIntWrapper.Create(action)
		Local menu:TGadget
		menu=TGadget(menumap.ValueForKey(intwrapper))
		If menu CheckMenu menu
		intwrapper = TIntWrapper(toolbaritemmap.valueforkey(intwrapper))	
		If intwrapper SelectGadgetItem toolbar,intwrapper.i
	EndMethod
	
	Method UncheckItem(action:Int)
		Local intwrapper:TIntWrapper=TIntWrapper.Create(action)
		Local menu:TGadget
		menu=TGadget(menumap.ValueForKey(intwrapper))
		If menu UncheckMenu menu
		intwrapper = TIntWrapper(toolbaritemmap.valueforkey(intwrapper))	
		If intwrapper DeselectGadgetItem toolbar,intwrapper.i
	EndMethod
	
	Method ItemEnabled:Int(action:Int)
		Local intwrapper:TIntWrapper=TIntWrapper.Create(action)
		Local menu:TGadget
		menu=TGadget(menumap.ValueForKey(intwrapper))
		If menu Return Not GadgetDisabled(menu)
		intwrapper = TIntWrapper(toolbaritemmap.valueforkey(intwrapper))	
		If intwrapper Return existingtoolbarenablestate[intwrapper.i]
		Return False
	EndMethod
	
	Method ItemChecked:Int(action:Int)
		Local intwrapper:TIntWrapper=TIntWrapper.Create(action)
		Local menu:TGadget
		menu=TGadget(menumap.ValueForKey(intwrapper))
		If menu Return MenuChecked(menu)
		intwrapper = TIntWrapper(toolbaritemmap.valueforkey(intwrapper))	
		If intwrapper Return GadgetItemSelected(toolbar,intwrapper.i)
		Return False
	EndMethod
	
	Function GadgetItemSelected:Int(gadget:TGadget,i:Int)
		Local n:Int
		Local items:Int[]=SelectedGadgetItems(gadget)
		For n=0 To items.length-1
			If items[n]=i Return True
		Next
		Return False
	EndFunction
	
	Method ProcessEvent:Int(event:TEvent)	
		Local parent:TGadget
		
		Select event.id
		
		Case EVENT_MENUACTION
			If Not TWindowEx(event.source)
				parent=TGadget(event.source)
				While parent
					If parent=WindowMenu(window)
						event.source=Self
					EndIf
					parent=parent.parent
				Wend
			EndIf
			
		Case EVENT_GADGETACTION
			Select event.source
			
			Case toolbar
			
				If toolbar
					EmitEvent CreateEvent(EVENT_MENUACTION,Self,toolbaritemid[event.data])
					Return False
				EndIf
				
			EndSelect
			
		EndSelect
		
		Return True
	EndMethod
	
	Method AddMenuItem:TGadget(name:String,group:TGadget=Null,action:Int=0)
		Local menu:TGadget
		Local pixmap:TPixmap
		If Not group group=WindowMenu(window)
		menu = CreateMenu("  "+name,action,group)
		menumap.Insert TIntWrapper.Create(action),menu
		If action
			?win32
			If actionmenuicon[action]
				SetGadgetPixmap menu,actionmenuicon[action]
			EndIf
			?
		EndIf
		Return menu
	EndMethod
	
	Method AddMenuSeparator(group:TGadget)
		CreateMenu("",0,group)
	EndMethod
	
	Method AddToolbarSeparator()
		toolbaritemid = toolbaritemid[..toolbaritemid.length+1]
		toolbaritemid[toolbaritemid.length-1]=-1
		toolbartips=toolbartips[..toolbartips.length+1]
	EndMethod
	
	Method AddToolbarItem(tip:String,action:Int)
		toolbaritemid = toolbaritemid[..toolbaritemid.length+1]
		toolbaritemid[toolbaritemid.length-1]=action
		toolbaritemmap.Insert TIntWrapper.Create(action),TIntWrapper.Create(toolbaritemid.length-1)
		toolbartips=toolbartips[..toolbartips.length+1]
		toolbartips[toolbartips.length-1]=tip
	EndMethod
	
	Method Clear()
		Local menu:TGadget
		For menu=EachIn WindowMenu(window).kids
			FreeGadget menu
		Next
		If toolbar
			FreeGadget toolbar
			toolbar=Null
		EndIf
		menumap.Clear()
		toolbaritemmap.Clear()
		toolbartips=Null
		toolbaritemid=Null
		existingtips=Null
		existingtoolbarenablestate=Null
	EndMethod
	
	Method UpdateMenu()
		UpdateWindowMenu window
	EndMethod
	
	Method Build()
		Local pixmap:TPixmap
		Local icon:TPixmap
		Local i:Int
		Local action:Int
		
		UpdateWindowMenu window
		
		If toolbar
			FreeGadget toolbar
			toolbar=Null
		EndIf
		
		If toolbaritemid
			pixmap=CreatePixmap(ToolbarIconSize*toolbaritemid.length,ToolbarIconSize,PF_RGBA8888)
			For i=0 To toolbaritemid.length-1
				action=toolbaritemid[i]
				If action=-1
					icon=BlankIcon
				Else
					icon=actionicon[action]
				EndIf
				If Not icon
					icon=MissingIcon
				EndIf
				CopyRect 0,0,ToolbarIconSize,ToolbarIconSize,i*ToolbarIconSize,0,icon,pixmap
			Next
			toolbar = CreateToolbar(pixmap,0,0,0,0,window)
			SetToolbarTips toolbar,toolbartips
			existingtips = toolbartips
			existingtoolbarenablestate=existingtoolbarenablestate[..toolbaritemid.length]
			For i=0 To existingtoolbarenablestate.length-1
				existingtoolbarenablestate[i]=True
			Next
		EndIf
	EndMethod
	
	Function Create:TWindowEx(title:String,x:Int,y:Int,width:Int,height:Int,group:TGadget,style:Int=WINDOW_DEFAULT)
		Local windowex:TWindowEx=New TWindowEx
		windowex.window=CreateWindow(title,x,y,width,height,group,style)
		windowex.SetProxy windowex.window
		AddHook EmitEventHook,EventHook,windowex
		Return windowex
	EndFunction
	
	Function EventHook:Object(id:Int,data:Object,context:Object)
		Local event:TEvent
		Local windowex:TWindowEx
		
		windowex=TWindowEx(context)
		If windowex
			event=TEvent(data)
			If event
				If Not windowex.ProcessEvent(event) Return Null
			EndIf
		EndIf
		Return data
	EndFunction
	
	Function AllocMenuID:Int()
		menuiditerator:+1
		actionicon=actionicon[..menuiditerator+1]
		actionmenuicon=actionmenuicon[..menuiditerator+1]
		Return menuiditerator
	EndFunction
		
EndType

Function GetActionID:Int(action:String)
	Local intwrapper:TIntWrapper
	action = action.ToLower()		
	intwrapper = TIntWrapper(TWindowEx.menuidmap.valueforkey(action))
	If Not intwrapper
		intwrapper=New TIntWrapper
		intwrapper.i = TWindowEx.AllocMenuID()
		TWindowEx.menuidmap.Insert action,intwrapper
	EndIf
	TWindowEx.actionicon[intwrapper.i]=LoadPixmap(TWindowEx.LoadPixmapPath+action+".png")
	If TWindowEx.actionicon[intwrapper.i]
		If TWindowEx.actionicon[intwrapper.i].width<>TWindowEx.ToolbarIconSize Or TWindowEx.actionicon[intwrapper.i].height<>TWindowEx.ToolbarIconSize
			TWindowEx.actionicon[intwrapper.i] = ResizePixmap(TWindowEx.actionicon[intwrapper.i],TWindowEx.ToolbarIconSize,TWindowEx.ToolbarIconSize)
		EndIf
		TWindowEx.actionmenuicon[intwrapper.i] = ResizePixmap(TWindowEx.actionicon[intwrapper.i],13,13)
	EndIf
	Return intwrapper.i
EndFunction

Function CreateWindowEx:TWindowEx(title:String,x:Int,y:Int,width:Int,height:Int,group:TGadget,style:Int=WINDOW_DEFAULT)
	Return TWindowEx.Create(title,x,y,width,height,group,style)
EndFunction

'=========================================
'Example
'=========================================

AppTitle = "WindowEx Example"

Local window:TWindowEx = CreateWindowEx( AppTitle, 100, 100, 320, 240, Null)
Local menu:TGadget

Global action_file_new:Int = GetActionID("File_New")
Global action_file_open:Int = GetActionID("File_Open")
Global action_file_save:Int = GetActionID("File_Save")
Global action_app_exit:Int = GetActionID("App_Exit")
Global action_option_toggle:Int = GetActionID("Options_Toggle")
Global action_option_disable:Int = GetActionID("Options_Disable")
Global action_option_enable:Int = GetActionID("Options_Enable")

menu=window.AddMenuItem("File")
window.AddMenuItem "New",menu,action_file_new
window.AddMenuSeparator menu
window.AddMenuItem "Open",menu,action_file_open
window.AddMenuItem "Save",menu,action_file_save
window.AddMenuSeparator menu
window.AddMenuItem "Exit",menu,action_app_exit

menu=window.AddMenuItem("Options")
window.AddMenuItem "Toggle",menu,action_option_toggle
window.AddMenuItem "Disable",menu,action_option_disable
window.AddMenuItem "Enable",menu,action_option_enable

window.AddToolbarItem "New File",action_file_new
window.AddToolbarSeparator
window.AddToolbarItem "Open File",action_file_open
window.AddToolbarItem "Save File",action_file_save
window.AddToolbarSeparator
window.AddToolbarItem "Toggle",action_option_toggle
window.AddToolbarItem "Enable",action_option_enable
window.AddToolbarItem "Disable",action_option_disable

window.Build()

Repeat
	WaitEvent()
	Select EventID()
		
		Case EVENT_MENUACTION
			Select EventSource()
			
			Case window
				
				Select EventData()
				
				Case action_file_new
					Print "File_New"
				
				Case action_file_open
					Print "File_Open"
				
				Case action_file_save
					Print "File_Save"				
				
				Case action_app_exit
					Print "App_Exit"
					End
				
				Case action_option_toggle
					Print "Option_Toggle"
					If window.ItemChecked(action_option_toggle)
						window.UncheckItem action_option_toggle
					Else
						window.CheckItem action_option_toggle
					EndIf
					UpdateWindowMenu window
	
				Case action_option_disable
					Print "Option_Disable"
					If window.ItemEnabled(action_option_disable)
						window.DisableItem action_option_disable
					Else
						window.EnableItem action_option_disable
					EndIf
					UpdateWindowMenu window
				
				Case action_option_enable
					Print "Option_Enable"
					window.EnableItem action_option_disable
					UpdateWindowMenu window
					
				EndSelect
				
			EndSelect
			
		Case EVENT_APPTERMINATE, EVENT_WINDOWCLOSE
			End
			
	End Select
Forever
