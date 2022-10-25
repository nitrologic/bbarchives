; ID: 132
; Author: Inner
; Date: 2001-11-08 02:39:36
; Title: OpenMeshExt()
; Description: Opens a mesh with extenend attributes.

; Function : OpenMeshExt(filename$[,xx#=0,yy#=0,zz#=0,pp#=0,ya#=0,rr#=0,parent=0,flags=0])
; 
; Example : mesh_handle=OpenMeshExt("foo.x",-9,0,15,0,270,0,0,OME_AUTOHIDE)
;
; Discription : loads a mesh and x,y,z roll,yaw & pitch positions it in one line,
; which are optional.
;
; parent (optional) - parent entity of mesh
;
; flags: (optional)
;	OME_AUTOHIDE ; Load Hiden 
;
; Author : T.J.Roughton
; Email : thelastone2k@hotmail.com

Const OME_AUTOHIDE=1

Function OpenMeshExt(filename$,xx#=0,yy#=0,zz#=0,pp#=0,ya#=0,rr#=0,parent=0,flags=0)

	hdle=LoadMesh(filename$,parent)
	PositionEntity hdle,xx#,yy#,zz#
	RotateEntity hdle,pp#,ya#,rr#

	Select flags
		Case OME_AUTOHIDE
			HideEntity hdle
	End Select 

	Return hdle
End Function 
