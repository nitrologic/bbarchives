; ID: 581
; Author: Braneloc
; Date: 2003-02-10 04:37:37
; Title: Max Mirror
; Description: Fixes strange "mirrored" objects

;
;
;		Max Mirror
;
;		(c) Braneloc, Feb 2003
;			Fixes "mirror" errors with objects
;
;		Contact Details: 
;			Braneloc@mirex.demon.co.uk
;
;
Function max_mirror(mesh)
	;# for some reason, either 3D Studio Max or Blitz messes with
	;# the coordinates, textures, flips or mirrors them, whatever.  
	;# This fixes it.
	ScaleMesh mesh,1,1,-1
	FlipMesh mesh
	For s=1 To CountSurfaces(mesh)
		surf=GetSurface(mesh,s)
		For n=0 To CountVertices(surf)-1
			u#=VertexU(surf,n)
			v#=VertexV(surf,n)
			VertexTexCoords surf,n,u,1-v
		Next
	Next
End Function
