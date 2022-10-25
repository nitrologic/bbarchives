; ID: 2575
; Author: degac
; Date: 2009-08-29 09:41:51
; Title: ListManager
; Description: Add simple commands to a list box

Rem
'
'List Manager
'	v.2.1 - Degac
'	29-08-2009
'
SuperStrict
Import MaxGUI.Drivers
Local window:TGadget = CreateWindow("ListManager v. 2.0",0,0,660,300,,WINDOW_titlebar|window_center)

'this opens a ListManager that handles user-text-item ORDERED
Local listbox1:tlistManager=CreateListManager( 10,10,210,200,window,LIST_ITEMS|LIST_FORCEORDER)
'this opens a ListManager that handles files (default LIST_FILES|LIST_SHOWONLYNAME)
Local listbox2:tlistManager=CreateListManager(220,10,210,200,window)
'this opens a ListManager that handles user-text-item not ordered
Local listbox3:tlistManager=CreateListManager(430,10,210,200,window,LIST_ITEMS)
DisableGadget listbox3

'remember to set the Filter...

listbox2.SelectFilter("JPG;PNG")
listbox2.SelectPath("C:\Documents and Settings\degac.AMIGA.000\Documenti\Immagini")


Local button1:tgadget	=CreateButton("Disable 1",10,230,80,25,window)
Local button2:tgadget	=CreateButton("Enable 1",100,230,80,25,window)


Local array:String[]=["This","is","a","test"]

listbox3.SetItemArray(array)

Print "What is the 3rd element ? "+GadgetItemText(listbox3,2)
ModifyGadgetItem listbox3,2,"New item"

AddGadgetItem listbox1,"This is a new line #0"
AddGadgetItem listbox1,"This is a new line #1"
AddGadgetItem listbox1,"This is a new line #2"
AddGadgetItem listbox1,"This is a new line #3"

InsertGadgetItem listbox1,2,"---changed"
RemoveGadgetItem listbox1,1

Print "How many items in listbox1? :" +CountGadgetItems(listbox1)


Local _terminate:Int=False

While _terminate=False
	WaitEvent
		Select EventID()
		
		Case event_gadgetaction
			If EventSource()=button1	DisableGadget listbox1
			If EventSource()=button2	EnableGadget listbox1
			
		
						
		Case event_windowclose
			If EventSource() = window _terminate=True
		End Select
Wend

FreeGadget listbox1
FreeGadget listbox2
End


End Rem

Rem
		
		This gadget creates a listbox with some useful command-buttons the allow to
		
			ADD (a user text or a file)
			REMOVE an item
			Move UP
			Move DOWN
			Clear all the items
			Scan a folder to add determinated files based on a filter
		
		
		Options style:
		
		LIST_FILES			with this style the ListManager allows to add files and
						to scan a folder to add files based on the filter.
						Additional command-methods:
					
						- SelectFilter("jpg,png,tga")
						- SelectPath("where/open/my/folder")
						
		LIST_SHOWONLYNAME		used in conjunction with LIST_FILES shows in the listbox
						only the file name (without path and extension). The tips contains full PATH

		LIST_ITEMS			manages simple user-text input
						
		LIST_FORCEORDER		only effect the LIST_ITEMS. Every item manually added is sorted in alphabetic
						order.
						
						
		Standard MaxGUI commands reconigzed
		
		DisableGadget 	listmanager
		EnableGadget	listmanager
		SetGadgetColor	listmanager,red,green,blue
		ModifyGadgetItem listmanager,index,text,flags,icon,tip,extra
		RemoveGadgetItem listmanager,index
		AddGadgetItem    listmanager,index
		GadgetItemText   listmanager,index
		InsertGadgetItem listmanager,index		
		
		Not standard method available
		
		SelectFilter(for_file_filtering)
		SelectPath(path_where_to_open)
		GetItemList			returns a list with the items in the listbox
		GetItemArray		returns a string array with the items in the listbox
		SetItemList(list)		fill the listbox with the items contained in the list
		SetItemArray(array$[])	fill the listbox with the items contained in the string array

		
		the gadget needs 2 images for the buttons: enabled and disabled icons.
		If there are not images, the gadget button will show some text (+ - ^ v Clear Dir) to allow the user to use it

		to do:
		
		- handle window resizing
		- handle EVENT_GADGETLOSTFOCUS without errors

End Rem

Function CreateListManager:TlistManager(x:Int,y:Int,w:Int,h:Int,group:TGadget = Null,_style:Int = LIST_FILES|LIST_SHOWONLYNAME|LIST_FORCEORDER,filter:String="exe")
	If group = Null Return Null	
	Return TlistManager.Create(x,y,w,h,group,_style)
