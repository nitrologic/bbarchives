; ID: 1231
; Author: Nilium
; Date: 2004-12-12 14:30:00
; Title: F-UI GUI XML Loader
; Description: Loads GUI layouts for F-UI using XML

;#Region XMLGui.bb
	;#Region CLASSES
		Type FUI_XMLGadget
			Field Name$
			Field Gadget
		End Type
		
		Global LED_ColorMode=1
	;#End Region
	
	;#Region PROCEDURES
		Function FUI_LoadGUI(Path$)
			If FileType(Path) <> 1 Then RuntimeError "Failed to load GUI '"+Path+"'"
			
			node.XMLNode = ReadXML(Path)
			root.XMLNode = node
		
			While(node <> Null)
				FUI_ParseXMLGUI(node)
				node = XMLNextNode(node)
			Wend
			
			XMLCloseNode(root)
			
			Return 1
		End Function
		
		Function FUI_ParseXMLGUI(node.XMLNode,owner=0)
			If node = Null Then Return
			
			Local X%,Y%,Width%,Height%,Caption$,Name$
			Local F1#,F2#,F3#,F4#,F5#,F6#,F7#
			Local I1%,I2%,I3%,I4%,I5%,I6%,I7%
			Local Icon$,Shortcut$,DType%=DTYPE_INTEGER,Alpha#=1
			Local Flags
			
			Local a.XMLAttr = XMLGetFirstAttribute(node)
			While (a <> Null)
				Select Lower(a\name)
					Case "x"
						X = a\value
					Case "y"
						Y = a\value
					Case "height"
						Height = a\value
					Case "width"
						Width = a\value
					Case "caption"
						Caption = a\value
					Case "name"
						Name = a\value
					Case "id"
						I1 = a\value
					Case "checked"
						I1 = a\value
					Case "dispitems"
						I4 = a\value
					Case "checkable"
						I2 = a\value
					Case "checked"
						I3 = a\value
					Case "shortcut"
						Shortcut = a\value
					Case "image"
						Shortcut = a\value
					Case "align"
						I1 = ((Lower(a\value)="center")*ALIGN_CENTER Or (Lower(a\value)="left")*ALIGN_LEFT Or (Lower(a\value)="right")*ALIGN_RIGHT)
					Case "multisel"
						I1 = a\value
					Case "forcesel"
						I2 = a\value
					Case "min"
						F1 = a\value
					Case "max"
						F2 = a\value
					Case "value"
						F3 = a\value
					Case "dtype"
						DType = ((Lower(a\value)="integer")*DTYPE_INTEGER Or (Lower(a\value)="float")*DTYPE_FLOAT)
					Case "scrollw"
						I1 = a\value
					Case "direction"
						I2 = ((Lower(a\value)="horizontal")*DIR_HORIZONTAL Or (Lower(a\value)="vertical")*DIR_VERTICAL)
					Case "inc"
						F4 = a\value
					Case "append"
						Shortcut = a\value
					Case "maxlength"
						I1 = a\value
					Case "flags"
						Flags = a\value
					Case "buttons"
						I2 = a\value
					Case "alpha"
						Alpha# = a\value
					Case "red","r"
						I1 = a\value
					Case "green","g"
						I2 = a\value
					Case "blue","b"
						I3 = a\value
					Case "bitsperpixel"
						I1 = a\value
					Case "fullscreen"
						I2 = Not Int a\value + 1
					Case "windowed"
						I2 = Int a\value + 1
					Case "locked"
						Locked = a\value
					Case "colormode"
						LED_ColorMode=Int a\value - 1
				End Select
				a = XMLGetNextAttribute(a)
			Wend
			
			Select Lower(node\tag)
				Case "window"
					If Flags=0 Then Flags=WS_TITLEBAR Or WS_ALLBUTTONS
					gad = FUI_Window(X,Y,Width,Height,Caption,Icon,Flags,I2)
					FUI_LockWindow(gad,Locked)
				Case "button"
					If Flags=0 Then Flags=CS_BORDER
					gad = FUI_Button(owner,X,Y,Width,Height,Caption,Icon,I1,Flags)
				Case "groupbox"
					gad = FUI_GroupBox(owner,X,Y,Width,Height,Caption)
				Case "listbox"
					gad = FUI_ListBox(owner,X,Y,Width,Height,I1,I2)
				Case "listboxitem"
					gad = FUI_ListBoxItem(owner,Caption,Icon)
				Case "treeview"
					gad = FUI_TreeView(owner,X,Y,Width,Height)
				Case "treeviewnode"
					gad = FUI_TreeViewNode(owner,Caption)
				Case "combobox"
					gad = FUI_ComboBox(owner,X,Y,Width,Height,I4)
				Case "comboboxitem"
					gad = FUI_ComboBoxItem(owner,Caption,Icon)
				Case "label"
					gad = FUI_Label(owner,X,Y,Caption,I1)
				Case "slider"
					gad = FUI_Slider(owner,X,Y,Width,Height,F1,F2,F3,I1,I2)
				Case "menubar"
					gad = FUI_MenuBar(owner)
				Case "menuitem"
					gad = FUI_MenuItem(owner,Caption,Shortcut,Icon,I2,I3,I1)
				Case "menutitle"
					gad = FUI_MenuTitle(owner,Caption,Width)
				Case "panel"
					gad = FUI_Panel(owner,X,Y,Width,Height,Caption)
				Case "progressbar"
					gad = FUI_ProgressBar(owner,X,Y,Width,Height,F1,F2,F3,DType)
				Case "radio"
					gad = FUI_Radio(owner,X,Y,Caption,I3,I1)
				Case "checkbox"
					gad = FUI_CheckBox(owner,X,Y,Caption,I3)
				Case "spinner"
					gad = FUI_Spinner(owner,X,Y,Width,Height,F1,F2,F3,F4,DType,Shortcut)
				Case "tab"
					gad = FUI_Tab(owner,X,Y,Width,Height)
				Case "tabpage"
					gad = FUI_TabPage(owner,Caption,Icon)
				Case "view"
					gad = FUI_View(owner,X,Y,Width,Height,I1,I2,I3)
				Case "textobx"
					gad = FUI_TextBox(owner,X,Y,Width,Height,I1)
				Case "skinpath"
					SKIN_PATH = node\value
				Case "skinenabled"
					SKIN_ENABLED = node\value
				Case "gui"
					If Width = 0 Or Height = 0 Then Width = 1024 Height = 768
					If I1 = 0 Then I1 = 32
					If I2 = 0 Then I2 = 2
					FUI_Initialise(Width,Height,I1,I2,0,1,"LotusEd R2","1.8")
					SetupColors(LED_ColorMode)
			End Select
			
			If gad <> 0 Then
				g.FUI_XMLGadget = New FUI_XMLGadget
				g\Name = Name
				g\Gadget = gad
				If Alpha <> 1 Then FUI_SetGadgetAlpha(gad,Alpha,1)
			EndIf
			
			node = XMLGetChild(node,0)
			While(node <> Null)
				FUI_ParseXMLGUI(node,gad)
				node = XMLNextNode(node)
			Wend
		End Function
		
		Function FUI_GetGadget(name$)
			name = Lower(name)
			For g.FUI_XMLGadget = Each FUI_XMLGadget
				If Lower(g\Name) = name Then Return g\Gadget
			Next
			Return -1
		End Function
		
		;;; Add your own color schemes to this function
		Function SetupColors(Mode=1)
			If Mode=0
				SC_FORM=FUI_RGBToInt(75,82,93)
				SC_FORM_BORDER=FUI_RGBToInt(0,0,0)
				SC_TITLEBAR=FUI_RGBToInt(255,155,48)
				SC_TITLEBAR_TEXT=FUI_RGBToInt(24,55,80)
				SC_MENUBAR=FUI_RGBToInt(51,55,60)
				SC_MENUBAR_BORDER=FUI_RGBToInt(32,32,32)
				SC_STATUSBAR=FUI_RGBToInt(75,82,93)
				SC_STATUSBAR_TEXT=FUI_RGBToInt(255,155,48)
				SC_STATUSBAR_BORDER=FUI_RGBToInt(32,32,32)
				
				SC_MENUTITLE=FUI_RGBToInt(75,82,93)
				SC_MENUTITLE_OVER=FUI_RGBToInt(66,120,164)
				SC_MENUTITLE_SEL=FUI_RGBToInt(66,120,164)
				SC_MENUTITLE_TEXT=FUI_RGBToInt(255,171,61)
				SC_MENUTITLE_TEXT_OVER=FUI_RGBToInt(48,46,45)
				SC_MENUTITLE_TEXT_SEL=FUI_RGBToInt(48,46,45)
				SC_MENUTITLE_BORDER=FUI_RGBToInt(0,0,0)
				SC_MENUTITLE_BORDER_OVER=FUI_RGBToInt(0,0,0)
				SC_MENUTITLE_BORDER_SEL=FUI_RGBToInt(0,0,0)
				
				SC_MENUITEM=FUI_RGBToInt(75,82,93)
				SC_MENUITEM_OVER=FUI_RGBToInt(66,120,164)
				SC_MENUITEM_SEL=FUI_RGBToInt(66,120,164)
				SC_MENUITEM_TEXT=FUI_RGBToInt(255,171,61)
				SC_MENUITEM_TEXT_OVER=FUI_RGBToInt(48,46,45)
				SC_MENUITEM_TEXT_SEL=FUI_RGBToInt(48,46,45)
				SC_MENUITEM_BORDER=FUI_RGBToInt(0,0,0)
				SC_MENUITEM_BORDER_OVER=FUI_RGBToInt(0,0,0)
				SC_MENUITEM_BORDER_SEL=FUI_RGBToInt(0,0,0)
				
				SC_MENUDROPDOWN=FUI_RGBToInt(75,82,93)
				SC_MENUDROPDOWN_BORDER=FUI_RGBToInt(0,0,0)
				SC_MENUDROPDOWN_STRIP=FUI_RGBToInt(255,171,61)
				
				SC_TOOLTIP=FUI_RGBToInt(75,82,93)
				SC_TOOLTIP_BORDER=FUI_RGBToInt(0,0,0)
				SC_TOOLTIP_TEXT=FUI_RGBToInt(255,171,61)
				
				SC_GADGET =FUI_RGBToInt(75,82,93)
				SC_GADGET_TEXT =FUI_RGBToInt(255,171,61)
				SC_GADGET_COLOR =FUI_RGBToInt(75,82,93)
				SC_GADGET_COLOR_TEXT =FUI_RGBToInt(255,171,61)
				SC_GADGET_BORDER =FUI_RGBToInt(0,0,0)
				
				SC_INPUT=FUI_RGBToInt(32,45,64)
				SC_INPUT_TEXT=FUI_RGBToInt(255,171,61)
				SC_INPUT_COLOR=FUI_RGBToInt(32,45,64)
				SC_INPUT_COLOR_TEXT=FUI_RGBToInt(255,171,61)
				SC_INPUT_BORDER=FUI_RGBToInt(0,0,0)
			ElseIf Mode=1 Then
				SC_FORM=FUI_RGBToInt(242,240,238)
				SC_FORM_BORDER=FUI_RGBToInt(32,32,32)
				SC_TITLEBAR=FUI_RGBToInt(15,128,206)
				SC_TITLEBAR_TEXT=FUI_RGBToInt(255,255,255)
				SC_MENUBAR=FUI_RGBToInt(245,244,240)
				SC_MENUBAR_BORDER=FUI_RGBToInt(32,32,32)
				SC_STATUSBAR=FUI_RGBToInt(245,244,240)
				SC_STATUSBAR_TEXT=FUI_RGBToInt(0,0,0)
				SC_STATUSBAR_BORDER=FUI_RGBToInt(64,64,64)
				
				SC_MENUTITLE=FUI_RGBToInt(255,197,128)
				SC_MENUTITLE_OVER=FUI_RGBToInt(255,197,128)
				SC_MENUTITLE_SEL=FUI_RGBToInt(255,197,128)
				SC_MENUTITLE_TEXT=FUI_RGBToInt(0,0,0)
				SC_MENUTITLE_TEXT_OVER=FUI_RGBToInt(0,0,0)
				SC_MENUTITLE_TEXT_SEL=FUI_RGBToInt(0,0,0)
				SC_MENUTITLE_BORDER=FUI_RGBToInt(0,0,0)
				SC_MENUTITLE_BORDER_OVER=FUI_RGBToInt(255,255,255)
				SC_MENUTITLE_BORDER_SEL=FUI_RGBToInt(255,255,255)
				
				SC_MENUITEM=FUI_RGBToInt(255,197,128)
				SC_MENUITEM_OVER=FUI_RGBToInt(255,197,128)
				SC_MENUITEM_SEL=FUI_RGBToInt(255,197,128)
				SC_MENUITEM_TEXT=FUI_RGBToInt(0,0,0)
				SC_MENUITEM_TEXT_OVER=FUI_RGBToInt(0,0,0)
				SC_MENUITEM_TEXT_SEL=FUI_RGBToInt(0,0,0)
				SC_MENUITEM_BORDER=FUI_RGBToInt(0,0,0)
				SC_MENUITEM_BORDER_OVER=FUI_RGBToInt(255,255,255)
				SC_MENUITEM_BORDER_SEL=FUI_RGBToInt(255,255,255)
				
				SC_MENUDROPDOWN=FUI_RGBToInt(242,240,238)
				SC_MENUDROPDOWN_BORDER=FUI_RGBToInt(0,0,0)
				SC_MENUDROPDOWN_STRIP=FUI_RGBToInt(21,119,218)
				
				SC_TOOLTIP=FUI_RGBToInt(252,242,202)
				SC_TOOLTIP_BORDER=FUI_RGBToInt(0,0,0)
				SC_TOOLTIP_TEXT=FUI_RGBToInt(0,0,0)
				
				SC_GADGET=FUI_RGBToInt(250,248,246)
				SC_GADGET_TEXT=FUI_RGBToInt(0,0,0)
				SC_GADGET_COLOR=FUI_RGBToInt(250,248,246)
				SC_GADGET_COLOR_TEXT=FUI_RGBToInt(0,0,0)
				SC_GADGET_BORDER=FUI_RGBToInt(0,0,0)
				
				SC_INPUT=FUI_RGBToInt(255,255,255)
				SC_INPUT_TEXT=FUI_RGBToInt(0,0,0)
				SC_INPUT_COLOR=FUI_RGBToInt(255,255,255)
				SC_INPUT_COLOR_TEXT=FUI_RGBToInt(0,0,0)
				SC_INPUT_BORDER=FUI_RGBToInt(0,0,0)
			ElseIf Mode=2 Then
				SC_FORM=FUI_RGBToInt(80,74,90)
				SC_FORM_BORDER=FUI_RGBToInt(32,32,32)
				SC_TITLEBAR=FUI_RGBToInt(179,247,41)
				SC_TITLEBAR_TEXT=FUI_RGBToInt(0,0,0)
				SC_MENUBAR=FUI_RGBToInt(80,74,90)
				SC_MENUBAR_BORDER=FUI_RGBToInt(32,32,32)
				SC_STATUSBAR=FUI_RGBToInt(80,74,90)
				SC_STATUSBAR_TEXT=FUI_RGBToInt(180,255,57)
				SC_STATUSBAR_BORDER=FUI_RGBToInt(64,64,64)
				
				SC_MENUTITLE=FUI_RGBToInt(96,66,173)
				SC_MENUTITLE_OVER=FUI_RGBToInt(96,66,173)
				SC_MENUTITLE_SEL=FUI_RGBToInt(96,66,173)
				SC_MENUTITLE_TEXT=FUI_RGBToInt(180,255,57)
				SC_MENUTITLE_TEXT_OVER=FUI_RGBToInt(180,255,57)
				SC_MENUTITLE_TEXT_SEL=FUI_RGBToInt(180,255,57)
				SC_MENUTITLE_BORDER=FUI_RGBToInt(0,0,0)
				SC_MENUTITLE_BORDER_OVER=FUI_RGBToInt(0,0,0)
				SC_MENUTITLE_BORDER_SEL=FUI_RGBToInt(0,0,0)
				
				SC_MENUITEM=FUI_RGBToInt(96,66,173)
				SC_MENUITEM_OVER=FUI_RGBToInt(96,66,173)
				SC_MENUITEM_SEL=FUI_RGBToInt(96,66,173)
				SC_MENUITEM_TEXT=FUI_RGBToInt(180,255,57)
				SC_MENUITEM_TEXT_OVER=FUI_RGBToInt(180,255,57)
				SC_MENUITEM_TEXT_SEL=FUI_RGBToInt(180,255,57)
				SC_MENUITEM_BORDER=FUI_RGBToInt(0,0,0)
				SC_MENUITEM_BORDER_OVER=FUI_RGBToInt(0,0,0)
				SC_MENUITEM_BORDER_SEL=FUI_RGBToInt(0,0,0)
				
				SC_MENUDROPDOWN=FUI_RGBToInt(80,74,90)
				SC_MENUDROPDOWN_BORDER=FUI_RGBToInt(0,0,0)
				SC_MENUDROPDOWN_STRIP=FUI_RGBToInt(180,255,57)
				
				SC_TOOLTIP=FUI_RGBToInt(80,74,90)
				SC_TOOLTIP_BORDER=FUI_RGBToInt(0,0,0)
				SC_TOOLTIP_TEXT=FUI_RGBToInt(180,255,57)
				
				SC_GADGET=FUI_RGBToInt(80,74,90)
				SC_GADGET_TEXT=FUI_RGBToInt(180,255,57)
				SC_GADGET_COLOR=FUI_RGBToInt(80,74,90)
				SC_GADGET_COLOR_TEXT=FUI_RGBToInt(180,255,57)
				SC_GADGET_BORDER=FUI_RGBToInt(0,0,0)
				
				SC_INPUT=FUI_RGBToInt(40,37,45)
				SC_INPUT_TEXT=FUI_RGBToInt(180,255,57)
				SC_INPUT_COLOR=FUI_RGBToInt(40,37,45)
				SC_INPUT_COLOR_TEXT=FUI_RGBToInt(180,255,57)
				SC_INPUT_BORDER=FUI_RGBToInt(0,0,0)
			EndIf
		End Function
	;#End Region
