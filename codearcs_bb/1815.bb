; ID: 1815
; Author: markcw
; Date: 2006-09-15 23:20:24
; Title: SaveMesh3DS function
; Description: Saves a mesh to 3DS file format.

;Save Mesh 3DS, on 26/9/06

Graphics3D 640,480,0,2
AppTitle "Save Mesh 3DS"
SetBuffer BackBuffer()

camera=CreateCamera() ;init world
light=CreateLight()
RotateEntity light,45,45,0

tex=MakeTestTexture(128,128) ;new texture

shape=0 ;type of shape to save
Select shape
 Case 0
  mesh1=CreateCube() : child1=CreateCube(mesh1)
  PositionEntity child1,1,-1,1 : RotateEntity child1,30,-30,30
 Case 1
  mesh1=CreateSphere(8) : child1=CreateSphere(8,mesh1)
  PositionEntity child1,1,1,-1
 Case 2
  mesh1=CreateCylinder(8,True) : child1=CreateCylinder(8,True,mesh1)
  PositionEntity child1,1,1,1 : RotateEntity child1,30,30,-30
 Case 3
  mesh1=CreateCone(8,True) : child1=CreateCone(8,True,mesh1)
  PositionEntity child1,1,-1,-1
 Case 4
  mesh1=LoadMesh("yourname.x")
 Case 5
  mesh1=LoadAnimMesh("yourname.x")
End Select
EntityTexture mesh1,tex ;texture
If child1 Then EntityTexture child1,tex

filename$="test.3ds" ;save to 3ds file
;texfile$="test.bmp" ;optional texture filename
ok=SaveMesh3DS(mesh1,filename$,texfile$)

mesh2=LoadMesh(filename$) ;load new 3ds
If Len(texfile$)=0 Then EntityTexture mesh2,tex ;texture

PositionEntity mesh1,-2,0,5 ;position to left
PositionEntity mesh2,2,0,5 ;position to right

While Not KeyHit(1)
 RenderWorld()

 TurnEntity mesh1,0.3,0.2,0.1
 TurnEntity mesh2,0.3,0.2,0.1

 If KeyHit(17) Then wf=Not wf : WireFrame wf ;W key

 Text 0,0,"ok="+ok+" filename$="+filename$

 Flip
Wend

