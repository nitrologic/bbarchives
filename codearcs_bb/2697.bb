; ID: 2697
; Author: Dimas
; Date: 2010-04-11 16:54:31
; Title: Fire/Flames Animation
; Description: A very nice fire/flames animation, and 2 examples of use.

;FIRE ANIMATION DEMO 1
;Created by AMpóstata 11-Abril-2010
;You need to download the zip file from: ;www.ampostata.org/Llamas/Fuego.zip

Dim tx(481)

Type fuego
	Field x#
	Field anim
	Field sp
End Type

Global numfire,logo,ancho,alto
ancho=800 : alto=600
AppTitle "FIRE FLAMES DEMO 1"
Graphics3D ancho,alto,32,2

numfire=ancho/66	;distance between flames. Change this number for more or less flasmes

inicia
intro
End

Function intro()
	x#=0
	anim=262
	sp=LoadSprite("anim\262.jpg")		;just load a sprite, so it can be cloned easily
	PositionEntity sp,-7,0,0
	ScaleSprite sp,1,1.5
	s#=numfire
	salto#=10/s
	For n#=0 To numfire				;create flames sprites
		fire.fuego = New fuego
		fire\x=n*(ancho/numfire)
		fire\sp=CopyEntity (sp)
		fire\anim=Rand(1,481)
		EntityTexture fire\sp,tx(fire\anim)
		x#=salto#*n#
		PositionEntity fire\sp,-5+x,-1.9,0	;x coordinate varies from -5 to +5
	Next
	
	SetBuffer BackBuffer()
	cam = CreateCamera()
	MoveEntity cam,0,0,-5 
	
	
	For y#=-5 To -2 Step 0.01			;move fires from out-botton to botton
		For fire.fuego=Each fuego
			fire\anim=fire\anim+1:If fire\anim=482 Then fire\anim=1
			EntityTexture fire\sp,tx(fire\anim)
			PositionEntity fire\sp,EntityX(fire\sp),y#,0
		Next
		RenderWorld
		VWait:Flip
	Next
	
	For y=alto+100 To -100 Step -2		;move my logo OVER the fire
		For fire.fuego=Each fuego
			fire\anim=fire\anim+1:If fire\anim=482 Then fire\anim=1
			EntityTexture fire\sp,tx(fire\anim)
		Next
		RenderWorld
		DrawImage logo,ancho/2,y
		VWait:Flip
	Next
	
	CameraClsMode cam,False,True		;this way, background will be not deleted by renderworld
	
	For y=alto+100 To -100 Step -2		;move my logo UNDER the fire
		For fire.fuego=Each fuego
			fire\anim=fire\anim+1:If fire\anim=482 Then fire\anim=1
			EntityTexture fire\sp,tx(fire\anim)
		Next
		Cls
		DrawImage logo,ancho/2,y
		RenderWorld
		VWait:Flip
	Next
	
	CameraClsMode cam,True,True		;back to normal renderworld mode
	
	
	For n=1 To 481 Step 1			;stop fire animation when last frame is reached
		For fire.fuego=Each fuego
			fire\anim=fire\anim+1:If fire\anim>481 Then fire\anim=1:HideEntity fire\sp
			EntityTexture fire\sp,tx(fire\anim)
		Next
		RenderWorld
		VWait:Flip
		If KeyHit(1)=True Then End
	Next
	
End Function



Function inicia()
	Text ancho/2,100,"<____________LOADING FIRE TEXTURES_____________>",1
	m#=0
	For n=1 To 481
		l$=""
		For b=0 To n/10:l$=l$+".":Next
		Locate ancho/2-195,120:Write l$
		l$=Trim(n)
		If Len(l$)=1 Then l$="00"+l$
		If Len(l$)=2 Then l$="0"+l$
		tx(n)=LoadTexture("anim/"+l$+".jpg")
		If tx(n)=0 Then Print "Error loading fire":WaitKey():End
	Next
	logo=LoadImage("logo.bmp")
	If logo=0 Then Print "Error loading logo":WaitKey():End
	
	MaskImage logo,75,40,40
	MidHandle logo
	
End Function
