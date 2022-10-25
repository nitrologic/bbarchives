; ID: 1662
; Author: KimoTech
; Date: 2006-04-12 09:05:34
; Title: Calc terrain height - Ideal for terrain tool
; Description: Uses Cos#() to calc the vertex/terrain height for ex. a mountain

;GetHeight#( center x of mountain, center z of mountain, vertex/terrain-seg x to calc, vertex/terrain-seg z to calc, mountain radius )

Function GetHeight#(cx#,cz#,px#,pz#,r#)
If PointDistance#(cx#,0,cz#,px#,0,pz#)<r# Then Return -Cos#(( PointDistance#(cx#,0,cz#,px#,0,pz#) / r#)*180)-1
End Function

Function PointDistance#(X1#,Y1#,Z1#,X2#,Y2#,Z2#)
dx# = X1 - X2:dy# = Y1 - Y2:dz# = Z1 - Z2
Return Sqr(dx*dx + dy*dy + dz*dz)
End Function


;Example:

Graphics 800,600

Repeat
Cls
For z=0 To 600
Plot z,GetHeight(200,0,z,0,300)*MouseZ()+400
Next

Flip
Until KeyHit(1)
End
