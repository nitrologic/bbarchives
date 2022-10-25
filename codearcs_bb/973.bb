; ID: 973
; Author: Andy_A
; Date: 2004-03-21 19:32:26
; Title: Curved Text routine and RLE vector functions (part 1)
; Description: True curved text using RLE vector functions (not the well known sine text effect). Simple demo included.

;     Title: Blitz RLE Vector Image Demo
;Programmer: Andy Amaya
;      Date: 2004.03.25
;   Version: 1.08-Added dat2curv2buf() function
;    Update: 1.07-Added data2buffer() function
;    Update: 1.06-Added makeData() and getImgData() functions
;                 as a result, a new vector storage method is used.
;    Update: 1.05-Lock & UnLock buffer to use ReadPixelFast (does NOT work in B2D)
;    Update: 1.04-Added constants to support use with 16 bit color cards
;    Update: 1.03-Added curveImg Routine
;    Update: 1.01-Added maskImg, replaceColor, maskAndReplace routines
;    Update: 1.00-Original RLE image routine

AppTitle "Blitz Run Length Encoded Vector Images"

Global sw% = 800
Global sh% = 600
Global cd% = 32 ;<--------------- Color depth

Graphics sw, sh , cd, 2
SetBuffer BackBuffer()

Dim vecs(1) ;vector array (true dimension size in image data)
Dim pal%(1)	;Color palette array (true dimension size in image data)

;========== masking constants for 32 bit color =========
	Global maskRed% = 16711680
	Global maskGrn% = 65280
	Global maskBlu% = 255
;=======================================================

;========= masking constants for 16 bit (5-6-5) ========
;	Global maskRed% = 16252928
;	Global maskGrn% = 64512 ;change to 63488 for (5-5-5)
;	Global maskBlu% = 248
;=======================================================


Arial40bi% = LoadFont("Arial",40,True,True)
Arial18b%  = LoadFont("Arial",18,True)

Cls
Color 0,128,255
Rect 0, 0,sw-1, sh-1,True

SetFont Arial40bi
msg$ = "Rotate text"
msgWide% = StringWidth(msg$)
msgHigh% = StringHeight(msg$)
Color 255,0,0
center% = (sw-msgWide) Shr 1
Rect center, 0,msgWide,msgHigh,True
Color 255,255,255
Text center,0,msg$
SetFont arial18b
Text 350,46,"(Original Image)"

getImg(center, 0, msgWide, msgHigh)
rotateImg(  0,	10,		100,						msgWide, msgHigh)
rotateImg( 90,	10+msgWide,	100+msgHigh,			msgWide, msgHigh)
rotateImg(180,	10+msgWide,	100+msgWide+msgHigh*2,	msgWide, msgHigh)
rotateImg(270,	10,			100+msgWide+msgHigh,	msgWide, msgHigh)

maskImg(  0, 210,			100,					msgWide, msgHigh, 255, 0, 0)
maskImg( 90, 210+msgWide,	100+msgHigh,			msgWide, msgHigh, 255, 0, 0)
maskImg(180, 210+msgWide,	100+msgWide+msgHigh*2,	msgWide, msgHigh, 255, 0, 0)
maskImg(270, 210,			100+msgWide+msgHigh,	msgWide, msgHigh, 255, 0, 0)

replaceColor(  0, 410,			100,					msgWide, msgHigh, 255, 255, 255, 255, 255, 0)
replaceColor( 90, 410+msgWide,	100+msgHigh,			msgWide, msgHigh, 255, 255, 255, 255, 255, 0)
replaceColor(180, 410+msgWide,	100+msgWide+msgHigh*2,	msgWide, msgHigh, 255, 255, 255, 255, 255, 0)
replaceColor(270, 410,			100+msgWide+msgHigh,	msgWide, msgHigh, 255, 255, 255, 255, 255, 0)

