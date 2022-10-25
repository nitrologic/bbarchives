; ID: 3281
; Author: Santiworld
; Date: 2016-06-27 10:09:48
; Title: 3D Water Waves
; Description: 3D waves and .b3d and textures

Graphics3D 1024,768,0,2
Const eo# = .01 ;escala objetos
Const ee# = .01 ;escala escenarios


Global mx#,my#,mhl,mhc,mhr,mdc,mdl,mdr,mxs#,mys#,mzs#,mun$,accion$,mx1,my1,mzsv#,mlcv#,mrcv#,mouse_over$,last_mouse_over$,l_mdl



;! Create water 3d
Global mesh_agua = LoadMesh("objetos\agua\aguacurva3.b3d")
EntityPickMode mesh_agua,2
EntityFX mesh_agua,2
EntityShininess mesh_agua,.3
EntityColor mesh_agua,255,255,255
Global t_cube_map = LoadTexture("objetos\agua\cubemap2.jpg",64)
EntityTexture mesh_agua,t_cube_map,0,2
Global t_espuma = LoadAnimTexture("wateranim.bmp",256,124,124,0,25)
ScaleTexture t_espuma,.02,.02
TextureBlend t_espuma,3
Global t_e_frame
Global t_rio = LoadTexture("objetos\agua\waterhi2.jpg")
TextureBlend t_rio,2
te# = .06
ScaleTexture t_rio,te,te


;! Create camera
Global cam = CreateCamera()
PositionEntity cam,10,10,20
CameraClsColor cam,150,160,255
CameraFogColor cam,150,160,200
CameraFogMode cam,1
CameraFogRange cam,1,5000
CameraRange cam,1,100000



;! Create ambient sounds
;;SONIDOS!!!
;Global oido = CreatePivot(cam)
;CreateListener  oido,.1,0,1
;Global ps_agua = CreatePivot(cam)
;Global s_agua = Load3DSound("sonidos\oleaje suave.mp3")
;LoopSound s_agua
;Global c_agua = EmitSound(s_agua,ps_agua)
;SoundVolume s_agua,.1
;ChannelPitch c_agua,22000
;ChannelVolume c_agua,.051

;! Create ships
Type barco
	Field pivot
	Field entidad
	
	Field r#[3]
	Field v#[3]
End Type

