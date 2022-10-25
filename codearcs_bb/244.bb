; ID: 244
; Author: bradford6
; Date: 2002-02-16 12:18:45
; Title: FITMESH example
; Description: draws a bounding box around a mesh

; FitMesh Example
; ----------------

Graphics3D 640,480
SetBuffer BackBuffer()

midw = GraphicsWidth()/2
midh = GraphicsHeight()/2

HidePointer

camspeed#=15 			; used to make the camera movements smooth
camera=CreateCamera()	; create a camera for to see with

light=CreateLight(2)	; a light!

tex=CreateTexture(32,32)		; create a 32X32 texture for OBJECT
SetBuffer TextureBuffer(tex)	; set drawing to the texure TEX
Color 255,255,0					; create a texture for the object
Rect 0,0,31,31
Color 0,0,255
Rect 15,15,28,28
Rect 2,2,14,14

worldtex=CreateTexture(32,32)		; create a 32X32 texture for WORLD
SetBuffer TextureBuffer(worldtex)	; set drawing to the texure TEX
Color 100,100,240					; create a texture for the object
Rect 0,0,31,31
Color 40,40,80
Rect 16,16,32,32
Rect 0,0,16,16

fittex1=CreateTexture(32,32)		; create 2 boxes and flipmesh one so all angles will show
SetBuffer TextureBuffer(fittex1)
Color 40,0,0
Rect 0,0,31,31

Color 255,255,255
Rect 0,0,31,31,True

fittex2=CreateTexture(32,32)
SetBuffer TextureBuffer(fittex2)
Color 0,40,0
Rect 0,0,31,31

Color 255,255,255
Rect 0,0,32,32,False


fitbox = CreateCube()
fitbox2=CreateCube()

EntityAlpha fitbox,.6
EntityAlpha fitbox2,.6
EntityTexture fitbox,fittex1
EntityTexture fitbox2,fittex2


FlipMesh fitbox

cube=CreateCube()
EntityTexture cube,tex
ScaleMesh cube,.5,5,3
RotateMesh cube,45,45,0
PositionEntity cube,0,0,15


world=CreateCube()
ScaleEntity world,60,60,60
FlipMesh world
EntityTexture world,worldtex




SetBuffer BackBuffer()

While Not KeyDown( 1 )  ;*** MAIN LOOP

MY#=interpolate#(MouseYSpeed(),MY#,camspeed# )/6   ; smooth camera
MX#=interpolate#(MouseXSpeed(),MX#,camspeed# )/3
MoveMouse 100,100

MoveEntity camera,mx,0,-my
PointEntity camera,cube

mw# = MeshWidth(cube)
MH# = MeshHeight(cube)
MD# = MeshDepth(cube)
xp# = EntityX(cube)
yp# = EntityY(cube)
zp# = EntityZ(cube)

If MouseDown(1)=0 Then RotateMesh cube,1,1,1
;R# = R# + .001
If R#>180 Then R#=-180	
;PositionEntity fitbox,xp,yp,zp
FitMesh fitbox,xp-(mw/2),yp-(mh/2),zp-(md/2),mw#,Mh,Md
FitMesh fitbox2,xp-(mw/2),yp-(mh/2),zp-(md/2),mw#,Mh,Md

;TurnEntity cube,.1,0,.1

MoveEntity camera,mx,0,0
PointEntity camera,cube

	RenderWorld
	Color 255,255,0
	Text midw,midh,"LEFT CLICK TO FREEZE MESH",True
	Flip

Wend

End



Function interpolate#(newvalue#,oldvalue#,increments# )
	If increments>1 Then oldvalue#=oldvalue#-(oldvalue#-newvalue#)/increments
	If increments<=1 Then oldvalue=newvalue
	Return oldvalue#
End Function
