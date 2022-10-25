; ID: 1519
; Author: Mikele
; Date: 2005-11-05 15:57:50
; Title: ZX Screen Simulator
; Description: ZX Spectrum screenfile viewer

;ZXVideoSimulator
;by Michal Nowak

Const	VIDRAM% 	= 16384	;videoram
Const	VIDATR% 	= 22528	;attributes

Global	ZXRAM% 		= CreateBank (65536) ;ZX memory 16KB ROM + 48KB RAM
Dim colortable%(16)				;colors for WritePixelFast
MakeColors()

Global	flashstate = True 	
Global	time = 250			; flash frq
Global	oldTime%
Global	load% = True		;load simulator
Global 	BWmode% = False

;.scr, .zx, .$c, .s  6912 And 6929 are supported
Function OpenSCR(scrfile$)
	If scrfile$<>""
		screenfile = OpenFile(scrfile$)
		If FileSize(scrfile$) = 6912 Then ReadBytes (ZXRAM,screenfile,16384,6912)
		If FileSize(scrfile$) = 6929 Then ReadBytes (ZXRAM,screenfile,16384 - 17,6912)
		CloseFile screenfile 
	EndIf
End Function

;render ZX screen
Function ZXField()
	Local argb%
	FlashEffect()

	LockBuffer BackBuffer()
	
	For y%=0 To 255 Step 2

		If load 
			argb = Rnd (0,10)
			If argb>5
				If Not BWmode Then argb = 255 Else argb = 5592405
			Else
				If Not BWmode Then argb = 16776960 Else argb = 11184810
			EndIf
		Else
			argb = 0
		EndIf
		For x%=0 To 319
			WritePixelFast x,y,argb
			WritePixelFast x,y+1,argb
		Next 
	Next 
	
	DrawScreen(32, 32, ZXRAM)

	UnlockBuffer BackBuffer()

End Function

;get INK color
Function AttrInk (byte)
	Return byte And 7
End Function

;get PAPER color
Function AttrPaper (byte)
	Return (byte Shr 3) And 7
End Function

;get BRIGHT attribute
Function AttrBright (byte)
	Return byte And 64
End Function

;get FLASH attribute
Function AttrFlash (byte)
	Return byte And 128
End Function

;video processor ;)
;x,y - screen coordinates
;ZXRAMbank - ZX memory image
Function DrawScreen(X% = 32, Y% = 32, ZXRAMbank)
	Local Xpixel%, Ypixel%, bblok%, bajt%, attr%, brightcolor%, inkcolor%, papercolor%, flashcolor%, temp%, video%
 	Ypixel = Y
 	For blok% = 0 To 2

		bblok = blok * 256

	 For at% = 0 To 7

		bat = at*32

	  For l% = 0+at*32+blok*2048 To 2047+blok*2048 Step 256

			Xpixel = X

		For b% = 0 To 31

			bajt = PeekByte (ZXRAMbank, VIDRAM+b+l)
			attr = PeekByte (ZXRAMbank, VIDATR+b+bat+bblok)
			brightcolor = AttrBright (attr) Shr 3
			inkcolor = AttrInk (attr) + brightcolor
			papercolor = AttrPaper (attr) + brightcolor
			
			flashcolor = AttrFlash (attr)

			If flashcolor=128 And flashstate = True 

				temp=inkcolor
				inkcolor=papercolor
				papercolor=temp
			
			EndIf

			
				video = (bajt And 128) ;1
				Select video
					Case 128
						WritePixelFast Xpixel,Ypixel, colortable(inkcolor)
					Case 0
						WritePixelFast Xpixel,Ypixel, colortable(paperColor)
				End Select
				Xpixel = Xpixel + 1
				
				video = (bajt And 64) ;2
				Select video
					Case 64
						WritePixelFast Xpixel,Ypixel, colortable(inkcolor)
					Case 0
						WritePixelFast Xpixel,Ypixel, colortable(paperColor)
				End Select
				Xpixel = Xpixel + 1

				video = (bajt And 32) ;3
				Select video
					Case 32
						WritePixelFast Xpixel,Ypixel, colortable(inkcolor)
					Case 0
						WritePixelFast Xpixel,Ypixel, colortable(paperColor)
				End Select
				Xpixel = Xpixel + 1
				
				video = (bajt And 16) ;4
				Select video
					Case 16
						WritePixelFast Xpixel,Ypixel, colortable(inkcolor)
					Case 0
						WritePixelFast Xpixel,Ypixel, colortable(paperColor)
				End Select
				Xpixel = Xpixel + 1
				
				video = (bajt And 8) ;5
				Select video
					Case 8
						WritePixelFast Xpixel,Ypixel, colortable(inkcolor)
					Case 0
						WritePixelFast Xpixel,Ypixel, colortable(paperColor)
				End Select
				Xpixel = Xpixel + 1

				video = (bajt And 4) ;6
				Select video
					Case 4
						WritePixelFast Xpixel,Ypixel, colortable(inkcolor)
					Case 0
						WritePixelFast Xpixel,Ypixel, colortable(paperColor)
				End Select
				Xpixel = Xpixel + 1
				
				video = (bajt And 2) ;7
				Select video
					Case 2
						WritePixelFast Xpixel,Ypixel, colortable(inkcolor)
					Case 0
						WritePixelFast Xpixel,Ypixel, colortable(paperColor)
				End Select
				Xpixel = Xpixel + 1
				
				video = (bajt And 1) ;8
				Select video
					Case 1
						WritePixelFast Xpixel,Ypixel, colortable(inkcolor)
					Case 0
						WritePixelFast Xpixel,Ypixel, colortable(paperColor)
				End Select
				Xpixel = Xpixel + 1				
		Next

     Ypixel = Ypixel + 1
	
	Next
   
   Next	

  Next
	
