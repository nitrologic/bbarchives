; ID: 108
; Author: Dragon57
; Date: 2001-10-28 20:25:04
; Title: CreateCylinderTaper()
; Description: Creates a tapered cylinder per user passed values

; Tapered Cylinder function
; Written by Martin Parrott
; V1.1
; Oct. 28, 2001 - 1. Added ability to map texture on object
;                 2. Changed triangle creation order, caused by 1. above
;                 3. Cleaned up variable names
;
; This code is hiware. If you use it, please send me an email and say Hi!
; You are free to use, modify, etc. No warranty is written Or implied
; Use at your own risk
; This code is free to use, but if you modify it, please send the
; changes to the above email address so I can continue to release
; updates so others can benefit.

Function CreateCylinderTaper(seg=8,parent=0,solid=True,xloc#=0,yloc#=0,zloc#=0,rad1#=1,rad2#=.75,height#=2)
  ; seg defines the number of segments/vertices in the cylinder cross-section
  ; parent is the parent enitity handle
  ; solid defines whether the cylinder ends are capped or open, true-they are closed, false-they are open
  ; xloc# is the final x axis location for the cylinder
  ; yloc# is the final y axis location for the cylinder
  ; zloc# is the final z axis location for the cylinder
  ; rad1# is the radius of the bottom of the cylinder
  ; rad2# is the radius of the top of the cylinder
  ; height# is the total height of the cylinder

  If seg<2 Then seg=8 ; make sure the number of segments is set to something sane
  If seg>32 Then seg=32

  cylmesh=CreateMesh()
  cylsurf=CreateSurface(cylmesh)
  
  ;Create center vertex of bottom disc
  AddVertex cylsurf,0,0,0,1,1

  angle#=0 ; Set our initial starting angle
  inc#=Float 360 / Float seg ; Setup increment for setting up vertices around our cylinder ends

  ; Do bottom end vertices
  While angle# < 360.01
    x#=rad1#*Cos(angle#)
    z#=rad1#*Sin(angle#)
    u#=angle#/360
    AddVertex (cylsurf,x#,0,z#,u#,1)
    angle#=angle#+inc#
  Wend

  ; If solid is set, then cap end. Do triangles
  If solid>0
    For vert=1 To seg
      AddTriangle cylsurf,0,vert,vert+1
    Next
  EndIf

  ;Create center vertex of top disc
  AddVertex cylsurf,0,height#,0,0,0

  angle#=0 ; reset angle

  ; Do top end
  While angle# < 360.01
    x#=rad2#*Cos(angle#)
    z#=rad2#*Sin(angle#)
    u#=angle#/360
    AddVertex (cylsurf,x#,height#,z#,u#,0)
    angle#=angle#+inc#
  Wend

  ; If solid is set, then cap end. Do triangles
  If solid>0
    For vert=seg+3 To (seg*2)+3
      AddTriangle cylsurf,seg+2,vert+1,vert
    Next
  EndIf

  ; Do sides of cylinder
  For vert=1 To seg
    AddTriangle cylsurf,vert,vert+seg+2,vert+seg+3
    AddTriangle cylsurf,vert,vert+seg+3,vert+1
  Next

  UpdateNormals cylmesh ; fix our normals
  MoveEntity cylmesh,xloc#,yloc#,zloc# ; Put the cylinder in place

  If parent > 0 ; Assign our cylinder to a parent if one is passed to us
    EntityParent cylmesh,parent
  EndIf

  Return cylmesh

End Function
