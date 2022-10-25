; ID: 3166
; Author: zoqfotpik
; Date: 2014-12-10 21:59:17
; Title: 2D Midpoint Displacement Mountains
; Description: Mountains made with the midpoint displacement method.  Uses recursion.

Graphics 640,480
Global arr:Int[640]
Global MAXITER:Int = 12

Cls
' 2 calls, one for leftmost->midpoint, the second from midpoint->rightmost.  Note that the midpoint at Y is somewhat higher than the 
' endpoints, that's to give it a grade upwards and downwards.  Otherwise it will be more uniform.
displacemidpoints(0,420,320,400,1)
displacemidpoints(320,400,639,420,1)


Flip
While Not KeyHit(KEY_ESCAPE)
Cls
For i = 0 To 639
DrawLine i,480,i,arr[i]
Next
Flip
Wend
	
Function displacemidpoints(x1:Int, y1:Int, x2:Int, y2:Int, iter:Int)
'Iter is the bailout variable so the recursion knows when to stop
Local midx:Int
Local midy:Int
	If iter < MAXITER
		midx = (x2 + x1) / 2
		midy = (y1+y2)/2 +Rand(10-iter)
		arr[x1]=y1
		arr[x2]=y2
' calls itself twice.  Having it call a slightly different version of the function can be somewhat interesting too.
		displacemidpoints(x1,y1,midx,midy, iter+1)
		displacemidpoints(midx,midy,x2,y2,iter+1)
	EndIf
	Return
End Function
