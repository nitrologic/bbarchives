; ID: 1152
; Author: Nilium
; Date: 2004-08-31 23:55:28
; Title: Simple 2D in 3D Functions
; Description: Various functions useful for doing operations that require 2d in 3d

Type t2dI3d
	Field X#,Y#,Z#,W#,H#,Camera%
End Type

Global X3D#,Y3D#,Z3D#

Function Init2DIn3D(Camera,Z#=4,ResW=0,ResH=0)
	If ResW <= 0 Then ResW = GraphicsWidth()
	If ResH <= 0 Then ResH = GraphicsHeight()
	P = CreatePlane(1)
	EntityPickMode P,2
	PositionEntity P,EntityX(Camera,1),EntityY(Camera,1),EntityZ(Camera,1),1
	RotateEntity P,EntityPitch(Camera,1),EntityYaw(Camera,1),EntityRoll(Camera,1),1
	MoveEntity P,0,0,Z#
	TurnEntity P,-90,0,0
	CameraPick Camera,0,0
	While PickedEntity() <> P And Z# > 1.0
		Z# = Z# - .1
		MoveEntity P,0,.1,0
		CameraPick Camera,0,0
	Wend
	If Z < 1.0 Or PickedEntity() <> P Then RuntimeError "Unable to initialize 2d in 3d module"
	TFormPoint PickedX(),PickedY(),Z,0,Camera
	X# = Abs TFormedX()
	Y# = Abs TFormedY()
	i.t2dI3d = New t2dI3d
	i\X = X : i\Y = Y : i\Z = Z : i\W = ResW : i\H = ResH : i\Camera = Camera
	FreeEntity P
End Function


Function To3D(X#,Y#,TFormGlobal = 0,TFormTo=0)
	i.t2dI3d = First t2dI3d
	X# = -i\X + ((X# / i\W) * (i\X * 2))
	Y# = i\Y - ((Y# / i\H) * (i\Y * 2))
	If TFormGlobal = 0 Then
		TFormPoint X,Y,i\Z,i\Camera,TFormTo
		X3D = TFormedX() : Y3D = TFormedY() : Z3D = TFormedZ()
		Return
	EndIf
	
	X3D = X : Y3D = Y : Z3D = i\Z
	Return
End Function


Function CreateQuad(X#,Y#,Width#,Height#,CenterX=0,CenterY=0)
	If CenterX >= 1 Then : CenterX = 1 : Else : CenterX = 0 : EndIf
	If CenterY >= 1 Then : CenterY = 1 : Else : CenterY = 0 : EndIf
	X = X - Width * (CenterX*.5)
	Y = Y - Height * (CenterY*.5)
	
	i.t2dI3D = First t2dI3d
	M = CreateMesh()
	S = CreateSurface(M)
	To3D(X,Y)
	TFormPoint X3D,Y3D,Z3D,i\Camera,0
	PositionEntity M,TFormedX(),TFormedY(),TFormedZ(),1
	RotateEntity M,EntityPitch(i\Camera,1),EntityYaw(i\Camera,1),EntityRoll(i\Camera,1),1
	EntityParent M,i\Camera,1
	
	To3D X,Y
	TFormPoint X3D,Y3D,Z3D,i\Camera,M
	V = AddVertex(S,TFormedX(),TFormedY(),TFormedZ(),0,0)
	
	To3D X+Width,Y
	TFormPoint X3D,Y3D,Z3D,i\Camera,M
	AddVertex(S,TFormedX(),TFormedY(),TFormedZ(),1,0)
	
	To3D X+Width,Y+Height
	TFormPoint X3D,Y3D,Z3D,i\Camera,M
	AddVertex(S,TFormedX(),TFormedY(),TFormedZ(),1,1)
	
	To3D X,Y+Height
	TFormPoint X3D,Y3D,Z3D,i\Camera,M
	AddVertex(S,TFormedX(),TFormedY(),TFormedZ(),0,1)
	
	AddTriangle S,V,V+1,V+2
	AddTriangle S,V+2,V+3,V
	
	EntityFX M,1+2+16+32
	
	Return M
End Function


Function AddQuad(Mesh,X#,Y#,Width#,Height#,CenterX=0,CenterY=0)
	i.t2dI3D = First t2dI3d
	
	M = Mesh
	S = GetSurface(M,1)
	
	To3D X,Y
	TFormPoint X3D,Y3D,Z3D,i\Camera,M
	V = AddVertex(S,TFormedX(),TFormedY(),TFormedZ(),0,0)
	
	To3D X+Width,Y
	TFormPoint X3D,Y3D,Z3D,i\Camera,M
	AddVertex(S,TFormedX(),TFormedY(),TFormedZ(),1,0)
	
	To3D X+Width,Y+Height
	TFormPoint X3D,Y3D,Z3D,i\Camera,M
	AddVertex(S,TFormedX(),TFormedY(),TFormedZ(),1,1)
	
	To3D X,Y+Height
	TFormPoint X3D,Y3D,Z3D,i\Camera,M
	AddVertex(S,TFormedX(),TFormedY(),TFormedZ(),0,1)
	
	AddTriangle S,V,V+1,V+2
	AddTriangle S,V+2,V+3,V
End Function


Function CreateTriangleFan(X#,Y#,Radius#,InnerRadius#,StartAngle#=0,AngleMax#=360,Segments=24,FlipY=0,FlipX=0)
	i.t2dI3d = First t2dI3d
	M = CreateMesh()
	S = CreateSurface(M)
	TurnEntity M,0,0,180*FlipY
	TurnEntity M,0,180*FlipX,0
	To3D(X,Y)
	TFormPoint X3D,Y3D,Z3D,i\Camera,0
	PositionEntity M,TFormedX(),TFormedY(),TFormedZ(),1
	RotateEntity M,EntityPitch(i\Camera,1),EntityYaw(i\Camera,1),EntityRoll(i\Camera,1),1
	EntityParent M,i\Camera,1
	
	AStep# = (AngleMax-StartAngle)/Segments
	A# = StartAngle
	
	For n = 0 To Segments-1
		A# = 180 + StartAngle + AStep*n
		To3D( X+Sin(A)*Radius, Y+Cos(A)*Radius )
		TFormPoint X3D,Y3D,Z3D,i\Camera,M
		V = AddVertex( S, TFormedX(),TFormedY(),TFormedZ(), 0, 0 )
		
		To3D( X+Sin(A+AStep)*Radius, Y+Cos(A+AStep)*Radius )
		TFormPoint X3D,Y3D,Z3D,i\Camera,M
		AddVertex( S, TFormedX(),TFormedY(),TFormedZ(), 1, 0 )
		
		To3D( X+Sin(A+AStep)*InnerRadius, Y+Cos(A+AStep)*InnerRadius )
		TFormPoint X3D,Y3D,Z3D,i\Camera,M
		AddVertex( S, TFormedX(),TFormedY(),TFormedZ(), 1, 1 )
		
		To3D( X+Sin(A)*InnerRadius, Y+Cos(A)*InnerRadius )
		TFormPoint X3D,Y3D,Z3D,i\Camera,M
		AddVertex( S, TFormedX(),TFormedY(),TFormedZ(), 0, 1 )
		
		AddTriangle S,V+2,V+1,V
		AddTriangle S,V,V+3,V+2
	Next
	EntityFX M,1+2+16+32
	TurnEntity M,0,0,180*FlipY
	TurnEntity M,0,180*FlipX,0
	Return M
End Function


Function AddTriangleFan(Mesh,X#,Y#,Radius#,InnerRadius#,StartAngle#=0,AngleMax#=360,Segments=24)
	i.t2dI3d = First t2dI3d
	S = GetSurface(Mesh,1)
	
	AStep# = (AngleMax-StartAngle)/Segments
	A# = StartAngle
	
	For n = 0 To Segments-1
		A# = 180 + StartAngle + AStep*n
		To3D( X+Sin(A)*Radius, Y+Cos(A)*Radius )
		TFormPoint X3D,Y3D,Z3D,i\Camera,Mesh
		V = AddVertex( S, TFormedX(),TFormedY(),TFormedZ(), 0, 0 )
		
		To3D( X+Sin(A+AStep)*Radius, Y+Cos(A+AStep)*Radius )
		TFormPoint X3D,Y3D,Z3D,i\Camera,Mesh
		AddVertex( S, TFormedX(),TFormedY(),TFormedZ(), 1, 0 )
		
		To3D( X+Sin(A+AStep)*InnerRadius, Y+Cos(A+AStep)*InnerRadius )
		TFormPoint X3D,Y3D,Z3D,i\Camera,Mesh
		AddVertex( S, TFormedX(),TFormedY(),TFormedZ(), 1, 1 )
		
		To3D( X+Sin(A)*InnerRadius, Y+Cos(A)*InnerRadius )
		TFormPoint X3D,Y3D,Z3D,i\Camera,Mesh
		AddVertex( S, TFormedX(),TFormedY(),TFormedZ(), 0, 1 )
		
		AddTriangle S,V+2,V+1,V
		AddTriangle S,V,V+3,V+2
	Next
End Function


Function ColorMeter(Mesh,Percent#,RFrom=255,GFrom=255,BFrom=255,AFrom#=1,RTo=128,GTo=128,BTo=128,ATo#=.5,Surface=0)
	If Surface = 0 Then
		S = GetSurface(Mesh,1)
	Else
		S = Surface
	EndIf
	
	Vs = CountVertices(S)
	For v = 0 To Vs-1 Step 4
		A# = (Float(v+4)/(Vs))*100
		If A <= Percent Then
			For n = 0 To 3
				VertexColor S,v+n,RFrom,GFrom,BFrom,AFrom
			Next
		ElseIf NoBlend = 0 Then
			D# = (A#-Percent)/((1.0/(Vs/4))*100)
			D2# = 1.0 - D
			
			R# = RFrom*D2 + RTo*D
			G# = GFrom*D2 + GTo*D
			B# = BFrom*D2 + BTo*D
			A# = AFrom*D2 + ATo*D
			For n = 0 To 3
				VertexColor S,v+n,R,G,B,A
			Next
			NoBlend = 1
		Else
			For n = 0 To 3
				VertexColor S,v+n,RTo,GTo,BTo,ATo
			Next
		EndIf
	Next
End Function


Function LoadPanel(X,Y,Path$,Flags=1+8,CenterX=0,CenterY=0)
	I = LoadImage(Path$)
	T = LoadTexture(Path$,Flags)
	W = ImageWidth(I)
	H = ImageHeight(I)
	Q = CreateQuad(X,Y,W,H,CenterX,CenterY)
	EntityTexture Q,T
	FreeTexture T
	FreeImage I
	Return Q
End Function
	

;; Example Code

Graphics3D 800,600,32,2

C = CreateCamera()

Init2DIn3D(C,4,800,600)


TriFan = CreateTriangleFan(256,384,64,48,90,360-90,8,0,1)
AddTriangleFan(TriFan,256-48-64,384,64,48,-90,90,8)

T = CreateTexture(32,32)
SetBuffer TextureBuffer(T)

Color 0,0,0
Rect 0,0,32,32,1
Color 255,255,255
Rect 3,3,32-6,32-6,1
Color 192,192,192
Rect 10,10,12,12,1

SetBuffer BackBuffer()

EntityTexture TriFan,T,0,0
EntityBlend TriFan,3

Quad = CreateMesh()
EntityFX Quad,1+2+32
CreateSurface(Quad)
EntityTexture Quad,T

For y = 0 To 3
	For n = 0 To 25
		AddQuad(Quad,10+n*18,50+y*18,16,16)
	Next
Next


Repeat
	A# = Sin(MilliSecs()*.02)*.3
	TurnEntity TriFan,0,0,A
	A =1
	
	HP# = HP# + (KeyDown(3) - KeyDown(2))*.1
	If HP > 100 Then Hp = 100
	If HP < 0 Then Hp = 0
	
	ColorMeter(TriFan,HP,0,186,255,A,255,100,0,.5)
	ColorMeter(Quad,HP,0,186,255,A,255,100,0,.5)
	
	UpdateWorld
	RenderWorld
	Text 4,4,"Press 1 to reduce the meter value, and 2 to increase the meter value"
	Flip 0
Until KeyHit(1)
