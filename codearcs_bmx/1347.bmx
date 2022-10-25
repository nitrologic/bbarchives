; ID: 1347
; Author: ImaginaryHuman
; Date: 2005-04-10 13:47:23
; Title: Fast 2D Blobby Objects/Metaballs in Max2D
; Description: How to draw blobby objects in 2D using just Max2D commands, and do it fast!

'Blobby objects with BlitzMax using Max2D only

'Some special numbers
Local ballsize:Int=512
Local ballsizehalf:Int=ballsize/2

'Set up the display
Graphics 800,600,0
Cls

'Work out what the dividers needs to be
Local balldivider:Float
If ballsize=128 Then balldivider=64 '8x8
If ballsize=256 Then balldivider=256 '16x16
If ballsize=512 Then balldivider=1024 '32x32
Local lineardivider:Float
If ballsize=128 Then lineardivider=0.5
If ballsize=256 Then lineardivider=1
If ballsize=512 Then lineardivider=2

'Render the gradient image
For Local r:Float=1 To ballsize-1 Step 0.5
	Local level:Float=r
	level:*level
	level=level/balldivider
	SetColor level,level,level 'For blobby gradient shape
	'SetColor r/lineardivider,r/lineardivider,r/lineardivider 'For linear gradients
	DrawOval r/2,r/2,ballsize-r,ballsize-r
Next

'Turn it into an image
AutoMidHandle True
Local img:TImage=CreateImage(ballsize,ballsize,1,FILTEREDIMAGE)
GrabImage(img,0,0,0)

'Set the drawing mode
SetBlend LIGHTBLEND

'Keep drawing the image until you press Escape
Repeat
	Cls
	DrawImage img,400,300
	DrawImage img,MouseX(),MouseY()
	Flip
Until KeyHit(KEY_ESCAPE)
