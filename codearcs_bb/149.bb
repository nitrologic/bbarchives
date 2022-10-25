; ID: 149
; Author: FlameDuck
; Date: 2001-11-29 03:14:55
; Title: MyPaintMesh
; Description: This small function paints a mesh without collapsing surfaces.

Function MyPaintMesh(mesh,brush)

For i = 1 To CountSurfaces(mesh)
	surfh=GetSurface(mesh,i)
	PaintSurface surfh,brush
Next

End Function
