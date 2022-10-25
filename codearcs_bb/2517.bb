; ID: 2517
; Author: DareDevil
; Date: 2009-06-30 12:35:08
; Title: Vertex Ambient Occlusion
; Description: Vertex Ambient Occlusion

;===>O=========================o<=================================================================
;===>O=========================o<=================================================================
;===>	Name file: 
;===>
;===>	Programmatore:
;===>	  Caldarulo Vincenzo (Vision&Design Software)
;===>	Descrizione:
;===>
;===>O=========================o<=================================================================
;===>O=========================o<=================================================================
; 
;
AppTitle("Ambient Occlusion Vertex by Vincentx (Vincenzo Caldarulo Italy-Bari)")
;=====================================================
;===>
Type AO_vertex 
	Field Vx#
	Field Vy#
	Field Vz#
	Field Vc%
End Type

;=====================================================
;===>
Type AO_receiver
	;===>
	Field ObjOrig%
	Field Obj%
	;===>
End Type

;====>
;=====================================================
;===>
Const AO_Method%				= 0 ; 0 = LinePick B3D  // 1 = LinePick SW  // 2 = Octree
Const AO_Ray#					= 100
Const AO_DetBase#			= 4;	Level of precision AO 3 is a good result
Const AO_Detail#				= (AO_DetBase+1) ;;3=4 INTERAZIONI
Const AO_TotRay#				= ((4*(AO_DetBase-1)) * (AO_DetBase-1))+1
Const AO_Attenuation#	= (255.0 - 64 )/AO_TotRay


;=====================================================
;===>
Dim AO_Vertex.AO_vertex(65535)
For id=0 To 65535
	AO_Vertex(id) = New AO_vertex
Next
;=====================================================
;===>
Global dlr.AO_receiver

Global AO_TimeCollision=0
;Global AO_InterCollision=0

Const AO_LevelAlpha#  = 0.8
;=====================================================
;===>

Dim RayList.Point3D(AO_TotRay)
Global NSphere% = 0

;=====================================================
;===>
Precalcolo();



Graphics3D 800,600,32,2

Global Camera = CreateCamera() 


LoadScene()

; =================================================================================================
; ===>
Cls
Print ">===================================================="
Print ">"
Print "> Numero di interazioni oggetto: "+AO_TotRay
Print ">"
Print ">===================================================="
Print ">"
;Stop
Local TimeAO% = AO_Update()
Global spd# = 0.5
Local l_OldTime,l_CurTime
; =================================================================================================
; ===>
Repeat
	
	MoveEntity Camera,(KeyDown(205)-KeyDown(203))*spd,0,(MouseDown(1)-MouseDown(2))*spd
	TurnEntity Camera,-MouseYSpeed()*0.1,-MouseXSpeed()*0.1,0
	RotateEntity Camera,EntityPitch(Camera,True),EntityYaw(Camera,True),0
	
	MoveMouse GraphicsWidth()*.5,GraphicsHeight()*.5
	
	; ===========================================================================
	If KeyHit(2) Then  AO_Show()
	If KeyHit(3) Then  AO_Hide()
	
	
	If KeyHit(24) Then  WireFrame True
	If KeyHit(25) Then  WireFrame False
	
	
	If (KeyDown(46)) Then 
		PositionEntity Camera,60,60,-60
		RotateEntity Camera,30,45,0
	EndIf
	
	; ===========================================================================
	; ===>
	UpdateWorld()
	RenderWorld()
	;===>
	Local Riga=0
	;====>
	Color 255,0,0
	l_CurTime=MilliSecs()-l_OldTime
	l_OldTime=MilliSecs()
	Text(0,Riga,"Ms: "+l_CurTime+" AO Time: "+ TimeAO) : Riga = Riga + 10
	Riga = Riga + 10 : Riga = Riga + 10
	Text(0,Riga," 1 - Enable Ambient occlusion")	: Riga = Riga + 10
	Text(0,Riga," 2 - Disable Ambient occlusion")	: Riga = Riga + 10
	;===>
	Flip(False)
	
	;===>
Until KeyHit(1)

End


Function LoadScene()
	;===>
	Local Obj;
	Local R = 20
	Local R2 = 20
	Local x,z
	Local Segment=50
	;===>
	Obj = CPlane(Segment*2) 
	PositionEntity Obj,0,-5,0
	ScaleEntity Obj, 2, 1, 2
	AO_SetReceiver(Obj)
	;===>
	Local a#;
	For a=0 To 100
		;Obj = CreateSphere(16)
		Obj = CreateCube()
		ang	= a*25.0
		d0	= R2-(a/10.0)
		d1	= a/3.0
		PositionEntity Obj, d0*Cos(ang), d1, d0*Sin(ang)
		RotateEntity Obj, 0, ang, 0
		ScaleEntity Obj, 2, 2, 2
		
		AO_SetReceiver(Obj)
		
	Next
	;===>
	
