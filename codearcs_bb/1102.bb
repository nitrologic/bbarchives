; ID: 1102
; Author: Baystep Productions
; Date: 2004-07-02 16:58:14
; Title: OPIS2
; Description: A colormap to .BB utility

; You need a few files to use this.
; First a "ground.bmp" texture file:
; It's only used in the test mode.
; Second a heightmap by the name of "level.bmp"
; Third a water.bmp texture: for the water.
; And lastly the OPI.BMP file: I just copy the heightmap
; and put some green dots where I need to. It's easier.
Graphics 640,480,16,0
AppTitle "Information for OPIS"
scales%=Input$("Map Scale:")

Graphics3D 800,600,32,0
AppTitle "OPIS version 2"
SetBuffer BackBuffer()

Const obj=1,scn=2
Global opi=LoadImage("opi.bmp")
Global gx,gy,clrr,clrg,clrb
Global ltimer$

cam=CreateCamera()
PositionEntity cam,0,100,0
EntityType cam,obj
EntityRadius cam,10

light=CreateLight()
RotateEntity light,90,0,0

land=LoadTerrain("level.bmp")
ScaleEntity land,scales,95,scales
TerrainDetail land,8000
EntityType land,scn

water=CreatePlane()
PositionEntity water,0,15.5,0

watertex=LoadBrush("water.bmp",2,6,6)
BrushAlpha watertex,.5
PaintEntity water,watertex

ground=LoadTexture("ground.bmp",512)
EntityTexture land,ground
ScaleTexture ground,512,512

tree=CreateCylinder()
ScaleEntity tree,4,25,4
EntityColor tree,0,0,320 
EntityType tree,scn

imgsize=ImageWidth(opi)

temp=WriteFile("temperary.tmp")
WriteLine(temp,";Use the replace tool in Blitz3D to replace 'replc' with the tree")
WriteLine(temp,"Global imgsize="+imgsize)

SetBuffer FrontBuffer()
For gx=0 To imgsize
	For gy=0 To imgsize
		DrawImage(opi,0,0)
		GetColor gx,gy
		clrr=ColorRed():clrg=ColorGreen():clrb=ColorBlue()
		posx=gx*scales
		posy=(TerrainSize(land)*scales)-(gy*scales)
		If clrr=0 And clrg=255 And clrb=0
			trees=CopyEntity(tree)
			ScaleEntity trees,4,25,4
			EntityColor trees,0,0,320 
			EntityType trees,scn
			PositionEntity trees,posx,TerrainHeight(land,posx,posy)+50,posy,1
			WriteLine(temp,"tree=CopyEntity(replc)")
			WriteLine(temp,"PositionEntity tree,"+posx+",TerrainHeight(land,"+posx+","+posy+"),"+posy+",1")
		EndIf
		If gx<100 Then ltimer$="|||||"
		If gx>100 And gx<150 Then ltimer$="||||||||||"
		If gx>150 And gx<200 Then ltimer$="|||||||||||||||"
		If gx>200 And gx<250 Then ltimer$="||||||||||||||||||||"
		If gx>250 And gx<300 Then ltimer$="|||||||||||||||||||||||||"
		If gx>300 And gx<350 Then ltimer$="||||||||||||||||||||||||||||||"
		If gx>350 And gx<400 Then ltimer$="|||||||||||||||||||||||||||||||||||"
		If gx>400 And gx<450 Then ltimer$="||||||||||||||||||||||||||||||||||||||||"
		If gx>450 And gx<500 Then ltimer$="|||||||||||||||||||||||||||||||||||||||||||||"
		If gx>500 And gx<550 Then ltimer$="|||||||||||||||||||||||||||||||||||||||||||||||||| Done"
		Text 10,575,"Image Size:"+imgsize
		Text 10,585,"Loading..."+ltimer$
	Next
Next
SetBuffer BackBuffer()
CloseFile(temp)
Color 255,255,255

Collisions obj,scn,2,3

While Not KeyHit(1)
	If KeyDown(203) Then TurnEntity cam,0,1,0
	If KeyDown(205) Then TurnEntity cam,0,-1,0
	If KeyDown(200) Then MoveEntity cam,0,0,1
	If KeyDown(208) Then MoveEntity cam,0,0,-1
	If KeyHit(59)
		CopyFile "temperary.tmp","OPIS.bb"
	End If
	UpdateWorld
	 TranslateEntity cam,0,-.1,0
	RenderWorld
	Text 10,10,"Terrain Width: "+TerrainSize(land)
	Text 10,25,"Position: "+EntityX(cam,1)+","+EntityY(cam,1)+","+EntityZ(cam,1)
	Text 10,40,"Terrian Height: "+TerrainHeight(land,EntityX(cam,1),EntityZ(cam,1))*512
	Flip
Wend

End
