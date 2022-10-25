; ID: 432
; Author: starfox
; Date: 2002-09-19 17:52:03
; Title: Portable Lightmapper
; Description: Lightmap Your World!

;Simple Lightmap Package
;Open Source, by David Dawkins(Starfox)
;Some thanks go to Elias for his lightmapper!
;All meshes must be unwelded!
;Built in 9/07/02 - 11/29/02
;Added Lightmap Compression!
;Added Optimization, thanks to Olive!
;Added Automatic lightmap size choser
;Sped up lightmap placement
;Lightmap Sharing is now in!
;This now includes light types(1-Direct,2-Point,3-Spot)

;LightmapMesh(mesh,saveuvfilename,detail,lightmapsize,scale-true|false,AmR,G,B,Luminance,Share)
;CreatelmLight(x,y,z,red,green,blue,range,ltype)
;CreateLUVs(mesh,savefilename,coordset)
;LoadLUVs(mesh,openfilename,coordset)
;Unweld(mesh)
;LightmapTerrain(terrain,Amr,G,B,size,luminance)
Function example()
Graphics3D 640,480,0,2
cube = CreateCube() ;Create a object to lightmap
unweld(cube) ;try it without this line:)
EntityPickMode cube,2 ;Make sure that anything you want in the way is pickable
sha = CreateCube() ;Create a cube shadow
ScaleEntity sha,.25,.25,.25
MoveEntity sha,-3,0,0
EntityPickMode sha,2
CreateLight()
UpdateNormals cube
ScaleEntity cube,10,3,3
FlipMesh(cube) ;Make sure you are able to see inside out
cam = CreateCamera()
UpdateNormals cube
MoveEntity cam,0,0,-15
PointEntity cam,cube
EntityPickMode cube,2

;Create generic lights to do the lightmapping
createlmlight(0,0,0,0,0,255,25) ;Position the lights
createlmlight(-5,0,0,255,0,0,15) ;light 2 is at -5,0,0 and color is 255,0,0 and range is 20
While Not KeyHit(1)
If MouseHit(1)
CameraPick(cam,MouseX(),MouseY())
If PickedEntity()
en = PickedEntity()
food = MilliSecs()
tex = lightmapmesh(en,"",0,128,1,0,0,0,1,share) ;Lightmapper
EntityFX cube,1
time = MilliSecs() - food
EntityTexture en,tex
im = CreateImage(256,256)
CopyRect(0,0,256,256,0,0,TextureBuffer(tex),ImageBuffer(im))
FreeTexture tex
EndIf
EndIf

UpdateWorld
RenderWorld
Color 255,255,255
If im > 0
DrawImage im,0,0
EndIf
Text 0,30,"Time To Lightmap: "+time
Text 0,40,"Hit Space To Toggle Lightmap Sharing: "+share
Text 0,50,"Click On Object"
If KeyHit(57)
share = 1-share
EndIf
Flip
Wend
End
End Function

example() ;The example

Type tris
Field x#[3],y#[3],z#[3]
Field surf,index,mesh
Field ver[3],u#[3],v#[3]
Field tex,size#
Field xpos#,ypos#,hosttex
End Type

Type lmlight
Field x#,y#,z#,range#
Field r#,g#,b#,lmtype
Field inner,outer
Field pitch,yaw,roll
End Type

Type lightmapnode
Field x,y,size
End Type

