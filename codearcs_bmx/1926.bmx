; ID: 1926
; Author: TomToad
; Date: 2007-02-14 23:39:06
; Title: Icon Type
; Description: Type for using icons in BlitzMAX app

'IconType.bmx
Type TIcon
	Global IconList:TList = Null
	Field ID:Int
	Field X:Int
	Field Y:Int
	Field Width:Int
	Field Height:Int
	Field Image:TImage
	
	'Call this function for each icon.  Passing it an image, x and y location, and a unique ID
	Function create(Image:TImage,X:Int,Y:Int,ID:Int)
		Local Icon:TIcon = New TIcon
		
		If IconList = Null Then IconList = CreateList()
		Icon.X = X
		Icon.Y = Y
		Icon.Image = Image
		Icon.Width = ImageWidth(Image)
		Icon.Height = ImageHeight(Image)
		Icon.ID = ID
		ListAddLast(IconList,Icon)
	End Function
	
	'Call this function in the main loop to draw the Icons to the screen
	Function Draw()
		If IconList = Null Then Return
		
		For Local Icon:TIcon = EachIn IconList
			DrawImage Icon.Image,Icon.X,Icon.Y
		Next
	End Function
	
	'This function will return the ID of the icon located at X and Y. 
	' Usually you would pass the mouse pointer's X and Y location to the function
	' Returns 0 if no icon is selected.
	Function Collide:Int(X:Int,Y:Int)
		If IconList = Null Then Return 0
		
		For Local Icon:TIcon = EachIn IconList
			If X >= Icon.X And X < Icon.X+Icon.Width And Y >= Icon.Y And Y < Icon.Y+Icon.Width
				Return Icon.ID
			End If
		Next
		
		Return 0
	End Function
End Type
