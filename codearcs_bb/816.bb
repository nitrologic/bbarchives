; ID: 816
; Author: Pepsi
; Date: 2003-10-26 02:03:44
; Title: Triangulate Toy
; Description: Triangulate Points of a Polygon - Oct 27,03: Clean up code, bug fix

; -----------------------------------------------------------------------------------
; A Triangulate 3D Polygon Example
; Author: Todd Riggins
; 10-25-03
; It is assumed that this method will run on clockwise convex/concave polygons.
; It is assumed that a consecutive point will not create a line across a given edge.
; This is not plug and play code. 
; -----------------------------------------------------------------------------------

; -----------------------------------------------------------------------------------
; Initializations
; -----------------------------------------------------------------------------------
Type point3d
	Field mesh
	Field used
	Field x#
	Field y#
	Field z#
End Type

Dim this3dPointArray.point3d(100)
Global num3dPoints=0
Global currentmode=0

; ### input helpers
key_space=0
key_w=0
key_a=0
key_s=0
key_d=0
mouse_button1=0 

; -----------------------------------------------------------------------------------
; Set Graphics
; -----------------------------------------------------------------------------------
Graphics3D 640,480,0,2
SetBuffer BackBuffer()
HidePointer()


; ### Make Pivot for Camera to rotate around
Global camerapivot=CreatePivot()

; ### Set Camera
Global camera=CreateCamera(camerapivot)
PositionEntity camera,0,3,-4
CameraRange camera,.1,100
RotateEntity camera,24,0,0 

; ### Create Mouse Cursor
Global mouse_image=CreateImage(16,16)
SetBuffer ImageBuffer(mouse_image)
ClsColor 0,0,0
Cls
Color 255,255,255
Line 0,0,7,0
Line 0,0,0,7
Line 0,7,7,0
Line 4,4,15,15
MaskImage mouse_image,0,0,0

; ### Create Point Selection Sphere
Global sphereselect_mesh=CreateSphere()
ScaleEntity sphereselect_mesh,0.07,0.07,0.07
EntityColor sphereselect_mesh,255,255,0
HideEntity sphereselect_mesh

; ### Create Result Mesh for Poly Triangulation Result
Global ResultMesh=CreateMesh()

; ### Create static xz plane
Global xzgrid_tex=CreateTexture(256,256,9)
SetBuffer TextureBuffer(xzgrid_tex)
ClsColor 30,128,200
Cls
Color 255,255,255
Line 0,0,255,0
Line 0,0,0,255
Global xzgrid_plane=CreatePlane()
EntityTexture xzgrid_plane,xzgrid_tex

; ### Create canvas mesh to create triangulated poly on.
; ### This canvas will be allowed to rotate before creating triangulated poly.
Global canvas_tex=CreateTexture(256,256,9)
SetBuffer TextureBuffer(canvas_tex)
ClsColor 32,32,32
Cls
Color 255,255,0
Line 0,0,255,0
Line 0,0,0,255
Global canvas_mesh=CreateMesh()
Global canvas_surface=CreateSurface(canvas_mesh)
size=3
AddVertex(canvas_surface,-size,0, size,0,0)
AddVertex(canvas_surface, size,0, size,1,0)
AddVertex(canvas_surface,-size,0,-size,0,1)
AddVertex(canvas_surface, size,0,-size,1,1)
AddTriangle canvas_surface,2,0,1
AddTriangle canvas_surface,2,1,3
EntityTexture canvas_mesh,canvas_tex
PositionEntity canvas_mesh,0,1,0
RotateEntity canvas_mesh,-70,-25,0
UpdateNormals canvas_mesh
EntityPickMode canvas_mesh,2

; calculate normalized normals...
Global thisPivot1=CreatePivot(canvas_mesh)
PositionEntity thisPivot1,-size,0, size
Global thisPivot2=CreatePivot(canvas_mesh)
PositionEntity thisPivot2, size,0, size
Global thisPivot3=CreatePivot(canvas_mesh)
PositionEntity thisPivot3,-size,0,-size
Global canvas_nx#
Global canvas_ny#
Global canvas_nz#
Update_Canvas_FaceNormal()

; ### Set default buffer and drawing colors...
SetBuffer BackBuffer()
ClsColor 0,0,0
Cls
Color 255,255,255

