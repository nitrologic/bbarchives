; ID: 2490
; Author: JoshK
; Date: 2009-05-26 17:48:29
; Title: Property Editor
; Description: Tree-style property editor

Strict 

Import maxgui.maxgui
Import brl.eventqueue
Import brl.retro
Import brl.tgaloader
Import brl.bmploader
?win32
Import pub.win32
?

Rem

Import maxgui.drivers

Local window:TGadget

window=CreateWindow("Properties",40,40,360,400,Null,WINDOW_TITLEBAR|WINDOW_CENTER|WINDOW_RESIZABLE|WINDOW_HIDDEN)

Local grid:TPropertyGrid=TPropertyGrid.Create(0,0,,,window)
SetGadgetLayout grid,1,1,1,1
Local group:TPropertyGroup

group=grid.AddGroup("Settings")
group.AddProperty("String","Hello!")
group.AddProperty("Integer","3",PROPERTY_INTEGER,"Edit the integer")
group.AddProperty("Float","1",PROPERTY_FLOAT,"This is a float")
group.AddProperty("Boolean","1",PROPERTY_BOOL,"Toggle this on and off")
group.AddProperty("Choice","1|A,B,C,D",PROPERTY_CHOICE,"Choose your destiny")
group.expand()

group=grid.AddGroup("More Settings")
group.AddProperty("Integer with range","3|3,10",PROPERTY_INTEGER,"Edit the integer")
group.AddProperty("Float with range","0.63|0.5,1.0,10",PROPERTY_FLOAT,"This is a float")
group.AddProperty("High-res float","0.63|0.5,1.0,100",PROPERTY_FLOAT,"This is a float")
group.AddProperty("File","C:\MyFile.bmp|Text Files (*.txt):txt;Windows Bitmap (*.bmp):bmp",PROPERTY_FILE,"This file does something")
group.AddProperty("Folder","C:\",PROPERTY_PATH,"This folder is used for something")
group.AddProperty("Color","255,0,0",PROPERTY_COLOR,"Choose a color")
group.AddProperty("Editable Choice","Candle|Pulse,Candle,Strobe,Flame",PROPERTY_CHOICEEDIT,"Sets the light style:~nStrobe - turns on and off quickly~n2blinking - blinks~n3pulse - gentle intensity pulse~n4candle - firelight flicker")
group.AddProperty("Vector2","1.0,2.0,3.0,4.0",PROPERTY_VEC2,"Sets the position")
group.AddProperty("Vector3","1.0,2.0,3.0,4.0",PROPERTY_VEC3,"Sets the position")
group.AddProperty("Vector4","1.0,2.0,3.0,4.0",PROPERTY_VEC4,"Sets the position")

group.sort()

ShowGadget window

While True
	WaitEvent
	Print CurrentEvent.tostring()
	Select EventID()
		Case EVENT_WINDOWCLOSE
			End
	EndSelect
Wend

EndRem

Incbin "header.tga"
Incbin "header2.tga"
Incbin "tree.bmp"
Incbin "folder.tga"
Incbin "border.tga"


Type TPropertyGrid Extends TProxyGadget {expose}
	
	Const headerheight:Int=20
	Const headerspacer:Int=0
	Const sliderwidth:Int=18
	Const treeimagebottompadding:Int=14
	
	Global list:TList=New TList
	Global background:Int[]=[255,255,255]
	Global headercolor:Int[]=[128,128,128]
	Global color_headertext:Int[]=[255,255,255]
	Global color_headertexthover:Int[]=[150,150,150]
	Global color_itemtext:Int[]=[0,0,0]
	Global color_selecteditemtext:Int[]=[0,0,255]
	
	Field slider:TGadget
	Field groups:TList=New TList
	Field panel:TGadget
	Field sections:TMap=New TMap
	Field currentitem:TProperty
	Field font:TGUIFont
	Field properties:TMap=New TMap

	Method Collapse()
		For Local propertygroup:Tpropertygroup=EachIn sections.values()
			propertygroup.Collapse(0)
		Next
		AdjustLayout()
	EndMethod
	
	Method Expand()
		For Local propertygroup:Tpropertygroup=EachIn sections.values()
			propertygroup.Expand(0)
		Next
		AdjustLayout()
	EndMethod
	
	Method Combine(combiner:TPropertyGrid,update=1)
		Local item:TProperty[2]
		Local key$
		For Local gs:TPropertyGroup=EachIn groups
			If Not gs.hidden
				If combiner.sections.valueforkey(gs.name)=Null
					gs.hide(0)
				Else
					For item[0]=EachIn gs.items
						If Not item[0].hidden
							key=item[0].name.tolower()
							item[1]=TProperty(combiner.properties.valueforkey(key))
							If Not item[1]
								item[0].hide(0)
							Else
								If item[0].style<>item[1].style Or item[0].extra<>item[1].extra Or item[0].range[0]<>item[1].range[0] Or item[0].range[1]<>item[1].range[1] Or item[0].flags<>item[1].flags Or item[0].group.name<>item[1].group.name
									item[0].hide(0)
								EndIf
							EndIf
						EndIf
					Next
				EndIf
			EndIf
		Next
		'slower
		Rem
		For key=EachIn properties.keys()
			item[0]=TProperty(properties.valueforkey(key))
			item[1]=TProperty(combiner.properties.valueforkey(key))
			If Not item[1]
				item[0].hide(0)
			Else
				If item[0].style<>item[1].style Or item[0].extra<>item[1].extra Or item[0].range[0]<>item[1].range[0] Or item[0].range[1]<>item[1].range[1] Or item[0].flags<>item[1].flags Or item[0].group.name<>item[1].group.name
					item[0].hide(0)
					Continue
				EndIf
			EndIf
		Next
		EndRem
		If update AdjustLayout()
	EndMethod

	Method SetFont(font:TGUIFont)
		Self.font=font
		For Local propertygroup:Tpropertygroup=EachIn sections.values()
			propertygroup.SetFont(font)
		Next
	EndMethod

	Method New()
		headerpixmap[0]=LoadPixmap("incbin::header.tga")
		headerpixmap[1]=LoadPixmap("incbin::header2.tga")
	EndMethod
	
	Method SetProperty(name:String,value:String)
		Local property:TProperty
		name=name.tolower()
		property=TProperty(properties.valueforkey(name))
		If property property.setvalue(value)
	EndMethod

	Method GetProperty:String(name:String,defaultvalue:String="")
		Local property:TProperty
		name=name.tolower()
		property=TProperty(properties.valueforkey(name))
		If property.hidden Or property.group.hidden Or property.undetermined property=Null
		If property Return property.value Else Return defaultvalue
	EndMethod

	Method Cleanup()
		For Local propertygroup:Tpropertygroup=EachIn sections.values()
			propertygroup.Free()
		Next
		FreeGadget slider
		FreeGadget panel
		sections.clear()
		currentitem=Null
		Super.cleanup
	EndMethod
	
	Rem
	Method Load:Int(url:Object)
		Local stream:TStream=ReadStream(url)
		If Not stream Return False
		Local s:String
		Local group:TPropertyGroup
		Local sarr:String[]
		While Not stream.Eof()
			s=stream.ReadLine().Trim()
			If s
				sarr=s.split("//")
				s=sarr[0]
				If s
					If s[0]=Asc("[")
						s=s[1..s.length-1].Trim()
						group=AddGroup(s)
					Else
						If group
							sarr=s.split("\\")
							sarr=sarr[..6]
							Local class:Int
							Select sarr[2].tolower().Trim()
								Case "","string" class=PROPERTY_STRING
								Case "float" class=PROPERTY_FLOAT
								Case "integer" class=PROPERTY_INTEGER
								Case "file" class=PROPERTY_FILE
								Case "path" class=PROPERTY_PATH
								Case "color" class=PROPERTY_COLOR
								Case "bool" class=PROPERTY_BOOL
								Case "choice" class=PROPERTY_CHOICE
								Case "choiceedit" class=PROPERTY_choiceedit
								Case "vector2" class=PROPERTY_VEC2
								Case "vector3" class=PROPERTY_VEC3
								Case "vector4" class=PROPERTY_VEC4
							EndSelect
							group.addproperty(sarr[0].Trim(),sarr[1].Trim(),class,sarr[3].Trim(),Int(sarr[4]),sarr[5].Trim())
						EndIf
					EndIf
				EndIf
			EndIf
		Wend
		stream.close()
		Return True
	EndMethod
	EndRem
	
	Method Clear()
		For Local gs:TPropertyGroup=EachIn sections.values()
			gs.free()
		Next
	EndMethod
	
	Function Create:TPropertyGrid(x=0,y=0,width=-1,height=-1,group:TGadget)
		Local grid:TPropertyGrid=New TPropertyGrid
		If width=-1 width=group.ClientWidth()-x
		If height=-1 height=group.ClientHeight()-y
		grid.panel=CreatePanel(x,y,width,height,group)
		grid.SetProxy(grid.panel)
		AddHook EmitEventHook, eventHandler, Null, 1
		list.addlast(grid)
		grid.slider=CreateSlider(grid.panel.ClientWidth()-sliderwidth,0,sliderwidth,grid.panel.ClientHeight(),grid.panel)
		SetGadgetLayout grid.slider,0,1,1,1
		SetGadgetColor grid.panel,background[0],background[1],background[2]
		Return grid
	EndFunction
	
	Method AddGroup:TPropertyGroup(name:String)
		Local gs:TPropertyGroup=New TPropertyGroup
		gs.header=CreatePanel(0,0,panel.ClientWidth(),headerheight,panel,PANEL_ACTIVE)
		SetGadgetPixmap gs.header,LoadPixmap("incbin::border.tga")
		SetGadgetLayout gs.header,1,1,1,0
		SetPanelColor gs.header,headercolor[0],headercolor[1],headercolor[2]
		gs.headerlabel=CreateLabel(name,headerheight,2,gs.header.ClientWidth()-headerheight,20,gs.header)
		If font SetGadgetFont gs.headerlabel,font
		SetGadgetColor gs.headerlabel,color_headertext[0],color_headertext[1],color_headertext[2],0
		SetGadgetLayout gs.headerlabel,1,1,1,0
		gs.panel=CreatePanel(0,20,panel.ClientWidth(),2,panel)
		SetGadgetLayout gs.panel,1,1,1,0
		gs.grid=Self
		gs.link=groups.addlast(gs)
		sections.insert(name,gs)

		gs.name=name

		gs.sidepanel=CreatePanel(0,0,TPropertyGroup.itemheight,gs.panel.height,gs.panel)
		HideGadget gs.sidepanel
		SetGadgetPixmap gs.sidepanel,LoadPixmap("incbin::tree.bmp")
		SetGadgetLayout gs.sidepanel,1,1,1,1
		
		SetPanelColor gs.panel,background[0],background[1],background[2]
		
		gs.imagepanel=CreatePanel(0,0,headerheight,headerheight,gs.header,PANEL_ACTIVE)		
		SetGadgetPixmap gs.imagepanel,headerpixmap[0]
		SetGadgetLayout gs.imagepanel,1,0,1,0
		SetPanelColor gs.imagepanel,headercolor[0],headercolor[1],headercolor[2]
		
		SetGadgetSensitivity gs.headerlabel,SENSITIZE_MOUSE
		
		AdjustLayout()
		Return gs
	EndMethod
	
	Method CalcHeight:Int()
		Local gs:TPropertyGroup
		Local y:Int=0
		For gs=EachIn sections.values()
			If Not gs.hidden
				If gs.collapsed
					y:+headerheight+headerspacer
				Else
					y:+headerheight+gs.y
				EndIf
			EndIf
		Next
		Return y
	EndMethod
	
	Method Sort()
		Local gs:TPropertyGroup
		For gs=EachIn sections.values()
			gs.sort()
		Next
	EndMethod

	Method ShowAll(update=1)
		Local gs:TPropertyGroup
		For gs=EachIn groups
			gs.showall()
		Next
		If update AdjustLayout()
	EndMethod
	
	Field slidermode:Int=1
	
	Field headerpixmap:TPixmap[2]
	
	Method AdjustLayout(items:Int=True)
		Local x:Int
		Local y:Int
		Local h:Int
		
		h=CalcHeight()
		If h>panel.ClientHeight()
			SetSliderRange slider,panel.ClientHeight(),h
			If slidermode<>1
				slidermode=1
				items=True
				ShowGadget slider
			EndIf
		Else
			If slidermode<>0
				slidermode=0
				items=True
				HideGadget slider
				SetSliderValue slider,0
			EndIf
		EndIf
		If slidermode
			y=-SliderValue(slider)
		EndIf
		If items
			For Local gs:TPropertyGroup=EachIn groups
				If gs.hidden
					HideGadget gs.header
					HideGadget gs.panel
				Else
					SetGadgetShape gs.header,0,y,panel.ClientWidth(),headerheight
					If gs.collapsed
						SetGadgetPixmap gs.imagepanel,headerpixmap[0]
						HideGadget gs.panel
						y:+headerheight+headerspacer
					Else
						SetGadgetPixmap gs.imagepanel,headerpixmap[1]
						SetGadgetShape gs.panel,0,y+headerheight,panel.ClientWidth()-sliderwidth*slidermode,gs.y
						gs.AdjustLayout()
						ShowGadget gs.panel
						y:+headerheight+gs.panel.height
						ShowGadget gs.panel
					EndIf
					ShowGadget gs.header

				EndIf
				SetGadgetShape(gs.sidepanel,0,0,gs.itemheight,gs.panel.height-treeimagebottompadding)
				SetGadgetLayout gs.sidepanel,1,0,1,1
			Next
		EndIf
	EndMethod
	
	Function GadgetWindow:TGadget(gadget:TGadget)
		While gadget
			gadget=GadgetGroup(gadget)
			If GadgetClass(gadget)=GADGET_WINDOW Return gadget
		Wend
		Return Null
	EndFunction
	
	Function GadgetHasParent:Int(gadget:TGadget,parent:TGadget)
		While gadget
			gadget=GadgetGroup(gadget)
			If gadget=parent Return True
		Wend
		Return False
	EndFunction
	
	Function eventHandler:Object( pID%, pData:Object, pContext:Object )
		Local event:TEvent = TEvent(pData)
		
		If event
			Local grid:TPropertyGrid
			Local gs:TPropertyGroup
			Local gi:TProperty
			
			For grid=EachIn TPropertyGrid.list
				If Not GadgetHidden(grid)
					If event.id=EVENT_WINDOWSIZE' Or event.id=EVENT_GADGETSHOW
						If GadgetWindow(grid.panel)=event.source
							grid.AdjustLayout(0)
							Return event
						EndIf
					EndIf
					
					If GadgetHasParent(TGadget(event.source),grid.panel)
					
						If event.source=grid.slider
							grid.AdjustLayout()
							Return event
						EndIf
						
						For gs=EachIn grid.groups
							
							If gs.imagepanel=event.source Or gs.header=event.source Or gs.headerlabel=event.source
								Return gs.EventHook(event)
							EndIf
							If Not gs.collapsed
								If Not gs.hidden
									If GadgetHasParent(TGadget(event.source),gs.panel)
										For gi=EachIn gs.items
											If Not gi.hidden
												If gi.control=event.source Or gi.control2=event.source Or gi.helper[0]=event.source
													Return gi.eventhook(event,grid)
												EndIf
											EndIf
										Next
									EndIf
								EndIf
							EndIf
						Next
						
					EndIf
				EndIf
			Next
		EndIf
		
		Return pData
	EndFunction
	
EndType

Const PROPERTY_STRING=0
Const PROPERTY_CHOICE=1
Const PROPERTY_CHOICEEDIT=2
Const PROPERTY_INTEGER=3
Const PROPERTY_FLOAT=4
Const PROPERTY_BOOL=5
Const PROPERTY_FILE=6
Const PROPERTY_PATH:Int=7
Const PROPERTY_COLOR:Int=8
Const PROPERTY_VEC2:Int=9
Const PROPERTY_VEC3:Int=10
Const PROPERTY_VEC4:Int=11

Type TPropertyGroup {expose}

	Const labelwidth:Int=100
	Const itemheight:Int=20
	Const indent:Int=itemheight
	Const itemspacing:Int=2
	Const numberitemspacing:Int=50
	Const numberitemsliderwidth:Int=12
	
	Field link:TLink
	Field items:TList=New TList
	Field grid:TPropertyGrid
	Field headerlabel:TGadget
	Field imagepanel:TGadget
	Field header:TGadget
	Field panel:TGadget
	Field sidepanel:TGadget
	Field x:Int=0
	Field y:Int=itemspacing
	Field h:Int=2
	Field collapsed:Int=1
	Field enterevents:Int
	Field name:String
	Field hidden:Int
	
	Method hide(update=1)
		hidden=True
		If update grid.AdjustLayout()
	EndMethod
	
	Method ShowAll()
		show(0)
		For Local item:TProperty=EachIn items
			item.show(0)
		Next
	EndMethod
	
	Method Show(update=1)
		hidden=False
		If update grid.AdjustLayout()
	EndMethod
	
	Method Free()
		For Local item:TProperty=EachIn items
			item.free()
		Next
		FreeGadget imagepanel
		FreeGadget sidepanel
		FreeGadget panel
		FreeGadget header	
		If grid.sections.valueforkey(name)=Self
			grid.sections.remove(name)
		EndIf
		If link
			link.remove()
			link=Null
		EndIf
		items.clear()
		grid.AdjustLayout()
		grid=Null
	EndMethod

	Method SetFont(font:TGUIFont)
		SetGadgetFont headerlabel,font
		For Local item:TProperty=EachIn items
			item.setfont(font)
		Next
	EndMethod

	Method Sort()
		Local y:Int=itemspacing
		SortList(items)
		AdjustLayout()
	EndMethod
	
	Method AdjustLayout()
		Local y:Int=itemspacing
		Local n:Int
		Local count:Int
		count=items.count()
		For Local item:TProperty=EachIn items
			If item.hidden
				HideGadget item.label
				HideGadget item.control
				If item.control2 HideGadget item.control2
				If item.helper[0] HideGadget item.helper[0]
				If item.helper[1] HideGadget item.helper[1]
			Else
				SetGadgetShape item.label,GadgetX(item.label),y,GadgetWidth(item.label),GadgetHeight(item.label)
				SetGadgetShape item.control,GadgetX(item.control),y,GadgetWidth(item.control),GadgetHeight(item.control)
				If item.control2
					SetGadgetShape item.control2,GadgetX(item.control2),y,GadgetWidth(item.control2),GadgetHeight(item.control2)
				EndIf
				If item.helper[0]
					SetGadgetShape item.helper[0],GadgetX(item.helper[0]),y,GadgetWidth(item.helper[0]),GadgetHeight(item.helper[0])
				EndIf
				ShowGadget item.label
				ShowGadget item.control
				If item.control2 ShowGadget item.control2
				If item.helper[0] ShowGadget item.helper[0]
				If item.helper[1] ShowGadget item.helper[1]
				y:+itemheight+itemspacing
				n:+1
			EndIf
		Next
		SetGadgetShape panel,panel.xpos,panel.ypos,panel.width,y
		'SetPanelColor panel,255,0,0
	EndMethod
	
	'Method UpdateColor()
	'	If enterevents>0
	'		SetGadgetColor headerlabel,grid.color_headertexthover[0],grid.color_headertexthover[1],grid.color_headertexthover[2],0
	'	Else
	'		SetGadgetColor headerlabel,grid.color_headertext[0],grid.color_headertext[1],grid.color_headertext[2],0
	'	EndIf
	'EndMethod
	
	Method EventHook:TEvent( event:TEvent )
		Select event.id
			'Case EVENT_MOUSELEAVE
			'	enterevents:-1
			'	UpdateCOlor
			'Case EVENT_MOUSEENTER
			'	enterevents:+1
			'	UpdateColor
			Case EVENT_MOUSEDOWN
				SetGadgetColor imagepanel,grid.color_headertexthover[0],grid.color_headertexthover[1],grid.color_headertexthover[2],1
				SetGadgetColor header,grid.color_headertexthover[0],grid.color_headertexthover[1],grid.color_headertexthover[2],1
				Return Null
			Case EVENT_MOUSEUP
				toggle()
				SetGadgetColor imagepanel,grid.headercolor[0],grid.headercolor[1],grid.headercolor[2],1
				SetGadgetColor header,grid.headercolor[0],grid.headercolor[1],grid.headercolor[2],1
				Return Null
		EndSelect
		Return Null
	EndMethod

	Method Toggle()
		If collapsed Expand() Else Collapse()
	EndMethod
	
	Method Collapse(update=1)
		collapsed=True
		If update grid.AdjustLayout()
	EndMethod
	
	Method Expand(update=1)
		collapsed=False
		'For Local gs:TPropertyGroup=EachIn grid.groups
		'	If gs<>Self gs.hidden=True
		'Next
		If update grid.AdjustLayout()
	EndMethod

	Function Filter_Vector(event:TEvent,context:Object)
		If event.ID=EVENT_KEYCHAR
			If event.data=KEY_BACKSPACE Return 1
			If event.data=44 Return 1
			If event.data=47 Return 0
			If event.data<45 Or event.data>57 Return 0
		EndIf
		Return 1
	EndFunction

	Function Filter_Color(event:TEvent,context:Object)
		If event.ID=EVENT_KEYCHAR
			If event.data=KEY_BACKSPACE Return 1
			If event.data=44 Return 1
			If event.data<48 Or event.data>57 Return 0
		EndIf
		Return 1
	EndFunction
	
	Function Filter_Integer(event:TEvent,context:Object)
		If event.ID=EVENT_KEYCHAR
			If event.data=KEY_BACKSPACE Return 1
			If event.data<48 Or event.data>57 Return 0
		EndIf
		Return 1
	EndFunction

	Function Filter_Float(event:TEvent,context:Object)
		If event.ID=EVENT_KEYCHAR
			If event.data=KEY_BACKSPACE Return 1
			If event.data=47 Return 0
			If event.data<45 Or event.data>57 Return 0
		EndIf
		Return 1
	EndFunction
	
	Method AddProperty:TProperty(name:String,value:String="",style:Int=0,tooltip:String="",flags:Int=0,label:String="")
		Local griditem:TProperty=New TProperty
		items.addlast(griditem)
		
		ShowGadget sidepanel

		If Not label label=name
		
		griditem.style=style
		griditem.flags=flags
		
		griditem.label=CreateLabel(label,indent+x,y,labelwidth,20,panel)
		
		griditem.group=Self
		
		SetGadgetLayout griditem.label,1,0,1,0
		SetGadgetColor griditem.label,grid.background[0],grid.background[1],grid.background[2],1
		SetGadgetColor griditem.label,grid.color_itemtext[0],grid.color_itemtext[1],grid.color_itemtext[2],0
		
		Local sarr:String[]=value.split("|")
		Local s:String,i:Int
		
		griditem.value=sarr[0]
		
		If sarr.length>1
			griditem.extra=sarr[1]
		EndIf
		
		griditem.name=name'Self.name+"_"+name'uncomment this for category+name
		grid.properties.insert(griditem.name.tolower(),griditem)
		
		'griditem.imagepanel=CreatePanel(0,y-itemspacing,itemheight,itemheight+itemspacing,panel)
		'SetGadgetLayout griditem.imagepanel,1,0,1,0
		'SetPanelColor griditem.imagepanel,0,0,255
		'SetGadgetPixmap griditem.imagepanel,LoadPixmap("tree.bmp")
		
		Select style

			Case PROPERTY_VEC2
				sarr=value.split(",")
				griditem.control=CreateTextField(indent+x+labelwidth,y,panel.ClientWidth()-x-labelwidth-2-indent,itemheight,panel)
				SetGadgetLayout griditem.control,1,1,1,0
				SetGadgetFilter(griditem.control,Filter_Vector)
				If sarr.length>1 griditem.setvaluevec2(Float(sarr[0]),Float(sarr[1])) Else griditem.setvaluevec2(0,0)
				
			Case PROPERTY_VEC3
				sarr=value.split(",")
				griditem.control=CreateTextField(indent+x+labelwidth,y,panel.ClientWidth()-x-labelwidth-2-indent,itemheight,panel)
				SetGadgetLayout griditem.control,1,1,1,0
				SetGadgetFilter(griditem.control,Filter_Vector)
				If sarr.length>2 griditem.setvaluevec3(Float(sarr[0]),Float(sarr[1]),Float(sarr[2])) Else griditem.setvaluevec3(0,0,0)
	
			Case PROPERTY_VEC4
				sarr=value.split(",")
				griditem.control=CreateTextField(indent+x+labelwidth,y,panel.ClientWidth()-x-labelwidth-2-indent,itemheight,panel)
				SetGadgetLayout griditem.control,1,1,1,0
				SetGadgetFilter(griditem.control,Filter_Vector)
				If sarr.length>3 griditem.setvaluevec4(Float(sarr[0]),Float(sarr[1]),Float(sarr[2]),Float(sarr[3])) Else griditem.setvaluevec4(0,0,0,0)
			
			Case PROPERTY_COLOR
				'griditem.control=CreateColorPicker(indent+x+labelwidth,y,panel.ClientWidth()-x-labelwidth-2-indent-itemheight,itemheight,panel)
				'SetGadgetLayout griditem.control,1,1,1,0
				'SetGadgetToolTip griditem.control,"Select a color"
				'Notify GadgetY(griditem.control)+", "+y
				
				'SetGadgetColor griditem.control,sarr[0]
				'griditem.control2=CreateButton("",GadgetX(griditem.control)+GadgetWidth(griditem.control),y,itemheight,itemheight,panel)
				
				'SetGadgetLayout griditem.control2,0,1,1,0
				'SetGadgetColor griditem.control2,255,255,255
				'SetGadgetFilter(griditem.control,Filter_Color)
				'Rem
				griditem.control=CreateTextField(indent+x+labelwidth,y,panel.ClientWidth()-x-labelwidth-2-indent-itemheight,itemheight,panel)
				SetGadgetLayout griditem.control,1,1,1,0
				griditem.control2=CreateButton("",GadgetX(griditem.control)+GadgetWidth(griditem.control),y,itemheight,itemheight,panel)
				SetGadgetToolTip griditem.control2,"Select a color"
				SetGadgetLayout griditem.control2,0,1,1,0
				sarr=griditem.value.split(",")
				If sarr.length=3
					griditem.value=Int(sarr[0])+","+Int(sarr[1])+","+Int(sarr[2])
				Else
					griditem.value="0,0,0"
				EndIf
				SetGadgetText griditem.control,griditem.value
				sarr=griditem.value.split(",")
				SetGadgetColor griditem.control2,Int(sarr[0]),Int(sarr[1]),Int(sarr[2])
				SetGadgetFilter(griditem.control,Filter_Color)
				
				'EndRem
			
			Case PROPERTY_BOOL
				griditem.control=CreateButton("True",indent+x+labelwidth+2,y-1,panel.ClientWidth()-x-labelwidth-2-indent,itemheight,panel,BUTTON_CHECKBOX)
				SetButtonState griditem.control,Int(sarr[0])
				SetGadgetLayout griditem.control,1,1,1,0
				griditem.setvaluebool(Int(griditem.value))
				
			Case PROPERTY_FILE
				griditem.control=CreateTextField(indent+x+labelwidth,y,panel.ClientWidth()-x-labelwidth-2-indent-itemheight,itemheight,panel)
				SetGadgetText griditem.control,sarr[0]
				SetGadgetLayout griditem.control,1,1,1,0
				griditem.control2=CreateButton("...",GadgetX(griditem.control)+GadgetWidth(griditem.control),y,itemheight,itemheight,panel)
				SetGadgetPixmap griditem.control2,LoadPixmap("incbin::folder.tga")
				SetGadgetToolTip griditem.control2,"Select file"
				SetGadgetLayout griditem.control2,0,1,1,0

			Case PROPERTY_PATH
				griditem.control=CreateTextField(indent+x+labelwidth,y,panel.ClientWidth()-x-labelwidth-2-indent-itemheight,itemheight,panel)
				SetGadgetText griditem.control,sarr[0]
				SetGadgetLayout griditem.control,1,1,1,0
				griditem.control2=CreateButton("...",GadgetX(griditem.control)+GadgetWidth(griditem.control),y,itemheight,itemheight,panel)
				SetGadgetPixmap griditem.control2,LoadPixmap("incbin::folder.tga")
				SetGadgetToolTip griditem.control2,"Select path"
				SetGadgetLayout griditem.control2,0,1,1,0
				
			Case PROPERTY_STRING
				griditem.control=CreateTextField(indent+x+labelwidth,y,panel.ClientWidth()-x-labelwidth-2-indent,itemheight,panel)
				SetGadgetText griditem.control,sarr[0]
				SetGadgetLayout griditem.control,1,1,1,0
			
			Case PROPERTY_INTEGER
				If sarr.length>1
					sarr=sarr[1].split(",")
				EndIf
				If sarr.length>1
					griditem.control=CreateTextField(indent+x+labelwidth,y,numberitemspacing-numberitemsliderwidth,itemheight,panel)
					SetGadgetLayout griditem.control,1,0,1,0
					griditem.control2=CreateSlider(indent+x+labelwidth+numberitemspacing,y,panel.ClientWidth()-GadgetX(griditem.control)-numberitemspacing,itemheight,panel,SLIDER_HORIZONTAL|SLIDER_TRACKBAR)
					griditem.range[0]=Int(sarr[0])
					griditem.range[1]=Int(sarr[1])-griditem.range[0]+1
					SetGadgetLayout griditem.control2,1,1,1,0
					SetSliderRange griditem.control2,1,griditem.range[1]
					griditem.helper[0]=CreateSlider(GadgetX(griditem.control)+GadgetWidth(griditem.control),y,12,itemheight,panel,SLIDER_VERTICAL)
					SetGadgetLayout griditem.helper[0],1,0,1,0					
					SetSliderRange griditem.helper[0],1,griditem.range[1]
				Else
					griditem.control=CreateTextField(indent+x+labelwidth,y,panel.ClientWidth()-x-labelwidth-2-indent,itemheight,panel)
					SetGadgetLayout griditem.control,1,1,1,0
				EndIf
				griditem.setvalueinteger(Int(griditem.value))
				SetGadgetFilter(griditem.control,Filter_Integer)
				
			Case PROPERTY_FLOAT
				If sarr.length>1
					sarr=sarr[1].split(",")
				EndIf
				If sarr.length>1
					griditem.control=CreateTextField(indent+x+labelwidth,y,numberitemspacing-numberitemsliderwidth,itemheight,panel)
					SetGadgetLayout griditem.control,1,0,1,0
					If sarr.length>2 griditem.floatitemmultiplier=Float(sarr[2])
					griditem.control2=CreateSlider(indent+x+labelwidth+numberitemspacing,y,panel.ClientWidth()-GadgetX(griditem.control)-numberitemspacing,itemheight,panel,SLIDER_HORIZONTAL|SLIDER_TRACKBAR)
					griditem.range[0]=Int(Float(sarr[0])*griditem.floatitemmultiplier)
					griditem.range[1]=Int(Float(sarr[1])*griditem.floatitemmultiplier)-griditem.range[0]+1
					SetGadgetLayout griditem.control2,1,1,1,0
					SetSliderRange griditem.control2,1,griditem.range[1]
					griditem.helper[0]=CreateSlider(GadgetX(griditem.control)+GadgetWidth(griditem.control),y,12,itemheight,panel,SLIDER_VERTICAL)
					SetGadgetLayout griditem.helper[0],1,0,1,0					
					SetSliderRange griditem.helper[0],1,griditem.range[1]
				Else
					griditem.control=CreateTextField(indent+x+labelwidth,y,panel.ClientWidth()-x-labelwidth-2-indent,itemheight,panel)
					SetGadgetLayout griditem.control,1,1,1,0
				EndIf
				griditem.setvaluefloat(Float(griditem.value))
				SetGadgetFilter(griditem.control,Filter_Float)
				Rem
				griditem.control=CreateTextField(indent+x+labelwidth,y,panel.ClientWidth()-x-labelwidth-2-indent,itemheight,panel)
				SetGadgetText griditem.control,FloatToString(Float(sarr[0]))
				SetGadgetFilter(griditem.control,Filter_Integer)
				SetGadgetLayout griditem.control,1,1,1,0
				EndRem
				
			Case PROPERTY_CHOICE,PROPERTY_CHOICEEDIT
				i=Int(sarr[0])
				value=sarr[0]
				If style=PROPERTY_CHOICE
					griditem.control=CreateComboBox(indent+x+labelwidth,y,panel.ClientWidth()-x-labelwidth-2-indent,itemheight,panel)
				Else
					griditem.control=CreateComboBox(indent+x+labelwidth,y,panel.ClientWidth()-x-labelwidth-2-indent,itemheight,panel,COMBOBOX_EDITABLE)
				EndIf
				?win32
				SendMessageA(QueryGadget(griditem.control,QUERY_HWND),CB_SETITEMHEIGHT,-1,itemheight-5);
				?
				If sarr.length>1
					sarr=sarr[1].split(",")
					For s=EachIn sarr
						AddGadgetItem(griditem.control,s)
					Next
				EndIf
				If style=1
					If i<CountGadgetItems(griditem.control) And i>-2
						SelectGadgetItem griditem.control,i
					EndIf
				Else
					SetGadgetText griditem.control,value
				EndIf
				SetGadgetLayout griditem.control,1,1,1,0
		EndSelect
		
		'SetGadgetColor griditem.control,128,128,128,1
		
		'SetGadgetColor griditem.control,grid.background[0],grid.background[1],grid.background[2],1
		'If griditem.control2 SetGadgetColor griditem.control2,grid.background[0],grid.background[1],grid.background[2],0
		'If griditem.helper[0] SetGadgetColor griditem.helper[0],grid.background[0],grid.background[1],grid.background[2],0
		
		SetGadgetToolTip griditem.control,tooltip
		
		If grid.font griditem.SetFont(grid.font)
		
		y:+itemheight+itemspacing
		grid.AdjustLayout()
		
		Return griditem
	EndMethod	
EndType


Type TProperty
	
	Field name:String
	Field label:TGadget
	Field flags:Int
	Field style:Int
	Field control:TGadget
	Field control2:TGadget
	Field imagepanel:TGadget
	Field value:String
	Field extra:String
	Field helper:TGadget[2]
	Field range:Int[2]
	Field floatitemmultiplier:Float=10.0
	Field hidden:Int=0
	Field group:TPropertyGroup
	Field undetermined:Int

	Const undeterminedtext:String="<different>"

	Method MakeUndetermined()
		Select style
			Case PROPERTY_STRING,PROPERTY_INTEGER,PROPERTY_FLOAT,PROPERTY_FILE,PROPERTY_PATH,PROPERTY_COLOR,PROPERTY_VEC2,PROPERTY_VEC3,PROPERTY_VEC4,PROPERTY_CHOICEEDIT
				SetGadgetText control,undeterminedtext
			Case PROPERTY_CHOICE
				SelectGadgetItem control,-1
			Case PROPERTY_BOOL
				SetButtonState control,-1
		EndSelect
		undetermined=1
	EndMethod
	
	Method Hide(update=1)
		hidden=1
		If update group.grid.AdjustLayout()
	EndMethod
	
	Method Show(update=1)
		hidden=0
		If update group.grid.AdjustLayout()
	EndMethod
	
	Method Free()
		FreeGadget control
		If control2 FreeGadget control2
		If helper[0] FreeGadget helper[0]
		If helper[1] FreeGadget helper[1]
		If group.grid.properties.valueforkey(name.tolower())=Self
			group.grid.properties.remove(name.tolower())
		EndIf
		If group.grid.currentitem=Self group.grid.currentitem=Null
		group=Null
	EndMethod
	
	Method Compare:Int(o:Object)
		Return GadgetText(label).compare(GadgetText(TProperty(o).label))
	EndMethod
	
	Method SetFont(font:TGUIFont)
		If label SetGadgetFont label,font
		If control SetGadgetFont control,font
		If control2 SetGadgetFont control2,font
		If helper[0] SetGadgetFont helper[0],font
		If helper[1] SetGadgetFont helper[1],font
	EndMethod
	
	Method EventHook:TEvent( event:TEvent,grid:TPropertyGrid )
		Local s:String
		Local ovalue:String=value
		Local wasundetermined:Int
		
		wasundetermined=undetermined
		
		Rem
		If event.id=EVENT_GADGETLOSTFOCUS
			SetGadgetColor label,0,0,0,0
			group.grid.currentitem=Null
		Else
			If group.grid.currentitem
				SetGadgetColor group.grid.currentitem.label,0,0,0,0
			EndIf
			SetGadgetColor label,0,0,255,0
			group.grid.currentitem=Self
		EndIf
		EndRem
		
		Select event.source
			Case control
				Select style
					
					Case PROPERTY_STRING,PROPERTY_FILE,PROPERTY_PATH
						If event.id=EVENT_GADGETLOSTFOCUS
							setvaluestring(GadgetText(control))
						EndIf
					
					Case PROPERTY_INTEGER
						If event.id=EVENT_GADGETLOSTFOCUS
							SetValueInteger(Int(GadgetText(control)))
						EndIf

					Case PROPERTY_FLOAT
						If event.id=EVENT_GADGETLOSTFOCUS
							SetValueFloat(Float(GadgetText(control)))
						EndIf
					
					Case PROPERTY_VEC2
						If event.id=EVENT_GADGETLOSTFOCUS
							Local sarr:String[]=GadgetText(control).split(",")
							If sarr.length<>2
								If undetermined
									SetGadgetText control,undeterminedtext
								Else
									SetGadgetText control,value
								EndIf
							Else
								SetValueVec2(Float(sarr[0]),Float(sarr[1]))

							EndIf
						EndIf					
					
					Case PROPERTY_VEC4
						If event.id=EVENT_GADGETLOSTFOCUS
							Local sarr:String[]=GadgetText(control).split(",")
							If sarr.length<>4
								If undetermined
									SetGadgetText control,undeterminedtext
								Else
									SetGadgetText control,value
								EndIf
							Else
								SetValueVec4(Float(sarr[0]),Float(sarr[1]),Float(sarr[2]),Float(sarr[3]))

							EndIf
						EndIf
												
					Case PROPERTY_VEC3
						If event.id=EVENT_GADGETLOSTFOCUS
							Local sarr:String[]=GadgetText(control).split(",")
							If sarr.length<>3
								If undetermined
									SetGadgetText control,undeterminedtext
								Else
									SetGadgetText control,value
								EndIf
							Else
								SetValueVec3(Float(sarr[0]),Float(sarr[1]),Float(sarr[2]))

							EndIf
						EndIf
					
					Case PROPERTY_COLOR
						If event.id=EVENT_GADGETLOSTFOCUS
							Local sarr:String[]=GadgetText(control).split(",")
							If sarr.length=3
								SetValueColor(Int(sarr[0]),Int(sarr[1]),Int(sarr[2]))
							Else
								If undetermined
									SetGadgetText control,undeterminedtext
								Else
									SetGadgetText control,value
								EndIf
							EndIf
						EndIf

					Case PROPERTY_CHOICEEDIT
						If event.id=EVENT_GADGETACTION
							SetValueString(GadgetText(control))
						EndIf
						
					Case PROPERTY_CHOICE
						If event.id=EVENT_GADGETACTION SetValueChoice(SelectedGadgetItem(control))
					
					Case PROPERTY_BOOL
						If event.id=EVENT_GADGETACTION SetValueBool(event.data)

				EndSelect
			
			Case helper[0]
				Select style
					Case PROPERTY_INTEGER
						Local i:Int=range[1]-(SliderValue(helper[0])-range[0]+1)
						SetValueInteger(i)

					Case PROPERTY_FLOAT
						Local i:Int=range[1]-(SliderValue(helper[0])-range[0]+1)
						SetValueFloat(i/floatitemmultiplier)
				
				EndSelect
				
			Case control2
				Select style
					
					Case PROPERTY_INTEGER
						Local i:Int=SliderValue(control2)+range[0]-1
						SetValueInteger(i)

					Case PROPERTY_FLOAT
						Local i:Int=(SliderValue(control2)+range[0]-1)
						SetValueFloat(i/floatitemmultiplier)
					
					Case PROPERTY_FILE
						s=RequestFile("Select File",extra,0,GadgetText(control))
						If s
							If (1 & flags) s=StripDir(s)
							setvaluestring(s)
						EndIf
						
					Case PROPERTY_PATH
						s=RequestDir("Select path",GadgetText(control))
						If s setvaluestring(s)
					
					Case PROPERTY_COLOR
						Local sarr:String[]=value.split(",")
						If RequestColor(Int(sarr[0]),Int(sarr[1]),Int(sarr[2])) SetValueColor(RequestedRed(),RequestedGreen(),RequestedBlue())
					
				EndSelect
		EndSelect
		
		If Not undetermined
			If value<>ovalue Or wasundetermined=1
				Return CreateEvent(EVENT_GADGETACTION,grid,0,0,0,0,name+"="+value)
			EndIf
		EndIf
		
		Return Null
	EndMethod

	Method SetValueColor(r:Int,g:Int,b:Int)
		undetermined=0
		SetGadgetColor control2,r,g,b
		value=r+","+g+","+b
		SetGadgetText control,value
	EndMethod
	
	Method SetValueFloat(i:Float)
		undetermined=0
		If control2
			i=Round(i*floatitemmultiplier)/floatitemmultiplier
			i:*floatitemmultiplier
			If i<range[0] i=range[0]
			If i>range[0]+range[1]-1 i=range[0]+range[1]-1
			value=FloatToString(i/floatitemmultiplier)
			SetGadgetText control,value
			If control2 SetSliderValue control2,i-range[0]+1
			If helper[0] SetSliderValue helper[0],range[1]-(i-range[0]+1)
		Else
			value=FloatToString(i)
			SetGadgetText control,value
		EndIf
	EndMethod
	
	Method SetValueChoice(choice:Int)
		undetermined=0
		If choice<0 choice=0
		If choice>CountGadgetItems(control)-1 choice=CountGadgetItems(control)-1
		value=String(choice)
		SelectGadgetItem control,choice
	EndMethod
	
	Method SetValueInteger(i:Int)
		undetermined=0
		If control2
			If i<range[0] i=range[0]
			If i>range[0]+range[1]-1 i=range[0]+range[1]-1
		EndIf
		value=String(i)
		SetGadgetText control,value
		If control2 SetSliderValue control2,i-range[0]+1
		If helper[0] SetSliderValue helper[0],range[1]-(i-range[0]+1)
	EndMethod

	Method SetValueVec2(x#,y#)
		undetermined=0
		value=floattostring(x)+","+floattostring(y)
		SetGadgetText control,value
	EndMethod
	
	Method SetValueVec3(x#,y#,z#)
		undetermined=0
		value=floattostring(x)+","+floattostring(y)+","+floattostring(z)
		SetGadgetText control,value
	EndMethod

	Method SetValueVec4(x#,y#,z#,w#)
		undetermined=0
		value=floattostring(x)+","+floattostring(y)+","+floattostring(z)+","+floattostring(w)
		SetGadgetText control,value
	EndMethod

	Method SetValueString(s$)
		undetermined=0
		value=s
		If GadgetText(control)<>value SetGadgetText control,value
	EndMethod
	
	Method SetValueBool(b:Int)
		undetermined=0
		If b b=1
		value=String(b)
		SetButtonState control,b
		If b SetGadgetText control,"True" Else SetGadgetText control,"False"	
	EndMethod
	
	Method SetValue(s$)
		Local i:Int
		Local sarr:String[]
		undetermined=False
		Select style
			Case PROPERTY_STRING,PROPERTY_CHOICEEDIT,PROPERTY_PATH
				SetValueString(s)
			Case PROPERTY_FILE
				If (1 & flags) s=StripDir(s)
				SetValueString(s)
			Case PROPERTY_CHOICE
				SetValueString(Int(s))
			Case PROPERTY_INTEGER
				SetValueInteger(Int(s))
			Case PROPERTY_FLOAT
				SetValueFloat(Float(s))
			Case PROPERTY_BOOL
				SetValueBool(Int(s))
			Case PROPERTY_COLOR
				sarr=s.split(",")[..3]
				SetValueColor(Int(sarr[0]),Int(sarr[1]),Int(sarr[2]))
			Case PROPERTY_VEC2
				sarr=s.split(",")[..2]
				SetValueVec2(Float(sarr[0]),Float(sarr[1]))				
			Case PROPERTY_VEC3
				sarr=s.split(",")[..3]
				SetValueVec3(Float(sarr[0]),Float(sarr[1]),Float(sarr[2]))
			Case PROPERTY_VEC4
				sarr=s.split(",")[..4]
				SetValueVec4(Float(sarr[0]),Float(sarr[1]),Float(sarr[2]),Float(sarr[3]))				
		EndSelect
	EndMethod
	
EndType

Private

Function Round(val#)
	Local dec#
	dec#=val-Floor(val)
	If dec<0.5 Return Floor(val) Else Return Ceil(val)
EndFunction

Function FloatToString:String(value:Float,places=3)
	Local sign=Sgn(value)
	value=Abs(value)
	Local i=Round(value*10^places)
	Local ipart=Int(i/10^places)
	Local dpart=i-ipart*10^places
	Local si$=ipart
	Local di$
	If dpart>0
		di=dpart
		While di.length<places
			di="0"+di
		Wend
		di="."+di
	EndIf
	While Right(di,1)="0"
		di=Left(di,di.length-1)
	Wend
	If di="" di=".0"
	If sign=-1 si="-"+si
	Return si+di
EndFunction

Public
