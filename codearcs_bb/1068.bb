; ID: 1068
; Author: AntMan - Banned in the line of duty.
; Date: 2004-06-03 10:14:52
; Title: Fast Line Part deux
; Description: For non straight lines.

function line2(x1#,y1#,x2#,y2#,red=-1,green=-1,blue=-1)
     if red>0 and green=-1
          rgb=red
     else if red=-1
         rgb = colorBlue() or ( colorGreen() shl 8) or ( colorRed() shl 16)
     else 
          rgb=blue or (green shl 8) or (red shl 16)
     endif
     xd#=x2-x1
     yd#=y2-y1
     if abs(xd)>abs(yd) steps=abs(xd) else steps=abs(yd)
     x2=xd/float(steps)
     y2=yd/float(steps)
     for steps=steps to 1 step -1
         writepixelFast x1,y1,rgb
         x1=x1+x2
         y1=y1+y2
     next
end function
