; ID: 2998
; Author: EsseEmmeErre
; Date: 2012-11-03 10:26:02
; Title: HalvePic
; Description: Halve a pix with no original image point cuts out!

;-> HalvePic public by Stefano Maria Regattin
;d> 3 Nov 2012
;m> 3 Nov 2012
;--------------
AppTitle("HalvePic public by Stefano Maria Regattin")
Graphics(710,FontHeight(),0,2)
PixPath$=Input$(">")
If FileType(PixPath$)=1 Then
 Pix=LoadImage(PixPath$)
 PixWidth=ImageWidth(Pix)
 PixHeight=ImageHeight(Pix)
 BeforeLast=PixHeight-FontHeight()-FontHeight()
 EndGraphics()
 Graphics(PixWidth,PixHeight,0,2)
 HalvedPixWidth=PixWidth/2
 HalvedPixHeight=PixHeight/2
 Immagine=LoadImage(PixPath$)
 DrawImage(Pix,0,0):Delay(2000):Cls()
 For PointY=0 To HalvedPixHeight-1
  For PointX=0 To HalvedPixWidth-1
   SetBuffer(ImageBuffer(Pix))
   PointColour=ReadPixel(PointX*2,PointY*2) And $FFFFFF
   RedOfPoint=PointColour/65536 Mod 256
   GreenOfPoint=PointColour/256 Mod 256
   BlueOfPoint=PointColour Mod 256
   PointColour=ReadPixel(PointX*2,PointY*2) And $FFFFFF
   RedOfPoint=RedOfPoint+PointColour/65536 Mod 256
   GreenOfPoint=GreenOfPoint+PointColour/256 Mod 256
   BlueOfPoint=BlueOfPoint+PointColour Mod 256
   RedOfPoint=RedOfPoint/4:GreenOfPoint=GreenOfPoint/4:BlueOfPoint=BlueOfPoint/4
   SetBuffer(FrontBuffer())
   Color(RedOfPoint,GreenOfPoint,BlueOfPoint)
   Plot(PointX,PointY)
  Next
 Next
 Locate(0,BeforeLast):Color(255,255,255):Print("Image halved.")
 Write("Press a key to leave...")
Else
 Write("Image not found, press a key to leave...")
EndIf
WaitKey()
EndGraphics():End
