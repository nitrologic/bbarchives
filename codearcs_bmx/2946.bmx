; ID: 2946
; Author: JoshK
; Date: 2012-05-25 01:18:23
; Title: .Net-style Toolbars
; Description: Gradient background for toolbars

SuperStrict
Import maxgui.drivers
Import maxgui.maxgui
Import brl.pixmap
?win32
Import pub.win32
?

Function CreateToolbar:TGadget(source:Object,x:Int,y:Int,width:Int,height:Int,group:TGadget,style:Int=0)
	?macos
	Return maxgui.maxgui.CreateToolbar(source,x,y,width,height,group,style)
	?
	?win32
	Local color:Int
	Local b:Byte[4]
	Local bt:Byte[4]
	Local pixmap:TPixmap
	Local panel:TGadget
	Local gradient:TPixmap
	Local toolbarheight:Int
	Local btncolor:Int
	
	btncolor=GetSysColor(COLOR_BTNFACE)
	MemCopy bt,Varptr btncolor,4
			
	pixmap=TPixmap(source)
	If Not pixmap pixmap=LoadPixmap(source)
	If Not pixmap Return Null
	
	toolbarheight=pixmap.height+8
	
	panel=CreatePanel(0,0,group.ClientWidth(),toolbarheight,group)
	panel.SetLayout 1,1,1,0
	Local toolbar:TGadget=maxgui.maxgui.CreateToolbar(source,x,y,width,height,panel,style)
	gradient=CreatePixmap(1,toolbarheight,PF_RGBA8888)
	For Local px:Int=0 To gradient.width-1
		For Local py:Int=0 To gradient.height-1
			color=gradient.ReadPixel(px,py)
			MemCopy b,Varptr color,4
			Local m#
			Local factor#=0.9'make this bigger (but less than one) for a less visible gradient
			m=(1.0-Float(py)/Float(gradient.height))*(1.0-factor)+factor
			m=Min(1.0,m)
			If py=gradient.height-1 m=factor*0.85
			b[0]=bt[2]*m
			b[1]=bt[1]*m
			b[2]=bt[0]*m
			b[3]=255
			MemCopy Varptr color,b,4
			gradient.WritePixel px,py,color
		Next
	Next
	SetGadgetPixmap panel,gradient
	Return toolbar
	?
EndFunction

Rem

'Example

AppTitle = "ToolBar Example"

Global window:TGadget = CreateWindow( AppTitle, 100, 100, 600, 300, Null, WINDOW_TITLEBAR|WINDOW_STATUS|WINDOW_RESIZABLE|WINDOW_CLIENTCOORDS )

Global toolbar:TGadget = CreateToolbar( "toolbar.png", 0, 0, 0, 0, window )
DisableGadgetItem toolbar, 2

SetToolbarTips toolbar, ["New", "Open", "Save should be disabled."] 

AddGadgetItem toolbar, "", 0, GADGETICON_SEPARATOR	'Add a separator.
AddGadgetItem toolbar, "Toggle", GADGETITEM_TOGGLE, 2, "This toggle button should change to a light bulb when clicked."

While WaitEvent()
	Print CurrentEvent.ToString()
	Select EventID()
		Case EVENT_WINDOWCLOSE, EVENT_APPTERMINATE;End
		'ToolBar Event(s)
		'EventData() holds the index of the toolbar item clicked.
		Case EVENT_GADGETACTION
			Select EventSource()
				Case toolbar 
					SetStatusText window, "Toolbar Item Clicked: " + EventData()
			EndSelect
	End Select
Wend

endrem
