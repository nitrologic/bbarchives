; ID: 735
; Author: elias_t
; Date: 2003-07-05 17:17:17
; Title: Remove individual triangles,vertices &amp; isolated vertices
; Description: Remove individual triangles,vertices & isolated vertices functions

;REMOVE INDIVIDUAL TRIANGLES , VERTICES & ISOLATED VERTICES
;BY ELIAS TSIANTAS [elias_t]

;#########################################################################
;YOU NEED THESE ARRAYS AND TYPES
;the original no of tris and verts must be declared !!!

Dim Trilist(0,2);the triangle list
Dim Vertlist#(0,4);the vertex list


;common tri
Type ctri
Field id
End Type

;#########################################################################

;THEN YOU CAN CALL 1 OF THE 3 FOLLOWING FUNCTIONS


;remove a single triangle

Function removetri(surf,tri)
	numtris=CountTriangles(surf)
	For t=0 To numtris-1
		trilist(t,0)=TriangleVertex(surf,t,0)
		trilist(t,1)=TriangleVertex(surf,t,1)
		trilist(t,2)=TriangleVertex(surf,t,2)
	Next
	ClearSurface surf,False,True
	For t=0 To numtris-1
		If t<>tri Then 	AddTriangle(surf,trilist(t,0),trilist(t,1),trilist(t,2))
	Next
End Function	 





;remove a single vertex

Function removevert(surf,vert)

	numtris=CountTriangles(surf)

	For t=0 To numtris-1
		trilist(t,0)=TriangleVertex(surf,t,0)
		trilist(t,1)=TriangleVertex(surf,t,1)
		trilist(t,2)=TriangleVertex(surf,t,2)
	Next


	numverts=CountVertices(surf)

	For t=0 To numverts-1
		Vertlist#(t,0)=VertexX(surf,t)
		Vertlist#(t,1)=VertexY(surf,t)
		Vertlist#(t,2)=VertexZ(surf,t)
		
		Vertlist#(t,3)=VertexU(surf,t,0)
		Vertlist#(t,4)=VertexV(surf,t,0)
		
	Next

	ClearSurface surf,True,True
	
	For t=0 To numverts-1
		If t<>vert
		AddVertex (surf,Vertlist#(t,0),Vertlist#(t,1),Vertlist#(t,2),Vertlist#(t,3),Vertlist#(t,4) )
		EndIf
	Next

;find which triangles were connected to this vertex

	For t=0 To numtris-1
	
		If trilist(t,0)=vert Or trilist(t,1)=vert Or trilist(t,2)=vert
		common.ctri=New ctri
		common\id=t
		EndIf
		
	Next

;rebuild the trilist
	For t=0 To numtris-1
	
	If trilist(t,0)>vert Then trilist(t,0)=trilist(t,0)-1
	If trilist(t,1)>vert Then trilist(t,1)=trilist(t,1)-1
	If trilist(t,2)>vert Then trilist(t,2)=trilist(t,2)-1
	
	Next
	

;add triangles that were not connected to the removed vert

	For t=0 To numtris-1
		
		tis=0
		common.ctri=First ctri
		For common.ctri=Each ctri
		If t=common\id Then tis=1
		Next


		
		If tis=0
		AddTriangle(surf,trilist(t,0),trilist(t,1),trilist(t,2))
		EndIf
		
	Next


Delete Each ctri

End Function






;remove isolated vertices

Function remove_iso_verts(surf)

For k=0 To 2;3 waves [?] needed

	numtris=CountTriangles(surf)
	For t=0 To numtris-1
		trilist(t,0)=TriangleVertex(surf,t,0)
		trilist(t,1)=TriangleVertex(surf,t,1)
		trilist(t,2)=TriangleVertex(surf,t,2)
	Next
	
	
	
	numverts=CountVertices(surf)

	For i=0 To numverts-1
	
	tis=0
	
	For t=0 To numtris-1
		If i=trilist(t,0) Then tis=1
		If i=trilist(t,1) Then tis=1
		If i=trilist(t,2) Then tis=1
	Next
	
	If tis=0
	removevert(surf,i)
	EndIf
	
	Next


Next

End Function



;#########################################################################