Function LightmapMesh(mesh,luvsave$="",detail=16,bound=256,scaletex=1,ambr=0,ambg=0,ambb=0,lum#=1,share=0)
Local nx#,ny#,nz#,normlen#
Local imagepivot = CreatePivot()
EntityPickMode imagepivot,1:EntityRadius imagepivot,.625
Local lightpivot = CreatePivot()
For surfcount = 1 To CountSurfaces(mesh) ;Surface count
surf = GetSurface(mesh,surfcount)
For tricount = 0 To CountTriangles(surf)-1 ;Triangle count
;Prestuff
t.tris = New tris
t\surf = surf : t\mesh = mesh
t\index = tricount
	in = TriangleVertex(t\surf,t\index,0)
	t\x[1] = VertexX(surf,in) : t\y[1] = VertexY(surf,in)
	t\z[1] = VertexZ(surf,in) : t\ver[1] = in
	t\u[1] = VertexU(surf,in) : t\v[1] = VertexV(surf,in)
	TFormPoint(t\x[1],t\y[1],t\z[1],t\mesh,0)
	t\x[1] = TFormedX() : t\y[1] = TFormedY() : t\z[1] = TFormedZ()
	in = TriangleVertex(t\surf,t\index,1)
	t\x[2] = VertexX(surf,in) : t\y[2] = VertexY(surf,in)
	t\z[2] = VertexZ(surf,in) : t\ver[2] = in
	t\u[2] = VertexU(surf,in) : t\v[2] = VertexV(surf,in)
	TFormPoint(t\x[2],t\y[2],t\z[2],t\mesh,0)
	t\x[2] = TFormedX() : t\y[2] = TFormedY() : t\z[2] = TFormedZ()
	in = TriangleVertex(t\surf,t\index,2)
	t\x[3] = VertexX(surf,in) : t\y[3] = VertexY(surf,in)
	t\z[3] = VertexZ(surf,in) : t\ver[3] = in
	t\u[3] = VertexU(surf,in) : t\v[3] = VertexV(surf,in)
	TFormPoint(t\x[3],t\y[3],t\z[3],t\mesh,0)
	t\x[3] = TFormedX() : t\y[3] = TFormedY() : t\z[3] = TFormedZ()
VertexTexCoords t\surf,t\ver[1],t\u[1],t\v[1]
VertexTexCoords t\surf,t\ver[2],t\u[2],t\v[2]
VertexTexCoords t\surf,t\ver[3],t\u[3],t\v[3]
;Get the normals
nx = getnorm(t\x[1],t\y[1],t\z[1],t\x[2],t\y[2],t\z[2],t\x[3],t\y[3],t\z[3],1)
ny = getnorm(t\x[1],t\y[1],t\z[1],t\x[2],t\y[2],t\z[2],t\x[3],t\y[3],t\z[3],2)	
nz = getnorm(t\x[1],t\y[1],t\z[1],t\x[2],t\y[2],t\z[2],t\x[3],t\y[3],t\z[3],3)	
normlen# = Sqr#((nx*nx)+(ny*ny)+(nz*nz))
If normlen > 0
nx = nx/normlen : ny = ny/normlen : nz = nz/normlen
Else
nx = 0 : ny = 0 : nz = 1
EndIf
;End Pre
Local plane
Local uvmin#[2],uvmax#[2]
Local uvdelta#[2]
;Figure out the plane its on
uvxnorm# = nx : uvynorm# = ny : uvznorm# = nz
nx = Abs(nx) : ny = Abs(ny) : nz = Abs(nz)
plane = 3
If nx > ny And nx > nz Then plane = 1 ;YZ Right
If ny > nx And ny > nz Then plane = 2 ;XZ Top
If nz > nx And nz > ny Then plane = 3 ;XY Front
If nx = ny And nx = nz Then plane = 1
If nz = ny And nz = nx Then plane = 2

If nz = nx And nz = ny Then plane = 3
If plane = 2
t\u[1] = t\x[1] : t\v[1] = t\z[1] ;1st
t\u[2] = t\x[2] : t\v[2] = t\z[2] ;2nd
t\u[3] = t\x[3] : t\v[3] = t\z[3] ;3rd
ElseIf plane = 1
t\u[1] = t\y[1] : t\v[1] = t\z[1] ;1st
t\u[2] = t\y[2] : t\v[2] = t\z[2] ;2nd
t\u[3] = t\y[3] : t\v[3] = t\z[3] ;3rd
ElseIf plane = 3
t\u[1] = t\x[1] : t\v[1] = t\y[1] ;1st
t\u[2] = t\x[2] : t\v[2] = t\y[2] ;2nd
t\u[3] = t\x[3] : t\v[3] = t\y[3] ;3rd
EndIf
;Get the bounding box for the uv coords
uvmin[1] = t\u[1] : uvmin[2] = t\v[1]
uvmax[1] = t\u[1] : uvmax[2] = t\v[1]
For co = 1 To 3
	If t\u[co] < uvmin[1] Then uvmin[1] = t\u[co];If U is < uv minimun u then uvmin u = U
	If t\v[co] < uvmin[2] Then uvmin[2] = t\v[co];If V is < uv minimun v then uvmin v = V
	If t\u[co] > uvmax[1] Then uvmax[1] = t\u[co];If U is > uv maximum u then uvmax u = U
	If t\v[co] > uvmax[2] Then uvmax[2] = t\v[co];If V is > uv maximum v then uvmax v = V
Next
uvdelta[1] = uvmax[1] - uvmin[1] ;U Delta = U Max - U Min
uvdelta[2] = uvmax[2] - uvmin[2] ;V Delta = V Max - V Min
;Now make it a range from 0.0 to 1.0
For co = 1 To 3
	t\u[co] = t\u[co] - uvmin[1] ;Subtract to make the min at the origin
	t\v[co] = t\v[co] - uvmin[2]
	t\u[co] = t\u[co] / uvdelta[1] ;Then Divide to make it range from 0 to 1
	t\v[co] = t\v[co] / uvdelta[2]
Next
;Convert Lumels(Lightmap Pixels) to global space using vectors
Local uvor,dist#,uvvector#[3]
Local vec1#[3], vec2#[3] ;X,Y,Z
Local edge1#[3], edge2#[3]
Local lumelpos#[3]
Local nedge1#[3],nedge2#[3]
Local lightvector#[3]
For co = 1 To 3 ;Find the origin
	If t\u[co] = 0 And t\v[co] = 0 Then uvor = co
Next
uvor = 1
;Distance on a plane Formula by Elias 
nx = uvxnorm : ny = uvynorm : nz = uvznorm
dist = -(uvxnorm * t\x[uvor] + uvynorm * t\y[uvor] + uvznorm * t\z[uvor])
;Confusing vector work
If plane = 1 ;YZ
	tempx# = - (ny * uvmin[1] + nz * uvmin[2] + dist) / nx
	uvvector[1] = tempx
	uvvector[2] = uvmin[1] : uvvector[3] = uvmin[2]
	tempx# = - (ny * uvmax[1] + nz * uvmin[2] + dist) / nx
	vec1[1] = tempx

	vec1[2] = uvmax[1] : vec1[3] = uvmin[2]
	tempx# = - (ny * uvmin[1] + nz * uvmax[2] + dist) / nx
	vec2[1] = tempx
	vec2[2] = uvmin[1] : vec2[3] = uvmax[2]
ElseIf plane = 2 ;XZ
	tempy# = - (nx * uvmin[1] + nz * uvmin[2] + dist) / ny
	uvvector[2] = tempy
	uvvector[1] = uvmin[1] : uvvector[3] = uvmin[2]
	tempy# = - (nx * uvmax[1] + nz * uvmin[2] + dist) / ny
	vec1[2] = tempy
	vec1[1] = uvmax[1] : vec1[3] = uvmin[2]
	tempy# = - (nx * uvmin[1] + nz * uvmax[2] + dist) / ny
	vec2[2] = tempy
	vec2[1] = uvmin[1] : vec2[3] = uvmax[2]
ElseIf plane = 3 ;XY
	tempz# = - (nx * uvmin[1] + ny * uvmin[2] + dist) / nz
	uvvector[3] = tempz
	uvvector[1] = uvmin[1] : uvvector[2] = uvmin[2]
	tempz# = - (nx * uvmax[1] + ny * uvmin[2] + dist) / nz
	vec1[3] = tempz
	vec1[1] = uvmax[1] : vec1[2] = uvmin[2]
	tempz# = - (nx * uvmin[1] + ny * uvmax[2] + dist) / nz
	vec2[3] = tempz
	vec2[1] = uvmin[1] : vec2[2] = uvmax[2]
EndIf
edge1[1] = vec1[1] - uvvector[1] ;X
edge1[2] = vec1[2] - uvvector[2] ;Y
edge1[3] = vec1[3] - uvvector[3] ;Z
edge2[1] = vec2[1] - uvvector[1] ;X
edge2[2] = vec2[2] - uvvector[2] ;Y
edge2[3] = vec2[3] - uvvector[3] ;Z
;Done with vectors, now onto the image
Local lightmapsize = 0 ;Size of lightmap ^2 or whatever
If detail > 0 Then lightmapsize = detail
If detail = 0
mydist# = Abs(uvdelta[1] + uvdelta[2])
If mydist < 1 Then lightmapsize = 1
If mydist >= 1 And mydist < 4 Then lightmapsize = 2
If mydist >= 4 And mydist < 10 Then lightmapsize = 4
If mydist >= 10 And mydist < 26 Then lightmapsize = 8
If mydist >= 26 And mydist < 50 Then lightmapsize = 16
If mydist >= 50 And mydist < 100 Then lightmapsize = 32
If mydist >= 100 And mydist < 200 Then lightmapsize = 64
If mydist >= 200 And mydist < 400 Then lightmapsize = 128
If mydist >= 400 Then lightmapsize = 256
EndIf
Local uf#,vf#
Local lx#,ly#,lz#,lrange#
Local lightmapim = CreateTexture(lightmapsize,lightmapsize)
SetBuffer TextureBuffer(lightmapim)
ClsColor ambr,ambg,ambb : Cls : ClsColor 0,0,0
LockBuffer()
For l.lmlight = Each lmlight ;Incorperate lights
If l\lmtype = 3
piv = CreateCone(8,0)
RotateMesh piv,-90,0,0
PositionEntity piv,l\x,l\y,l\z
FlipMesh piv:RotateEntity piv,l\pitch,l\yaw,l\roll
ScaleMesh piv,l\outer,l\outer,l\inner
EntityPickMode piv,2
EndIf
PositionEntity lightpivot,l\x,l\y,l\z
lrange = l\range
falloffr# = l\r#/lrange#
falloffg# = l\g#/lrange#
falloffb# = l\b#/lrange#
For y = 0 To lightmapsize-1
	For x = 0 To lightmapsize-1
		uf = Float(x) / Float(lightmapsize)
		vf = Float(y) / Float(lightmapsize)
		nedge1[1] = edge1[1] * uf ;X
		nedge1[2] = edge1[2] * uf ;Y
		nedge1[3] = edge1[3] * uf ;Z
		nedge2[1] = edge2[1] * vf
		nedge2[2] = edge2[2] * vf
		nedge2[3] = edge2[3] * vf
		;Get image pos in 3d space
		lumelpos[1] = uvvector[1] + nedge2[1] + nedge1[1]
		lumelpos[2] = uvvector[2] + nedge2[2] + nedge1[2]
		lumelpos[3] = uvvector[3] + nedge2[3] + nedge1[3]
		lightvector[1] = lx - lumelpos[1]
		lightvector[2] = ly - lumelpos[2]
		lightvector[3] = lz - lumelpos[3]
		PositionEntity imagepivot,lumelpos[1],lumelpos[2],lumelpos[3]
		xi# = EntityX(imagepivot) : yi# = EntityY(imagepivot) : zi# = EntityZ(imagepivot)
		;Check for visibility
		distx# = xi-l\x
		disty# = yi-l\y
		distz# = zi-l\z
		endist# = EntityDistance(imagepivot,lightpivot)
		If endist <= lrange Or l\lmtype = 1
		If LinePick(l\x,l\y,l\z,distx,disty,distz,0)=imagepivot
			;If endist < 1 Then endist = 1
			normx# = xi - l\x : normy# = yi - l\y : normz# = zi - l\z
			normx = normx/endist : normy = normy/endist : normz = normz/endist
			cosangle# = (nx*normx)+(ny*normy)+(nz*normz)
			If cosangle > 0
			intense# = Abs(cosangle)
			If intense > 1 Then intense = 1
			If intense < 0 Then intense = 0
			minusr# = falloffr*endist
			minusg# = falloffg*endist
			minusb# = falloffb*endist
			colr# = l\r - minusr+falloffr
			colg# = l\g - minusg+falloffg
			colb# = l\b - minusb+falloffb
			If l\lmtype = 1 Then colr = l\r:colg=l\g:colb=l\b;Direct
			colr = colr*intense*lum : colg = colg*intense*lum
			colb = colb*intense*lum
			argb = ReadPixelFast(x,y) And $FFFFFF
			colr = colr + (argb Shr 16 And %11111111)
			colg = colg + (argb Shr 8 And %11111111)
			colb = colb + (argb And %11111111)
			If colr < 0 Then colr = 0
			If colr > 255 Then colr = 255
			If colg < 0 Then colg = 0
			If colg > 255 Then colg = 255
			If colb < 0 Then colb = 0
			If colb > 255 Then colb = 255
			rgb = colb Or (colg Shl 8) Or (colr Shl 16)
			;Color colr,colg,colb
			;Rect x,y,1,1
			WritePixelFast x,y,rgb
			EndIf
			EndIf
		EndIf
	Next
Next
If l\lmtype = 3 Then FreeEntity piv
Next
UnlockBuffer()
SetBuffer BackBuffer()
t\tex = lightmapim : t\size = lightmapsize
;Do all the transformations here
;blur(t\tex) or whatever
Next
Next

;Do lightmap sharing
If share = 1
For t.tris = Each tris
	For tr.tris = Each tris
	If tr <> t And tr\hosttex = 0
		alike = texturealike(tr\tex,t\tex)
		If alike = 1
			t\hosttex = tr\tex:Exit
		EndIf
	EndIf
	Next
Next
;Second Wave
For t.tris = Each tris
	If t\hosttex > 0
		For tr.tris = Each tris
		If tr\hosttex = 0
			alike = texturealike(tr\tex,t\tex)
			If alike = 1
				t\hosttex = tr\tex:Exit
			EndIf
		EndIf
		Next
	EndIf
Next
EndIf

;Pack on one big bitmap
Local lightmapbound# = lightmapsize^2;Maximum size for lightmap ^2
Local lightbound =bound
Local xpos#,ypos#
Local xsmall#,ysmall#
Local move#
Local bigtex = CreateTexture(lightbound,lightbound,512)
scale# = lightbound/lightmapbound
move# = lightmapsize/lightmapbound
If detail = 0

;tbcount = 0
;tbcurrent=0

;tbbank = CreateBank(tbcount*3*4)

For t.tris = Each tris
If t\hosttex = 0
scale# = lightbound/(t\size*t\size)
lightmapbound = t\size*t\size
	For ypos = 0 To lightbound-1
		For xpos = 0 To lightbound-1
		overlap = 0
		
		;For cmk = 0 To tbcurrent
		;off = cmk*3*4
		;	nlx = PeekInt(tbbank,off)
		;	nly = PeekInt(tbbank,off+4)
		;	nlsize = PeekInt(tbbank,off+8)
		;	If RectsOverlap(nlx,nly,nlsize,nlsize,xpos,ypos,t\size,t\size) = 1
		;	overlap=1:xpos=xpos+nlsize-1
		;	Exit
		;	EndIf
		;Next
		
		For nl.lightmapnode = Each lightmapnode
			If RectsOverlap(nl\x,nl\y,nl\size,nl\size,xpos,ypos,t\size,t\size) = 1
				overlap=1:xpos=xpos+nl\size-1
			Exit
			EndIf
		Next
		If xpos + t\size > lightbound Then overlap = 1:dot1=1:ypos=ypos+1:xpos=-1
		If ypos + t\size > lightbound Then overlap = 1:dot2=1:ypos=lightbound:xpos=lightbound
		
		If overlap = 0
		;off = tbcurrent*3*4
		;tbcurrent = tbcurrent + 1
		t\xpos = xpos : t\ypos = ypos
			n.lightmapnode = New lightmapnode
			n\x = xpos : n\y = ypos : n\size = t\size
		;	PokeInt(tbbank,off,xpos):PokeInt(tbbank,off+4,ypos):PokeInt(tbbank,off+8,t\size)
			CopyRect(0,0,t\size,t\size,xpos,ypos,TextureBuffer(t\tex),TextureBuffer(bigtex))
			xsmall# = xpos/Float(lightmapbound) : ysmall# = ypos/Float(lightmapbound)

			For co = 1 To 3
			;Get rid of the black lines by scaling up and down
			If scaletex = 1
			If t\u[co] > .5 Then t\u[co] = t\u[co]-.1
			If t\v[co] > .5 Then t\v[co] = t\v[co]-.1
			If t\u[co] < .5 Then t\u[co] = t\u[co]+.1
			If t\v[co] < .5 Then t\v[co] = t\v[co]+.1
			EndIf
			
			t\u[co] = (t\u[co]/t\size) + xsmall
			t\v[co] = (t\v[co]/t\size) + ysmall
			t\u[co] = t\u[co]/scale : t\v[co] = t\v[co] / scale

			VertexTexCoords t\surf,t\ver[co],t\u[co],t\v[co],0,1
			Next
		Exit
		EndIf
		Next
		If overlap = 0 Then Exit
	Next
	If dot1=1 And dot2=1 Then RuntimeError "Too Many Polys or too high detail, Increase Bound"
EndIf
Next
FreeBank tbbank
ElseIf detail > 0
For t.tris = Each tris
If t\hosttex = 0
t\xpos = xpos : t\ypos = ypos
	CopyRect(0,0,t\size,t\size,xpos,ypos,TextureBuffer(t\tex),TextureBuffer(bigtex))
	For co = 1 To 3
	;Get rid of the black lines by scaling up and down
	If scaletex = 1
	If t\u[co] > .5 Then t\u[co] = t\u[co]-.1
	If t\v[co] > .5 Then t\v[co] = t\v[co]-.1
	If t\u[co] < .5 Then t\u[co] = t\u[co]+.1
	If t\v[co] < .5 Then t\v[co] = t\v[co]+.1
	EndIf
	uvm# = t\u[co]
	uvn# = t\v[co]
	
	t\u[co] = (t\u[co]/t\size) + xsmall
	t\v[co] = (t\v[co]/t\size) + ysmall
	t\u[co] = t\u[co]/scale : t\v[co] = t\v[co] / scale
	uvm = t\u[co]
	uvn = t\v[co]
	VertexTexCoords t\surf,t\ver[co],t\u[co],t\v[co],0,1
	Next
	xpos = xpos + t\size
	xsmall = xsmall + move
	If xpos >= lightbound Then xpos = 0 : ypos = ypos + t\size:xsmall = 0:ysmall = ysmall +move
If ypos>lightbound Then RuntimeError "Too Many Polys, or too high detail, Increase Lightmap bound"
EndIf
Next
EndIf

;Finally share the lightmap
For t.tris = Each tris
If t\hosttex > 0
	For tr.tris = Each tris
	If tr\tex = t\hosttex
	scale# = lightbound/(t\size*t\size)
	lightmapbound = t\size*t\size
	xsmall# = tr\xpos/Float(lightmapbound) : ysmall# = tr\ypos/Float(lightmapbound)
		For co = 1 To 3
		;Get rid of the black lines by scaling up and down
		If scaletex = 1
		If t\u[co] > .5 Then t\u[co] = t\u[co]-.1
		If t\v[co] > .5 Then t\v[co] = t\v[co]-.1
		If t\u[co] < .5 Then t\u[co] = t\u[co]+.1
		If t\v[co] < .5 Then t\v[co] = t\v[co]+.1
		EndIf
		
		t\u[co] = (t\u[co]/t\size) + xsmall
		t\v[co] = (t\v[co]/t\size) + ysmall
	uvm = t\u[co]
	uvn = t\v[co]
		t\u[co] = t\u[co]/scale : t\v[co] = t\v[co] / scale
	uvm = t\u[co]
	uvn = t\v[co]

		VertexTexCoords t\surf,t\ver[co],t\u[co],t\v[co],0,1
		Next
	Exit
	EndIf
	Next
EndIf
If t\tex > 0 Then FreeTexture t\tex
Delete t
Next
	

Delete Each lightmapnode
TextureCoords bigtex,1
If luvsave <> ""
createluvs(mesh,luvsave,1)
EndIf
;FreeEntity copy
FreeEntity lightpivot : FreeEntity imagepivot
Return bigtex
End Function 

Function getNorm#(x1#,y1#,z1#,x2#,y2#,z2#,x3#,y3#,z3#,axis)
;Once again, thanks to elias and sswift
  	ux#= x1#- x2#
   	uy#= y1#- y2#
  	uz#= z1#- z2#
    vx#= x3#- x2#
    vy#= y3#- y2#
   	vz#= z3#- z2#
If axis = 1
	Return (uy#*vz#)-(vy#*uz#)
ElseIf axis = 2
	Return (uz#*vx#)-(vz#*ux#)
ElseIf axis = 3
	Return (ux#*vy#)-(vx#*uy#)
EndIf
End Function 

Function CreateLMLight.lmlight(x#,y#,z#,r#,g#,b#,range#,ltype=2)
l.lmlight = New lmlight
l\x = x : l\y = y : l\z = z
l\r =r : l\g = g : l\b =b
l\outer = 1:l\inner =1
l\range = range:l\lmtype = ltype
Return l
End Function

Function CreateLUVs(mesh,filename$,coordset=1)
file = WriteFile(filename+".luv")
For surfcount = 1 To CountSurfaces(mesh)
	surf = GetSurface(mesh,surfcount)
	For vercount = 0 To CountVertices(surf)-1
		WriteFloat(file,VertexU(surf,vercount,coordset))
		WriteFloat(file,VertexV(surf,vercount,coordset))
	Next
Next
CloseFile file
End Function

Function LoadLUVs(mesh,filename$,coordset=1)
file = ReadFile(filename)
For surfcount = 1 To CountSurfaces(mesh)
	surf = GetSurface(mesh,surfcount)
	For vercount = 0 To CountVertices(surf)-1
		u# = ReadFloat(file)
		v# = ReadFloat(file)
		VertexTexCoords surf,vercount,u,v,0,coordset
	Next
Next
CloseFile file
End Function

Function Unweld(mesh)
;Unweld a mesh, retaining all of its textures coords and textures
For surfcount = 1 To CountSurfaces(mesh)
	surf = GetSurface(mesh,surfcount)

	count = CountTriangles(surf)
	bank = CreateBank((15*count)*4)
	For tricount = 0 To count-1
	off = (tricount*15)*4
	in = TriangleVertex(surf,tricount,0)
	x# = VertexX(surf,in):y#=VertexY(surf,in):z#=VertexZ(surf,in)
	u# = VertexU(surf,in):v#=VertexV(surf,in)
	PokeFloat(bank,off,x)
	PokeFloat(bank,off+4,y)
	PokeFloat(bank,off+8,z)
	PokeFloat(bank,off+12,u)
	PokeFloat(bank,off+16,v)

	in = TriangleVertex(surf,tricount,1)
	x# = VertexX(surf,in):y#=VertexY(surf,in):z#=VertexZ(surf,in)
	u# = VertexU(surf,in):v#=VertexV(surf,in)
	PokeFloat(bank,off+20,x)
	PokeFloat(bank,off+24,y)
	PokeFloat(bank,off+28,z)
	PokeFloat(bank,off+32,u)
	PokeFloat(bank,off+36,v)

	in = TriangleVertex(surf,tricount,2)
	x# = VertexX(surf,in):y#=VertexY(surf,in):z#=VertexZ(surf,in)
	u# = VertexU(surf,in):v#=VertexV(surf,in)
	PokeFloat(bank,off+40,x)
	PokeFloat(bank,off+44,y)
	PokeFloat(bank,off+48,z)
	PokeFloat(bank,off+52,u)
	PokeFloat(bank,off+56,v)

	Next
	
	ClearSurface(surf,True,True)
	
	For tricount = 0 To count-1
	off = (tricount*15)*4
	x# = PeekFloat(bank,off)
	y# = PeekFloat(bank,off+4)
	z# = PeekFloat(bank,off+8)
	u# = PeekFloat(bank,off+12)
	v# = PeekFloat(bank,off+16)
	a = AddVertex(surf,x,y,z,u,v)
	x# = PeekFloat(bank,off+20)
	y# = PeekFloat(bank,off+24)
	z# = PeekFloat(bank,off+28)
	u# = PeekFloat(bank,off+32)
	v# = PeekFloat(bank,off+36)
	b = AddVertex(surf,x,y,z,u,v)
	x# = PeekFloat(bank,off+40)
	y# = PeekFloat(bank,off+44)
	z# = PeekFloat(bank,off+48)
	u# = PeekFloat(bank,off+52)
	v# = PeekFloat(bank,off+56)
	c = AddVertex(surf,x,y,z,u,v)
	AddTriangle(surf,a,b,c)
	Next
	FreeBank bank
Next
UpdateNormals mesh
Return mesh
End Function

Function LightmapTerrain(terr,r=0,g=0,b=0,detail=0,lum#=1)
width = TerrainSize(terr)
If detail = 0 Then detail = width
If detail < width Then detail = width
tex = CreateTexture(detail,detail,512)
vx# = GetMatElement(terr,0,0)
vy# = GetMatElement(terr,0,1)
vz# = GetMatElement(terr,0,2)
xscale# = Sqr(vx*vx+vy*vy+vz*vz)
vx# = GetMatElement(terr,1,0)
vy# = GetMatElement(terr,1,1)
vz# = GetMatElement(terr,1,2)
yscale# = Sqr(vx*vx+vy*vy+vz*vz)
vx# = GetMatElement(terr,2,0)
vy# = GetMatElement(terr,2,1)
vz# = GetMatElement(terr,2,2)
zscale# = Sqr(vx*vx+vy*vy+vz*vz)
scale# = 1
If detail < width
scale# = Float(detail)/Float(width)
EndIf
piv = CreatePivot():EntityPickMode piv,1
EntityRadius piv,5
light = CreatePivot()
SetBuffer TextureBuffer(tex)
ClsColor r,g,b : Cls : Color 0,0,0
LockBuffer()
For l.lmlight = Each lmlight
PositionEntity light,l\x,l\y,l\z
lrange# = l\range
falloffr# = l\r#/lrange#
falloffg# = l\g#/lrange#
falloffb# = l\b#/lrange#
For z = 0 To detail-1
	For x = 0 To detail-1
	xpos# = EntityX(terr) : ypos# = EntityY(terr) : zpos# = EntityZ(terr)
	movx# = (xpos+x*xscale)/scale
	movy# = (ypos+(TerrainHeight(terr,x,width-z)*yscale))/scale
	movz# = ((zpos+(width*zscale))-(z*zscale))/scale
		PositionEntity piv,movx,movy,movz
		xi# = EntityX(piv) : zi# = EntityZ(piv) : yi# = EntityY(piv)
		distx# = xi-l\x : disty# = yi-l\y : distz# = zi-l\z
		If LinePick(l\x,l\y,l\z,distx,disty,distz)=piv
		endist# = EntityDistance(piv,light)
		If endist <= lrange
			minusr# = falloffr*endist
			minusg# = falloffg*endist
			minusb# = falloffb*endist
			colr# = l\r - minusr
			colg# = l\g - minusg
			colb# = l\b - minusb
			colr = colr*lum : colg = colg*lum : colb = colb*lum
			argb = ReadPixelFast(x,z) And $FFFFFF
			colr = colr + (argb Shr 16 And %11111111)
			colg = colg + (argb Shr 8 And %11111111)
			colb = colb + (argb And %11111111)
			If colr < 0 Then colr = 0
			If colr > 255 Then colr = 255
			If colg < 0 Then colg = 0
			If colg > 255 Then colg = 255
			If colb < 0 Then colb = 0
			If colb > 255 Then colb = 255
			rgb = colb Or (colg Shl 8) Or (colr Shl 16)
			WritePixelFast(x,z,rgb)
			;Color colr,colg,colb
			;Rect x,z,1,1
		EndIf
		EndIf
	Next
Next
Next
UnlockBuffer()
SetBuffer BackBuffer()
ScaleTexture tex,detail,detail
FreeEntity piv : FreeEntity light
Return tex
End Function

Function TextureAlike(tex1,tex2)
;Check if textures are congruent
width1 = TextureWidth(tex1) : width2 = TextureWidth(tex2)
If width1 <> width2 Then Return 0
height1 = TextureHeight(tex1) : height2 = TextureHeight(tex2)
If height1 <> height2 Then Return 0
LockBuffer(TextureBuffer(tex1))
LockBuffer(TextureBuffer(tex2))
For y = 0 To height1-1
	For x = 0 To width1-1
	rgb1 = ReadPixelFast(x,y,TextureBuffer(tex1))
	rgb2 = ReadPixelFast(x,y,TextureBuffer(tex2))
	If rgb1 <> rgb2
	UnlockBuffer(TextureBuffer(tex1))
	UnlockBuffer(TextureBuffer(tex2))
	Return 0
	EndIf
	Next
Next
UnlockBuffer(TextureBuffer(tex1))
UnlockBuffer(TextureBuffer(tex2))
Return 1
End Function
