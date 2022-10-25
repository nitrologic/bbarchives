; ID: 1776
; Author: IKG
; Date: 2006-08-04 17:37:30
; Title: Simple Lightning for Max
; Description: Created by plotting pixels..

'Written by David Schwartz - http://www.devdave.net
Graphics 640,480,0
SetColor 255,255,255

Global newpixelx = 0
Global pixelx = 240
Global pixely = 0
Global pixelmove = 0

Repeat 

If KeyHit(key_space) Then newpixelx = Rand(0,640)

CreateBolt()

Flip;Cls

Until KeyHit(key_escape)

Function CreateBolt()
pixelx = newpixelx
pixely = 0
	For i=1 To 480
		pixelmove = Rand(-1,1)
		pixelx = pixelx - pixelmove	
		pixelmove = Rand(-1,1)
		pixelx = pixelx + pixelmove
		pixely = pixely + 1
		Plot pixelx,pixely
	Next
End Function
