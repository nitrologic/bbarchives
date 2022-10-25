; ID: 292
; Author: wedoe
; Date: 2002-04-15 05:25:10
; Title: Oily water waves
; Description: Waves like the ones inside a bottle with water and oil

Graphics 640,480,16,1
Global count1,count2
; set up the background image
Global background=CreateImage (640,480)
SetBuffer ImageBuffer(background)
Color 0,100,200
Rect 0,0,640,240,1
Color 0,0,255
Rect 0,240,640,240,1
; set up the wave-image
Global waveimage=CreateImage(10,200)
SetBuffer ImageBuffer(waveimage)
Color 0,100,200
Rect 0,0,1,100,1
Color 0,0,255
Rect 0,100,1,100,1
; make sinus-table
Dim sinustable1(2000)
Dim sinustable2(2000)
 For a=1 To 2000
  sinustable1(a)=Sin(a)*45
  sinustable2(a)=Cos(a)*15
 Next
; back to the work-buffers
SetBuffer BackBuffer()
; loop
While Not KeyDown(1)
DrawBlock background,0,0
wave
Flip
Wend
End
;-----------
Function wave()
 b=0
 For a=0 To 640
  DrawImage waveimage,a,140+sinustable1(a+count1)+sinustable2(b+count2)
  b=b+2
 Next
count1=(count1+3) Mod 360
count2=(count2+1) Mod 360
End Function
