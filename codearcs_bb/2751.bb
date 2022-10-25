; ID: 2751
; Author: Krischan
; Date: 2010-08-08 07:08:10
; Title: Interplanetary travel
; Description: Calculates and shows interplanetary ship travel between two moving celestial bodies in 3D

AppTitle "Interplanetary Travel"

Graphics3D 800,600,32,2

SeedRnd MilliSecs()

; sun obstacle demo
;SeedRnd 6

Const MOONS			= 29			; number of moons to read
Const PLANETS		= 9				; number of planets to read
Const RANDOMIZE%	= True			; random positions on/off
Const DAYINC#		= 1.0/60		; time increment per cycle
Const YEAR#			= 365.256		; earth year in days
Const AVOIDSUNCROSS%= True			; avoid sun crossing trajectory yes/no
Const AVOIDINCDAYS% = 10			; days increment for new trajectory

Const START$		= "Moon"		; object to start at
Const TARGET$		= "Callisto"	; object to target
Const SHIPSPEED#	= 20			; ship speed (Mio. km/Day) (normal = 20 / hispeed = 40 / c=1079.9)

Global WIDTH%=GraphicsWidth()		; screen width
Global HEIGHT%=GraphicsHeight()		; screen height
Global SPEED#=DAYINC*SHIPSPEED		; calculated ship speed
Global TIMER%=CreateTimer(60)		; timer
Global INFO%=True					; Text info flag
Global STOPPER%=False				; Ship arrival flag

Global S.System=New System

Global DAYS#,MAXDIST#,CALCTIME#,ZOOM#
Global CAM%,PLANETORBITS%,SUN%,SUN0%,SUN1%,SUN2%,SUN3%,TRACER%,SHIP%,STARTOBJECT%,TARGETOBJECT%
Global MASTERPIVOT%,SOLIDPIVOT%,WIREDPIVOT%,TARGETPIVOT%

Type Moon
	Field id%,parent%,name$,radius#,period#,size#,r%,g%,b%,SPEED#,random%,entity%,orbit%
End Type

Type Planet
	Field id%,parent%,name$,radius#,period#,size#,r%,g%,b%,MOONS%,moon.Moon[MOONS],SPEED#,random%,entity%
End Type

Type System
	Field planet.Planet[PLANETS]
End Type

; init scene
InitScene()

; init planet and moon objects and update once
Initialize()
UpdateObjects()

; set ship start position
InitShipPosition(SHIP,AVOIDSUNCROSS,AVOIDINCDAYS)

MoveMouse WIDTH/2,HEIGHT/2


;===========================================================================
; main loop
;===========================================================================

While Not KeyHit(1)
	
	ZOOM=1.0
	
	Local oldx#,oldy#,oldz#,multi%=1,l#=5.0
	Local f#=Sqr(EntityDistance(CAM,MASTERPIVOT)/1000.0)
	Local d#=EntityDistance(SHIP,TARGETPIVOT)
	
	; store current ship position
	oldx=EntityX(SHIP)
	oldy=EntityY(SHIP)
	oldz=EntityZ(SHIP)
	
	; LMB = advance time
	If MouseDown(1) And (Not STOPPER) Then
		
		DAYS=DAYS+DAYINC
		
		; near target? stop it
		If d<=SPEED*2 Then
			
			UpdateObjects()
			STOPPER=True
			PositionEntity SHIP,EntityX(TARGETOBJECT),EntityY(TARGETOBJECT),EntityZ(TARGETOBJECT)
			CreateLine(TRACER,oldx,oldy,oldz,EntityX(SHIP),EntityY(SHIP),EntityZ(SHIP),0,255,0,1)
			
		Else
			
			; move ship to target
			PointEntity SHIP,TARGETPIVOT
			MoveEntity SHIP,0,0,SPEED
			CreateLine(TRACER,oldx,oldy,oldz,EntityX(SHIP),EntityY(SHIP),EntityZ(SHIP),0,255,0,1)
			
		EndIf
			
	EndIf
	
	; SHIFT = 10x faster cam flight
	If KeyDown(42) Or KeyDown(54) Then multi=10
	
	; RMB = Zoom
	If MouseDown(2) Then ZOOM=20 : l=50.0
	
	; SPACE = show/hide text info
	If KeyHit(57) Then INFO=1-INFO
	
	; camera movement
	RotateEntity CAM,EntityPitch(CAM)+(MouseYSpeed()/l),EntityYaw(CAM)-(MouseXSpeed()/l),0
	MoveEntity CAM,(KeyDown(205)-KeyDown(203))*f*multi,0,(KeyDown(200)-KeyDown(208))*f*multi
	MoveMouse WIDTH/2,HEIGHT/2
	CameraZoom CAM,ZOOM
	
	; sun quads always point to cam
	PointEntity SUN0,CAM
	PointEntity SUN1,CAM
	PointEntity SUN2,CAM
	PointEntity SUN3,CAM
	
	; wireframe render pass
	WireFrame 1
	ShowEntity WIREDPIVOT
	HideEntity SOLIDPIVOT
	CameraClsMode CAM,1,1
	RenderWorld
	
	; solid object render pass
	WireFrame 0
	HideEntity WIREDPIVOT
	ShowEntity SOLIDPIVOT
	CameraClsMode CAM,0,0
	RenderWorld
	
	; update object positions
	UpdateObjects()
	
	; text info
	Text 0, 0,"Source > Target: "+START+" > "+TARGET
	Text 0,15,"Distance.......: "+Round(d)+" Mio. km [total "+Round(MAXDIST)+" Mio. km]"
	Text 0,30,"Ship Speed.....: "+Round(SPEED/DAYINC)+" Mio. km/Day ["+Round(SPEED/DAYINC/3600*1000000)+"km/sec.]"
	Text 0,45,"Flight time....: "+Round(DAYS)+" Days ["+Round(CALCTIME)+" total]"
	
	WaitTimer TIMER
	
	Flip 0
	