maskAndReplace(  0, 610,		100,					msgWide, msgHigh, 255, 0, 0, 255, 255, 255, 255, 255, 32)
maskAndReplace( 90, 610+msgWide,100+msgHigh,			msgWide, msgHigh, 255, 0, 0, 255, 255, 255, 255, 192, 32)
maskAndReplace(180, 610+msgWide,100+msgWide+msgHigh*2,	msgWide, msgHigh, 255, 0, 0, 255, 255, 255, 200, 224, 64)
maskAndReplace(270, 610,		100+msgWide+msgHigh,	msgWide, msgHigh, 255, 0, 0, 255, 255, 255, 192, 255, 32)

Color 255,255,255
SetFont Arial18b
Text 20,365,"Example of rotated text"

Text 220,365,"Example of masking"
Text 220,388,"the red from image"

Text 420,365,"Example of replacing"
Text 420,388,"white with yellow"

Text 620,365,"Example of masking"
Text 620,388,"red and replacing"
Text 620,411,"the white"
Text 10,570,"click to continue"

Flip

WaitMouse()
FlushMouse()

SetFont Arial40bi
Color 0,128,255
Rect 0, 0, sw-1, sh-1, True
msg$ = "Zoom 1x-32x"
msgWide% = StringWidth(msg$)
msgHigh% = StringHeight(msg$)

Color 255,0,0
center% = (sw-msgWide) Shr 1
Rect 0, 0,msgWide,msgHigh,True
Color 255,255,255
Text 0,0,msg$
SetFont arial18b
Text 40,46,"(Original Image)"

getImg(0, 0, msgWide, msgHigh)

magnify(  0, center,			0,						msgWide, msgHigh, 2)
magnify( 90, center+msgWide*2-2,msgHigh*2,				msgWide, msgHigh, 2)
magnify(180, center+msgWide*2,	msgWide*2+msgHigh*4-2,	msgWide, msgHigh, 2)
magnify(270, center,			msgWide*2+msgHigh*2,	msgWide, msgHigh, 2)

SetFont Arial18b
Color 255,255,255
Text center+175,289,"(this is 2x)"
Text 10,570,"click to continue"
Flip

WaitMouse()
FlushMouse()

Color 0, 128, 255
Rect 0, 0, sw-1, sh-1, True

SetFont Arial40bi
msg$ = "Curved Text Example "
msgWide% = StringWidth(msg$)-5
msgHigh% = StringHeight(msg$)

center% = (sw-msgWide) Shr 1
Color 255,0,0
Rect center, 0,msgWide,msgHigh,True
Color 255,255,255
Text center,0,msg$
SetFont Arial18b
Text 100,0,"(Original Image)"
Text 10,570,"click to exit"
getImg(center, 0, msgWide, msgHigh)

curveImg(400, 320, 275, 180.0, 360.0, msgWide, msgHigh, 5)
curveImg(400, 320, 225, 270.0, 270.0, msgWide, msgHigh, 4)
curveImg(400, 320, 175,   0.0, 225.0, msgWide, msgHigh, 4)
curveImg(400, 320, 125,  90.0, 270.0, msgWide, msgHigh, 3)
curveImg(400, 320,  75, 112.0, 315.0, msgWide, msgHigh, 2)


Flip
WaitMouse()

FreeFont Arial40bi
FreeFont Arial18b
End

Function getImg(x%, y%, imgWidth%, imgHeight%)
	;Allocate enough memory for worst case scenario
	Dim vecs(imgWidth*imgHeight*2)
	;================
		LockBuffer
	;================
	vCount% = -2
	cCount% = -1
	lastColor% = -1
	For j% = y To y + imgHeight-1
		pixCount = 0
		For i% = x To x+imgWidth-1
			;Get pixel color from current screen coords
			pixColor% = ReadPixelFast(i, j)
			;============== 16 bit (5-6-5)================================
			;  modify pixel color  values for 16bit color
			;=============================================================
			If cd = 16 Then
				pixColor = pixColor And 16317688 ;use 16253176 for (5-5-5)
		    End If
			;=============================================================
			;Keep track of how many pixels of current color
			pixCount% = pixCount% + 1
			;If at start of a raster line OR there's a change in color
			;Then keep track of how many pixels for current color
			If (i = x) Or (pixColor <> lastColor) Then
				;remember pixel color as the last color encountered in bitmap
				lastColor = pixColor
				vecLen% = 1
				vCount = vCount + 2 ;even numbered elements hold length values
				cCount = cCount + 2 ;odd numbered elements hold color values
				;Update vector array: evenNumber element = number of same color pixels
				vecs(vCount) = vecLen
				;Update vector array:  oddNumber element = new pixel color to store
				vecs(cCount) = pixColor
			Else
				;Otherwise just add to the number of same colored pixels
				vecLen = vecLen + 1
				vecs(vCount) = vecLen
			End If
		Next
	Next
	;==================
		UnlockBuffer
	;==================
