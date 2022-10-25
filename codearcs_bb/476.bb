; ID: 476
; Author: Markus Rauch
; Date: 2002-11-03 17:10:25
; Title: Aqua Effect on 3D Cube
; Description: Aqua Effect

; Aqua Effect (C) 2002 by M.Rauch from Germany

; If DebugMode = True Then it is very slow !

; MR 03.11.2002

.Top

Graphics3D 640,480,16,1 

SetBuffer BackBuffer()

;------------------------------------------------------------------------- Camera

.Cam

Global Camera =CreateCamera()

PositionEntity Camera,0,0,-10

CameraClsColor Camera,0,20,0

;------------------------------------------------------------------------- Texture

.Texture

;a Texture size 128 x 128 is very fast

Global ReflectImage=LoadTexture("Texture.bmp")    ;<--- 
Global ReflectImageB=TextureBuffer(ReflectImage)

Global Output=CreateTexture(TextureWidth(ReflectImage),TextureHeight(ReflectImage),1) ;The same size 
Global OutputB=TextureBuffer(Output)

;-------------------------------------------------------------------------

.Entitys

 Global Cube=CreateCube()

 ScaleMesh Cube,3,3,3

 EntityTexture Cube,Output

;--------------------------------------------------------

.Light

 Global Light1 =CreateLight(1)
 Global Light2 =CreateLight(1)
 Global Light3 =CreateLight(1)

 LightRange Light1 ,50
 LightRange Light2 ,50
 LightRange Light3 ,50

 LightColor Light1, 64, 64, 64 
 LightColor Light2,255,255,255 
 LightColor Light3, 64, 64, 64 

 PositionEntity Light1 ,-10, 10,-10
 PositionEntity Light2 ,  0,  0,-10
 PositionEntity Light3 , 10,-10,-10

 AmbientLight 0,0,0 

;------------------------------------------------------------------------- WaterSettings

.WaterSettings

Global WATERSIZE=TextureWidth(ReflectImage) ;like 128 or 256

Global RainCount=0

Global DripRadius = 12
Global DripRadiusSqr = DripRadius * DripRadius
Global DampingFactor# = 0.04 ;Values For damping from 0.04 - 0.0001 look pretty good (the Buffer must in float)

.WaterBuffers

Global BufferSize=(WATERSIZE * WATERSIZE)

Dim ReadBuffer #(BufferSize)
Dim WriteBuffer#(BufferSize)
Dim TempBuffer #(BufferSize)

Local i

For i = 0 To BufferSize 
 TempBuffer (i) = 0
 ReadBuffer (i) = 0
 WriteBuffer(i) = 0
Next

;------------------------------------------------------------------------- MainLoop

.MainLoop

While Not KeyHit(1) ; 1=Escape 

 Local ti#

 ti=MilliSecs()

 SwapBuffers
 Show

 CheckMouse ;<- Press left Button and move the Mouse

 TurnEntity Cube,1,-1.5,0

 RenderWorld

 Rain ;<- automatic 

 ProcessWater

 While Abs(MilliSecs()-ti)<10 
 Wend

 Flip
Wend
End

;-------------------------------------------------------------------------

.Buffers

;-------------------------------------------------------------------------

