; ID: 1691
; Author: Devils Child
; Date: 2006-05-03 08:07:11
; Title: Stencil Shadow Volume
; Description: A code which creates a shadow volume on the fly(with little demo)

hi there!

i know that there is a giant discusion about stencil shadows and shadow volumes... so i decidet to show you my shadow volume creation code!

it is a mod of the version of this thread: www.blitzbasic.com/Community/posts.php?topic=58767

and i have modifyed it up to this(it is VERY MUCH OPTIMIZED!!!):

[code]
Graphics3D 1024, 768, 32, 2
SetBuffer BackBuffer()

;Globs
Dim Edge#(65000, 7)

;Camera
Cam = CreateCamera()

;Caster
Cube = CreateSphere()
PositionEntity Cube, 0, 0, 7

;Light
Light = CreateLight()
PositionEntity Light, 3, 3, 7
PointEntity Light, Cube

While Not KeyHit(1)
	TurnEntity Cube, 1, .5, 1.5
	vol = CreateVolume(Cube, Light)
	RenderWorld
	FreeEntity vol
	Flip
Wend
End

Function CreateVolume(model, light, volume_lenght = 1000000)
VolumeMesh = CreateMesh()
VolumeSurface = CreateSurface(VolumeMesh)
EntityAlpha VolumeMesh, .5
EntityColor VolumeMesh, 255, 0, 0
EntityFX VolumeMesh, 1
light_x# = EntityX(light)
light_y# = EntityY(light)
light_z# = EntityZ(light)
For n = 1 To CountSurfaces(model)
	surf = GetSurface(model, n)
	dwNumFaces = CountTriangles(surf) - 1
	For v = 0 To dwNumFaces
		vert0 = TriangleVertex(surf, v, 0)
		vert1 = TriangleVertex(surf, v, 1)
		vert2 = TriangleVertex(surf, v, 2)
		TFormPoint VertexX(surf, vert0), VertexY(surf, vert0), VertexZ(surf, vert0), model, 0
		v1_x# = TFormedX()
		v1_y# = TFormedY()
		v1_z# = TFormedZ()
		TFormPoint VertexX(surf, vert1), VertexY(surf, vert1), VertexZ(surf, vert1), model, 0
		v2_x# = TFormedX()
		v2_y# = TFormedY()
		v2_z# = TFormedZ()
		TFormPoint VertexX(surf, vert2), VertexY(surf, vert2), VertexZ(surf, vert2), model, 0
		v3_x# = TFormedX()
		v3_y# = TFormedY()
		v3_z# = TFormedZ()
		aa_x# = v3_x# - v2_x#
		aa_y# = v3_y# - v2_y#
		aa_z# = v3_z# - v2_z#
		bb_x# = v2_x# - v1_x#
		bb_y# = v2_y# - v1_y#
		bb_z# = v2_z# - v1_z#
		norm_x# = aa_y# * bb_z# - aa_z# * bb_y#
		norm_y# = aa_z# * bb_x# - aa_x# * bb_z#
		norm_z# = aa_x# * bb_y# - aa_y# * bb_x#
		normlight_x# = (v1_x# + v2_x# + v3_x#) / 3 - light_x#
		normlight_y# = (v1_y# + v2_y# + v3_y#) / 3 - light_y#
		normlight_z# = (v1_z# + v2_z# + v3_z#) / 3 - light_z#
		If (norm_x# * normlight_x# + norm_y# * normlight_y# + norm_z# * normlight_z#) * (1.0 / Float(Sqr(norm_x# * norm_x# + norm_y# * norm_y# + norm_z# * norm_z#))) * (1.0 / Float(Sqr(normlight_x# * normlight_x# + normlight_y# * normlight_y# + normlight_z# * normlight_z#))) => 0 Then
			Edge(CNTFront, 0) = surf
			Edge(CNTFront, 1) = tri
			Edge(CNTFront, 2) = v1_x#
			Edge(CNTFront, 3) = v1_y#
			Edge(CNTFront, 4) = v1_z#
			Edge(CNTFront, 5) = v2_x#
			Edge(CNTFront, 6) = v2_y#
			Edge(CNTFront, 7) = v2_z#
			CNTFront = CNTFront + 1
			Edge(CNTFront, 0) = surf
			Edge(CNTFront, 1) = tri
			Edge(CNTFront, 2) = v2_x#
			Edge(CNTFront, 3) = v2_y#
			Edge(CNTFront, 4) = v2_z#
			Edge(CNTFront, 5) = v3_x#
			Edge(CNTFront, 6) = v3_y#
			Edge(CNTFront, 7) = v3_z#
			CNTFront = CNTFront + 1
			Edge(CNTFront, 0) = surf
			Edge(CNTFront, 1) = tri
			Edge(CNTFront, 2) = v3_x#
			Edge(CNTFront, 3) = v3_y#
			Edge(CNTFront, 4) = v3_z#
			Edge(CNTFront, 5) = v1_x#
			Edge(CNTFront, 6) = v1_y#
			Edge(CNTFront, 7) = v1_z#
			CNTFront = CNTFront + 1
		EndIf
	Next
Next
For a = 0 To CNTFront
	If Edge(a, 0) > 0 Then
		Diverso = True
		p0_x# = Edge(a, 2)
		p0_y# = Edge(a, 3)
		p0_z# = Edge(a, 4)
		p1_x# = Edge(a, 5)
		p1_y# = Edge(a, 6)
		p1_z# = Edge(a, 7)
		For b = a + 1 To CNTFront
			p0_2_x# = Edge(b, 2)
			p0_2_y# = Edge(b, 3)
			p0_2_z# = Edge(b, 4)
			p1_2_x# = Edge(b, 5)
			p1_2_y# = Edge(b, 6)
			p1_2_z# = Edge(b, 7)
			If Edge(b, 0) > 0 Then
				If (p0_x# = p0_2_x# And p0_y# = p0_2_y# And p0_z# = p0_2_z# And p1_x# = p1_2_x# And p1_y# = p1_2_y# And p1_z# = p1_2_z#) = False Then
					If p0_x# = p1_2_x# And p0_y# = p1_2_y# And p0_z# = p1_2_z# And p1_x# = p0_2_x# And p1_y# = p0_2_y# And p1_z# = p0_2_z# Then
						Edge(a, 0) = surf = 0
						Edge(b, 0) = surf = 0
						Diverso = False
						Exit
					EndIf
				EndIf
			EndIf
		Next
		If Diverso Then
			pe0_x# = p0_x# - light_x#
			pe0_y# = p0_y# - light_y#
			pe0_z# = p0_z# - light_z#
			do# = 1.0 / Float(Sqr(pe0_x# * pe0_x# + pe0_y# * pe0_y# + pe0_z# * pe0_z#))
			pe0_x# = pe0_x# * do# * volume_lenght + p0_x#
			pe0_y# = pe0_y# * do# * volume_lenght + p0_y#
			pe0_z# = pe0_z# * do# * volume_lenght + p0_z#
			pe1_x# = (p1_x# - light_x#) * volume_lenght + p1_x#
			pe1_y# = (p1_y# - light_y#) * volume_lenght + p1_y#
			pe1_z# = (p1_z# - light_z#) * volume_lenght + p1_z#
			va = AddVertex(VolumeSurface, p0_x#, p0_y#, p0_z#)
			vb = AddVertex(VolumeSurface, pe1_x#, pe1_y#, pe1_z#)
			AddTriangle(VolumeSurface, va, AddVertex(VolumeSurface, pe0_x#, pe0_y#, pe0_z#), vb)
			AddTriangle(VolumeSurface, va, vb, AddVertex(VolumeSurface, p1_x#, p1_y#, p1_z#))
			Edge(a, 0) = 0
		EndIf
	EndIf
Next
Return VolumeMesh
End Function
[/code]
 


the stencil shadow system demo is here:
http://patrick-sch.de/bleibdafuerimmer/StencilShadowSystem.zip
