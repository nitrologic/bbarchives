; ID: 515
; Author: fredborg
; Date: 2002-11-30 11:51:35
; Title: PickedU(), PickedV(), PickedW()
; Description: Returns the U,V, and W coordinates of the last successful pick command!

;
; PickedU(), PickedV(), PickedW() commands 
;
; Created by Mikkel Fredborg
; 
; Use as you please, but please include a thank you :)
;

;
; PickedTri type
; Necessary for the PickedU(), PickedV(), and PickedW() commands
Type PickedTri
	Field ent,surf,tri				;picked entity, surface and triangle
	Field px#,py#,pz#			    ;picked xyz
	Field pu#[1],  pv#[1]  ,pw#[1]  ;picked uvw x 2
	
	Field vx#[2],  vy#[2]  ,vz#[2]  ;vertex xyz
	Field vnx#[2], vny#[2] ,vnz#[2] ;vertex normals
	Field vu#[5],  vv#[5]  ,vw#[5]  ;vertex uvw x 2
End Type

Global ptri.pickedtri = New pickedtri

;
; Returns the Texture U coordinate of the last successful pick command
; coordset may be set to either 0 or 1
Function PickedU#(coordset = 0)
	
	; if something new has been picked then calculate the new uvw coordinates
	If (PickedX()<>ptri\px) Or (PickedY()<>ptri\py) Or (PickedZ()<>ptri\pz) Or (PickedSurface()<>ptri\surf)
		PickedUVW()
	End If
	
	Return ptri\pu[coordset]
	
End Function

;
; Returns the Texture U coordinate of the last successful pick command
; coordset may be set to either 0 or 1
Function PickedV#(coordset = 0)
	
	; if something new has been picked then calculate the new uvw coordinates
	If (PickedX()<>ptri\px) Or (PickedY()<>ptri\py) Or (PickedZ()<>ptri\pz) Or (PickedSurface()<>ptri\surf)
		PickedUVW()
	End If
	
	Return ptri\pv[coordset]
	
End Function

;
; Returns the Texture U coordinate of the last successful pick command
; coordset may be set to either 0 or 1
Function PickedW#(coordset = 0)
	
	; if something new has been picked then calculate the new uvw coordinates
	If (PickedX()<>ptri\px) Or (PickedY()<>ptri\py) Or (PickedZ()<>ptri\pz) Or (PickedSurface()<>ptri\surf)
		PickedUVW()
	End If
	
	Return ptri\pw[coordset]
	
End Function

