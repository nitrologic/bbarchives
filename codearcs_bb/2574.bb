; ID: 2574
; Author: Bobysait
; Date: 2009-08-28 11:36:09
; Title: TMap For Blitz3D
; Description: Use Hash for sorting datas

Const TMap_RED%		=	-1
Const TMap_BLACK%	=	+1

Type TNode
	Field key_$
	Field value_$
	Field Color_%
	Field parent_.TNode
	Field left_.TNode
	Field right_.TNode
End Type

Global nil.TNode=	New TNode
	nil\Color_	=	TMap_BLACK
	nil\parent_	=	nil
	nil\left_	=	nil
	nil\right_	=	nil


Type TMap
	Field root_.TNode
End Type

Function CreateMap.TMap()
	Local map.TMap=New TMap
	map\root_=nil
	Return map
End Function

Function MapDelete(map.TMap)
	MapClear(map)
	Delete map
End Function

Function MapClear(map.TMap)
	If map\root_=nil Return
	TNodeClear(map\root_)
	map\root_=nil
End Function

Function MapIsEmpty%(map.TMap)
	Return map\root_=nil
End Function

Function MapInsert( map.TMap,key$,value$ )
	Local node.TNode	=	map\root_
	Local parent.TNode	=	nil
	Local cmp%			=	0

	While node<>nil
		parent=node
		cmp=StringCompare(key,node\key_ )
		If cmp>0
			node=node\right_
		Else If cmp<0
			node=node\left_
		Else
			node\value_=value
			Return
		EndIf
	Wend
	
	node			=	TNodeNew()
	node\key_		=	key
	node\value_		=	value
	node\Color_		=	TMap_RED
	node\parent_	=	parent
	
	If parent=nil
		map\root_=node
		Return
	EndIf
	If cmp>0
		parent\right_=node
	Else
		parent\left_=node
	EndIf

	TMapInsertFixup map,node
End Function

Function MapContains(map.TMap,key$)
	Local obj.TNode=TMapFindNode(map,key)
	Return obj<>nil
End Function

Function MapValueForKey$(map.TMap,key$)
	Local node.TNode=TMapFindNode(map,key)
	If node<>Null Return node\value_
End Function

Function MapCopy.TMap(map.TMap)
	Local copy.TMap	=	CreateMap()
	copy\root_		=	TNodeCopy(map\root_,nil)
	Return copy
End Function

Function MapRemove(map.TMap , key$)
	Local node.TNode=TMapFindNode(map,key)
	If node=nil Return 0
	TMapRemoveNode(map,node)
	Return 1
End Function

Type TMapList
	Field Root_.TNode
	Field Last_.TNode
End Type

Function MapList.TMapList(map.TMap)
	Local List.TMapList=New TMapList
	List\Root_		=	TMapFirstNode(map)
	List\Last_		=	TMapLastNode(map)
	Return List
End Function

Function FirstNode.TNode(list.TMapList)
	Return list\Root_
End Function

Function LastNode.TNode(list.TMapList)
	Return list\Last_
End Function

Function NextNode.TNode(list.TMapList ,node.TNode)
	If node=list\Last_			Return Null
	Return TNodeNextNode(node);node\right_
End Function

Function PrevNode.TNode(list.TMapList ,node.TNode)
	If node=list\Root_			Return Null
	Return TNodePrevNode(node);node\left_
End Function

Function NodeKey$(node.TNode)
	Return node\key_
End Function

Function NodeValue$(node.TNode)
	Return node\value_
End Function

; -----------
; - PRIVATE -
; -----------
Function TMAP__________PRIVATE() End Function

