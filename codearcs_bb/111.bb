; ID: 111
; Author: David Bird(Birdie)
; Date: 2001-10-25 14:32:59
; Title: Saving meshes x file format
; Description: Save single surface meshes to a x file format

;    X File Saving Function (c)2001 David Bird
;    dave@birdie72.freeserve.co.uk
;    www.birdie72.freeserve.co.uk

Function SaveMeshX(mesh,file$)
;single surface at the moment
    cnt=CountSurfaces(mesh)
    If cnt=0 Then Return
    
    ;TODO Multi surface support
    surf=GetSurface(mesh,1)
    
    out=WriteFile(file$)
    WriteLine out,"xof 0302txt 0064"
    WriteLine out,""
    WriteLine out,"Header {"
    WriteLine out," 1;"
    WriteLine out," 0;"
    WriteLine out," 1;"
    WriteLine out,"}"
    WriteLine out,""
    WriteLine out,"Frame frm_Scene_Root {"
    WriteLine out," FrameTransformMatrix {"
    WriteLine out," 1.000000,0.000000,0.000000,0.000000,"
    WriteLine out," 0.000000,1.000000,0.000000,0.000000,"
    WriteLine out," 0.000000,0.000000,1.000000,0.000000,"
    WriteLine out," 0.000000,0.000000,0.000000,1.000000;"
    WriteLine out,"}"
    WriteLine out,""    
    name$=EntityName$(mesh)
    If name$="" Then name$="Unknown"
    WriteLine out,"Frame frm_"+name$+" {"
    WriteLine out," FrameTransformMatrix {"
    WriteLine out," 1.000000,0.000000,0.000000,0.000000,"
    WriteLine out," 0.000000,1.000000,0.000000,0.000000,"
    WriteLine out," 0.000000,0.000000,1.000000,0.000000,"
    WriteLine out," 0.000000,0.000000,0.000000,1.000000;"
    WriteLine out,"}"
    WriteLine out,""
    WriteLine out," Mesh frm_"+name$+" {"
    WriteLine out,CountVertices(surf)+";"
    For a=0 To CountVertices(surf)-1
        WriteLine out,VertexX(surf,a)+";"+VertexY(surf,a)+";"+VertexZ(surf,a)+";,"
    Next
    WriteLine out,""
    WriteLine out," "+CountTriangles(surf)+";"
    For a=0 To CountTriangles(surf)-1
        in1=TriangleVertex(surf,a,0)
        in2=TriangleVertex(surf,a,1)
        in3=TriangleVertex(surf,a,2)
        ln$=" 3;"+in1+","+in2+","+in3+";"
        If a=CountTriangles(surf)-1 Then ln$=ln$+";" Else ln$=ln$+","
        WriteLine out,ln$
    Next
    WriteLine out,""

    WriteLine out,"MeshMaterialList {"
    WriteLine out,"1;"
    WriteLine out,"1;"
    WriteLine out,"0;;"
    WriteLine out,""
    WriteLine out,"Material {"

    WriteLine out," 1.000000,1.000000,1.000000,1.000000;;";rgba
    WriteLine out," 1.000000;"
    WriteLine out," 0.500000,0.500000,0.500000;;"
    WriteLine out," 0.000000,0.000000,0.000000;;"
    WriteLine out,"}"
    WriteLine out,"}"
    WriteLine out,""
    WriteLine out,"MeshNormals {"
    WriteLine out,CountVertices(surf)+";"
    For a=0 To CountVertices(surf)-1
        WriteLine out,VertexNX(surf,a)+";"+VertexNY(surf,a)+";"+VertexNZ(surf,a)+";,"
    Next
    WriteLine out," "+CountTriangles(surf)+";"
    For a=0 To CountTriangles(surf)-1
        in1=TriangleVertex(surf,a,0)
        in2=TriangleVertex(surf,a,1)
        in3=TriangleVertex(surf,a,2)
        ln$=" 3;"+in1+","+in2+","+in3+";"
        If a=CountTriangles(surf)-1 Then ln$=ln$+";" Else ln$=ln$+","
        WriteLine out,ln$
    Next
    WriteLine out,"}"
    WriteLine out,""

    WriteLine out,"MeshTextureCoords {"
    WriteLine out,CountVertices(surf)+";"
    For a=0 To CountVertices(surf)-1
        ln$=VertexU(surf,a)+";"+VertexV(surf,a)+";"
        If a=CountVertices(surf)-1 Then ln$=ln$+";" Else ln$=ln$+","
        WriteLine out,ln$
    Next
    WriteLine out," }"
    WriteLine out," }"
    WriteLine out," }"
    WriteLine out,"}"
    CloseFile out
End Function
