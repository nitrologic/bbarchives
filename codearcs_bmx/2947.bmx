; ID: 2947
; Author: Jur
; Date: 2012-06-01 13:10:22
; Title: Gadgets tree
; Description: A tree structure with the nodes made from gadgets

SuperStrict

Import MaxGui.Drivers
Import MaxGUI.ProxyGadgets

Local window:TGadget = CreateWindow("Gadgets Tree",100,100,420,620, Null, WINDOW_DEFAULT|WINDOW_HIDDEN)
Local GadgetsTree:TGadgetsTree = New TGadgetsTree.Make(10,10,400,500,window) 

Local Gadget:TGadget
Local NodeA:TgtNode[] = New TgtNode[10]
For Local i:Int=0 Until NodeA.length
	NodeA[i] = GadgetsTree.AddNode(22, GadgetsTree.Root)
	Gadget = NodeA[i].AddGadget(CreateButton("",0,0,20,20, NodeA[i].Group, BUTTON_CHECKBOX), CENTER_VERTICALLY)
	Gadget = NodeA[i].AddGadget(CreateButton("Remove"+String(i), GadgetX(Gadget)+GadgetWidth(Gadget)+5,0,70,20,NodeA[i].Group), CENTER_VERTICALLY)
	Gadget = NodeA[i].AddGadget(CreateSlider(GadgetX(Gadget)+GadgetWidth(Gadget)+5,0,100,20,NodeA[i].Group,SLIDER_TRACKBAR|SLIDER_HORIZONTAL), CENTER_VERTICALLY|SELECTING_GADGET)
	Gadget = NodeA[i].AddGadget(CreateLabel("Click here to select node:"+String(i),GadgetX(Gadget)+GadgetWidth(Gadget)+5,0,170,20,NodeA[i].Group), CENTER_VERTICALLY|SELECTING_GADGET)

	If i>5 Then Continue
	
	Local NodeB:TgtNode[] = New TgtNode[3]
	For Local j:Int=0 Until NodeB.length
		NodeB[j] = GadgetsTree.AddNode(34, NodeA[i])
		Gadget = NodeB[j].AddGadget(CreateButton("radio:"+String(i)+String(j),0,0,80,20,NodeB[j].Group, BUTTON_RADIO), CENTER_VERTICALLY)
		'Gadget = NodeB[j].AddGadget(CreateSpinner(0,0,130,20,NodeB[j].Group, SPINNER_TRACKBAR), CENTER_VERTICALLY)
		Gadget = NodeB[j].AddGadget(CreateButton("radio:"+String(i)+String(j),GadgetX(Gadget)+GadgetWidth(Gadget)+5,0,80,20, NodeB[j].Group, BUTTON_RADIO), CENTER_VERTICALLY|SELECTING_GADGET)
		Gadget = NodeB[j].AddGadget(CreatePanel(GadgetX(Gadget)+GadgetWidth(Gadget)+5,0,40,20,NodeB[j].Group), CENTER_VERTICALLY)
		SetGadgetColor(Gadget, Rand(0,255), Rand(0,255), Rand(0,255))
		
		'Rem
		Local NodeC:TgtNode[] = New TgtNode[2]
		For Local k:Int=0 Until NodeC.length
			NodeC[k] = GadgetsTree.AddNode(22, NodeB[j])
			Gadget = NodeC[k].AddGadget(CreateLabel("Enter text: "+String(k),0,0,100,20, NodeC[k].Group, LABEL_RIGHT), CENTER_VERTICALLY)
			Gadget = NodeC[k].AddGadget(CreateTextField(GadgetX(Gadget)+GadgetWidth(Gadget)+5,0,150,20, NodeC[k].Group), CENTER_VERTICALLY)
		Next	
		'EndRem
	Next	
Next

Local NodeD:TgtNode = GadgetsTree.AddNode(50, NodeA[3], 1)   'this will insert node at id=1 of NodeA[3] kids
NodeD.AddGadget(CreateButton("BIG BUTTON", 0,0, 120,40, NodeD.Group), CENTER_VERTICALLY)

GadgetsTree.MoveNode(NodeA[5], NodeA[5].Parent, 8)  'move NodeA[5] to position 8 within the list

