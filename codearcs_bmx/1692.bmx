; ID: 1692
; Author: tonyg
; Date: 2006-05-03 09:00:28
; Title: Pixmap paste with mask
; Description: Pixmap.paste doesn't take mask colour into account.

Graphics 640,480
image1:TImage=LoadImage("max.png")
image2:TImage=LoadImage("light.png")
While Not KeyHit(key_escape)
	Cls
	If MouseHit(1)
		argb:Int=intcolor(image2.mask_r,image2.mask_g,image2.mask_b)
		image1:TImage=drawbuffer(image1,image2,0,0,argb)
	EndIf
	DrawImage image1,0,0
	Flip
Wend
WaitKey()
Function drawbuffer:TImage(imagea:TImage,imageb:TImage,x:Int,y:Int,argb:Int)
  If x + ImageWidth(imageb) > ImageWidth(imagea) Or y + ImageHeight(imageb) > ImageHeight(imagea) RuntimeError("Imagea to big to fit in imageb")
'  start_func=MilliSecs()
  mypixmap2:TPixmap=LockImage(imageb)
  UnlockImage(imageb)
  mypixmap1:TPixmap=LockImage(imagea)
  Local mypixelptr2:Int Ptr = Int Ptr(mypixmap2.pixelptr(0,0))
  Local mypixelptr2backup:Int Ptr = mypixelptr2
  Local mypixelptr1:Int Ptr = Int Ptr(mypixmap1.pixelptr(x,y))
  Local mypixelptr1backup:Int Ptr = mypixelptr1
  For my_x=0 To ((mypixmap2.width)*(mypixmap2.height))
     If mypixelptr2[0] <> argb 
         If mypixelptr2[0] <> 0 mypixelptr1[0]=mypixelptr2[0]
 '             If mypixelptr2[0] <> 16777215 mypixelptr1[0]=mypixelptr2[0]
    EndIf
     mypixelptr1:+1
     mypixelptr2:+1
     If mypixelptr2 = mypixelptr2backup+(mypixmap2.pitch Shr 2)
         mypixelptr1 = mypixelptr1backup+(mypixmap1.pitch Shr 2)
         mypixelptr1backup=mypixelptr1
         mypixelptr2backup=mypixelptr2
     EndIf
  Next
  Return LoadImage(mypixmap1)
'  end_func=MilliSecs()
End Function
Function IntColor(R,G,B,A=255)
'returns argb value from red, green, blue.
     Return A Shl 24 | R Shl 16 | G Shl 8 | B Shl 0
End Function