Function TMapInsertFixup (map.TMap,node.TNode)
	Local uncle.TNode=nil
	While node\parent_\Color_=TMap_RED And node\parent_\parent_<>nil

		If node\parent_=node\parent_\parent_\left_
			uncle = node\parent_\parent_\right_
			If uncle\Color_			=	TMap_RED
				node\parent_\Color_	=	TMap_BLACK
				uncle\Color_		=	TMap_BLACK
				uncle\parent_\Color_=	TMap_RED
				node				=	uncle\parent_
			Else
				If node=node\parent_\right_
					node			=	node\parent_
					TMapRotateLeft		(map,node)
				EndIf
				node\parent_\Color_	=	TMap_BLACK
				node\parent_\parent_\Color_=TMap_RED
				TMapRotateRight			(map,node\parent_\parent_)
			EndIf
		Else
			uncle=node\parent_\parent_\left_
			If uncle\Color_			=	TMap_RED
				node\parent_\Color_	=	TMap_BLACK
				uncle\Color_		=	TMap_BLACK
				uncle\parent_\Color_=	TMap_RED
				node				=	uncle\parent_
			Else
				If node=node\parent_\left_
					node			=	node\parent_
					TMapRotateRight		(map,node)
				EndIf
				node\parent_\Color_	=	TMap_BLACK
				node\parent_\parent_\Color_=TMap_RED
				TMapRotateLeft			(map,node\parent_\parent_)
			EndIf
		EndIf
	Wend

	map\root_\Color_=TMap_BLACK

End Function


Function TMapRotateLeft(map.TMap,node.TNode)
	Local child.TNode=node\right_
	node\right_=child\left_
	If child\left_<>nil
		child\left_\parent_=node
	EndIf
	child\parent_=node\parent_
	If node\parent_<>nil
		If node=node\parent_\left_
			node\parent_\left_=child
		Else
			node\parent_\right_=child
		EndIf
	Else
		map\root_=child
	EndIf
	child\left_=node
	node\parent_=child
End Function

Function TMapRotateRight(map.TMap,node.TNode)
	Local child.TNode=node\left_
	node\left_=child\right_
	If child\right_<>nil
		child\right_\parent_=node
	EndIf
	child\parent_=node\parent_
	If node\parent_<>nil
		If node=node\parent_\right_
			node\parent_\right_=child
		Else
			node\parent_\left_=child
		EndIf
	Else
		map\root_=child
	EndIf
	child\right_=node
	node\parent_=child
End Function


Function TMapFindNode.TNode(map.TMap,key$)
	Local node.TNode=map\root_
	While node<>nil
		Local cmp=StringCompare(key,node\key_)
		If cmp>0
			node=node\right_
		Else If cmp<0
			node=node\left_
		Else
			Return node
		EndIf
	Wend
	Return node
End Function

Function TMapFirstNode.TNode(map.TMap)
	Local node.TNode=map\root_
	While node\left_<>nil
		node=node\left_
	Wend
	Return node
End Function

Function TMapLastNode.TNode(map.TMap)
	Local node.TNode=map\root_
	While node\right_<>nil
		node=node\right_
	Wend
	Return node
End Function


Function TMapRemoveNode(map.TMap, node.TNode )
	Local splice.TNode
	Local child.TNode
	
	If node\left_=nil
		splice=node
		child=node\right_
	Else If node\right_=nil
		splice=node
		child=node\left_
	Else
		splice=node\left_
		While splice\right_<>nil
			splice=splice\right_
		Wend
		child=splice\left_
		node\key_=splice\key_
		node\value_=splice\value_
	EndIf
	Local parent.TNode=splice\parent_
	If child<>nil
		child\parent_=parent
	EndIf
	If parent=nil
		map\root_=child
		Return
	EndIf
	If splice=parent\left_
		parent\left_=child
	Else
		parent\right_=child
	EndIf
		
	If splice\Color_=TMap_BLACK	TMapDeleteFixup map,child,parent
End Function


