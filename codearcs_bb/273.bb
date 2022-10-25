; ID: 273
; Author: Difference
; Date: 2002-04-05 03:03:00
; Title: Autosmooth
; Description: Give a mesh hard/soft edges. Weld or explode

;------------------------------------------------------------
;                                                    
; Autosmooth by Peter Schuetz                        
;                                                    
; Function to give a mesh hard/soft edges            
; Welds/explodes vertices                            
;                                                    
; Works on single meshes
; Check for children before calling!
;                                                    
; Version 1.0, 18 Martch 2002                        
; Rev 1        30 Martch 2002
; Changed defaults and description 
; Rev 2        5 April 2002
; Removed obsolete params in Showmodel() 
;                                                    
; Public Domain, but try to give credit if used :)   
;                                                    
;------------------------------------------------------------

;Bank offsets for Vertices
Const VertStructSize=80

Const C_X=0
Const C_Y=4
Const C_Z=8

Const C_NX=12
Const c_NY=16
Const C_NZ=20

Const C_R=24
Const C_G=28
Const C_B=32

Const C_U=36
Const C_V=40
Const C_W=44

Const C_AvgNX=48
Const C_AvgNY=52
Const C_AvgNZ=56

Const C_ISUSED=60
Const C_NewIndex=64
Const C_Qx=68
Const C_PREV=72
Const C_NEXT=76


;Bank offsets for Triangles
Const TriStructSize=12

Const C_V1=0
Const C_V2=4
Const C_V3=8




; Parameters
;
; m = The Mesh to smooth
; sin_angle = The angle below witch triangles are to be smooth shaded
; If sin_angle is less than -1, all smooth everything welded 
; If sin_angle is larger than 1, all flat, every triangle has its own set of vertices
; vThresh = weld distancee for vertices (Box compare)
; tThresh = weld distancee for texture coords (Box compare)
; cThresh = weld distancee for color values (Not tested, commented out)


