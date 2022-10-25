; ID: 1223
; Author: Nilium
; Date: 2004-12-05 14:05:48
; Title: Connected-AABB Occlusion System
; Description: An occlusion system handy for FPS or other similar games

;#Region DESCRIPTION
	;; Scene node culling system
;#End Region

;#Region CLASSES
	Type SceneNode
		Field MinCube.Vector
		Field MaxCube.Vector
		Field Cube
		Field Adjacent
		Field Root
		Field Visible
	End Type
	
	Global NodeRange = 2
;#End Region

;#Region PROCEDURES
	Function CreateSceneNode()
		s.SceneNode = New SceneNode
		s\MinCube = Vector(9999,9999,9999)
		s\MaxCube = Vector(-9999,-9999,-9999)
		s\Root = CreatePivot()
		NameEntity(CreateCube(s\Root),"_NODEOCCLUDER")
		s\Adjacent = CreateStack()
		NameEntity s\Root,Handle(s)
		
		Return s\Root
	End Function
	
	Function AddEntityToNode(Node,Entity)
		s.SceneNode = Object.SceneNode(EntityName(Node))
		
		If Upper(EntityClass(Entity)) = "MESH" Then
			For n = 1 To CountSurfaces(Entity)
				surf = GetSurface(Entity,n)
				For i = 0 To CountVertices(surf)-1
					x# = VertexX(surf,i)
					y# = VertexY(surf,i)
					z# = VertexZ(surf,i)
					
					TFormPoint x,y,z,Entity,0
					
					If x < s\MinCube\X Then s\MinCube\X = x
					If x > s\MaxCube\X Then s\MaxCube\X = x
					
					If y < s\MinCube\y Then s\MinCube\y = y
					If y > s\MaxCube\y Then s\MaxCube\y = y
					
					If z < s\MinCube\z Then s\MinCube\z = z
					If z > s\MaxCube\z Then s\MaxCube\z = z
				Next
			Next
			
			CreateMeshBox(Entity)
		Else
			x# = EntityX(Entity,1)
			y# = EntityY(Entity,1)
			z# = EntityZ(Entity,1)
			
			If x < s\MinCube\X Then s\MinCube\X = x
			If x > s\MaxCube\X Then s\MaxCube\X = x
			
			If y < s\MinCube\y Then s\MinCube\y = y
			If y > s\MaxCube\y Then s\MaxCube\y = y
			
			If z < s\MinCube\z Then s\MinCube\z = z
			If z > s\MaxCube\z Then s\MaxCube\z = z
		EndIf
		
		EntityBox FindChild(s\Root,"_NODEOCCLUDER"),s\MaxCube\X,s\MaxCube\Y,s\MaxCube\Z,s\MinCube\X-s\MaxCube\X,s\MinCube\Y-s\MaxCube\Y,s\MinCube\Z-s\MaxCube\Z
		
		EntityParent Entity,s\Root
	End Function
	
	Function AddAdjacentNode(NodeA,NodeB)
		a.SceneNode = Object.SceneNode(EntityName(NodeA))
		b.SceneNode = Object.SceneNode(EntityName(NodeB))
		
		PushObject b\Adjacent,NodeA
		PushObject a\Adjacent,NodeB
	End Function
	
	Function SetNodeBoundaries(Node,MinX#,MinY#,MinZ#,MaxX#,MaxY#,MaxZ#)
		s.SceneNode = Object.SceneNode(EntityName(Node))
		If s = Null Then Return 0
		Delete s\MinCube
		Delete s\MaxCube
		s\MinCube = Vector(MinX,MinY,MinZ)
		s\MaxCube = Vector(MaxX,MaxY,MaxZ)
		Return 1
	End Function
	
	Function EntityInsideNode(Entity,Node)
		s.SceneNode = Object.SceneNode(EntityName(Node))
		If s = Null Then Return 0
		
		TFormPoint 0,0,0,Entity,s\Root
		
		x# = TFormedX()
		y# = TFormedY()
		z# = TFormedZ()
		
		Return ( x > s\MinCube\X And x < s\MaxCube\X And y > s\MinCube\Y And y < s\MaxCube\Y And z > s\MinCube\Z And z < s\MaxCube\Z )
	End Function
	
	Function UpdateSceneNodes(Camera)
		For s.SceneNode = Each SceneNode
			HideEntity s\Root
			s\Visible = 0
		Next
		
		For s.SceneNode = Each SceneNode
			If EntityInsideNode(Camera,s\Root)
				Cube = FindChild(s\Root,"_NODEOCCLUDER")
				For c = 1 To CountChildren(s\Root)
					child = GetChild(s\Root,c)
					If Child <> Cube Then
						If EntityInView(Child,Camera)=0 Then HideEntity child
					EndIf
				Next
				SetNodesVisible(Camera,s\Root,NodeRange)
				Return
			EndIf
		Next
		
		For s.SceneNode = Each SceneNode
			ShowEntity s\Root
			HideEntity FindChild(s\Root,"_NODEOCCLUDER")
		Next
	End Function
	
	Function NodeVisible(Node)
		s.SceneNode = Object.SceneNode(EntityName(Node))
		If s = Null Then Return 0
		Return s\Visible
	End Function
	
	Function GetNodeRoot(Node)
		s.SceneNode = Object.SceneNode(EntityName(Node))
		If s = Null Then Return 0
		Return s\Root
	End Function
	
	Function SetNodesVisible(Camera,Node,Range=2)
		s.SceneNode = Object.SceneNode(EntityName(Node))
		If s = Null Then Return 0
		
		Cube = FindChild(Node,"_NODEOCCLUDER")
		
		If Cube = 0 Then
			rt$ = "ERROR: _NODEOCCLUDER was not found in child list"+Chr(10)+Chr(10)+"Children:"
			For c = 1 To CountChildren(Node)
				name$ = EntityName(GetChild(Node,c))
				If name$ = "" Then name$ = "NONAME"
				rt$ = rt$ + Chr(10) + name$ + "  :  " + GetChild(Node,c)
			Next
			If CountChildren(Node) = 0 Then rt$ = rt$ + Chr(10) + "None"
			RuntimeError rt$
		EndIf
		
		EntityParent Cube,0
		ShowEntity Cube
		
		If EntityInView(Cube,Camera) = 1 Or Range=NodeRange Then
			ShowEntity s\Root
			s\Visible = 1
			
			HideEntity Cube
			EntityParent Cube,s\Root
			
			For i = 0 To (Objects(s\Adjacent)*(Range-1 > 0))-1
				SetNodesVisible(Camera,GetObjectI(s\Adjacent,i),Range-1)
			Next
			
			Return
		EndIf
		
		HideEntity Cube
		EntityParent Cube,s\Root
		Return
	End Function
;#End Region
