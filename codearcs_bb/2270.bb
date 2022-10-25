; ID: 2270
; Author: Bobysait
; Date: 2008-06-12 12:17:32
; Title: Generate Nice Terrain+Lightmap
; Description: Terrain Generator , with ColorMap and ultra fast Lightmaping

Graphics3D 800,600,0,2
SetBuffer BackBuffer()

Local World%=	CreatePivot		();

Local piv	=	CreatePivot		(World);
Local cam	=	CreateCamera	(piv);
Local CamY#	=	10.0
				PositionEntity	(cam, 0,CamY,0,0);
				CameraRange		(cam, 1,10000);
				CameraClsColor	(cam, 100,150,255);

Local Sun%	=	CreateSphere	(10,World)
				PositionEntity	(Sun,1000,500,1000)
				ScaleEntity		(Sun,15,15,15)
				EntityColor		(Sun,250,230,220)
				EntityFX		(Sun,1)
				PointEntity		(Sun,World)
Local TSize%=	512
Local Terr	=	CreateTerrain	(TSize,World)
				Generation_Terrain(Terr,4,250,4,150,.01,.15,20,TSize/3)
				TerrainColore	(Terr)
;				ExtractLightMap	(Terr,4,250,Sun)
				EntityFX		(Terr,1)

Local Plane%=	CreatePlane		(1,World)
				EntityColor		(Plane,80,50,10)
				EntityFX		(Plane,1)

Local Sea%	=	CreatePlane		(1,World)
				EntityColor		(Sea,10,50,100)
				EntityFX		(Sea,1)
				EntityAlpha		(Sea,.5)
				MoveEntity		(Sea,0,250*.08,0)

Local MainTime%=MilliSecs()
Local OldTime%=0
Repeat
	msx=MouseXSpeed()
	msy=MouseYSpeed()
	msz=MouseZSpeed()
	TurnEntity piv,0,-msx,0
	TurnEntity cam,+msy,0,0
	Local vz# = Float ( ( ( KeyDown(200)+KeyDown(17) ) > 0 ) - ( ( KeyDown(208)+KeyDown(31) ) > 0 ) )
	Local vx# = Float ( ( ( KeyDown(205)+KeyDown(32) ) > 0 ) - ( ( KeyDown(203)+KeyDown(30) ) > 0 ) )
	MoveEntity piv,.1*vx*CamY,0,.1*vz*CamY
	Px#=EntityX(piv,1)
	Pz#=EntityZ(piv,1)
	Py#=TerrainY(Terr,Px,0,Pz)
	PositionEntity piv,pX,Py,Pz
	If msz CamY=CamY+CamY*.1*Float(msz):PositionEntity cam,0,CamY,0,0

	Time=MilliSecs()-MainTime
	; 4*360° / Min
		AT%=Float(time)*.001
		Tour%=4
		Dec#=.360
		Ang#=Dec*Float(AT*Tour)
		Rayon#=1500.0
		SunPosX#=Rayon*Cos(Ang):SunPosZ#=Rayon*Sin(Ang)
		PositionEntity Sun,SunPosX,500,SunPosZ,1:PointEntity Sun,World

		If Time>OldTime	ExtractLightMap(Terr,4,250,Sun):OldTime=Time+5000

	MoveMouse 400,300
	RenderWorld
		Text 10,10,"Ang="+Ang
	Flip
Until KeyHit(1)
FreeEntity World
End