nombre$ = "puma26"
b.barco = New barco
b\pivot = CreatePivot()
b\entidad = CreateCube(b\pivot);LoadMesh("objetos\barcos\"+nombre$+"\"+nombre$+".b3d",b\pivot)
ScaleEntity b\entidad,4,4,8
UpdateNormals b\entidad

;! Create light
sol = CreateLight(1)
PositionEntity sol,200,100,0
PointEntity sol,b\pivot



crear_botones()

While Not KeyHit(1)
	update_mouse()
	If KeyDown(57) Then
		WireFrame 1
	Else
		WireFrame 0
	End If
	
	wave_mesh()
	
	PointEntity cam,mesh_agua
	
	If mdr Then
		FlushMouse
		;MoveMouse -MouseXSpeed(),-MouseYSpeed()
		FlushMouse
	End If
	If mdr Then
		MoveEntity cam,mxs*.05,mys*-.05,mzs*5
		MoveMouse 500,400
	End If
	
	PointEntity cam,mesh_agua
	
	update_barcos()
	
	UpdateWorld()
	RenderWorld()
	
	Color 255,255,255
	Text 10,10, "3D Waves by Santiago Gonzalez. www.indiesoft.com.ar"
	Text 10,30, "SPACE - Wireframe
	Text 10,50, "MOUSE DOWN RIGHT : Mouse move camera"
	Text 10,70, "Change the configuration of waves with the mouse"
	Text 10,90, "Try Others .b3d water : aguacurva(1,2,3,4,5,6).b3d or aguarrecta(1 or 2).b3d"
	Text 10,110, "i make diferents mesh to test results.
	
	
	
	water_conf()
	
	Flip
	
Wend

End



Function wave_mesh()
	
	surf_agua = GetSurface (mesh_agua,1)
	t_e_frame = (t_e_frame + 1) Mod 23
	EntityTexture mesh_agua,t_espuma,t_e_frame,3
	
	vert = CountVertices(surf_agua)
	tri = CountTriangles(surf_agua)
	
	fdx# = 5
	fdy# = 1
	fdz# = 11
	wtipe = 0
	alpha# = 1
	f_alt# = 5
	For b.boton = Each boton
		If b\texto = "Vel" Then f_vel# = b\val
		If b\texto = "Alt" Then f_alt# = b\val
		If b\texto = "fdx" Then fdx# = b\val
		If b\texto = "fdy" Then fdy# = b\val
		If b\texto = "fdz" Then fdz# = b\val
		If b\texto = "Wtype" Then wtipe = b\val
		If b\texto = "Waves" Then waves = b\val
		If b\texto = "Alpha" Then alpha# = b\val
	Next
	
	f_alt# = 3
	
	For v = 0 To vert-1
		
		If wtipe = 1 Then
			
			z# = VertexZ(surf_agua,v)
			y# = VertexY(surf_agua,v) 
			x# = VertexX(surf_agua,v) 
			
			
			For w = 1 To waves
				
				dir = w * 30
				y = y+Sin(dir+(z*waves*fdz+((x+waves)*fdy)+(MilliSecs()*f_vel)))* f_alt 
				x# = VertexX(surf_agua,v) + Cos((z*fdx)+MilliSecs()*f_vel)*.1
				
			Next		
			
			
			y# = y# - (y *.6)
			
			VertexCoords (surf_agua,v,x,y,z)
			fy=100
			;VertexColor surf_agua,v,100-(y*fy),100-(y*fy),250,alpha
			
		End If
		
		
		If wtipe = 2 Then
			z# = VertexZ(surf_agua,v)
			y# = VertexY(surf_agua,v) + Sin((v*fdz)+(MilliSecs()*f_vel))*.01
			x# = VertexX(surf_agua,v) + Cos((z*fdx)+MilliSecs()*f_vel)*.002
			For w = 1 To waves
				y# =  Sin((z+x)*fdy+(dir+MilliSecs()*f_vel))*f_alt
				dir = w * 30
				
			Next
			VertexCoords (surf_agua,v,x,y,z)
			fy=100
			VertexColor surf_agua,v,100-(y*fy),100-(y*fy),250,alpha
		End If
		
	Next
	
	UpdateNormals mesh_agua
	
	
	
End Function


Type boton 
	
	Field nombre$
	Field texto$
	Field val#
	Field max#
	Field min#
	Field tipo
	
	Field cat
	
End Type

Function crear_botones()
	new_boton ("","Direction",0,0,360,1,1)
	new_boton ("","WZize",100,10,1000,1,1)
	new_boton ("","Wtype",1,0,10,2,1)
	new_boton ("","Waves",1,1,10,2,1)
	new_boton ("","Vel",.1,0,10,1,1)
	new_boton ("","Alt",1,0,10,1,1)
	new_boton ("","fdx",5,0,100,1,1)
	new_boton ("","fdy",1,0,100,1,1)
	new_boton ("","fdz",11,0,100,1,1)
	
	new_boton ("","",11,0,10,1,1)
	new_boton ("","Alpha",1,0,1,1,1)
	
End Function

Function water_conf()
	
	Color 255,255,255
	y=115
	
	For b.boton = Each boton
		
		x=20
		x2=200
		
		y=y+20
		
		If Abs(my-y) < 10 Then
			Color 255,255,0
			
			If b\tipo = 1 Then b\val = b\val + mzs*b\val*.1
			If b\tipo = 2 Then b\val = Int(b\val + mzs)
			
		Else
			Color 0,0,0
		End If
		
		If b\val > b\max Then b\val = b\max
		If b\val < b\min Then b\val = b\min
		
		
		Text x,y,b\texto
		Text x2,y,b\val
		
		
		
	Next
	
End Function

Function new_boton(nombre$,texto$,val#,min#,max#,tipo,cat)
	b.boton = New boton
	b\nombre$ = nombre$
	b\texto$ = texto$
	b\val# = val#
	b\min# = min#
	b\max# = max#
	b\tipo = tipo
	b\cat = cat
End Function

Function update_mouse()
	
	l_mdl = mdl
	
	mhl = MouseHit(1) ;Or JoyHit(1,0)
	mhr = MouseHit(2) ;Or JoyHit(2,0)
	mhc = MouseHit(3) ;Or JoyHit(1,0)
	
	
	mx# = MouseX()
	my# = MouseY()
	mz# = MouseZ()
	
	mxs# = MouseXSpeed() 
	mys# = MouseYSpeed() 
	mzs# = MouseZSpeed()
	
	
	mdl = MouseDown(1) ;Or JoyDown(1,0)
	mdr = MouseDown(2) ;Or JoyDown(2,0)
	mdc = MouseDown(3) ;Or KeyDown(56); Or JoyDown(3,0)
	
	If mdl = 0  Then 
		
		mx1 = mx
		my1 = my
		
	End If
	
	
	pick = CameraPick(cam,mx,my)
	
	If mdr = 1 Or mdc = 1 Then
		MoveMouse gancho*.5,galto*.5
	End If
	
End Function

Function update_barcos()
	
	For b.barco = Each barco
		
		
		pick = LinePick( EntityX(b\pivot,1),100,EntityZ(b\pivot,1),0,-200,0,1)
		
		nx# = PickedNX()
		ny# = PickedNY()
		nz# = PickedNZ()
		
		y# = PickedY()
		
		ys# = EntityY(b\pivot,1)
		
		flotacion# = .0005
		gravedad#= .0005
		If y > ys Then
			b\v[1] = b\v[1] + flotacion
		Else
			b\v[1] = b\v[1] - gravedad
		End If
		
		rv# = .999
		rr# = .999
		av# = 100
		
		b\r[0] = b\r[0] + nx * av
		b\r[1] = b\r[1] + ny * av
		b\r[2] = b\r[2] + nz * av
		
		For i = 0 To 2
			b\v[i] = b\v[i] * rv
			b\r[i] = b\v[i] * rr
		Next
		
		AlignToVector b\pivot,nx,ny,nz,2,.05
		b\r[1] = .2
		b\v[2] = .05
		
		TurnEntity b\pivot,b\r[0],b\r[1],b\r[2]
		TranslateEntity b\pivot,b\v[0],b\v[1],0
		MoveEntity b\pivot,0,0,b\v[2]
		
		
		PositionEntity b\pivot,EntityX(b\pivot,1),y,EntityZ(b\pivot,1)
		
	Next
	
	
End Function
