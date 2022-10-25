; ID: 2097
; Author: Jan_
; Date: 2007-08-29 13:27:03
; Title: B3D, Colormap, Dot3map, Cubemap und Alphamap
; Description: B3D, Colormap, Dot3map, Cubemap und Alphamap

Include "blur.bb"
Graphics3D 800,600,32,2
SetBuffer BackBuffer()

cam= CreateCamera()
MoveEntity cam,80,120,0
CameraClsColor cam,0,0,0

cube_cam=CreateCamera()
;CameraClsColor cube_cam,255,255,255;0,0,0
CameraClsColor cube_cam,0,0,0;255,255,255

CreateBlur(Cam,8,3,255,255,255,0,True)

Type Dot3Light
   Field ent
   Field mul#
   Field typ
End Type
AmbientLight 0,0,0

enemy = LoadAnimMesh("beast.b3d")

texture = LoadTexture("beast3.jpg")

tex=CreateTexture(256,256,128+256)
TextureBlend tex ,2


spec = LoadTexture("spec_beast.jpg",4)

bump=LoadTexture( "n_beast.png",0)
TextureBlend bump,4

Global piv=CreatePivot()
light = Dot3_CreateLight(2,piv,1.0,1)
lighticon = CreateSphere(8,light)
EntityFX lighticon,1
;ScaleEntity lighticon,0.01,0.01,0.01
Dot3_LightRange light,100
PositionEntity piv,0,80,0

level = LoadMesh("blub.x")
ScaleEntity level,80,80,80

ClsColor 0,0,0
Local blender#=0.2

timer = CreateTimer(60)

Local maps[4]
For j = 0 To 3
	maps[j] = 1
Next
Repeat

	
	Cls
	m=m+1
	
	If i >= 250 Then i=0
	i=i+1
	SetAnimTime(enemy,i/10.0)
	MoveEntity enemy,0,0,-0.25
	If EntityZ(enemy) > 250 Then TurnEntity enemy,0,180,0
	If EntityZ(enemy) < -200 Then TurnEntity enemy,0,180,0
;	TurnEntity enemy,0,0.1,0

	HideEntity cam
	UpdateCubemap(tex,cube_cam,enemy,i)
	ShowEntity cam

	PointEntity cam,enemy

	If maps[0] Then EntityTexture enemy,tex,0,2
	If maps[1] Then EntityTexture enemy,spec,0,1

	Cls

	HideEntity level
	RenderWorld
	ShowEntity level

	UpdateBlur(blender#)
	UpdateBumpNormals(enemy,light,2)

	fpr=fpr+1
	t1=MilliSecs()
	If t2+1000 < t1 Then
		t2=t1
		fps=fpr
		fpr=0
	EndIf
	If maps[2] Then EntityTexture enemy,texture,0,2
	If maps[3] Then EntityTexture enemy,bump,0,1

	If KeyHit(17) Then
		wires=1-wires	
		WireFrame wires
	EndIf
	If KeyHit(46) Then
		campos = campos +1
		campos = campos Mod 3
		Select campos
			Case 0
				PositionEntity cam,80,120,0
			Case 1
				PositionEntity cam,0,10,0
			Case 2
				PositionEntity cam,90,50,0
		End Select
	EndIf

	If KeyHit(16) Then blender = blender +0.01
	If KeyHit(30) Then blender = blender -0.01
	If KeyHit(20) Then break = 1-break
	
	For j = 2 To 5
		If KeyHit(j) Then maps[j-2]=1-maps[j-2]
	Next
	
	RenderWorld
	UpdateWorld
	If KeyDown(59) Then ;F1
		Text 0,20, "w = Wireframes"	
		Text 0,40, "c = cameraposition"
		Text 0,60, "1 = toogle colormap"
		Text 0,80, "2 = toogle Normalmap"
		Text 0,100, "3 = toogle cubemap"
		Text 0,120, "4 = toogle cubemapmask"
		Text 0,140, "q = blend +"
		Text 0,160, "a = blend -"
		Text 0,180, "t = 60 FPS breake"
	Else
		Text 0,20, "F1 = Help"	
		Text 0,40, "blending: "+blender
		Text 0,80, "Made by Jan Kuhnert"
		Text 0,100, "www.blitzforum.de"
		Text 0,580, "Beast model by: Psionic, http://www.psionic3d.co.uk"
	EndIf

	Text 0,0,fps;1000.0/(t1-t2)

	If break WaitTimer(timer)
	Flip 0
Until KeyHit(1)
End


Function UpdateCubemap(tex,camera,entity,lma)
tex_sz=TextureWidth(tex)
ShowEntity camera
HideEntity entity
PositionEntity camera,EntityX#(entity),EntityY#(entity),EntityZ#(entity)
CameraClsMode camera,True,True
CameraViewport camera,0,0,tex_sz,tex_sz

lma = lma Mod 6
Select lma
	Case 1
		SetCubeFace tex,0
		RotateEntity camera,0,90,0
		RenderWorld
		CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)
	Case 2
		SetCubeFace tex,1
		RotateEntity camera,0,0,0
		RenderWorld
		CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)
	Case 3
		SetCubeFace tex,2
		RotateEntity camera,0,-90,0
		RenderWorld
		CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)
	Case 4
		SetCubeFace tex,3
		RotateEntity camera,0,180,0
		RenderWorld
		CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)
	Case 5
		SetCubeFace tex,4
		RotateEntity camera,-90,0,0
		RenderWorld
		CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)
	Case 0
		SetCubeFace tex,5
		RotateEntity camera,90,0,0
		RenderWorld
		CopyRect 0,0,tex_sz,tex_sz,0,0,BackBuffer(),TextureBuffer(tex)
	End Select