; -----------------------------------------------------------------------------------
; Main
; -----------------------------------------------------------------------------------
While Not KeyHit(1)

	If currentmode=0
;-------rotate canvas via WASD keys
		If KeyDown(17) And key_w=0
			key_w=1
			TurnEntity canvas_mesh,-5,0,0
		EndIf
		If KeyDown(17)=False And key_w=1 Then key_w=0
		If KeyDown(30) And key_a=0
			key_a=1
			TurnEntity canvas_mesh,0,-5,0
		EndIf
		If KeyDown(30)=False And key_a=1 Then key_a=0
		If KeyDown(31) And key_s=0
			TurnEntity canvas_mesh,5,0,0
			key_s=1
		EndIf
		If KeyDown(31)=False And key_s=1 Then key_s=0
		If KeyDown(32) And key_d=0
			TurnEntity canvas_mesh,0,5,0
			key_d=1
		EndIf
		If KeyDown(32)=False And key_d=1 Then key_d=0

		; switch to draw poly mode		
		If KeyDown(57) And key_space=0
			key_space=1
		EndIf
		If KeyDown(57)=False And key_space=1
			key_space=0
			Update_Canvas_FaceNormal()
			MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
			currentmode=1
		EndIf
	Else
;-------draw poly mode
		If currentmode=1
			; if we have 2 or more points, then we can generate
			;  a triangulated poly.
			If num3dPoints>2
				; Generate Triangulated Poly
				If KeyDown(57) And key_space=0
					key_space=1
				EndIf
				If KeyDown(57)=False And key_space=1
					key_space=0
					currentmode=2
					HideEntity sphereselect_mesh				
					HideEntity canvas_mesh	
					; Triangulate 
					triangulate()	
						
					; Reset this3dPointArray.point3d(i) array
					For i=0 To num3dPoints-1
						If this3dPointArray.point3d(i)\mesh>0
							HideEntity this3dPointArray.point3d(i)\mesh
							FreeEntity this3dPointArray.point3d(i)\mesh
							this3dPointArray.point3d(i)\mesh=0
							this3dPointArray.point3d(i)\used=False
						EndIf
					Next
					
			
				EndIf
			Else
				;display error
			EndIf
	
			; Set Current Selector Point to Mouse coords
			If currentmode=1 Then SetPoint()
			
			; draw poly points
			If MouseDown(1) And mouse_button1=0 
				mouse_button1=1
				NewPolyPoint()
			EndIf
			If MouseDown(1)=False And mouse_button1=1 Then mouse_button1=0 
		Else
;-----------view triangulated polygon
			If currentmode=2
			
				; rotate camera around pivot to view created triangulated poly.
				yrot#=yrot#+1.0
				If yrot#>360 Then yrot#=yrot#-360
				RotateEntity camerapivot,0,yrot#,0
				
				; reset pivot orientation and delete Result surfaces to
				; start over again...
				If KeyDown(57) And key_space=0
					key_space=1
				EndIf
				If KeyDown(57)=False And key_space=1
					key_space=0
					currentmode=0
					yrot#=0
					RotateEntity camerapivot,0,yrot#,0
					
					numofsurfaces=CountSurfaces(ResultMesh)
					For i=1 To numofsurfaces
						ResultSurface=GetSurface(ResultMesh,i)
						ClearSurface ResultSurface
					Next
					num3dPoints=0
					ShowEntity canvas_mesh					
				EndIf
			
			EndIf
			
		EndIf
	EndIf
	
	UpdateWorld
	RenderWorld
	
	
;---Output
	
	Text 0,0,"Current Mode="+currentmode
	Text 0,15,"Number of Points="+num3dPoints
	
	Text 120,0,"* Key [Esc] = End Program"
	If currentmode=0
		Text 0,60,"[Rotate Canvas Mode]"
		Text 0,75,"Key [W] = Rotate -X Axis"
		Text 0,90,"Key [S] = Rotate X Axis"
		Text 0,105,"Key [A] = Rotate -Y Axis"
		Text 0,120,"Key [D] = Rotate Y Axis"
		Text 0,150,"Key [Space] = Draw Poly Points Mode"
	EndIf

	If currentmode=1
		DrawImage mouse_image,MouseX(),MouseY()	
		Text 0,60,"[Draw Poly Points Mode]"
		Text 0,75,"Point mouse on canvas and"
		Text 0,90,"use left mouse button to"
		Text 0,105,"draw 3 or more points in a"
		Text 0,120,"clockwise orderly fashion."
		If num3dPoints>2
			Text 0,150,"Key [Space] = Triangulate the Poly"
		EndIf		
	EndIf

	If currentmode=2
		Text 0,60,"[View Triangulated Poly]"
		Text 0,90,"Key [Space] = Rotate Canvas Mode"
	EndIf

	
	Flip
