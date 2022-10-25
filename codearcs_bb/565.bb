; ID: 565
; Author: sswift
; Date: 2003-02-01 14:12:10
; Title: Font Texture Creator
; Description: Creates textures for fonts spanning multiple textures if desired.

; -------------------------------------------------------------------------------------------------------------------
; Font Texture Creator - Copyright 2003 Shawn C. Swift - sswift@earthlink.net
; -------------------------------------------------------------------------------------------------------------------

Graphics 640,480,32,1
TFormFilter True 		


	Global ANTIALIAS_FONT = False 				; Enable antialiasing on the font.
												;
												; It is suggested that rather than using this internal antialiasing,
												; you simply set the scaling factor to 2x-4x, and afterwards scale
												; the texture down in Photoshop.  This is because Photoshop's bicubic
												; filtering can do a much better job of antialiasing than the
												; bilinear filtering built into Blitz.

	Global SCALE_FACTOR = 4						; Use this to easily scale the textures and font up by a specific amount.
												

	; This is the set of characters which will be included in the font textures.  
	Global CHARACTER_SET$ = "!" + Chr$(34) + "#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\]^_`abcdefghijklmnopqrstuvwxyz{|}~‘’¡¢£¤¥¦§©ª«¬­®¯°±´µ¶·¸º»¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ"

	Global TEXTURE_RESOLUTION = 256				; Must be a power of 2.  (256 reccomended)
	Global CHARACTER_RESOLUTION = 32			; Must be a power of 2 <= TEXTURE_RESOLUTION.	

	Global FONT_NAME$ = "Arial Black"				
	Global FONT_HEIGHT = 28						; Should be less than CHARACTER_RESOLUTION to avoid errors in the image.
	Global FONT_BOLD = False						
	Global FONT_ITALIC = False
	Global FONT_UNDERLINE = False

	Texture_Counter = 0							; Keeps track of how many textures have been saved.


.Main 
	
	; Calculate stuff we need to know to create the textures.

		Total_Characters 		= Len(CHARACTER_SET$)
		Characters_Per_Line 	= TEXTURE_RESOLUTION / CHARACTER_RESOLUTION
		Characters_Per_Texture	= Characters_Per_Line^2
		Total_Textures 			= Ceil(Float(Total_Characters) / Float(Characters_Per_Texture))

		; Create a multiplier to adjust the scale of things to take antialising into account.
		AA = 1
		If ANTIALIAS_FONT Then AA = 2
	
		; Take the texture scaling factor into account.
		AA = AA * SCALE_FACTOR


	; Load and set the font.
		Font01 = LoadFont(FONT_NAME$, FONT_HEIGHT*AA, FONT_BOLD, FONT_ITALIC, FONT_UNDERLINE) 
		SetFont Font01


	; Loop through each character in the character set.
	For LOOP_Character = 0 To (Total_Characters-1)
				
		; If we are at the first character of a new image...
		If (LOOP_Character Mod Characters_Per_Texture) = 0
		
			; Create a new image.
			ThisImage = CreateImage(TEXTURE_RESOLUTION*AA, TEXTURE_RESOLUTION*AA)
			
			; Set all drawing operations to draw to this image.
			SetBuffer ImageBuffer(ThisImage)
			
		EndIf
				
		; Get the next character to draw.
		ThisChar$ = Mid$(CHARACTER_SET$, LOOP_Character+1, 1)
				
		; Calculate the offset in the image which the character should be drawn.
		OffsetX = (LOOP_Character Mod Characters_Per_Line) * (CHARACTER_RESOLUTION*AA)		
		OffsetY = ((LOOP_Character Mod Characters_Per_Texture) / Characters_Per_Line) * (CHARACTER_RESOLUTION*AA)			
		
		; Print the character centered in the appropriate region.
		Text OffsetX+(CHARACTER_RESOLUTION*AA)/2, OffsetY+(CHARACTER_RESOLUTION*AA)/2, ThisChar$, True, True
		
		; If this is the last character in this texture...
		If (LOOP_Character Mod Characters_Per_Texture) = (Characters_Per_Texture-1)

			; Increment the texture counter.
			Texture_Counter = Texture_Counter + 1
	
			; Save the texture.
			SaveTexture(ThisImage, Texture_Counter)			
				
		EndIf
					
	Next
		
	; If after the loop is completed we were still adding characters to the last texture...
	If (Total_Characters Mod Characters_Per_Texture) > 0
		
		; Increment the texture counter.
		Texture_Counter = Texture_Counter + 1
	
		; Save the texture.
		SaveTexture(ThisImage, Texture_Counter)			
			
	EndIf	
	
End	

; -------------------------------------------------------------------------------------------------------------------

Function SaveTexture(ThisImage, TextureNum)

	; If the font is antialiased, scale the texture down to do the 4x AA before saving it.
	If ANTIALIAS_FONT 
		ResizeImage	ThisImage, TEXTURE_RESOLUTION*SCALE_FACTOR, TEXTURE_RESOLUTION*SCALE_FACTOR
	EndIf

	; Create the filename for this texture.	
	Font_Filename$ = Replace$(FONT_NAME$, " ", "") + Str$(FONT_HEIGHT)
	
	If FONT_BOLD      Then Font_Filename$ = Font_Filename$ + "b"
	If FONT_ITALIC    Then Font_Filename$ = Font_Filename$ + "i"
	If FONT_UNDERLINE Then Font_Filename$ = Font_Filename$ + "u"

	Font_Filename$ = Font_Filename$ + "-" + Str$(TextureNum) + ".bmp"

	; Save the texture.
	SaveImage(ThisImage, Font_FileName$) 
	
End Function
