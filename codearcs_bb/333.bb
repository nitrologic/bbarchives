; ID: 333
; Author: Klaas
; Date: 2002-05-30 14:52:06
; Title: Half-Bright Images
; Description: fastest way to draw transparent shadows in 2D

Graphics 800,600,16,2
SetBuffer BackBuffer()

background=LoadImage("bg.jpg")
player=LoadImage("player.png")
playershadow=LoadImage_hb("shadow.png")

While Not KeyHit(1)
begin=MilliSecs()

Cls

;mouse pos
mx=MouseX()
my=MouseY()

;draw background
DrawBlock(background,0,0)

;draw playershadow
drawblock_hb(mx+20,my+20,playershadow)

;draw player
DrawImage(player,mx,my)

finish=MilliSecs()

;calc FPS
fps=1000/(finish-begin+1)
Text 10,10,fps
Flip
Wend
End

;Function to load the Half-Bright Images
Function LoadImage_hb(imageload$)
;load image
image=LoadImage(imageload)
;analyse the Image
x=ImageWidth(image)
y=ImageHeight(image)
;ceate bank for Image (x*y + infobytes for size info)
bank=CreateBank(x*y+2)
PokeByte bank,0,x
PokeByte bank,1,y
LockBuffer(ImageBuffer(image))
pixelstep=2
For sx=0 To x-1
For sy=0 To y-1
;this one is for scanning the Image
pixel=ReadPixel(sx,sy,ImageBuffer(image)) And $ff
PokeByte bank,pixelstep,pixel
pixelstep=pixelstep+1
Next
Next
UnlockBuffer(ImageBuffer(image))
FreeImage(image)
;return bankhandle
Return bank
End Function

;this one is for drawing the Half-bright image
Function drawblock_hb(offx,offy,image)
LockBuffer(BackBuffer())
x=PeekByte(image,0)
y=PeekByte(image,1)
pixelstep=2
For sx=0 To x-1
For sy=0 To y-1
pixel=PeekByte(image,pixelstep)
;if pixel is black ...
If Not pixel
;thats the main thingy-> 
;read the pixel in BackBuffer on current position
;now mask the 4 color bytes
;then shift the right
ct=(ReadPixelFast(sx+offx,sy+offy) And $fefefefe) Shr 1
;know write the half brightd pixel
WritePixelFast(sx+offx,sy+offy,ct)
End If
pixelstep=pixelstep+1
Next
Next
UnlockBuffer(BackBuffer())
End Function