Wend
End


; -----------------------------------------------------------------------------------
; Functions
; -----------------------------------------------------------------------------------

; Pick Point on Canvas Mesh
Function SetPoint()
	If CameraPick(camera,MouseX(),MouseY())=canvas_mesh
		PositionEntity sphereselect_mesh,PickedX#(),PickedY#(),PickedZ#()
		ShowEntity sphereselect_mesh
	Else
		HideEntity sphereselect_mesh		
	EndIf
End Function

; Create new poly point
Function NewPolyPoint()

	If CameraPick(camera,MouseX(),MouseY())=canvas_mesh
		num3dPoints=num3dPoints+1
	
		this3dPointArray.point3d(num3dPoints-1)=New point3d
		this3dPointArray.point3d(num3dPoints-1)\mesh=CreateSphere()
		ScaleEntity this3dPointArray.point3d(num3dPoints-1)\mesh,0.07,0.07,0.07
		EntityColor this3dPointArray.point3d(num3dPoints-1)\mesh,255,63,63	
	
		this3dPointArray.point3d(num3dPoints-1)\x#=PickedX#()
		this3dPointArray.point3d(num3dPoints-1)\y#=PickedY#()
		this3dPointArray.point3d(num3dPoints-1)\z#=PickedZ#()
		
		locx#=this3dPointArray.point3d(num3dPoints-1)\x#
		locy#=this3dPointArray.point3d(num3dPoints-1)\y#
		locz#=this3dPointArray.point3d(num3dPoints-1)\z#
		
		PositionEntity this3dPointArray.point3d(num3dPoints-1)\mesh,locx#,locy#,locz#
	EndIf
End Function

