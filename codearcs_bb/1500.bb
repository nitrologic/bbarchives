; ID: 1500
; Author: Banshee
; Date: 2005-10-21 08:25:40
; Title: MultiTextureModel-&gt;SingleTextureFile Convertor
; Description: converts models with multiple textures into a single surface model.

Graphics3D 640,480,0,2
Global source$="source.3ds"
Global model=LoadMesh(source)
Global vQ=0

Type vert
	Field index
	Field surface
	Field x#
	Field y#
	Field z#
	Field u#
	Field v#
	Field w#
	Field nx#
	Field ny#
	Field nz#
End Type
Type tri
	Field vertA,vertB,vertC
End Type

compileMesh

Function compileMesh()
	texture=CreateImage(512,512)
	For s=1 To CountSurfaces(model)
		surface=GetSurface(model,s)
		
		brush=GetSurfaceBrush(surface)
		tex=GetBrushTexture(brush)
		FreeBrush brush
		
		uOff=0 : vOff=0
		If s=2 Then uOff=256
		If s=3 Then vOff=256
		If s=4 Then uOff=256 : vOff=256
		CopyRect 0,0,256,256,uOff,vOff,TextureBuffer(tex),ImageBuffer(texture)
		FreeTexture tex
		
		For tr=0 To CountTriangles(surface)
			t.tri=New tri
			t\vertA=TriangleVertex(surface,tr,0)
			t\vertB=TriangleVertex(surface,tr,1)
			t\vertC=TriangleVertex(surface,tr,2)
			
			If t\vertA>CountVertices(surface) Or t\vertB>CountVertices(surface) Or t\vertC>CountVertices(surface)
				Delete t
			Else
				t\vertA=checkVertice(surface,t\vertA,s)
				t\vertB=checkVertice(surface,t\vertB,s)
				t\vertC=checkVertice(surface,t\vertC,s)
			EndIf
		Next
	Next

	mesh=CreateMesh()
	surface=CreateSurface(mesh)
		
	For v.vert=Each vert
		AddVertex surface,v\x,v\y,v\z,v\u,v\v
		Delete v
	Next	
	For t.tri=Each tri
		AddTriangle surface,t\vertA,t\vertB,t\vertC
		Delete t
	Next
	
	SaveImage(texture,Left(source,Len(source)-4)+"4in1.bmp")
	saveMeshX(mesh,Left(source,Len(source)-4)+"4in1.x")
End Function
Function checkVertice(surface,vertice,s)
	offU#=0.0
	offV#=0.0
	
	If s=2 Then offU=.5
	If s=3 Then offV=.5
	If s=4 Then offU=.5 : offV=.5

	vertExist=False
	For v.vert=Each vert
		If v\surface=surface
			If v\x=VertexX(surface,vertice)
				If v\y=VertexY(surface,vertice)
					If v\z=VertexZ(surface,vertice)
						If v\u=(VertexU(surface,vertice)/2.0)+offU
							If v\v=(VertexV(surface,vertice)/2.0)+offV
								If v\w=VertexW(surface,vertice)
									If v\nx=VertexNX(surface,vertice)
										If v\ny=VertexNY(surface,vertice)
											If v\nz=VertexNZ(surface,vertice)
												vertExist=True
												foundVert=v\index
											EndIf
										EndIf
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf
		EndIf
	Next

	If Not vertExist
		v.vert=New vert
		v\index=vQ
		v\surface=surface
		v\x=VertexX(surface,vertice)							
		v\y=VertexY(surface,vertice)
		v\z=VertexZ(surface,vertice)							
		v\u=(VertexU(surface,vertice)/2.0)+offU
		v\v=(VertexV(surface,vertice)/2.0)+offV	
		v\w=VertexW(surface,vertice)				
		v\nx=VertexNX(surface,vertice)							
		v\ny=VertexNY(surface,vertice)							
		v\nz=VertexNZ(surface,vertice)							
		vQ=vQ+1
		foundVert=v\index
	EndIf

	Return foundVert
End Function


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
