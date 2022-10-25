; ID: 395
; Author: DarkNature
; Date: 2002-08-12 12:20:53
; Title: rolling sin wave chequered flag
; Description: makes a nice (not too realitic) chequered flag effect

Graphics 640,480,16

Dim stripe(300)

flag=CreateImage(300,200)
	
SetBuffer ImageBuffer(flag)
For x=0 To 300 Step 30
	For y=0 To 200 Step 20
		If tcolor=1 Then
			Color 0,0,0
			tcolor=0
		Else
			Color 175,175,175
			tcolor=1
		End If
		Rect x,y,30,20
	Next
Next

Color 175,175,175
Rect 0,0,300,200,False

For x=0 To 300
	stripe(x)=CreateImage(1,200)
	CopyRect x,0,1,200,0,0,ImageBuffer(flag),ImageBuffer(stripe(x))
Next

FreeImage flag

FlushKeys()
	
yOffset#=0
t#=0
tim=CreateTimer(60)
SetBuffer BackBuffer()
ClsColor 0,0,120

While Not KeyHit(1)

	WaitTimer tim
	Cls
	
	For i=0 To 300
		DrawImage stripe(i),170+i,110+yOffset#
		yOffset#=Sin(t#)*7
		yOffset#=yOffset#+Sin(yOffset#)
		t#=t#+3.57
		If t#>1075 Then t#=0
	Next
		
	Flip
Wend
	
For strimg=0 To 300
	FreeImage stripe(strimg)
Next

End
