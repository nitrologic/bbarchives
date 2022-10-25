; ID: 974
; Author: Andy_A
; Date: 2004-03-25 03:28:06
; Title: Curved Text (part 2)
; Description: Added save images to data statements and restoring directly to image buffers. Simple demo included

;     Title: Blitz RLE Vector Data To Buffer Demo
;Programmer: Andy Amaya
;      Date: 2004.03.25
;   Version: 1.08-Added dat2curv2buf() function
;    Update: 1.07-Added data2buffer() function
;    Update: 1.06-Added makeData() and getImgData() functions
;    Update: 1.05-Lock & UnLock buffer to use ReadPixelFast (does NOT work in B2D)
;    Update: 1.04-Added constants to support use with 16 bit color cards
;    Update: 1.03-Added curveImg Routine
;    Update: 1.01-Added maskImg, replaceColor, maskAndReplace routines
;    Update: 1.00-Original RLE image routine

AppTitle "Blitz Run Length Encoded Vector Data To Image Buffer"

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

Arial18b%  = LoadFont("Arial",18,True)

st = MilliSecs()
SetFont arial18b
Color 0, 128, 255
Rect 0, 0, sw-1, sh-1, True


;use "Data to Image Buffer" image data statements
Restore data2buf
;Get text image and store in gfxMsg0 image buffer
  gfxMsg0 = data2buffer(0)

;use "Data to Image Buffer" image data statements
Restore data2buf
;Get text image and store in gfxMsg90 image buffer
 gfxMsg90 = data2buffer(90)

;use "Data to Image Buffer" image data statements
Restore data2buf
;Get text image and store in gfxMsg180 image buffer
gfxMsg180 = data2buffer(180)

;use "Data to Image Buffer" image data statements
Restore data2buf
;Get text image and store in gfxMsg270 image buffer
gfxMsg270 = data2buffer(270)

;use "Data to Image Buffer" image data statements
Restore data2buf
;Get text image, make it curved  and store in gfxCurve image buffer
gfxCurve = dat2curv2buf(125, 115.0, 310.0, 3, 255, 0, 0)

;use yellow "gradient" image data statements
Restore gradient
;Get gradient image, flip to 180 degrees and store in gfxGrad image buffer
gfxGrad = data2buffer(180)



DrawBlock(  gfxMsg0, 227, 100)
DrawBlock( gfxMsg90, 227+305, 140)
DrawBlock(gfxMsg180, 227, 485)
DrawBlock(gfxMsg270, 227, 140)

DrawBlock(gfxGrad,267,140)

MaskImage gfxCurve, 255, 0, 0
DrawImage(gfxCurve, 400-125, 320-125)

et = MilliSecs() - st

Color 128,255,255

Text 0, 0, "To read in one text image from data statements four times at  0, 90, 180, and 270 degrees."
Text 0, 32, "Read in a gradient image from data statements."
Text 0, 16, "Read in the same text image from data statements one more time, and wrap around a curve."
Text 0, 48, "Store all six images in separate Blitz image buffers."
Text 0, 64, "And then to display all six newly created image buffers on the screen."
Text 0, 80, "Takes "+et+" milliseconds."

Text 0, 112, "(The text image was a 41K bmp,"
Text 0, 128, "it was reduced to a 9K text file."
Text 0, 160, "The gradient image was a 274K"
Text 0, 176, "bmp, it was reduced to a 5K"
Text 0, 192, "text file. )"





Text 10,570,"click to exit"


Flip
WaitMouse()

FreeFont Arial18b

FreeImage gfxMsg0
FreeImage gfxMsg90
FreeImage gfxMsg180
FreeImage gfxMsg270
FreeImage gfxGrad
FreeImage gfxCurve

