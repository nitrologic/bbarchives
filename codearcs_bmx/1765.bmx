; ID: 1765
; Author: Jake L.
; Date: 2006-07-27 11:16:30
; Title: [MaxGUI] GUITools class
; Description: Collection of MaxGUI related functions

SuperStrict

Rem
BBDoc: MaxGUI Tools and useful functions
EndRem
Module fuw.guitools

ModuleInfo "Version: 0.8"
ModuleInfo "OS: Win32 / MacOS / Linux"
ModuleInfo "History: 0.8 - Removed GadgetClass() and ClearTreeView() as they are now supported in MaxGUI.MaxGUI."
ModuleInfo "History: 0.7 - Added RequestString and TreeViewKidsList"
ModuleInfo "History: 0.6 - Added ListViewSwapItems"
ModuleInfo "History: 0.5 - Added GadgetGetD/GadgetSetD"
ModuleInfo "History: 0.4 - TreeviewNodeFind and others added"
ModuleInfo "History: 0.3 - some bugfixes, added GadgetGet$/GadgetSet$"
ModuleInfo "History: 0.2 - multifile-requester (Win32 only)"
ModuleInfo "HISTORY: 0.1 - some treeview helpers to start with"


Import maxgui.drivers
Import brl.retro
Import brl.eventqueue

Private

Rem
BBDoc: Float to string, cutting of precision digits
EndRem
Function _TrimFloat$(value:Double,places:Int=2)
	Return Left(String(value),Instr(String(value),".") + places)
End Function

Const MAX_BUFFER_SIZE:Int = 8192

Const OFN_ALLOWMULTISELECT:Int = 512
Const OFN_CREATEPROMPT:Int = $2000
Const OFN_ENABLEHOOK:Int = 32
Const OFN_ENABLESIZING:Int = $800000
Const OFN_ENABLETEMPLATE:Int = 64
Const OFN_ENABLETEMPLATEHANDLE:Int = 128
Const OFN_EXPLORER:Int = $80000
Const OFN_EXTENSIONDIFFERENT:Int = $400
Const OFN_FILEMUSTEXIST:Int = $1000
Const OFN_HIDEREADONLY:Int = 4
Const OFN_LONGNAMES:Int = $200000
Const OFN_NOCHANGEDIR:Int = 8
Const OFN_NODEREFERENCELINKS:Int = $100000
Const OFN_NOLONGNAMES:Int = $40000
Const OFN_NONETWORKBUTTON:Int = $20000
Const OFN_NOREADONLYRETURN:Int = $8000
Const OFN_NOTESTFILECREATE:Int = $10000
Const OFN_NOVALIDATE:Int = 256
Const OFN_OVERWRITEPROMPT:Int = 2
Const OFN_PATHMUSTEXIST:Int = $800
Const OFN_READONLY:Int = 1
Const OFN_SHAREAWARE:Int = $4000
Const OFN_SHOWHELP:Int = 16
Const OFN_SHAREFALLTHROUGH:Int = 2
Const OFN_SHARENOWARN:Int = 1
Const OFN_SHAREWARN:Int = 0

Type TOpenFileNameA
	Field lStructSize:Int
	Field hwndOwner:Int
	Field hInstance:Int
	Field lpstrFilter:Byte Ptr
	Field lpstrCustomFilter:Int
	Field nMaxCustFilter:Int
	Field nFilterIndex:Int
	Field lpstrFile:Byte Ptr
	Field nMaxFile:Int
	Field lpstrFileTitle:Byte Ptr
	Field nMaxFileTitle:Int
	Field lpstrInitialDir:Byte Ptr
	Field lpstrTitle:Byte Ptr
	Field flags:Int
	Field nFileOffset:Short
	Field nFileExtension:Short
	Field lpstrDefExt:Byte Ptr
	Field lCustData:Int
	Field lpfnHook:Byte Ptr
	Field lpTemplateName:Byte Ptr
EndType

Extern "Win32"
	Function GetOpenFileName:Int( of:Byte Ptr) = "GetOpenFileNameA@4"	
EndExtern

Public

