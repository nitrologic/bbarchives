; ID: 861
; Author: Pepsi
; Date: 2003-12-21 23:32:43
; Title: CreateMySphere
; Description: a CreateSphere blitz-like function. Does the Same thing. Update[12-22-03] Fix: North & South Pole UV coords

; CreateMySphere Example
; ----------------------
; By: Todd Riggins 12-21-2003
;
; a CreateSphere blitz-like function. Does the Same thing. Just wanted to do
; this to see how to actaully create a sphere with the addvertex/addtriangle
; commands. Just thought I would share.
;
; 12-22-2003 Fix: North & South Pole UV coords


; Left sphere is created by the CreateMySphere function.
; Right sphere is created by blitz's CreateSphere command.
;
; Controls:
; - Use mouse to rotate the spheres
; - wireframe toggle 
; - Esc key to escape


Graphics3D 640,480
SetBuffer BackBuffer()

camera=CreateCamera()

light=CreateLight()
RotateEntity light,90,0,0

;earth=LoadTexture("earth.bmp",9)

; enter how many segments the sphere has
segs=24

; Create Blitz Sphere
sphere=CreateSphere(segs)
PositionEntity sphere,1,0,4
;EntityTexture sphere,earth

; Create Sphere manually
mysphere=CreateMySphere(segs)
PositionEntity mysphere,-1,0,4
;EntityTexture mysphere,earth

; key helper
wkey=0

MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

While Not KeyDown( 1 )

	mspx#=MouseXSpeed()
	mspy#=MouseYSpeed()

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


	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
	TurnEntity sphere,0,0,mspx#
	TurnEntity sphere,mspy#,0,0

	TurnEntity mysphere,0,0,mspx#
	TurnEntity mysphere,mspy#,0,0

	RenderWorld
	Flip
Wend

End

; ---------------------------------------------------------
Function CreateMySphere(segments,parent=0)

	If segments<2 Or segments>100 Then Return 0
	
	thissphere=CreateMesh(parent)
	thissurf=CreateSurface(thissphere)

	div#=Float(360.0/(segments*2))
	height#=1.0
	upos#=1.0
	udiv#=Float(1.0/(segments*2))
	vdiv#=Float(1.0/segments)
	RotAngle#=90	

	If segments=2 ; diamond shape - no center strips
	
		For i=1 To (segments*2)
			np=AddVertex(thissurf,0.0,height,0.0,upos#-(udiv#/2.0),0);northpole
			sp=AddVertex(thissurf,0.0,-height,0.0,upos#-(udiv#/2.0),1);southpole
			XPos#=-Cos(RotAngle#)
			ZPos#=Sin(RotAngle#)
			v0=AddVertex(thissurf,XPos#,0,ZPos#,upos#,0.5)
			RotAngle#=RotAngle#+div#
			If RotAngle#>=360.0 Then RotAngle#=RotAngle#-360.0
			XPos#=-Cos(RotAngle#)
			ZPos#=Sin(RotAngle#)
			upos#=upos#-udiv#
			v1=AddVertex(thissurf,XPos#,0,ZPos#,upos#,0.5)
			AddTriangle(thissurf,np,v0,v1)
			AddTriangle(thissurf,v1,v0,sp)	
		Next
		
	Else ; have center strips now
	
		; poles first
		For i=1 To (segments*2)
		
			np=AddVertex(thissurf,0.0,height,0.0,upos#-(udiv#/2.0),0);northpole
			sp=AddVertex(thissurf,0.0,-height,0.0,upos#-(udiv#/2.0),1);southpole
			
			YPos#=Cos(div#)
			
			XPos#=-Cos(RotAngle#)*(Sin(div#))
			ZPos#=Sin(RotAngle#)*(Sin(div#))
			
			v0t=AddVertex(thissurf,XPos#,YPos#,ZPos#,upos#,vdiv#)
			v0b=AddVertex(thissurf,XPos#,-YPos#,ZPos#,upos#,1-vdiv#)
			
			RotAngle#=RotAngle#+div#
			
			XPos#=-Cos(RotAngle#)*(Sin(div#))
			ZPos#=Sin(RotAngle#)*(Sin(div#))
			
			upos#=upos#-udiv#

			v1t=AddVertex(thissurf,XPos#,YPos#,ZPos#,upos#,vdiv#)
			v1b=AddVertex(thissurf,XPos#,-YPos#,ZPos#,upos#,1-vdiv#)
			
			AddTriangle(thissurf,np,v0t,v1t)
			AddTriangle(thissurf,v1b,v0b,sp)	
			
		Next
		
		; then center strips

		upos#=1.0
		RotAngle#=90
		For i=1 To (segments*2)
		
			mult#=1
			YPos#=Cos(div#*(mult#))
			YPos2#=Cos(div#*(mult#+1.0))
			Thisvdiv#=vdiv#
			For j=1 To (segments-2)

				
				XPos#=-Cos(RotAngle#)*(Sin(div#*(mult#)))
				ZPos#=Sin(RotAngle#)*(Sin(div#*(mult#)))

				XPos2#=-Cos(RotAngle#)*(Sin(div#*(mult#+1.0)))
				ZPos2#=Sin(RotAngle#)*(Sin(div#*(mult#+1.0)))
							
				v0t=AddVertex(thissurf,XPos#,YPos#,ZPos#,upos#,Thisvdiv#)
				v0b=AddVertex(thissurf,XPos2#,YPos2#,ZPos2#,upos#,Thisvdiv#+vdiv#)
			
				tempRotAngle#=RotAngle#+div#
			
				XPos#=-Cos(tempRotAngle#)*(Sin(div#*(mult#)))
				ZPos#=Sin(tempRotAngle#)*(Sin(div#*(mult#)))
				
				XPos2#=-Cos(tempRotAngle#)*(Sin(div#*(mult#+1.0)))
				ZPos2#=Sin(tempRotAngle#)*(Sin(div#*(mult#+1.0)))				
			
				temp_upos#=upos#-udiv#

				v1t=AddVertex(thissurf,XPos#,YPos#,ZPos#,temp_upos#,Thisvdiv#)
				v1b=AddVertex(thissurf,XPos2#,YPos2#,ZPos2#,temp_upos#,Thisvdiv#+vdiv#)
				
				AddTriangle(thissurf,v1t,v0t,v0b)
				AddTriangle(thissurf,v1b,v1t,v0b)
				
				Thisvdiv#=Thisvdiv#+vdiv#			
				mult#=mult#+1
				YPos#=Cos(div#*(mult#))
				YPos2#=Cos(div#*(mult#+1.0))
			
			Next
			upos#=upos#-udiv#
			RotAngle#=RotAngle#+div#
		Next

	EndIf

	UpdateNormals thissphere 
	Return thissphere 
End Function
