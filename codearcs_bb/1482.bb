; ID: 1482
; Author: Space Fractal
; Date: 2005-10-07 10:42:51
; Title: CDG player
; Description: CDG Karaoke player

Global CDG_COMMAND = 9
Global CDG_MASK = 63

Global CDG_INST_MEMORY_PRESET     = 1
Global CDG_INST_BORDER_PRESET     = 2
Global CDG_INST_TILE_BLOCK        = 6
Global CDG_INST_SCROLL_PRESET     = 20
Global CDG_INST_SCROLL_COPY       = 24
Global CDG_INST_DEF_TRANSP_COL    = 28
Global CDG_INST_LOAD_COL_TBL_0_7  = 30
Global CDG_INST_LOAD_COL_TBL_8_15 = 31
Global CDG_INST_TILE_BLOCK_XOR    = 38

Global CDG_STREAM_COMMAND
Global CDG_STREAM_INTRUCTION
Global CDG_BUFFER_FRONT=0
Global CDG_BUFFER_BACK=1

Global CDG_COLOR_BORDER=0
Global CDG_COLOR_FILL=0
Global CDG_COLOR_TRANSPERANT=0

Global CDG_STREAM_FILE$      = ""
Global CDG_STREAM_FILE_ID    = 0
Global CDG_STREAM_FILE_SOUND = 0
Global CDG_STREAM_FILE_TIMER
Global CDG_STREAM_XOFFSET = 0
Global CDG_STREAM_YOFFSET = 0
Global CDG_IMAGE
Global CDG_ROTATION = 0

Dim CDG_COLOR_TABLE(17,5)
Dim CDG_STREAM_PARITYQ(2)
Dim CDG_STREAM_DATA$(17)
Dim CDG_STREAM_PARITYP(4)
Dim CDG_SCREEN(301,217,2,2)

CMD$=Lower$(CommandLine$())
If Instr(CMD$,"\rotate90") Then CDG_ROTATION=90 : CMD$=Replace$(CMD$,"\rotate90","")
If Instr(CMD$,"\rotate180") Then CDG_ROTATION=180 : CMD$=Replace$(CMD$,"\rotate180","")
If Instr(CMD$,"\rotate270") Then CDG_ROTATION=270 : CMD$=Replace$(CMD$,"\rotate270","")
FILE$=Trim$(CMD$)
If FileType(FILE$)=-0 Then FILE$=RequestFile("Open a CDG file","CDG",False)

CDG_PLAY(FILE$,CDG_ROTATION)

