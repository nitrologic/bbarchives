; ID: 2290
; Author: degac
; Date: 2008-07-21 19:46:37
; Title: [MaxGUI] MultiButtonList
; Description: A special list box with checkbox, label, tooltips, and scrolling

Rem

		test for MyGadget
		v. 1.01 - bug: check array size
End Rem
Import maxgui.drivers

Global wndMain:TGadget = CreateWindow( AppTitle , 100 , 100 , 700 , 400) 

Local bu1:tgadget,bu2:tgadget,bu3:tgadget,bu4:tgadget
Local bu5:tgadget,bu6:tgadget,bu7:tgadget,bu8:tgadget


bu1 = CreateButton("Disable" , 10 , 220 , 100 , 20 , wndMain) 
bu2 = CreateButton("Enable" , 120 , 220 , 100 , 20 , wndMain) 
bu3 = CreateButton("Clear" , 10 , 240 , 100 , 20 , wndMain) 
bu4 = CreateButton("Add items" , 120 , 240 , 100 , 20 , wndMain) 

bu5 = CreateButton("Colors" , 10 , 260 , 100 , 20 , wndMain) 
bu6 = CreateButton("Item col" , 120 , 260 , 100 , 20 , wndMain)
bu7 = CreateButton("Extract status" , 10 , 280 , 100 , 20 , wndMain)
bu8 = CreateButton("Invert Status" , 120 , 280 , 100 , 20 , wndMain)


Global mb1:tmultiButton = CreateMultiButton(10 , 10 , 270 , 200 , wndMain ) 

Global mb2:tmultiButton = CreateMultiButton(300 , 10 , 270 , 200 , wndMain ) 

For Local kk:Int = 0 To 14
	mb1.AddItem("Item " + kk , "Description " + kk , "Tips " + kk , "Tips Description " + kk) 
Next

For kk:Int = 0 To 14
	mb2.AddItem("Item " + kk , "Description " + kk , "Tips " + kk , "Tips Description " + kk) 
Next


mb1.InvertState()

'mb1.SetItemBackColor(3 , 200 , 10 , 10) 
'mb1.SetItemSelectColor(3 , 200 , 210 , 10)
'mb1.SetButtonColor(220 , 250 , 20) 

mb1.SetItemLabelColor(3,255,0,255)
Print mb1.getItemStatus(15)

Local tem:Int[]



Repeat
	
	WaitEvent()
	
	SetStatusText wndMain, CurrentEvent.ToString()
	
	Select EventID()
		Case EVENT_WINDOWCLOSE , EVENT_APPTERMINATE ; GotoEnd()
		
		Case EVENT_GADGETACTION	
			Select EventSource() 
			
				Case bu1
					mb1.Disable() 
				Case bu2
					mb1.Enable()
				Case bu3
					mb1.clearItems() 
				Case bu4
					
					For kk:Int = 0 To 14
						mb1.AddItem("Item " + kk , "Description " + kk , "Tips " + kk , "Tips Description " + kk) 
					Next

				Case bu5
				
					mb1.SetBackColor(Rnd(255) , Rnd(255) , Rnd(255) ) 
					mb1.SetSelectColor(Rnd(255) , Rnd(255) , Rnd(255) )
					
				Case bu6
				
					mb1.SetItemBackColor(Rand(mb1.array.length),Rnd(255) , Rnd(255) , Rnd(255) )

				Case bu7
					Print "Extract status..."				
					tem = mb1.ExtractStatus() 
						For Local k12:Int = EachIn tem
							Print k12
						Next
						Print "-------------------"
						
				Case bu8
					mb1.InvertState()
					
			End Select
		
	EndSelect
	
Forever

Function GotoEnd() 
	
	mb1.free() 
	End
	
	
	
End Function
		

Function CreateMultiButton:tmultibutton(x:Int , y:Int , w:Int , h:Int , win:tgadget , t1$ = "" , t2$ = "" , ti1$ = "" , ti2$ = "" , style:Int = 0) 
	Return tmultibutton.Create(x , y , w , h , win , t1 , t2 , t1 , t2 , style) 
