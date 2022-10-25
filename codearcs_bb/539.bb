; ID: 539
; Author: Markus Rauch
; Date: 2003-01-06 21:28:32
; Title: RotateMeshRecursive
; Description: Rotate a Mesh Recursive with Entitys Childs

Function RotateMeshRecursive(m,p#,y#,r#)

 ;In Blitz the Function RotateMesh rotate only the first mesh from a entity

 ;This Function rotate all Meshes from a entity with childs

 If m=0 Then Return

 ;Rotate Mesh entity/childs/childs/childs/... 

 RotateMesh m,p#,y#,r#

 Local co=CountChildren(m)

 Local i

 Local x=0

 If co > 0 Then
  For i=1 To co
 
   RotateMeshRecursive GetChild(m,i),p#,y#,r#
  
  Next
 EndIf

End Function
