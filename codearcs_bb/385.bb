; ID: 385
; Author: Rob 
; Date: 2002-08-05 11:21:50
; Title: shows the hierarchy of a model.
; Description: directory view of hierarchy

;directory view of hierarchy
;check your DebugLog After calling - by Rob Cummings
Global tab ; makes debuglog output more readible (tabbing).

Graphics3D 640,480,16,2
mesh=LoadAnimMesh("mesh.3ds")

xtree(mesh)

End
Function xtree(ent)
	tab=tab+4
	For i=1 To CountChildren(ent)	
		child=GetChild(ent,i)
		name$=EntityName(child)
		DebugLog String(" ",tab)+child+" "+name
		xtree(child)
	Next
	tab=tab-4
End Function
