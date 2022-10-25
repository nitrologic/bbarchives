; ID: 2416
; Author: Krischan
; Date: 2009-02-21 17:35:44
; Title: Realtime Procedural Planet Generator
; Description: Creates a whole Planet Texture in milliseconds!

; Realtime Procedural Planet Generator
; by Krischan webmaster(at)jaas.de
;
; creates a planet out of a combination of 4 pre-rendered planets out of 10 and three blending filters
; it is possible to create ten thousands of unique planet surfaces in 50-100 milliseconds!
; this is done by calculating the planet step by step so it doesn't consume lots of power and whole
; solar systems can be created while moving towards the system or even the planet

Graphics3D 800,600,32,2

; Declarations
Dim GradientR%(0),GradientG%(0),GradientB%(0),Prozent%(0),Rot%(0),Gruen%(0),Blau%(0)
Dim images%(9)
Dim Array%(0)
Global maxx%=512
Global maxy%=256
Global max%=maxx*maxy
Global pixelstep%=16384
Global movespeed#=0.1

; Randomize
SeedRnd MilliSecs()

; Create Color Gradient
Restore ClassMT : CreateGradient(9,255)

; Read source images
For i%=0 To 9 : images(i)=LoadBankImage("planet"+i+".img",maxx,maxy) : Next

; Create random array
CreateRandomArray(max)

; Create target texture
output=CreateTexture(maxx,maxy,16+32)
texbuff=TextureBuffer(output)

; Base color of texture (water)
SetBuffer texbuff
Color 17, 82,112
Rect 0,0,maxx,maxy,1
Color 255,255,255
SetBuffer BackBuffer()

; Planet
planet=CreateSphere(32)
ScaleEntity planet,10,10,10
PositionEntity planet,0,0,10
EntityFX planet,2
EntityTexture planet,output,0,1
TextureBlend output,2
EntityShininess planet,0.25

; Light
light=CreateLight(1)
AmbientLight 8,8,8

; Camera
camera=CreateCamera()
PositionEntity camera,20,0,0
CameraRange camera,0.1,1000

; Cursor centered, cam points to planet
MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
HidePointer()
PointEntity camera,planet

; World creation flag
newworld=True

; Time measurement start
time=MilliSecs()

