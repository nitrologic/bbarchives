; ID: 1565
; Author: ozak
; Date: 2005-12-14 06:01:45
; Title: Bitmap font class, for use with Bitmap Font Builder
; Description: Bitmap font class

' Bitmap font class by Odin Jensen (www.furi.dk)
' Free to use as you please :)

' Bitmap font class
Type BitmapFont

	' Actual image used in font
	Field Image:TImage
	
	' Font offsets
	Field Offsets:Int[256]
		
	' Load font
	Method Load(URL:Object, Width:Int, Height:Int, MaxChars:Int)
	
		' Load image
		Image = LoadAnimImage(URL, Width, Height, 0, MaxChars)				
		
		' Create new file name
		Local OffsetFileName:String = URL.toString()
		OffsetFileName = OffsetFileName[0..OffsetFileName.Length -4] + ".ini"
		
		
		' Load image offsets		
		Local File:TStream = OpenStream(OffsetFileName)
		
		' Read offsets
		Local CurOffset = 0
		Local Temp:String
		ReadLine(File)
		While Not Eof(File)
		
			' Read current line
			Temp = ReadLine(File)
			
			' Split it
			Temp = Temp[Temp.Find("=")+1..Temp.Length]
			
			' Add to list
			Offsets[CurOffset] = Temp.ToInt()	
			CurOffset = CurOffset + 1		

		Wend
		
		' Close stream
		CloseStream(File)						

	EndMethod
	
	' Draw font
	Method Draw(X:Int, Y:Int, Text:String)
	
		' Store X locally so we can safely modify
		Local tx = X
		
		' Loop through it
		For Local i:Int= 0 To Text.length-1

			DrawImage(Image, tx, Y, Text[i])
			tx = tx + Offsets[Text[i]]
		Next
	
	EndMethod
	
	' Get length of string
	Method GetLength:Int(Text:String)
	
		Local Size:Int = 0;
		
		' Loop through it
		For Local i:Int= 0 To Text.length-1

			size = Size + Offsets[Text[i]]
		Next
		
		Return Size
	
	EndMethod	
	
	' Get image for manipulation
	Method GetImage:TImage()
	
		Return Image
	
	EndMethod


EndType
