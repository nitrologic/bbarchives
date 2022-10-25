; ID: 2121
; Author: Fuller
; Date: 2007-10-17 21:19:21
; Title: GetSecMesh(mesh,parent)
; Description: Retrieves the handle from a child mesh simply and easily

Function LoadSecMesh(file$,parent=0)
	
	mesh=LoadAnimMesh(file$,parent)
	If mesh=0 Then RuntimeError "Mesh "+file$+" does not exist"
	
	Return mesh
	
End Function 

Function GetEntFromName(mesh,name$)
	
	For x=1 To CountChildren(mesh)
		If EntityName$(GetChild(mesh,x))=name$
			Return GetChild(mesh,x)
		EndIf
	Next 
	
End Function
