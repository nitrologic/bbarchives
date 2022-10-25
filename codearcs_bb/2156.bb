; ID: 2156
; Author: Leon Drake
; Date: 2007-11-18 23:19:25
; Title: BMAX - Distance from Point to Line
; Description: get XYZ and Area distance to a line

Function Magnitude#( px#,py#,pz#,dx#,dy#,dz# )

 Local vx#,vy#,vz#
 vx# = dx# - px#
 vy# = dy# - py#
 vz# = dz# - pz#
 Return Float(Sqr( vx# * vx# + vy# * vy# + vz# * vz# ))
End Function

Function DistancePointLine( ax#,ay#,az#,bx#,by#,bz#,px#,py#,pz# )
 Local LineMag#,U#
 Local ix#,iy#,iz#
 Local Distance#
 LineMag# = Magnitude( bx#, by#, bz#, ax#, ay#, az# )
 U# = ( ( ( px# - ax# ) * ( bx# - ax# ) ) +( ( py# - ay# ) * ( by# - ay# ) ) +( ( pz# - az# ) * ( bz# - az# ) ) ) /( LineMag# * LineMag# )

 If  U# < 0.0 Or U# > 1.0 Then Return False 
 ' closest point does Not fall within the line segment
 
 ix# = ax# + U# * ( bx# - ax# )
 iy# = ay# + U# * ( by# - ay# )
 iz# = az# + U# * ( bz# - az# )
 Distance# = Magnitude( px#,py#,pz#,ix#,iy#,iz# )
 Return True
End Function
