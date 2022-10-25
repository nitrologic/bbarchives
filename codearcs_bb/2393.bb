; ID: 2393
; Author: Krischan
; Date: 2009-01-07 15:47:21
; Title: Spacegame tech demo
; Description: Spacegame tech demo

AppTitle "Planet Atmosphere Glow Demo"
; by Krischan webmaster(at)jaas.de

; Constants
Const ScreenWidth%			= 800			; Screen width
Const ScreenHeight%			= 600			; Screen height
Const ColorDepth%			= 32			; Color depth
Const ScreenMode%			= 2				; Screen Mode
Const MouseSpeed#			= 0.1			; Mousespeed
Const CameraSmoothness#		= 10			; Smoothness of movement
Const Scale#				= 12.756		; Approx. the diameter of earth (1 unit = 1000km)
Const GameTime%				= 20			; Game speed
Const CursorSize%			= 33			; Size of cursor and crosshair image
Const TurnSpeed#			= 2.0			; Turnspeed of player
Const RollSpeed#			= 1.0			; Roll speed
Const MaxRollAngle#			= 20.0			; Maximum roll angle
Const RingDetail%			= 60			; Number of segments

; Glow Color scheme
Const R1%=192,G1%=224,B1%=255				; Surface near color
Const R2%=128,G2%=160,B2%=255				; Surface far color

; Variables
Global CameraSpeed#			= Scale/10.0	; Movement speed
Global TargetDistance#		= 100.0*Scale	; Mousetarget distance to player
Global FrameTime			= MilliSecs()	; Initialize Frame Timer
Global Period%				= 1000/75.0		; Calc frame period (here: 75FPS)

; help variables and objects
Global MX%,MY%								; Mouse position
Global Player%,MouseTarget%,Cam%,Ship%		; Player
Global Cursor%,Cross%						; Images
Global Planet%,Light%,Sun%,StarBox%			; Scene objects
Global GlowPivot%							; Pivot of Glow
Global Glow1%,Glow2%						; Glow rings
Global GlowScale#							; Glow scale indicator
Global Tween#								; Frame tween
Global MoveSpeed#							; movespeed in km/h

; Init 3D
Graphics3D ScreenWidth,ScreenHeight,ColorDepth,ScreenMode

; Init Player and Scene
InitPlayer()
InitScene()

; Center mouse and hide pointer
MoveMouse ScreenWidth/2,ScreenHeight/2
HidePointer

; Main loop
While Not KeyHit(1)
	
	; Frame tweening
	Tween#=Float(MilliSecs()-FrameTime)/Float(GameTime) : FrameTime=MilliSecs()
	
	; SPACE = Wireframe
	If KeyHit(57) Then wf%=1-wf : WireFrame wf
	
	; Get Distance player to planet
	Local distance#=EntityDistance(Cam,Planet)
	
	; Update atmosphere glow
	UpdateAtmosphere(distance)
	
	; Attach Starbox to camera
	PositionEntity StarBox,EntityX(Player),EntityY(Player),EntityZ(Player)
	
	;Sun points To Player
	PointEntity Sun,Player
	
	UpdateWorld
	RenderWorld
	
	;Move Player
	Movement(GlowScale,distance)
	
	; Draw cursor and crosshair by chasing the mousetarget
	CameraProject Cam,EntityX(MouseTarget,1),EntityY(MouseTarget,1),EntityZ(MouseTarget,1)
	DrawImage Cursor,MX-(CursorSize/2),MY-(CursorSize/2)
	DrawImage Cross,ProjectedX()-(CursorSize/2),ProjectedY()-(CursorSize/2)
	
	; Statistics
	Text 0, 0,"Triangles rendered....: "+TrisRendered()
	Text 0,15,"Distance To Surface...: "+Int((distance-Scale)*1000)+" km"
	Text 0,30,"Move speed............: "+MoveSpeed+" km/h"
	
	Flip 0
	
Wend

End

; The functions that creates the mesh and surface for a ring
Function CreateRing(fx%=0,blend%=0)
	
	Local a1#,a2#,a3#,a4#,angle%
	Local v0%,v1%,v2%,v3%
	
	Local mesh=CreateMesh()
	Local surf=CreateSurface(mesh)
	
	; Ring FX
	If fx>0 Then EntityFX mesh,fx
	If blend>0 Then EntityBlend mesh,blend
	
	Return mesh
	
End Function