End Function

Function rotateImg(angle%, x%, y%, imgWidth%, imgHeight%)
	If angle = 0 Or angle = 90 Or angle = 180 Or angle = 270 Then
		angle2% = angle + 90
		vCount% = 0
		For i% = 1 To imgHeight
			p# = Cos(angle2)*(i-1)+x
			q# = Sin(angle2)*(i-1)+y
			lineLen% = 0
			While lineLen < imgWidth
				vecLen = vecs(vCount)
				lineLen = linelen + vecLen
				red% = (vecs(vCount + 1) And maskRed) Shr 16
				grn% = (vecs(vCount + 1) And maskGrn) Shr 8
				blu% =  vecs(vCount + 1) And maskBlu
				;===========================================================
				If cd = 16 Then
					red = red Shr 3 Shl 3
					grn = grn Shr 2 Shl 2 ;change to Shr 3 Shl 3 for (5-5-5)
					blu = blu Shr 3 Shl 3
			    End If
				;===========================================================

				Color red,grn,blu
				r# = Cos(angle)*vecLen+p
				s# = Sin(angle)*vecLen+q
				Line p,q,r,s
				vCount = vCount+2
				p=r
				q=s
			Wend
		Next 
	End If
End Function

Function maskImg(angle%, x%, y%, imgWidth%, imgHeight%, r1%, g1%, b1%)
	If angle = 0 Or angle = 90 Or angle = 180 Or angle = 270 Then
	;===16 bit (5-6-5)============================
	;  modify parameter values for 16bit color
	;=============================================
	If cd = 16 Then
		r1 = r1 Shr 3 Shl 3
		g1 = g1 Shr 2 Shl 2 ;change to Shr 3 Shl 3 for (5-5-5)
		b1 = b1 Shr 3 Shl 3
	End If
	;=============================================
		angle2% = angle + 90
		vCount% = 0
		For i% = 1 To imgHeight
			p# = Cos(angle2)*(i-1)+x
			q# = Sin(angle2)*(i-1)+y
			lineLen% = 0
			While lineLen < imgWidth
				vecLen = vecs(vCount)
				lineLen = linelen + vecLen
				red% = (vecs(vCount + 1) And maskRed) Shr 16
				grn% = (vecs(vCount + 1) And maskGrn) Shr 8
				blu% =  vecs(vCount + 1) And maskBlu
				;============== 16 bit (5-6-5)==============================
				;  modify parameter values for 16bit color
				;===========================================================
				If cd = 16 Then
					red = red Shr 3 Shl 3
					grn = grn Shr 2 Shl 2 ;change to Shr 3 Shl 3 for (5-5-5)
					blu = blu Shr 3 Shl 3
			    End If
				;===========================================================
				Color red,grn,blu
				r# = Cos(angle)*vecLen+p
				s# = Sin(angle)*vecLen+q
				If r1=red And g1=grn And b1=blu Then
					vCount = vCount+2
				Else
					Line p,q,r,s
					vCount = vCount+2
			    End If
				p=r
				q=s
			Wend
		Next 
	End If
End Function

