; ID: 1563
; Author: ozak
; Date: 2005-12-14 05:58:37
; Title: Simple texture class
; Description: Simplest texture class ever :)

' Simple texture class by Odin Jensen (http://www.furi.dk)
' Free to use for all purposes :)
Strict

' Texture class
Type Texture

' Texture dimensions
Field Width:Int
Field Height:Int

' Texture name
Field FileName:String

' Texture OpenGL ID
Field TexID:Int

' Load texture
Method Load(URL:Object)
	
	' Attempt To load image
	Local TextureImage:TPixmap=LoadPixmap(URL);
	
	' Save dimensions for later
	Self.Width=TextureImage.Width
	Self.Height=TextureImage.Height
	
	' Create GL texture
	glGenTextures 1, Varptr TexID
	
	' Bind it first
	Bind()

	' Enable bilinear filtering
	glTexParameteri GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,GL_LINEAR
	glTexParameteri GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_LINEAR
	
	' Create actual texture
	glTexImage2D GL_TEXTURE_2D, 0, 3, Width, Height, 0, GL_RGB, GL_UNSIGNED_BYTE, TextureImage.pixels
	
	' Flag texture as unused so the garbage collector can do it's thing
	TextureImage = Null
	
	' Save filename for later
	Self.FileName = FileName

EndMethod

' Destroy texture
Method Destroy()

	glDeleteTextures 1, Varptr TexID

EndMethod

' Bind texture (Call before drawing)
Method Bind()

	glBindTexture GL_TEXTURE_2D, TexID
	
EndMethod

EndType
