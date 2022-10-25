; ID: 14
; Author: BlitzSupport
; Date: 2001-09-19 18:47:10
; Title: CenterMesh ()
; Description: Fixes problems with weird-offset meshes

Function CenterMesh (entity)
	FitMesh entity, -(MeshWidth (entity) / 2), -(MeshHeight (entity) / 2), -(MeshDepth (entity) / 2), MeshWidth (entity), MeshHeight (entity), MeshDepth (entity)
End Function

; Example usage (don't try to run this!)...

planeModel = LoadMesh ("747.x")
CenterMesh (planeModel)
PositionEntity planeModel, 50, 100, 50

