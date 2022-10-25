; ID: 463
; Author: Ziltch
; Date: 2002-10-16 20:14:16
; Title: CreateSquare
; Description: Create polygon based 2D square (For blitz3d)

Function createsquare(segs#=2,parent=0)
; by ADAmor Ziltch. Oct 2002
; v2 Feb 2003
;v3  may 2003 fixed rounding bug

    mesh=CreateMesh( parent )
    surf=CreateSurface( mesh )

    l# =-.5
    b# = -.5
    tvc= 0

    ;create all the vertices first
    Repeat
      u# = l + .5
      v# = b + .5
      AddVertex surf,l,0,b,u,1-v
      tvc=tvc + 1
      l = l + 1/segs
      If l > .501 Then
        l = -.5
        b = b + 1/segs
      End If
    Until b > .5

    ;create polys
    vc# =0
    Repeat

      AddTriangle (surf,vc,vc+segs+1,vc+segs+2)
      AddTriangle (surf,vc,vc+segs+2,vc+1)

      vc = vc + 1
      tst# =  ((vc+1) /(segs+1)) -floor ((vc+1) /(segs+1))

      If (vc > 0) And (tst=0) Then
        vc = vc + 1
      End If

    Until vc=>tvc-segs-2
    UpdateNormals mesh
    Return mesh

End Function
