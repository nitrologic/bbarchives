; ID: 1083
; Author: AntMan - Banned in the line of duty.
; Date: 2004-06-13 04:37:46
; Title: Smart 3rd person camera
; Description: Follows a entity and avoids geo.

type csys.sys
   field cx#,cy#,cz#
   field mx#,my#,sps
end type
Function chaseCam(cam,entity,xoff#,yoff#=2,zoff#=-5,spd#=0.8)
      sys.csys=first csys
      if sys=null 
             sys=new cSys
             sys\sps=createpivot()
      endif
      sys\mx=mousex()
      sys\my=mousey()
sps=sys\sps
PositionEntity sps,EntityX(entity),EntityY(entity),EntityZ(entity)
TFormVector xOff,yOff,zOff,entity,0
ex#=EntityX(entity)
ey#=EntityY(entity)
ez#=EntityZ(entity)
nx#=ex+TFormedX()
ny#=ey+TFormedY()
nz#=ez+TFormedZ()
dx#=nx-ex
dy#=ny-ey
dz#=nz-ez
hit=LinePick(ex,ey,ez,dx,dy,dz,0.2)
If hit
nx=PickedX()
ny=PickedY()
nz=PickedZ()
EndIf
sys\cx=sys\cx+(nx-sys\cx)*spd
sys\cy=sys\cy+(ny-sys\cy)*spd
sys\cz=sys\cz+(nz-sys\cz)*spd
positionEntity cam,sys\cx,sys\cy,sys\cz
pointentity cam,entity,0
End Function
