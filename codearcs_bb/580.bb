; ID: 580
; Author: Braneloc
; Date: 2003-02-09 21:43:56
; Title: VRML Mesh Exporter
; Description: Export a mesh in VRML format, load in max or whatever.

;
;		VRML Exporter
;		(c) Braneloc, Feb 2003
;			Just to export mesh geometry into max
;
;		Usage:
;			Feel free to use it !  Please send me any changes, 
;			don't claim you wrote it (!)
;			Add as new file to project
;
;		Good bits:
;			Correctly exports single and multi-surface static meshes
;			Preserves UVs
;			Well, it works !!
;
;		Limitations:
;			No animation support
;		
;		History:
;			1.0 Feb 2003
;				First release to Blitz code archives.
;				(Protean is my code editor of choice, check it out)
;			1.0	July 2003
;				First release to Protean code archives	
;			1.1	Apr 2004
;				Removed brush & texture information limitation :)
;				Now exports correct filenames for textures.
;
;		Omissions:	(might add if there is sufficiant/any demand)
;					(it's just an exporter, not a world creator !)
;			Vertex colouring
;			normals

vrml_demo()

;;; <summary>just create/load an object, and then export it.
;;; it should load nicely into 3D Studio Max and anything that
;;; can handle VRML files.</summary>
;;; <subsystem>vrml</subsystem>
Function vrml_demo()
	Graphics3D 640,480,0,2
	
	c=CreateCube()
	NameEntity c,"Cube"
	vrml_export("cube.wrl",c)
End Function

;;; <summary>Converts a mesh into a vrml file</summary>
;;; <param name="file">The file to export, presumably ending .wrl</param>
;;; <param name="mesh">Blitz's mesh handle</param>
;;; <remarks>Preserves as much as possible</remarks>
;;; <subsystem>vrml</subsystem>
Function vrml_export(file$,mesh)
	o=WriteFile(file$)
	
	WriteLine o,"#VRML V1.0 ascii"
	WriteLine o,"# Blitz mesh to VRML converted on "+CurrentDate$()
	WriteLine o,"# Braneloc's BlitzVRML Exporter 1.1 (Apr 2004)"
	WriteLine o,"#"
	If EntityName(mesh)
		WriteLine o,"# "+EntityName(mesh)
	EndIf
	For n=1 To CountSurfaces(mesh)
		;WriteLine o,"DEF surface"+n
		WriteLine o,"Separator { # surface "+n
		
		vrml_tex(o,vrml_surfacename$(mesh,n))	; you have a better idea?
		
		WriteLine o,"	Coordinate3 {"
		WriteLine o,"		point ["
		vrml_verts(o,mesh,n)
		WriteLine o,"		]"
		WriteLine o,"	}"
		
		WriteLine o,"	TextureCoordinate2 {"
		WriteLine o,"		point ["
		vrml_UVs(o,mesh,n)
		WriteLine o,"		]"
		WriteLine o,"	}"
		
		WriteLine o,"	IndexedFaceSet {"
		WriteLine o,"		coordIndex ["
		vrml_faces(o,mesh,n)
		WriteLine o,"		]"
		WriteLine o,"	}"
		
		WriteLine o,"}"
	Next
	
	CloseFile o
End Function

;;; <summary>Get texture name of mesh surface</summary>
;;; <param name="mesh">Mesh to analyse</param>
;;; <param name="surfacenumber">Surface number in question</param>
;;; <returns>Texture name of specified surface on mesh</returns>
;;; <subsystem>vrml</subsystem>
Function vrml_surfacename$(mesh,surfacenumber)
	surf=GetSurface(mesh,surfacenumber)
	br=GetSurfaceBrush(surf)
	te=GetBrushTexture(br)
	
	tn$=vrml_StripPath(TextureName(te))
	If tn$<>""
		Return tn$
	Else
		Return surfacenumber+".bmp"
	EndIf
End Function

;#Region vrml fragments
;;; <summary>Rips the path from a full filename</summary>
;;; <param name="file"></param>
;;; <remarks></remarks>
;;; <returns>File part of filename</returns>
;;; <subsystem>vrml</subsystem>
;;; <example></example>
Function vrml_StripPath$(file$) 
	If Len(file$)>0 
		For i=Len(file$) To 1 Step -1 
			mi$=Mid$(file$,i,1) 
			If mi$="\" Or mi$="/" Then Return name$ Else name$=mi$+name$ 
		Next 
	EndIf 
	Return name$ 
End Function

;;; <summary>outputs vrml fragment for the vertices</summary>
;;; <param name="file"></param>
;;; <param name="mesh"></param>
;;; <param name="surf"></param>
;;; <remarks></remarks>
;;; <returns></returns>
;;; <subsystem>vrml</subsystem>
;;; <example></example>
Function vrml_verts(file,mesh, surf=1)
	s=GetSurface(mesh,surf)
	For n=0 To CountVertices(s)-1
		WriteLine file,"			"+VertexX(s,n)+" "+VertexY(s,n)+" "+VertexZ(s,n)+","
	Next
End Function

;;; <summary>outputs vrml fragment for the triangles in the mesh</summary>
;;; <param name="file"></param>
;;; <param name="mesh"></param>
;;; <param name="surf"></param>
;;; <remarks></remarks>
;;; <returns></returns>
;;; <subsystem>vrml</subsystem>
;;; <example></example>
Function vrml_faces(file,mesh,surf=1)
	s=GetSurface(mesh,surf)
	For n=0 To CountTriangles (s)-1
		a$="			"
		For m=0 To 2
			a$=a$+TriangleVertex(s,n,m)+","
		Next
		a$=a$+"-1,"
		WriteLine file,a$
	Next
End Function

;;; <summary>outputs vrml fragment for UV coordinates</summary>
;;; <param name="file"></param>
;;; <param name="mesh"></param>
;;; <param name="surf"></param>
;;; <remarks></remarks>
;;; <returns></returns>
;;; <subsystem>vrml</subsystem>
;;; <example></example>
Function vrml_UVs(file,mesh,surf=1)
	s=GetSurface(mesh,surf)
	; not sure how/if extra sets are handled.
	For n=0 To CountVertices(s)-1
		WriteLine file,"			"+VertexU(s,n)+" "+VertexV(s,n)+","
	Next
End Function


;;; <summary>outputs vrml fragment for textures</summary>
;;; <param name="file"></param>
;;; <param name="name"></param>
;;; <remarks></remarks>
;;; <returns></returns>
;;; <subsystem>vrml</subsystem>
;;; <example></example>
Function vrml_tex(file,name$)
	o=file
	WriteLine o,"		Texture2 {"
	WriteLine o,"			filename "+Chr$(34)+name$+Chr$(34)
	WriteLine o,"			wrapS REPEAT"
	WriteLine o,"			wrapT REPEAT"
	WriteLine o,"		}"
	WriteLine o,"		Texture2Transform {"
	WriteLine o,"			rotation     0"
	WriteLine o,"			center       0 0"
	WriteLine o,"			translation  0 0"
	WriteLine o,"			scaleFactor  1 1"
	WriteLine o,"		}"
End Function

;#End Region