Function SaveMesh3DS(mesh,filename$,texfile$="",diffuse=$CCCCCC)
 ;Saves a given mesh, or mesh hierarchy, as a 3DS file
 ;From "3dsinfo.txt" by Jochen Wilhelmy
 ;mesh=mesh/mesh hierarchy handle, filename$="name.3ds"
 ;texfile$=optional texture file, diffuse=optional diffuse color

 Local file,piv,matname$,mnlen,tflen,ent,surf,si,mi,objname$,onlen
 Local tssize,tmsize,tfsize,tosize,tcsize,tvsize,otsize
 Local eobjsize,ematsize,editsize,i

 file=WriteFile(filename$)
 If Not file Return False ;fail code
 piv=CreatePivot() ;pivot to tform vertices

 ;calculate chunk sizes and file size
 matname$="Material"
 mnlen=Len(matname$)+1
 tflen=Len(texfile$)+1
 ent=mesh
 While ent
  For si=1 To CountSurfaces(ent)
   surf=GetSurface(ent,si)
   mi=mi+1 : If mi<10 Then objname$="mesh0"+mi Else objname$="mesh"+mi
   onlen=Len(objname$)+1
   tssize=6+(CountTriangles(surf)*4) ;sizeof(Tri_Smooth)
   tmsize=8+mnlen+(CountTriangles(surf)*2) ;sizeof(Tri_Material)
   tfsize=8+(CountTriangles(surf)*4*2)+tmsize+tssize ;sizeof(Tri_FaceList)
   tosize=8+(CountVertices(surf)*2) ;sizeof(Tri_VertexOptions)
   tcsize=8+(CountVertices(surf)*2*4) ;sizeof(Tri_MappingCoords)
   tvsize=8+(CountVertices(surf)*3*4) ;sizeof(Tri_VertexList)
   otsize=6+tvsize+tcsize+tosize+54+tfsize ;sizeof(Object_TriMesh)
   eobjsize=eobjsize+(6+onlen+otsize) ;sizeof(All_Edit_Objects)
  Next
  ent=NextChild(ent)
 Wend
 ematsize=6+(6+mnlen)+(15*3)+(14*6)+10 ;sizeof(Edit_Material)
 If tflen>1 Then ematsize=ematsize+(6+(6+tflen)) ;Mat_TextureMap1
 editsize=6+10+ematsize+10+eobjsize ;sizeof(Edit3DS)

 ;Main3DS Chunk
 WriteShort file,$4D4D ;wChunkID, Main3DS
 WriteInt file,6+10+editsize ;dwChunkSize, filesize
 ;Version3DS Chunk
 WriteShort file,$0002 ;wChunkID, Version3DS
 WriteInt file,10 ;dwChunkSize, sizeof(Version3DS)
 WriteInt file,3 ;ChunkData, dwVersion3DS

 ;Edit3DS Chunk, Main3DS Subchunk
 WriteShort file,$3D3D ;wChunkID, Edit3DS
 WriteInt file,editsize ;dwChunkSize, editsize=6+ematsize+eobjsize
 ;Edit_MeshVersion, Edit3DS Subchunk
 WriteShort file,$3D3E ;wChunkID, Edit_MeshVersion
 WriteInt file,10 ;dwChunkSize, sizeof(Edit_MeshVersion)
 WriteInt file,3 ;ChunkData, dwMeshVersion

 ;Edit_Material Chunk, Edit3DS Subchunk
 WriteShort file,$AFFF ;wChunkID, Edit_Material
 WriteInt file,ematsize ;dwChunkSize, ematsize=6+(6+mnlen)+(15*3)
 ;Mat_Name01 Chunk, Edit_Material Subchunk
 WriteShort file,$A000 ;wChunkID, Mat_Name01
 WriteInt file,6+mnlen ;dwChunkSize, sizeof(Mat_Name01)
 WriteStringAscii(file,matname$) ;ChunkData, material name
 WriteByte file,0 ;ChunkData, Ascii-z null byte
 ;Mat_Ambient Chunk, Edit_Material Subchunk
 WriteShort file,$A010 ;wChunkID, Mat_Ambient
 WriteInt file,15 ;dwChunkSize, sizeof(Mat_Ambient)
 WriteShort file,$0011 ;wChunkID, bRGB
 WriteInt file,9 ;dwChunkSize, sizeof(bRGB)
 WriteRGBColor(file,$666666) ;ChunkData, ambient color
 ;Mat_Diffuse Chunk, Edit_Material Subchunk
 WriteShort file,$A020 ;wChunkID, Mat_Diffuse
 WriteInt file,15 ;dwChunkSize, sizeof(Mat_Diffuse)
 WriteShort file,$0011 ;wChunkID, bRGB
 WriteInt file,9 ;dwChunkSize, sizeof(bRGB)
 WriteRGBColor(file,diffuse) ;ChunkData, diffuse color
 ;Mat_Specular Chunk, Edit_Material Subchunk
 WriteShort file,$A030 ;wChunkID, Mat_Specular
 WriteInt file,15 ;dwChunkSize, sizeof(Mat_Specular)
 WriteShort file,$0011 ;wChunkID, bRGB
 WriteInt file,9 ;dwChunkSize, sizeof(bRGB)
 WriteRGBColor(file,$FFFFFF) ;ChunkData, specular color
 ;Mat_Shininess Chunk, Edit_Material Subchunk
 WriteShort file,$A040 ;wChunkID, Mat_Shininess
 WriteInt file,14 ;dwChunkSize, sizeof(Mat_Shininess)
 WriteShort file,$0030 ;wChunkID, wPercent
 WriteInt file,8 ;dwChunkSize, sizeof(wPercent)
 WriteShort file,75 ;ChunkData, Shininess
 ;Mat_ShininessStrength Chunk, Edit_Material Subchunk
 WriteShort file,$A041 ;wChunkID, Mat_ShininessStrength
 WriteInt file,14 ;dwChunkSize, sizeof(Mat_ShininessStrength)
 WriteShort file,$0030 ;wChunkID, wPercent
 WriteInt file,8 ;dwChunkSize, sizeof(wPercent)
 WriteShort file,20 ;ChunkData, ShininessStrength
 ;Mat_Transparency Chunk, Edit_Material Subchunk
 WriteShort file,$A050 ;wChunkID, Mat_Transparency
 WriteInt file,14 ;dwChunkSize, sizeof(Mat_Transparency)
 WriteShort file,$0030 ;wChunkID, wPercent
 WriteInt file,8 ;dwChunkSize, sizeof(wPercent)
 WriteShort file,0 ;ChunkData, Transparency
 ;Mat_TransparencyFalloff Chunk, Edit_Material Subchunk
 WriteShort file,$A052 ;wChunkID, Mat_TransparencyFalloff
 WriteInt file,14 ;dwChunkSize, sizeof(Mat_TransparencyFalloff)
 WriteShort file,$0030 ;wChunkID, wPercent
 WriteInt file,8 ;dwChunkSize, sizeof(wPercent)
 WriteShort file,0 ;ChunkData, TransparencyFalloff
 ;Mat_ReflectionBlur Chunk, Edit_Material Subchunk
 WriteShort file,$A053 ;wChunkID, Mat_ReflectionBlur
 WriteInt file,14 ;dwChunkSize, sizeof(Mat_ReflectionBlur)
 WriteShort file,$0030 ;wChunkID, wPercent
 WriteInt file,8 ;dwChunkSize, sizeof(wPercent)
 WriteShort file,0 ;ChunkData, ReflectionBlur
 ;Mat_SelfIllumination Chunk, Edit_Material Subchunk
 WriteShort file,$A084 ;wChunkID, Mat_SelfIllumination
 WriteInt file,14 ;dwChunkSize, sizeof(Mat_SelfIllumination)
 WriteShort file,$0030 ;wChunkID, wPercent
 WriteInt file,8 ;dwChunkSize, sizeof(wPercent)
 WriteShort file,0 ;ChunkData, SelfIllumination
 ;Edit_WireThickness Chunk, Edit_Material Subchunk
 WriteShort file,$A087 ;wChunkID, Edit_WireThickness
 WriteInt file,10 ;dwChunkSize, sizeof(Edit_WireThickness)
 WriteFloat file,1 ;fWireThickness
 ;Mat_TextureMap1 Chunk, Edit_Material Subchunk
 If tflen>1
  WriteShort file,$A200 ;wChunkID, Mat_TextureMap1
  WriteInt file,6+(6+tflen) ;dwChunkSize, sizeof(Mat_TextureMap1)
  ;Mat_TextureFilename, Mat_TextureMap1 Subchunk
  WriteShort file,$A300 ;wChunkID, Mat_TextureFilename
  WriteInt file,6+tflen ;dwChunkSize, sizeof(Mat_TextureFilename)
  WriteStringAscii(file,texfile$) ;ChunkData, texture filename
  WriteByte file,0 ;ChunkData, Ascii-z null byte
 EndIf

 ;Edit_OneUnit Chunk, Edit3DS Subchunk
 WriteShort file,$0100 ;wChunkID, Edit_OneUnit
 WriteInt file,10 ;dwChunkSize, sizeof(Edit_OneUnit)
 WriteFloat file,1 ;fOneUnit

 ;calculate each mesh object chunk sizes
 ent=mesh : mi=0
 While ent
  For si=1 To CountSurfaces(ent)
   surf=GetSurface(ent,si)
   mi=mi+1 : If mi<10 Then objname$="mesh0"+mi Else objname$="mesh"+mi
   onlen=Len(objname$)+1
   tssize=6+(CountTriangles(surf)*4) ;sizeof(Tri_Smooth)
   tmsize=8+mnlen+(CountTriangles(surf)*2) ;sizeof(Tri_Material)
   tfsize=8+(CountTriangles(surf)*4*2)+tmsize+tssize ;sizeof(Tri_FaceList)
   tosize=8+(CountVertices(surf)*2) ;sizeof(Tri_VertexOptions)
   tcsize=8+(CountVertices(surf)*2*4) ;sizeof(Tri_MappingCoords)
   tvsize=8+(CountVertices(surf)*3*4) ;sizeof(Tri_VertexList)
   otsize=6+tvsize+tcsize+tosize+54+tfsize ;sizeof(Object_TriMesh)
   eobjsize=6+onlen+otsize ;sizeof(Edit_Object)
   ;position/rotate pivot to tform surface vertices
   PositionEntity piv,EntityX(ent,1),EntityY(ent,1),EntityZ(ent,1),1
   RotateEntity piv,EntityPitch(ent,1),EntityYaw(ent,1),EntityRoll(ent,1),1

   ;Edit_Object Chunk, Edit3DS Subchunk
   WriteShort file,$4000 ;wChunkID, Edit_Object
   WriteInt file,eobjsize ;dwChunkSize, eobjsize=6+onlen+otsize
   WriteStringAscii(file,objname$) ;ChunkData, object name
   WriteByte file,0 ;ChunkData, Ascii-z null byte

   ;Object_TriMesh Chunk, Edit_Object Subchunk
   WriteShort file,$4100 ;wChunkID, Object_TriMesh
   WriteInt file,otsize ;dwChunkSize, otsize=6+tvsize+tcsize+54+tfsize
   ;Tri_VertexList Chunk, Object_TriMesh Subchunk
   WriteShort file,$4110 ;wChunkID, Tri_VertexList
   WriteInt file,tvsize ;dwChunkSize, tvsize=8+(nVerts*3*4)
   WriteShort file,CountVertices(surf) ;ChunkData, wVerticesTotal
   For i=0 To CountVertices(surf)-1 ;switch y and z axis
    TFormPoint VertexX(surf,i),VertexY(surf,i),VertexZ(surf,i),piv,0
    WriteFloat file,TFormedX() ;fVertexX
    WriteFloat file,TFormedZ() ;fVertexY
    WriteFloat file,TFormedY() ;fVertexZ
   Next
   ;Tri_MappingCoords Chunk, Object_TriMesh Subchunk
   WriteShort file,$4140 ;wChunkID, Tri_MappingCoords
   WriteInt file,tcsize ;dwChunkSize, tcsize=8+(nVerts*2*4)
   WriteShort file,CountVertices(surf) ;ChunkData, wVerticesTotal
   For i=0 To CountVertices(surf)-1 ;invert v coord
    WriteFloat file,VertexU(surf,i) ;fVertexU
    WriteFloat file,-VertexV(surf,i) ;fVertexV
   Next
   ;Tri_VertexOptions Chunk, Object_TriMesh Subchunk
   WriteShort file,$4111 ;wChunkID, Tri_VertexOptions
   WriteInt file,tosize ;dwChunkSize, tosize=8+(nVerts*2)
   WriteShort file,CountVertices(surf) ;ChunkData, wVerticesTotal
   For i=0 To CountVertices(surf)-1 ;invert v coord
    WriteShort file,$0700 ;AllVertexOptions
   Next
   ;Tri_Local Chunk, Object_TriMesh Subchunk
   WriteShort file,$4160 ;wChunkID, Tri_Local
   WriteInt file,54 ;dwChunkSize, sizeof(Tri_Local)
   For i=0 To 3 ;X,Y,Z,Origin
    If i=0 Then WriteFloat file,1 Else WriteFloat file,0 ;fLocal1
    If i=1 Then WriteFloat file,1 Else WriteFloat file,0 ;fLocal2
    If i=2 Then WriteFloat file,1 Else WriteFloat file,0 ;fLocal3
   Next
   ;Tri_FaceList Chunk, Object_TriMesh Subchunk
   WriteShort file,$4120 ;wChunkID, Tri_FaceList
   WriteInt file,tfsize ;dwChunkSize, tfsize=8+(nTris*4*2)+tmsize+tssize
   WriteShort file,CountTriangles(surf) ;ChunkData, wTrianglesTotal
   For i=0 To CountTriangles(surf)-1 ;invert face order
    WriteShort file,TriangleVertex(surf,i,2) ;wTriangleVertexA
    WriteShort file,TriangleVertex(surf,i,1) ;wTriangleVertexB
    WriteShort file,TriangleVertex(surf,i,0) ;wTriangleVertexC
    WriteShort file,$0407 ;wFaceFlags
   Next
   ;Tri_Material Chunk, Tri_FaceList Subchunk
   WriteShort file,$4130 ;wChunkID, Tri_Material
   WriteInt file,tmsize ;dwChunkSize, tmsize=8+mnlen+(nTris*2)
   WriteStringAscii(file,matname$) ;ChunkData, material name
   WriteByte file,0 ;ChunkData, Ascii-z null byte
   WriteShort file,CountTriangles(surf) ;ChunkData, wTrianglesAssigned
   For i=0 To CountTriangles(surf)-1
    WriteShort file,i ;wTriangleIndex
   Next
   ;Tri_Smooth Chunk, Tri_FaceList Subchunk
   WriteShort file,$4150 ;wChunkID, Tri_Smooth
   WriteInt file,tssize ;dwChunkSize, tssize=6+(nTris*4)
   For i=0 To CountTriangles(surf)-1
    WriteInt file,1 ;dwTriangleSmoothGroup
   Next

  Next
  ent=NextChild(ent)
 Wend

 FreeEntity piv
 CloseFile file
 Return True ;success code

