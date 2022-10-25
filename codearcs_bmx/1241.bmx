; ID: 1241
; Author: Nilium
; Date: 2004-12-19 14:18:02
; Title: DrawImageBlock
; Description: Draws a portion of a TImage (frame)

Strict

Module Cower.Drawing
ModuleInfo "Cower drawing module"
ModuleInfo "This code is public domain ( because it's not special in any way )"

Import BRL.GLMax2D
Import Pub.OpenGL

Function DrawImageBlock( Img:TImage, X#, Y#, Width#, Height#, TX#, TY#, TWidth#, THeight#, Frame%=0, Rotation#=0, MX#=0, MY#=0 )
	Global __glob_ix#
	Global __glob_iy#
	Global __glob_jx#
	Global __glob_jy#
	
	Local frm:TGLImageFrame = TGLImageFrame( Img.frames[frame] )
	If frm = Null Then Return
	
	Local texEnabled:Int = glIsEnabled( GL_TEXTURE_2D )
	If texEnabled=0 Then
		glEnable( GL_TEXTURE_2D )
	EndIf
	
	Local lastTex:Int
	glGetIntegerv( GL_TEXTURE_BINDING_2D, Varptr lastTex )
	
	c_setupTrans( rotation )
	
	Local wo1:Float = 1.0 / Img.Width
	Local ho1:Float = 1.0 / Img.Height
	
	Local UF:Float = TX * wo1
	Local VF:Float = TY * ho1
	Local UT:Float = ( TX+TWidth ) * wo1
	Local VT:Float = ( TY+THeight ) * ho1
	
	glBindTexture( GL_TEXTURE_2D, frm.name )
	
	glBegin GL_QUADS
	
	glTexCoord2f( UF, VF )
	glVertex2f( mx+x*__glob_ix+y*__glob_iy, my+x*__glob_jx+y*__glob_jy )
	
	glTexCoord2f( UT, VF )
	glVertex2f( mx+( x+width )*__glob_ix+y*__glob_iy, my+( x+width )*__glob_jx+y*__glob_jy )
	
	glTexCoord2f( UT, VT )
	glVertex2f( mx+( x+width )*__glob_ix+( y+height )*__glob_iy, my+( x+width )*__glob_jx+( y+height )*__glob_jy )
	
	glTexCoord2f( UF, VT )
	glVertex2f( mx+x*__glob_ix+( y+height )*__glob_iy, my+x*__glob_jx+( y+height )*__glob_jy )
	
	glEnd( )
	
	glBindTexture( GL_TEXTURE_2D, lastTex )
	If texEnabled=0 Then glDisable( GL_TEXTURE_2D )
	
	Function c_setupTrans( theta# )
		Local c#, s#
		c=Cos( theta )
		s=Sin( theta )
		__glob_ix=c
		__glob_iy=-s
		__glob_jx=s
		__glob_jy=c
	End Function
End Function
