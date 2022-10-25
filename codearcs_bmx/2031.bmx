; ID: 2031
; Author: H&amp;K
; Date: 2007-06-09 19:33:06
; Title: SDK Entity Wrapper
; Description: Very Rough SDK Entity oop Wrapper

Import blitz3d.blitz3dsdk

SuperStrict 


Type TbbEntity
	
	Global List:TList=CreateList()
	
	Field handle:Int
	
	Function HandleToType:TbbEntity (Comparisonhandle:Int)
		
		For Local Ent:TbbEntity = EachIn TbbEntity.List
			If Ent.handle=Comparisonhandle Then Return Ent
		Next
		
		Return Null
	
	End Function
	
	Method CopyEntity:TbbEntity(parent:TbbEntity=Null)
	
		Local New_Ent:TbbEntity = New TbbEntity
		If parent<>Null
			New_Ent.handle = bbCopyEntity(Self.handle,parent.handle)
		Else
			New_Ent.handle = bbCopyEntity(Self.handle)
		EndIf
		List.AddLast(New_Ent)
		Return New_Ent
	
	EndMethod
	
	Method FreeEntity()
	
		bbFreeEntity(Self.handle)	
		Self.List.Remove(Self)
		
	End Method
	
	Method EntityColour(red:Float,green:Float,blue:Float)
		
		bbEntityColor (Self.handle,red,green,blue)
			
	End Method
	
	Method EntityAlpha(alpha:Float)
		
		bbEntityAlpha(Self.handle,alpha)
		
	End Method

	Method EntityShininess(Shininess:Float)
		
		bbEntityShininess(Self.handle,Shininess)
		
	End Method
	
	Method EntityTexture(Texture:TbbTexture,Frame:Int=0,index:Int=0)
	
		bbEntityTexture(Self.handle,Texture.handle,Frame,index)
		
	End Method

	Method EntityBlend(Blend:Int = 0)
		
		bbEntityBlend(Self.handle,Blend)
		
	End Method
	
	Method EntityFX(FX:Int=0)
		
		bbEntityFX(Self.handle,Fx)
		
	End Method
	
	Method EntityAutoFade(Near:Float,Far:Float)
		
		bbEntityAutoFade(Self.handle,Near,Far)
		
	End Method
	
	Method PaintEntity(Brush:TbbBrush)
		
		bbPaintEntity(Self.handle,Brush.handle)
		
	End Method

	Method EntityOrder(Order:Int=0)
		
		bbEntityOrder(Self.handle,Order)
		
	End Method
	
	Method ShowEntity()
		
		bbShowEntity(Self.handle)
		
	End Method
	
	Method NameEntity(name:String)
		
		bbNameEntity(Self.handle,name)
		
	End Method
	
	Method EntityParent(parent:TbbEntity,GlobalSpace:Int=True)
		
		bbEntityParent(Self.handle,parent.handle,GLobalSpace)
		
	End Method
	
	Method GetParent:TbbEntity()
	
		Return Self.HandleToType(bbGetParent(Self.handle))
		
	End Method
	
	Method EntityX:Float(GlobalSpace:Int=False)
		
		Return bbEntityX(Self.handle,GlobalSpace)
		
	End Method
	
	Method EntityY:Float(GlobalSpace:Int=False)
		
		Return bbEntityY(Self.handle,GlobalSpace)
		
	End Method
	
	Method EntityZ:Float(GlobalSpace:Int=False)
		
		Return bbEntityZ(Self.handle,GlobalSpace)
		
	End Method
	
	Method EntityRoll:Float(GlobalSpace:Int=False)
		
		Return bbEntityRoll(Self.handle,GlobalSpace)
		
	End Method
	
	Method EntityYaw:Float(GlobalSpace:Int=False)
		
		Return bbEntityYaw(Self.handle,GlobalSpace)
		
	End Method

	Method EntityPitch:Float(GlobalSpace:Int=False)
		
		Return bbEntityPitch(Self.handle,GlobalSpace)
		
	End Method

	Method EntityClass:String()
		
		Return bbEntityClass(Self.handle)
		
	End Method

	Method EntityName:String()
		
		Return bbEntityName(Self.handle)
		
	End Method
	
	Method CountChildren:Int()
		
		Return bbCountChildren(Self.handle)
		
	End Method
	
	Method GetChild:TbbEntity(index:Int)
	 
		Return Self.HandleToType(bbGetChild(Self.handle,index))
		
	End Method
	
	Method FindChild:TbbEntity (name:String)
		
		Return Self.HandleToType(bbFindChild (Self.handle,name))

	End Method

	Method EntityPick:TbbEntity (Range:Float)
	
		Return Self.HandleToType (bbEntityPick(Self.handle,Range))
		
	End Method

	Method LinePick:TbbEntity(X:Float,Y:Float,Z:Float,DX:Float,DY:Float,DZ:Float,Radius:Float=0.0)
	
		Return Self.HandleToType(bbLinePick(X,Y,Z,DX,DY,DZ,Radius))
	
	End Method
	
	Method EntityVisible:Int(DestEntity:TbbEntity)
		
		Return bbEntityVisible(Self.handle,DestEntity.handle)
		
	End Method
	
	Method EntityDistance:Float(DestEntity:TbbEntity)
		
		Return bbEntityDistance(Self.handle,DestEntity.handle)
		
	End Method

	Method DeltaYaw:Float(DestEntity:TbbEntity)
	
		Return bbDeltaYaw(Self.handle,DestEntity.handle)
	
	End Method

	Method DeltaPitch:Float(DestEntity:TbbEntity)
	
		Return bbDeltaPitch(Self.handle,DestEntity.handle)
	
	End Method
	
	Method TFormPoint(X:Float,Y:Float,Z:Float,DestEntity:TbbEntity)
		
		bbTFormPoint(X,Y,Z,Self.handle,DestEntity.handle)
		
	End Method

	Method TFormVector(X:Float,Y:Float,Z:Float,DestEntity:TbbEntity)
		
		bbTFormVector (X,Y,Z,Self.handle,DestEntity.handle)
		
	End Method

	Method TFormNormal(X:Float,Y:Float,Z:Float,DestEntity:TbbEntity)
		
		bbTFormNormal (X,Y,Z,Self.handle,DestEntity.handle)
		
	End Method
	
	Function TFormedX:Float()
		
		Return bbTFormedX()
		
	End Function
	
	Function TFormedY:Float()
		
		Return bbTFormedY()
		
	End Function
	
	Function TFormedZ:Float()
		
		Return bbTFormedZ()
		
	End Function
	
	Method GetMatElement:Float (row:Int,column:Int )
		
		Return bbGetMatElement(Self.handle,row,column)
		
	End Method
	
	Method ScaleEntity (x_scale:Float,y_scale:Float,z_scalel:Float,GlobalSpace:Int=False)
	
		bbScaleEntity(Self.handle,x_scale,y_scale,z_scalel,GlobalSpace)

	End Method
	
	Method PositionEntity (X:Float,Y:Float,Z:Float,GlobalSpace:Int=False)
	
		bbPositionEntity (Self.handle,X,Y,Z,GlobalSpace)

	End Method	

	Method MoveEntity (X:Float,Y:Float,Z:Float)
	
		bbMoveEntity (Self.handle,X,Y,Z)

	End Method	

	Method TranslateEntity (X:Float,Y:Float,Z:Float,GlobalSpace:Int=False)
	
		bbTranslateEntity (Self.handle,X,Y,Z,GlobalSpace)

	End Method	

	Method RotateEntity (pitch:Float,Yaw:Float,Roll:Float,GlobalSpace:Int=False)
		
		bbRotateEntity (Self.handle,pitch,Yaw,Roll,GlobalSpace)
		
	End Method
	
	Method TurnEntity (pitch:Float,Yaw:Float,Roll:Float,GlobalSpace:Int=False)
		
		bbTurnEntity (Self.handle,pitch,Yaw,Roll,GlobalSpace)
		
	End Method
	
	Method PointEntity (Target:TbbEntity,roll:Float=0)
		
		bbPointEntity (Self.handle,target.handle,roll)
	
	End Method
	
	Method AlignToVector (vector_x:Float,vector_y:Float,vector_z:Float,axis:Int,rate:Float=1.0)
		
		bbAlignToVector (Self.handle,vector_x,vector_y,vector_z,axis,rate)

	End Method
	
	Method LoadAnimSeq:Int (filename:String)
		
		Return bbLoadAnimSeq ( Self.handle,filename)
		
	End Method
	
	Method SetAnimKey (Frame:Int,pos_key:Int=True,rot_key:Int=True,scale_key:Int=True)
	
		bbSetAnimKey (Self.handle,Frame,pos_key,rot_key,scale_key)
	
	End Method
	
	Method AddAnimSeq (length:Int)
	
		bbAddAnimSeq ( Self.handle,length)
	
	End Method

	Method ExtractAnimSeq (first_frame:Int,last_frame:Int,anim_seq:Int=0) 
		
		bbExtractAnimSeq( Self.handle,first_frame,last_frame,anim_seq )

	End Method

	Method Animate (mode:Int=1,speed:Float=1.0,sequence:Int=0,transition:Float=0)
		
		bbAnimate (Self.handle,mode,speed,sequence,transition)
		
	End Method
	
	Method SetAnimTime (time:Float,anim_seq:Int=0)
		
		bbSetAnimTime (Self.handle,time,anim_seq)
		
	End Method
	
	Method AnimSeq:Int ()
		
		Return bbAnimSeq ( Self.handle )
	
	End Method
	
	Method AnimLength:Int ()
		
		Return bbAnimLength ( Self.handle )
	
	End Method	
	
	Method AnimTime:Float ()
		
		Return bbAnimTime ( Self.handle )
	
	End Method
	
	Method Animating:Int ()
		
		Return bbAnimating ( Self.handle )
	
	End Method
	
	Method ResetEntity()
	
		bbResetEntity(Self.handle)
	
	End Method

	Method EntityRadius (x_radius:Float,y_radius:Float=0)
	
		bbEntityRadius (Self.handle,x_radius,y_radius)
	
	End Method
	
	Method EntityBox (X:Float,Y:Float,Z:Float,width:Float,height:Float,depth:Float)
	
		bbEntityBox (Self.handle,X,Y,Z,width,height,depth)
		
	End Method
	
	Method EntityType (collision_type:Int,recursive:Int=False)
		
		bbEntityType (Self.handle,collision_type,recursive)
		
	End Method

	Method EntityPickMode (pick_geometry:Int,obscurer:Int=True)
		
		bbEntityPickMode (Self.handle,pick_geometry,obscurer)

	End Method
	
	Method EntityCollided:TbbEntity ( Src_type:Int )
		
		Return TbbEntity.HandleToType(bbEntityCollided ( Self.handle,Src_Type ))
		
	EndMethod
	
	Method CountCollisions:Int ()
	
		Return bbCountCollisions(Self.handle)
	
	End Method
	
	Method CollisionX:Float (index:Int)
		
		Return bbCollisionX(Self.handle,index)
		
	End Method
	
	Method CollisionY:Float (index:Int)
		
		Return bbCollisionY(Self.handle,index)
		
	End Method
	
	Method CollisionZ:Float (index:Int)
		
		Return bbCollisionZ(Self.handle,index)
		
	End Method
	
	Method CollisionNX:Float (index:Int)
		
		Return bbCollisionNX(Self.handle,index)
		
	End Method
	
	Method CollisionNY:Float (index:Int)
		
		Return bbCollisionNY(Self.handle,index)
		
	End Method
	
	Method CollisionNZ:Float (index:Int)
		
		Return bbCollisionNZ(Self.handle,index)
		
	End Method

	Method CollisionTime:Float (index:Int)
		
		Return bbCollisionTime(Self.handle,index)
		
	End Method
	
	Method CollisionEntity:TbbEntity (index:Int)
		
		Return TbbEntity.HandleToType (bbCollisionEntity(Self.handle,index))
		
	End Method
	
	Method CollisionSurface:TbbSurface (index:Int)
		
		Return TbbSurface.HandleToType (bbCollisionSurface(Self.handle,index))
		
	End Method

	Method CollisionTriangle:Int (index:Int)
		
		Return bbCollisionTriangle(Self.handle,index)
		
	End Method
	
	Method GetEntityType ()
		
		bbGetEntityType ( Self.handle )
		
	End Method
	
	Method EntityInView:Int (camera:TbbEntity)
	
		Return bbEntityInView ( Self.handle,camera.handle)
	
	EndMethod
	
	