;#End Region

;#Region LotusXML.bb
	;#Region DESCRIPTION
		; XML load / parse / save functions
		; Written by Blitztastic, butchered by Noel Cower
	;#End Region
	
	;#Region CLASSES
		Type XMLnodelist
			Field node.XMLnode
			Field nextnode.XMLnodelist
			Field prevnode.XMLnodelist
		End Type
		
		; for internal use, do not use in code outside of this file
		Type XMLworklist
			Field node.XMLnode
		End Type
		
		
		Type XMLnode
			Field tag$,value$,path$
			Field firstattr.XMLattr
			Field lastattr.XMLattr	
			Field attrcount,fileid
			Field endtag$
			
			; linkage functionality
			Field firstchild.XMLnode
			Field lastchild.XMLnode
			Field childcount
			Field nextnode.XMLnode
			Field prevnode.XMLnode
			Field parent.XMLnode
		End Type
		
		Type XMLattr
			Field name$,value$
			Field sibattr.XMLattr
			Field parent.XMLnode
		End Type
		
		Global XMLFILEID
	;#End Region
	
	;#Region PROCEDURES
		Function ReadXML.XMLnode(filename$)
			infile = ReadFile(filename$)
			XMLFILEID=MilliSecs()
			x.XMLnode = XMLReadNode(infile,Null)
			CloseFile infile
			Return x
		End Function
		
		Function WriteXML(filename$,node.XMLnode,writeroot=False)
			outfile = WriteFile(filename$)
			WriteLine outfile,"<?xml version="+Chr$(34)+"1.0"+Chr$(34)+" ?>"
			XMLwriteNode(outfile,node)
			CloseFile outfile
		End Function
		
		Function XMLOpenNode.XMLnode(parent.XMLnode,tag$="")
			x.XMLnode = New XMLnode
			x\tag$=tag$
			x\fileid = XMLFILEID; global indicator to group type entries (allows multiple XML files to be used)
			XMLaddNode(parent,x)
			Return x
		End Function
		
		Function XMLCloseNode.XMLnode(node.XMLnode)
			Return node\parent
		End Function
		
		; adds node to end of list (need separate function for insert, or mod this on)
		Function XMLAddNode(parent.XMLnode,node.XMLnode)
			If parent <> Null
				If parent\childcount = 0 Then
					parent\firstchild = node
				Else
					parent\lastchild\nextnode = node
				End If
				node\prevnode = parent\lastchild
				parent\lastchild = node
				parent\childcount = parent\childcount +1
				node\path$ = parent\path$+parent\tag$
			End If
			node\parent = parent
			node\path$=node\path$+"/"
		End Function
		
		Function XMLDeleteNode(node.XMLnode)
			n.XMLnode = node\firstchild
			; delete any children recursively
			While n <> Null
				nn.XMLnode= n\nextnode
				XMLdeletenode(n)
				n = nn
			Wend
		
			; delete attributes for this node
			a.XMLattr = node\firstattr
			While a <> Null
				na.XMLattr = a\sibattr
				Delete a
				a = na
			Wend
		
			; dec parents child count
			If node\parent <> Null
				node\parent\childcount = node\parent\childcount -1
				
				; heal linkages
				If node\prevnode <> Null Then node\prevnode\nextnode = node\nextnode
				If node\nextnode <> Null Then node\nextnode\prevnode = node\prevnode
				If node\parent\firstchild = node Then node\parent\firstchild = node\nextnode
				If node\parent\lastchild = node Then node\parent\lastchild = node\prevnode
			End If
			; delete this node		
			Delete node
		
		End Function
		
		; node functions
		Function XMLfindNode.XMLnode(node.XMLnode,path$)
		
			ret.XMLnode = Null
			p=Instr(path$,"/")
			If p > 0 Then 
				tag$=Left$(path$,p-1)
				a.XMLnode = node
				While ret=Null And a<>Null 
					If Lower(tag$)=Lower(a\tag$) Then
						If p=Len(path$) Then
								ret = a
						Else
							If a\firstchild <> Null Then
								ret = XMLfindnode(a\firstchild,Mid$(path$,p+1))
							End If
						End If
					End If
					a = a\nextnode
				Wend
			End If
			Return ret
		End Function
		
		Function XMLDeleteList(nl.XMLnodelist)
			While nl <> Null
				na.XMLnodelist = nl\nextnode
				Delete nl
				nl = na
			Wend
		End Function
		
		
		Function XMLSelectNodes.XMLnodelist(node.XMLnode,path$,recurse=True)
			root.XMLnodelist=Null
			XMLselectnodesi(node,path$,recurse)
			prev.XMLnodelist=Null
			c = 0
			For wl.XMLworklist = Each XMLworklist
				c = c + 1
				nl.XMLnodelist = New XMLnodelist
				nl\node = wl\node
				If prev = Null Then 
					root = nl
					prev = nl
				Else
					prev\nextnode = nl
					nl\prevnode = prev
				End If
				prev = nl
				Delete wl

			Next
			;gak debuglog "XML: "+c+" nodes selected"
			Return root
		End Function
		
		; internal selection function, do not use outside this file
		Function XMLSelectNodesI(node.XMLnode,path$,recurse=True)
			wl.XMLworklist=Null
			If node = Null Then
			End If
			ret.XMLnode = Null
			p=Instr(path$,"/")
			If p > 0 Then 
				tag$=Left$(path$,p-1)
				a.XMLnode = node
				While a<>Null 
					If Lower(path$)=Lower(Right$(a\path$+a\tag$+"/",Len(path$))) Then
							wl = New XMLworklist
							wl\node = a
					End If
					If a\firstchild <> Null And (recurse) Then
						XMLSelectNodesI(a\firstchild,path$)
					End If
					a = a\nextnode
				Wend
			End If
		
		End Function
		
		Function XMLNextNode.XMLnode(node.XMLnode)
			Return node\nextnode
		End Function
		
		Function XMLPrevNode.XMLnode(node.XMLnode)
			Return node\prevnode
		End Function
		
		Function XMLAddAttr(node.XMLnode,name$,value$)
			;gak debuglog "XML:adding attribute "+name$+"="+value$+" ("+Len(value$)+")"
			a.XMLattr = New XMLattr
			a\name$ = name$
			a\value$ = value$
			If node\attrcount = 0 Then
				node\firstattr = a
			Else
				node\lastattr\sibattr = a
			End If
			node\lastattr=a
			node\attrcount = node\attrcount + 1
			a\parent = node
			If Upper(a\value)="TRUE" a\value=1
			If Upper(a\Value)="FALSE" a\value=0
			If Upper(a\Value)="GRAPHICSWIDTH" a\value=GraphicsWidth()
			If Upper(a\Value)="GRAPHICSHEIGHT" a\value=GraphicsHeight()
		End Function
		
		Function XMLReadNode.XMLnode(infile,parent.XMLnode,pushed=False)
			mode = 0
			root.XMLnode = Null
			cnode.XMLnode = Null
			x.XMLnode = Null
			ispushed = False
			done = False
			While (Not done) And (Not Eof(infile))
				c = ReadByte(infile)
				If c<32 Then c=32
				ch$=Chr$(c)
		;		;gak debuglog "{"+ch$+"} "+c+" mode="+mode
				Select mode
				  Case 0 ; looking for the start of a tag, ignore everything else
					If ch$ = "<" Then 
						mode = 1; start collecting the tag
					End If
				  Case 1 ; check first byte of tag, ? special tag
					If ch$ = "?" Or ch$ = "!" Then
				 		mode = 0; class special nodes as garbage & consume
					Else
						If ch$ = "/" Then 
							mode = 2 ; move to collecting end tag
							x\endtag$=ch$
							;gak debuglog "** found end tag"
						Else
							cnode=x
							x.XMLnode = XMLOpennode(cnode)
							If cnode=Null Then root=x
							x\tag$=ch$
							mode = 3 ; move to collecting start tag
						End If
					End If
				  Case 2 ; collect the tag name (close tag)
					If ch$=">" Then 
						mode = 0 ; end of the close tag so jump out of loop
						;done = True
						x = XMLclosenode(x)
					Else 
						x\endtag$ = x\endtag$ + ch$
					End If
				  Case 3 ; collect the tag name 
					If ch$=" " Then 
						;gak debuglog "TAG:"+x\tag$
						mode = 4 ; tag name collected, move to collecting attributes
					Else 
						If ch$="/" Then 
							;gak debuglog "TAG:"+x\tag$
							x\endtag$=x\tag$
							mode = 2; start/end tag combined, move to close
						Else
							If ch$=">" Then
								;gak debuglog "TAG:"+x\tag$
								mode = 20; tag closed, move to collecting value
							Else
								x\tag$ = x\tag$ + ch$
							End If
						End If
					End If
				  Case 4 ; start to collect attributes
					If Lower(ch$)>="a" And Lower(ch$)<="z" Then 
						aname$=ch$;
						mode = 5; move to collect attribute name
					Else
						If ch$=">" Then
							x\value$=""
							mode = 20; tag closed, move to collecting value
						Else
							If ch$="/" Then 
								mode = 2 ; move to collecting end tag
								x\endtag$=ch$
								;gak debuglog "** found end tag"
							End If
						End If
					End If
				  Case 5 ; collect attribute name
					If ch$="=" Then
					  ;gak debuglog "ATT:"+aname$
					  aval$=""
					  mode = 6; move to collect attribute value
					Else
					  aname$=aname$+ch$
					End If
				  Case 6 ; collect attribute value
					If c=34 Then
						mode = 7; move to collect string value
					Else
						If c <= 32 Then 
							;gak debuglog "ATV:"+aname$+"="+aval$
							XMLAddAttr(x,aname$,aval$)
							mode = 4; start collecting a new attribute
						Else
					   		aval$=aval$+ch$
						End If
					End If
				  Case 7 ; collect string value
					If c=34 Then
						;gak debuglog "ATV:"+aname$+"="+aval$
						XMLADDattr(x,aname$,aval$)
						mode = 4; go and collect next attribute
					Else
						aval$=aval$+ch$
					End If
				  Case 20 ; COLLECT THE VALUE PORTION
					If ch$="<" Then 
						;gak debuglog "VAL:"+x\tag$+"="+x\value$
						mode=1; go to tag checking
					Else
						x\value$=x\value$+ch$
					End If
				End Select
				
				If Eof(infile) Then done=True
			
			Wend
		
			Return root
		
		End Function
		
		; write out an XML node (and children)
		Function XMLWriteNode(outfile,node.XMLnode,tab$="")
		;	;gak debuglog "Writing...."+node\tag$+".."
			s$="<"+node\tag$
			a.XMLattr = node\firstattr
			While a<>Null
				s$ = s$+" "+Lower(a\name$)+"="+Chr$(34)+a\value$+Chr$(34)
				a = a\sibattr
			Wend
			
			If node\value$="" And node\childcount = 0 Then
				s$=s$+"/>"
				et$=""
			Else
				s$=s$+">"+node\value$
				et$="</"+node\tag$+">"
			End If
			
			WriteLine outfile,XMLcleanStr$(tab$+s$)
			n.XMLnode = node\firstchild
			While n <> Null
				XMLwriteNode(outfile,n,tab$+"  ")
				n = n\nextnode
			Wend
			
			If et$<> "" Then WriteLine outfile,XMLCleanStr$(tab$+et$)
		
		End Function
		
		; remove non-visible chars from the output stream
		Function XMLCleanStr$(s$)
			a$=""
			For i = 1 To Len(s$)
				If Asc(Mid$(s$,i,1))>=32 Then a$ = a$ +Mid$(s$,i,1)

			Next
			Return a$
		
		End Function
		
		; attribute functions
		; return an attribute of a given name
		Function XMLFindAttr.XMLattr(node.XMLnode,name$)
			ret.XMLattr = Null
			If node <> Null Then 
				a.XMLattr = node\firstattr
				done = False
				While ret=Null And a<>Null 
					If Lower(name$)=Lower(a\name$) Then
						ret = a
					End If
					a = a\sibattr
				Wend
			End If
			Return ret
		End Function
		
		; return an attribute value as a string
		Function XMLAttrValueStr$(node.XMLnode,name$,dflt$="")
			ret$=dflt$
			a.XMLattr = XMLfindattr(node,name$)
			If a <> Null Then ret$=a\value$
			Return ret$
		End Function
		
		; return an attribute value as an integer
		Function XMLAttrValueInt(node.XMLnode,name$,dflt=0)
			ret=dflt
			a.XMLattr = XMLfindattr(node,name$)
			If a <> Null Then ret=a\value
			Return ret
		End Function
		
		; return an attribute value as a float
		Function XMLAttrValueFloat#(node.XMLnode,name$,dflt#=0)
			ret#=dflt#
			a.XMLattr = XMLfindattr(node,name$)
			If a <> Null Then ret#=a\value
			Return ret
		End Function
		
		Function XMLHasChildren(node.XMLnode)
			Return node\firstchild <> Null
		End Function
		
		Function XMLHasAttributes(node.XMLnode)
			Return node\firstattr <> Null
		End Function
		
		Function XMLGetChild.XMLNode(node.XMLNode,index=0)
			child.XMLNode = node\FirstChild
			For i = 0 To index-1
				child.XMLNode = child\nextnode
			Next
			Return child
		End Function
		
		Function XMLGetFirstAttribute.XMLAttr(node.XMLNode)
			Return node\firstattr
		End Function
		
		Function XMLGetNextAttribute.XMLAttr(attr.XMLAttr)
			Return attr\sibattr
		End Function
		
		Function XMLGetParent.XMLNode(node.XMLNode)
			Return node\parent
		End Function
		
		Function PrintXMLNode(i.XMLNode,start$="")
			If i = Null Then Return
			Write start+"<"+i\tag
			a.XMLAttr = XMLGetFirstAttribute(i)
			While a <> Null
				Write " "+a\name+"="+Chr(34)+a\value+Chr(34)
				a = XMLGetNextAttribute(a)
			Wend
			Write ">"
			Print ""
			
			f.XMLNode = XMLGetChild(i,0)
			While f.XMLNode <> Null
				PrintXMLNode(f,start+"    ")
				f = XMLNextNode(f)
			Wend
			Print start+"</"+i\tag+">"
		End Function
	;#End Region
;#End Region