Local par:TgtNode = TgtNode(NodeA[1].Kids.First())
GadgetsTree.MoveNode(NodeA[3], par, 0)	'move NodeA[3] to the NodeA[1].Kids at position 0   



GadgetsTree.Refresh()  'redraw nodes at their tree positions
ShowGadget(window)



While True
	WaitEvent 
	Print CurrentEvent.ToString()
	Select EventID()
		Case EVENT_WINDOWCLOSE
			End
			
		Case EVENT_GADGETACTION
			
			'identify gadget as a node´s gadget
			Local Gadget:TGadget = TGadget(EventSource())
			Local NodeGadgetExtra:TgtEvent = TgtEvent(Gadget.Extra)
			If NodeGadgetExtra Then
				Print "node id:"+NodeGadgetExtra.Node.id+" depth:"+NodeGadgetExtra.Node.depth+"  gadget action event:"+CurrentEvent.ToString()
				
				Print "gadget text:"+GadgetText(Gadget)
				'a quick test for node removal
				If GadgetText(Gadget).contains("Remove") Then
					GadgetsTree.RemoveNode(NodeGadgetExtra.Node)
				EndIf
			EndIf
	End Select
Wend



Type TGadgetsTree
	Field ScrollPanel:TScrollPanel
	Field Panel:TGadget
	'---
	Field Root:TgtNode
	Field SelectedNode:TgtNode
	
	'---graphics for markers
	Field PixmapKidsExpanded:TPixmap
	Field PixmapKidsCollapsed:TPixmap
	Field PixmapParentByPassLine:TPixmap
	Field MarkerAreaPixmapsBuffer:TgtMarkerAreaPixmaps[]   'index = node´s height - every node with different height needs different pixmaps 
	
	Field markerAreaWidth:Int=20
	Field xBorderOffset:Int=10
	Field yBorderOffset:Int=10
	Field rBack:Int=255, gBack:Int=255, bBack:Int=255
	Field rBackSelected:Int=0, gBackSelected:Int=0, bBackSelected:Int=128
	Field rText:Int=0, gText:Int=0, bText:Int=0
	Field rTextSelected:Int=255, gTextSelected:Int=255, bTextSelected:Int=255
	
	'---
	Field nodesWidth:Int
	Field nodesHeight:Int

	
	Method Make:TGadgetsTree(x:Int,y:Int,width:Int,height:Int,group:TGadget)
		
		ScrollPanel = CreateScrollPanel(x,y,width,height,group,SCROLLPANEL_SUNKEN)
		SetGadgetLayout ScrollPanel,EDGE_ALIGNED,EDGE_ALIGNED,EDGE_ALIGNED,EDGE_ALIGNED
		SetGadgetColor(ScrollPanel, rBack,gBack,bBack)
		
		Panel = ScrollPanelClient(ScrollPanel)
		'SetGadgetColor(Panel, 255,128,255)
		'SetProxy(Panel)
		
		Root = New TgtNode
		Root.Group = CreatePanel(xBorderOffset,yBorderOffset,1,1,Panel)  'dummy panel, for kids to get starting position
		HideGadget(Root.Group)
		
		PixmapKidsExpanded = MakeExpanedMarkerPixmap()
		PixmapKidsCollapsed = MakeCollapsedMarkerPixmap()
		PixmapParentByPassLine = MakeParentByPassLinePixmap()

		AddHook(EmitEventHook,EventHook,Self)
		Return Self
	EndMethod
	
	
	
	Method SetBackColors(_rBack:Int, _gBack:Int, _bBack:Int, _rBackSelected:Int, _gBackSelected:Int, _bBackSelected:Int)
		rBack = _rBack
		gBack = _gBack
		bBack = _bBack
		rBackSelected = _rBackSelected
		gBackSelected = _gBackSelected
		bBackSelected = _bBackSelected
		SetGadgetColor(ScrollPanel, rBack,gBack,bBack)
	EndMethod
	
	Method SetTextColors(_rText:Int, _gText:Int, _bText:Int, _rTextSelected:Int, _gTextSelected:Int, _bTextSelected:Int)
		rText = _rText
		gText = _gText
		bText = _bText
		rTextSelected = _rTextSelected
		gTextSelected = _gTextSelected
		bTextSelected = _bTextSelected
	EndMethod


	
	'Add a new node at position "_pos" (-1 = added at the end); "_height" = height of the node
	Method AddNode:TgtNode(_height:Int, _ParentNode:TgtNode, _pos:Int=-1, _Extra:Object=Null)
		
		Local Node:TgtNode = New TgtNode
		Node.Parent = _ParentNode
		Node.depth = _ParentNode.depth+1
		Node.Extra = _Extra
		Node.GadgetsTree = Self

		Node.Group = CreatePanel(0, 0, 5, _height, Panel)  '5... some value for starting width
		
		Node.MarkerArea = CreatePanel(0, 0, markerAreaWidth, _height, Panel)
		SetGadgetExtra(Node.MarkerArea, New TgtEvent.Make(Self, Null, 0, 1))
		SetGadgetLayout(Node.MarkerArea, EDGE_ALIGNED,EDGE_CENTERED,EDGE_ALIGNED,EDGE_CENTERED)
		If _height>=MarkerAreaPixmapsBuffer.length Then MarkerAreaPixmapsBuffer = MarkerAreaPixmapsBuffer[.._height+1]
		If MarkerAreaPixmapsBuffer[_height]=Null Then
			MarkerAreaPixmapsBuffer[_height] = New TgtMarkerAreaPixmaps.Make(markerAreaWidth, _height, PixmapKidsExpanded.width, PixmapKidsExpanded.height)
		EndIf	
		Node.MarkerAreaPixmaps = MarkerAreaPixmapsBuffer[_height]
		'---
		Node.Marker = CreatePanel(0, (_height-PixmapKidsExpanded.height)/2, PixmapKidsExpanded.width, PixmapKidsExpanded.height, Node.MarkerArea, PANEL_ACTIVE) 
		SetGadgetExtra(Node.Marker, New TgtEvent.Make(Self, Null, 0, 1))
		SetGadgetLayout(Node.Marker, EDGE_ALIGNED,EDGE_CENTERED,EDGE_ALIGNED,EDGE_CENTERED)
		'---
		Node.ParentByPassLine = CreatePanel(0, 0, 1, _height, Panel)
		SetGadgetExtra(Node.ParentByPassLine, New TgtEvent.Make(Self, Null, 0, 1))
		SetGadgetPixmap(Node.ParentByPassLine, PixmapParentByPassLine, PANELPIXMAP_TILE)
		HideGadget(Node.ParentByPassLine)
		
		'---add node to parents kids
		AddNodeToList(Node, _pos) 
		
		Return Node
	EndMethod
	
	
	
	Method AddNodeToList(Node:TgtNode, pos:Int)
	
		If pos=-1 Or pos>Node.Parent.Kids.Count() Then pos=Node.Parent.Kids.Count()
		Node.id = pos
		
		Local Link:TLink = Node.Parent.Kids.FirstLink()
		If Link Then
			Local i:Int
			While i<Node.id
				Link = Link.NextLink()
				i:+1
			Wend
		EndIf
		If Link Then	
			Node.Parent.Kids.InsertBeforeLink(Node,Link)
		Else
			Node.Parent.Kids.AddLast(Node)	
		EndIf
		'update ids
		Local i:Int
		For Local n:TgtNode=EachIn Node.Parent.Kids
			n.id = i
			i:+1
		Next
	EndMethod
	
	
	
	'redraw tree
	Method Refresh()

		HideGadget(Panel)
		nodesWidth = 0
		nodesHeight:Int = RefreshNode(Root)
		SetGadgetShape(Panel, GadgetX(Panel), GadgetY(Panel), nodesWidth+xBorderOffset*2, nodesHeight+yBorderOffset*2)
		RedrawGadget(Panel)
		ShowGadget(Panel)
		
	EndMethod
	

	Method RemoveNode(Node:TgtNode)
		Node.Parent.Kids.Remove(Node)
		'update ids
		Local i:Int
		For Local n:TgtNode=EachIn Node.Parent.Kids
			n.id = i
			i:+1
		Next
		Node.RemoveData()
		Refresh()
	EndMethod
	
	
	Method ClearTree()
		For Local n:TgtNode=EachIn Root.Kids
			RemoveNode(n)
		Next
	EndMethod
	
	
	Method MoveNode(Node:TgtNode, NewParent:TgtNode, pos:Int)
	
		If Node.Parent=NewParent And Node.id=pos Then Return
		
		'remove node
		Node.Parent.Kids.Remove(Node)
		If Node.Parent<>NewParent Then
			'update ids
			Local i:Int
			For Local n:TgtNode=EachIn Node.Parent.Kids
				n.id = i
				i:+1
			Next
		EndIf
		
		'add to the new parent
		Node.Parent = NewParent
		Node.depth = NewParent.depth+1
		AddNodeToList(Node, pos)
	
	EndMethod
	
	
	
	
	'PRIVATE
	
	Method RefreshNode:Int(Node:TgtNode)
		
		Local xParent:Int = GadgetX(Node.Group)
		Local yParent:Int = GadgetY(Node.Group)
		Local x:Int
		Local y:Int = yParent + GadgetHeight(Node.Group)
		For Local n:TgtNode=EachIn Node.Kids
			x = xParent + markerAreaWidth
			'Print "A node id:"+n.id+" depth:"+n.depth+" x:"+x+" y:"+y+"  n.kidsExpanded:"+ n.kidsExpanded+" GadgetHeight(n.Panel):"+GadgetHeight(n.Panel)

			'If n.depth=2 Then Print "x:"+x+" y:"+y+"  n.kidsExpanded:"+ n.kidsExpanded
			SetGadgetShape(n.MarkerArea, x-markerAreaWidth, y, GadgetWidth(n.MarkerArea), GadgetHeight(n.MarkerArea))
			SetGadgetShape(n.Group, x, y, GadgetWidth(n.Group), GadgetHeight(n.Group))
			
			ShowGadget(n.MarkerArea)
			ShowGadget(n.Group)
	
			'----- marker area
			If Node=Root And n=Node.Kids.First() Then
				If n.Kids.IsEmpty()=True Then 
					SetGadgetPixmap(n.MarkerArea, n.MarkerAreaPixmaps.FirstNodeNoKids)
					HideGadget(n.Marker)
				Else
					SetGadgetPixmap(n.MarkerArea, n.MarkerAreaPixmaps.FistNodeKids)
					ShowGadget(n.Marker)
				EndIf
				
			ElseIf n=Node.Kids.Last() Then
				If n.Kids.IsEmpty()=True Then 
					SetGadgetPixmap(n.MarkerArea, n.MarkerAreaPixmaps.LastNodeNoKids)
					HideGadget(n.Marker)
				Else
					SetGadgetPixmap(n.MarkerArea, n.MarkerAreaPixmaps.LastNodeKids)
					ShowGadget(n.Marker)
				EndIf
			Else
				'Print "B node id:"+n.id+" depth:"+n.depth+" x:"+x+" y:"+y+"  n.kidsExpanded:"+ n.kidsExpanded+" GadgetHeight(n.Panel):"+GadgetHeight(n.Panel)
				If n.Kids.IsEmpty()=True Then 
					SetGadgetPixmap(n.MarkerArea, n.MarkerAreaPixmaps.IntermediateNodeNoKids)
					HideGadget(n.Marker)
				Else
					SetGadgetPixmap(n.MarkerArea, n.MarkerAreaPixmaps.IntermediateNodeKids)
					ShowGadget(n.Marker)
				EndIf
			EndIf
				
			If n.kidsExpanded=True Then
				'If n.depth=2 Then Print "B x:"+x+" y:"+y
				SetGadgetPixmap(n.Marker, PixmapKidsExpanded)
				y:+RefreshNode(n)
			Else
				SetGadgetPixmap(n.Marker, PixmapKidsCollapsed)
				HideKids(n)
				y:+GadgetHeight(n.Group)
			EndIf
			
			'n.Redraw()
			
			nodesWidth = Max(nodesWidth, GadgetX(n.Group)+GadgetWidth(n.Group))
		Next
		
		Local hKids:Int = y-yparent   'height of kids
		
		If Node.ParentByPassLine Then
			If Node.kidsExpanded=True Then
				SetGadgetShape(Node.ParentByPassLine, GadgetX(Node.Group)-markerAreaWidth+PixmapKidsExpanded.width/2+1, GadgetY(Node.Group), GadgetWidth(Node.ParentByPassLine), hKids)
				ShowGadget(Node.ParentByPassLine)
			EndIf
		EndIf
		
		Return hKids   
	EndMethod
	

	
	Method HideKids(Node:TgtNode)
	
		HideGadget(Node.ParentByPassLine)
		For Local n:TgtNode=EachIn Node.Kids
			'Print" hide"
			HideGadget(n.Group)
			HideGadget(n.MarkerArea)
			HideGadget(n.ParentByPassLine)
			HideKids(n)
		Next
	EndMethod
	

	Method OnEvent:Int(Event:TEvent, Gadget:TGadget)
		OnEventNode(Root, Event, Gadget)
	EndMethod
	
	
	Method OnEventNode:Int(Node:TgtNode, Event:TEvent, Gadget:TGadget)
		
		For Local n:TgtNode=EachIn Node.Kids
			If TgtEvent(Gadget.Extra).Node=n Then
				If TgtEvent(Gadget.Extra).nodeSelector=True Then
					If n<>SelectedNode Then
						If Event.id=EVENT_MOUSEDOWN Or Event.id=EVENT_GADGETACTION Then
							n.MarkAsSelected()
							If SelectedNode Then
								SelectedNode.MarkAsUnselected()
							EndIf
							SelectedNode=n
						EndIf
					EndIf
				EndIf
				Return True
				
			ElseIf Event.Source=n.Marker Then
				If Event.id=EVENT_MOUSEDOWN Then
					n.kidsExpanded = Not n.kidsExpanded
					Refresh()
					Return True
				EndIf
			EndIf
			
			Local gadgetFound:Int = OnEventNode(n, Event, Gadget)
			If gadgetFound=True Then Return True
		Next
		
		Return False
	EndMethod
			

	Function EventHook:Object(id:Int,data:Object,context:Object)

		Local event:TEvent = TEvent(data)
		If event Then
			Local Gadget:TGadget = TGadget(event.Source)
			If Gadget Then
				If TgtEvent(Gadget.Extra) Then
					TgtEvent(Gadget.Extra).GadgetsTree.OnEvent(event, Gadget)  'perform tree operations if needed
				EndIf
			EndIf
		EndIf
		Return data
	End Function
	
