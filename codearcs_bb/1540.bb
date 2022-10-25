; ID: 1540
; Author: jfk EO-11110
; Date: 2005-11-22 22:55:05
; Title: Sharpen
; Description: Sharpen 2D Image

;Sharpen 2D function for BlitzBasic


Graphics 800,600,16,2
SetBuffer BackBuffer()

img=LoadImage("4.jpg")
DrawBlock img,0,0

MultiSharpen(img, 0.175)
;MultiSharpen(img, 0.15,1)

DrawBlock img,400,0
Flip
WaitKey()


; The MultiSharpen Function simply uses the Sharpen function in 4 directions
;usage: 
;img=image Handle
;sh# sharpen amount (recc: 0.01 to 1.0) (optional)
;contour: 1 or 0: use sinus curve to amplify contours (recc:0, optional)
;ig#: max brightness diffrence to ignore (protect low contrast edges) (recc:0, optional, still kinda bugous)

Function MultiSharpen(img,sh#=0.15,contour=0,ig#=0)
 sharpen(img, sh#, contour,-1,-1,ig#)
 sharpen(img, sh#, contour, 1,-1,ig#)
 sharpen(img, sh#, contour,-1, 1,ig#)
 sharpen(img, sh#, contour, 1, 1,ig#)
End Function




;usage: 
;img=image Handle
;sh# sharpen amount (recc: 0.01 to 1.0) (optional)
;contour: 1 or 0: use sinus curve to amplify contours (optional)
;xo, yo: offset for pixel comparing (-1,0 or 1)
;ignore: max brightness diffrence to ignore (protect low contrast edges)

Function sharpen(img, amount#,contour=0,xo=-1,yo=-1, ignore#=0)

 Width = ImageWidth(img)
 Height = ImageHeight(img) 
 img2 = CopyImage(img) 
 SetBuffer ImageBuffer(img)
 LockBuffer ImageBuffer(img)
 LockBuffer ImageBuffer(img2)
  If xo<-1 Then xo=-1
  If xo>1 Then xo=1
;  If xo=0 Then xo=-1 ; disallow horizontal/vertical mode
  If yo<-1 Then yo=-1
  If yo>1 Then yo=1
;  If yo=0 Then yo=-1
  ys=1:ye=height-1
  If yo=1 Then: ys=0:ye=height-2:EndIf
  xs=1:xe=width-1
  If xo=1 Then: xs=0:xe=width-2:EndIf
  For y1 = ys To ye
    For x1 = xs To xe
      rgb1 = ReadPixelFast(x1,y1,ImageBuffer(img)) And $FFFFFF
      rgb2 = ReadPixelFast(x1+xo,y1+yo,ImageBuffer(img)) And $FFFFFF

      r1#=(rgb1 Shr 16) And $FF
      g1#=(rgb1 Shr 8) And $FF
      b1#=rgb1 And $FF

      r2#=(rgb2 Shr 16) And $FF
      g2#=(rgb2 Shr 8) And $FF
      b2#=rgb2 And $FF

      grey1#=(r1+g1+b1)/3.0
      grey2#=(r2+g2+b2)/3.0
      If Abs(grey1-grey2)>ignore

       If contour=0
        r3# = r1 + Amount * (r1 - r2) 
        g3# = g1 + Amount * (g1 - g2) 
        b3# = b1 + Amount * (b1 - b2) 
       Else
        r3# = r1 + Amount * Cos(270+(r1 - r2)*0.352941)*255.0
        g3# = g1 + Amount * Cos(270+(g1 - g2)*0.352941)*255.0
        b3# = b1 + Amount * Cos(270+(b1 - b2)*0.352941)*255.0
       EndIf
       If r3>255 Then r3=255
       If r3<0 Then r3=0
       If g3>255 Then g3=255
       If g3<0 Then g3=0
       If b3>255 Then b3=255
       If b3<0 Then b3=0
 
       rgb3=(r3 Shl 16)Or(g3 Shl 8)Or(b3)      
       WritePixelFast x1+xo,y1+yo,rgb3,ImageBuffer(img2)
      Else
       WritePixelFast x1+xo,y1+yo,rgb2,ImageBuffer(img2)
      EndIf
    Next 
  Next 
 UnlockBuffer ImageBuffer(img)
 UnlockBuffer ImageBuffer(img2)
 SetBuffer BackBuffer()
 CopyRect 0,0,width,height,0,0,ImageBuffer(img2),ImageBuffer(img)
 FreeImage img2
End Function