Function replaceColor(angle%, x%, y%, imgWidth%, imgHeight%, r1%, g1%, b1%, r2%, g2%, b2%)
	If angle = 0 Or angle = 90 Or angle = 180 Or angle = 270 Then
	;===16 bit (5-6-5)========================================
	;  modify parameter values for 16bit color
	;=========================================================
	If cd = 16 Then
		r1 = r1 Shr 3 Shl 3
		g1 = g1 Shr 2 Shl 2 ;change to Shr 3 Shl 3 for (5-5-5)
		b1 = b1 Shr 3 Shl 3
		r2 = r2 Shr 3 Shl 3
		g2 = g2 Shr 2 Shl 2 ;change to Shr 3 Shl 3 for (5-5-5)
		b2 = b2 Shr 3 Shl 3
	End If
	;========================================================= 
		angle2% = angle + 90
		vCount% = 0
		For i% = 1 To imgHeight
			p# = Cos(angle2)*(i-1)+x
			q# = Sin(angle2)*(i-1)+y
			lineLen% = 0
			While lineLen < imgWidth
				vecLen = vecs(vCount)
				lineLen = linelen + vecLen
				red = (vecs(vCount + 1) And maskRed) Shr 16
				grn = (vecs(vCount + 1) And maskGrn) Shr 8
				blu =  vecs(vCount + 1) And maskBlu
				;============== 16 bit (5-6-5)==============================
				;  modify parameter values for 16bit color
				;===========================================================
				If cd = 16 Then
					red = red Shr 3 Shl 3
					grn = grn Shr 2 Shl 2 ;change to Shr 3 Shl 3 for (5-5-5)
					blu = blu Shr 3 Shl 3
			    End If
				;===========================================================
				Color red,grn,blu
				r# = Cos(angle)*vecLen+p
				s# = Sin(angle)*vecLen+q
				If r1=red And g1=grn And b1=blu Then
					Color r2,g2,b2
			    End If
				Line p,q,r,s
				vCount = vCount+2
				p=r
				q=s
			Wend
		Next 
	End If
End Function

Function maskAndReplace(angle%, x%, y%, imgWidth%, imgHeight%, r1%, g1%, b1%, r2%, g2%, b2%, r3%, g3%, b3%)
	 If angle = 0 Or angle = 90 Or angle = 180 Or angle = 270 Then
	;===16 bit (5-6-5)========================================
	;  modify parameter values for 16bit color
	;=========================================================
	If cd = 16 Then
		r1 = r1 Shr 3 Shl 3
		g1 = g1 Shr 2 Shl 2 ;change to Shr 3 Shl 3 for (5-5-5)
		b1 = b1 Shr 3 Shl 3
		r2 = r2 Shr 3 Shl 3
		g2 = g2 Shr 2 Shl 2 ;change to Shr 3 Shl 3 for (5-5-5)
		b2 = b2 Shr 3 Shl 3
		r3 = r3 Shr 3 Shl 3
		g3 = g3 Shr 2 Shl 2 ;change to Shr 3 Shl 3 for (5-5-5)
		b3 = b3 Shr 3 Shl 3
	End If
	;========================================================= 
		angle2% = angle + 90
		vCount% = 0
		For i% = 1 To imgHeight
			p# = Cos(angle2)*(i-1)+x
			q# = Sin(angle2)*(i-1)+y
			lineLen% = 0
			While lineLen < imgWidth
				vecLen = vecs(vCount)
				lineLen = linelen + vecLen
				red = (vecs(vCount + 1) And maskRed) Shr 16
				grn = (vecs(vCount + 1) And maskGrn) Shr 8
				blu =  vecs(vCount + 1) And maskBlu
				;============== 16 bit (5-6-5)==============================
				;  modify parameter values for 16bit color
				;===========================================================
				If cd = 16 Then
					red = red Shr 3 Shl 3
					grn = grn Shr 2 Shl 2 ;change to Shr 3 Shl 3 for (5-5-5)
					blu = blu Shr 3 Shl 3
			    End If
				;===========================================================
				Color red,grn,blu
				r# = Cos(angle)*vecLen+p
				s# = Sin(angle)*vecLen+q				
				If r2=red And g2=grn And b2=blu Then
					Color r3,g3,b3
					Line p,q,r,s
					vCount = vCount+2
				Else
					If r1=red And g1=grn And b1=blu Then
						vCount = vCount+2
					Else
						Line p,q,r,s
						vCount = vCount+2
				    End If
				End If
				p=r
				q=s
			Wend
		Next
	End If
