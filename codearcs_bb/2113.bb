; ID: 2113
; Author: Vignoli
; Date: 2007-09-29 04:49:12
; Title: chaos cube
; Description: theory of chaos

; chaos cube

chn=30 ; change this for more or less chaos

Graphics3D 800,600,32,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()

camera=CreateCamera()
AmbientLight 200,200,200
PositionEntity camera,0,0,-300

Dim ang(chn,3)
Dim sens(chn,3)
Dim speed#(chn,3)

For i=1 To chn
	For j=1 To 3
		ang(i,j)=Rand(0,359)
		sens(i,j)=Rand(0,1)
		If sens(i,j)=0 Then sens(i,j)=-1
		speed#(i,j)=Rand(5,200)
		speed#(i,j)=speed#(i,j)/10.0
	Next
Next

cube=CreateCube()
ScaleEntity cube,10,5,20

Repeat

If KeyDown(1) Then End

PositionEntity cube,0,0,0
For i=1 To chn
	RotateEntity cube,ang(i,1),ang(i,2),ang(i,3)
	MoveEntity cube,speed#(i,1),speed#(i,2),speed#(i,3)
	For j=1 To 3
		ang(i,j)=ang(i,j)+Int(sens(i,j)*speed#(i,j))
		While ang(i,j)<0
			ang(i,j)=ang(i,j)+360
		Wend
		While ang(i,j)>359
			ang(i,j)=ang(i,j)-360
		Wend
		If KeyDown(1) Then End
	Next
Next

UpdateWorld
RenderWorld
Color 255,255,255
Text 0,0,"Push [Return] to randomize the chaos"
Flip

If KeyDown(28)
SeedRnd MilliSecs()
For i=1 To chn
	For j=1 To 3
		ang(i,j)=Rand(0,359)
		sens(i,j)=Rand(0,1)
		If sens(i,j)=0 Then sens(i,j)=-1
		speed#(i,j)=Rand(5,200)
		speed#(i,j)=speed#(i,j)/10.0
	Next
Next
While KeyDown(28) : Wend
EndIf

tt1=MilliSecs()
While MilliSecs()-tt1<50
	If KeyDown(1) Then End
Wend


Forever

End