Function CDG_PLAY(FILE$, CDG_ROTATION)
	;mode=2
	CDG_BUFFER_FRONT=0
	CDG_BUFFER_BACK=1
	
	; SETUP THE GRAPHICS CARD. THIS APPLICATION SUPPORT ROTATION
	If CDG_ROTATION=0 Or CDG_ROTATION=180
		If GfxModeExists(320,240,16) Then Graphics 320,240,16,0 Else Graphics 321,240,16,0
		CDG_IMAGE=CreateImage(301,217,1,1)
		MaskImage CDG_IMAGE,5,5,5
	EndIf

	If CDG_ROTATION=90 Or CDG_ROTATION=270
		If GfxModeExists(400,300,16) Then Graphics 400,300,16,0 Else Graphics 640,480,16,0
		CDG_IMAGE=CreateImage(217,301,1,1)
	EndIf
	

	fntVerdana=LoadFont("Verdana",14,False,False,False)
	SetFont fntVerdana

	A=StringWidth("Karaoke")
	COPYRIGHT=CreateImage(A,20,1,1)
	SetBuffer ImageBuffer(Copyright)
	SetFont fntVerdana
	Color 90,90,90
	Text 0,0,"Karaoke"

	A=StringWidth("Arcade Music Box")
	COPYRIGHT2=CreateImage(A,20,1,1)
	SetBuffer ImageBuffer(Copyright2)
	SetFont fntVerdana
	Color 90,90,90
	Text 0,0,"Arcade Music Box"

	If CDG_ROTATION=90
		MidHandle Copyright : MidHandle Copyright2
		RotateImage Copyright,90 :	RotateImage Copyright2,90
		HandleImage Copyright,0,0 : HandleImage Copyright2,0,0
	Else If CDG_ROTATION=180
		MidHandle Copyright : MidHandle Copyright2
		RotateImage Copyright,180 :	RotateImage Copyright2,180
		HandleImage Copyright,0,0 : HandleImage Copyright2,0,0
	Else If CDG_ROTATION=270
		MidHandle Copyright : MidHandle Copyright2
		RotateImage Copyright,270 :	RotateImage Copyright2,270
		HandleImage Copyright,0,0 : HandleImage Copyright2,0,0
	EndIf

	; READ MUSIC FILE AND OPEN CDG FILE
	CDG_STREAM_FILE_ID=ReadFile(FILE$)
	If CDG_STREAM_FILE_ID=0 Then Return

	FILE2$=Left$(FILE$,Len(FILE$)-4)
	CDG_STREAM_FILE_SOUND=LoadSound(FILE2$+".mp3")
	If CDG_STREAM_FILE_SOUND=0 Then LoadSound(FILE2$+".ogg")
	If CDG_STREAM_FILE_SOUND=0 Then LoadSound(FILE2$+".wav")
	If CDG_STREAM_FILE_SOUND=0 Then Return
	ok=PlaySound(CDG_STREAM_FILE_SOUND)
	
	; ********************
	; **** CDG PLAYER ****
	; ********************
	CDG_STREAM_FILE_TIMER = MilliSecs()+50
	Repeat
 		; READ A 24 BYTE PACKET FROM THE FILE (PARITYQ and PARITYP seen not been used in anyware, but keept them here)
		For ticks=1 To 15
			CDG_STREAM_COMMAND=ReadByte(CDG_STREAM_FILE_ID)  And CDG_MASK
			CDG_STREAM_INTRUCTION=ReadByte(CDG_STREAM_FILE_ID) And CDG_MASK
			CDG_STREAM_PARITYQ(1)=ReadByte(CDG_STREAM_FILE_ID) And CDG_MASK
			CDG_STREAM_PARITYQ(2)=ReadByte(CDG_STREAM_FILE_ID) And CDG_MASK
			For i=0 To 15 : Byte=ReadByte(CDG_STREAM_FILE_ID) : CDG_STREAM_DATA$(i)=Right$(Bin$(Byte),6) : Next
			For i=1 To 4 : CDG_STREAM_PARITYP(i)=ReadByte(CDG_STREAM_FILE_ID) And CDG_MASK : Next
			If CDG_STREAM_COMMAND=9
				ok=0
				If CDG_STREAM_INTRUCTION=CDG_INST_MEMORY_PRESET Then CDG_MEMORY_PRESET() : ok=1
				If CDG_STREAM_INTRUCTION=CDG_INST_BORDER_PRESET  Then CDG_BORDER_PRESET() : ok=1
				If CDG_STREAM_INTRUCTION=CDG_INST_LOAD_COL_TBL_0_7 Then CDG_LOAD_COL_TBL(0) : ok=1
				If CDG_STREAM_INTRUCTION=CDG_INST_LOAD_COL_TBL_8_15 Then CDG_LOAD_COL_TBL(8) : ok=1
				If CDG_STREAM_INTRUCTION=CDG_INST_TILE_BLOCK Then CDG_TILE_BLOCK(0) : ok=1
				If CDG_STREAM_INTRUCTION=CDG_INST_TILE_BLOCK_XOR Then CDG_TILE_BLOCK(1) : ok=1
				If CDG_STREAM_INTRUCTION=CDG_INST_SCROLL_PRESET Then CDG_SCROLL(0) : ok=1
				If CDG_STREAM_INTRUCTION=CDG_INST_SCROLL_COPY Then CDG_SCROLL(1) : ok=1
				;If CDG_STREAM_INTRUCTION>0 And ok=0 Then Print "unsupported instruction: "+CDG_STREAM_INTRUCTION
			EndIf
		Next

		SetBuffer ImageBuffer(CDG_IMAGE)
		If KeyDown(1)=True Then End
		
		; DISPLAY THE BUFFER TO THE SCREEN
		If (CDG_STREAM_FILE_TIMER >= MilliSecs())
			LockBuffer ImageBuffer(CDG_IMAGE)
			For x=7 To 300
				For y=13 To 216
					xx=x+CDG_STREAM_XOFFSET : If xx>300 Then xx=xx-300
					yy=y+CDG_STREAM_YOFFSET : If yy>216 Then yy=yy-216
					If CDG_ROTATION=0 Then WritePixelFast x-6,y-12,CDG_SCREEN(xx,yy,0,CDG_BUFFER_FRONT)
					If CDG_ROTATION=90 Then WritePixelFast 217-y,x-6,CDG_SCREEN(xx,yy,0,CDG_BUFFER_FRONT)
					If CDG_ROTATION=180 Then WritePixelFast 301-x,217-y,CDG_SCREEN(xx,yy,0,CDG_BUFFER_FRONT)
					If CDG_ROTATION=270 Then WritePixelFast y-12,301-x,CDG_SCREEN(xx,yy,0,CDG_BUFFER_FRONT)
				Next
			Next

			UnlockBuffer ImageBuffer(CDG_IMAGE)

			SetBuffer BackBuffer()
			If CDG_ROTATION=-1
		
			Else If CDG_ROTATION=0
				Color 255,255,255
				x=14 : y=18
				DrawImage CDG_IMAGE,x,y,0
				Rect x,y,294+2,204+2,0
				DrawImage COPYRIGHT,160-ImageWidth(COPYRIGHT)/2,1 : DrawImage COPYRIGHT2, 160-ImageWidth(COPYRIGHT2)/2,224
				Flip
			Else If CDG_ROTATION=90
				Color 255,255,255
				x=2 : y=100 : If GraphicsWidth()=640 Then x=96 : cx=244
				Rect y-1,x-1,204+2,294+2,1
				DrawImage CDG_IMAGE,y-1,x-1,0
				DrawImage COPYRIGHT2,30,GraphicsHeight()/2-ImageHeight(COPYRIGHT2)/2
				DrawImage COPYRIGHT, GraphicsWidth()-50-cx, GraphicsHeight()/2-ImageHeight(COPYRIGHT)/2	
				Flip
			Else If CDG_ROTATION=270
				Color 255,255,255
				x=2 : y=100 : If GraphicsWidth()=640 Then x=96 : cy=240 : y=y+cy : cx=244
				Rect y-1,x-1,204+2,294+2,1
				DrawImage CDG_IMAGE,y-1,x-1,0
				DrawImage COPYRIGHT,30+cy,GraphicsHeight()/2-ImageHeight(COPYRIGHT)/2
				DrawImage COPYRIGHT2, GraphicsWidth()-50-cx+cy, GraphicsHeight()/2-ImageHeight(COPYRIGHT2)/2
				Flip
			Else CDG_ROTATION=180
				Color 255,255,255
				x=14 : y=18
				Rect x-1,y-1,294+2,204+2,1
				DrawImage CDG_IMAGE,x-1,y-1,0
				DrawImage COPYRIGHT2,160-ImageWidth(COPYRIGHT2)/2,-5 : DrawImage COPYRIGHT, 160-ImageWidth(COPYRIGHT)/2,219		
				Flip
			EndIf
		EndIf
		
		; WAIT TIMER
		Repeat : Delay 1 : Until CDG_STREAM_FILE_TIMER < MilliSecs()
		CDG_STREAM_FILE_TIMER=CDG_STREAM_FILE_TIMER+50
	Until Eof(CDG_STREAM_FILE_ID) Or ChannelPlaying(CDG_STREAM_FILE_SOUND)=1
	FreeSound CDG_STREAM_FILE_SOUND
	FreeImage CDG_IMAGE