;
; Calculates the UVW coordinates of a pick
; Do not call this by yourself, as PickedU(), PickedV(), and PickedW()
; takes care of calling it when nescessary
Function PickedUVW()

	If PickedSurface()
		ptri\ent  = PickedEntity()
		ptri\surf = PickedSurface()
		ptri\tri  = PickedTriangle()
			
		ptri\px = PickedX()
		ptri\py = PickedY()
		ptri\pz = PickedZ()
		
		For i = 0 To 2
			TFormPoint VertexX(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i)),VertexY(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i)),VertexZ(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i)),ptri\ent,0

			ptri\vx[i] = TFormedX()
			ptri\vy[i] = TFormedY()
			ptri\vz[i] = TFormedZ()

			ptri\vnx[i] = VertexNX(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i))
			ptri\vny[i] = VertexNY(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i))
			ptri\vnz[i] = VertexNZ(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i))
					
			ptri\vu[i+0] = VertexU(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i),0)
			ptri\vv[i+0] = VertexV(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i),0)
			ptri\vw[i+0] = VertexW(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i),0)

			ptri\vu[i+3] = VertexU(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i),1)
			ptri\vv[i+3] = VertexV(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i),1)
			ptri\vw[i+3] = VertexW(ptri\surf,TriangleVertex(ptri\surf,ptri\tri,i),1)
		Next

		; Select which component of xyz coordinates to ignore
		Local coords = 3

		If Abs(PickedNX()) > Abs(PickedNY())
			If Abs(PickedNX())>Abs(PickedNZ()) Then coords = 1
		Else
			If Abs(PickedNY())>Abs(PickedNZ()) Then coords = 2
		EndIf
		
		Local a0#,a1#,b0#,b1#,c0#,c1#
		
		; xy components
		If (coords = 3)
			; edge 0
			a0# = ptri\vx[1] - ptri\vx[0]
			a1# = ptri\vy[1] - ptri\vy[0]
		
			; edge 1
			b0# = ptri\vx[2] - ptri\vx[0]
			b1# = ptri\vy[2] - ptri\vy[0]

			; picked offset from triangle vertex 0
			c0# = PickedX() - ptri\vx[0]
			c1# = PickedY() - ptri\vy[0]
		Else		
			; xz components
			If (coords = 2)
				; edge 0
				a0# = ptri\vx[1] - ptri\vx[0]
				a1# = ptri\vz[1] - ptri\vz[0]
		
				; edge 1
				b0# = ptri\vx[2] - ptri\vx[0]
				b1# = ptri\vz[2] - ptri\vz[0]

				; picked offset from triangle vertex 0
				c0# = PickedX() - ptri\vx[0]
				c1# = PickedZ() - ptri\vz[0]
			Else
				; yz components

				; edge 0
				a0# = ptri\vy[1] - ptri\vy[0]
				a1# = ptri\vz[1] - ptri\vz[0]
		
				; edge 1
				b0# = ptri\vy[2] - ptri\vy[0]
				b1# = ptri\vz[2] - ptri\vz[0]

				; picked offset from triangle vertex 0
				c0# = PickedY() - ptri\vy[0]
				c1# = PickedZ() - ptri\vz[0]
			End If
		End If
						
		;
		; u and v are offsets from vertex 0 along edge 0 and edge 1
		; using these it is possible to calculate the Texture UVW coordinates
		; of the picked XYZ location
		;
		; a0*u + b0*v = c0
		; a1*u + b1*v = c1
		;
		; solve equation (standard equation with 2 unknown quantities)
		; check a math book to see why the following is true
		;
		Local u# = (c0*b1 - b0*c1) / (a0*b1 - b0*a1)
		Local v# = (a0*c1 - c0*a1) / (a0*b1 - b0*a1)
		
		; If either u or v is out of range then the
		; picked entity was not a mesh, and therefore
		; the uvw coordinates cannot be calculated
		If (u<0.0 Or u>1.0) Or (v<0.0 Or v>1.0)
			Return 
		End If
		
		; Calculate picked uvw's for coordset 0 (and modulate them to be in the range of 0-1 nescessary)
		ptri\pu[0] = (ptri\vu[0] + ((ptri\vu[1] - ptri\vu[0]) * u) + ((ptri\vu[2] - ptri\vu[0]) * v)) Mod 1
		ptri\pv[0] = (ptri\vv[0] + ((ptri\vv[1] - ptri\vv[0]) * u) + ((ptri\vv[2] - ptri\vv[0]) * v)) Mod 1
		ptri\pw[0] = (ptri\vw[0] + ((ptri\vw[1] - ptri\vw[0]) * u) + ((ptri\vw[2] - ptri\vw[0]) * v)) Mod 1
		
		; If any of the coords are negative
		If ptri\pu[0]<0.0 Then ptri\pu[0] = 1.0 + ptri\pu[0]
		If ptri\pv[0]<0.0 Then ptri\pv[0] = 1.0 + ptri\pv[0]
		If ptri\pw[0]<0.0 Then ptri\pw[0] = 1.0 + ptri\pw[0]
		
		; Calculate picked uvw's for coordset 1 (and modulate them to be in the range of 0-1 nescessary)
		ptri\pu[1] = (ptri\vu[3] + ((ptri\vu[4] - ptri\vu[3]) * u) + ((ptri\vu[5] - ptri\vu[3]) * v)) Mod 1
		ptri\pv[1] = (ptri\vv[3] + ((ptri\vv[4] - ptri\vv[3]) * u) + ((ptri\vv[5] - ptri\vv[3]) * v)) Mod 1
		ptri\pw[1] = (ptri\vw[3] + ((ptri\vw[4] - ptri\vw[3]) * u) + ((ptri\vw[5] - ptri\vw[3]) * v)) Mod 1

		; If any of the coords are negative
		If ptri\pu[1]<0.0 Then ptri\pu[1] = 1.0 + ptri\pu[1]
		If ptri\pv[1]<0.0 Then ptri\pv[1] = 1.0 + ptri\pv[1]
		If ptri\pw[1]<0.0 Then ptri\pw[1] = 1.0 + ptri\pw[1]
	End If

End Function




; 
; Test example for
; PickedU(),PickedV(),PickedW() commands
; 
; Created by Mikkel Fredborg
; Inspired by David Bird
;
Graphics3D 640,480
SetBuffer BackBuffer()

lit=CreateLight()
LightColor lit,60,60,60
PositionEntity lit,0,10,0
RotateEntity lit,90,0,0

cam=CreateCamera()
CameraRange cam,.1,1000
PositionEntity cam,0,0,-3
CameraClsColor cam,100,100,100

cubetex=CreateTexture(64,64)
; make some squares on the texture
SetBuffer TextureBuffer(cubetex)
Color 128,128,128
Rect 0,0,TextureWidth(cubetex)/2.0,TextureHeight(cubetex)/2.0,True
Rect TextureWidth(cubetex)/2.0,TextureHeight(cubetex)/2.0,(TextureWidth(cubetex)/2.0)-1,(TextureHeight(cubetex)/2.0)-1,True
SetBuffer BackBuffer()

cube = CreateCube()
PositionEntity cube,1,0,0
EntityTexture cube,cubetex,0,0
EntityPickMode cube,2

