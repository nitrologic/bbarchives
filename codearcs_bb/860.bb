; ID: 860
; Author: jfk EO-11110
; Date: 2003-12-20 19:32:56
; Title: UV Spy
; Description: Track down disobedient texture-triangles

; UV Spy 1.0 by CSP
;
; Sometimes I have problems with some Triangles after the UV Mapping Works.
; EG sometimes there are some Tris which are mapped by the wrong texture parts and I cannot
; figure out where those Triangles are located on the Texture Bitmap.
; This is where UV_Spy comes in handy: simply click the nasty Triangle and it
; will be marked on a Reference Texture which then can be used as a blended Layer
; in any Painting App to correct the wrong mapped Triangle on the Texture Bitmap.

; ATM it works only with one Surface, but it might be modified to work with multiple
; Textures.

meshname$="fighter.3ds" ; this Model will be loaded and investigated
texname$="fighter_tex.bmp" ; this is the Models Texture (must be declared anyway)

Graphics3D 640,480,16,2
SetBuffer BackBuffer()


Global camera=CreateCamera()
CameraClsColor camera,50,50,50
TranslateEntity camera,0,0,-30
light=CreateLight()

Global ttx0,tty0,ttx1,tty1,ttx2,tty2

Global mesh=LoadMesh(meshname$)
Global tex=LoadTexture(texname$)
Global texsize=TextureWidth(tex)
EntityTexture mesh,tex

EntityPickMode mesh,2

ay#=0
az#=0

; ####################################### MAIN #####################################
While Not KeyDown(1)
 If KeyDown(54)
  If KeyDown(208) Then TranslateEntity mesh,0,.1,0
  If KeyDown(200) Then TranslateEntity mesh,0,-.1,0
 Else
  If KeyDown(208) Then TranslateEntity mesh,0,0,.1
  If KeyDown(200) Then TranslateEntity mesh,0,0,-.1
 EndIf
 If KeyDown(54)
  If KeyDown(203) Then az=az+.25
  If KeyDown(205) Then az=az-.25
 Else
  If KeyDown(203) Then ay=ay+.25
  If KeyDown(205) Then ay=ay-.25
 EndIf
 RotateEntity mesh,0,ay,az
 mx=MouseX()
 my=MouseY()
 p=DrawUV(mx,my)
 RenderWorld()
 If p<>0 ; preview selected Triangle
  Color 255,255,255
  Line ttx0,tty0,ttx1,tty1
  Line ttx1,tty1,ttx2,tty2
  Line ttx2,tty2,ttx0,tty0
 EndIf
 Color 255,255,255
 Text 0,0, "up/down=zoom, (up/down)+rightShift=move-Y"
 Text 0,16,"left/right=turn-Y, (left/right)+rightShift=turn-Z"
 Text 0,32,"Esc=End, LMB=permanently draw Triangle"
 Text 0,48,p
 Flip
Wend
; ##################################################################################
Cls
yes$=Input("save UV_spy.bmp ? (Y/N)")
If Upper$(yes$)="Y"
 SaveBuffer(TextureBuffer(tex),"UV_spy.bmp")
EndIf
End


Function DrawUV(mx,my)
 p=CameraPick(camera,mx,my)
 If p<>0
  ; picked position
  px#=PickedX()
  py#=PickedY()
  pz#=PickedZ()

  s=PickedSurface()
  t=PickedTriangle()
  ; vertices of picked triangle
  x0#=VertexX(s,TriangleVertex(s,t,0))
  x1#=VertexX(s,TriangleVertex(s,t,1))
  x2#=VertexX(s,TriangleVertex(s,t,2))

  y0#=VertexY(s,TriangleVertex(s,t,0))
  y1#=VertexY(s,TriangleVertex(s,t,1))
  y2#=VertexY(s,TriangleVertex(s,t,2))

  z0#=VertexZ(s,TriangleVertex(s,t,0))
  z1#=VertexZ(s,TriangleVertex(s,t,1))
  z2#=VertexZ(s,TriangleVertex(s,t,2))

;-------
  ; UVW of picked triangle
  u0#=VertexU(s,TriangleVertex(s,t,0))
  u1#=VertexU(s,TriangleVertex(s,t,1))
  u2#=VertexU(s,TriangleVertex(s,t,2))

  v0#=VertexV(s,TriangleVertex(s,t,0))
  v1#=VertexV(s,TriangleVertex(s,t,1))
  v2#=VertexV(s,TriangleVertex(s,t,2))

  w0#=VertexW(s,TriangleVertex(s,t,0))
  w1#=VertexW(s,TriangleVertex(s,t,1))
  w2#=VertexW(s,TriangleVertex(s,t,2))

;-------

  ; world positon of tris
  TFormPoint x0,y0,z0,mesh,0
  tx0#=TFormedX()
  ty0#=TFormedY()
  tz0#=TFormedZ()

  TFormPoint x1,y1,z1,mesh,0
  tx1#=TFormedX()
  ty1#=TFormedY()
  tz1#=TFormedZ()

  TFormPoint x2,y2,z2,mesh,0
  tx2#=TFormedX()
  ty2#=TFormedY()
  tz2#=TFormedZ()

  If MouseDown(1) ; definitively mark UV Triangle on Texture
   Color 255,0,0
   SetBuffer TextureBuffer(tex)
   Line u0*texsize,v0*texsize,u1*texsize,v1*texsize
   Line u2*texsize,v2*texsize,u1*texsize,v1*texsize
   Line u0*texsize,v0*texsize,u2*texsize,v2*texsize
   SetBuffer BackBuffer()
  EndIf
  If p<>0 ; screen coords for a Triangle Preview
   CameraProject camera,tx0,ty0,tz0
   ttx0=ProjectedX()
   tty0=ProjectedY()
   CameraProject camera,tx1,ty1,tz1
   ttx1=ProjectedX()
   tty1=ProjectedY()
   CameraProject camera,tx2,ty2,tz2
   ttx2=ProjectedX()
   tty2=ProjectedY()
  EndIf
 EndIf
Return p
End Function
