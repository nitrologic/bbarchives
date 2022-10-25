; ID: 98
; Author: Unknown
; Date: 2001-10-14 00:40:48
; Title: Pixelate
; Description: Pixelates an image to whatever resolution you want.


; Pixelate v0.3 By The Prof (15% faster)- For Blitzers Everywhere!
; 
Graphics 640,480,16
SetBuffer BackBuffer()
Global Pic=LoadImage("c:\windows\desktop\class56.jpg") ; <- put your image in here

; ****** MAIN ****** 
Demo
End

; ********************************************************

Function Demo()
; This little routine shows how to manipulate the image
; from hi-res to low-res.
DrawBlock Pic,0,0
Color 255,255,255:Text 150,400,"Any key to start - Esc to quit"
Flip:Cls:WaitKey:Flip:Cls
DeRes=True
Repeat
  If DeRes=True
    Res=Res+1
    If res=75
      DeRes=False
   EndIf
Else If DeRes=False
   Res=Res-1
   If Res=1
     DeRes=True
   End If
End If

Pixelate(Pic,0,0,Res)

Flip:Cls
Until KeyHit(1)
End Function

; ********************************************************

Function Pixelate(ImageID,XPos,YPos,Res)
; Pixelate V0.3 By The Prof (15% Faster) - Last compiled 13/10/01 - 23:15
;
; This function pixelates (decreases & increases) the resolution
; of an image without increasing its original size. This version
; requires no arrays/banks etc as everything is calculated in realtime :-)
; It is capable of doing full screens although a hefty cpu is required. 
;
; PARAMETERS:
; ImageID - The Image you want to Pixelate - must be loaded in first!
; XPos,YPos - Where you want the pixelated image to display
; Res - is the resolution of the new image (2 to whatever)
;
Width=ImageWidth(ImageID):Height=ImageHeight(ImageID)
RectsWidth=Int(Width/Res)+1:RectsHeight=Int(Height/Res)+1
Colums=0:Rows=0:X=XPos:Y=YPos
Viewport XPos,YPos,Width,Height ; limit the viewport to clip the image.
DrawBlock ImageID,XPos,YPos ; paste the image down first.
If Res>1
    Repeat
    Repeat
      GetColor x,y:Rect x,y,res,res
      x=x+Res:Colums=Colums+1
    Until Colums=RectsWidth
    X=XPos:y=y+Res
    Colums=0:Rows=Rows+1
  Until Rows=RectsHeight
 End If
Viewport 0,0,GraphicsWidth,GraphicsHeight ; reset the viewport to full screen
End Function

; ********************************************************



