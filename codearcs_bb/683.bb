; ID: 683
; Author: Markus Rauch
; Date: 2003-05-10 12:28:40
; Title: QuadMesh and MeshTerrain
; Description: QuadMesh and MeshTerrain

Const WaterHeight=550
Global TxTerrainHMap
Global TerrainMaxHeight#
Global Land=MyTerrain()

;#######################################################################################

Function QuadMesh(width,st,brush)

 ;MR 10.05.2003

 ;st = STEP
 
 Local m=CreateMesh() 
 Local s=CreateSurface(m) 

 Local x#,z#
 Local u#,v#
 Local vi

 x=-width/2.0
 Repeat
  z=-width/2.0
  Repeat
   u#= x/width 
   v#=-z/width 
   vi=AddVertex(s,x,0,z,u,v) 
   VertexNormal s,vi,0,1,0 
   z=z+st:If z>width/2.0 Then Exit
  Forever
  x=x+st:If x>width/2.0 Then Exit
 Forever

 Local i,c=0
 
 For i=0 To CountVertices(s)-(width/st+3)
  c=c+1
  If c< (width/st)+1 Then
   AddTriangle s,i  ,i+1              ,i+(width+st)/st
   AddTriangle s,i+1,i+(width+st)/st+1,i+(width+st)/st
  Else
   c=0
  EndIf
 Next 

 PaintSurface s,brush

 Return m

End Function

;#######################################################################################

Function HMap(Land)

 ;MR 10.05.2003

 ;Land is a QuadMesh and TxTerrainHMap is the Height Map

 Local s=GetSurface(Land,1)

 Local tw#=TextureWidth(TxTerrainHMap)
 Local th#=TextureHeight(TxTerrainHMap)
 
 Local x#,y#,z#,tx#,ty#,c 

 Local wx#=MeshWidth(Land)
 Local wz#=MeshDepth(Land)

 LockBuffer TextureBuffer(TxTerrainHMap)

 For i=0 To CountVertices(s)-1
  x=VertexX(s,i)
  y=VertexY(s,i)
  z=VertexZ(s,i)
  tx=(x/wx+0.5)*tw
  ty=(z/wz+0.5)*th
  If tx<0 Then tx=0
  If ty<0 Then ty=0
  If tx>tw-1 Then tx=tw-1
  If ty>th-1 Then ty=th-1
  c=ReadPixelFast(tx,ty,TextureBuffer(TxTerrainHMap))
  Color 0,0,c
  y=(((ColorRed()+ColorGreen()+ColorBlue())/3.0)/255.0)*TerrainMaxHeight
  VertexCoords s,i,x,y,z
 Next 

 UnlockBuffer TextureBuffer(TxTerrainHMap)

End Function 

;#######################################################################################

Function MyTerrainY#(e,x#,y#,z#,InWater=False)

 ;MR 10.05.2003

 ;e=MeshTerrain (QuadMesh)

 Local tw#=TextureWidth(TxTerrainHMap)
 Local th#=TextureHeight(TxTerrainHMap)
 
 Local tx#,ty#,c 

 Local wx#=MeshWidth(e)
 Local wz#=MeshDepth(e)

 LockBuffer TextureBuffer(TxTerrainHMap)

  tx=(x/wx+0.5)*tw
  ty=(z/wz+0.5)*th
  If tx<0 Then tx=0
  If ty<0 Then ty=0
  If tx>tw-1 Then tx=tw-1
  If ty>th-1 Then ty=th-1
  c=ReadPixelFast(tx,ty,TextureBuffer(TxTerrainHMap))
  Color 0,0,c
  y=(((ColorRed()+ColorGreen()+ColorBlue())/3.0)/255.0)*TerrainMaxHeight

 UnlockBuffer TextureBuffer(TxTerrainHMap)

 If InWater=False
  If y<WaterHeight Then y=WaterHeight
 EndIf
 
 Return y

End Function 

;#######################################################################################

Function MyTerrain()
  	
  ;---------------------------------------------------------------------

   Print "MyTerrain ..."

   TxTerrainHMap=mLoadTexture("world\HMap.bmp",1) 
   TerrainMaxHeight=5000
   
   Local BrLand=CreateBrush()

   ;Maserung 
   floor_tex=mLoadTexture("world\Terrain.jpg",1+256) 
   ScaleTexture floor_tex,1.0,1.0
   ;TextureBlend floor_tex,2
   BrushTexture BrLand,floor_tex,0,1

   ;Boden an sich 
   ;floor_map=mLoadTexture("Texturen\TestKaro.bmp",1+256)
   floor_map=mLoadTexture("Texturen\Fels.bmp",1+256)
   ScaleTexture floor_map,1.0/15.0,1.0/15.0
   TextureBlend floor_map,2
   BrushTexture BrLand,floor_map,0,2

   Local land=QuadMesh(100000,2500,BrLand) 

   HMap Land

   Local eWater=CreatePlane(4,Land)
   Local TxWater1=mLoadTexture("Texturen\Water.bmp",1+256) 
   Local TxWater2=mLoadTexture("Texturen\Water.bmp",1+256) 
   TextureBlend TxWater2,2
   ScaleTexture TxWater1,10000,10000
   ScaleTexture TxWater2,1000,1000
   EntityTexture eWater,TxWater1,0,1
   EntityTexture eWater,TxWater2,0,2
   EntityAlpha eWater,0.5
   EntityFX eWater,1+16
   PositionEntity eWater,0,WaterHeight,0
  
   EntityFX land,1
   EntityType land,cTerrain
   NameEntity land,"Land"
   
   Print "OK"

  ;---------------------------------------------------------------------
  
 Return Land

End Function
