; ID: 2606
; Author: Malice
; Date: 2009-11-02 15:24:56
; Title: B3D 'Zelda' Hearts
; Description: A similar concept to the THearts by Ked for Bmax written for B3D

Const Gwidth=800
Const Gheight=600

Const MaxHearts%=5
Const MaxHealth%=100
Const HeartSize=48
Const HealthDivs=MaxHealth/MaxHearts

Graphics Gwidth,Gheight

Global HeartImage%=CreateHeartImage%(HeartSize)
MidHandle HeartImage
Global Health%=MaxHealth

Function RenderHearts()
 	Local x,y,img,rx,ry,angle,count
	For count=1 To Health/MaxHealth*MaxHearts
		y=Gheight-(HeartSize)
	
		x=((count-1)*(HeartSize))+(Gwidth/80)+(HeartSize Shr True)
		
		;set the background for hearts
		Colour(8396832)
		Rect x-HeartSize Shr True,y-HeartSize Shr True,HeartSize,HeartSize,True
		
		;Shade the heart according to health remaining
		angle=(((HeartHealth(count))/HealthDivs )*360) Mod 360
		
		rx=x+(HeartSize Shr True)*(Sin(angle))
		ry=y+(HeartSize Shr True)*(Cos(angle))
		
		Colour(8392894) 
		
		Line x-1,y-1,rx,ry
		Line x+1,y-1,rx,ry
		Line x,y-1,rx,ry
		Line x+1,y,rx,ry
		Line x+1,y+1,rx,ry
		
		;overlay with a heart template
		DrawImage HeartImage,x,y
	Next
End Function 

Function HeartHealth(Heart)
	Local fFract#=(MaxHealth/HealthDivs)
	fFract=fFract-(MaxHearts-Heart)
	Return fFract*HealthDivs
End Function
	
Function Colour(Col,ClsCol=False)
	If ClsCol
		ClsColor(Col Shr 16) And 255,(Col Shr 8) And 255,Col And 255
	Else
		Color (Col Shr 16) And 255,(Col Shr 8) And 255,Col And 255
	End If
End Function

Function CreateHeartImage%(SmallSize)
	Local img=CreateImage(SmallSize,SmallSize)
	SetBuffer ImageBuffer(img)
	Colour(16777215,False)
	Colour(False,True)
	Oval SmallSize Shr 2, SmallSize  Shr 2,SmallSize Shr True, SmallSize - (SmallSize Shr 2),True
	Oval SmallSize Shr 2, SmallSize  Shr 2,SmallSize - (SmallSize Shr 2), SmallSize Shr True,True
	SetBuffer BackBuffer()
	RotateImage img,225
	MaskImage img,255,255,255
	Return img
End Function

While Not KeyDown(1)
	RenderHearts()
	Flip
Wend