EndType



Const CENTER_VERTICALLY:Int=1
Const SELECTING_GADGET:Int=2


Type TgtNode
	'node information
	Field GadgetsTree:TGadgetsTree
	Field Parent:TgtNode
	Field Kids:TList=New TList
	Field id:Int
	Field depth:Int
	Field kidsExpanded:Int
	
	'gadgets
	Field Group:TGadget
	Field Gadgets:TList=New TList

	'marker
	Field MarkerArea:TGadget
	Field Marker:TGadget
	Field ParentByPassLine:TGadget
	Field MarkerAreaPixmaps:TgtMarkerAreaPixmaps
	
	Field Extra:Object  'use this to connect nodes to your classes
	
	
	
	Method AddGadget:TGadget(Gadget:TGadget, flags:Int=CENTER_VERTICALLY)
		'DebugStop()
		
		SetGadgetLayout(Gadget, EDGE_ALIGNED,EDGE_CENTERED,EDGE_ALIGNED,EDGE_CENTERED)
		If flags & CENTER_VERTICALLY Then 
			SetGadgetShape(Gadget, GadgetX(Gadget), (GadgetHeight(Group)-GadgetHeight(Gadget))/2, GadgetWidth(Gadget), GadgetHeight(Gadget))
		EndIf
		
		Local groupWidth:Int = Max(GadgetWidth(Group), GadgetX(Gadget)+GadgetWidth(Gadget))
		SetGadgetShape(Group, GadgetX(Group), GadgetY(Group), groupWidth, GadgetHeight(Group))
		
		Local selectingGadget:Int
		If flags & SELECTING_GADGET Then
			selectingGadget=True
			If GadgetClass(Gadget)=GADGET_LABEL Then
				SetGadgetSensitivity(Gadget, SENSITIZE_MOUSE)
			EndIf
		EndIf
		SetGadgetExtra(Gadget, New TgtEvent.Make(GadgetsTree, Self, selectingGadget, 0))
		Gadgets.AddLast(Gadget)
		
		Return Gadget
	EndMethod

	
	Method MarkAsSelected()
		SetGadgetColor(Group, GadgetsTree.rBackSelected, GadgetsTree.gBackSelected, GadgetsTree.bBackSelected)
		For Local g:TGadget=EachIn Gadgets
			If GadgetClass(g)=GADGET_LABEL Then
				SetGadgetColor(g, GadgetsTree.rBackSelected, GadgetsTree.gBackSelected, GadgetsTree.bBackSelected)
				SetGadgetTextColor(g,  GadgetsTree.rTextSelected, GadgetsTree.gTextSelected, GadgetsTree.bTextSelected)
			EndIf
		Next	 
	EndMethod
	
	
	Method MarkAsUnselected()
		SetGadgetColor(Group, GadgetsTree.rBack, GadgetsTree.gBack, GadgetsTree.bBack)
		For Local g:TGadget=EachIn Gadgets
			If GadgetClass(g)=GADGET_LABEL Then
				SetGadgetColor(g, GadgetsTree.rBack, GadgetsTree.gBack, GadgetsTree.bBack)
				SetGadgetTextColor(g, GadgetsTree.rText, GadgetsTree.gText, GadgetsTree.bText)
			EndIf
		Next	 
	EndMethod
	
	

	Method Redraw()
		'Print "redraw"
		RedrawGadget(MarkerArea)
		RedrawGadget(Marker)
		RedrawGadget(ParentByPassLine)
		RedrawGadget(Group)
		For Local g:Tgadget=EachIn gadgets
			RedrawGadget(g)
		Next
	EndMethod
	
	
	Method RemoveData()
		For Local g:Tgadget=EachIn gadgets
			FreeGadget(g)
		Next
		FreeGadget(Group)
		FreeGadget(MarkerArea)
		FreeGadget(Marker)
		FreeGadget(ParentByPassLine)
		GadgetsTree = Null
		Parent = Null
		Extra = Null

		For Local n:TgtNode=EachIn Kids
			n.RemoveData()
		Next
		Kids = Null
		Gadgets = Null
	EndMethod
	
