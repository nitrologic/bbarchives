; ID: 2459
; Author: Filax
; Date: 2009-04-11 17:43:28
; Title: Screen mode selector
; Description: Quick screen mode selector without graphics :)

ChoosescreenMode()

While Not KeyHit(1) 
	RenderWorld 

	Color 255,255,0
	Text 0,20,GraphicsWidth()+" / "+GraphicsHeight()+" / "+GraphicsDepth() 
	
	Flip False
Wend

ClearWorld 
End



Type tMode

	Field Width%
	Field Height%
	Field Depth%
	Field Mode%
	Field Id%
	
End Type


Function ChooseScreenMode(Title$="My App",MinWidth=1024)

	Graphics3D 400,240,0,2
	SetBuffer BackBuffer ()
	AppTitle Title$
	
	Local Count%=CountGfxModes3D()
	
	Local Total%
	Local Current%=0
	
	Local Width%
	Local Height%
	Local Depth%
	Local Mode%
	
	Local C%
	Local Y%
	Local O%
	
	Local Sx%=GraphicsWidth()
	Local Sy%=GraphicsHeight()
	Local ScrollY#=50
	
		
	Local Size%=20	
	Local Font=LoadFont("arial",Size%-2,1,0,0)
	SetFont Font
	
	For i=1 To Count%
	
		If GfxModeWidth(I)>=MinWidth
		
		Current%=Current%+1
		
		m.tMode=New tMode
		m\Width%=GfxModeWidth(I)
		m\Height%=GfxModeHeight(I)
		m\Depth%=GfxModeDepth(I)
		m\Mode%=1
		m\Id%=Current%


		Total%=Total%+1
				
		EndIf
		
	Next	
	
	Current=1
		
	Repeat
		
		Cls

		; ---
		; back
		; ---
		Viewport 0, 0, Sx%,Sy%
			
		Color 0,49,72
		Rect 0,0,Sx%,Sy%,1
		
		ScrollY#=ScrollY#+0.8 : If ScrollY#>Sy%-50 Then ScrollY#=50
		
		Color 0,53,76 : Line 0,ScrollY#+0,Sx%,ScrollY#+0
		Color 0,57,80 : Line 0,ScrollY#+1,Sx%,ScrollY#+1
		Color 0,61,84 : Line 0,ScrollY#+2,Sx%,ScrollY#+2
		Color 0,65,88 : Line 0,ScrollY#+3,Sx%,ScrollY#+3
		Color 5,76,99 : Line 0,ScrollY#+4,Sx%,ScrollY#+4
		Color 0,65,88 : Line 0,ScrollY#+5,Sx%,ScrollY#+5
		Color 0,61,84 : Line 0,ScrollY#+6,Sx%,ScrollY#+6
												
		Color 0,29,52
		Rect 0,0,Sx%,50,1
		Rect 0,Sy%-50,Sx%,50,1
		
		Color 0,69,112
		Text Sx%/2,Sy%-25,"Press <up><down> / <left><right>",True,True
				
		; ---
		; Bar
		; ---
		Color 0,128,192
		Rect 0,110,Sx%,Size%,1

		Color 20,169,192
		Rect 0,110,Sx%,Size%,0
		
		Viewport 0, 60, Sx%, Sy%-120

		; -------
		; Refresh
		; -------
		C%=0
		Y%=101
										
		For d.tMode =Each tMode

			C%=C%+Size%

			If d\Id%=Current% Then	
				Color 255,128,0
				
				If KeyHit(203) Then 
					d\Mode%=1
				EndIf
				
				If KeyHit(205) Then 
					d\Mode%=2
				EndIf
			Else
				Color 168,54,0
			EndIf

			Text 70,Y%+C%-O%,d\Width%,True,True
			Text 105,Y%+C%-O%,"/",True,True
			Text 140,Y%+C%-O%,d\Height%,True,True
						
			Text 190,Y%+C%-O%,d\Depth%,True,True
			Text 220,Y%+C%-O%,"Bits",True,True
			
			Select d\Mode%
				Case 1
					Caption$="Screen"
				Case 2
					Caption$="Windowed"
			End Select


			Text 300,Y%+C%-O%,Caption$,True,True

		Next
		
		

		If KeyHit(200) Then 
			If Current%>1 Then
				O%=O%-Size%
				
				Current%=Current%-1
			EndIf
		EndIf

		If KeyHit(208) Then 
			If Current%<Total% Then
				O%=O%+Size%
				
				Current%=Current%+1
			EndIf
		EndIf


			
		If KeyHit(28)
			For e.tMode =Each tMode
				If e\Id=Current% Then
				
					Graphics3D e\Width%,e\Height%,e\Depth%,e\Mode%
					Return True
			
				EndIf
			Next
		EndIf		
			
		Flip
	Forever

End Function
