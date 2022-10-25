; ID: 3125
; Author: munch
; Date: 2014-05-14 23:41:19
; Title: Openb3d library wrapper
; Description: Openb3d is a version of Minib3d in C++

' functions.bmx
' Wrapped functions from Openb3d library


' Wrapped functions with Byte Ptr arguments (for casting to Int)
' --------------------------------------------------------------

' Minib3d Only

Rem
bbdoc: undocumented
End Rem
Function MeshCullRadius( ent:Int, radius:Float )
	MeshCullRadius_( Byte Ptr(ent), radius )
End Function

' Blitz3D functions, A-Z

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=AddAnimSeq">Online doc</a>
End Rem
Function AddAnimSeq:Int( ent:Int, length:Int )
	Return AddAnimSeq_( Byte Ptr(ent), length )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=AddMesh">Online doc</a>
End Rem
Function AddMesh( mesh1:Int, mesh2:Int )
	AddMesh_( Byte Ptr(mesh1), Byte Ptr(mesh2) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=AddTriangle">Online doc</a>
End Rem
Function AddTriangle:Int( surf:Int, v0:Int, v1:Int, v2:Int )
	Return AddTriangle_( Byte Ptr(surf), v0, v1, v2 )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=Animating">Online doc</a>
End Rem
Function Animating:Int( ent:Int )
	Return Animating_( Byte Ptr(ent) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=AnimLength">Online doc</a>
End Rem
Function AnimLength( ent:Int )
	AnimLength_( Byte Ptr(ent) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=AnimSeq">Online doc</a>
End Rem
Function AnimSeq:Int( ent:Int )
	Return AnimSeq_( Byte Ptr(ent) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=AnimTime">Online doc</a>
End Rem
Function AnimTime:Float( ent:Int )
	Return AnimTime_( Byte Ptr(ent) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=BrushAlpha">Online doc</a>
End Rem
Function BrushAlpha( brush:Int, a:Float )
	BrushAlpha_( Byte Ptr(brush), a )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=BrushBlend">Online doc</a>
End Rem
Function BrushBlend( brush:Int, blend:Int )
	BrushBlend_( Byte Ptr(brush), blend )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=BrushColor">Online doc</a>
End Rem
Function BrushColor( brush:Int, r:Float, g:Float, b:Float )
	BrushColor_( Byte Ptr(brush), r, g, b )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=BrushFX">Online doc</a>
End Rem
Function BrushFX( brush:Int, fx:Int )
	BrushFX_( Byte Ptr(brush), fx )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=BrushShininess">Online doc</a>
End Rem
Function BrushShininess( brush:Int, s:Float )
	BrushShininess_( Byte Ptr(brush), s )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CameraClsColor">Online doc</a>
End Rem
Function CameraClsColor( cam:Int, r:Float, g:Float, b:Float )
	CameraClsColor_( Byte Ptr(cam), r, g, b )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CameraClsMode">Online doc</a>
End Rem
Function CameraClsMode( cam:Int, cls_depth:Int, cls_zbuffer:Int )
	CameraClsMode_( Byte Ptr(cam), cls_depth, cls_zbuffer )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CameraFogColor">Online doc</a>
End Rem
Function CameraFogColor( cam:Int, r:Float, g:Float, b:Float )
	CameraFogColor_( Byte Ptr(cam), r, g, b )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CameraFogMode">Online doc</a>
End Rem
Function CameraFogMode( cam:Int, mode:Int )
	CameraFogMode_( Byte Ptr(cam), mode )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CameraFogRange">Online doc</a>
End Rem
Function CameraFogRange( cam:Int, nnear:Float, nfar:Float )
	CameraFogRange_( Byte Ptr(cam), nnear, nfar )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CameraPick">Online doc</a>
End Rem
Function CameraPick:Int( cam:Int, x:Float, y:Float )
	Return Int( CameraPick_( Byte Ptr(cam), x, GraphicsHeight()-y ) ) ' inverted y
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CameraProject">Online doc</a>
End Rem
Function CameraProject( cam:Int, x:Float, y:Float, z:Float )
	CameraProject_( Byte Ptr(cam), x, y, z )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CameraProjMode">Online doc</a>
End Rem
Function CameraProjMode( cam:Int, mode:Int )
	CameraProjMode_( Byte Ptr(cam), mode )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CameraRange">Online doc</a>
End Rem
Function CameraRange( cam:Int, nnear:Float, nfar:Float )
	CameraRange_( Byte Ptr(cam), nnear, nfar )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CameraViewport">Online doc</a>
End Rem
Function CameraViewport( cam:Int, x:Int, y:Int, width:Int, height:Int )
	CameraViewport_( Byte Ptr(cam), x, y, width, height )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CameraZoom">Online doc</a>
End Rem
Function CameraZoom( cam:Int, zoom:Float )
	CameraZoom_( Byte Ptr(cam), zoom )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CollisionEntity">Online doc</a>
End Rem
Function CollisionEntity:Int( ent:Int, index:Int )
	Return Int( CollisionEntity_( Byte Ptr(ent), index ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CollisionNX">Online doc</a>
End Rem
Function CollisionNX:Float( ent:Int, index:Int )
	Return CollisionNX_( Byte Ptr(ent), index )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CollisionNY">Online doc</a>
End Rem
Function CollisionNY:Float( ent:Int, index:Int )
	Return CollisionNY_( Byte Ptr(ent), index )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CollisionNZ">Online doc</a>
End Rem
Function CollisionNZ:Float( ent:Int, index:Int )
	Return CollisionNZ_( Byte Ptr(ent), index )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CollisionSurface">Online doc</a>
End Rem
Function CollisionSurface:Int( ent:Int, index:Int )
	Return Int( CollisionSurface_( Byte Ptr(ent), index ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CollisionTime">Online doc</a>
End Rem
Function CollisionTime:Float( ent:Int, index:Int )
	Return CollisionTime_( Byte Ptr(ent), index )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CollisionTriangle">Online doc</a>
End Rem
Function CollisionTriangle:Int( ent:Int, index:Int )
	Return CollisionTriangle_( Byte Ptr(ent), index )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CollisionX">Online doc</a>
End Rem
Function CollisionX:Float( ent:Int, index:Int )
	Return CollisionX_( Byte Ptr(ent), index )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CollisionY">Online doc</a>
End Rem
Function CollisionY:Float( ent:Int, index:Int )
	Return CollisionY_( Byte Ptr(ent), index )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CollisionZ">Online doc</a>
End Rem
Function CollisionZ:Float( ent:Int, index:Int )
	Return CollisionZ_( Byte Ptr(ent), index )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CountChildren">Online doc</a>
End Rem
Function CountChildren:Int( ent:Int )
	Return CountChildren_( Byte Ptr(ent) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CountCollisions">Online doc</a>
End Rem
Function CountCollisions:Int( ent:Int )
	Return CountCollisions_( Byte Ptr(ent) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CountSurfaces">Online doc</a>
End Rem
Function CountSurfaces:Int( mesh:Int )
	Return CountSurfaces_( Byte Ptr(mesh) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CountTriangles">Online doc</a>
End Rem
Function CountTriangles:Int( surf:Int )
	Return CountTriangles_( Byte Ptr(surf) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CountVertices">Online doc</a>
End Rem
Function CountVertices:Int( surf:Int )
	Return CountVertices_( Byte Ptr(surf) )
End Function

Rem
bbdoc: undocumented
End Rem
Function CreateStencil:Int()
	Return Int( CreateStencil_() )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=DeltaPitch">Online doc</a>
End Rem
Function DeltaPitch:Float( ent1:Int, ent2:Int )
	Return -DeltaPitch_( Byte Ptr(ent1), Byte Ptr(ent2) ) ' inverted pitch
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=DeltaYaw">Online doc</a>
End Rem
Function DeltaYaw:Float( ent1:Int, ent2:Int )
	Return DeltaYaw_( Byte Ptr(ent1), Byte Ptr(ent2) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityAlpha">Online doc</a>
End Rem
Function EntityAlpha( ent:Int, alpha:Float )
	EntityAlpha_( Byte Ptr(ent), alpha )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityAutoFade">Online doc</a>
End Rem
Function EntityAutoFade( ent:Int, near:Float, far:Float )
	EntityAutoFade_( Byte Ptr(ent), near, far )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityBlend">Online doc</a>
End Rem
Function EntityBlend( ent:Int, blend:Int )
	EntityBlend_( Byte Ptr(ent), blend )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityBox">Online doc</a>
End Rem
Function EntityBox( ent:Int, x:Float, y:Float, z:Float, w:Float, h:Float, d:Float )
	EntityBox_( Byte Ptr(ent), x, y, z, w, h, d )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityClass">Online doc</a>
End Rem
Function EntityClass:String( ent:Int )
	Return String.FromCString( EntityClass_( Byte Ptr(ent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityCollided">Online doc</a>
End Rem
Function EntityCollided:Int( ent:Int, type_no:Int )
	Return Int( EntityCollided_( Byte Ptr(ent), type_no ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityColor">Online doc</a>
End Rem
Function EntityColor( ent:Int, red:Float, green:Float, blue:Float )
	EntityColor_( Byte Ptr(ent), red, green, blue )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityDistance">Online doc</a>
End Rem
Function EntityDistance:Float( ent1:Int, ent2:Int )
	Return EntityDistance_( Byte Ptr(ent1), Byte Ptr(ent2) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityFX">Online doc</a>
End Rem
Function EntityFX( ent:Int, fx:Int )
	EntityFX_( Byte Ptr(ent), fx )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityInView">Online doc</a>
End Rem
Function EntityInView:Int( ent:Int, cam:Int )
	Return EntityInView_( Byte Ptr(ent), Byte Ptr(cam) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityName">Online doc</a>
End Rem
Function EntityName:String( ent:Int )
	Return String.FromCString( EntityName_( Byte Ptr(ent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityOrder">Online doc</a>
End Rem
Function EntityOrder( ent:Int, order:Int )
	EntityOrder_( Byte Ptr(ent), order )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityPick">Online doc</a>
End Rem
Function EntityPick:Int( ent:Int, range:Float )
	Return Int( EntityPick_( Byte Ptr(ent), range ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityShininess">Online doc</a>
End Rem
Function EntityShininess( ent:Int, shine:Float )
	EntityShininess_( Byte Ptr(ent), shine )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityVisible">Online doc</a>
End Rem
Function EntityVisible:Int( src_ent:Int, dest_ent:Int )
	Return EntityVisible_( Byte Ptr(src_ent), Byte Ptr(dest_ent) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=FindChild">Online doc</a>
End Rem
Function FindChild:Int( ent:Int, child_name:String )
	Return Int( FindChild_( Byte Ptr(ent), child_name.ToCString() ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=FindSurface">Online doc</a>
End Rem
Function FindSurface:Int( mesh:Int, brush:Int )
	Return Int( FindSurface_( Byte Ptr(mesh), Byte Ptr(brush) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=FlipMesh">Online doc</a>
End Rem
Function FlipMesh( mesh:Int )
	FlipMesh_( Byte Ptr(mesh) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=FreeBrush">Online doc</a>
End Rem
Function FreeBrush( brush:Int )
	FreeBrush_( Byte Ptr(brush) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=FreeEntity">Online doc</a>
End Rem
Function FreeEntity( ent:Int )
	FreeEntity_( Byte Ptr(ent) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=FreeTexture">Online doc</a>
End Rem
Function FreeTexture( tex:Int )
	FreeTexture_( Byte Ptr(tex) )
End Function

Rem
bbdoc: undocumented
End Rem
Function GeosphereHeight( geo:Int, h:Float )
	GeosphereHeight_( Byte Ptr(geo), h )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=GetChild">Online doc</a>
End Rem
Function GetChild:Int( ent:Int, child_no:Int )
	Return Int( GetChild_( Byte Ptr(ent), child_no ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=GetEntityBrush">Online doc</a>
End Rem
Function GetEntityBrush:Int( ent:Int )
	Return Int( GetEntityBrush_( Byte Ptr(ent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=GetEntityType">Online doc</a>
End Rem
Function GetEntityType:Int( ent:Int )
	Return GetEntityType_( Byte Ptr(ent) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=GetMatElement">Online doc</a>
End Rem
Function GetMatElement:Float( ent:Int, row:Int, col:Int )
	Return GetMatElement_( Byte Ptr(ent), row, col )
End Function

Rem
bbdoc: undocumented
End Rem
Function GetParentEntity:Int( ent:Int )
	Return Int( GetParentEntity_( Byte Ptr(ent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=GetSurface">Online doc</a>
End Rem
Function GetSurface:Int( mesh:Int, surf_no:Int )
	Return Int( GetSurface_( Byte Ptr(mesh), surf_no ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=GetSurfaceBrush">Online doc</a>
End Rem
Function GetSurfaceBrush:Int( surf:Int )	
	Return Int( GetSurfaceBrush_( Byte Ptr(surf) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=HandleSprite">Online doc</a>
End Rem
Function HandleSprite( sprite:Int, h_x:Float, h_y:Float )
	HandleSprite_( Byte Ptr(sprite), h_x, h_y )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=HideEntity">Online doc</a>
End Rem
Function HideEntity( ent:Int )
	HideEntity_( Byte Ptr(ent) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=LightColor">Online doc</a>
End Rem
Function LightColor( light:Int, red:Float, green:Float, blue:Float )
	LightColor_( Byte Ptr(light), red, green, blue )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=LightConeAngles">Online doc</a>
End Rem
Function LightConeAngles( light:Int, inner_ang:Float, outer_ang:Float )
	LightConeAngles_( Byte Ptr(light), inner_ang, outer_ang )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=LightRange">Online doc</a>
End Rem
Function LightRange( light:Int, range:Float )
	LightRange_( Byte Ptr(light), range )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=LoadAnimTexture">Online doc</a>
End Rem
Function LoadAnimTexture:Int( file:String, flags:Int, frame_width:Int, frame_height:Int, first_frame:Int, frame_count:Int )
	Return Int( LoadAnimTexture_( file.ToCString(), flags, frame_width, frame_height, first_frame, frame_count ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=MeshDepth">Online doc</a>
End Rem
Function MeshDepth:Float( mesh:Int )
	Return MeshDepth_( Byte Ptr(mesh) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=MeshesIntersect">Online doc</a>
End Rem
Function MeshesIntersect:Int( mesh1:Int, mesh2:Int )
	Return MeshesIntersect_( Byte Ptr(mesh1), Byte Ptr(mesh2) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=MeshHeight">Online doc</a>
End Rem
Function MeshHeight:Float( mesh:Int )
	Return MeshHeight_( Byte Ptr(mesh) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=MeshWidth">Online doc</a>
End Rem
Function MeshWidth:Float( mesh:Int )
	Return MeshWidth_( Byte Ptr(mesh) )
End Function

Rem
bbdoc: undocumented
End Rem
Function ModifyGeosphere( geo:Int, x:Int, z:Int, new_height:Float )
	ModifyGeosphere_( Byte Ptr(geo), x, z, new_height )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=ModifyTerrain">Online doc</a>
End Rem
Function ModifyTerrain( terr:Int, x:Int, z:Int, new_height:Float )
	ModifyTerrain_( Byte Ptr(terr), x, z, new_height )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=MoveEntity">Online doc</a>
End Rem
Function MoveEntity( ent:Int, x:Float, y:Float, z:Float )
	MoveEntity_( Byte Ptr(ent), x, y, z )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=NameEntity">Online doc</a>
End Rem
Function NameEntity( ent:Int, name:String )
	NameEntity_( Byte Ptr(ent), name.ToCString() )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=PaintEntity">Online doc</a>
End Rem
Function PaintEntity( ent:Int, brush:Int )
	PaintEntity_( Byte Ptr(ent), Byte Ptr(brush) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=PaintMesh">Online doc</a>
End Rem
Function PaintMesh( mesh:Int, brush:Int )
	PaintMesh_( Byte Ptr(mesh), Byte Ptr(brush) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=PaintSurface">Online doc</a>
End Rem
Function PaintSurface( surf:Int, brush:Int )
	PaintSurface_( Byte Ptr(surf), Byte Ptr(brush) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=PickedEntity">Online doc</a>
End Rem
Function PickedEntity:Int()
	Return Int( PickedEntity_() )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=PickedSurface">Online doc</a>
End Rem
Function PickedSurface:Int()
	Return Int( PickedSurface_() )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=PositionMesh">Online doc</a>
End Rem
Function PositionMesh( mesh:Int, px:Float, py:Float, pz:Float )
	PositionMesh_( Byte Ptr(mesh), px, py, pz )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=PositionTexture">Online doc</a>
End Rem
Function PositionTexture( tex:Int, u_pos:Float, v_pos:Float )
	PositionTexture_( Byte Ptr(tex), u_pos, v_pos )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=ResetEntity">Online doc</a>
End Rem
Function ResetEntity( ent:Int )
	ResetEntity_( Byte Ptr(ent) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=RotateMesh">Online doc</a>
End Rem
Function RotateMesh( mesh:Int, pitch:Float, yaw:Float, roll:Float )
	RotateMesh_( Byte Ptr(mesh), -pitch, yaw, roll ) ' inverted pitch
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=RotateSprite">Online doc</a>
End Rem
Function RotateSprite( sprite:Int, ang:Float )
	RotateSprite_( Byte Ptr(sprite), ang )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=RotateTexture">Online doc</a>
End Rem
Function RotateTexture( tex:Int, ang:Float )
	RotateTexture_( Byte Ptr(tex), ang )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=ScaleMesh">Online doc</a>
End Rem
Function ScaleMesh( mesh:Int, sx:Float, sy:Float, sz:Float )
	ScaleMesh_( Byte Ptr(mesh), sx, sy, sz )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=ScaleSprite">Online doc</a>
End Rem
Function ScaleSprite( sprite:Int, s_x:Float, s_y:Float )
	ScaleSprite_( Byte Ptr(sprite), s_x, s_y )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=ScaleTexture">Online doc</a>
End Rem
Function ScaleTexture( tex:Int, u_scale:Float, v_scale:Float )
	ScaleTexture_( Byte Ptr(tex), u_scale, v_scale )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=SetCubeFace">Online doc</a>
End Rem
Function SetCubeFace( tex:Int, face:Int )
	SetCubeFace_( Byte Ptr(tex), face )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=SetCubeMode">Online doc</a>
End Rem
Function SetCubeMode( tex:Int, mode:Int )
	SetCubeMode_( Byte Ptr(tex), mode )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=ShowEntity">Online doc</a>
End Rem
Function ShowEntity( ent:Int )
	ShowEntity_( Byte Ptr(ent) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=SpriteViewMode">Online doc</a>
End Rem
Function SpriteViewMode( sprite:Int, mode:Int )
	SpriteViewMode_( Byte Ptr(sprite), mode )
End Function

Rem
bbdoc: undocumented
End Rem
Function StencilAlpha( stencil:Int, a:Float )
	StencilAlpha_( Byte Ptr(stencil), a )
End Function

Rem
bbdoc: undocumented
End Rem
Function StencilClsColor( stencil:Int, r:Float, g:Float, b:Float )
	StencilClsColor_( Byte Ptr(stencil), r, g, b )
End Function

Rem
bbdoc: undocumented
End Rem
Function StencilClsMode( stencil:Int, cls_depth:Int, cls_zbuffer:Int )
	StencilClsMode_( Byte Ptr(stencil), cls_depth, cls_zbuffer )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=TerrainHeight">Online doc</a>
End Rem
Function TerrainHeight:Float( terr:Int, x:Int, z:Int )
	Return TerrainHeight_( Byte Ptr(terr), x, z )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=TerrainX">Online doc</a>
End Rem
Function TerrainX:Float( terr:Int, x:Float, y:Float, z:Float )
	Return TerrainX_( Byte Ptr(terr), x, y, z )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=TerrainY">Online doc</a>
End Rem
Function TerrainY:Float( terr:Int, x:Float, y:Float, z:Float )
	Return TerrainY_( Byte Ptr(terr), x, y, z )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=TerrainZ">Online doc</a>
End Rem
Function TerrainZ:Float( terr:Int, x:Float, y:Float, z:Float )
	Return TerrainZ_( Byte Ptr(terr), x, y, z )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=TextureBlend">Online doc</a>
End Rem
Function TextureBlend( tex:Int, blend:Int )
	TextureBlend_( Byte Ptr(tex), blend )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=TextureCoords">Online doc</a>
End Rem
Function TextureCoords( tex:Int, coords:Int )
	TextureCoords_( Byte Ptr(tex), coords )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=TextureHeight">Online doc</a>
End Rem
Function TextureHeight:Int( tex:Int )
	Return TextureHeight_( Byte Ptr(tex) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=TextureFilter">Online doc</a>
End Rem
Function TextureFilter( match_text:String, flags:Int )
	TextureFilter_( match_text.ToCString(), flags )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=TextureName">Online doc</a>
End Rem
Function TextureName:String( tex:Int )
	Return String.FromCString( TextureName_( Byte Ptr(tex) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=TextureWidth">Online doc</a>
End Rem
Function TextureWidth:Int( tex:Int )
	Return TextureWidth_( Byte Ptr(tex) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=TFormNormal">Online doc</a>
End Rem
Function TFormNormal( x:Float, y:Float, z:Float, src_ent:Int, dest_ent:Int )
	TFormNormal_( x, y, z, Byte Ptr(src_ent), Byte Ptr(dest_ent) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=TFormPoint">Online doc</a>
End Rem
Function TFormPoint( x:Float, y:Float, z:Float, src_ent:Int, dest_ent:Int )
	TFormPoint_( x, y, z, Byte Ptr(src_ent), Byte Ptr(dest_ent) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=TFormVector">Online doc</a>
End Rem
Function TFormVector( x:Float, y:Float, z:Float, src_ent:Int, dest_ent:Int )
	TFormVector_( x, y, z, Byte Ptr(src_ent), Byte Ptr(dest_ent) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=TriangleVertex">Online doc</a>
End Rem
Function TriangleVertex:Int( surf:Int, tri_no:Int, corner:Int )
	Return TriangleVertex_( Byte Ptr(surf), tri_no, corner )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=UpdateNormals">Online doc</a>
End Rem
Function UpdateNormals( mesh:Int )
	UpdateNormals_( Byte Ptr(mesh) )
End Function

Rem
bbdoc: undocumented
End Rem
Function UpdateTexCoords( surf:Int )
	UpdateTexCoords_( Byte Ptr(surf) )
End Function

Rem
bbdoc: undocumented
End Rem
Function UseStencil( stencil:Int )
	UseStencil_( Byte Ptr(stencil) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=VertexAlpha">Online doc</a>
End Rem
Function VertexAlpha:Float( surf:Int, vid:Int )
	Return VertexAlpha_( Byte Ptr(surf), vid )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=VertexBlue">Online doc</a>
End Rem
Function VertexBlue:Float( surf:Int, vid:Int )
	Return VertexBlue_( Byte Ptr(surf), vid )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=VertexCoords">Online doc</a>
End Rem
Function VertexCoords( surf:Int, vid:Int, x:Float, y:Float, z:Float )
	VertexCoords_( Byte Ptr(surf), vid, x, y, z )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=VertexGreen">Online doc</a>
End Rem
Function VertexGreen:Float( surf:Int, vid:Int )
	Return VertexGreen_( Byte Ptr(surf), vid )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=VertexNormal">Online doc</a>
End Rem
Function VertexNormal( surf:Int, vid:Int, nx:Float, ny:Float, nz:Float )
	VertexNormal_( Byte Ptr(surf), vid, nx, ny, nz )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=VertexNX">Online doc</a>
End Rem
Function VertexNX:Float( surf:Int, vid:Int )
	Return VertexNX_( Byte Ptr(surf), vid )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=VertexNY">Online doc</a>
End Rem
Function VertexNY:Float( surf:Int, vid:Int )
	Return VertexNY_( Byte Ptr(surf), vid )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=VertexNZ">Online doc</a>
End Rem
Function VertexNZ:Float( surf:Int, vid:Int )
	Return VertexNZ_( Byte Ptr(surf), vid )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=VertexRed">Online doc</a>
End Rem
Function VertexRed:Float( surf:Int, vid:Int )
	Return VertexRed_( Byte Ptr(surf), vid )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=VertexX">Online doc</a>
End Rem
Function VertexX:Float( surf:Int, vid:Int )
	Return VertexX_( Byte Ptr(surf), vid )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=VertexY">Online doc</a>
End Rem
Function VertexY:Float( surf:Int, vid:Int )
	Return VertexY_( Byte Ptr(surf), vid )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=VertexZ">Online doc</a>
End Rem
Function VertexZ:Float( surf:Int, vid:Int )
	Return VertexZ_( Byte Ptr(surf), vid )
End Function

Rem
bbdoc: undocumented
End Rem
Function VoxelSpriteMaterial( voxelspr:Int, mat:Int )
	VoxelSpriteMaterial_( Byte Ptr(voxelspr), Byte Ptr(mat) )
End Function

' ***extras***

Rem
bbdoc: Load shader from two files, vertex and fragment.
End Rem
Function LoadShader:Int( ShaderName:String, VshaderFileName:String, FshaderFileName:String )
	Return Int( LoadShader_( ShaderName.ToCString(), VshaderFileName.ToCString(), FshaderFileName.ToCString() ) )
End Function

Rem
bbdoc: Load shader from two strings, vertex and fragment.
End Rem
Function CreateShader:Int( ShaderName:String, VshaderString:String, FshaderString:String )
	Return Int( CreateShader_( ShaderName.ToCString(), VshaderString.ToCString(), FshaderString.ToCString() ) )
End Function

Rem
bbdoc: Apply shader to a surface.
End Rem
Function ShadeSurface( surf:Int, material:Int )
	ShadeSurface_( Byte Ptr(surf), Byte Ptr(material) )
End Function

Rem
bbdoc: Apply shader to a mesh.
End Rem
Function ShadeMesh( mesh:Int, material:Int )
	ShadeMesh_( Byte Ptr(mesh), Byte Ptr(material) )
End Function

Rem
bbdoc: Apply shader to an entity.
End Rem
Function ShadeEntity( ent:Int, material:Int )
	ShadeEntity_( Byte Ptr(ent), Byte Ptr(material) )
End Function

Rem
bbdoc: Set a shader variable name of a uniform float type to a float value.
End Rem
Function SetFloat( material:Int, name:String, v1:Float )
	SetFloat_( Byte Ptr(material), name.ToCString(), v1 )
End Function

Rem
bbdoc: Set a shader variable name of a uniform vec2 type to 2 float values.
End Rem
Function SetFloat2( material:Int, name:String, v1:Float, v2:Float )
	SetFloat2_( Byte Ptr(material), name.ToCString(), v1, v2 )
End Function

Rem
bbdoc: Set a shader variable name of a uniform vec3 type to 3 float values.
End Rem
Function SetFloat3( material:Int, name:String, v1:Float, v2:Float, v3:Float )
	SetFloat3_( Byte Ptr(material), name.ToCString(), v1, v2, v3 )
End Function

Rem
bbdoc: Set a shader variable name of a uniform vec4 type to 4 float values.
End Rem
Function SetFloat4( material:Int, name:String, v1:Float, v2:Float, v3:Float, v4:Float )
	SetFloat4_( Byte Ptr(material), name.ToCString(), v1, v2, v3, v4 )
End Function

Rem
bbdoc: Bind a float variable to a shader variable name of a uniform float type.
End Rem
Function UseFloat( material:Int, name:String, v1:Float Var )
	UseFloat_( Byte Ptr(material), name.ToCString(), Varptr(v1) )
End Function

Rem
bbdoc: Bind 2 float variables to a shader variable name of a uniform vec2 type.
End Rem
Function UseFloat2( material:Int, name:String, v1:Float Var, v2:Float Var )
	UseFloat2_( Byte Ptr(material), name.ToCString(), Varptr(v1), Varptr(v2) )
End Function

Rem
bbdoc: Bind 3 float variables to a shader variable name of a uniform vec3 type.
End Rem
Function UseFloat3( material:Int, name:String, v1:Float Var, v2:Float Var, v3:Float Var )
	UseFloat3_( Byte Ptr(material), name.ToCString(), Varptr(v1), Varptr(v2), Varptr(v3) )
End Function

Rem
bbdoc: Bind 4 float variables to a shader variable name of a uniform vec4 type.
End Rem
Function UseFloat4( material:Int, name:String, v1:Float Var, v2:Float Var, v3:Float Var, v4:Float Var )
	UseFloat4_( Byte Ptr(material), name.ToCString(), Varptr(v1), Varptr(v2), Varptr(v3), Varptr(v4) )
End Function

Rem
bbdoc: Set a shader variable name of a uniform int type to an integer value.
End Rem
Function SetInteger( material:Int, name:String, v1:Int )
	SetInteger_( Byte Ptr(material), name.ToCString(), v1 )
End Function

Rem
bbdoc: Set a shader variable name of a uniform ivec2 type to 2 integer values.
End Rem
Function SetInteger2( material:Int, name:String, v1:Int, v2:Int )
	SetInteger2_( Byte Ptr(material), name.ToCString(), v1, v2 )
End Function

Rem
bbdoc: Set a shader variable name of a uniform ivec3 type to 3 integer values.
End Rem
Function SetInteger3( material:Int, name:String, v1:Int, v2:Int, v3:Int )
	SetInteger3_( Byte Ptr(material), name.ToCString(), v1, v2, v3 )
End Function

Rem
bbdoc: Set a shader variable name of a uniform ivec4 type to 4 integer values.
End Rem
Function SetInteger4( material:Int, name:String, v1:Int, v2:Int, v3:Int, v4:Int )
	SetInteger4_( Byte Ptr(material), name.ToCString(), v1, v2, v3, v4 )
End Function

Rem
bbdoc: Bind an integer variable to a shader variable name of a uniform int type.
End Rem
Function UseInteger( material:Int, name:String, v1:Int Var )
	UseInteger_( Byte Ptr(material), name.ToCString(), Varptr(v1) )
End Function

Rem
bbdoc: Bind 2 integer variables to a shader variable name of a uniform ivec2 type.
End Rem
Function UseInteger2( material:Int, name:String, v1:Int Var, v2:Int Var )
	UseInteger2_( Byte Ptr(material), name.ToCString(), Varptr(v1), Varptr(v2) )
End Function

Rem
bbdoc: Bind 3 integer variables to a shader variable name of a uniform ivec3 type.
End Rem
Function UseInteger3( material:Int, name:String, v1:Int Var, v2:Int Var, v3:Int Var )
	UseInteger3_( Byte Ptr(material), name.ToCString(), Varptr(v1), Varptr(v2), Varptr(v3) )
End Function

Rem
bbdoc: Bind 4 integer variables to a shader variable name of a uniform ivec4 type.
End Rem
Function UseInteger4( material:Int, name:String, v1:Int Var, v2:Int Var, v3:Int Var, v4:Int Var )
	UseInteger4_( Byte Ptr(material), name.ToCString(), Varptr(v1), Varptr(v2), Varptr(v3), Varptr(v4) )
End Function

Rem
bbdoc: undocumented
End Rem
Function UseSurface( material:Int, name:String, surf:Int, vbo:Int )
	UseSurface_( Byte Ptr(material), name.ToCString(), Byte Ptr(surf), vbo )
End Function

Rem
bbdoc: Send matrix data to a shader variable name of a uniform mat4 type.
If mode is true it sends camera matrix otherwise projection matrix.
End Rem
Function UseMatrix( material:Int, name:String, mode:Int )
	UseMatrix_( Byte Ptr(material), name.ToCString(), mode )
End Function

Rem
bbdoc: undocumented
End Rem
Function LoadMaterial:Int( filename:String, flags:Int, frame_width:Int, frame_height:Int, first_frame:Int, frame_count:Int )
	Return Int( LoadMaterial_( filename.ToCString(), flags, frame_width, frame_height, first_frame, frame_count ) )
End Function


' Wrapped functions with default arguments (can't be declared in globals)
' -----------------------------------------------------------------------

Rem
bbdoc: Copy the contents of the backbuffer to a texture.
End Rem
Function BackBufferToTex( tex:Int, frame:Int=0 )
	BackBufferToTex_( Byte Ptr(tex), frame )
End Function

Rem
bbdoc: Copy a pixmap buffer to texture, buffer must be a byte ptr.
End Rem
Function BufferToTex( tex:Int, buffer:Byte Ptr, frame:Int=0 )
	BufferToTex_( Byte Ptr(tex), buffer, frame )
End Function

Rem
bbdoc: Copy a rendered camera view to texture.
End Rem
Function CameraToTex( tex:Int, cam:Int, frame:Int=0 )
	CameraToTex_( Byte Ptr(tex), Byte Ptr(cam), frame )
End Function

Rem
bbdoc: Copy a texture to a pixmap buffer, buffer must be a byte ptr.
End Rem
Function TexToBuffer( tex:Int, buffer:Byte Ptr, frame:Int=0 )
	TexToBuffer_( Byte Ptr(tex), buffer, frame )
End Function

' Blitz3D functions, A-Z

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=AddVertex">Online doc</a>
End Rem
Function AddVertex:Int( surf:Int, x:Float, y:Float, z:Float, u:Float=0, v:Float=0, w:Float=0 )
	Return AddVertex_( Byte Ptr(surf), x, y, z, u, v, w )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=Animate">Online doc</a>
End Rem
Function Animate( ent:Int, mode:Int=1, speed:Float=1, seq:Int=0, trans:Int=0 )
	Animate_( Byte Ptr(ent), mode, speed, seq, trans )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=BrushTexture">Online doc</a>
End Rem
Function BrushTexture( brush:Int, tex:Int, frame:Int=0, index:Int=0 )
	BrushTexture_( Byte Ptr(brush), Byte Ptr(tex), frame, index )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=ClearSurface">Online doc</a>
End Rem
Function ClearSurface( surf:Int, clear_verts:Int=True, clear_tris:Int=True )
	ClearSurface_( Byte Ptr(surf), clear_verts, clear_tris )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=ClearWorld">Online doc</a>
End Rem
Function ClearWorld( entities:Int=True, brushes:Int=True, textures:Int=True )
	ClearWorld_( entities, brushes, textures )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=Collisions">Online doc</a>
End Rem
Function Collisions( src_no:Int, dest_no:Int, method_no:Int, response_no:Int=0 )
	Collisions_( src_no, dest_no, method_no, response_no )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CopyEntity">Online doc</a>
End Rem
Function CopyEntity:Int( ent:Int, parent:Int=Null )
	Return Int( CopyEntity_( Byte Ptr(ent), Byte Ptr(parent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CopyMesh">Online doc</a>
End Rem
Function CopyMesh:Int( mesh:Int, parent:Int=Null )
	Return Int( CopyMesh_( Byte Ptr(mesh), Byte Ptr(parent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CreateBrush">Online doc</a>
End Rem
Function CreateBrush:Int( r:Float=255, g:Float=255, b:Float=255 )
	Return Int( CreateBrush_( r, g, b ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CreateCamera">Online doc</a>
End Rem
Function CreateCamera:Int( parent:Int=Null )
	Return Int( CreateCamera_( Byte Ptr(parent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CreateCone">Online doc</a>
End Rem
Function CreateCone:Int( segments:Int=8, solid:Int=True, parent:Int=Null )
	Return Int( CreateCone_( segments, solid, Byte Ptr(parent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CreateCylinder">Online doc</a>
End Rem
Function CreateCylinder:Int( segments:Int=8, solid:Int=True, parent:Int=Null )
	Return Int( CreateCylinder_( segments, solid, Byte Ptr(parent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CreateCube">Online doc</a>
End Rem
Function CreateCube:Int( parent:Int=Null )
	Return Int( CreateCube_( Byte Ptr(parent) ) )
End Function

Rem
bbdoc: undocumented
End Rem
Function CreateGeosphere:Int( size:Int, parent:Int=Null )
	Return Int( CreateGeosphere_( size, Byte Ptr(parent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CreateMesh">Online doc</a>
End Rem
Function CreateMesh:Int( parent:Int=Null )
	Return Int( CreateMesh_( Byte Ptr(parent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CreateLight">Online doc</a>
End Rem
Function CreateLight:Int( light_type:Int=1, parent:Int=Null )
	Return Int( CreateLight_( light_type, Byte Ptr(parent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CreatePivot">Online doc</a>
End Rem
Function CreatePivot:Int( parent:Int=Null )
	Return Int( CreatePivot_( Byte Ptr(parent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CreatePlane">Online doc</a>
End Rem
Function CreatePlane:Int( divisions:Int=1, parent:Int=Null )
	Return Int( CreatePlane_( divisions, Byte Ptr(parent) ) )
End Function

Rem
bbdoc: undocumented
End Rem
Function CreateQuad:Int( parent:Int=Null )
	Return Int( CreateQuad_( Byte Ptr(parent) ) )
End Function

Rem
bbdoc: undocumented
End Rem
Function CreateShadow:Int( parent:Int, Static:Int=False )
	Return Int( CreateShadow_( Byte Ptr(parent), Static ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CreateSphere">Online doc</a>
End Rem
Function CreateSphere:Int( segments:Int=8, parent:Int=Null )
	Return Int( CreateSphere_( segments, Byte Ptr(parent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CreateSprite">Online doc</a>
End Rem
Function CreateSprite:Int( parent:Int=Null )
	Return Int( CreateSprite_( Byte Ptr(parent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CreateSurface">Online doc</a>
End Rem
Function CreateSurface:Int( mesh:Int, brush:Int=Null )
	Return Int( CreateSurface_( Byte Ptr(mesh), Byte Ptr(brush) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CreateTerrain">Online doc</a>
End Rem
Function CreateTerrain:Int( size:Int, parent:Int=Null )
	Return Int( CreateTerrain_( size, Byte Ptr(parent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=CreateTexture">Online doc</a>
End Rem
Function CreateTexture:Int( width:Int, height:Int, flags:Int=9, frames:Int=1 )
	Return Int( CreateTexture_( width, height, flags, frames ) )
End Function

Rem
bbdoc: undocumented
End Rem
Function CreateVoxelSprite:Int( slices:Int=64, parent:Int=Null )
	Return Int( CreateVoxelSprite_( slices, Byte Ptr(parent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityParent">Online doc</a>
End Rem
Function EntityParent( ent:Int, parent_ent:Int, glob:Int=True )
	EntityParent_( Byte Ptr(ent), Byte Ptr(parent_ent), glob )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityPickMode">Online doc</a>
End Rem
Function EntityPickMode( ent:Int, pick_mode:Int, obscurer:Int=True )
	EntityPickMode_( Byte Ptr(ent), pick_mode, obscurer )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityPitch">Online doc</a>
End Rem
Function EntityPitch:Float( ent:Int, glob:Int=False )
	Return -EntityPitch_( Byte Ptr(ent), glob ) ' inverted pitch
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityRadius">Online doc</a>
End Rem
Function EntityRadius( ent:Int, radius_x:Float, radius_y:Float=0 )
	EntityRadius_( Byte Ptr(ent), radius_x, radius_y )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityRoll">Online doc</a>
End Rem
Function EntityRoll:Float( ent:Int, glob:Int=True )
	Return EntityRoll_( Byte Ptr(ent), glob )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityTexture">Online doc</a>
End Rem
Function EntityTexture( ent:Int, tex:Int, frame:Int=0, index:Int=0 )
	EntityTexture_( Byte Ptr(ent), Byte Ptr(tex), frame, index )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityType">Online doc</a>
End Rem
Function EntityType( ent:Int, type_no:Int, recursive:Int=False )
	EntityType_( Byte Ptr(ent), type_no, recursive )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityX">Online doc</a>
End Rem
Function EntityX:Float( ent:Int, glob:Int=False )
	Return EntityX_( Byte Ptr(ent), glob )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityY">Online doc</a>
End Rem
Function EntityY:Float( ent:Int, glob:Int=False )
	Return EntityY_( Byte Ptr(ent), glob )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityYaw">Online doc</a>
End Rem
Function EntityYaw:Float( ent:Int, glob:Int=False )
	Return EntityYaw_( Byte Ptr(ent), glob )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=EntityZ">Online doc</a>
End Rem
Function EntityZ:Float( ent:Int, glob:Int=False )
	Return EntityZ_( Byte Ptr(ent), glob )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=ExtractAnimSeq">Online doc</a>
End Rem
Function ExtractAnimSeq:Int( ent:Int, first_frame:Int, last_frame:Int, seq:Int=0 )
	Return ExtractAnimSeq_( Byte Ptr(ent), first_frame, last_frame, seq )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=FitMesh">Online doc</a>
End Rem
Function FitMesh( mesh:Int, x:Float, y:Float, z:Float, width:Float, height:Float, depth:Float, uniform:Int=False )
	FitMesh_( Byte Ptr(mesh), x, y, z, width, height, depth, uniform )
End Function

Rem
bbdoc: undocumented
End Rem
Function FreeShadow( shad:Int )
	FreeShadow_( Byte Ptr(shad) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=GetBrushTexture">Online doc</a>
End Rem
Function GetBrushTexture:Int( brush:Int, index:Int=0 )
	Return Int( GetBrushTexture_( Byte Ptr(brush), index ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=LinePick">Online doc</a>
End Rem
Function LinePick:Int( x:Float, y:Float, z:Float, dx:Float, dy:Float, dz:Float, radius:Float=0 )
	Return Int( LinePick_( x, y, z, dx, dy, dz, radius ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=LoadAnimMesh">Online doc</a>
End Rem
Function LoadAnimMesh:Int( file:String, parent:Int=Null )
	Return Int( LoadAnimMesh_( file.ToCString(), Byte Ptr(parent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=LoadBrush">Online doc</a>
End Rem
Function LoadBrush:Int( file:String, flags:Int=1, u_scale:Float=1, v_scale:Float=1 )
	Return Int( LoadBrush_( file.ToCString(), flags, u_scale, v_scale ) )
End Function

Rem
bbdoc: undocumented
End Rem
Function LoadGeosphere:Int( file:String, parent:Int=Null )
	Return Int( LoadGeosphere_( file.ToCString(), Byte Ptr(parent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=LoadMesh">Online doc</a>
End Rem
Function LoadMesh:Int( file:String, parent:Int=Null )
	Return Int( LoadMesh_( file.ToCString(), Byte Ptr(parent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=LoadTerrain">Online doc</a>
End Rem
Function LoadTerrain:Int( file:String, parent:Int=Null )
	Return Int( LoadTerrain_( file.ToCString(), Byte Ptr(parent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=LoadTexture">Online doc</a>
End Rem
Function LoadTexture:Int( file:String, flags:Int=1 )
	Return Int( LoadTexture_( file.ToCString(), flags ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=LoadSprite">Online doc</a>
End Rem
Function LoadSprite:Int( tex_file:String, tex_flag:Int=1, parent:Int=Null )
	Return Int( LoadSprite_( tex_file.ToCString(), tex_flag, Byte Ptr(parent) ) )
End Function

Rem
bbdoc: undocumented
End Rem
Function MeshCSG:Int( m1:Int, m2:Int, method_no:Int=1 )
	Return Int( MeshCSG_( Byte Ptr(m1), Byte Ptr(m2), method_no ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=PointEntity">Online doc</a>
End Rem
Function PointEntity( ent:Int, target_ent:Int, roll:Float=0 )
	PointEntity_( Byte Ptr(ent), Byte Ptr(target_ent), roll )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=PositionEntity">Online doc</a>
End Rem
Function PositionEntity( ent:Int, x:Float, y:Float, z:Float, glob:Int=False )
	PositionEntity_( Byte Ptr(ent), x, y, z, glob )
End Function

Rem
bbdoc: undocumented
End Rem
Function RepeatMesh:Int( mesh:Int, parent:Int=Null )
	Return Int( RepeatMesh_( Byte Ptr(mesh), Byte Ptr(parent) ) )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=RotateEntity">Online doc</a>
End Rem
Function RotateEntity( ent:Int, x:Float, y:Float, z:Float, glob:Int=False )
	RotateEntity_( Byte Ptr(ent), -x, y, z, glob ) ' inverted pitch
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=ScaleEntity">Online doc</a>
End Rem
Function ScaleEntity( ent:Int, x:Float, y:Float, z:Float, glob:Int=False )
	ScaleEntity_( Byte Ptr(ent), x, y, z, glob )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=SetAnimTime">Online doc</a>
End Rem
Function SetAnimTime( ent:Int, time:Float, seq:Int=0 )
	SetAnimTime_( Byte Ptr(ent), time, seq )
End Function

Rem
bbdoc: undocumented
End Rem
Function StencilMesh( stencil:Int, mesh:Int, mode:Int=1 )
	StencilMesh_( Byte Ptr(stencil), Byte Ptr(mesh), mode )
End Function

Rem
bbdoc: undocumented
End Rem
Function StencilMode( stencil:Int, m:Int, o:Int=1 )
	StencilMode_( Byte Ptr(stencil), m, o )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=TranslateEntity">Online doc</a>
End Rem
Function TranslateEntity( ent:Int, x:Float, y:Float, z:Float, glob:Int=False )
	TranslateEntity_( Byte Ptr(ent), x, y, z, glob )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=TurnEntity">Online doc</a>
End Rem
Function TurnEntity( ent:Int, x:Float, y:Float, z:Float, glob:Int=False )
	TurnEntity_( Byte Ptr(ent), -x, y, z, glob ) ' inverted pitch
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=UpdateWorld">Online doc</a>
End Rem
Function UpdateWorld( anim_speed:Float=1 )
	UpdateWorld_( anim_speed )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=VertexColor">Online doc</a>
End Rem
Function VertexColor( surf:Int, vid:Int, r:Float, g:Float, b:Float, a:Float=1 )
	VertexColor_( Byte Ptr(surf), vid, r, g, b, a )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=VertexTexCoords">Online doc</a>
End Rem
Function VertexTexCoords( surf:Int, vid:Int, u:Float, v:Float, w:Float=0, coord_set:Int=0 )
	VertexTexCoords_( Byte Ptr(surf), vid, u, v, w, coord_set )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=VertexU">Online doc</a>
End Rem
Function VertexU:Float( surf:Int, vid:Int, coord_set:Int=0 )
	Return VertexU_( Byte Ptr(surf), vid, coord_set )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=VertexV">Online doc</a>
End Rem
Function VertexV:Float( surf:Int, vid:Int, coord_set:Int=0 )
	Return VertexV_( Byte Ptr(surf), vid, coord_set )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=VertexW">Online doc</a>
End Rem
Function VertexW:Float( surf:Int, vid:Int, coord_set:Int=0 )
	Return VertexW_( Byte Ptr(surf), vid, coord_set )
End Function

' ***extras***

Rem
bbdoc: undocumented
End Rem
Function EntityScaleX:Float( ent:Int, glob:Int=False )
	Return EntityScaleX_( Byte Ptr(ent), glob )
End Function

Rem
bbdoc: undocumented
End Rem
Function EntityScaleY:Float( ent:Int, glob:Int=False )
	Return EntityScaleY_( Byte Ptr(ent), glob )
End Function

Rem
bbdoc: undocumented
End Rem
Function EntityScaleZ:Float( ent:Int, glob:Int=False )
	Return EntityScaleZ_( Byte Ptr(ent), glob )
End Function

Rem
bbdoc: Load a texture for 2D texture sampling.
End Rem
Function ShaderTexture( material:Int, tex:Int, name:String, index:Int=0 )
	ShaderTexture_( Byte Ptr(material), Byte Ptr(tex), name.ToCString(), index )
End Function

Rem
bbdoc: Load a texture for 3D texture sampling.
End Rem
Function ShaderMaterial( material:Int, tex:Int, name:String, index:Int=0 )
	ShaderMaterial_( Byte Ptr(material), Byte Ptr(tex), name.ToCString(), index )
End Function

Rem
bbdoc: undocumented
End Rem
Function CreateOcTree:Int( w:Float, h:Float, d:Float, parent_ent:Int=Null )
	Return Int( CreateOcTree_( w, h, d, Byte Ptr(parent_ent) ) )
End Function

Rem
bbdoc: undocumented
End Rem
Function OctreeBlock( octree:Int, mesh:Int, level:Int, X:Float, Y:Float, Z:Float, Near:Float=0, Far:Float=1000 )
	OctreeBlock_( Byte Ptr(octree), Byte Ptr(mesh), level, X, Y, Z, Near, Far )
End Function

Rem
bbdoc: undocumented
End Rem
Function OctreeMesh( octree:Int, mesh:Int, level:Int, X:Float, Y:Float, Z:Float, Near:Float=0, Far:Float=1000 )
	OctreeMesh_( Byte Ptr(octree), Byte Ptr(mesh), level, X, Y, Z, Near, Far )
End Function


' Remaining functions (wrapped for docs)
' --------------------------------------

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=AmbientLight">Online doc</a>
End Rem
Function AmbientLight( r:Float, g:Float, b:Float )
	AmbientLight_( r, g, b )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=AntiAlias">Online doc</a>
End Rem
Function AntiAlias( samples:Int )
	AntiAlias_( samples )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=ClearCollisions">Online doc</a>
End Rem
Function ClearCollisions()
	ClearCollisions_()
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=ClearTextureFilters">Online doc</a>
End Rem
Function ClearTextureFilters()
	ClearTextureFilters_()
End Function
	
Rem
bbdoc: undocumented
End Rem
Function GraphicsResize( width:Int, height:Int )
	GraphicsResize_( width, height )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=PickedNX">Online doc</a>
End Rem
Function PickedNX:Float()
	Return PickedNX_()
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=PickedNY">Online doc</a>
End Rem
Function PickedNY:Float()
	Return PickedNY_()
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=PickedNZ">Online doc</a>
End Rem
Function PickedNZ:Float()
	Return PickedNZ_()
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=PickedTime">Online doc</a>
End Rem
Function PickedTime:Float()
	Return PickedTime_()
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=PickedTriangle">Online doc</a>
End Rem
Function PickedTriangle:Int()
	Return PickedTriangle_()
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=PickedX">Online doc</a>
End Rem
Function PickedX:Float()
	Return PickedX_()
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=PickedY">Online doc</a>
End Rem
Function PickedY:Float()
	Return PickedY_()
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=PickedZ">Online doc</a>
End Rem
Function PickedZ:Float()
	Return PickedZ_()
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=ProjectedX">Online doc</a>
End Rem
Function ProjectedX:Float()
	Return ProjectedX_()
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=ProjectedY">Online doc</a>
End Rem
Function ProjectedY:Float()
	Return ProjectedY_()
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=ProjectedZ">Online doc</a>
End Rem
Function ProjectedZ:Float()
	Return ProjectedZ_()
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=RenderWorld">Online doc</a>
End Rem
Function RenderWorld()
	RenderWorld_()
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=TFormedX">Online doc</a>
End Rem
Function TFormedX:Float()
	Return TFormedX_()
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=TFormedY">Online doc</a>
End Rem
Function TFormedY:Float()
	Return TFormedY_()
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=TFormedZ">Online doc</a>
End Rem
Function TFormedZ:Float()
	Return TFormedZ_()
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=VectorPitch">Online doc</a>
End Rem
Function VectorPitch:Float( vx:Float, vy:Float, vz:Float )
	Return -VectorPitch_( vx, vy, vz ) ' inverted pitch
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=VectorYaw">Online doc</a>
End Rem
Function VectorYaw:Float( vx:Float, vy:Float, vz:Float )
	Return VectorYaw_( vx, vy, vz )
End Function

Rem
bbdoc: <a href="http://www.blitzbasic.com/b3ddocs/command.php?name=Wireframe">Online doc</a>
End Rem
Function Wireframe( enable:Int )
	Wireframe_( enable )
End Function
