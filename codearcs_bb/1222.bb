; ID: 1222
; Author: Nilium
; Date: 2004-12-05 13:56:40
; Title: Portal Occlusion System
; Description: Name says it all.

;#Region DESCRIPTION
	;; An alternative to the simple occlusion provided by the default Vein Scene Manager
	;; You have to set up the portal polygons yourself in whichever map editor you use
	;; Do NOT pass SceneManager nodes to the portal functions, they are NOT the same
;#End Region

;#Region CLASSES
	Type Portal
		Field Polygon				;; The portal mesh (contains a polygon with the mesh that defines the portal)
		Field AABB.Cube			;; Portal axially aligned bounding box, used for determining visibility
		Field NodeA.PortalNode		;; A portal can only connect two nodes
		Field NodeB.PortalNode
		Field R,G,B
		Field Name$
	End Type
	
	Global PortalDebugCount = 0
	
	Type PortalNode
		Field Traversed
		Field Node
		Field AABB.Cube			;; The node's axially aligned bounding box- used to determine if the view is in the node
		Field Portals				;; Stack of portals
		Field Visible
	End Type
	
	Global PortalRange = 2
;#End Region

;#Region PROCEDURES
	Function CreatePortal(Mesh,NodeA,NodeB)
		p.Portal = New Portal
		p\Polygon = Mesh
		EntityColor p\Polygon,127+Rand(127),127+Rand(127),127+Rand(127)
		EntityAlpha p\Polygon,.5
		EntityFX p\Polygon,1+16
		p\AABB = GetMeshAABB(Mesh,1)
		p\NodeA = Object.PortalNode(NodeA)
		p\NodeB = Object.PortalNode(NodeB)
		PushObject p\NodeA\Portals,Handle(p)
		PushObject p\NodeB\Portals,Handle(p)
		HideEntity Mesh
		PortalDebugCount = PortalDebugCount + 1
		p\Name = PortalDebugCount
		p\R = Rand(4,14)*18
		p\G = Rand(4,14)*18
		p\B = Rand(4,14)*18
		Return Handle(p)
	End Function
	
	Function FreePortal(Portal)
		p.Portal = Object.Portal(Portal)
		
		For n.PortalNode = Each PortalNode
			For i = 0 To Objects(n\Portals)-1
				hand = GetObjectI(n\Portals,i)
				If hand = Portal Then
					GetObject(n\Portals,i,1)
					Exit
				EndIf
			Next
		Next
		
		FreeEntity p\Polygon
		FreeCube p\AABB
		Delete p
	End Function
	
	Function CreatePortalNode(Mesh)
		p.PortalNode = New PortalNode
		p\AABB = GetMeshAABB(Mesh)
		p\Portals = CreateStack()
		p\Node = Mesh
		Return Handle(p)
	End Function
	
	Function FreePortalNode(Node,FreeContent=1)
		p.PortalNode = Object.PortalNode(Node)
		If FreeContent Then FreeEntity p\Node
		FreeCube p\AABB
		FreeStack p\Portals
		Delete p
	End Function
	
	Function UpdatePortals(Camera)
		For n.PortalNode = Each PortalNode
			HideEntity n\Node
			n\Visible = 0
			n\Traversed = 0
		Next
		
		x# = EntityX(Camera,1)
		y# = EntityY(Camera,1)
		z# = EntityZ(Camera,1)
		
		For n.PortalNode = Each PortalNode
			If PointInCube(x,y,z,n\AABB) Then
				IteratePortals(Camera,n,Null,PortalRange)
				Done=1
			EndIf
		Next
		
		If Done Then Return 1
		
		For n.PortalNode = Each PortalNode
			ShowEntity n\Node
			n\Visible = 1
		Next
		
		Return 2
	End Function
	
	Function IteratePortals(Camera,p.PortalNode,par.Rectangle,Range)
		If p\Traversed = 1 Then
			Return
		EndIf
		
		If par = Null Then
			par = New Rectangle
			par\x = 0
			par\y = 0
			par\width = GraphicsWidth()
			par\height = GraphicsHeight()
			nsd = 1
		EndIf
		
		p\Traversed = 1
		
		If Range <= 0 Then
			Return
		EndIf
		ShowEntity p\Node
		
		For n = 0 To Objects(p\Portals)-1
			i.Portal = Object.Portal(GetObject(p\Portals,n))
			r.Rectangle = AABBToScreen(Camera,i\AABB\Position\X,i\AABB\Position\Y,i\AABB\Position\Z,i\AABB\Size\Width,i\AABB\Size\Height,i\AABB\Size\Depth)
			
			If RectsOverlap(r\x,r\y,r\width,r\height,par\x,par\y,par\width,par\height) And r\Onscreen > 0 Then
				If r\x < par\x Then
					d = par\x-r\x
					r\x = r\x + d
					r\width = r\width - d
				EndIf
				
				If r\x + r\width > par\x + par\width Then
					d = (r\x + r\width) - (par\x + par\width)
					r\width = r\width + d
				EndIf
				
				If r\y < par\y Then
					d = r\y - par\y
					r\y = r\y + d
					r\height = r\height - d
				EndIf
				
				If r\y + r\Height > par\y + par\height Then
					d = (r\y + r\height) - (par\y + par\height)
					r\height = r\height + d
				EndIf
				
				If p = i\NodeA Then
					IteratePortals(Camera,i\NodeB,r,Range-1)
				ElseIf p = i\NodeB
					IteratePortals(Camera,i\NodeA,r,Range-1)
				EndIf
			EndIf
			
			Delete r
		Next
		
		If nsd Then Delete par
	End Function
	
	Function ShowPortals()
		For p.Portal = Each Portal
			ShowEntity p\Polygon
		Next
	End Function
	
	Function HidePortals()
		For p.Portal = Each Portal
			HideEntity p\Polygon
		Next
	End Function
;#End Region
