; ID: 1497
; Author: John Blackledge
; Date: 2005-10-20 15:02:22
; Title: Render Tweening
; Description: I thought it was about time I posted this (it was previously on BlitzCoder) - grief - it's two years old!

;--------------------------------------------
; TweenTest.bb
; John Blackledge 20/9/03
; john@tla-data.co.uk
;--------------------------------------------

; --- Set Graphics mode, create a camera, create a light.
Graphics3D 800,600,0,2
camera = CreateCamera()
PositionEntity camera,0,0,-15
light = CreateLight()
PositionEntity light,20,20,40

; --- Set the Tweening/timer constants and initial variables.
Const FPS=30
period=1000/FPS
time=MilliSecs()-period

; --- Create an big entity of 1000 spheres parented to a piv.
piv=CreatePivot()
PositionEntity piv,0,0,0
For cnt = 1 To 1000
	cube = CreateSphere(16,piv)
	PositionEntity cube,Float(Rnd(-5,+5)),Float(Rnd(-5,+5)),Float(Rnd(-5,+5)),False
	EntityColor cube,Rnd(0,255),Rnd(0,255),Rnd(0,255)
Next

; --- The main loop start - based on Mark's Castle Demo code..
While Not KeyHit(1)
	Repeat
		elapsed=MilliSecs()-time
	Until elapsed
	ticks=elapsed/period	;how many 'frames' have elapsed	
	tween#=Float(elapsed Mod period)/Float(period)	;fractional remainder
	For k=1 To ticks
		time=time+period
		If k=ticks Then CaptureWorld

		; --- Rotate our big spheres entity.
		; --- This will stress the system, especially windowed.
		TurnEntity piv,1,2,3
		; ----------------------------------------

		UpdateWorld

		; --- Count the internal updates per second.
		; --- This will show what sort of progress is actually happening as regards
		; --- the updating of animated figures, water lapping etc?
		UpdateWorldCounter()
		; ----------------------------------------

	Next
	RenderWorld tween#

	; --- Count the number of updates per second being sent to the screen.
	; --- This is actually what people are referring to 
	; --- when they talk about frames per second.
	RenderWorldCounter1()
	RenderWorldCounter2()
	CounterDisplay()
	; ----------------------------------------

	Flip
Wend
End

; --- I've kept the counters and displays as external functions
; --- (rather than embedded in the code) so as to simplify the
; --- reading of both areas.
Global UWcurTime,UWcheckTime,UWcurFPS,UWcounter
Function UpdateWorldCounter()
	UWcurTime = MilliSecs()
	If UWcurTime => UWcheckTime Then
		UWcheckTime = UWcurTime + 1000
		UWcurFPS = UWcounter
		UWcounter = 0
	Else
		UWcounter = UWcounter + 1
	End If
End Function

Global RW1curTime,RW1checkTime,RW1curFPS,RW1fpscounter
Function RenderWorldCounter1()
	RW1curTime = MilliSecs()
	If RW1curTime > RW1checkTime Then
		RW1checkTime = RW1curTime + 1000
		RW1curFPS = RW1fpscounter
		RW1fpscounter = 0
	Else
		RW1fpscounter = RW1fpscounter + 1
	End If
End Function

Global RW2counter,RW2time,RW2framerate
; --- This is a different fps counter that I came across
; --- so I thought I'd add it in as a contrast.
Function RenderWorldCounter2()
	RW2counter = RW2counter+1
	If RW2time=0 Then RW2time=MilliSecs()
	If RW2time+1001 < MilliSecs()
		RW2framerate = RW2counter
		RW2counter=0
		RW2time=MilliSecs()
	EndIf
End Function

Function CounterDisplay()
	Color 0,0,0
	Rect 0,0,300,100,1
	Color 255,255,255
	Text 0, 0,"Const FPS " + FPS + " "
	Text 0,20,"UpdateWorld() / sec " + UWcurFPS + " "
	Text 0,40,"RenderWorld() / sec 1st method " + RW1curFPS + " "
	Text 0,60,"RenderWorld() / sec 2nd method "+RW2framerate
End Function
