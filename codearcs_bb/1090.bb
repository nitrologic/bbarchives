; ID: 1090
; Author: jfk EO-11110
; Date: 2004-06-18 14:22:13
; Title: Sunbeams in the air
; Description: Visible sunbeams in the air as seen in Hitman 2

; CSP Demo: Hitman 2 like "Sunbeams-in-dusty-air" Effect. This Function takes a Texture and creates
; sunbeams based on the images colors, using a lowpoly method.
Graphics3D 800,600,32,2
SetBuffer BackBuffer()


camera=CreateCamera()
TranslateEntity camera,0,0,-12

;create a "building"
wall=CreateCube()
walltex=LoadTexture("stone19.jpg")
EntityTexture wall,walltex
EntityColor wall,110,110,110 ; a bit darker please
ScaleEntity wall,7,6,7
TranslateEntity wall,6.9,0,0
FlipMesh wall 



window=CreateSunbeams("churchwin3.jpg",40,5,5,1.0,-1.0,10.0,1,1)
ScaleEntity window,1,4,2


;display the result...
EntityParent wall,window
While Not KeyDown(1)
 ang#=(ang#+1) Mod 180
 RotateEntity window,0,ang-180,0
 RenderWorld()
 Text 0,0,"Tris Rendered: "+TrisRendered()
 Flip
Wend
End




; CreateSunbeams() usage:
;texpath$ ; used texture-file
;transp=40 ; beams transparency (0 to 255)
;numsurfx#= number of vertical beam-quads
;numsurfy#= number of horizontal beam-quads
;zoom#=1.0 ; used to create a cone-shaped projection. this can be used 
;            only when no vertical beam-quads are used
;height# ; this will define the angle the "sun" shines down, eg. -1.0
;lenght#=5 ; this will define the lenght of the beams
;vertical_on=1 ; this will turn on/off vertical quads
;horizontal_on=1 ; this will turn on/off horizontal quads
Function CreateSunbeams(texpath$,transp=40,numsurfx#=5,numsurfy#=5,zoom#=1.0,height#=-1.0,lenght#=3.0,vertical_on=1,horizontal_on=1)

 tex=LoadTexture(texpath$) ; used for "glass"
 tex2=LoadTexture(texpath$,2) ; used for beams

 ; amplyfy beam-significant texels
 SetBuffer TextureBuffer(tex2)
 LockBuffer()
 For j=0 To TextureHeight(tex2)-1
  For i=0 To TextureWidth(tex2)-1
   rgb=ReadPixelFast(i,j) And $FFFFFF
   r=(rgb And $FF0000) Shr 16
   g=(rgb And $FF00) Shr 8
   b=(rgb And $FF)
   minv=128
   If r>minv Or g>minv Or b>minv Then
    a = transp Shl 24
   Else
    a=0 
   EndIf
   r=r*3
   g=g*3
   b=b*3
   If r>255 Then r=255  
   If g>255 Then g=255  
   If b>255 Then b=255  
   argb=a Or (r Shl 16) Or (g Shl 8) Or b
   WritePixelFast i,j,argb
  Next
 Next
 UnlockBuffer()
 SetBuffer BackBuffer()

 ; window glass mesh
 win=CreateQuad()
 EntityTexture win,tex
 EntityFX win,1

 ; beams quads mesh
 mesh=CreateMesh()
 surf=CreateSurface(mesh)

 ;vertical beam quads
 If vertical_on
  For i=0 To numsurfx#
   where#=(((Float(i)-numsurfx)/numsurfx)*2.0)+1.0
   wherev#=(where#+1.0)/2.0
   v0=AddVertex(surf,-1.0,-1.0,where#,wherev,1.0 )
   v1=AddVertex(surf, 1.0*Lenght,-1.0+height,where#,wherev,1.0)
   v2=AddVertex(surf, 1.0*Lenght,1.0+height,where#,wherev,0.0)
   v3=AddVertex(surf,-1,1.0,where#,wherev,0.0 )
   VertexColor surf,v0, 255,255,255,1
   VertexColor surf,v1, 0,0,0,0
   VertexColor surf,v2, 0,0,0,0
   VertexColor surf,v3, 255,255,255,1
   AddTriangle(surf,v0,v1,v2)
   AddTriangle(surf,v2,v3,v0)
  Next
 EndIf

 ;horizontal beam quads
 If horizontal_on
  For i=0 To numsurfy#
   where#=(((Float(i)-numsurfy)/numsurfy)*2.0)+1.0
   wherev#=(where#+1.0)/2.0
   v0=AddVertex(surf,-1.0,where#,-1,1,-wherev )
   v1=AddVertex(surf, 1.0*lenght,where#*zoom+height,-1,1,-wherev )
   v2=AddVertex(surf, 1.0*lenght,where#*zoom+height,1,0,-wherev )
   v3=AddVertex(surf,-1.0,where#, 1,0,-wherev )
   VertexColor surf,v0, 255,255,255,1
   VertexColor surf,v1, 0,0,0,0
   VertexColor surf,v2, 0,0,0,0
   VertexColor surf,v3, 255,255,255,1
   AddTriangle(surf,v0,v1,v2)
   AddTriangle(surf,v2,v3,v0)
  Next
 EndIf

 UpdateNormals mesh
 EntityFX mesh,16 Or 1 Or 2
 EntityTexture mesh,tex2
 ;EntityBlend mesh,3
 TranslateEntity mesh,1,0,0
 EntityParent mesh,win
 Return win
End Function



Function CreateQuad()
  ; creates a quad, facing to the right side
  mesh=CreateMesh()
  surf=CreateSurface(mesh)
  v0=AddVertex(surf,0, -1.0,  -1.0, 0,1 )
  v1=AddVertex(surf,0,  1.0,  -1.0, 0,0 )
  v2=AddVertex(surf,0,  1.0,   1.0, 1,0 )
  v3=AddVertex(surf,0, -1.0,   1.0, 1,1 )
  AddTriangle(surf,v0,v1,v2)
  AddTriangle(surf,v2,v3,v0)
  UpdateNormals mesh
  Return mesh
End Function
