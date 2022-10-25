; ID: 2205
; Author: Sauer
; Date: 2008-01-21 05:49:40
; Title: Map Generator
; Description: Creates maps for pen and paper RPG's

AppTitle "Legends of Arthonia Map Generator v1.0"
Graphics 640,480,32,2


Dim terrain$(6)
terrain$(1)="FOREST"
terrain$(2)="MOUNTS"
terrain$(3)="WATER"
terrain$(4)="PlAINS"
terrain$(5)="SWAMP"
terrain$(6)="DESERT"



Global x=24
Global y=24
 
Global back=CreateImage(481,481,1)
SetBuffer ImageBuffer(back)
Color 255,255,255
Rect 0,0,480,480,1
Color 0,0,0
Rect 0,0,481,481,0
For t=1 To 10
	Line 0,48*t,480,48*t
Next 
For u=1 To 10
	Line 48*u,0,48*u,480
Next

MaskImage back,255,0,0

SetBuffer BackBuffer()
While Not KeyHit(1)	 
	ClsColor 255,255,255
	Color 0,0,0
	DrawImage back,0,0
	Text 490,0,"Editor Key:"
	Text 490,12,"-Terrain"
	Text 490,24,"1=Woods"
	Text 490,36,"2=Rocks"
	Text 490,48,"3=Water"
	Text 490,60,"4=Grass"
	Text 490,72,"5=Swamp"
	Text 490,84,"6=Sand"
	Text 490,108,"-Walls/Unwalkable"
	Text 490,120,"7=Horizontal"
	Text 490,132,"8=Vertical"
	Text 490,144,"9=Corner"
	Text 490,156,"0=Unwalkable"
	Text 490,180,"-Custom"
	Text 490,192,"BACK=New custom"
	Text 490,204,"Z=Custom"
	Text 490,228,"-Height"
	Text 490,240,"F1=1"
	Text 490,252,"F2=2"
	Text 490,264,"F3=3"
	Text 490,276,"F4=4"
	Text 490,288,"F5=5"
	Text 490,300,"F6=6"
	Text 490,324,"-Other"
	Text 490,336,"SPACE=delete"
	Text 490,348,"S=save"
	Text 490,360,"L=Load"
	Text 490,372,"H=Help"
	;/////////////////////////////
	Color 128,128,128
	Text x,y,"X"
	If KeyHit(203)
		x=x-48
		If x<=24
			x=24
		EndIf 
	EndIf
	If KeyHit(205)
		x=x+48
		If x>=456
			x=456
		EndIf 
	EndIf 	
	If KeyHit(200)
		y=y-48
		If y<=24
			y=24
		EndIf
	EndIf
	If KeyHit(208)
		y=y+48
		If y>=456
			y=456
		EndIf 
	EndIf 
	;///////////////////////////////
	;TEXT
	key=GetKey()
	If key>48 And key<55
		SetBuffer ImageBuffer(back)
		Color 0,0,0
		Text x-24,y,terrain(key-48)
		SetBuffer BackBuffer()
	EndIf
	;WALLS, DELETE, UNWALKABLE 
	If key=32
		SetBuffer ImageBuffer(back)
		Color 255,255,255
		Rect x-23,y-23,47,47,1
		SetBuffer BackBuffer()	
	EndIf
	If key=48
		SetBuffer ImageBuffer(back)
		Color 0,0,0
		Rect x-23,y-23,46,46,1
		SetBuffer BackBuffer()
	EndIf 
	If key=55
		SetBuffer ImageBuffer(back)
		Color 0,0,0
		Rect x-23,y-5,47,10
		SetBuffer BackBuffer()
	EndIf
	If key=56
		SetBuffer ImageBuffer(back)
		Color 0,0,0
		Rect x-5,y-23,10,47
		SetBuffer BackBuffer()
	EndIf
	If key=57
		SetBuffer ImageBuffer(back)
		Color 0,0,0
		Rect x-5,y-23,10,47
		Rect x-23,y-5,47,10
		SetBuffer BackBuffer()
	EndIf
	;LEVEL
	
	If KeyHit(59)
		SetBuffer ImageBuffer(back)
		Color 128,128,128
		Text x-23,y-23,"1"
		SetBuffer BackBuffer()
	EndIf
	If KeyHit(60)
		SetBuffer ImageBuffer(back)
		Color 128,128,128
		Text x-23,y-23,"2"
		SetBuffer BackBuffer()
	EndIf
	If KeyHit(61)
		SetBuffer ImageBuffer(back)
		Color 128,128,128
		Text x-23,y-23,"3"
		SetBuffer BackBuffer()
	EndIf
	If KeyHit(62)
		SetBuffer ImageBuffer(back)
		Color 128,128,128
		Text x-23,y-23,"4"
		SetBuffer BackBuffer()
	EndIf
	If KeyHit(63)
		SetBuffer ImageBuffer(back)
		Color 128,128,128
		Text x-23,y-23,"5"
		SetBuffer BackBuffer()
	EndIf
	If KeyHit(64)
		SetBuffer ImageBuffer(back)
		Color 128,128,128
		Text x-23,y-23,"6"
		SetBuffer BackBuffer()
	EndIf

	;CUSTOM
	If KeyHit(14)
		custom=CreateWindow("New Custom Label",100,100,230,150,gamewindow,1)
		customname=CreateTextField(10,40,200,20,custom)
		CreateLabel("Enter custom entry (max. 6 characters)",10,10,200,20,custom)
		okbutton=CreateButton("OK",72,75,85,25,custom,1)
		ActivateGadget customname
		Repeat
			If WaitEvent()=$401
				If EventSource()=okbutton
					customstring$=Upper$(Left$(TextFieldText$(customname),6))
					SetBuffer ImageBuffer(back)
					Text x-23,y,customstring$
					FreeGadget custom
					SetBuffer BackBuffer()
					Exit
				EndIf
			EndIf
			If WaitEvent()=$803
				FreeGadget custom
				Exit 
			EndIf 
		Forever
	EndIf
	If KeyHit(44)
		SetBuffer ImageBuffer(back)
		Text x-23,y,customstring$
		SetBuffer BackBuffer()
	EndIf  
	
	;SAVE AND LOAD
	If KeyHit(31)  
		save=CreateWindow("Save File Name",100,100,230,150,gamewindow,1)
		savename=CreateTextField(10,40,200,20,save)
		CreateLabel("Enter the file name with suffix .bmp:",10,10,200,20,save)
		okbutton=CreateButton("OK",72,75,85,25,save,1) 
		ActivateGadget savename 
		Repeat
			If WaitEvent()=$401
				If EventSource()=okbutton
					ok=SaveImage(back,"c:"+TextFieldText$(savename)) 
					If ok=1
						Notify "File '"+TextFieldText$(savename)+"' successfully saved." 
					Else 
						Notify "Save error.",True
					EndIf 
					FreeGadget save
					Exit
				EndIf
			EndIf 
			If WaitEvent()=$803
				FreeGadget save
				Exit 
			EndIf 
		Forever		
	EndIf 
	If KeyHit(38)
		loadmap$=RequestFile("Load Map","bmp")
		If loadmap$<>""
			back=LoadImage(loadmap$)
			MaskImage back,255,0,0
		EndIf 
	EndIf 
	If KeyHit(35)
		help=CreateWindow("Help",100,100,400,400,gamewindow,1)
		helptext=CreateCanvas(0,0,400,400,help)
		SetBuffer CanvasBuffer(helptext)
		Color 128,128,128
		Text 200,0,"Legends of Arthonia Map Generator",True,False 
		Text 200,12,"By: Brian Sauer, 2008",True,False
		Text 200,36,"This utility is for the creation of maps",True,False
		Text 200,48,"for the use in the LEGENDS OF ARTHONIA",True,False
		Text 200,60,"RPG.  Use the key on the left to add map",True,False
		Text 200,72,"characters.  Maps can be saved and loaded",True,False
		Text 200,84,"as .bmp files.",True,False
		Text 200,108,"TIPS:",True,False
		Text 200,120,"-Use the UNWALKABLE surface to make ",True,False
		Text 200,132,"     dungeons and castle walls.",True,False
		Text 200,146,"-For shops, NPC's, and other specialty",True,False
		Text 200,158,"     characters, use the CUSTOM feature.",True,False
		Text 200,170,"-After creating maps, submit them to the",True,False
		Text 200,182,"     the DOF website for public display.",True,False
		Text 200,206,"If you have any questions, comments, or",True,False
		Text 200,218,"suggestions for this generator, please",True,False
		Text 200,230,"e-mail contact@dungeonsoffear.",True,False
		Text 200,254,"More Legends of Arthonia tools and game",True,False
		Text 200,266,"material at www.dungeonsoffear.com!",True,False
		FlipCanvas(helptext)
		SetBuffer BackBuffer()
		
		Repeat 
			If WaitEvent()=$803
				FreeGadget help
				Exit 
			EndIf
		Forever
	EndIf 	
	Flip
	Cls
Wend
