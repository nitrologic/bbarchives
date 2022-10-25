; ID: 560
; Author: Markus Rauch
; Date: 2003-01-26 09:44:33
; Title: CSG Functions another edition
; Description: 02.11.03 Use Boolean Union/Subtraction/Intersection ,  with UV and multiple Surfaces

; MR 02.11.2003 

;####################################################################################################

;Csg Engine 

;Thanks to David Dawkins(Starfox) for the bb code example ;-)

;MR 19.01.2003
;tuned and optimized , now by cube subtract sphere 2 seconds + :-)
;MR 25.01.2003
;add uvmapping :-) slow down but works
;MR 26.01.2003
;Multiple Surfaces :-)
;MR 02.11.2003
;Repaint target Surfaces , need BB 3D => V 1.85 !

;Features: Union, Subtraction, Intersect, Fast enough, Removes bad triangles
;Call the CSG(mesha,meshb,booltype) to do the actual functions

;Limits: Not to fast with many tris
;It works only if all Entity have no Pickmode !

;What CSG() returns is the result mesh, not the actual meshes you send to it
;so you might want to delete those.

;NOTE:AddMesh add no surfaces :-( , and with CopyMesh you became only 1 surface :-(

;####################################################################################################

;Here are the globals
Global csgscale#=1,csgpiv ;The world scale (1- Normal, 2-2x Normal)
Global csgmesh[2] ;Different surface holders for different opereations

;-----------------------

Type CSGVectorType
 Field x#
 Field y#
 Field z#

 Field u# ;Texture Coords !
 Field v#
 Field w#
End Type

;-----------------------

Type CSGTriangleTYPE

 Field id
 Field tarid            ;See Split
 Field del              ;Can be deleted
 Field mindex           ;Mesh Index (NOT a Handle)
 Field sindex						;Surface Index			
 Field tindex 					;Triangle Index

 Field v1x#           ;Edge Vertex 1
 Field v1y#
 Field v1z#
 Field u1#
 Field v1#
 Field w1#

 Field v2x#           ;Edge Vertex 2
 Field v2y#
 Field v2z#
 Field u2#
 Field v2#
 Field w2#

 Field v3x#           ;Edge Vertex 3
 Field v3y#
 Field v3z#
 Field u3#
 Field v3#
 Field w3#

 Field mix#   ;Middle Vector from all Edge Vertices
 Field miy# 
 Field miz#

 Field normx# ;Normal of Triangle
 Field normy# 
 Field normz# 

End Type

;###############################################################################################

.CSGTestIt

;Commend the two lines if you include this CSG file !

CSGExample()
End

;###############################################################################################

Function CSG(mesh1,mesh2,mode=2)

 ;MR 02.11.2003

 ;mode 1 - Union, mode 2 - Subtraction, mode 3 - Intersection

 Local x1#,y1#,z1#
 Local x2#,y2#,z2#
 Local x3#,y3#,z3#

 Local vi1,vi2,vi3    ;Only VertexIndex

 ;-------------------------------------------------------------------------------------- Memory Position and Angles

 Local ex#,ey#,ez#

 ex=EntityX(mesh1,1)
 ey=EntityY(mesh1,1)
 ez=EntityZ(mesh1,1)

 Local epitch#,eyaw#,eroll#

 epitch=EntityPitch(mesh1,1)
 eyaw  =EntityYaw  (mesh1,1)
 eroll =EntityRoll (mesh1,1)

 ;--------------------------------------------------------------------------------------

 Local curmesh
 Local mindex
 Local surf,sindex
 Local SurfaceBrush
 Local tindex
 Local idstate=0
 Local t.CSGTriangleTYPE
 Local t1.CSGTriangleTYPE
 Local t2.CSGTriangleTYPE

 Local ve1.CSGVectorType
 Local ve2.CSGVectorType
 Local ve3.CSGVectorType

 For mindex = 1 To 4

 If mindex = 1 Then curmesh = mesh1
 If mindex = 2 Then curmesh = mesh2
 If mindex = 3 Then curmesh = mesh1
 If mindex = 4 Then curmesh = mesh2

 ;Now copy all 4 Meshes to Buffer

 For sindex = 1 To CountSurfaces(curmesh)

  ;DebugLog "sindex "+sindex

	surf = GetSurface(curmesh,sindex)
	For tindex = 0 To CountTriangles(surf)-1
   
   ;---------------------------------------
  
	 vi1 = TriangleVertex(surf,tindex,0)
	 vi2 = TriangleVertex(surf,tindex,1)
	 vi3 = TriangleVertex(surf,tindex,2)

   ve1=CSGGetVertexRealPosition(curmesh,surf,vi1)
   ve2=CSGGetVertexRealPosition(curmesh,surf,vi2)
   ve3=CSGGetVertexRealPosition(curmesh,surf,vi3)

   t=CSGTriAdd(ve1,ve2,ve3)

	 t\mindex = mindex
	 t\sindex = sindex 
	 t\tindex = tindex
	 t\tarid = -1

   ;---------------------------------------

	Next ;Triangles
 Next ;Surfaces
 Next ;1-4

 CSGTriRemoveDel

 ;--------------------------------------------------------------------------------------

 ;Create the reuse objects
 csgmesh[0] = CreateMesh()
 surf = CreateSurface(csgmesh[0])
 vi1 = AddVertex(surf,-1,0,1)
 vi2 = AddVertex(surf,1,0,1)
 vi3 = AddVertex(surf,1,0,-1)
 AddTriangle(surf,vi1,vi2,vi3)

 ;----------------------------------------

 csgmesh[1] = CreateMesh()
 surf = CreateSurface(csgmesh[1])
 vi1 = AddVertex(surf,-1,0,1)
 vi2 = AddVertex(surf,1,0,1)
 vi3 = AddVertex(surf,1,0,-1)
 AddTriangle(surf,vi1,vi2,vi3)

 ;----------------------------------------

 csgmesh[2] = CreateCube()
 ScaleMesh csgmesh[2],100000*csgscale,0,100000*csgscale ;<--- mesh vertices must be in range of float !

 ;----------------------------------------

 ;Time to split the a polys
 For t1.CSGTriangleTYPE = Each CSGTriangleTYPE
 If t1\mindex = 2
	For t2.CSGTriangleTYPE = Each CSGTriangleTYPE
	If t2\mindex = 1 And t2\tarid <> t1\id
	 If t2\del = 0
	  If CSGTrisIntersect(t1,t2) = 1
	 	 CSGSplit t1,t2
	  EndIf
	 EndIf
	EndIf
	Next
 EndIf
 Next

 ;----------------------------------------

 ;Now split the b poly's
 For t1.CSGTriangleTYPE = Each CSGTriangleTYPE
 If t1\mindex = 3
	For t2.CSGTriangleTYPE = Each CSGTriangleTYPE
	If t2\mindex = 2 And t2\tarid <> t1\id
	 If t2\del = 0
	  If CSGTrisIntersect(t1,t2) = 1
		 CSGSplit t1,t2
	  EndIf
	 EndIf
	EndIf
	Next
 EndIf
 Next

 ;----------------------------------------

 ;Step 2 of reuse
 surf = GetSurface(csgmesh[0],1)
 AddTriangle(surf,2,1,0)

 FreeEntity csgmesh[1]
 FreeEntity csgmesh[2]

 csgpiv = CreatePivot()

 ;----------------------------------------

 ;Setting a flag to delete all the triangles without normals
 For t1.CSGTriangleTYPE = Each CSGTriangleTYPE
  If t1\normx = 0.0 And t1\normy = 0.0 And t1\normz = 0.0 ;And t1\del = 0
   ;DebugLog "NORM=0 : "+t1\normx+" "+t1\normy+" "+t1\normz+" MIndex="+t1\mindex
   Delete t1
  EndIf
 Next

 ;----------------------------------------

 Local epsilon#=0.000001

 ;MeshA
 For t1.CSGTriangleTYPE = Each CSGTriangleTYPE
 If t1\mindex = 1 And t1\del = 0
  cosangle# = -1
  inter = 0
  intermode = 0 ;1 - inside, 2 - shared, 3 - Not inside
  quickdist# = 100000

	For t2.CSGTriangleTYPE = Each CSGTriangleTYPE
	 If t2\mindex = 4
	  res = CSGRayIntersect(t2,t1)
	  If res = 1
	   intx# = PickedX()
	   inty# = PickedY()
	   intz# = PickedZ()
	   dist# = Sqr((intx-t1\mix)*(intx-t1\mix)+(inty-t1\miy)*(inty-t1\miy)+(intz-t1\miz)*(intz-t1\miz))
	   If dist < quickdist
	    quickdist = dist
	    inter = 1
	    cosangle# = (t1\normx*t2\normx)+(t1\normy*t2\normy)+(t1\normz*t2\normz)
	   EndIf
	  EndIf
	 EndIf
	Next
	
	If inter = 1
	 If cosangle > 0 Then intermode = 1
	 If cosangle < 0 Then intermode = 3
	 If quickdist < epsilon ;If the triangle is shared
	  intermode = 2
	 EndIf
	Else
	 intermode = 3
	EndIf

  Select mode
  Case  2
	 If intermode = 1 Or intermode = 2
	  t1\del = 1
	 EndIf
  Case 1
	 If intermode = 1 Or intermode = 2
	  t1\del = 1
	 EndIf
  Case  3
	 If intermode = 3 Or intermode = 2
	  t1\del = 1
	 EndIf
  End Select

  If t1\del=1 Then Delete t1

 EndIf ;Mesh A
 Next ;Tri

 ;----------------------------------------

 ;MeshB
 For t.CSGTriangleTYPE = Each CSGTriangleTYPE
 If t\mindex = 2 And t\del = 0
  cosangle = -1
  inter = 0
  intermode = 0;1 - inside, 2 - shared, 3 - not inside
  quickdist# = 100000
	For tr.CSGTriangleTYPE = Each CSGTriangleTYPE
	 If tr\mindex = 3
	  res = CSGRayIntersect(tr,t)
	  If res = 1
	   intx# = PickedX()
	   inty# = PickedY()
	   intz# = PickedZ()
	   dist# = Sqr((intx-t\mix)*(intx-t\mix)+(inty-t\miy)*(inty-t\miy)+(intz-t\miz)*(intz-t\miz))
	   If dist < quickdist
	    quickdist = dist
	    inter = 1
	    cosangle# = (t\normx*tr\normx)+(t\normy*tr\normy)+(t\normz*tr\normz)
	   EndIf
	  EndIf
	 EndIf
	Next
	
	If inter = 1
	 If cosangle > 0 Then intermode = 1
	 If cosangle < 0 Then intermode = 3
	 If quickdist < epsilon
	  intermode = 2
	 EndIf
	Else
	 intermode = 3
	EndIf
	
  If mode = 2
	 If intermode = 3 Or intermode = 2
	  t\del = 1
	 EndIf
  ElseIf mode = 1
	 If intermode = 1
	  t\del = 1
	 EndIf
  ElseIf mode = 3
	 If intermode = 3
	  t\del = 1
	 EndIf
  EndIf

  If t\del=1 Then Delete t

 EndIf
 Next

 ;----------------------------------------

 FreeEntity csgpiv
 FreeEntity csgmesh[0]

 ;----------------------------------------

 ;Now delete the polys that don't belong(loose polys)

 CSGClearlTris 1 
 CSGClearlTris 2

 ;----------------------------------------------- New Mesh

 Local mesh=CSGMakeNewMesh()
 
 CSGAddTheTriangles mesh,1

 ;Now add Mesh 2 to Mesh 1
 If mode = 2
  CSGAddTheTriangles mesh,2,True ;Flips all the triangles in a mesh
 Else
  CSGAddTheTriangles mesh,2,False
 EndIf

 ;----------------------------------------------- Repaint !

 CSGRepaint mesh,mesh1,mesh2

 ;----------------------------------------------- Reposition Mesh

 PositionMesh   mesh,-ex,-ey,-ez
 PositionEntity mesh, ex, ey, ez

 RotateMesh mesh,      0,-eyaw,0
 RotateMesh mesh,-epitch,    0,0
 RotateMesh mesh,      0,    0,-eroll

 RotateEntity mesh,epitch,eyaw,eroll

 ;-----------------------------------------------

 Delete Each CSGTriangleTYPE ;Free the whole Triangle Collection

 Return mesh

End Function

;###############################################################################################

Function CSGVec.CSGVectorType(x#,y#,z#,u#,v#,w#) 

 ;MR 25.01.2003 

 ;Simple Return a Vector as type :-)

 Local ve.CSGVectorType = New CSGVectorType

 ve\x=x#
 ve\y=y#
 ve\z=z#

 ve\u=u#
 ve\v=v#
 ve\w=w#

 Return ve

End Function

;###############################################################################################

Function CSGTriAdd.CSGTriangleTYPE(v1.CSGVectorType,v2.CSGVectorType,v3.CSGVectorType)

 ;MR 02.11.2003

 Local newid=0
 Local tr.CSGTriangleTYPE = Last CSGTriangleTYPE
 If tr<>Null Then newid = tr\id + 1

 ;------------------------------------------

  t.CSGTriangleTYPE = New CSGTriangleTYPE

  t\id = newid

 ;------------------------------------------ Copy Parameters V1,V2,V3

	t\v1x = v1\x
	t\v1y = v1\y
	t\v1z = v1\z

	t\v2x = v2\x
	t\v2y = v2\y
	t\v2z = v2\z

	t\v3x = v3\x
	t\v3y = v3\y
	t\v3z = v3\z

 ;------------------------------------------ Memory UVW for Vertex V1,V2,V3

	t\u1 = v1\u
	t\v1 = v1\v
	t\w1 = v1\w

	t\u2 = v2\u
	t\v2 = v2\v
	t\w2 = v2\w

	t\u3 = v3\u
	t\v3 = v3\v
	t\w3 = v3\w

 ;------------------------------------------ Middle

  t\mix = (t\v1x + t\v2x + t\v3x) / 3.0
  t\miy = (t\v1y + t\v2y + t\v3y) / 3.0
  t\miz = (t\v1z + t\v2z + t\v3z) / 3.0

 ;------------------------------------------ Normal

  Local ax#,ay#,az#
  Local bx#,by#,bz#

  ax#=t\v2x-t\v1x
  ay#=t\v2y-t\v1y
  az#=t\v2z-t\v1z
	
  bx#=t\v3x-t\v2x
  by#=t\v3y-t\v2y
  bz#=t\v3z-t\v2z

  t\normx=(ay#*bz#)-(az#*by#)
  t\normy=(az#*bx#)-(ax#*bz#)
  t\normz=(ax#*by#)-(ay#*bx#)

  Local normlen# = Sqr#((t\normx*t\normx)+(t\normy*t\normy)+(t\normz*t\normz))
  If normlen > 0.0
   t\normx = t\normx/normlen 
   t\normy = t\normy/normlen 
   t\normz = t\normz/normlen
  Else
   t\del = 1
  EndIf

 ;------------------------------------------

 Return t

End Function

;###############################################################################################
	
Function CSGTrisIntersect(t1.CSGTriangleTYPE,t2.CSGTriangleTYPE)

 ;MR 19.01.2003

 ;if one triangle in another triangle

 Local surf

 surf = GetSurface(csgmesh[0],1) ;Dummy Mesh with one triangle :-)

 VertexCoords(surf,0,t1\v1x,t1\v1y,t1\v1z)
 VertexCoords(surf,1,t1\v2x,t1\v2y,t1\v2z)
 VertexCoords(surf,2,t1\v3x,t1\v3y,t1\v3z)

 surf = GetSurface(csgmesh[1],1)

 VertexCoords(surf,0,t2\v1x,t2\v1y,t2\v1z)
 VertexCoords(surf,1,t2\v2x,t2\v2y,t2\v2z)
 VertexCoords(surf,2,t2\v3x,t2\v3y,t2\v3z)

 Return MeshesIntersect(csgmesh[0],csgmesh[1])

End Function

;###############################################################################################

Function CSGRayIntersect(t1.CSGTriangleTYPE,t2.CSGTriangleTYPE)

 ;MR 02.11.2003

 ;i think it used to find the triangles that can be deleted

 Local surf = GetSurface(csgmesh[0],1) ;Dummy Mesh with one triangle :-)

 VertexCoords(surf,0,t1\v1x,t1\v1y,t1\v1z)
 VertexCoords(surf,1,t1\v2x,t1\v2y,t1\v2z)
 VertexCoords(surf,2,t1\v3x,t1\v3y,t1\v3z)

 Local piv = csgpiv
 RotateEntity piv,0,0,0 ;!?
 PositionEntity(piv,t2\mix  ,t2\miy  ,t2\miz)
 AlignToVector (piv,t2\normx,t2\normy,t2\normz,3)
 MoveEntity piv,0,0,100000.0*csgscale ;<--- !

 EntityPickMode csgmesh[0],2

 Local distx# = EntityX(piv)-t2\mix
 Local disty# = EntityY(piv)-t2\miy
 Local distz# = EntityZ(piv)-t2\miz
 Local picked = LinePick(t2\mix,t2\miy,t2\miz,distx,disty,distz)

 EntityPickMode csgmesh[0],0

 If picked Then
  Return 1
 Else
  Return 0
 EndIf

End Function

;###############################################################################################

Function CSGSplit(t1.CSGTriangleTYPE,t2.CSGTriangleTYPE)

 ;MR 02.11.2003

 ;if CSGTrisIntersect = true than split it to new triangles

 ;now the new triangles are in menory triangle type collection !

 If t1 = Null Or t2 = Null Then Return 0

;------------------------------------------------------------

 Local newvx#[3]
 Local newvy#[3]
 Local newvz#[3]

 Local newu#[3]
 Local newv#[3]
 Local neww#[3]

 Local v.CSGVectorType 

 Local edge1,edge2,edge3

 Local epsilon#=0.000001 

;------------------------------------------------------------

 Local cube = csgmesh[2]
 EntityPickMode cube,2
 PositionEntity cube,t1\mix,t1\miy,t1\miz
 RotateEntity cube,0,0,0
 AlignToVector(cube,t1\normx,t1\normy,t1\normz,2)

;------------------------------------------------------------

 ;Edge1 to 2

 distx# = t2\v1x-t2\v2x
 disty# = t2\v1y-t2\v2y
 distz# = t2\v1z-t2\v2z

 picked = LinePick(t2\v2x,t2\v2y,t2\v2z,distx,disty,distz)
 If picked = cube
  newvx[1] = PickedX()
  newvy[1] = PickedY()
  newvz[1] = PickedZ()

  v=CSGPickedUVW(t2)

  newu[1]=v\u
  newv[1]=v\v
  neww[1]=v\w
 
  distx# = newvx[1] - t2\v2x
  disty# = newvy[1] - t2\v2y
  distz# = newvz[1] - t2\v2z
  cdist# = Sqr(distx*distx + disty*disty + distz*distz)
  If cdist <= epsilon
   edge1 = False
  Else
   edge1 = True
  EndIf

 EndIf

;------------------------------------------------------------

 ;Edge2 to 3

 distx# = t2\v2x-t2\v3x
 disty# = t2\v2y-t2\v3y
 distz# = t2\v2z-t2\v3z

 picked = LinePick(t2\v3x,t2\v3y,t2\v3z,distx,disty,distz)
 If picked = cube
  newvx[2] = PickedX()
  newvy[2] = PickedY()
  newvz[2] = PickedZ()

  v=CSGPickedUVW(t2)

  newu[2]=v\u
  newv[2]=v\v
  neww[2]=v\w

  distx# = newvx[2] - t2\v3x
  disty# = newvy[2] - t2\v3y
  distz# = newvz[2] - t2\v3z
  cdist# = Sqr(distx*distx + disty*disty + distz*distz)
  If cdist <= epsilon
   edge2 = False
  Else
   edge2 = True
  EndIf

 EndIf

;------------------------------------------------------------

 ;Edge3 to 1

 distx# = t2\v3x-t2\v1x
 disty# = t2\v3y-t2\v1y
 distz# = t2\v3z-t2\v1z

 picked = LinePick(t2\v1x,t2\v1y,t2\v1z,distx,disty,distz)
 If picked = cube
  newvx[3] = PickedX()
  newvy[3] = PickedY()
  newvz[3] = PickedZ()

  v=CSGPickedUVW(t2)

  newu[3]=v\u
  newv[3]=v\v
  neww[3]=v\w

  distx# = newvx[3] - t2\v1x
  disty# = newvy[3] - t2\v1y
  distz# = newvz[3] - t2\v1z
  cdist# = Sqr(distx*distx + disty*disty + distz*distz)
  If cdist <= epsilon
   edge3 = False
  Else
   edge3 = True
  EndIf

 EndIf

;------------------------------------------------------------

 EntityPickMode cube,0

 If edge1=0 And edge2=0 And edge3=0 Then Return 0

;------------------------------------------------------------

 Local v1.CSGVectorType = New CSGVectorType
 Local v2.CSGVectorType = New CSGVectorType
 Local v3.CSGVectorType = New CSGVectorType
 Local v4.CSGVectorType = New CSGVectorType
 Local v5.CSGVectorType = New CSGVectorType

 Local mindex = t2\mindex
 Local sindex = t2\sindex
 Local tm.CSGTriangleTYPE

;------------------------------------------------------------

 If edge1 And edge2

  t2\del = 1

  v1 = CSGVec(t2\v1x,t2\v1y,t2\v1z,t2\u1,t2\v1,t2\w1)
  v2 = CSGVec(newvx[1],newvy[1],newvz[1],newu[1],newv[1],neww[1])
  v3 = CSGVec(t2\v2x,t2\v2y,t2\v2z,t2\u2,t2\v2,t2\w2)
  v4 = CSGVec(newvx[2],newvy[2],newvz[2],newu[2],newv[2],neww[2])
  v5 = CSGVec(t2\v3x,t2\v3y,t2\v3z,t2\u3,t2\v3,t2\w3)

  tm = CSGTriAdd(v1,v2,v5)
  tm\mindex= mindex
  tm\sindex= sindex
  tm\tarid = t1\id

  tm = CSGTriAdd(v2,v4,v5)
  tm\mindex= mindex
  tm\sindex= sindex
  tm\tarid = t1\id

  tm = CSGTriAdd(v2,v3,v4)
  tm\mindex= mindex
  tm\sindex= sindex
  tm\tarid = t1\id

 ElseIf edge1 And edge3

  t2\del = 1

  v1 = CSGVec(t2\v1x,t2\v1y,t2\v1z,t2\u1,t2\v1,t2\w1)
  v2 = CSGVec(newvx[1],newvy[1],newvz[1],newu[1],newv[1],neww[1])
  v3 = CSGVec(t2\v2x,t2\v2y,t2\v2z,t2\u2,t2\v2,t2\w2)
  v4 = CSGVec(t2\v3x,t2\v3y,t2\v3z,t2\u3,t2\v3,t2\w3)
  v5 = CSGVec(newvx[3],newvy[3],newvz[3],newu[3],newv[3],neww[3])

  tm = CSGTriAdd(v1,v2,v5) ;1,2,5
  tm\mindex= mindex
  tm\sindex= sindex
  tm\tarid = t1\id

  tm = CSGTriAdd(v2,v4,v5)
  tm\mindex= mindex
  tm\sindex= sindex
  tm\tarid = t1\id

  tm = CSGTriAdd(v2,v3,v4)
  tm\mindex= mindex
  tm\sindex= sindex
  tm\tarid = t1\id

 ElseIf edge2 And edge3

  t2\del = 1

  v1 = CSGVec(t2\v1x,t2\v1y,t2\v1z,t2\u1,t2\v1,t2\w1)
  v2 = CSGVec(t2\v2x,t2\v2y,t2\v2z,t2\u2,t2\v2,t2\w2)
  v3 = CSGVec(newvx[2],newvy[2],newvz[2],newu[2],newv[2],neww[2])
  v4 = CSGVec(t2\v3x,t2\v3y,t2\v3z,t2\u3,t2\v3,t2\w3)
  v5 = CSGVec(newvx[3],newvy[3],newvz[3],newu[3],newv[3],neww[3])

  tm = CSGTriAdd(v1,v2,v5)
  tm\mindex= mindex
  tm\sindex= sindex
  tm\tarid = t1\id

  tm = CSGTriAdd(v2,v3,v5)
  tm\mindex= mindex
  tm\sindex= sindex
  tm\tarid = t1\id

  tm = CSGTriAdd(v3,v4,v5)
  tm\mindex= mindex
  tm\sindex= sindex
  tm\tarid = t1\id

 ElseIf edge2

  t2\del = 1

  v1 = CSGVec(t2\v1x,t2\v1y,t2\v1z,t2\u1,t2\v1,t2\w1)
  v2 = CSGVec(t2\v2x,t2\v2y,t2\v2z,t2\u2,t2\v2,t2\w2)
  v3 = CSGVec(newvx[2],newvy[2],newvz[2],newu[2],newv[2],neww[2])
  v4 = CSGVec(t2\v3x,t2\v3y,t2\v3z,t2\u3,t2\v3,t2\w3)

  tm = CSGTriAdd(v1,v2,v3)
  tm\mindex= mindex
  tm\sindex= sindex
  tm\tarid = t1\id

  tm = CSGTriAdd(v1,v3,v4)
  tm\mindex= mindex
  tm\sindex= sindex
  tm\tarid = t1\id

 ElseIf edge1

  t2\del = 1

  v1 = CSGVec(t2\v1x,t2\v1y,t2\v1z,t2\u1,t2\v1,t2\w1)
  v2 = CSGVec(newvx[1],newvy[1],newvz[1],newu[1],newv[1],neww[1]) 
  v3 = CSGVec(t2\v2x,t2\v2y,t2\v2z,t2\u2,t2\v2,t2\w2)
  v4 = CSGVec(t2\v3x,t2\v3y,t2\v3z,t2\u3,t2\v3,t2\w3)

  tm = CSGTriAdd(v1,v2,v4)
  tm\mindex= mindex
  tm\sindex= sindex
  tm\tarid = t1\id

  tm = CSGTriAdd(v2,v3,v4)
  tm\mindex= mindex
  tm\sindex= sindex
  tm\tarid = t1\id

 ElseIf edge3

  t2\del = 1

  v1 = CSGVec(t2\v1x,t2\v1y,t2\v1z,t2\u1,t2\v1,t2\w1)
  v2 = CSGVec(t2\v2x,t2\v2y,t2\v2z,t2\u2,t2\v2,t2\w2)
  v3 = CSGVec(t2\v3x,t2\v3y,t2\v3z,t2\u3,t2\v3,t2\w3)
  v4 = CSGVec(newvx[3],newvy[3],newvz[3],newu[3],newv[3],neww[3])

  tm = CSGTriAdd(v1,v2,v4)
  tm\mindex= mindex
  tm\sindex= sindex
  tm\tarid = t1\id

  tm = CSGTriAdd(v2,v3,v4)
  tm\mindex= mindex
  tm\sindex= sindex
  tm\tarid = t1\id

 EndIf

 If t2\del=1 Then Delete t2

End Function

;###############################################################################################

Function CSGTriRemoveDel()

 ;MR 19.01.2003

 ;-----------------------------------------------

 For t.CSGTriangleTYPE = Each CSGTriangleTYPE
  If t\del=1 Then Delete t  
 Next

End Function

;###############################################################################################

Function CSGMakeNewMesh()

 ;MR 26.01.2003

 ;----------------------------------------------- New Mesh

 Local m=CreateMesh()

 ;----------------------------------------------- Find Max Surfaces for Mesh 1

 Local smax1=0

 For t.CSGTriangleTYPE = Each CSGTriangleTYPE
  If t\mindex=1 Then
   If t\sindex>smax1 Then smax1=t\sindex  
  EndIf
 Next

 ;----------------------------------------------- Find Max Surfaces for Mesh 2

 Local smax2=0

 For t.CSGTriangleTYPE = Each CSGTriangleTYPE
  If t\mindex=2 Then
   If t\sindex>smax2 Then smax2=t\sindex  
  EndIf
 Next

 ;----------------------------------------------- Add Max Surface Index from Mesh 1 to Mesh 2

 For t.CSGTriangleTYPE = Each CSGTriangleTYPE
  If t\mindex=2 Then
   t\sindex=t\sindex+smax1
  EndIf
 Next

 ;----------------------------------------------- Create all Surfaces

 Local smax=smax1+smax2 ;all needed surfaces

 Local si

 Local surf

 For si=1 To smax

  surf=CreateSurface(m)
 
 Next

 ;-----------------------------------------------

 Return m

End Function

;###############################################################################################

Function CSGAddTheTriangles(m,mindex,FlipTriangles=0)

 ;MR 02.11.2003

 ;----------------------------------------------- Bring Triangle to Surface

 Local v1,v2,v3 ;VertexIndex

 Local surf=0

 Local si

 ;-----------------------------------------------

 For si=1 To CountSurfaces(m)

  For t.CSGTriangleTYPE = Each CSGTriangleTYPE
   If t\del=0 And t\mindex=mindex And t\sindex=si Then

    surf=GetSurface(m,si)  

    v1=AddVertex(surf,t\v1x,t\v1y,t\v1z)
    v2=AddVertex(surf,t\v2x,t\v2y,t\v2z)
    v3=AddVertex(surf,t\v3x,t\v3y,t\v3z)

    VertexNormal surf,v1,t\normx,t\normy,t\normz 
    VertexNormal surf,v2,t\normx,t\normy,t\normz 
    VertexNormal surf,v3,t\normx,t\normy,t\normz 

    VertexTexCoords surf,v1,t\u1,t\v1,t\w1 ;Set UV Coords !
    VertexTexCoords surf,v2,t\u2,t\v2,t\w2 
    VertexTexCoords surf,v3,t\u3,t\v3,t\w3 

    If FlipTriangles=0 Then
     AddTriangle surf,v1,v2,v3
    Else
     AddTriangle surf,v3,v2,v1
    EndIf

    Delete t ;Remove the Triangle from Collection !

   EndIf  
  Next

 Next ;All Surfaces

 ;-----------------------------------------------

 UpdateNormals m

End Function

;###############################################################################################

Function CSGRepaint(meshnew,mesh1,mesh2)

 ;MR 02.11.2003

 ;Repaint all Surfaces :-)

 ;----------------------------------------------------------------------------------------------

  Local cc1=0 ;New SurfaceCount !

  Local c1

  ;Paint New Surfaces from Mesh A  

  If CountSurfaces (mesh1)=>1 Then 
   For c1=1 To CountSurfaces(mesh1)
    cc1=cc1+1
   
    If CountSurfaces(meshnew)=>cc1 Then 
     PaintSurface GetSurface(meshnew,cc1),GetSurfaceBrush(GetSurface(mesh1,c1))
    EndIf 

   Next
  EndIf ;CountSurfaces=>1

 ;----------------------------------------------------------------------------------------------

  ;Paint New Surfaces from Mesh B  

  If CountSurfaces(mesh2)=>1 Then 
   For c1=1 To CountSurfaces(mesh2)
    cc1=cc1+1
   
    If CountSurfaces(meshnew)=>cc1 Then 
     PaintSurface GetSurface(meshnew,cc1),GetSurfaceBrush(GetSurface(mesh2,c1))
    EndIf 

   Next
  EndIf ;CountSurfaces=>1

 ;----------------------------------------------------------------------------------------------

End Function

;###############################################################################################

Function CSGClearLTris(mindex)

 ;MR 26.01.2003

 ;Clears the triangles that have no partners
 Local epsilon#=0.000001 

 Local count=0
 Local full
 Local car

 Local distx#
 Local disty#
 Local distz#
 Local cdist#

 Local vax#
 Local vay#
 Local vaz#

 Local vbx#
 Local vby#
 Local vbz#

 For t.CSGTriangleTYPE = Each CSGTriangleTYPE
  If t\mindex = mindex And t\del = 0
   count=0
   For full = 0 To 2
    Select full
    Case 0
     vax#=t\v1x
     vay#=t\v1y
     vaz#=t\v1z
    Case 1
     vax#=t\v2x
     vay#=t\v2y
     vaz#=t\v2z
    Case 2
     vax#=t\v3x
     vay#=t\v3y
     vaz#=t\v3z
    End Select

 	  For tom.CSGTriangleTYPE = Each CSGTriangleTYPE
 	   If t\id<>tom\id And tom\del = 0 And tom\mindex = mindex
	    cdist = 0
	    For car = 0 To 2
       Select car
       Case 0
        vbx#=tom\v1x
        vby#=tom\v1y
        vbz#=tom\v1z
       Case 1
        vbx#=tom\v2x
        vby#=tom\v2y
        vbz#=tom\v2z
       Case 2
        vbx#=tom\v3x
        vby#=tom\v3y
        vbz#=tom\v3z
       End Select
		   distx# = vbx - vax
		   disty# = vby - vay
		   distz# = vbz - vaz 
		   cdist# = Sqr(distx*distx + disty*disty + distz*distz)
		   If cdist <= epsilon Then Exit
	    Next
 	    If cdist >= 0.0 Then count = count + 1:Exit
	   EndIf
	  Next
   Next

   If count < 3
    Delete t
   EndIf
  EndIf
 Next

End Function

;############################################################################################################

Function CSGGetVertexRealPosition.CSGVectorType(entity,surf,vindex)

 Local pivot1=CreatePivot()
 Local pivot2=CreatePivot(pivot1)

 Local vreal.CSGVectorType =New CSGVectorType

 PositionEntity pivot1,0,0,0
 PositionEntity pivot2,VertexX(surf,vindex),VertexY(surf,vindex),VertexZ(surf,vindex)
 RotateEntity pivot1,EntityPitch(entity,True),EntityYaw(entity,True),EntityRoll(entity,True)
 
 vreal\x=EntityX(pivot2,True)+EntityX(entity,True)
 vreal\y=EntityY(pivot2,True)+EntityY(entity,True)
 vreal\z=EntityZ(pivot2,True)+EntityZ(entity,True)

 vreal\u=VertexU(surf,vindex)
 vreal\v=VertexV(surf,vindex)
 vreal\w=VertexW(surf,vindex)

 FreeEntity pivot2
 FreeEntity pivot1

 Return vreal

End Function

;#################################################################################################### UVW

Function CSGPickedUVW.CSGVectorType(t2.CSGTriangleTYPE)

  ;MR 25.01.2003

  Local ve.CSGVectorType = New CSGVectorType

  Local px#=PickedX()
  Local py#=PickedY()
  Local pz#=PickedZ() 

  Local pnx#=t2\normx
  Local pny#=t2\normy
  Local pnz#=t2\normz

  ;DebugLog "Picked XYZ "+px+" "+py+" "+pz
		
		; Select which component of xyz coordinates to ignore
		Local coords = 3

		If Abs(PNX) > Abs(PNY)
			If Abs(PNX)>Abs(PNZ) Then coords = 1
		Else
			If Abs(PNY)>Abs(PNZ) Then coords = 2
		EndIf

    ;DebugLog "coords "+coords
		
		Local a0#,a1#,b0#,b1#,c0#,c1#
		
		If (coords = 3)

 		  ;xy components

			; edge 1
			a0# = t2\v2x - t2\v1x
			a1# = t2\v2y - t2\v1y
		
			; edge 2
			b0# = t2\v3x - t2\v1x
			b1# = t2\v3y - t2\v1y

			; picked offset from triangle vertex 1
			c0# = px - t2\v1x
			c1# = py - t2\v1y
		Else		
			If (coords = 2)
				; xz components

				; edge 1
				a0# = t2\v2x - t2\v1x
				a1# = t2\v2z - t2\v1z
		
				; edge 2
				b0# = t2\v3x - t2\v1x
				b1# = t2\v3z - t2\v1z

				; picked offset from triangle vertex 1
				c0# = px - t2\v1x
				c1# = pz - t2\v1z
			Else
				; yz components

				; edge 1
				a0# = t2\v2y - t2\v1y
				a1# = t2\v2z - t2\v1z
		
				; edge 2
				b0# = t2\v3y - t2\v1y
				b1# = t2\v3z - t2\v1z

				; picked offset from triangle vertex 1
				c0# = py - t2\v1y
				c1# = pz - t2\v1z
			End If
		End If
						
		;
		; u and v are offsets from vertex 0 along edge 0 and edge 1
		; using these it is possible to calculate the Texture UVW coordinates
		; of the picked XYZ location
		;
		; a0*u + b0*v = c0
		; a1*u + b1*v = c1
		;
		; solve equation (standard equation with 2 unknown quantities)
		; check a math book to see why the following is true
		;
		Local u# = (c0*b1 - b0*c1) / (a0*b1 - b0*a1)
		Local v# = (a0*c1 - c0*a1) / (a0*b1 - b0*a1)

    ;DebugLog "U "+u+" V "+v
				
		; Calculate picked uvw's 
		ve\u = (t2\u1 + ((t2\u2 - t2\u1) * u) + ((t2\u3 - t2\u1) * v))
		ve\v = (t2\v1 + ((t2\v2 - t2\v1) * u) + ((t2\v3 - t2\v1) * v))
		ve\w = (t2\w1 + ((t2\w2 - t2\w1) * u) + ((t2\w3 - t2\w1) * v))
		
    ;DebugLog "U "+ve\u+" V "+ve\v

 Return ve
		
End Function

;###############################################################################################

Function CSGExample()

 ;MR 02.11.2003

 AppTitle "CSG Test"

 Graphics3D 640,480

 ;---------------------------------------------------------------------------------------

 Local texture=LoadTexture("ch1.bmp")
 Local brush=CreateBrush(255,255,255)

 BrushTexture brush,texture

 ;cube =CSGAddMyMeshCubeX(   0,0,0,0,20, 4,20,1,1,1,brush,brush,brush,brush,brush,brush)
 ;cube =CSGAddMyMeshCubeX(cube,0,0,0,10,10,10,1,1,1,brush,brush,brush,brush,brush,brush)

 cube = CreateCube()
 ScaleMesh cube,10,2,10
 For s=1 To CountSurfaces(cube)
  PaintSurface GetSurface(cube,s),brush
 Next 
 EntityFX cube,16

 ;TurnEntity cube,90,0,0
 ;MoveEntity cube,0,5,0

 ;---------------------------------------------------------------------------------------

 Local tex2=CreateTexture(64,64)
 SetBuffer TextureBuffer(tex2) 
 Color 128,0,0
 Rect 0,0,64,64 
 Color 255,0,0
 Rect 0,0,32,32
 Rect 32,32,32,32
 SetBuffer BackBuffer()
 Local br2=CreateBrush()
 BrushTexture br2,tex2
 ScaleTexture tex2,.25,.25

 ;cube2 = CreateSphere(8)
 ;cube2 = CreateCube()
 cube2 = CreateCylinder(16)
 
 ScaleMesh cube2,6,6,6
 For s=1 To CountSurfaces(cube2)
  PaintSurface GetSurface(cube2,s),br2
 Next 

 ;---------------------------------------------------------------------------------------

 cam = CreateCamera()
 MoveEntity cam,0,10,-20
 PointEntity cam,cube2
 CameraClsColor cam,0,0,80 
 CameraRange cam,1,2000

 ;---------------------------------------------------------------------------------------

 Local light=CreateLight()
 TurnEntity light,45,1,0
 LightRange light,50
 PositionEntity light,0,50,-50

 ;---------------------------------------------------------------------------------------

 UpdateNormals cube
 UpdateNormals cube2
 EntityAlpha cube2,.5

 ;---------------------------------------------------------------------------------------

 Local wire=0
 Local Mode=2

 Color 255,255,255
 While Not KeyHit(1) ;ESC

  If KeyHit(2) Then Mode=1 ;1
  If KeyHit(3) Then Mode=2 ;2
  If KeyHit(4) Then Mode=3 ;3

  If KeyDown(205) Then TranslateEntity cube2,.1,0,0
  If KeyDown(203) Then TranslateEntity cube2,-.1,0,0

  If KeyDown(30)  Then TranslateEntity cube2,0,0,.1
  If KeyDown(44)  Then TranslateEntity cube2,0,0,-.1 ;Y German

  If KeyDown(200) Then TranslateEntity cube2,0,.1,0
  If KeyDown(208) Then TranslateEntity cube2,0,-.1,0

  If KeyDown(37) Then ;K
   ;ClearSurface GetSurface(cube,7),True,True ;TEST
  EndIf

  If KeyDown(18) Then ;E
   TurnEntity cube,-1,0,0   
  EndIf

  If KeyDown(19) Then ;R
   TurnEntity cube,0,-1,0   
  EndIf

  If KeyDown(20) Then ;T
   TurnEntity cube,0,0,-1   
  EndIf

  If KeyHit(21) Then ;Z    German
   TurnEntity cube2,-45,0,0   
  EndIf

  If KeyHit(31) Then ;S = Screenshot
   SaveBuffer FrontBuffer(),"screenshot.bmp"
  EndIf

  If KeyHit(17) ;W
   wire = 1 - wire
   WireFrame wire
  EndIf
 
  If KeyHit(57) ;Space
   Cls
   RenderWorld
   Text 0,0,"Wait ..."
   Flip 
   e = MilliSecs()
   man = csg(cube,cube2,Mode) ;1 Add , 2 Sub , 3 Diff
   e = MilliSecs() -e
   FreeEntity cube
   cube = man
   ;PaintEntity cube,brush
   EntityFX cube,16
   FlushKeys
  EndIf

  UpdateWorld
  RenderWorld
  Text 0,0,"Time: "+e+" ms"
  Text 0,15*1,"Arrows to Move Left/Right/Up/Down"
  Text 0,15*2,"A/Z to move Forward and Back"
  Text 0,15*3,"Tris All : "+TrisRendered()
  Text 0,15*4,"Tris 1 : "+CSGCountAllTriangles(cube )+" Surfaces : " +CountSurfaces(cube)
  Text 0,15*5,"Tris 2 : "+CSGCountAllTriangles(cube2)
  Text 0,15*6,"Mode: "+Mode
  Text 0,15*7,"E,R,T Rotate Obj. 1"
  Text 0,15*8,"Y Rotate Obj. 2"
  Text 0,15*9,"S Screenshot"
  Flip
 Wend

End Function

;#################################################################################################### 

Function CSGCountAllTriangles(mesh) ;only for example

 ;without child entitys

 Local si

 If mesh=0 Then Return 0

 Local c=0

 For si=1 To CountSurfaces(mesh)
  c=c+CountTriangles(GetSurface(mesh,si))
 Next 

 Return c

End Function

;#################################################################################################### 

Function CSGAddMyMeshCubeX(m,x1#,y1#,z1#,x2#,y2#,z2#,cx,cy,cz,br_top,br_bottom,br_left,br_right,br_front,br_back,o_top=1,o_bottom=1,o_left=1,o_right=1,o_front=1,o_back=1)

 ;MR 31.10.2002

 ;Create a Cube centered

 ;m       =Entity Handle 0=Create a new one :-)
 ;x1,x2   =From X1 To X2
 ;cx,cy,cy=Center  
 ;br_     =Brush  Handle
 ;o_      =Optional 1=Create 0=No Create  
 
  Local w#,h#,d#

 ;w#=witdh  (X)
 ;h#=height (Y)
 ;d#=depth  (Z)

  w#=x2#-x1#
  h#=y2#-y1#
  d#=z2#-z1#

  ;--------------------- Center ?

  If cx=1 Then 
   x1=x1-w/2.0
   x2=x2-w/2.0
  EndIf

  If cy=1 Then 
   y1=y1-h/2.0
   y2=y2-h/2.0
  EndIf

  If cz=1 Then 
   z1=z1-d/2.0
   z2=z2-d/2.0
  EndIf

  ;-----------------------------

  If m=0 Then
	 m=CreateMesh()
  EndIf

  ;-----------------------------

	;top face
	If o_top=1 Then 
	 s=CreateSurface( m , Br_Top)
	 AddVertex s,x1,y2,z2,0,1:AddVertex s,x2,y2,z2,0,0
	 AddVertex s,x2,y2,z1,1,0:AddVertex s,x1,y2,z1,1,1
	 AddTriangle s,0,1,2:AddTriangle s,0,2,3
  EndIf

	;bottom face	
  If o_bottom=1 Then
	 s=CreateSurface( m , Br_Bottom)
	 AddVertex s,x1,y1,z1,1,0:AddVertex s,x2,y1,z1,1,1
	 AddVertex s,x2,y1,z2,0,1:AddVertex s,x1,y1,z2,0,0
	 AddTriangle s,0,1,2:AddTriangle s,0,2,3
  EndIf

	;left face
  If o_left=1 Then
	 s=CreateSurface( m , Br_Left)
	 AddVertex s,x1,y2,z2,0,0:AddVertex s,x1,y2,z1,1,0
	 AddVertex s,x1,y1,z1,1,1:AddVertex s,x1,y1,z2,0,1
	 AddTriangle s,0,1,2:AddTriangle s,0,2,3
  EndIf 

	;right face
  If o_right=1 Then
	 s=CreateSurface( m , Br_Right)
	 AddVertex s,x2,y2,z1,0,0:AddVertex s,x2,y2,z2,1,0
	 AddVertex s,x2,y1,z2,1,1:AddVertex s,x2,y1,z1,0,1
	 AddTriangle s,0,1,2:AddTriangle s,0,2,3
  EndIf

	;front face
  If o_front=1 Then
	 s=CreateSurface( m , Br_Front)
	 AddVertex s,x1,y2,z1,0,0:AddVertex s,x2,y2,z1,1,0
	 AddVertex s,x2,y1,z1,1,1:AddVertex s,x1,y1,z1,0,1
	 AddTriangle s,0,1,2:AddTriangle s,0,2,3
  EndIf

	;back face
  If o_back=1 Then
	 s=CreateSurface( m , Br_Back)
	 AddVertex s,x2,y2,z2,0,0:AddVertex s,x1,y2,z2,1,0
	 AddVertex s,x1,y1,z2,1,1:AddVertex s,x2,y1,z2,0,1
	 AddTriangle s,0,1,2:AddTriangle s,0,2,3
  EndIf

  UpdateNormals m
	  
  ;EntityPickMode m,2 ;Poly

  Return m  

End Function

;####################################################################################################
