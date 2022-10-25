; ID: 2820
; Author: JoshK
; Date: 2011-02-05 17:55:32
; Title: Move TreeViewNode
; Description: Moves a tree view node up and down expanded nodes

SuperStrict

Import maxgui.drivers

Private

Function TreeViewNodeExpanded:Int(node:TGadget)
	?win32
	Return TWindowsTreeNode(node)._expanded
	?
EndFunction

Function ListTreeNodes(node:TGadget,nodeofinterest:TGadget,depth:Int=0)
	treeviewnodearray=treeviewnodearray[..treeviewnodearray.length+1]
	treeviewnodearray[treeviewnodearray.length-1]=node
	treeviewnodedepth=treeviewnodedepth[..treeviewnodedepth.length+1]
	treeviewnodedepth[treeviewnodedepth.length-1]=depth		
	If nodeofinterest<>node
		If TreeViewNodeExpanded(node)
			For node=EachIn node.kids
				ListTreeNodes(node,nodeofinterest,depth+1)
			Next
		EndIf
	EndIf
EndFunction

Function GetTreeViewNodeIndex:Int(gadget:TGadget)
	Local n:Int
	Local child:TGadget
	
	For child=EachIn gadget.parent.kids
		If child=gadget Return n
		n:+1
	Next
	Return -1
EndFunction

Function CopyNodes(src:TGadget,dst:TGadget)
	Local srcchild:TGadget
	Local dstchild:TGadget
		
	For srcchild=EachIn src.kids
		dstchild=AddTreeViewNode(GadgetText(srcchild),dst,,GadgetExtra(srcchild))
		CopyNodes(srcchild,dstchild)
	Next
	If TreeViewNodeExpanded(src) ExpandTreeViewNode dst
EndFunction

Global treeviewnodearray:TGadget[]
Global treeviewnodedepth:Int[]

Public

Function MoveTreeViewNodeUp:TGadget(node:TGadget,tree:TGadget)',Filter:Int(node:TGadget)=Null)
	Local n:Int
	Local prevnode:TGadget
	Local root:TGadget
	Local i:Int,m:Int
	Local child:TGadget
	Local newnode:TGadget
	Local restoreselected:Int
	
	If node=SelectedTreeViewNode(tree) restoreselected=True
	treeviewnodearray=Null	
	treeviewnodedepth=Null	
	root=TreeViewRoot(tree)
	For child=EachIn root.kids
		ListTreeNodes(child,node)
	Next
	For n=0 To treeviewnodearray.length-1
		If treeviewnodearray[n]=node
			If n>0
				prevnode=treeviewnodearray[n-1]
				If prevnode.parent
					i=GetTreeViewNodeIndex(prevnode)
					If treeviewnodedepth[n-1]>treeviewnodedepth[n]
						If treeviewnodedepth[n-1]-treeviewnodedepth[n]=1
							newnode=AddTreeViewNode(GadgetText(node),prevnode.parent,,GadgetExtra(node))
						Else
							For m=1 To (treeviewnodedepth[n-1]-treeviewnodedepth[n])
								prevnode=prevnode.parent
							Next
							newnode=AddTreeViewNode(GadgetText(node),prevnode,,GadgetExtra(node))
						EndIf
					Else
						newnode=InsertTreeViewNode(i,GadgetText(node),prevnode.parent,,GadgetExtra(node))						
					EndIf
					Exit
				EndIf
			EndIf
			Exit
		EndIf
	Next
	If newnode
		CopyNodes(node,newnode)
		FreeGadget node
		If restoreselected SelectTreeViewNode newnode
		Return newnode
	Else
		Return node
	EndIf
EndFunction

Function MoveTreeViewNodeDown:TGadget(node:TGadget,tree:TGadget)',Filter:Int(node:TGadget)=Null)
	Local n:Int
	Local nextnode:TGadget
	Local root:TGadget
	Local i:Int
	Local child:TGadget
	Local newnode:TGadget
	Local restoreselected:Int
	
	If node=SelectedTreeViewNode(tree) restoreselected=True
	treeviewnodearray=Null	
	treeviewnodedepth=Null	
	root=TreeViewRoot(tree)
	For child=EachIn root.kids
		ListTreeNodes(child,node)
	Next
	
	If GetTreeViewNodeIndex(node)=node.parent.kids.count()-1
		i=GetTreeViewNodeIndex(node.parent)
		newnode=InsertTreeViewNode(i+1,GadgetText(node),node.parent.parent,,GadgetExtra(node))
	Else
		For n=0 To treeviewnodearray.length-1
			If treeviewnodearray[n]=node
				If n<treeviewnodearray.length-1
					nextnode=treeviewnodearray[n+1]
					If nextnode.parent
						i=GetTreeViewNodeIndex(nextnode)					
						If TreeViewNodeExpanded(nextnode)
							If treeviewnodedepth[n+1]=treeviewnodedepth[n]-1
								newnode=InsertTreeViewNode(i,GadgetText(node),nextnode.parent,,GadgetExtra(node))
							Else
								newnode=InsertTreeViewNode(0,GadgetText(node),nextnode,,GadgetExtra(node))
							EndIf
						Else
							If treeviewnodedepth[n+1]<treeviewnodedepth[n]
								newnode=InsertTreeViewNode(i,GadgetText(node),nextnode.parent,,GadgetExtra(node))
							Else
								newnode=InsertTreeViewNode(i+1,GadgetText(node),nextnode.parent,,GadgetExtra(node))						
							EndIf
						EndIf
						Exit
					EndIf
				Else
					If node.parent.parent
						newnode=AddTreeViewNode(GadgetText(node),node.parent.parent,,GadgetExtra(node))
						Exit
					EndIf
				EndIf
				Exit
			EndIf
		Next
	EndIf
	If newnode
		CopyNodes(node,newnode)
		FreeGadget node
		If restoreselected SelectTreeViewNode newnode
		Return newnode
	Else
		Return node
	EndIf
EndFunction
