; ID: 615
; Author: jfk EO-11110
; Date: 2003-03-08 13:21:56
; Title: List Mesh Hierarchy
; Description: Let's you spy recursively  a Meshes complete Family/Hierarchy

; This recursive app will spy a Mesh Hierarchy's 
; Children Names up To 50 Generations.
Graphics3D 640,480,16,2

mesh=LoadAnimMesh("cass.3ds")
Global recursive_limit

hierarchy_tree(mesh)

WaitKey()
End

Function hierarchy_tree(m)
 If recursive_limit<=50
  recursive_limit=recursive_limit+1
  k=CountChildren(m)
  If k>0
   For i=1 To k
    m2=GetChild(m,i)
    Print String$("|",recursive_limit*3)+EntityName$(m2)
    hierarchy_tree(m2)
   Next
  EndIf
  recursive_limit=recursive_limit-1
 EndIf
End Function
