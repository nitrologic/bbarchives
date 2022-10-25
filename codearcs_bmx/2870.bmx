; ID: 2870
; Author: Warpy
; Date: 2011-07-07 07:46:13
; Title: Minimum spanning tree
; Description: Kruskal's algorithm to find a minimum spanning tree of a graph

'Kruskal's algorithm
'
'this program will generate a random graph, then step through kruskal's algorithm, describing each step.
'
'
'-------------------
'Some background:
' - a graph is a set of vertices and a set of edges. 
' - a vertex is just a point in space
' - an edge connects two vertices.
' - a "weighted" edge also has a number associated with it. This could be the distance between its two vertices, for example. 
' - a graph is "connected" if it is possible to trace a path from each vertex to any other vertex.
' - a tree is a graph with no loops in it - that is, it isn't possible to trace a path along the edges starting and ending at the same vertex, and which never uses any edge twice.
' - a spanning tree of a graph is a connected tree which contains every vertex from the original graph, only using the edges from the original graph.
'
'-------------------
'Summary of algorithm:
' - start with a graph consisting of a set of vertices connected by edges. The graph must be connected - that is, there must be a path from each vertex to any other vertex
' - for each vertex, create a tree containing only that vertex. Mark on the vertex which tree it belongs to. The set of all the trees is called the "forest".
' - the idea is that by joining these trees together when there is an edge connecting them, we will end up with a spanning tree.
' - if we add the edges in order of length, that will ensure that the resulting tree is a minimum spanning tree.

'
' - sort the edges of the graph by length (or weight).
' - starting with the shortest edge:
' - - if the two vertices that the edge connects belong to the same tree, forget about this edge and move on to the next one.
' - - if the two vertices that the edge connects belong to different trees, merge the two trees together, and add this edge to it.
' - when a tree contains every vertex in the graph, you have created a minimum spanning tree and can end the algorithm.



'a vertex is a point on the graph
'for the purposes of Kruskal's algorithm, each vertex needs to keep track of which tree in the forest it belongs to
'the id property is just for convenience, so I can describe each step the algorithm makes
Type vertex
	Field x#,y#
	Field id
	Field t:graph
	
	Method draw()
		DrawOval x-5,y-5,10,10
		DrawText "Vertex: "+id,x,y+5
		If t
			DrawText "Tree: "+t.id,x,y+18
		EndIf
	End Method
End Type

'an edge connects two vertices together
Type edge
	Field a:vertex
	Field b:vertex
	Field length#
	
	Function Create:edge(a:vertex,b:vertex)
		e:edge = New edge
		e.a=a
		e.b=b
		e.length = Sqr( (a.x-b.x)^2 + (a.y-b.y)^2 )
		Return e
	End Function
	
	'this method allows edges to be sorted by length
	Method Compare(o2:Object)
		e2:edge = edge(o2)
		
		Return Sgn(length-e2.length)
	End Method
	
	Method draw()
		DrawLine a.x,a.y,b.x,b.y
	End Method
End Type

'a graph is a set of vertices and a set of edges
'again, the id property and red/green/blue are just for convenience
Type graph
	Field vertices:TList
	Field edges:TList
	Field id
	Field red,green,blue
	
	Method New()
		vertices = New TList
		edges = New TList
		
		
		'generate a random colour
		acc = 255
		red = Rand(0,acc)
		acc :- red
		green = Rand(0,acc)
		acc :- green
		blue = acc
		
	End Method
	
	Method draw()
		SetColor red,green,blue
		For e:edge=EachIn edges
			e.draw
		Next
		
		SetColor 255,255,255
		For v:vertex = EachIn vertices
			v.draw
		Next
	End Method
End Type