Rem
BBDoc: get a node's level
EndRem
Function TreeViewNodeLevel%(NODE:TGadget)
		Assert NODE<>Null,"TreeViewNodeLevel: node=Null!"
	Local lvl%
	While NODE.parent And GadgetClass(NODE)<>GADGET_TREEVIEW
		NODE=NODE.parent
		lvl:+1
	Wend
	Return lvl
End Function

Rem
BBDoc: get the subindex of a node
EndRem
Function TreeViewNodeIndex%(NODE:TGadget)
		Assert NODE<>Null,"TreeViewNodeIndex: node=Null!"
		Assert NODE.parent<>Null, "TreeViewNodeIndex: node is not a child!"
	Local idx%
	Local Link:TLink=NODE.parent.kids.FirstLink()
	While TGadget(Link.value())<>NODE
		Link=Link.NextLink()
		idx:+1
	Wend
	Return idx
End Function

Rem
BBDoc: Return names of node's kids as stringarray
EndRem
Function TreeViewKidsList$[](node:TGadget)
	Assert NODE<>Null,"TreeViewNodeIndex: node=Null!"
	Local cnt%=node.kids.Count()
	Local res$[]=New String[cnt]
	Local Link:TLink=NODE.kids.FirstLink()
	Local i%
	While Link<>Null
		res[i]=TGadget(Link.Value()).name
		Link=Link.NextLink()
		i:+1
	Wend
	Return res
End Function


Rem
BBDoc: returns the node by a parentnodes subindex
EndRem
Function TreeViewNodeAtIndex:TGadget(NODE:TGadget,index%)
		Assert NODE<>Null,"TreeViewNodeAtIndex: node=Null!"
	Local Link:TLink=NODE.kids.FirstLink()
	While Link<>Null
		If index<=0 Then Return TGadget(Link.value())
		index:-1
		Link=Link.NextLink()
	Wend
	Return Null
End Function

Rem
BBDoc: This function will swap node's captions but cannot swap icons unless you provide them
EndRem
Function TreeViewSwapNodes (node1:TGadget,node2:TGadget,icon1%=-1,icon2%=-1)
	?Debug
		Assert node1<>Null And node2<>Null,"TreeViewSwapNodes: Null not allowed"
	?
	Local temp$=node1.name
	Local tobj:Object=node1.context
	ModifyTreeViewNode (node1,node2.name,icon2)
	ModifyTreeViewNode (node2,temp,icon1)
	node1.context=node2.context
	node2.context=tobj
End Function

Rem
BBDoc: Search a treeview starting from node to find a children by name (and optional level)
EndRem
Function TreeViewNodeFind:TGadget (NODE:TGadget,txt$,lvl%=-1)
	Local Link:TLink=NODE.kids.FirstLink()
	Local n:TGadget
	While Link<>Null
		n=TGadget(Link.value())
		If n.name=txt And (lvl=-1 Or TreeViewNodeLevel(n)=lvl) Then Return n
		Link=Link.NextLink()
	Wend
End Function

Rem
BBDoc: Find a listitem by extra content
EndRem
Function GetGadgetItemByExtra% (listgadget:TGadget,extra:Object,compare_string%=False)
	Local ex:Object
	For Local i%=0 Until CountGadgetItems(listgadget)
		ex=GadgetItemExtra(listgadget,i)
		If ex=extra Or (compare_string And String(ex)=String(extra))
			Return i
		End If
	Next
	Return -1
End Function 

Rem
BBDoc: Swap two listitems
EndRem
Function ListViewSwapItems (lstview:TGadget,idx1%,idx2%)
	Local tmp$=GadgetItemText (lstview,idx1)
	Local tmpflags%=GadgetItemFlags (lstview,idx1)
	Local tmpextra:Object=GadgetItemExtra(lstview,idx1)
	Local tmptip$=lstView.ItemTip(idx1)
	Local tmpicon%=GadgetItemIcon(lstview,idx1)

	Local si%=SelectedGadgetItem(lstView)
	ModifyGadgetItem (lstview,idx1,GadgetItemText(lstview,idx2),GadgetItemFlags(lstview,idx2),GadgetItemIcon(lstview,idx2),..
					  lstView.ItemTip(idx2),GadgetItemExtra(lstview,idx2))
	ModifyGadgetItem (lstview,idx2,tmp,tmpflags,tmpicon,tmptip,tmpextra)
	If si=idx1 Then SelectGadgetItem(lstView,idx2) ElseIf si=idx2 Then SelectGadgetItem(lstView,idx1)
