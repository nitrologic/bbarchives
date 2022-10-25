; ID: 2467
; Author: superStruct
; Date: 2009-04-28 16:52:26
; Title: Analytic Geometric Algorithms
; Description: Uses basic geometric rules to graph shapes

;----------------------------------------;
;--------Advanced Analytic Geometry------;
;----------------Created By--------------;
;---------------Sam Stratter-------------:
;----------------------------------------;


;Function to Draw Ellips
;FD is the distance between the 2 focal points
;SOFR is the Sum Of Focal Radii
;Major$ is the major axis allignment 

Function DrawEllipse(FD,SOFR,major$)

If major = "x"
	x1 = GraphicsWidth()/2 + FD
	x2 = GraphicsWidth()/2 - FD 
	y1 = GraphicsHeight()/2
	y2 = GraphicsHeight()/2
Else If major = "y"
	x1 = GraphicsWidth()/2
	x2 = GraphicsWidth()/2
	y1 = GraphicsHeight()/2 - FD 
	y2 = GraphicsHeight()/2 + FD
EndIf 
	
Plot x1,y1
Plot x2,y2

For y3 = 0 To GraphicsHeight() Step 1
	For x3 = 0 To GraphicsWidth() Step 1
		res1 = (x3 - x1)^2
		res2 = (y3 - y1)^2
		res3 = res2 + res1
		dist1% = Sqr(res3)
		res4 = (x3 - x2)^2
		res5 = (y3 - y2)^2
		res6 = res4 + res5
		dist2% = Sqr(res6)
		If dist1 + dist2 = SOFR + 200
			Plot x3,y3
			Flip
		EndIf   
	Next 
Next 

End Function 

;-----------------------------------------------------------------

;Function to Draw Circles
;Uses the CenterX
;Uses the CenterY
;Uses the Radius

Function DrawCircle(CenterX,CenterY,radius)

x1 = CenterX
y1 = CenterY

Plot x1,y1

For y2 = 0 To GraphicsHeight() 
	For x2 = 0 To GraphicsWidth() 
		res1 = (x2 - x1)^2
		res2 = (y2 - y1)^2
		res3 = res2 + res1
		dist = Sqr(res3)
		If dist = radius
			Plot x2,y2
			Flip
		EndIf   
	Next 
Next 

End Function 

;-------------------------------------------------------------------

;Function to Draw Parabola
;Uses the center x and y
;DC is distance from the focus to the vertex
;Major is the same as it is in the ellipse
;Curve is -1<Curve<1 to show which way it opens

Function DrawParabola(CenterX,CenterY,DC,major$,Curve)

dist1% = 0
dist2% = 0
DirectX = 0
DirectY = 0

If major = "x"
	If curve < 0
		DirectY = CenterY - 2*DC
	Else
		DirectY = CenterY + 2*DC
	EndIf
ElseIf major = "y"
	If curve < 0 
		DirectX = CenterX - 2*DC
	Else
		DirectX = CenterX + 2*DC
	EndIf
EndIf 

Plot CenterX,CenterY

If major = "x"	
	For y = 0 To GraphicsHeight()
		For x = 0 To GraphicsWidth()
			If major = "x"
				DirectX = x
			ElseIf major = "y"
				DirectY = y
			EndIf 
			res1 = (CenterX - x)^2
			res2 = (CenterY - y)^2
			res3 = res2 + res1
			dist1 = Sqr(res3)
			res4 = (x - DirectX)^2
			res5 = (y - DirectY)^2
			res6 = res4 + res5
			dist2 = Sqr(res6)
			If dist1 = dist2
				Plot x,y
				Flip 
			EndIf
		Next
	Next 
ElseIf major = "y"
	For x = 0 To GraphicsWidth()
		For y = 0 To GraphicsHeight()
			If major = "x"
				DirectX = x
			ElseIf major = "y"
				DirectY = y
			EndIf 
			res1 = (CenterX - x)^2
			res2 = (CenterY - y)^2
			res3 = res2 + res1
			dist1 = Sqr(res3)
			res4 = (x - DirectX)^2
			res5 = (y - DirectY)^2
			res6 = res4 + res5
			dist2 = Sqr(res6)
			If dist1 = dist2
				Plot x,y
				Flip 
			EndIf
		Next
	Next
EndIf  		

End Function

;---------------------------------------------------------------------------------------------------------------

;Function to Draw Hyperbola
;All variables are the same as seen in other functions

Function DrawHyperbola(FD,SOFR,major$)

If major = "x"
	x1 = GraphicsWidth()/2 + FD
	x2 = GraphicsWidth()/2 - FD 
	y1 = GraphicsHeight()/2
	y2 = GraphicsHeight()/2
Else If major = "y"
	x1 = GraphicsWidth()/2
	x2 = GraphicsWidth()/2
	y1 = GraphicsHeight()/2 - FD 
	y2 = GraphicsHeight()/2 + FD
EndIf 
	
Plot x1,y1
Plot x2,y2

If major = "x"
	For y3 = 0 To GraphicsHeight() Step 1
		For x3 = 0 To GraphicsWidth() Step 1
			res1 = (x3 - x1)^2
			res2 = (y3 - y1)^2
			res3 = res2 + res1
			dist1% = Sqr(res3)
			res4 = (x3 - x2)^2
			res5 = (y3 - y2)^2
			res6 = res4 + res5
			dist2% = Sqr(res6)
			If dist1 > dist2
				If dist1 - dist2 = SOFR
					Plot x3,y3
					Flip	
				EndIf
			EndIf
			If dist2 > dist1
				If dist2 - dist1 = SOFR
					Plot x3,y3
					Flip
				EndIf
			EndIf     
		Next 
	Next 
EndIf 

If major = "y"
	For x3 = 0 To GraphicsWidth() Step 1
		For y3 = 0 To GraphicsHeight() Step 1
			res1 = (x3 - x1)^2
			res2 = (y3 - y1)^2
			res3 = res2 + res1
			dist1% = Sqr(res3)
			res4 = (x3 - x2)^2
			res5 = (y3 - y2)^2
			res6 = res4 + res5
			dist2% = Sqr(res6)
			If dist1 > dist2
				If dist1 - dist2 = SOFR
					Plot x3,y3
					Flip	
				EndIf
			EndIf
			If dist2 > dist1
				If dist2 - dist1 = SOFR
					Plot x3,y3
					Flip
				EndIf
			EndIf     
		Next 
	Next 
EndIf

End Function
