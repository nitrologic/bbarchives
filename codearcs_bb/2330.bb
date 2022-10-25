; ID: 2330
; Author: Matthew Smith
; Date: 2008-10-08 11:37:09
; Title: C64 Sprite Dumper
; Description: Dumps sprites from c64 screen captures

;--------------------------------------------------------------------------------------------------
; C64 Sprite Dumper
; v1.1
; Matthew Smith 2008, 2011
;
; This tool grabs c64 sprite dump images from the Action Reply 7.5 cartridge using CCS64
; It's a good tool to grab images if you are doing a remake!
;
; All you need to do is screen dump the required images (Alt+F2), copy the files into the same 
; folder as the tool and run.  It will scan all files for images including the 'double size' 
; image in the centre and put them into a single file.
;
; Notes:
; Dump images need to be sequential ie. ccs0.bmp, ccs1.bmp, ccs2.bmp.  Once a dump file is not
; found the dumping will finish and exit
;
; Default screen size is 320x240, alternate screen size is 384x282
; This appears to have changed with later version of CCS64
;--------------------------------------------------------------------------------------------------

Global fileNameTemp$ = "ccs%.bmp"
Global fileName$ =""
Global fileNameIndex% = 0
Global spriteSizeX% = 24
Global spriteSizeY% = 21
Global spriteCounter% = 0
Global pasteX% = 0
Global pasteY% = 0
Global dumpImage
Global finalImage
Global spriteImage

Type Sprite
	Field OffsetX%	
	Field OffsetY%
	Field Rescale%
	Field SizeX%
	Field SizeY%
	
End Type

;Initialise array
GetSpriteOffset()

;Initialise screen
Graphics(800,600,16,0)

;Prepare image
finalImage = CreateImage(spriteSizeX * 24, spriteSizeY * 10)

;Loop
Repeat
	;Get next image and validate it exists
	fileName = Replace(fileNameTemp, "%", fileNameIndex)
	If (FileType(fileName) = 0) Then Exit
	
	;Load next image and display
	SetBuffer(FrontBuffer())
	Cls
	dumpImage = LoadImage(fileName)
	DrawImage(dumpImage,0,0)
	
	;Copy from dump to final
	For spr.Sprite = Each Sprite
		Local imageOffsetX% = 0
		Local imageOffsetY% = 0
		
		;Get alternate offset (based on size of image loaded)
		If (ImageWidth(dumpImage) = 384 And ImageHeight(dumpImage) = 282) Then
			imageOffsetX = 32
			imageOffsetY = 22
			
		End If
		
		;Copy dump image
		SetBuffer(FrontBuffer())
		spriteImage = CreateImage(spr\SizeX,spr\SizeY)
		GrabImage(spriteImage, spr\OffsetX + imageOffsetX, spr\OffsetY + imageOffsetY)
		
		;Rescale dump image?
		If (spr\Rescale = 1) Then ResizeImage(spriteImage, spriteSizeX, spriteSizeY)
		
		;Paste image into final 
		SetBuffer(ImageBuffer(finalImage))
		DrawImage(spriteImage, pasteX, pasteY)
		
		;Update paste into final position
		pasteX = pasteX + spriteSizeX
		If (pasteX >= ImageWidth(finalImage)) Then
			;Move to next line
			pasteX = 0
			pastey = pasteY + spriteSizeY
			
		End If
		
		;Increment
		spriteCounter = spriteCounter + 1
		
	Next
	
	;Get next image
	fileNameIndex = fileNameIndex + 1
	
Forever

;Save final image?
If(spriteCounter > 0) Then SaveImage(finalImage, "Images.bmp")

;Finish
EndGraphics
End

;#Region " SpriteOffset "

Function GetSpriteOffset()
	Local offsetX%, offsetY%, rescale%
	Local index%, total%
	
	;Prepare
	Restore SpriteOffset
	Read total
	
	;Process
	For index = 0 To total - 1
		Read offsetX, offsetY, rescale
		
		;Create sprite item
		spr.Sprite = New Sprite
		spr\OffsetX = offsetX
		spr\OffsetY = offsetY
		spr\Rescale = rescale
		spr\SizeX = (spriteSizeX * (rescale + 1))
		spr\SizeY = (spriteSizeY * (rescale + 1))
		
	Next
	
End Function

.SpriteOffset
Data 7						;Total
Data 8,41,0					;X,Y,Rescale
Data 48,41,0
Data 88,41,0
Data 136,29,1
Data 208,41,0
Data 248,41,0
Data 288,41,0

;#End Region