Function TMapDeleteFixup(map.TMap,node.TNode,parent.TNode)
	Local sib.TNode
	While node<>map\root_ And node\Color_=TMap_BLACK
		If node=parent\left_
			sib						=	parent\right_
			If sib\Color_			=	TMap_RED
				sib\Color_			=	TMap_BLACK
				parent\Color_		=	TMap_RED
				TMapRotateLeft			(map,parent)
				sib					=	parent\right_
			EndIf
			If sib\left_\Color_=TMap_BLACK And sib\right_\Color_=TMap_BLACK
				sib\Color_			=	TMap_RED
				node				=	parent
				parent				=	parent\parent_
			Else
				If sib\right_\Color_=	TMap_BLACK
					sib\left_\Color_=	TMap_BLACK
					sib\Color_		=	TMap_RED
					TMapRotateRight		(map,sib)
					sib				=	parent\right_
				EndIf
				sib\Color_			=	parent\Color_
				parent\Color_		=	TMap_BLACK
				sib\right_\Color_	=	TMap_BLACK
				TMapRotateLeft			(map,parent)
				node				=	map\root_
			EndIf
		Else	
			sib						=	parent\left_
			If sib\Color_=TMap_RED
				sib\Color_			=	TMap_BLACK
				parent\Color_		=	TMap_RED
				TMapRotateRight			(map,parent)
				sib					=	parent\left_
			EndIf
			If sib\right_\Color_=TMap_BLACK And sib\left_\Color_=TMap_BLACK
				sib\Color_			=	TMap_RED
				node				=	parent
				parent				=	parent\parent_
			Else
				If sib\left_\Color_=TMap_BLACK
					sib\right_\Color_=	TMap_BLACK
					sib\Color_		=	TMap_RED
					TMapRotateLeft		(map,sib)
					sib				=	parent\left_
				EndIf
				sib\Color_			=	parent\Color_
				parent\Color_		=	TMap_BLACK
				sib\left_\Color_	=	TMap_BLACK
				TMapRotateRight			(map,parent)
				node				=	map\root_
			EndIf
		EndIf
	Wend
	node\Color_	=	TMap_BLACK
End Function


Function TNodeNew.TNode()
	Local node.TNode=New TNode
	node\parent_=nil
	node\left_=nil
	node\right_=nil
	Return node
End Function

Function TNodeClear(node.TNode)
	node\parent_=Null
	If node\left_<>nil	TNodeClear(node\left_)
	If node\right_<>nil	TNodeClear(node\right_)
End Function

Function TNodeCopy.TNode(node.TNode,parent.TNode)
	Local copy.TNode	=	TNodeNew()
	copy\key_	=	node\key_
	copy\value_	=	node\value_
	copy\Color_	=	node\Color_
	copy\parent_=	parent
	If node\left_<>nil	copy\left_=TNodeCopy(node\left_,copy)
	If node\right_<>nil	copy\right_=TNodeCopy(node\right_,copy)
	Return copy
End Function

Function TNodeNextNode.TNode(node.TNode)
	Local node_.TNode=node
	If node_\right_<>nil
		node_=node_\right_
		While node_\left_<>nil
			node_=node_\left_
		Wend
		Return node_
	EndIf
	Local parent.TNode=node_\parent_
	While node_=parent\right_
		node_=parent
		parent=parent\parent_
	Wend
	Return parent
End Function

Function TNodePrevNode.TNode(node.TNode)
	Local node_.TNode=node
	If node_\left_<>nil
		node_=node_\left_
		While node_\right_<>nil
			node_=node_\right_
		Wend
		Return node_
	EndIf
	Local parent.TNode=node_\parent_
	While node_=parent\left_
		node_=parent
		parent=node_\parent_
	Wend
	Return parent
End Function


Function StringCompare%(s1$,s2$)
	Local n1%=Len(s1)
	Local n2%=Len(s2)
	Local nb%=Len(s1)
	If n2<nb	nb=n2
	Local n,c1,c2
	For n = 1 To nb
		c1=Asc(Mid(s1,n))
		c2=Asc(Mid(s2,n))
		If c1<c2 Return -1
		If c2<c1 Return 1
	Next
	If n1<n2 Return -1
	Return (n2<n1)
End Function
















;TMap_Demo2()



Function TMap_Demo1(AnimFile$)
	Graphics 800,600,0,2
	SetBuffer BackBuffer()
	Local MyMap.TMap=CreateMap()
	Local Mesh=LoadAnimMesh(AnimFile$)
	RegisterEntityInTMap(MyMap,Mesh)
End Function

Function RegisterEntityInTMap(map.TMap,entity)
	MapInsert(map,EntityName(entity),entity)
	Local nc%
	For nc=1 To CountChildren(entity)
		RegisterEntityInTMap(map,GetChild(entity,nc))
	Next
End Function


