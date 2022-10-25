; ID: 2064
; Author: chwaga
; Date: 2007-07-13 00:13:53
; Title: Random &amp; Graphics
; Description: fun with the rnd command and fun visual effects

AppTitle "Time Waster"

Graphics 1280,1024,32,2

SeedRnd MilliSecs()
Global n=0
;SORRY FOR THE CRYPTIC VARIABLE NAMES
;oldx
Global ox
;oldy
Global oy
;newx
Global nx=640
;newy
Global ny=512
;etch-e-sketch mode
Global ees=False 
;keyx (etch-e-sketch)
Global kx=640
;keyy (etch-e-sketch)
Global ky=512
;font...for some reason i had to repeat the font=loadfont() thing every time i changed graphics-mode
Global font=LoadFont("Arial",36,1,0,0)
;incomplete
Global s
;linex1
Global lx1=Rnd(0,1280)
;liney1
Global ly1=Rnd(0,1024)
;linex2
Global lx2=Rnd(0,1280)
;liney2
Global ly2=Rnd(0,1024)
;line drawing speed
Global speed=50
;set the font
SetFont font 


Print "WELCOME TO THE TIME-WASTER"
Print "PRESS ANY KEY (except escape) TO CONTINUE"
Print "FOLLOW GIVEN DIRECTIONS PER 'PHASE' AND" 
Print "PRESS THE ESCAPE KEY To CONTINUE To THE Next PHASE"
Print "AT ANY TIME PRESS F10 TO TAKE A SCREENSHOT (named 'screenshot.bmp')
Print "PRESS KEYS 1-7 TO SWITCH AROUND THE PROGRAM & PHASES"
Print "0 to exit"
WaitKey 

.phase1
While Not KeyHit(1)
	Text 500,950,"PHASE ONE"
	Color Rnd(0,255),Rnd(0,255),Rnd(0,255)
	Rect Rnd(0,1280),Rnd(0,1024),Rnd(0,1280),Rnd(0,1024),Rnd(0,1)
	If KeyHit(68) Then SaveBuffer(FrontBuffer(),"screenshot.bmp")
;every time you see the below goto's, its the code set to use keys 1-7 and 0 to jump around the program using labels
	If KeyHit(2) Then Goto phase1
	If KeyHit(3) Then Goto phase2
	If KeyHit(4) Then Goto phase3
	If KeyHit(5) Then Goto phase4
	If KeyHit(6) Then Goto phase5
	If KeyHit(7) Then Goto phase6
	If KeyHit(8) Then Goto phase7
	If KeyHit(11) Then Goto el



	For n=0 To 1000
		WritePixel Rnd(0,1280),Rnd(0,1024),ReadPixel(Rnd(0,1280),Rnd(0,1024))
		n=n+1

	Next 
n=0
Wend
n=0


.phase2
Cls 
While Not KeyHit(1)
	If KeyHit(2) Then Goto phase1
	If KeyHit(3) Then Goto phase2
	If KeyHit(4) Then Goto phase3
	If KeyHit(5) Then Goto phase4
	If KeyHit(6) Then Goto phase5
	If KeyHit(7) Then Goto phase6
	If KeyHit(8) Then Goto phase7
	If KeyHit(11) Then Goto el
	Text 500,950,"PHASE TWO"
	WritePixel Rnd(0,1280),Rnd(0,1024),ReadPixel(401,401)
	Color Rnd(0,255),Rnd(0,255),Rnd(0,255)
	Rect 400,400,500,500,1
	If KeyHit(68) Then SaveBuffer(FrontBuffer(),"screenshot.bmp")

Wend


