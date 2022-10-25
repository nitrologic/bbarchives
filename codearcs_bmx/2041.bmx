; ID: 2041
; Author: computercoder
; Date: 2007-06-23 01:05:30
; Title: TWindowMode
; Description: Simple Windowed mode chooser

Rem

TWindowMode
--------------------------------------------------

This type is designed to allow the user to select a screen mode
of Windowed, Full Screen, or Quit without using the MaxGUI framework

This *could* be easily modified a bit more into a full on OOP type,
*BUT* I decided to keep it simple :)

--------------------------------------------------
USEAGE:

intVar = TWindowMode.GetMode("Your Title", "Your Info")

--------------------------------------------------
OUTPUTS:
0 = Cancelled
1 = Windowed
2 = Full Screen

End Rem

Type TWindowMode

	Const C_Cancelled:Int = 0
	Const C_Window:Int = 1
	Const C_FullScreen:Int = 2

	Function GetMode:Int(strTitle:String,strInfo:String)
		Local intWindowMode:Int = 0
		Local intWindowW:Int = 340
		Local intWindowH:Int = 100

		Local intButtonOver:Int = -1
		
		Local blnExitLoop:Byte = False
		
		Local intMouseX:Int = 0
		Local intMouseY:Int = 0
		
		Local intTitleX:Int = (intWindowW - (8 * strTitle.length)) / 2
		Local intInfoX:Int = (intWindowW - (8 * strInfo.length)) / 2
		
		Local objGraphics:TGraphics
		
		Const cWINDOW:String = "Windowed"
		Const cFULLSCREEN:String = "Full Screen"
		Const cQUIT:String = "Quit"
		
		' Create the window
		objGraphics = Graphics(intWindowW, intWindowH)

		SetClsColor(215, 215, 215)
		
		While blnExitLoop = False
			intMouseX = MouseX()
			intMouseY = MouseY()


			Cls
			SetColor(0, 0, 0)
			
			DrawText(strTitle, intTitleX, 10)
			DrawText(strInfo, intInfoX, 34)
			
			intButtonOver = -1
			If PointToBox(intMouseX, intMouseY, 10, 60, 110, 84) = True Then
				DrawButton(cWINDOW, 10, 60, 100, 24, True)
				intButtonOver = C_Window
			Else
				DrawButton(cWINDOW, 10, 60, 100, 24, False)
			End If
			
			If PointToBox(intMouseX, intMouseY, 120, 60, 220, 84) = True Then
				DrawButton(cFULLSCREEN, 120, 60, 100, 24, True)
				intButtonOver = C_FullScreen
			Else
				DrawButton(cFULLSCREEN, 120, 60, 100, 24, False)
			End If
			
			If PointToBox(intMouseX, intMouseY, 230, 60, 330, 84) = True Then
				DrawButton(cQUIT, 230, 60, 100, 24, True)
				intButtonOver = C_Cancelled
			Else
				DrawButton(cQUIT, 230, 60, 100, 24, False)
			End If
			
			If MouseHit(1) Or MouseHit(2) Or MouseHit(3) Then
				If intButtonOver <> -1 Then
					intWindowMode = intButtonOver
					blnExitLoop = True
				End If 
			End If

			Flip			
			
		Wend

		CloseGraphics(objGraphics)
		Return intWindowMode

	End Function

	Function DrawButton(strText:String, intPosX:Int, intPosY:Int, intWidth:Int, intHeight:Int, blnIsDown:Byte)
		' Draw a button
		' Get text center
		Local intTextPosX:Int = intPosX + ((intWidth - (8 * strText.length)) / 2)
		Local intTextPosY:Int = intPosY + ((intHeight - 12) / 2)
		
		' Draw background for button
		If blnIsDown = False Then
			SetColor(220, 220, 220)
		Else
			SetColor(175, 175, 175)
		End If
		
		' Draw Highlights
		DrawRect(intPosX, intPosY, intWidth, intHeight)
		SetColor(245, 245, 245)
		DrawLine(intPosX, intPosY, intPosX + intWidth, intPosY)
		DrawLine(intPosX, intPosY, intPosX, intPosY + intHeight)
		
		' Draw Shadows
		SetColor(150, 150, 150)
		DrawLine(intPosX + intWidth, intPosY + 1, intPosX + intWidth, intPosY + intHeight)
		DrawLine(intPosX + 1, intPosY + intHeight, intPosX + intWidth, intPosY + intHeight)
		
		' Draw text
		SetColor(0, 0, 0)
		DrawText(strText, intTextPosX, intTextPosY)
		
	End Function
	
	Function PointToBox:Byte(px:Int, py:Int, x1:Int, y1:Int, x2:Int, y2:Int)
		' Determines if a point is in a rect
		Local blnReturn:Byte = False
		
		If x1 <= px And x2 => px And y1 <= py And y2 => py Then
			blnReturn = True
		End If
		
		Return blnReturn
			
	End Function

End Type