; Adjust uv coordinates of the cube to tile the texture
surf_n = CountSurfaces(cube)
For i = 1 To surf_n
	surf = GetSurface(cube,i)
	n_verts = CountVertices(surf)-1
	For j = 0 To n_verts
		VertexTexCoords surf,j,VertexU(surf,j)*5.0,VertexV(surf,j)*5.0,VertexW(surf,j)*5.0
	Next
Next

spheretex=CreateTexture(256,256)
sphere = CreateSphere()
PositionEntity sphere,-1,0,0
EntityTexture sphere,spheretex,0,0
EntityPickMode sphere,2

plane = CreatePlane()
PositionEntity plane,0,-2,0
EntityPickMode plane,2

Global dotred# = 0.0
Global dotgrn# = 0.0
Global dotblu# = 0.0

ang# = 0
While Not KeyDown(1)

	xm = MouseX()
	ym = MouseY()

	If MouseDown(1) Then
		Select CameraPick(cam,xm,ym)
			Case cube:    PaintRedDot(cubetex  ,PickedU()*(TextureWidth(cubetex)-1)  ,PickedV()*(TextureHeight(cubetex)-1))
			Case sphere:  PaintRedDot(spheretex,PickedU()*(TextureWidth(spheretex)-1),PickedV()*(TextureHeight(spheretex)-1))
		End Select
	Else
		If MouseDown(2) Then
			Select CameraPick(cam,xm,ym)
				Case cube:    ReadDot(cubetex  ,PickedU()*(TextureWidth(cubetex)-1)  ,PickedV()*(TextureHeight(cubetex)-1))
				Case sphere:  ReadDot(spheretex,PickedU()*(TextureWidth(spheretex)-1),PickedV()*(TextureHeight(spheretex)-1))
			End Select
		End If
	End If
	
	ang = (ang + 0.1) Mod 360
	TurnEntity cube,-Sin(ang)*0.3,-Cos(ang)*0.3,0
	TurnEntity sphere,Sin(ang)*0.1,Cos(ang)*0.1,0
	
	UpdateWorld 
	RenderWorld

	; draw mouse cursor
	Line xm-8,ym,xm-4,ym
	Line xm+8,ym,xm+4,ym
	Line xm,ym-8,xm,ym-4
	Line xm,ym+8,xm,ym+4
	Oval xm-4,ym-4,9,9,False	

	; write some info
	Text 0,  0,"Use left mousebutton to paint the cube and the sphere"
	Text 0, 20,"ent = "+PickedEntity()
	Text 0, 35,"surf = "+PickedSurface()
	Text 0, 50,"tri = "+PickedTriangle()
			
	Text 0, 80,"x = "+PickedX()
	Text 0, 95,"y = "+PickedY()
	Text 0,110,"z = "+PickedZ()
	
	Text 0,140,"u = "+PickedU()
	Text 0,155,"v = "+PickedV()
	Text 0,170,"w = "+PickedW()

	Text 0,200,"nx= "+PickedNX()
	Text 0,215,"ny= "+PickedNY()
	Text 0,230,"nz= "+PickedNZ()
	
	Text 14,460,"Use right mousebutton to pick up a texture color"
	Color dotred,dotgrn,dotblu
	Rect 0,460,12,12,True
	Color 255,255,255
	Flip 
Wend

ClearWorld
End

;
; Plots a red dot :)
Function PaintRedDot(tex,x,y)
	SetBuffer TextureBuffer(tex)
	
	maxwidth  = TextureWidth(tex)
	maxheight = TextureHeight(tex)
	
	red# = 255
	grn# = 0
	blu# = 0
	WritePixel x,y,blu Or (grn Shl 8) Or (red Shl 16)
	
	For xx = -1 To 1
		For yy = -1 To 1
			If (xx=0) Xor (yy=0)
				If (xx<0 And x>0) Or (xx>0 And x<maxwidth) Or (yy<0 And y>0) Or (yy>0 And y<maxheight)
					argb = ReadPixel(x+xx,y+yy)
					red# =  64 + (argb Shr 16 And %11111111)
					grn# =   0 + (argb Shr 8 And %11111111)
					blu# =   0 + (argb Shr 8 And %11111111)

					If red>255 Then red = 255
					If grn>255 Then grn = 255
					If blu>255 Then blu = 255
		
					WritePixel x+xx,y+yy,blu Or (grn Shl 8) Or (red Shl 16)
				End If
			End If
		Next
	Next
	
	SetBuffer BackBuffer()
End Function

;
; Reads a pixel in a texture
Function ReadDot(tex,x,y)
	SetBuffer TextureBuffer(tex)
	
	maxwidth  = TextureWidth(tex)
	maxheight = TextureHeight(tex)
	
	If x<maxwidth And y<maxheight
		argb = ReadPixel(x,y)
		dotred# = (argb Shr 16 And %11111111)
		dotgrn# = (argb Shr 8 And %11111111)
		dotblu# = (argb Shr 8 And %11111111)
	End If
	
	SetBuffer BackBuffer()
End Function
