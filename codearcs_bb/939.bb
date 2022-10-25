; ID: 939
; Author: Olive
; Date: 2004-02-20 07:58:15
; Title: Tunnel Effect
; Description: A tunnel effect like in demos

; CreateMyCylinder Example 
; ---------------------- 
; By Pigmin (alias Olive) 02-20-2004
;
; pigmin@wanadoo.fr
; 
; meet me at http://www.blitz3dfr.com (forum section)
;
;
;
; With wonderfull function :
;
; CreateMyCylinder
; ---------------------- 
; By: Todd Riggins 12-22-2003 
; 
; a CreateCylinder blitz-like function. Does the Same thing. Just wanted to do 
; this to see how to actaully create a cylinder with the addvertex/addtriangle 
; commands. Just thought I would share. 

; ----------------- 
; 2004-2-19 1:10:00 
; - Added ring segments. Ring Segment param of zero acts like the 
; blitz-like Function. IE: no ring segments. 
; - The added ring segments adds vertices to help bend cylinder tunnels or angle 
; pipe meshs. Ofcoarse, you will need to add your own code to manipulate the 
; cylinder's vertices. 
; 2004-2-18 21:00:00 
; - Fixed normal vectors problem: Needed to have cylinder ends as seperate surface 
; 
; Left sphere is created by the CreateMyCylinder function. 
; Right sphere is created by blitz's CreateCylinder command. 
; 
; Controls: 
; - Use mouse to rotate the cylinders 
; - wireframe toggle 
; - Esc key to escape 

; rediminsionable arrays 
Dim tRing(0) 
Dim bRing(0) 

Type TCtrlPoint
	Field x#
	Field y#
End Type

Const MAX_CTRL_POINTS = 16
Const TWIST# = 30

Global CtrlPoints.TCtrlPoint[MAX_CTRL_POINTS]
Global CtrlPointsPhase% = 0
Global CtrlPointsPhase2% = 0

Graphics3D 640,480,0,2
SetBuffer BackBuffer() 


AmbientLight 32,0,0

camera=CreateCamera() 
CameraRange camera,.1,2000
light=CreateLight() 
RotateEntity light,90,0,0 

; enter how many segments the sphere has 
Global vsegs=32 
Global rsegs=MAX_CTRL_POINTS

;Texture
tex=CreateTexture(32,32,1+8)
SetBuffer(TextureBuffer(tex))
colR = 128
For j = 0 To 31 Step 8
	colR = 64 - colR
	For i = 0 To 31 Step 8
		colR = 64 - colR
		Color colR,colR,colR
		Rect(i,j,8,8,1)
	Next
Next
SetBuffer (BackBuffer())
ScaleTexture tex,.25,.25

;Texture2
tex2=CreateTexture(64,64,1+4+8)
SetBuffer(TextureBuffer(tex2))
For j = 0 To 16
	colR = Rand(10,64)
	colG = Rand(0,128)
	colB = Rand(0,180)
	Color colR,colG,colB
	Rect(Rand(0,63),Rand(0,63),Rand(1,2),Rand(1,31))
	Rect(Rand(0,63),Rand(0,63),Rand(1,31),Rand(1,2))
Next
SetBuffer (BackBuffer())
ScaleTexture tex2,.1,.25
TextureBlend tex,2
TextureBlend tex2,3

; Create Cylinder manually 
mycylinder=CreateMyCylinder(vsegs,rsegs,False,0)
ScaleMesh mycylinder,6,40,6
RotateMesh mycylinder,90,0,0
FlipMesh mycylinder
PositionEntity mycylinder,0,0,39 
EntityTexture mycylinder,tex,0,0
EntityTexture mycylinder,tex2,0,1

mycylindercopy = CopyMesh(mycylinder)
HideEntity mycylindercopy