End Function

; **** DRAW A 6x12 TILE (USING XOR OR NORMAL OPERATION)
Function CDG_TILE_BLOCK(BLOCK_XOR)
	colorA=ReadBit(CDG_STREAM_DATA$(0),3,4)
	colorB=ReadBit(CDG_STREAM_DATA$(1),3,4)
	row=ReadBit(CDG_STREAM_DATA$(2),2,5)*12
	col=ReadBit(CDG_STREAM_DATA$(3),1,6)*6
	For y=0 To 11
		For x=0 To 5
			yy=y+row : xx=x+col
			If xx<294 And yy<205
				If Mid(CDG_STREAM_DATA$(y+4),x+1,1)="1" 
					display=colorB
				Else 
					display=colorA
				EndIf
				If BLOCK_XOR=1
					display=display Xor CDG_SCREEN(xx,yy,1,CDG_BUFFER_FRONT)   ;THIS COMMAND IS HEAVY USED
				EndIf
				CDG_SCREEN(xx,yy,1,CDG_BUFFER_FRONT)=display
				CDG_SCREEN(xx,yy,0,CDG_BUFFER_FRONT)=CDG_COLOR_TABLE(display,4)
			EndIf
		Next
	Next
End Function

; **** FILL THE SCREEN WITH THAT COLOR
Function CDG_MEMORY_PRESET()
	CDG_COLOR_FILL=ReadBit(CDG_STREAM_DATA$(0),3,4)
	CDG_COLOR_REPEAT=ReadBit(CDG_STREAM_DATA$(1),3,4) ; THIS MAY BE A BOUNCS IF COMMANDS. IT ONLY NEED THIS ONCE.
	If CDG_COLOR_REPEAT=0
		For x=7 To 300
			For y=13 To 216
				CDG_SCREEN(x,y,0,CDG_BUFFER_FRONT)=CDG_COLOR_TABLE(CDG_COLOR_FILL,4)
				CDG_SCREEN(x,y,1,CDG_BUFFER_FRONT)=CDG_COLOR_FILL
			Next
		Next
	EndIf
