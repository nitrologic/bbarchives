; ID: 1170
; Author: jfk EO-11110
; Date: 2004-10-03 07:32:16
; Title: Make EntityAlpha etc. work with animated Meshes
; Description: Animated Meshes with multiple Child-Levels require recursive Manipulation

usage:

EntityAnimColor(mesh,red,green,blue)
or
EntityAnimAlpha(mesh,alpha#)




Function EntityAnimColor(m,r,g,b)
 If EntityClass$(m)="Mesh"
  EntityColor m,r,g,b
 Endif
 For i=1 to CountChildren(m)
  ww=GetChild(m,i)
  EntityAnimColor(ww,r,g,b)
 Next
End Function

Function EntityAnimAlpha(m,a#)
 If EntityClass$(m)="Mesh"
  EntityAlpha m,a#
 Endif
 For i=1 to CountChildren(m)
  ww=GetChild(m,i)
  EntityAnimAlpha(ww,a#)
 Next
End Function
