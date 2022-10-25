; ID: 797
; Author: AuzingLG
; Date: 2003-09-23 11:41:47
; Title: ResizeMesh(mesh,width#,height#,depth#)
; Description: Do you hate ScaleMesh?

Function ResizeMesh(mesh,width#,height#,depth#)
   
   ScaleMesh mesh,1/MeshWidth#(mesh)*width#,1/MeshHeight#(mesh)*height#,(1/MeshDepth#(mesh))*depth#

End Function
