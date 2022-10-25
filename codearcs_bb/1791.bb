; ID: 1791
; Author: Bobysait
; Date: 2006-08-21 17:31:04
; Title: Projectional shadow system
; Description: fast project shadows for mesh ( not AnimMesh )  using a single surface mesh

;*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
; 											SMS LIB 
; 								©Bobysait
;
;
;						have Fun - Free for any use
;						Released "As is" without any waranty.
;
;*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
; see the demo at the end of the code



;======================================================================================================
;======================================================================================================
;||		||								-----------
;||		||								-Fonctions-
;||		||								-----------
;======================================================================================================
;======================================================================================================


;=========================================================
; 			=========== Constantes SMS ===========
Const C_SMS_MaxMesh%=1000
Const C_SMS_MaxSurf%=20
;==============================================


;=========================================================
;				 ========== Types SMS ==========
Type ENV_SMS_Layer
	Field SMS_Mesh%
	Field SMS_Surf%
	Field NbMesh%
	Field HandleMesh%[C_SMS_MaxMesh]
	Field Statik%[C_SMS_MaxMesh]
	Field Anim%[C_SMS_MaxMesh]
	Field SMS_Rate%						; Contrôle du rafraichissement.
	Field MajTime%
	Field st%
	Field lt%
	Field dt%
	Field GroundR%
	Field GroundG%
	Field GroundB%
	Field Vrt_X#[3]
	Field Vrt_Y#[3]
	Field Vrt_Z#[3]
	Field Vrt_dX#[3]
	Field Vrt_dY#[3]
	Field Vrt_dZ#[3]
	Field Actif%						; variable pour l'affichage des ombres simples 
								; ( pour ne pas refaire le maillage à chaque boucle)
								; comme il s'agit d'ombre statique, inutile de les recréer sans cesse !
End Type


Type ENV_SMS_Mesh
	Field Mesh%
	Field NbSurf%
	Field IdSurf%[C_SMS_MaxSurf]
	Field HandleSurf%[C_SMS_MaxSurf]
	Field NbTris%
	Field ColR,ColG,ColB
End Type


Type ENV_SMS_Surf
	Field Surf%
	Field CountTris%
	Field Vrt1%[65536]
	Field Vrt2%[65536]
	Field Vrt3%[65536]
End Type


Type ENV_SMS_BoxMesh
	Field Mesh
	Field Pointx#[4]
	Field Pointy#[4]
	Field Pointz#[4]
End Type
;==============================================



;=========================================================
; 		=========== Initialisation SMS ============
;		===========   Initialise SMS   ============
;=========================================================

Function SMS_Init(World%)
	f.ENV_SMS_Layer=New ENV_SMS_Layer
		If world	=	0
			f\SMS_Mesh=CreateMesh()
		Else
			f\SMS_Mesh=CreateMesh(World)
		EndIf
		f\SMS_surf=CreateSurface(f\SMS_Mesh)
		EntityFX f\SMS_Mesh,1+2+8
		f\st%=MilliSecs()
		f\SMS_Rate%=16	; 60 Hetz ( 1000 / 60 ) => 16 milliSecondes
		ImgX=32
		tex=CreateTexture(ImgX,1,3)
			currentbuffer=GraphicsBuffer()
			SetBuffer TextureBuffer(tex)
				For i = 0 To ImgX
					Color 255-i*255/ImgX,255-i*255/ImgX,255-i*255/ImgX
					Rect i,0,1,1
				Next
			SetBuffer currentbuffer
			EntityTexture f\SMS_Mesh,tex
End Function






;=========================================================
; 	=========== Ajout d'un mesh dans le SMS ============
;       =========== Add a mesh to the SMS ============
;=========================================================