End Function

Function WriteStringAscii(file,ascii$)
 ;file=file handle, ascii$=ascii string

 Local i,char$
 For i=1 To Len(ascii$)
  char$=Mid(ascii$,i,1)
  WriteByte(file,Asc(char$))
 Next

End Function

Function WriteRGBColor(file,rgb)
 ;file=file handle, rgb=3-byte value

 WriteByte(file,(rgb And $FF0000) Shr 16) ;r
 WriteByte(file,(rgb And $00FF00) Shr 8) ;g
 WriteByte(file,(rgb And $0000FF)) ;b

End Function

Function NextChild(ent)
 ;Returns next child of entity as if it was on the same hierarchy level
 ;"NextChild(entity)", by Beaker

 If CountChildren(ent)>0
  Return GetChild(ent,1)
 EndIf
 Local foundunused=False
 Local foundent=0,parent,sibling
 While foundunused=False And ent<>0
  parent=GetParent(ent)
  If parent<>0
   If CountChildren(parent)>1
    If GetChild(parent,CountChildren(parent))<>ent
     For siblingcnt=1 To CountChildren(parent)
      sibling=GetChild(parent,siblingcnt)
      If sibling=ent
       foundunused=True
       foundent=GetChild(parent,siblingcnt+1)
      EndIf
     Next
    EndIf
   EndIf
  EndIf
  ent=parent
 Wend
 Return foundent

End Function

Function MakeTestTexture(width,height)

 Local texture,x,y,rgb
 texture=CreateTexture(width,height)
 LockBuffer(TextureBuffer(texture))
 For y=0 To TextureHeight(texture)-1
  For x=0 To TextureWidth(texture)-1
   rgb=y+(y*256)+(x*256^2) ;gradient color
   WritePixelFast x,y,rgb,TextureBuffer(texture)
  Next
 Next
 UnlockBuffer(TextureBuffer(texture))
 SetBuffer TextureBuffer(texture)
 Color 255,255,255 : Oval 40,40,30,30
 Color 0,0,0 : Text 50,50,"3DS" : Color 255,255,255
 SetBuffer BackBuffer()
 Return texture

End Function
