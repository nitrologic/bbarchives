; ID: 2563
; Author: degac
; Date: 2009-08-18 02:28:55
; Title: CheckListBox
; Description: Create a listbox gadget with checkbox

Rem
	checklistbox
	
	version 2.0 - degac
	
	18 agosto 2009
	
	2.1
		+ added IconStrip support
		+ added RemoveGadgetItem
		+ added SelectGadgetItem (-1 as index means ALL the items)
		+ added DeselectGadgetItem (-1 as index means ALL the items)
		+ added ToggleGadgetItem (-1 as index means ALL the items)

		
	
End Rem

SuperStrict
Import maxgui.drivers


	Local window:tgadget=CreateWindow("test 2",100,100,350,340,,WINDOW_TITLEBAR|WINDOW_CENTER)
	
	Local list1:tgadget=CreateCheckListBox( 10,10,150,200,window)
	Local list2:tgadget=CreateCheckListBox(180,10,150,200,window)
	
	Local but1:tgadget	=CreateButton("Clear all"	,10,220,100,25,window)
	Local but2:tgadget	=CreateButton("Copy"		,10,250,100,25,window)
	Local but3:tgadget	=CreateButton("Destroy"	,120,220,100,25,window)
	Local but4:tgadget	=CreateButton("Enable"	,120,250,100,25,window)
	Local but5:tgadget	=CreateButton("Disable"	,230,250,100,25,window)
	Local but6:tgadget	=CreateButton("Check all"	, 10,280,100,25,window)
	Local but7:tgadget	=CreateButton("UnCheck all"	,120,280,100,25,window)
	Local but8:tgadget	=CreateButton("Toggle all"	,230,280,100,25,window)
	
	Local ic:ticonstrip=LoadIconStrip("D:\_BlitzMax\MaxGUI_extra\toolbar.png")
	
	SetGadgetIconStrip list1,ic
	SetGadgetIconStrip list2,ic
	SetGadgetColor list1,200,100,90

	For Local ii:Int=1 To 20
		AddGadgetItem list1,"Item "+ii,Rand(0,1),(ii Mod 11),"Tip "+ii
	Next

	Print "Item in List1: "+CountGadgetItems(list1)
	
	ModifyGadgetItem list1,3,"---New 4",1,8,"This is new..."
	RemoveGadgetItem list1,4 ' it's the 5th items in the list (starting from 0...)
	
	Print GadgetItemText(list1,3)

	'DisableGadget list1
	'DeselectGadgetItem list1,-1
	'ToggleGadgetItem list1,1
	SelectGadgetItem list1,22
	
	
	While True
	WaitEvent 
	'Print CurrentEvent.ToString()
	Select EventID()
		Case EVENT_GADGETACTION	
			Select EventSource()
				Case but6 'check all
					SelectGadgetItem(list1,-1)
				Case but7 'uncheck all
					DeselectGadgetItem(list1,-1)	
				Case but8 'toggle item
					ToggleGadgetItem(list1,-1)			
			
				Case but1
					ClearGadgetItems list1
				Case but2
					
					ClearGadgetItems list2
					Local result:Int[]=SelectedGadgetItems(list1)
					For Local i:Int=EachIn result
							AddGadgetItem list2,GadgetItemText(list1,i),list1.ItemState(i),list1.ItemIcon(i),list1.ItemTip(i),GadgetItemExtra(list1,i)
					Next

				Case but3
					FreeGadget list1
					
				Case but5
					DisableGadget list1
				Case but4
					EnableGadget list1
				
					
				
			End Select
	
		Case EVENT_WINDOWCLOSE
			End
	End Select
Wend

End 


Function CreateCheckListBox:Tchecklistbox (x:Int , y:Int , w:Int , h:Int , win:tgadget , style:Int = 0) 
	Return Tchecklistbox .Create(x , y , w , h , win ,  style) 
End Function


