; ID: 2975
; Author: RifRaf
; Date: 2012-09-11 11:44:22
; Title: Safe Loads (b3d)
; Description: Get the missing filename reported

;safe loads for mav trapping media issues
Function Loadimage_Safe(file$)
	If FileType(file$)<>1 Then RuntimeError "Image "+file$+" missing. "
	Return LoadImage(file$)
End Function

Function LoadAnimImage_safe(file$,cellwidth,cellheight,Fst,Cnt) 
	If FileType(file$)<>1 Then RuntimeError "AnimImage "+file$+ "not found."
	Return LoadAnimImage( file$,cellwidth,cellheight,fst,cnt)
End Function

Function LoadSound_Safe(file$)
	If FileType(file$)<>1 Then RuntimeError "Sound "+file$+ "not found."
    Return LoadSound(file$)
End Function

Function Load3DSound_Safe(FILE$)
	If FileType(file$)<>1 Then RuntimeError "3D Sound "+file$+ "not found."
    Return Load3DSound(file$)
End Function

Function LoadMesh_safe(File$,parent=0)
	If FileType(file$)<>1 Then RuntimeError "3D Mesh "+file$+ "not found."
	Return LoadMesh(file$,parent)  
End Function   

Function LoadAnimMesh_safe(File$,parent=0)
	If FileType(file$)<>1 Then RuntimeError "3D Animated Mesh "+file$+ "not found."
	Return LoadAnimMesh(file$,parent)  
End Function   

Function LoadAnimSeq_safe(entity,file$)
    If entity=0 Then RuntimeError "Cannot load Animation Sequence. Non existing entity"
	If FileType(file$)<>1 Then RuntimeError "Animation Sequence "+file$+ "not found."
	Return LoadAnimSeq(entity,file$)  
End Function   

Function LoadTexture_safe(File$,flags=0)
	If FileType(file$)<>1 Then RuntimeError "Texture "+file$+ "not found."
	Return LoadTexture(file$,flags)  
End Function   

Function LoadAnimTexture_Safe(file$,flags,width,height,Fst,cnt)
	If FileType(file$)<>1 Then RuntimeError "Animated Texture "+file$+ "not found."
	Return LoadAnimTexture(file$,flags,width,height,fst,cnt)  
End Function   

Function LoadBrush_safe(file$,flags,u#=1.0,v#=1.0)
	If FileType(file$)<>1 Then RuntimeError "Brush Texture "+file$+ "not found."
	Return LoadBrush(file$,flags,u#,v#)  
End Function 

Function LoadSprite_safe(file$,flags,parent=0)
	If FileType(file$)<>1 Then RuntimeError "Sprite Texture "+file$+ "not found."
	Return LoadSprite(file$,flags,parent)  
End Function 

Function LoadTerrain_safe(file$,parent=0)
	If FileType(file$)<>1 Then RuntimeError "Heightmap Image "+file$+ "not found."
	Return LoadTerrain(file$,parent)  
End Function 

Function LoadFont_safe(file$,height=12,bold=False,italic=False,underline=False)
	If FileType(file$)<>1 Then RuntimeError "Font "+file$+ "not found."
	Return LoadFont(file$,height,bold,italic,underline)  
End Function
