; ID: 3065
; Author: JesseJoh
; Date: 2013-08-20 22:06:48
; Title: (Blitz3dSDK) PNG Texture Colorizer And Alpha Modifier
; Description: Modify R/G/B + Alpha (Colorize) for PNG Textures! Even colorizes alpha channeled textures!

' Usage
' modify_texture_rgba( <input texture>, <r modifier (-255 to 255)>, <g modifier (-255 to 255)>, <b modifier (-255 to 255)>, <a modifier (-255 to 255)> )
'
' For example, if you entered -20 for the green and blue modifiers, your texture would get redder.
Function modify_texture_rgba(texture, r_modifier, g_modifier, b_modifier, a_modifier)
	
	' Lock texture buffer for fast operation
	bbLockBuffer(bbTextureBuffer(texture))
	
	' Loop through the texture
	For Local y = 0 To bbTextureHeight(texture) - 1
		For Local x = 0 To bbTextureWidth(texture) - 1
		
			' Get RGBA
			Local rgba_value = bbReadPixelFast(x, y, bbTextureBuffer(texture))
			
			' Get individual values
			Local a = (rgba_value Shr 24) & $FF
			Local r = (rgba_value Shr 16) & $FF
			Local g = (rgba_value Shr 8) & $FF
			Local b = rgba_value & $FF
			
			' Red modifications
			If r_modifier > 0 Then
				If r + r_modifier < 255 Then
					r = r + r_modifier
				Else
					r = 255
				End If
			End If
			If r_modifier < 0 Then
				If r + r_modifier > 0 Then
					r = r + r_modifier
				Else
					r = 0
				End If
			End If
			
			' Green modifications
			If g_modifier > 0 Then
				If g + g_modifier < 255 Then
					g = g + g_modifier
				Else
					g = 255
				End If
			End If
			If g_modifier < 0 Then
				If g + g_modifier > 0 Then
					g = g + g_modifier
				Else
					g = 0
				End If
			End If
			
			' Blue modifications
			If b_modifier > 0 Then
				If b + b_modifier < 255 Then
					b = b + b_modifier
				Else
					b = 255
				End If
			End If
			If b_modifier < 0 Then
				If b + b_modifier > 0 Then
					b = b + b_modifier
				Else
					b = 0
				End If
			End If
			
			' Alpha modifications
			If a_modifier > 0 Then
				If a + a_modifier < 255 Then
					a = a + a_modifier
				Else
					a = 255
				End If
			End If
			If a_modifier < 0 Then
				If a + a_modifier > 0 Then
					a = a + a_modifier
				Else
					a = 0
				End If
			End If
		
			' Make it RGBA Again
			Local new_rgba = (a Shl 24 | r Shl 16 | g Shl 8 | b)
			
			' Write the pixel to the texture
			bbWritePixelFast(x, y, new_rgba, bbTextureBuffer(texture))
	
		Next
	Next
	
	' Unlock the texture buffer
	bbUnlockBuffer(bbTextureBuffer(texture))

End Function