.data2buf ;image data for "Data to Image Buffer" text on red background - 345x40
Data 345,40,3994,5
Data -65536,-32897,-1,-16449,-24673
Data 345,0,345,0,345,0,345,0,345,0,345,0,345,0,5,0,1,1,11,2,1,3,1,4,1,1,113,0,1,1,5,2,104,0,1,1,13,2,1,3,1,4,1,1,31,0,1,4,1,3,3,2,1,3,1,4,1,1,4,0,1,4,1,3,3,2,1,3,1,4,1,1,34,0,5,0,1,1,14,2,1,3,1,1,32,0
Data 1,1,1,3,39,0,1,1,1,3,36,0,1,1,4,2,1,3,104,0,1,1,16,2,1,3,29,0,1,3,8,2,3,0,1,3,8,2,34,0,5,0,1,4,16,2,1,1,30,0,1,3,1,2,1,3,38,0,1,3,1,2,1,3,36,0,1,4,4,2,1,4,104,0,1,4,17,2,1,3,27,0,1,4,8,2,1,3,2,0,1,4
Data 8,2,1,3,34,0,5,0,1,3,17,2,1,1,27,0,1,4,3,2,1,3,36,0,1,4,3,2,1,3,36,0,1,3,4,2,1,4,104,0,1,3,18,2,1,4,26,0,1,3,8,2,1,4,2,0,1,3,8,2,1,4,34,0,5,0,5,2,1,1,5,0,1,1,1,3,5,2,1,3,26,0,1,4,4,2,1,4,35,0,1,4
Data 4,2,1,4,36,0,5,2,1,1,104,0,5,2,1,1,7,0,1,4,5,2,1,3,26,0,5,2,1,4,2,0,1,4,1,1,2,0,5,2,1,4,2,0,1,4,1,1,34,0,4,0,1,1,5,2,8,0,1,4,5,2,1,1,25,0,5,2,1,1,35,0,5,2,1,1,35,0,1,1,5,2,104,0,1,1,5,2,9,0,1,1,5,2
Data 25,0,1,1,5,2,6,0,1,1,5,2,39,0,4,0,1,1,4,2,1,3,9,0,1,3,4,2,1,4,7,0,1,1,2,3,4,2,1,3,1,4,6,0,1,1,10,2,5,0,1,1,2,3,4,2,1,3,1,4,16,0,1,1,10,2,6,0,1,4,1,3,4,2,2,3,1,1,18,0,1,1,4,2,1,3,4,0,1,4,4,2,1,3,2,0
Data 1,4,1,3,2,2,1,3,1,4,5,0,1,4,1,3,3,2,1,4,9,0,1,1,2,3,4,2,1,3,1,4,10,0,1,1,1,4,3,2,1,3,1,4,2,0,1,1,4,2,1,3,6,0,1,1,1,4,1,3,4,2,1,3,1,1,17,0,1,1,4,2,1,3,10,0,5,2,4,0,1,4,4,2,1,3,5,0,1,4,4,2,1,3,1,0,1,1
Data 11,2,1,1,11,2,5,0,1,1,1,4,1,3,4,2,1,3,1,1,7,0,1,4,4,2,1,3,2,0,1,4,3,2,1,3,2,0,4,0,1,4,4,2,1,4,9,0,1,1,4,2,1,3,5,0,1,1,1,3,10,2,1,4,4,0,1,4,9,2,1,3,3,0,1,1,1,3,10,2,1,4,14,0,1,4,9,2,1,3,4,0,1,1,10,2
Data 1,3,17,0,1,4,4,2,1,4,4,0,1,3,4,2,1,4,1,1,7,2,1,3,2,0,1,1,7,2,1,3,6,0,1,1,1,3,10,2,1,4,7,0,1,4,7,2,1,3,1,0,1,4,4,2,1,4,5,0,1,4,9,2,1,4,16,0,1,4,4,2,1,4,9,0,1,1,4,2,1,3,4,0,1,3,4,2,1,4,5,0,1,3,4,2,1,4
Data 1,0,1,4,10,2,1,3,1,4,10,2,1,3,4,0,1,4,9,2,1,4,6,0,1,3,4,2,1,4,1,1,5,2,1,4,2,0,4,0,1,3,4,2,1,4,10,0,5,2,4,0,1,1,13,2,1,1,3,0,1,3,9,2,1,4,2,0,1,1,13,2,1,1,13,0,1,3,9,2,1,4,3,0,1,3,13,2,16,0,1,3,4,2
Data 1,4,4,0,1,3,4,2,1,3,9,2,1,1,1,4,9,2,1,4,4,0,1,1,13,2,1,1,5,0,1,3,9,2,2,3,4,2,1,4,4,0,1,3,11,2,1,4,15,0,1,3,4,2,1,1,9,0,1,3,4,2,1,4,4,0,5,2,1,1,5,0,1,3,4,2,1,1,1,0,1,3,10,2,1,4,1,3,10,2,1,4,3,0,1,3
Data 11,2,1,4,5,0,1,3,4,2,1,4,6,2,3,0,4,0,5,2,1,1,10,0,5,2,4,0,14,2,1,3,3,0,10,2,1,1,2,0,14,2,1,3,13,0,10,2,1,1,2,0,1,4,14,2,1,3,15,0,5,2,1,1,4,0,15,2,1,3,10,2,1,3,4,0,14,2,1,3,4,0,1,3,16,2,1,1,3,0,1,3
Data 13,2,1,1,14,0,5,2,1,1,6,0,1,1,1,4,5,2,1,3,4,0,1,1,5,2,6,0,5,2,2,0,11,2,1,1,11,2,1,1,2,0,1,3,13,2,1,1,4,0,11,2,1,4,3,0,3,0,1,1,5,2,11,0,5,2,3,0,1,4,5,2,1,1,2,0,1,1,1,3,5,2,5,0,5,2,1,1,4,0,1,4,5,2,1,1
Data 2,0,1,1,1,3,5,2,15,0,5,2,1,1,4,0,1,1,6,2,1,4,2,0,1,1,1,3,5,2,1,1,13,0,1,1,5,2,4,0,1,1,7,2,1,1,1,0,1,1,1,3,6,2,1,3,1,1,2,0,1,3,5,2,3,0,1,4,5,2,1,1,2,0,1,1,1,3,5,2,3,0,1,1,5,2,1,3,1,1,1,0,1,1,1,3,7,2
Data 3,0,1,1,5,2,1,4,2,0,1,1,5,2,1,4,13,0,1,1,18,2,1,3,5,0,1,1,4,2,1,3,5,0,1,1,4,2,1,3,4,0,1,1,5,2,6,0,1,1,5,2,5,0,1,1,5,2,1,4,2,0,1,1,5,2,1,4,3,0,1,1,7,2,1,3,1,1,1,0,1,1,4,0,3,0,1,1,4,2,1,3,11,0,5,2,3,0
Data 3,3,2,2,1,1,5,0,5,2,5,0,5,2,5,0,3,3,2,2,1,1,5,0,5,2,15,0,5,2,5,0,6,2,6,0,1,3,4,2,1,3,13,0,1,1,4,2,1,3,4,0,1,4,5,2,1,3,4,0,1,4,5,2,1,3,4,0,1,1,4,2,1,3,3,0,3,3,2,2,1,1,5,0,5,2,3,0,1,3,4,2,1,3,5,0,1,3
Data 5,2,1,3,3,0,5,2,1,4,4,0,1,4,4,2,1,3,13,0,1,1,17,2,1,1,6,0,1,4,4,2,1,4,5,0,1,4,4,2,1,3,4,0,1,4,4,2,1,3,6,0,1,4,4,2,1,3,5,0,5,2,1,4,4,0,1,4,4,2,1,3,3,0,1,4,6,2,1,4,8,0,3,0,1,4,4,2,1,4,10,0,1,1,5,2,13,0
Data 1,1,5,2,4,0,1,1,5,2,15,0,1,1,5,2,14,0,1,1,5,2,4,0,1,1,5,2,1,1,6,0,1,1,5,2,13,0,1,4,4,2,1,4,4,0,1,3,5,2,1,1,4,0,1,4,5,2,1,1,4,0,1,4,4,2,1,4,13,0,1,1,5,2,2,0,1,1,5,2,1,1,5,0,1,1,5,2,1,4,2,0,1,1,5,2
Data 6,0,5,2,13,0,1,4,17,2,1,3,6,0,1,3,4,2,1,1,5,0,1,4,4,2,1,4,4,0,1,4,4,2,1,4,6,0,1,4,4,2,1,4,4,0,1,1,5,2,6,0,5,2,3,0,1,3,5,2,1,4,9,0,3,0,1,3,4,2,1,4,10,0,1,4,4,2,1,3,7,0,3,1,2,4,1,3,5,2,1,3,4,0,1,4,4,2
Data 1,3,9,0,3,1,2,4,1,3,5,2,1,3,14,0,1,4,4,2,1,3,4,0,1,4,4,2,1,3,8,0,5,2,13,0,1,3,4,2,1,4,4,0,1,3,4,2,1,3,5,0,1,3,4,2,1,3,5,0,1,3,4,2,1,4,7,0,3,1,2,4,1,3,5,2,1,3,2,0,1,4,4,2,1,3,7,0,5,2,1,4,2,0,1,3,4,2
Data 1,4,6,0,5,2,13,0,1,3,18,2,1,3,5,0,5,2,6,0,1,3,4,2,1,1,4,0,1,3,4,2,1,1,6,0,1,3,4,2,1,1,4,0,1,3,4,2,1,4,6,0,5,2,3,0,1,3,4,2,1,3,10,0,3,0,5,2,1,1,10,0,1,3,4,2,1,4,4,0,1,4,1,3,12,2,1,4,4,0,1,3,4,2,1,4
Data 6,0,1,4,1,3,12,2,1,4,14,0,1,3,4,2,1,4,4,0,1,3,4,2,1,4,8,0,5,2,13,0,5,2,1,1,4,0,5,2,1,1,5,0,5,2,1,1,5,0,5,2,1,1,4,0,1,4,1,3,12,2,1,4,2,0,1,3,4,2,1,4,7,0,5,2,1,1,2,0,1,3,16,2,13,0,5,2,1,1,7,0,1,1,1,4
Data 5,2,1,4,3,0,1,1,4,2,1,3,6,0,5,2,5,0,5,2,1,1,6,0,5,2,1,1,4,0,1,3,16,2,3,0,5,2,1,4,10,0,2,0,1,1,5,2,11,0,5,2,1,1,2,0,1,1,15,2,1,1,4,0,1,3,4,2,1,1,4,0,1,1,15,2,1,1,14,0,1,3,4,2,1,1,4,0,5,2,1,1,7,0,1,1
Data 5,2,12,0,1,1,5,2,4,0,1,1,5,2,6,0,5,2,5,0,1,1,5,2,3,0,1,1,15,2,1,1,2,0,5,2,1,1,6,0,1,1,5,2,3,0,17,2,12,0,1,1,5,2,10,0,1,4,5,2,3,0,1,4,4,2,1,4,5,0,1,1,5,2,4,0,1,1,5,2,6,0,1,1,5,2,5,0,17,2,2,0,1,1,5,2
Data 11,0,2,0,1,1,4,2,1,3,10,0,1,4,5,2,2,0,1,1,6,2,1,3,2,4,2,1,5,2,5,0,5,2,4,0,1,1,6,2,1,3,2,4,2,1,5,2,15,0,5,2,5,0,5,2,8,0,1,4,4,2,1,3,12,0,1,1,4,2,1,3,4,0,1,4,4,2,1,3,5,0,1,1,4,2,1,3,5,0,1,1,4,2,1,3
Data 2,0,1,1,6,2,1,3,2,4,2,1,5,2,3,0,5,2,7,0,1,4,4,2,1,3,3,0,16,2,1,3,12,0,1,1,4,2,1,3,11,0,5,2,3,0,1,3,4,2,1,1,5,0,1,4,4,2,1,3,4,0,1,1,4,2,1,3,6,0,1,1,4,2,1,3,5,0,16,2,1,3,2,0,1,4,4,2,1,3,11,0,2,0,1,4
Data 4,2,1,4,9,0,1,1,5,2,1,4,2,0,1,3,4,2,1,3,1,1,4,0,1,4,4,2,1,3,4,0,1,1,5,2,4,0,1,3,4,2,1,3,1,1,4,0,1,4,4,2,1,3,14,0,1,1,5,2,5,0,5,2,8,0,1,3,4,2,1,4,12,0,1,4,4,2,1,4,4,0,1,4,4,2,1,4,5,0,1,4,4,2,1,4,5,0
Data 1,4,4,2,1,4,2,0,1,3,4,2,1,3,1,1,4,0,1,4,4,2,1,3,3,0,5,2,7,0,1,3,4,2,1,4,3,0,5,2,24,0,1,4,4,2,1,4,11,0,5,2,3,0,5,2,1,1,5,0,1,3,4,2,1,4,4,0,1,4,4,2,1,4,6,0,1,4,4,2,1,4,5,0,5,2,14,0,1,4,4,2,1,4,11,0
Data 2,0,1,3,4,2,1,4,9,0,1,3,5,2,3,0,5,2,6,0,1,3,4,2,1,4,4,0,1,4,4,2,1,3,4,0,5,2,6,0,1,3,4,2,1,4,14,0,1,4,4,2,1,3,5,0,5,2,1,1,6,0,1,1,5,2,1,1,12,0,1,3,4,2,1,4,4,0,1,3,4,2,1,1,5,0,1,3,4,2,1,4,5,0,1,3,4,2
Data 1,4,2,0,5,2,6,0,1,3,4,2,1,4,3,0,5,2,1,1,5,0,1,1,5,2,1,4,3,0,1,3,4,2,1,1,23,0,1,3,4,2,1,4,10,0,1,1,4,2,1,3,3,0,5,2,5,0,1,1,5,2,1,1,4,0,1,3,4,2,1,1,6,0,1,3,4,2,1,1,5,0,1,3,4,2,1,1,13,0,1,3,4,2,1,1,11,0
Data 2,0,5,2,1,1,7,0,1,1,1,3,5,2,1,1,3,0,5,2,5,0,1,4,5,2,1,4,4,0,1,3,4,2,1,4,4,0,5,2,5,0,1,4,5,2,1,4,14,0,1,3,4,2,1,4,5,0,1,3,4,2,1,3,5,0,1,1,5,2,1,3,13,0,5,2,1,1,4,0,5,2,1,1,5,0,5,2,1,1,5,0,5,2,1,1,2,0
Data 5,2,5,0,1,4,5,2,1,4,3,0,1,3,4,2,1,3,5,0,1,3,5,2,1,1,3,0,1,3,4,2,1,4,5,0,1,4,1,2,1,3,1,4,1,1,13,0,5,2,1,1,10,0,5,2,1,4,2,0,1,1,5,2,4,0,1,1,6,2,5,0,5,2,1,1,6,0,5,2,1,1,5,0,1,3,4,2,1,4,5,0,1,4,1,2,1,3
Data 1,4,1,1,3,0,5,2,12,0,1,0,1,1,5,2,5,0,2,1,1,4,6,2,1,4,4,0,5,2,1,3,1,1,1,0,1,1,1,4,6,2,1,1,4,0,5,2,1,4,4,0,5,2,1,3,1,1,1,0,1,1,1,4,6,2,1,1,14,0,5,2,1,4,5,0,1,1,5,2,1,3,1,1,2,0,1,4,6,2,1,1,12,0,1,1,5,2
Data 4,0,1,1,5,2,6,0,5,2,5,0,1,1,5,2,3,0,5,2,1,3,1,1,1,0,1,1,1,4,6,2,1,1,3,0,1,3,5,2,1,4,2,0,1,1,1,3,6,2,4,0,1,1,5,2,1,4,1,1,1,0,1,1,1,4,4,2,1,3,12,0,1,1,5,2,8,0,1,1,1,4,6,2,1,1,2,0,1,1,5,2,1,4,2,0,1,4
Data 7,2,4,0,1,1,5,2,6,0,1,1,5,2,6,0,1,1,5,2,1,4,1,1,1,0,1,1,1,4,4,2,1,3,2,0,1,1,5,2,12,0,1,0,1,1,18,2,1,3,5,0,1,3,15,2,5,0,9,2,1,0,1,3,15,2,15,0,9,2,3,0,1,3,14,2,1,4,13,0,1,1,4,2,1,3,4,0,1,1,4,2,1,3,5,0
Data 1,1,4,2,1,3,5,0,1,1,4,2,1,3,3,0,1,3,15,2,4,0,1,1,15,2,1,3,5,0,1,3,13,2,1,1,12,0,1,1,20,2,1,4,3,0,1,1,10,2,1,3,4,2,1,3,4,0,1,1,4,2,1,3,6,0,1,1,4,2,1,3,7,0,1,3,13,2,1,1,2,0,1,1,4,2,1,3,12,0,1,0,1,4
Data 17,2,1,4,6,0,1,1,15,2,5,0,8,2,1,3,1,0,1,1,15,2,15,0,8,2,1,3,4,0,13,2,1,4,14,0,1,4,4,2,1,4,4,0,1,4,4,2,1,4,5,0,1,4,4,2,1,4,5,0,1,4,4,2,1,4,3,0,1,1,15,2,5,0,1,3,14,2,1,4,6,0,1,3,11,2,1,1,13,0,1,4,19,2
Data 1,3,5,0,9,2,1,3,1,4,4,2,1,4,4,0,1,4,4,2,1,4,6,0,1,4,4,2,1,4,8,0,1,3,11,2,1,1,3,0,1,4,4,2,1,4,12,0,1,0,1,3,15,2,1,3,1,1,8,0,1,4,8,2,1,4,5,2,5,0,1,4,7,2,1,1,2,0,1,4,8,2,1,4,5,2,15,0,1,4,7,2,1,1,5,0
Data 1,3,10,2,1,1,15,0,1,3,4,2,1,4,4,0,1,3,4,2,1,1,5,0,1,3,4,2,1,4,5,0,1,3,4,2,1,4,4,0,1,4,8,2,1,4,5,2,6,0,1,3,7,2,1,4,1,3,4,2,1,1,7,0,1,3,8,2,1,3,1,1,14,0,1,3,18,2,1,4,6,0,1,4,7,2,1,4,1,0,1,3,4,2,1,1
Data 4,0,1,3,4,2,1,4,6,0,1,3,4,2,1,4,9,0,1,3,8,2,1,3,1,1,4,0,1,3,4,2,1,1,12,0,1,0,12,2,2,3,1,4,1,1,11,0,1,1,1,3,3,2,1,3,1,4,1,1,1,0,5,2,1,1,5,0,1,1,1,3,4,2,1,3,4,0,1,1,1,3,3,2,1,3,1,4,1,1,1,0,5,2,1,1,15,0
Data 1,1,1,3,4,2,1,3,7,0,1,1,1,4,1,3,3,2,2,3,1,4,17,0,5,2,1,1,4,0,5,2,1,1,5,0,5,2,1,1,5,0,5,2,1,1,5,0,1,1,1,3,3,2,1,3,1,4,1,1,1,0,5,2,1,1,6,0,1,4,1,3,3,2,1,3,1,1,1,0,5,2,9,0,1,1,1,3,4,2,1,3,1,4,16,0,15,2
Data 1,3,1,4,1,1,9,0,1,1,1,3,3,2,1,3,1,1,2,0,5,2,5,0,5,2,1,1,6,0,5,2,1,1,10,0,1,1,1,3,4,2,1,3,1,4,6,0,5,2,1,1,12,0,200,0,1,1,4,2,1,3,139,0,189,0,1,2,3,3,1,4,6,0,1,3,4,2,1,4,139,0,189,0,5,2,1,4,1,1,2,0
Data 1,1,1,4,5,2,1,1,139,0,189,0,1,3,14,2,1,3,140,0,189,0,1,1,14,2,1,1,140,0,190,0,1,4,11,2,1,3,1,1,141,0,192,0,1,4,1,3,5,2,2,3,1,1,143,0,345,0

