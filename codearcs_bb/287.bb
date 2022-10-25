; ID: 287
; Author: Rob
; Date: 2002-04-06 17:57:36
; Title: Independant texture scale per entity
; Description: ScaleTexture scales all entitie's textures using the same texuture, this avoids that issue.

Function ScaleUV#(entity,scalex#=1,scaley#=1)
	If entity=0 Then Return 0
	For i=1 To CountSurfaces(entity)
		s=GetSurface(entity,i)
		For j=0 To CountVertices(s)-1
			u#=VertexU(s,j,0)
			v#=VertexV(s,j,0)
			VertexTexCoords s,j,u*scalex,v*scaley,0,1
		Next
	Next
End Function
