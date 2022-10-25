; ID: 497
; Author: poopla
; Date: 2002-11-20 11:14:53
; Title: Project PLASMA FPS:  Material
; Description: Material.class  | Material object

Update .01:
    Removed the physics field from the material object.  A seperate physics object will be enstated for greater 
flexability.
----------------------------------------------------------

;The material objects type
Type Material
        field Texture[4],Brush,frame_width,frame_height
        Field frame_count,first_frame
	Field anim_speed#, CurFrame
	Field CollisionId,SoundRes[30]
End Type

Dim materialRef.material(n)

;The function to pre-cache our mateials before runtime.
Function CacheMaterials(n)

	For count = 1 To n
	
		materialRef(count) = New material
		materialRef(count)\brush = CreateBrush()

	Next
	
End Function

;These functions are all pretty self explanetory

;This is how you load the materials standard 4 textures
;and set the multitexturing properties.
 
Function Material_LoadTexture(id, tex, path$, frame = 0, index = 0)

	materialRef(id)\texture[tex] = LoadTexture(path$)
	
	If materialRef(id)\texture[tex] = 0 Then RuntimeError "Material texture file does not exist"
	
End Function


Function Material_BrushTexture(id, path$, frame = 0, index = 0)

	BrushTexture (materialRef(id)\brush,path$,frame,index)
		
End Function


Function Material_Shine(id, shine#)

	BrushShininess (materialRef(id)\brush, shine)
	
End Function


Function Material_Fx(id, Fx)

	BrushFX (materialRef(id)\brush, Fx)
	
End Function


Function Material_Alpha(id, alpha#)

	BrushFX (materialRef(id)\brush, alpha)
	
End Function


Function Material_RGB(id, R, G, B)

	BrushColor (materialRef(id)\brush, R, G, B)
	
End Function



Function ApplyMaterial(entity, id)

	PaintMesh (entity, materialRef(id)\brush)
	
End Function


Function freeMaterial(id)

	FreeBrush(materialRef(id)\brush)
	
	For tex = 0 To 3 
	
		If materialRef(id)\texture[tex] <> 0 Then
		
			FreeTexture materialRef(id)\texture[tex]
		
		EndIf
		
	Next
	
	Delete materialRef(id)
	
End Function
	
	
Function Material_blend(id,blend)

	BrushBlend materialRef(id)\brush, blend
	
End Function
