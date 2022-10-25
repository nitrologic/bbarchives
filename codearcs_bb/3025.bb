; ID: 3025
; Author: Kryzon
; Date: 2013-02-03 12:30:27
; Title: Realtime Color-filter
; Description: Very fast and simple to use; works with any color combination.

Graphics3D 800,600,0,2

Local camera = CreateCamera()
	PositionEntity camera,0,5,-5
	RotateEntity camera,45,0,0
	
Local filterQuad = CreateFilterQuad(camera)
	
LightRange CreateLight(2),3
AmbientLight 100,100,100

VariousOtherThings 30

Local fpsTimer = CreateTimer(60)
Const FILTER_SPEED# = 0.01
Local filterStrength# = 1.0
Local activeColors$ = ""

Local redPass% = 1, greenPass% = 0, bluePass% = 0

While Not KeyHit(1)
	WaitTimer(fpsTimer)

	filterStrength = filterStrength + (KeyDown(30)*FILTER_SPEED) - (KeyDown(44)*FILTER_SPEED)
	If filterStrength > 1.0 Then filterStrength = 1.0
	If filterStrength < 0.0 Then filterStrength = 0.0

	;Possible filter colors: 
	
	;RED: 255,0,0
	;YELLOW: 255,255,0
	;GREEN: 0,255,0 (Great for night-vision scenes.)
	;CYAN: 0,255,255
	;BLUE: 0,0,255
	;PINK: 255,0,255
	
	;(You can use any values lower than 255, but that may darken the screen a bit.)
	
	activeColors = ""
	If (KeyHit(2) + KeyHit(79)) Then redPass = 1 - redPass
	If redPass Then activeColors  = activeColors + "RED"
	
	If (KeyHit(3) + KeyHit(80)) Then greenPass = 1 - greenPass
	If greenPass Then activeColors = activeColors + " GREEN"
	
	If (KeyHit(4) + KeyHit(81)) Then bluePass = 1 - bluePass
	If bluePass Then activeColors = activeColors + " BLUE"
	
	If (redPass+greenPass+bluePass = 0) Then activeColors = "NONE"
	If (redPass+greenPass+bluePass = 3) Then activeColors = "ALL"

	;Update filter color.
	SetFilterColor(filterQuad, filterStrength, 255*redPass, 255*greenPass, 255*bluePass)

	RenderWorld()
	Text 10,10,"Use the A or Z keys to control the intensity of the filter."
	Text 10,30,"Use the 1, 2 and 3 keys to toggle the colors of the filter."
	Text 10,60,"Active colors: "+activeColors
	Text 10,80,"Filter intensity: ["+Int(filterStrength*100)+" %]"
	
	Flip
Wend

;Creates a full screen quad.
Function CreateFilterQuad(parent=0)
	Local mesh = CreateMesh(parent)
	Local surf = CreateSurface(mesh)
	
	AddVertex surf,-0.5,0.5,0
	AddVertex surf,0.5,0.5,0
	AddVertex surf,-0.5,-0.5,0
	AddVertex surf,0.5,-0.5,0
	
	AddTriangle surf,0,1,3 
	AddTriangle surf,0,3,2
	
	EntityColor mesh,255,255,255
	
	EntityFX mesh,1 ;Fullbright FX
	EntityBlend mesh,2 ;Multiply
	
	ScaleMesh mesh,4,3,1 ;Scale the quad mesh with a 4:3 ratio, to fill the entire screen.
	MoveEntity mesh,0,0,2 ;Move quad mesh so it fills the entire screen of the 4:3 camera.
	
	Return mesh
End Function

Function SetFilterColor(filterMesh, fVal#=1.0, r#=0, g#=0, b#=0)
	;Interpolate from [255,255,255] (which is white, no filter) to [r,g,b] supplied by the user.
	EntityColor filterMesh, 255.0 - (255.0-r)*fVal, 255.0 - (255.0-g)*fVal, 255.0 - (255.0-b)*fVal
End Function

;Aesthetics. Function by Floyd.
Function VariousOtherThings( quantity )
	For n = 1 To quantity
		Select Rand( 1, 4 )
			Case 1 : temp = CreateSphere()
			Case 2 : temp = CreateCone()
			Case 3 : temp = CreateCylinder()
			Case 4 : temp = CreateCube()
		End Select
		ScaleEntity temp, Rnd( 0.6, 1.5 ), Rnd( 0.6, 1.5 ), Rnd( 0.6, 1.5 )
		EntityColor temp, Rand( 100, 255 ), Rand( 100, 255 ), Rand( 100, 255 )
		RotateEntity temp, Rnd( -20, 20 ), Rnd( -50, 50 ), Rnd( -20, 20 )
		angle# = Rnd( -45, 225)
		dist#  = Rnd( 2.5, 6 )
		PositionEntity temp, dist * Cos( angle ), Rnd( - 3, 3 ), dist * Sin( angle )
	Next
End Function

End
