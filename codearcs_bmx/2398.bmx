; ID: 2398
; Author: Otus
; Date: 2009-01-23 23:16:31
; Title: Auto Form
; Description: Automatically input data to an object using MaxGUI

SuperStrict

Import BRL.EventQueue
Import BRL.Reflection
Import MaxGUI.MaxGUI

Private

' Sizes and spacing for general gadgets
Const GADGET_X:Int = 170
Const GADGET_Y:Int = 25
Const SPACE_X:Int = 15
Const SPACE_Y:Int = 10

' Button (max) width
Const BUTTON_X:Int = 80

' Label height
Const LABEL_Y:Int = 20

' Strings

' Meta strings to match
Const META_DIRECTORY:String	= "directory"
Const META_LOAD:String		= "loadfile"
Const META_SAVE:String		= "savefile"
Const META_CHECKBOX:String	= "bool"
Const META_COMBOBOX:String	= "choose"
Const META_SEPARATOR:String	= ","

' Labels
Const LABEL_AFTER:String		= ":"
Const LABEL_BROWSE:String	= "Browse..."
Const LABEL_OK:String		= "OK"
Const LABEL_RESET:String		= "Reset"

' Special characters
Const UNDERSCORE:String		= "_"
Const SPACE:String			= " "

Public

Const FORM_TRIM_:Int	= 1		'Replace underscores with spaces and trim (Default)
Const FORM_RESET:Int	= 2		'Show a Reset button
Const FORM_SKIP_:Int	= 4		'Skip fields beginning with underscores
Const FORM_RESIZABLE:Int	= 8		'Window allows resizing

