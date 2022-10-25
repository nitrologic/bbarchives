; ID: 874
; Author: Pedro
; Date: 2004-01-04 12:05:56
; Title: weld routine performance improvement
; Description: 90% improvement of the performance for "weld" function : 21s to 1,6s

;--------------------------------------------------------------------------------------------------------
;--------------------------------------------------------------------------------------------------------
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

Dim vt(1) ; this array stores the x,y,z values of each vertex just to check if the vertex exists or not

Graphics3D 640,480,0,2

cam = CreateCamera()
PositionEntity cam,0,0,-5

sph = CreateSphere(64)
EntityColor sph,128,128,128
RotateMesh sph,45,45,0
EntityFX sph,16
EntityPickMode sph,2

lit = CreateLight(1)
PositionEntity lit,5,-5,3


;***********************************
;b(0) = LoadBrush ("media\b3dlogo.jpg") ; *Substitute your own ones*
;b(1) = LoadBrush ("media\MossyGround.BMP") ; *Substitute your own ones*
;b(2) = LoadBrush ("media\sky.bmp") ; *Substitute your own ones*
b(0) = CreateBrush(255,0,0)
b(1) = CreateBrush(0,255,0)
b(2) = CreateBrush(0,0,255)
;***********************************


weld = True : weldtext$ = "weld"

Gosub count

While Not KeyDown(1)

	If KeyHit(78) And bno<2 Then bno=bno + 1
	If KeyHit(74) And bno >0 Then bno=bno - 1
	
	If MouseHit(1) Then 
		psf = CameraPick(cam,MouseX(),MouseY())
		If psf<>0 Then
			psf = PickedSurface()
			ind = PickedTriangle()
			PaintTriangle(sph,psf,ind,b(bno))
		EndIf
		weld = False : : weldtext = "unweld"

		Gosub count		
	EndIf
	
	If MouseHit(2) = 1
		
		If weld = True Then 
			unweld(sph) : weld = False 	: weldtext = "unweld"
		Else
			t1 = MilliSecs() 
			weld(sph) : weld = True	: weldtext = "weld"
			temps = MilliSecs() - t1
		EndIf
		
		Gosub count
	EndIf
	
	
	
	TurnEntity sph,0,0.1,0
	
	UpdateWorld
	RenderWorld
	
	Color 0,0,255
	Oval MouseX(),MouseY(),5,5,False
	Color 255,255,255
	
	Text 10,0,  "Brush                    : "+bno+" of 2."  
	Text 10,15, "Use + or - on Keypad     : Change Brush"
	Text 10,30, "click mouse left button  : color with brush on triangle"
	Text 10,45, "click mouse right button : weld/unweld"
	Text 10,60, "weld/unweld status       : " + weldtext
	Text 10,80, "statistics               : surf = " + surf + " vetrices = " + ver + " triangles = " + tri
	Text 10,100,"weld computation time    : " + temps
	Flip
Wend


End

.count

	ver  = 0
	tri = 0
	surf = CountSurfaces(sph)
	For i = 1 To surf
		su = GetSurface(sph,i)
		ver = ver + CountVertices( su)
		tri = tri + CountTriangles( su)
	Next
	UpdateNormals sph

Return

Function unWeld(mish)


	For vq.tris = Each tris
		Delete vq
	Next


	For nsurf = 1 To CountSurfaces(mish)

		su=GetSurface(mish,nsurf)

		For tq = 0 To CountTriangles(su)-1
			txv0 = TriangleVertex(su,tq,0)
			txv1 = TriangleVertex(su,tq,1)
			txv2 = TriangleVertex(su,tq,2)
			vq.TRIS = New TRIS
			vq\x0# = VertexX(su,txv0)
			vq\y0# = VertexY(su,txv0)
			vq\z0# = VertexZ(su,txv0)
			vq\u0# = VertexU(su,txv0,0)
			vq\v0# = VertexV(su,txv0,0)
			vq\u20# = VertexU(su,txv0,1)
			vq\v20# = VertexV(su,txv0,1)
			
			vq\x1# = VertexX(su,txv1)
			vq\y1# = VertexY(su,txv1)
			vq\z1# = VertexZ(su,txv1)
			vq\u1# = VertexU(su,txv1,0)
			vq\v1# = VertexV(su,txv1,0)
			vq\u21# = VertexU(su,txv1,1)
			vq\v21# = VertexV(su,txv1,1)
		
			vq\x2# = VertexX(su,txv2)
			vq\y2# = VertexY(su,txv2)
			vq\z2# = VertexZ(su,txv2)
			vq\u2# = VertexU(su,txv2,0)
			vq\v2# = VertexV(su,txv2,0)
			vq\u22# = VertexU(su,txv2,1)
			vq\v22# = VertexV(su,txv2,1)
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

	For vq.tris = Each tris
		Delete vq
	Next

	For tq = 0 To CountTriangles(su)-1
		txv0 = TriangleVertex(su,tq,0)
		txv1 = TriangleVertex(su,tq,1)
		txv2 = TriangleVertex(su,tq,2)
		If tq <> TRIGONE Then
			vq.TRIS = New TRIS
			vq\x0# = VertexX(su,txv0)
			vq\y0# = VertexY(su,txv0)
			vq\z0# = VertexZ(su,txv0)
			vq\u0# = VertexU(su,txv0,0)
			vq\v0# = VertexV(su,txv0,0)
			vq\u20# = VertexU(su,txv0,1)
			vq\v20# = VertexV(su,txv0,1)
			
			vq\x1# = VertexX(su,txv1)
			vq\y1# = VertexY(su,txv1)
			vq\z1# = VertexZ(su,txv1)
			vq\u1# = VertexU(su,txv1,0)
			vq\v1# = VertexV(su,txv1,0)
			vq\u21# = VertexU(su,txv1,1)
			vq\v21# = VertexV(su,txv1,1)
		
			vq\x2# = VertexX(su,txv2)
			vq\y2# = VertexY(su,txv2)
			vq\z2# = VertexZ(su,txv2)
			vq\u2# = VertexU(su,txv2,0)
			vq\v2# = VertexV(su,txv2,0)
			vq\u22# = VertexU(su,txv2,1)
			vq\v22# = VertexV(su,txv2,1)
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




