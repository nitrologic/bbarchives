; ID: 1324
; Author: John J.
; Date: 2005-03-12 22:13:21
; Title: Tree System
; Description: This is a fast prop-style tree system

;*************************************************************************
;Fast Tree System
;												Written by John Judnich
;*************************************************************************

;Note: This tree system may perform poorly in debug mode

Const MAXLAYERS=100
Global TS_bank,TS_xtiles,TS_ztiles,TS_layers,TS_x1#,TS_x2#,TS_z1#,TS_z2#,TS_LOD=1,TS_morph=True,TS_camera=-1
Dim TS_layertexture(MAXLAYERS)
Dim TS_layerFX(MAXLAYERS)
;Dim TS_layerLOD(MAXLAYERS)
Const XPROP=1 ;Two polygons forming an 'X'
Const IPROP=2 ;One polygon for flat props, sprites, etc.

Type PropGroup
  Field mesh
  Field props
  Field propbank
  Field polycount
End Type

Type Prop
  Field vertex,verts
  Field surface
  Field x#,y#,z#,yaw#,xs#,ys#,zs#
  Field r,g,b,alpha#
End Type

;This function must be called before any of the tree system functions
;can be used. x1,z1,x2, And z2 are the bounds of the tree system. layers
;is the number of different trees that can be used (you can't texture Each
;tree individually). xtiles and ztiles are settings used for the occlusion
Function InitTreeSystem(x1#,z1#,x2#,z2#,layers,xtiles=10,ztiles=10)
  ;Setup variables
  TS_xtiles=xtiles-1:TS_ztiles=ztiles-1:TS_layers=layers-1
  TS_x1=x1:TS_x2=x2:TS_z1=z1:TS_z2=z2

  ;Create prop group handle bank
  TS_bank=CreateBank((TS_xtiles+1)*(TS_ztiles+1)*(TS_layers+1)*5)

  ;Make all the prop groups needed
  For z=0 To TS_ztiles
	For x=0 To TS_xtiles
	  For l=0 To TS_layers
		group=CreatePropGroup()
		WriteTSBank(x,z,l,group)
		WriteTSBank2(x,z,l,IPROP) ;Default groups to low detail for faster loading
		HideEntity group ;Hide until used
	  Next
	Next
  Next
  For l=1 To TS_layers+1
	LayerFX(l,2+16)
	;LayerViewDist(l,10)
  Next
End Function

;This function sets the camera to use for LOD calculations. Use this
;function to set the tree system camera to the main camera in your program.
;If no camera is set when UpdateTreeSystemLOD is called
Function SetTreeSystemCamera(camera)
  TS_camera=camera
End Function

;This function sets the level of detail for the tree system. The
;detail_level is actually the radius (in tiles) where trees will change
;to their higher detail level. Morph can be set to true or false, setting
;"morphing" on or off. Morphing actually just slowly changes trees from
;one detail level to another, instead of changing all that need to be
;changed at once (this smoothes out temporary frame rate drops when moving
;from one "tile" to another).
Function SetTreeSystemLOD(detail_level,morph=True)
  TS_LOD=detail_level
  TS_morph=morph
End Function

;Use this function if you want to delete all trees for any reason
Function DestroyTreeSystem()
  ;Delete all the prop groups
  For z=0 To TS_ztiles
	For x=0 To TS_xtiles
	  For l=0 To TS_layers
		DestroyPropGroup(ReadTSBank(x,z,l))
	  Next
	Next
  Next 

  ;Delete prop group bank
  FreeBank TS_bank
End Function

;This textures a layer in the tree system
Function LayerTexture(layer,texture)
  TS_layertexture(layer-1)=texture
  For z=0 To TS_ztiles
	For x=0 To TS_xtiles
	  EntityTexture ReadTSBank(x,z,layer-1),texture
	Next
  Next
End Function

;This sets the EntityFX for a layer in the tree system
Function LayerFX(layer,fx)
  TS_layerFX(layer-1)=fx
  For z=0 To TS_ztiles
	For x=0 To TS_xtiles
	  EntityFX ReadTSBank(x,z,layer-1),fx
	Next
  Next
End Function

;This sets the maximum viewing distance (in tiles) for trees in a layer
;Function LayerViewDist(layer,dist)
;  TS_layerLOD(layer-1)=dist
;End Function

;This makes a tree at the coordinates x,y,z with the specified layer. The
;x and z coordinates are permanent, but the y coordinate can be changed
Function CreateTree(x#,y#,z#,layer)
  ;Locate grid square to put tree
  xg=TSXGridLoc(x)
  zg=TSZGridLoc(z)

  ;Read group to put tree in and group detail level
  group=ReadTSBank(xg,zg,layer-1)
  detail=ReadTSBank2(xg,zg,layer-1) 

  ;Create the prop with in the group with it's detail level
  prop=CreateProp(group,detail)

  ;Position the tree at it's correction location
  PositionProp(prop,x#,y#,z#)

  ;Randomly rotate the tree
  RotateProp(prop,Rnd#(0,360))

  ;Show used group (by default groups are hidden to increase performance)
  ShowEntity group

  Return prop
End Function

;This positions a tree along the y axis
Function TreePositionY(tree,y#)
  tmp.Prop=Object.Prop(tree)
  MoveProp(tree,0,y#-tmp\y,0)
End Function

;This scales a tree according to xs,ys, and zs
Function ScaleTree(tree,xs#,ys#,zs#)
  ScaleProp(tree,xs#,ys#,zs#)
End Function

;This colors a tree
Function ColorTree(tree,r,g,b)
  ColorProp(tree,r,g,b)
End Function

;This sets a tree's alpha (opacity)
Function TreeAlpha(tree,alpha#)
  PropAlpha(tree,alpha#)
End Function

;This rotates a tree along the y axis (yaw) relative to it's last rotation
;Function TurnTree(tree,yaw#)
  ;TurnProp(tree,yaw#)
;End Function

;Call this function just before the RenderWorld command. This function
;dynamicly updates trees' LOD (level of detail). This command will fail
;if no tree system camera has been set (see SetTreeSystemCamera)
Function UpdateTreeSystem()
;tx=MilliSecs()
  ;Check camera
  If TS_camera=-1 Then RuntimeError "Error: UpdateTreeSystemLOD() function has been called, and no tree system camera has been set yet."
  
  ;Locate grid squares that the camera is in
  xg=TSXGridLoc(EntityX(TS_camera,True))
  zg=TSZGridLoc(EntityZ(TS_camera,True))
  
  ;Calculate the level of detail for each grid square and update it if it has changed
  For z=0 To TS_ztiles
	For x=0 To TS_xtiles
	  ;Calculate level of detail
	  xd=Abs(x-xg)
	  zd=Abs(z-zg)
	  If xd<=TS_LOD And zd<=TS_LOD Then detail=XPROP Else detail=IPROP
	  ;Hide tiles out of range
	  ;For l=0 To TS_layers
		;If TS_LayerLOD(l)<>False And (xd>TS_LayerLOD(l) Or zd>TS_LayerLOD(l)) Then HideEntity ReadTSBank(x,z,l) Else ShowEntity ReadTSBank(x,z,l)
	  ;Next
	  ;Check if grid tile needs updating
	  If ReadTSBank2(x,z,0)<>detail Then
		;Update grid tile
	    For l=0 To TS_layers
		  RegenerateGroup(x,z,l,detail)
	    Next
		WriteTSBank2(x,z,0,detail)
		If TS_morph=True Then Return ;Don't change all at once
	  End If
	Next
  Next
;If MilliSecs()-tx>10 then DebugLog MilliSecs()-tx
End Function



;(Internal functions:)
Function TSXGridLoc(x#)
  n#=(x#-TS_x1#)/(TS_x2#-TS_x1#)
  rtn=n#*TS_xtiles
  Return rtn
End Function

Function TSZGridLoc(z#)
  n#=(z#-TS_z1#)/(TS_z2#-TS_z1#)
  rtn=n#*TS_ztiles
  Return rtn
End Function

Function WriteTSBank(x,z,l,value)
  pos=(l*(TS_xtiles+1)*(TS_ztiles+1))+(z*(TS_xtiles+1)+x)
  PokeInt TS_bank,pos*5,value
End Function

Function WriteTSBank2(x,z,l,value)
  pos=(l*(TS_xtiles+1)*(TS_ztiles+1))+(z*(TS_xtiles+1)+x)
  PokeByte TS_bank,(pos*5)+4,value
End Function

Function ReadTSBank(x,z,l)
  pos=(l*(TS_xtiles+1)*(TS_ztiles+1))+(z*(TS_xtiles+1)+x)
  Return PeekInt(TS_bank,pos*5)
End Function

Function ReadTSBank2(x,z,l)
  pos=(l*(TS_xtiles+1)*(TS_ztiles+1))+(z*(TS_xtiles+1)+x)
  Return PeekByte(TS_bank,(pos*5)+4)
End Function

Function RegenerateGroup(x,z,l,detail)
  ;Get the group from the bank
  group=ReadTSBank(x,z,l)
  grp.PropGroup=Object.PropGroup(EntityName(group))

  ;Create a new group
  group2=CreatePropGroup()
  grp2.PropGroup=Object.PropGroup(EntityName(group2))

  ;Copy all the trees from the old group to the new group, with the new detail level
  props=BankSize(grp\propbank)/4
  For i=1 To props
	;Get the old and new prop
	oldprop=PeekInt(grp\propbank,(i*4)-4)
	oldprp.Prop=Object.Prop(oldprop)
	prop=CreateProp(group2,detail)
	prp.Prop=Object.Prop(prop)
	
	PositionProp(prop,oldprp\x,oldprp\y,oldprp\z)	;Copy position
	TurnProp(prop,oldprp\yaw)						;Copy rotation
	ScaleProp(prop,oldprp\xs,oldprp\ys,oldprp\zs)	;Copy scale
	ColorProp(prop,oldprp\r,oldprp\g,oldprp\b)		;Copy color
	PropAlpha(prop,oldprp\alpha)					;Copy alpha
  Next

  ;Copy texture and EntityFX
  EntityFX group2,TS_layerFX(l)
  EntityTexture group2,TS_layertexture(l)

  ;Delete the old group
  DestroyPropGroup(group)

  ;Put the new prop group handle into the bank in place of the old one
  WriteTSBank(x,z,l,group2)
End Function

Function CreatePropGroup()
  ;Create the new group
  grp.PropGroup=New PropGroup

  ;Set up it's mesh with one surface
  grp\mesh=CreateMesh()
  surface=CreateSurface(grp\mesh)
  NameEntity grp\mesh,Handle(grp)
  EntityFX grp\mesh,2+16 ;Vert. colors + no culling

  ;Create the prop list bank
  grp\propbank=CreateBank()

  ;Return the mesh's handle
  Return grp\mesh
End Function

Function DestroyPropGroup(group)
  grp.PropGroup=Object.PropGroup(EntityName(group))

  FreeBank grp\propbank
  FreeEntity grp\mesh

  Delete grp.PropGroup
End Function

Function GetPropCount(group)
  grp.PropGroup=Object.PropGroup(EntityName(group))
  Return grp\props  
End Function

Function GetProp(group,indx)
  grp.PropGroup=Object.PropGroup(EntityName(group))
  Return PeekInt(grp\propbank,(indx*4)-4)
End Function

Function CreateProp(group,proptype=XPROP)
  ;Get the group's surface
  grp.PropGroup=Object.PropGroup(EntityName(group))
  surface=GetSurface(grp\mesh,1)

  ;Resize the bank
  grp\props=grp\props+1
  ResizeBank grp\propbank,grp\props*4

  ;Create the new prop	
  prop.Prop=New Prop
  prop\xs=1.0:prop\ys=1.0:prop\zs=1.0
  ;Side 1
  prop\vertex=AddVertex(surface,0,-.54,0  ,.5,1.7) ;bottom
 			  AddVertex(surface,-1,.76,0  ,-.65,0) ;right
			  AddVertex(surface,1,.76,0  ,1.65,0) ;left
  AddTriangle(surface,prop\vertex+2,prop\vertex+1,prop\vertex+0)

  ;Side 2
  If proptype=XPROP Then
			  AddVertex(surface,0,-.54,0  ,.5,1.7) ;bottom
			  AddVertex(surface,0,.76,-1  ,-.65,0) ;front
			  AddVertex(surface,0,.76,1  ,1.65,0) ;back
	AddTriangle(surface,prop\vertex+5,prop\vertex+4,prop\vertex+3)
  End If
  prop\surface=surface

  If proptype=XPROP Then prop\verts=5
  If proptype=IPROP Then prop\verts=2
 
  ;Put the new prop into the group's bank
  PokeInt grp\propbank,(grp\props*4)-4,Handle(prop)
  grp\polycount=grp\polycount+2

  prop\r=255:prop\g=255:prop\b=255
  prop\alpha#=1

  ;Return the prop's handle
  Return Handle(prop)
End Function

Function ColorProp(prop,r,g,b)
  tmp.Prop=Object.Prop(prop)
  tmp\r=r:tmp\g=g:tmp\b=b
  For i=0 To tmp\verts
	VertexColor tmp\surface,tmp\vertex+i,r,g,b
  Next
End Function

Function PropAlpha(prop,alpha#)
  tmp.Prop=Object.Prop(prop)
  tmp\alpha#=alpha#
  For i=0 To tmp\verts
	VertexColor tmp\surface,tmp\vertex+i,VertexRed(tmp\surface,tmp\vertex+i),VertexGreen(tmp\surface,tmp\vertex+i),VertexBlue(tmp\surface,tmp\vertex+i),alpha#
  Next
End Function

Function PropTexCoords(prop,u1#,v1#,u2#,v2#)
  tmp.Prop=Object.Prop(prop)

End Function

Function MoveProp(prop,mx#,my#,mz#)
  tmp.Prop=Object.Prop(prop)
  For i=0 To tmp\verts
	x#=VertexX(tmp\surface,tmp\vertex+i)
	y#=VertexY(tmp\surface,tmp\vertex+i)
	z#=VertexZ(tmp\surface,tmp\vertex+i)
	VertexCoords tmp\surface,tmp\vertex+i,x#+mx#,y#+my#,z#+mz#
  Next
  tmp\x=tmp\x+mx#
  tmp\y=tmp\y+my#
  tmp\z=tmp\z+mz#
End Function

Function PositionProp(prop,x#,y#,z#)
  tmp.Prop=Object.Prop(prop)
  MoveProp(prop,x#-tmp\x,y#-tmp\y,z#-tmp\z)
End Function

Function ScaleProp(prop,xs#,ys#,zs#)
  tmp.Prop=Object.Prop(prop)
  tmp\xs=tmp\xs*xs:tmp\ys=tmp\ys*ys:tmp\zs=tmp\zs*zs
  For i=0 To tmp\verts
	dx#=VertexX(tmp\surface,tmp\vertex+i)-tmp\x
	dy#=VertexY(tmp\surface,tmp\vertex+i)-tmp\y
	dz#=VertexZ(tmp\surface,tmp\vertex+i)-tmp\z
	x#=tmp\x+dx#*xs#
	y#=tmp\y+dy#*ys#
	z#=tmp\z+dz#*zs#
	VertexCoords tmp\surface,tmp\vertex+i,x#,y#,z#
  Next
End Function

Function TurnProp(prop,yaw#)
  tmp.Prop=Object.Prop(prop)
  tmp\yaw=tmp\yaw+yaw#

  CosAY#=Cos(yaw)
  SinAY#=Sin(yaw)

  For i=0 To tmp\verts
	x#=VertexX(tmp\surface,tmp\vertex+i)-tmp\x
	y#=VertexY(tmp\surface,tmp\vertex+i)-tmp\y
	z#=VertexZ(tmp\surface,tmp\vertex+i)-tmp\z

	;Rotate around y axis
	tx#=x*cosAY+z*sinAY
	z=z*cosAY-x*sinAY
	x=tx

	x=x+tmp\x
	y=y+tmp\y
	z=z+tmp\z
	VertexCoords tmp\surface,tmp\vertex+i,x,y,z
  Next
End Function

Function RotateProp(prop,yaw#)
  tmp.Prop=Object.Prop(prop)
  TurnProp(prop,yaw#-tmp\yaw#)
End Function

Function UpdatePropGroupNormals(group)
  grp.PropGroup=Object.PropGroup(EntityName(group))
  UpdateNormals grp\Mesh
End Function
