; ID: 1277
; Author: Pinete
; Date: 2005-02-03 16:14:09
; Title: TFont Antialias 2D font system for realtime
; Description: Antialias Font library for realtime

;
; ************************************************************
; * Project Name: TFont2D - Antialiased 2D text v1.0 for realtime
; * Author(s): Pinete
; * Date Started: -
; * Last Updated: -
; * Website: -
; * Email: 
; * Version: 1.0
; * Copyright: Donated to the public domain
; * Trademark: -
; * Product: -
; ************************************************************
;
;
; Based on the piece of code called "Anti-Alias Simulation"
; by Daniel Nobis who gives thanks to Triton für die Idee.
;
; I think I have improved the code to obtain a useful library for scoreboards,
; sports management games or, in general, games that need to use very small fonts or
; the text is important.
;
; I have tried to comment all the code.
; The library and all its functions are really easy to use and modify.
; The code is not brilliant, of course, and I'm sure that everybody with
; a little more knowledge than me can improve the functions a lot.
; At least I have achieve my first goal, doing my first contribution to the community.
;
; How it works?
;
; First a blank image is created, with the size of the character we will draw within.
; After that, we draw a rectangle with the color we want to antialiased with.
; Scale the image by half.
; Now, we mask the image with the background color.
; The result is the character with the antialiased pixels but whitout the background.
; That's the action that we will do with all the characters beetween 31 and 128
; And we will do again with differents background colors (from black to white)
; in order to obtain fonts with antialias for different backgrounds (five by default)
; We will do the same with the shadow.
; The last point is to draw the text.
; To do that, it reads the pixel of the graphic buffer (backbuffer, for example, and after draw the background)
; with the goal of obtain the most similar generated antialias to draw the character on to that background.
;
; From my point of view its usage is very comfortable and easy to manage and modify for a personal adaptation.
; It's very far to be fantastic but it is a way to print beauty strings at screen whitout use the 3Ds and
; with a very nice result.
;
; :)
;
; Should be fantastic if somebody could improve it in terms of speed or flexibility.
; Thanks to all that support blitbasic and blitzcoder community.

;
; The code has been writed using BB3D v1.87 and Protean Editor
; using four or five hours more or less.
;
; I hope you find this useful!

;
; List of functions
; -------------------
;
; TFontCreate
; TFontDraw
; TFontSet
; TfontGetWidth
; TFontShadow
; TFontShadowDistance
; TFontSetAutoGrad
; TFontSetGrad
; TFontDebugON
; TFontDebugOFF
;

Type TipoFont
	Field Tchar[255]
	Field Tlongx[255]
	Field Tlongy[255]
	Field TShadow[255]
	Field ent
	Field ref
End Type

Type TipoShadow
	Field Tchar[255]
	Field Tlongx[255]
	Field Tlongy[255]
	Field TShadow[255]
	Field ent
	Field ref
End Type

; Internal variables

Const GradSteps = 5						; Number of steps in which the antialias is precalculated (from black to white)
Const MaxFonts = 255					; Maximum fonts to be host
Dim fnt.TipoFont(GradSteps,MaxFonts)	; Host the fonts
Dim shw.TipoFont(GradSteps,MaxFonts)	; Host its shadows
Global TBoolShadow = -1					; Shadow Bool. -1 = OFF, 1 = ON
Global TShadowDistX = 1
Global TShadowDistY = 1
Global TFontDefaultGrad = 3				; Sets the default grad if it is not automatic
Global TFontFont = 1					; Current Font
Global TFontBoolDEBUG = -1				; Debug for each string
										; it will show the current grad level, x,y, 2D width and 2D Height


; Framerate variables

Const FPS=85
Const debug=0
Global FPS_Oldtime, FPS_Newtime, FPS_Ticks
Global FPS_Current,FPS_Final
Global FPS_SampleRate   = 5  ;Take a sample every N ticks
Global FPS_Samples      = 10 ;Samples to average (res of the average)
Global FPS_BufferIndex  = 1
Global FPS_Font
Dim FPS_Buffer(10)




