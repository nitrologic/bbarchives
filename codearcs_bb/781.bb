; ID: 781
; Author: Klaas
; Date: 2003-08-26 14:42:16
; Title: Interpolation
; Description: Linear, Cosine, Cubic and Hermite Interpolation

; Set the screensize
Graphics 640,480,32,2 
SetBuffer BackBuffer() 

; Dimension the pointarray and set startvalues
Dim DataPoint(6,1) 
DataPoint(0,0) = 50 : DataPoint(0,1) = 090 
DataPoint(1,0) = 150 : DataPoint(1,1) = 160 
DataPoint(2,0) = 250 : DataPoint(2,1) = 300 
DataPoint(3,0) = 350 : DataPoint(3,1) = 210 
DataPoint(4,0) = 450 : DataPoint(4,1) = 100 
DataPoint(5,0) = 550 : DataPoint(5,1) = 300 

; Set the clearcolor to red, and select point 0 
ClsColor 0,0,0 : Global Selected = 0 
Global linear_draw,cosin_draw,cubic_draw,hermite_draw,tension#,bias#
; Mainloop
While Not KeyDown(1) 
   Cls ; Clear screen

   ; Userinput (Use [Q] and [W] to select a point,
   ; and Arrowkeys to position the selected point)
   If KeyHit(16) And Selected > 0 Then Selected = Selected - 1
   If KeyHit(17) And Selected < 5 Then Selected = Selected + 1

   If KeyHit(30) And tension > -1 Then tension = tension - 0.1
   If KeyHit(31) And tension < 1 Then tension = tension + 0.1

   If KeyHit(44) And bias > -1 Then bias = bias - 0.1
   If KeyHit(45) And bias < 1 Then bias = bias + 0.1

   If KeyDown(203) Then DataPoint(Selected,0) = DataPoint(Selected,0) - 1 
   If KeyDown(205) Then DataPoint(Selected,0) = DataPoint(Selected,0) + 1
   If KeyDown(200) Then DataPoint(Selected,1) = DataPoint(Selected,1) - 1
   If KeyDown(208) Then DataPoint(Selected,1) = DataPoint(Selected,1) + 1
	
	If KeyHit(59) Then linear_draw = Not linear_draw
	If KeyHit(60) Then cosin_draw = Not cosin_draw
	If KeyHit(61) Then cubic_draw = Not cubic_draw
	If KeyHit(62) Then hermite_draw = Not hermite_draw
   DrawLine() ; Draw the interpolated line between point 0 and 4

	Color 250,250,250
	Text 10,10,"F1 - Linear Interpolation" 
	Text 10,25,"F2 - Cosine Interpolation" 
	Text 10,40,"F3 - Cubic Interpolation" 
	Text 10,55,"F4 - Hermite Interpolation" 

	Text 300,10,"Tension(+A,-S) - "+tension 
	Text 300,25,"Bias(+Z,-X) - "+Bias
	
	Text 10,460,"use W,Q and Cursor to steer the points"
   Flip ; Flip backbuffer to frontbuffer 
Wend 

End ; End of programm 