;allocate control points
For i = 0 To MAX_CTRL_POINTS
	CtrlPoints[i] = New TCtrlPoint
	CtrlPoints[i]\x# = (i*260/MAX_CTRL_POINTS)
	CtrlPoints[i]\y# = (i*460/MAX_CTRL_POINTS)
Next

; key helper 
wkey=0 

xx#=0
cnt#=0
freqX = 1
freqY = 1
While Not KeyDown( 1 ) 
	Tunnel_effect(mycylinder, mycylindercopy,  CtrlPointsPhase,  CtrlPointsPhase2)
	CtrlPointsPhase = CtrlPointsPhase + freqX
	CtrlPointsPhase2 = CtrlPointsPhase2 + freqY

	If (Not Rand(0,200))
		freqY = Rand(0,4)
	EndIf

	If (Not Rand(0,400))
		freqX = Rand(1,4)
	EndIf

	PositionTexture Tex,0,cnt#
	PositionTexture Tex2,cnt#,cnt#
	cnt#=cnt#+.01*(freqX+freqY)*.5

	If KeyDown(17) And wkey=0 
	wkey=1 
	EndIf 
	
	If KeyDown(17)=False And wkey=1 
	wkey=0 
	If wframe=0 
	wframe=1 
	Else 
	wframe=0 
	EndIf 
	If wframe=0 WireFrame False 
	If wframe=1 WireFrame True 
	EndIf 
	
	
	RenderWorld 

	Flip 
Wend 

End 