Function TFontCreate.TipoFont(ref = 1,font,r1=255,g1=255,b1=255)
	
	; You need to call this function before your main loop starts.
	; To create a font, depending of the computer specs will need
	; 1 second (aprox). Surely you will need various fonts in your
	; game, and using 1 second per font, the creation of five fonts
	; would take about 5 or 6 seconds, more or less.
	;
	;
	;
	;ref 	-	Is the "reference" of the fonts. It is a number that will identify the font.
	;			Each font need a different reference when it is going to be created
	;
	;font 	-	The font handle returned by the BB function "Loadfont"
	;
	;r1,g1,b1 - The RGB color of the font. The fonts cannot change its color in realtime because
	;			is a very slow process. In order to have various colours for the fonts, they need to
	;			be generated previously.
	

	; Init RGB and misc vars for the background antialias
	Local CharBegin = 31
	Local CharFinal = 122
	r = 0
	g = 0
	b = 0
	grad = 1
	
	; Select the font
	SetFont font
	
	; Generate the fonts with the antialias for the diferent gradients
	For gr = 1 To GradSteps
		fnt(gr,ref) = New TipoFont
		For c = CharBegin To CharFinal
			char$ = Chr$(c)
			fnt(grad,ref)\Tchar[c]  = CreateImage(StringWidth(char$),StringHeight(char$))
			fnt(grad,ref)\Tlongx[c] = StringWidth(char$)/2
			fnt(grad,ref)\Tlongy[c] = StringHeight(char$)
			SetBuffer ImageBuffer(fnt(grad,ref)\Tchar[c])
			Color r,g,b
			Rect 0,0,ImageWidth(fnt(grad,ref)\Tchar[c]),ImageHeight(fnt(grad,ref)\Tchar[c])
			Color r1,g1,b1
			Text 0,0,char$
			MaskImage fnt(grad,ref)\Tchar[c],r,g,b
			ScaleImage fnt(grad,ref)\Tchar[c],0.5,0.5
		Next
		;DebugLog ("-> Antialias created for RGB: "+Str$(r)+","+Str$(g)+","+Str$(b))
		r = r + Int(255/(GradSteps-1));63
		g = g + Int(255/(GradSteps-1))
		b = b + Int(255/(GradSteps-1))
		grad = grad + 1
	Next
	
	; Generate the Shadows for each font, again, with the same number of antialias levels
	
	; init RGB for the background antialias
	r = 0
	g = 0
	b = 0
	grad = 1
	SetFont font
	; Generate the antialias for the diferent gradients
	For gr = 1 To GradSteps
		shw(gr,ref) = New TipoFont
		For c = CharBegin To CharFinal
			char$ = Chr$(c)
			shw(grad,ref)\Tchar[c]  = CreateImage(StringWidth(char$),StringHeight(char$))
			shw(grad,ref)\Tlongx[c] = StringWidth(char$)/2
			shw(grad,ref)\Tlongy[c] = StringHeight(char$)
			SetBuffer ImageBuffer(shw(grad,ref)\Tchar[c])
			Color r,g,b
			Rect 0,0,ImageWidth(shw(grad,ref)\Tchar[c]),ImageHeight(shw(grad,ref)\Tchar[c])
			Color 50,50,50 ;The shadow color can be altered. 50,50,50 is not a pure black.
			Text 0,0,char$
			MaskImage shw(grad,ref)\Tchar[c],r,g,b
			ScaleImage shw(grad,ref)\Tchar[c],0.5,0.5
		Next
		;DebugLog ("-> Shadow Antialias created for RGB: "+Str$(r)+","+Str$(g)+","+Str$(b))
		r = r + Int(255/(GradSteps-1))
		g = g + Int(255/(GradSteps-1))
		b = b + Int(255/(GradSteps-1))
		grad = grad + 1
	Next
	
	SetBuffer BackBuffer()
	
