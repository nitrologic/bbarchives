; ID: 1244
; Author: jfk EO-11110
; Date: 2004-12-23 12:22:37
; Title: ClusterizeMesh
; Description: Divide a huge Mesh into a 3D Grid of clusters (Updated)

;The following Code will take one huge Mesh with multiple surfaces and split it up
;into several space clusters. This method will give you more surfaces to render, but it
;allows the camera to skip things that are part of the initial mesh and may be 
;out of the camera range or view angle. Especially useful with huge Meshes and a small camera
;range. 
;When you run this example, TrisRendered will be less after Clusterizing the Mesh because
;some Clusters will be out of the camera FOV after Clusterisation.

;Written by jfk (Executive Order 11110) of CSP games

; Updated July 2005: added support for VertexAlpha and VertexColor, fixed a fundamental Bug
; (in the prev. verison i accidentally omited the W (from UVW) parameter prior the TextureSet vector flag)
; This version is now fully tested with a lightmapped gile[s] exported Map and shoud support 
; most features of a B3D map.
; It will now also determine the maps total size and automaticly clusterize the required space.
; all you have to do is to define the cluster size in blitz units (clustersize#).

Graphics3D 640,480,32,2
SetBuffer BackBuffer()


; some Gobals that are required:
Global vis_minx#,vis_miny#,vis_minz#,vis_maxx#,vis_maxy#,vis_maxz#
; wanted clustersize in Blitz Untis: Reccomended: about 10% to 30% of the full width or lenght:
Global clustersize#=10.0
Global n_xvis#,n_yvis#,n_zvis#
Global world_xvis#,world_yvis#,world_zvis#



camera=CreateCamera()
CameraRange camera,1,150
TranslateEntity camera,0,22,-50

; this mesh will be clusterized:
mesh=LoadMesh("building\crypto3slim9test2k.b3d")
;ScaleMesh mesh,10,10,10 ; scalemesh works, scaleentity not yet

;----------------- show single mesh
RenderWorld()
Color 255,255,255
Text 0,0,"Tris Rendered: "+TrisRendered()
Text 0,16,"Will now clusterize the mesh, press a key"
Flip
WaitKey()


;---------------- Init Clusterisation
find_vis_minmax(mesh) ; find max world space
world_xvis=(max(Abs(vis_maxx),Abs(vis_minx))*2.0)+clustersize#
world_yvis=(max(Abs(vis_maxy),Abs(vis_miny))*2.0)+clustersize#
world_zvis=(max(Abs(vis_maxz),Abs(vis_minz))*2.0)+clustersize#
; calc number of clusters
n_xvis=Floor(max(2,world_xvis/clustersize#))
n_yvis=Floor(max(2,world_yvis/clustersize#))
n_zvis=Floor(max(2,world_zvis/clustersize#))
Dim cluster      (n_xvis,n_yvis,n_zvis)
Dim cluster_mflag(n_xvis,n_yvis,n_zvis)
Dim cluster_sflag(n_xvis,n_yvis,n_zvis)

;Finally make clusters
ClusterizeMesh(mesh,n_xvis,n_yvis,n_zvis,world_xvis,world_yvis,world_zvis)


; a little flytrough code, so you can test it
While KeyDown(1)=0
 mxs#=-MouseXSpeed()/4.0
 mys#=MouseYSpeed()/4.0
 mxsa#=mxsa#+mxs#
 mysa#=mysa#+mys#
 If mxsa#<0 Then mxsa#=mxsa#+360.0
 If mxsa#>360.0 Then mxsa#=mxsa#-360.0
 If mysa#<0 Then mysa#=mysa#+360.0
 If mysa#>360.0 Then mysa#=mysa#-360.0
 MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
 RotateEntity camera,mysa#,mxsa#,0
 If KeyDown(200) Then MoveEntity camera,0,0,1
 If KeyDown(208) Then MoveEntity camera,0,0,-1
 If KeyDown(205) Then MoveEntity camera,1,0,0
 If KeyDown(203) Then MoveEntity camera,-1,0,0
 RenderWorld()
 Text 0,0,"Tris Rendered: "+TrisRendered()
 Text 0,16,"Clusterisation complete"
 Text 0,32,""+Int(n_xvis+1)+" * "+Int(n_yvis+1)+" * "+Int(n_zvis+1)+" Clusters"
 Flip
Wend


End




; cx, cy and cz is the dimension of clusterisation, sx, sy and sz is the size of the scene,
; (the function expects the scene to fit inside -(sx/2) and +(sx/2) etc.

Function ClusterizeMesh(mesh,cx#,cy#,cz#,sx#,sy#,sz#)
 Local surf,s,mybrush,x,y,z,t,x_c#,y_c#,z_c#,v0,v1,v2,nope
 Local x0#,y0#,z0#,u0a#,v0a#,u0b#,v0b#,nx0#,ny0#,nz0#,v0_r#,v0_g#,v0_b#,v0_a#
 Local x1#,y1#,z1#,u1a#,v1a#,u1b#,v1b#,nx1#,ny1#,nz1#,v1_r#,v1_g#,v1_b#,v1_a#
 Local x2#,y2#,z2#,u2a#,v2a#,u2b#,v2b#,nx2#,ny2#,nz2#,v2_r#,v2_g#,v2_b#,v2_a#
 For s=1 To CountSurfaces(mesh)
  surf=GetSurface(mesh,s)
  mybrush=GetSurfaceBrush(surf)
  For z=0 To cz ; clear all clusters current surface holder variable
   For y=0 To cy
    For x=0 To cx
     cluster_sflag(x,y,z)=0
    Next
   Next
  Next
  ;probably need to TFormPoint if you want to use ScaleEntity etc.
  For t=0 To CountTriangles(surf)-1 ; store all neccessary triangle data
   x0#=VertexX(surf,TriangleVertex(surf,t,0))
   y0#=VertexY(surf,TriangleVertex(surf,t,0))
   z0#=VertexZ(surf,TriangleVertex(surf,t,0))
   u0a#=VertexU(surf,TriangleVertex(surf,t,0),0)
   v0a#=VertexV(surf,TriangleVertex(surf,t,0),0)
   u0b#=VertexU(surf,TriangleVertex(surf,t,0),1)
   v0b#=VertexV(surf,TriangleVertex(surf,t,0),1)
   nx0#=VertexNX(surf,TriangleVertex(surf,t,0))
   ny0#=VertexNY(surf,TriangleVertex(surf,t,0))
   nz0#=VertexNZ(surf,TriangleVertex(surf,t,0))
   v0_r#=VertexRed(surf,TriangleVertex(surf,t,0))
   v0_g#=VertexGreen(surf,TriangleVertex(surf,t,0))
   v0_b#=VertexBlue(surf,TriangleVertex(surf,t,0))
   v0_a#=VertexAlpha(surf,TriangleVertex(surf,t,0))


   x1#=VertexX(surf,TriangleVertex(surf,t,1))
   y1#=VertexY(surf,TriangleVertex(surf,t,1))
   z1#=VertexZ(surf,TriangleVertex(surf,t,1))
   u1a#=VertexU(surf,TriangleVertex(surf,t,1),0)
   v1a#=VertexV(surf,TriangleVertex(surf,t,1),0)
   u1b#=VertexU(surf,TriangleVertex(surf,t,1),1)
   v1b#=VertexV(surf,TriangleVertex(surf,t,1),1)
   nx1#=VertexNX(surf,TriangleVertex(surf,t,1))
   ny1#=VertexNY(surf,TriangleVertex(surf,t,1))
   nz1#=VertexNZ(surf,TriangleVertex(surf,t,1))
   v1_r#=VertexRed(surf,TriangleVertex(surf,t,1))
   v1_g#=VertexGreen(surf,TriangleVertex(surf,t,1))
   v1_b#=VertexBlue(surf,TriangleVertex(surf,t,1))
   v1_a#=VertexAlpha(surf,TriangleVertex(surf,t,1))

   x2#=VertexX(surf,TriangleVertex(surf,t,2))
   y2#=VertexY(surf,TriangleVertex(surf,t,2))
   z2#=VertexZ(surf,TriangleVertex(surf,t,2))
   u2a#=VertexU(surf,TriangleVertex(surf,t,2),0)
   v2a#=VertexV(surf,TriangleVertex(surf,t,2),0)
   u2b#=VertexU(surf,TriangleVertex(surf,t,2),1)
   v2b#=VertexV(surf,TriangleVertex(surf,t,2),1)
   nx2#=VertexNX(surf,TriangleVertex(surf,t,2))
   ny2#=VertexNY(surf,TriangleVertex(surf,t,2))
   nz2#=VertexNZ(surf,TriangleVertex(surf,t,2))
   v2_r#=VertexRed(surf,TriangleVertex(surf,t,2))
   v2_g#=VertexGreen(surf,TriangleVertex(surf,t,2))
   v2_b#=VertexBlue(surf,TriangleVertex(surf,t,2))
   v2_a#=VertexAlpha(surf,TriangleVertex(surf,t,2))

   ; find corresponding space cluster (checking Vertex 0 only )
   x_c#=(VertexX(surf,TriangleVertex(surf,t,0))+(sx/2.0))/(sx/cx)
   y_c#=(VertexY(surf,TriangleVertex(surf,t,0))+(sy/2.0))/(sy/cy)
   z_c#=(VertexZ(surf,TriangleVertex(surf,t,0))+(sz/2.0))/(sz/cz)

   ; create a cluster mesh if it's used the first time
   If cluster_mflag(x_c,y_c,z_c)=0 
    cluster_mflag(x_c,y_c,z_c)=1   ; and set it's flag to 1 (= already created)
    cluster(x_c,y_c,z_c)=CreateMesh()
   EndIf
   ; create a cluster surface if it's used the first time (used as a flag too: <>0 = already existing)
   If cluster_sflag(x_c,y_c,z_c)=0
    cluster_sflag(x_c,y_c,z_c)=CreateSurface(cluster(x_c,y_c,z_c))
    PaintSurface cluster_sflag(x_c,y_c,z_c),mybrush
   EndIf

   ; and finally recreate the triangle for the cluster
   v0=AddVertex(cluster_sflag(x_c,y_c,z_c),x0,y0,z0)
   VertexTexCoords cluster_sflag(x_c,y_c,z_c),v0,u0a,v0a,0,0
   VertexTexCoords cluster_sflag(x_c,y_c,z_c),v0,u0b,v0b,0,1
   VertexColor cluster_sflag(x_c,y_c,z_c),v0,v0_r,v0_g,v0_b,v0_a
   VertexNormal cluster_sflag(x_c,y_c,z_c),v0,nx0,ny0,nz0

   v1=AddVertex(cluster_sflag(x_c,y_c,z_c),x1,y1,z1)
   VertexTexCoords cluster_sflag(x_c,y_c,z_c),v1,u1a,v1a,0,0
   VertexTexCoords cluster_sflag(x_c,y_c,z_c),v1,u1b,v1b,0,1
   VertexColor cluster_sflag(x_c,y_c,z_c),v1,v1_r,v1_g,v1_b,v1_a
   VertexNormal cluster_sflag(x_c,y_c,z_c),v1,nx1,ny1,nz1

   v2=AddVertex(cluster_sflag(x_c,y_c,z_c),x2,y2,z2)
   VertexTexCoords cluster_sflag(x_c,y_c,z_c),v2,u2a,v2a,0,0
   VertexTexCoords cluster_sflag(x_c,y_c,z_c),v2,u2b,v2b,0,1
   VertexColor cluster_sflag(x_c,y_c,z_c),v2,v2_r,v2_g,v2_b,v2_a
   VertexNormal cluster_sflag(x_c,y_c,z_c),v2,nx2,ny2,nz2

   nope=AddTriangle(cluster_sflag(x_c,y_c,z_c),v0,v1,v2)

  Next
 Next

 For z=0 To cz
  For y=0 To cy
   For x=0 To cx
    If cluster_mflag(x,y,z)<>0 ; does cluster contain a mesh at all?
     ; here you can also set the required attributes, like collision, FX etc.
     ; EG:
     ; EntityFX cluster(x,y,z),1
    EndIf
   Next
  Next
 Next
 FreeEntity mesh ; orginal mesh not used anymore
End Function



Function find_vis_minmax(lmesh) 
 ; this will find the min and max xyz of the mesh. This space will be clusterized.
 Local lmin_x#,lmin_y#,lmin_z#,lmax_x#,lmax_y#,lmax_z#,s,i,v,i2,lx#,ly#,lz#
 lmin_x#=1000000
 lmin_y#=1000000
 lmin_z#=1000000
 lmax_x#=-1000000
 lmax_y#=-1000000
 lmax_z#=-1000000

 s=CountSurfaces(lmesh)
 For i=1 To s
  ls=GetSurface(lmesh,i)
  v=CountVertices(ls)
  For i2=0 To v-1
   lx#=VertexX(ls,i2)
   ly#=VertexY(ls,i2)
   lz#=VertexZ(ls,i2)
   If lx>lmax_x Then lmax_x=lx
   If ly>lmax_y Then lmax_y=ly
   If lz>lmax_z Then lmax_z=lz

   If lx<lmin_x Then lmin_x=lx
   If ly<lmin_y Then lmin_y=ly
   If lz<lmin_z Then lmin_z=lz
  Next
 Next
 vis_minx=lmin_x ; store results in globals!
 vis_miny=lmin_y
 vis_minz=lmin_z

 vis_maxx=lmax_x
 vis_maxy=lmax_y
 vis_maxz=lmax_z
End Function

; misc stuff...
Function min#(a#,b#)
  If a<b Then Return a
 Return b
End Function

Function max#(a#,b#)
  If a>b Then Return a
 Return b
End Function