.gradient ;image data for black to yellow gradient - 265x345
Data 265,345,690,255
Data -256,-66048,-131840,-197632,-263424,-329216,-395008,-460800,-526592,-592384,-658176,-723968,-789760,-855552,-921344,-987136,-1052928,-1118720,-1184512,-1250304,-1316096,-1381888,-1447680,-1513472
Data -1579264,-1645056,-1710848,-1776640,-1842432,-1908224,-1974016,-2039808,-2105600,-2171392,-2237184,-2302976,-2368768,-2434560,-2500352,-2566144,-2631936,-2697728,-2763520,-2829312,-2895104,-2960896
Data -3026688,-3092480,-3158272,-3224064,-3289856,-3355648,-3421440,-3487232,-3553024,-3618816,-3684608,-3750400,-3816192,-3881984,-3947776,-4013568,-4079360,-4145152,-4210944,-4276736,-4342528,-4408320
Data -4474112,-4539904,-4605696,-4671488,-4737280,-4803072,-4868864,-4934656,-5000448,-5066240,-5132032,-5197824,-5263616,-5329408,-5395200,-5460992,-5526784,-5592576,-5658368,-5724160,-5789952,-5855744
Data -5921536,-5987328,-6053120,-6118912,-6184704,-6250496,-6316288,-6382080,-6447872,-6513664,-6579456,-6645248,-6711040,-6776832,-6842624,-6908416,-6974208,-7040000,-7105792,-7171584,-7237376,-7303168
Data -7368960,-7434752,-7500544,-7566336,-7632128,-7697920,-7763712,-7829504,-7895296,-7961088,-8026880,-8092672,-8158464,-8224256,-8290048,-8355840,-8421632,-8487424,-8553216,-8619008,-8684800,-8750592
Data -8816384,-8882176,-8947968,-9013760,-9079552,-9145344,-9211136,-9276928,-9342720,-9408512,-9474304,-9540096,-9605888,-9671680,-9737472,-9803264,-9869056,-9934848,-10000640,-10066432,-10132224,-10198016
Data -10263808,-10329600,-10395392,-10461184,-10526976,-10592768,-10658560,-10724352,-10790144,-10855936,-10921728,-10987520,-11053312,-11119104,-11184896,-11250688,-11316480,-11382272,-11448064,-11513856
Data -11579648,-11645440,-11711232,-11777024,-11842816,-11908608,-11974400,-12040192,-12105984,-12171776,-12237568,-12303360,-12369152,-12434944,-12500736,-12566528,-12632320,-12698112,-12763904,-12829696
Data -12895488,-12961280,-13027072,-13092864,-13158656,-13224448,-13290240,-13356032,-13421824,-13487616,-13553408,-13619200,-13684992,-13750784,-13816576,-13882368,-13948160,-14013952,-14079744,-14145536
Data -14211328,-14277120,-14342912,-14408704,-14474496,-14540288,-14606080,-14671872,-14737664,-14803456,-14869248,-14935040,-15000832,-15066624,-15132416,-15198208,-15264000,-15329792,-15395584,-15461376
Data -15527168,-15592960,-15658752,-15724544,-15790336,-15856128,-15921920,-15987712,-16053504,-16119296,-16185088,-16250880,-16316672,-16382464,-16448256,-16514048,-16579840,-16645632,-16711424
Data 265,0,265,1,265,1,265,2,265,3,265,4,265,4,265,5,265,6,265,7,265,7,265,8,265,9,265,10,265,10,265,11,265,12,265,13,265,13,265,14,265,15,265,16,265,16,265,17,265,18,265,18,265,19,265,20,265,21,265,21
Data 265,22,265,23,265,24,265,24,265,25,265,26,265,27,265,27,265,28,265,29,265,30,265,30,265,31,265,32,265,32,265,33,265,34,265,35,265,35,265,36,265,37,265,38,265,38,265,39,265,40,265,41,265,41,265,42
Data 265,43,265,44,265,44,265,45,265,46,265,47,265,47,265,48,265,49,265,49,265,50,265,51,265,52,265,52,265,53,265,54,265,55,265,55,265,56,265,57,265,58,265,58,265,59,265,60,265,61,265,61,265,62,265,63
Data 265,63,265,64,265,65,265,66,265,66,265,67,265,68,265,69,265,69,265,70,265,71,265,72,265,72,265,73,265,74,265,75,265,75,265,76,265,77,265,78,265,78,265,79,265,80,265,80,265,81,265,82,265,83,265,83
Data 265,84,265,85,265,86,265,86,265,87,265,88,265,89,265,89,265,90,265,91,265,92,265,92,265,93,265,94,265,94,265,95,265,96,265,97,265,97,265,98,265,99,265,100,265,100,265,101,265,102,265,103,265,103,265,104
Data 265,105,265,106,265,106,265,107,265,108,265,109,265,109,265,110,265,111,265,111,265,112,265,113,265,114,265,114,265,115,265,116,265,117,265,117,265,118,265,119,265,120,265,120,265,121,265,122,265,123
Data 265,123,265,124,265,125,265,125,265,126,265,127,265,128,265,128,265,129,265,130,265,131,265,131,265,132,265,133,265,134,265,134,265,135,265,136,265,137,265,137,265,138,265,139,265,140,265,140,265,141
Data 265,142,265,142,265,143,265,144,265,145,265,145,265,146,265,147,265,148,265,148,265,149,265,150,265,151,265,151,265,152,265,153,265,154,265,154,265,155,265,156,265,156,265,157,265,158,265,159,265,159
Data 265,160,265,161,265,162,265,162,265,163,265,164,265,165,265,165,265,166,265,167,265,168,265,168,265,169,265,170,265,171,265,171,265,172,265,173,265,173,265,174,265,175,265,176,265,176,265,177,265,178
Data 265,179,265,179,265,180,265,181,265,182,265,182,265,183,265,184,265,185,265,185,265,186,265,187,265,187,265,188,265,189,265,190,265,190,265,191,265,192,265,193,265,193,265,194,265,195,265,196,265,196
Data 265,197,265,198,265,199,265,199,265,200,265,201,265,202,265,202,265,203,265,204,265,204,265,205,265,206,265,207,265,207,265,208,265,209,265,210,265,210,265,211,265,212,265,213,265,213,265,214,265,215
Data 265,216,265,216,265,217,265,218,265,218,265,219,265,220,265,221,265,221,265,222,265,223,265,224,265,224,265,225,265,226,265,227,265,227,265,228,265,229,265,230,265,230,265,231,265,232,265,233,265,233
Data 265,234,265,235,265,235,265,236,265,237,265,238,265,238,265,239,265,240,265,241,265,241,265,242,265,243,265,244,265,244,265,245,265,246,265,247,265,247,265,248,265,249,265,249,265,250,265,251,265,252
Data 265,252,265,253,265,254

