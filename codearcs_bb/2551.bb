; ID: 2551
; Author: Mahan
; Date: 2009-07-31 17:49:38
; Title: ImageTexturizer
; Description: Make a texture from smaller pictures

;
; This program helps building textures (currenty 512x512) consisting of smaller pictures.
;
; My first program ever written in Blitz+.
;
;
; The input to the program (button "Load image") are smaller pictures that fit inside the "texture-canvas". You can load as many as you need.
; The output of the program (button "Save texture") is 1) A .png file of the new texture 2) A .ini:ish file that describes the original images and their location on the texture
;
; General usage. Load images (button)
; Select an image for movement by left-clicking it.
; "Unselect" selection by pressing the right mouse button
;
;
; Disclamer: By using this software you agree that the author (Mahan) will not be held resposible, directly, indirectly or otherwise for anything that this software does or does not do.
;
; This software is public domain (PD) and you can use it as you please. 
; Mentions are welcome if you use this.
;
; - Mattias Hansson aka MaHan
;

Type TSubImage
	Field image%      		; image handle
	Field fileName$   		; images filename
	Field shortFileName$ 	; filename with stripped path
	Field cvX         		; position on canvas X. 
	Field cvY         		; position on canvas Y.
	Field sizeX       		; image width
	Field sizeY       		; image height
End Type



Function firstImageUnderXY.TSubImage(x, y)

	Local result.TSubImage=Null

	For subImage.TSubImage = Each TSubImage
		If RectsOverlap(x, y, 1, 1, subImage\cvX, subImage\cvY, subImage\sizeX, subImage\sizeY) Then
			result = subImage
			Exit
		EndIf
	Next

	Return result
End Function