; Re-creates the ring vertices and triangles with different values
Function UpdateRing(mesh%,radius1#=1.0,radius2#=2.0,segments%=360,r1%=255,g1%=255,b1%=255,alpha1#=1.0,r2%=0,g2%=0,b2%=0,alpha2#=1.0,scale#=0.0)
	
	Local a1#,a2#,angle%
	Local v0%,v1%,v2%,v3%,v%
	
	; get and clear the surface
	Local surf=GetSurface(mesh,1)
	ClearSurface surf,1,1
	
	; Limit segments
	If segments>360 Then segments=360
	
	; Create ring
	For angle=0 To segments
		
		a1=angle*360.0/segments
		a2=angle*360.0/segments+180.0/segments
		
		; Calc vertex points
		v0=AddVertex(surf,radius1*Cos(a1),radius1*Sin(a1),0,0,0)
		v1=AddVertex(surf,radius2*Cos(a2),radius2*Sin(a2),scale,1,1)
		
		; Color
		VertexColor surf,v0,r1,g1,b1,alpha1
		VertexColor surf,v1,r2,g2,b2,alpha2
		
	Next
	
	; Create Triangles
	For v=0 To CountVertices(surf)-3
		
		AddTriangle(surf,v,v+1,v+2)
		
	Next
	
	Return mesh
	
End Function

; Initialize player, camera
Function InitPlayer()
	
	Local i%
	
	; Player pivot
	Player=CreatePivot()
	PositionEntity Player,-Scale*2,0,Scale*2
	
	; Ship mesh
	Ship=CreateCube(Player)
	EntityFX Ship,1
	ScaleEntity Ship,0.2,0.05,0.2
	PositionEntity Ship,0,-0.5,1.5
	EntityOrder Ship,-1000
	
	; Mousetarget in space
	MouseTarget=CreatePivot(Ship)
	MoveEntity MouseTarget,0,0,TargetDistance
	
	; Camera
	Cam=CreateCamera(Player)
	PositionEntity Cam,0,0,0
	CameraRange Cam,0.01,1000*Scale
	
	; Create cursor image
	Cursor=CreateImage(CursorSize,CursorSize)
	SetBuffer ImageBuffer(Cursor)
	For i=0 To 2
		Color 0,Int(255.0/(1+i)),0
		Rect i,i,CursorSize-(2*i),CursorSize-(2*i),0
	Next
	
	; Create crosshair image
	Cross=CreateImage(CursorSize,CursorSize)
	SetBuffer ImageBuffer(Cross)
	Color 255,0,0
	Line (CursorSize-1)/2.0,0,(CursorSize-1)/2.0,CursorSize
	Line 0,(CursorSize-1)/2.0,CursorSize,(CursorSize-1)/2.0
	
	; reset buffer and color
	SetBuffer BackBuffer()
	Color 255,255,255
	
End Function

; Initialize scene
Function InitScene()
	
	Local startex%,i%,col%,rgb%
	
	; Planet
	Planet=CreateSphere(60)
	ScaleEntity Planet,Scale,Scale,Scale
	EntityColor Planet,32,192,64
	
	; Directional Sunlight
	Light=CreateLight(1)
	PositionEntity Light,0,0,-Scale*200
	LightRange Light,200*Scale
	AmbientLight 16,16,16
	
	; Sun
	Sun=CreateQuad()
	ScaleEntity Sun,Scale*10,Scale*10,Scale*10
	EntityFX Sun,1
	EntityColor Sun,255,255,192
	EntityParent Sun,Light
	PositionEntity Sun,0,0,0
	EntityBlend Sun,3
	EntityTexture Sun,CreateSunTexture()
	
	; Simple Starbox
	StarBox=CreateCube()
	startex=CreateTexture(1024,1024)
	LockBuffer TextureBuffer(startex)
	For i=1 To 1000
		col=Rand(0,255)
		rgb=col*$10000+col*$100+col
		WritePixelFast Rand(0,1023),Rand(0,1023),rgb,TextureBuffer(startex)
	Next
	UnlockBuffer TextureBuffer(startex)
	EntityTexture StarBox,startex
	ScaleEntity StarBox,10,10,10
	EntityOrder StarBox,1
	EntityFX StarBox,1
	FlipMesh StarBox
	
	; Glow
	GlowPivot=CreatePivot()
	Glow1=CreateRing(1+2+16+32,1)
	Glow2=CreateRing(1+2+16+32,3)
	EntityParent Glow1,GlowPivot
	EntityParent Glow2,GlowPivot
	EntityOrder Glow1,1
	EntityOrder Glow2,1
	
	; Player points to planet first
	PointEntity Player,Planet
	TurnEntity Player,0,-20,-90
	
End Function

; Update atmosphere glow and background color
Function UpdateAtmosphere(distance#)
	
	Local s2#,s2d#,aa#,bb#,cc#
	Local angle#,intensity#,clscol#,horizon#
	
	; Calculate ring scale with the help of two right-angled triangles and trigonometry
	s2#=Scale^2
	s2d#=s2/distance
	GlowScale#=(Sqr(s2+(Scale/Tan(90-(90-(90-ATan(Sqr((distance-(s2d))*(s2d))/(distance-(s2d)))))))^2))/Scale
	
	; Calculcate the sun light angle (1 = exactly between sun and planet, 0 = exactly behind the planet)
	aa#=EntityDistance(Cam,Planet)
	bb#=EntityDistance(Planet,Light)
	cc#=EntityDistance(Cam,Light)
	angle#=ACos((aa^2+cc^2-bb^2)/(2*aa*cc))/180.0
	If angle>1 Then angle=1
	If angle<0 Then angle=0
	
	; Calculate the glow intensity according to sun light angle and distance to planet
	intensity#=1-(1.0/Exp(GlowScale*angle))
	If intensity<0 Then intensity=0
	If intensity>1 Then intensity=1
	
	; Calculate the horizon glow scale multiplicator
	horizon#=1-(1.2/Exp(GlowScale*angle/2.0))
	If horizon<0 Then horizon=0
	If horizon>1 Then horizon=1
	
	; Update ring intensity according to sun light angle and distance to planet
	UpdateRing(Glow1,0.6*Scale,0.01*Scale,RingDetail,R1*intensity,G1*intensity,B1*intensity,angle,R2*(angle-intensity),G2*(angle-intensity),B2*(angle-intensity),   0,Scale/3.0)
	UpdateRing(Glow2,(1.0-(horizon/10.0))*Scale,(1.05+(horizon/10.0))*Scale,RingDetail,R1*intensity,G1*intensity,B1*intensity,1.0-horizon,R2*angle,G2*angle,B2*angle,0,horizon*2)
	
	; Scale the rings and always point to player
	ScaleEntity GlowPivot,GlowScale,GlowScale,GlowScale
	PointEntity GlowPivot,Player
	
	; Calculate the background color to simulate atmosphere penetration
	clscol#=(1-(5.0/Exp(GlowScale*angle/2.0)))*intensity
	If clscol<0 Then clscol=0
	If clscol>1 Then clscol=1
	CameraClsColor Cam,R2*clscol,G2*clscol,B2*clscol
	
	; Change Starbox alpha
	EntityAlpha StarBox,1-clscol
	
End Function

; Player movement
Function Movement(scale#,distance#)
	
	Local roll#,mox#,moz#,cx#,cz#
	Local t1#,t2#
	
	; get mouse position
	MX=MouseX()
	MY=MouseY()
	
	; Movement with speed limit
	cx=(KeyDown(205)-KeyDown(203))*CameraSpeed
	cz=(KeyDown(200)-KeyDown(208))*CameraSpeed
	
	; Arrow left/right = roll
	If KeyDown(203) Then roll=RollSpeed
	If KeyDown(205) Then roll=-RollSpeed
	
	; Normalize Mouse position (-1 to +1)
	t1=Normalize(MY,0,ScreenHeight,-1,1)
	t2=Normalize(MX,0,ScreenWidth,1,-1)
	
	; Slower cursor movement in the center of the screen
	;If t1<0 Then t1=(Abs(t1)^2.0)*-1 Else t1=t1^2.0
	;If t2<0 Then t2=(Abs(t2)^2.0)*-1 Else t2=t2^2.0
	
	; Rotate ship mesh and turn player pivot
	RotateEntity Ship,t1*MaxRollAngle,t2*MaxRollAngle,t2*MaxRollAngle*2
	TurnEntity Player,t1*TurnSpeed*Tween,t2*TurnSpeed*Tween,roll*TurnSpeed*Tween
	
	; Move the player forward/backward
	MoveEntity Player,0,0,(cz*1.0/(scale)^3)*Tween
	
	; Calculate actual movespeed
	MoveSpeed#=(cz*1.0/(scale)^3)*3600000
	
End Function

; Normalize a value
Function Normalize#(value#=128.0,value_min#=0.0,value_max#=255.0,norm_min#=0.0,norm_max#=1.0)
	
	Return ((value-value_min)/(value_max-value_min))*(norm_max-norm_min)+norm_min
	
End Function

Function CreateQuad()
	
	; Create mesh and surface
	Local mesh%=CreateMesh()
	Local surf%=CreateSurface(mesh)
	
	; Add vertices
	Local v0%=AddVertex(surf,  1.0,  1.0, 0.0, 0.0, 0.0 )	; upper left
	Local v1%=AddVertex(surf, -1.0,  1.0, 0.0, 1.0, 0.0 )	; upper right
	Local v2%=AddVertex(surf, -1.0, -1.0, 0.0, 1.0, 1.0 )	; lower right
	Local v3%=AddVertex(surf,  1.0, -1.0, 0.0, 0.0, 1.0 )	; lower left
	
	; Connect vertices
	AddTriangle surf,v0,v1,v2
	AddTriangle surf,v0,v2,v3
	
	Return mesh
	
End Function

; Create a simple sun texture
Function CreateSunTexture()
	
	Local tex%=CreateTexture(512,512,2)
	Local tb%=TextureBuffer(tex)
	
	Local i#,j%,col%,rgb%
	
	SetBuffer tb
	LockBuffer tb
	
	; Intensity steps
	For j=0 To 255
		
		; Exponential falloff
		col=Int((1.0/Exp(j*0.075))*100000000)
		If col>255 Then col=255
		rgb=col*$1000000+col*$10000+col*$100+col
		
		; Draw circles
		For i=0 To 360 Step 0.1
			WritePixelFast 256+(Sin(i)*j),256+(Cos(i)*j),rgb,tb
		Next
		
	Next
	
	UnlockBuffer tb
	SetBuffer BackBuffer()
	
	Return tex
	
End Function