Type SingleItem
	Field panel:tgadget
	Field check:tgadget
	Field label:tgadget
	Field icon:tgadget
	Field iconNumber:Int
	Field parent:tchecklistbox
	
	
		
	Function Create:SingleItem(parent:tchecklistbox,txt$,tip$="",icon:Int=-1,state:Int=0)
		Local ss:singleItem=New singleItem
		Local yy:Int=parent.it.length*20-20
			
		ss.panel=CreatePanel(0,yy,ClientWidth(parent.panel),20,parent.panel,PANEL_ACTIVE)
		ss.check=CreateButton("",5,0,16,19,ss.panel,BUTTON_CHECKBOX)
		ss.label=CreateLabel(txt,40,2,ClientWidth(ss.PANEL),20,SS.PANEL)
		ss.icon=CreatePanel(20,1,18,18,ss.panel)
		
		ss.parent=parent
		
		SetGadgetToolTip ss.label,tip
		SetButtonState ss.check,state
		Return SS
	End Function
	
	Method Change(txt$,tip$,_iconNumber:Int=-1,state:Int=-1)
	
		SetGadgetText label,txt
		SetGadgetToolTip label,tip
		If _iconNumber>-1
				Local px:TPixmap=PixmapFromIconStrip(parent.myiconstrip, _iconNumber)
				SetGadgetPixmap icon,px,PANELPIXMAP_CENTER
				iconNumber=_iconNumber
				
		End If

		
		If state<>-1 SetButtonState check,state	
	
	End Method
	
	Method Clear()
		If label FreeGadget label
		If check FreeGadget check
		If panel FreeGadget panel
		If icon FreeGadget icon
		
		icon=Null
		label=Null
		check=Null
		panel=Null	
	End Method

	Method SetEnabled(status:Int=True)
		
		If status=True
			EnableGadget label
			EnableGadget check
			EnableGadget panel
			EnableGadget icon
		Else
			DisableGadget label
			DisableGadget check
			DisableGadget panel
			DisableGadget icon

		End If
	
	End Method
	
End Type


