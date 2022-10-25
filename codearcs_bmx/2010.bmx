; ID: 2010
; Author: tonyg
; Date: 2007-05-09 07:18:31
; Title: Bloom
; Description: Blur/Bloom using scaling

SuperStrict
Graphics 640,480
SeedRnd MilliSecs()
Const scaledown:Float = 8.0
AutoMidHandle True
Local IMAGE:TIMAGE = LoadImage("MAX_alpha.PNG")
Local blur:Int = 1
Local image1:timage=Null
While Not KeyHit(KEY_ESCAPE)
	Local t1:Int=MilliSecs()
	Cls
	If MouseHit(1) image1=createbloom(image)
	SetScale 1.0 , 1.0
	SetAlpha 1.0
	SetBlend maskblend
	DrawImage image , MouseX() , MouseY()
	If image1
		SetScale scaledown + (scaledown / 10.0) , scaledown + (scaledown / 10.0)
		SetBlend lightblend
		SetAlpha 0.8  ' try changing alpha value for bigger/smaller blooms.
		DrawImage image1 , MouseX() , MouseY()
	EndIf
	Flip 0
	Local t2:Int=MilliSecs()
'	Print t2 - t1
Wend
Function createbloom:timage(image:timage)
		Local st:Int=MilliSecs()
   		SetAlpha 1.0
		SetScale 1.0/scaledown,1.0/scaledown
		DrawImage image , (ImageWidth(image)/scaledown)/2 , (ImageHeight(image)/scaledown)/2
		Local mypixmap:tpixmap = GrabPixmap(0,0,ImageWidth(image)/scaledown,ImageHeight(image)/scaledown)
		Local et:Int = MilliSecs()
		Print (et-st)
		Return LoadImage(mypixmap)
End Function
