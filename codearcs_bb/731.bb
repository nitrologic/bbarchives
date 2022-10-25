; ID: 731
; Author: Shagwana
; Date: 2003-07-01 11:00:08
; Title: 2d distance calculation and approximation
; Description: Sweet ways to calculate 2d distance along a line

;
; Distance algos (2d) and some approximation methods
;
; Full credit for algo to this artical on flipcode - http://www.flipcode.com/articles/article_fastdistance.shtml
;


;Usefull functions follow ....


;True distance formular
Function Calc_Distance(XDelta%,YDelta%)
  Return Sqr((XDelta*XDelta)+(YDelta*YDelta))
  End Function 


;Approximation calculation, faster then sqr method of calculation
Function Approx_Distance(XDelta%,YDelta%)
  Local max,min,approx
  XDelta=Abs(XDelta)
  YDelta=Abs(YDelta)
  If XDelta>YDelta
    max=XDelta
    min=YDelta
    Else
    max=YDelta
    min=XDelta
    EndIf
  approx=(max*1007)+(min*441)
  If (max<(min Shl 2))
    approx=approx-(max*111)
    Else
    approx=approx-(min*441)
    EndIf
  Return ((approx+512) Shr 10)
  End Function 


;Integer shift only method of the above function, should be faster
Function FasterApprox_Distance(XDelta%,YDelta%)
  Local max,min
  If XDelta>YDelta
    max=XDelta
    min=YDelta
    Else
    max=YDelta
    min=XDelta
    EndIf
  Return (((max Shl 8)+(max Shl 3)-(max Shl 4)-(max Shl 1)+(min Shl 7)-(min Shl 5)+(min Shl 3)-(min Shl 1)) Shr 8)
  End Function 


;Example code follows ....



Const DRAW_DISTANCE_LINE=200

;Example code
Graphics 800,600,16,1
SetBuffer FrontBuffer()
Cls


;First, render the 3 distance quarters.
Text 0,0,"Plotting distance images, please wait"

;Create and plot the image
pImage1=CreateImage(400,400)
SetBuffer ImageBuffer(pImage1)
For x=0 To 399
  For y=0 To 399
    dist=Calc_Distance(x,y)
    If dist>255
      Color 255,dist-255,255
      Else
      Color dist,dist,dist
      EndIf
    If dist=DRAW_DISTANCE_LINE
      Color 0,0,255
      EndIf
    Plot x,y
    Next
  Next

;Create and plot the image
pImage2=CreateImage(400,400)
SetBuffer ImageBuffer(pImage2)
For x=0 To 399
  For y=0 To 399
    dist=Approx_Distance(x,y)
    If dist>255
      Color 255,dist-255,255
      Else
      Color dist,dist,dist
      EndIf
    If dist=DRAW_DISTANCE_LINE
      Color 0,0,255
      EndIf
    Plot x,y
    Next
  Next


;Create and plot the image
pImage3=CreateImage(400,400)
SetBuffer ImageBuffer(pImage3)
For x=0 To 399
  For y=0 To 399
    dist=FasterApprox_Distance(x,y)
    If dist>255
      Color 255,dist-255,255
      Else
      Color dist,dist,dist
      EndIf
    If dist=DRAW_DISTANCE_LINE
      Color 0,0,255
      EndIf
    Plot x,y
    Next
  Next




ShowImage=pImage1
msg$=""
Repeat


  SetBuffer BackBuffer() 
  Flip
  Cls

  Color 255,255,255
  Text 0,0,"keys: [1] show sqr()     [2] show approx      [3] show faster approx     [esc] quit"

  ;When key is pressed, swap the image being displayed...  
  If KeyDown(2)=True Then ShowImage=pImage1    ;key presses = show different image
  If KeyDown(3)=True Then ShowImage=pImage2
  If KeyDown(4)=True Then ShowImage=pImage3

  DrawBlock ShowImage,100,100

  Select ShowImage
    Case pImage1
    Text 0,24,"showing Calc_Distance()"
    Case pImage2
    Text 0,24,"showing Approx_Distance()" 
    Case pImage3
    Text 0,24,"showing FasterApprox_Distance()" 
    End Select

  Until KeyDown(1)=True
End