End Function

 Rem
 BBDoc: Create a textfield with label
 EndRem
Function AddTextField:TGadget (parent:TGadget,x%,y%,w%,caption$="",text$="")
	Local G:TGadget=CreateTextField (x,y,w,20,parent)
	
	If text Then SetGadgetText (G,text)
	If caption
		Local lbl:TGadget=CreateLabel (caption,x-105,y+3,100,20,parent,LABEL_RIGHT)
	EndIf
	Return G
End Function

Rem
BBDoc: Create a checkbox and set it's state
EndRem
Function AddCheckbox:TGadget (parent:TGadget,x%,y%,w%,caption$,checked%=False)
	Local G:TGadget=CreateButton (caption,x,y,w,20,parent,BUTTON_CHECKBOX)
	SetButtonState(G,checked)
End Function

Rem
BBDoc: Request multiple files
About: Done by Blitzmax community, slightly changed the way files are returned
EndRem
Function RequestFiles:String[]( text:String, exts:String = Null, path:String = Null)
	?MacOs | Linux	
	Local res:String = RequestFile( test, exts, False, path)
	If res.length <= 0 Then Return Null
	Return [res]
	?
	?Win32	
	Global hwndFocus:Int
	
	' prepare filename / path (ripped from BRL's RequestFile())
	Local File:String, Dir:String
	path = path.Replace( "/","\" )	
	Local i:Int = path.FindLast( "\" )
	If i <> -1 Then
		Dir = path[..i]
		File = path[i+1..]
	Else
		File = path
	EndIf
	' calculate default index of extension in extension list from path name
	Local ext:String, defext:Int,p:Int,q:Int
	p = path.Find(".")
	If (p>-1) Then
		ext = "," + path[p+1..].toLower() + ","
		Local exs:String = exts.toLower()
		exs = exs.Replace(":",":,")
		exs = exs.Replace(";",",;")
		p = exs.find(ext)
		If p >-1 Then
			Local q:Int = -1
			defext = 1
			While True
				q = exs.find(";",q+1)
				If q > p Then Exit
				If q = -1 Then 
					defext = 0
					Exit
				EndIf
				defext :+ 1
			Wend
		EndIf
	EndIf
	If exts Then
		If exts.Find(":") = -1 Then
			exts = "Files~0*." + exts
		Else
			exts = exts.Replace(":","~0*.")
		EndIf
		exts = exts.Replace(";","~0")
		exts = exts.Replace(",",";*.") + "~0"
	EndIf
	
	' allocate cstrings
	Local textp:Byte Ptr = text.ToCString()
	Local extsp:Byte Ptr = exts.ToCString()
	Local dirp:Byte Ptr
	If Dir.length > 0 Then dirp = Dir.ToCString()
	
	' prepare file buffer
	Local Buf:Byte[MAX_BUFFER_SIZE]
	memcpy_( Buf, File, File.length)
	
	' initialize dialog options
	Local of:TOpenFileNameA = New TOpenFileNameA
	of.lStructSize = SizeOf(TOpenFileNameA)
	of.hwndOwner = GetActiveWindow()
	of.lpstrTitle = textp
	of.lpstrFilter = extsp
	of.nFilterIndex = defext
	of.lpstrFile = Buf
	of.lpstrInitialDir = dirp
	of.nMaxFile = Buf.length
	of.flags = OFN_HIDEREADONLY | OFN_NOCHANGEDIR | OFN_PATHMUSTEXIST | OFN_FILEMUSTEXIST | OFN_ALLOWMULTISELECT | OFN_EXPLORER ' | OFN_LONGNAMES
	
	' display dialog
	hwndFocus = GetFocus()
	Local n:Int = GetOpenFileName( of)
	SetFocus( hwndFocus)
	
	' free cstrings
	MemFree textp
	MemFree extsp
	If dirp Then MemFree dirp

	' failure ?
	If n <= 0 Then Return Null
	
	' count the number of files
	Local s:Byte Ptr = Buf
	Local count:Int = 0
	While s[0] <> 0
		If s[1] = 0 Then
			count :+ 1
			s :+ 2
			If s[0] = 0 Then Exit
		EndIf
		s :+ 1
	Wend
	If count <= 0 Then Return Null
	
	' extract filenames into String array
	If count = 1 Then 
		'MARK: im following RequestFile() convention here, and returing "\" path seperators #1
		'Return [ String.FromCString( buf).Replace( "\", "/") ]
		Return [ String.FromCString( Buf) ]
	Else
		Local result:String[] = New String[count]	
		s = Buf
		For Local i:Int = 0 Until count
			result[i] = String.FromCString( s)
			s :+ result[i].length + 1
			If i>0 Then result[i] = result[0]+"\"+result[i]
		Next	
		'MARK: im following RequestFile() convention here, and returing "\" path seperators #2
		'result[0] = result[0].Replace( "\", "/") + "/"
		result=Result[1..]
		Return result
	EndIf
EndFunction

Rem
BBDoc: Return a gadget's value as Double
EndRem
Function GadgetGetD! (G:TGadget)
	Return Double(GadgetGet(G))
End Function

Rem
BBDoc: Set a gadget's value from Double
EndRem
Function GadgetSetD (G:TGadget,d!)
	SetGadgetText (G,_TrimFloat(d))
End Function

Rem
BBDoc: Return a gadget's value as Float
EndRem
Function GadgetGetF# (G:TGadget)
		Select GadgetClass(G)
			Case GADGET_TEXTFIELD
				Return Float(GadgetText(G))
		End Select
End Function

Rem
BBDoc: Return a gadget's value as Int
EndRem
Function GadgetGetI% (G:TGadget)
		Select GadgetClass(G)
			Case GADGET_TEXTFIELD
				Return Int(GadgetText(G))
		End Select
End Function

Rem
BBDoc: Return a gadget's value as string
About: Not quite useful yet, but other gadget-types could be added
EndRem
Function GadgetGet$ (G:TGadget)
	Select GadgetClass(G)
			Case GADGET_TEXTFIELD
				Return GadgetText(G)
		End Select
End Function

Rem
BBDoc: Set a gadget's value from Float
EndRem	
Function GadgetSetF (G:TGadget,f#)
		SetGadgetText(G,_TrimFloat(f))
End Function

Rem
BBDoc: Set a gadget's value from Int
EndRem		
Function GadgetSetI (G:TGadget,i%)
		SetGadgetText(G,i)
End Function

Rem
BBDoc: Set a gadget's value from string
About: Not quite useful yet, but other gadget-types could be added
EndRem	
Function GadgetSet (G:TGadget,s$)
		SetGadgetText(G,s)
End Function

Rem
BBDoc: A DialogBox
EndRem	
Function RequestString$(caption$,text$)
	Local ww%=220
	Local wh%=30
	Local wx%=Desktop().width/2-ww/2
	Local wy%=Desktop().height/2-wh/2
	Local win:TGadget=CreateWindow(caption,wx,wy,ww,wh,Null,WINDOW_TITLEBAR|WINDOW_TOOL|WINDOW_CLIENTCOORDS)
	Local txt:TGadget=CreateTextField(5,5,ww-100,20,win)
		SetGadgetText (txt,text)
	Local ok:TGadget=CreateButton("OK",ww-85,5,80,20,win)
	
	ActivateGadget(txt)
	Repeat
		Select WaitEvent()
			Case EVENT_WINDOWCLOSE
				FreeGadget(win)
				Return text
			Case EVENT_GADGETACTION
				If EventSource()=ok
					text=GadgetText(txt)
					FreeGadget(win)
					Return text
				End If
		End Select
	Forever
End Function

Rem
BBDoc: Absolute to relative paths
About: Copied from Blitzmax community 
EndRem
Function ExtractRelativePath:String( src:String, dest:String )
	
	src=ExtractDir(RealPath(src))
	dest=RealPath(dest)
	
	Local count:Int=0
	While src<>Left(dest,Len(src))
		count:+1
		Local i:Int=src.FindLast("/")
		src=src[..i]
	Wend
	
	dest=dest.Replace(src,"")
	dest=dest[1..]
	For Local i:Int=1 To count
		dest="../"+dest
	Next
	
	Return dest
	
End Function