Function AutoSmooth(m,sin_angle#,vThresh#=.00001,tThresh#=.00001,cThresh#=5)

	Local maxVcount=0
	Local maxTcount=0
	;Local vThresh#=0.00001  ; ajust this if you poly collapses or does not weld
	;Local tThresh#=0.000001
	;Local cThresh#=.1	; not testet!
		
	Local sc,k,vc,tc,b,i,v,t,v1,v2,v3,vOff,tOff

	Local surf
	Local B1,B2,B3,B4
	
	Local qGrups
	Local minX#, xRange#,maxX#, xScalFactor#
	Local vprev,vnext
	
	Local Ax#,Ay#,Az#,Bx#,By#,Bz#,Cx#,Cy#,Cz#,vLen#   
	
	Local vx#,vy#,vz#,tU#,tV#,tW#,vR#,vG#,vB#
	Local nx1#,ny1#,nz1#,nx2#,ny2#,nz2#
	
	Local OrgVOff1,OrgVOff2,OrgVOff3
	
	;calculate max size of banks
	sc=CountSurfaces(m)
	For k=1 To sc
		surf=GetSurface( m,k )
		vc=CountVertices(surf)
		tc=CountTriangles(surf)
			If vc>maxVcount  maxVcount=vc
			If tc>maxTcount maxTcount=tc
		Next 
	
	
		qGroups=Int(maxVcount/10)	; maybe try different values for qGroups
		If qGroups<1 Then qGroups=1
	
	
		;allocate banks for the largest vertice and triangle counts
		B1=CreateBank (maxVcount*VertStructSize) 	; bank for original vertices
		B2=CreateBank (maxTcount*TriStructSize) 	; bank for triangles
		B3=CreateBank (maxTcount*VertStructSize*3) 	; new seperated vertices
 		B4=CreateBank (qGroups*4) 					;entry point for group
	
	
	For k=1 To sc
		surf=GetSurface( m,k )
		vc=CountVertices(surf)
		tc=CountTriangles(surf)

		maxX# = -10000000000000000
		minX# = 10000000000000000
		
		For v=0 To vc -1
			b=v*VertStructSize
			
			x#=VertexX(surf,v)
			PokeFloat B1,b,VertexX(surf,v)
			PokeFloat B1,b+C_Y,VertexY(surf,v)
			PokeFloat B1,b+C_Z,VertexZ(surf,v)
			
			If x#> maxX# Then maxX#=x#
			If x#< minX# Then minX#=x#
			
			; making new!
			;PokeFloat B1,b+C_NX,VertexNX(surf,v)
			;PokeFloat B1,b+C_NY,VertexNY(surf,v)
			;PokeFloat B1,b+C_NZ,VertexNZ(surf,v)


			PokeFloat B1,b+C_R,VertexRed(surf,v)
			PokeFloat B1,b+C_G,VertexGreen(surf,v)
			PokeFloat B1,b+C_B,VertexBlue(surf,v)


			PokeFloat B1,b+C_U,VertexU(surf,v)
			PokeFloat B1,b+C_V,VertexV(surf,v)
			PokeFloat B1,b+C_W,VertexW(surf,v)

			

		Next
			
			; *** calc quant qx

			xRange#=maxX#-minX#
			xScalFactor#=xRange#/(qGroups-1)
			
			For v=0 To vc-1
				b=v*VertStructSize
				PokeInt B1,b+C_Qx,Int((PeekFloat( B1,b+C_X)-minX#)/xScalFactor#)
			Next

		
		
			; init groups index
			For i=0 To qGroups -1
				PokeInt B4,i*4,-1
			Next
		
		
		
		; create new vertices for all triange corners		
		For t=0 To tc -1
			b=t*TriStructSize
			
			;offset in new vertice list:
			voff=t*3*VertStructSize
			
			v1=voff
			v2=voff+1*VertStructSize
			v3=voff+2*VertStructSize
	
	
			; now copy original vertice data:
			OrgVOff1=TriangleVertex(surf,t,0)*VertStructSize
			CopyBank B1,OrgVOff1,B3,v1,VertStructSize
	
			OrgVOff2=TriangleVertex(surf,t,1)*VertStructSize
			CopyBank B1,OrgVOff2,B3,v2,VertStructSize		
		
			OrgVOff3=TriangleVertex(surf,t,2)*VertStructSize
			CopyBank B1,OrgVOff3,B3,v3,VertStructSize


			; new vertice offset in bank 3
			PokeInt B2,b+C_V1,v1	; no need to read original 
			PokeInt B2,b+C_V2,v2	; cause new verts are created for all tri-corners
			PokeInt B2,b+C_V3,v3	


			vx1#=PeekFloat (B3,v1+C_X)
			vy1#=PeekFloat (B3,v1+C_Y)
			vz1#=PeekFloat (B3,v1+C_Z)
			
			vx2#=PeekFloat (B3,v2+C_X)
			vy2#=PeekFloat (B3,v2+C_Y)
			vz2#=PeekFloat (B3,v2+C_Z)
						
			vx3#=PeekFloat (B3,v3+C_X)
			vy3#=PeekFloat (B3,v3+C_Y)
			vz3#=PeekFloat (B3,v3+C_Z)
			
			
		;Add a normal
			
			;get two vectors

			Ax#=vx2#-vx1#
			Ay#=vy2#-vy1#
			Az#=vz2#-vz1#
	
			Bx#=vx3#-vx1#
			By#=vy3#-vy1#
			Bz#=vz3#-vz1#
	

			;crossproduct
			Cx# = Ay# * Bz# - By# * Az#
    		Cy# = Az# * Bx# - Bz# * Ax#
    		Cz# = Ax# * By# - Bx# * Ay#


			;normalize:
			vLen#=Sqr#((Cx#*Cx#)+(Cy#*Cy#)+(Cz#*Cz#))
			
			Cx#=Cx#/vLen#
			Cy#=Cy#/vLen#
			Cz#=Cz#/vLen#
			
		; copy face normal to all 3 vertices
			PokeFloat B3,v1+C_NX,Cx#
			PokeFloat B3,v1+C_NY,Cy#
			PokeFloat B3,v1+C_NZ,Cz#
	
			PokeFloat B3,v2+C_NX,Cx#
			PokeFloat B3,v2+C_NY,Cy#
			PokeFloat B3,v2+C_NZ,Cz#	

			PokeFloat B3,v3+C_NX,Cx#
			PokeFloat B3,v3+C_NY,Cy#
			PokeFloat B3,v3+C_NZ,Cz#	
			

		;init the average with face normal as shared normals will accumulate here
;			PokeFloat B3,v1+C_AvgNX,Cx#
;			PokeFloat B3,v1+C_AvgNY,Cy#
;			PokeFloat B3,v1+C_AvgNZ,Cz#
	
;			PokeFloat B3,v2+C_AvgNX,Cx#
;			PokeFloat B3,v2+C_AvgNY,Cy#
;			PokeFloat B3,v2+C_AvgNZ,Cz#	

;			PokeFloat B3,v3+C_AvgNX,Cx#
;			PokeFloat B3,v3+C_AvgNY,Cy#
;			PokeFloat B3,v3+C_AvgNZ,Cz#

			;faster?	
			CopyBank B3,v1+C_NX,B3,v1+C_AvgNX,12
			CopyBank B3,v2+C_NX,B3,v2+C_AvgNX,12
			CopyBank B3,v3+C_NX,B3,v3+C_AvgNX,12	
						
		Next ; t

				
		ClearSurface surf,True,True

		
		;New vertexcount
		vc=3*tc 
		
; Groups vertices into qGroups groups, based on quantizised x coord
; - allows for faster processing
; B3 doubles as qGroups linked lists.		

		
		; mark all new vertices in use, and make group lists:
		For v=0 To vc-1
			b=v*VertStructSize
			; mark all new vertices used
			PokeInt B3,v*VertStructSize+C_ISUSED,True
			
			
			;make grup lists:
			vxgrp=	PeekInt (B3,b+C_Qx)
			If PeekInt(B4,vxgrp*4)=-1 Then 	; is this the first in the group?
				PokeInt B4,vxgrp*4,b 		; store offset to first vertice in group
				PokeInt B3,b+C_PREV,-1 		; mark start of list
				PokeInt B3,b+C_NEXT,-1 		; mark end of list
			
			Else
				vgl=PeekInt (B4,vxgrp*4)  	;read offset to last entry in list
				
				PokeInt B3,vgl+C_NEXT,b		;make v1 the next
				PokeInt B3,b+C_PREV,vgl 	
				PokeInt B3,b+C_NEXT,-1		; mark end of list
				PokeInt B4,vxgrp*4,b		;store offset to *last* vertice in group

			EndIf
		Next 




;process surface		

	If sin_angle#<= 1 Then ; collapse vertices
	
		
		
		
		
		For g=0 To qGroups-1
			vcmp=PeekInt(B4,g*4)	; get list entry (pointing to last entry )

			

			; replace with :
			While vcmp<>-1 						;is group empty?
				vx#=PeekFloat (B3,vcmp+C_X)
				vy#=PeekFloat (B3,vcmp+C_Y)
				vz#=PeekFloat (B3,vcmp+C_Z)
				
				nx1#=PeekFloat (B3,vcmp+C_NX)
				ny1#=PeekFloat (B3,vcmp+C_NY)
				nz1#=PeekFloat (B3,vcmp+C_NZ)	
	
	
				tU#=PeekFloat (B3,vcmp+C_U)
				tV#=PeekFloat (B3,vcmp+C_V)
				tW#=PeekFloat (B3,vcmp+C_W)	
				
				cR#=PeekFloat (B3,vcmp+C_R)
				cG#=PeekFloat (B3,vcmp+C_G)
				cB#=PeekFloat (B3,vcmp+C_B)		

			
			v=PeekInt(B3,vcmp+C_PREV)			; now in the list look up previus
			
			;walk list (list is walked from bottom to top, so vertices will come out in reverse order)
			While v<>-1	; 					
				vprev=PeekInt(B3,v+C_PREV)				
				vnext=PeekInt(B3,v+C_NEXT)
				

				If Abs(vx# - PeekFloat (B3,v+C_X))<=vThresh# Then
				If Abs(vy# - PeekFloat (B3,v+C_Y))<=vThresh#  Then
				If Abs(vz# - PeekFloat (B3,v+C_Z))<=vThresh#  Then
			; comment if you dont want texture coord checking
				If Abs(tU# - PeekFloat (B3,v+C_U))<=tThresh#  Then
				If Abs(tV# - PeekFloat (B3,v+C_V))<=tThresh#  Then
				If Abs(tW# - PeekFloat (B3,v+C_W))<=tThresh#  Then
				
			; uncomment if you want vertex color checking	
			;	If Abs(cR# - PeekFloat (B3,v+C_R))<=cTresh#  Then
			;	If Abs(cG# - PeekFloat (B3,v+C_G))<=cTresh#  Then
			;	If Abs(cB# - PeekFloat (B3,v+C_B))<=cTresh#  Then
									
			 		nx2#=PeekFloat (B3,v+C_NX)
					ny2#=PeekFloat (B3,v+C_NY)
					nz2#=PeekFloat (B3,v+C_NZ)							
					
					DotProduct#=nx1#*nx2#+ny1#*ny2#+nz1#*nz2#
					
					; compare smoothing angle aginst face normal
					If DotProduct#>sin_Angle#  Then 
						;maching vertex found, mark this one not used
						PokeInt B3,v+C_ISUSED,False
						
						
						i=Int(v/VertStructSize)
						tOff=Int(i/3)
						corner=(i Mod 3)*4
							
						; point triangle at the found vertex
						PokeInt B2,tOff*TriStructSize+C_V1+corner,vcmp
					
					
						; add to the averaged normal						
					
						PokeFloat B3,vcmp+C_AvgNX, PeekFloat (B3,vcmp+C_AvgNX)+PeekFloat (B3,v+C_NX)
						PokeFloat B3,vcmp+C_AvgNY, PeekFloat (B3,vcmp+C_AvgNY)+PeekFloat (B3,v+C_NY)
						PokeFloat B3,vcmp+C_AvgNZ, PeekFloat (B3,vcmp+C_AvgNZ)+PeekFloat (B3,v+C_NZ)
						

						;remove	vertex from list
						If vprev<>-1 Then
							PokeInt B3,vprev+C_NEXT,vnext ; copy next offset (could be -1 )
						EndIf

						If vnext<>-1 Then
							PokeInt B3,vnext+C_PREV,vprev ; copy offset  (could be -1 )
						EndIf
						
					
					EndIf
					
		; uncomment if you want vertex color checking
		;		EndIf
		;		EndIf
		;		EndIf
		
		; comment if you dont want texture coord checking					
				EndIf
				EndIf
				EndIf					
				
				EndIf
				EndIf
				EndIf
			
				v=vprev
			Wend


			PokeInt B4,g*4,PeekInt(B3,vcmp+C_PREV)
			vcmp=PeekInt(B4,g*4)	; get list entry (pointing to last entry )
			Wend 	
		
		Next 

EndIf



		; calc the vertex new index
		newIndex=0
		For v=0 To vc-1
			; been using offsets, now reverting to index
			PokeInt B3,v*VertStructSize+C_NewIndex,newIndex
			If PeekInt(B3,v*VertStructSize+C_ISUSED) Then newIndex=newIndex+1
						
		Next

	;Create new surface	
	;**************************
		
		For v=0 To vc -1
			b=v*VertStructSize


			If PeekInt( B3,b+C_ISUSED)=True Then

				newV=AddVertex( surf,PeekFloat (B3,b),PeekFloat (B3,b+C_Y),PeekFloat (B3,b+C_Z), PeekFloat (B3,b+C_U),PeekFloat (B3,b+C_V),PeekFloat(B3,b+C_W))
	
		;		normalize :	
				Cx#=PeekFloat (B3,b+C_AvgNX)
				Cy#=PeekFloat (B3,b+C_AvgNY)
				Cz#=PeekFloat (B3,b+C_AvgNZ)
				vLen#=Sqr#((Cx#*Cx#)+(Cy#*Cy#)+(Cz#*Cz#))

			
				Cx#=Cx#/vLen#
				Cy#=Cy#/vLen#
				Cz#=Cz#/vLen#

				VertexNormal surf,newV,Cx#,Cy#,Cz#		
				VertexColor surf,newV,PeekFloat (B3,b+C_R),PeekFloat (B3,b+C_G),PeekFloat (B3,b+C_B)
			End If


		Next
		


		
		;add the triangles
		For t=0 To tc -1
			b=t*TriStructSize
			; been using offsets, now reverting to index
			; look up new index from original:
			newV1=PeekInt(B3,PeekInt(B2,b+C_V1)+C_NewIndex)
			newV2=PeekInt(B3,PeekInt(B2,b+C_V2)+C_NewIndex)
			newV3=PeekInt(B3,PeekInt(B2,b+C_V3)+C_NewIndex)
			AddTriangle surf,newV1,newV2,newV3		
		Next	
		

	Next
	
	FreeBank B1
	FreeBank B2
	FreeBank B3	
	FreeBank B4
	

End Function





; Test program for Autosmooth
;************************************************
;press 1 make flatter
;press 2 make smoother
;press 3 make explode all tri's 
;press 4 to make all smooth

ShowModel "C:\test.3ds"





Function ShowModel( fil$)
	If Windowed3D()
		Graphics3D 640,480,0,2
	Else
		Graphics3D 1024,768,0,1
	EndIf
	
	SetBuffer BackBuffer()



	model=LoadMesh( fil$ )

	If model FitMesh model,-1,-1,-1,2,2,2,True
	If model=0 RuntimeError "Unable to load 3D mesh:"+fil$
	sc=CountSurfaces(model)
	For k=1 To sc
		vc=vc+CountVertices( GetSurface( model,k ) )
		tc=tc+CountTriangles( GetSurface( model,k ) )
	Next
	
	camera=CreateCamera()
	CameraClsColor camera,0,0,64
	CameraRange camera,.01,10
	xr#=0:yr#=0:z#=2.1
	light=CreateLight()
	TurnEntity light,45,20,0
	

	LightColor light,255,255,255
	ca#=1
	Repeat
		RotateEntity model,xr,yr,0
		PositionEntity model,0,0,z
		UpdateWorld
		RenderWorld
		Text 0,0,"Triangles:"+tc+" Vertices:"+vc+" Surfaces:"+sc +" Cos Angle:"+ca#

		Flip
		key=False
		Repeat
			If KeyHit(1) End
			If KeyDown(200) xr=xr-3:key=True
			If KeyDown(208) xr=xr+3:key=True
			If KeyDown(203) yr=yr+3:key=True
			If KeyDown(205) yr=yr-3:key=True
			If KeyDown( 30) z=z-.1:key=True
			If KeyDown( 44) z=z+.1:key=True

			If KeyDown( 2) ca=ca+0.1 : doSmooth=True :key=True: ;press 1 make flatter
			If KeyDown( 3) ca=ca-0.1 : doSmooth=True :key=True: ;press 1 make smoother
				
			If KeyDown( 4) 	ca=1.1 : doSmooth=True :key=True:	;press 3 make explode all tri's 
			If KeyDown( 5) 	ca=-1.1 : doSmooth=True :key=True:	;press 4 to make all smooth

			
			
			If doSmooth Then
				doSmooth=False
				AutoSmooth model ,ca# :key=True
				vc=0
				tc=0

				For k=1 To sc
					vc=vc+CountVertices( GetSurface( model,k ) )
					tc=tc+CountTriangles( GetSurface( model,k ) )
				Next

			EndIf
			
			
			If Not key WaitKey
		Until key
	Forever
End Function