Wend

End


;===========================================================================
; init scene objects
;===========================================================================

Function InitScene()
	
	Local size#=13.92,suntex%
	
	; pivots
	MASTERPIVOT=CreatePivot()
	SOLIDPIVOT=CreatePivot(MASTERPIVOT)
	WIREDPIVOT=CreatePivot(MASTERPIVOT)
	
	; cam
	CAM=CreateCamera()
	CameraRange CAM,1,32768
	
	; orbit wireframe
	PLANETORBITS=CreateMesh(WIREDPIVOT)
	CreateSurface(PLANETORBITS)
	EntityFX PLANETORBITS,1+2+16+32
	
	; ship tracer wireframe
	TRACER=CreateMesh()
	CreateSurface(TRACER)
	EntityFX TRACER,1+2+16+32
	
	; ship
	SHIP=CreateCone(16,1,SOLIDPIVOT)
	RotateMesh SHIP,90,0,0
	EntityFX SHIP,1
	EntityColor SHIP,255,0,255
	ScaleEntity SHIP,0.2,0.2,0.2
	
	; sun
	SUN=CreateSphere(8)
	ScaleEntity SUN,size,size,size
	EntityRadius SUN,size/2.0,size/2.0
	EntityPickMode SUN,1
	EntityAlpha SUN,0
	
	; sun flares
	suntex=CreateSunTexture()
	SUN0=CreateQuad(SOLIDPIVOT,size*1.0,suntex,3,1+8,255,255,255,1.00)
	SUN1=CreateQuad(SOLIDPIVOT,size*1.5,suntex,3,1+8,255,192,128,1.00)
	SUN2=CreateQuad(SOLIDPIVOT,size*3.0,suntex,3,1+8,255,255,224,0.75)
	SUN3=CreateQuad(SOLIDPIVOT,size*6.0,suntex,3,1+8,255,255,224,0.50)
	
End Function


;===========================================================================
; calculate ship trajectory from start object to target object
;===========================================================================

