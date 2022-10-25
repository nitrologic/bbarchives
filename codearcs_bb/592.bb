; ID: 592
; Author: jfk EO-11110
; Date: 2003-02-17 16:51:20
; Title: GetTextures()
; Description: Get Texture Paths of Surfaces

;This Functions are used to determine what Textures are used by the Surfaces of a Mesh.
;This App won an award for the most Work-Aroundish Code of the year 2003!
;Textures on the second UV Set (ie Lightmaps) are ignored. Infact it was even hard to ignore them
;and to prevent them messing up the whole thing. Like every indirect way it is a bit slow.
;I tested it with a 600kB Decorator Export with 8 Surfaces. Hope it's useful somehow.
;Have added some lines to make it work with Alpha-Textures (hi wmaass!). Tho it requires V.1.82 now.

Graphics3D 640,480,16,1
SetBuffer BackBuffer()
cam=CreateCamera()


; initialize GetTextures()
Dim tex(0),texfile$(0),surf_tex$(0)
Global bababa
; end of Initialisation

;usage:
meshfile$="PINE01.X" ; load mesh here (B3D,3DS,X)
mesh=LoadMesh(meshfile$)

GetTextures(meshfile$,mesh) ; texture paths will be stored in Array surf_tex$()


; a little test to see if it worked...
surfaces=CountSurfaces(mesh)
Dim brush(surfaces)
For i=1 To surfaces
  Print "Surface "+i+" is using "+surf_tex$(i)
  If surf_tex$(i)<>"" ; does this surface have a texture at all?
   brush(i)=LoadBrush(surf_tex$(i))
   ; remap everything manually (lightmap is lost - but it is only a check)
   PaintSurface GetSurface(mesh,i),brush(i)
  EndIf
Next

Print "Press Space to continue"
WaitKey()

MoveEntity cam,0,20,-50
PointEntity cam,mesh
While KeyDown(1)=0
  RenderWorld()
  Text 0,0,"I hope it looks ok! Press ESC to exit"
  Flip
Wend
End



;---------------------------------------------------------------------------------
Function extract_path$(i)
  oldi=i
  i=i-4
  p=100
  While p<>0 And p<>34 And i>0
    p=PeekByte(bababa,i)
    i=i-1
  Wend
  i=i+2
  path$=""
  For j=i To oldi
    path$=path$+Chr$(PeekByte(bababa,j))
  Next
  Return path$
End Function
;---------------------------------------------------------------------------------
Function GetTextures(meshfile$,model)
  mesh=CopyMesh(model) ; we work with a copy
  HideEntity model
  EntityFX mesh,17
  
  white=CreateTexture(16,16) ; just painting a potential Lightmap away
  SetBuffer TextureBuffer(white)
  Color 255,255,255
  Rect 0,0,16,16,1
  SetBuffer BackBuffer()
  EntityTexture mesh,white,0,1
  
  PositionEntity mesh,16000,1000,1000 ; moving mesh out of view
  Dim tex(1000),texfile$(1000) ; assuming there ain't more than 1000 textures used
  fs=FileSize(meshfile$)
  bababa=CreateBank(fs)
  re=OpenFile(meshfile$)
  ReadBytes(bababa,re,0,fs)
  CloseFile re
  ;Parsing Mesh File for Textures
  count=0
  For i=0 To fs-1
    t$=t$+Upper$(Chr$(PeekByte(bababa,i)))
    If Len(t$)>4 Then
      t$=Right$(t$,4)
    EndIf
    Select t$
    Case ".JPG",".BMP",".TGA",".PNG",".PCX"
    texfile$(count)=extract_path$(i)
    ;Print "Found Reference to "+ texfile$(count)
    count=count+1
    End Select
  Next
  
  If count>0
    For i=0 To count-1
      tex(i)=LoadBrush(texfile$(i))
    Next
  EndIf
  graw=GraphicsWidth() ; define Size for comparing Brushes...
  grah=GraphicsHeight()
  grawh=graw/2
  grahh=grah/2
  ckw=100:If graw<100 Then ckw=graw ; ...use 100*100 pixels for the checks
  ckh=100:If grah<100 Then ckh=grah
  ckw=ckw/2
  ckh=ckh/2
  
  ;Checking Surfaces
  s=CountSurfaces(mesh)
  Dim surf_tex$(s)
  If s>0 Then
    For i=1 To s
      surf=GetSurface(mesh,i)
      verts=CountVertices(surf) ; adding a reference quad to each surface in front of the cam...
      du1=AddVertex(surf,-3-EntityX(mesh,1),-3-EntityY(mesh,1), 5-EntityZ(mesh,1))
      du2=AddVertex(surf, 3-EntityX(mesh,1),-3-EntityY(mesh,1), 5-EntityZ(mesh,1))
      du3=AddVertex(surf,-3-EntityX(mesh,1), 3-EntityY(mesh,1), 5-EntityZ(mesh,1))
      du4=AddVertex(surf, 3-EntityX(mesh,1), 3-EntityY(mesh,1), 5-EntityZ(mesh,1))
      du=AddTriangle(surf,du1,du2,du3)
      du=AddTriangle(surf,du2,du4,du3)
      VertexTexCoords surf, du1, 0,0
      VertexTexCoords surf, du2, 1,0
      VertexTexCoords surf, du3, 0,1
      VertexTexCoords surf, du4, 1,1
      VertexColor surf, du1, 255,255,255,1.0 ; these req. V1.82
      VertexColor surf, du2, 255,255,255,1.0
      VertexColor surf, du3, 255,255,255,1.0
      VertexColor surf, du4, 255,255,255,1.0
      
      UpdateNormals mesh
      RenderWorld()
      chck=0
      LockBuffer BackBuffer() ; get checksum of original render
      For y=grahh-ckh To grahh+ckh
        For x=grawh-ckw To grawh+ckw
          chck=chck+((ReadPixelFast(x,y)And $FFFFFF) And $FFFFFFF)
        Next
      Next
      UnlockBuffer BackBuffer()
      For bru=0 To count-1
        If tex(bru)<>0 Then
          PaintSurface surf,tex(bru)
          RenderWorld()
          chck2=0
          LockBuffer BackBuffer()
          For y=grahh-ckh To grahh+ckh ; compare with the checksum of other textures rendered
            For x=grawh-ckw To grawh+ckw
              chck2=chck2+((ReadPixelFast(x,y)And $FFFFFF) And $FFFFFFF)
            Next
          Next
          UnlockBuffer BackBuffer()
          If chck=chck2 Then
            surf_tex$(i)=texfile$(bru) ; checksum fits - must be this brush then
            Exit
          EndIf
        EndIf
      Next
      ClearSurface surf,1,1 ; not used anymore
    Next
  EndIf
  ; release some resources...
  If count>0
    For i=0 To count-1
      If tex(i)<>0 Then FreeBrush tex(i)
    Next
  EndIf
  Dim texfile$(0),tex(0)
  FreeBank bababa
  FreeTexture white
  FreeEntity mesh
  ShowEntity model
End Function