End

Function makeData(dataFile$, imgWidth%, imgHeight%)
	;=======================================================
	;approximate length of characters per data statement for
	;palette data and image data
	;longer data lines take up less room overall
	dataLen%  = 200
	;=======================================================
	vCount%   = 0
	pal(0)    = vecs(1)
	clrCount% = 1
	idx%      = 0
    For i% = 1 To imgHeight
		lineLen% = 0
        While lineLen < imgWidth
			vLen% = vecs(vCount)
			vClr% = vecs(vCount + 1)
			lineLen = lineLen + vLen
			;build the list of palette colors in the pal() array
			colorExists% = 0
			For j% = 1 To clrCount
				;if the current color already exists
				;don't look any further, exit the loop
				If pal(j-1) = vClr Then
				    colorExists = 1
					Exit
				End If
			Next
			;if we didn't find an existing color then add to pal() array then
			;increment the number of colors in the image and add to pal() array
			If colorExists = 0 Then
				idx = idx + 1
				clrCount = clrCount + 1
				pal(idx) = vClr
			End If
			colorExists = 0
			;increment the vector counter			
            vCount = vCount+2
        Wend
    Next
    hiCount% = vCount ; + 2 ;extra two elements, just in case ;)
	fileOut% = WriteFile(dataFile$)
    ;Save the width and height of the image followed by largest number of
	;vectors in a raster line in the image and the number of unique
	;colors to dimension vec() array and pal() array in main program
	dat$ = "Data "+Str(imgWidth)+","+Str(imgHeight)+","+Str(hiCount)+","+Str(clrCount)
    WriteLine(fileOut, dat$)
	;Now write the palette table into the text data file
	dat$ = "Data "
	For idx = 0 To clrCount-1
		dat$ = dat$ + Str(pal(idx))+","
		If Len(dat$) > dataLen Then
			dat$= Left$(dat$,Len(dat$)-1)
			WriteLine(fileOut, dat$)
			dat$ = "Data "
		End If
	Next
	If Right$(dat$,1) = "," Then
		dat$ = Left$(dat$, Len(dat$)-1)
	    WriteLine(fileOut, dat$)
	Else
		If dat$ <> "Data " Then WriteLine(fileOut, dat$)
	End If

	;All set to go, let's start writing data to disk!
    dat$ = "Data "
	vCount = 0
	For i = 1 To imgHeight
		lineLen = 0
		While lineLen < imgWidth
			lineLen = lineLen + vecs(vCount)
			For j% = 0 To clrCount-1
				If vecs(vCount + 1) = pal(j) Then
					idx = j
					Exit
				End If
			Next
			dat$ = dat$ + Str(vecs(vCount))+","+Str(idx)+","
			If Len(dat$) > dataLen Then
				dat$ = Left$(dat$,Len(dat$)-1)
				WriteLine(fileOut, dat$)
				dat$ = "Data "
			End If
			vCount = vCount + 2
		Wend
    Next
	If dat$ <> "Data " Then
		If Right$(dat$,1) = "," Then
			dat$ = Left$(dat$,Len(dat$)-1)
		    WriteLine(fileOut, dat$)
		Else
		    WriteLine(fileOut, dat$)
		End If
	End If
    CloseFile fileOut