End Function

;FLASH timing
Function FlashEffect()
	If	MilliSecs() > oldTime + time 

		If flashstate = True
			flashstate = False
			oldTime=MilliSecs()
		Else
			flashstate = True
			oldTime=MilliSecs()
		EndIf
	EndIf
End Function

;ZXGreyscale
Function MakeGrey()
	For k=0 To 15
		colortable(k) = Grey(colortable(k))
	Next
End Function

;ZXSpectrum colors
Function MakeColors()
	colortable(0) = 0			;BLACK
	colortable(1) = 120			;BLUE
	colortable(2) = 11796480	;RED
	colortable(3) = 11141290	;MAGENTA
	colortable(4) = 30720		;GREEN
	colortable(5) = 43690		;CYAN
	colortable(6) = 13158400	;YELLOW
	colortable(7) = 13158600	;WHITE
	colortable(8) = 0			;BLACK + BRIGHT
	colortable(9) = 255			;BLUE + BRIGHT
	colortable(10) = 16711680	;RED + BRIGHT
	colortable(11) = 16711935	;MAGENTA + BRIGHT
	colortable(12) = 65280		;GREEN + BRIGHT
	colortable(13) = 65535		;CYAN + BRIGHT
	colortable(14) = 16776960	;YELLOW + BRIGHT
	colortable(15) = 16777215	;WHITE + BRIGHT
End Function

Function Grey(rgb1)
	red1=((rgb1 Shr 16) And $FF) 
	green1=((rgb1 Shr 8)And $FF)
	blue1=((rgb1 And $FF))
	;simple grayscale
	;gray=(red1+green1+blue1)/3
	
	;greyscale by Peter Scheutz
	gray=red1*0.3 + green1*.059 + blue1*0.11

	red1=gray
	green1=gray
	blue1=gray
	Return (red1 Shl 16) + (green1 Shl 8) + blue1
End Function

;------- demo --------
;press 1 to change mode (black&white/color)
;press 2 for border effects on/off ;)

scrfile$ = "screens/LuckyLucke.scr"
OpenSCR(scrfile$)

Graphics 320,256,0,2
SetBuffer BackBuffer()

AppTitle "1=BW/COLOR mode, 2=loadFX"



While Not KeyDown(1)

	Cls
	
	ZXField()

	If KeyHit( 3 ) Then load = Not load	;key 2 = border loading effect

	If KeyHit( 2 )						;key 1 = B/W / color mode
		BWmode = Not BWmode
		If BWmode = True
			MakeGrey()
		Else
			MakeColors()
		EndIf
	EndIf

	Flip

Wend

End
