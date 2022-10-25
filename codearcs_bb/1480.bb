; ID: 1480
; Author: Jan_
; Date: 2005-10-07 03:20:30
; Title: Heat shader Fake
; Description: a littele Heat shader fake, with vertexcolor

Graphics3D 640,480,32,2
SetBuffer BackBuffer()

cam=CreateCamera()

MoveEntity cam,0,0,-3
Zombie=LoadMesh("zombie.b3d")
EntityFX zombie, 1+2
;EntityColor zombie,255,255,255
tex=CreateTexture(1,1)
For x#= 0 To TextureWidth(tex)-1
  For y#=0 To TextureHeight(tex)-1
    WritePixel x,y,$FFFFFFFF,TextureBuffer(tex)
  Next
Next
EntityTexture zombie,tex
FitMesh zombie,-2,-2,-2,4,4,4,1
EntityPickMode zombie,2
Repeat
  t2=t1
  t1=MilliSecs()
  TurnEntity zombie,0,5,0
  sc=CountSurfaces(zombie)
  For i = 1 To sc
    surf=GetSurface(zombie,i)
    vc=CountVertices(surf)
    For g =0 To vc-1
      x#=VertexX#(surf,g)
      y#=VertexY#(surf,g)
      z#=VertexX#(surf,g)

      LinePick x#,y#,z#,-x#,-y#,-z#
      dis#=0
      If PickedEntity()
         dis#=z#-PickedZ#()
      EndIf
      ende=0
      

      If Abs(dis) < 0.1 Vcolor = $00FF0000
      If Abs(dis) > 0.1 Vcolor = $0000FF00
      If Abs(dis) > 0.4 Vcolor = $000000FF

      VertexColor Surf, g,(VColor And $00FF0000) Shr 16 , (VColor And $0000FF00) Shr 8, VColor And $000000FF ,255

    Next
  Next
  ;UpdateWorld
  RenderWorld
  Text 0,0,(1000.0/(t1-t2))
  Flip 0
Until KeyHit(1)
End