Type Tchecklistbox Extends TProxyGadget

	Global list_checklistbox:TList

	Field myiconstrip:ticonstrip
	Field panel:tgadget
	Field slider:tgadget
	Field style:Int
	Field current_position:Int,visible:Int
	Field panel_width:Int
	Field it:singleItem[]
	
	Method InsertItem(index:Int,text:String,tip:String,icon:Int,extra:Object,flags:Int)
		
		?debug
		If index<0 Or index>it.length Throw "Index out of range"
		?	
		it=it[..it.length+1]
		For Local i:Int=it.length-2 To index Step -1
			it[i+1]=it[i]
		Next
		If it[index]<>Null 
				SetGadgetText it[index].label,text
				SetGadgetExtra it[index].label,extra
				If icon>-1
					Local px:TPixmap=PixmapFromIconStrip(myiconstrip, icon)
					SetGadgetPixmap it[index].icon,px,PANELPIXMAP_CENTER
					it[index].iconNumber=icon
				End If
		Else
			it[index]=SingleItem.Create(Self,text$,tip$,icon,flags)
			it[index].label.Extra=Extra
			If icon>-1
				Local px:TPixmap=PixmapFromIconStrip(myiconstrip, icon)
				SetGadgetPixmap it[index].icon,px,PANELPIXMAP_CENTER
				it[index].iconNumber=icon
			End If
		End If
		
		Local num:Int = it.length - visible + 1
		If slider SetSliderRange slider , 0 , num

		
	End Method
	
	Method SetIconStrip(_iconstrip:ticonStrip)
		myiconstrip=_iconStrip
	End Method
	
	Method SelectItem(index:Int=-1,op:Int=1)
		Rem
			if index=-1 SELECT ALL the items
			
			OP = 1	set CHECKED
			OP = 0	set UNCHEKED
			OP = 2	TOGGLE
		End Rem
		
		If index=-1
			If op=0 Or op=1
				For Local ss:singleitem=EachIn it
					If ss	SetButtonState(ss.check,OP)
				Next
			Else
				For Local ss:singleitem=EachIn it
					If ss	SetButtonState(ss.check,1-ButtonState(ss.check))
				Next
			
			End If
		Else
			
			If index<it.length 
				If OP=0 Or OP=1 
					SetButtonState(it[index].check,OP)
				Else
					SetButtonState(it[index].check,1-ButtonState(it[index].check))

				End If
			End If
			
		End If
					
		
	
	End Method
	
	
	
	Method SetEnabled(status:Int=True)
	
		For Local ss:singleitem=EachIn it
			ss.SetEnabled(status)
		Next

		If status=True
			EnableGadget slider
			EnableGadget panel
		Else
			DisableGadget slider
			DisableGadget panel
		End If
	End Method
	
	Method ItemCount:Int()
		Return it.length
	End Method
	
	Method RemoveItem:Int(index:Int)
		?debug
		If index<0 Or index>it.length Throw "Index out of range"
		?	
		
		it[index].Clear()
		it[index]=Null
		
		For Local t:Int=index+1 Until it.length
			it[t-1]=it[t]
		
		Next
		it=it[..it.length-1]
		
		'Replace position

		For Local t:Int=index Until it.length
			SetGadgetShape it[t].panel , 0 , t * 20, panel_width , 20
		Next
		If slider SetSliderRange slider , 0 , it.length - visible + 1


	End Method
	
	Method SetItem(index:Int,text:String,tip:String,icon:Int,extra:Object,flags:Int)
		?debug
		If index<0 Or index>it.length Throw "Index out of range"
		?	
		it[index].Change(text,tip,icon,flags)	
	End Method
	
	Method ItemText:String(index:Int)
		?debug
		If index<0 Or index>it.length Throw "Index out of range"
		?
		Return GadgetText(it[index].label)
	End Method
	
	Method ItemTip:String(index:Int)
		?debug
		If index<0 Or index>it.length Throw "Index out of range"
		?
		Return it[index].label.GetTooltip()
	End Method
	
	Method ItemExtra:Object(index:Int)
		?debug
		If index<0 Or index>it.length Throw "Index out of range"
		?
		Return GadgetExtra(it[index].label)

	End Method
	
	Method ItemIcon:Int(index:Int)
		?debug
				If index<0 Or index>=it.length Throw "Index out of range"
		?
		Return it[index].iconNumber
	End Method
	
	Method ItemState:Int(index:Int)
		?debug
		If index<0 Or index>it.length Throw "Index out of range"
		?
		Return ButtonState(it[index].check)
	End Method
	
	Method Clear()
		For Local ss:singleItem=EachIn it
			ss.clear()
			ss=Null
		Next
		it=Null
		
	End Method
	
	Method SetColor(r:Int,g:Int,b:Int)
		SetGadgetColor panel,r,g,b
		For Local si:singleitem=EachIn it
			SetGadgetColor si.panel,r,g,b
			SetGadgetColor si.label,r,g,b
			SetGadgetColor si.check,r,g,b
		Next	
	End Method
	
	Method SelectedItems:Int[]()
		'SELECED (=CHECKED) ITEMS
		Local count:Int,i:Int,array:Int[it.length]
		For i=0 Until it.length
			If it[i]
				If ButtonState(it[i].check)=True
					array[count]=i
					count:+1
				End If
			End If
		Next
		If count>0 Return array[..count]	

	End Method
	
	Method CleanUp()
		Clear()
		If slider FreeGadget slider
		If panel FreeGadget panel
		
		list_checklistbox.remove(Self)
		
	
	End Method
	
	Method Free()
		
		ClearList list_checklistbox
		list_checklistbox=Null
	End Method
	


	Function Create:Tchecklistbox ( x:Int, y:Int, w:Int, h:Int, group:TGadget,style:Int=0 )
		Local mb:Tchecklistbox =New Tchecklistbox 
		mb.panel = CreatePanel(x , y , w-20 , h , group , PANEL_ACTIVE) 
		mb.style = style
		mb.slider=CreateSlider(x+w-20,y,20,h,group,SLIDER_SCROLLBAR|SLIDER_VERTICAL)
		SetGadgetColor mb.panel,255,255,255
		ActivateGadget mb.panel
		AddHook EmitEventHook,_Hook,mb
		mb.setproxy(mb.panel)
		If list_checklistbox=Null list_checklistbox=New TList
		list_checklistbox.addlast mb
		
		mb.panel_width=ClientWidth(mb.panel)
		mb.visible=Int(h/20)
		
		Return mb
	EndFunction

	Function _Hook:Object( id:Int, data:Object, context:Object )
		Local mb:Tchecklistbox 
		Local event:TEvent
		event=TEvent(data)
		If event
			mb=Tchecklistbox (context)	
			If mb Return mb.HandleEvents(event)
		EndIf
		Return data
	EndFunction
	
	Method HandleEvents:TEvent( event:TEvent ) 
		Local what:Tchecklistbox 
		Local con:Int = 0
		Local datamove:Int
		
		
		Select event.id
			'mouse_wheel for changing position?
									
			Case EVENT_GADGETACTION
							
				If EventSource() = slider
				
					current_position = SliderValue(slider)'EventData() 
					
					If current_position < 0 current_position = 0
					If current_position > it.length current_position=it.length
					Local con:Int=0
					For Local what:singleitem=EachIn it
						SetGadgetShape what.panel , 0 , con * 20 - current_position* 20 , panel_width , 20
						con:+1
					Next

				
				End If
				
		EndSelect
	Return event
	EndMethod

End Type
