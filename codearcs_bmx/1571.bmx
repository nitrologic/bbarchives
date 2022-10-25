; ID: 1571
; Author: deps
; Date: 2005-12-21 20:21:11
; Title: Simple screenshot function
; Description: Takes a screenshot and saves it as PNG

Function take_screenshot()

	Local filename:String, padded:String
	Local num:Int = 0

	padded = num
	While padded.length < 3 
		padded = "0"+padded
	Wend
	filename = "screen"+padded+".png"
	
	While FileType(filename) <> 0
		num:+1

		padded = num
		While padded.length < 3 
			padded = "0"+padded
		Wend
		filename = "screen"+padded+".png"
	Wend

	Local img:tpixmap = GrabPixmap(0,0,GraphicsWidth(),GraphicsHeight())

	SavePixmapPNG(img, filename )
	
	Print "Screenshot saved as "+filename

EndFunction
