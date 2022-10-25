; ID: 1774
; Author: Matty
; Date: 2006-08-03 05:42:52
; Title: Convert 2 Antialiased Images into single image with alpha channel
; Description: Take 2 images which have been antialiased against a white & black background and save as a single TGA image with alpha

;Just a useful bit of code I put together as I needed to create some antialiased images from a model
;rendered in blitz3d as I couldn't get the model I had into a renderer that would keep the animation which 
;I wanted to render out as anti aliased tgas.....
;
;
;
;
;It is written in blitzplus but the only blitzplus specific code is the requestfile commands in the example 
;
;The purpose of this program is to take 2 images with no alpha which have been anti aliased against both a 
;black and a white background and convert it into a single image with an alpha channel which retains the antialiasing
;information in the alpha channel.  
;
;Seems to work fine for what I used it for.
;
;
;
;
;
;

Graphics 800,600,32,2

;Supply your own images to test.

RGBBlack=GetColorMap(RequestFile("Anti Aliased Image File with Black Background","jpg;*.png;*.bmp"))
RGBWhite=GetColorMap(RequestFile("Anti Aliased Image File with White Background","jpg;*.png;*.bmp"))
CreateTGAWithAlpha(RequestFile("TGA Image With Alpha To Save","tga",True),RGBBlack,RGBWhite)
FreeBank RGBBlack
FreeBank RGBWhite
End


;CODE



Function GetColorMap(FileName$)

image=LoadImage(FileName$)
If image=0 Then RuntimeError("Image Does Not Exist")
width=ImageWidth(image)
height=ImageHeight(image)
bank=CreateBank(8+width*height*4)
PokeInt bank,0,width
PokeInt bank,4,height
SetBuffer ImageBuffer(image)
LockBuffer
For y=0 To height-1
For x=0 To width-1
PokeInt bank,8+4*(x+y*width),ReadPixelFast(x,y)

Next
Next
UnlockBuffer
SetBuffer BackBuffer()
Flip
FreeImage image
Return bank
End Function

Function CreateTGAWithAlpha(FileName$,BlackImageBank,WhiteImageBank)
RGBBlack=BlackImageBank
RGBWhite=WhiteImageBank
If BlackImageBank=0 Or WhiteImageBank=0 Then RuntimeError("Need to pass white and black image banks")
If BankSize(BlackImageBank)<>BankSize(WhiteImageBank) Then RuntimeError("Black / White Image Banks should be the same size")
Width=PeekInt(BlackImageBank,0)
Height=PeekInt(BlackImageBank,4)
Offset=8
ImageBank=CreateBank(BankSize(BlackImageBank))
PokeInt ImageBank,0,Width
PokeInt ImageBank,4,Height
For Y=height-1 To 0 Step -1
For X=0 To Width-1
RGBBlack=PeekInt(BlackImageBank,8+4*(width*y+x))
RGBWhite=PeekInt(WhiteImageBank,8+4*(width*y+x))
DiffRed=GetRed(RGBBlack)-GetRed(RGBWhite)
DiffGreen=GetGreen(RGBBlack)-GetGreen(RGBWhite)
DiffBlue=GetBlue(RGBBlack)-GetBlue(RGBWhite)

AverageColorDifference=(Abs(DiffRed)+Abs(DiffGreen)+Abs(DiffBlue))/3
RGBAlpha=255-AverageColorDifference
If RGBWhite=RGBBlack Then RGBAlpha=255
If RGBBlack=GetRGB(0,0,0) And RGBWhite=GetRGB(255,255,255) Then RGBAlpha=0
PokeByte ImageBank,9+4*(x+((Height-1)-y)*width),getred(RGBBlack)
PokeByte ImageBank,10+4*(x+((Height-1)-y)*width),getgreen(RGBBlack)
PokeByte ImageBank,11+4*(x+((Height-1)-y)*width),getblue(RGBBlack)
PokeByte ImageBank,8+4*(x+((Height-1)-y)*width),RGBAlpha

Next
Next
f=WriteFile(FileName$)
If f=0 Then RuntimeError("Error Writing File")
;Borrowed from Save TGA Example in Code Archives
 	WriteByte(f,0) ;idlength
    WriteByte(f,0) ;colormaptype
    WriteByte(f,2) ;imagetype 2=rgb
    WriteShort(f,0) ;colormapindex
    WriteShort(f,0) ;colormapnumentries
    WriteByte(f,0) ;colormapsize 
    WriteShort(f,0) ;xorigin
    WriteShort(f,0) ;yorigin
    WriteShort(f,width) ;width
    WriteShort(f,height) ;height
    WriteByte(f,32) ;pixsize
    WriteByte(f,8) ;attributes
	For offset=8 To BankSize(ImageBank)-4 Step 4
		WriteByte f,PeekByte(ImageBank,offset+3)
		WriteByte f,PeekByte(ImageBank,offset+2)
		WriteByte f,PeekByte(ImageBank,offset+1)
		WriteByte f,PeekByte(imagebank,offset)
	
	Next

	;WriteBytes ImageBank,f,8,width*height*4	




CloseFile f
 
FreeBank ImageBank

End Function



;From Code Archives
Function GetRGB(Red,Green,Blue)


Return Blue Or (Green Shl 8) Or (Red Shl 16)

End Function
Function GetRed(RGB)

Return RGB Shr 16 And %11111111

End Function
Function GetBlue(RGB)

Return RGB And %11111111

End Function
Function GetGreen(RGB)

Return RGB Shr 8 And %11111111
End Function
