; ID: 283
; Author: Klapster
; Date: 2002-03-30 14:33:33
; Title: Pointing one image towards another using ATan
; Description: This code demonstrates how ATan is used to determine the frame of a rotating image

;Example Code and Function to find the angle from a point to another point
;+ Make an image point towards the second point
;By Tom Klapiscak -- 2002
;Feel free to use this code, wherever, whenever, and however...

Graphics 800,600,16,1
Px=400
Py=300
IMG=Create_Line()	
SetBuffer BackBuffer()

While KeyHit(1)=0
	Mx=MouseX()
	My=MouseY()
	Color 0,255,0
	Rect Mx-1,My-1,3,3,1	
	Color 255,0,0
	Rect Px-1,Py-1,3,3,1	
	I=Get_Iteration(Mx,My,Px,Py,72)
	If I=>0 And I<72 Then It=I	
	DrawImage IMG,Px,Py,It
	Flip
	Cls
Wend
End


;Iterations is just how many images you have per
;360 degrees(ie how smooth the rotation is)
Function Get_Iteration(X1,Y1,X2,Y2,Iterations)
	Steps=(360/Iterations)
	Dx#=X2-X1
	Dy#=Y2-Y1	
	Theta#=ATan#(Dy/Dx)
	If Dx<0 And Dy=>0
		Theta#=Theta#+180
	EndIf
	If Dx<0 And Dy<0
		Theta#=Theta#+180
	EndIf		
	If Dx=>0 And Dy<0
		Theta#=Theta#+360
	EndIf
	Return Int(Theta/Steps)
End Function

Function Create_Line()
	IMG=CreateImage(200,200,73)
	HandleImage IMG,100,100	
	For Count=0 To 360 Step 5
		SetBuffer ImageBuffer(IMG,(360-Count)/5)
		Line 100,100,Sin(Count-90)*100+100,Cos(Count-90)*100+100
	Next		
	Return IMG
End Function
