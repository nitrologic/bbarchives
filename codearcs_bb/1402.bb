; ID: 1402
; Author: Cygnus
; Date: 2005-06-16 11:36:33
; Title: Waveform Functions
; Description: Synthesizer Wave Shape functions

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
Function wave#(wt,time#,freq#)
Local ret#,frq#=(time/100000.0)*360.0*freq*.5
If wt=1 Then ret#=Sin(frq*2) ;Sine
If wt=2 Then ret#=(((Int(frq*2 Mod 360)/180) Mod 2)-.5) ;Square
If wt=3 Then ret=(((frq*2 Mod 180)/180.0)-.5)*2 ;Saw
If wt=4 Then ret=((Abs(((90-(frq Mod 180)) Mod 180)/90.0))-.5) ;Saw.. bit zhit to be honest..
Return ret
End Function
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;






Global scx=640,scy=480

Graphics scx,scy,16,2
SetBuffer BackBuffer()

Dim oy#(3); store old poisions. Used for making lines


Repeat
	time=time+1
	Delay 1
	For n=1 To 3
		y#=wave(n,time*100,1)
		y#=(scy/2.0)+y*(scy/2.0)
		r=0:g=0:b=0
		If n=1 Then r=255 Else If n=2 Then g=255 Else If n=3 Then b=255
		Color r,g,b
		Line scx-2,oy(n),scx-1,y
		oy(n)=y
	Next
	
	CopyRect 0,0,scx,scy,-1,0
	Color 0,0,0
	Line scx-1,0,scx-1,scy
	
	Flip 0
Until KeyDown(1)
