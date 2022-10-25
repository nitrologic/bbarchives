; ID: 950
; Author: Matty
; Date: 2004-02-28 01:32:56
; Title: Create a Grid
; Description: Create a single surface Grid

Type TileObj

Field MeshID
Field SurfaceID
Field TileWidth#
Field TileHeight#

Field UL_Vertex_Index
Field LL_Vertex_Index
Field UR_Vertex_Index
Field LR_Vertex_Index


End Type


;could have used a type but if you are only interested in using a single image for your tiled texture
;then this will do...
Global MyTexture,TotalTexWidth,TotalTexHeight,OnetileWidth,OneTileheight



Dim Grid.TileObj(30,30)
For x=0 To 30
 For z=0 To 30
Grid(x,z)=New TileObj

 Next
Next


;this function is merely an example which makes use of the functions below...
MyExampleGridRoutine()



Function CreateGrid(MeshID,SurfaceID,X,Z,Width#,Height#)
;only use grid x and z values from 1 to 30, do not use an x or z value of 0.

Grid(X,Z)\MeshID=MeshID
Grid(X,Z)\SurfaceID=SurfaceID
Grid(X,Z)\TileWidth=Width
Grid(X,Z)\TileHeight=Height

Grid(X,Z)\LL_Vertex_Index=AddVertex(SurfaceID,Float(X)*Width,0,Float(Z)*Height,0,0)
Grid(X,Z)\UL_Vertex_Index=AddVertex(SurfaceID,Float(X)*Width,0,Height+Float(Z)*Height,0,0)
Grid(X,Z)\UR_Vertex_Index=AddVertex(SurfaceID,Width+Float(X)*Width,0,Height+Float(Z)*Height,0,0)
Grid(X,Z)\LR_Vertex_Index=AddVertex(SurfaceID,Width+Float(X)*Width,0,Float(Z)*Height,0,0)


AddTriangle(SurfaceID,Grid(X,Z)\LL_Vertex_Index,Grid(X,Z)\UL_Vertex_Index,Grid(X,Z)\UR_Vertex_Index)
AddTriangle(SurfaceID,Grid(X,Z)\LL_Vertex_Index,Grid(X,Z)\UR_Vertex_Index,Grid(X,Z)\LR_Vertex_Index)


End Function

Function PaintGrid(X,Z,TileIndex)
;Tileindex is like an image strip with loadanimimage.  It takes the appropriate region from the
;tile texture and paints it on an individual tile...

;TileIndex begins at 0 and ends at 1 number less than the total number of tiles on the texture

XWidth=TotalTexWidth/OnetileWidth
YHeight=TotalTexheight/OnetileHeight

Column=TileIndex Mod XWidth
Row=1+(TileIndex-Column)/YHeight
If row>yheight Then Return -1
If Column=0 Then Column=XWidth

VertexTexCoords(Grid(X,Z)\SurfaceID,Grid(X,Z)\LL_Vertex_Index,Float(OneTileWidth*(Column-1))/Float(TotalTexWidth),Float(OneTileHeight*Row)/Float(TotalTexHeight))
VertexTexCoords(Grid(X,Z)\SurfaceID,Grid(X,Z)\UL_Vertex_Index,Float(OneTileWidth*(Column-1))/Float(TotalTexWidth),Float(OneTileHeight*(Row-1))/Float(TotalTexHeight))
VertexTexCoords(Grid(X,Z)\SurfaceID,Grid(X,Z)\UR_Vertex_Index,Float(OneTileWidth*(Column))/Float(TotalTexWidth),Float(OneTileHeight*(Row-1))/Float(TotalTexHeight))
VertexTexCoords(Grid(X,Z)\SurfaceID,Grid(X,Z)\LR_Vertex_Index,Float(OneTileWidth*Column)/Float(TotalTexWidth),Float(OneTileHeight*(Row))/Float(TotalTexHeight))


End Function

Function MyExampleGridRoutine()
Graphics3D 800,600
SetBuffer BackBuffer()
myimage=CreateImage(32,32)
SetBuffer ImageBuffer(myimage)
Color 255,255,0
Rect 0,0,16,16,1
Color 255,0,0
Rect 16,0,16,16,1
Color 0,255,0
Rect 0,16,16,16,1
Color 0,0,255
Rect 16,16,16,16,1
SetBuffer BackBuffer()


MyTexture=CreateTexture(32,32)  ;normally you would load your texture here...
SetBuffer TextureBuffer(mytexture)
DrawBlock myimage,0,0
SetBuffer BackBuffer()
FreeImage myimage
TotalTexWidth=TextureWidth(MyTexture)
TotalTexHeight=TextureHeight(MyTexture)

;Specify these yourself.  Make sure that the total width and height are divisible by these numbers
OneTileWidth=16
OneTileHeight=16
MyMesh=CreateMesh()
MySurface=CreateSurface(MyMesh)
EntityTexture MyMesh,MyTexture
For X=1 To 30
For Z=1 To 30
CreateGrid(MyMesh,MySurface,X,Z,32.0,32.0)
PaintGrid(X,Z,Rand(0,3))
Next
Next
UpdateNormals mymesh
camera=CreateCamera()
PositionEntity camera,400,50,200

Repeat

If KeyDown(203) Then TurnEntity camera,0,1,0
If KeyDown(205) Then TurnEntity camera,0,-1,0
If KeyDown(208) Then MoveEntity camera,0,0,-1
If KeyDown(200) Then MoveEntity camera,0,0,1

UpdateWorld
RenderWorld
Flip

Until KeyDown(1)
End



End Function