End Function

Function magnify(angle%, x%, y%, imgWidth%, imgHeight%, magLevel%)
    If angle = 0 Or angle = 90 Or angle = 180 Or angle = 270 Then
		If magLevel > 0 And magLevel < 33 Then
			angle2% = angle + 90
			vCount% = 0
			i% = 1
			While i < imgHeight*magLevel
				p# = Cos(angle2)*(i-1)+x
				q# = Sin(angle2)*(i-1)+y
				lineLen% = 0
				While lineLen < imgWidth
					vecLen = vecs(vCount)
					lineLen = linelen + vecLen
					red = (vecs(vCount + 1) And maskRed) Shr 16
					grn = (vecs(vCount + 1) And maskGrn) Shr 8
					blu =  vecs(vCount + 1) And maskBlu
					;============== 16 bit (5-6-5)==============================
					;  modify parameter values for 16bit color
					;===========================================================
					If cd = 16 Then
						red = red Shr 3 Shl 3
						grn = grn Shr 2 Shl 2 ;change to Shr 3 Shl 3 for (5-5-5)
						blu = blu Shr 3 Shl 3
				    End If
					;===========================================================
					Color red,grn,blu
					Select angle
						Case  0
							boxWide% = magLevel * vecLen
							boxHigh% = magLevel
							Rect p, q, boxWide, boxHigh, True
							p = p + boxWide
						Case  90
							boxWide% = magLevel
							boxHigh% = magLevel * vecLen
							Rect p, q, boxWide, boxHigh, True
							q = q + boxHigh
						Case  180
							p = p - magLevel * vecLen
							boxWide = magLevel * vecLen
							boxHigh = magLevel
							Rect p, q, boxWide, boxHigh, True
						Case  270
							q = q - magLevel*vecLen
							boxWide = magLevel
							boxHigh = magLevel * vecLen
							Rect p, q, boxWide, boxHigh, True
					End Select
					vCount = vCount + 2
				Wend
				i = i + magLevel
			Wend
		End If
	End If
End Function

Function curveImg(centerX%, centerY%, radius%, startAngle#, arcSegment#, imgWidth%, imgHeight%, penSize%)
	If penSize > 1 Then penOffset# = penSize/2
	stepSize# = arcSegment/imgWidth
	vCount% = 0
	For i% = 1 To imgHeight
		lineLen% = 0
		arc# = startAngle
		While lineLen < imgWidth
			arcLen = vecs(vCount)
			lineLen = lineLen + arcLen
			red = (vecs(vCount + 1) And maskRed) Shr 16
			grn = (vecs(vCount + 1) And maskGrn) Shr 8
			blu =  vecs(vCount + 1) And maskBlu
			;============== 16 bit (5-6-5)==============================
			;  modify parameter values for 16bit color
			;===========================================================
			If cd = 16 Then
				red = red Shr 3 Shl 3
				grn = grn Shr 2 Shl 2 ;change to Shr 3 Shl 3 for (5-5-5)
				blu = blu Shr 3 Shl 3
		    End If
			;===========================================================
			Color red, grn, blu
			arcInc# = arcLen*stepSize
			c# = arc#
			While c <= arc+arcInc
				x = Cos(c)*radius+centerX
				y = Sin(c)*radius+centerY
				If penSize > 1 Then
					Rect x-penOffset, y-penOffset, penSize, penSize, True
				Else
					Plot x,y
				End If
				c = c+stepSize
			Wend
			arc = arc+arcInc
			vCount = vCount+2
		Wend
		radius = radius - 1
	Next
End Function
