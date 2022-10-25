; ID: 2042
; Author: _33
; Date: 2007-06-23 18:02:13
; Title: Testing FPS
; Description: How to test the frames per seconds from Blitz3D

Graphics3D 800,600,32,1
total% = 0
tests% = 200
executed% = 0
errors% = 0
x = 0
y = 0
fps_cible% = 60
For i = 1 To tests
   fps = get_fps()
   If fps = fps_cible Then Color 0,255,0 Else Color 255,0,0 : errors = errors + 1
   Text x, y, "FPS = " + fps
   x = x + 100 : If x > 700 Then x = 0 : y = y + 20
   total = total + fps
   executed = executed + 1
   If KeyHit(1) Then Exit
   Flip 0
Next
frames = 0
Text 0, y + 20, "tests executed : " + executed + "       AVERAGE FPS = " + total / executed + "   errors = " + errors
FlushKeys()
Flip 0
WaitKey()

Function get_fps()
   timer% = 0
   frames% = 0
   m% = MilliSecs()
   While timer < 186
      VWait() : frames = frames + 1
      timer = MilliSecs() - m
   Wend
   Return frames * 5
End Function