End Function



Function CPlane%(Number#)
	;===>
	If(Number>150) Then Number=150
	;===>
	Local mesh = CreateMesh()
	Local surf = CreateSurface(mesh)
	Local center#= (Number/2.0)
	;===>
	
	For pz# = 0 To Number
		For px# = 0 To Number
			k = AddVertex(surf, px-center, 0, pz-center, px/Number, pz/Number )
		Next
	Next
	;===>
	
	For pz=0 To Number-1
		For px=0 To Number-1
			;===>
			p0 = px+((pz+0)*(Number+1))
			p1 = px+((pz+0)*(Number+1))+1
			p2 = px+((pz+1)*(Number+1))
			p3 = px+((pz+1)*(Number+1))+1
			;===>
			AddTriangle(surf,p2,p1,p0)
			AddTriangle(surf,p2,p3,p1)
			;===>
		Next
	Next
	;===>
	Return mesh
	;===>
End Function



; ================================================== 
; ----------------------------------------------- 
; Punto 3D 
;--------------------------------------- 
;======================================= 
Type Point3D 
	Field x# 
	Field y# 
	Field z# 
End Type 

; ================================================== 
; ----------------------------------------------- 

Function Point3DSet( Dst.Point3D, x#, y#, z#) 
	;----> 
	Dst\x = x ; 
	Dst\y = y ; 
	Dst\z = z ; 
	;----> 
End Function 

; ================================================== 
; ----------------------------------------------- 
; Rotazione Asse X Y
;-------------------------- 
;================================ 
Function RotateXY( Rtn.Point3D, P.Point3D, RotX%, RotY% ) 
	; ===> 
	Local l_c# = Cos(RotX);
	Local l_s# = Sin(RotX); 
	; ===> 
	;Rotazione Asse X 
	Local RX_x# =  P\x 
	Local RX_y# = (P\y * l_c)-(P\z * l_s);
	Local RX_z# = (P\z * l_c)+(P\y * l_s);
	; ===> 
	l_c# = Cos(RotY);
	l_s# = Sin(RotY); 
	; ===> 
	;Rotazione Asse Y 
	Local RY_x# = (RX_x * l_c)+(RX_z * l_s);
	Local RY_y# =  RX_y 
	Local RY_z# = (RX_z * l_c)-(RX_x * l_s)
	; ===> 
	Rtn\x# = RY_x
	Rtn\y# = RY_y
	Rtn\z# = RY_z
	; ===> 
End Function 

; ================================================== 
; ----------------------------------------------- 
; Calcolo della normale 
;-------------------------- 
;================================ 
; 
Function CalcNormalFast( Rtn.Point3D, A.Point3D, B.Point3D, C.Point3D) 
	; ===>
	Local K1x# = A\x - B\x
	Local K1y# = A\y - B\y
	Local K1z# = A\z - B\z
	; ===>
	Local K2x# = B\x - C\x
	Local K2y# = B\y - C\y
	Local K2z# = B\z - C\z
	; ===>
	; Compute their cross product. 
	Local Rx# = K1y#*K2z# - K1z#*K2y# 
	Local Ry# = K1z#*K2x# - K1x#*K2z# 
	Local Rz# = K1x#*K2y# - K1y#*K2x# 
	; ===>
	Local do# = 1.0 / Sqr( (Rx*Rx) + (Ry*Ry) + (Rz*Rz) ) 
  ; ===>
	Rtn\x = Rx*do; 
	Rtn\y = Ry*do; 
	Rtn\z = Rz*do;
	;===>
End Function


; ================================================== 
; ----------------------------------------------- 
; Vector To Angle
;-------------------------- 
;================================ 
; 
Function VectorToAngle( dst.Point3D, Vect.Point3D ) 
 	;===>
	Local dist1  = Sqr( (Vect\x*Vect\x)+(Vect\z*Vect\z)) ;
	dst\x = ATan2( Vect\y ,dist1);
	dst\y = ATan2( Vect\x ,Vect\z);
	;===>
End Function

;===>O=========================o<=================================================================
;===>O=========================o<=================================================================
;===>	Name file: 
;===>
;===>	Programmatore:
;===>	  Caldarulo Vincenzo (Vision&Design Software)
;===>	Descrizione:
;===>
;===>O=========================o<=================================================================
;===>O=========================o<=================================================================
; 




;;=====================================================
;;===>
;Type AO_vertex 
;	Field Vx#
;	Field Vy#
;	Field Vz#
;	Field Vc%
;End Type
;
;;=====================================================
;;===>
;Type AO_receiver
;	;===>
;	Field ObjOrig%
;	Field Obj%
;	;===>
;End Type
;
;;====>
;;=====================================================
;;===>
;Const AO_Method%				= 0 ; 0 = LinePick B3D  // 1 = LinePick SW  // 2 = Octree
;Const AO_Ray#					= 100
;Const AO_DetBase#			= 2;
;Const AO_Detail#				= (AO_DetBase+1) ;;3=4 INTERAZIONI
;Const AO_TotRay#				= ((4*(AO_DetBase-1)) * (AO_DetBase-1))+1
;Const AO_Attenuation#	= (255.0 - 64 )/AO_TotRay
;
;
;;=====================================================
;;===>
;Dim AO_Vertex.AO_vertex(65535)
;For id=0 To 65535
;	AO_Vertex(id) = New AO_vertex
;Next
;;=====================================================
;;===>
;Global dlr.AO_receiver
;
;Global AO_TimeCollision=0
;;Global AO_InterCollision=0
;
;Const AO_LevelAlpha#  = 0.8
;;=====================================================
;;===>
;
;Dim RayList.Point3D(AO_TotRay)
;Global NSphere% = 0
;
;;=====================================================
;;===>
;Precalcolo();

;=====================================================
;--------------------------------------------------
;===>
;
;===>
;
Function Precalcolo()
	;===>
	If (AO_DetBase<=1) Then Return
	;===>
	NSphere%=0
	Local RX#,RY#,RXa#,RYa#
	Local l_Normal.Point3D	= New Point3D : Point3DSet(l_Normal, 0, AO_Ray, 0)
	Local l_PartRY# = ((AO_DetBase-1)*4)
	Local l_AngS# = 360 / l_PartRY ; 
	Local l_AngT# = 90.0 / AO_DetBase
	;===>
	For RY=0 To l_PartRY-1
		;===>
		RYa# = l_AngS*RY
		;===>
		For RX=1 To AO_DetBase-1 
			;===>
			RXa# = RX*l_AngT
			;===>
			RayList.Point3D(NSphere) = New Point3D
			;===>
			RotateXY(RayList(NSphere),l_Normal, RXa, RYa)
			;===>
			NSphere = NSphere+1
			;===>
		Next
		;===>
	Next
	;===>
	Delete l_Normal
	;===>
End Function

;=====================================================
;--------------------------------------------------
;===>
;
;===>
;
Function AO_Free()
	Local l_AO_Recived.AO_receiver
	For l_AO_Recived.AO_receiver = Each AO_receiver
		FreeEntity l_AO_Recived\Obj
		Delete l_AO_Recived
	Next
	
End Function

;=====================================================
;--------------------------------------------------
;===>
;
;===>
;
Function AO_SetReceiver(mesh, ShadowEnable=True, TexAlpha=0)
	;===>
	Local l_AO_Recived.AO_receiver = New AO_receiver
	;===>
	l_AO_Recived\ObjOrig = mesh
	l_AO_Recived\Obj = CopyMesh(l_AO_Recived\ObjOrig,l_AO_Recived\ObjOrig)
	NameEntity( l_AO_Recived\Obj, EntityName(l_AO_Recived\ObjOrig))
	EntityAlpha l_AO_Recived\Obj, AO_LevelAlpha
	EntityBlend l_AO_Recived\Obj, 2
	
	;
	EntityPickMode(l_AO_Recived\Obj,2)
	;===>
	EntityFX l_AO_Recived\Obj,2+1
	UpdateNormals l_AO_Recived\Obj
	;===>
End Function


;=====================================================
;--------------------------------------------------
;===>
;
;===>
;
Function AO_Update()
	;===>
	Local l_total_tris%,l_total_vert%
	Local Total_ColliTime = 0 
	;===>
	Local Start_Time=MilliSecs()
	;===>
	Local l_IdV0c%, l_IdV0x#, l_IdV0y#, l_IdV0z#, l_IdV0.AO_vertex
	Local l_IdV1c%, l_IdV1x#, l_IdV1y#, l_IdV1z#, l_IdV1.AO_vertex
	Local l_IdV2c%, l_IdV2x#, l_IdV2y#, l_IdV2z#, l_IdV2.AO_vertex
	;===>
	Local l_Point0.Point3D	= New Point3D
	Local l_Point1.Point3D	= New Point3D
	Local l_Point2.Point3D	= New Point3D
	;===>
	Local l_Normal.Point3D	= New Point3D : Point3DSet(l_Normal, 0, AO_Ray, 0)
	Local l_NorMod.Point3D	= New Point3D
	Local l_Angles.Point3D	= New Point3D
	;===>
	Local SphereList=0
	Local v%, s%
	;===>
	Local l_CurObj;
	Local n_surfs
	Local surf, n_tris
	Local Px#,Py#,Pz#;
	;===>
	Local Color_a# = 0
	Local l_rX#,l_rY#
	;===>
	Local cnt=0
	Local l_AO_Recived.AO_receiver
	Local Riga$
	;===>
	Local RYa#,RXa#
	Start_Time=MilliSecs()
	Local Start_ms=Start_Time
	
	Local l_CurVert.AO_vertex
	Local aamod
	;===>
	For l_AO_Recived = Each AO_receiver
		; ===>
		l_CurObj	= l_AO_Recived\Obj
		; ===>
		n_surfs= CountSurfaces(l_CurObj)
		Local l_NewId%=0;
		Print "> Oggetto: "+cnt+" Name: "+EntityName(l_CurObj) 
		
		cnt=cnt+1
		;===>
		For s = 1 To n_surfs
			;===>
			surf = GetSurface(l_CurObj,s)
			n_tris = CountTriangles(surf)-1
			aamod = n_tris * .2
			If aamod=0 Then aamod=1
			;===>
			If (n_tris>0) Then 
				;===>
				Local l_NVert = CountVertices(surf)-1
				;===>
				l_total_vert = l_total_vert+l_NVert
				l_total_tris = l_total_tris+n_tris
				;===>
				For v = 0 To l_NVert
					;===>
					l_CurVert = AO_Vertex(v)
					TFormPoint VertexX( surf, v ), VertexY( surf, v ), VertexZ( surf, v ), l_CurObj, 0
					l_CurVert\Vx = TFormedX()
					l_CurVert\Vy = TFormedY()
					l_CurVert\Vz = TFormedZ()
					l_CurVert\Vc = 255;
				Next
				;===>
				For v = 0 To n_tris
					; ===>
					l_IdV0 = AO_Vertex(TriangleVertex ( surf, v, 0 ))
					l_IdV1 = AO_Vertex(TriangleVertex ( surf, v, 1 ))
					l_IdV2 = AO_Vertex(TriangleVertex ( surf, v, 2 ))
					; ===>
					l_IdV0x = l_IdV0\Vx ; Vertexx		(surf,l_IdV0)
					l_IdV0y = l_IdV0\Vy ; VertexY		(surf,l_IdV0)
					l_IdV0z = l_IdV0\Vz ; VertexZ		(surf,l_IdV0)
					l_IdV0c = l_IdV0\Vc ; VertexRed	(surf,l_IdV0)
					;===>
					l_IdV1x = l_IdV1\Vx ; 
					l_IdV1y = l_IdV1\Vy ; 
					l_IdV1z = l_IdV1\Vz ; 
					l_IdV1c = l_IdV1\Vc ; 
					;===>
					l_IdV2x = l_IdV2\Vx ; 
					l_IdV2y = l_IdV2\Vy ; 
					l_IdV2z = l_IdV2\Vz ; 
					l_IdV2c = l_IdV2\Vc ; 
					;===>
					Px = (l_IdV0x + l_IdV1x + l_IdV2x) * .3333333
					Py = (l_IdV0y + l_IdV1y + l_IdV2y) * .3333333
					Pz = (l_IdV0z + l_IdV1z + l_IdV2z) * .3333333
					;===>
					l_Point0\x = l_IdV0x : l_Point0\y = l_IdV0y : l_Point0\z = l_IdV0z;
					l_Point1\x = l_IdV1x : l_Point1\y = l_IdV1y : l_Point1\z = l_IdV1z;
					l_Point2\x = l_IdV2x : l_Point2\y = l_IdV2y : l_Point2\z = l_IdV2z;
					;===>
					
					l_NorMod\x = VertexNX(surf,TriangleVertex ( surf, v, 0 ))
					l_NorMod\y = VertexNY(surf,TriangleVertex ( surf, v, 0 ))
					l_NorMod\z = VertexNZ(surf,TriangleVertex ( surf, v, 0 ))
					CalcNormalFast(l_NorMod, l_Point0, l_Point1, l_Point2);
					VectorToAngle(l_Angles,l_NorMod)
					;===>
					l_rX# = l_Angles\x + 270 ;
					l_rY# = l_Angles\y + 180 ;
					;===>
					Color_a = 255
					; =========================================================================================================
					; ----------------------------------------------------------------------------------------------------
					; Solution 2  
					; ===>
					;
					; ===>
					; Asse perpedicolare al piano
					l_NorMod\x = l_NorMod\x*AO_Ray : l_NorMod\y = l_NorMod\y*AO_Ray : l_NorMod\z = l_NorMod\z*AO_Ray
					; ===>
					
					LinePick(Px, Py, Pz, l_NorMod\x, l_NorMod\y, l_NorMod\z);
					If (PickedEntity()<>0) Then Color_a = Color_a - AO_Attenuation
					; ===>
					Total_ColliTime = Total_ColliTime + AO_TimeCollision
					; ===>
					If (AO_DetBase>1) Then 
						;===>
						For SphereList=0 To NSphere-1
							;===>
							RotateXY(l_NorMod, RayList(SphereList), l_rX, l_rY)
							;===>
							LinePick(Px, Py, Pz, l_NorMod\x, l_NorMod\y, l_NorMod\z);
							If (PickedEntity()<>0) Then Color_a = Color_a - AO_Attenuation
							;===>
						Next
						; ===>
					EndIf
					;===>
					Total_ColliTime = Total_ColliTime + AO_TimeCollision
					;===>
					;----------------------------------------------------------------------------------------------------
					;=========================================================================================================
					;===>
					
					If ( Color_a < l_IdV0c) Then l_IdV0\Vc = Color_a
					If ( Color_a < l_IdV1c) Then l_IdV1\Vc = Color_a
					If ( Color_a < l_IdV2c) Then l_IdV2\Vc = Color_a
					;===>
					If ((v Mod aamod)=0) Then 
						;===>
						Riga$ =         "> Surfaces:    " + Float(Int((Float(s)/Float(n_surfs))*10000))/100.0
						Riga$ = Riga$ + "% Complete:    " + Float(Int((Float(v)/Float(n_tris ))*10000))/100.0
						Riga$ = Riga$ + "  TimeCol:     " + Total_ColliTime
						Riga$ = Riga$ + "  TotalTime:   " + (MilliSecs()-Start_ms)
						Print Riga$
						;===>
						Total_ColliTime = 0 
						Start_ms=MilliSecs()
						;===>
					EndIf
					;===>
				Next
				;===>
				For v = 0 To l_NVert
					;===>
					Color_a = AO_Vertex(v)\Vc
					VertexColor surf, v, Color_a, Color_a, Color_a;
					;===>
				Next
				;===>
				Riga$ =         "> Surfaces:    " + Float(Int((Float(s)/Float(n_surfs))*10000))/100.0
				Riga$ = Riga$ + "% Complete:    " + 100.0
				Riga$ = Riga$ + "  TimeCol:     " + Total_ColliTime
				Riga$ = Riga$ + "  TotalTime:   " + (MilliSecs()-Start_ms)
				Print Riga$
				;===>
				Total_ColliTime = 0 
				Start_ms=MilliSecs()
				;===>
			EndIf
			;===>
			Print " "
			l_NewId = l_NewId + CountVertices(surf)
			;===>
		Next		
	Next
	;===>
	Delete l_Point0
	Delete l_Point1
	Delete l_Point2
	
	Delete l_Normal;
	Delete l_NorMod;
	Delete l_Angles;
	;===>
	Local Stop_Time% = MilliSecs()-Start_Time
	;===>
	Print "->"
	Print "-> Total Time collision: "+Stop_Time
	Print "->"
	Print " Total Tris: "+l_total_tris
	Print " Total Vert: "+l_total_vert
	Print "->"
	Print " "
	Print "Press any Key"
	WaitKey()
	;===>
	Return Stop_Time 
	;===>
End Function



;=====================================================
;--------------------------------------------------
;===>
;
;===>
;
Function AO_Show()
	;===>
	Local l_AO_Recived.AO_receiver
	;===>
	For l_AO_Recived.AO_receiver = Each AO_receiver
		; ===>
		ShowEntity l_AO_Recived\Obj
		; ===>
	Next
	;===>
End Function

;=====================================================
;--------------------------------------------------
;===>
;
;===>
;
Function AO_Hide()
	;===>
	Local l_AO_Recived.AO_receiver
	;===>
	For l_AO_Recived.AO_receiver = Each AO_receiver
		; ===>
		HideEntity l_AO_Recived\Obj
		; ===>
	Next
	;===>
End Function


;~IDEal Editor Parameters:
;~C#Blitz3D
