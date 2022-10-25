; ID: 3075
; Author: JesseJoh
; Date: 2013-09-12 18:47:12
; Title: (Blitz3dSDK) Combine alpha textures
; Description: This is a function to combine multiple textures together. Much faster to do this at game load and use a single texture than it is to use hardware multi-texturing

Function add_texture_to_texture(destination_texture, add_texture)
	
	Local texture_width = bbTextureHeight(add_texture)
	Local texture_height = bbTextureWidth(add_texture)
	
	texture_width = texture_width - 1
	texture_height = texture_height - 1
	
	bbLockBuffer(bbTextureBuffer(destination_texture))
	bbLockBuffer(bbTextureBuffer(add_texture))
	
	For Local y = 0 To texture_width
		For Local x = 0 To texture_height
		
			
			Local rgba_value_src = bbReadPixelfast(x, y, bbTextureBuffer(add_texture))
			
			Local a_src = (rgba_value_src Shr 24) & $FF
			Local r_src = (rgba_value_src Shr 16) & $FF
			Local g_src = (rgba_value_src Shr 8) & $FF
			Local b_src = rgba_value_src & $FF
			
			If a_src = 0 Then Continue
			
			Local masking_factor:Float = Float(a_src) / Float(255)
			
			
			Local rgba_value_dest = bbReadPixel(x, y, bbTextureBuffer(destination_texture))
			
			Local a_dest = (rgba_value_dest Shr 24) & $FF
			Local r_dest = (rgba_value_dest Shr 16) & $FF
			Local g_dest = (rgba_value_dest Shr 8) & $FF
			Local b_dest = rgba_value_dest & $FF
			
			Local new_a = a_dest * (1 - masking_factor) + a_src * masking_factor
			Local new_r = r_dest * (1 - masking_factor) + r_src * masking_factor
			Local new_g = g_dest * (1 - masking_factor) + g_src * masking_factor
			Local new_b = b_dest * (1 - masking_factor) + b_src * masking_factor
			
			
			Local new_rgba = (new_a Shl 24 | new_r Shl 16 | new_g Shl 8 | new_b)
			
			bbWritePixelFast(x, y, new_rgba, bbTextureBuffer(destination_texture))
			bbBufferDirty(bbTextureBuffer(destination_texture))
		Next
	Next
	
	bbUnlockBuffer(bbTextureBuffer(add_texture))
	bbUnlockBuffer(bbTextureBuffer(destination_texture))

End Function