EndType	



Type TgtEvent
	Field GadgetsTree:TGadgetsTree
	Field Node:TgtNode
	Field nodeSelector:Int
	Field marker:Int
	
	
	Method Make:TgtEvent(_GadgetsTree:TGadgetsTree, _Node:TgtNode, _nodeSelector:Int, _marker:Int)
		GadgetsTree = _GadgetsTree
		Node = _Node
		nodeSelector = _nodeSelector
		marker = _marker
		Return Self
	EndMethod
EndType	
	








Type TgtMarkerAreaPixmaps
	Field FirstNodeNoKids:TPixmap
	Field FistNodeKids:TPixmap
	Field IntermediateNodeNoKids:TPixmap
	Field IntermediateNodeKids:TPixmap
	Field LastNodeNoKids:TPixmap
	Field LastNodeKids:TPixmap
	
	
	Method Make:TgtMarkerAreaPixmaps(width:Int, height:Int, markerWidth:Int, markerHeight:Int)
		
		Local MarkerEmpty:TPixmap = CreatePixmap(markerWidth, markerHeight, PF_RGBA8888)
		MarkerEmpty.ClearPixels(0)
		
		Local xPos:Int = markerWidth/2+1
		Local yPos:Int = height/2
		
		'-----
		FirstNodeNoKids = CreatePixmap(width, height, PF_RGBA8888)
		FirstNodeNoKids.ClearPixels(0)
		
		For Local x:Int=xPos Until width Step 2
			WritePixel(FirstNodeNoKids, x, yPos, $FF000000)
		Next
		For Local y:Int=yPos Until height Step 2
			WritePixel(FirstNodeNoKids, xPos, y, $FF000000)
		Next 
		'with kids - cleared area for marker
		FistNodeKids =FirstNodeNoKids.Copy()
		FistNodeKids.Paste(MarkerEmpty, xPos-markerWidth/2, yPos-markerHeight/2)
		
		'-----
		IntermediateNodeNoKids = CreatePixmap(width, height, PF_RGBA8888)
		IntermediateNodeNoKids.ClearPixels(0)
		For Local x:Int=xPos Until width Step 2
			WritePixel(IntermediateNodeNoKids, x, yPos, $FF000000)
		Next
		For Local y:Int=0 Until height Step 2
			WritePixel(IntermediateNodeNoKids, xPos, y, $FF000000)
		Next 
		'with kids - cleared area for marker
		IntermediateNodeKids =IntermediateNodeNoKids.Copy()
		IntermediateNodeKids.Paste(MarkerEmpty, xPos-markerWidth/2, yPos-markerHeight/2)
		
		'-----
		LastNodeNoKids = CreatePixmap(width, height, PF_RGBA8888)
		LastNodeNoKids.ClearPixels(0)
		For Local x:Int=xPos Until width Step 2
			WritePixel(LastNodeNoKids, x, yPos, $FF000000)
		Next
		For Local y:Int=0 To yPos Step 2
			WritePixel(LastNodeNoKids, xPos, y, $FF000000)
		Next
		
		'with kids - cleared area for marker
		LastNodeKids = LastNodeNoKids.Copy()
		LastNodeKids.Paste(MarkerEmpty, xPos-markerWidth/2, yPos-markerHeight/2)
		
		Return Self
	EndMethod
