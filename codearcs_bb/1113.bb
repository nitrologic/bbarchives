; ID: 1113
; Author: David819
; Date: 2004-07-20 11:10:34
; Title: Simple hair
; Description: code to create a simple hair effect

Graphics3D 640,480,32,2
SetBuffer BackBuffer()
camera=CreateCamera()
TranslateEntity camera,0,0,-3


c=CreateSphere(60) ; sphere width 60 segments for demo
EntityColor c,235,176,142

;       mesh, lenght,width
c2=hair(c,0.5,.01)

;length#=0

While Not KeyDown(1)
;TurnEntity c,.1,.1,.1
RenderWorld()
 Flip
Wend
FreeEntity c 
FreeEntity c2 
FreeEntity camera 
End

Function hair(mesh,ln#,wid#)
 s=GetSurface(mesh,1)
 m3=CreateMesh()
 For i=0 To CountVertices(s)-1
  m2=CreateMesh()
  s2=CreateSurface(m2)
  ScaleMesh m2,wid,ln,wid
  FitMesh m2,-MeshWidth(m2)/2.0,0,-MeshDepth(m2)/2.0,MeshWidth(m2),MeshHeight(m2),MeshDepth(m2)
  x#=VertexX(s,i)
  y#=VertexY(s,i)
  z#=VertexZ(s,i)
  
  nx#=VertexNX(s,i)
  ny#=VertexNY(s,i)
  nz#=VertexNZ(s,i)

  TFormPoint x,y,z,mesh,0
  x=TFormedX()
  y=TFormedY()
  z=TFormedZ()
  v0=AddVertex(s2,0,0,0)
  v1=AddVertex(s2,0,0+ln,0)
  v2=AddVertex(s2,0,0,0+wid)
  red  =(255)
  green=(255)
  blue =Rand(1,255)
  VertexColor s2,v0,red,green,blue
  VertexColor s2,v1,red,green,blue
  VertexColor s2,v2,red,green,blue
  AddTriangle(s2,v0,v1,v2)
  AddTriangle(s2,v0,v2,v1)
  UpdateNormals m2

  AlignToVector m2,nx,ny,nz,2 ; didn't find a better way yet
  mpitch#= EntityPitch(m2,1)
  myaw#=   EntityYaw(m2,1)
  mroll#=  EntityRoll(m2,1)
  RotateEntity m2,0,0,0,1
  RotateMesh m2,mpitch,myaw,mroll
  PositionMesh m2,x,y,z
  EntityParent m2,mesh
  AddMesh m2,m3
  FreeEntity m2
 Next
 UpdateNormals m3 
 EntityFX m3,16 Or 2 ; 2=use vertexcolors, 16=show both sides of triangles
 EntityParent m3,mesh

End Function