Function SMS_Add_Emetteur(Mesh,World%=0,ColR%=128,ColG%=128,ColB%=128)

	; avant tout on verifie que le mesh existe, et qu'il a de quoi projeté des ombres.
	; We first have to check for existing mesh.
		; <!> ============== mesh inexistant...
		; mesh does not exist
			If mesh=0 Return False
			NbSurf=CountSurfaces(mesh)
			; ========== <!>
		; <!> ============== pas de surface ...
		; no surface
			If nbsurf=0 Return False
			For ns= 1 To NbSurf
				srf=GetSurface(mesh,ns)
				CountTris=CountTriangles (srf)
				CntTri=CntTri+CountTris
				If CountTris>0 CountSurfForMesh=CountSurfForMesh+1
			Next
			; ========== <!>
		; <!> ============== surfaces vides ...
		; empty surface
			If CntTri=0 Return False
			; ========== <!>

	; le mesh est correct, on l'enregistre !
	; If mesh is Ok, we reg. it.
		f.ENV_SMS_Layer=Last ENV_SMS_Layer
			; <!> ============== SMS non initialisé ? alors faisons le !
			; SMS not initialised ? Let's do it !
				If f=Null 
					SMS_Init(World)
					f.ENV_SMS_Layer=Last ENV_SMS_Layer
				EndIf
				; ========== <!>
			; c'est parti !
			; let's go !
				f\NbMesh=f\NbMesh+1
				m.ENV_SMS_Mesh=New ENV_SMS_Mesh
					m\ColR%=ColR
					m\ColG%=ColG
					m\ColB%=ColB
					f\HandleMesh[f\NbMesh]=Handle(m)
					m\Mesh=Mesh
					m\NBSurf=CountSurfForMesh
					For ns= 1 To nbsurf
						srf=GetSurface(mesh,ns)
						CountTris=CountTriangles(srf)
						If countTris<>0
							CurIdSurf=CurIdSurf+1
							s.ENV_SMS_Surf=New ENV_SMS_Surf
							m\HandleSurf[CurIdSurf]=Handle(s)
							s\Surf=srf
							s\CountTris=CountTris
							For Nt= 0 To CountTris-1
								s\Vrt1[Nt]=TriangleVertex(srf,Nt,0)
								s\Vrt2[Nt]=TriangleVertex(srf,Nt,1)
								s\Vrt3[Nt]=TriangleVertex(srf,Nt,2)
							Next
						EndIf
					Next

End Function



Function SMS_Free_Emetteur(Mesh)
	For m.ENV_SMS_Mesh=Each ENV_SMS_Mesh
		If m\Mesh	=	mesh
			Delete m
			Exit
		EndIf
	Next
End Function


;=========================================================
; 	=========== Couleur du Sol ============	; à finir !!!
; 	=========== Ground Color ============
;=========================================================


Function SMS_Define_GroundColor(R%,G%,B%)
	f.ENV_SMS_Layer=Last ENV_SMS_Layer
		F\GroundR%=R
		F\GroundG%=G
		F\GroundB%=B
End Function





Function SMS_RateTime%(f.ENV_SMS_Layer)
	Local l_mt=MilliSecs()
	l_dmt=f\lt+f\st
	f\dt%=l_mt-l_dmt
	If f\dt<2 f\dt=1

	f\MajTime=f\MajTime+f\Dt

	If f\MajTime>f\SMS_Rate
		f\MajTime=0
		f\st=MilliSecs()
		f\lt=0
		Return True
	EndIf
	f\lt#=f\lt+f\dt
	Return False
End Function


Function SMS_SetMajRate(Rate%)
	f.ENV_SMS_Layer = Last ENV_SMS_Layer
	f\SMS_Rate%=Rate
End Function




;=========================================================
; 		=========== Mise à Jour du SMS ============
; 		 ===========  Update The SMS  ============
;=========================================================

Function SMS_Update(Light%,Camera,Methode,Range%=1000)
	f.ENV_SMS_Layer = Last ENV_SMS_Layer
	If SMS_RateTime(f)=True
	Select methode
		Case 0
			SMS_NoUpdate():f\Actif=0
		Case 1
			If f\Actif=0 SMS_Simple_Shader(Light%,Camera):f\Actif=1
		Case 2
			SMS_Projection_Shader(Light%,Camera,Range):f\Actif=0
	End Select
	EndIf
End Function




