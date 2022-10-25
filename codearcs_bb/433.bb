; ID: 433
; Author: TeraBit
; Date: 2002-09-20 06:39:22
; Title: PaintTriangle
; Description: A Function to Paint Individual Triangles

; Paint Triangle Function
; By Lee Page
; TeraBit Software
; PaintTriangle(Mesh, Surface, TriangleIndex, Brush) 
; Mesh Must be UnWelded using the Unweld(Mesh) function.


; Example of Use

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

Dim b(2)
Dim txv(3)

Graphics3D 640,480
Global mash, mashs
cam = CreateCamera()
cub = CreateCube()
EntityPickMode cub,2
lit = CreateLight(1)
PositionEntity lit,5,-5,3
PositionEntity cam,0,0,-5
RotateMesh cub,45,45,0
EntityFX cub,16


;***********************************
b(0) = LoadBrush ("shingle.bmp") ; *Substitute your own ones*
b(1) = LoadBrush ("oldbric.bmp") ; *Substitute your own ones*
b(2) = LoadBrush ("gothic3.bmp") ; *Substitute your own ones*
;***********************************

Unweld(cub) : UpdateNormals cub 

While Not KeyDown(1)

If KeyHit(78) And bno<2 Then bno=bno + 1
If KeyHit(74) And bno >0 Then bno=bno - 1

If MouseHit(1) Then 
	psf = CameraPick(cam,MouseX(),MouseY())
	If psf<>0 Then
		psf = PickedSurface()
		ind = PickedTriangle()
		PaintTriangle(cub,psf,ind,b(bno))
	EndIf
EndIf
TurnEntity cub,0,0.5,0

UpdateWorld
RenderWorld
Color 0,0,255
Oval MouseX(),MouseY(),5,5,False
Color 255,255,255

Text 10,10,"Brush: "+bno+" of 2."
Text 10,25,"Use + or - on Keypad to Change Brush"

Flip
Wend
End


Function unWeld(mish)
For nsurf = 1 To CountSurfaces(mish)
su=GetSurface(mish,nsurf)
For tq = 0 To CountTriangles(su)-1
txv(0) = TriangleVertex(su,tq,0)
txv(1) = TriangleVertex(su,tq,1)
txv(2) = TriangleVertex(su,tq,2)
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

For vq.tris = Each tris
Delete vq
Next

mycount=0
Next
End Function

Function PaintTriangle(PMesh, Surfhand, Trindex, Pbrush)
	
	dest = FindSurface(pmesh,pbrush)
	If dest = 0 Then dest = CreateSurface(pmesh,pbrush)	
	vertind = CountVertices(dest)
	For p=0 To 2
	vx# = VertexX(Surfhand,TriangleVertex(Surfhand,trindex,p))
	vy# = VertexY(Surfhand,TriangleVertex(Surfhand,trindex,p))
	vZ# = VertexZ(Surfhand,TriangleVertex(Surfhand,trindex,p))
			
	u# = VertexU(Surfhand,TriangleVertex(surfhand,trindex,p),0)
	V# = VertexV(Surfhand,TriangleVertex(surfhand,trindex,p),0)
			
	lmu# = VertexU(Surfhand,TriangleVertex(Surfhand,trindex,p),1)
	lmV# = VertexV(Surfhand,TriangleVertex(Surfhand,trindex,p),1)
	
				
		AddVertex dest,vx#,vy#,vz#
		VertexTexCoords dest,vertind+p,U#,v#,0,0
		VertexTexCoords dest,vertind+p,lmu#,lmv#,0,1
	Next
	vertind = CountVertices(dest)-3
	
	AddTriangle dest,vertind,vertind+1,vertind+2
	
	removetri(Surfhand,trindex)
	UpdateNormals pmesh
End Function

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

For vq.tris = Each tris
Delete vq
Next
End Function
