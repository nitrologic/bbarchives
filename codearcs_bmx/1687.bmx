; ID: 1687
; Author: tonyg
; Date: 2006-04-28 06:09:50
; Title: Another drawimagerect
; Description: Simple drawimagerect as per bb

Graphics 640,480
SeedRnd MilliSecs()
image:TImage=LoadImage("max.png")
While Not KeyHit(key_escape)
  Cls
  tg_drawimagerect(image,Rand(0,640),Rand(0,480),0,0,200,100)
  Flip
Wend
Function tg_drawimagerect(image:TImage,x:Int,y:Int,xs:Int,ys:Int,width:Int,height:Int)
    DrawImage LoadImage(PixmapWindow(LockImage(image),xs,ys,width,height)),x,y
End Function
