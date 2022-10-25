; ID: 1837
; Author: markcw
; Date: 2006-10-09 14:21:55
; Title: Read mesh and write to bb data
; Description: For packing meshes in executable

;Read Mesh Write Data, on 8/10/06

Graphics3D 640,480,0,2
SetBuffer BackBuffer()

camera=CreateCamera() ;init world
PositionEntity camera,0,0,-5
light=CreateLight()
RotateEntity light,45,45,0

fileout$="temp.bb"
filein$="yourname.x" ;mesh in

meshtype=0 ;set mesh type
Select meshtype
 Case 1
  mesh=LoadAnimMesh(filein$)
 Default
  mesh=LoadMesh(filein$)
End Select

ok=ReadMeshWriteData(mesh,filein$,fileout$,1,0)

;Main loop
While Not KeyHit(1) Or KeyHit(57) ;esc or space keys
 RenderWorld

 TurnEntity mesh,0.3,0.2,0.1

 If KeyHit(17) wf=Not wf : WireFrame wf ;W key

 Text 0,0,"ok="+ok+" filein$="+filein$+" fileout$="+fileout$

 Flip
Wend

Function ReadMeshWriteData(mesh,filein$,fileout$,onelabel,onetriuvset)
 ;Saves a mesh, or mesh hierarchy, as a bb data file
 ;mesh=mesh handle, filein$="mesh.x/3ds/b3d", fileout$="name.bb"
 ;onelabel=one data label per mesh, 0=all/1=one
 ;onetriuvset=one triangle/uv set per mesh, 0=all/1=one/2=none

 Local file,child,surf,nmeshes,dotlen,si,mi,vi,vx#,vy#,vz#

 ;Write fileout.bb
 If fileout$="" Then Return False ;fail code
 file=WriteFile(fileout$)
 If Not file Then Return False ;write fail

 ;write title comment
 WriteStringAscii(file,";"+fileout$)
 WriteByte file,13 : WriteByte file,10 ;newline

 ;calculate number of meshes
 child=mesh
 While child
  For si=1 To CountSurfaces(child) : nmeshes=nmeshes+1 : Next
  child=NextChild(child)
 Wend
 If onelabel=0 Then nmeshes=1

 ;write mesh/each child
 child=mesh
 While child
  For si=1 To CountSurfaces(child)
   surf=GetSurface(child,si) ;next surface
   mi=mi+1 ;mesh index
   dotlen=Instr(filein$,".",1)

   ;write data label
   If onelabel=0 Or mi=1
    WriteByte file,13 : WriteByte file,10 ;newline
    WriteStringAscii(file,"."+Left(filein$,dotlen-1))
    If onelabel=0 Then WriteStringAscii(file,"_mesh"+mi)
    WriteStringAscii(file,"_"+Mid(filein$,dotlen+1,Len(filein$)-dotlen))
   EndIf

   ;write mesh data
   WriteByte file,13 : WriteByte file,10 ;newline
   WriteStringAscii(file,"Data ")
   WriteStringAscii(file,nmeshes) ;1st value is nMeshes
   WriteStringAscii(file,","+CountVertices(surf)) ;2nd value is nVerts
   WriteStringAscii(file,","+CountTriangles(surf)) ;3rd value is nTris
   WriteStringAscii(file,","+onetriuvset) ;4th value is onetriuvset
   WriteStringAscii(file,","+EntityX(child,1)) ;5th value is x position
   WriteStringAscii(file,","+EntityY(child,1)) ;6th value is y position
   WriteStringAscii(file,","+EntityZ(child,1)) ;7th value is z position
   WriteStringAscii(file,","+EntityPitch(child,1)) ;8th value is pitch
   WriteStringAscii(file,","+EntityYaw(child,1)) ;9th value is yaw
   WriteStringAscii(file,","+EntityRoll(child,1)) ;10th value is roll
   For vi=0 To CountVertices(surf)-1 ;vertices, nVerts*3
    vx#=VertexX(surf,vi) : If Abs(vx#)<0.001 Then vx#=0 ;prevent errors
    vy#=VertexY(surf,vi) : If Abs(vy#)<0.001 Then vy#=0
    vz#=VertexZ(surf,vi) : If Abs(vz#)<0.001 Then vz#=0
    WriteStringAscii(file,","+vx#)
    WriteStringAscii(file,","+vy#)
    WriteStringAscii(file,","+vz#)
   Next
   If onetriuvset<2 And (onetriuvset=0 Or mi=1)
    For vi=0 To CountVertices(surf)-1 ;uv coords, nVerts*2
     WriteStringAscii(file,","+VertexU(surf,vi,0))
     WriteStringAscii(file,","+VertexV(surf,vi,0))
    Next
    For vi=0 To CountTriangles(surf)-1 ;triangles, nTris*3
     WriteStringAscii(file,","+TriangleVertex(surf,vi,0))
     WriteStringAscii(file,","+TriangleVertex(surf,vi,1))
     WriteStringAscii(file,","+TriangleVertex(surf,vi,2))
    Next
   EndIf

  Next
  child=NextChild(child)
 Wend

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
