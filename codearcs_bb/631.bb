; ID: 631
; Author: starfox
; Date: 2003-03-19 08:49:26
; Title: DirectX Exporter
; Description: Exports MultiSurf+TexCoords

Function savemeshdx9(mesh,file$)

NumberofSurfaces=CountSurfaces(mesh)
If NumberofSurfaces=0 Return
;Templates

output=WriteFile(file$)
WriteLine output,"xof 0302txt 0064"
WriteLine output,""

;WriteLine output,"template Header {"
;WriteLine output,"<3D82AB43-62DA-11cf-AB39-0020AF71E433>"
;WriteLine output,"WORD major;"
;WriteLine output,"WORD minor;"
;WriteLine output,"DWORD flags;"
;WriteLine output,"}"
;WriteLine output,""


;Texture Filename
;WriteLine output,"template TextureFilename {"
;WriteLine output,"<A42790E1-7810-11cf-8F52-0040333594A3>"
;WriteLine output,"String filename;"
;WriteLine output,"}"
;WriteLine output,""

;Material template
;WriteLine output,"template Material {"
;WriteLine output,"<3D82AB4D-62DA-11cf-AB39-0020AF71E433>"
;WriteLine output,"ColorRGBA faceColor;"
;WriteLine output,"Float power;"
;WriteLine output,"ColorRGB specularColor;"
;WriteLine output,"ColorRGB emissiveColor;"
;WriteLine output,"[...]"
;WriteLine output,"}"
;WriteLine output,""

;Mesh Faces Template
;WriteLine output,"template MeshFace {"
;WriteLine output,"<3D82AB5F-62DA-11cf-AB39-0020AF71E433>"
;WriteLine output,"DWORD nFaceVertexIndices;"
;WriteLine output,"array DWORD faceVertexIndices[nFaceVertexIndices];"
;WriteLine output,"}"
;WriteLine output,""


;Texture coords
;WriteLine output,"template MeshTextureCoords {"
;WriteLine output,"<F6F23F40-7686-11cf-8F52-0040333594A3>"
;WriteLine output,"DWORD nTextureCoords;"
;WriteLine output,"array Coords2d TextureCoords[nTextureCoords];"
;WriteLine output,"}"
;WriteLine output,""


;WriteLine output,"template MeshMaterialList {"
;WriteLine output,"<F6F23F42-7686-11cf-8F52-0040333594A3>"
;WriteLine output,"DWORD nMaterials;"
;WriteLine output,"DWORD nFaceIndexes;"
;WriteLine output,"array DWORD faceIndexes[nFaceIndexes];"
;WriteLine output,"[Material]"
;WriteLine output,"}"
;WriteLine output,""

;WriteLine output,"template FrameTransformMatrix {"
;WriteLine output,"<F6F23F41-7686-11cf-8F52-0040333594A3>"
;WriteLine output,"Matrix4x4 frameMatrix;"
;WriteLine output,"}"
;WriteLine output,""

;WriteLine output,"template Frame {"
;WriteLine output," <3D82AB46-62DA-11cf-AB39-0020AF71E433>"
;WriteLine output," [...]"
;WriteLine output,"}"
;WriteLine output,""



;Header 
WriteLine output,"Header {"
WriteLine output,"1;"
WriteLine output,"0;"
WriteLine output,"1;"
WriteLine output,"}"
WriteLine output,""

;TX Matrix
WriteLine output,"Frame CUBE_Root {"
WriteLine output," FrameTransformMatrix {"
WriteLine output,"  1.000000, 0.000000, 0.000000, 0.000000,"
WriteLine output,"  0.000000, 1.000000, 0.000000, 0.000000,"
WriteLine output,"  0.000000, 0.000000, 1.000000, 0.000000,"
WriteLine output,"  0.000000, 0.000000, 0.000000, 1.000000;"
WriteLine output,"}"
WriteLine output,""



For s=1 To NumberofSurfaces
Surface=GetSurface(mesh,s)
MeshName$="Mesh Surface_"+s+" {"
WriteLine output,MeshName$
NumberofVertices=CountVertices(Surface)
WriteLine output,NumberofVertices+";"




For v=0 To NumberofVertices-1
WriteLine output,VertexX(Surface,v)+";"+VertexY(Surface,v)+";"+VertexZ(Surface,v)+";,"
Next
WriteLine output,""

NumberofTriangles=CountTriangles(Surface)
WriteLine output," "+NumberofTriangles+";"

For t=0 To NumberofTriangles-1
index1=TriangleVertex(Surface,t,0)
index2=TriangleVertex(Surface,t,1) 
index3=TriangleVertex(Surface,t,2)
tl$=" 3;"+index1+","+index2+","+index3+";"
If t=CountTriangles(Surface)-1 Then tl$=tl$+";" Else tl$=tl$+","
WriteLine output,tl$
Next

WriteLine output,""


;Mesh Unnamed_0 {
; 4;
; 1.000000;1.000000;-1.000000;,    // 0
; -1.000000;1.000000;-1.000000;,   // 1
; -1.000000;1.000000;1.000000;,    // 2
; 1.000000;1.000000;1.000000;;     // 3

; 2;
; 3;0,2,1;,
; 3;0,3,2;;


WriteLine output,"MeshMaterialList {"
WriteLine output,"1;"
WriteLine output,"1;"

;WriteLine output,NumberofTriangles+";"
;For t=0 To NumberofTriangles-2
;WriteLine output,"0,"
;Next 
WriteLine output,"0;;"
WriteLine output,""


WriteLine output,"Material {"
WriteLine output,"1.000000;1.000000;1.000000;1.000000;;"
WriteLine output,"0.000000;"
WriteLine output,"1.000000;1.000000;1.000000;;"
WriteLine output,"0.000000;0.000000;0.000000;;"

WriteLine output,"TextureFilename {"
WriteLine output,SurfaceTexture(s)+";"
WriteLine output,"}"

WriteLine output,"}"
WriteLine output,"}"




;  MeshMaterialList {
;      1;
;  2;
;  0,
;  0;
;     Material {
;     1.000000;1.000000;1.000000;1.000000;;
;     0.000000;
;     1.000000;1.000000;1.000000;;
;       0.000000;0.000000;0.000000;;
;          TextureFilename {
;      "LobbyYPos.bmp";
;     }
;     }
; }



;dont need mesh normals
;  MeshNormals {
; 4;
; 0.000000;1.000000;0.000000;,
; 0.000000;1.000000;0.000000;,
; 0.000000;1.000000;0.000000;,
; 0.000000;1.000000;0.000000;;

; 2;
; 3;0,1,2;,
; 3;0,2,3;;
; }

 WriteLine output,"MeshNormals {"
 WriteLine output,NumberofVertices+";"

For v=0 To NumberofVertices-1


vnx#=VertexNX(Surface,v)
vny#=VertexNY(Surface,v)
vnz#=VertexNZ(Surface,v)

;length#=Sqr(vnx*vnx + vny*vny + vnz*vnz)	
;vnx=vnx/length
;vny=vny/length
;vnz=vnz/length


;VertexNormal(Surface,v,vnx#,vny#,vnz#)

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
If v=0 Then tc$=tc$+";" Else tc$=tc$+","
WriteLine output,tc$
Next
WriteLine output,"}"
WriteLine output,"}"


;  MeshTextureCoords {
;    4;
;    1.000000;0.000000;;
;    0.000000;0.000000;,
;    0.000000;1.000000;,
;    1.000000;1.000000;,
; }
;}

Next 

WriteLine output,"}"
WriteLine output,"}"

CloseFile output



End Function
