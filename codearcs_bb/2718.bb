; ID: 2718
; Author: GIB3D
; Date: 2010-05-19 15:57:03
; Title: Player Movement: Round vs Square
; Description: Something about game controls that some people might not know

;The problem with using Square controls is that players can
;give themselves extra unintended speed

;For example let's say you make a moat between land and a castle...
;the player couldn't normally jump over it by pressing just forward.
;But if the player holds forward+right they might be able to jump the gap.

Graphics3D 800,600,32,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()

AmbientLight 20,20,20
Global Light = CreateLight():RotateEntity Light,65,0,0

Global Camera = CreateCamera()
	PositionEntity Camera,0,50,0
	CameraProjMode Camera,2
	CameraZoom Camera,.1

Global Cube = CreateCube()
	PointEntity Camera,Cube

Local Key[1],MoveX#,MoveY#,Angle#
Local Toggle

;[Block] Used to calculate where to put the 2D stuff on the screen
Local X,Y,Width,Height
CameraProject Camera,-1,0,1
X = ProjectedX()
Y = ProjectedY()
CameraProject Camera,1,0,-1
Width = ProjectedX()-X
Height = ProjectedY()-Y
;[End]

While Not KeyDown(1)
	If KeyHit(57) Toggle = Not Toggle
	
	Key[0] = KeyDown(30)-KeyDown(32) ; X
	Key[1] = KeyDown(31)-KeyDown(17) ; Y
	
	Select Toggle
		Case True ; Round Controls
			
			Angle = VectorYaw(Key[0],0,Key[1])
			
			If Key[0] Or Key[1]
				MoveX = AngleX(Angle)
				MoveY = AngleY(Angle)
			Else
				MoveX=0
				MoveY=0
			EndIf
			
			PositionEntity Cube,MoveX,0,MoveY
			
		Case False ; Square Controls
			
			PositionEntity Cube,-Key[0],0,-Key[1]
			
	End Select
	
	UpdateWorld
	RenderWorld
	
	Color 255,255,255
	Text GraphicsWidth()*.5,0,"Controls: WASD and Space to toggle input",1
	
	Select Toggle
		Case True ; Round Controls
			Color 255,0,0
			Oval X,Y,Width,Height,0
			
			Color 255,255,255
			Text GraphicsWidth()*.5,20,"These are more circular controls",1
			Text GraphicsWidth()*.5,ProjectedY()-160,"X("+AngleX(Angle)+")",1
			Text GraphicsWidth()*.5,ProjectedY()-140,"Y("+AngleY(Angle)+")",1
		Case False ; Square Controls
			Color 255,0,0
			Rect X,Y,Width,Height,0
			
			Color 255,255,255
			Text GraphicsWidth()*.5,20,"These are the basic kind of controls mostly used by First Person Shooter games",1
			Text GraphicsWidth()*.5,ProjectedY()-160,"X("+Key[0]+")",1
			Text GraphicsWidth()*.5,ProjectedY()-140,"Y("+Key[1]+")",1
	End Select
	
	Flip
	
Wend
End

Function AngleX#(angle#)
	Return Cos(angle-90)
End Function

Function AngleY#(angle#)
	Return Sin(angle-90)
End Function