End Type

Type TbbBrush

	
	Global List:TList=CreateList()
	
	Field handle:Int
	
	Function HandleToType:TbbBrush (Comparisonhandle:Int)
		
		For Local Ent:TbbBrush = EachIn TbbBrush.List
			If Ent.handle=Comparisonhandle Then Return Ent
		Next
		
		Return Null
	
	End Function
	
	Function CreateBrush:TbbBrush (red:Float=255,green:Float=255,blue:Float=255)
		
		Local New_Brush:TbbBrush = New TbbBrush
		New_Brush.handle = bbCreateBrush ( red,green,blue )
		TbbBrush.List.AddLast(New_Brush)
		Return New_Brush
	
	End Function
	
	Function LoadBrush:TbbBrush (texture_file:String,flags:Int=1,u_scale:Float=1,v_scale:Float=1)
	
		Local New_Brush:TbbBrush = New TbbBrush
		New_Brush.handle = bbLoadBrush ( texture_file,flags,u_scale,v_scale)
		TbbBrush.List.AddLast(New_Brush)
		Return New_Brush
		
	End Function
	
	Method Free()
		
		bbFreeBrush(Self.handle)
		Self.List.Remove(Self)
		
	End Method
	
	Method BrushColor (red:Float,green:Float,blue:Float)
		
		bbBrushColor (Self.handle,red,green,blue)
		
	End Method
	
	Method BrushAlpha (alpha:Float)
	
		bbBrushAlpha (Self.handle,alpha)
		
	EndMethod
	
	Method BrushShininess (Shininess:Float)
	
		bbBrushShininess (Self.handle,Shininess)
		
	EndMethod
	
	Method BrushTexture (texture:TbbTexture,Frame:Int=0,index:Int=0)
	
		bbBrushTexture(Self.handle,Texture.handle,Frame,index)
		
	EndMethod
	
	Method BrushBlend (Blend:Int)
	
		bbBrushBlend (Self.handle,Blend)
		
	EndMethod
	
	Method BrushFX (FX:Int)
	
		bbBrushBlend (Self.handle,FX)
		
	EndMethod
	
	Method GetEntityBrush:TbbBrush(Entity:TbbEntity)
	
		Local New_Brush:TbbBrush = New TbbBrush
		New_Brush.handle = bbGetEntityBrush(Entity.handle)
		TbbBrush.List.AddLast(New_Brush)
		Return New_Brush
		
	End Method
		
	Method GetBrushTexture:TbbTexture (index:Int=0)
	
		Return TbbTexture.HandleToType(bbGetBrushTexture (Self.handle,index))
		
	EndMethod

