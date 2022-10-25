; ID: 2154
; Author: Moraldi
; Date: 2007-11-17 07:25:27
; Title: 3D Short est Path
; Description: An implementation of Dijkstra's algorithm for 3D

Const MAXCONPERVER%=20						; Max connections per vertex

Type Vertex

	Field d#								; shortest path estimation. A value of d = -1 represents infinity
	Field p.Vertex							; predecessor (used in Dijkstra's algorithm)
	Field m%								; visual representation of vertex
	Field graphid%							; Graph id where vertex belongs. If graphid = -1 then it not belongs to any graph
	Field connection.Vertex[MAXCONPERVER]	; array of connected vertices.
	Field conidx%							; index to connection matrix. Range: From 0 to MAXCONPERVER-1

End Type

Type Graph

	Field id%				; graph id
	Field visible%
	Field vcount%			; vertices counter
	Field bestreach.Vertex
	
End Type

Graphics3D 800, 600, 32

Global l% = CreateLight() : RotateEntity l,0,90,0
Global cam% = CreateCamera() : MoveEntity cam, 0,10,-20
Global camerapivot% = CreatePivot() :EntityParent cam, camerapivot
Global fntArial = LoadFont("Arial",15) : SetFont fntArial

Global done% = False

Global g_source.Vertex=Null
Global g_dest.Vertex=Null

Global Graph1.Graph = Graph_Create(0)

Global v1.Vertex = Graph_CreateVertex(Graph1, 0, 0, 0)
Global v2.Vertex = Graph_CreateVertex(Graph1, 5, 0, 5)
Global v3.Vertex = Graph_CreateVertex(Graph1, 0, 20, 5)
Global v4.Vertex = Graph_CreateVertex(Graph1, -5, 0, 5)
Global v5.Vertex = Graph_CreateVertex(Graph1, 5, 5, 10)
Global v6.Vertex = Graph_CreateVertex(Graph1, 2, 4, 10)
Global v7.Vertex = Graph_CreateVertex(Graph1, -5, 6,8)
Global v8.Vertex = Graph_CreateVertex(Graph1, 0, 0, 15)

Vertex_Connection(v1, v2)
Vertex_Connection(v1, v3)
Vertex_Connection(v1, v4)
Vertex_Connection(v2, v4)
Vertex_Connection(v3, v6)
Vertex_Connection(v4, v7)
Vertex_Connection(v4, v6)
Vertex_Connection(v6, v8)
Vertex_Connection(v7, v8)

Graph_SetVisible(Graph1, True)

Repeat

	CaptureWorld
	UpdateWorld
	RenderWorld
	Graph_DrawConnections(Graph1, cam)
	Info()
	Flip
	CameraDrive(cam, camerapivot)
	If KeyHit(1) Then done = True
	If KeyHit(34) Then Graph_SetVisible(Graph1, Not Graph1\visible)
	If KeyHit(32)
		If g_source<>Null And g_dest<>Null
			Graph_SetNodeColor(Graph1, 255, 255, 255)
			Graph_FindShortestPath(Graph1, g_source, g_dest)
			v.Vertex = Graph1\bestreach
			While v<>Null
				EntityColor v\m, 255,255,0
				v = v\p
			Wend
			If g_source<>Null Then EntityColor g_source\m, 0,255,0
			If g_dest<>Null Then EntityColor g_dest\m, 255,0,0
		EndIf
	EndIf

Until done

Graph_Delete(Graph1)
End

;******************
;
; UTILITY FUNCTIONS
;
;******************
Function Info()

	y = 0
	Text 0,y,"Camera navigation"
	y = y + FontHeight()
	Text 0,y,"LMB: Move"
	y = y + FontHeight()
	Text 0,y,"RMB: Rotate"
	y = y + FontHeight()
	Text 0,y,"[ALT]+LMB: Up/Down"
	y = y + FontHeight()
	Text 0,y,"LMB+RMB: Pitch"
	y = y + 2*FontHeight()
	Text 0,y,"Commands"
	y = y + FontHeight()
	Text 0,y,"LMB: Select source node"
	y = y + FontHeight()
	Text 0,y,"RMB: Select destination node"
	y = y + FontHeight()
	Text 0,y,"[D]: Run Dijkstra's algorithm"
	y = y + FontHeight()
	Text 0,y,"[G]: Toggle graph visibility"

End Function

Function CameraDrive(c%, cp%)

	If MouseHit(1)
		m% =CameraPick(c, MouseX(), MouseY())
		For v.Vertex = Each Vertex
			If m = v\m
				If g_source<>Null Then EntityColor g_source\m, 255,255,255
				g_source = v
				EntityColor g_source\m, 0,255,0
			EndIf
		Next
	EndIf
	If MouseHit(2)
		m% =CameraPick(c, MouseX(), MouseY())
		For v.Vertex = Each Vertex
			If m = v\m
				If g_dest<>Null Then EntityColor g_dest\m, 255,255,255
				g_dest = v
				EntityColor g_dest\m, 255,0,0
			EndIf
		Next
	EndIf
	If MouseDown(1) And MouseDown(2)=0 And KeyDown(56) = 0
		MoveEntity c, -MouseXSpeed(),0,0
		MoveEntity c, 0,0,MouseYSpeed()
	EndIf
	If MouseDown(1)=0 And MouseDown(2) Then TurnEntity cp, 0,Sgn(MouseXSpeed())*5,0
	If MouseDown(1) And MouseDown(2) Then TurnEntity c,MouseYSpeed(),0,0
	If KeyDown(56)=1 And MouseDown(1) Then MoveEntity c, 0,MouseYSpeed(),0

	MouseXSpeed()
	MouseYSpeed()
	MouseZSpeed()

End Function

;*****************
;
; VERTEX FUNCTIONS
;
;*****************

Function Vertex_Create.Vertex(x#, y#, z#)

	v.Vertex = New Vertex
	v\d = -1
	v\p = Null
	v\m = CreateSphere() : PositionEntity v\m, x,y,z : EntityPickMode v\m,2
	v\graphid = -1
	v\conidx = 0
	For i%=1 To MAXCONPERVER
		v\connection[i] = Null
	Next
	Return v
	
End Function


Function Vertex_Copy.Vertex(src.Vertex)

	If src = Null Then Return
	dest.Vertex = New Vertex
	dest\d = src\d
	dest\p = src\p
	dest\m = CreateSphere() : PositionEntity dest\m, EntityX(src\m),EntityY(src\m),EntityZ(src\m)
	dest\graphid = src\graphid
	dest\conidx = src\conidx
	For i%=o To dest\conidx
		dest\connection[i] = src\connection[i]
	Next
	Return dest

End Function

Function Vertex_Delete(v.Vertex)

	If v = Null Then Return
	FreeEntity v\m
	Delete v
	v = Null
	
End Function

Function Vertex_Connection(src.Vertex, dest.Vertex)

	If src = Null Or dest = Null Then Return
	If src\conidx = MAXCONPERVER-1 Then Return	; we reched maximum number of connections in source
	If dest\conidx = MAXCONPERVER-1 Then Return		; we reched maximum number of connections in dest
	src\connection[src\conidx] = dest
	src\conidx = src\conidx + 1
	dest\connection[dest\conidx] = src
	dest\conidx = dest\conidx + 1

End Function

Function Vertex_RemoveConnection(v.Vertex, src.Vertex)

	Local i%, j%, n%, doremove%=False
	
	If v = Null Or n > v\conidx-1 Or v\conidx=0 Then Return
	For i=0 To v\conidx-1
		If v\connection[i] = src
			n = i
			doremove = True
			Exit
		EndIf
	Next
	If Not doremove Then Return
	For i=0 To v\conidx
		If i=n
			For j=i+1 To v\conidx-1
				v\connection[j-1] = v\connection[j]
			Next
		EndIf
	Next
	v\conidx = v\conidx - 1

End Function

Function Vertex_DrawConnections(v.Vertex, c%, r%=255, g%=255, b%=255)

	Local x1#, y1#, z1#
	If Not EntityInView(v\m, c) Then Return
	CameraProject c, EntityX(v\m), EntityY(v\m), EntityZ(v\m)
	x1 = ProjectedX()
	y1 = ProjectedY()
	z1 = ProjectedZ()
	Color r, g, b
	For i=0 To v\conidx-1
		If EntityInView(v\m, c)
			CameraProject c, EntityX(v\connection[i]\m), EntityY(v\connection[i]\m), EntityZ(v\connection[i]\m)
			Line x1, y1, ProjectedX(), ProjectedY()
		EndIf
	Next

End Function

;
; returns the distance between v and its neigbor specified by n index
; If v does not exist or n index is invalid then returns infinite (-1)
;
Function Vertex_GetCost#(v.Vertex, n%)

	If v = Null Then Return -1
	If n<0 Or n>v\conidx-1 Then Return -1
	Return EntityDistance(v\m, v\connection[n]\m)

End Function

;*****************
;
; GRAPH FUNCTIONS
;
;*****************
Function Graph_Create.Graph(id%)

	gr.Graph = New Graph
	If gr = Null Then Return
	gr\id = id
	gr\visible = False
	gr\vcount = 0
	gr\bestreach = Null
	Return gr
	
End Function

Function Graph_Copy.Graph(src.Graph, id%)

	Local doconnect%

	If src = Null Then Return Null
	dest.Graph = New Graph
	If dest = Null Then Return Null
	dest\id = id
	dest\bestreach = src\bestreach
	; create new vertices and add into the destination graph
	For v.Vertex = Each Vertex
		If v\graphid = src\id
			newv.Vertex = Vertex_Create(EntityX(v\m), EntityY(v\m), EntityZ(v\m))
			newv\d = v\d
			Graph_AddVertex(dest, newv)
		EndIf
	Next
	; create connections in the destination graph
	For v.Vertex = Each Vertex
		If v\graphid = src\id
			v1.Vertex = Graph_FindVertex(dest, EntityX(v\m), EntityY(v\m), EntityZ(v\m))
			For i%=0 To v\conidx-1
				v2.Vertex = Graph_FindVertex(dest, EntityX(v\connection[i]\m), EntityY(v\connection[i]\m), EntityZ(v\connection[i]\m))
				; do not duplicate the same connection
				doconnect = True
				For j%=0 To v2\conidx-1
					If v2\connection[j] = v1 Then doconnect = False
				Next
				If doconnect Then Vertex_Connection(v1, v2)
			Next
		EndIf
	Next
	Graph_SetVisible(dest, src\visible)
	Return dest
	
End Function

Function Graph_Delete(gr.Graph)

	If gr = Null Then Return
	Graph_DeleteAllVertices(gr)
	Delete gr
	gr = Null

End Function

Function Graph_Move(gr.Graph, dx#, dy#, dz#)

	If gr = Null Then Return
	For v.Vertex = Each Vertex
		If v\graphid = gr\id
			MoveEntity v\m, dx, dy, dz
		EndIf
	Next

End Function

Function Graph_CreateVertex.Vertex(gr.Graph, x#, y#, z#)

	If gr = Null Then Return
	v.Vertex = Vertex_Create(x, y, z)
	v\graphid = gr\id
	HideEntity v\m
	gr\vcount = gr\vcount + 1
	Return v
	
End Function

Function Graph_AddVertex(gr.Graph, v.Vertex)

	If gr = Null Or v = Null Then Return
	v\graphid = gr\id
	If gr\visible
		ShowEntity v\m
	Else
		HideEntity v\m
	EndIf
	gr\vcount = gr\vcount + 1

End Function

Function Graph_RemoveVertexShallow.Vertex(gr.Graph, v.Vertex)

	If gr = Null Or v = Null Or gr\vcount = 0 Then Return Null
	v\graphid = -1
	gr\vcount = gr\vcount - 1
	Return v

End Function

Function Graph_RemoveVertexDeep.Vertex(gr.Graph, v.Vertex)

	If gr = Null Or v = Null Or gr\vcount = 0 Then Return Null
	v\graphid = -1
	gr\vcount = gr\vcount - 1
	; remove connections of vertex v from graph
	For vv.Vertex = Each Vertex
		If vv\graphid = gr\id
			Vertex_RemoveConnection(vv, v)
		EndIf
	Next
	Return v

End Function

Function Graph_DeleteAllVertices(gr.Graph)

	If gr = Null Then Return
	For v.Vertex = Each Vertex
		If v\graphid = gr\id
			Vertex_Delete(v)			
		EndIf
	Next
	gr\vcount = 0

End Function

Function Graph_FindVertex.Vertex(gr.Graph, x#, y#, z#)

	For v.Vertex = Each Vertex
		If v\graphid = gr\id
			If (x=EntityX(v\m) And y=EntityY(v\m) And z=EntityZ(v\m)) Return v
		EndIf
	Next
	Return Null

End Function

Function Graph_SetNodeColor(gr.Graph, r%, g%, b%)

	If gr = Null Then Return
	For v.Vertex = Each Vertex
		If v\graphid = gr\id
			EntityColor v\m, r, g, b
		EndIf
	Next

End Function

Function Graph_SetVisible(gr.Graph, visible%=True)

	If gr\visible = visible Then Return
	gr\visible = visible
	For v.Vertex = Each Vertex
		If v\graphid = gr\id
			If gr\visible
				ShowEntity v\m
			Else
				HideEntity v\m
			EndIf
		EndIf
	Next

End Function

Function Graph_DrawConnections(gr.Graph, c%, r%=255, g%=255, b%=255)

	If gr = Null Then Return
	If gr\visible = False Then Return
	For v.Vertex = Each Vertex
		If v\graphid = gr\id Then Vertex_DrawConnections(v, c)
	Next

End Function

Function Graph_DrawDistances(gr.Graph, c%, r%=255, g%=255, b%=255)

	If gr = Null Then Return
	If gr\visible = False Then Return
	Color r, g, b
	For v.Vertex = Each Vertex
		If v\graphid = gr\id
			If EntityInView(v\m, c)
				CameraProject(c, EntityX(v\m), EntityY(v\m), EntityZ(v\m))
				Text ProjectedX(), ProjectedY(), v\d
			EndIf
		EndIf
	Next

End Function

Function Graph_FindMinD.Vertex(gr.Graph)

	Local mind# = -1
	Local rv.Vertex = Null

	For v.Vertex = Each Vertex
		If v\graphid = gr\id
			If v\d <> -1
				If mind = -1
					rv = v
					mind = v\d
				Else
					If v\d < mind
						rv = v
						mind = v\d
					EndIf
				EndIf
			EndIf
		EndIf
	Next
	Return rv

End Function

Function Graph_FindShortestPath(gr.Graph, src.Vertex, dest.Vertex)

	Local u.Vertex
	Local prevu.Vertex

	If (gr=Null) Or (src=Null) Or (dest=Null) Or (src=dest) Then Return
	gr\bestreach = Null
	u = src
	For v.Vertex = Each Vertex
		v\p = Null
		If v = src
			v\d = 0
		Else
			v\d = -1
		EndIf
	Next
	q.Graph = Graph_Copy(gr, 1) : Graph_SetVisible(q, False)
	s.Graph = Graph_Create(2) : Graph_SetVisible(s, False)
	qdest.Vertex = Graph_FindVertex(q, EntityX(dest\m), EntityY(dest\m), EntityZ(dest\m))
	While q\vcount
		prevu = u
		u = Graph_FindMinD(q)
		Graph_RemoveVertexShallow(q, u)
		Graph_AddVertex(s, u)
		If u = Null		; destination is unreachable
			u = prevu	; rollback
			Exit
		EndIf
		If u = qdest Then Exit
		For i% = 0 To u\conidx-1
			If (u\connection[i]\d=-1 Or u\connection[i]\d>(u\d + Vertex_GetCost(u, i)))
				u\connection[i]\d = u\d + Vertex_GetCost(u, i)
				u\connection[i]\p = u
			EndIf
		Next
	Wend
	v = u
	; setup predecessors in gr Graph
	While v\p <> Null
		vv.Vertex = Graph_FindVertex(gr, EntityX(v\m), EntityY(v\m), EntityZ(v\m))
		If v = u Then gr\bestreach = vv
		vp.Vertex = Graph_FindVertex(gr, EntityX(v\p\m), EntityY(v\p\m), EntityZ(v\p\m))
		vv\p = vp
		v = v\p
	Wend
	; clean up
	Graph_Delete(q)
	Graph_Delete(s)

End Function
