; ID: 109
; Author: Dragon57
; Date: 2001-10-28 20:24:25
; Title: CreateTorus()
; Description: Creates a torus 'primitive' according to user passed values

; Torus function
; Written by Martin Parrott
; V1.1
; Oct. 28, 2001 - 1. Added ability to map texture on object
;                 2. Cleaned up variable names
;
; This code is hiware. If you use it, please send me an email and say Hi!
; You are free to use, modify, etc. No warranty is written Or implied
; Use at your own risk
; This code is free to use, but if you modify it, please send the
; changes to the above email address so I can continue to release
; updates so others can benefit.

Type f_torus
  Field x#
  Field y#
  Field z#
  Field v#
End Type

Function CreateTorus(seg=8,outerseg=16,parent=0,xloc#=0,yloc#=0,zloc#=0,rad1#=1,rad2#=3)
  ; seg# defines the number of vertices in the torus cross-section
  ; outerseg# defines the number of 'chambers' our torus will have about its circumference
  ; parent is the parent enitity handle
  ; xloc# is the final x axis location for the torus
  ; yloc# is the final y axis location for the torus
  ; zloc# is the final z axis location for the torus
  ; rad1# is the radius of the cross-section
  ; rad2# is the radius of the circumference

  If seg<3 Then seg=8 ; make sure the number of segments is set to something sane
  If seg>64 Then seg=64 ; change this if you need more segments
  If outerseg<3 Then outerseg=8 ; make sure the number of segments is set to something sane
  If outerseg>120 Then outerseg=120 ; change this if you need more segments

  torusmesh=CreateMesh(parent)
  torussurf=CreateSurface(torusmesh)
  
  angle#=0 ; Set our initial starting angle
  inc#=Float 360 / Float seg ; Setup increment for setting up vertices around our torus cross-section

  ; Do vertices
  For doverts = 0 To seg
    angle#=inc#*doverts
    verts.f_torus = New f_torus
    verts\x#=rad1#*Cos(angle#)
    verts\y#=rad1#*Sin(angle#)
    verts\z#=0
    verts\v#=angle#/360
    AddVertex (torussurf,verts\x#+rad2#,verts\y#,verts\z#,0,verts\v#)
  Next

  outside_inc#=Float 360 / Float outerseg ; Setup increment for setting up # of seg#ments that make up our torus sweep
  rotinc#=outside_inc#

  For rotseg= 1 To outerseg ; Rotate our initial verts around the Y axis
    For verts.f_torus = Each f_torus
      rx#=Cos(rotinc#)*(verts\x#+rad2#)+Sin(rotinc#)*verts\z#
      rz#=-Sin(rotinc#)*(verts\x#+rad2#)+Cos(rotinc#)*verts\z#
      u#=rotinc#/360
      AddVertex (torussurf,rx#,verts\y#,rz#,u#,verts\v#)
    Next
    rotinc#=Float(outside_inc#*(rotseg+1))
  Next

  ; Do sides of torus
  seginc=0
  For rottri = 1 To outerseg
    For vert= 0 To seg-1
      AddTriangle torussurf,vert+seginc,vert+seg+1+seginc,vert+seg+2+seginc
      AddTriangle torussurf,vert+seginc,vert+seg+2+seginc,vert+seginc+1
    Next
    seginc=(seg+1)*rottri
  Next

;  FlipMesh torusmesh ; Uncomment this is you want to put the camera inside the torus

  UpdateNormals torusmesh ; fix our normals

  MoveEntity torusmesh,xloc#,yloc#,zloc# ; Put the torus in place per passed X,Y,Z parameters

  For verts.f_torus = Each f_torus ; Clean up our data
    Delete verts
  Next

  Return torusmesh

End Function
