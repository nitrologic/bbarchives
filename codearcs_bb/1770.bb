; ID: 1770
; Author: MCP
; Date: 2006-07-31 13:02:00
; Title: Sorted Alpha Rendering
; Description: Hide unwanted object detail!

;*** Sorted Alpha rendering (object fade effect)
;*** Written by R Ferriby 2006

Graphics3D 800,600,32
SetBuffer BackBuffer()
img_screen%=CreateImage(GraphicsWidth(),GraphicsHeight())

light%=CreateLight()
PositionEntity light,50,50,-50

cam%=CreateCamera()

bgsphere%=CreateSphere()
ScaleEntity bgsphere,100,50,10
PositionEntity bgsphere,0,0,200

redcube%=CreateCube()
greencube%=CreateCube(redcube)
bluecube%=CreateCube(redcube)
yellowcube%=CreateCube(redcube)

PositionEntity redcube,0,0,100

ScaleEntity redcube,20,20,20,1
ScaleEntity greencube,10,10,30,1
ScaleEntity bluecube,10,30,10,1
ScaleEntity yellowcube,30,10,10,1

EntityColor redcube,255,0,0
EntityColor greencube,0,255,0
EntityColor bluecube,0,0,255
EntityColor yellowcube,255,255,0

alphamode%=0
alpha#=1.0
While Not KeyHit(1)
	If KeyDown(208)
		If alpha>0.0
			alpha=alpha-0.01
		Else
			alpha=0
		EndIf
	EndIf
	If KeyDown(200)
		If alpha<1.0
			alpha=alpha+0.01
		Else
			alpha=1.0
		EndIf
	EndIf
	If KeyDown(2)
		If alphamode=1
			alphamode=0
		EndIf
	EndIf
	If KeyDown(3)
		If alphamode=0
			alphamode=1
		EndIf
	EndIf
	TurnEntity redcube,1,0.5,0.5
	TurnEntity bgsphere,0,0,-0.25
	If alphamode=0
		;*** normal rendering
		CameraClsMode cam,1,1
		ShowEntity bgsphere
		ShowEntity redcube
		EntityAlpha redcube,alpha
		EntityAlpha greencube,alpha
		EntityAlpha bluecube,alpha
		EntityAlpha yellowcube,alpha
		RenderWorld
	Else
		;*** step 1 render bg scene
		HideEntity redcube
		ShowEntity bgsphere
		CameraClsMode cam,1,1
		RenderWorld
		CopyRect 0,0,GraphicsWidth(),GraphicsHeight(),0,0,BackBuffer(),ImageBuffer(img_screen)

		;*** step 2 render alpha mask
		HideEntity bgsphere
		CameraClsMode cam,0,0
		ShowEntity redcube
		EntityAlpha redcube,1
		EntityAlpha greencube,1
		EntityAlpha bluecube,1
		EntityAlpha yellowcube,1
		RenderWorld

		;*** step 3 restore bg scene
		CopyRect 0,0,GraphicsWidth(),GraphicsHeight(),0,0,ImageBuffer(img_screen),BackBuffer()

		;*** step 4 render alpha object
		EntityAlpha redcube,alpha
		EntityAlpha greencube,alpha
		EntityAlpha bluecube,alpha
		EntityAlpha yellowcube,alpha
		RenderWorld
	EndIf
	If alphamode=0
		Text 0,0,"Normal Alpha Mode"
	Else
		Text 0,0,"Sorted Alpha Mode"
	EndIf
	Text 0,30,"Alpha = "+alpha
	Text 0,60,"'1' - Normal Alpha"
	Text 0,80,"'2' - Sorted Alpha"
	Text 0,100,"'Up' - Inc Alpha"
	Text 0,120,"'Down' - Dec Alpha"
	Text 0,150,"'Esc' - Quit"
	
	Flip
Wend

End