End Function

; **** FILL THE BORDER WITH THAT COLOR (ONLY USED FOR SCROLLING THINGS)
Function CDG_BORDER_PRESET(COLOUR=-1)
	If COLOUR=-1 Then CDG_COLOR_BORDER=ReadBit(CDG_STREAM_DATA$(0),3,4) Else CDG_COLOR_BORDER=COLOUR 
	For x=1 To 6
		For y=1 To 216
			CDG_SCREEN(x,y,0,CDG_BUFFER_FRONT)=CDG_COLOR_TABLE(CDG_COLOR_FILL,4)
			CDG_SCREEN(x,y,1,CDG_BUFFER_FRONT)=CDG_COLOR_BORDER
		Next
	Next 

	For x=1 To 300
		For y=1 To 12
			CDG_SCREEN(x,y,0,CDG_BUFFER_FRONT)=CDG_COLOR_TABLE(CDG_COLOR_FILL,4)
			CDG_SCREEN(x,y,1,CDG_BUFFER_FRONT)=CDG_COLOR_BORDER
		Next
	Next
End Function

; **** READ COLOR THE LOWER 8 OR HIGHER 8 COLOR TABLES
Function CDG_LOAD_COL_TBL(FROM)
	count=0
	For i=0 To 15 Step 2
		COLORS$=CDG_STREAM_DATA$(i)+CDG_STREAM_DATA$(i+1)
		RED=ReadBit(COLORS$,1,4)*17
		GREEN=ReadBit(COLORS$,5,4)*17
		BLUE=ReadBit(COLORS$,9,4)*17
		CDG_COLOR_TABLE(count+from,1)=RED
		CDG_COLOR_TABLE(count+from,2)=GREEN
		CDG_COLOR_TABLE(count+from,3)=BLUE
		COLORS$="00"+HexByte$(RED)+HexByte$(GREEN)+HexByte$(BLUE)
		COLOUR=VAL(COLORS$)
		CDG_COLOR_TABLE(count+FROM,4)=COLOUR
		count=count+1
	Next
End Function

