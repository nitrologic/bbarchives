; ID: 1472
; Author: Mr Snidesmin
; Date: 2005-09-30 23:51:36
; Title: Jellyfish Animation
; Description: Jellyfish Animation

Global JellyParam#
Global WaveParam1#
Global WaveParam2#
Global WaveParam3#


Local fps%
Local fpscount%
Local millistop%
Local millis%


Graphics3D 800, 600

;create world:
Global GlobalLight% = CreateLight()
RotateEntity GlobalLight, 70, 45, 45
Global GlobalCamera% = CreateCamera()
LightColor GlobalLight, 255,255,255
AmbientLight 100,120,255
PositionEntity GlobalCamera, 4, 0, 0
CameraClsColor GlobalCamera, 0,50,90

;create jellyfish (mshJellyBase is just used to store original vert positions)
Global mshJellyBase% = CreateMesh() ;LoadMesh("jelly.3ds")
For n# = 0 To 1 Step 0.2
	mshtmp = CreateSphere(8)
	ScaleMesh mshtmp, 1/(1+10*n), 0.35, 1/(1+10*n)
	PositionMesh mshtmp, 0, -n, 0
	AddMesh mshtmp, mshJellyBase
	FreeEntity mshtmp
Next
For a# = 0 To 350 Step 10
	off# = -Rnd(0.3)
	For n# = 0 To 0.5 Step 0.05
		mshtmp = CreateCylinder(2, False)
		ScaleMesh mshtmp, 0.003, 0.05, 0.003
		PositionMesh mshtmp, 0.8 * Cos(a), off-n, 0.8 * Sin(a)
		AddMesh mshtmp, mshJellyBase
		FreeEntity mshtmp
	Next
Next
Global mshJelly% = CopyMesh(mshJellyBase)
HideEntity mshJellyBase
EntityAlpha mshJelly, 0.3

;Begin main loop
PointEntity GlobalCamera, mshJelly
While Not KeyHit(1)
	UpdateJelly
	Navigate_World_With_MouseAndKeys(0.5, .1)
	
	UpdateWorld()
	
	SetBuffer BackBuffer()
	RenderWorld()
	Color 255, 0, 0
	Text 1, 1, "FPS=" + fps	
	Text 1, 12, "TrisRendered=" + TrisRendered()
	Flip
	
	fpscount = fpscount + 1
	millis% = MilliSecs()
	If millistop < millis - 1000 Then
		millistop = millis
		fps = fpscount
		fpscount = 0
	End If
Wend
End


;deforms mesh:
Function UpdateJelly()
	spd# = 1.5 + 0.5 * Sin(JellyParam)
	JellyParam = JellyParam + spd Mod 360
	WaveParam1= WaveParam1+ 1.4 Mod 360
	WaveParam2= WaveParam2+ 2.16 Mod 360
	WaveParam3= WaveParam3+ 0.79 Mod 360
	
	s0% = GetSurface(mshJellyBase, 1)
	s1% = GetSurface(mshJelly, 1)
	
	For iv% = 0 To CountVertices(s0)-1
		h1# = 590*(VertexY(s0, iv)+VertexX(s0, iv))
		h2# = 190*(VertexY(s0, iv)+VertexZ(s0, iv))
		
		wz# = 0.08 * Cos(WaveParam1+h1) * Cos(WaveParam2+h1) * Cos(WaveParam3+h1) *VertexY(s0, iv)
		wx# = 0.08 * Sin(WaveParam1+h2) * Cos(WaveParam2+h2) * Cos(WaveParam3+h2) *VertexY(s0, iv)


		s# = Sqr(VertexX(s0, iv)^2 + VertexY(s0, iv)^2)
		
		n# = 0.5 * Cos(JellyParam) / (Abs(VertexY(s0, iv))+0.3) + s
		
		If VertexY(s0, iv) < 0 Then
			c# = 0.1 * Abs(VertexY(s0, iv)) ^ 2
		Else
			c = 0
		End If
		
		n2# = 0.3 * (2.5+Sin(-JellyParam+140*VertexY(s0, iv))) / (Abs(VertexY(s0, iv))+1.3)
		VertexCoords s1, iv, VertexX(s0, iv) * (n2+c) + wx, VertexY(s0, iv) * (n+1.4)*0.4, VertexZ(s0, iv)* (n2+c) + wz
	Next
	UpdateNormals mshJelly
End Function






Function Navigate_World_With_MouseAndKeys(turnSpeed#=1, moveSpeed#=1)
	Local dY# = EntityPitch(GlobalCamera)+MouseYSpeed()/2*turnSpeed
	If dY > 89 Then dY = 89
	If dY < -89 Then dY = -89
	
	Local dz# = (KeyDown(200)-KeyDown(208)) * moveSpeed
	Local dx# = (KeyDown(205)-KeyDown(203)) * moveSpeed
	If dz <> 0 And dx <> 0 Then 
		dx=dx * 0.707
		dz=dz * 0.707
	End If
	RotateEntity GlobalCamera, dY, EntityYaw(GlobalCamera)-(MouseXSpeed()/2)*turnSpeed, 0
	MoveEntity GlobalCamera, dx, 0, dz
	MoveMouse GraphicsWidth()/2, GraphicsHeight()/2
	
	If KeyHit(4) Then 
		CameraZoom GlobalCamera, 4.0
	End If
	If KeyHit(5) Then
		CameraZoom GlobalCamera, 1.0
	End If
End Function
