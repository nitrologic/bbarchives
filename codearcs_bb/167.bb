; ID: 167
; Author: Equinox
; Date: 2001-12-26 06:12:53
; Title: Rubber
; Description: Rubber Effect

Frames=48 ;should divide exactly into screen height
ScreenWidth=640
ScreenHeight=480

Graphics3D ScreenWidth,ScreenHeight,16,1

tex=CreateTexture( 64,64 )
SetBuffer TextureBuffer( tex )
Color 255,0,0:Rect 0,0,32,32:Rect 32,32,32,32
Color 255,128,0:Rect 32,0,32,32:Rect 0,32,32,32
SetBuffer BackBuffer()
Color 255,255,255

cone=CreateCone(20)
PositionEntity cone,0,0,0
EntityTexture cone,tex

light=CreateLight()
TurnEntity light,45,45,0

pivot=CreatePivot()
camMain=CreateCamera(pivot)
PositionEntity camMain,0,0,-3


;Initialse Frame Buffers
Dim imgImages(Frames-1)

For Loop1=0 To Frames-1
	imgImages(Loop1)=CreateImage(ScreenWidth,ScreenHeight)
Next 
ImgCount=0

While Not KeyHit(1)

	;Rotate Our Object
	X=X+1.0
	Y=Y+0.5
	z=z+0.8
	If x>=360 Then x=x-360
	If y>=360 Then y=y-360
	If z>=360 Then z=z-360
	RotateEntity cone,x,y,z

	;Update & Render The World
	UpdateWorld
	RenderWorld
	
	;Generate Next Frame Buffer Pos
	ImgCount=ImgCount+1	
	If ImgCount>Frames-1 Then ImgCount=0
	
	;Store Frame To Buffer
	SetBuffer ImageBuffer(imgImages(ImgCount))
	CopyRect 0,0,ScreenWidth,ScreenHeight,0,0,BackBuffer(),ImageBuffer(imgImages(ImgCount))
	SetBuffer BackBuffer()
	
	;Render Each Section Of Screen Downwards As Previous Image Than One Above
	ImgPos=ImgCount
	For Loop1=0 To Frames-1
		CopyRect 0,(ScreenHeight/Frames)*Loop1,ScreenWidth,(ScreenHeight/Frames),0,(ScreenHeight/Frames)*Loop1,ImageBuffer(imgImages(ImgPos)),BackBuffer()
		ImgPos=ImgPos-1
		If ImgPos<0 Then ImgPos=Frames-1
	Next

	Flip

Wend

End