' Automatically generate a MaxGUI form based on an object
' Returns: True on success (OK), False on quit (WINDOW_CLOSE)
Function AutoForm:Int(title:String, o:Object, opts:Int = FORM_TRIM_ )
	Local i% = 0
	
	' Object is required
	If Not o Return False
	
	' Find Type ID
	Local tid:TTypeId = TTypeId.ForObject(o)
	
	' Find Fields and calculate window size
	Local flist:TList = tid.EnumFields()
	Local fields:TField[flist.Count()]
	Local sizex:Int = GADGET_X + 2*SPACE_X
	Local sizey:Int = GADGET_Y + 2*SPACE_Y
	For Local f:TField = EachIn flist
		' Skip?
		If opts&FORM_SKIP_ And f.Name().StartsWith(UNDERSCORE) Then Continue
		
		fields[i] = f
		
		' Calculate size
		If fields[i].MetaData(META_CHECKBOX)
			sizey :+ GADGET_Y + SPACE_Y
		Else If fields[i].MetaData(META_DIRECTORY) ..
			Or fields[i].MetaData(META_SAVE)..
			Or fields[i].MetaData(META_LOAD)
			
			sizex = GADGET_X + 3*SPACE_X + BUTTON_X
			sizey :+ LABEL_Y + GADGET_Y + SPACE_Y
		Else
			sizey :+ LABEL_Y + GADGET_Y + SPACE_Y
		End If
		
		i :+ 1
	Next
	If FORM_SKIP_ fields = fields[..i]		'Removes empty elements
	
	' Create window
	Local flags:Int = WINDOW_CLIENTCOORDS | WINDOW_TITLEBAR | WINDOW_CENTER
	If opts & FORM_RESIZABLE flags = flags | WINDOW_RESIZABLE
	Local win:TGadget = CreateWindow( title, 0,0, sizex,sizey, Null, flags )
	
	' Create gadgets for fields
	Local flabels:TGadget[fields.length]	'Labels
	Local ftexts:TGadget[fields.length]	'Text fields
	Local fcombos:TGadget[fields.length]	'Combo boxes
	Local fchecks:TGadget[fields.length]	'Check boxes
	Local fbuttons:TGadget[fields.length]	'Buttons (browse)
	Local y:Int = 10
	For i = 0 Until fields.length
		' Get Field name and trim
		Local n:String = fields[i].Name()
		If opts & FORM_TRIM_ Then n = n.Replace(UNDERSCORE, SPACE).Trim()
		
		If fields[i].MetaData(META_CHECKBOX)
			' Checkbox
			fchecks[i] = CreateButton(n, SPACE_X,y, GADGET_X,GADGET_Y, win, BUTTON_CHECKBOX)
			y :+ GADGET_Y + SPACE_Y
			SetButtonState fchecks[i], fields[i].GetInt(o)
			Continue
		End If
		
		' Create label
		flabels[i] = CreateLabel(n+LABEL_AFTER, SPACE_X,y, GADGET_X,LABEL_Y, win)
		y :+ LABEL_Y
		
		Local c:String = fields[i].MetaData(META_COMBOBOX)
		If c	'Combo box
			If opts & FORM_TRIM_ Then c = c.Replace(UNDERSCORE, SPACE).Trim()
			
			fcombos[i] = CreateComboBox(SPACE_X,y, GADGET_X,GADGET_Y, win)
			Local value:String = fields[i].GetString(o)
			Local items:String[] = c.Split(META_SEPARATOR)
			For Local j:Int = 0 Until items.length
				AddGadgetItem fcombos[i], items[j]
				If items[j]=value Then SelectGadgetItem fcombos[i], j
			Next
			
		Else 'Text field
			ftexts[i] = CreateTextField(SPACE_X,y, GADGET_X,GADGET_Y, win)
			SetGadgetText ftexts[i], fields[i].GetString(o)
			
			If fields[i].MetaData(META_DIRECTORY)..
				Or fields[i].MetaData(META_SAVE)..
				Or fields[i].MetaData(META_LOAD)
				fbuttons[i] = CreateButton( LABEL_BROWSE, 2*SPACE_X+GADGET_X,y, ..
					BUTTON_X,GADGET_Y, win )
			End If
		End If
		y :+ GADGET_Y + SPACE_Y
	Next
	
	' Create buttons
	Local ok:TGadget, reset:TGadget
	If opts & FORM_RESET
		Local bx:Int = Min((sizex-3*SPACE_X)/2, BUTTON_X)
		Local sx:Int = (sizex-2*bx)/3
		ok = CreateButton(LABEL_OK, sx,y, bx,GADGET_Y, win, BUTTON_OK)
		reset = CreateButton(LABEL_RESET, 2*sx+bx,y, bx,GADGET_Y, win)
	Else
		ok = CreateButton(LABEL_OK, (sizex-BUTTON_X)/2,y, BUTTON_X,GADGET_Y, win, BUTTON_OK)
	End If
	
	' Main loop
	Repeat
		Select WaitEvent()
		Case EVENT_GADGETACTION
			Local source:Object = EventSource()
			' OK
			If source=ok
				Exit
			
			' Reset
			Else If source=reset
				For i = 0 Until fields.length
					If ftexts[i]		'Text field
						SetGadgetText ftexts[i], fields[i].GetString(o)
						
					Else If fcombos[i]	'Combo box
						Local value:String = fields[i].GetString(o)
						For Local j% = 0 Until CountGadgetItems(fcombos[i])
							If GadgetItemText(fcombos[i],j)=value
								SelectGadgetItem fcombos[i], j
								Exit
							End If
						Next
						
					Else If fchecks[i]	'Check box
						SetButtonState fchecks[i], fields[i].GetInt(o)
					End If
				Next
			
			' Browse
			Else
				For i = 0 Until fields.length
					If Not fbuttons[i] Or source<>fbuttons[i] Then Continue
					
					Local t:String = GadgetText(ftexts[i])
					If fields[i].MetaData(META_DIRECTORY)
						t = RequestDir( GadgetText(flabels[i]), t )
						
					Else
						Local s:Int = True
						Local exts:String = fields[i].MetaData(META_SAVE)
						If Not exts
							s = False
							exts = fields[i].MetaData(META_LOAD)
						End If
						If opts & FORM_TRIM_
							exts = exts.Replace(UNDERSCORE, SPACE).Trim()
						End If
						
						t = RequestFile( GadgetText(flabels[i]), exts, s, t )
					End If
					SetGadgetText ftexts[i], t
				Next
			End If
			
		Case EVENT_WINDOWCLOSE, EVENT_APPTERMINATE
			' Free all gadgets
			For Local g:TGadget = EachIn flabels+ftexts+fcombos+fchecks + [ok,reset,win]
				FreeGadget g
			Next
			Return False
		End Select
	Forever
	
	' Write data to object
	For i = 0 Until fields.length
		If ftexts[i]
			fields[i].SetString o, GadgetText(ftexts[i])
		Else If fcombos[i]
			fields[i].SetString o, GadgetItemText( fcombos[i], SelectedGadgetItem(fcombos[i]) )
		Else If fchecks[i]
			fields[i].SetInt o, ButtonState(fchecks[i])
		End If
	Next
	
	' Free all gadgets
	For Local g:TGadget = EachIn flabels+ftexts+fcombos+fchecks + [ok,reset,win]
		FreeGadget g
	Next
	
	Return True
End Function