; --------------------------------------------------------- 
Function CreateMyCylinder(verticalsegments,ringsegments=0,solid=True,parent=0) 
	Local tr,tl,br,bl; side of cylinder 
	Local ts0,ts1,newts; top side vertexs 
	Local bs0,bs1,newbs; bottom side vertexs 
	If verticalsegments<3 Or verticalsegments>100 Then Return 0 
	If ringsegments<0 Or ringsegments>100 Then Return 0 
	
	thiscylinder=CreateMesh(parent) 
	thissurf=CreateSurface(thiscylinder) 
	If solid=True 
		thissidesurf=CreateSurface(thiscylinder) 
	EndIf 
	div#=Float(360.0/(verticalsegments)) 
	
	height#=1.0 
	ringSegmentHeight#=(height#*2.0)/(ringsegments+1) 
	upos#=1.0 
	udiv#=Float(1.0/(verticalsegments)) 
	vpos#=1.0 
	vdiv#=Float(1.0/(ringsegments+1)) 
	
	SideRotAngle#=90 
	
	; re-diminsion arrays to hold needed memory. 
	; this is used just for helping to build the ring segments... 
	Dim tRing(verticalsegments) 
	Dim bRing(verticalsegments) 
	
	;render end caps if solid 
	If solid=True 
		XPos#=-Cos(SideRotAngle#) 
		ZPos#=Sin(SideRotAngle#) 
		
		ts0=AddVertex(thissidesurf,XPos#,height,ZPos#,XPos#/2.0+0.5,ZPos#/2.0+0.5) 
		bs0=AddVertex(thissidesurf,XPos#,-height,ZPos#,XPos#/2.0+0.5,ZPos#/2.0+0.5) 
		
		SideRotAngle#=SideRotAngle#+div# 
		
		XPos#=-Cos(SideRotAngle#) 
		ZPos#=Sin(SideRotAngle#) 
		
		ts1=AddVertex(thissidesurf,XPos#,height,ZPos#,XPos#/2.0+0.5,ZPos#/2.0+0.5) 
		bs1=AddVertex(thissidesurf,XPos#,-height,ZPos#,XPos#/2.0+0.5,ZPos#/2.0+0.5) 
		
		For i=1 To (verticalsegments-2) 
			SideRotAngle#=SideRotAngle#+div# 
			
			XPos#=-Cos(SideRotAngle#) 
			ZPos#=Sin(SideRotAngle#) 
			
			newts=AddVertex(thissidesurf,XPos#,height,ZPos#,XPos#/2.0+0.5,ZPos#/2.0+0.5) 
			newbs=AddVertex(thissidesurf,XPos#,-height,ZPos#,XPos#/2.0+0.5,ZPos#/2.0+0.5) 
			
			AddTriangle(thissidesurf,ts0,ts1,newts) 
			AddTriangle(thissidesurf,newbs,bs1,bs0) 
			
			If i<(verticalsegments-2) 
				ts1=newts 
				bs1=newbs 
			EndIf 
		Next 
	EndIf 

	; ----------------------- 
	; middle part of cylinder 
	thisHeight#=height# 
	
	; top ring first 
	SideRotAngle#=90 
	XPos#=-Cos(SideRotAngle#) 
	ZPos#=Sin(SideRotAngle#) 
	thisUPos#=upos# 
	thisVPos#=0 
	tRing(0)=AddVertex(thissurf,XPos#,thisHeight,ZPos#,thisUPos#,thisVPos#) 
	For i=0 To (verticalsegments-1) 
		SideRotAngle#=SideRotAngle#+div# 
		XPos#=-Cos(SideRotAngle#) 
		ZPos#=Sin(SideRotAngle#) 
		thisUPos#=thisUPos#-udiv# 
		tRing(i+1)=AddVertex(thissurf,XPos#,thisHeight,ZPos#,thisUPos#,thisVPos#) 
	Next 
	
	For ring=0 To (ringsegments) 
	
		; decrement vertical segment 
		thisHeight=thisHeight-ringSegmentHeight# 
		
		; now bottom ring 
		SideRotAngle#=90 
		XPos#=-Cos(SideRotAngle#) 
		ZPos#=Sin(SideRotAngle#) 
		thisUPos#=upos# 
		thisVPos#=thisVPos#+vdiv# 
		bRing(0)=AddVertex(thissurf,XPos#,thisHeight,ZPos#,thisUPos#,thisVPos#) 
		For i=0 To (verticalsegments-1) 
			SideRotAngle#=SideRotAngle#+div# 
			XPos#=-Cos(SideRotAngle#) 
			ZPos#=Sin(SideRotAngle#) 
			thisUPos#=thisUPos#-udiv# 
			bRing(i+1)=AddVertex(thissurf,XPos#,thisHeight,ZPos#,thisUPos#,thisVPos#) 
		Next 
		
		; Fill in ring segment sides with triangles 
		For v=1 To (verticalsegments) 
			tl=tRing(v) 
			tr=tRing(v-1) 
			bl=bRing(v) 
			br=bRing(v-1) 
			
			AddTriangle(thissurf,tl,tr,br) 
			AddTriangle(thissurf,bl,tl,br) 
		Next 
		
		; make bottom ring segmentthe top ring segment for the next loop. 
		For v=0 To (verticalsegments) 
			tRing(v)=bRing(v) 
		Next 
	Next 
	
	UpdateNormals thiscylinder 
	Return thiscylinder 
End Function 

Function Tunnel_Effect(mesh, mesh_original, phase, phase2)

	cntsurf = CountSurfaces(mesh)
	
	surf=GetSurface(mesh,cntsurf)
	surf_original=GetSurface(mesh_original,cntsurf)
	
	cnt=CountVertices(surf)
	For s=0 To rsegs
	
		deltax# = Cos(CtrlPoints[s]\x# + phase)*(TWIST#/(s+1))
		deltay# = Sin(CtrlPoints[s]\y# + phase2)*(TWIST#/(s+1))
			
		firstVertice = s*(vsegs+1)
		endVertice = firstVertice + vsegs
		For a=firstVertice To endVertice
			x#=VertexX#(surf_original,a)
			y#=VertexY#(surf_original,a)
			z#=VertexZ#(surf_original,a)
								
			x#=x#+deltax#
			y#=y#+deltay#

			VertexCoords surf,a,x#,y#,z#
		Next
	Next
	UpdateNormals mesh
		
End Function