Function Generation_Terrain(Terrain%,sclX#,sclY#,sclZ#,nbc#,COEF_HAUT1#=.005,COEF_HAUT2#=.02,Zone1#=15,Zone2#=50, smoothterrain%=2)
	Local Taille#=TerrainSize(Terrain)
	SeedRnd (MilliSecs())
	Local a
	For a = 0 To nbc
		; varie la hauteur de coef1 à coef2 => plus la surface est grande, plus on adoucie !
		; inversement, plus la surface est petite, plus on generera de "pics"
		Local COEF_HAUT#	=	Rnd(COEF_HAUT1,COEF_HAUT2)
		Local ZONE#			=	Rnd(Zone1/2,Zone2/2);*COEF_HAUT*10
		
		Local Force# = 1.0 / (ZONE*COEF_HAUT);
		If Force<1 Then Force=1;
		If Force>10 Then Force=10;
		
		;ZONE=ZONE/2
		Local COEF#=90.0/ZONE
		Local pX#=Rand(ZONE+Taille/10,Taille#-ZONE-Taille/10)
		Local pZ#=Rand(ZONE+Taille/10,Taille#-ZONE-Taille/10)
		TFormPoint pX,0,pZ,0,Terrain : pX=TFormedX():pZ=TFormedZ()
		
		Local frc%
		For frc=1 To Force
			pX=pX+Rand(-ZONE/Force,ZONE/Force)
			pZ=pZ+Rand(-ZONE/Force,ZONE/Force)
			Local X#, Z#
			Local AH_X#, AH_Y#, AH_Z#, AH_T#
			For X = -ZONE To ZONE
				AH_X	=	Cos(X*COEF)*COEF_HAUT
				For Z# = -ZONE To ZONE
					AH_Z	=	Cos(Z*COEF)
					AH_T	=	AH_X * AH_Z + TerrainHeight(Terrain,pX+X,pZ+Z)
					If AH_T>1	Then AH_T=1;
					ModifyTerrain Terrain, pX+X,pZ+Z,AH_T
				Next
			Next
		Next
	Next
	
	For i = smoothterrain To TerrainSize(Terrain)-1-smoothterrain
		For j = smoothterrain To TerrainSize(Terrain)-1-smoothterrain
			Local sum# = 0.0, nb=0
			For tx = -smoothterrain To smoothterrain
				For ty = -smoothterrain To smoothterrain
					sum=sum+TerrainHeight(Terrain, i+tx,j+ty)
					nb=nb+1
				Next
			Next
			ModifyTerrain(Terrain, i,j, sum/nb, True);
		Next
	Next
	ScaleEntity (Terrain,sclX#,sclY#,sclZ#)
	MoveEntity	(Terrain,-sclX*Taille*.5,0,-sclZ*Taille*.5)

End Function


Function TerrainColore(Terrain%)
	Tsz	=	TerrainSize		(Terrain)
	Tex	=	CreateTexture	(Tsz,Tsz):ScaleTexture(Tex,Tsz,Tsz)
	CBuf%=	GraphicsBuffer	()
	TBuf%=	TextureBuffer	(Tex):SetBuffer(TBuf):LockBuffer(TBuf)
	For i = 0 To Tsz-1
		For j = 0 To Tsz-1
			AH_T#=TerrainHeight(Terrain,i,j)
			If AH_T<.08		; Sol->Mer => Sol->Sable
				Dh#=AH_T*1.0/.08	:R=080+030*Dh:G=050+030*Dh:B=010+040*Dh	; fin = 110 / 080 / 050
			ElseIf AH_T<.1	; Sable
				Dh#=(AH_T-.08)*1/.02:R=110+080*Dh:G=080+080*Dh:B=050+070*Dh	; fin = 190 / 160 / 120
			ElseIf AH_T<.15	; Herbe
				Dh#=(AH_T-.1)*1/.05	:R=190-170*Dh:G=160-120*Dh:B=120-110*Dh	; fin = 020 / 040 / 010
			ElseIf AH_T<.7	; Roche
				Dh#=(AH_T-.17)*1/.55:R=020+080*Dh:G=040+060*Dh:B=010+040*Dh	; fin = 120 / 100 / 050
			Else			; neige
				Dh#=(AH_T-.72)*1/.3	:R=120+020*Dh:G=100+050*Dh:B=050+160*Dh	; fin = 180 / 150/ 210
			EndIf
			If R<0 R=0
			If G<0 G=0
			If B<0 B=0
			WritePixelFast i,Tsz-j-1,R Shl(16) + G Shl(8) + B
		Next
	Next
	UnlockBuffer(TBuf):SetBuffer(CBuf)
	EntityTexture	(Terrain,Tex,0,0):FreeTexture(Tex)
End Function

Dim Terr_Shd#(0,0)
Function ExtractLightMap%(Terrain%,Scx#,Scy#,Sun%=0)
	Local Sz#	=	TerrainSize(Terrain)
	Dim Terr_Shd(Sz,Sz)
	Local LMap	=	CreateTexture(Sz,Sz):ScaleTexture(LMap,Sz,Sz):EntityTexture(Terrain,LMap,0,2)
	Local CBuff%=	GraphicsBuffer(),TBuff%=TextureBuffer(LMap)
	If Sun<>0	:TFormNormal(0,0,1,Sun,0)
	Else		:TFormNormal(-1,-.5,-.8,0,0)
	EndIf
	Local SunVx#=TFormedX(),SunVy#=TFormedY(),SunVz#=TFormedZ()
	Scy#=Scy/Scx
	For i = 0 To Sz-1
		For j = 0 To Sz-1
			dx#=0.0:dy#=0.0:dz#=0.0:dh#=TerrainHeight(Terrain,i,j)*Scy
			Repeat
				dx=dx+SunVx*.95:dy=dy+SunVy*.95:dz=dz+SunVz*.95
				If i+dx>=0 And i+dx<Sz And j+dz>=0 And j+dz<Sz
					TrY#=TerrainHeight(Terrain,i+dx,j+dz)*Scy
					If TrY>dh+dy Exit
					Terr_Shd(i+dx,j+dz)=.9-.4*(dh-Try)/Scy
				Else	:Exit
				EndIf
			Forever
		Next
	Next
	SetBuffer(TBuff):LockBuffer(TBuff)
	For i= 0 To Sz-1:For j= 0 To Sz-1
		If Terr_Shd(i,j)<>0	:rgb=255*Terr_Shd(i,j)
		Else				:rgb=255
		EndIf
		WritePixelFast i,Sz-j-1,RGB Shl(16) + RGB Shl(8) + RGB + 255 Shl(24)
	Next:Next
	UnlockBuffer(TBuff):SetBuffer CBuff
	FreeTexture			(LMap)
	Dim Terr_Shd(0,0)
End Function