End Function

Const LIST_FILES:Int=1
Const LIST_ITEMS:Int=2
Const LIST_SHOWONLYNAME:Int=4
Const LIST_FORCEORDER:Int=8

Global _list_manager_icons:ticonstrip
Global _list_manager_icons_disabled:ticonstrip


Type TlistManager Extends Tproxygadget

	Global list_listmanager:TList=New TList

	Field sid:String
	Field parent:tgadget
	Field panel:TGadget
	Field list_box:tgadget
	Field btn_add:tgadget
	Field btn_remove:tgadget
	Field btn_up:tgadget
	Field btn_down:tgadget
	Field btn_clear:tgadget
	Field btn_AddDir:tgadget
	Field txt_item:tgadget
	Field filter:String,path:String,reqfilter:String
	Field filter_items:String[]
	
	Field style:Int
	

	Method SetEnabled(sta:Int = True)
		If sta EnableAll() Else DisableAll()
	End Method
	
	Method SelectFilter(_filter:String="")
		'filter examples = EXE;TXT;JPG
		If _filter="" 
			filter=""
			filter_items=filter_items[..0]	
			reqfilter=""	
			Return
		End If
		filter=_filter
		Local Current_item:Int=0
		For Local it:String=EachIn _filter.split(";")
			filter_items=filter_items[..current_item+1]
			filter_items[current_item]=Lower(it)
			reqfilter=reqfilter+Lower(it)+","
			current_item:+1
		Next
		reqfilter=reqfilter[..Len(reqfilter)-1]
		reqfilter="User:"+reqfilter+";All files:*"
		
	End Method
	
	Method SelectPath(_path:String="")
		path=_path
	End Method
	
	Method GetItem:String()
		Local ipos:Int=SelectedGadgetItem(list_box)
		If ipos>-1  Return String(GadgetItemExtra(list_box,ipos))
		Return ""

	End Method

	Method SetItem(index:Int,text:String,tip:String,icon:Int,extra:Object,flags:Int)
		?debug
		If index<0 Or index>CountGadgetItems(list_box) Throw "Index out of range"
		?
		ModifyGadgetItem list_box,index,text,flags,icon,tip,extra
	End Method
	
	Method InsertItem(index:Int,text:String,tip:String,icon:Int,extra:Object,flags:Int)
		?debug
		If index<0 Or index>CountGadgetItems(list_box)  Throw "Index out of range"
		?	
		
		If index=CountGadgetItems(list_box)
			AddGadgetItem list_box,text,flags,icon,tip,extra
		Else
			InsertGadgetItem list_box,index,text,flags,icon,tip,extra
		End If
		
	End Method
	
	Method RemoveItem:Int(index:Int)
		?debug
		If index<0 Or index>CountGadgetItems(list_box)  Throw "Index out of range"
		?	
		RemoveGadgetItem list_box,index
	End Method

	Method ItemCount:Int()
		Return CountGadgetItems(list_box)
	End Method
	
	
	Method CleanUp()
		RemoveHook EmitEventHook,EventHandler,Self

		If list_box FreeGadget list_box;list_box=Null
		If btn_Add FreeGadget btn_add;btn_add=Null
		If btn_remove FreeGadget btn_remove;btn_remove=Null
		If btn_up FreeGadget btn_up;btn_UP=Null
		If btn_down FreeGadget btn_down;btn_down=Null
		If btn_clear FreeGadget btn_clear;btn_clear=Null
		If btn_adddir FreeGadget btn_AddDir;btn_addDir=Null
		If txt_item:tgadget FreeGadget txt_item:tgadget;txt_item=Null
		If panel FreeGadget panel;panel = Null
		
		Super.Free
	End Method

	Method SetColor(red:Int,green:Int,blue:Int)
		SetGadgetColor list_box,red,green,blue
	End Method
	
	Method ItemText:String(index:Int)
		?debug
		If index<0 Or index>CountGadgetItems(list_box) Throw "Index out of range"
		?
		Return GadgetItemText(list_box,index)
	End Method

	
	Method SetItemList(_content:TList=Null)
		If _content=Null Return
		For Local ss:String=EachIn _content
			If ss<>Null AddGadgetItem list_box,ss,0,-1,ss,ss
		Next
		
	End Method
	
	Method SetItemArray(_content:String[])
		If _content=Null Return
		For Local ss:String=EachIn _content
			If ss<>Null AddGadgetItem list_box,ss,0,-1,ss,ss
		Next

	End Method
	
	Method GetItemList:TList()
		Local temp:TList=New TList
		For Local c:Int=0 Until CountGadgetItems(list_box)
			temp.addlast String(GadgetItemExtra(list_box,c))
		Next
		Return temp
	End Method
	
	Method GetItemArray:String[]()
		Local temp:String[]
		Local size:Int
		For Local c:Int=0 Until CountGadgetItems(list_box)
			temp=temp[..size+1]
			temp[size]=String(GadgetItemExtra(list_box,c))
			size:+1
		Next
		Return temp
	End Method
		
	Function Create:TlistManager(x:Int,y:Int,w:Int,h:Int,group:TGadget,style:Int = LIST_FILES)
		Local sp:TlistManager= New TlistManager
		Local sizew:Int=(w-10)/5
		Local hh:Int=50
		If style&LIST_FILES Then sizew=(w-10)/6;hh=25
		sp.panel = 		CreatePanel(x,y,w,h,group)
	
		sp.list_box=	CreateListBox(5,1,w-10,h-hh-3,sp.panel)
		sp.btn_add=		CreateButton(" + "	,5		 ,h-hh,sizew,22,sp.panel)
		sp.btn_remove=	CreateButton(" - "	,5+sizew	 ,h-hh,sizew,22,sp.panel)
		sp.btn_up=		CreateButton(" ^ "	,5+sizew*2,h-hh,sizew,22,sp.panel)
		sp.btn_down=	CreateButton(" v "	,5+sizew*3,h-hh,sizew,22,sp.panel)
		sp.btn_clear=	CreateButton("Clear"	,5+sizew*4,h-hh,sizew,22,sp.panel)

		If style&LIST_FILES
			sp.btn_adddir=	CreateButton("Dir",5+sizew*5,h-hh,sizew,22,sp.panel)
			SetGadgetToolTip sp.btn_adddir,"Scan a folder and adds the files"
		Else
			sp.txt_item=	CreateTextField(5,h-hh+24,w-10,22,sp.panel)
		End If
	
		If _list_manager_icons=Null _list_manager_icons=LoadIconStrip("listmanager_icons.png")
		If _list_manager_icons_disabled=Null _list_manager_icons_disabled=LoadIconStrip("listmanager_icons_disabled.png")
		
		sp.EnableIcons()
				
		SetGadgetToolTip sp.btn_add		,"Add an item"
		SetGadgetToolTip sp.btn_remove	,"Remove an item"
		SetGadgetToolTip sp.btn_up		,"Move up the selected item"
		SetGadgetToolTip sp.btn_down	,"Move down the selected item"
		SetGadgetToolTip sp.btn_clear	,"Clear all the items"
		sp.style=style
		sp.SetProxy(sp.panel)

		AddHook(EmitEventHook,EventHandler,sp,-1)
		
		list_listmanager.addlast sp
	
		Return sp
	End Function
	
	Method EnableIcons()
		Local pix:TPixmap
		If _list_manager_icons<>Null
			pix=PixmapFromIconStrip(_list_manager_icons,0)
			SetGadgetPixmap btn_add,pix,GADGETPIXMAP_ICON|GADGETPIXMAP_NOTEXT
			pix=PixmapFromIconStrip(_list_manager_icons,1)
			SetGadgetPixmap btn_remove,pix,GADGETPIXMAP_ICON|GADGETPIXMAP_NOTEXT
			pix=PixmapFromIconStrip(_list_manager_icons,2)
			SetGadgetPixmap btn_up,pix,GADGETPIXMAP_ICON|GADGETPIXMAP_NOTEXT
			pix=PixmapFromIconStrip(_list_manager_icons,3)
			SetGadgetPixmap btn_down,pix,GADGETPIXMAP_ICON|GADGETPIXMAP_NOTEXT
			pix=PixmapFromIconStrip(_list_manager_icons,4)
			SetGadgetPixmap btn_clear,pix,GADGETPIXMAP_ICON|GADGETPIXMAP_NOTEXT
			
			If btn_addDir
				pix=PixmapFromIconStrip(_list_manager_icons,5)
				SetGadgetPixmap btn_addDir,pix,GADGETPIXMAP_ICON|GADGETPIXMAP_NOTEXT
			End If
		End If

	End Method
	
	Method DisableIcons()
		Local pix:TPixmap
		If _list_manager_icons_disabled<>Null
			pix=PixmapFromIconStrip(_list_manager_icons_disabled,0)
			SetGadgetPixmap btn_add,pix,GADGETPIXMAP_ICON|GADGETPIXMAP_NOTEXT
			pix=PixmapFromIconStrip(_list_manager_icons_disabled,1)
			SetGadgetPixmap btn_remove,pix,GADGETPIXMAP_ICON|GADGETPIXMAP_NOTEXT
			pix=PixmapFromIconStrip(_list_manager_icons_disabled,2)
			SetGadgetPixmap btn_up,pix,GADGETPIXMAP_ICON|GADGETPIXMAP_NOTEXT
			pix=PixmapFromIconStrip(_list_manager_icons_disabled,3)
			SetGadgetPixmap btn_down,pix,GADGETPIXMAP_ICON|GADGETPIXMAP_NOTEXT
			pix=PixmapFromIconStrip(_list_manager_icons_disabled,4)
			SetGadgetPixmap btn_clear,pix,GADGETPIXMAP_ICON|GADGETPIXMAP_NOTEXT
			
			If btn_addDir
				pix=PixmapFromIconStrip(_list_manager_icons_disabled,5)
				SetGadgetPixmap btn_addDir,pix,GADGETPIXMAP_ICON|GADGETPIXMAP_NOTEXT
			End If
		End If
		
	End Method
	
	Method EnableAll()
		EnableGadget panel
		EnableGadget list_box
		EnableGadget btn_add
		EnableGadget btn_remove
		EnableGadget btn_up
		EnableGadget btn_down
		EnableGadget btn_clear
		If btn_adddir EnableGadget btn_addDir
		If txt_item EnableGadget txt_item;SetGadgetColor txt_item,255,255,255
		EnableIcons()
	End Method 
	
	Method DisableAll()
		DisableGadget panel
		DisableGadget list_box
		DisableGadget btn_add
		DisableGadget btn_remove
		DisableGadget btn_up
		DisableGadget btn_down
		DisableGadget btn_clear
		If btn_adddir DisableGadget btn_addDir
		If txt_item SetGadgetColor txt_item,212,208,200;DisableGadget txt_item
		DisableIcons()
	End Method 

	
	Function eventHandler:Object(pID%, pData:Object, pContext:Object)

		Local event:TEvent=TEvent(pData)
		Local obj:tlistManager=TlistManager(pContext)
		Local temp:String,ipos:Int,dpos:Int
		Local cotemp:Object,cttemp:String
		Local otemp:Object,ttemp:String
		Local otip:String,dtip:String

		
		If event
			If obj
				Select event.ID
					Case EVENT_GADGETACTION	
						Select event.source
											
							Case obj.btn_add
								
							If obj.style&LIST_FILES	
								temp=RequestFile("Add a file",obj.reqfilter,False,obj.path)
								If temp<>""
									If obj.style&LIST_SHOWONLYNAME
										
										AddGadgetItem obj.list_box,StripAll(temp),0,-1,temp,temp
									Else
										
										AddGadgetItem obj.list_box,temp,0,-1,temp,temp
	
									End If
								End If
							End If
							
							If obj.style&LIST_ITEMS
								SetGadgetColor obj.txt_item,255,255,255
								temp=GadgetText(obj.txt_item)
								If temp<>""
									AddGadgetItem obj.list_box,temp,0,-1,temp,temp
									SetGadgetText obj.txt_item,""
									ActivateGadget OBJ.TXT_ITEM
								Else
									SetGadgetColor obj.txt_item,200,20,20
									ActivateGadget OBJ.TXT_ITEM

								End If
							
								If OBJ.STYLE&LIST_FORCEORDER
								
									Local tempitems:String[]=Obj.GetItemArray()
									tempItems.Sort()
									ClearGadgetItems obj.list_box
									obj.SetItemArray(tempItems)
									tempitems=Null				
								End If
							
							End If

							Return Null
						Case obj.btn_up
		
						ipos=SelectedGadgetItem(obj.list_box)
						If ipos>-1
							cotemp=GadgetItemExtra(obj.list_box,ipos) ' oggetto _ ORIGINE
							cttemp=GadgetItemText(obj.list_box,ipos)
							otip=obj.list_box.itemtip(Ipos)

							dpos=ipos-1
							If dpos>-1
								otemp=GadgetItemExtra(obj.list_box,dpos) ' DESTINAZIONE
								ttemp=GadgetItemText(obj.list_box,dpos)
								dtip=obj.list_box.itemTip(Dpos)

								ModifyGadgetItem(obj.list_box,dpos,cttemp,0,-1,otip,cotemp)
								ModifyGadgetItem(obj.list_box,ipos,ttemp,0,-1,dtip,otemp)
								SelectGadgetItem obj.list_box,dpos
							End If
												
						End If
						Return Null
					Case obj.btn_down
								ipos=SelectedGadgetItem(obj.list_box)
							
						If ipos>-1
							cotemp=GadgetItemExtra(obj.list_box,ipos) ' oggetto _ ORIGINE
							cttemp=GadgetItemText(obj.list_box,ipos)
							otip=obj.list_box.itemtip(Ipos)
	
							dpos=ipos+1
							If dpos>-1 And dpos<CountGadgetItems(obj.list_box)
								otemp=GadgetItemExtra(obj.list_box,dpos) ' DESTINAZIONE
								ttemp=GadgetItemText(obj.list_box,dpos)
								dtip=obj.list_box.itemTip(Dpos)
	
								ModifyGadgetItem(obj.list_box,dpos,cttemp,0,-1,otip,cotemp)
								ModifyGadgetItem(obj.list_box,ipos,ttemp,0,-1,dtip,otemp)
								SelectGadgetItem obj.list_box,dpos
							End If
						
						End If
						Return Null
			
					
					Case obj.btn_remove
						ipos=SelectedGadgetItem(obj.list_box)
						If ipos>-1
							RemoveGadgetItem obj.list_box,ipos
						End If
						Return Null
					
					Case obj.btn_clear
						If CountGadgetItems(obj.list_box)>0
							Local Con:Int=Confirm("Clear everything ?",True)
							If con=True ClearGadgetItems obj.list_box
						End If
						Return Null
					
					Case obj.btn_adddir
						
						If obj.style&LIST_FILES	
							temp=RequestDir("Select a folder to scan...",obj.path)
							tfiles.clear()
							obj.DisableAll()
							ScanDir(obj,temp)
							If tfiles.lista.count()>10000
								Notify "Warning!!! There are "+tfiles.lista.count()+" files~n to add to the list!"
							End If
							For Local ss:tfiles=EachIn tfiles.lista
								If obj.style&LIST_SHOWONLYNAME
									AddGadgetItem obj.list_box,StripAll(ss.file),0,-1,ss.file,ss.file
								Else
									AddGadgetItem obj.list_box,ss.file,0,-1,ss.file,ss.file
								End If
							Next
							obj.EnableAll()
						End If
						Return Null
					End Select
	Rem		
		Case EVENT_GADGETLOSTFOCUS
		'		Print "Lost Focus..."
			
				If event.source=obj.txt_item And obj.style=LIST_ITEMS
		'			Print "ADD A NEW ITEM"		
								temp=GadgetText(obj.txt_item)
								If temp<>""
									AddGadgetItem obj.list_box,temp,0,-1,temp,temp
								End If
								SetGadgetText obj.txt_item,""
								DisableGadget obj.btn_Add
								ActivateGadget obj.txt_item
				End If					
				
	End Rem
				End Select
			End If
		End If
	
	
		Return pdata

	EndFunction

	

	
