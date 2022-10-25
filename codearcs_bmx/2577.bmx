; ID: 2577
; Author: AndrewT
; Date: 2009-09-01 10:22:12
; Title: Bitmap Font Library
; Description: Library for displaying bitmap fonts.

'***********************************************************************************************************************************
'                                        B I T M A P   F O N T   L I B R A R Y
'***********************************************************************************************************************************
Rem

This library will allow you To draw bitmap fonts obtained from bitmap font image.

How To use it:

	-First, Load a font like so:
		
		Local Font:TBitmapFont = TBitmapFont.Load(FileName)
		
	where FileName is the filename of a bitmap font image.
	
	-Then draw text with the Draw() method:
	
		Font.Draw("Hello, world!", 100, 100)
		
	You can change the scale and rotation of the font as well, using SetScale() and SetAngle().
	
	You can also change the spacing of the font using SetSpacing(). You may need to set the spacing
	to a negative value in order to obtain the appearance you desire.
	
	You can also change the handle of the font with SetHandle(). The handle of the font is the center
	at which the font is rotated and scaled. Additionally you can center the font using SetCentered() and setting
	it to 1, which will automatically set the handle in the center of any text you draw.
		
EndRem
''***********************************************************************************************************************************

Type TBitmapFont

	Field Image:TImage
	
	Field Offset:Int[190]
	Field Spacing:Float
	
	Field CellWidth:Int
	Field CellHeight:Int
	
	Field HandleX:Float
	Field HandleY:Float
	Field Angle:Float
	Field ScaleX:Float
	Field ScaleY:Float
	Field Centered:Int
	
	Method GetWidth:Int(Text:String)
		Local CurIndex:Int
		Local CurWidth:Int
		
		For Local I:Int = 1 To Len(Text)
			CurIndex = Asc(Mid(Text, I, 1)) - 32
			CurWidth :+ (Offset[CurIndex * 2 + 1] - Offset[CurIndex * 2]) + Spacing
		Next
	
		Return CurWidth
	EndMethod

	Method GetHeight:Int()
		Return ImageHeight(Image)
	EndMethod
	
	Method SetSpacing(Space:Float)
		Spacing = Space
	EndMethod
	
	Method GetSpacing:Int()
		Return Spacing
	EndMethod
	
	Method SetHandle(X:Float, Y:Float)
		HandleX = X
		HandleY = Y
	EndMethod
	
	Method GetHandle(X:Float Var, Y:Float Var)
		X = HandleX
		Y = HandleY
	EndMethod
	
	Method SetCentered(IsCentered:Int)
		Centered = IsCentered
	EndMethod
		
	
	Method SetAngle(Ang:Float)
		Angle = Ang
	EndMethod
	
	Method GetAngle:Float()
		Return Angle
	EndMethod
	
	Method SetScale(X:Float, Y:Float)
		ScaleX = X
		ScaleY = Y
	EndMethod
	
	Method GetScale(X:Float Var, Y:Float Var)
		X = ScaleX
		Y = ScaleY
	EndMethod
	
	Method Draw(Text:String, X:Int, Y:Int)
		SetTransform(Angle, ScaleX, ScaleY)
	
		Local CurIndex:Int
		Local OrigX:Int = X
		
		Local OldHX:Float
		Local OldHY:Float
		
		If Centered
			OldHX = HandleX
			OldHY = HandleY
			HandleX = GetWidth(Text) / 2
			HandleY = GetHeight() / 2
		EndIf
		
		For Local I:Int = 1 To Len(Text)
			CurIndex = Asc(Mid(Text, I, 1)) - 32
			SetImageHandle(Image, -((X - Offset[CurIndex * 2]) - (OrigX + HandleX)),  HandleY)
			DrawImage(Image, OrigX, Y, CurIndex)
			X :+ Offset[CurIndex * 2 + 1] - Offset[CurIndex * 2]
			X :+ Spacing
		Next
		
		HandleX = OldHX
		HandleY = OldHY
		
	EndMethod
	
	Method Trim()
		Local Pixmap:TPixmap
                Pixmap = Pixmap.Convert(PF_RGBA8888)
	
		For Local I:Int = 0 To 94
		
			Pixmap = LockImage(Image, I)
			Pixmap = Pixmap.Convert(PF_RGBA8888)
			
			Local LeftMax:Int = CellHeight - 1
			Local RightMax:Int = 0
			
			For Local Y:Int = 0 To CellHeight - 1
				For Local X:Int = 0 To CellWidth - 1
					Local Color:Int = Pixmap.ReadPixel(X, Y)
					If Color Shr 24 & $000000FF > 128
						If X < LeftMax
							LeftMax = X
						EndIf
						Exit
					EndIf
				Next
			Next
			
			For Local Y:Int = 0 To CellHeight - 1
				For Local X:Int = CellWidth - 1 To 0 Step -1
					Local Color:Int = Pixmap.ReadPixel(X, Y)
					If Color Shr 24 & $000000FF > 128
						If X > RightMax
							RightMax = X
						EndIf
						Exit
					EndIf
				Next
			Next
			
			If RightMax = 0
				RightMax = CellWidth - CellWidth / 3
			EndIf
			If LeftMax = CellWidth - 1
				LeftMax = CellWidth / 3
			EndIf
			
			LeftMax = LeftMax - Spacing / 2
			RightMax = RightMax + Spacing / 2
			
			If RightMax > CellWidth - 1
				RightMax = CellWidth - 1
			EndIf
			If LeftMax < 0
				LeftMax = 0
			EndIf
			
			Offset[I * 2] = LeftMax
			Offset[I * 2 + 1] = RightMax
			
			UnlockImage(Image, I)
		
		Next
	EndMethod

	Function Load:TBitmapFont(FileName:String)
		Local Font:TBitmapFont = New TBitmapFont
		
		Local FontImage:TImage = LoadImage(LoadBank(FileName))
		
		Local CellWidth:Int = ImageWidth(FontImage) / 10
		Local CellHeight:Int = ImageHeight(FontImage) / 10
		
		FontImage = LoadAnimImage(LoadBank(FileName), CellWidth, CellHeight, 0, 95)
		
		If Not FontImage
			Notify("The bitmap font image you specified does not exist, or is not of a supported file format.", True)
			End
		EndIf		
		Font.Image = FontImage
		Font.CellWidth = CellWidth
		Font.CellHeight = CellHeight
		Font.ScaleX = 1.0
		Font.ScaleY = 1.0
		
		Font.Trim()
		
		Return Font
	EndFunction
	
EndType