.phase3
Cls
While Not KeyHit(1)
	If KeyHit(2) Then Goto phase1
	If KeyHit(3) Then Goto phase2
	If KeyHit(4) Then Goto phase3
	If KeyHit(5) Then Goto phase4
	If KeyHit(6) Then Goto phase5
	If KeyHit(7) Then Goto phase6
	If KeyHit(8) Then Goto phase7
	If KeyHit(11) Then Goto el
	Text 500,950,"PHASE THREE"
	Color Rnd(0,255),Rnd(0,255),Rnd(0,255)
	If KeyHit(68) Then SaveBuffer(FrontBuffer(),"screenshot.bmp")


	For n=0 To 1000
		WritePixel Rnd(0,1280),Rnd(0,1024),ReadPixel(Rnd(0,1280),Rnd(0,1024))
		n=n+1
 
	Next 
	Oval Rnd(0,1280),Rnd(0,1024),Rnd(0,1280),Rnd(0,1024),Rnd(0,1)

	Delay 16.666

Wend 



.phase4
Cls
While Not KeyHit(1)
	Text 500,950,"PHASE FOUR"
	If KeyHit(2) Then Goto phase1
	If KeyHit(3) Then Goto phase2
	If KeyHit(4) Then Goto phase3
	If KeyHit(5) Then Goto phase4
	If KeyHit(6) Then Goto phase5
	If KeyHit(7) Then Goto phase6
	If KeyHit(8) Then Goto phase7
	If KeyHit(11) Then Goto el
	Color Rnd(0,255),Rnd(0,255),Rnd(0,255)
	Rect Rnd(0,1280),Rnd(0,1024),Rnd(0,1280),Rnd(0,1024),Rnd(0,1)
	If KeyHit(68) Then SaveBuffer(FrontBuffer(),"screenshot.bmp")


	For n=0 To 1000
		WritePixel Rnd(0,1280),Rnd(0,1024),ReadPixel(Rnd(0,1280),Rnd(0,1024))
		n=n+1

	Next 
	n=0
	Oval Rnd(0,1280),Rnd(0,1024),Rnd(0,1280),Rnd(0,1024),Rnd(0,1)

Wend 

.phase5
Cls 
MoveMouse 640,512

While Not KeyHit(1)
	If KeyHit(68) Then SaveBuffer(FrontBuffer(),"screenshot.bmp")


	Text 500,0,"PRESS THE SPACE-BAR TO CLEAR THE SCREEN"
	Text 400,40,"PRESS ENTER TO ENTER/EXIT ETCH-E-SKETCH MODE"
	Text 500,950,"PHASE FIVE"
	If KeyHit(2) Then Goto phase1
	If KeyHit(3) Then Goto phase2
	If KeyHit(4) Then Goto phase3
	If KeyHit(5) Then Goto phase4
	If KeyHit(6) Then Goto phase5
	If KeyHit(7) Then Goto phase6
	If KeyHit(8) Then Goto phase7
	If KeyHit(11) Then Goto el

	Color Rnd(0,255),Rnd(0,255),Rnd(0,255)
	Rect 0,0,1,1,1

	If Not KeyDown(20) And ees=False 
		ox=MouseX()
		oy=MouseY()

		connect
		nx=MouseX()
		ny=MouseY()
		connect 

	EndIf 

	If KeyHit(28) And ees=False Then 
		ees=True 
		Cls 
	Else If ees=True And KeyHit(28)
		ees=False 
	EndIf 

	While ees=True
		Color Rnd(0,255),Rnd(0,255),Rnd(0,255)
		Rect 0,0,1,1,1


		WritePixel kx,ky,ReadPixel(0,0)
		If KeyDown(200) Then ky=ky-1
		If KeyDown(208) Then ky=ky+1
		If KeyDown(203) Then kx=kx-1
		If KeyDown(205) Then kx=kx+1

		Delay 10
		If KeyHit(28) Then 
			ees=False 
			Cls
		EndIf 

	Wend 

  

	If KeyHit(57) Then Cls 

Wend 

.phase6

Cls
Graphics3D 1280,1024,32,2
SetBuffer BackBuffer()
font=LoadFont("Arial",36,1,0,0)

SetFont font 

;While Not KeyHit(1)

campivot=CreatePivot()
PositionEntity campivot,0,0,0
camera=CreateCamera(campivot)
PositionEntity camera,0,0,-10

