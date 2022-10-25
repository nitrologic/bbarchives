; ID: 2745
; Author: MusicianKool
; Date: 2010-07-26 19:49:33
; Title: Verlet intergration Lib
; Description: Simple Verlet Library with by-passable physics.

Function List.

.lib "verlet.dll"
;Globals
SetVerlet_Config%(Time#,Force#,Drag#,Length#)
SetVerlet_TimeStep%(Time#)
SetVerlet_Default_RestLength%(Length#)
SetVerlet_GravityForce%(Force#)
SetVerlet_Drag%(Drag#)
GetVerlet_TimeStep#()
GetVerlet_Default_RestLength#()
GetVerlet_GravityForce#()
GetVerlet_NumberOfVerletPoints%()

;Verlet stuff
SetVerlet_GravDirection%(Index%,X_axis#,Y_axis#,Z_axis#)

SetVerlet_AccelX%(index% , Velocity#)
SetVerlet_AccelY%(index% , Velocity#)
SetVerlet_AccelZ%(index% , Velocity#)
SetVerlet_AccelXYZ%(Index%, X_Vel#,Y_Vel#,Z_Vel#)

SetVerlet_PointX%(Index% , X_Location#)
SetVerlet_PointY%(Index% , Y_Location#)
SetVerlet_PointZ%(Index% , Z_Location#)
SetVerlet_PointXYZ%(Index%, X#,Y#,Z#)

SetVerlet_OldX%( Index% , X_Location# )
SetVerlet_OldY%( Index% , Y_Location# )
SetVerlet_OldZ%( Index% , Z_Location# )
SetVerlet_OldXYZ%( Index% , X_Location#, Y_Location#, Z_Location#)

SetVerlet_Name%( Index% , Name$ )
GetVerlet_Name$( Index% )

SetVerlet_RestLength( Index% , Length# )
GetVerlet_RestLength#( Index% )

GetVerlet_AccelX#(index%) 
GetVerlet_AccelY#(index%)
GetVerlet_AccelZ#(index%)

GetVerlet_PrevIndex%(Index%)
GetVerlet_NextIndex%(Index%)


GetVerlet_PointX#( Index% )
GetVerlet_PointY#( Index% )
GetVerlet_PointZ#( Index% )

GetVerlet_OldX#( Index% )
GetVerlet_OldY#( Index% )
GetVerlet_OldZ#( Index% )

GetVerlet_ConnectedX#( Index% )
GetVerlet_ConnectedY#( Index% )
GetVerlet_ConnectedZ#( Index% )

;Creation stuff
CreateVerletPoint%(X#,Y#,Z#,Mass#) 
CreateVerletConnection( Index1% , Index2%)
DeleteVerletPoint%( Index% )

;MainEngine stuff
Verlet%()
ConstrainVerletMass%()
ApplyVerletPhysics%()
