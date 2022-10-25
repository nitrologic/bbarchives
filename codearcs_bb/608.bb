; ID: 608
; Author: Giano
; Date: 2003-03-03 14:10:10
; Title: SetGfx( )
; Description: New set graphic resolution + gui buttons

Global info1$="GUI Demo for Blitz Community"


SetGfx()

;***********************************************************

Function SetGfx()
	
	If info1$<>""
		AppTitle info1$,"Exit "+info1$+" ?"
	EndIf
	
	FlushKeys()
	
	mode_cnt=CountGfxModes3D()
	If Not mode_cnt RuntimeError "Can't find any 3D graphics modes"
	
	mode=0
	If Not Windowed3D() 
		mode=1
	Else
		For t=1 To mode_cnt
			If GfxModeWidth(t)=1024 And GfxModeHeight(t)=768 And GfxModeDepth(t)=32 Then mode=t
		Next
	End If
	
	Graphics 320,240,16,2
	SetBuffer BackBuffer()
	font = LoadFont("",17)
	SetFont font
	;PakInit("Resources.Pak", "IWRULEZ", gTempDir$, $MYCODE)
	;image=LoadImage( pak("back.jpg") )
	;ResizeImage image,GraphicsWidth(), GraphicsHeight()
	;DLPak() 
	tx=320+160
	nx=-160
	ty=-32
	
	url$=" www.intensiveworks.tk "
	url_x=6;300-StringWidth( url$ )
	url_y=210

;******** MY BUTTONS ARE THERE *******
	button("<<",60,ty+FontHeight()*5,30,24)
	button(">>",230,ty+FontHeight()*5,30,24)
	button("OK",272,202,40,32)
	button(url$,url_x,url_y,StringWidth( url$ ),24)
;******** MY BUTTONS ARE THERE *******
	
	Repeat
 		ClsColor 0,0,64
		Cls
		
;******** UPDATE BUTTONS THERE *******		
		;DrawBlock image,0,0
		updateSimpleButtons()
;******** GET THE BUTTON RETURNED STRING (the same of the text) *******		
		b$=getSimpleButton(MouseX(), MouseY())					
		
		Color 0,255,0
;		Text tx,ty+FontHeight()*0,info1$,True
;		Text nx,ty+FontHeight()*1,info2$,True
;		Text tx,ty+FontHeight()*2,info3$,True
;		Text nx,ty+FontHeight()*3,info4$,True

		Text 0,0,"Select screen resolution..."
	
		Color 255,255,255

		If mode=0
			Text tx,ty+FontHeight()*5,"Windowed",True
		Else
			Text tx,ty+FontHeight()*5,GfxModeWidth( mode )+","+GfxModeHeight( mode )+","+GfxModeDepth( mode ),True
		EndIf
		
		Color 255,255,0
		Text nx,ty+FontHeight()*7,"[Return] to begin",True
		Text tx,ty+FontHeight()*8,"[Arrows] change mode",True
		Text nx,ty+FontHeight()*9,"[Escape] to exit",True
		
;		Color 0,255,255
;		Text url_x,url_y,url$
		
		
	
		If KeyHit( 1 ) End
		
;***********************************************************
;******** IF BUTTON RETURNS "OK"  *******
		If KeyHit( 28 ) Or b$="OK"	;******** EXIT  *********
			Cls:Flip
			Cls:Flip
			FreeFont font
			:FreeImage image
			EndGraphics
			If mode
				Graphics3D GfxModeWidth(mode),GfxModeHeight(mode),GfxModeDepth(mode),1
			Else
				Graphics3D 640,480,0,2
			EndIf
			SetBuffer BackBuffer()
			Return
		EndIf
;***********************************************************

		If MouseDown(2) Then 
			mode=0
			If (Not Windowed3D())
				mode=mode_cnt
			EndIf
			FlushMouse()
			Goto NEXT_CTRL
		End If
;******** IF BUTTON RETURNS "<<"  *******
		If KeyHit( 203 ) Or b$="<<"
			mode=mode-1
			If mode<0 Or (mode=0 And (Not Windowed3D()))
				mode=mode_cnt
			EndIf
;******** IF BUTTON RETURNS ">>"  *******			
		Else If KeyHit( 205 ) Or  b$=">>"
			mode=mode+1
			If mode>mode_cnt
				mode=0
				If Not Windowed3D() mode=1
			EndIf
		EndIf
;******** IF BUTTON RETURNS the url string  *******		
		If b$=url$ Then
			ExecFile("http:\\www.intensiveworks.tk")
			End
		End If
.NEXT_CTRL		
		If tx>160 tx=tx-32
		If nx<160 nx=nx+32
		
		Flip
		
	Forever
	
End Function

;***********************************************************

Type SimpleButton
	Field x1,y1,x2,y2
	Field txt$
	Field pressed
End Type

;***********************************************************
;*** Add a button and then when is pressed the title is
;*** returned
;***********************************************************

Function button.SimpleButton(a$,x,y,w,h,HILITE=False)
	b.SimpleButton= New SimpleButton
	b\x1=x
	b\y1=y
	b\x2=w
	b\y2=h
	b\txt$=a$
	Return b
End Function

;***********************************************************
;*** Update loop for hilites needed to show the buttons
;***********************************************************
Function updateSimpleButtons()

	For b.SimpleButton= each SimpleButton
		If b\pressed Then	 Color 160,160,192 Else Color 128,128,160
		x=b\x1
		y=b\y1
		w=b\x2
		h=b\y2
		a$=b\txt$
		Rect x,y,w,h,True
		If b\pressed=False Then	 Color 192,192,240 Else Color 240,240,255  
		Line x,y,x+w-1,y
		Line x,y,x,y+h-1
		If b\pressed=False  Then Color  80,80,128 Else Color 92,92,148
		Line x+1,y+h,x+w,y+h
		Line x+w,y+1,x+w,y+h
		Color 0,0,0 
		Text x+(w/2)+1,y+(h/2)+1,a$,True,True
		If b\pressed Then Color 255,255,0 Else Color 255,255,255
		Text x+(w/2),y+(h/2),a$,True,True
		yy=y+(h/2) + StringHeight(a$)/2
		If b\pressed Then Line x+5,yy,x+w-5,yy
		b\x1=x
		b\y1=y
		b\x2=w
		b\y2=h
		b\txt$=a$
	Next
End Function

;***********************************************************
;*** retrieve the button title pressed
;***********************************************************
Function getSimpleButton$(winmx,winmy)
	res$=""
	For b.SimpleButton=Each SimpleButton
		b\pressed=0
		If RectsOverlap(winmx,winmy,1,1,b\x1,b\y1,b\x2,b\y2) Then
			b\pressed=True 
			If MouseHit(1) Then	
				b\pressed=0
				res$=b\txt$
			End If
		End If
	Next
	FlushMouse
	Return res$
End Function
;***********************************************************