;EntityParent camera,campivot

light=CreateLight()
PositionEntity light,30,70,0

cube=CreateCube()
PositionEntity cube,0,0,0

cube2=CreateCube()
PositionEntity cube2,0,30,0
ScaleEntity cube2,50,50,50

sphere=CreateSphere(20)
PositionEntity sphere,5,0,0

cone=CreateCone()
PositionEntity cone,-5,0,0

FlipMesh cube2


tex=CreateTexture(256,256)
EntityTexture cube,tex
EntityTexture sphere,tex 
EntityTexture cone,tex 
EntityTexture cube2,tex

While Not KeyHit(1)
	If KeyHit(2) Then Goto phase1
	If KeyHit(3) Then Goto phase2
	If KeyHit(4) Then Goto phase3
	If KeyHit(5) Then Goto phase4
	If KeyHit(6) Then Goto phase5
	If KeyHit(7) Then Goto phase6
	If KeyHit(8) Then Goto phase7
	If KeyHit(11) Then Goto el
	If KeyHit(68) Then SaveBuffer(FrontBuffer(),"screenshot.bmp")


	If KeyDown(203) Then TurnEntity campivot,0,1,0
	If KeyDown(205) Then TurnEntity campivot,0,-1,0



;EVERYTHING FROM HERE TO RESETTING TO BACKBUFFER IS PUT INSIDE THE TEXTURE tex
	SetBuffer TextureBuffer(tex)
		Color Rnd(0,255),Rnd(0,255),Rnd(0,255)
		Rect Rnd(0,256),Rnd(0,256),Rnd(0,256),Rnd(0,256),Rnd(0,1)
		Oval Rnd(0,256),Rnd(0,256),Rnd(0,256),Rnd(0,256),Rnd(0,1)

	SetBuffer BackBuffer()

	TurnEntity cube,3,3,3
	TurnEntity sphere,-4,-3,-2
	TurnEntity cone,-3,3,-3


RenderWorld
	Text 500,950,"PHASE SIX"

	If Not KeyDown(20)  
		ox=MouseX()
		oy=MouseY()

		connect
		nx=MouseX()
		ny=MouseY()
		connect 

	EndIf 


Flip
Wend 


.phase7
Cls 
 
 
Graphics 1280,1024,32,2
font=LoadFont("Arial",36,1,0,0)

SetFont font
While Not KeyHit(1)
	If KeyHit(2) Then Goto phase1
	If KeyHit(3) Then Goto phase2
	If KeyHit(4) Then Goto phase3
	If KeyHit(5) Then Goto phase4
	If KeyHit(6) Then Goto phase5
	If KeyHit(7) Then Goto phase6
	If KeyHit(8) Then Goto phase7
	If KeyHit(11) Then Goto el
	If KeyHit(68) Then 
		SaveBuffer(BackBuffer(),"screenshot.bmp")
	EndIf 

	Color Rnd(0,255),Rnd(0,255),Rnd(0,255)
	Text 500,0,"PRESS THE SPACE-BAR TO CLEAR THE SCREEN"
	Text 400,40,"PRESS NUM + AND NUM - TO DECREASE AND INCREASE "
	Text 500,80,"THE SPEED THAT THE LINES DRAW AT"
	Text 0,0,"DRAWING SPEED = "+speed
	Text 0,40,"(low is fast :D)"

	Text 500,950,"PHASE SEVEN"
	lx2=Rnd(0,1280) 
	ly2=Rnd(0,1024)
	Line lx1,ly1,lx2,ly2
	lx1=lx2
	ly1=ly2

	If KeyDown(78) And speed > 3 Then speed=speed-1
	If KeyDown(74) Then speed=speed+1

	If KeyHit(57) Then Cls 
Delay speed

Wend 
.el
End 


;Cls
;Graphics3D 1280,1024,32,2
;SetBuffer BackBuffer()


Function connect()

	Line ox,oy,nx,ny

End Function
