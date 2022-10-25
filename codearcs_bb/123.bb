; ID: 123
; Author: Entity
; Date: 2001-11-04 09:50:44
; Title: Floating point trap
; Description: Demonstrates FFP (in)accuracy becoming a problem

; Identical planetary systems at different 3d world coordinates
; by Jamie "Entity" van den Berge
;
; Demonstrates that single precision float variables are not that great :)
;
Graphics3D 800,600,16,2
Global w = GraphicsWidth(), h = GraphicsHeight(), f = FontHeight()
sun = CreateLight(): RotateEntity sun, 30, -45, 0

near# = 0 : far# = 600000: vfar# = 3000000: wow# = 10000000

Type System Field moon Field distance#,r#,lx# Field l,t End Type

Function CreateSystemView.System( distance#, l, t )
	Local s.System = New System: s\distance = distance: s\l = l: s\t = t
	planet = CreateSphere(    20) : PositionEntity planet, distance, distance, distance
	camera = CreateCamera(planet) : PositionEntity camera, 0, 0, -3
	s\moon = CreateSphere( 20, planet ): ScaleEntity s\moon, 0.2, 0.2, 0.2
	CameraViewport camera, l*w/2, t*h/2, w/2, h/2: s\distance = distance
	Return s
End Function

Function UpdateSystem( s.System, orbit )
	Origin s\l*w/2, s\t*h/2: Rect 0,0,w,h,0
	Text 0,0,"Planetary system": x# = s\distance+EntityX(s\moon)
	Text 0,20,"PlanetX: "+Int(s\distance)+", MoonX: "+(x)
	Text 0,20+f,"PlanetY: "+Int(s\distance)+", MoonY: "+(s\distance+EntityY(s\moon))
	Text 0,20+f*2,"PlanetZ: "+Int(s\distance)+", MoonZ: "+(s\distance+EntityZ(s\moon))
	Text 0,h/2-f*2,"Calculated movement accuracy: "+s\r
	If x<>s\lx Then s\r = Abs(x-s\lx): s\lx = x
	PositionEntity s\moon, 2*Sin(orbit), 2*Cos(orbit), 0
End Function

Dim s.System(4)
s(0) = CreateSystemView( near, 0, 0 )
s(1) = CreateSystemView( far , 1, 0 )
s(2) = CreateSystemView( vfar, 0, 1 )
s(3) = CreateSystemView( wow , 1, 1 )

While Not KeyHit(1)
	Flip: UpdateWorld:RenderWorld: orbit = orbit + 1
	For x = 0 To 3: UpdateSystem( s(x), orbit ): Next
Wend
End