EndType

Type TbbTexture

	
	Global List:TList=CreateList()
	
	Field handle:Int
	
	Function HandleToType:TbbTexture (Comparisonhandle:Int)
		
		For Local Ent:TbbTexture = EachIn TbbTexture.List
			If Ent.handle=Comparisonhandle Then Return Ent
		Next
		
		Return Null
	
	End Function
	
	Function CreateTexture:TbbTexture (width:Int,height:Int,flags:Int=0,frames:Int=0)
		
		Local New_Texture:TbbTexture = New TbbTexture
		New_Texture.handle = bbCreateTexture (width,height,flags,frames)
		TbbTexture.List.AddLast(New_Texture)
		Return New_Texture
	
	End Function

	Function LoadTexture:TbbTexture (File:String,flags:Int=1)
		
		Local New_Texture:TbbTexture = New TbbTexture
		New_Texture.handle = bbLoadTexture (File,flags)
		TbbTexture.List.AddLast(New_Texture)
		Return New_Texture
	
	End Function

	Function LoadAnimTexture:TbbTexture(file:String,flags:Int,frame_width:Int,frame_height:Int,first_frame:Int,frame_count:Int)
		
		Local New_Texture:TbbTexture = New TbbTexture
		New_Texture.handle = bbLoadAnimTexture (file,flags,frame_width,frame_height,first_frame,frame_count)
		TbbTexture.List.AddLast(New_Texture)
		Return New_Texture
	
	End Function
	
	Method FreeTexture()
		
		bbFreeTexture(Self.handle)
		Self.List.Remove(Self)
		
	End Method	
	
	Method TextureBlend (Blend:Int)
	
		bbTextureBlend (Self.handle,Blend)
		
	EndMethod	
	
	Method TextureCoords (Coords:Int)
	
		bbTextureCoords (Self.handle,Coords:Int)
		
	EndMethod	
	
	Method ScaleTexture (U_Scale:Float,V_Scale:Float)
	
		bbScaleTexture (Self.handle,U_Scale,V_Scale)
		
	EndMethod	
	
	Method PositionTexture (U_Scale:Float,V_Scale:Float)
	
		bbPositionTexture (Self.handle,U_Scale,V_Scale)
		
	EndMethod	
	
	Method RotateTexture (Angle:Float)
	
		bbRotateTexture (Self.handle,Angle)
		
	EndMethod	
	
	Method TextureWidth:Int ()
	
		Return bbTextureWidth (Self.handle)
		
	EndMethod
	
	Method TextureHeight:Int ()
	
		Return bbTextureHeight (Self.handle)
		
	EndMethod	
	
	Method TextureBuffer:Int (Frame:Int = 0)
	
		Return bbTextureBuffer (Self.handle,Frame)
		
	EndMethod
	
	Method TextureName:String ()
	
		Return bbTextureName (Self.handle)
		
	EndMethod
	
	Function ClearTextureFilters()
		
		bbClearTextureFilters()
		
	End Function
	
	Function TextureFilter (match_text:String,flags:Int)
		
		bbTextureFilter (match_text,flags)
		
	End Function
	
	Method SetCubeFace (face:Int)
	
		bbSetCubeFace (Self.handle,face)
	
	End Method
	
	Method SetCubeMode (Mode:Int)
	
		bbSetCubeMode (Self.handle,Mode)
	
	End Method
	