Function Update_Canvas_FaceNormal()
	Local x0#,y0#,z0#,x1#,y1#,z1#,x2#,y2#,z2#
	Local vectorx1#,vectory1#,vectorz1#,vectorx2#,vectory2#,vectorz2#
	Local normx#,normy#,normz#,nx1#,ny1#,nz1#
	Local magnitude#
	
	; calculate normalized normals...
	x0#=EntityX#(thisPivot1,True)
	y0#=EntityY#(thisPivot1,True)
	z0#=EntityZ#(thisPivot1,True)
	x1#=EntityX#(thisPivot2,True)
	y1#=EntityY#(thisPivot2,True)
	z1#=EntityZ#(thisPivot2,True)
	x2#=EntityX#(thisPivot3,True)
	y2#=EntityY#(thisPivot3,True)
	z2#=EntityZ#(thisPivot3,True)
	vectorx1#=x1#-x0#
	vectory1#=y1#-y0#
	vectorz1#=z1#-z0#
	vectorx2#=x2#-x0#
	vectory2#=y2#-y0#
	vectorz2#=z2#-z0#
	normx#=(vectory1# * vectorz2#)-(vectorz1# * vectory2#)
	normy#=(vectorz1# * vectorx2#)-(vectorx1# * vectorz2#)
	normz#=(vectorx1# * vectory2#)-(vectory1# * vectorx2#)
	magnitude#=(Sqr((normx#*normx#)+(normy#*normy#)+(normz#*normz#)))
	If (magnitude#>-0.00001 And magnitude#<0.00001)
		;rem do nothing, cannot divide by zero!
	Else
		nx1#=normx#/magnitude#
		ny1#=normy#/magnitude#
		nz1#=normz#/magnitude#
		normx#=nx1#
		normy#=ny1#
		normz#=nz1#
	EndIf
	canvas_nx#=normx#
	canvas_ny#=normy#
	canvas_nz#=normz#
End Function

; Does the 3 vertex points create the same facing as the canvas?
Function IsSameNormal(x0#,y0#,z0#,x1#,y1#,z1#,x2#,y2#,z2#)

	; get face normal for the three provided vertex points
	vectorx1#=x1#-x0#
	vectory1#=y1#-y0#
	vectorz1#=z1#-z0#
	vectorx2#=x2#-x0#
	vectory2#=y2#-y0#
	vectorz2#=z2#-z0#
	normx#=(vectory1# * vectorz2#)-(vectorz1# * vectory2#)
	normy#=(vectorz1# * vectorx2#)-(vectorx1# * vectorz2#)
	normz#=(vectorx1# * vectory2#)-(vectory1# * vectorx2#)
	magnitude#=(Sqr((normx#*normx#)+(normy#*normy#)+(normz#*normz#)))
	If (magnitude#>-0.00001 And magnitude#<0.00001)
		;rem do nothing, cannot divide by zero!
	Else
		nx1#=normx#/magnitude#
		ny1#=normy#/magnitude#
		nz1#=normz#/magnitude#
		normx#=nx1#
		normy#=ny1#
		normz#=nz1#
	EndIf

	; Now just get the distance from two vertex points created from normal directions. 
	
	rangevalue#=10
	
	px1#=x1#+(normx#*rangevalue#) ; Add current three vertex point's face normal to a vertex point
	py1#=y1#+(normy#*rangevalue#) ; Add current three vertex point's face normal to a vertex point
	pz1#=z1#+(normz#*rangevalue#) ; Add current three vertex point's face normal to a vertex point
	px2#=x1#+(canvas_nx#*rangevalue#) ; Add current canvas face normal to a vertex point
	py2#=y1#+(canvas_ny#*rangevalue#) ; Add current canvas face normal to a vertex point
	pz2#=z1#+(canvas_nz#*rangevalue#) ; Add current canvas face normal to a vertex point

	; get the distance between two points.
	; note: if dist# = ( rangevalue# * 2 = Max distance ), then both faces face complete opposite directions )
	dist#=Sqr(((px1#-px2#)^2)+((py1#-py2#)^2)+((pz1#-pz2#)^2))
	;DebugLog "dist="+dist#
	
	; give a little tolerance to planar 
	If dist#<1.0 Then Return True
	Return False	
End Function

Function triangulate()
	Local vindex=0
	Local pos0=0
	Local pos1=1
	Local pos2=2
	Local skipused=0
	Local numofUsed=0

.loop	
	x0#=this3dPointArray.point3d(pos0)\x#
	y0#=this3dPointArray.point3d(pos0)\y#
	z0#=this3dPointArray.point3d(pos0)\z#
	x1#=this3dPointArray.point3d(pos1)\x#
	y1#=this3dPointArray.point3d(pos1)\y#
	z1#=this3dPointArray.point3d(pos1)\z#
	x2#=this3dPointArray.point3d(pos2)\x#
	y2#=this3dPointArray.point3d(pos2)\y#
	z2#=this3dPointArray.point3d(pos2)\z#
				
	result=IsSameNormal(x0#,y0#,z0#,x1#,y1#,z1#,x2#,y2#,z2#)

	If result=True	
		For i=0 To (num3dPoints-1)
			If (i<>pos0) And (i<>pos1) And (i<>pos2)
				If IsPointInsideTri(i,x0#,y0#,z0#,x1#,y1#,z1#,x2#,y2#,z2#)=True
					result=False
				EndIf
			EndIf
		Next	
	EndIf
	
	If result=True
		NewResultSurface=CreateSurface(ResultMesh)
		AddVertex NewResultSurface,x0#,y0#,z0#
		AddVertex NewResultSurface,x1#,y1#,z1#
		AddVertex NewResultSurface,x2#,y2#,z2#
		
		AddTriangle NewResultSurface,2+vindex,vindex,vindex+1
		
		brush=CreateBrush(Rand(127)+128,Rand(127)+128,Rand(127)+128)
		PaintSurface NewResultSurface,brush
		FreeBrush brush
		
		this3dPointArray.point3d(pos1)\used=True
		numofUsed=numofUsed+1
		EntityColor this3dPointArray.point3d(pos1)\mesh,63,255,63			
		
		UpdateWorld
		RenderWorld

		
		Text 0,0,"Press Left Mosue Button to Advance"
		Text 0,10,"Press [Esc] to quit triangulating"
		Flip		
		
		While MouseDown(1)=False
			If KeyDown(1)=True Then Goto done
		Wend
		While MouseDown(1)=True
		Wend
		
		If numofUsed=num3dPoints Then Goto done
		
		If skipused=1
			pos1=pos2
		Else
			pos1=pos1+1
			If pos1>(num3dPoints-1) Then pos1=0
		EndIf
.addpos22
		If KeyDown(1)=True Then Goto done			
		pos2=pos2+1
		If pos2>(num3dPoints-1) Then pos2=0
		If this3dPointArray.point3d(pos2)\used=True
			skipused=1
			Goto addpos22
		EndIf
	Else
		pos0=pos1
.addpos1
		If KeyDown(1)=True Then Goto done	
		pos1=pos1+1
		If pos1>(num3dPoints-1) Then pos1=0
		If this3dPointArray.point3d(pos1)\used=True
			Goto addpos1
		EndIf		
		pos2=pos1
.addpos2
		If KeyDown(1)=True Then Goto done
		pos2=pos2+1
		If pos2>(num3dPoints-1) Then pos2=0
		If this3dPointArray.point3d(pos2)\used=True
			skipused=1
			Goto addpos2
		EndIf
	EndIf
	
	If pos2=pos0 Then Goto done
	If usedverts>(num3dPoints-1) Then Goto done
	Goto loop
.done

End Function


Function IsPointInsideTri(PointIndex,x0#,y0#,z0#,x1#,y1#,z1#,x2#,y2#,z2#)

   ; First get angles from 3 corners of the triangle poly
   vx1#=x1#-x0#
   vy1#=y1#-y0#
   vz1#=z1#-z0#
   vx2#=x2#-x0#
   vy2#=y2#-y0#
   vz2#=z2#-z0#
   vlen1#=Sqr((vx1#*vx1#)+(vy1#*vy1#)+(vz1#*vz1#))
   vlen2#=Sqr((vx2#*vx2#)+(vy2#*vy2#)+(vz2#*vz2#))
   angle1#=ACos(((vx1# * vx2#) + (vy1# * vy2#) + (vz1# * vz2#))/(vlen1#*vlen2#))

   vx1#=x0#-x1#
   vy1#=y0#-y1#
   vz1#=z0#-z1#
   vx2#=x2#-x1#
   vy2#=y2#-y1#
   vz2#=z2#-z1#
   vlen1#=Sqr((vx1#*vx1#)+(vy1#*vy1#)+(vz1#*vz1#))
   vlen2#=Sqr((vx2#*vx2#)+(vy2#*vy2#)+(vz2#*vz2#))
   angle2#=ACos(((vx1# * vx2#) + (vy1# * vy2#) + (vz1# * vz2#))/(vlen1#*vlen2#))

   vx1#=x0#-x2#
   vy1#=y0#-y2#
   vz1#=z0#-z2#
   vx2#=x1#-x2#
   vy2#=y1#-y2#
   vz2#=z1#-z2#
   vlen1#=Sqr((vx1#*vx1#)+(vy1#*vy1#)+(vz1#*vz1#))
   vlen2#=Sqr((vx2#*vx2#)+(vy2#*vy2#)+(vz2#*vz2#))
   angle3#=ACos(((vx1# * vx2#) + (vy1# * vy2#) + (vz1# * vz2#))/(vlen1#*vlen2#))

   ; Now find the angle of the 3d point relative to each
   ; corner of the triangle poly. Also the reverse angle
   ; for each...
   vx1#=x1#-x0#
   vy1#=y1#-y0#
   vz1#=z1#-z0#
   vx2#=this3dPointArray.point3d(PointIndex)\x#-x0#
   vy2#=this3dPointArray.point3d(PointIndex)\y#-y0#
   vz2#=this3dPointArray.point3d(PointIndex)\z#-z0#
   vlen1#=Sqr((vx1#*vx1#)+(vy1#*vy1#)+(vz1#*vz1#))
   vlen2#=Sqr((vx2#*vx2#)+(vy2#*vy2#)+(vz2#*vz2#))
   pointAngle1#=ACos(((vx1# * vx2#) + (vy1# * vy2#) + (vz1# * vz2#))/(vlen1#*vlen2#))

   vx1#=this3dPointArray.point3d(PointIndex)\x#-x0#
   vy1#=this3dPointArray.point3d(PointIndex)\y#-y0#
   vz1#=this3dPointArray.point3d(PointIndex)\z#-z0#
   vx2#=x2#-x0#
   vy2#=y2#-y0#
   vz2#=z2#-z0#
   vlen1#=Sqr((vx1#*vx1#)+(vy1#*vy1#)+(vz1#*vz1#))
   vlen2#=Sqr((vx2#*vx2#)+(vy2#*vy2#)+(vz2#*vz2#))
   pointAngle11#=ACos(((vx1# * vx2#) + (vy1# * vy2#) + (vz1# * vz2#))/(vlen1#*vlen2#))

   vx1#=x0#-x1#
   vy1#=y0#-y1#
   vz1#=z0#-z1#
   vx2#=this3dPointArray.point3d(PointIndex)\x#-x1#
   vy2#=this3dPointArray.point3d(PointIndex)\y#-y1#
   vz2#=this3dPointArray.point3d(PointIndex)\z#-z1#
   vlen1#=Sqr((vx1#*vx1#)+(vy1#*vy1#)+(vz1#*vz1#))
   vlen2#=Sqr((vx2#*vx2#)+(vy2#*vy2#)+(vz2#*vz2#))
   pointAngle2#=ACos(((vx1# * vx2#) + (vy1# * vy2#) + (vz1# * vz2#))/(vlen1#*vlen2#))

   vx1#=this3dPointArray.point3d(PointIndex)\x#-x1#
   vy1#=this3dPointArray.point3d(PointIndex)\y#-y1#
   vz1#=this3dPointArray.point3d(PointIndex)\z#-z1#
   vx2#=x2#-x1#
   vy2#=y2#-y1#
   vz2#=z2#-z1#
   vlen1#=Sqr((vx1#*vx1#)+(vy1#*vy1#)+(vz1#*vz1#))
   vlen2#=Sqr((vx2#*vx2#)+(vy2#*vy2#)+(vz2#*vz2#))
   pointAngle22#=ACos(((vx1# * vx2#) + (vy1# * vy2#) + (vz1# * vz2#))/(vlen1#*vlen2#))

   vx1#=x0#-x2#
   vy1#=y0#-y2#
   vz1#=z0#-z2#
   vx2#=this3dPointArray.point3d(PointIndex)\x#-x2#
   vy2#=this3dPointArray.point3d(PointIndex)\y#-y2#
   vz2#=this3dPointArray.point3d(PointIndex)\z#-z2#
   vlen1#=Sqr((vx1#*vx1#)+(vy1#*vy1#)+(vz1#*vz1#))
   vlen2#=Sqr((vx2#*vx2#)+(vy2#*vy2#)+(vz2#*vz2#))
   pointAngle3#=ACos(((vx1# * vx2#) + (vy1# * vy2#) + (vz1# * vz2#))/(vlen1#*vlen2#))

   vx1#=this3dPointArray.point3d(PointIndex)\x#-x2#
   vy1#=this3dPointArray.point3d(PointIndex)\y#-y2#
   vz1#=this3dPointArray.point3d(PointIndex)\z#-z2#
   vx2#=x1#-x2#
   vy2#=y1#-y2#
   vz2#=z1#-z2#
   vlen1#=Sqr((vx1#*vx1#)+(vy1#*vy1#)+(vz1#*vz1#))
   vlen2#=Sqr((vx2#*vx2#)+(vy2#*vy2#)+(vz2#*vz2#))
   pointAngle33#=ACos(((vx1# * vx2#) + (vy1# * vy2#) + (vz1# * vz2#))/(vlen1#*vlen2#))


   ; See If point lies inside of the angles.
   inside1=0
   If pointAngle1#<angle1# And pointAngle11#<angle1# Then inside1=1
   inside2=0
   If pointAngle2#<angle2# And pointAngle22#<angle2# Then inside2=1
   inside3=0
   If pointAngle3#<angle3# And pointAngle33#<angle3# Then inside3=1

	If inside1=1 And inside2=1 And inside3=1
		DebugLog "Point inside Tri!!!"
		Return True
	EndIf
   Return False
End Function