End Function


Type TMultiButton Extends TProxyGadget
	
	Field panel:tgadget
	Field button:tgadget
	Field label:tgadget
	Field slider:tgadget
	Field line:tgadget
	
	Field parent:tMultiButton
	Field current_position:Int=1
	Field width:Int,he:Int
	Field visible:Int
	
	Field bcolor:Int[] = [188 , 188 , 188]
	Field scolor:Int[] = [200 , 200 , 200]
	Field tcolor:Int[] = [40 , 40 , 220]
	Field lcolor:Int[] = [0,0,0]
	
	Field counter:Int=0
	Field array:tmultibutton[]
	
	Function Create:TMultiButton( x:Int, y:Int, w:Int, h:Int, group:TGadget, t1$="",t2$="",tip1$="",tip2$="",style:Int=0 )
		Local mb:TMultiButton=New TMultiButton
		mb.panel = CreatePanel(x , y , w-20 , h , group , PANEL_ACTIVE) 
		' mb.button = CreateButton(t1$ , 5 , 1 , w - 5 , 16 , mb.panel , BUTTON_CHECKBOX) 
		'mb.label = CreateLabel(t2,23,18,w-5,16,mb.panel,LABEL_LEFT)
		mb.style = style
		mb.slider=CreateSlider(x+w-20,y,20,h,group,SLIDER_SCROLLBAR|SLIDER_VERTICAL)
		mb.parent = mb
		mb.width = w - 20
		mb.he = h
		mb.visible=Int(h/40)
		SetGadgetColor mb.panel , mb.bcolor[0],mb.bcolor[1],mb.bcolor[2]
		ActivateGadget mb.panel
		AddHook EmitEventHook,MultiButtonHook,mb
		Return mb
	EndFunction
	
	Method AddItem:tmultibutton(t1$ = "" , t2$ = "" , tp1$ = "" , tp2$ = "",_state:Int=1)
		
		If t1 = "" Return Null
		counter:+1
		array = array[..counter]
		array[counter - 1] =Self.CreateItem(t1,t2,tp1,tp2,Self,_state)
		'Print "Counter: "+counter
		
		Local num:Int = counter - Int(he / 40) + 1
		If slider SetSliderRange slider,0,num
		
		Return array[counter-1]
		
	End Method
	
	Method AddFromArray(title$[] , label$[] , state:Int[]) 
		
		If label.length <>title.length Or label.length <>state.length Or title.length <>state.length Return 0
		
		For Local c1:Int = 0 Until title.length
			
			AddItem(title[c1],label[c1],"","",state[c1])
						
		Next
		
	End Method
	
	Method SetButtonColor(r:Int = 188 , g:Int = 188 , b:Int = 188)
		tcolor = [r , g , b]
		For Local it:tmultibutton = EachIn array
			it.tcolor = [r , g , b]
			If it.button SetGadgetColor it.button,r,g,b,False			
			If it.panel ActivateGadget it.panel
		Next
		ActivateGadget panel
	End Method
	
	Method SetLabelColor(r:Int = 188 , g:Int = 188 , b:Int = 188)
		tcolor = [r , g , b]
		For Local it:tmultibutton = EachIn array
			it.tcolor = [r , g , b]
			If it.label SetGadgetColor it.label,r,g,b,False			
			If it.panel ActivateGadget it.panel
		Next
		ActivateGadget panel
	End Method

	
	
	Method SetBackColor(r:Int = 188 , g:Int = 188 , b:Int = 188)
		bcolor = [r , g , b]
		
		For Local it:tmultibutton = EachIn array
			it.bcolor = [r , g , b]
			If it.panel ActivateGadget it.panel
		Next
		ActivateGadget panel
		
	End Method
	
	Method SetSelectColor(r:Int = 200 , g:Int = 200 , b:Int = 200)
		scolor = [r , g , b]
		For Local it:tmultibutton = EachIn array
			it.scolor = [r , g , b]
			If it.panel ActivateGadget it.panel
		Next
		ActivateGadget panel

	End Method
	
	Method SetItemBackColor(item:Int = 0 , r:Int = 188 , g:Int = 188 , b:Int = 188)
		If array = Null Return
		
		If item < 0 Or item > array.length Return
		
		array[item].bColor = [r , g , b]
		
		
		
	End Method
	
	Method SetItemButtonColor(item:Int = 0 , r:Int = 255 , g:Int = 255 , b:Int = 255) 
		If array = Null Return
		
		If item < 0 Or item > array.length Return
		
		array[item].tColor = [r , g , b]
		If array[item].button SetGadgetColor array[item].button,r,g,b,False

	End Method 	
	
	Method SetItemLabelColor(item:Int = 0 , r:Int = 255 , g:Int = 255 , b:Int = 255) 
		If array = Null Return
		
		If item < 0 Or item > array.length Return
		
		array[item].tColor = [r , g , b]
		If array[item].label SetGadgetColor array[item].label,r,g,b,False

	End Method 	
	


	

	Method SetItemSelectColor(item:Int = 0 , r:Int = 200 , g:Int = 200 , b:Int = 200) 
		If array = Null Return
		
		If item < 0 Or item > array.length Return
		
		array[item].sColor = [r , g , b]

	End Method 	
	
	Method GetItemStatus:Int(item:Int = 0)
		If array = Null Return 0
		If item < 0 Or item > array.length-1 Return 0
		If array[item].button=Null Return 0
		Return ButtonState(array[item].button)
	End Method
	
	Method SetItemStatus:Int(item:Int = 0,state:Int=1)
		If array = Null Return 0
		If item < 0 Or item > array.length-1 Return 0
		If array[item].button=Null Return 0
		Return SetButtonState(array[item].button,state)
	End Method
	
	
	Method ExtractStatus:Int[]() 
		'returns an array of Int of all the checkbox
		If array = Null Return [0]
		Local temp:Int[] , con:Int
		temp=New Int[array.length]
		For Local what:tmultibutton = EachIn array
			If what.button
				temp[con] = ButtonState(what.button) 
			End If
			con:+ 1
		Next
		Return temp
		
	End Method
	
	Method SetTip(item:Int , tip$ = "",tip2$="") 
		If array = Null Return 0
		If item < 0 Or item > array.length-1 Return 0
		
		If array[item].button SetGadgetToolTip array[item].button,tip$
		If array[item].label SetGadgetToolTip array[item].label , tip2$	
			
	End Method
	
	Method DisableItem(item:Int = 0) 
		If array = Null Return 0
		If item < 0 Or item > array.length-1 Return 0
		If array[item].button DisableGadget array[item].button
		If array[item].label DisableGadget array[item].label
	End Method
	
	Method EnableItem(item:Int = 0) 
		If array = Null Return 0
		If item < 0 Or item > array.length-1 Return 0
		If array[item].button EnableGadget array[item].button
		If array[item].label EnableGadget array[item].label
	End Method

	
	
	Method CreateItem:tmultibutton(t1$ = "" , t2$ = "" , tp1$ = "" , tp2$ = "",parent:tmultibutton,_state:Int=1)  
		If parent=Null Return Null
		Local w:Int=parent.width	
		If parent.panel=Null Return Null
		
		Local h:Int = 40
		
		Local mb:tmultibutton = New tmultibutton
		mb.panel = CreatePanel(0 , 40*(counter-1) , w , 40 , parent.panel , PANEL_ACTIVE) 
		
		SetGadgetColor mb.panel , mb.bcolor[0] , mb.bcolor[1] , mb.bcolor[2]
			
		mb.button = CreateButton(t1$ , 5 , 1 , w - 5 , 16 , mb.panel , BUTTON_CHECKBOX) 
		mb.label = CreateLabel(t2,23,20,w-5,16,mb.panel,LABEL_LEFT)
		mb.line  = CreateLabel("",0,39,w+1,1,mb.panel,LABEL_SEPARATOR)
		
		SetGadgetColor mb.button , mb.tcolor[0] , mb.tcolor[1] , mb.tcolor[2] , False
		SetGadgetColor mb.label , mb.lcolor[0] , mb.lcolor[1] , mb.lcolor[2] , False

		
		SetGadgetToolTip mb.button , tp1
		SetGadgetToolTip mb.label , tp2
		SetButtonState mb.button,_state
		
		
	'	Print "Create item..."+counter
		
		Return mb
		
	End Method
	
	Method ChangeState(sta:Int = 0) 
		For Local c:tmultibutton = EachIn array
			If c.button SetButtonState c.button , sta
		Next
	End Method
	
	Method InvertState(sta:Int = 0) 
		For Local c:tmultibutton = EachIn array
			If c.button SetButtonState c.button , 1-ButtonState(c.button)
		Next
	End Method
	
	Method Disable()
		For Local c:tmultibutton = EachIn array
			If c.button DisableGadget c.button
			If c.label DisableGadget c.label
		Next

	End Method
	
	Method Enable()
		For Local c:tmultibutton = EachIn array
			If c.button EnableGadget c.button
			If c.label EnableGadget c.label
		Next
	End Method
	
	Method ClearItems() 
		
		For Local c:tmultibutton = EachIn array
			If c.button FreeGadget c.button
			If c.label FreeGadget c.label
			If c.line FreeGadget c.line
			If c.panel FreeGadget c.panel
			
		Next
		array = array[..0]
		counter=0
		
		Local num:Int = counter - Int(he / 40) + 1
		If slider SetSliderRange slider,0,num

		
		If panel ActivateGadget panel
	End Method
	
	Method Free()
		RemoveHook EmitEventHook,MultiButtonHook,Self
		'textField is freed in super
		ClearItems() 
		
		If button FreeGadget button
		If label FreeGadget label
		If slider FreeGadget slider
		If panel FreeGadget panel
		If line FreeGadget line
		
		button = Null
		label = Null
		slider = Null
		panel=Null
		Super.Free
	End Method

	Function MultiButtonHook:Object( id:Int, data:Object, context:Object )
		Local mb:TMultiButton 
		Local event:TEvent
		event=TEvent(data)
		If event
			mb=TMultiButton(context)	
			If mb Return mb.HandleEvents(event)
		EndIf
		Return data
	EndFunction
	
	Method HandleEvents:TEvent( event:TEvent ) 
		Local what:tmultibutton
		Local con:Int = 0
		Local datamove:Int
		
		
		Select event.id
		
			Case EVENT_MOUSEWHEEL
				If EventSource()=panel
					datamove = EventData() 
			
					current_position:+ datamove*-1
					
					If current_position < 0 current_position = 0
					If current_position > array.length-visible current_position=array.length-visible
					
					For what=EachIn array
						SetGadgetShape what.panel , 0 , con * 40 - current_position * 40 , width , 40
						con:+1
					Next
					
					SetSliderValue slider , current_position
			
				End If
		
		

			
		
		
			Case EVENT_MOUSELEAVE
				
	
				For what:tmultibutton=EachIn array
					SetGadgetColor what.panel , what.bcolor[0] , what.bcolor[1] , what.bcolor[2]
				
					If EventSource() = what.panel
						SetGadgetColor what.panel , what.scolor[0] , what.scolor[1] , what.scolor[2]
						ActivateGadget panel
						Continue
					End If
				con:+1

				Next
				
			Case EVENT_GADGETACTION
				
				If EventSource() = slider
					datamove:Int = EventData() 
					
					For what=EachIn array
						SetGadgetShape what.panel , 0 , con * 40 - datamove * 40 , width , 40
						con:+1
					Next

				
				End If
			
		EndSelect
	Return event
	EndMethod
EndType
