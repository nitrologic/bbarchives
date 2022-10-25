; ID: 1350
; Author: Helios
; Date: 2005-04-14 09:55:34
; Title: Xp/Normal Group Box
; Description: Group Box, with both the XP and normal style!

;--------------------------------------------------------------------
;		THe type!!
;		(C) 2500 TIM lEOANRD
; -------------------------------------------------------------------
Type groupbox
	Field panel
	Field parent
	Field name$
	Field style
End Type


;--------------------------------------------------------------------
;		The Update Code
;		(C) 2500 TIM lEOANRD
;		NOTE: This is used when resizeing and freeing windows
;		      asccociated with it!
; -------------------------------------------------------------------
Function updategadgets()
id=PeekEvent()
ev=EventSource()

;Update Group Boxes
For g.groupbox=Each groupbox
	If id=$802
		resizeGroupbox(Handle(g.groupbox))
	EndIf
	If id=$803 And EventSource()=g\parent Then FreeGroupBox(g\panel)
Next

End Function


;--------------------------------------------------------------------
;		GROUP BOX CODE
;		(C) 2500 TIM lEOANRD
;		NOTE: The buffer parameter is used to set the 
;             current buffer back To the one you were using!
; -------------------------------------------------------------------
Function CreateGroupBox(name$, x, y, w, h, p , style=0, buffer=0) 
	g.groupbox=New groupbox
	
	;Create the panel to make the gadget out of
		g\panel=CreatePanel(x,y,w,h,p)
		g\parent=p
		g\name=name
		g\style=style
	
	;Create the image
	image=CreateImage(w,h) : SetBuffer ImageBuffer(image)
	
	;Draw gadgets with system colors
	ClsColor GetSysColorR(15),GetSysColorG(15),GetSysColorB(15) : Cls 
	
	If style=0 
	Color GetSysColorR(11),GetSysColorG(11),GetSysColorB(11) : rRect 0,4,w-1,h-5,6
	;Not used anymore, not needed just slows down...
	;Color GetSysColorR(5),GetSysColorG(5),GetSysColorB(5) : rRect 1,5,w-3,h-7,5
	EndIf
	
	If style=1
	Color GetSysColorR(16),GetSysColorG(16),GetSysColorB(16) : Rect 0,4,w,h-5,0
	EndIf
	
	font=LoadFont("MS Sans Serif",8) : SetFont(font)
	Viewport 13,0,StringWidth(name)+6,FontHeight()
	Cls
	If style=0 Then Color 0,70,213 
	If style=1 Then Color GetSysColorR(8),GetSysColorG(8),GetSysColorB(8)
	Text(16,0,name)
	
	; Save image and set it as panel image
	SaveImage(image,"tempGB.bmp")
	SetPanelImage(g\panel,"tempGB.bmp")
	DeleteFile("tempGB.bmp")
	
	;Reset the buffer
	If buffer<>0 Then SetBuffer buffer
	
Return g\panel
End Function



Function FreeGroupBox(pan) 
For g.groupbox=Each groupbox
	If g\panel=pan Then FreeGadget g\panel : Delete g.groupbox
Next
End Function



Function ResizeGroupBox(han)
g.groupbox=Object.groupbox(han)
w=GadgetWidth(g\panel)
h=GadgetHeight(g\panel)

	;Create the image
	image=CreateImage(w,h) : SetBuffer ImageBuffer(image)
	
	;Draw gadgets with system colors
	ClsColor GetSysColorR(15),GetSysColorG(15),GetSysColorB(15) : Cls 
	
	If g\style=0 
	Color GetSysColorR(11),GetSysColorG(11),GetSysColorB(11) : rRect 0,4,w-1,h-5,6
	;Not used anymore, not needed just slows down...
	;Color GetSysColorR(5),GetSysColorG(5),GetSysColorB(5) : rRect 1,5,w-3,h-7,5
	EndIf
	
	If g\style=1
	Color GetSysColorR(16),GetSysColorG(16),GetSysColorB(16) : Rect 0,4,w,h-5,0
	EndIf
	
	font=LoadFont("MS Sans Serif",8) : SetFont(font)
	Viewport 13,0,StringWidth(g\name)+6,FontHeight()
	Cls
	If g\style=0 Then Color 0,70,213 
	If g\style=1 Then Color GetSysColorR(8),GetSysColorG(8),GetSysColorB(8)
	Text(16,0,g\name)
	
	; Save image and set it as panel image
	SaveImage(image,"tempGB.bmp")
	SetPanelImage(g\panel,"tempGB.bmp")
	DeleteFile("tempGB.bmp")
	
End Function


;--------------------------------------------------------------------
;		Rounded Rectangle CODE
;	  Thanks to Stephen C. Demuth for this!
; -------------------------------------------------------------------
Function RRect(x,y,width,height,radius=5)

	If radius > width/2 Then radius = width/2
	If radius > height/2 Then radius = height/2

	;---DRAW BORDERS
	Line x+radius,y,x+width-radius,y			   ;Top
	Line x+radius,y+height,x+width-radius,y+height ;Bottom	
	Line x,y+radius,x,y+height-radius			   ;Left
	Line x+width,y+radius,x+width,y+height-radius  ;Right	


	;---DRAW CORNERS

	;Upper Left
	For deg = 90 To 180
		yp = Sin(deg) * radius * -1 + y + radius
		xp = Cos(deg) * radius + x + radius		
		Plot xp,yp
	Next

	;Lower Left
	For deg = 180 To 270
		yp = Sin(deg) * radius * -1 + y + height - radius
		xp = Cos(deg) * radius + x + radius		
		Plot xp,yp
	Next

	;Upper Right
	For deg = 0 To 90
		yp = Sin(deg) * radius * -1 + y + radius
		xp = Cos(deg) * radius + x + width - radius		
		Plot xp,yp
	Next

	;Lower Right
	For deg = 270 To 359
		yp = Sin(deg) * radius * -1 + y + height - radius
		xp = Cos(deg) * radius + x + width - radius		
		Plot xp,yp
	Next

End Function


;--------------------------------------------------------------------
;		System Colour code
;	  I cant remember who made it but credit to him anyway!
; -------------------------------------------------------------------
Function GetSysColorR(SystemColor)
        Return (api_GetSysColor(SystemColor) And $000000FF) 
End Function


Function GetSysColorG(SystemColor)
	Return (api_GetSysColor(SystemColor) And $0000FF00) Shr 8
End Function


Function GetSysColorB(SystemColor)
	Return (api_GetSysColor(SystemColor) And $00FF0000) Shr 16 
End Function