Function CalculateTrajectory(add#=0.0,avoidsun%=True)
	
	Local pick%
	
	; create ship target pivot
	TARGETPIVOT=CreatePivot(MASTERPIVOT)
	
	; calculate distance and estimated time to reach target position
	MAXDIST#=EntityDistance(SHIP,TARGETOBJECT)
	CALCTIME#=(MAXDIST/SPEED*DAYINC)+add
	
	; calculate approximate future position of target object
	DAYS=DAYS+CALCTIME
	UpdateObjects()
	
	; get ship position
	Local sx#=EntityX(SHIP)
	Local sy#=EntityY(SHIP)
	Local sz#=EntityZ(SHIP)
	
	; get target position
	Local tx#=EntityX(TARGETOBJECT)
	Local ty#=EntityY(TARGETOBJECT)
	Local tz#=EntityZ(TARGETOBJECT)
	
	; place target pivot to target object position and calc distance
	PositionEntity TARGETPIVOT,tx,ty,tz
	MAXDIST#=EntityDistance(SHIP,TARGETOBJECT)
	
	; reset scene to initial position
	DAYS=DAYS-CALCTIME
	UpdateObjects()
	
	; check free path avoiding sun crossing
	If avoidsun Then
	
		; check if sun is blocking
		pick=LinePick(sx,sy,sz,tx-sx,ty-sy,tz-sz,10)
	
		; free path?
		If (Not pick) Then
		
			; create a blue line and calculate the constant ship speed (should be slower than max ship speed)
			CreateLine(TRACER,sx,sy,sz,tx,ty,tz,0,0,128,1)
			SPEED#=(EntityDistance(SHIP,TARGETPIVOT)/(CALCTIME-DAYS)*DAYINC)
		
			; position cam
			PositionEntity CAM,sx,sy+20,sz-20
			PointEntity CAM,SHIP
		
		Else
		
			; create a red line if occupied space (for debugging only)
			CreateLine(TRACER,sx,sy,sz,tx,ty,tz,255,0,0,0.2)
			
		EndIf
		
	Else
		
		; create a blue line
		CreateLine(TRACER,sx,sy,sz,tx,ty,tz,0,0,128,1)
		SPEED#=(EntityDistance(SHIP,TARGETPIVOT)/(CALCTIME-DAYS)*DAYINC)
		
		; position cam
		PositionEntity CAM,sx,sy+20,sz-20
		PointEntity CAM,SHIP
		
	EndIf
	
	Return pick
	
End Function


;===========================================================================
; set a given object to start object position
;===========================================================================

Function InitShipPosition(obj%,avoidsun%=True,avoidinc%=1,addx#=0.0,addy#=0.0,addz#=0.0)
	
	Local i%,pick%,c%,p.Planet,m.Moon
	
	; check all planets
	For p.Planet = Each Planet
		
		; start or target found? store entity information
		If p\name=START Then STARTOBJECT=p\entity
		If p\name=TARGET Then TARGETOBJECT=p\entity
		
		; check all moons
		For i=1 To p\MOONS
			
			m.Moon = MOONdata(p\id,i)
			
			; start or target found? store entity information
			If m\name=START Then STARTOBJECT=m\entity
			If m\name=TARGET Then TARGETOBJECT=m\entity
			
		Next
		
	Next
	
	; position object to start object position
	PositionEntity obj,EntityX(STARTOBJECT)+addx,EntityY(STARTOBJECT)+addy,EntityZ(STARTOBJECT)+addz
	PointEntity obj,TARGETOBJECT
	
	; calculate direct trajectory (with optional avoiding sun crossing)
	Repeat pick=CalculateTrajectory(c,avoidsun) : c=c+avoidinc : Until Not pick
	
End Function


;===========================================================================
; read planet/moon data and create objects, fill types
;===========================================================================

Function Initialize()
	
	Local p%,m%
	Local parent%,name$,radius#,period#,size#,r%,g%,b%
	Local speed#,entity%,random%,orbit%
	
	; read planet data
	Restore PLANETDATA
	
	For p=1 To PLANETS
		
		Read parent,name,radius,period,size,r,g,b
		
		; calc speed and initial position
		speed=YEAR/Float(period)
		If RANDOMIZE Then random=Rnd(0,2^16)
		
		; create sphere object
		entity=CreateSphere(16,SOLIDPIVOT)
		EntityFX entity,1
		EntityColor entity,r,g,b
		ScaleEntity entity,0.5,0.5,0.5
		
		; add orbit
		CreateOrbit(360,PLANETORBITS,radius,radius,255,255,255,0.1,0,0,0)
		
		; add planet
		S\planet[p]=PLANETcreate(p,parent%,name$,radius#,period#,size#,r%,g%,b%,speed,entity,random)
		
	Next
	
	; read moon data
	Restore MOONDATA
	
	For m=1 To MOONS
			
		Read parent,name,radius,period,size,r,g,b
		
		; calc speed and initial position
		speed=YEAR/Float(period)
		If RANDOMIZE Then random=Rnd(0,2^16)
		
		; create sphere object
		entity=CreateSphere(16,SOLIDPIVOT)
		EntityFX entity,1
		EntityColor entity,r,g,b
		ScaleEntity entity,0.125,0.125,0.125
		
		; add orbit
		orbit=CreateOrbit(360,0,radius,radius,255,255,255,0.1)
		EntityFX orbit,1+16
		EntityAlpha orbit,0.1
		EntityAutoFade orbit,1,100
		
		; increase planet's moon counter
		S\planet[parent]\MOONS=S\planet[parent]\MOONS+1
		
		; add moon
		S\planet[parent]\moon[S\planet[parent]\MOONS]=MOONcreate(S\planet[parent]\MOONS,parent%,name$,radius#,period#,size#,r%,g%,b%,speed,entity,random,orbit)
			
	Next
	
End Function


;===========================================================================
; update object positions
;===========================================================================

Function UpdateObjects()
	
	Local p.Planet,m.Moon,i%
	
	For p.Planet = Each Planet
		
		UpdatePlanet(p\id)
		
		; only show text if object is in view or text flag enabled
		If EntityInView(p\entity,CAM) And INFO Then
			
			CameraProject CAM,EntityX(p\entity),EntityY(p\entity),EntityZ(p\entity)
			Text ProjectedX(),ProjectedY(),p\name,1
			
		EndIf
		
		For i=1 To p\MOONS
			
			m.Moon = MOONdata(p\id,i)
			
			If m<>Null Then
				
				UpdateMoon(p\id,m\id)
				
				; only show text if object is in view or text flag enabled
				If EntityInView(m\entity,CAM) And INFO And EntityDistance(CAM,m\entity)<100*ZOOM Then
					
					CameraProject CAM,EntityX(m\entity),EntityY(m\entity),EntityZ(m\entity)
					Text ProjectedX(),ProjectedY(),m\name,1
					
				EndIf
				
			EndIf
			
		Next
		
	Next
	
End Function


;===========================================================================
; create a planet object
;===========================================================================

Function PLANETcreate.Planet(id%,parent%,name$,radius#,period#,size#,r%,g%,b%,speed#,entity%,random%)
	
	Local p.Planet = New Planet
	
	p\id=id
	p\parent=parent
	p\name=name
	p\radius=radius
	p\period=period
	p\size=size
	p\r=r
	p\g=g
	p\b=b
	p\SPEED=speed
	p\entity=entity
	p\random=random
	
	Return p
	
End Function


;===========================================================================
; create a moon object
;===========================================================================

Function MOONcreate.Moon(id%,parent%,name$,radius#,period#,size#,r%,g%,b%,speed#,entity%,random%,orbit%)
	
	Local m.Moon = New Moon
	
	m\id=id
	m\parent=parent
	m\name=name
	m\radius=radius
	m\period=period
	m\size=size
	m\r=r
	m\g=g
	m\b=b
	m\SPEED=speed
	m\entity=entity
	m\random=random
	m\orbit=orbit
	
	Return m
	
End Function


;===========================================================================
; return a moon object handle
;===========================================================================

Function MOONdata.Moon(planet,moon)

	Return S\planet[planet]\moon[moon]

End Function


;===========================================================================
; return a planet object handle
;===========================================================================

Function PLANETdata.Planet(planet)
	
	Return S\planet[planet]
	
End Function


;===========================================================================
; update a single planet object position according to time
;===========================================================================

Function UpdatePlanet(id%)
	
	Local radiusx#,radiusy#
	Local xcos#=Cos(360)
	Local xsin#=Sin(360)
	Local tmpx#,tmpy#
	
	Local p.Planet= PLANETdata(id)
	
	radiusx=p\radius
	radiusy=p\radius
		
	tmpx#=(Cos((DAYS*(360.0/YEAR)*p\SPEED)+p\random)*radiusx#)
	tmpy#=(Sin((DAYS*(360.0/YEAR)*p\SPEED)+p\random)*radiusy#)
	PositionEntity p\entity,tmpx#*xcos#+tmpy*-xsin#,0,tmpx#*xsin#+tmpy*xcos#
		
End Function


;===========================================================================
; update a single moon object position according to time
;===========================================================================

Function UpdateMoon(planet%,id%)
	
	Local radiusx#,radiusy#
	Local xcos#=Cos(360)
	Local xsin#=Sin(360)
	Local tmpx#,tmpy#,addx#,addy#,addz#
	
	Local p.Planet = PLANETdata(planet)
	Local m.Moon= MOONdata(planet,id)
	
	radiusx=m\radius
	radiusy=m\radius
	
	addx=EntityX(p\entity)
	addy=EntityY(p\entity)
	addz=EntityZ(p\entity)
	
	tmpx#=(Cos((DAYS*(360.0/YEAR)*m\SPEED)+m\random)*radiusx#)+addx
	tmpy#=(Sin((DAYS*(360.0/YEAR)*m\SPEED)+m\random)*radiusy#)+addz
	
	PositionEntity m\entity,tmpx#*xcos#+tmpy*-xsin#,addy,tmpx#*xsin#+tmpy*xcos#
	PositionEntity m\orbit,addx,addy,addz
	
End Function


;===========================================================================
; round cut a value for better display
;===========================================================================

Function Round$(value$,digits%=1)
	
	Local midpoint%=Instr(value,".")
	Local prefix$,suffix$,zero%,i%
	
	If midpoint Then
		
		prefix=Mid(value,1,midpoint-1)
		suffix=Mid(value,midpoint+1,Len(value))
		suffix=Mid(suffix,1,digits)
		zero=digits-Len(suffix)
		For i=1 To zero : suffix=suffix+"0" : Next
		
	Else
		
		prefix=Int(value)
		For i=1 To digits : suffix=suffix+"0" : Next
		
	EndIf
	
	Return prefix+"."+suffix
	
End Function 


;===========================================================================
; create a wireframe orbit mesh
;===========================================================================

Function CreateOrbit(segments%,mesh=0,r1#=1,r2#=1.5,r%=255,g%=255,b%=255,a#=1.0,sx#=0.0,sy#=0.0,sz#=0.0)
	
	Local la#,na#,i%
	
	For i = 1 To segments
		
		na=la+(360.0/segments)
		
		mesh=CreateLine(mesh,(Cos(la)*r1)+sx,sy,(Sin(la)*r2)+sz,(Cos(na)*r1)+sx,sy,(Sin(na)*r2)+sz,r,g,b,a)
		
		la=na
		
	Next
	
	Return mesh
	
End Function


;===========================================================================
; create a single wireframe line
;===========================================================================

Function CreateLine(mesh,x0#,y0#,z0#,x1#,y1#,z1#,r%=255,g%=255,b%=255,a#=1.0)
	
	Local surf%,v1%,v2%
	
	If mesh Then
		
		surf=GetSurface(mesh,1)
		
	Else
		
		mesh = CreateMesh()
		surf = CreateSurface(mesh)
		EntityFX mesh,1+2+16+32
		
	End If
	
    v1=AddVertex(surf,x1,y1,z1)
	v2=AddVertex(surf,x0,y0,z0)
	AddTriangle surf,v1,v1,v2
	
    VertexColor surf,v1,r,g,b,a
	VertexColor surf,v2,r,g,b,a
	
	Return mesh
	
End Function


;==========================================================================
; create a quad
;===========================================================================

Function CreateQuad(parent%=False,scale#=1.0,tex%=False,blend%=False,fx%=False,r%=255,g%=255,b%=255,a#=1.0)
	
	Local mesh%=CreateMesh()
	Local surf%=CreateSurface(mesh)
	
	Local v0%=AddVertex(surf, 1, 1,0,0,0)
	Local v1%=AddVertex(surf,-1, 1,0,1,0)
	Local v2%=AddVertex(surf,-1,-1,0,1,1)
	Local v3%=AddVertex(surf, 1,-1,0,0,1)
	
	AddTriangle surf,v0,v1,v2
	AddTriangle surf,v0,v2,v3
	
	If parent Then EntityParent mesh,parent
	If fx Then EntityFX mesh,fx
	If tex Then EntityTexture mesh,tex
	If blend Then EntityBlend mesh,blend
	
	EntityColor mesh,r,g,b
	EntityAlpha mesh,a
	
	VertexColor surf,v0,r,g,b,a
	VertexColor surf,v1,r,g,b,a
	VertexColor surf,v2,r,g,b,a
	VertexColor surf,v3,r,g,b,a
	
	ScaleEntity mesh,scale,scale,scale
	
	Return mesh
	
End Function


;==========================================================================
; create sun texture
;===========================================================================

Function CreateSunTexture()
	
	Local tex%=CreateTexture(512,512,3)
	Local tb%=TextureBuffer(tex)
	
	Local i#,j%,col%,rgb%
	
	SetBuffer tb
	LockBuffer tb
	
	For j=0 To 255
		
		col=255-j
		If col>255 Then col=255
		rgb=col*$1000000+col*$10000+col*$100+col
		
		For i=0 To 360 Step 0.1
			
			WritePixelFast 256+(Sin(i)*j),256+(Cos(i)*j),rgb,tb
			
		Next
		
	Next
	
	UnlockBuffer tb
	SetBuffer BackBuffer()
	
	Return tex
	
End Function


.PLANETDATA

;==========================================================================
;    Parent   Name           Radius      Period      Size     R     G     B
;==========================================================================
Data 0      , "Mercury"  ,   57.909 ,    87.989 ,   4.878 , 255 , 128 ,   0
Data 0      , "Venus"    ,  108.160 ,   224.701 ,   5.000 , 255 , 255 ,   0
Data 0      , "Earth"    ,  149.600 ,   365.256 ,  12.756 ,   0 ,   0 , 255
Data 0      , "Mars"     ,  227.990 ,   686.980 ,   6.794 , 255 ,   0 ,   0
Data 0      , "Jupiter"  ,  778.360 ,  4331.936 , 142.984 , 255 , 192 ,   0
Data 0      , "Saturn"   , 1433.400 , 10759.346 , 120.536 , 255 , 224 ,   0
Data 0      , "Uranus"   , 2872.400 , 30685.522 ,  51.118 ,   0 , 128 , 255
Data 0      , "Neptune"  , 4495.000 , 60190.536 ,  49.528 ,   0 , 255 , 255
Data 0      , "Pluto"    , 5906.400 , 90466.606 ,   2.390 , 255 , 255 , 255


;==========================================================================
;    Parent   Name           Radius      Period      Size     R     G     B
;==========================================================================

.MOONDATA
Data 3     , "Moon"      ,    1.000 ,     1.000 ,   3.476 , 255 , 255 , 255
Data 4     , "Phobos"    ,    1.000 ,     1.000 ,   0.027 , 255 , 255 , 255
Data 4     , "Deimos"    ,    2.000 ,     2.000 ,   0.015 , 255 , 255 , 255
Data 5     , "Amalthea"  ,    1.000 ,     1.000 ,   0.167 , 255 , 255 , 255
Data 5     , "Io"        ,    2.000 ,     2.000 ,   3.643 , 255 , 255 , 255
Data 5     , "Europa"    ,    3.000 ,     4.000 ,   3.122 , 255 , 255 , 255
Data 5     , "Ganymede"  ,    4.000 ,     8.000 ,   5.262 , 255 , 255 , 255
Data 5     , "Callisto"  ,    5.000 ,    16.000 ,   4.821 , 255 , 255 , 255
Data 5     , "Leda"      ,    6.000 ,    32.000 ,   0.020 , 255 , 255 , 255
Data 5     , "Himalia"   ,    7.000 ,    64.000 ,   0.170 , 255 , 255 , 255
Data 5     , "Elara"     ,    8.000 ,   128.000 ,   0.086 , 255 , 255 , 255
Data 5     , "Pasiphae"  ,    9.000 ,   256.000 ,   0.056 , 255 , 255 , 255
Data 6     , "Mimas"     ,    1.000 ,     1.000 ,   0.397 , 255 , 255 , 255
Data 6     , "Enceladus" ,    2.000 ,     2.000 ,   0.504 , 255 , 255 , 255
Data 6     , "Tethys"    ,    3.000 ,     4.000 ,   1.060 , 255 , 255 , 255
Data 6     , "Dione"     ,    4.000 ,     8.000 ,   1.127 , 255 , 255 , 255
Data 6     , "Rhea"      ,    5.000 ,    16.000 ,   1.528 , 255 , 255 , 255
Data 6     , "Titan"     ,    6.000 ,    32.000 ,   5.150 , 255 , 255 , 255
Data 6     , "Hyperion"  ,    7.000 ,    64.000 ,   0.266 , 255 , 255 , 255
Data 6     , "Iapetus"   ,    8.000 ,   128.000 ,   1.436 , 255 , 255 , 255
Data 6     , "Phoebe"    ,    9.000 ,   256.000 ,   0.220 , 255 , 255 , 255
Data 7     , "Miranda"   ,    1.000 ,      1.00 ,   0.472 , 255 , 255 , 255
Data 7     , "Ariel"     ,    2.000 ,      2.00 ,   1.158 , 255 , 255 , 255
Data 7     , "Umbriel"   ,    3.000 ,      4.00 ,   1.169 , 255 , 255 , 255
Data 7     , "Titania"   ,    4.000 ,      8.00 ,   1.578 , 255 , 255 , 255
Data 7     , "Oberon"    ,    5.000 ,     16.00 ,   1.523 , 255 , 255 , 255
Data 8     , "Triton"    ,    1.000 ,      1.00 ,   2.707 , 255 , 255 , 255
Data 8     , "Nereid"    ,    2.000 ,      2.00 ,   0.340 , 255 , 255 , 255
Data 9     , "Charon"    ,    1.000 ,      1.00 ,   1.212 , 255 , 255 , 255