ShowEntity entity
HideEntity camera
End Function



Function UpdateBumpNormals(mesh,light,lighttype=0)
   EP=GetParent(mesh)
   n_surf = CountSurfaces(mesh)
   For s = 1 To n_surf
      surf = GetSurface(mesh,s)
      n_vert = CountVertices(surf)-1
      For v = 0 To n_vert
         red2# = 0
         grn2# = 0
         blu2# = 0
         For d3l.Dot3Light = Each Dot3Light
            lx#=EntityX(d3l\ent,True)
            ly#=EntityY(d3l\ent,True)
            lz#=EntityZ(d3l\ent,True)
   
            If d3l\typ = 1 ; Directional light
               TFormVector 0,0,1,d3l\ent,0
               nx# = TFormedX()
               ny# = TFormedY()
               nz# = TFormedZ()
               TFormNormal VertexNX(surf,v),VertexNY(surf,v),VertexNZ(surf,v),mesh,0
               red# = TFormedX()
               grn# = TFormedY()
               blu# = TFormedZ()
               
               
            ElseIf d3l\typ = 2 ; Point light      
               ; Vertex Normal in World coordinates
               TFormNormal VertexNX(surf,v),VertexNY(surf,v),VertexNZ(surf,v),mesh,0
               Vnx# = TFormedX()
               Vny# = TFormedY()
               Vnz# = TFormedZ()
               
               ; Vertex > Light Vector in World coordinates
               TFormPoint VertexX(surf,v),VertexY(surf,v),VertexZ(surf,v),mesh,0
               Lvx# = lx - TFormedX()
               Lvy# = ly - TFormedY()
               Lvz# = lz - TFormedZ()

               
               ; Normalize Vertex > Light Vector
               d# = Sqr(Lvx*Lvx + Lvy*Lvy + Lvz*Lvz)
               Lvx   =   Lvx / d
               Lvy = Lvy / d
               Lvz = Lvz / d
            End If

            ; Theta Angle between Vertex Normal & Vertex>Light Normal
            dot# = (Lvx*Vnx + Lvy*Vny + Lvz*Vnz)

            ; Clamp Colors to 0
            If dot<0.0 Then dot# = 0
            
            ; If the Mesh had a Parent, Convert Light Vector into that Parents Local coordinates
            ; unsure if this'll work with multiple hierarchy
            If EP
               TFormNormal Lvx,Lvy,Lvz,0,EP
               Lvx# = TFormedX()
               Lvy# = TFormedY()
               Lvz# = TFormedZ()
            End If
            
            red# = ( (1.0+(  Lvx * dot)) * 127) * d3l\mul
            grn# = ( (1.0+(  Lvy * dot)) * 127) * d3l\mul
            blu# = ( (1.0+( -Lvz * dot)) * 127) * d3l\mul
            
            red2# = red2+red
            grn2# = grn2+grn
            blu2# = blu2+blu
         Next
         VertexColor surf,v,red2,grn2,blu2
      Next
   Next
End Function





Function Dot3_CreateLight(typ=1,parent=0,mul#=1.0,real=True)
   
   d3l.Dot3Light = New Dot3Light
   
   If real
      d3l\ent = CreateLight(typ,parent)
      LightRange d3l\ent,50
   Else
      d3l\ent = CreatePivot(parent)
   End If
   PositionEntity d3l\ent,.4,.5,-2


   d3l\typ = typ
   d3l\mul = mul
   
   Return d3l\ent

End Function

Function Dot3_LightRange(ent,range#)
   For d3l.dot3light = Each dot3light
      If d3l\ent = ent
         If Lower$(EntityClass(d3l\ent))="light"
            LightRange d3l\ent,range
            Return
         End If
      End If
   Next
   
End Function

Function Dot3_LightIntensity(ent,intens#)
   For d3l.dot3light = Each dot3light
      If d3l\ent = ent
         d3l\mul = intens
         If Lower$(EntityClass(d3l\ent))="light"
            LightColor d3l\ent,d3l\mul*255.0,d3l\mul*255.0,d3l\mul*255.0
         End If         
         Return
      End If
   Next
End Function