Function DrawLine()
   ; Startpoint for the first Line
   X = DataPoint(0,0) : Y = DataPoint(0,1) : 
    
   ; Draw the interpolated line between point 1 and 4
	If cosin_draw
		f = False
		Color 255,50,50
		For I = 1 To 5
			MX = (DataPoint(I - 1,0) - DataPoint(I,0)) * (-1) 
			For MU# = 0 To 1.0 Step 0.08 
				OX = X : OY = Y
				Y = Cosine_Interpolate(DataPoint(I - 1,1),DataPoint(I,1),MU#)  
				X = Cosine_Interpolate(DataPoint(I - 1,0),DataPoint(I,0),MU#)  
				If Not f
					ox = x
					oy = y
					f = True
				EndIf
				Line OX,OY,X,Y
			Next
		Next 
	EndIf
	If linear_draw
		f = False
		Color 50,255,50
		For I = 1 To 5
			MX = (DataPoint(I - 1,0) - DataPoint(I,0)) * (-1) 
			For MU# = 0 To 1.0 Step 0.08 
				OX = X : OY = Y
				Y = Linear_Interpolate(DataPoint(I - 1,1),DataPoint(I,1),MU#)  
				X = Linear_Interpolate(DataPoint(I - 1,0),DataPoint(I,0),MU#)  
				If Not f
					ox = x
					oy = y
					f = True
				EndIf
				Line OX,OY,X,Y
			Next
		Next 
	EndIf
	If cubic_draw
		f = False
		Color 50,50,255
		For I = 2 To 4
			MX = (DataPoint(I - 1,0) - DataPoint(I,0)) * (-1) 
			For MU# = 0 To 1.0 Step 0.08 
				OX = X : OY = Y
				Y = Cubic_Interpolate(DataPoint(I - 2,1),DataPoint(I-1,1),DataPoint(I,1),DataPoint(I+1,1),MU#)  
				X = Cubic_Interpolate(DataPoint(I - 2,0),DataPoint(I-1,0),DataPoint(I,0),DataPoint(I+1,0),MU#)  
				If Not f
					ox = x
					oy = y
					f = True
				EndIf
				Line OX,OY,X,Y
			Next
		Next 
	EndIf
	If hermite_draw
		f = False
		Color 255,50,255
		For I = 2 To 4
			MX = (DataPoint(I - 1,0) - DataPoint(I,0)) * (-1) 
			For MU# = 0 To 1.0 Step 0.08 
				OX = X : OY = Y
				Y = Hermite_Interpolate(DataPoint(I - 2,1),DataPoint(I-1,1),DataPoint(I,1),DataPoint(I+1,1),MU#,tension,bias)  
				X = Hermite_Interpolate(DataPoint(I - 2,0),DataPoint(I-1,0),DataPoint(I,0),DataPoint(I+1,0),MU#,tension,bias)  
				If Not f
					ox = x
					oy = y
					f = True
				EndIf
				Line OX,OY,X,Y
			Next
		Next 
	EndIf
   ; Dra the anchor points
   Color 255,0,0
   For I = 0 To 5
      If Selected = I Then
         Color 0,0,255
      Else
         Color 255,0,0
      EndIf
      Oval DataPoint(I,0) - 2,DataPoint(I,1) - 2,5,5,1
   Next 
End Function 

Function Cubic_Interpolate(v0#, v1#, v2#, v3#, x#)
	P# = (v3-v2) - (v0-v1)
	Q# = (v0-v1) - P
	R# = v2 - v0
	S# = v1
	Return P * x^3 + Q * x^2 + R * x + S
End Function

;Function For cosineinterpolated Line
Function Cosine_Interpolate(Y1#,Y2#,MU#) 
	Local   MU2#
	MU2 = (1.0 - Cos(MU * 180))/2.0;
	Return (Y1 * (1.0 - MU2) + Y2 * MU2) 
End Function

;Function For normal Line
Function Linear_Interpolate(Y1#,Y2#,MU#) 
	Return Y1 * (1 - MU) + Y2 * MU 
End Function


;Tension: 1 is high, 0 normal, -1 is low
;Bias: 0 is even,
;positive is towards First segment,
;negative towards the other

Function Hermite_Interpolate(y0#,y1#,y2#,y3#,mu#,tension#,bias#)
	Local m0#,m1#,mu2#,mu3#
	Local a0#,a1#,a2#,a3#

	mu2 = mu * mu
	mu3 = mu2 * mu
	m0  = (y1-y0)*(1+bias)*(1-tension)/2
	m0  = m0 + (y2-y1)*(1-bias)*(1-tension)/2
	m1  = (y2-y1)*(1+bias)*(1-tension)/2
	m1 = m1 + (y3-y2)*(1-bias)*(1-tension)/2
	a0 =  2*mu3 - 3*mu2 + 1
	a1 =    mu3 - 2*mu2 + mu
	a2 =    mu3 -   mu2
	a3 = -2*mu3 + 3*mu2

	Return(a0*y1+a1*m0+a2*m1+a3*y2)
End Function
