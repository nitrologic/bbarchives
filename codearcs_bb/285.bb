; ID: 285
; Author: Doiron
; Date: 2002-06-17 23:46:52
; Title: Heightland
; Description: Heightmap based Texture Landscape Generator

;===================================================================================
;
;  "Heightland 0.5b - Heightmap based Texture Landscape Generator"
;
;  --CREDITS--
;  Procedural Colourmap Algorithm: Rhodan (Pat Meloy, derived from a tutorial by Tobias Franke)
;  GUI: Yappy (Stewart Yapp)
;  Interface & advanced functions: Doiron (Leonardo D'Alessandri)
;
;  Special thanks to all the blitz community!  
;
;===================================================================================

Include "XLnt-3D_v1.0.bb"
Include "start.bb"

AppTitle ("Heightland 0.5b - Heightmap based Texture Landscape Generator")

Const FPS = 30
Global fstep
Global ExitProgram=False
Global Windowed
period=1000/FPS
time=MilliSecs()-period

GUI_GFXSETUP()

SetFont LoadFont("VERDANA",14,True)
StartDir$=CurrentDir()

Global snowlev,snowblend,stonelev,stoneblend,grasslev,grassblend,sandblend

;** SKINS **
;ChangeDir StartDir$
;DefaultSkin=LoadImage("skin_darkoak.bmp")
;GUI_DEFAULT_SKIN(DefaultSkin)

;CREATE **TEXTURE BLENDING WINDOW**
Blending.Window=GUI_WINDOW.window(8,8,400,464,"TEXTURE BLENDER",1,0,1,0,0)
SnowFrame.gadget=GUI_FRAME.gadget(Blending.Window,10,20,260,100,"Snow",0,"Snow Related Settings")
SnowText1.gadget=GUI_TEXT.gadget(Blending.Window,20,35,200,10,"Snow start level:","")SnowText1.gadget=GUI_TEXT.gadget(Blending.Window,20,35,200,10,"Snow start level:","")
SnowStartSlider.gadget=GUI_SLIDER.gadget(Blending.Window,20,50,180,192,0,255,1,1,"pull slider")
SnowText2.gadget=GUI_TEXT.gadget(Blending.Window,20,65,200,10,"Snow blending range:","")
SnowRangeSlider.gadget=GUI_SLIDER.gadget(Blending.Window,20,80,180,12,0,255,1,1,"pull slider")
SnowStartValue.gadget=GUI_TXTINPUT.gadget(Blending.Window,229,48,35,SnowStartSlider\slider\val,"",0,0,"")
SnowRangeValue.gadget=GUI_TXTINPUT.gadget(Blending.Window,229,78,35,SnowRangeSlider\slider\val,"",0,0,"")
SnowInputFile.gadget=GUI_TXTINPUT.gadget(Blending.Window,20,100,172,".\TextureSets\Default\snowtexture.bmp","File:",0,1,"")
SnowLoadFile.gadget=GUI_BUTTUN.gadget(Blending.Window,230,100,33,"dirs",1,"Leaf through your folders...")
SnowTexture.gadget=GUI_IMAGEBOX.gadget(Blending.Window,280,26,110,100,SnowInputFile\txtinput\txt$,1,1,"")

StoneFrame.gadget=GUI_FRAME.gadget(Blending.Window,10,130,260,100,"Stone",0,"Stone Related Settings")
StoneText1.gadget=GUI_TEXT.gadget(Blending.Window,20,145,200,10,"Stone start level:","")
StoneStartSlider.gadget=GUI_SLIDER.gadget(Blending.Window,20,160,180,128,0,255,1,1,"pull slider")
StoneText2.gadget=GUI_TEXT.gadget(Blending.Window,20,175,200,10,"Stone blending range:","")
StoneRangeSlider.gadget=GUI_SLIDER.gadget(Blending.Window,20,190,180,12,0,255,1,1,"pull slider")
StoneStartValue.gadget=GUI_TXTINPUT.gadget(Blending.Window,229,158,35,StoneStartSlider\slider\val,"",0,0,"")
StoneRangeValue.gadget=GUI_TXTINPUT.gadget(Blending.Window,229,188,35,StoneRangeSlider\slider\val,"",0,0,"")
StoneInputFile.gadget=GUI_TXTINPUT.gadget(Blending.Window,20,210,172,".\TextureSets\Default\stonetexture.bmp","File:",0,1,"")
StoneLoadFile.gadget=GUI_BUTTUN.gadget(Blending.Window,230,210,33,"dirs",1,"Leaf through your folders...")
StoneTexture.gadget=GUI_IMAGEBOX.gadget(Blending.Window,280,136,110,100,StoneInputFile\txtinput\txt$,1,1,"")

GrassFrame.gadget=GUI_FRAME.gadget(Blending.Window,10,240,260,100,"Grass",0,"Grass Related Settings")
GrassText1.gadget=GUI_TEXT.gadget(Blending.Window,20,255,200,10,"Grass start level:","")
GrassStartSlider.gadget=GUI_SLIDER.gadget(Blending.Window,20,270,180,64,0,255,1,1,"pull slider")
GrassText2.gadget=GUI_TEXT.gadget(Blending.Window,20,285,200,10,"Grass blending range:","")
GrassRangeSlider.gadget=GUI_SLIDER.gadget(Blending.Window,20,300,180,64,0,255,1,1,"pull slider")
GrassStartValue.gadget=GUI_TXTINPUT.gadget(Blending.Window,229,268,35,GrassStartSlider\slider\val,"",0,0,"")
GrassRangeValue.gadget=GUI_TXTINPUT.gadget(Blending.Window,229,298,35,GrassRangeSlider\slider\val,"",0,0,"")
GrassInputFile.gadget=GUI_TXTINPUT.gadget(Blending.Window,20,320,172,".\TextureSets\Default\grasstexture.bmp","File:",0,1,"")
GrassLoadFile.gadget=GUI_BUTTUN.gadget(Blending.Window,230,320,33,"dirs",1,"Leaf through your folders...")
GrassTexture.gadget=GUI_IMAGEBOX.gadget(Blending.Window,280,246,110,100,GrassInputFile\txtinput\txt$,1,1,"")

SandFrame.gadget=GUI_FRAME.gadget(Blending.Window,10,350,260,100,"Sand",0,"Sand Related Settings")
SandText2.gadget=GUI_TEXT.gadget(Blending.Window,20,395,200,10,"Sand blending range:","")
SandRangeSlider.gadget=GUI_SLIDER.gadget(Blending.Window,20,410,180,64,0,255,1,1,"pull slider")
SandRangeValue.gadget=GUI_TXTINPUT.gadget(Blending.Window,229,408,35,SandRangeSlider\slider\val,"",0,0,"")
SandInputFile.gadget=GUI_TXTINPUT.gadget(Blending.Window,20,430,172,".\TextureSets\Default\sandtexture.bmp","File:",0,1,"")
SandLoadFile.gadget=GUI_BUTTUN.gadget(Blending.Window,230,430,33,"dirs",1,"Leaf through your folders...")
SandTexture.gadget=GUI_IMAGEBOX.gadget(Blending.Window,280,356,110,100,SandInputFile\txtinput\txt$,1,1,"")

;CREATE **HEIGHTMAP WINDOW**
HeightmapLoad.Window=GUI_WINDOW.window(421,8,240,265,"HEIGHTMAP",1,0,1,0,0)
HeightmapLoadFile.gadget=GUI_BUTTUN.gadget(HeightmapLoad.Window,HeightmapLoad.window\w/3-10,HeightmapLoad.window\h-20,33,"load heightmap",1,"")
HeightmapTexture.gadget=GUI_IMAGEBOX.gadget(HeightmapLoad.Window,10,20,220,220,"Heightmaps\heightmap.bmp",1,1,"")

;CREATE **COLOURMAP WINDOW**
Colourmap.Window=GUI_WINDOW.window(421,287,240,265,"COLOURMAP",1,0,1,0,0)
ColourmapPreview.gadget=GUI_BUTTUN.gadget(Colourmap.Window,HeightmapLoad.window\w/2-StringWidth("...preview...")/1.7,21,33,"...preview...",1,"")
ColourmapTexture.gadget=GUI_IMAGEBOX.gadget(Colourmap.Window,18,40,200,200,"Colourmaps\Colourmap.bmp",1,1,"")
ColourmapRenderFile.gadget=GUI_BUTTUN.gadget(Colourmap.Window,HeightmapLoad.window\w/2-StringWidth("render to file...")/1.7,HeightmapLoad.window\h-20,33,"render to file...",1,"render colourmap (cpu intensive)")
ColourmapLoadFile.gadget=GUI_BUTTUN.gadget(Colourmap.Window,HeightmapLoad.window\w-55,HeightmapLoad.window\h-20,33,"load",1,"load a premade colourmap")

;CREATE **TEXTURESET WINDOW**
TextureSet.Window=GUI_WINDOW.window(8,486,400,100,"TEXTURE SETS",1,0,1,0,0)
TextureSetFrame.gadget=GUI_FRAME.gadget(TextureSet.Window,10,20,378,40,"Current Texture Set",0,"")
TextureSetInputFile.gadget=GUI_TXTINPUT.gadget(TextureSet.Window,20,40,283,".\TextureSets\default.set","File:",0,1,"")
TextureSetLoadFile.gadget=GUI_BUTTUN.gadget(TextureSet.Window,343,40,33,"dirs",1,"Leaf through your folders...")
TextureSetNewFile.gadget=GUI_BUTTUN.gadget(TextureSet.Window,12,75,118,"NEW",1,"Creates a new Texture Set")
TextureSetSaveFile.gadget=GUI_BUTTUN.gadget(TextureSet.Window,140,75,119,"SAVE",1,"Saves current Texture Set")
TextureSetSaveAsFile.gadget=GUI_BUTTUN.gadget(TextureSet.Window,267,75,118,"SAVE AS...",1,"Saves current Texture Set with a new filename")

;CREATE **MAIN WINDOW**
Main.Window=GUI_WINDOW.window(675,8,80,100,"MAIN",1,0,1,0,0)
Credits.gadget=GUI_BUTTUN.gadget(Main.Window,18,32,70,"Credits",1,"")
	About.Window=GUI_WINDOW.window(GraphicsWidth()/2-200,GraphicsHeight()/2-200,400,400,"CREDITS",1,0,1,0,0)
	FIL=ReadFile("Credits.dat")
	Repeat
		A$=A$+ReadLine$(FIL)+Chr$(10)
	Until Eof(FIL)
	CloseFile FIL
	CreditsText.gadget=GUI_TEXT.gadget(About.Window,70,40,360,360,A$,"")				
	Backtowork.gadget=GUI_BUTTUN.gadget(About.Window,About.Window\h/2-100,About.Window\h-40,200,"Back to work...",1,"")
	GUI_OPENWIN(About.Window)
	GUI_HIDEWIN(About.Window)
Help.gadget=GUI_BUTTUN.gadget(Main.Window,18,52,70,"Help",1,"")
	HelpPopUp.Window=GUI_WINDOW.window(GraphicsWidth()/2-200,GraphicsHeight()/2-200,400,400,"HELP",1,0,1,0,0)
	FIL=ReadFile("Help.dat")
	Repeat
		B$=B$+ReadLine$(FIL)+Chr$(10)
	Until Eof(FIL)
	CloseFile FIL
	HelpText.gadget=GUI_TEXT.gadget(HelpPopup.Window,20,40,360,360,B$,"")				
	Backtowork2.gadget=GUI_BUTTUN.gadget(HelpPopUp.Window,HelpPopUp.Window\h/2-100,HelpPopUp.Window\h-40,200,"Back to work...",1,"")
	GUI_OPENWIN(HelpPopUp.Window)
	GUI_HIDEWIN(HelpPopUp.Window)
Quit.gadget=GUI_BUTTUN.gadget(Main.Window,18,72,70,"Quit",1,"")
	AreYouSure.Window=GUI_WINDOW.window(GraphicsWidth()/2-200,GraphicsHeight()/2-50,400,100,"QUITTING... ARE YOU SURE?",1,0,1,0,0)
	AreYouSureText.gadget=GUI_TEXT.gadget(AreYouSure.Window,AreYouSure.Window\w-290,AreYouSure.Window\h/2-20,200,10,"Do you really want to quit?","")
	yes.gadget=GUI_BUTTUN.gadget(AreYouSure.Window,50,AreYouSure.Window\h/2+20,100,"Yes...",1,"Have a nice day...")
	no.gadget=GUI_BUTTUN.gadget(AreYouSure.Window,AreYouSure.Window\w-150,AreYouSure.Window\h/2+20,100,"NO!",1,"I hit the wrong button...")
	GUI_OPENWIN(AreYouSure.Window)
	GUI_HIDEWIN(AreYouSure.Window)

;CREATE **3D CONTROLS WINDOW**
LandscapeControls.Window=GUI_WINDOW.window(675,118,80,100,"3D VIEW",1,0,1,0,0)
Preview3D.gadget=GUI_BUTTUN.gadget(LandscapeControls.Window,12,32,70,"3D Preview",1,"")
GUI_GADACTIVE(Preview3D.gadget,0)

GUI_OPENWIN(Blending.Window)
GUI_OPENWIN(HeightmapLoad.Window)
GUI_OPENWIN(Colourmap.Window)
GUI_OPENWIN(TextureSet.Window)
GUI_OPENWIN(Main.Window)
GUI_OPENWIN(LandscapeControls.Window)

GUI_QMENU_ON=1

Dim mymaps(4)
	mymaps(0)=LoadImage(SnowInputFile\txtinput\txt$)
	mymaps(1)=LoadImage(StoneInputFile\txtinput\txt$)
	mymaps(2)=LoadImage(GrassInputFile\txtinput\txt$)
	mymaps(3)=LoadImage(SandInputFile\txtinput\txt$)
	
	snowlev=SnowStartSlider\slider\val
	snowblend=SnowRangeSlider\slider\val
	stonelev=StoneStartSlider\slider\val
	stoneblend=StoneRangeSlider\slider\val
	grasslev=GrassStartSlider\slider\val
	grassblend=GrassRangeSlider\slider\val
	sandblend=SandRangeSlider\slider\val

While ExitProgram=False

		ClsColor 54,71,110	

		Select True
			Case GUI_GADHIT.gadget=SnowLoadFile.gadget
				FILE$=GUI_FILEREQUEST("Please Select a texture To load (snow)",".bmp#.jpg#.png#","TextureSets\Default")
				ChangeDir StartDir$
				GUI_SETTEXT(SnowInputFile.gadget,FILE$)
				GUI_SETTEXT(SnowTexture.gadget,FILE$)
				mymaps(0)=LoadImage(SnowInputFile\txtinput\txt$)
				If mymaps(0)="0" Or mymaps(0)="NONE"
					GUI_WINACTIVE(Colourmap.window,0) : GUI_GADACTIVE(ColourmapPreview.gadget,0) : GUI_GADACTIVE(ColourmapRenderFile.gadget,0)
					ChangeDir startdir$
					GUI_SETTEXT(SnowTexture.gadget,"InvalidFilename.bmp")
				Else 
					GUI_WINACTIVE(Colourmap.window,1) : GUI_GADACTIVE(ColourmapPreview.gadget,1) : GUI_GADACTIVE(ColourmapRenderFile.gadget,1)
				EndIf
			Case GUI_GADHIT.gadget=StoneLoadFile.gadget
				FILE$=GUI_FILEREQUEST("Please Select a texture to load (stone)",".bmp#.jpg#.png##","TextureSets\Default")
				ChangeDir StartDir$
				GUI_SETTEXT(StoneInputFile.gadget,FILE$)
				GUI_SETTEXT(StoneTexture.gadget,FILE$)				
				mymaps(1)=LoadImage(StoneInputFile\txtinput\txt$)
				If mymaps(1)="0" Or mymaps(1)="NONE"
					GUI_WINACTIVE(Colourmap.window,0) : GUI_GADACTIVE(ColourmapPreview.gadget,0) : GUI_GADACTIVE(ColourmapRenderFile.gadget,0)
					ChangeDir startdir$
					GUI_SETTEXT(StoneTexture.gadget,"InvalidFilename.bmp")
				Else 
					GUI_WINACTIVE(Colourmap.window,1) : GUI_GADACTIVE(ColourmapPreview.gadget,1) : GUI_GADACTIVE(ColourmapRenderFile.gadget,1)
				EndIf
			Case GUI_GADHIT.gadget=GrassLoadFile.gadget
				FILE$=GUI_FILEREQUEST("Please Select a texture to load (grass)",".bmp#.jpg#.png##","TextureSets\Default")
				ChangeDir StartDir$				
				GUI_SETTEXT(GrassInputFile.gadget,FILE$)
				GUI_SETTEXT(GrassTexture.gadget,FILE$)
				mymaps(2)=LoadImage(GrassInputFile\txtinput\txt$)
				If mymaps(2)="0" Or mymaps(2)="NONE"
					GUI_WINACTIVE(Colourmap.window,0) : GUI_GADACTIVE(ColourmapPreview.gadget,0) : GUI_GADACTIVE(ColourmapRenderFile.gadget,0)
					ChangeDir startdir$
					GUI_SETTEXT(GrassTexture.gadget,"InvalidFilename.bmp")
				Else 
					GUI_WINACTIVE(Colourmap.window,1) : GUI_GADACTIVE(ColourmapPreview.gadget,1) : GUI_GADACTIVE(ColourmapRenderFile.gadget,1)
				EndIf
			Case GUI_GADHIT.gadget=SandLoadFile.gadget
				FILE$=GUI_FILEREQUEST("Please Select a texture to load (sand)",".bmp#.jpg#.png##","TextureSets\Default")
				ChangeDir StartDir$				
				GUI_SETTEXT(SandInputFile.gadget,FILE$)
				GUI_SETTEXT(SandTexture.gadget,FILE$)
				mymaps(3)=LoadImage(SandInputFile\txtinput\txt$)
				If mymaps(3)="0" Or mymaps(3)="NONE"
					GUI_WINACTIVE(Colourmap.window,0) : GUI_GADACTIVE(ColourmapPreview.gadget,0) : GUI_GADACTIVE(ColourmapRenderFile.gadget,0)
					ChangeDir startdir$
					GUI_SETTEXT(SandTexture.gadget,"InvalidFilename.bmp")
				Else 
					GUI_WINACTIVE(Colourmap.window,1) : GUI_GADACTIVE(ColourmapPreview.gadget,1) : GUI_GADACTIVE(ColourmapRenderFile.gadget,1)
				EndIf

			Case GUI_GADHIT.gadget=HeightmapLoadFile.gadget
				FILE$=GUI_FILEREQUEST("Please Select a heightmap to load",".bmp#.jpg#.png#.all#","Heightmaps\")
				ChangeDir StartDir$				
				GUI_SETTEXT(HeightmapTexture.gadget,FILE$)
				If LoadImage(FILE$)="0" Or LoadImage(FILE$)="NONE"
					GUI_WINACTIVE(Colourmap.window,0) : GUI_GADACTIVE(ColourmapPreview.gadget,0) : GUI_GADACTIVE(ColourmapRenderFile.gadget,0)
					ChangeDir startdir$
					GUI_SETTEXT(HeightmapTexture.gadget,"InvalidFilename.bmp")
				Else 
					GUI_WINACTIVE(Colourmap.window,1) : GUI_GADACTIVE(ColourmapPreview.gadget,1) : GUI_GADACTIVE(ColourmapRenderFile.gadget,1)
				EndIf
			Case GUI_GADHIT.gadget=ColourmapRenderFile.gadget
				FILE$=GUI_FILEREQUEST("Select a destination for the colourmap",".bmp#","Colourmaps\")
				ChangeDir StartDir$
				If Right(FILE$,1)="\" Then FILE$=FILE$+"DefaultColourMap.bmp"
				If Right$(FILE$,4)<>".bmp" Then FILE$=FILE$+".bmp"
				ProcessColourmap(HeightMapTexture\IMAGEBOX\FILE$,FILE$)
				GUI_SETTEXT(ColourmapTexture.gadget,FILE$)
				GUI_GADACTIVE(Preview3D.gadget,1)
			Case GUI_GADHIT.gadget=ColourmapLoadFile.gadget
				FILE$=GUI_FILEREQUEST("Please Select a colourmap to load",".bmp#.jpg#.png#.all#","Colourmaps\")
				ChangeDir StartDir$				
				GUI_SETTEXT(ColourmapTexture.gadget,FILE$)
				If LoadImage(FILE$)="0" Or LoadImage(FILE$)="NONE"
					GUI_GADACTIVE(Preview3D.gadget,0)
					ChangeDir startdir$
					GUI_SETTEXT(ColourmapTexture.gadget,"InvalidFilename.bmp")
				Else 
					GUI_GADACTIVE(Preview3D.gadget,1)
				EndIf
							
			Case GUI_GADHIT.gadget=ColourmapPreview.gadget
				ProcessColourmap(HeightMapTexture\IMAGEBOX\FILE$,"",True)
				ChangeDir StartDir$
				GUI_SETTEXT(ColourmapTexture.gadget,"preview.bm_")
				GUI_GADACTIVE(Preview3D.gadget,1)
				
			Case GUI_GADHIT.gadget=TextureSetNewFile.gadget Or GUI_GADHIT.gadget=TextureSetLoadFile.gadget
				If GUI_GADHIT.gadget=TextureSetLoadFile.gadget Then FILE$=GUI_FILEREQUEST("Please Select a Texture Set to load",".set#","TextureSets\")
				ChangeDir StartDir$
				If GUI_GADHIT.gadget=TextureSetNewFile.gadget Or FileType(FILE$)<>1 Then FILE$="TextureSets\Default.set"
				ChangeDir StartDir$				
				FIL=ReadFile(FILE$)
				CurrentLine$=ReadLine$(FIL)
				GUI_SETTEXT(SnowInputFile.gadget,CurrentLine$)
				GUI_SETTEXT(SnowTexture.gadget,CurrentLine$)
				CurrentLine$=ReadLine$(FIL)
				GUI_SETTEXT(StoneInputFile.gadget,CurrentLine$)
				GUI_SETTEXT(StoneTexture.gadget,CurrentLine$)
				CurrentLine$=ReadLine$(FIL)
				GUI_SETTEXT(GrassInputFile.gadget,CurrentLine$)
				GUI_SETTEXT(GrassTexture.gadget,CurrentLine$)
				CurrentLine$=ReadLine$(FIL)
				GUI_SETTEXT(SandInputFile.gadget,CurrentLine$)
				GUI_SETTEXT(SandTexture.gadget,CurrentLine$)
				CurrentLineInt=ReadByte(FIL)
				GUI_SETVAL(SnowStartSlider.gadget,CurrentLineInt)
				CurrentLineInt=ReadByte(FIL)
				GUI_SETVAL(SnowRangeSlider.gadget,CurrentLineInt)
				CurrentLineInt=ReadByte(FIL)
				GUI_SETVAL(StoneStartSlider.gadget,CurrentLineInt)
				CurrentLineInt=ReadByte(FIL)
				GUI_SETVAL(StoneRangeSlider.gadget,CurrentLineInt)
				CurrentLineInt=ReadByte(FIL)
				GUI_SETVAL(GrassRangeSlider.gadget,CurrentLineInt)
				CurrentLineInt=ReadByte(FIL)
				GUI_SETVAL(GrassStartSlider.gadget,CurrentLineInt)
				CurrentLineInt=ReadByte(FIL)
				GUI_SETVAL(SandRangeSlider.gadget,CurrentLineInt)
				CloseFile FIL
				
				mymaps(0)=LoadImage(SnowInputFile\txtinput\txt$)
				mymaps(1)=LoadImage(StoneInputFile\txtinput\txt$)
				mymaps(2)=LoadImage(GrassInputFile\txtinput\txt$)
				mymaps(3)=LoadImage(SandInputFile\txtinput\txt$)
				
				snowlev=SnowStartSlider\slider\val
				snowblend=SnowRangeSlider\slider\val
				stonelev=StoneStartSlider\slider\val
				stoneblend=StoneRangeSlider\slider\val
				grasslev=GrassStartSlider\slider\val
				grassblend=GrassRangeSlider\slider\val
				sandblend=SandRangeSlider\slider\val

				GUI_SETTEXT(TextureSetInputFile.gadget,FILE$)
				
			Case GUI_GADHIT.gadget=TextureSetSaveFile.gadget Or GUI_GADHIT.gadget=TextureSetSaveAsFile.gadget
				If GUI_GADHIT.gadget=TextureSetSaveAsFile.gadget 
					FILE$=GUI_FILEREQUEST("Please Select a destination for the Texture",".set#","TextureSets\")
				Else
					FILE$=TextureSetInputFile\txtinput\txt$
				EndIf
;				If GUI_GADHIT.gadget=TextureSetSaveFile.gadget Then FILE$=TextureSetInputFile\txtinput\txt$
				ChangeDir StartDir$
				If Right(FILE$,1)="\" Or Right(FILE$,1)="" Then FILE$=FILE$+"noname"
				If Right$(FILE$,4)<>".set" Then FILE$=FILE$+".set"				
				PresetFile=WriteFile(FILE$)
					WriteLine(PresetFile,SnowInputFile\txtinput\txt$)
					WriteLine(PresetFile,StoneInputFile\txtinput\txt$)
					WriteLine(PresetFile,GrassInputFile\txtinput\txt$)
					WriteLine(PresetFile,SandInputFile\txtinput\txt$)
					WriteByte(PresetFile,snowlev)
					WriteByte(PresetFile,snowblend)
					WriteByte(PresetFile,stonelev)
					WriteByte(PresetFile,stoneblend)
					WriteByte(PresetFile,grasslev)
					WriteByte(PresetFile,grassblend)
					WriteByte(PresetFile,sandblend)
				CloseFile PresetFile
				GUI_SETTEXT(TextureSetInputFile.gadget,FILE$)
			Case GUI_GADHIT.gadget=Credits.gadget
				GUI_SHOWWIN(About.Window)
				GUI_WINFRONT(About.Window)
				Back=False
				Repeat
					If GUI_GADHIT.gadget=Backtowork.gadget
						GUI_HIDEWIN(About.Window)
						FlushMouse()
						back=True
					EndIf
					GUI()
					Flip
				Until back=True
			Case GUI_GADHIT.gadget=Help.gadget
				GUI_SHOWWIN(HelpPopUp.Window)
				GUI_WINFRONT(HelpPopUp.Window)
				Back=False
				Repeat
					If GUI_GADHIT.gadget=Backtowork2.gadget
						GUI_HIDEWIN(HelpPopUp.Window)
						FlushMouse()
						back=True
					EndIf
					GUI()
					Flip
				Until back=True
			Case GUI_GADHIT.gadget=quit.gadget Or KeyDown(1)
				GUI_SHOWWIN(AreYouSure.Window)
				GUI_WINFRONT(AreYouSure.Window)
				Back=False
				Repeat
					Select True
						Case GUI_GADHIT.gadget=yes.gadget
							ExitProgram=True
						Case GUI_GADHIT.gadget=no.gadget
							GUI_HIDEWIN(AreYouSure.Window)
							FlushMouse()
							back=True
					End Select
					GUI()
					Flip
				Until ExitProgram=True Or back=True

			Case GUI_GADHIT.gadget=Preview3D.gadget
				GUI_HIDEWIN(Blending.Window)
				GUI_HIDEWIN(HeightmapLoad.Window)
				GUI_HIDEWIN(Colourmap.Window)
				GUI_HIDEWIN(TextureSet.Window)
				GUI_HIDEWIN(Main.Window)
				GUI_HIDEWIN(LandscapeControls.Window)
			;-----------3D PREVIEW CODE------------
				;Ambient Light
				AmbientLight 255,255,255
				
				;Load Terrain
				terrain=LoadTerrain(HeightMapTexture\IMAGEBOX\FILE$)
				ChangeDir StartDir$
				CMap=LoadTexture(ColourMapTexture\IMAGEBOX\FILE$)
				ScaleTexture CMap,TerrainSize(terrain),TerrainSize(terrain)
				EntityTexture terrain,CMap
				ScaleEntity terrain,2,400,2
				TerrainDetail terrain,3000,True
				TerrainShading terrain,True
				PositionEntity terrain,-TerrainSize(terrain),0,-TerrainSize(terrain)

				TerrainPitch=CreatePivot()
				TerrainYaw=CreatePivot(TerrainPitch)
				PositionEntity TerrainPitch,0,0,0
				EntityParent terrain,TerrainYaw
				RotateEntity terrainPitch,-10,0,0
								
				;Init Camera
				Camera=CreateCamera()
				SetBuffer FrontBuffer()
				PositionEntity camera,0,0,-2000
				TurnEntity camera,0,0,0
				CameraRange camera,0.01,10000
				CameraFogMode camera,1
				CameraFogRange camera,200,8000
				CameraFogColor camera,240,230,200
				CameraClsColor camera,54,71,110
				CameraViewport camera,0,0,GraphicsWidth(),GraphicsHeight()
				Zoom#=1

				QuitPreview.Window=GUI_WINDOW.window(GraphicsWidth()/2-310,20,620,40,"3D PREVIEW... ( press [ESC] to return )",1,0,0,0,0)
				If Right(ColourMapTexture\IMAGEBOX\FILE$,11)<>"preview.bm_"
					QuitPreviewText.gadget=GUI_TEXT.gadget(QuitPreview.Window,150,20,580,"","3D LANDSCAPE PREVIEW - FULL DETAIL","")
				Else
					QuitPreviewText.gadget=GUI_TEXT.gadget(QuitPreview.Window,20,20,580,"","3D PREVIEW - LOW DETAIL TEXTURE (SELECT 'RENDER TO FILE...' FOR HIGH DETAIL)","")
				EndIf
				GUI_OPENWIN(QuitPreview.Window)

				PreviewControls.Window=GUI_WINDOW.window(GraphicsWidth()-340,GraphicsHeight()-120,330,110,"3D PREVIEW CONTROLS",1,0,1,0,0)
				PreviewFrame.gadget=GUI_FRAME.gadget(PreviewControls.Window,10,20,280,75,"TERRAIN HEIGHT AND DETAIL LEVEL",0,"")
				PreviewDetailText.gadget=GUI_TEXT.gadget(PreviewControls.Window,20,35,200,10,"Terrain Detail level:","")
				PreviewDetailSlider.gadget=GUI_SLIDER.gadget(PreviewControls.Window,20,50,180,2000,1,10000,1,1,"pull slider")
				PreviewDetailValue.gadget=GUI_TXTINPUT.gadget(PreviewControls.Window,230,47,53,PreviewDetailSlider\slider\val,"",0,0,"")
				PreviewHeightText.gadget=GUI_TEXT.gadget(PreviewControls.Window,20,65,200,10,"Terrain Height:","")
				PreviewHeightSlider.gadget=GUI_SLIDER.gadget(PreviewControls.Window,20,80,180,400,1,1000,1,1,"pull slider")
				PreviewHeightValue.gadget=GUI_TXTINPUT.gadget(PreviewControls.Window,230,77,53,PreviewHeightSlider\slider\val,"",0,0,"")
				PreviewZoomText.gadget=GUI_TEXT.gadget(PreviewControls.Window,295,37,20,90,"Z O O M","")
				PreviewZoomSlider.gadget=GUI_SLIDER.gadget(PreviewControls.Window,310,26,52,2000,0,8000,0,1,"pull slider")
				GUI_OPENWIN(PreviewControls.Window)
				
				While KeyDown(1)=False
					
					If MouseDown(2)
						If LookStart=False
							If MouseHit(2)=1 Then oldxmouse=MouseX() : oldymouse=MouseY()
							MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
							HidePointer()
							Lookstart=True
							GUI_MOUSE_ON=0							
						EndIf
			
						; Mouse x and y speed
						mxs=MouseXSpeed()/4
						mys=MouseYSpeed()/4
				
						; Destination camera angle x and y values
						dest_cam_pitch#=limit(dest_cam_pitch#+mys,-90,-10)
						dest_cam_yaw#=dest_cam_yaw#-mxs
									
						; Current camera angle x and y values
						cam_yaw#=cam_yaw+(dest_cam_yaw-cam_yaw)/2
						cam_pitch#=cam_pitch+(dest_cam_pitch-cam_pitch)/2

						RotateEntity terrainPitch,cam_pitch#,0,0
						RotateEntity terrainYaw,0,cam_yaw#,0
						
						MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
						MovePointer=True

					Else
						If LookStart=True And MouseDown(2)=False
							Lookstart=False
							MoveMouse oldxmouse,oldymouse
							ShowPointer()
							If Not Windowed Then GUI_MOUSE_ON=1
						EndIf
						MovePointer=False
						FlushMouse()
					EndIf

					PositionEntity camera,EntityX(camera),EntityY(camera),-PreviewZoomSlider\slider\val
					
					GUI_SETVAL(PreviewZoomSlider.gadget,PreviewZoomSlider\slider\val-MouseZSpeed()*100)
					
				RenderWorld tween
				
				If MouseDown(1) And (GUI_GADHIT.gadget=PreviewDetailSlider.gadget Or GUI_GADHIT.gadget=PreviewHeightSlider.gadget)
					GUI_SETTEXT(PreviewDetailValue.gadget,PreviewDetailSlider\slider\val)
					TerrainDetail terrain,PreviewDetailSlider\slider\val,True
					GUI_SETTEXT(PreviewHeightValue.gadget,PreviewHeightSlider\slider\val)
					ScaleEntity terrain,2,PreviewHeightSlider\slider\val,2
				EndIf

				GUI()
				If MovePointer Then Text GraphicsWidth()/2,GraphicsHeight()/2,"+"
				Flip
				Wend
			;--------END OF 3D PREVIEW CODE--------

				FreeEntity Terrain
				FreeEntity Camera
				FreeEntity TerrainPitch			
				GUI_FREEWIN(QuitPreview.Window)
				GUI_FREEWIN(PreviewControls.Window)
				
				GUI_OPENWIN(Blending.Window)
				GUI_OPENWIN(HeightmapLoad.Window)
				GUI_OPENWIN(Colourmap.Window)
				GUI_OPENWIN(TextureSet.Window)
				GUI_OPENWIN(Main.Window)
				GUI_OPENWIN(LandscapeControls.Window)
				FlushKeys()
				FlushMouse()

		End Select

		If GUI_WINHOVER.WINDOW<>Null 
			If MouseDown(1) Then GUI_WINFRONT(GUI_WINHOVER.WINDOW)
		EndIf
		
		snowlev=SnowStartSlider\slider\val
		snowblend=SnowRangeSlider\slider\val
		stonelev=StoneStartSlider\slider\val
		stoneblend=StoneRangeSlider\slider\val
		grasslev=GrassStartSlider\slider\val
		grassblend=GrassRangeSlider\slider\val
		sandblend=SandRangeSlider\slider\val
	
	If MouseDown(1) And (GUI_GADHIT.gadget=SnowStartSlider.gadget Or GUI_GADHIT.gadget=SnowRangeSlider.gadget Or GUI_GADHIT.gadget=StoneStartSlider.gadget Or GUI_GADHIT.gadget=StoneRangeSlider.gadget Or GUI_GADHIT.gadget=GrassStartSlider.gadget Or GUI_GADHIT.gadget=GrassRangeSlider.gadget Or GUI_GADHIT.gadget=SandRangeSlider.gadget)
	GUI_SETTEXT(SnowStartValue.gadget,SnowStartSlider\slider\val)
	GUI_SETTEXT(SnowRangeValue.gadget,SnowRangeSlider\slider\val)
	
	GUI_SETTEXT(StoneStartValue.gadget,StoneStartSlider\slider\val)
	GUI_SETTEXT(StoneRangeValue.gadget,StoneRangeSlider\slider\val)
	
	GUI_SETTEXT(GrassStartValue.gadget,GrassStartSlider\slider\val)
	GUI_SETTEXT(GrassRangeValue.gadget,GrassRangeSlider\slider\val)
	
	GUI_SETTEXT(SandRangeValue.gadget,SandRangeSlider\slider\val)
	EndIf

	GUI()
	FlushMouse()
	Flip

Wend

Function ProcessColourmap(Heightmapfile$,Colourmap$,Preview=False)
	Render.Window=GUI_WINDOW.window(GraphicsWidth()/2-300,GraphicsHeight()/2-20,600,40,"RENDERING IN PROGRESS... ( [ESC] To abort )",1,0,0,0,0)
	RenderText.gadget=GUI_TEXT.gadget(Render.Window,190,20,560,"","Completed: ","")
	RenderPercentage.gadget=GUI_PROG(Render.Window,270,20,0,100,"")
	GUI_OPENWIN(Render.Window)

	snow=0 : stone=1 : grass=2 : sand=3
	oldpercent#=0.0 : newpercent#=0.0

	ChangeDir StartDir$
	heightmap=LoadImage(Heightmapfile$)

	If Preview=True Then ScaleImage heightmap,.20,.20
		
	resultmap=CreateImage(ImageWidth(heightmap),ImageHeight(heightmap))
	heightwidth#=ImageWidth(heightmap)
	For x=0 To (ImageWidth(heightmap)-1)
    	For y=0 To (ImageHeight(heightmap)-1)
            
        	SetBuffer ImageBuffer(heightmap)
	        LockBuffer ImageBuffer(heightmap)
    	    GetColor(x,y)
        	    height#=ColorGreen() ; Pick out one colour channel. I use green
					            	 ; because the generator I use seems to vary
						             ; green more than the others.
    	    UnlockBuffer ImageBuffer(heightmap)

	        newtexnumber=-2
    	    If height# >= snowlev Then
        	    texnumber=snow
            	If height#<=snowlev+snowblend
                	newtexnumber=stone
	                fade#=(height#-Float snowlev)/snowblend
    	        EndIf
        	ElseIf height# >= stonelev
	            texnumber=stone
    	        If height#<=stonelev+stoneblend
        	        newtexnumber=grass
            	    fade#=(height#-Float stonelev)/Float stoneblend
	            EndIf
    	    ElseIf height# >= grasslev
        	    texnumber=grass
            	If height#<=grasslev+grassblend
                	newtexnumber=sand
	                fade#=(height#-Float grasslev)/Float grassblend
    	        EndIf

        	Else
            	texnumber=sand
	            If height#<=sandblend
    	            newtexnumber=-1
        	        fade#=height#/Float sandblend
            	EndIf
	        EndIf

    	    SetBuffer ImageBuffer(mymaps(texnumber))
        	GetColor x Mod ImageWidth(mymaps(texnumber)),y Mod ImageHeight(mymaps(texnumber))
	        oldred#=ColorRed()
    	    oldgreen#=ColorGreen()
        	oldblue#=ColorBlue()
	        If newtexnumber>-1
	            SetBuffer ImageBuffer(mymaps(newtexnumber))
    	        GetColor x Mod ImageWidth(mymaps(newtexnumber)),y Mod ImageHeight(mymaps(newtexnumber))
        	    newred#=ColorRed()
            	newgreen#=ColorGreen()
	            newblue#=ColorBlue()
    	        oldred#=(oldred#*fade#)+(newred#*(1-fade#))
        	    oldgreen#=(oldgreen#*fade#)+(newgreen#*(1-fade#))
            	oldblue#=(oldblue#*fade#)+(newblue#*(1-fade#))
	        Else If newtexnumber=-1
	;	        If fade#<0.5 Then fade#=0.5
	            oldred#=oldred#*fade#
    	        oldgreen#=oldgreen#*fade#
        	    If fade#<0.5 Then fade#=0.5
            	oldblue#=oldblue#*fade#
	        EndIf
            
	        SetBuffer ImageBuffer(resultmap)
    	    Color oldred#,oldgreen#,oldblue#
        	Plot x,y
        
        Next
    	SetBuffer FrontBuffer()
	    complete#= Float x / heightwidth * 100
		GUI_SETVAL(RenderPercentage.gadget,Int complete#)
    	SetBuffer BackBuffer()
		GUI()
		Flip
    	SetBuffer FrontBuffer()
		RenderWorld

		If KeyDown(1)
			Exit
		EndIf

	Next

	GUI_FREEWIN(Render.Window)
	If Preview=True 
		SaveBuffer(ImageBuffer(resultmap),"preview.bm_") 
	Else 
		SaveBuffer(ImageBuffer(resultmap),Colourmap$)
	EndIf
	
End Function

Function limit#(number#,min#,max#)
	If number#<min# Then number#=min#
	If number#>max# Then number#=max#
	Return number#
End Function

End