EndType




Function MakeParentByPassLinePixmap:TPixmap()

	Local Pixmap:TPixmap = CreatePixmap(1, 20, PF_RGBA8888)
	Pixmap.ClearPixels(0)
	For Local y:Int=0 Until Pixmap.height-1 Step 2
		WritePixel(Pixmap, 0, y, $FF000000) 
	Next	

	Return Pixmap
EndFunction	



Function MakeExpanedMarkerPixmap:TPixmap()
	
	Local mask:Int[][]
	mask = mask[..11]
	'For Local i:Int=0 Until mask.length
	'	mask[i] = mask[i][..11]
	'Next
	mask[0] = [1,1,1,1,1,1,1,1,1,1,1]
	mask[1] = [1,0,0,0,0,0,0,0,0,0,1]
	mask[2] = [1,0,0,0,0,0,0,0,0,0,1]
	mask[3] = [1,0,0,0,0,0,0,0,0,0,1]
	mask[4] = [1,0,0,0,0,0,0,0,0,0,1]
	mask[5] = [1,0,1,1,1,1,1,1,1,0,1]
	mask[6] = [1,0,0,0,0,0,0,0,0,0,1]
	mask[7] = [1,0,0,0,0,0,0,0,0,0,1]
	mask[8] = [1,0,0,0,0,0,0,0,0,0,1]
	mask[9] = [1,0,0,0,0,0,0,0,0,0,1]
	mask[10] = [1,1,1,1,1,1,1,1,1,1,1]
	
	Local Pixmap:TPixmap = CreatePixmap(mask.length, mask[0].length, PF_RGBA8888)
	Pixmap.ClearPixels(0)
	For Local x:Int=0 Until Pixmap.width
		For Local y:Int=0 Until Pixmap.height
			If mask[y][x]=1 Then WritePixel(Pixmap, x, y, $FF000000)
		Next
	Next
	
	Return Pixmap
	