Function SetBufferR(x,y,value#)

 ReadBuffer(x+y*WATERSIZE)=value

End Function

;-------------------------------------------------------------------------

Function SetBufferW(x,y,value#)

 If value >  32 Then value =  32
 If value < -32 Then value = -32

 WriteBuffer(x+y*WATERSIZE)=value

End Function

;-------------------------------------------------------------------------

Function GetBufferR#(x,y)

 Return ReadBuffer(x+y*WATERSIZE)

End Function

;-------------------------------------------------------------------------

Function GetBufferW#(x,y)

 Return WriteBuffer(x+y*WATERSIZE)

End Function

;-------------------------------------------------------------------------

Function SwapBuffers() 

 Local i

 ;Swap the buffers !

 For i = 0 To BufferSize 
  TempBuffer(i) =ReadBuffer(i)  
  ReadBuffer(i) =WriteBuffer(i)  
  WriteBuffer(i)=TempBuffer(i)
 Next

End Function

;-------------------------------------------------------------------------

.RenderTexture

Function Show()

 Local x,y
 Local xoff,yoff
 Local xm,ym
 Local pix,pix2
 Local r,g,b,a
 Local bu 

 xm=GraphicsWidth() /2-WATERSIZE/2
 ym=GraphicsHeight()/2-WATERSIZE/2

 ;-----------------------------------------------

 LockBuffer ReflectImageB
 LockBuffer OutputB

 Local cnt=0 

  y=0
  While y < WATERSIZE
	  x=0
    While x < WATERSIZE

     xoff = x
		 If x > 0 And x < WATERSIZE - 1 Then
			xoff =xoff- (ReadBuffer(cnt - 1))
			xoff =xoff+ (ReadBuffer(cnt + 1))
		 EndIf

		 yoff = y
		 If y > 0 And y < WATERSIZE - 1 Then
			yoff =yoff- ReadBuffer(cnt - WATERSIZE)
			yoff =yoff+ ReadBuffer(cnt + WATERSIZE)
     EndIf

     If xoff < 0 Then xoff = 0
     If yoff < 0 Then yoff = 0
     If xoff > WATERSIZE-1 Then xoff = WATERSIZE-1
     If yoff > WATERSIZE-1 Then yoff = WATERSIZE-1

     pix=ReadPixelFast(xoff,yoff,ReflectImageB)
     r=(pix And $ff0000)/$10000
     g=(pix And $ff00)/$100
     b=(pix And $ff)

     ;r=128 ;<- only color
     ;g=128
     ;b=128

     bu=ReadBuffer(cnt)
     r = r + bu
     g = g + bu
     b = b + bu
     If r < 0 Then r = 0
     If g < 0 Then g = 0
     If b < 0 Then b = 0
     If r > 255 Then r = 255
     If g > 255 Then g = 255
     If b > 255 Then b = 255

     pix2=ARGB(r,g,b)
     WritePixelFast x,y,pix2,OutputB

     cnt=cnt+1

     x=x+1
		Wend
   y=y+1
	Wend

 UnlockBuffer OutputB
 UnlockBuffer ReflectImageB

End Function

;-------------------------------------------------------------------------

.Helpers

Function ARGB(r,g,b)

 ;Return ((128 * $1000000) Or (r * $10000) Or (g * $100) Or b)
 Return ((r * $10000) Or (g * $100) Or b)

End Function

;-------------------------------------------------------------------------

Function SquaredDist(sx, sy, dx, dy)

 ;Find the Squared distance between two 2D points

 Return ((dx - sx) * (dx - sx)) + ((dy - sy) * (dy - sy))

End Function

;-------------------------------------------------------------------------

.MouseInput

Function CheckMouse()

 Local mx,my

 mx=MouseX()
 my=MouseY()

 If mx<0 Then mx=0
 If my<0 Then my=0

 If mx>WATERSIZE-1 Then mx=WATERSIZE-1
 If my>WATERSIZE-1 Then my=WATERSIZE-1

 WritePixel mx,my,ARGB(128,128,128),OutputB  

 If MouseDown(1) Then
  MakeDrip mx,my,4
 EndIf

End Function

;-------------------------------------------------------------------------

.RainInParadise

Function Rain()

 Local mx,my

 Local i

 RainCount=RainCount+1
 
 If RainCount > 10 Then

  RainCount=0

  SeedRnd MilliSecs()

  mx=Rnd(0,WATERSIZE-1)
  my=Rnd(0,WATERSIZE-1)

  MakeDrip mx,my,4

 EndIf

End Function

;-------------------------------------------------------------------------

.WaterDrip

Function MakeDrip(xm , ym , depth) 

 ;Creates an initial drip in the water Field

 ;DebugLog "MakeDrip "+x+" "+y+" "+depth

	Local x,y
	Local dist,finaldepth#

  y=ym - DripRadius
  While y < ym + DripRadius
   x=xm - DripRadius
   While x < xm + DripRadius
		If x => 0 And y => 0 And x < WATERSIZE And y < WATERSIZE Then			
				dist = SquaredDist(x,y,xm,ym)
				If dist < DripRadiusSqr Then				
					finaldepth = (depth * DripRadius - Sqr(dist))/DripRadius
					If finaldepth >  127 Then finaldepth =  127
					If finaldepth < -127 Then finaldepth = -127
					SetBufferW x,y,finaldepth
				EndIf
		EndIf
    x=x+1
	 Wend		
   y=y+1
  Wend

End Function

;-------------------------------------------------------------------------

.WaterInAction

Function ProcessWater() 

 ;Calculate New values For the water height Field

 Local x,y
 Local v#
	
  y=2
  While y < WATERSIZE-2
	  x=2
    While x < WATERSIZE-2
		
			;Sample a "circle" around the center point
      v =0
			v = v +	GetBufferR(x-2,y)
			v = v +	GetBufferR(x+2,y)
			v = v +	GetBufferR(x  ,y-2)
			v = v +	GetBufferR(x  ,y+2)
			v = v +	GetBufferR(x-1,y)
			v = v +	GetBufferR(x+1,y)
			v = v +	GetBufferR(x  ,y-1)
			v = v +	GetBufferR(x  ,y+1)
			v = v +	GetBufferR(x-1,y-1)
			v = v +	GetBufferR(x+1,y-1)
			v = v +	GetBufferR(x-1,y+1)
			v = v +	GetBufferR(x+1,y+1)

			v = v / 6.0 		
			v = v - GetBufferW(x,y)		
      v = v - (v * DampingFactor)

			SetBufferW (x,y,v)

     x=x+1
		Wend
   y=y+1
	Wend

End Function

;-------------------------------------------------------------------------