; ID: 454
; Author: TeraBit
; Date: 2002-10-09 10:11:10
; Title: Weld()
; Description: Weld a mesh's Vertices

Function Weld(mish)

	RenderWorld : Text GraphicsWidth()/2,GraphicsHeight()/2,"Optimising Please Wait..",True,True : Flip  

	
	nbSurf = CountSurfaces(mish)
	For nsurf = 1 To nbSurf
	
		For vq.tris = Each tris
			Delete vq
		Next
	
		su=GetSurface(mish,nsurf)
		
		ntris = CountTriangles(su)
		Dim vt((ntris)*3)
		ntris = ntris-1
		
	
		For tq = 0 To ntris
			txv0 = TriangleVertex(su,tq,0)
			txv1 = TriangleVertex(su,tq,1)
			txv2 = TriangleVertex(su,tq,2)
			vq.TRIS = New TRIS
			vq\x0# = VertexX(su,txv0)
			vq\y0# = VertexY(su,txv0)
			vq\z0# = VertexZ(su,txv0)
			vq\u0# = VertexU(su,txv0,0)
			vq\v0# = VertexV(su,txv0,0)
			vq\u20# = VertexU(su,txv0,1)
			vq\v20# = VertexV(su,txv0,1)
			
			vq\x1# = VertexX(su,txv1)
			vq\y1# = VertexY(su,txv1)
			vq\z1# = VertexZ(su,txv1)
			vq\u1# = VertexU(su,txv1,0)
			vq\v1# = VertexV(su,txv1,0)
			vq\u21# = VertexU(su,txv1,1)
			vq\v21# = VertexV(su,txv1,1)
		
			vq\x2# = VertexX(su,txv2)
			vq\y2# = VertexY(su,txv2)
			vq\z2# = VertexZ(su,txv2)
			vq\u2# = VertexU(su,txv2,0)
			vq\v2# = VertexV(su,txv2,0)
			vq\u22# = VertexU(su,txv2,1)
			vq\v22# = VertexV(su,txv2,1)
		Next

		ClearSurface su
		
		mycount=0		
		
		For vq.tris = Each tris
		

			vt1=Findvert(vq\x0#,vq\y0#,vq\z0#,mycount)

			If vt1=-1 Then
				vt(mycount) = vq\x0#*1000 + vq\y0#*1000000 + vq\z0#*1000000000 ; storage of real values as 1 integer value

				AddVertex su,vq\x0#,vq\y0#,vq\z0#,vq\u0#,vq\v0#
				VertexTexCoords su,mycount,vq\u20#,vq\v20#,0,1
				vt1 = mycount
				mycount = mycount +1
			EndIf
	

			vt2=Findvert(vq\x1#,vq\y1#,vq\z1#,mycount)

			If Vt2=-1 Then
				vt(mycount) = vq\x1#*1000 + vq\y1#*1000000 + vq\z1#*1000000000 ; storage of real values as 1 integer value
				
				AddVertex su,vq\x1#,vq\y1#,vq\z1#,vq\u1#,vq\v1#
				VertexTexCoords su,mycount,vq\u21#,vq\v21#,0,1
				vt2 = mycount
				mycount = mycount +1
			EndIf

			vt3=Findvert(vq\x2#,vq\y2#,vq\z2#,mycount)
			
			If vt3=-1 Then 
				vt(mycount) = vq\x2#*1000 + vq\y2#*1000000 + vq\z2#*1000000000 ; storage of real values as 1 integer value
				
				AddVertex su,vq\x2#,vq\y2#,vq\z2#,vq\u2#,vq\v2#
				VertexTexCoords su,mycount,vq\u22#,vq\v22#,0,1
				vt3 = mycount
				mycount = mycount +1
			EndIf
	
			AddTriangle su,vt1,vt2,vt3
	
		Next
	
	Next
	
	Dim vt(1)
	For vq.tris = Each tris
		Delete vq
	Next

End Function

; Some usefull technics to optmize saerch in vertex
;
; The control needed is only on x,y,z because it is the position of vertex which is the most important
; The value are stored into an array insted of search it into the 3d model.
; It saves :  22881 ms  => 4571 ms
;
; The search is better if you don't follow the sequence order : but search as described in the function 
; It is seems that the ramdom is better than the sequence !!!
; it saves : 4571 ms => 2900 ms
;
; an integer array is used :  comparison of integer value is faster than comparison between real value in the array 
; it saves : 2900 ms => 1565 ms

Function findvert(x#,y#,z#,max)

	xx = x# * 1000 + y# * 1000000 + z# * 1000000000

	For j = 0 To 9
		For t=j To max Step 10
			If vt(t)=xx Then 
				 Return t
			EndIf
		Next
	Next 
	
	Return -1 

End Function
