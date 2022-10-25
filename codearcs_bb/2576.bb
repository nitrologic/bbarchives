; ID: 2576
; Author: LedgerARC
; Date: 2009-08-29 22:52:07
; Title: 3D, 2D Images
; Description: A Simple 3D imaging system

;////////////////////////////////////////////////////////////////////////////////
;THIS SHOWS THE FUNCTIONALITY AND SPEED
;///////////////////////////////////////////////////////////////////////////////

Graphics3D 1024,768,0,2
SetBuffer BackBuffer()

Global Camera = CreateCamera()
SetDefault3DImagingCamera(Camera)

Local Image3D
Local HandleX
Local HandleY
Local DrawX
Local DrawY
Local SizeX
Local SizeY
Local Count
Local FrameRate
Local FrameTime
Local UpdateFrameRate
Local UpdateFrameRateDelay = 500
Local ImageUse = 1

FR3DAlwaysRefreshImages = True

AutoMidHandle3D(False)
Image3D = LoadImage3D("image1.png")
HandleX = ImageWidth3D(Image3D)/2
HandleY = ImageHeight3D(Image3D)/2
HandleImage3D(Image3D,HandleX,HandleY)

Dim Image2D(5)
For i = 1 To 5
	Image2D(i) = LoadImage("Image"+i+".png")
Next

