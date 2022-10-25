; ID: 2994
; Author: TAS
; Date: 2012-10-30 22:01:37
; Title: Object_Edit()
; Description: Edit any Object

'Edit the fields of any object
'By TAS: Thomas A Stevenson
'war-game-programming.com
'10-30-12
'Public Domain

Import MaxGui.Drivers

'example type for test run
Type myType
	Field bb:Byte
	Field ss:Short
	Field II:Int
	Field LL:Long
	Field ff:Float
	Field dd:Double
	Field s:String
	Field List:TList
	Field Link:TLink
	
	Method myMethod()
	End Method
End Type

'example of use
m:myType=New myType
Obj_Edit(m)
End

Function Obj_Edit(obj:Object)
	If obj=Null Then Return
	
	Local gad:tgadget[8]	
	Local y=10,sh=40,s$
	Local fld:TField
	Local id:TTypeId=TTypeId.ForObject(obj)

	Local style=WINDOW_TITLEBAR|WINDOW_CLIENTCOORDS|WINDOW_CENTER
	gad[0]=CreateWindow("Object Editor",0,0,310,180,whand,style)	
	gad[1]=CreateLabel("Object type: "+id.name$(),5,y,300,25,gad[0])	;y=y+sh
	gad[2]=CreateLabel("Fields: ",5,y,50,25,gad[0])
	gad[3]=CreateComboBox(55,y,240,25,gad[0])	;y=y+sh
	Local fld_List:TList=New TList
	For Fld=EachIn id.EnumFields()
		'add type fields to combobox
  	AddGadgetItem(gad[3],fld.Name()+":"+fld.TypeId().Name())
		'add to list of TFields in same order
		fld_List.AddLast(fld)
	Next
	gad[4]=CreateTextField(5,y,200,25,gad[0])	
	gad[5]=CreateButton("Apply",220,y,80,25,gad[0])	;y=y+sh+10
	gad[6]=CreateButton("View Values",  10,y,110,25,gad[0])
	gad[7]=CreateButton("View Methods",190,y,110,25,gad[0])
	
	Repeat
		WaitEvent()
		Select EventID()
		Case EVENT_WINDOWCLOSE
			FreeGadget gad[0]
			Return
		Case EVENT_GADGETACTION
			Select EventSource()
			Case gad[3]	'combobox changed
				fld=TField(fld_List.ValueAtIndex(SelectedGadgetItem(gad[3])))
				'update edit field
				SetGadgetText gad[4],String(fld.get(obj))
			Case gad[5]	'Apply
				'find Tfield associated with selected gadget item
				fld=TField(fld_List.ValueAtIndex(SelectedGadgetItem(gad[3])))
				s$=GadgetText(gad[4])
				'fld.Set obj,s$ works also but this approachg is probaly safer
				'use field name to determine proper set method
				Select fld.TypeId().Name()
				Case ByteTypeId.name()
					fld.SetInt obj,Byte(s)
				Case ShortTypeId.name()
					fld.SetInt obj,Short(s)
				Case IntTypeId.name()
					fld.SetInt obj,Int(s)
				Case LongTypeId.name()
					fld.SetLong obj,Long(s)
				Case FloatTypeId.name()
					fld.Setfloat obj,Float(s)
				Case DoubleTypeId.name()
					fld.Setdouble obj,Double(s)
				Case StringTypeId.name()
					fld.Setstring obj,s
				Case ObjectTypeId.name()
					'do nothing 
				End Select
				SetGadgetText gad[4],String(fld.get(obj))
			Case gad[6]
				s$="******* Fields and Values ********"+Chr(13)
				For fld=EachIn id.EnumFields()
		  		s=s+fld.Name()+":"+fld.TypeId().Name()+" = "+String(fld.get(obj))+Chr(13)
				Next
				Notify s
			Case gad[7]
				s$="******** Methods ********"+Chr(13)
				For Local mthd:TMethod=EachIn id.EnumMethods()
			  	s=s+mthd.Name()+Chr(13)
				Next
				Notify s
			End Select
		End Select
	Forever
End Function
