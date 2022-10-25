; ID: 2936
; Author: Captain Wicker
; Date: 2012-03-18 20:58:37
; Title: A few DB commands for B3D
; Description: A few DarkBasic functions for Blitz3D I had whipped up a while back

;DBA Commands v0.1
;By Captain Wicker
;Modify and Use as you wish

Function Sync()
UpdateWorld 
RenderWorld
Flip
End Function 

Function SetDisplayMode(width#,height#,depth#)
Graphics3D(width#,height#,depth#,3)
SetBuffer BackBuffer()
DEF_camera=CreateCamera()
DEF_light=CreateLight()
RotateEntity DEF_light,90,0,0
End Function 

Function MakeLight()
num#=CreateLight()
End Function

Function MakeSphere(Seg#)
num#=CreateSphere(Seg#)
End Function

Function MakeCamera()
num#=CreateCamera()
End Function

Function MakeCube()
num#=CreateCube()
End Function

Function MakeCone()
num#=CreateCone()
End Function

Function LoadBitmap()
num#=LoadImage(filename$)
End Function
