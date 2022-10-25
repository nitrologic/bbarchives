; ID: 2540
; Author: b32
; Date: 2009-07-22 13:11:25
; Title: minib3d scenegraph
; Description: scenegraph system for minib3d

Type TSceneGraph
	
	'create scenegraph
	Field cluster:TList[,,]
	Field scale%
	Field clustermax%

	Method Create:TSceneGraph(size%,clustersize%)
		clustermax% = (size/clustersize) + 1
		cluster = New TList[clustermax, clustermax, clustermax]
		For Local i% = 0 To clustermax-1
		For Local j% = 0 To clustermax-1
		For Local k% = 0 To clustermax-1
			cluster[i, j, k] = CreateList()
		Next
		Next
		Next
		scale = clustersize
		Return Self
	End Method
	
	Method AddToSceneGraph:Int(e:TEntity)
		Local x#,y#,z#
		x# = EntityX(e, True)
		y# = EntityY(e, True)
		z# = EntityZ(e, True)
		Local cx%,cy%,cz%
		cx = x/scale
		cy = y/scale
		cz = z/scale
		If cx<0 Then Return False
		If cy<0 Then Return False
		If cz<0 Then Return False
		If cx>=clustermax Then Return False
		If cy>=clustermax Then Return False
		If cz>=clustermax Then Return False
		ListRemove TEntity.entity_list, e
		ListAddLast cluster[cx,cy,cz], e
		Return True
	End Method
	
	Method Render(cam:TCamera, size%=8)
		'show scenegraph	
		Local bx%, by%, bz%
		bx = EntityX(cam) / scale
		by = EntityY(cam) / scale
		bz = EntityZ(cam) / scale
	
		For Local j% = -size To size
		For Local i% = -size To size
		For Local k% = -size To size
			
			Local x%,y%,z%
			x = i + bx
			y = j + by
			z = k + bz
		
			If (x >= 0) And (y >= 0) And (z >= 0) And (x < clustermax) And (y < clustermax) And (z < clustermax) Then
					For Local e:TEntity = EachIn cluster[x, y, z]
						If sphereinfrustum(cam, e.px, e.py, e.pz, 1) Then 
							If TSprite(e) Then UpdateSprite cam, TSprite(e)
							e.Update()
						End If
					Next
			End If
		
		Next
		Next
		Next	
	End Method
	
End Type

Function SphereInFrustum#(cam:TCamera, x#,y#,z#,radius#)
	
	Local d#
	
	For Local p=0 To 5
	
		d# = cam.frustum[p,0] * x + cam.frustum[p,1] * y + cam.frustum[p,2] * -z + cam.frustum[p,3]
	      
		If d <= -radius Then Return 0
	
	Next
	
	Return d + radius
	
End Function

Function UpdateSprite(cam:TCamera, sprite:TSprite)

	If sprite.view_mode<>2
	
		Local x#=sprite.mat.grid[3,0]
		Local y#=sprite.mat.grid[3,1]
		Local z#=sprite.mat.grid[3,2]
	
		sprite.mat.Overwrite(cam.mat)
		sprite.mat.grid[3,0]=x
		sprite.mat.grid[3,1]=y
		sprite.mat.grid[3,2]=z
		sprite.mat_sp.Overwrite(sprite.mat)
		
		If sprite.angle#<>0.0
			sprite.mat_sp.RotateRoll(sprite.angle#)
		EndIf
		
		If sprite.scale_x#<>1.0 Or sprite.scale_y#<>1.0
			sprite.mat_sp.Scale(sprite.scale_x#,sprite.scale_y#,1.0)
		EndIf
		
		If sprite.handle_x#<>0.0 Or sprite.handle_y#<>0.0
			sprite.mat_sp.Translate(-sprite.handle_x#,-sprite.handle_y#,0.0)
		EndIf
		
	Else
	
		sprite.mat_sp.Overwrite(sprite.mat)
		
		If sprite.scale_x#<>1.0 Or sprite.scale_y#<>1.0
			sprite.mat_sp.Scale(sprite.scale_x#,sprite.scale_y#,1.0)
		EndIf

	EndIf
	
End Function
