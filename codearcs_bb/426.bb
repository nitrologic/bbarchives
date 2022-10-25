; ID: 426
; Author: TeraBit
; Date: 2002-09-15 07:50:38
; Title: RemoveTRI
; Description: Removes an individual Triangle from an Unwelded Surface

Dim txv(3)

Type TRIS
	Field x0#
	Field y0#
	Field z0#

	Field u0#
	Field v0#

	Field U20#
	Field V20#
	
	Field x1#
	Field y1#
	Field z1#

	Field u1#
	Field v1#

	Field U21#
	Field V21#
	
	Field x2#
	Field y2#
	Field z2#

	Field u2#
	Field v2#

	Field U22#
	Field V22#
	
	Field surface
	
End Type


Function RemoveTRI(su,TRIGONE)

For tq = 0 To CountTriangles(su)-1
txv(0) = TriangleVertex(su,tq,0)
txv(1) = TriangleVertex(su,tq,1)
txv(2) = TriangleVertex(su,tq,2)
If tq <> TRIGONE Then
	vq.TRIS = New TRIS
	vq\x0# = VertexX(su,txv(0))
	vq\y0# = VertexY(su,txv(0))
	vq\z0# = VertexZ(su,txv(0))
	vq\u0# = VertexU(su,txv(0),0)
	vq\v0# = VertexV(su,txv(0),0)
	vq\u20# = VertexU(su,txv(0),1)
	vq\v20# = VertexV(su,txv(0),1)
	
	vq\x1# = VertexX(su,txv(1))
	vq\y1# = VertexY(su,txv(1))
	vq\z1# = VertexZ(su,txv(1))
	vq\u1# = VertexU(su,txv(1),0)
	vq\v1# = VertexV(su,txv(1),0)
	vq\u21# = VertexU(su,txv(1),1)
	vq\v21# = VertexV(su,txv(1),1)

	vq\x2# = VertexX(su,txv(2))
	vq\y2# = VertexY(su,txv(2))
	vq\z2# = VertexZ(su,txv(2))
	vq\u2# = VertexU(su,txv(2),0)
	vq\v2# = VertexV(su,txv(2),0)
	vq\u22# = VertexU(su,txv(2),1)
	vq\v22# = VertexV(su,txv(2),1)
	
EndIf
Next

ClearSurface su

For vq.tris = Each tris

		AddVertex su,vq\x0#,vq\y0#,vq\z0#,vq\u0#,vq\v0#
		VertexTexCoords su,mycount,vq\u20#,vq\v20#,0,1
		mycount = mycount +1

		AddVertex su,vq\x1#,vq\y1#,vq\z1#,vq\u1#,vq\v1#
		VertexTexCoords su,mycount,vq\u21#,vq\v21#,0,1
		mycount = mycount +1

		AddVertex su,vq\x2#,vq\y2#,vq\z2#,vq\u2#,vq\v2#
		VertexTexCoords su,mycount,vq\u22#,vq\v22#,0,1
		mycount = mycount +1


	AddTriangle su,mycount-3,mycount-2,mycount-1

Next

Delete Each tris

End Function