Function TMap_Demo2()

	Graphics 400,750,0,2


	Local map.TMap, map2.TMap
	Local list.TMapList
	Local node.TNode
	Local n, value$, id%

	; creation d'une TMap
		map = CreateMap()
		
		
	; insertion d'entrées dans la TMap
		For n = 1 To 10
			MapInsert(map,Str(n),"blabla["+n+"]")
		Next
		
		
	; affiche les valeurs entrées en utilisant la clé pour retourner la valeur
		Color 100,255,150
		Print " ----- Map values 'map'"
		Color 200,200,200
		
		For n = 1 To 10
			value$=MapValueForKey(map,n)
			If value<>"" Print "{key="+n+"} : value="+value
		Next
		Print
		
		
	; affiche les valeurs en utilisant les TMapList pour lister le contenu de la TMap
		Color 100,255,150
		Print " ----- MapList 'map'"
		Color 200,200,200
		
		; genere la TMapList
		list=MapList(map)
		
		; recupere la premiere node de la liste
		node=FirstNode(list)
		
		; incremente la node jusqu'à la fin du stack
		While node<>Null
			id=id+1
			Print "item["+id+"] = "+NodeValue(node)+" {key="+NodeKey(node)+"}"
			
			node=NextNode(list,node)
		Wend
		Print
		Color 255,128,000
		Print " /!\ hit any key to continue"
		WaitKey:Cls:Locate 0,0
		
	; suppression de clés ( et des valeurs associées à ses clés )
		Color 100,255,150
		Print " ------ remove keys '2' to '7'"
		Color 200,200,200
		
		For n = 2 To 7
			MapRemove(map,n)
		Next
		Print
		
	; affiche le contenu après suppresion
		Color 100,255,150
		Print " ------ values after deletion"
		Color 200,200,200
		
		For n = 1 To 10
			value$=MapValueForKey(map,n)
			If value<>"" Print "{key="+n+"} : value="+value
		Next
		Print
		
		
	; test si la tamp contien la clé specifiée
		Color 100,255,150
		Print " ------ 'map' contain key '8'"
		Color 200,200,200
		
		Print MapContains(map,"8")
		
		; supprime la clé et retest
		MapRemove(map,"8")
		Color 100,255,150
		Print " ------ now the key '8' has been deleted."
		Color 200,200,200
		
		Print " Does 'map' still contain key '8' ?"
		Print MapContains(map,"8")
		
		Print
		Color 255,128,000
		Print " /!\ hit any key to continue"
		WaitKey:Cls:Locate 0,0
		
	; Copie de la TMap 'map'
		Color 100,255,150
		Print " ------ 'map2' is a new copy of 'map'"
		Color 200,200,200
		
		map2=MapCopy(map)
		; vide la TMap 'map'
		MapClear(map)
		
		Color 100,255,150
		Print " ------ 'map' cleared -"
		Color 200,200,200
		
		Print "'map' is empty ? : "+MapIsEmpty(map)
		Print "'map2' is empty ? : "+MapIsEmpty(map2)
		Print
		
	; affiche le contenu de 'map'
		Color 100,255,150
		Print " ------ 'map' values"
		Color 200,200,200
		
		For n = 1 To 10
			value$=MapValueForKey(map,n)
			If value<>"" Print "{key="+n+"} : value="+value
		Next
		Print
		
	; affiche le contenu de 'map2'
		Color 100,255,150
		Print " ------ 'map2' values"
		Color 200,200,200
		
		For n = 1 To 10
			value$=MapValueForKey(map2,n)
			If value<>"" Print "{key="+n+"} : value="+value
		Next
		Print
		
		Color 100,255,150
		Print " ----- MapList 'map2'"
		Color 200,200,200
		
		list.TMapList=MapList(map2)
		node.TNode=FirstNode(list)
		n=0
		While node<>Null
			n=n+1
			Print "item["+n+"] = "+NodeValue(node)+" {key="+NodeKey(node)+"}"
			node=NextNode(list,node)
		Wend
		
		Print
		Color 255,128,000
		Print " /!\ hit any key to exit"
		WaitKey:Cls:Locate 0,0
		
	End


End Function