; **** SCROLL THE TEXT ****
Function CDG_SCROLL(COPY=0)
	colorA=ReadBit(CDG_STREAM_DATA$(0),3,4)
	
	hSCmd = ReadBit(CDG_STREAM_DATA$(1),1,2)
 	hScroll=ReadBit(CDG_STREAM_DATA$(1),3,4)

	vSCmd = ReadBit(CDG_STREAM_DATA$(2),1,2)
 	vScroll=ReadBit(CDG_STREAM_DATA$(2),3,4)
	If vSCmd=0
		CDG_STREAM_YOFFSET=vScroll+1
	Else If vSCmd=1
		MOVE_Y=12
	Else
		MOVE_Y=-12
	EndIf

	If hSCmd=0
		CDG_STREAM_XOFFSET=hScroll+1
	Else If hSCmd=1
		MOVE_X=6
	Else
		MOVE_X=-6
	EndIf

	If MOVE_Y=0 And MOVE_Y=0 Then Return
	CDG_STREAM_YOFFSET=0
	CDG_STREAM_XOFFSET=0
	If COPY=0 Then CDG_BORDER_PRESET(colorA)
	
	For y=1 To 216
		For x=1 To 300
			NewX=x+MOVE_X 
			NewY=y+MOVE_Y 
			If NewY<1 Then NewY=NewY+216
			If NewY>216 Then NewY=NewY-216
			If NewX<1 Then NewX=NewX+300
			If NewX>300 Then NewX=NewX-300
			CDG_SCREEN(NewX,NewY,0, CDG_BUFFER_BACK)=CDG_SCREEN(x,y,0, CDG_BUFFER_FRONT) 
			CDG_SCREEN(NewX,NewY,1, CDG_BUFFER_BACK)=CDG_SCREEN(x,y,1, CDG_BUFFER_FRONT) 
		Next
	Next
	CDG_BUFFER_FRONT=CDG_BUFFER_FRONT+1 : If CDG_BUFFER_FRONT=2 Then CDG_BUFFER_FRONT=0
	CDG_BUFFER_BACK=CDG_BUFFER_BACK+1 : If CDG_BUFFER_BACK=2 Then CDG_BUFFER_BACK=0


End Function

; ************************
; **** HELP FUNCTIONS ****
; ************************

Function HexByte$(BYTE) ;THIS WAS USED FOR GETTING RGB VALUES OF COLORS
	Return Right$(Hex$(BYTE),2)
End Function 

Function ReadBit(BINARY$,s,c) ;IT EASIER TO READ BINARY BITS FROM A STRING, RATHER THAN BITS IT SELF.
	BI$=Mid$(BINARY$,s,c)
	D=1
	RES=0
	For i=c To 1 Step -1
		If Mid$(BI$,i,1)="1" Then RES=RES+D
		D=D+D
	Next
	Return RES
End Function

Function Tok$(FIND$,TXT$)
	RESULT$="" : ADD=0 : R=0
	For I=1 To Len(TXT$)
		If ADD=0 Then CHAR$=Mid$(TXT$, I,Len(FIND$)+1)
		If ADD=1 Then CHAR$=Mid$(TXT$, I,1)
		If CHAR$="|" Then Return RESULT$
		If ADD=1 Then RESULT$=RESULT$+CHAR$
		If Trim(CHAR$)=Trim(FIND$)+"=" And ADD=0 Then I=I+Len(FIND$) : ADD=1
	Next
Return
End Function

Function Val#(StringNumeric$)
	StringNumeric$="$"+StringNumeric$
	Local Num# = 0
	Local Hex1 = ((Left$(StringNumeric$,1)="#") Or (Left$(StringNumeric$,1)="$"))
	Local Hex2 = (Left$(StringNumeric$,2)="0x")
	Local i,c
   
	StringNumeric$ = Upper(StringNumeric$)
	For i=(Hex1 + (Hex2 * 2) + 1) To Len(StringNumeric$) 
		c = Asc(Mid$(StringNumeric$,i,1))
		Select True
			Case (c>=48 And c<=57)  ;0 through 9
 				Num# = (Num# * 16) + c-48
			Case (c>=65 And c<=70)  ;A through F
				Num# = (Num# * 16) + c-55
			Default
				Return Num#                        
		End Select
	Next
	Return Num#
End Function
