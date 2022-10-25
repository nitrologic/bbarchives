; ID: 2002
; Author: tonyg
; Date: 2007-04-24 06:15:38
; Title: Add animation frame
; Description: Takes a new image and add existing loadanimimage timage

Graphics 800 , 600
Local image1:timage = LoadAnimImage("add_anim_test.png" , 32 , 32 , 0 , 2)
Local image2:timage = LoadImage("max.png")
For Local x1:Int = 0 To Len(image1.frames) - 1
	DrawImage image1 , x1 * 32 , 0 , x1
Next
Flip
WaitKey()
add_animframe(image1,image2)
For Local x2:Int = 0 To Len(image1.frames) - 1
	DrawImage image1 , x2 * 32 , 100 , x2
Next
Flip
WaitKey()

Function add_animframe(image1:timage , image2:timage)
	If ImageWidth(image1) <> ImageWidth(image2) Or ImageHeight(image1) <> ImageHeight(image2)
		Notify "Images are different sizes"
	Else
		' Create our pixmap from the image
 		Local temp_pixmap:tpixmap = LockImage(image2)
		' slice our frame counters to cater for the extra image
		image1.frames = image1.frames[..Len(image1.frames) + 1]
		image1.pixmaps = image1.pixmaps[..Len(image1.pixmaps) + 1]
		image1.seqs = image1.seqs[..Len(image1.seqs) + 1]
		' include our new pixmap in the existing array of pixmaps
		image1.setpixmap(Len(image1.pixmaps) - 1 , temp_pixmap)
		'create a frame from the pixmap
		image1.frame(Len(image1.frames) - 1)
                UnlockImage(image2) ' in case it becomes necessary
	EndIf
End Function