End Type

Type tfiles

	Global lista:TList=New TList

	Field file:String
	
	Function add:tfiles(_name$)
		If _name="" Return Null
		
		Local cc:tfiles=New tfiles
		cc.file=_name
		lista.addlast cc
		Return cc		
	End Function
	
	Function Clear()
		ClearList lista
	End Function
	
End Type

Function ScanDir:Int(obj:TlistManager,path:String,recursive:Int=True,lvl:Int=0)
Local counter:Int,mydir:Int,file$
Local filename$,lastpath:String,lst:String
If path="" Then path=CurrentDir()
If Right(path,1)<>"\" Then path=path+"\"

mydir=ReadDir(path)
counter=0
Repeat
	file$=NextFile(mydir)
	If file$="" Then Exit
		filename$=path$+file$
		If FileType(filename)=1
		
			If obj.filter<>""	
				For Local fitem:String=EachIn obj.filter_items

					If ExtractExt(Lower(filename))=Lower(fitem)
						tfiles.add(filename)
						counter:+1
					End If
				Next
			
			Else

				tfiles.add(filename)
				counter:+1
			End If
		Else
			
			If recursive=True
				If file$<>"." And file$<>".."
					If FileType(filename$) = 2
						lst=lPath(filename)
						lvl=lvl+1
						scandir(obj,filename$ , True , lvl)
					End If
				End If
			End If
		End If
	
	Forever
	
	CloseDir mydir
	Return counter
								
End Function

Function lPath:String(file:String)
	Local fi:String = file'ExtractDir(file)
	Local lo:Int
	For lo = Len(fi) To 1 Step - 1
		If Mid(fi , lo , 1) = "/" Or Mid(fi,lo,1)="\" Exit
	Next
	Return Mid(fi,lo+1,Len(fi)-lo)
End Function