'this function creates a random graph
'it isn't directly relevant to Kruskal's algorithm, so ignore it if you like
Function randomGraph:graph()

	'create a graph
	g:graph = New graph
	
	'decide how many vertices it should contain
	n = Rand(5,15)
	
	'this array will keep track of whether there is already an edge connecting two vertices
	'kruskal's algorithm will work if there are repeated edges, but to make the graphical description easier to understand, I don't want any.
	Local hasedge[n*n]
	
	'create the vertices
	For c=0 To n-1
		v:vertex = New vertex
		
		'arrange the vertices around an ellipse so they're easy to differentiate
		v.x = 400+Cos(360*(c+Rnd(0.5))/n)*300
		v.y = 300+Sin(360*c/n)*200
		v.id = c
		
		'create an edge between this vertex and a previously created one, to make sure that the graph is connected
		If c
			v2:vertex = vertex(g.vertices.valueatindex(Rand(0,c-1)))
			g.edges.addlast edge.Create(v,v2)
			hasedge[n*Min(v.id,v2.id)+Max(v.id,v2.id)] = 1
		EndIf
		
		g.vertices.addlast v
		
	Next
	
	'at this point, the graph is a tree because of the way I added edges before.
	'So, add a few more edges
	For c=1 To n/2
	
		'pick a random vertex
		i=Rand(0,n-1)
		
		'pick another one, but make sure it's not the same as the first one
		j=Rand(0,n-2)
		If j>=i Then j:+1
		
		'if there's already an edge between i and j, pick a new pair of vertices
		'I'm assuming it's not possible for this algorithm to end up creating edges between every pair of vertices, so this while loop will terminate.
		While hasedge[n*Min(i,j)+Max(i,j)]
			i=Rand(0,n-1)
			j=Rand(0,n-2)
			If j>=i Then j:+1
		Wend
		
		'get the vertex objects corresponding to i and j
		a:vertex = vertex(g.vertices.valueatindex(i))
		b:vertex = vertex(g.vertices.valueatindex(j))
		
		'create an edge between the picked vertices and add it to the graph
		e:edge = edge.Create(a,b)
		g.edges.addlast e
		
		'record that there is now an edge between these vertices
		hasedge[n*Min(i,j)+Max(i,j)] = 1
	Next
	
	'just for convenience when describing the algorithm graphically
	g.red = 50
	g.green = 50
	g.blue = 50
	
	Return g
End Function

'this function will compute the minimum spanning tree for a graph
Function spanningTree:graph(g:graph)

	'the forest is the set of all trees. It should be reduced down to one tree by the time the algorithm finishes.
	forest:TList = New TList
	
	describe g,forest,Null,"Start with this graph. Create a tree for each vertex."
	numgraphs = 0
	For v:vertex=EachIn g.vertices
		describe g,forest,Null,"Create a tree for each vertex."
		
		'create a tree
		t:graph = New graph
		t.id=numgraphs
		numgraphs:+1
		
		'add the vertex to it
		t.vertices.addlast v
		'mark on the vertex which tree it belongs to
		v.t = t
		'add the tree to the forest
		forest.addlast t
	Next
	
	describe g,forest,Null,"Now we can begin. Work through the edges, starting with the shortest one."
	
	'sort the edges by length
	g.edges.sort()
	
	'for each edge in the graph:
	For e:edge=EachIn g.edges
		txt$="Vertices "+e.a.id+" and "+e.b.id
		If e.a.t = e.b.t
			txt:+" are in the same tree, so discard this edge."
		Else
			txt:+" are in different trees, so merge the trees and add this edge."
		EndIf
		describe g,forest,e,txt
		
		'if the two vertices this edge connects belong to different trees, then we can merge them.
		If e.a.t <> e.b.t
		
			t1:graph = e.a.t
			t2:graph = e.b.t
			
			'I want to keep the tree with the most edges in it, so the least colour change happens on scree
			'this bit isn't part of kruskal's algorithm, but it makes the graphical description easier to follow
			If t2.edges.count() > t1.edges.count()
				t:graph = t1
				t1 = t2
				t2 = t
			EndIf

			'add the edge to t1
			t1.edges.addlast e

			'add all of t2's vertices to t1
			'remember to mark on each vertex that it now belongs to t1
			For v:vertex=EachIn t2.vertices
				v.t = t1
				t1.vertices.addlast v
			Next
			
			'add all of t2's edges to t1
			For e:edge = EachIn t2.edges
				t1.edges.addlast e
			Next
			
			'remove t2 from the forest, because all of its contents are now in t1
			forest.remove t2
			
			'if there is only one tree left in the forest, then it must be a spanning tree, so the algorithm can finish
			If forest.count() = 1
				Print forest.count()
				describe Null,forest,Null,"All vertices are in the same tree now, so this is a minimum spanning tree."
			
				Return t1
			EndIf
		EndIf
	Next
End Function

'this function describes a step of the algorithm graphically
Function describe(g:graph=Null,forest:TList=Null,e:edge=Null,txt$="")
	While Not KeyHit(KEY_SPACE)
		If KeyHit(KEY_ESCAPE) Or AppTerminate()
			End
		EndIf
		
		If g
			SetLineWidth 1
			g.draw
		EndIf
		
		If forest
			SetLineWidth 2
			For t:graph=EachIn forest
				t.draw
			Next
		EndIf
		
		If e
			SetColor 255,255,255
			SetLineWidth 3
			e.draw
		EndIf
		
		SetColor 255,255,255
		DrawText txt,0,0
		DrawText "Press SPACE to continue.",0,15
		
		Flip
		Cls
	Wend
End Function

Graphics 800,600,0
SeedRnd MilliSecs()

While 1
	g:graph = randomGraph()
	tree:graph = spanningTree(g)
Wend
