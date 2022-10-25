; ID: 835
; Author: Techlord
; Date: 2003-11-23 04:51:28
; Title: B3D Model Hierarchy
; Description: B3D Model Hierarchy

model%=LoadAnimMesh("model.b3d")
b3dhierarchy(model%)

Function b3dhierarchy(parent%)
	children%=CountChildren(parent%)
	For loop = 1 To children%
		child%=GetChild (parent%,loop)
		Print EntityName$(child%);<-- do something w/ child% here.
		b3dhierarchy(child%)
	Next
End Function
