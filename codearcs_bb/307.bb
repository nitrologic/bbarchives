; ID: 307
; Author: Litobyte
; Date: 2002-04-22 11:43:51
; Title: MeshExpand() 
; Description: It blows a mesh like a baloon moving vertices.

;***code by halo
Function MeshExpand(mesh,omesh,am#)

For k=1 To CountSurfaces(mesh) 
	surf=GetSurface(mesh,k) 
	surf2=GetSurface(omesh,k) 
	For index=0 To CountVertices(surf)-1 
		newx#=VertexX(surf2,index)+VertexNX(surf2,index)*am 
		newy#=VertexY(surf2,index)+VertexNY(surf2,index)*am 
		newz#=VertexZ(surf2,index)+VertexNZ(surf2,index)*am 
		VertexCoords surf,index,newx,newy,newz 
	Next 
Next 

End Function
