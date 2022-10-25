; ID: 639
; Author: Graythe
; Date: 2003-04-03 10:23:39
; Title: Meshes, surfaces, vertices, triangles
; Description: Create hollowed bisected sphere from bb sphere

[code]
;Hollowed half sphere function by Graythe@2003

;What the hell - some constants
Const One%=1,Two%=2
;Pre-declare vertex array
Dim VertArray(False)

;Initialise Blitz display
Graphics3D 640,480,32,2:SetBuffer BackBuffer()
;Create a camera and some light
Camera=CreateCamera(): Light=CreateLight(Camera)

;Set up a pivot (something to attach entities to - you don't really neeeeed this but pivots are handy and require few resources)
SpherePivot=CreatePivot()





;I want to create a half of a sphere - so to start create a whole one
Sphere=CreateSphere(64,SpherePivot)


;Copy 50% of the triangles in our sphere as though it were bisected 
Copied_Sphere=AdjustTriangles(Sphere,0.5,False)
;Now create the inside of the sphere by doing the same thing again
Internal_Sphere=AdjustTriangles(Sphere,0.5,True)
;Flip the second creation (inside out) to finish the `inside effect`
FlipMesh Internal_Sphere

;Plop that in front of the camera
PositionEntity SpherePivot,False,False,2.5


;Repeat until escape key is pressed
While Not KeyDown(One)

	;Make our hollowed - half sphere rotate
	TurnEntity SpherePivot,One,False,False

	;Tell Blitz to paint the awesome scene
	RenderWorld

	;Update the display
	Flip

Wend


;Program ends here
End






Function AdjustTriangles%(EntityNo%,Triangles#,FreeSource%=True,FX%=False)

;The arguments passed are
	;EntityNo% A handle to the source entity that we will alter
	;Triangle# A ratio (0.0 to 1.0) of the existing triangles that this function will process
	;FreeSource A binary switch to indicate wether or not to delete the source mesh when finished
	;FX an bitmask of affects to the appearence of objects

;Conjur virgin mesh
Copied=CreateMesh(GetParent(EntityNo))

;Iterate surfaces of supplied mesh
For SurfLoop=One To CountSurfaces(EntityNo)
	
	;Add a surface to our new mesh
	NewSurface=CreateSurface(Copied)
	;Establish handle to source surface
	OldSurface=GetSurface(EntityNo,SurfLoop)
	;Determine number of vertices in source surface
	NoVertices=CountVertices(OldSurface)-One

	;Calculate new triangle total
	TotTriangles=Int(CountTriangles(OldSurface)*Triangles)-One
	
	;Determine relevent vertices by creating an array of all vertices and tipping to true those that are connected to an included triangle
	Dim VertArray%(NoVertices)
	;Iterate the triangles that we are interested in
	For TriLoop=False To TotTriangles
		;Iterate the three vertices of this triangle
		For CornerLoop=False To Two
			;Tip the array ident calculated by the vertex number of this corner the triangle
			VertArray(TriangleVertex(OldSurface,TriLoop,CornerLoop))=True
		Next
	Next
	
	;Iterate vertices
	For VertLoop = False To NoVertices
		;If the array ident for this vertex number was tipped
		If VertArray(VertLoop) Then
			;Copy this vertex from the old surface to the new surface
			NewVertex=AddVertex(NewSurface,VertexX(OldSurface,VertLoop),VertexY(OldSurface,VertLoop),VertexZ(OldSurface,VertLoop),VertexU(OldSurface,VertLoop),VertexV(OldSurface,VertLoop),VertexW(OldSurface,VertLoop))
		End If
	Next		
		
	;Add Triangles
	For TriLoop=False To TotTriangles
		AddTriangle NewSurface, TriangleVertex(OldSurface,TriLoop,False),TriangleVertex(OldSurface,TriLoop,One),TriangleVertex(OldSurface,TriLoop,Two)
	Next	

Next
;Release (most) space allocated the array
Dim VertArray(0)
;Update vertex normals
UpdateNormals Copied
;Apply EntityFX
If FX Then EntityFX Copied,FX
;Free source entity?
If FreeSource Then FreeEntity EntityNo

;Return new mesh handle
Return Copied

End Function
[/code]