End Function

Function getImgData%()
    Read imgWidth
    Read imgHeight
	Read numVecs
	Read palSize
    Dim vecs(numVecs)
	Dim pal(palSize-1)
	For i = 0 To palSize-1
		Read pal(i)
	Next
	ras = 1
	lineLen = 0
	vCount = 0
	While ras <= imgHeight 
		Read vecLen
		Read idx
		vecs(vCount) = vecLen
		vecs(vCount+1) = pal(idx)
		lineLen = lineLen + vecLen
		vCount = vCount + 2
		If lineLen >= imgWidth Then
			lineLen = 0
			ras = ras + 1
		End If
	Wend
End Function

Function data2buffer%(displayAngle%)
	If displayAngle = 0 Or displayAngle = 90 Or displayAngle = 180 Or displayAngle = 270 Then
		Read imgWidth%
	    Read imgHeight%
		Read numVecs%
		Read palSize%
	    Dim vecs(numVecs)
		Dim pal(palSize-1)
		For i% = 0 To palSize-1
			Read pal(i)
		Next
		ras% = 1
		vCount% = 0
		lineLen% = 0
		While ras <= imgHeight 
			Read vecLen%
			Read idx%
			vecs(vCount) = vecLen
			vecs(vCount+1) = pal(idx)
			lineLen = lineLen + vecLen
			If lineLen = imgWidth Then
				lineLen = 0
				ras = ras + 1
			End If
			vCount = vCount + 2
		Wend
		Select displayAngle
			Case   0
				newWidth%  = imgWidth
				newHeight% = imgHeight
				x% = 0
				y% = 0
			Case  90
				newWidth%  = imgHeight
				newHeight% = imgWidth
			 	x% = newWidth-1
				y% = 0
			Case 180
				newWidth%  = imgWidth
				newHeight% = imgHeight
				x% = imgWidth-1
				y% = imgHeight-1
			Case 270
				newWidth%  = imgHeight
				newHeight% = imgWidth
				x% = 0
				y% = newHeight-1
		End Select
		imgTemp% = CreateImage(newWidth, newHeight)
		SetBuffer ImageBuffer(imgTemp)
        angle2% = displayAngle + 90
		vCount = 0
        For i = 1 To imgHeight
            p# = Cos(angle2)*(i-1)+x
            q# = Sin(angle2)*(i-1)+y
			lineLen = 0
            While lineLen < imgWidth
			    vecLen% = vecs(vCount)
				lineLen = lineLen + vecLen
	            red = (vecs(vCount + 1) And maskRed) Shr 16
     	    	grn = (vecs(vCount + 1) And maskGrn) Shr 8
	            blu =  vecs(vCount + 1) And maskBlu
                Color red,grn,blu
			    r# = Cos(displayAngle)*vecLen+p
			    s# = Sin(displayAngle)*vecLen+q
			    Line p,q,r,s
                vCount = vCount+2
			    p=r
			    q=s
            Wend
        Next
	    SetBuffer BackBuffer()
		Return imgTemp
	Else 
		Return 0
	End If