End Function

Function TFontDraw(cad$,x,y)
	
	;
	; It works like the standard BB function.
	; Just to pass the string (cad$) and its 2D coordinates.
	; Of course, it's a need to create the font with TcreateFont(...) previous to this call
	;
	
	;
	; GradType = 0 for automatic selection of Antialias Gradient
	; GradType = 1 to 5 for manual selection
	;			 1...Darkest (black background)
	;			 5...White

	Local cx,cy
	Local ref
	cx = x
	cy = y
	Local numchar
	Local GradTypeMode 
	
	GradTypeMode = TFontDefaultGrad
	ref = TFontFont
	
	; Let's see if we are debugging strings...
	If TFontBoolDEBUG > 0
		cad$ = cad$ + " -> "+Str$(TFontDefaultGrad)+", "+Str$(x)+", "+Str$(y)+", "+Str$(TfontGetWidth(cad$))
	EndIf
	
	
	If GradTypeMode <> 0 Then GradType = GradTypeMode
	For r = 1 To Len(cad$)
		numchar = Asc(Mid(cad$,r,1))
		If GradTypeMode = 0 Then GradType = TExtractColorSingle(cx+1,cy+1)
		
		If TBoolShadow > 0 Then DrawImage shw(GradType,ref)\Tchar[numchar],cx+TShadowDistX,cy+TShadowDistY
		DrawImage fnt(GradType,ref)\Tchar[numchar],cx,cy
		
		cx = cx + fnt(GradType,ref)\Tlongx[numchar]
	Next

End Function

Function TFontSet(nFont)
	
	; Sets the current font to use.
	
	TFontFont = nFont
	
End Function

Function TfontGetWidth(wcad$)
	
	; Return the 2D width in pixels of the string with the current font and with
	; This could be useful to detect clicks and something similar...
	
	Local ms
	Local numch
	Local ref
	
	ms = 0
	ref = TFontFont
	
	For r = 1 To Len(wcad$)
		numch = Asc(Mid(wcad$,r,1))
		ms = ms + fnt(1,ref)\Tlongx[numch]
	Next
	
	Return ms
	
End Function

Function TFontShadow(bs)
	
	; Activate or Deactivate Shadow for the current font.
	; Using a parameter lower or equal to 0 the shadows will be OFF
	; Using a parameter higher than 0 the shadows will be ON
	;
	; Remember, using shadows is a bit slower than drawing the normal text
	; because you are drawing two strings!
	; However, if you are not managing a really great quantity of text at screen,
	; you could use shadows whitout any problem.
	
	If bs > 0 Or bs = True Then TBoolShadow = 1
	If bs <= 0 Or bs = False Then TBoolShadow = -1
	
End Function

Function TFontShadowDistance(xd,yd)

	; Just define the distance of the shadows
	; xd is the x distance in pixels
	; yd is the y distance in pixels
	; normal values are 1,1 or 2,2 for both parameters.

	TShadowDistX = xd
	TShadowDistY = yd
	
End Function

Function TExtractColorSingle(xe,ye)
	
	; FOR INTERNAL USE ONLY
	; Basically this function makes a very simple task.
	; Just read the pixel at the coordinates in which the character will be printed
	; in order to obtain its color information.
	; After that, it look for the best gradient of the generated text in order to
	; select the better visual result.
		
	LockBuffer
	
	; Check for the first point
	 
	TRead1 = ReadPixelFast(xe,ye,BackBuffer())
	red#  =((TRead1 Shr 16) And $FF)
	green#=((TRead1 Shr 8)And $FF)
	blue# =((TRead1 And $FF))
	res1 = red + green + blue
	Pass = Int(255/(GradSteps-1))
	;Rgrad1 = (res1+63)/3/63
	Rgrad1 = (res1+Pass)/3/Pass
		
	UnlockBuffer
	
	RetGrad = Int(Rgrad1)+1
	If RetGrad>GradSteps Then RetGrad = GradSteps
	Return RetGrad
	