Function loadSubImage(fileName$, canvas)

	If Len(fileName) > 0 Then
		DebugLog "Loading image"
		Local newLoadedImage.TSubImage=New TSubImage
		newLoadedImage\image=LoadImage(fileName)
		newLoadedImage\fileName=fileName
		newLoadedImage\sizeX=ImageWidth(newLoadedImage\image)
		newLoadedImage\sizeY=ImageHeight(newLoadedImage\image)
		newLoadedImage\cvX=GadgetWidth(canvas)-newLoadedImage\sizeX
		newLoadedImage\cvY=GadgetHeight(canvas)-newLoadedImage\sizeY
		
		; fix up the fileName
		Local i = Len(newLoadedImage\fileName)
		Local fixedFileName$=""
		While (i > 0) And (Mid(newLoadedImage\fileName, i, 1) <> "\")
			fixedFileName = Mid(newLoadedImage\fileName, i, 1) + fixedFileName
			i = i - 1
		Wend
		newLoadedImage\shortFileName = fixedFileName
		
		;check that the newly loaded picture does not overlap a previously loaded one - if it does, tell and abort
		If newSubImageCoordsForcesImageOverlap(newLoadedImage, newLoadedImage\cvX, newLoadedImage\cvY)
			Notify "Cannot load this image since it overlaps an existing image. Clear space in the lower right area please", True
			FreeImage(newLoadedImage\image)
			Delete newLoadedImage
		EndIf
		
	EndIf
	
End Function

Function newSubImageCoordsWithinCanvas(subImage.TSubImage, canvas, x, y)
	If (x >= 0) And (y >= 0) And (x <= (GadgetWidth(canvas) - subImage\sizeX)) And (y <= (GadgetHeight(canvas) - subImage\sizey)) Then Return True
	Return False
End Function

Function newSubImageCoordsForcesImageOverlap(subImage.TSubImage, x, y)

	Local otherImage.TSubImage
	For otherImage = Each TSubImage
		If otherImage <> subImage Then
			If RectsOverlap(x, y, subImage\sizeX, subImage\sizeY, otherImage\cvX, otherImage\cvY, otherImage\sizeX, otherImage\sizeY) Then Return True
		EndIf
	Next
	Return False
End Function

Function moveSelected(selected.TSubImage, canvas, label)
	If selected <> Null
		Local newX=MouseX(canvas)
		Local newY=MouseY(canvas)
		If newSubImageCoordsWithinCanvas(selected, canvas, newX, newY) And (Not newSubImageCoordsForcesImageOverlap(selected, newX, newY))
			selected\cvX=newX
			selected\cvY=newY
			SetGadgetText(label, "x=" + Str(selected\cvX) + " y=" + Str(selected\cvY))
		EndIf
	EndIf
End Function

Function saveTexture(fileName$, canvas)


	; first save the texture
	Local w = GadgetWidth(canvas) 
	Local h = GadgetHeight(canvas) 
	Local texture = CreateImage(w, h)
	render(Null) ; render the canvas before the copy
	CopyRect 0,0,w,h,0,0,CanvasBuffer(canvas),ImageBuffer(texture)
	SaveImage texture, fileName + ".png" 

	; then save a description textfile (for possible external use) - quite common .ini-file format
	Local outFile=WriteFile(fileName + ".tdc") ; tdc = Texture DesCription
	If outFile Then
		Local subImage.TSubImage
		For subImage = Each TSubImage
			WriteLine(outFile, "[Image " + subImage\shortFileName + "]")
			WriteLine(outFile, "FileName=" + subImage\fileName)
			WriteLine(outFile, "ShortFileName=" + subImage\shortFileName)
			WriteLine(outFile, "cvX="+Str(subImage\cvX))
			WriteLine(outFile, "cvY="+Str(subImage\cvY))
			WriteLine(outFile, "sizeX="+Str(subImage\sizeX))
			WriteLine(outFile, "sizeY="+Str(subImage\sizeY))
			WriteLine(outFile, "")
		Next
		CloseFile(outFile)
	EndIf
End Function

Function render(selected.TSubImage)
	For subImage.TSubImage=Each TSubImage
		If subImage<>selected Then
			DrawImage(subImage\image, subImage\cvX, subImage\cvY)
		Else
			DrawImage(subImage\image, subImage\cvX, subImage\cvY)
			Color(255, 0, 0) ; red
			Rect(subImage\cvX, subImage\cvY, subImage\sizeX, subImage\sizeY, False) 
		EndIf
	Next
End Function


Function main()

	Local win=CreateWindow("Image Texturizer", 20, 20, 660, 640)
	Local canvas=CreateCanvas(10, 10, 512, 512, win)
	Local btLoad=CreateButton("Load image", GadgetX(canvas) + GadgetWidth(canvas) + 20, 10, 100, 30, win)
	Local btSaveTex=CreateButton("Save texture", GadgetX(btLoad), GadgetY(btLoad) + GadgetHeight(btload) + 10, 100, 30, win)
	Local lbImagePos=CreateLabel("x=? y=?", 10, GadgetY(canvas) + GadgetHeight(canvas), 200, 20, win)
	Local fileName$=""
	Local subImage.TSubImage
	Local selected.TSubImage 
	Local event
	
	SetBuffer(CanvasBuffer(canvas))
	
	Repeat 
		Cls
		;If WaitEvent()=$803 Then Exit
		
		event=WaitEvent()
		Select event
			Case $803 ; close window event
				Exit
			Case $401 ; gadget interacted (like button pressed)
				Select EventSource()
					Case btLoad
						loadSubImage(RequestFile(), canvas)
					Case btSaveTex
						saveTexture(RequestFile("", "", True), canvas)
				End Select 
			Case $201 ; mouse down
				Select EventData()
					Case 1 ; Left putton pressed
					
					selected=firstImageUnderXY(MouseX(canvas), MouseY(canvas))
					Case 2 ; right button pushed
						selected = Null
				End Select
			Case $203 ; mouse move
				If EventSource()=canvas
					;if selected then move image around with the mouse (if over canvas)
					moveSelected(selected, canvas, lbImagePos)
				EndIf
			Default
				; DebugLog Hex(event)
		End Select 
	
	
		;DebugLog "Oh, hai!" + Handle(First TSubImage)
		
		render(selected)		
		
		FlipCanvas(canvas)
	Forever 
	
End Function

main()