EndFunction


Function MakeCollapsedMarkerPixmap:TPixmap()
	
	Local mask:Int[][]
	mask = mask[..11]
	'For Local i:Int=0 Until mask.length
	'	mask[i] = mask[i][..11]
	'Next
	mask[0] = [1,1,1,1,1,1,1,1,1,1,1]
	mask[1] = [1,0,0,0,0,0,0,0,0,0,1]
	mask[2] = [1,0,0,0,0,1,0,0,0,0,1]
	mask[3] = [1,0,0,0,0,1,0,0,0,0,1]
	mask[4] = [1,0,0,0,0,1,0,0,0,0,1]
	mask[5] = [1,0,1,1,1,1,1,1,1,0,1]
	mask[6] = [1,0,0,0,0,1,0,0,0,0,1]
	mask[7] = [1,0,0,0,0,1,0,0,0,0,1]
	mask[8] = [1,0,0,0,0,1,0,0,0,0,1]
	mask[9] = [1,0,0,0,0,0,0,0,0,0,1]
	mask[10] = [1,1,1,1,1,1,1,1,1,1,1]
	
	Local Pixmap:TPixmap = CreatePixmap(mask.length, mask[0].length, PF_RGBA8888)
	Pixmap.ClearPixels(0)
	For Local x:Int=0 Until Pixmap.width
		For Local y:Int=0 Until Pixmap.height
			If mask[y][x]=1 Then WritePixel(Pixmap, x, y, $FF000000)
		Next
	Next
	
	Return Pixmap
	
EndFunction