End Function

Function TExtractColorMultiple(xe,ye)
	
	; FOR INTERNAL USE ONLY
	; This function is identical to the previous one, just with the difference
	; that this time, it read two pixel and extract the media.
			
	LockBuffer
	
	; Check for the first point
	 
	TRead1 = ReadPixelFast(xe+2,ye+2,BackBuffer())
	red#  =((TRead1 Shr 16) And $FF)
	green#=((TRead1 Shr 8)And $FF)
	blue# =((TRead1 And $FF))
	res1 = red + green + blue
	Rgrad1 = (res1+Int(255/(GradSteps-1)))/3/(Int(255/(GradSteps-1)))
	
	; Check for the second one
	
	TRead2 = ReadPixelFast(xe+6,ye+6,BackBuffer())
	red#  =((TRead2 Shr 16) And $FF)
	green#=((TRead2 Shr 8)And $FF)
	blue# =((TRead2 And $FF))
	res2 = red + green + blue
	Rgrad2 = (res2+Int(255/(GradSteps-1)))/3/(Int(255/(GradSteps-1)))
	
	; Make the media
	Rgrad = (Rgrad1 + Rgrad2) / 2
	
	UnlockBuffer
	
	RetGrad = Int(Rgrad)+1
	If RetGrad>GradSteps Then RetGrad = GradSteps
	Return RetGrad;Int(Rgrad)+1
	
End Function

Function TFontSetGrad(defgrad = 0)

	; Sets manually the gradient to use with the current font.
	; Default gradients works in the range from 1 to 5.
	; If the selected gradient is 0 the selection turn automatic for each character looking for
	; the best result-
	
	; Default is automatic (=0)
	
	If defgrad > GradSteps Then defgrad = GradSteps
	If defgrad < 0 Then deafgrad = 0
	TFontDefaultGrad = defgrad
	;DebugLog(TFontDefaultGrad)
	Return defgrad
	
End Function

Function TFontSetAutoGrad()
	
	; No parameters. Just call this function to set the automatic selection
	; of gradient for each character.
	
	TFontDefaultGrad = 0
	
End Function

Function TFontDebugON()
	
	; Activate the debug mode for strings.
	; Its a silly function that show, after draw each string, an addon that
	; contains info about its gradient, x and y coordinates and 2D width.
	TFontBoolDEBUG = -1
	
End Function

Function TFontDebugOFF()
	
	; Deactivate the debug mode for strings.
	TFontBoolDEBUG = 1
	
End Function

Function Get_FPS(PosX#=10,PosY#=2)
	
	; This function is not of my property.
	; It's a very useful function to scan the media of FPS.
	; I dont remember who is the original autor.
	; The credits goes for him as well, of course.
	; I'm sorry for this.
    
         FPS_Newtime = MilliSecs()
         FPS_Ticks = FPS_Ticks + 1
         If FPS_Ticks > FPS_SampleRate Then
            FPS_Current = FPS_Newtime - FPS_Oldtime
            If FPS_Current = 0 Then FPS_Current = 1000 Else FPS_Current = 1000/FPS_Current
            FPS_Buffer(FPS_BufferIndex) = FPS_Current
            FPS_BufferIndex = FPS_BufferIndex + 1
            If FPS_BufferIndex > FPS_Samples Then
                 For FPS_Count = 1 To FPS_Samples
                     FPS_Master = FPS_Master + FPS_Buffer(FPS_Count)                      
                 Next
                 FPS_Final = FPS_Master / FPS_Samples
                 FPS_BufferIndex = 1
            EndIf
            FPS_Ticks = 0
         EndIf
         FPS_Oldtime = MilliSecs()
         
         Text(PosX#,PosY#,"iFPS:"+FPS_Final) 
         
End Function