Function SMS_Simple_Shader(Light%,Camera)
	f.ENV_SMS_Layer = Last ENV_SMS_Layer
			l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
			If l=Null Return False
			If f\NbMesh>0
				ClearSurface f\SMS_surf,1,1
				For nm = 1 To f\NbMesh
					m.ENV_SMS_Mesh=Object.ENV_SMS_Mesh(f\HandleMesh[nm])
						EntityColor f\SMS_Mesh,f\GroundR-m\ColR,f\GroundG-m\ColG,f\GroundB-m\ColB
						EntityFX f\SMS_Mesh,1+2
						If m<>Null
							mx#=EntityX(m\mesh,1)
							my#=EntityY(m\mesh,1)
							mz#=EntityZ(m\mesh,1)
							TFormPoint mx,my,mz,0,camera
							If TFormedZ()>0
							For ns = 1 To m\NbSurf
								s.ENV_SMS_Surf=Object.ENV_SMS_Surf(m\handleSurf[ns])
								surf=s\Surf
								NbTris=s\CountTris
								For Nt=0 To NbTris-1
									enableTri=True
									For nv=0 To 2
										Vrt%=	TriangleVertex(surf,Nt,nv)
										y#	=	VertexY(surf,Vrt)
										x#	=	VertexX(surf,Vrt)
										z#	=	VertexZ(surf,Vrt)
										TFormPoint x#,y#,z#,m\mesh,0
										y#	=	TFormedY()/Vy#
										If y#>=0
											x#	=	TFormedX()
											z#	=	TFormedZ()
											Vrt	=	AddVertex (f\SMS_surf,x#,0.01,z#)
													VertexNormal f\SMS_surf,Vrt,0,1,0
										Else
											enableTri=False
										EndIf
									Next
									If enableTri=True AddTriangle f\SMS_surf,Vrt-2,Vrt-1,Vrt
								Next
							Next
						EndIf
					EndIf
				Next
			EndIf
End Function





Function SMS_Projection_Shader(Light%,Camera,Range%=1000)
	f.ENV_SMS_Layer = Last ENV_SMS_Layer
			l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
			If l=Null Return False
			L_Lx#	=	EntityX(l\light,1)
			L_Ly#	=	EntityY(l\light,1)
			L_Lz#	=	EntityZ(l\light,1)
			l_FColR	=	f\GroundR
			l_FColG	=	f\GroundG
			l_FColB	=	f\GroundB
			L_Range#=	l\Range
			EntityFX f\SMS_Mesh,1+2
			If f\NbMesh>0
				ClearSurface f\SMS_surf,1,1
				For nm = 1 To f\NbMesh
					m.ENV_SMS_Mesh=Object.ENV_SMS_Mesh(f\HandleMesh[nm])
					If m<>Null
						DistMesh	=	EntityDistance(m\mesh,camera)
						If Abs DistMesh>Range
						Else
							mx#		=	EntityX(m\mesh,1)
							my#		=	EntityY(m\mesh,1)
							mz#		=	EntityZ(m\mesh,1)
							l_ty#	=	MeshHeight(m\mesh)*2
							l_NColR	=	m\ColR
							l_NColG	=	m\ColG
							l_NColB	=	m\ColB
							TFormPoint mx,my,mz,0,camera
							If TFormedZ()>0
							For ns = 1 To m\NbSurf
								s.ENV_SMS_Surf=Object.ENV_SMS_Surf(m\handleSurf[ns])
								surf=s\Surf
								NbTris=s\CountTris
								For Nt=0 To NbTris-1
									enableTri=True
									For nv=0 To 2
										Select nv
											Case 0
												Vrt=s\Vrt1[Nt]
											Case 1
												Vrt=s\Vrt2[Nt]
											Case 2
												Vrt=s\Vrt3[Nt]
										End Select
										dy#	=	VertexY(surf,Vrt)
										dx#	=	VertexX(surf,Vrt)
										dz#	=	VertexZ(surf,Vrt)
										TFormPoint dx#,dy#,dz#,m\mesh,0
										ddy#	=	TFormedY()
										Vy#	=	Abs(l_ly-ddy)
										dy#	=	ddy/Vy
										If Dy#>=0
											dx#	=	TFormedX()
											dz#	=	TFormedZ()
											Vx#	=	l_lx-dx
											Vz#	=	l_lz-dz
											x#	=	dx#-Vx*dy
											z#	=	dz#-Vz*dy

											ddY#	=	(ddy)/l_ty
											Coef#	=	SMS_LightCoef#(x,z,l_lx,l_lz,l_Range)
											Vrt	=	AddVertex (f\SMS_surf,x,ddy/50,z)
									ColR=SMS_SmoothColor(l_NColR,l_FColR,ddy,coef)
									ColG=SMS_SmoothColor(l_NColG,l_FColG,ddy,coef)
									ColB=SMS_SmoothColor(l_NColB,l_FColB,ddy,coef)
									VertexColor f\SMS_surf,Vrt,ColR,ColG,ColB
										Else
											enableTri=False
										EndIf
									Next
									If enableTri=True AddTriangle f\SMS_surf,Vrt-2,Vrt-1,Vrt
								Next
							Next
							EndIf
						EndIf
					EndIf
				Next
			EndIf
End Function




Function SMS_NoUpdate()
	f.ENV_SMS_Layer = Last ENV_SMS_Layer
	If f\NbMesh>0
		ClearSurface f\SMS_surf,1,1
	EndIf
End Function 




Function SMS_Distance#(dd#,Range#)
	If dd>range Return 1
	If dd<0 Return 0
	coef#=Float(dd)/Float(Range)
	Return coef#
End Function



Global G_Coef#
Function SMS_LightCoef#(X1#,Y1#,X2#,Y2#,Range#)
	Local ddx#=(x1-x2)*(x1-x2)
	Local ddz#=(z1-z2)*(z1-z2)
	Local dd#=ddx+ddz
			dd=Sqr(dd)
	Local Coef#=SMS_Distance#(dd#,Range);*Range)
	G_Coef#=Coef
	Return Coef#
End Function





Function SMS_SmoothColor%(NCol,FCol,CoefN#,CoefF#)
	Col%=NCol*(CoefN)*(1-coefF)+FCol*(coefF)
	Return Col
End Function





Function SMS_BoxMesh()
End Function







Type ENV_SMS_Light
	Field Light%,Mesh%
	Field X#,Y#,Z#
	Field RX%,RY%,RZ%
	Field ColR%,ColG%,ColB%
	Field Range#
End Type


; Light Create ======================================

Function SMS_Createlight(Typ%=1,Parent%=0,ColR%=255,ColG%=255,ColB%=255,Range#=100)
	l.ENV_SMS_Light=New ENV_SMS_Light
		If parent<>0
			l\Light=CreateLight(Typ,Parent)
		Else
			l\Light=CreateLight(Typ)
		EndIf
		l\Mesh=CreateSphere(4,l\light)
		HideEntity l\mesh
		NameEntity l\light,Handle(l)
		l\ColR=ColR
		l\ColG=ColG
		l\ColB=ColB
		l\Range=range
		LightColor l\light,l\ColR,l\ColG,l\ColB
		EntityColor l\mesh,l\ColR,l\ColG,l\ColB
		EntityFX l\mesh,9
		LightRange l\Light,l\Range
	Return l\Light
End Function



; Hide/Show Repere =====================================

Function SMS_MeshVisible(Light,Alpha#)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
		If alpha#=0 
			HideEntity l\mesh
		Else
			ShowEntity l\mesh
			EntityAlpha l\mesh,alpha
		EndIf
End Function



Function SMS_SetParent(Light,Parent)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
		EntityParent l\light,Parent
End Function



; Light positions ======================================

; Set 
Function SMS_SetLightPosition(Light,X#,Y#,Z#,Globale%=0)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
		l\x#=x
		l\y#=y
		l\z#=z
		PositionEntity l\light,x,y,z,Globale
End Function



Function SMS_SetLightTranslate(Light,vX#,vY#,vZ#,Globale%=0)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
		TranslateEntity l\light,vx,vy,vz,globale
		l\X#=EntityX(l\light,1)
		l\Y#=EntityY(l\light,1)
		l\Z#=EntityZ(l\light,1)
End Function



Function SMS_SetLightMove(Light,vX#,vY#,vZ#)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
		MoveEntity l\light,vx,vy,vz
		l\X#=EntityX(l\light,1)
		l\Y#=EntityY(l\light,1)
		l\Z#=EntityZ(l\light,1)
End Function


; get
Function SMS_GetLightPosX#(light)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
	Return l\x#
End Function


Function SMS_GetLightPosY#(light)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
	Return l\y#
End Function


Function SMS_GetLightPosZ#(light)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
	Return l\z#
End Function



; Light Rotations ======================================

; Set
Function SMS_SetLightRotate(Light,RX%,RY%,RZ%,Globale%=0)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
		l\Rx%=rx
		l\Ry%=ry
		l\Rz%=rz
		RotateEntity l\light,Rx,Ry,Rz,Globale
End Function




Function SMS_SetLightTurn(Light,vRX%,vRY%,vRZ%,Globale%=0)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
		TurnEntity l\light,vRx,vRy,vRz,Globale
		l\Rx%=EntityPitch(l\light,1)
		l\Ry%=EntityYaw  (l\light,1)
		l\Rz%=EntityRoll (l\light,1)
End Function



; get
Function SMS_GetLightRotX#(light)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
	Return l\rx#
End Function


Function SMS_GetLightRotY#(light)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
	Return l\ry#
End Function


Function SMS_GetLightRotZ#(light)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
	Return l\rz#
End Function




; Light Color ======================================

; Set 
Function SMS_SetLightColor(Light,ColR%,ColG%,ColB%)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
		l\ColR%=Colr
		l\ColG%=Colg
		l\ColB%=Colb
		LightColor l\light,colr,colg,colb
		EntityColor l\mesh,colr,colg,colb
End Function



Function SMS_SetLightColorRed(Light,ColR%)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
		l\ColR%=Colr
		LightColor l\light,l\colr,l\colg,l\colb
		EntityColor l\mesh,l\colr,l\colg,l\colb
End Function


Function SMS_SetLightColorGreen(Light,ColG%)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
		l\ColG%=Colg
		LightColor l\light,l\colr,l\colg,l\colb
		EntityColor l\mesh,l\colr,l\colg,l\colb
End Function


Function SMS_SetLightColorBlue(Light,ColB%)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
		l\ColB%=Colb
		LightColor l\light,l\colr,l\colg,l\colb
		EntityColor l\mesh,l\colr,l\colg,l\colb
End Function



; Get
Function SMS_GetColorR%(light)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
	Return l\ColR
End Function



Function SMS_GetColorG%(light)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
	Return l\ColG
End Function


Function SMS_GetColorB%(light)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
	Return l\ColB
End Function








; Light Range ======================================

; Set 
Function SMS_SetLightRange(Light,Range#)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
		l\Range#=Range
		LightRange l\Light,l\Range
End Function

; get
Function SMS_GetRange#(light)
	l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))
	Return l\Range#
End Function

;============================
; The demo
Graphics3D 1024,768,0,2
SetBuffer BackBuffer()
AmbientLight 125,125,125
SeedRnd MilliSecs()

	world	=	CreatePivot()

	pivcam1	=	CreatePivot		(world)
	pivcam2	=	CreatePivot		(pivcam1)
				MoveEntity		pivcam2,0,2,0
	camera 	= 	CreateCamera	(pivcam2)
				CameraClsColor	camera,120,150,255
				MoveEntity		camera,0,2,-10
				PointEntity		camera,pivcam2
				CameraRange		camera,.1,200

; initialise variables for the shadow system ( SMS Library )
	SMS_Init(World)
		LightR%			=	255
		LightG%			=	120
		LightB%			=	20
		groundR%		=	50
		groundG%		=	120
		groundB%		=	20
		Shade_R%		=	20
		Shade_G%		=	50
		Shade_B%		=	0
		SMS_Refreshrate%=	20

	; add the light for the shadows ( remember it adds a real light ! )
		light			=	SMS_Createlight			(1,0,LightR,LightG,LightB,2)
							SMS_SetLightPosition	(light,0,30,0)
							SMS_SetMajRate			(Refreshrate)
							SMS_Define_GroundColor	(groundR,groundG,groundB)
		Repere			=	CreateSphere			(4,light)
							EntityColor				repere,LightR,LightG,LightB
							EntityFX				repere,9
; Cubes...=========================== !>

	Type cube
		Field mesh
	End Type

	For i=0 To 20
		cube.cube = New cube
		cube\mesh = CreateCube()
					ScaleEntity			cube\mesh,Rnd(.5,2),Rnd(.5,2),Rnd(.5,2)
					TurnEntity			cube\mesh,Rnd(0,360),Rnd(0,360),Rnd(0,360)
					PositionEntity		cube\mesh,Rnd(-50,50),Rnd(1.5,5),Rnd(-50,50)
					EntityShininess		cube\mesh,1
					SMS_Add_Emetteur	(cube\mesh,World,Shade_R,Shade_G,Shade_B)

	Next
;====================================/!>



; a simple plane ==================== !>
	p 	=	CreatePlane	()
			MoveEntity	p,0,0,0
			EntityColor	p,groundR,groundG,groundB
			EntityFX	p,9
;====================================/!>

fnt=LoadFont("Comic Sans Ms",18,1)
	SetFont fnt

st=MilliSecs()
lt#			=	0
zoom#		=	1
methode%	=	2

Repeat
	; time based .... <================= !>
		mt%=MilliSecs()
		realDt=mt-(st+lt)
		If realDt>60
			 dt#=60.00
		ElseIf realDt<2 
			dt#=2.00
		Else
			dt#=Float(realDt)
		EndIf
		lt=lt+dt
	;====================================/!>

	; Set the light ===================== !>
		poslightX#=20*Cos(lt/20)
		poslighty#=10+5*Cos(lt/50)
		poslightZ#=10*Sin(lt/20)
		SMS_SetLightPosition(light,poslightX,poslightY,poslightZ)
		PointEntity light,world
	;====================================/!>

	; Camera ============================ !>
		TurnEntity 	pivcam1		,0,-MouseXSpeed(),0
		TurnEntity 	PivCam2		,MouseYSpeed(),0,0
					zoom#		=MouseZSpeed()
		MoveEntity 	camera		,0,0,zoom
	;====================================/!>

		l.ENV_SMS_Light=Object.ENV_SMS_Light(EntityName(light))

		If MouseHit(2) methode=(methode+1) Mod(4)

		ColR#	=	(ColR+dt#/120) Mod(255)
		ColG#	=	(ColG+dt#/150) Mod(255)
		ColB#	=	(ColB+dt#/180) Mod(255)
		SMS_SetLightColor	(Light,ColR,ColG,ColB)
		SMS_Update			(light,camera,methode,100) ; maj ombres/Update Shadow
	RenderWorld

		If lt>ltfps:ltfps=lt+1000:fpsC=fpscur:fpscur=0:ddt=dt:Else fpscur=fpscur+1:EndIf
		Color 180,200,150
		Rect 2,10,354,64,1
		Color 50,50,50
		Rect 5,13,348,58,1
		Color 0,255,0
		Text 10,20,"Use MouseHit 2 to toggle Shadow rendering"
		Text 10,35," fps    :"+fpsC
		Text 10,50," tris   :"+TrisRendered()
		Color 180,120,50
		Rect 500,12,200,30,1
		Color 50,50,55
		Rect 503,15,194,24,1
		Color 0,120,250
		Select Methode
			Case 0
				TextMethode$=" Off "
			Case 1
				TextMethode$=" One Time render
			Case 2
				TextMethode$=" RealTime"
			Case 3
				TextMethode$=" no Update"
		End Select
		Text 510,20,"Methode :"+TextMethode

		; repositionne la souris / replace mouse on the screen !
		If MouseX()>GraphicsWidth()-20 MoveMouse 30,MouseY()
		If MouseX()<20 MoveMouse GraphicsWidth()-30,MouseY()
		If MouseY()>GraphicsHeight()-20 MoveMouse MouseX(),30
		If MouseY()<20 MoveMouse MouseX(),GraphicsHeight()-30
	Flip 0
Until KeyDown(1)
End
