; ID: 102
; Author: skidracer
; Date: 2001-10-14 18:49:30
; Title: primitives2
; Description: update of samples/mak/primitives.bb with FPS and Poly Count

Function CountPoly(mesh)
	For i=1 To CountSurfaces(mesh)
		n=n+CountTriangles(GetSurface(mesh,i))
	Next
	Return n
End Function


Global info1$="Primitives demo"

Include "../start.bb"

tex=CreateTexture( 64,64 )
ScaleTexture tex,.125,.125
SetBuffer TextureBuffer( tex )
Color 64,192,255:Rect 32,0,32,32:Rect 0,32,32,32
Color 255,255,255:Rect 0,0,32,32:Rect 32,32,32,32
SetBuffer BackBuffer()
Color 255,255,255

cam=CreateCamera()
PositionEntity cam,0,0,-6

light=CreateLight()
TurnEntity light,45,45,0

segs=16
rebuild=True

brush=CreateBrush()
BrushTexture brush,tex

pivot=CreatePivot()

clock=MilliSecs():frames=0

While Not KeyHit(1)

	If KeyHit(17)
		wire=Not wire
		WireFrame wire
	EndIf
	If KeyHit(26)
		If segs>3 Then segs=segs-1:rebuild=True
	Else If KeyHit(27)
		If segs<100 Then segs=segs+1:rebuild=True
	EndIf
	
	If rebuild
		If cube FreeEntity cube
		If sphere FreeEntity sphere
		If cylinder FreeEntity cylinder
		If cone FreeEntity cone
		cube=CreateCube( pivot )
		PaintEntity cube,brush
		PositionEntity cube,-3,0,0
		cylinder=CreateCylinder( segs,True,pivot )
		PaintEntity cylinder,brush
		PositionEntity cylinder,1,0,0
		cone=CreateCone( segs,True,pivot )
		PaintEntity cone,brush
		PositionEntity cone,-1,0,0
		sphere=CreateSphere( segs,pivot )
		PaintEntity sphere,brush
		PositionEntity sphere,3,0,0
		rebuild=False

		polycount=CountPoly(cube)+CountPoly(cone)+CountPoly(cylinder)+CountPoly(sphere)
		clock=MilliSecs():frames=0
	EndIf
	
	If KeyDown(203) TurnEntity pivot,0,-3,0
	If KeyDown(205) TurnEntity pivot,0,+3,0
	If KeyDown(200) TurnEntity pivot,-3,0,0
	If KeyDown(208) TurnEntity pivot,+3,0,0
	If KeyDown(30) TranslateEntity pivot,0,0,-.2
	If KeyDown(44) TranslateEntity pivot,0,0,+.2

	UpdateWorld
	RenderWorld
	frames=frames+1:fps#=1000*frames/(MilliSecs()+1-clock)
	Text 0,0,"Segs="+segs+" Polycount="+polycount+" FPS="+fps+" - [] To adjust, 'W' For WireFrame"
	Flip	
Wend
End