EndType

Type TbbMesh Extends TbbEntity
	
	Function CreateMesh:TbbMesh(parent:TbbEntity=Null)
	
		Local New_Mesh:TbbMesh = New TbbMesh
		If parent<>Null
			New_Mesh.handle = bbCreateMesh(parent.handle)
		Else
			New_Mesh.handle = bbCreateMesh()
		EndIf
		TbbMesh.List.AddLast(New_Mesh)
		Return New_Mesh
		
	End Function
	
	Function LoadMesh:TbbMesh(Filename:String,parent:TbbEntity=Null)
	
		Local New_Mesh:TbbMesh = New TbbMesh
		If parent<>Null
			New_mesh.handle = bbLoadMesh(filename$,parent.handle)
		Else
			New_mesh.handle = bbLoadMesh(filename$)
		EndIf
		
		TbbMesh.List.AddLast(New_Mesh)
		Return New_Mesh
		
	End Function
	
	Function LoadAnimMesh:TbbMesh(Filename:String,parent:TbbEntity=Null)
	
		Local New_Mesh:TbbMesh = New TbbMesh
		If parent<>Null
			New_mesh.handle = bbLoadAnimMesh(filename$,parent.handle)
		Else
			New_mesh.handle = bbLoadAnimMesh(filename$)
		EndIf
		
		TbbMesh.List.AddLast(New_Mesh)
		Return New_Mesh
		
	End Function
	
	Function CreateCube:TbbMesh(parent:TbbEntity=Null)
	
		Local New_Mesh:TbbMesh = New TbbMesh
		If parent<>Null
			New_mesh.handle = bbCreateCube(parent.handle)
		Else
			New_mesh.handle = bbCreateCube()
		EndIf
		
		TbbMesh.List.AddLast(New_Mesh)
		Return New_Mesh
		
	End Function
	
	Function CreateSphere:TbbMesh(Segments:Int=8,parent:TbbEntity=Null)
	
		Local New_Mesh:TbbMesh = New TbbMesh
		If parent<>Null
			New_mesh.handle = bbCreateSphere(Segments,parent.handle)
		Else
			New_mesh.handle = bbCreateSphere(Segments)
		EndIf
		
		TbbMesh.List.AddLast(New_Mesh)
		Return New_Mesh
		
	End Function
	
	Function CreateCylinder:TbbMesh(Segments:Int=8,SOLID:Int=True,parent:TbbEntity=Null)
	
		Local New_Mesh:TbbMesh = New TbbMesh
		If parent<>Null
			New_mesh.handle = bbCreateCylinder(Segments,SOLID,parent.handle)
		Else
			New_mesh.handle = bbCreateCylinder(Segments,SOLID)
		EndIf
		
		TbbMesh.List.AddLast(New_Mesh)
		Return New_Mesh
		
	End Function
	
	Function CreateCone:TbbMesh(Segments:Int=8,SOLID:Int=True,parent:TbbEntity=Null)
	
		Local New_Mesh:TbbMesh = New TbbMesh
		If parent<>Null
			New_mesh.handle = bbCreateCone(Segments,SOLID,parent.handle)
		Else
			New_mesh.handle = bbCreateCone(Segments,SOLID)
		EndIf
		
		TbbMesh.List.AddLast(New_Mesh)
		Return New_Mesh
		
	End Function
	
	Method CopyMesh:TbbMesh (parent:TbbEntity = Null)
		
		Local New_Mesh:TbbMesh = New TbbMesh
		If parent<>Null
			New_Mesh.handle = bbCopyMesh(Self.handle,parent.handle)
		Else
			New_Mesh.handle = bbCopyMesh(Self.handle)
		EndIf
		
		TbbMesh.List.AddLast(New_Mesh)
		Return New_Mesh
		
	End Method
	
	Method AddMesh (dest_mesh:TbbMesh)
		
		bbAddMesh (Self.handle,dest_mesh.handle)
		
	End Method
	
	Method FlipMesh()
	
		bbFlipMesh(Self.handle)
	
	End Method
	
	Method PaintMesh(Brush:TbbBrush)
		
		bbPaintMesh(Self.handle,Brush.handle)
		
	End Method
	
	Method LightMesh (red:Float,green:Float,blue:Float,Range:Float=0,light_x:Float=0,light_y:Float=0,light_z:Float=0)
		
		bbLightMesh (Self.handle,red,green,blue,Range,light_x,light_y,light_z#)
	
	End Method
	
	Method FitMesh (X:Float,Y:Float,z:Float,width:Float,height:Float,depth:Float,uniform:Int=False)
		
		bbFitMesh (Self.handle,X,Y,z,width,height,depth,uniform)
		
	End Method

	Method ScaleMesh (x_scale:Float,y_scale:Float,z_scale:Float)
		
		bbScaleMesh (Self.handle,x_scale,y_scale,z_scale)
	
	End Method
	
	Method RotateMesh (pitch:Float,yaw:Float,roll:Float)
		
		bbRotateMesh (Self.handle,pitch,yaw,roll)
		
	End Method
	
	Method PositionMesh (X:Float,Y:Float,z:Float)
		
		bbPositionMesh (Self.handle,X,Y,z)
		
	End Method
	
	Method UpdateNormals ()
		
		bbUpdateNormals (Self.handle)
		
	End Method
	
	Method MeshesIntersect:Int (mesh_b:TbbMesh )
		
		Return bbMeshesIntersect (Self.handle,mesh_b.handle)
		
	End Method
	
	Method MeshWidth:Float ()
		
		Return bbMeshWidth (Self.handle)
		
	End Method
	
	Method MeshHeight:Float ()
		
		Return bbMeshHeight (Self.handle)
		
	End Method
	
	Method MeshDepth:Float ()
		
		Return bbMeshDepth (Self.handle)
		
	End Method
	
	Method CountSurfaces:Int ()
		
		Return bbCountSurfaces (Self.handle)
	
	End Method

	Method GetSurface:TbbSurface (index:Int)
		
		Return TbbSurface.HandleToType (bbGetSurface ( Self.handle,index))
		
	End Method
	
End Type

Type TbbSurface
	
	Global List:TList=CreateList()
	
	Field handle:Int
	
	Function HandleToType:TbbSurface (Comparisonhandle:Int)
		
		For Local Ent:TbbSurface = EachIn TbbSurface.List
			If Ent.handle=Comparisonhandle Then Return Ent
		Next
		
		Return Null
	
	End Function
	
	Function CreateSurface:TbbSurface(Mesh:TbbMesh,Brush:TbbBrush=Null)
	
		Local New_Surface:TbbSurface = New TbbSurface
		If Brush<>Null
			New_Surface.handle = bbCreateSurface(Mesh.handle,Brush.handle)
		Else
			New_Surface.handle = bbCreateSurface(Mesh.handle)
		EndIf
		TbbSurface.List.AddLast(New_Surface)
		Return New_Surface
		
	End Function
	
	Method PaintSurface(Brush:TbbBrush)
		
		bbPaintSurface (Self.handle,Brush.handle)
		
	End Method
	
	Method ClearSurface (Clear_Verts:Int=True,Clear_Triangles:Int=True)
		
		bbClearSurface(Self.handle,Clear_Verts,Clear_triangles)
		
	End Method
	
	Function FindSurface:TbbSurface ( mesh:TbbMesh,brush:TbbBrush)
	
		Return TbbSurface.HandleToType (bbFindSurface ( mesh.handle,brush.handle))
	
	End Function
	
	Method AddVertex:Int (X:Float,Y:Float,z:Float,u:Float=0,v:Float=0,w:Float=1 )
		
		Return bbAddVertex (Self.handle,X,Y,z,u,v,w )
		
	End Method
	
	Method AddTriangle:Int(v0:Int,v1:Int,V2:Int)
		
		Return bbAddTriangle(Self.handle,v0,v1,v2)
		
	End Method
	
	Method VertexCoords (index:Int,X:Float,Y:Float,z:Float)
		
		bbVertexCoords (Self.handle,index,X,Y,z)
		
	End Method
	
	Method VertexNormal (index:Int,nx:Float,ny:Float,nz:Float)
		
		bbVertexNormal (Self.handle,index,nx,ny,nz)
		
	End Method
	
	Method VertexColor (index:Int,red:Float,green:Float,blue:Float,alpha:Float=1.0)
		
		bbVertexColor (Self.handle,index,red,green,blue,alpha)
		
	End Method
	
	Method VertexTexCoords (index:Int,u:Float,v:Float,w:Float=1.0,coord_set:Float=0.0)
		
		bbVertexTexCoords (Self.handle,index,u,v,w,coord_set)
		
	End Method

	Method CountVertices:Int ()
		
		Return bbCountVertices (Self.handle)
		
	End Method
	
	Method CountTriangles:Int ()
		
		Return bbCountTriangles (Self.handle)
		
	End Method
	
	Method VertexX:Float (index:Int)
	
		Return bbVertexX( Self.handle,index )
	
	End Method
	
	Method VertexY:Float (index:Int)
	
		Return bbVertexY( Self.handle,index )
	
	End Method
	
	Method VertexZ:Float (index:Int)
	
		Return bbVertexZ( Self.handle,index )
	
	End Method
	
	Method VertexNX:Float (index:Int)
	
		Return bbVertexNX( Self.handle,index )
	
	End Method
	
	Method VertexNY:Float (index:Int)
	
		Return bbVertexNY( Self.handle,index )
	
	End Method
	
	Method VertexNZ:Float (index:Int)
	
		Return bbVertexNZ( Self.handle,index )
	
	End Method
	
	Method VertexRed:Float (index:Int)
	
		Return bbVertexRed( Self.handle,index )
	
	End Method
	
	Method VertexGreen:Float (index:Int)
	
		Return bbVertexGreen( Self.handle,index )
	
	End Method
	
	Method VertexBLue:Float (index:Int)
	
		Return bbVertexBlue( Self.handle,index )
	
	End Method
	
	Method VertexAlpha:Float (index:Int)
	
		Return bbVertexAlpha( Self.handle,index )
	
	End Method

	Method VertexU:Float (index:Int,Coord_set:Int=0)
	
		Return bbVertexU (Self.handle,index,Coord_set )
	
	End Method

	Method VertexV:Float (index:Int,Coord_set:Int=0)
	
		Return bbVertexV (Self.handle,index,Coord_set )
	
	End Method

	Method VertexW:Float (index:Int)
	
		Return bbVertexW (Self.handle,index)
	
	End Method
	
	Method TriangleVertex :Float (Triangle_index:Int,Corner:Int)
	
		Return bbTriangleVertex  (Self.handle,Triangle_index,corner )
	
	End Method
	
	Method GetSurfaceBrush:TbbBrush()
	
		Local New_Brush:TbbBrush = New TbbBrush
		New_Brush.handle = bbGetSurfaceBrush(Self.handle)
		TbbBrush.List.AddLast(New_Brush)
		Return New_Brush
		
	End Method
EndType

Type TbbCamera Extends TbbEntity
	
	Function CreateCamera:TbbCamera(parent:TbbEntity=Null)
	
		Local New_Camera:TbbCamera = New TbbCamera
		If parent<>Null
			New_Camera.handle = bbCreateCamera(parent.handle)
		Else
			New_Camera.handle = bbCreateCamera()
		EndIf
		TbbCamera.List.AddLast(New_Camera)
		Return New_Camera
		
	End Function
	
	Method CameraProjMode (mode:Int)
	
		bbCameraProjMode (Self.handle,mode)

	End Method
	
	Method CameraFogMode (mode:Int)
	
		bbCameraFogMode (Self.handle,mode)

	End Method

	Method CameraFogRange (Near:Float,Far:Float)
	
		bbCameraFogRange (Self.handle,Near,Far)

	End Method
	
	Method CameraFogColor (red:Float,green:Float,blue:Float)
	
		bbCameraFogColor (Self.handle,red,green,blue)

	End Method
	
	Method CameraViewPort (X:Int,Y:Int,width:Int,height:Int)
	
		bbCameraViewport (Self.handle,X,Y,width,height)

	End Method
	
	Method CameraClsMode (CLS_Colour:Int,Cls_zBuffer:Int)
	
		bbCameraClsMode (Self.handle,CLS_colour,Cls_ZBuffer)

	End Method
	
	Method CameraClsColor (red:Float,green:Float,blue:Float)
	
		bbCameraClsColor (Self.handle,red,green,blue)

	End Method

	Method CameraRange (Near:Float,Far:Float)
	
		bbCameraRange (Self.handle,Near,Far)

	End Method
	
	Method CameraZoom (Zoom:Float)
	
		bbCameraZoom (Self.handle,Zoom)

	End Method
	
	Method CameraPick (viewport_x:Float,viewport_y:Float )
	
		bbCameraPick (Self.handle,viewport_x,viewport_y)

	End Method

	Function PickedX:Float()
		
		Return bbPickedX( )
		
	End Function

	Function PickedY:Float()
		
		Return bbPickedY( )
		
	End Function

	Function PickedZ:Float()
		
		Return bbPickedZ( )
		
	End Function

	Function PickedNX:Float()
		
		Return bbPickedNX( )
		
	End Function

	Function PickedNY:Float()
		
		Return bbPickedNY( )
		
	End Function

	Function PickedNZ:Float()
		
		Return bbPickedNZ( )
		
	End Function

	Function PickedTime:Int()
		
		Return bbPickedTime( )
		
	End Function

	Function PickedEntity:TbbEntity()
		
		Return TbbEntity.HandleToType(bbPickedEntity( ))
		
	End Function

	Function PickedSurface:TbbSurface()
		
		Return TbbSurface.HandleToType (bbPickedSurface( ))
		
	End Function

	Function PickedTriangle:Int()
		
		Return bbPickedTriangle( )
		
	End Function
	
	Method CameraProject (X:Float,Y:Float,Z:Float)
		
		bbCameraProject (Self.handle,X,Y,Z)
		
	End Method

	Function ProjectedX:Float()
		
		Return bbProjectedX( )
		
	End Function

	Function ProjectedY:Float()
		
		Return bbProjectedY( )
		
	End Function

	Function ProjectedZ:Float()
		
		Return bbProjectedZ( )
		
	End Function
	
End Type

Type TbbLight Extends TbbEntity
	
	Function CreateLight:TbbLight(LightType:Int=0,parent:TbbEntity=Null)
	
		Local New_Light:TbbLight = New TbbLight
		If parent<>Null
			New_Light.handle = bbCreateLight (LightType,parent.handle)
		Else
			New_Light.handle = bbCreateLight(LightType)
		EndIf
		TbbLight.List.AddLast(New_Light)
		Return New_Light
		
	End Function
	
	Method LightRange (Range:Float)
		
		bbLightRange (Self.handle,Range)

	End Method
	
	Method LightColor (red:Float,green:Float,blue:Float)
		
		bbLightColor (Self.handle,red:Float,green:Float,blue:Float)
		
	End Method
	
	Method LightConeAngles (inner_angle:Float,outer_angle:Float)
		
		bbLightConeAngles (Self.handle,inner_angle,outer_angle)
		
	End Method
	
	
End Type

Type TbbPivot Extends TbbEntity

	Function CreatePivot:TbbPivot (LightType:Int=0,parent:TbbEntity=Null)
	
		Local New_Pivot:TbbPivot = New TbbPivot
		If parent<>Null
			New_Pivot.handle = bbCreatePivot (parent.handle)
		Else
			New_Pivot.handle = bbCreatePivot()
		EndIf
		TbbPivot.List.AddLast(New_Pivot)
		Return New_Pivot
		
	End Function
	
End Type

Type TbbMD2 Extends TbbEntity

	Function LoadMD2:TbbMD2( md2_file:String,parent:TbbEntity=Null)
	
		Local New_MD2:TbbMD2 = New TbbMD2
		If parent<>Null
			New_MD2.handle = bbLoadMD2(MD2_File,parent.handle)
		Else
			New_MD2.handle = bbLoadMD2(MD2_File)
		EndIf
		TbbMD2.List.AddLast(New_MD2)
		Return New_MD2
	
	EndFunction
	
	Method AnimateMD2 (mode:Int=0,speed:Float=1.0,first_frame:Int=1,last_frame:Int=0,transition:Float=0.0)
		
		bbAnimateMD2 (Self.handle,mode,speed,first_frame,last_frame,transition)
		
	End Method
	
	Method MD2AnimTime:Int ()
		
		Return bbMD2AnimTime ( Self.handle )
		
	End Method
	
	Method MD2AnimLength:Int()
		
		Return bbMD2AnimLength (Self.handle)
		
	End Method

	Method MD2Animating:Int()
	
	bbMD2Animating (Self.handle)

	End Method
	
EndType

Type TbbBSP Extends TbbEntity

	Function LoadBSP:TbbBSP ( file:String,gamma_adjust:Float=0,parent:TbbEntity=Null)
	
		Local New_BSP:TbbBSP = New TbbBSP
		If parent<>Null
			New_BSP.handle = bbLoadBSP( file,gamma_adjust,parent.handle )
		Else
			New_BSP.handle = bbLoadBSP( file,gamma_adjust)
		EndIf
		TbbBSP.List.AddLast(New_BSP)
		Return New_BSP
	
	EndFunction

	Method BSPAmbientLight (red:Float, green:Float, blue:Float)
		
		bbBSPAmbientLight (Self.handle,red,green,blue)
		
	End Method
	
	Method BSPLighting (use_lightmaps:Int)
		
		bbBSPLighting (Self.handle,use_lightmaps)
		
	End Method

End Type

Type TbbPlane Extends TbbEntity

	Function LoadPlane:TbbPlane (sub_divs:Int=1,parent:TbbEntity=Null )
	
		Local New_Plane:TbbPlane = New TbbPlane
		If parent<>Null
			New_Plane.handle = bbCreatePlane (sub_divs,parent.handle )
		Else
			New_Plane.handle = bbCreatePlane (sub_divs)
		EndIf
		TbbPlane.List.AddLast(New_Plane)
		Return New_Plane
	
	EndFunction

End Type

Type TbbMirror Extends TbbEntity

	Function LoadMirror:TbbMirror (parent:TbbEntity=Null )
	
		Local New_Mirror:TbbMirror = New TbbMirror
		If parent<>Null
			New_Mirror.handle = bbCreateMirror (parent.handle )
		Else
			New_Mirror.handle = bbCreateMirror ()
		EndIf
		TbbMirror.List.AddLast(New_Mirror)
		Return New_Mirror
	
	EndFunction

End Type

Type TbbTerrain Extends TbbEntity

	Function CreateTerrain:TbbTerrain (Grid_size:Int,parent:TbbEntity=Null )
	
		Local New_Terrain:TbbTerrain = New TbbTerrain
		If parent<>Null
			New_Terrain.handle = bbCreateTerrain (Grid_size,parent.handle )
		Else
			New_Terrain.handle = bbCreateTerrain (Grid_size)
		EndIf
		TbbTerrain.List.AddLast(New_Terrain)
		Return New_Terrain
	
	EndFunction
	
	Function LoadTerrain:TbbTerrain (File:String,parent:TbbEntity=Null )
	
		Local New_Terrain:TbbTerrain = New TbbTerrain
		If parent<>Null
			New_Terrain.handle = bbLoadTerrain (File,parent.handle )
		Else
			New_Terrain.handle = bbLoadTerrain (File)
		EndIf
		TbbTerrain.List.AddLast(New_Terrain)
		Return New_Terrain
	
	EndFunction

	Method TerrainSize:Int()
		
		Return bbTerrainSize(Self.handle)
		
	End Method
	
	Method TerrainDetail (detail_level:Int,vertex_morph:Int =0)
		
		bbTerrainDetail (Self.handle,detail_level,vertex_morph)
		
	End Method
	
	Method TerrainShading(Enable:Int)
		
		bbTerrainShading(Self.handle,Enable)
		
	End Method
	
	Method TerrainHeight:Float(grid_x:Int,grid_z:Int )
		
		Return bbTerrainHeight(Self.handle,grid_x,grid_z)
		
	End Method
	
	Method ModifyTerrain (grid_x:Int,grid_z:Int,height:Float,realtime:Int=False)
		
		bbModifyTerrain (Self.handle,grid_x,grid_z,height,realtime)
		
	End Method
	
	Method TerrainX:Float (X:Float,Y:Float,z:Float)
		
		Return bbTerrainX(Self.handle,X,Y,z)
		
	End Method
	
	Method TerrainY:Float (X:Float,Y:Float,z:Float)
		
		Return bbTerrainY(Self.handle,X,Y,z)
		
	End Method
	
	Method TerrainZ:Float (X:Float,Y:Float,z:Float)
		
		Return bbTerrainZ(Self.handle,X,Y,z)
		
	End Method

End Type

'
'bbBeginBlitz3D
'
'bbGraphics3D 800,600,0,2
'
'Local MyCube:TbbMesh = TbbMesh.CreateCube()
'Local MyLight:TbbLight = TbbLight.CreateLight()
'Local MyCamera:TbbCamera = TbbCamera.CreateCamera()
'
'MyCamera.PositionEntity(0,0,-4)
'
'Mylight.LightColor(0,255,255)
'
'While not bbKeyHit(1)
'	MyCube.TurnEntity(.1,.2,.3)
'	bbRenderWorld
'	bbFlip 1
'WEnd
