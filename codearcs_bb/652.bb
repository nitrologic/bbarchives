; ID: 652
; Author: Vertex
; Date: 2003-04-14 13:50:07
; Title: Cosineinterpolytion
; Description: Interpolated line with cosinealgorithm

; Set the screensize
Graphics 640,480,32,2 
SetBuffer BackBuffer() 

; Dimension the pointarray and set startvalues
Dim DataPoint(4,1) 
DataPoint(0,0) = 100 : DataPoint(0,1) = 090 
DataPoint(1,0) = 200 : DataPoint(1,1) = 160 
DataPoint(2,0) = 300 : DataPoint(2,1) = 300 
DataPoint(3,0) = 400 : DataPoint(3,1) = 210 
DataPoint(4,0) = 500 : DataPoint(4,1) = 100 

; Set the clearcolor to red, and select point 0 
ClsColor 255,255,255 : Global Selected = 0 

; Mainloop
While Not KeyDown(1) 
   Cls ; Clear screen

   ; Userinput (Use [Q] and [W] to select a point,
   ; and Arrowkeys to position the selected point)
   If KeyHit(16) And Selected > 0 Then Selected = Selected - 1
   If KeyHit(17) And Selected < 4 Then Selected = Selected + 1
   If KeyDown(203) Then DataPoint(Selected,0) = DataPoint(Selected,0) - 1 
   If KeyDown(205) Then DataPoint(Selected,0) = DataPoint(Selected,0) + 1
   If KeyDown(200) Then DataPoint(Selected,1) = DataPoint(Selected,1) - 1
   If KeyDown(208) Then DataPoint(Selected,1) = DataPoint(Selected,1) + 1

   DrawLine() ; Draw the interpolated line between point 0 and 4

   Flip ; Flip backbuffer to frontbuffer 
Wend 

End ; End of programm 

Function DrawLine()
   ; Startpoint for the first Line
   X = DataPoint(0,0) : Y = DataPoint(0,1) : Color 0,0,0

   ; Draw the interpolyted Line between point 0 and 1
   MX = (DataPoint(0,0) - DataPoint(1,0)) * (-1)
   For MU# = 0 To 1.0 Step 0.08 
      OX = X : OY = Y
      Y = CosineInterpolate(DataPoint(0,1),DataPoint(1,1),MU#)
      X = MX * MU# + DataPoint(0,0)
      Line OX,OY,X,Y
   Next 
    
   ; Draw the interpolated line between point 1 and 4 
   For I = 2 To 4
      MX = (DataPoint(I - 1,0) - DataPoint(I,0)) * (-1) 
      For MU# = 0 To 1.0 Step 0.08 
         OX = X : OY = Y
         Y = CosineInterpolate(DataPoint(I - 1,1),DataPoint(I,1),MU#)  
         X = MX * MU# + DataPoint(I - 1,0) 
         Line OX,OY,X,Y
      Next 
   Next 

   ; Dra the anchor points
   Color 255,0,0
   For I = 0 To 4
      If Selected = I Then
         Color 0,0,255
      Else
         Color 255,0,0
      EndIf
      Oval DataPoint(I,0) - 2,DataPoint(I,1) - 2,5,5,1
   Next 
End Function 

; Function for cosineinterpolated line
Function CosineInterpolate(Y1#,Y2#,MU#) 
   Local MU2# 
   MU2# = (1.0 - Cos(MU# * 180.0))/2.0 
   Return (Y1# * (1.0 - MU2#) + Y2# * MU2#) 
End Function 

; Function for normal line
Function LinearInterpolate(Y1#,Y2#,MU#) 
   Return Y1# * (1 - MU#) + Y2# * MU# 
End Function