End Function

Function dat2curv2buf(radius%, startAngle#, arcSegment#, penSize%, bkR%, bkG%, bkB%)
	Read imgWidth%
    Read imgHeight%
	Read numVecs%
	Read palSize%
    Dim vecs(numVecs)
	Dim pal(palSize-1)
	For i% = 0 To palSize-1
		Read pal(i)
	Next
	ras% = 1
	vCount% = 0
	lineLen% = 0
	While ras <= imgHeight 
		Read vecLen%
		Read idx%
		vecs(vCount) = vecLen
		vecs(vCount+1) = pal(idx)
		lineLen = lineLen + vecLen
		If lineLen = imgWidth Then
			lineLen = 0
			ras = ras + 1
		End If
		vCount = vCount + 2
	Wend
	;Create a Blitz image buffer
	diameter% = radius Shl 1
	imgTemp% = CreateImage(diameter%, diameter%)
	SetBuffer ImageBuffer(imgTemp)
	;Fill the background with desired color
	Color bkR, bkG, bkB
	Rect(0,0,diameter, diameter, True)
	;Start drawing the curved image on the image buffer
	If penSize > 1 Then
		penOffset# = penSize Shr 1
	End If
	stepSize# = arcSegment/imgWidth
	vCount = 0
	centerX% = radius
	centerY% = radius
	For i% = 1 To imgHeight
		lineLen = 0
		arc# = startAngle
		While lineLen < imgWidth
			arcLen = vecs(vCount)
			lineLen = lineLen + arcLen
			red = (vecs(vCount + 1) And maskRed) Shr 16
			grn = (vecs(vCount + 1) And maskGrn) Shr 8
			blu =  vecs(vCount + 1) And maskBlu
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
	SetBuffer BackBuffer()
	Return imgTemp	
End Function
