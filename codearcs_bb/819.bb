; ID: 819
; Author: Shambler
; Date: 2003-11-04 04:30:15
; Title: DirectX X File Exporter
; Description: Saves MultiSurface meshes with normals+tex coords

;savemeshdx9(mymesh,"mymesh.x") 

Function savemeshdx9(mesh,file$)

NumberofSurfaces=CountSurfaces(mesh)
If NumberofSurfaces=0 Return

output=WriteFile(file$)
WriteLine output,"xof 0302txt 0064"
WriteLine output,""


;Header 
WriteLine output,"Header {"
WriteLine output,"1;"
WriteLine output,"0;"
WriteLine output,"1;"
WriteLine output,"}"
WriteLine output,""

;TX Matrix
WriteLine output,"Frame Root {"
WriteLine output," FrameTransformMatrix {"
WriteLine output,"  1.000000, 0.000000, 0.000000, 0.000000,"
WriteLine output,"  0.000000, 1.000000, 0.000000, 0.000000,"
WriteLine output,"  0.000000, 0.000000, 1.000000, 0.000000,"
WriteLine output,"  0.000000, 0.000000, 0.000000, 1.000000;;"
WriteLine output,"}"
WriteLine output,""



For s=1 To NumberofSurfaces
Surface=GetSurface(mesh,s)
MeshName$="Mesh Surface_"+s+" {"
WriteLine output,MeshName$
NumberofVertices=CountVertices(Surface)
WriteLine output,NumberofVertices+";"

For v=0 To NumberofVertices-2
WriteLine output,VertexX(Surface,v)+";"+VertexY(Surface,v)+";"+VertexZ(Surface,v)+";,"
Next
WriteLine output,VertexX(Surface,v)+";"+VertexY(Surface,v)+";"+VertexZ(Surface,v)+";;"
;WriteLine output,""

NumberofTriangles=CountTriangles(Surface)
WriteLine output," "+NumberofTriangles+";"

For t=0 To NumberofTriangles-2
index1=TriangleVertex(Surface,t,0)
index2=TriangleVertex(Surface,t,1) 
index3=TriangleVertex(Surface,t,2)
tl$=" 3;"+index1+","+index2+","+index3+";,"
WriteLine output,tl$
Next

index1=TriangleVertex(Surface,t,0)
index2=TriangleVertex(Surface,t,1) 
index3=TriangleVertex(Surface,t,2)
tl$=" 3;"+index1+","+index2+","+index3+";;"
WriteLine output,tl$


;WriteLine output,""


WriteLine output,"MeshMaterialList {"
WriteLine output,"1;"
WriteLine output,"1;"
WriteLine output,"0;;"
WriteLine output,""


WriteLine output,"Material {"
WriteLine output,"1.000000;1.000000;1.000000;1.000000;;"
WriteLine output,"0.000000;"
WriteLine output,"1.000000;1.000000;1.000000;;"
WriteLine output,"0.000000;0.000000;0.000000;;"

WriteLine output,"TextureFilename {"

;new code not working
b=GetSurfaceBrush(Surface)
bt=GetBrushTexture(b)
use_tex$=TextureName$(bt)
FreeTexture bt
FreeBrush b
; so strip the path
For i2= Len(use_tex$) To 1 Step -1
	If Mid$(use_tex$,i2,1)="\" Then
	use_tex$=Right$(use_tex$,Len(use_tex$)-i2)
	Exit
EndIf
Next

If use_tex$="" Then use_tex$="xxx.jpg"

WriteLine output,Chr$(34)+use_tex$+Chr$(34)+";" 

;WriteLine output,Chr$(34)+SurfaceTexture(s)+Chr$(34)+";"

WriteLine output,"}"
WriteLine output,"}"
WriteLine output,"}"



WriteLine output,"MeshNormals {"
WriteLine output,NumberofVertices+";"

For v=0 To NumberofVertices-1
vnx#=VertexNX(Surface,v)
vny#=VertexNY(Surface,v)
vnz#=VertexNZ(Surface,v)
l$=vnx#+";"+vny#+";"+vnz#+";"
If v=NumberofVertices-1 Then l$=l$+";" Else l$=l$+","
WriteLine output,l$ 
Next


WriteLine output," "+NumberofTriangles+";" 
For t=0 To NumberofTriangles-1
in1=TriangleVertex(Surface,t,0)
in2=TriangleVertex(Surface,t,1) 
in3=TriangleVertex(Surface,t,2)
l$=" 3;"+in1+","+in2+","+in3+";"
If t=NumberofTriangles-1 Then l$=l$+";" Else l$=l$+","
WriteLine output,l$ 
Next
WriteLine output,"}"

WriteLine output,"MeshTextureCoords {"
WriteLine output,NumberofVertices+";"
For v=0 To NumberofVertices-1
tc$=VertexU(Surface,v)+";"+VertexV(Surface,v)+";"
If v=NumberofVertices-1 Then tc$=tc$+";" Else tc$=tc$+","
WriteLine output,tc$
Next
;WriteLine output,"}"
WriteLine output,"}"

WriteLine output,"}"

;meshvertexcolors
WriteLine output,"MeshVertexColors {"
WriteLine output,NumberofVertices+";"
For v=0 To NumberofVertices-1
tc$=v+";"
tc$=tc$+VertexRed#(Surface,v)+";"
tc$=tc$+VertexGreen#(Surface,v)+";"
tc$=tc$+VertexBlue#(Surface,v)+";"
tc$=tc$+VertexAlpha#(Surface,v)+";"
If v=NumberofVertices-1 Then tc$=tc$+";" Else tc$=tc$+","
WriteLine output,tc$
Next
WriteLine output,"}"
;WriteLine output,"}"

Next 

WriteLine output,"}"




WriteLine output,"}"



CloseFile output

End Function
