; ID: 664
; Author: Red
; Date: 2003-05-01 11:59:05
; Title: Skinnable App (dragable)
; Description: use bitmap to skin your app / click to drag window

; ==============================
; Example 
; ==============================
skin=SkinApp("interface.png",$FF00FF)
Repeat
	;Drag Window... (Darklordz's addon)
	While MouseDown(1) 
		app=GetActiveWindow():ReleaseCapture%()
		SendMessage%(app,161,2,0):ReleaseCapture%()
		Exit
	Wend

	;Draw skin
	DrawBlock skin,0,0
	Flip
Until KeyHit(1)
End

; ==============================
; Function
; ==============================
Function SkinApp%(Image$,MaskColor%)
	Local app=GetActiveWindow()
	
	;size of client aera
	Local client=LoadImage(Image$)
	Graphics ImageWidth(client),ImageHeight(client),0,2	
	SetBuffer BackBuffer()

	ShowWindow app,2
	
	;remove all borders around the client aera
	Local bmp=LoadImage(Image$)
	Local x=GetSystemMetrics($07)
	Local y=GetSystemMetrics($08)+GetSystemMetrics($04)		
	Local region=CreateRectRgn(x,y,x+GraphicsWidth(),y+GraphicsHeight())	

	Local yoffset = -1-(GetSystemMetrics($4)+GetSystemMetrics($2E))
	Local xoffset = -1-GetSystemMetrics($2D)

	;fix visual problem on non-XP computer
	If Not Instr(SystemProperty("OS"),"XP",1)	
		x=x+xoffset
		y=y+yoffset
	EndIf 

	;remove all useless pixels 
	Local pixel,px,py,c
	For py=0 To ImageHeight(bmp)-1
		For px=0 To ImageWidth(bmp)-1
		
			c=ReadPixel(px,py,ImageBuffer(bmp))	And $00FFFFFF 		

			If c=MaskColor
				pixel=CreateRectRgn(x+px,y+py,x+px+1,y+py+1)
				CombineRgn region,region,pixel,3;RGN_XOR
				DeleteObject pixel
			EndIf 
			
		Next
	Next
		
	;set visible region
	SetWindowRgn(app,region,True)

	ShowWindow app,1

	Return bmp
End Function
