; ID: 156
; Author: Captain Darius
; Date: 2001-12-05 19:32:07
; Title: Rotate Image
; Description: Takes an image and creates a new image containing 72 frames of the old image rotated at 5 degree intervals.

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;
;; File: ImageRotate.bb
;; Created: May 25, 2001
;; Created By: Captain Darius
;;
;; Description: This code will take an image and create a new image that
;;              contains 72 frames of the first image rotated at 5 degree
;;              increments.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; These are the input/output paramters
Const InputFile$ = "<Your Input File Name Here>"
Const OutputFile$ = "<Your Output File Name Here>"

; Graphics mode
Const SCREEN_WIDTH = 640
Const SCREEN_HEIGHT = 480

Graphics SCREEN_WIDTH,SCREEN_HEIGHT

; Create an array to hold all the frames we cretae
Dim ImageFrames(72)

; "Automidhandle" helps make the rotation look nice :)
AutoMidHandle True
TFormFilter 1

; Load the initial image, this will be frame #1
DebugLog "Loading Image: " + InputFile$
ImageFrames(1) = LoadImage(InputFile$)
If ( ImageFrames(1) = 0 ) Then
	DebugLog "ERROR! Failed to load image: " + InputFile$
	End
EndIf

; Setup some counters
MaxHeight = 0
MaxWidth = 0
Count = 2

; Loop through and create all the rotated images
For i = 5 To 355 Step 5
	ImageFrames(Count) = CopyImage( ImageFrames(1) )
	RotateImage ImageFrames(Count), i

	; We want to know what size of the biggest frame is
	If ( ImageHeight(ImageFrames(Count)) > MaxHeight ) MaxHeight = ImageHeight(ImageFrames(Count))
	If ( ImageWidth(ImageFrames(Count) ) > MaxWidth  ) MaxWidth = ImageWidth(ImageFrames(Count))
	
	; Move through the array
	Count = Count + 1
Next

; Now, turn off AutoMidHandle so we can create the new image
; NOTE: The images in out frames array are still set to midhandle
AutoMidHandle False

; Report the Max Height & Width
DebugLog "MaxHeight = " + MaxHeight + ", MaxWidth = " + MaxWidth

; Create the new image (we will create 9 x 8 frames)
DebugLog "Creating New Image..."
ShipImg = CreateImage( 9 * MaxWidth, 8 * MaxHeight )

; Now, write the frames out into the new image
x = MaxWidth / 2
y = MaxHeight / 2
For i = 1 To 72
	SetBuffer ImageBuffer(ShipImg)
	DrawImage ImageFrames(i), x, y
	x = x + MaxWidth
	If ( i Mod 9 ) = 0 Then
		x = MaxWidth / 2
		y = y + MaxHeight
	End If
Next

; Finally, write out the new file
DebugLog "Saving new image, filename = " + OutputFile$
SaveImage ShipImg, OutputFile$