While Not KeyDown(1)
	
	DrawX = MouseX()
	DrawY = MouseY()
	
	DrawImage3D(Image3D,DrawX,DrawY)
	
	If MouseDown(1)
		ScaleImage3D(Image3D,1.01,1.01)
	EndIf
	If MouseDown(2)
		ScaleImage3D(Image3D,.99,.99)
	EndIf
	If KeyHit(28)
		FlushKeys()
		Locate 0,GraphicsHeight()-FontHeight()*2
		SizeX = Input("Enter image width in pixels ")
		Locate 0,GraphicsHeight()-FontHeight()
		FlushKeys()
		SizeY = Input("Entity image height in pixels ")
		FlushKeys()
		ResizeImage3D(Image3D,SizeX,SizeY)
	EndIf
	If KeyDown(200)
		SetImageAlpha3D(Image3D,ImageAlpha3D#(Image3D)+.01)
	EndIf
	If KeyDown(208)
		SetImageAlpha3D(Image3D,ImageAlpha3D#(Image3D)-.01)
	EndIf
	If KeyHit(57)
		ScaleImage3D(Image3D,-1,-1)
	EndIf
	If KeyDown(203)
		RotateImage3D(Image3D,ImageRotation3D#(Image3D)+1)
	EndIf
	If KeyDown(205)
		RotateImage3D(Image3D,ImageRotation3D#(Image3D)-1)
	EndIf
	If KeyHit(16)
		HandleImage3D(Image3D,0,0)
	EndIf
	If KeyDown(17)
		HandleImage3D(Image3D,ImageXHandle3D(Image3D),ImageYHandle3D(Image3D)+1)
	EndIf
	If KeyHit(18)
		MidHandle3D(Image3D)
	EndIf
	If KeyDown(30)
		HandleImage3D(Image3D,ImageXHandle3D(Image3D)+1,ImageYHandle3D(Image3D))
	EndIf
	If KeyDown(31)
		HandleImage3D(Image3D,ImageXHandle3D(Image3D),ImageYHandle3D(Image3D)-1)
	EndIf
	If KeyDown(32)
		HandleImage3D(Image3D,ImageXHandle3D(Image3D)-1,ImageYHandle3D(Image3D))
	EndIf
	If KeyDown(46)
		Image3D = CopyImage3D(Image3D)
	EndIf
	If KeyDown(47)
		Free.FR3DImage = First FR3DImage
		If Free.FR3DImage <> Last FR3DImage
			FreeImage3D(Free\Img3D)
		EndIf
	EndIf
	If KeyDown(45)
		TileImage3D(Image3D)
	EndIf
	If KeyHit(44)
		FreeImage3D(Image3D,True,True)
		ImageUse = ImageUse + 1
		If ImageUse > 5 Then ImageUse = 1
		Image3D = Make2D3D(Image2D(ImageUse))
	EndIf
	
	RenderWorld()
	Update3DImages()
	
	For LoopCount.FR3DImage = Each FR3DImage
		Count = Count + 1
	Next
	
	Text ImageXCorner3D(Image3D,DrawX,1),ImageYCorner3D(Image3D,DrawY,1),"<---"+ImageXCorner3D(Image3D,DrawX,1)+" * "+ImageYCorner3D(Image3D,DrawY,1)+" Pixels",False,True
	Text ImageXCorner3D(Image3D,DrawX,2),ImageYCorner3D(Image3D,DrawY,2),"<---"+ImageXCorner3D(Image3D,DrawX,2)+" * "+ImageYCorner3D(Image3D,DrawY,2)+" Pixels",False,True
	Text ImageXCorner3D(Image3D,DrawX,3),ImageYCorner3D(Image3D,DrawY,3),"<---"+ImageXCorner3D(Image3D,DrawX,3)+" * "+ImageYCorner3D(Image3D,DrawY,3)+" Pixels",False,True
	Text ImageXCorner3D(Image3D,DrawX,4),ImageYCorner3D(Image3D,DrawY,4),"<---"+ImageXCorner3D(Image3D,DrawX,4)+" * "+ImageYCorner3D(Image3D,DrawY,4)+" Pixels",False,True
	
	Text ImageXCorner3D(Image3D,DrawX,0),ImageYCorner3D(Image3D,DrawY,0),"Image Width = "+ImageWidth3D(Image3D),True,False
	Text ImageXCorner3D(Image3D,DrawX,0),ImageYCorner3D(Image3D,DrawY,0)+FontHeight()*-3,"Number of images loaded = "+Count,True,False
	Text ImageXCorner3D(Image3D,DrawX,0),ImageYCorner3D(Image3D,DrawY,0)+FontHeight()*-2,"Image X Handle = "+ImageXHandle3D(Image3D),True,False
	Text ImageXCorner3D(Image3D,DrawX,0),ImageYCorner3D(Image3D,DrawY,0)+FontHeight()*-1,"Image Y Handle = "+ImageYHandle3D(Image3D),True,False
	Text ImageXCorner3D(Image3D,DrawX,0),ImageYCorner3D(Image3D,DrawY,0)+FontHeight(),"Image Height = "+ImageHeight3D(Image3D),True,False
	Text ImageXCorner3D(Image3D,DrawX,0),ImageYCorner3D(Image3D,DrawY,0)+FontHeight()*2,"Image Alpha# = "+ImageAlpha3D(Image3D),True,False
	Text ImageXCorner3D(Image3D,DrawX,0),ImageYCorner3D(Image3D,DrawY,0)+FontHeight()*3,"Image Rotation = "+ImageRotation3D(Image3D),True,False
	
	Text 0,0,"Click Left mouse button to scale up"
	Text 0,FontHeight(),"Click Right mouse button to scale down"
	Text 0,FontHeight()*2,"Press Enter to enter specific scale amount"
	Text 0,FontHeight()*3,"Press 'Up' to increase image alpha"
	Text 0,FontHeight()*4,"Press 'Down' to decrease image alpha"
	Text 0,FontHeight()*5,"Press Spacebar to flip image"
	Text 0,FontHeight()*6,"Press 'Left' to rotate left"
	Text 0,FontHeight()*7,"Press 'Right' to rotate right"
	Text 0,FontHeight()*8,"Use W,A,S,D to change image X and Y handles"
	Text 0,FontHeight()*9,"Use E for middle handle, and Q for 0,0 handle"
	Text 0,FontHeight()*10,"Hold C to duplicate image"
	Text 0,FontHeight()*11,"Hold V to free images"
	Text 0,FontHeight()*12,"Hold X to tile image"
	Text 0,FontHeight()*13,"Press Z to change image with Make2D3D() (slow)"
	
	If UpdateFrameRate < MilliSecs()
		UpdateFrameRate = MilliSecs() + UpdateFrameRateDelay
		FrameRate = MilliSecs() - FrameTime
		FrameRate = 1000/FrameRate
	EndIf
	FrameTime = MilliSecs()
	Text GraphicsWidth()/2,0,"FPS = "+FrameRate	
	
	Flip
	
	Count = 0
	SizeX = 0
	SizeY = 0
	
Wend
End





























































;////////////////////////////////////////////////////////////////////////////////////////
;THIS IS THE INCLUDE CODE
;////////////////////////////////////////////////////////////////////////////////////////
Global FR3DImageID ;The ID# of FR3DImage type
Global FR3DImagingCamera ;The Default 3D imaging camera
Global FR3DAutoMidHandle = False ;auto middle handle, use AutoMidHandle3D to switch
Global FR3DAlwaysRefreshImages = True ;Switch to false if you want to not hide the images every time Update3DImages() is called

Type FR3DImage
	
	Field Camera
	Field ID
	Field Img3D
	Field ZPos#
	Field ImgW#
	Field ImgH#
	Field ImgXHandle#
	Field ImgYHandle#
	Field Tex
	Field Temporary
	Field TemporaryReset
	Field Flags
	Field Alpha#
	Field Frames
	Field Rotate#
	
End Type

;;; <summary>Creates a 3D image</summary>
;;; <param name="TempPath">The full path/filename</param>
;;; <param name="TempFlag">The flag that the 3D image uses</param>
;;; <param name="TempZPos">Z buffer position for the 3D image to be drawn</param>
;;; <param name="Temp3DImagingCamera">The camera the 3D image is attached to</param>
;;; <remarks></remarks>
;;; <returns>The 3D image as a sprite</returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function LoadImage3D(TempPath$,TempFlags=1,TempZPos#=2,Temp3DImagingCamera=0)
	
	Local TempImage3D
	Local TempTex
	Local TempImage
	Local TempWidth# = GraphicsWidth()
	Local TempScale# = TempZPos#/TempWidth#
	If Temp3DImagingCamera = 0 Then Temp3DImagingCamera = FR3DImagingCamera
	
	TempImage = LoadImage(TempPath$)
	TempTex = LoadTexture(TempPath$,TempFlags)
	TempImage3D = CreateSprite(Temp3DImagingCamera)
	PositionEntity TempImage3D,0,0,TempZPos#
	EntityTexture(TempImage3D,TempTex)
	ScaleSprite TempImage3D,ImageWidth(TempImage)*TempScale#,ImageHeight(TempImage)*TempScale#
	
	;Setup the Image3D Type
	NewFR3DImage.FR3DImage = New FR3DImage
	NewFR3DImage\Camera = Temp3DImagingCamera
	NewFR3DImage\ID = FRNewFR3DImageID()
	NewFR3DImage\Img3D = TempImage3D
	NewFR3DImage\ZPos# = TempZPos#
	NewFR3DImage\ImgW# = ImageWidth(TempImage)
	NewFR3DImage\ImgH# = ImageHeight(TempImage)
	If FR3DAutoMidHandle = False
		HandleSprite(TempImage3D,-1,1)
		NewFR3DImage\ImgXHandle# = -1
		NewFR3DImage\ImgYHandle# = 1
	Else
		HandleSprite(TempImage3D,0,0)
		NewFR3DImage\ImgXHandle# = 0
		NewFR3DImage\ImgYHandle# = 0
	EndIf
	NewFR3DImage\Tex = TempTex
	NewFR3DImage\Temporary = False
	NewFR3DImage\TemporaryReset = 0
	NewFR3DImage\Flags = TempFlags
	NewFR3DImage\Alpha# = 1
	NewFR3DImage\Frames = 1
	NewFR3DImage\Rotate# = 0
	
	FreeImage TempImage
	HideEntity TempImage3D
	
	Return TempImage3D
	
End Function

;;; <summary>Sets the 3D imaging camera</summary>
;;; <param name="TempCamera">The camera used for 3D images</param>
;;; <remarks></remarks>
;;; <returns></returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function SetDefault3DImagingCamera(TempCamera)
	
	FR3DImagingCamera = TempCamera
	
End Function

;;; <summary>Shows a 3D image with 2D corodinates</summary>
;;; <param name="TempImage">The handle of a 3D image</param>
;;; <param name="TempX">X position on-screen to draw the image</param>
;;; <param name="TempY">Y position on-screen to draw the image</param>
;;; <remarks></remarks>
;;; <returns></returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function DrawImage3D(TempImage,TempX#,TempY#,TempFrame=0)
	
	Local TempWidth# = GraphicsWidth()
	Local TempHeight# = GraphicsHeight()
	Local TempX0# = (TempWidth#/2)/(TempWidth#*.25)
	Local TempY0# = (TempHeight#/2)/(TempWidth#*.25)
	Local Temp3DX# = TempX#/(TempWidth#*.25)
	Local Temp3DY# = TempY#/(TempWidth#*.25)
	
	For Find.FR3DImage = Each FR3DImage
		
		If Find\Img3D = TempImage
			
			ShowEntity(Find\Img3D)
			EntityTexture(Find\Img3D,Find\Tex,TempFrame)
			PositionEntity Find\Img3D,-TempX0#+Temp3DX#,TempY0#-Temp3DY#,Find\ZPos#
			
		EndIf
		
	Next
	
End Function

;;; <summary>Increases the image ID by 1 and returns it</summary>
;;; <remarks></remarks>
;;; <returns>FR3DImageID</returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function FRNewFR3DImageID()
	
	FR3DImageID = FR3DImageID + 1
	Return FR3DImageID
	
End Function

;;; <summary>Sets the handle for the 3D image in pixels</summary>
;;; <param name="TempImage">The handle of a 3D image</param>
;;; <param name="TempX">X position of handle in pixels</param>
;;; <param name="TempY">Y position of handle in pixels</param>
;;; <remarks></remarks>
;;; <returns></returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function HandleImage3D(TempImage,TempX#,TempY#)
	
	For Find.FR3DImage = Each FR3DImage
		
		If Find\Img3D = TempImage
			
			Local TempX3D# = TempX#/(Find\ImgW#/2)
			Local TempY3D# = TempY#/(Find\ImgH#/2)
			TempX3D# = TempX3D# - 1
			TempY3D# = -TempY3D# + 1
			HandleSprite Find\Img3D,TempX3D#,TempY3D#
			Find\ImgXHandle = TempX3D#
			Find\ImgYHandle = TempY3D#
			
		EndIf
		
	Next
	
End Function

;;; <summary>Sets the handle of the 3D image to the middle</summary>
;;; <param name="TempImage">The handle of a 3D image</param>
;;; <remarks></remarks>
;;; <returns></returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function MidHandle3D(TempImage)
	
	For Find.FR3DImage = Each FR3DImage
		
		If Find\Img3D = TempImage
			
			HandleSprite Find\Img3D,0,0
			Find\ImgXHandle# = 0
			Find\ImgYHandle# = 0
			
		EndIf
		
	Next
	
End Function

;;; <summary>Returns the X handle position of the image in pixels</summary>
;;; <param name="TempImage">The handle of a 3D image</param>
;;; <remarks></remarks>
;;; <returns>The X handle of an image in pixels</returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function ImageXHandle3D#(TempImage)
	
	For Find.FR3DImage = Each FR3DImage
		
		If Find\Img3D = TempImage
			
			Return (Find\ImgXHandle#+1)*(Find\ImgW#/2)
			
			Return 
			
		EndIf
		
	Next
	
End Function

;;; <summary>Returns the Y handle position of the image in pixels</summary>
;;; <param name="TempImage">The handle of a 3D image</param>
;;; <remarks></remarks>
;;; <returns>The Y handle of an image in pixels</returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function ImageYHandle3D#(TempImage)
	
	For Find.FR3DImage = Each FR3DImage
		
		If Find\Img3D = TempImage
			
			Return (-Find\ImgYHandle#+1)*(Find\ImgH#/2)
			
		EndIf
		
	Next
	
End Function

;;; <summary>Turns auto middle handle of 3D images on/off</summary>
;;; <param name="Enable">True or false for on or off</param>
;;; <remarks></remarks>
;;; <returns></returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function AutoMidHandle3D(Enable)
	
	If Enable <> False Then
		FR3DAutoMidHandle = True
	Else
		FR3DAutoMidHandle = False
	EndIf
	
End Function

;;; <summary>Updates all the 3D images</summary>
;;; <remarks></remarks>
;;; <returns></returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function Update3DImages()
	
	For Find.FR3DImage = Each FR3DImage
		
		If FR3DAlwaysRefreshImages = True
			
			HideEntity Find\Img3D
			
		EndIf
		
		If Find\Temporary = True
			
			FreeImage3D(Find\Img3D,Find\TemporaryReset,False)
			
		EndIf
		
	Next
	
End Function

;;; <summary>Set the alpha value for a 3D image</summary>
;;; <param name="TempImage">The handle of a 3D image</param>
;;; <param name="TempAlpha">a number between 1 and 0 1=solid, 0=invisible</param>
;;; <remarks></remarks>
;;; <returns></returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function SetImageAlpha3D(TempImage,TempAlpha#)
	
	If TempAlpha# > 1 Then TempAlpha# = 1
	If TempAlpha# < 0 Then TempAlpha# = 0
	
	For Find.FR3DImage = Each FR3DImage
		
		If Find\Img3D = TempImage
			
			EntityAlpha(Find\Img3D,TempAlpha#)
			Find\Alpha# = TempAlpha#
			
		EndIf
		
	Next
	
End Function

;;; <summary>Returns the alpha of a 3D image</summary>
;;; <param name="TempImage">The handle of a 3D image</param>
;;; <remarks></remarks>
;;; <returns>The alpha value of the specified 3D image</returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function ImageAlpha3D#(TempImage)
	
	For Find.FR3DImage = Each FR3DImage
		
		If Find\Img3D = TempImage
			
			Return Find\Alpha#
			
		EndIf
		
	Next
	
End Function

;;; <summary>Copy's a 3D image</summary>
;;; <param name="TempImage">The handle of a 3D image</param>
;;; <remarks></remarks>
;;; <returns>The newly copied Image</returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function CopyImage3D(TempImage)
	
	For Find.FR3DImage = Each FR3DImage
		
		If Find\Img3D = TempImage
			
			NewFR3DImage.FR3DImage = New FR3DImage
			NewFR3DImage\Camera = Find\Camera
			NewFR3DImage\ID = FRNewFR3DImageID()
			NewFR3DImage\Img3D = CopyEntity(Find\Img3D,Find\Camera)
			NewFR3DImage\ZPos# = Find\ZPos#
			NewFR3DImage\ImgW# = Find\ImgW#
			NewFR3DImage\ImgH# = Find\ImgH#
			NewFR3DImage\ImgXHandle# = Find\ImgXHandle#
			NewFR3DImage\ImgYHandle# = Find\ImgYHandle#
			NewFR3DImage\Tex = CreateTexture(TextureWidth(Find\Tex),TextureHeight(Find\Tex),Find\Flags,Find\Frames)
			CopyRect(0,0,TextureWidth(Find\Tex),TextureHeight(Find\Tex),0,0,TextureBuffer(Find\Tex),TextureBuffer(NewFR3DImage\Tex))
			EntityTexture(NewFR3DImage\Img3D,NewFR3DImage\Tex)
			NewFR3DImage\Temporary = False
			NewFR3DImage\TemporaryReset = False
			NewFR3DImage\Flags = Find\Flags
			NewFR3DImage\Alpha# = Find\Alpha#
			NewFR3DImage\Frames = Find\Frames
			NewFR3DImage\Rotate# = Find\Rotate#
			
			Return NewFR3DImage\Img3D
			
		EndIf
		
	Next
	
End Function

;;; <summary>Deletes a 3D image</summary>
;;; <param name="TempImage">The handle of a 3D image</param>
;;; <param name="TempResetOrder">If true, this will reset the order of all FR3DImage ID's</param>
;;; <param name="TempWait">True to not delete until next call to Update3DImages()</param>
;;; <remarks></remarks>
;;; <returns></returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function FreeImage3D(TempImage,TempResetOrder=True,TempWait=False)
	
	Local TempReseting = False
	
	For Find.FR3DImage = Each FR3DImage
		
		If TempReseting = True
			
			Find\ID = Find\ID - 1
			
		EndIf
		
		If Find\Img3D = TempImage
			
			If TempWait = False
				
				FreeEntity(Find\Img3D)
				FreeTexture(Find\Tex)
				Delete Find.FR3DImage
				TempReseting = TempResetOrder
				If TempReseting = True Then FR3DImageID = FR3DImageID - 1
				
			Else
				
				Find\Temporary = True
				Find\TemporaryReset = TempResetOrder
				
			EndIf
			
		EndIf
		
	Next
	
End Function

;;; <summary>Creates a blank 3D image</summary>
;;; <param name="TempWidth">Width of 3D image</param>
;;; <param name="TempHeight">Height of 3D image</param>
;;; <param name="TempFlags">The flag that the 3D image uses</param>
;;; <param name="TempFrames">How many  frames the 3D image has</param>
;;; <param name="TempZPos">Z buffer position for the 3D image to be drawn</param>
;;; <param name="Temp3DImagingCamera">The camera the 3D image is attached to</param>
;;; <remarks></remarks>
;;; <returns>The 3D image as a sprite</returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function CreateImage3D(TempWidth,TempHeight,TempFlags=1,TempFrames=1,TempZPos#=2,Temp3DImagingCamera=0)
	
	Local TempImage3D
	Local TempGFXWidth# = GraphicsWidth()
	Local TempXScale# = TempZPos#/TempGFXWidth#
	Local TempYScale# = TempZPos#/TempGFXWidth#
	If Temp3DImagingCamera = 0 Then Temp3DImagingCamera = FR3DImagingCamera
	
	TempImage3D = CreateSprite(Temp3DImagingCamera)
	PositionEntity TempImage3D,0,0,TempZPos#
	ScaleSprite TempImage3D,TempWidth*TempXScale#,TempHeight*TempYScale#
	
	;Setup the Image3D Type
	NewFR3DImage.FR3DImage = New FR3DImage
	NewFR3DImage\Camera = Temp3DImagingCamera
	NewFR3DImage\ID = FRNewFR3DImageID()
	NewFR3DImage\Img3D = TempImage3D
	NewFR3DImage\ZPos# = TempZPos#
	NewFR3DImage\ImgW# = TempWidth
	NewFR3DImage\ImgH# = TempHeight
	If FR3DAutoMidHandle = False
		HandleSprite(TempImage3D,-1,1)
		NewFR3DImage\ImgXHandle# = -1
		NewFR3DImage\ImgYHandle# = 1
	Else
		HandleSprite(TempImage3D,0,0)
		NewFR3DImage\ImgXHandle# = 0
		NewFR3DImage\ImgYHandle# = 0
	EndIf
	NewFR3DImage\Tex = CreateTexture(TempWidth,TempHeight,TempFlags,TempFrames)
	NewFR3DImage\Temporary = False
	NewFR3DImage\TemporaryReset = 0
	NewFR3DImage\Flags = TempFlags
	NewFR3DImage\Alpha# = 1
	NewFR3DImage\Frames = TempFrames
	NewFR3DImage\Rotate# = 0
	
	HideEntity TempImage3D
	
	Return TempImage3D
	
End Function

;;; <summary>Creates a 3D image from a loaded 2D image</summary>
;;; <param name="TempImage2D">The 2D image to make into a 3D image</param>
;;; <param name="TempFlag">The flag that the 3D image uses</param>
;;; <param name="TempZPos">Z buffer position for the 3D image to be drawn</param>
;;; <param name="Temp3DImagingCamera">The camera the 3D image is attached to</param>
;;; <remarks>Vary Slow! much faster to use LoadImage3D()!</remarks>
;;; <returns>The 3D image as a sprite</returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function Make2D3D(Temp2DImage,TempFlags=1,TempFrames=1,TempZPos#=2,Temp3DImaginfCamera=0)
	
	Local TempImage3D
	Local TempTex
	Local TempGFXWidth# = GraphicsWidth()
	Local TempXScale# = TempZPos#/TempGFXWidth#
	Local TempYScale# = TempZPos#/TempGFXWidth#
	If Temp3DImagingCamera = 0 Then Temp3DImagingCamera = FR3DImagingCamera
	
	TempTex = CreateTexture(ImageWidth(Temp2DImage),ImageHeight(Temp2DImage),TempFlags,TempFrames)
	TempImgWidth = ImageWidth(Temp2DImage)
	TempImgHeight = ImageHeight(Temp2DImage)
	ResizeImage(Temp2DImage,TextureWidth(TempTex),TextureHeight(TempTex))
	
	For TempCountFrames = 0 To TempFrames - 1
		
		SetBuffer TextureBuffer(TempTex,TempCountFrames)
		DrawImage(Temp2DImage,ImageXHandle(Temp2DImage),ImageYHandle(Temp2DImage),TempCountFrames)
		
	Next
	SetBuffer BackBuffer()
	ResizeImage(Temp2DImage,TempImgWidth,TempImgHeight)
	
	TempImage3D = CreateSprite(Temp3DImagingCamera)
	PositionEntity TempImage3D,0,0,TempZPos#
	EntityTexture(TempImage3D,TempTex)
	ScaleSprite TempImage3D,ImageWidth(Temp2DImage)*TempXScale#,ImageHeight(Temp2DImage)*TempYScale#
	
	NewFR3DImage.FR3DImage = New FR3DImage
	NewFR3DImage\Camera = Temp3DImagingCamera
	NewFR3DImage\ID = FRNewFR3DImageID()
	NewFR3DImage\Img3D = TempImage3D
	NewFR3DImage\ZPos# = TempZPos#
	NewFR3DImage\ImgW# = ImageWidth(Temp2DImage)
	NewFR3DImage\ImgH# = ImageHeight(Temp2DImage)
	If FR3DAutoMidHandle = False
		HandleSprite(TempImage3D,-1,1)
		NewFR3DImage\ImgXHandle# = -1
		NewFR3DImage\ImgYHandle# = 1
	Else
		HandleSprite(TempImage3D,0,0)
		NewFR3DImage\ImgXHandle# = 0
		NewFR3DImage\ImgYHandle# = 0
	EndIf
	NewFR3DImage\Tex = TempTex
	NewFR3DImage\Temporary = False
	NewFR3DImage\TemporaryReset = 0
	NewFR3DImage\Flags = TempFlags
	NewFR3DImage\Alpha# = 1
	NewFR3DImage\Frames = TempFrames
	NewFR3DImage\Rotate# = 0
	
	HideEntity TempImage3D
	
	Return TempImage3D
	
End Function

;;; <summary>Grabs a portion of the current drawing buffer and put it onto a 3D image</summary>
;;; <param name="TempImage">The handle of a 3D image</param>
;;; <param name="TempX">X position of current buffer to start from</param>
;;; <param name="TempY">Y position of current buffer to start from</param>
;;; <param name="TempFrame">Frame to insert the grabed image into</param>
;;; <remarks></remarks>
;;; <returns>Vary Slow!</returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function GrabImage3D(TempImage,TempX,TempY,TempFrame=0)
	
	For Find.FR3DImage = Each FR3DImage
		
		If Find\Img3D = TempImage
			
			Local TempImg2D = CreateImage(Find\ImgW,Find\ImgH)
			
			CopyRect(TempX,TempY,Find\ImgW,Find\ImgH,0,0,GraphicsBuffer(),ImageBuffer(TempImg2D))
			ResizeImage TempImg2D,TextureWidth(Find\Tex),TextureHeight(Find\Tex)
			CopyRect(0,0,ImageWidth(TempImg2D),ImageHeight(TempImg2D),0,0,ImageBuffer(TempImg2D),TextureBuffer(Find\Tex))
			
		EndIf
		
	Next
	
End Function

;;; <summary>Tiles a 3D image over the screen</summary>
;;; <param name="TempImage">The handle of a 3D image</param>
;;; <param name="TempX">X position to start</param>
;;; <param name="TempY">Y position to start</param>
;;; <param name="TempFrame">Frame to Tile</param>
;;; <remarks>VARY SLOW!!!!!</remarks>
;;; <returns></returns>
;;; <subsystemBlitz.Images.3D></subsystem>
;;; <example></example>
Function TileImage3D(TempImage,TempX=0,TempY=0,TempFrame=0)
	
	Local TempNextX = TempX
	Local TempNextY = TempY
	Local TempImage3D
	
	For Find.FR3DImage = Each FR3DImage
		
		If Find\Img3D = TempImage
			
			For TempTileX = TempX To GraphicsWidth()
				
				For TempTileY = TempY To GraphicsHeight()
					
					If TempTileX = TempNextX 
						
						If TempTileY = TempNextY
							
							TempImage3D = CopyImage3D(Find\Img3D)
							FreeImage3D(TempImage3D,True,True)
							
							DrawImage3D(TempImage3D,TempTileX,TempTileY,TempFrame)
							
							TempNextY = TempNextY + Find\ImgH
							
						EndIf
						
					EndIf
					
				Next
				
				If TempTileX = TempNextX
					
					TempNextX = TempNextX + Find\ImgW
					TempNextY = TempY
					
				EndIf
				
			Next
			
		EndIf
		
	Next
	
End Function

;;; <summary>Scales a 3D image usng percents</summary>
;;; <param name="TempImage">The handle of a 3D image</param>
;;; <param name="TempXScale">X scale of 3D image</param>
;;; <param name="TempYScale">Y scale of 3D image</param>
;;; <remarks></remarks>
;;; <returns></returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function ScaleImage3D(TempImage,TempXScale#,TempYScale#)
	
	For Find.FR3DImage = Each FR3DImage
		
		If Find\Img3D = TempImage
			
			Local TempWidth# = GraphicsWidth()
			Local TempScale# = Find\ZPos#/TempWidth#
			
			ScaleSprite Find\Img3D,(Find\ImgW*TempScale#)*TempXScale#,(Find\ImgH*TempScale#)*TempYScale#
			Find\ImgW = Find\ImgW * TempXScale#
			Find\ImgH = Find\ImgH * TempYScale#
			
		EndIf
		
	Next
	
End Function

;;; <summary>Scales a 3D image usng pixels</summary>
;;; <param name="TempImage">The handle of a 3D image</param>
;;; <param name="TempXScale">X scale of 3D image</param>
;;; <param name="TempYScale">Y scale of 3D image</param>
;;; <remarks></remarks>
;;; <returns></returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function ResizeImage3D(TempImage,TempXScale,TempYScale)
	
	For Find.FR3DImage = Each FR3DImage
		
		If Find\Img3D = TempImage
			
			Local TempWidth# = GraphicsWidth()
			Local TempScale# = Find\ZPos#/TempWidth#
			
			ScaleSprite Find\Img3D,TempXScale*TempScale#,TempYScale*TempScale#
			Find\ImgW = TempXScale
			Find\ImgH = TempYScale
			
		EndIf
		
	Next
	
End Function

;;; <summary>Returns the width of a 3D image</summary>
;;; <param name="TempImage">The handle of a 3D image</param>
;;; <remarks></remarks>
;;; <returns>The width of the specified 3D image</returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function ImageWidth3D#(TempImage)
	
	For Find.FR3DImage = Each FR3DImage
		
		If Find\Img3D = TempImage
			
			Return Find\ImgW#
			
		EndIf
		
	Next
	
End Function

;;; <summary>Returns the height of a 3D image</summary>
;;; <param name="TempImage">The handle of a 3D image</param>
;;; <remarks></remarks>
;;; <returns>The height of the specified 3D image</returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function ImageHeight3D#(TempImage)
	
	For Find.FR3DImage = Each FR3DImage
		
		If Find\Img3D = TempImage
			
			Return Find\ImgH#
			
		EndIf
		
	Next
	
End Function

;;; <summary>Creates a 3D rectangle</summary>
;;; <param name="TempX">Starting X position</param>
;;; <param name="TempY">Starting Y position</param>
;;; <param name="TempWidth">Width of rectangle</param>
;;; <param name="TempHeight">Height of rectangle</param>
;;; <param name="TempTemp">If true, the rectangle will be deleted when Update3DImages() is called</param>
;;; <param name="TempResetOrder">If TempTemp if true, this will reset the order of FR3DImage\ID if true</param>
;;; <param name="TempZPos">Z buffer position for the 3D image to be drawn</param>
;;; <param name="Temp3DImagingCamera">The camera the 3D image is attached to</param>
;;; <remarks></remarks>
;;; <returns>Will return the rectangle image as a sprite ONLY if TempTemp is false</returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function Rect3D(TempX,TempY,TempWidth,TempHeight,TempTemp=True,TempResetOrder=True,TempZPos#=2,Temp3DImagingCamera=0)
	
	
	If Temp3DImagingCamera = 0 Then Temp3DImagingCamera = FR3DImagingCamera
	Local TempImage3D = CreateImage3D(TempWidth,TempHeight,4,1,TempZPos#,Temp3DImagingCamera)
	If TempTemp = True FreeImage3D(TempImage3D,TempResetOrder,True)
	
	For Find.FR3DImage = Each FR3DImage
		
		If Find\Img3D = TempImage3D
			
			TempBuffer = GraphicsBuffer()
			SetBuffer TextureBuffer(Find\Tex)
			ClsColor ColorRed(),ColorGreen(),ColorBlue()
			Cls
			SetBuffer TempBuffer
			Exit
			
		EndIf
		
	Next
	
	DrawImage3D(TempImage3D,TempX,TempY)
	
	If TempTemp=False Return TempImage3D
	
End Function

;;; <summary>Rotates a 3D image</summary>
;;; <param name="TempImage">The handle of a 3D image</param>
;;; <param name="TempValue">Absolute rotation of the 3D image</param>
;;; <remarks></remarks>
;;; <returns></returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function RotateImage3D(TempImage,TempValue#)
	
	For Find.FR3DImage = Each FR3DImage
		
		If Find\Img3D = TempImage
			
			RotateSprite Find\Img3D,TempValue#
			Find\Rotate# = TempValue#
			
		EndIf
		
	Next
	
End Function

;;; <summary>Returns the Rotation of a 3D image</summary>
;;; <param name="TempImage">The handle of a 3D image</param>
;;; <remarks></remarks>
;;; <returns>The rotation of a 3D image</returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function ImageRotation3D#(TempImage)
	
	For Find.FR3DImage = Each FR3DImage
		
		If Find\Img3D = TempImage
			
			Return Find\Rotate#
			
		EndIf
		
	Next
	
End Function

;;; <summary>Returns the X coords on-screen of the specified image's specified corner</summary>
;;; <param name="TempImage">The handle of a 3D image</param>
;;; <param name="TempX">X position on-screen that the image is drawn</param>
;;; <param name="TempCorner">The corner's number [0=center, 1=top-left, 2=bottom-left, 3=bottom-right, 4=top-left</param>
;;; <remarks></remarks>
;;; <returns>The Image's corner's X position on-screen</returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function ImageXCorner3D#(TempImage,TempX#,TempCorner)
	
	Local TempDrawX# = TempX#
	Local TempHDist#
	Local TempAngle1#
	Local TempAngle2#
	Local TempDist#
	Local TempRotateX
	Local TempDistAngle#
	Local TempReturn#
	
	For Find.FR3DImage = Each FR3DImage
		
		If Find\Img3D = TempImage
			
			TempHDist# = Find\ImgW# ^ 2 + Find\ImgH# ^ 2
			TempHDist# = Sqr(TempHDist#)/2
			TempAngle1# = ASin(Find\ImgH#/2/TempHDist#) + -Find\Rotate#
			TempAngle2# = ACos(Find\ImgH#/2/TempHDist#) + -Find\Rotate#
			
			TempDist# = (Find\ImgW#/2 - ImageXHandle3D(Find\Img3D)) ^ 2 + (Find\ImgH#/2 - ImageYHandle3D(Find\Img3D)) ^ 2
			TempDist# = Sqr(TempDist#)
			
			If ImageXHandle3D(Find\Img3D) => Find\ImgW#/2 And ImageYHandle3D(Find\Img3D) <= Find\ImgH#/2 Then
				TempRotateX = -Find\Rotate#
			EndIf
			If ImageXHandle3D(Find\Img3D) => Find\ImgW#/2 And ImageYHandle3D(Find\Img3D) => Find\ImgH#/2
				TempRotateX = Find\Rotate#
			EndIf
			If ImageXHandle3D(Find\Img3D) =< Find\ImgW#/2 And ImageYHandle3D(Find\Img3D) => Find\ImgH#/2
				TempRotateX = Find\Rotate#
			EndIf
			If ImageXHandle3D(Find\Img3D) =< Find\ImgW#/2 And ImageYHandle3D(Find\Img3D) =< Find\ImgH#/2
				TempRotateX = -Find\Rotate#
			EndIf
			
			If TempDist# <> 0
				TempDistAngle# = ACos((Find\ImgW#/2 - ImageXHandle3D(Find\Img3D))/TempDist#) + TempRotateX
				TempDrawX# = TempX# + Cos(TempDistAngle#)*TempDist#
			EndIf
			
			Select TempCorner
				
				Case 0
					
					Return TempDrawX
					
				Case 1
					
					Return TempDrawX - Cos(TempAngle1#)*TempHDist#
					
					
				Case 2
					
					TempAngle2# = TempAngle2# + 90
					Return TempDrawX - Cos(TempAngle2#)*TempHDist#
					
				Case 3
					
					TempAngle1# = TempAngle1# + 180
					Return TempDrawX - Cos(TempAngle1#)*TempHDist#
					
				Case 4
					
					TempAngle2# = TempAngle2# + 270
					Return TempDrawX - Cos(TempAngle2#)*TempHDist#
					
			End Select
			
		EndIf
		
	Next
	
End Function

;;; <summary>Returns the Y coords on-screen of the specified image's specified corner</summary>
;;; <param name="TempImage">The handle of a 3D image</param>
;;; <param name="TempX">Y position on-screen that the image is drawn</param>
;;; <param name="TempCorner">The corner's number [0=center, 1=top-left, 2=bottom-left, 3=bottom-right, 4=top-left</param>
;;; <remarks></remarks>
;;; <returns>The Image's corner's Y position on-screen</returns>
;;; <subsystem>Blitz.Images.3D</subsystem>
;;; <example></example>
Function ImageYCorner3D#(TempImage,TempY,TempCorner)
	
	Local TempDrawY = TempY
	Local TempHDist#
	Local TempAngle1#
	Local TempAngle2#
	Local TempDist#
	Local TempRotateY
	Local TempDistAngle#
	
	For Find.FR3DImage = Each FR3DImage
		
		If Find\Img3D = TempImage
			
			TempHDist# = Find\ImgW# ^ 2 + Find\ImgH# ^ 2
			TempHDist# = Sqr(TempHDist#)/2
			TempAngle1# = ASin(Find\ImgH#/2/TempHDist#) + -Find\Rotate#
			TempAngle2# = ACos(Find\ImgH#/2/TempHDist#) + -Find\Rotate#
			
			TempDist# = (Find\ImgW#/2 - ImageXHandle3D(Find\Img3D)) ^ 2 + (Find\ImgH#/2 - ImageYHandle3D(Find\Img3D)) ^ 2
			TempDist# = Sqr(TempDist#)
			
			If ImageXHandle3D(Find\Img3D) => Find\ImgW#/2 And ImageYHandle3D(Find\Img3D) <= Find\ImgH#/2 Then
				TempRotateY = Find\Rotate#
			EndIf
			If ImageXHandle3D(Find\Img3D) => Find\ImgW#/2 And ImageYHandle3D(Find\Img3D) => Find\ImgH#/2
				TempRotateY = Find\Rotate#
			EndIf
			If ImageXHandle3D(Find\Img3D) =< Find\ImgW#/2 And ImageYHandle3D(Find\Img3D) => Find\ImgH#/2
				TempRotateY = -Find\Rotate#
			EndIf
			If ImageXHandle3D(Find\Img3D) =< Find\ImgW#/2 And ImageYHandle3D(Find\Img3D) =< Find\ImgH#/2
				TempRotateY = -Find\Rotate#
			EndIf
			
			If TempDist# <> 0
				TempDistAngle# = ASin((Find\ImgH#/2 - ImageYHandle3D(Find\Img3D))/TempDist#) + TempRotateY
				TempDrawY = TempY + Sin(TempDistAngle#)*TempDist#
			EndIf
			
			Select TempCorner
					
				Case 0
					
					Return TempDrawY
					
				Case 1
					
					Return TempDrawY - Sin(TempAngle1#)*TempHDist#
					
				Case 2
					
					TempAngle2# = TempAngle2# + 90
					Return TempDrawY - Sin(TempAngle2#)*TempHDist#
					
				Case 3
					
					TempAngle1# = TempAngle1# + 180
					Return TempDrawY - Sin(TempAngle1#)*TempHDist#
					
				Case 4
					
					TempAngle2# = TempAngle2# + 270
					Return TempDrawY - Sin(TempAngle2#)*TempHDist#
					
			End Select
			
		EndIf
		
	Next
	
End Function
