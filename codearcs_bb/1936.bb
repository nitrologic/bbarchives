; ID: 1936
; Author: Xzider
; Date: 2007-02-28 07:27:48
; Title: Smiley Face
; Description: A smiley face

Graphics 800,600,32,2

			Speed% = Input("Drawing Speed In Milli-Seconds (1000 Milli-Seconds = 1 Second - ")
			Size% = Input("Size of smiley? (250-400 recommended) - ")
			
  Cls
		
  Color 0,255,0

  For degree = 0 To 359

		Delay Speed%/200

		x = Cos(degree)*Size%
		y = Sin(degree)*Size%

		Rect(GraphicsWidth()/2+x,GraphicsHeight()/2+y,Size/50,Size/50)

  Next

  Color 0,0,255

		Delay Speed%

        xx = -Size%/2
        yy = -Size%/2

		Rect(GraphicsWidth()/2+xx,GraphicsHeight()/2+yy,Size/25,Size/25)

		Delay Speed%
		
        xx = -Size/2+Size
        yy = -Size/2

		Rect(GraphicsWidth()/2+xx,GraphicsHeight()/2+yy,Size/25,Size/25)

		Delay Speed%

  Color 0,255,255

		xx = -Size/2

  For i = 0 To Size/5

        Delay Speed%/20

		xx = xx + 5
		yy = Size/2

		Rect(GraphicsWidth()/2+xx,GraphicsHeight()/2+yy,Size/50,Size/50)

  Next


		Delay Speed%
		
		yy = Size/2
		yyy = Size/2
		t=xx
		
  For i = 1 To Size/15

        Delay Speed%/200

		xx = -Size/2 + 5
		yy = yy - Size/100

		Rect(GraphicsWidth()/2+xx,GraphicsHeight()/2+yy,Size/50,Size/50)

       Delay Speed%/200


		xxx = t
		yyy = yyy - Size/100

		Rect(GraphicsWidth()/2+xxx,GraphicsHeight()/2+yyy,Size/50,Size/50)

       Delay Speed%/200

  Next

		Delay Speed%/500

        Color 255,0,0
		Text GraphicsWidth()/2,GraphicsHeight()/2,"I'm so happy!"
		
  WaitKey
