; ID: 1400
; Author: SillyPutty
; Date: 2005-06-14 06:24:25
; Title: Listing Available Graphics Modes
; Description: This will retrieve all graphics modes compatible with your vide card

Strict

setgraphicsdriver glmax2ddriver()

Graphics 640,480,0

Global width
Global height
Global depth
Global hertz

Global n = CountGraphicsModes()

While Not KeyDown(KEY_ESCAPE)
SetOrigin 10,10

DrawText "Graphics modes available",10,10

SetOrigin 10,40
For Local x# = 0 To n-1
GetGraphicsMode(x,width,height,depth,hertz)
DrawText "Mode: Width: " + width + " Height: " + height + " Depth: " + depth + " Hertz: " + hertz,10,10*x
Next

Flip
Cls
Wend
