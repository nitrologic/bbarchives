; ID: 1348
; Author: ImaginaryHuman
; Date: 2005-04-10 13:51:28
; Title: Fast 2D Blobby Objects/Metaballs in Max2D with OpenGL extras
; Description: Draws 2D metaballs/blobby objects and then manipulates the image with OpenGL calls to produce a `band` around the objects

'Blobby objects with BlitzMax using Max2D and some direct OpenGL

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

'Set the blend mode
SetBlend LIGHTBLEND

'Keep drawing the image until you press Escape
Repeat
	Cls
	glEnable(GL_BLEND)
	glBlendFunc(GL_SRC_ALPHA,GL_ONE)
	DrawImage img,400,300
	DrawImage img,MouseX(),MouseY()
	glBlendFunc(GL_SRC_ALPHA,GL_ONE)
	glColor4b($40,$40,$40,$40)
	DrawRect 0,0,800,600
	glBlendFunc(GL_SRC_ALPHA_SATURATE,GL_DST_COLOR)
	glColor4b(0,0,0,$FF)
	Local Counter:Int
	For Counter:Int=1 To 3
		DrawRect 0,0,800,600
	Next
	glBlendFunc(GL_ONE_MINUS_DST_COLOR,GL_ONE_MINUS_DST_COLOR)
	glColor4b($0,$0,$0,$0)
	DrawRect 0,0,800,600
	glBlendFunc(GL_DST_COLOR,GL_DST_COLOR)
	glColor4b($FF,$FF,$FF,$FF)
                SetColor $FF,$FF,$FF
	For Counter:Int=1 To 3
		DrawRect 0,0,800,600
	Next
	glDisable(GL_BLEND)
	Flip
Until KeyHit(KEY_ESCAPE)
