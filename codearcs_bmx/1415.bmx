; ID: 1415
; Author: Oddball
; Date: 2005-07-01 09:37:37
; Title: Texture type
; Description: A texture type for BlitzGL. Includes auto-deleting with FlushMem.

'Code by David Williamson
'01/07/05

Strict
Rem
	Texture type for use with BlitzGL.
	Auto-deletes textures when no longer in use(Remember to use FlushMem).
	
	To load a texture:
		VarName:FDtexture=FDtexture.Load(filename$,mipmap%)
		
	To bind a texture:
		VarName.Bind()
	
	To access the texture directly:
		VarName.texture.ID
	
End Rem

'Texture type
Type FDtexture
	
	'Texture data
	Field texture:fdtexdata
	
	'Bind texture
	Method Bind()
		'Bind texture
		glBindTexture GL_TEXTURE_2D,texture.ID
		
	End Method
	
	'Clean up method
	Method Delete()
		'Remove referance
		texture.refcount:-1
		
		'If texture not referanced anywhere else then remove from texture list
		If texture.refcount<1 Then fd_texlist.Remove(texture)
		
	End Method
	
	'Load texture
	Function Load:FDtexture(lfilename$,flag%=True)
		'Change to full path name
		lfilename=RealPath(lfilename)
		
		Local ltex:FDtexture=New FDtexture
		
		'Check to see if texture is already loaded
		For Local tdata:fdtexdata=EachIn fd_texlist
			
			'If it is already loaded
			If lfilename=tdata.filename
				
				'Referance texture
				ltex.texture=tdata
				
				'increase refcounter
				ltex.texture.refcount:+1
				
				'return loaded texture
				Return ltex
				
			EndIf
			
		Next
		
		'If texture isn't already loaded
		ltex.texture=New fdtexdata
		
		'Store texture filename
		ltex.texture.filename=lfilename
		
		'Load texture
		ltex.texture.ID=bglTexFromPixmap(LoadPixmap(lfilename),flag)
		
		'Return the loaded texture
		Return ltex
		
	End Function

End Type

'List of loaded textures
Global fd_texlist:TList=CreateList()

'Shared texture info
Type fdtexdata
	
	Field ID%
	Field refcount%=1
	Field filename$
	
	Method New()
		'Add to texture list
		fd_texlist.AddLast(Self)
	
	End Method
	
	'Clean up method
	Method Delete()
		'Delete texture
		gldeletetextures 1,Varptr ID
		
	End Method
	
End Type