; Main Loop
While Not KeyHit(1)
	
	; FPS measurement
    FPS_C=FPS_C+1 : If fms<MilliSecs() Then fms=MilliSecs()+1000 : FPS=FPS_C : FPS_C=0
	
	; Frame tweening
	Tween#=Float(MilliSecs()-FrameTime)/10.0 : FrameTime=MilliSecs()
	
	; Simple Steering
	mxs#=MouseXSpeed()
	mys#=MouseYSpeed()
	RotateEntity camera,EntityPitch(camera)+(mys#/5),EntityYaw(camera)-(mxs#/5),0
	If KeyDown(200) Then MoveEntity camera,0,0,movespeed*Tween
	If KeyDown(208) Then MoveEntity camera,0,0,-movespeed*Tween
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
	
	; Current time
	ms=MilliSecs()
	
	; SPACE and Newworld-Flag False? create a new map!
	If KeyHit(57) Then
		
		; Measure start time
		starttime=ms
		
		; Reset pixel counters
		i=0
		water%=0
		land%=0
		
		; Select 4 planet maps as base
		map$=ZERO(Rand(0,9999),4)
		
		; Extract Planet Codes
		i1=Int(Left(map,1))
		i2=Int(Mid(map,1,1))
		i2=Int(Mid(map,2,1))
		i4=Int(Right(map,1))
		
		; set Newworld-Flag True
		newworld=True
		
	EndIf
	
	; Turn Planet and Clouds a little bit
	TurnEntity planet,0,-0.1*Tween,0
	
	; SPACE, 30ms gone and Newworld-Flag true? go on!
	If ms>time And newworld Then
		
		roundstart=ms
		
		; Increase time
		time=ms+30
		
		; Calculate number of pixels to set
		adder%=(pixelstep/Tween)
		
		; Start/End for current loop
		start=i
		ende=i+adder
		
		; End greater than pixels? end=pixels
		If ende>max-1 Then ende=max-1
		
		; Lock Texturebuffer
		LockBuffer texbuff
		
		; Current Pixel loop
		For j=start To ende
			
			; Pixelincrement
			i=i+1
			
			; Pixel greater than pixel amount? End!
			If i>max-1 Then
				newworld=False
				Goto skip
			EndIf
				
			; Calculate Pixel X/Y-Position from the randomized array
			y=Int(Array(i)/maxx)
			x=Array(i)-(y*maxx)
			
			; get current pixels from bank
			offset=(y*maxx)+x
			r1=PeekByte(images(i1),offset)
			r2=PeekByte(images(i2),offset)
			r3=PeekByte(images(i3),offset)
			r4=PeekByte(images(i4),offset)
			
			; Mix with Image Filter
			r=Average(r1,r2)
			r=Lighten(r,r3)
			r=HardLight(r,r4)
			
			; Legend of usesful Filter combinations:
			;
			; S = Softlight
			; M = Multiply
			; L = Lighten
			; A = Average
			; H = Hardlight
			; O = Overlay
			; E = exclusion
			; D = Difference
			; N = Negation
			;
			; SSM =  ~ 99% Land
			; SSS = 25-75% Land
			; SSL = 75-25% Land, flat
			; ALH = 35-65% Land, continental
			; AMM =   100% Land, mountaineous
			; OLS = 25-75% Land
			; OLC =   <10% Land, oceancic
			; MMM =   100% Land, snowy mountains
			; EDN = different, earthlike, Archipelagos
			; MNS = different, earthlike, Archipelagos
			
			; Above 128: below water, otherwise Land
			If r>=128 Then water=water+1 Else land=land+1
			
			; Target Color from Gradient
			rgb=GradientR(r)*$10000+GradientG(r)*$100+GradientB(r)
			
			; Write to Texturebuffer
			WritePixelFast x,y,rgb,texbuff
			
		Next
		
		.skip
		
		endtime=MilliSecs()
		
		; Unlock Texturebuffer
		UnlockBuffer texbuff
		
		; Calculate amount of used time
		midtime=(midtime+(endtime-roundstart))/2.0
		
	EndIf
	
	RenderWorld
	
	; Statistics
	Text 0,  0,"Planet Source Maps: "+map
	Text 0, 15,"Pixels blended....: "+(i*100)/max+"%"
	Text 0, 30,"Transition Time...: "+(endtime-starttime)+"ms"
	Text 0, 45,"Used ms per cycle.: "+midtime+"ms"
	Text 0, 60,"Pixels per cycle..: "+adder
	Text 0, 75,"Water coverage....: "+(water*100.0)/max+"%"
	Text 0, 90,"Land coverage.....: "+(land*100.0)/max+"%"
	Text 0,105,"FPS...............: "+FPS
	Text 0,120,"Tris rendered.....: "+TrisRendered()
	
	Flip 0
	
Wend

End

; Soft Light Filter
Function SoftLight(a%,b%)
	Local c%
	c=a*b Shr 8
	Return (c+a*(255-((255-a)*(255-b) Shr 8)-c) Shr 8)
End Function

; Hard Light Filter
Function HardLight(a%,b%)
	If b<128 Then Return (a*b) Shr 7 Else Return 255-((255-b)*(255-a) Shr 7)
End Function

; Difference Filter
Function Difference(a%,b%)
	Return Abs(a-b)
End Function

; Multiply Filter
Function Multiply(a%,b%)
	Return (a*b) Shr 8
End Function

; Average Filter
Function Average(a%,b%)
	Return (a+b) Shr 1
End Function

; Screen Filter
Function Screen(a%,b%)
	Return 255-((255-a)*(255-b) Shr 8)
End Function

; Lighten Filter
Function Lighten(a%,b%)
	If a>b Then Return a Else Return b
End Function

; Darken Filter
Function Darken(a%,b%)
	If a<b Then Return a Else Return b
End Function

; Negative Filter
Function Negation(a%,b%)
	Return 255-Abs(255-a-b)
End Function

; Exclusion Filter
Function Exclusion(a%,b%)
	Return a+b-(a*b Shr 7)
End Function

; Overlay Filter
Function Overlay(a%,b%)
	If a<128 Then Return (a*b) Shr 7 Else Return 255-((255-a)*(255-b) Shr 7)
End Function

; Color Burn Filter
Function ColorDodge(a%,b%)
	If b=255 Then
		Return 255
	Else
		Local c%=Floor((a Shl 8)/(255-b))
		If c>255 Then Return 255 Else Return c
	EndIf
End Function

; Create Gradient
Function CreateGradient(colors%,steps%)
	
	Dim GradientR%(steps),GradientG%(steps),GradientB%(steps),Prozent%(colors),Rot%(colors),Gruen%(colors),Blau%(colors)
	
	Local i%,pos1%,pos2%,pdiff%
	Local rdiff%,gdiff%,bdiff%
	Local rstep#,gstep#,bstep#
	Local counter%=1
	
	; read color codes
	For i=1 To colors : Read Prozent(i),Rot(i),Gruen(i),Blau(i) : Next
	
    ; calculate gradient
	While counter<colors
		
        ; transform percent value into step position
		pos1=Prozent(counter)*steps/100
		pos2=Prozent(counter+1)*steps/100
		
        ; calculate position difference
		pdiff=pos2-pos1
		
        ; calculate color difference
		rdiff%=Rot(counter)-Rot(counter+1)
		gdiff%=Gruen(counter)-Gruen(counter+1)
		bdiff%=Blau(counter)-Blau(counter+1)
		
        ; calculate color steps
		rstep#=rdiff*1.0/pdiff
		gstep#=gdiff*1.0/pdiff
		bstep#=bdiff*1.0/pdiff
		
        ; calculate "in-between" color codes
		For i=0 To pdiff
			
			GradientR(pos1+i)=Int(Rot(counter)-(rstep*i))
			GradientG(pos1+i)=Int(Gruen(counter)-(gstep*i))
			GradientB(pos1+i)=Int(Blau(counter)-(bstep*i))
			
		Next
		
        ; increment counter
		counter=counter+1
		
	Wend
	
End Function

; create a random array where each value appears only once
Function CreateRandomArray(size%)
	
	; Redim Array
	Dim Array(size)
	
	; Fill with values
	For i = 0 To size-1 : Array(i) = i : Next
	
	; play dice
	For N% = 0 To size-2
		
		M% = Rand( N%, size - 1)
		Z% = Array(N%)
		
		Array(N%) = Array(M%)
		Array(M%) = Z%
		
	Next
	
End Function

; Fill a String with prefix Zeros
Function ZERO$(number%,lenght%=2)
	
	Local r$=""
	
	For i=1 To lenght-Len(Str(number))
		
		r$=r$+"0"
		
	Next
	
	Return r$+Str(number)
	
End Function

; Load a raw Image
Function LoadBankImage(filename$,width%,height%)
	
	Local f%=OpenFile(filename$)
	
	Local bank%=CreateBank(width*height)
	
	ReadBytes(bank,f,0,BankSize(bank))
	
	CloseFile f
	
	Return bank
	
End Function

; Color codes for an earthlike Planet
.ClassMT
Data   0,255,255,255
Data   5,179,179,179
Data  10,153,143, 92
Data  25,115,128, 77
Data  48, 42,102, 41
Data  50, 69,108,118
Data  52, 17, 82,112
Data  75,  9, 62, 92
Data 100,  2, 43, 68
