; ID: 734
; Author: zawran
; Date: 2003-07-03 17:23:33
; Title: Dot tunnel
; Description: a 3D tunnel effect in 2D

;	DOT TUNNEL / made by Zawran in July 2003

Graphics 800,600,32,2

;// SETUP VARS AND STUFF

Type ringdot
	Field x		;xpos
	Field y		;ypos
	Field a		;angel
	Field s		;speed
	Field z		;distance
End Type

speed = 5

;//

SetBuffer BackBuffer() 

While Not KeyHit(1) 
If Timer + 1000 <= MilliSecs() Timer = MilliSecs() : FPS_Real = FPS_Temp : FPS_Temp = 0
FPS_Temp = FPS_Temp + 1 : Text 0,580,"FPS: " + FPS_Real

;	Every 100 millisecond create another dot ring

If newring + 100 <= MilliSecs() Then
	newring = MilliSecs()
	Gosub createRing
	End If
	
t=0

;	DRAW THE RINGS EVERY FRAME

rd.ringdot = Last ringdot
Repeat
	Color 50+rd\z/2,50+rd\z/2,50+rd\z/2
	Rect rd\x+Sin(rd\a)*rd\z,rd\y+Cos(rd\a)*rd\z,2,2
	rd.ringdot = Before rd
	t=t+1
Until rd = Null

Color 255,255,255
	Text 0,0,"# Dots: "+t
		Text 340,0,"MOVE YOUR MOUSE!"

;	TIME UPDATE

If ringupd + 5 <= MilliSecs() Then
	ringupd = MilliSecs()
	For rd.ringdot = Each ringdot
		rd\z = rd\z + speed
		If rd\z > 400 Then Delete rd
	Next
	End If

Flip
Cls
Wend

For rd.ringdot = Each ringdot
	Delete rd
Next

End

.createRing
For a = 0 To 360 Step 5
	rd.ringdot = New ringdot
	rd\x = MouseX()
	rd\y = MouseY()
	rd\a = a
	rd\z = 5
	rd\s = 2
Next
Return
