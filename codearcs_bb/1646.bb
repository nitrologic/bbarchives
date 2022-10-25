; ID: 1646
; Author: t3K|Mac
; Date: 2006-03-22 06:28:49
; Title: Graphical GameTimer
; Description: A simple Gametimer using Quads

; Graphical GameTimer Example
; ---------------------------
; by t3K|Mac
; load timer.bmp from here:
; www.the3kings.de/test/timer.bmp

Graphics3D 800,600,16,1
SetBuffer BackBuffer()

camera=CreateCamera()
light=CreateLight()

timer_texture=LoadAnimTexture("timer.bmp",1+2+16+32,16,16,0,11)
If Not timer_texture Then RuntimeError("timer.bmp is missing!")
timer_pivot=CreatePivot()
tim1=CreateQuad(timer_pivot)
ScaleEntity tim1,.1,.1,.1
EntityFX tim1,1
tim2=CopyEntity(tim1,timer_pivot)
tim3=CopyEntity(tim1,timer_pivot)
tim4=CopyEntity(tim1,timer_pivot)
tim5=CopyEntity(tim1,timer_pivot)
PositionEntity tim1,-.35,0,3
PositionEntity tim2,-.15,0,3
PositionEntity tim3,0,0,3
PositionEntity tim4,.12,0,3
PositionEntity tim5,.32,0,3
EntityTexture tim3,timer_texture,10 ; ":" texturize
PositionEntity timer_pivot,0,0,0


timer_duration=75 ; timer duration in secs

start_time=MilliSecs()

While Not KeyDown( 1 )
	Cls
	
	Gosub UpdateGameTimer
	
	RenderWorld
	;VWait
	Flip False
Wend

End

.UpdateGameTimer
	t=timer_duration-((MilliSecs()-start_time)/1000)
	
	If t>=0
		If t<>old_t Then
			; update timerquads
			duration$=Str (t/60.0)
			pos=Instr(duration$,".")
			min$=Left$(duration$,pos-1)
			sek=Float(Mid$(duration$,pos,10))*60
			timer$=Right$("0"+min$,2)+Right$("0"+sek,2)
			old_t=t
			EntityTexture tim1,timer_texture,Int(Mid$(timer$,1,1))
			EntityTexture tim2,timer_texture,Int(Mid$(timer$,2,1))
			EntityTexture tim4,timer_texture,Int(Mid$(timer$,3,1))
			EntityTexture tim5,timer_texture,Int(Mid$(timer$,4,1))		
		EndIf
	Else
		Text 400,300,"Timer has expired!",True,True : Flip : WaitKey : End
	EndIf
Return

Function CreateQuad(parent=0)
	m=CreateMesh()
	s=CreateSurface(m)
	AddVertex s,-1,+1,-1,0,0
	AddVertex s,+1,+1,-1,1,0
	AddVertex s,+1,-1,-1,1,1
	AddVertex s,-1,-1,-1,0,1
	AddTriangle s,0,1,2
	AddTriangle s,0,2,3
	;FlipMesh m
	If parent>0
		EntityParent m,parent
	EndIf
	Return m
End Function
